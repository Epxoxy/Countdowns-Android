package com.example.xiaox.countdowns.activities.debug;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.util.ArrayList;
import java.util.List;

public class DebugNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_nav);
        ListView debugListView = (ListView)findViewById(R.id.debugListView);
        if(debugListView != null){
            List<String> items = new ArrayList<>();
            items.add("RecyclerView debug");
            items.add("Delete store file");
            items.add("Sampling blur debug");
            items.add("Blur debug");
            items.add("Logs");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.textview_item, items);
            debugListView.setAdapter(adapter);
            debugListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    XLog.logLine((String)parent.getItemAtPosition(position));
                    if(position == 0){
                        Intent intent = new Intent();
                        intent.setClass(DebugNavActivity.this, RecyclerMasterActivity.class);
                        startActivity(intent);
                    }else if(position == 1){
                        EventItemManager.deleteStored();
                        Toast.makeText(DebugNavActivity.this, "OK", Toast.LENGTH_SHORT).show();
                    }else if(position == 2){
                        Intent intent = new Intent();
                        intent.setClass(DebugNavActivity.this, SamplingBlurActivity.class);
                        startActivity(intent);
                    }else if(position == 3){
                        Intent intent = new Intent();
                        intent.setClass(DebugNavActivity.this, BlurActivity.class);
                        startActivity(intent);
                    }else if(position == 4){
                        Intent intent = new Intent();
                        intent.setClass(DebugNavActivity.this, LogActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}
