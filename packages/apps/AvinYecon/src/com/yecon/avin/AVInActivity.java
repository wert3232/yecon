package com.yecon.avin;

import android.app.Activity;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autochips.inputsource.AVIN;
import com.autochips.inputsource.InputSource;
import com.yecon.avin.unitl.L;
import com.yecon.common.SourceManager;
import com.yecon.common.SourceManager.SRC_ACTION;
import com.yecon.settings.YeconSettings;

import static android.mcu.McuExternalConstant.*;
import static com.yecon.avin.AVINConstant.*;
import static android.constant.YeconConstants.*;

public class AVInActivity extends Activity implements View.OnClickListener,
		View.OnTouchListener {
	final static String TAG = "AVINActivity";
	final static String TAG_AUX_PORT="persist.sys.auxport";

	private int aux_port = 2;
	
	enum state{create,resume,start,restart,pause,stop,destroy};
	private static state eActivityState = state.destroy;
	private static AVINSurfaceView mAvinsurfaceview4Front = null;
	// private static AVINSurfaceView mAvinsurfaceview4Rear = null;
	
	private LinearLayout mParkingLayout = null;
	
	private YeconSettings.RGBTYPE rgbType;
	private int videoport;
	private int audioport;
	//private View mTopPanelView = null;
	//private View mBottomPanelView = null;

	//private boolean mIsControllerShow = true;
	private boolean mIsFullScreen = false;
	private int mDestDir = InputSource.DEST_TYPE_FRONT;

	private int mCbmCtlStatus = InputSource.CBM_CONTROL_NONE;
	private int mCurrentStatus = InputSource.STATUS_NONE;

	boolean mIsAvSwitchOpen = false;
	boolean mPreIsHome = false;
	LinearLayout top_img_parent = null;
	TextView avin_light_TView, avin_switch_TView, avin_sound_TView,
			avin_back_TView, avin_close_view;// ,avin_switch_tips ;
	boolean bIsOffRear = true;
	final String show_rear = "show";
	final String hide_rear = "hide";

	TextView av_signal = null;
	TextView av_signal_rear = null;
	public static boolean avinV_signal_on = false;
	boolean mPip_tag = false;


	public static String MTKSettings = "com.android.color_preferences";
	private static final String SATURATION = "saturation";
	final static String packageName = "com.android.settings";
	
	public AVIN avinV;
	public AVIN avinA;
	// for rds test
	private static boolean rdsTest = false;
	private static int sScreenWidth = 0;
	private static int sScreenHeight = 0;

	private DisplayManager mDisplayManager;
	Display[] displays;
	private Object mCbmLock = new Object();
	// switch the front and back avin
	private boolean isFrontAvin = true;
	/** Called when the activity is first created. */
	
	private boolean isSetSysPara = true;
	
	private boolean mParkingEnable = false;
    private boolean mMcuIsParking = false;
    
    private boolean mNeedMute = false;
    
    private AudioManager mAudioManager;
    
    private Object sourceTocken;
    
    private RearManager rearManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.avin);
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
	                	pauseAudio();
	                    break;
	                case AudioManager.AUDIOFOCUS_GAIN:
	                	resumeAudio();
	                    break;
					}
					
				}
			});
		sendBroadcast(new Intent(AVINConstant.ACTION_AVIN_REQUEST_NOTIFY));
		SystemClock.sleep(50);
	
		setContentView(R.layout.main);
		
		mNeedMute = true;
		eActivityState = state.create;
		initView();		
		initData();	
		myHandler.sendEmptyMessageDelayed(SHOW_NO_SIGNAL_STATE, AVINConstant.SIG_STATUS_TIME);

		avinV = new AVIN();
		avinA = new AVIN();
		
		SetAuxPort();
		YeconSettings.initVideoRgb(rgbType);
		
		rearManager = new RearManager(this.getApplicationContext());
		rearManager.init(avinV, avinA);
		
		startPlay(videoport, audioport);
		myHandler.removeMessages(SHOW_NO_SIGNAL_STATE);
		myHandler.sendEmptyMessageDelayed(SHOW_NO_SIGNAL_STATE, AVINConstant.SIG_STATUS_TIME);
		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
	}

	private void SetAuxPort()
	{
	    // aux_port = Integer.parseInt(SystemProperties.get(TAG_AUX_PORT, aux_port + ""));
        // if (aux_port == 1)
        // {
        // videoport = 2;
        // audioport = 1;
        // rgbType = YeconSettings.RGBTYPE.AVIN_2;
        // }
        // else if (aux_port == 2)
        // {
        // videoport = 3;
        // audioport = 2;
        // rgbType = YeconSettings.RGBTYPE.AVIN_3;
        // }

        videoport = 2;
        audioport = 1;
        rgbType = YeconSettings.RGBTYPE.AVIN_2;	
	}


	void initView() {
		av_signal = (TextView) findViewById(R.id.av_signal); 
																
		av_signal.setVisibility(View.INVISIBLE);
		top_img_parent = (LinearLayout) findViewById(R.id.top_img);
		avin_light_TView = (TextView) findViewById(R.id.avin_light_view);
		avin_sound_TView = (TextView) findViewById(R.id.avin_sound_view);
		avin_back_TView = (TextView) findViewById(R.id.avin_back_view);
		avin_switch_TView = (TextView) findViewById(R.id.avin_switch_view);
		avin_close_view = (TextView) findViewById(R.id.avin_close_view);
		avin_light_TView.setOnClickListener(this);
		avin_sound_TView.setOnClickListener(this);
		avin_switch_TView.setOnClickListener(this);
		avin_back_TView.setOnClickListener(this);
		avin_close_view.setOnClickListener(this);
		mAvinsurfaceview4Front = (AVINSurfaceView) findViewById(R.id.avin_surfaceview);
		mAvinsurfaceview4Front.setOnTouchListener(this);
		mParkingLayout = (LinearLayout) findViewById(R.id.layout_parking);
	}

	void initData() {
		
		Display display = getWindowManager().getDefaultDisplay();
		
		sScreenHeight = display.getHeight();
		sScreenWidth = display.getWidth();
		mAvinsurfaceview4Front.getLayoutParams();
		LayoutParams lp = mAvinsurfaceview4Front.getLayoutParams();
		lp.height = sScreenHeight;
		lp.width = sScreenWidth;
		mAvinsurfaceview4Front.setLayoutParams(lp);
		
		mAvinsurfaceview4Front.mIsRearSurfaceView = false;
		mAvinsurfaceview4Front.getHolder().setFormat(ImageFormat.YV12);
		 
		mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
		displays = mDisplayManager.getDisplays(null);
	
		IntentFilter filter = new IntentFilter();

		filter.addAction(ACTION_QB_POWERON);
		filter.addAction(ACTION_QB_POWEROFF);
		filter.addAction(ACTION_QB_PREPOWEROFF);
		filter.addAction(MCU_ACTION_ACC_ON);
		filter.addAction(MCU_ACTION_ACC_OFF);
		filter.addAction(AVINConstant.ACTION_AVIN_REQUEST_NOTIFY);
		filter.addAction("action_lightactivity.stop");
		filter.addAction("action_lightactivity.resume");
		filter.addAction(AVINConstant.ACTION_AUX_PORT_SET);
		filter.addAction(MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS);
		filter.addAction(AVINConstant.ACTION_BLUETOOTH_CALL_STATUS_CHANGE);
		
		if (rdsTest) {
			filter.addAction(AVINConstant.ACTION_RDS_ON);
			filter.addAction(AVINConstant.ACTION_RDS_OFF);
		}
		registerReceiver(mQuickBootListener, filter);
		hideTopBar(AVINConstant.HIDE_TOP_BAT_TIME);

		SystemProperties.set(PROPERTY_KEY_AVIN_TYPE, "aux_type");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	}
	
	private void SetAuxVideoPara(){
		/*
		if(!this.isResumed()){
			return;
		}
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

	public void startPlay(int videoid, int audioid) {
		mAvinsurfaceview4Front.mAvinA = avinA;
		mAvinsurfaceview4Front.mAvinV = avinV;
		mAvinsurfaceview4Front.getHolder().setFixedSize(sScreenWidth,
				sScreenWidth);
		avinV.setOnSignalListener(mListenerSignal);
		if(rearManager.isRearOpened()){
			avinV.setDestination(InputSource.DEST_TYPE_FRONT_REAR);
			avinA.setDestination(InputSource.DEST_TYPE_FRONT_REAR);
		}
		else{
			avinV.setDestination(InputSource.DEST_TYPE_FRONT);
			avinA.setDestination(InputSource.DEST_TYPE_FRONT);
		}
		
		mDestDir = InputSource.DEST_TYPE_FRONT;
		int retValueVideo = avinV.setSource(InputSource.SOURCE_TYPE_AVIN,
				videoid, AVIN.PORT_NONE, 0);
		int retValueAudio = avinA.setSource(InputSource.SOURCE_TYPE_AVIN,
				AVIN.PORT_NONE, audioid, AVIN.PRIORITY_IN_CBM_LEVEL_DEFAULT);
		if ((InputSource.ERR_FAILED == retValueVideo)
				&& (InputSource.ERR_FAILED == retValueAudio)) {
			L.d(TAG, "setSource()&&&&&&&&&&&&&&: L_FAILED!");
			DestroyAvin();
			finish();
			System.exit(0);
		}

		int retValueVideo2 = 0;
		int retValueAudio2 = 0;
		L.d(TAG, "startPlay(): audio video enter");

		if ((avinA == null) && (avinV == null)) {
			L.d(TAG, "startPlay(): there is no A/V so cannot start play");
			return;
		}
		if (avinV != null) {
			retValueVideo2 = avinV.play();
		}
		if (avinA != null) {
			retValueAudio2 = avinA.play();
		}
		
		if ((InputSource.ERR_CBM_NOT_ALLOWED == retValueVideo2)
				|| (InputSource.ERR_CBM_NOT_ALLOWED == retValueAudio2)) {
			DestroyAvin();
			finish();
			System.exit(0);
		}

		mCurrentStatus = InputSource.STATUS_STARTED;
		myHandler.sendEmptyMessage(SET_INPUTSOURCE);
	}
	
	private boolean pauseAudio = false;
	private void pauseAudio() {
        L.d(TAG, "pauseTargetPlay");
    	if(!pauseAudio){
    		if(mCurrentStatus == InputSource.STATUS_STARTED){
           	 	if (avinA != null) {
                    avinA.stop();
                }
    		}
       	 	pauseAudio = true;
       }     
    }
    
    private void resumeAudio() {
        L.d(TAG, "restartTargetPlay");
    	if(pauseAudio){
    		 if(mCurrentStatus == InputSource.STATUS_STARTED){
    			 if (avinA != null) {
                     setVolumeMute(true);
                     avinA.play();
                     myHandler.removeMessages(MSG_SET_VOLUME);
                     Message msg = new Message();
                     msg.what = MSG_SET_VOLUME;
                     myHandler.sendMessageDelayed(msg, SET_VOLUME_DELAY);
                 }
    		 }               
            pauseAudio = false;
        }     
    }

	public void stopTargetPlay() {
		L.d(TAG, "stopTargetPlay");
		if (avinV != null) {
			avinV.stop();
		}

		if (avinA != null) {
			avinA.stop();
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

		if (avinA != null) {
			avinA.play();
		}
		mCurrentStatus = InputSource.STATUS_STARTED;
	}
	@Override
	protected void onResume() {
	    L.e(TAG, "AVInActivity - onResume");
		SourceManager.acquireSource(sourceTocken);
		resumeAudio();
		YeconSettings.initVideoRgb(rgbType);
	    mParkingEnable = SystemProperties.getBoolean("persist.sys.parking_enable",
                false);
        mMcuIsParking = SystemProperties.getBoolean("persist.sys.mcu_parking", false);
        
		eActivityState = state.resume;
		isSetSysPara = true;
		//myHandler.sendEmptyMessageDelayed(SET_AUX_VIDEO_PARA,  500);
		myHandler.removeMessages(SHOW_NO_SIGNAL_STATE);
		myHandler.sendEmptyMessageDelayed(CHECK_SIGNAL,  500);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
	    mNeedMute = false;
		eActivityState = state.pause;
		myHandler.removeMessages(HIDE_TOP_BAR);
		L.d(TAG, "AVInActivity - onPause");
		if (isFinishing()) {
			L.d(TAG, "onPause was cause by finish!!!");
			// myHandler.sendEmptyMessage(RELEASE_RESOURCE);
		}
        //showSurfaceView(View.INVISIBLE);
		super.onPause();
	}
	
	@Override
	protected void onRestart() {
		eActivityState = state.restart;
		L.d(TAG, "AVInActivity - onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
	    if (mNeedMute) {
	        setVolumeMute(true);
	    }
		eActivityState = state.restart;
		L.d(TAG, "AVInActivity - onStart - mNeedMute: " + mNeedMute);
		super.onStart();
	}

	@Override
	protected void onStop() {
		eActivityState = state.stop;
		myHandler.removeMessages(SET_AUX_VIDEO_PARA);
		L.d(TAG, "AVInActivity - onStop");
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
	    avinV_signal_on = false;	    
		// AllServiceControl.mActivityName=null;
		eActivityState = state.destroy;
		L.d(TAG, "onDestroy(): Front HashCode " + this.hashCode() + "TaskId: "
				+ getTaskId());
		// AllServiceControl.mBackCarListenList.remove(this);
		unregisterReceiver(mQuickBootListener);
		myHandler.removeMessages(PROGRESS_CHANGED);
		myHandler.removeMessages(HIDE_CONTROLER);
		myHandler.removeMessages(SHOW_NO_SIGNAL_STATE);
		showSurfaceView(View.INVISIBLE);
		rearManager.release();
		DestroyAvin();
		super.onDestroy();
		SourceManager.unregisterSourceListener(sourceTocken);
	}
	
	private void setVolumeMute(boolean mute) {
	    boolean isPowerKey = SystemProperties.getBoolean(PROPERTY_KEY_POWERKEY,
                false);
	    L.d(TAG, "setVolumeMute - isPowerKey: " + isPowerKey);
	    if (isPowerKey) {
	        return;
	    }
	    boolean isMuted = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        if (!isMuted) {
            mAudioManager.setYeconVolumeMute(AudioManager.STREAM_MUSIC, mute, 0, 
                    YeconConstants.SRC_VOLUME_AUX1);
        }
	}

	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;
	private final static int DISMISS_ERROR_DIALOG = 2;
	private final static int SHOW_NO_SIGNAL_STATE = 3;
	// add for the case we new two Activity Client ,the will be conflict because
	// of cbm
	//private final static int SHOW_SURFACEVIEW = 4;
	private final static int SET_INPUTSOURCE = 5;
	private final static int SHOW_TOP_BAR = 6;
	private final static int HIDE_TOP_BAR = 7;
	private final static int SHOW_BT_SURFACEVIEW = 8;
	private final static int SET_AUX_VIDEO_PARA = 9;
	private final static int CHECK_SIGNAL = 10;
	private final static int MSG_SET_VOLUME = 11;

	private static final int SET_VOLUME_DELAY = 600;
	
	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
		    L.e(TAG, "AVInActivity - msg.what: " + msg.what);
			switch (msg.what) {
			case CHECK_SIGNAL:
			    if (mNeedMute) {
			        setVolumeMute(false);
			    }
				if(avinV_signal_on)
				{
					av_signal.setVisibility(View.INVISIBLE);
					showSurfaceView(View.VISIBLE);
					this.sendEmptyMessage(SET_AUX_VIDEO_PARA);
					hideTopBar(AVINConstant.HIDE_TOP_BAT_TIME);
				}
				else{
					if(state.resume == eActivityState){
						av_signal.setVisibility(View.VISIBLE);
					}
					showSurfaceView(View.INVISIBLE);			
					showTopBar(0);
				}
				rearManager.onSignal(avinV_signal_on);
				break;
			case PROGRESS_CHANGED:
				break;
			case HIDE_CONTROLER:
				// hideController();
				break;
			case DISMISS_ERROR_DIALOG:
				break;
			case SHOW_NO_SIGNAL_STATE:
			{
				if(state.resume == eActivityState){
					av_signal.setVisibility(View.VISIBLE);
				}
				showSurfaceView(View.INVISIBLE);
				mIsFullScreen = false;
				top_img_parent.setVisibility(View.VISIBLE);
				
				rearManager.onSignal(false);
				
				WindowManager.LayoutParams params = getWindow().getAttributes();
	            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	            getWindow().setAttributes(params);
	            
				//by lzy
				SetSysVideoPara();
				Intent intent=new Intent("aux.signal.lost");
				sendBroadcast(intent);
			}
				break;

			case SET_INPUTSOURCE:		
				changeSourceInput();
				break;
			case SHOW_TOP_BAR:
			{
				mIsFullScreen = false;
				top_img_parent.setVisibility(View.VISIBLE);
				//mAvinsurfaceview4Front.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);   
				WindowManager.LayoutParams params = getWindow().getAttributes();
	            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	            getWindow().setAttributes(params);
	            if(avinV_signal_on){
					hideTopBar(AVINConstant.HIDE_TOP_BAT_TIME);
				}
			}
				break;
			case HIDE_TOP_BAR:
			{
				mIsFullScreen = true;
				if (av_signal.isShown()) {
					mIsFullScreen = false;
					top_img_parent.setVisibility(View.VISIBLE);
					//mAvinsurfaceview4Front.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);   
					WindowManager.LayoutParams params = getWindow().getAttributes();
		            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		            getWindow().setAttributes(params);
				} else {
					mIsFullScreen = true;
					top_img_parent.setVisibility(View.INVISIBLE);
					//mAvinsurfaceview4Front.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
					WindowManager.LayoutParams params = getWindow().getAttributes();
		            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		            getWindow().setAttributes(params);
				}
			}
				break;
			case SHOW_BT_SURFACEVIEW:
				if (!avinV_signal_on) {
					if(state.resume == eActivityState){
						av_signal.setVisibility(View.VISIBLE);
					}
					showSurfaceView(View.INVISIBLE);
					showTopBar(0);
				} else {
					av_signal.setVisibility(View.INVISIBLE);
					showSurfaceView(View.VISIBLE);
					hideTopBar(AVINConstant.HIDE_TOP_BAT_TIME);
				}
				rearManager.onSignal(avinV_signal_on);

				//by lzy
				myHandler.sendEmptyMessageDelayed(SET_AUX_VIDEO_PARA,  500);
				
				Intent intent1 = new Intent("aux.signal.ready");
				sendBroadcast(intent1);	
				break;
				
			case SET_AUX_VIDEO_PARA:
				if(avinV_signal_on && eActivityState == state.resume)
				{
				    if (mParkingEnable && !mMcuIsParking) {
				        
				    } else {
				        SetAuxVideoPara();
				    }
				}
				break;
				
			case MSG_SET_VOLUME:
			    setVolumeMute(false);
			    break;
			}

			super.handleMessage(msg);
		}
	};

	private void showSurfaceView(int visibility) {

		if (mAvinsurfaceview4Front != null && mAvinsurfaceview4Front.getVisibility()!=visibility) {
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.color.background);
            mAvinsurfaceview4Front.setBackgroundDrawable(drawable);   			
			mAvinsurfaceview4Front.setVisibility(visibility);

		}
		
		if (visibility == View.VISIBLE) {
		    if (mParkingEnable && !mMcuIsParking) {
		        SetSysVideoPara();
                mParkingLayout.setVisibility(View.VISIBLE);
            } else {
                SetAuxVideoPara();
                mParkingLayout.setVisibility(View.INVISIBLE);
            }
		} else {
		    mParkingLayout.setVisibility(View.INVISIBLE);
		}
	}

	private AVIN.OnSignalListener mListenerSignal = new AVIN.OnSignalListener() {
		public void onSignal(int msg, int param1, int param2) {
			L.i(TAG, "onSignal msg:" + msg);
			switch (msg) {
			case AVIN.SIGNAL_READY:
				L.i(TAG, "..............................SIGNAL_READY");
				avinV_signal_on = true;
				myHandler.removeMessages(SHOW_NO_SIGNAL_STATE);
				if (mAvinsurfaceview4Front != null) {
				    Log.d(TAG,  "............... param1 =" + param1 +  "...............param2 +" + param2);
					mAvinsurfaceview4Front.SetAVINSignalType(param1, param2);
				}
				rearManager.setAvinSignalType(param1, param2);
				myHandler.sendEmptyMessage(SHOW_BT_SURFACEVIEW);
				break;
			case AVIN.SIGNAL_LOST:
				L.i(TAG, "..............................SIGNAL_LOST");
				avinV_signal_on = false;
				myHandler.removeMessages(SHOW_BT_SURFACEVIEW);
				myHandler.removeMessages(SHOW_NO_SIGNAL_STATE);
				myHandler.sendEmptyMessageDelayed(SHOW_NO_SIGNAL_STATE,  2000);
				break;
			default:
				return;
			}
		}
	};
	
	// now A/V handled together ,if we need to seperate, the implement need to
	// change
	// asynchronous call need add lock
	private AVIN.OnCbmCmdListener mListenerCbmCmd = new AVIN.OnCbmCmdListener() {
		public void onCmd(int msg, int param1) {
			L.i(TAG, "OnCbmCmdListener enter msg: " + msg + " param1: "
					+ param1 + " mDestDir: " + mDestDir);
			int destDir = mDestDir;
			// if (AVIN.INPUTSOURCE_HANDLE_CBM == 0) {
			switch (msg) {
			case AVIN.INPUTSOURCE_CBM_STOP:
			case AVIN.INPUTSOURCE_CBM_PAUSE:
				L.i(TAG, "OnCbmCmdListener: CBM_STOP!");
				synchronized (mCbmLock) {
					destDir &= ~param1;
					if (destDir == mDestDir) {
						return;
					} else if (destDir == InputSource.DEST_TYPE_NONE) {
						if (mCbmCtlStatus == InputSource.CBM_CONTROL_STOPED) {
							L.i(TAG,"OnCbmCmdListener CBM_STOP Already Stoped mDestDir:"+ mDestDir);
							return;
						}
						if (mCurrentStatus == InputSource.STATUS_STOPED) {
							L.i(TAG,"OnCbmCmdListener CBM_STOP Already been stoped because there is other activity in front");
							return;
						}
						showSurfaceView(View.INVISIBLE);
						mCbmCtlStatus = InputSource.CBM_CONTROL_STOPED;
					}
				}
				break;

			case AVIN.INPUTSOURCE_CBM_START:
			case AVIN.INPUTSOURCE_CBM_RESUME:
				L.i(TAG, "OnCbmCmdListener CBM_START!" + mCbmCtlStatus);
				synchronized (mCbmLock) {

					if (mCbmCtlStatus == InputSource.CBM_CONTROL_STOPED) {
						L.i(TAG, "OnCbmCmdListener CBM_CONTROL_STOPED");
						mCbmCtlStatus = InputSource.CBM_CONTROL_NONE;
					}
				}
				break;

			default:
				L.i(TAG, "OnCbmCmdListener Unknown CMD!");
				return;
			}

			L.i(TAG, "OnCbmCmdListener leave!");
		}
	};

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
			} else if (ACTION_QB_POWEROFF.equals(action) || ACTION_QB_PREPOWEROFF.equals(action)) {
				L.i(TAG, "mQuickBootListener - ACTION_QB_POWEROFF received");
				L.i(TAG, "mQuickBootListener stop AVIN Playback begin");
				//stopTargetPlay();
				finish();
				L.i(TAG, "mQuickBootListener stop AVIN end");
			} else if (MCU_ACTION_ACC_OFF.equals(action)) {
                L.i(TAG, "mQuickBootListener - MCU_ACTION_ACC_OFF received");
                L.i(TAG, "mQuickBootListener stop AVIN Playback begin");
                stopTargetPlay();
                L.i(TAG, "mQuickBootListener stop AVIN end");
            } else if (rdsTest) {
				if (AVINConstant.ACTION_RDS_ON.equals(action)) {
					L.i(TAG, "RDS ON received");
					if (avinA != null) {
						avinA.setSource(InputSource.SOURCE_TYPE_RDS_AUD,
								AVIN.PORT_NONE, audioport, 0);
					}
				} else if (AVINConstant.ACTION_RDS_OFF.equals(action)) {
					L.i(TAG, "RDS OFF received");
					if (avinA != null) {
						avinA.setSource(InputSource.SOURCE_TYPE_AVIN,
								AVIN.PORT_NONE, audioport, 0);
					}
				}
			} else if (AVINConstant.ACTION_AVIN_REQUEST_NOTIFY.equals(action)) {
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
					myHandler.sendEmptyMessageDelayed(SET_AUX_VIDEO_PARA, AVINConstant.SET_AUX_DELAY_TIME);
				}
			}
			else if(AVINConstant.ACTION_AUX_PORT_SET.equals(action))
			{
				if(eActivityState == state.stop)
				{
					SetAuxPort();
					avinV_signal_on = false;
					myHandler.sendEmptyMessage(SET_INPUTSOURCE);
					myHandler.removeMessages(SHOW_NO_SIGNAL_STATE);
					myHandler.sendEmptyMessageDelayed(SHOW_NO_SIGNAL_STATE,
							AVINConstant.SIG_STATUS_TIME);			
				}

			} else if (MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS.equals(action)) {
			    int brakeStatus = intent.getIntExtra(INTENT_EXTRA_BRAKE_STATUS, 0);
                mMcuIsParking = (brakeStatus == 0) ? true : false;
			    if (avinV_signal_on) {
	                if (mParkingEnable && !mMcuIsParking) {
	                    SetSysVideoPara();
	                    mParkingLayout.setVisibility(View.VISIBLE);
	                } else {
	                    SetAuxVideoPara();
	                    mParkingLayout.setVisibility(View.INVISIBLE);
	                }
			    }
			} else if (AVINConstant.ACTION_BLUETOOTH_CALL_STATUS_CHANGE.equals(action)) {
			    int callState = intent.getIntExtra(ACTION_BLUETOOTH_CALL_STATUS, 0);
			    L.d(TAG, "ACTION_BLUETOOTH_CALL_STATUS_CHANGE - callState: " + callState);
			    if (callState == 1) {
			        mNeedMute = true;
			    }
			}
		}
	};

	private void onBackPressedToHome() {
		finish();
	}

	@Override
	public void onClick(View mView) {
		Intent mIntent;
		switch (mView.getId()) {
		case R.id.avin_back_view:
			onBackPressedToHome();
			break;
		case R.id.avin_close_view:
			finish();
			break;
		case R.id.avin_light_view:
			mIntent = new Intent(Intent.ACTION_VIEW);
			mIntent.setComponent(new ComponentName("com.yecon.sound.setting",
					"com.yecon.sound.setting.LightActivity"));
			mIntent.putExtra("IsAuxPara", avinV_signal_on);
			startActivity(mIntent);
			isSetSysPara = false;
			break;
		case R.id.avin_sound_view:
			mIntent = new Intent(Intent.ACTION_VIEW);
			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.setComponent(new ComponentName("com.yecon.sound.setting",
					"com.yecon.sound.setting.AudioSetting"));
			startActivity(mIntent);
			break;
		case R.id.avin_switch_view:
			if (isFrontAvin) {
				videoport = 3;
				audioport = 2;
				rgbType = YeconSettings.RGBTYPE.AVIN_3;
			} else {
				videoport = 2;
				audioport = 1;
				rgbType = YeconSettings.RGBTYPE.AVIN_2;
			}
			
			avinV_signal_on = false;
			myHandler.sendEmptyMessage(SET_INPUTSOURCE);
			myHandler.removeMessages(SHOW_NO_SIGNAL_STATE);
			myHandler.sendEmptyMessageDelayed(SHOW_NO_SIGNAL_STATE,
					AVINConstant.SIG_STATUS_TIME);
			break;
		default:
			break;
		}
	}

	private void hideTopBar(long delayTime){
		myHandler.removeMessages(HIDE_TOP_BAR);
		myHandler.removeMessages(SHOW_TOP_BAR);
		myHandler.sendEmptyMessageDelayed(HIDE_TOP_BAR, delayTime);
	}
	private void showTopBar(long delayTime){
		myHandler.removeMessages(HIDE_TOP_BAR);
		myHandler.removeMessages(SHOW_TOP_BAR);
		myHandler.sendEmptyMessageDelayed(SHOW_TOP_BAR, delayTime);
	}
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!avinV_signal_on || mIsFullScreen == true) {// if full screen then show top bar or display
				showTopBar(0);				
			} else {
				hideTopBar(0);
			}
			break;

		}

		return true;
	}

	public void changeSourceInput() {
		if (videoport == 2) {
			isFrontAvin = true;
			avin_switch_TView.setText(R.string.avin_switch_front);
		} else {
			isFrontAvin = false;
			avin_switch_TView.setText(R.string.avin_switch_back);
		}
		
		avinV.setSource(InputSource.SOURCE_TYPE_AVIN, videoport,
				AVIN.PORT_NONE, 0);
		avinA.setSource(InputSource.SOURCE_TYPE_AVIN, AVIN.PORT_NONE,
				audioport, 0);

		YeconSettings.initVideoRgb(rgbType);
	}

	public void DestroyAvin() {
		if (avinV != null) {
			avinV.setDisplay(null);
			avinV.setRearDisplay(null);
			avinV.stop();
			avinV.release();
			avinV = null;
		}

		if (avinA != null) {
			avinA.stop();
			avinA.release();
			avinA = null;
		}
	}

}
