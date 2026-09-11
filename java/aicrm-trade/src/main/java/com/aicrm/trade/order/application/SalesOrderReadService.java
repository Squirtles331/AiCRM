package com.aicrm.trade.order.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.trade.contract.application.ContractReadService;
import com.aicrm.trade.order.domain.SalesOrder;
import com.aicrm.trade.order.domain.SalesOrderLine;
import com.aicrm.trade.order.domain.SalesOrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SalesOrderReadService {
    private final SalesOrderRepository repository;
    private final ContractReadService contracts;
    public SalesOrderReadService(SalesOrderRepository repository, ContractReadService contracts) { this.repository = repository; this.contracts = contracts; }
    public SalesOrder order(Actor actor, long id) {
        requireAny(actor, "order:read:own", "order:read:any", "order:write:own", "order:write:any");
        SalesOrder order = repository.find(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "销售订单不存在"));
        contracts.contract(actor, order.contractId());
        return order;
    }
    public PageResult<SalesOrder> page(Actor actor, long page, long size) {
        requireAny(actor, "order:read:own", "order:read:any", "order:write:own", "order:write:any");
        return repository.page(actor, page, size);
    }
    public List<SalesOrderLine> lines(Actor actor, long id) { return repository.findLines(actor.tenantId(), order(actor, id).id()); }
    private void requireAny(Actor actor, String... permissions) { for (String permission : permissions) if (actor.hasPermission(permission)) return; throw new DomainException(ErrorCode.FORBIDDEN, "缺少销售订单查看权限"); }
}
