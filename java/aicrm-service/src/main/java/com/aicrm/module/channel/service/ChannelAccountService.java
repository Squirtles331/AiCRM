package com.aicrm.module.channel.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.channel.entity.ChannelAccount;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 渠道账号服务：账号绑定、授权配置、健康状态监控、token 刷新
 */
public interface ChannelAccountService extends IService<ChannelAccount> {

    /**
     * 账号分页查询
     */
    PageResult<ChannelAccount> page(String keyword, Long channelId, Integer healthStatus, long page, long size);

    /**
     * 绑定渠道账号
     */
    ChannelAccount create(ChannelAccount account);

    /**
     * 更新账号信息/授权配置
     */
    ChannelAccount update(ChannelAccount account);

    /**
     * 删除账号
     */
    void delete(Long id);

    /**
     * 更新健康状态（状态监控回调）
     */
    void updateHealthStatus(Long id, Integer healthStatus, Integer riskLevel);

    /**
     * 刷新单个账号 token（HTTP 请求场景）
     */
    boolean refreshToken(Long id);

    /**
     * 批量刷新过期 token（定时任务场景，跨租户），返回刷新成功数量
     */
    int refreshExpiredTokens();
}
