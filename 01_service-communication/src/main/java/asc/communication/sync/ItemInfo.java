package asc.communication.sync;

/**
 * 상대 서비스에서 받아오는 데이터 (예제용으로 일반화한 도메인).
 */
public record ItemInfo(String itemNo, String name, Integer revision) {
}
