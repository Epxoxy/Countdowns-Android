package com.example.xiaox.countdowns.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;
import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.adapter.EventViewUpdater;
import com.example.xiaox.countdowns.basic.image.ImageData;
import com.example.xiaox.countdowns.basic.image.SamplingBlurAsyncTask;
import com.example.xiaox.countdowns.basic.image.BitmapUtils;
import com.example.xiaox.countdowns.basic.extension.Condition;
import com.example.xiaox.countdowns.basic.file.FileUtils;
import com.example.xiaox.countdowns.views.TimePickerWrap;
import com.example.xiaox.countdowns.basic.extension.XLog;
import com.example.xiaox.countdowns.views.SwitchView;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import com.jzxiang.pickerview.TimeWheel;
import com.jzxiang.pickerview.data.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import jp.wasabeef.glide.transformations.BlurTransformation;

//import jp.wasabeef.glide.transformations.BlurTransformation;

public class EventManageActivity extends AppCompatActivity {
    //Static
    public static final String ITEM_INDEX_KEY = "EventItem_Index";
    public static final String TYPE_KEY = "Event_Type";
    public static final int TYPE_ADD = 0;
    public static final int TYPE_EDIT = 1;
    private static final String TEMP_IMG_FILENAME = "bgitemtemp.jpg";
    private static final String S_TEMP_IMG_FILENAME= "sbgitemtemp.jpg";
    private static final int DEF_MAX_SAMPLING = 10;
    private static final int MAX_RADIUS = 25;
    private static int RESULT_LOAD_GALLERY = 2;
    private static int RESULT_LOAD_IMAGE = 1;
    //Data
    private int type = 0;
    private int index;
    private ImageData imgData;
    private boolean isBlurDynamic;
    private String tempPath;
    private String selectedTempPath;
    private ImageSetter imgSetter = new ImageSetter();
    //View
    private Button setTimeBtn;
    private Button setImgBtn;
    private EditText nameEditText;
    private EditText celebrationEditText;
    private TextView timeTextView;
    private EventItem opEventItem;
    private Button positiveBtn;
    private SeekBar radiusBar;
    private SeekBar samplingBar;
    private SwitchView blurDynamicSwitch;
    private EventViewUpdater viewUpdater;
    private SamplingBlurAsyncTask samplingBlurAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_manage);
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        celebrationEditText = (EditText)findViewById(R.id.celebrationEditText);
        timeTextView = (TextView)findViewById(R.id.timeTextView);
        positiveBtn = (Button)findViewById(R.id.positiveBtn);
        //Init type
        String title = null;
        Intent intent = getIntent();
        int type  = intent.getIntExtra(TYPE_KEY, 0);
        this.type = type;
        if(type == TYPE_EDIT){
            index = intent.getIntExtra(ITEM_INDEX_KEY, -1);
            if(index > -1){
                opEventItem = EventItemManager.ITEMS.get(index).copy();
                //Update show info from exist event item
                nameEditText.setText(opEventItem.name);
                celebrationEditText.setText(opEventItem.celebration);
                timeTextView.setText(opEventItem.getMillisToDate());
                positiveBtn.setText(R.string.save);
                title = "Edit event";
            }else{
                waringExits(getString(R.string.event_item_not_exist));
            }
        }else{
            title = "Add event";
            opEventItem = new EventItem();
            opEventItem.setID(EventItemManager.generateID());
            opEventItem.name = nameEditText.getText().toString();
            opEventItem.celebration = celebrationEditText.getText().toString();
            XLog.logLine("Generated id " + opEventItem.getID());
        }
        imgData = opEventItem.imagedata;
        //Init view holder
        viewUpdater = new EventViewUpdater(this);
        viewUpdater.refreshHoleViewBy(opEventItem);
        //Setup path
        tempPath = FileUtils.pathFromExtPicture(this, TEMP_IMG_FILENAME);
        selectedTempPath = FileUtils.pathFromExtPicture(this, S_TEMP_IMG_FILENAME);
        //Setup actionbar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //Init view
        setTimeBtn = (Button)findViewById(R.id.setTimeBtn);
        setImgBtn = (Button)findViewById(R.id.setImgBtn);
        radiusBar = (SeekBar)findViewById(R.id.radiusSeekBar);
        samplingBar = (SeekBar)findViewById(R.id.samplingSeekBar);
        blurDynamicSwitch = (SwitchView)findViewById(R.id.blurDynamicSwitch);
        Button setForeBtn = (Button)findViewById(R.id.setForeBtn);
        //Setup listener
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                opEventItem.name = s.toString();
                viewUpdater.updateName(opEventItem.name);
            }
        });
        celebrationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                opEventItem.celebration = s.toString();
                viewUpdater.updateCelebration(opEventItem.celebration);
            }
        });
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallValidateData();
            }
        });
        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallTimePicker();
            }
        });
        setImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallSetBackground();
            }
        });
        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = (int)((progress / (float)seekBar.getMax() ) * MAX_RADIUS);
                if(imgData.radius != radius ){
                    onRadiusChanged(radius);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        samplingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int sampling = (int)(progress / (float)seekBar.getMax() * DEF_MAX_SAMPLING) + 1;
                if(sampling != imgData.sampling){
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
        setForeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickColor(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int i, @ColorInt int i1) {
                        onTextColorChanged(i1);
                    }
                    @Override
                    public void onDialogDismissed(int i) {
                    }
                });
            }
        });
        blurDynamicSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(boolean isOpened) {
                onIsBlurDynamicChanged(isOpened);
            }
        });
        XLog.logLine("Event manage activity created");
        //Raise if is blur dynamic for blurDynamicSwitch changing state
        initBlur(imgData);
    }

    private void initBlur(ImageData imagedata){
        if(imagedata.hasImage()){
            if(imagedata.isInAssets()){
                imgSetter.setFromAssets(imgData.path());
            }else{
                imgSetter.setFromLocalExist(imgData.path());
            }
            if(imagedata.radius > 0) {
                int sampling = imagedata.sampling > 0 ? imagedata.sampling : 1;
                radiusBar.setProgress((int)((imagedata.radius / (float)MAX_RADIUS) * radiusBar.getMax()));
                samplingBar.setProgress((int)(((sampling - 1) / (float)DEF_MAX_SAMPLING) * samplingBar.getMax()));
                XLog.logLine("Apply old blur data of radius(" + imagedata.radius
                        + "), sampling(" + imagedata.sampling+")");
                blurDynamicSwitch.setOpened(true);
            }
        }
    }

    private void onIsBlurDynamicChanged(boolean isBlurDynamic){
        if(this.isBlurDynamic != isBlurDynamic) {
            this.isBlurDynamic = isBlurDynamic;
            XLog.logLine("On is blur dynamic changed " + isBlurDynamic);
            if(this.blurDynamicSwitch.isOpened() != isBlurDynamic){
                this.blurDynamicSwitch.setOpened(isBlurDynamic);
            }
        }
    }

    private void waringExits(String exitMsg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventManageActivity.this);
        alertDialog.setTitle(exitMsg).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EventManageActivity.this.finish();
            }
        });
    }

    private void onRadiusChanged(int radius){
        XLog.logLine("On radius changed " + radius);
        imgData.radius = radius;
        if(imgSetter.hasSet()){
            if(imgSetter.fromAssets)
                blurDynamicSwitch.setOpened(true);
            if(radius == 0){
                blurDynamicSwitch.setOpened(false);
            }
            imgData.radius = radius;
            asyncRunSamplingBlur(this.imgData);
        }
    }

    private void onSamplingChanged(int sampling){
        XLog.logLine("On sampling changed " + sampling);
        imgData.sampling = sampling;
        if(imgSetter.hasSet()){
            if(imgSetter.fromAssets)
                blurDynamicSwitch.setOpened(true);
            asyncRunSamplingBlur(this.imgData);
        }
    }

    private void asyncRunSamplingBlur(ImageData imgData){
        ensureSamplingBlurTaskCanceled();
        samplingBlurAsyncTask = SamplingBlurAsyncTask.create(this, viewUpdater.image);
        samplingBlurAsyncTask.execute(imgData);
    }

    private void ensureSamplingBlurTaskCanceled(){
        if(samplingBlurAsyncTask != null && samplingBlurAsyncTask.isRunning())
            samplingBlurAsyncTask.cancel(true);
    }

    private void onImageChanged(String path){
        ensureSamplingBlurTaskCanceled();
        //viewUpdater.imageView.setImageAlpha(this.alpha);
        if(this.imgSetter.fromAssets && this.imgData.radius > 0){
            blurDynamicSwitch.setOpened(true);
        }
        viewUpdater.image.setBackgroundColor(Color.TRANSPARENT);
        asyncRunSamplingBlur(this.imgData);
    }

    private void onTextColorChanged(@ColorInt int textColor){
        opEventItem.textColor = textColor;
        viewUpdater.updateTextColor(textColor);
    }

    private void onBgColorChanged(@ColorInt int bgColor){
        System.out.println("BG color " + String.valueOf(bgColor));
        ensureSamplingBlurTaskCanceled();
        imgData.setColor(bgColor);
        viewUpdater.image.setImageResource(0);
        viewUpdater.image.setBackgroundColor(bgColor);
        imgSetter.clear();
    }

    private void onCallSetBackground(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EventManageActivity.this);
        dialogBuilder.setTitle(R.string.bg_from).setPositiveButton(R.string.file, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectImageBy(ImageFrom.FILE);
            }
        }).setNegativeButton(R.string.gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectImageBy(ImageFrom.GALLERY);
            }
        }).setNeutralButton(R.string.color, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectImageBy(ImageFrom.COLOR);
            }
        }).create().show();
    }

    private void updateTargetMillis(long targetMillis){
        XLog.logLine(opEventItem.getMillisToDate());
        opEventItem.setTargetMillis(targetMillis);
        viewUpdater.updateTargetMillisBothState(opEventItem);
        if(timeTextView != null){
            timeTextView.setText(opEventItem.getMillisToDate());
        }
    }

    private void selectImageBy(int mode){
        switch (mode){
            case ImageFrom.FILE :{
                Intent gIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                gIntent.putExtra("crop", "true");
                gIntent.putExtra("aspectX", 1);
                gIntent.setType("image/*");
                File tempFile = new File(tempPath);
                if (tempFile.exists()) tempFile.delete();
                gIntent.putExtra("output", Uri.fromFile(tempFile));
                gIntent.putExtra("outputFormat", "JPEG");
                Intent wrapperIntent = Intent.createChooser(gIntent, "Select background");
                startActivityForResult(wrapperIntent, RESULT_LOAD_IMAGE);
            }break;
            case ImageFrom.GALLERY :{
                Intent intent = new Intent();
                intent.setClass(EventManageActivity.this, GalleryActivity.class);
                startActivityForResult(intent, RESULT_LOAD_GALLERY);
            }break;
            default:{
                pickColor(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int i, @ColorInt int i1) {
                        onBgColorChanged(i1);
                    }
                    @Override
                    public void onDialogDismissed(int i) {
                    }
                });
            }break;
        }
    }

    /***
     * Show color picker dialog with special listener
     */
    private void pickColor(ColorPickerDialogListener colorPickerDialogListener){
        ColorPickerDialog dialog = ColorPickerDialog.newBuilder().create();
        dialog.setColorPickerDialogListener(colorPickerDialogListener);
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    /***
     * Show time picker and get time setting
     * ****/
    private void onCallTimePicker(){
        //Set now target millis
        long nowTargetMillis;
        if(opEventItem.targetMillis() > 0) nowTargetMillis = opEventItem.targetMillis();
        else nowTargetMillis = System.currentTimeMillis();
        //Init dialog
        View view = LayoutInflater.from(this).inflate(R.layout.time_picker, null);
        final TimeWheel timeWheel = new TimePickerWrap().setType(Type.ALL)
                .setMinMilliseconds(System.currentTimeMillis())
                .setCurrentMilliseconds(nowTargetMillis)
                .setCyclic(false).setHourText(getString(R.string.hr)).setMinuteText(getString(R.string.min))
                .wrap(view);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EventManageActivity.this);
        dialogBuilder.setView(view).setTitle(getString(R.string.set_time)).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateTargetMillis(TimePickerWrap.getMillisOf(timeWheel));
            }
        }).create().show();
    }

    private String generatePath(int id){
        //Generate new filename
        String folder = FileUtils.pictureFolderFromExt(EventManageActivity.this);
        String end = "x" + id;
        return EventItem.generateValidPath(folder,EventItem.IMG_NAME_PRE, end);
    }

    private void saveAsBlurFile(String originPath, String targetPath, int radius, int sampling, int quality){
        //Blur from origin file and save
        Bitmap bitmap = BitmapFactory.decodeFile(originPath);
        Bitmap blurBitmap = BitmapUtils.blurSampling(this, bitmap, radius, sampling);
        BitmapUtils.saveBitmapAsJPG(blurBitmap, quality, targetPath);
    }

    private void onCallValidateData(){
        String name = nameEditText.getText().toString();
        String celebration = celebrationEditText.getText().toString();
        if(TextUtils.isEmpty(name)){
            nameEditText.setError(getString(R.string.name) + getString(R.string.separator)+ getString(R.string.cant_empty));
            nameEditText.requestFocus();
        }else if(TextUtils.isEmpty(celebration)){
            celebrationEditText.setError(getString(R.string.celebration)+ getString(R.string.cant_empty));
            celebrationEditText.requestFocus();
        }else if(opEventItem.targetMillis() <= 0){
            setTimeBtn.setError(getString(R.string.please_set_time));
            setTimeBtn.requestFocus();
        }else{
            //BlurTransform with sampling of zero will case some problem
            if(imgData.radius > 0 && imgData.sampling < 1) imgData.sampling = 1;
            //Get image path
            //TODO
            if(imgSetter.hasSet()){
                String selectedPath = (String)imgSetter.newest();
                XLog.logLine("***** Has selected path of ---> " + selectedPath);
                if(imgSetter.isNewestInLocal()){
                    //Set background image path from gallery
                    if(imgSetter.fromAssets){
                        imgData.setAssets(selectedPath);
                    }else if(imgData.radius > 0 && !isBlurDynamic){
                        //Not use origin file
                        String savePath = generatePath(opEventItem.getID());
                        saveAsBlurFile(selectedPath, savePath, imgData.radius, imgData.sampling, 100);
                        imgData.unsetBlur();
                        imgData.setLocal(savePath, new File(savePath).getName());
                        XLog.logLine("Save from gallery as blur image ---> " + selectedPath);
                    }else{
                        imgData.setLocal(selectedPath, new File(selectedPath).getName());
                        XLog.logLine("Store from gallery ---> " + selectedPath);
                    }
                }else{
                    //Generate new file to sdcard for new image
                    String savePath = generatePath(opEventItem.getID());
                    File tempFile = new File(selectedPath);
                    File saveFile = new File(savePath);
                    if(imgData.radius <= 0 || isBlurDynamic){
                        if(tempFile.exists()){
                            tempFile.renameTo(saveFile);
                            XLog.logLine("Create new image ---> " + savePath);
                        }
                    }else{
                        saveAsBlurFile(selectedPath, savePath, imgData.radius, imgData.sampling, 100);
                        if(tempFile.exists()){
                            tempFile.delete();
                        }
                        imgData.unsetBlur();
                        XLog.logLine("Save as blur image ---> " + savePath);
                    }
                    imgData.setLocal(savePath, saveFile.getName());
                }
            }
            //Save to holder
            if(this.type == TYPE_ADD){
                EventItemManager.addItem(opEventItem);
            }else{
                if(EventItemManager.saveChangesTo(this.index, opEventItem)){
                    //Not used part
                    XLog.logLine("Save changes of id "+ opEventItem.getID() + ",index " + index);
                }
            }
            EventManageActivity.this.finish();
        }
    }

    /**
     * Current this method do:
     * Get result from Image selector
     * Hand background
     * **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            //Update to selected file
            File tempFile = new File(tempPath);
            File selectedTempFile = new File(selectedTempPath);
            if(selectedTempFile.exists())
                selectedTempFile.delete();
            if(tempFile.exists()){
                tempFile.renameTo(selectedTempFile);
            }else{
                //If get temp file fail
                //Copy from origin file
                Uri uri = data.getData();
                String path = FileUtils.pathFromUri(this, uri);
                if(path == null){
                    XLog.logLine("Get origin file path fail.");
                    return;
                }else{
                    File file = new File(path);
                    if(FileUtils.copyFile(file, selectedTempFile, true)){
                        XLog.logLine("Copy origin file");
                    }else{
                        XLog.logLine("Origin file not exist");
                        return;
                    }
                }
            }
            //Update background
            imgData.setLocal(selectedTempPath);
            imgSetter.setLocalNewGenerated(selectedTempPath);
            onImageChanged(selectedTempPath);
        }else if(requestCode == RESULT_LOAD_GALLERY && resultCode == GalleryActivity.CHOOSE_SUCCESS){
            String path = data.getStringExtra(GalleryActivity.SELECTED_IMAGE_PATH_KEY);
            int type = data.getIntExtra(GalleryActivity.PATH_TYPE_KEY, FileUtils.TYPE_LOCAL);
            if(type == FileUtils.TYPE_LOCAL){
                imgData.setLocal(path);
                imgSetter.setFromLocalExist(path);
                XLog.logLine("Set from local " + path);
            }else{
                imgData.setAssets(path);
                imgSetter.setFromAssets(path);
                XLog.logLine("Set from assets " + path);
            }
            onImageChanged(path);
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

    @Override
    public void onPause(){
        super.onPause();
        XLog.logLine("On Pause " + this.isFinishing());
        if(this.isFinishing()){
            File file = new File(tempPath);
            if(file.exists()) file.delete();
        }
    }

    static class ImageFrom{
        public static final int FILE = 0;
        public static final int GALLERY = 1;
        public static final int COLOR = 2;
    }

    class ImageSetter extends Condition<String, String>{
        private boolean fromAssets;

        public boolean isNewestInLocal(){
            return this.isFirstNewest();
        }

        public boolean isNewestFromAssets(){
            return this.fromAssets;
        }

        public void setFromLocalExist(String value){
            fromAssets = false;
            this.updateFirst(value);
        }

        public void setFromAssets(String value){
            fromAssets = true;
            this.updateFirst(value);
        }

        public void setLocalNewGenerated(String value){
            fromAssets = false;
            this.updateSecond(value);
        }

    }
}
