package com.aicrm.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI crmOpenApi() {
        return new OpenAPI()
                .info(new Info().title("CRM API").version("v1")
                        .description("CRM 模块化单体公开契约，仅提供 /api/v1 接口。"))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components().addSecuritySchemes("BearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi crmV1Api() {
        return GroupedOpenApi.builder().group("crm-v1")
                .packagesToScan("com.aicrm.web")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(this::useStringIdentifiers)
                .build();
    }

    /** Java accepts decimal-string IDs, and the public contract must not lose Snowflake precision in JavaScript. */
    private void useStringIdentifiers(OpenAPI openApi) {
        if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) return;
        openApi.getComponents().getSchemas().values().forEach(schema -> {
            if (schema.getProperties() == null) return;
            schema.getProperties().forEach((name, property) -> {
                String propertyName = String.valueOf(name);
                Schema<?> propertySchema = (Schema<?>) property;
                if ("id".equals(propertyName) || propertyName.endsWith("Id") || propertyName.endsWith("Ids")) {
                    propertySchema.setType("string");
                    propertySchema.setFormat(null);
                }
            });
        });
    }
}
