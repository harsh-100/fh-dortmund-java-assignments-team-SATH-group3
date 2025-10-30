package tasks;

import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;
import java.util.HashMap;
// import industrialProcess.AGV;
import storage.Order;
import logging.LogManager;
// import tasks.Tasks;


public class TaskManager{

    // private Queue<Tasks> taskQueue;
    private Deque<Tasks> taskQueue = new LinkedList<>();
    //private List<Tasks> completedTasks;

    private Map<String, Tasks> activeTasks;
    private LogManager logManager;

    private final static int MAX_COMPLETED_TASKS = 10;
    private LinkedList<Tasks> completedTasksList;

    public TaskManager() throws IOException{
        this.taskQueue = new LinkedList<>();
        //this.completedTasks = new ArrayList<>();
        this.activeTasks = new HashMap<>();
        this.completedTasksList = new LinkedList<>();
        this.logManager = new LogManager("logs/taskmanagerlogs");
    }

    public void createTasksFromOrders(Order order) throws IOException{
        //funktio saa parametrinä order objektin jonka se sitten jakaa itemeiksi ja itemeistä tehdään taskeja

    }

    public void addTask(Tasks task) {

        taskQueue.offer(task);
        //get the timestamp for logging
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        //write to log the string with timestamp and text to add
        logManager.writeLog("taskmanagerlogs", timestamp + " - Added task " + task.getId() + " to queue.");
    }

    //assignTask
    public Tasks robotGetTask() {
        Tasks task = taskQueue.poll();
        return task;
    }

    //requeueTask
    public void requeueTask(Tasks task){
        try {
            this.taskQueue.addFirst(task);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //completeTask for robots to add tasks to completed.
    // Only MAX_COMPLETED_TASKS amount of tasks can be in at a given moment.
    public void completeTask(Tasks task) {
        completedTasksList.add(task);

        if (this.completedTasksList.size() > MAX_COMPLETED_TASKS) {
            this.completedTasksList.removeLast();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        logManager.writeLog("taskmanagerlogs", timestamp + " - Added task " + task.getId() + " to active tasks ");
    }

    // get queue
    public Queue<Tasks> getTaskQueue() {
        return new LinkedList<>(taskQueue);
    }


    // get active tasks
    public Map<String, Tasks> getActiveTasks() {
        return new HashMap<>(activeTasks);
    }

    public LinkedList<Tasks> getCompletedTasksList() {
        return completedTasksList;
    }

    // get pending tasks
    public List<Tasks> getPendingTasks() {
        return new LinkedList<>(taskQueue);
    }

    // active task count 
    public int getActiveTaskCount() {
        return activeTasks.size();
    }

    public void displayStatus() {
        System.out.println("Active Tasks: " + activeTasks.size());
        System.out.println("Pending Tasks: " + taskQueue.size());
    }

}