package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 直接退宿请求。
 */
@Data
public class DirectCheckoutRequest {

    @NotBlank(message = "请输入学号")
    private String studentId;

    private String checkoutDate;

    private String reason;

    private String remark;
}