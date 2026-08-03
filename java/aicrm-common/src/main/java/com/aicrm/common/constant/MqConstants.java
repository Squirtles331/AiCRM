package com.aicrm.common.constant;

/**
 * RabbitMQ 事件总线常量
 * <p>
 * 约定：
 * - 交换器：aicrm.events (topic)
 * - 队列前缀：aicrm.queue.
 * - 死信队列：aicrm.queue.dlq（失败重试上限后投递）
 */
public final class MqConstants {

    private MqConstants() {
    }

    public static final String EVENT_EXCHANGE = "aicrm.events";
    public static final String DLQ = "aicrm.queue.dlq";
    public static final String DLQ_EXCHANGE = "aicrm.events.dlq";
    public static final String DLQ_ROUTING_KEY = "dlq.#";

    /** 渠道事件：新线索/新互动触发 */
    public static final String QUEUE_CHANNEL_EVENT = "aicrm.queue.channel.event";
    /** AI 分析完成回写 */
    public static final String QUEUE_AI_ANALYSIS = "aicrm.queue.ai.analysis";
    /** 工作流执行 */
    public static final String QUEUE_WORKFLOW = "aicrm.queue.workflow";

    /** 渠道事件路由键：channel.event.new 等 */
    public static final String ROUTING_CHANNEL_EVENT = "channel.event.*";
    /** AI 分析完成回写路由键 */
    public static final String ROUTING_AI_ANALYSIS = "ai.analysis.*";
    /** 工作流执行路由键 */
    public static final String ROUTING_WORKFLOW = "workflow.*";
    /** 知识库向量同步路由键（通知 Python 侧更新向量数据） */
    public static final String ROUTING_KNOWLEDGE_SYNC = "knowledge.sync";
}
