package com.aicrm.module.followup.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.auth.UserContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.followup.entity.FollowUp;
import com.aicrm.module.followup.mapper.FollowUpMapper;
import com.aicrm.module.followup.service.FollowUpService;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.mapper.LeadMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 客户跟进记录服务实现（3.2.5）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowUpServiceImpl extends ServiceImpl<FollowUpMapper, FollowUp> implements FollowUpService {

    /** 跟进后 SLA 默认 24 小时 */
    private static final int SLA_HOURS = 24;

    private final LeadMapper leadMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FollowUp create(FollowUp followUp) {
        if (!StringUtils.hasText(followUp.getContent())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "跟进内容不能为空");
        }
        if (followUp.getLeadId() == null && followUp.getCustomerId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "线索或客户至少关联一个");
        }
        if (!StringUtils.hasText(followUp.getMethod())) {
            followUp.setMethod("other");
        }
        // 跟进人：当前登录用户
        followUp.setUserId(UserContext.getUserId());
        this.save(followUp);

        // 跟进状态同步：关联线索置为跟进中，并按下次跟进时间刷新 SLA 截止时间
        if (followUp.getLeadId() != null) {
            Lead lead = leadMapper.selectById(followUp.getLeadId());
            if (lead != null) {
                LocalDateTime deadline = followUp.getNextTime() != null
                        ? followUp.getNextTime()
                        : LocalDateTime.now().plusHours(SLA_HOURS);
                leadMapper.update(null, new LambdaUpdateWrapper<Lead>()
                        .eq(Lead::getId, lead.getId())
                        .set(Lead::getStatus, "contacting")
                        .set(Lead::getSlaDeadline, deadline));
            }
        }
        log.info("新增跟进记录 id={}, leadId={}, user={}", followUp.getId(), followUp.getLeadId(), followUp.getUserId());
        return followUp;
    }

    @Override
    public PageResult<FollowUp> page(Long leadId, Long customerId, long page, long size) {
        if (leadId == null && customerId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "线索或客户至少指定一个");
        }
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(leadId != null, FollowUp::getLeadId, leadId)
                .eq(customerId != null, FollowUp::getCustomerId, customerId)
                .orderByDesc(FollowUp::getId);
        return PageResult.of(this.page(new Page<>(page, size), wrapper));
    }
}
