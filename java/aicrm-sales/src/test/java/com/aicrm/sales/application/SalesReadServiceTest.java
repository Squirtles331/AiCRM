package com.aicrm.sales.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import com.aicrm.sales.domain.OwnershipType;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.lead.Lead;
import com.aicrm.sales.domain.lead.LeadStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SalesReadServiceTest {
    @Test
    void departmentAndSubScopeCanReadPrivateLeadInSubtree() {
        Lead lead = lead(201L, 31L);
        SalesReadService service = new SalesReadService(repository(lead, true));
        Actor actor = actor(Set.of(DataScope.DEPARTMENT_AND_SUB));

        assertEquals(lead, service.lead(actor, lead.id()));
    }

    @Test
    void privateLeadOutsideScopeIsRejectedEvenWithReadAnyPermission() {
        Lead lead = lead(201L, 99L);
        SalesReadService service = new SalesReadService(repository(lead, false));
        Actor actor = new Actor(1L, 101L, 11L, "/11", Set.of("supervisor"),
                Set.of("lead:read:any"), Set.of(DataScope.DEPARTMENT_AND_SUB));

        assertThrows(DomainException.class, () -> service.lead(actor, lead.id()));
    }

    private Actor actor(Set<DataScope> scopes) {
        return new Actor(1L, 101L, 11L, "/11", Set.of("sales"), Set.of("lead:read:any"), scopes);
    }

    private Lead lead(long ownerUserId, long ownerDeptId) {
        Instant now = Instant.now();
        return new Lead(2001L, 1L, "LEAD-1", "张三", null, null, null, "MANUAL", null, null, null, null,
                LeadStatus.NEW, OwnershipType.PRIVATE, ownerUserId, ownerDeptId, null, null,
                null, null, null, 0L, now, now);
    }

    private SalesRepository repository(Lead lead, boolean departmentVisible) {
        return (SalesRepository) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{SalesRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findLead" -> Optional.of(lead);
                    case "isDepartmentInActorScope" -> departmentVisible;
                    case "toString" -> "sales-read-test-repository";
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }
}
