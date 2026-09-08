package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 房间。
 */
@Data
@TableName("dorm_room")
public class DormRoom {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 逻辑FK -> dorm_building.id */
    private Long buildingId;

    private Integer floor;

    private String roomNo;

    /** 床位容量，与 dorm_bed 行数一致（Service 新建房间时保证） */
    private Integer capacity;

    /** 四人间/六人间 */
    private String roomType;

    /** 空闲/部分入住/已满/维修中 */
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}