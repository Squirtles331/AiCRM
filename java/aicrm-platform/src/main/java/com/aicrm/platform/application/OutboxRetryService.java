package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/** Replays a dead-lettered outbox message under an explicit high-risk permission. */
@Service
public class OutboxRetryService {
    private final JdbcTemplate jdbcTemplate;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public OutboxRetryService(JdbcTemplate jdbcTemplate, AuditLogService auditLogService, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RetryReceipt retryDead(Actor actor, long eventId) {
        require(actor);
        OutboxState before = jdbcTemplate.query("select aggregate_type, aggregate_id, event_type, retry_count, last_error "
                        + "from crm_outbox_event where tenant_id=? and id=? and deleted_at is null and status='DEAD' for update",
                rs -> rs.next() ? new OutboxState(rs.getString(1), rs.getLong(2), rs.getString(3), rs.getInt(4), rs.getString(5)) : null,
                actor.tenantId(), eventId);
        if (before == null) {
            throw new DomainException(ErrorCode.CONFLICT, "仅可重放本租户的死信事件");
        }
        if (jdbcTemplate.update("update crm_outbox_event set status='PENDING', retry_count=0, last_error=null, next_retry_at=null, "
                        + "dead_lettered_at=null, locked_by=null, locked_at=null, available_at=now(), updated_at=now(), updated_by=? "
                        + "where tenant_id=? and id=? and status='DEAD'",
                actor.userId(), actor.tenantId(), eventId) != 1) {
            throw new DomainException(ErrorCode.CONFLICT, "死信事件状态已变化");
        }
        auditLogService.record(actor, "RETRY", "OUTBOX", eventId, null, "API", null, json(Map.of(
                        "aggregateType", before.aggregateType(), "aggregateId", before.aggregateId(), "eventType", before.eventType(),
                        "retryCount", before.retryCount(), "lastError", before.lastError() == null ? "" : before.lastError())),
                json(Map.of("status", "PENDING", "retryCount", 0)));
        return new RetryReceipt(eventId, "PENDING", 0);
    }

    private void require(Actor actor) {
        if (!actor.permissions().contains("outbox:retry")) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限: outbox:retry");
        }
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("无法序列化 Outbox 重放审计", exception);
        }
    }

    public record RetryReceipt(long eventId, String status, int retryCount) {
    }

    private record OutboxState(String aggregateType, long aggregateId, String eventType, int retryCount, String lastError) {
    }
}
