package asc.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

/*
 * Q4. 같은 JSON을 Jackson으로 파싱하면 타입이 어떻게 달라지는가?
 *
 * 예상 (Map으로 받을 때 타입 이름):
 *   - "a": 0            → ___   (json-simple에서는 ___ 였다)
 *   - "b": 0.0          → ___
 *   - "c": 10000000000  → ___
 *
 * 예상 (DTO로 받을 때):
 *   - 필드 타입을 BigDecimal로 선언하면 0과 0.0은 각각 ___ / ___
 *
 * 결과:
 *
 * 이유:
 *   (힌트: Jackson은 숫자 크기에 맞춰 가장 작은 타입을 고른다? 직접 확인하기)
 */
class Q4_JacksonNumberTypeTest {

    private static final String JSON = """
            { "a": 0, "b": 0.0, "c": 10000000000 }
            """;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void readAsMap() throws JsonProcessingException {
        Map<String, Object> map = mapper.readValue(JSON, new TypeReference<>() {
        });

        Show.title("Jackson → Map");
        map.forEach(Show::value);

        // TODO json-simple(Q2) 결과와 나란히 비교해서 표로 정리하기
    }

    @Test
    void readAsMapWithBigDecimalOption() throws JsonProcessingException {
        // 소수를 Double 대신 BigDecimal로 읽는 옵션
        ObjectMapper bigDecimalMapper = new ObjectMapper()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

        Map<String, Object> map = bigDecimalMapper.readValue(JSON, new TypeReference<>() {
        });

        Show.title("Jackson → Map (USE_BIG_DECIMAL_FOR_FLOATS)");
        map.forEach(Show::value);

        // 생각해 볼 것: 이 옵션을 켜면 0 vs 0.0 문제가 해결될까? 정수 쪽은 어떻게 되나?
    }

    @Test
    void readAsTree() throws JsonProcessingException {
        // JsonNode: Jackson판 JSONObject. 타입을 확인하는 메서드를 제공한다
        JsonNode root = mapper.readTree(JSON);

        Show.title("Jackson → JsonNode");
        root.fields().forEachRemaining(e -> {
            JsonNode node = e.getValue();
            Show.line(e.getKey() + " nodeType", node.getNodeType() + " / " + node.getClass().getSimpleName());
        });
    }

    // record: Java 16+의 불변 데이터 클래스. 회사 코드의 DTO(필드 + getter)와 같은 역할
    record Price(BigDecimal a, BigDecimal b, Long c) {
    }

    @Test
    void readAsDto() throws JsonProcessingException {
        Price price = mapper.readValue(JSON, Price.class);

        Show.title("Jackson → DTO");
        Show.value("a", price.a());
        Show.value("b", price.b());
        Show.value("c", price.c());
        Show.line("a.compareTo(b)", price.a().compareTo(price.b()));

        // 생각해 볼 것: "타입을 받는 쪽(DTO)이 정한다"는 것의 장점은?
    }
}
