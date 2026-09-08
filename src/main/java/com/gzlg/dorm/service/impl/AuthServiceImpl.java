package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.jwt.JwtUtil;
import com.gzlg.dorm.common.result.ResultCode;
import com.gzlg.dorm.dto.LoginRequest;
import com.gzlg.dorm.entity.Student;
import com.gzlg.dorm.entity.SysUser;
import com.gzlg.dorm.mapper.StudentMapper;
import com.gzlg.dorm.mapper.SysUserMapper;
import com.gzlg.dorm.service.AuthService;
import com.gzlg.dorm.vo.LoginResult;
import com.gzlg.dorm.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 登录鉴权实现：sys_user 校验账号/密码/状态，按角色回填用户信息，签发 JWT。
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** 管理员显示名（sys_user 无姓名字段，统一用此占位展示，与前端演示一致） */
    private static final String ADMIN_DISPLAY_NAME = "系统管理员";

    private final SysUserMapper sysUserMapper;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(SysUserMapper sysUserMapper, StudentMapper studentMapper,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.studentMapper = studentMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResult login(LoginRequest request) {
        SysUser account = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, request.getUsername()));
        // 账号不存在或密码不匹配，统一提示，避免暴露账号是否存在
        if (account == null || !passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new BizException(ResultCode.PARAM_ERROR, "账号或密码错误");
        }
        if (account.getStatus() != null && account.getStatus() == 0) {
            throw new BizException(ResultCode.FORBIDDEN, "账号已停用，请联系管理员");
        }

        String token = jwtUtil.generateToken(account.getUsername(), account.getRole());
        return new LoginResult(token, buildUser(account));
    }

    @Override
    public UserVO me(String username) {
        SysUser account = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
        if (account == null) {
            throw new BizException(ResultCode.UNAUTHORIZED, "账号不存在");
        }
        return buildUser(account);
    }

    private UserVO buildUser(SysUser account) {
        UserVO user = new UserVO();
        user.setRole(account.getRole());
        user.setUsername(account.getUsername());

        if ("STUDENT".equals(account.getRole())) {
            user.setStudentId(account.getUsername());
            Student student = studentMapper.selectById(account.getStudentId() != null
                    ? account.getStudentId() : account.getUsername());
            user.setName(student != null ? student.getName() : account.getUsername());
        } else {
            user.setName(ADMIN_DISPLAY_NAME);
        }
        return user;
    }
}