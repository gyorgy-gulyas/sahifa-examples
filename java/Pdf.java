// HTML to PDF with an Arabic footer. Java 18+ (UTF-8 source by default).
// Run: SAHIFA_API_KEY=... java Pdf.java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class Pdf {
    public static void main(String[] args) throws Exception {
        String key = System.getenv("SAHIFA_API_KEY");
        String auth = Base64.getEncoder().encodeToString(("api:" + key).getBytes(StandardCharsets.UTF_8));

        // JSON written by hand to keep the example dependency-free; use Jackson or Gson in real code.
        String body = """
            {
              "source": "<html dir='rtl' lang='ar'><body><h1>فاتورة ضريبية</h1><p>الإجمالي: 1,150.00 ر.س</p></body></html>",
              "format": "A4",
              "margin": "20mm",
              "footer": { "source": "<div style='width:100%;text-align:center'>صفحة {{page}} من {{total}}</div>" }
            }
            """;

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.sahifa.dev/v3/convert/pdf"))
            .header("Authorization", "Basic " + auth)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
            .build();

        HttpResponse<byte[]> res = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (res.statusCode() != 200) {
            throw new RuntimeException("Sahifa error " + res.statusCode() + ": " + new String(res.body(), StandardCharsets.UTF_8));
        }
        Files.write(Path.of("invoice.pdf"), res.body());
        System.out.println("invoice.pdf saved");
    }
}
