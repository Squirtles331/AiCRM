package com.aicrm.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具：签发与解析登录令牌
 * <p>
 * Claims 约定：sub=userId，tenantId、roles（角色码集合）、perms（按钮权限码集合）。
 * 密钥 aicrm.jwt.secret 由配置文件提供（生产环境建议通过环境变量注入）。
 */
@Component
public class JwtUtil {

    @Value("${aicrm.jwt.secret:aicrm-default-secret-change-me-0123456789}")
    private String secret;

    @Value("${aicrm.jwt.expire-seconds:86400}")
    private long expireSeconds;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 签发令牌 */
    public String createToken(Long userId, Long tenantId, List<String> roleCodes, List<String> perms) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("tenantId", tenantId)
                .claim("roles", roleCodes == null ? List.of() : roleCodes)
                .claim("perms", perms == null ? List.of() : perms)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expireSeconds * 1000))
                .signWith(key())
                .compact();
    }

    /** 解析令牌，失败返回 null（过期/篡改/非法） */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser().verifyWith(key()).build()
                    .parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    public Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public Long getTenantId(Claims claims) {
        return claims.get("tenantId", Long.class);
    }

    /** 角色码集合 */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List<?> list) {
            return (List<String>) list;
        }
        return List.of();
    }

    /** 按钮权限码集合 */
    @SuppressWarnings("unchecked")
    public List<String> getPerms(Claims claims) {
        Object perms = claims.get("perms");
        if (perms instanceof List<?> list) {
            return (List<String>) list;
        }
        return List.of();
    }
}
