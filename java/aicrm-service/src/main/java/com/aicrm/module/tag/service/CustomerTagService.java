package com.aicrm.module.tag.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.customer.entity.Customer;
import com.aicrm.module.tag.entity.CustomerTag;
import com.aicrm.module.tag.entity.CustomerTagRule;

import java.util.List;

/**
 * 客户标签服务（3.2.3）：标签管理、手动打标、自动规则、标签筛选
 */
public interface CustomerTagService {

    // ---------- 标签管理 ----------

    PageResult<CustomerTag> pageTags(String keyword, long page, long size);

    CustomerTag createTag(CustomerTag tag);

    CustomerTag updateTag(CustomerTag tag);

    void deleteTag(Long id);

    void updateTagStatus(Long id, Integer status);

    // ---------- 手动打标 / 筛选 ----------

    /**
     * 给客户批量打标（幂等：已打标签自动忽略）
     */
    void tagCustomers(Long customerId, List<Long> tagIds);

    /**
     * 移除客户标签
     */
    void untagCustomer(Long customerId, Long tagId);

    /**
     * 标签筛选：分页查询某标签下的客户
     */
    PageResult<Customer> pageCustomersByTag(Long tagId, long page, long size);

    // ---------- 自动标签规则 ----------

    PageResult<CustomerTagRule> pageRules(long page, long size);

    CustomerTagRule createRule(CustomerTagRule rule);

    CustomerTagRule updateRule(CustomerTagRule rule);

    void deleteRule(Long id);

    /**
     * 执行单条规则：为当前租户下匹配条件的客户打标，返回命中客户数
     */
    int applyRule(Long ruleId);

    /**
     * 执行全部启用规则（跨租户，供定时任务调用）
     */
    int applyAllRules();
}
