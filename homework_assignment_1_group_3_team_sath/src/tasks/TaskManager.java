package tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import industrialProcess.AGV;
import logging.LogManager;


public class TaskManager{


    private Queue<Tasks> taskQueue;
    private List<Tasks> completedTasks;
    private Map<String, Tasks> activeTasks;
    private LogManager logManager;
    private final DateTimeFormatter df = DateTimeFormatter.ISO_DATE;

    public TaskManager() {
        this.taskQueue = new LinkedList<>();
        this.completedTasks = new ArrayList<>();
        this.activeTasks = new HashMap<>();
        try {
            this.logManager = new LogManager("./logs");
        } catch (Exception e) {
            // If logging cannot be initialized, continue without logging
            this.logManager = null;
            System.err.println("Warning: could not initialize LogManager: " + e.getMessage());
        }
    }

    public void addTask(Tasks  task) {
        taskQueue.offer(task);
    }

    // assign task to the robot 

    public boolean assignTaskToRobot(AGV robot) {

        if (taskQueue.isEmpty()) {
            return false;
        }
        if (!robot.isAvailable()) {
            return false;
        }
        Tasks task = taskQueue.poll();
        task.setRobotId(robot.getId());
        task.setStatus(Tasks.TaskStatus.IN_PROGRESS);
        activeTasks.put(task.getId(), task);
        // write per-AGV daily log
        if (logManager != null) {
            String date = df.format(LocalDate.now());
            String fileName = String.format("AGV-%s-%s.log", robot.getId(), date);
            String msg = String.format("[%s] Assigned task %s to AGV %s — status=%s", LocalDateTime.now(), task.getId(), robot.getId(), task.getStatus());
            logManager.writeLog(fileName, msg);
        }
        return true;
    }

    public void removeTask(String taskId) {
        Tasks task = activeTasks.remove(taskId);

        if(task != null){
            task.setStatus(Tasks.TaskStatus.COMPLETED);
            completedTasks.add(task);
            // write completion to AGV log
            if (logManager != null && task.getRobotId() != null) {
                String date = df.format(LocalDate.now());
                String fileName = String.format("AGV-%s-%s.log", task.getRobotId(), date);
                String msg = String.format("[%s] Completed task %s by AGV %s — status=%s", LocalDateTime.now(), task.getId(), task.getRobotId(), task.getStatus());
                logManager.writeLog(fileName, msg);
            }
        }
    }


    // get queue
    public Queue<Tasks> getTaskQueue() {
        return new LinkedList<>(taskQueue);
    }

    // get completed tasks
    public List<Tasks> getCompletedTasks() {
        return new ArrayList<>(completedTasks);
    }

    // get active tasks
    public Map<String, Tasks> getActiveTasks() {
        return new HashMap<>(activeTasks);
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
        System.out.println("Completed Tasks: " + completedTasks.size());
    }

}