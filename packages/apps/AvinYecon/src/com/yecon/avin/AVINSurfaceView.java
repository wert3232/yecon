package com.yecon.avin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.autochips.inputsource.InputSource;
import com.autochips.inputsource.InputSourceClient;
import com.yecon.avin.unitl.L;

public class AVINSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private String TAG = "AVINSurfaceView";
	
    private final static int PALWIDTH = 720;
    private final static int PALHEIGHT = 576;
    private final static int RECTLEFT = 0;
    private final static int RECTTOP = 2;
    
	public Context mContext;
	public SurfaceHolder mSurfaceHolder = null;
	public Surface surface = null;
	public boolean mIsRearSurfaceView = false;
	// sync with InputSourceBase.h
	public static final int VDO_RESOLUTION_720_576 = 0x01;
	public static final int VDO_RESOLUTION_720_480 = 0x02;
	public static final int VDO_RESOLUTION_640_480 = 0x03;
	public static final int VDO_RESOLUTION_800_600 = 0x04;
	public static final int VDO_RESOLUTION_1440_480 = 0x05;
	public static final int VDO_RESOLUTION_1440_576 = 0x06;
	public static final int VDO_RESOLUTION_1280_720 = 0x07;
	public static final int VDO_RESOLUTION_1920_1080 = 0x08;

	private Thread t;
	private volatile boolean flag;
	private Canvas mCanvas;
	private int mResolutionType = 0;
	public InputSourceClient mAvinV = null;
	public InputSourceClient mAvinA = null;

	private int mWidth = PALWIDTH;
	private int mHeight = PALHEIGHT;

	public AVINSurfaceView(Context context) {
		super(context);
		mContext = context;
		initAVINView(context);
	}

	public AVINSurfaceView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
		initAVINView(context);
	}

	public AVINSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initAVINView(context);
	}

	@SuppressWarnings("deprecation")
	public void initAVINView(Context context) {
		Log.i(TAG, "initAVINView");
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
	}


	
	public void SetAVINSignalType(int width, int height) {
       if (mWidth == width && mHeight == height) {
            Log.i(TAG,"SetAVINSignalType : resolution have not been changed!");
       } else {
            mWidth = width;
            mHeight = height;
            
           if (mSurfaceHolder != null) {
               mSurfaceHolder.setFixedSize(mWidth,mHeight);
               Log.i(TAG,"resolution have been changed!");
               //mAvinV.setDisplay(mSurfaceHolder);
               //mAvinV.setSourceRect(InputSource.DEST_TYPE_FRONT, RECTLEFT, RECTTOP, mWidth, (mHeight - RECTTOP));
            }
       }
	}

	protected void OnDrawColor(int color) {
		if (mSurfaceHolder != null) {
			Canvas canvas = mSurfaceHolder.lockCanvas();
			if (canvas != null) {
				L.v("AVINSurfaceView....lzy.....OnDrawColor..........");
				canvas.drawColor(color, Mode.CLEAR);
				// canvas.drawColor(R.color.background, Mode.CLEAR);
				mSurfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.d(TAG, "call surfaceChanged");
	}

		
    public void surfaceCreated(SurfaceHolder holder) {
        Log.w(TAG, "----mSHCallback----surfaceCreated,holder="+holder);
        mSurfaceHolder = holder;
		
        if (mSurfaceHolder != null) {
            surface = mSurfaceHolder.getSurface();
            Log.w(TAG, "----mSHCallback----surfaceCreated,mWidth = " + mWidth + ", mHeight = " + mHeight);
            mSurfaceHolder.setFixedSize(mWidth, mHeight);
           
            if (surface != null  && mAvinV != null) {
                try{
                    if (false == mIsRearSurfaceView) {
                        Log.d(TAG,"set display will be called  mSurfaceHolder " +mSurfaceHolder);
                        mAvinV.setDisplay(mSurfaceHolder);
                        mAvinV.setSourceRect(InputSource.DEST_TYPE_FRONT, RECTLEFT, RECTTOP, mWidth, (mHeight - RECTTOP));
                    } else {                       
                    	Log.d(TAG,"setRearDisplay  mSurfaceHolder ");
                        mAvinV.setRearDisplay(mSurfaceHolder);
                    }
                }catch(Exception e){
                    Log.e(TAG,"expection ="+e);
                }
            }else {
                Log.e(TAG,"surface is null or mAvinV is null!!");
                if (surface == null) {
                Log.e(TAG,"surface is null!!");
            }
            }
        } else {
            surface = null;
            Log.e(TAG, "SurfaceView is null!!");
            Log.e(TAG,"Create AVIN SurfaceView Fail!!");
        }
    }

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.w(TAG, "----mSHCallback----surfaceDestroyed");

		if (mAvinV == null) {
			Log.w(TAG, "surfaceDestroyed mAvinV is null");
			mSurfaceHolder = null;
			surface = null;
			return;
		}
		if (false == mIsRearSurfaceView) {
			mSurfaceHolder = null;
			surface = null;
			mAvinV.setDisplay(null);
		} else {
			mAvinV.setRearDisplay(null);
		}
	}

}
