package asc.communication.sync;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

/**
 * 상대 서비스(Item 서비스) 역할을 하는 가짜 서버.
 * enqueue로 "다음 요청에 돌려줄 응답"을 순서대로 쌓아 둔다.
 */
final class FakeItemServer implements AutoCloseable {

    static final String STYLE_A_BODY = """
            { "queryResult": [ { "itemNo": "A-001", "name": "pump", "revision": 1 } ] }
            """;
    static final String STYLE_B_BODY = """
            { "value": [ { "itemNo": "A-001", "name": "pump", "revision": 1 } ] }
            """;

    final MockWebServer server = new MockWebServer();

    FakeItemServer() throws IOException {
        server.start();
    }

    WebClient webClient() {
        return WebClient.builder()
                .baseUrl(server.url("/").toString())
                .build();
    }

    static MockResponse json(int status, String body) {
        return new MockResponse()
                .setResponseCode(status)
                .setHeader("Content-Type", "application/json")
                .setBody(body);
    }

    @Override
    public void close() throws IOException {
        server.shutdown();
    }
}
