package com.aicrm.module.channel.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 风控频控配置（对应 application.yml 的 aicrm.risk）
 */
@Data
@Component
@ConfigurationProperties(prefix = "aicrm.risk")
public class RiskProperties {

    /** 同一联系人单渠道每日消息上限 */
    private int dailyMessageLimit = 20;

    /** 评论回复频控：每账号每小时条数 */
    private int commentPerHourLimit = 30;
}
