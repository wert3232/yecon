package com.yecon.sound.setting.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yecon.sound.setting.R;
import com.yecon.sound.setting.unitl.L;
import com.yecon.sound.setting.unitl.Tag;
import com.yecon.sound.setting.unitl.mtksetting;

/**
 * set the background bitmap let the icon on background diff (x,y) to distance
 * with top,left,right,bottom
 * 
 * deside the sound
 * 
 * */

public class BalanceView extends View {

	private Paint mPoint;
	private Bitmap pointBp;
	private Bitmap backGroundBp;
	private float backGroundHeight, backGroundWidth;
	private float pointX, pointY;
	private float mWidthParent, mHeightParent;
	public final static int BALANCE_VALUE_RANGE = 14; 
	public final static int BALANCE_VALUE_MAX = 7;
	// get the values for back front and right or left
	private int mValueX, mValueY;
	mtksetting mmtksetting;

	int w_ajust, y_ajust;

	public BalanceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public BalanceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mmtksetting = new mtksetting(context);
		mmtksetting.setSharedPreferences(context.getSharedPreferences("sound_settings", Context.MODE_PRIVATE));
		initPaints();
	}
	
	public void changeBalanceMode(int value){
		switch (value) {
		case 0:
			mValueX = 0;
			mValueY = 0;
			break;
		case 1:
			mValueX = 0;
			mValueY = BALANCE_VALUE_MAX;
			break;
		case 2:
			mValueX = 0;
			mValueY = -BALANCE_VALUE_MAX;
			break;
		case 3:
			mValueX = -BALANCE_VALUE_MAX;
			mValueY = 0;
			break;
		case 4:
			mValueX = BALANCE_VALUE_MAX;
			mValueY = 0;
			break;
		case 5:
			mValueX = -BALANCE_VALUE_MAX;
			mValueY = BALANCE_VALUE_MAX;
			break;
		case 6:
			mValueX = BALANCE_VALUE_MAX;
			mValueY = BALANCE_VALUE_MAX;
			break;
		case 7:
			mValueX = -BALANCE_VALUE_MAX;
			mValueY = -BALANCE_VALUE_MAX;
			break;
		case 8:
			mValueX = BALANCE_VALUE_MAX;
			mValueY = -BALANCE_VALUE_MAX;
			break;

		}

		pointX = getWidth() / 2 + mValueX * mWidthParent / BALANCE_VALUE_RANGE;
		pointY = getHeight() / 2 - mValueY * mHeightParent / BALANCE_VALUE_RANGE;
		invalidate();
//		mmtksetting.setBalance(mValueX, mValueY);
		setBlance(mValueX, mValueY);
		
		saveBalanceValue();
	}
	
	public void saveBalanceValue() {
		if (mOnBalanceListener != null) {
			mOnBalanceListener.onChangeValue(mValueY, mValueX);
		}
		/*mmtksetting.editor.putInt(Tag.valueX_tag, mValueX);
		mmtksetting.editor.putInt(Tag.valueY_tag, mValueY);*/
		
		mmtksetting.editor.putInt(Tag.valueX_tag, getBalanceValueX(mValueX));
		mmtksetting.editor.putInt(Tag.valueY_tag, getBalanceValueY(mValueY));
		
		mmtksetting.editor.commit();
	}

	private void initPaints() {
		mPoint = new Paint();
		mPoint.setAntiAlias(true);
		mPoint.setDither(true);//
		// mPoint.setColor(mCircleColor);
		// mPoint.setStrokeWidth(mCircleStrokeWidth);
		// setBackgroundResource(R.drawable.balance_sound_view);
		mPoint.setStyle(Paint.Style.STROKE);
		mPoint.setStrokeJoin(Paint.Join.ROUND);
		mPoint.setStrokeCap(Paint.Cap.ROUND);
		pointBp = BitmapFactory.decodeResource(getResources(), R.drawable.sound_point);
		backGroundBp = BitmapFactory.decodeResource(getResources(), R.drawable.balance_sound_view);
		backGroundHeight = backGroundBp.getHeight();
		backGroundWidth = backGroundBp.getWidth();
		w_ajust = pointBp.getWidth() / 2;
		y_ajust = pointBp.getHeight() / 2;
	}

	private void setBalanceValues(float pointX2, float pointY2) {
		// TODO Auto-generated method stub
		mValueX = (int) (BALANCE_VALUE_RANGE * (pointX2 - w_ajust) / mWidthParent) - BALANCE_VALUE_MAX;
		mValueY = BALANCE_VALUE_MAX - (int) ((BALANCE_VALUE_RANGE * (pointY2 - y_ajust)) / mHeightParent);
//		mmtksetting.setBalance(mValueX, mValueY);
		setBlance(mValueX, mValueY);
		
		saveBalanceValue();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawPoint(canvas);
	}

	public void addPointX() {
		if (mValueX >= BALANCE_VALUE_MAX)
			return;
		mValueX += 1;
		pointX = getWidth() / 2 + mValueX * mWidthParent / BALANCE_VALUE_RANGE;
		invalidate();
//		mmtksetting.setBalance(mValueX, mValueY);
		setBlance(mValueX, mValueY);
		saveBalanceValue();
	}

	public void cutPointX() {
		if (mValueX <= -BALANCE_VALUE_MAX)
			return;
		mValueX -= 1;
		pointX = getWidth() / 2 + mValueX * mWidthParent / BALANCE_VALUE_RANGE;
		invalidate();
//		mmtksetting.setBalance(mValueX, mValueY);
		setBlance(mValueX, mValueY);
		saveBalanceValue();
	}

	public void addPointY() {
		if (mValueY <= -BALANCE_VALUE_MAX)
			return;
		mValueY -= 1;
		pointY = getHeight() / 2 - mValueY * mHeightParent / BALANCE_VALUE_RANGE;
		invalidate();
//		mmtksetting.setBalance(mValueX, mValueY);
		setBlance(mValueX, mValueY);
		saveBalanceValue();
	}

	public void cutPointY() {
		if (mValueY >= BALANCE_VALUE_MAX)
			return;
		mValueY += 1;
		pointY = getHeight() / 2 - mValueY * mHeightParent / BALANCE_VALUE_RANGE;
		invalidate();
//		mmtksetting.setBalance(mValueX, mValueY);
		setBlance(mValueX, mValueY);
		saveBalanceValue();
	}
	
	private void setBlance(int valueX,int valueY){
		float blanceValueX = (float)valueX * (20 / (float)BALANCE_VALUE_MAX);
		float blanceValueY = (float)valueY * (20 / (float)BALANCE_VALUE_MAX);
		if(blanceValueX > 20){
			blanceValueX = 20;
		}else if(blanceValueX < -20){
			blanceValueX = -20;
		}
		
		if(blanceValueY > 20){
			blanceValueY = 20;
		}else if(blanceValueY < -20){
			blanceValueY = -20;
		}
//		L.e("blanceValueX: " + blanceValueX + " blanceValueY:" + blanceValueY + " valueX:" + valueX + " valueY:" + valueY);
		mmtksetting.setBalance(Math.round(blanceValueX), Math.round(blanceValueY));
	}
	
	public static int getBalanceValueX(int valueX){
		float balanceValueX = (float)valueX * (20 / (float)BALANCE_VALUE_MAX);
		if(balanceValueX > 20){
			balanceValueX = 20;
		}else if(balanceValueX < -20){
			balanceValueX = -20;
		}
		
		return Math.round(balanceValueX);
	}
	
	public static int getBalanceValueY(int valueY){
		float balanceValueY = (float)valueY * (20 / (float)BALANCE_VALUE_MAX);
		if(balanceValueY > 20){
			balanceValueY = 20;
		}else if(balanceValueY < -20){
			balanceValueY = -20;
		}
		return Math.round(balanceValueY);
	}
	
	public static int getViewValueX(int balanceValueX){
		float viewValueX = (float)balanceValueX * ((float)BALANCE_VALUE_MAX / 20 );
		if(viewValueX > BALANCE_VALUE_MAX){
			viewValueX = BALANCE_VALUE_MAX;
		}else if(viewValueX < -BALANCE_VALUE_MAX){
			viewValueX = -BALANCE_VALUE_MAX;
		}
		
		return Math.round(viewValueX);
	}
	
	public static int getViewValueY(int balanceValueY){
		float viewValueY = (float)balanceValueY * ((float)BALANCE_VALUE_MAX / 20);
		if(viewValueY > BALANCE_VALUE_MAX){
			viewValueY = BALANCE_VALUE_MAX;
		}else if(viewValueY < -BALANCE_VALUE_MAX){
			viewValueY = -BALANCE_VALUE_MAX;
		}
		return Math.round(viewValueY);
	}

	@Override
	protected void onFinishInflate() {

		super.onFinishInflate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// will compare to the xml settings
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		// int min = Math.min(width, height);
		// setMeasuredDimension(min, min);
		mWidthParent = width - 2 * w_ajust;
		mHeightParent = height - 2 * y_ajust;

		mValueX = getViewValueX(mmtksetting.uiState.getInt(Tag.valueX_tag, 0));
		mValueY = getViewValueY(mmtksetting.uiState.getInt(Tag.valueY_tag, 0));
		L.e("mValueX: " + mValueX + " mValueY:" + mValueY);
		pointX = getWidth() / 2 + mValueX * mWidthParent / BALANCE_VALUE_RANGE;
		pointY = getHeight() / 2 - mValueY * mHeightParent / BALANCE_VALUE_RANGE;
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
			calculatePointerXYPosition(x, y);
			setBalanceValues(pointX, pointY);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			calculatePointerXYPosition(x, y);
			setBalanceValues(pointX, pointY);
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			break;
		}

		return true;// super.onTouchEvent(event);
	}

	// /////////////////////////////
	public interface onBalanceListener {
		public void onChangeValue(int back_front, int left_right);
	}

	private onBalanceListener mOnBalanceListener;

	public void setBalanceListener(onBalanceListener hLisenter) {
		mOnBalanceListener = hLisenter;
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

	public void drawPoint(Canvas canvas) {
		canvas.drawBitmap(pointBp, pointX - pointBp.getWidth() / 2, pointY - pointBp.getHeight()
				/ 2, null);
	}

	private void calculatePointerXYPosition(float x, float y) {
		pointX = x;
		pointY = y;

		if (x < w_ajust) {
			pointX = w_ajust;
		} else if (x > this.getWidth() - w_ajust) {
			pointX = this.getWidth() - w_ajust;
		}

		if (y < y_ajust) {
			pointY = y_ajust;
		} else if (y > this.getHeight() - y_ajust) {
			pointY = this.getHeight() - y_ajust;
		}
	}

}
