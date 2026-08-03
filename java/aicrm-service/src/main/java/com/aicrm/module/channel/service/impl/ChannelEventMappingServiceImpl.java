package com.aicrm.module.channel.service.impl;

import com.aicrm.module.channel.entity.Channel;
import com.aicrm.module.channel.entity.ChannelAccount;
import com.aicrm.module.channel.entity.ChannelEvent;
import com.aicrm.module.channel.mapper.ChannelAccountMapper;
import com.aicrm.module.channel.mapper.ChannelMapper;
import com.aicrm.module.channel.service.ChannelEventMappingService;
import com.aicrm.module.channel.service.ChannelEventService;
import com.aicrm.module.identity.service.IdentityService;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 渠道事件 → 线索 映射实现
 * <p>
 * 3.2.2 起接入身份归一：同渠道同用户（douyin/video_channel → social 身份，wecom → wecom 身份）
 * 首次出现创建线索并绑定身份，再次出现命中已有线索复用，避免重复建线索。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelEventMappingServiceImpl implements ChannelEventMappingService {

    private final LeadService leadService;
    private final ChannelEventService channelEventService;
    private final IdentityService identityService;
    private final ChannelAccountMapper channelAccountMapper;
    private final ChannelMapper channelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean mapToLead(ChannelEvent event) {
        // 幂等保护：已映射则跳过
        if (Integer.valueOf(1).equals(event.getMapped())) {
            return false;
        }
        Long tenantId = event.getTenantId();

        // 身份归一：同渠道同用户命中已有线索则复用
        String identityType = resolveIdentityType(event);
        String identityValue = resolveIdentityValue(event);
        if (identityType != null && identityValue != null) {
            Long existingLeadId = identityService.resolveLeadId(tenantId, identityType, identityValue);
            if (existingLeadId != null && leadService.getById(existingLeadId) != null) {
                markMapped(event);
                log.info("渠道事件命中已有线索 eventId={}, leadId={}", event.getExternalEventId(), existingLeadId);
                return true;
            }
        }

        Lead lead = new Lead();
        lead.setTenantId(tenantId);
        lead.setSourceChannelId(event.getChannelAccountId());
        lead.setSourceType(event.getEventType());
        lead.setSourceContentId(event.getExternalUserId());
        lead.setStatus("new");
        lead.setScore(0);
        leadService.createLead(lead);

        if (identityType != null && identityValue != null) {
            identityService.bindToLead(tenantId, identityType, identityValue, lead.getId(), "channel_event");
        }

        markMapped(event);
        log.info("渠道事件映射为线索 eventId={}, leadId={}", event.getExternalEventId(), lead.getId());
        return true;
    }

    private void markMapped(ChannelEvent event) {
        event.setMapped(1);
        channelEventService.updateById(event);
    }

    /** 身份类型：短视频渠道 → social，企微 → wecom；未接入渠道不归一 */
    private String resolveIdentityType(ChannelEvent event) {
        String channelCode = resolveChannelCode(event.getChannelAccountId());
        return switch (channelCode == null ? "" : channelCode) {
            case "douyin", "video_channel" -> "social";
            case "wecom" -> "wecom";
            default -> null;
        };
    }

    /** 身份值：渠道编码 + 渠道侧用户 ID，跨账号唯一避免冲突 */
    private String resolveIdentityValue(ChannelEvent event) {
        if (!StringUtils.hasText(event.getExternalUserId())) {
            return null;
        }
        String channelCode = resolveChannelCode(event.getChannelAccountId());
        if (!StringUtils.hasText(channelCode)) {
            return null;
        }
        return channelCode + ":" + event.getExternalUserId();
    }

    private String resolveChannelCode(Long channelAccountId) {
        if (channelAccountId == null) {
            return null;
        }
        ChannelAccount account = channelAccountMapper.selectById(channelAccountId);
        if (account == null) {
            return null;
        }
        Channel channel = channelMapper.selectById(account.getChannelId());
        return channel == null ? null : channel.getCode();
    }
}
