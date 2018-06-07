package com.yecon.sound.setting.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yecon.sound.setting.R;
import com.yecon.sound.setting.unitl.Tag;

public class PanView extends View {

	private Bitmap RotatePointer, pan_centerpoint;
	private double rotate;
	private double angle;
	private View mView;

	public PanView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPaints();

	}

	public void onChangeAngleValue(float value) {
		rotate = value;
		invalidate();
	}

	private void initPaints() {
		RotatePointer = BitmapFactory.decodeResource(getResources(), R.drawable.pan_pointer);
		pan_centerpoint = BitmapFactory.decodeResource(getResources(), R.drawable.pan_centerpoint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.rotate((float) rotate, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
		canvas.drawBitmap(RotatePointer, 0, 0, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// return super.onTouchEvent(event);
		if (Math.hypot(event.getX() - getMeasuredWidth() / 2,
				getMeasuredHeight() / 2 - event.getY()) > getMeasuredHeight() / 2
				|| Math.hypot(event.getX() - getMeasuredWidth() / 2, getMeasuredHeight() / 2
						- event.getY()) < pan_centerpoint.getHeight() / 2) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_DOWN:
			angle = (double) (event.getX() - getMeasuredWidth() / 2)
					/ (getMeasuredHeight() / 2 - event.getY());
			rotate = (float) Math.atan(angle) / Math.PI * 180;
			if (event.getY() > getMeasuredHeight() / 2) {
				if (event.getX() > getMeasuredWidth() / 2) {
					rotate += 180;
				} else {
					rotate -= 180;
				}
			}

			int offset = 10;
			if (rotate <= -Tag.RANGLE && rotate > -Tag.RANGLE - offset) {
				rotate = -Tag.RANGLE;
			} else if (rotate >= Tag.RANGLE && rotate < Tag.RANGLE + offset) {
				rotate = Tag.RANGLE;
			}

			if (Math.abs(rotate) <= Tag.RANGLE) {
				mRotateListener.onChangeRotateAngle(mView, (float) rotate);
				invalidate();
			}
			// Log.i("lzy","...............................X="+event.getX());
			// Log.i("lzy","...............................Y="+event.getY());
			Log.i("lzy", "...............................rotate=" + rotate);
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return true;
	}

	public interface onRotateListener {
		public void onChangeRotateAngle(View view, float value);
	}

	static onRotateListener mRotateListener;

	public void setRotateListener(PanView view, onRotateListener hListener) {

		mView = view;
		mRotateListener = hListener;
	}

}
