package com.example.xiaox.countdowns.views;

import android.view.View;

import com.jzxiang.pickerview.TimeWheel;
import com.jzxiang.pickerview.config.PickerConfig;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.data.WheelCalendar;

import java.util.Calendar;

/**
 * Created by xiaox on 2/13/2017.
 */
public class TimePickerWrap {
    PickerConfig mPickerConfig;

    public TimePickerWrap() {
        mPickerConfig = new PickerConfig();
        mPickerConfig.mYear = "";
        mPickerConfig.mMonth = "";
        mPickerConfig.mDay = "";
        mPickerConfig.mHour = "";
        mPickerConfig.mMinute = "";
    }

    public TimePickerWrap setType(Type type) {
        mPickerConfig.mType = type;
        return this;
    }

    public TimePickerWrap setThemeColor(int color) {
        mPickerConfig.mThemeColor = color;
        return this;
    }

    public TimePickerWrap setCancelStringId(String left) {
        mPickerConfig.mCancelString = left;
        return this;
    }

    public TimePickerWrap setSureStringId(String right) {
        mPickerConfig.mSureString = right;
        return this;
    }

    public TimePickerWrap setTitleStringId(String title) {
        mPickerConfig.mTitleString = title;
        return this;
    }

    public TimePickerWrap setToolBarTextColor(int color) {
        mPickerConfig.mToolBarTVColor = color;
        return this;
    }

    public TimePickerWrap setWheelItemTextNormalColor(int color) {
        mPickerConfig.mWheelTVNormalColor = color;
        return this;
    }

    public TimePickerWrap setWheelItemTextSelectorColor(int color) {
        mPickerConfig.mWheelTVSelectorColor = color;
        return this;
    }

    public TimePickerWrap setWheelItemTextSize(int size) {
        mPickerConfig.mWheelTVSize = size;
        return this;
    }

    public TimePickerWrap setCyclic(boolean cyclic) {
        mPickerConfig.cyclic = cyclic;
        return this;
    }

    public TimePickerWrap setMinMilliseconds(long millseconds) {
        mPickerConfig.mMinCalendar = new WheelCalendar(millseconds);
        return this;
    }

    public TimePickerWrap setMaxMilliseconds(long millseconds) {
        mPickerConfig.mMaxCalendar = new WheelCalendar(millseconds);
        return this;
    }

    public TimePickerWrap setCurrentMilliseconds(long millseconds) {
        mPickerConfig.mCurrentCalendar = new WheelCalendar(millseconds);
        return this;
    }

    public TimePickerWrap setYearText(String year){
        mPickerConfig.mYear = year;
        return this;
    }

    public TimePickerWrap setMonthText(String month){
        mPickerConfig.mMonth = month;
        return this;
    }

    public TimePickerWrap setDayText(String day){
        mPickerConfig.mDay = day;
        return this;
    }

    public TimePickerWrap setHourText(String hour){
        mPickerConfig.mHour = hour;
        return this;
    }

    public TimePickerWrap setMinuteText(String minute){
        mPickerConfig.mMinute = minute;
        return this;
    }

    public static long getMillisOf(TimeWheel timeWheel){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, timeWheel.getCurrentYear());
        calendar.set(Calendar.MONTH, timeWheel.getCurrentMonth() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, timeWheel.getCurrentDay());
        calendar.set(Calendar.HOUR_OF_DAY, timeWheel.getCurrentHour());
        calendar.set(Calendar.MINUTE, timeWheel.getCurrentMinute());
        return calendar.getTimeInMillis();
    }

    public TimeWheel wrap(View view){
        TimeWheel timeWheel = new TimeWheel(view, mPickerConfig);
        return timeWheel;
    }

}
