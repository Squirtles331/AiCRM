package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.math.BigDecimal;
import java.util.Locale;

@Service
public class ApprovalService {
    private final JdbcTemplate jdbc;
    private final IdGenerator ids;
    private final AuditLogService audit;
    private final OutboxService outbox;
    private final ObjectMapper mapper;

    public ApprovalService(JdbcTemplate jdbc, IdGenerator ids, AuditLogService audit, OutboxService outbox, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.ids = ids;
        this.audit = audit;
        this.outbox = outbox;
        this.mapper = mapper;
    }

    @Transactional
    public Definition createDefinition(Actor actor, CreateDefinition command) {
        require(actor, "approval:manage");
        String code = required(command.code(), "审批编码").toUpperCase(Locale.ROOT);
        String name = required(command.name(), "审批名称");
        String resourceType = required(command.resourceType(), "资源类型").toUpperCase(Locale.ROOT);
        List<NodeCommand> nodes = command.nodes() == null ? List.of() : command.nodes();
        if (nodes.isEmpty() || nodes.size() > 20) throw validation("审批节点数量必须在 1 到 20 之间");
        long definitionId = ids.nextId();
        int definitionVersion = jdbc.queryForObject("select coalesce(max(definition_version), 0) + 1 from crm_approval_definition where tenant_id=? and resource_type=? and code=?",
                Integer.class, actor.tenantId(), resourceType, code);
        jdbc.update("insert into crm_approval_definition (id,tenant_id,code,name,resource_type,definition_version,status,version,created_by,updated_by) values (?,?,?,?,?,?,'DRAFT',0,?,?)",
                definitionId, actor.tenantId(), code, name, resourceType, definitionVersion, actor.userId(), actor.userId());
        for (int index = 0; index < nodes.size(); index++) {
            NodeCommand node = nodes.get(index);
            String mode = required(node.decisionMode(), "节点决策方式").toUpperCase(Locale.ROOT);
            if (!List.of("ANY", "ALL").contains(mode)) throw validation("节点决策方式必须为 ANY 或 ALL");
            String condition = normalizeCondition(node.conditionExpression());
            List<Long> approvers = node.approverUserIds() == null ? List.of() : node.approverUserIds().stream().distinct().toList();
            if (approvers.isEmpty()) throw validation("每个节点至少指定一位审批人");
            for (Long approverId : approvers) {
                if (approverId == null || !activeUser(actor.tenantId(), approverId)) throw validation("审批人不存在、已停用或不属于当前租户");
                jdbc.update("insert into crm_approval_definition_node (id,tenant_id,definition_id,node_no,name,decision_mode,approver_user_id,condition_expression,created_by,updated_by) values (?,?,?,?,?,?,?,?::jsonb,?,?)",
                        ids.nextId(), actor.tenantId(), definitionId, index + 1, required(node.name(), "节点名称"), mode, approverId, condition, actor.userId(), actor.userId());
            }
        }
        Definition result = definition(actor.tenantId(), definitionId);
        journal(actor, "APPROVAL_DEFINITION_CREATE", "APPROVAL_DEFINITION", definitionId, "ApprovalDefinitionCreated", result);
        return result;
    }

    @Transactional
    public Definition activateDefinition(Actor actor, long definitionId, long expectedVersion) {
        require(actor, "approval:manage");
        Definition before = definition(actor.tenantId(), definitionId);
        if (before.status() != DefinitionStatus.DRAFT) throw conflict("只有草稿审批定义可以启用");
        if (nodes(actor.tenantId(), definitionId).isEmpty()) throw validation("审批定义至少需要一个节点");
        jdbc.update("update crm_approval_definition set status='RETIRED',updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and resource_type=? and code=? and status='ACTIVE' and deleted_at is null",
                actor.userId(), actor.tenantId(), before.resourceType(), before.code());
        if (jdbc.update("update crm_approval_definition set status='ACTIVE',updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and id=? and status='DRAFT' and version=? and deleted_at is null",
                actor.userId(), actor.tenantId(), definitionId, expectedVersion) != 1) throw conflict("审批定义已被其他操作修改");
        Definition result = definition(actor.tenantId(), definitionId);
        journal(actor, "APPROVAL_DEFINITION_ACTIVATE", "APPROVAL_DEFINITION", definitionId, "ApprovalDefinitionActivated", result);
        return result;
    }

