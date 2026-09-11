package com.gzlg.dorm.service;

/**
 * 改密/重置密码。
 */
public interface AccountService {

    void changePassword(String username, String oldPassword, String newPassword);

    void resetStudentPassword(String username);
}