package com.gzlg.dorm.service;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.RoomReq;
import com.gzlg.dorm.vo.BedVO;
import com.gzlg.dorm.vo.RoomVO;

import java.util.List;
import java.util.Map;

/**
 * 房间服务。
 */
public interface RoomService {

    PageResult<RoomVO> page(Long buildingId, String roomNo, String status, String roomType,
                            int page, int pageSize);

    /** 房间新增/编辑下拉选项：{buildings:[{id,buildingName}], types:[四人间,六人间]} */
    Map<String, Object> options();

    void create(RoomReq req);

    void update(Long id, RoomReq req);

    void delete(Long id);

    List<BedVO> beds(Long roomId);
}