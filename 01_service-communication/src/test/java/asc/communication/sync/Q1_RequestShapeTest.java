package asc.communication.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/*
 * Q1. Proxy를 한 번 호출하면, 실제로 어떤 HTTP 요청이 나가는가?
 *
 * 예상:
 *   - 메서드 / 경로: ___
 *   - Authorization 헤더 값: ___
 *   - body: ___
 *   - 토큰이 없을 때 스타일 A의 Authorization 헤더는 ___, 스타일 B는 ___
 *
 * 결과:
 *
 * 이유:
 *
 * 확장 질문 (모르면 README에 적어 두기):
 *   - Bearer 토큰은 무엇이고, 서비스 간 호출에서는 누가 이 토큰을 발급하나?
 *   - 조회인데 왜 GET이 아니라 POST를 썼을까? (힌트: 조회 조건이 목록일 때)
 */
class Q1_RequestShapeTest {

    private FakeItemServer fake;

    @BeforeEach
    void setUp() throws Exception {
        fake = new FakeItemServer();
    }

    @AfterEach
    void tearDown() throws Exception {
        fake.close();
    }

    @Test
    void styleARequest() throws Exception {
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_A_BODY));
        ItemProxy proxy = new RetryBlockingItemProxy(fake.webClient(), () -> "token-123");

        List<ItemInfo> items = proxy.findItems(List.of("A-001"));
        RecordedRequest request = fake.server.takeRequest();

        Show.title("스타일 A 요청");
        Show.line("method", request.getMethod());
        Show.line("path", request.getPath());
        Show.line("Authorization", request.getHeader("Authorization"));
        Show.line("Content-Type", request.getHeader("Content-Type"));
        Show.line("body", request.getBody().readUtf8());
        Show.value("결과", items);

        // TODO assertEquals("POST", request.getMethod()); 형태로 확인한 사실을 고정하기
    }

    @Test
    void styleBRequest() throws Exception {
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_B_BODY));
        ItemProxy proxy = new FallbackItemProxy(fake.webClient(), () -> "token-123", new ObjectMapper());

        List<ItemInfo> items = proxy.findItems(List.of("A-001"));
        RecordedRequest request = fake.server.takeRequest();

        Show.title("스타일 B 요청");
        Show.line("method", request.getMethod());
        Show.line("path", request.getPath());
        Show.line("Authorization", request.getHeader("Authorization"));
        Show.line("body", request.getBody().readUtf8());
        Show.value("결과", items);
    }

    @Test
    void withoutToken() throws Exception {
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_A_BODY));
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_B_BODY));

        new RetryBlockingItemProxy(fake.webClient(), null).findItems(List.of("A-001"));
        new FallbackItemProxy(fake.webClient(), null, new ObjectMapper()).findItems(List.of("A-001"));

        Show.title("토큰이 없을 때");
        Show.line("스타일 A Authorization", "[" + fake.server.takeRequest().getHeader("Authorization") + "]");
        Show.line("스타일 B Authorization", "[" + fake.server.takeRequest().getHeader("Authorization") + "]");

        // 생각해 볼 것: 빈 Authorization 헤더를 보내는 것과 아예 안 보내는 것, 받는 쪽에서는 어떻게 다를까?
    }
}
