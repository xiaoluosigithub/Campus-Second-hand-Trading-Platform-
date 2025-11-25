package com.lyx.secondhandsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.Message;

// 消息服务接口
public interface MessageService {
    Message create(Message message); // 创建消息
    Message getById(Integer id); // 根据ID获取消息
    Page<Message> listConversation(Page<Message> page, Integer meId, Integer contactId); // 分页获取消息列表（单聊）
    boolean update(Message message); // 更新消息
    boolean deleteById(Integer id); // 根据ID删除消息
}
