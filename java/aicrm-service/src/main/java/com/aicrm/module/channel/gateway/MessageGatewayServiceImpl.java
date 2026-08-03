package com.aicrm.module.channel.gateway;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.channel.client.douyin.DouyinApiClient;
import com.aicrm.module.channel.client.video.VideoChannelApiClient;
import com.aicrm.module.channel.client.wecom.WecomApiClient;
import com.aicrm.module.channel.entity.Channel;
import com.aicrm.module.channel.entity.ChannelAccount;
import com.aicrm.module.channel.entity.ChannelEvent;
import com.aicrm.module.channel.mapper.ChannelAccountMapper;
import com.aicrm.module.channel.mapper.ChannelMapper;
import com.aicrm.module.channel.service.ChannelEventService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 统一消息网关实现：三层防护 + 按渠道分发
 * <p>
 * 防护顺序：敏感词拦截 → 账号风控等级检查 → 频率限流（账号小时限流 / 联系人日限流，Redis 计数）。
 * Redis 不可用时降级放行（不影响主链路），仅记录告警日志。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageGatewayServiceImpl implements MessageGatewayService {

    /** 风控规则：账号风险等级达到该值后禁止发送（平台风控规则适配） */
    private static final int RISK_LEVEL_BLOCK = 2;

    private final ChannelAccountMapper channelAccountMapper;
    private final ChannelMapper channelMapper;
    private final ChannelEventService channelEventService;
    private final SensitiveWordService sensitiveWordService;
    private final RiskProperties riskProperties;
    private final StringRedisTemplate redisTemplate;
    private final DouyinApiClient douyinApiClient;
    private final WecomApiClient wecomApiClient;
    private final VideoChannelApiClient videoChannelApiClient;
    private final ObjectMapper objectMapper;

    @Override
    public void sendText(Long channelAccountId, String toExternalUserId, String content) {
        if (!StringUtils.hasText(toExternalUserId) || !StringUtils.hasText(content)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "目标用户与内容不能为空");
        }
        ChannelAccount account = requireAccount(channelAccountId);

        // 1. 敏感词拦截
        String hit = sensitiveWordService.match(content);
        if (hit != null) {
            log.warn("消息含敏感词被拦截 accountId={}, word={}", channelAccountId, hit);
            throw new BusinessException(ResultCode.RISK_BLOCKED, "内容含敏感词: " + hit);
        }

        // 2. 账号风控等级检查（平台风控规则适配）
        if (account.getRiskLevel() != null && account.getRiskLevel() >= RISK_LEVEL_BLOCK) {
            log.warn("账号风控等级过高被拦截 accountId={}, riskLevel={}", channelAccountId, account.getRiskLevel());
            throw new BusinessException(ResultCode.RISK_BLOCKED, "账号存在风控风险，已暂停发送");
        }

        String channelCode = resolveChannelCode(account.getChannelId());

        // 3. 频率限流（Redis 计数，不可用时降级放行）
        checkRateLimit(account, toExternalUserId);

        // 4. 按渠道分发发送
        dispatch(account, channelCode, toExternalUserId, content);

        // 5. 记录发送事件（eventType=send，供审计/统计）
        recordSendEvent(account, toExternalUserId, content);
        log.info("消息发送成功 accountId={}, to={}, channel={}", channelAccountId, toExternalUserId, channelCode);
    }

    private void dispatch(ChannelAccount account, String channelCode, String toExternalUserId, String content) {
        String token = accessToken(account);
        switch (channelCode == null ? "" : channelCode) {
            case "douyin" -> douyinApiClient.sendDirectMessage(token, toExternalUserId, content);
            case "video_channel" -> videoChannelApiClient.sendDirectMessage(token, toExternalUserId, content);
            case "wecom" -> wecomApiClient.sendTextMessage(token, agentId(account), toExternalUserId, content);
            default -> throw new BusinessException(ResultCode.CHANNEL_API_ERROR,
                    "暂不支持该渠道发送: " + channelCode);
        }
    }

    // ---------- 限流 ----------

    private void checkRateLimit(ChannelAccount account, String toExternalUserId) {
        LocalDateTime now = LocalDateTime.now();
        // 账号小时限流：评论回复频控
        String hourKey = "risk:cmt:" + account.getId() + ":" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        long hourCount = increment(hourKey, Duration.ofHours(1));
        if (hourCount > riskProperties.getCommentPerHourLimit()) {
            throw new BusinessException(ResultCode.RISK_BLOCKED, "该账号评论回复过于频繁，请稍后再试");
        }
        // 联系人日限流
        String dayKey = "risk:msg:" + account.getTenantId() + ":" + toExternalUserId + ":"
                + now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long dayCount = increment(dayKey, Duration.ofDays(1));
        if (dayCount > riskProperties.getDailyMessageLimit()) {
            throw new BusinessException(ResultCode.RISK_BLOCKED, "今日与该用户沟通已达上限");
        }
    }

    /** Redis 自增计数；不可用时降级放行 */
    private long increment(String key, Duration ttl) {
        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, ttl);
            }
            return count == null ? 0 : count;
        } catch (Exception e) {
            log.warn("限流计数不可用，降级放行 key={}, err={}", key, e.getMessage());
            return 0;
        }
    }

    // ---------- 辅助 ----------

    private ChannelAccount requireAccount(Long id) {
        ChannelAccount account = channelAccountMapper.selectById(id);
        if (account == null) {
            throw new BusinessException(ResultCode.CHANNEL_ACCOUNT_NOT_FOUND, "渠道账号不存在");
        }
        return account;
    }

    private String resolveChannelCode(Long channelId) {
        if (channelId == null) {
            return null;
        }
        Channel channel = channelMapper.selectById(channelId);
        return channel == null ? null : channel.getCode();
    }

    private String accessToken(ChannelAccount account) {
        return str(parseAuth(account.getAuthConfig()).get("accessToken"));
    }

    private String agentId(ChannelAccount account) {
        return str(parseAuth(account.getAuthConfig()).get("agentId"));
    }

    private Map<String, Object> parseAuth(String authConfig) {
        if (StringUtils.hasText(authConfig)) {
            try {
                return objectMapper.readValue(authConfig, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ignored) {
            }
        }
        return new HashMap<>();
    }

    private String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private void recordSendEvent(ChannelAccount account, String toExternalUserId, String content) {
        try {
            ChannelEvent event = new ChannelEvent();
            event.setTenantId(account.getTenantId());
            event.setChannelAccountId(account.getId());
            event.setEventType("send");
            event.setExternalEventId(UUID.randomUUID().toString());
            event.setExternalUserId(toExternalUserId);
            event.setRawPayload(objectMapper.writeValueAsString(Map.of("content", content)));
            event.setMapped(1);
            channelEventService.receiveEvent(event);
        } catch (Exception e) {
            log.warn("记录发送事件失败: {}", e.getMessage());
        }
    }
}
