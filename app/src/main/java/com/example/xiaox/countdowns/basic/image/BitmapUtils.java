package com.example.xiaox.countdowns.basic.image;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import com.example.xiaox.countdowns.basic.extension.XLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xiaox on 2/11/2017.
 */
public class BitmapUtils {
    private static final int DEF_SAMPLING = 8;
    private static int calculateInSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSize = 1;
        if(height > reqHeight || width > reqWidth){
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSize) > reqHeight &&(halfWidth / inSize > reqWidth)){
                inSize *= 2;
            }
        }
        return inSize;
    }

    public static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight){
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if(src != dst){
            src.recycle();
        }
        return dst;
    }

    public static Bitmap decodeBitmapFromResource(Resources res,int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    public static Bitmap decodeBitmapFromSD(String pathName,int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    public static Bitmap decodeBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }
        return bitmap;
    }

    //Case OOM
    public static Bitmap scaleBitmap(Bitmap origin, float radio){
        if(origin == null)return null;
        int height = origin.getHeight();
        int width = origin.getWidth();
        Matrix matrix = new Matrix();
        matrix.preScale(radio, radio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0,0, width, height, matrix, false);
        System.out.println("data "+newBM.getWidth() + ", " + newBM.getHeight());
        if(newBM.equals(origin)) return newBM;
        origin.recycle();;
        return newBM;
    }

    public static Bitmap decodeSamplingRestoreFromPath(String filePath, int sampling){
        if(sampling <= 1) return BitmapFactory.decodeFile(filePath);
        System.out.println("sampling "+sampling);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampling;
        Bitmap sample = BitmapFactory.decodeFile(filePath, options);
        System.out.println("data "+sample.getWidth() + ", " + sample.getHeight());
        return scaleBitmap(sample, sampling);
    }

    public static Bitmap cropScaleBitmap(Bitmap bitmap, int dstWidth, int dstHeight){
        Bitmap bitmap1 = cropBitmapRadio(bitmap, dstWidth, dstHeight);
        return createScaleBitmap(bitmap1, dstWidth, dstHeight);
    }

    public  static Bitmap cropBitmap(Bitmap bitmap, int dstWidth, int dstHeight){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int x = (width - dstWidth) / 2;
        int y = (height - dstHeight) / 2;
        return Bitmap.createBitmap(bitmap, x, y, dstWidth, dstHeight);
    }

    public  static Bitmap cropBitmapRadio(Bitmap bitmap, int rectWidth, int rectHeight){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int rw = width / rectWidth;
        int rh = height / rectHeight;
        if(rw > rh){
            return cropBitmap(bitmap, rectWidth / rh, height);
        }else {
            return cropBitmap(bitmap, width, rectHeight / rw);
        }
    }

    public static Bitmap blur(Context context, Bitmap bitmap, int radius){
        if(radius <=0 ) return bitmap;
        Bitmap output = Bitmap.createBitmap(bitmap); // 创建输出图片
        RenderScript rs = RenderScript.create(context); // 构建一个RenderScript对象
        ScriptIntrinsicBlur gaussianBlue = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)); //
        // 创建高斯模糊脚本
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap); // 开辟输入内存
        Allocation allOut = Allocation.createFromBitmap(rs, output); // 开辟输出内存
        gaussianBlue.setRadius(radius); // 设置模糊半径，范围0f<radius<=25f
        gaussianBlue.setInput(allIn); // 设置输入内存
        gaussianBlue.forEach(allOut); // 模糊编码，并将内存填入输出内存
        allOut.copyTo(output); // 将输出内存编码为Bitmap，图片大小必须注意
        rs.destroy(); // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
        return output;
    }

    public static Bitmap sampling(Bitmap origin, int sampling){
        if(sampling < 1) return origin;
        //Create bitmap of scaled
        int height = origin.getHeight();
        int width = origin.getWidth();
        int scaledWidth = width / sampling;
        int scaledHeight = height / sampling;
        Bitmap bitmap = Bitmap.createBitmap(scaledWidth,scaledHeight, Bitmap.Config.ARGB_8888);
        //Draw to canvas
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(origin, 0, 0, paint);
        return bitmap;
    }

    public static Bitmap blurSampling(Context context, Bitmap origin, float radius){
        return blurSampling(context, origin, radius, DEF_SAMPLING);
    }

    public static Bitmap blurSampling2(Context context, String filePath, int radius, int sampling){
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        bitmap = BitmapFactory.decodeFile(filePath);
        //Sampling
        if(sampling > 1) bitmap = sampling(bitmap, sampling);
        //Blur
        Bitmap blur = blur(context, bitmap, radius);
        bitmap.recycle();
        if(sampling > 1){
            //Restore
            Bitmap bigBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            //Draw to canvas
            Canvas canvas = new Canvas(bigBitmap);
            canvas.scale(sampling, sampling);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(blur, 0, 0, paint);
            return bigBitmap;
        }
        return blur;
    }

    public static Bitmap blurSampling(Context context, Bitmap origin, float radius, int sampling){
        Bitmap bitmap = sampling(origin, sampling);
        System.out.println("blur sampling " + bitmap.getHeight() + ", " + bitmap.getWidth());
        if(radius <= 0) return bitmap;
        return blur(context, bitmap, (int)radius);
    }

    public static boolean saveBitmapAsJPG(Bitmap bitmap,int quality,String filePath){
        return saveBitmapAsFile(bitmap, Bitmap.CompressFormat.JPEG, quality, filePath);
    }

    public static boolean saveBitmapAsFile(Bitmap bitmap, Bitmap.CompressFormat format, int quality, String filePath){
        File file = new File(filePath);
        if(file.exists()) file.delete();
        FileOutputStream fOut = null;
        try{
            fOut = new FileOutputStream(file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
            return false;
        }
        bitmap.compress(format, quality, fOut);
        try{
            fOut.flush();
            fOut.close();
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Bitmap scale(Bitmap bitmap, float sx, float sy){
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public static Bitmap bitmapFromDrawingCache(View view){
        if(!view.isDrawingCacheEnabled()){
            view.setDrawingCacheEnabled(true);
            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        }
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = null;
        try {
            int width = view.getWidth();
            int height = view.getHeight();
            if(width != 0 && height != 0){
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.layout(0, 0, width, height);
                view.draw(canvas);
            }
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    public static Bitmap mix(Bitmap src, Bitmap src2, int left, int top) {
        if (src == null || src2  == null) {
            XLog.logLine("mix src2 is null");
            return src;
        }

        int sWid = src.getWidth();
        int sHei = src.getHeight();
        int wWid = src2.getWidth();
        int wHei = src2.getHeight();
        if (sWid == 0 || sHei == 0) {
            return null;
        }

        if (sWid < wWid || sHei < wHei) {
            return src;
        }

        Bitmap bitmap = Bitmap.createBitmap(sWid, sHei, Bitmap.Config.ARGB_8888);
        try {
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(src, 0, 0, null);
            cv.drawBitmap(src2, left, top, null);
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }
}
