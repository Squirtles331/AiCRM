---
title: AiCRM 接口文档 v0.1.0
language_tabs:
  - shell: Shell
  - javascript: JavaScript
language_clients:
  - shell: ""
  - javascript: ""
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="aicrm-">AiCRM 接口文档 v0.1.0</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

AI获客销售系统（AiCRM）核心业务接口文档。

统一返回结构：`{ code, message, data }`，code=200 表示成功；
分页接口 data 为 `PageResult{ page, size, total, pages, records }`；
错误码：400 参数错误 / 401 未登录 / 403 无权限 / 404 资源不存在 / 500 系统错误，业务错误码 1000+ 见各接口描述。

鉴权：除登录外的接口需携带请求头 `Authorization: Bearer <token>`；
多租户接口建议同时携带请求头 `X-Tenant-Id`。

Base URLs:

* <a href="http://localhost:8080">http://localhost:8080</a>

# Authentication

- HTTP Authentication, scheme: bearer 登录后获取的 JWT 令牌，格式：Bearer <token>

<h1 id="aicrm---">坐席管理</h1>

## 查询坐席详情

<a id="opIddetail"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/users/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/users/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/users/{id}`

需权限 user:list

<h3 id="查询坐席详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|坐席 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="查询坐席详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultUser](#schemaresultuser)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新坐席

<a id="opIdupdate"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/users/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "string",
  "mobile": "string",
  "email": "string",
  "roleCode": "sales",
  "status": 1,
  "lastActiveAt": "2019-08-24T14:15:22Z"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/users/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/users/{id}`

需权限 user:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "string",
  "mobile": "string",
  "email": "string",
  "roleCode": "sales",
  "status": 1,
  "lastActiveAt": "2019-08-24T14:15:22Z"
}
```

<h3 id="更新坐席-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|坐席 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[User](#schemauser)|true|none|

> Example responses

> 404 Response

<h3 id="更新坐席-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|用户不存在（业务码 1101）|[ResultUser](#schemaresultuser)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除坐席（逻辑删除）

<a id="opIddelete"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/users/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/users/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/users/{id}`

需权限 user:delete

<h3 id="删除坐席（逻辑删除）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|坐席 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除坐席（逻辑删除）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|用户不存在（业务码 1101）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 启用/停用坐席

<a id="opIdupdateStatus"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/users/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/users/{id}/status?status=0',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/users/{id}/status`

需权限 user:edit

<h3 id="启用/停用坐席-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|坐席 ID|
|status|query|integer(int32)|true|状态：1启用/0停用|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="启用/停用坐席-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|status 仅支持 0/1|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|用户不存在（业务码 1101）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 查询用户已分配角色 ID

<a id="opIdroleIds"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/users/{id}/roles \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/users/{id}/roles',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/users/{id}/roles`

需权限 user:list

<h3 id="查询用户已分配角色-id-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|坐席 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="查询用户已分配角色-id-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListLong](#schemaresultlistlong)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分配用户角色（全量覆盖）

<a id="opIdsetRoles"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/users/{id}/roles \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '[
  0
]';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/users/{id}/roles',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/users/{id}/roles`

需权限 user:edit

> Body parameter

```json
[
  0
]
```

<h3 id="分配用户角色（全量覆盖）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|坐席 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|array[integer]|true|none|

> Example responses

> 404 Response

<h3 id="分配用户角色（全量覆盖）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|用户不存在（业务码 1101）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 重置密码

<a id="opIdresetPassword"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/users/{id}/password?newPassword=string \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/users/{id}/password?newPassword=string',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/users/{id}/password`

需权限 user:reset-password

<h3 id="重置密码-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|坐席 ID|
|newPassword|query|string|true|新密码（明文）|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="重置密码-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|新密码长度不能少于 6 位|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|用户不存在（业务码 1101）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分页查询坐席

<a id="opIdpage"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/users \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/users',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/users`

需权限 user:list

<h3 id="分页查询坐席-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|false|租户 ID|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="分页查询坐席-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultUser](#schemaresultpageresultuser)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建坐席

<a id="opIdcreate"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "string",
  "mobile": "string",
  "email": "string",
  "roleCode": "sales",
  "status": 1,
  "lastActiveAt": "2019-08-24T14:15:22Z"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/users',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/users`

需权限 user:add

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "string",
  "mobile": "string",
  "email": "string",
  "roleCode": "sales",
  "status": 1,
  "lastActiveAt": "2019-08-24T14:15:22Z"
}
```

<h3 id="创建坐席-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[User](#schemauser)|true|none|

> Example responses

> 400 Response

<h3 id="创建坐席-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|租户 ID/手机号/密码不能为空|[ResultUser](#schemaresultuser)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">租户管理</h1>

## 租户详情

<a id="opIddetail_1"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/tenants/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tenants/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/tenants/{id}`

需权限 admin

<h3 id="租户详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|租户 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="租户详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultTenant](#schemaresulttenant)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新租户（含套餐/到期时间/联系人等）

<a id="opIdupdate_1"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/tenants/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "name": "string",
  "planCode": "starter",
  "seatCount": 0,
  "aiQuotaMonth": 0,
  "expireAt": "2019-08-24T14:15:22Z",
  "contactName": "string",
  "contactMobile": "string",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tenants/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/tenants/{id}`

需权限 admin

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "name": "string",
  "planCode": "starter",
  "seatCount": 0,
  "aiQuotaMonth": 0,
  "expireAt": "2019-08-24T14:15:22Z",
  "contactName": "string",
  "contactMobile": "string",
  "status": 1
}
```

<h3 id="更新租户（含套餐/到期时间/联系人等）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|租户 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Tenant](#schematenant)|true|none|

> Example responses

> 404 Response

<h3 id="更新租户（含套餐/到期时间/联系人等）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|租户不存在（业务码 1001）|[ResultTenant](#schemaresulttenant)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除租户（逻辑删除）

<a id="opIddelete_1"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/tenants/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tenants/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/tenants/{id}`

需权限 admin

<h3 id="删除租户（逻辑删除）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|租户 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除租户（逻辑删除）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|租户不存在（业务码 1001）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 启用/停用租户

<a id="opIdupdateStatus_1"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/tenants/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tenants/{id}/status?status=0',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/tenants/{id}/status`

需权限 admin

<h3 id="启用/停用租户-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|租户 ID|
|status|query|integer(int32)|true|状态：1启用/0停用|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="启用/停用租户-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|status 仅支持 0（停用）/1（启用）|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|租户不存在（业务码 1001）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分页查询租户

<a id="opIdpage_1"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/tenants \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tenants',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/tenants`

需权限 admin

<h3 id="分页查询租户-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（企业名称）|
|planCode|query|string|false|套餐编码：starter/pro/enterprise|
|status|query|integer(int32)|false|状态：1启用/0停用|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="分页查询租户-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultTenant](#schemaresultpageresulttenant)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建租户（自动初始化默认管理员账号）

<a id="opIdcreate_1"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/tenants \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "name": "string",
  "planCode": "starter",
  "seatCount": 0,
  "aiQuotaMonth": 0,
  "expireAt": "2019-08-24T14:15:22Z",
  "contactName": "string",
  "contactMobile": "string",
  "status": 1,
  "adminMobile": "string",
  "adminPassword": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tenants',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/tenants`

需权限 admin

> Body parameter

```json
{
  "name": "string",
  "planCode": "starter",
  "seatCount": 0,
  "aiQuotaMonth": 0,
  "expireAt": "2019-08-24T14:15:22Z",
  "contactName": "string",
  "contactMobile": "string",
  "status": 1,
  "adminMobile": "string",
  "adminPassword": "string"
}
```

<h3 id="创建租户（自动初始化默认管理员账号）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[TenantCreateRequest](#schematenantcreaterequest)|true|none|

> Example responses

> 400 Response

<h3 id="创建租户（自动初始化默认管理员账号）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|管理员手机号不能为空|[ResultTenant](#schemaresulttenant)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|套餐已下架（业务码 1005）|[ResultTenant](#schemaresulttenant)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">客户标签</h1>

## 更新标签

<a id="opIdupdateTag"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/tags/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "高意向客户",
  "color": "#FF5733",
  "remark": "string",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/tags/{id}`

权限：tag:edit。按 ID 更新标签名称/颜色/备注。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "高意向客户",
  "color": "#FF5733",
  "remark": "string",
  "status": 1
}
```

<h3 id="更新标签-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|标签 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[CustomerTag](#schemacustomertag)|true|none|

> Example responses

> 404 Response

<h3 id="更新标签-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|标签不存在|[ResultCustomerTag](#schemaresultcustomertag)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除标签

<a id="opIddeleteTag"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/tags/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/tags/{id}`

权限：tag:delete。按 ID 删除标签（含客户关联关系一并解除）。

<h3 id="删除标签-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|标签 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除标签-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|标签不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 标签启停

<a id="opIdupdateTagStatus"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/tags/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/{id}/status?status=0',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/tags/{id}/status`

权限：tag:edit。启停标签；status 取值：1启用/0停用。

<h3 id="标签启停-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|标签 ID|
|status|query|integer(int32)|true|状态：1启用/0停用|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="标签启停-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|status 仅支持 0/1|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新自动标签规则

<a id="opIdupdateRule"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/tags/rules/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "评分高于80自动打标",
  "tagId": 0,
  "conditionField": "score",
  "conditionOp": "gte",
  "conditionValue": "80",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/rules/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/tags/rules/{id}`

权限：tag:rule。按 ID 更新自动标签规则；condition_field/condition_op 取值见创建接口。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "评分高于80自动打标",
  "tagId": 0,
  "conditionField": "score",
  "conditionOp": "gte",
  "conditionValue": "80",
  "status": 1
}
```

<h3 id="更新自动标签规则-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|规则 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[CustomerTagRule](#schemacustomertagrule)|true|none|

> Example responses

> 400 Response

<h3 id="更新自动标签规则-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|规则名称/条件值/规则字段非法|[ResultCustomerTagRule](#schemaresultcustomertagrule)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|标签规则不存在|[ResultCustomerTagRule](#schemaresultcustomertagrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除自动标签规则

<a id="opIddeleteRule"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/tags/rules/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/rules/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/tags/rules/{id}`

权限：tag:rule。按 ID 删除自动标签规则。

<h3 id="删除自动标签规则-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|规则 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除自动标签规则-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|标签规则不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 标签分页查询

<a id="opIdpageTags"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/tags \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/tags`

权限：tag:list。按关键字（标签名称模糊匹配）分页查询标签。

<h3 id="标签分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（标签名称模糊匹配）|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="标签分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCustomerTag](#schemaresultpageresultcustomertag)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建标签

<a id="opIdcreateTag"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/tags \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "高意向客户",
  "color": "#FF5733",
  "remark": "string",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/tags`

权限：tag:add。新建客户标签定义，status 默认 1启用。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "高意向客户",
  "color": "#FF5733",
  "remark": "string",
  "status": 1
}
```

<h3 id="创建标签-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[CustomerTag](#schemacustomertag)|true|none|

> Example responses

> 400 Response

<h3 id="创建标签-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|标签名称不能为空|[ResultCustomerTag](#schemaresultcustomertag)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 规则分页查询

<a id="opIdpageRules"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/tags/rules \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/rules',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/tags/rules`

权限：tag:rule。分页查询自动标签规则。

<h3 id="规则分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="规则分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCustomerTagRule](#schemaresultpageresultcustomertagrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建自动标签规则

<a id="opIdcreateRule"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/tags/rules \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "评分高于80自动打标",
  "tagId": 0,
  "conditionField": "score",
  "conditionOp": "gte",
  "conditionValue": "80",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/rules',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/tags/rules`

权限：tag:rule。新建自动标签规则；condition_field 取值：score评分/intent_level意向等级/stage客户阶段/industry行业/region地区/source来源；condition_op 取值：gt大于/gte大于等于/lt小于/lte小于等于/eq等于/contains包含。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "评分高于80自动打标",
  "tagId": 0,
  "conditionField": "score",
  "conditionOp": "gte",
  "conditionValue": "80",
  "status": 1
}
```

<h3 id="创建自动标签规则-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[CustomerTagRule](#schemacustomertagrule)|true|none|

> Example responses

> 400 Response

<h3 id="创建自动标签规则-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|规则名称/规则字段/条件值非法|[ResultCustomerTagRule](#schemaresultcustomertagrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 手动执行规则

<a id="opIdapplyRule"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/tags/rules/{id}/apply \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/rules/{id}/apply',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/tags/rules/{id}/apply`

权限：tag:rule。立即对全量客户执行一次指定规则，命中客户自动打上规则绑定的标签；返回本次命中客户数。

<h3 id="手动执行规则-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|规则 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="手动执行规则-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|标签规则不存在|[ResultInteger](#schemaresultinteger)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 给客户批量打标

<a id="opIdtagCustomers"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/tags/customers/{customerId} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '[
  0
]';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/customers/{customerId}',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/tags/customers/{customerId}`

权限：tag:edit。请求体为标签 ID 列表，将多个标签绑定到指定客户。

> Body parameter

```json
[
  0
]
```

<h3 id="给客户批量打标-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|customerId|path|integer(int64)|true|客户 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|array[integer]|true|none|

> Example responses

> 400 Response

<h3 id="给客户批量打标-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|标签列表不能为空|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|客户不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 标签筛选：查询某标签下的客户

<a id="opIdpageCustomersByTag"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/tags/{id}/customers \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/{id}/customers',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/tags/{id}/customers`

权限：tag:list。按标签 ID 分页查询打上该标签的客户列表。

<h3 id="标签筛选：查询某标签下的客户-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|标签 ID|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="标签筛选：查询某标签下的客户-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCustomer](#schemaresultpageresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 移除客户标签

<a id="opIduntagCustomer"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/tags/customers/{customerId}/{tagId} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags/customers/{customerId}/{tagId}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/tags/customers/{customerId}/{tagId}`

权限：tag:edit。解除指定客户上的单个标签绑定。

<h3 id="移除客户标签-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|customerId|path|integer(int64)|true|客户 ID|
|tagId|path|integer(int64)|true|标签 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="移除客户标签-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|客户/标签不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">话术库</h1>

## 话术详情

<a id="opIddetail_2"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/speech-libraries/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/speech-libraries/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/speech-libraries/{id}`

按 ID 查询话术内容，场景：话术库管理、一键发送预览；权限：speech:list

<h3 id="话术详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|话术 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="话术详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultSpeechLibrary](#schemaresultspeechlibrary)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新话术

<a id="opIdupdate_2"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/speech-libraries/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "开场白-标准版",
  "category": "general",
  "content": "string",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/speech-libraries/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/speech-libraries/{id}`

修改话术标题/分类/内容，场景：话术库管理维护；权限：speech:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "开场白-标准版",
  "category": "general",
  "content": "string",
  "status": 1
}
```

<h3 id="更新话术-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|话术 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[SpeechLibrary](#schemaspeechlibrary)|true|none|

> Example responses

> 400 Response

<h3 id="更新话术-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|话术内容不能为空|[ResultSpeechLibrary](#schemaresultspeechlibrary)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|话术不存在|[ResultSpeechLibrary](#schemaresultspeechlibrary)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除话术

<a id="opIddelete_2"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/speech-libraries/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/speech-libraries/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/speech-libraries/{id}`

删除话术，场景：话术库管理维护；权限：speech:delete。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

<h3 id="删除话术-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|话术 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除话术-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|话术不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 话术启停

<a id="opIdupdateStatus_2"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/speech-libraries/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/speech-libraries/{id}/status?status=0',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/speech-libraries/{id}/status`

启用/停用话术，停用后不可被一键发送检索，场景：话术库上下架管理；权限：speech:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

<h3 id="话术启停-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|话术 ID|
|status|query|integer(int32)|true|状态：1 启用 / 0 停用|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="话术启停-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|话术不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 话术分页查询（keyword/场景分类/状态过滤）

<a id="opIdpage_2"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/speech-libraries \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/speech-libraries',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/speech-libraries`

话术列表检索，支持关键字/场景分类/状态过滤，场景：话术库管理、一键发送适配；权限：speech:list

<h3 id="话术分页查询（keyword/场景分类/状态过滤）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（话术标题/内容模糊匹配）|
|category|query|string|false|场景分类：general通用/quote报价/selection选型/objection异议/follow跟进/opening开场|
|status|query|integer(int32)|false|状态：1 启用 / 0 停用|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="话术分页查询（keyword/场景分类/状态过滤）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultSpeechLibrary](#schemaresultpageresultspeechlibrary)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建话术

<a id="opIdcreate_2"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/speech-libraries \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "开场白-标准版",
  "category": "general",
  "content": "string",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/speech-libraries',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/speech-libraries`

新增话术（通用/场景分类），场景：话术库管理维护；权限：speech:add。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "开场白-标准版",
  "category": "general",
  "content": "string",
  "status": 1
}
```

<h3 id="创建话术-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[SpeechLibrary](#schemaspeechlibrary)|true|none|

> Example responses

> 400 Response

<h3 id="创建话术-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|话术标题/内容不能为空|[ResultSpeechLibrary](#schemaresultspeechlibrary)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">角色管理</h1>

## 角色详情

<a id="opIddetail_3"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/roles/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/roles/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/roles/{id}`

需权限 role:list

<h3 id="角色详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|角色 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="角色详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultRole](#schemaresultrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新角色

<a id="opIdupdate_3"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/roles/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "code": "sales",
  "name": "string",
  "description": "string",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/roles/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/roles/{id}`

需权限 role:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "code": "sales",
  "name": "string",
  "description": "string",
  "status": 1
}
```

<h3 id="更新角色-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|角色 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Role](#schemarole)|true|none|

> Example responses

> 400 Response

<h3 id="更新角色-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|角色不存在|[ResultRole](#schemaresultrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除角色

<a id="opIddelete_3"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/roles/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/roles/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/roles/{id}`

需权限 role:delete

<h3 id="删除角色-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|角色 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="删除角色-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|角色不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 启用/停用角色

<a id="opIdupdateStatus_3"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/roles/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/roles/{id}/status?status=0',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/roles/{id}/status`

需权限 role:edit

<h3 id="启用/停用角色-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|角色 ID|
|status|query|integer(int32)|true|状态：1启用/0停用|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="启用/停用角色-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|角色不存在/status 仅支持 0/1|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 查询角色已分配的菜单 ID

<a id="opIdmenuIds"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/roles/{id}/menus \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/roles/{id}/menus',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/roles/{id}/menus`

需权限 role:assign-menu

<h3 id="查询角色已分配的菜单-id-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|角色 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="查询角色已分配的菜单-id-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListLong](#schemaresultlistlong)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分配角色菜单（全量覆盖）

<a id="opIdsetMenus"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/roles/{id}/menus \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '[
  0
]';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/roles/{id}/menus',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/roles/{id}/menus`

需权限 role:assign-menu

> Body parameter

```json
[
  0
]
```

<h3 id="分配角色菜单（全量覆盖）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|角色 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|array[integer]|true|none|

> Example responses

> 400 Response

<h3 id="分配角色菜单（全量覆盖）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|角色不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分页查询角色

<a id="opIdpage_3"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/roles \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/roles',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/roles`

需权限 role:list

<h3 id="分页查询角色-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（角色名称/编码）|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="分页查询角色-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultRole](#schemaresultpageresultrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建角色

<a id="opIdcreate_3"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/roles \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "code": "sales",
  "name": "string",
  "description": "string",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/roles',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/roles`

需权限 role:add

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "code": "sales",
  "name": "string",
  "description": "string",
  "status": 1
}
```

<h3 id="创建角色-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Role](#schemarole)|true|none|

> Example responses

> 400 Response

<h3 id="创建角色-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|角色编码与名称不能为空/角色编码已存在|[ResultRole](#schemaresultrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 启用角色列表（下拉用）

<a id="opIdenabled"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/roles/enabled \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/roles/enabled',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/roles/enabled`

登录用户即可访问

<h3 id="启用角色列表（下拉用）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="启用角色列表（下拉用）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListRole](#schemaresultlistrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">产品库</h1>

## 产品详情

<a id="opIddetail_4"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/products/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/products/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/products/{id}`

按 ID 查询产品完整资料（含参数/附件），场景：产品库管理、快捷发送预览；权限：product:list

<h3 id="产品详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|产品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="产品详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultProduct](#schemaresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新产品（含参数/附件）

<a id="opIdupdate_4"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/products/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "categoryId": 0,
  "name": "企业版 CRM",
  "sku": "CRM-ENT-001",
  "spec": "标准版",
  "price": 1999,
  "params": "{\"容量\":\"100GB\",\"并发数\":\"500\"}",
  "attachments": "[{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
  "description": "string",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/products/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/products/{id}`

修改产品资料（含参数/附件），场景：产品库管理维护；权限：product:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "categoryId": 0,
  "name": "企业版 CRM",
  "sku": "CRM-ENT-001",
  "spec": "标准版",
  "price": 1999,
  "params": "{\"容量\":\"100GB\",\"并发数\":\"500\"}",
  "attachments": "[{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
  "description": "string",
  "status": 1
}
```

<h3 id="更新产品（含参数/附件）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|产品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Product](#schemaproduct)|true|none|

> Example responses

> 400 Response

<h3 id="更新产品（含参数/附件）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|产品名称不能为空|[ResultProduct](#schemaresultproduct)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|产品不存在|[ResultProduct](#schemaresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除产品

<a id="opIddelete_4"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/products/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/products/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/products/{id}`

删除产品资料，场景：产品库管理维护；权限：product:delete。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

<h3 id="删除产品-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|产品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除产品-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|产品不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 产品上下架

<a id="opIdupdateStatus_4"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/products/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/products/{id}/status?status=0',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/products/{id}/status`

上架/下架产品，下架后不可被快捷发送检索，场景：产品库上下架管理；权限：product:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

<h3 id="产品上下架-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|产品 ID|
|status|query|integer(int32)|true|状态：1 上架 / 0 下架|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="产品上下架-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|产品不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新分类

<a id="opIdupdateCategory"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/product-categories/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "CRM 产品线",
  "parentId": 0,
  "sort": 1,
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/product-categories/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/product-categories/{id}`

修改产品分类名称/父级/排序/状态，场景：产品库分类体系维护；权限：product:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "CRM 产品线",
  "parentId": 0,
  "sort": 1,
  "status": 1
}
```

<h3 id="更新分类-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|分类 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[ProductCategory](#schemaproductcategory)|true|none|

> Example responses

> 404 Response

<h3 id="更新分类-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|产品分类不存在|[ResultProductCategory](#schemaresultproductcategory)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除分类

<a id="opIddeleteCategory"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/product-categories/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/product-categories/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/product-categories/{id}`

删除产品分类节点（存在子分类/关联产品时按业务规则处理），场景：产品库分类体系维护；权限：product:delete。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

<h3 id="删除分类-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|分类 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="删除分类-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|存在子分类/分类下存在产品，不能删除|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分类启停

<a id="opIdupdateCategoryStatus"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/product-categories/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/product-categories/{id}/status?status=0',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/product-categories/{id}/status`

启用/停用产品分类，停用后下级不再展示，场景：产品库分类体系维护；权限：product:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

<h3 id="分类启停-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|分类 ID|
|status|query|integer(int32)|true|状态：1 启用 / 0 停用|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="分类启停-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|产品分类不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 产品分页查询

<a id="opIdpage_4"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/products \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/products',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/products`

产品列表检索，支持关键字/分类/上下架状态过滤，场景：产品库管理、侧边栏快捷发送选品；权限：product:list

