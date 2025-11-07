package test;

public class TestAll {
    public static void main(String[] args) {
        try {
            // Run all test groups; each group throws AssertionError on failure
            TestItem.run();
            System.out.println();

            TestOrder.run();
            System.out.println();

            TestStorageUnit.run();
            System.out.println();

            TaskManagerTest.run();
            System.out.println();

            TasksTest.run();
            System.out.println();

            System.out.println("ALL TESTS PASSED");
        } catch (AssertionError e) {
            System.err.println("TEST RUN FAILED: " + e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            System.err.println("ERROR DURING TESTS: " + e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }
    }
}
