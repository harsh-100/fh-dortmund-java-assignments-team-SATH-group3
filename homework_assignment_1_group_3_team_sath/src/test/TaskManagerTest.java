package test;
import storage.Order;
import tasks.TaskManager;
import tasks.Tasks;

import java.io.IOException;
import storage.Item;

public class TaskManagerTest {

    public static void run() throws IOException {
        int failed = 0;
        System.out.println("Running TaskManager tests...");

        try { 
            testConstructorAndQueue();
            System.out.println("  testConstructorAndQueue: OK"); 
        } 
        catch (AssertionError e) 
        { 
            failed++; 
            System.err.println("  FAILED: " + e.getMessage()); 
        }

        try { 
            testAddTask(); 
            System.out.println("  testAddTask: OK"); 
        } 
        catch (AssertionError e) 
        { 
            failed++; 
            System.err.println("  FAILED: " + e.getMessage()); 
        }

        try 
        { 
            testRequeueTask(); 
            System.out.println("  testRequeueTask: OK"); 
        } 
        catch (AssertionError e) 
        { 
            failed++; 
            System.err.println("  FAILED: " + e.getMessage()); 
        }

        try 
        {
            testCompletedTasks(); 
            System.out.println("  testCompletedTasks: OK"); 
        } 
        catch (AssertionError e) 
        {
            failed++; 
            System.err.println("  FAILED: " + e.getMessage()); 
        }

        if (failed > 0) throw new AssertionError("TaskManager tests failed: " + failed);
    }

    static void testConstructorAndQueue() throws IOException {
        TaskManager t = new TaskManager("TaskManager-1");
        assert "TaskManager-1".equals(t.getTaskManagerId()) : "id mismatch";
        assert t.getTaskQueue() != null : "taskQueue should be initialized";
        assert t.getActiveTasks() != null : "activeTasks should be initialized";
        assert t.getCompletedTasksList() != null : "completedTasks should be initialized";
        
    }
    
    static void testAddTask() throws IOException {
        TaskManager t = new TaskManager("TaskManager-1");
        Item i = new Item("I-1", "Item 1", 24.4);
        Tasks task = new Tasks("task1", i);
        t.addTask(task);
        assert t.getTaskQueue().size() > 0 : "TaskQueue should have an item in it.";
    }
    
    static void testRequeueTask() throws IOException {
     
        TaskManager t = new TaskManager("TaskManager-1");
        Item i1 = new Item("I-1", "Item 1", 24.4);
        Item i2 = new Item("I-2", "Item 2", 20.2);
        Item i3 = new Item("I-3", "Item 3", 7.7);
        Tasks task1 = new Tasks("task1", i1);
        Tasks task2 = new Tasks("task2", i2);
        
        t.addTask(task1);
        t.addTask(task2);
        assert t.getTaskQueue().size() == 2 : "TaskQueue should have 2 items in it.";
        
        Tasks task3 = new Tasks("task3", i3);
        
        t.requeueTask(task3);

        assert t.getTaskQueue().size() > 0 : "TaskQueue should not be empty.";
        
        assert "task3".equals(t.robotGetTask().getId()) : "Method returns wrong object.";
    }
    
    static void testCompletedTasks() throws IOException {
        TaskManager t = new TaskManager("taskmanager");
        Item item = new Item("I-1", "Item 1", 7.7);
        
        for (int i = 0; i < 11; i++){
            Tasks task = new Tasks("task" + i, item);
            t.completeTask(task);            
        }

        assert t.getCompletedTasksList().size() <= 10 : "There should only be 10 tasks in this list";

    }

}