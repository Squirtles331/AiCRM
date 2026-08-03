package com.aicrm.module.conversation.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.mapper.MessageMapper;
import com.aicrm.module.conversation.service.MessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 消息服务实现（3.3.2）
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public PageResult<Message> page(Long conversationId, String senderType, long page, long size) {
        if (conversationId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "conversationId 不能为空");
        }
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getConversationId, conversationId)
                .eq(StringUtils.hasText(senderType), Message::getSenderType, senderType)
                .orderByAsc(Message::getId);
        return PageResult.of(messageMapper.selectPage(new Page<>(page, size), wrapper));
    }
}
