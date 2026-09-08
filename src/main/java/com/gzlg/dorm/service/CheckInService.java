package com.gzlg.dorm.service;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.CheckinRequest;
import com.gzlg.dorm.vo.CheckInVO;
import com.gzlg.dorm.vo.CheckinRoomVO;
import com.gzlg.dorm.vo.CurrentRoomVO;

import java.util.List;

/**
 * 入住业务服务。
 */
public interface CheckInService {

    List<CheckinRoomVO> checkinRooms(Long buildingId);

    List<Integer> checkinFreeBeds(Long roomId);

    void checkIn(CheckinRequest req);

    /** 执行退宿（source=apply/direct）：置 check_in 已退宿 + 释放床位 + 学生住宿状态 + 刷新房间 */
    void checkout(String studentId, java.time.LocalDateTime checkoutTime, String source, String remark);

    PageResult<CheckInVO> listRecords(String studentId, String studentName,
                                      String buildingName, String status, int page, int pageSize);

    CurrentRoomVO currentRoom(String studentId);
}