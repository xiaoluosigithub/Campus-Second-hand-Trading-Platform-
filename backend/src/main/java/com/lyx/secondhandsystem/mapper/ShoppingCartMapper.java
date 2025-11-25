package com.lyx.secondhandsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyx.secondhandsystem.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {} // 购物车Mapper接口
