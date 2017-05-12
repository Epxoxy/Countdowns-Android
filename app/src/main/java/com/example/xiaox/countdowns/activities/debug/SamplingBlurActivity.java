package com.example.xiaox.countdowns.activities.debug;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.image.BitmapUtils;
import com.example.xiaox.countdowns.basic.file.FileUtils;
import com.example.xiaox.countdowns.basic.image.ImageData;
import com.example.xiaox.countdowns.basic.image.SamplingBlurAsyncTask;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.io.File;

public class SamplingBlurActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int DEF_MAX_SAMPLING = 10;
    private static final int MAX_RADIUS = 25;
    private int maxSampling = DEF_MAX_SAMPLING;
    private ImageData imageData;
    private SamplingBlurAsyncTask samplingBlurAsyncTask;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampling_blur);
        //Data basic
        FileUtils.verifyStoragePermission(this);
        imageData = ImageData.createEmpty();
        //View
        Button saveAsBtn = (Button)findViewById(R.id.saveasBtn);
        image = (ImageView)findViewById(R.id.blurImageView);
        Button setMaxSampling = (Button)findViewById(R.id.setMaxSamplingBtn);
        Button setImgBtn = (Button)findViewById(R.id.setImgBtn);
        SeekBar radiusBar = (SeekBar)findViewById(R.id.radiusSeekBar);
        SeekBar samplingBar = (SeekBar)findViewById(R.id.samplingSeekBar);
        setupMaxSampling(setMaxSampling);
        setupActionBar(getSupportActionBar());
        setupImageBtn(setImgBtn);
        setupSeekBar(radiusBar, samplingBar);
        setupSaveAsBtn(saveAsBtn);
    }

    private void setupMaxSampling(Button maxSampling){
        if(maxSampling != null){
            maxSampling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCallSetMaxSampling();
                }
            });
        }
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

    private void setupSeekBar(SeekBar radiusBar, SeekBar samplingBar){
        if(radiusBar != null)
            radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int radius = (int)((progress / (float)seekBar.getMax()) * MAX_RADIUS);
                    if(radius != imageData.radius){
                        onRadiusChanged(radius);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        if(samplingBar != null)
            samplingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int sampling = (int)((progress / (float)seekBar.getMax()) * maxSampling);
                    if(sampling != imageData.sampling){
                        onSamplingChanged(sampling);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
    }

    private void setupSaveAsBtn(Button saveAsBtn){
        if(saveAsBtn != null){
            saveAsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imageData.path() == null || TextUtils.isEmpty(imageData.path())) return;
                    File file = new File(imageData.path());
                    if(!file.exists()) return;
                    final String folder = file.getParentFile().getAbsolutePath();
                    String fileName = "blur_" + file.getName();
                    final EditText editText = new EditText(SamplingBlurActivity.this);
                    editText.setPadding(20,20,20,20);
                    editText.setText(fileName);
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SamplingBlurActivity.this);
                    AlertDialog dialog = dialogBuilder
                            .setView(editText)
                            .setTitle("Save file name")
                            .setPositiveButton(R.string.ok, null)
                            .setNegativeButton(R.string.cancel, null)
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            Button btn = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String name = editText.getText().toString();
                                    if(TextUtils.isEmpty(name)){
                                        editText.setError(getString(R.string.cant_empty));
                                        editText.requestFocus();
                                    }else{
                                        String newPath = folder + File.separator + name;
                                        if(new File(newPath).exists()){
                                            editText.setError("File exist!");
                                            editText.requestFocus();
                                        }else{
                                            saveFile(imageData, newPath);
                                            dialog.dismiss();
                                        }
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    private void saveFile(ImageData imageData, String newPath){
        Bitmap blurBitmap = BitmapUtils.blurSampling2(this, imageData.path(), imageData.radius, imageData.sampling);
        boolean success = false;
        if(blurBitmap != null) {
            success = BitmapUtils.saveBitmapAsJPG(blurBitmap, 100, newPath);
        }
        Toast.makeText(this, success ? "Save successful!" : "Save fail!", Toast.LENGTH_SHORT).show();
    }

    private void onRadiusChanged(int radius){
        imageData.radius = radius;
        if(imageData.hasImage()){
            asyncRunSamplingBlur(imageData);
        }
    }

    private void onSamplingChanged(int sampling){
        imageData.sampling = sampling;
        if(imageData.hasImage()){
            asyncRunSamplingBlur(imageData);
        }
    }

    private void onMaxSamplingChanged(int oldMax, int newMax){
        if(this.maxSampling == newMax) return;
        this.maxSampling = newMax;
        int newSampling = (int)((imageData.sampling / (float)oldMax) * newMax);
        this.onSamplingChanged(newSampling);
    }

    private void onCallSetMaxSampling(){
        final EditText editText = new EditText(SamplingBlurActivity.this);
        editText.setText(String.valueOf(maxSampling));
        AlertDialog dialog = (new AlertDialog.Builder(this))
                .setTitle("Set max sampling")
                .setView(editText)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button btn = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editText.getText().toString();
                        int newMax = 0;
                        try{
                            newMax = Integer.parseInt(name);
                        }catch (Exception ex){
                            XLog.logLine(ex.getMessage());
                            editText.setError(getString(R.string.error_input));
                            editText.requestFocus();
                            return;
                        }
                        dialog.dismiss();
                        onMaxSamplingChanged(maxSampling, newMax);
                    }
                });
            }
        });
        dialog.show();
    }

    private void onImageChanged(ImageData imageData){
        if(imageData.hasImage()){
            asyncRunSamplingBlur(imageData);
        }
    }

    private void asyncRunSamplingBlur(ImageData imgData){
        ensureSamplingBlurTaskCanceled();
        samplingBlurAsyncTask = SamplingBlurAsyncTask.create(this, image);
        samplingBlurAsyncTask.execute(imgData);
    }

    private void ensureSamplingBlurTaskCanceled(){
        if(samplingBlurAsyncTask != null)
            samplingBlurAsyncTask.cancel(true);
    }

    private void onCallSelectedImage(){
        Intent gIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        /*gIntent.putExtra("crop", "true");
        gIntent.putExtra("aspectX", 1);*/
        gIntent.setType("image/*");
        /*File tempFile = new File(tempPath);
        if (tempFile.exists()) tempFile.delete();
        gIntent.putExtra("output", Uri.fromFile(tempFile));
        gIntent.putExtra("outputFormat", "JPEG");*/
        Intent wrapperIntent = Intent.createChooser(gIntent, "Select background");
        startActivityForResult(wrapperIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            //Update to selected file
            Uri uri = data.getData();
            String path = FileUtils.pathFromUri(this, uri);
            XLog.logLine(uri.toString());
            XLog.logLine(path);
            if(path != null){
                imageData.setLocal(path);
                onImageChanged(imageData);
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
