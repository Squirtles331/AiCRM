package com.aicrm.module.ai.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 能力服务配置（对应 application.yml 的 aicrm.ai-service）
 */
@Data
@Component
@ConfigurationProperties(prefix = "aicrm.ai-service")
public class AiServiceProperties {

    /** Python AI 服务地址 */
    private String baseUrl = "http://localhost:8100";

    /** 同步调用超时（毫秒） */
    private int timeoutMs = 10000;
}
