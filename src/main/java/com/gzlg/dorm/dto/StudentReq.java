package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 学生新增/编辑请求（前端传班级名 className，后端映射为 classId）。
 */
@Data
public class StudentReq {

    @NotBlank(message = "学号不能为空")
    private String studentId;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    private String college;

    private String major;

    @NotBlank(message = "班级不能为空")
    private String className;

    private String contactPhone;

    private String emergencyContact;

    private String emergencyPhone;

    private String academicStatus;

    private String housingStatus;
}