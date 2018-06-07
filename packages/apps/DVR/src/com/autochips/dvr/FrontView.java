package com.autochips.dvr;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class FrontView extends SurfaceView implements Callback {
	private static final String TAG = "dvr_8317";
	private static final boolean DEBUG = true;
	
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;
    private DVR mDvr = null;

	public FrontView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

    public void setDvr(DVR dvr){
        mDvr = dvr;
    }

	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated set surface");
		if(0 != (MainActivity.mDvrState & MainActivity.STATE_PREVIEW)) {
			if ( 0 == (MainActivity.mDvrState & MainActivity.STATE_CAMERA_DETATCH)) {
				mDvr.SetFrontSurface(holder);
				Log.i(TAG, "surfaceCreated set surface start preview");
				mDvr.StartPreview();
			}			
		}
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		holder.setFixedSize(w, h);
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed set surface NULL");
		if ( 0 == (MainActivity.mDvrState & MainActivity.STATE_CAMERA_DETATCH)) {
			mDvr.StopPreview();
			mDvr.SetFrontSurface(null);
		}		
		
		holder.getSurface().release();
	}
}
