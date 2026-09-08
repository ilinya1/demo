package com.gzlg.dorm.service;

import com.gzlg.dorm.dto.LoginRequest;
import com.gzlg.dorm.vo.LoginResult;
import com.gzlg.dorm.vo.UserVO;

/**
 * 登录鉴权服务。
 */
public interface AuthService {

    /** 登录：校验账号密码，签发 token */
    LoginResult login(LoginRequest request);

    /** 当前登录用户信息（按用户名回查，用于 /auth/me） */
    UserVO me(String username);
}