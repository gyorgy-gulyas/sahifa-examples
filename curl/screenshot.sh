# Full-page screenshot of a URL, without cookie banners. Requires: SAHIFA_API_KEY.
curl --get https://api.sahifa.dev/take \
  --data-urlencode "access_key=$SAHIFA_API_KEY" \
  --data-urlencode "url=https://example.com" \
  --data "format=png" \
  --data "full_page=true" \
  --data "block_cookie_banners=true" \
  --fail-with-body --output page.png
