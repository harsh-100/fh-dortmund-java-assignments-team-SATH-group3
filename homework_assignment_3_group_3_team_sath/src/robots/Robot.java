package robots;

import utils.IGridEntity;
import tasks.TaskManager;
import tasks.Tasks;
import java.awt.Point;
import charging.ChargingStation;
import utils.PathFinder;
import warehouse.Warehouse;
import logging.LogManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;


public class Robot implements Runnable, IGridEntity  {
	
	private static int num = 0;
    private final String id;
	private Point currentPosition;
	private double batteryLevel;
	private final TaskManager taskManager;
	private Tasks currentTask;
	private LogManager logManager;
    private PathFinder pathFinder;
    private Point locationOfStorageUnit;
    private Queue<Point> currentPath;
    private Point dropOffLocation;
    private Point chargingStationLocation;
    private Point robotsCamp;


    public enum RobotState{
		IDLE,
		WORKING,
		CHARGING,
        WAITING_FOR_CHARGE,
        MOVING_TO_CHARGE,
        MOVING_TO_IDLE_POINT
	}

    private enum WorkingState {
        GOING_TO_PICKUP,
        GOING_TO_DROPOFF  
    }
	
	private RobotState state;
    private WorkingState workingState;
	private ChargingStation currentStation;
	public Warehouse warehouse;
	private final DateTimeFormatter df = DateTimeFormatter.ISO_DATE;
	
	// ----------------------------------

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
    private static final int CHARGING_DURATION_IN_TICKS = 100000;
    private static final int TICK_DELAY_MS = 100;
    
