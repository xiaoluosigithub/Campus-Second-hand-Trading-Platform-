package com.lyx.secondhandsystem.dto;

import com.lyx.secondhandsystem.entity.Product;

// 商品详情响应类
public class ProductDetailResponse {
    private Product product; // 商品信息
    private Integer sellerId; // 卖家ID
    private String sellerNickname; // 卖家昵称
    private String sellerAvatarUrl; // 卖家头像URL

    public Product getProduct() { return product; } // 获取商品信息
    public void setProduct(Product product) { this.product = product; } // 设置商品信息
    public Integer getSellerId() { return sellerId; } // 获取卖家ID
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; } // 设置卖家ID
    public String getSellerNickname() { return sellerNickname; } // 获取卖家昵称
    public void setSellerNickname(String sellerNickname) { this.sellerNickname = sellerNickname; } // 设置卖家昵称
    public String getSellerAvatarUrl() { return sellerAvatarUrl; } // 获取卖家头像URL
    public void setSellerAvatarUrl(String sellerAvatarUrl) { this.sellerAvatarUrl = sellerAvatarUrl; } // 设置卖家头像URL
}
