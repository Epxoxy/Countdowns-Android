package com.example.xiaox.countdowns.basic.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.xiaox.countdowns.basic.eventItem.EventItem;

/**
 * Created by xiaox on 2/14/2017.
 */
public class EventViewRecyclerHolder extends RecyclerView.ViewHolder{
    private EventViewUpdater viewHolder;
    public final View mView;
    //private int id = -1;

    public EventViewRecyclerHolder(View view){
        super(view);
        System.out.println( " I'm created");
        this.mView = view;
        this.viewHolder = new EventViewUpdater(view);
    }
    public void updateHoleIfNew(EventItem eventItem){
        this.viewHolder.refreshHoleViewBy(eventItem);
        System.out.println(this.getItemId() + " refreshViewBy " +eventItem.getID());
        /*System.out.println(this.id + " refreshViewBy " +eventItem.getID());
        this.id = eventItem.getID();
        System.out.println(" to " + this.id);
        if(id != eventItem.getID()){
        }*/
    }

    public void updateHole(EventItem eventItem){
        this.viewHolder.updateViewBy(eventItem);
    }

    public EventViewUpdater getViewHolder(){
        return this.viewHolder;
    }
}
