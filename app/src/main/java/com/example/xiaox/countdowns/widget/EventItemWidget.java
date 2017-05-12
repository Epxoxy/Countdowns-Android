package com.example.xiaox.countdowns.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.extension.XLog;

/**
 * Implementation of App Widget functionality.
 */
public class EventItemWidget extends AppWidgetProvider {
    public static final String WIDGET_OPACITY_ACTION = "com.example.xiaox.widget.action.opacity.updated";
    public static final String WIDGET_ITEMS_UPDATED = "com.example.xiaox.widget.action.items.updated";
    private static final String UPDATE_ITEMS_CHANGED_TYPE_KEY = "updateItemsChanged";
    public static final int TYPE_ADDED = 0;
    public static final int TYPE_REMOVED = 1;

    private static void updateAppWidget(Context context, AppWidgetManager aWM, int aWId) {
        // Construct the RemoteViews object
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.event_item_widget);
        //views.setTextViewText(R.id.dayswidget, "What the fuck");
        //XLog.logLine("views.setTextViewText");
        //views.setTextViewText(R.id.appwidget_text, "Hello world");
        // Instruct the widget manager to update the widget
        //aWM.updateAppWidget(aWId, views);

    }

    private void updateWidget(Context context){
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId:appWidgetIds) {
            XLog.logLine("onUpdate " + appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
        context.startService(new Intent(context,WidgetUpdateService.class));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
        context.stopService(new Intent(context,WidgetUpdateService.class));

    }

    @Override
    public void onReceive(Context context, Intent intent){
        XLog.logLine("onReceive " + intent.getAction());
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds){
        WidgetConfigureActivity.removeObjects(context, appWidgetIds);
        for(int id : appWidgetIds){
            context.sendBroadcast(intentOfItemsRemoved(id));
            XLog.logLine("onDeleted " + id);
        }
    }

    public static Intent intentOfItemsAdded(int appWidgetId){
        return intentOfItemsUpdated(appWidgetId, TYPE_ADDED);
    }

    public static Intent intentOfItemsRemoved(int appWidgetId){
        return intentOfItemsUpdated(appWidgetId, TYPE_REMOVED);
    }

    private static Intent intentOfItemsUpdated(int appWidgetId, int type){
        return new Intent().setAction(WIDGET_ITEMS_UPDATED)
                .putExtra(UPDATE_ITEMS_CHANGED_TYPE_KEY,type)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    }

    public static int getChangeType(Intent intent){
        return intent.getIntExtra(UPDATE_ITEMS_CHANGED_TYPE_KEY, TYPE_ADDED);
    }

    public static int getAppWidgetId(Intent intent){
        return intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }
}

