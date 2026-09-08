package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统参数（系统设置）。param_key 业务唯一，存内置参数如系统名称/登录欢迎语等。
 */
@Data
@TableName("sys_parameter")
public class SysParameter {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 参数键，业务唯一 */
    private String paramKey;

    private String paramName;

    private String paramValue;

    private LocalDateTime updatedAt;
}