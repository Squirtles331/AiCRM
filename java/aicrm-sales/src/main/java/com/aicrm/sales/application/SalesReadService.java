package com.aicrm.sales.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.sales.domain.OwnershipType;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.customer.Contact;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.lead.Lead;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesReadService {
    private final SalesRepository repository;

    public SalesReadService(SalesRepository repository) {
        this.repository = repository;
    }

    public PageResult<Lead> privateLeads(Actor actor, long page, long size) {
        return repository.pageLeads(actor, OwnershipType.PRIVATE, page, size);
    }

    public PageResult<Lead> publicLeads(Actor actor, long page, long size) {
        return repository.pageLeads(actor, OwnershipType.PUBLIC, page, size);
    }

    public Lead lead(Actor actor, long id) {
        Lead lead = repository.findLead(actor.tenantId(), id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "线索不存在"));
        if (lead.ownershipType() == OwnershipType.PRIVATE && !canAccessPrivate(actor, lead.ownerUserId(), lead.ownerDeptId())) {
            throw new DomainException(ErrorCode.FORBIDDEN, "无权查看该线索");
        }
        return lead;
    }

    public PageResult<Customer> privateCustomers(Actor actor, long page, long size) {
        return repository.pageCustomers(actor, OwnershipType.PRIVATE, page, size);
    }

    public PageResult<Customer> publicCustomers(Actor actor, long page, long size) {
        return repository.pageCustomers(actor, OwnershipType.PUBLIC, page, size);
    }

    public Customer customer(Actor actor, long id) {
        Customer customer = repository.findCustomer(actor.tenantId(), id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "客户不存在"));
        if (customer.ownershipType() == OwnershipType.PRIVATE
                && !canAccessPrivate(actor, customer.ownerUserId(), customer.ownerDeptId())) {
            throw new DomainException(ErrorCode.FORBIDDEN, "无权查看该客户");
        }
        return customer;
    }

    public List<Contact> contacts(Actor actor, long customerId) {
        customer(actor, customerId);
        return repository.findContacts(actor.tenantId(), customerId);
    }

    private boolean canAccessPrivate(Actor actor, Long ownerUserId, Long ownerDeptId) {
        if (ownerUserId != null && ownerUserId == actor.userId()) {
            return true;
        }
        return ownerDeptId != null && repository.isDepartmentInActorScope(actor, ownerDeptId);
    }
}
