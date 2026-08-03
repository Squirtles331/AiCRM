// 一次性修复脚本：将 @ApiResponse 中非法的业务码响应码修正为合法 HTTP 状态码
// 业务码语义 -> HTTP 状态码映射；原业务码保留在 description 中说明
const fs = require('fs');
const path = require('path');

const moduleRoot = 'd:/Project/AI销售线索系统/AiCRM/java/aicrm-api/src/main/java/com/aicrm/module';
const map = {
  '1001': '404', // 租户不存在
  '1002': '403', // 租户已停用
  '1003': '403', // 租户已到期
  '1004': '404', // 套餐不存在
  '1005': '404', // 套餐已下架
  '1101': '404', // 用户不存在
  '1201': '404', // 线索不存在
  '1302': '404', // 渠道账号不存在
  '1304': '502', // 渠道开放平台 API 调用失败
  '1401': '404', // 会话不存在
  '1701': '500', // 文件上传失败
};

const files = [];
(function walk(dir) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p);
    else if (e.name.endsWith('.java')) files.push(p);
  }
})(moduleRoot);

const re = /@ApiResponse\(responseCode = "(1\d{3})", description = "([^"]*)"\)/g;
let total = 0;
for (const f of files) {
  let src = fs.readFileSync(f, 'utf8');
  let changed = false;
  src = src.replace(re, (m, code, desc) => {
    if (!map[code]) { console.log('NO-MAP code=' + code + ' file=' + f); return m; }
    changed = true;
    total++;
    return '@ApiResponse(responseCode = "' + map[code] + '", description = "' + desc + '（业务码 ' + code + '）")';
  });
  if (changed) fs.writeFileSync(f, src, 'utf8');
}
console.log('replaced total: ' + total);