<h3 id="产品分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（产品名称/编码/规格模糊匹配）|
|categoryId|query|integer(int64)|false|分类 ID|
|status|query|integer(int32)|false|上下架状态：1 上架 / 0 下架|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="产品分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultProduct](#schemaresultpageresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建产品

<a id="opIdcreate_4"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "categoryId": 0,
  "name": "企业版 CRM",
  "sku": "CRM-ENT-001",
  "spec": "标准版",
  "price": 1999,
  "params": "{\"容量\":\"100GB\",\"并发数\":\"500\"}",
  "attachments": "[{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
  "description": "string",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/products',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/products`

新增产品资料（含参数/附件），场景：产品库管理维护；权限：product:add。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "categoryId": 0,
  "name": "企业版 CRM",
  "sku": "CRM-ENT-001",
  "spec": "标准版",
  "price": 1999,
  "params": "{\"容量\":\"100GB\",\"并发数\":\"500\"}",
  "attachments": "[{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
  "description": "string",
  "status": 1
}
```

<h3 id="创建产品-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Product](#schemaproduct)|true|none|

> Example responses

> 400 Response

<h3 id="创建产品-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|产品名称不能为空|[ResultProduct](#schemaresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分类树列表

<a id="opIdcategories"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/product-categories \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/product-categories',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/product-categories`

按层级返回产品分类树，场景：产品库管理、侧边栏快捷发送选品分类；权限：product:list

<h3 id="分类树列表-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="分类树列表-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListProductCategory](#schemaresultlistproductcategory)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建分类

<a id="opIdcreateCategory"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/product-categories \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "CRM 产品线",
  "parentId": 0,
  "sort": 1,
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/product-categories',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/product-categories`

新增产品分类节点，场景：产品库分类体系维护；权限：product:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "CRM 产品线",
  "parentId": 0,
  "sort": 1,
  "status": 1
}
```

<h3 id="创建分类-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[ProductCategory](#schemaproductcategory)|true|none|

> Example responses

> 400 Response

<h3 id="创建分类-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|分类名称不能为空|[ResultProductCategory](#schemaresultproductcategory)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">菜单管理</h1>

## 更新菜单

<a id="opIdupdate_5"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/menus/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "parentId": 0,
  "menuName": "string",
  "menuType": "menu",
  "path": "string",
  "component": "string",
  "perms": "user:add",
  "icon": "string",
  "sort": 0,
  "visible": 1,
  "status": 1,
  "children": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "parentId": 0,
      "menuName": "string",
      "menuType": "menu",
      "path": "string",
      "component": "string",
      "perms": "user:add",
      "icon": "string",
      "sort": 0,
      "visible": 1,
      "status": 1,
      "children": []
    }
  ]
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/menus/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/menus/{id}`

需权限 menu:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "parentId": 0,
  "menuName": "string",
  "menuType": "menu",
  "path": "string",
  "component": "string",
  "perms": "user:add",
  "icon": "string",
  "sort": 0,
  "visible": 1,
  "status": 1,
  "children": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "parentId": 0,
      "menuName": "string",
      "menuType": "menu",
      "path": "string",
      "component": "string",
      "perms": "user:add",
      "icon": "string",
      "sort": 0,
      "visible": 1,
      "status": 1,
      "children": []
    }
  ]
}
```

<h3 id="更新菜单-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|菜单 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Menu](#schemamenu)|true|none|

> Example responses

> 400 Response

<h3 id="更新菜单-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|菜单不存在/父菜单不能是自己|[ResultMenu](#schemaresultmenu)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除菜单

<a id="opIddelete_5"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/menus/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/menus/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/menus/{id}`

需权限 menu:delete

<h3 id="删除菜单-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|菜单 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="删除菜单-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|菜单不存在/存在子菜单，请先删除子菜单|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 全量菜单树（含按钮，平台管理用）

<a id="opIdtree"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/menus \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/menus',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/menus`

需权限 menu:list

<h3 id="全量菜单树（含按钮，平台管理用）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="全量菜单树（含按钮，平台管理用）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListMenu](#schemaresultlistmenu)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建菜单

<a id="opIdcreate_5"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/menus \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "parentId": 0,
  "menuName": "string",
  "menuType": "menu",
  "path": "string",
  "component": "string",
  "perms": "user:add",
  "icon": "string",
  "sort": 0,
  "visible": 1,
  "status": 1,
  "children": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "parentId": 0,
      "menuName": "string",
      "menuType": "menu",
      "path": "string",
      "component": "string",
      "perms": "user:add",
      "icon": "string",
      "sort": 0,
      "visible": 1,
      "status": 1,
      "children": []
    }
  ]
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/menus',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/menus`

需权限 menu:add

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "parentId": 0,
  "menuName": "string",
  "menuType": "menu",
  "path": "string",
  "component": "string",
  "perms": "user:add",
  "icon": "string",
  "sort": 0,
  "visible": 1,
  "status": 1,
  "children": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "parentId": 0,
      "menuName": "string",
      "menuType": "menu",
      "path": "string",
      "component": "string",
      "perms": "user:add",
      "icon": "string",
      "sort": 0,
      "visible": 1,
      "status": 1,
      "children": []
    }
  ]
}
```

<h3 id="创建菜单-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Menu](#schemamenu)|true|none|

> Example responses

> 400 Response

<h3 id="创建菜单-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|菜单名称不能为空|[ResultMenu](#schemaresultmenu)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 当前用户可见菜单树（前端路由）

<a id="opIdrouters"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/menus/routers \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/menus/routers',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/menus/routers`

登录用户即可访问

<h3 id="当前用户可见菜单树（前端路由）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="当前用户可见菜单树（前端路由）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListMenu](#schemaresultlistmenu)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">线索分配</h1>

## 更新分配规则

<a id="opIdupdateRule_1"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/lead-assign-rules/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "华东区域线索分配",
  "ruleType": "region",
  "matchValue": "华东",
  "targetUserId": 0,
  "targetGroupIds": "string",
  "sort": 1,
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/lead-assign-rules/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/lead-assign-rules/{id}`

权限：lead:assign。按 ID 更新分配规则；rule_type 取值：product按产品线/region按地域/round_robin轮询组。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "华东区域线索分配",
  "ruleType": "region",
  "matchValue": "华东",
  "targetUserId": 0,
  "targetGroupIds": "string",
  "sort": 1,
  "status": 1
}
```

<h3 id="更新分配规则-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|分配规则 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[LeadAssignRule](#schemaleadassignrule)|true|none|

> Example responses

> 404 Response

<h3 id="更新分配规则-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|分配规则不存在|[ResultLeadAssignRule](#schemaresultleadassignrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除分配规则

<a id="opIddeleteRule_1"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/lead-assign-rules/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/lead-assign-rules/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/lead-assign-rules/{id}`

权限：lead:assign。按 ID 删除分配规则。

<h3 id="删除分配规则-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|分配规则 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除分配规则-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|分配规则不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分配规则启停

<a id="opIdupdateRuleStatus"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/lead-assign-rules/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/lead-assign-rules/{id}/status?status=0',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/lead-assign-rules/{id}/status`

权限：lead:assign。启停分配规则；status 取值：1启用/0停用。

<h3 id="分配规则启停-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|分配规则 ID|
|status|query|integer(int32)|true|状态：1启用/0停用|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="分配规则启停-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|分配规则不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 手动分配单条线索

<a id="opIdassign"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/leads/{id}/assign \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/leads/{id}/assign',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/leads/{id}/assign`

权限：lead:assign。按当前租户已启用的分配规则（product/region/round_robin）将线索自动分配给匹配销售；无匹配规则时保持未分配。

<h3 id="手动分配单条线索-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|线索 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="手动分配单条线索-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|线索不存在（业务码 1201）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 回收超时线索并重新分配

<a id="opIdreassign"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/leads/reassign \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/leads/reassign',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/leads/reassign`

权限：lead:assign。扫描超过响应 SLA（sla_deadline）仍未处理的线索，回收后按分配规则重新分配；返回本次回收并重新分配的线索数。

<h3 id="回收超时线索并重新分配-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="回收超时线索并重新分配-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultInteger](#schemaresultinteger)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分配规则分页查询

<a id="opIdpageRules_1"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/lead-assign-rules \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/lead-assign-rules',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/lead-assign-rules`

权限：lead:assign。分页查询当前租户的线索分配规则。

<h3 id="分配规则分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="分配规则分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultLeadAssignRule](#schemaresultpageresultleadassignrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建分配规则

<a id="opIdcreateRule_1"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/lead-assign-rules \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "华东区域线索分配",
  "ruleType": "region",
  "matchValue": "华东",
  "targetUserId": 0,
  "targetGroupIds": "string",
  "sort": 1,
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/lead-assign-rules',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/lead-assign-rules`

权限：lead:assign。新建线索分配规则；rule_type 取值：product按产品线/region按地域/round_robin轮询组。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "华东区域线索分配",
  "ruleType": "region",
  "matchValue": "华东",
  "targetUserId": 0,
  "targetGroupIds": "string",
  "sort": 1,
  "status": 1
}
```

<h3 id="创建分配规则-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[LeadAssignRule](#schemaleadassignrule)|true|none|

> Example responses

> 400 Response

<h3 id="创建分配规则-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|规则名称/规则类型/轮询组/匹配值/指定销售不能为空|[ResultLeadAssignRule](#schemaresultleadassignrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">资料包中心</h1>

## 发布资料

<a id="opIdpublish"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/documents/{id}/publish \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/documents/{id}/publish',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/documents/{id}/publish`

将草稿资料置为已发布状态

<h3 id="发布资料-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|资料 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="发布资料-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|资料不存在|[ResultDocument](#schemaresultdocument)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 分页查询资料

<a id="opIdpage_8"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/documents \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/documents',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/documents`

按租户/资料类型过滤

<h3 id="分页查询资料-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|false|租户 ID（平台管理员可指定，为空默认当前租户）|
|docType|query|string|false|资料类型：product_brochure产品手册/case案例/whitepaper白皮书/selection_table选型表|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="分页查询资料-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultDocument](#schemaresultpageresultdocument)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 新增资料

<a id="opIdcreate_8"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/documents \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "AI 外呼系统产品手册",
  "docType": "product_brochure",
  "fileUrl": "/uploads/xxx.pdf",
  "version": 1,
  "status": 0,
  "tags": "[\"AI\",\"外呼\"]"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/documents',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/documents`

请求体为资料信息（不含文件，文件通过上传接口获取 URL 后写入 fileUrl）

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "AI 外呼系统产品手册",
  "docType": "product_brochure",
  "fileUrl": "/uploads/xxx.pdf",
  "version": 1,
  "status": 0,
  "tags": "[\"AI\",\"外呼\"]"
}
```

<h3 id="新增资料-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Document](#schemadocument)|true|none|

> Example responses

> 400 Response

<h3 id="新增资料-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|tenantId/title/docType 不能为空|[ResultDocument](#schemaresultdocument)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 上传资料文件（返回可访问 URL）

<a id="opIdupload_1"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/documents/upload \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "file": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/documents/upload',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/documents/upload`

multipart/form-data 表单上传，返回 /uploads/xxx 形式的访问 URL

> Body parameter

```json
{
  "file": "string"
}
```

<h3 id="上传资料文件（返回可访问-url）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|object|false|none|
|» file|body|string(binary)|true|文件|

> Example responses

> 400 Response

<h3 id="上传资料文件（返回可访问-url）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|文件不能为空|[ResultString](#schemaresultstring)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|文件上传失败（业务码 1701）|[ResultString](#schemaresultstring)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">字典管理</h1>

## 更新字典类型

<a id="opIdupdateType"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/dicts/types/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "dictName": "线索状态",
  "status": 1,
  "remark": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/types/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/dicts/types/{id}`

需权限 dict:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "dictName": "线索状态",
  "status": 1,
  "remark": "string"
}
```

<h3 id="更新字典类型-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|字典类型 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[DictType](#schemadicttype)|true|none|

> Example responses

> 400 Response

<h3 id="更新字典类型-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|字典类型不存在|[ResultDictType](#schemaresultdicttype)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除字典类型

<a id="opIddeleteType"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/dicts/types/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/types/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/dicts/types/{id}`

需权限 dict:delete

<h3 id="删除字典类型-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|字典类型 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="删除字典类型-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|字典类型不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新字典数据

<a id="opIdupdateData"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/dicts/data/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "label": "已分配",
  "value": "1",
  "sort": 0,
  "status": 1,
  "remark": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/data/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/dicts/data/{id}`

需权限 dict:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "label": "已分配",
  "value": "1",
  "sort": 0,
  "status": 1,
  "remark": "string"
}
```

<h3 id="更新字典数据-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|字典数据 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[DictData](#schemadictdata)|true|none|

> Example responses

> 400 Response

<h3 id="更新字典数据-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|字典数据不存在|[ResultDictData](#schemaresultdictdata)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除字典数据

<a id="opIddeleteData"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/dicts/data/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/data/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/dicts/data/{id}`

需权限 dict:delete

<h3 id="删除字典数据-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|字典数据 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="删除字典数据-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|字典数据不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 字典类型分页

<a id="opIdpageTypes"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/dicts/types \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/types',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/dicts/types`

需权限 dict:list

<h3 id="字典类型分页-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（字典名称/编码模糊匹配）|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="字典类型分页-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultDictType](#schemaresultpageresultdicttype)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建字典类型

<a id="opIdcreateType"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/dicts/types \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "dictName": "线索状态",
  "status": 1,
  "remark": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/types',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/dicts/types`

需权限 dict:add

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "dictName": "线索状态",
  "status": 1,
  "remark": "string"
}
```

<h3 id="创建字典类型-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[DictType](#schemadicttype)|true|none|

> Example responses

> 400 Response

<h3 id="创建字典类型-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|字典类型编码已存在/编码与名称不能为空|[ResultDictType](#schemaresultdicttype)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 字典数据分页

<a id="opIdpageData"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/dicts/data \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/data',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/dicts/data`

需权限 dict:list

<h3 id="字典数据分页-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|dictType|query|string|false|字典类型编码|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="字典数据分页-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultDictData](#schemaresultpageresultdictdata)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建字典数据

<a id="opIdcreateData"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/dicts/data \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "label": "已分配",
  "value": "1",
  "sort": 0,
  "status": 1,
  "remark": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/data',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/dicts/data`

需权限 dict:add

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "label": "已分配",
  "value": "1",
  "sort": 0,
  "status": 1,
  "remark": "string"
}
```

<h3 id="创建字典数据-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[DictData](#schemadictdata)|true|none|

> Example responses

> 400 Response

<h3 id="创建字典数据-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|字典类型、标签、值不能为空|[ResultDictData](#schemaresultdictdata)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 启用字典类型列表

<a id="opIdlistTypes"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/dicts/types/enabled \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/types/enabled',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/dicts/types/enabled`

前端下拉框直接调用，仅返回启用状态字典类型，无需登录权限

<h3 id="启用字典类型列表-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="启用字典类型列表-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListDictType](#schemaresultlistdicttype)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 按类型查询启用字典数据（前端下拉）

<a id="opIdlistDataByType"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/dicts/data/type/{dictType} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dicts/data/type/{dictType}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/dicts/data/type/{dictType}`

前端下拉框直接调用，仅返回启用状态字典数据，无需登录权限

<h3 id="按类型查询启用字典数据（前端下拉）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|dictType|path|string|true|字典类型编码|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="按类型查询启用字典数据（前端下拉）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListDictData](#schemaresultlistdictdata)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">客户管理</h1>

## 客户详情

<a id="opIddetail_8"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/customers/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/customers/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/customers/{id}`

权限：customer:list。按 ID 查询客户详情（含人员架构 org_structure）。

<h3 id="客户详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|客户 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="客户详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新客户

<a id="opIdupdate_6"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/customers/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某科技有限公司",
  "industry": "软件服务",
  "scale": "100-499人",
  "region": "上海",
  "orgStructure": "string",
  "source": "public_data",
  "enrichmentStatus": 0,
  "stage": "potential",
  "intentLevel": 3,
  "score": 80
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/customers/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/customers/{id}`

权限：customer:edit。按 ID 更新客户基本信息（名称/行业/规模/地区等）。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某科技有限公司",
  "industry": "软件服务",
  "scale": "100-499人",
  "region": "上海",
  "orgStructure": "string",
  "source": "public_data",
  "enrichmentStatus": 0,
  "stage": "potential",
  "intentLevel": 3,
  "score": 80
}
```

<h3 id="更新客户-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|客户 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Customer](#schemacustomer)|true|none|

> Example responses

> 404 Response

<h3 id="更新客户-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|客户不存在|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除客户

<a id="opIddelete_7"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/customers/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/customers/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/customers/{id}`

权限：customer:delete。按 ID 逻辑删除客户。

<h3 id="删除客户-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|客户 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除客户-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|客户不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 客户阶段流转

<a id="opIdupdateStage"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/customers/{id}/stage?stage=string \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/customers/{id}/stage?stage=string',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/customers/{id}/stage`

权限：customer:edit。推进/回退客户阶段；stage 取值：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失。

<h3 id="客户阶段流转-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|客户 ID|
|stage|query|string|true|客户阶段：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="客户阶段流转-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|非法客户阶段|[ResultCustomer](#schemaresultcustomer)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|客户不存在|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 意向等级/评分维护

<a id="opIdupdateScore"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/customers/{id}/score \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/customers/{id}/score',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/customers/{id}/score`

权限：customer:edit。维护客户意向等级与综合评分；intentLevel 取值 0-5，score 取值 0-100。

<h3 id="意向等级/评分维护-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|客户 ID|
|intentLevel|query|integer(int32)|false|意向等级：0-5|
|score|query|integer(int32)|false|综合评分：0-100|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="意向等级/评分维护-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|意向等级需在 0-5 之间/评分需在 0-100 之间|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 客户分页查询

<a id="opIdpage_9"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/customers \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/customers',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/customers`

权限：customer:list。按关键字/行业/地区/客户阶段分页查询客户；stage 取值：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失，结果按综合评分倒序。

<h3 id="客户分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（公司名称模糊匹配）|
|industry|query|string|false|行业（模糊匹配）|
|region|query|string|false|地区（模糊匹配）|
|stage|query|string|false|客户阶段：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="客户分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCustomer](#schemaresultpageresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建客户

<a id="opIdcreate_9"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/customers \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某科技有限公司",
  "industry": "软件服务",
  "scale": "100-499人",
  "region": "上海",
  "orgStructure": "string",
  "source": "public_data",
  "enrichmentStatus": 0,
  "stage": "potential",
  "intentLevel": 3,
  "score": 80
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/customers',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/customers`

权限：customer:add。新建客户公司，客户名称必填；stage 默认 new潜在。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某科技有限公司",
  "industry": "软件服务",
  "scale": "100-499人",
  "region": "上海",
  "orgStructure": "string",
  "source": "public_data",
  "enrichmentStatus": 0,
  "stage": "potential",
  "intentLevel": 3,
  "score": 80
}
```

<h3 id="创建客户-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Customer](#schemacustomer)|true|none|

> Example responses

> 400 Response

<h3 id="创建客户-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|客户名称不能为空|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 企业信息回填（第三方查询）

<a id="opIdenrich"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/customers/{id}/enrich \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = 'string';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/customers/{id}/enrich',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/customers/{id}/enrich`

权限：customer:edit。第三方企业信息查询接口，有调用频率限制，请勿高频调用；返回字段以第三方响应为准。请求体为人员架构 org_structure 原始 JSON 字符串（可空），例如 {"executives":[{"name":"张三","title":"CEO","phone":"13800138000","email":"zhangsan@corp.com"}],"departments":["销售部","技术部"]}；接口会将 org_structure 与 source 写入客户并置 enrichment_status=1，返回回填后的客户详情（含 org_structure）。

> Body parameter

```json
"string"
```

<h3 id="企业信息回填（第三方查询）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|客户 ID|
|source|query|string|false|数据来源标识（如 public_data公开数据/customer_provided客户提供）|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|string|false|none|

> Example responses

> 404 Response

<h3 id="企业信息回填（第三方查询）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|客户不存在|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">系统参数</h1>

## 更新参数

<a id="opIdupdate_7"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/configs/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "configKey": "system.siteName",
  "configValue": "AI 销售线索系统",
  "configName": "站点名称",
  "configType": 2,
  "remark": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/configs/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/configs/{id}`

需权限 config:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "configKey": "system.siteName",
  "configValue": "AI 销售线索系统",
  "configName": "站点名称",
  "configType": 2,
  "remark": "string"
}
```

<h3 id="更新参数-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|参数 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Config](#schemaconfig)|true|none|

> Example responses

> 200 Response

<h3 id="更新参数-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultConfig](#schemaresultconfig)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除参数（内置参数不可删除）

<a id="opIddelete_8"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/configs/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/configs/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/configs/{id}`

需权限 config:edit，内置参数（configType=1）删除将返回业务错误

<h3 id="删除参数（内置参数不可删除）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|参数 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="删除参数（内置参数不可删除）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|内置参数不允许删除|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 参数分页

<a id="opIdpage_11"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/configs \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/configs',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/configs`

需权限 config:list

<h3 id="参数分页-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（参数名称/参数键模糊匹配）|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="参数分页-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultConfig](#schemaresultpageresultconfig)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建参数

<a id="opIdcreate_11"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/configs \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "configKey": "system.siteName",
  "configValue": "AI 销售线索系统",
  "configName": "站点名称",
  "configType": 2,
  "remark": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/configs',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/configs`

需权限 config:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "configKey": "system.siteName",
  "configValue": "AI 销售线索系统",
  "configName": "站点名称",
  "configType": 2,
  "remark": "string"
}
```

<h3 id="创建参数-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Config](#schemaconfig)|true|none|

> Example responses

> 200 Response

<h3 id="创建参数-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultConfig](#schemaresultconfig)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 按 key 查询参数（业务方调用）

<a id="opIdgetByKey"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/configs/key/{key} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/configs/key/{key}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/configs/key/{key}`

业务方直接调用，无需登录权限，不存在时返回 null

<h3 id="按-key-查询参数（业务方调用）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|key|path|string|true|参数键|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="按-key-查询参数（业务方调用）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultConfig](#schemaresultconfig)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">竞品库</h1>

## 竞品详情

<a id="opIddetail_10"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/competitors/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/competitors/{id}`

按 ID 查询竞品完整档案（含优劣势/攻防话术），场景：竞品库管理、知识检索；权限：competitor:list

<h3 id="竞品详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|竞品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="竞品详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCompetitor](#schemaresultcompetitor)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新竞品（优劣势/攻防话术）

<a id="opIdupdate_8"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/competitors/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某云 CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"功能全面\",\"价格低\"]",
  "weaknesses": "[\"实施复杂\",\"售后差\"]",
  "defenseTactics": "[{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/competitors/{id}`

修改竞品档案（含优劣势/攻防话术），场景：竞品库管理维护；权限：competitor:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某云 CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"功能全面\",\"价格低\"]",
  "weaknesses": "[\"实施复杂\",\"售后差\"]",
  "defenseTactics": "[{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]",
  "status": 1
}
```

<h3 id="更新竞品（优劣势/攻防话术）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|竞品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Competitor](#schemacompetitor)|true|none|

> Example responses

> 400 Response

<h3 id="更新竞品（优劣势/攻防话术）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|竞品名称不能为空/竞品不存在|[ResultCompetitor](#schemaresultcompetitor)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除竞品

<a id="opIddelete_9"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/competitors/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/competitors/{id}`

删除竞品档案（含关联竞品产品），场景：竞品库管理维护；权限：competitor:delete。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

<h3 id="删除竞品-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|竞品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除竞品-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|竞品不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 竞品启停

