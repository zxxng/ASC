package asc.json;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/*
 * Q3. 같은 "0"인데 equals 결과는 왜 다를 수 있는가?
 *
 * 예상 (true / false):
 *   - Long(0).equals(Integer(0))              → ___
 *   - Long(0).equals(Double(0.0))             → ___
 *   - Long(0) == 0L  (언박싱 비교)             → ___
 *   - Long(127) == Long(127)  (참조 비교)      → ___
 *   - Long(128) == Long(128)  (참조 비교)      → ___
 *   - BigDecimal("0").equals(BigDecimal("0.0"))     → ___
 *   - BigDecimal("0").compareTo(BigDecimal("0.0"))  → ___ (숫자)
 *
 * 결과:
 *
 * 이유:
 *   (힌트: Long.equals 소스, Long.valueOf 소스의 LongCache, BigDecimal.equals의 javadoc에서 "scale")
 */
class Q3_NumberEqualityTest {

    @Test
    void equalsAcrossWrapperTypes() {
        Long longZero = 0L;
        Integer intZero = 0;
        Double doubleZero = 0.0;

        Show.title("래퍼 타입 간 equals");
        Show.line("Long(0).equals(Integer(0))", longZero.equals(intZero));
        Show.line("Long(0).equals(Double(0.0))", longZero.equals(doubleZero));
        Show.line("Long(0) == 0L", longZero == 0L);

        // TODO assertTrue / assertFalse로 고정하기
    }

    @Test
    @SuppressWarnings("NumberEquality")
    void referenceEqualityAndCache() {
        Long a127 = Long.valueOf(127);
        Long b127 = Long.valueOf(127);
        Long a128 = Long.valueOf(128);
        Long b128 = Long.valueOf(128);

        Show.title("== 참조 비교와 캐시");
        Show.line("Long(127) == Long(127)", a127 == b127);
        Show.line("Long(128) == Long(128)", a128 == b128);

        // 생각해 볼 것: 테스트 데이터가 작은 숫자뿐이면 이 버그를 발견할 수 있을까?
    }

    @Test
    void bigDecimalEqualsVsCompareTo() {
        BigDecimal zero = new BigDecimal("0");
        BigDecimal zeroPointZero = new BigDecimal("0.0");

        Show.title("BigDecimal");
        Show.value("zero", zero);
        Show.value("zeroPointZero", zeroPointZero);
        Show.line("zero.scale()", zero.scale());
        Show.line("zeroPointZero.scale()", zeroPointZero.scale());
        Show.line("equals", zero.equals(zeroPointZero));
        Show.line("compareTo", zero.compareTo(zeroPointZero));

        // 생각해 볼 것: 숫자 값만 비교하려면 어떤 방법을 써야 하나?
    }

    @Test
    void designNormalizedComparison() {
        // 직접 구현해 보기: json-simple에서 꺼낸 Object 두 개를 "숫자 값 기준"으로 비교하는 메서드
        // 예) sameValue(0L, 0.0) → true, sameValue(1L, 1.5) → false, sameValue("0", 0L) → ?
        //
        // 고민할 점
        //   - 둘 다 Number일 때 어떤 타입으로 맞춰 비교할 것인가 (double? BigDecimal?)
        //   - double로 바꾸면 생길 수 있는 문제는? (힌트: 0.1 + 0.2)
        //   - 문자열 "0"과 숫자 0은 같다고 볼 것인가? (정책 결정의 문제)

        // TODO sameValue 메서드를 아래에 만들고, 여기서 assert로 검증하기
    }
}
