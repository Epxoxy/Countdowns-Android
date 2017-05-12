package com.example.xiaox.countdowns.basic.eventItem;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;

import com.bumptech.glide.Glide;
import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.datetime.DateExt;
import com.example.xiaox.countdowns.basic.file.FileUtils;
import com.example.xiaox.countdowns.basic.image.ImageData;
import com.example.xiaox.countdowns.basic.datetime.LunarUtils;
import com.example.xiaox.countdowns.basic.extension.NRandom;
import com.example.xiaox.countdowns.basic.extension.XLog;
import com.example.xiaox.countdowns.receivers.AlarmReceiver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import per.epxoxy.event.Event;
import per.epxoxy.event.EventArgs;
import per.epxoxy.event.EventWrapper;
import per.epxoxy.event.WeakEvent;

/**
 * Created by xiaox on 2/11/2017.
 */
public class EventItemManager {
    public static final String storedFileName = "eventitems.dat";
    public static final List<EventItem> ITEMS = new ArrayList<>();
    public static boolean unComingFirst = true;
    private static boolean isLoaded = false;
    private static String dataStoredPath;
    private static String picturesDir;

    public static List<EventItem> createNormal(Context context){
        //Get current date
        Calendar calendar = Calendar.getInstance();
        int sYear = calendar.get(Calendar.YEAR);
        int sMonth = calendar.get(Calendar.MONTH) + 1;
        int sDay = calendar.get(Calendar.DAY_OF_MONTH);
        int nextSYear = sYear + 1;
        //Ger current date of lunar
        LunarUtils lunar = new LunarUtils();
        int[] lunarDate = lunar.solarToLunar(sYear, sMonth, sDay);
        int lYear = lunarDate[0];
        int lMonth = lunarDate[1];
        int lDay = lunarDate[2];
        int nextLYear = lYear + 1;
        DateExt sNow = new DateExt(sYear, sMonth, sDay);
        DateExt lNow = new DateExt(lYear, lMonth, lDay);
        XLog.logLine("Solar date now is " + sNow.toString());
        XLog.logLine("Lunar date now is " + lNow.toString());
        //Array
        Resources res = context.getResources();
        String[] festNames = res.getStringArray(R.array.festivals_array);
        String[] celebrations = res.getStringArray(R.array.celebration_array);
        String[] festDates = new String[festNames.length];
        //Generate basic lunar festival data
        festDates[res.getInteger(R.integer.linsDay)] = lunar.getTranslateSolarString(lNow.lateThan(3,25) ? nextLYear : lYear, 3, 25);
        festDates[res.getInteger(R.integer.spring)] = lunar.getTranslateSolarString(lNow.lateThan(1,1) ? nextLYear : lYear, 1, 1);
        festDates[res.getInteger(R.integer.lantern)] = lunar.getTranslateSolarString(lNow.lateThan(1,15)? nextLYear : lYear, 1, 15);
        festDates[res.getInteger(R.integer.dragon)] = lunar.getTranslateSolarString(lNow.lateThan(5,5) ? nextLYear : lYear, 5, 5);
        festDates[res.getInteger(R.integer.moon)] = lunar.getTranslateSolarString(lNow.lateThan(8,15) ? nextLYear : lYear, 8, 15);
        festDates[res.getInteger(R.integer.doubleNinth)] = lunar.getTranslateSolarString(lNow.lateThan(9,9) ? nextLYear : lYear, 9, 9);
        int month12Days = lunar.daysInLunarMonth(lYear, 12);//NewYearsEve
        if(month12Days > 0){
            festDates[res.getInteger(R.integer.newYearEve)] = lunar.getTranslateSolarString(lYear, 12, month12Days);
        }
        //Generate basic solar festival data
        festDates[res.getInteger(R.integer.newYearsDay)]= (sNow.lateThan(1,1)? nextSYear : sYear) + "-01-01";
        festDates[res.getInteger(R.integer.foolsDay)] = (sNow.lateThan(4,1) ? nextSYear : sYear) + "-04-01";
        festDates[res.getInteger(R.integer.mayDay)] = (sNow.lateThan(5,1)? nextSYear : sYear) + "-05-01";
        festDates[res.getInteger(R.integer.childrensDay)] = (sNow.lateThan(6,1) ? nextSYear : sYear) + "-06-01";
        festDates[res.getInteger(R.integer.teachersDay)] = (sNow.lateThan(9,10)? nextSYear : sYear) + "-09-10";
        festDates[res.getInteger(R.integer.nationalDay)] = (sNow.lateThan(10,1) ? nextSYear : sYear) + "-10-01";
        //Get father's day and mother's day
        int moDay = DateExt.getDay(sYear, 5, 2, 0);
        int faDay = DateExt.getDay(sYear, 6, 3, 0);
        String moDayString = moDay > 9 ? String.valueOf(moDay) : "0" + moDay;
        festDates[res.getInteger(R.integer.mothersDay)] = (sNow.lateThan(5, moDay)? nextSYear : sYear) + "-05-" + moDayString;
        festDates[res.getInteger(R.integer.fathersDay)] = (sNow.lateThan(6, faDay)? nextSYear : sYear) + "-06-" + faDay;
        //Init default pictures
        ImageData[] imagesData = new ImageData[]{
                ImageData.fromAsset("file:///android_asset/pictures/47621790_p0_master1200.jpg", 25, 10),
                ImageData.fromAsset("file:///android_asset/pictures/picfromepx.jpg", 25, 10),
                ImageData.fromAsset("file:///android_asset/pictures/56207638_p0.jpg", 25, 10),
                ImageData.fromAsset("file:///android_asset/pictures/48444823_p0.jpg"),
                ImageData.fromAsset("file:///android_asset/pictures/45223349_p0.jpg"),
                ImageData.fromAsset("file:///android_asset/pictures/45223349_p0.jpg", 25, 10),
                ImageData.fromAsset("file:///android_asset/pictures/35019721_p0_master1200.jpg", 25, 10),
                ImageData.fromAsset("file:///android_asset/pictures/29601755_p0_master1200.jpg", 25, 10),
                ImageData.fromAsset("file:///android_asset/pictures/8913281_p0_master1200.jpg", 25, 10),
                ImageData.fromAsset("file:///android_asset/pictures/pixiv59038033_1.jpg", 25, 10),
                ImageData.fromAsset("file:///android_asset/pictures/6388533920140512204703091_640.jpg", 25, 10),
                ImageData.fromAsset("file:///android_asset/pictures/6388533920140512204651069_640.jpg"),
                ImageData.fromAsset("file:///android_asset/pictures/6388533920140512204615079_640.jpg"),
                ImageData.fromColor((-1) * (8336444)),
                ImageData.fromColor((-1) * (9655698)),
                ImageData.fromColor((-1) * (8744034)),
                ImageData.fromColor((-1) * (6381922))
        };
        int[] imgIndexes = NRandom.getSequence(imagesData.length);
        int[] idIndexes = NRandom.getSequence(festDates.length);
        //Init event items
        List<EventItem> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(int i = 0; i < festDates.length; i++){
            String value = festDates[i];
            if(value == null) continue;
            long time = 0l;
            try{
                time = sdf.parse(value).getTime();
            }catch (java.text.ParseException ex){
                ex.printStackTrace();
            }
            if(time != 0){
                ImageData imgData = null;
                if(i == 0){
                    imgData = ImageData.fromAsset("file:///android_asset/pictures/6388533920140512204703091_640.jpg");
                }else{
                    imgData = imagesData[imgIndexes[i]];
                }
                list.add(EventItem.from(
                        idIndexes[i],
                        festNames[i],
                        time,
                        celebrations[i],
                        imgData));
            }
        }
        return list;
    }

