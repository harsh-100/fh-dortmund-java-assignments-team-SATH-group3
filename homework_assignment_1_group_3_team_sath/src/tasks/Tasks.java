
package tasks;
import java.util.List;
// import utils.Position;
import storage.Item;
import java.awt.Point;

public class Tasks{

    private String id;
    private Point destination;
    private String robotId;
    private Item item;
    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
    private TaskStatus status;

    public Tasks(String id, Point destination, Item item){
        this.id = id;
        this.destination = destination;
        this.robotId = null;
        this.item = item;
        this.status = TaskStatus.PENDING;
    }

    public Tasks(String id, Item item){
        this.id = id;
        this.destination = null;
        this.robotId = null;
        this.item = item;
        this.status = TaskStatus.PENDING;
    }

    public String getId(){
        return id;
    }

    public Point getDestination(){
        return destination;
    }

    public String getRobotId(){
        return robotId;
    }

    public Item getItems(){
        return item;
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
        return "Task ID: " + id + ", Destination: " + destination + ", Robot ID: " + robotId + ", Item: " + item + ", Status: " + status;
    }
}