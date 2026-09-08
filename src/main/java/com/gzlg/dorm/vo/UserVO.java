package com.gzlg.dorm.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 登录用户信息（对齐前端 user 结构：role/name/username，学生多 studentId）。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVO {

    private String role;
    private String name;
    private String username;
    private String studentId;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}