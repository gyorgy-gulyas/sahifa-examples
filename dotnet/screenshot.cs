// Full-page screenshot of a URL, without cookie banners. .NET 8+.
// .NET 10: SAHIFA_API_KEY=... dotnet run screenshot.cs
var key = Environment.GetEnvironmentVariable("SAHIFA_API_KEY");
var query = string.Join("&", new Dictionary<string, string>
{
    ["access_key"] = key!,
    ["url"] = "https://example.com",
    ["format"] = "png",
    ["full_page"] = "true",
    ["block_cookie_banners"] = "true",
}.Select(p => $"{p.Key}={Uri.EscapeDataString(p.Value)}"));

using var http = new HttpClient { Timeout = TimeSpan.FromSeconds(60) };
var res = await http.GetAsync($"https://api.sahifa.dev/take?{query}");
if (!res.IsSuccessStatusCode)
    throw new Exception($"Sahifa error {(int)res.StatusCode}: {await res.Content.ReadAsStringAsync()}");

await File.WriteAllBytesAsync("page.png", await res.Content.ReadAsByteArrayAsync());
Console.WriteLine("page.png saved");
