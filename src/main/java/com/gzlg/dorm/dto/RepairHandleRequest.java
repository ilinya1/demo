package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 报修处理（派单/完成）请求。
 */
@Data
public class RepairHandleRequest {

    @NotBlank(message = "请填写处理人")
    private String handlerName;

    @NotBlank(message = "请填写联系电话")
    private String handlerPhone;

    private String status;

    private String handleDesc;
}