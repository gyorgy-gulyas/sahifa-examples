# HTML to PDF with an Arabic footer. Requires: SAHIFA_API_KEY in the environment.
curl https://api.sahifa.dev/v3/convert/pdf \
  --user "api:$SAHIFA_API_KEY" \
  --header "Content-Type: application/json" \
  --data '{
    "source": "<html dir=\"rtl\" lang=\"ar\"><body><h1>فاتورة ضريبية</h1><p>الإجمالي: 1,150.00 ر.س</p></body></html>",
    "format": "A4",
    "margin": "20mm",
    "footer": { "source": "<div style=\"width:100%;text-align:center\">صفحة {{page}} من {{total}}</div>" }
  }' \
  --fail-with-body --output invoice.pdf
