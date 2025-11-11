package tasks;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.HashMap;
import storage.Order;
import logging.LogManager;
import storage.Item;
import exceptions.ExceptionHandler;

public class TaskManager{

    private String taskmanagerId;
    private Deque<Tasks> taskQueue = new ConcurrentLinkedDeque<>();
    private Deque<Tasks> needAddingItemsQueue = new ConcurrentLinkedDeque<>();
    private Map<String, Tasks> activeTasks;
    private LogManager logManager;
    private LinkedList<Tasks> completedTasksList;
    private final static int MAX_COMPLETED_TASKS = 10; //the amount of completed tasks in memory

    public TaskManager(String id) throws IOException{
        this.taskmanagerId = id;
        this.taskQueue = new LinkedList<>();
        this.needAddingItemsQueue = new LinkedList<>();
        this.activeTasks = new HashMap<>();
        this.completedTasksList = new LinkedList<>();

        try {
            this.logManager = new LogManager("./logs");
        } catch (Exception e) {
            // If logging cannot be initialized, continue without logging
            this.logManager = null;
            ExceptionHandler.handle(e, "tasks.TaskManager.<init>");
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

    //Used for when item is taken from StorageUnit.
    public void createTasksFromOrders(Order order) throws IOException{
    

	    	try {
	            List<Item> items = order.getItems();
	            
	            for (Item item : items) {
		            
		            Tasks t = null;
	                try {
	                    String suId = item.getStorageUnitId();
	                    if (suId != null && !suId.isBlank()) {
	                        // lookup storage unit and use its position as destination
	                        StorageUnitsStore sus = StorageUnitsStore.getInstance();
	                        StorageUnit su = sus.getUnits()
	                        		.stream().
	                        		filter(x -> x.getId().
	                        				equals(suId)).
	                        		findFirst().
	                        		orElse(null);
	                        if (su != null) {
	                            t = new Tasks(su.getPosition(), item);
	                        }
	                    }
	                } catch (Throwable ignore) {}
	                
	                if (t == null) t = new Tasks(item);
	                
	                // associate this task with the originating order
	                try { 
	                	t.setOrderId(order.getId()); 
	                } 
	                catch (Throwable ignore) {}
	                
	                this.addTask(t);

	            }
	        }
	        catch (Exception e){
	            ExceptionHandler.handle(e, "tasks.TaskManager.createTasksFromOrders");
	        }
    }

    //Used for when an item needs to be added to a storageUnit.
    public void createNeedsAddingTask(Item item){
        
    	Tasks t = null;
    	try {
            String suId = item.getStorageUnitId();
            if (suId != null && !suId.isBlank()) {
                StorageUnitsStore sus = StorageUnitsStore.getInstance();
                StorageUnit su = sus.getUnits()
                		.stream().
                		filter(x -> x.getId().
                				equals(suId)).
                		findFirst().
                		orElse(null);
                if (su != null) {
                    t = new Tasks(su.getPosition(), item);
                }
            }
        } catch (Throwable ignore) {}
        
        if (t == null) t = new Tasks(item);
        
        this.addTaskToNeedsAdding(t);

    }

    //this adds a task to queue
    public void addTask(Tasks task) {
        taskQueue.offer(task);    
    }

    //this adds a task to a queue of items that need to be put to units
    public void addTaskToNeedsAdding(Tasks task) {
        needAddingItemsQueue.offer(task);
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
            ExceptionHandler.handle(e, "tasks.TaskManager.requeueTask");
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
            ExceptionHandler.handle(e, "tasks.TaskManager.completeTask.manageCompletedList");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        try {
            if (logManager != null) {
                logManager.writeLog("taskmanagerlogs", timestamp + " - Added task " + task.getId() + " to active tasks ");
            } else {
                ExceptionHandler.handle(new RuntimeException("LogManager unavailable"), "tasks.TaskManager.completeTask.log");
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e, "tasks.TaskManager.completeTask.logWrite");
        }
    }


    public void displayStatus() {
        System.out.println("Active Tasks: " + activeTasks.size());
        System.out.println("Queued Tasks: " + taskQueue.size());
        System.out.println("Completed Tasks: " + completedTasksList.size());
    }

}