    public static void load(){
        if(isLoaded) return;
        List<EventItem> eventItemList = FileUtils.loadObject(dataStoredPath);
        loadItems(eventItemList);
    }

    private static void loadItems(List<EventItem> eventItemList){
        if(eventItemList != null){
            ITEMS.clear();
            Collections.sort(eventItemList, new SortByTargetMillis());
            for(int i = 0; i < eventItemList.size(); i++){
                EventItem eventItem = eventItemList.get(i);
                if(eventItem != null){
                    if(!eventItem.imagedata.isInAssets())
                        fitFullPath(eventItem);
                    eventItem.updateTime();
                    ITEMS.add(eventItem);
                    XLog.logLine("Load item <" + eventItem.getMillisToDate() + ", " + eventItem.targetMillis() + ">");
                }
            }
            isLoaded = true;
        }
    }

    public static void save(){
        FileUtils.saveObjectsWithEmpty(dataStoredPath, ITEMS);
    }

    public static EventItem get(int index){
        if(index + 1 > ITEMS.size()) return null;
        return ITEMS.get(index);
    }

    public static int generateID(){
        if(ITEMS.size() <= 0) return 0;
        if(ITEMS.size() == 1) return ITEMS.get(0).getID() + 1;
        int[] allID = new int[ITEMS.size()];
        for (int i = 0; i < ITEMS.size(); i++){
            allID[i] = ITEMS.get(i).getID();
        }
        for (int i = 0; i < allID.length - 1; i++){
            for (int j = i + 1; j < allID.length; j++){
                if(allID[i] > allID[j]){
                    int tmp = allID[i];
                    allID[i] = allID[j];
                    allID[j] = tmp;
                }
            }
        }
        int preID = allID[0];
        for(int i = 1; i < allID.length; i++){
            if(allID[i] - preID > 1){
                return preID + 1;
            }
            preID = allID[i];
        }
        return preID + 1;
    }

