# 本地接口文档配置说明（开箱即用）

> 目标：本地 `dev` 环境默认开启接口文档，Knife4j 增强 UI 与原生 Swagger UI **双入口**可用，无需任何额外参数。

## 1. 访问入口（应用启动后直接打开）

| 入口 | 地址 | 特点 |
| --- | --- | --- |
| Knife4j 增强 UI | http://localhost:8080/doc.html | 9 个业务分组导航、全局参数（X-Tenant-Id / Bearer）、字段级 Mock、在线调试 |
| 原生 Swagger UI | http://localhost:8080/swagger-ui.html | 标准 Swagger UI，分组下拉 + Try it out |
| 原始 OpenAPI JSON | http://localhost:8080/v3/api-docs | 全量；按分组：`/v3/api-docs/{分组名}`（如 `全部接口`） |

## 2. 配置位置

### 2.1 开关（`application.yml`）

```yaml
springdoc:
  api-docs:
    enabled: ${AICRM_DOC_ENABLED:true}   # 默认开启，生产由 application-prod.yml 强制 false
    path: /v3/api-docs
  swagger-ui:
    enabled: ${AICRM_DOC_ENABLED:true}
    path: /swagger-ui.html
```

- `AICRM_DOC_ENABLED=false` 可一键全关（`/doc.html`、`/swagger-ui.html`、`/v3/api-docs` 全部 404）；
- 生产环境：`application-prod.yml` 已固定关闭（含 `knife4j.enable: false`），详见 [production-config-check.md](production-config-check.md)。

### 2.2 原生 Swagger UI 调试增强（`application.yml` springdoc.swagger-ui）

```yaml
springdoc:
  swagger-ui:
    display-request-duration: true   # 接口列表显示请求耗时
    persist-authorization: true      # 刷新页面保留已填写的 Authorization
    try-it-out-enabled: true         # 默认展开 Try it out
    filter: true                     # 顶部接口搜索过滤
    tags-sorter: alpha               # 分组按字母排序
    operations-sorter: alpha         # 接口按字母排序
```

### 2.3 文档分组（`OpenApiConfig.java`，编程式）

分组通过 `GroupedOpenApi` Bean 定义（9 组：全部接口 / 系统管理 / 客户管理 / 会话管理 / 企微侧边栏 / 渠道管理 / 知识中台 / 数据统计），SpringDoc 会自动将其同步到两个 UI 的分组下拉/标签页，**无需在 yml 重复配置 `springdoc.group-configs`**（重复配置同一分组名会冲突）。

文件：`java/aicrm-api/src/main/java/com/aicrm/module/openapi/config/OpenApiConfig.java`

包含：全局 `X-Tenant-Id` 请求头注入、JWT Bearer 安全方案、接口基本信息（标题/描述/版本/联系方式）。

### 2.4 Knife4j 字段级 Mock（`application.yml` knife4j.setting）

```yaml
knife4j:
  enable: true
  setting:
    mock: true        # 字段级随机示例数据（前端可基于 Mock 渲染页面）
    language: zh_cn
```

配合 `@MockResponse` 注解（`com.aicrm.module.openapi.annotation.MockResponse`）为未完成接口提供完整示例返回。

## 3. 快速验证

```bash
# 启动应用后逐项验证（预期全部 200）
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/doc.html
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/swagger-ui.html
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/v3/api-docs
```

## 4. 常见问题

| 问题 | 原因与处理 |
| --- | --- |
| 打开 `/doc.html` 白屏/404 | 检查是否设置了 `AICRM_DOC_ENABLED=false`；确认启动 profile 为 dev/test |
| `/doc.html` 正常但 `/swagger-ui.html` 404 | 检查 `springdoc.swagger-ui.enabled` 是否被覆盖 |
| 页面报错 `NoSuchMethodError: getGroupConfigs()` | Knife4j 与 springdoc 版本不兼容；本项目固定 springdoc 2.3.0 + Knife4j 4.5.0（见根 `pom.xml`），勿单独升级 springdoc |
| 文档中文乱码 | 仅影响本地导出 openapi.json 的场景，见 [api-doc-maintenance-guide.md](api-doc-maintenance-guide.md) 第 3 节（使用 curl -o 导出） |
