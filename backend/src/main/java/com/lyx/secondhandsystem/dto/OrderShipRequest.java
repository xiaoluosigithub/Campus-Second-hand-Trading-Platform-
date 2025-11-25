package com.lyx.secondhandsystem.dto;

import jakarta.validation.constraints.NotBlank;

// 订单发货请求DTO
public class OrderShipRequest {
    @NotBlank
    private String trackingNumber; // 物流跟踪号

    public String getTrackingNumber() { return trackingNumber; } // 获取物流跟踪号
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; } // 设置物流跟踪号
}
