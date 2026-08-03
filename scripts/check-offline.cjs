const fs = require('fs');
const h = fs.readFileSync('./docs/offline-api-doc.html', 'utf8');
const m = h.match(/https?:\/\/[^"'\s<>)]+/g) || [];
const external = [...new Set(m)].filter(u => !u.includes('w3.org') && !u.includes('schema.org'));
console.log('external refs:', external.length ? external.slice(0, 15).join('\n') : 'NONE - fully offline');
