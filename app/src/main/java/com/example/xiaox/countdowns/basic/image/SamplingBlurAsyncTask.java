package com.example.xiaox.countdowns.basic.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.xiaox.countdowns.basic.file.FileUtils;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by xiaox on 2/16/2017.
 */
public class SamplingBlurAsyncTask extends AsyncTask<ImageData, Void, Bitmap> {
    private static int idCounter = 0;
    private Context context;
    private ImageView samplingBlurView;
    private boolean isRunning;
    private int id;

    private SamplingBlurAsyncTask(Context context, ImageView samplingBlurView){
        this.context = context;
        this.samplingBlurView = samplingBlurView;
        this.id = idCounter;
        ++idCounter;
        XLog.logLine("AsyncLoadTask(" +id + ") ---> created");
    }

    public static SamplingBlurAsyncTask create(Context context, ImageView samplingBlurView){
        return new SamplingBlurAsyncTask(context, samplingBlurView);
    }

    public boolean isRunning(){
        return this.isRunning;
    }

    @Override
    protected void onPreExecute(){
        this.isRunning = true;
        XLog.logLine("AsyncLoadTask(" +id + ") ---> onPreExecute isRunning");
    }

    @Override
    protected Bitmap doInBackground(ImageData... params) {
        if(params != null && params.length > 0 && params[0] != null){
            ImageData data = params[0];
            FileInputStream fis = null;
            try{
                if(data.isInAssets()){
                    fis = context.getAssets().openFd(FileUtils.toAssetPath(data.path()))
                            .createInputStream();
                }else{
                    fis  = new FileInputStream(new File(data.path()));
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            if(fis == null) return null;
            //Decode to bitmap
            Bitmap origin = BitmapFactory.decodeStream(fis);
            if(origin == null) return null;
            //Blur
            Bitmap blurBitmap = BitmapUtils.blurSampling(context, origin, data.radius, data.sampling);
            XLog.logLine("AsyncLoadTask(" +id + ") ---> doInBackground");
            return blurBitmap;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        XLog.logLine("AsyncLoadTask(" +id + ") ---> onPostExecute");
        isRunning = false;
        if(bitmap != null && samplingBlurView != null){
            samplingBlurView.setImageBitmap(bitmap);
            XLog.logLine("AsyncLoadTask(" +id + ") ---> onPostExecute samplingBlurView set ");
        }else {
            XLog.logLine("AsyncLoadTask(" +id + ") ---> onPostExecute empty");
        }
    }

    @Override
    protected void onCancelled(){
        super.onCancelled();
        XLog.logLine("AsyncLoadTask(" +id + ") ---> onCancelled");
    }

}
