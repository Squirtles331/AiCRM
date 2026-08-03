package com.aicrm.module.channel.client.video;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 视频号开放平台配置（对应 application.yml 的 aicrm.video-channel）
 */
@Data
@Component
@ConfigurationProperties(prefix = "aicrm.video-channel")
public class VideoChannelProperties {

    /** 微信开放平台 API 地址（stable_token 等通用接口） */
    private String baseUrl = "https://api.weixin.qq.com";

    /** 视频号开放平台业务 API 地址（评论/私信，以官方文档为准） */
    private String apiUrl = "https://api.weixin.qq.com";

    /** 调用超时（毫秒） */
    private int timeoutMs = 10000;
}
