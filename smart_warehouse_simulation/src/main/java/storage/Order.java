package storage;
import exceptions.ExceptionHandler;
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
        try {
            items.add(item);
        } catch (Throwable t) {
            ExceptionHandler.handle(t, "storage.Order.addItem");
        }
    }

    public void removeItem(Item item) {
        try {
            items.remove(item);
        } catch (Throwable t) {
            ExceptionHandler.handle(t, "storage.Order.removeItem");
        }
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "Order ID: " + id + ", Status: " + status + ", Items: " + items + ", Timestamp: " + timestamp;
    }
}
