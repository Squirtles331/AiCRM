package com.aicrm.module.conversation.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.conversation.entity.Message;

/**
 * 消息服务（3.3.2 存储与查询）
 */
public interface MessageService {

    /**
     * 会话消息分页查询（历史消息漫游，按时间正序返回）
     *
     * @param conversationId 会话 ID（必填）
     * @param senderType     发送方类型（customer/human/system，可空）
     */
    PageResult<Message> page(Long conversationId, String senderType, long page, long size);
}
