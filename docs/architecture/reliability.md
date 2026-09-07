# 审计、幂等、Outbox、重试与对账规范

## 事务边界

创建、转换、合并、交接、认领、释放、分配、转移、回收和无效处理遵循同一事务模板：锁定/条件更新聚合，写 `crm_ownership_history`，写 `crm_audit_log`，写一条或多条 `crm_outbox_event`，最后提交。RabbitMQ、Redis 和外部 API 均不在事实事务内。

数据库延迟约束确保销售聚合的创建、状态和归属变化在提交前已有归属历史和审计；集成测试再验证 Outbox 完整性。审计和归属历史只允许 INSERT，不允许 UPDATE 或 DELETE。快照须脱敏，至少包含状态、归属、版本和关键引用。

## 幂等

创建、转换、合并、交接和外部回调必须带 `Idempotency-Key`；推荐认领和批量命令也使用。作用域固定为 `(tenant_id, operation, idempotency_key)`。

1. 首次请求插入 `PROCESSING` 和规范化请求体 SHA-256 `request_hash`。
2. 唯一冲突时，hash 不同返回 `IDEMPOTENCY_KEY_REUSED`；`COMPLETED` 返回原状态码和响应；`PROCESSING` 返回 409；`FAILED` 按操作策略返回原失败或原子增加尝试次数后重试。
3. 成功写 `COMPLETED/completed_at/response_body/http_status`；可重试失败写 `FAILED/failed_at/error_code`。幂等记录保留时间不得短于客户端最大重试窗口。

## Outbox 发布

发布器以 `FOR UPDATE SKIP LOCKED` 批量领取 `PENDING/FAILED` 且已到 `available_at/next_retry_at` 的记录，置 `PUBLISHING` 并记录租约；发布成功置 `PUBLISHED/published_at`。失败采用分级退避，建议 1m、5m、30m、2h、12h；达到上限置 `DEAD/dead_lettered_at`，只能由 `outbox:retry` 权限重放。

当前实现使用 1m、5m、30m、2h、12h 五级确定性退避，第六次失败进入 `DEAD`；领取时会恢复超过租约时间的 `PUBLISHING` 记录。发布调度默认关闭，需设置 `AICRM_OUTBOX_ENABLED=true`，并可通过 `AICRM_OUTBOX_INTERVAL_MILLIS`、`AICRM_OUTBOX_BATCH_SIZE`、`AICRM_OUTBOX_LEASE_SECONDS` 调整。Broker Confirm 超时或否认均按失败处理。

消息 ID 使用 Outbox `id`。消费者先插入 `crm_inbox_record`，以 `(tenant_id,consumer,message_id)` 去重；存在稳定业务键时同时使用部分唯一索引。消费者允许至少一次投递，不得假设顺序或仅依赖 RabbitMQ 去重。

Inbox 处理器与 `COMPLETED` 更新处于同一个本地数据库事务；处理异常时事务回滚并在独立事务记录 `FAILED`，后续相同消息可重试。相同消息 ID 携带不同载荷哈希必须拒绝，超过五分钟的 `PROCESSING` 记录允许重新领取。

## 对账

- 每 15 分钟检查超过发布 SLA 的 `PENDING/FAILED/PUBLISHING`，过期租约回退为 `FAILED`。
- 每日按聚合、事件类型和日期核对业务变更、审计、历史、Outbox 计数；差异进入人工队列，不直接伪造业务事实。
- 人工重放生成新的审计记录，保留原事件 ID、重试次数和错误；RabbitMQ 永远不是恢复业务状态的来源。
