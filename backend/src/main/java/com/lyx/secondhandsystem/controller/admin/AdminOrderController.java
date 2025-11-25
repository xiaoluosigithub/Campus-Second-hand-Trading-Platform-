package com.lyx.secondhandsystem.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.entity.Order;
import com.lyx.secondhandsystem.mapper.OrderMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// 管理员订单控制器
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final OrderMapper orderMapper; // 订单映射器

    public AdminOrderController(OrderMapper orderMapper) {
        this.orderMapper = orderMapper; // 初始化订单映射器
    }

    // 分页获取订单列表（可选筛选+排序）
    @GetMapping
    public ApiResponse<Page<Order>> list(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) Integer buyerId,
                                         @RequestParam(required = false) Integer sellerId,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) String date) 
    {
        if (size <= 0 || size > 50) throw new IllegalArgumentException("size范围为1-50");
        Page<Order> p = new Page<>(page, size); // 创建分页对象
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>(); // 构建Lambda查询包装器
        if (buyerId != null) qw.eq(Order::getBuyerId, buyerId); // 筛选出该买家的订单
        if (sellerId != null) qw.eq(Order::getSellerId, sellerId); // 筛选出该卖家的订单
        if (status != null) qw.eq(Order::getStatus, status); // 筛选出该状态的订单
        if (date != null && !date.isBlank()) {
            // 筛选出该日期的订单（包含该日期的所有时间）
            LocalDate d = LocalDate.parse(date);
            LocalDateTime start = d.atStartOfDay();
            LocalDateTime end = d.atTime(LocalTime.MAX);
            qw.between(Order::getCreatedAt, start, end);
        }
        // 返回分页订单列表
        return ApiResponse.ok(orderMapper.selectPage(p, qw));
    }

    // 获取订单详情
    @GetMapping("/{id}")
    public ApiResponse<Order> detail(@PathVariable Integer id) {
        Order o = orderMapper.selectById(id); // 根据ID查询订单
        if (o == null) throw new IllegalArgumentException("订单不存在"); // 如果订单不存在，抛出异常
        return ApiResponse.ok(o); // 返回订单详情
    }
}
