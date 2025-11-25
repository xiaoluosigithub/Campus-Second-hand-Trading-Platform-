package com.lyx.secondhandsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.entity.Category;
import com.lyx.secondhandsystem.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// 分类控制器
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService; // 分类服务

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 获取分类树
    @GetMapping("/tree")
    public ApiResponse<List<Category>> tree() {
        // 分页获取所有分类
        Page<Category> p = new Page<>(1, Integer.MAX_VALUE);
        // 转换为树结构
        return ApiResponse.ok(categoryService.list(p).getRecords());
    }

    // 获取分类路径
    @GetMapping("/{id}/path")
    public ApiResponse<List<Category>> path(@PathVariable Integer id) {
        // 根据ID获取分类
        Category c = categoryService.getById(id);
        if (c == null) throw new IllegalArgumentException("分类不存在");
        // 递归获取分类路径
        List<Category> path = new ArrayList<>();
        path.add(c); // 添加当前分类
        return ApiResponse.ok(path); // 返回分类路径
    }
}
