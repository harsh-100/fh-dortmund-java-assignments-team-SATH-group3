package robots;

import utils.IGridEntity;
import tasks.TaskManager;
import tasks.Tasks;
import java.awt.Point;
import charging.ChargingStation;
import warehouse.Warehouse;
import logging.LogManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Robot implements Runnable, IGridEntity  {
    
    private static int num = 0;
    private final String id;
    private Point currentPosition;
    private double batteryLevel;
    private final TaskManager taskManager;
    private Tasks currentTask;
    private LogManager logManager;
    public enum RobotState{
        IDLE,
        WORKING,
        CHARGING,
        WAITING_FOR_CHARGE
    }
    
    private RobotState state;
    private RobotState lastLoggedState = null;
    private ChargingStation currentStation;
    public Warehouse warehouse;
    private final DateTimeFormatter df = DateTimeFormatter.ISO_DATE;
    
    // ----------------------------------
    private int taskTimer = 0; // just a temporary solution
    private int chargeTimer = 0; // temporary solution
    // ----------------------------------

    // ----------- for second simulation ---------
    private long waitingStartTime;
    private static final long MAX_WAIT_TIME_MS = 1000;  // 15 seconds for test
    //----------------------------------------
    
    private static final double MAX_BATTERY = 100.0;
    private static final double LOW_BATTERY_THRESHOLD = 50.0;
    private static final double BATTERY_COST_PER_MOVE = 0.5;
    private static final double CHARGE_RATE_PER_TICK = 4.0;
    // Make tasks last roughly 10 seconds: with TICK_DELAY_MS=100ms, 100 ticks ≈ 10s
    private static final int TASK_DURATION_IN_TICKS = 100; // increased to show progress in UI (~10s)
    // charging lasts ~10s as well (100 ticks × 100ms)
    private static final int CHARGING_DURATION_IN_TICKS = 100;
    private static final double BATTERY_COST_PER_TICK = 0.5;
    private static final int TICK_DELAY_MS = 100;
    
    public Robot(Warehouse warehouse, Point currentPosition, TaskManager taskManager) {
        this.id = "robot_" + num;
        num++;
        this.currentPosition = currentPosition;
        this.batteryLevel = MAX_BATTERY;
        this.state = RobotState.IDLE;
        this.taskManager = taskManager;
        this.warehouse = warehouse;
        
        try {
            this.logManager = logging.LogManager.getInstance("logs");
        } catch (Exception e) {
            System.out.println("LogManager wasn't created");
            this.logManager = null;
            System.err.println("Warning: could not initialize LogManager: " + e.getMessage());
        }
        
    }
    
    @Override
    public void run() {
        try {
            System.out.println("robot is started");
            while (true) {
                
                updateState();
                performAction();
                
                    Thread.sleep(TICK_DELAY_MS );
            }
        } catch (InterruptedException e) {
            System.out.println(id + " is stopped");
        }
    }
    
    public void updateState() {

        String fileName = null;

        if (logManager != null) {
            String date = df.format(LocalDate.now());
            fileName = String.format("RobotsLogs/%s-%s.log", this.getID(), date);
        }


        if (state == RobotState.WAITING_FOR_CHARGE){
            long waitTime = System.currentTimeMillis() - this.waitingStartTime;

            if (waitTime > MAX_WAIT_TIME_MS) {
                warehouse.leaveQueue(this);
                this.state = RobotState.IDLE;

                String msg = String.format("[%s] Robot %s left the queue", LocalDateTime.now(), this.getID());
                logManager.writeLog(fileName, msg);
            }
        }
        
        
        
    else if (batteryLevel < LOW_BATTERY_THRESHOLD
        && state != RobotState.CHARGING
        && state != RobotState.WORKING) {
            
            if (this.currentTask != null) {
                taskManager.requeueTask(this.currentTask);
                this.currentTask = null;
                this.taskTimer = 0; 
            }
            
            ChargingStation station = warehouse.requestCharging(this); // just a plug
            
            if (station != null) {
                this.state = RobotState.CHARGING;
                this.chargeTimer = 0;
                this.currentStation = station;
                this.currentStation.occupy(this);
                this.currentPosition = station.getLocation();

                if (fileName != null) {
                    String msg = String.format("[%s] Robot %s is charging at %s", LocalDateTime.now(), this.getID(), this.currentStation.getID());
                    logManager.writeLog(fileName, msg);
                }

            } else {
                this.state = RobotState.WAITING_FOR_CHARGE;
                if (fileName != null) {
                    String msg = String.format("[%s] Robot %s is in the charging queue", LocalDateTime.now(), this.getID());
                    logManager.writeLog(fileName, msg);
                }
                this.waitingStartTime = System.currentTimeMillis(); // Start of the timer for queue
            }
        }

        else if (state == RobotState.IDLE && batteryLevel > LOW_BATTERY_THRESHOLD) {
            // nothing special here; robot will attempt to get new task in performAction()
        }
        
   
        
//     else if (state == RobotState.CHARGING && chargeTimer >= CHARGING_DURATION_IN_TICKS) {
        else if (state == RobotState.CHARGING && this.chargeTimer >= CHARGING_DURATION_IN_TICKS) {
            // charging finished after configured ticks
            this.batteryLevel = MAX_BATTERY;
            this.state = RobotState.IDLE;
            this.chargeTimer = 0;

            if (this.currentStation != null) {
                warehouse.releaseStation(this.currentStation);
                this.currentStation = null;
            }
        }
        
        else if (state == RobotState.WORKING && taskTimer >= TASK_DURATION_IN_TICKS) {
            // task finished: notify TaskManager, reduce battery and become idle
            taskManager.completeTask(this.currentTask);
            // reduce battery by 20% upon task completion
            this.batteryLevel -= 20.0;
            if (this.batteryLevel < 0) this.batteryLevel = 0;
            this.currentTask = null;
            this.state = RobotState.IDLE;
            this.taskTimer = 0;
        }
        
        
    }
    
    public void performAction() {

        String fileName = null;
        
        if (logManager != null) {
            String date = df.format(LocalDate.now());
            fileName = String.format("RobotsLogs/%s-%s.log", this.getID(), date);
        }
        
        switch (this.state) {
        case IDLE:
            // Try to obtain a task. If a task is obtained, log it. Otherwise
            // only log IDLE when the state actually changed to avoid flooding logs.
            tryToGetNewTask();
            if (this.currentTask != null) {
                if (fileName != null) {
                    String msg = String.format("[%s] Robot %s starts executing the new task with id: %s", LocalDateTime.now(), this.getID(), this.currentTask.getId());
                    logManager.writeLog(fileName, msg);
                }
                lastLoggedState = RobotState.WORKING;
            } else {
                if (fileName != null && lastLoggedState != RobotState.IDLE) {
                    String msg = String.format("[%s] Robot %s is IDLE (battery=%.1f)", LocalDateTime.now(), this.getID(), this.batteryLevel);
                    logManager.writeLog(fileName, msg);
                    lastLoggedState = RobotState.IDLE;
                }
            }
            break;

        case WORKING:
            workOnTask();
            if (fileName != null && lastLoggedState != RobotState.WORKING) {
                String taskId = this.currentTask != null ? this.currentTask.getId() : "-";
                String msg = String.format("[%s] Robot %s is working on task with id: %s (battery=%.1f)", LocalDateTime.now(), this.getID(), taskId, this.batteryLevel);
                logManager.writeLog(fileName, msg);
                lastLoggedState = RobotState.WORKING;
            }
            break;

        case CHARGING:
            chargeBattery();
            if (fileName != null && lastLoggedState != RobotState.CHARGING) {
                String msg = String.format("[%s] Robot %s is charging (battery=%.1f)", LocalDateTime.now(), this.getID(), this.batteryLevel);
                logManager.writeLog(fileName, msg);
                lastLoggedState = RobotState.CHARGING;
            }
            break;

        case WAITING_FOR_CHARGE:
            // Similar to IDLE: log only on transition
            if (fileName != null && lastLoggedState != RobotState.WAITING_FOR_CHARGE) {
                String msg = String.format("[%s] Robot %s is waiting for charge (battery=%.1f)", LocalDateTime.now(), this.getID(), this.batteryLevel);
                logManager.writeLog(fileName, msg);
                lastLoggedState = RobotState.WAITING_FOR_CHARGE;
            }
            break;
        }
    }
    
    private void workOnTask() {
        this.taskTimer++;
        this.batteryLevel -= BATTERY_COST_PER_TICK;
        if (this.batteryLevel < 0)
            this.batteryLevel = 0; // exception in the future
    }
    
    private void chargeBattery() {
        this.chargeTimer++;
        this.batteryLevel += CHARGE_RATE_PER_TICK;
        if (this.batteryLevel > MAX_BATTERY)
            this.batteryLevel = MAX_BATTERY;
    }
    
    private void tryToGetNewTask() {
        Tasks newTask = taskManager.robotGetTask();
        if (newTask != null) {
            this.currentTask = newTask;
            try { this.currentTask.setRobotId(this.getID()); } catch (Throwable ignore) {}
            try { this.currentTask.setStatus(Tasks.TaskStatus.IN_PROGRESS); } catch (Throwable ignore) {}
            this.state = RobotState.WORKING;
            this.taskTimer = 0;
        }
        
    }

    public void assignStation(ChargingStation station) {
        if (this.state == RobotState.WAITING_FOR_CHARGE) {
            this.currentStation = station;
            this.state = RobotState.CHARGING;
            this.chargeTimer = 0;
            this.currentPosition = station.getLocation();

            // logs in the future
        }
    }
    
    
    public double getBattery() {
        return this.batteryLevel;
    }
    
    public RobotState getState() {
        return this.state;
    }
    
    public Tasks getCurrentTask() {
        return this.currentTask;
    }
    
    
    
    // -------- IGridEntity methods -------- 
    @Override
    public Point getLocation() {
        return this.currentPosition;
    }
    
    @Override
    public String getID() {
        return this.id;
    }
    
    
    
    // ----------- Methods for uni tests --------
    
    public void setBatteryForTest(double p) {
        this.batteryLevel = p;
    }
    
    public void setTaskTimerForTest(int t) {
        this.taskTimer = t;
    }
    
}
