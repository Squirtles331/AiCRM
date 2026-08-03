package com.aicrm.module.conversation.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.conversation.dto.TransferPackage;
import com.aicrm.module.conversation.entity.Conversation;
import com.aicrm.module.conversation.entity.Message;
import com.aicrm.module.conversation.service.AiChatService;
import com.aicrm.module.conversation.service.ConversationService;
import com.aicrm.module.conversation.service.MessageService;
import com.aicrm.module.conversation.service.TransferPackageService;
import com.aicrm.module.log.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会话接口（3.3.1 生命周期管理：创建/查询/关闭/归档/状态流转）
 */
@Tag(name = "会话")
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final TransferPackageService transferPackageService;
    private final MessageService messageService;
    private final AiChatService aiChatService;

    @Operation(summary = "创建会话",
            description = "会话管理 - 创建会话（权限 conversation:edit），承接企微/WhatsApp/私信客户，是会话生命周期起点。\n\n"
                    + "必填：tenantId、leadId；status 不传时默认 active。")
    @ApiResponse(responseCode = "400", description = "tenantId 与 leadId 不能为空")
    @OperLog(module = "会话管理", operation = "创建会话")
    @RequirePermission(perms = "conversation:edit")
    @PostMapping
    public Result<Conversation> create(@Valid @RequestBody Conversation conversation) {
        return Result.ok(conversationService.createConversation(conversation));
    }

    @Operation(summary = "会话分页查询",
            description = "会话管理 - 会话分页查询（权限 conversation:list），支持关键字（会话类型/ID）、状态、处理人过滤。\n\n"
                    + "status 枚举：active进行中/transferred已转人工/closed已关闭/archived已归档。")
    @RequirePermission(perms = "conversation:list")
    @GetMapping
    public Result<PageResult<Conversation>> page(
            @Parameter(description = "关键字（模糊匹配会话类型/会话 ID）") @RequestParam(required = false) String keyword,
            @Parameter(description = "会话状态：active进行中/transferred已转人工/closed已关闭/archived已归档") @RequestParam(required = false) String status,
            @Parameter(description = "当前人工处理人 ID") @RequestParam(required = false) Long assignedTo,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(conversationService.page(keyword, status, assignedTo, page, size));
    }

    @Operation(summary = "会话详情",
            description = "会话管理 - 会话详情（权限 conversation:list）。")
    @RequirePermission(perms = "conversation:list")
    @GetMapping("/{id}")
    public Result<Conversation> detail(@Parameter(description = "会话 ID", required = true) @PathVariable Long id) {
        return Result.ok(conversationService.detail(id));
    }

    @Operation(summary = "会话消息分页（历史漫游）",
            description = "会话管理 - 消息分页查询（权限 message:list），可按发送方过滤，消息按 ID 升序返回。\n\n"
                    + "senderType 枚举：customer客户/ai机器人/human人工/system系统。")
    @RequirePermission(perms = "message:list")
    @GetMapping("/{id}/messages")
    public Result<PageResult<Message>> messages(
            @Parameter(description = "会话 ID", required = true) @PathVariable Long id,
            @Parameter(description = "发送方：customer客户/ai机器人/human人工/system系统") @RequestParam(required = false) String senderType,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(messageService.page(id, senderType, page, size));
    }

    @Operation(summary = "追加消息",
            description = "会话管理 - 追加一条消息（权限 message:add），用于人工/系统补录消息。\n\n"
                    + "请求示例：\n"
                    + "```json\n"
                    + "{\n"
                    + "  \"tenantId\": 1,\n"
                    + "  \"conversationId\": 100,\n"
                    + "  \"senderType\": \"human\",\n"
                    + "  \"content\": \"您好，关于报价需求我已整理，请补充采购数量\",\n"
                    + "  \"msgType\": \"text\",\n"
                    + "  \"aiGenerated\": false\n"
                    + "}\n"
                    + "```\n"
                    + "返回示例：\n"
                    + "```json\n"
                    + "{\n"
                    + "  \"code\": 200,\n"
                    + "  \"message\": \"success\",\n"
                    + "  \"data\": {\n"
                    + "    \"id\": 1001,\n"
                    + "    \"tenantId\": 1,\n"
                    + "    \"conversationId\": 100,\n"
                    + "    \"senderType\": \"human\",\n"
                    + "    \"content\": \"您好，关于报价需求我已整理，请补充采购数量\",\n"
                    + "    \"msgType\": \"text\",\n"
                    + "    \"aiGenerated\": false,\n"
                    + "    \"createdAt\": \"2026-08-03 12:00:00\",\n"
                    + "    \"updatedAt\": \"2026-08-03 12:00:00\"\n"
                    + "  }\n"
                    + "}\n"
                    + "```\n"
                    + "注意：conversationId 以路径参数为准，请求体中的值会被覆盖；msgType 不传默认 text。")
    @ApiResponse(responseCode = "400", description = "conversationId/消息内容不能为空")
    @ApiResponse(responseCode = "1401", description = "会话不存在")
    @OperLog(module = "会话管理", operation = "追加消息")
    @RequirePermission(perms = "message:add")
    @PostMapping("/{id}/messages")
    public Result<Message> appendMessage(
            @Parameter(description = "会话 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody Message message) {
        message.setConversationId(id);
        return Result.ok(conversationService.appendMessage(message));
    }

    @Operation(summary = "关闭会话",
            description = "会话管理 - 关闭会话（权限 conversation:edit），已归档会话不可关闭，关闭后状态置为 closed。")
    @ApiResponse(responseCode = "400", description = "已归档会话不能关闭")
    @ApiResponse(responseCode = "1401", description = "会话不存在")
    @OperLog(module = "会话管理", operation = "关闭会话")
    @RequirePermission(perms = "conversation:edit")
    @PostMapping("/{id}/close")
    public Result<Conversation> close(@Parameter(description = "会话 ID", required = true) @PathVariable Long id) {
        return Result.ok(conversationService.close(id));
    }

    @Operation(summary = "归档会话",
            description = "会话管理 - 归档会话（权限 conversation:edit），归档后状态置为 archived。")
    @ApiResponse(responseCode = "1401", description = "会话不存在")
    @OperLog(module = "会话管理", operation = "归档会话")
    @RequirePermission(perms = "conversation:edit")
    @PostMapping("/{id}/archive")
    public Result<Conversation> archive(@Parameter(description = "会话 ID", required = true) @PathVariable Long id) {
        return Result.ok(conversationService.archive(id));
    }

    @Operation(summary = "AI 接待（消息落库 → 意向判定 → 生成回复）",
            description = "会话管理 - AI 接待入口（权限 message:add）：客户消息落库 → 意向判定 → 生成 AI 回复（AI 不可用降级规则话术）→ 回复落库。\n\n"
                    + "请求体为 JSON 字符串（裸字符串），例如：\"我想了解贵司产品的报价\"\n"
                    + "返回 AiReply 字段：content 回复内容、intent 意向（quote/sample/selection/other）、confidence 置信度、shouldTransfer 是否建议转人工。")
    @ApiResponse(responseCode = "1401", description = "会话不存在")
    @OperLog(module = "会话管理", operation = "AI 接待")
    @RequirePermission(perms = "message:add")
    @PostMapping("/{id}/ai-reply")
    public Result<AiChatService.AiReply> aiReply(
            @Parameter(description = "会话 ID", required = true) @PathVariable Long id,
            @Parameter(description = "客户消息内容（JSON 字符串，如 \"我想了解报价\"）", required = true) @RequestBody String customerMessage) {
        return Result.ok(aiChatService.handleIncoming(id, customerMessage));
    }

    @Operation(summary = "转人工条件评估",
            description = "会话管理 - 转人工条件评估（权限 conversation:list）：基于最近意向判定结果判断是否需要转人工。\n\n"
                    + "触发条件：意向为 other / 置信度低于 0.5 / 尚无意向判定。\n"
                    + "返回 TransferEvaluation 字段：shouldTransfer 是否建议转人工、intent 意向、confidence 置信度、reason 判定原因。")
    @RequirePermission(perms = "conversation:list")
    @GetMapping("/{id}/transfer-evaluation")
    public Result<AiChatService.TransferEvaluation> evaluateTransfer(
            @Parameter(description = "会话 ID", required = true) @PathVariable Long id) {
        return Result.ok(aiChatService.evaluate(id));
    }

    @Operation(summary = "转人工并生成交接包（不传 operatorId 时自动分配销售）",
            description = "会话管理 - 转人工并生成交接包（权限 conversation:edit）：会话状态置为 transferred、assignedTo 指向接手坐席，返回 AI 摘要 + 最近意向 + 缺失字段 + 推荐话术的交接包。\n\n"
                    + "operatorId 不传时优先线索负责人，否则走分配引擎自动分配；无可用坐席时返回 400。\n\n"
                    + "请求示例：\n"
                    + "```\n"
                    + "POST /api/conversations/100/transfer?operatorId=8\n"
                    + "```\n"
                    + "返回示例：\n"
                    + "```json\n"
                    + "{\n"
                    + "  \"code\": 200,\n"
                    + "  \"message\": \"success\",\n"
                    + "  \"data\": {\n"
                    + "    \"conversationId\": 100,\n"
                    + "    \"tenantId\": 1,\n"
                    + "    \"leadId\": 50,\n"
                    + "    \"conversationType\": \"wecom_chat\",\n"
                    + "    \"operatorId\": 8,\n"
                    + "    \"summary\": \"客户咨询产品报价，已确认应用场景，采购数量与预算待补充\",\n"
                    + "    \"summarySource\": \"ai\",\n"
                    + "    \"intent\": \"quote\",\n"
                    + "    \"confidence\": 0.82,\n"
                    + "    \"evidence\": \"客户提到需要 500 台，希望本周内拿到报价\",\n"
                    + "    \"missingFields\": [\"qty\", \"budget\"],\n"
                    + "    \"recommendedReply\": \"您好，我是人工顾问，已为您整理报价需求。为准确报价，请补充：应用场景、采购数量、预算范围、期望交期，我尽快给您正式报价。\",\n"
                    + "    \"nextSteps\": [\n"
                    + "      \"AI 已生成对话摘要，坐席确认后回复客户\",\n"
                    + "      \"补齐报价关键字段（qty/budget）\",\n"
                    + "      \"字段齐全后生成报价单，跟进报价意向\",\n"
                    + "      \"低置信度/复杂问题已转人工，坐席优先响应\"\n"
                    + "    ],\n"
                    + "    \"transferredAt\": \"2026-08-03 14:30:00\"\n"
                    + "  }\n"
                    + "}\n"
                    + "```")
    @ApiResponse(responseCode = "400", description = "暂无可接手的在线坐席/operatorId 不能为空")
    @ApiResponse(responseCode = "1401", description = "会话不存在")
    @OperLog(module = "会话管理", operation = "转人工并生成交接包")
    @RequirePermission(perms = "conversation:edit")
    @PostMapping("/{id}/transfer")
    public Result<TransferPackage> transfer(
            @Parameter(description = "会话 ID", required = true) @PathVariable Long id,
            @Parameter(description = "接手坐席 ID（可空，不传时自动分配）") @RequestParam(required = false) Long operatorId) {
        return Result.ok(transferPackageService.build(id, operatorId));
    }

    @Operation(summary = "查询交接包（坐席接手后查看）",
            description = "会话管理 - 查询交接包（权限 conversation:list）：坐席接手后查看会话上下文（对话摘要 + 意向 + 缺失字段 + 推荐话术）。\n\n"
                    + "返回示例：\n"
                    + "```json\n"
                    + "{\n"
                    + "  \"code\": 200,\n"
                    + "  \"message\": \"success\",\n"
                    + "  \"data\": {\n"
                    + "    \"conversationId\": 100,\n"
                    + "    \"tenantId\": 1,\n"
                    + "    \"leadId\": 50,\n"
                    + "    \"conversationType\": \"wecom_chat\",\n"
                    + "    \"operatorId\": 8,\n"
                    + "    \"summary\": \"AI 摘要服务暂不可用，请坐席查看最近 20 条消息记录。\",\n"
                    + "    \"summarySource\": \"fallback\",\n"
                    + "    \"intent\": \"selection\",\n"
                    + "    \"confidence\": 0.76,\n"
                    + "    \"evidence\": \"客户表示对型号选型不确定，需要对比推荐\",\n"
                    + "    \"missingFields\": [\"scene\", \"qty\", \"budget\", \"lead_time\", \"model\"],\n"
                    + "    \"recommendedReply\": \"您好，已收到您的选型需求。为给出精准推荐，请补充应用场景与工况要求，我们马上为您选型。\",\n"
                    + "    \"nextSteps\": [\n"
                    + "      \"AI 已生成对话摘要，坐席确认后回复客户\",\n"
                    + "      \"输出选型对比表（推荐 2-3 款）\",\n"
                    + "      \"选型确认后引导报价\",\n"
                    + "      \"低置信度/复杂问题已转人工，坐席优先响应\"\n"
                    + "    ],\n"
                    + "    \"transferredAt\": \"2026-08-03 14:30:00\"\n"
                    + "  }\n"
                    + "}\n"
                    + "```")
    @RequirePermission(perms = "conversation:list")
    @GetMapping("/{id}/transfer-package")
    public Result<TransferPackage> getTransferPackage(
            @Parameter(description = "会话 ID", required = true) @PathVariable Long id) {
        return Result.ok(transferPackageService.get(id));
    }
}
