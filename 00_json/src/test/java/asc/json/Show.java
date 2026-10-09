package asc.json;

/**
 * 실습용 출력 도우미.
 * 값과 함께 "실제 타입(클래스)"을 출력해서, 예상과 비교할 수 있게 한다.
 */
final class Show {

    private Show() {
    }

    static void value(String label, Object value) {
        String type = (value == null) ? "null" : value.getClass().getName();
        System.out.printf("%-28s = %-20s (type: %s)%n", label, value, type);
    }

    static void line(String label, Object result) {
        System.out.printf("%-28s → %s%n", label, result);
    }

    static void title(String title) {
        System.out.println();
        System.out.println("===== " + title + " =====");
    }
}
