package com.yecon.carsetting;

import com.yecon.carsetting.unitl.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawRound extends View {

	private int progress;

	public DrawRound(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		canvas.drawColor(Color.BLACK);
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		canvas.drawCircle(500, 100, Util.pos_threshold, paint);

	}
}
