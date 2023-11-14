package GraphXings;

import GraphXings.tests.TestBentleyOttmann;

public class GraphXings {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            TestBentleyOttmann.testBentleyOttman(10, 10, 10);
        }
    }
}
