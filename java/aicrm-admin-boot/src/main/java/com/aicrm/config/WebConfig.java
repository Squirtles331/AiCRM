package com.aicrm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web 配置：跨域与统一 V1 身份上下文。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** 请求头中的租户标识（登录后由网关/JWT 注入，M1 直连模式允许显式传参） */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    private final V1ActorContextInterceptor v1ActorContextInterceptor;
    private final List<String> allowedOrigins;

    public WebConfig(V1ActorContextInterceptor v1ActorContextInterceptor,
                     @Value("${aicrm.web.cors.allowed-origins:http://localhost:3000,http://localhost:5173}") String allowedOrigins) {
        this.v1ActorContextInterceptor = v1ActorContextInterceptor;
        this.allowedOrigins = List.of(allowedOrigins.split(",")).stream().map(String::trim).filter(origin -> !origin.isEmpty()).toList();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(v1ActorContextInterceptor)
                .addPathPatterns("/api/v1/**");
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
