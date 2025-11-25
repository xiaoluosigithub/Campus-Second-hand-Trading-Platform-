package com.lyx.secondhandsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

// 商品创建请求DTO
public class ProductCreateRequest {
    @NotBlank
    private String title; // 商品标题
    private String description; // 商品描述
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price; // 商品价格
    @NotNull
    private Integer categoryId; // 商品分类ID
    private List<String> images; // 商品图片URL列表

    public String getTitle() { return title; } // 获取商品标题
    public void setTitle(String title) { this.title = title; } // 设置商品标题
    public String getDescription() { return description; } // 获取商品描述
    public void setDescription(String description) { this.description = description; } // 设置商品描述
    public BigDecimal getPrice() { return price; } // 获取商品价格
    public void setPrice(BigDecimal price) { this.price = price; } // 设置商品价格
    public Integer getCategoryId() { return categoryId; } // 获取商品分类ID
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; } // 设置商品分类ID
    public List<String> getImages() { return images; } // 获取商品图片URL列表   
    public void setImages(List<String> images) { this.images = images; } // 设置商品图片URL列表
}
