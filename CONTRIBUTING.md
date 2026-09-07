# 贡献指南（Contributing Guide）

欢迎为 **AiCRM** 做出贡献！无论是报告 Bug、提出功能建议、完善文档还是提交代码，我们都非常感谢。

开始之前，请先阅读：

- [README](./README.md) —— 项目定位与快速开始
- [开发规范](./docs/development-standards.md) —— Git 分支 / 提交 / 接口 / 代码规范
- [接口注解开发规范](./docs/api-annotation-guide.md) —— 接口文档注解约定
- [接口文档迭代维护规范](./docs/api-doc-maintenance-guide.md) —— 文档变更流程与评审检查项
- [行为准则](./CODE_OF_CONDUCT.md) —— 社区交流准则

---

## 目录

- [如何报告 Bug](#如何报告-bug)
- [如何提交功能建议](#如何提交功能建议)
- [本地开发环境](#本地开发环境)
- [开发与提交流程](#开发与提交流程)
- [Pull Request 规范](#pull-request-规范)
- [代码与文档要求](#代码与文档要求)
- [审核与合入标准](#审核与合入标准)

## 如何报告 Bug

1. **先搜索**：在 Issue 列表中检索是否已有相同或相似问题，避免重复提交；
2. **使用模板**：创建 Issue 时选择 Bug 模板，并按模板填写；
3. **必备信息**：
   - 环境：操作系统、JDK / Maven / Python 版本、Spring Boot 版本
   - 复现步骤：尽可能精简到最小可复现路径
   - 期望行为与实际行为（附截图 / 日志片段更佳）
   - 相关配置（注意脱敏，勿粘贴密钥）

> ⚠️ 安全类漏洞**请勿**通过公开 Issue 报告，参见 [SECURITY.md](./SECURITY.md)。

## 如何提交功能建议

- 创建 Issue 并选择 Feature 模板；
- 说明**使用场景**（解决什么问题）、**期望能力**、**可选的实现思路**；
- 若涉及接口变更，请同时说明对现有调用方的影响。

## 本地开发环境

| 依赖 | 版本 | 必须 | 说明 |
| --- | --- | --- | --- |
| JDK | 17+ | 是 | 后端运行环境 |
| Maven | 3.8+ | 是 | 构建工具 |
| PostgreSQL | 16 | 是 | 业务主库 |
| Redis | 6+ | 是 | 缓存 / 限流（本地 6379） |
| RabbitMQ | 3.x | 否 | 未启动时自动降级 |
| Python | 3.11+ | 否 | 仅开发 / 调试 AI 服务时需要 |
| uv | 最新 | 否 | Python 依赖管理（AI 服务） |

## 开发与提交流程

```text
main ──── 发布分支（打 tag，如 v0.1.0）
  └── develop ──── 集成分支（开发基线）
        └── feature/模块-简述 ──── 新功能开发
```

1. 从 `develop` 拉取特性分支：`git checkout -b feature/lead-pool develop`
2. 开发并本地验证（见下方「代码与文档要求」）
3. 提交代码，遵循 **Conventional Commits**：

   ```text
   <type>(<scope>): <subject>
   feat(lead): 新增统一线索池分页接口
   fix(channel): 修复事件幂等去重偶发失效
   ```

   - type：`feat` / `fix` / `refactor` / `docs` / `test` / `chore` / `perf`
   - subject 使用中文、动词开头、不超过 50 字
   - 一次提交只做一件事；禁止提交 `.env`、密钥与本地构建产物
4. 推送到远端并创建 Pull Request（目标分支 `develop`）

## Pull Request 规范

PR 描述请包含：

- **变更内容**：做了什么、为什么做
- **验证结果**：构建命令输出、接口调用示例（curl / 截图）
- **关联 Issue**：如 `Closes #123`
- **破坏性变更说明**：是否影响现有接口 / 数据结构，若影响请同步更新文档

> 接口 / 文档变更请同步在 PR 中附上《[接口文档迭代维护规范](./docs/api-doc-maintenance-guide.md)》要求的检查项核对结果。

## 代码与文档要求

### Java 后端（java/）

- 模块边界：`aicrm-web → aicrm-sales/aicrm-platform → aicrm-shared-kernel`，基础设施由 `aicrm-admin-boot` 装配
- 领域模块内部采用 `api/application/domain/infrastructure`；禁止 Controller 访问 JDBC，禁止跨领域引用实体或 Repository
- 统一返回体：`ApiResponse<T>{code,message,data,traceId}`，分页使用共享内核 `PageResult<T>`
- 接口必须补充 OpenAPI 注解：`@Tag` / `@Operation` / `@Parameter` / `@Schema` / `@ApiResponse`，规则见 [接口注解开发规范](./docs/api-annotation-guide.md)
- 权限从平台表解析，所有租户 SQL 必须显式带 `tenant_id`；关键操作在同一事务写审计、历史和 Outbox
- 数据库只能通过 Flyway 变更，不得新增旧表、MyBatis Mapper 或兼容 Controller
- 提交前自测：`mvn -f java/pom.xml clean verify` 必须通过

### 文档

- 接口变更后，按 [接口文档迭代维护规范](./docs/api-doc-maintenance-guide.md) 重新导出 `docs/openapi.json` 与离线文档
- 用户可见的新能力，同步补充 README 与文档索引

## 审核与合入标准

- ✅ 构建与阶段门通过：`mvn -f java/pom.xml clean verify`
- ✅ 至少 1 位维护者 Review 通过
- ✅ 接口文档与代码同步更新
- ✅ 无未决的破坏性变更（或已提供迁移说明）
- ✅ 未包含密钥 / 敏感信息

感谢你的贡献！🚀
