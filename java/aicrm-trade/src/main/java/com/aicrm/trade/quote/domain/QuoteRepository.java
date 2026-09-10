package com.aicrm.trade.quote.domain;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository {
    Quote insertQuote(Quote quote, long actorId);
    QuoteVersion insertVersion(QuoteVersion version, long actorId);
    QuoteLine insertLine(QuoteLine line, long actorId);
    Optional<Quote> findQuote(long tenantId, long quoteId);
    Optional<QuoteVersion> findVersion(long tenantId, long quoteId, int versionNo);
    List<QuoteVersion> findVersions(long tenantId, long quoteId);
    List<QuoteLine> findLines(long tenantId, long quoteVersionId);
    boolean submit(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId);
    boolean reject(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, String reason, long actorId);
    boolean expire(long tenantId, long quoteId, int versionNo, long expectedRootVersion, long expectedCurrentVersionVersion, long actorId);
    boolean hasQuoteVersion(long tenantId, long quoteId, int versionNo);
    boolean addVersion(long tenantId, long quoteId, long expectedQuoteVersion, QuoteVersion version, long actorId);
}
