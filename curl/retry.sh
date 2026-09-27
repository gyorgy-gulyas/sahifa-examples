# curl retries 408, 429, 500, 502, 503 and 504 responses on its own, honouring Retry-After.
curl https://api.sahifa.dev/v3/convert/pdf \
  --user "api:$SAHIFA_API_KEY" \
  --header "Content-Type: application/json" \
  --data '{ "source": "https://example.com" }' \
  --retry 4 --retry-delay 2 --retry-max-time 60 \
  --fail-with-body --output example.pdf
