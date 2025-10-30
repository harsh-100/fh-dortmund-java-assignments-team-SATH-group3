package charging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;

import charging.ChargingStation;

public class ChargingStationTest {
	
	private ChargingStation station;
	
	@BeforeEach
    void setUp() {
		
        station = new ChargingStation(new Point(1, 1));
        
    }
	
	@Test
    void testStationCreation() {
		
        assertNotNull(station.getID());
        assertEquals(new Point(1, 1), station.getLocation());
        assertTrue(station.isAvailable());
    }
	
	@Test
    void testOccupyWhenAvailable() {
		
        boolean success = station.occupy();
        
        assertTrue(success);
        assertFalse(station.isAvailable());
    }
	
	@Test
    void testOccupyWhenOccupied() {
		
        station.occupy(); 
        boolean secondTry = station.occupy(); 
        
        assertFalse(secondTry); 
        assertFalse(station.isAvailable());
    }
	
	@Test
    void testReleaseStation() {
		
        station.occupy();
        assertFalse(station.isAvailable());
        
        station.release(); 
        assertTrue(station.isAvailable()); 
    }
	
	@Test
    void testUniqueIDs() {
		
        ChargingStation station2 = new ChargingStation(new Point(2, 2));
        assertNotEquals(station.getID(), station2.getID());
        
    }
	
}