<a id="opIdupdateStatus_5"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/competitors/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors/{id}/status?status=0',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/competitors/{id}/status`

启用/停用竞品，停用后不可被知识检索，场景：竞品库上下架管理；权限：competitor:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

<h3 id="竞品启停-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|竞品 ID|
|status|query|integer(int32)|true|状态：1 启用 / 0 停用|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="竞品启停-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|竞品不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新竞品产品参数

<a id="opIdupdateProduct"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/competitors/products/{productId} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "competitorId": 0,
  "productName": "竞品云版",
  "spec": "旗舰版",
  "price": 2999,
  "params": "{\"并发数\":\"300\",\"存储\":\"50GB\"}"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors/products/{productId}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/competitors/products/{productId}`

修改竞品产品参数，场景：竞品库管理维护；权限：competitor:edit。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "competitorId": 0,
  "productName": "竞品云版",
  "spec": "旗舰版",
  "price": 2999,
  "params": "{\"并发数\":\"300\",\"存储\":\"50GB\"}"
}
```

<h3 id="更新竞品产品参数-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|productId|path|integer(int64)|true|竞品产品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[CompetitorProduct](#schemacompetitorproduct)|true|none|

> Example responses

> 400 Response

<h3 id="更新竞品产品参数-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|竞品产品名称不能为空|[ResultCompetitorProduct](#schemaresultcompetitorproduct)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|竞品产品不存在|[ResultCompetitorProduct](#schemaresultcompetitorproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除竞品产品

<a id="opIddeleteProduct"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/competitors/products/{productId} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors/products/{productId}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/competitors/products/{productId}`

删除竞品产品参数，场景：竞品库管理维护；权限：competitor:delete。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

<h3 id="删除竞品产品-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|productId|path|integer(int64)|true|竞品产品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除竞品产品-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|竞品产品不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 竞品分页查询

<a id="opIdpage_12"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/competitors \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/competitors`

竞品列表检索，支持关键字/品类过滤，场景：竞品库管理、知识检索；权限：competitor:list

<h3 id="竞品分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（竞品名称/主体描述模糊匹配）|
|category|query|string|false|竞品品类|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="竞品分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCompetitor](#schemaresultpageresultcompetitor)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建竞品

<a id="opIdcreate_12"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/competitors \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某云 CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"功能全面\",\"价格低\"]",
  "weaknesses": "[\"实施复杂\",\"售后差\"]",
  "defenseTactics": "[{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]",
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/competitors`

新增竞品主体档案，场景：竞品库管理维护；权限：competitor:add。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某云 CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"功能全面\",\"价格低\"]",
  "weaknesses": "[\"实施复杂\",\"售后差\"]",
  "defenseTactics": "[{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]",
  "status": 1
}
```

<h3 id="创建竞品-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Competitor](#schemacompetitor)|true|none|

> Example responses

> 200 Response

<h3 id="创建竞品-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCompetitor](#schemaresultcompetitor)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 竞品产品列表

<a id="opIdlistProducts"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/competitors/{id}/products \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors/{id}/products',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/competitors/{id}/products`

查询指定竞品下的产品参数列表，场景：竞品库管理、知识检索；权限：competitor:list

<h3 id="竞品产品列表-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|竞品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="竞品产品列表-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListCompetitorProduct](#schemaresultlistcompetitorproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 新增竞品产品参数

<a id="opIdcreateProduct"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/competitors/{id}/products \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "competitorId": 0,
  "productName": "竞品云版",
  "spec": "旗舰版",
  "price": 2999,
  "params": "{\"并发数\":\"300\",\"存储\":\"50GB\"}"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/competitors/{id}/products',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/competitors/{id}/products`

为指定竞品新增产品参数，场景：竞品库管理维护；权限：competitor:add。⚠️ 异步联动：本操作会触发 RabbitMQ 通知（exchange=aicrm.events，routingKey=knowledge.sync），Python 侧消费后更新向量数据；MQ 不可用时仅记录日志降级，不影响本接口返回

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "competitorId": 0,
  "productName": "竞品云版",
  "spec": "旗舰版",
  "price": 2999,
  "params": "{\"并发数\":\"300\",\"存储\":\"50GB\"}"
}
```

<h3 id="新增竞品产品参数-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|竞品 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[CompetitorProduct](#schemacompetitorproduct)|true|none|

> Example responses

> 200 Response

<h3 id="新增竞品产品参数-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCompetitorProduct](#schemaresultcompetitorproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">渠道活码</h1>

## 更新活码

<a id="opIdupdate_9"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/channel/qr-codes/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "name": "抖音引流活码",
  "channelAccountId": 1,
  "scene": "1:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1,
  "scanCount": 0,
  "convertedCount": 0
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/qr-codes/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/channel/qr-codes/{id}`

更新渠道活码信息（整体覆盖提交字段，未提交字段保持原值）。需权限 channel:edit。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "name": "抖音引流活码",
  "channelAccountId": 1,
  "scene": "1:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1,
  "scanCount": 0,
  "convertedCount": 0
}
```

<h3 id="更新活码-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|活码 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[ChannelQrCode](#schemachannelqrcode)|true|none|

> Example responses

> 404 Response

<h3 id="更新活码-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|活码不存在|[ResultChannelQrCode](#schemaresultchannelqrcode)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除活码

<a id="opIddelete_10"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/channel/qr-codes/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/qr-codes/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/channel/qr-codes/{id}`

删除渠道活码（逻辑删除），已产生的扫码事件数据保留。需权限 channel:delete。

<h3 id="删除活码-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|活码 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除活码-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|活码不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 活码分页查询

<a id="opIdpage_13"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/channel/qr-codes \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/qr-codes',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/channel/qr-codes`

渠道活码分页查询，支持按名称关键字/引流渠道账号过滤。需权限 channel:list。

<h3 id="活码分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|活码名称关键字（模糊匹配）|
|channelAccountId|query|integer(int64)|false|引流渠道账号 ID（对应 channel_account.id）|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="活码分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultChannelQrCode](#schemaresultpageresultchannelqrcode)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 生成活码

<a id="opIdcreate_13"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/channel/qr-codes \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "name": "抖音引流活码",
  "channelAccountId": 1,
  "scene": "1:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1,
  "scanCount": 0,
  "convertedCount": 0
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/qr-codes',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/channel/qr-codes`

生成渠道活码：指定引流渠道账号与落地地址，生成的二维码内容指向 /{id}/scan 扫码入口，用于渠道来源标记与引流归因。需权限 channel:add。
scene 不填时默认取 渠道ID:账号ID；scanCount/convertedCount 不填默认 0；status 不填默认 1 启用。

请求示例：
```json
{
  "name": "抖音引流活码",
  "channelAccountId": 1,
  "scene": "douyin:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1
}
```
返回示例：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "tenantId": 1,
    "name": "抖音引流活码",
    "channelAccountId": 1,
    "scene": "douyin:1",
    "qrUrl": "https://example.com/landing?from=qr",
    "status": 1,
    "scanCount": 0,
    "convertedCount": 0,
    "createdAt": "2026-08-03 12:00:00",
    "updatedAt": "2026-08-03 12:00:00"
  }
}
```

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "name": "抖音引流活码",
  "channelAccountId": 1,
  "scene": "1:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1,
  "scanCount": 0,
  "convertedCount": 0
}
```

<h3 id="生成活码-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[ChannelQrCode](#schemachannelqrcode)|true|none|

> Example responses

> 400 Response

<h3 id="生成活码-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|活码名称/引流渠道账号不能为空|[ResultChannelQrCode](#schemaresultchannelqrcode)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|引流渠道账号不存在（业务码 1302）|[ResultChannelQrCode](#schemaresultchannelqrcode)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 启停活码

<a id="opIdupdateStatus_6"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/channel/qr-codes/{id}/status?status=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/qr-codes/{id}/status?status=0',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/channel/qr-codes/{id}/status`

启用/停用渠道活码：停用后扫码入口返回 400 业务错误“活码已停用”。需权限 channel:edit。

<h3 id="启停活码-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|活码 ID|
|status|query|integer(int32)|true|状态：1启用/0停用|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="启停活码-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|活码不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 扫码跳转入口

<a id="opIdscan"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/channel/qr-codes/{id}/scan \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/qr-codes/{id}/scan',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/channel/qr-codes/{id}/scan`

扫码跳转入口（二维码内容指向此地址）：记录扫码次数并上报扫码事件（event_type=qr，携带 scene 来源标记用于线索归因），随后 302 跳转到落地地址 qrUrl；活码停用时返回 400 业务错误“活码已停用”。公开接口，无登录与权限校验。

<h3 id="扫码跳转入口-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|活码 ID|
|scene|query|string|false|渠道来源标记（扫码场景），不传则取活码自带 scene|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

<h3 id="扫码跳转入口-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|活码已停用|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|活码不存在|None|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">渠道账号</h1>

## 更新账号信息/授权配置

<a id="opIdupdate_10"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/channel/accounts/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelId": 1,
  "accountName": "品牌官方号",
  "externalId": "douyin_open_id_xxx",
  "authConfig": "string",
  "healthStatus": 1,
  "riskLevel": 0
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/accounts/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/channel/accounts/{id}`

更新渠道账号信息或授权配置（整体覆盖提交字段，未提交字段保持原值）。需权限 channel:edit。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelId": 1,
  "accountName": "品牌官方号",
  "externalId": "douyin_open_id_xxx",
  "authConfig": "string",
  "healthStatus": 1,
  "riskLevel": 0
}
```

<h3 id="更新账号信息/授权配置-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|渠道账号 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[ChannelAccount](#schemachannelaccount)|true|none|

> Example responses

> 404 Response

<h3 id="更新账号信息/授权配置-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|渠道账号不存在（业务码 1302）|[ResultChannelAccount](#schemaresultchannelaccount)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除渠道账号

<a id="opIddelete_11"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/channel/accounts/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/accounts/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/channel/accounts/{id}`

删除渠道账号（逻辑删除）。需权限 channel:delete。

<h3 id="删除渠道账号-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|渠道账号 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除渠道账号-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|渠道账号不存在（业务码 1302）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 账号分页查询

<a id="opIdpage_14"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/channel/accounts \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/accounts',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/channel/accounts`

渠道账号分页查询，支持按账号名称关键字/渠道/健康状态过滤。需权限 channel:list。

<h3 id="账号分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|账号名称关键字（模糊匹配）|
|channelId|query|integer(int64)|false|渠道 ID（对应 channel.id）|
|healthStatus|query|integer(int32)|false|健康状态：1正常/2受限/3封禁|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="账号分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultChannelAccount](#schemaresultpageresultchannelaccount)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 绑定渠道账号

<a id="opIdcreate_14"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/channel/accounts \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelId": 1,
  "accountName": "品牌官方号",
  "externalId": "douyin_open_id_xxx",
  "authConfig": "string",
  "healthStatus": 1,
  "riskLevel": 0
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/accounts',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/channel/accounts`

绑定渠道账号：录入账号基础信息与授权配置 auth_config，绑定后可用于该渠道的消息收发与事件接收。需权限 channel:add。
auth_config（JSONB 字符串）存储结构：{"accessToken":"访问令牌","refreshToken":"刷新令牌","expireAt":"过期时间epoch毫秒"}，抖音另含 appId/secret，企业微信另含 corpId/corpSecret，按渠道可扩展。

请求示例：
```json
{
  "channelId": 1,
  "accountName": "品牌官方号",
  "externalId": "douyin_open_id_123",
  "authConfig": "{\"accessToken\":\"at_xxx\",\"refreshToken\":\"rt_xxx\",\"expireAt\":\"1780000000000\",\"appId\":\"appid\",\"secret\":\"secret\"}",
  "healthStatus": 1,
  "riskLevel": 0
}
```
返回示例：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "tenantId": 1,
    "channelId": 1,
    "accountName": "品牌官方号",
    "externalId": "douyin_open_id_123",
    "authConfig": "{\"accessToken\":\"at_xxx\",...}",
    "healthStatus": 1,
    "riskLevel": 0,
    "createdAt": "2026-08-03 12:00:00",
    "updatedAt": "2026-08-03 12:00:00"
  }
}
```

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelId": 1,
  "accountName": "品牌官方号",
  "externalId": "douyin_open_id_xxx",
  "authConfig": "string",
  "healthStatus": 1,
  "riskLevel": 0
}
```

<h3 id="绑定渠道账号-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[ChannelAccount](#schemachannelaccount)|true|none|

> Example responses

> 400 Response

<h3 id="绑定渠道账号-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|账号名称/渠道不能为空|[ResultChannelAccount](#schemaresultchannelaccount)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 刷新授权令牌

<a id="opIdrefreshToken"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/channel/accounts/{id}/refresh-token \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/accounts/{id}/refresh-token',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/channel/accounts/{id}/refresh-token`

手动刷新渠道账号的 access_token（token 过期或手动触发场景）。刷新成功返回 true，失败返回 false（如密钥错误/接口异常）。需权限 channel:token。

<h3 id="刷新授权令牌-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|渠道账号 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="刷新授权令牌-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|渠道账号不存在（业务码 1302）|[ResultBoolean](#schemaresultboolean)|
|502|[Bad Gateway](https://tools.ietf.org/html/rfc7231#section-6.6.3)|渠道开放平台 API 调用失败（业务码 1304）|[ResultBoolean](#schemaresultboolean)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 更新健康状态（状态监控回调）

<a id="opIdupdateHealth"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/channel/accounts/{id}/health?healthStatus=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/accounts/{id}/health?healthStatus=0',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/channel/accounts/{id}/health`

更新渠道账号健康状态/风控等级（供定时巡检或人工标记调用）。需权限 channel:token。

<h3 id="更新健康状态（状态监控回调）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|渠道账号 ID|
|healthStatus|query|integer(int32)|true|健康状态：1正常/2受限/3封禁|
|riskLevel|query|integer(int32)|false|风控等级：0低/1中/2高（可空，传空则不更新）|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="更新健康状态（状态监控回调）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|渠道账号不存在（业务码 1302）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">线索管理</h1>

## 分页查询线索

<a id="opIdpage_5"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/leads \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/leads',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/leads`

权限：lead:list。按租户/状态/意向维度分页查询线索；status 取值：new新线索/assigned已分配/contacting跟进中/effective有效/quoted已报价/opportunity商机/lost流失；intent 取值：quote报价/sample样品/selection选型/other其他。

<h3 id="分页查询线索-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|false|租户 ID（平台管理员可指定，为空默认当前租户）|
|status|query|string|false|线索状态：new新线索/assigned已分配/contacting跟进中/effective有效/quoted已报价/opportunity商机/lost流失|
|intent|query|string|false|客户意向：quote报价/sample样品/selection选型/other其他|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="分页查询线索-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultLead](#schemaresultpageresultlead)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建线索

<a id="opIdcreate_6"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/leads \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "customerId": 0,
  "contactId": 0,
  "sourceChannelId": 0,
  "sourceContentId": "string",
  "sourceType": "comment",
  "intent": "quote",
  "status": "new",
  "score": 60,
  "ownerId": 0,
  "slaDeadline": "2019-08-24T14:15:22Z",
  "extra": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/leads',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/leads`

权限：lead:list。创建线索（渠道接入/坐席手动录入）；intent 取值：quote报价/sample样品/selection选型/other其他，status 默认 new新线索。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "customerId": 0,
  "contactId": 0,
  "sourceChannelId": 0,
  "sourceContentId": "string",
  "sourceType": "comment",
  "intent": "quote",
  "status": "new",
  "score": 60,
  "ownerId": 0,
  "slaDeadline": "2019-08-24T14:15:22Z",
  "extra": "string"
}
```

<h3 id="创建线索-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Lead](#schemalead)|true|none|

> Example responses

> 400 Response

<h3 id="创建线索-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|tenantId 不能为空|[ResultLead](#schemaresultlead)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 查询线索详情

<a id="opIddetail_6"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/leads/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/leads/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/leads/{id}`

权限：lead:list。按 ID 查询线索详情，含抽取关键字段（extra）。

<h3 id="查询线索详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|线索 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="查询线索详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultLead](#schemaresultlead)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">定时任务</h1>

## 手动触发任务

<a id="opIdrun"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/jobs/{code}/run \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/jobs/{code}/run',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/jobs/{code}/run`

需权限 job:run，立即执行一次指定任务

<h3 id="手动触发任务-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|code|path|string|true|任务编码|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="手动触发任务-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|任务不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 任务列表（含运行状态）

<a id="opIdlist_1"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/jobs \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/jobs',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/jobs`

需权限 job:list，返回全部已注册定时任务及其当前运行状态

<h3 id="任务列表（含运行状态）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="任务列表（含运行状态）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListJobInfo](#schemaresultlistjobinfo)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 任务执行记录分页查询

<a id="opIdlogs"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/jobs/logs \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/jobs/logs',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/jobs/logs`

需权限 job:list

<h3 id="任务执行记录分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|jobCode|query|string|false|任务编码|
|result|query|integer(int32)|false|执行结果：1 成功 / 0 失败|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="任务执行记录分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultJobLog](#schemaresultpageresultjoblog)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">身份归一</h1>

## 线索合并（次要线索归并到主线索）

<a id="opIdmergeLeads"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/identities/leads/{primaryId}/merge/{secondaryId} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/identities/leads/{primaryId}/merge/{secondaryId}',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/identities/leads/{primaryId}/merge/{secondaryId}`

权限：identity:merge。将次要线索的身份映射、跟进记录等归并到主线索，并删除次要线索；返回合并后的主线索 ID。

<h3 id="线索合并（次要线索归并到主线索）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|primaryId|path|integer(int64)|true|主线索 ID（保留）|
|secondaryId|path|integer(int64)|true|次要线索 ID（被合并）|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="线索合并（次要线索归并到主线索）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|不能合并自身|[ResultLong](#schemaresultlong)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|线索不存在（业务码 1201）|[ResultLong](#schemaresultlong)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 手动绑定身份到线索

<a id="opIdbind"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/identities/bind \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "tenantId": 0,
  "identityType": "string",
  "identityValue": "string",
  "entityId": 0,
  "source": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/identities/bind',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/identities/bind`

权限：identity:edit。将一条身份（手机号/邮箱/社媒ID/企微ID/WhatsApp/企业域名）手动绑定到指定线索；tenantId 为空时默认当前租户。

> Body parameter

```json
{
  "tenantId": 0,
  "identityType": "string",
  "identityValue": "string",
  "entityId": 0,
  "source": "string"
}
```

<h3 id="手动绑定身份到线索-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[BindRequest](#schemabindrequest)|true|none|

> Example responses

> 400 Response

<h3 id="手动绑定身份到线索-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|身份类型与值不能为空|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|线索不存在（业务码 1201）|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 查询实体的身份映射列表

<a id="opIdmappings"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/identities?entityType=string&entityId=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/identities?entityType=string&entityId=0',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/identities`

权限：identity:list。按实体类型+实体 ID 查询其下绑定的全部身份映射（含匹配置信度）。

<h3 id="查询实体的身份映射列表-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|entityType|query|string|true|实体类型：lead线索/contact联系人/customer客户|
|entityId|query|integer(int64)|true|实体 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="查询实体的身份映射列表-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListIdentityMapping](#schemaresultlistidentitymapping)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 身份详情

<a id="opIddetail_7"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/identities/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/identities/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/identities/{id}`

权限：identity:list。按 ID 查询身份详情（identity_type 取值：mobile手机号/email邮箱/social社媒ID/wecom企微ID/whatsapp/domain企业域名）。

<h3 id="身份详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|身份 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="身份详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultIdentity](#schemaresultidentity)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除身份映射

<a id="opIdremoveMapping"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/identities/mappings/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/identities/mappings/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/identities/mappings/{id}`

权限：identity:delete。按映射 ID 删除身份与实体之间的绑定关系。

<h3 id="删除身份映射-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|身份映射 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="删除身份映射-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|身份映射不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">跟进记录</h1>

## 跟进记录历史查询

<a id="opIdpage_6"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/follow-ups \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/follow-ups',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/follow-ups`

权限：follow:list。按线索或客户维度分页查询跟进历史，两个维度均可不填（查全部）。

<h3 id="跟进记录历史查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|leadId|query|integer(int64)|false|关联线索 ID（按线索维度查跟进记录）|
|customerId|query|integer(int64)|false|关联客户 ID（按客户维度查跟进记录）|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="跟进记录历史查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultFollowUp](#schemaresultpageresultfollowup)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 新增跟进记录（自动同步线索跟进状态）

<a id="opIdcreate_7"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/follow-ups \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "leadId": 0,
  "customerId": 0,
  "userId": 0,
  "content": "客户对报价方案有意向，约下周面谈",
  "method": "wechat",
  "nextTime": "2019-08-24T14:15:22Z"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/follow-ups',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/follow-ups`

权限：follow:add。新增一条跟进记录；若关联线索，创建后自动将线索状态同步为跟进中（contacting）。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "leadId": 0,
  "customerId": 0,
  "userId": 0,
  "content": "客户对报价方案有意向，约下周面谈",
  "method": "wechat",
  "nextTime": "2019-08-24T14:15:22Z"
}
```

<h3 id="新增跟进记录（自动同步线索跟进状态）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[FollowUp](#schemafollowup)|true|none|

> Example responses

> 400 Response

<h3 id="新增跟进记录（自动同步线索跟进状态）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|跟进内容不能为空/线索或客户至少关联一个|[ResultFollowUp](#schemaresultfollowup)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">文件管理</h1>

## 上传文件

<a id="opIdupload"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/files/upload \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "file": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/files/upload',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/files/upload`

multipart/form-data 表单上传，file 字段为文件流，返回文件记录与访问地址

> Body parameter

```json
{
  "file": "string"
}
```

<h3 id="上传文件-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|object|false|none|
|» file|body|string(binary)|true|文件|

> Example responses

> 400 Response

<h3 id="上传文件-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|上传文件不能为空|[ResultFileUploadVO](#schemaresultfileuploadvo)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|文件上传失败（业务码 1701）|[ResultFileUploadVO](#schemaresultfileuploadvo)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 文件列表分页

<a id="opIdpage_7"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/files \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/files',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/files`

需权限 file:list

<h3 id="文件列表分页-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="文件列表分页-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultFileRecord](#schemaresultpageresultfilerecord)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 下载/预览文件

<a id="opIddownload"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/files/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/files/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/files/{id}`

按文件 ID 返回文件内容（响应头带原始文件名与 Content-Type）

<h3 id="下载/预览文件-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|文件记录 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="下载/预览文件-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 删除文件

<a id="opIddelete_6"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/files/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/files/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/files/{id}`

需权限 file:delete

<h3 id="删除文件-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|文件记录 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="删除文件-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|文件不存在|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">会话</h1>

## 会话分页查询

<a id="opIdpage_10"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/conversations \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/conversations`

会话管理 - 会话分页查询（权限 conversation:list），支持关键字（会话类型/ID）、状态、处理人过滤。

status 枚举：active进行中/transferred已转人工/closed已关闭/archived已归档。

<h3 id="会话分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（模糊匹配会话类型/会话 ID）|
|status|query|string|false|会话状态：active进行中/transferred已转人工/closed已关闭/archived已归档|
|assignedTo|query|integer(int64)|false|当前人工处理人 ID|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="会话分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultConversation](#schemaresultpageresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 创建会话

<a id="opIdcreate_10"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/conversations \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelAccountId": 1,
  "contactId": 1,
  "leadId": 1,
  "conversationType": "wecom_chat",
  "status": "active",
  "assignedTo": 8,
  "lastMessageAt": "2019-08-24T14:15:22Z"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/conversations`

会话管理 - 创建会话（权限 conversation:edit），承接企微/WhatsApp/私信客户，是会话生命周期起点。

必填：tenantId、leadId；status 不传时默认 active。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelAccountId": 1,
  "contactId": 1,
  "leadId": 1,
  "conversationType": "wecom_chat",
  "status": "active",
  "assignedTo": 8,
  "lastMessageAt": "2019-08-24T14:15:22Z"
}
```

<h3 id="创建会话-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Conversation](#schemaconversation)|true|none|

> Example responses

> 400 Response

<h3 id="创建会话-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|tenantId 与 leadId 不能为空|[ResultConversation](#schemaresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 转人工并生成交接包（不传 operatorId 时自动分配销售）

<a id="opIdtransfer"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/conversations/{id}/transfer \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations/{id}/transfer',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/conversations/{id}/transfer`

