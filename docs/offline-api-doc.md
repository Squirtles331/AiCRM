---
title: "AiCRM æ¥å£ææ¡£ v0.1.0"
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

<h1 id="aicrm-">AiCRM æ¥å£ææ¡£ v0.1.0</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

AIè·å®¢éå®ç³»ç»ï¼AiCRMï¼æ ¸å¿ä¸å¡æ¥å£ææ¡£ã

ç»ä¸è¿åç»æï¼`{ code, message, data }`ï¼code=200 è¡¨ç¤ºæåï¼
åé¡µæ¥å£ data ä¸º `PageResult{ page, size, total, pages, records }`ï¼
éè¯¯ç ï¼400 åæ°éè¯¯ / 401 æªç»å½ / 403 æ æé / 404 èµæºä¸å­å¨ / 500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ è§åæ¥å£æè¿°ã

é´æï¼é¤ç»å½å¤çæ¥å£éæºå¸¦è¯·æ±å¤´ `Authorization: Bearer <token>`ï¼
å¤ç§æ·æ¥å£å»ºè®®åæ¶æºå¸¦è¯·æ±å¤´ `X-Tenant-Id`ã

Base URLs:

* <a href="http://localhost:8080">http://localhost:8080</a>

# Authentication

- HTTP Authentication, scheme: bearer ç»å½åè·åç JWT ä»¤çï¼æ ¼å¼ï¼Bearer <token>

<h1 id="aicrm---">åå¸­ç®¡ç</h1>

## æ¥è¯¢åå¸­è¯¦æ

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

éæé user:list

<h3 id="æ¥è¯¢åå¸­è¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åå¸­ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ¥è¯¢åå¸­è¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultUser](#schemaresultuser)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°åå¸­

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

éæé user:edit

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

<h3 id="æ´æ°åå¸­-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åå¸­ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[User](#schemauser)|true|none|

> Example responses

> 404 Response

<h3 id="æ´æ°åå¸­-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç¨æ·ä¸å­å¨ï¼ä¸å¡ç  1101ï¼|[ResultUser](#schemaresultuser)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤åå¸­ï¼é»è¾å é¤ï¼

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

éæé user:delete

<h3 id="å é¤åå¸­ï¼é»è¾å é¤ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åå¸­ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤åå¸­ï¼é»è¾å é¤ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç¨æ·ä¸å­å¨ï¼ä¸å¡ç  1101ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å¯ç¨/åç¨åå¸­

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

éæé user:edit

<h3 id="å¯ç¨/åç¨åå¸­-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åå¸­ ID|
|status|query|integer(int32)|true|ç¶æï¼1å¯ç¨/0åç¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å¯ç¨/åç¨åå¸­-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|status ä»æ¯æ 0/1|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç¨æ·ä¸å­å¨ï¼ä¸å¡ç  1101ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ¥è¯¢ç¨æ·å·²åéè§è² ID

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

éæé user:list

<h3 id="æ¥è¯¢ç¨æ·å·²åéè§è²-id-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åå¸­ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ¥è¯¢ç¨æ·å·²åéè§è²-id-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListLong](#schemaresultlistlong)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åéç¨æ·è§è²ï¼å¨éè¦çï¼

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

éæé user:edit

> Body parameter

```json
[
  0
]
```

<h3 id="åéç¨æ·è§è²ï¼å¨éè¦çï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åå¸­ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|array[integer]|true|none|

> Example responses

> 404 Response

<h3 id="åéç¨æ·è§è²ï¼å¨éè¦çï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç¨æ·ä¸å­å¨ï¼ä¸å¡ç  1101ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## éç½®å¯ç 

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

éæé user:reset-password

<h3 id="éç½®å¯ç -parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åå¸­ ID|
|newPassword|query|string|true|æ°å¯ç ï¼ææï¼|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="éç½®å¯ç -responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|æ°å¯ç é¿åº¦ä¸è½å°äº 6 ä½|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç¨æ·ä¸å­å¨ï¼ä¸å¡ç  1101ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åé¡µæ¥è¯¢åå¸­

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

éæé user:list

<h3 id="åé¡µæ¥è¯¢åå¸­-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|false|ç§æ· ID|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åé¡µæ¥è¯¢åå¸­-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultUser](#schemaresultpageresultuser)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºåå¸­

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

éæé user:add

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

<h3 id="åå»ºåå¸­-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[User](#schemauser)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºåå¸­-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|ç§æ· ID/ææºå·/å¯ç ä¸è½ä¸ºç©º|[ResultUser](#schemaresultuser)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">ç§æ·ç®¡ç</h1>

## ç§æ·è¯¦æ

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

éæé admin

<h3 id="ç§æ·è¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç§æ· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ç§æ·è¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultTenant](#schemaresulttenant)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°ç§æ·ï¼å«å¥é¤/å°ææ¶é´/èç³»äººç­ï¼

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

éæé admin

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

<h3 id="æ´æ°ç§æ·ï¼å«å¥é¤/å°ææ¶é´/èç³»äººç­ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç§æ· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Tenant](#schematenant)|true|none|

> Example responses

> 404 Response

<h3 id="æ´æ°ç§æ·ï¼å«å¥é¤/å°ææ¶é´/èç³»äººç­ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç§æ·ä¸å­å¨ï¼ä¸å¡ç  1001ï¼|[ResultTenant](#schemaresulttenant)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤ç§æ·ï¼é»è¾å é¤ï¼

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

éæé admin

<h3 id="å é¤ç§æ·ï¼é»è¾å é¤ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç§æ· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤ç§æ·ï¼é»è¾å é¤ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç§æ·ä¸å­å¨ï¼ä¸å¡ç  1001ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å¯ç¨/åç¨ç§æ·

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

éæé admin

<h3 id="å¯ç¨/åç¨ç§æ·-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç§æ· ID|
|status|query|integer(int32)|true|ç¶æï¼1å¯ç¨/0åç¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å¯ç¨/åç¨ç§æ·-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|status ä»æ¯æ 0ï¼åç¨ï¼/1ï¼å¯ç¨ï¼|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç§æ·ä¸å­å¨ï¼ä¸å¡ç  1001ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åé¡µæ¥è¯¢ç§æ·

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

éæé admin

<h3 id="åé¡µæ¥è¯¢ç§æ·-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼ä¼ä¸åç§°ï¼|
|planCode|query|string|false|å¥é¤ç¼ç ï¼starter/pro/enterprise|
|status|query|integer(int32)|false|ç¶æï¼1å¯ç¨/0åç¨|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åé¡µæ¥è¯¢ç§æ·-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultTenant](#schemaresultpageresulttenant)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºç§æ·ï¼èªå¨åå§åé»è®¤ç®¡çåè´¦å·ï¼

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

éæé admin

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

<h3 id="åå»ºç§æ·ï¼èªå¨åå§åé»è®¤ç®¡çåè´¦å·ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[TenantCreateRequest](#schematenantcreaterequest)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºç§æ·ï¼èªå¨åå§åé»è®¤ç®¡çåè´¦å·ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|ç®¡çåææºå·ä¸è½ä¸ºç©º|[ResultTenant](#schemaresulttenant)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|å¥é¤å·²ä¸æ¶ï¼ä¸å¡ç  1005ï¼|[ResultTenant](#schemaresulttenant)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">å®¢æ·æ ç­¾</h1>

## æ´æ°æ ç­¾

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
  "name": "é«æåå®¢æ·",
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

æéï¼tag:editãæ ID æ´æ°æ ç­¾åç§°/é¢è²/å¤æ³¨ã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "é«æåå®¢æ·",
  "color": "#FF5733",
  "remark": "string",
  "status": 1
}
```

<h3 id="æ´æ°æ ç­¾-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ ç­¾ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[CustomerTag](#schemacustomertag)|true|none|

> Example responses

> 404 Response

<h3 id="æ´æ°æ ç­¾-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ ç­¾ä¸å­å¨|[ResultCustomerTag](#schemaresultcustomertag)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤æ ç­¾

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

æéï¼tag:deleteãæ ID å é¤æ ç­¾ï¼å«å®¢æ·å³èå³ç³»ä¸å¹¶è§£é¤ï¼ã

<h3 id="å é¤æ ç­¾-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ ç­¾ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤æ ç­¾-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ ç­¾ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ ç­¾å¯å

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

æéï¼tag:editãå¯åæ ç­¾ï¼status åå¼ï¼1å¯ç¨/0åç¨ã

<h3 id="æ ç­¾å¯å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ ç­¾ ID|
|status|query|integer(int32)|true|ç¶æï¼1å¯ç¨/0åç¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="æ ç­¾å¯å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|status ä»æ¯æ 0/1|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°èªå¨æ ç­¾è§å

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
  "ruleName": "è¯åé«äº80èªå¨ææ ",
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

æéï¼tag:ruleãæ ID æ´æ°èªå¨æ ç­¾è§åï¼condition_field/condition_op åå¼è§åå»ºæ¥å£ã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "è¯åé«äº80èªå¨ææ ",
  "tagId": 0,
  "conditionField": "score",
  "conditionOp": "gte",
  "conditionValue": "80",
  "status": 1
}
```

<h3 id="æ´æ°èªå¨æ ç­¾è§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[CustomerTagRule](#schemacustomertagrule)|true|none|

> Example responses

> 400 Response

<h3 id="æ´æ°èªå¨æ ç­¾è§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è§ååç§°/æ¡ä»¶å¼/è§åå­æ®µéæ³|[ResultCustomerTagRule](#schemaresultcustomertagrule)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ ç­¾è§åä¸å­å¨|[ResultCustomerTagRule](#schemaresultcustomertagrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤èªå¨æ ç­¾è§å

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

æéï¼tag:ruleãæ ID å é¤èªå¨æ ç­¾è§åã

<h3 id="å é¤èªå¨æ ç­¾è§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤èªå¨æ ç­¾è§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ ç­¾è§åä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ ç­¾åé¡µæ¥è¯¢

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

æéï¼tag:listãæå³é®å­ï¼æ ç­¾åç§°æ¨¡ç³å¹éï¼åé¡µæ¥è¯¢æ ç­¾ã

<h3 id="æ ç­¾åé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼æ ç­¾åç§°æ¨¡ç³å¹éï¼|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ ç­¾åé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCustomerTag](#schemaresultpageresultcustomertag)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºæ ç­¾

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
  "name": "é«æåå®¢æ·",
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

æéï¼tag:addãæ°å»ºå®¢æ·æ ç­¾å®ä¹ï¼status é»è®¤ 1å¯ç¨ã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "é«æåå®¢æ·",
  "color": "#FF5733",
  "remark": "string",
  "status": 1
}
```

<h3 id="åå»ºæ ç­¾-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[CustomerTag](#schemacustomertag)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºæ ç­¾-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|æ ç­¾åç§°ä¸è½ä¸ºç©º|[ResultCustomerTag](#schemaresultcustomertag)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## è§ååé¡µæ¥è¯¢

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

æéï¼tag:ruleãåé¡µæ¥è¯¢èªå¨æ ç­¾è§åã

<h3 id="è§ååé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="è§ååé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCustomerTagRule](#schemaresultpageresultcustomertagrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºèªå¨æ ç­¾è§å

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
  "ruleName": "è¯åé«äº80èªå¨ææ ",
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

æéï¼tag:ruleãæ°å»ºèªå¨æ ç­¾è§åï¼condition_field åå¼ï¼scoreè¯å/intent_levelæåç­çº§/stageå®¢æ·é¶æ®µ/industryè¡ä¸/regionå°åº/sourceæ¥æºï¼condition_op åå¼ï¼gtå¤§äº/gteå¤§äºç­äº/ltå°äº/lteå°äºç­äº/eqç­äº/containsåå«ã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "è¯åé«äº80èªå¨ææ ",
  "tagId": 0,
  "conditionField": "score",
  "conditionOp": "gte",
  "conditionValue": "80",
  "status": 1
}
```

<h3 id="åå»ºèªå¨æ ç­¾è§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[CustomerTagRule](#schemacustomertagrule)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºèªå¨æ ç­¾è§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è§ååç§°/è§åå­æ®µ/æ¡ä»¶å¼éæ³|[ResultCustomerTagRule](#schemaresultcustomertagrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æå¨æ§è¡è§å

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

æéï¼tag:ruleãç«å³å¯¹å¨éå®¢æ·æ§è¡ä¸æ¬¡æå®è§åï¼å½ä¸­å®¢æ·èªå¨æä¸è§åç»å®çæ ç­¾ï¼è¿åæ¬æ¬¡å½ä¸­å®¢æ·æ°ã

<h3 id="æå¨æ§è¡è§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="æå¨æ§è¡è§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ ç­¾è§åä¸å­å¨|[ResultInteger](#schemaresultinteger)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ç»å®¢æ·æ¹éææ 

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

æéï¼tag:editãè¯·æ±ä½ä¸ºæ ç­¾ ID åè¡¨ï¼å°å¤ä¸ªæ ç­¾ç»å®å°æå®å®¢æ·ã

> Body parameter

```json
[
  0
]
```

<h3 id="ç»å®¢æ·æ¹éææ -parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|customerId|path|integer(int64)|true|å®¢æ· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|array[integer]|true|none|

> Example responses

> 400 Response

<h3 id="ç»å®¢æ·æ¹éææ -responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|æ ç­¾åè¡¨ä¸è½ä¸ºç©º|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|å®¢æ·ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ ç­¾ç­éï¼æ¥è¯¢ææ ç­¾ä¸çå®¢æ·

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

æéï¼tag:listãææ ç­¾ ID åé¡µæ¥è¯¢æä¸è¯¥æ ç­¾çå®¢æ·åè¡¨ã

<h3 id="æ ç­¾ç­éï¼æ¥è¯¢ææ ç­¾ä¸çå®¢æ·-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ ç­¾ ID|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ ç­¾ç­éï¼æ¥è¯¢ææ ç­¾ä¸çå®¢æ·-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCustomer](#schemaresultpageresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ç§»é¤å®¢æ·æ ç­¾

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

æéï¼tag:editãè§£é¤æå®å®¢æ·ä¸çåä¸ªæ ç­¾ç»å®ã

<h3 id="ç§»é¤å®¢æ·æ ç­¾-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|customerId|path|integer(int64)|true|å®¢æ· ID|
|tagId|path|integer(int64)|true|æ ç­¾ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="ç§»é¤å®¢æ·æ ç­¾-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|å®¢æ·/æ ç­¾ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">è¯æ¯åº</h1>

## è¯æ¯è¯¦æ

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

æ ID æ¥è¯¢è¯æ¯åå®¹ï¼åºæ¯ï¼è¯æ¯åºç®¡çãä¸é®åéé¢è§ï¼æéï¼speech:list

<h3 id="è¯æ¯è¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è¯æ¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="è¯æ¯è¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultSpeechLibrary](#schemaresultspeechlibrary)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°è¯æ¯

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
  "title": "å¼åºç½-æ åç",
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

ä¿®æ¹è¯æ¯æ é¢/åç±»/åå®¹ï¼åºæ¯ï¼è¯æ¯åºç®¡çç»´æ¤ï¼æéï¼speech:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "å¼åºç½-æ åç",
  "category": "general",
  "content": "string",
  "status": 1
}
```

<h3 id="æ´æ°è¯æ¯-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è¯æ¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[SpeechLibrary](#schemaspeechlibrary)|true|none|

> Example responses

> 400 Response

<h3 id="æ´æ°è¯æ¯-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è¯æ¯åå®¹ä¸è½ä¸ºç©º|[ResultSpeechLibrary](#schemaresultspeechlibrary)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|è¯æ¯ä¸å­å¨|[ResultSpeechLibrary](#schemaresultspeechlibrary)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤è¯æ¯

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

å é¤è¯æ¯ï¼åºæ¯ï¼è¯æ¯åºç®¡çç»´æ¤ï¼æéï¼speech:deleteãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

<h3 id="å é¤è¯æ¯-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è¯æ¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤è¯æ¯-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|è¯æ¯ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## è¯æ¯å¯å

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

å¯ç¨/åç¨è¯æ¯ï¼åç¨åä¸å¯è¢«ä¸é®åéæ£ç´¢ï¼åºæ¯ï¼è¯æ¯åºä¸ä¸æ¶ç®¡çï¼æéï¼speech:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

<h3 id="è¯æ¯å¯å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è¯æ¯ ID|
|status|query|integer(int32)|true|ç¶æï¼1 å¯ç¨ / 0 åç¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="è¯æ¯å¯å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|è¯æ¯ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## è¯æ¯åé¡µæ¥è¯¢ï¼keyword/åºæ¯åç±»/ç¶æè¿æ»¤ï¼

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

è¯æ¯åè¡¨æ£ç´¢ï¼æ¯æå³é®å­/åºæ¯åç±»/ç¶æè¿æ»¤ï¼åºæ¯ï¼è¯æ¯åºç®¡çãä¸é®åéééï¼æéï¼speech:list

<h3 id="è¯æ¯åé¡µæ¥è¯¢ï¼keyword/åºæ¯åç±»/ç¶æè¿æ»¤ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼è¯æ¯æ é¢/åå®¹æ¨¡ç³å¹éï¼|
|category|query|string|false|åºæ¯åç±»ï¼generaléç¨/quoteæ¥ä»·/selectionéå/objectionå¼è®®/followè·è¿/openingå¼åº|
|status|query|integer(int32)|false|ç¶æï¼1 å¯ç¨ / 0 åç¨|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="è¯æ¯åé¡µæ¥è¯¢ï¼keyword/åºæ¯åç±»/ç¶æè¿æ»¤ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultSpeechLibrary](#schemaresultpageresultspeechlibrary)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºè¯æ¯

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
  "title": "å¼åºç½-æ åç",
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

æ°å¢è¯æ¯ï¼éç¨/åºæ¯åç±»ï¼ï¼åºæ¯ï¼è¯æ¯åºç®¡çç»´æ¤ï¼æéï¼speech:addãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "å¼åºç½-æ åç",
  "category": "general",
  "content": "string",
  "status": 1
}
```

<h3 id="åå»ºè¯æ¯-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[SpeechLibrary](#schemaspeechlibrary)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºè¯æ¯-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è¯æ¯æ é¢/åå®¹ä¸è½ä¸ºç©º|[ResultSpeechLibrary](#schemaresultspeechlibrary)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">è§è²ç®¡ç</h1>

## è§è²è¯¦æ

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

éæé role:list

<h3 id="è§è²è¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è§è² ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="è§è²è¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultRole](#schemaresultrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°è§è²

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

éæé role:edit

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

<h3 id="æ´æ°è§è²-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è§è² ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Role](#schemarole)|true|none|

> Example responses

> 400 Response

<h3 id="æ´æ°è§è²-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è§è²ä¸å­å¨|[ResultRole](#schemaresultrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤è§è²

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

éæé role:delete

<h3 id="å é¤è§è²-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è§è² ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å é¤è§è²-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è§è²ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å¯ç¨/åç¨è§è²

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

éæé role:edit

<h3 id="å¯ç¨/åç¨è§è²-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è§è² ID|
|status|query|integer(int32)|true|ç¶æï¼1å¯ç¨/0åç¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å¯ç¨/åç¨è§è²-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è§è²ä¸å­å¨/status ä»æ¯æ 0/1|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ¥è¯¢è§è²å·²åéçèå ID

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

éæé role:assign-menu

<h3 id="æ¥è¯¢è§è²å·²åéçèå-id-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è§è² ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ¥è¯¢è§è²å·²åéçèå-id-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListLong](#schemaresultlistlong)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åéè§è²èåï¼å¨éè¦çï¼

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

éæé role:assign-menu

> Body parameter

```json
[
  0
]
```

<h3 id="åéè§è²èåï¼å¨éè¦çï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|è§è² ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|array[integer]|true|none|

> Example responses

> 400 Response

<h3 id="åéè§è²èåï¼å¨éè¦çï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è§è²ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åé¡µæ¥è¯¢è§è²

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

éæé role:list

<h3 id="åé¡µæ¥è¯¢è§è²-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼è§è²åç§°/ç¼ç ï¼|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åé¡µæ¥è¯¢è§è²-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultRole](#schemaresultpageresultrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºè§è²

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

éæé role:add

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

<h3 id="åå»ºè§è²-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Role](#schemarole)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºè§è²-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è§è²ç¼ç ä¸åç§°ä¸è½ä¸ºç©º/è§è²ç¼ç å·²å­å¨|[ResultRole](#schemaresultrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å¯ç¨è§è²åè¡¨ï¼ä¸æç¨ï¼

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

ç»å½ç¨æ·å³å¯è®¿é®

<h3 id="å¯ç¨è§è²åè¡¨ï¼ä¸æç¨ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å¯ç¨è§è²åè¡¨ï¼ä¸æç¨ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListRole](#schemaresultlistrole)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">äº§ååº</h1>

## äº§åè¯¦æ

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

æ ID æ¥è¯¢äº§åå®æ´èµæï¼å«åæ°/éä»¶ï¼ï¼åºæ¯ï¼äº§ååºç®¡çãå¿«æ·åéé¢è§ï¼æéï¼product:list

<h3 id="äº§åè¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|äº§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="äº§åè¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultProduct](#schemaresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°äº§åï¼å«åæ°/éä»¶ï¼

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
  "name": "ä¼ä¸ç CRM",
  "sku": "CRM-ENT-001",
  "spec": "æ åç",
  "price": 1999,
  "params": "{\"å®¹é\":\"100GB\",\"å¹¶åæ°\":\"500\"}",
  "attachments": "[{\"name\":\"éåè¡¨.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
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

ä¿®æ¹äº§åèµæï¼å«åæ°/éä»¶ï¼ï¼åºæ¯ï¼äº§ååºç®¡çç»´æ¤ï¼æéï¼product:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "categoryId": 0,
  "name": "ä¼ä¸ç CRM",
  "sku": "CRM-ENT-001",
  "spec": "æ åç",
  "price": 1999,
  "params": "{\"å®¹é\":\"100GB\",\"å¹¶åæ°\":\"500\"}",
  "attachments": "[{\"name\":\"éåè¡¨.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
  "description": "string",
  "status": 1
}
```

<h3 id="æ´æ°äº§åï¼å«åæ°/éä»¶ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|äº§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Product](#schemaproduct)|true|none|

> Example responses

> 400 Response

<h3 id="æ´æ°äº§åï¼å«åæ°/éä»¶ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|äº§ååç§°ä¸è½ä¸ºç©º|[ResultProduct](#schemaresultproduct)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|äº§åä¸å­å¨|[ResultProduct](#schemaresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤äº§å

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

å é¤äº§åèµæï¼åºæ¯ï¼äº§ååºç®¡çç»´æ¤ï¼æéï¼product:deleteãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

<h3 id="å é¤äº§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|äº§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤äº§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|äº§åä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## äº§åä¸ä¸æ¶

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

ä¸æ¶/ä¸æ¶äº§åï¼ä¸æ¶åä¸å¯è¢«å¿«æ·åéæ£ç´¢ï¼åºæ¯ï¼äº§ååºä¸ä¸æ¶ç®¡çï¼æéï¼product:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

<h3 id="äº§åä¸ä¸æ¶-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|äº§å ID|
|status|query|integer(int32)|true|ç¶æï¼1 ä¸æ¶ / 0 ä¸æ¶|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="äº§åä¸ä¸æ¶-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|äº§åä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°åç±»

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
  "name": "CRM äº§åçº¿",
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

ä¿®æ¹äº§ååç±»åç§°/ç¶çº§/æåº/ç¶æï¼åºæ¯ï¼äº§ååºåç±»ä½ç³»ç»´æ¤ï¼æéï¼product:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "CRM äº§åçº¿",
  "parentId": 0,
  "sort": 1,
  "status": 1
}
```

