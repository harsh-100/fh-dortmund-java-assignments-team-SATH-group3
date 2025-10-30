package test;

import tasks.Tasks;
import storage.Item;

public class TasksTest {

    public static void run() {
        int failed = 0;
        System.out.println("Running Tasks tests...");

        try { testConstructorAndGetters(); System.out.println("  testConstructorAndGetters: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testSetStatusAndRobot(); System.out.println("  testSetStatusAndRobot: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testIsComplete(); System.out.println("  testIsComplete: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testToStringContainsInfo(); System.out.println("  testToStringContainsInfo: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testConstructorWithItemOnly(); System.out.println("  testConstructorWithItemOnly: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }

        if (failed > 0) throw new AssertionError("Tasks tests failed: " + failed);
    }

    static void testConstructorAndGetters() {
        Item it = new Item("TI-1", "Thing", 2.2);
        Tasks t = new Tasks("TID-1", it);
        assert "TID-1".equals(t.getId()) : "id mismatch";
        assert t.getItems() != null : "item should be present";
    }

    static void testConstructorWithItemOnly() {
        Item it = new Item("TI-2", "Thing2", 3.3);
        Tasks t = new Tasks("TID-2", it);
        assert t.getDestination() == null : "destination should be null in this ctor";
    }

    static void testSetStatusAndRobot() {
        Item it = new Item("TI-3", "Thing3", 1.1);
        Tasks t = new Tasks("TID-3", it);
        t.setStatus(Tasks.TaskStatus.IN_PROGRESS);
        assert t.getStatus() == Tasks.TaskStatus.IN_PROGRESS : "status should be IN_PROGRESS";
        t.setRobotId("AGV-1");
        assert "AGV-1".equals(t.getRobotId()) : "robotId mismatch";
    }

    static void testIsComplete() {
        Item it = new Item("TI-4", "Thing4", 4.4);
        Tasks t = new Tasks("TID-4", it);
        assert !t.isComplete() : "new task should not be complete";
        t.setStatus(Tasks.TaskStatus.COMPLETED);
        assert t.isComplete() : "task should be complete after setting status";
    }

    static void testToStringContainsInfo() {
        Item it = new Item("TI-5", "Thing5", 5.5);
        Tasks t = new Tasks("TID-5", it);
        String s = t.toString();
        assert s.contains("TID-5") : "toString missing id";
    }
}
