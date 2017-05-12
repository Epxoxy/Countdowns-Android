package com.example.xiaox.countdowns.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;
import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.adapter.EventViewUpdater;
import com.example.xiaox.countdowns.basic.extension.XLog;
import com.example.xiaox.countdowns.activities.debug.LogActivity;
//TODO Unhand problem
//Some problem in this activity
//This activity use Glide to load image with crossFade
//But sometimes it won't effect
public class DetailActivity extends AppCompatActivity {

    public static final String ITEM_KEY = "detail_item";
    private EventViewUpdater updater;
    private EventItem eventItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Check if empty
        //For some reason in my MI4 phone,
        //When there is leak memory and app run in background
        //Try to resume app will cause index out of range(ITEM's size is zero)
        Intent intent = getIntent();
        int itemKey = intent.getIntExtra(ITEM_KEY, 0);
        eventItem = EventItemManager.get(itemKey);
        XLog.logLine("Get extra of item's key : " + itemKey);
        if (eventItem == null) {
            this.finish();
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(eventItem.name);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            updater = new EventViewUpdater(this, true);
        }
    }

    private Handler updateTimeHandler = new Handler() {
        public void handleMessage(Message message) {
            handMsg(message.what);
        }
    };

    private void handMsg(int what) {
        switch (what) {
            case 0:
                updateTimeHandler.removeMessages(0);
                updater.updateTimeAndViewByType(EventItem.PARAM_REMAIN_MILLIS, eventItem);
                if (eventItem.isUnComing()) {
                    updateTimeHandler.sendEmptyMessageDelayed(0, 1000);
                }
                break;
            case 1:
                updateTimeHandler.removeMessages(0);
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (eventItem == null) {
            this.finish();
        } else {
            updater.refreshHoleViewBy(eventItem);
            updater.updateTargetMillisToDate(eventItem.getMillisToDate());
            this.updateTimeHandler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.updateTimeHandler.removeMessages(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateTimeHandler.removeMessages(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.action_editItem) {
            Intent intent = new Intent();
            intent.putExtra(EventManageActivity.TYPE_KEY, EventManageActivity.TYPE_EDIT);
            intent.putExtra(EventManageActivity.ITEM_INDEX_KEY, EventItemManager.ITEMS.indexOf(eventItem));
            intent.setClass(DetailActivity.this, EventManageActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_deleteItem) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailActivity.this);
            alertDialog.setTitle(R.string.delete_item_tips).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventItemManager.removeItem(eventItem);
                    EventItemManager.save();
                    DetailActivity.this.finish();
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ;
                }
            }).create().show();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(DetailActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_log) {
            Intent intent = new Intent();
            intent.setClass(DetailActivity.this, LogActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);
        return true;
    }
}
