package warehouse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.io.IOException;


import charging.ChargingStation;
import robots.Robot;
import tasks.TaskManager;
import utils.WarehouseGrid;

public class Warehouse {
	
	private WarehouseGrid grid;
    private TaskManager taskManager;
    private List<Robot> robots;
    private List<ChargingStation> stations;
    private Random random = new Random();
    
    public Warehouse() {

        grid = new WarehouseGrid(10, 10); 
        try {
        	taskManager = new TaskManager(); 
        } catch (IOException e) {
        	System.err.println("TaskManager wasn't loaded");
            e.printStackTrace();
        }
        robots = new ArrayList<>();
        stations = new ArrayList<>();


        createStations();
        createRobots();
        
    }
    
    private void createStations() {
    	
    	ChargingStation station1 = new ChargingStation(new Point(0, 9)); 
        ChargingStation station2 = new ChargingStation(new Point(9, 9)); 
        
        stations.add(station1);
        stations.add(station2);
        
        grid.placeObject(station1, station1.getLocation());
        grid.placeObject(station2, station2.getLocation());
        
    }
    
    private void createRobots() {
    	
    	Robot robot1 = new Robot(this, new Point(0, 0), taskManager);
        Robot robot2 = new Robot(this, new Point(5, 0), taskManager);
        
        robots.add(robot1);
        robots.add(robot2);
        
    }
    
    public synchronized ChargingStation getRandomAvailableStation() {
    	
    	List<ChargingStation> available = stations.stream()
    			.filter(station -> station.isAvailable()).collect(Collectors.toList());
    	
    	if (!available.isEmpty()) {
            int randomIndex = random.nextInt(available.size());
            return available.get(randomIndex);
        }
    	
    	return null;
    	
    }
    
    public void startSimulation() {
        System.out.println("Sumulation is running");
        System.out.println(robots.size() + " robots were created");
        System.out.println(stations.size() + " stations were created");
        
        for (Robot robot : robots) {
            Thread robotThread = new Thread(robot);
            robotThread.start(); 
        }
    }
    
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();
        warehouse.startSimulation();
    }

}
