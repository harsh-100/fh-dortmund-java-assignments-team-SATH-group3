import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class Operation implements IOperation {
    private String id;
    private String description;
    private LocalTime nominalTime;
    private List<AGV> resources;

    public Operation(String id, String description) {
        this.id = id;
        this.description = description;
        this.nominalTime = LocalTime.of(0, 0);
        this.resources = new ArrayList<>();
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getDescription() { return description; }

    @Override
    public LocalTime getNominalTime() { return nominalTime; }

    @Override
    public List<AGV> getResources() { return new ArrayList<>(resources); }

    public void setId(String id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setNominalTime(LocalTime nominalTime) { this.nominalTime = nominalTime; }


    public void addResource(AGV agv) {
        if (!resources.contains(agv)) {
            resources.add(agv);
        }
    }


    public void removeResource(AGV agv) {
        resources.remove(agv);
    }

    @Override
    public void setData(Object data) {
        if (data instanceof LocalTime) {
            setNominalTime((LocalTime) data);
        } else if (data instanceof String) {
            setDescription((String) data);
        } else if (data instanceof AGV) {
            addResource((AGV) data);
        }
    }

    @Override
    public Object getData(String dataType) {
        switch (dataType.toLowerCase()) {
            case "id":
                return id;
            case "description":
                return description;
            case "time":
                return nominalTime;
            case "resources":
                return getResources();
            case "resourcecount":
                return resources.size();
            default:
                return null;
        }
    }

    @Override
    public LocalTime getDuration() {
        return nominalTime;
    }


    public double calculateEnergyConsumption() {
        double totalConsumption = 0.0;
        double operationHours = nominalTime.getHour() + nominalTime.getMinute() / 60.0;

        for (AGV agv : resources) {
            totalConsumption += agv.calculateEnergyConsumption(operationHours);
        }

        return totalConsumption;
    }

    @Override
    public String toString() {
        return String.format("Operation[ID=%s, Description=%s, Duration=%s, AGVs=%d]",
                id, description, nominalTime, resources.size());
    }
}