# 阶段 3：合同与订单设计

状态：设计基线；物理基线：Flyway `V14__contract_and_order_foundation.sql`。本阶段由交易域主责销售合同、合同变更、销售订单和取消申请；交付、库存、实际发货、到账核销和正式发票仍由后续上下文或 ERP/财务系统主责。

## 1. 范围与主责

| 对象 | CRM 主责 | ERP/财务主责 | 本阶段边界 |
|---|---|---|---|
| 销售合同 | 草稿、提交签署、签署结果、变更和作废 | 归档副本可同步，不反向覆盖 CRM 合同事实 | 仅已批准报价可创建合同。 |
| 销售订单 | 从已签合同生成、确认、取消申请与取消结果 | 库存预占、实际发货和 WMS 状态 | CRM 不扣减库存、不记录实际发货。 |
| 合同/订单明细 | 固化报价或合同中的商业快照 | 物料、库存和履约事实 | 不随产品目录变化回写。 |
| ERP 回传 | 记录外部订单号、回传状态和错误 | 外部事实源 | 使用外部系统、业务类型、外部单号组成幂等业务键。 |

## 2. 聚合、状态与逆向流程

```mermaid
stateDiagram-v2
  [*] --> DRAFT: 已批准报价创建合同
  DRAFT --> PENDING_SIGNATURE: 提交签署
  PENDING_SIGNATURE --> SIGNED: 签署完成
  PENDING_SIGNATURE --> DRAFT: 撤回签署
  DRAFT --> VOIDED: 作废
  PENDING_SIGNATURE --> VOIDED: 作废
  SIGNED --> VOIDED: 作废
  SIGNED --> [*]
```

```mermaid
stateDiagram-v2
  [*] --> DRAFT: 已签合同生成订单
  DRAFT --> CONFIRMED: 销售确认
  CONFIRMED --> CANCELLING: 提交取消申请
  CANCELLING --> CANCELLED: 取消完成
  CANCELLING --> CONFIRMED: 取消驳回
  CONFIRMED --> CLOSED: ERP 回传全部履约完成
```

- 合同创建仅引用同租户、状态为 `APPROVED` 的报价及其当前批准版本，合同明细复制报价行快照。
- 合同变更不是直接改写已签合同；变更申请保存原始快照、变更快照、原因和审批状态，批准后生成新的合同修订事实。首期不支持覆盖原签署快照。
- 合同作废须记录原因和时间。已经存在有效销售订单的签署合同不得直接作废，必须先完成订单取消或由有审批权限的作废流程处理。
- 订单创建仅引用已签且未作废合同；确认后只允许走取消申请，不允许删除或直接回到草稿。
- ERP 回传必须携带 `Idempotency-Key` 和外部单号。重复回传返回首次结果；业务写入、审计和 Outbox 处于同一事务。

## 3. 数据模型

| 表 | 用途 | 关键约束 |
|---|---|---|
| `crm_contract` | 合同根与当前商业事实 | 活跃合同号租户内唯一；状态为 `DRAFT/PENDING_SIGNATURE/SIGNED/VOIDED`；金额非负、币种三位大写。 |
| `crm_contract_line` | 合同不可变商业快照行 | 每合同号行号唯一；数量正数；金额、折扣、税率受范围检查。 |
| `crm_contract_change` | 合同变更申请与快照 | 每合同变更号唯一；状态为 `DRAFT/SUBMITTED/APPROVED/REJECTED/CANCELLED`；原始与变更快照为 JSONB。 |
| `crm_sales_order` | CRM 销售订单协调事实 | 活跃订单号租户内唯一；状态为 `DRAFT/CONFIRMED/CANCELLING/CANCELLED/CLOSED`；外部订单号可回传。 |
| `crm_sales_order_line` | 订单商业快照行 | 每订单行号唯一；引用仅保存 ID 和快照，不建立跨上下文实体外键。 |
| `crm_order_cancel` | 取消申请与处理结果 | 每订单取消编号唯一；状态为 `PENDING/APPROVED/REJECTED/COMPLETED`；原订单保持不可变。 |

所有表使用应用生成的 `BIGINT` 主键、`tenant_id` 共享表隔离、UTC `TIMESTAMPTZ`、`NUMERIC(18,2)` 金额、乐观锁 `version` 和软删除审计字段。合同到报价、订单到合同以及合同/订单到客户均只保存 ID，由应用服务做租户和业务状态校验；不建立跨聚合实体外键。

## 4. API 契约与权限

| API | 权限 | 幂等与版本规则 |
|---|---|---|
| `POST /api/v1/contracts` | `contract:create` | `Idempotency-Key`；报价必须已批准。 |
| `GET /api/v1/contracts/{id}` | `contract:read:own/any` | 使用来源商机的数据范围。 |
| `POST /api/v1/contracts/{id}/actions/submit-signature|withdraw-signature|sign|void` | 对应 `contract:*` | 请求含合同 `version`；作废额外含原因。 |
| `POST /api/v1/contracts/{id}/changes` | `contract:change` | `Idempotency-Key`；创建变更快照。 |
| `POST /api/v1/orders` | `order:create` | `Idempotency-Key`；合同必须已签。 |
| `POST /api/v1/orders/{id}/actions/confirm|request-cancel` | 对应 `order:*` | 请求含订单 `version`；取消须说明原因。 |
| `POST /api/v1/integrations/erp/orders/{externalOrderNo}/callbacks` | `integration:erp:callback` | `Idempotency-Key`；按外部单号幂等处理回传。 |

第一实现切片严格限定为“批准报价 -> 合同草稿 -> 签署 -> 订单草稿”。合同变更、作废拦截、订单确认/取消和 ERP 回传在该切片完成验收后逐项实现；交付协调不在本阶段提前建表。

## 5. 事件与验收

事件：`ContractCreated`、`ContractSignatureSubmitted`、`ContractSigned`、`ContractVoided`、`ContractChangeSubmitted`、`OrderCreated`、`OrderConfirmed`、`OrderCancellationRequested`、`OrderCancelled`、`ErpOrderSynced`。

阶段门必须验证：跨租户引用拒绝；未批准报价不可创建合同；未签合同不可创建订单；签署、作废、订单确认和取消均有乐观锁冲突测试；重复创建与 ERP 回传不产生重复聚合、审计或 Outbox；产品、价格和报价变更不会回写合同/订单快照。
