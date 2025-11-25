package com.lyx.secondhandsystem.controller;

import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.entity.Order;
import com.lyx.secondhandsystem.mapper.OrderMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

// 支付控制器
@RestController
@RequestMapping("/api/pay")
public class PayController {
    private final OrderMapper orderMapper; // 订单映射器    

    public PayController(OrderMapper orderMapper) {
        this.orderMapper = orderMapper; // 初始化订单映射器
    }

    // 模拟支付
    @PostMapping("/mock")
    public ApiResponse<String> mock(@RequestParam Integer orderId, @RequestParam String method, HttpSession session) {
        // 检查用户登录状态
        Object uid = session.getAttribute("userId");
        // 检查用户是否登录
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 检查订单是否存在
        Order o = orderMapper.selectById(orderId);
        if (o == null) throw new IllegalArgumentException("订单不存在");
        if (!o.getBuyerId().equals((Integer) uid)) throw new IllegalArgumentException("无权操作他人订单");
        if (Integer.valueOf(3).equals(o.getStatus())) throw new IllegalArgumentException("订单已取消，无法支付");
        if (o.getPaymentMethod() != null && method != null && !o.getPaymentMethod().equalsIgnoreCase(method)) {
            throw new IllegalArgumentException("支付方式与订单不一致");
        }
        return ApiResponse.ok("支付成功"); // 返回支付成功响应
    }
}
