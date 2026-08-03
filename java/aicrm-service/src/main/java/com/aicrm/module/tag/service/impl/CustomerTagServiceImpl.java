package com.aicrm.module.tag.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.customer.entity.Customer;
import com.aicrm.module.customer.mapper.CustomerMapper;
import com.aicrm.module.tag.entity.CustomerTag;
import com.aicrm.module.tag.entity.CustomerTagRel;
import com.aicrm.module.tag.entity.CustomerTagRule;
import com.aicrm.module.tag.mapper.CustomerTagMapper;
import com.aicrm.module.tag.mapper.CustomerTagRelMapper;
import com.aicrm.module.tag.mapper.CustomerTagRuleMapper;
import com.aicrm.module.tag.service.CustomerTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 客户标签服务实现（3.2.3）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerTagServiceImpl implements CustomerTagService {

    /** 数值类规则字段 */
    private static final Set<String> NUMERIC_FIELDS = Set.of("score", "intent_level");
    /** 字符串类规则字段 */
    private static final Set<String> STRING_FIELDS = Set.of("stage", "industry", "region", "source");
    private static final Set<String> NUMERIC_OPS = Set.of("gt", "gte", "lt", "lte", "eq");
    private static final Set<String> STRING_OPS = Set.of("eq", "contains");

    private final CustomerTagMapper tagMapper;
    private final CustomerTagRelMapper relMapper;
    private final CustomerTagRuleMapper ruleMapper;
    private final CustomerMapper customerMapper;

    // ---------- 标签管理 ----------

    @Override
    public PageResult<CustomerTag> pageTags(String keyword, long page, long size) {
        LambdaQueryWrapper<CustomerTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), CustomerTag::getName, keyword)
                .orderByDesc(CustomerTag::getId);
        return PageResult.of(tagMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public CustomerTag createTag(CustomerTag tag) {
        if (!StringUtils.hasText(tag.getName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "标签名称不能为空");
        }
        if (tag.getStatus() == null) {
            tag.setStatus(1);
        }
        tag.setTenantId(TenantContext.getTenantId());
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public CustomerTag updateTag(CustomerTag tag) {
        requireTag(tag.getId());
        tagMapper.updateById(tag);
        return requireTag(tag.getId());
    }

    @Override
    public void deleteTag(Long id) {
        requireTag(id);
        tagMapper.deleteById(id);
        // 级联清理关联（保留历史 rel 亦可，此处物理清理避免孤儿数据）
        relMapper.delete(new LambdaQueryWrapper<CustomerTagRel>().eq(CustomerTagRel::getTagId, id));
    }

    @Override
    public void updateTagStatus(Long id, Integer status) {
        requireTag(id);
        tagMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CustomerTag>()
                .eq(CustomerTag::getId, id)
                .set(CustomerTag::getStatus, status));
    }

    // ---------- 手动打标 / 筛选 ----------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void tagCustomers(Long customerId, List<Long> tagIds) {
        if (customerMapper.selectById(customerId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "客户不存在");
        }
        if (tagIds == null || tagIds.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "标签列表不能为空");
        }
        Long tenantId = TenantContext.getTenantId();
        for (Long tagId : tagIds) {
            requireTag(tagId);
            CustomerTagRel rel = new CustomerTagRel();
            rel.setTenantId(tenantId);
            rel.setCustomerId(customerId);
            rel.setTagId(tagId);
            try {
                relMapper.insert(rel);
            } catch (DuplicateKeyException e) {
                // 已打标，忽略
            }
        }
    }

    @Override
    public void untagCustomer(Long customerId, Long tagId) {
        relMapper.delete(new LambdaQueryWrapper<CustomerTagRel>()
                .eq(CustomerTagRel::getCustomerId, customerId)
                .eq(CustomerTagRel::getTagId, tagId));
    }

    @Override
    public PageResult<Customer> pageCustomersByTag(Long tagId, long page, long size) {
        requireTag(tagId);
        return PageResult.of(relMapper.pageCustomersByTag(new Page<>(page, size), tagId));
    }

    // ---------- 自动标签规则 ----------

    @Override
    public PageResult<CustomerTagRule> pageRules(long page, long size) {
        return PageResult.of(ruleMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<CustomerTagRule>().orderByDesc(CustomerTagRule::getId)));
    }

    @Override
    public CustomerTagRule createRule(CustomerTagRule rule) {
        validateRule(rule);
        if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        rule.setTenantId(TenantContext.getTenantId());
        ruleMapper.insert(rule);
        return rule;
    }

    @Override
    public CustomerTagRule updateRule(CustomerTagRule rule) {
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
    @Transactional(rollbackFor = Exception.class)
    public int applyRule(Long ruleId) {
        CustomerTagRule rule = requireRule(ruleId);
        LambdaQueryWrapper<Customer> wrapper = buildRuleWrapper(rule);
        List<Customer> matched = customerMapper.selectList(wrapper);
        Long tenantId = TenantContext.getTenantId();
        for (Customer customer : matched) {
            CustomerTagRel rel = new CustomerTagRel();
            rel.setTenantId(tenantId);
            rel.setCustomerId(customer.getId());
            rel.setTagId(rule.getTagId());
            try {
                relMapper.insert(rel);
            } catch (DuplicateKeyException e) {
                // 已打标，忽略
            }
        }
        log.info("自动标签规则执行 ruleId={}, rule={}, 命中客户数={}", ruleId, rule.getRuleName(), matched.size());
        return matched.size();
    }

    @Override
    public int applyAllRules() {
        int total = 0;
        for (CustomerTagRule rule : ruleMapper.selectAllEnabledRules()) {
            TenantContext.setTenantId(rule.getTenantId());
            try {
                total += applyRule(rule.getId());
            } catch (Exception e) {
                log.warn("自动标签规则执行失败 ruleId={}, err={}", rule.getId(), e.getMessage());
            } finally {
                TenantContext.clear();
            }
        }
        return total;
    }

    // ---------- 基础 ----------

    private LambdaQueryWrapper<Customer> buildRuleWrapper(CustomerTagRule rule) {
        String field = rule.getConditionField();
        String op = rule.getConditionOp();
        String value = rule.getConditionValue();
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (NUMERIC_FIELDS.contains(field)) {
            int v;
            try {
                v = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "规则条件值必须为数字");
            }
            if ("score".equals(field)) {
                switch (op) {
                    case "gt" -> wrapper.gt(Customer::getScore, v);
                    case "gte" -> wrapper.ge(Customer::getScore, v);
                    case "lt" -> wrapper.lt(Customer::getScore, v);
                    case "lte" -> wrapper.le(Customer::getScore, v);
                    case "eq" -> wrapper.eq(Customer::getScore, v);
                    default -> throw new BusinessException(ResultCode.BAD_REQUEST, "数值字段不支持操作符: " + op);
                }
            } else {
                switch (op) {
                    case "gt" -> wrapper.gt(Customer::getIntentLevel, v);
                    case "gte" -> wrapper.ge(Customer::getIntentLevel, v);
                    case "lt" -> wrapper.lt(Customer::getIntentLevel, v);
                    case "lte" -> wrapper.le(Customer::getIntentLevel, v);
                    case "eq" -> wrapper.eq(Customer::getIntentLevel, v);
                    default -> throw new BusinessException(ResultCode.BAD_REQUEST, "数值字段不支持操作符: " + op);
                }
            }
        } else if (STRING_FIELDS.contains(field)) {
            if (!STRING_OPS.contains(op)) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "字符串字段不支持操作符: " + op);
            }
            // {0} 为预编译参数占位，避免 SQL 注入
            String condition = "eq".equals(op)
                    ? field + " = {0}"
                    : field + " LIKE CONCAT('%', {0}, '%')";
            wrapper.apply(condition, value);
        } else {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不支持的规则字段: " + field);
        }
        return wrapper;
    }

    private void validateRule(CustomerTagRule rule) {
        if (!StringUtils.hasText(rule.getRuleName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "规则名称不能为空");
        }
        requireTag(rule.getTagId());
        if (!NUMERIC_FIELDS.contains(rule.getConditionField())
                && !STRING_FIELDS.contains(rule.getConditionField())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不支持的规则字段: " + rule.getConditionField());
        }
        if (!StringUtils.hasText(rule.getConditionValue())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "规则条件值不能为空");
        }
    }

    private CustomerTag requireTag(Long id) {
        CustomerTag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "标签不存在");
        }
        return tag;
    }

    private CustomerTagRule requireRule(Long id) {
        CustomerTagRule rule = ruleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "标签规则不存在");
        }
        return rule;
    }
}
