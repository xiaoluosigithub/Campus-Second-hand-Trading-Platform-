package com.lyx.secondhandsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 订单创建请求DTO
public class OrderCreateRequest {
    @NotNull
    private Integer productId; // 商品ID
    @NotBlank
    private String shippingName; // 收货人姓名
    @NotBlank
    private String shippingPhone; // 收货人手机号
    @NotBlank
    private String shippingAddress; // 收货人地址
    @NotBlank
    private String paymentMethod; // 可选：alipay|wechatpay

    public Integer getProductId() { return productId; } // 获取商品ID
    public void setProductId(Integer productId) { this.productId = productId; } // 设置商品ID
    public String getShippingName() { return shippingName; } // 获取收货人姓名
    public void setShippingName(String shippingName) { this.shippingName = shippingName; } // 设置收货人姓名
    public String getShippingPhone() { return shippingPhone; } // 获取收货人手机号
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; } // 设置收货人手机号
    public String getShippingAddress() { return shippingAddress; } // 获取收货人地址
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; } // 设置收货人地址
    public String getPaymentMethod() { return paymentMethod; } // 获取支付方式
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; } // 设置支付方式  
}
