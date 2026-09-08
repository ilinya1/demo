package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 楼栋新增/编辑请求。
 */
@Data
public class BuildingReq {

    @NotBlank(message = "楼栋名称不能为空")
    private String buildingName;

    private Integer floorCount;

    private Integer roomCount;

    private String manager;
}