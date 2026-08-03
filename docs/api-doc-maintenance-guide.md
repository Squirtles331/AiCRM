# 接口文档迭代维护规范

> 任务编号：5.3 ｜ 责任人：技术负责人 ｜ 交付物：维护规范文档
> 验收标准：后续版本迭代时，文档与代码同步更新，不会出现文档滞后问题

## 1. 规范目的

保证 AiCRM 接口文档与代码**始终同步**，避免"代码已改、文档滞后"导致前端/测试/客户对接出现歧义。适用于所有需求变更、接口迭代、缺陷修复。

## 2. 文档体系与唯一事实来源

| 文档 | 位置 | 生成方式 | 更新时机 |
| --- | --- | --- | --- |
| OpenAPI 在线文档 | `/doc.html`（dev/test 环境） | Knife4j + SpringDoc 实时生成 | 代码注解变更后自动生效，无需手动维护 |
| OpenAPI JSON（标准文件） | `docs/openapi.json` | 从 `/v3/api-docs` 导出 | 每个迭代版本发布前重新导出 |
| 离线 HTML 版 | `docs/offline-api-doc.html` | `npx @redocly/cli build-docs` | 每个迭代版本发布前重新生成 |
| 离线 Markdown 版 | `docs/offline-api-doc.md` | `npx widdershins` | 每个迭代版本发布前重新生成 |
| 错误码对照表 | `docs/error-code-table.md` | 手动维护（与 `ResultCode.java` 一致） | `ResultCode.java` 变更时同步 |
| Mock 链路说明 | `docs/mock-chain-guide.md` | 手动维护 | Mock 链路变更时同步 |
| 压测接口清单 | `docs/performance-testing-list.md` | 手动维护 | 压测范围变更时同步 |

**核心原则**：接口定义以代码中的 SpringDoc 注解（`@Operation` / `@Parameter` / `@Schema` / `@ApiResponse`）为**唯一事实来源**；`openapi.json` 与离线文档为其导出产物，禁止手工修改导出产物。

## 3. 变更流程要求

### 3.1 需求变更 / 接口迭代必做清单

任何涉及接口的变更（新增、修改、删除接口，改字段，改参数，改错误码），必须同时完成：

1. **修改代码注解**：同步更新 `@Operation(summary/description)`、`@Parameter(required/description)`、`@Schema(description/example)`、`@ApiResponse`；
2. **登记错误码**：若新增异常分支，先在 `ResultCode.java` 增加业务码，再同步 `docs/error-code-table.md`；
3. **刷新导出产物**（发布前执行）：
   ```bash
   # 启动 dev 环境后
   curl -s http://localhost:8080/v3/api-docs -o docs/openapi.json
   npx @redocly/cli@latest build-docs docs/openapi.json -o docs/offline-api-doc.html --title "AiCRM 接口文档（离线版）"
   npx --yes widdershins@4.0.1 --language_tabs "shell:Shell" "javascript:JavaScript" --summary docs/openapi.json -o docs/offline-api-doc.md
   ```
4. **通知受影响方**：在联调台账 `docs/integration-issues.md` 或项目群同步变更点（新增/废弃接口、字段类型变更、错误码变化），标注"破坏性变更"或"兼容变更"。

### 3.2 破坏性变更（Breaking Change）特别要求

以下变更视为破坏性，需提前一个版本在文档中标注废弃（Deprecated）并在变更说明中列出：

- 删除或重命名接口路径；
- 修改请求/响应字段名称、类型、必填性；
- 修改请求方式（GET→POST 等）；
- 业务错误码语义变更。

## 4. 代码评审检查项（评审清单）

在 Merge Request 评审中，接口相关代码必须逐项检查：

| # | 检查项 | 通过标准 |
| --- | --- | --- |
| 1 | Controller 有 `@Tag` | 分组归属清晰 |
| 2 | 每个接口方法有 `@Operation(summary)` | summary 非空、无占位符文案 |
| 3 | 入参字段有 `@Schema(description)`，必填项标注 | 前端/测试可明确识别必填 |
| 4 | 返回 DTO 字段有 `@Schema(description)` | 字段含义无歧义 |
| 5 | `@ApiResponse` 状态码合法 | **必须为 HTTP 状态码（100-599）**，业务码放 description；禁止使用业务码（1001 等）作响应码 |
| 6 | 业务异常使用 `BusinessException(ResultCode.XXX)` | 错误码登记在对照表 |
| 7 | 新增接口有 Mock 返回（`@MockResponse` 或可运行实现） | 前端可独立联调 |
| 8 | 幂等/鉴权标注 | Webhook、登录、多租户接口有明确说明 |
| 9 | 导出产物与代码一致 | 发布前重新导出 openapi.json 并 diff 无意外差异 |

## 5. 版本与发布节奏

- **每个迭代版本发布前**：执行第 3.1 节第 3 步刷新导出产物，随版本归档；
- **openapi.json 作为交付物**：随版本号归档，命名 `openapi-<版本号>.json`（同步给测试/客户侧）；
- **离线文档包**：版本发布时打包 `offline-api-doc.html` + `offline-api-doc.md` + `openapi.json` 为文档交付包。

## 6. 责任分工

| 角色 | 职责 |
| --- | --- |
| 开发 A/B | 变更接口时同步注解与错误码；通过评审检查项 |
| 技术负责人 | 发布前把关导出产物刷新；文档体系完整性 |
| 测试 | 以 openapi.json 导入 Apifox/JMeter 作为接口基线，发现文档不一致立即反馈 |
