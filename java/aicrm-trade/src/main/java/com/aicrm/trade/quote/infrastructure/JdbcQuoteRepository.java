package com.aicrm.trade.quote.infrastructure;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import com.aicrm.trade.quote.domain.Quote;
import com.aicrm.trade.quote.domain.QuoteLine;
import com.aicrm.trade.quote.domain.QuoteRepository;
import com.aicrm.trade.quote.domain.QuoteVersion;
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
public class JdbcQuoteRepository implements QuoteRepository {
    private final JdbcTemplate jdbc;

    public JdbcQuoteRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public Quote insertQuote(Quote q, long actorId) {
        jdbc.update("insert into crm_quote (id,tenant_id,quote_no,opportunity_id,customer_id,price_list_id,currency,status,current_version_no,valid_until,version,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                q.id(), q.tenantId(), q.quoteNo(), q.opportunityId(), q.customerId(), q.priceListId(), q.currency(), q.status().name(),
                q.currentVersionNo(), q.validUntil(), q.version(), actorId, actorId, timestamp(q.createdAt()), timestamp(q.updatedAt()));
        return findQuote(q.tenantId(), q.id()).orElseThrow();
    }

    @Override
    public QuoteVersion insertVersion(QuoteVersion v, long actorId) {
        jdbc.update("insert into crm_quote_version (id,tenant_id,quote_id,version_no,status,subtotal,discount_amount,tax_amount,total_amount,discount_rate,rejection_reason,submitted_at,approved_at,rejected_at,expired_at,version,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                v.id(), v.tenantId(), v.quoteId(), v.versionNo(), v.status().name(), v.subtotal(), v.discountAmount(), v.taxAmount(), v.totalAmount(),
                v.discountRate(), v.rejectionReason(), timestamp(v.submittedAt()), timestamp(v.approvedAt()), timestamp(v.rejectedAt()), timestamp(v.expiredAt()),
                v.version(), actorId, actorId, timestamp(v.createdAt()), timestamp(v.updatedAt()));
        return findVersion(v.tenantId(), v.quoteId(), v.versionNo()).orElseThrow();
    }

    @Override
    public QuoteLine insertLine(QuoteLine l, long actorId) {
        jdbc.update("insert into crm_quote_line (id,tenant_id,quote_version_id,line_no,product_id,price_item_id,product_no_snapshot,sku_snapshot,product_name_snapshot,unit,quantity,list_price,minimum_price,unit_price,discount_rate,tax_rate,line_amount,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                l.id(), l.tenantId(), l.quoteVersionId(), l.lineNo(), l.productId(), l.priceItemId(), l.productNo(), l.sku(), l.productName(), l.unit(),
                l.quantity(), l.listPrice(), l.minimumPrice(), l.unitPrice(), l.discountRate(), l.taxRate(), l.lineAmount(), actorId, actorId,
                timestamp(l.createdAt()), timestamp(l.updatedAt()));
        return l;
    }

    @Override
    public Optional<Quote> findQuote(long tenantId, long quoteId) {
        return jdbc.query("select * from crm_quote where tenant_id=? and id=? and deleted_at is null", quoteMapper(), tenantId, quoteId).stream().findFirst();
    }

    @Override
    public PageResult<Quote> page(Actor actor, long page, long size) {
        StringBuilder where = new StringBuilder(" from crm_quote q join crm_opportunity o on o.tenant_id=q.tenant_id and o.id=q.opportunity_id where q.tenant_id=? and q.deleted_at is null and o.deleted_at is null");
        List<Object> args = new ArrayList<>(List.of(actor.tenantId()));
        appendOwnerScope(where, args, actor, "o");
        Long total = jdbc.queryForObject("select count(1)" + where, Long.class, args.toArray());
        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(size);
        pageArgs.add((page - 1) * size);
        List<Quote> records = jdbc.query("select q.*" + where + " order by q.updated_at desc, q.id desc limit ? offset ?", quoteMapper(), pageArgs.toArray());
        return new PageResult<>(records, page, size, total == null ? 0 : total);
    }

    @Override
    public Optional<QuoteVersion> findVersion(long tenantId, long quoteId, int versionNo) {
        return jdbc.query("select * from crm_quote_version where tenant_id=? and quote_id=? and version_no=? and deleted_at is null",
                versionMapper(), tenantId, quoteId, versionNo).stream().findFirst();
    }

    @Override
    public List<QuoteVersion> findVersions(long tenantId, long quoteId) {
        return jdbc.query("select * from crm_quote_version where tenant_id=? and quote_id=? and deleted_at is null order by version_no desc",
                versionMapper(), tenantId, quoteId);
    }

    @Override
    public List<QuoteLine> findLines(long tenantId, long quoteVersionId) {
        return jdbc.query("select * from crm_quote_line where tenant_id=? and quote_version_id=? and deleted_at is null order by line_no",
                lineMapper(), tenantId, quoteVersionId);
    }

    @Override
    public boolean submit(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId) {
        int version = jdbc.update("update crm_quote_version set status='SUBMITTED',submitted_at=now(),version=version+1,updated_by=?,updated_at=now() where tenant_id=? and quote_id=? and version_no=? and status='DRAFT' and version=? and deleted_at is null",
                actorId, tenantId, quoteId, versionNo, expectedCurrentVersionVersion);
        if (version != 1) return false;
        return jdbc.update("update crm_quote set status='SUBMITTED',current_version_no=?,version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status='DRAFT' and version=? and deleted_at is null",
                versionNo, actorId, tenantId, quoteId, expectedRootVersion) == 1;
    }

