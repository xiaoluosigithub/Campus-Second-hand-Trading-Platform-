package com.lyx.secondhandsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyx.secondhandsystem.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {} // 用户映射器
