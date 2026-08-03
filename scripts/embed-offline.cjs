const fs = require('fs');
const htmlPath = './docs/offline-api-doc.html';
let h = fs.readFileSync(htmlPath, 'utf8');
const js = fs.readFileSync('./scripts/redoc.standalone.js', 'utf8');
let replaced = 0;
h = h.replace(/<script src="https:\/\/cdn\.redocly\.com\/redoc\/v[^"]+\/bundles\/redoc\.standalone\.js"[^>]*><\/script>/g, () => {
  replaced++;
  return '<script>' + js + '</script>';
});
// 移除外部字体 link（离线回退系统字体）
const linksBefore = (h.match(/<link[^>]*fonts\.googleapis\.com[^>]*>/g) || []).length;
h = h.replace(/<link[^>]*fonts\.googleapis\.com[^>]*>/g, '');
fs.writeFileSync(htmlPath, h);
console.log('script replaced:', replaced, '| font links removed:', linksBefore);
