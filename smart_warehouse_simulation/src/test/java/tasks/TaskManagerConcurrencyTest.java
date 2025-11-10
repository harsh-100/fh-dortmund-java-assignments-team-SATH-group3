package tasks;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskManagerConcurrencyTest {

    @Test
    public void testMultipleWorkersProcessAllTasks() throws IOException, InterruptedException {
        final int NUM_TASKS = 200;
        final int NUM_WORKERS = 8;

        TaskManager tm = new TaskManager("tm-concurrency-test");

        // Enqueue tasks
        for (int i = 0; i < NUM_TASKS; i++) {
            Tasks t = new Tasks("task-" + i, null);
            tm.addTask(t);
        }

        ExecutorService exec = Executors.newFixedThreadPool(NUM_WORKERS);
        AtomicInteger processed = new AtomicInteger(0);

        CountDownLatch done = new CountDownLatch(NUM_WORKERS);

        for (int w = 0; w < NUM_WORKERS; w++) {
            exec.submit(() -> {
                try {
                    while (processed.get() < NUM_TASKS) {
                        Tasks t = tm.robotGetTask();
                        if (t != null) {
                            // simulate work
                            tm.completeTask(t);
                            processed.incrementAndGet();
                        } else {
                            Thread.sleep(5);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        // wait for workers to finish or timeout
        boolean finished = done.await(10, TimeUnit.SECONDS);
        exec.shutdownNow();

        // Ensure all tasks were processed
        assertEquals(NUM_TASKS, processed.get(), "All tasks should be processed by workers");
        // And queue should be empty
        assertEquals(0, tm.getPendingTasks().size(), "Task queue should be empty after processing");
    }
}
