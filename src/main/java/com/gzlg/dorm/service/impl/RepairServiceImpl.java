package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.util.PhoneUtils;
import com.gzlg.dorm.dto.RepairCreateRequest;
import com.gzlg.dorm.dto.RepairHandleRequest;
import com.gzlg.dorm.entity.DormBuilding;
import com.gzlg.dorm.entity.DormRoom;
import com.gzlg.dorm.entity.RepairOrder;
import com.gzlg.dorm.entity.RepairType;
import com.gzlg.dorm.entity.Student;
import com.gzlg.dorm.mapper.DormBuildingMapper;
import com.gzlg.dorm.mapper.DormRoomMapper;
import com.gzlg.dorm.mapper.RepairOrderMapper;
import com.gzlg.dorm.mapper.RepairTypeMapper;
import com.gzlg.dorm.mapper.StudentMapper;
import com.gzlg.dorm.service.RepairService;
import com.gzlg.dorm.vo.RepairVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报修实现。
 */
@Service
public class RepairServiceImpl implements RepairService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RepairOrderMapper repairOrderMapper;
    private final StudentMapper studentMapper;
    private final DormBuildingMapper buildingMapper;
    private final DormRoomMapper roomMapper;
    private final RepairTypeMapper repairTypeMapper;

    public RepairServiceImpl(RepairOrderMapper repairOrderMapper, StudentMapper studentMapper,
                             DormBuildingMapper buildingMapper, DormRoomMapper roomMapper,
                             RepairTypeMapper repairTypeMapper) {
        this.repairOrderMapper = repairOrderMapper;
        this.studentMapper = studentMapper;
        this.buildingMapper = buildingMapper;
        this.roomMapper = roomMapper;
        this.repairTypeMapper = repairTypeMapper;
    }

    @Override
    public PageResult<RepairVO> page(String orderNo, Long buildingId, String studentId, String status,
                                     int page, int pageSize) {
        Page<RepairOrder> p = new Page<>(page, pageSize);
        repairOrderMapper.selectPage(p, Wrappers.<RepairOrder>lambdaQuery()
                .like(orderNo != null && !orderNo.isBlank(), RepairOrder::getOrderNo, orderNo)
                .eq(buildingId != null, RepairOrder::getBuildingId, buildingId)
                .eq(studentId != null && !studentId.isBlank(), RepairOrder::getStudentId, studentId)
                .eq(status != null && !status.isBlank(), RepairOrder::getStatus, status)
                .orderByDesc(RepairOrder::getCreateTime));
        if (p.getRecords().isEmpty()) {
            return PageResult.of(List.of(), p.getTotal());
        }
        return PageResult.of(toVOList(p.getRecords()), p.getTotal());
    }

    @Override
    public RepairVO detail(Long id) {
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("报修单不存在");
        }
        return toVOList(List.of(order)).get(0);
    }

    @Override
    @Transactional
    public void create(RepairCreateRequest req) {
        Student student = studentMapper.selectById(req.getStudentId());
        if (student == null) {
            throw new BizException("学生不存在");
        }
        DormRoom room = roomMapper.selectById(req.getRoomId());
        if (room == null) {
            throw new BizException("宿舍信息不存在");
        }

        // 单号：前缀+毫秒时间戳+4位随机，避免并发撞号（唯一键兜底）
        String orderNo = "BX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));

        RepairOrder order = new RepairOrder();
        order.setOrderNo(orderNo);
        order.setStudentId(req.getStudentId());
        order.setBuildingId(room.getBuildingId());
        order.setRoomId(req.getRoomId());
        order.setTypeId(req.getTypeId());
        order.setDescription(req.getDescription());
        order.setContactPhone(PhoneUtils.requireMobile(req.getContactPhone(), "联系电话"));
        order.setImages(toJson(req.getImages()));
        order.setStatus("待处理");
        order.setCreateTime(LocalDateTime.now());
        repairOrderMapper.insert(order);
    }

    @Override
    @Transactional
    public void handle(Long id, RepairHandleRequest req) {
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("报修单不存在");
        }
        String status = (req.getStatus() != null && !req.getStatus().isBlank())
                ? req.getStatus() : order.getStatus();
        if ("已完成".equals(status) && (req.getHandleDesc() == null || req.getHandleDesc().isBlank())) {
            throw new BizException("完成处理请填写处理说明");
        }
        order.setStatus(status);
        order.setHandlerName(req.getHandlerName());
        order.setHandlerPhone(PhoneUtils.requireMobile(req.getHandlerPhone(), "处理人电话"));
        order.setHandleDesc(req.getHandleDesc());
        if (order.getHandleTime() == null) {
            order.setHandleTime(LocalDateTime.now());
        }
        repairOrderMapper.updateById(order);
    }

    /** 批量转 VO：一次性加载学生/楼栋/房间/报修类型字典，避免逐条 N+1 查询 */
    private List<RepairVO> toVOList(List<RepairOrder> orders) {
        List<String> studentIds = orders.stream().map(RepairOrder::getStudentId).distinct().toList();
        List<Long> buildingIds = orders.stream().map(RepairOrder::getBuildingId).distinct().toList();
        List<Long> roomIds = orders.stream().map(RepairOrder::getRoomId).distinct().toList();
        List<Long> typeIds = orders.stream().map(RepairOrder::getTypeId).distinct().toList();

        Map<String, Student> studentMap = studentIds.isEmpty() ? Map.of()
                : studentMapper.selectBatchIds(studentIds).stream()
                        .collect(Collectors.toMap(Student::getStudentId, s -> s, (a, b) -> a));
        Map<Long, DormBuilding> buildingMap = buildingIds.isEmpty() ? Map.of()
                : buildingMapper.selectBatchIds(buildingIds).stream()
                        .collect(Collectors.toMap(DormBuilding::getId, b -> b, (a, b) -> a));
        Map<Long, DormRoom> roomMap = roomIds.isEmpty() ? Map.of()
                : roomMapper.selectBatchIds(roomIds).stream()
                        .collect(Collectors.toMap(DormRoom::getId, r -> r, (a, b) -> a));
        Map<Long, RepairType> typeMap = typeIds.isEmpty() ? Map.of()
                : repairTypeMapper.selectBatchIds(typeIds).stream()
                        .collect(Collectors.toMap(RepairType::getId, t -> t, (a, b) -> a));

        return orders.stream().map(o -> toVO(o, studentMap, buildingMap, roomMap, typeMap)).toList();
    }

    private RepairVO toVO(RepairOrder o, Map<String, Student> studentMap,
                          Map<Long, DormBuilding> buildingMap, Map<Long, DormRoom> roomMap,
                          Map<Long, RepairType> typeMap) {
        RepairVO vo = new RepairVO();
        vo.setId(o.getId());
        vo.setOrderNo(o.getOrderNo());
        vo.setStudentId(o.getStudentId());
        Student student = studentMap.get(o.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getName());
        }
        vo.setBuildingId(o.getBuildingId());
        vo.setRoomId(o.getRoomId());
        DormBuilding building = buildingMap.get(o.getBuildingId());
        if (building != null) {
            vo.setBuildingName(building.getBuildingName());
        }
        DormRoom room = roomMap.get(o.getRoomId());
        if (room != null) {
            vo.setRoomNo(room.getRoomNo());
        }
        vo.setTypeId(o.getTypeId());
        RepairType type = typeMap.get(o.getTypeId());
        if (type != null) {
            vo.setTypeName(type.getName());
        }
        vo.setDescription(o.getDescription());
        vo.setContactPhone(o.getContactPhone());
        vo.setImages(parseList(o.getImages()));
        vo.setStatus(o.getStatus());
        vo.setHandlerName(o.getHandlerName());
        vo.setHandlerPhone(o.getHandlerPhone());
        vo.setHandleDesc(o.getHandleDesc());
        vo.setCreateTime(o.getCreateTime());
        vo.setHandleTime(o.getHandleTime());
        return vo;
    }

    private static String toJson(List<String> list) {
        try {
            return new ObjectMapper().writeValueAsString(list == null ? List.of() : list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private static List<String> parseList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return new ObjectMapper().readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }
}