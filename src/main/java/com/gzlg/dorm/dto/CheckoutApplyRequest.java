package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学生提交退宿申请请求。
 */
@Data
public class CheckoutApplyRequest {

    @NotBlank(message = "请先登录")
    private String studentId;

    @NotBlank(message = "请选择退宿原因")
    private String reason;

    @NotBlank(message = "请选择计划退宿日期")
    private String planDate;

    private String description;
}