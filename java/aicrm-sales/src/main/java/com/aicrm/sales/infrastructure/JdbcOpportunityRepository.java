package com.aicrm.sales.infrastructure;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import com.aicrm.sales.domain.opportunity.Opportunity;
import com.aicrm.sales.domain.opportunity.OpportunityRepository;
import com.aicrm.sales.domain.opportunity.OpportunityStageHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** PostgreSQL adapter for the opportunity aggregate. */
@Repository
public class JdbcOpportunityRepository implements OpportunityRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcOpportunityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Opportunity insert(Opportunity value, long actorId) {
        jdbcTemplate.update("insert into crm_opportunity (id, tenant_id, opportunity_no, name, customer_id, contact_id, source_lead_id, "
                        + "stage, status, expected_amount, currency, probability, expected_close_date, owner_user_id, owner_dept_id, "
                        + "version, created_by, updated_by, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                value.id(), value.tenantId(), value.opportunityNo(), value.name(), value.customerId(), value.contactId(), value.sourceLeadId(),
                value.stage().name(), value.status().name(), value.expectedAmount(), value.currency(), value.probability(), value.expectedCloseDate(),
                value.ownerUserId(), value.ownerDeptId(), value.version(), actorId, actorId, timestamp(value.createdAt()), timestamp(value.updatedAt()));
        return find(value.tenantId(), value.id()).orElseThrow();
    }

    @Override
    public Optional<Opportunity> find(long tenantId, long opportunityId) {
        return jdbcTemplate.query("select * from crm_opportunity where tenant_id=? and id=? and deleted_at is null", opportunityMapper(),
                tenantId, opportunityId).stream().findFirst();
    }

    @Override
    public PageResult<Opportunity> page(Actor actor, long page, long size) {
        StringBuilder where = new StringBuilder(" where tenant_id=? and deleted_at is null");
        List<Object> args = new ArrayList<>(List.of(actor.tenantId()));
        appendDataScope(where, args, actor);
        Long total = jdbcTemplate.queryForObject("select count(1) from crm_opportunity" + where, Long.class, args.toArray());
        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(size);
        pageArgs.add((page - 1) * size);
        List<Opportunity> records = jdbcTemplate.query("select * from crm_opportunity" + where
                + " order by updated_at desc, id desc limit ? offset ?", opportunityMapper(), pageArgs.toArray());
        return new PageResult<>(records, page, size, total == null ? 0 : total);
    }

    @Override
    public boolean changeStage(long tenantId, long opportunityId, long expectedVersion, Opportunity.Stage stage,
                               short probability, long actorId) {
        return jdbcTemplate.update("update crm_opportunity set stage=?, probability=?, version=version+1, updated_by=?, updated_at=now() "
                        + "where tenant_id=? and id=? and status='OPEN' and version=? and deleted_at is null",
                stage.name(), probability, actorId, tenantId, opportunityId, expectedVersion) == 1;
    }

    @Override
    public boolean win(long tenantId, long opportunityId, long expectedVersion, long actorId) {
        return jdbcTemplate.update("update crm_opportunity set stage='CLOSED_WON', status='WON', probability=100, won_at=now(), "
                        + "version=version+1, updated_by=?, updated_at=now() where tenant_id=? and id=? and status='OPEN' "
                        + "and version=? and deleted_at is null", actorId, tenantId, opportunityId, expectedVersion) == 1;
    }

    @Override
    public boolean lose(long tenantId, long opportunityId, long expectedVersion, String reason, long actorId) {
        return jdbcTemplate.update("update crm_opportunity set stage='CLOSED_LOST', status='LOST', lost_reason=?, lost_at=now(), "
                        + "version=version+1, updated_by=?, updated_at=now() where tenant_id=? and id=? and status='OPEN' "
                        + "and version=? and deleted_at is null", reason, actorId, tenantId, opportunityId, expectedVersion) == 1;
    }

    @Override
    public boolean restart(long tenantId, long opportunityId, long expectedVersion, long actorId) {
        return jdbcTemplate.update("update crm_opportunity set stage='DISCOVERY', status='OPEN', probability=0, lost_reason=null, "
                        + "lost_at=null, version=version+1, updated_by=?, updated_at=now() where tenant_id=? and id=? "
                        + "and status='LOST' and version=? and deleted_at is null", actorId, tenantId, opportunityId, expectedVersion) == 1;
    }

    @Override
    public void appendHistory(long id, long tenantId, long opportunityId, OpportunityStageHistory.Action action,
                              Opportunity before, Opportunity after, String reason, long actorId) {
        jdbcTemplate.update("insert into crm_opportunity_stage_history (id, tenant_id, opportunity_id, action, from_stage, to_stage, "
                        + "from_status, to_status, from_probability, to_probability, reason, operator_user_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, tenantId, opportunityId, action.name(), before == null ? null : before.stage().name(), after.stage().name(),
                before == null ? null : before.status().name(), after.status().name(), before == null ? null : before.probability(),
                after.probability(), reason, actorId);
    }

    @Override
    public List<OpportunityStageHistory> histories(long tenantId, long opportunityId) {
        return jdbcTemplate.query("select * from crm_opportunity_stage_history where tenant_id=? and opportunity_id=? order by created_at, id",
                historyMapper(), tenantId, opportunityId);
    }

    private void appendDataScope(StringBuilder where, List<Object> args, Actor actor) {
        if (actor.hasDataScope(DataScope.ALL)) {
            return;
        }
        List<String> clauses = new ArrayList<>();
        if (actor.hasDataScope(DataScope.SELF)) {
            clauses.add("owner_user_id=?");
            args.add(actor.userId());
        }
        if (actor.hasDataScope(DataScope.DEPARTMENT)) {
            clauses.add("owner_dept_id=?");
            args.add(actor.departmentId());
        }
        if (actor.hasDataScope(DataScope.DEPARTMENT_AND_SUB)) {
            clauses.add("owner_dept_id in (select id from crm_department where tenant_id=? and path like ? and deleted_at is null)");
            args.add(actor.tenantId());
            args.add(actor.departmentPath() + "%");
        }
        if (clauses.isEmpty()) {
            where.append(" and 1=0");
            return;
        }
        where.append(" and (").append(String.join(" or ", clauses)).append(")");
    }

    private RowMapper<Opportunity> opportunityMapper() {
        return (rs, rowNum) -> new Opportunity(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("opportunity_no"),
                rs.getString("name"), rs.getLong("customer_id"), nullableLong(rs, "contact_id"), nullableLong(rs, "source_lead_id"),
                Opportunity.Stage.valueOf(rs.getString("stage")), Opportunity.Status.valueOf(rs.getString("status")),
                rs.getBigDecimal("expected_amount"), rs.getString("currency"), rs.getShort("probability"),
                rs.getObject("expected_close_date", LocalDate.class), rs.getLong("owner_user_id"), rs.getLong("owner_dept_id"),
                rs.getString("lost_reason"), instant(rs, "lost_at"), instant(rs, "won_at"), rs.getLong("version"),
                instant(rs, "created_at"), instant(rs, "updated_at"));
    }

    private RowMapper<OpportunityStageHistory> historyMapper() {
        return (rs, rowNum) -> new OpportunityStageHistory(rs.getLong("id"), rs.getLong("opportunity_id"),
                OpportunityStageHistory.Action.valueOf(rs.getString("action")), nullableStage(rs, "from_stage"),
                Opportunity.Stage.valueOf(rs.getString("to_stage")), nullableStatus(rs, "from_status"),
                Opportunity.Status.valueOf(rs.getString("to_status")), nullableShort(rs, "from_probability"),
                rs.getShort("to_probability"), rs.getString("reason"), rs.getLong("operator_user_id"), instant(rs, "created_at"));
    }

    private Opportunity.Stage nullableStage(ResultSet rs, String column) throws SQLException {
        String value = rs.getString(column);
        return value == null ? null : Opportunity.Stage.valueOf(value);
    }

    private Opportunity.Status nullableStatus(ResultSet rs, String column) throws SQLException {
        String value = rs.getString(column);
        return value == null ? null : Opportunity.Status.valueOf(value);
    }

    private Long nullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private Short nullableShort(ResultSet rs, String column) throws SQLException {
        short value = rs.getShort(column);
        return rs.wasNull() ? null : value;
    }

    private Instant instant(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value == null ? null : value.toInstant();
    }

    private Timestamp timestamp(Instant value) {
        return value == null ? null : Timestamp.from(value);
    }
}
