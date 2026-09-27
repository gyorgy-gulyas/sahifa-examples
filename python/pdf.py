# HTML to PDF with an Arabic footer. Python 3.8+, requires: pip install requests
# Run: SAHIFA_API_KEY=... python pdf.py
import os
import requests

html = """<html dir="rtl" lang="ar"><body>
  <h1>فاتورة ضريبية</h1>
  <p>الإجمالي: 1,150.00 ر.س</p>
</body></html>"""

res = requests.post(
    "https://api.sahifa.dev/v3/convert/pdf",
    auth=("api", os.environ["SAHIFA_API_KEY"]),
    json={
        "source": html,
        "format": "A4",
        "margin": "20mm",
        "footer": {"source": '<div style="width:100%;text-align:center">صفحة {{page}} من {{total}}</div>'},
    },
    timeout=60,
)
if not res.ok:
    raise RuntimeError(f"Sahifa error {res.status_code}: {res.json()['error']}")

with open("invoice.pdf", "wb") as f:
    f.write(res.content)
print(f"invoice.pdf created in {res.headers['X-Response-Duration']} ms")
