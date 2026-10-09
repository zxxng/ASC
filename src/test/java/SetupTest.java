import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

class SetupTest {
    @Test
    void check_setting() {
        JSONObject obj = new JSONObject();
        obj.put("hello", "world");
        System.out.println(obj.toJSONString());
    }
}