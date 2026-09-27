#!/usr/bin/env bash
# Runs every example against the live API and checks the output file type.
# Usage: SAHIFA_API_KEY=... PYTHON=python bash examples/run-all.sh
set -u
root="$(cd "$(dirname "$0")" && pwd)"
py="${PYTHON:-python}"
ok=0; fail=0
check() { # name, file, expected magic
  local head; head=$(head -c 4 "$2" 2>/dev/null | od -An -c | tr -d ' ')
  if [[ "$head" == "$3"* ]]; then echo "OK    $1"; ok=$((ok+1)); else echo "FAIL  $1 ($(head -c 200 "$2" 2>/dev/null))"; fail=$((fail+1)); fi
}
run() { # name, dir, command, output, magic
  local d; d=$(mktemp -d); (cd "$d" && eval "$3" >/dev/null 2>"$d/err") || true
  check "$1" "$d/$4" "$5"; [ -s "$d/err" ] && [ ! -f "$d/$4" ] && sed -n 1,5p "$d/err"
  rm -rf "$d"
}
PDF='%PDF'; PNG='211PNG'
run curl/pdf        . "bash '$root/curl/pdf.sh'"             invoice.pdf "$PDF"
run curl/screenshot . "bash '$root/curl/screenshot.sh'"      page.png    "$PNG"
run curl/retry      . "bash '$root/curl/retry.sh'"           example.pdf "$PDF"
run js/pdf          . "node '$root/javascript/pdf.mjs'"      invoice.pdf "$PDF"
run js/screenshot   . "node '$root/javascript/screenshot.mjs'" page.png  "$PNG"
run js/retry        . "node '$root/javascript/retry.mjs'"    example.pdf "$PDF"
run py/pdf          . "'$py' '$root/python/pdf.py'"          invoice.pdf "$PDF"
run py/screenshot   . "'$py' '$root/python/screenshot.py'"   page.png    "$PNG"
run py/retry        . "'$py' '$root/python/retry.py'"        example.pdf "$PDF"
run dotnet/pdf      . "dotnet run '$root/dotnet/pdf.cs'"     invoice.pdf "$PDF"
run dotnet/screenshot . "dotnet run '$root/dotnet/screenshot.cs'" page.png "$PNG"
run dotnet/retry    . "dotnet run '$root/dotnet/retry.cs'"   example.pdf "$PDF"
run java/pdf        . "java '$root/java/Pdf.java'"           invoice.pdf "$PDF"
run java/screenshot . "java '$root/java/Screenshot.java'"    page.png    "$PNG"
run java/retry      . "java '$root/java/Retry.java'"         example.pdf "$PDF"
echo "--- $ok OK, $fail FAIL"
