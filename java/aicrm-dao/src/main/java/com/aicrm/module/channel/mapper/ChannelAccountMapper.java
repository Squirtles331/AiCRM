package com.aicrm.module.channel.mapper;

import com.aicrm.module.channel.entity.ChannelAccount;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 渠道账号 Mapper
 */
public interface ChannelAccountMapper extends BaseMapper<ChannelAccount> {

    /**
     * 查询全部健康账号（跨租户，供定时任务刷新 token 使用；业务查询走租户拦截）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM channel_account WHERE deleted = 0 AND health_status = 1")
    List<ChannelAccount> selectAllHealthyAccounts();

    /**
     * 更新授权配置（跨租户，定时任务场景无租户上下文）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Update("UPDATE channel_account SET auth_config = CAST(#{authConfig} AS jsonb), updated_at = now() WHERE id = #{id}")
    int updateAuthConfigIgnoreTenant(@Param("id") Long id, @Param("authConfig") String authConfig);
}
