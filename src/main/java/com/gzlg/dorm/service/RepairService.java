package com.gzlg.dorm.service;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.RepairCreateRequest;
import com.gzlg.dorm.dto.RepairHandleRequest;
import com.gzlg.dorm.vo.RepairVO;

/**
 * 报修服务。
 */
public interface RepairService {

    PageResult<RepairVO> page(String orderNo, Long buildingId, String studentId, String status,
                              int page, int pageSize);

    RepairVO detail(Long id);

    void create(RepairCreateRequest req);

    void handle(Long id, RepairHandleRequest req);
}