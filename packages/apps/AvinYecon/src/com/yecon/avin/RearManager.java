package com.yecon.avin;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.app.Presentation;
import com.autochips.inputsource.AVIN;
import com.autochips.inputsource.InputSource;

public class RearManager {
	private static final String TAG = "RearManager";
	private boolean mIsRearSupport = false, mIsRearOn = false;
	private DisplayManager mDisplayManager;
	private Display[] mDisplays;
	private DemoPresentation presentation;
	private Context context;
	private AVIN avinV, avinA;
	private static AVINSurfaceView mAvinsurfaceview4Rear = null;
	private boolean signalOn = false;
	private TextView tvSignalOn;
	private static final int MIN_DISPLAY_NUM = 2;
	
	RearManager(Context context){
		this.context = context;
	}
	
	public boolean isRearOpened(){
		return mIsRearOn;
	}
    
	public void init(AVIN avinV, AVIN avinA){
		if(!SystemProperties.getBoolean("persist.sys.cvbs_enable", true)){
			return;
		}
		
		if(context==null){
			Log.w(TAG, "context == null");
			return;
		}
		String value = SystemProperties.get("atc.rearproperty", "false");
		if (("false").equals(value)) {
			Log.e(TAG, "atc.rearproperty  == false, not support rear display");
			mIsRearSupport =  false;
			return;
        }
		else{
			mIsRearSupport = true;
		}
		this.avinV = avinV;
		this.avinA = avinA;
		mDisplayManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE);
		mDisplays = mDisplayManager.getDisplays(null);
		switchRear(true);
	}
	
	public void release(){
		hidePresentation(null);
	}
	
	 private void hidePresentation(Display display) {
        if (presentation == null) {
            return;
        }
        Log.d(TAG, "Dismissing presentation on display #");
        presentation.dismiss();
        if (avinV!= null) {
        	avinV.setRearDisplay(null);
        }
    }
	 
	public void setAvinSignalType(int width, int height){
		if (mAvinsurfaceview4Rear != null) {
			mAvinsurfaceview4Rear.SetAVINSignalType(width, height);
			Log.i(TAG, "Rear Video Show: Video Size: " + width + "X"
					+ height);
		}
	}
	
	public void onSignal(boolean on) {
		signalOn = on;
		if (on) {
			if (mAvinsurfaceview4Rear != null) {
				tvSignalOn.setVisibility(View.INVISIBLE);
				mAvinsurfaceview4Rear.setVisibility(View.VISIBLE);
			} else {
				//Log.e(TAG, "mAvinsurfaceview4Rear is Null!!!!");
			}
		} else {
			if (mAvinsurfaceview4Rear != null) {
				tvSignalOn.setVisibility(View.VISIBLE);
				mAvinsurfaceview4Rear.setVisibility(View.INVISIBLE);
			} else {
				//Log.e(TAG, "mAvinsurfaceview4Rear is Null!!!!");
			}
		}
	}
	 
	 private void showPresentation(Display display) {
	        Log.d(TAG,"showPresentation");
	        
	        presentation = new DemoPresentation(context, display);

	        if (presentation != null) {
	            presentation.show();
	            tvSignalOn = (TextView) presentation.findViewById(R.id.av_signal);
	            if (signalOn == true) {	            	
	            	tvSignalOn.setVisibility(View.INVISIBLE);
	            } else  {
	            	tvSignalOn.setVisibility(View.VISIBLE);
	            }
	        }
	    }
	 

    private void switchRear(boolean open) {
         if(!mIsRearSupport){
        	 Log.w(TAG, "Rear display not supported");
        	 return;
         }
         for (Display display : mDisplays) {
             Log.d(TAG, "  " + display);
         }

         if (!open && mIsRearOn) {
             try {
                  Log.d(TAG,"Hide Presentation");
                  if (mDisplays.length >= MIN_DISPLAY_NUM) {
                      hidePresentation(mDisplays[1]);
                      mIsRearOn = false;
                      if(avinV!=null){
                    	  avinV.setDestination(InputSource.DEST_TYPE_FRONT);
                      }                      
                      if(avinA!=null){
                    	  avinA.setDestination(InputSource.DEST_TYPE_FRONT);
                      }                      
                  } else {
                      Log.e(TAG,"Do not have rear display!!");
                  }
              } catch (Exception e){
                  Log.e(TAG,"hide presentation exception = " + e);
              }

         } else if(!mIsRearOn){

            try {
                 Log.d(TAG,"Show Presentation");

                 if (mDisplays.length >= MIN_DISPLAY_NUM) {
                     showPresentation(mDisplays[1]);
                     mIsRearOn = true;
                     if(avinV!=null){
                   	  	avinV.setDestination(InputSource.DEST_TYPE_FRONT_REAR);
                     } 
                     if(avinA!=null){
                   	  	avinA.setDestination(InputSource.DEST_TYPE_FRONT_REAR);
                     }
                 } else {
                     Log.e(TAG,"Do not have rear display!!");
                 }
             }catch (Exception e) {
                 Log.e(TAG,"show presentation exception = " + e);
             }
         }
    }
    
    private final class DemoPresentation extends Presentation {
        public DemoPresentation(Context context, Display display) {
            super(context, display);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // Be sure to call the super class.
            Log.d(TAG,"showPresentation  onCreate");
            super.onCreate(savedInstanceState);

            Window win = getWindow();
            WindowManager.LayoutParams params = win.getAttributes();

            params.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 26;
            // Inflate the layout.
            Log.d(TAG,"setContentView");
            setContentView(R.layout.rear_layout);

            mAvinsurfaceview4Rear = (AVINSurfaceView) findViewById(R.id.avin_surfaceview);
            mAvinsurfaceview4Rear.getHolder().setFormat(ImageFormat.YV12);
            mAvinsurfaceview4Rear.mIsRearSurfaceView = true;
            mAvinsurfaceview4Rear.mAvinA = avinA;
            mAvinsurfaceview4Rear.mAvinV = avinV;

        }
    }
}
