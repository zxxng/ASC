package asc.communication.sync;

import java.util.List;

/**
 * 다른 서비스(Item 서비스)를 호출하는 창구.
 *
 * 호출하는 쪽은 이 인터페이스만 알고, HTTP·토큰·JSON 변환 같은 세부 사항은 구현체가 숨긴다.
 * 생각해 볼 것: 인터페이스와 구현체를 나누면 무엇이 좋아지나? (테스트, 교체, 의존 방향)
 */
public interface ItemProxy {

    List<ItemInfo> findItems(List<String> itemNos);
}
