package com.lyx.secondhandsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.Order;

// 订单服务接口
public interface OrderService {
    Order create(Order order); // 创建订单
    Order getById(Integer id); // 根据ID获取订单
    Page<Order> listByBuyer(Page<Order> page, Integer buyerId); // 分页获取买家订单列表
    Page<Order> listBySeller(Page<Order> page, Integer sellerId); // 分页获取卖家订单列表
    boolean update(Order order); // 更新订单
    boolean deleteById(Integer id); // 根据ID删除订单
    // 下单
    Order placeSingleOrder(Integer buyerId, Integer productId, String shippingName, String shippingPhone, String shippingAddress, String paymentMethod);
    // 取消订单 
    boolean cancelOrder(Integer buyerId, Integer orderId);
    // 发货
    boolean shipOrder(Integer sellerId, Integer orderId, String trackingNumber);
    // 确认收货
    boolean confirmOrder(Integer buyerId, Integer orderId);
    // 前置校验：商品是否在售且无进行中订单
    boolean precheckProduct(Integer productId);
}
