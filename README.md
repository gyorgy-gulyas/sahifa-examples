# Sahifa: Arabic HTML to PDF and screenshot API, hosted in Saudi Arabia

Ready-to-run examples for [Sahifa](https://sahifa.dev/en/), an HTML-to-PDF and screenshot API that renders **Arabic and right-to-left documents correctly** and processes everything **in Saudi Arabia** (Jeddah).

[Documentation](https://sahifa.dev/en/docs) · [Quick start](https://sahifa.dev/en/docs/quickstart) · [OpenAPI](https://sahifa.dev/en/docs/openapi) · [Pricing](https://sahifa.dev/en/#pricing) · [Data residency](https://sahifa.dev/en/data-residency)

```
curl https://api.sahifa.dev/v3/convert/pdf \
  --user "api:$SAHIFA_API_KEY" \
  --header "Content-Type: application/json" \
  --data '{"source": "<html dir=\"rtl\" lang=\"ar\"><body><h1>فاتورة ضريبية</h1></body></html>"}' \
  --output invoice.pdf
```

## Why this exists

Generating a PDF with Arabic text is harder than it should be. Most PDF libraries draw each Arabic letter in its isolated form, reverse the word order, or show `????`, and mixed Arabic, English and numbers in one line make it worse. Typical symptoms:

- letters that do not join: `ف ا ت و ر ة` instead of `فاتورة`;
- words in reverse order, or punctuation on the wrong side;
- `?` or empty boxes instead of Arabic characters;
- tables and columns that do not mirror for right-to-left layout.

Sahifa avoids this by rendering your HTML in **Chromium**, the same engine as Chrome, with Arabic fonts installed (Noto Naskh Arabic, Noto Sans Arabic, Amiri, Noto Kufi Arabic). Whatever looks right in Chrome looks right in the PDF: letter joining, bidi reordering, `dir="rtl"`, Arabic-Indic digits, and mixed Arabic/English.

For fintechs and other regulated companies in the Kingdom there is a second reason: documents such as account statements and invoices often may not leave Saudi Arabia. Sahifa runs on Oracle Cloud in Jeddah, does not store the documents you send, and signs a data processing agreement. See the [data residency statement](https://sahifa.dev/en/data-residency).

## Examples

| | PDF from HTML | Screenshot of a URL | Retry on busy / rate limit |
|---|---|---|---|
| curl | [curl/pdf.sh](curl/pdf.sh) | [curl/screenshot.sh](curl/screenshot.sh) | [curl/retry.sh](curl/retry.sh) |
| JavaScript (Node 18+) | [javascript/pdf.mjs](javascript/pdf.mjs) | [javascript/screenshot.mjs](javascript/screenshot.mjs) | [javascript/retry.mjs](javascript/retry.mjs) |
| Python 3.8+ | [python/pdf.py](python/pdf.py) | [python/screenshot.py](python/screenshot.py) | [python/retry.py](python/retry.py) |
| .NET 8 (C#) | [dotnet/pdf.cs](dotnet/pdf.cs) | [dotnet/screenshot.cs](dotnet/screenshot.cs) | [dotnet/retry.cs](dotnet/retry.cs) |
| Java 18+ | [java/Pdf.java](java/Pdf.java) | [java/Screenshot.java](java/Screenshot.java) | [java/Retry.java](java/Retry.java) |

Each example is a single file with no SDK: it uses the language's standard HTTP client (Python needs `requests`).

## Getting started

1. Create a free account at [sahifa.dev](https://sahifa.dev/en/account). The Sandbox plan gives 100 renders a month (up to 10 a day) with a watermark, no card needed.
2. Copy your API key and put it in the environment:
   ```
   export SAHIFA_API_KEY=sk_live_...        # macOS / Linux
   $env:SAHIFA_API_KEY = "sk_live_..."      # Windows PowerShell
   ```
3. Run an example:
   ```
   bash curl/pdf.sh
   node javascript/pdf.mjs
   python python/pdf.py
   dotnet run dotnet/pdf.cs               # .NET 10; or paste into a console app
   java java/Pdf.java
   ```
   `run-all.sh` runs every example against the live API and checks the output files.

## The two endpoints

### PDF: `POST https://api.sahifa.dev/v3/convert/pdf`

Compatible with the PDFShift v3 API, so existing PDFShift code usually needs only the host name changed.

- **Auth:** HTTP Basic with user `api` and your key as the password, or the header `X-API-Key: <key>`.
- **Body (JSON):** `source` (HTML or a URL, required), `format` (`A4`, `Letter`, …), `landscape`, `margin`, `header` / `footer` (`{ "source": "<html>" }` with `{{page}}` and `{{total}}`), `css`, `javascript`, `delay`, `wait_for`, `pages`, `zoom`.
- **Response:** the PDF bytes (`application/pdf`).

### Screenshot: `GET` or `POST https://api.sahifa.dev/take`

Compatible with the ScreenshotOne API.

- **Auth:** `access_key` parameter or the header `X-Access-Key`.
- **Parameters:** `url` or `html`, `format` (`png`, `jpeg`, `webp`, `pdf`), `viewport_width`, `viewport_height`, `full_page`, `device_scale_factor`, `selector`, `block_ads`, `block_cookie_banners`, `dark_mode`, `delay` (seconds).
- **Response:** the image bytes.

Every parameter is in the [API reference](https://sahifa.dev/en/docs) and the [OpenAPI document](https://sahifa.dev/en/docs/openapi).

## Arabic tips

A few things make Arabic documents look professional, whichever tool you use:

```html
<html dir="rtl" lang="ar">
<head>
  <meta charset="utf-8">
  <style>
    body { font-family: 'Noto Naskh Arabic', 'Amiri', serif; }
    .ltr { direction: ltr; unicode-bidi: isolate; }   /* IBANs, emails, SKUs inside Arabic text */
    table { width: 100%; }                               /* columns mirror automatically with dir="rtl" */
    @page { size: A4; margin: 15mm; }
  </style>
</head>
<body>
  <h1>فاتورة ضريبية مبسطة</h1>
  <p>رقم الحساب: <bdi class="ltr">SA03 8000 0000 6080 1016 7519</bdi></p>
</body>
</html>
```

- Put `dir="rtl"` on `<html>`, not only on single elements, so tables, lists and page layout mirror.
- Wrap Latin runs such as IBANs, e-mail addresses, SKUs and order numbers in `<bdi>` (or `unicode-bidi: isolate`) so they are not reordered with the Arabic text around them.
- Use `font-variant-numeric: tabular-nums` for amount columns so the digits line up.
- For a bilingual document, keep each language in its own element with its own `dir`, instead of mixing them inside one paragraph.
- More in the docs: [Arabic and right-to-left](https://sahifa.dev/en/docs/arabic) and [Tips](https://sahifa.dev/en/docs/tips).

## ZATCA invoices

Sahifa turns your invoice HTML into a PDF; it does not issue or report e-invoices to ZATCA. If your system already produces the ZATCA QR code (phase 1 TLV or the phase 2 value from the cleared XML), put it into the HTML as an image or an inline SVG and it will be printed as is.

## Plans

| Plan | Renders a month | Price (monthly, excl. VAT) |
|---|---|---|
| Sandbox | 100 (up to 10 a day), watermarked | free |
| Developer | 2,000 | SAR 59 |
| Business | 15,000 | SAR 199 |
| Compliance | 50,000, DPA and data residency letter | SAR 799 |

One PDF or one screenshot counts as one render. Current prices: [sahifa.dev/en/#pricing](https://sahifa.dev/en/#pricing).

## Store apps for Salla and Zid

Merchants on Salla and Zid can use the Sahifa app to print Arabic invoices, packing slips, receipts and quotations for their orders, without any code. See [Sahifa for Salla and Zid stores](https://sahifa.dev/en/store-app).

## Support

- Questions about the examples: open an [issue](../../issues) in this repository.
- Anything about your account or the API: [support@sahifa.dev](mailto:support@sahifa.dev).
- Security reports: see [SECURITY.md](SECURITY.md).

Sahifa is a service of Yimello LLC, United Arab Emirates. The code in this repository is released under the [MIT License](LICENSE); the Sahifa service itself is governed by its [terms](https://sahifa.dev/en/terms).

---

## بالعربية

**صحيفة** واجهة برمجية لتحويل HTML إلى PDF ولقطات الشاشة، تعرض النصوص العربية واتجاه اليمين إلى اليسار بشكل صحيح، وتعمل بالكامل داخل المملكة العربية السعودية (جدة)، ولا تحتفظ بالمستندات التي ترسلها.

يحتوي هذا المستودع على أمثلة جاهزة للتشغيل بلغات curl وJavaScript وPython و.NET وJava. أنشئ حسابًا مجانيًا في [sahifa.dev](https://sahifa.dev/account)، وضع مفتاحك في المتغير `SAHIFA_API_KEY`، ثم شغّل أي مثال.

التوثيق الكامل بالعربية: [sahifa.dev/docs](https://sahifa.dev/docs) · الدعم: [support@sahifa.dev](mailto:support@sahifa.dev)
