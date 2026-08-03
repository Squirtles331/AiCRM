package com.aicrm.module.customer.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.customer.entity.Customer;
import com.aicrm.module.customer.mapper.CustomerMapper;
import com.aicrm.module.customer.service.CustomerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 客户主数据服务实现（3.2.1）
 */
@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    /** 合法客户阶段（字典与前端一致） */
    private static final Set<String> STAGES = Set.of(
            "new", "potential", "intention", "negotiating", "won", "lost");

    @Override
    public PageResult<Customer> page(String keyword, String industry, String region, String stage,
                                     long page, long size) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Customer::getName, keyword)
                .like(StringUtils.hasText(industry), Customer::getIndustry, industry)
                .like(StringUtils.hasText(region), Customer::getRegion, region)
                .eq(StringUtils.hasText(stage), Customer::getStage, stage)
                .orderByDesc(Customer::getScore)
                .orderByDesc(Customer::getId);
        return PageResult.of(baseMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public Customer detail(Long id) {
        return requireCustomer(id);
    }

    @Override
    public Customer create(Customer customer) {
        if (!StringUtils.hasText(customer.getName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "客户名称不能为空");
        }
        if (!StringUtils.hasText(customer.getStage())) {
            customer.setStage("new");
        }
        if (customer.getIntentLevel() == null) {
            customer.setIntentLevel(0);
        }
        if (customer.getScore() == null) {
            customer.setScore(0);
        }
        if (customer.getEnrichmentStatus() == null) {
            customer.setEnrichmentStatus(0);
        }
        customer.setTenantId(TenantContext.getTenantId());
        baseMapper.insert(customer);
        log.info("客户创建 id={}, name={}", customer.getId(), customer.getName());
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        requireCustomer(customer.getId());
        baseMapper.updateById(customer);
        return requireCustomer(customer.getId());
    }

    @Override
    public void delete(Long id) {
        requireCustomer(id);
        baseMapper.deleteById(id);
    }

    @Override
    public Customer updateStage(Long id, String stage) {
        requireCustomer(id);
        if (!StringUtils.hasText(stage) || !STAGES.contains(stage)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "非法客户阶段: " + stage);
        }
        this.lambdaUpdate()
                .eq(Customer::getId, id)
                .set(Customer::getStage, stage)
                .update();
        return requireCustomer(id);
    }

    @Override
    public Customer updateScore(Long id, Integer intentLevel, Integer score) {
        requireCustomer(id);
        if (intentLevel != null && (intentLevel < 0 || intentLevel > 5)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "意向等级需在 0-5 之间");
        }
        if (score != null && (score < 0 || score > 100)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "评分需在 0-100 之间");
        }
        this.lambdaUpdate()
                .eq(Customer::getId, id)
                .set(intentLevel != null, Customer::getIntentLevel, intentLevel)
                .set(score != null, Customer::getScore, score)
                .update();
        return requireCustomer(id);
    }

    @Override
    public Customer enrich(Long id, String orgStructure, String source) {
        requireCustomer(id);
        this.lambdaUpdate()
                .eq(Customer::getId, id)
                .set(orgStructure != null, Customer::getOrgStructure, orgStructure)
                .set(StringUtils.hasText(source), Customer::getSource, source)
                .set(Customer::getEnrichmentStatus, 1)
                .update();
        return requireCustomer(id);
    }

    private Customer requireCustomer(Long id) {
        Customer customer = baseMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "客户不存在");
        }
        return customer;
    }
}
