package com.niit.mall.security;

/**
 * 当前登录用户 ThreadLocal 持有器（由 UserContextInterceptor 写入）
 */
public class UserContextHolder {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
