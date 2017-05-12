package com.example.xiaox.countdowns.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;

import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.activities.MainActivity;
import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.extension.XLog;
import com.example.xiaox.countdowns.basic.file.FileUtils;
import com.example.xiaox.countdowns.basic.image.BitmapUtils;
import com.example.xiaox.countdowns.basic.image.ImageData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by xiaox on 2/26/2017.
 */

public class RemoteViewsUpdater {

    static RemoteViews getViews(Context context) {
        return new RemoteViews(context.getPackageName(), R.layout.event_item_widget);
    }

    static void initWidgetViews(Context context, int appWidgetId, EventItem item){
        XLog.logLine("Init : " + item.name + "," + item.getID() +  "," + item.getMillisToDate());
        RemoteViews views = getViews(context);
        //Update text
        views.setTextViewText(R.id.widgetName, item.name);
        views.setTextViewText(R.id.widgetId, String.valueOf(item.getID()));
        views.setTextViewText(R.id.widgetTarget, item.getMillisToDate());
        updateBackground(context, views, item.imagedata);
        updateTimeViews(views, item);
        setOpacityClick(context, views, appWidgetId);
        setOpenApplicationClick(context, views, appWidgetId);
        savePartiallyUpdateToAppWidget(context, appWidgetId, views);
    }

    private static void setOpacityClick(Context context, RemoteViews views, int appWidgetId){
        Intent btnIntent = new Intent().setAction(EventItemWidget.WIDGET_OPACITY_ACTION);
        btnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent btnPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, btnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetName, btnPendingIntent);
    }

    private static void setOpenApplicationClick(Context context, RemoteViews views, int appWidgetId){
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent btnPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetCelebration, btnPendingIntent);
        Intent intent2 = new Intent(context, MainActivity.class);
        PendingIntent btnPendingIntent2 = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetTimeRoot, btnPendingIntent);
    }

    static void updateTimeViews(RemoteViews views, EventItem item) {
        item.updateTime();
        if (item.state() == EventItem.GONE) {
            views.setViewVisibility(R.id.widgetTimeRoot, View.GONE);
            views.setViewVisibility(R.id.widgetCelebration, View.VISIBLE);
            views.setTextViewText(R.id.widgetCelebration, item.celebration);
            XLog.logLine("updateCelebration");
        } else {
            views.setTextViewText(R.id.widgetDays, String.valueOf(item.days));
            views.setTextViewText(R.id.widgetHours, String.format("%02d", item.hours));
            views.setTextViewText(R.id.widgetMinutes, String.format("%02d", item.minutes));
            XLog.logLine("updateTime");
        }
    }

    private static void saveUpdateToAppWidget(Context context, int appWidgetId, RemoteViews views) {
        AppWidgetManager awg = AppWidgetManager.getInstance(context);
        awg.updateAppWidget(appWidgetId, views);
    }

    static void savePartiallyUpdateToAppWidget(Context context, int appWidgetId, RemoteViews views){
        AppWidgetManager awg = AppWidgetManager.getInstance(context);
        awg.partiallyUpdateAppWidget(appWidgetId, views);
    }

    static int[] getAppWidgetIds(Context context){
        ComponentName thisWidget = new ComponentName(context, EventItemWidget.class);
        AppWidgetManager awg = AppWidgetManager.getInstance(context);
        return awg.getAppWidgetIds(thisWidget);
    }

    static void updateBackground(Context context, RemoteViews views, ImageData data){
        if(data.useTransparent){
            updateTransparent(views);
        }else{
            updateImage(context, views, data);
        }
    }

    private static void updateTransparent(RemoteViews views) {
        views.setInt(R.id.widgetImage, "setBackgroundColor", Color.TRANSPARENT);
        views.setImageViewResource(R.id.widgetImage, 0);
    }

    private static void updateImage(Context context, RemoteViews views, ImageData data) {
        if (data.hasData()) {
            if (!data.hasImage()) {
                views.setInt(R.id.widgetImage, "setBackgroundColor", data.color());
            } else {
                FileInputStream fis = null;
                try {
                    if (data.isInAssets()) {
                        fis = context.getAssets().openFd(FileUtils.toAssetPath(data.path())).createInputStream();
                    } else {
                        fis = new FileInputStream(new File(data.path()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fis != null) {
                    //Decode to bitmap
                    Bitmap origin = BitmapFactory.decodeStream(fis);
                    if (origin != null) {
                        //Blur
                        if (data.radius > 0) {
                            Bitmap blurBitmap = BitmapUtils.blurSampling(context, origin, data.radius, data.sampling);
                            views.setImageViewBitmap(R.id.widgetImage, blurBitmap);
                        } else {
                            views.setImageViewBitmap(R.id.widgetImage, origin);
                        }
                    }
                }
            }
        } else {
            views.setImageViewResource(R.id.widgetImage, R.drawable.def_thumb);
        }
    }

}
