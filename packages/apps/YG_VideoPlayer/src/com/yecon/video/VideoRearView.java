package com.yecon.video;

import android.annotation.SuppressLint;
import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class VideoRearView extends Presentation {

	private static final String TAG = "VideoRearView";

	private SurfaceView mRearSurfaceView;

	public VideoRearView(Context outerContext, Display display) {
		super(outerContext, display);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
        Window win = getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.type = WindowManager.LayoutParams.TYPE_DISPLAY_OVERLAY;
        
		setContentView(R.layout.yecon_video_player_rear);

		mRearSurfaceView = (SurfaceView) findViewById(R.id.rearSurface);
		Log.e(TAG, "onCreate:" + mRearSurfaceView);
		if (mRearSurfaceView != null) {
			mRearSurfaceView.setBackgroundResource(android.R.color.transparent);
			mRearSurfaceView.setVisibility(View.VISIBLE);
		}
	}

	public SurfaceView getSurfaceView() {
		return mRearSurfaceView;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e(TAG, "onStop");
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		Log.e(TAG, "finalize");
	}
	
	
}
