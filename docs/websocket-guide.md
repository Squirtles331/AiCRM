# WebSocket 消息推送接入说明

> 版本：v1.0　适用范围：前端（客服工作台 / 侧边栏）消息实时接收
> 说明：Knife4j / OpenAPI 对 WebSocket 协议支持有限（仅能描述 HTTP 接口），本文件为 WebSocket 通道的独立接入文档。

## 一、现状与选型

- **当前状态**：本项目尚未实现 WebSocket 服务端端点。消息获取统一走 HTTP 轮询：
  `GET /api/conversations/{id}/messages`（按会话分页、时间正序漫游）。
- **演进建议**：消息实时性要求出现后，建议按本文档约定新增 WebSocket 推送通道（后端 Spring 内置 `spring-websocket` 即可，无需引入额外中间件）。
- **降级策略**：前端以"HTTP 轮询兜底 + WebSocket 增强"双通道实现；WS 断开时自动降级轮询，保证消息不丢。

## 二、规划端点（待后端开发）

| 项 | 约定 |
| --- | --- |
| 连接地址 | `ws://{host}/ws/messages?token={JWT}` |
| 协议 | STOMP over WebSocket（或原生 WebSocket，二选一，建议 STOMP） |
| 鉴权 | 连接参数携带 JWT（`Authorization` 头在浏览器 WS 中受限，故放 query 参数）；服务端校验失败返回 401 后关闭连接 |
| 心跳 | 客户端每 30s 发送 ping，服务端 60s 未收到 pong 判定掉线 |

## 三、订阅主题与消息格式

订阅（STOMP）：

| 场景 | 主题 | 说明 |
| --- | --- | --- |
| 当前用户负责的会话新消息 | `/user/queue/messages` | 服务端按登录用户定向推送，仅该会话负责人可收 |
| 会话状态流转 | `/topic/conversation/{conversationId}` | 关闭/归档/转人工等状态变更 |

推送消息 JSON（统一结构，与 REST 的 Message 实体字段对齐）：

```json
{
  "conversationId": 100,
  "messageId": 5201,
  "senderType": "customer",
  "senderName": "张三",
  "content": "你们产品多少钱？",
  "msgType": "text",
  "createdAt": "2026-08-03 14:30:00",
  "channelCode": "wecom"
}
```

字段说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| conversationId | number | 会话 ID |
| messageId | number | 消息 ID（用于去重/回执） |
| senderType | string | customer客户 / ai机器人 / human人工 / system系统 |
| senderName | string | 发送者展示名 |
| content | string | 消息内容 |
| msgType | string | text文本 / image图片 / file文件 / card卡片 |
| createdAt | string | 服务端落库时间（yyyy-MM-dd HH:mm:ss） |
| channelCode | string | 来源渠道：douyin/video_channel/tiktok/wecom/whatsapp |

## 四、客户端接入要点

1. **连接**：`new WebSocket("ws://{host}/ws/messages?token=" + token)`；断线自动重连，指数退避（1s→2s→4s→…上限 30s）。
2. **幂等**：以 `messageId` 做本地去重；`createdAt` 用于会话列表增量拉取（`GET /api/conversations/{id}/messages` 传 `createdAt` 游标）。
3. **收发职责**：WS 仅负责"接收"实时推送；发送消息仍走 REST（`POST /api/conversations/{id}/messages`），避免 WS 半开连接导致消息丢失。
4. **多标签页**：同一用户多标签页共用 token 会同时收到推送，前端用 `messageId` 去重即可。

## 五、与 REST 接口的对应关系

| 能力 | WebSocket | REST（文档内） |
| --- | --- | --- |
| 接收实时消息 | `/ws/messages`（规划） | `GET /api/conversations/{id}/messages` |
| 发送消息 | —（不建议走 WS） | `POST /api/conversations/{id}/messages` |
| 会话状态变更 | `/topic/conversation/{id}`（规划） | `POST /api/conversations/{id}/close` 等 |
