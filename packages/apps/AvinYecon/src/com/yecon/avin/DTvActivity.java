package com.yecon.avin;

import static android.mcu.McuExternalConstant.*;
import static com.yecon.avin.AVINConstant.*;
import static android.constant.YeconConstants.*;

import java.io.File;
import java.io.IOException;
import com.autochips.inputsource.AVIN;
import com.autochips.inputsource.InputSource;
import com.yecon.avin.R;
import com.yecon.avin.unitl.L;
import com.yecon.common.SourceManager;
import com.yecon.settings.YeconSettings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.exdevport.SerialPort;
import android.graphics.ImageFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class DTvActivity extends Activity  implements OnClickListener {
	protected static final String TAG = null;
	private SerialPort serialPort;
	private static AVINSurfaceView mAvinsurfaceview4Front = null;
	private AudioManager mAudioManager;
	public AVIN  avinV, avinA;
	private int videoport = 5;
	private YeconSettings.RGBTYPE rgbType = YeconSettings.RGBTYPE.AVIN_5;
	private int audioport = 4;
	private boolean avinV_signal_on = false;
	private static boolean rdsTest = false;
	private static int sScreenWidth = 0;
	private static int sScreenHeight = 0;
	private int mCurrentStatus = InputSource.STATUS_NONE;
	
	private View  toolsSetting, toolsOrientation, toolsChannel, toolbar, bottombar;
	private TextView av_signal;
	private Object sourceTocken;
	private RearManager rearManager;
	
	enum KeyNo{
		Poweron(0x8A),Mute(0x8C),Num0(0x90),Num9(0x99),
	    ChUp(0x80),ChDown(0x81),VolDownLeft(0x83),VolUpRight(0x82),
	    Menu(0x85),Exit(0x87),Info(0x89),Epg(0x9B),
	    Subtitle(0xC4),FastScan(0x9A),OK(0x9F);
		private final int value; 
        public int getValue() { 
            return value; 
        }
        KeyNo(int value) { 
            this.value = value; 
        } 
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.dtv);
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
		
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		this.setContentView(R.layout.activity_tv);
		toolbar = findViewById(R.id.toolbar);
		toolsSetting = (View) findViewById(R.id.toolbar_setting);
		toolsSetting.setVisibility(View.GONE);
		toolsOrientation = (View) findViewById(R.id.toolbar_orientation);
		toolsOrientation.setVisibility(View.GONE);
		toolsChannel = (View) findViewById(R.id.toolbar_channel);
		toolsChannel.setVisibility(View.GONE);
		bottombar = findViewById(R.id.bottombar);
		av_signal = (TextView) findViewById(R.id.av_signal); 
		mAvinsurfaceview4Front = (AVINSurfaceView) findViewById(R.id.avin_surfaceview);
		
		initData();
		avinV = new AVIN();
		avinA = new AVIN();
		mAvinsurfaceview4Front.mAvinV = avinV;
		
		rearManager = new RearManager(getApplicationContext());
		rearManager.init(avinV, avinA);
		
		YeconSettings.initVideoRgb(rgbType);
		
		startPlay(videoport, audioport);
		
		try 
		{ 
			serialPort = new SerialPort(new File("/dev/ttyMT6"),115200);
			
		}
		catch (SecurityException e)
		{ 
			e.printStackTrace(); 
		}
       catch (IOException e) 
       { 
    	   e.printStackTrace(); 
       }
		
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
		registerReceiver(mQuickBootListener, filter);
		
		SystemProperties.set(PROPERTY_KEY_AVIN_TYPE, "aux_type");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
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
		//myHandler.sendEmptyMessage(SET_INPUTSOURCE);
	}
	
	private Handler uiHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 0){
			}
			else{
				switch (msg.what) {
				case AVINConstant.CHECK_SIGNAL:
					if(avinV_signal_on)
					{
						av_signal.setVisibility(View.INVISIBLE);
						showSurfaceView(View.VISIBLE);
					}
					else{
						av_signal.setVisibility(View.VISIBLE);
						showSurfaceView(View.INVISIBLE);			
						showToolBar(true, false);				
					}
					rearManager.onSignal(avinV_signal_on);
					break;
				case AVINConstant.SHOW_NO_SIGNAL_STATE:
				{
					if(avinV_signal_on)
					{
						av_signal.setVisibility(View.INVISIBLE);
						showSurfaceView(View.VISIBLE);
					}
					else{
						av_signal.setVisibility(View.VISIBLE);
						showSurfaceView(View.INVISIBLE);	
						showToolBar(true, false);				
					}					
					rearManager.onSignal(avinV_signal_on);
				}
					break;
				case AVINConstant.SHOW_BT_SURFACEVIEW:
					if (avinV_signal_on) {
						av_signal.setVisibility(View.INVISIBLE);
						showSurfaceView(View.VISIBLE);	
					} else {
						av_signal.setVisibility(View.VISIBLE);
						showSurfaceView(View.INVISIBLE);
						showToolBar(true, false);		
					}
					rearManager.onSignal(avinV_signal_on);
					break;
				case AVINConstant.SET_INPUTSOURCE:		
					//changeSourceInput();
					break;
				case AVINConstant.SHOW_TOP_BAR:
				{
					showToolBar(true, false);				
				}
					break;
				case AVINConstant.HIDE_TOP_BAR:
				{
					if (avinV_signal_on){
						showToolBar(false, false);		
					}
				}
					break;
				}
			}
			super.handleMessage(msg);
		}
		
	};
	
	protected void showSurfaceView(int visible) {
		// TODO Auto-generated method stub
		mAvinsurfaceview4Front.setVisibility(visible);
	}
	
	private AVIN.OnSignalListener mListenerSignal = new AVIN.OnSignalListener() {
		public void onSignal(int msg, int param1, int param2) {
			L.i(TAG, "onSignal msg:" + msg);
			switch (msg) {
			case AVIN.SIGNAL_READY:
				L.i(TAG, "..............................SIGNAL_READY");
				avinV_signal_on = true;
				uiHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
				if (mAvinsurfaceview4Front != null) {
				    Log.d(TAG,  "............... param1 =" + param1 +  "...............param2 +" + param2);
					mAvinsurfaceview4Front.SetAVINSignalType(param1, param2);
				}
				rearManager.setAvinSignalType(param1, param2);
				uiHandler.sendEmptyMessage(AVINConstant.SHOW_BT_SURFACEVIEW);
				uiHandler.sendEmptyMessageDelayed(AVINConstant.HIDE_TOP_BAR, AVINConstant.HIDE_TOP_BAT_TIME);
				break;
			case AVIN.SIGNAL_LOST:
				L.i(TAG, "..............................SIGNAL_LOST");
				avinV_signal_on = false;
				uiHandler.removeMessages(AVINConstant.SHOW_BT_SURFACEVIEW);
				uiHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
				uiHandler.sendEmptyMessageDelayed(AVINConstant.SHOW_NO_SIGNAL_STATE,  2000);
				break;
			default:
				return;
			}
		}
	};
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
                     avinA.play();
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
				if (ACTION_RDS_ON.equals(action)) {
					L.i(TAG, "RDS ON received");
					if (avinA != null) {
						avinA.setSource(InputSource.SOURCE_TYPE_RDS_AUD,
								AVIN.PORT_NONE, audioport, 0);
					}
				} else if (ACTION_RDS_OFF.equals(action)) {
					L.i(TAG, "RDS OFF received");
					if (avinA != null) {
						avinA.setSource(InputSource.SOURCE_TYPE_AVIN,
								AVIN.PORT_NONE, audioport, 0);
					}
				}
			} else if (ACTION_AVIN_REQUEST_NOTIFY.equals(action)) {
				Log.i(TAG, "ACTION_AVIN_REQUEST_NOTIFY");
				finish();
			}
		}
	};
	
	@Override
    protected void onStop() {
	    YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);
        super.onStop();
    }

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		sendCmd(KeyNo.Exit);
		serialPort.close();
		unregisterReceiver(mQuickBootListener);
		rearManager.release();
		DestroyAvin();
		super.onDestroy();
		SourceManager.unregisterSourceListener(sourceTocken);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		SourceManager.acquireSource(sourceTocken);
		resumeAudio();
		uiHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
		uiHandler.sendEmptyMessageDelayed(AVINConstant.CHECK_SIGNAL,  500);
		YeconSettings.initVideoRgb(rgbType);
		super.onResume();
	}

	private View getContentShow(){
		if(toolsChannel.getVisibility()==View.VISIBLE)
			return toolsChannel;
		if(toolsOrientation.getVisibility()==View.VISIBLE)
			return toolsOrientation;
		if(toolsSetting.getVisibility()==View.VISIBLE)
			return toolsSetting;
		return null;
	}
	private void showToolBarContent(int viewId, boolean show){
		if(show){
			showToolBar(true, false);
		}
		final View vshow;
		View vhide1, vhide2;
		switch(viewId){
		case R.id.toolbar_channel:
			vshow = toolsChannel;
			vhide1 = toolsOrientation;
			vhide2 = toolsSetting;
			break;
		case R.id.toolbar_setting:
			vshow = toolsSetting;
			vhide1 = toolsOrientation;
			vhide2 = toolsChannel;
			break;
		case R.id.toolbar_orientation:
			vshow = toolsOrientation;
			vhide1 = toolsChannel;
			vhide2 = toolsSetting;
			break;
		default:
			return;
		}
		View curShow = getContentShow();
		if(null==curShow){
			if(show){
				if(vshow.getVisibility()!=View.VISIBLE){
					vhide1.setVisibility(View.GONE);
					vhide2.setVisibility(View.GONE);
					
					TranslateAnimation tAnim = new TranslateAnimation(vshow.getWidth(), 0, 0, 0);
					tAnim.setDuration(400);
					tAnim.setInterpolator(new OvershootInterpolator());
					vshow.clearAnimation();
					vshow.startAnimation(tAnim);
					vshow.setVisibility(View.VISIBLE);
					
					tAnim = new TranslateAnimation(vshow.getWidth(), 0, 0, 0);
					tAnim.setDuration(500);
					tAnim.setInterpolator(new OvershootInterpolator());
					toolbar.clearAnimation();
					toolbar.startAnimation(tAnim);
				}
			}
		}
		else{
			if(show){
				if(curShow == vshow)
					return;
				if(vshow.getVisibility()!=View.VISIBLE){
					vhide1.setVisibility(View.GONE);
					vhide2.setVisibility(View.GONE);
					vshow.setVisibility(View.VISIBLE);
				}
			}
			else{
				if(vshow.getVisibility()==View.VISIBLE){
					vhide1.setVisibility(View.GONE);
					vhide2.setVisibility(View.GONE);
					vshow.setVisibility(View.INVISIBLE);
					
					TranslateAnimation tAnim = new TranslateAnimation(0, vshow.getWidth(), 0, 0);
					tAnim.setDuration(400);
					tAnim.setInterpolator(new OvershootInterpolator());
					vshow.clearAnimation();
					vshow.startAnimation(tAnim);
					
					tAnim = new TranslateAnimation(0, vshow.getWidth(), 0, 0);
					tAnim.setDuration(500);
					tAnim.setInterpolator(new OvershootInterpolator());
					tAnim.setAnimationListener(new AnimationListener(){

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							toolbar.clearAnimation();
							vshow.setVisibility(View.GONE);
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
					toolbar.clearAnimation();
					toolbar.startAnimation(tAnim);
				}
			}
		}

	}
	
	private void showToolBar(boolean show, boolean toogle){
		if(toogle){
			if(toolbar.getVisibility()==View.VISIBLE){
				show = false;
			}
			else{
				show = true;
			}
		}
		if(!avinV_signal_on){
			show = true;
		}
		
		if(show){
			AVINCommon.switchFullScreen(getWindow(), false);
			if(toolbar.getVisibility()!=View.VISIBLE){
				TranslateAnimation tAnim = new TranslateAnimation(toolbar.getWidth(), 0, 0, 0);
				tAnim.setDuration(500);
				tAnim.setInterpolator(new OvershootInterpolator());
				tAnim.setAnimationListener(new AnimationListener(){

					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						toolbar.clearAnimation();
						toolbar.setVisibility(View.VISIBLE);		
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
				toolbar.clearAnimation();
				toolbar.startAnimation(tAnim);
			}
			if(bottombar.getVisibility()!=View.VISIBLE){
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
				bottombar.startAnimation(tAnim);
			}
			
		}
		else{
			AVINCommon.switchFullScreen(getWindow(), true);
			//final View curShow = this.getContentShow();
			if(toolbar.getVisibility()==View.VISIBLE){
				ViewGroup vp =  (ViewGroup) toolbar.getParent();
				TranslateAnimation tAnim = new TranslateAnimation(0, vp.getRight()-toolbar.getLeft(), 0, 0);
				tAnim.setDuration(500);
				tAnim.setInterpolator(new OvershootInterpolator());
				tAnim.setAnimationListener(new AnimationListener(){

					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						//if(null!=curShow){
						//	curShow.setVisibility(View.GONE);
						//}
						toolbar.clearAnimation();
						toolbar.setVisibility(View.INVISIBLE);		
						toolsChannel.setVisibility(View.GONE);
						toolsOrientation.setVisibility(View.GONE);
						toolsSetting.setVisibility(View.GONE);
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
				View curShow = this.getContentShow();
				if(curShow!=null){
					showToolBarContent(curShow.getId(), false);
				}
				toolbar.clearAnimation();
				toolbar.startAnimation(tAnim);
			}
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
	
	private byte[]pkgBuffer=new byte[4];
	private void sendCmd(KeyNo keyNo, int plus){
		if(serialPort==null || serialPort.getOutputStream()==null){
			if(serialPort==null)
				Log.w(TAG, "serialPort not opened");				
			else
				Log.w(TAG, "serialPort can not write");
			return;
		}
		Log.i(TAG, "sendCmd:"+Integer.toHexString(keyNo.getValue() +plus));
		pkgBuffer[0]=(byte) 0xA5;
		pkgBuffer[1]=(byte) ((byte)(keyNo.getValue()+plus)&0xff);
		pkgBuffer[2]=(byte)0x2A;
		try {
			serialPort.getOutputStream().write(pkgBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void sendCmd(KeyNo keyNo){
		sendCmd(keyNo, 0);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		View view = this.getContentShow();
		
		switch (arg0.getId()) {
		case R.id.avin_surfaceview:
			showToolBar(true, true);
			return;
		case R.id.ivSetting:
			if(toolsSetting.getVisibility()!=View.VISIBLE){
				showToolBarContent(R.id.toolbar_setting, true);
			}
			else{
				showToolBarContent(view.getId(), false);
			}
			break;
		case R.id.ivOrientation:
			if(toolsOrientation.getVisibility()!=View.VISIBLE){
				showToolBarContent(R.id.toolbar_orientation, true);
			}
			else{
				showToolBarContent(view.getId(), false);
			}
			break;
		case R.id.ivChannel:
			if(toolsChannel.getVisibility()!=View.VISIBLE){
				showToolBarContent(R.id.toolbar_channel,  true);
			}
			else{
				showToolBarContent(view.getId(), false);
			}
			break;
		case R.id.ivMenu:
			if(view!=null){
				//showToolBarContent(view.getId(), false);
			}
			sendCmd(KeyNo.Menu);			
			break;
		case R.id.ivNext:
			if(view!=null){
				//showToolBarContent(view.getId(), false);
			}
			sendCmd(KeyNo.ChDown);	
			break;
		case R.id.ivPrevious:
			if(view!=null){
				//showToolBarContent(view.getId(), false);
			}
			sendCmd(KeyNo.ChUp);	
			break;
		case R.id.ivFavorite:
			if(view!=null){
				//showToolBarContent(view.getId(), false);
			}
			sendCmd(KeyNo.Info);
			break;
		case R.id.ivSearch:
			if(view!=null){
				//showToolBarContent(view.getId(), false);
			}
			sendCmd(KeyNo.FastScan);			
			break;
		case R.id.ivEpg:
			sendCmd(KeyNo.Epg);
			break;
		case R.id.ivOk:
			sendCmd(KeyNo.OK);
			break;
		case R.id.ivLeft:
			sendCmd(KeyNo.VolDownLeft);
			break;
		case R.id.ivRight:
			sendCmd(KeyNo.VolUpRight);
			break;
		case R.id.ivUp:
			sendCmd(KeyNo.ChUp);
			break;
		case R.id.ivDown:
			sendCmd(KeyNo.ChDown);
			break;
		case R.id.iv0:
			sendCmd(KeyNo.Num0, 0);
			break;
		case R.id.iv1:
			sendCmd(KeyNo.Num0, 1);
			break;
		case R.id.iv2:
			sendCmd(KeyNo.Num0, 2);
			break;
		case R.id.iv3:
			sendCmd(KeyNo.Num0, 3);
			break;
		case R.id.iv4:
			sendCmd(KeyNo.Num0, 4);
			break;
		case R.id.iv5:
			sendCmd(KeyNo.Num0, 5);
			break;
		case R.id.iv6:
			sendCmd(KeyNo.Num0, 6);
			break;
		case R.id.iv7:
			sendCmd(KeyNo.Num0, 7);
			break;
		case R.id.iv8:
			sendCmd(KeyNo.Num0, 8);
			break;
		case R.id.iv9:
			sendCmd(KeyNo.Num0, 9);
			break;
		}
		uiHandler.removeMessages(AVINConstant.HIDE_TOP_BAR);
		uiHandler.sendEmptyMessageDelayed(AVINConstant.HIDE_TOP_BAR, AVINConstant.HIDE_TOP_BAT_TIME);
	
	}

}
