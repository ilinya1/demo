package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 卫生检查记录。
 */
@Data
@TableName("hygiene_record")
public class HygieneRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDate checkDate;

    private String checker;

    /** 逻辑FK -> dorm_building.id（NOT NULL） */
    private Long buildingId;

    /** 逻辑FK -> dorm_room.id（NOT NULL） */
    private Long roomId;

    /** 评分（100 起扣） */
    private Integer score;

    /** 优秀/合格/不合格 */
    private String result;

    /** 扣分项（JSON 数组字符串） */
    private String deductItems;

    /** 照片路径列表（JSON 数组字符串） */
    private String photos;

    private String comment;

    private LocalDateTime createdAt;
}