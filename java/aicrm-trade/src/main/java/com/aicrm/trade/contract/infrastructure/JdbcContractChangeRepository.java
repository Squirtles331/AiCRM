package com.aicrm.trade.contract.infrastructure;

import com.aicrm.trade.contract.domain.ContractChange;
import com.aicrm.trade.contract.domain.ContractChangeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.List;

@Repository
public class JdbcContractChangeRepository implements ContractChangeRepository {
    private final JdbcTemplate jdbc;

    public JdbcContractChangeRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public ContractChange insert(ContractChange c, long actorId) {
        jdbc.update("insert into crm_contract_change (id,tenant_id,contract_id,change_no,status,reason,before_snapshot,after_snapshot,version,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?::jsonb,?::jsonb,?,?,?,?,?)",
                c.id(), c.tenantId(), c.contractId(), c.changeNo(), c.status().name(), c.reason(), c.beforeSnapshot(), c.afterSnapshot(),
                c.version(), actorId, actorId, timestamp(c.createdAt()), timestamp(c.updatedAt()));
        return find(c.tenantId(), c.id()).orElseThrow();
    }

    @Override
    public Optional<ContractChange> find(long tenantId, long changeId) {
        return jdbc.query("select * from crm_contract_change where tenant_id=? and id=? and deleted_at is null", this::map,
                tenantId, changeId).stream().findFirst();
    }

    @Override
    public List<ContractChange> findByContract(long tenantId, long contractId) {
        return jdbc.query("select * from crm_contract_change where tenant_id=? and contract_id=? and deleted_at is null order by created_at desc,id desc",
                this::map, tenantId, contractId);
    }

    @Override
    public boolean transition(long tenantId, long changeId, ContractChange.Status from, ContractChange.Status to,
                              long expectedVersion, String rejectionReason, long actorId) {
        String timestamp = switch (to) {
            case SUBMITTED -> "submitted_at";
            case APPROVED -> "approved_at";
            case REJECTED -> "rejected_at";
            case CANCELLED -> "cancelled_at";
            case DRAFT -> throw new IllegalArgumentException("变更申请不能回到草稿");
        };
        String sql = "update crm_contract_change set status=?, " + timestamp + "=now(), rejection_reason=?, version=version+1, updated_by=?, updated_at=now() "
                + "where tenant_id=? and id=? and status=? and version=? and deleted_at is null";
        return jdbc.update(sql, to.name(), rejectionReason, actorId, tenantId, changeId, from.name(), expectedVersion) == 1;
    }

    private ContractChange map(ResultSet rs, int row) throws SQLException {
        return new ContractChange(rs.getLong("id"), rs.getLong("tenant_id"), rs.getLong("contract_id"), rs.getString("change_no"),
                ContractChange.Status.valueOf(rs.getString("status")), rs.getString("reason"), rs.getString("before_snapshot"),
                rs.getString("after_snapshot"), instant(rs, "submitted_at"), instant(rs, "approved_at"), instant(rs, "rejected_at"),
                instant(rs, "cancelled_at"), rs.getString("rejection_reason"), rs.getLong("version"), rs.getLong("created_by"),
                instant(rs, "created_at"), instant(rs, "updated_at"));
    }
    private Instant instant(ResultSet rs, String column) throws SQLException { Timestamp value = rs.getTimestamp(column); return value == null ? null : value.toInstant(); }
    private Timestamp timestamp(Instant value) { return Timestamp.from(value); }
}
