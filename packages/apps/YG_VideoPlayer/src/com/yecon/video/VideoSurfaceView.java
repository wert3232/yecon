package com.yecon.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

public class VideoSurfaceView extends SurfaceView {
	
	private static final String TAG = "VideoSurfaceView";

	public VideoSurfaceView(Context context) {
		super(context);
		init();
	}
	
	public VideoSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		// TODO Auto-generated method stub
		super.onWindowVisibilityChanged(View.VISIBLE);
		Log.e(TAG, "onWindowVisibilityChanged");
	}

	@Override
	public void setVisibility(int visibility) {
		// TODO Auto-generated method stub
		super.setVisibility(visibility);
		Log.e(TAG, "setVisibility");
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		Log.e(TAG, "dispatchDraw");
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Log.e(TAG, "onDraw");
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
		Log.e(TAG, "draw");
	}

}
