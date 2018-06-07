package com.can.ui.draw;

import com.can.activity.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class Compass extends View {
	private Bitmap mClockBgBmp = null;
	private Bitmap mPointBmp = null;
	
	private int mLayoutWidth = 0;
	private int mLayoutHeight = 0;
	
	private int mPointX = 0;
	private int mPointY = 0;
	private int mClockbgId = 0;
	private int mPointId = 0;
	private int mAngle = 240;
	public Compass(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.Compass); 
		mClockbgId = typedArray.getResourceId(R.styleable.Compass_clock_bg, 0);
		mPointId = typedArray.getResourceId(R.styleable.Compass_needle, 0);
		Options options = new BitmapFactory.Options();
		if (mClockbgId != 0) {
			mClockBgBmp = BitmapFactory.decodeResource(getResources(), mClockbgId, options);
			mLayoutWidth = mClockBgBmp.getWidth();
			mLayoutHeight = mClockBgBmp.getHeight();
		}
		if (mPointId != 0) {
			mPointBmp = BitmapFactory.decodeResource(getResources(), mPointId, options);
		}
		typedArray.recycle();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//        mBgDrawable.setBounds( (availableWidth -mBgDrawable.getIntrinsicWidth())/2, 
//        		(availableHeight - mBgDrawable.getIntrinsicHeight())/2,
//        		(availableWidth + mBgDrawable.getIntrinsicWidth())/2,
//        		(availableHeight + mBgDrawable.getIntrinsicHeight())/2);
//        mBgDrawable.draw(canvas);  
//        int x = availableWidth / 2;  
//        int y = availableHeight / 2;  
//
//        
//        canvas.save();
//        canvas.rotate(mAngle, x, y);  
//        
//        int w = mClockBg.getHeight();;  
//        int h = mClockBg.getWidth();; 
//        
//        w = mPointDrawable.getIntrinsicWidth();  
//        h = mPointDrawable.getIntrinsicHeight();  
//        mPointDrawable.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));  
//        mPointDrawable.draw(canvas);  
//        canvas.restore();  
		if (mClockBgBmp != null) {
			canvas.drawBitmap(mClockBgBmp, 0, 0, null);
		}
        
		if (mPointBmp != null) {
			Bitmap bmp = mPointBmp;
			mPointX = (mLayoutWidth-bmp.getWidth())/2;
			mPointY = (mLayoutHeight-bmp.getHeight())/2;
			canvas.save();
			canvas.translate(mPointX, mPointY);
			canvas.rotate(mAngle, bmp.getWidth()/2, bmp.getHeight()/2);
			canvas.drawBitmap(bmp, 0, 0, null);
			canvas.restore();
		}
	}

	public void setAngle(int angle) {
		if (mAngle != angle) {
			mAngle = angle;
			invalidate();
		}
	}

}
