# Full-page screenshot of a URL, without cookie banners. Requires: pip install requests
# Run: SAHIFA_API_KEY=... python screenshot.py
import os
import requests

res = requests.get(
    "https://api.sahifa.dev/take",
    params={
        "access_key": os.environ["SAHIFA_API_KEY"],
        "url": "https://example.com",
        "format": "png",
        "full_page": "true",
        "block_cookie_banners": "true",
    },
    timeout=60,
)
if not res.ok:
    raise RuntimeError(f"Sahifa error {res.status_code}: {res.json()['error']}")

with open("page.png", "wb") as f:
    f.write(res.content)
print("page.png saved")
