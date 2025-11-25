package com.lyx.secondhandsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.ShoppingCart;
import com.lyx.secondhandsystem.mapper.ShoppingCartMapper;
import com.lyx.secondhandsystem.service.ShoppingCartService;
import org.springframework.stereotype.Service;

// 购物车服务实现类
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartMapper mapper; // 购物车映射器

    // 构造函数注入购物车映射器
    public ShoppingCartServiceImpl(ShoppingCartMapper mapper) {
        this.mapper = mapper;
    }

    // 创建购物车项
    @Override
    public ShoppingCart create(ShoppingCart cart) {
        mapper.insert(cart);
        return cart;
    }

    // 根据ID获取购物车项
    @Override
    public ShoppingCart getById(Integer id) {
        return mapper.selectById(id);
    }

    // 根据用户ID分页获取购物车项列表
    @Override
    public Page<ShoppingCart> listByUser(Page<ShoppingCart> page, Integer userId) {
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);
        return mapper.selectPage(page, qw);
    }

    // 更新购物车项
    @Override
    public boolean update(ShoppingCart cart) {
        return mapper.updateById(cart) > 0;
    }

    // 根据ID删除购物车项
    @Override
    public boolean deleteById(Integer id) {
        return mapper.deleteById(id) > 0;
    }
}
