package com.lyx.secondhandsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.User;
import com.lyx.secondhandsystem.mapper.UserMapper;
import com.lyx.secondhandsystem.service.UserService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collection;

// 用户服务实现类
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper mapper; // 用户映射器

    // 构造函数注入用户映射器
    public UserServiceImpl(UserMapper mapper) {
        this.mapper = mapper;
    }

    // 创建用户
    @Override
    public User create(User user) {
        mapper.insert(user);
        return user;
    }

    // 根据ID获取用户
    @Override
    public User getById(Integer id) {
        return mapper.selectById(id);
    }

    // 根据用户名获取用户
    @Override
    public User getByUsername(String username) {
        return mapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
    
    // 分页获取用户列表（根据用户名或昵称搜索）
    @Override
    public Page<User> list(Page<User> page, String keyword) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(User::getUsername, keyword).or().like(User::getNickname, keyword);
        }
        return mapper.selectPage(page, qw);
    }

    // 更新用户
    @Override
    public boolean update(User user) {
        return mapper.updateById(user) > 0;
    }
    
    // 根据ID删除用户
    @Override
    public boolean deleteById(Integer id) {
        return mapper.deleteById(id) > 0;
    }

    @Override
    public List<User> listByIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) return java.util.Collections.emptyList();
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.in(User::getUserId, ids);
        return mapper.selectList(qw);
    }
}
