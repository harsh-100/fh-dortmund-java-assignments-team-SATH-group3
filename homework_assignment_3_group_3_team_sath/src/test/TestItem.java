package test;

import storage.Item;

public class TestItem {

    public static void run() {
        int failed = 0;
        System.out.println("Running Item tests...");

        try { testConstructorAndGetters(); System.out.println("  testConstructorAndGetters: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testToStringContainsFields(); System.out.println("  testToStringContainsFields: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testWeightPrecision(); System.out.println("  testWeightPrecision: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testMultipleInstancesIndependence(); System.out.println("  testMultipleInstancesIndependence: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }
        try { testToStringFormat(); System.out.println("  testToStringFormat: OK"); } catch (AssertionError e) { failed++; System.err.println("  FAILED: " + e.getMessage()); }

        if (failed > 0) throw new AssertionError("Item tests failed: " + failed);
    }

    static void testConstructorAndGetters() {
        Item it = new Item("I-1", "Bolt", 0.25);
        assert "I-1".equals(it.getId()) : "id mismatch";
        assert "Bolt".equals(it.getName()) : "name mismatch";
        assert Math.abs(it.getWeight() - 0.25) < 1e-9 : "weight mismatch";
    }

    static void testToStringContainsFields() {
        Item it = new Item("X", "Nut", 0.1);
        String s = it.toString();
        assert s.contains("X") : "toString missing id";
        assert s.contains("Nut") : "toString missing name";
    }

    static void testWeightPrecision() {
        Item it = new Item("W", "Weighty", 1.3333333);
        assert Math.abs(it.getWeight() - 1.3333333) < 1e-9 : "precision lost";
    }

    static void testMultipleInstancesIndependence() {
        Item a = new Item("A","Aname",1.0);
        Item b = new Item("B","Bname",2.0);
        assert !a.getId().equals(b.getId()) : "ids should differ";
        assert !a.getName().equals(b.getName()) : "names should differ";
    }

    static void testToStringFormat() {
        Item it = new Item("ID","Name",3.21);
        String s = it.toString();
        // simple sanity: contains parentheses and weight
        assert s.contains("(") && s.contains(")") : "toString format unexpected";
        assert s.contains("3.21") : "weight not present in toString";
    }
}
