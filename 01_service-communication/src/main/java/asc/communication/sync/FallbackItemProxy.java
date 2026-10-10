package asc.communication.sync;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 스타일 B: 직접 구현했던 프로젝트의 구조를 일반화해 재현한 것.
 *
 * 특징
 *   - 응답을 Map으로 받고 "value" 키에서 꺼낸다
 *   - 토큰이 있을 때만 Authorization 헤더를 붙인다
 *   - "value"가 없거나 변환에 실패하면 빈 목록을 돌려준다
 *
 * 스타일 A와 무엇이 같고 무엇이 다른지 비교하며 본다.
 */
public class FallbackItemProxy implements ItemProxy {

    private final WebClient webClient;
    private final Supplier<String> tokenSupplier;
    private final ObjectMapper objectMapper;

    public FallbackItemProxy(WebClient webClient, Supplier<String> tokenSupplier, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.tokenSupplier = tokenSupplier;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ItemInfo> findItems(List<String> itemNos) {
        Map<String, Object> response = webClient.post()
                .uri("/items/fetch")
                .headers(headers -> {
                    if (tokenSupplier != null) {
                        headers.setBearerAuth(tokenSupplier.get());
                    }
                })
                .bodyValue(Map.of("itemNos", itemNos))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();

        if (response != null && response.containsKey("value")) {
            try {
                return objectMapper.convertValue(response.get("value"), new TypeReference<List<ItemInfo>>() {
                });
            } catch (Exception e) {
                System.err.println("변환 실패: " + e.getMessage());
                return List.of();
            }
        }
        return List.of();
    }
}
