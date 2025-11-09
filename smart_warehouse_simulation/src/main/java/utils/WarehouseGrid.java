package utils;
import java.awt.Point;

public class WarehouseGrid {
    
    private final int rows;
    private final int columns;
    private Object[][] gridLayout;
    
    public WarehouseGrid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.gridLayout = new Object[rows][columns];
    }
    
    private boolean isWithinBounds(int row, int column) {
        return row >= 0 && row < this.rows && column >= 0 && column < this.columns;
    }
    
    //-------- place object methods --------------
    
    public void placeObject(Object obj, int row, int column) {
        if (isWithinBounds(row, column))
            this.gridLayout[row][column] = obj;
    }
    
    public void placeObject(Object obj, Point p) {
        if (p == null) 
            return; // exception later
        placeObject(obj, p.x, p.y);
    }
    

    //-------- get object methods ----------------
    
    public Object getObjectAt(int row, int column) {
        if (!isWithinBounds(row, column))
            return null;
        return this.gridLayout[row][column];
    }
    
    public Object getObjectAt(Point p) {
        if (p == null)
            return null; // exception later
        return getObjectAt(p.x, p.y);
    }
    
    //-------- get bounds of the grid ------------
    
    public int getMaxRow() {
        return this.rows;
    }
    
    public int getMaxColumn() {
        return this.columns;
    }

}
