package com.aicrm.stagegate;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.aicrm.sales.application.SalesCommandService;
import com.aicrm.sales.application.SalesCommands;
import com.aicrm.sales.domain.OwnershipType;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.channel.AcquisitionChannel;
import com.aicrm.sales.domain.channel.AcquisitionChannelRepository;
import com.aicrm.sales.domain.channel.AcquisitionChannelStatus;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.lead.Lead;
import com.aicrm.sales.domain.lead.LeadStatus;
import com.aicrm.sales.domain.pool.PublicPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Service-level rules that must remain true independently of HTTP mapping.
 * The idempotency port executes the supplied command so these tests cover the
 * same command body that runs after an idempotency record is acquired.
 */
class SalesCommandServiceRulesTest {
    private static final long TENANT = 71L;
    private static final long ACTOR = 701L;
    private static final long OTHER_USER = 702L;
    private static final long POOL = 703L;

    private SalesRepository repository;
    private AcquisitionChannelRepository channels;
    private AuditLogService audit;
    private OutboxService outbox;
    private SalesCommandService service;
    private Actor actor;

    @BeforeEach
    void setUp() {
        repository = mock(SalesRepository.class);
        channels = mock(AcquisitionChannelRepository.class);
        IdempotencyService idempotency = mock(IdempotencyService.class);
        audit = mock(AuditLogService.class);
        outbox = mock(OutboxService.class);
        AtomicLong ids = new AtomicLong(10_000L);
        IdGenerator idGenerator = ids::incrementAndGet;
        service = new SalesCommandService(repository, idGenerator, idempotency, audit, outbox,
                new ObjectMapper().findAndRegisterModules(), channels);
        actor = actor("lead:create", "customer:create", "lead:claim", "customer:claim", "lead:assign", "customer:assign",
                "lead:write:own", "lead:write:any", "customer:write:own", "customer:write:any", "lead:handover", "customer:handover");

        doAnswer(invocation -> ((Supplier<?>) invocation.getArgument(5)).get())
                .when(idempotency).execute(any(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void createsPublicLeadWithActiveChannelAndImmutableJournal() {
        PublicPool pool = pool(PublicPool.ResourceType.LEAD, true, true, true, true);
        AcquisitionChannel channel = new AcquisitionChannel(801L, TENANT, "FORM_WEB", "官网表单", "FORM",
                AcquisitionChannelStatus.ACTIVE, 0L, Instant.now(), Instant.now());
        when(repository.findPublicPool(TENANT, POOL)).thenReturn(Optional.of(pool));
        when(channels.find(TENANT, channel.id())).thenReturn(Optional.of(channel));
        when(repository.insertLead(any(), anyLong())).thenAnswer(invocation -> invocation.getArgument(0));

        Lead created = service.createLead(actor, new SalesCommands.CreateLead("  新线索  ", "13800000000", null,
                "示例公司", "FORM", null, null, POOL, channel.id()), "lead-create-1");

        assertThat(created.name()).isEqualTo("新线索");
        assertThat(created.ownershipType()).isEqualTo(OwnershipType.PUBLIC);
        assertThat(created.ownerUserId()).isNull();
        assertThat(created.publicPoolId()).isEqualTo(POOL);
        assertThat(created.acquisitionChannelCode()).isEqualTo("FORM_WEB");
        verify(repository).appendOwnershipHistory(anyLong(), anyLong(), anyString(), anyLong(), anyString(), any(), any(), any(), any(), anyString(), anyString(), any(), anyString(), anyString(), any(), any(), anyLong());
        verify(audit).record(any(), anyString(), anyString(), anyLong(), anyString(), anyString(), any(), anyString(), anyString());
        verify(outbox).append(any(), anyString(), anyLong());
    }

    @Test
    void rejectsInactiveOrMismatchedAcquisitionChannelBeforeCreatingLead() {
        AcquisitionChannel inactive = new AcquisitionChannel(801L, TENANT, "FORM_WEB", "官网表单", "FORM",
                AcquisitionChannelStatus.DISABLED, 0L, Instant.now(), Instant.now());
        when(channels.find(TENANT, inactive.id())).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> service.createLead(actor, new SalesCommands.CreateLead("线索", null, null, null,
                "FORM", null, null, null, inactive.id()), "lead-channel-disabled"))
                .isInstanceOf(DomainException.class).hasMessageContaining("未启用");

        AcquisitionChannel active = new AcquisitionChannel(802L, TENANT, "FORM_WEB", "官网表单", "FORM",
                AcquisitionChannelStatus.ACTIVE, 0L, Instant.now(), Instant.now());
        when(channels.find(TENANT, active.id())).thenReturn(Optional.of(active));
        assertThatThrownBy(() -> service.createLead(actor, new SalesCommands.CreateLead("线索", null, null, null,
                "REFERRAL", null, null, null, active.id()), "lead-channel-source"))
                .isInstanceOf(DomainException.class).hasMessageContaining("来源与获客渠道不一致");
    }

    @Test
    void createsAndClaimsPublicCustomerThenRejectsStaleOrPrivateClaim() {
        PublicPool pool = pool(PublicPool.ResourceType.CUSTOMER, true, true, true, true);
        Customer publicCustomer = customer(901L, OwnershipType.PUBLIC, null, null, POOL, 0L);
        Customer privateCustomer = customer(901L, OwnershipType.PRIVATE, ACTOR, 7001L, null, 1L);
        when(repository.findPublicPool(TENANT, POOL)).thenReturn(Optional.of(pool));
        when(repository.insertCustomer(any(), anyLong())).thenAnswer(invocation -> invocation.getArgument(0));
        Customer created = service.createCustomer(actor, new SalesCommands.CreateCustomer("公海客户", "软件", "上海", POOL), "customer-create-1");
        assertThat(created.ownershipType()).isEqualTo(OwnershipType.PUBLIC);

        when(repository.findCustomer(TENANT, publicCustomer.id())).thenReturn(Optional.of(publicCustomer), Optional.of(privateCustomer));
        when(repository.claimCustomer(TENANT, publicCustomer.id(), POOL, ACTOR, 0L)).thenReturn(true);
        Customer claimed = service.claimCustomer(actor, publicCustomer.id(), new SalesCommands.OwnershipChange(publicCustomer.id(), 0L, null, POOL, "主动认领"));
        assertThat(claimed.ownershipType()).isEqualTo(OwnershipType.PRIVATE);
        assertThat(claimed.ownerUserId()).isEqualTo(ACTOR);

        when(repository.findCustomer(TENANT, privateCustomer.id())).thenReturn(Optional.of(privateCustomer));
        assertThatThrownBy(() -> service.claimCustomer(actor, privateCustomer.id(), new SalesCommands.OwnershipChange(privateCustomer.id(), 1L, null, POOL, "重复认领")))
                .isInstanceOf(DomainException.class).hasMessageContaining("不可认领");
    }

    @Test
    void releaseAndAssignLeadUsePoolRulesAndOptimisticVersion() {
        PublicPool pool = pool(PublicPool.ResourceType.LEAD, true, true, true, true);
        Lead privateLead = lead(1001L, LeadStatus.NEW, OwnershipType.PRIVATE, ACTOR, 7001L, null, 0L);
        Lead publicLead = lead(1001L, LeadStatus.NEW, OwnershipType.PUBLIC, null, null, POOL, 1L);
        Lead assignedLead = lead(1001L, LeadStatus.NEW, OwnershipType.PRIVATE, OTHER_USER, 7002L, null, 2L);
        when(repository.findPublicPool(TENANT, POOL)).thenReturn(Optional.of(pool));
        when(repository.findLead(TENANT, privateLead.id())).thenReturn(Optional.of(privateLead), Optional.of(publicLead),
                Optional.of(publicLead), Optional.of(assignedLead), Optional.of(publicLead));
        when(repository.moveLead(TENANT, privateLead.id(), 0L, ACTOR, ACTOR, OwnershipType.PUBLIC, null, POOL, null, null)).thenReturn(true);

        Lead released = service.releaseLead(actor, privateLead.id(), new SalesCommands.OwnershipChange(privateLead.id(), 0L, null, POOL, "释放"));
        assertThat(released.ownershipType()).isEqualTo(OwnershipType.PUBLIC);

        when(repository.moveLead(TENANT, publicLead.id(), 1L, null, ACTOR, OwnershipType.PRIVATE, OTHER_USER, null, null, null)).thenReturn(true);
        Lead assigned = service.transferLead(actor, publicLead.id(), new SalesCommands.OwnershipChange(publicLead.id(), 1L, OTHER_USER, POOL, "分配"), true);
        assertThat(assigned.ownerUserId()).isEqualTo(OTHER_USER);

        assertThatThrownBy(() -> service.transferLead(actor, publicLead.id(), new SalesCommands.OwnershipChange(publicLead.id(), 2L, null, POOL, "缺少目标"), true))
                .isInstanceOf(DomainException.class).hasMessageContaining("分配或转移失败");
    }

    @Test
    void rejectsTerminalLeadAndInvalidPoolRulesBeforeMutating() {
        Lead converted = lead(1101L, LeadStatus.CONVERTED, OwnershipType.PRIVATE, ACTOR, 7001L, null, 3L);
        when(repository.findLead(TENANT, converted.id())).thenReturn(Optional.of(converted));
        assertThatThrownBy(() -> service.invalidateLead(actor, converted.id(), 3L, "重复无效"))
                .isInstanceOf(DomainException.class).hasMessageContaining("终态");

        Lead publicLead = lead(1102L, LeadStatus.NEW, OwnershipType.PUBLIC, null, null, POOL, 0L);
        PublicPool noClaim = pool(PublicPool.ResourceType.LEAD, true, false, true, true);
        when(repository.findLead(TENANT, publicLead.id())).thenReturn(Optional.of(publicLead));
        when(repository.findPublicPool(TENANT, POOL)).thenReturn(Optional.of(noClaim));
        assertThatThrownBy(() -> service.claimLead(actor, publicLead.id(), new SalesCommands.OwnershipChange(publicLead.id(), 0L, null, POOL, "认领")))
                .isInstanceOf(DomainException.class).hasMessageContaining("未开启认领");
    }

    @Test
    void followUpAndContactRequirePrivateOwnershipAndRecordLifecycle() {
        Customer customer = customer(1201L, OwnershipType.PRIVATE, ACTOR, 7001L, null, 0L);
        Customer followed = customer(1201L, OwnershipType.PRIVATE, ACTOR, 7001L, null, 1L);
        Lead lead = lead(1202L, LeadStatus.NEW, OwnershipType.PRIVATE, ACTOR, 7001L, null, 0L);
        Lead followedLead = lead(1202L, LeadStatus.NEW, OwnershipType.PRIVATE, ACTOR, 7001L, null, 1L);
        when(repository.findCustomer(TENANT, customer.id())).thenReturn(Optional.of(customer), Optional.of(followed));
        when(repository.findLead(TENANT, lead.id())).thenReturn(Optional.of(lead), Optional.of(followedLead));
        when(repository.insertContact(any(), anyLong())).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.updateCustomerFollowUp(TENANT, customer.id(), 0L, ACTOR, null)).thenReturn(true);
        when(repository.updateLeadFollowUp(TENANT, lead.id(), 0L, ACTOR, null)).thenReturn(true);

        assertThat(service.createContact(actor, customer.id(), new SalesCommands.CreateContact("联系人", null, null, "销售", "经理", true)).customerId())
                .isEqualTo(customer.id());
        assertThat(service.addCustomerFollowUp(actor, customer.id(), new SalesCommands.AddFollowUp(0L, "CALL", "已沟通", null)).version()).isEqualTo(1L);
        assertThat(service.addLeadFollowUp(actor, lead.id(), new SalesCommands.AddFollowUp(0L, "CALL", "已沟通", null)).version()).isEqualTo(1L);
        verify(repository, times(2)).addFollowUp(anyLong(), anyLong(), any(), any(), anyLong(), anyString(), anyString(), any());
    }

    @Test
    void convertsLeadToExistingCustomerAndRejectsStaleConversion() {
        Lead before = lead(1301L, LeadStatus.NEW, OwnershipType.PRIVATE, ACTOR, 7001L, null, 0L);
        Lead converted = new Lead(before.id(), before.tenantId(), before.leadNo(), before.name(), before.mobile(), before.email(),
                before.companyName(), before.sourceType(), before.sourceRef(), before.intent(), before.acquisitionChannelId(),
                before.acquisitionChannelCode(), LeadStatus.CONVERTED, OwnershipType.PRIVATE, ACTOR, 7001L, null, 1302L,
                null, null, null, 1L, before.createdAt(), Instant.now());
        Customer existing = customer(1302L, OwnershipType.PRIVATE, ACTOR, 7001L, null, 0L);
        when(repository.findLead(TENANT, before.id())).thenReturn(Optional.of(before), Optional.of(converted));
        when(repository.findCustomer(TENANT, existing.id())).thenReturn(Optional.of(existing));
        when(repository.moveLead(TENANT, before.id(), 0L, ACTOR, ACTOR, OwnershipType.PRIVATE, ACTOR, null,
                LeadStatus.CONVERTED.name(), existing.id())).thenReturn(true);

        Customer result = service.convertLead(actor, new SalesCommands.ConvertLead(before.id(), 0L, existing.id(), null, null, null),
                "convert-existing-customer");
        assertThat(result.id()).isEqualTo(existing.id());
        verify(repository).moveLead(TENANT, before.id(), 0L, ACTOR, ACTOR, OwnershipType.PRIVATE, ACTOR, null,
                LeadStatus.CONVERTED.name(), existing.id());

        when(repository.findLead(TENANT, before.id())).thenReturn(Optional.of(converted));
        assertThatThrownBy(() -> service.convertLead(actor,
                new SalesCommands.ConvertLead(before.id(), 1L, existing.id(), null, null, null), "convert-terminal"))
                .isInstanceOf(DomainException.class).hasMessageContaining("终态");
    }

    @Test
    void mergesDistinctCustomersAndRejectsSelfMerge() {
        Customer source = customer(1401L, OwnershipType.PRIVATE, ACTOR, 7001L, null, 0L);
        Customer target = customer(1402L, OwnershipType.PRIVATE, ACTOR, 7001L, null, 0L);
        when(repository.findCustomer(TENANT, source.id())).thenReturn(Optional.of(source));
        when(repository.findCustomer(TENANT, target.id())).thenReturn(Optional.of(target));

        service.mergeCustomers(actor, new SalesCommands.MergeCustomers(source.id(), target.id(), 0L, "去重合并"), "merge-customers");
        verify(repository).mergeCustomer(TENANT, source.id(), target.id(), ACTOR, 0L);

        assertThatThrownBy(() -> service.mergeCustomers(actor,
                new SalesCommands.MergeCustomers(source.id(), source.id(), 0L, "错误合并"), "merge-self"))
                .isInstanceOf(DomainException.class).hasMessageContaining("不能相同");
    }

    @Test
    void handsOverPrivateLeadAndCustomerWithImmutableHandoverRecords() {
        Lead beforeLead = lead(1501L, LeadStatus.NEW, OwnershipType.PRIVATE, OTHER_USER, 7002L, null, 0L);
        Lead afterLead = lead(1501L, LeadStatus.NEW, OwnershipType.PRIVATE, ACTOR, 7001L, null, 1L);
        Customer beforeCustomer = customer(1502L, OwnershipType.PRIVATE, OTHER_USER, 7002L, null, 0L);
        Customer afterCustomer = customer(1502L, OwnershipType.PRIVATE, ACTOR, 7001L, null, 1L);
        when(repository.isDepartmentInActorScope(actor, 7002L)).thenReturn(true);
        when(repository.findLead(TENANT, beforeLead.id())).thenReturn(Optional.of(beforeLead), Optional.of(afterLead));
        when(repository.findCustomer(TENANT, beforeCustomer.id())).thenReturn(Optional.of(beforeCustomer), Optional.of(afterCustomer));
        when(repository.moveLead(TENANT, beforeLead.id(), 0L, OTHER_USER, ACTOR, OwnershipType.PRIVATE, ACTOR, null, null, null)).thenReturn(true);
        when(repository.moveCustomer(TENANT, beforeCustomer.id(), 0L, OTHER_USER, ACTOR, OwnershipType.PRIVATE, ACTOR, null)).thenReturn(true);

        assertThat(service.handoverLead(actor, new SalesCommands.Handover(beforeLead.id(), 0L, OTHER_USER, ACTOR, "人员调整"), "handover-lead").ownerUserId())
                .isEqualTo(ACTOR);
        assertThat(service.handoverCustomer(actor, new SalesCommands.Handover(beforeCustomer.id(), 0L, OTHER_USER, ACTOR, "人员调整"), "handover-customer").ownerUserId())
                .isEqualTo(ACTOR);
        verify(repository, times(2)).addHandover(anyLong(), anyLong(), anyLong(), anyLong(), anyString(), anyLong(), anyString(), any(), anyString(), anyString(), any(), any(), anyLong());
    }

    @Test
    void recyclesLeadAndCustomerOnlyWhenTheVersionedMoveSucceeds() {
        Actor automation = new Actor(TENANT, 799L, 7001L, "/7001", Set.of("system"), Set.of(), Set.of(DataScope.ALL));
        PublicPool leadPool = new PublicPool(POOL, TENANT, PublicPool.ResourceType.LEAD, "RECYCLE_LEAD", "自动回收线索",
                true, true, 30, true, true, true, 1, Instant.now());
        Lead beforeLead = lead(1601L, LeadStatus.NEW, OwnershipType.PRIVATE, OTHER_USER, 7002L, null, 0L);
        Lead afterLead = lead(1601L, LeadStatus.NEW, OwnershipType.PUBLIC, null, null, POOL, 1L);
        when(repository.findAutomationActor(TENANT)).thenReturn(Optional.of(automation));
        when(repository.lockRecyclableLeads(any(), any(), anyInt())).thenReturn(java.util.List.of(beforeLead));
        when(repository.moveLead(TENANT, beforeLead.id(), 0L, OTHER_USER, automation.userId(), OwnershipType.PUBLIC, null, POOL, null, null)).thenReturn(true);
        when(repository.findLead(TENANT, beforeLead.id())).thenReturn(Optional.of(afterLead));
        assertThat(service.recycleExpiredPrivateResources(leadPool, 100)).isEqualTo(1L);

        PublicPool customerPool = new PublicPool(POOL + 1, TENANT, PublicPool.ResourceType.CUSTOMER, "RECYCLE_CUSTOMER", "自动回收客户",
                true, true, 30, true, true, true, 1, Instant.now());
        Customer beforeCustomer = customer(1602L, OwnershipType.PRIVATE, OTHER_USER, 7002L, null, 0L);
        Customer afterCustomer = customer(1602L, OwnershipType.PUBLIC, null, null, customerPool.id(), 1L);
        when(repository.lockRecyclableCustomers(any(), any(), anyInt())).thenReturn(java.util.List.of(beforeCustomer));
        when(repository.moveCustomer(TENANT, beforeCustomer.id(), 0L, OTHER_USER, automation.userId(), OwnershipType.PUBLIC, null, customerPool.id())).thenReturn(true);
        when(repository.findCustomer(TENANT, beforeCustomer.id())).thenReturn(Optional.of(afterCustomer));
        assertThat(service.recycleExpiredPrivateResources(customerPool, 100)).isEqualTo(1L);

        assertThat(service.recycleExpiredPrivateResources(new PublicPool(POOL, TENANT, PublicPool.ResourceType.LEAD, "OFF", "停用",
                false, true, 30, true, true, true, 1, Instant.now()), 100)).isZero();
        assertThatThrownBy(() -> service.recycleExpiredPrivateResources(leadPool, 0))
                .isInstanceOf(DomainException.class).hasMessageContaining("批次大小");
    }

    @Test
    void batchHandoverRejectsInvalidParticipantsAndPageSizeBeforeExecution() {
        assertThatThrownBy(() -> service.handoverAll(actor, new SalesCommands.BatchHandover("batch-1", ACTOR, ACTOR, 10, "离职"), "same-user"))
                .isInstanceOf(DomainException.class).hasMessageContaining("双方不能相同");

        when(repository.existsTenantUser(TENANT, OTHER_USER)).thenReturn(false);
        assertThatThrownBy(() -> service.handoverAll(actor, new SalesCommands.BatchHandover("batch-2", OTHER_USER, ACTOR, 10, "离职"), "missing-source"))
                .isInstanceOf(DomainException.class).hasMessageContaining("来源用户不存在");

        when(repository.existsTenantUser(TENANT, OTHER_USER)).thenReturn(true);
        when(repository.isActiveUser(TENANT, ACTOR)).thenReturn(true);
        assertThatThrownBy(() -> service.handoverAll(actor, new SalesCommands.BatchHandover("batch-3", OTHER_USER, ACTOR, 501, "离职"), "bad-page"))
                .isInstanceOf(DomainException.class).hasMessageContaining("分页大小");
    }

    private Actor actor(String... permissions) {
        return new Actor(TENANT, ACTOR, 7001L, "/7001", Set.of("sales"), Set.of(permissions), Set.of(DataScope.SELF));
    }

    private PublicPool pool(PublicPool.ResourceType resourceType, boolean active, boolean claim, boolean assign, boolean release) {
        return new PublicPool(POOL, TENANT, resourceType, resourceType + "-POOL", "测试公海", active, false, null,
                claim, assign, release, 1, Instant.now());
    }

    private Lead lead(long id, LeadStatus status, OwnershipType ownership, Long ownerUserId, Long ownerDeptId, Long poolId, long version) {
        Instant now = Instant.now();
        return new Lead(id, TENANT, "LEAD-" + id, "线索", null, null, null, "MANUAL", null, null, null, null,
                status, ownership, ownerUserId, ownerDeptId, poolId, null, null, null, null, version, now, now);
    }

    private Customer customer(long id, OwnershipType ownership, Long ownerUserId, Long ownerDeptId, Long poolId, long version) {
        Instant now = Instant.now();
        return new Customer(id, TENANT, "CUS-" + id, "客户", null, null, "ACTIVE", ownership, ownerUserId, ownerDeptId,
                poolId, null, null, null, version, now, now);
    }
}
