# Retry on 429 (queue full) and 503 (temporarily unavailable), honouring Retry-After.
# Requires: pip install requests. Run: SAHIFA_API_KEY=... python retry.py
import os
import time
import requests


def render(body, attempts=4):
    for attempt in range(1, attempts + 1):
        res = requests.post(
            "https://api.sahifa.dev/v3/convert/pdf",
            headers={"X-API-Key": os.environ["SAHIFA_API_KEY"]},
            json=body,
            timeout=60,
        )
        if res.ok:
            return res.content
        if res.status_code not in (429, 503) or attempt == attempts:
            raise RuntimeError(f"Sahifa error {res.status_code}: {res.json()['error']}")
        time.sleep(int(res.headers.get("Retry-After", 2 ** attempt)))


with open("example.pdf", "wb") as f:
    f.write(render({"source": "https://example.com"}))
print("example.pdf saved")
