package com.aicrm.analytics.target.application;

import com.aicrm.analytics.target.domain.SalesTarget;
import com.aicrm.analytics.target.domain.SalesTargetRepository;
import com.aicrm.analytics.target.domain.SalesTargetResult;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import org.springframework.stereotype.Service;

@Service
public class SalesTargetReadService {
    private final SalesTargetRepository repository;
    public SalesTargetReadService(SalesTargetRepository repository) { this.repository = repository; }
    public TargetDetail target(Actor actor, long id) {
        SalesTarget target = repository.find(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "销售目标不存在"));
        boolean mayReadOwn = target.targetUserId() == actor.userId() && actor.hasPermission("target:read:own");
        if (!mayReadOwn && !actor.hasPermission("target:read:any") && !actor.hasPermission("target:manage")) {
            throw new DomainException(ErrorCode.FORBIDDEN, "无权查看该销售目标");
        }
        return new TargetDetail(target, repository.findResult(actor.tenantId(), id).orElse(null));
    }
    public record TargetDetail(SalesTarget target, SalesTargetResult result) { }
}
