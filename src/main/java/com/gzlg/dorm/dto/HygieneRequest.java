package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 卫生检查登记请求。
 */
@Data
public class HygieneRequest {

    @NotBlank(message = "请选择检查日期")
    private String checkDate;

    @NotBlank(message = "请输入检查人")
    private String checker;

    @NotNull(message = "请选择检查楼栋")
    private Long buildingId;

    @NotNull(message = "请选择检查房间")
    private Long roomId;

    @NotNull(message = "请确定评分")
    private Integer score;

    private List<String> deductItems;

    private List<String> photos;

    private String comment;
}