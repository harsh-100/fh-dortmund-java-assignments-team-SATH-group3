package robots;

import utils.IGridEntity;
import tasks.TaskManager;
import tasks.Tasks;
import java.awt.Point;
import charging.ChargingStation;
import warehouse.Warehouse;
import logging.LogManager;
import java.io.IOException;
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
    private static final double LOW_BATTERY_THRESHOLD = 20.0;
    private static final double BATTERY_COST_PER_MOVE = 0.5;
    private static final double CHARGE_RATE_PER_TICK = 4.0;
    private static final int TASK_DURATION_IN_TICKS = 20; // just a temporary solution as well
    private static final int CHARGING_DURATION_IN_TICKS = 100000;
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
                
                Thread.sleep(100); 
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
                && state != RobotState.CHARGING) {
            
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
                this.currentStation.occupy();
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
            if (this.currentTask != null) {
                this.currentTask = taskManager.robotGetTask();

                if (fileName != null) {
                    String msg = String.format("[%s] Robot %s starts executing of the new task with id: %s", LocalDateTime.now(), this.getID(), this.currentTask.getId());
                    logManager.writeLog(fileName, msg);
                }
            }
        }
        
   
        
//     else if (state == RobotState.CHARGING && chargeTimer >= CHARGING_DURATION_IN_TICKS) {
        else if (state == RobotState.CHARGING && this.batteryLevel == MAX_BATTERY) {
//            this.batteryLevel = MAX_BATTERY;
            this.state = RobotState.IDLE;
            this.chargeTimer = 0;
            
            if (this.currentStation != null) {
//             this.currentStation.release(); // ?
                warehouse.releaseStation(this.currentStation);
                this.currentStation = null;
            }
            
        }
        
        else if (state == RobotState.WORKING && taskTimer >= TASK_DURATION_IN_TICKS) {
            
            taskManager.completeTask(this.currentTask);
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
            tryToGetNewTask();
            if (fileName != null) {
                String msg = String.format("[%s] Robot %s is IDLE now and tries to get a new task (it's battery level is %s)", LocalDateTime.now(), this.getID(), this.batteryLevel);
                logManager.writeLog(fileName, msg);
            }
            break;
            
            
        case WORKING:
            workOnTask();
            if (fileName != null) {
                String msg = String.format("[%s] Robot %s is working on task with id: %s (it's battery level is %s)", LocalDateTime.now(), this.getID(), this.currentTask.getId(), this.batteryLevel);
                logManager.writeLog(fileName, msg);
            }
            break;
            
        case CHARGING:
            chargeBattery();
            if (fileName != null) {
                String msg = String.format("[%s] Robot %s is charging (it's battery level is %s)", LocalDateTime.now(), this.getID(), this.batteryLevel);
                logManager.writeLog(fileName, msg);
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
