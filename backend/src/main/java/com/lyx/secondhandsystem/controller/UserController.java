package com.lyx.secondhandsystem.controller;

import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.dto.ProfileUpdateRequest;
import com.lyx.secondhandsystem.entity.User;
import com.lyx.secondhandsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

// 用户相关接口
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService; // 用户服务

    public UserController(UserService userService) {
        this.userService = userService; // 构造函数注入用户服务
    }

    // 更新个人资料
    @PatchMapping("/me/profile")
    public ApiResponse<Void> updateProfile(@RequestBody @Valid ProfileUpdateRequest req, HttpSession session) {
        // 从会话中获取当前登录用户ID
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 根据ID获取用户
        User u = userService.getById((Integer) uid);
        if (u == null) throw new IllegalArgumentException("用户不存在");
        // 更新用户资料
        if (req.getNickname() != null && !req.getNickname().trim().isEmpty()) {
            u.setNickname(req.getNickname().trim());
        }
        // 更新用户头像URL
        if (req.getAvatarUrl() != null && !req.getAvatarUrl().trim().isEmpty()) {
            u.setAvatarUrl(req.getAvatarUrl().trim());
        }
        // 更新用户个人签名
        if (req.getSignature() != null && !req.getSignature().trim().isEmpty()) {
            u.setSignature(req.getSignature().trim());
        }
        // 保存更新后的用户资料
        userService.update(u);
        return ApiResponse.ok(null); // 返回成功响应
    }

    // 获取我的资料
    @GetMapping("/me")
    public ApiResponse<User> me(HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        User u = userService.getById((Integer) uid);
        if (u == null) throw new IllegalArgumentException("用户不存在");
        return ApiResponse.ok(u);
    }
}
