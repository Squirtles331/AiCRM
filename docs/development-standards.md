# AiCRM 开发规范

> 版本：v1.0（M1 基线）
> 适用范围：java/（Spring Boot 后端）与 python/（FastAPI AI 服务）团队开发

## 1. Git 分支规范

### 1.1 分支模型（Git Flow 简化版）

| 分支 | 命名 | 用途 |
| --- | --- | --- |
| `main` | 固定 | 发布分支，只接受 `release/*` / `hotfix/*` 合入，打 tag（v1.0.0） |
| `develop` | 固定 | 集成分支，`feature/*` 开发完成后合入 |
| `feature/*` | `feature/模块-简述`，如 `feature/lead-pool` | 新功能开发，从 `develop` 拉出 |
| `hotfix/*` | `hotfix/问题简述` | 线上紧急修复，从 `main` 拉出，修复后合回 `main` 与 `develop` |

### 1.2 提交规范（Conventional Commits）

```
<type>(<scope>): <subject>

feat(lead): 新增统一线索池分页接口
fix(channel): 修复事件幂等去重偶发失效
refactor(auth): 抽取 JWT 解析公共逻辑
docs: 补充接口命名规范
```

- type：`feat` / `fix` / `refactor` / `docs` / `test` / `chore` / `perf`
- subject 使用中文，动词开头，不超过 50 字
- 一次提交只做一件事；禁止提交 `.env`、密钥、本地构建产物

### 1.3 合入流程

- 禁止直接推送 `main` / `develop`
- feature 分支提 PR，至少 1 人 Review，CI 编译通过后合入

## 2. 接口命名规范（RESTful）

- 基础路径：`/api/{业务域}`，业务域小写复数，如 `/api/leads`、`/api/conversations`
- 动词用 HTTP 方法表达：

| 方法 | 语义 | 示例 |
| --- | --- | --- |
| GET | 查询 | `GET /api/leads`（列表）、`GET /api/leads/{id}`（详情） |
| POST | 创建 / 动作 | `POST /api/leads`、`POST /api/conversations/{id}/transfer` |
| PUT | 全量更新 / 发布等动作 | `PUT /api/documents/{id}/publish` |
| DELETE | 删除（逻辑删除） | `DELETE /api/leads/{id}` |

- 动作型接口用 `POST /资源/{id}/动作`，如 `transfer`、`publish`、`login`
- 分页：`?page=1&size=20`（page 从 1 起），返回 `PageResult<T>`
- 时间参数：`yyyy-MM-dd HH:mm:ss`，用 `@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")`
- 响应统一包一层 `Result<T>`；分页返回 `PageResult<T>`；异常由全局处理器兜底
- 鉴权：除登录接口外均需 `Authorization: Bearer {token}`；角色权限用 `@RequirePermission` 标注

## 3. 代码格式规范（Java）

- 缩进 4 空格，禁止 Tab；`{` 同行；`else` 换行（本项目以 IDEA 默认 + EditorConfig 为准）
- 命名：
  - 类/接口：大驼峰（`LeadService`）
  - 方法/字段/变量：小驼峰（`pageUsers`）
  - 常量：全大写下划线（`MqConstants.QUEUE_CHANNEL_EVENT`）
  - 枚举：大驼峰，成员大写下划线
- 分层归属（多模块工程）：

| 模块 | 职责 | 禁止事项 |
| --- | --- | --- |
| `aicrm-common` | 通用工具/返回结果/上下文/类型处理器 | 不引业务 Mapper |
| `aicrm-dao` | 实体 + Mapper（BaseMapper，CRUD 免写） | 不含业务逻辑 |
| `aicrm-service` | 业务逻辑 + AI 客户端 + MQ 消费 | 不出现 Controller |
| `aicrm-api` | Controller + OpenAPI 注解 | 不含 SQL/业务计算 |
| `aicrm-admin-boot` | 启动类 + 框架配置 + 配置文件 | 不写业务 |

- 依赖方向：`api → service → dao → common`，禁止反向依赖与环依赖
- 注释：类级 Javadoc 说明职责；关键算法/边界逻辑加行注释；不写废话注释
- 校验：入参用 JSR-380（`@NotBlank` / `@NotNull` / `@Valid`）在 Controller/DTO 层校验

## 4. 日志规范

- 统一使用 SLF4J + Lombok `@Slf4j`，禁止 `System.out`
- 使用占位符，禁止字符串拼接：`log.info("userId={}, tenantId={}", id, tid)`
- 级别：
  - `debug`：详细流程（消息收发、AI 调用参数）
  - `info`：关键业务事件（登录、转人工、发布）
  - `warn`：可恢复异常（AI 服务降级、频控命中）
  - `error`：业务/系统异常（带异常栈）
- 敏感信息脱敏后输出：手机号/身份证/邮箱用 `DesensitizeUtil`，密码/密钥永不打印
- 日志必须含定位上下文：`tenantId` / 业务主键，便于按租户检索

## 5. 数据库设计规范（PostgreSQL）

- 命名：表/字段 `snake_case`；表名复数名词（`leads`、`channel_events`）；字段名不缩写（`created_at` 而非 `crt_time`）
- 所有业务表强制字段：

```sql
id          BIGSERIAL PRIMARY KEY,        -- 或雪花 ID BIGINT
tenant_id   BIGINT NOT NULL,              -- 多租户隔离（拦截器自动注入）
created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
deleted     SMALLINT NOT NULL DEFAULT 0   -- 逻辑删除 0/1
```

- `tenant_id` 必带并建索引：`CREATE INDEX idx_{table}_tenant ON {table}(tenant_id)`
- 高频查询字段建索引；组合索引遵循最左前缀；禁止全表扫描的 `LIKE '%xx%'`
- 枚举类字段用 `VARCHAR` 存语义码（`quote`/`sample`）或 `SMALLINT` + COMMENT，不存魔法数
- 金额用 `NUMERIC(18,2)`；时间统一 `TIMESTAMPTZ`
- 动态结构（标签、扩展属性）用 `JSONB`，配合 MyBatis-Plus `JsonbTypeHandler`；向量字段 `vector(n)` 配合 `VectorTypeHandler`
- 唯一约束用于幂等（如 `tenant_id+channel_account_id+external_event_id`）
- DDL 变更：统一维护在 `aicrm-admin-boot/src/main/resources/db/init.sql`（增量脚本放 `db/migration/`），不允许手工改库后不回填脚本
