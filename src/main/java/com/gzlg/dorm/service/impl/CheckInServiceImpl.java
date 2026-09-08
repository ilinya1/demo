package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.util.BedNoUtil;
import com.gzlg.dorm.dto.CheckinRequest;
import com.gzlg.dorm.entity.CheckIn;
import com.gzlg.dorm.entity.Clazz;
import com.gzlg.dorm.entity.DormBed;
import com.gzlg.dorm.entity.DormBuilding;
import com.gzlg.dorm.entity.DormRoom;
import com.gzlg.dorm.entity.Student;
import com.gzlg.dorm.mapper.CheckInMapper;
import com.gzlg.dorm.mapper.ClazzMapper;
import com.gzlg.dorm.mapper.DormBedMapper;
import com.gzlg.dorm.mapper.DormBuildingMapper;
import com.gzlg.dorm.mapper.DormRoomMapper;
import com.gzlg.dorm.mapper.StudentMapper;
import com.gzlg.dorm.service.CheckInService;
import com.gzlg.dorm.vo.CheckInVO;
import com.gzlg.dorm.vo.CheckinRoomVO;
import com.gzlg.dorm.vo.CurrentRoomVO;
import com.gzlg.dorm.vo.CurrentRoomVO.DormInfo;
import com.gzlg.dorm.vo.CurrentRoomVO.Roommate;
import com.gzlg.dorm.vo.CurrentRoomVO.StudentMini;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 入住业务实现：入住事务写 check_in 快照并联动床位/房间/学生住宿状态。
 */
@Service
public class CheckInServiceImpl implements CheckInService {

    private static final String OCCUPIED = "占用";
    private static final String IN_HOUSE = "在住";

    private final StudentMapper studentMapper;
    private final ClazzMapper clazzMapper;
    private final DormBuildingMapper buildingMapper;
    private final DormRoomMapper roomMapper;
    private final DormBedMapper bedMapper;
    private final CheckInMapper checkInMapper;

    public CheckInServiceImpl(StudentMapper studentMapper, ClazzMapper clazzMapper,
                              DormBuildingMapper buildingMapper, DormRoomMapper roomMapper,
                              DormBedMapper bedMapper, CheckInMapper checkInMapper) {
        this.studentMapper = studentMapper;
        this.clazzMapper = clazzMapper;
        this.buildingMapper = buildingMapper;
        this.roomMapper = roomMapper;
        this.bedMapper = bedMapper;
        this.checkInMapper = checkInMapper;
    }

    @Override
    public List<CheckinRoomVO> checkinRooms(Long buildingId) {
        return roomMapper.selectList(Wrappers.<DormRoom>lambdaQuery()
                        .eq(buildingId != null, DormRoom::getBuildingId, buildingId)
                        .orderByAsc(DormRoom::getRoomNo))
                .stream()
                .filter(r -> occupiedCount(r.getId()) < r.getCapacity())
                .map(r -> {
                    CheckinRoomVO vo = new CheckinRoomVO();
                    vo.setId(r.getId());
                    vo.setRoomNo(r.getRoomNo());
                    vo.setFreeBeds(r.getCapacity() - occupiedCount(r.getId()));
                    return vo;
                })
                .toList();
    }

    @Override
    public List<Integer> checkinFreeBeds(Long roomId) {
        return bedMapper.selectList(Wrappers.<DormBed>lambdaQuery()
                        .eq(DormBed::getRoomId, roomId).eq(DormBed::getStatus, "空闲")
                        .orderByAsc(DormBed::getBedNo))
                .stream()
                .map(b -> parseBedNo(b.getBedNo()))
                .toList();
    }

