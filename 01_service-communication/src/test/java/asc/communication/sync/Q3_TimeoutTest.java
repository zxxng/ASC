package asc.communication.sync;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
 * Q3. 상대 서비스가 응답하지 않으면, 내 서비스는 어떻게 되는가?
 *
 * 예상:
 *   - 타임아웃 설정이 없는 Proxy는 상대가 3초 늦으면 ___초를 기다린다
 *   - 상대가 응답을 아예 안 주면? ___
 *   - Mono.timeout(1초)을 걸면 ___ 예외가 난다
 *
 * 결과:
 *
 * 이유:
 *
 * 확장 질문:
 *   - 내 서비스의 요청 처리 스레드가 전부 "기다리는 중"이 되면 어떤 일이 생기나? (장애 전파)
 *   - 타임아웃은 어디에 거는 게 맞나? Mono.timeout vs HttpClient의 connect/response timeout
 *   - 서킷 브레이커(Resilience4j)는 타임아웃과 무엇이 다른가?
 */
class Q3_TimeoutTest {

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
    void noTimeoutWaitsAsLongAsServer() {
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_A_BODY)
                .setHeadersDelay(3, TimeUnit.SECONDS));
        ItemProxy proxy = new RetryBlockingItemProxy(fake.webClient(), () -> "token");

        Show.title("타임아웃 없음");
        long start = System.currentTimeMillis();
        proxy.findItems(List.of("A-001"));
        Show.line("걸린 시간(ms)", System.currentTimeMillis() - start);
    }

    @Test
    void monoTimeout() {
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_A_BODY)
                .setHeadersDelay(3, TimeUnit.SECONDS));

        Show.title("Mono.timeout(1초)");
        long start = System.currentTimeMillis();
        try {
            fake.webClient().post()
                    .uri("/items/query")
                    .bodyValue(Map.of("itemNos", List.of("A-001")))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(1))
                    .block();
        } catch (RuntimeException e) {
            Show.line("예외", e.getClass().getName());
            Show.line("원인", e.getCause() == null ? "없음" : e.getCause().getClass().getName());
        }
        Show.line("걸린 시간(ms)", System.currentTimeMillis() - start);

        // TODO 예외 타입을 확인한 뒤 assertThrows로 고정하기
    }

    @Test
    void timeoutWithRetry() {
        // 직접 해 보기: 스타일 A처럼 retry(3)까지 붙어 있다면, 타임아웃 1초일 때 최악의 경우 몇 초를 기다릴까?
        // 1) 예상: ___초
        // 2) 응답 4개를 모두 3초 지연으로 enqueue 하고,
        //    .timeout(Duration.ofSeconds(1)).retry(3) 순서로 붙여 걸린 시간을 재 보기
        // 3) .retry(3).timeout(...) 으로 순서를 바꾸면 결과가 어떻게 달라지는지도 비교하기
    }
}
