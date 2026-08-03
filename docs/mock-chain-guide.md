# Mock 返回链配置指南（核心业务流程）

> 版本：v1.0　用途：前端无需后端真实接口，可独立走完「评论 → 私信 → 加企微 → 会话」全流程调试。
> 机制：`@MockResponse("{\"code\":200,...}")` 标注未完成接口 → 文档 200 响应展示示例数据；`knife4j.setting.mock=true` 已开启字段级 Mock。

## 一、全流程接口链路

```
① 评论产生 → ② 私信/评论回复 → ③ 加企微好友 → ④ 建立会话 → ⑤ 消息往返/AI 接待 → ⑥ 转人工
```

| 环节 | 接口 | 说明 |
| --- | --- | --- |
| ① 评论 | POST /api/channel/events（Webhook） | 渠道事件统一入口，eventType=comment 落库建线索 |
| ② 评论回复/私信 | POST /api/conversations/{id}/messages | 发送消息（渠道回推由消息网关内部处理） |
| ③ 加企微 | POST /api/channel/events（eventType=lead / wecom 客户添加） | 企微客户添加事件 → 身份归一 → 线索 |
| ④ 建立会话 | POST /api/conversations | 创建会话（客户+渠道+负责人） |
| ⑤ AI 接待 | POST /api/conversations/{id}/ai-reply | AI 生成回复并落库（降级话术） |
| ⑥ 转人工 | GET/POST /api/conversations/{id}/transfer* | 转人工评估/执行 |

## 二、Mock 配置方法

**方式 A：接口未开发完成时**，在 Controller 方法上标注示例返回：

```java
@MockResponse(value = """
        {"code":200,"message":"操作成功","data":{"id":1,"name":"示例客户公司"}}
        """, note = "联调临时 Mock，接口完成后移除")
```

**方式 B：接口已开发完成**，前端调试可用两种途径：
1. Knife4j 文档"在线调试"直接请求真实接口；
2. 本地 Mock Server：从文档「响应示例」复制 JSON 到前端 mock 工具（Apifox/Mockoon）按本表配置。

## 三、全流程 Mock 示例数据

### ① 评论事件（Webhook）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```
请求体（事件示例）：`{"tenantId":1,"channelAccountId":2,"eventType":"comment","externalEventId":"20260803120000_001","externalUserId":"douyin_10086","rawPayload":{"content":"这款产品多少钱？","videoId":"v123","commentId":"c456"}}`

### ② 发送私信/评论回复

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 5201,
    "conversationId": 100,
    "senderType": "human",
    "content": "您好，方便留个联系方式吗？",
    "msgType": "text",
    "createdAt": "2026-08-03 14:30:00"
  }
}
```

### ③ 加企微（客户添加事件）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```
事件示例：`{"tenantId":1,"channelAccountId":4,"eventType":"lead","externalEventId":"wecom_20260803_001","externalUserId":"wm_xxxx","rawPayload":{"action":"add_external_contact","corpId":"ww123"}}`

### ④ 创建会话

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 100,
    "tenantId": 1,
    "leadId": 88,
    "customerId": 30,
    "channelAccountId": 4,
    "conversationType": "wecom_chat",
    "status": "active",
    "assignedTo": 5,
    "createdAt": "2026-08-03 14:31:00"
  }
}
```

### ⑤ AI 接待回复

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "messageId": 5202,
    "reply": "我们企业版 CRM 支持按坐席数灵活报价，方便留个联系方式，让销售顾问给您出方案吗？",
    "intent": "quote",
    "confidence": 0.87
  }
}
```

### ⑥ 转人工评估

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "needTransfer": true,
    "reason": "客户意向明确（quote，置信度 0.87），建议转人工",
    "recommendedReply": "您稍等，我为您转接专业顾问。"
  }
}
```

## 四、联调注意

- Mock 数据字段与真实接口完全一致（取自真实 Service/DTO 实现），前端可直接按此渲染。
- 批量/分页接口的 Mock 用 `PageResult` 结构：`{"page":1,"size":20,"total":1,"pages":1,"records":[...]}`。
- 接口完成后务必移除 `@MockResponse`，避免文档误导测试。
