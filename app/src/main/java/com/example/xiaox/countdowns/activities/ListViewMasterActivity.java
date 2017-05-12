package com.example.xiaox.countdowns.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.adapter.EventViewAdapter;
import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;
import com.example.xiaox.countdowns.basic.eventItem.ItemPropertyChangedArgs;
import com.example.xiaox.countdowns.basic.extension.XLog;
import com.example.xiaox.countdowns.activities.debug.LogActivity;

import java.util.List;

import per.epxoxy.event.EventArgs;
import per.epxoxy.event.IEvent;
import per.epxoxy.event.IEventHandler;

public class ListViewMasterActivity extends AppCompatActivity {
    private ListView listView;
    private EventViewAdapter adapter;
    private boolean isRunning = false;
    //private EventViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_master);

        listView = (ListView) findViewById(R.id.master_lv);
        if(listView != null){
            adapter = new EventViewAdapter(this, R.layout.eventitem_list_thumb);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ListViewMasterActivity.this, DetailActivity.class);
                    intent.putExtra(DetailActivity.ITEM_KEY, position);
                    startActivity(intent);
                }
            });
        }
        EventItemManager.with(this).onEventItemUpdated.addHandler(itemUpdatedHandler);
        EventItemManager.with(this).onItemPropertyUpdated.addHandler(itemPropertyChangedHandler);
        XLog.logLine("On list view master activity created");
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setDisplayShowTitleEnabled(false);
            actionbar.setDisplayShowCustomEnabled(true);
            actionbar.setCustomView(R.layout.actionbar_title);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        isRunning = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        isRunning = false;
    }

    private IEventHandler<EventArgs> itemUpdatedHandler = new IEventHandler<EventArgs>() {
        @Override
        public void onEvent(Object o, EventArgs eventArgs) {
            adapter.notifyItemsChanged();
            if(isRunning && allOutOf) {
                updateHandler.sendEmptyMessage(DELAY_REPEAT);
            }
        }
    };

    private IEventHandler<ItemPropertyChangedArgs> itemPropertyChangedHandler = new IEventHandler<ItemPropertyChangedArgs>() {
        @Override
        public void onEvent(Object o, ItemPropertyChangedArgs args) {
            adapter.notifyItemChanged(args.index);
            if(isRunning && allOutOf) {
                updateHandler.sendEmptyMessage(DELAY_REPEAT);
            }
        }
    };

    private boolean allOutOf;
    private static final int REPEAT_UPDATE = 0;
    private static final int STOP_UPDATE = 1;
    private static final int UPDATE_ALL_THEN_REPEAT = 2;
    private static final int DELAY_REPEAT = 3;
    private Handler updateHandler = new Handler(){
        public void handleMessage(Message message) {
            switch (message.what) {
                case REPEAT_UPDATE:{
                    XLog.logLine("REPEAT_UPDATE");
                    this.removeMessages(0);
                    List<Integer> updatedIndexes = EventItemManager.updateTimeAll();
                    adapter.notifyItemsChanged(updatedIndexes, EventItem.PARAM_REMAIN_MILLIS);
                    allOutOf = updatedIndexes.size() < 1;
                    if(!allOutOf){
                        this.sendEmptyMessageDelayed(0, 1000);
                    }else{
                        XLog.logLine("UnComing empty");
                    }
                }break;
                case STOP_UPDATE:{
                    XLog.logLine("STOP_UPDATE");
                    this.removeMessages(0);
                }break;
                case UPDATE_ALL_THEN_REPEAT:{
                    XLog.logLine("UPDATE_ALL_THEN_REPEAT");
                    EventItemManager.updateTimeAll();
                    adapter.notifyItemsChanged();
                    this.sendEmptyMessageDelayed(0, 1000);
                }break;
                case DELAY_REPEAT:{
                    XLog.logLine("DELAY_REPEAT");
                    this.sendEmptyMessageDelayed(0, 1000);
                }break;
                default:break;
            }
        }
    };

    @Override
    protected void onPause(){
        super.onPause();
        this.updateHandler.removeMessages(REPEAT_UPDATE);
    }

    @Override
    protected void onResume(){
        super.onResume();
        this.updateHandler.sendEmptyMessage(UPDATE_ALL_THEN_REPEAT);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(this.isFinishing()){
            this.updateHandler.removeMessages(REPEAT_UPDATE);
        }
        XLog.logLine("ListView Master onDestroy");
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
            intent.setClass(ListViewMasterActivity.this, EventManageActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_clearCache){
            EventItemManager.clearDiskCache(this);
            Glide.get(this).clearMemory();
        }else if(id == R.id.action_settings){
            Intent intent = new Intent();
            intent.setClass(ListViewMasterActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_log){
            Intent intent = new Intent();
            intent.setClass(ListViewMasterActivity.this, LogActivity.class);
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