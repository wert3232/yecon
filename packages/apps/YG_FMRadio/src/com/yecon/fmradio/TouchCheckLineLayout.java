
package com.yecon.fmradio;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class TouchCheckLineLayout extends LinearLayout {
    static final int DELAY_TIME = 2000;
    static final int HAND_COUNT = 4;
    public static final String ACTION_FIVEHAND_TOUCH = "action.fivehand.touch";
    Context mContext = null;
    static int mTouchPointCount = 0;
    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            sendFiveHandMessage();
        }
    };

    public TouchCheckLineLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public TouchCheckLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public TouchCheckLineLayout(Context context) {
        super(context);
        mContext = context;
    }

    void sendFiveHandMessage() {
        if (mContext != null && mTouchPointCount >= HAND_COUNT) {
            Intent intent = new Intent(ACTION_FIVEHAND_TOUCH);
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        mTouchPointCount = ev.getPointerCount();
        // Log.d("xhh", "onInterceptTouchEvent="+action+", count="+count);

        if ((mTouchPointCount >= HAND_COUNT)
                && (action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_DOWN)) {
            // Log.d("xhh", "#4#===five hands===");
            if (mHandler != null && mRunnable != null) {
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, DELAY_TIME);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        mTouchPointCount = event.getPointerCount();
        // Log.d("xhh", "onTouchEvent="+action+", count="+count);

        if ((mTouchPointCount >= HAND_COUNT)
                && (action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_DOWN)) {
            // Log.d("xhh", "#4#===five hands===");
            if (mHandler != null && mRunnable != null) {
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, DELAY_TIME);
            }
            return false;
        }
        return true;
    }
}
