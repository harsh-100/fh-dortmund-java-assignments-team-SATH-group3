
package tasks;
import java.util.List;
import utils.Position;
import storage.Item;

public class Tasks{

    private String id;
    private Position destination;
    private String robotId;
    private List<Item> items;
    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
    private TaskStatus status;

    public Tasks(String id, Position destination,List<Item>itemsToFetch){
        this.id = id;
        this.destination = destination;
        this.robotId = null;
        this.items = itemsToFetch;
        this.status = TaskStatus.PENDING;
    }

    public String getId(){
        return id;
    }

    public Position getDestination(){
        return destination;
    }

    public String getRobotId(){
        return robotId;
    }

    public List<Item> getItems(){
        return items;
    }

    public TaskStatus getStatus(){
        return status;
    }

    public void setStatus(TaskStatus status){
        this.status = status;
    }
    public void setRobotId(String robotId){
        this.robotId = robotId;
    }

    public boolean isComplete(){
        return status == TaskStatus.COMPLETED;
    }

    @Override
    public String toString(){
        return "Task ID: " + id + ", Destination: " + destination + ", Robot ID: " + robotId + ", Items: " + items + ", Status: " + status;
    }
}