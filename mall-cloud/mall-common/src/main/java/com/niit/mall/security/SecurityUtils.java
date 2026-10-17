package com.niit.mall.security;

/**
 * 获取当前登录用户工具（从 ThreadLocal 读取，由网关注入的请求头解析而来）
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户，未登录返回 null
     */
    public static LoginUser getCurrentUser() {
        return UserContextHolder.get();
    }

    /**
     * 获取当前登录用户 ID，未登录返回 null
     */
    public static Long getCurrentUserId() {
        LoginUser user = getCurrentUser();
        return user == null ? null : user.getId();
    }
}
