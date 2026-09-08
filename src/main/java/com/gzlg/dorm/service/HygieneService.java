package com.gzlg.dorm.service;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.HygieneRequest;
import com.gzlg.dorm.vo.HygieneVO;

/**
 * 卫生检查服务。
 */
public interface HygieneService {

    PageResult<HygieneVO> page(String checkDate, Long buildingId, Long roomId, String result,
                               int page, int pageSize);

    void create(HygieneRequest req);
}