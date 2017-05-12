package com.example.xiaox.countdowns.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * Created by xiaox on 1/22/2017.
 */
//Fit last child's height
public class LastFitFrame extends ViewGroup {

    private int lastFitHeight = 0;
    private int custom;

    public void setCustom(int custom){
        this.custom = custom;
    }
    public int getCustom(){
        return custom;
    }

    public LastFitFrame(Context context){
        super(context);
        this.setWillNotDraw(false);
        init();
    }
    public LastFitFrame(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setWillNotDraw(false);
        init();
    }
    public LastFitFrame(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        this.setWillNotDraw(false);
        init();
    }

    private void init(){

        final LastFitFrame self = this;
        ViewTreeObserver vto = this.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                self.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = self.getWidth();
                int height = self.getHeight();
                self.invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = 0;
        int heightTemp = 0;
        int maxPreviousHeight = 0;
        int count = getChildCount();
        View child;
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            if(child.getVisibility() != GONE){
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                heightTemp = child.getMeasuredHeight();
                maxPreviousHeight = Math.max(maxPreviousHeight, heightTemp);
            }
        }

        // Check against our minimum height and width
        maxPreviousHeight = Math.max(maxPreviousHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        lastFitHeight = heightTemp;
        setMeasuredDimension(maxWidth, lastFitHeight);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int minAdapt = Math.min(sizeHeight, sizeWidth);
        /*
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int layoutWidth = 0;
        int layoutHeight = 0;
        int cWidth = 0;
        int cHeight = 0;
        if(widthMode == MeasureSpec.EXACTLY){
            layoutWidth = sizeWidth;
        }else{
            for(int i = 0; i < internalViews.length; i++){
                for (int j = 0; j < internalViews[i].length; j++){
                    if(internalViews[i][j] == null) continue;
                    View child = internalViews[i][j];
                    cHeight = child.getMeasuredHeight();
                    layoutHeight = cHeight > layoutHeight ? cHeight : layoutHeight;
                }
            }
        }
        setMeasuredDimension(layoutWidth, layoutHeight);*/
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = 0;
        int count = getChildCount();
        View child;
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            //View.layout(left, top, right, bottom);
            int ch = child.getMeasuredHeight();
            int cw = child.getMeasuredWidth();
            child.layout(0, 0, cw, lastFitHeight);
            height += child.getMeasuredHeight();
        }
    }
}
