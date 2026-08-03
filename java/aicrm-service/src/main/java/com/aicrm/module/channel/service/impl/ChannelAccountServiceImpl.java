package com.aicrm.module.channel.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.channel.client.douyin.DouyinApiClient;
import com.aicrm.module.channel.client.douyin.dto.DouyinTokenResponse;
import com.aicrm.module.channel.client.video.VideoChannelApiClient;
import com.aicrm.module.channel.client.wecom.WecomApiClient;
import com.aicrm.module.channel.client.wecom.dto.WecomTokenResponse;
import com.aicrm.module.channel.entity.Channel;
import com.aicrm.module.channel.entity.ChannelAccount;
import com.aicrm.module.channel.mapper.ChannelAccountMapper;
import com.aicrm.module.channel.mapper.ChannelMapper;
import com.aicrm.module.channel.service.ChannelAccountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * 渠道账号服务实现
 * <p>
 * auth_config（JSONB）约定结构：{"accessToken":"...", "refreshToken":"...", "expireAt":"epoch毫秒",
 * 渠道密钥 appId/secret 等按渠道不同可扩展}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelAccountServiceImpl extends ServiceImpl<ChannelAccountMapper, ChannelAccount>
        implements ChannelAccountService {

    /** token 有效期：2 小时（模拟/兜底，接入真实平台后由渠道响应决定） */
    private static final long TOKEN_TTL_MS = 2 * 3600_000L;

    private final ObjectMapper objectMapper;
    private final ChannelMapper channelMapper;
    private final DouyinApiClient douyinApiClient;
    private final WecomApiClient wecomApiClient;
    private final VideoChannelApiClient videoChannelApiClient;

    @Override
    public PageResult<ChannelAccount> page(String keyword, Long channelId, Integer healthStatus, long page, long size) {
        LambdaQueryWrapper<ChannelAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), ChannelAccount::getAccountName, keyword)
                .eq(channelId != null, ChannelAccount::getChannelId, channelId)
                .eq(healthStatus != null, ChannelAccount::getHealthStatus, healthStatus)
                .orderByDesc(ChannelAccount::getId);
        return PageResult.of(baseMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public ChannelAccount create(ChannelAccount account) {
        if (!StringUtils.hasText(account.getAccountName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "账号名称不能为空");
        }
        if (account.getChannelId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "渠道不能为空");
        }
        if (account.getHealthStatus() == null) {
            account.setHealthStatus(1);
        }
        if (account.getRiskLevel() == null) {
            account.setRiskLevel(0);
        }
        account.setTenantId(TenantContext.getTenantId());
        baseMapper.insert(account);
        return account;
    }

    @Override
    public ChannelAccount update(ChannelAccount account) {
        if (account.getId() == null || baseMapper.selectById(account.getId()) == null) {
            throw new BusinessException(ResultCode.CHANNEL_ACCOUNT_NOT_FOUND, "渠道账号不存在");
        }
        baseMapper.updateById(account);
        return baseMapper.selectById(account.getId());
    }

    @Override
    public void delete(Long id) {
        if (baseMapper.selectById(id) == null) {
            throw new BusinessException(ResultCode.CHANNEL_ACCOUNT_NOT_FOUND, "渠道账号不存在");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public void updateHealthStatus(Long id, Integer healthStatus, Integer riskLevel) {
        if (baseMapper.selectById(id) == null) {
            throw new BusinessException(ResultCode.CHANNEL_ACCOUNT_NOT_FOUND, "渠道账号不存在");
        }
        this.lambdaUpdate()
                .eq(ChannelAccount::getId, id)
                .set(ChannelAccount::getHealthStatus, healthStatus)
                .set(riskLevel != null, ChannelAccount::getRiskLevel, riskLevel)
                .update();
    }

    @Override
    public boolean refreshToken(Long id) {
        ChannelAccount account = requireAccount(id);
        return refreshAuth(account, false);
    }

    @Override
    public int refreshExpiredTokens() {
        List<ChannelAccount> accounts = baseMapper.selectAllHealthyAccounts();
        int count = 0;
        for (ChannelAccount account : accounts) {
            try {
                if (isExpired(account.getAuthConfig()) && refreshAuth(account, true)) {
                    count++;
                }
            } catch (Exception e) {
                log.warn("账号 [{}] token 刷新失败: {}", account.getAccountName(), e.getMessage());
            }
        }
        return count;
    }

    private ChannelAccount requireAccount(Long id) {
        ChannelAccount account = baseMapper.selectById(id);
        if (account == null) {
            throw new BusinessException(ResultCode.CHANNEL_ACCOUNT_NOT_FOUND, "渠道账号不存在");
        }
        return account;
    }

    /**
     * 刷新授权 token 并写回
     *
     * @param ignoreTenant true=定时任务场景（跨租户直接更新）；false=HTTP 请求场景（走租户过滤）
     */
    private boolean refreshAuth(ChannelAccount account, boolean ignoreTenant) {
        try {
            Map<String, Object> auth = parseAuth(account.getAuthConfig());
            String channelCode = resolveChannelCode(account.getChannelId());
            switch (channelCode == null ? "" : channelCode) {
                case "douyin" -> {
                    DouyinTokenResponse resp = douyinApiClient.refreshAccessToken(
                            str(auth.get("appId")), str(auth.get("secret")), str(auth.get("refreshToken")));
                    if (resp.getAccessToken() == null) {
                        throw new BusinessException(ResultCode.CHANNEL_API_ERROR, "抖音刷新未返回 access_token");
                    }
                    auth.put("accessToken", resp.getAccessToken());
                    auth.put("refreshToken", resp.getRefreshToken());
                    auth.put("expireAt", String.valueOf(System.currentTimeMillis()
                            + (resp.getExpiresIn() == null ? 0 : resp.getExpiresIn()) * 1000L));
                }
                case "wecom" -> {
                    // 企微用 corpid + secret 直接换取 access_token（有效期 7200s）
                    WecomTokenResponse resp = wecomApiClient.getAccessToken(
                            str(auth.get("corpId")), str(auth.get("corpSecret")));
                    if (resp.getAccessToken() == null) {
                        throw new BusinessException(ResultCode.CHANNEL_API_ERROR, "企微未返回 access_token");
                    }
                    auth.put("accessToken", resp.getAccessToken());
                    auth.put("expireAt", String.valueOf(System.currentTimeMillis()
                            + (resp.getExpiresIn() == null ? 7200 : resp.getExpiresIn()) * 1000L));
                }
                case "video_channel" -> {
                    String token = videoChannelApiClient.getAccessToken(
                            str(auth.get("appId")), str(auth.get("secret")));
                    if (token == null) {
                        throw new BusinessException(ResultCode.CHANNEL_API_ERROR, "视频号未返回 access_token");
                    }
                    auth.put("accessToken", token);
                    auth.put("expireAt", String.valueOf(System.currentTimeMillis() + 2 * 3600_000L));
                }
                default -> {
                    // TODO 其他渠道接入后按渠道分发；当前为模拟刷新
                    long now = System.currentTimeMillis();
                    auth.put("accessToken", "mock_" + now);
                    auth.put("refreshToken", "mock_r_" + now);
                    auth.put("expireAt", String.valueOf(now + TOKEN_TTL_MS));
                }
            }
            String json = objectMapper.writeValueAsString(auth);
            if (ignoreTenant) {
                baseMapper.updateAuthConfigIgnoreTenant(account.getId(), json);
            } else {
                account.setAuthConfig(json);
                baseMapper.updateById(account);
            }
            log.info("渠道账号 [{}] token 已刷新", account.getAccountName());
            return true;
        } catch (Exception e) {
            log.error("渠道账号 [{}] token 刷新异常", account.getAccountName(), e);
            return false;
        }
    }

    /** 渠道编码解析：channel_id → channel.code */
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

    private boolean isExpired(String authConfig) {
        Object expireAt = parseAuth(authConfig).get("expireAt");
        if (expireAt == null) {
            return true;
        }
        try {
            return System.currentTimeMillis() >= Long.parseLong(String.valueOf(expireAt));
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
