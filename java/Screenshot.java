// Full-page screenshot of a URL, without cookie banners. Java 11+.
// Run: SAHIFA_API_KEY=... java Screenshot.java
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Screenshot {
    public static void main(String[] args) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("access_key", System.getenv("SAHIFA_API_KEY"));
        params.put("url", "https://example.com");
        params.put("format", "png");
        params.put("full_page", "true");
        params.put("block_cookie_banners", "true");
        String query = params.entrySet().stream()
            .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.sahifa.dev/take?" + query)).GET().build();
        HttpResponse<byte[]> res = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (res.statusCode() != 200) {
            throw new RuntimeException("Sahifa error " + res.statusCode() + ": " + new String(res.body(), StandardCharsets.UTF_8));
        }
        Files.write(Path.of("page.png"), res.body());
        System.out.println("page.png saved");
    }
}
