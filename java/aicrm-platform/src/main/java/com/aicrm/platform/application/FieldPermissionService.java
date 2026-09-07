package com.aicrm.platform.application;

import com.aicrm.kernel.security.Actor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/** Resolves field access from tenant-scoped role grants. Sensitive fields default to denied. */
@Service
public class FieldPermissionService {
    private final JdbcTemplate jdbcTemplate;

    public FieldPermissionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean canView(Actor actor, String resourceType, String fieldName) {
        if (actor.hasRole("admin")) {
            return true;
        }
        Boolean granted = jdbcTemplate.query("select exists (select 1 from crm_user_role ur "
                        + "join crm_role r on r.tenant_id=ur.tenant_id and r.id=ur.role_id "
                        + "join crm_role_field_permission fp on fp.tenant_id=ur.tenant_id and fp.role_id=ur.role_id "
                        + "where ur.tenant_id=? and ur.user_id=? and r.status=1 and r.deleted_at is null "
                        + "and fp.resource_type=? and fp.field_name=? and fp.can_view=true and fp.deleted_at is null)",
                rs -> rs.next() ? rs.getBoolean(1) : false,
                actor.tenantId(), actor.userId(), resourceType, fieldName);
        return Boolean.TRUE.equals(granted);
    }
}