    public Robot(Warehouse warehouse, Point currentPosition, TaskManager taskManager, PathFinder pathFinder) {
    	this.id = "robot_" + num;
    	num++;
    	this.currentPosition = currentPosition;
    	this.batteryLevel = MAX_BATTERY;
    	this.state = RobotState.IDLE;
    	this.taskManager = taskManager;
    	this.warehouse = warehouse;
        this.pathFinder = pathFinder;
        this.currentPath = new LinkedList<>();
        this.dropOffLocation = warehouse.getDropOffLocation();
        this.robotsCamp = warehouse.getIdleLocation();
    	
    	try {
            this.logManager = new LogManager("./logs/RobotsLogs");
        } catch (Exception e) {
            System.out.println("LogManager wasn't created");
            this.logManager = null;
            System.err.println("Warning: could not initialize LogManager: " + e.getMessage());
        }



        if (logManager != null) {
            String date = df.format(LocalDate.now());
            String fileName = String.format("%s-%s.log", this.getID(), date);
            String msg = String.format("[%s] Robot %s is on now ready to perform actions (it's battery level is %s)", LocalDateTime.now(), this.getID(), this.batteryLevel);
            logManager.writeLog(fileName, msg);
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
            fileName = String.format("%s-%s.log", this.getID(), date);
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
                && state != RobotState.MOVING_TO_CHARGE
                && state != RobotState.WAITING_FOR_CHARGE){
    		
    		if (this.currentTask != null) {
                taskManager.requeueTask(this.currentTask);
                this.currentTask = null;
            }
    		
    		ChargingStation station = warehouse.requestCharging(this); // just a plug
    		
    		if (station != null) {

                this.currentStation = station;
                this.state = RobotState.MOVING_TO_CHARGE;
                this.currentPath = pathFinder.findPath(this.currentPosition, station.getLocation());

                if (fileName != null) {
                    String msg = String.format("[%s] Robot %s starts moving to the charging station %s", LocalDateTime.now(), this.getID(), this.currentStation.getID());
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


        else if (state == RobotState.MOVING_TO_CHARGE && (currentPath == null || currentPath.isEmpty())){
            this.state = RobotState.CHARGING;
            this.chargeTimer = 0;

            if (this.currentStation != null) {
                this.currentStation.occupy();
            }

            if (fileName != null) {
                String msg = String.format("[%s] Robot %s is charging at the station %s", LocalDateTime.now(), this.getID(),  this.currentStation.getID());
                logManager.writeLog(fileName, msg);
            }
        }
    	

        else if (state == RobotState.CHARGING && this.batteryLevel >= MAX_BATTERY) {
            this.batteryLevel = MAX_BATTERY;
            this.chargeTimer = 0;
            
            if (this.currentStation != null) {
                warehouse.releaseStation(this.currentStation);
            	this.currentStation = null;
            }

            this.state = RobotState.MOVING_TO_IDLE_POINT;
            this.currentPath = pathFinder.findPath(this.currentPosition, warehouse.getIdleLocation());
            
        }

        else if (state == RobotState.MOVING_TO_IDLE_POINT && (currentPath == null || currentPath.isEmpty())){
            this.state = RobotState.IDLE;

            if (fileName != null) {
                String msg = String.format("[%s] Robot %s is at IDLE point and ready to get new tasks (it's battery level is %s)", LocalDateTime.now(), this.getID(), this.batteryLevel);
                logManager.writeLog(fileName, msg);
            }

        }
    	
    	else if (state == RobotState.WORKING && (currentPath == null || currentPath.isEmpty())) {

            if (this.workingState == WorkingState.GOING_TO_PICKUP) {

                this.workingState = WorkingState.GOING_TO_DROPOFF;
                System.out.println(this.dropOffLocation.toString());
                this.currentPath = pathFinder.findPath(this.currentPosition, this.dropOffLocation);
                System.out.println(currentPath);

            } else {

                if (this.currentTask != null) {
                    taskManager.completeTask(this.currentTask);
                    this.currentTask = null;
                    System.out.println("задача выполнена");
                    System.out.println(this.currentPosition.toString());
                }

                this.state = RobotState.MOVING_TO_IDLE_POINT;
                this.currentPath = pathFinder.findPath(this.currentPosition, warehouse.getIdleLocation());

            }
        }
    	
    }
    
    public void performAction() {

        String fileName = null;
    	
    	if (logManager != null) {
            String date = df.format(LocalDate.now());
            fileName = String.format("%s-%s.log", this.getID(), date);
        }
    	
    	switch (this.state) {

    	case IDLE:
    		tryToGetNewTask();
//            if (fileName != null) {
//                String msg = String.format("[%s] Robot %s is IDLE now and tries to get a new task (it's battery level is %s)", LocalDateTime.now(), this.getID(), this.batteryLevel);
//                logManager.writeLog(fileName, msg);
//            }
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

        case MOVING_TO_CHARGE:
            workOnTask();
            if (fileName != null) {
                String msg = String.format("[%s] Robot %s is charging (it's battery level is %s)", LocalDateTime.now(), this.getID(), this.batteryLevel);
                logManager.writeLog(fileName, msg);
            }
            break;


        case MOVING_TO_IDLE_POINT:
            workOnTask();
            if (fileName != null) {
                String msg = String.format("[%s] Robot %s is moving to idle point (it's battery level is %s)", LocalDateTime.now(), this.getID(), this.batteryLevel);
                logManager.writeLog(fileName, msg);
            }
            break;
        }
    }
    
    private void workOnTask() {

        String fileName = null;
        if (logManager != null) {
            String date = df.format(LocalDate.now());
            fileName = String.format("%s-%s.log", this.getID(), date);
        }

        if (currentPath != null && !currentPath.isEmpty()) {
            this.currentPosition = currentPath.poll();
            this.batteryLevel -= BATTERY_COST_PER_MOVE;

            if (fileName != null) {
                String msg = String.format("[%s] Robot %s is at position %s", LocalDateTime.now(), this.getID(), this.currentPosition.toString());
                logManager.writeLog(fileName, msg);
            }
        }

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
            this.workingState = WorkingState.GOING_TO_PICKUP;

            this.currentPath = pathFinder.findPath(currentPosition, newTask.getDestination());

            if (this.currentPath == null) {

                this.state = RobotState.IDLE;
                this.currentTask = null;
                System.out.println("PATH NOT FOUND");
            }
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
	

	
	
	
	
	
	
	
}