package com.aicrm.sales.domain;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.sales.domain.customer.Contact;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.lead.Lead;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Sales persistence port. Application services never depend on SQL or Spring JDBC details. */
public interface SalesRepository {
    Lead insertLead(Lead lead);
    Optional<Lead> findLead(long tenantId, long leadId);
    PageResult<Lead> pageLeads(long tenantId, OwnershipType ownershipType, Long userId, long page, long size);
    boolean claimLead(long tenantId, long leadId, long poolId, long userId, long expectedVersion);
    boolean moveLead(long tenantId, long leadId, long expectedVersion, Long expectedOwnerId,
                     OwnershipType targetType, Long targetUserId, Long targetPoolId, String status, Long customerId);
    void updateLeadFollowUp(long tenantId, long leadId, long actorId, Instant nextFollowUpAt);

    Customer insertCustomer(Customer customer);
    Optional<Customer> findCustomer(long tenantId, long customerId);
    PageResult<Customer> pageCustomers(long tenantId, OwnershipType ownershipType, Long userId, long page, long size);
    boolean claimCustomer(long tenantId, long customerId, long poolId, long userId, long expectedVersion);
    boolean moveCustomer(long tenantId, long customerId, long expectedVersion, Long expectedOwnerId,
                         OwnershipType targetType, Long targetUserId, Long targetPoolId);
    void mergeCustomer(long tenantId, long sourceCustomerId, long targetCustomerId, long actorId, long expectedVersion);

    Contact insertContact(Contact contact);
    List<Contact> findContacts(long tenantId, long customerId);
    void addFollowUp(long id, long tenantId, Long leadId, Long customerId, long actorId,
                     String channel, String content, Instant nextFollowUpAt);
    void appendOwnershipHistory(long id, long tenantId, String resourceType, long resourceId, String action,
                                Long fromOwnerId, Long toOwnerId, Long fromPoolId, Long toPoolId,
                                String reason, long actorId);
    void addHandover(long id, long tenantId, long fromUserId, long toUserId, String resourceType,
                     long resourceId, long actorId);
}