<h3 id="æ´æ°åç±»-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åç±» ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[ProductCategory](#schemaproductcategory)|true|none|

> Example responses

> 404 Response

<h3 id="æ´æ°åç±»-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|äº§ååç±»ä¸å­å¨|[ResultProductCategory](#schemaresultproductcategory)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤åç±»

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

å é¤äº§ååç±»èç¹ï¼å­å¨å­åç±»/å³èäº§åæ¶æä¸å¡è§åå¤çï¼ï¼åºæ¯ï¼äº§ååºåç±»ä½ç³»ç»´æ¤ï¼æéï¼product:deleteãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

<h3 id="å é¤åç±»-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åç±» ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å é¤åç±»-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|å­å¨å­åç±»/åç±»ä¸å­å¨äº§åï¼ä¸è½å é¤|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åç±»å¯å

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

å¯ç¨/åç¨äº§ååç±»ï¼åç¨åä¸çº§ä¸åå±ç¤ºï¼åºæ¯ï¼äº§ååºåç±»ä½ç³»ç»´æ¤ï¼æéï¼product:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

<h3 id="åç±»å¯å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åç±» ID|
|status|query|integer(int32)|true|ç¶æï¼1 å¯ç¨ / 0 åç¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="åç±»å¯å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|äº§ååç±»ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## äº§ååé¡µæ¥è¯¢

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

äº§ååè¡¨æ£ç´¢ï¼æ¯æå³é®å­/åç±»/ä¸ä¸æ¶ç¶æè¿æ»¤ï¼åºæ¯ï¼äº§ååºç®¡çãä¾§è¾¹æ å¿«æ·åééåï¼æéï¼product:list

<h3 id="äº§ååé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼äº§ååç§°/ç¼ç /è§æ ¼æ¨¡ç³å¹éï¼|
|categoryId|query|integer(int64)|false|åç±» ID|
|status|query|integer(int32)|false|ä¸ä¸æ¶ç¶æï¼1 ä¸æ¶ / 0 ä¸æ¶|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="äº§ååé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultProduct](#schemaresultpageresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºäº§å

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
  "name": "ä¼ä¸ç CRM",
  "sku": "CRM-ENT-001",
  "spec": "æ åç",
  "price": 1999,
  "params": "{\"å®¹é\":\"100GB\",\"å¹¶åæ°\":\"500\"}",
  "attachments": "[{\"name\":\"éåè¡¨.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
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

æ°å¢äº§åèµæï¼å«åæ°/éä»¶ï¼ï¼åºæ¯ï¼äº§ååºç®¡çç»´æ¤ï¼æéï¼product:addãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "categoryId": 0,
  "name": "ä¼ä¸ç CRM",
  "sku": "CRM-ENT-001",
  "spec": "æ åç",
  "price": 1999,
  "params": "{\"å®¹é\":\"100GB\",\"å¹¶åæ°\":\"500\"}",
  "attachments": "[{\"name\":\"éåè¡¨.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
  "description": "string",
  "status": 1
}
```

<h3 id="åå»ºäº§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Product](#schemaproduct)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºäº§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|äº§ååç§°ä¸è½ä¸ºç©º|[ResultProduct](#schemaresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åç±»æ åè¡¨

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

æå±çº§è¿åäº§ååç±»æ ï¼åºæ¯ï¼äº§ååºç®¡çãä¾§è¾¹æ å¿«æ·åééååç±»ï¼æéï¼product:list

<h3 id="åç±»æ åè¡¨-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åç±»æ åè¡¨-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListProductCategory](#schemaresultlistproductcategory)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºåç±»

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
  "name": "CRM äº§åçº¿",
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

æ°å¢äº§ååç±»èç¹ï¼åºæ¯ï¼äº§ååºåç±»ä½ç³»ç»´æ¤ï¼æéï¼product:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "CRM äº§åçº¿",
  "parentId": 0,
  "sort": 1,
  "status": 1
}
```

<h3 id="åå»ºåç±»-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[ProductCategory](#schemaproductcategory)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºåç±»-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|åç±»åç§°ä¸è½ä¸ºç©º|[ResultProductCategory](#schemaresultproductcategory)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">èåç®¡ç</h1>

## æ´æ°èå

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

éæé menu:edit

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

<h3 id="æ´æ°èå-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|èå ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Menu](#schemamenu)|true|none|

> Example responses

> 400 Response

<h3 id="æ´æ°èå-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|èåä¸å­å¨/ç¶èåä¸è½æ¯èªå·±|[ResultMenu](#schemaresultmenu)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤èå

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

éæé menu:delete

<h3 id="å é¤èå-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|èå ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å é¤èå-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|èåä¸å­å¨/å­å¨å­èåï¼è¯·åå é¤å­èå|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å¨éèåæ ï¼å«æé®ï¼å¹³å°ç®¡çç¨ï¼

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

éæé menu:list

<h3 id="å¨éèåæ ï¼å«æé®ï¼å¹³å°ç®¡çç¨ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å¨éèåæ ï¼å«æé®ï¼å¹³å°ç®¡çç¨ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListMenu](#schemaresultlistmenu)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºèå

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

éæé menu:add

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

<h3 id="åå»ºèå-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Menu](#schemamenu)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºèå-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|èååç§°ä¸è½ä¸ºç©º|[ResultMenu](#schemaresultmenu)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å½åç¨æ·å¯è§èåæ ï¼åç«¯è·¯ç±ï¼

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

ç»å½ç¨æ·å³å¯è®¿é®

<h3 id="å½åç¨æ·å¯è§èåæ ï¼åç«¯è·¯ç±ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å½åç¨æ·å¯è§èåæ ï¼åç«¯è·¯ç±ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListMenu](#schemaresultlistmenu)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">çº¿ç´¢åé</h1>

## æ´æ°åéè§å

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
  "ruleName": "åä¸åºåçº¿ç´¢åé",
  "ruleType": "region",
  "matchValue": "åä¸",
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

æéï¼lead:assignãæ ID æ´æ°åéè§åï¼rule_type åå¼ï¼productæäº§åçº¿/regionæå°å/round_robinè½®è¯¢ç»ã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "åä¸åºåçº¿ç´¢åé",
  "ruleType": "region",
  "matchValue": "åä¸",
  "targetUserId": 0,
  "targetGroupIds": "string",
  "sort": 1,
  "status": 1
}
```

<h3 id="æ´æ°åéè§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åéè§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[LeadAssignRule](#schemaleadassignrule)|true|none|

> Example responses

> 404 Response

<h3 id="æ´æ°åéè§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|åéè§åä¸å­å¨|[ResultLeadAssignRule](#schemaresultleadassignrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤åéè§å

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

æéï¼lead:assignãæ ID å é¤åéè§åã

<h3 id="å é¤åéè§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åéè§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤åéè§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|åéè§åä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åéè§åå¯å

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

æéï¼lead:assignãå¯ååéè§åï¼status åå¼ï¼1å¯ç¨/0åç¨ã

<h3 id="åéè§åå¯å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åéè§å ID|
|status|query|integer(int32)|true|ç¶æï¼1å¯ç¨/0åç¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="åéè§åå¯å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|åéè§åä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æå¨åéåæ¡çº¿ç´¢

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

æéï¼lead:assignãæå½åç§æ·å·²å¯ç¨çåéè§åï¼product/region/round_robinï¼å°çº¿ç´¢èªå¨åéç»å¹ééå®ï¼æ å¹éè§åæ¶ä¿ææªåéã

<h3 id="æå¨åéåæ¡çº¿ç´¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|çº¿ç´¢ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="æå¨åéåæ¡çº¿ç´¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|çº¿ç´¢ä¸å­å¨ï¼ä¸å¡ç  1201ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åæ¶è¶æ¶çº¿ç´¢å¹¶éæ°åé

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

æéï¼lead:assignãæ«æè¶è¿ååº SLAï¼sla_deadlineï¼ä»æªå¤çççº¿ç´¢ï¼åæ¶åæåéè§åéæ°åéï¼è¿åæ¬æ¬¡åæ¶å¹¶éæ°åéççº¿ç´¢æ°ã

<h3 id="åæ¶è¶æ¶çº¿ç´¢å¹¶éæ°åé-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åæ¶è¶æ¶çº¿ç´¢å¹¶éæ°åé-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultInteger](#schemaresultinteger)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åéè§ååé¡µæ¥è¯¢

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

æéï¼lead:assignãåé¡µæ¥è¯¢å½åç§æ·ççº¿ç´¢åéè§åã

<h3 id="åéè§ååé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åéè§ååé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultLeadAssignRule](#schemaresultpageresultleadassignrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºåéè§å

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
  "ruleName": "åä¸åºåçº¿ç´¢åé",
  "ruleType": "region",
  "matchValue": "åä¸",
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

æéï¼lead:assignãæ°å»ºçº¿ç´¢åéè§åï¼rule_type åå¼ï¼productæäº§åçº¿/regionæå°å/round_robinè½®è¯¢ç»ã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "ruleName": "åä¸åºåçº¿ç´¢åé",
  "ruleType": "region",
  "matchValue": "åä¸",
  "targetUserId": 0,
  "targetGroupIds": "string",
  "sort": 1,
  "status": 1
}
```

<h3 id="åå»ºåéè§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[LeadAssignRule](#schemaleadassignrule)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºåéè§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è§ååç§°/è§åç±»å/è½®è¯¢ç»/å¹éå¼/æå®éå®ä¸è½ä¸ºç©º|[ResultLeadAssignRule](#schemaresultleadassignrule)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">èµæåä¸­å¿</h1>

## åå¸èµæ

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

å°èç¨¿èµæç½®ä¸ºå·²åå¸ç¶æ

<h3 id="åå¸èµæ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|èµæ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="åå¸èµæ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|èµæä¸å­å¨|[ResultDocument](#schemaresultdocument)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åé¡µæ¥è¯¢èµæ

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

æç§æ·/èµæç±»åè¿æ»¤

<h3 id="åé¡µæ¥è¯¢èµæ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|false|ç§æ· IDï¼å¹³å°ç®¡çåå¯æå®ï¼ä¸ºç©ºé»è®¤å½åç§æ·ï¼|
|docType|query|string|false|èµæç±»åï¼product_brochureäº§åæå/caseæ¡ä¾/whitepaperç½ç®ä¹¦/selection_tableéåè¡¨|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åé¡µæ¥è¯¢èµæ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultDocument](#schemaresultpageresultdocument)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ°å¢èµæ

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
  "title": "AI å¤å¼ç³»ç»äº§åæå",
  "docType": "product_brochure",
  "fileUrl": "/uploads/xxx.pdf",
  "version": 1,
  "status": 0,
  "tags": "[\"AI\",\"å¤å¼\"]"
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

è¯·æ±ä½ä¸ºèµæä¿¡æ¯ï¼ä¸å«æä»¶ï¼æä»¶éè¿ä¸ä¼ æ¥å£è·å URL ååå¥ fileUrlï¼

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "title": "AI å¤å¼ç³»ç»äº§åæå",
  "docType": "product_brochure",
  "fileUrl": "/uploads/xxx.pdf",
  "version": 1,
  "status": 0,
  "tags": "[\"AI\",\"å¤å¼\"]"
}
```

<h3 id="æ°å¢èµæ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Document](#schemadocument)|true|none|

> Example responses

> 400 Response

<h3 id="æ°å¢èµæ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|tenantId/title/docType ä¸è½ä¸ºç©º|[ResultDocument](#schemaresultdocument)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ä¸ä¼ èµææä»¶ï¼è¿åå¯è®¿é® URLï¼

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

multipart/form-data è¡¨åä¸ä¼ ï¼è¿å /uploads/xxx å½¢å¼çè®¿é® URL

> Body parameter

```json
{
  "file": "string"
}
```

<h3 id="ä¸ä¼ èµææä»¶ï¼è¿åå¯è®¿é®-urlï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|object|false|none|
|» file|body|string(binary)|true|æä»¶|

> Example responses

> 400 Response

<h3 id="ä¸ä¼ èµææä»¶ï¼è¿åå¯è®¿é®-urlï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|æä»¶ä¸è½ä¸ºç©º|[ResultString](#schemaresultstring)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|æä»¶ä¸ä¼ å¤±è´¥ï¼ä¸å¡ç  1701ï¼|[ResultString](#schemaresultstring)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">å­å¸ç®¡ç</h1>

## æ´æ°å­å¸ç±»å

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
  "dictName": "çº¿ç´¢ç¶æ",
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

éæé dict:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "dictName": "çº¿ç´¢ç¶æ",
  "status": 1,
  "remark": "string"
}
```

<h3 id="æ´æ°å­å¸ç±»å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å­å¸ç±»å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[DictType](#schemadicttype)|true|none|

> Example responses

> 400 Response

<h3 id="æ´æ°å­å¸ç±»å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|å­å¸ç±»åä¸å­å¨|[ResultDictType](#schemaresultdicttype)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤å­å¸ç±»å

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

éæé dict:delete

<h3 id="å é¤å­å¸ç±»å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å­å¸ç±»å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å é¤å­å¸ç±»å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|å­å¸ç±»åä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°å­å¸æ°æ®

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
  "label": "å·²åé",
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

éæé dict:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "label": "å·²åé",
  "value": "1",
  "sort": 0,
  "status": 1,
  "remark": "string"
}
```

<h3 id="æ´æ°å­å¸æ°æ®-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å­å¸æ°æ® ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[DictData](#schemadictdata)|true|none|

> Example responses

> 400 Response

<h3 id="æ´æ°å­å¸æ°æ®-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|å­å¸æ°æ®ä¸å­å¨|[ResultDictData](#schemaresultdictdata)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤å­å¸æ°æ®

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

éæé dict:delete

<h3 id="å é¤å­å¸æ°æ®-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å­å¸æ°æ® ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å é¤å­å¸æ°æ®-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|å­å¸æ°æ®ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å­å¸ç±»ååé¡µ

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

éæé dict:list

<h3 id="å­å¸ç±»ååé¡µ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼å­å¸åç§°/ç¼ç æ¨¡ç³å¹éï¼|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å­å¸ç±»ååé¡µ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultDictType](#schemaresultpageresultdicttype)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºå­å¸ç±»å

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
  "dictName": "çº¿ç´¢ç¶æ",
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

éæé dict:add

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "dictName": "çº¿ç´¢ç¶æ",
  "status": 1,
  "remark": "string"
}
```

<h3 id="åå»ºå­å¸ç±»å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[DictType](#schemadicttype)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºå­å¸ç±»å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|å­å¸ç±»åç¼ç å·²å­å¨/ç¼ç ä¸åç§°ä¸è½ä¸ºç©º|[ResultDictType](#schemaresultdicttype)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å­å¸æ°æ®åé¡µ

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

éæé dict:list

<h3 id="å­å¸æ°æ®åé¡µ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|dictType|query|string|false|å­å¸ç±»åç¼ç |
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å­å¸æ°æ®åé¡µ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultDictData](#schemaresultpageresultdictdata)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºå­å¸æ°æ®

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
  "label": "å·²åé",
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

éæé dict:add

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "dictType": "lead_status",
  "label": "å·²åé",
  "value": "1",
  "sort": 0,
  "status": 1,
  "remark": "string"
}
```

<h3 id="åå»ºå­å¸æ°æ®-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[DictData](#schemadictdata)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºå­å¸æ°æ®-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|å­å¸ç±»åãæ ç­¾ãå¼ä¸è½ä¸ºç©º|[ResultDictData](#schemaresultdictdata)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å¯ç¨å­å¸ç±»ååè¡¨

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

åç«¯ä¸ææ¡ç´æ¥è°ç¨ï¼ä»è¿åå¯ç¨ç¶æå­å¸ç±»åï¼æ éç»å½æé

<h3 id="å¯ç¨å­å¸ç±»ååè¡¨-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å¯ç¨å­å¸ç±»ååè¡¨-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListDictType](#schemaresultlistdicttype)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æç±»åæ¥è¯¢å¯ç¨å­å¸æ°æ®ï¼åç«¯ä¸æï¼

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

åç«¯ä¸ææ¡ç´æ¥è°ç¨ï¼ä»è¿åå¯ç¨ç¶æå­å¸æ°æ®ï¼æ éç»å½æé

<h3 id="æç±»åæ¥è¯¢å¯ç¨å­å¸æ°æ®ï¼åç«¯ä¸æï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|dictType|path|string|true|å­å¸ç±»åç¼ç |
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æç±»åæ¥è¯¢å¯ç¨å­å¸æ°æ®ï¼åç«¯ä¸æï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListDictData](#schemaresultlistdictdata)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">å®¢æ·ç®¡ç</h1>

## å®¢æ·è¯¦æ

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

æéï¼customer:listãæ ID æ¥è¯¢å®¢æ·è¯¦æï¼å«äººåæ¶æ org_structureï¼ã

<h3 id="å®¢æ·è¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å®¢æ· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å®¢æ·è¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°å®¢æ·

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
  "name": "ææç§ææéå¬å¸",
  "industry": "è½¯ä»¶æå¡",
  "scale": "100-499äºº",
  "region": "ä¸æµ·",
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

æéï¼customer:editãæ ID æ´æ°å®¢æ·åºæ¬ä¿¡æ¯ï¼åç§°/è¡ä¸/è§æ¨¡/å°åºç­ï¼ã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "ææç§ææéå¬å¸",
  "industry": "è½¯ä»¶æå¡",
  "scale": "100-499äºº",
  "region": "ä¸æµ·",
  "orgStructure": "string",
  "source": "public_data",
  "enrichmentStatus": 0,
  "stage": "potential",
  "intentLevel": 3,
  "score": 80
}
```

<h3 id="æ´æ°å®¢æ·-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å®¢æ· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Customer](#schemacustomer)|true|none|

> Example responses

> 404 Response

<h3 id="æ´æ°å®¢æ·-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|å®¢æ·ä¸å­å¨|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤å®¢æ·

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

æéï¼customer:deleteãæ ID é»è¾å é¤å®¢æ·ã

<h3 id="å é¤å®¢æ·-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å®¢æ· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤å®¢æ·-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|å®¢æ·ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å®¢æ·é¶æ®µæµè½¬

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

æéï¼customer:editãæ¨è¿/åéå®¢æ·é¶æ®µï¼stage åå¼ï¼newæ½å¨/potentialææå/intentionæ¥ä»·/negotiatingè°å¤/wonæäº¤/lostæµå¤±ã

<h3 id="å®¢æ·é¶æ®µæµè½¬-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å®¢æ· ID|
|stage|query|string|true|å®¢æ·é¶æ®µï¼newæ½å¨/potentialææå/intentionæ¥ä»·/negotiatingè°å¤/wonæäº¤/lostæµå¤±|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å®¢æ·é¶æ®µæµè½¬-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|éæ³å®¢æ·é¶æ®µ|[ResultCustomer](#schemaresultcustomer)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|å®¢æ·ä¸å­å¨|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æåç­çº§/è¯åç»´æ¤

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

æéï¼customer:editãç»´æ¤å®¢æ·æåç­çº§ä¸ç»¼åè¯åï¼intentLevel åå¼ 0-5ï¼score åå¼ 0-100ã

<h3 id="æåç­çº§/è¯åç»´æ¤-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å®¢æ· ID|
|intentLevel|query|integer(int32)|false|æåç­çº§ï¼0-5|
|score|query|integer(int32)|false|ç»¼åè¯åï¼0-100|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="æåç­çº§/è¯åç»´æ¤-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|æåç­çº§éå¨ 0-5 ä¹é´/è¯åéå¨ 0-100 ä¹é´|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å®¢æ·åé¡µæ¥è¯¢

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

æéï¼customer:listãæå³é®å­/è¡ä¸/å°åº/å®¢æ·é¶æ®µåé¡µæ¥è¯¢å®¢æ·ï¼stage åå¼ï¼newæ½å¨/potentialææå/intentionæ¥ä»·/negotiatingè°å¤/wonæäº¤/lostæµå¤±ï¼ç»ææç»¼åè¯åååºã

<h3 id="å®¢æ·åé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼å¬å¸åç§°æ¨¡ç³å¹éï¼|
|industry|query|string|false|è¡ä¸ï¼æ¨¡ç³å¹éï¼|
|region|query|string|false|å°åºï¼æ¨¡ç³å¹éï¼|
|stage|query|string|false|å®¢æ·é¶æ®µï¼newæ½å¨/potentialææå/intentionæ¥ä»·/negotiatingè°å¤/wonæäº¤/lostæµå¤±|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å®¢æ·åé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCustomer](#schemaresultpageresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºå®¢æ·

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
  "name": "ææç§ææéå¬å¸",
  "industry": "è½¯ä»¶æå¡",
  "scale": "100-499äºº",
  "region": "ä¸æµ·",
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

æéï¼customer:addãæ°å»ºå®¢æ·å¬å¸ï¼å®¢æ·åç§°å¿å¡«ï¼stage é»è®¤ newæ½å¨ã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "ææç§ææéå¬å¸",
  "industry": "è½¯ä»¶æå¡",
  "scale": "100-499äºº",
  "region": "ä¸æµ·",
  "orgStructure": "string",
  "source": "public_data",
  "enrichmentStatus": 0,
  "stage": "potential",
  "intentLevel": 3,
  "score": 80
}
```

<h3 id="åå»ºå®¢æ·-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Customer](#schemacustomer)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºå®¢æ·-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|å®¢æ·åç§°ä¸è½ä¸ºç©º|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ä¼ä¸ä¿¡æ¯åå¡«ï¼ç¬¬ä¸æ¹æ¥è¯¢ï¼

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

æéï¼customer:editãç¬¬ä¸æ¹ä¼ä¸ä¿¡æ¯æ¥è¯¢æ¥å£ï¼æè°ç¨é¢çéå¶ï¼è¯·å¿é«é¢è°ç¨ï¼è¿åå­æ®µä»¥ç¬¬ä¸æ¹ååºä¸ºåãè¯·æ±ä½ä¸ºäººåæ¶æ org_structure åå§ JSON å­ç¬¦ä¸²ï¼å¯ç©ºï¼ï¼ä¾å¦ {"executives":[{"name":"å¼ ä¸","title":"CEO","phone":"13800138000","email":"zhangsan@corp.com"}],"departments":["éå®é¨","ææ¯é¨"]}ï¼æ¥å£ä¼å° org_structure ä¸ source åå¥å®¢æ·å¹¶ç½® enrichment_status=1ï¼è¿ååå¡«åçå®¢æ·è¯¦æï¼å« org_structureï¼ã

> Body parameter

```json
"string"
```

<h3 id="ä¼ä¸ä¿¡æ¯åå¡«ï¼ç¬¬ä¸æ¹æ¥è¯¢ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|å®¢æ· ID|
|source|query|string|false|æ°æ®æ¥æºæ è¯ï¼å¦ public_dataå¬å¼æ°æ®/customer_providedå®¢æ·æä¾ï¼|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|string|false|none|

> Example responses

> 404 Response

<h3 id="ä¼ä¸ä¿¡æ¯åå¡«ï¼ç¬¬ä¸æ¹æ¥è¯¢ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|å®¢æ·ä¸å­å¨|[ResultCustomer](#schemaresultcustomer)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">ç³»ç»åæ°</h1>

## æ´æ°åæ°

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
  "configValue": "AI éå®çº¿ç´¢ç³»ç»",
  "configName": "ç«ç¹åç§°",
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

éæé config:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "configKey": "system.siteName",
  "configValue": "AI éå®çº¿ç´¢ç³»ç»",
  "configName": "ç«ç¹åç§°",
  "configType": 2,
  "remark": "string"
}
```

