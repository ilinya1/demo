package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 学院新增/编辑请求（前端字段：name）。
 */
@Data
public class CollegeReq {

    @NotBlank(message = "请输入学院名称")
    @Size(max = 50, message = "学院名称不能超过50字")
    private String name;

    /** 排序，越小越靠前（可选，默认 0） */
    private Integer sort;
}