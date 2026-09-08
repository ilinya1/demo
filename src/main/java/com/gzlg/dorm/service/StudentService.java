package com.gzlg.dorm.service;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.StudentReq;
import com.gzlg.dorm.vo.StudentVO;

/**
 * 学生服务。
 */
public interface StudentService {

    PageResult<StudentVO> page(String studentId, String name, String college,
                               String academicStatus, String className, int page, int pageSize);

    StudentVO getByStudentId(String studentId);

    void create(StudentReq req);

    void update(String studentId, StudentReq req);

    void delete(String studentId);
}