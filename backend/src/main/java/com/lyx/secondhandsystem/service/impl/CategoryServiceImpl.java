package com.lyx.secondhandsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.Category;
import com.lyx.secondhandsystem.mapper.CategoryMapper;
import com.lyx.secondhandsystem.service.CategoryService;
import org.springframework.stereotype.Service;

// 分类服务实现类
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper mapper; // 分类映射器

    // 构造函数注入分类映射器
    public CategoryServiceImpl(CategoryMapper mapper) {
        this.mapper = mapper;
    }

    // 创建分类
    @Override
    public Category create(Category category) {
        mapper.insert(category);
        return category;
    }

    // 根据ID获取分类
    @Override
    public Category getById(Integer id) {
        return mapper.selectById(id);
    }

    // 分页获取分类列表 
    @Override
    public Page<Category> list(Page<Category> page) {
        return mapper.selectPage(page, null);
    }

    // 更新分类
    @Override
    public boolean update(Category category) {
        return mapper.updateById(category) > 0;
    }

    // 根据ID删除分类
    @Override
    public boolean deleteById(Integer id) {
        return mapper.deleteById(id) > 0;
    }
}
