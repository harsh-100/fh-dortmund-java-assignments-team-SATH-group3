package tasks;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.Map;
import java.util.HashMap;
import storage.Order;
import logging.LogManager;
import storage.Item;

public class TaskManager{

    private String taskmanagerId;
    private Deque<Tasks> taskQueue = new LinkedList<>();
    private Map<String, Tasks> activeTasks;
    private LogManager logManager;
    private LinkedList<Tasks> completedTasksList;
    private final static int MAX_COMPLETED_TASKS = 10;
        private LogManager logManager;
    private final DateTimeFormatter df = DateTimeFormatter.ISO_DATE;

    public TaskManager(String id) throws IOException{
        this.taskmanagerId = id;
        this.taskQueue = new LinkedList<>();
        this.activeTasks = new HashMap<>();
        this.completedTasksList = new LinkedList<>();

        try {
            this.logManager = new LogManager("./logs");
        } catch (Exception e) {
            // If logging cannot be initialized, continue without logging
            this.logManager = null;
            System.err.println("Warning: could not initialize LogManager: " + e.getMessage());
        }
    }
    
    //------------------- GETTERS ------------------------------
    public String getTaskManagerId(){
        return taskmanagerId;
    }

    public Queue<Tasks> getTaskQueue() {
        return new LinkedList<>(taskQueue);
    }
    // get active tasks
    public Map<String, Tasks> getActiveTasks() {
        return new HashMap<>(activeTasks);
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public LinkedList<Tasks> getCompletedTasksList() {
        return completedTasksList;
    }


    //------------------- METHODS ------------------------------

    public List<Tasks> getPendingTasks() {
        return new LinkedList<>(taskQueue);
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
            System.err.println(e);
        }
    }

    public void addTask(Tasks task) {
        taskQueue.offer(task);    
    }

    public Tasks robotGetTask() {
        Tasks task = taskQueue.poll();
        return task;
    }

    public void requeueTask(Tasks task){
        try {
            this.taskQueue.addFirst(task);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void completeTask(Tasks task) {
        completedTasksList.add(task);

        try{
            if (this.completedTasksList.size() > MAX_COMPLETED_TASKS) {
                this.completedTasksList.removeLast();
            }
        }
        catch (Exception e){
            System.out.println("Error occured when running completeTask method.");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        logManager.writeLog("taskmanagerlogs", timestamp + " - Added task " + task.getId() + " to active tasks ");
    }

    public int getActiveTaskCount() {
        return activeTasks.size();
    }

    public void displayStatus() {
        System.out.println("Active Tasks: " + activeTasks.size());
        System.out.println("Pending Tasks: " + taskQueue.size());
    }

}