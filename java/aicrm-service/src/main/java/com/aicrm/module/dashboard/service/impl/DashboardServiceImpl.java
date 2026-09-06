package com.aicrm.module.dashboard.service.impl;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.channel.entity.ChannelAccount;
import com.aicrm.module.channel.entity.ChannelEvent;
import com.aicrm.module.channel.mapper.ChannelAccountMapper;
import com.aicrm.module.channel.mapper.ChannelEventMapper;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.mapper.ConversationMapper;
import com.aicrm.module.conversation.mapper.MessageMapper;
import com.aicrm.module.dashboard.dto.ChannelConversionStat;
import com.aicrm.module.dashboard.dto.DashboardOverview;
import com.aicrm.module.dashboard.dto.DashboardTrend;
import com.aicrm.module.dashboard.dto.SalesWorkloadStat;
import com.aicrm.module.dashboard.service.DashboardService;
import com.aicrm.module.followup.entity.FollowUp;
import com.aicrm.module.followup.mapper.FollowUpMapper;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.mapper.LeadMapper;
import com.aicrm.module.user.entity.User;
import com.aicrm.module.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础看板服务实现（M1 实时统计，V2 切换 metric_daily 预聚合）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    /** 有效对话判定：≥3 条消息 */
    private static final int EFFECTIVE_MESSAGE_THRESHOLD = 3;

    private final LeadMapper leadMapper;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final ChannelAccountMapper channelAccountMapper;
    private final ChannelEventMapper channelEventMapper;
    private final UserMapper userMapper;
    private final FollowUpMapper followUpMapper;

    @Override
    public DashboardOverview getOverview(Long tenantId, LocalDateTime from, LocalDateTime to) {
        TimeRange range = normalizeRange(tenantId, from, to);
        LocalDateTime start = range.from();
        LocalDateTime end = range.to();

        DashboardOverview overview = new DashboardOverview();
        overview.setTenantId(tenantId);
        overview.setFrom(start);
        overview.setTo(end);

        // 线索量
        overview.setLeadCount(leadMapper.selectCount(new LambdaQueryWrapper<Lead>()
                .eq(Lead::getTenantId, tenantId)
                .ge(Lead::getCreatedAt, start)
                .le(Lead::getCreatedAt, end)));

        // 会话量 / 转人工数
        List<Conversation> conversations = conversationMapper.selectList(new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getTenantId, tenantId)
                .ge(Conversation::getCreatedAt, start)
                .le(Conversation::getCreatedAt, end));
        overview.setConversationCount((long) conversations.size());
        overview.setTransferCount(conversations.stream()
                .filter(c -> "transferred".equals(c.getStatus()))
                .count());

        // 响应时效 + 有效对话率
        computeMessageMetrics(overview, tenantId, conversations);

        // 意向分布
        overview.setIntentDistribution(queryIntentDistribution(tenantId, start, end));
        return overview;
    }

    @Override
    public List<DashboardTrend> getTrend(Long tenantId, LocalDateTime from, LocalDateTime to) {
        TimeRange range = normalizeRange(tenantId, from, to);
        LocalDateTime start = range.from();
        LocalDateTime end = range.to();

        // 线索按日聚合
        Map<String, Long> leadByDay = countByDay(leadMapper.selectMaps(new QueryWrapper<Lead>()
                .select("to_char(created_at, 'YYYY-MM-DD') AS day", "count(*) AS cnt")
                .eq("tenant_id", tenantId)
                .ge("created_at", start)
                .le("created_at", end)
                .groupBy("to_char(created_at, 'YYYY-MM-DD')")));

        // 会话按日聚合（含转人工状态）
        Map<String, Long> convByDay = new HashMap<>();
        Map<String, Long> transferByDay = new HashMap<>();
        for (Conversation c : conversationMapper.selectList(new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getTenantId, tenantId)
                .ge(Conversation::getCreatedAt, start)
                .le(Conversation::getCreatedAt, end))) {
            if (c.getCreatedAt() == null) {
                continue;
            }
            String day = c.getCreatedAt().toLocalDate().toString();
            convByDay.merge(day, 1L, Long::sum);
            if ("transferred".equals(c.getStatus())) {
                transferByDay.merge(day, 1L, Long::sum);
            }
        }

        // 补齐缺失日期
        List<DashboardTrend> trend = new ArrayList<>();
        LocalDate cursor = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        while (!cursor.isAfter(endDate)) {
            String day = cursor.toString();
            long conv = convByDay.getOrDefault(day, 0L);
            DashboardTrend item = new DashboardTrend();
            item.setDate(cursor);
            item.setLeadCount(leadByDay.getOrDefault(day, 0L));
            item.setConversationCount(conv);
            item.setTransferCount(transferByDay.getOrDefault(day, 0L));
            trend.add(item);
            cursor = cursor.plusDays(1);
        }
        return trend;
    }

    @Override
    public List<ChannelConversionStat> getChannelConversion(Long tenantId, LocalDateTime from, LocalDateTime to) {
        TimeRange range = normalizeRange(tenantId, from, to);
        LocalDateTime start = range.from();
        LocalDateTime end = range.to();

        List<ChannelAccount> accounts = channelAccountMapper.selectList(new LambdaQueryWrapper<ChannelAccount>()
                .eq(ChannelAccount::getTenantId, tenantId)
                .orderByAsc(ChannelAccount::getId));

        // 渠道事件按账号聚合
        Map<String, Long> eventByAccount = countGrouped(channelEventMapper.selectMaps(new QueryWrapper<ChannelEvent>()
                .select("channel_account_id AS key", "count(*) AS cnt")
                .eq("tenant_id", tenantId)
                .ge("created_at", start)
                .le("created_at", end)
                .groupBy("channel_account_id")));

        // 线索按来源渠道账号聚合
        Map<String, Long> leadByAccount = countGrouped(leadMapper.selectMaps(new QueryWrapper<Lead>()
                .select("source_channel_id AS key", "count(*) AS cnt")
                .eq("tenant_id", tenantId)
                .isNotNull("source_channel_id")
                .ge("created_at", start)
                .le("created_at", end)
                .groupBy("source_channel_id")));

        List<ChannelConversionStat> stats = new ArrayList<>();
        for (ChannelAccount account : accounts) {
            String id = String.valueOf(account.getId());
            long events = eventByAccount.getOrDefault(id, 0L);
            long leads = leadByAccount.getOrDefault(id, 0L);
            ChannelConversionStat stat = new ChannelConversionStat();
            stat.setChannelAccountId(account.getId());
            stat.setAccountName(account.getAccountName());
            stat.setEventCount(events);
            stat.setLeadCount(leads);
            stat.setConversionRate(events == 0 ? null : divide(leads, events, 4));
            stats.add(stat);
        }
        return stats;
    }

    @Override
    public List<SalesWorkloadStat> getSalesWorkload(Long tenantId, LocalDateTime from, LocalDateTime to) {
        TimeRange range = normalizeRange(tenantId, from, to);
        LocalDateTime start = range.from();
        LocalDateTime end = range.to();

        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getTenantId, tenantId)
                .eq(User::getStatus, 1));

        // 分配线索（owner_id）聚合
        Map<String, Long> assignedByUser = countGrouped(leadMapper.selectMaps(new QueryWrapper<Lead>()
                .select("owner_id AS key", "count(*) AS cnt")
                .eq("tenant_id", tenantId)
                .isNotNull("owner_id")
                .ge("created_at", start)
                .le("created_at", end)
                .groupBy("owner_id")));

        // 成交线索聚合
        Map<String, Long> wonByUser = countGrouped(leadMapper.selectMaps(new QueryWrapper<Lead>()
                .select("owner_id AS key", "count(*) AS cnt")
                .eq("tenant_id", tenantId)
                .isNotNull("owner_id")
                .eq("status", "won")
                .ge("created_at", start)
                .le("created_at", end)
                .groupBy("owner_id")));

        // 跟进记录按跟进人聚合
        Map<String, Long> followByUser = countGrouped(followUpMapper.selectMaps(new QueryWrapper<FollowUp>()
                .select("user_id AS key", "count(*) AS cnt")
                .eq("tenant_id", tenantId)
                .isNotNull("user_id")
                .ge("created_at", start)
                .le("created_at", end)
                .groupBy("user_id")));

        // 人工会话按接待人聚合
        Map<String, Long> convByUser = countGrouped(conversationMapper.selectMaps(new QueryWrapper<Conversation>()
                .select("assigned_to AS key", "count(*) AS cnt")
                .eq("tenant_id", tenantId)
                .isNotNull("assigned_to")
                .ge("created_at", start)
                .le("created_at", end)
                .groupBy("assigned_to")));

        List<SalesWorkloadStat> stats = new ArrayList<>();
        for (User user : users) {
            String id = String.valueOf(user.getId());
            SalesWorkloadStat stat = new SalesWorkloadStat();
            stat.setUserId(user.getId());
            stat.setUserName(user.getName());
            stat.setAssignedLeadCount(assignedByUser.getOrDefault(id, 0L));
            stat.setFollowUpCount(followByUser.getOrDefault(id, 0L));
            stat.setWonCount(wonByUser.getOrDefault(id, 0L));
            stat.setConversationCount(convByUser.getOrDefault(id, 0L));
            stats.add(stat);
        }
        return stats;
    }

    // ---------- 工具 ----------

    /** 时间范围校验与默认值 */
    private TimeRange normalizeRange(Long tenantId, LocalDateTime from, LocalDateTime to) {
        if (tenantId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "tenantId 不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = from != null ? from : now.minusDays(7);
        LocalDateTime end = to != null ? to : now;
        if (start.isAfter(end)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "from 不能晚于 to");
        }
        return new TimeRange(start, end);
    }

    private record TimeRange(LocalDateTime from, LocalDateTime to) {
    }

    /** 聚合查询结果转 Map：key 列 AS key，计数列 AS cnt */
    private Map<String, Long> countGrouped(List<Map<String, Object>> rows) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Object key = row.get("key");
            Number cnt = row.get("cnt") instanceof Number n ? n : 0L;
            if (key != null) {
                result.put(String.valueOf(key), cnt.longValue());
            }
        }
        return result;
    }

    /** 按日聚合结果转 Map：day 列 AS day，计数列 AS cnt */
    private Map<String, Long> countByDay(List<Map<String, Object>> rows) {
        return countGrouped(rows);
    }

    private double divide(long a, long b, int scale) {
        return BigDecimal.valueOf(a)
                .divide(BigDecimal.valueOf(b), scale, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }

    /** 响应时效：客户首条消息到人工首条回复（秒）；有效对话率：至少 3 条消息会话占比。 */
    private void computeMessageMetrics(DashboardOverview overview, Long tenantId, List<Conversation> conversations) {
        if (conversations.isEmpty()) {
            overview.setAvgResponseSec(null);
            overview.setEffectiveRate(0d);
            return;
        }
        List<Long> conversationIds = conversations.stream().map(Conversation::getId).toList();
        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getTenantId, tenantId)
                .in(Message::getConversationId, conversationIds)
                .orderByAsc(Message::getId));

        Map<Long, List<Message>> byConversation = new HashMap<>();
        for (Message m : messages) {
            byConversation.computeIfAbsent(m.getConversationId(), k -> new ArrayList<>()).add(m);
        }

        long responseTotalSec = 0;
        int responseCount = 0;
        int effectiveCount = 0;

        for (Conversation c : conversations) {
            List<Message> msgs = byConversation.get(c.getId());
            if (msgs == null || msgs.isEmpty()) {
                continue;
            }
            // 有效对话：消息数达标
            if (msgs.size() >= EFFECTIVE_MESSAGE_THRESHOLD) {
                effectiveCount++;
            }
            // 响应时效：首个客户消息到其后首个人工回复
            LocalDateTime firstCustomerAt = null;
            LocalDateTime firstHumanAt = null;
            for (Message m : msgs) {
                if ("customer".equals(m.getSenderType()) && firstCustomerAt == null) {
                    firstCustomerAt = m.getCreatedAt();
                }
                if ("human".equals(m.getSenderType()) && firstHumanAt == null
                        && firstCustomerAt != null
                        && m.getCreatedAt() != null
                        && !m.getCreatedAt().isBefore(firstCustomerAt)) {
                    firstHumanAt = m.getCreatedAt();
                }
            }
            if (firstCustomerAt != null && firstHumanAt != null) {
                responseTotalSec += Duration.between(firstCustomerAt, firstHumanAt).getSeconds();
                responseCount++;
            }
        }

        overview.setAvgResponseSec(responseCount == 0
                ? null
                : BigDecimal.valueOf(responseTotalSec)
                        .divide(BigDecimal.valueOf(responseCount), 2, java.math.RoundingMode.HALF_UP)
                        .doubleValue());
        overview.setEffectiveRate(BigDecimal.valueOf(effectiveCount)
                .divide(BigDecimal.valueOf(conversations.size()), 4, java.math.RoundingMode.HALF_UP)
                .doubleValue());
    }

    /** 人工维护的意向分布：按 lead.intent 分组计数。 */
    private List<DashboardOverview.IntentCount> queryIntentDistribution(Long tenantId, LocalDateTime start, LocalDateTime end) {
        List<Map<String, Object>> rows = leadMapper.selectMaps(new QueryWrapper<Lead>()
                .select("intent", "count(*) AS cnt")
                .eq("tenant_id", tenantId)
                .ge("created_at", start)
                .le("created_at", end)
                .groupBy("intent"));

        List<DashboardOverview.IntentCount> distribution = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String intent = row.get("intent") == null ? "unknown" : String.valueOf(row.get("intent"));
            Number cnt = row.get("cnt") instanceof Number n ? n : 0L;
            distribution.add(new DashboardOverview.IntentCount(intent, cnt.longValue()));
        }
        return distribution;
    }
}
