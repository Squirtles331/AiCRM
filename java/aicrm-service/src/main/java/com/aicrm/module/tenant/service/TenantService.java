package com.aicrm.module.tenant.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.tenant.dto.TenantCreateRequest;
import com.aicrm.module.tenant.entity.Tenant;

/**
 * 租户管理服务（平台级，跨租户）
 */
public interface TenantService {

    /**
     * 分页查询租户（支持企业名/联系人关键字、套餐、状态过滤）
     */
    PageResult<Tenant> pageTenants(String keyword, String planCode, Integer status, long page, long size);

    /**
     * 租户详情
     */
    Tenant detail(Long id);

    /**
     * 创建租户并初始化默认数据（默认管理员账号）
     */
    Tenant createTenant(TenantCreateRequest request);

    /**
     * 更新租户（切换套餐时自动带出新套餐默认坐席/额度）
     */
    Tenant updateTenant(Tenant tenant);

    /**
     * 删除租户（逻辑删除）
     */
    void deleteTenant(Long id);

    /**
     * 启用/停用租户
     */
    void updateStatus(Long id, Integer status);

    /**
     * 校验租户可用性：存在、已启用、未到期；供登录及业务入口调用
     */
    void checkTenantAvailable(Long tenantId);
}
