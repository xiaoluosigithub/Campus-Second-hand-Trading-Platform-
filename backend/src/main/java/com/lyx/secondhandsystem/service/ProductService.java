package com.lyx.secondhandsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.Product;

// 商品服务接口
public interface ProductService {
    Product create(Product product); // 创建商品
    Product getById(Integer id); // 根据ID获取商品
    // 分页获取商品列表（可选筛选+排序）
    Page<Product> list(Page<Product> page, Integer sellerId, Integer categoryId, Integer status, String keyword, String sort, Boolean excludeActive);
    boolean update(Product product); // 更新商品
    boolean deleteById(Integer id); // 根据ID删除商品
}
