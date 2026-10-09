# 00_json: JSON과 Java 타입

## 목표

- JSON 값이 Java에서 어떤 타입이 되는지 라이브러리별로 설명할 수 있다
- 숫자 비교에서 `0`과 `0.0`이 다르게 판단되는 원인과 해결 방법을 설명할 수 있다
- JSONObject 방식과 DTO 방식의 차이, Jdk8Module이 필요한 이유를 설명할 수 있다

## 질문 목록

| # | 질문 | 파일 |
|---|---|---|
| Q1 | JSONObject와 JSONArray는 실제로 어떤 클래스인가? | `Q1_JsonClassIdentityTest` |
| Q2 | json-simple로 파싱하면 각 값은 어떤 타입이 되는가? | `Q2_JsonSimpleNumberTypeTest` |
| Q3 | 같은 "0"인데 equals 결과가 왜 다를 수 있는가? | `Q3_NumberEqualityTest` |
| Q4 | Jackson으로 파싱하면 타입이 어떻게 달라지는가? | `Q4_JacksonNumberTypeTest` |
| Q5 | JSONObject 방식과 DTO 방식 중 무엇이 더 안전한가? | `Q5_JsonObjectVsDtoTest` |
| Q6 | Optional 필드는 Jdk8Module 없이 직렬화할 수 있는가? | `Q6_Jdk8ModuleTest` |

## 학습 노트

| # | 예상 | 결과 | 이유 |
|---|---|---|---|
| Q1 | | | |
| Q2 | | | |
| Q3 | | | |
| Q4 | | | |
| Q5 | | | |
| Q6 | | | |

## 라이브러리별 숫자 타입 비교 (Q2, Q4를 마치고 채우기)

| JSON 값 | json-simple | Jackson (Map) | Jackson (BigDecimal 옵션) |
|---|---|---|---|
| `0` | | | |
| `0.0` | | | |
| `10000000000` | | | |

## 면접 예상 질문 (30초 답변 적기)

- JSON의 숫자는 Java에서 어떤 타입으로 변환되나요?
- JSONObject와 Map의 차이는 무엇인가요?
- `BigDecimal`의 `equals`와 `compareTo`는 어떻게 다른가요?
- 실무에서 JSONObject 대신 DTO와 Jackson을 쓰는 이유는?
- Jdk8Module은 무엇이고 왜 등록하나요?

## 블로그 링크

- 블로그:
- 링크드인:
