package com.aicrm.integration.security;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import com.aicrm.platform.api.AccessTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/** JWT infrastructure adapter. Authorization claims are deliberately not embedded in the token. */
@Component
public class JwtAccessTokenService implements AccessTokenService {
    private final SecretKey key;
    private final long expiresInSeconds;

    public JwtAccessTokenService(@Value("${aicrm.jwt.secret}") String secret,
                                 @Value("${aicrm.jwt.expire-seconds:86400}") long expiresInSeconds) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("aicrm.jwt.secret must contain at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiresInSeconds = expiresInSeconds;
    }

    @Override
    public String issue(Actor actor) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(actor.userId()))
                .claim("tenantId", actor.tenantId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiresInSeconds)))
                .signWith(key)
                .compact();
    }

    @Override
    public TokenIdentity parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return new TokenIdentity(claims.get("tenantId", Long.class), Long.parseLong(claims.getSubject()));
        } catch (Exception exception) {
            throw new DomainException(ErrorCode.UNAUTHORIZED, "登录已失效");
        }
    }

    @Override
    public long expiresInSeconds() {
        return expiresInSeconds;
    }
}
