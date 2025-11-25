package com.lyx.secondhandsystem.controller;

import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.dto.LoginRequest;
import com.lyx.secondhandsystem.dto.LoginResponse;
import com.lyx.secondhandsystem.dto.RegisterRequest;
import com.lyx.secondhandsystem.entity.User;
import com.lyx.secondhandsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 认证控制器，处理用户注册和登录
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // 注入用户服务
    private final UserService userService;
    // 密码编码器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 构造函数注入用户服务
    public AuthController(UserService userService) {
        // 注入用户服务
        this.userService = userService;
    }

    // 注册用户
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Integer>> register(@RequestBody @Valid RegisterRequest req) {
        // 检查用户名是否已存在
        User exists = userService.getByUsername(req.getUsername());
        if (exists != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, "当前账号已存在"));
        }
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, "两次输入密码不一致"));
        }
        // 创建新用户
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setNickname(req.getNickname() != null && !req.getNickname().isEmpty() ? req.getNickname() : req.getUsername());
        u.setRole("user");
        userService.create(u);
        // 返回注册成功响应
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(u.getUserId()));
    }

    // 登录用户
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest req, HttpSession session) {
        // 检查用户名是否存在
        User u = userService.getByUsername(req.getUsername());
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401, "账号不存在"));
        }
        // 检查密码是否匹配
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401, "密码错误"));
        }
        session.setAttribute("userId", u.getUserId());
        session.setAttribute("nickname", u.getNickname());
        session.setAttribute("role", u.getRole());
        // 返回登录成功响应
        LoginResponse resp = new LoginResponse();
        resp.setUserId(u.getUserId());
        resp.setNickname(u.getNickname());
        resp.setRole(u.getRole());
        // 返回登录成功响应
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    // 注销登录
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        // 失效会话
        session.invalidate();
        // 返回注销成功响应
        return ApiResponse.ok(null);
    }
}
