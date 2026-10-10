# ASC

할리노트 ASC 스터디(5주, 15회)에서 백엔드 기초를 직접 실행하며 공부한 기록입니다.

## 구조

주제마다 Gradle 모듈 하나를 만들고, 폴더 이름 앞에 순서 번호를 붙입니다.

```
ASC/
├── build.gradle        ← 모든 모듈의 공통 설정 (Java 21, JUnit 5)
├── settings.gradle     ← 모듈 목록 (include)
└── 00_json/            ← 주제별 모듈
    ├── build.gradle    ← 이 주제에만 필요한 라이브러리
    ├── README.md       ← 학습 노트 (질문 / 예상 / 결과 / 이유)
    └── src/test/java/asc/json/
```

## 학습 방법

1. 질문을 하나 정한다
2. 실행 전에 결과를 예상해서 적는다
3. 테스트를 실행하고 결과를 본다
4. 예상과 다르면 라이브러리 소스를 열어 이유를 찾는다
5. 확인한 사실을 assert로 고정하고, 모듈 README에 정리한다

## 주제 목록

| 폴더 | 주제 | 상태 |
|---|---|---|
| 00_json | JSON과 Java 타입 (json-simple, Jackson, Jdk8Module) | 진행 중 |
| 01_service-communication | 서비스 간 통신 (WebClient Proxy, Kafka 이벤트) | 진행 중 |
| 02_rdb | DB 설계 (모델링, 키, 인덱스, 트랜잭션) | 예정 |
| 03_binary | 파일·바이너리 처리 (byte, Stream, Base64) | 예정 |

## 새 주제 추가하기

1. `ASC/` 아래에 `NN_주제` 폴더를 만들고 `build.gradle`, `src/test/java/asc/<주제>/`를 추가한다
2. `settings.gradle`에 `include 'NN_주제'`를 한 줄 추가한다
3. Gradle을 새로고침한다 (코끼리 아이콘)

> 폴더 이름은 숫자로 시작해도 되지만, Java 패키지 이름은 숫자로 시작할 수 없어서 패키지는 `asc.json`처럼 씁니다.
