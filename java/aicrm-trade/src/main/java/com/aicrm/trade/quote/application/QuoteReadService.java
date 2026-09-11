package com.aicrm.trade.quote.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.sales.application.OpportunityReadService;
import com.aicrm.trade.quote.domain.Quote;
import com.aicrm.trade.quote.domain.QuoteLine;
import com.aicrm.trade.quote.domain.QuoteRepository;
import com.aicrm.trade.quote.domain.QuoteVersion;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class QuoteReadService {
    private final QuoteRepository repository;
    private final OpportunityReadService opportunities;

    public QuoteReadService(QuoteRepository repository, OpportunityReadService opportunities) {
        this.repository = repository;
        this.opportunities = opportunities;
    }

    public Quote quote(Actor actor, long id) {
        Quote quote = repository.findQuote(actor.tenantId(), id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "报价不存在"));
        opportunities.opportunity(actor, quote.opportunityId());
        return quote;
    }

    public PageResult<Quote> page(Actor actor, long page, long size) {
        requireAny(actor, "quote:read:own", "quote:read:any", "quote:write:own", "quote:write:any");
        return repository.page(actor, page, size);
    }

    public QuoteVersion currentVersion(Actor actor, long id) {
        Quote quote = quote(actor, id);
        return repository.findVersion(actor.tenantId(), quote.id(), quote.currentVersionNo())
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "报价版本不存在"));
    }

    public List<QuoteVersion> versions(Actor actor, long id) {
        quote(actor, id);
        return repository.findVersions(actor.tenantId(), id);
    }

    public List<QuoteLine> lines(Actor actor, long id, int versionNo) {
        Quote quote = quote(actor, id);
        repository.findVersion(actor.tenantId(), quote.id(), versionNo)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "报价版本不存在"));
        return repository.findLines(actor.tenantId(), repository.findVersion(actor.tenantId(), quote.id(), versionNo).orElseThrow().id());
    }

    /** Public cross-context contract. Consumers receive a copy, never Quote aggregates or repositories. */
    public ApprovedContractSource approvedContractSource(Actor actor, long id) {
        Quote quote = quote(actor, id);
        QuoteVersion version = currentVersion(actor, id);
        if (quote.status() != Quote.Status.APPROVED || version.status() != QuoteVersion.Status.APPROVED) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "仅已批准报价可以创建合同");
        }
        List<ContractSourceLine> sourceLines = repository.findLines(actor.tenantId(), version.id()).stream()
                .map(line -> new ContractSourceLine(line.id(), line.lineNo(), line.productId(), line.productNo(), line.sku(),
                        line.productName(), line.unit(), line.quantity(), line.listPrice(), line.unitPrice(), line.discountRate(),
                        line.taxRate(), line.lineAmount())).toList();
        return new ApprovedContractSource(quote.id(), version.id(), quote.customerId(), quote.currency(), version.subtotal(),
                version.discountAmount(), version.taxAmount(), version.totalAmount(), sourceLines);
    }

    public record ApprovedContractSource(long quoteId, long quoteVersionId, long customerId, String currency,
                                         BigDecimal subtotal, BigDecimal discountAmount, BigDecimal taxAmount,
                                         BigDecimal totalAmount, List<ContractSourceLine> lines) { }
    public record ContractSourceLine(long quoteLineId, int lineNo, long productId, String productNo, String sku,
                                     String productName, String unit, BigDecimal quantity, BigDecimal listPrice,
                                     BigDecimal unitPrice, BigDecimal discountRate, BigDecimal taxRate,
                                     BigDecimal lineAmount) { }

    private void requireAny(Actor actor, String... permissions) {
        for (String permission : permissions) if (actor.hasPermission(permission)) return;
        throw new DomainException(ErrorCode.FORBIDDEN, "缺少报价查看权限");
    }
}
