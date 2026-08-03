package com.aicrm.module.lead.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.lead.entity.LeadAssignRule;

/**
 * 线索分配引擎（3.2.4）：按产品线/地域/轮询规则分配、销售离线兜底、超时回收重分配
 */
public interface LeadAssignService {

    /**
     * 分配单条线索：按启用规则（sort 升序）匹配 → 目标销售；离线兜底；无可用销售保持未分配
     */
    void assignLead(Long leadId);

    /**
     * 回收超时未跟进线索并重新分配（定时任务场景，跨租户），返回回收重分配条数
     */
    int reassignExpiredLeads();

    // ---------- 规则管理 ----------

    PageResult<LeadAssignRule> pageRules(long page, long size);

    LeadAssignRule createRule(LeadAssignRule rule);

    LeadAssignRule updateRule(LeadAssignRule rule);

    void deleteRule(Long id);

    void updateRuleStatus(Long id, Integer status);
}
