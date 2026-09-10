package com.aicrm.integration.application;

import com.aicrm.integration.domain.Connector;
import com.aicrm.integration.domain.ConnectorEvent;
import com.aicrm.integration.domain.ConnectorEventReceipt;
import com.aicrm.integration.domain.ConnectorRepository;
import com.aicrm.integration.domain.ConnectorStatus;
import com.aicrm.integration.domain.ConnectorType;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.aicrm.platform.application.PlatformPrincipalService;
import com.aicrm.sales.application.SalesCommandService;
import com.aicrm.sales.application.SalesCommands;
import com.aicrm.sales.domain.lead.Lead;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registers tenant-owned shared-secret webhooks and turns only marketing captures into CRM leads.
 * Organization payloads are retained as receipts; they never mirror an external directory into CRM.
 */
@Service
public class ConnectorService {
    private final ConnectorRepository repository;
    private final IdGenerator idGenerator;
    private final IdempotencyService idempotencyService;
    private final PlatformPrincipalService principalService;
    private final PasswordEncoder passwordEncoder;
    private final SalesCommandService salesCommandService;
    private final AuditLogService auditLogService;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public ConnectorService(ConnectorRepository repository, IdGenerator idGenerator, IdempotencyService idempotencyService,
                            PlatformPrincipalService principalService, PasswordEncoder passwordEncoder,
                            SalesCommandService salesCommandService, AuditLogService auditLogService,
                            OutboxService outboxService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.idempotencyService = idempotencyService;
        this.principalService = principalService;
        this.passwordEncoder = passwordEncoder;
        this.salesCommandService = salesCommandService;
        this.auditLogService = auditLogService;
        this.outboxService = outboxService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Connector create(Actor actor, ConnectorCommands.Create command, String idempotencyKey) {
        require(actor, "connector:manage");
        return idempotencyService.execute(actor, "connector:create", idempotencyKey, command, Connector.class, () -> {
            validateCreate(actor, command);
            long id = idGenerator.nextId();
            Instant now = Instant.now();
            Connector connector = new Connector(id, actor.tenantId(), "CONN-" + id, required(command.name(), "连接器名称"),
                    command.type(), ConnectorStatus.ACTIVE, command.operatorUserId(), command.publicPoolId(),
                    passwordEncoder.encode(command.sharedSecret()), 0, now, now);
            repository.insert(connector, actor.userId());
            journal(actor, "CREATE", "CONNECTOR", connector.id(), null, safeConnector(connector), "创建连接器", "ConnectorCreated", "API");
            return connector;
        });
    }

    @Transactional(readOnly = true)
    public Connector get(Actor actor, long connectorId) {
        requireRead(actor);
        return connector(actor.tenantId(), connectorId);
    }

    @Transactional
    public Connector activate(Actor actor, long connectorId, ConnectorCommands.StatusChange command) {
        return changeStatus(actor, connectorId, command, ConnectorStatus.ACTIVE, "启用连接器", "ConnectorActivated");
    }

    @Transactional
    public Connector disable(Actor actor, long connectorId, ConnectorCommands.StatusChange command) {
        return changeStatus(actor, connectorId, command, ConnectorStatus.DISABLED, "停用连接器", "ConnectorDisabled");
    }

    @Transactional
    public ConnectorEventReceipt receive(long connectorId, String sharedSecret, JsonNode payload) {
        Connector connector = repository.findAnyTenant(connectorId)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "连接器不存在"));
        if (connector.status() != ConnectorStatus.ACTIVE || !passwordEncoder.matches(required(sharedSecret, "连接器密钥"), connector.secretHash())) {
            throw new DomainException(ErrorCode.FORBIDDEN, "连接器认证失败或已停用");
        }
        String externalEventId = requiredText(payload, "eventId", "事件编号");
        String eventType = requiredText(payload, "eventType", "事件类型");
        if (externalEventId.length() > 128 || eventType.length() > 64) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "事件编号或事件类型长度超限");
        }
        String payloadText = json(payload);
        String payloadHash = sha256(payloadText);
        var existing = repository.findEvent(connector.tenantId(), connector.id(), externalEventId);
        if (existing.isPresent()) {
            ensureSamePayload(existing.get(), payloadHash);
            return ConnectorEventReceipt.from(existing.get(), true);
        }

        Actor operator = principalService.resolve(connector.tenantId(), connector.operatorUserId());
        ConnectorEvent.Outcome outcome = ConnectorEvent.Outcome.ACCEPTED;
        Long leadId = null;
        if (connector.type() == ConnectorType.MARKETING_WEBHOOK) {
            JsonNode lead = payload.path("lead");
            if (!lead.isObject()) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "营销事件必须包含 lead 对象");
            }
            Lead created = salesCommandService.createLead(operator, new SalesCommands.CreateLead(
                    requiredText(lead, "name", "线索姓名"), text(lead, "mobile"), text(lead, "email"),
                    text(lead, "companyName"), "MARKETING", connector.connectorNo() + ":" + sha256(externalEventId).substring(0, 24),
                    text(lead, "intent"), connector.publicPoolId()), leadIdempotencyKey(connector.id(), externalEventId));
            outcome = ConnectorEvent.Outcome.LEAD_CREATED;
            leadId = created.id();
        }

        ConnectorEvent event = new ConnectorEvent(idGenerator.nextId(), connector.tenantId(), connector.id(), externalEventId,
                eventType, payloadText, payloadHash, outcome, leadId, Instant.now());
        if (!repository.insertEvent(event, operator.userId())) {
            ConnectorEvent replay = repository.findEvent(connector.tenantId(), connector.id(), externalEventId)
                    .orElseThrow(() -> new IllegalStateException("连接器事件写入未找到"));
            ensureSamePayload(replay, payloadHash);
            return ConnectorEventReceipt.from(replay, true);
        }
        Map<String, Object> receipt = new LinkedHashMap<>();
        receipt.put("connectorId", connector.id());
        receipt.put("externalEventHash", sha256(externalEventId));
        receipt.put("outcome", outcome.name());
        receipt.put("leadId", leadId);
        journal(operator, "RECEIVE", "CONNECTOR_EVENT", event.id(), null, receipt,
                "接收连接器事件", "ConnectorEventAccepted", "CALLBACK");
        return ConnectorEventReceipt.from(event, false);
    }

    private Connector changeStatus(Actor actor, long connectorId, ConnectorCommands.StatusChange command,
                                   ConnectorStatus status, String reason, String eventType) {
        require(actor, "connector:manage");
        Connector before = connector(actor.tenantId(), connectorId);
        if (before.status() == status) {
            throw new DomainException(ErrorCode.CONFLICT, "连接器已处于目标状态");
        }
        if (!repository.updateStatus(actor.tenantId(), connectorId, command.version(), status, actor.userId())) {
            throw new DomainException(ErrorCode.CONFLICT, "连接器状态已变化");
        }
        Connector after = connector(actor.tenantId(), connectorId);
        journal(actor, status.name(), "CONNECTOR", connectorId, safeConnector(before), safeConnector(after), reason, eventType, "API");
        return after;
    }

    private void validateCreate(Actor actor, ConnectorCommands.Create command) {
        if (command.type() == null || command.sharedSecret() == null || command.sharedSecret().length() < 16
                || command.sharedSecret().length() > 200) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "连接器类型和 16 至 200 位共享密钥必填");
        }
        Actor operator = principalService.resolve(actor.tenantId(), command.operatorUserId());
        if (command.type() == ConnectorType.MARKETING_WEBHOOK && !operator.permissions().contains("lead:create")) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "连接器操作用户必须拥有创建线索权限");
        }
        if (command.type() == ConnectorType.MARKETING_WEBHOOK && command.publicPoolId() == null) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "营销连接器必须配置线索公海");
        }
        if (command.type() == ConnectorType.ORGANIZATION_WEBHOOK && command.publicPoolId() != null) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "组织连接器不能配置线索公海");
        }
    }

    private Connector connector(long tenantId, long connectorId) {
        return repository.find(tenantId, connectorId)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "连接器不存在"));
    }

    private void requireRead(Actor actor) {
        if (!actor.permissions().contains("connector:read") && !actor.permissions().contains("connector:manage")) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少连接器查看权限");
        }
    }

    private void require(Actor actor, String permission) {
        if (!actor.permissions().contains(permission)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限: " + permission);
        }
    }

    private void ensureSamePayload(ConnectorEvent event, String payloadHash) {
        if (!event.payloadHash().equals(payloadHash)) {
            throw new DomainException(ErrorCode.IDEMPOTENCY_KEY_REUSED, "事件编号已用于不同请求");
        }
    }

    private void journal(Actor actor, String action, String resourceType, long resourceId, Object before, Object after,
                         String reason, String eventType, String source) {
        String beforeJson = json(before == null ? Map.of() : before);
        String afterJson = json(after == null ? Map.of() : after);
        auditLogService.record(actor, action, resourceType, resourceId, null, source, null, beforeJson, afterJson);
        outboxService.append(new DomainEvent(eventType, resourceType, resourceId, actor.tenantId(), afterJson, Instant.now()),
                null, actor.userId());
    }

    private Map<String, Object> safeConnector(Connector connector) {
        return Map.of("connectorId", connector.id(), "connectorNo", connector.connectorNo(), "name", connector.name(),
                "type", connector.type().name(), "status", connector.status().name(), "operatorUserId", connector.operatorUserId(),
                "publicPoolId", connector.publicPoolId() == null ? "" : connector.publicPoolId());
    }

    private String leadIdempotencyKey(long connectorId, String externalEventId) {
        return "connector-lead:" + connectorId + ":" + sha256(externalEventId).substring(0, 32);
    }

    private String requiredText(JsonNode object, String field, String label) {
        return required(text(object, field), label);
    }

    private String text(JsonNode object, String field) {
        JsonNode value = object.get(field);
        return value == null || value.isNull() ? null : value.asText().trim();
    }

    private String required(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, label + "不能为空");
        }
        return value.trim();
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("无法序列化连接器数据", exception);
        }
    }

    private String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 不可用", exception);
        }
    }
}
