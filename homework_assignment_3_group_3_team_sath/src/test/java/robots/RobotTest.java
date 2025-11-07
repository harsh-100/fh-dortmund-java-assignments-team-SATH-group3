package test.java.robots;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;
import java.io.IOException;

import robots.Robot;
import robots.Robot.RobotState;
import tasks.TaskManager;
import tasks.Tasks;
import warehouse.Warehouse;
import charging.ChargingStation;

class MockTaskManager extends TaskManager {
    public boolean taskAvailable = false;
    public boolean taskCompleted = false;
    
    public MockTaskManager() throws IOException {
        super("taskManager1");
    }
    
    @Override
    public synchronized Tasks robotGetTask() {
        if (!taskAvailable) return null;
        return new Tasks(); 
    }
    
    @Override
    public void completeTask(Tasks task) {
        taskCompleted = true; 
    }
    
    @Override
    public synchronized void requeueTask(Tasks task) {
 
    }
}
    
    
class MockWarehouse extends Warehouse {
	
	public boolean stationAvailable = false;
	
	public MockWarehouse() throws IOException {
        super();
    }
	
//	@Override
//    public synchronized ChargingStation getRandomAvailableStation() {
//        if (!stationAvailable) return null;
//
//        return new ChargingStation(new Point(9,9));
//    }
}
    

public class RobotTest {
	
	private Robot robot;
    private MockTaskManager mockTM;
    private MockWarehouse mockWH;
    
    @BeforeEach
    void setUp() throws IOException { 
    	
        mockTM = new MockTaskManager();
        mockWH = new MockWarehouse();
        robot = new Robot(mockWH, new Point(0, 0), mockTM);
    }
    
    @Test
    void testRobotCreation() {
    	
        assertNotNull(robot.getID());
        assertEquals(100.0, robot.getBattery());
        assertEquals(RobotState.IDLE, robot.getState());
    }
    
    @Test
    void testPerformAction_whenTaskIsAvailable_robotShouldStartWorking() {
    	
        mockTM.taskAvailable = true; 
        robot.performAction(); 
       
        assertEquals(RobotState.WORKING, robot.getState());
        assertNotNull(robot.getCurrentTask());
    }
    
    @Test
    void testPerformAction_whenNoTask_ShouldStayIdle() {
    	
        mockTM.taskAvailable = false;
        robot.performAction();
        
        assertEquals(RobotState.IDLE, robot.getState());
        assertNull(robot.getCurrentTask());
    }
    
    @Test
    void testUpdateState_LowBattery_ShouldStartCharging() {
    	
        mockWH.stationAvailable = true; 
        robot.setBatteryForTest(10.0); 
        
        robot.updateState();
        
        assertEquals(RobotState.CHARGING, robot.getState());
    }
    
    
    @Test
    void testUpdateState_whenTaskIsDone_ShouldCompleteTask() {
        
        mockTM.taskAvailable = true;
        robot.performAction(); 
        assertEquals(RobotState.WORKING, robot.getState());
        
        robot.setTaskTimerForTest(100);
        robot.updateState();
        
        assertEquals(RobotState.IDLE, robot.getState());
        assertNull(robot.getCurrentTask());
 
    }

}