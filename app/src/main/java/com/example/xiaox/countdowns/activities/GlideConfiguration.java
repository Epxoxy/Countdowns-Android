package com.example.xiaox.countdowns.activities;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.example.xiaox.countdowns.basic.extension.XLog;

/**
 * Created by xiaox on 2/16/2017.
 */
/**add this codein AndroidManifest.xml to work
 *<meta-data
 *android:name="packagePath.GlideConfiguration"
 *android:value="GlideModule" />
***/
public class GlideConfiguration implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        XLog.logLine("Working.......");
        glideBuilder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        //Set disk cache size to 100 * 1024 * 1024 (100M)
        glideBuilder.setDiskCache(new InternalCacheDiskCacheFactory(context, 100 * 1024 * 1024));
        //If cache in external
        //glideBuilder.setDiskCache(new ExternalCacheDiskCacheFactory(context, "diskCacheName", 100 * 1024 * 1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        //On register model loaders
        //We can clear cache as option
        //glide.clearDiskCache();
        //Option : set memory category
        //glide.setMemoryCategory(MemoryCategory.LOW);
    }
}
