package com.gzlg.dorm.service;

import java.util.Map;

/**
 * 个人中心：资料展示/更新。
 */
public interface ProfileService {

    /**
     * 获取个人资料。role=ADMIN 读 sys_user；role=STUDENT 按学号读 student。
     */
    Map<String, Object> getProfile(String role, String username);

    /**
     * 更新个人资料。ADMIN 更新 phone/email；STUDENT 更新 phone/emergency。
     */
    void updateProfile(String role, String username, Map<String, Object> data);
}