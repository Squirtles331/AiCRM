package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Resolves permissions from database records rather than trusting JWT role claims. */
@Service
public class PlatformPrincipalService {
    private final JdbcTemplate jdbcTemplate;

    public PlatformPrincipalService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Actor resolve(long tenantId, long userId) {
        Integer active = jdbcTemplate.query(
                "select status from crm_user where tenant_id = ? and id = ? and deleted_at is null",
                rs -> rs.next() ? rs.getInt(1) : null, tenantId, userId);
        if (active == null || active != 1) {
            throw new DomainException(ErrorCode.UNAUTHORIZED, "用户不存在或已停用");
        }
        Set<String> roles = new LinkedHashSet<>(jdbcTemplate.query(
                "select r.code from crm_user_role ur join crm_role r on r.id = ur.role_id "
                        + "where ur.tenant_id = ? and ur.user_id = ? and r.status = 1 and r.deleted_at is null",
                (rs, rowNum) -> rs.getString(1), tenantId, userId));
        Set<String> permissions = new LinkedHashSet<>(jdbcTemplate.query(
                "select distinct rp.permission_code from crm_user_role ur "
                        + "join crm_role_permission rp on rp.tenant_id = ur.tenant_id and rp.role_id = ur.role_id "
                        + "where ur.tenant_id = ? and ur.user_id = ?",
                (rs, rowNum) -> rs.getString(1), tenantId, userId));
        permissions.addAll(defaultPermissions(roles));
        return new Actor(tenantId, userId, Set.copyOf(roles), Set.copyOf(permissions));
    }

    private Set<String> defaultPermissions(Set<String> roles) {
        Set<String> permissions = new LinkedHashSet<>();
        if (roles.stream().anyMatch(role -> List.of("admin", "supervisor").contains(role))) {
            permissions.addAll(Set.of("lead:read:any", "lead:write:any", "lead:assign", "lead:handover",
                    "customer:read:any", "customer:write:any", "customer:assign", "customer:handover"));
        }
        if (!roles.isEmpty()) {
            permissions.addAll(Set.of("lead:create", "lead:claim", "lead:read:own", "lead:write:own",
                    "customer:create", "customer:claim", "customer:read:own", "customer:write:own"));
        }
        return permissions;
    }
}