    public static void addItem(EventItem item){
        int i = 0;
        SortByTargetMillis sort = new SortByTargetMillis();
        for(; i < ITEMS.size(); i++){
            if(sort.compare(item, ITEMS.get(i)) < 0){
                XLog.logLine("Add in index of " + i);
                break;
            }
        }
        ITEMS.add(i, item);
        save();
        if(eventItemManager != null){
            eventItemManager.onEventItemUpdatedEvent.raiseEvent(null, null);
        }
    }

    public static void addItems(EventItem[] eventItems){
        for(int i = 0; i < eventItems.length; i++){
            ITEMS.add(eventItems[i]);
        }
        save();
        Collections.sort(ITEMS, new SortByTargetMillis());
        if(eventItemManager != null){
            eventItemManager.onEventItemUpdatedEvent.raiseEvent(null, null);
        }
    }

    public static boolean saveChangesTo(int srcIndex, EventItem syncItem){
        if(srcIndex > ITEMS.size()) return false;
        EventItem editItem = ITEMS.get(srcIndex);
        ArrayList<Integer> syncList = editItem.syncTo(syncItem);
        if(syncList.isEmpty()) return false;
        EventItemManager.ITEMS.set(srcIndex, editItem);
        XLog.logLine("Save changed "+ syncList.size());
        save();
        if(eventItemManager != null) {
            eventItemManager.onItemPropertyUpdatedEvent.raiseEvent(
                    eventItemManager, new ItemPropertyChangedArgs(srcIndex, syncList));
        }
        return true;
    }

    public static void removeItem(int location){
        removeItem(ITEMS.get(location));
    }

    public static void removeItem(EventItem item){
        ITEMS.remove(item);
        save();
        if(eventItemManager != null)
            eventItemManager.onEventItemUpdatedEvent.raiseEvent(null, null);
    }

    public static void removeItems(EventItem[] eventItems){
        for(int i = 0; i < eventItems.length; i++){
            ITEMS.remove(eventItems[i]);
        }
        save();
        if(eventItemManager != null)
            eventItemManager.onEventItemUpdatedEvent.raiseEvent(null, null);
    }

    public static void clear(){
        ITEMS.clear();
        save();
        if(eventItemManager != null)
            eventItemManager.onEventItemUpdatedEvent.raiseEvent(null, null);
    }

    public static void deleteStored(){
        File file = new File(dataStoredPath);
        if(file.exists()) file.delete();
        if(isLoaded){
            ITEMS.clear();
            isLoaded = false;
            if(eventItemManager != null){
                eventItemManager.onEventItemUpdatedEvent.raiseEvent(eventItemManager, EventArgs.empty);
                XLog.logLine("------Raise OnItemUpdated -----" + eventItemManager.onEventItemUpdatedEvent.sizeOfHandlers());
            }
        }
    }

    public static void rewriteOrigin(Context context){
        isLoaded = false;
        loadItems(createNormal(context));
        save();
        if(eventItemManager != null){
            eventItemManager.onEventItemUpdatedEvent.raiseEvent(null, null);
            XLog.logLine("--------- Raise OnItemUpdated --------" + eventItemManager.onEventItemUpdatedEvent.sizeOfHandlers());
        }
    }

    private static void fitFullPath(EventItem item){
        String imgFileName = item.imagedata.fileName();
        if(imgFileName == null) return;
        if(imgFileName != null && !TextUtils.isEmpty(imgFileName)){
            item.imagedata.setLocal(picturesDir + File.separator + imgFileName);
        }
    }

