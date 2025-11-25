package com.lyx.secondhandsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 订单实体类
@Data
@TableName("orders")
public class Order {
    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId; // 订单主键ID（自增）

    @TableField("order_number")
    private String orderNumber; // 订单编号（唯一）

    @TableField("buyer_id")
    private Integer buyerId; // 买家用户ID

    @TableField("seller_id")
    private Integer sellerId; // 卖家用户ID

    @TableField("product_id")
    private Integer productId; // 商品ID

    @TableField("product_title_snapshot")
    private String productTitleSnapshot; // 商品标题快照（订单创建时保存）

    @TableField("product_image_snapshot")
    private String productImageSnapshot; // 商品图片快照（订单创建时保存）

    @TableField("transaction_price")
    private BigDecimal transactionPrice; // 交易价格

    @TableField("shipping_name")
    private String shippingName; // 配送姓名

    @TableField("shipping_phone")
    private String shippingPhone; // 配送手机号

    @TableField("shipping_address")
    private String shippingAddress; // 配送地址 

    @TableField("tracking_number")
    private String trackingNumber; // 物流跟踪号

    @TableField("payment_method")
    private String paymentMethod; // 支付方式

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt; // 创建时间（默认当前时间）

    @TableField("updated_at")
    private LocalDateTime updatedAt; // 更新时间（默认当前时间）
}
