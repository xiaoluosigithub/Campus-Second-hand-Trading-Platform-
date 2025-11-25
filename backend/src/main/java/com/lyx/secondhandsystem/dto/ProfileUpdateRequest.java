package com.lyx.secondhandsystem.dto;

// 个人资料更新请求DTO
public class ProfileUpdateRequest {
    private String nickname; // 昵称
    private String avatarUrl; // 头像URL
    private String signature; // 个人签名

    public String getNickname() { return nickname; } // 获取昵称
    public void setNickname(String nickname) { this.nickname = nickname; } // 设置昵称
    public String getAvatarUrl() { return avatarUrl; } // 获取头像URL
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; } // 设置头像URL
    public String getSignature() { return signature; } // 获取个人签名
    public void setSignature(String signature) { this.signature = signature; } // 设置个人签名
}
