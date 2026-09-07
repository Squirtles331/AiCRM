# 错误码目录

响应格式固定为 `code/message/data/traceId`，HTTP 状态码表达协议语义，业务码稳定且不暴露 SQL、表名或跨租户对象存在性。

| HTTP | 错误码 | 触发条件 |
|---:|---|---|
| 400 | `VALIDATION_ERROR` | 字段缺失、格式或枚举非法 |
| 400 | `INVALID_STATE_TRANSITION` | 状态机不允许当前操作 |
| 400 | `INVALID_OWNERSHIP` | 私海/公海参数不完整或互斥失败 |
| 401 | `UNAUTHENTICATED` | 凭证无效或过期 |
| 403 | `PERMISSION_DENIED` | 功能、数据范围或字段权限不足 |
| 404 | `RESOURCE_NOT_FOUND` | 对象不存在、已删除或不属于当前租户 |
| 409 | `VERSION_CONFLICT` | 乐观锁版本不一致 |
| 409 | `RESOURCE_ALREADY_CLAIMED` | 并发认领已由其他请求成功 |
| 409 | `DUPLICATE_LEAD_NO` | 活跃线索编号重复 |
| 409 | `DUPLICATE_CUSTOMER_NO` | 活跃客户编号重复 |
| 409 | `DUPLICATE_CUSTOMER_NAME` | 同租户存在同名活跃客户 |
| 409 | `IDEMPOTENCY_IN_PROGRESS` | 同幂等请求尚在处理 |
| 409 | `IDEMPOTENCY_KEY_REUSED` | 相同键对应不同请求 hash |
| 422 | `PUBLIC_POOL_DISABLED` | 公海已停用或对应动作关闭 |
| 422 | `MERGE_TARGET_INVALID` | 合并目标无效、跨租户或等于源客户 |
| 422 | `HANDOVER_TARGET_INVALID` | 接收人停用、跨租户或等于原负责人 |
| 500 | `INTERNAL_ERROR` | 未分类服务端异常，响应仅带 traceId |
| 503 | `EVENT_PUBLISH_UNAVAILABLE` | 管理端同步重放暂不可用；业务事实仍已保存在数据库 |
