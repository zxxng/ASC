package asc.communication.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/*
 * Q5. 스타일 B는 실패하면 빈 목록을 돌려준다. 호출한 쪽은 "데이터 없음"과 "실패"를 구분할 수 있는가?
 *
 * 예상 (각 상황에서 findItems의 결과):
 *   - 정상 응답, 데이터 0건                 → ___
 *   - 응답에 "value" 키가 없음              → ___
 *   - "value"의 모양이 예상과 다름          → ___
 *   - 상대 서비스가 500을 줌               → ___ (빈 목록? 예외?)
 *
 * 결과:
 *
 * 이유:
 *
 * 확장 질문:
 *   - 실패를 빈 목록으로 바꾸면, 화면이나 다음 로직에서는 어떤 일이 생길 수 있나?
 *   - 실패를 알리는 방법들: 예외 던지기, Optional, 결과 객체(성공/실패), 기본값 + 경고 로그. 각각 언제 쓰나?
 *   - 실패해도 화면은 떠야 하는 경우(부분 실패 허용)와, 실패하면 멈춰야 하는 경우는 어떻게 구분하나?
 */
class Q5_FailureVisibilityTest {

    private FakeItemServer fake;
    private ItemProxy proxy;

    @BeforeEach
    void setUp() throws Exception {
        fake = new FakeItemServer();
        proxy = new FallbackItemProxy(fake.webClient(), () -> "token", new ObjectMapper());
    }

    @AfterEach
    void tearDown() throws Exception {
        fake.close();
    }

    @Test
    void emptyData() {
        fake.server.enqueue(FakeItemServer.json(200, "{ \"value\": [] }"));
        Show.title("데이터 0건");
        Show.value("결과", proxy.findItems(List.of("A-001")));
    }

    @Test
    void missingValueKey() {
        fake.server.enqueue(FakeItemServer.json(200, "{ \"result\": [] }"));
        Show.title("value 키 없음");
        Show.value("결과", proxy.findItems(List.of("A-001")));
    }

    @Test
    void unexpectedShape() {
        fake.server.enqueue(FakeItemServer.json(200, "{ \"value\": { \"itemNo\": \"A-001\" } }"));
        Show.title("value 모양이 다름 (목록 대신 객체)");
        Show.value("결과", proxy.findItems(List.of("A-001")));
    }

    @Test
    void serverError() {
        fake.server.enqueue(FakeItemServer.json(500, "{}"));
        Show.title("500 에러");
        try {
            Show.value("결과", proxy.findItems(List.of("A-001")));
        } catch (RuntimeException e) {
            Show.line("예외", e.getClass().getName());
        }

        // 생각해 볼 것: try-catch가 감싸고 있는 범위는 어디까지인가? 위 네 가지 결과를 표로 정리해 보기
    }

    @Test
    void designBetterContract() {
        // 직접 해 보기: 호출한 쪽이 "0건"과 "실패"를 구분할 수 있도록 ItemProxy 계약을 다시 설계하기
        //   예) 실패 시 예외를 던지는 버전, 또는 결과 객체를 돌려주는 버전
        //   만든 뒤, 위 네 가지 상황을 다시 테스트해서 결과를 비교하기
    }
}
