package com.lyx.secondhandsystem.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.entity.Category;
import com.lyx.secondhandsystem.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

// 管理员分类控制器
@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService; // 分类服务

    // 构造函数注入分类服务 
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 获取分类列表
    @GetMapping
    public ApiResponse<Page<Category>> list(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        if (size <= 0 || size > 50) throw new IllegalArgumentException("size范围为1-50");
        Page<Category> p = new Page<>(page, size);
        // 调用分类服务获取分类列表
        return ApiResponse.ok(categoryService.list(p));
    }

    // 创建分类
    @PostMapping
    public ApiResponse<Integer> create(@RequestBody @Valid Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) throw new IllegalArgumentException("分类名称不能为空");
        // 调用分类服务创建分类
        categoryService.create(category);
        return ApiResponse.ok(category.getCategoryId());
    }

    // 删除分类
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        try {
            categoryService.deleteById(id);
            return ApiResponse.ok(null);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalArgumentException("分类存在关联商品，无法删除");
        }
    }
}
