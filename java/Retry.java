// Retry on 429 (queue full) and 503 (temporarily unavailable), honouring Retry-After. Java 11+.
// Run: SAHIFA_API_KEY=... java Retry.java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Retry {
    static final HttpClient HTTP = HttpClient.newHttpClient();

    static byte[] render(String json, int attempts) throws Exception {
        for (int attempt = 1; ; attempt++) {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.sahifa.dev/v3/convert/pdf"))
                .header("X-API-Key", System.getenv("SAHIFA_API_KEY"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
            HttpResponse<byte[]> res = HTTP.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (res.statusCode() == 200) return res.body();
            boolean retryable = res.statusCode() == 429 || res.statusCode() == 503;
            if (!retryable || attempt == attempts) {
                throw new RuntimeException("Sahifa error " + res.statusCode() + ": " + new String(res.body(), StandardCharsets.UTF_8));
            }
            long wait = res.headers().firstValue("Retry-After").map(Long::parseLong).orElse((long) Math.pow(2, attempt));
            Thread.sleep(wait * 1000);
        }
    }

    public static void main(String[] args) throws Exception {
        Files.write(Path.of("example.pdf"), render("{\"source\": \"https://example.com\"}", 4));
        System.out.println("example.pdf saved");
    }
}
