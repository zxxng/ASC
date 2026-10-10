package asc.communication.sync;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
 * Q2. block()은 무엇을 하는가? 블로킹과 논블로킹은 무엇이 다른가?
 *
 * 예상:
 *   - 상대가 2초 뒤에 응답하면, block() 호출은 ___초 뒤에 끝난다
 *   - subscribe()로 바꾸면, 호출 줄은 ___초 뒤에 끝난다
 *   - 응답을 처리하는 코드는 ___ 스레드에서 실행된다 (테스트 스레드? 다른 스레드?)
 *
 * 결과:
 *
 * 이유:
 *
 * 확장 질문:
 *   - Mono는 무엇인가? (힌트: "아직 오지 않은 값 1개", JS의 Promise와 비교)
 *   - 회사 코드는 왜 WebClient를 쓰면서 block()을 했을까? RestTemplate·RestClient와 비교하면?
 *   - Spring MVC(서블릿) 서버에서 block()하는 것과 WebFlux 서버에서 block()하는 것은 왜 다르게 취급될까?
 */
class Q2_BlockingTest {

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
    void blockWaitsForResponse() {
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_A_BODY)
                .setHeadersDelay(2, TimeUnit.SECONDS));
        ItemProxy proxy = new RetryBlockingItemProxy(fake.webClient(), () -> "token");

        Show.title("block()");
        Show.thread("테스트");
        long start = System.currentTimeMillis();
        List<ItemInfo> items = proxy.findItems(List.of("A-001"));
        Show.line("걸린 시간(ms)", System.currentTimeMillis() - start);
        Show.value("결과", items);
    }

    @Test
    void subscribeReturnsImmediately() throws InterruptedException {
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_A_BODY)
                .setHeadersDelay(2, TimeUnit.SECONDS));

        // block() 없이 Mono를 그대로 다룬다
        Mono<Map> mono = fake.webClient().post()
                .uri("/items/query")
                .bodyValue(Map.of("itemNos", List.of("A-001")))
                .retrieve()
                .bodyToMono(Map.class);

        Show.title("subscribe()");
        Show.thread("테스트");
        long start = System.currentTimeMillis();
        mono.subscribe(body -> {
            Show.thread("응답 처리");
            Show.line("응답 도착(ms)", System.currentTimeMillis() - start);
        });
        Show.line("subscribe 줄 통과(ms)", System.currentTimeMillis() - start);

        // 응답이 오기 전에 테스트가 끝나지 않도록 잠시 기다린다
        Thread.sleep(3000);

        // 생각해 볼 것: 그럼 subscribe만 하면 되지 왜 block()을 쓰나? 결과를 return 해야 할 때는?
    }

    @Test
    void lazyUntilSubscribe() throws InterruptedException {
        fake.server.enqueue(FakeItemServer.json(200, FakeItemServer.STYLE_A_BODY));

        // 예상: 아래 줄만 실행하면 HTTP 요청이 나갈까? ___
        Mono<Map> mono = fake.webClient().post()
                .uri("/items/query")
                .bodyValue(Map.of("itemNos", List.of("A-001")))
                .retrieve()
                .bodyToMono(Map.class);

        Thread.sleep(500);
        Show.title("Mono는 언제 실행되나");
        Show.line("만들기만 했을 때 요청 수", fake.server.getRequestCount());

        mono.block();
        Show.line("block() 후 요청 수", fake.server.getRequestCount());
    }
}
