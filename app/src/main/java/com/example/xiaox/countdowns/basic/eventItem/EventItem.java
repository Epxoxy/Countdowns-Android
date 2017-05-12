package com.example.xiaox.countdowns.basic.eventItem;

import android.graphics.Color;

import com.example.xiaox.countdowns.basic.image.ImageData;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xiaox on 2/11/2017.
 */
public class EventItem extends Item implements Serializable {
    //Static member
    public static final long serialVersionUID = 7085785138908660986L;
    public static final int PARAM_ALL = 0;
    public static final int PARAM_BACKGROUND = 1;
    public static final int PARAM_REMAIN_MILLIS = 2;
    public static final int PARAM_NAME = 3;
    public static final int PARAM_CELEBRATION = 4;
    public static final int PARAM_TEXT_COLOR = 5;
    public static final int PARAM_TARGET_MILLIS = 6;
    public static final int PARAM_ID = 7;
    public static final int PARAM_IMAGE_NAME = 8;
    public static final String IMG_NAME_PRE = "bgitem";
    public static final String IMG_NAME_EXT = ".jpg";
    public static final int UN_COMING = 0;
    public static final int GONE = 1;
    //Private member
    private long targetMillis = 0l;
    private String tMillisToDate;
    public int textColor = Color.WHITE;
    public final ImageData imagedata;
    //Public member
    public transient long remainTime = 0;
    public transient int days = 0;
    public transient int hours = 0;
    public transient int minutes = 0;
    public transient int seconds = 0;
    private transient int state = UN_COMING;

    //Static
    public static EventItem createNegative(){
        EventItem eventItem = new EventItem();
        eventItem.setID(-1);
        eventItem.targetMillis = -1;
        eventItem.remainTime = -1;
        eventItem.state = -1;
        eventItem.days = -1;
        eventItem.hours = -1;
        eventItem.minutes = -1;
        eventItem.seconds = -1;
        return eventItem;
    }

    public static EventItem create(int id, String name, long targetTime){
        return new EventItem(id, name, targetTime);
    }

    public static EventItem from(int id, String name, long targetMillis, String celebration, ImageData imagedata){
        return new EventItem(id, name, targetMillis, celebration, imagedata);
    }

    //Constructor
    public EventItem(){
        imagedata = ImageData.createEmpty();
    }

    public EventItem(int id, String name, long targetMillis){
        this();
        this.name = name;
        this.setID(id);
        this.setTargetMillis(targetMillis);
    }

    public EventItem(int id, String name, long targetMillis, String celebration){
        this(id, name, targetMillis);
        this.celebration = celebration;
    }

    public EventItem(int id, String name, long targetMillis, String celebration, ImageData imagedata){
        this.name = name;
        this.setID(id);
        this.setTargetMillis(targetMillis);
        this.celebration = celebration;
        this.imagedata = imagedata;
    }

    //Basic
    public int state(){
        return this.state;
    }

    public long targetMillis(){
        return this.targetMillis;
    }

    public void setTargetMillis(long targetMillis){
        this.targetMillis = targetMillis;
        remainTime = targetMillis - System.currentTimeMillis();
        if(targetMillis > 0){
            tMillisToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(targetMillis));
        }
        updateInternal(true);
    }

    public String getMillisToDate(){
        return this.tMillisToDate;
    }

    private boolean updateInternal(boolean focusUpdate){
        if(this.state == GONE && !focusUpdate) return false;
        remainTime = targetMillis - System.currentTimeMillis();
        if(remainTime > 0){
            this.state = UN_COMING;
            long[] data = calculateTime(remainTime);
            this.days = (int)data[0];
            this.hours = (int)data[1];
            this.minutes = (int)data[2];
            this.seconds = (int)data[3];
        }else{
            this.state = GONE;
            this.days = 0;
            this.hours = 0;
            this.minutes = 0;
            this.seconds = 0;
        }
        return true;
    }

    public boolean updateTime(){
        return this.updateInternal(false);
    }

    //Verify
    public boolean isUnComing(){
        return this.state == UN_COMING;
    }

    //Other
    public EventItem copy(){
        EventItem eventItem = new EventItem();
        eventItem.setID(this.getID());
        eventItem.name = this.name;
        eventItem.setTargetMillis(this.targetMillis);
        eventItem.imagedata.syncTo(this.imagedata);
        eventItem.celebration = this.celebration;
        eventItem.textColor = this.textColor;
        return eventItem;
    }

    private boolean notEqualOrNullJustOld(String old, String newer){
        if(old == null && newer == null) return false;
        if(old == null) return true;
        if(newer == null) return true;
        return !old.equals(newer);
    }

    /*
    * Sync in need property to target
    * <in>Sync target</in>
    * <return>Sync property count</return>
    * */
    public ArrayList<Integer> syncTo(EventItem newerItem){
        ArrayList<Integer> syncProperties = new ArrayList<>();
        if(newerItem != null){
            if(this.getID() != newerItem.getID()){
                this.setID(newerItem.getID());
                syncProperties.add(PARAM_ID);
            }
            if(notEqualOrNullJustOld(this.name, newerItem.name)){
                this.name = newerItem.name;
                syncProperties.add(PARAM_NAME);
            }
            if(this.targetMillis != newerItem.targetMillis){
                this.setTargetMillis(newerItem.targetMillis);
                syncProperties.add(PARAM_TARGET_MILLIS);
            }
            if(this.textColor != newerItem.textColor){
                this.textColor = newerItem.textColor;
                syncProperties.add(PARAM_TEXT_COLOR);
            }
            ImageData newImgData = newerItem.imagedata;
            ImageData myImgData = this.imagedata;
            if(newImgData.hasData()){
                if(newImgData.hasImage()){
                    if(notEqualOrNullJustOld(myImgData.path(),newImgData.path())){
                        if(newImgData.isInAssets())
                            myImgData.setAssets(newImgData.path());
                        else
                            myImgData.setLocal(newImgData.path(), newImgData.fileName());
                        syncProperties.add(PARAM_BACKGROUND);
                    }
                    XLog.logLine("Check Blur " + newImgData.radius + "," + newImgData.sampling);
                    if(myImgData.radius != newImgData.radius
                            || myImgData.sampling != newImgData.sampling){
                        if(newImgData.radius < 0){
                            myImgData.unsetBlur();
                            XLog.logLine("Unset blur");
                        }else{
                            myImgData.setBlur(newImgData.radius, newImgData.sampling);
                            XLog.logLine("Sync Blur");
                        }
                        syncProperties.add(PARAM_BACKGROUND);
                    }
                }else{
                    if(myImgData.hasImage()) myImgData.unset();
                    myImgData.setColor(newImgData.color());
                    syncProperties.add(PARAM_BACKGROUND);
                }
            }else{
                myImgData.unset();
                syncProperties.add(PARAM_BACKGROUND);
            }
            if(notEqualOrNullJustOld(this.celebration,newerItem.celebration)){
                this.celebration = newerItem.celebration;
                syncProperties.add(PARAM_CELEBRATION);
            }
        }
        return syncProperties;
    }

    public static long[] calculateTime(long _milliseconds){
        long totalTime = _milliseconds;
        long seconds = _milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long milliseconds = (int)(_milliseconds % 1000);
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;
        return new long[]{ days, hours, minutes, seconds, milliseconds};
    }

    public static String generateValidPath(String folder, String head, String end){
        String filePath = folder + File.separator + head + end;
        File file = null;
        while ((file = new File(filePath + IMG_NAME_EXT)).exists()){
            filePath += end;
        }
        return filePath + IMG_NAME_EXT;
    }

}
