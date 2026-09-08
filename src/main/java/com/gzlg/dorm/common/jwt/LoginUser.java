package com.gzlg.dorm.common.jwt;

/**
 * 当前登录用户信息（从 JWT Claims 解析，存入 ThreadLocal）。
 */
public class LoginUser {

    private final String username;
    private final String role;

    public LoginUser(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}