<h3 id="æ´æ°åæ°-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åæ° ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Config](#schemaconfig)|true|none|

> Example responses

> 200 Response

<h3 id="æ´æ°åæ°-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultConfig](#schemaresultconfig)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤åæ°ï¼åç½®åæ°ä¸å¯å é¤ï¼

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

éæé config:editï¼åç½®åæ°ï¼configType=1ï¼å é¤å°è¿åä¸å¡éè¯¯

<h3 id="å é¤åæ°ï¼åç½®åæ°ä¸å¯å é¤ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|åæ° ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å é¤åæ°ï¼åç½®åæ°ä¸å¯å é¤ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|åç½®åæ°ä¸åè®¸å é¤|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åæ°åé¡µ

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

éæé config:list

<h3 id="åæ°åé¡µ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼åæ°åç§°/åæ°é®æ¨¡ç³å¹éï¼|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åæ°åé¡µ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultConfig](#schemaresultpageresultconfig)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºåæ°

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
  "configValue": "AI éå®çº¿ç´¢ç³»ç»",
  "configName": "ç«ç¹åç§°",
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

éæé config:edit

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "configKey": "system.siteName",
  "configValue": "AI éå®çº¿ç´¢ç³»ç»",
  "configName": "ç«ç¹åç§°",
  "configType": 2,
  "remark": "string"
}
```

<h3 id="åå»ºåæ°-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Config](#schemaconfig)|true|none|

> Example responses

> 200 Response

<h3 id="åå»ºåæ°-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultConfig](#schemaresultconfig)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ key æ¥è¯¢åæ°ï¼ä¸å¡æ¹è°ç¨ï¼

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

ä¸å¡æ¹ç´æ¥è°ç¨ï¼æ éç»å½æéï¼ä¸å­å¨æ¶è¿å null

<h3 id="æ-key-æ¥è¯¢åæ°ï¼ä¸å¡æ¹è°ç¨ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|key|path|string|true|åæ°é®|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ-key-æ¥è¯¢åæ°ï¼ä¸å¡æ¹è°ç¨ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultConfig](#schemaresultconfig)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">ç«ååº</h1>

## ç«åè¯¦æ

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

æ ID æ¥è¯¢ç«åå®æ´æ¡£æ¡ï¼å«ä¼å£å¿/æ»é²è¯æ¯ï¼ï¼åºæ¯ï¼ç«ååºç®¡çãç¥è¯æ£ç´¢ï¼æéï¼competitor:list

<h3 id="ç«åè¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç«å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ç«åè¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCompetitor](#schemaresultcompetitor)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°ç«åï¼ä¼å£å¿/æ»é²è¯æ¯ï¼

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
  "name": "ææäº CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"åè½å¨é¢\",\"ä»·æ ¼ä½\"]",
  "weaknesses": "[\"å®æ½å¤æ\",\"å®åå·®\"]",
  "defenseTactics": "[{\"scenario\":\"æ¯ä»·æ ¼\",\"tactic\":\"å¼ºè°æ»æ¥æææ¬\"}]",
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

ä¿®æ¹ç«åæ¡£æ¡ï¼å«ä¼å£å¿/æ»é²è¯æ¯ï¼ï¼åºæ¯ï¼ç«ååºç®¡çç»´æ¤ï¼æéï¼competitor:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "ææäº CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"åè½å¨é¢\",\"ä»·æ ¼ä½\"]",
  "weaknesses": "[\"å®æ½å¤æ\",\"å®åå·®\"]",
  "defenseTactics": "[{\"scenario\":\"æ¯ä»·æ ¼\",\"tactic\":\"å¼ºè°æ»æ¥æææ¬\"}]",
  "status": 1
}
```

<h3 id="æ´æ°ç«åï¼ä¼å£å¿/æ»é²è¯æ¯ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç«å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Competitor](#schemacompetitor)|true|none|

> Example responses

> 400 Response

<h3 id="æ´æ°ç«åï¼ä¼å£å¿/æ»é²è¯æ¯ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|ç«ååç§°ä¸è½ä¸ºç©º/ç«åä¸å­å¨|[ResultCompetitor](#schemaresultcompetitor)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤ç«å

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

å é¤ç«åæ¡£æ¡ï¼å«å³èç«åäº§åï¼ï¼åºæ¯ï¼ç«ååºç®¡çç»´æ¤ï¼æéï¼competitor:deleteãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

<h3 id="å é¤ç«å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç«å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤ç«å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç«åä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ç«åå¯å

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

å¯ç¨/åç¨ç«åï¼åç¨åä¸å¯è¢«ç¥è¯æ£ç´¢ï¼åºæ¯ï¼ç«ååºä¸ä¸æ¶ç®¡çï¼æéï¼competitor:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

<h3 id="ç«åå¯å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç«å ID|
|status|query|integer(int32)|true|ç¶æï¼1 å¯ç¨ / 0 åç¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="ç«åå¯å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç«åä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°ç«åäº§ååæ°

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
  "productName": "ç«åäºç",
  "spec": "æè°ç",
  "price": 2999,
  "params": "{\"å¹¶åæ°\":\"300\",\"å­å¨\":\"50GB\"}"
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

ä¿®æ¹ç«åäº§ååæ°ï¼åºæ¯ï¼ç«ååºç®¡çç»´æ¤ï¼æéï¼competitor:editãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "competitorId": 0,
  "productName": "ç«åäºç",
  "spec": "æè°ç",
  "price": 2999,
  "params": "{\"å¹¶åæ°\":\"300\",\"å­å¨\":\"50GB\"}"
}
```

<h3 id="æ´æ°ç«åäº§ååæ°-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|productId|path|integer(int64)|true|ç«åäº§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[CompetitorProduct](#schemacompetitorproduct)|true|none|

> Example responses

> 400 Response

<h3 id="æ´æ°ç«åäº§ååæ°-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|ç«åäº§ååç§°ä¸è½ä¸ºç©º|[ResultCompetitorProduct](#schemaresultcompetitorproduct)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç«åäº§åä¸å­å¨|[ResultCompetitorProduct](#schemaresultcompetitorproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤ç«åäº§å

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

å é¤ç«åäº§ååæ°ï¼åºæ¯ï¼ç«ååºç®¡çç»´æ¤ï¼æéï¼competitor:deleteãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

<h3 id="å é¤ç«åäº§å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|productId|path|integer(int64)|true|ç«åäº§å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤ç«åäº§å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç«åäº§åä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ç«ååé¡µæ¥è¯¢

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

ç«ååè¡¨æ£ç´¢ï¼æ¯æå³é®å­/åç±»è¿æ»¤ï¼åºæ¯ï¼ç«ååºç®¡çãç¥è¯æ£ç´¢ï¼æéï¼competitor:list

<h3 id="ç«ååé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼ç«ååç§°/ä¸»ä½æè¿°æ¨¡ç³å¹éï¼|
|category|query|string|false|ç«ååç±»|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ç«ååé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultCompetitor](#schemaresultpageresultcompetitor)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºç«å

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
  "name": "ææäº CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"åè½å¨é¢\",\"ä»·æ ¼ä½\"]",
  "weaknesses": "[\"å®æ½å¤æ\",\"å®åå·®\"]",
  "defenseTactics": "[{\"scenario\":\"æ¯ä»·æ ¼\",\"tactic\":\"å¼ºè°æ»æ¥æææ¬\"}]",
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

æ°å¢ç«åä¸»ä½æ¡£æ¡ï¼åºæ¯ï¼ç«ååºç®¡çç»´æ¤ï¼æéï¼competitor:addãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "name": "ææäº CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"åè½å¨é¢\",\"ä»·æ ¼ä½\"]",
  "weaknesses": "[\"å®æ½å¤æ\",\"å®åå·®\"]",
  "defenseTactics": "[{\"scenario\":\"æ¯ä»·æ ¼\",\"tactic\":\"å¼ºè°æ»æ¥æææ¬\"}]",
  "status": 1
}
```

<h3 id="åå»ºç«å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Competitor](#schemacompetitor)|true|none|

> Example responses

> 200 Response

<h3 id="åå»ºç«å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCompetitor](#schemaresultcompetitor)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ç«åäº§ååè¡¨

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

æ¥è¯¢æå®ç«åä¸çäº§ååæ°åè¡¨ï¼åºæ¯ï¼ç«ååºç®¡çãç¥è¯æ£ç´¢ï¼æéï¼competitor:list

<h3 id="ç«åäº§ååè¡¨-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç«å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ç«åäº§ååè¡¨-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListCompetitorProduct](#schemaresultlistcompetitorproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ°å¢ç«åäº§ååæ°

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
  "productName": "ç«åäºç",
  "spec": "æè°ç",
  "price": 2999,
  "params": "{\"å¹¶åæ°\":\"300\",\"å­å¨\":\"50GB\"}"
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

ä¸ºæå®ç«åæ°å¢äº§ååæ°ï¼åºæ¯ï¼ç«ååºç®¡çç»´æ¤ï¼æéï¼competitor:addãâ ï¸ å¼æ­¥èå¨ï¼æ¬æä½ä¼è§¦å RabbitMQ éç¥ï¼exchange=aicrm.eventsï¼routingKey=knowledge.syncï¼ï¼Python ä¾§æ¶è´¹åæ´æ°åéæ°æ®ï¼MQ ä¸å¯ç¨æ¶ä»è®°å½æ¥å¿éçº§ï¼ä¸å½±åæ¬æ¥å£è¿å

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 0,
  "competitorId": 0,
  "productName": "ç«åäºç",
  "spec": "æè°ç",
  "price": 2999,
  "params": "{\"å¹¶åæ°\":\"300\",\"å­å¨\":\"50GB\"}"
}
```

<h3 id="æ°å¢ç«åäº§ååæ°-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ç«å ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[CompetitorProduct](#schemacompetitorproduct)|true|none|

> Example responses

> 200 Response

<h3 id="æ°å¢ç«åäº§ååæ°-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCompetitorProduct](#schemaresultcompetitorproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">æ¸ éæ´»ç </h1>

## æ´æ°æ´»ç 

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
  "name": "æé³å¼æµæ´»ç ",
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

æ´æ°æ¸ éæ´»ç ä¿¡æ¯ï¼æ´ä½è¦çæäº¤å­æ®µï¼æªæäº¤å­æ®µä¿æåå¼ï¼ãéæé channel:editã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "name": "æé³å¼æµæ´»ç ",
  "channelAccountId": 1,
  "scene": "1:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1,
  "scanCount": 0,
  "convertedCount": 0
}
```

<h3 id="æ´æ°æ´»ç -parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ´»ç  ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[ChannelQrCode](#schemachannelqrcode)|true|none|

> Example responses

> 404 Response

<h3 id="æ´æ°æ´»ç -responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ´»ç ä¸å­å¨|[ResultChannelQrCode](#schemaresultchannelqrcode)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤æ´»ç 

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

å é¤æ¸ éæ´»ç ï¼é»è¾å é¤ï¼ï¼å·²äº§ççæ«ç äºä»¶æ°æ®ä¿çãéæé channel:deleteã

<h3 id="å é¤æ´»ç -parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ´»ç  ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤æ´»ç -responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ´»ç ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´»ç åé¡µæ¥è¯¢

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

æ¸ éæ´»ç åé¡µæ¥è¯¢ï¼æ¯ææåç§°å³é®å­/å¼æµæ¸ éè´¦å·è¿æ»¤ãéæé channel:listã

