package com.aicrm.module.channel.client.douyin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 抖音开放平台配置（对应 application.yml 的 aicrm.douyin）
 */
@Data
@Component
@ConfigurationProperties(prefix = "aicrm.douyin")
public class DouyinProperties {

    /** 抖音开放平台 API 地址 */
    private String baseUrl = "https://open.douyin.com";

    /** 调用超时（毫秒） */
    private int timeoutMs = 10000;
}
