package com.example.xiaox.countdowns.basic.extension;

import com.example.xiaox.countdowns.basic.image.ImageData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaox on 2/19/2017.
 */
public class EventJsonObject {

    public ImageData readImageData(String value){
        try{
            JSONObject object = new JSONObject(value);
            String path = object.getString("path");
            int radius = object.getInt("radius");
            int sampling = object.getInt("sampling");
            return ImageData.fromAsset(path, radius, sampling);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void set(){

    }
}
