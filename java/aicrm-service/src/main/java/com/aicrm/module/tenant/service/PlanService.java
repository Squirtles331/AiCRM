package com.aicrm.module.tenant.service;

import com.aicrm.module.tenant.entity.Plan;

import java.util.List;

/**
 * 套餐服务（平台级套餐定义查询）
 */
public interface PlanService {

    /**
     * 上架套餐列表（供创建租户/前端下拉使用）
     */
    List<Plan> listEnabled();

    /**
     * 按套餐编码查询详情
     */
    Plan getByCode(String code);
}
