package com.aicrm.analytics.infrastructure;

import com.aicrm.analytics.domain.WorkbenchRepository;
import com.aicrm.analytics.domain.WorkbenchSummary;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Read-through reporting adapter; it never creates analytics or external-system facts. */
@Repository
public class JdbcWorkbenchRepository implements WorkbenchRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcWorkbenchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public WorkbenchSummary summary(Actor actor, LocalDate from, LocalDate to, Instant startInclusive, Instant endExclusive) {
        return new WorkbenchSummary(from, to,
                countOwnedResource(actor, "crm_lead", "l", "l.created_at >= ? and l.created_at < ?", startInclusive, endExclusive),
                countOwnedResource(actor, "crm_customer", "c", "c.created_at >= ? and c.created_at < ?", startInclusive, endExclusive),
                countOpportunity(actor, "o.status = 'OPEN'"),
                countOpportunity(actor, "o.status = 'WON' and o.won_at >= ? and o.won_at < ?", startInclusive, endExclusive),
                countOpportunity(actor, "o.status = 'LOST' and o.lost_at >= ? and o.lost_at < ?", startInclusive, endExclusive),
                countQuoteVersion(actor, "qv.submitted_at >= ? and qv.submitted_at < ?", startInclusive, endExclusive),
                countQuoteVersion(actor, "qv.approved_at >= ? and qv.approved_at < ?", startInclusive, endExclusive),
                countContract(actor, "c.signed_at >= ? and c.signed_at < ?", startInclusive, endExclusive),
                countOrder(actor, "so.confirmed_at >= ? and so.confirmed_at < ?", startInclusive, endExclusive),
                countOrder(actor, "so.cancelled_at >= ? and so.cancelled_at < ?", startInclusive, endExclusive),
                countOrder(actor, "so.closed_at >= ? and so.closed_at < ?", startInclusive, endExclusive),
                overdueFollowUps(actor));
    }

    private long countOwnedResource(Actor actor, String table, String alias, String predicate, Instant... range) {
        StringBuilder where = new StringBuilder(" where ").append(alias).append(".tenant_id=? and ")
                .append(alias).append(".deleted_at is null and ").append(predicate);
        List<Object> args = new ArrayList<>();
        args.add(actor.tenantId());
        addRange(args, range);
        appendPublicOrOwnerScope(where, args, actor, alias);
        return count("select count(1) from " + table + " " + alias + where, args);
    }

    private long countOpportunity(Actor actor, String predicate, Instant... range) {
        StringBuilder where = new StringBuilder(" where o.tenant_id=? and o.deleted_at is null and ").append(predicate);
        List<Object> args = new ArrayList<>();
        args.add(actor.tenantId());
        addRange(args, range);
        appendOwnerScope(where, args, actor, "o");
        return count("select count(1) from crm_opportunity o" + where, args);
    }

    private long countQuoteVersion(Actor actor, String predicate, Instant... range) {
        StringBuilder where = new StringBuilder(" where qv.tenant_id=? and qv.deleted_at is null and q.deleted_at is null and o.deleted_at is null and ")
                .append(predicate);
        List<Object> args = new ArrayList<>();
        args.add(actor.tenantId());
        addRange(args, range);
        appendOwnerScope(where, args, actor, "o");
        return count("select count(1) from crm_quote_version qv join crm_quote q on q.tenant_id=qv.tenant_id and q.id=qv.quote_id "
                + "join crm_opportunity o on o.tenant_id=q.tenant_id and o.id=q.opportunity_id" + where, args);
    }

    private long countContract(Actor actor, String predicate, Instant... range) {
        StringBuilder where = new StringBuilder(" where c.tenant_id=? and c.deleted_at is null and q.deleted_at is null and o.deleted_at is null and ")
                .append(predicate);
        List<Object> args = new ArrayList<>();
        args.add(actor.tenantId());
        addRange(args, range);
        appendOwnerScope(where, args, actor, "o");
        return count("select count(1) from crm_contract c join crm_quote q on q.tenant_id=c.tenant_id and q.id=c.quote_id "
                + "join crm_opportunity o on o.tenant_id=q.tenant_id and o.id=q.opportunity_id" + where, args);
    }

    private long countOrder(Actor actor, String predicate, Instant... range) {
        StringBuilder where = new StringBuilder(" where so.tenant_id=? and so.deleted_at is null and c.deleted_at is null and q.deleted_at is null and o.deleted_at is null and ")
                .append(predicate);
        List<Object> args = new ArrayList<>();
        args.add(actor.tenantId());
        addRange(args, range);
        appendOwnerScope(where, args, actor, "o");
        return count("select count(1) from crm_sales_order so join crm_contract c on c.tenant_id=so.tenant_id and c.id=so.contract_id "
                + "join crm_quote q on q.tenant_id=c.tenant_id and q.id=c.quote_id "
                + "join crm_opportunity o on o.tenant_id=q.tenant_id and o.id=q.opportunity_id" + where, args);
    }

    private long overdueFollowUps(Actor actor) {
        return overdueOwnedResource(actor, "crm_lead", "l", "l.status in ('NEW', 'FOLLOWING')")
                + overdueOwnedResource(actor, "crm_customer", "c", "c.status = 'ACTIVE'");
    }

    private long overdueOwnedResource(Actor actor, String table, String alias, String activePredicate) {
        StringBuilder where = new StringBuilder(" where ").append(alias).append(".tenant_id=? and ")
                .append(alias).append(".deleted_at is null and ").append(activePredicate)
                .append(" and ").append(alias).append(".next_follow_up_at is not null and ")
                .append(alias).append(".next_follow_up_at < now()");
        List<Object> args = new ArrayList<>(List.of(actor.tenantId()));
        appendPublicOrOwnerScope(where, args, actor, alias);
        return count("select count(1) from " + table + " " + alias + where, args);
    }

    private void appendPublicOrOwnerScope(StringBuilder where, List<Object> args, Actor actor, String alias) {
        if (actor.hasDataScope(DataScope.ALL)) {
            return;
        }
        List<String> clauses = ownerScopeClauses(args, actor, alias);
        where.append(" and (").append(alias).append(".ownership_type='PUBLIC'");
        if (!clauses.isEmpty()) {
            where.append(" or ").append(String.join(" or ", clauses));
        }
        where.append(")");
    }

    private void appendOwnerScope(StringBuilder where, List<Object> args, Actor actor, String alias) {
        if (actor.hasDataScope(DataScope.ALL)) {
            return;
        }
        List<String> clauses = ownerScopeClauses(args, actor, alias);
        if (clauses.isEmpty()) {
            where.append(" and 1=0");
            return;
        }
        where.append(" and (").append(String.join(" or ", clauses)).append(")");
    }

    private List<String> ownerScopeClauses(List<Object> args, Actor actor, String alias) {
        List<String> clauses = new ArrayList<>();
        if (actor.hasDataScope(DataScope.SELF)) {
            clauses.add(alias + ".owner_user_id=?");
            args.add(actor.userId());
        }
        if (actor.hasDataScope(DataScope.DEPARTMENT)) {
            clauses.add(alias + ".owner_dept_id=?");
            args.add(actor.departmentId());
        }
        if (actor.hasDataScope(DataScope.DEPARTMENT_AND_SUB)) {
            clauses.add(alias + ".owner_dept_id in (select id from crm_department where tenant_id=? and path like ? and deleted_at is null)");
            args.add(actor.tenantId());
            args.add(actor.departmentPath() + "%");
        }
        return clauses;
    }

    private void addRange(List<Object> args, Instant... range) {
        for (Instant value : range) {
            args.add(Timestamp.from(value));
        }
    }

    private long count(String sql, List<Object> args) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class, args.toArray());
        return value == null ? 0 : value;
    }
}
