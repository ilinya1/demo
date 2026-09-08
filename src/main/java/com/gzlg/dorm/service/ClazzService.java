package com.gzlg.dorm.service;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.ClazzReq;
import com.gzlg.dorm.vo.ClazzVO;

/**
 * 班级服务。
 */
public interface ClazzService {

    PageResult<ClazzVO> page(String name, String college, String grade, int page, int pageSize);

    void create(ClazzReq req);

    void update(Long id, ClazzReq req);

    void delete(Long id);
}