package asc.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/*
 * Q1. JSONObject와 JSONArray는 실제로 어떤 클래스인가?
 *
 * 순서
 *   1) 실행 전에 아래 "예상"을 먼저 채운다
 *   2) 테스트를 실행하고 콘솔 출력을 본다
 *   3) JSONObject, JSONArray 위에서 Cmd+클릭(또는 Cmd+B)으로 소스를 직접 열어 본다
 *   4) "결과"와 "이유"를 채우고, 확인한 사실을 assert로 고정한다
 *
 * 예상:
 *   - JSONObject의 부모 클래스는 ___
 *   - JSONArray의 부모 클래스는 ___
 *   - JSONObject를 Map 변수에 담을 수 있다? (O / X)
 *
 * 결과:
 *
 * 이유:
 */
class Q1_JsonClassIdentityTest {

    @Test
    void printSuperclasses() {
        Show.title("부모 클래스");
        Show.line("JSONObject superclass", JSONObject.class.getSuperclass().getName());
        Show.line("JSONArray superclass", JSONArray.class.getSuperclass().getName());

        // TODO 결과를 확인한 뒤 assert로 고정하기
        // assertEquals(???.class, JSONObject.class.getSuperclass());
    }

    @Test
    void checkMapAndListCompatibility() {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        Show.title("Map / List 호환");
        Show.line("obj instanceof Map", obj instanceof Map);
        Show.line("arr instanceof List", arr instanceof List);

        // 예상: 아래 코드는 컴파일될까? 된다면 출력은?
        Map<?, ?> asMap = obj;
        List<?> asList = arr;
        Show.line("asMap.size()", asMap.size());
        Show.line("asList.size()", asList.size());

        // TODO assertTrue / assertFalse로 결과 고정하기
    }

    @Test
    void putAnyTypeWithoutGenerics() {
        // JSONObject는 제네릭 없는 HashMap(raw type)을 상속한다.
        // 그래서 IntelliJ에 "unchecked call" 경고가 뜬다. 왜 그런지 생각해 보기
        JSONObject obj = new JSONObject();
        obj.put("name", "jaeng");
        obj.put("count", 1);
        obj.put("nested", new JSONObject());

        Show.title("아무 타입이나 들어간다");
        Show.value("name", obj.get("name"));
        Show.value("count", obj.get("count"));
        Show.value("nested", obj.get("nested"));
        Show.line("toJSONString()", obj.toJSONString());

        // 생각해 볼 것: 꺼낼 때 Object로 나오면, 사용하는 쪽은 무엇을 해야 하나?
    }
}
