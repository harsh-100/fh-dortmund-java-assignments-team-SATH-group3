package test;

import storage.Order;
import storage.Item;

public class TestOrder {

    public static void run() {
        int failed = 0;
        System.out.println("Running Order tests...");

        try { testConstructorAndStatus(); System.out.println("  testConstructorAndStatus: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testAddAndRemoveItems(); System.out.println("  testAddAndRemoveItems: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testTimestampSet(); System.out.println("  testTimestampSet: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testSetStatus(); System.out.println("  testSetStatus: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testToStringContainsInfo(); System.out.println("  testToStringContainsInfo: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }

        if (failed > 0) throw new AssertionError("Order tests failed: " + failed);
    }

    static void testConstructorAndStatus() {
        Order o = new Order("O-1");
        assert "O-1".equals(o.getId()) : "id mismatch";
        assert o.getStatus() != null : "status should be initialized";
    }

    static void testAddAndRemoveItems() {
        Order o = new Order("O-2");
        Item a = new Item("A","X",1.0);
        o.addItem(a);
        assert o.getItems().size() == 1 : "should have 1 item";
        o.removeItem(a);
        assert o.getItems().isEmpty() : "should be empty after removal";
    }

    static void testTimestampSet() {
        Order o = new Order("O-3");
        assert o.getTimestamp() > 0 : "timestamp should be set";
    }

    static void testSetStatus() {
        Order o = new Order("O-4");
        o.setStatus(Order.Status.SHIPPED);
        assert o.getStatus() == Order.Status.SHIPPED : "status should be SHIPPED";
    }

    static void testToStringContainsInfo() {
        Order o = new Order("O-5");
        String s = o.toString();
        assert s.contains("O-5") : "toString missing id";
        assert s.contains("Status") || s.contains("Status:") || s.contains("Status") : "toString missing status";
    }
}
