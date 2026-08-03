package com.aicrm.module.lead.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.entity.LeadAssignRule;
import com.aicrm.module.lead.mapper.LeadAssignRuleMapper;
import com.aicrm.module.lead.mapper.LeadMapper;
import com.aicrm.module.lead.service.LeadAssignService;
import com.aicrm.module.user.entity.User;
import com.aicrm.module.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 线索分配引擎实现（3.2.4）
 * <p>
 * 分配流程：按启用规则（sort 升序）匹配线索 extra 中的产品线/地域 → 指定销售；
 * 目标销售离线（停用或超过 {@link #ONLINE_TIMEOUT_MINUTES} 分钟未活跃）时，顺延到规则轮询组
 * 在线销售；无匹配规则时默认分配给在线销售中未完成线索最少者；无可用销售则保持未分配待下次调度。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeadAssignServiceImpl implements LeadAssignService {

    /** 分配后 SLA：24 小时内未跟进将被回收 */
    private static final int SLA_HOURS = 24;
    /** 销售在线判定：最后活跃超过该分钟数视为离线 */
    private static final int ONLINE_TIMEOUT_MINUTES = 10;

    private final LeadMapper leadMapper;
    private final LeadAssignRuleMapper ruleMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignLead(Long leadId) {
        Lead lead = leadMapper.selectById(leadId);
        if (lead == null) {
            throw new BusinessException(ResultCode.LEAD_NOT_FOUND, "线索不存在");
        }
        applyAssignment(lead);
    }

    @Override
    public int reassignExpiredLeads() {
        int count = 0;
        for (Lead lead : leadMapper.selectExpiredAssignedLeads()) {
            TenantContext.setTenantId(lead.getTenantId());
            try {
                applyAssignment(lead);
                count++;
            } catch (Exception e) {
                log.warn("线索回收重分配失败 leadId={}, err={}", lead.getId(), e.getMessage());
            } finally {
                TenantContext.clear();
            }
        }
        return count;
    }

    // ---------- 规则管理 ----------

    @Override
    public PageResult<LeadAssignRule> pageRules(long page, long size) {
        return PageResult.of(ruleMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<LeadAssignRule>().orderByAsc(LeadAssignRule::getSort)));
    }

    @Override
    public LeadAssignRule createRule(LeadAssignRule rule) {
        validateRule(rule);
        if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        if (rule.getSort() == null) {
            rule.setSort(0);
        }
        rule.setTenantId(TenantContext.getTenantId());
        ruleMapper.insert(rule);
        return rule;
    }

    @Override
    public LeadAssignRule updateRule(LeadAssignRule rule) {
        requireRule(rule.getId());
        validateRule(rule);
        ruleMapper.updateById(rule);
        return requireRule(rule.getId());
    }

    @Override
    public void deleteRule(Long id) {
        requireRule(id);
        ruleMapper.deleteById(id);
    }

    @Override
    public void updateRuleStatus(Long id, Integer status) {
        requireRule(id);
        ruleMapper.update(null, new LambdaUpdateWrapper<LeadAssignRule>()
                .eq(LeadAssignRule::getId, id)
                .set(LeadAssignRule::getStatus, status));
    }

    // ---------- 分配核心 ----------

    private void applyAssignment(Lead lead) {
        Long targetUserId = resolveAssignTarget(lead);
        if (targetUserId == null) {
            // 无可用销售：回置未分配，等待下个调度周期
            leadMapper.update(null, new LambdaUpdateWrapper<Lead>()
                    .eq(Lead::getId, lead.getId())
                    .set(Lead::getOwnerId, null)
                    .set(Lead::getStatus, "new"));
            log.info("线索 leadId={} 无可用在线销售，保持未分配", lead.getId());
            return;
        }
        leadMapper.update(null, new LambdaUpdateWrapper<Lead>()
                .eq(Lead::getId, lead.getId())
                .set(Lead::getOwnerId, targetUserId)
                .set(Lead::getStatus, "assigned")
                .set(Lead::getSlaDeadline, LocalDateTime.now().plusHours(SLA_HOURS)));
        log.info("线索 leadId={} 已分配 userId={}", lead.getId(), targetUserId);
    }

    /** 按规则解析目标销售；无匹配或目标离线时兜底到在线销售 */
    private Long resolveAssignTarget(Lead lead) {
        Map<String, Object> extra = parseExtra(lead.getExtra());
        String productLine = str(extra.get("productLine"));
        String region = str(extra.get("region"));

        List<LeadAssignRule> rules = ruleMapper.selectList(new LambdaQueryWrapper<LeadAssignRule>()
                .eq(LeadAssignRule::getStatus, 1)
                .orderByAsc(LeadAssignRule::getSort));
        for (LeadAssignRule rule : rules) {
            switch (rule.getRuleType() == null ? "" : rule.getRuleType()) {
                case "product" -> {
                    if (productLine != null && productLine.equals(rule.getMatchValue())) {
                        return pickTarget(rule);
                    }
                }
                case "region" -> {
                    if (region != null && region.equals(rule.getMatchValue())) {
                        return pickTarget(rule);
                    }
                }
                case "round_robin" -> {
                    return pickFromGroup(rule);
                }
                default -> log.debug("忽略未知分配规则类型 ruleId={}", rule.getId());
            }
        }
        // 默认兜底：在线销售中负载最轻
        return pickOnlineSales(new ArrayList<>());
    }

    /** 指定销售优先；离线则轮询组兜底；仍无则全局在线销售兜底 */
    private Long pickTarget(LeadAssignRule rule) {
        if (rule.getTargetUserId() != null) {
            User target = userMapper.selectById(rule.getTargetUserId());
            if (isOnline(target)) {
                return target.getId();
            }
        }
        Long groupFallback = pickFromGroup(rule);
        return groupFallback != null ? groupFallback : pickOnlineSales(new ArrayList<>());
    }

    /** 轮询组：组内在线销售中未完成线索最少者（负载均衡） */
    private Long pickFromGroup(LeadAssignRule rule) {
        List<Long> groupIds = parseGroupIds(rule.getTargetGroupIds());
        return pickOnlineSales(groupIds);
    }

    /**
     * 候选池内选择在线且未完成线索最少的销售；候选池为空时取全部在线销售。
     * 全部候选离线/无候选时返回 null。
     */
    private Long pickOnlineSales(List<Long> candidateIds) {
        List<User> candidates;
        if (candidateIds == null || candidateIds.isEmpty()) {
            candidates = userMapper.selectList(new LambdaQueryWrapper<User>()
                    .eq(User::getRoleCode, "sales")
                    .eq(User::getStatus, 1));
        } else {
            candidates = userMapper.selectList(new LambdaQueryWrapper<User>()
                    .in(User::getId, candidateIds)
                    .eq(User::getRoleCode, "sales")
                    .eq(User::getStatus, 1));
        }
        Long best = null;
        long bestLoad = Long.MAX_VALUE;
        for (User user : candidates) {
            if (!isOnline(user)) {
                continue;
            }
            long load = countUnfinishedLeads(user.getId());
            if (load < bestLoad) {
                bestLoad = load;
                best = user.getId();
            }
        }
        return best;
    }

    /** 销售未完成线索数（new/assigned/contacting） */
    private long countUnfinishedLeads(Long userId) {
        return leadMapper.selectCount(new LambdaQueryWrapper<Lead>()
                .eq(Lead::getOwnerId, userId)
                .in(Lead::getStatus, "new", "assigned", "contacting"));
    }

    /** 在线判定：启用且（无活跃记录视为在线，避免新销售永无线索）或最后活跃在超时窗口内 */
    private boolean isOnline(User user) {
        if (user == null || !Integer.valueOf(1).equals(user.getStatus())) {
            return false;
        }
        if (user.getLastActiveAt() == null) {
            return true;
        }
        return user.getLastActiveAt().isAfter(LocalDateTime.now().minusMinutes(ONLINE_TIMEOUT_MINUTES));
    }

    // ---------- 基础 ----------

    private void validateRule(LeadAssignRule rule) {
        if (!StringUtils.hasText(rule.getRuleName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "规则名称不能为空");
        }
        if (!StringUtils.hasText(rule.getRuleType())
                || !List.of("product", "region", "round_robin").contains(rule.getRuleType())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "规则类型必须为 product/region/round_robin");
        }
        if ("round_robin".equals(rule.getRuleType()) && !StringUtils.hasText(rule.getTargetGroupIds())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "轮询规则必须配置轮询组");
        }
        if (!"round_robin".equals(rule.getRuleType())) {
            if (!StringUtils.hasText(rule.getMatchValue())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "匹配值不能为空");
            }
            if (rule.getTargetUserId() == null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "指定销售不能为空");
            }
        }
    }

    private LeadAssignRule requireRule(Long id) {
        LeadAssignRule rule = ruleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分配规则不存在");
        }
        return rule;
    }

    private Map<String, Object> parseExtra(String extra) {
        if (StringUtils.hasText(extra)) {
            try {
                return objectMapper.readValue(extra, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ignored) {
                // 解析失败按空处理
            }
        }
        return new HashMap<>();
    }

    private List<Long> parseGroupIds(String targetGroupIds) {
        if (StringUtils.hasText(targetGroupIds)) {
            try {
                return objectMapper.readValue(targetGroupIds, new TypeReference<List<Long>>() {
                });
            } catch (Exception ignored) {
                // 解析失败按空组处理
            }
        }
        return new ArrayList<>();
    }

    private String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
