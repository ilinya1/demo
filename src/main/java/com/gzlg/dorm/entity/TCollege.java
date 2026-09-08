package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学院字典。class/student 的 college 字符串以其 name 软关联。
 */
@Data
@TableName("t_college")
public class TCollege {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学院名称，唯一 */
    private String name;

    /** 排序，越小越靠前 */
    private Integer sort;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}