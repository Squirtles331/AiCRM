package com.aicrm.trade.contract.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import com.aicrm.trade.contract.domain.Contract;
import com.aicrm.trade.contract.domain.ContractChange;
import com.aicrm.trade.contract.domain.ContractChangeRepository;
import com.aicrm.trade.contract.domain.ContractLine;
import com.aicrm.trade.contract.domain.ContractRepository;
import com.aicrm.trade.quote.application.QuoteReadService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ContractReadService {
    private final ContractRepository repository;
    private final ContractChangeRepository changes;
    private final QuoteReadService quotes;

    public ContractReadService(ContractRepository repository, ContractChangeRepository changes, QuoteReadService quotes) { this.repository = repository; this.changes = changes; this.quotes = quotes; }

    public Contract contract(Actor actor, long id) {
        requireAny(actor, "contract:read:own", "contract:read:any", "contract:write:own", "contract:write:any");
        Contract contract = repository.find(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "合同不存在"));
        quotes.quote(actor, contract.quoteId());
        return contract;
    }

    public List<ContractLine> lines(Actor actor, long id) { return repository.findLines(actor.tenantId(), contract(actor, id).id()); }

    public ContractChange change(Actor actor, long contractId, long changeId) {
        contract(actor, contractId);
        ContractChange change = changes.find(actor.tenantId(), changeId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "合同变更不存在"));
        if (change.contractId() != contractId) throw new DomainException(ErrorCode.NOT_FOUND, "合同变更不存在");
        return change;
    }

    /** Public order-source contract; order code does not import Contract domain objects. */
    public SignedOrderSource signedOrderSource(Actor actor, long id) {
        Contract contract = contract(actor, id);
        if (contract.status() != Contract.Status.SIGNED) throw new DomainException(ErrorCode.VALIDATION_ERROR, "仅已签署合同可以创建销售订单");
        List<OrderSourceLine> sourceLines = repository.findLines(actor.tenantId(), contract.id()).stream()
                .map(line -> new OrderSourceLine(line.id(), line.lineNo(), line.productId(), line.productNo(), line.sku(), line.productName(),
                        line.unit(), line.quantity(), line.unitPrice(), line.taxRate(), line.lineAmount())).toList();
        return new SignedOrderSource(contract.id(), contract.customerId(), contract.currency(), contract.totalAmount(), sourceLines);
    }

    public record SignedOrderSource(long contractId, long customerId, String currency, BigDecimal totalAmount, List<OrderSourceLine> lines) { }
    public record OrderSourceLine(long contractLineId, int lineNo, long productId, String productNo, String sku, String productName,
                                  String unit, BigDecimal quantity, BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineAmount) { }

    private void requireAny(Actor actor, String... permissions) {
        for (String permission : permissions) if (actor.hasPermission(permission)) return;
        throw new DomainException(ErrorCode.FORBIDDEN, "缺少合同查看权限");
    }
}