<h3 id="æ´»ç åé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|æ´»ç åç§°å³é®å­ï¼æ¨¡ç³å¹éï¼|
|channelAccountId|query|integer(int64)|false|å¼æµæ¸ éè´¦å· IDï¼å¯¹åº channel_account.idï¼|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ´»ç åé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultChannelQrCode](#schemaresultpageresultchannelqrcode)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## çææ´»ç 

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
  "name": "æé³å¼æµæ´»ç ",
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

çææ¸ éæ´»ç ï¼æå®å¼æµæ¸ éè´¦å·ä¸è½å°å°åï¼çæçäºç»´ç åå®¹æå /{id}/scan æ«ç å¥å£ï¼ç¨äºæ¸ éæ¥æºæ è®°ä¸å¼æµå½å ãéæé channel:addã
scene ä¸å¡«æ¶é»è®¤å æ¸ éID:è´¦å·IDï¼scanCount/convertedCount ä¸å¡«é»è®¤ 0ï¼status ä¸å¡«é»è®¤ 1 å¯ç¨ã

è¯·æ±ç¤ºä¾ï¼
```json
{
  "name": "æé³å¼æµæ´»ç ",
  "channelAccountId": 1,
  "scene": "douyin:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1
}
```
è¿åç¤ºä¾ï¼
```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "tenantId": 1,
    "name": "æé³å¼æµæ´»ç ",
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
  "name": "æé³å¼æµæ´»ç ",
  "channelAccountId": 1,
  "scene": "1:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1,
  "scanCount": 0,
  "convertedCount": 0
}
```

<h3 id="çææ´»ç -parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[ChannelQrCode](#schemachannelqrcode)|true|none|

> Example responses

> 400 Response

<h3 id="çææ´»ç -responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|æ´»ç åç§°/å¼æµæ¸ éè´¦å·ä¸è½ä¸ºç©º|[ResultChannelQrCode](#schemaresultchannelqrcode)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|å¼æµæ¸ éè´¦å·ä¸å­å¨ï¼ä¸å¡ç  1302ï¼|[ResultChannelQrCode](#schemaresultchannelqrcode)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å¯åæ´»ç 

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

å¯ç¨/åç¨æ¸ éæ´»ç ï¼åç¨åæ«ç å¥å£è¿å 400 ä¸å¡éè¯¯âæ´»ç å·²åç¨âãéæé channel:editã

<h3 id="å¯åæ´»ç -parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ´»ç  ID|
|status|query|integer(int32)|true|ç¶æï¼1å¯ç¨/0åç¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å¯åæ´»ç -responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ´»ç ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ«ç è·³è½¬å¥å£

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

æ«ç è·³è½¬å¥å£ï¼äºç»´ç åå®¹æåæ­¤å°åï¼ï¼è®°å½æ«ç æ¬¡æ°å¹¶ä¸æ¥æ«ç äºä»¶ï¼event_type=qrï¼æºå¸¦ scene æ¥æºæ è®°ç¨äºçº¿ç´¢å½å ï¼ï¼éå 302 è·³è½¬å°è½å°å°å qrUrlï¼æ´»ç åç¨æ¶è¿å 400 ä¸å¡éè¯¯âæ´»ç å·²åç¨âãå¬å¼æ¥å£ï¼æ ç»å½ä¸æéæ ¡éªã

<h3 id="æ«ç è·³è½¬å¥å£-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ´»ç  ID|
|scene|query|string|false|æ¸ éæ¥æºæ è®°ï¼æ«ç åºæ¯ï¼ï¼ä¸ä¼ ååæ´»ç èªå¸¦ scene|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

<h3 id="æ«ç è·³è½¬å¥å£-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|æ´»ç å·²åç¨|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ´»ç ä¸å­å¨|None|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">æ¸ éè´¦å·</h1>

## æ´æ°è´¦å·ä¿¡æ¯/ææéç½®

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
  "accountName": "åçå®æ¹å·",
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

æ´æ°æ¸ éè´¦å·ä¿¡æ¯æææéç½®ï¼æ´ä½è¦çæäº¤å­æ®µï¼æªæäº¤å­æ®µä¿æåå¼ï¼ãéæé channel:editã

> Body parameter

```json
{
  "id": 1,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "tenantId": 1,
  "channelId": 1,
  "accountName": "åçå®æ¹å·",
  "externalId": "douyin_open_id_xxx",
  "authConfig": "string",
  "healthStatus": 1,
  "riskLevel": 0
}
```

<h3 id="æ´æ°è´¦å·ä¿¡æ¯/ææéç½®-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ¸ éè´¦å· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[ChannelAccount](#schemachannelaccount)|true|none|

> Example responses

> 404 Response

<h3 id="æ´æ°è´¦å·ä¿¡æ¯/ææéç½®-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ¸ éè´¦å·ä¸å­å¨ï¼ä¸å¡ç  1302ï¼|[ResultChannelAccount](#schemaresultchannelaccount)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤æ¸ éè´¦å·

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

å é¤æ¸ éè´¦å·ï¼é»è¾å é¤ï¼ãéæé channel:deleteã

<h3 id="å é¤æ¸ éè´¦å·-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ¸ éè´¦å· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤æ¸ éè´¦å·-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ¸ éè´¦å·ä¸å­å¨ï¼ä¸å¡ç  1302ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## è´¦å·åé¡µæ¥è¯¢

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

æ¸ éè´¦å·åé¡µæ¥è¯¢ï¼æ¯ææè´¦å·åç§°å³é®å­/æ¸ é/å¥åº·ç¶æè¿æ»¤ãéæé channel:listã

<h3 id="è´¦å·åé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|è´¦å·åç§°å³é®å­ï¼æ¨¡ç³å¹éï¼|
|channelId|query|integer(int64)|false|æ¸ é IDï¼å¯¹åº channel.idï¼|
|healthStatus|query|integer(int32)|false|å¥åº·ç¶æï¼1æ­£å¸¸/2åé/3å°ç¦|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="è´¦å·åé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultChannelAccount](#schemaresultpageresultchannelaccount)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ç»å®æ¸ éè´¦å·

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
  "accountName": "åçå®æ¹å·",
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

ç»å®æ¸ éè´¦å·ï¼å½å¥è´¦å·åºç¡ä¿¡æ¯ä¸ææéç½® auth_configï¼ç»å®åå¯ç¨äºè¯¥æ¸ éçæ¶æ¯æ¶åä¸äºä»¶æ¥æ¶ãéæé channel:addã
auth_configï¼JSONB å­ç¬¦ä¸²ï¼å­å¨ç»æï¼{"accessToken":"è®¿é®ä»¤ç","refreshToken":"å·æ°ä»¤ç","expireAt":"è¿ææ¶é´epochæ¯«ç§"}ï¼æé³å¦å« appId/secretï¼ä¼ä¸å¾®ä¿¡å¦å« corpId/corpSecretï¼ææ¸ éå¯æ©å±ã

è¯·æ±ç¤ºä¾ï¼
```json
{
  "channelId": 1,
  "accountName": "åçå®æ¹å·",
  "externalId": "douyin_open_id_123",
  "authConfig": "{\"accessToken\":\"at_xxx\",\"refreshToken\":\"rt_xxx\",\"expireAt\":\"1780000000000\",\"appId\":\"appid\",\"secret\":\"secret\"}",
  "healthStatus": 1,
  "riskLevel": 0
}
```
è¿åç¤ºä¾ï¼
```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "tenantId": 1,
    "channelId": 1,
    "accountName": "åçå®æ¹å·",
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
  "accountName": "åçå®æ¹å·",
  "externalId": "douyin_open_id_xxx",
  "authConfig": "string",
  "healthStatus": 1,
  "riskLevel": 0
}
```

<h3 id="ç»å®æ¸ éè´¦å·-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[ChannelAccount](#schemachannelaccount)|true|none|

> Example responses

> 400 Response

<h3 id="ç»å®æ¸ éè´¦å·-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è´¦å·åç§°/æ¸ éä¸è½ä¸ºç©º|[ResultChannelAccount](#schemaresultchannelaccount)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å·æ°ææä»¤ç

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

æå¨å·æ°æ¸ éè´¦å·ç access_tokenï¼token è¿æææå¨è§¦ååºæ¯ï¼ãå·æ°æåè¿å trueï¼å¤±è´¥è¿å falseï¼å¦å¯é¥éè¯¯/æ¥å£å¼å¸¸ï¼ãéæé channel:tokenã

<h3 id="å·æ°ææä»¤ç-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ¸ éè´¦å· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å·æ°ææä»¤ç-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ¸ éè´¦å·ä¸å­å¨ï¼ä¸å¡ç  1302ï¼|[ResultBoolean](#schemaresultboolean)|
|502|[Bad Gateway](https://tools.ietf.org/html/rfc7231#section-6.6.3)|æ¸ éå¼æ¾å¹³å° API è°ç¨å¤±è´¥ï¼ä¸å¡ç  1304ï¼|[ResultBoolean](#schemaresultboolean)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ´æ°å¥åº·ç¶æï¼ç¶æçæ§åè°ï¼

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

æ´æ°æ¸ éè´¦å·å¥åº·ç¶æ/é£æ§ç­çº§ï¼ä¾å®æ¶å·¡æ£æäººå·¥æ è®°è°ç¨ï¼ãéæé channel:tokenã

<h3 id="æ´æ°å¥åº·ç¶æï¼ç¶æçæ§åè°ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æ¸ éè´¦å· ID|
|healthStatus|query|integer(int32)|true|å¥åº·ç¶æï¼1æ­£å¸¸/2åé/3å°ç¦|
|riskLevel|query|integer(int32)|false|é£æ§ç­çº§ï¼0ä½/1ä¸­/2é«ï¼å¯ç©ºï¼ä¼ ç©ºåä¸æ´æ°ï¼|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="æ´æ°å¥åº·ç¶æï¼ç¶æçæ§åè°ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|æ¸ éè´¦å·ä¸å­å¨ï¼ä¸å¡ç  1302ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">çº¿ç´¢ç®¡ç</h1>

## åé¡µæ¥è¯¢çº¿ç´¢

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

æéï¼lead:listãæç§æ·/ç¶æ/æåç»´åº¦åé¡µæ¥è¯¢çº¿ç´¢ï¼status åå¼ï¼newæ°çº¿ç´¢/assignedå·²åé/contactingè·è¿ä¸­/effectiveææ/quotedå·²æ¥ä»·/opportunityåæº/lostæµå¤±ï¼intent åå¼ï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»ã

<h3 id="åé¡µæ¥è¯¢çº¿ç´¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|false|ç§æ· IDï¼å¹³å°ç®¡çåå¯æå®ï¼ä¸ºç©ºé»è®¤å½åç§æ·ï¼|
|status|query|string|false|çº¿ç´¢ç¶æï¼newæ°çº¿ç´¢/assignedå·²åé/contactingè·è¿ä¸­/effectiveææ/quotedå·²æ¥ä»·/opportunityåæº/lostæµå¤±|
|intent|query|string|false|å®¢æ·æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åé¡µæ¥è¯¢çº¿ç´¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultLead](#schemaresultpageresultlead)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºçº¿ç´¢

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

æéï¼lead:listãåå»ºçº¿ç´¢ï¼æ¸ éæ¥å¥/åå¸­æå¨å½å¥ï¼ï¼intent åå¼ï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»ï¼status é»è®¤ newæ°çº¿ç´¢ã

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

<h3 id="åå»ºçº¿ç´¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Lead](#schemalead)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºçº¿ç´¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|tenantId ä¸è½ä¸ºç©º|[ResultLead](#schemaresultlead)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ¥è¯¢çº¿ç´¢è¯¦æ

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

æéï¼lead:listãæ ID æ¥è¯¢çº¿ç´¢è¯¦æï¼å«æ½åå³é®å­æ®µï¼extraï¼ã

<h3 id="æ¥è¯¢çº¿ç´¢è¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|çº¿ç´¢ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ¥è¯¢çº¿ç´¢è¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultLead](#schemaresultlead)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">å®æ¶ä»»å¡</h1>

## æå¨è§¦åä»»å¡

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

éæé job:runï¼ç«å³æ§è¡ä¸æ¬¡æå®ä»»å¡

<h3 id="æå¨è§¦åä»»å¡-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|code|path|string|true|ä»»å¡ç¼ç |
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="æå¨è§¦åä»»å¡-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ä»»å¡ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ä»»å¡åè¡¨ï¼å«è¿è¡ç¶æï¼

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

éæé job:listï¼è¿åå¨é¨å·²æ³¨åå®æ¶ä»»å¡åå¶å½åè¿è¡ç¶æ

<h3 id="ä»»å¡åè¡¨ï¼å«è¿è¡ç¶æï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ä»»å¡åè¡¨ï¼å«è¿è¡ç¶æï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListJobInfo](#schemaresultlistjobinfo)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ä»»å¡æ§è¡è®°å½åé¡µæ¥è¯¢

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

éæé job:list

<h3 id="ä»»å¡æ§è¡è®°å½åé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|jobCode|query|string|false|ä»»å¡ç¼ç |
|result|query|integer(int32)|false|æ§è¡ç»æï¼1 æå / 0 å¤±è´¥|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ä»»å¡æ§è¡è®°å½åé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultJobLog](#schemaresultpageresultjoblog)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">èº«ä»½å½ä¸</h1>

## çº¿ç´¢åå¹¶ï¼æ¬¡è¦çº¿ç´¢å½å¹¶å°ä¸»çº¿ç´¢ï¼

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

æéï¼identity:mergeãå°æ¬¡è¦çº¿ç´¢çèº«ä»½æ å°ãè·è¿è®°å½ç­å½å¹¶å°ä¸»çº¿ç´¢ï¼å¹¶å é¤æ¬¡è¦çº¿ç´¢ï¼è¿ååå¹¶åçä¸»çº¿ç´¢ IDã

<h3 id="çº¿ç´¢åå¹¶ï¼æ¬¡è¦çº¿ç´¢å½å¹¶å°ä¸»çº¿ç´¢ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|primaryId|path|integer(int64)|true|ä¸»çº¿ç´¢ IDï¼ä¿çï¼|
|secondaryId|path|integer(int64)|true|æ¬¡è¦çº¿ç´¢ IDï¼è¢«åå¹¶ï¼|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="çº¿ç´¢åå¹¶ï¼æ¬¡è¦çº¿ç´¢å½å¹¶å°ä¸»çº¿ç´¢ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|ä¸è½åå¹¶èªèº«|[ResultLong](#schemaresultlong)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|çº¿ç´¢ä¸å­å¨ï¼ä¸å¡ç  1201ï¼|[ResultLong](#schemaresultlong)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æå¨ç»å®èº«ä»½å°çº¿ç´¢

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

æéï¼identity:editãå°ä¸æ¡èº«ä»½ï¼ææºå·/é®ç®±/ç¤¾åªID/ä¼å¾®ID/WhatsApp/ä¼ä¸ååï¼æå¨ç»å®å°æå®çº¿ç´¢ï¼tenantId ä¸ºç©ºæ¶é»è®¤å½åç§æ·ã

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

<h3 id="æå¨ç»å®èº«ä»½å°çº¿ç´¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[BindRequest](#schemabindrequest)|true|none|

> Example responses

> 400 Response

<h3 id="æå¨ç»å®èº«ä»½å°çº¿ç´¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|èº«ä»½ç±»åä¸å¼ä¸è½ä¸ºç©º|[ResultVoid](#schemaresultvoid)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|çº¿ç´¢ä¸å­å¨ï¼ä¸å¡ç  1201ï¼|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ¥è¯¢å®ä½çèº«ä»½æ å°åè¡¨

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

æéï¼identity:listãæå®ä½ç±»å+å®ä½ ID æ¥è¯¢å¶ä¸ç»å®çå¨é¨èº«ä»½æ å°ï¼å«å¹éç½®ä¿¡åº¦ï¼ã

<h3 id="æ¥è¯¢å®ä½çèº«ä»½æ å°åè¡¨-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|entityType|query|string|true|å®ä½ç±»åï¼leadçº¿ç´¢/contactèç³»äºº/customerå®¢æ·|
|entityId|query|integer(int64)|true|å®ä½ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ¥è¯¢å®ä½çèº«ä»½æ å°åè¡¨-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListIdentityMapping](#schemaresultlistidentitymapping)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## èº«ä»½è¯¦æ

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

æéï¼identity:listãæ ID æ¥è¯¢èº«ä»½è¯¦æï¼identity_type åå¼ï¼mobileææºå·/emailé®ç®±/socialç¤¾åªID/wecomä¼å¾®ID/whatsapp/domainä¼ä¸ååï¼ã

<h3 id="èº«ä»½è¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|èº«ä»½ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="èº«ä»½è¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultIdentity](#schemaresultidentity)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤èº«ä»½æ å°

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

æéï¼identity:deleteãææ å° ID å é¤èº«ä»½ä¸å®ä½ä¹é´çç»å®å³ç³»ã

<h3 id="å é¤èº«ä»½æ å°-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|èº«ä»½æ å° ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å é¤èº«ä»½æ å°-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|èº«ä»½æ å°ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">è·è¿è®°å½</h1>

## è·è¿è®°å½åå²æ¥è¯¢

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

æéï¼follow:listãæçº¿ç´¢æå®¢æ·ç»´åº¦åé¡µæ¥è¯¢è·è¿åå²ï¼ä¸¤ä¸ªç»´åº¦åå¯ä¸å¡«ï¼æ¥å¨é¨ï¼ã

<h3 id="è·è¿è®°å½åå²æ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|leadId|query|integer(int64)|false|å³èçº¿ç´¢ IDï¼æçº¿ç´¢ç»´åº¦æ¥è·è¿è®°å½ï¼|
|customerId|query|integer(int64)|false|å³èå®¢æ· IDï¼æå®¢æ·ç»´åº¦æ¥è·è¿è®°å½ï¼|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="è·è¿è®°å½åå²æ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultFollowUp](#schemaresultpageresultfollowup)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ°å¢è·è¿è®°å½ï¼èªå¨åæ­¥çº¿ç´¢è·è¿ç¶æï¼

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
  "content": "å®¢æ·å¯¹æ¥ä»·æ¹æ¡ææåï¼çº¦ä¸å¨é¢è°",
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

æéï¼follow:addãæ°å¢ä¸æ¡è·è¿è®°å½ï¼è¥å³èçº¿ç´¢ï¼åå»ºåèªå¨å°çº¿ç´¢ç¶æåæ­¥ä¸ºè·è¿ä¸­ï¼contactingï¼ã

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
  "content": "å®¢æ·å¯¹æ¥ä»·æ¹æ¡ææåï¼çº¦ä¸å¨é¢è°",
  "method": "wechat",
  "nextTime": "2019-08-24T14:15:22Z"
}
```

<h3 id="æ°å¢è·è¿è®°å½ï¼èªå¨åæ­¥çº¿ç´¢è·è¿ç¶æï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[FollowUp](#schemafollowup)|true|none|

> Example responses

> 400 Response

<h3 id="æ°å¢è·è¿è®°å½ï¼èªå¨åæ­¥çº¿ç´¢è·è¿ç¶æï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è·è¿åå®¹ä¸è½ä¸ºç©º/çº¿ç´¢æå®¢æ·è³å°å³èä¸ä¸ª|[ResultFollowUp](#schemaresultfollowup)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">æä»¶ç®¡ç</h1>

## ä¸ä¼ æä»¶

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

multipart/form-data è¡¨åä¸ä¼ ï¼file å­æ®µä¸ºæä»¶æµï¼è¿åæä»¶è®°å½ä¸è®¿é®å°å

> Body parameter

```json
{
  "file": "string"
}
```

<h3 id="ä¸ä¼ æä»¶-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|object|false|none|
|» file|body|string(binary)|true|æä»¶|

> Example responses

> 400 Response

<h3 id="ä¸ä¼ æä»¶-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|ä¸ä¼ æä»¶ä¸è½ä¸ºç©º|[ResultFileUploadVO](#schemaresultfileuploadvo)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|æä»¶ä¸ä¼ å¤±è´¥ï¼ä¸å¡ç  1701ï¼|[ResultFileUploadVO](#schemaresultfileuploadvo)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æä»¶åè¡¨åé¡µ

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

éæé file:list

<h3 id="æä»¶åè¡¨åé¡µ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æä»¶åè¡¨åé¡µ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultFileRecord](#schemaresultpageresultfilerecord)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ä¸è½½/é¢è§æä»¶

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

ææä»¶ ID è¿åæä»¶åå®¹ï¼ååºå¤´å¸¦åå§æä»¶åä¸ Content-Typeï¼

<h3 id="ä¸è½½/é¢è§æä»¶-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æä»¶è®°å½ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ä¸è½½/é¢è§æä»¶-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å é¤æä»¶

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

éæé file:delete

<h3 id="å é¤æä»¶-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|æä»¶è®°å½ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å é¤æä»¶-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|æä»¶ä¸å­å¨|[ResultVoid](#schemaresultvoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">ä¼è¯</h1>

## ä¼è¯åé¡µæ¥è¯¢

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

ä¼è¯ç®¡ç - ä¼è¯åé¡µæ¥è¯¢ï¼æé conversation:listï¼ï¼æ¯æå³é®å­ï¼ä¼è¯ç±»å/IDï¼ãç¶æãå¤çäººè¿æ»¤ã

status æä¸¾ï¼activeè¿è¡ä¸­/transferredå·²è½¬äººå·¥/closedå·²å³é­/archivedå·²å½æ¡£ã

<h3 id="ä¼è¯åé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼æ¨¡ç³å¹éä¼è¯ç±»å/ä¼è¯ IDï¼|
|status|query|string|false|ä¼è¯ç¶æï¼activeè¿è¡ä¸­/transferredå·²è½¬äººå·¥/closedå·²å³é­/archivedå·²å½æ¡£|
|assignedTo|query|integer(int64)|false|å½åäººå·¥å¤çäºº ID|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ä¼è¯åé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultConversation](#schemaresultpageresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå»ºä¼è¯

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

ä¼è¯ç®¡ç - åå»ºä¼è¯ï¼æé conversation:editï¼ï¼æ¿æ¥ä¼å¾®/WhatsApp/ç§ä¿¡å®¢æ·ï¼æ¯ä¼è¯çå½å¨æèµ·ç¹ã

å¿å¡«ï¼tenantIdãleadIdï¼status ä¸ä¼ æ¶é»è®¤ activeã

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

<h3 id="åå»ºä¼è¯-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Conversation](#schemaconversation)|true|none|

> Example responses

> 400 Response

<h3 id="åå»ºä¼è¯-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|tenantId ä¸ leadId ä¸è½ä¸ºç©º|[ResultConversation](#schemaresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## è½¬äººå·¥å¹¶çæäº¤æ¥åï¼ä¸ä¼  operatorId æ¶èªå¨åééå®ï¼

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

ä¼è¯ç®¡ç - è½¬äººå·¥å¹¶çæäº¤æ¥åï¼æé conversation:editï¼ï¼ä¼è¯ç¶æç½®ä¸º transferredãassignedTo æåæ¥æåå¸­ï¼è¿å AI æè¦ + æè¿æå + ç¼ºå¤±å­æ®µ + æ¨èè¯æ¯çäº¤æ¥åã

operatorId ä¸ä¼ æ¶ä¼åçº¿ç´¢è´è´£äººï¼å¦åèµ°åéå¼æèªå¨åéï¼æ å¯ç¨åå¸­æ¶è¿å 400ã

è¯·æ±ç¤ºä¾ï¼
```
POST /api/conversations/100/transfer?operatorId=8
```
è¿åç¤ºä¾ï¼
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
    "summary": "å®¢æ·å¨è¯¢äº§åæ¥ä»·ï¼å·²ç¡®è®¤åºç¨åºæ¯ï¼éè´­æ°éä¸é¢ç®å¾è¡¥å",
    "summarySource": "ai",
    "intent": "quote",
    "confidence": 0.82,
    "evidence": "å®¢æ·æå°éè¦ 500 å°ï¼å¸ææ¬å¨åæ¿å°æ¥ä»·",
    "missingFields": ["qty", "budget"],
    "recommendedReply": "æ¨å¥½ï¼ææ¯äººå·¥é¡¾é®ï¼å·²ä¸ºæ¨æ´çæ¥ä»·éæ±ãä¸ºåç¡®æ¥ä»·ï¼è¯·è¡¥åï¼åºç¨åºæ¯ãéè´­æ°éãé¢ç®èå´ãææäº¤æï¼æå°½å¿«ç»æ¨æ­£å¼æ¥ä»·ã",
    "nextSteps": [
      "AI å·²çæå¯¹è¯æè¦ï¼åå¸­ç¡®è®¤ååå¤å®¢æ·",
      "è¡¥é½æ¥ä»·å³é®å­æ®µï¼qty/budgetï¼",
      "å­æ®µé½å¨åçææ¥ä»·åï¼è·è¿æ¥ä»·æå",
      "ä½ç½®ä¿¡åº¦/å¤æé®é¢å·²è½¬äººå·¥ï¼åå¸­ä¼åååº"
    ],
    "transferredAt": "2026-08-03 14:30:00"
  }
}
```

<h3 id="è½¬äººå·¥å¹¶çæäº¤æ¥åï¼ä¸ä¼ -operatorid-æ¶èªå¨åééå®ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ä¼è¯ ID|
|operatorId|query|integer(int64)|false|æ¥æåå¸­ IDï¼å¯ç©ºï¼ä¸ä¼ æ¶èªå¨åéï¼|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="è½¬äººå·¥å¹¶çæäº¤æ¥åï¼ä¸ä¼ -operatorid-æ¶èªå¨åééå®ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|ææ å¯æ¥æçå¨çº¿åå¸­/operatorId ä¸è½ä¸ºç©º|[ResultTransferPackage](#schemaresulttransferpackage)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ä¼è¯ä¸å­å¨ï¼ä¸å¡ç  1401ï¼|[ResultTransferPackage](#schemaresulttransferpackage)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ä¼è¯æ¶æ¯åé¡µï¼åå²æ¼«æ¸¸ï¼

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

ä¼è¯ç®¡ç - æ¶æ¯åé¡µæ¥è¯¢ï¼æé message:listï¼ï¼å¯æåéæ¹è¿æ»¤ï¼æ¶æ¯æ ID ååºè¿åã

senderType æä¸¾ï¼customerå®¢æ·/aiæºå¨äºº/humanäººå·¥/systemç³»ç»ã

<h3 id="ä¼è¯æ¶æ¯åé¡µï¼åå²æ¼«æ¸¸ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ä¼è¯ ID|
|senderType|query|string|false|åéæ¹ï¼customerå®¢æ·/aiæºå¨äºº/humanäººå·¥/systemç³»ç»|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ä¼è¯æ¶æ¯åé¡µï¼åå²æ¼«æ¸¸ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultMessage](#schemaresultpageresultmessage)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## è¿½å æ¶æ¯

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

ä¼è¯ç®¡ç - è¿½å ä¸æ¡æ¶æ¯ï¼æé message:addï¼ï¼ç¨äºäººå·¥/ç³»ç»è¡¥å½æ¶æ¯ã

è¯·æ±ç¤ºä¾ï¼
```json
{
  "tenantId": 1,
  "conversationId": 100,
  "senderType": "human",
  "content": "æ¨å¥½ï¼å³äºæ¥ä»·éæ±æå·²æ´çï¼è¯·è¡¥åéè´­æ°é",
  "msgType": "text",
  "aiGenerated": false
}
```
è¿åç¤ºä¾ï¼
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1001,
    "tenantId": 1,
    "conversationId": 100,
    "senderType": "human",
    "content": "æ¨å¥½ï¼å³äºæ¥ä»·éæ±æå·²æ´çï¼è¯·è¡¥åéè´­æ°é",
    "msgType": "text",
    "aiGenerated": false,
    "createdAt": "2026-08-03 12:00:00",
    "updatedAt": "2026-08-03 12:00:00"
  }
}
```
æ³¨æï¼conversationId ä»¥è·¯å¾åæ°ä¸ºåï¼è¯·æ±ä½ä¸­çå¼ä¼è¢«è¦çï¼msgType ä¸ä¼ é»è®¤ textã

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

<h3 id="è¿½å æ¶æ¯-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ä¼è¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[Message](#schemamessage)|true|none|

> Example responses

> 400 Response

<h3 id="è¿½å æ¶æ¯-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|conversationId/æ¶æ¯åå®¹ä¸è½ä¸ºç©º|[ResultMessage](#schemaresultmessage)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ä¼è¯ä¸å­å¨ï¼ä¸å¡ç  1401ï¼|[ResultMessage](#schemaresultmessage)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å³é­ä¼è¯

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

ä¼è¯ç®¡ç - å³é­ä¼è¯ï¼æé conversation:editï¼ï¼å·²å½æ¡£ä¼è¯ä¸å¯å³é­ï¼å³é­åç¶æç½®ä¸º closedã

<h3 id="å³é­ä¼è¯-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ä¼è¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 400 Response

<h3 id="å³é­ä¼è¯-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|å·²å½æ¡£ä¼è¯ä¸è½å³é­|[ResultConversation](#schemaresultconversation)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ä¼è¯ä¸å­å¨ï¼ä¸å¡ç  1401ï¼|[ResultConversation](#schemaresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å½æ¡£ä¼è¯

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

ä¼è¯ç®¡ç - å½æ¡£ä¼è¯ï¼æé conversation:editï¼ï¼å½æ¡£åç¶æç½®ä¸º archivedã

<h3 id="å½æ¡£ä¼è¯-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ä¼è¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 404 Response

<h3 id="å½æ¡£ä¼è¯-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ä¼è¯ä¸å­å¨ï¼ä¸å¡ç  1401ï¼|[ResultConversation](#schemaresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## AI æ¥å¾ï¼æ¶æ¯è½åº â æåå¤å® â çæåå¤ï¼

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

ä¼è¯ç®¡ç - AI æ¥å¾å¥å£ï¼æé message:addï¼ï¼å®¢æ·æ¶æ¯è½åº â æåå¤å® â çæ AI åå¤ï¼AI ä¸å¯ç¨éçº§è§åè¯æ¯ï¼â åå¤è½åºã

è¯·æ±ä½ä¸º JSON å­ç¬¦ä¸²ï¼è£¸å­ç¬¦ä¸²ï¼ï¼ä¾å¦ï¼"ææ³äºè§£è´µå¸äº§åçæ¥ä»·"
è¿å AiReply å­æ®µï¼content åå¤åå®¹ãintent æåï¼quote/sample/selection/otherï¼ãconfidence ç½®ä¿¡åº¦ãshouldTransfer æ¯å¦å»ºè®®è½¬äººå·¥ã

> Body parameter

```json
"string"
```

<h3 id="ai-æ¥å¾ï¼æ¶æ¯è½åº-â-æåå¤å®-â-çæåå¤ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ä¼è¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|string|true|none|

> Example responses

> 404 Response

<h3 id="ai-æ¥å¾ï¼æ¶æ¯è½åº-â-æåå¤å®-â-çæåå¤ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ä¼è¯ä¸å­å¨ï¼ä¸å¡ç  1401ï¼|[ResultAiReply](#schemaresultaireply)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ä¼è¯è¯¦æ

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

ä¼è¯ç®¡ç - ä¼è¯è¯¦æï¼æé conversation:listï¼ã

<h3 id="ä¼è¯è¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ä¼è¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ä¼è¯è¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultConversation](#schemaresultconversation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ¥è¯¢äº¤æ¥åï¼åå¸­æ¥æåæ¥çï¼

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

ä¼è¯ç®¡ç - æ¥è¯¢äº¤æ¥åï¼æé conversation:listï¼ï¼åå¸­æ¥æåæ¥çä¼è¯ä¸ä¸æï¼å¯¹è¯æè¦ + æå + ç¼ºå¤±å­æ®µ + æ¨èè¯æ¯ï¼ã

è¿åç¤ºä¾ï¼
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
    "summary": "AI æè¦æå¡æä¸å¯ç¨ï¼è¯·åå¸­æ¥çæè¿ 20 æ¡æ¶æ¯è®°å½ã",
    "summarySource": "fallback",
    "intent": "selection",
    "confidence": 0.76,
    "evidence": "å®¢æ·è¡¨ç¤ºå¯¹åå·éåä¸ç¡®å®ï¼éè¦å¯¹æ¯æ¨è",
    "missingFields": ["scene", "qty", "budget", "lead_time", "model"],
    "recommendedReply": "æ¨å¥½ï¼å·²æ¶å°æ¨çéåéæ±ãä¸ºç»åºç²¾åæ¨èï¼è¯·è¡¥ååºç¨åºæ¯ä¸å·¥åµè¦æ±ï¼æä»¬é©¬ä¸ä¸ºæ¨éåã",
    "nextSteps": [
      "AI å·²çæå¯¹è¯æè¦ï¼åå¸­ç¡®è®¤ååå¤å®¢æ·",
      "è¾åºéåå¯¹æ¯è¡¨ï¼æ¨è 2-3 æ¬¾ï¼",
      "éåç¡®è®¤åå¼å¯¼æ¥ä»·",
      "ä½ç½®ä¿¡åº¦/å¤æé®é¢å·²è½¬äººå·¥ï¼åå¸­ä¼åååº"
    ],
    "transferredAt": "2026-08-03 14:30:00"
  }
}
```

<h3 id="æ¥è¯¢äº¤æ¥åï¼åå¸­æ¥æåæ¥çï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ä¼è¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ¥è¯¢äº¤æ¥åï¼åå¸­æ¥æåæ¥çï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultTransferPackage](#schemaresulttransferpackage)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## è½¬äººå·¥æ¡ä»¶è¯ä¼°

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

ä¼è¯ç®¡ç - è½¬äººå·¥æ¡ä»¶è¯ä¼°ï¼æé conversation:listï¼ï¼åºäºæè¿æåå¤å®ç»æå¤æ­æ¯å¦éè¦è½¬äººå·¥ã

è§¦åæ¡ä»¶ï¼æåä¸º other / ç½®ä¿¡åº¦ä½äº 0.5 / å°æ æåå¤å®ã
è¿å TransferEvaluation å­æ®µï¼shouldTransfer æ¯å¦å»ºè®®è½¬äººå·¥ãintent æåãconfidence ç½®ä¿¡åº¦ãreason å¤å®åå ã