会话管理 - 转人工并生成交接包（权限 conversation:edit）：会话状态置为 transferred、assignedTo 指向接手坐席，返回 AI 摘要 + 最近意向 + 缺失字段 + 推荐话术的交接包。

operatorId 不传时优先线索负责人，否则走分配引擎自动分配；无可用坐席时返回 400。

请求示例：
```
POST /api/conversations/100/transfer?operatorId=8
```
返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": 100,
    "tenantId": 1,
    "leadId": 50,
    "conversationType": "wecom_chat",
    "operatorId": 8,
    "summary": "客户咨询产品报价，已确认应用场景，采购数量与预算待补充",
    "summarySource": "ai",
    "intent": "quote",
    "confidence": 0.82,
    "evidence": "客户提到需要 500 台，希望本周内拿到报价",
    "missingFields": ["qty", "budget"],
    "recommendedReply": "您好，我是人工顾问，已为您整理报价需求。为准确报价，请补充：应用场景、采购数量、预算范围、期望交期，我尽快给您正式报价。",
    "nextSteps": [
      "AI 已生成对话摘要，坐席确认后回复客户",
      "补齐报价关键字段（qty/budget）",
      "字段齐全后生成报价单，跟进报价意向",
      "低置信度/复杂问题已转人工，坐席优先响应"
    ],
    "transferredAt": "2026-08-03 14:30:00"
  }
}
```

<h3 id="转人工并生成交接包（不传-operatorid-时自动分配销售）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|会话 ID|
|operatorId|query|integer(int64)|false|接手坐席 ID（可空，不传时自动分配）|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="转人工并生成交接包（不传-operatorid-时自动分配销售）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|暂无可接手的在线坐席/operatorId 不能为空|[ResultTransferPackage](#schemaresulttransferpackage)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|会话不存在（业务码 1401）|[ResultTransferPackage](#schemaresulttransferpackage)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 会话消息分页（历史漫游）

<a id="opIdmessages"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/conversations/{id}/messages \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations/{id}/messages',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/conversations/{id}/messages`

会话管理 - 消息分页查询（权限 message:list），可按发送方过滤，消息按 ID 升序返回。

senderType 枚举：customer客户/ai机器人/human人工/system系统。

<h3 id="会话消息分页（历史漫游）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|会话 ID|
|senderType|query|string|false|发送方：customer客户/ai机器人/human人工/system系统|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="会话消息分页（历史漫游）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultMessage](#schemaresultpageresultmessage)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 追加消息

<a id="opIdappendMessage"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/conversations/{id}/messages \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "conversationId": 100,
  "senderType": "customer",
  "content": "string",
  "msgType": "text",
  "attachments": "[]",
  "aiGenerated": false,
  "quotedDocIds": "[]",
  "raw": "{}"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations/{id}/messages',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/conversations/{id}/messages`

会话管理 - 追加一条消息（权限 message:add），用于人工/系统补录消息。

请求示例：
```json
{
  "tenantId": 1,
  "conversationId": 100,
  "senderType": "human",
  "content": "您好，关于报价需求我已整理，请补充采购数量",
  "msgType": "text",
  "aiGenerated": false
}
```
返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1001,
    "tenantId": 1,
    "conversationId": 100,
    "senderType": "human",
    "content": "您好，关于报价需求我已整理，请补充采购数量",
    "msgType": "text",
    "aiGenerated": false,
    "createdAt": "2026-08-03 12:00:00",
    "updatedAt": "2026-08-03 12:00:00"
  }
}
```
注意：conversationId 以路径参数为准，请求体中的值会被覆盖；msgType 不传默认 text。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "conversationId": 100,
  "senderType": "customer",
  "content": "string",
  "msgType": "text",
  "attachments": "[]",
  "aiGenerated": false,
  "quotedDocIds": "[]",
  "raw": "{}"
}
```

<h3 id="追加消息-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|会话 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[Message](#schemamessage)|true|none|

> Example responses

> 400 Response

<h3 id="追加消息-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|conversationId/消息内容不能为空|[ResultMessage](#schemaresultmessage)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|会话不存在（业务码 1401）|[ResultMessage](#schemaresultmessage)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 关闭会话

<a id="opIdclose"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/conversations/{id}/close \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations/{id}/close',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/conversations/{id}/close`

会话管理 - 关闭会话（权限 conversation:edit），已归档会话不可关闭，关闭后状态置为 closed。

<h3 id="关闭会话-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|会话 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 400 Response

<h3 id="关闭会话-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|已归档会话不能关闭|[ResultConversation](#schemaresultconversation)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|会话不存在（业务码 1401）|[ResultConversation](#schemaresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 归档会话

<a id="opIdarchive"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/conversations/{id}/archive \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations/{id}/archive',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/conversations/{id}/archive`

会话管理 - 归档会话（权限 conversation:edit），归档后状态置为 archived。

<h3 id="归档会话-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|会话 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 404 Response

<h3 id="归档会话-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|会话不存在（业务码 1401）|[ResultConversation](#schemaresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## AI 接待（消息落库 → 意向判定 → 生成回复）

<a id="opIdaiReply"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/conversations/{id}/ai-reply \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = 'string';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations/{id}/ai-reply',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/conversations/{id}/ai-reply`

会话管理 - AI 接待入口（权限 message:add）：客户消息落库 → 意向判定 → 生成 AI 回复（AI 不可用降级规则话术）→ 回复落库。

请求体为 JSON 字符串（裸字符串），例如："我想了解贵司产品的报价"
返回 AiReply 字段：content 回复内容、intent 意向（quote/sample/selection/other）、confidence 置信度、shouldTransfer 是否建议转人工。

> Body parameter

```json
"string"
```

<h3 id="ai-接待（消息落库-→-意向判定-→-生成回复）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|会话 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|string|true|none|

> Example responses

> 404 Response

<h3 id="ai-接待（消息落库-→-意向判定-→-生成回复）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|会话不存在（业务码 1401）|[ResultAiReply](#schemaresultaireply)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 会话详情

<a id="opIddetail_9"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/conversations/{id} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/conversations/{id}`

会话管理 - 会话详情（权限 conversation:list）。

<h3 id="会话详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|会话 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="会话详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultConversation](#schemaresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 查询交接包（坐席接手后查看）

<a id="opIdgetTransferPackage"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/conversations/{id}/transfer-package \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations/{id}/transfer-package',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/conversations/{id}/transfer-package`

会话管理 - 查询交接包（权限 conversation:list）：坐席接手后查看会话上下文（对话摘要 + 意向 + 缺失字段 + 推荐话术）。

返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": 100,
    "tenantId": 1,
    "leadId": 50,
    "conversationType": "wecom_chat",
    "operatorId": 8,
    "summary": "AI 摘要服务暂不可用，请坐席查看最近 20 条消息记录。",
    "summarySource": "fallback",
    "intent": "selection",
    "confidence": 0.76,
    "evidence": "客户表示对型号选型不确定，需要对比推荐",
    "missingFields": ["scene", "qty", "budget", "lead_time", "model"],
    "recommendedReply": "您好，已收到您的选型需求。为给出精准推荐，请补充应用场景与工况要求，我们马上为您选型。",
    "nextSteps": [
      "AI 已生成对话摘要，坐席确认后回复客户",
      "输出选型对比表（推荐 2-3 款）",
      "选型确认后引导报价",
      "低置信度/复杂问题已转人工，坐席优先响应"
    ],
    "transferredAt": "2026-08-03 14:30:00"
  }
}
```

<h3 id="查询交接包（坐席接手后查看）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|会话 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="查询交接包（坐席接手后查看）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultTransferPackage](#schemaresulttransferpackage)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 转人工条件评估

<a id="opIdevaluateTransfer"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/conversations/{id}/transfer-evaluation \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/conversations/{id}/transfer-evaluation',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/conversations/{id}/transfer-evaluation`

会话管理 - 转人工条件评估（权限 conversation:list）：基于最近意向判定结果判断是否需要转人工。

触发条件：意向为 other / 置信度低于 0.5 / 尚无意向判定。
返回 TransferEvaluation 字段：shouldTransfer 是否建议转人工、intent 意向、confidence 置信度、reason 判定原因。

<h3 id="转人工条件评估-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|会话 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="转人工条件评估-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultTransferEvaluation](#schemaresulttransferevaluation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">渠道事件</h1>

## 接收渠道事件（Webhook）

<a id="opIdreceive"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/channel/events \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelAccountId": 1,
  "eventType": "dm",
  "externalEventId": "evt_20260803_001",
  "externalUserId": "openid_xxx",
  "rawPayload": "string",
  "mapped": 0
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channel/events',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/channel/events`

三渠道 Webhook 统一入口：接收抖音/视频号/企业微信等渠道推送的事件，按 (tenantId+channelAccountId+externalEventId) 幂等去重后入库并发布异步事件。
返回 true 表示首次接收并已入库；返回 false 表示重复事件被忽略（不重复落库）。
eventType 取值：comment评论/dm私信/form表单/click点击/lead线索。
rawPayload（JSONB）存储渠道回调的完整原始 JSON，字段随渠道与事件类型不同。
该接口为渠道平台回调入口，无登录与权限校验（本版本为模拟实现，未做渠道签名校验）。

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelAccountId": 1,
  "eventType": "dm",
  "externalEventId": "evt_20260803_001",
  "externalUserId": "openid_xxx",
  "rawPayload": "string",
  "mapped": 0
}
```

<h3 id="接收渠道事件（webhook）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[ChannelEvent](#schemachannelevent)|true|none|

> Example responses

> 200 Response

<h3 id="接收渠道事件（webhook）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultBoolean](#schemaresultboolean)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">认证</h1>

## 登录（签发 JWT）

<a id="opIdlogin"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "tenantId": 0,
  "mobile": "13800138000",
  "password": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/auth/login',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/auth/login`

公开接口，无需登录；成功返回 JWT 令牌

> Body parameter

```json
{
  "tenantId": 0,
  "mobile": "13800138000",
  "password": "string"
}
```

<h3 id="登录（签发-jwt）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|
|body|body|[LoginRequest](#schemaloginrequest)|true|none|

> Example responses

> 400 Response

<h3 id="登录（签发-jwt）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|账号或密码错误/账号已禁用/参数缺失|[ResultLoginResponse](#schemaresultloginresponse)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|租户已停用（业务码 1002）|[ResultLoginResponse](#schemaresultloginresponse)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|租户不存在（业务码 1001）|[ResultLoginResponse](#schemaresultloginresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">企微侧边栏</h1>

## 推荐话术

<a id="opIdreplySuggestions"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/wecom/sidebar/reply-suggestions?externalUserId=string \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/wecom/sidebar/reply-suggestions?externalUserId=string',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/wecom/sidebar/reply-suggestions`

企微侧边栏 - 推荐话术（权限 wecom:sidebar）：按意向返回建议话术与缺失字段；intent 不传时取线索最近意向。

intent 枚举：quote报价/sample样品/selection选型/other其他。

<h3 id="推荐话术-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|externalUserId|query|string|true|企微外部用户 ID|
|intent|query|string|false|意向：quote报价/sample样品/selection选型/other其他（可空，默认取线索最近意向）|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="推荐话术-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultReplySuggestion](#schemaresultreplysuggestion)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 客户画像

<a id="opIdprofile"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/wecom/sidebar/profile?externalUserId=string \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/wecom/sidebar/profile?externalUserId=string',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/wecom/sidebar/profile`

企微侧边栏 - 客户画像（权限 wecom:sidebar）：按企微外部用户 ID 聚合身份→线索→客户信息、标签、最近意向。

<h3 id="客户画像-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|externalUserId|query|string|true|企微外部用户 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="客户画像-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCustomerProfile](#schemaresultcustomerprofile)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 产品资料（快捷发送）

<a id="opIdproducts"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/wecom/sidebar/products \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/wecom/sidebar/products',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/wecom/sidebar/products`

企微侧边栏 - 产品资料分页（权限 wecom:sidebar）：仅展示上架产品，供坐席快捷发送。

<h3 id="产品资料（快捷发送）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|产品名称关键字（可空）|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="产品资料（快捷发送）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultProduct](#schemaresultpageresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 历史会话

<a id="opIdhistory"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/wecom/sidebar/history?externalUserId=string \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/wecom/sidebar/history?externalUserId=string',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/wecom/sidebar/history`

企微侧边栏 - 历史会话（权限 wecom:sidebar）：返回该企微客户关联的会话列表（含最近一条消息）。

<h3 id="历史会话-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|externalUserId|query|string|true|企微外部用户 ID|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="历史会话-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListConversationBrief](#schemaresultlistconversationbrief)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">套餐</h1>

## 套餐列表（仅上架）

<a id="opIdlist"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/plans \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/plans',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/plans`

登录用户即可访问

<h3 id="套餐列表（仅上架）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="套餐列表（仅上架）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListPlan](#schemaresultlistplan)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 套餐详情

<a id="opIddetail_5"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/plans/{code} \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/plans/{code}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/plans/{code}`

登录用户即可访问

<h3 id="套餐详情-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|code|path|string|true|套餐编码：starter/pro/enterprise|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="套餐详情-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPlan](#schemaresultplan)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">日志管理</h1>

## 操作日志分页查询

<a id="opIdpageOperLogs"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/logs/oper \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/logs/oper',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/logs/oper`

需权限 log:list

<h3 id="操作日志分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|关键字（操作人姓名/操作内容/请求地址模糊匹配）|
|module|query|string|false|业务模块|
|result|query|integer(int32)|false|执行结果：1 成功 / 0 失败|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="操作日志分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultOperLog](#schemaresultpageresultoperlog)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 登录日志分页查询

<a id="opIdpageLoginLogs"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/logs/login \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/logs/login',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/logs/login`

需权限 log:list

<h3 id="登录日志分页查询-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|mobile|query|string|false|登录手机号|
|status|query|integer(int32)|false|登录结果：1 成功 / 0 失败|
|page|query|integer(int64)|false|页码，默认 1|
|size|query|integer(int64)|false|每页条数，默认 20|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="登录日志分页查询-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultLoginLog](#schemaresultpageresultloginlog)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">基础看板</h1>

## 每日趋势（线索量/会话量/AI 解决量按日聚合）

<a id="opIdtrend"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/dashboard/trend?tenantId=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dashboard/trend?tenantId=0',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/dashboard/trend`

按日聚合线索/会话/AI 解决量趋势，场景：趋势分析与运营复盘

<h3 id="每日趋势（线索量/会话量/ai-解决量按日聚合）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|true|租户 ID|
|from|query|string(date-time)|false|开始时间（yyyy-MM-dd HH:mm:ss），为空取全部|
|to|query|string(date-time)|false|结束时间（yyyy-MM-dd HH:mm:ss），为空取全部|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="每日趋势（线索量/会话量/ai-解决量按日聚合）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListDashboardTrend](#schemaresultlistdashboardtrend)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 销售工作量统计（按销售聚合分配线索/跟进/成交/会话）

<a id="opIdsalesWorkload"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/dashboard/sales-workload?tenantId=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dashboard/sales-workload?tenantId=0',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/dashboard/sales-workload`

按销售聚合分配线索/跟进/成交/会话，场景：销售绩效统计与工作量评估

<h3 id="销售工作量统计（按销售聚合分配线索/跟进/成交/会话）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|true|租户 ID|
|from|query|string(date-time)|false|开始时间（yyyy-MM-dd HH:mm:ss），为空取全部|
|to|query|string(date-time)|false|结束时间（yyyy-MM-dd HH:mm:ss），为空取全部|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="销售工作量统计（按销售聚合分配线索/跟进/成交/会话）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListSalesWorkloadStat](#schemaresultlistsalesworkloadstat)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 看板总览（线索量/会话量/转人工数/响应时效/有效对话率/意向分布）

<a id="opIdoverview"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/dashboard/overview?tenantId=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dashboard/overview?tenantId=0',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/dashboard/overview`

按时间范围聚合看板核心指标，场景：管理后台首页数据总览

<h3 id="看板总览（线索量/会话量/转人工数/响应时效/有效对话率/意向分布）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|true|租户 ID|
|from|query|string(date-time)|false|开始时间（yyyy-MM-dd HH:mm:ss），为空取全部|
|to|query|string(date-time)|false|结束时间（yyyy-MM-dd HH:mm:ss），为空取全部|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="看板总览（线索量/会话量/转人工数/响应时效/有效对话率/意向分布）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultDashboardOverview](#schemaresultdashboardoverview)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## 渠道转化数据（按渠道账号聚合事件/线索/转化率）

<a id="opIdchannelConversion"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/dashboard/channel-conversion?tenantId=0 \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/dashboard/channel-conversion?tenantId=0',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/dashboard/channel-conversion`

按渠道账号聚合事件/线索/转化率，场景：渠道效果分析与投放优化

<h3 id="渠道转化数据（按渠道账号聚合事件/线索/转化率）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|true|租户 ID|
|from|query|string(date-time)|false|开始时间（yyyy-MM-dd HH:mm:ss），为空取全部|
|to|query|string(date-time)|false|结束时间（yyyy-MM-dd HH:mm:ss），为空取全部|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="渠道转化数据（按渠道账号聚合事件/线索/转化率）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListChannelConversionStat](#schemaresultlistchannelconversionstat)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">渠道管理</h1>

## 启用渠道列表（前端下拉）

<a id="opIdlistEnabled"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/channels \
  -H 'Accept: */*' \
  -H 'X-Tenant-Id: string' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'X-Tenant-Id':'string',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/channels',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/channels`

获取全部启用状态的渠道定义列表，供前端下拉/筛选使用。需权限 channel:list。
渠道编码 code 取值：douyin抖音 / video_channel视频号 / tiktok TikTok / wecom企业微信 / whatsapp WhatsApp。

<h3 id="启用渠道列表（前端下拉）-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|租户 ID（多租户隔离场景必填；未登录直连调试时使用）|

> Example responses

> 200 Response

<h3 id="启用渠道列表（前端下拉）-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListChannel](#schemaresultlistchannel)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

# Schemas

<h2 id="tocS_User">User</h2>
<!-- backwards compatibility -->
<a id="schemauser"></a>
<a id="schema_User"></a>
<a id="tocSuser"></a>
<a id="tocsuser"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "string",
  "mobile": "string",
  "email": "string",
  "roleCode": "sales",
  "status": 1,
  "lastActiveAt": "2019-08-24T14:15:22Z"
}

```

坐席/员工

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|name|string|false|none|姓名|
|mobile|string|false|none|手机号|
|email|string|false|none|邮箱|
|roleCode|string|false|none|角色编码：sales/supervisor/admin|
|status|integer(int32)|false|none|状态：1启用/0停用|
|lastActiveAt|string(date-time)|false|none|最后活跃时间|

<h2 id="tocS_ResultUser">ResultUser</h2>
<!-- backwards compatibility -->
<a id="schemaresultuser"></a>
<a id="schema_ResultUser"></a>
<a id="tocSresultuser"></a>
<a id="tocsresultuser"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "name": "string",
    "mobile": "string",
    "email": "string",
    "roleCode": "sales",
    "status": 1,
    "lastActiveAt": "2019-08-24T14:15:22Z"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[User](#schemauser)|false|none|坐席/员工|

<h2 id="tocS_ResultVoid">ResultVoid</h2>
<!-- backwards compatibility -->
<a id="schemaresultvoid"></a>
<a id="schema_ResultVoid"></a>
<a id="tocSresultvoid"></a>
<a id="tocsresultvoid"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|object|false|none|业务数据|

<h2 id="tocS_Tenant">Tenant</h2>
<!-- backwards compatibility -->
<a id="schematenant"></a>
<a id="schema_Tenant"></a>
<a id="tocStenant"></a>
<a id="tocstenant"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "name": "string",
  "planCode": "starter",
  "seatCount": 0,
  "aiQuotaMonth": 0,
  "expireAt": "2019-08-24T14:15:22Z",
  "contactName": "string",
  "contactMobile": "string",
  "status": 1
}

```

租户（企业）—— 订阅模式根实体

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|name|string|false|none|企业名称|
|planCode|string|false|none|套餐编码：starter/pro/enterprise|
|seatCount|integer(int32)|false|none|坐席数上限|
|aiQuotaMonth|integer(int64)|false|none|月度 AI 调用额度|
|expireAt|string(date-time)|false|none|到期时间，为空表示长期有效|
|contactName|string|false|none|平台对接联系人|
|contactMobile|string|false|none|平台对接联系电话|
|status|integer(int32)|false|none|状态：1启用/0停用|

<h2 id="tocS_ResultTenant">ResultTenant</h2>
<!-- backwards compatibility -->
<a id="schemaresulttenant"></a>
<a id="schema_ResultTenant"></a>
<a id="tocSresulttenant"></a>
<a id="tocsresulttenant"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "name": "string",
    "planCode": "starter",
    "seatCount": 0,
    "aiQuotaMonth": 0,
    "expireAt": "2019-08-24T14:15:22Z",
    "contactName": "string",
    "contactMobile": "string",
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Tenant](#schematenant)|false|none|租户（企业）—— 订阅模式根实体|

<h2 id="tocS_CustomerTag">CustomerTag</h2>
<!-- backwards compatibility -->
<a id="schemacustomertag"></a>
<a id="schema_CustomerTag"></a>
<a id="tocScustomertag"></a>
<a id="tocscustomertag"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "高意向客户",
  "color": "#FF5733",
  "remark": "string",
  "status": 1
}

```

标签信息

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|name|string|false|none|标签名称|
|color|string|false|none|标签颜色（前端展示，如 #FF5733）|
|remark|string|false|none|备注|
|status|integer(int32)|false|none|状态：1 启用 / 0 停用|

<h2 id="tocS_ResultCustomerTag">ResultCustomerTag</h2>
<!-- backwards compatibility -->
<a id="schemaresultcustomertag"></a>
<a id="schema_ResultCustomerTag"></a>
<a id="tocSresultcustomertag"></a>
<a id="tocsresultcustomertag"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "name": "高意向客户",
    "color": "#FF5733",
    "remark": "string",
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[CustomerTag](#schemacustomertag)|false|none|标签信息|

<h2 id="tocS_CustomerTagRule">CustomerTagRule</h2>
<!-- backwards compatibility -->
<a id="schemacustomertagrule"></a>
<a id="schema_CustomerTagRule"></a>
<a id="tocScustomertagrule"></a>
<a id="tocscustomertagrule"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "评分高于80自动打标",
  "tagId": 0,
  "conditionField": "score",
  "conditionOp": "gte",
  "conditionValue": "80",
  "status": 1
}

```

