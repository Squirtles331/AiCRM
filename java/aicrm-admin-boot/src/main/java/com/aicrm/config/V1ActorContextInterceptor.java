package com.aicrm.config;

import com.aicrm.common.util.JwtUtil;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.PlatformPrincipalService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/** Resolves the v1 actor from platform tables after validating the JWT identity. */
@Component
public class V1ActorContextInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final PlatformPrincipalService principalService;

    public V1ActorContextInterceptor(JwtUtil jwtUtil, PlatformPrincipalService principalService) {
        this.jwtUtil = jwtUtil;
        this.principalService = principalService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        Claims claims = jwtUtil.parseToken(resolveToken(request));
        if (claims == null) {
            throw new DomainException(ErrorCode.UNAUTHORIZED, "登录已失效");
        }
        long tenantId = jwtUtil.getTenantId(claims);
        String requestedTenant = request.getHeader(WebConfig.HEADER_TENANT_ID);
        if (requestedTenant != null && !requestedTenant.isBlank() && !String.valueOf(tenantId).equals(requestedTenant.trim())) {
            throw new DomainException(ErrorCode.FORBIDDEN, "请求租户与登录身份不一致");
        }
        String traceId = request.getHeader("X-Trace-Id");
        TraceContext.set(traceId == null || traceId.isBlank() ? UUID.randomUUID().toString().replace("-", "") : traceId);
        response.setHeader("X-Trace-Id", TraceContext.get());
        ActorContext.set(principalService.resolve(tenantId, jwtUtil.getUserId(claims)));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ActorContext.clear();
        TraceContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(LoginInterceptor.HEADER_AUTHORIZATION);
        return header != null && header.startsWith(LoginInterceptor.TOKEN_PREFIX)
                ? header.substring(LoginInterceptor.TOKEN_PREFIX.length()).trim() : "";
    }
}
