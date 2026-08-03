package com.aicrm.config;

import com.aicrm.common.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web 配置：跨域 + 租户上下文填充 + 登录鉴权
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** 请求头中的租户标识（登录后由网关/JWT 注入，M1 直连模式允许显式传参） */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    private final LoginInterceptor loginInterceptor;

    public WebConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantContextInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login");
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/error");
    }

    @Bean
    public HandlerInterceptor tenantContextInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                Long tenantId = resolveTenantId(request);
                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                        Object handler, Exception ex) {
                // 线程池复用：请求结束必须清理，避免串租户
                TenantContext.clear();
            }
        };
    }

    /** 解析租户 ID：优先请求头 X-Tenant-Id，其次 query 参数 tenantId */
    private Long resolveTenantId(HttpServletRequest request) {
        String fromHeader = request.getHeader(HEADER_TENANT_ID);
        if (fromHeader != null && !fromHeader.isBlank()) {
            return parseLong(fromHeader);
        }
        return parseLong(request.getParameter("tenantId"));
    }

    private Long parseLong(String text) {
        try {
            return text == null || text.isBlank() ? null : Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
