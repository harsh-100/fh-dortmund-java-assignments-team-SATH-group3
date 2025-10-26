package tasks;


import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import industrialProcess.AGV;


public class TaskManager{


    private Queue<Tasks> taskQueue;
    private List<Tasks> completedTasks;
    private Map<String, Tasks> activeTasks;

    public TaskManager() {
        this.taskQueue = new LinkedList<>();
        this.completedTasks = new ArrayList<>();
        this.activeTasks = new HashMap<>();
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
        return true;
    }

    public void removeTask(String taskId) {
        Tasks task = activeTasks.remove(taskId);

        if(task != null){
            task.setStatus(Tasks.TaskStatus.COMPLETED);
            completedTasks.add(task);
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