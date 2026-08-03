package com.aicrm.module.channel.service;

/**
 * 渠道异步同步服务：定时拉取渠道评论/私信，转为渠道事件落库并发布（3.1.7 全异步链路）
 * <p>
 * 链路：定时任务 → 拉取（各渠道 API）→ 幂等落库 channel_event → MQ 发布 → 消费者映射线索
 */
public interface ChannelSyncService {

    /**
     * 拉取渠道评论：跨租户遍历健康账号，新评论落库并发布事件
     *
     * @return 本次新增事件数（首次入库）
     */
    int syncComments();

    /**
     * 拉取渠道私信：跨租户遍历健康账号
     * <p>私信拉取接口待各渠道开放平台联调确认（当前为占位，仅记录跳过日志）
     *
     * @return 本次新增事件数（首次入库）
     */
    int syncDirectMessages();
}
