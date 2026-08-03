# 压测接口清单（JMeter 对齐）

> 版本：v1.0　用途：性能测试核心接口清单，参数/返回结构与实际接口 100% 对齐，支持 JMeter 导入后直接生成压测脚本。
> 环境：BASE_URL=`http://localhost:8080`；除登录外均需请求头 `Authorization: Bearer {token}`（压测前先跑登录接口提取 token 存变量）。

## 一、核心压测接口

| # | 场景 | 方法 | 路径 | 关键参数 | 返回结构 | 说明 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 登录 | POST | /api/auth/login | body：`{"mobile":"...","password":"..."}` | Result{data:{token,...}} | 鉴权前置，压测时先执行并提取 token |
| 2 | 渠道事件写入（评论） | POST | /api/channel/events | body：ChannelEvent（tenantId/channelAccountId/eventType/externalEventId/rawPayload） | Result{data:Boolean} | 高频写入场景，幂等去重 |
| 3 | 创建会话 | POST | /api/conversations | body：Conversation（leadId/customerId/channelAccountId） | Result{data:Conversation} | 会话生命周期起点 |
| 4 | 会话分页 | GET | /api/conversations | keyword/status/page/size | Result{data:PageResult} | 列表高频查询 |
| 5 | 发送消息 | POST | /api/conversations/{id}/messages | body：Message（senderType/content） | Result{data:Message} | 消息高频写入 |
| 6 | 消息记录 | GET | /api/conversations/{id}/messages | page/size（时间正序） | Result{data:PageResult} | 历史消息漫游 |
| 7 | AI 回复 | POST | /api/conversations/{id}/ai-reply | body：客户消息字符串 | Result{data:AiReply} | AI 调用+降级 |
| 8 | 线索分页 | GET | /api/leads | keyword/status/intent/page/size | Result{data:PageResult} | 列表高频查询 |
| 9 | 客户分页 | GET | /api/customers | keyword/industry/region/stage/page/size | Result{data:PageResult} | 列表高频查询 |
| 10 | 用户分页 | GET | /api/users | keyword/status/page/size | Result{data:PageResult} | 管理端查询 |
| 11 | 活码扫码 | GET | /api/channel/qr-codes/{id}/scan | id/scene（302 重定向） | 302 → 目标 URL | 非 Result 结构，压测断言按状态码 302 |
| 12 | 看板总览 | GET | /api/dashboard/overview | tenantId/from/to | Result{data:DashboardOverview} | 聚合统计 |
| 13 | 看板趋势 | GET | /api/dashboard/trend | tenantId/from/to | Result{data:[DashboardTrend]} | 聚合统计 |

## 二、JMeter 导入与断言建议

1. **导入**：通过 Knife4j /doc.html →「离线文档 → 导出 OpenAPI JSON」或直接访问 `/v3/api-docs` 下载 `openapi.json`，在 JMeter 中导入生成 HTTP 请求（4.2 已导出标准文件，可直接使用）。
2. **统一返回结构**（压测断言模板）：
   ```json
   {"code":200,"message":"操作成功","data":...}
   ```
   断言：`JSONPath $.code == 200`。
3. **鉴权处理**：线程组内先执行「登录」取样器，用 JSONPath `$.data.token` 提取并拼接 `Bearer ${token}` 写入 HTTP Header Manager，全局生效。
4. **参数化**：`page/size` 用 CSV 或函数变量（`${__Random(1,10)}`）；`conversationId/leadId` 从创建接口响应提取，或压测前用数据初始化脚本造数。
5. **性能基线参考**（MVP 目标）：登录 ≥ 200 TPS、事件写入 ≥ 500 TPS、消息写入 ≥ 300 TPS、列表查询 P95 < 200ms；超出需扩容/走 metric_daily 预聚合。

## 三、数据准备

- 压测前通过 init.sql 初始化租户/用户/渠道账号数据。
- 大并发下注意：Redis 限流不可用时降级放行（不影响压测）；RabbitMQ 不可用时消息仅降级日志，发送接口返回不受影响。
