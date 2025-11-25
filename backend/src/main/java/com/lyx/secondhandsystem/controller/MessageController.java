package com.lyx.secondhandsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.dto.MessageItemResponse;
import com.lyx.secondhandsystem.dto.ContactResponse;
import com.lyx.secondhandsystem.dto.MessageSendRequest;
import com.lyx.secondhandsystem.entity.Message;
import com.lyx.secondhandsystem.entity.User;
import com.lyx.secondhandsystem.mapper.MessageMapper;
import com.lyx.secondhandsystem.service.MessageService;
import com.lyx.secondhandsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;

// 消息控制器
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService; // 消息服务
    private final MessageMapper messageMapper; // 消息映射器    
    private final UserService userService; // 用户服务

    // 构造函数
    public MessageController(MessageService messageService, MessageMapper messageMapper, UserService userService) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.userService = userService;
    }

    // 获取消息对话
    @GetMapping
    public ApiResponse<Page<MessageItemResponse>> conversation(@RequestParam Integer contactId,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   HttpSession session) 
    {
        // 检查登录状态
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        if (size <= 0 || size > 50) throw new IllegalArgumentException("size范围为1-50");
        // 分页获取消息对话
        Page<Message> p = new Page<>(page, size);
        // 分页查询消息对话
        Page<Message> result = messageService.listConversation(p, (Integer) uid, contactId);
        User me = userService.getById((Integer) uid);
        User other = userService.getById(contactId);
        Page<MessageItemResponse> out = new Page<>(result.getCurrent(), result.getSize());
        out.setTotal(result.getTotal());
        out.setPages(result.getPages());
        List<MessageItemResponse> records = new java.util.ArrayList<>();
        for (Message m : result.getRecords()) {
            MessageItemResponse r = new MessageItemResponse();
            r.setMessageId(m.getMessageId());
            r.setSenderId(m.getSenderId());
            r.setReceiverId(m.getReceiverId());
            r.setContent(m.getContent());
            r.setIsRead(m.getIsRead());
            r.setCreatedAt(m.getCreatedAt());
            if (me != null && other != null) {
                if (m.getSenderId().equals(me.getUserId())) {
                    r.setSenderNickname(me.getNickname());
                    r.setSenderAvatarUrl(me.getAvatarUrl());
                    r.setReceiverNickname(other.getNickname());
                    r.setReceiverAvatarUrl(other.getAvatarUrl());
                } else {
                    r.setSenderNickname(other.getNickname());
                    r.setSenderAvatarUrl(other.getAvatarUrl());
                    r.setReceiverNickname(me.getNickname());
                    r.setReceiverAvatarUrl(me.getAvatarUrl());
                }
            }
            records.add(r);
        }
        out.setRecords(records);
        return ApiResponse.ok(out); // 返回分页消息对话（含昵称与头像）
    }
        
    // 发送消息
    @PostMapping
    public ApiResponse<Integer> send(@RequestBody @Valid MessageSendRequest req, HttpSession session) {
        // 检查登录状态
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        if (((Integer) uid).equals(req.getReceiverId())) throw new IllegalArgumentException("禁止给自己发送消息");
        // 创建消息实体
        Message m = new Message();
        m.setSenderId((Integer) uid);
        m.setReceiverId(req.getReceiverId());
        m.setContent(req.getContent());
        m.setIsRead(false);
        // 插入消息
        messageMapper.insert(m);
        // 返回消息ID
        return ApiResponse.ok(m.getMessageId());
    }

    // 获取消息联系人列表
    @GetMapping("/contacts")
    public ApiResponse<List<ContactResponse>> contacts(HttpSession session) {
        // 检查登录状态
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 查询用户参与的所有消息对话
        LambdaQueryWrapper<Message> qw = new LambdaQueryWrapper<>();
        qw.and(w -> w.eq(Message::getSenderId, (Integer) uid).or().eq(Message::getReceiverId, (Integer) uid))
          .orderByDesc(Message::getCreatedAt);
        List<Message> msgs = messageMapper.selectList(qw);
        // 按最后一条消息时间排序
        LinkedHashMap<Integer, LocalDateTime> map = new LinkedHashMap<>();
        // 遍历所有消息，按最后一条消息时间排序
        for (Message m : msgs) {
            // 找到消息的另一个参与方
            int other = m.getSenderId().equals((Integer) uid) ? m.getReceiverId() : m.getSenderId();
            if (!map.containsKey(other)) {
                // 如果另一个参与方不在映射中，添加到映射
                map.put(other, m.getCreatedAt());
            }
        }
        HashSet<Integer> ids = new HashSet<>(map.keySet());
        List<User> users = userService.listByIds(ids);
        Map<Integer, User> userMap = new java.util.HashMap<>();
        for (User u : users) userMap.put(u.getUserId(), u);
        List<ContactResponse> out = new ArrayList<>();
        for (var e : map.entrySet()) {
            ContactResponse c = new ContactResponse();
            c.setContactId(e.getKey());
            c.setLastAt(e.getValue());
            User u = userMap.get(e.getKey());
            if (u != null) {
                c.setContactNickname(u.getNickname());
                c.setContactAvatarUrl(u.getAvatarUrl());
            }
            out.add(c);
        }
        return ApiResponse.ok(out);
    }

    // 获取未读消息数量
    @GetMapping("/unread/count")
    public ApiResponse<Integer> unreadCount(HttpSession session) {
        // 检查登录状态
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 查询未读消息数量 
        LambdaQueryWrapper<Message> qw = new LambdaQueryWrapper<>();
        qw.eq(Message::getReceiverId, (Integer) uid).eq(Message::getIsRead, false);
        Long count = messageMapper.selectCount(qw);
        // 返回未读消息数量
        return ApiResponse.ok(count.intValue());
    }
    // 标记消息为已读
    @PostMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Integer id, HttpSession session) {
        // 检查登录状态
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 查询消息是否存在
        Message m = messageMapper.selectById(id);
        if (m == null) throw new IllegalArgumentException("消息不存在");
        if (!m.getReceiverId().equals((Integer) uid)) throw new IllegalArgumentException("无权标记他人消息");
        // 标记消息为已读   
        m.setIsRead(true);
        messageMapper.updateById(m);
        // 返回空响应
        return ApiResponse.ok(null);
    }
}
