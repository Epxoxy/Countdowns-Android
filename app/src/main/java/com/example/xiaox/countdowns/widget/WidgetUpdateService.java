package com.example.xiaox.countdowns.widget;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.widget.RemoteViews;

import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.util.List;

public class WidgetUpdateService extends Service {
    private SparseArray<EventItem> updateItems;
    private SparseArray<EventItem> soonItems;
    private boolean isUpdating;
    private boolean isSoonUpdating;

    public BroadcastReceiver timeTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                /*if(intent.getAction().equals(Intent.ACTION_TIME_CHANGED)){
                    XLog.logLine("ACTION_TIME_CHANGED");
                }else if(intent.getAction().equals(Intent.ACTION_TIME_TICK)){
                    XLog.logLine("ACTION_TIME_TICK");
                }else */
                if(intent.getAction().equals(EventItemWidget.WIDGET_OPACITY_ACTION)){
                    Bundle bundle = intent.getExtras();
                    int appWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                    RemoteViews views = RemoteViewsUpdater.getViews(context);
                    EventItem item = updateItems.get(appWidgetId);
                    if(null != item){
                        //Change and save
                        item.imagedata.useTransparent = !item.imagedata.useTransparent;
                        WidgetConfigureActivity.saveToSpf(context, appWidgetId, item);
                        //Update views
                        RemoteViewsUpdater.updateBackground(context, views, item.imagedata);
                        RemoteViewsUpdater.savePartiallyUpdateToAppWidget(context, appWidgetId, views);
                    }else{
                        XLog.logLine("EventItem is null.");
                    }
                    XLog.logLine("UpdateTransparent for " + appWidgetId);
                }else if(intent.getAction().equals(EventItemWidget.WIDGET_ITEMS_UPDATED)){
                    int appWidgetId = EventItemWidget.getAppWidgetId(intent);
                    if(appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                        int type = EventItemWidget.getChangeType(intent);
                        if(type == EventItemWidget.TYPE_ADDED) updateItemsOnAdded(appWidgetId);
                        else updateItemsOnRemoved(appWidgetId);
                        XLog.logLine("WIDGET_ITEMS_UPDATED : " + appWidgetId);
                    }else{
                        XLog.logLine("AppWidgetId : " + appWidgetId + ", INVALID_APPWIDGET_ID : "
                                + AppWidgetManager.INVALID_APPWIDGET_ID );
                    }
                }
                XLog.logLine("Update service : " + intent.getAction());
            }
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();
        improvePriority();
        initUpdateItems();
        //Register receiver
        IntentFilter filter = new IntentFilter();
        /*filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);*/
        filter.addAction(EventItemWidget.WIDGET_OPACITY_ACTION);
        filter.addAction(EventItemWidget.WIDGET_ITEMS_UPDATED);
        getApplicationContext().registerReceiver(timeTickReceiver, filter);
        XLog.logLine("------ Update service : onCreate -----");
    }

    private boolean hasUpdateItems(){
        return updateItems != null && updateItems.size() > 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        /*AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getBaseContext(), EventItemWidget.class);
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 0,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + ALARM_DURATION, pendingIntent);*/
        initUpdateItems();
        XLog.logLine("onStartCommand " + startId);
        return START_STICKY;
    }

    private void improvePriority() {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, WidgetUpdateService.class), 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Foreground Service")
                .setContentText("Foreground Service Started.")
                .build();
        notification.contentIntent = contentIntent;
        startForeground(0, notification);
    }

    private void initUpdateItems(){
        int[] appWidgetIds = RemoteViewsUpdater.getAppWidgetIds(this);
        for(int appWidgetId : appWidgetIds){
            XLog.logLine("appWidgetId: " + appWidgetId);
        }
        updateItems = WidgetConfigureActivity.loadSparseArrayFromSpf(this, appWidgetIds);
        soonItems = new SparseArray<>();
        if(hasUpdateItems()){
            int unComing = 0;
            for(int i = 0; i < updateItems.size(); i++){
                int id = updateItems.keyAt(i);
                EventItem item = updateItems.get(id);
                if(item != null){
                    RemoteViewsUpdater.initWidgetViews(this, id, item);
                    if(item.state() == EventItem.UN_COMING) ++unComing;
                }
            }
            if(unComing > 0) reactiveUpdating();
        }
    }

    private void updateItemsOnAdded(int appWidgetId){
        EventItem newItem = WidgetConfigureActivity.loadFromSpf(this, appWidgetId);
        if(newItem != null){
            //Update update items
            int index1 = updateItems.indexOfKey(appWidgetId);
            if(index1 >= 0) updateItems.put(appWidgetId, newItem);
            else updateItems.append(appWidgetId, newItem);
            //Update views
            RemoteViewsUpdater.initWidgetViews(this, appWidgetId, newItem);
            reactiveUpdating();
            if(newItem.remainTime <= 60000){
                soonItems.append(appWidgetId, newItem);
                ensureSoonList();
            }
        }
    }

    private void updateItemsOnRemoved(int appWidgetId){
        int index = updateItems.indexOfKey(appWidgetId);
        if(index >= 0){
            updateItems.delete(appWidgetId);
        }
    }

    private void reactiveUpdating(){
        if(!isUpdating){
            isUpdating = true;
            handler.sendEmptyMessage(0);
        }
    }

    private void ensureSoonList(){
        if(!isSoonUpdating){
            isSoonUpdating = true;
            soonHandler.sendEmptyMessage(0);
        }
    }

    private Handler soonHandler = new Handler(){
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:{
                    this.removeMessages(0);
                    int unComing = 0;
                    for(int i = 0; i < soonItems.size(); i++){
                        int id = soonItems.keyAt(i);
                        EventItem item = soonItems.get(id);
                        if(null != item){
                            RemoteViews views = RemoteViewsUpdater.getViews(WidgetUpdateService.this);
                            RemoteViewsUpdater.updateTimeViews(views, item);
                            RemoteViewsUpdater.savePartiallyUpdateToAppWidget(WidgetUpdateService.this, id, views);
                            if(item.state() == EventItem.UN_COMING){
                                ++unComing;
                            }else{
                                soonItems.remove(id);
                            }
                        }
                    }
                    if(unComing > 0){
                        this.sendEmptyMessageDelayed(0, 10000);
                    }else{
                        isSoonUpdating = false;
                    }
                }break;
                case 1:{
                    this.removeMessages(0);
                    isSoonUpdating = false;
                }break;
                default:break;
            }
        }
    };

    private Handler handler = new Handler(){
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:{
                    this.removeMessages(0);
                    int unComing = 0;
                    for(int i = 0; i < updateItems.size(); i++){
                        int id = updateItems.keyAt(i);
                        EventItem item = updateItems.get(id);
                        if(null != item){
                            RemoteViews views = RemoteViewsUpdater.getViews(WidgetUpdateService.this);
                            RemoteViewsUpdater.updateTimeViews(views, item);
                            RemoteViewsUpdater.savePartiallyUpdateToAppWidget(WidgetUpdateService.this, id, views);
                            if(item.state() == EventItem.UN_COMING){
                                ++unComing;
                                if(item.remainTime <= 60000){
                                    soonItems.append(id, item);
                                }
                            }
                        }
                    }
                    if(unComing > 0){
                        this.sendEmptyMessageDelayed(0, 60000);
                    }else{
                        isUpdating = false;
                    }
                    if(soonItems.size() > 0) ensureSoonList();
                }break;
                case 1:{
                    this.removeMessages(0);
                    isUpdating = false;
                }break;
                default:break;
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.handler.removeMessages(0);
        if(null != timeTickReceiver){
            getApplicationContext().unregisterReceiver(timeTickReceiver);
        }
        this.stopForeground(true);
        XLog.logLine("service onDestroy");
    }

    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
