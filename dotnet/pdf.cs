// HTML to PDF with an Arabic footer. .NET 8+.
// .NET 10: SAHIFA_API_KEY=... dotnet run pdf.cs  (or paste into Program.cs of a console app)
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json.Nodes;

var key = Environment.GetEnvironmentVariable("SAHIFA_API_KEY");
using var http = new HttpClient { Timeout = TimeSpan.FromSeconds(60) };
http.DefaultRequestHeaders.Authorization =
    new AuthenticationHeaderValue("Basic", Convert.ToBase64String(Encoding.UTF8.GetBytes($"api:{key}")));

var html = """
    <html dir="rtl" lang="ar"><body>
      <h1>فاتورة ضريبية</h1>
      <p>الإجمالي: 1,150.00 ر.س</p>
    </body></html>
    """;

var body = new JsonObject
{
    ["source"] = html,
    ["format"] = "A4",
    ["margin"] = "20mm",
    ["footer"] = new JsonObject { ["source"] = "<div style=\"width:100%;text-align:center\">صفحة {{page}} من {{total}}</div>" },
};
var res = await http.PostAsync("https://api.sahifa.dev/v3/convert/pdf",
    new StringContent(body.ToJsonString(), Encoding.UTF8, "application/json"));
if (!res.IsSuccessStatusCode)
    throw new Exception($"Sahifa error {(int)res.StatusCode}: {await res.Content.ReadAsStringAsync()}");

await File.WriteAllBytesAsync("invoice.pdf", await res.Content.ReadAsByteArrayAsync());
Console.WriteLine("invoice.pdf saved");
