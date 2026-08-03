package com.aicrm.module.followup.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.followup.entity.FollowUp;

/**
 * 客户跟进记录服务（3.2.5）
 */
public interface FollowUpService {

    /**
     * 新增跟进记录：记录跟进人，并同步线索跟进状态（status → contacting，SLA 刷新为 next_time 或默认 24h）
     */
    FollowUp create(FollowUp followUp);

    /**
     * 跟进记录历史查询：按线索或客户分页
     */
    PageResult<FollowUp> page(Long leadId, Long customerId, long page, long size);
}
