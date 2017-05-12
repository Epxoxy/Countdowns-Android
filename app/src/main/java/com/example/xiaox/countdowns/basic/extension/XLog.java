package com.example.xiaox.countdowns.basic.extension;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import per.epxoxy.event.Event;
import per.epxoxy.event.EventArgs;
import per.epxoxy.event.EventWrapper;

/**
 * Created by xiaox on 2/17/2017.
 */
public class XLog {
    private static final XLog xLog = new XLog();
    public static boolean logEnable;
    private StringBuilder stringBuilder;
    private SimpleDateFormat formatter;
    private XLog(){
        stringBuilder = new StringBuilder();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    }

    private void onLogUpdated(Type type, String value){
        onLogUpdatedEvent.raiseEvent(this, new LogArgs(type, value));
    }

    private String getNowTime(){
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static void log(double value){
        if(logEnable){
            xLog.stringBuilder.append(value);
            xLog.onLogUpdated(Type.Add, String.valueOf(value));
        }
        System.out.print("XLog -> " +value);
    }

    public static void log(float value){
        if(logEnable) {
            xLog.stringBuilder.append(value);
            xLog.onLogUpdated(Type.Add, String.valueOf(value));
        }
        System.out.print("XLog -> " +value);
    }

    public static void log(int value){
        if(logEnable) {
            xLog.stringBuilder.append(value);
            xLog.onLogUpdated(Type.Add, String.valueOf(value));
        }
        System.out.print("XLog -> " + value);
    }

    public static void log(boolean value){
        if(logEnable) {
            xLog.stringBuilder.append(value);
            xLog.onLogUpdated(Type.Add, String.valueOf(value));
        }
        System.out.print("XLog -> " + value);
    }

    public static void log(String value){
        if(logEnable) {
            xLog.stringBuilder.append(value);
            xLog.onLogUpdated(Type.Add, value);
        }
        System.out.print("XLog -> " + value);
    }

    public static void logLine(double value){
        if(logEnable) {
            String _value = value + "\n";
            xLog.stringBuilder.insert(0,"--------" + xLog.getNowTime() + "--------\n" + _value);
            xLog.onLogUpdated(Type.Add, _value);
        }
        System.out.println("XLog -> " +value);
    }

    public static void logLine(float value){
        if(logEnable) {
            String _value = value + "\n";
            xLog.stringBuilder.insert(0,"--------" + xLog.getNowTime() + "--------\n" + _value);
            xLog.onLogUpdated(Type.Add, _value);
        }
        System.out.println("XLog -> " +value);
    }

    public static void logLine(int value){
        if(logEnable) {
            String _value = value + "\n";
            xLog.stringBuilder.insert(0,"--------" + xLog.getNowTime() + "--------\n" + _value);
            xLog.onLogUpdated(Type.Add, _value);
        }
        System.out.println("XLog -> " +value);
    }

    public static void logLine(boolean value){
        if(logEnable) {
            String _value = value + "\n";
            xLog.stringBuilder.insert(0, "--------" + xLog.getNowTime() + "--------\n" + _value);
            xLog.onLogUpdated(Type.Add, _value);
        }
        System.out.println("XLog -> " +value);
    }

    public static void logLine(String value){
        if(logEnable) {
            String _value = value + "\n";
            xLog.stringBuilder.insert(0,"--------" + xLog.getNowTime() + "--------\n" + _value);
            xLog.onLogUpdated(Type.Add, _value);
        }
        System.out.println("XLog -> " + value);
    }

    public static void clear(){
        xLog.stringBuilder = new StringBuilder();
        xLog.onLogUpdated(Type.Clear, "");
    }

    public static String logs(){
        return xLog.stringBuilder.toString();
    }

    public static XLog xLog(){
        return xLog;
    }

    public enum Type{
        Add,
        Clear
    }

    private Event<LogArgs> onLogUpdatedEvent = new Event<>();
    public EventWrapper<LogArgs> onLogUpdated = EventWrapper.wrap(onLogUpdatedEvent);

    public class LogArgs extends EventArgs{
        public final Type type;
        public final String value;
        public LogArgs(){
            this.type = null;
            this.value = null;
        }
        public LogArgs(Type type, String value){
            this.type = type;
            this.value = value;
        }
    }
}
