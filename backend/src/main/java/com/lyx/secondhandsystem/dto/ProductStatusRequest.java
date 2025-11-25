package com.lyx.secondhandsystem.dto;

import jakarta.validation.constraints.NotNull;

// 商品状态更新请求DTO
public class ProductStatusRequest {
    @NotNull
    private Integer status; // 商品状态

    public Integer getStatus() { return status; } // 获取商品状态
    public void setStatus(Integer status) { this.status = status; } // 设置商品状态 
}
