package com.example.xiaox.countdowns.basic.image;

import android.support.annotation.ColorInt;
import android.text.TextUtils;

import com.example.xiaox.countdowns.basic.extension.XLog;

import java.io.Serializable;

/**
 * Created by xiaox on 2/16/2017.
 */
public class ImageData implements Serializable {
    public boolean useTransparent;
    private String path;
    private String fileName;
    private int color;
    private boolean isPathSet;
    private boolean isColorSet;
    private boolean inAssets;
    public int sampling;
    public int radius;

    private ImageData(){
    }

    private ImageData(String path, boolean inAssets){
        if(inAssets){
            this.setAssets(path);
        }else{
            this.setAssets(path);
        }
    }

    private ImageData(String path, boolean inAssets, String fileName, int radius, int sampling){
        this(path, inAssets);
        this.fileName = fileName;
        this.radius = radius;
        this.sampling = sampling;
    }

    public static ImageData createEmpty(){
        return new ImageData();
    }

    public static ImageData fromAsset(String path){
        return new ImageData(path, true);
    }

    public static ImageData fromLocal(String path){
        return new ImageData(path, false);
    }

    public static ImageData fromAsset(String path, int radius){
        return new ImageData(path, true, null, radius, 0);
    }

    public static ImageData fromLocal(String path, int radius){
        return new ImageData(path, false, null, radius, 0);
    }

    public static ImageData fromAsset(String path, int radius, int sampling){
        return new ImageData(path, true, null, radius, sampling);
    }

    public static ImageData fromLocal(String path, String fileName, int radius, int sampling){
        return new ImageData(path, false, fileName, radius, sampling);
    }

    public static ImageData fromColor(@ColorInt int color){
        ImageData imagedata = new ImageData();
        imagedata.setColor(color);
        return imagedata;
    }

    public int color(){
        return this.color;
    }

    public String path(){
        return this.path;
    }

    public String fileName(){
        return this.fileName;
    }

    public void setAssets(String path){
        this.unsetColor();
        this.path = path;
        this.fileName = null;
        this.inAssets = true;
        this.isPathSet = true;
    }

    public void setLocal(String path, String fileName){
        this.fileName = fileName;
        this.setLocal(path);
    }

    public void setLocal(String path){
        this.unsetColor();
        this.isPathSet = true;
        this.inAssets = false;
        this.path = path;
    }

    public void setColor(@ColorInt int color){
        this.color = color;
        this.isColorSet = true;
        unsetFile();
    }

    public void setBlur(int radius, int sampling){
        this.radius = radius;
        this.sampling = sampling;
    }

    private void unsetFile(){
        this.inAssets = false;
        this.isPathSet = false;
        this.fileName = null;
        this.path = null;
        this.sampling = 0;
        this.radius = 0;
    }

    private void unsetColor(){
        this.color = 0;
        this.isColorSet = false;
    }

    public void unsetBlur(){
        this.radius = 0;
        this.sampling = 0;
    }

    public void unset(){
        this.unsetFile();
        this.unsetColor();
    }

    public void check(){
        this.isPathSet = null != path && !TextUtils.isEmpty(path);
    }

    public boolean hasImage(){
        return this.isPathSet;
    }

    public boolean hasData(){
        return this.isColorSet || this.isPathSet;
    }

    public boolean validPath(){
        return this.inAssets || (path != null && !TextUtils.isEmpty(path));
    }

    public boolean isInAssets(){
        return this.inAssets;
    }

    @Override
    public boolean equals(Object other){
        if(other == this) return true;
        ImageData data = (ImageData)other;
        if(data == null) return false;
        if(this.isColorSet) return this.color == data.color;
        if(this.inAssets){
            return data.inAssets && path.equals(data.path)
                    && this.radius == data.radius
                    && this.sampling == data.sampling;
        }else {
            return null == path ? null == data.path : path.equals(data.path);
        }
    }

    public ImageData copy(){
        ImageData imageData = new ImageData();
        imageData.path = this.path;
        imageData.fileName = this.fileName;
        imageData.color = this.color;
        imageData.radius = this.radius;
        imageData.sampling = this.sampling;
        imageData.isColorSet = this.isColorSet;
        imageData.isPathSet = this.isPathSet;
        imageData.inAssets = this.inAssets;
        return imageData;
    }

    public void syncTo(ImageData imageData){
        this.path = imageData.path;
        this.fileName = imageData.fileName;
        this.color = imageData.color;
        this.radius = imageData.radius;
        this.sampling = imageData.sampling;
        this.isColorSet = imageData.isColorSet;
        this.isPathSet = imageData.isPathSet;
        this.inAssets = imageData.inAssets;
    }
}
