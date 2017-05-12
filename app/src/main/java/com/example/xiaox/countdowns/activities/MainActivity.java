package com.example.xiaox.countdowns.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.extension.XLog;
import com.example.xiaox.countdowns.activities.debug.DebugNavActivity;
import com.example.xiaox.countdowns.activities.debug.LogActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static String LOADED_LANG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSavedConfig();
        //Setup view
        TextView startMain = (TextView)findViewById(R.id.startMain);
        TextView debugBlur = (TextView)findViewById(R.id.debugBlur);
        TextView subtitle = (TextView)findViewById(R.id.homeSubtitle);
        TextView homeTitle = (TextView)findViewById(R.id.homeTitle);
        TextView settingsTextView = (TextView)findViewById(R.id.settings);
        //Setup listener
        if(startMain != null)startMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ListViewMasterActivity.class);
                startActivity(intent);
            }
        });
        if(homeTitle != null)homeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ListViewMasterActivity.class);
                startActivity(intent);
            }
        });
        if(debugBlur != null)debugBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DialogActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION );
                startActivity(intent);
                //DialogActivity.startAlertActivity(MainActivity.this);
                //Intent intent = new Intent();
                //intent.setClass(MainActivity.this, DebugNavActivity.class);
                //startActivity(intent);
            }
        });
        if(subtitle != null)subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LogActivity.class);
                startActivity(intent);
            }
        });
        if(settingsTextView != null)settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadSavedConfig(){
        //Get stored language
        String notSet = getString(R.string.not_set);
        SharedPreferences preferences = this.getSharedPreferences(
                getString(R.string.sharedPref_stored_key), Context.MODE_PRIVATE);
        String storedLang = preferences.getString(getString(R.string.lang_pref_key_str), notSet);
        if(!storedLang.equals(notSet)){
            Locale locale;
            String en_value =getString(R.string.lang_en);
            if(storedLang.equals(en_value)){
                locale = Locale.ENGLISH;
            }else{
                locale = Locale.SIMPLIFIED_CHINESE;
            }
            //Save changed
            setLanguage(locale, this);
            LOADED_LANG = storedLang;
            XLog.logLine("StoredLang " + storedLang);
        }else{
            XLog.logLine("No storedLang");
        }
        //Setup log
        XLog.logEnable = preferences.getBoolean(getString(R.string.debug_switch_key), false);
        XLog.logLine("XLong is enable " + XLog.logEnable);
    }

    public static void callSetLangRestartHome(Activity activity){
        Locale curLocale = activity.getResources().getConfiguration().locale;
        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
            setLangRestartHome(Locale.ENGLISH, activity);
        } else {
            setLangRestartHome(Locale.SIMPLIFIED_CHINESE, activity);
        }
    }

    public static void setLangRestartHome(Locale locale, Activity activity) {
        setLanguage(locale, activity);
        restartActivity(activity);
    }

    public static void setLanguage(Locale locale, Activity activity){
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = locale;
        resources.updateConfiguration(config, dm);
    }

    private static void restartActivity(Activity activity){
        final Intent intent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

}
