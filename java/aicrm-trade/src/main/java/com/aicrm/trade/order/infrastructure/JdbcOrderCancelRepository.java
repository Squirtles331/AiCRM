package com.aicrm.trade.order.infrastructure;

import com.aicrm.trade.order.domain.OrderCancel;
import com.aicrm.trade.order.domain.OrderCancelRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Repository
public class JdbcOrderCancelRepository implements OrderCancelRepository {
    private final JdbcTemplate jdbc;
    public JdbcOrderCancelRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public OrderCancel insert(OrderCancel c, long actorId) {
        jdbc.update("insert into crm_order_cancel (id,tenant_id,order_id,cancel_no,status,reason,request_snapshot,requested_at,version,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?::jsonb,?,?,?,?,?,?)",
                c.id(), c.tenantId(), c.orderId(), c.cancelNo(), c.status().name(), c.reason(), c.requestSnapshot(), timestamp(c.requestedAt()),
                c.version(), actorId, actorId, timestamp(c.createdAt()), timestamp(c.updatedAt()));
        return find(c.tenantId(), c.id()).orElseThrow();
    }

    @Override
    public Optional<OrderCancel> find(long tenantId, long cancellationId) {
        return jdbc.query("select * from crm_order_cancel where tenant_id=? and id=? and deleted_at is null", this::map,
                tenantId, cancellationId).stream().findFirst();
    }

    @Override
    public boolean transition(long tenantId, long cancellationId, OrderCancel.Status from, OrderCancel.Status to,
                              long expectedVersion, String rejectionReason, long actorId) {
        String timestamp = to == OrderCancel.Status.REJECTED ? "rejected_at" : "completed_at";
        String sql = "update crm_order_cancel set status=?, " + timestamp + "=now(), rejection_reason=?, version=version+1, updated_by=?, updated_at=now() "
                + "where tenant_id=? and id=? and status=? and version=? and deleted_at is null";
        return jdbc.update(sql, to.name(), rejectionReason, actorId, tenantId, cancellationId, from.name(), expectedVersion) == 1;
    }

    private OrderCancel map(ResultSet rs, int row) throws SQLException {
        return new OrderCancel(rs.getLong("id"), rs.getLong("tenant_id"), rs.getLong("order_id"), rs.getString("cancel_no"),
                OrderCancel.Status.valueOf(rs.getString("status")), rs.getString("reason"), rs.getString("request_snapshot"),
                instant(rs, "requested_at"), instant(rs, "approved_at"), instant(rs, "rejected_at"), instant(rs, "completed_at"),
                rs.getString("rejection_reason"), rs.getLong("version"), rs.getLong("created_by"), instant(rs, "created_at"), instant(rs, "updated_at"));
    }
    private Instant instant(ResultSet rs, String column) throws SQLException { Timestamp value = rs.getTimestamp(column); return value == null ? null : value.toInstant(); }
    private Timestamp timestamp(Instant value) { return Timestamp.from(value); }
}
