package com.lyx.secondhandsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyx.secondhandsystem.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {} // 分类Mapper接口
