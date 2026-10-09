package asc.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

/*
 * Q2. JSON 문자열을 json-simple로 파싱하면, 각 값은 Java에서 어떤 타입이 되는가?
 *
 * 예상 (타입 이름을 적기):
 *   - "a": 0            → ___
 *   - "b": 0.0          → ___
 *   - "c": 10000000000  → ___   (int 범위 2,147,483,647를 넘는 수)
 *   - "d": "0"          → ___
 *   - "e": true         → ___
 *   - "f": null         → ___
 *   - "g": [1, 2.5]     → ___ , 안의 원소는 ___ / ___
 *
 * 결과:
 *
 * 이유:
 *   (힌트: JSONParser 소스에서 숫자를 만드는 부분을 찾아 보기. Yylex 클래스도 같이)
 */
class Q2_JsonSimpleNumberTypeTest {

    private static final String JSON = """
            {
              "a": 0,
              "b": 0.0,
              "c": 10000000000,
              "d": "0",
              "e": true,
              "f": null,
              "g": [1, 2.5]
            }
            """;

    @Test
    void printParsedTypes() throws ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(JSON);

        Show.title("json-simple 파싱 결과");
        for (String key : new String[]{"a", "b", "c", "d", "e", "f"}) {
            Show.value(key, obj.get(key));
        }

        JSONArray g = (JSONArray) obj.get("g");
        Show.value("g", g);
        Show.value("g[0]", g.get(0));
        Show.value("g[1]", g.get(1));

        // TODO 결과를 확인한 뒤 assertInstanceOf(???.class, obj.get("a")); 형태로 고정하기
    }

    @Test
    void compareZeroAndZeroPointZero() throws ParseException {
        // PAP-MAP에서 겪은 문제를 최소 단위로 재현
        JSONObject before = (JSONObject) new JSONParser().parse("{\"price\": 0}");
        JSONObject after = (JSONObject) new JSONParser().parse("{\"price\": 0.0}");

        // 예상: 두 값은 같다고 판단될까? ___
        Show.title("0 vs 0.0");
        Show.value("before.price", before.get("price"));
        Show.value("after.price", after.get("price"));
        Show.line("equals?", before.get("price").equals(after.get("price")));

        // 생각해 볼 것: 화면에서는 같은 0인데, 비교 로직은 왜 "변경됨"으로 판단했을까?
    }
}
