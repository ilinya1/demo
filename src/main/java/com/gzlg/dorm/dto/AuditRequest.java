package com.gzlg.dorm.dto;

import lombok.Data;

/**
 * 退宿申请审核请求。
 */
@Data
public class AuditRequest {

    private Boolean approve;

    private String rejectReason;
}