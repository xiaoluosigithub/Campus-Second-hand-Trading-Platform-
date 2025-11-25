package com.lyx.secondhandsystem.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.entity.Order;
import com.lyx.secondhandsystem.mapper.OrderMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

// 管理员统计接口
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {
    private final OrderMapper orderMapper; // 订单映射器

    public AdminStatsController(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    // 获取指定日期范围内的订单统计信息
    @GetMapping("/orders")
    public ApiResponse<Map<String, Object>> ordersByDate(@RequestParam String date) {
        LocalDate d = LocalDate.parse(date); // 解析日期字符串为 LocalDate 对象
        LocalDateTime start = d.atStartOfDay(); // 开始时间为该日期的 00:00:00
        LocalDateTime end = d.atTime(LocalTime.MAX); // 结束时间为该日期的 23:59:59
        return ApiResponse.ok(summaryBetween(start, end)); // 返回指定日期范围内的订单统计信息
    }

    // 获取指定日期范围内的订单统计信息
    @GetMapping("/orders-range")
    public ApiResponse<Map<String, Object>> ordersRange(@RequestParam String start,
                                                        @RequestParam String end) {
        LocalDateTime s = LocalDate.parse(start).atStartOfDay(); // 开始时间为指定日期的 00:00:00
        LocalDateTime e = LocalDate.parse(end).atTime(LocalTime.MAX); // 结束时间为指定日期的 23:59:59
        return ApiResponse.ok(summaryBetween(s, e)); // 返回指定日期范围内的订单统计信息    
    }

    // 统计指定日期范围内的订单信息
    private Map<String, Object> summaryBetween(LocalDateTime start, LocalDateTime end) {
        // 构建查询条件，筛选出在指定日期范围内的订单
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();
        qw.between(Order::getCreatedAt, start, end);
        int total = orderMapper.selectCount(qw).intValue(); // 统计订单总数
        BigDecimal amount = BigDecimal.ZERO; // 初始化订单金额为 0
        // 遍历订单列表，累加订单金额
        for (Order o : orderMapper.selectList(qw)) {
            if (o.getTransactionPrice() != null) {
                amount = amount.add(o.getTransactionPrice());
            }
        }
        BigDecimal amountCompleted = BigDecimal.ZERO;
        LambdaQueryWrapper<Order> completedQw = new LambdaQueryWrapper<>();
        completedQw.between(Order::getCreatedAt, start, end).eq(Order::getStatus, 2);
        for (Order o : orderMapper.selectList(completedQw)) {
            if (o.getTransactionPrice() != null) {
                amountCompleted = amountCompleted.add(o.getTransactionPrice());
            }
        }
        // 统计每个订单状态的数量
        Map<String, Integer> status = new HashMap<>();
        status.put("pending", countByStatus(start, end, 0));
        status.put("shipped", countByStatus(start, end, 1));
        status.put("completed", countByStatus(start, end, 2));
        status.put("canceled", countByStatus(start, end, 3));
        // 构建输出结果 ，包含订单总数、订单金额和每个订单状态的数量
        Map<String, Object> out = new HashMap<>();
        out.put("total", total);
        out.put("amount", amount);
        out.put("amountCompleted", amountCompleted);
        out.put("status", status);
        // 返回输出结果
        return out;
    }
    // 统计指定日期范围内指定状态的订单数量
    private int countByStatus(LocalDateTime start, LocalDateTime end, int st) {
        // 构建查询条件，筛选出在指定日期范围内且状态为 st 的订单
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();
        qw.between(Order::getCreatedAt, start, end).eq(Order::getStatus, st);
        return orderMapper.selectCount(qw).intValue(); // 返回订单数量
    }
}
