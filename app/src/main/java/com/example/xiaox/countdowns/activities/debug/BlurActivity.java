package com.example.xiaox.countdowns.activities.debug;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.image.BitmapUtils;
import com.example.xiaox.countdowns.basic.file.FileUtils;

import java.io.File;

public class BlurActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int FILE_SELECT_CODE = 2;
    private static final int MAX_RADIUS = 25;
    private static final String IMG_TEMP_NAME = "tempblur.jpg";
    private String folder;
    private String filePath;
    private ImageView backImage;
    private ImageView foreImage;
    private int alpha = 255;
    private int radius = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);
        //Data basic
        FileUtils.verifyStoragePermission(this);
        //View
        backImage = (ImageView)findViewById(R.id.backImageView);
        foreImage = (ImageView)findViewById(R.id.foreImageView);
        Button setImgBtn = (Button)findViewById(R.id.setImgBtn);
        SeekBar radiusBar = (SeekBar)findViewById(R.id.radiusSeekBar);
        setupActionBar(getSupportActionBar());
        setupImageBtn(setImgBtn);
        setupSeekBar(radiusBar);
    }

    private void setupActionBar(ActionBar actionBar){
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupImageBtn(Button btn){
        if(btn!= null){
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCallSelectedImage();
                }
            });
        }
    }

    private void setupSeekBar(SeekBar radiusBar){
        if(radiusBar != null)
            radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = (int)((progress / (float)seekBar.getMax()) * MAX_RADIUS);
                BlurActivity.this.alpha = seekBar.getMax() - progress;
                onRadiusChanged(radius, BlurActivity.this.alpha);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void onRadiusChanged(int radius, int alpha){
        this.radius = radius;
        foreImage.setImageAlpha(alpha);
    }

    private void onCallSelectedImage(){
        Intent gIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(gIntent, "Select background");
        startActivityForResult(wrapperIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            Uri uri = data.getData();
            filePath = FileUtils.pathFromUri(this, uri);
            if(filePath == null){
                foreImage.setImageResource(0);
                backImage.setImageResource(0);
            }else{
                folder = new File(filePath).getParentFile().getAbsolutePath();
                foreImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
                Bitmap origin = BitmapFactory.decodeFile(filePath);
                Bitmap blur = BitmapUtils.blur(this, origin, 25);
                backImage.setImageBitmap(blur);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
