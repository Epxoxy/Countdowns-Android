package com.example.xiaox.countdowns.basic.datetime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xiaox on 2/17/2017.
 */
public class DateExt {
    private int year;
    private int month;
    private int day;
    public DateExt(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public boolean earlyThan(int month, int day){
        if(this.month == month)
            return this.day < day;
        return this.month < month;
    }

    public boolean lateThan(int month, int day){
        if(this.month == month)
            return this.day > day;
        return this.month > month;
    }

    @Override
    public String toString(){
        return this.year + "-" + this.month  + "-" + this.day;
    }

    public static String getDate(int year,int month,int weekOfMonth,int dayOfWeek){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        //Get the first day of current year-month
        c.set(year, month-1, 1);
        //Calculate what's the weekday of this day(Su,Mo,Tu,We,Th,Fr,Sa )
        //Sunday == 1
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        //Calculate count day
        int countDay = 0;
        if(weekday==1){
            countDay = (weekOfMonth-1)*7 + dayOfWeek+1;
        }
        else{
            countDay = (weekOfMonth-1)*7 + 7-weekday+1 + dayOfWeek+1;
        }
        c.set(Calendar.DAY_OF_MONTH, countDay);
        int sDay = c.get(Calendar.DAY_OF_MONTH);
        String dateStr = sdf.format(c.getTime());
        return dateStr;
    }

    public static int getDay(int year,int month,int weekOfMonth,int dayOfWeek){
        Calendar c = Calendar.getInstance();
        //Get the first day of current year-month
        c.set(year, month-1, 1);
        //Calculate what's the weekday of this day(Su,Mo,Tu,We,Th,Fr,Sa )
        //Sunday == 1
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        //Calculate count day
        int countDay = 0;
        if(weekday==1){
            countDay = (weekOfMonth-1)*7 + dayOfWeek+1;
        }
        else{
            countDay = (weekOfMonth-1)*7 + 7-weekday+1 + dayOfWeek+1;
        }
        c.set(Calendar.DAY_OF_MONTH, countDay);
        return c.get(Calendar.DAY_OF_MONTH);
    }
}
