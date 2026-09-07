package com.aicrm.sales.infrastructure;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import com.aicrm.sales.domain.OwnershipType;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.customer.Contact;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.lead.Lead;
import com.aicrm.sales.domain.lead.LeadStatus;
import com.aicrm.sales.domain.pool.PublicPool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** PostgreSQL adapter for the sales repository port. */
@Repository
public class JdbcSalesRepository implements SalesRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcSalesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Lead insertLead(Lead lead, long actorId) {
        jdbcTemplate.update("insert into crm_lead (id, tenant_id, lead_no, name, mobile, email, company_name, "
                        + "source_type, source_ref, intent, status, ownership_type, owner_user_id, owner_dept_id, "
                        + "public_pool_id, pool_entered_at, version, created_by, updated_by, created_at, updated_at) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                lead.id(), lead.tenantId(), lead.leadNo(), lead.name(), lead.mobile(), lead.email(), lead.companyName(),
                lead.sourceType(), lead.sourceRef(), lead.intent(), lead.status().name(), lead.ownershipType().name(),
                lead.ownerUserId(), lead.ownerDeptId(), lead.publicPoolId(), lead.poolEnteredAt(), lead.version(),
                actorId, actorId, lead.createdAt(), lead.updatedAt());
        return lead;
    }

    @Override
    public Optional<Lead> findLead(long tenantId, long leadId) {
        List<Lead> records = jdbcTemplate.query("select * from crm_lead where tenant_id = ? and id = ? and deleted_at is null",
                leadMapper(), tenantId, leadId);
        return records.stream().findFirst();
    }

    @Override
    public PageResult<Lead> pageLeads(Actor actor, OwnershipType ownershipType, long page, long size) {
        StringBuilder where = new StringBuilder(" where tenant_id = ? and deleted_at is null and ownership_type = ?");
        List<Object> args = new ArrayList<>(List.of(actor.tenantId(), ownershipType.name()));
        appendPrivateDataScope(where, args, actor, ownershipType);
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
    public boolean moveLead(long tenantId, long leadId, long expectedVersion, Long expectedOwnerId, long actorId,
                            OwnershipType targetType, Long targetUserId, Long targetPoolId, String status, Long customerId) {
        return jdbcTemplate.update("update crm_lead set ownership_type = ?, owner_user_id = ?, public_pool_id = ?, "
                        + "pool_entered_at = case when ? = 'PUBLIC' then now() else null end, status = coalesce(?, status), "
                        + "customer_id = coalesce(?, customer_id), version = version + 1, updated_by = ?, updated_at = now() "
                        + "where tenant_id = ? and id = ? and version = ? and deleted_at is null "
                        + "and (? is null or owner_user_id = ?)",
                targetType.name(), targetUserId, targetPoolId, targetType.name(), status, customerId,
                actorId, tenantId, leadId, expectedVersion, expectedOwnerId, expectedOwnerId) == 1;
    }

    @Override
    public boolean invalidateLead(long tenantId, long leadId, long expectedVersion, long actorId, String reason) {
        return jdbcTemplate.update("update crm_lead set status = 'INVALID', invalid_reason = ?, updated_by = ?, "
                        + "updated_at = now(), version = version + 1 where tenant_id = ? and id = ? and version = ? "
                        + "and status in ('NEW', 'FOLLOWING') and deleted_at is null",
                reason, actorId, tenantId, leadId, expectedVersion) == 1;
    }

    @Override
    public boolean updateLeadFollowUp(long tenantId, long leadId, long expectedVersion, long actorId,
                                      Instant nextFollowUpAt) {
        return jdbcTemplate.update("update crm_lead set status = case when status = 'NEW' then 'FOLLOWING' else status end, "
                        + "last_follow_up_at = now(), next_follow_up_at = ?, updated_by = ?, updated_at = now(), "
                        + "version = version + 1 where tenant_id = ? and id = ? and version = ? "
                        + "and status in ('NEW', 'FOLLOWING') and deleted_at is null",
                nextFollowUpAt, actorId, tenantId, leadId, expectedVersion) == 1;
    }

    @Override
    public List<Lead> lockRecyclableLeads(PublicPool pool, Instant inactiveSince, int limit) {
        return jdbcTemplate.query("select * from crm_lead where tenant_id = ? and ownership_type = 'PRIVATE' "
                        + "and status in ('NEW', 'FOLLOWING') and deleted_at is null "
                        + "and coalesce(last_follow_up_at, created_at) <= ? order by id limit ? for update skip locked",
                leadMapper(), pool.tenantId(), inactiveSince, limit);
    }

    @Override
    public List<Lead> lockPrivateLeadsForHandover(long tenantId, long ownerUserId, int limit) {
        return jdbcTemplate.query("select * from crm_lead where tenant_id = ? and ownership_type = 'PRIVATE' "
                        + "and owner_user_id = ? and status in ('NEW', 'FOLLOWING') and deleted_at is null "
                        + "order by id limit ? for update skip locked", leadMapper(), tenantId, ownerUserId, limit);
    }

    @Override
    public long countPrivateLeads(long tenantId, long ownerUserId) {
        return count("select count(1) from crm_lead where tenant_id = ? and ownership_type = 'PRIVATE' "
                + "and owner_user_id = ? and status in ('NEW', 'FOLLOWING') and deleted_at is null", List.of(tenantId, ownerUserId));
    }

    @Override
    public Customer insertCustomer(Customer customer, long actorId) {
        jdbcTemplate.update("insert into crm_customer (id, tenant_id, customer_no, name, industry, region, status, "
                        + "ownership_type, owner_user_id, owner_dept_id, public_pool_id, pool_entered_at, version, "
                        + "created_by, updated_by, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                customer.id(), customer.tenantId(), customer.customerNo(), customer.name(), customer.industry(), customer.region(),
                customer.status(), customer.ownershipType().name(), customer.ownerUserId(), customer.ownerDeptId(),
                customer.publicPoolId(), customer.poolEnteredAt(), customer.version(), actorId,
                actorId, customer.createdAt(), customer.updatedAt());
        return customer;
    }

    @Override
    public Optional<Customer> findCustomer(long tenantId, long customerId) {
        List<Customer> records = jdbcTemplate.query("select * from crm_customer where tenant_id = ? and id = ? and deleted_at is null",
                customerMapper(), tenantId, customerId);
        return records.stream().findFirst();
    }

    @Override
    public PageResult<Customer> pageCustomers(Actor actor, OwnershipType ownershipType, long page, long size) {
        StringBuilder where = new StringBuilder(" where tenant_id = ? and deleted_at is null and ownership_type = ?");
        List<Object> args = new ArrayList<>(List.of(actor.tenantId(), ownershipType.name()));
        appendPrivateDataScope(where, args, actor, ownershipType);
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
    public boolean moveCustomer(long tenantId, long customerId, long expectedVersion, Long expectedOwnerId, long actorId,
                                OwnershipType targetType, Long targetUserId, Long targetPoolId) {
        return jdbcTemplate.update("update crm_customer set ownership_type = ?, owner_user_id = ?, public_pool_id = ?, "
                        + "pool_entered_at = case when ? = 'PUBLIC' then now() else null end, version = version + 1, "
                        + "updated_by = ?, updated_at = now() where tenant_id = ? and id = ? and version = ? "
                        + "and deleted_at is null and (? is null or owner_user_id = ?)",
                targetType.name(), targetUserId, targetPoolId, targetType.name(), actorId,
                tenantId, customerId, expectedVersion, expectedOwnerId, expectedOwnerId) == 1;
    }

    @Override
    public void mergeCustomer(long tenantId, long sourceCustomerId, long targetCustomerId, long actorId, long expectedVersion) {
        int updated = jdbcTemplate.update("update crm_customer set merged_into_customer_id = ?, merged_at = now(), "
                        + "deleted_at = now(), deleted_by = ?, updated_by = ?, updated_at = now(), version = version + 1 "
                        + "where tenant_id = ? and id = ? and version = ? and status = 'ACTIVE' and deleted_at is null",
                targetCustomerId, actorId, actorId, tenantId, sourceCustomerId, expectedVersion);
        if (updated != 1) {
            throw new DomainException(ErrorCode.CONFLICT, "客户已被其他操作修改");
        }
        jdbcTemplate.update("update crm_contact set source_customer_id = coalesce(source_customer_id, customer_id), customer_id = ?, "
                        + "updated_by = ?, updated_at = now(), version = version + 1 where tenant_id = ? and customer_id = ? "
                        + "and deleted_at is null", targetCustomerId, actorId, tenantId, sourceCustomerId);
        jdbcTemplate.update("update crm_follow_up set customer_id = ?, updated_by = ?, updated_at = now(), version = version + 1 "
                + "where tenant_id = ? and customer_id = ? and deleted_at is null", targetCustomerId, actorId, tenantId, sourceCustomerId);
        jdbcTemplate.update("update crm_lead set customer_id = ?, updated_by = ?, updated_at = now(), version = version + 1 "
                + "where tenant_id = ? and customer_id = ? and deleted_at is null", targetCustomerId, actorId, tenantId, sourceCustomerId);
    }

    @Override
    public boolean updateCustomerFollowUp(long tenantId, long customerId, long expectedVersion, long actorId,
                                          Instant nextFollowUpAt) {
        return jdbcTemplate.update("update crm_customer set last_follow_up_at = now(), next_follow_up_at = ?, "
                        + "updated_by = ?, updated_at = now(), version = version + 1 where tenant_id = ? and id = ? "
                        + "and version = ? and status = 'ACTIVE' and deleted_at is null",
                nextFollowUpAt, actorId, tenantId, customerId, expectedVersion) == 1;
    }

    @Override
    public List<Customer> lockRecyclableCustomers(PublicPool pool, Instant inactiveSince, int limit) {
        return jdbcTemplate.query("select * from crm_customer where tenant_id = ? and ownership_type = 'PRIVATE' "
                        + "and status = 'ACTIVE' and deleted_at is null and coalesce(last_follow_up_at, created_at) <= ? "
                        + "order by id limit ? for update skip locked", customerMapper(), pool.tenantId(), inactiveSince, limit);
    }

    @Override
    public List<Customer> lockPrivateCustomersForHandover(long tenantId, long ownerUserId, int limit) {
        return jdbcTemplate.query("select * from crm_customer where tenant_id = ? and ownership_type = 'PRIVATE' "
                        + "and owner_user_id = ? and status = 'ACTIVE' and deleted_at is null order by id limit ? for update skip locked",
                customerMapper(), tenantId, ownerUserId, limit);
    }

    @Override
    public long countPrivateCustomers(long tenantId, long ownerUserId) {
        return count("select count(1) from crm_customer where tenant_id = ? and ownership_type = 'PRIVATE' "
                + "and owner_user_id = ? and deleted_at is null", List.of(tenantId, ownerUserId));
    }

    @Override
    public Contact insertContact(Contact contact, long actorId) {
        jdbcTemplate.update("insert into crm_contact (id, tenant_id, customer_id, name, mobile, email, department, title, "
                        + "is_decision_maker, version, created_by, updated_by) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                contact.id(), contact.tenantId(), contact.customerId(), contact.name(), contact.mobile(), contact.email(),
                contact.department(), contact.title(), contact.decisionMaker(), contact.version(), actorId, actorId);
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
                                       String operationId, String source, String reason, String beforeSnapshot,
                                       String afterSnapshot, String batchNo, String traceId, long actorId) {
        jdbcTemplate.update("insert into crm_ownership_history (id, tenant_id, resource_type, resource_id, action, "
                        + "from_owner_user_id, to_owner_user_id, from_pool_id, to_pool_id, operation_id, source, reason, "
                        + "before_snapshot, after_snapshot, batch_no, trace_id, operator_user_id) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, ?, ?)",
                id, tenantId, resourceType, resourceId, action, fromOwnerId, toOwnerId, fromPoolId, toPoolId,
                operationId, source, reason, beforeSnapshot, afterSnapshot, batchNo, traceId, actorId);
    }

    @Override
    public void addHandover(long id, long tenantId, long fromUserId, long toUserId, String resourceType,
                            long resourceId, String operationId, String reason, String beforeSnapshot,
                            String afterSnapshot, String batchNo, String traceId, long actorId) {
        jdbcTemplate.update("insert into crm_resource_handover (id, tenant_id, from_user_id, to_user_id, resource_type, "
                + "resource_id, operation_id, reason, before_snapshot, after_snapshot, batch_no, trace_id, created_by, updated_by) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, ?, ?, ?)", id, tenantId, fromUserId, toUserId,
                resourceType, resourceId, operationId, reason, beforeSnapshot, afterSnapshot, batchNo, traceId, actorId, actorId);
    }

    @Override
    public boolean isDepartmentInActorScope(Actor actor, long departmentId) {
        if (actor.hasDataScope(DataScope.ALL)) {
            return true;
        }
        if (actor.hasDataScope(DataScope.DEPARTMENT) && departmentId == actor.departmentId()) {
            return true;
        }
        if (!actor.hasDataScope(DataScope.DEPARTMENT_AND_SUB)) {
            return false;
        }
        Boolean visible = jdbcTemplate.query("select exists (select 1 from crm_department "
                        + "where tenant_id = ? and id = ? and path like ? and deleted_at is null)",
                rs -> rs.next() ? rs.getBoolean(1) : false,
                actor.tenantId(), departmentId, actor.departmentPath() + "%");
        return Boolean.TRUE.equals(visible);
    }

    @Override
    public Optional<PublicPool> findPublicPool(long tenantId, long poolId) {
        List<PublicPool> pools = jdbcTemplate.query("select * from crm_public_pool where tenant_id = ? and id = ? and deleted_at is null",
                publicPoolMapper(), tenantId, poolId);
        return pools.stream().findFirst();
    }

    @Override
    public List<PublicPool> findActiveAutoRecyclePools() {
        return jdbcTemplate.query("select * from crm_public_pool where status = 1 and auto_recycle_enabled = true "
                        + "and deleted_at is null order by tenant_id, resource_type, id", publicPoolMapper());
    }

    @Override
    public Optional<Actor> findAutomationActor(long tenantId) {
        List<Actor> actors = jdbcTemplate.query("select u.id as user_id, u.tenant_id, d.id as department_id, d.path "
                        + "from crm_user u join crm_department d on d.id = u.department_id and d.tenant_id = u.tenant_id "
                        + "where u.tenant_id = ? and u.username = '__system__' and u.status = 1 and u.deleted_at is null "
                        + "and d.status = 1 and d.deleted_at is null",
                (rs, rowNum) -> new Actor(rs.getLong("tenant_id"), rs.getLong("user_id"), rs.getLong("department_id"),
                        rs.getString("path"), Set.of("system"), Set.of(), Set.of(DataScope.ALL)), tenantId);
        return actors.stream().findFirst();
    }

    @Override
    public boolean isActiveUser(long tenantId, long userId) {
        Boolean active = jdbcTemplate.query("select exists (select 1 from crm_user where tenant_id = ? and id = ? "
                        + "and status = 1 and deleted_at is null)",
                rs -> rs.next() ? rs.getBoolean(1) : false, tenantId, userId);
        return Boolean.TRUE.equals(active);
    }

    @Override
    public boolean existsTenantUser(long tenantId, long userId) {
        Boolean exists = jdbcTemplate.query("select exists (select 1 from crm_user where tenant_id = ? and id = ? and deleted_at is null)",
                rs -> rs.next() ? rs.getBoolean(1) : false, tenantId, userId);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public boolean deactivateUser(long tenantId, long userId, long actorId) {
        return jdbcTemplate.update("update crm_user set status = 0, updated_by = ?, updated_at = now() "
                        + "where tenant_id = ? and id = ? and status = 1 and deleted_at is null",
                actorId, tenantId, userId) == 1;
    }

    private void appendPrivateDataScope(StringBuilder where, List<Object> args, Actor actor,
                                        OwnershipType ownershipType) {
        if (ownershipType != OwnershipType.PRIVATE || actor.hasDataScope(DataScope.ALL)) {
            return;
        }
        List<String> clauses = new ArrayList<>();
        if (actor.hasDataScope(DataScope.SELF)) {
            clauses.add("owner_user_id = ?");
            args.add(actor.userId());
        }
        if (actor.hasDataScope(DataScope.DEPARTMENT)) {
            clauses.add("owner_dept_id = ?");
            args.add(actor.departmentId());
        }
        if (actor.hasDataScope(DataScope.DEPARTMENT_AND_SUB)) {
            clauses.add("owner_dept_id in (select id from crm_department where tenant_id = ? and path like ? and deleted_at is null)");
            args.add(actor.tenantId());
            args.add(actor.departmentPath() + "%");
        }
        if (clauses.isEmpty()) {
            where.append(" and 1 = 0");
            return;
        }
        where.append(" and (").append(String.join(" or ", clauses)).append(")");
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

    private RowMapper<PublicPool> publicPoolMapper() {
        return (rs, rowNum) -> new PublicPool(rs.getLong("id"), rs.getLong("tenant_id"),
                PublicPool.ResourceType.valueOf(rs.getString("resource_type")), rs.getString("code"), rs.getString("name"),
                rs.getInt("status") == 1, rs.getBoolean("auto_recycle_enabled"), nullableInteger(rs, "recycle_after_days"),
                rs.getBoolean("claim_enabled"), rs.getBoolean("assign_enabled"), rs.getBoolean("release_enabled"),
                rs.getInt("rule_version"), instant(rs, "effective_from"));
    }

    private Long nullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private Integer nullableInteger(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private Instant instant(ResultSet rs, String column) throws SQLException {
        java.sql.Timestamp value = rs.getTimestamp(column);
        return value == null ? null : value.toInstant();
    }
}
