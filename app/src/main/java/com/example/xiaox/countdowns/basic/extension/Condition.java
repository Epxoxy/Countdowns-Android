package com.example.xiaox.countdowns.basic.extension;

import java.io.Serializable;

/**
 * Created by xiaox on 2/13/2017.
 */
public class Condition<T1, T2> implements Serializable{
    private Object newestObj;
    private boolean hasSet;
    private boolean isFirstNewest;

    public boolean isFirstNewest(){
        return this.isFirstNewest;
    }

    public boolean hasSet(){
        return this.hasSet;
    }

    public Object newest(){
        return this.newestObj;
    }

    public void clear(){
        this.isFirstNewest = false;
        this.hasSet = false;
        this.newestObj = null;
    }

    public void updateFirst(T1 value){
        this.newestObj = value;
        this.hasSet = true;
        this.isFirstNewest = true;
    }

    public void updateSecond(T2 value){
        this.newestObj = value;
        this.hasSet = true;
        this.isFirstNewest = false;
    }
}
