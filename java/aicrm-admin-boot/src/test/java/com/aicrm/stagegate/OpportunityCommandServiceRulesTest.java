package com.aicrm.stagegate;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.aicrm.sales.application.OpportunityCommandService;
import com.aicrm.sales.application.OpportunityCommands;
import com.aicrm.sales.domain.OwnershipType;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.opportunity.Opportunity;
import com.aicrm.sales.domain.opportunity.OpportunityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Business-rule coverage for the CRM opportunity state machine. */
class OpportunityCommandServiceRulesTest {
    private static final long TENANT = 81L;
    private static final long USER = 811L;
    private static final long CUSTOMER = 812L;
    private static final long OPPORTUNITY = 813L;

    private OpportunityRepository opportunities;
    private SalesRepository sales;
    private AuditLogService audit;
    private OutboxService outbox;
    private OpportunityCommandService service;
    private Actor actor;

    @BeforeEach
    void setUp() {
        opportunities = mock(OpportunityRepository.class);
        sales = mock(SalesRepository.class);
        IdempotencyService idempotency = mock(IdempotencyService.class);
        audit = mock(AuditLogService.class);
        outbox = mock(OutboxService.class);
        AtomicLong ids = new AtomicLong(20_000L);
        IdGenerator idGenerator = ids::incrementAndGet;
        service = new OpportunityCommandService(opportunities, sales, idGenerator, idempotency, audit, outbox,
                new ObjectMapper().findAndRegisterModules());
        actor = new Actor(TENANT, USER, 8101L, "/8101", Set.of("sales"), Set.of(
                "opportunity:create", "opportunity:stage", "opportunity:win", "opportunity:lose", "opportunity:restart",
                "opportunity:write:own", "customer:read:own"), Set.of(DataScope.SELF));
        doAnswer(invocation -> ((Supplier<?>) invocation.getArgument(5)).get())
                .when(idempotency).execute(any(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void createsOpportunityWithNormalizedCommercialValuesAndJournal() {
        Customer customer = customer();
        when(sales.findCustomer(TENANT, CUSTOMER)).thenReturn(Optional.of(customer));
        when(opportunities.insert(any(), anyLong())).thenAnswer(invocation -> invocation.getArgument(0));

        Opportunity created = service.create(actor, new OpportunityCommands.Create(CUSTOMER, null, null, "  项目 A  ",
                new BigDecimal("1200.50"), "cny", (short) 45, LocalDate.of(2026, 12, 31)), "opp-create-1");

        assertThat(created.name()).isEqualTo("项目 A");
        assertThat(created.currency()).isEqualTo("CNY");
        assertThat(created.stage()).isEqualTo(Opportunity.Stage.DISCOVERY);
        assertThat(created.status()).isEqualTo(Opportunity.Status.OPEN);
        verify(opportunities).appendHistory(anyLong(), anyLong(), anyLong(), any(), any(), any(), any(), anyLong());
        verify(audit).record(any(), anyString(), anyString(), anyLong(), anyString(), anyString(), any(), anyString(), anyString());
        verify(outbox).append(any(), anyString(), anyLong());
    }

    @Test
    void changesStageAndRejectsClosedOrUnchangedStage() {
        Opportunity discovery = opportunity(Opportunity.Stage.DISCOVERY, Opportunity.Status.OPEN, 0L);
        Opportunity solution = opportunity(Opportunity.Stage.SOLUTION, Opportunity.Status.OPEN, 1L);
        when(opportunities.find(TENANT, OPPORTUNITY)).thenReturn(Optional.of(discovery), Optional.of(solution));
        when(opportunities.changeStage(TENANT, OPPORTUNITY, 0L, Opportunity.Stage.SOLUTION, (short) 60, USER)).thenReturn(true);

        Opportunity changed = service.changeStage(actor, OPPORTUNITY,
                new OpportunityCommands.ChangeStage(0L, "solution", (short) 60));
        assertThat(changed.stage()).isEqualTo(Opportunity.Stage.SOLUTION);

        when(opportunities.find(TENANT, OPPORTUNITY)).thenReturn(Optional.of(solution));
        assertThatThrownBy(() -> service.changeStage(actor, OPPORTUNITY,
                new OpportunityCommands.ChangeStage(1L, "SOLUTION", (short) 60)))
                .isInstanceOf(DomainException.class).hasMessageContaining("不同的进行中阶段");
        assertThatThrownBy(() -> service.changeStage(actor, OPPORTUNITY,
                new OpportunityCommands.ChangeStage(1L, "CLOSED_WON", (short) 100)))
                .isInstanceOf(DomainException.class).hasMessageContaining("进行中阶段");
    }

    @Test
    void winsLosesAndRestartsOnlyThroughVersionedTransitions() {
        Opportunity open = opportunity(Opportunity.Stage.NEGOTIATION, Opportunity.Status.OPEN, 2L);
        Opportunity won = opportunity(Opportunity.Stage.CLOSED_WON, Opportunity.Status.WON, 3L);
        when(opportunities.find(TENANT, OPPORTUNITY)).thenReturn(Optional.of(open), Optional.of(won));
        when(opportunities.win(TENANT, OPPORTUNITY, 2L, USER)).thenReturn(true);
        assertThat(service.win(actor, OPPORTUNITY, 2L).status()).isEqualTo(Opportunity.Status.WON);

        Opportunity lost = opportunity(Opportunity.Stage.CLOSED_LOST, Opportunity.Status.LOST, 4L);
        Opportunity restarted = opportunity(Opportunity.Stage.DISCOVERY, Opportunity.Status.OPEN, 5L);
        when(opportunities.find(TENANT, OPPORTUNITY)).thenReturn(Optional.of(open), Optional.of(lost), Optional.of(lost), Optional.of(restarted));
        when(opportunities.lose(TENANT, OPPORTUNITY, 2L, "预算不足", USER)).thenReturn(true);
        when(opportunities.restart(TENANT, OPPORTUNITY, 4L, USER)).thenReturn(true);
        assertThat(service.lose(actor, OPPORTUNITY, 2L, "预算不足").status()).isEqualTo(Opportunity.Status.LOST);
        assertThat(service.restart(actor, OPPORTUNITY, 4L).status()).isEqualTo(Opportunity.Status.OPEN);

        when(opportunities.find(TENANT, OPPORTUNITY)).thenReturn(Optional.of(won));
        assertThatThrownBy(() -> service.lose(actor, OPPORTUNITY, 3L, "不应再输单"))
                .isInstanceOf(DomainException.class).hasMessageContaining("终态");
    }

    @Test
    void rejectsInvalidCommercialInputAndInvisibleCustomer() {
        when(sales.findCustomer(TENANT, CUSTOMER)).thenReturn(Optional.of(customer()));
        OpportunityCommands.Create validShape = new OpportunityCommands.Create(CUSTOMER, null, null, "项目",
                BigDecimal.ONE, "CNY", (short) 50, null);

        assertThatThrownBy(() -> service.create(actor, new OpportunityCommands.Create(CUSTOMER, null, null, "项目",
                new BigDecimal("-1"), "CNY", (short) 50, null), "negative-amount"))
                .isInstanceOf(DomainException.class).hasMessageContaining("预计金额");
        assertThatThrownBy(() -> service.create(actor, new OpportunityCommands.Create(CUSTOMER, null, null, "项目",
                BigDecimal.ONE, "CN", (short) 50, null), "bad-currency"))
                .isInstanceOf(DomainException.class).hasMessageContaining("ISO 4217");
        assertThatThrownBy(() -> service.create(actor, new OpportunityCommands.Create(CUSTOMER, null, null, "项目",
                BigDecimal.ONE, "CNY", (short) 101, null), "bad-probability"))
                .isInstanceOf(DomainException.class).hasMessageContaining("赢单概率");

        Actor noCustomerRead = new Actor(TENANT, USER + 1, 8102L, "/8102", Set.of("sales"), Set.of("opportunity:create"), Set.of(DataScope.SELF));
        assertThatThrownBy(() -> service.create(noCustomerRead, validShape, "customer-forbidden"))
                .isInstanceOf(DomainException.class).hasMessageContaining("无权使用该客户");
    }

    private Customer customer() {
        Instant now = Instant.now();
        return new Customer(CUSTOMER, TENANT, "CUS-" + CUSTOMER, "客户", null, null, "ACTIVE", OwnershipType.PRIVATE,
                USER, 8101L, null, null, null, null, 0L, now, now);
    }

    private Opportunity opportunity(Opportunity.Stage stage, Opportunity.Status status, long version) {
        Instant now = Instant.now();
        return new Opportunity(OPPORTUNITY, TENANT, "OPP-" + OPPORTUNITY, "项目", CUSTOMER, null, null, stage, status,
                BigDecimal.TEN, "CNY", (short) 50, null, USER, 8101L, null, null, null, version, now, now);
    }
}
