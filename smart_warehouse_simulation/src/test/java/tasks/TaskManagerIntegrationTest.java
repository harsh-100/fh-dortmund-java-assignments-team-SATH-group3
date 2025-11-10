package tasks;

import org.junit.jupiter.api.Test;

import storage.Item;
import storage.Order;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskManagerIntegrationTest {

    @Test
    public void testCreateTasksFromOrderIncreasesPending() throws IOException {
        TaskManager tm = new TaskManager("tm-integ-test");
        Order o = new Order("O1");
        o.addItem(new Item("it1","One",1.0));
        o.addItem(new Item("it2","Two",1.0));

        int before = tm.getPendingTasks().size();
        tm.createTasksFromOrders(o);
        int after = tm.getPendingTasks().size();

        assertEquals(before + 2, after, "Pending tasks should increase by number of items in order");
    }
}
