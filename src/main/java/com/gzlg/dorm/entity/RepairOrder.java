package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修单。
 */
@Data
@TableName("repair_order")
public class RepairOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务唯一 */
    private String orderNo;

    /** 逻辑FK -> student.student_id */
    private String studentId;

    /** 逻辑FK -> dorm_building.id（NOT NULL） */
    private Long buildingId;

    /** 逻辑FK -> dorm_room.id（NOT NULL） */
    private Long roomId;

    /** 逻辑FK -> repair_type.id；报修物品/类型 */
    private Long typeId;

    private String description;

    /** 联系电话（提交时留） */
    private String contactPhone;

    /** 图片路径列表（JSON 数组字符串） */
    private String images;

    /** 待处理/处理中/已完成 */
    private String status;

    private String handlerName;

    private String handlerPhone;

    private String handleDesc;

    private LocalDateTime createTime;

    private LocalDateTime handleTime;
}