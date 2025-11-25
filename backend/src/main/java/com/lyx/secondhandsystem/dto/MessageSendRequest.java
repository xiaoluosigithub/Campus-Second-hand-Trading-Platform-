package com.lyx.secondhandsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 消息发送请求DTO
public class MessageSendRequest {
    @NotNull
    private Integer receiverId; // 接收者ID
    @NotBlank
    private String content; // 消息内容 

    public Integer getReceiverId() { return receiverId; } // 获取接收者ID
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; } // 设置接收者ID
    public String getContent() { return content; } // 获取消息内容  
    public void setContent(String content) { this.content = content; } // 设置消息内容
}
