
package com.yecon.canbus.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.yecon.canbus.R;

import static com.yecon.canbus.info.CANBusConstants.*;

public class CANBusTempView extends View {
    private static final int BITMAP_WIDTH = 8;
    private static final int BITMAP_HEIGHT = 51;

    private int mColdStalls = HALF_MAX_STALLS;
    private int mHotStalls = HALF_MAX_STALLS;

    private boolean mHasCenterGear = false;

    private Paint mPaint;
    private Paint mPaintLucency;

    private Resources mRes;

    public CANBusTempView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();

        mRes = getResources();

        mPaintLucency = new Paint();
        mPaintLucency.setColor(mRes.getColor(R.color.lucency));

        if (!TEMPERATURE_HAS_CENTER_GEAR) {
            mHasCenterGear = false;
        }

        if (TEMPERATURE_HAS_CENTER_GEAR) {
            mHasCenterGear = true;
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TEMPERATURE_HAS_CENTER_GEAR) {
            drawCenterGearUI(canvas);
        } else {
            drawNoCenterGearUI(canvas);
        }
    }

    private void drawCenterGearUI(Canvas canvas) {
        if (mColdStalls == 0 && mHotStalls == 0 && !mHasCenterGear) {
            canvas.drawRect(0, 0, BITMAP_WIDTH * (MAX_STALLS + 1), BITMAP_HEIGHT, mPaintLucency);
            return;
        }

        if (mColdStalls == 0) {
            canvas.drawRect(0, 0, BITMAP_WIDTH * HALF_MAX_STALLS, BITMAP_HEIGHT, mPaintLucency);
        }

        if (!mHasCenterGear) {
            canvas.drawRect(0, BITMAP_WIDTH * HALF_MAX_STALLS, BITMAP_WIDTH, BITMAP_HEIGHT,
                    mPaintLucency);
        }

        if (mHotStalls == 0) {
            canvas.drawRect(0, BITMAP_WIDTH * (HALF_MAX_STALLS + 1), BITMAP_WIDTH * MAX_STALLS,
                    BITMAP_HEIGHT, mPaintLucency);
        }

        if (mColdStalls > 0 && mColdStalls <= HALF_MAX_STALLS) {
            Bitmap bitmap1 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature1);
            Bitmap bitmap2 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature2);
            Bitmap bitmap3 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature3);
            Bitmap bitmap4 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature4);
            Bitmap bitmap5 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature5);
            Bitmap bitmap6 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature6);
            Bitmap bitmap7 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature7);
            Bitmap bitmap8 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature8);

            canvas.drawBitmap(bitmap1, BITMAP_WIDTH * 0, 0,
                    (mColdStalls + 1 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap2, BITMAP_WIDTH * 1, 0,
                    (mColdStalls + 2 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap3, BITMAP_WIDTH * 2, 0,
                    (mColdStalls + 3 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap4, BITMAP_WIDTH * 3, 0,
                    (mColdStalls + 4 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap5, BITMAP_WIDTH * 4, 0,
                    (mColdStalls + 5 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap6, BITMAP_WIDTH * 5, 0,
                    (mColdStalls + 6 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap7, BITMAP_WIDTH * 6, 0,
                    (mColdStalls + 7 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap8, BITMAP_WIDTH * 7, 0,
                    (mColdStalls + 8 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
        }

        if (mHasCenterGear) {
            Bitmap bitmapOff = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature_off);

            canvas.drawBitmap(bitmapOff, BITMAP_WIDTH * HALF_MAX_STALLS, 0, mPaint);
        }

        if (mHotStalls > 0 && mHotStalls <= HALF_MAX_STALLS) {
            Bitmap bitmap9 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature9);
            Bitmap bitmap10 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature10);
            Bitmap bitmap11 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature11);
            Bitmap bitmap12 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature12);
            Bitmap bitmap13 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature13);
            Bitmap bitmap14 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature14);
            Bitmap bitmap15 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature15);
            Bitmap bitmap16 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature16);

            canvas.drawBitmap(bitmap9, BITMAP_WIDTH * (HALF_MAX_STALLS + 1), 0,
                    (9 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap10, BITMAP_WIDTH * (HALF_MAX_STALLS + 2), 0,
                    (10 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap11, BITMAP_WIDTH * (HALF_MAX_STALLS + 3), 0,
                    (11 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap12, BITMAP_WIDTH * (HALF_MAX_STALLS + 4), 0,
                    (12 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap13, BITMAP_WIDTH * (HALF_MAX_STALLS + 5), 0,
                    (13 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap14, BITMAP_WIDTH * (HALF_MAX_STALLS + 6), 0,
                    (14 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap15, BITMAP_WIDTH * (HALF_MAX_STALLS + 7), 0,
                    (15 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap16, BITMAP_WIDTH * (HALF_MAX_STALLS + 8), 0,
                    (16 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
        }

    }

    private void drawNoCenterGearUI(Canvas canvas) {
        if (mColdStalls == 0 && mHotStalls == 0) {
            canvas.drawRect(0, 0, BITMAP_WIDTH * MAX_STALLS, BITMAP_HEIGHT, mPaintLucency);
            return;
        }

        if (mColdStalls == 0) {
            canvas.drawRect(0, 0, BITMAP_WIDTH * HALF_MAX_STALLS, BITMAP_HEIGHT, mPaintLucency);
        }

        if (mHotStalls == 0) {
            canvas.drawRect(0, BITMAP_WIDTH * HALF_MAX_STALLS, BITMAP_WIDTH * MAX_STALLS,
                    BITMAP_HEIGHT, mPaintLucency);
        }

        if (mColdStalls > 0 && mColdStalls <= HALF_MAX_STALLS) {
            Bitmap bitmap1 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature1);
            Bitmap bitmap2 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature2);
            Bitmap bitmap3 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature3);
            Bitmap bitmap4 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature4);
            Bitmap bitmap5 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature5);
            Bitmap bitmap6 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature6);
            Bitmap bitmap7 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature7);
            Bitmap bitmap8 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature8);

            canvas.drawBitmap(bitmap1, BITMAP_WIDTH * 0, 0,
                    (mColdStalls + 1 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap2, BITMAP_WIDTH * 1, 0,
                    (mColdStalls + 2 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap3, BITMAP_WIDTH * 2, 0,
                    (mColdStalls + 3 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap4, BITMAP_WIDTH * 3, 0,
                    (mColdStalls + 4 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap5, BITMAP_WIDTH * 4, 0,
                    (mColdStalls + 5 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap6, BITMAP_WIDTH * 5, 0,
                    (mColdStalls + 6 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap7, BITMAP_WIDTH * 6, 0,
                    (mColdStalls + 7 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap8, BITMAP_WIDTH * 7, 0,
                    (mColdStalls + 8 > HALF_MAX_STALLS) ? mPaint : mPaintLucency);
        }

        if (mHotStalls > 0 && mHotStalls <= HALF_MAX_STALLS) {
            Bitmap bitmap9 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature9);
            Bitmap bitmap10 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature10);
            Bitmap bitmap11 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature11);
            Bitmap bitmap12 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature12);
            Bitmap bitmap13 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature13);
            Bitmap bitmap14 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature14);
            Bitmap bitmap15 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature15);
            Bitmap bitmap16 = BitmapFactory.decodeResource(mRes, R.drawable.gc7_temperature16);

            canvas.drawBitmap(bitmap9, BITMAP_WIDTH * (HALF_MAX_STALLS + 0), 0,
                    (9 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap10, BITMAP_WIDTH * (HALF_MAX_STALLS + 1), 0,
                    (10 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap11, BITMAP_WIDTH * (HALF_MAX_STALLS + 2), 0,
                    (11 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap12, BITMAP_WIDTH * (HALF_MAX_STALLS + 3), 0,
                    (12 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap13, BITMAP_WIDTH * (HALF_MAX_STALLS + 4), 0,
                    (13 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap14, BITMAP_WIDTH * (HALF_MAX_STALLS + 5), 0,
                    (14 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap15, BITMAP_WIDTH * (HALF_MAX_STALLS + 6), 0,
                    (15 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
            canvas.drawBitmap(bitmap16, BITMAP_WIDTH * (HALF_MAX_STALLS + 7), 0,
                    (16 - mHotStalls <= HALF_MAX_STALLS) ? mPaint : mPaintLucency);
        }
    }

    public void setColdStalls(int coldStalls) {
        mColdStalls = coldStalls;
    }

    public void setHotStalls(int hotStalls) {
        mHotStalls = hotStalls;
    }

    public void setCenterGear(boolean hasCenterGear) {
        mHasCenterGear = hasCenterGear;
    }

}
