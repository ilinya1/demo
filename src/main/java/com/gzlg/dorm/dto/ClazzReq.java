package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 班级新增/编辑请求（前端字段：name/college/major/grade/headTeacher）。
 */
@Data
public class ClazzReq {

    @NotBlank(message = "班级名称不能为空")
    private String name;

    private String college;

    private String major;

    private String grade;

    private String headTeacher;
}