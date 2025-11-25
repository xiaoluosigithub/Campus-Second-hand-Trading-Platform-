package com.lyx.secondhandsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.dto.OrderCreateRequest;
import com.lyx.secondhandsystem.dto.OrderShipRequest;
import com.lyx.secondhandsystem.entity.Order;
import com.lyx.secondhandsystem.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

// 订单控制器
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService; // 订单服务

    // 构造函数注入订单服务
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 创建订单
    @PostMapping
    public ApiResponse<Integer> create(@RequestBody @Valid OrderCreateRequest req, HttpSession session) {
        // 检查用户是否登录
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 创建订单
        Order o = orderService.placeSingleOrder((Integer) uid, req.getProductId(), req.getShippingName(), req.getShippingPhone(), req.getShippingAddress(), req.getPaymentMethod());
        return ApiResponse.ok(o.getOrderId()); // 返回订单ID
    }

    // 取消订单
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Integer id, HttpSession session) {
        // 检查用户是否登录 
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 取消订单 
        orderService.cancelOrder((Integer) uid, id);
        return ApiResponse.ok(null); // 返回空响应
    }

    // 发货
    @PatchMapping("/{id}/ship")
    public ApiResponse<Void> ship(@PathVariable Integer id, @RequestBody @Valid OrderShipRequest req, HttpSession session) {
        // 检查用户是否登录
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 发货
        orderService.shipOrder((Integer) uid, id, req.getTrackingNumber());
        return ApiResponse.ok(null); // 返回空响应
    }

    // 确认收货
    @PatchMapping("/{id}/confirm")
    public ApiResponse<Void> confirm(@PathVariable Integer id, HttpSession session) {
        // 检查用户是否登录
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 确认收货
        orderService.confirmOrder((Integer) uid, id);
        return ApiResponse.ok(null);
    }

    // 查询我购买的订单
    @GetMapping("/me/bought")
    public ApiResponse<Page<Order>> myBought(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             HttpSession session) 
    {
        // 检查用户是否登录
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        if (size <= 0 || size > 50) throw new IllegalArgumentException("size范围为1-50");
        // 查询我购买的订单
        Page<Order> p = new Page<>(page, size);
        Page<Order> result = orderService.listByBuyer(p, (Integer) uid);
        // 返回查询结果
        return ApiResponse.ok(result);
    }

    // 查询我出售的订单
    @GetMapping("/me/sold")
    public ApiResponse<Page<Order>> mySold(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpSession session) 
    {
        // 检查用户是否登录
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        if (size <= 0 || size > 50) throw new IllegalArgumentException("size范围为1-50");
        // 查询我出售的订单
        Page<Order> p = new Page<>(page, size);
        Page<Order> result = orderService.listBySeller(p, (Integer) uid);
        // 返回查询结果 
        return ApiResponse.ok(result);
    }

    // 订单前置校验：校验商品是否在售且无进行中订单
    @GetMapping("/precheck")
    public ApiResponse<Boolean> precheck(@RequestParam Integer productId) {
        if (productId == null || productId <= 0) throw new IllegalArgumentException("productId必须大于0");
        return ApiResponse.ok(orderService.precheckProduct(productId));
    }
}
