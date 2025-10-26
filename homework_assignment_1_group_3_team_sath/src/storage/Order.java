

package storage;
import java.util.List;
import java.util.ArrayList;

//This class will represents the Order by the customer
public class Order{
    private String id;
    public enum Status {
        PENDING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
    private Status status;
    private List<Item> items;
    private long timestamp;

    public Order(String id) {
        this.id = id;
        this.status = Status.PENDING;
        this.items = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public List<Item> getItems() {
        return items;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "Order ID: " + id + ", Status: " + status + ", Items: " + items + ", Timestamp: " + timestamp;
    }
}