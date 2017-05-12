package com.example.xiaox.countdowns.basic.eventItem;

import java.util.List;

import per.epxoxy.event.EventArgs;

/**
 * Created by xiaox on 2/15/2017.
 */
public class ItemPropertyChangedArgs  extends EventArgs {
    public final List<Integer> properties;
    public final int index;
    public ItemPropertyChangedArgs(int index, List<Integer> properties){
        this.index = index;
        this.properties = properties;
    }
}