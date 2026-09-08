package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.jwt.UserContext;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.LoginRequest;
import com.gzlg.dorm.service.AuthService;
import com.gzlg.dorm.vo.LoginResult;
import com.gzlg.dorm.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录鉴权接口（映射在 /api 之下，/auth/login 放行）。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResult> login(@RequestBody @Valid LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    /** 登出：JWT 为无状态，由客户端丢弃 token 即可 */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }

    /** 当前登录用户信息 */
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.ok(authService.me(UserContext.getUsername()));
    }
}