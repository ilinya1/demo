package com.gzlg.dorm.service;

import com.gzlg.dorm.dto.CollegeReq;
import com.gzlg.dorm.entity.TCollege;

import java.util.List;

/**
 * 学院字典服务。list 返回数组对齐前端契约；改名级联 class/student，删除引用拦截。
 */
public interface CollegeService {

    List<TCollege> list();

    void create(CollegeReq req);

    void update(Long id, CollegeReq req);

    void delete(Long id);
}