package app.demo;

import storage.Item;
import storage.Order;
import tasks.TaskManager;
import warehouse.Warehouse;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple scenario runner that programmatically creates orders/tasks and controls the simulation.
 * Scenarios are intentionally short and safe for demo purposes.
 */
public class ScenarioRunner {

    private static final ScheduledExecutorService SCHED = Executors.newScheduledThreadPool(2);
    private static final Random RAND = new Random();

    public static void runScenarioA(Warehouse warehouse, TaskManager tm) {
        // Scenario A: single order, single robot processing
        SCHED.execute(() -> {
            try {
                Order o = new Order("SCEN-A-" + System.currentTimeMillis());
                o.addItem(new Item("A1", "Widget-A", 1.2));
                tm.createTasksFromOrders(o);

                if (!warehouse.isSimulationRunning()) warehouse.startSimulation();

                // stop after 8 seconds for demo
                SCHED.schedule(() -> warehouse.stopSimulation(), 8, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void runScenarioB(Warehouse warehouse, TaskManager tm) {
        // Scenario B: multiple orders, multiple robots (stress)
        SCHED.execute(() -> {
            try {
                for (int i = 0; i < 30; i++) {
                    Order o = new Order("SCEN-B-" + i + "-" + System.currentTimeMillis());
                    int count = 1 + RAND.nextInt(3);
                    for (int j = 0; j < count; j++) {
                        o.addItem(new Item("B" + i + "-" + j, "BulkItem", 0.5 + RAND.nextDouble()));
                    }
                    tm.createTasksFromOrders(o);
                    // small gap
                    Thread.sleep(50);
                }

                if (!warehouse.isSimulationRunning()) warehouse.startSimulation();

                // Let it run for 12s then stop
                SCHED.schedule(() -> warehouse.stopSimulation(), 12, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void runScenarioC(Warehouse warehouse, TaskManager tm) {
        // Scenario C: drain robot batteries to trigger charging behavior
        SCHED.execute(() -> {
            try {
                // set robot batteries low to force charging behavior
                warehouse.getRobots().forEach(r -> r.setBatteryForTest(5.0));

                // enqueue a few long-running tasks
                for (int i = 0; i < 8; i++) {
                    Order o = new Order("SCEN-C-" + i + "-" + System.currentTimeMillis());
                    o.addItem(new Item("C" + i, "ChargeTask", 1.0));
                    tm.createTasksFromOrders(o);
                }

                if (!warehouse.isSimulationRunning()) warehouse.startSimulation();

                // stop after 15s
                SCHED.schedule(() -> warehouse.stopSimulation(), 15, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
