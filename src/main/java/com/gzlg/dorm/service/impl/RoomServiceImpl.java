package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.RoomReq;
import com.gzlg.dorm.entity.CheckIn;
import com.gzlg.dorm.entity.DormBed;
import com.gzlg.dorm.entity.DormBuilding;
import com.gzlg.dorm.entity.DormRoom;
import com.gzlg.dorm.mapper.CheckInMapper;
import com.gzlg.dorm.mapper.DormBedMapper;
import com.gzlg.dorm.mapper.DormBuildingMapper;
import com.gzlg.dorm.mapper.DormRoomMapper;
import com.gzlg.dorm.service.RoomService;
import com.gzlg.dorm.vo.BedVO;
import com.gzlg.dorm.vo.RoomVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 房间服务实现：楼栋校验、(buildingId,roomNo) 唯一、capacity 与 dorm_bed 行数一致（新建自动建床）、
 * 已住房间禁止删除、更新容量时床位增减。
 */
@Service
public class RoomServiceImpl implements RoomService {

    private static final List<String> TYPES = List.of("四人间", "六人间");
    private static final String OCCUPIED = "占用";

    private final DormRoomMapper roomMapper;
    private final DormBuildingMapper buildingMapper;
    private final DormBedMapper bedMapper;
    private final CheckInMapper checkInMapper;

    public RoomServiceImpl(DormRoomMapper roomMapper, DormBuildingMapper buildingMapper,
                           DormBedMapper bedMapper, CheckInMapper checkInMapper) {
        this.roomMapper = roomMapper;
        this.buildingMapper = buildingMapper;
        this.bedMapper = bedMapper;
        this.checkInMapper = checkInMapper;
    }

    @Override
    public PageResult<RoomVO> page(Long buildingId, String roomNo, String status, String roomType,
                                   int page, int pageSize) {
        Page<DormRoom> p = new Page<>(page, pageSize);
        roomMapper.selectPage(p, Wrappers.<DormRoom>lambdaQuery()
                .eq(buildingId != null, DormRoom::getBuildingId, buildingId)
                .like(roomNo != null && !roomNo.isBlank(), DormRoom::getRoomNo, roomNo)
                .eq(status != null && !status.isBlank(), DormRoom::getStatus, status)
                .eq(roomType != null && !roomType.isBlank(), DormRoom::getRoomType, roomType)
                .orderByAsc(DormRoom::getBuildingId, DormRoom::getRoomNo));
        List<RoomVO> list = p.getRecords().stream().map(r -> toVO(r)).toList();
        return PageResult.of(list, p.getTotal());
    }

    @Override
    public Map<String, Object> options() {
        List<Map<String, Object>> buildings = buildingMapper.selectList(null).stream()
                .map(b -> Map.<String, Object>of("id", b.getId(), "buildingName", b.getBuildingName()))
                .toList();
        return Map.of("buildings", buildings, "types", TYPES);
    }

    @Override
    @Transactional
    public void create(RoomReq req) {
        ensureBuilding(req.getBuildingId());
        if (roomMapper.selectCount(Wrappers.<DormRoom>lambdaQuery()
                .eq(DormRoom::getBuildingId, req.getBuildingId())
                .eq(DormRoom::getRoomNo, req.getRoomNo())) > 0) {
            throw new BizException("该楼栋房间号已存在");
        }
        DormRoom room = new DormRoom();
        room.setBuildingId(req.getBuildingId());
        room.setFloor(req.getFloor());
        room.setRoomNo(req.getRoomNo());
        room.setCapacity(req.getCapacity());
        room.setRoomType(roomTypeOf(req.getCapacity()));
        room.setStatus("空闲");
        roomMapper.insert(room);
        createBeds(room.getId(), req.getCapacity());
    }

