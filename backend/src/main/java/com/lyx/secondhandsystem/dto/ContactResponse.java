package com.lyx.secondhandsystem.dto;

import java.time.LocalDateTime;

// 联系人响应DTO
public class ContactResponse {
    private Integer contactId; // 联系人ID
    private LocalDateTime lastAt; // 最后联系时间
    private String contactNickname; // 联系人昵称
    private String contactAvatarUrl; // 联系人头像

    public Integer getContactId() { return contactId; } // 获取联系人ID
    public void setContactId(Integer contactId) { this.contactId = contactId; } // 设置联系人ID
    public LocalDateTime getLastAt() { return lastAt; } // 获取最后联系时间
    public void setLastAt(LocalDateTime lastAt) { this.lastAt = lastAt; } // 设置最后联系时间

    public String getContactNickname() { return contactNickname; }
    public void setContactNickname(String contactNickname) { this.contactNickname = contactNickname; }

    public String getContactAvatarUrl() { return contactAvatarUrl; }
    public void setContactAvatarUrl(String contactAvatarUrl) { this.contactAvatarUrl = contactAvatarUrl; }
}
