package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.jwt.UserContext;
import com.gzlg.dorm.common.result.ResultCode;
import com.gzlg.dorm.entity.SysUser;
import com.gzlg.dorm.mapper.SysUserMapper;
import com.gzlg.dorm.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 改密/重置密码实现。
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final String DEFAULT_STUDENT_PASSWORD = "123456";

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        SysUser account = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
        if (account == null) {
            throw new BizException(ResultCode.UNAUTHORIZED, "账号不存在");
        }
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new BizException("原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 20) {
            throw new BizException("新密码长度应为 6-20 位");
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(account);
    }

    @Override
    @Transactional
    public void resetStudentPassword(String username) {
        // 仅管理员可重置密码（拦截器在前端已拦，此处服务层兜底）
        if (!"ADMIN".equals(UserContext.getRole())) {
            throw new BizException("仅管理员可重置密码");
        }
        SysUser account = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
        if (account == null) {
            SysUser create = new SysUser();
            create.setUsername(username);
            create.setPassword(passwordEncoder.encode(DEFAULT_STUDENT_PASSWORD));
            create.setRole("STUDENT");
            create.setStudentId(username);
            create.setStatus(1);
            create.setCreatedAt(LocalDateTime.now());
            sysUserMapper.insert(create);
            return;
        }
        account.setPassword(passwordEncoder.encode(DEFAULT_STUDENT_PASSWORD));
        sysUserMapper.updateById(account);
    }
}