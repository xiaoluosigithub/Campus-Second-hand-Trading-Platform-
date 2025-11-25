package com.lyx.secondhandsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// 登录拦截器
public class LoginInterceptor implements HandlerInterceptor {
    // 预处理方法，在控制器方法执行前调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        if ("GET".equals(method) && ("/api/products".equals(uri) || uri.matches("^/api/products/\\d+$")
                || "/api/categories/tree".equals(uri) || uri.matches("^/api/categories/\\d+/path$"))) {
            return true;
        }
        Object userId = request.getSession().getAttribute("userId");
        if (userId == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
