package test;

import storage.StorageUnit;
import storage.Item;
import utils.Position;

/**
 * 5 lightweight tests for storage.StorageUnit
 */
public class TestStorageUnit {

    public static void run() {
        int failed = 0;
        System.out.println("Running StorageUnit tests...");

        try { testAddUpToCapacity(); System.out.println("  testAddUpToCapacity: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testCannotAddBeyondCapacity(); System.out.println("  testCannotAddBeyondCapacity: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testRemoveItem(); System.out.println("  testRemoveItem: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testRemainingCapacity(); System.out.println("  testRemainingCapacity: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testToStringContainsId(); System.out.println("  testToStringContainsId: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }

        if (failed > 0) throw new AssertionError("StorageUnit tests failed: " + failed);
    }

    static void testAddUpToCapacity() {
        StorageUnit su = new StorageUnit("SU1", 2, new Position(0,0));
        Item i1 = new Item("I1","One",1.0);
        Item i2 = new Item("I2","Two",2.0);
        assert su.addItems(i1) : "should add i1";
        assert su.addItems(i2) : "should add i2";
        assert su.getCurrentItemCount() == 2 : "count should be 2";
    }

    static void testCannotAddBeyondCapacity() {
        StorageUnit su = new StorageUnit("SU2", 1, new Position(0,0));
        Item i1 = new Item("A","A",1.0);
        Item i2 = new Item("B","B",1.0);
        assert su.addItems(i1) : "should add first";
        assert !su.addItems(i2) : "should NOT add second (capacity)";
    }

    static void testRemoveItem() {
        StorageUnit su = new StorageUnit("SU3", 2, new Position(0,0));
        Item i = new Item("R","Rem",1.0);
        su.addItems(i);
        assert su.removeItems("R") : "remove should return true";
        assert su.getCurrentItemCount() == 0 : "count should be 0 after removal";
    }

    static void testRemainingCapacity() {
        StorageUnit su = new StorageUnit("SU4", 3, new Position(0,0));
        su.addItems(new Item("a","a",1));
        su.addItems(new Item("b","b",1));
        assert Math.abs(su.getRemainingCapacity() - 1.0) < 1e-9 : "remaining capacity should be 1";
    }

    static void testToStringContainsId() {
        StorageUnit su = new StorageUnit("SU5", 5, new Position(0,0));
        String s = su.toString();
        assert s.contains("SU5") : "toString should contain id";
    }
}
