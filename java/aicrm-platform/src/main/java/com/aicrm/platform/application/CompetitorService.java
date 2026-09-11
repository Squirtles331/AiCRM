package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Service
public class CompetitorService {
    public record Competitor(long id, String name, String positioning, String strengths, String weaknesses,
                             Status status, long ownerUserId, long version) {
        public enum Status { ACTIVE, ARCHIVED }
    }
    public record Create(String name, String positioning, String strengths, String weaknesses) { }
    public record Versioned(long version) { }

    private final JdbcTemplate jdbc;
    private final IdGenerator ids;
    private final IdempotencyService idempotency;
    private final AuditLogService audit;
    private final OutboxService outbox;
    private final ObjectMapper mapper;

    public CompetitorService(JdbcTemplate jdbc, IdGenerator ids, IdempotencyService idempotency,
                             AuditLogService audit, OutboxService outbox, ObjectMapper mapper) {
        this.jdbc = jdbc; this.ids = ids; this.idempotency = idempotency; this.audit = audit; this.outbox = outbox; this.mapper = mapper;
    }

    @Transactional
    public Competitor create(Actor actor, Create command, String key) {
        manage(actor);
        return idempotency.execute(actor, "competitor:create", key, command, Competitor.class, () -> {
            Competitor competitor = new Competitor(ids.nextId(), required(command.name(), 200, "名称"), optional(command.positioning(), 1000, "定位"),
                    optional(command.strengths(), 2000, "优势"), optional(command.weaknesses(), 2000, "短板"), Competitor.Status.ACTIVE, actor.userId(), 0);
            jdbc.update("insert into crm_competitor(id,tenant_id,name,positioning,strengths,weaknesses,status,owner_user_id,version,created_by,updated_by) values(?,?,?,?,?,?, 'ACTIVE',?,?,?,?)",
                    competitor.id(), actor.tenantId(), competitor.name(), competitor.positioning(), competitor.strengths(), competitor.weaknesses(), actor.userId(), 0, actor.userId(), actor.userId());
            journal(actor, "CREATE", competitor, "CompetitorCreated");
            return competitor;
        });
    }

    public Competitor get(Actor actor, long id) { read(actor); return find(actor, id); }
    public List<Competitor> list(Actor actor) {
        read(actor);
        return jdbc.query("select * from crm_competitor where tenant_id=? and deleted_at is null order by updated_at desc,id desc", (rs, n) -> map(rs), actor.tenantId());
    }

    @Transactional
    public Competitor archive(Actor actor, long id, Versioned command) {
        manage(actor); find(actor, id);
        if (jdbc.update("update crm_competitor set status='ARCHIVED',version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status='ACTIVE' and version=? and deleted_at is null", actor.userId(), actor.tenantId(), id, command.version()) != 1) {
            throw new DomainException(ErrorCode.CONFLICT, "竞争信息状态已变化，请刷新后重试");
        }
        Competitor after = find(actor, id); journal(actor, "ARCHIVE", after, "CompetitorArchived"); return after;
    }

    private Competitor find(Actor actor, long id) {
        List<Competitor> rows = jdbc.query("select * from crm_competitor where tenant_id=? and id=? and deleted_at is null", (rs, n) -> map(rs), actor.tenantId(), id);
        if (rows.isEmpty()) throw new DomainException(ErrorCode.NOT_FOUND, "竞争信息不存在");
        return rows.get(0);
    }
    private Competitor map(ResultSet rs) throws SQLException { return new Competitor(rs.getLong("id"), rs.getString("name"), rs.getString("positioning"), rs.getString("strengths"), rs.getString("weaknesses"), Competitor.Status.valueOf(rs.getString("status")), rs.getLong("owner_user_id"), rs.getLong("version")); }
    private void read(Actor actor) { if (!actor.hasPermission("competitor:read") && !actor.hasPermission("competitor:manage")) throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：competitor:read"); }
    private void manage(Actor actor) { if (!actor.hasPermission("competitor:manage")) throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：competitor:manage"); }
    private String required(String value, int max, String field) { if (value == null || value.isBlank()) throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "不能为空"); return optional(value, max, field); }
    private String optional(String value, int max, String field) { if (value == null || value.isBlank()) return null; String trimmed = value.trim(); if (trimmed.length() > max) throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "长度超限"); return trimmed; }
    private void journal(Actor actor, String action, Competitor competitor, String eventType) { try { String snapshot = mapper.writeValueAsString(competitor), operationId = "competitor:" + action + ":" + ids.nextId(); audit.record(actor, action, "COMPETITOR", competitor.id(), operationId, "API", null, "{}", snapshot); outbox.append(new DomainEvent(eventType, "COMPETITOR", competitor.id(), actor.tenantId(), snapshot, Instant.now()), operationId, actor.userId()); } catch (Exception exception) { throw new IllegalStateException(exception); } }
}
