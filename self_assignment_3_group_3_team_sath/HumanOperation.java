package selfAssignment3;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class HumanOperation extends IOperation {

    private HumanResources humanResource;
    private int requiredHours;

    public HumanOperation(String id, String description, LocalTime nominalTime,
                          HumanResources humanResource, int requiredHours) {
        super(id, description, nominalTime);
        this.humanResource = humanResource;
        this.requiredHours = requiredHours;
    }

    public HumanResources getHumanResource() {
        return humanResource;
    }

    public void setHumanResource(HumanResources humanResource) {
        this.humanResource = humanResource;
    }

    public int getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(int requiredHours) {
        this.requiredHours = requiredHours;
    }

    @Override
    public void setData(Object data) {
        if (data instanceof HumanResources) {
            setHumanResource((HumanResources) data);
        } else if (data instanceof Integer) {
            setRequiredHours((Integer) data);
        }
        
    }

    @Override
    public Object getData(String dataType) {
        switch (dataType.toLowerCase()) {
            case "humanresource":
                return getHumanResource();
            case "requiredhours":
                return getRequiredHours();
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
        return String.format("HumanOperation[ID=%s, Description=%s, Human=%s, RequiredHours=%d, Duration=%s, AGVs=%d]",
                id, description, humanResource != null ? humanResource.getName() : "None", requiredHours, nominalTime, resources.size());
    }
}