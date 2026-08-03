# 接口注解开发规范（API Annotation Guide）

> 版本：v1.0　适用范围：AiCRM Java 后端（Spring Boot 3 + SpringDoc OpenAPI 2.5 + Knife4j）
> 目的：统一接口文档注解写法，保证所有开发对齐规则，无随意命名、缺失说明的情况。

## 一、文档访问

| 环境 | 地址 | 说明 |
| --- | --- | --- |
| 开发 / 测试 | http://localhost:8080/doc.html | Knife4j 增强 UI，默认开启 |
| 开发 / 测试 | http://localhost:8080/swagger-ui.html | 原生 Swagger UI |
| 生产 | — | 强制关闭（`springdoc.api-docs.enabled=false`） |

开关：环境变量 `AICRM_DOC_ENABLED`（默认 `true`，生产配置覆盖为 `false`）。

## 二、注解使用规则

### 1. 控制器类：`@Tag`

- 每个 `@RestController` 必须标注 `@Tag(name = "模块名")`，name 为中文业务名（如 `产品库`），不允许为空或英文缩写。
- 示例：

```java
@Tag(name = "产品库")
@RestController
@RequestMapping("/api/products")
public class ProductController { ... }
```

### 2. 接口方法：`@Operation`

- 每个对外接口方法必须标注 `@Operation(summary = "一句话说明")`，summary 用动宾结构描述（如 `产品分页查询`、`创建产品`）。
- 复杂说明放入 `description`（可选）。
- 示例：

```java
@Operation(summary = "产品分页查询（keyword/分类/上下架过滤）")
@GetMapping
public Result<PageResult<Product>> page(...) { ... }
```

### 3. 实体字段：`@Schema`

- 所有实体类加 `@Schema(description = "表注释")`；每个字段加 `@Schema(description = "字段说明")`，字段说明与表注释一致。
- 有明确取值范围的字段补充 `example`（如 `code`、`status`）。
- 通用返回结构 `Result` / `PageResult` 已内置 `@Schema`，无需重复添加。
- 示例：

```java
@Schema(description = "产品")
public class Product extends BaseEntity {

    @Schema(description = "产品名称", example = "企业版 CRM")
    private String name;
}
```

### 4. 参数说明：`@Parameter` / `@Schema(required = ...)`

- 查询参数 / 路径参数按需添加 `@Parameter(description = "...", required = true)`；Controller 方法参数上直接标注。
- 请求体 DTO 通过字段 `@Schema` 描述；必填字段在 `@Schema` 中注明 `requiredMode = Schema.RequiredMode.REQUIRED` 并配合 `@NotNull` 校验注解（`spring-boot-starter-validation`）。
- 示例：

```java
@GetMapping("/{id}")
public Result<Product> detail(
        @Parameter(description = "产品 ID", required = true) @PathVariable Long id) { ... }
```

### 5. 响应说明：`@ApiResponse`

- 当接口存在非 200 的业务失败分支（业务错误码 1000+）时，用 `@ApiResponse` 补充说明：

```java
@Operation(summary = "删除产品")
@ApiResponse(responseCode = "1301", description = "资源不存在")
@DeleteMapping("/{id}")
public Result<Void> delete(@PathVariable Long id) { ... }
```

## 三、参数必填项约定

| 类型 | 写法 | 必填说明 |
| --- | --- | --- |
| 路径参数 | `@PathVariable Long id` | 必填，缺失返回 404 |
| 查询参数 | `@RequestParam(required = true/false, defaultValue = "...")` | 分页默认 `page=1, size=20` |
| 请求体 | `@RequestBody` + DTO 字段 `@NotNull` | 校验失败返回 400 |

## 四、返回结构约定

- 所有接口必须返回 `Result<T>`（统一结构 `{ code, message, data }`），禁止裸返回实体或 Map。
- 分页接口 data 为 `PageResult<T>`（`{ page, size, total, pages, records }`）。
- `code` 语义：`200` 成功；`400` 参数错误；`401` 未登录；`403` 无权限；`404` 资源不存在；`500` 系统错误；业务错误码统一 `1000+`（见 `ResultCode` 枚举，不允许自造数字）。
- 失败统一抛 `BusinessException(ResultCode.XXX, "可读提示")`，由全局异常处理器收敛，禁止在 Controller 内 catch 后返回任意结构。

## 五、字典枚举说明要求

- 字段取值是固定枚举（如状态、类型、渠道编码、场景分类）时，必须在 `@Schema` 的 description 中列出全部取值及含义，例如：

```java
@Schema(description = "场景分类：general通用/quote报价/selection选型/objection异议/follow跟进/opening开场")
private String category;
```

- 多个模块复用的枚举统一收敛到 `ResultCode` 同包的枚举类或字段注释中，禁止各写各的。

## 六、代码生成器约定

- 使用 `aicrm-generator`（FastAutoGenerator）生成的代码自带：实体 `@Schema`、Controller `@Tag` / `@Operation` / `@RequirePermission` / `@OperLog`。
- 生成后人工只需补充：参数级 `@Parameter`、特殊业务分支的 `@ApiResponse`、枚举取值说明。