<h3 id="è½¬äººå·¥æ¡ä»¶è¯ä¼°-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|ä¼è¯ ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="è½¬äººå·¥æ¡ä»¶è¯ä¼°-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultTransferEvaluation](#schemaresulttransferevaluation)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">æ¸ éäºä»¶</h1>

## æ¥æ¶æ¸ éäºä»¶ï¼Webhookï¼

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

ä¸æ¸ é Webhook ç»ä¸å¥å£ï¼æ¥æ¶æé³/è§é¢å·/ä¼ä¸å¾®ä¿¡ç­æ¸ éæ¨éçäºä»¶ï¼æ (tenantId+channelAccountId+externalEventId) å¹ç­å»éåå¥åºå¹¶åå¸å¼æ­¥äºä»¶ã
è¿å true è¡¨ç¤ºé¦æ¬¡æ¥æ¶å¹¶å·²å¥åºï¼è¿å false è¡¨ç¤ºéå¤äºä»¶è¢«å¿½ç¥ï¼ä¸éå¤è½åºï¼ã
eventType åå¼ï¼commentè¯è®º/dmç§ä¿¡/formè¡¨å/clickç¹å»/leadçº¿ç´¢ã
rawPayloadï¼JSONBï¼å­å¨æ¸ éåè°çå®æ´åå§ JSONï¼å­æ®µéæ¸ éä¸äºä»¶ç±»åä¸åã
è¯¥æ¥å£ä¸ºæ¸ éå¹³å°åè°å¥å£ï¼æ ç»å½ä¸æéæ ¡éªï¼æ¬çæ¬ä¸ºæ¨¡æå®ç°ï¼æªåæ¸ éç­¾åæ ¡éªï¼ã

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

<h3 id="æ¥æ¶æ¸ éäºä»¶ï¼webhookï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[ChannelEvent](#schemachannelevent)|true|none|

> Example responses

> 200 Response

<h3 id="æ¥æ¶æ¸ éäºä»¶ï¼webhookï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultBoolean](#schemaresultboolean)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">è®¤è¯</h1>

## ç»å½ï¼ç­¾å JWTï¼

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

å¬å¼æ¥å£ï¼æ éç»å½ï¼æåè¿å JWT ä»¤ç

> Body parameter

```json
{
  "tenantId": 0,
  "mobile": "13800138000",
  "password": "string"
}
```

<h3 id="ç»å½ï¼ç­¾å-jwtï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|
|body|body|[LoginRequest](#schemaloginrequest)|true|none|

> Example responses

> 400 Response

<h3 id="ç»å½ï¼ç­¾å-jwtï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|è´¦å·æå¯ç éè¯¯/è´¦å·å·²ç¦ç¨/åæ°ç¼ºå¤±|[ResultLoginResponse](#schemaresultloginresponse)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|ç§æ·å·²åç¨ï¼ä¸å¡ç  1002ï¼|[ResultLoginResponse](#schemaresultloginresponse)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|ç§æ·ä¸å­å¨ï¼ä¸å¡ç  1001ï¼|[ResultLoginResponse](#schemaresultloginresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">ä¼å¾®ä¾§è¾¹æ </h1>

## æ¨èè¯æ¯

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

ä¼å¾®ä¾§è¾¹æ  - æ¨èè¯æ¯ï¼æé wecom:sidebarï¼ï¼ææåè¿åå»ºè®®è¯æ¯ä¸ç¼ºå¤±å­æ®µï¼intent ä¸ä¼ æ¶åçº¿ç´¢æè¿æåã

intent æä¸¾ï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»ã

<h3 id="æ¨èè¯æ¯-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|externalUserId|query|string|true|ä¼å¾®å¤é¨ç¨æ· ID|
|intent|query|string|false|æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»ï¼å¯ç©ºï¼é»è®¤åçº¿ç´¢æè¿æåï¼|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ¨èè¯æ¯-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultReplySuggestion](#schemaresultreplysuggestion)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å®¢æ·ç»å

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

ä¼å¾®ä¾§è¾¹æ  - å®¢æ·ç»åï¼æé wecom:sidebarï¼ï¼æä¼å¾®å¤é¨ç¨æ· ID èåèº«ä»½âçº¿ç´¢âå®¢æ·ä¿¡æ¯ãæ ç­¾ãæè¿æåã

<h3 id="å®¢æ·ç»å-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|externalUserId|query|string|true|ä¼å¾®å¤é¨ç¨æ· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å®¢æ·ç»å-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultCustomerProfile](#schemaresultcustomerprofile)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## äº§åèµæï¼å¿«æ·åéï¼

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

ä¼å¾®ä¾§è¾¹æ  - äº§åèµæåé¡µï¼æé wecom:sidebarï¼ï¼ä»å±ç¤ºä¸æ¶äº§åï¼ä¾åå¸­å¿«æ·åéã

<h3 id="äº§åèµæï¼å¿«æ·åéï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|äº§ååç§°å³é®å­ï¼å¯ç©ºï¼|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="äº§åèµæï¼å¿«æ·åéï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultProduct](#schemaresultpageresultproduct)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## åå²ä¼è¯

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

ä¼å¾®ä¾§è¾¹æ  - åå²ä¼è¯ï¼æé wecom:sidebarï¼ï¼è¿åè¯¥ä¼å¾®å®¢æ·å³èçä¼è¯åè¡¨ï¼å«æè¿ä¸æ¡æ¶æ¯ï¼ã

<h3 id="åå²ä¼è¯-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|externalUserId|query|string|true|ä¼å¾®å¤é¨ç¨æ· ID|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="åå²ä¼è¯-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListConversationBrief](#schemaresultlistconversationbrief)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">å¥é¤</h1>

## å¥é¤åè¡¨ï¼ä»ä¸æ¶ï¼

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

ç»å½ç¨æ·å³å¯è®¿é®

<h3 id="å¥é¤åè¡¨ï¼ä»ä¸æ¶ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å¥é¤åè¡¨ï¼ä»ä¸æ¶ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListPlan](#schemaresultlistplan)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## å¥é¤è¯¦æ

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

ç»å½ç¨æ·å³å¯è®¿é®

<h3 id="å¥é¤è¯¦æ-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|code|path|string|true|å¥é¤ç¼ç ï¼starter/pro/enterprise|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å¥é¤è¯¦æ-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPlan](#schemaresultplan)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">æ¥å¿ç®¡ç</h1>

## æä½æ¥å¿åé¡µæ¥è¯¢

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

éæé log:list

<h3 id="æä½æ¥å¿åé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|å³é®å­ï¼æä½äººå§å/æä½åå®¹/è¯·æ±å°åæ¨¡ç³å¹éï¼|
|module|query|string|false|ä¸å¡æ¨¡å|
|result|query|integer(int32)|false|æ§è¡ç»æï¼1 æå / 0 å¤±è´¥|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æä½æ¥å¿åé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultOperLog](#schemaresultpageresultoperlog)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## ç»å½æ¥å¿åé¡µæ¥è¯¢

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

éæé log:list

<h3 id="ç»å½æ¥å¿åé¡µæ¥è¯¢-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|mobile|query|string|false|ç»å½ææºå·|
|status|query|integer(int32)|false|ç»å½ç»æï¼1 æå / 0 å¤±è´¥|
|page|query|integer(int64)|false|é¡µç ï¼é»è®¤ 1|
|size|query|integer(int64)|false|æ¯é¡µæ¡æ°ï¼é»è®¤ 20|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="ç»å½æ¥å¿åé¡µæ¥è¯¢-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultPageResultLoginLog](#schemaresultpageresultloginlog)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">åºç¡çæ¿</h1>

## æ¯æ¥è¶å¿ï¼çº¿ç´¢é/ä¼è¯é/AI è§£å³éææ¥èåï¼

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

ææ¥èåçº¿ç´¢/ä¼è¯/AI è§£å³éè¶å¿ï¼åºæ¯ï¼è¶å¿åæä¸è¿è¥å¤ç

<h3 id="æ¯æ¥è¶å¿ï¼çº¿ç´¢é/ä¼è¯é/ai-è§£å³éææ¥èåï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|true|ç§æ· ID|
|from|query|string(date-time)|false|å¼å§æ¶é´ï¼yyyy-MM-dd HH:mm:ssï¼ï¼ä¸ºç©ºåå¨é¨|
|to|query|string(date-time)|false|ç»ææ¶é´ï¼yyyy-MM-dd HH:mm:ssï¼ï¼ä¸ºç©ºåå¨é¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ¯æ¥è¶å¿ï¼çº¿ç´¢é/ä¼è¯é/ai-è§£å³éææ¥èåï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListDashboardTrend](#schemaresultlistdashboardtrend)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## éå®å·¥ä½éç»è®¡ï¼æéå®èååéçº¿ç´¢/è·è¿/æäº¤/ä¼è¯ï¼

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

æéå®èååéçº¿ç´¢/è·è¿/æäº¤/ä¼è¯ï¼åºæ¯ï¼éå®ç»©æç»è®¡ä¸å·¥ä½éè¯ä¼°

<h3 id="éå®å·¥ä½éç»è®¡ï¼æéå®èååéçº¿ç´¢/è·è¿/æäº¤/ä¼è¯ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|true|ç§æ· ID|
|from|query|string(date-time)|false|å¼å§æ¶é´ï¼yyyy-MM-dd HH:mm:ssï¼ï¼ä¸ºç©ºåå¨é¨|
|to|query|string(date-time)|false|ç»ææ¶é´ï¼yyyy-MM-dd HH:mm:ssï¼ï¼ä¸ºç©ºåå¨é¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="éå®å·¥ä½éç»è®¡ï¼æéå®èååéçº¿ç´¢/è·è¿/æäº¤/ä¼è¯ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListSalesWorkloadStat](#schemaresultlistsalesworkloadstat)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## çæ¿æ»è§ï¼çº¿ç´¢é/ä¼è¯é/è½¬äººå·¥æ°/ååºæ¶æ/ææå¯¹è¯ç/æååå¸ï¼

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

ææ¶é´èå´èåçæ¿æ ¸å¿ææ ï¼åºæ¯ï¼ç®¡çåå°é¦é¡µæ°æ®æ»è§

<h3 id="çæ¿æ»è§ï¼çº¿ç´¢é/ä¼è¯é/è½¬äººå·¥æ°/ååºæ¶æ/ææå¯¹è¯ç/æååå¸ï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|true|ç§æ· ID|
|from|query|string(date-time)|false|å¼å§æ¶é´ï¼yyyy-MM-dd HH:mm:ssï¼ï¼ä¸ºç©ºåå¨é¨|
|to|query|string(date-time)|false|ç»ææ¶é´ï¼yyyy-MM-dd HH:mm:ssï¼ï¼ä¸ºç©ºåå¨é¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="çæ¿æ»è§ï¼çº¿ç´¢é/ä¼è¯é/è½¬äººå·¥æ°/ååºæ¶æ/ææå¯¹è¯ç/æååå¸ï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultDashboardOverview](#schemaresultdashboardoverview)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

## æ¸ éè½¬åæ°æ®ï¼ææ¸ éè´¦å·èåäºä»¶/çº¿ç´¢/è½¬åçï¼

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

ææ¸ éè´¦å·èåäºä»¶/çº¿ç´¢/è½¬åçï¼åºæ¯ï¼æ¸ éææåæä¸ææ¾ä¼å

<h3 id="æ¸ éè½¬åæ°æ®ï¼ææ¸ éè´¦å·èåäºä»¶/çº¿ç´¢/è½¬åçï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tenantId|query|integer(int64)|true|ç§æ· ID|
|from|query|string(date-time)|false|å¼å§æ¶é´ï¼yyyy-MM-dd HH:mm:ssï¼ï¼ä¸ºç©ºåå¨é¨|
|to|query|string(date-time)|false|ç»ææ¶é´ï¼yyyy-MM-dd HH:mm:ssï¼ï¼ä¸ºç©ºåå¨é¨|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="æ¸ éè½¬åæ°æ®ï¼ææ¸ éè´¦å·èåäºä»¶/çº¿ç´¢/è½¬åçï¼-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ResultListChannelConversionStat](#schemaresultlistchannelconversionstat)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth
</aside>

<h1 id="aicrm---">æ¸ éç®¡ç</h1>

## å¯ç¨æ¸ éåè¡¨ï¼åç«¯ä¸æï¼

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

è·åå¨é¨å¯ç¨ç¶æçæ¸ éå®ä¹åè¡¨ï¼ä¾åç«¯ä¸æ/ç­éä½¿ç¨ãéæé channel:listã
æ¸ éç¼ç  code åå¼ï¼douyinæé³ / video_channelè§é¢å· / tiktok TikTok / wecomä¼ä¸å¾®ä¿¡ / whatsapp WhatsAppã

<h3 id="å¯ç¨æ¸ éåè¡¨ï¼åç«¯ä¸æï¼-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Tenant-Id|header|string|false|ç§æ· IDï¼å¤ç§æ·éç¦»åºæ¯å¿å¡«ï¼æªç»å½ç´è¿è°è¯æ¶ä½¿ç¨ï¼|

> Example responses

> 200 Response

<h3 id="å¯ç¨æ¸ éåè¡¨ï¼åç«¯ä¸æï¼-responses">Responses</h3>

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

åå¸­/åå·¥

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|name|string|false|none|å§å|
|mobile|string|false|none|ææºå·|
|email|string|false|none|é®ç®±|
|roleCode|string|false|none|è§è²ç¼ç ï¼sales/supervisor/admin|
|status|integer(int32)|false|none|ç¶æï¼1å¯ç¨/0åç¨|
|lastActiveAt|string(date-time)|false|none|æåæ´»è·æ¶é´|

<h2 id="tocS_ResultUser">ResultUser</h2>
<!-- backwards compatibility -->
<a id="schemaresultuser"></a>
<a id="schema_ResultUser"></a>
<a id="tocSresultuser"></a>
<a id="tocsresultuser"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[User](#schemauser)|false|none|åå¸­/åå·¥|

<h2 id="tocS_ResultVoid">ResultVoid</h2>
<!-- backwards compatibility -->
<a id="schemaresultvoid"></a>
<a id="schema_ResultVoid"></a>
<a id="tocSresultvoid"></a>
<a id="tocsresultvoid"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {}
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|object|false|none|ä¸å¡æ°æ®|

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

ç§æ·ï¼ä¼ä¸ï¼ââ è®¢éæ¨¡å¼æ ¹å®ä½

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|name|string|false|none|ä¼ä¸åç§°|
|planCode|string|false|none|å¥é¤ç¼ç ï¼starter/pro/enterprise|
|seatCount|integer(int32)|false|none|åå¸­æ°ä¸é|
|aiQuotaMonth|integer(int64)|false|none|æåº¦ AI è°ç¨é¢åº¦|
|expireAt|string(date-time)|false|none|å°ææ¶é´ï¼ä¸ºç©ºè¡¨ç¤ºé¿æææ|
|contactName|string|false|none|å¹³å°å¯¹æ¥èç³»äºº|
|contactMobile|string|false|none|å¹³å°å¯¹æ¥èç³»çµè¯|
|status|integer(int32)|false|none|ç¶æï¼1å¯ç¨/0åç¨|

<h2 id="tocS_ResultTenant">ResultTenant</h2>
<!-- backwards compatibility -->
<a id="schemaresulttenant"></a>
<a id="schema_ResultTenant"></a>
<a id="tocSresulttenant"></a>
<a id="tocsresulttenant"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Tenant](#schematenant)|false|none|ç§æ·ï¼ä¼ä¸ï¼ââ è®¢éæ¨¡å¼æ ¹å®ä½|

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
  "name": "é«æåå®¢æ·",
  "color": "#FF5733",
  "remark": "string",
  "status": 1
}

```

æ ç­¾ä¿¡æ¯

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|name|string|false|none|æ ç­¾åç§°|
|color|string|false|none|æ ç­¾é¢è²ï¼åç«¯å±ç¤ºï¼å¦ #FF5733ï¼|
|remark|string|false|none|å¤æ³¨|
|status|integer(int32)|false|none|ç¶æï¼1 å¯ç¨ / 0 åç¨|

<h2 id="tocS_ResultCustomerTag">ResultCustomerTag</h2>
<!-- backwards compatibility -->
<a id="schemaresultcustomertag"></a>
<a id="schema_ResultCustomerTag"></a>
<a id="tocSresultcustomertag"></a>
<a id="tocsresultcustomertag"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "name": "é«æåå®¢æ·",
    "color": "#FF5733",
    "remark": "string",
    "status": 1
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[CustomerTag](#schemacustomertag)|false|none|æ ç­¾ä¿¡æ¯|

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
  "ruleName": "è¯åé«äº80èªå¨ææ ",
  "tagId": 0,
  "conditionField": "score",
  "conditionOp": "gte",
  "conditionValue": "80",
  "status": 1
}

```

èªå¨æ ç­¾è§åä¿¡æ¯

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|ruleName|string|false|none|è§ååç§°|
|tagId|integer(int64)|false|none|å½ä¸­åæçæ ç­¾ ID|
|conditionField|string|false|none|æ¡ä»¶å­æ®µï¼scoreè¯å/intent_levelæåç­çº§/stageå®¢æ·é¶æ®µ/industryè¡ä¸/regionå°åº/sourceæ¥æº|
|conditionOp|string|false|none|æ¡ä»¶æä½ç¬¦ï¼gtå¤§äº/gteå¤§äºç­äº/ltå°äº/lteå°äºç­äº/eqç­äº/containsåå«|
|conditionValue|string|false|none|æ¡ä»¶å¼ï¼ä¸ condition_field å¯¹åºï¼å¦è¯åéå¼ãé¶æ®µåå¼ãè¡ä¸åç­ï¼|
|status|integer(int32)|false|none|ç¶æï¼1 å¯ç¨ / 0 åç¨|

<h2 id="tocS_ResultCustomerTagRule">ResultCustomerTagRule</h2>
<!-- backwards compatibility -->
<a id="schemaresultcustomertagrule"></a>
<a id="schema_ResultCustomerTagRule"></a>
<a id="tocSresultcustomertagrule"></a>
<a id="tocsresultcustomertagrule"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "ruleName": "è¯åé«äº80èªå¨ææ ",
    "tagId": 0,
    "conditionField": "score",
    "conditionOp": "gte",
    "conditionValue": "80",
    "status": 1
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[CustomerTagRule](#schemacustomertagrule)|false|none|èªå¨æ ç­¾è§åä¿¡æ¯|

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
  "title": "å¼åºç½-æ åç",
  "category": "general",
  "content": "string",
  "status": 1
}

```

è¯æ¯åº

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|title|string|false|none|è¯æ¯æ é¢|
|category|string|false|none|åºæ¯åç±»ï¼generaléç¨/quoteæ¥ä»·/selectionéå/objectionå¼è®®/followè·è¿/openingå¼åº|
|content|string|false|none|è¯æ¯åå®¹|
|status|integer(int32)|false|none|ç¶æï¼1 å¯ç¨ / 0 åç¨|

<h2 id="tocS_ResultSpeechLibrary">ResultSpeechLibrary</h2>
<!-- backwards compatibility -->
<a id="schemaresultspeechlibrary"></a>
<a id="schema_ResultSpeechLibrary"></a>
<a id="tocSresultspeechlibrary"></a>
<a id="tocsresultspeechlibrary"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "title": "å¼åºç½-æ åç",
    "category": "general",
    "content": "string",
    "status": 1
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[SpeechLibrary](#schemaspeechlibrary)|false|none|è¯æ¯åº|

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

è§è²ï¼ç§æ·åï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|code|string|false|none|è§è²ç¼ç ï¼admin/sales/supervisor æèªå®ä¹|
|name|string|false|none|è§è²åç§°|
|description|string|false|none|è§è²æè¿°|
|status|integer(int32)|false|none|ç¶æï¼1å¯ç¨/0åç¨|

<h2 id="tocS_ResultRole">ResultRole</h2>
<!-- backwards compatibility -->
<a id="schemaresultrole"></a>
<a id="schema_ResultRole"></a>
<a id="tocSresultrole"></a>
<a id="tocsresultrole"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Role](#schemarole)|false|none|è§è²ï¼ç§æ·åï¼|

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
  "name": "ä¼ä¸ç CRM",
  "sku": "CRM-ENT-001",
  "spec": "æ åç",
  "price": 1999,
  "params": "{\"å®¹é\":\"100GB\",\"å¹¶åæ°\":\"500\"}",
  "attachments": "[{\"name\":\"éåè¡¨.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
  "description": "string",
  "status": 1
}

```

äº§åèµæ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|categoryId|integer(int64)|false|none|åç±» ID|
|name|string|false|none|äº§ååç§°|
|sku|string|false|none|äº§åç¼ç ï¼SKUï¼|
|spec|string|false|none|è§æ ¼åå·|
|price|number|false|none|åèä»·æ ¼ï¼åï¼|
|params|string|false|none|äº§ååæ°ï¼JSONB å¯¹è±¡ï¼ç»æåé®å¼å¯¹ï¼å¦ {"å®¹é":"100GB","å¹¶åæ°":"500"}ï¼|
|attachments|string|false|none|éä»¶åè¡¨ï¼JSONB æ°ç»ï¼åç´ å« name åç§°/url å°åï¼å¦ [{"name":"éåè¡¨.xlsx","url":"/uploads/xxx.xlsx"}]ï¼|
|description|string|false|none|äº§åæè¿°|
|status|integer(int32)|false|none|ç¶æï¼1 ä¸æ¶ / 0 ä¸æ¶|

<h2 id="tocS_ResultProduct">ResultProduct</h2>
<!-- backwards compatibility -->
<a id="schemaresultproduct"></a>
<a id="schema_ResultProduct"></a>
<a id="tocSresultproduct"></a>
<a id="tocsresultproduct"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "categoryId": 0,
    "name": "ä¼ä¸ç CRM",
    "sku": "CRM-ENT-001",
    "spec": "æ åç",
    "price": 1999,
    "params": "{\"å®¹é\":\"100GB\",\"å¹¶åæ°\":\"500\"}",
    "attachments": "[{\"name\":\"éåè¡¨.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
    "description": "string",
    "status": 1
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Product](#schemaproduct)|false|none|äº§åèµæ|

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
  "name": "CRM äº§åçº¿",
  "parentId": 0,
  "sort": 1,
  "status": 1
}

```

äº§ååç±»

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|name|string|false|none|åç±»åç§°|
|parentId|integer(int64)|false|none|ç¶åç±» IDï¼0 ä¸ºé¡¶çº§ï¼|
|sort|integer(int32)|false|none|æåºï¼å°å¨åï¼|
|status|integer(int32)|false|none|ç¶æï¼1 å¯ç¨ / 0 åç¨|

<h2 id="tocS_ResultProductCategory">ResultProductCategory</h2>
<!-- backwards compatibility -->
<a id="schemaresultproductcategory"></a>
<a id="schema_ResultProductCategory"></a>
<a id="tocSresultproductcategory"></a>
<a id="tocsresultproductcategory"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "name": "CRM äº§åçº¿",
    "parentId": 0,
    "sort": 1,
    "status": 1
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[ProductCategory](#schemaproductcategory)|false|none|äº§ååç±»|

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

èå/æéï¼å¹³å°çº§å®ä¹ï¼ç§æ·éè¿è§è²-èåå³èè·å¾ï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|parentId|integer(int64)|false|none|ç¶èå IDï¼é¡¶çº§ä¸º 0|
|menuName|string|false|none|èååç§°|
|menuType|string|false|none|ç±»åï¼dirç®å½/menuèå/buttonæé®|
|path|string|false|none|åç«¯è·¯ç±è·¯å¾|
|component|string|false|none|åç«¯ç»ä»¶|
|perms|string|false|none|æé®æéç ï¼å¦ user:add|
|icon|string|false|none|å¾æ |
|sort|integer(int32)|false|none|æåºå·|
|visible|integer(int32)|false|none|æ¯å¦æ¾ç¤ºï¼1æ¾ç¤º/0éè|
|status|integer(int32)|false|none|ç¶æï¼1å¯ç¨/0åç¨|
|children|[[Menu](#schemamenu)]|false|none|å­èåï¼æ å½¢å±ç¤ºç¨ï¼éè¡¨å­æ®µï¼|

<h2 id="tocS_ResultMenu">ResultMenu</h2>
<!-- backwards compatibility -->
<a id="schemaresultmenu"></a>
<a id="schema_ResultMenu"></a>
<a id="tocSresultmenu"></a>
<a id="tocsresultmenu"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Menu](#schemamenu)|false|none|èå/æéï¼å¹³å°çº§å®ä¹ï¼ç§æ·éè¿è§è²-èåå³èè·å¾ï¼|

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
  "ruleName": "åä¸åºåçº¿ç´¢åé",
  "ruleType": "region",
  "matchValue": "åä¸",
  "targetUserId": 0,
  "targetGroupIds": "string",
  "sort": 1,
  "status": 1
}

```

åéè§åä¿¡æ¯

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|ruleName|string|false|none|è§ååç§°|
|ruleType|string|false|none|è§åç±»åï¼productæäº§åçº¿/regionæå°å/round_robinè½®è¯¢ç»|
|matchValue|string|false|none|å¹éå¼ï¼äº§åçº¿æå°åï¼product/region è§åç¨ï¼|
|targetUserId|integer(int64)|false|none|æå®éå® IDï¼product/region è§åç¨ï¼|
|targetGroupIds|string|false|none|è½®è¯¢ç»éå® ID æ°ç»ï¼round_robin è§åç¨ï¼JSONBï¼ï¼å­å¨ç»æç¤ºä¾ï¼[1,2,3]|
|sort|integer(int32)|false|none|ä¼åçº§ï¼æ°å¼è¶å°è¶åå¹éï¼|
|status|integer(int32)|false|none|ç¶æï¼1 å¯ç¨ / 0 åç¨|

<h2 id="tocS_ResultLeadAssignRule">ResultLeadAssignRule</h2>
<!-- backwards compatibility -->
<a id="schemaresultleadassignrule"></a>
<a id="schema_ResultLeadAssignRule"></a>
<a id="tocSresultleadassignrule"></a>
<a id="tocsresultleadassignrule"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "ruleName": "åä¸åºåçº¿ç´¢åé",
    "ruleType": "region",
    "matchValue": "åä¸",
    "targetUserId": 0,
    "targetGroupIds": "string",
    "sort": 1,
    "status": 1
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[LeadAssignRule](#schemaleadassignrule)|false|none|åéè§åä¿¡æ¯|

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
  "title": "AI å¤å¼ç³»ç»äº§åæå",
  "docType": "product_brochure",
  "fileUrl": "/uploads/xxx.pdf",
  "version": 1,
  "status": 0,
  "tags": "[\"AI\",\"å¤å¼\"]"
}

```

èµæææ¡£

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|title|string|false|none|æ é¢|
|docType|string|false|none|ç±»åï¼product_brochureäº§åæå/caseæ¡ä¾/whitepaperç½ç®ä¹¦/selection_tableéåè¡¨|
|fileUrl|string|false|none|æä»¶å°åï¼å¯¹è±¡å­å¨/æ¬å°ï¼|
|version|integer(int32)|false|none|çæ¬å·|
|status|integer(int32)|false|none|ç¶æï¼0 èç¨¿ / 1 å·²åå¸|
|tags|string|false|none|æ ç­¾ï¼JSON æ°ç»å­ç¬¦ä¸²ï¼|

<h2 id="tocS_ResultDocument">ResultDocument</h2>
<!-- backwards compatibility -->
<a id="schemaresultdocument"></a>
<a id="schema_ResultDocument"></a>
<a id="tocSresultdocument"></a>
<a id="tocsresultdocument"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "title": "AI å¤å¼ç³»ç»äº§åæå",
    "docType": "product_brochure",
    "fileUrl": "/uploads/xxx.pdf",
    "version": 1,
    "status": 0,
    "tags": "[\"AI\",\"å¤å¼\"]"
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Document](#schemadocument)|false|none|èµæææ¡£|

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
  "dictName": "çº¿ç´¢ç¶æ",
  "status": 1,
  "remark": "string"
}

```

å­å¸ç±»å

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|dictType|string|false|none|å­å¸ç±»åç¼ç |
|dictName|string|false|none|å­å¸åç§°|
|status|integer(int32)|false|none|ç¶æï¼1 å¯ç¨ / 0 åç¨|
|remark|string|false|none|å¤æ³¨|

<h2 id="tocS_ResultDictType">ResultDictType</h2>
<!-- backwards compatibility -->
<a id="schemaresultdicttype"></a>
<a id="schema_ResultDictType"></a>
<a id="tocSresultdicttype"></a>
<a id="tocsresultdicttype"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "dictType": "lead_status",
    "dictName": "çº¿ç´¢ç¶æ",
    "status": 1,
    "remark": "string"
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[DictType](#schemadicttype)|false|none|å­å¸ç±»å|

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
  "label": "å·²åé",
  "value": "1",
  "sort": 0,
  "status": 1,
  "remark": "string"
}

```

å­å¸æ°æ®

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|dictType|string|false|none|æå±å­å¸ç±»åç¼ç |
|label|string|false|none|æ¾ç¤ºææ¬|
|value|string|false|none|å­å¸å¼|
|sort|integer(int32)|false|none|æåºå·ï¼ååºæåï¼|
|status|integer(int32)|false|none|ç¶æï¼1 å¯ç¨ / 0 åç¨|
|remark|string|false|none|å¤æ³¨|

<h2 id="tocS_ResultDictData">ResultDictData</h2>
<!-- backwards compatibility -->
<a id="schemaresultdictdata"></a>
<a id="schema_ResultDictData"></a>
<a id="tocSresultdictdata"></a>
<a id="tocsresultdictdata"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "dictType": "lead_status",
    "label": "å·²åé",
    "value": "1",
    "sort": 0,
    "status": 1,
    "remark": "string"
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[DictData](#schemadictdata)|false|none|å­å¸æ°æ®|

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
  "name": "ææç§ææéå¬å¸",
  "industry": "è½¯ä»¶æå¡",
  "scale": "100-499äºº",
  "region": "ä¸æµ·",
  "orgStructure": "string",
  "source": "public_data",
  "enrichmentStatus": 0,
  "stage": "potential",
  "intentLevel": 3,
  "score": 80
}

```

å®¢æ·ä¿¡æ¯ï¼name å¿å¡«ï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|name|string|false|none|å¬å¸åç§°|
|industry|string|false|none|è¡ä¸|
|scale|string|false|none|è§æ¨¡|
|region|string|false|none|å°åº|
|orgStructure|string|false|none|äººåæ¶æï¼ææ/å¬å¼æ°æ®ï¼JSONBï¼ï¼å­å¨ç»æç¤ºä¾ï¼{"executives":[{"name":"å§å","title":"èä½","phone":"ææºå·","email":"é®ç®±"}],"departments":["é¨é¨å"]}ï¼å®éå­æ®µä»¥ç¬¬ä¸æ¹è¿åä¸ºå|
|source|string|false|none|enrichment æ¥æºï¼public_dataå¬å¼æ°æ®/customer_providedå®¢æ·æä¾|
|enrichmentStatus|integer(int32)|false|none|æ°æ®è¡¥å¨ç¶æï¼0 æªè¡¥å¨ / 1 å·²è¡¥å¨|
|stage|string|false|none|å®¢æ·é¶æ®µï¼newæ½å¨/potentialææå/intentionæ¥ä»·/negotiatingè°å¤/wonæäº¤/lostæµå¤±|
|intentLevel|integer(int32)|false|none|æåç­çº§ï¼0-5|
|score|integer(int32)|false|none|ç»¼åè¯åï¼0-100|

<h2 id="tocS_ResultCustomer">ResultCustomer</h2>
<!-- backwards compatibility -->
<a id="schemaresultcustomer"></a>
<a id="schema_ResultCustomer"></a>
<a id="tocSresultcustomer"></a>
<a id="tocsresultcustomer"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "name": "ææç§ææéå¬å¸",
    "industry": "è½¯ä»¶æå¡",
    "scale": "100-499äºº",
    "region": "ä¸æµ·",
    "orgStructure": "string",
    "source": "public_data",
    "enrichmentStatus": 0,
    "stage": "potential",
    "intentLevel": 3,
    "score": 80
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Customer](#schemacustomer)|false|none|å®¢æ·ä¿¡æ¯ï¼name å¿å¡«ï¼|

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
  "configValue": "AI éå®çº¿ç´¢ç³»ç»",
  "configName": "ç«ç¹åç§°",
  "configType": 2,
  "remark": "string"
}