    @Transactional
    public Instance start(Actor actor, StartApproval command) {
        Definition definition = activeDefinition(actor.tenantId(), command.resourceType(), command.definitionCode());
        List<DefinitionNode> definitionNodes = nodes(actor.tenantId(), definition.id());
        if (definitionNodes.isEmpty()) throw validation("审批定义没有节点");
        List<SnapshotNode> snapshotNodes = definitionNodes.stream().collect(java.util.stream.Collectors.groupingBy(
                DefinitionNode::nodeNo, java.util.LinkedHashMap::new, java.util.stream.Collectors.toList())).values().stream()
                .map(group -> new SnapshotNode(group.get(0).nodeNo(), group.get(0).name(), group.get(0).decisionMode(),
                        group.stream().map(DefinitionNode::approverUserId).toList(), group.get(0).conditionExpression())).toList();
        Snapshot snapshot = new Snapshot(definition.code(), definition.definitionVersion(), snapshotNodes, command.businessSnapshot());
        long instanceId = ids.nextId();
        jdbc.update("insert into crm_approval_instance (id,tenant_id,resource_type,resource_id,definition_id,definition_code,definition_version,definition_snapshot,applicant_user_id,status,current_node_no,version,created_by,updated_by) values (?,?,?,?,?,?,?,?::jsonb,?,'PENDING',?,0,?,?)",
                instanceId, actor.tenantId(), command.resourceType().toUpperCase(), command.resourceId(), definition.id(), definition.code(),
                definition.definitionVersion(), json(snapshot), actor.userId(), snapshotNodes.get(0).nodeNo(), actor.userId(), actor.userId());
        SnapshotNode first = nextApplicableNode(snapshotNodes, 0, command.businessSnapshot());
        if (first == null) throw validation("审批定义没有适用节点");
        jdbc.update("update crm_approval_instance set current_node_no=? where tenant_id=? and id=?", first.nodeNo(), actor.tenantId(), instanceId);
        activateNode(actor, instanceId, first);
        Instance result = instance(actor.tenantId(), instanceId);
        action(actor, instanceId, null, "START", null, result);
        journal(actor, "APPROVAL_START", "APPROVAL", instanceId, "ApprovalStarted", result);
        return result;
    }

    @Transactional
    public Decision approve(Actor actor, long taskId, long expectedVersion, String comment) {
        Task task = task(actor.tenantId(), taskId);
        requireTaskActor(actor, task);
        if (jdbc.update("update crm_approval_task set status='APPROVED',acted_at=now(),comment=?,updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and id=? and approver_user_id=? and status='PENDING' and version=? and deleted_at is null",
                optional(comment), actor.userId(), actor.tenantId(), taskId, actor.userId(), expectedVersion) != 1) throw conflict("审批任务已被其他操作修改");
        Instance instance = instance(actor.tenantId(), task.instanceId());
        action(actor, instance.id(), taskId, "APPROVE", null, task(actor.tenantId(), taskId));
        Decision decision = advanceAfterApproval(actor, instance, task);
        journal(actor, "APPROVAL_TASK_APPROVE", "APPROVAL", instance.id(), "ApprovalTaskApproved", decision);
        return decision;
    }

