package selfAssignment3;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class TransportOperation extends IOperation {


    private String startLocation;
    private String endLocation;
    private double distance; 

    public TransportOperation(String id, String description, LocalTime nominalTime,
                             String startLocation, String endLocation, double distance) {
        super(id, description, nominalTime);
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.distance = distance;
    }


    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public void setData(Object data) {
        
        if (data instanceof Double) {
            setDistance((Double) data);
        } else if (data instanceof String[]) {
            String[] locations = (String[]) data;
            if (locations.length == 2) {
                setStartLocation(locations[0]);
                setEndLocation(locations[1]);
            }
        }
    }

    @Override
    public Object getData(String dataType) {
        switch (dataType.toLowerCase()) {
            case "startlocation":
                return getStartLocation();
            case "endlocation":
                return getEndLocation();
            case "distance":
                return getDistance();
            case "id":
                return getId();
            case "description":
                return getDescription();
            case "time":
                return getNominalTime();
            case "resources":
                return getResources();
            default:
                return null;
        }
    }

    @Override
    public LocalTime getDuration() {
        
        return nominalTime;
    }

    @Override
    public String toString() {
        return String.format("TransportOperation[ID=%s, Description=%s, From=%s, To=%s, Distance=%.2f, Duration=%s, AGVs=%d]",
                id, description, startLocation, endLocation, distance, nominalTime, resources.size());
    }
}