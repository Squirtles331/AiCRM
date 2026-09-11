# 第一里程碑开发前阶段门

版本：`M1-DB-1.3`。评审状态分为 `READY`（已有可执行证据）、`PENDING-RUN`（门禁已实现但当前环境未执行）和 `BLOCKED`。数据库设计阶段门已经通过，允许继续第一里程碑功能编码；产品、架构、开发、测试签字仍是里程碑发布门禁。

| 验收项 | 证据 | 当前状态 |
|---|---|---|
| 平台域、销售域、连接器、获客渠道、会话、销售资料、销售话术和竞争信息表名及字段冻结 | 数据字典 + V1-V26 | READY |
| 类型、默认值、可空性、索引、唯一规则冻结 | 数据字典 + Flyway | READY |
| 公私海不变量、租户引用、终态保护 | V7 CHECK/复合 FK/触发器 | READY |
| 空库 Flyway 初始化 | `DatabaseStageGateTest.emptyDatabaseMigratesThroughFrozenVersion` | READY |
| 旧表退出且不导入数据 | `predecessorTablesAreRemovedWithoutImportingTheirData` | READY |
| 线索、客户、公海测试数据 | `frozenModelSupportsMilestoneTransactionsAndRejectsInvalidOnes` | READY |
| 租户隔离与四级数据范围 | 同上 `verifyTenantIsolationAndDataScope` | READY |
| 并发认领、版本冲突、幂等重复 | 同上三个验证方法 | READY |
| 线索转换、客户合并、离职交接 | 同上三个事务验证方法 | READY |
| 审计、归属历史完整且不可变 | V7 延迟约束 + 同上不可变测试 | READY |
| Outbox 完整、可重试、消费者去重 | V6 + Outbox/Inbox 测试 | READY |
| 文档和字段一致 | 本目录交叉评审 + `SchemaContractStaticTest` | READY |
| ArchUnit 依赖规则 | `ModuleDependencyTest`、`ModuleBoundaryTest` + CI | READY |

## 执行方式

本地需运行 Docker，然后执行 `mvn -f java/pom.xml verify`。CI 使用 `.github/workflows/database-stage-gate.yml` 在 PostgreSQL 16 Testcontainers 上执行同一命令。Windows Docker Desktop 如遇 Testcontainers 命名管道兼容问题，应启用 WSL 集成并在 Linux 环境运行，或使用仅绑定回环地址的受控 Docker API 端点；不得关闭数据库测试或人工改库后标记通过。

最近一次完整自动化验收：2026-09-11，`mvn -f java/pom.xml verify "-Dspring-boot.repackage.skip=true"` 通过，42 个测试通过、1 个 RabbitMQ 条件测试按本地开关跳过，覆盖至 V26。Testcontainers 验证空库迁移、旧表退出、JWT 权限与租户隔离、销售话术和竞争信息幂等创建及生命周期、销售和交易生命周期、工作台与目标计分、连接器、获客渠道、销售会话、销售资料、审计、Outbox、OpenAPI、静态契约及 ArchUnit；服务级规则测试覆盖销售线索、客户、商机的状态、权限、乐观锁、交接与自动回收。Windows 开发服务占用旧 `aicrm.jar` 时，需停止服务后再执行不带跳过参数的重打包。

## 第一里程碑发布门进度

| 收尾项 | 当前状态 | 证据或后续动作 |
|---|---|---|
| 公海规则与定时自动回收 | READY | V8 + `recyclesExpiredPrivateResourcesUsingVersionedPoolRules` |
| 批量离职交接 | READY | `/api/v1/handovers/batch` 应用级集成测试 |
| 单轨接口与旧入口下线 | READY | 聚合工程已删除旧模块；测试断言旧线索、客户、登录路径均返回 404 |
| `/api/v1` OpenAPI 与接口契约 | READY | `/v3/api-docs/crm-v1` 契约测试 |
| PostgreSQL 业务集成测试 | READY | 并发认领、幂等、转换、合并、交接及自动回收均通过 |
| 租户、组织、字段权限和越权 | READY | JWT 服务端权限加载、租户头一致性、SELF 范围、字段明文授权与列表脱敏测试 |
| Outbox、重试和 Inbox | READY | 数据库租约领取、发布成功/失败退避、Inbox 完成去重与失败重试测试；传输端使用 RabbitMQ 发布确认 |
| 独立端口真实 HTTP 服务 | READY | `servesTheV1ApiOnARealHttpPort` 在随机真实端口完成创建线索请求 |
| 真实 RabbitMQ 验收 | PENDING-RUN | 已新增 `RabbitOutboxEventPublisherIntegrationTest`，检查持久化消息信封、Broker Confirm 与拒绝后死信投递；GitHub Actions 强制以 `-Daicrm.rabbitmq.integration.enabled=true` 运行。当前本机 Docker Hub 无法拉取 `rabbitmq:3.13-management-alpine`，待网络恢复后执行同一命令。 |
| 覆盖率硬门禁 | READY | 根工程 `verify` 在 `aicrm-coverage/target/site/jacoco-aggregate/` 生成全工程 JaCoCo 报告，并由 `CoverageGate` 强制检查全工程至少 70%、销售域至少 80%。本次指令覆盖率为 84.93%，销售域为 80.69%。 |
| 百万级性能验收 | PENDING-RUN | 4C8G、10 租户、百万级销售数据执行 P95 基准 |

第一里程碑的 RabbitMQ 实机和性能发布门仍待关闭。项目已按产品负责人指令进入阶段 2 的增量开发；这些待办项继续作为发布门，不得被标记为完成或绕过。

## 评审签字

| 角色 | 姓名 | 结论 | 日期 |
|---|---|---|---|
| 产品负责人 | 待填写 | 待评审 | - |
| 架构负责人 | 待填写 | 待评审 | - |
| 开发负责人 | 待填写 | 待评审 | - |
| 测试负责人 | 待填写 | 待评审 | - |
