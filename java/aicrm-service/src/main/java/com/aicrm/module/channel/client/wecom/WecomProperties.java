package com.aicrm.module.channel.client.wecom;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 企业微信配置（对应 application.yml 的 aicrm.wecom）
 */
@Data
@Component
@ConfigurationProperties(prefix = "aicrm.wecom")
public class WecomProperties {

    /** 企业微信 API 地址 */
    private String baseUrl = "https://qyapi.weixin.qq.com";

    /** 调用超时（毫秒） */
    private int timeoutMs = 10000;
}
