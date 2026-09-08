package com.gzlg.dorm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 退宿申请项（含学生与当前宿舍展示信息，由表关联回填）。
 */
@Data
public class CheckoutAppVO {

    private Long id;
    private String applyNo;
    private String studentId;
    private String studentName;
    private String college;
    private String buildingName;
    private String roomNo;
    private String bedNo;
    private Long roomId;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planDate;
    private String description;
    private String status;
    private String rejectReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
}