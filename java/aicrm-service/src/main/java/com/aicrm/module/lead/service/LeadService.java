package com.aicrm.module.lead.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.lead.entity.Lead;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 线索服务
 */
public interface LeadService extends IService<Lead> {

    /**
     * 分页查询线索
     *
     * @param tenantId 租户 ID
     * @param status   状态过滤（可空）
     * @param intent   意向过滤（可空）
     * @param page     页码（从 1 开始）
     * @param size     每页条数
     * @return 分页结果
     */
    PageResult<Lead> pageLeads(Long tenantId, String status, String intent, long page, long size);

    /**
     * 创建线索（渠道事件映射入口，后续在此触发工作流）
     *
     * @param lead 线索
     * @return 创建后的线索
     */
    Lead createLead(Lead lead);
}
