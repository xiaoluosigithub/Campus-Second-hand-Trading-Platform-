package com.lyx.secondhandsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.Product;
import com.lyx.secondhandsystem.mapper.ProductMapper;
import com.lyx.secondhandsystem.service.ProductService;
import org.springframework.stereotype.Service;

// 商品服务实现类
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper mapper; // 商品映射器

    // 构造函数注入商品映射器
    public ProductServiceImpl(ProductMapper mapper) {
        this.mapper = mapper;
    }

    // 创建商品
    @Override
    public Product create(Product product) {
        mapper.insert(product);
        return product;
    }

    // 根据ID获取商品
    @Override
    public Product getById(Integer id) {
        return mapper.selectById(id);
    }

    // 分页获取商品列表
    @Override
    public Page<Product> list(Page<Product> page, Integer sellerId, Integer categoryId, Integer status, String keyword, String sort, Boolean excludeActive) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<>(); // 构建Lambda查询包装器
        if (sellerId != null) qw.eq(Product::getSellerId, sellerId); // 筛选出该卖家的商品
        if (categoryId != null) qw.eq(Product::getCategoryId, categoryId); // 筛选出该分类的商品
        if (status != null) qw.eq(Product::getStatus, status);
        if (keyword != null && !keyword.trim().isEmpty()) qw.like(Product::getTitle, keyword.trim()); // 模糊查询商品标题
        if ("price_asc".equalsIgnoreCase(sort)) {
            qw.orderByAsc(Product::getPrice);
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            qw.orderByDesc(Product::getPrice);
        } else {
            qw.orderByDesc(Product::getCreatedAt);
        }
        if (excludeActive != null && excludeActive && sellerId == null) {
            qw.apply("NOT EXISTS (SELECT 1 FROM orders o WHERE o.product_id = products.product_id AND o.status IN (0,1))");
        }
        return mapper.selectPage(page, qw); // 返回分页商品列表
    }

    // 更新商品
    @Override
    public boolean update(Product product) {
        return mapper.updateById(product) > 0; // 返回更新是否成功
    }

    // 根据ID删除商品
    @Override
    public boolean deleteById(Integer id) {
        return mapper.deleteById(id) > 0; // 返回删除是否成功
    }
}
