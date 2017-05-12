package com.example.xiaox.countdowns.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xiaox.countdowns.R;

public class DialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        TextView ignore = (TextView)findViewById(R.id.dialogIgnore);
        TextView iKnow = (TextView)findViewById(R.id.dialogIKnow);
        if(ignore != null) ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogActivity.this.finish();
            }
        });
        if(iKnow != null) iKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogActivity.this.finish();
            }
        });
    }

    public static void startAlertActivity(Context context){
        Intent intent = new Intent(context, DialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION );
        context.startActivity(intent);
    };
}
