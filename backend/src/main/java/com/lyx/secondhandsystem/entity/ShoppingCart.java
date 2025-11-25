package com.lyx.secondhandsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// 购物车实体类
@Data
@TableName("shopping_cart")
public class ShoppingCart {
    @TableId(value = "cart_id", type = IdType.AUTO)
    private Integer cartId; // 购物车ID（主键）

    @TableField("user_id")
    private Integer userId; // 用户ID（外键）

    @TableField("product_id")
    private Integer productId; // 商品ID（外键）

    @TableField("created_at")
    private LocalDateTime createdAt; // 创建时间
}