```

ç³»ç»åæ°éç½®

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|configKey|string|false|none|åæ°é®|
|configValue|string|false|none|åæ°å¼|
|configName|string|false|none|åæ°åç§°|
|configType|integer(int32)|false|none|ç±»åï¼1 åç½® / 2 èªå®ä¹|
|remark|string|false|none|å¤æ³¨|

<h2 id="tocS_ResultConfig">ResultConfig</h2>
<!-- backwards compatibility -->
<a id="schemaresultconfig"></a>
<a id="schema_ResultConfig"></a>
<a id="tocSresultconfig"></a>
<a id="tocsresultconfig"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "configKey": "system.siteName",
    "configValue": "AI éå®çº¿ç´¢ç³»ç»",
    "configName": "ç«ç¹åç§°",
    "configType": 2,
    "remark": "string"
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Config](#schemaconfig)|false|none|ç³»ç»åæ°éç½®|

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
  "name": "ææäº CRM",
  "category": "CRM",
  "officialUrl": "https://www.example.com",
  "description": "string",
  "strengths": "[\"åè½å¨é¢\",\"ä»·æ ¼ä½\"]",
  "weaknesses": "[\"å®æ½å¤æ\",\"å®åå·®\"]",
  "defenseTactics": "[{\"scenario\":\"æ¯ä»·æ ¼\",\"tactic\":\"å¼ºè°æ»æ¥æææ¬\"}]",
  "status": 1
}

```

ç«åä¸»ä½æ¡£æ¡

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|name|string|false|none|ç«ååç§°|
|category|string|false|none|ç«ååç±»|
|officialUrl|string|false|none|å®ç½å°å|
|description|string|false|none|ä¸»ä½æè¿°|
|strengths|string|false|none|ä¼å¿åè¡¨ï¼JSONB æ°ç»ï¼å¦ ["åè½å¨é¢","ä»·æ ¼ä½"]ï¼|
|weaknesses|string|false|none|å£å¿åè¡¨ï¼JSONB æ°ç»ï¼å¦ ["å®æ½å¤æ","å®åå·®"]ï¼|
|defenseTactics|string|false|none|æ»é²è¯æ¯åè¡¨ï¼JSONB æ°ç»ï¼åç´ å« scenario åºæ¯/tactic è¯æ¯ï¼å¦ [{"scenario":"æ¯ä»·æ ¼","tactic":"å¼ºè°æ»æ¥æææ¬"}]ï¼|
|status|integer(int32)|false|none|ç¶æï¼1 å¯ç¨ / 0 åç¨|

<h2 id="tocS_ResultCompetitor">ResultCompetitor</h2>
<!-- backwards compatibility -->
<a id="schemaresultcompetitor"></a>
<a id="schema_ResultCompetitor"></a>
<a id="tocSresultcompetitor"></a>
<a id="tocsresultcompetitor"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "name": "ææäº CRM",
    "category": "CRM",
    "officialUrl": "https://www.example.com",
    "description": "string",
    "strengths": "[\"åè½å¨é¢\",\"ä»·æ ¼ä½\"]",
    "weaknesses": "[\"å®æ½å¤æ\",\"å®åå·®\"]",
    "defenseTactics": "[{\"scenario\":\"æ¯ä»·æ ¼\",\"tactic\":\"å¼ºè°æ»æ¥æææ¬\"}]",
    "status": 1
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Competitor](#schemacompetitor)|false|none|ç«åä¸»ä½æ¡£æ¡|

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
  "productName": "ç«åäºç",
  "spec": "æè°ç",
  "price": 2999,
  "params": "{\"å¹¶åæ°\":\"300\",\"å­å¨\":\"50GB\"}"
}

```

ç«åäº§ååæ°

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|competitorId|integer(int64)|false|none|ç«å ID|
|productName|string|false|none|ç«åäº§ååç§°|
|spec|string|false|none|è§æ ¼åå·|
|price|number|false|none|åèä»·æ ¼ï¼åï¼|
|params|string|false|none|ç«åäº§ååæ°ï¼JSONB å¯¹è±¡ï¼ç»æåé®å¼å¯¹ï¼å¦ {"å¹¶åæ°":"300","å­å¨":"50GB"}ï¼|

<h2 id="tocS_ResultCompetitorProduct">ResultCompetitorProduct</h2>
<!-- backwards compatibility -->
<a id="schemaresultcompetitorproduct"></a>
<a id="schema_ResultCompetitorProduct"></a>
<a id="tocSresultcompetitorproduct"></a>
<a id="tocsresultcompetitorproduct"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "competitorId": 0,
    "productName": "ç«åäºç",
    "spec": "æè°ç",
    "price": 2999,
    "params": "{\"å¹¶åæ°\":\"300\",\"å­å¨\":\"50GB\"}"
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[CompetitorProduct](#schemacompetitorproduct)|false|none|ç«åäº§ååæ°|

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
  "name": "æé³å¼æµæ´»ç ",
  "channelAccountId": 1,
  "scene": "1:1",
  "qrUrl": "https://example.com/landing?from=qr",
  "status": 1,
  "scanCount": 0,
  "convertedCount": 0
}

```

æ¸ éæ´»ç ï¼æ«ç å¼æµ/æ¸ éæ¥æºæ è®°/å¼æµå½å ï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|name|string|false|none|æ´»ç åç§°|
|channelAccountId|integer(int64)|false|none|å¼æµç®æ æ¸ éè´¦å· ID|
|scene|string|false|none|æ¸ éæ¥æºæ è®°ï¼æ«ç äºä»¶æºå¸¦ï¼ç¨äºçº¿ç´¢å½å ï¼ï¼ä¸å¡«é»è®¤å æ¸ éID:è´¦å·ID|
|qrUrl|string|false|none|æ«ç è·³è½¬è½å°å°å|
|status|integer(int32)|false|none|ç¶æï¼1å¯ç¨/0åç¨|
|scanCount|integer(int32)|false|none|æ«ç æ¬¡æ°|
|convertedCount|integer(int32)|false|none|è½¬åæ°ï¼æ«ç åè½¬åä¸ºçº¿ç´¢æ°ï¼ä¸å®¢æ·èº«ä»½å½ä¸èå¨ï¼|

<h2 id="tocS_ResultChannelQrCode">ResultChannelQrCode</h2>
<!-- backwards compatibility -->
<a id="schemaresultchannelqrcode"></a>
<a id="schema_ResultChannelQrCode"></a>
<a id="tocSresultchannelqrcode"></a>
<a id="tocsresultchannelqrcode"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 1,
    "name": "æé³å¼æµæ´»ç ",
    "channelAccountId": 1,
    "scene": "1:1",
    "qrUrl": "https://example.com/landing?from=qr",
    "status": 1,
    "scanCount": 0,
    "convertedCount": 0
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[ChannelQrCode](#schemachannelqrcode)|false|none|æ¸ éæ´»ç ï¼æ«ç å¼æµ/æ¸ éæ¥æºæ è®°/å¼æµå½å ï¼|

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
  "accountName": "åçå®æ¹å·",
  "externalId": "douyin_open_id_xxx",
  "authConfig": "string",
  "healthStatus": 1,
  "riskLevel": 0
}

```

æ¸ éè´¦å·ï¼ä¼ä¸å·/ç©éµå·ï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|channelId|integer(int64)|false|none|æ¸ é IDï¼å¯¹åº channel.idï¼|
|accountName|string|false|none|è´¦å·åç§°|
|externalId|string|false|none|æ¸ éä¾§è´¦å· ID|
|authConfig|string|false|none|ææå­è¯ï¼å å¯å­å¨ï¼JSONBï¼ï¼ç»æï¼{"accessToken":"è®¿é®ä»¤ç","refreshToken":"å·æ°ä»¤ç","expireAt":"è¿ææ¶é´epochæ¯«ç§"}ï¼æé³å¦å« appId/secretãä¼ä¸å¾®ä¿¡å¦å« corpId/corpSecretï¼ææ¸ éå¯æ©å±|
|healthStatus|integer(int32)|false|none|è´¦å·å¥åº·ç¶æï¼1æ­£å¸¸/2åé/3å°ç¦|
|riskLevel|integer(int32)|false|none|é£æ§ç­çº§ï¼0ä½/1ä¸­/2é«|

<h2 id="tocS_ResultChannelAccount">ResultChannelAccount</h2>
<!-- backwards compatibility -->
<a id="schemaresultchannelaccount"></a>
<a id="schema_ResultChannelAccount"></a>
<a id="tocSresultchannelaccount"></a>
<a id="tocsresultchannelaccount"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 1,
    "channelId": 1,
    "accountName": "åçå®æ¹å·",
    "externalId": "douyin_open_id_xxx",
    "authConfig": "string",
    "healthStatus": 1,
    "riskLevel": 0
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[ChannelAccount](#schemachannelaccount)|false|none|æ¸ éè´¦å·ï¼ä¼ä¸å·/ç©éµå·ï¼|

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

åå»ºç§æ·è¯·æ±ï¼å«åå§åç®¡çåè´¦å·ï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|true|none|ä¼ä¸åç§°|
|planCode|string|false|none|å¥é¤ç¼ç ï¼starter/pro/enterpriseï¼ä¸ºç©ºé»è®¤ starter|
|seatCount|integer(int32)|false|none|åå¸­æ°ä¸éï¼ä¸ºç©ºæå¥é¤é»è®¤|
|aiQuotaMonth|integer(int64)|false|none|æåº¦ AI è°ç¨é¢åº¦ï¼ä¸ºç©ºæå¥é¤é»è®¤|
|expireAt|string(date-time)|false|none|å°ææ¶é´ï¼ä¸ºç©ºè¡¨ç¤ºé¿æææ|
|contactName|string|false|none|å¹³å°å¯¹æ¥èç³»äºº|
|contactMobile|string|false|none|å¹³å°å¯¹æ¥èç³»çµè¯|
|status|integer(int32)|false|none|ç¶æï¼1å¯ç¨/0åç¨ï¼ä¸ºç©ºé»è®¤å¯ç¨|
|adminMobile|string|true|none|åå§åç®¡çåææºå·ï¼ç»å½è´¦å·ï¼|
|adminPassword|string|false|none|åå§åç®¡çåå¯ç ï¼ä¸ºç©ºä½¿ç¨é»è®¤å¯ç |

<h2 id="tocS_ResultInteger">ResultInteger</h2>
<!-- backwards compatibility -->
<a id="schemaresultinteger"></a>
<a id="schema_ResultInteger"></a>
<a id="tocSresultinteger"></a>
<a id="tocsresultinteger"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": 0
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|integer(int32)|false|none|ä¸å¡æ°æ®|

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

çº¿ç´¢ä¿¡æ¯

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|customerId|integer(int64)|false|none|å³èå®¢æ·å¬å¸ IDï¼å¯ç©ºï¼|
|contactId|integer(int64)|false|none|å³èèç³»äºº IDï¼å¯ç©ºï¼|
|sourceChannelId|integer(int64)|false|none|æ¥æºæ¸ éè´¦å· ID|
|sourceContentId|string|false|none|æ¥æºåå®¹/è§é¢/å¹¿å ID|
|sourceType|string|false|none|æ¥æºç±»åï¼commentè¯è®º/dmç§ä¿¡/formè¡¨å/clickç¹å»|
|intent|string|false|none|æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»|
|status|string|false|none|ç¶æï¼newæ°çº¿ç´¢/assignedå·²åé/contactingè·è¿ä¸­/effectiveææ/quotedå·²æ¥ä»·/opportunityåæº/lostæµå¤±|
|score|integer(int32)|false|none|çº¿ç´¢è¯åï¼0-100|
|ownerId|integer(int64)|false|none|å½å±åå¸­ ID|
|slaDeadline|string(date-time)|false|none|ååº SLA æªæ­¢æ¶é´|
|extra|string|false|none|æ½åå³é®å­æ®µï¼JSONBï¼ï¼å­å¨ç»æç¤ºä¾ï¼{"scenario":"åºæ¯","quantity":"æ°é","budget":"é¢ç®","delivery":"äº¤æ"}|

<h2 id="tocS_ResultLead">ResultLead</h2>
<!-- backwards compatibility -->
<a id="schemaresultlead"></a>
<a id="schema_ResultLead"></a>
<a id="tocSresultlead"></a>
<a id="tocsresultlead"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Lead](#schemalead)|false|none|çº¿ç´¢ä¿¡æ¯|

<h2 id="tocS_ResultLong">ResultLong</h2>
<!-- backwards compatibility -->
<a id="schemaresultlong"></a>
<a id="schema_ResultLong"></a>
<a id="tocSresultlong"></a>
<a id="tocsresultlong"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": 0
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|integer(int64)|false|none|ä¸å¡æ°æ®|

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

ç»å®è¯·æ±ä½

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tenantId|integer(int64)|false|none|ç§æ· IDï¼ä¸ºç©ºé»è®¤å½åç§æ·ï¼|
|identityType|string|false|none|èº«ä»½ç±»åï¼mobileææºå·/emailé®ç®±/socialç¤¾åªID/wecomä¼å¾®ID/whatsapp/domainä¼ä¸åå|
|identityValue|string|false|none|èº«ä»½å¼ï¼å¦ææºå·ãé®ç®±å°åãç¤¾åª/ä¼å¾®/WhatsApp IDãä¼ä¸ååï¼|
|entityId|integer(int64)|false|none|ç®æ çº¿ç´¢ ID|
|source|string|false|none|ç»å®æ¥æºæ è¯ï¼å¦ manualæå¨ï¼|

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
  "content": "å®¢æ·å¯¹æ¥ä»·æ¹æ¡ææåï¼çº¦ä¸å¨é¢è°",
  "method": "wechat",
  "nextTime": "2019-08-24T14:15:22Z"
}

```

è·è¿è®°å½ä¿¡æ¯ï¼method åå¼ï¼phoneçµè¯/wechatå¾®ä¿¡/visitæè®¿/otherå¶ä»ï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|leadId|integer(int64)|false|none|å³èçº¿ç´¢ IDï¼å¯ç©ºï¼|
|customerId|integer(int64)|false|none|å³èå®¢æ· IDï¼å¯ç©ºï¼|
|userId|integer(int64)|false|none|è·è¿äºº ID|
|content|string|false|none|è·è¿åå®¹|
|method|string|false|none|è·è¿æ¹å¼ï¼phoneçµè¯/wechatå¾®ä¿¡/visitæè®¿/otherå¶ä»|
|nextTime|string(date-time)|false|none|ä¸æ¬¡è·è¿æ¶é´|

