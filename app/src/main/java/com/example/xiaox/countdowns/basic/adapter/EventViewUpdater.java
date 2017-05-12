package com.example.xiaox.countdowns.basic.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.eventItem.EventItem;
import com.example.xiaox.countdowns.basic.image.BlurTransformation;
import com.example.xiaox.countdowns.basic.image.ImageData;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaox on 2/13/2017.
 */
public class EventViewUpdater {
    private List<TextView> textViews;
    public TextView daysLabel;
    public TextView hoursLabel;
    public TextView minutesLabel;
    public TextView secondsLabel;
    public TextView daysTextView;
    public TextView hoursTextView;
    public TextView minutesTextView;
    public TextView secondsTextView;
    public TextView nameTextView;
    public TextView targetTextView;
    public TextView celebrationTextView;
    public ImageView image;
    public View daytimeContainer;
    private Context context;
    private int state = -1;
    private int seconds = -1;
    private int minutes = -1;
    private int hours = -1;
    private int days = -1;
    private ImageSettings imgSettings;
    private int textColor = 0;
    private boolean useAnimation;

    private TextView idTestTextView;

    private EventViewUpdater(){
        this.imgSettings = new ImageSettings();
    }

    public EventViewUpdater(View view){
        this();
        this.context = view.getContext();
        this.initWith(view);
        initTextViews();
    }

    public EventViewUpdater(Activity activity){
        this();
        this.context = activity;
        this.initWith(activity);
        initTextViews();
    }

    public EventViewUpdater(Activity activity, boolean useAnimation){
        this(activity);
        this.useAnimation = useAnimation;
    }

    public EventViewUpdater(View view, boolean useAnimation){
        this(view);
        this.useAnimation = useAnimation;
    }

    private void initWith(Activity activity){
        this.daysLabel = (TextView)activity.findViewById(R.id.daysLabel);
        this.hoursLabel = (TextView)activity.findViewById(R.id.hoursLabel);
        this.minutesLabel = (TextView)activity.findViewById(R.id.minutesLabel);
        this.secondsLabel = (TextView)activity.findViewById(R.id.secondsLabel);
        this.secondsTextView = (TextView)activity.findViewById(R.id.secondsTextView);
        this.minutesTextView = (TextView)activity.findViewById(R.id.minutesTextView);
        this.hoursTextView = (TextView)activity.findViewById(R.id.hoursTextView);
        this.daysTextView = (TextView)activity.findViewById(R.id.daysTextView);
        this.nameTextView = (TextView)activity.findViewById(R.id.nameTextView);
        this.targetTextView = (TextView)activity.findViewById(R.id.targetTextView);
        this.image = (ImageView)activity.findViewById(R.id.imageView);
        this.celebrationTextView = (TextView)activity.findViewById(R.id.celebrationTextView);
        this.daytimeContainer = activity.findViewById(R.id.daytimeContainer);

        this.idTestTextView = (TextView)activity.findViewById(R.id.idTestTextView);
    }

    private void initWith(View view){
        this.daysLabel = (TextView)view.findViewById(R.id.daysLabel);
        this.hoursLabel = (TextView)view.findViewById(R.id.hoursLabel);
        this.minutesLabel = (TextView)view.findViewById(R.id.minutesLabel);
        this.secondsLabel = (TextView)view.findViewById(R.id.secondsLabel);
        this.secondsTextView = (TextView)view.findViewById(R.id.secondsTextView);
        this.minutesTextView = (TextView)view.findViewById(R.id.minutesTextView);
        this.hoursTextView = (TextView)view.findViewById(R.id.hoursTextView);
        this.daysTextView = (TextView)view.findViewById(R.id.daysTextView);
        this.nameTextView = (TextView)view.findViewById(R.id.nameTextView);
        this.targetTextView = (TextView)view.findViewById(R.id.targetTextView);
        this.image = (ImageView)view.findViewById(R.id.imageView);
        this.celebrationTextView = (TextView)view.findViewById(R.id.celebrationTextView);
        this.daytimeContainer = view.findViewById(R.id.daytimeContainer);

        this.idTestTextView = (TextView)view.findViewById(R.id.idTestTextView);
    }

