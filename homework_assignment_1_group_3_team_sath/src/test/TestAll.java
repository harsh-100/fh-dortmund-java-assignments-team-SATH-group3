package test;

public class TestAll {
    public static void main(String[] args) {
        int failed = 0;
        try {
            TestItem.run();
            System.out.println();
        } catch (AssertionError e) {
            failed++;
            System.err.println("TestItem failed: " + e.getMessage());
        }

        try {
            TestOrder.run();
            System.out.println();
        } catch (AssertionError e) {
            failed++;
            System.err.println("TestOrder failed: " + e.getMessage());
        }

        try {
            TestStorageUnit.run();
            System.out.println();
        } catch (AssertionError e) {
            failed++;
            System.err.println("TestStorageUnit failed: " + e.getMessage());
        }

        if (failed == 0) {
            System.out.println("ALL STORAGE TESTS PASSED");
        } else {
            System.err.println(failed + " TEST GROUP(S) FAILED");
            System.exit(2);
        }
    }
}
