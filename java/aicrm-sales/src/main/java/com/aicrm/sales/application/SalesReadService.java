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
        return repository.pageLeads(actor.tenantId(), OwnershipType.PRIVATE,
                actor.hasPermission("lead:read:any") ? null : actor.userId(), page, size);
    }

    public PageResult<Lead> publicLeads(Actor actor, long page, long size) {
        return repository.pageLeads(actor.tenantId(), OwnershipType.PUBLIC, null, page, size);
    }

    public Lead lead(Actor actor, long id) {
        Lead lead = repository.findLead(actor.tenantId(), id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "线索不存在"));
        if (!actor.hasPermission("lead:read:any") && !lead.isPrivateOwnedBy(actor.userId())) {
            throw new DomainException(ErrorCode.FORBIDDEN, "无权查看该线索");
        }
        return lead;
    }

    public PageResult<Customer> privateCustomers(Actor actor, long page, long size) {
        return repository.pageCustomers(actor.tenantId(), OwnershipType.PRIVATE,
                actor.hasPermission("customer:read:any") ? null : actor.userId(), page, size);
    }

    public PageResult<Customer> publicCustomers(Actor actor, long page, long size) {
        return repository.pageCustomers(actor.tenantId(), OwnershipType.PUBLIC, null, page, size);
    }

    public Customer customer(Actor actor, long id) {
        Customer customer = repository.findCustomer(actor.tenantId(), id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "客户不存在"));
        if (!actor.hasPermission("customer:read:any") && !customer.isPrivateOwnedBy(actor.userId())) {
            throw new DomainException(ErrorCode.FORBIDDEN, "无权查看该客户");
        }
        return customer;
    }

    public List<Contact> contacts(Actor actor, long customerId) {
        customer(actor, customerId);
        return repository.findContacts(actor.tenantId(), customerId);
    }
}
