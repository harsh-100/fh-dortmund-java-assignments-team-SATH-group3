package warehouse;

import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;


import charging.ChargingStation;
import robots.Robot;
import tasks.TaskManager;
import tasks.Tasks;
import utils.WarehouseGrid;

public class Warehouse {
	
	private WarehouseGrid grid;
    private TaskManager taskManager;
    private List<Robot> robots;
    private List<ChargingStation> stations;
    private Random random = new Random();
    private Queue<Robot> chargingQueue = new LinkedList<>();
    
    public Warehouse() {

        grid = new WarehouseGrid(10, 10); 
        try {
        	taskManager = new TaskManager("TM1"); 
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


    
//    public synchronized ChargingStation getRandomAvailableStation() {
//
//    	List<ChargingStation> available = stations.stream()
//    			.filter(station -> station.isAvailable()).collect(Collectors.toList());
//
//    	if (!available.isEmpty()) {
//            int randomIndex = random.nextInt(available.size());
//            return available.get(randomIndex);
//        }
//
//    	return null;
//    }

    public synchronized ChargingStation requestCharging(Robot robot) {
        for (ChargingStation station : stations) {
            if (station.isAvailable()) {
                station.occupy();
                return station;
            }
        }

        if (!chargingQueue.contains(robot))
            chargingQueue.add(robot);

        return null;
    }

    public synchronized void leaveQueue(Robot robot) {
        chargingQueue.remove(robot);
        // LOG IN THE FUTURE
    }

    public synchronized void releaseStation(ChargingStation station) {
        if (!chargingQueue.isEmpty()) {
            Robot waitingRobot = chargingQueue.poll();
            waitingRobot.assignStation(station);
        } else {
            station.release();
        }
    }
    
    public void startSimulation() {
        System.out.println("Sumulation is running");
        System.out.println(robots.size() + " robots were created");
        System.out.println(stations.size() + " stations were created");
        
        for (Robot robot : robots) {
            robot.setBatteryForTest(19);
            Thread robotThread = new Thread(robot);
            robotThread.start(); 
        }
    }

    public void task1_simulation(){

        ChargingStation station3 = new ChargingStation(new Point(9, 0));
        ChargingStation station4 = new ChargingStation(new Point(0, 0));

        stations.add(station3);
        stations.add(station4);

        grid.placeObject(station3, station3.getLocation());
        grid.placeObject(station4, station3.getLocation());

        Robot robot3 = new Robot(this, new Point(5, 5), taskManager);
        Robot robot4 = new Robot(this, new Point(0, 5), taskManager);
        Robot robot5 = new Robot(this, new Point(1, 5), taskManager);

        robots.add(robot3);
        robots.add(robot4);
        robots.add(robot5);

        System.out.println("Sumulation of the first subtask is running");
        System.out.println(robots.size() + " robots were created");
        System.out.println(stations.size() + " stations were created");

        for (Robot robot : robots) {
            robot.setBatteryForTest(19.0);
            Thread robotThread = new Thread(robot);
            robotThread.start();
        }

    }

    public void task2_simulation(){

        Robot robot3 = new Robot(this, new Point(5, 5), taskManager);
        Robot robot4 = new Robot(this, new Point(0, 5), taskManager);
        Robot robot5 = new Robot(this, new Point(1, 5), taskManager);

        robots.add(robot3);
        robots.add(robot4);
        robots.add(robot5);

        System.out.println("Sumulation of the first subtask is running");
        System.out.println(robots.size() + " robots were created");
        System.out.println(stations.size() + " stations were created");

        for (Robot robot : robots) {
            robot.setBatteryForTest(19.0);
            Thread robotThread = new Thread(robot);

            try {
                Thread.sleep(random.nextInt(2000));
            } catch (InterruptedException e) {}

            robotThread.start();
        }

    }

    public void task3_simulation(){

        System.out.println("Sumulation with tasks is running");
        System.out.println(robots.size() + " robots were created");
        System.out.println(stations.size() + " stations were created");

        for (int i = 0; i < 10; i++) {
            taskManager.addTask(new Tasks());
        }

        for (Robot robot : robots) {
            Thread robotThread = new Thread(robot);
            robotThread.start();
        }

        System.out.println("10 tasks have been added");
    }
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();
        warehouse.task3_simulation();
    }

}