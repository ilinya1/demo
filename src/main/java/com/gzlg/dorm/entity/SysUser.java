package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录账号。管理员 role=ADMIN（student_id 为 NULL）；学生 role=STUDENT（关联学号）。
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录名：管理员账号 / 学生学号 */
    private String username;

    /** BCrypt 密文（演示样本 {noop} 明文） */
    private String password;

    /** ADMIN / STUDENT（预留 TEACHER） */
    private String role;

    /** 逻辑FK -> student.student_id；管理员为 NULL */
    private String studentId;

    /** 1启用 / 0停用 */
    private Integer status;

    private LocalDateTime createdAt;
}