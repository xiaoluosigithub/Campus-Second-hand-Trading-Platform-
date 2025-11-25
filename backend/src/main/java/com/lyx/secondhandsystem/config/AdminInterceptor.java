package com.lyx.secondhandsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// 管理员拦截器
public class AdminInterceptor implements HandlerInterceptor {
    // 预处理方法
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查用户角色是否为管理员
        Object role = request.getSession().getAttribute("role");
        if (role == null || !"admin".equals(role)) {
            // 如果用户角色不是管理员，返回403 Forbidden状态
            response.setStatus(403);
            return false;
        }
        return true;
    }
}
