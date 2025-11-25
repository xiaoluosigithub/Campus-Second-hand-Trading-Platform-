package com.lyx.secondhandsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// 商品更新请求DTO
public class ProductUpdateRequest {
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price; // 商品价格
    private String description; // 商品描述

    public BigDecimal getPrice() { return price; } // 获取商品价格
    public void setPrice(BigDecimal price) { this.price = price; } // 设置商品价格
    public String getDescription() { return description; } // 获取商品描述
    public void setDescription(String description) { this.description = description; } // 设置商品描述  
}
