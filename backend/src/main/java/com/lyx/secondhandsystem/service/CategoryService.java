package com.lyx.secondhandsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.Category;

// 分类服务接口
public interface CategoryService {
    Category create(Category category); // 创建分类
    Category getById(Integer id); // 根据ID获取分类
    Page<Category> list(Page<Category> page); // 分页获取分类列表
    boolean update(Category category); // 更新分类
    boolean deleteById(Integer id); // 根据ID删除分类
}
