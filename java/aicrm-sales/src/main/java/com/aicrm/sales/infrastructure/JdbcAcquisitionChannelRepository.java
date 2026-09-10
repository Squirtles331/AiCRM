package com.aicrm.sales.infrastructure;

import com.aicrm.sales.domain.channel.AcquisitionChannel;
import com.aicrm.sales.domain.channel.AcquisitionChannelRepository;
import com.aicrm.sales.domain.channel.AcquisitionChannelStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.List;

@Repository
public class JdbcAcquisitionChannelRepository implements AcquisitionChannelRepository {
    private final JdbcTemplate jdbc;
    public JdbcAcquisitionChannelRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public AcquisitionChannel insert(AcquisitionChannel channel, long actorId) {
        jdbc.update("insert into crm_acquisition_channel (id,tenant_id,code,name,source_type,status,version,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?)",
                channel.id(), channel.tenantId(), channel.code(), channel.name(), channel.sourceType(), channel.status().name(), channel.version(), actorId, actorId,
                Timestamp.from(channel.createdAt()), Timestamp.from(channel.updatedAt()));
        return find(channel.tenantId(), channel.id()).orElseThrow();
    }
    public Optional<AcquisitionChannel> find(long tenantId, long channelId) {
        return jdbc.query("select id,tenant_id,code,name,source_type,status,version,created_at,updated_at from crm_acquisition_channel where tenant_id=? and id=? and deleted_at is null",
                rs -> rs.next() ? Optional.of(new AcquisitionChannel(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("code"), rs.getString("name"), rs.getString("source_type"),
                        AcquisitionChannelStatus.valueOf(rs.getString("status")), rs.getLong("version"), rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant())) : Optional.empty(), tenantId, channelId);
    }
    public List<AcquisitionChannel> list(long tenantId) {
        return jdbc.query("select id,tenant_id,code,name,source_type,status,version,created_at,updated_at from crm_acquisition_channel where tenant_id=? and deleted_at is null order by created_at desc,id desc",
                (rs, rowNum) -> new AcquisitionChannel(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("code"), rs.getString("name"), rs.getString("source_type"),
                        AcquisitionChannelStatus.valueOf(rs.getString("status")), rs.getLong("version"), rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant()), tenantId);
    }
    public boolean changeStatus(long tenantId, long channelId, long expectedVersion, AcquisitionChannelStatus from, AcquisitionChannelStatus to, long actorId) {
        return jdbc.update("update crm_acquisition_channel set status=?,version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and version=? and status=? and deleted_at is null",
                to.name(), actorId, tenantId, channelId, expectedVersion, from.name()) == 1;
    }
}
