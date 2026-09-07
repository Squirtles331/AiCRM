package com.aicrm.platform.application;

import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.TraceContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/** Writes immutable command audit records in the same transaction as the command. */
@Service
public class AuditLogService {
    private final JdbcTemplate jdbcTemplate;
    private final IdGenerator idGenerator;

    public AuditLogService(JdbcTemplate jdbcTemplate, IdGenerator idGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.idGenerator = idGenerator;
    }

    public void record(Actor actor, String action, String resourceType, long resourceId,
                       String operationId, String source, String batchNo, String beforeData, String afterData) {
        jdbcTemplate.update("insert into crm_audit_log (id, tenant_id, actor_user_id, action, resource_type, "
                        + "resource_id, operation_id, source, batch_no, before_data, after_data, trace_id, created_by, updated_by) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, ?, ?)",
                idGenerator.nextId(), actor.tenantId(), actor.userId(), action, resourceType, resourceId,
                operationId, source, batchNo, beforeData, afterData, TraceContext.get(), actor.userId(), actor.userId());
    }
}
