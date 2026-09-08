package com.gzlg.dorm.vo;

import lombok.Data;

/**
 * 学生列表/详情项（含 className，前端以 className 为关联键展示）。
 */
@Data
public class StudentVO {

    private String studentId;
    private String name;
    private String gender;
    private String college;
    private String major;
    private Long classId;
    private String className;
    private String contactPhone;
    private String emergencyContact;
    private String emergencyPhone;
    private String academicStatus;
    private String housingStatus;
}