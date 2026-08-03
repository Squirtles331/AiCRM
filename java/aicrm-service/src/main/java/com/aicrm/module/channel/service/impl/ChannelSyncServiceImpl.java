package com.aicrm.module.channel.service.impl;

import com.aicrm.common.context.TenantContext;
import com.aicrm.module.channel.client.douyin.DouyinApiClient;
import com.aicrm.module.channel.client.douyin.dto.DouyinComment;
import com.aicrm.module.channel.client.douyin.dto.DouyinVideo;
import com.aicrm.module.channel.client.video.VideoChannelApiClient;
import com.aicrm.module.channel.entity.Channel;
import com.aicrm.module.channel.entity.ChannelAccount;
import com.aicrm.module.channel.entity.ChannelEvent;
import com.aicrm.module.channel.mapper.ChannelAccountMapper;
import com.aicrm.module.channel.mapper.ChannelMapper;
import com.aicrm.module.channel.service.ChannelEventService;
import com.aicrm.module.channel.service.ChannelSyncService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 渠道异步同步服务实现：定时拉取评论/私信 → 渠道事件（幂等落库 + MQ 发布）
 * <p>
 * 拉取任务运行在无 Web 上下文的调度线程，账号查询跨租户（selectAllHealthyAccounts），
 * 每次入库前显式设置 {@link TenantContext}，保证租户拦截正常注入 tenant_id。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelSyncServiceImpl implements ChannelSyncService {

    private static final int VIDEO_LIMIT = 20;
    private static final int COMMENT_LIMIT = 50;

    private final ChannelAccountMapper channelAccountMapper;
    private final ChannelMapper channelMapper;
    private final ChannelEventService channelEventService;
    private final DouyinApiClient douyinApiClient;
    private final VideoChannelApiClient videoChannelApiClient;
    private final ObjectMapper objectMapper;

    @Override
    public int syncComments() {
        int total = 0;
        for (ChannelAccount account : channelAccountMapper.selectAllHealthyAccounts()) {
            TenantContext.setTenantId(account.getTenantId());
            try {
                String channelCode = resolveChannelCode(account.getChannelId());
                switch (channelCode == null ? "" : channelCode) {
                    case "douyin" -> total += pullDouyinComments(account);
                    case "video_channel" -> total += pullVideoComments(account);
                    default -> log.debug("渠道 [{}] 暂不支持评论拉取，跳过", channelCode);
                }
            } catch (Exception e) {
                // 单账号拉取失败不影响其他账号，重试由下一个调度周期兜底
                log.warn("账号 [{}] 评论拉取失败: {}", account.getAccountName(), e.getMessage());
            } finally {
                TenantContext.clear();
            }
        }
        return total;
    }

    @Override
    public int syncDirectMessages() {
        // 私信拉取接口待各渠道开放平台联调确认，当前保持占位，避免假拉取
        for (ChannelAccount account : channelAccountMapper.selectAllHealthyAccounts()) {
            String channelCode = resolveChannelCode(account.getChannelId());
            log.debug("账号 [{}] 私信拉取暂未接入（渠道={}），跳过", account.getAccountName(), channelCode);
        }
        return 0;
    }

    // ---------- 抖音 ----------

    private int pullDouyinComments(ChannelAccount account) throws Exception {
        Map<String, Object> auth = parseAuth(account.getAuthConfig());
        String accessToken = str(auth.get("accessToken"));
        if (!StringUtils.hasText(accessToken) || !StringUtils.hasText(account.getExternalId())) {
            log.warn("账号 [{}] 缺 access_token/open_id，跳过评论拉取", account.getAccountName());
            return 0;
        }
        int added = 0;
        List<DouyinVideo> videos = douyinApiClient.fetchVideos(accessToken, account.getExternalId(), 0, VIDEO_LIMIT);
        for (DouyinVideo video : videos) {
            List<DouyinComment> comments = douyinApiClient.fetchComments(accessToken, video.getVideoId(), 0, COMMENT_LIMIT);
            for (DouyinComment comment : comments) {
                ChannelEvent event = new ChannelEvent();
                event.setTenantId(account.getTenantId());
                event.setChannelAccountId(account.getId());
                event.setEventType("comment");
                // 评论 ID 全局唯一，前缀渠道编码避免跨账号冲突
                event.setExternalEventId("douyin:" + comment.getCommentId());
                event.setExternalUserId(comment.getOpenId());
                event.setRawPayload(objectMapper.writeValueAsString(Map.of(
                        "videoId", video.getVideoId(),
                        "videoTitle", video.getTitle(),
                        "content", comment.getContent(),
                        "createTime", comment.getCreateTime())));
                event.setMapped(0);
                if (channelEventService.receiveEvent(event)) {
                    added++;
                }
            }
        }
        return added;
    }

    // ---------- 视频号 ----------

    private int pullVideoComments(ChannelAccount account) throws Exception {
        Map<String, Object> auth = parseAuth(account.getAuthConfig());
        String accessToken = str(auth.get("accessToken"));
        if (!StringUtils.hasText(accessToken)) {
            log.warn("账号 [{}] 缺 access_token，跳过评论拉取", account.getAccountName());
            return 0;
        }
        // 视频号评论拉取依赖作品列表接口（待联调），当前保留接入点
        log.debug("账号 [{}] 视频号评论拉取待联调，跳过", account.getAccountName());
        return 0;
    }

    // ---------- 基础 ----------

    private String resolveChannelCode(Long channelId) {
        if (channelId == null) {
            return null;
        }
        Channel channel = channelMapper.selectById(channelId);
        return channel == null ? null : channel.getCode();
    }

    private String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Map<String, Object> parseAuth(String authConfig) {
        if (StringUtils.hasText(authConfig)) {
            try {
                return objectMapper.readValue(authConfig, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ignored) {
                // 解析失败按空配置处理
            }
        }
        return new HashMap<>();
    }
}
