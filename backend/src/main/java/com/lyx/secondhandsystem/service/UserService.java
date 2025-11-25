package com.lyx.secondhandsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.User;

// 用户服务接口
public interface UserService {
    User create(User user); // 创建用户
    User getById(Integer id); // 根据ID获取用户
    User getByUsername(String username); // 根据用户名获取用户
    Page<User> list(Page<User> page, String keyword); // 分页获取用户列表（根据用户名或昵称搜索）
    boolean update(User user); // 更新用户
    boolean deleteById(Integer id); // 根据ID删除用户
    java.util.List<User> listByIds(java.util.Collection<Integer> ids); // 批量根据ID获取用户
}
