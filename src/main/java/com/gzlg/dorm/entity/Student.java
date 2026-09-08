package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生。主键为业务学号（student_id，非自增）。
 */
@Data
@TableName("student")
public class Student {

    @TableId(type = IdType.INPUT)
    private String studentId;

    private String name;

    private String gender;

    private String college;

    private String major;

    /** 逻辑FK -> class.id */
    private Long classId;

    private String contactPhone;

    private String emergencyContact;

    private String emergencyPhone;

    private String academicStatus;

    /** 冗余：在住/已退宿/未住，随 check_in 同步 */
    private String housingStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}