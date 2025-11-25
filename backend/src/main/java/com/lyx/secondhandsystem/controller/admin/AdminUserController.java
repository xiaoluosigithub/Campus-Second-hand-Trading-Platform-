package com.lyx.secondhandsystem.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.entity.User;
import com.lyx.secondhandsystem.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

// 管理员用户控制器
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserMapper userMapper;

    public AdminUserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 获取用户列表
    @GetMapping
    public ApiResponse<Page<User>> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String keyword) {
        if (size <= 0 || size > 50) throw new IllegalArgumentException("size范围为1-50");
        Page<User> p = new Page<>(page, size); // 创建分页对象
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>(); // 构建Lambda查询包装器
        if (keyword != null && !keyword.trim().isEmpty()) qw.like(User::getUsername, keyword.trim()); // 筛选出包含关键词的用户
        return ApiResponse.ok(userMapper.selectPage(p, qw)); // 返回分页用户列表
    }

    // 修改用户角色
    @PatchMapping("/{id}/role")
    public ApiResponse<Void> changeRole(@PathVariable Integer id, @RequestParam String role) {
        if (!("user".equals(role) || "admin".equals(role))) throw new IllegalArgumentException("角色仅支持user/admin");
        User u = userMapper.selectById(id); // 根据ID查询用户
        if (u == null) throw new IllegalArgumentException("用户不存在");
        u.setRole(role); // 设置用户角色
        userMapper.updateById(u); // 更新用户角色
        return ApiResponse.ok(null); // 返回成功响应
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        User u = userMapper.selectById(id);
        if (u == null) throw new IllegalArgumentException("用户不存在");
        try {
            userMapper.deleteById(id); // 删除用户
        } catch (Exception e) {
            throw new IllegalArgumentException("用户存在关联数据，无法删除"); // 捕获异常，提示用户存在关联数据
        }
        return ApiResponse.ok(null); // 返回成功响应
    }
}
