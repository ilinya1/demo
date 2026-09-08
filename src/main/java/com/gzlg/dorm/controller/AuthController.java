package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.jwt.UserContext;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.LoginRequest;
import com.gzlg.dorm.service.AccountService;
import com.gzlg.dorm.service.AuthService;
import com.gzlg.dorm.vo.LoginResult;
import com.gzlg.dorm.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 登录鉴权接口（映射在 /api 之下，/auth/login 放行）。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;

    public AuthController(AuthService authService, AccountService accountService) {
        this.authService = authService;
        this.accountService = accountService;
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

    /** 修改密码 */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, Object> body) {
        accountService.changePassword(asString(body.get("username")),
                asString(body.get("oldPassword")), asString(body.get("newPassword")));
        return Result.ok();
    }

    /** 重置学生密码为默认 123456 */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody Map<String, Object> body) {
        accountService.resetStudentPassword(asString(body.get("username")), asString(body.get("name")));
        return Result.ok();
    }

    private String asString(Object o) {
        return o == null ? null : o.toString();
    }
}