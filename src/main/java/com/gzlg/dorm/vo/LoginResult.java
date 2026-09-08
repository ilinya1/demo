package com.gzlg.dorm.vo;

/**
 * 登录结果 {token, user}（对齐前端）。
 */
public class LoginResult {

    private String token;
    private UserVO user;

    public LoginResult() {
    }

    public LoginResult(String token, UserVO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }
}