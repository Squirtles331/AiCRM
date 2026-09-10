package com.aicrm.sales.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.opportunity.Opportunity;
import com.aicrm.sales.domain.opportunity.OpportunityRepository;
import com.aicrm.sales.domain.opportunity.OpportunityStageHistory;
import org.springframework.stereotype.Service;

import java.util.List;

/** Read use cases for the opportunity pipeline, including organization data-scope enforcement. */
@Service
public class OpportunityReadService {
    private final OpportunityRepository repository;
    private final SalesRepository salesRepository;

    public OpportunityReadService(OpportunityRepository repository, SalesRepository salesRepository) {
        this.repository = repository;
        this.salesRepository = salesRepository;
    }

    public PageResult<Opportunity> opportunities(Actor actor, long page, long size) {
        requireAny(actor, "opportunity:read:own", "opportunity:read:any");
        return repository.page(actor, page, size);
    }

    public Opportunity opportunity(Actor actor, long id) {
        Opportunity opportunity = repository.find(actor.tenantId(), id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "商机不存在"));
        if (opportunity.ownerUserId() == actor.userId() && actor.hasPermission("opportunity:read:own")) {
            return opportunity;
        }
        if (actor.hasPermission("opportunity:read:any") && salesRepository.isDepartmentInActorScope(actor, opportunity.ownerDeptId())) {
            return opportunity;
        }
        throw new DomainException(ErrorCode.FORBIDDEN, "无权查看该商机");
    }

    public List<OpportunityStageHistory> histories(Actor actor, long id) {
        opportunity(actor, id);
        return repository.histories(actor.tenantId(), id);
    }

    private void requireAny(Actor actor, String... permissions) {
        for (String permission : permissions) {
            if (actor.hasPermission(permission)) {
                return;
            }
        }
        throw new DomainException(ErrorCode.FORBIDDEN, "缺少商机查看权限");
    }
}
