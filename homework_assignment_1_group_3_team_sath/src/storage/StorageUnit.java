package storage;

import java.util.List;
import java.util.ArrayList;
import utils.Position;

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
        if( items.size() < capacity){
            items.add(item);
            return true;
        }
        return false;
    }

    public boolean removeItems(String itemid){
        return items.removeIf(i -> i.getId().equals(itemid));
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