    @Transactional
    public Decision reject(Actor actor, long taskId, long expectedVersion, String comment) {
        Task task = task(actor.tenantId(), taskId);
        requireTaskActor(actor, task);
        if (jdbc.update("update crm_approval_task set status='REJECTED',acted_at=now(),comment=?,updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and id=? and approver_user_id=? and status='PENDING' and version=? and deleted_at is null",
                required(comment, "驳回意见"), actor.userId(), actor.tenantId(), taskId, actor.userId(), expectedVersion) != 1) throw conflict("审批任务已被其他操作修改");
        Instance instance = instance(actor.tenantId(), task.instanceId());
        jdbc.update("update crm_approval_task set status='CANCELLED',updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and instance_id=? and node_no=? and status='PENDING'",
                actor.userId(), actor.tenantId(), instance.id(), task.nodeNo());
        if (jdbc.update("update crm_approval_instance set status='REJECTED',current_node_no=null,completed_at=now(),updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and id=? and status='PENDING' and version=?",
                actor.userId(), actor.tenantId(), instance.id(), instance.version()) != 1) throw conflict("审批实例已被其他操作修改");
        Instance result = instance(actor.tenantId(), instance.id());
        action(actor, result.id(), taskId, "REJECT", null, result);
        Decision decision = decision(result);
        journal(actor, "APPROVAL_TASK_REJECT", "APPROVAL", result.id(), "ApprovalRejected", decision);
        return decision;
    }

    @Transactional
    public Task transfer(Actor actor, long taskId, long expectedVersion, long toUserId, String comment) {
        Task task = task(actor.tenantId(), taskId);
        requireTaskActor(actor, task);
        if (toUserId == actor.userId() || !activeUser(actor.tenantId(), toUserId)) throw validation("转交对象必须是当前租户的其他启用用户");
        if (jdbc.update("update crm_approval_task set status='TRANSFERRED',acted_at=now(),comment=?,updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and id=? and approver_user_id=? and status='PENDING' and version=?",
                optional(comment), actor.userId(), actor.tenantId(), taskId, actor.userId(), expectedVersion) != 1) throw conflict("审批任务已被其他操作修改");
        long replacementId = ids.nextId();
        jdbc.update("insert into crm_approval_task (id,tenant_id,instance_id,node_no,node_name,decision_mode,approver_user_id,status,version,transferred_from_task_id,created_by,updated_by) values (?,?,?,?,?,?,?,'PENDING',0,?,?,?)",
                replacementId, actor.tenantId(), task.instanceId(), task.nodeNo(), task.nodeName(), task.decisionMode(), toUserId, taskId, actor.userId(), actor.userId());
        Task result = task(actor.tenantId(), replacementId);
        action(actor, task.instanceId(), taskId, "TRANSFER", task, result);
        journal(actor, "APPROVAL_TASK_TRANSFER", "APPROVAL", task.instanceId(), "ApprovalTaskTransferred", result);
        return result;
    }

    @Transactional
    public Decision withdraw(Actor actor, long instanceId, long expectedVersion, String comment) {
        Instance instance = instance(actor.tenantId(), instanceId);
        if (instance.applicantUserId() != actor.userId() && !actor.hasPermission("approval:manage")) throw forbidden("只有申请人可以撤回审批");
        if (jdbc.update("update crm_approval_instance set status='WITHDRAWN',current_node_no=null,completed_at=now(),updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and id=? and status='PENDING' and version=?",
                actor.userId(), actor.tenantId(), instanceId, expectedVersion) != 1) throw conflict("审批实例已被其他操作修改");
        jdbc.update("update crm_approval_task set status='CANCELLED',updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and instance_id=? and status='PENDING'",
                actor.userId(), actor.tenantId(), instanceId);
        Instance result = instance(actor.tenantId(), instanceId);
        action(actor, instanceId, null, "WITHDRAW", null, result);
        Decision decision = decision(result);
        journal(actor, "APPROVAL_WITHDRAW", "APPROVAL", instanceId, "ApprovalWithdrawn", decision);
        return decision;
    }

    public List<Task> pendingTasks(Actor actor) {
        require(actor, "approval:task:read");
        return jdbc.query("select * from crm_approval_task where tenant_id=? and approver_user_id=? and status='PENDING' and deleted_at is null order by created_at desc",
                (rs, row) -> task(rs), actor.tenantId(), actor.userId());
    }

