package com.example.xiaox.countdowns.activities.debug;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.activities.SettingsActivity;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;
import com.example.xiaox.countdowns.basic.extension.XLog;

import per.epxoxy.event.IEventHandler;

public class LogActivity extends AppCompatActivity {
    TextView textView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        setupActionBar();
        textView = (TextView)findViewById(R.id.logTextView);
        XLog.xLog().onLogUpdated.addHandler(onLogUpdated);
        textView.setText(XLog.logs());
        Button clearBtn = (Button)findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLog.clear();
            }
        });
    }

    private IEventHandler<XLog.LogArgs> onLogUpdated = new IEventHandler<XLog.LogArgs>() {
        @Override
        public void onEvent(Object sender, XLog.LogArgs args) {
            if(textView != null){
                if(args.type == XLog.Type.Add){
                    textView.setText(XLog.logs());
                }else{
                    textView.setText("");
                }
            }
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        XLog.xLog().onLogUpdated.removeHandler(onLogUpdated);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }else if(id == R.id.action_clearCache){
            EventItemManager.clearDiskCache(this);
            Glide.get(this).clearMemory();
        }else if(id == R.id.action_settings){
            Intent intent = new Intent();
            intent.setClass(LogActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_clear){
            EventItemManager.clear();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Logs");
        }
    }
}
