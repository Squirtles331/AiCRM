package com.aicrm.module.tenant.service.impl;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.tenant.entity.Plan;
import com.aicrm.module.tenant.mapper.PlanMapper;
import com.aicrm.module.tenant.service.PlanService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 套餐服务实现
 */
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanMapper planMapper;

    @Override
    public List<Plan> listEnabled() {
        return planMapper.selectList(new LambdaQueryWrapper<Plan>()
                .eq(Plan::getStatus, 1)
                .orderByAsc(Plan::getId));
    }

    @Override
    public Plan getByCode(String code) {
        Plan plan = planMapper.selectOne(new LambdaQueryWrapper<Plan>().eq(Plan::getCode, code));
        if (plan == null) {
            throw new BusinessException(ResultCode.PLAN_NOT_FOUND);
        }
        return plan;
    }
}
