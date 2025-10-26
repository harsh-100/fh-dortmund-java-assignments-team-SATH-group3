package industrialProcess;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public abstract class IOperation {
    protected String id;
    protected String description;
    protected LocalTime nominalTime;
    protected List<AGV> resources;

    public IOperation(String id, String description, LocalTime nominalTime) {
        this.id = id;
        this.description = description;
        this.nominalTime = nominalTime != null ? nominalTime : LocalTime.of(0, 0);
        this.resources = new ArrayList<>();
    }


    public String getId() {

        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalTime getNominalTime() {
        return nominalTime;
    }

    public List<AGV> getResources() {
        return new ArrayList<>(resources);
    }


    public void addResource(AGV agv) {
        if (!resources.contains(agv)) {
            resources.add(agv);
        }
    }

    public void removeResource(AGV agv) {

        resources.remove(agv);

    }


    public abstract void setData(Object data);

    public abstract Object getData(String dataType);

    public abstract LocalTime getDuration();
}