package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Authenticates an active tenant user and resolves all authority from platform tables. */
@Service
public class AuthenticationService {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final PlatformPrincipalService principalService;

    public AuthenticationService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder,
                                 PlatformPrincipalService principalService) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.principalService = principalService;
    }

    public Actor authenticate(long tenantId, String username, String password) {
        Credential credential = jdbcTemplate.query(
                "select u.id, u.password_hash from crm_user u join crm_tenant t on t.id=u.tenant_id "
                        + "where u.tenant_id=? and lower(u.username)=lower(?) and u.status=1 and t.status=1 "
                        + "and u.deleted_at is null and t.deleted_at is null",
                rs -> rs.next() ? new Credential(rs.getLong(1), rs.getString(2)) : null,
                tenantId, username);
        if (credential == null || credential.passwordHash() == null
                || !passwordEncoder.matches(password, credential.passwordHash())) {
            throw new DomainException(ErrorCode.UNAUTHORIZED, "租户、用户名或密码错误");
        }
        return principalService.resolve(tenantId, credential.userId());
    }

    private record Credential(long userId, String passwordHash) {
    }
}