自动标签规则信息

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|ruleName|string|false|none|规则名称|
|tagId|integer(int64)|false|none|命中后打的标签 ID|
|conditionField|string|false|none|条件字段：score评分/intent_level意向等级/stage客户阶段/industry行业/region地区/source来源|
|conditionOp|string|false|none|条件操作符：gt大于/gte大于等于/lt小于/lte小于等于/eq等于/contains包含|
|conditionValue|string|false|none|条件值（与 condition_field 对应，如评分阈值、阶段取值、行业名等）|
|status|integer(int32)|false|none|状态：1 启用 / 0 停用|

<h2 id="tocS_ResultCustomerTagRule">ResultCustomerTagRule</h2>
<!-- backwards compatibility -->
<a id="schemaresultcustomertagrule"></a>
<a id="schema_ResultCustomerTagRule"></a>
<a id="tocSresultcustomertagrule"></a>
<a id="tocsresultcustomertagrule"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "ruleName": "评分高于80自动打标",
    "tagId": 0,
    "conditionField": "score",
    "conditionOp": "gte",
    "conditionValue": "80",
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[CustomerTagRule](#schemacustomertagrule)|false|none|自动标签规则信息|

<h2 id="tocS_SpeechLibrary">SpeechLibrary</h2>
<!-- backwards compatibility -->
<a id="schemaspeechlibrary"></a>
<a id="schema_SpeechLibrary"></a>
<a id="tocSspeechlibrary"></a>
<a id="tocsspeechlibrary"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "开场白-标准版",
  "category": "general",
  "content": "string",
  "status": 1
}

```

话术库

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|title|string|false|none|话术标题|
|category|string|false|none|场景分类：general通用/quote报价/selection选型/objection异议/follow跟进/opening开场|
|content|string|false|none|话术内容|
|status|integer(int32)|false|none|状态：1 启用 / 0 停用|

<h2 id="tocS_ResultSpeechLibrary">ResultSpeechLibrary</h2>
<!-- backwards compatibility -->
<a id="schemaresultspeechlibrary"></a>
<a id="schema_ResultSpeechLibrary"></a>
<a id="tocSresultspeechlibrary"></a>
<a id="tocsresultspeechlibrary"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "title": "开场白-标准版",
    "category": "general",
    "content": "string",
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[SpeechLibrary](#schemaspeechlibrary)|false|none|话术库|

<h2 id="tocS_Role">Role</h2>
<!-- backwards compatibility -->
<a id="schemarole"></a>
<a id="schema_Role"></a>
<a id="tocSrole"></a>
<a id="tocsrole"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "code": "sales",
  "name": "string",
  "description": "string",
  "status": 1
}

```

角色（租户内）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|code|string|false|none|角色编码：admin/sales/supervisor 或自定义|
|name|string|false|none|角色名称|
|description|string|false|none|角色描述|
|status|integer(int32)|false|none|状态：1启用/0停用|

<h2 id="tocS_ResultRole">ResultRole</h2>
<!-- backwards compatibility -->
<a id="schemaresultrole"></a>
<a id="schema_ResultRole"></a>
<a id="tocSresultrole"></a>
<a id="tocsresultrole"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "code": "sales",
    "name": "string",
    "description": "string",
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Role](#schemarole)|false|none|角色（租户内）|

<h2 id="tocS_Product">Product</h2>
<!-- backwards compatibility -->
<a id="schemaproduct"></a>
<a id="schema_Product"></a>
<a id="tocSproduct"></a>
<a id="tocsproduct"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "categoryId": 0,
  "name": "企业版 CRM",
  "sku": "CRM-ENT-001",
  "spec": "标准版",
  "price": 1999,
  "params": "{\"容量\":\"100GB\",\"并发数\":\"500\"}",
  "attachments": "[{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
  "description": "string",
  "status": 1
}

```

产品资料

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|categoryId|integer(int64)|false|none|分类 ID|
|name|string|false|none|产品名称|
|sku|string|false|none|产品编码（SKU）|
|spec|string|false|none|规格型号|
|price|number|false|none|参考价格（元）|
|params|string|false|none|产品参数（JSONB 对象，结构化键值对，如 {"容量":"100GB","并发数":"500"}）|
|attachments|string|false|none|附件列表（JSONB 数组，元素含 name 名称/url 地址，如 [{"name":"选型表.xlsx","url":"/uploads/xxx.xlsx"}]）|
|description|string|false|none|产品描述|
|status|integer(int32)|false|none|状态：1 上架 / 0 下架|

<h2 id="tocS_ResultProduct">ResultProduct</h2>
<!-- backwards compatibility -->
<a id="schemaresultproduct"></a>
<a id="schema_ResultProduct"></a>
<a id="tocSresultproduct"></a>
<a id="tocsresultproduct"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "categoryId": 0,
    "name": "企业版 CRM",
    "sku": "CRM-ENT-001",
    "spec": "标准版",
    "price": 1999,
    "params": "{\"容量\":\"100GB\",\"并发数\":\"500\"}",
    "attachments": "[{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
    "description": "string",
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Product](#schemaproduct)|false|none|产品资料|

<h2 id="tocS_ProductCategory">ProductCategory</h2>
<!-- backwards compatibility -->
<a id="schemaproductcategory"></a>
<a id="schema_ProductCategory"></a>
<a id="tocSproductcategory"></a>
<a id="tocsproductcategory"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "CRM 产品线",
  "parentId": 0,
  "sort": 1,
  "status": 1
}

```

产品分类

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|name|string|false|none|分类名称|
|parentId|integer(int64)|false|none|父分类 ID（0 为顶级）|
|sort|integer(int32)|false|none|排序（小在前）|
|status|integer(int32)|false|none|状态：1 启用 / 0 停用|

<h2 id="tocS_ResultProductCategory">ResultProductCategory</h2>
<!-- backwards compatibility -->
<a id="schemaresultproductcategory"></a>
<a id="schema_ResultProductCategory"></a>
<a id="tocSresultproductcategory"></a>
<a id="tocsresultproductcategory"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "name": "CRM 产品线",
    "parentId": 0,
    "sort": 1,
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[ProductCategory](#schemaproductcategory)|false|none|产品分类|

<h2 id="tocS_Menu">Menu</h2>
<!-- backwards compatibility -->
<a id="schemamenu"></a>
<a id="schema_Menu"></a>
<a id="tocSmenu"></a>
<a id="tocsmenu"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "parentId": 0,
  "menuName": "string",
  "menuType": "menu",
  "path": "string",
  "component": "string",
  "perms": "user:add",
  "icon": "string",
  "sort": 0,
  "visible": 1,
  "status": 1,
  "children": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "parentId": 0,
      "menuName": "string",
      "menuType": "menu",
      "path": "string",
      "component": "string",
      "perms": "user:add",
      "icon": "string",
      "sort": 0,
      "visible": 1,
      "status": 1,
      "children": []
    }
  ]
}

```

菜单/权限（平台级定义，租户通过角色-菜单关联获得）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|parentId|integer(int64)|false|none|父菜单 ID，顶级为 0|
|menuName|string|false|none|菜单名称|
|menuType|string|false|none|类型：dir目录/menu菜单/button按钮|
|path|string|false|none|前端路由路径|
|component|string|false|none|前端组件|
|perms|string|false|none|按钮权限码，如 user:add|
|icon|string|false|none|图标|
|sort|integer(int32)|false|none|排序号|
|visible|integer(int32)|false|none|是否显示：1显示/0隐藏|
|status|integer(int32)|false|none|状态：1启用/0停用|
|children|[[Menu](#schemamenu)]|false|none|子菜单（树形展示用，非表字段）|

<h2 id="tocS_ResultMenu">ResultMenu</h2>
<!-- backwards compatibility -->
<a id="schemaresultmenu"></a>
<a id="schema_ResultMenu"></a>
<a id="tocSresultmenu"></a>
<a id="tocsresultmenu"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "parentId": 0,
    "menuName": "string",
    "menuType": "menu",
    "path": "string",
    "component": "string",
    "perms": "user:add",
    "icon": "string",
    "sort": 0,
    "visible": 1,
    "status": 1,
    "children": [
      {}
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Menu](#schemamenu)|false|none|菜单/权限（平台级定义，租户通过角色-菜单关联获得）|

<h2 id="tocS_LeadAssignRule">LeadAssignRule</h2>
<!-- backwards compatibility -->
<a id="schemaleadassignrule"></a>
<a id="schema_LeadAssignRule"></a>
<a id="tocSleadassignrule"></a>
<a id="tocsleadassignrule"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "华东区域线索分配",
  "ruleType": "region",
  "matchValue": "华东",
  "targetUserId": 0,
  "targetGroupIds": "string",
  "sort": 1,
  "status": 1
}

```

分配规则信息

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|ruleName|string|false|none|规则名称|
|ruleType|string|false|none|规则类型：product按产品线/region按地域/round_robin轮询组|
|matchValue|string|false|none|匹配值：产品线或地域（product/region 规则用）|
|targetUserId|integer(int64)|false|none|指定销售 ID（product/region 规则用）|
|targetGroupIds|string|false|none|轮询组销售 ID 数组（round_robin 规则用，JSONB），存储结构示例：[1,2,3]|
|sort|integer(int32)|false|none|优先级（数值越小越先匹配）|
|status|integer(int32)|false|none|状态：1 启用 / 0 停用|

<h2 id="tocS_ResultLeadAssignRule">ResultLeadAssignRule</h2>
<!-- backwards compatibility -->
<a id="schemaresultleadassignrule"></a>
<a id="schema_ResultLeadAssignRule"></a>
<a id="tocSresultleadassignrule"></a>
<a id="tocsresultleadassignrule"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "ruleName": "华东区域线索分配",
    "ruleType": "region",
    "matchValue": "华东",
    "targetUserId": 0,
    "targetGroupIds": "string",
    "sort": 1,
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[LeadAssignRule](#schemaleadassignrule)|false|none|分配规则信息|

<h2 id="tocS_Document">Document</h2>
<!-- backwards compatibility -->
<a id="schemadocument"></a>
<a id="schema_Document"></a>
<a id="tocSdocument"></a>
<a id="tocsdocument"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "AI 外呼系统产品手册",
  "docType": "product_brochure",
  "fileUrl": "/uploads/xxx.pdf",
  "version": 1,
  "status": 0,
  "tags": "[\"AI\",\"外呼\"]"
}

```

资料文档

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|title|string|false|none|标题|
|docType|string|false|none|类型：product_brochure产品手册/case案例/whitepaper白皮书/selection_table选型表|
|fileUrl|string|false|none|文件地址（对象存储/本地）|
|version|integer(int32)|false|none|版本号|
|status|integer(int32)|false|none|状态：0 草稿 / 1 已发布|
|tags|string|false|none|标签（JSON 数组字符串）|

<h2 id="tocS_ResultDocument">ResultDocument</h2>
<!-- backwards compatibility -->
<a id="schemaresultdocument"></a>
<a id="schema_ResultDocument"></a>
<a id="tocSresultdocument"></a>
<a id="tocsresultdocument"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "title": "AI 外呼系统产品手册",
    "docType": "product_brochure",
    "fileUrl": "/uploads/xxx.pdf",
    "version": 1,
    "status": 0,
    "tags": "[\"AI\",\"外呼\"]"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Document](#schemadocument)|false|none|资料文档|

<h2 id="tocS_DictType">DictType</h2>
<!-- backwards compatibility -->
<a id="schemadicttype"></a>
<a id="schema_DictType"></a>
<a id="tocSdicttype"></a>
<a id="tocsdicttype"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "dictName": "线索状态",
  "status": 1,
  "remark": "string"
}

```

字典类型

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|dictType|string|false|none|字典类型编码|
|dictName|string|false|none|字典名称|
|status|integer(int32)|false|none|状态：1 启用 / 0 停用|
|remark|string|false|none|备注|

<h2 id="tocS_ResultDictType">ResultDictType</h2>
<!-- backwards compatibility -->
<a id="schemaresultdicttype"></a>
<a id="schema_ResultDictType"></a>
<a id="tocSresultdicttype"></a>
<a id="tocsresultdicttype"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "dictType": "lead_status",
    "dictName": "线索状态",
    "status": 1,
    "remark": "string"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[DictType](#schemadicttype)|false|none|字典类型|

<h2 id="tocS_DictData">DictData</h2>
<!-- backwards compatibility -->
<a id="schemadictdata"></a>
<a id="schema_DictData"></a>
<a id="tocSdictdata"></a>
<a id="tocsdictdata"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "label": "已分配",
  "value": "1",
  "sort": 0,
  "status": 1,
  "remark": "string"
}

```

字典数据

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|dictType|string|false|none|所属字典类型编码|
|label|string|false|none|显示文本|
|value|string|false|none|字典值|
|sort|integer(int32)|false|none|排序号（升序排列）|
|status|integer(int32)|false|none|状态：1 启用 / 0 停用|
|remark|string|false|none|备注|

<h2 id="tocS_ResultDictData">ResultDictData</h2>
<!-- backwards compatibility -->
<a id="schemaresultdictdata"></a>
<a id="schema_ResultDictData"></a>
<a id="tocSresultdictdata"></a>
<a id="tocsresultdictdata"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "dictType": "lead_status",
    "label": "已分配",
    "value": "1",
    "sort": 0,
    "status": 1,
    "remark": "string"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[DictData](#schemadictdata)|false|none|字典数据|

<h2 id="tocS_Customer">Customer</h2>
<!-- backwards compatibility -->
<a id="schemacustomer"></a>
<a id="schema_Customer"></a>
<a id="tocScustomer"></a>
<a id="tocscustomer"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某科技有限公司",
  "industry": "软件服务",
  "scale": "100-499人",
  "region": "上海",
  "orgStructure": "string",
  "source": "public_data",
  "enrichmentStatus": 0,
  "stage": "potential",
  "intentLevel": 3,
  "score": 80
}

```

客户信息（name 必填）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|name|string|false|none|公司名称|
|industry|string|false|none|行业|
|scale|string|false|none|规模|
|region|string|false|none|地区|
|orgStructure|string|false|none|人员架构（授权/公开数据，JSONB），存储结构示例：{"executives":[{"name":"姓名","title":"职位","phone":"手机号","email":"邮箱"}],"departments":["部门名"]}，实际字段以第三方返回为准|
|source|string|false|none|enrichment 来源：public_data公开数据/customer_provided客户提供|
|enrichmentStatus|integer(int32)|false|none|数据补全状态：0 未补全 / 1 已补全|
|stage|string|false|none|客户阶段：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失|
|intentLevel|integer(int32)|false|none|意向等级：0-5|
|score|integer(int32)|false|none|综合评分：0-100|

<h2 id="tocS_ResultCustomer">ResultCustomer</h2>
<!-- backwards compatibility -->
<a id="schemaresultcustomer"></a>
<a id="schema_ResultCustomer"></a>
<a id="tocSresultcustomer"></a>
<a id="tocsresultcustomer"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "name": "某某科技有限公司",
    "industry": "软件服务",
    "scale": "100-499人",
    "region": "上海",
    "orgStructure": "string",
    "source": "public_data",
    "enrichmentStatus": 0,
    "stage": "potential",
    "intentLevel": 3,
    "score": 80
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Customer](#schemacustomer)|false|none|客户信息（name 必填）|

<h2 id="tocS_Config">Config</h2>
<!-- backwards compatibility -->
<a id="schemaconfig"></a>
<a id="schema_Config"></a>
<a id="tocSconfig"></a>
<a id="tocsconfig"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "configKey": "system.siteName",
  "configValue": "AI 销售线索系统",
  "configName": "站点名称",
  "configType": 2,
  "remark": "string"
}

```

系统参数配置

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|configKey|string|false|none|参数键|
|configValue|string|false|none|参数值|
|configName|string|false|none|参数名称|
|configType|integer(int32)|false|none|类型：1 内置 / 2 自定义|
|remark|string|false|none|备注|

<h2 id="tocS_ResultConfig">ResultConfig</h2>
<!-- backwards compatibility -->
<a id="schemaresultconfig"></a>
<a id="schema_ResultConfig"></a>
<a id="tocSresultconfig"></a>
<a id="tocsresultconfig"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "configKey": "system.siteName",
    "configValue": "AI 销售线索系统",
    "configName": "站点名称",
    "configType": 2,
    "remark": "string"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Config](#schemaconfig)|false|none|系统参数配置|

<h2 id="tocS_Competitor">Competitor</h2>
<!-- backwards compatibility -->
<a id="schemacompetitor"></a>
<a id="schema_Competitor"></a>
<a id="tocScompetitor"></a>
<a id="tocscompetitor"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "某某云 CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"功能全面\",\"价格低\"]",
  "weaknesses": "[\"实施复杂\",\"售后差\"]",
  "defenseTactics": "[{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]",
  "status": 1
}

```

竞品主体档案

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|name|string|false|none|竞品名称|
|category|string|false|none|竞品品类|
|officialUrl|string|false|none|官网地址|
|description|string|false|none|主体描述|
|strengths|string|false|none|优势列表（JSONB 数组，如 ["功能全面","价格低"]）|
|weaknesses|string|false|none|劣势列表（JSONB 数组，如 ["实施复杂","售后差"]）|
|defenseTactics|string|false|none|攻防话术列表（JSONB 数组，元素含 scenario 场景/tactic 话术，如 [{"scenario":"比价格","tactic":"强调总拥有成本"}]）|
|status|integer(int32)|false|none|状态：1 启用 / 0 停用|

<h2 id="tocS_ResultCompetitor">ResultCompetitor</h2>
<!-- backwards compatibility -->
<a id="schemaresultcompetitor"></a>
<a id="schema_ResultCompetitor"></a>
<a id="tocSresultcompetitor"></a>
<a id="tocsresultcompetitor"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "name": "某某云 CRM",
    "category": "CRM",
    "officialUrl": "https://www.example.com",
    "description": "string",
    "strengths": "[\"功能全面\",\"价格低\"]",
    "weaknesses": "[\"实施复杂\",\"售后差\"]",
    "defenseTactics": "[{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]",
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Competitor](#schemacompetitor)|false|none|竞品主体档案|

<h2 id="tocS_CompetitorProduct">CompetitorProduct</h2>
<!-- backwards compatibility -->
<a id="schemacompetitorproduct"></a>
<a id="schema_CompetitorProduct"></a>
<a id="tocScompetitorproduct"></a>
<a id="tocscompetitorproduct"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "competitorId": 0,
  "productName": "竞品云版",
  "spec": "旗舰版",
  "price": 2999,
  "params": "{\"并发数\":\"300\",\"存储\":\"50GB\"}"
}

```

竞品产品参数

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|competitorId|integer(int64)|false|none|竞品 ID|
|productName|string|false|none|竞品产品名称|
|spec|string|false|none|规格型号|
|price|number|false|none|参考价格（元）|
|params|string|false|none|竞品产品参数（JSONB 对象，结构化键值对，如 {"并发数":"300","存储":"50GB"}）|

<h2 id="tocS_ResultCompetitorProduct">ResultCompetitorProduct</h2>
<!-- backwards compatibility -->
<a id="schemaresultcompetitorproduct"></a>
<a id="schema_ResultCompetitorProduct"></a>
<a id="tocSresultcompetitorproduct"></a>
<a id="tocsresultcompetitorproduct"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "competitorId": 0,
    "productName": "竞品云版",
    "spec": "旗舰版",
    "price": 2999,
    "params": "{\"并发数\":\"300\",\"存储\":\"50GB\"}"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[CompetitorProduct](#schemacompetitorproduct)|false|none|竞品产品参数|

<h2 id="tocS_ChannelQrCode">ChannelQrCode</h2>
<!-- backwards compatibility -->
<a id="schemachannelqrcode"></a>
<a id="schema_ChannelQrCode"></a>
<a id="tocSchannelqrcode"></a>
<a id="tocschannelqrcode"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "name": "抖音引流活码",
  "channelAccountId": 1,
  "scene": "1:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1,
  "scanCount": 0,
  "convertedCount": 0
}

```

渠道活码（扫码引流/渠道来源标记/引流归因）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|name|string|false|none|活码名称|
|channelAccountId|integer(int64)|false|none|引流目标渠道账号 ID|
|scene|string|false|none|渠道来源标记（扫码事件携带，用于线索归因），不填默认取 渠道ID:账号ID|
|qrUrl|string|false|none|扫码跳转落地地址|
|status|integer(int32)|false|none|状态：1启用/0停用|
|scanCount|integer(int32)|false|none|扫码次数|
|convertedCount|integer(int32)|false|none|转化数（扫码后转化为线索数，与客户身份归一联动）|

<h2 id="tocS_ResultChannelQrCode">ResultChannelQrCode</h2>
<!-- backwards compatibility -->
<a id="schemaresultchannelqrcode"></a>
<a id="schema_ResultChannelQrCode"></a>
<a id="tocSresultchannelqrcode"></a>
<a id="tocsresultchannelqrcode"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 1,
    "name": "抖音引流活码",
    "channelAccountId": 1,
    "scene": "1:1",
    "qrUrl": "https://example.com/landing?from=qr",
    "status": 1,
    "scanCount": 0,
    "convertedCount": 0
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[ChannelQrCode](#schemachannelqrcode)|false|none|渠道活码（扫码引流/渠道来源标记/引流归因）|

<h2 id="tocS_ChannelAccount">ChannelAccount</h2>
<!-- backwards compatibility -->
<a id="schemachannelaccount"></a>
<a id="schema_ChannelAccount"></a>
<a id="tocSchannelaccount"></a>
<a id="tocschannelaccount"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelId": 1,
  "accountName": "品牌官方号",
  "externalId": "douyin_open_id_xxx",
  "authConfig": "string",
  "healthStatus": 1,
  "riskLevel": 0
}

```

渠道账号（企业号/矩阵号）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|channelId|integer(int64)|false|none|渠道 ID（对应 channel.id）|
|accountName|string|false|none|账号名称|
|externalId|string|false|none|渠道侧账号 ID|
|authConfig|string|false|none|授权凭证（加密存储，JSONB），结构：{"accessToken":"访问令牌","refreshToken":"刷新令牌","expireAt":"过期时间epoch毫秒"}，抖音另含 appId/secret、企业微信另含 corpId/corpSecret，按渠道可扩展|
|healthStatus|integer(int32)|false|none|账号健康状态：1正常/2受限/3封禁|
|riskLevel|integer(int32)|false|none|风控等级：0低/1中/2高|

<h2 id="tocS_ResultChannelAccount">ResultChannelAccount</h2>
<!-- backwards compatibility -->
<a id="schemaresultchannelaccount"></a>
<a id="schema_ResultChannelAccount"></a>
<a id="tocSresultchannelaccount"></a>
<a id="tocsresultchannelaccount"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 1,
    "channelId": 1,
    "accountName": "品牌官方号",
    "externalId": "douyin_open_id_xxx",
    "authConfig": "string",
    "healthStatus": 1,
    "riskLevel": 0
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[ChannelAccount](#schemachannelaccount)|false|none|渠道账号（企业号/矩阵号）|

<h2 id="tocS_TenantCreateRequest">TenantCreateRequest</h2>
<!-- backwards compatibility -->
<a id="schematenantcreaterequest"></a>
<a id="schema_TenantCreateRequest"></a>
<a id="tocStenantcreaterequest"></a>
<a id="tocstenantcreaterequest"></a>

```json
{
  "name": "string",
  "planCode": "starter",
  "seatCount": 0,
  "aiQuotaMonth": 0,
  "expireAt": "2019-08-24T14:15:22Z",
  "contactName": "string",
  "contactMobile": "string",
  "status": 1,
  "adminMobile": "string",
  "adminPassword": "string"
}

