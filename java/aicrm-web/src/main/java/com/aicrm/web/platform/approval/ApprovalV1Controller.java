package com.aicrm.web.platform.approval;

import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.ApprovalService;
import com.aicrm.trade.quote.application.QuoteApprovalWorkflowService;
import com.aicrm.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "CRM V1 - 审批管理")
public class ApprovalV1Controller {
    private final ApprovalService approvals;
    private final QuoteApprovalWorkflowService quoteWorkflow;

    public ApprovalV1Controller(ApprovalService approvals, QuoteApprovalWorkflowService quoteWorkflow) {
        this.approvals = approvals;
        this.quoteWorkflow = quoteWorkflow;
    }

    @PostMapping("/approval-definitions")
    @Operation(summary = "创建审批定义")
    public ResponseEntity<ApiResponse<ApprovalApiDtos.DefinitionView>> createDefinition(@Valid @RequestBody ApprovalApiDtos.CreateDefinitionRequest request) {
        ApprovalService.Definition result = approvals.createDefinition(actor(), new ApprovalService.CreateDefinition(request.code(), request.name(), request.resourceType(),
                request.nodes().stream().map(node -> new ApprovalService.NodeCommand(node.name(), node.decisionMode(), node.approverUserIds(),
                        node.condition() == null ? null : node.condition().toString())).toList()));
        return ResponseEntity.status(HttpStatus.CREATED).body(success(definition(result)));
    }

    @PostMapping("/approval-definitions/{id}/actions/activate")
    @Operation(summary = "启用审批定义")
    public ApiResponse<ApprovalApiDtos.DefinitionView> activateDefinition(@PathVariable long id, @Valid @RequestBody ApprovalApiDtos.VersionRequest request) {
        return success(definition(approvals.activateDefinition(actor(), id, request.version())));
    }

    @GetMapping("/approval-tasks/pending")
    @Operation(summary = "查询我的待审批任务")
    public ApiResponse<List<ApprovalApiDtos.TaskView>> pendingTasks() {
        return success(approvals.pendingTasks(actor()).stream().map(ApprovalV1Controller::task).toList());
    }

    @PostMapping("/approval-tasks/{id}/actions/approve")
    @Operation(summary = "通过报价审批任务")
    public ApiResponse<ApprovalApiDtos.ApprovalActionView> approve(@PathVariable long id, @Valid @RequestBody ApprovalApiDtos.TaskActionRequest request) {
        return success(result(quoteWorkflow.approve(actor(), id, request.version(), request.comment())));
    }

    @PostMapping("/approval-tasks/{id}/actions/reject")
    @Operation(summary = "驳回报价审批任务")
    public ApiResponse<ApprovalApiDtos.ApprovalActionView> reject(@PathVariable long id, @Valid @RequestBody ApprovalApiDtos.TaskActionRequest request) {
        return success(result(quoteWorkflow.reject(actor(), id, request.version(), request.comment())));
    }

    @PostMapping("/approval-tasks/{id}/actions/transfer")
    @Operation(summary = "转交审批任务")
    public ApiResponse<ApprovalApiDtos.TaskView> transfer(@PathVariable long id, @Valid @RequestBody ApprovalApiDtos.TransferRequest request) {
        return success(task(quoteWorkflow.transfer(actor(), id, request.version(), request.toUserId(), request.comment())));
    }

    private static ApprovalApiDtos.DefinitionView definition(ApprovalService.Definition value) { return new ApprovalApiDtos.DefinitionView(id(value.id()), value.code(), value.name(), value.resourceType(), value.definitionVersion(), value.status().name(), value.version()); }
    private static ApprovalApiDtos.TaskView task(ApprovalService.Task value) { return new ApprovalApiDtos.TaskView(id(value.id()), id(value.instanceId()), value.nodeNo(), value.nodeName(), value.decisionMode(), id(value.approverUserId()), value.status().name(), value.version()); }
    private static ApprovalApiDtos.ApprovalActionView result(QuoteApprovalWorkflowService.ApprovalResult value) { return new ApprovalApiDtos.ApprovalActionView(id(value.decision().instanceId()), value.decision().status().name(), value.decision().version(), value.quote() == null ? null : id(value.quote().id()), value.quote() == null ? null : value.quote().status().name(), value.quote() == null ? null : value.quote().version()); }
    private static String id(long value) { return String.valueOf(value); }
    private Actor actor() { return ActorContext.require(); }
    private <T> ApiResponse<T> success(T data) { return ApiResponse.success(data, TraceContext.get()); }
}
