package com.aicrm.module.customer.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.customer.entity.Customer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 客户主数据服务（3.2.1）
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 分页查询客户
     */
    PageResult<Customer> page(String keyword, String industry, String region, String stage,
                              long page, long size);

    /**
     * 客户详情
     */
    Customer detail(Long id);

    /**
     * 创建客户（tenantId 取当前租户上下文）
     */
    Customer create(Customer customer);

    /**
     * 更新客户基础信息
     */
    Customer update(Customer customer);

    /**
     * 删除客户（逻辑删除）
     */
    void delete(Long id);

    /**
     * 客户阶段流转：new/potential/intention/negotiating/won/lost
     */
    Customer updateStage(Long id, String stage);

    /**
     * 意向等级 / 评分维护
     */
    Customer updateScore(Long id, Integer intentLevel, Integer score);

    /**
     * 企业信息回填：写入人员架构等补全数据，标记已补全
     *
     * @param orgStructure 人员架构（JSONB 字符串，可空）
     * @param source       补全来源：公开数据/客户提供
     */
    Customer enrich(Long id, String orgStructure, String source);
}
