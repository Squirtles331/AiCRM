package com.aicrm.config;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.PlatformPrincipalService;
import com.aicrm.platform.api.AccessTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/** Resolves the v1 actor from platform tables after validating the JWT identity. */
@Component
public class V1ActorContextInterceptor implements HandlerInterceptor {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private final AccessTokenService accessTokenService;
    private final PlatformPrincipalService principalService;

    public V1ActorContextInterceptor(AccessTokenService accessTokenService, PlatformPrincipalService principalService) {
        this.accessTokenService = accessTokenService;
        this.principalService = principalService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String traceId = request.getHeader("X-Trace-Id");
        TraceContext.set(traceId == null || traceId.isBlank() ? UUID.randomUUID().toString().replace("-", "") : traceId);
        response.setHeader("X-Trace-Id", TraceContext.get());
        if (isAnonymousV1Route(request)) {
            return true;
        }
        AccessTokenService.TokenIdentity identity = accessTokenService.parse(resolveToken(request));
        long tenantId = identity.tenantId();
        String requestedTenant = request.getHeader(WebConfig.HEADER_TENANT_ID);
        if (requestedTenant != null && !requestedTenant.isBlank() && !String.valueOf(tenantId).equals(requestedTenant.trim())) {
            throw new DomainException(ErrorCode.FORBIDDEN, "请求租户与登录身份不一致");
        }
        ActorContext.set(principalService.resolve(tenantId, identity.userId()));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ActorContext.clear();
        TraceContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        return header != null && header.startsWith(BEARER)
                ? header.substring(BEARER.length()).trim() : "";
    }

    private boolean isAnonymousV1Route(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.equals("/api/v1/auth/login") || path.matches("/api/v1/connectors/[^/]+/events");
    }
}
