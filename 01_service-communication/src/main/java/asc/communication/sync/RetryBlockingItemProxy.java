package asc.communication.sync;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 스타일 A: 처음 접한 프로젝트에서 본 구조를 일반화해 재현한 것.
 *
 * 특징
 *   - 응답을 { "queryResult": ... } 래퍼로 받는다
 *   - retry(3) 후 block()으로 결과를 기다린다
 *   - 호출할 때마다 ObjectMapper를 새로 만들어 변환한다
 *
 * 이 코드는 "정답"이 아니라 리뷰 대상이다. Q1~Q6에서 동작을 하나씩 확인한다.
 */
public class RetryBlockingItemProxy implements ItemProxy {

    private final WebClient webClient;
    private final Supplier<String> tokenSupplier;   // 내부 인증 토큰 (없으면 null)

    public RetryBlockingItemProxy(WebClient webClient, Supplier<String> tokenSupplier) {
        this.webClient = webClient;
        this.tokenSupplier = tokenSupplier;
    }

    private String internalToken() {
        if (tokenSupplier != null) {
            return String.join(" ", "Bearer", tokenSupplier.get());
        }
        return "";
    }

    @Override
    public List<ItemInfo> findItems(List<String> itemNos) {
        QueryResponse response = webClient.post()
                .uri("/items/query")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, internalToken())
                .bodyValue(Map.of("itemNos", itemNos))
                .retrieve()
                .bodyToMono(QueryResponse.class)
                .retry(3)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.convertValue(response.queryResult(), new TypeReference<>() {
        });
    }

    /** 상대 서비스의 공통 응답 래퍼 */
    public record QueryResponse(Object queryResult) {
    }
}
