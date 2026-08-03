package com.aicrm.config;

import com.aicrm.common.context.TenantContext;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置：多租户 / 分页 / 乐观锁 / 防全表更新 / 审计字段自动填充
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 多租户拦截器：SQL 自动拼接 tenant_id 条件（跨租户完全隔离）
     * <p>
     * - 所有业务表均含 tenant_id，自动注入；tenant 表（字典）忽略
     * - INSERT 已显式包含 tenant_id 列时跳过注入（不覆盖实体值）
     * - 上下文未设置时使用 -1，查询匹配不到数据，保证不越权
     */
    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor() {
        return new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new LongValue(TenantContext.getTenantIdOrDefault());
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 平台级表：租户主表、套餐定义、菜单/权限、字典、系统参数、渠道定义、任务执行记录，不做租户隔离
                return switch (tableName.toLowerCase()) {
                    case "tenant", "sys_plan", "sys_menu",
                         "sys_dict_type", "sys_dict_data", "sys_config",
                         "channel", "sys_job_log" -> true;
                    default -> false;
                };
            }
        });
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(TenantLineInnerInterceptor tenantLineInnerInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户（最先执行，后续拦截器基于已注入条件继续处理）
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        // 分页（PostgreSQL）
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        pagination.setMaxLimit(500L);
        interceptor.addInnerInterceptor(pagination);
        // 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 防全表更新/删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

    /**
     * 审计字段自动填充：created_at / updated_at
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
