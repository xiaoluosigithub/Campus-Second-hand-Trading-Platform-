package com.lyx.secondhandsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyx.secondhandsystem.entity.Product;
import org.apache.ibatis.annotations.Mapper;

// 商品映射器接口
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 根据ID查询商品并加锁
    @org.apache.ibatis.annotations.Select("SELECT * FROM products WHERE product_id = #{id} FOR UPDATE")
    // 用于在更新商品信息时避免并发问题
    Product selectForUpdate(Integer id);
}
