package com.aicrm.trade.quote.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import com.aicrm.sales.application.OpportunityReadService;
import com.aicrm.trade.quote.domain.Quote;
import com.aicrm.trade.quote.domain.QuoteLine;
import com.aicrm.trade.quote.domain.QuoteRepository;
import com.aicrm.trade.quote.domain.QuoteVersion;
import org.springframework.stereotype.Service;

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
}
