package asc.communication.sync;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/*
 * Q4. retry(3)은 언제, 몇 번, 어떤 실패에 재시도하는가?
 *
 * 예상:
 *   - 500, 500, 200 순서로 응답하면 → 요청 수 ___, 결과 ___
 *   - 400(잘못된 요청), 200 순서로 응답하면 → 요청 수 ___ (400에도 재시도할까?)
 *   - 500만 계속 오면 → 요청 수 ___, 최종 예외 ___
 *   - 재시도 사이 간격은 ___
 *
 * 결과:
 *
 * 이유:
 *
 * 확장 질문:
 *   - 재시도해도 되는 요청과 안 되는 요청은? (멱등성: 같은 요청을 여러 번 보내도 결과가 같은가)
 *   - retry(3)과 retryWhen(Retry.backoff(...))은 무엇이 다른가?
 *   - 상대 서비스가 과부하로 500을 줄 때, 간격 없이 바로 재시도하면 어떤 일이 생기나?
 */
class Q4_RetryTest {

    private FakeItemServer fake;
    private ItemProxy proxy;

    @BeforeEach
    void setUp() throws Exception {
        fake = new FakeItemServer();
        proxy = new RetryBlockingItemProxy(fake.webClient(), () -> "token");
    }

    @AfterEach
    void tearDown() throws Exception {
        fake.close();
    }

    @Test
    void recoversAfterServerErrors() {
        fake.server.enqueue(FakeItemServer.json(500, "{}"));
        fake.server.enqueue(FakeItemServer.json(500, "{}"));
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_A_BODY));

        Show.title("500, 500, 200");
        Show.value("결과", proxy.findItems(List.of("A-001")));
        Show.line("요청 수", fake.server.getRequestCount());
    }

    @Test
    void clientErrorIsAlsoRetried() {
        fake.server.enqueue(FakeItemServer.json(400, "{\"message\":\"bad request\"}"));
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_A_BODY));

        Show.title("400, 200");
        Show.value("결과", proxy.findItems(List.of("A-001")));
        Show.line("요청 수", fake.server.getRequestCount());

        // 생각해 볼 것: 요청 자체가 잘못되어 400이 났다면, 다시 보내는 게 의미가 있을까?
    }

    @Test
    void givesUpAfterRetries() {
        for (int i = 0; i < 5; i++) {
            fake.server.enqueue(FakeItemServer.json(500, "{}"));
        }

        Show.title("500만 계속");
        long start = System.currentTimeMillis();
        try {
            proxy.findItems(List.of("A-001"));
        } catch (RuntimeException e) {
            Show.line("최종 예외", e.getClass().getName());
        }
        Show.line("요청 수", fake.server.getRequestCount());
        Show.line("걸린 시간(ms)", System.currentTimeMillis() - start);

        // TODO 직접 해 보기: 500일 때만, 간격을 두고 재시도하는 버전을 만들어 비교하기
        //   힌트: Retry.backoff(3, Duration.ofMillis(200)).filter(e -> ...)
    }
}
