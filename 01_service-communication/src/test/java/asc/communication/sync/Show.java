package asc.communication.sync;

/**
 * 실습용 출력 도우미 (00_json의 Show와 같은 역할).
 */
final class Show {

    private Show() {
    }

    static void value(String label, Object value) {
        String type = (value == null) ? "null" : value.getClass().getName();
        System.out.printf("%-30s = %s (type: %s)%n", label, value, type);
    }

    static void line(String label, Object result) {
        System.out.printf("%-30s → %s%n", label, result);
    }

    static void title(String title) {
        System.out.println();
        System.out.println("===== " + title + " =====");
    }

    static void thread(String label) {
        line(label + " thread", Thread.currentThread().getName());
    }
}
