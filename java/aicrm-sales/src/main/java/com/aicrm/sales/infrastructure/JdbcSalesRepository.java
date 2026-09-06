package com.aicrm.sales.infrastructure;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.page.PageResult;
import com.aicrm.sales.domain.OwnershipType;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.customer.Contact;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.lead.Lead;
import com.aicrm.sales.domain.lead.LeadStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** PostgreSQL adapter for the sales repository port. */
@Repository
public class JdbcSalesRepository implements SalesRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcSalesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Lead insertLead(Lead lead) {
        jdbcTemplate.update("insert into crm_lead (id, tenant_id, lead_no, name, mobile, email, company_name, "
                        + "source_type, source_ref, intent, status, ownership_type, owner_user_id, owner_dept_id, "
                        + "public_pool_id, pool_entered_at, version, created_by, updated_by, created_at, updated_at) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                lead.id(), lead.tenantId(), lead.leadNo(), lead.name(), lead.mobile(), lead.email(), lead.companyName(),
                lead.sourceType(), lead.sourceRef(), lead.intent(), lead.status().name(), lead.ownershipType().name(),
                lead.ownerUserId(), lead.ownerDeptId(), lead.publicPoolId(), lead.poolEnteredAt(), lead.version(),
                lead.ownerUserId(), lead.ownerUserId(), lead.createdAt(), lead.updatedAt());
        return lead;
    }

    @Override
    public Optional<Lead> findLead(long tenantId, long leadId) {
        List<Lead> records = jdbcTemplate.query("select * from crm_lead where tenant_id = ? and id = ? and deleted_at is null",
                leadMapper(), tenantId, leadId);
        return records.stream().findFirst();
    }

    @Override
    public PageResult<Lead> pageLeads(long tenantId, OwnershipType ownershipType, Long userId, long page, long size) {
        StringBuilder where = new StringBuilder(" where tenant_id = ? and deleted_at is null and ownership_type = ?");
        List<Object> args = new ArrayList<>(List.of(tenantId, ownershipType.name()));
        if (userId != null) {
            where.append(" and owner_user_id = ?");
            args.add(userId);
        }
        long total = count("select count(1) from crm_lead" + where, args);
        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(size);
        pageArgs.add((page - 1) * size);
        List<Lead> records = jdbcTemplate.query("select * from crm_lead" + where
                        + " order by updated_at desc, id desc limit ? offset ?", leadMapper(), pageArgs.toArray());
        return new PageResult<>(records, page, size, total);
    }

    @Override
    public boolean claimLead(long tenantId, long leadId, long poolId, long userId, long expectedVersion) {
        return jdbcTemplate.update("update crm_lead set ownership_type = 'PRIVATE', owner_user_id = ?, "
                        + "public_pool_id = null, pool_entered_at = null, version = version + 1, updated_by = ?, updated_at = now() "
                        + "where tenant_id = ? and id = ? and public_pool_id = ? and ownership_type = 'PUBLIC' "
                        + "and version = ? and deleted_at is null",
                userId, userId, tenantId, leadId, poolId, expectedVersion) == 1;
    }

    @Override
    public boolean moveLead(long tenantId, long leadId, long expectedVersion, Long expectedOwnerId,
                            OwnershipType targetType, Long targetUserId, Long targetPoolId, String status, Long customerId) {
        return jdbcTemplate.update("update crm_lead set ownership_type = ?, owner_user_id = ?, public_pool_id = ?, "
                        + "pool_entered_at = case when ? = 'PUBLIC' then now() else null end, status = coalesce(?, status), "
                        + "customer_id = coalesce(?, customer_id), version = version + 1, updated_by = ?, updated_at = now() "
                        + "where tenant_id = ? and id = ? and version = ? and deleted_at is null "
                        + "and (? is null or owner_user_id = ?)",
                targetType.name(), targetUserId, targetPoolId, targetType.name(), status, customerId,
                targetUserId, tenantId, leadId, expectedVersion, expectedOwnerId, expectedOwnerId) == 1;
    }

    @Override
    public void updateLeadFollowUp(long tenantId, long leadId, long actorId, Instant nextFollowUpAt) {
        jdbcTemplate.update("update crm_lead set last_follow_up_at = now(), next_follow_up_at = ?, "
                + "updated_by = ?, updated_at = now(), version = version + 1 where tenant_id = ? and id = ? and deleted_at is null",
                nextFollowUpAt, actorId, tenantId, leadId);
    }

    @Override
    public Customer insertCustomer(Customer customer) {
        jdbcTemplate.update("insert into crm_customer (id, tenant_id, customer_no, name, industry, region, status, "
                        + "ownership_type, owner_user_id, owner_dept_id, public_pool_id, pool_entered_at, version, "
                        + "created_by, updated_by, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                customer.id(), customer.tenantId(), customer.customerNo(), customer.name(), customer.industry(), customer.region(),
                customer.status(), customer.ownershipType().name(), customer.ownerUserId(), customer.ownerDeptId(),
                customer.publicPoolId(), customer.poolEnteredAt(), customer.version(), customer.ownerUserId(),
                customer.ownerUserId(), customer.createdAt(), customer.updatedAt());
        return customer;
    }

    @Override
    public Optional<Customer> findCustomer(long tenantId, long customerId) {
        List<Customer> records = jdbcTemplate.query("select * from crm_customer where tenant_id = ? and id = ? and deleted_at is null",
                customerMapper(), tenantId, customerId);
        return records.stream().findFirst();
    }

    @Override
    public PageResult<Customer> pageCustomers(long tenantId, OwnershipType ownershipType, Long userId, long page, long size) {
        StringBuilder where = new StringBuilder(" where tenant_id = ? and deleted_at is null and ownership_type = ?");
        List<Object> args = new ArrayList<>(List.of(tenantId, ownershipType.name()));
        if (userId != null) {
            where.append(" and owner_user_id = ?");
            args.add(userId);
        }
        long total = count("select count(1) from crm_customer" + where, args);
        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(size);
        pageArgs.add((page - 1) * size);
        List<Customer> records = jdbcTemplate.query("select * from crm_customer" + where
                        + " order by updated_at desc, id desc limit ? offset ?", customerMapper(), pageArgs.toArray());
        return new PageResult<>(records, page, size, total);
    }

    @Override
    public boolean claimCustomer(long tenantId, long customerId, long poolId, long userId, long expectedVersion) {
        return jdbcTemplate.update("update crm_customer set ownership_type = 'PRIVATE', owner_user_id = ?, "
                        + "public_pool_id = null, pool_entered_at = null, version = version + 1, updated_by = ?, updated_at = now() "
                        + "where tenant_id = ? and id = ? and public_pool_id = ? and ownership_type = 'PUBLIC' "
                        + "and version = ? and deleted_at is null",
                userId, userId, tenantId, customerId, poolId, expectedVersion) == 1;
    }

    @Override
    public boolean moveCustomer(long tenantId, long customerId, long expectedVersion, Long expectedOwnerId,
                                OwnershipType targetType, Long targetUserId, Long targetPoolId) {
        return jdbcTemplate.update("update crm_customer set ownership_type = ?, owner_user_id = ?, public_pool_id = ?, "
                        + "pool_entered_at = case when ? = 'PUBLIC' then now() else null end, version = version + 1, "
                        + "updated_by = ?, updated_at = now() where tenant_id = ? and id = ? and version = ? "
                        + "and deleted_at is null and (? is null or owner_user_id = ?)",
                targetType.name(), targetUserId, targetPoolId, targetType.name(), targetUserId,
                tenantId, customerId, expectedVersion, expectedOwnerId, expectedOwnerId) == 1;
    }

    @Override
    public void mergeCustomer(long tenantId, long sourceCustomerId, long targetCustomerId, long actorId, long expectedVersion) {
        int updated = jdbcTemplate.update("update crm_customer set deleted_at = now(), deleted_by = ?, updated_by = ?, "
                        + "updated_at = now(), version = version + 1 where tenant_id = ? and id = ? and version = ? and deleted_at is null",
                actorId, actorId, tenantId, sourceCustomerId, expectedVersion);
        if (updated != 1) {
            throw new DomainException(ErrorCode.CONFLICT, "客户已被其他操作修改");
        }
        jdbcTemplate.update("update crm_contact set customer_id = ?, updated_by = ?, updated_at = now(), version = version + 1 "
                + "where tenant_id = ? and customer_id = ? and deleted_at is null", targetCustomerId, actorId, tenantId, sourceCustomerId);
        jdbcTemplate.update("update crm_lead set customer_id = ?, updated_by = ?, updated_at = now(), version = version + 1 "
                + "where tenant_id = ? and customer_id = ? and deleted_at is null", targetCustomerId, actorId, tenantId, sourceCustomerId);
    }

    @Override
    public Contact insertContact(Contact contact) {
        jdbcTemplate.update("insert into crm_contact (id, tenant_id, customer_id, name, mobile, email, department, title, "
                        + "is_decision_maker, version, created_by, updated_by) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                contact.id(), contact.tenantId(), contact.customerId(), contact.name(), contact.mobile(), contact.email(),
                contact.department(), contact.title(), contact.decisionMaker(), contact.version(), null, null);
        return contact;
    }

    @Override
    public List<Contact> findContacts(long tenantId, long customerId) {
        return jdbcTemplate.query("select * from crm_contact where tenant_id = ? and customer_id = ? and deleted_at is null order by id",
                contactMapper(), tenantId, customerId);
    }

    @Override
    public void addFollowUp(long id, long tenantId, Long leadId, Long customerId, long actorId,
                            String channel, String content, Instant nextFollowUpAt) {
        jdbcTemplate.update("insert into crm_follow_up (id, tenant_id, lead_id, customer_id, actor_user_id, channel, content, next_follow_up_at) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?)", id, tenantId, leadId, customerId, actorId, channel, content, nextFollowUpAt);
    }

    @Override
    public void appendOwnershipHistory(long id, long tenantId, String resourceType, long resourceId, String action,
                                       Long fromOwnerId, Long toOwnerId, Long fromPoolId, Long toPoolId,
                                       String reason, long actorId) {
        jdbcTemplate.update("insert into crm_ownership_history (id, tenant_id, resource_type, resource_id, action, "
                        + "from_owner_user_id, to_owner_user_id, from_pool_id, to_pool_id, reason, operator_user_id) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, tenantId, resourceType, resourceId, action, fromOwnerId, toOwnerId, fromPoolId, toPoolId, reason, actorId);
    }

    @Override
    public void addHandover(long id, long tenantId, long fromUserId, long toUserId, String resourceType,
                            long resourceId, long actorId) {
        jdbcTemplate.update("insert into crm_resource_handover (id, tenant_id, from_user_id, to_user_id, resource_type, "
                + "resource_id, created_by) values (?, ?, ?, ?, ?, ?, ?)", id, tenantId, fromUserId, toUserId,
                resourceType, resourceId, actorId);
    }

    private long count(String sql, List<Object> args) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class, args.toArray());
        return result == null ? 0 : result;
    }

    private RowMapper<Lead> leadMapper() {
        return (rs, rowNum) -> new Lead(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("lead_no"),
                rs.getString("name"), rs.getString("mobile"), rs.getString("email"), rs.getString("company_name"),
                rs.getString("source_type"), rs.getString("source_ref"), rs.getString("intent"),
                LeadStatus.valueOf(rs.getString("status")), OwnershipType.valueOf(rs.getString("ownership_type")),
                nullableLong(rs, "owner_user_id"), nullableLong(rs, "owner_dept_id"), nullableLong(rs, "public_pool_id"),
                nullableLong(rs, "customer_id"), instant(rs, "pool_entered_at"), instant(rs, "last_follow_up_at"),
                instant(rs, "next_follow_up_at"), rs.getLong("version"), instant(rs, "created_at"), instant(rs, "updated_at"));
    }

    private RowMapper<Customer> customerMapper() {
        return (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("customer_no"),
                rs.getString("name"), rs.getString("industry"), rs.getString("region"), rs.getString("status"),
                OwnershipType.valueOf(rs.getString("ownership_type")), nullableLong(rs, "owner_user_id"),
                nullableLong(rs, "owner_dept_id"), nullableLong(rs, "public_pool_id"), instant(rs, "pool_entered_at"),
                instant(rs, "last_follow_up_at"), instant(rs, "next_follow_up_at"), rs.getLong("version"),
                instant(rs, "created_at"), instant(rs, "updated_at"));
    }

    private RowMapper<Contact> contactMapper() {
        return (rs, rowNum) -> new Contact(rs.getLong("id"), rs.getLong("tenant_id"), rs.getLong("customer_id"),
                rs.getString("name"), rs.getString("mobile"), rs.getString("email"), rs.getString("department"),
                rs.getString("title"), rs.getBoolean("is_decision_maker"), rs.getLong("version"));
    }

    private Long nullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private Instant instant(ResultSet rs, String column) throws SQLException {
        java.sql.Timestamp value = rs.getTimestamp(column);
        return value == null ? null : value.toInstant();
    }
}
