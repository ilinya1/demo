package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 楼栋。
 */
@Data
@TableName("dorm_building")
public class DormBuilding {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String buildingName;

    private Integer floorCount;

    private Integer roomCount;

    private String manager;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}