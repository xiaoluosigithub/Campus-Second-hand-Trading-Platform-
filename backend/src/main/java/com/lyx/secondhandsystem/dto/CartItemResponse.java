package com.lyx.secondhandsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItemResponse {
    private Integer productId;
    private String title;
    private BigDecimal price;
    private Integer status;
    private String image;
    private LocalDateTime addedAt;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
