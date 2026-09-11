package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.util.PhoneUtils;
import com.gzlg.dorm.entity.Clazz;
import com.gzlg.dorm.entity.Student;
import com.gzlg.dorm.entity.SysUser;
import com.gzlg.dorm.mapper.ClazzMapper;
import com.gzlg.dorm.mapper.StudentMapper;
import com.gzlg.dorm.mapper.SysUserMapper;
import com.gzlg.dorm.service.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 个人中心实现。管理员资料取自 sys_user（name 用占位显示名），学生资料取自 student。
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    private static final String ADMIN_DISPLAY_NAME = "系统管理员";

    private final SysUserMapper sysUserMapper;
    private final StudentMapper studentMapper;
    private final ClazzMapper clazzMapper;

    public ProfileServiceImpl(SysUserMapper sysUserMapper, StudentMapper studentMapper, ClazzMapper clazzMapper) {
        this.sysUserMapper = sysUserMapper;
        this.studentMapper = studentMapper;
        this.clazzMapper = clazzMapper;
    }

    @Override
    public Map<String, Object> getProfile(String role, String username) {
        if ("ADMIN".equals(role)) {
            SysUser account = queryUser(username);
            if (account == null) {
                throw new BizException("账号不存在");
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("username", account.getUsername());
            map.put("name", ADMIN_DISPLAY_NAME);
            map.put("role", "ADMIN");
            map.put("roleName", "管理员");
            map.put("phone", account.getPhone());
            map.put("email", account.getEmail());
            return map;
        }
        Student student = studentMapper.selectById(username);
        if (student == null) {
            throw new BizException("学生不存在");
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("username", student.getStudentId());
        map.put("name", student.getName());
        map.put("role", "STUDENT");
        map.put("roleName", "学生");
        map.put("gender", student.getGender());
        map.put("college", student.getCollege());
        map.put("major", student.getMajor());
        map.put("className", resolveClassName(student.getClassId()));
        map.put("phone", student.getContactPhone());
        map.put("emergency", student.getEmergencyContact());
        map.put("academicStatus", student.getAcademicStatus());
        map.put("housingStatus", student.getHousingStatus());
        return map;
    }

    @Override
    @Transactional
    public void updateProfile(String role, String username, Map<String, Object> data) {
        if ("ADMIN".equals(role)) {
            SysUser account = queryUser(username);
            if (account == null) {
                throw new BizException("账号不存在");
            }
            if (data.containsKey("phone")) {
                account.setPhone(PhoneUtils.requireMobile(asString(data.get("phone")), "联系电话"));
            }
            if (data.containsKey("email")) {
                account.setEmail(asString(data.get("email")));
            }
            sysUserMapper.updateById(account);
            return;
        }
        Student student = studentMapper.selectById(username);
        if (student == null) {
            throw new BizException("学生不存在");
        }
        if (data.containsKey("phone")) {
            student.setContactPhone(PhoneUtils.requireMobile(asString(data.get("phone")), "联系电话"));
        }
        if (data.containsKey("emergency")) {
            student.setEmergencyContact(asString(data.get("emergency")));
        }
        studentMapper.updateById(student);
    }

    private SysUser queryUser(String username) {
        return sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
    }

    private String resolveClassName(Long classId) {
        if (classId == null) {
            return null;
        }
        Clazz clazz = clazzMapper.selectById(classId);
        return clazz == null ? null : clazz.getClassName();
    }

    private String asString(Object o) {
        return o == null ? null : o.toString();
    }
}