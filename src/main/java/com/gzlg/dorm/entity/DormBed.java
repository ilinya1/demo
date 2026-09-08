package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 床位（独立建模，二期调宿/维修床可复用）。
 */
@Data
@TableName("dorm_bed")
public class DormBed {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 逻辑FK -> dorm_room.id */
    private Long roomId;

    /** 床位号，如 1号床 */
    private String bedNo;

    /** 空闲/占用/维修 */
    private String status;

    private LocalDateTime createdAt;
}