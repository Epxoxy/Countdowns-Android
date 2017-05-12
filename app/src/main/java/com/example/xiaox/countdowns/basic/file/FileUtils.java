package com.example.xiaox.countdowns.basic.file;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.example.xiaox.countdowns.basic.extension.XLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * Created by xiaox on 2/11/2017.
 */
public class FileUtils {
    public static final String ASSET_PATH_SEGMENT = "android_asset";
    public static final String ASSET_PREFIX = ContentResolver.SCHEME_FILE + ":///" + ASSET_PATH_SEGMENT + "/";
    public static final int ASSET_PREFIX_LENGTH = ASSET_PREFIX.length();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int TYPE_LOCAL = 0;
    public static final int TYPE_ASSETS = 1;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermission(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    public static String createExternal(Activity activity, String fileName) {
        File fileExtra = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        if (!fileExtra.exists()) {
            try {
                fileExtra.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return fileExtra.getPath();
    }

    public static String createInternal(Activity activity, String fileName){
        File file = new File(activity.getApplicationContext().getFilesDir(), fileName);
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return file.getPath();
    }

    public static boolean createIfNotExist(String path){
        File file = new File(path);
        if(!file.exists()){
            try {
                file.createNewFile();
            }catch (IOException ex){
                ex.printStackTrace();
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean createIfNotExist(File file){
        if(!file.exists()){
            try {
                file.createNewFile();
            }catch (IOException ex){
                ex.printStackTrace();
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean tryCreate(File file){
        try {
            file.createNewFile();
        }catch (IOException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /*public static String getPathTimeInMillis(Activity activity, String extension){
        return getInternalPath(activity, Calendar.getInstance().getTimeInMillis() + extension);
    }*/

    public static String pathFromInternal(Activity activity){
        File file = new File(activity.getApplicationContext().getFilesDir(), "");
        return file.getPath();
    }

    public static String filesDirFromInternal(Activity activity){
        return activity.getApplicationContext().getFilesDir().getPath();
    }

    public static File fileFromInternal(Context context, String fileName){
        return new File(context.getApplicationContext().getFilesDir(), fileName);
    }

    public static String pathFromExtDoc(Context context, String fileName){
        File fileExtra = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        return fileExtra.getPath();
    }

    public static File dirFromExt(Context context, String type){
        return new File(context.getExternalFilesDir(type), "");
    }

    public static  String pathFromExt(Context context, String type, String fileName){
        File fileExtra = new File(context.getExternalFilesDir(type), fileName);
        return fileExtra.getPath();
    }

    public static String pathFromExtPicture(Context context, String fileName){
        return pathFromExt(context, Environment.DIRECTORY_PICTURES, fileName);
    }

    public static String pictureFolderFromExt(Context context){
        return pathFromExt(context, Environment.DIRECTORY_PICTURES, "");
    }

    public static void empty(String fileName){
        OutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(fileName);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            try{
                if(outputStream != null)
                    outputStream.close();

            }catch (Exception ex0){
                ex0.printStackTrace();
            }
        }
    }

    public static void tryDelete(String path){
        if(path == null) return;
        File file = new File(path);
        if(file.exists()) file.delete();
    }

    public static <T> T loadObject(String fileName){
        T t = null;
        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName));
            System.out.println(objectInputStream.available());
            Object objects = (Object)objectInputStream.readObject();
            objectInputStream.close();
            t = (T)objects;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return t;
    }

    public static <T> T[] loadObjects(String fileName){
        T[] ts = null;
        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName));
            System.out.println(objectInputStream.available());
            Object[] objects = (Object[])objectInputStream.readObject();
            objectInputStream.close();
            ts = (T[])objects;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return ts;
    }

    public static <T> void saveObjects(String fileName, T object){
        try{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static <T> void saveObjectsWithEmpty(String fileName, T object){
        empty(fileName);
        try{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static String pathFromUri(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String toAssetPath(String path){
        return path.substring(ASSET_PREFIX_LENGTH);
    }

    public static boolean isAssetUri(Uri uri) {
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme()) && !uri.getPathSegments().isEmpty()
                && ASSET_PATH_SEGMENT.equals(uri.getPathSegments().get(0));
    }

    public static boolean copyFile(File srcFile, File dtsFile, boolean rewrite){
        if(!srcFile.exists()) {
            XLog.logLine("Copy file fail from not exist.");
            return false;
        }
        if(!srcFile.isFile()) {
            XLog.logLine("Copy file fail from wrong file.");
            return false;
        }
        if(!srcFile.canRead()) {
            XLog.logLine("Could not Copy file from unRead file.");
            return false;
        }
        if(!dtsFile.getParentFile().exists()){
            dtsFile.getParentFile().mkdir();
        }
        if(dtsFile.exists() && rewrite)
            dtsFile.delete();
        try{
            InputStream inStream = new FileInputStream(srcFile);
            FileOutputStream outStream = new FileOutputStream(dtsFile);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = inStream.read(buffer)) != -1){
                outStream.write(buffer, 0, byteRead);
            }
            inStream.close();
            outStream.close();
            XLog.logLine("Copy file from " + srcFile.getPath());
        }catch (Exception ex){
            XLog.logLine("Copy file fail " + ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean copyFile(String srcPath, String dtsPath, boolean rewrite){
        File srcFile = new File(srcPath);
        File dtsFile = new File(dtsPath);
        return copyFile(srcFile, dtsFile, rewrite);
    }
}
