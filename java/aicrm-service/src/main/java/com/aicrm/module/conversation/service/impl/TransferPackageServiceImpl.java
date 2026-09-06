package com.aicrm.module.conversation.service.impl;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.conversation.dto.TransferPackage;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.mapper.MessageMapper;
import com.aicrm.module.conversation.service.ConversationService;
import com.aicrm.module.conversation.service.TransferPackageService;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.mapper.LeadMapper;
import com.aicrm.module.lead.service.LeadAssignService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 转人工交接包服务实现：基础会话信息、人工维护的线索意向和最近消息。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferPackageServiceImpl implements TransferPackageService {

    private static final int RECENT_MESSAGE_LIMIT = 20;

    private final ConversationService conversationService;
    private final MessageMapper messageMapper;
    private final LeadMapper leadMapper;
    private final LeadAssignService leadAssignService;

    @Override
    public TransferPackage build(Long conversationId, Long operatorId) {
        Conversation conversation = requireConversation(conversationId);
        Long targetOperator = operatorId == null ? resolveOperator(conversation) : operatorId;
        Conversation transferred = conversationService.transferToHuman(conversationId, targetOperator);
        TransferPackage transferPackage = assemble(transferred);
        log.info("转人工交接包已生成 conversationId={}, operatorId={}", conversationId, targetOperator);
        return transferPackage;
    }

    @Override
    public TransferPackage get(Long conversationId) {
        return assemble(requireConversation(conversationId));
    }

    private Conversation requireConversation(Long conversationId) {
        Conversation conversation = conversationService.getById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ResultCode.CONVERSATION_NOT_FOUND);
        }
        return conversation;
    }

    /** 未指定坐席时解析接手人：线索负责人 -> 分配引擎自动分配。 */
    private Long resolveOperator(Conversation conversation) {
        if (conversation.getLeadId() != null) {
            Lead lead = leadMapper.selectById(conversation.getLeadId());
            if (lead != null && lead.getOwnerId() != null) {
                return lead.getOwnerId();
            }
            if (lead != null) {
                leadAssignService.assignLead(lead.getId());
                lead = leadMapper.selectById(lead.getId());
                if (lead != null && lead.getOwnerId() != null) {
                    return lead.getOwnerId();
                }
            }
        }
        throw new BusinessException(ResultCode.BAD_REQUEST, "暂无可接手的在线坐席，请手动指派");
    }

    private TransferPackage assemble(Conversation conversation) {
        TransferPackage transferPackage = new TransferPackage();
        transferPackage.setConversationId(conversation.getId());
        transferPackage.setTenantId(conversation.getTenantId());
        transferPackage.setLeadId(conversation.getLeadId());
        transferPackage.setConversationType(conversation.getConversationType());
        transferPackage.setStatus(conversation.getStatus());
        transferPackage.setOperatorId(conversation.getAssignedTo());

        if (conversation.getLeadId() != null) {
            Lead lead = leadMapper.selectById(conversation.getLeadId());
            transferPackage.setIntent(lead == null ? null : lead.getIntent());
        }

        List<Message> recentMessages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getTenantId, conversation.getTenantId())
                .eq(Message::getConversationId, conversation.getId())
                .orderByDesc(Message::getId)
                .last("LIMIT " + RECENT_MESSAGE_LIMIT));
        Collections.reverse(recentMessages);
        transferPackage.setRecentMessages(recentMessages);
        return transferPackage;
    }
}
