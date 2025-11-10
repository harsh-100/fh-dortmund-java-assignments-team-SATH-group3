package storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testAddRemoveItemsAndStatus() {
        Order o = new Order("ORD-1");
        assertEquals("ORD-1", o.getId());
        assertEquals(Order.Status.PENDING, o.getStatus());
        assertTrue(o.getItems().isEmpty());

        Item a = new Item("A","Alpha",1.0);
        Item b = new Item("B","Beta",2.0);
        o.addItem(a);
        o.addItem(b);
        assertEquals(2, o.getItems().size());

        o.removeItem(a);
        assertEquals(1, o.getItems().size());

        o.setStatus(Order.Status.SHIPPED);
        assertEquals(Order.Status.SHIPPED, o.getStatus());
    }
}
