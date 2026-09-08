package com.gzlg.dorm.service;

import com.gzlg.dorm.dto.RepairTypeRequest;
import com.gzlg.dorm.entity.RepairType;

import java.util.List;

/**
 * 报修类型字典服务。
 */
public interface RepairTypeService {

    List<RepairType> list();

    void create(RepairTypeRequest req);

    void update(Long id, RepairTypeRequest req);

    void delete(Long id);
}