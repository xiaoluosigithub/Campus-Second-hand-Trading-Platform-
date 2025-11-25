package com.lyx.secondhandsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.ShoppingCart;

// 购物车服务接口
public interface ShoppingCartService {
    ShoppingCart create(ShoppingCart cart); // 创建购物车
    ShoppingCart getById(Integer id); // 根据ID获取购物车
    Page<ShoppingCart> listByUser(Page<ShoppingCart> page, Integer userId); // 分页获取用户购物车列表
    boolean update(ShoppingCart cart); // 更新购物车
    boolean deleteById(Integer id); // 根据ID删除购物车
}
