package com.lyx.secondhandsystem.dto;

import java.time.LocalDateTime;

public class MessageItemResponse {
    private Integer messageId;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;

    private String senderNickname;
    private String senderAvatarUrl;
    private String receiverNickname;
    private String receiverAvatarUrl;

    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getSenderNickname() { return senderNickname; }
    public void setSenderNickname(String senderNickname) { this.senderNickname = senderNickname; }

    public String getSenderAvatarUrl() { return senderAvatarUrl; }
    public void setSenderAvatarUrl(String senderAvatarUrl) { this.senderAvatarUrl = senderAvatarUrl; }

    public String getReceiverNickname() { return receiverNickname; }
    public void setReceiverNickname(String receiverNickname) { this.receiverNickname = receiverNickname; }

    public String getReceiverAvatarUrl() { return receiverAvatarUrl; }
    public void setReceiverAvatarUrl(String receiverAvatarUrl) { this.receiverAvatarUrl = receiverAvatarUrl; }
}
