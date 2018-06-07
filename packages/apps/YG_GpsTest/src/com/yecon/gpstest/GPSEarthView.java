
package com.yecon.gpstest;

import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Math;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GPSEarthView extends View {
    Context mContext;

    Bitmap mEarthBmp;

    private ArrayList<GPSInfoItem> mGPSInfoItemList = new ArrayList<GPSInfoItem>();
    private int heightSize;
    private int widthSize;

    private boolean isLocation;

    private Paint mPaintText;
    private Paint mPaintBg;

    public void setMList(ArrayList<GPSInfoItem> gpsInfoItemList) {
        this.mGPSInfoItemList = gpsInfoItemList;
    }

    public void setIsLocation(boolean isLocation) {
        this.isLocation = isLocation;
    }

    public GPSEarthView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaintText = new Paint();
        mPaintBg = new Paint();

        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(15);
        mPaintText.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightSize = MeasureSpec.getSize(heightMeasureSpec);

        widthSize = heightSize;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,
                MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mGPSInfoItemList.isEmpty()) {
            if (isLocation) {
                mPaintBg.setColor(Color.RED);
            } else {
                mPaintBg.setColor(Color.BLUE);
            }

            Iterator<GPSInfoItem> it = mGPSInfoItemList.iterator();
            while (it.hasNext()) {
                GPSInfoItem item = it.next();

                int prn = item.mPrn;
                float azi = item.mAzi;
                float ele = item.mEle;

                float cx = (float) (heightSize / 2 + (heightSize * ele / 180)
                        * Math.sin(azi));
                float cy = (float) (heightSize / 2 - (heightSize * ele / 180)
                        * Math.cos(azi));

                canvas.drawCircle(cx, cy, 10, mPaintBg);
                canvas.drawText("" + prn, cx - 6, cy + 4, mPaintText);
            }

        }

    }

}