    @Override
    @Transactional
    public void checkIn(CheckinRequest req) {
        Student student = studentMapper.selectById(req.getStudentId());
        if (student == null) {
            throw new BizException("学生不存在");
        }
        if (IN_HOUSE.equals(student.getHousingStatus())) {
            throw new BizException("该学生当前已在住，不能重复入住");
        }
        DormRoom room = roomMapper.selectById(req.getRoomId());
        if (room == null) {
            throw new BizException("房间不存在");
        }
        DormBed bed = bedMapper.selectOne(Wrappers.<DormBed>lambdaQuery()
                .eq(DormBed::getRoomId, room.getId()).eq(DormBed::getBedNo, req.getBedNo() + "号床"));
        if (bed == null || !"空闲".equals(bed.getStatus())) {
            throw new BizException("该床位已被占用，请重新选择");
        }
        DormBuilding building = buildingMapper.selectById(room.getBuildingId());
        Clazz clazz = clazzMapper.selectById(student.getClassId());

        CheckIn record = new CheckIn();
        record.setStudentId(student.getStudentId());
        record.setBuildingId(room.getBuildingId());
        record.setRoomId(room.getId());
        record.setBedId(bed.getId());
        record.setStudentName(student.getName());
        record.setClassName(clazz == null ? null : clazz.getClassName());
        record.setBuildingName(building == null ? null : building.getBuildingName());
        record.setRoomNo(room.getRoomNo());
        record.setBedNo(bed.getBedNo());
        record.setCheckInTime(parseDateTime(req.getCheckInDate(), LocalDateTime.now()));
        record.setStatus(IN_HOUSE);
        record.setSource("");
        record.setRemark(req.getRemark());
        checkInMapper.insert(record);

        bed.setStatus(OCCUPIED);
        bedMapper.updateById(bed);
        student.setHousingStatus(IN_HOUSE);
        studentMapper.updateById(student);
        refreshRoomStatus(room.getId());
    }

    @Override
    @Transactional
    public void checkout(String studentId, LocalDateTime checkoutTime, String source, String remark) {
        CheckIn record = checkInMapper.selectOne(Wrappers.<CheckIn>lambdaQuery()
                .eq(CheckIn::getStudentId, studentId).eq(CheckIn::getStatus, IN_HOUSE).last("limit 1"));
        if (record == null) {
            throw new BizException("该学生当前不在住，无需办理退宿");
        }
        record.setCheckOutTime(checkoutTime);
        record.setStatus("已退宿");
        record.setSource(source);
        if (remark != null && !remark.isBlank()) {
            record.setRemark(remark);
        }
        checkInMapper.updateById(record);

        DormBed bed = bedMapper.selectById(record.getBedId());
        if (bed != null) {
            bed.setStatus("空闲");
            bedMapper.updateById(bed);
        }
        Student student = studentMapper.selectById(studentId);
        if (student != null) {
            student.setHousingStatus("已退宿");
            studentMapper.updateById(student);
        }
        refreshRoomStatus(record.getRoomId());
    }

    @Override
    public PageResult<CheckInVO> listRecords(String studentId, String studentName,
                                             String buildingName, String status, int page, int pageSize) {
        Page<CheckIn> p = new Page<>(page, pageSize);
        checkInMapper.selectPage(p, Wrappers.<CheckIn>lambdaQuery()
                .like(studentId != null && !studentId.isBlank(), CheckIn::getStudentId, studentId)
                .like(studentName != null && !studentName.isBlank(), CheckIn::getStudentName, studentName)
                .eq(buildingName != null && !buildingName.isBlank(), CheckIn::getBuildingName, buildingName)
                .eq(status != null && !status.isBlank(), CheckIn::getStatus, status)
                .orderByDesc(CheckIn::getId));
        List<CheckInVO> list = p.getRecords().stream().map(CheckInServiceImpl::toVO).toList();
        return PageResult.of(list, p.getTotal());
    }