    private void initTextViews(){
        textViews = new ArrayList<>();
        textViews.clear();
        tryAdd(this.secondsTextView);
        tryAdd(this.minutesTextView);
        tryAdd(this.hoursTextView);
        tryAdd(this.daysTextView);
        tryAdd(this.nameTextView);
        tryAdd(this.targetTextView);
        tryAdd(this.celebrationTextView);
        tryAdd(this.idTestTextView);
        //Label
        tryAdd(this.daysLabel);
        tryAdd(this.hoursLabel);
        tryAdd(this.minutesLabel);
        tryAdd(this.secondsLabel);
    }

    private boolean tryAdd(TextView textView){
        if(textView == null) return false;
        textViews.add(textView);
        return true;
    }

    //Recycling
    private String getPath(){
        return this.imgSettings.path();
    }

    //Basic
    public EventViewUpdater updateName(String name){
        if(this.nameTextView != null){
            this.nameTextView.setText(name);
        }
        return this;
    }

    public EventViewUpdater updateTargetMillisToDate(String millisDate){
        if(this.targetTextView != null){
            this.targetTextView.setText(millisDate);
        }
        return this;
    }

    public EventViewUpdater updateDay(int days){
        if(this.days != days && daysTextView != null){
            daysTextView.setText(String.valueOf(days));
            this.days = days;
        }
        return this;
    }

    public EventViewUpdater updateHours(int hours){
        if(this.hours != hours && hoursTextView != null){
            hoursTextView.setText(String.format("%02d", hours));
            this.hours = hours;
        }
        return this;
    }

    public EventViewUpdater updateMinutes(int minutes){
        if(this.minutes != minutes && minutesTextView != null){
            minutesTextView.setText(String.format("%02d", minutes));
            this.minutes = minutes;
        }
        return this;
    }

    public EventViewUpdater updateSecond(int seconds){
        if(this.seconds != seconds && secondsTextView != null){
            secondsTextView.setText(String.format("%02d", seconds));
            this.seconds = seconds;
        }
        return this;
    }

    public EventViewUpdater updateBackground(EventItem item, boolean verifyPath){
        if(this.image != null){
            ImageData imageData = item.imagedata;
            if(imageData.hasData()){
                if(!imageData.hasImage()){
                    if(!imgSettings.usedColor(imageData.color())){
                        imgSettings.useColor(imageData.color());
                        this.image.setImageResource(0);
                        this.image.setBackgroundColor(imgSettings.color());
                        XLog.logLine("ID(" + item.getID() + ") set bg by color " +imageData.color());
                    }
                }else if(context != null){
                    if(!verifyPath ||(verifyPath && !imgSettings.usedOption(item.imagedata))){
                        imgSettings.useOption(item.imagedata);
                        //Use glide to set blur ->
                        DrawableRequestBuilder<String> glideBuilder =  Glide.with(context)
                                .load(imgSettings.path())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop().placeholder(R.drawable.def_thumb);
                        if(imageData.radius > 0){
                            BlurTransformation blur = new BlurTransformation(context, imageData.radius, imageData.sampling);
                            glideBuilder = glideBuilder.bitmapTransform(blur);
                            XLog.logLine("ID(" + item.getID() + ") set blur bg by path " + imageData.path());
                        }
                        if(!useAnimation) glideBuilder = glideBuilder.dontAnimate();
                        glideBuilder.into(this.image);
                        XLog.logLine("ID(" + item.getID() + ") set bg by path "+ imageData.path());
                    }
                }
            }else if(!imgSettings.usedDefault()){
                //Not effect use glide
                //Glide.with(activity).load(R.drawable.def_thumb).into(this.imageView);
                imgSettings.useDefault();
                this.image.setImageResource(R.drawable.def_thumb);
                XLog.logLine("ID(" + item.getID() + ") set background by default");
            }else{
                XLog.logLine("ID(" + item.getID() + ") hasData: " + imageData.hasData()
                        + ", hasImage:" + imageData.hasImage());
            }
        }else{
        }
        return this;
    }

