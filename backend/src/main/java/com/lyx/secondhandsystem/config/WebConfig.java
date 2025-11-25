package com.lyx.secondhandsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Web 配置类，用于注册登录拦截器
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 添加登录拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**"
                );
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/api/admin/**");
    }
    // 配置静态资源映射，将 /uploads/** 映射到文件系统中的 uploads 目录
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
    }
}
