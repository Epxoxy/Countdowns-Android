package com.example.xiaox.countdowns.basic.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xiaox.countdowns.activities.DetailActivity;
import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;

import java.util.List;

/**
 * Created by xiaox on 2/14/2017.
 */
public class EventViewRecyclerAdapter extends RecyclerView.Adapter<EventViewRecyclerHolder> {
    public List<EventItem> items = EventItemManager.ITEMS;
    private int layoutID;
    private Context context;
    public EventViewRecyclerAdapter(Context context, int layoutID) {
        this.context = context;
        this.layoutID = layoutID;
        this.setHasStableIds(true);
    }

    @Override
    public EventViewRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);
        EventViewRecyclerHolder holder = new EventViewRecyclerHolder(view);
        //XLog.logLine("Create " + holder.getItemId());
        return holder;
    }

    @Override
    public void onBindViewHolder(EventViewRecyclerHolder holder, int position) {
    }

    @Override
    public void onViewAttachedToWindow(EventViewRecyclerHolder holder){
        super.onViewAttachedToWindow(holder);
        //XLog.logLine("Attached " + holder.getItemId());
    }

    @Override
    public void onViewRecycled(EventViewRecyclerHolder holder){
        super.onViewRecycled(holder);
        //XLog.logLine("Recycled " + holder.getItemId());
    }

    @Override
    public void onViewDetachedFromWindow(EventViewRecyclerHolder holder){
        super.onViewDetachedFromWindow(holder);
        //XLog.logLine("Detached " + holder.getItemId());
    }



    @Override
    public boolean onFailedToRecycleView(EventViewRecyclerHolder holder){
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onBindViewHolder(EventViewRecyclerHolder holder, int position, List payloads){
        EventItem item = items.get(position);
        if(payloads.isEmpty()){
            holder.updateHoleIfNew(item);
        }else{
            holder.getViewHolder().updateByType((int)payloads.get(0), item);
        }
        final int index = position;
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(DetailActivity.ITEM_KEY, index);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }
}
