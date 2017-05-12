package com.example.xiaox.countdowns.basic.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.xiaox.countdowns.basic.extension.XLog;

/**
 * Created by xiaox on 2/16/2017.
 */
public class SamplingBlurAsyncTask02 extends AsyncTask<ImageData, Void, Bitmap[]> {
    private static int idCounter = 0;
    private Context context;
    private ImageView samplingView;
    private ImageView blurView;
    private boolean isRunning;
    private int id;

    public SamplingBlurAsyncTask02(Context context, ImageView samplingView, ImageView blurView){
        this.context = context;
        this.samplingView = samplingView;
        this.blurView = blurView;
        this.id = idCounter;
        ++idCounter;
        XLog.logLine("AsyncLoadTask(" +id + ") ---> created");
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
    protected Bitmap[] doInBackground(ImageData... params) {
        if(params[0] != null){
            ImageData data = params[0];
            Bitmap origin = BitmapFactory.decodeFile(data.path());
            Bitmap samplingBitmap = BitmapUtils.sampling(origin, params[0].sampling);
            Bitmap blurBitmap = BitmapUtils.blur(context, samplingBitmap, params[0].radius);
            XLog.logLine("AsyncLoadTask(" +id + ") ---> doInBackground" + params[0].sampling);
            return new Bitmap[]{samplingBitmap, blurBitmap};
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap[] bitmap){
        XLog.logLine("AsyncLoadTask(" +id + ") ---> onPostExecute");
        isRunning = false;
        if(bitmap != null && bitmap.length == 2){
            if(samplingView != null && bitmap[0] != null){
                samplingView.setImageBitmap(bitmap[0]);
                XLog.logLine("AsyncLoadTask(" +id + ") ---> onPostExecute samplingView set " + bitmap[0].isRecycled());
            }
            if(blurView != null && bitmap[1] != null){
                blurView.setImageResource(0);
                blurView.setImageBitmap(bitmap[1]);
                XLog.logLine("AsyncLoadTask(" +id + ") ---> onPostExecute  blurView set " + bitmap[1].isRecycled());
            }
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
