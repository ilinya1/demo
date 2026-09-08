package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.result.PageResult;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return PageResult.of(p.getRecords().stream().map(this::toVO).toList(), p.getTotal());
    }

    @Override
    public RepairVO detail(Long id) {
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("报修单不存在");
        }
        return toVO(order);
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

        long seq = repairOrderMapper.selectCount(null) + 1;
        String orderNo = "BX" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + String.format("%03d", seq);

        RepairOrder order = new RepairOrder();
        order.setOrderNo(orderNo);
        order.setStudentId(req.getStudentId());
        order.setBuildingId(room.getBuildingId());
        order.setRoomId(req.getRoomId());
        order.setTypeId(req.getTypeId());
        order.setDescription(req.getDescription());
        order.setContactPhone(req.getContactPhone());
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
        order.setHandlerPhone(req.getHandlerPhone());
        order.setHandleDesc(req.getHandleDesc());
        if (order.getHandleTime() == null) {
            order.setHandleTime(LocalDateTime.now());
        }
        repairOrderMapper.updateById(order);
    }

    private RepairVO toVO(RepairOrder o) {
        RepairVO vo = new RepairVO();
        vo.setId(o.getId());
        vo.setOrderNo(o.getOrderNo());
        vo.setStudentId(o.getStudentId());
        Student student = studentMapper.selectById(o.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getName());
        }
        vo.setBuildingId(o.getBuildingId());
        vo.setRoomId(o.getRoomId());
        DormBuilding building = buildingMapper.selectById(o.getBuildingId());
        if (building != null) {
            vo.setBuildingName(building.getBuildingName());
        }
        DormRoom room = roomMapper.selectById(o.getRoomId());
        if (room != null) {
            vo.setRoomNo(room.getRoomNo());
        }
        vo.setTypeId(o.getTypeId());
        RepairType type = repairTypeMapper.selectById(o.getTypeId());
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