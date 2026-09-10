package com.aicrm.trade.quote.application;

import com.aicrm.catalog.application.CatalogReadService;
import com.aicrm.catalog.domain.PriceItem;
import com.aicrm.catalog.domain.PriceList;
import com.aicrm.catalog.domain.Product;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.aicrm.sales.application.OpportunityReadService;
import com.aicrm.sales.domain.opportunity.Opportunity;
import com.aicrm.trade.quote.domain.Quote;
import com.aicrm.trade.quote.domain.QuoteLine;
import com.aicrm.trade.quote.domain.QuoteRepository;
import com.aicrm.trade.quote.domain.QuoteVersion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class QuoteCommandService {
    private final QuoteRepository repository;
    private final OpportunityReadService opportunities;
    private final CatalogReadService catalog;
    private final IdGenerator ids;
    private final IdempotencyService idempotency;
    private final AuditLogService audit;
    private final OutboxService outbox;
    private final ObjectMapper mapper;

    public QuoteCommandService(QuoteRepository repository, OpportunityReadService opportunities, CatalogReadService catalog,
                               IdGenerator ids, IdempotencyService idempotency, AuditLogService audit, OutboxService outbox,
                               ObjectMapper mapper) {
        this.repository = repository;
        this.opportunities = opportunities;
        this.catalog = catalog;
        this.ids = ids;
        this.idempotency = idempotency;
        this.audit = audit;
        this.outbox = outbox;
        this.mapper = mapper;
    }

    @Transactional
    public Quote create(Actor actor, QuoteCommands.Create command, String key) {
        require(actor, "quote:create");
        return idempotency.execute(actor, "quote:create", key, command, Quote.class, () -> {
            Opportunity opportunity = opportunities.opportunity(actor, command.opportunityId());
            if (opportunity.status() != Opportunity.Status.OPEN && opportunity.status() != Opportunity.Status.WON) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "只有进行中或赢单商机可以报价");
            }
            PriceList priceList = catalog.priceList(actor, command.priceListId());
            if (priceList.status() != PriceList.Status.ACTIVE) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "价目表未生效");
            }
            if (command.validUntil() != null && command.validUntil().isBefore(LocalDate.now())) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "报价有效期不能早于今天");
            }
            List<QuoteCommands.Line> requested = command.lines() == null ? List.of() : command.lines();
            if (requested.isEmpty() || requested.size() > 100) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "报价行数必须在 1 到 100 之间");
            }
            List<PriceItem> prices = catalog.priceItems(actor, priceList.id());
            Instant now = Instant.now();
            long quoteId = ids.nextId();
            Quote quote = new Quote(quoteId, actor.tenantId(), "QUO-" + ids.nextId(), opportunity.id(), opportunity.customerId(),
                    priceList.id(), priceList.currency(), Quote.Status.DRAFT, 1, command.validUntil(), 0, now, now);
            Quote created = repository.insertQuote(quote, actor.userId());
            QuoteTotals totals = lines(actor, created, requested, prices, priceList.currency(), now);
            QuoteVersion version = new QuoteVersion(ids.nextId(), actor.tenantId(), quoteId, 1, QuoteVersion.Status.DRAFT,
                    totals.subtotal(), totals.discountAmount(), totals.taxAmount(), totals.totalAmount(), totals.discountRate(),
                    null, null, null, null, null, 0, now, now);
            repository.insertVersion(version, actor.userId());
            insertLines(actor, version, totals.lines());
            journal(actor, "CREATE", created, "QuoteCreated");
            return created;
        });
    }

    @Transactional
    public Quote submit(Actor actor, long quoteId, long rootVersion, long currentVersionVersion) {
        require(actor, "quote:submit");
        Quote quote = quote(actor, quoteId);
        requireWrite(actor, quote);
        QuoteVersion draft = currentVersion(actor, quote);
        if (draft.status() != QuoteVersion.Status.DRAFT) throw conflict("只有草稿报价可以提交");
        if (!repository.submit(actor.tenantId(), quoteId, draft.versionNo(), rootVersion, currentVersionVersion, actor.userId())) throw conflict("报价已被其他操作修改");
        Quote after = quote(actor, quoteId);
        journal(actor, "SUBMIT", after, "QuoteSubmitted");
        return after;
    }

    @Transactional
    public Quote reject(Actor actor, long quoteId, long rootVersion, long currentVersionVersion, String reason) {
        require(actor, "quote:reject");
        Quote quote = quote(actor, quoteId); requireWrite(actor, quote);
        String normalized = required(reason, "拒绝原因");
        QuoteVersion current = currentVersion(actor, quote);
        if (!repository.reject(actor.tenantId(), quoteId, current.versionNo(), rootVersion, currentVersionVersion, normalized, actor.userId())) throw conflict("报价已被其他操作修改");
        Quote after = quote(actor, quoteId); journal(actor, "REJECT", after, "QuoteRejected"); return after;
    }

    @Transactional
    public Quote expire(Actor actor, long quoteId, long rootVersion, long currentVersionVersion) {
        require(actor, "quote:expire");
        Quote quote = quote(actor, quoteId); requireWrite(actor, quote);
        if (quote.validUntil() == null || !quote.validUntil().isBefore(LocalDate.now())) throw new DomainException(ErrorCode.VALIDATION_ERROR, "报价尚未到期");
        QuoteVersion current = currentVersion(actor, quote);
        if (!repository.expire(actor.tenantId(), quoteId, current.versionNo(), rootVersion, currentVersionVersion, actor.userId())) throw conflict("报价已被其他操作修改");
        Quote after = quote(actor, quoteId); journal(actor, "EXPIRE", after, "QuoteExpired"); return after;
    }

    private QuoteTotals lines(Actor actor, Quote quote, List<QuoteCommands.Line> requested, List<PriceItem> prices, String currency, Instant now) {
        BigDecimal subtotal = BigDecimal.ZERO, discount = BigDecimal.ZERO, tax = BigDecimal.ZERO;
        java.util.ArrayList<QuoteLine> result = new java.util.ArrayList<>();
        for (int i = 0; i < requested.size(); i++) {
            QuoteCommands.Line input = requested.get(i);
            PriceItem price = prices.stream().filter(item -> item.id() == input.priceItemId() && item.productId() == input.productId()).findFirst()
                    .orElseThrow(() -> new DomainException(ErrorCode.VALIDATION_ERROR, "价格项不属于该价目表"));
            Product product = catalog.product(actor, input.productId());
            if (price.status() != PriceItem.Status.ACTIVE || product.status() != Product.Status.ACTIVE || !product.saleEnabled()) throw new DomainException(ErrorCode.VALIDATION_ERROR, "产品或价格项不可销售");
            BigDecimal quantity = positive(input.quantity(), "数量");
            BigDecimal unitPrice = positiveOrZero(input.unitPrice(), "成交单价");
            if (unitPrice.compareTo(price.listPrice()) > 0 || (price.minimumPrice() != null && unitPrice.compareTo(price.minimumPrice()) < 0)) throw new DomainException(ErrorCode.VALIDATION_ERROR, "成交价不符合价格上下限");
            BigDecimal derived = price.listPrice().signum() == 0 ? BigDecimal.ZERO : BigDecimal.ONE.subtract(unitPrice.divide(price.listPrice(), 8, RoundingMode.HALF_UP));
            BigDecimal rate = input.discountRate() == null ? derived : rate(input.discountRate());
            if (input.discountRate() != null && derived.subtract(rate).abs().compareTo(new BigDecimal("0.0002")) > 0) throw new DomainException(ErrorCode.VALIDATION_ERROR, "折扣率与成交价不一致");
            BigDecimal lineAmount = unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineTax = lineAmount.multiply(price.taxRate()).setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(price.listPrice().multiply(quantity)); discount = discount.add(price.listPrice().subtract(unitPrice).multiply(quantity)); tax = tax.add(lineTax);
            result.add(new QuoteLine(ids.nextId(), actor.tenantId(), 0, i + 1, product.id(), price.id(), product.productNo(), product.sku(), product.name(), product.unit(), quantity, price.listPrice(), price.minimumPrice(), unitPrice, rate, price.taxRate(), lineAmount, now, now));
        }
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP); discount = discount.setScale(2, RoundingMode.HALF_UP); tax = tax.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(discount).add(tax).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rate = subtotal.signum() == 0 ? BigDecimal.ZERO : discount.divide(subtotal, 8, RoundingMode.HALF_UP);
        return new QuoteTotals(subtotal, discount, tax, total, rate, result);
    }

    private void insertLines(Actor actor, QuoteVersion version, List<QuoteLine> lines) { for (QuoteLine line : lines) repository.insertLine(new QuoteLine(line.id(), line.tenantId(), version.id(), line.lineNo(), line.productId(), line.priceItemId(), line.productNo(), line.sku(), line.productName(), line.unit(), line.quantity(), line.listPrice(), line.minimumPrice(), line.unitPrice(), line.discountRate(), line.taxRate(), line.lineAmount(), line.createdAt(), line.updatedAt()), actor.userId()); }
    private Quote quote(Actor actor, long id) { return repository.findQuote(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "报价不存在")); }
    private QuoteVersion currentVersion(Actor actor, Quote quote) { return repository.findVersion(actor.tenantId(), quote.id(), quote.currentVersionNo()).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "报价版本不存在")); }
    private void requireWrite(Actor actor, Quote quote) {
        Opportunity opportunity = opportunities.opportunity(actor, quote.opportunityId());
        if (opportunity.ownerUserId() == actor.userId() && actor.hasPermission("quote:write:own")) return;
        if (!actor.hasPermission("quote:write:any")) throw new DomainException(ErrorCode.FORBIDDEN, "无权维护该报价");
    }
    private void journal(Actor actor, String action, Quote quote, String event) { String op = "quote:" + action + ":" + quote.id() + ":" + ids.nextId(); String json = json(quote); audit.record(actor, action, "QUOTE", quote.id(), op, "API", null, "{}", json); outbox.append(new DomainEvent(event, "QUOTE", quote.id(), actor.tenantId(), json, Instant.now()), op, actor.userId()); }
    private void require(Actor actor, String permission) { if (!actor.hasPermission(permission)) throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：" + permission); }
    private String required(String value, String field) { if (value == null || value.isBlank()) throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "不能为空"); return value.trim(); }
    private BigDecimal positive(BigDecimal value, String field) { if (value == null || value.signum() <= 0) throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "必须大于零"); return value; }
    private BigDecimal positiveOrZero(BigDecimal value, String field) { if (value == null || value.signum() < 0) throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "不能小于零"); return value; }
    private BigDecimal rate(BigDecimal value) { if (value.signum() < 0 || value.compareTo(BigDecimal.ONE) > 0) throw new DomainException(ErrorCode.VALIDATION_ERROR, "折扣率必须在 0 到 1 之间"); return value; }
    private DomainException conflict(String message) { return new DomainException(ErrorCode.CONFLICT, message); }
    private String json(Object value) { try { return mapper.writeValueAsString(value); } catch (JsonProcessingException e) { throw new IllegalStateException("无法序列化报价", e); } }
    private record QuoteTotals(BigDecimal subtotal, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal totalAmount, BigDecimal discountRate, List<QuoteLine> lines) { }
}
