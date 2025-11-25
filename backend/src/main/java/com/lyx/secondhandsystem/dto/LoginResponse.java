package com.lyx.secondhandsystem.dto;

// 登录响应DTO
public class LoginResponse {
    private Integer userId; // 用户ID
    private String nickname; // 昵称
    private String role; // 角色

    // 获取用户ID
    public Integer getUserId() { return userId; }
    // 设置用户ID   
    public void setUserId(Integer userId) { this.userId = userId; }
    // 获取昵称
    public String getNickname() { return nickname; }
    // 设置昵称
    public void setNickname(String nickname) { this.nickname = nickname; }
    // 获取角色
    public String getRole() { return role; }
    // 设置角色 
    public void setRole(String role) { this.role = role; }
}
