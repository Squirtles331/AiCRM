package com.aicrm.module.lead.mapper;

import com.aicrm.module.lead.entity.Lead;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 线索 Mapper
 */
public interface LeadMapper extends BaseMapper<Lead> {

    /**
     * 查询超时未跟进线索（跨租户，供定时任务回收重分配）：
     * 已分配且超过 SLA 截止时间、仍处于跟进中状态
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM lead WHERE deleted = 0 AND owner_id IS NOT NULL " +
            "AND status IN ('assigned','contacting') AND sla_deadline < now()")
    List<Lead> selectExpiredAssignedLeads();
}
