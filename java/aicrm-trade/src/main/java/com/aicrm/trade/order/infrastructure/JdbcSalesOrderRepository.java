package com.aicrm.trade.order.infrastructure;

import com.aicrm.trade.order.domain.SalesOrder;
import com.aicrm.trade.order.domain.SalesOrderLine;
import com.aicrm.trade.order.domain.SalesOrderRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcSalesOrderRepository implements SalesOrderRepository {
    private final JdbcTemplate jdbc;
    public JdbcSalesOrderRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public SalesOrder insert(SalesOrder o, long actorId) {
        jdbc.update("insert into crm_sales_order (id,tenant_id,order_no,contract_id,customer_id,currency,total_amount,status,expected_delivery_at,version,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                o.id(), o.tenantId(), o.orderNo(), o.contractId(), o.customerId(), o.currency(), o.totalAmount(), o.status().name(), timestamp(o.expectedDeliveryAt()),
                o.version(), actorId, actorId, timestamp(o.createdAt()), timestamp(o.updatedAt()));
        return find(o.tenantId(), o.id()).orElseThrow();
    }
    public void insertLine(SalesOrderLine l, long actorId) {
        jdbc.update("insert into crm_sales_order_line (id,tenant_id,order_id,line_no,contract_line_id,product_id,product_no_snapshot,sku_snapshot,product_name_snapshot,unit,quantity,unit_price,tax_rate,line_amount,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                l.id(), l.tenantId(), l.orderId(), l.lineNo(), l.contractLineId(), l.productId(), l.productNo(), l.sku(), l.productName(), l.unit(),
                l.quantity(), l.unitPrice(), l.taxRate(), l.lineAmount(), actorId, actorId, timestamp(l.createdAt()), timestamp(l.updatedAt()));
    }
    public Optional<SalesOrder> find(long tenantId, long orderId) { return jdbc.query("select * from crm_sales_order where tenant_id=? and id=? and deleted_at is null", orderMapper(), tenantId, orderId).stream().findFirst(); }
    public List<SalesOrderLine> findLines(long tenantId, long orderId) { return jdbc.query("select * from crm_sales_order_line where tenant_id=? and order_id=? and deleted_at is null order by line_no", lineMapper(), tenantId, orderId); }
    public boolean existsForContract(long tenantId, long contractId) { Boolean value = jdbc.query("select exists(select 1 from crm_sales_order where tenant_id=? and contract_id=? and deleted_at is null)", rs -> rs.next() && rs.getBoolean(1), tenantId, contractId); return Boolean.TRUE.equals(value); }
    private RowMapper<SalesOrder> orderMapper() { return (rs, row) -> new SalesOrder(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("order_no"), rs.getLong("contract_id"), rs.getLong("customer_id"), rs.getString("external_order_no"), rs.getString("currency"), rs.getBigDecimal("total_amount"), SalesOrder.Status.valueOf(rs.getString("status")), instant(rs, "expected_delivery_at"), instant(rs, "confirmed_at"), instant(rs, "cancelled_at"), instant(rs, "closed_at"), rs.getLong("version"), instant(rs, "created_at"), instant(rs, "updated_at")); }
    private RowMapper<SalesOrderLine> lineMapper() { return (rs, row) -> new SalesOrderLine(rs.getLong("id"), rs.getLong("tenant_id"), rs.getLong("order_id"), rs.getInt("line_no"), rs.getLong("contract_line_id"), rs.getLong("product_id"), rs.getString("product_no_snapshot"), rs.getString("sku_snapshot"), rs.getString("product_name_snapshot"), rs.getString("unit"), rs.getBigDecimal("quantity"), rs.getBigDecimal("unit_price"), rs.getBigDecimal("tax_rate"), rs.getBigDecimal("line_amount"), instant(rs, "created_at"), instant(rs, "updated_at")); }
    private Instant instant(ResultSet rs, String column) throws SQLException { Timestamp value = rs.getTimestamp(column); return value == null ? null : value.toInstant(); }
    private Timestamp timestamp(Instant value) { return value == null ? null : Timestamp.from(value); }
}
