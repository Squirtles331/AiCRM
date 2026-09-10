package com.aicrm.sales.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.aicrm.sales.domain.OwnershipType;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.customer.Contact;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.lead.Lead;
import com.aicrm.sales.domain.lead.LeadStatus;
import com.aicrm.sales.domain.pool.PublicPool;
import com.aicrm.sales.domain.channel.AcquisitionChannel;
import com.aicrm.sales.domain.channel.AcquisitionChannelRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/** Transactional use cases for the lead/customer private and public pools. */
@Service
public class SalesCommandService {
    private final SalesRepository repository;
    private final IdGenerator idGenerator;
    private final IdempotencyService idempotencyService;
    private final AuditLogService auditLogService;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final AcquisitionChannelRepository acquisitionChannels;

    public SalesCommandService(SalesRepository repository, IdGenerator idGenerator,
                               IdempotencyService idempotencyService, AuditLogService auditLogService,
                               OutboxService outboxService, ObjectMapper objectMapper, AcquisitionChannelRepository acquisitionChannels) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.idempotencyService = idempotencyService;
        this.auditLogService = auditLogService;
        this.outboxService = outboxService;
        this.objectMapper = objectMapper;
        this.acquisitionChannels = acquisitionChannels;
    }

    @Transactional
    public Lead createLead(Actor actor, SalesCommands.CreateLead command, String idempotencyKey) {
        require(actor, "lead:create");
        return idempotencyService.execute(actor, "lead:create", idempotencyKey, command, Lead.class, () -> {
            OwnershipType ownership = command.publicPoolId() == null ? OwnershipType.PRIVATE : OwnershipType.PUBLIC;
            if (ownership == OwnershipType.PUBLIC) {
                requireActivePool(actor, command.publicPoolId(), PublicPool.ResourceType.LEAD);
            }
            AcquisitionChannel channel = resolveAcquisitionChannel(actor, command.acquisitionChannelId(), command.sourceType());
            Instant now = Instant.now();
            Lead lead = new Lead(idGenerator.nextId(), actor.tenantId(), "LEAD-" + idGenerator.nextId(),
                    required(command.name(), "姓名"), trim(command.mobile()), trim(command.email()), trim(command.companyName()),
                    required(command.sourceType(), "来源"), trim(command.sourceRef()), trim(command.intent()),
                    channel == null ? null : channel.id(), channel == null ? null : channel.code(), LeadStatus.NEW,
                    ownership, ownership == OwnershipType.PRIVATE ? actor.userId() : null, null,
                    command.publicPoolId(), null, ownership == OwnershipType.PUBLIC ? now : null,
                    null, null, 0, now, now);
            lead = repository.insertLead(lead, actor.userId());
            journal(actor, "CREATE", "LEAD", lead.id(), null, lead, null, lead.publicPoolId(), "创建线索", "LeadCreated");
            return lead;
        });
    }

    @Transactional
    public Customer createCustomer(Actor actor, SalesCommands.CreateCustomer command, String idempotencyKey) {
        require(actor, "customer:create");
        return idempotencyService.execute(actor, "customer:create", idempotencyKey, command, Customer.class, () -> {
            OwnershipType ownership = command.publicPoolId() == null ? OwnershipType.PRIVATE : OwnershipType.PUBLIC;
            if (ownership == OwnershipType.PUBLIC) {
                requireActivePool(actor, command.publicPoolId(), PublicPool.ResourceType.CUSTOMER);
            }
            Instant now = Instant.now();
            Customer customer = new Customer(idGenerator.nextId(), actor.tenantId(), "CUS-" + idGenerator.nextId(),
                    required(command.name(), "客户名称"), trim(command.industry()), trim(command.region()), "ACTIVE", ownership,
                    ownership == OwnershipType.PRIVATE ? actor.userId() : null, null, command.publicPoolId(),
                    ownership == OwnershipType.PUBLIC ? now : null, null, null, 0, now, now);
            customer = repository.insertCustomer(customer, actor.userId());
            journal(actor, "CREATE", "CUSTOMER", customer.id(), null, customer, null, customer.publicPoolId(),
                    "创建客户", "CustomerCreated");
            return customer;
        });
    }

    @Transactional
    public Lead claimLead(Actor actor, long leadId, SalesCommands.OwnershipChange command) {
        require(actor, "lead:claim");
        Lead before = lead(actor, leadId);
        requirePublic(before, "线索不可认领");
        requirePoolAction(actor, command.publicPoolId(), PublicPool.ResourceType.LEAD, PoolAction.CLAIM);
        requireCurrentPool(before.publicPoolId(), command.publicPoolId());
        if (command.publicPoolId() == null || !repository.claimLead(actor.tenantId(), leadId, command.publicPoolId(),
                actor.userId(), command.version())) {
            throw conflict("线索已被认领或版本已变化");
        }
        Lead after = lead(actor, leadId);
        journal(actor, "CLAIM", "LEAD", leadId, before, after, before.publicPoolId(), null, command.reason(), "LeadClaimed");
        return after;
    }

    @Transactional
    public Customer claimCustomer(Actor actor, long customerId, SalesCommands.OwnershipChange command) {
        require(actor, "customer:claim");
        Customer before = customer(actor, customerId);
        requirePublic(before, "客户不可认领");
        requirePoolAction(actor, command.publicPoolId(), PublicPool.ResourceType.CUSTOMER, PoolAction.CLAIM);
        requireCurrentPool(before.publicPoolId(), command.publicPoolId());
        if (command.publicPoolId() == null || !repository.claimCustomer(actor.tenantId(), customerId, command.publicPoolId(),
                actor.userId(), command.version())) {
            throw conflict("客户已被认领或版本已变化");
        }
        Customer after = customer(actor, customerId);
        journal(actor, "CLAIM", "CUSTOMER", customerId, before, after, before.publicPoolId(), null, command.reason(), "CustomerClaimed");
        return after;
    }

    @Transactional
    public Lead releaseLead(Actor actor, long leadId, SalesCommands.OwnershipChange command) {
        Lead before = lead(actor, leadId);
        requireLeadWrite(actor, before);
        requireMutable(before);
        requirePoolAction(actor, command.publicPoolId(), PublicPool.ResourceType.LEAD, PoolAction.RELEASE);
        if (command.publicPoolId() == null || !repository.moveLead(actor.tenantId(), leadId, command.version(), before.ownerUserId(),
                actor.userId(), OwnershipType.PUBLIC, null, command.publicPoolId(), null, null)) {
            throw conflict("线索释放失败，记录已变化");
        }
        Lead after = lead(actor, leadId);
        journal(actor, "RELEASE", "LEAD", leadId, before, after, before.publicPoolId(), after.publicPoolId(), command.reason(), "LeadReleased");
        return after;
    }

    @Transactional
    public Customer releaseCustomer(Actor actor, long customerId, SalesCommands.OwnershipChange command) {
        Customer before = customer(actor, customerId);
        requireCustomerWrite(actor, before);
        requirePoolAction(actor, command.publicPoolId(), PublicPool.ResourceType.CUSTOMER, PoolAction.RELEASE);
        if (command.publicPoolId() == null || !repository.moveCustomer(actor.tenantId(), customerId, command.version(), before.ownerUserId(),
                actor.userId(), OwnershipType.PUBLIC, null, command.publicPoolId())) {
            throw conflict("客户释放失败，记录已变化");
        }
        Customer after = customer(actor, customerId);
        journal(actor, "RELEASE", "CUSTOMER", customerId, before, after, before.publicPoolId(), after.publicPoolId(), command.reason(), "CustomerReleased");
        return after;
    }

    @Transactional
    public Lead transferLead(Actor actor, long leadId, SalesCommands.OwnershipChange command, boolean assignedFromPublic) {
        require(actor, assignedFromPublic ? "lead:assign" : "lead:write:any");
        Lead before = lead(actor, leadId);
        if (assignedFromPublic) {
            requirePublic(before, "仅公海线索可分配");
            requirePoolAction(actor, before.publicPoolId(), PublicPool.ResourceType.LEAD, PoolAction.ASSIGN);
        } else {
            requireLeadWrite(actor, before);
            requireMutable(before);
        }
        if (command.targetUserId() == null || !repository.moveLead(actor.tenantId(), leadId, command.version(),
                assignedFromPublic ? null : before.ownerUserId(), actor.userId(), OwnershipType.PRIVATE, command.targetUserId(),
                null, null, null)) {
            throw conflict("线索分配或转移失败，记录已变化");
        }
        Lead after = lead(actor, leadId);
        journal(actor, assignedFromPublic ? "ASSIGN" : "TRANSFER", "LEAD", leadId, before, after,
                before.publicPoolId(), after.publicPoolId(), command.reason(), assignedFromPublic ? "LeadAssigned" : "LeadTransferred");
        return after;
    }

    @Transactional
    public Customer transferCustomer(Actor actor, long customerId, SalesCommands.OwnershipChange command, boolean assignedFromPublic) {
        require(actor, assignedFromPublic ? "customer:assign" : "customer:write:any");
        Customer before = customer(actor, customerId);
        if (assignedFromPublic) {
            requirePublic(before, "仅公海客户可分配");
            requirePoolAction(actor, before.publicPoolId(), PublicPool.ResourceType.CUSTOMER, PoolAction.ASSIGN);
        } else {
            requireCustomerWrite(actor, before);
        }
        if (command.targetUserId() == null || !repository.moveCustomer(actor.tenantId(), customerId, command.version(),
                assignedFromPublic ? null : before.ownerUserId(), actor.userId(), OwnershipType.PRIVATE,
                command.targetUserId(), null)) {
            throw conflict("客户分配或转移失败，记录已变化");
        }
        Customer after = customer(actor, customerId);
        journal(actor, assignedFromPublic ? "ASSIGN" : "TRANSFER", "CUSTOMER", customerId, before, after,
                before.publicPoolId(), after.publicPoolId(), command.reason(), assignedFromPublic ? "CustomerAssigned" : "CustomerTransferred");
        return after;
    }

    @Transactional
    public Lead invalidateLead(Actor actor, long leadId, long version, String reason) {
        Lead before = lead(actor, leadId);
        requireLeadWrite(actor, before);
        requireMutable(before);
        if (!repository.invalidateLead(actor.tenantId(), leadId, version, actor.userId(), required(reason, "无效原因"))) {
            throw conflict("线索无效处理失败，记录已变化");
        }
        Lead after = lead(actor, leadId);
        journal(actor, "INVALIDATE", "LEAD", leadId, before, after, before.publicPoolId(), after.publicPoolId(), reason, "LeadInvalidated");
        return after;
    }

    @Transactional
    public Customer convertLead(Actor actor, SalesCommands.ConvertLead command, String idempotencyKey) {
        return idempotencyService.execute(actor, "lead:convert", idempotencyKey, command, Customer.class, () -> {
            Lead before = lead(actor, command.leadId());
            requireLeadWrite(actor, before);
            requireMutable(before);
            Customer customer = command.existingCustomerId() == null ? createCustomerFromLead(actor, before, command)
                    : customer(actor, command.existingCustomerId());
            if (!repository.moveLead(actor.tenantId(), before.id(), command.version(), before.ownerUserId(), actor.userId(),
                    OwnershipType.PRIVATE, before.ownerUserId(), null, LeadStatus.CONVERTED.name(), customer.id())) {
                throw conflict("线索转换失败，记录已变化");
            }
            Lead after = lead(actor, before.id());
            journal(actor, "CONVERT", "LEAD", before.id(), before, after, null, null, "转换为客户", "LeadConverted");
            return customer;
        });
    }

    @Transactional
    public void mergeCustomers(Actor actor, SalesCommands.MergeCustomers command, String idempotencyKey) {
        idempotencyService.execute(actor, "customer:merge", idempotencyKey, command, MergeResult.class, () -> {
            Customer source = customer(actor, command.sourceCustomerId());
            Customer target = customer(actor, command.targetCustomerId());
            if (source.id() == target.id()) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "合并源与目标不能相同");
            }
            requireCustomerWrite(actor, source);
            requireCustomerWrite(actor, target);
            repository.mergeCustomer(actor.tenantId(), source.id(), target.id(), actor.userId(), command.version());
            journal(actor, "MERGE", "CUSTOMER", source.id(), source, target, source.publicPoolId(), target.publicPoolId(),
                    command.reason(), "CustomerMerged");
            return new MergeResult(source.id(), target.id());
        });
    }

    @Transactional
    public Contact createContact(Actor actor, long customerId, SalesCommands.CreateContact command) {
        Customer customer = customer(actor, customerId);
        requireCustomerWrite(actor, customer);
        return repository.insertContact(new Contact(idGenerator.nextId(), actor.tenantId(), customerId,
                required(command.name(), "联系人姓名"), trim(command.mobile()), trim(command.email()), trim(command.department()),
                trim(command.title()), command.decisionMaker(), 0), actor.userId());
    }

    @Transactional
    public Lead addLeadFollowUp(Actor actor, long leadId, SalesCommands.AddFollowUp command) {
        Lead before = lead(actor, leadId);
        requireLeadWrite(actor, before);
        requireMutable(before);
        if (!repository.updateLeadFollowUp(actor.tenantId(), leadId, command.version(), actor.userId(), command.nextFollowUpAt())) {
            throw conflict("线索跟进失败，记录已变化");
        }
        repository.addFollowUp(idGenerator.nextId(), actor.tenantId(), leadId, null, actor.userId(),
                required(command.channel(), "跟进渠道"), required(command.content(), "跟进内容"), command.nextFollowUpAt());
        Lead after = lead(actor, leadId);
        journal(actor, "STATUS_CHANGE", "LEAD", leadId, before, after, null, null, "记录跟进", "LeadFollowedUp");
        return after;
    }

    @Transactional
    public Customer addCustomerFollowUp(Actor actor, long customerId, SalesCommands.AddFollowUp command) {
        Customer before = customer(actor, customerId);
        requireCustomerWrite(actor, before);
        if (!repository.updateCustomerFollowUp(actor.tenantId(), customerId, command.version(), actor.userId(), command.nextFollowUpAt())) {
            throw conflict("客户跟进失败，记录已变化");
        }
        repository.addFollowUp(idGenerator.nextId(), actor.tenantId(), null, customerId, actor.userId(),
                required(command.channel(), "跟进渠道"), required(command.content(), "跟进内容"), command.nextFollowUpAt());
        Customer after = customer(actor, customerId);
        journal(actor, "STATUS_CHANGE", "CUSTOMER", customerId, before, after, null, null, "记录跟进", "CustomerFollowedUp");
        return after;
    }

    @Transactional
    public Lead handoverLead(Actor actor, SalesCommands.Handover command, String idempotencyKey) {
        require(actor, "lead:handover");
        return idempotencyService.execute(actor, "lead:handover", idempotencyKey, command, Lead.class, () -> {
            Lead before = lead(actor, command.resourceId());
            if (before.ownershipType() != OwnershipType.PRIVATE || before.ownerUserId() == null
                    || before.ownerUserId() != command.fromUserId()) {
                throw new DomainException(ErrorCode.CONFLICT, "线索不属于交接来源人员");
            }
            if (!repository.isDepartmentInActorScope(actor, before.ownerDeptId())) {
                throw new DomainException(ErrorCode.FORBIDDEN, "无权交接该线索");
            }
            requireMutable(before);
            if (!repository.moveLead(actor.tenantId(), before.id(), command.version(), command.fromUserId(), actor.userId(),
                    OwnershipType.PRIVATE, command.toUserId(), null, null, null)) {
                throw conflict("线索交接失败，记录已变化");
            }
            Lead after = lead(actor, before.id());
            String operationId = journal(actor, "HANDOVER", "LEAD", before.id(), before, after, null, null,
                    command.reason(), "ResourcesHandedOver");
            repository.addHandover(idGenerator.nextId(), actor.tenantId(), command.fromUserId(), command.toUserId(),
                    "LEAD", before.id(), operationId, trim(command.reason()), json(before), json(after), null,
                    TraceContext.get(), actor.userId());
            return after;
        });
    }

    @Transactional
    public Customer handoverCustomer(Actor actor, SalesCommands.Handover command, String idempotencyKey) {
        require(actor, "customer:handover");
        return idempotencyService.execute(actor, "customer:handover", idempotencyKey, command, Customer.class, () -> {
            Customer before = customer(actor, command.resourceId());
            if (before.ownershipType() != OwnershipType.PRIVATE || before.ownerUserId() == null
                    || before.ownerUserId() != command.fromUserId()) {
                throw new DomainException(ErrorCode.CONFLICT, "客户不属于交接来源人员");
            }
            if (!repository.isDepartmentInActorScope(actor, before.ownerDeptId())) {
                throw new DomainException(ErrorCode.FORBIDDEN, "无权交接该客户");
            }
            if (!repository.moveCustomer(actor.tenantId(), before.id(), command.version(), command.fromUserId(), actor.userId(),
                    OwnershipType.PRIVATE, command.toUserId(), null)) {
                throw conflict("客户交接失败，记录已变化");
            }
            Customer after = customer(actor, before.id());
            String operationId = journal(actor, "HANDOVER", "CUSTOMER", before.id(), before, after, null, null,
                    command.reason(), "ResourcesHandedOver");
            repository.addHandover(idGenerator.nextId(), actor.tenantId(), command.fromUserId(), command.toUserId(),
                    "CUSTOMER", before.id(), operationId, trim(command.reason()), json(before), json(after), null,
                    TraceContext.get(), actor.userId());
            return after;
        });
    }

    /** Processes one bounded page for a configured automatic-recycle destination pool. */
    @Transactional
    public long recycleExpiredPrivateResources(PublicPool pool, int pageSize) {
        if (!pool.active() || !pool.autoRecycleEnabled() || pool.recycleAfterDays() == null) {
            return 0;
        }
        if (pageSize < 1 || pageSize > 500) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "回收批次大小必须在 1 到 500 之间");
        }
        Actor actor = repository.findAutomationActor(pool.tenantId())
                .orElseThrow(() -> new DomainException(ErrorCode.INTERNAL_ERROR, "租户缺少系统任务操作者"));
        Instant inactiveSince = Instant.now().minus(Duration.ofDays(pool.recycleAfterDays()));
        if (pool.resourceType() == PublicPool.ResourceType.LEAD) {
            long recycled = 0;
            for (Lead before : repository.lockRecyclableLeads(pool, inactiveSince, pageSize)) {
                if (repository.moveLead(pool.tenantId(), before.id(), before.version(), before.ownerUserId(), actor.userId(),
                        OwnershipType.PUBLIC, null, pool.id(), null, null)) {
                    Lead after = lead(actor, before.id());
                    journal(actor, "RECYCLE", "LEAD", before.id(), before, after, null, pool.id(),
                            "超过 " + pool.recycleAfterDays() + " 天未跟进", "LeadRecycled", "SCHEDULER", null);
                    recycled++;
                }
            }
            return recycled;
        }

        long recycled = 0;
        for (Customer before : repository.lockRecyclableCustomers(pool, inactiveSince, pageSize)) {
            if (repository.moveCustomer(pool.tenantId(), before.id(), before.version(), before.ownerUserId(), actor.userId(),
                    OwnershipType.PUBLIC, null, pool.id())) {
                Customer after = customer(actor, before.id());
                journal(actor, "RECYCLE", "CUSTOMER", before.id(), before, after, null, pool.id(),
                        "超过 " + pool.recycleAfterDays() + " 天未跟进", "CustomerRecycled", "SCHEDULER", null);
                recycled++;
            }
        }
        return recycled;
    }

    /** Transfers all transferable private leads and customers from a departing user in one idempotent command. */
    @Transactional
    public BatchHandoverResult handoverAll(Actor actor, SalesCommands.BatchHandover command, String idempotencyKey) {
        require(actor, "lead:handover");
        require(actor, "customer:handover");
        String batchNo = required(command.batchNo(), "批次号");
        if (command.fromUserId() == command.toUserId()) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "交接双方不能相同");
        }
        if (!repository.existsTenantUser(actor.tenantId(), command.fromUserId())) {
            throw new DomainException(ErrorCode.NOT_FOUND, "交接来源用户不存在或不属于当前租户");
        }
        if (!repository.isActiveUser(actor.tenantId(), command.toUserId())) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "交接目标用户不存在、未启用或不属于当前租户");
        }
        int pageSize = command.pageSize() == 0 ? 200 : command.pageSize();
        if (pageSize < 1 || pageSize > 500) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "交接分页大小必须在 1 到 500 之间");
        }
        return idempotencyService.execute(actor, "handover:batch:" + batchNo, idempotencyKey, command, BatchHandoverResult.class,
                () -> executeBatchHandover(actor, command, batchNo, pageSize));
    }

    private BatchHandoverResult executeBatchHandover(Actor actor, SalesCommands.BatchHandover command,
                                                     String batchNo, int pageSize) {
        boolean sourceDeactivated = repository.deactivateUser(actor.tenantId(), command.fromUserId(), actor.userId());
        if (sourceDeactivated) {
            String operationId = "op-" + idGenerator.nextId();
            auditLogService.record(actor, "USER_EXIT_HANDOVER", "USER", command.fromUserId(), operationId,
                    "API", batchNo, "{\"status\":\"ACTIVE\"}", "{\"status\":\"INACTIVE\"}");
        }
        long leadCount = 0;
        List<Lead> leads;
        while (!(leads = repository.lockPrivateLeadsForHandover(actor.tenantId(), command.fromUserId(), pageSize)).isEmpty()) {
            for (Lead before : leads) {
                if (before.ownerDeptId() == null || !repository.isDepartmentInActorScope(actor, before.ownerDeptId())) {
                    throw new DomainException(ErrorCode.FORBIDDEN, "无权交接部分线索");
                }
                if (!repository.moveLead(actor.tenantId(), before.id(), before.version(), command.fromUserId(), actor.userId(),
                        OwnershipType.PRIVATE, command.toUserId(), null, null, null)) {
                    throw conflict("线索交接发生版本冲突");
                }
                Lead after = lead(actor, before.id());
                String operationId = journal(actor, "HANDOVER", "LEAD", before.id(), before, after, null, null,
                        command.reason(), "ResourcesHandedOver", "API", batchNo);
                repository.addHandover(idGenerator.nextId(), actor.tenantId(), command.fromUserId(), command.toUserId(),
                        "LEAD", before.id(), operationId, trim(command.reason()), json(before), json(after), batchNo,
                        TraceContext.get(), actor.userId());
                leadCount++;
            }
        }

        long customerCount = 0;
        List<Customer> customers;
        while (!(customers = repository.lockPrivateCustomersForHandover(actor.tenantId(), command.fromUserId(), pageSize)).isEmpty()) {
            for (Customer before : customers) {
                if (before.ownerDeptId() == null || !repository.isDepartmentInActorScope(actor, before.ownerDeptId())) {
                    throw new DomainException(ErrorCode.FORBIDDEN, "无权交接部分客户");
                }
                if (!repository.moveCustomer(actor.tenantId(), before.id(), before.version(), command.fromUserId(), actor.userId(),
                        OwnershipType.PRIVATE, command.toUserId(), null)) {
                    throw conflict("客户交接发生版本冲突");
                }
                Customer after = customer(actor, before.id());
                String operationId = journal(actor, "HANDOVER", "CUSTOMER", before.id(), before, after, null, null,
                        command.reason(), "ResourcesHandedOver", "API", batchNo);
                repository.addHandover(idGenerator.nextId(), actor.tenantId(), command.fromUserId(), command.toUserId(),
                        "CUSTOMER", before.id(), operationId, trim(command.reason()), json(before), json(after), batchNo,
                        TraceContext.get(), actor.userId());
                customerCount++;
            }
        }

        return new BatchHandoverResult(batchNo, leadCount, customerCount,
                repository.countPrivateLeads(actor.tenantId(), command.fromUserId())
                        + repository.countPrivateCustomers(actor.tenantId(), command.fromUserId()));
    }

    private Customer createCustomerFromLead(Actor actor, Lead lead, SalesCommands.ConvertLead command) {
        Instant now = Instant.now();
        Customer customer = new Customer(idGenerator.nextId(), actor.tenantId(), "CUS-" + idGenerator.nextId(),
                required(command.customerName(), "客户名称"), trim(command.industry()), trim(command.region()), "ACTIVE",
                OwnershipType.PRIVATE, lead.ownerUserId(), lead.ownerDeptId(), null, null, null, null, 0, now, now);
        customer = repository.insertCustomer(customer, actor.userId());
        journal(actor, "CREATE", "CUSTOMER", customer.id(), null, customer, null, null, "线索转换创建客户", "CustomerCreated");
        return customer;
    }

    private Lead lead(Actor actor, long id) {
        return repository.findLead(actor.tenantId(), id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "线索不存在"));
    }

    private AcquisitionChannel resolveAcquisitionChannel(Actor actor, Long channelId, String sourceType) {
        if (channelId == null) {
            return null;
        }
        AcquisitionChannel channel = acquisitionChannels.find(actor.tenantId(), channelId)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "获客渠道不存在"));
        if (!channel.active()) {
            throw new DomainException(ErrorCode.CONFLICT, "获客渠道未启用");
        }
        if (!channel.sourceType().equals(required(sourceType, "来源"))) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "线索来源与获客渠道不一致");
        }
        return channel;
    }

    private Customer customer(Actor actor, long id) {
        return repository.findCustomer(actor.tenantId(), id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "客户不存在"));
    }

    private void requireLeadWrite(Actor actor, Lead lead) {
        requirePrivateWrite(actor, lead.ownerUserId(), lead.ownerDeptId(), "lead:write:own", "lead:write:any");
    }

    private void requireCustomerWrite(Actor actor, Customer customer) {
        requirePrivateWrite(actor, customer.ownerUserId(), customer.ownerDeptId(), "customer:write:own", "customer:write:any");
    }

    private void requirePrivateWrite(Actor actor, Long ownerUserId, Long ownerDeptId, String ownPermission, String anyPermission) {
        if (ownerUserId != null && ownerUserId == actor.userId()) {
            require(actor, ownPermission);
            return;
        }
        require(actor, anyPermission);
        if (ownerDeptId == null || !repository.isDepartmentInActorScope(actor, ownerDeptId)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "无权操作该私海资源");
        }
    }

    private void requireMutable(Lead lead) {
        if (lead.status() == LeadStatus.CONVERTED || lead.status() == LeadStatus.INVALID) {
            throw new DomainException(ErrorCode.CONFLICT, "线索已处于终态");
        }
    }

    private void requirePublic(Lead lead, String message) {
        if (lead.ownershipType() != OwnershipType.PUBLIC || lead.status() == LeadStatus.CONVERTED || lead.status() == LeadStatus.INVALID) {
            throw new DomainException(ErrorCode.CONFLICT, message);
        }
    }

    private void requirePublic(Customer customer, String message) {
        if (customer.ownershipType() != OwnershipType.PUBLIC) {
            throw new DomainException(ErrorCode.CONFLICT, message);
        }
    }

    private PublicPool requireActivePool(Actor actor, Long poolId, PublicPool.ResourceType resourceType) {
        if (poolId == null) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "公海池不能为空");
        }
        PublicPool pool = repository.findPublicPool(actor.tenantId(), poolId)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "公海池不存在"));
        if (!pool.active() || pool.resourceType() != resourceType) {
            throw new DomainException(ErrorCode.CONFLICT, "公海池已停用或资源类型不匹配");
        }
        return pool;
    }

    private void requirePoolAction(Actor actor, Long poolId, PublicPool.ResourceType resourceType, PoolAction action) {
        PublicPool pool = requireActivePool(actor, poolId, resourceType);
        boolean allowed = switch (action) {
            case CLAIM -> pool.claimEnabled();
            case ASSIGN -> pool.assignEnabled();
            case RELEASE -> pool.releaseEnabled();
        };
        if (!allowed) {
            throw new DomainException(ErrorCode.CONFLICT, "公海池未开启" + action.label + "操作");
        }
    }

    private void requireCurrentPool(Long currentPoolId, Long requestedPoolId) {
        if (requestedPoolId == null || !requestedPoolId.equals(currentPoolId)) {
            throw conflict("公海池已变化，请刷新后重试");
        }
    }

    private void require(Actor actor, String permission) {
        if (!actor.hasPermission(permission)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：" + permission);
        }
    }

    private DomainException conflict(String message) {
        return new DomainException(ErrorCode.CONFLICT, message);
    }

    private String required(String value, String field) {
        String normalized = trim(value);
        if (normalized == null) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "不能为空");
        }
        return normalized;
    }

    private String trim(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String journal(Actor actor, String action, String resourceType, long resourceId, Object before, Object after,
                           Long fromPoolId, Long toPoolId, String reason, String eventType) {
        return journal(actor, action, resourceType, resourceId, before, after, fromPoolId, toPoolId, reason, eventType,
                "API", null);
    }

    private String journal(Actor actor, String action, String resourceType, long resourceId, Object before, Object after,
                           Long fromPoolId, Long toPoolId, String reason, String eventType, String source, String batchNo) {
        String operationId = "op-" + idGenerator.nextId();
        String beforeJson = json(before);
        String afterJson = json(after);
        Long fromOwnerId = ownerId(before);
        Long toOwnerId = ownerId(after);
        repository.appendOwnershipHistory(idGenerator.nextId(), actor.tenantId(), resourceType, resourceId, action,
                fromOwnerId, toOwnerId, fromPoolId, toPoolId, operationId, source, trim(reason),
                beforeJson, afterJson, batchNo, TraceContext.get(), actor.userId());
        auditLogService.record(actor, action, resourceType, resourceId, operationId, source, batchNo, beforeJson, afterJson);
        outboxService.append(new DomainEvent(eventType, resourceType, resourceId, actor.tenantId(),
                json(Map.of("resourceId", String.valueOf(resourceId), "action", action)), Instant.now()), operationId, actor.userId());
        return operationId;
    }

    private enum PoolAction {
        CLAIM("认领"), ASSIGN("分配"), RELEASE("释放");

        private final String label;

        PoolAction(String label) {
            this.label = label;
        }
    }

    private Long ownerId(Object value) {
        if (value instanceof Lead lead) {
            return lead.ownerUserId();
        }
        if (value instanceof Customer customer) {
            return customer.ownerUserId();
        }
        return null;
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? Map.of() : value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("无法序列化审计快照", exception);
        }
    }

    private record MergeResult(long sourceCustomerId, long targetCustomerId) {
    }
}
