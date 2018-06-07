
package com.yecon.backcar;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TraceView extends View {

    static private Paint paint;
    static private int angle = 0;
    public int miWidth = 0;
    public int mHeight = 0;
    
    public int miSrcWidth = 1024;
    public int miSrcHeight = 600;

    private Bitmap backGroundBp;

    public TraceView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public TraceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        //OnTimer_trace();
    }

    public void OnTimer_trace() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Message message = Message.obtain();
                message.what = 300;
                message.obj = 0;
                myHandler.sendMessage(message);
            }
        }, 0, 100);
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 300) {

                angle--;

                if (angle < -45)
                {
                    angle = 45;
                }
                else if (angle > 45)
                {
                    angle = -45;
                }
                invalidate();
            }
        }
    };

    private void initPaints() {
        setBackgroundResource(R.drawable.track_bk);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha(50);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth((float) 4.0); // �����߿�
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        //backGroundBp = BitmapFactory.decodeResource(getResources(),
         //       R.drawable.track_bk);
        
    }

    private void setBalanceValues(float pointX2, float pointY2) {
        // TODO Auto-generated method stub

    }
    
    public void DrawTrack(int iAngle){
    	angle = iAngle;
    	invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // drawPoint(canvas);

        canvas.drawColor(Color.TRANSPARENT);
        Trace.DrawBackCarTrack(canvas, paint, angle, getContext());
    }

    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // will compare to the xml settings
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                invalidate();
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
        // super.onTouchEvent(event);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;

        Parcelable superState = savedState.getParcelable("PARENT");
        super.onRestoreInstanceState(superState);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle state = new Bundle();
        state.putParcelable("PARENT", superState);
        return state;
    }

}
