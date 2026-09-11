package com.aicrm.trade.contract.infrastructure;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import com.aicrm.trade.contract.domain.Contract;
import com.aicrm.trade.contract.domain.ContractLine;
import com.aicrm.trade.contract.domain.ContractRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public class JdbcContractRepository implements ContractRepository {
    private final JdbcTemplate jdbc;

    public JdbcContractRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public Contract insert(Contract c, long actorId) {
        jdbc.update("insert into crm_contract (id,tenant_id,contract_no,name,quote_id,quote_version_id,customer_id,currency,subtotal,discount_amount,tax_amount,total_amount,status,effective_from,effective_to,version,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                c.id(), c.tenantId(), c.contractNo(), c.name(), c.quoteId(), c.quoteVersionId(), c.customerId(), c.currency(),
                c.subtotal(), c.discountAmount(), c.taxAmount(), c.totalAmount(), c.status().name(), c.effectiveFrom(), c.effectiveTo(),
                c.version(), actorId, actorId, timestamp(c.createdAt()), timestamp(c.updatedAt()));
        return find(c.tenantId(), c.id()).orElseThrow();
    }

    @Override
    public void insertLine(ContractLine l, long actorId) {
        jdbc.update("insert into crm_contract_line (id,tenant_id,contract_id,line_no,quote_line_id,product_id,product_no_snapshot,sku_snapshot,product_name_snapshot,unit,quantity,list_price,unit_price,discount_rate,tax_rate,line_amount,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                l.id(), l.tenantId(), l.contractId(), l.lineNo(), l.quoteLineId(), l.productId(), l.productNo(), l.sku(), l.productName(),
                l.unit(), l.quantity(), l.listPrice(), l.unitPrice(), l.discountRate(), l.taxRate(), l.lineAmount(), actorId, actorId,
                timestamp(l.createdAt()), timestamp(l.updatedAt()));
    }

    @Override
    public Optional<Contract> find(long tenantId, long contractId) {
        return jdbc.query("select * from crm_contract where tenant_id=? and id=? and deleted_at is null", contractMapper(), tenantId, contractId).stream().findFirst();
    }

    @Override
    public PageResult<Contract> page(Actor actor, long page, long size) {
        StringBuilder where = new StringBuilder(" from crm_contract c join crm_quote q on q.tenant_id=c.tenant_id and q.id=c.quote_id join crm_opportunity o on o.tenant_id=q.tenant_id and o.id=q.opportunity_id where c.tenant_id=? and c.deleted_at is null and q.deleted_at is null and o.deleted_at is null");
        List<Object> args = new ArrayList<>(List.of(actor.tenantId()));
        appendOwnerScope(where, args, actor, "o");
        Long total = jdbc.queryForObject("select count(1)" + where, Long.class, args.toArray());
        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(size);
        pageArgs.add((page - 1) * size);
        List<Contract> records = jdbc.query("select c.*" + where + " order by c.updated_at desc, c.id desc limit ? offset ?", contractMapper(), pageArgs.toArray());
        return new PageResult<>(records, page, size, total == null ? 0 : total);
    }

    @Override
    public List<ContractLine> findLines(long tenantId, long contractId) {
        return jdbc.query("select * from crm_contract_line where tenant_id=? and contract_id=? and deleted_at is null order by line_no", lineMapper(), tenantId, contractId);
    }

    @Override
    public boolean existsForQuoteVersion(long tenantId, long quoteId, long quoteVersionId) {
        Boolean exists = jdbc.query("select exists(select 1 from crm_contract where tenant_id=? and quote_id=? and quote_version_id=? and deleted_at is null)",
                rs -> rs.next() && rs.getBoolean(1), tenantId, quoteId, quoteVersionId);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public boolean transition(long tenantId, long contractId, Contract.Status from, Contract.Status to, long expectedVersion, long actorId) {
        String updates = switch (to) {
            case PENDING_SIGNATURE -> "submitted_at=now()";
            case DRAFT -> "submitted_at=null";
            case SIGNED -> "signed_at=now()";
            case VOIDED -> throw new IllegalArgumentException("作废必须记录原因");
        };
        String sql = "update crm_contract set status=?, " + updates + ", version=version+1, updated_by=?, updated_at=now() "
                + "where tenant_id=? and id=? and status=? and version=? and deleted_at is null";
        return jdbc.update(sql, to.name(), actorId, tenantId, contractId, from.name(), expectedVersion) == 1;
    }

    @Override
    public boolean voidContract(long tenantId, long contractId, Contract.Status from, long expectedVersion, String reason, long actorId) {
        String sql = "update crm_contract set status='VOIDED', voided_at=now(), void_reason=?, version=version+1, updated_by=?, updated_at=now() "
                + "where tenant_id=? and id=? and status=? and version=? and deleted_at is null";
        return jdbc.update(sql, reason, actorId, tenantId, contractId, from.name(), expectedVersion) == 1;
    }

    @Override
    public boolean hasNonCancelledOrder(long tenantId, long contractId) {
        Boolean value = jdbc.query("select exists(select 1 from crm_sales_order where tenant_id=? and contract_id=? and status <> 'CANCELLED' and deleted_at is null)",
                rs -> rs.next() && rs.getBoolean(1), tenantId, contractId);
        return Boolean.TRUE.equals(value);
    }

    private RowMapper<Contract> contractMapper() { return (rs, row) -> new Contract(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("contract_no"),
            rs.getString("name"), rs.getLong("quote_id"), rs.getLong("quote_version_id"), rs.getLong("customer_id"), rs.getString("currency"),
            rs.getBigDecimal("subtotal"), rs.getBigDecimal("discount_amount"), rs.getBigDecimal("tax_amount"), rs.getBigDecimal("total_amount"),
            Contract.Status.valueOf(rs.getString("status")), rs.getObject("effective_from", LocalDate.class), rs.getObject("effective_to", LocalDate.class),
            instant(rs, "submitted_at"), instant(rs, "signed_at"), instant(rs, "voided_at"), rs.getString("void_reason"), rs.getLong("version"),
            instant(rs, "created_at"), instant(rs, "updated_at")); }
    private RowMapper<ContractLine> lineMapper() { return (rs, row) -> new ContractLine(rs.getLong("id"), rs.getLong("tenant_id"), rs.getLong("contract_id"),
            rs.getInt("line_no"), rs.getLong("quote_line_id"), rs.getLong("product_id"), rs.getString("product_no_snapshot"), rs.getString("sku_snapshot"),
            rs.getString("product_name_snapshot"), rs.getString("unit"), rs.getBigDecimal("quantity"), rs.getBigDecimal("list_price"), rs.getBigDecimal("unit_price"),
            rs.getBigDecimal("discount_rate"), rs.getBigDecimal("tax_rate"), rs.getBigDecimal("line_amount"), instant(rs, "created_at"), instant(rs, "updated_at")); }
    private Instant instant(ResultSet rs, String column) throws SQLException { Timestamp value = rs.getTimestamp(column); return value == null ? null : value.toInstant(); }
    private Timestamp timestamp(Instant value) { return value == null ? null : Timestamp.from(value); }

    private void appendOwnerScope(StringBuilder sql, List<Object> args, Actor actor, String alias) {
        if (actor.hasDataScope(DataScope.ALL)) return;
        List<String> scopes = new ArrayList<>();
        if (actor.hasDataScope(DataScope.SELF)) { scopes.add(alias + ".owner_user_id=?"); args.add(actor.userId()); }
        if (actor.hasDataScope(DataScope.DEPARTMENT)) { scopes.add(alias + ".owner_dept_id=?"); args.add(actor.departmentId()); }
        if (actor.hasDataScope(DataScope.DEPARTMENT_AND_SUB)) { scopes.add(alias + ".owner_dept_id in (select id from crm_department where tenant_id=? and path like ? and deleted_at is null)"); args.add(actor.tenantId()); args.add(actor.departmentPath() + "%"); }
        sql.append(scopes.isEmpty() ? " and 1=0" : " and (" + String.join(" or ", scopes) + ")");
    }
}
