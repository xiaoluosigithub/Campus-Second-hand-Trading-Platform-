package com.lyx.secondhandsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.Message;
import com.lyx.secondhandsystem.mapper.MessageMapper;
import com.lyx.secondhandsystem.service.MessageService;
import org.springframework.stereotype.Service;

// 消息服务实现类
@Service
public class MessageServiceImpl implements MessageService {
    private final MessageMapper mapper; // 消息映射器

    // 构造函数注入消息映射器
    public MessageServiceImpl(MessageMapper mapper) {
        this.mapper = mapper;
    }

    // 创建消息
    @Override
    public Message create(Message message) {
        mapper.insert(message);
        return message;
    }

    // 根据ID获取消息   
    @Override
    public Message getById(Integer id) {
        return mapper.selectById(id);
    }

    // 分页获取用户与联系人之间的消息对话
    @Override
    public Page<Message> listConversation(Page<Message> page, Integer meId, Integer contactId) {
        LambdaQueryWrapper<Message> qw = new LambdaQueryWrapper<>();
        qw.and(w -> w.eq(Message::getSenderId, meId).eq(Message::getReceiverId, contactId))
          .or(w -> w.eq(Message::getSenderId, contactId).eq(Message::getReceiverId, meId));
        qw.orderByDesc(Message::getCreatedAt);
        return mapper.selectPage(page, qw);
    }

    // 更新消息
    @Override
    public boolean update(Message message) {
        return mapper.updateById(message) > 0;
    }

    // 根据ID删除消息
    @Override
    public boolean deleteById(Integer id) {
        return mapper.deleteById(id) > 0;
    }
}
