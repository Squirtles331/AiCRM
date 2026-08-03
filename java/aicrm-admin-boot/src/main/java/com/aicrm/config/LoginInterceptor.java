package com.aicrm.config;

import com.aicrm.common.ResultCode;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.common.auth.UserContext;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 登录拦截器：解析 JWT → 填充用户/租户上下文 → 校验权限注解
 * <p>
 * 未携带或非法令牌一律 401；不满足 @RequirePermission（角色码或按钮权限码）一律 403。
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = resolveToken(request);
        if (token == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "登录已过期，请重新登录");
        }
        Long userId = jwtUtil.getUserId(claims);
        List<String> roles = jwtUtil.getRoles(claims);
        List<String> perms = jwtUtil.getPerms(claims);
        UserContext.set(userId, roles, perms);
        TenantContext.setTenantId(jwtUtil.getTenantId(claims));

        checkPermission(handler, roles, perms);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
        TenantContext.clear();
    }

    /**
     * 校验 @RequirePermission：角色码（value）或按钮权限码（perms）任一命中即放行；
     * admin 超管角色一律放行；两者均为空仅需登录。
     */
    private void checkPermission(Object handler, List<String> roleCodes, List<String> perms) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }
        RequirePermission permission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (permission == null) {
            return;
        }
        if (roleCodes.contains("admin")) {
            return; // 超管角色不参与按钮级权限校验
        }
        if (permission.value().length == 0 && permission.perms().length == 0) {
            return;
        }
        boolean allowed = Arrays.stream(permission.value()).anyMatch(roleCodes::contains)
                || Arrays.stream(permission.perms()).anyMatch(perms::contains);
        if (!allowed) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问该接口");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length()).trim();
        }
        return null;
    }
}