    @Override
    public boolean approve(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId) {
        int changed = jdbc.update("update crm_quote_version set status='APPROVED',approved_at=now(),version=version+1,updated_by=?,updated_at=now() where tenant_id=? and quote_id=? and version_no=? and status='SUBMITTED' and version=? and deleted_at is null",
                actorId, tenantId, quoteId, versionNo, expectedCurrentVersionVersion);
        if (changed != 1) return false;
        return jdbc.update("update crm_quote set status='APPROVED',version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status='SUBMITTED' and version=? and deleted_at is null",
                actorId, tenantId, quoteId, expectedRootVersion) == 1;
    }

    @Override
    public boolean reject(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, String reason, long actorId) {
        int changed = jdbc.update("update crm_quote_version set status='REJECTED',rejection_reason=?,rejected_at=now(),version=version+1,updated_by=?,updated_at=now() where tenant_id=? and quote_id=? and version_no=? and status='SUBMITTED' and version=? and deleted_at is null",
                reason, actorId, tenantId, quoteId, versionNo, expectedCurrentVersionVersion);
        if (changed != 1) return false;
        return jdbc.update("update crm_quote set status='REJECTED',version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status='SUBMITTED' and version=? and deleted_at is null",
                actorId, tenantId, quoteId, expectedRootVersion) == 1;
    }

    @Override
    public boolean expire(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId) {
        int changed = jdbc.update("update crm_quote_version set status='EXPIRED',expired_at=now(),version=version+1,updated_by=?,updated_at=now() where tenant_id=? and quote_id=? and version_no=? and status in ('SUBMITTED','APPROVED') and version=? and deleted_at is null",
                actorId, tenantId, quoteId, versionNo, expectedCurrentVersionVersion);
        if (changed != 1) return false;
        return jdbc.update("update crm_quote set status='EXPIRED',version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status in ('SUBMITTED','APPROVED') and version=? and deleted_at is null",
                actorId, tenantId, quoteId, expectedRootVersion) == 1;
    }

    @Override
    public boolean withdrawApproval(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId) {
        int changed = jdbc.update("update crm_quote_version set status='DRAFT',submitted_at=null,version=version+1,updated_by=?,updated_at=now() where tenant_id=? and quote_id=? and version_no=? and status='SUBMITTED' and version=? and deleted_at is null",
                actorId, tenantId, quoteId, versionNo, expectedCurrentVersionVersion);
        if (changed != 1) return false;
        return jdbc.update("update crm_quote set status='DRAFT',version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status='SUBMITTED' and version=? and deleted_at is null",
                actorId, tenantId, quoteId, expectedRootVersion) == 1;
    }

    @Override
    public boolean hasQuoteVersion(long tenantId, long quoteId, int versionNo) {
        Boolean result = jdbc.query("select exists(select 1 from crm_quote_version where tenant_id=? and quote_id=? and version_no=? and deleted_at is null)",
                rs -> rs.next() && rs.getBoolean(1), tenantId, quoteId, versionNo);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean addVersion(long tenantId, long quoteId, long expectedQuoteVersion, LocalDate validUntil, QuoteVersion value, long actorId) {
        insertVersion(value, actorId);
        return jdbc.update("update crm_quote set status='DRAFT',current_version_no=?,valid_until=?,version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status='REJECTED' and version=? and deleted_at is null",
                value.versionNo(), validUntil, actorId, tenantId, quoteId, expectedQuoteVersion) == 1;
    }

    private RowMapper<Quote> quoteMapper() { return (rs, row) -> new Quote(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("quote_no"),
            rs.getLong("opportunity_id"), rs.getLong("customer_id"), rs.getLong("price_list_id"), rs.getString("currency"),
            Quote.Status.valueOf(rs.getString("status")), rs.getInt("current_version_no"), rs.getObject("valid_until", LocalDate.class),
            rs.getLong("version"), instant(rs, "created_at"), instant(rs, "updated_at")); }
    private RowMapper<QuoteVersion> versionMapper() { return (rs, row) -> new QuoteVersion(rs.getLong("id"), rs.getLong("tenant_id"), rs.getLong("quote_id"),
            rs.getInt("version_no"), QuoteVersion.Status.valueOf(rs.getString("status")), rs.getBigDecimal("subtotal"), rs.getBigDecimal("discount_amount"),
            rs.getBigDecimal("tax_amount"), rs.getBigDecimal("total_amount"), rs.getBigDecimal("discount_rate"), rs.getString("rejection_reason"),
            instant(rs, "submitted_at"), instant(rs, "approved_at"), instant(rs, "rejected_at"), instant(rs, "expired_at"), rs.getLong("version"),
            instant(rs, "created_at"), instant(rs, "updated_at")); }
    private RowMapper<QuoteLine> lineMapper() { return (rs, row) -> new QuoteLine(rs.getLong("id"), rs.getLong("tenant_id"), rs.getLong("quote_version_id"),
            rs.getInt("line_no"), rs.getLong("product_id"), rs.getLong("price_item_id"), rs.getString("product_no_snapshot"), rs.getString("sku_snapshot"),
            rs.getString("product_name_snapshot"), rs.getString("unit"), rs.getBigDecimal("quantity"), rs.getBigDecimal("list_price"), rs.getBigDecimal("minimum_price"),
            rs.getBigDecimal("unit_price"), rs.getBigDecimal("discount_rate"), rs.getBigDecimal("tax_rate"), rs.getBigDecimal("line_amount"),
            instant(rs, "created_at"), instant(rs, "updated_at")); }
    private Instant instant(ResultSet rs, String name) throws SQLException { Timestamp value = rs.getTimestamp(name); return value == null ? null : value.toInstant(); }
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
