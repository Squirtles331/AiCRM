package com.aicrm.module.lead.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.mapper.LeadMapper;
import com.aicrm.module.lead.service.LeadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 线索服务实现
 */
@Service
public class LeadServiceImpl extends ServiceImpl<LeadMapper, Lead> implements LeadService {

    @Override
    public PageResult<Lead> pageLeads(Long tenantId, String status, String intent, long page, long size) {
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(tenantId != null, Lead::getTenantId, tenantId)
                .eq(status != null && !status.isBlank(), Lead::getStatus, status)
                .eq(intent != null && !intent.isBlank(), Lead::getIntent, intent)
                .orderByDesc(Lead::getId);
        return PageResult.of(this.page(new Page<>(page, size), wrapper));
    }

    @Override
    public Lead createLead(Lead lead) {
        if (lead.getTenantId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "tenantId 不能为空");
        }
        if (lead.getStatus() == null) {
            lead.setStatus("new");
        }
        if (lead.getScore() == null) {
            lead.setScore(0);
        }
        this.save(lead);
        // TODO(M1): 触发工作流事件 —— 发布 RabbitMQ channel.event.new / lead.created
        return lead;
    }
}
