package com.gzlg.dorm.common.jwt;

/**
 * 当前登录用户上下文（ThreadLocal）。拦截器在请求开始时写入、结束后清除。
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static String getUsername() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.getUsername();
    }

    public static String getRole() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.getRole();
    }

    public static void clear() {
        HOLDER.remove();
    }
}