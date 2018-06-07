package com.can.ui.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * ClassName:FuelProgressBar
 * 
 * @function:TODO
 * @author Kim
 * @Date:  2016-6-25 上午11:42:44
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class FuelProgressBar extends ProgressBar{

	public FuelProgressBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FuelProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FuelProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.rotate(-90); 
		canvas.translate(-getHeight(), 0);
		invalidate();
		super.onDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(h, w, oldw, oldh);
	}
}
