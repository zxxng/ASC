package asc.communication.sync;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/*
 * Q6. 호출할 때마다 new ObjectMapper()를 만드는 것은 괜찮은가?
 *
 * 예상:
 *   - 1만 번 변환할 때, 매번 새로 만들기 vs 하나를 재사용하기 → 시간 차이 약 ___배
 *   - ObjectMapper를 여러 스레드가 같이 써도 안전한가? ___
 *   - 스타일 A가 FAIL_ON_UNKNOWN_PROPERTIES를 끈 이유는? ___ (00_json Q5와 연결)
 *
 * 결과:
 *
 * 이유:
 *
 * 확장 질문:
 *   - Spring Boot는 ObjectMapper를 Bean으로 하나 만들어 준다. 그 Bean에는 어떤 설정과 모듈이 들어 있나?
 *     (회사 코드에서 Jdk8Module을 직접 등록한 이유와 연결해 보기)
 *   - WebClient의 bodyToMono(...)는 내부에서 어떤 ObjectMapper를 쓸까?
 */
class Q6_ObjectMapperReuseTest {

    private static final int COUNT = 10_000;

    private static final Object RAW = List.of(
            Map.of("itemNo", "A-001", "name", "pump", "revision", 1),
            Map.of("itemNo", "A-002", "name", "valve", "revision", 2)
    );

    @Test
    void newMapperEveryTime() {
        long start = System.nanoTime();
        for (int i = 0; i < COUNT; i++) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.convertValue(RAW, new TypeReference<List<ItemInfo>>() {
            });
        }
        Show.title("매번 새로 만들기");
        Show.line("걸린 시간(ms)", (System.nanoTime() - start) / 1_000_000);
    }

    @Test
    void reuseOneMapper() {
        ObjectMapper mapper = new ObjectMapper();
        long start = System.nanoTime();
        for (int i = 0; i < COUNT; i++) {
            mapper.convertValue(RAW, new TypeReference<List<ItemInfo>>() {
            });
        }
        Show.title("하나를 재사용");
        Show.line("걸린 시간(ms)", (System.nanoTime() - start) / 1_000_000);

        // 생각해 볼 것: 두 테스트를 여러 번 실행해 보면 숫자가 매번 같은가? (JVM 워밍업)
        // 측정을 제대로 하려면 어떤 도구를 써야 할까? (힌트: JMH)
    }

    @Test
    void unknownPropertyHandling() {
        Object rawWithExtra = List.of(Map.of("itemNo", "A-001", "name", "pump", "revision", 1, "extra", "?"));

        Show.title("모르는 필드가 섞여 올 때");
        try {
            Show.value("기본 ObjectMapper", new ObjectMapper().convertValue(rawWithExtra, new TypeReference<List<ItemInfo>>() {
            }));
        } catch (IllegalArgumentException e) {
            Show.line("기본 ObjectMapper 실패", e.getClass().getSimpleName());
        }

        // TODO FAIL_ON_UNKNOWN_PROPERTIES를 끈 버전으로도 실행해서 비교하기
        // 생각해 볼 것: 상대 서비스가 응답에 필드를 추가했을 때, 내 서비스가 깨지는 게 좋은가 무시하는 게 좋은가?
    }
}