    public static void clearDiskCache(final Context context){
        try{
            Thread thread = new Thread(){
                @Override
                public void run() {
                    Glide.get(context).clearDiskCache();
                }
            };thread.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static List<Integer> updateTimeAll(){
        List<Integer> updatedIndexes = new ArrayList<>();
        int unComingCount = 0;
        for(int i = 0; i < ITEMS.size(); i++){
            if(ITEMS.get(i).updateTime()){
                ++unComingCount;
                updatedIndexes.add(i);
            }
        }
        XLog.logLine("**** update time of " + unComingCount+ " item *****");
        return updatedIndexes;
    }

    private static final String FRAGMENT_TAG = "com.example.xiaox.countdowns.manager";
    private static EventItemManager eventItemManager;
    private static Activity hostActivity;
    private static void ensureLoaded(Activity activity){
        if(!EventItemManager.isLoaded){
            FileUtils.verifyStoragePermission(activity);
            //String externalFN = IOExtension.createExternal(activity, storedFileName);
            File storeFile = FileUtils.fileFromInternal(activity, storedFileName);
            File picDir = FileUtils.dirFromExt(activity, Environment.DIRECTORY_PICTURES);
            EventItemManager.dataStoredPath = storeFile.getPath();
            EventItemManager.picturesDir = picDir.getPath();
            FileUtils.createIfNotExist(picDir);
            XLog.logLine(storeFile.getPath());
            if(!storeFile.exists()){
                XLog.logLine("Store File not exist!");
                FileUtils.tryCreate(storeFile);
                loadItems(createNormal(activity));
                save();
            }else{
                EventItemManager.load();
            }
        }
    }

    public static EventItemManager with(Activity activity){
        ensureLoaded(activity);
        if(!activity.isDestroyed()){
            if(eventItemManager == null){
                FragmentManager fm = activity.getFragmentManager();
                LifeFragment lifeFrag = (LifeFragment) fm.findFragmentByTag(FRAGMENT_TAG);
                if(lifeFrag == null){
                    lifeFrag = new LifeFragment();
                    fm.beginTransaction().add(lifeFrag, FRAGMENT_TAG).commitAllowingStateLoss();
                }
                eventItemManager = new EventItemManager();
                hostActivity = activity;
            }else if(hostActivity != activity){
                if(hostActivity != null){
                    FragmentManager fm = hostActivity.getFragmentManager();
                    LifeFragment lifeFrag = (LifeFragment) fm.findFragmentByTag(FRAGMENT_TAG);
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.remove(lifeFrag);
                }
                FragmentManager fm = activity.getFragmentManager();
                LifeFragment lifeFrag = new LifeFragment();
                fm.beginTransaction().add(lifeFrag, FRAGMENT_TAG).commitAllowingStateLoss();
                eventItemManager = new EventItemManager();
                hostActivity = activity;
            }
        }
        return eventItemManager;
    }

    private WeakEvent<EventArgs> onEventItemUpdatedEvent = new WeakEvent<>();
    private WeakEvent<ItemPropertyChangedArgs> onItemPropertyUpdatedEvent = new WeakEvent<>();
    public EventWrapper<EventArgs> onEventItemUpdated = EventWrapper.wrap(onEventItemUpdatedEvent);
    public EventWrapper<ItemPropertyChangedArgs> onItemPropertyUpdated = EventWrapper.wrap(onItemPropertyUpdatedEvent);

    private EventItemManager(){
        XLog.logLine("Create EventItemManager");
    }

    private void clearHandlers(){
        this.onEventItemUpdated.clearHandlers();
        XLog.logLine("--------- Clear handlers --------");
    }

    public static class LifeFragment extends Fragment{

        public LifeFragment(){
            super();
        }

        @Override
        public void onAttach(Context context){
            super.onAttach(context);
            XLog.logLine("LiftFragment onAttach");
        }

        @Override
        public void onPause(){
            super.onPause();
        }

        @Override
        public void onResume(){
            super.onResume();
        }

        @Override
        public void onDetach(){
            super.onDetach();
            if(eventItemManager != null){
                eventItemManager.clearHandlers();
                eventItemManager = null;
            }
            XLog.logLine("LiftFragment onDetach");
        }

        @Override
        public void onDestroy(){
            super.onDestroy();
            if(eventItemManager != null){
                eventItemManager.clearHandlers();
                eventItemManager = null;
            }
            XLog.logLine("LiftFragment onDestroy");
        }
    }

    public static class SortByTargetMillis implements Comparator<EventItem>{
        @Override
        public int compare(EventItem lhs, EventItem rhs) {
            return lhs.targetMillis() > rhs.targetMillis() ? 1 :
                    lhs.targetMillis() == rhs.targetMillis() ? 0 : -1;
        }
    }


/*
    private static final String ALARMINTENT = "com.example.xiaox.alarmintent";
    private static final String ID = "";
    private static final String TIME = "alarm_time";
    public static void AddAlarm(Context context, EventItem item){
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ALARMINTENT);
        intent.setData(Uri.parse(""));
        intent.setClass(context, AlarmReceiver.class);
        long millis = item.targetMillis();
        intent.putExtra(TIME, millis);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, millis, sender);

    }

    public static void CancelAlarm(Context context, EventItem item){
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ALARMINTENT);
        intent.setClass(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        if(sender != null){
            am.cancel(sender);
            XLog.logLine("Cancel alarm of " + item.getID());
        }else {
            XLog.logLine("Cancel alarm fail, sender is null.");
        }
    }
    */
}
