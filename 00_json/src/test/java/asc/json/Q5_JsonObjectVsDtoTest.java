package asc.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

/*
 * Q5. JSONObject로 꺼내 쓰는 방식과 DTO로 받는 방식을 나란히 놓으면, 무엇이 더 안전한가?
 *
 * 예상:
 *   - key 이름을 오타 냈을 때, JSONObject 방식은 ___ / DTO 방식은 ___
 *   - 숫자를 Integer로 캐스팅하면 ___
 *   - JSON에 DTO에 없는 필드가 오면 Jackson은 ___
 *
 * 결과:
 *
 * 이유:
 *
 * 정리 (표로): 오류를 발견하는 시점은 언제인가? (컴파일 / 실행 / 발견 못함)
 */
class Q5_JsonObjectVsDtoTest {

    private static final String JSON = """
            { "itemNo": 1001, "name": "sample", "count": 3 }
            """;

    @Test
    void accessWithJsonObject() throws ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(JSON);

        Show.title("JSONObject 방식");
        Show.value("get(\"count\")", obj.get("count"));
        Show.value("get(\"cuont\") 오타", obj.get("cuont"));

        // 예상: 아래 캐스팅은 성공할까?
        try {
            Integer count = (Integer) obj.get("count");
            Show.value("(Integer) count", count);
        } catch (RuntimeException e) {
            Show.line("(Integer) 캐스팅 실패", e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // TODO 실제 회사 코드에서 이런 캐스팅을 어떻게 처리했는지 떠올려 보기
    }

    record Item(Long itemNo, String name, Integer count) {
    }

    @Test
    void accessWithDto() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Item item = mapper.readValue(JSON, Item.class);

        Show.title("DTO 방식");
        Show.value("item.count()", item.count());
        // item.cuont();  ← 주석을 풀면? (오타를 언제 발견하는지 확인)

        // 예상: DTO에 없는 필드("extra")가 들어오면?
        String withExtra = """
                { "itemNo": 1001, "name": "sample", "count": 3, "extra": "?" }
                """;
        try {
            Show.value("extra 포함 파싱", mapper.readValue(withExtra, Item.class));
        } catch (JsonProcessingException e) {
            Show.line("extra 포함 파싱 실패", e.getClass().getSimpleName());
        }

        // 생각해 볼 것: 이 동작을 바꾸는 옵션(FAIL_ON_UNKNOWN_PROPERTIES)은 언제 켜고 언제 끄는가?
    }
}
