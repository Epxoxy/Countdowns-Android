package com.example.xiaox.countdowns.basic.datetime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiaox on 2/13/2017.
 */
public class TimeFormat {
    public static String fromMillis(long millis){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(millis));
    }
}
