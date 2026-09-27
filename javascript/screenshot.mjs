// Full-page screenshot of a URL, without cookie banners. Node.js 18+.
// Run: SAHIFA_API_KEY=... node screenshot.mjs
import { writeFile } from 'node:fs/promises';

const params = new URLSearchParams({
  access_key: process.env.SAHIFA_API_KEY,
  url: 'https://example.com',
  format: 'png',
  full_page: 'true',
  block_cookie_banners: 'true',
});

const res = await fetch(`https://api.sahifa.dev/take?${params}`);
if (!res.ok) throw new Error(`Sahifa error ${res.status}: ${(await res.json()).error}`);
await writeFile('page.png', Buffer.from(await res.arrayBuffer()));
console.log('page.png saved');
