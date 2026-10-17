package com.niit.mall.security;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户上下文拦截器：从网关传递的请求头解析当前登录用户，写入 ThreadLocal。
 * 每个业务服务在自己的 WebMvcConfig 中注册本拦截器。
 */
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String id = request.getHeader("X-User-Id");
        if (id != null && !id.isEmpty()) {
            LoginUser user = new LoginUser();
            user.setId(Long.valueOf(id));
            user.setUsername(request.getHeader("X-Username"));
            user.setRole(request.getHeader("X-User-Role"));
            UserContextHolder.set(user);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.clear();
    }
}
