package com.gzlg.dorm.vo;

import lombok.Data;

/**
 * 房间列表项（含楼栋名与已住人数，供前端房间管理列表展示）。
 */
@Data
public class RoomVO {

    private Long id;
    private Long buildingId;
    private String buildingName;
    private Integer floor;
    private String roomNo;
    private Integer capacity;
    private String roomType;
    private String status;
    private Integer occupiedCount;
}