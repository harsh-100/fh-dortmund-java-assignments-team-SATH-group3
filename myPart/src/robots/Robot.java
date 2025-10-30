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
		CHARGING
	}
	
	private RobotState state;
	private ChargingStation currentStation;
	public Warehouse warehouse;
	private final DateTimeFormatter df = DateTimeFormatter.ISO_DATE;
	
	// ----------------------------------
	private int taskTimer = 0; // just a temporary solution
	private int chargeTimer = 0; // temporary solution
	// ----------------------------------
	
	private static final double MAX_BATTERY = 100.0;
    private static final double LOW_BATTERY_THRESHOLD = 20.0;
    private static final double BATTERY_COST_PER_MOVE = 0.5;
    private static final double CHARGE_RATE_PER_TICK = 2.0;
    private static final int TASK_DURATION_IN_TICKS = 100; // just a temporary solution as well
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
            this.logManager = new LogManager("/Users/artemrymar/Desktop/MDT/Java compact course /PreparatoryJavaCourse/fh-dortmund-java-assignments-team-SATH-group3/myPart/RobotsLog");
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
    	
    	
    	if (logManager != null) {
            String date = df.format(LocalDate.now());
            String fileName = String.format("%s-%s.log", this.getID(), date);
            String msg = String.format("[%s] State of robot %s is updated", LocalDateTime.now(), this.getID());
            logManager.writeLog(fileName, msg);
        }
    	
    	
    	if (batteryLevel < LOW_BATTERY_THRESHOLD 
    			&& state != RobotState.CHARGING) {
    		
    		if (this.currentTask != null) {
                taskManager.requeueTask(this.currentTask);
                this.currentTask = null;
                this.taskTimer = 0; 
            }
    		
    		
    		
    		
    		ChargingStation station = warehouse.getRandomAvailableStation(); // just a plug
    		
    		if (station != null) {
    			this.state = RobotState.CHARGING;
    			this.chargeTimer = 0;
    			this.currentStation = station;
    			this.currentStation.occupy();
    			
    			this.currentPosition = station.getLocation();
    		} else {
    			this.state = RobotState.IDLE;
    		}
    	}
    	
   
    	
    	else if (state == RobotState.CHARGING && chargeTimer >= CHARGING_DURATION_IN_TICKS) {
    		
            this.batteryLevel = MAX_BATTERY;
            this.state = RobotState.IDLE;
            this.chargeTimer = 0;
            
            if (this.currentStation != null) {
            	this.currentStation.release();
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
    	
    	if (logManager != null) {
            String date = df.format(LocalDate.now());
            String fileName = String.format("%s-%s.log", this.getID(), date);
            String msg = String.format("[%s] Robot %s is maybe performing action (it's battery level is %s)", LocalDateTime.now(), this.getID(), this.batteryLevel);
            logManager.writeLog(fileName, msg);
        }
    	
    	switch (this.state) {
    	case IDLE:
    		tryToGetNewTask();
    		break;
    		
    		
    	case WORKING:
    		workOnTask();
    		break;
    		
    	case CHARGING:
    		chargeBattery();
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
