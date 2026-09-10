package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
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
        UserMembership membership = jdbcTemplate.query(
                "select u.status, u.department_id, d.path from crm_user u "
                        + "join crm_department d on d.tenant_id = u.tenant_id and d.id = u.department_id "
                        + "where u.tenant_id = ? and u.id = ? and u.deleted_at is null and d.deleted_at is null",
                rs -> rs.next() ? new UserMembership(rs.getInt(1), rs.getLong(2), rs.getString(3)) : null,
                tenantId, userId);
        if (membership == null || membership.status() != 1) {
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
        Set<DataScope> dataScopes = new LinkedHashSet<>(jdbcTemplate.query(
                "select distinct r.data_scope from crm_user_role ur join crm_role r on r.id = ur.role_id "
                        + "where ur.tenant_id = ? and ur.user_id = ? and r.status = 1 and r.deleted_at is null",
                (rs, rowNum) -> DataScope.valueOf(rs.getString(1)), tenantId, userId));
        if (roles.contains("admin")) {
            dataScopes.add(DataScope.ALL);
        }
        if (dataScopes.isEmpty()) {
            dataScopes.add(DataScope.SELF);
        }
        return new Actor(tenantId, userId, membership.departmentId(), membership.departmentPath(),
                Set.copyOf(roles), Set.copyOf(permissions), Set.copyOf(dataScopes));
    }

    private Set<String> defaultPermissions(Set<String> roles) {
        Set<String> permissions = new LinkedHashSet<>();
        if (roles.stream().anyMatch(role -> List.of("admin", "supervisor").contains(role))) {
            permissions.addAll(Set.of("lead:read:any", "lead:write:any", "lead:assign", "lead:handover",
                    "customer:read:any", "customer:write:any", "customer:assign", "customer:handover"));
            permissions.addAll(Set.of("catalog:read", "catalog:write", "catalog:publish"));
            permissions.addAll(Set.of("opportunity:read:any", "opportunity:write:any", "opportunity:stage",
                    "opportunity:win", "opportunity:lose", "opportunity:restart"));
            permissions.addAll(Set.of("quote:read:any", "quote:write:any", "quote:submit", "quote:expire"));
            permissions.addAll(Set.of("quote:withdraw", "approval:manage", "approval:task:read", "approval:task:act"));
            permissions.addAll(Set.of("contract:create", "contract:read:any", "contract:write:any",
                    "contract:submit-signature", "contract:withdraw-signature", "contract:sign", "contract:void", "contract:change", "contract:approve-change"));
            permissions.addAll(Set.of("order:create", "order:read:any", "order:write:any", "order:confirm", "order:cancel", "order:approve-cancel", "order:close"));
            permissions.add("analytics:read");
        }
        if (!roles.isEmpty()) {
            permissions.addAll(Set.of("lead:create", "lead:claim", "lead:read:own", "lead:write:own",
                    "customer:create", "customer:claim", "customer:read:own", "customer:write:own",
                    "opportunity:create", "opportunity:read:own", "opportunity:write:own",
                    "opportunity:stage", "opportunity:win", "opportunity:lose", "opportunity:restart"));
            permissions.add("catalog:read");
            permissions.addAll(Set.of("quote:create", "quote:read:own", "quote:write:own", "quote:submit",
                    "quote:expire", "quote:withdraw", "approval:task:read", "approval:task:act"));
            permissions.addAll(Set.of("contract:create", "contract:read:own", "contract:write:own",
                    "contract:submit-signature", "contract:withdraw-signature"));
            permissions.addAll(Set.of("order:create", "order:read:own", "order:write:own", "order:confirm", "order:cancel", "order:close"));
            permissions.add("analytics:read");
        }
        return permissions;
    }

    private record UserMembership(int status, long departmentId, String departmentPath) {
    }
}
