package com.aicrm.module.conversation.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.mapper.ConversationMapper;
import com.aicrm.module.conversation.mapper.MessageMapper;
import com.aicrm.module.conversation.service.ConversationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 会话服务实现（3.3.1 生命周期管理）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
        implements ConversationService {

    private final MessageMapper messageMapper;

    @Override
    public Conversation createConversation(Conversation conversation) {
        if (conversation.getTenantId() == null || conversation.getLeadId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "tenantId 与 leadId 不能为空");
        }
        if (conversation.getStatus() == null) {
            conversation.setStatus("active");
        }
        this.save(conversation);
        return conversation;
    }

    @Override
    public PageResult<Conversation> page(String keyword, String status, Long assignedTo, long page, long size) {
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.hasText(keyword), w -> w
                        .like(Conversation::getConversationType, keyword)
                        .or().like(Conversation::getId, keyword))
                .eq(StringUtils.hasText(status), Conversation::getStatus, status)
                .eq(assignedTo != null, Conversation::getAssignedTo, assignedTo)
                .orderByDesc(Conversation::getLastMessageAt);
        return PageResult.of(this.page(new Page<>(page, size), wrapper));
    }

    @Override
    public Conversation detail(Long id) {
        Conversation conversation = this.getById(id);
        if (conversation == null) {
            throw new BusinessException(ResultCode.CONVERSATION_NOT_FOUND);
        }
        return conversation;
    }

    @Override
    public Conversation close(Long id) {
        Conversation conversation = detail(id);
        if ("archived".equals(conversation.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已归档会话不能关闭");
        }
        conversation.setStatus("closed");
        this.updateById(conversation);
        log.info("会话关闭 conversationId={}", id);
        return conversation;
    }

    @Override
    public Conversation archive(Long id) {
        Conversation conversation = detail(id);
        conversation.setStatus("archived");
        this.updateById(conversation);
        log.info("会话归档 conversationId={}", id);
        return conversation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message appendMessage(Message message) {
        if (message.getTenantId() == null || message.getConversationId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "tenantId 与 conversationId 不能为空");
        }
        Conversation conversation = this.getById(message.getConversationId());
        if (conversation == null) {
            throw new BusinessException(ResultCode.CONVERSATION_NOT_FOUND);
        }
        if (message.getMsgType() == null) {
            message.setMsgType("text");
        }
        if (message.getAiGenerated() == null) {
            message.setAiGenerated(false);
        }
        messageMapper.insert(message);

        // 更新会话最后消息时间
        conversation.setLastMessageAt(LocalDateTime.now());
        this.updateById(conversation);
        return message;
    }

    @Override
    public Conversation transferToHuman(Long conversationId, Long operatorId) {
        Conversation conversation = this.getById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ResultCode.CONVERSATION_NOT_FOUND);
        }
        if (operatorId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "operatorId 不能为空");
        }
        conversation.setAssignedTo(operatorId);
        conversation.setStatus("transferred");
        this.updateById(conversation);
        log.info("会话转人工 conversationId={}, operatorId={}", conversationId, operatorId);
        return conversation;
    }
}
