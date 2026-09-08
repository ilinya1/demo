package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 入住登记请求。
 */
@Data
public class CheckinRequest {

    @NotBlank(message = "请选择学生")
    private String studentId;

    @NotNull(message = "请选择房间")
    private Long roomId;

    @NotNull(message = "请选择床位")
    private Integer bedNo;

    /** yyyy-MM-dd，可空（空则取当前时间） */
    private String checkInDate;

    private String remark;
}