package com.aicrm.module.ai.notify;

import com.aicrm.common.constant.MqConstants;
import com.aicrm.common.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * 知识库向量同步通知（3.4.3）
 * <p>产品/竞品/话术新增、修改、删除后，发 MQ 通知 Python 侧更新向量数据。
 * MQ 不可用时记录日志降级（Python 侧可在下次全量同步时兜底）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeSyncNotifier {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 通知知识库向量同步
     *
     * @param entityType 实体类型：product/competitor/speech
     * @param entityId   实体 ID
     * @param action     动作：create/update/delete
     * @param content    向量化文本（组合标题与正文）
     */
    public void notify(String entityType, Long entityId, String action, String content) {
        KnowledgeSyncMessage message = new KnowledgeSyncMessage(
                TenantContext.getTenantId(), entityType, entityId, action, content, Instant.now().toString());
        try {
            rabbitTemplate.convertAndSend(MqConstants.EVENT_EXCHANGE, MqConstants.ROUTING_KNOWLEDGE_SYNC, message);
            log.info("知识向量同步通知已发送 entityType={}, entityId={}, action={}", entityType, entityId, action);
        } catch (Exception e) {
            log.warn("知识向量同步通知发送失败（MQ 不可用）entityType={}, entityId={}, action={}, err={}",
                    entityType, entityId, action, e.getMessage());
        }
    }

    /** 知识同步消息体 */
    public record KnowledgeSyncMessage(Long tenantId, String entityType, Long entityId,
                                       String action, String content, String timestamp) {
    }
}