    @Override
    public CurrentRoomVO currentRoom(String studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BizException("学生不存在");
        }
        CurrentRoomVO vo = new CurrentRoomVO();
        StudentMini mini = new StudentMini();
        mini.setStudentId(student.getStudentId());
        mini.setName(student.getName());
        mini.setGender(student.getGender());
        Clazz clazz = clazzMapper.selectById(student.getClassId());
        mini.setClassName(clazz == null ? null : clazz.getClassName());
        vo.setStudent(mini);

        CheckIn record = checkInMapper.selectOne(Wrappers.<CheckIn>lambdaQuery()
                .eq(CheckIn::getStudentId, studentId).eq(CheckIn::getStatus, IN_HOUSE).last("limit 1"));
        if (record == null) {
            vo.setDorm(null);
            vo.setRoommates(List.of());
            return vo;
        }
        DormInfo dorm = new DormInfo();
        dorm.setBuildingId(record.getBuildingId());
        dorm.setBuildingName(record.getBuildingName());
        dorm.setRoomNo(record.getRoomNo());
        dorm.setBedNo(BedNoUtil.strip(record.getBedNo()));
        dorm.setRoomId(record.getRoomId());
        dorm.setCheckInTime(record.getCheckInTime());
        vo.setDorm(dorm);

        List<Roommate> roommates = checkInMapper.selectList(Wrappers.<CheckIn>lambdaQuery()
                        .eq(CheckIn::getRoomId, record.getRoomId())
                        .eq(CheckIn::getStatus, IN_HOUSE)
                        .ne(CheckIn::getStudentId, studentId))
                .stream().map(r -> {
                    Roommate rm = new Roommate();
                    rm.setStudentId(r.getStudentId());
                    rm.setName(r.getStudentName());
                    rm.setBedNo(BedNoUtil.strip(r.getBedNo()));
                    return rm;
                }).toList();
        vo.setRoommates(roommates);
        return vo;
    }

    // ---------- 内部联动 ----------

    /** 供 CheckoutService 复用：退宿后刷新房间状态 */
    void refreshRoomStatus(Long roomId) {
        DormRoom room = roomMapper.selectById(roomId);
        if (room == null) {
            return;
        }
        int occupied = occupiedCount(roomId);
        int capacity = room.getCapacity() == null ? 0 : room.getCapacity();
        String status = occupied == 0 ? "空闲" : occupied >= capacity ? "已满" : "部分入住";
        room.setStatus(status);
        roomMapper.updateById(room);
    }

    int occupiedCount(Long roomId) {
        return bedMapper.selectCount(Wrappers.<DormBed>lambdaQuery()
                .eq(DormBed::getRoomId, roomId).eq(DormBed::getStatus, OCCUPIED)).intValue();
    }

    private static int parseBedNo(String bedNo) {
        int n = 0;
        for (char c : bedNo.toCharArray()) {
            if (Character.isDigit(c)) {
                n = n * 10 + (c - '0');
            } else if (n > 0 && !Character.isDigit(c)) {
                break;
            }
        }
        return n == 0 ? 1 : n;
    }

    static LocalDateTime parseDateTime(String s, LocalDateTime fallback) {
        if (s == null || s.isBlank()) {
            return fallback;
        }
        try {
            return java.time.LocalDate.parse(s).atStartOfDay();
        } catch (Exception e) {
            try {
                return java.time.LocalDateTime.parse(s);
            } catch (Exception e2) {
                return fallback;
            }
        }
    }

    private static CheckInVO toVO(CheckIn r) {
        CheckInVO vo = new CheckInVO();
        vo.setStudentId(r.getStudentId());
        vo.setStudentName(r.getStudentName());
        vo.setBuildingName(r.getBuildingName());
        vo.setRoomNo(r.getRoomNo());
        vo.setBedNo(BedNoUtil.strip(r.getBedNo()));
        vo.setCheckInTime(r.getCheckInTime());
        vo.setCheckOutTime(r.getCheckOutTime());
        vo.setSource(r.getSource());
        vo.setStatus(r.getStatus());
        vo.setRemark(r.getRemark());
        return vo;
    }
}