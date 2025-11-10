package storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    public void testItemProperties() {
        Item it = new Item("I1", "Widget", 2.5);
        assertEquals("I1", it.getId());
        assertEquals("Widget", it.getName());
        assertEquals(2.5, it.getWeight(), 1e-6);
        String s = it.toString();
        assertTrue(s.contains("I1"));
        assertTrue(s.contains("Widget"));
    }
}
