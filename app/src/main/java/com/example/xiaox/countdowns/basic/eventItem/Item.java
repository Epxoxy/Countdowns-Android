package com.example.xiaox.countdowns.basic.eventItem;

import java.io.Serializable;

/**
 * Created by xiaox on 2/11/2017.
 */
public class Item implements Serializable {
    public static final long serialVersionUID = 3182299136236747120L;

    private int id = 0;
    public String celebration;
    public String name;

    public int getID(){
        return this.id;
    }

    public void setID(int id){
        this.id = id;
    }

}
