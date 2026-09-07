# 第一里程碑开发前阶段门

版本：`M1-DB-1.0`。评审状态分为 `READY`（已有可执行证据）、`PENDING-RUN`（门禁已实现但当前环境未执行）和 `BLOCKED`。只有全部为 `READY` 且产品、架构、开发、测试共同签字后，才允许开始/继续第一里程碑功能编码。

| 验收项 | 证据 | 当前状态 |
|---|---|---|
| 平台域、销售域表名和字段冻结 | 数据字典 + V1-V7 | READY |
| 类型、默认值、可空性、索引、唯一规则冻结 | 数据字典 + Flyway | READY |
| 公私海不变量、租户引用、终态保护 | V7 CHECK/复合 FK/触发器 | READY |
| 空库 Flyway 初始化 | `DatabaseStageGateTest.emptyDatabaseMigratesThroughFrozenVersion` | READY |
| 旧租户、用户、组织顺序升级 | `legacyTenantAndUsersUpgradeInOrder` | READY |
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

最近一次完整验收：2026-09-07，Java 全模块 `verify` 通过。Testcontainers 启动 PostgreSQL 16 容器，空库 V1-V7 初始化、V1 基线后的旧租户/用户升级、私海公海约束、租户隔离、并发认领、乐观锁、幂等、线索转换、客户合并、离职交接、审计、Outbox/Inbox、静态契约和 ArchUnit 全部通过。

## 评审签字

| 角色 | 姓名 | 结论 | 日期 |
|---|---|---|---|
| 产品负责人 | 待填写 | 待评审 | - |
| 架构负责人 | 待填写 | 待评审 | - |
| 开发负责人 | 待填写 | 待评审 | - |
| 测试负责人 | 待填写 | 待评审 | - |
