package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修类型字典。
 */
@Data
@TableName("repair_type")
public class RepairType {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 报修物品/类型名称，如 灯管（唯一） */
    private String name;

    /** 排序，越小越靠前 */
    private Integer sort;

    private LocalDateTime createdAt;
}