    @Override
    @Transactional
    public void update(Long id, RoomReq req) {
        DormRoom room = roomMapper.selectById(id);
        if (room == null) {
            throw new BizException("房间不存在");
        }
        ensureBuilding(req.getBuildingId());
        if (!req.getBuildingId().equals(room.getBuildingId()) || !req.getRoomNo().equals(room.getRoomNo())) {
            if (roomMapper.selectCount(Wrappers.<DormRoom>lambdaQuery()
                    .eq(DormRoom::getBuildingId, req.getBuildingId())
                    .eq(DormRoom::getRoomNo, req.getRoomNo())
                    .ne(DormRoom::getId, id)) > 0) {
                throw new BizException("该楼栋房间号已存在");
            }
        }
        if (req.getCapacity() < occupiedCount(id)) {
            throw new BizException("容纳人数不能小于已住人数");
        }
        room.setBuildingId(req.getBuildingId());
        room.setFloor(req.getFloor());
        room.setRoomNo(req.getRoomNo());
        room.setCapacity(req.getCapacity());
        room.setRoomType(roomTypeOf(req.getCapacity()));
        roomMapper.updateById(room);
        reconcileBeds(id, req.getCapacity());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (roomMapper.selectById(id) == null) {
            throw new BizException("房间不存在");
        }
        if (bedMapper.selectCount(Wrappers.<DormBed>lambdaQuery()
                .eq(DormBed::getRoomId, id).eq(DormBed::getStatus, OCCUPIED)) > 0) {
            throw new BizException("该房间仍有学生入住，无法删除");
        }
        bedMapper.delete(Wrappers.<DormBed>lambdaQuery().eq(DormBed::getRoomId, id));
        roomMapper.deleteById(id);
    }

    @Override
    public List<BedVO> beds(Long roomId) {
        List<DormBed> beds = bedMapper.selectList(Wrappers.<DormBed>lambdaQuery()
                .eq(DormBed::getRoomId, roomId).orderByAsc(DormBed::getBedNo));
        return beds.stream().map(b -> {
            BedVO vo = new BedVO();
            vo.setBedId(roomId + "-" + b.getBedNo());
            vo.setBedNo(b.getBedNo());
            vo.setStatus(b.getStatus());
            if (OCCUPIED.equals(b.getStatus())) {
                CheckIn inHouse = checkInMapper.selectOne(Wrappers.<CheckIn>lambdaQuery()
                        .eq(CheckIn::getBedId, b.getId()).eq(CheckIn::getStatus, "在住").last("limit 1"));
                if (inHouse != null) {
                    vo.setStudentId(inHouse.getStudentId());
                    vo.setStudentName(inHouse.getStudentName());
                }
            }
            return vo;
        }).toList();
    }

    private RoomVO toVO(DormRoom r) {
        RoomVO vo = new RoomVO();
        vo.setId(r.getId());
        vo.setBuildingId(r.getBuildingId());
        DormBuilding b = buildingMapper.selectById(r.getBuildingId());
        vo.setBuildingName(b == null ? null : b.getBuildingName());
        vo.setFloor(r.getFloor());
        vo.setRoomNo(r.getRoomNo());
        vo.setCapacity(r.getCapacity());
        vo.setRoomType(r.getRoomType());
        vo.setStatus(r.getStatus());
        vo.setOccupiedCount(occupiedCount(r.getId()));
        return vo;
    }

    private int occupiedCount(Long roomId) {
        return bedMapper.selectCount(Wrappers.<DormBed>lambdaQuery()
                .eq(DormBed::getRoomId, roomId).eq(DormBed::getStatus, OCCUPIED)).intValue();
    }

    private void ensureBuilding(Long buildingId) {
        if (buildingMapper.selectById(buildingId) == null) {
            throw new BizException("所属楼栋不存在");
        }
    }

    private String roomTypeOf(Integer capacity) {
        return capacity >= 6 ? "六人间" : "四人间";
    }

    private void createBeds(Long roomId, int capacity) {
        for (int i = 1; i <= capacity; i++) {
            DormBed bed = new DormBed();
            bed.setRoomId(roomId);
            bed.setBedNo(i + "号床");
            bed.setStatus("空闲");
            bedMapper.insert(bed);
        }
    }

    private void reconcileBeds(Long roomId, int capacity) {
        long current = bedMapper.selectCount(Wrappers.<DormBed>lambdaQuery().eq(DormBed::getRoomId, roomId));
        if (current == capacity) {
            return;
        }
        if (current < capacity) {
            for (int i = (int) current + 1; i <= capacity; i++) {
                DormBed bed = new DormBed();
                bed.setRoomId(roomId);
                bed.setBedNo(i + "号床");
                bed.setStatus("空闲");
                bedMapper.insert(bed);
            }
            return;
        }
        // 当前床位多于新容量：删除多出的空闲床位（占用位不足 capacity 已被 above 校验保证，空闲床位足够）
        while (bedMapper.selectCount(Wrappers.<DormBed>lambdaQuery().eq(DormBed::getRoomId, roomId)) > capacity) {
            DormBed free = bedMapper.selectOne(Wrappers.<DormBed>lambdaQuery()
                    .eq(DormBed::getRoomId, roomId).eq(DormBed::getStatus, "空闲")
                    .orderByDesc(DormBed::getBedNo).last("limit 1"));
            if (free == null) {
                break;
            }
            bedMapper.deleteById(free.getId());
        }
    }
}