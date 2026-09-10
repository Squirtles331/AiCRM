package com.aicrm.analytics.report.infrastructure;

import com.aicrm.analytics.report.domain.SalesFunnelSnapshot;
import com.aicrm.analytics.report.domain.SalesPerformanceReport;
import com.aicrm.analytics.report.domain.SalesReportRepository;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** SQL read model over CRM facts only. No reporting projections or external facts are persisted. */
@Repository
public class JdbcSalesReportRepository implements SalesReportRepository {
    private final JdbcTemplate jdbc;

    public JdbcSalesReportRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public SalesFunnelSnapshot funnel(Actor actor, Instant asOf) {
        StringBuilder sql = new StringBuilder("select o.stage, count(*), coalesce(sum(o.expected_amount),0), coalesce(sum(o.expected_amount * o.probability / 100.0),0) from crm_opportunity o where o.tenant_id=? and o.deleted_at is null and o.status='OPEN'");
        List<Object> args = new ArrayList<>(List.of(actor.tenantId()));
        appendOwnerScope(sql, args, actor, "o");
        sql.append(" group by o.stage order by case o.stage when 'DISCOVERY' then 1 when 'QUALIFICATION' then 2 when 'SOLUTION' then 3 when 'QUOTATION' then 4 when 'NEGOTIATION' then 5 else 99 end");
        List<SalesFunnelSnapshot.Stage> stages = jdbc.query(sql.toString(), (rs, row) -> new SalesFunnelSnapshot.Stage(
                rs.getString(1), rs.getLong(2), rs.getBigDecimal(3), rs.getBigDecimal(4)), args.toArray());
        return new SalesFunnelSnapshot(asOf, stages);
    }

    @Override
    public SalesPerformanceReport performance(Actor actor, LocalDate from, LocalDate to, Instant start, Instant end) {
        Map<Long, Totals> owners = new LinkedHashMap<>();
        aggregateOpportunities(actor, start, end, owners);
        aggregateContracts(actor, start, end, owners);
        aggregateOrders(actor, start, end, owners);
        List<SalesPerformanceReport.Owner> rows = owners.entrySet().stream().map(entry -> {
            Totals totals = entry.getValue();
            return new SalesPerformanceReport.Owner(String.valueOf(entry.getKey()), totals.opportunitiesWon, totals.wonExpectedAmount,
                    totals.contractsSigned, totals.signedContractAmount, totals.ordersConfirmed, totals.confirmedOrderAmount);
        }).toList();
        return new SalesPerformanceReport(from, to, rows);
    }

    private void aggregateOpportunities(Actor actor, Instant start, Instant end, Map<Long, Totals> owners) {
        StringBuilder sql = new StringBuilder("select o.owner_user_id, count(*), coalesce(sum(o.expected_amount),0) from crm_opportunity o where o.tenant_id=? and o.deleted_at is null and o.status='WON' and o.won_at>=? and o.won_at<?");
        List<Object> args = rangeArgs(actor, start, end);
        appendOwnerScope(sql, args, actor, "o");
        sql.append(" group by o.owner_user_id");
        jdbc.query(sql.toString(), rs -> { Totals t = owners.computeIfAbsent(rs.getLong(1), ignored -> new Totals()); t.opportunitiesWon += rs.getLong(2); t.wonExpectedAmount = t.wonExpectedAmount.add(rs.getBigDecimal(3)); }, args.toArray());
    }

    private void aggregateContracts(Actor actor, Instant start, Instant end, Map<Long, Totals> owners) {
        StringBuilder sql = new StringBuilder("select o.owner_user_id, count(*), coalesce(sum(c.total_amount),0) from crm_contract c join crm_quote q on q.tenant_id=c.tenant_id and q.id=c.quote_id join crm_opportunity o on o.tenant_id=q.tenant_id and o.id=q.opportunity_id where c.tenant_id=? and c.deleted_at is null and q.deleted_at is null and o.deleted_at is null and c.status='SIGNED' and c.signed_at>=? and c.signed_at<?");
        List<Object> args = rangeArgs(actor, start, end);
        appendOwnerScope(sql, args, actor, "o");
        sql.append(" group by o.owner_user_id");
        jdbc.query(sql.toString(), rs -> { Totals t = owners.computeIfAbsent(rs.getLong(1), ignored -> new Totals()); t.contractsSigned += rs.getLong(2); t.signedContractAmount = t.signedContractAmount.add(rs.getBigDecimal(3)); }, args.toArray());
    }

    private void aggregateOrders(Actor actor, Instant start, Instant end, Map<Long, Totals> owners) {
        StringBuilder sql = new StringBuilder("select o.owner_user_id, count(*), coalesce(sum(so.total_amount),0) from crm_sales_order so join crm_contract c on c.tenant_id=so.tenant_id and c.id=so.contract_id join crm_quote q on q.tenant_id=c.tenant_id and q.id=c.quote_id join crm_opportunity o on o.tenant_id=q.tenant_id and o.id=q.opportunity_id where so.tenant_id=? and so.deleted_at is null and c.deleted_at is null and q.deleted_at is null and o.deleted_at is null and so.status in ('CONFIRMED','CANCELLING','CLOSED') and so.confirmed_at>=? and so.confirmed_at<?");
        List<Object> args = rangeArgs(actor, start, end);
        appendOwnerScope(sql, args, actor, "o");
        sql.append(" group by o.owner_user_id");
        jdbc.query(sql.toString(), rs -> { Totals t = owners.computeIfAbsent(rs.getLong(1), ignored -> new Totals()); t.ordersConfirmed += rs.getLong(2); t.confirmedOrderAmount = t.confirmedOrderAmount.add(rs.getBigDecimal(3)); }, args.toArray());
    }

    private List<Object> rangeArgs(Actor actor, Instant start, Instant end) {
        return new ArrayList<>(List.of(actor.tenantId(), Timestamp.from(start), Timestamp.from(end)));
    }

    private void appendOwnerScope(StringBuilder sql, List<Object> args, Actor actor, String alias) {
        if (actor.hasDataScope(DataScope.ALL)) return;
        List<String> scopes = new ArrayList<>();
        if (actor.hasDataScope(DataScope.SELF)) { scopes.add(alias + ".owner_user_id=?"); args.add(actor.userId()); }
        if (actor.hasDataScope(DataScope.DEPARTMENT)) { scopes.add(alias + ".owner_dept_id=?"); args.add(actor.departmentId()); }
        if (actor.hasDataScope(DataScope.DEPARTMENT_AND_SUB)) { scopes.add(alias + ".owner_dept_id in (select id from crm_department where tenant_id=? and path like ? and deleted_at is null)"); args.add(actor.tenantId()); args.add(actor.departmentPath() + "%"); }
        sql.append(scopes.isEmpty() ? " and 1=0" : " and (" + String.join(" or ", scopes) + ")");
    }

    private static final class Totals {
        private long opportunitiesWon;
        private BigDecimal wonExpectedAmount = BigDecimal.ZERO;
        private long contractsSigned;
        private BigDecimal signedContractAmount = BigDecimal.ZERO;
        private long ordersConfirmed;
        private BigDecimal confirmedOrderAmount = BigDecimal.ZERO;
    }
}