<h2 id="tocS_ResultFollowUp">ResultFollowUp</h2>
<!-- backwards compatibility -->
<a id="schemaresultfollowup"></a>
<a id="schema_ResultFollowUp"></a>
<a id="tocSresultfollowup"></a>
<a id="tocsresultfollowup"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 1,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "tenantId": 0,
    "leadId": 0,
    "customerId": 0,
    "userId": 0,
    "content": "å®¢æ·å¯¹æ¥ä»·æ¹æ¡ææåï¼çº¦ä¸å¨é¢è°",
    "method": "wechat",
    "nextTime": "2019-08-24T14:15:22Z"
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[FollowUp](#schemafollowup)|false|none|è·è¿è®°å½ä¿¡æ¯ï¼method åå¼ï¼phoneçµè¯/wechatå¾®ä¿¡/visitæè®¿/otherå¶ä»ï¼|

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

æä»¶ä¸ä¼ ç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|æä»¶è®°å½ ID|
|fileName|string|false|none|åå§æä»¶å|
|fileSize|integer(int64)|false|none|æä»¶å¤§å°ï¼å­èï¼|
|contentType|string|false|none|åå®¹ç±»å|
|url|string|false|none|è®¿é®å°å|

<h2 id="tocS_ResultFileUploadVO">ResultFileUploadVO</h2>
<!-- backwards compatibility -->
<a id="schemaresultfileuploadvo"></a>
<a id="schema_ResultFileUploadVO"></a>
<a id="tocSresultfileuploadvo"></a>
<a id="tocsresultfileuploadvo"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "id": 0,
    "fileName": "string",
    "fileSize": 0,
    "contentType": "image/png",
    "url": "/api/files/1"
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[FileUploadVO](#schemafileuploadvo)|false|none|æä»¶ä¸ä¼ ç»æ|

<h2 id="tocS_ResultString">ResultString</h2>
<!-- backwards compatibility -->
<a id="schemaresultstring"></a>
<a id="schema_ResultString"></a>
<a id="tocSresultstring"></a>
<a id="tocsresultstring"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": "string"
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|string|false|none|ä¸å¡æ°æ®|

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

ä¼è¯ï¼æ¿æ¥éµå°ï¼ä¼å¾®/WhatsApp/ç§ä¿¡ï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|channelAccountId|integer(int64)|false|none|æ¿æ¥æ¸ éè´¦å· ID|
|contactId|integer(int64)|false|none|èç³»äºº ID|
|leadId|integer(int64)|false|none|çº¿ç´¢ ID|
|conversationType|string|false|none|ä¼è¯ç±»åï¼dmç´æ¥ç§ä¿¡/wecom_chatä¼å¾®èå¤©/whatsapp|
|status|string|false|none|ç¶æï¼activeè¿è¡ä¸­/transferredå·²è½¬äººå·¥/closedå·²å³é­/archivedå·²å½æ¡£|
|assignedTo|integer(int64)|false|none|å½åäººå·¥å¤çäºº ID|
|lastMessageAt|string(date-time)|false|none|æåæ¶æ¯æ¶é´|

<h2 id="tocS_ResultConversation">ResultConversation</h2>
<!-- backwards compatibility -->
<a id="schemaresultconversation"></a>
<a id="schema_ResultConversation"></a>
<a id="tocSresultconversation"></a>
<a id="tocsresultconversation"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Conversation](#schemaconversation)|false|none|ä¼è¯ï¼æ¿æ¥éµå°ï¼ä¼å¾®/WhatsApp/ç§ä¿¡ï¼|

<h2 id="tocS_ResultTransferPackage">ResultTransferPackage</h2>
<!-- backwards compatibility -->
<a id="schemaresulttransferpackage"></a>
<a id="schema_ResultTransferPackage"></a>
<a id="tocSresulttransferpackage"></a>
<a id="tocsresulttransferpackage"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[TransferPackage](#schematransferpackage)|false|none|è½¬äººå·¥äº¤æ¥åï¼åå¸­æ¥ææ¶çå°çä¼è¯ä¸ä¸æï¼å¯¹è¯æè¦/æåå¤å®/ç¼ºå¤±å­æ®µ/æ¨èåå¤/ä¸ä¸æ­¥å¨ä½ï¼|

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

è½¬äººå·¥äº¤æ¥åï¼åå¸­æ¥ææ¶çå°çä¼è¯ä¸ä¸æï¼å¯¹è¯æè¦/æåå¤å®/ç¼ºå¤±å­æ®µ/æ¨èåå¤/ä¸ä¸æ­¥å¨ä½ï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|conversationId|integer(int64)|false|none|ä¼è¯ ID|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|leadId|integer(int64)|false|none|çº¿ç´¢ ID|
|conversationType|string|false|none|ä¼è¯ç±»åï¼dmç´æ¥ç§ä¿¡/wecom_chatä¼å¾®èå¤©/whatsapp|
|operatorId|integer(int64)|false|none|æ¥æåå¸­ ID|
|summary|string|false|none|å¯¹è¯æè¦ï¼AI summaryï¼å¤±è´¥æ¶éçº§æç¤ºï¼|
|summarySource|string|false|none|æè¦æ¥æºï¼ai/fallback|
|intent|string|false|none|æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»|
|confidence|number|false|none|æåç½®ä¿¡åº¦|
|evidence|string|false|none|å¤å®ä¾æ®çæ®µ|
|missingFields|[string]|false|none|ç¼ºå¤±å­æ®µï¼sceneåºæ¯/qtyæ°é/budgeté¢ç®/lead_timeäº¤æ/modelåå· ä¸­æªæ¶éå°ç|
|recommendedReply|string|false|none|æ¨èåå¤è¯æ¯ï¼ææåçæï¼|
|nextSteps|[string]|false|none|ä¸ä¸æ­¥å¨ä½æ¸å|
|transferredAt|string(date-time)|false|none|è½¬äººå·¥æ¶é´|

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

æ¶æ¯

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|conversationId|integer(int64)|false|none|ä¼è¯ ID|
|senderType|string|false|none|åéæ¹ï¼customerå®¢æ·/aiæºå¨äºº/humanäººå·¥/systemç³»ç»|
|content|string|false|none|æ¶æ¯åå®¹|
|msgType|string|false|none|æ¶æ¯ç±»åï¼textææ¬/imageå¾ç/fileæä»¶/cardå¡ç|
|attachments|string|false|none|éä»¶ ID åè¡¨ï¼JSONB å­ç¬¦ä¸²ï¼|
|aiGenerated|boolean|false|none|æ¯å¦ AI çæ|
|quotedDocIds|string|false|none|å¼ç¨ç¥è¯ææ¡£ IDï¼æº¯æºï¼JSONB å­ç¬¦ä¸²ï¼|
|raw|string|false|none|åå§æ¶æ¯ï¼JSONB å­ç¬¦ä¸²ï¼|

<h2 id="tocS_ResultMessage">ResultMessage</h2>
<!-- backwards compatibility -->
<a id="schemaresultmessage"></a>
<a id="schema_ResultMessage"></a>
<a id="tocSresultmessage"></a>
<a id="tocsresultmessage"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Message](#schemamessage)|false|none|æ¶æ¯|

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

AI åå¤ç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|string|false|none|åå¤åå®¹|
|intent|string|false|none|æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»|
|confidence|number|false|none|ç½®ä¿¡åº¦ï¼0-1ï¼|
|shouldTransfer|boolean|false|none|æ¯å¦å»ºè®®è½¬äººå·¥|

<h2 id="tocS_ResultAiReply">ResultAiReply</h2>
<!-- backwards compatibility -->
<a id="schemaresultaireply"></a>
<a id="schema_ResultAiReply"></a>
<a id="tocSresultaireply"></a>
<a id="tocsresultaireply"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "content": "string",
    "intent": "quote",
    "confidence": 0.82,
    "shouldTransfer": true
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[AiReply](#schemaaireply)|false|none|AI åå¤ç»æ|

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

æ¸ éåå§äºä»¶ï¼ä¸æ¸ éç»ä¸å¥å£ï¼å¹ç­å»éï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|channelAccountId|integer(int64)|false|none|æ¸ éè´¦å· IDï¼å¯¹åº channel_account.idï¼|
|eventType|string|false|none|äºä»¶ç±»åï¼commentè¯è®º/dmç§ä¿¡/formè¡¨å/clickç¹å»/leadçº¿ç´¢ï¼æ´»ç æ«ç åé¨äºä»¶ä¸º qrï¼ä¸å¥æ­¤æ¥å£ï¼|
|externalEventId|string|false|none|æ¸ éäºä»¶å¯ä¸ IDï¼å»éé®ï¼ä¸ tenantId+channelAccountId ç»åå¹ç­ï¼|
|externalUserId|string|false|none|æ¸ éä¾§ç¨æ· ID|
|rawPayload|string|false|none|åå§äºä»¶æ°æ®ï¼JSONBï¼ï¼å­å¨æ¸ éåè°çå®æ´åå§ JSONï¼å­æ®µéæ¸ éä¸äºä»¶ç±»åä¸å|
|mapped|integer(int32)|false|none|æ¯å¦å·²æ å°è½çº¿ç´¢ï¼0æªå¤ç/1å·²æ å°|

<h2 id="tocS_ResultBoolean">ResultBoolean</h2>
<!-- backwards compatibility -->
<a id="schemaresultboolean"></a>
<a id="schema_ResultBoolean"></a>
<a id="tocSresultboolean"></a>
<a id="tocsresultboolean"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": true
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|boolean|false|none|ä¸å¡æ°æ®|

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

ç»å½è¯·æ±

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tenantId|integer(int64)|true|none|ç§æ· ID|
|mobile|string|true|none|ææºå·ï¼ç»å½è´¦å·ï¼|
|password|string|true|none|ç»å½å¯ç ï¼ææï¼|

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

ç»å½ååº

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|token|string|false|none|JWT ä»¤ç|
|userId|integer(int64)|false|none|ç¨æ· ID|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|roleCode|string|false|none|ä¸»è§è²ç ï¼è§è²éåç¬¬ä¸ä¸ªï¼å¼å®¹æ§åç«¯ï¼ï¼sales/supervisor/admin|
|roles|[string]|false|none|è§è²ç éåï¼sales/supervisor/admin|
|perms|[string]|false|none|æé®æéç éåï¼å¦ user:add|
|name|string|false|none|å§å|

<h2 id="tocS_ResultLoginResponse">ResultLoginResponse</h2>
<!-- backwards compatibility -->
<a id="schemaresultloginresponse"></a>
<a id="schema_ResultLoginResponse"></a>
<a id="tocSresultloginresponse"></a>
<a id="tocsresultloginresponse"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[LoginResponse](#schemaloginresponse)|false|none|ç»å½ååº|

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

æ¨èè¯æ¯

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|intent|string|false|none|æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»|
|replies|[string]|false|none|æ¨èåå¤è¯æ¯åè¡¨|
|missingFields|[string]|false|none|ç¼ºå¤±å­æ®µï¼sceneåºæ¯/qtyæ°é/budgeté¢ç®/lead_timeäº¤æ/modelåå·|

<h2 id="tocS_ResultReplySuggestion">ResultReplySuggestion</h2>
<!-- backwards compatibility -->
<a id="schemaresultreplysuggestion"></a>
<a id="schema_ResultReplySuggestion"></a>
<a id="tocSresultreplysuggestion"></a>
<a id="tocsresultreplysuggestion"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[ReplySuggestion](#schemareplysuggestion)|false|none|æ¨èè¯æ¯|

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

å®¢æ·ç»å

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|leadId|integer(int64)|false|none|çº¿ç´¢ ID|
|leadStatus|string|false|none|çº¿ç´¢ç¶æï¼newæ°çº¿ç´¢/assignedå·²åé/contactingè·è¿ä¸­/effectiveææ/quotedå·²æ¥ä»·/opportunityåæº/lostæµå¤±|
|intent|string|false|none|çº¿ç´¢æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»|
|score|integer(int32)|false|none|å®¢æ·ç»¼åè¯åï¼0-100ï¼|
|customerId|integer(int64)|false|none|å®¢æ· ID|
|customerName|string|false|none|å®¢æ·åç§°|
|industry|string|false|none|è¡ä¸|
|region|string|false|none|å°åº|
|tags|[string]|false|none|æ ç­¾åè¡¨|
|latestIntent|string|false|none|æè¿æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»|
|intentConfidence|number|false|none|æåç½®ä¿¡åº¦ï¼0-1ï¼|

<h2 id="tocS_ResultCustomerProfile">ResultCustomerProfile</h2>
<!-- backwards compatibility -->
<a id="schemaresultcustomerprofile"></a>
<a id="schema_ResultCustomerProfile"></a>
<a id="tocSresultcustomerprofile"></a>
<a id="tocsresultcustomerprofile"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[CustomerProfile](#schemacustomerprofile)|false|none|å®¢æ·ç»å|

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
      "name": "ä¼ä¸ç CRM",
      "sku": "CRM-ENT-001",
      "spec": "æ åç",
      "price": 1999,
      "params": "{\"å®¹é\":\"100GB\",\"å¹¶åæ°\":\"500\"}",
      "attachments": "[{\"name\":\"éåè¡¨.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
      "description": "string",
      "status": 1
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Product](#schemaproduct)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultProduct">ResultPageResultProduct</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultproduct"></a>
<a id="schema_ResultPageResultProduct"></a>
<a id="tocSresultpageresultproduct"></a>
<a id="tocsresultpageresultproduct"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "name": "ä¼ä¸ç CRM",
        "sku": "CRM-ENT-001",
        "spec": "æ åç",
        "price": 1999,
        "params": "{\"å®¹é\":\"100GB\",\"å¹¶åæ°\":\"500\"}",
        "attachments": "[{\"name\":\"éåè¡¨.xlsx\",\"url\":\"/uploads/xxx.xlsx\"}]",
        "description": "string",
        "status": 1
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultProduct](#schemapageresultproduct)|false|none|åé¡µè¿åç»æ|

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

ä¼è¯æè¦

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|conversationId|integer(int64)|false|none|ä¼è¯ ID|
|conversationType|string|false|none|ä¼è¯ç±»åï¼dmç´æ¥ç§ä¿¡/wecom_chatä¼å¾®èå¤©/whatsapp|
|status|string|false|none|ä¼è¯ç¶æï¼activeè¿è¡ä¸­/transferredå·²è½¬äººå·¥/closedå·²å³é­/archivedå·²å½æ¡£|
|lastMessageAt|string(date-time)|false|none|æè¿ä¸æ¡æ¶æ¯æ¶é´|
|lastMessage|string|false|none|æè¿ä¸æ¡æ¶æ¯åå®¹|
|lastSender|string|false|none|æè¿ä¸æ¡æ¶æ¯åéæ¹ï¼customerå®¢æ·/aiæºå¨äºº/humanäººå·¥/systemç³»ç»|

<h2 id="tocS_ResultListConversationBrief">ResultListConversationBrief</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistconversationbrief"></a>
<a id="schema_ResultListConversationBrief"></a>
<a id="tocSresultlistconversationbrief"></a>
<a id="tocsresultlistconversationbrief"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[ConversationBrief](#schemaconversationbrief)]|false|none|ä¸å¡æ°æ®|

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

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[User](#schemauser)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultUser">ResultPageResultUser</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultuser"></a>
<a id="schema_ResultPageResultUser"></a>
<a id="tocSresultpageresultuser"></a>
<a id="tocsresultpageresultuser"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultUser](#schemapageresultuser)|false|none|åé¡µè¿åç»æ|

<h2 id="tocS_ResultListLong">ResultListLong</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistlong"></a>
<a id="schema_ResultListLong"></a>
<a id="tocSresultlistlong"></a>
<a id="tocsresultlistlong"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": [
    0
  ]
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[integer]|false|none|ä¸å¡æ°æ®|

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

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Tenant](#schematenant)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultTenant">ResultPageResultTenant</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresulttenant"></a>
<a id="schema_ResultPageResultTenant"></a>
<a id="tocSresultpageresulttenant"></a>
<a id="tocsresultpageresulttenant"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultTenant](#schemapageresulttenant)|false|none|åé¡µè¿åç»æ|

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
      "name": "é«æåå®¢æ·",
      "color": "#FF5733",
      "remark": "string",
      "status": 1
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[CustomerTag](#schemacustomertag)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultCustomerTag">ResultPageResultCustomerTag</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultcustomertag"></a>
<a id="schema_ResultPageResultCustomerTag"></a>
<a id="tocSresultpageresultcustomertag"></a>
<a id="tocsresultpageresultcustomertag"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "name": "é«æåå®¢æ·",
        "color": "#FF5733",
        "remark": "string",
        "status": 1
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultCustomerTag](#schemapageresultcustomertag)|false|none|åé¡µè¿åç»æ|

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
      "name": "ææç§ææéå¬å¸",
      "industry": "è½¯ä»¶æå¡",
      "scale": "100-499äºº",
      "region": "ä¸æµ·",
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

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Customer](#schemacustomer)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultCustomer">ResultPageResultCustomer</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultcustomer"></a>
<a id="schema_ResultPageResultCustomer"></a>
<a id="tocSresultpageresultcustomer"></a>
<a id="tocsresultpageresultcustomer"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "name": "ææç§ææéå¬å¸",
        "industry": "è½¯ä»¶æå¡",
        "scale": "100-499äºº",
        "region": "ä¸æµ·",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultCustomer](#schemapageresultcustomer)|false|none|åé¡µè¿åç»æ|

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
      "ruleName": "è¯åé«äº80èªå¨ææ ",
      "tagId": 0,
      "conditionField": "score",
      "conditionOp": "gte",
      "conditionValue": "80",
      "status": 1
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[CustomerTagRule](#schemacustomertagrule)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultCustomerTagRule">ResultPageResultCustomerTagRule</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultcustomertagrule"></a>
<a id="schema_ResultPageResultCustomerTagRule"></a>
<a id="tocSresultpageresultcustomertagrule"></a>
<a id="tocsresultpageresultcustomertagrule"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "ruleName": "è¯åé«äº80èªå¨ææ ",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultCustomerTagRule](#schemapageresultcustomertagrule)|false|none|åé¡µè¿åç»æ|

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
      "title": "å¼åºç½-æ åç",
      "category": "general",
      "content": "string",
      "status": 1
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[SpeechLibrary](#schemaspeechlibrary)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultSpeechLibrary">ResultPageResultSpeechLibrary</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultspeechlibrary"></a>
<a id="schema_ResultPageResultSpeechLibrary"></a>
<a id="tocSresultpageresultspeechlibrary"></a>
<a id="tocsresultpageresultspeechlibrary"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "title": "å¼åºç½-æ åç",
        "category": "general",
        "content": "string",
        "status": 1
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultSpeechLibrary](#schemapageresultspeechlibrary)|false|none|åé¡µè¿åç»æ|

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

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Role](#schemarole)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultRole">ResultPageResultRole</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultrole"></a>
<a id="schema_ResultPageResultRole"></a>
<a id="tocSresultpageresultrole"></a>
<a id="tocsresultpageresultrole"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultRole](#schemapageresultrole)|false|none|åé¡µè¿åç»æ|

<h2 id="tocS_ResultListRole">ResultListRole</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistrole"></a>
<a id="schema_ResultListRole"></a>
<a id="tocSresultlistrole"></a>
<a id="tocsresultlistrole"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[Role](#schemarole)]|false|none|ä¸å¡æ°æ®|

<h2 id="tocS_ResultListProductCategory">ResultListProductCategory</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistproductcategory"></a>
<a id="schema_ResultListProductCategory"></a>
<a id="tocSresultlistproductcategory"></a>
<a id="tocsresultlistproductcategory"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "name": "CRM äº§åçº¿",
      "parentId": 0,
      "sort": 1,
      "status": 1
    }
  ]
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[ProductCategory](#schemaproductcategory)]|false|none|ä¸å¡æ°æ®|

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

å¥é¤å®ä¹ï¼å¹³å°çº§ï¼ç§æ·è®¢éççæ¬éç½®ï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|code|string|false|none|å¥é¤ç¼ç ï¼starter/pro/enterprise|
|name|string|false|none|å¥é¤åç§°|
|seatCount|integer(int32)|false|none|åå¸­æ°ä¸é|
|aiQuotaMonth|integer(int64)|false|none|æåº¦ AI è°ç¨é¢åº¦|
|monthlyPrice|number|false|none|æåä»·ï¼åï¼|
|description|string|false|none|å¥é¤æè¿°|
|status|integer(int32)|false|none|ç¶æï¼1ä¸æ¶/0ä¸æ¶|

<h2 id="tocS_ResultListPlan">ResultListPlan</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistplan"></a>
<a id="schema_ResultListPlan"></a>
<a id="tocSresultlistplan"></a>
<a id="tocsresultlistplan"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[Plan](#schemaplan)]|false|none|ä¸å¡æ°æ®|

<h2 id="tocS_ResultPlan">ResultPlan</h2>
<!-- backwards compatibility -->
<a id="schemaresultplan"></a>
<a id="schema_ResultPlan"></a>
<a id="tocSresultplan"></a>
<a id="tocsresultplan"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Plan](#schemaplan)|false|none|å¥é¤å®ä¹ï¼å¹³å°çº§ï¼ç§æ·è®¢éççæ¬éç½®ï¼|

<h2 id="tocS_ResultListMenu">ResultListMenu</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistmenu"></a>
<a id="schema_ResultListMenu"></a>
<a id="tocSresultlistmenu"></a>
<a id="tocsresultlistmenu"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[Menu](#schemamenu)]|false|none|ä¸å¡æ°æ®|

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
  "userName": "å¼ ä¸",
  "module": "å­å¸ç®¡ç",
  "operation": "åå»ºå­å¸ç±»å",
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

æä½æ¥å¿

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|userId|integer(int64)|false|none|æä½äººç¨æ· ID|
|userName|string|false|none|æä½äººå§å|
|module|string|false|none|ä¸å¡æ¨¡å|
|operation|string|false|none|æä½åå®¹|
|method|string|false|none|æ§è¡æ¹æ³ï¼å¨éå®åï¼|
|requestUrl|string|false|none|è¯·æ±å°å|
|httpMethod|string|false|none|è¯·æ±æ¹å¼|
|requestParams|string|false|none|è¯·æ±åæ°ï¼JSONï¼ææå­æ®µå·²è±æï¼|
|result|integer(int32)|false|none|ç»æï¼1 æå / 0 å¤±è´¥|
|errorMsg|string|false|none|å¼å¸¸ä¿¡æ¯|
|ip|string|false|none|æ¥æº IP|
|durationMs|integer(int64)|false|none|èæ¶ï¼æ¯«ç§ï¼|

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
      "userName": "å¼ ä¸",
      "module": "å­å¸ç®¡ç",
      "operation": "åå»ºå­å¸ç±»å",
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

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[OperLog](#schemaoperlog)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultOperLog">ResultPageResultOperLog</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultoperlog"></a>
<a id="schema_ResultPageResultOperLog"></a>
<a id="tocSresultpageresultoperlog"></a>
<a id="tocsresultpageresultoperlog"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "userName": "å¼ ä¸",
        "module": "å­å¸ç®¡ç",
        "operation": "åå»ºå­å¸ç±»å",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultOperLog](#schemapageresultoperlog)|false|none|åé¡µè¿åç»æ|

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
  "message": "ç»å½æå"
}

```

ç»å½æ¥å¿

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· IDï¼ç»å½å¤±è´¥æ¶å¯è½ä¸ºç©ºï¼|
|userId|integer(int64)|false|none|ç¨æ· IDï¼ç»å½å¤±è´¥æ¶ä¸ºç©ºï¼|
|mobile|string|false|none|ç»å½ææºå·|
|ip|string|false|none|æ¥æº IP|
|userAgent|string|false|none|User-Agent|
|status|integer(int32)|false|none|ç»æï¼1 æå / 0 å¤±è´¥|
|message|string|false|none|ç»ææè¿°|

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
      "message": "ç»å½æå"
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[LoginLog](#schemaloginlog)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultLoginLog">ResultPageResultLoginLog</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultloginlog"></a>
<a id="schema_ResultPageResultLoginLog"></a>
<a id="tocSresultpageresultloginlog"></a>
<a id="tocsresultpageresultloginlog"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "message": "ç»å½æå"
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultLoginLog](#schemapageresultloginlog)|false|none|åé¡µè¿åç»æ|

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

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Lead](#schemalead)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultLead">ResultPageResultLead</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultlead"></a>
<a id="schema_ResultPageResultLead"></a>
<a id="tocSresultpageresultlead"></a>
<a id="tocsresultpageresultlead"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultLead](#schemapageresultlead)|false|none|åé¡µè¿åç»æ|

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
      "ruleName": "åä¸åºåçº¿ç´¢åé",
      "ruleType": "region",
      "matchValue": "åä¸",
      "targetUserId": 0,
      "targetGroupIds": "string",
      "sort": 1,
      "status": 1
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[LeadAssignRule](#schemaleadassignrule)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultLeadAssignRule">ResultPageResultLeadAssignRule</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultleadassignrule"></a>
<a id="schema_ResultPageResultLeadAssignRule"></a>
<a id="tocSresultpageresultleadassignrule"></a>
<a id="tocsresultpageresultleadassignrule"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "ruleName": "åä¸åºåçº¿ç´¢åé",
        "ruleType": "region",
        "matchValue": "åä¸",
        "targetUserId": 0,
        "targetGroupIds": "string",
        "sort": 1,
        "status": 1
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultLeadAssignRule](#schemapageresultleadassignrule)|false|none|åé¡µè¿åç»æ|

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

ä¸å¡æ°æ®

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
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[JobInfo](#schemajobinfo)]|false|none|ä¸å¡æ°æ®|

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
  "jobName": "æ¸ éä»¤çå·æ°",
  "triggerType": "cron",
  "result": 1,
  "errorMsg": "string",
  "durationMs": 120
}

```

å®æ¶ä»»å¡æ§è¡è®°å½

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|jobCode|string|false|none|ä»»å¡ç¼ç |
|jobName|string|false|none|ä»»å¡åç§°|
|triggerType|string|false|none|è§¦åæ¹å¼ï¼cron å®æ¶ / manual æå¨|
|result|integer(int32)|false|none|ç»æï¼1 æå / 0 å¤±è´¥|
|errorMsg|string|false|none|å¼å¸¸ä¿¡æ¯|
|durationMs|integer(int64)|false|none|èæ¶ï¼æ¯«ç§ï¼|

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
      "jobName": "æ¸ éä»¤çå·æ°",
      "triggerType": "cron",
      "result": 1,
      "errorMsg": "string",
      "durationMs": 120
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[JobLog](#schemajoblog)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultJobLog">ResultPageResultJobLog</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultjoblog"></a>
<a id="schema_ResultPageResultJobLog"></a>
<a id="tocSresultpageresultjoblog"></a>
<a id="tocsresultpageresultjoblog"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "jobName": "æ¸ éä»¤çå·æ°",
        "triggerType": "cron",
        "result": 1,
        "errorMsg": "string",
        "durationMs": 120
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultJobLog](#schemapageresultjoblog)|false|none|åé¡µè¿åç»æ|

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

èº«ä»½å°å®ä½æ å°ï¼identity â lead/contact/customerï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|identityId|integer(int64)|false|none|èº«ä»½ ID|
|entityType|string|false|none|å®ä½ç±»åï¼leadçº¿ç´¢/contactèç³»äºº/customerå®¢æ·|
|entityId|integer(int64)|false|none|å®ä½ ID|
|confidence|number|false|none|å¹éç½®ä¿¡åº¦ï¼0-1ï¼é»è®¤ 1ï¼|
|source|string|false|none|æ¥æºï¼å¦ manualæå¨/autoèªå¨å¹éï¼|

<h2 id="tocS_ResultListIdentityMapping">ResultListIdentityMapping</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistidentitymapping"></a>
<a id="schema_ResultListIdentityMapping"></a>
<a id="tocSresultlistidentitymapping"></a>
<a id="tocsresultlistidentitymapping"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[IdentityMapping](#schemaidentitymapping)]|false|none|ä¸å¡æ°æ®|

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

èº«ä»½ï¼è·¨æ¸ éåå¹¶æ ¸å¿ï¼ï¼ææºå·/é®ç®±/ç¤¾åªID/ä¼å¾®ID/WhatsApp/ä¼ä¸åå

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|identityType|string|false|none|èº«ä»½ç±»åï¼mobileææºå·/emailé®ç®±/socialç¤¾åªID/wecomä¼å¾®ID/whatsapp/domainä¼ä¸åå|
|identityValue|string|false|none|èº«ä»½å¼ï¼å¦ææºå·ãé®ç®±å°åãç¤¾åª/ä¼å¾®/WhatsApp IDãä¼ä¸ååï¼|
|status|integer(int32)|false|none|ç¶æï¼1 ææ / 0 å¤±æ|
|consent|integer(int32)|false|none|åæè®°å½ï¼0 æªåæ / 1 å·²åæï¼åè§ï¼|
|consentTime|string(date-time)|false|none|åææ¶é´|

<h2 id="tocS_ResultIdentity">ResultIdentity</h2>
<!-- backwards compatibility -->
<a id="schemaresultidentity"></a>
<a id="schema_ResultIdentity"></a>
<a id="tocSresultidentity"></a>
<a id="tocsresultidentity"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[Identity](#schemaidentity)|false|none|èº«ä»½ï¼è·¨æ¸ éåå¹¶æ ¸å¿ï¼ï¼ææºå·/é®ç®±/ç¤¾åªID/ä¼å¾®ID/WhatsApp/ä¼ä¸åå|

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
      "content": "å®¢æ·å¯¹æ¥ä»·æ¹æ¡ææåï¼çº¦ä¸å¨é¢è°",
      "method": "wechat",
      "nextTime": "2019-08-24T14:15:22Z"
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[FollowUp](#schemafollowup)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultFollowUp">ResultPageResultFollowUp</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultfollowup"></a>
<a id="schema_ResultPageResultFollowUp"></a>
<a id="tocSresultpageresultfollowup"></a>
<a id="tocsresultpageresultfollowup"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "content": "å®¢æ·å¯¹æ¥ä»·æ¹æ¡ææåï¼çº¦ä¸å¨é¢è°",
        "method": "wechat",
        "nextTime": "2019-08-24T14:15:22Z"
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultFollowUp](#schemapageresultfollowup)|false|none|åé¡µè¿åç»æ|

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
  "fileName": "äº§åæå.pdf",
  "filePath": "string",
  "fileSize": 0,
  "contentType": "application/pdf",
  "storageType": "local"
}

```

æä»¶è®°å½

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|fileName|string|false|none|åå§æä»¶å|
|filePath|string|false|none|å­å¨ keyï¼æ¬å°ä¸ºç¸å¯¹è·¯å¾ï¼MinIO ä¸ºå¯¹è±¡åï¼|
|fileSize|integer(int64)|false|none|æä»¶å¤§å°ï¼å­èï¼|
|contentType|string|false|none|åå®¹ç±»å|
|storageType|string|false|none|å­å¨ç±»åï¼local æ¬å°å­å¨ / minio å¯¹è±¡å­å¨|

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
      "fileName": "äº§åæå.pdf",
      "filePath": "string",
      "fileSize": 0,
      "contentType": "application/pdf",
      "storageType": "local"
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[FileRecord](#schemafilerecord)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultFileRecord">ResultPageResultFileRecord</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultfilerecord"></a>
<a id="schema_ResultPageResultFileRecord"></a>
<a id="tocSresultpageresultfilerecord"></a>
<a id="tocsresultpageresultfilerecord"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "fileName": "äº§åæå.pdf",
        "filePath": "string",
        "fileSize": 0,
        "contentType": "application/pdf",
        "storageType": "local"
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultFileRecord](#schemapageresultfilerecord)|false|none|åé¡µè¿åç»æ|

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
      "title": "AI å¤å¼ç³»ç»äº§åæå",
      "docType": "product_brochure",
      "fileUrl": "/uploads/xxx.pdf",
      "version": 1,
      "status": 0,
      "tags": "[\"AI\",\"å¤å¼\"]"
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Document](#schemadocument)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultDocument">ResultPageResultDocument</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultdocument"></a>
<a id="schema_ResultPageResultDocument"></a>
<a id="tocSresultpageresultdocument"></a>
<a id="tocsresultpageresultdocument"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "title": "AI å¤å¼ç³»ç»äº§åæå",
        "docType": "product_brochure",
        "fileUrl": "/uploads/xxx.pdf",
        "version": 1,
        "status": 0,
        "tags": "[\"AI\",\"å¤å¼\"]"
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultDocument](#schemapageresultdocument)|false|none|åé¡µè¿åç»æ|

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
      "dictName": "çº¿ç´¢ç¶æ",
      "status": 1,
      "remark": "string"
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[DictType](#schemadicttype)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultDictType">ResultPageResultDictType</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultdicttype"></a>
<a id="schema_ResultPageResultDictType"></a>
<a id="tocSresultpageresultdicttype"></a>
<a id="tocsresultpageresultdicttype"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "dictName": "çº¿ç´¢ç¶æ",
        "status": 1,
        "remark": "string"
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultDictType](#schemapageresultdicttype)|false|none|åé¡µè¿åç»æ|

<h2 id="tocS_ResultListDictType">ResultListDictType</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistdicttype"></a>
<a id="schema_ResultListDictType"></a>
<a id="tocSresultlistdicttype"></a>
<a id="tocsresultlistdicttype"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "dictType": "lead_status",
      "dictName": "çº¿ç´¢ç¶æ",
      "status": 1,
      "remark": "string"
    }
  ]
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[DictType](#schemadicttype)]|false|none|ä¸å¡æ°æ®|

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
      "label": "å·²åé",
      "value": "1",
      "sort": 0,
      "status": 1,
      "remark": "string"
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[DictData](#schemadictdata)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultDictData">ResultPageResultDictData</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultdictdata"></a>
<a id="schema_ResultPageResultDictData"></a>
<a id="tocSresultpageresultdictdata"></a>
<a id="tocsresultpageresultdictdata"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "label": "å·²åé",
        "value": "1",
        "sort": 0,
        "status": 1,
        "remark": "string"
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultDictData](#schemapageresultdictdata)|false|none|åé¡µè¿åç»æ|

<h2 id="tocS_ResultListDictData">ResultListDictData</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistdictdata"></a>
<a id="schema_ResultListDictData"></a>
<a id="tocSresultlistdictdata"></a>
<a id="tocsresultlistdictdata"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "dictType": "lead_status",
      "label": "å·²åé",
      "value": "1",
      "sort": 0,
      "status": 1,
      "remark": "string"
    }
  ]
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[DictData](#schemadictdata)]|false|none|ä¸å¡æ°æ®|

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

çæ¿æ¯æ¥è¶å¿ï¼çº¿ç´¢é/ä¼è¯é/AI è§£å³éææ¥èå

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|date|string(date)|false|none|ç»è®¡æ¥æ|
|leadCount|integer(int64)|false|none|æ°å¢çº¿ç´¢æ°|
|conversationCount|integer(int64)|false|none|æ°å¢ä¼è¯æ°|
|aiResolvedCount|integer(int64)|false|none|AI è§£å³ä¼è¯æ°ï¼å½æ¥ä¼è¯åå»è½¬äººå·¥ä¼è¯ï¼|

<h2 id="tocS_ResultListDashboardTrend">ResultListDashboardTrend</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistdashboardtrend"></a>
<a id="schema_ResultListDashboardTrend"></a>
<a id="tocSresultlistdashboardtrend"></a>
<a id="tocsresultlistdashboardtrend"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[DashboardTrend](#schemadashboardtrend)]|false|none|ä¸å¡æ°æ®|

<h2 id="tocS_ResultListSalesWorkloadStat">ResultListSalesWorkloadStat</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistsalesworkloadstat"></a>
<a id="schema_ResultListSalesWorkloadStat"></a>
<a id="tocSresultlistsalesworkloadstat"></a>
<a id="tocsresultlistsalesworkloadstat"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[SalesWorkloadStat](#schemasalesworkloadstat)]|false|none|ä¸å¡æ°æ®|

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

çæ¿éå®å·¥ä½éç»è®¡ï¼æéå®èååéçº¿ç´¢/è·è¿/æäº¤/ä¼è¯é

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|userId|integer(int64)|false|none|éå®ç¨æ· ID|
|userName|string|false|none|éå®å§å|
|assignedLeadCount|integer(int64)|false|none|åéçº¿ç´¢æ°|
|followUpCount|integer(int64)|false|none|è·è¿è®°å½æ°|
|wonCount|integer(int64)|false|none|æäº¤çº¿ç´¢æ°ï¼status=wonï¼|
|conversationCount|integer(int64)|false|none|äººå·¥æ¥å¾ä¼è¯æ°|

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

çæ¿æ»è§ï¼çº¿ç´¢é/ä¼è¯é/è½¬äººå·¥æ°/ååºæ¶æ/ææå¯¹è¯ç/æååå¸

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tenantId|integer(int64)|false|none|ç§æ· ID|
|from|string(date-time)|false|none|ç»è®¡èµ·å§æ¶é´|
|to|string(date-time)|false|none|ç»è®¡æªæ­¢æ¶é´|
|leadCount|integer(int64)|false|none|çº¿ç´¢é|
|conversationCount|integer(int64)|false|none|æ°å¢ä¼è¯æ°|
|transferCount|integer(int64)|false|none|è½¬äººå·¥æ°|
|avgResponseSec|number(double)|false|none|å¹³åååºæ¶æï¼ç§ï¼ï¼å®¢æ·é¦æ¡æ¶æ¯å° AI é¦æ¡åå¤|
|effectiveRate|number(double)|false|none|ææå¯¹è¯çï¼0-1ï¼ï¼â¥3 æ¡æ¶æ¯çä¼è¯å æ¯|
|intentDistribution|[[IntentCount](#schemaintentcount)]|false|none|æååå¸|

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

æååå¸æç»

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|intent|string|false|none|æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»/unknownæªç¥|
|count|integer(int64)|false|none|çº¿ç´¢æ°|

<h2 id="tocS_ResultDashboardOverview">ResultDashboardOverview</h2>
<!-- backwards compatibility -->
<a id="schemaresultdashboardoverview"></a>
<a id="schema_ResultDashboardOverview"></a>
<a id="tocSresultdashboardoverview"></a>
<a id="tocsresultdashboardoverview"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[DashboardOverview](#schemadashboardoverview)|false|none|çæ¿æ»è§ï¼çº¿ç´¢é/ä¼è¯é/è½¬äººå·¥æ°/ååºæ¶æ/ææå¯¹è¯ç/æååå¸|

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

çæ¿æ¸ éè½¬åæ°æ®ï¼ææ¸ éè´¦å·èåäºä»¶æ°/çº¿ç´¢æ°/è½¬åç

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|channelAccountId|integer(int64)|false|none|æ¸ éè´¦å· ID|
|accountName|string|false|none|æ¸ éè´¦å·åç§°|
|eventCount|integer(int64)|false|none|æ¸ éäºä»¶æ°|
|leadCount|integer(int64)|false|none|æ å°çº¿ç´¢æ°|
|conversionRate|number(double)|false|none|è½¬åçï¼çº¿ç´¢æ°/äºä»¶æ°ï¼0-1ï¼|

<h2 id="tocS_ResultListChannelConversionStat">ResultListChannelConversionStat</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistchannelconversionstat"></a>
<a id="schema_ResultListChannelConversionStat"></a>
<a id="tocSresultlistchannelconversionstat"></a>
<a id="tocsresultlistchannelconversionstat"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[ChannelConversionStat](#schemachannelconversionstat)]|false|none|ä¸å¡æ°æ®|

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

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Conversation](#schemaconversation)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultConversation">ResultPageResultConversation</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultconversation"></a>
<a id="schema_ResultPageResultConversation"></a>
<a id="tocSresultpageresultconversation"></a>
<a id="tocsresultpageresultconversation"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultConversation](#schemapageresultconversation)|false|none|åé¡µè¿åç»æ|

<h2 id="tocS_ResultTransferEvaluation">ResultTransferEvaluation</h2>
<!-- backwards compatibility -->
<a id="schemaresulttransferevaluation"></a>
<a id="schema_ResultTransferEvaluation"></a>
<a id="tocSresulttransferevaluation"></a>
<a id="tocsresulttransferevaluation"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": {
    "shouldTransfer": true,
    "intent": "selection",
    "confidence": 0.76,
    "reason": "string"
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[TransferEvaluation](#schematransferevaluation)|false|none|è½¬äººå·¥è¯ä¼°ç»æ|

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

è½¬äººå·¥è¯ä¼°ç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|shouldTransfer|boolean|false|none|æ¯å¦å»ºè®®è½¬äººå·¥|
|intent|string|false|none|æåï¼quoteæ¥ä»·/sampleæ ·å/selectionéå/otherå¶ä»|
|confidence|number|false|none|ç½®ä¿¡åº¦ï¼0-1ï¼|
|reason|string|false|none|å¤å®åå|

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

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Message](#schemamessage)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultMessage">ResultPageResultMessage</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultmessage"></a>
<a id="schema_ResultPageResultMessage"></a>
<a id="tocSresultpageresultmessage"></a>
<a id="tocsresultpageresultmessage"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultMessage](#schemapageresultmessage)|false|none|åé¡µè¿åç»æ|

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
      "configValue": "AI éå®çº¿ç´¢ç³»ç»",
      "configName": "ç«ç¹åç§°",
      "configType": 2,
      "remark": "string"
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Config](#schemaconfig)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultConfig">ResultPageResultConfig</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultconfig"></a>
<a id="schema_ResultPageResultConfig"></a>
<a id="tocSresultpageresultconfig"></a>
<a id="tocsresultpageresultconfig"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "configValue": "AI éå®çº¿ç´¢ç³»ç»",
        "configName": "ç«ç¹åç§°",
        "configType": 2,
        "remark": "string"
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultConfig](#schemapageresultconfig)|false|none|åé¡µè¿åç»æ|

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
      "name": "ææäº CRM",
      "category": "CRM",
      "officialUrl": "https://www.example.com",
      "description": "string",
      "strengths": "[\"åè½å¨é¢\",\"ä»·æ ¼ä½\"]",
      "weaknesses": "[\"å®æ½å¤æ\",\"å®åå·®\"]",
      "defenseTactics": "[{\"scenario\":\"æ¯ä»·æ ¼\",\"tactic\":\"å¼ºè°æ»æ¥æææ¬\"}]",
      "status": 1
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[Competitor](#schemacompetitor)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultCompetitor">ResultPageResultCompetitor</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultcompetitor"></a>
<a id="schema_ResultPageResultCompetitor"></a>
<a id="tocSresultpageresultcompetitor"></a>
<a id="tocsresultpageresultcompetitor"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "name": "ææäº CRM",
        "category": "CRM",
        "officialUrl": "https://www.example.com",
        "description": "string",
        "strengths": "[\"åè½å¨é¢\",\"ä»·æ ¼ä½\"]",
        "weaknesses": "[\"å®æ½å¤æ\",\"å®åå·®\"]",
        "defenseTactics": "[{\"scenario\":\"æ¯ä»·æ ¼\",\"tactic\":\"å¼ºè°æ»æ¥æææ¬\"}]",
        "status": 1
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultCompetitor](#schemapageresultcompetitor)|false|none|åé¡µè¿åç»æ|

<h2 id="tocS_ResultListCompetitorProduct">ResultListCompetitorProduct</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistcompetitorproduct"></a>
<a id="schema_ResultListCompetitorProduct"></a>
<a id="tocSresultlistcompetitorproduct"></a>
<a id="tocsresultlistcompetitorproduct"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "tenantId": 0,
      "competitorId": 0,
      "productName": "ç«åäºç",
      "spec": "æè°ç",
      "price": 2999,
      "params": "{\"å¹¶åæ°\":\"300\",\"å­å¨\":\"50GB\"}"
    }
  ]
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[CompetitorProduct](#schemacompetitorproduct)]|false|none|ä¸å¡æ°æ®|

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
  "name": "æé³",
  "type": "short_video",
  "configSchema": "string",
  "status": 1
}

```

æ¸ éå®ä¹ï¼å¹³å°çº§ï¼æé³/è§é¢å·/TikTok/ä¼ä¸å¾®ä¿¡/WhatsAppï¼

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|ä¸»é® ID|
|createdAt|string(date-time)|false|none|åå»ºæ¶é´|
|updatedAt|string(date-time)|false|none|æ´æ°æ¶é´|
|code|string|false|none|æ¸ éç¼ç ï¼douyinæé³/video_channelè§é¢å·/tiktok TikTok/wecomä¼ä¸å¾®ä¿¡/whatsapp WhatsApp|
|name|string|false|none|æ¸ éåç§°|
|type|string|false|none|æ¸ éç±»åï¼short_videoç­è§é¢/socialç¤¾äº¤åªä½/imå³æ¶éè®¯|
|configSchema|string|false|none|éç½®é¡¹ schemaï¼JSONBï¼ï¼å®ä¹è¯¥æ¸ éæ¥å¥æééç½®é¡¹ï¼å¦ appId/secret ç­é®ï¼ç JSON ç»æ|
|status|integer(int32)|false|none|ç¶æï¼1å¯ç¨/0åç¨|

<h2 id="tocS_ResultListChannel">ResultListChannel</h2>
<!-- backwards compatibility -->
<a id="schemaresultlistchannel"></a>
<a id="schema_ResultListChannel"></a>
<a id="tocSresultlistchannel"></a>
<a id="tocsresultlistchannel"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
  "data": [
    {
      "id": 1,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "code": "douyin",
      "name": "æé³",
      "type": "short_video",
      "configSchema": "string",
      "status": 1
    }
  ]
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[[Channel](#schemachannel)]|false|none|ä¸å¡æ°æ®|

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
      "name": "æé³å¼æµæ´»ç ",
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

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[ChannelQrCode](#schemachannelqrcode)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultChannelQrCode">ResultPageResultChannelQrCode</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultchannelqrcode"></a>
<a id="schema_ResultPageResultChannelQrCode"></a>
<a id="tocSresultpageresultchannelqrcode"></a>
<a id="tocsresultpageresultchannelqrcode"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "name": "æé³å¼æµæ´»ç ",
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

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultChannelQrCode](#schemapageresultchannelqrcode)|false|none|åé¡µè¿åç»æ|

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
      "accountName": "åçå®æ¹å·",
      "externalId": "douyin_open_id_xxx",
      "authConfig": "string",
      "healthStatus": 1,
      "riskLevel": 0
    }
  ]
}

```

åé¡µè¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|page|integer(int64)|false|none|å½åé¡µï¼ä» 1 å¼å§ï¼|
|size|integer(int64)|false|none|æ¯é¡µæ¡æ°|
|total|integer(int64)|false|none|æ»æ¡æ°|
|pages|integer(int64)|false|none|æ»é¡µæ°|
|records|[[ChannelAccount](#schemachannelaccount)]|false|none|æ°æ®åè¡¨|

<h2 id="tocS_ResultPageResultChannelAccount">ResultPageResultChannelAccount</h2>
<!-- backwards compatibility -->
<a id="schemaresultpageresultchannelaccount"></a>
<a id="schema_ResultPageResultChannelAccount"></a>
<a id="tocSresultpageresultchannelaccount"></a>
<a id="tocsresultpageresultchannelaccount"></a>

```json
{
  "code": 200,
  "message": "æä½æå",
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
        "accountName": "åçå®æ¹å·",
        "externalId": "douyin_open_id_xxx",
        "authConfig": "string",
        "healthStatus": 1,
        "riskLevel": 0
      }
    ]
  }
}

```

ç»ä¸ API è¿åç»æ

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|ä¸å¡ç ï¼200 æåï¼å¶ä½ä¸ºå¤±è´¥ï¼400 åæ°éè¯¯/401 æªç»å½/403 æ æé/404 ä¸å­å¨/500 ç³»ç»éè¯¯ï¼ä¸å¡éè¯¯ç  1000+ï¼|
|message|string|false|none|æç¤ºä¿¡æ¯|
|data|[PageResultChannelAccount](#schemapageresultchannelaccount)|false|none|åé¡µè¿åç»æ|

