package com.gzlg.dorm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 房间新增/编辑请求。
 */
@Data
public class RoomReq {

    @NotNull(message = "请选择楼栋")
    private Long buildingId;

    @NotNull(message = "请输入楼层")
    private Integer floor;

    @NotBlank(message = "房间号不能为空")
    private String roomNo;

    @NotNull(message = "请选择容纳人数")
    @Min(value = 1, message = "容纳人数至少为 1")
    private Integer capacity;
}