package com.lyx.secondhandsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 商品实体类
@Data
@TableName(value = "products", autoResultMap = true)
public class Product {
    @TableId(value = "product_id", type = IdType.AUTO)
    private Integer productId; // 商品ID（主键）

    @TableField("seller_id")
    private Integer sellerId; // 卖家ID（外键）

    @TableField("category_id")
    private Integer categoryId; // 分类ID（外键）

    private String title; // 商品标题

    private String description; // 商品描述

    private BigDecimal price; // 商品价格

    @TableField(value = "images", typeHandler = JacksonTypeHandler.class)
    private List<String> images; // 商品图片（JSON格式）

    private Integer status;

    @TableField("rejection_reason")
    private String rejectionReason; // 审核拒绝原因

    @TableField("view_count")
    private Integer viewCount; // 商品浏览次数

    @TableField("created_at")
    private LocalDateTime createdAt; // 创建时间

    @TableField("updated_at")
    private LocalDateTime updatedAt; // 更新时间
}
