package com.example.xiaox.countdowns.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.adapter.EventViewAdapter;
import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;
import com.example.xiaox.countdowns.basic.extension.ComplexPreferences;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaox on 2/25/2017.
 */

public class WidgetConfigureActivity extends Activity {
    private static final String PREFS_NAME = "com.example.xiaox.countdowns.widget.EventItemWidget";
    private static final String PREF_PREFIX_KEY = "eventitemwidget_";
    private int itemWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            itemWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if(itemWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            Intent result = new Intent();
            result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, itemWidgetId);
            setResult(RESULT_CANCELED, result);
            finish();
            return;
        }
        setContentView(R.layout.activity_list_view_master);

        ListView listView = (ListView) findViewById(R.id.master_lv);
        if(listView != null){
            EventItemManager.with(this);
            EventViewAdapter adapter = new EventViewAdapter(this, R.layout.eventitem_list_thumb);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Context context = WidgetConfigureActivity.this;
                    EventItem item = (EventItem)parent.getAdapter().getItem(position);
                    Intent result = getIntent();
                    result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, itemWidgetId);
                    if(item != null){
                        // Store locally
                        XLog.logLine("Put extra " + itemWidgetId + " of item " +  item.getID());
                        saveToSpf(context, itemWidgetId, item);
                        // It is the responsibility of the configuration activity to update the app widget
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        //Set result
                        setResult(RESULT_OK, result);
                        context.sendBroadcast(EventItemWidget.intentOfItemsAdded(itemWidgetId));
                    }else{
                        setResult(RESULT_CANCELED, result);
                    }
                    finish();
                }
            });
        }
    }

    static void saveToSpf(Context context, int appWidgetId, EventItem item){
        ComplexPreferences cPref = ComplexPreferences.getComplexPreferences(context, PREFS_NAME, 0);
        String key = PREF_PREFIX_KEY + appWidgetId;
        cPref.putObject(key, item);
        cPref.commit();
    }

    static EventItem loadFromSpf(Context context, int appWidgetId){
        ComplexPreferences cPref = ComplexPreferences.getComplexPreferences(context, PREFS_NAME, 0);
        String key = PREF_PREFIX_KEY + appWidgetId;
        EventItem item = cPref.getObject(key, EventItem.class);
        return item;
    }

    static List<EventItem> loadFromSpf(Context context, int[] appWidgetIds){
        ComplexPreferences cPref = ComplexPreferences.getComplexPreferences(context, PREFS_NAME, 0);
        List<EventItem> items = new ArrayList<>();
        for (int appWidgetId : appWidgetIds){
            items.add(cPref.getObject(PREF_PREFIX_KEY + appWidgetId, EventItem.class));
        }
        return items;
    }

    static SparseArray<EventItem> loadSparseArrayFromSpf(Context context, int[] appWidgetIds){
        ComplexPreferences cPref = ComplexPreferences.getComplexPreferences(context, PREFS_NAME, 0);
        SparseArray<EventItem> items = new SparseArray<>();
        for (int appWidgetId : appWidgetIds){
            items.put(appWidgetId, cPref.getObject(PREF_PREFIX_KEY + appWidgetId, EventItem.class));
            XLog.logLine("Load " + appWidgetId + " of " + items.get(appWidgetId));
        }
        return items;
    }


    static void removeObject(Context context, int appWidgetId){
        ComplexPreferences cPref = ComplexPreferences.getComplexPreferences(context, PREFS_NAME, 0);
        cPref.removeObject(PREF_PREFIX_KEY + appWidgetId);
        cPref.commit();
    }

    static void removeObjects(Context context, int[] appWidgetIds){
        ComplexPreferences cPref = ComplexPreferences.getComplexPreferences(context, PREFS_NAME, 0);
        for (int appWidgetId : appWidgetIds){
            cPref.removeObject(PREF_PREFIX_KEY + appWidgetId);
        }
        cPref.commit();
    }


}
