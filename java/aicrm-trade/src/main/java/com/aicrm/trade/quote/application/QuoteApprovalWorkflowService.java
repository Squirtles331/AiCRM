package com.aicrm.trade.quote.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import com.aicrm.platform.application.ApprovalService;
import com.aicrm.trade.quote.domain.Quote;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuoteApprovalWorkflowService {
    private final ApprovalService approvals;
    private final QuoteCommandService quotes;

    public QuoteApprovalWorkflowService(ApprovalService approvals, QuoteCommandService quotes) {
        this.approvals = approvals;
        this.quotes = quotes;
    }

    @Transactional
    public ApprovalResult approve(Actor actor, long taskId, long version, String comment) {
        ApprovalService.Decision decision = approvals.approve(actor, taskId, version, comment);
        return result(actor, decision, null);
    }

    @Transactional
    public ApprovalResult reject(Actor actor, long taskId, long version, String comment) {
        ApprovalService.Decision decision = approvals.reject(actor, taskId, version, comment);
        return result(actor, decision, comment);
    }

    @Transactional
    public ApprovalService.Task transfer(Actor actor, long taskId, long version, long toUserId, String comment) {
        return approvals.transfer(actor, taskId, version, toUserId, comment);
    }

    @Transactional
    public Quote withdraw(Actor actor, long quoteId, long instanceId, long version, String comment) {
        ApprovalService.Decision decision = approvals.withdraw(actor, instanceId, version, comment);
        if (!"QUOTE".equals(decision.resourceType()) || decision.resourceId() != quoteId || decision.status() != ApprovalService.InstanceStatus.WITHDRAWN) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "审批实例不属于该报价");
        }
        return quotes.withdrawApproval(actor, quoteId);
    }

    private ApprovalResult result(Actor actor, ApprovalService.Decision decision, String reason) {
        Quote quote = switch (decision.status()) {
            case APPROVED, REJECTED -> quotes.applyApprovalDecision(actor, decision, reason);
            default -> null;
        };
        return new ApprovalResult(decision, quote);
    }

    public record ApprovalResult(ApprovalService.Decision decision, Quote quote) { }
}
