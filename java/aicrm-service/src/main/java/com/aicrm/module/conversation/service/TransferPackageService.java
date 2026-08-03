package com.aicrm.module.conversation.service;

import com.aicrm.module.conversation.dto.TransferPackage;

/**
 * 转人工交接包服务
 */
public interface TransferPackageService {

    /**
     * 转人工并生成交接包（摘要/意向/缺失字段/推荐回复）
     *
     * @param conversationId 会话 ID
     * @param operatorId     接手坐席 ID
     * @return 交接包
     */
    TransferPackage build(Long conversationId, Long operatorId);

    /**
     * 查询交接包（坐席接手后查看）
     *
     * @param conversationId 会话 ID
     * @return 交接包
     */
    TransferPackage get(Long conversationId);
}
