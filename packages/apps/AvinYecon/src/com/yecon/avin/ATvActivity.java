package com.yecon.avin;

import static android.mcu.McuExternalConstant.*;
import static com.yecon.avin.AVINConstant.*;
import static android.constant.YeconConstants.*;

import java.text.DecimalFormat;
import java.util.Arrays;

import org.json.JSONArray;
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
import android.content.SharedPreferences;
import android.exdevport.AtvPort;
import android.graphics.ImageFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class ATvActivity extends Activity  implements OnClickListener, OnLongClickListener {
	protected static final String TAG = "ATvActivity";
	//private I2cPort i2cPort=new I2cPort();
	private AtvPort atvPort = new AtvPort();
	private String DEVICE_NAME =  "/dev/i2c-1";
	//private final int i2cDevAddr = 0x3d;
	private TextView tvFormatHint, tvChannel, tvHint;
	
	public AVIN  avinV, avinA;
	private int videoport = 5;
	private int audioport = 4;
	private YeconSettings.RGBTYPE rgbType = YeconSettings.RGBTYPE.AVIN_5;
	private boolean avinV_signal_on = false;
	
	private View  bottombar;
	TextView av_signal = null;
	private static boolean rdsTest = false;
	private static int sScreenWidth = 0;
	private static int sScreenHeight = 0;
	//private DisplayManager mDisplayManager;
	private static AVINSurfaceView mAvinsurfaceview4Front = null;
	//private int mDestDir = InputSource.DEST_TYPE_FRONT;
	//private int mCbmCtlStatus = InputSource.CBM_CONTROL_NONE;
	private int mCurrentStatus = InputSource.STATUS_NONE;
	
	private HandlerThread atvThread;
	private Handler atvHandler;
	private final int ATV_SCHEDULE_TIME = 20;
	private int []atvStatus;
	
	private AudioManager mAudioManager;
	private RearManager rearManager;
	private Object sourceTocken;
	
	public enum ATV_CMD{
		;
		public static  final int ATV_CMD_IDLE=0, 
		ATV_CMD_AMS=1,
		ATV_CMD_PS=2,
		ATV_CMD_SEARCHDOWN=3,
		ATV_CMD_SEARCHUP=4,
		ATV_CMD_NEXT_CH=5,
		ATV_CMD_PRE_CH=6,
		ATV_CMD_FREQ_DOWN=7,
		ATV_CMD_FREQ_UP=8,
		ATV_CMD_FORMAT=9,
		ATV_CMD_PS_FINISH=10,
		ATV_CMD_KEY=11,
		ATV_CMD_SRCH_FOCE_STOP=12,
		ATV_END=13;
		;
	};
	public enum ATV_FORMAT{
		;
		public static  final int TV_TUNER_SYSTEM_PAL_DK=0, 
		TV_TUNER_SYSTEM_PAL_I=1,
		TV_TUNER_SYSTEM_PAL_BG=2,
		TV_TUNER_SYSTEM_NTSC_MN=3,
		TV_TUNER_SYSTEM_SECAM_DK=4,
		TV_TUNER_SYSTEM_SECAM_BG=5,
		TV_TUNER_SYSTEM_PAL_M=6,
		TV_TUNER_SYSTEM_MAX=7;
		;
	};
	private final String ATV_FORMAT_NAME[]={
			"PAL DK",
			"PAL I",
			"PAL BG",
			"NTSC MN",
			"SECAM DK",
			"SECAM BG",
			"PAL M",
	};
	/*频段
	 * enum{
			CDT1028_BAND_VHF_LOW,
			CDT1028_BAND_VHF_HIGH,
			CDT1028_BAND_UHF,
		};
	 */
	private final String ATV_FREP_SECTOR_NAME[]={
			"VHF L",
			"VHF H",
			"UHF",
	};
	
	private long lastTime = 0;
	private void updateSearchAnim(){
		if((SystemClock.uptimeMillis() - lastTime)<1000)
			return;
		Integer i = (Integer) tvHint.getTag();
		if(null == i){
			i = 1;
		}
		else{
			i = (i+1)%20 + 1;
		}
		tvHint.setTag(i);
		StringBuffer str = new StringBuffer();
		for(int j=0;j<i;j++){
			str.append(".");
		}
		tvHint.setText(str);
		lastTime = SystemClock.uptimeMillis();
	}
	
	private Handler uiHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 0){
				//show tv status.
				Log.i(TAG, "0");
				if(msg.obj!=null){
					int []status = (int[]) msg.obj;
					Log.i(TAG, " " + status.length);
					if(status[0]>0){
						updateSearchAnim();
					}
					else{
						Integer i = 1;
						tvHint.setTag(i);
						
						String freqSector = "";
						if(status[5]>=0 && status[5]<ATV_FREP_SECTOR_NAME.length){
							freqSector = ATV_FREP_SECTOR_NAME[status[5]];
						}
						DecimalFormat fnum = new DecimalFormat("0.00 MHz");
						tvHint.setText(freqSector + " " + fnum.format((float)status[1]/20));
					}
					if(status[2]>=0 && status[2]<ATV_FORMAT_NAME.length){
						tvFormatHint.setText(ATV_FORMAT_NAME[status[2]]);
					}
					else{
						tvFormatHint.setText("unknown");
					}
					if(status[4]>=0 && status[4]<100){
						tvChannel.setText("CH " + (1+status[4]));
					}
					/*制式
					 * pData[0] = AtvGetState();					//搜索状态
			pData[1] = AtvGetCurrentFreq();				//当前的频率
			pData[2] = AtvGetFormat();					//接受制式
			pData[3] = AtvGetValidStationNum();
			if(AtvGetRadioArea() == ATVAREA_Brazil || AtvGetRadioArea() == ATVAREA_RUSSIA)
				pData[4] = AtvGetCurrentPresetListIndex()+1;	//频道号
			else
				pData[4] = AtvGetCurrentPresetListIndex();	//频道号
#ifdef ATVTYPE_ADT1028
			pData[5] = m_pAtvDriver->GetTvDriverBand();
#else
			pData[5] = GetTvDriverBand();				//频段
					 */
				}
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
					showToolBar(false, false);			
				}
					break;
				}
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.atv);
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
		
		this.setContentView(R.layout.activity_atv);
		av_signal = (TextView) findViewById(R.id.av_signal); 
		bottombar = findViewById(R.id.bottombar);
		findViewById(R.id.tvFormat).setOnClickListener(this);
		findViewById(R.id.tvScan).setOnClickListener(this);
		findViewById(R.id.ivPrevious).setOnLongClickListener(this);
		findViewById(R.id.ivNext).setOnLongClickListener(this);
		findViewById(R.id.tvScan).setOnLongClickListener(this);
		tvFormatHint = (TextView) findViewById(R.id.tvFormatHint);
		tvChannel = (TextView) findViewById(R.id.tvChannel);
		tvHint = (TextView) findViewById(R.id.tvHint);
		mAvinsurfaceview4Front = (AVINSurfaceView) findViewById(R.id.avin_surfaceview);
				
		initData();
		avinV = new AVIN();
		avinA = new AVIN();
		mAvinsurfaceview4Front.mAvinV = avinV;
		rearManager = new RearManager(getApplicationContext());
		rearManager.init(avinV, avinA);
		YeconSettings.initVideoRgb(rgbType);
		startPlay(videoport, audioport);

		Log.i(TAG, "onCreate");
		startAtv();
	}
	
	protected void showSurfaceView(int visible) {
		// TODO Auto-generated method stub
		mAvinsurfaceview4Front.setVisibility(visible);
	}

	@Override
	protected void onResume() {
		//myHandler.sendEmptyMessageDelayed(SET_AUX_VIDEO_PARA,  500);
		SourceManager.acquireSource(sourceTocken);
		resumeAudio();
		uiHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
		uiHandler.sendEmptyMessageDelayed(AVINConstant.CHECK_SIGNAL,  500);
		YeconSettings.initVideoRgb(rgbType);
		super.onResume();
	}
	
	@Override
	protected void onPause() {	
		if (isFinishing()) {
			L.d(TAG, "onPause was cause by finish!!!");
			// myHandler.sendEmptyMessage(RELEASE_RESOURCE);
		}
       // showSurfaceView(View.INVISIBLE);
		super.onPause();
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
		 
		//mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
	
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
		//mDestDir = InputSource.DEST_TYPE_FRONT;
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
	
	private AVIN.OnSignalListener mListenerSignal = new AVIN.OnSignalListener() {
		public void onSignal(int msg, int param1, int param2) {
			L.i(TAG, "onSignal msg:" + msg);
			switch (msg) {
			case AVIN.SIGNAL_READY:
				L.i(TAG, "..............................SIGNAL_READY");
				atvPort.notifyAvinSignalStatus(1);
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
				atvPort.notifyAvinSignalStatus(0);
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
    		 if (mCurrentStatus == InputSource.STATUS_STARTED) {
            	 if (avinA != null) {
                     avinA.stop();
                 }
    		 }
        	 pauseAudio = true;
        }       
    }
    
	private void resumeAudio() {
		L.d(TAG, "restartTargetPlay");
		if (pauseAudio) {
			if (mCurrentStatus == InputSource.STATUS_STARTED) {
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
	
	private int[] getPreferece(){
		int[] intArray = null;
		SharedPreferences prefs = getSharedPreferences(AVINConstant.atv_preference, Context.MODE_PRIVATE);
		int arrayLen = prefs.getInt(AVINConstant.atv_preference_len, 0);
		if(arrayLen>0){
		    intArray=new int[arrayLen];
		    Arrays.fill(intArray, 0);
		    try {
		        JSONArray jsonArray = new JSONArray(prefs.getString(AVINConstant.atv_preference_data, "[]"));
		        for (int i = 0; i < jsonArray.length(); i++) {
		        	intArray[i] = jsonArray.getInt(i);
		        	//Log.i(TAG, "get array["+i+"]"+intArray[i]);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	    return intArray;
	}
	private void savePreferece(int []data){
		if(data!=null && data.length>0){
			SharedPreferences prefs = getSharedPreferences(AVINConstant.atv_preference, Context.MODE_PRIVATE);
		    JSONArray jsonArray = new JSONArray();
		    for (int i=0;i<data.length; i++) {
		      jsonArray.put(data[i]);
		      //Log.i(TAG, "save arrayp["+i+"]"+data[i]);
		    }
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putInt(AVINConstant.atv_preference_len, data.length);
		    editor.putString(AVINConstant.atv_preference_data, jsonArray.toString());
		    editor.commit();
		}
	}
	private void handleAtvStatus(int []status){
		if(null!=status){
			if(null == atvStatus || atvStatus.length!=status.length){
				atvStatus = status;
				uiHandler.sendMessage(uiHandler.obtainMessage(0, status));
			}
			else{
				for(int i=0;i<status.length;i++){
					if(status[i] != atvStatus[i]){
						//status changed
						atvStatus = status;
						uiHandler.sendMessage(uiHandler.obtainMessage(0, status));
						break;
					}
				}
			}
		}
	}
	private void startAtv(){
		if(null!=atvThread && atvThread.isAlive()){
			return;
		}
		atvThread = new HandlerThread("ATV-THREAD");
		atvThread.start();
		atvHandler = new Handler(atvThread.getLooper()){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case 0:
					atvPort.timerSchedule(0, 0);
					handleAtvStatus(atvPort.getStatus());
					this.removeMessages(0);
					this.sendEmptyMessageDelayed(0, ATV_SCHEDULE_TIME);
					break;
				case 1:
					atvPort.sendUserMsg(msg.arg1, msg.arg2);
					handleAtvStatus(atvPort.getStatus());
					break;
				case 88:
					Log.i(TAG, "atvPort.exit");
					atvPort.exit();
					this.getLooper().quit();
					break;
				}
				super.handleMessage(msg);
			}
				
		};
		atvHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//tv region.
				Log.i(TAG, "atvPort.init");
				int tvRegion = AVINCommon.getTvRegion();
				atvPort.init(tvRegion, getPreferece());
				handleAtvStatus(atvPort.getStatus());
				startAtvSchedule();
			}
			
		});
	}
	private void stopAtv(){
		if(null!=atvHandler){
			atvHandler.removeCallbacksAndMessages(null);
			atvHandler.sendEmptyMessage(88);
		}
		//if(null!=atvThread && atvThread.isAlive()){
		//		atvThread.getLooper().quit();
		//}
		//atvPort.exit();
	}
	private void startAtvSchedule(){
		if(null!=atvHandler){
			atvHandler.removeMessages(0);
			atvHandler.sendEmptyMessageDelayed(0, ATV_SCHEDULE_TIME);
		}
	}
	private void stopAtvSchedule(){
		if(null!=atvHandler){
			atvHandler.removeCallbacksAndMessages(null);
		}
	}
	private void sendAtvCmd(int cmd, int key){
		if(null!=atvHandler){
			switch(cmd){
			case ATV_CMD.ATV_CMD_AMS:
			case ATV_CMD.ATV_CMD_PS:
			case ATV_CMD.ATV_CMD_SEARCHDOWN:
			case ATV_CMD.ATV_CMD_SEARCHUP:
				//startAtvSchedule();
				break;
			}
			atvHandler.sendMessage(atvHandler.obtainMessage(1, cmd, key));
		}
	}

	@Override
    protected void onStop() {
	    YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);
        super.onStop();
    }

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//i2cPort.i2cClose();
		stopAtvSchedule();
		savePreferece(atvPort.getConfig());
		stopAtv();	
		unregisterReceiver(mQuickBootListener);
		rearManager.release();
		DestroyAvin();
		super.onDestroy();
		SourceManager.unregisterSourceListener( sourceTocken);
	}
	
	private void showToolBar(boolean show, boolean toogle){
		uiHandler.removeMessages(AVINConstant.HIDE_TOP_BAR);
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
			
			tvFormatHint.setVisibility(View.VISIBLE);
			tvChannel.setVisibility(View.VISIBLE);
			tvHint.setVisibility(View.VISIBLE);
			
			if(avinV_signal_on){
				uiHandler.sendEmptyMessageDelayed(AVINConstant.HIDE_TOP_BAR, AVINConstant.HIDE_TOP_BAT_TIME);
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
			
			tvFormatHint.setVisibility(View.INVISIBLE);
			tvChannel.setVisibility(View.INVISIBLE);
			tvHint.setVisibility(View.INVISIBLE);
		}
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.avin_surfaceview:
			if(!avinV_signal_on){
				showToolBar(true, false);
			}
			else{
				showToolBar(true, true);
			}
			return;
		case R.id.tvScan:
			sendAtvCmd(ATV_CMD.ATV_CMD_AMS, 0);
			break;
		case R.id.ivNext:
			sendAtvCmd(ATV_CMD.ATV_CMD_NEXT_CH, 0);
			break;
		case R.id.ivPrevious:
			sendAtvCmd(ATV_CMD.ATV_CMD_PRE_CH, 0);
			break;
		case R.id.tvFormat:
			sendAtvCmd(ATV_CMD.ATV_CMD_FORMAT, 0);
			break;
		case R.id.ivInc:
			sendAtvCmd(ATV_CMD.ATV_CMD_FREQ_DOWN, 0);
			break;
		case R.id.ivDec:
			sendAtvCmd(ATV_CMD.ATV_CMD_FREQ_UP, 0);
			break;
		}
		if(avinV_signal_on){
			showToolBar(true, false);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tvScan:
			sendAtvCmd(ATV_CMD.ATV_CMD_PS, 0);
			return true;
		case R.id.ivNext:
			sendAtvCmd(ATV_CMD.ATV_CMD_SEARCHDOWN, 0);
			return true;
		case R.id.ivPrevious:
			sendAtvCmd(ATV_CMD.ATV_CMD_SEARCHUP, 0);
			return true;
		}
		return false;
	}
}
