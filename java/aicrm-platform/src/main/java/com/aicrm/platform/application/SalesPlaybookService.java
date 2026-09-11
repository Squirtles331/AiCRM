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
public class SalesPlaybookService {
    public record Playbook(long id, String title, String salesStage, String scenario, String content,
                           Status status, long ownerUserId, Instant publishedAt, Instant archivedAt, long version) {
        public enum Status { DRAFT, PUBLISHED, ARCHIVED }
    }

    public record Create(String title, String salesStage, String scenario, String content) { }
    public record Versioned(long version) { }

    private final JdbcTemplate jdbc;
    private final IdGenerator ids;
    private final IdempotencyService idempotency;
    private final AuditLogService audit;
    private final OutboxService outbox;
    private final ObjectMapper mapper;

    public SalesPlaybookService(JdbcTemplate jdbc, IdGenerator ids, IdempotencyService idempotency,
                                AuditLogService audit, OutboxService outbox, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.ids = ids;
        this.idempotency = idempotency;
        this.audit = audit;
        this.outbox = outbox;
        this.mapper = mapper;
    }

    @Transactional
    public Playbook create(Actor actor, Create command, String idempotencyKey) {
        manage(actor);
        return idempotency.execute(actor, "playbook:create", idempotencyKey, command, Playbook.class, () -> {
            Playbook playbook = new Playbook(ids.nextId(), required(command.title(), 200, "标题"),
                    required(command.salesStage(), 64, "销售阶段"), required(command.scenario(), 200, "适用场景"),
                    required(command.content(), 20_000, "内容"), Playbook.Status.DRAFT, actor.userId(), null, null, 0);
            jdbc.update("insert into crm_sales_playbook(id,tenant_id,title,sales_stage,scenario,content,status,owner_user_id,version,created_by,updated_by) values(?,?,?,?,?,?,'DRAFT',?,?,?,?)",
                    playbook.id(), actor.tenantId(), playbook.title(), playbook.salesStage(), playbook.scenario(),
                    playbook.content(), actor.userId(), 0, actor.userId(), actor.userId());
            journal(actor, "CREATE", playbook, "SalesPlaybookCreated");
            return playbook;
        });
    }

    public Playbook get(Actor actor, long id) {
        read(actor);
        return find(actor, id);
    }

    public List<Playbook> list(Actor actor) {
        read(actor);
        return jdbc.query("select * from crm_sales_playbook where tenant_id=? and deleted_at is null order by updated_at desc,id desc",
                (rs, rowNum) -> map(rs), actor.tenantId());
    }

    @Transactional
    public Playbook publish(Actor actor, long id, Versioned command) {
        return transition(actor, id, command, Playbook.Status.DRAFT, Playbook.Status.PUBLISHED,
                "PUBLISH", "SalesPlaybookPublished");
    }

    @Transactional
    public Playbook archive(Actor actor, long id, Versioned command) {
        return transition(actor, id, command, Playbook.Status.PUBLISHED, Playbook.Status.ARCHIVED,
                "ARCHIVE", "SalesPlaybookArchived");
    }

    private Playbook transition(Actor actor, long id, Versioned command, Playbook.Status from,
                                Playbook.Status to, String action, String eventType) {
        manage(actor);
        find(actor, id);
        int changed = jdbc.update("update crm_sales_playbook set status=?,published_at=case when ?='PUBLISHED' then now() else published_at end,archived_at=case when ?='ARCHIVED' then now() else null end,version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status=? and version=? and deleted_at is null",
                to.name(), to.name(), to.name(), actor.userId(), actor.tenantId(), id, from.name(), command.version());
        if (changed != 1) {
            throw new DomainException(ErrorCode.CONFLICT, "话术状态已变化，请刷新后重试");
        }
        Playbook after = find(actor, id);
        journal(actor, action, after, eventType);
        return after;
    }

    private Playbook find(Actor actor, long id) {
        List<Playbook> rows = jdbc.query("select * from crm_sales_playbook where tenant_id=? and id=? and deleted_at is null",
                (rs, rowNum) -> map(rs), actor.tenantId(), id);
        if (rows.isEmpty()) {
            throw new DomainException(ErrorCode.NOT_FOUND, "销售话术不存在");
        }
        return rows.get(0);
    }

    private Playbook map(ResultSet resultSet) throws SQLException {
        var publishedAt = resultSet.getTimestamp("published_at");
        var archivedAt = resultSet.getTimestamp("archived_at");
        return new Playbook(resultSet.getLong("id"), resultSet.getString("title"), resultSet.getString("sales_stage"),
                resultSet.getString("scenario"), resultSet.getString("content"),
                Playbook.Status.valueOf(resultSet.getString("status")), resultSet.getLong("owner_user_id"),
                publishedAt == null ? null : publishedAt.toInstant(), archivedAt == null ? null : archivedAt.toInstant(),
                resultSet.getLong("version"));
    }

    private void read(Actor actor) {
        if (!actor.hasPermission("playbook:read") && !actor.hasPermission("playbook:manage")) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：playbook:read");
        }
    }

    private void manage(Actor actor) {
        if (!actor.hasPermission("playbook:manage")) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：playbook:manage");
        }
    }

    private String required(String value, int maxLength, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "不能为空");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "长度超限");
        }
        return trimmed;
    }

    private void journal(Actor actor, String action, Playbook playbook, String eventType) {
        try {
            String snapshot = mapper.writeValueAsString(playbook);
            String operationId = "playbook:" + action + ":" + ids.nextId();
            audit.record(actor, action, "SALES_PLAYBOOK", playbook.id(), operationId, "API", null, "{}", snapshot);
            outbox.append(new DomainEvent(eventType, "SALES_PLAYBOOK", playbook.id(), actor.tenantId(), snapshot, Instant.now()),
                    operationId, actor.userId());
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
