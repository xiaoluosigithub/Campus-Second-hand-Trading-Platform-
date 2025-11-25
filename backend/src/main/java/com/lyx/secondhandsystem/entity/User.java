package com.lyx.secondhandsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

// 用户实体类
@Data
@TableName("users")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId; // 用户ID

    private String username; // 用户名

    @TableField("password_hash")
    @JsonIgnore
    private String passwordHash; // 密码哈希值

    private String nickname; // 昵称

    @TableField("avatar_url")
    private String avatarUrl; // 头像URL

    private String signature; // 签名

    private String role; // 角色

    @TableField("created_at")
    private LocalDateTime createdAt; // 创建时间

    @TableField("updated_at")
    private LocalDateTime updatedAt; // 更新时间
}