    public Instance instance(long tenantId, long id) {
        return jdbc.query("select * from crm_approval_instance where tenant_id=? and id=? and deleted_at is null", (rs, row) -> instance(rs), tenantId, id)
                .stream().findFirst().orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "审批实例不存在"));
    }

    private Decision advanceAfterApproval(Actor actor, Instance instance, Task approvedTask) {
        List<Task> nodeTasks = jdbc.query("select * from crm_approval_task where tenant_id=? and instance_id=? and node_no=? and deleted_at is null", (rs, row) -> task(rs), actor.tenantId(), instance.id(), approvedTask.nodeNo());
        boolean complete = "ANY".equals(approvedTask.decisionMode()) || nodeTasks.stream().allMatch(task -> task.status() == TaskStatus.APPROVED);
        if (!complete) return decision(instance(actor.tenantId(), instance.id()));
        if ("ANY".equals(approvedTask.decisionMode())) jdbc.update("update crm_approval_task set status='CANCELLED',updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and instance_id=? and node_no=? and status='PENDING'",
                actor.userId(), actor.tenantId(), instance.id(), approvedTask.nodeNo());
        Snapshot snapshot = snapshot(instance.definitionSnapshot());
        SnapshotNode next = nextApplicableNode(snapshot.nodes(), approvedTask.nodeNo(), snapshot.businessSnapshot());
        if (next == null) {
            if (jdbc.update("update crm_approval_instance set status='APPROVED',current_node_no=null,completed_at=now(),updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and id=? and status='PENDING' and version=?",
                    actor.userId(), actor.tenantId(), instance.id(), instance.version()) != 1) throw conflict("审批实例已被其他操作修改");
        } else {
            if (jdbc.update("update crm_approval_instance set current_node_no=?,updated_by=?,updated_at=now(),version=version+1 where tenant_id=? and id=? and status='PENDING' and version=?",
                    next.nodeNo(), actor.userId(), actor.tenantId(), instance.id(), instance.version()) != 1) throw conflict("审批实例已被其他操作修改");
            activateNode(actor, instance.id(), next);
        }
        Instance result = instance(actor.tenantId(), instance.id());
        action(actor, result.id(), null, "ACTIVATE_NODE", instance, result);
        return decision(result);
    }

    private void activateNode(Actor actor, long instanceId, SnapshotNode node) {
        for (Long approverId : node.approverUserIds()) jdbc.update("insert into crm_approval_task (id,tenant_id,instance_id,node_no,node_name,decision_mode,approver_user_id,status,version,created_by,updated_by) values (?,?,?,?,?,?,?,'PENDING',0,?,?)",
                ids.nextId(), actor.tenantId(), instanceId, node.nodeNo(), node.name(), node.decisionMode(), approverId, actor.userId(), actor.userId());
    }

    private void action(Actor actor, long instanceId, Long taskId, String name, Object before, Object after) {
        jdbc.update("insert into crm_approval_action (id,tenant_id,instance_id,task_id,action,actor_user_id,comment,before_snapshot,after_snapshot,operation_id,trace_id) values (?,?,?,?,?,?,null,?::jsonb,?::jsonb,?,null)",
                ids.nextId(), actor.tenantId(), instanceId, taskId, name, actor.userId(), json(before == null ? java.util.Map.of() : before), json(after), operationId());
    }

    private Definition activeDefinition(long tenantId, String resourceType, String code) {
        return jdbc.query("select * from crm_approval_definition where tenant_id=? and resource_type=? and code=? and status='ACTIVE' and deleted_at is null",
                (rs, row) -> definition(rs), tenantId, resourceType.toUpperCase(), code.toUpperCase()).stream().findFirst()
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "未找到启用的审批定义"));
    }

    private Definition definition(long tenantId, long id) {
        return jdbc.query("select * from crm_approval_definition where tenant_id=? and id=? and deleted_at is null", (rs, row) -> definition(rs), tenantId, id)
                .stream().findFirst().orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "审批定义不存在"));
    }

    private List<DefinitionNode> nodes(long tenantId, long definitionId) {
        return jdbc.query("select *, condition_expression::text as condition_text from crm_approval_definition_node where tenant_id=? and definition_id=? and deleted_at is null order by node_no,id", (rs, row) -> new DefinitionNode(
                rs.getLong("id"), rs.getInt("node_no"), rs.getString("name"), rs.getString("decision_mode"), rs.getLong("approver_user_id"), rs.getString("condition_text")), tenantId, definitionId);
    }

    private Task task(long tenantId, long id) {
        return jdbc.query("select * from crm_approval_task where tenant_id=? and id=? and deleted_at is null", (rs, row) -> task(rs), tenantId, id)
                .stream().findFirst().orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "审批任务不存在"));
    }

    private Definition definition(java.sql.ResultSet rs) throws java.sql.SQLException { return new Definition(rs.getLong("id"), rs.getString("code"), rs.getString("name"), rs.getString("resource_type"), rs.getInt("definition_version"), DefinitionStatus.valueOf(rs.getString("status")), rs.getLong("version")); }
    private Instance instance(java.sql.ResultSet rs) throws java.sql.SQLException { return new Instance(rs.getLong("id"), rs.getString("resource_type"), rs.getLong("resource_id"), rs.getLong("definition_id"), rs.getString("definition_code"), rs.getInt("definition_version"), rs.getString("definition_snapshot"), rs.getLong("applicant_user_id"), InstanceStatus.valueOf(rs.getString("status")), (Integer) rs.getObject("current_node_no"), rs.getLong("version")); }
    private Task task(java.sql.ResultSet rs) throws java.sql.SQLException { return new Task(rs.getLong("id"), rs.getLong("instance_id"), rs.getInt("node_no"), rs.getString("node_name"), rs.getString("decision_mode"), rs.getLong("approver_user_id"), TaskStatus.valueOf(rs.getString("status")), rs.getLong("version")); }
    private Decision decision(Instance instance) { return new Decision(instance.id(), instance.resourceType(), instance.resourceId(), instance.status(), instance.version()); }
    private SnapshotNode nextApplicableNode(List<SnapshotNode> nodes, int afterNodeNo, Object businessSnapshot) {
        return nodes.stream().filter(node -> node.nodeNo() > afterNodeNo && conditionMatches(node.conditionExpression(), businessSnapshot))
                .min(Comparator.comparingInt(SnapshotNode::nodeNo)).orElse(null);
    }
    private String normalizeCondition(String value) {
        if (value == null || value.isBlank()) return "{}";
        try {
            JsonNode condition = mapper.readTree(value);
            if (!condition.isObject()) throw validation("审批条件必须是 JSON 对象");
            String operator = condition.path("operator").asText("ALWAYS").toUpperCase(Locale.ROOT);
            if (!List.of("ALWAYS", "GT", "GTE", "LT", "LTE", "EQ").contains(operator)) throw validation("审批条件操作符不支持");
            if (!"ALWAYS".equals(operator) && (condition.path("field").asText().isBlank() || !condition.path("value").isNumber())) {
                throw validation("比较条件必须包含 field 和 value");
            }
            return condition.toString();
        } catch (JsonProcessingException exception) { throw validation("审批条件不是有效 JSON"); }
    }
    private boolean conditionMatches(String expression, Object businessSnapshot) {
        JsonNode condition;
        try { condition = mapper.readTree(expression); } catch (JsonProcessingException exception) { throw new IllegalStateException("审批条件快照损坏", exception); }
        String operator = condition.path("operator").asText("ALWAYS").toUpperCase(Locale.ROOT);
        if ("ALWAYS".equals(operator)) return true;
        JsonNode business = mapper.valueToTree(businessSnapshot);
        JsonNode actual = business.path(condition.path("field").asText());
        if (actual.isMissingNode() || actual.isNull()) return false;
        if (!actual.isNumber() || !condition.path("value").isNumber()) throw new IllegalStateException("审批条件值必须是数字");
        BigDecimal left;
        BigDecimal right;
        try { left = actual.decimalValue(); right = condition.path("value").decimalValue(); }
        catch (RuntimeException exception) { throw new IllegalStateException("审批条件值必须是数字", exception); }
        int compared = left.compareTo(right);
        return switch (operator) { case "GT" -> compared > 0; case "GTE" -> compared >= 0; case "LT" -> compared < 0; case "LTE" -> compared <= 0; case "EQ" -> compared == 0; default -> throw new IllegalStateException("审批条件操作符快照损坏"); };
    }
    private Snapshot snapshot(String value) { try { return mapper.readValue(value, Snapshot.class); } catch (JsonProcessingException e) { throw new IllegalStateException("审批定义快照损坏", e); } }
    private boolean activeUser(long tenantId, long userId) { Boolean exists = jdbc.query("select exists(select 1 from crm_user where tenant_id=? and id=? and status=1 and deleted_at is null)", rs -> rs.next() && rs.getBoolean(1), tenantId, userId); return Boolean.TRUE.equals(exists); }
    private void journal(Actor actor, String action, String resourceType, long resourceId, String event, Object payload) { String operation = operationId(); String json = json(payload); audit.record(actor, action, resourceType, resourceId, operation, "API", null, "{}", json); outbox.append(new DomainEvent(event, "APPROVAL", resourceId, actor.tenantId(), json, Instant.now()), operation, actor.userId()); }
    private String operationId() { return "op-" + ids.nextId(); }
    private void require(Actor actor, String permission) { if (!actor.hasPermission(permission)) throw forbidden("缺少权限：" + permission); }
    private void requireTaskActor(Actor actor, Task task) { require(actor, "approval:task:act"); if (task.approverUserId() != actor.userId()) throw forbidden("无权处理该审批任务"); }
    private String required(String value, String field) { if (value == null || value.isBlank()) throw validation(field + "不能为空"); return value.trim(); }
    private String optional(String value) { return value == null ? null : value.trim(); }
    private String json(Object value) { try { return mapper.writeValueAsString(value); } catch (JsonProcessingException e) { throw new IllegalStateException("无法序列化审批数据", e); } }
    private DomainException validation(String message) { return new DomainException(ErrorCode.VALIDATION_ERROR, message); }
    private DomainException conflict(String message) { return new DomainException(ErrorCode.CONFLICT, message); }
    private DomainException forbidden(String message) { return new DomainException(ErrorCode.FORBIDDEN, message); }

    public record CreateDefinition(String code, String name, String resourceType, List<NodeCommand> nodes) { }
    public record NodeCommand(String name, String decisionMode, List<Long> approverUserIds, String conditionExpression) { }
    public record StartApproval(String resourceType, long resourceId, String definitionCode, Object businessSnapshot) { }
    public record Definition(long id, String code, String name, String resourceType, int definitionVersion, DefinitionStatus status, long version) { }
    public record DefinitionNode(long id, int nodeNo, String name, String decisionMode, long approverUserId, String conditionExpression) { }
    public record Instance(long id, String resourceType, long resourceId, long definitionId, String definitionCode, int definitionVersion, String definitionSnapshot, long applicantUserId, InstanceStatus status, Integer currentNodeNo, long version) { }
    public record Task(long id, long instanceId, int nodeNo, String nodeName, String decisionMode, long approverUserId, TaskStatus status, long version) { }
    public record Decision(long instanceId, String resourceType, long resourceId, InstanceStatus status, long version) { }
    public record Snapshot(String code, int version, List<SnapshotNode> nodes, Object businessSnapshot) { }
    public record SnapshotNode(int nodeNo, String name, String decisionMode, List<Long> approverUserIds, String conditionExpression) { }
    public enum DefinitionStatus { DRAFT, ACTIVE, RETIRED, DISABLED }
    public enum InstanceStatus { PENDING, APPROVED, REJECTED, WITHDRAWN }
    public enum TaskStatus { PENDING, APPROVED, REJECTED, TRANSFERRED, CANCELLED }
}
