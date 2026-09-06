package com.aicrm.module.openapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Knife4j 统一配置（阶段 1.2）
 * <p>配置接口扫描包、文档分组、接口基本信息（标题/版本/描述）、
 * 全局请求头（租户 ID X-Tenant-Id、JWT 令牌 Authorization Bearer）。
 * 访问入口：/doc.html（Knife4j 增强 UI）
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aicrmOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AiCRM 接口文档")
                        .description("""
销售线索系统（AiCRM）核心业务接口文档。

                                统一返回结构：`{ code, message, data }`，code=200 表示成功；
                                分页接口 data 为 `PageResult{ page, size, total, pages, records }`；
                                错误码：400 参数错误 / 401 未登录 / 403 无权限 / 404 资源不存在 / 500 系统错误，业务错误码 1000+ 见各接口描述。

                                鉴权：除登录外的接口需携带请求头 `Authorization: Bearer <token>`；
                                多租户接口建议同时携带请求头 `X-Tenant-Id`。
                                """)
                        .version("0.1.0")
                        .contact(new Contact().name("AiCRM 技术团队")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components().addSecuritySchemes("BearerAuth",
                        new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("登录后获取的 JWT 令牌，格式：Bearer <token>")));
    }

    /** 全局请求头：租户 ID（所有 /api/** 接口可携带） */
    @Bean
    public GlobalOpenApiCustomizer tenantHeaderCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> operation.addParametersItem(
                        new Parameter()
                                .in("header")
                                .name("X-Tenant-Id")
                                .description("租户 ID（多租户隔离场景必填；未登录直连调试时使用）")
                                .required(false)
                                .schema(new StringSchema()))));
    }

    // ---------- 文档分组 ----------

    @Bean
    public GroupedOpenApi groupAll() {
        return grouped("全部接口", "com.aicrm.module");
    }

    @Bean
    public GroupedOpenApi groupSystem() {
        return grouped("系统管理",
                "com.aicrm.module.user.controller",
                "com.aicrm.module.tenant.controller",
                "com.aicrm.module.system.controller",
                "com.aicrm.module.log.controller",
                "com.aicrm.module.job.controller",
                "com.aicrm.module.document.controller",
                "com.aicrm.module.file.controller");
    }

    @Bean
    public GroupedOpenApi groupCustomer() {
        return grouped("客户管理",
                "com.aicrm.module.lead.controller",
                "com.aicrm.module.customer.controller",
                "com.aicrm.module.identity.controller",
                "com.aicrm.module.tag.controller",
                "com.aicrm.module.followup.controller");
    }

    @Bean
    public GroupedOpenApi groupConversation() {
        return grouped("会话管理", "com.aicrm.module.conversation.controller");
    }

    @Bean
    public GroupedOpenApi groupWecom() {
        return grouped("企微侧边栏", "com.aicrm.module.wecom.controller");
    }

    @Bean
    public GroupedOpenApi groupChannel() {
        return grouped("渠道管理", "com.aicrm.module.channel.controller");
    }

    @Bean
    public GroupedOpenApi groupKnowledge() {
        return grouped("知识中台",
                "com.aicrm.module.product.controller",
                "com.aicrm.module.competitor.controller",
                "com.aicrm.module.speech.controller");
    }

    @Bean
    public GroupedOpenApi groupDashboard() {
        return grouped("数据统计", "com.aicrm.module.dashboard.controller");
    }

    private GroupedOpenApi grouped(String group, String... packages) {
        return GroupedOpenApi.builder()
                .group(group)
                .packagesToScan(packages)
                .pathsToMatch("/api/**")
                .build();
    }
}
