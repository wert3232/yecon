package com.yecon.filemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.GestureDetector;

/**
 * Created by chenchu on 14-12-15.
 */
public class ListViewItemHolder extends LinearLayout {

    public ListViewItemHolder(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    private FileItemGestureDetector.OnListItemTouchListener mFileTouchListener;
    private FileItemGestureDetector mGestureDetector;
    public void setOnListItemTouchListener(FileItemGestureDetector.OnListItemTouchListener listener) {
        mFileTouchListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //super.onInterceptTouchEvent(event);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if (super.onTouchEvent(event)){
        //return true;
       // } else {
        if (mGestureDetector == null) {
            mGestureDetector = FileItemGestureDetector.newInstance(this, mFileTouchListener);
       }
        return mGestureDetector.doDetect(event);
       // }//return super.onTouchEvent(event);?????????
    }

}
