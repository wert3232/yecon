
package com.yecon.gpstest;

import java.util.ArrayList;
import java.util.Iterator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class GPSSnrPrnView extends View {
    private int RECT_COUNT = 12;
    private int RECT_X = 0; // 45;
    private int RECT_Y = 50;
    private int RECT_WIDTH = 25;
    private int RECT_HEIGHT = 70;
    private int RECT_OFFSET = 10;
    private int SNR_TEXT_Y = 40;
    private int PRN_TEXT_Y = 140;
    /**
     * 0: RECT_COUNT 1: RECT_X 2: RECT_Y 3: RECT_WIDTH 4: RECT_HEIGHT 5: RECT_OFFSET 6: SNR_TEXT_Y
     * 7: PRN_TEXT_Y
     */
    private int[] mSnrPrnViewConstant = null;

    private ArrayList<GPSInfoItem> mGPSInfoItemList = new ArrayList<GPSInfoItem>();
    private boolean isLocation = false;

    private Paint mPaintText;
    private Paint mPaintBgRect;
    private Paint mPaintFillRect;

    public void setMList(ArrayList<GPSInfoItem> gpsInfoItemList) {
        this.mGPSInfoItemList = gpsInfoItemList;
    }

    public void setIsLocation(boolean isLocation) {
        this.isLocation = isLocation;
    }

    public GPSSnrPrnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.postInvalidate();

        mPaintText = new Paint();
        mPaintBgRect = new Paint();
        mPaintFillRect = new Paint();

        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(22);
        mPaintText.setAntiAlias(true);

        mPaintBgRect.setColor(Color.GRAY);

        mPaintFillRect.setAntiAlias(true);
        mPaintFillRect.setStyle(Style.FILL);

        mSnrPrnViewConstant = context.getResources().getIntArray(R.array.SnrPrnViewConstant);
        if (mSnrPrnViewConstant != null && mSnrPrnViewConstant.length == 8) {
            RECT_COUNT = mSnrPrnViewConstant[0];
            RECT_X = mSnrPrnViewConstant[1];
            RECT_Y = mSnrPrnViewConstant[2];
            RECT_WIDTH = mSnrPrnViewConstant[3];
            RECT_HEIGHT = mSnrPrnViewConstant[4];
            RECT_OFFSET = mSnrPrnViewConstant[5];
            SNR_TEXT_Y = mSnrPrnViewConstant[6];
            PRN_TEXT_Y = mSnrPrnViewConstant[7];
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (mGPSInfoItemList.isEmpty()) {
            DrawClear(canvas);
        } else {
            DrawSnrPrn(canvas);
        }
    }

    private void DrawClear(Canvas canvas) {
        for (int i = 0; i < RECT_COUNT; i++) {
            canvas.drawRect(
                    RECT_X + i * (RECT_WIDTH + RECT_OFFSET),
                    RECT_Y,
                    RECT_X + RECT_WIDTH + i * (RECT_WIDTH + RECT_OFFSET),
                    RECT_Y + RECT_HEIGHT,
                    mPaintBgRect);
            canvas.drawText(
                    "0",
                    RECT_X + RECT_OFFSET / 2 + i * (RECT_WIDTH + RECT_OFFSET),
                    SNR_TEXT_Y,
                    mPaintText);
            canvas.drawText(
                    "0",
                    RECT_X + RECT_OFFSET / 2 + i * (RECT_WIDTH + RECT_OFFSET),
                    PRN_TEXT_Y,
                    mPaintText);
        }
    }

    private void DrawSnrPrn(Canvas canvas) {
        if (isLocation) {
            mPaintFillRect.setColor(Color.GREEN);
        } else {
            mPaintFillRect.setColor(Color.BLUE);
        }

        Iterator<GPSInfoItem> it = mGPSInfoItemList.iterator();

        for (int i = 0; i < RECT_COUNT; i++) {
            if (it.hasNext()) {
                GPSInfoItem item = it.next();
                int snr = (int) item.mSnr;
                int prn = item.mPrn;

                canvas.drawRect(
                        RECT_X + i * (RECT_WIDTH + RECT_OFFSET),
                        RECT_Y,
                        RECT_X + RECT_WIDTH + i * (RECT_WIDTH + RECT_OFFSET),
                        RECT_Y + RECT_HEIGHT,
                        mPaintBgRect);
                canvas.drawRect(
                        RECT_X + i * (RECT_WIDTH + RECT_OFFSET),
                        RECT_Y + RECT_HEIGHT - snr,
                        RECT_X + RECT_WIDTH + i * (RECT_WIDTH + RECT_OFFSET),
                        RECT_Y + RECT_HEIGHT,
                        mPaintFillRect);
                canvas.drawText(
                        "" + snr,
                        RECT_X + RECT_OFFSET / 2 + i * (RECT_WIDTH + RECT_OFFSET),
                        SNR_TEXT_Y,
                        mPaintText);
                canvas.drawText(
                        "" + prn,
                        RECT_X + RECT_OFFSET / 2 + i * (RECT_WIDTH + RECT_OFFSET),
                        PRN_TEXT_Y,
                        mPaintText);
            } else {
                canvas.drawRect(
                        RECT_X + i * (RECT_WIDTH + RECT_OFFSET),
                        RECT_Y,
                        RECT_X + RECT_WIDTH + i * (RECT_WIDTH + RECT_OFFSET),
                        RECT_Y + RECT_HEIGHT,
                        mPaintBgRect);
                canvas.drawText(
                        "0",
                        RECT_X + RECT_OFFSET / 2 + i * (RECT_WIDTH + RECT_OFFSET),
                        SNR_TEXT_Y,
                        mPaintText);
                canvas.drawText(
                        "0",
                        RECT_X + RECT_OFFSET / 2 + i * (RECT_WIDTH + RECT_OFFSET),
                        PRN_TEXT_Y,
                        mPaintText);
            }
        }
    }
}
