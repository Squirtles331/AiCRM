package com.aicrm.trade.quote.domain;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface QuoteRepository {
    Quote insertQuote(Quote quote, long actorId);
    QuoteVersion insertVersion(QuoteVersion version, long actorId);
    QuoteLine insertLine(QuoteLine line, long actorId);
    Optional<Quote> findQuote(long tenantId, long quoteId);
    PageResult<Quote> page(Actor actor, long page, long size);
    Optional<QuoteVersion> findVersion(long tenantId, long quoteId, int versionNo);
    List<QuoteVersion> findVersions(long tenantId, long quoteId);
    List<QuoteLine> findLines(long tenantId, long quoteVersionId);
    boolean submit(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId);
    boolean approve(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId);
    boolean reject(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, String reason, long actorId);
    boolean expire(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId);
    boolean withdrawApproval(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId);
    boolean hasQuoteVersion(long tenantId, long quoteId, int versionNo);
    boolean addVersion(long tenantId, long quoteId, long expectedQuoteVersion, LocalDate validUntil, QuoteVersion version, long actorId);
}
