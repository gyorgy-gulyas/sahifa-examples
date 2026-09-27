// Retry on 429 (queue full) and 503 (temporarily unavailable), honouring Retry-After.
// Node.js 18+. Run: SAHIFA_API_KEY=... node retry.mjs
import { writeFile } from 'node:fs/promises';

async function render(body, attempts = 4) {
  for (let attempt = 1; ; attempt++) {
    const res = await fetch('https://api.sahifa.dev/v3/convert/pdf', {
      method: 'POST',
      headers: { 'X-API-Key': process.env.SAHIFA_API_KEY, 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });
    if (res.ok) return Buffer.from(await res.arrayBuffer());
    const retryable = res.status === 429 || res.status === 503;
    if (!retryable || attempt === attempts) throw new Error(`Sahifa error ${res.status}: ${(await res.json()).error}`);
    const wait = Number(res.headers.get('retry-after')) || 2 ** attempt;
    await new Promise((r) => setTimeout(r, wait * 1000));
  }
}

await writeFile('example.pdf', await render({ source: 'https://example.com' }));
console.log('example.pdf saved');
