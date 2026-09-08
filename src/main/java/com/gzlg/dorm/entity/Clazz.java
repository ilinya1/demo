package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级。@TableName("class")：class 为数据库表名，实体类名用 Clazz 避 Java 保留字。
 */
@Data
@TableName("class")
public class Clazz {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** class_name */
    private String className;

    private String college;

    private String major;

    private String grade;

    private String headTeacher;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}