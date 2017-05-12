package com.example.xiaox.countdowns.activities.debug;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xiaox.countdowns.activities.EventManageActivity;
import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.activities.SettingsActivity;
import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;
import com.example.xiaox.countdowns.basic.adapter.EventViewRecyclerAdapter;
import com.example.xiaox.countdowns.basic.eventItem.ItemPropertyChangedArgs;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.util.List;

import per.epxoxy.event.EventArgs;
import per.epxoxy.event.IEventHandler;

public class RecyclerMasterActivity extends AppCompatActivity {
    private RecyclerView masterRecyclerView;
    private EventViewRecyclerAdapter adapter;
    //private EventViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        masterRecyclerView = (RecyclerView) findViewById(R.id.masterRecyclerView);
        if(masterRecyclerView != null){
            adapter = new EventViewRecyclerAdapter(this, R.layout.eventitem_list_thumb);
            masterRecyclerView.setItemViewCacheSize(20);
            masterRecyclerView.setDrawingCacheEnabled(false);
            //masterRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            masterRecyclerView.setAdapter(adapter);
        }
        EventItemManager.with(this).onEventItemUpdated.addHandler(onEventItemUpdated);
        EventItemManager.with(this).onItemPropertyUpdated.addHandler(onItemPropertyUpdated);
        XLog.logLine("On master activity created");
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setDisplayShowTitleEnabled(false);
            actionbar.setDisplayShowCustomEnabled(true);
            actionbar.setCustomView(R.layout.actionbar_title);
        }
    }

    public View createListItemView(Context context){
        return LayoutInflater.from(context).inflate(R.layout.eventitem_list_thumb, null);
    }

    private IEventHandler<EventArgs> onEventItemUpdated = new IEventHandler<EventArgs>() {
        @Override
        public void onEvent(Object o, EventArgs eventArgs) {
            //Send message for checking if there is new UN_COMING
            if(allOutOf) updateTimeHandler.sendEmptyMessage(0);
        }
    };

    private IEventHandler<ItemPropertyChangedArgs> onItemPropertyUpdated = new IEventHandler<ItemPropertyChangedArgs>() {
        @Override
        public void onEvent(Object o, ItemPropertyChangedArgs eventArgs) {
            //Send message for checking if there is new UN_COMING
            if(RecyclerMasterActivity.this.isFinishing()){
            }
            if(EventItemManager.ITEMS.isEmpty()) adapter.notifyDataSetChanged();
            else adapter.notifyItemChanged(eventArgs.index, EventItem.PARAM_ALL);
            XLog.logLine("onItemPropertyUpdated --------------");
        }
    };

    private boolean allOutOf;
    private Handler updateTimeHandler = new Handler(){
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    this.removeMessages(0);
                    List<Integer> updatedIndexes = EventItemManager.updateTimeAll();
                    for(int i = 0; i < updatedIndexes.size(); i++){
                        adapter.notifyItemChanged(updatedIndexes.get(i),
                                EventItem.PARAM_REMAIN_MILLIS);
                    }
                    allOutOf = updatedIndexes.size() < 1;
                    if(!allOutOf){
                        this.sendEmptyMessageDelayed(0, 1000);
                    }else{
                        XLog.logLine("UnComing empty");
                    }
                    break;
                case 1:
                    this.removeMessages(0);
                default:break;
            }
        }
    };

    @Override
    protected void onPause(){
        super.onPause();
        this.updateTimeHandler.removeMessages(0);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //Force update all item
        if(EventItemManager.ITEMS.size() > 0){
            adapter.notifyItemRangeChanged(0, EventItemManager.ITEMS.size(),
                    EventItem.PARAM_ALL);
        }else{
            adapter.notifyDataSetChanged();
        }
        this.updateTimeHandler.sendEmptyMessage(0);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(this.isFinishing())
            this.updateTimeHandler.removeMessages(0);
        XLog.logLine("Master onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.master_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }else if(id == R.id.action_addItem){
            Intent intent = new Intent();
            intent.putExtra(EventManageActivity.TYPE_KEY, EventManageActivity.TYPE_ADD);
            intent.setClass(RecyclerMasterActivity.this, EventManageActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_clearCache){
            EventItemManager.clearDiskCache(this);
            Glide.get(this).clearMemory();
        }else if(id == R.id.action_settings){
            Intent intent = new Intent();
            intent.setClass(RecyclerMasterActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_log){
            Intent intent = new Intent();
            intent.setClass(RecyclerMasterActivity.this, LogActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_clear){
            EventItemManager.clear();
        }else if(id == R.id.action_rebuild_fest){
            EventItemManager.rewriteOrigin(this);
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
