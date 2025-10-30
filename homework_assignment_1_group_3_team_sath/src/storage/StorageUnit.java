package storage;

import java.util.List;
import java.util.ArrayList;
import utils.Position;
import exceptions.ExceptionHandler;

public class StorageUnit{

    private final String id;
    private double capacity;
    private Position position;
    private List<Item> items;

    public StorageUnit(String id, double capacity , Position position){
        this.id = id;
        this.capacity = capacity;
        this.position = position;
        this.items = new ArrayList<>();
    }
    public String getId(){
        return id;
    }
    public double getCapacity(){
        return capacity;
    }
    public Position getPosition(){
        return position;
    }
    public List<Item> getItems(){
        return items;
    }

    public boolean addItems(Item item){
        try {
            if( items.size() < capacity){
                items.add(item);
                return true;
            }
        } catch (Throwable t) {
            ExceptionHandler.handle(t, "storage.StorageUnit.addItems");
        }
        return false;
    }

    public boolean removeItems(String itemid){
        try {
            return items.removeIf(i -> i.getId().equals(itemid));
        } catch (Throwable t) {
            ExceptionHandler.handle(t, "storage.StorageUnit.removeItems");
            return false;
        }
    }

    public double getRemainingCapacity(){
        return capacity - items.size();
    }


    public int getCurrentItemCount(){
        return items.size();
    }


    @Override
    public  String toString(){
        return "StorageUnit ID: " + id + ", Capacity: " + capacity + ", Position: " + position + ", Items: " + items;
    }


}