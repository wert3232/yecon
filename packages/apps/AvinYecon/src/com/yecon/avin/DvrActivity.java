package com.yecon.avin;

import static android.mcu.McuExternalConstant.*;
import static com.yecon.avin.AVINConstant.*;
import static android.constant.YeconConstants.*;

import java.io.File;
import java.io.IOException;
import com.autochips.inputsource.AVIN;
import com.autochips.inputsource.InputSource;
import com.autochips.settings.AtcSettings;
import com.yecon.avin.AVInActivity.state;
import com.yecon.avin.unitl.L;
import com.yecon.common.SourceManager;
import com.yecon.settings.YeconSettings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.exdevport.*;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.mcu.McuExternalConstant;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DvrActivity extends Activity  implements OnClickListener,OnTouchListener {	
	protected static final String TAG = "DvrActivity";
	private View   bottombar;
	private DvrProtocol dvrProtocal;
	public AVIN  avinV;
	private boolean avinV_signal_on = false;
	private AVINSurfaceView mSurfaceView=null;
	private TextView mAVSingle = null;
	private int mCbmCtlStatus = InputSource.CBM_CONTROL_NONE;
	private int mCurrentStatus = InputSource.STATUS_NONE;
	
	private final int videoport  = AVIN.PORT2;
	YeconSettings.RGBTYPE rgbType = YeconSettings.RGBTYPE.AVIN_2;
	
    //set Video CP
    private AtcSettings.VCP.YUVGain mVcpinfo = null;
    private AtcSettings.VCP.SrcType mVcpSrcType;

	//by lzy add	
	private String aux_tag="persist.sys.yeconAux";
	private int aux_exist;
	
	private String hue_tag="persist.sys.yeconHue";
	private  int hue_values;

	private String saturation_tag="persist.sys.yeconSaturation";
	private  int saturation_values;

	private String contrast_tag="persist.sys.yeconContrast";
	private  int  contrast_values;
	
	//brightness
	private String auxbrightness_tag="persist.sys.auxBrightness";
	private String brightness_tag="persist.sys.yeconBrightness";
	private  int  brightness_values;
	
	private static state eActivityState = state.destroy;

	 //default values
	private static final Integer[] DEFAULT_AUX_VALUES = {50,65,30,60};
	private static final Integer[] DEFAULT_SYS_VALUES = {50,50,20,50};
	private boolean isSetSysPara = true;
	
	private boolean mParkingEnable = false;
    private boolean mMcuIsParking = false;
    private boolean mIsFullScreen = false;
    
	private DisplayManager mDisplayManager;
	Display[] displays;
	
	private LinearLayout mBottomLayout = null;
	private long endDownTime = 0;
	private long endUPTime = 0;
	private boolean mTimeFormat = false;
	
	private AudioManager mAudioManager;
	private McuManager mcuManager;
	
	private AVINCommon.ReceiveThread receiveThread;
	private Object sourceTocken;
	private RearManager rearManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.dvr);
		 SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify() {
				
				@Override
				public void onAudioFocusChange(int arg0) {
					Log.i(TAG, "onAudioFocusChange:" +arg0 );
					switch (arg0) {
	                case AudioManager.AUDIOFOCUS_LOSS:
	                	finish();
	                	break;
	                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: 
	                    //stopTargetPlay();
	                    break;
	                case AudioManager.AUDIOFOCUS_GAIN:
	                    //resumeTargetPlay();
	                    break;
					}
					
				}
			});
		 
		sendBroadcast(new Intent(AVINConstant.ACTION_AVIN_REQUEST_NOTIFY));
		SystemClock.sleep(50);
		
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		this.setContentView(R.layout.activity_dvr);
		bottombar = findViewById(R.id.bottombar);
		avinV = new AVIN();
		mSurfaceView = (AVINSurfaceView) findViewById(R.id.avin_surfaceview);
		mSurfaceView.setOnTouchListener(this);
		mSurfaceView.mAvinV = avinV;
		mBottomLayout = (LinearLayout) findViewById(R.id.bottombar);
		mBottomLayout.setVisibility(View.INVISIBLE);
		mAVSingle = (TextView)findViewById(R.id.av_signal);
		//add for test touch event
		//mAVSingle.setOnTouchListener(this);
		//
		initData();
		
		rearManager= new RearManager(getApplicationContext());
		rearManager.init(avinV, null);
		
		startPlay();
		
		YeconSettings.initVideoRgb(rgbType);
		
		mHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
		mHandler.sendEmptyMessageDelayed(AVINConstant.SHOW_NO_SIGNAL_STATE, AVINConstant.SIG_STATUS_TIME);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		dvrProtocal = new DvrProtocol(this);
		dvrProtocal.init();
		sendDateTimeLater(500);
	}
	
	@SuppressLint("NewApi")
	void initData() {
		
		Display display = getWindowManager().getDefaultDisplay();
		
		AVINConstant.sScreenHeight = display.getHeight();
		AVINConstant.sScreenWidth = display.getWidth();
		mSurfaceView.getLayoutParams();
		LayoutParams lp = mSurfaceView.getLayoutParams();
		lp.height = AVINConstant.sScreenHeight;
		lp.width = AVINConstant.sScreenWidth;
		mSurfaceView.setLayoutParams(lp);
		
		mSurfaceView.mIsRearSurfaceView = false;
		mSurfaceView.getHolder().setFormat(ImageFormat.YV12);
		 
		mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
		displays = mDisplayManager.getDisplays(null);
	
		IntentFilter filter = new IntentFilter();

		filter.addAction(ACTION_QB_POWERON);
		filter.addAction(ACTION_QB_POWEROFF);
		filter.addAction(ACTION_QB_PREPOWEROFF);
		filter.addAction(MCU_ACTION_ACC_ON);
		filter.addAction(MCU_ACTION_ACC_OFF);
		filter.addAction(ACTION_AVIN_REQUEST_NOTIFY);
		filter.addAction("action_lightactivity.stop");
		filter.addAction("action_lightactivity.resume");
		filter.addAction(ACTION_AUX_PORT_SET);
		filter.addAction(MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS);
		
		if (AVINConstant.rdsTest) {
			filter.addAction(AVINConstant.ACTION_RDS_ON);
			filter.addAction(AVINConstant.ACTION_RDS_OFF);
		}
		registerReceiver(mQuickBootListener, filter);
		//myHandler.sendEmptyMessageDelayed(HIDE_TOP_BAR, HIDE_TOP_BAT_TIME);
		
		SystemProperties.set(PROPERTY_KEY_AVIN_TYPE, "aux_type");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	}
	
	private void SetAuxVideoPara(){
		/*
		hue_values = DEFAULT_AUX_VALUES[0];
		saturation_values = DEFAULT_AUX_VALUES[1];
		contrast_values = DEFAULT_AUX_VALUES[2];
		brightness_values = DEFAULT_AUX_VALUES[3];
		
		aux_exist = 1;
		SystemProperties.set(aux_tag, aux_exist+"");

		hue_values =  Integer.parseInt(SystemProperties.get(hue_tag, hue_values+""));
		saturation_values = Integer.parseInt(SystemProperties.get(saturation_tag, saturation_values+""));
		contrast_values = Integer.parseInt(SystemProperties.get(contrast_tag, contrast_values+"")) ;
		brightness_values = Integer.parseInt(SystemProperties.get(auxbrightness_tag, brightness_values+""));
	    
		AtcSettings.Display.SetHueLevel(hue_values);
		AtcSettings.Display.SetSaturationLevel(saturation_values);
		AtcSettings.Display.SetContrastLevel(contrast_values);
		AtcSettings.Display.SetBrightnessLevel(brightness_values);
		*/
	}
	
	private void SetSysVideoPara(){
		/*
		hue_values = DEFAULT_SYS_VALUES[0];
		saturation_values = DEFAULT_SYS_VALUES[1];
		contrast_values = DEFAULT_SYS_VALUES[2];
		brightness_values =  DEFAULT_SYS_VALUES[3];

		aux_exist = 0;
		SystemProperties.set(aux_tag, aux_exist+"");
		
		hue_values = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_HUE, hue_values);
		saturation_values = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_SATURATION, saturation_values);
		contrast_values = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_CONTRAST, contrast_values);	
		brightness_values = Integer.parseInt(SystemProperties.get(brightness_tag, brightness_values+""));
				
		AtcSettings.Display.SetHueLevel(hue_values);
		AtcSettings.Display.SetSaturationLevel(saturation_values);
		AtcSettings.Display.SetContrastLevel(contrast_values);
		AtcSettings.Display.SetBrightnessLevel(brightness_values);
		*/
	}	
	
	void startPlay()
	{
		if (avinV != null) {
			if(rearManager.isRearOpened()){
				avinV.setDestination(InputSource.DEST_TYPE_FRONT_REAR);
			}
			else{
				avinV.setDestination(InputSource.DEST_TYPE_FRONT);
			}			
			avinV.setOnSignalListener(mListenerSignal);
			int retValueVideo = avinV.setSource(InputSource.SOURCE_TYPE_AVIN, videoport ,AVIN.PORT_NONE,0);
			if ((InputSource.ERR_FAILED == retValueVideo)) {
				L.d(TAG, "setSource()&&&&&&&&&&&&&&: L_FAILED!");
				finish();
				System.exit(0);
			}
			
			avinV.play();
		}
	}
	
	public void stopTargetPlay() {
		L.d(TAG, "stopTargetPlay");
		if (avinV != null) {
			avinV.stop();
		}

		mCurrentStatus = InputSource.STATUS_STOPED;
		
	}
	
	public void resumeTargetPlay() {
		L.d(TAG, "resumeTargetPlay");
		if (mCurrentStatus == InputSource.STATUS_STARTED) {
			L.d(TAG, "resumeTargetPlay already started");
			return;
		}
		if (avinV != null) {
			avinV.play();
		}
		mCurrentStatus = InputSource.STATUS_STARTED;
	}
	
	
	protected void onResume() {
		SourceManager.acquireSource(sourceTocken);
		YeconSettings.initVideoRgb(rgbType);
	    mParkingEnable = SystemProperties.getBoolean("persist.sys.parking_enable",
                false);
        mMcuIsParking = SystemProperties.getBoolean("persist.sys.mcu_parking", false);
		eActivityState = state.resume;
		isSetSysPara = true;
		super.onResume();
	}
	
	
	protected void onPause() {	
		eActivityState = state.pause;
		mHandler.removeMessages(AVINConstant.HIDE_TOP_BAR);
		if (isFinishing()) {
			L.d(TAG, "onPause was cause by finish!!!");
			// myHandler.sendEmptyMessage(RELEASE_RESOURCE);
		}
		super.onPause();
	}
	
	@Override
	protected void onRestart() {
		eActivityState = state.restart;
		L.d(TAG, "onRestart!!!");
		super.onRestart();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		eActivityState = state.restart;
		L.d(TAG, "onStart!!!");
		super.onStart();
	}

	@Override
	protected void onStop() {
		eActivityState = state.stop;
		mHandler.removeMessages(AVINConstant.SET_AUX_VIDEO_PARA);
		//by lzy
		if(isSetSysPara && avinV_signal_on)
		{
		   SetSysVideoPara();
		   isSetSysPara = false;
		}
		YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//receiveThread.exit();
		
		eActivityState = state.destroy;
		L.d(TAG, "onDestroy(): Front HashCode " + this.hashCode() + "TaskId: "
				+ getTaskId());
		// AllServiceControl.mBackCarListenList.remove(this);
		unregisterReceiver(mQuickBootListener);
		mHandler.removeMessages(AVINConstant.PROGRESS_CHANGED);
		mHandler.removeMessages(AVINConstant.HIDE_CONTROLER);
		mHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
		showSurfaceView(View.INVISIBLE);
		rearManager.release();
		DestroyAvin();
		
		dvrProtocal.release();
		
		super.onDestroy();
		SourceManager.unregisterSourceListener(sourceTocken);
	}
	

	private void showToolBar(boolean show, boolean toogle){
		if(toogle){
			if(bottombar.getVisibility()==View.VISIBLE){
				show = false;
			}
			else{
				show = true;
			}
		}
		
		if(show){
			AVINCommon.switchFullScreen(getWindow(), false);
			if(bottombar.getVisibility()!=View.VISIBLE){/*
				TranslateAnimation tAnim = new TranslateAnimation(0, 0, bottombar.getHeight(), 0);
				tAnim.setDuration(200);
				//tAnim.setInterpolator(new OvershootInterpolator());
				tAnim.setAnimationListener(new AnimationListener(){

					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						bottombar.clearAnimation();
						bottombar.setVisibility(View.VISIBLE);		
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
					}
					
				});
				bottombar.clearAnimation();
				bottombar.startAnimation(tAnim);*/
			}
			
		}
		else{
			
			AVINCommon.switchFullScreen(getWindow(), true);
			if(bottombar.getVisibility()==View.VISIBLE){
				TranslateAnimation tAnim = new TranslateAnimation(0, 0, 0, bottombar.getHeight());
				tAnim.setDuration(500);
				tAnim.setInterpolator(new OvershootInterpolator());
				tAnim.setAnimationListener(new AnimationListener(){

					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						bottombar.clearAnimation();
						bottombar.setVisibility(View.INVISIBLE);		
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
					}
					
				});
				bottombar.clearAnimation();
				bottombar.startAnimation(tAnim);
			}
		}
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		/*

		switch (arg0.getId()) {
		case R.id.avin_surfaceview:
			showToolBar(true, true);
			break;
		case R.id.ivMenu:
			try {
				mSerialPort.SendData(AVINConstant.DVR_KEYCOD_MENU);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.ivNext:
			try {
				mSerialPort.SendData(AVINConstant.DVR_EKYCODE_DW);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.ivPrevious:
			try {
				mSerialPort.SendData(AVINConstant.DVR_KEYCODE_UP);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.ivMode:
			try {
				mSerialPort.SendData(AVINConstant.DVR_KEYCDOE_MODE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.ivOk:
			try {
				mSerialPort.SendData(AVINConstant.DVR_KEYCODE_OK);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		*/
	
	}
	
	
	private void showSurfaceView(int visibility) {
		//always visible , by chenwl
		//return;
		
		if (mSurfaceView != null && mSurfaceView.getVisibility()!=visibility) {
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.color.background);
            mSurfaceView.setBackgroundDrawable(drawable);   			
            mSurfaceView.setVisibility(visibility);

		}
		
		
		if (visibility == View.VISIBLE) {
		    if (mParkingEnable && !mMcuIsParking) {
		        SetSysVideoPara();
                /*mBottomLayout.setVisibility(View.VISIBLE);*/
            } else {
                SetAuxVideoPara();
               /* mBottomLayout.setVisibility(View.INVISIBLE);*/
            }
		} else {
			/*mBottomLayout.setVisibility(View.INVISIBLE);*/
		}
	}



	private AVIN.OnSignalListener mListenerSignal = new AVIN.OnSignalListener() {
		public void onSignal(int msg, int param1, int param2) {
			L.i(TAG, "onSignal msg:" + msg);
			switch (msg) {
			case AVIN.SIGNAL_READY:
				L.i(TAG, "..............................SIGNAL_READY");
				avinV_signal_on = true;
				mHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
				if (mAVSingle != null) {
					mAVSingle.setVisibility(View.INVISIBLE);
				}
				if (mSurfaceView != null) {
				    Log.d(TAG,  "............... param1 =" + param1 +  "...............param2 +" + param2);
				    mSurfaceView.SetAVINSignalType(param1, param2);
				}
				rearManager.setAvinSignalType(param1, param2);
				mHandler.sendEmptyMessage(AVINConstant.SHOW_BT_SURFACEVIEW);
				break;
			case AVIN.SIGNAL_LOST:
				L.i(TAG, "..............................SIGNAL_LOST");
				avinV_signal_on = false;
				mHandler.removeMessages(AVINConstant.SHOW_BT_SURFACEVIEW);
				mHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
				mHandler.sendEmptyMessageDelayed(AVINConstant.SHOW_NO_SIGNAL_STATE,  2000);
				break;
	
			default:
				return;
			}
		}
	};
	
	
	
	@SuppressLint("HandlerLeak")
	Handler  mHandler = new Handler(){
	public void handleMessage(Message msg) {
		
			switch (msg.what) {
			case AVINConstant.SHOW_NO_SIGNAL_STATE:{
					mAVSingle.setVisibility(View.VISIBLE);
					showSurfaceView(View.INVISIBLE);
					mIsFullScreen = false;
					rearManager.onSignal(false);
					/*mBottomLayout.setVisibility(View.VISIBLE);*/
					
					WindowManager.LayoutParams params = getWindow().getAttributes();
		            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		            getWindow().setAttributes(params);
		            
					//by lzy
					SetSysVideoPara();
					Intent intent=new Intent("aux.signal.lost");
					sendBroadcast(intent);
				
				}
				break;
			case AVINConstant.SHOW_TOP_BAR:
			{
				mIsFullScreen = false;
			/*	mBottomLayout.setVisibility(View.VISIBLE);*/
				//mAvinsurfaceview4Front.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);   
				WindowManager.LayoutParams params = getWindow().getAttributes();
	            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	            getWindow().setAttributes(params);
			}
				break;
			case AVINConstant.HIDE_TOP_BAR:
			{
				mIsFullScreen = true;
				if (mAVSingle.isShown()) {
					mIsFullScreen = false;
				/*	mBottomLayout.setVisibility(View.VISIBLE);*/
					//mAvinsurfaceview4Front.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);   
					WindowManager.LayoutParams params = getWindow().getAttributes();
		            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		            getWindow().setAttributes(params);
				} else {
					mIsFullScreen = true;
				/*	mBottomLayout.setVisibility(View.INVISIBLE);*/
					//mAvinsurfaceview4Front.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
					WindowManager.LayoutParams params = getWindow().getAttributes();
		            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		            getWindow().setAttributes(params);
				}
			}
				break;
			case AVINConstant.SHOW_BT_SURFACEVIEW:
				if (!avinV_signal_on) {
					mAVSingle.setVisibility(View.VISIBLE);
					showSurfaceView(View.INVISIBLE);
	
				} else {
					if (mIsFullScreen == false) {// if full screen then show topbar or display
						mHandler.removeMessages(AVINConstant.HIDE_TOP_BAR);
						mHandler.sendEmptyMessageDelayed(AVINConstant.HIDE_TOP_BAR,
								AVINConstant.HIDE_TOP_BAT_TIME);
					}

					mAVSingle.setVisibility(View.INVISIBLE);
					showSurfaceView(View.VISIBLE);
				}
				
				rearManager.onSignal(avinV_signal_on);

				//by lzy
				mHandler.sendEmptyMessageDelayed(AVINConstant.SET_AUX_VIDEO_PARA,  500);
				
				Intent intent1 = new Intent("aux.signal.ready");
				sendBroadcast(intent1);	
				break;
			case AVINConstant.SET_AUX_VIDEO_PARA:{
					
				if(avinV_signal_on && eActivityState == state.resume)
				{
				    if (mParkingEnable && !mMcuIsParking) {
				        
				    } else {
				        SetAuxVideoPara();
				    }
				}
			}
				break;
			case AVINConstant.SEND_DVR_TIME:
			{
				dvrProtocal.sendTime();
				dvrProtocal.sendDate();
				this.sendEmptyMessageDelayed(AVINConstant.SEND_DVR_TIME, 5000);
			}
				break;
			
			default:
				break;
			}
		
		
		}
	};
	
	private void sendDateTimeLater(long deleyMillis){
		mHandler.removeMessages(AVINConstant.SEND_DVR_TIME);
		mHandler.sendEmptyMessageDelayed(AVINConstant.SEND_DVR_TIME, deleyMillis);
	}
	
	BroadcastReceiver mQuickBootListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			final String action = intent.getAction();
			if (null == action) {
				L.i(TAG, "mQuickBootListener - action is null");
				return;
			}
			if (ACTION_QB_POWERON.equals(action)
					|| MCU_ACTION_ACC_ON.equals(action)) {
				L.i(TAG, "mQuickBootListener - ACTION_QB_POWERON received");
				L.i(TAG, "mQuickBootListener resume AVIN Playback begin");
				resumeTargetPlay();
				L.i(TAG, "mQuickBootListener resume AVIN end");
			} else if (ACTION_QB_POWEROFF.equals(action)
			        || ACTION_QB_PREPOWEROFF.equals(action)
					|| MCU_ACTION_ACC_OFF.equals(action)) {
				L.i(TAG, "mQuickBootListener - ACTION_QB_POWEROFF received");
				L.i(TAG, "mQuickBootListener stop AVIN Playback begin");
				//stopTargetPlay();
				finish();
				L.i(TAG, "mQuickBootListener stop AVIN end");
			}
			else if (AVINConstant.rdsTest) {
			/*	if (AVINConstant.ACTION_RDS_ON.equals(action)) {
					L.i(TAG, "RDS ON received");
					if (avinA != null) {
						avinA.setSource(InputSource.SOURCE_TYPE_RDS_AUD,
								AVIN.PORT_NONE, audioport, 0);
					}
				} */
			} 
			else if (ACTION_AVIN_REQUEST_NOTIFY.equals(action)) {
				//isReleaseAvin = true;
				Log.i(TAG, "ACTION_AVIN_REQUEST_NOTIFY");
				finish();
			}
			else if("action_lightactivity.stop".equals(action))
			{
				isSetSysPara = true;				
				if(avinV_signal_on && eActivityState == state.stop)
				{
					SetSysVideoPara();
				}
			}
			else if("action_lightactivity.resume".equals(action))
			{			
				if(avinV_signal_on && eActivityState == state.stop)
				{
					mHandler.sendEmptyMessageDelayed(AVINConstant.SET_AUX_VIDEO_PARA, AVINConstant.SET_AUX_DELAY_TIME);
				}
			}
			else if(ACTION_AUX_PORT_SET.equals(action))
			{
				if(eActivityState == state.stop)
				{
					//SetAuxPort();
					avinV_signal_on = false;
					mHandler.sendEmptyMessage(AVINConstant.SET_INPUTSOURCE);
					mHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
					mHandler.sendEmptyMessageDelayed(AVINConstant.SHOW_NO_SIGNAL_STATE,
							AVINConstant.SIG_STATUS_TIME);			
				}

			} else if (MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS.equals(action)) {
			    int brakeStatus = intent.getIntExtra(McuExternalConstant.INTENT_EXTRA_BRAKE_STATUS, 0);
			       mMcuIsParking = (brakeStatus == 0) ? true : false;
			    if (avinV_signal_on) {
	                if (mParkingEnable && !mMcuIsParking) {
	                    SetSysVideoPara();
	                   /*mBottomLayout.setVisibility(View.VISIBLE);*/
	                } else {
	                    SetAuxVideoPara();
	                    /*mBottomLayout.setVisibility(View.INVISIBLE);*/
	                }
			    }
			}
		}
	};
	
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			if (mIsFullScreen == true) {// if full screen then show top bar or
										// display
				// mHandler.removeMessages(AVINConstant.HIDE_TOP_BAR);
				// mHandler.sendEmptyMessage(AVINConstant.SHOW_TOP_BAR);
				mIsFullScreen = false;
			} else {
				// mHandler.removeMessages(AVINConstant.SHOW_TOP_BAR);
				// mHandler.removeMessages(AVINConstant.HIDE_TOP_BAR);
				// mHandler.sendEmptyMessage(AVINConstant.HIDE_TOP_BAR);
				mIsFullScreen = true;
			}

			mHandler.removeMessages(AVINConstant.HIDE_TOP_BAR);
			mHandler.sendEmptyMessage(AVINConstant.SHOW_TOP_BAR);

			// static long endTime = 0;
			if ((System.currentTimeMillis() - endDownTime) > 50) {
				Log.i(TAG, "touch down event.");
				sendDateTimeLater(2000);
				dvrProtocal.sendTouchEvent(MotionEvent.ACTION_DOWN,
						(int) event.getX(), (int) event.getY());
				endDownTime = System.currentTimeMillis();
			}
		}

			break;

		case MotionEvent.ACTION_UP: {
			if (mIsFullScreen == false) {// if full screen then show top bar or
											// display
				mHandler.sendEmptyMessageDelayed(AVINConstant.HIDE_TOP_BAR,
						AVINConstant.HIDE_TOP_BAT_TIME);
			}

			if ((System.currentTimeMillis() - endUPTime) > 50) {
				sendDateTimeLater(2000);
				dvrProtocal.sendTouchEvent(MotionEvent.ACTION_UP,
						(int) event.getX(), (int) event.getY());
				endUPTime = System.currentTimeMillis();
			}			
		}

			break;
		}

		return true;
	}
	
	public void DestroyAvin() {
		if (avinV != null) {
			avinV.setDisplay(null);
			avinV.setRearDisplay(null);
			avinV.stop();
			avinV.release();
			avinV = null;
		}
	}
	
	
}
