package com.aicrm.module.channel.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.channel.entity.ChannelQrCode;

/**
 * 渠道活码服务：活码生成、扫码统计、来源标记、引流归因
 */
public interface ChannelQrCodeService {

    PageResult<ChannelQrCode> page(String keyword, Long channelAccountId, long page, long size);

    ChannelQrCode create(ChannelQrCode qrCode);

    ChannelQrCode update(ChannelQrCode qrCode);

    void delete(Long id);

    void updateStatus(Long id, Integer status);

    /**
     * 扫码回调：次数自增 + 记录 qr 事件（携带 scene 来源标记），返回跳转落地地址
     */
    String recordScan(Long id, String scene);
}