```

创建租户请求（含初始化管理员账号）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|true|none|企业名称|
|planCode|string|false|none|套餐编码：starter/pro/enterprise，为空默认 starter|
|seatCount|integer(int32)|false|none|坐席数上限，为空按套餐默认|
|aiQuotaMonth|integer(int64)|false|none|月度 AI 调用额度，为空按套餐默认|
|expireAt|string(date-time)|false|none|到期时间，为空表示长期有效|
|contactName|string|false|none|平台对接联系人|
|contactMobile|string|false|none|平台对接联系电话|
|status|integer(int32)|false|none|状态：1启用/0停用，为空默认启用|
|adminMobile|string|true|none|初始化管理员手机号（登录账号）|
|adminPassword|string|false|none|初始化管理员密码，为空使用默认密码|

<h2 id="tocS_ResultInteger">ResultInteger</h2>
<!-- backwards compatibility -->
<a id="schemaresultinteger"></a>
<a id="schema_ResultInteger"></a>
<a id="tocSresultinteger"></a>
<a id="tocsresultinteger"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 0
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|integer(int32)|false|none|业务数据|

<h2 id="tocS_Lead">Lead</h2>
<!-- backwards compatibility -->
<a id="schemalead"></a>
<a id="schema_Lead"></a>
<a id="tocSlead"></a>
<a id="tocslead"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "customerId": 0,
  "contactId": 0,
  "sourceChannelId": 0,
  "sourceContentId": "string",
  "sourceType": "comment",
  "intent": "quote",
  "status": "new",
  "score": 60,
  "ownerId": 0,
  "slaDeadline": "2019-08-24T14:15:22Z",
  "extra": "string"
}

```

线索信息

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|customerId|integer(int64)|false|none|关联客户公司 ID（可空）|
|contactId|integer(int64)|false|none|关联联系人 ID（可空）|
|sourceChannelId|integer(int64)|false|none|来源渠道账号 ID|
|sourceContentId|string|false|none|来源内容/视频/广告 ID|
|sourceType|string|false|none|来源类型：comment评论/dm私信/form表单/click点击|
|intent|string|false|none|意向：quote报价/sample样品/selection选型/other其他|
|status|string|false|none|状态：new新线索/assigned已分配/contacting跟进中/effective有效/quoted已报价/opportunity商机/lost流失|
|score|integer(int32)|false|none|线索评分：0-100|
|ownerId|integer(int64)|false|none|归属坐席 ID|
|slaDeadline|string(date-time)|false|none|响应 SLA 截止时间|
|extra|string|false|none|抽取关键字段（JSONB），存储结构示例：{"scenario":"场景","quantity":"数量","budget":"预算","delivery":"交期"}|

<h2 id="tocS_ResultLead">ResultLead</h2>
<!-- backwards compatibility -->
<a id="schemaresultlead"></a>
<a id="schema_ResultLead"></a>
<a id="tocSresultlead"></a>
<a id="tocsresultlead"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "customerId": 0,
    "contactId": 0,
    "sourceChannelId": 0,
    "sourceContentId": "string",
    "sourceType": "comment",
    "intent": "quote",
    "status": "new",
    "score": 60,
    "ownerId": 0,
    "slaDeadline": "2019-08-24T14:15:22Z",
    "extra": "string"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Lead](#schemalead)|false|none|线索信息|

<h2 id="tocS_ResultLong">ResultLong</h2>
<!-- backwards compatibility -->
<a id="schemaresultlong"></a>
<a id="schema_ResultLong"></a>
<a id="tocSresultlong"></a>
<a id="tocsresultlong"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 0
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|integer(int64)|false|none|业务数据|

<h2 id="tocS_BindRequest">BindRequest</h2>
<!-- backwards compatibility -->
<a id="schemabindrequest"></a>
<a id="schema_BindRequest"></a>
<a id="tocSbindrequest"></a>
<a id="tocsbindrequest"></a>

```json
{
  "tenantId": 0,
  "identityType": "string",
  "identityValue": "string",
  "entityId": 0,
  "source": "string"
}

```

绑定请求体

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tenantId|integer(int64)|false|none|租户 ID（为空默认当前租户）|
|identityType|string|false|none|身份类型：mobile手机号/email邮箱/social社媒ID/wecom企微ID/whatsapp/domain企业域名|
|identityValue|string|false|none|身份值（如手机号、邮箱地址、社媒/企微/WhatsApp ID、企业域名）|
|entityId|integer(int64)|false|none|目标线索 ID|
|source|string|false|none|绑定来源标识（如 manual手动）|

<h2 id="tocS_FollowUp">FollowUp</h2>
<!-- backwards compatibility -->
<a id="schemafollowup"></a>
<a id="schema_FollowUp"></a>
<a id="tocSfollowup"></a>
<a id="tocsfollowup"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "leadId": 0,
  "customerId": 0,
  "userId": 0,
  "content": "客户对报价方案有意向，约下周面谈",
  "method": "wechat",
  "nextTime": "2019-08-24T14:15:22Z"
}

```

跟进记录信息（method 取值：phone电话/wechat微信/visit拜访/other其他）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|leadId|integer(int64)|false|none|关联线索 ID（可空）|
|customerId|integer(int64)|false|none|关联客户 ID（可空）|
|userId|integer(int64)|false|none|跟进人 ID|
|content|string|false|none|跟进内容|
|method|string|false|none|跟进方式：phone电话/wechat微信/visit拜访/other其他|
|nextTime|string(date-time)|false|none|下次跟进时间|

<h2 id="tocS_ResultFollowUp">ResultFollowUp</h2>
<!-- backwards compatibility -->
<a id="schemaresultfollowup"></a>
<a id="schema_ResultFollowUp"></a>
<a id="tocSresultfollowup"></a>
<a id="tocsresultfollowup"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "leadId": 0,
    "customerId": 0,
    "userId": 0,
    "content": "客户对报价方案有意向，约下周面谈",
    "method": "wechat",
    "nextTime": "2019-08-24T14:15:22Z"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[FollowUp](#schemafollowup)|false|none|跟进记录信息（method 取值：phone电话/wechat微信/visit拜访/other其他）|

<h2 id="tocS_FileUploadVO">FileUploadVO</h2>
<!-- backwards compatibility -->
<a id="schemafileuploadvo"></a>
<a id="schema_FileUploadVO"></a>
<a id="tocSfileuploadvo"></a>
<a id="tocsfileuploadvo"></a>

```json
{
  "id": 0,
  "fileName": "string",
  "fileSize": 0,
  "contentType": "image/png",
  "url": "/api/files/1"
}

```

文件上传结果

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|文件记录 ID|
|fileName|string|false|none|原始文件名|
|fileSize|integer(int64)|false|none|文件大小（字节）|
|contentType|string|false|none|内容类型|
|url|string|false|none|访问地址|

<h2 id="tocS_ResultFileUploadVO">ResultFileUploadVO</h2>
<!-- backwards compatibility -->
<a id="schemaresultfileuploadvo"></a>
<a id="schema_ResultFileUploadVO"></a>
<a id="tocSresultfileuploadvo"></a>
<a id="tocsresultfileuploadvo"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 0,
    "fileName": "string",
    "fileSize": 0,
    "contentType": "image/png",
    "url": "/api/files/1"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[FileUploadVO](#schemafileuploadvo)|false|none|文件上传结果|

<h2 id="tocS_ResultString">ResultString</h2>
<!-- backwards compatibility -->
<a id="schemaresultstring"></a>
<a id="schema_ResultString"></a>
<a id="tocSresultstring"></a>
<a id="tocsresultstring"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "string"
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|string|false|none|业务数据|

<h2 id="tocS_Conversation">Conversation</h2>
<!-- backwards compatibility -->
<a id="schemaconversation"></a>
<a id="schema_Conversation"></a>
<a id="tocSconversation"></a>
<a id="tocsconversation"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelAccountId": 1,
  "contactId": 1,
  "leadId": 1,
  "conversationType": "wecom_chat",
  "status": "active",
  "assignedTo": 8,
  "lastMessageAt": "2019-08-24T14:15:22Z"
}

```

会话（承接阵地：企微/WhatsApp/私信）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|channelAccountId|integer(int64)|false|none|承接渠道账号 ID|
|contactId|integer(int64)|false|none|联系人 ID|
|leadId|integer(int64)|false|none|线索 ID|
|conversationType|string|false|none|会话类型：dm直接私信/wecom_chat企微聊天/whatsapp|
|status|string|false|none|状态：active进行中/transferred已转人工/closed已关闭/archived已归档|
|assignedTo|integer(int64)|false|none|当前人工处理人 ID|
|lastMessageAt|string(date-time)|false|none|最后消息时间|

