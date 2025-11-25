package com.lyx.secondhandsystem.dto;

import jakarta.validation.constraints.NotNull;

// 购物车添加请求DTO
public class CartAddRequest {
    @NotNull
    private Integer productId; // 商品ID

    public Integer getProductId() { return productId; } // 获取商品ID
    public void setProductId(Integer productId) { this.productId = productId; } // 设置商品ID   
}
