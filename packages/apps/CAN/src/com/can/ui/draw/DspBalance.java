package com.can.ui.draw;


import com.can.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * ClassName:DspBalance
 * 
 * @function:平衡调节
 * @author Kim
 * @Date:  2016-6-27 下午2:13:33
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class DspBalance extends View {
	
	private Bitmap mObjPoint;
	private float mfFad, mfBal;
	private float mObjWidthParent, mObjHeightParent;
	
	private int w_ajust, y_ajust;
	private int miDefFad, miDefBal;
	
	private OnTouchListener mObjTouchListener = null;
	
	public DspBalance(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DspBalance(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mObjPoint = BitmapFactory.decodeResource(getResources(), R.drawable.toyota_dsp_seekbar_point);
		w_ajust = mObjPoint.getWidth() / 2;
		y_ajust = mObjPoint.getHeight() / 2;
	}
	
	public void setDefMaxVal(int iMax){
		this.miDefFad = iMax;
		this.miDefBal = iMax;
	}
	
	public void setBalanceVal(int iFad, int ibal){
		this.move(iFad*getWidth()/miDefFad, ibal*getHeight()/miDefBal);
		invalidate();
		
		if (mObjTouchListener != null) {
			mObjTouchListener.onBalance(iFad, ibal);
		}
	}
	
	public void setBalenceListener(OnTouchListener listener){
		this.mObjTouchListener = listener;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		canvas.drawBitmap(mObjPoint, mfFad - mObjPoint.getWidth() / 2, mfBal - mObjPoint.getHeight()
				/ 2, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		
		mObjWidthParent = width - 2 * w_ajust;
		mObjHeightParent = height - 2 * y_ajust;

		mfFad = getWidth() / 2 + (miDefFad * mObjWidthParent) / (miDefFad*2);
		mfBal = getHeight() / 2 - (miDefBal * mObjHeightParent) / (miDefBal*2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			move(x, y);
			invalidate();
			if (mObjTouchListener != null) {
				mObjTouchListener.onBalance(mfFad*miDefFad/(getWidth()-w_ajust*2), mfBal*miDefBal/(getHeight()-w_ajust*2));
			}
			break;
		default:
			break;
		}
		
		return true;
	}
	
	private void move(float x, float y) {
		mfFad = x;
		mfBal = y;

		if (x < w_ajust) {
			mfFad = w_ajust;
		} else if (x > this.getWidth() - w_ajust) {
			mfFad = this.getWidth() - w_ajust;
		}

		if (y < y_ajust) {
			mfBal = y_ajust;
		} else if (y > this.getHeight() - y_ajust) {
			mfBal = this.getHeight() - y_ajust;
		}
	}
	
	public interface OnTouchListener {
		void onBalance(float fFad, float fBal);
	}
}
