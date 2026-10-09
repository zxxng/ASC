package asc.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/*
 * Q6. Optional 필드를 가진 DTO는 Jdk8Module 없이 직렬화할 수 있는가?
 *
 * 예상:
 *   - 모듈 없이 writeValueAsString → ___ (성공? 실패? 성공한다면 어떤 JSON?)
 *   - 모듈 등록 후 nickname = Optional.of("jj")   → ___
 *   - 모듈 등록 후 nickname = Optional.empty()    → ___
 *   - 모듈 등록 후 {"nickname": null}을 읽으면     → ___ (null? Optional.empty?)
 *
 * 결과:
 *
 * 이유:
 *   (힌트: Jackson은 "Module"로 기능을 확장한다. registerModule이 하는 일은?)
 *
 * 추가로 확인할 것:
 *   - 회사 코드에서 Jdk8Module을 등록하는 곳을 찾고, 왜 필요했는지 적기
 *   - Jdk8Module 말고 JavaTimeModule(LocalDateTime용)도 쓰는지 확인하기
 */
class Q6_Jdk8ModuleTest {

    record Member(String name, Optional<String> nickname) {
    }

    @Test
    void serializeWithoutModule() {
        ObjectMapper mapper = new ObjectMapper();
        Member member = new Member("jaeng", Optional.of("jj"));

        Show.title("모듈 없이 직렬화");
        try {
            Show.line("result", mapper.writeValueAsString(member));
        } catch (JsonProcessingException e) {
            Show.line("실패", e.getClass().getSimpleName());
            Show.line("메시지", e.getOriginalMessage());
        }
    }

    @Test
    void serializeWithModule() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

        Show.title("Jdk8Module 등록 후 직렬화");
        Show.line("Optional.of(\"jj\")", mapper.writeValueAsString(new Member("jaeng", Optional.of("jj"))));
        Show.line("Optional.empty()", mapper.writeValueAsString(new Member("jaeng", Optional.empty())));
    }

    @Test
    void deserializeWithModule() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

        Show.title("Jdk8Module 등록 후 역직렬화");
        Show.value("nickname: \"jj\"", mapper.readValue("{\"name\":\"jaeng\",\"nickname\":\"jj\"}", Member.class).nickname());
        Show.value("nickname: null", mapper.readValue("{\"name\":\"jaeng\",\"nickname\":null}", Member.class).nickname());
        Show.value("nickname 없음", mapper.readValue("{\"name\":\"jaeng\"}", Member.class).nickname());

        // 생각해 볼 것: null과 "필드 없음"을 구분해야 하는 상황은 언제인가? (예: 부분 수정 API)
    }
}
