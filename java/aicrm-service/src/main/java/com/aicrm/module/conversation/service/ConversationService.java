package com.aicrm.module.conversation.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会话服务（3.3.1 生命周期管理）
 */
public interface ConversationService extends IService<Conversation> {

    /**
     * 创建会话（承接阵地：企微/WhatsApp/私信）
     *
     * @param conversation 会话（tenantId/leadId 必填）
     * @return 创建后的会话
     */
    Conversation createConversation(Conversation conversation);

    /**
     * 会话分页查询
     */
    PageResult<Conversation> page(String keyword, String status, Long assignedTo, long page, long size);

    /**
     * 会话详情
     */
    Conversation detail(Long id);

    /**
     * 关闭会话（status=closed，人工接待结束）
     */
    Conversation close(Long id);

    /**
     * 归档会话（status=archived，历史留存不可再承接）
     */
    Conversation archive(Long id);

    /**
     * 追加消息并更新会话最后消息时间
     *
     * @param message 消息（tenantId/conversationId 必填）
     * @return 保存后的消息
     */
    Message appendMessage(Message message);

    /**
     * 转人工：会话交给指定坐席处理（status=transferred, assignedTo=operatorId）
     *
     * @param conversationId 会话 ID
     * @param operatorId     坐席 ID
     * @return 更新后的会话
     */
    Conversation transferToHuman(Long conversationId, Long operatorId);
}