    public EventViewUpdater updateCelebration(String celebration){
        if(this.celebrationTextView != null){
            this.celebrationTextView.setText(celebration);
        }
        return this;
    }

    public EventViewUpdater updateState(EventItem item){
        if(this.state != item.state()){
            if(item.state() == EventItem.GONE){
                this.daytimeContainer.setVisibility(View.INVISIBLE);
                this.celebrationTextView.setVisibility(View.VISIBLE);
            }else {
                this.daytimeContainer.setVisibility(View.VISIBLE);
                this.celebrationTextView.setVisibility(View.INVISIBLE);
            }
            this.state = item.state();
        }
        return this;
    }

    public EventViewUpdater updateTextColor(int color){
        if(this.textColor != color){
            for(TextView textView : textViews){
                textView.setTextColor(color);
            }
            this.textColor = color;
        }
        return this;
    }

    private EventViewUpdater updateViewBy(EventItem item, boolean focusUpdate){
        updateState(item);
        updateCelebration(item.celebration);
        if(item.isUnComing()){
            updateTargetMillis(item);
        }else{
            updateTargetMillisToDate(item.getMillisToDate());
        }

        if(idTestTextView != null) idTestTextView.setText(String.valueOf(item.getID()));

        updateName(item.name);
        updateBackground(item, !focusUpdate);
        updateTextColor(item.textColor);
        XLog.logLine("updateHole of id " + item.getID());
        return this;
    }

    //Ext
    public EventViewUpdater updateTargetMillis(EventItem item){
        return updateTargetMillisToDate(item.getMillisToDate())
                .updateRemainMillis(item);
    }

    public EventViewUpdater updateRemainMillis(EventItem item){
        return updateDay(item.days)
                .updateHours(item.hours)
                .updateMinutes(item.minutes)
                .updateSecond(item.seconds);
    }

    public EventViewUpdater updateTargetMillisBothState(EventItem item){
        return this.updateTargetMillis(item)
                .updateState(item);
    }

    public EventViewUpdater updateBackground(EventItem item) {
        return this.updateBackground(item, true);
    }

    public EventViewUpdater updateTimeAndViewByType(int type, EventItem item){
        item.updateTime();
        return this.updateByType(type, item);
    }

    public EventViewUpdater updateViewBy(EventItem item){
        return updateViewBy(item, false);
    }

    public EventViewUpdater refreshHoleViewBy(EventItem item){
        return updateViewBy(item, true);
    }

    public EventViewUpdater updateByType(int type, EventItem item){
        switch (type){
            case EventItem.PARAM_ALL:{
                return refreshHoleViewBy(item);
            }
            case EventItem.PARAM_BACKGROUND:{
                return updateBackground(item, false);
            }
            case EventItem.PARAM_REMAIN_MILLIS:{
                return updateState(item)
                        .updateRemainMillis(item);
            }
            case EventItem.PARAM_NAME:{
                return updateName(item.name);
            }
            case EventItem.PARAM_CELEBRATION:{
                return updateCelebration(item.celebration);
            }
            case EventItem.PARAM_TEXT_COLOR:{
                return updateTextColor(item.textColor);
            }
            case EventItem.PARAM_TARGET_MILLIS:{
                return updateRemainMillis(item)
                        .updateTargetMillisToDate(item.getMillisToDate());
            }
            default: return this;
        }
    }

    class ImageSettings{
        //Background
        private int color;
        private boolean useDefault;
        private ImageData imagedata;

        public int color(){
            return this.color;
        }
        public String path(){
            return this.imagedata.path();
        }

        public void useColor(int color){
            this.color = color;
            this.imagedata = null;
            this.useDefault = false;
        }

        public void useOption(ImageData data){
            this.imagedata = data;
            this.color = 0;
            this.useDefault = false;
        }

        public void useDefault(){
            this.useDefault = true;
        }

        //Verify
        public boolean usedColor(int color){
            return this.color == color;
        }

        public boolean usedOption(ImageData data){
            if(this.imagedata == null) return false;
            return this.imagedata != data;
        }

        public boolean usedDefault(){
            return this.useDefault;
        }

    }
}
