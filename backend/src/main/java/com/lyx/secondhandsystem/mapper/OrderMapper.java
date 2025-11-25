package com.lyx.secondhandsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyx.secondhandsystem.entity.Order;
import org.apache.ibatis.annotations.Mapper;

// 订单映射器接口
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    // 统计商品的有效订单数量（状态为0或1） 
    @org.apache.ibatis.annotations.Select("SELECT COUNT(*) FROM orders WHERE product_id = #{productId} AND status IN (0,1)")
    // 用于在更新商品库存时避免并发问题
    int countActiveOrdersByProduct(Integer productId);
}
