package com.yecon.filemanager;

import java.lang.ref.WeakReference;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chenchu on 14-12-16.
 */
public class FileItemGestureDetector extends GestureDetector.SimpleOnGestureListener {
    public static final String Tag =" file item gesture detector";

    private WeakReference<View> mView;

    private OnListItemTouchListener mFileTouchListener;

    private GestureDetector mGestureDetector;

    public interface OnListItemTouchListener {
        public boolean onDoubleTap(View view ,MotionEvent e);
        public boolean onSignleTapConfirmed(View view);
        public boolean onLongPress(View view, MotionEvent e);
    }

    public void setOnListItemTouchListener(OnListItemTouchListener listener) {
        mFileTouchListener = listener;
    }


    public static FileItemGestureDetector newInstance(View v,FileItemGestureDetector.OnListItemTouchListener listener){
        FileItemGestureDetector gd = new FileItemGestureDetector();
        gd.setView(v);
        gd.setOnListItemTouchListener(listener);
        return gd;
    }

    public void setView(View v) {
      mView = new WeakReference<View>(v);
    }

    private View getView(){
        View v = mView.get();
        if (v == null){
            throw new IllegalStateException();
        }
        return v;
    }
    public boolean doDetect(MotionEvent event) {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getView().getContext(), this/*,mHandler*/);
            mGestureDetector.setOnDoubleTapListener(this);
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(Tag,"double tap confirmed");
        if(mFileTouchListener!=null) {
            mFileTouchListener.onDoubleTap(getView(), e);
        }
        return true;
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        //mView.performClick();
       // mFileTouchListener.onSignleTapConfirmed(mView,e);
        //int position = ((ListView)mView.getParent()).getPositionForView(mView);
        mFileTouchListener.onSignleTapConfirmed(getView());
        Log.d(Tag, "single tap confirmed");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d(Tag,"double tap event");
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(Tag, "on Down");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(Tag, "on fling");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(Tag, "on scroll");
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(Tag, "on single Tap up");
        //mView.performClick();
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(Tag, "on show press");
        return;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(Tag, "on long press");
        mFileTouchListener.onLongPress(getView(),e);
        return;
    }


}