<h2 id="tocS_ResultConversation">ResultConversation</h2>
<!-- backwards compatibility -->
<a id="schemaresultconversation"></a>
<a id="schema_ResultConversation"></a>
<a id="tocSresultconversation"></a>
<a id="tocsresultconversation"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 1,
    "channelAccountId": 1,
    "contactId": 1,
    "leadId": 1,
    "conversationType": "wecom_chat",
    "status": "active",
    "assignedTo": 8,
    "lastMessageAt": "2019-08-24T14:15:22Z"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Conversation](#schemaconversation)|false|none|会话（承接阵地：企微/WhatsApp/私信）|

<h2 id="tocS_ResultTransferPackage">ResultTransferPackage</h2>
<!-- backwards compatibility -->
<a id="schemaresulttransferpackage"></a>
<a id="schema_ResultTransferPackage"></a>
<a id="tocSresulttransferpackage"></a>
<a id="tocsresulttransferpackage"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "conversationId": 100,
    "tenantId": 1,
    "leadId": 50,
    "conversationType": "wecom_chat",
    "operatorId": 8,
    "summary": "string",
    "summarySource": "ai",
    "intent": "quote",
    "confidence": 0.82,
    "evidence": "string",
    "missingFields": [
      "qty",
      "budget"
    ],
    "recommendedReply": "string",
    "nextSteps": [
      "string"
    ],
    "transferredAt": "2019-08-24T14:15:22Z"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[TransferPackage](#schematransferpackage)|false|none|转人工交接包：坐席接手时看到的会话上下文（对话摘要/意向判定/缺失字段/推荐回复/下一步动作）|

<h2 id="tocS_TransferPackage">TransferPackage</h2>
<!-- backwards compatibility -->
<a id="schematransferpackage"></a>
<a id="schema_TransferPackage"></a>
<a id="tocStransferpackage"></a>
<a id="tocstransferpackage"></a>

```json
{
  "conversationId": 100,
  "tenantId": 1,
  "leadId": 50,
  "conversationType": "wecom_chat",
  "operatorId": 8,
  "summary": "string",
  "summarySource": "ai",
  "intent": "quote",
  "confidence": 0.82,
  "evidence": "string",
  "missingFields": [
    "qty",
    "budget"
  ],
  "recommendedReply": "string",
  "nextSteps": [
    "string"
  ],
  "transferredAt": "2019-08-24T14:15:22Z"
}

```

转人工交接包：坐席接手时看到的会话上下文（对话摘要/意向判定/缺失字段/推荐回复/下一步动作）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|conversationId|integer(int64)|false|none|会话 ID|
|tenantId|integer(int64)|false|none|租户 ID|
|leadId|integer(int64)|false|none|线索 ID|
|conversationType|string|false|none|会话类型：dm直接私信/wecom_chat企微聊天/whatsapp|
|operatorId|integer(int64)|false|none|接手坐席 ID|
|summary|string|false|none|对话摘要（AI summary，失败时降级提示）|
|summarySource|string|false|none|摘要来源：ai/fallback|
|intent|string|false|none|意向：quote报价/sample样品/selection选型/other其他|
|confidence|number|false|none|意向置信度|
|evidence|string|false|none|判定依据片段|
|missingFields|[string]|false|none|缺失字段：scene场景/qty数量/budget预算/lead_time交期/model型号 中未收集到的|
|recommendedReply|string|false|none|推荐回复话术（按意向生成）|
|nextSteps|[string]|false|none|下一步动作清单|
|transferredAt|string(date-time)|false|none|转人工时间|

<h2 id="tocS_Message">Message</h2>
<!-- backwards compatibility -->
<a id="schemamessage"></a>
<a id="schema_Message"></a>
<a id="tocSmessage"></a>
<a id="tocsmessage"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "conversationId": 100,
  "senderType": "customer",
  "content": "string",
  "msgType": "text",
  "attachments": "[]",
  "aiGenerated": false,
  "quotedDocIds": "[]",
  "raw": "{}"
}

```

消息

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|conversationId|integer(int64)|false|none|会话 ID|
|senderType|string|false|none|发送方：customer客户/ai机器人/human人工/system系统|
|content|string|false|none|消息内容|
|msgType|string|false|none|消息类型：text文本/image图片/file文件/card卡片|
|attachments|string|false|none|附件 ID 列表（JSONB 字符串）|
|aiGenerated|boolean|false|none|是否 AI 生成|
|quotedDocIds|string|false|none|引用知识文档 ID（溯源，JSONB 字符串）|
|raw|string|false|none|原始消息（JSONB 字符串）|

<h2 id="tocS_ResultMessage">ResultMessage</h2>
<!-- backwards compatibility -->
<a id="schemaresultmessage"></a>
<a id="schema_ResultMessage"></a>
<a id="tocSresultmessage"></a>
<a id="tocsresultmessage"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 1,
    "conversationId": 100,
    "senderType": "customer",
    "content": "string",
    "msgType": "text",
    "attachments": "[]",
    "aiGenerated": false,
    "quotedDocIds": "[]",
    "raw": "{}"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Message](#schemamessage)|false|none|消息|

<h2 id="tocS_AiReply">AiReply</h2>
<!-- backwards compatibility -->
<a id="schemaaireply"></a>
<a id="schema_AiReply"></a>
<a id="tocSaireply"></a>
<a id="tocsaireply"></a>

```json
{
  "content": "string",
  "intent": "quote",
  "confidence": 0.82,
  "shouldTransfer": true
}

```

AI 回复结果

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|string|false|none|回复内容|
|intent|string|false|none|意向：quote报价/sample样品/selection选型/other其他|
|confidence|number|false|none|置信度（0-1）|
|shouldTransfer|boolean|false|none|是否建议转人工|

<h2 id="tocS_ResultAiReply">ResultAiReply</h2>
<!-- backwards compatibility -->
<a id="schemaresultaireply"></a>
<a id="schema_ResultAiReply"></a>
<a id="tocSresultaireply"></a>
<a id="tocsresultaireply"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": "string",
    "intent": "quote",
    "confidence": 0.82,
    "shouldTransfer": true
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[AiReply](#schemaaireply)|false|none|AI 回复结果|

<h2 id="tocS_ChannelEvent">ChannelEvent</h2>
<!-- backwards compatibility -->
<a id="schemachannelevent"></a>
<a id="schema_ChannelEvent"></a>
<a id="tocSchannelevent"></a>
<a id="tocschannelevent"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelAccountId": 1,
  "eventType": "dm",
  "externalEventId": "evt_20260803_001",
  "externalUserId": "openid_xxx",
  "rawPayload": "string",
  "mapped": 0
}

```

渠道原始事件（三渠道统一入口，幂等去重）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|channelAccountId|integer(int64)|false|none|渠道账号 ID（对应 channel_account.id）|
|eventType|string|false|none|事件类型：comment评论/dm私信/form表单/click点击/lead线索（活码扫码内部事件为 qr，不入此接口）|
|externalEventId|string|false|none|渠道事件唯一 ID（去重键，与 tenantId+channelAccountId 组合幂等）|
|externalUserId|string|false|none|渠道侧用户 ID|
|rawPayload|string|false|none|原始事件数据（JSONB），存储渠道回调的完整原始 JSON，字段随渠道与事件类型不同|
|mapped|integer(int32)|false|none|是否已映射落线索：0未处理/1已映射|

<h2 id="tocS_ResultBoolean">ResultBoolean</h2>
<!-- backwards compatibility -->
<a id="schemaresultboolean"></a>
<a id="schema_ResultBoolean"></a>
<a id="tocSresultboolean"></a>
<a id="tocsresultboolean"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|boolean|false|none|业务数据|

<h2 id="tocS_LoginRequest">LoginRequest</h2>
<!-- backwards compatibility -->
<a id="schemaloginrequest"></a>
<a id="schema_LoginRequest"></a>
<a id="tocSloginrequest"></a>
<a id="tocsloginrequest"></a>

```json
{
  "tenantId": 0,
  "mobile": "13800138000",
  "password": "string"
}

```

登录请求

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tenantId|integer(int64)|true|none|租户 ID|
|mobile|string|true|none|手机号（登录账号）|
|password|string|true|none|登录密码（明文）|

<h2 id="tocS_LoginResponse">LoginResponse</h2>
<!-- backwards compatibility -->
<a id="schemaloginresponse"></a>
<a id="schema_LoginResponse"></a>
<a id="tocSloginresponse"></a>
<a id="tocsloginresponse"></a>

```json
{
  "token": "string",
  "userId": 0,
  "tenantId": 0,
  "roleCode": "string",
  "roles": [
    "string"
  ],
  "perms": [
    "string"
  ],
  "name": "string"
}

```

登录响应

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|token|string|false|none|JWT 令牌|
|userId|integer(int64)|false|none|用户 ID|
|tenantId|integer(int64)|false|none|租户 ID|
|roleCode|string|false|none|主角色码（角色集合第一个，兼容旧前端）：sales/supervisor/admin|
|roles|[string]|false|none|角色码集合：sales/supervisor/admin|
|perms|[string]|false|none|按钮权限码集合，如 user:add|
|name|string|false|none|姓名|

<h2 id="tocS_ResultLoginResponse">ResultLoginResponse</h2>
<!-- backwards compatibility -->
<a id="schemaresultloginresponse"></a>
<a id="schema_ResultLoginResponse"></a>
<a id="tocSresultloginresponse"></a>
<a id="tocsresultloginresponse"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "string",
    "userId": 0,
    "tenantId": 0,
    "roleCode": "string",
    "roles": [
      "string"
    ],
    "perms": [
      "string"
    ],
    "name": "string"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[LoginResponse](#schemaloginresponse)|false|none|登录响应|

<h2 id="tocS_ReplySuggestion">ReplySuggestion</h2>
<!-- backwards compatibility -->
<a id="schemareplysuggestion"></a>
<a id="schema_ReplySuggestion"></a>
<a id="tocSreplysuggestion"></a>
<a id="tocsreplysuggestion"></a>

```json
{
  "intent": "quote",
  "replies": [
    "string"
  ],
  "missingFields": [
    "string"
  ]
}

```

推荐话术

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|intent|string|false|none|意向：quote报价/sample样品/selection选型/other其他|
|replies|[string]|false|none|推荐回复话术列表|
|missingFields|[string]|false|none|缺失字段：scene场景/qty数量/budget预算/lead_time交期/model型号|

<h2 id="tocS_ResultReplySuggestion">ResultReplySuggestion</h2>
<!-- backwards compatibility -->
<a id="schemaresultreplysuggestion"></a>
<a id="schema_ResultReplySuggestion"></a>
<a id="tocSresultreplysuggestion"></a>
<a id="tocsresultreplysuggestion"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "intent": "quote",
    "replies": [
      "string"
    ],
    "missingFields": [
      "string"
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[ReplySuggestion](#schemareplysuggestion)|false|none|推荐话术|

<h2 id="tocS_CustomerProfile">CustomerProfile</h2>
<!-- backwards compatibility -->
<a id="schemacustomerprofile"></a>
<a id="schema_CustomerProfile"></a>
<a id="tocScustomerprofile"></a>
<a id="tocscustomerprofile"></a>

```json
{
  "leadId": 0,
  "leadStatus": "string",
  "intent": "string",
  "score": 0,
  "customerId": 0,
  "customerName": "string",
  "industry": "string",
  "region": "string",
  "tags": [
    "string"
  ],
  "latestIntent": "quote",
  "intentConfidence": 0.82
}

```

客户画像

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|leadId|integer(int64)|false|none|线索 ID|
|leadStatus|string|false|none|线索状态：new新线索/assigned已分配/contacting跟进中/effective有效/quoted已报价/opportunity商机/lost流失|
|intent|string|false|none|线索意向：quote报价/sample样品/selection选型/other其他|
|score|integer(int32)|false|none|客户综合评分（0-100）|
|customerId|integer(int64)|false|none|客户 ID|
|customerName|string|false|none|客户名称|
|industry|string|false|none|行业|
|region|string|false|none|地区|
|tags|[string]|false|none|标签列表|
|latestIntent|string|false|none|最近意向：quote报价/sample样品/selection选型/other其他|
|intentConfidence|number|false|none|意向置信度（0-1）|

<h2 id="tocS_ResultCustomerProfile">ResultCustomerProfile</h2>
<!-- backwards compatibility -->
<a id="schemaresultcustomerprofile"></a>
<a id="schema_ResultCustomerProfile"></a>
<a id="tocSresultcustomerprofile"></a>
<a id="tocsresultcustomerprofile"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "leadId": 0,
    "leadStatus": "string",
    "intent": "string",
    "score": 0,
    "customerId": 0,
    "customerName": "string",
    "industry": "string",
    "region": "string",
    "tags": [
      "string"
    ],
    "latestIntent": "quote",
    "intentConfidence": 0.82
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[CustomerProfile](#schemacustomerprofile)|false|none|客户画像|

<h2 id="tocS_PageResultProduct">PageResultProduct</h2>
<!-- backwards compatibility -->
<a id="schemapageresultproduct"></a>
<a id="schema_PageResultProduct"></a>
<a id="tocSpageresultproduct"></a>
<a id="tocspageresultproduct"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "categoryId": 0,
      "name": "企业版 CRM",
      "sku": "CRM-ENT-001",
      "spec": "标准版",
      "price": 1999,
      "params": "{\"容量\":\"100GB\",\"并发数\":\"500\"}",
      "attachments": "[{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
      "description": "string",
      "status": 1
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Product](#schemaproduct)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultProduct">ResultPageResultProduct</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultproduct"></a>
<a id="schema_ResultPageResultProduct"></a>
<a id="tocSresultpageresultproduct"></a>
<a id="tocsresultpageresultproduct"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "categoryId": 0,
        "name": "企业版 CRM",
        "sku": "CRM-ENT-001",
        "spec": "标准版",
        "price": 1999,
        "params": "{\"容量\":\"100GB\",\"并发数\":\"500\"}",
        "attachments": "[{\"name\":\"选型表.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
        "description": "string",
        "status": 1
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultProduct](#schemapageresultproduct)|false|none|分页返回结构|

<h2 id="tocS_ConversationBrief">ConversationBrief</h2>
<!-- backwards compatibility -->
<a id="schemaconversationbrief"></a>
<a id="schema_ConversationBrief"></a>
<a id="tocSconversationbrief"></a>
<a id="tocsconversationbrief"></a>

```json
{
  "conversationId": 0,
  "conversationType": "wecom_chat",
  "status": "string",
  "lastMessageAt": "2019-08-24T14:15:22Z",
  "lastMessage": "string",
  "lastSender": "string"
}

```

会话摘要

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|conversationId|integer(int64)|false|none|会话 ID|
|conversationType|string|false|none|会话类型：dm直接私信/wecom_chat企微聊天/whatsapp|
|status|string|false|none|会话状态：active进行中/transferred已转人工/closed已关闭/archived已归档|
|lastMessageAt|string(date-time)|false|none|最近一条消息时间|
|lastMessage|string|false|none|最近一条消息内容|
|lastSender|string|false|none|最近一条消息发送方：customer客户/ai机器人/human人工/system系统|

<h2 id="tocS_ResultListConversationBrief">ResultListConversationBrief</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistconversationbrief"></a>
<a id="schema_ResultListConversationBrief"></a>
<a id="tocSresultlistconversationbrief"></a>
<a id="tocsresultlistconversationbrief"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "conversationId": 0,
      "conversationType": "wecom_chat",
      "status": "string",
      "lastMessageAt": "2019-08-24T14:15:22Z",
      "lastMessage": "string",
      "lastSender": "string"
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[ConversationBrief](#schemaconversationbrief)]|false|none|业务数据|

<h2 id="tocS_PageResultUser">PageResultUser</h2>
<!-- backwards compatibility -->
<a id="schemapageresultuser"></a>
<a id="schema_PageResultUser"></a>
<a id="tocSpageresultuser"></a>
<a id="tocspageresultuser"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "name": "string",
      "mobile": "string",
      "email": "string",
      "roleCode": "sales",
      "status": 1,
      "lastActiveAt": "2019-08-24T14:15:22Z"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[User](#schemauser)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultUser">ResultPageResultUser</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultuser"></a>
<a id="schema_ResultPageResultUser"></a>
<a id="tocSresultpageresultuser"></a>
<a id="tocsresultpageresultuser"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "name": "string",
        "mobile": "string",
        "email": "string",
        "roleCode": "sales",
        "status": 1,
        "lastActiveAt": "2019-08-24T14:15:22Z"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultUser](#schemapageresultuser)|false|none|分页返回结构|

<h2 id="tocS_ResultListLong">ResultListLong</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistlong"></a>
<a id="schema_ResultListLong"></a>
<a id="tocSresultlistlong"></a>
<a id="tocsresultlistlong"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    0
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[integer]|false|none|业务数据|

<h2 id="tocS_PageResultTenant">PageResultTenant</h2>
<!-- backwards compatibility -->
<a id="schemapageresulttenant"></a>
<a id="schema_PageResultTenant"></a>
<a id="tocSpageresulttenant"></a>
<a id="tocspageresulttenant"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "name": "string",
      "planCode": "starter",
      "seatCount": 0,
      "aiQuotaMonth": 0,
      "expireAt": "2019-08-24T14:15:22Z",
      "contactName": "string",
      "contactMobile": "string",
      "status": 1
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Tenant](#schematenant)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultTenant">ResultPageResultTenant</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresulttenant"></a>
<a id="schema_ResultPageResultTenant"></a>
<a id="tocSresultpageresulttenant"></a>
<a id="tocsresultpageresulttenant"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "name": "string",
        "planCode": "starter",
        "seatCount": 0,
        "aiQuotaMonth": 0,
        "expireAt": "2019-08-24T14:15:22Z",
        "contactName": "string",
        "contactMobile": "string",
        "status": 1
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultTenant](#schemapageresulttenant)|false|none|分页返回结构|

<h2 id="tocS_PageResultCustomerTag">PageResultCustomerTag</h2>
<!-- backwards compatibility -->
<a id="schemapageresultcustomertag"></a>
<a id="schema_PageResultCustomerTag"></a>
<a id="tocSpageresultcustomertag"></a>
<a id="tocspageresultcustomertag"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "name": "高意向客户",
      "color": "#FF5733",
      "remark": "string",
      "status": 1
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[CustomerTag](#schemacustomertag)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultCustomerTag">ResultPageResultCustomerTag</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultcustomertag"></a>
<a id="schema_ResultPageResultCustomerTag"></a>
<a id="tocSresultpageresultcustomertag"></a>
<a id="tocsresultpageresultcustomertag"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "name": "高意向客户",
        "color": "#FF5733",
        "remark": "string",
        "status": 1
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultCustomerTag](#schemapageresultcustomertag)|false|none|分页返回结构|

<h2 id="tocS_PageResultCustomer">PageResultCustomer</h2>
<!-- backwards compatibility -->
<a id="schemapageresultcustomer"></a>
<a id="schema_PageResultCustomer"></a>
<a id="tocSpageresultcustomer"></a>
<a id="tocspageresultcustomer"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "name": "某某科技有限公司",
      "industry": "软件服务",
      "scale": "100-499人",
      "region": "上海",
      "orgStructure": "string",
      "source": "public_data",
      "enrichmentStatus": 0,
      "stage": "potential",
      "intentLevel": 3,
      "score": 80
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Customer](#schemacustomer)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultCustomer">ResultPageResultCustomer</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultcustomer"></a>
<a id="schema_ResultPageResultCustomer"></a>
<a id="tocSresultpageresultcustomer"></a>
<a id="tocsresultpageresultcustomer"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "name": "某某科技有限公司",
        "industry": "软件服务",
        "scale": "100-499人",
        "region": "上海",
        "orgStructure": "string",
        "source": "public_data",
        "enrichmentStatus": 0,
        "stage": "potential",
        "intentLevel": 3,
        "score": 80
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultCustomer](#schemapageresultcustomer)|false|none|分页返回结构|

<h2 id="tocS_PageResultCustomerTagRule">PageResultCustomerTagRule</h2>
<!-- backwards compatibility -->
<a id="schemapageresultcustomertagrule"></a>
<a id="schema_PageResultCustomerTagRule"></a>
<a id="tocSpageresultcustomertagrule"></a>
<a id="tocspageresultcustomertagrule"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "ruleName": "评分高于80自动打标",
      "tagId": 0,
      "conditionField": "score",
      "conditionOp": "gte",
      "conditionValue": "80",
      "status": 1
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[CustomerTagRule](#schemacustomertagrule)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultCustomerTagRule">ResultPageResultCustomerTagRule</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultcustomertagrule"></a>
<a id="schema_ResultPageResultCustomerTagRule"></a>
<a id="tocSresultpageresultcustomertagrule"></a>
<a id="tocsresultpageresultcustomertagrule"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "ruleName": "评分高于80自动打标",
        "tagId": 0,
        "conditionField": "score",
        "conditionOp": "gte",
        "conditionValue": "80",
        "status": 1
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultCustomerTagRule](#schemapageresultcustomertagrule)|false|none|分页返回结构|

<h2 id="tocS_PageResultSpeechLibrary">PageResultSpeechLibrary</h2>
<!-- backwards compatibility -->
<a id="schemapageresultspeechlibrary"></a>
<a id="schema_PageResultSpeechLibrary"></a>
<a id="tocSpageresultspeechlibrary"></a>
<a id="tocspageresultspeechlibrary"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "title": "开场白-标准版",
      "category": "general",
      "content": "string",
      "status": 1
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[SpeechLibrary](#schemaspeechlibrary)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultSpeechLibrary">ResultPageResultSpeechLibrary</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultspeechlibrary"></a>
<a id="schema_ResultPageResultSpeechLibrary"></a>
<a id="tocSresultpageresultspeechlibrary"></a>
<a id="tocsresultpageresultspeechlibrary"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "title": "开场白-标准版",
        "category": "general",
        "content": "string",
        "status": 1
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultSpeechLibrary](#schemapageresultspeechlibrary)|false|none|分页返回结构|

<h2 id="tocS_PageResultRole">PageResultRole</h2>
<!-- backwards compatibility -->
<a id="schemapageresultrole"></a>
<a id="schema_PageResultRole"></a>
<a id="tocSpageresultrole"></a>
<a id="tocspageresultrole"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "code": "sales",
      "name": "string",
      "description": "string",
      "status": 1
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Role](#schemarole)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultRole">ResultPageResultRole</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultrole"></a>
<a id="schema_ResultPageResultRole"></a>
<a id="tocSresultpageresultrole"></a>
<a id="tocsresultpageresultrole"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "code": "sales",
        "name": "string",
        "description": "string",
        "status": 1
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultRole](#schemapageresultrole)|false|none|分页返回结构|

<h2 id="tocS_ResultListRole">ResultListRole</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistrole"></a>
<a id="schema_ResultListRole"></a>
<a id="tocSresultlistrole"></a>
<a id="tocsresultlistrole"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "code": "sales",
      "name": "string",
      "description": "string",
      "status": 1
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[Role](#schemarole)]|false|none|业务数据|

<h2 id="tocS_ResultListProductCategory">ResultListProductCategory</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistproductcategory"></a>
<a id="schema_ResultListProductCategory"></a>
<a id="tocSresultlistproductcategory"></a>
<a id="tocsresultlistproductcategory"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "name": "CRM 产品线",
      "parentId": 0,
      "sort": 1,
      "status": 1
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[ProductCategory](#schemaproductcategory)]|false|none|业务数据|

<h2 id="tocS_Plan">Plan</h2>
<!-- backwards compatibility -->
<a id="schemaplan"></a>
<a id="schema_Plan"></a>
<a id="tocSplan"></a>
<a id="tocsplan"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "code": "starter",
  "name": "string",
  "seatCount": 0,
  "aiQuotaMonth": 0,
  "monthlyPrice": 0,
  "description": "string",
  "status": 1
}

```

套餐定义（平台级，租户订阅的版本配置）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|code|string|false|none|套餐编码：starter/pro/enterprise|
|name|string|false|none|套餐名称|
|seatCount|integer(int32)|false|none|坐席数上限|
|aiQuotaMonth|integer(int64)|false|none|月度 AI 调用额度|
|monthlyPrice|number|false|none|月单价（元）|
|description|string|false|none|套餐描述|
|status|integer(int32)|false|none|状态：1上架/0下架|

<h2 id="tocS_ResultListPlan">ResultListPlan</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistplan"></a>
<a id="schema_ResultListPlan"></a>
<a id="tocSresultlistplan"></a>
<a id="tocsresultlistplan"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "code": "starter",
      "name": "string",
      "seatCount": 0,
      "aiQuotaMonth": 0,
      "monthlyPrice": 0,
      "description": "string",
      "status": 1
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[Plan](#schemaplan)]|false|none|业务数据|

<h2 id="tocS_ResultPlan">ResultPlan</h2>
<!-- backwards compatibility -->
<a id="schemaresultplan"></a>
<a id="schema_ResultPlan"></a>
<a id="tocSresultplan"></a>
<a id="tocsresultplan"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "code": "starter",
    "name": "string",
    "seatCount": 0,
    "aiQuotaMonth": 0,
    "monthlyPrice": 0,
    "description": "string",
    "status": 1
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Plan](#schemaplan)|false|none|套餐定义（平台级，租户订阅的版本配置）|

<h2 id="tocS_ResultListMenu">ResultListMenu</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistmenu"></a>
<a id="schema_ResultListMenu"></a>
<a id="tocSresultlistmenu"></a>
<a id="tocsresultlistmenu"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "parentId": 0,
      "menuName": "string",
      "menuType": "menu",
      "path": "string",
      "component": "string",
      "perms": "user:add",
      "icon": "string",
      "sort": 0,
      "visible": 1,
      "status": 1,
      "children": [
        {}
      ]
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[Menu](#schemamenu)]|false|none|业务数据|

<h2 id="tocS_OperLog">OperLog</h2>
<!-- backwards compatibility -->
<a id="schemaoperlog"></a>
<a id="schema_OperLog"></a>
<a id="tocSoperlog"></a>
<a id="tocsoperlog"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "userId": 0,
  "userName": "张三",
  "module": "字典管理",
  "operation": "创建字典类型",
  "method": "string",
  "requestUrl": "/api/dicts/types",
  "httpMethod": "POST",
  "requestParams": "string",
  "result": 1,
  "errorMsg": "string",
  "ip": "127.0.0.1",
  "durationMs": 15
}

```

操作日志

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|userId|integer(int64)|false|none|操作人用户 ID|
|userName|string|false|none|操作人姓名|
|module|string|false|none|业务模块|
|operation|string|false|none|操作内容|
|method|string|false|none|执行方法（全限定名）|
|requestUrl|string|false|none|请求地址|
|httpMethod|string|false|none|请求方式|
|requestParams|string|false|none|请求参数（JSON，敏感字段已脱敏）|
|result|integer(int32)|false|none|结果：1 成功 / 0 失败|
|errorMsg|string|false|none|异常信息|
|ip|string|false|none|来源 IP|
|durationMs|integer(int64)|false|none|耗时（毫秒）|

<h2 id="tocS_PageResultOperLog">PageResultOperLog</h2>
<!-- backwards compatibility -->
<a id="schemapageresultoperlog"></a>
<a id="schema_PageResultOperLog"></a>
<a id="tocSpageresultoperlog"></a>
<a id="tocspageresultoperlog"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "userId": 0,
      "userName": "张三",
      "module": "字典管理",
      "operation": "创建字典类型",
      "method": "string",
      "requestUrl": "/api/dicts/types",
      "httpMethod": "POST",
      "requestParams": "string",
      "result": 1,
      "errorMsg": "string",
      "ip": "127.0.0.1",
      "durationMs": 15
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[OperLog](#schemaoperlog)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultOperLog">ResultPageResultOperLog</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultoperlog"></a>
<a id="schema_ResultPageResultOperLog"></a>
<a id="tocSresultpageresultoperlog"></a>
<a id="tocsresultpageresultoperlog"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "userId": 0,
        "userName": "张三",
        "module": "字典管理",
        "operation": "创建字典类型",
        "method": "string",
        "requestUrl": "/api/dicts/types",
        "httpMethod": "POST",
        "requestParams": "string",
        "result": 1,
        "errorMsg": "string",
        "ip": "127.0.0.1",
        "durationMs": 15
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultOperLog](#schemapageresultoperlog)|false|none|分页返回结构|

<h2 id="tocS_LoginLog">LoginLog</h2>
<!-- backwards compatibility -->
<a id="schemaloginlog"></a>
<a id="schema_LoginLog"></a>
<a id="tocSloginlog"></a>
<a id="tocsloginlog"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "userId": 0,
  "mobile": "13800000000",
  "ip": "127.0.0.1",
  "userAgent": "string",
  "status": 1,
  "message": "登录成功"
}

```

登录日志

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID（登录失败时可能为空）|
|userId|integer(int64)|false|none|用户 ID（登录失败时为空）|
|mobile|string|false|none|登录手机号|
|ip|string|false|none|来源 IP|
|userAgent|string|false|none|User-Agent|
|status|integer(int32)|false|none|结果：1 成功 / 0 失败|
|message|string|false|none|结果描述|

<h2 id="tocS_PageResultLoginLog">PageResultLoginLog</h2>
<!-- backwards compatibility -->
<a id="schemapageresultloginlog"></a>
<a id="schema_PageResultLoginLog"></a>
<a id="tocSpageresultloginlog"></a>
<a id="tocspageresultloginlog"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "userId": 0,
      "mobile": "13800000000",
      "ip": "127.0.0.1",
      "userAgent": "string",
      "status": 1,
      "message": "登录成功"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[LoginLog](#schemaloginlog)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultLoginLog">ResultPageResultLoginLog</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultloginlog"></a>
<a id="schema_ResultPageResultLoginLog"></a>
<a id="tocSresultpageresultloginlog"></a>
<a id="tocsresultpageresultloginlog"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "userId": 0,
        "mobile": "13800000000",
        "ip": "127.0.0.1",
        "userAgent": "string",
        "status": 1,
        "message": "登录成功"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultLoginLog](#schemapageresultloginlog)|false|none|分页返回结构|

<h2 id="tocS_PageResultLead">PageResultLead</h2>
<!-- backwards compatibility -->
<a id="schemapageresultlead"></a>
<a id="schema_PageResultLead"></a>
<a id="tocSpageresultlead"></a>
<a id="tocspageresultlead"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "customerId": 0,
      "contactId": 0,
      "sourceChannelId": 0,
      "sourceContentId": "string",
      "sourceType": "comment",
      "intent": "quote",
      "status": "new",
      "score": 60,
      "ownerId": 0,
      "slaDeadline": "2019-08-24T14:15:22Z",
      "extra": "string"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Lead](#schemalead)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultLead">ResultPageResultLead</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultlead"></a>
<a id="schema_ResultPageResultLead"></a>
<a id="tocSresultpageresultlead"></a>
<a id="tocsresultpageresultlead"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "customerId": 0,
        "contactId": 0,
        "sourceChannelId": 0,
        "sourceContentId": "string",
        "sourceType": "comment",
        "intent": "quote",
        "status": "new",
        "score": 60,
        "ownerId": 0,
        "slaDeadline": "2019-08-24T14:15:22Z",
        "extra": "string"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultLead](#schemapageresultlead)|false|none|分页返回结构|

<h2 id="tocS_PageResultLeadAssignRule">PageResultLeadAssignRule</h2>
<!-- backwards compatibility -->
<a id="schemapageresultleadassignrule"></a>
<a id="schema_PageResultLeadAssignRule"></a>
<a id="tocSpageresultleadassignrule"></a>
<a id="tocspageresultleadassignrule"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "ruleName": "华东区域线索分配",
      "ruleType": "region",
      "matchValue": "华东",
      "targetUserId": 0,
      "targetGroupIds": "string",
      "sort": 1,
      "status": 1
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[LeadAssignRule](#schemaleadassignrule)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultLeadAssignRule">ResultPageResultLeadAssignRule</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultleadassignrule"></a>
<a id="schema_ResultPageResultLeadAssignRule"></a>
<a id="tocSresultpageresultleadassignrule"></a>
<a id="tocsresultpageresultleadassignrule"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "ruleName": "华东区域线索分配",
        "ruleType": "region",
        "matchValue": "华东",
        "targetUserId": 0,
        "targetGroupIds": "string",
        "sort": 1,
        "status": 1
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultLeadAssignRule](#schemapageresultleadassignrule)|false|none|分页返回结构|

<h2 id="tocS_JobInfo">JobInfo</h2>
<!-- backwards compatibility -->
<a id="schemajobinfo"></a>
<a id="schema_JobInfo"></a>
<a id="tocSjobinfo"></a>
<a id="tocsjobinfo"></a>

```json
{
  "code": "string",
  "name": "string",
  "cron": "string",
  "running": true
}

```

业务数据

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|name|string|false|none|none|
|cron|string|false|none|none|
|running|boolean|false|none|none|

<h2 id="tocS_ResultListJobInfo">ResultListJobInfo</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistjobinfo"></a>
<a id="schema_ResultListJobInfo"></a>
<a id="tocSresultlistjobinfo"></a>
<a id="tocsresultlistjobinfo"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "code": "string",
      "name": "string",
      "cron": "string",
      "running": true
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[JobInfo](#schemajobinfo)]|false|none|业务数据|

<h2 id="tocS_JobLog">JobLog</h2>
<!-- backwards compatibility -->
<a id="schemajoblog"></a>
<a id="schema_JobLog"></a>
<a id="tocSjoblog"></a>
<a id="tocsjoblog"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "jobCode": "channelTokenRefresh",
  "jobName": "渠道令牌刷新",
  "triggerType": "cron",
  "result": 1,
  "errorMsg": "string",
  "durationMs": 120
}

```

定时任务执行记录

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|jobCode|string|false|none|任务编码|
|jobName|string|false|none|任务名称|
|triggerType|string|false|none|触发方式：cron 定时 / manual 手动|
|result|integer(int32)|false|none|结果：1 成功 / 0 失败|
|errorMsg|string|false|none|异常信息|
|durationMs|integer(int64)|false|none|耗时（毫秒）|

<h2 id="tocS_PageResultJobLog">PageResultJobLog</h2>
<!-- backwards compatibility -->
<a id="schemapageresultjoblog"></a>
<a id="schema_PageResultJobLog"></a>
<a id="tocSpageresultjoblog"></a>
<a id="tocspageresultjoblog"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "jobCode": "channelTokenRefresh",
      "jobName": "渠道令牌刷新",
      "triggerType": "cron",
      "result": 1,
      "errorMsg": "string",
      "durationMs": 120
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[JobLog](#schemajoblog)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultJobLog">ResultPageResultJobLog</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultjoblog"></a>
<a id="schema_ResultPageResultJobLog"></a>
<a id="tocSresultpageresultjoblog"></a>
<a id="tocsresultpageresultjoblog"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "jobCode": "channelTokenRefresh",
        "jobName": "渠道令牌刷新",
        "triggerType": "cron",
        "result": 1,
        "errorMsg": "string",
        "durationMs": 120
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultJobLog](#schemapageresultjoblog)|false|none|分页返回结构|

<h2 id="tocS_IdentityMapping">IdentityMapping</h2>
<!-- backwards compatibility -->
<a id="schemaidentitymapping"></a>
<a id="schema_IdentityMapping"></a>
<a id="tocSidentitymapping"></a>
<a id="tocsidentitymapping"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "identityId": 0,
  "entityType": "lead",
  "entityId": 0,
  "confidence": 1,
  "source": "manual"
}

```

身份到实体映射（identity → lead/contact/customer）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|identityId|integer(int64)|false|none|身份 ID|
|entityType|string|false|none|实体类型：lead线索/contact联系人/customer客户|
|entityId|integer(int64)|false|none|实体 ID|
|confidence|number|false|none|匹配置信度（0-1，默认 1）|
|source|string|false|none|来源（如 manual手动/auto自动匹配）|

<h2 id="tocS_ResultListIdentityMapping">ResultListIdentityMapping</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistidentitymapping"></a>
<a id="schema_ResultListIdentityMapping"></a>
<a id="tocSresultlistidentitymapping"></a>
<a id="tocsresultlistidentitymapping"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "identityId": 0,
      "entityType": "lead",
      "entityId": 0,
      "confidence": 1,
      "source": "manual"
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[IdentityMapping](#schemaidentitymapping)]|false|none|业务数据|

<h2 id="tocS_Identity">Identity</h2>
<!-- backwards compatibility -->
<a id="schemaidentity"></a>
<a id="schema_Identity"></a>
<a id="tocSidentity"></a>
<a id="tocsidentity"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "identityType": "mobile",
  "identityValue": "13800138000",
  "status": 1,
  "consent": 0,
  "consentTime": "2019-08-24T14:15:22Z"
}

```

身份（跨渠道合并核心）：手机号/邮箱/社媒ID/企微ID/WhatsApp/企业域名

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|identityType|string|false|none|身份类型：mobile手机号/email邮箱/social社媒ID/wecom企微ID/whatsapp/domain企业域名|
|identityValue|string|false|none|身份值（如手机号、邮箱地址、社媒/企微/WhatsApp ID、企业域名）|
|status|integer(int32)|false|none|状态：1 有效 / 0 失效|
|consent|integer(int32)|false|none|同意记录：0 未同意 / 1 已同意（合规）|
|consentTime|string(date-time)|false|none|同意时间|

<h2 id="tocS_ResultIdentity">ResultIdentity</h2>
<!-- backwards compatibility -->
<a id="schemaresultidentity"></a>
<a id="schema_ResultIdentity"></a>
<a id="tocSresultidentity"></a>
<a id="tocsresultidentity"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "identityType": "mobile",
    "identityValue": "13800138000",
    "status": 1,
    "consent": 0,
    "consentTime": "2019-08-24T14:15:22Z"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[Identity](#schemaidentity)|false|none|身份（跨渠道合并核心）：手机号/邮箱/社媒ID/企微ID/WhatsApp/企业域名|

<h2 id="tocS_PageResultFollowUp">PageResultFollowUp</h2>
<!-- backwards compatibility -->
<a id="schemapageresultfollowup"></a>
<a id="schema_PageResultFollowUp"></a>
<a id="tocSpageresultfollowup"></a>
<a id="tocspageresultfollowup"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "leadId": 0,
      "customerId": 0,
      "userId": 0,
      "content": "客户对报价方案有意向，约下周面谈",
      "method": "wechat",
      "nextTime": "2019-08-24T14:15:22Z"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[FollowUp](#schemafollowup)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultFollowUp">ResultPageResultFollowUp</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultfollowup"></a>
<a id="schema_ResultPageResultFollowUp"></a>
<a id="tocSresultpageresultfollowup"></a>
<a id="tocsresultpageresultfollowup"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "leadId": 0,
        "customerId": 0,
        "userId": 0,
        "content": "客户对报价方案有意向，约下周面谈",
        "method": "wechat",
        "nextTime": "2019-08-24T14:15:22Z"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultFollowUp](#schemapageresultfollowup)|false|none|分页返回结构|

<h2 id="tocS_FileRecord">FileRecord</h2>
<!-- backwards compatibility -->
<a id="schemafilerecord"></a>
<a id="schema_FileRecord"></a>
<a id="tocSfilerecord"></a>
<a id="tocsfilerecord"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "fileName": "产品手册.pdf",
  "filePath": "string",
  "fileSize": 0,
  "contentType": "application/pdf",
  "storageType": "local"
}

```

文件记录

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|tenantId|integer(int64)|false|none|租户 ID|
|fileName|string|false|none|原始文件名|
|filePath|string|false|none|存储 key（本地为相对路径，MinIO 为对象名）|
|fileSize|integer(int64)|false|none|文件大小（字节）|
|contentType|string|false|none|内容类型|
|storageType|string|false|none|存储类型：local 本地存储 / minio 对象存储|

<h2 id="tocS_PageResultFileRecord">PageResultFileRecord</h2>
<!-- backwards compatibility -->
<a id="schemapageresultfilerecord"></a>
<a id="schema_PageResultFileRecord"></a>
<a id="tocSpageresultfilerecord"></a>
<a id="tocspageresultfilerecord"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "fileName": "产品手册.pdf",
      "filePath": "string",
      "fileSize": 0,
      "contentType": "application/pdf",
      "storageType": "local"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[FileRecord](#schemafilerecord)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultFileRecord">ResultPageResultFileRecord</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultfilerecord"></a>
<a id="schema_ResultPageResultFileRecord"></a>
<a id="tocSresultpageresultfilerecord"></a>
<a id="tocsresultpageresultfilerecord"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "fileName": "产品手册.pdf",
        "filePath": "string",
        "fileSize": 0,
        "contentType": "application/pdf",
        "storageType": "local"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultFileRecord](#schemapageresultfilerecord)|false|none|分页返回结构|

<h2 id="tocS_PageResultDocument">PageResultDocument</h2>
<!-- backwards compatibility -->
<a id="schemapageresultdocument"></a>
<a id="schema_PageResultDocument"></a>
<a id="tocSpageresultdocument"></a>
<a id="tocspageresultdocument"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "title": "AI 外呼系统产品手册",
      "docType": "product_brochure",
      "fileUrl": "/uploads/xxx.pdf",
      "version": 1,
      "status": 0,
      "tags": "[\"AI\",\"外呼\"]"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Document](#schemadocument)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultDocument">ResultPageResultDocument</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultdocument"></a>
<a id="schema_ResultPageResultDocument"></a>
<a id="tocSresultpageresultdocument"></a>
<a id="tocsresultpageresultdocument"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "title": "AI 外呼系统产品手册",
        "docType": "product_brochure",
        "fileUrl": "/uploads/xxx.pdf",
        "version": 1,
        "status": 0,
        "tags": "[\"AI\",\"外呼\"]"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultDocument](#schemapageresultdocument)|false|none|分页返回结构|

<h2 id="tocS_PageResultDictType">PageResultDictType</h2>
<!-- backwards compatibility -->
<a id="schemapageresultdicttype"></a>
<a id="schema_PageResultDictType"></a>
<a id="tocSpageresultdicttype"></a>
<a id="tocspageresultdicttype"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "dictType": "lead_status",
      "dictName": "线索状态",
      "status": 1,
      "remark": "string"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[DictType](#schemadicttype)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultDictType">ResultPageResultDictType</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultdicttype"></a>
<a id="schema_ResultPageResultDictType"></a>
<a id="tocSresultpageresultdicttype"></a>
<a id="tocsresultpageresultdicttype"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "dictType": "lead_status",
        "dictName": "线索状态",
        "status": 1,
        "remark": "string"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultDictType](#schemapageresultdicttype)|false|none|分页返回结构|

<h2 id="tocS_ResultListDictType">ResultListDictType</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistdicttype"></a>
<a id="schema_ResultListDictType"></a>
<a id="tocSresultlistdicttype"></a>
<a id="tocsresultlistdicttype"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "dictType": "lead_status",
      "dictName": "线索状态",
      "status": 1,
      "remark": "string"
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[DictType](#schemadicttype)]|false|none|业务数据|

<h2 id="tocS_PageResultDictData">PageResultDictData</h2>
<!-- backwards compatibility -->
<a id="schemapageresultdictdata"></a>
<a id="schema_PageResultDictData"></a>
<a id="tocSpageresultdictdata"></a>
<a id="tocspageresultdictdata"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "dictType": "lead_status",
      "label": "已分配",
      "value": "1",
      "sort": 0,
      "status": 1,
      "remark": "string"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[DictData](#schemadictdata)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultDictData">ResultPageResultDictData</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultdictdata"></a>
<a id="schema_ResultPageResultDictData"></a>
<a id="tocSresultpageresultdictdata"></a>
<a id="tocsresultpageresultdictdata"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "dictType": "lead_status",
        "label": "已分配",
        "value": "1",
        "sort": 0,
        "status": 1,
        "remark": "string"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultDictData](#schemapageresultdictdata)|false|none|分页返回结构|

<h2 id="tocS_ResultListDictData">ResultListDictData</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistdictdata"></a>
<a id="schema_ResultListDictData"></a>
<a id="tocSresultlistdictdata"></a>
<a id="tocsresultlistdictdata"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "dictType": "lead_status",
      "label": "已分配",
      "value": "1",
      "sort": 0,
      "status": 1,
      "remark": "string"
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[DictData](#schemadictdata)]|false|none|业务数据|

<h2 id="tocS_DashboardTrend">DashboardTrend</h2>
<!-- backwards compatibility -->
<a id="schemadashboardtrend"></a>
<a id="schema_DashboardTrend"></a>
<a id="tocSdashboardtrend"></a>
<a id="tocsdashboardtrend"></a>

```json
{
  "date": "2026-08-03",
  "leadCount": 0,
  "conversationCount": 0,
  "aiResolvedCount": 0
}

```

看板每日趋势：线索量/会话量/AI 解决量按日聚合

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|date|string(date)|false|none|统计日期|
|leadCount|integer(int64)|false|none|新增线索数|
|conversationCount|integer(int64)|false|none|新增会话数|
|aiResolvedCount|integer(int64)|false|none|AI 解决会话数（当日会话减去转人工会话）|

<h2 id="tocS_ResultListDashboardTrend">ResultListDashboardTrend</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistdashboardtrend"></a>
<a id="schema_ResultListDashboardTrend"></a>
<a id="tocSresultlistdashboardtrend"></a>
<a id="tocsresultlistdashboardtrend"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "date": "2026-08-03",
      "leadCount": 0,
      "conversationCount": 0,
      "aiResolvedCount": 0
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[DashboardTrend](#schemadashboardtrend)]|false|none|业务数据|

<h2 id="tocS_ResultListSalesWorkloadStat">ResultListSalesWorkloadStat</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistsalesworkloadstat"></a>
<a id="schema_ResultListSalesWorkloadStat"></a>
<a id="tocSresultlistsalesworkloadstat"></a>
<a id="tocsresultlistsalesworkloadstat"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "userId": 0,
      "userName": "string",
      "assignedLeadCount": 0,
      "followUpCount": 0,
      "wonCount": 0,
      "conversationCount": 0
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[SalesWorkloadStat](#schemasalesworkloadstat)]|false|none|业务数据|

<h2 id="tocS_SalesWorkloadStat">SalesWorkloadStat</h2>
<!-- backwards compatibility -->
<a id="schemasalesworkloadstat"></a>
<a id="schema_SalesWorkloadStat"></a>
<a id="tocSsalesworkloadstat"></a>
<a id="tocssalesworkloadstat"></a>

```json
{
  "userId": 0,
  "userName": "string",
  "assignedLeadCount": 0,
  "followUpCount": 0,
  "wonCount": 0,
  "conversationCount": 0
}

```

看板销售工作量统计：按销售聚合分配线索/跟进/成交/会话量

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|userId|integer(int64)|false|none|销售用户 ID|
|userName|string|false|none|销售姓名|
|assignedLeadCount|integer(int64)|false|none|分配线索数|
|followUpCount|integer(int64)|false|none|跟进记录数|
|wonCount|integer(int64)|false|none|成交线索数（status=won）|
|conversationCount|integer(int64)|false|none|人工接待会话数|

<h2 id="tocS_DashboardOverview">DashboardOverview</h2>
<!-- backwards compatibility -->
<a id="schemadashboardoverview"></a>
<a id="schema_DashboardOverview"></a>
<a id="tocSdashboardoverview"></a>
<a id="tocsdashboardoverview"></a>

```json
{
  "tenantId": 0,
  "from": "2019-08-24T14:15:22Z",
  "to": "2019-08-24T14:15:22Z",
  "leadCount": 0,
  "conversationCount": 0,
  "transferCount": 0,
  "avgResponseSec": 0.1,
  "effectiveRate": 0.1,
  "intentDistribution": [
    {
      "intent": "quote",
      "count": 0
    }
  ]
}

```

看板总览：线索量/会话量/转人工数/响应时效/有效对话率/意向分布

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tenantId|integer(int64)|false|none|租户 ID|
|from|string(date-time)|false|none|统计起始时间|
|to|string(date-time)|false|none|统计截止时间|
|leadCount|integer(int64)|false|none|线索量|
|conversationCount|integer(int64)|false|none|新增会话数|
|transferCount|integer(int64)|false|none|转人工数|
|avgResponseSec|number(double)|false|none|平均响应时效（秒）：客户首条消息到 AI 首条回复|
|effectiveRate|number(double)|false|none|有效对话率（0-1）：≥3 条消息的会话占比|
|intentDistribution|[[IntentCount](#schemaintentcount)]|false|none|意向分布|

<h2 id="tocS_IntentCount">IntentCount</h2>
<!-- backwards compatibility -->
<a id="schemaintentcount"></a>
<a id="schema_IntentCount"></a>
<a id="tocSintentcount"></a>
<a id="tocsintentcount"></a>

```json
{
  "intent": "quote",
  "count": 0
}

```

意向分布明细

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|intent|string|false|none|意向：quote报价/sample样品/selection选型/other其他/unknown未知|
|count|integer(int64)|false|none|线索数|

<h2 id="tocS_ResultDashboardOverview">ResultDashboardOverview</h2>
<!-- backwards compatibility -->
<a id="schemaresultdashboardoverview"></a>
<a id="schema_ResultDashboardOverview"></a>
<a id="tocSresultdashboardoverview"></a>
<a id="tocsresultdashboardoverview"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "tenantId": 0,
    "from": "2019-08-24T14:15:22Z",
    "to": "2019-08-24T14:15:22Z",
    "leadCount": 0,
    "conversationCount": 0,
    "transferCount": 0,
    "avgResponseSec": 0.1,
    "effectiveRate": 0.1,
    "intentDistribution": [
      {
        "intent": "quote",
        "count": 0
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[DashboardOverview](#schemadashboardoverview)|false|none|看板总览：线索量/会话量/转人工数/响应时效/有效对话率/意向分布|

<h2 id="tocS_ChannelConversionStat">ChannelConversionStat</h2>
<!-- backwards compatibility -->
<a id="schemachannelconversionstat"></a>
<a id="schema_ChannelConversionStat"></a>
<a id="tocSchannelconversionstat"></a>
<a id="tocschannelconversionstat"></a>

```json
{
  "channelAccountId": 0,
  "accountName": "string",
  "eventCount": 0,
  "leadCount": 0,
  "conversionRate": 0.35
}

```

看板渠道转化数据：按渠道账号聚合事件数/线索数/转化率

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|channelAccountId|integer(int64)|false|none|渠道账号 ID|
|accountName|string|false|none|渠道账号名称|
|eventCount|integer(int64)|false|none|渠道事件数|
|leadCount|integer(int64)|false|none|映射线索数|
|conversionRate|number(double)|false|none|转化率（线索数/事件数，0-1）|

<h2 id="tocS_ResultListChannelConversionStat">ResultListChannelConversionStat</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistchannelconversionstat"></a>
<a id="schema_ResultListChannelConversionStat"></a>
<a id="tocSresultlistchannelconversionstat"></a>
<a id="tocsresultlistchannelconversionstat"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "channelAccountId": 0,
      "accountName": "string",
      "eventCount": 0,
      "leadCount": 0,
      "conversionRate": 0.35
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[ChannelConversionStat](#schemachannelconversionstat)]|false|none|业务数据|

<h2 id="tocS_PageResultConversation">PageResultConversation</h2>
<!-- backwards compatibility -->
<a id="schemapageresultconversation"></a>
<a id="schema_PageResultConversation"></a>
<a id="tocSpageresultconversation"></a>
<a id="tocspageresultconversation"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 1,
      "channelAccountId": 1,
      "contactId": 1,
      "leadId": 1,
      "conversationType": "wecom_chat",
      "status": "active",
      "assignedTo": 8,
      "lastMessageAt": "2019-08-24T14:15:22Z"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Conversation](#schemaconversation)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultConversation">ResultPageResultConversation</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultconversation"></a>
<a id="schema_ResultPageResultConversation"></a>
<a id="tocSresultpageresultconversation"></a>
<a id="tocsresultpageresultconversation"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 1,
        "channelAccountId": 1,
        "contactId": 1,
        "leadId": 1,
        "conversationType": "wecom_chat",
        "status": "active",
        "assignedTo": 8,
        "lastMessageAt": "2019-08-24T14:15:22Z"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultConversation](#schemapageresultconversation)|false|none|分页返回结构|

<h2 id="tocS_ResultTransferEvaluation">ResultTransferEvaluation</h2>
<!-- backwards compatibility -->
<a id="schemaresulttransferevaluation"></a>
<a id="schema_ResultTransferEvaluation"></a>
<a id="tocSresulttransferevaluation"></a>
<a id="tocsresulttransferevaluation"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "shouldTransfer": true,
    "intent": "selection",
    "confidence": 0.76,
    "reason": "string"
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[TransferEvaluation](#schematransferevaluation)|false|none|转人工评估结果|

<h2 id="tocS_TransferEvaluation">TransferEvaluation</h2>
<!-- backwards compatibility -->
<a id="schematransferevaluation"></a>
<a id="schema_TransferEvaluation"></a>
<a id="tocStransferevaluation"></a>
<a id="tocstransferevaluation"></a>

```json
{
  "shouldTransfer": true,
  "intent": "selection",
  "confidence": 0.76,
  "reason": "string"
}

```

转人工评估结果

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|shouldTransfer|boolean|false|none|是否建议转人工|
|intent|string|false|none|意向：quote报价/sample样品/selection选型/other其他|
|confidence|number|false|none|置信度（0-1）|
|reason|string|false|none|判定原因|

<h2 id="tocS_PageResultMessage">PageResultMessage</h2>
<!-- backwards compatibility -->
<a id="schemapageresultmessage"></a>
<a id="schema_PageResultMessage"></a>
<a id="tocSpageresultmessage"></a>
<a id="tocspageresultmessage"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 1,
      "conversationId": 100,
      "senderType": "customer",
      "content": "string",
      "msgType": "text",
      "attachments": "[]",
      "aiGenerated": false,
      "quotedDocIds": "[]",
      "raw": "{}"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Message](#schemamessage)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultMessage">ResultPageResultMessage</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultmessage"></a>
<a id="schema_ResultPageResultMessage"></a>
<a id="tocSresultpageresultmessage"></a>
<a id="tocsresultpageresultmessage"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 1,
        "conversationId": 100,
        "senderType": "customer",
        "content": "string",
        "msgType": "text",
        "attachments": "[]",
        "aiGenerated": false,
        "quotedDocIds": "[]",
        "raw": "{}"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultMessage](#schemapageresultmessage)|false|none|分页返回结构|

<h2 id="tocS_PageResultConfig">PageResultConfig</h2>
<!-- backwards compatibility -->
<a id="schemapageresultconfig"></a>
<a id="schema_PageResultConfig"></a>
<a id="tocSpageresultconfig"></a>
<a id="tocspageresultconfig"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "configKey": "system.siteName",
      "configValue": "AI 销售线索系统",
      "configName": "站点名称",
      "configType": 2,
      "remark": "string"
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Config](#schemaconfig)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultConfig">ResultPageResultConfig</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultconfig"></a>
<a id="schema_ResultPageResultConfig"></a>
<a id="tocSresultpageresultconfig"></a>
<a id="tocsresultpageresultconfig"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "configKey": "system.siteName",
        "configValue": "AI 销售线索系统",
        "configName": "站点名称",
        "configType": 2,
        "remark": "string"
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultConfig](#schemapageresultconfig)|false|none|分页返回结构|

<h2 id="tocS_PageResultCompetitor">PageResultCompetitor</h2>
<!-- backwards compatibility -->
<a id="schemapageresultcompetitor"></a>
<a id="schema_PageResultCompetitor"></a>
<a id="tocSpageresultcompetitor"></a>
<a id="tocspageresultcompetitor"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "name": "某某云 CRM",
      "category": "CRM",
      "officialUrl": "https://www.example.com",
      "description": "string",
      "strengths": "[\"功能全面\",\"价格低\"]",
      "weaknesses": "[\"实施复杂\",\"售后差\"]",
      "defenseTactics": "[{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]",
      "status": 1
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[Competitor](#schemacompetitor)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultCompetitor">ResultPageResultCompetitor</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultcompetitor"></a>
<a id="schema_ResultPageResultCompetitor"></a>
<a id="tocSresultpageresultcompetitor"></a>
<a id="tocsresultpageresultcompetitor"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 0,
        "name": "某某云 CRM",
        "category": "CRM",
        "officialUrl": "https://www.example.com",
        "description": "string",
        "strengths": "[\"功能全面\",\"价格低\"]",
        "weaknesses": "[\"实施复杂\",\"售后差\"]",
        "defenseTactics": "[{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]",
        "status": 1
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultCompetitor](#schemapageresultcompetitor)|false|none|分页返回结构|

<h2 id="tocS_ResultListCompetitorProduct">ResultListCompetitorProduct</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistcompetitorproduct"></a>
<a id="schema_ResultListCompetitorProduct"></a>
<a id="tocSresultlistcompetitorproduct"></a>
<a id="tocsresultlistcompetitorproduct"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "competitorId": 0,
      "productName": "竞品云版",
      "spec": "旗舰版",
      "price": 2999,
      "params": "{\"并发数\":\"300\",\"存储\":\"50GB\"}"
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[CompetitorProduct](#schemacompetitorproduct)]|false|none|业务数据|

<h2 id="tocS_Channel">Channel</h2>
<!-- backwards compatibility -->
<a id="schemachannel"></a>
<a id="schema_Channel"></a>
<a id="tocSchannel"></a>
<a id="tocschannel"></a>

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "code": "douyin",
  "name": "抖音",
  "type": "short_video",
  "configSchema": "string",
  "status": 1
}

```

渠道定义（平台级，抖音/视频号/TikTok/企业微信/WhatsApp）

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|主键 ID|
|createdAt|string(date-time)|false|none|创建时间|
|updatedAt|string(date-time)|false|none|更新时间|
|code|string|false|none|渠道编码：douyin抖音/video_channel视频号/tiktok TikTok/wecom企业微信/whatsapp WhatsApp|
|name|string|false|none|渠道名称|
|type|string|false|none|渠道类型：short_video短视频/social社交媒体/im即时通讯|
|configSchema|string|false|none|配置项 schema（JSONB），定义该渠道接入所需配置项（如 appId/secret 等键）的 JSON 结构|
|status|integer(int32)|false|none|状态：1启用/0停用|

<h2 id="tocS_ResultListChannel">ResultListChannel</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistchannel"></a>
<a id="schema_ResultListChannel"></a>
<a id="tocSresultlistchannel"></a>
<a id="tocsresultlistchannel"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "code": "douyin",
      "name": "抖音",
      "type": "short_video",
      "configSchema": "string",
      "status": 1
    }
  ]
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[[Channel](#schemachannel)]|false|none|业务数据|

<h2 id="tocS_PageResultChannelQrCode">PageResultChannelQrCode</h2>
<!-- backwards compatibility -->
<a id="schemapageresultchannelqrcode"></a>
<a id="schema_PageResultChannelQrCode"></a>
<a id="tocSpageresultchannelqrcode"></a>
<a id="tocspageresultchannelqrcode"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 1,
      "name": "抖音引流活码",
      "channelAccountId": 1,
      "scene": "1:1",
      "qrUrl": "https://example.com/landing?from=qr",
      "status": 1,
      "scanCount": 0,
      "convertedCount": 0
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[ChannelQrCode](#schemachannelqrcode)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultChannelQrCode">ResultPageResultChannelQrCode</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultchannelqrcode"></a>
<a id="schema_ResultPageResultChannelQrCode"></a>
<a id="tocSresultpageresultchannelqrcode"></a>
<a id="tocsresultpageresultchannelqrcode"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 1,
        "name": "抖音引流活码",
        "channelAccountId": 1,
        "scene": "1:1",
        "qrUrl": "https://example.com/landing?from=qr",
        "status": 1,
        "scanCount": 0,
        "convertedCount": 0
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultChannelQrCode](#schemapageresultchannelqrcode)|false|none|分页返回结构|

<h2 id="tocS_PageResultChannelAccount">PageResultChannelAccount</h2>
<!-- backwards compatibility -->
<a id="schemapageresultchannelaccount"></a>
<a id="schema_PageResultChannelAccount"></a>
<a id="tocSpageresultchannelaccount"></a>
<a id="tocspageresultchannelaccount"></a>

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "pages": 5,
  "records": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 1,
      "channelId": 1,
      "accountName": "品牌官方号",
      "externalId": "douyin_open_id_xxx",
      "authConfig": "string",
      "healthStatus": 1,
      "riskLevel": 0
    }
  ]
}

```

分页返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|当前页（从 1 开始）|
|size|integer(int64)|false|none|每页条数|
|total|integer(int64)|false|none|总条数|
|pages|integer(int64)|false|none|总页数|
|records|[[ChannelAccount](#schemachannelaccount)]|false|none|数据列表|

<h2 id="tocS_ResultPageResultChannelAccount">ResultPageResultChannelAccount</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultchannelaccount"></a>
<a id="schema_ResultPageResultChannelAccount"></a>
<a id="tocSresultpageresultchannelaccount"></a>
<a id="tocsresultpageresultchannelaccount"></a>

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "page": 1,
    "size": 20,
    "total": 100,
    "pages": 5,
    "records": [
      {
        "id": 1,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "tenantId": 1,
        "channelId": 1,
        "accountName": "品牌官方号",
        "externalId": "douyin_open_id_xxx",
        "authConfig": "string",
        "healthStatus": 1,
        "riskLevel": 0
      }
    ]
  }
}

```

统一 API 返回结构

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|业务码：200 成功，其余为失败（400 参数错误/401 未登录/403 无权限/404 不存在/500 系统错误，业务错误码 1000+）|
|message|string|false|none|提示信息|
|data|[PageResultChannelAccount](#schemapageresultchannelaccount)|false|none|分页返回结构|

