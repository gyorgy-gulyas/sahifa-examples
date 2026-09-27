// HTML to PDF with an Arabic footer. Node.js 18+ (built-in fetch).
// Run: SAHIFA_API_KEY=... node pdf.mjs
import { writeFile } from 'node:fs/promises';

const html = `<html dir="rtl" lang="ar"><body>
  <h1>فاتورة ضريبية</h1>
  <p>الإجمالي: 1,150.00 ر.س</p>
</body></html>`;

const res = await fetch('https://api.sahifa.dev/v3/convert/pdf', {
  method: 'POST',
  headers: {
    Authorization: 'Basic ' + Buffer.from(`api:${process.env.SAHIFA_API_KEY}`).toString('base64'),
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    source: html,
    format: 'A4',
    margin: '20mm',
    footer: { source: '<div style="width:100%;text-align:center">صفحة {{page}} من {{total}}</div>' },
  }),
});

if (!res.ok) throw new Error(`Sahifa error ${res.status}: ${(await res.json()).error}`);
await writeFile('invoice.pdf', Buffer.from(await res.arrayBuffer()));
console.log(`invoice.pdf created in ${res.headers.get('x-response-duration')} ms`);
