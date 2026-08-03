package com.aicrm.module.tag.mapper;

import com.aicrm.module.tag.entity.CustomerTagRule;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 自动标签规则 Mapper
 */
public interface CustomerTagRuleMapper extends BaseMapper<CustomerTagRule> {

    /**
     * 查询全部启用规则（跨租户，供定时任务自动打标；规则执行时按租户逐条设置上下文）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT r.* FROM customer_tag_rule r " +
            "JOIN customer_tag t ON t.id = r.tag_id AND t.deleted = 0 " +
            "WHERE r.deleted = 0 AND r.status = 1 AND t.status = 1")
    List<CustomerTagRule> selectAllEnabledRules();
}
