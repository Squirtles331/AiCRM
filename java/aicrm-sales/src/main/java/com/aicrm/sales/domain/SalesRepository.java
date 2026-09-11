package com.aicrm.sales.domain;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.sales.domain.customer.Contact;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.lead.Lead;
import com.aicrm.sales.domain.pool.PublicPool;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Sales persistence port. Application services never depend on SQL or Spring JDBC details. */
public interface SalesRepository {
    Lead insertLead(Lead lead, long actorId);
    Optional<Lead> findLead(long tenantId, long leadId);
    PageResult<Lead> pageLeads(Actor actor, OwnershipType ownershipType, long page, long size);
    boolean isDepartmentInActorScope(Actor actor, long departmentId);
    Optional<PublicPool> findPublicPool(long tenantId, long poolId);
    List<PublicPool> listActivePublicPools(long tenantId, PublicPool.ResourceType resourceType);
    List<PublicPool> findActiveAutoRecyclePools();
    Optional<Actor> findAutomationActor(long tenantId);
    boolean isActiveUser(long tenantId, long userId);
    boolean existsTenantUser(long tenantId, long userId);
    boolean deactivateUser(long tenantId, long userId, long actorId);
    boolean claimLead(long tenantId, long leadId, long poolId, long userId, long expectedVersion);
    boolean moveLead(long tenantId, long leadId, long expectedVersion, Long expectedOwnerId, long actorId,
                     OwnershipType targetType, Long targetUserId, Long targetPoolId, String status, Long customerId);
    boolean invalidateLead(long tenantId, long leadId, long expectedVersion, long actorId, String reason);
    boolean updateLeadFollowUp(long tenantId, long leadId, long expectedVersion, long actorId, Instant nextFollowUpAt);
    List<Lead> lockRecyclableLeads(PublicPool pool, Instant inactiveSince, int limit);
    List<Lead> lockPrivateLeadsForHandover(long tenantId, long ownerUserId, int limit);
    long countPrivateLeads(long tenantId, long ownerUserId);

    Customer insertCustomer(Customer customer, long actorId);
    Optional<Customer> findCustomer(long tenantId, long customerId);
    PageResult<Customer> pageCustomers(Actor actor, OwnershipType ownershipType, long page, long size);
    boolean claimCustomer(long tenantId, long customerId, long poolId, long userId, long expectedVersion);
    boolean moveCustomer(long tenantId, long customerId, long expectedVersion, Long expectedOwnerId, long actorId,
                         OwnershipType targetType, Long targetUserId, Long targetPoolId);
    void mergeCustomer(long tenantId, long sourceCustomerId, long targetCustomerId, long actorId, long expectedVersion);
    boolean updateCustomerFollowUp(long tenantId, long customerId, long expectedVersion, long actorId,
                                   Instant nextFollowUpAt);
    List<Customer> lockRecyclableCustomers(PublicPool pool, Instant inactiveSince, int limit);
    List<Customer> lockPrivateCustomersForHandover(long tenantId, long ownerUserId, int limit);
    long countPrivateCustomers(long tenantId, long ownerUserId);

    Contact insertContact(Contact contact, long actorId);
    List<Contact> findContacts(long tenantId, long customerId);
    void addFollowUp(long id, long tenantId, Long leadId, Long customerId, long actorId,
                     String channel, String content, Instant nextFollowUpAt);
    void appendOwnershipHistory(long id, long tenantId, String resourceType, long resourceId, String action,
                                Long fromOwnerId, Long toOwnerId, Long fromPoolId, Long toPoolId,
                                String operationId, String source, String reason, String beforeSnapshot,
                                String afterSnapshot, String batchNo, String traceId, long actorId);
    void addHandover(long id, long tenantId, long fromUserId, long toUserId, String resourceType,
                     long resourceId, String operationId, String reason, String beforeSnapshot,
                     String afterSnapshot, String batchNo, String traceId, long actorId);
}
