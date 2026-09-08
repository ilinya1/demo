package com.gzlg.dorm.service;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.BuildingReq;
import com.gzlg.dorm.entity.DormBuilding;

import java.util.List;
import java.util.Map;

/**
 * 楼栋服务。
 */
public interface BuildingService {

    PageResult<DormBuilding> page(String buildingName, String manager, int page, int pageSize);

    /** 楼栋下拉选项：[{id, buildingName}] */
    List<Map<String, Object>> options();

    void create(BuildingReq req);

    void update(Long id, BuildingReq req);

    void delete(Long id);
}