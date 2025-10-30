package charging;

import logging.LogManager;
import java.io.IOException;

import utils.IGridEntity;
import java.awt.Point;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class ChargingStation implements IGridEntity {
	
	private static int num = 0;
	private String id;
	private final Point location;
	private boolean isAvailable = true;
	private LogManager logManager;
	private final DateTimeFormatter df = DateTimeFormatter.ISO_DATE;

	
	public ChargingStation(Point location) {
		
		this.location = location;
		this.id = "ch_st_" + num;
		num++;
		
		try {
            this.logManager = new LogManager("./logs/chargingStationLogs");
        } catch (Exception e) {
            
            this.logManager = null;
            System.err.println("Warning: could not initialize LogManager: " + e.getMessage());
        }
		
		if (logManager != null) {
            String date = df.format(LocalDate.now());
            String fileName = String.format("%s-%s.log", this.getID(), date);
            String msg = String.format("[%s] Charging station %s is set up in the next coordinates (%d, %d)", LocalDateTime.now(), this.getID(), this.location.x, this.location.y);
            logManager.writeLog(fileName, msg);
        }
		

	}
	
	
	public synchronized boolean occupy() {
		if (this.isAvailable) {
			this.isAvailable = false;
			
			
			 if (logManager != null) {
		            String date = df.format(LocalDate.now());
		            String fileName = String.format("%s-%s.log", this.getID(), date);
//		            String msg = String.format("[%s] Assigned task %s to AGV %s — status=%s", LocalDateTime.now(), task.getId(), robot.getId(), task.getStatus());
		            String msg = String.format("[%s] Charging station %s is occupied", LocalDateTime.now(), this.getID());
		            logManager.writeLog(fileName, msg);
		        }
			 
			 return true;

		}
		return false;
	}
	
	public synchronized void release() {
		this.isAvailable = true;
		
		if (logManager != null) {
            String date = df.format(LocalDate.now());
            String fileName = String.format("%s-%s.log", this.getID(), date);
            String msg = String.format("[%s] Charging station %s is available again", LocalDateTime.now(), this.getID());
            logManager.writeLog(fileName, msg);
        }
		
		
	}
	
	
	public boolean isAvailable() {
		return this.isAvailable;
	}
	
	
	
    // -------- IGridEntity methods -------- 
	@Override
	public Point getLocation() {
		return this.location; 
	}

	@Override
	public String getID() {
		return this.id;
	}
	
	
	
	
}