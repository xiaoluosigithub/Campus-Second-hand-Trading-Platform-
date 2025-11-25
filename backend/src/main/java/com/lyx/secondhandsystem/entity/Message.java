package com.lyx.secondhandsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// 消息实体类
@Data
@TableName("messages")
public class Message {
    @TableId(value = "message_id", type = IdType.AUTO)
    private Integer messageId; // 消息主键ID（自增）

    @TableField("sender_id")
    private Integer senderId; // 发送者用户ID

    @TableField("receiver_id")
    private Integer receiverId; // 接收者用户ID

    private String content; // 消息内容

    @TableField("is_read")
    private Boolean isRead; // 是否已读（默认false）

    @TableField("created_at")
    private LocalDateTime createdAt; // 创建时间（默认当前时间）
}
