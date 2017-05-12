package com.example.xiaox.countdowns.basic.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.util.List;

/**
 * Created by xiaox on 2/13/2017.
 */
public class EventViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private int layoutID = -1;
    private SparseIntArray updateParams;

    public EventViewAdapter(Context context, int layoutID){
        this.mInflater = LayoutInflater.from(context);
        this.layoutID = layoutID;
        updateParams = new SparseIntArray();
        for(int i = 0; i < EventItemManager.ITEMS.size(); i++){
            updateParams.append(EventItemManager.ITEMS.get(i).getID(), EventItem.PARAM_ALL);
        }
    }

    @Override
    public int getCount() {
        return EventItemManager.ITEMS.size();
    }

    @Override
    public Object getItem(int position) {
        return EventItemManager.ITEMS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventItem eventItem = EventItemManager.ITEMS.get(position);
        MyHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(layoutID, null);
            holder = new MyHolder(convertView);
            convertView.setTag(holder);
            System.out.println("Create view for " + eventItem.getID());
        }else{
            holder = (MyHolder)convertView.getTag();
        }
        int type = updateParams.get(eventItem.getID());
        holder.updateByType(type, eventItem);
        return convertView;
    }

    public void notifyItemChanged(int position){
        updateParams.put(position, EventItem.PARAM_ALL);
        this.notifyDataSetChanged();
    }

    public void notifyItemChanged(int position, int type){
        updateParams.put(position, type);
        this.notifyDataSetChanged();
    }

    public void notifyItemsChanged(){
        if(updateParams == null) updateParams = new SparseIntArray();
        else updateParams.clear();
        for(int i = 0; i < EventItemManager.ITEMS.size(); i++){
            updateParams.append(EventItemManager.ITEMS.get(i).getID(), EventItem.PARAM_ALL);
        }
        this.notifyDataSetChanged();
    }

    public void notifyItemsChanged(int[] indexes, int type){
        int size = Math.min(indexes.length, updateParams.size());
        for(int i = 0; i < size; i++){
            updateParams.put(indexes[i], type);
        }
        this.notifyDataSetChanged();
    }

    public void notifyItemsChanged(List<Integer> indexes, int type){
        int size = Math.min(indexes.size(), updateParams.size());
        for(int i = 0; i < size; i++){
            updateParams.put(indexes.get(i), type);
        }
        this.notifyDataSetChanged();
    }

    static class MyHolder extends EventViewUpdater{
        private int log = -1;
        public MyHolder(View view){
            super(view);
        }

        public MyHolder(Activity activity){
            super(activity);
        }

        @Override
        public EventViewUpdater updateByType(int type, EventItem item){
            if(this.log == item.getID()) {
                XLog.logLine("Update exist(" + this.log + ") by type(" + type+")");
                return super.updateByType(type, item);
            }
            XLog.logLine("Replace " + this.log + " to " + item.getID());
            this.log = item.getID();
            return super.refreshHoleViewBy(item);
        }

    }
}
