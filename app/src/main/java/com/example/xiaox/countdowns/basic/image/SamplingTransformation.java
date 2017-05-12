package com.example.xiaox.countdowns.basic.image;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

/**
 * Created by xiaox on 2/16/2017.
 */
public class SamplingTransformation implements Transformation<Bitmap> {
    private static int DEFAULT_SAMPLING = 1;

    private Context mContext;
    private BitmapPool mBitmapPool;
    private int mSampling;

    public SamplingTransformation(Context context) {
        this(context, Glide.get(context).getBitmapPool(), DEFAULT_SAMPLING);
    }

    public SamplingTransformation(Context context, BitmapPool pool) {
        this(context, pool, DEFAULT_SAMPLING);
    }

    public SamplingTransformation(Context context, int mSampling) {
        this(context, Glide.get(context).getBitmapPool(), mSampling);
    }

    public SamplingTransformation(Context context, BitmapPool pool, int mSampling) {
        this.mContext = context;
        this.mBitmapPool = pool;
        this.mSampling = mSampling;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();
        Bitmap bitmap = BitmapUtils.sampling(source, mSampling);
        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @Override public String getId() {
        return "SamplingTransformation(sampling=" + mSampling + ")";
    }
}
