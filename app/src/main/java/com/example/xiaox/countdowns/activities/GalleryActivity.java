package com.example.xiaox.countdowns.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.eventItem.EventItemManager;
import com.example.xiaox.countdowns.basic.file.FileUtils;
import com.example.xiaox.countdowns.basic.file.TypeNameFilter;
import com.example.xiaox.countdowns.basic.extension.XLog;
import com.example.xiaox.countdowns.activities.debug.LogActivity;

import java.io.File;

public class GalleryActivity extends AppCompatActivity {
    public static final int CHOOSE_SUCCESS = 1;
    public static String SELECTED_IMAGE_PATH_KEY = "selected_img";
    public static String PATH_TYPE_KEY = "pathType";
    private ImageAdapter imageAdapter;
    //Max local index
    //For get type of selected file(Local or Assets)
    private int maxLocalIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(R.string.gallery);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        GridView galleryGridView = (GridView)findViewById(R.id.galleryGridView);
        setupPicturesGrid(galleryGridView);
    }

    private void setupPicturesGrid(GridView gridView){
        if(gridView != null){
            SparseArray<String> picturePaths = initPicturePaths();
            if(picturePaths.size() > 0){
                imageAdapter = new ImageAdapter(this, picturePaths);
                gridView.setAdapter(imageAdapter);
                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        return callDeletePicture(position);
                    }
                });
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String path = (String)parent.getAdapter().getItem(position);
                        if(path != null){
                            int type = position > maxLocalIndex ? FileUtils.TYPE_ASSETS : FileUtils.TYPE_LOCAL;
                            setSuccessOnSelected(path, type);
                        }
                    }
                });
            }
        }
    }

    private SparseArray<String> initPicturePaths(){
        //For generate key of SparseArray
        int index = -1;
        maxLocalIndex = -1;
        SparseArray<String> picturePaths = new SparseArray<>();
        //Get local pictures from external pictures folder
        File picturesDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(picturesDir != null && picturesDir.exists()){
            String dirPath = picturesDir.getPath();
            String[] picNames = picturesDir.list(new TypeNameFilter(".jpg"));
            //Check if there is pictures exist
            if(picNames.length > 0){
                maxLocalIndex = picNames.length - 1;
                System.out.println(picNames.length);
                XLog.logLine("****** Load pictures from local ********");
                for(int i = 0; i < picNames.length; i++){
                    index ++;
                    picturePaths.append(index, dirPath + File.separator + picNames[i]);
                    XLog.logLine(index + ", " + (dirPath + File.separator + picNames[i]));
                }
            }
        }
        //Get assets pictures
        String[] picAssetsNames = null;
        try{
            picAssetsNames = getAssets().list("pictures");
        }catch (java.io.IOException ex){
            ex.printStackTrace();
        }
        //Load assets pictures
        if(picAssetsNames != null){
            XLog.logLine("****** Load pictures from assets ********");
            for(int i = 0; i < picAssetsNames.length; i++){
                index ++;
                picturePaths.append(index, "file:///android_asset/pictures/"+picAssetsNames[i]);
                XLog.logLine(picAssetsNames[i]);
            }
        }
        return picturePaths;
    }

    private boolean callDeletePicture(final int position){
        final String path = (String)imageAdapter.getItem(position);
        if(path == null || position <= maxLocalIndex) return false;
        //Create image view
        View convertView = LayoutInflater.from(GalleryActivity.this).inflate(R.layout.galery_item, null);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.galleryItemImageView);
        imageView.setPadding(0,40,0,0);
        Glide.with(GalleryActivity.this).load(path).dontAnimate().into(imageView);
        boolean isDeleted = false;
        //Create alert dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GalleryActivity.this);
        dialogBuilder.setView(convertView).setTitle(R.string.delete).setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        File file = new File(path);
                        if(file.exists())file.delete();
                        imageAdapter.removeAt(position);
                    }
                }).setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
        return true;
    }

    private void setSuccessOnSelected(String path, int type){
        Intent intent = new Intent();
        intent.putExtra(PATH_TYPE_KEY, type);
        intent.putExtra(SELECTED_IMAGE_PATH_KEY, path);
        this.setResult(CHOOSE_SUCCESS, intent);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }else if(id == R.id.action_clearCache){
            EventItemManager.clearDiskCache(this);
        }else if(id == R.id.action_log){
            Intent intent = new Intent();
            intent.setClass(GalleryActivity.this, LogActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery_activity_menu, menu);
        return true;
    }

    private static class ViewHolder{
        public ImageView imageView;
        public ViewHolder(){

        }
    }

    public class ImageAdapter extends BaseAdapter{
        private Activity activity;
        private SparseArray<String> paths;

        public ImageAdapter(Activity activity, SparseArray<String> arrays){
            this.activity = activity;
            this.paths = arrays;
        }

        public void removeAt(int position){
            this.paths.remove(position);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public Object getItem(int position) {
            return paths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = activity.getLayoutInflater().inflate(R.layout.galery_item, null);
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.galleryItemImageView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            Glide.with(activity).load(paths.get(position)).into(viewHolder.imageView);
            return convertView;
        }
    }
}
