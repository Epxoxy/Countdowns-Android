package com.example.xiaox.countdowns.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.xiaox.countdowns.basic.extension.XLog;

/**
 * Created by xiaox on 3/4/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int id = intent.getIntExtra("id", -1);
        long alarmTime = intent.getLongExtra("alarm_time", -1);
        XLog.logLine("Alarm time " + alarmTime);
        Toast.makeText(context, "Received action = " + action + ", id = " + id, Toast.LENGTH_SHORT).show();
    }
}
