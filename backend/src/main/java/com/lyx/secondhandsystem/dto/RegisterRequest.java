package com.lyx.secondhandsystem.dto;

import jakarta.validation.constraints.NotBlank;

// 注册请求DTO
public class RegisterRequest {
    // 用户名
    @NotBlank
    private String username;
    // 密码
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    // 昵称
    private String nickname;
    
    // 获取用户名
    public String getUsername() { return username; }
    // 设置用户名   
    public void setUsername(String username) { this.username = username; }
    // 获取密码
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    // 获取昵称
    public String getNickname() { return nickname; }
    // 设置昵称 
    public void setNickname(String nickname) { this.nickname = nickname; }
}
