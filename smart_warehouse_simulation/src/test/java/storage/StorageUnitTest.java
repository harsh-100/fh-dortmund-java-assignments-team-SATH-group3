package storage;

import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

public class StorageUnitTest {

    @Test
    public void testAddRemoveAndCapacity() {
        StorageUnit su = new StorageUnit("S1", 3.0, new Point(1,2));
        assertEquals("S1", su.getId());
        assertEquals(3.0, su.getCapacity(), 1e-6);
        assertEquals(0, su.getCurrentItemCount());

        Item i1 = new Item("i1","One",1.0);
        Item i2 = new Item("i2","Two",1.0);
        Item i3 = new Item("i3","Three",1.0);

        assertTrue(su.addItems(i1));
        assertTrue(su.addItems(i2));
        assertTrue(su.addItems(i3));
        // capacity is treated as integer count in this implementation
        assertEquals(3, su.getCurrentItemCount());

        // adding a fourth should fail (capacity 3)
        Item i4 = new Item("i4","Four",1.0);
        assertFalse(su.addItems(i4));

        assertTrue(su.removeItems("i2"));
        assertEquals(2, su.getCurrentItemCount());
    }
}
