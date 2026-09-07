package com.aicrm.sales.domain;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.sales.domain.customer.Contact;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.lead.Lead;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Sales persistence port. Application services never depend on SQL or Spring JDBC details. */
public interface SalesRepository {
    Lead insertLead(Lead lead, long actorId);
    Optional<Lead> findLead(long tenantId, long leadId);
    PageResult<Lead> pageLeads(Actor actor, OwnershipType ownershipType, long page, long size);
    boolean isDepartmentInActorScope(Actor actor, long departmentId);
    boolean claimLead(long tenantId, long leadId, long poolId, long userId, long expectedVersion);
    boolean moveLead(long tenantId, long leadId, long expectedVersion, Long expectedOwnerId, long actorId,
                     OwnershipType targetType, Long targetUserId, Long targetPoolId, String status, Long customerId);
    boolean invalidateLead(long tenantId, long leadId, long expectedVersion, long actorId, String reason);
    boolean updateLeadFollowUp(long tenantId, long leadId, long expectedVersion, long actorId, Instant nextFollowUpAt);

    Customer insertCustomer(Customer customer, long actorId);
    Optional<Customer> findCustomer(long tenantId, long customerId);
    PageResult<Customer> pageCustomers(Actor actor, OwnershipType ownershipType, long page, long size);
    boolean claimCustomer(long tenantId, long customerId, long poolId, long userId, long expectedVersion);
    boolean moveCustomer(long tenantId, long customerId, long expectedVersion, Long expectedOwnerId, long actorId,
                         OwnershipType targetType, Long targetUserId, Long targetPoolId);
    void mergeCustomer(long tenantId, long sourceCustomerId, long targetCustomerId, long actorId, long expectedVersion);
    boolean updateCustomerFollowUp(long tenantId, long customerId, long expectedVersion, long actorId,
                                   Instant nextFollowUpAt);

    Contact insertContact(Contact contact, long actorId);
    List<Contact> findContacts(long tenantId, long customerId);
    void addFollowUp(long id, long tenantId, Long leadId, Long customerId, long actorId,
                     String channel, String content, Instant nextFollowUpAt);
    void appendOwnershipHistory(long id, long tenantId, String resourceType, long resourceId, String action,
                                Long fromOwnerId, Long toOwnerId, Long fromPoolId, Long toPoolId,
                                String operationId, String source, String reason, String beforeSnapshot,
                                String afterSnapshot, String traceId, long actorId);
    void addHandover(long id, long tenantId, long fromUserId, long toUserId, String resourceType,
                     long resourceId, String operationId, String reason, String beforeSnapshot,
                     String afterSnapshot, String traceId, long actorId);
}
