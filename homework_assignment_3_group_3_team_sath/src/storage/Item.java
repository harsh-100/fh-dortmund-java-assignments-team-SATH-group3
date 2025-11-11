package storage;

import java.awt.*;

public class Item {

    private String id;
    private String name;
    private static int num = 0;
    private Point position; // (5; 10)
    private int num_of_items;
    private String unit_id;
    // {"unit 1" : (5;10)}


    public Item (String name){
        this.name = name;
        this.id = name + "_" + num++;
    } 

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Point getPosition(){
        return position;
    }
    public int getNumOfItems(){
        return num_of_items;
    }

    @Override
    public String toString(){
        return id+ ":" +name+ "("+position+")";
    }


}