package tasks;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Map;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import storage.Order;
import logging.LogManager;
import storage.Item;
import exceptions.ExceptionHandler;

public class TaskManager{

    private String taskmanagerId;
    private final ConcurrentLinkedDeque<Tasks> taskQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentMap<String, Tasks> activeTasks = new ConcurrentHashMap<>();
    private logging.LogManager logManager;
    private final ConcurrentLinkedDeque<Tasks> completedTasksList = new ConcurrentLinkedDeque<>();
    private final CopyOnWriteArrayList<TaskListener> listeners = new CopyOnWriteArrayList<>();
    private final AtomicInteger pendingCount = new AtomicInteger(0);
    private final static int MAX_COMPLETED_TASKS = 1000; // larger buffer for tests
    private final DateTimeFormatter df = DateTimeFormatter.ISO_DATE;

    public TaskManager(String id) throws IOException{
        this.taskmanagerId = id;
        try {
            this.logManager = logging.LogManager.getInstance("logs");
        } catch (Exception e) {
            // If logging cannot be initialized, continue without logging
            this.logManager = null;
            ExceptionHandler.handle(e, "tasks.TaskManager.<init>");
        }
    }

    public interface TaskListener {
        void onPendingCountChanged(int newPending);
        void onCompletedCountChanged(int newCompleted);
    }

    public void addListener(TaskListener l) { listeners.addIfAbsent(l); }
    public void removeListener(TaskListener l) { listeners.remove(l); }
    
    //------------------- GETTERS ------------------------------
    public String getTaskManagerId(){
        return taskmanagerId;
    }

    public List<Tasks> getTaskQueue() {
        return new ArrayList<>(taskQueue);
    }
    // get active tasks
    public Map<String, Tasks> getActiveTasks() {
        return new ConcurrentHashMap<>(activeTasks);
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public LinkedList<Tasks> getCompletedTasksList() {
        // return a copy to avoid concurrent modification issues in callers
        return new LinkedList<>(completedTasksList);
    }


    //------------------- METHODS ------------------------------

    public List<Tasks> getPendingTasks() {
        return new ArrayList<>(taskQueue);
    }

    public void createTasksFromOrders(Order order) throws IOException{
        //funktio saa parametrinä order objektin jonka se sitten jakaa itemeiksi ja itemeistä tehdään taskeja
        
        try {
            List<Item> items = order.getItems();
            // StorageUnit storageUnit = new StorageUnit(null, MAX_COMPLETED_TASKS, null);

            for (Item item : items) {
                Tasks t = new Tasks(LocalDateTime.now().toString(), item);
                this.addTask(t);
            }
        }
        catch (Exception e){
            ExceptionHandler.handle(e, "tasks.TaskManager.createTasksFromOrders");
        }
    }

    public void addTask(Tasks task) {
        taskQueue.offer(task);
        int p = pendingCount.incrementAndGet();
        for (TaskListener l : listeners) {
            try { l.onPendingCountChanged(p); } catch (Throwable ignore) {}
        }
    }

    public Tasks robotGetTask() {
        Tasks t = taskQueue.poll();
        if (t != null) {
            int p = pendingCount.decrementAndGet();
            for (TaskListener l : listeners) {
                try { l.onPendingCountChanged(p); } catch (Throwable ignore) {}
            }
        }
        return t;
    }

    public void requeueTask(Tasks task){
        try {
            this.taskQueue.addFirst(task);
        }
        catch (Exception e) {
            ExceptionHandler.handle(e, "tasks.TaskManager.requeueTask");
        }
    }

    public void completeTask(Tasks task) {
        completedTasksList.addLast(task);
        try{
            while (this.completedTasksList.size() > MAX_COMPLETED_TASKS) {
                this.completedTasksList.removeFirst();
            }
        }
        catch (Exception e){
            ExceptionHandler.handle(e, "tasks.TaskManager.completeTask.manageCompletedList");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        try {
            if (logManager != null) {
                String date = LocalDate.now().toString();
                String fileName = String.format("taskmanagerlogs/%s-%s.log", this.getTaskManagerId(), date);
                logManager.writeLog(fileName, timestamp + " - Added task " + task.getId() + " to active tasks ");
            } else {
                ExceptionHandler.handle(new RuntimeException("LogManager unavailable"), "tasks.TaskManager.completeTask.log");
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e, "tasks.TaskManager.completeTask.logWrite");
        }
        // notify listeners about completed count
        int completed = completedTasksList.size();
        for (TaskListener l : listeners) {
            try { l.onCompletedCountChanged(completed); } catch (Throwable ignore) {}
        }
    }

    public int getActiveTaskCount() {
        return activeTasks.size();
    }

    public void displayStatus() {
        System.out.println("Active Tasks: " + activeTasks.size());
        System.out.println("Pending Tasks: " + taskQueue.size());
    }


    //------------- That's me ,Artem. I created these methods for the third homework --------------


}
