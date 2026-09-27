// Retry on 429 (queue full) and 503 (temporarily unavailable), honouring Retry-After. .NET 8+.
// .NET 10: SAHIFA_API_KEY=... dotnet run retry.cs
using System.Net;
using System.Text;
using System.Text.Json.Nodes;

using var http = new HttpClient { Timeout = TimeSpan.FromSeconds(60) };
http.DefaultRequestHeaders.Add("X-API-Key", Environment.GetEnvironmentVariable("SAHIFA_API_KEY"));

async Task<byte[]> Render(JsonObject body, int attempts = 4)
{
    for (var attempt = 1; ; attempt++)
    {
        var res = await http.PostAsync("https://api.sahifa.dev/v3/convert/pdf",
            new StringContent(body.ToJsonString(), Encoding.UTF8, "application/json"));
        if (res.IsSuccessStatusCode) return await res.Content.ReadAsByteArrayAsync();
        var retryable = res.StatusCode is HttpStatusCode.TooManyRequests or HttpStatusCode.ServiceUnavailable;
        if (!retryable || attempt == attempts)
            throw new Exception($"Sahifa error {(int)res.StatusCode}: {await res.Content.ReadAsStringAsync()}");
        var wait = res.Headers.RetryAfter?.Delta ?? TimeSpan.FromSeconds(Math.Pow(2, attempt));
        await Task.Delay(wait);
    }
}

await File.WriteAllBytesAsync("example.pdf", await Render(new JsonObject { ["source"] = "https://example.com" }));
Console.WriteLine("example.pdf saved");
