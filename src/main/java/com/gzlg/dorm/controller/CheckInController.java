package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.CheckinRequest;
import com.gzlg.dorm.service.CheckInService;
import com.gzlg.dorm.vo.CheckInVO;
import com.gzlg.dorm.vo.CheckinRoomVO;
import com.gzlg.dorm.vo.CurrentRoomVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 入住业务接口（入住登记 / 可选房间床位 / 入住记录 / 我的宿舍）。
 */
@RestController
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    /** 入住登记可选房间：该楼栋有剩余床位的房间 */
    @GetMapping("/checkin/rooms")
    public Result<List<CheckinRoomVO>> checkinRooms(@RequestParam(required = false) Long buildingId) {
        return Result.ok(checkInService.checkinRooms(buildingId));
    }

    /** 房间空闲床位号 */
    @GetMapping("/checkin/rooms/{roomId}/free-beds")
    public Result<List<Integer>> freeBeds(@PathVariable Long roomId) {
        return Result.ok(checkInService.checkinFreeBeds(roomId));
    }

    /** 办理入住 */
    @PostMapping("/checkin")
    public Result<Void> checkin(@RequestBody @Valid CheckinRequest req) {
        checkInService.checkIn(req);
        return Result.ok();
    }

    /** 入住记录 */
    @GetMapping("/checkin-records")
    public Result<PageResult<CheckInVO>> records(@RequestParam(required = false) String studentId,
                                                 @RequestParam(required = false) String studentName,
                                                 @RequestParam(required = false) String buildingName,
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(checkInService.listRecords(studentId, studentName, buildingName, status, page, pageSize));
    }

    /** 学生端·我的宿舍 */
    @GetMapping("/student/current-room")
    public Result<CurrentRoomVO> currentRoom(@RequestParam String studentId) {
        return Result.ok(checkInService.currentRoom(studentId));
    }
}