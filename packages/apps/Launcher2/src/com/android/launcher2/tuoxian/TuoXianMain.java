package com.android.launcher2.tuoxian;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.android.internal.app.LocalePicker;
import com.android.launcher.R;
import com.android.launcher2.Launcher;
import com.android.launcher2.tuoxian.SourceStack.SourceItem;
import com.android.launcher2.util.Function;
import com.android.launcher2.util.L;
import com.media.constants.MediaConstants;
import com.tuoxianui.view.DeviceStateMode;
import com.tuoxianui.view.MuteTextView;
import com.tuoxianui.view.AutoDismissDialog;
import com.yecon.common.YeconEnv;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityOptions;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.mcu.McuBaseInfo;
import android.mcu.McuDeviceStatusInfo;
import android.mcu.McuExternalConstant;
import static android.mcu.McuExternalConstant.*;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;
import android.provider.Settings;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class TuoXianMain extends Activity implements View.OnClickListener, OnLongClickListener{
	private View play;
	private View next;
	private View prev;
	private View pause;
	
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	private ActivityManager mActivityManager;
	private String mediaType;
	
	private DeviceStateMode mDeviceStateMode; 
	private AutoDismissDialog dialog;
	private Timer mChangeSourceVolumeTimer = null; 
	McuManager mMcuManager;
	
	private Activity self = this;
	private int backStauts = 0;
	private int sleepStatus = 0;
	int frontSource = McuExternalConstant.MCU_SOURCE_OFF;
	int rearSource = McuExternalConstant.MCU_SOURCE_OFF;
	private boolean initSourceState = false;
	private MediaSource mMediaSource = new MediaSource();
	private boolean hasImgSource = true;
	private SourceStack  mSourceStack = new SourceStack();
	private String resumeOpenAPP = null;
	private static boolean isFirstStart = true;
	private static boolean isNoDelay = false;
	public static boolean isPowerOn = true;
	
	private boolean mIsNight = false;

	private Disposable disposable1;
	private Disposable disposable2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.v("lifecycle " + this.getClass()  + " : onCreate");
		setContentView(R.layout.activity_tx_main);
		SystemProperties.set("persist.sys.isCarlifeOpen", "0");
		SystemProperties.set("persist.sys.music_init_state", "empty");
		SystemProperties.set("persist.sys.video_init_state", "empty");
		SystemProperties.set("persist.sys.top_media", "empty");
		mPref = this.getSharedPreferences("system_settings", Context.MODE_PRIVATE);
		mEditor = mPref.edit();
		mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
		/*if(!TextUtils.isEmpty(mPref.getString("powerMediaType",""))){		
			try {
				mMcuManager.RPC_SetSource(McuExternalConstant.MCU_SOURCE_SD, 0x00);
				L.e("RPC_SetSource:" + McuExternalConstant.MCU_SOURCE_SD + " (sd)");
			} catch (RemoteException e) {
				L.e("error:" + e.toString());
			}
		}*/
		L.e("isFirstStart: " + isFirstStart);
		if(isFirstStart){
			//((MuteTextView)findViewById(R.id.btn_mute_state)).initDefualtVolume();
			//mMcuManager.Init_RPC_Volume();
			isFirstStart = false;
		}
		init();
		
	}
	
	
	
	private void init() {
		//禁用屏幕触摸按钮
		findViewById(R.id.app_radio).setOnClickListener(this);
		findViewById(R.id.app_music).setOnClickListener(this);
		findViewById(R.id.app_video).setOnClickListener(this);
		findViewById(R.id.app_photo).setOnClickListener(this);
		findViewById(R.id.app_bluetooth).setOnClickListener(this);
		findViewById(R.id.app_bt_music).setOnClickListener(this);
		findViewById(R.id.app_car_record).setOnClickListener(this);
		findViewById(R.id.app_car_life).setOnClickListener(this);
		findViewById(R.id.app_phone_connect).setOnClickListener(this);
		findViewById(R.id.app_settings).setOnClickListener(this);
		findViewById(R.id.desktop).setOnClickListener(this);
		findViewById(R.id.test).setOnClickListener(this);
		findViewById(R.id.top_ic_return_home).setEnabled(false);
		findViewById(R.id.settings_item_rear_mute).setOnClickListener(this);
		
		findViewById(R.id.top_ic_bluetooth).setOnLongClickListener(this);
		findViewById(R.id.top_ic_usb).setOnLongClickListener(this);
		findViewById(R.id.top_ic_wifi_level).setOnLongClickListener(this);
		
		play = findViewById(R.id.play);
		pause = findViewById(R.id.pause);
		prev = findViewById(R.id.prev);
		next = findViewById(R.id.next);
		play.setOnClickListener(this);
		pause.setOnClickListener(this);
		prev.setOnClickListener(this);
		next.setOnClickListener(this);
		dialog = new AutoDismissDialog(this);
		findViewById(R.id.btn_mute).setOnClickListener(this);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(MediaConstants.TO_PLAY);
		filter.addAction(MediaConstants.TO_PAUSE);
		filter.addAction(MediaConstants.TO_STOP);
		filter.addAction(MediaConstants.CURRENT_MEDIA_IS_NONE);
		filter.addAction(MediaConstants.CURRENT_MEDIA_IS_RADIO);
		filter.addAction(MediaConstants.CURRENT_MEDIA_IS_MUSIC);
		filter.addAction(MediaConstants.CURRENT_MEDIA_IS_VIDEO);
		filter.addAction(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH);
		filter.addAction(MuteTextView.ACTION_MEDIA_VOLUME_OPEN);
		filter.addAction(MuteTextView.ACTION_MEDIA_VOLUME_MUTE);
		registerReceiver(mReceiver, filter);
		
		IntentFilter sourceStackFilter = new IntentFilter();
		sourceStackFilter.addAction(Context.ACTION_SOURCE_STACK_NOMAL);
		sourceStackFilter.addAction(Context.ACTION_SOURCE_STACK_SD);
		sourceStackFilter.addAction(Context.ACTION_SOURCE_STACK_USB);
		sourceStackFilter.addAction(Context.ACTION_SOURCE_STACK_AUTOEXIT);
		sourceStackFilter.addAction(Context.ACTION_SOURCE_STACK_DO_OUT_SD);
		sourceStackFilter.addAction(Context.ACTION_SOURCE_STACK_DO_OUT_USB);
		/*FIXME:11103 当有源是超时自动退出时,接收
		sourceStackFilter.addAction(Context.ACTION_SOURCE_STACK_DO_OUT_AUTOEXIT);*/
		sourceStackFilter.addAction(MediaConstants.CURRENT_MEDIA_IS_MUSIC);
		sourceStackFilter.addAction(MediaConstants.CURRENT_MEDIA_IS_VIDEO);
		registerReceiver(mSourceStackReceiver, sourceStackFilter);
		
		IntentFilter otherfilter = new IntentFilter();
		otherfilter.addAction(MediaConstants.ACTION_SETTINGS_AUTO_FINISH);
		otherfilter.addAction(YeconConstants.ACTION_QB_PREPOWEROFF);
		otherfilter.addAction(Context.ACTION_ACC_OFF);
		otherfilter.addAction(Context.ACTION_ACC_ON);
		otherfilter.addAction(MediaConstants.ACTION_INIT_MCU_SOURCE);
		otherfilter.addAction("action.set.backlight");
		otherfilter.addAction("action.system.keycode.backlightOn");
		otherfilter.addAction("action.system.keycode.backlightOff");
		otherfilter.addAction("action.system.keycode.next");
		otherfilter.addAction("action.system.keycode.prev");
		otherfilter.addAction("com.wesail.initvol");
		/*otherfilter.addAction("com.baidu.carlife.background.music.start");
		otherfilter.addAction("com.baidu.carlife.background.music.stop");
		otherfilter.addAction("com.baidu.carlife.disconnected");
		otherfilter.addAction("com.baidu.carlife.connecting");
		otherfilter.addAction("com.baidu.carlife.connected");
		otherfilter.addAction("com.baidu.carlife.reconnecting");
		otherfilter.addAction("com.baidu.carlife.reconnected");
		otherfilter.addAction("com.baidu.carlife.usb.reset");
		otherfilter.addAction("com.baidu.carlifevehicle.broadcast.EXIT_APP");
		otherfilter.addAction("com.baidu.carlife.recordservice.start");
		otherfilter.addAction("com.baidu.carlife.recordservice.stop");
		otherfilter.addAction("com.daudio.app.carlife.start");
		otherfilter.addAction("com.daudio.app.carlife.close");*/
		
		registerReceiver(mOtherReceiver, otherfilter);
		mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		mDeviceStateMode = new DeviceStateMode(this);
		//mDeviceStateMode.register();
		//mDeviceStateMode.setStorageCallBack(mStorageCallBack);
		
		try {
			mMcuManager.RPC_RequestMcuInfoChangedListener(mcuListener);
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(), e.toString());
		}
		startService(new Intent(this,NoTouchViewService.class));
	}

	@Override
	protected void onStart() {
		super.onStart();
		L.v("lifecycle " + this.getClass()  + " : onStart");
		
	}
    //**FIXME:11105
    private Disposable mDisposable;
	private boolean isInMain= false;
	//**
	@Override
	protected void onResume() {
		L.v("lifecycle " + this.getClass()  + " : onResume");
		super.onResume();
		///**FIXME:11105 通过停留时间判断是否在launcher界面上
        isInMain= false;
		Observable.timer(500, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Long>() {
					@Override
					public void onSubscribe(Disposable disposable) {
						mDisposable = disposable;
						L.i("rxJava timer onSubscribe" );
					}

					@Override
					public void onNext(Long aLong) {
                        isInMain = true;
						L.i("rxJava timer onNext" + aLong);
					}

					@Override
					public void onError(Throwable throwable) {
						L.e("rxJava timer onError");
					}

					@Override
					public void onComplete() {
						L.i("rxJava timer onComplete");
					}
				});
		//*/
		//FIXME:11109 按home后检查当前媒体源
		Observable.timer(100, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Long>() {

					@Override
					public void onSubscribe(Disposable disposable) {
						if(disposable1 != null){
							disposable1.dispose();
						}
						disposable1 = disposable;
					}

					@Override
					public void onNext(Long aLong) {
						L.i("rxJava timer disposable1 onNext");
						checkRecentMedia();
					}

					@Override
					public void onError(Throwable throwable) {

					}

					@Override
					public void onComplete() {

					}
				});

		//FIXME:11109 1秒后再检查一次,防止先显示主界面,源还没有销毁时,功能条显示错误
		Observable.timer(1000, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Long>() {
					@Override
					public void onSubscribe(Disposable disposable) {
						if(disposable2 != null){
							disposable2.dispose();
						}
						disposable2 = disposable;
					}

					@Override
					public void onNext(Long aLong) {
						L.i("rxJava timer disposable2 onNext");
						checkRecentMedia();
					}

					@Override
					public void onError(Throwable throwable) {

					}

					@Override
					public void onComplete() {

					}
				});
		/*FIXME:11102
		if(!TextUtils.isEmpty(resumeOpenAPP)){
			if(YeconConstants.CARLIFE_PACKAGE_NAME.equals(resumeOpenAPP) || YeconConstants.FAWLINK_PACKAGE_NAME.equals(resumeOpenAPP)){
				openSource(resumeOpenAPP);
			}
			resumeOpenAPP = null;
		}
		*/
		if(SystemProperties.get("persist.sys.rear.mute").equals("1"))
			((ImageView)(((FrameLayout)findViewById(R.id.settings_item_rear_mute)).getChildAt(0))).setImageResource(R.drawable.selector_settings_item_rear_unmute);
		else
			((ImageView)(((FrameLayout)findViewById(R.id.settings_item_rear_mute)).getChildAt(0))).setImageResource(R.drawable.selector_settings_item_rear_mute);
		setScreenBrightness();
	}
	@Override
	protected void onPause() {
		L.v("lifecycle " + this.getClass()  + " : onPause");
        ///**FIXME:11105
		if(mDisposable != null){
            mDisposable.dispose();
        }
		if(disposable1 != null){
			disposable1.dispose();
		}
		if(disposable2 != null){
			disposable2.dispose();
		}
        L.i("in main : " + isInMain);
		isInMain = false;
		//*/
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		L.v("lifecycle " + this.getClass()  + " : onDestroy");
		isNoDelay = true;
		//mDeviceStateMode.unRegister();
		unregisterReceiver(mReceiver);
		unregisterReceiver(mOtherReceiver);
		unregisterReceiver(mSourceStackReceiver);
		try {
			mMcuManager.RPC_RemoveMcuInfoChangedListener(mcuListener);
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(), e.toString());
		}
		//stopService(new Intent(this,NoTouchViewService.class));
		super.onDestroy();
	}
	
	int codeToOpenDesktop = 0;
	@Override
	public boolean onLongClick(View v) {

		switch (v.getId()) {
		case R.id.top_ic_wifi_level:{
			codeToOpenDesktop  = codeToOpenDesktop + 1;
			L.e("codeToOpenDesktop : "  + codeToOpenDesktop);
		}
		break;
		case R.id.top_ic_bluetooth:{
			if(codeToOpenDesktop == 1){
				codeToOpenDesktop  = codeToOpenDesktop + 10;
			}else{
				codeToOpenDesktop  = 0;
			}
			L.e("codeToOpenDesktop : "  + codeToOpenDesktop);
		}
		break;
		case R.id.top_ic_usb:{
			if(codeToOpenDesktop == 11){
				startActivity(new Intent(this, Launcher.class));
			}else{
				codeToOpenDesktop  = 0;
			}
			L.e("codeToOpenDesktop : "  + codeToOpenDesktop);
		}
		break;
		default:
			break;
		}
		return false; 
	}
	
	private boolean isHardDisk = false;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.app_radio:
				Function.onStartActivity(this, "com.yecon.fmradio", "com.yecon.fmradio.FMRadioMainActivity");
				isHardDisk = false;
				break;
			case R.id.app_music:
				//if(mDeviceStateMode.getStateStorageValue() > 0){
					Function.onStartActivity(this, "com.yecon.music", "com.yecon.music.MusicPlaybackMainActivity");
					if(MediaConstants.CURRENT_MEDIA_IS_CARLIFE.equals(mMediaSource.currentName) || MediaConstants.CURRENT_MEDIA_IS_FAWLINK.equals(mMediaSource.currentName)){
						checkRecentMedia();
					}
				//}else{
				//	dialog.show();
				//}
				if(mDeviceStateMode.getStateStorageValue()==0)
					isHardDisk = true;
				else
					isHardDisk = false;
				break;
			case R.id.app_video:
				//if(mDeviceStateMode.getStateStorageValue() > 0){
					Function.onStartActivity(this, "com.yecon.video", "com.yecon.video.VideoPlaybackMainActivity");
				//}else{
				//	dialog.show();
				//}
				if(mDeviceStateMode.getStateStorageValue()==0)
					isHardDisk = true;
				else
					isHardDisk = false;
				break;
			case R.id.app_photo:
				//if(mDeviceStateMode.getStateStorageValue() > 0){
					if(frontSource == McuExternalConstant.MCU_SOURCE_RADIO){
					sendSourceMCU2(McuExternalConstant.MCU_SOURCE_SD);
					}
					Function.onStartActivity(this, "com.yecon.imagebrowser", "com.yecon.imagebrowser.ImageBrowserActivity");
				//}else{
				//	dialog.show();
				//}
				isHardDisk = false;
				break;
			case R.id.app_bluetooth:
				//Function.onStartActivity(this, "com.autochips.bluetooth", "com.autochips.bluetooth.MainBluetoothActivity");
				
				{
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setComponent(new ComponentName("com.autochips.bluetooth", "com.autochips.bluetooth.MainBluetoothActivity"));
					intent.putExtra("startTag", 0);
					intent.putExtra("godial", true);
					startActivity(intent);
				}
				break;
			case R.id.app_bt_music:
				
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setComponent(new ComponentName("com.autochips.bluetooth", "com.autochips.bluetooth.MainBluetoothActivity"));
				intent.putExtra("startTag", 1);
				startActivity(intent);
				isHardDisk = false;
				break;
			case R.id.app_car_record:
                //**FIXME:11106 如果现在是main界面上打开应用,退出音乐、视频
                if(isInMain) {
                    sendOrderedBroadcast(new Intent(MediaConstants.DO_EXIT_APP), null);
                }
                //*/
				Function.onStartActivity(this, "com.wesail.tdr", "com.wesail.tdr.ui.MainMenuAcitivity");
				break;
			case R.id.app_car_life:
				checkRecentMedia();
				mMediaSource.setData(MediaConstants.CURRENT_MEDIA_IS_CARLIFE, null);

				//**FIXME:11106 如果现在是main界面上打开应用,退出音乐、视频
                if(isInMain) {
                    sendOrderedBroadcast(new Intent(MediaConstants.DO_EXIT_APP), null);
                }
                //*/

				if(mediaType == MediaConstants.CURRENT_MEDIA_IS_VIDEO){
					//ActivityManager mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE); 
					//mAm.forceStopPackage("com.yecon.video");
					
					if(currentSource != McuExternalConstant.MCU_SOURCE_SD && currentSource != McuExternalConstant.MCU_SOURCE_BT){
						sendSourceMCU2(McuExternalConstant.MCU_SOURCE_SD);
					}
					findViewById(R.id.app_car_life).postDelayed(new Runnable() {
						@Override
						public void run() {
							String packageName = YeconConstants.CARLIFE_PACKAGE_NAME;
							String className = YeconConstants.CARLIFE_START_ACTIVITY;
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_LAUNCHER);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setComponent(new ComponentName(packageName, className));
							startActivity(intent);
							
							{
					        	Intent intent2 = new Intent(Context.ACTION_SOURCE_STACK_NOMAL);
					        	intent2.putExtra(MediaConstants.EXTRA_APK_PACKAGENAME, YeconConstants.CARLIFE_PACKAGE_NAME);
					        	intent2.putExtra(MediaConstants.EXTRA_APK_ACTIVITY, YeconConstants.CARLIFE_START_ACTIVITY);
					        	sendOrderedBroadcast(intent2,null);
					        }
							
							Intent voiceIntent = new Intent("com.yecon.action.closevoice");
							sendBroadcast(voiceIntent);
						}
					}, 300);
					SystemProperties.set("persist.sys.top_media", "empty");
				}else{
					sendControlBroadcast(MediaConstants.DO_INACTIVE);
					if(currentSource != McuExternalConstant.MCU_SOURCE_SD && currentSource != McuExternalConstant.MCU_SOURCE_BT){
						sendSourceMCU2(McuExternalConstant.MCU_SOURCE_SD);
					}
					//sendBroadcast(new Intent(MediaConstants.CURRENT_MEDIA_IS_CARLIFE));
					Function.onCarlife(this);
					SystemProperties.set("persist.sys.top_media", "empty");
				}
				isHardDisk = false;
				break;
			case R.id.app_phone_connect:
				checkRecentMedia();
				mMediaSource.setData(MediaConstants.CURRENT_MEDIA_IS_FAWLINK, null);
                //**FIXME:11106 如果现在是main界面上打开应用,退出音乐、视频
                if(isInMain) {
                    sendOrderedBroadcast(new Intent(MediaConstants.DO_EXIT_APP), null);
                }
                //*/
				if(mediaType == MediaConstants.CURRENT_MEDIA_IS_VIDEO){
					//ActivityManager mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE); 
					//mAm.forceStopPackage("com.yecon.video");
					
					if(currentSource != McuExternalConstant.MCU_SOURCE_SD && currentSource != McuExternalConstant.MCU_SOURCE_BT){
						sendSourceMCU2(McuExternalConstant.MCU_SOURCE_SD);
					}
					findViewById(R.id.app_phone_connect).postDelayed(new Runnable() {
						@Override
						public void run() {
							String packageName = YeconConstants.FAWLINK_PACKAGE_NAME;
							String className = YeconConstants.FAWLINK_START_ACTIVITY;
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_LAUNCHER);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setComponent(new ComponentName(packageName, className));
							startActivity(intent);
							
							{
					        	Intent intent2 = new Intent(Context.ACTION_SOURCE_STACK_NOMAL);
					        	intent2.putExtra(MediaConstants.EXTRA_APK_PACKAGENAME, YeconConstants.FAWLINK_PACKAGE_NAME);
					        	intent2.putExtra(MediaConstants.EXTRA_APK_ACTIVITY, YeconConstants.FAWLINK_START_ACTIVITY);
					        	sendOrderedBroadcast(intent2,null);
					        }
							
							Intent voiceIntent = new Intent("com.yecon.action.closevoice");
							sendBroadcast(voiceIntent);
						}
					}, 300);
					SystemProperties.set("persist.sys.top_media", "empty");
				}else{
					sendControlBroadcast(MediaConstants.DO_INACTIVE);
					if(currentSource != McuExternalConstant.MCU_SOURCE_SD && currentSource != McuExternalConstant.MCU_SOURCE_BT){
						sendSourceMCU2(McuExternalConstant.MCU_SOURCE_SD);
					}
					//sendBroadcast(new Intent(MediaConstants.CURRENT_MEDIA_IS_CARLIFE));
					Function.onPhoneConnect(this);
					SystemProperties.set("persist.sys.top_media", "empty");
				}
				isHardDisk = false;
				
				break;
			case R.id.app_settings:
                //**FIXME:11106 如果现在是main界面上打开应用,退出音乐、视频
                if(isInMain) {
                    sendOrderedBroadcast(new Intent(MediaConstants.DO_EXIT_APP), null);
                }
                //*/
				Function.onStartActivity(this, "com.yecon.carsetting", "com.yecon.carsetting.tuoxian.SettingsAcitivity");
				break;
			case R.id.play:
				L.e("play");
				pause.setVisibility(View.VISIBLE);
				play.setVisibility(View.GONE);
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				((MuteTextView)findViewById(R.id.btn_mute_state)).setMute(false,400);
				*/
				sendControlBroadcast(MediaConstants.DO_PLAY);
				break;
			case R.id.pause:
				L.e("pause");
				pause.setVisibility(View.GONE);
				play.setVisibility(View.VISIBLE);
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				*((MuteTextView)findViewById(R.id.btn_mute_state)).setMute(false,400);
				*/
				sendControlBroadcast(MediaConstants.DO_PAUSE);
				break;
			case R.id.prev:
				L.e("prev");
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				*((MuteTextView)findViewById(R.id.btn_mute_state)).setMute(false,400);
				*/
				sendControlBroadcast(MediaConstants.DO_PREV);
				break;
			case R.id.next:
				L.e("next");
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				*((MuteTextView)findViewById(R.id.btn_mute_state)).setMute(false,400);
				*/
				sendControlBroadcast(MediaConstants.DO_NEXT);
				break;
			case R.id.btn_mute:
				((MuteTextView)findViewById(R.id.btn_mute_state)).toggle();
				break;
			case R.id.desktop:
				startActivity(new Intent(this, Launcher.class));
				break;
			case R.id.test:{
				findViewById(R.id.test).setVisibility(View.VISIBLE);
			}
//				sendSourceMCU3(McuExternalConstant.MCU_SOURCE_SD);
				break;
			case R.id.settings_item_rear_mute:{
				//Log.d("TEST","SystemProperties.get(persist.sys.rear.mute) "+SystemProperties.get("persist.sys.rear.mute"));
				if(SystemProperties.get("persist.sys.rear.mute").equals("1"))
					((ImageView)(((FrameLayout)findViewById(R.id.settings_item_rear_mute)).getChildAt(0))).setImageResource(R.drawable.selector_settings_item_rear_mute);
				else
					((ImageView)(((FrameLayout)findViewById(R.id.settings_item_rear_mute)).getChildAt(0))).setImageResource(R.drawable.selector_settings_item_rear_unmute);
				send_RearMute();
			}
			default:
				break;
		}
	}

	public void send_RearMute(){
		try {
			// mMcuManager.RPC_SetVolumeMute(true);
			byte[] param = new byte[4];
			param[0] = (byte) 0x00;
			param[1] = (byte) 0x00;
			param[2] = (byte) 0x00;
			param[3] = (byte) 0x00;

			mMcuManager.RPC_KeyCommand(K_REAR_MUTE, param);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		L.e("keyCode: " + keyCode );
		return super.onKeyDown(keyCode, event);
	}
	
	private void setControlViewEnable(List<View> enableViews,List<View> unenableViews){
		if(enableViews != null){		
			for (View view : enableViews) {
				view.setEnabled(true);
			}
		}
		if(unenableViews != null){			
			for (View view : unenableViews) {
				view.setEnabled(false);
			}
		}
	}
	
	private void setControlViewEnable(int[] enableViewIds,int[] unenableViewIds){
		List<View> enableViews = new ArrayList<View>();
		List<View> unenableViews = new ArrayList<View>();		
		for (int viewId : enableViewIds) {
			View view = findViewById(viewId);
			if(view !=null){
				enableViews.add(view);
			}
		}	
		for (int viewId : unenableViewIds) {
			View view = findViewById(viewId);
			if(view !=null){
				unenableViews.add(view);
			}	
		}
		setControlViewEnable(enableViews, unenableViews);
 	}
	
	private void startActivitySafe(String packageName, String className) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(new ComponentName(packageName, className));
		startActivity(intent);
	}
	
	private void sendControlBroadcast(String order){
		/*if(TextUtils.isEmpty(receiverPermission)) return;*/
		L.w("media_type  " + mediaType);
		Intent intent = new Intent();
		boolean isMediaControl = false;
		
		if(MediaConstants.DO_PLAY.equalsIgnoreCase(order)){
			isMediaControl = true;
			intent.setAction(order);
			
		}else if(MediaConstants.DO_PAUSE.equalsIgnoreCase(order)){
			isMediaControl = true;
			intent.setAction(order);
			
		}else if(MediaConstants.DO_PREV.equalsIgnoreCase(order)){
			intent.setAction(order);
			isMediaControl = true;	
		}else if(MediaConstants.DO_NEXT.equalsIgnoreCase(order)){
			intent.setAction(order);
			isMediaControl = true;
			
		}else if(MediaConstants.DO_STOP.equalsIgnoreCase(order)){
			intent.setAction(order);
			isMediaControl = true;
			
		}
		else if(MediaConstants.CURRENT_MEDIA.equalsIgnoreCase(order)){
			intent.setAction(order);
		}
		if(!TextUtils.isEmpty(intent.getAction())){
			/*if(isMediaControl){	
				sendOrderedBroadcast(intent,mediaType);
			}else{
				sendOrderedBroadcast(intent,null);
			}*/
			intent.putExtra("media_type", mediaType);
			sendOrderedBroadcast(intent,null);
		}
		
	}
	private boolean hasImageSource(){
		if(mActivityManager == null){
			mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		}
		List<RunningTaskInfo> tasks =  mActivityManager.getRunningTasks(10);
		for(RunningTaskInfo t : tasks){
			String packageName = t.topActivity.getPackageName();
			if("com.yecon.imagebrowser".equals(packageName)){
				return true;
			}
		}
		return false;
	}
	private void checkRecentMedia(){
		try {
			hasImgSource = false;
			mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasks =  mActivityManager.getRunningTasks(10);
			for(RunningTaskInfo t : tasks){
				String packageName = t.topActivity.getPackageName();
				//String activityName = t.topActivity.getClassName();
				L.e("packageName:" + packageName + " topActivity:" + t.topActivity.getClassName() + " num: " + t.numActivities);
				//Log.d("TEST","activityName:"+activityName);
				if("com.yecon.imagebrowser".equals(packageName)){
					hasImgSource = true;
					setMediaType(MediaConstants.CURRENT_MEDIA_IS_PHOTO);
					break;
				}
				if("com.yecon.music".equals(packageName)){
					setMediaType(MediaConstants.CURRENT_MEDIA_IS_MUSIC);
					break;
				}else if("com.yecon.video".equals(packageName)){
					setMediaType(MediaConstants.CURRENT_MEDIA_IS_VIDEO);
					break;
				}else if("com.yecon.fmradio".equals(packageName)){
					setMediaType(MediaConstants.CURRENT_MEDIA_IS_RADIO);
					break;
				}else if("com.autochips.bluetooth".equals(packageName)
				//&&!activityName.contains("TuoXianDialActivity")
				){
					setMediaType(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH);
					break;
				}else if("com.baidu.carlifevehicle".equals(packageName)){
					setMediaType(MediaConstants.CURRENT_MEDIA_IS_CARLIFE);
					break;
				}else if(YeconConstants.FAWLINK_PACKAGE_NAME.equals(packageName)){
					setMediaType(MediaConstants.CURRENT_MEDIA_IS_FAWLINK);
					break;
				}
				setMediaType("");
			}
					
			L.e("receiverPermission:" + mediaType);
			List<View> enableViews = new ArrayList<View>();
			List<View> unenableViews = new ArrayList<View>();	
			if(TextUtils.isEmpty(mediaType)){
				unenableViews.add(play);
				unenableViews.add(pause);
				unenableViews.add(prev);
				unenableViews.add(next);
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.GONE);
				setControlViewEnable(enableViews,unenableViews);
				
				Intent intent = new  Intent();
				/*FIXME:99001 已经忘了为什么为空时,要转给蓝牙源
				intent.setAction(MediaConstants.ACTION_GET_BT_CURRENT_STATE);
				intent.putExtra("media_type", MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH);
				*/
				sendOrderedBroadcast(intent,null);
				L.e("set MediaConstants.ACTION_GET_BT_CURRENT_STATE");
				
			}else if(MediaConstants.CURRENT_MEDIA_IS_MUSIC.equals(mediaType)){
				enableViews.add(play);
				enableViews.add(pause);
				enableViews.add(prev);
				enableViews.add(next);
				
				setControlViewEnable(enableViews,unenableViews);
				
				Intent intent = new Intent();
				intent.setAction(MediaConstants.CURRENT_MEDIA);
				intent.putExtra("media_type", mediaType);
				sendOrderedBroadcast(intent, null);
			}else if(MediaConstants.CURRENT_MEDIA_IS_VIDEO.equals(mediaType)){
				enableViews.add(play);
				enableViews.add(pause);
				enableViews.add(prev);
				enableViews.add(next);
				
				setControlViewEnable(enableViews,unenableViews);
				
				Intent intent = new Intent();
				intent.setAction(MediaConstants.CURRENT_MEDIA);
				intent.putExtra("media_type", mediaType);
				sendOrderedBroadcast(intent, null);
			}else if(MediaConstants.CURRENT_MEDIA_IS_RADIO.equals(mediaType)){
				unenableViews.add(play);
				unenableViews.add(pause);
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.GONE);
				enableViews.add(prev);
				enableViews.add(next);
				setControlViewEnable(enableViews,unenableViews);
			}else if(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH.equals(mediaType)){
				enableViews.add(play);
				enableViews.add(pause);
				enableViews.add(prev);
				enableViews.add(next);
				
				setControlViewEnable(enableViews,unenableViews);
				
				Intent intent = new Intent();
				intent.setAction(MediaConstants.CURRENT_MEDIA);
				intent.putExtra("media_type", mediaType);
				sendOrderedBroadcast(intent, null);
			}else if(MediaConstants.CURRENT_MEDIA_IS_CARLIFE.equals(mediaType)||MediaConstants.CURRENT_MEDIA_IS_FAWLINK.equals(mediaType)){
				unenableViews.add(play);
				unenableViews.add(pause);
				unenableViews.add(prev);
				unenableViews.add(next);
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.GONE);
				setControlViewEnable(enableViews,unenableViews);
			}else if(MediaConstants.CURRENT_MEDIA_IS_PHOTO.equals(mediaType)){
				unenableViews.add(play);
				unenableViews.add(pause);
				unenableViews.add(prev);
				unenableViews.add(next);
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.GONE);
				setControlViewEnable(enableViews,unenableViews);
			}
		} catch (Exception e) {
			L.e(e.toString());
		}
		
		if((MediaConstants.CURRENT_MEDIA_IS_CARLIFE.equals(mMediaSource.currentName) || MediaConstants.CURRENT_MEDIA_IS_FAWLINK.equals(mMediaSource.currentName))
				 && mMediaSource.prevIsPlay 
				&& !mMediaSource.currentName.equals(mediaType)){
			findViewById(R.id.play).callOnClick(); //FIXME: 这个可能可以改一汽问题点21的问题,先标记一下
		}
		mMediaSource.setData(mediaType, pause);
	}
	
	private int currentSource = McuExternalConstant.MCU_SOURCE_OFF;
	private void sendSourceMCU (int source){
		L.i("sendSourceMCU source:" + source +" currentSource:" + currentSource);
		int time = 1000;
		if(currentSource == source && !initSourceState){
			return;
		}
		
		if(currentSource == McuExternalConstant.MCU_SOURCE_BT && source  == McuExternalConstant.MCU_SOURCE_SD){
			time = 2000;
		}
		
		if(mChangeSourceVolumeTimer != null){
			mChangeSourceVolumeTimer.cancel();
			mChangeSourceVolumeTimer = new Timer();
		}else{
			mChangeSourceVolumeTimer = new Timer();
		}
		mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
		
		mChangeSourceVolumeTimer.schedule(new TimerTask() {	
			@Override
			public void run() {
				try {
					mMcuManager.RPC_SetVolumeMute(false);
				} catch (Exception e) {
					Log.e(this.getClass().getName(), e.toString());
				}
			}
		}, time);
		L.i("newSource:" + source +" oldSource:" + currentSource + " isNoDelay:"  + isNoDelay);
		try {
			//mMcuManager.RPC_SetSource(McuExternalConstant.MCU_SOURCE_OFF, currentSource);
			frontSource = source;
			rearSource = currentSource;
			///*FIXME: 11108 MCU问题 切源声道有问题
			/*if(source == McuExternalConstant.MCU_SOURCE_OFF && currentSource  == McuExternalConstant.MCU_SOURCE_RADIO){
				mMcuManager.RPC_SetSource(McuExternalConstant.MCU_SOURCE_OFF, McuExternalConstant.MCU_SOURCE_OFF);
			}else {
				mMcuManager.RPC_SetSource(source, currentSource);
			}*/
			//*/

			mMcuManager.RPC_SetSource(source, currentSource);
			if(initSourceState){
				initSourceState = false;
			}else{
				if(isNoDelay){
					isNoDelay = false;
				}else{
					mMcuManager.RPC_SetVolumeMute(true);
				}
			}
			currentSource = source;
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(), e.toString());
		}
	}
	
	private void sendSourceMCU2(int source){
		L.i("sendSourceMCU2 source:" + source +" currentSource:" + currentSource);
		if(currentSource == source){
			return;
		}	
		mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
		try {
			frontSource = source;
			rearSource = currentSource;
			mMcuManager.RPC_SetSource(source, currentSource);
			currentSource = source;
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(), e.toString());
		}
	}
	
	private void sendSourceMCU3(int source){
		mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
		try {
			frontSource = source;
			mMcuManager.RPC_SetSource(source, rearSource);
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(), e.toString());
		}
	}
	
	private void openMediaForPowerOn(){
			
		String mMediaType = mPref.getString("powerMediaType","");
		String mediaTypeIsNoneToOpen = mPref.getString("powerMediaType_is_none_to_open","");
		
		//photo source is open?
		if(hasImageSource()){
			findViewById(R.id.app_photo).callOnClick();
			return;
		}
		setMediaType(mMediaType);
		if(openApp(mMediaType)){
//			L.e("11mediaType openApp(mMediaType)");
		}else if(openApp(mediaTypeIsNoneToOpen)){ 
//			L.e("11mediaType openApp(mediaTypeIsNoneToOpen)");
		}
		
		/*L.e("11mediaType1 :   " + mMediaType);
		L.e("11mediaType2 :   " + mediaTypeIsNoneToOpen);*/
	}
	
	private boolean openApp(String mMediaType){
		L.e("11mediaType openApp : " + mMediaType); 
		if(MediaConstants.CURRENT_MEDIA_IS_RADIO.equals(mMediaType)){
			initSourceState = true;
			findViewById(R.id.app_radio).callOnClick();
			return true;
		}else if(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH.equals(mMediaType)){
			findViewById(R.id.app_bt_music).callOnClick();
			return true;
		}else if(MediaConstants.CURRENT_MEDIA_IS_MUSIC.equals(mMediaType)){
			//if(mDeviceStateMode.getStateStorageValue() > 0){
				findViewById(R.id.app_music).callOnClick();
			//}
			return true;
		}else if(MediaConstants.CURRENT_MEDIA_IS_VIDEO.equals(mMediaType)){
			//if(mDeviceStateMode.getStateStorageValue() > 0){
				findViewById(R.id.app_video).callOnClick();
			//}
			return true;
		}else if(MediaConstants.CURRENT_MEDIA_IS_CARLIFE.equals(mMediaType)){
			findViewById(R.id.app_car_life).callOnClick();
			return true;
		}else if(MediaConstants.CURRENT_MEDIA_IS_FAWLINK.equals(mMediaType)){
			findViewById(R.id.app_phone_connect).callOnClick();
			return true;
		}
		return false;
	}
	
	private void openSource(String packageName){
		L.e("sourceItem:packageName = " + packageName);
		if(YeconConstants.FMRADIO_PACKAGE_NAME.equals(packageName)){
			L.e("sourceItem:open packageName = " + YeconConstants.FMRADIO_PACKAGE_NAME);
			findViewById(R.id.app_radio).callOnClick();
		}if(YeconConstants.MUSIC_PACKAGE_NAME.equals(packageName)){
			L.e("sourceItem:open packageName = " + YeconConstants.MUSIC_PACKAGE_NAME);
			findViewById(R.id.app_music).callOnClick();
		}if(YeconConstants.VIDEO_PACKAGE_NAME.equals(packageName)){
			L.e("sourceItem:open packageName = " + YeconConstants.VIDEO_PACKAGE_NAME);
			findViewById(R.id.app_video).callOnClick();
		}if(YeconConstants.PICBROWSER_PACKAGE_NAME.equals(packageName)){
			L.e("sourceItem:open packageName = " + YeconConstants.PICBROWSER_PACKAGE_NAME);
			findViewById(R.id.app_photo).callOnClick();
		}if(YeconConstants.BLUETOOTH_PACKAGE_NAME.equals(packageName)){
			L.e("sourceItem:open packageName = " + YeconConstants.BLUETOOTH_PACKAGE_NAME);
			findViewById(R.id.app_bluetooth).callOnClick();
		}if(YeconConstants.CARLIFE_PACKAGE_NAME.equals(packageName)){
			L.e("sourceItem:open packageName = " + YeconConstants.CARLIFE_PACKAGE_NAME);
			findViewById(R.id.app_car_life).callOnClick();
		}if(YeconConstants.FAWLINK_PACKAGE_NAME.equals(packageName)){
			L.e("sourceItem:open packageName = " + YeconConstants.CARLIFE_PACKAGE_NAME);
			findViewById(R.id.app_phone_connect).callOnClick();
		}
	}
	
	private void sendMediaAttachOther(String packageName){
		if(YeconConstants.MUSIC_PACKAGE_NAME.equals(packageName) || YeconConstants.VIDEO_PACKAGE_NAME.equals(packageName)){
			Intent intent = new Intent(MediaConstants.ACTION_SOURCE_ATTACH_OTHER_STORAGE);
			intent.putExtra(Context.EXTRA_APK_PACKAGENAME, packageName);
			sendOrderedBroadcast(intent, null);
		}
	}
	
	private void doDeviceOut(SourceItem oldTopSourceItem,SourceItem newTopSourceItem){
		L.e("sourceItem:" + oldTopSourceItem + "   new:" + newTopSourceItem);
		if(oldTopSourceItem != null && newTopSourceItem != null){
			L.e("sourceItem:" + oldTopSourceItem.apkPackageName + "   new:" + newTopSourceItem.apkPackageName);
			if(newTopSourceItem.apkPackageName.equals(oldTopSourceItem.apkPackageName)){
				if(YeconConstants.CARLIFE_PACKAGE_NAME.equalsIgnoreCase(newTopSourceItem.apkPackageName)
				||YeconConstants.FAWLINK_PACKAGE_NAME.equalsIgnoreCase(newTopSourceItem.apkPackageName)){
					
				}else{
					sendMediaAttachOther(newTopSourceItem.apkPackageName);
				}
			}else{
				if(YeconConstants.MUSIC_PACKAGE_NAME.equalsIgnoreCase(oldTopSourceItem.apkPackageName) 
						&& YeconConstants.CARLIFE_PACKAGE_NAME.equalsIgnoreCase(newTopSourceItem.apkPackageName)){
					resumeOpenAPP = YeconConstants.CARLIFE_PACKAGE_NAME; 
					sendOrderedBroadcast(new Intent(MediaConstants.DO_EXIT_APP),null);
				}else if(YeconConstants.MUSIC_PACKAGE_NAME.equalsIgnoreCase(oldTopSourceItem.apkPackageName) 
						&& YeconConstants.FAWLINK_PACKAGE_NAME.equalsIgnoreCase(newTopSourceItem.apkPackageName)){
					resumeOpenAPP = YeconConstants.FAWLINK_PACKAGE_NAME; 
					sendOrderedBroadcast(new Intent(MediaConstants.DO_EXIT_APP),null);
				}else if(YeconConstants.VIDEO_PACKAGE_NAME.equalsIgnoreCase(oldTopSourceItem.apkPackageName) 
						&& YeconConstants.CARLIFE_PACKAGE_NAME.equalsIgnoreCase(newTopSourceItem.apkPackageName)){
					resumeOpenAPP = YeconConstants.CARLIFE_PACKAGE_NAME;
					sendOrderedBroadcast(new Intent(MediaConstants.DO_EXIT_APP),null);
				}else if(YeconConstants.VIDEO_PACKAGE_NAME.equalsIgnoreCase(oldTopSourceItem.apkPackageName) 
						&& YeconConstants.FAWLINK_PACKAGE_NAME.equalsIgnoreCase(newTopSourceItem.apkPackageName)){
					resumeOpenAPP = YeconConstants.FAWLINK_PACKAGE_NAME;
					sendOrderedBroadcast(new Intent(MediaConstants.DO_EXIT_APP),null);
				}else if(!isHardDisk){
					resumeOpenAPP = null;
					openSource(newTopSourceItem.apkPackageName); 
				}
			}
		}else if(oldTopSourceItem != null && newTopSourceItem == null){
			sendMediaAttachOther(oldTopSourceItem.apkPackageName);
		}else if(oldTopSourceItem != null){
			sendMediaAttachOther(oldTopSourceItem.apkPackageName);
		}
	}

	private void requestPushStackForDevice(String hasNormalName,String pushMediaSourcePackageName,String deviceType){
		if(!MediaConstants.DEVICE_SD.equals(deviceType) && !MediaConstants.DEVICE_USB.equals(deviceType)){
			return;
		}
		
		SourceItem topSourceItem = mSourceStack.getTop();
		if(topSourceItem == null){
			return;
		}
		String packageName = topSourceItem.apkPackageName == null ? "" : topSourceItem.apkPackageName;
		//L.e("1111111111111111111111111111");
		if(packageName.equals(hasNormalName)){
			//L.e("2222222222222222222222222222");
			if(YeconConstants.MUSIC_PACKAGE_NAME.equalsIgnoreCase(pushMediaSourcePackageName)){
				if(MediaConstants.DEVICE_SD.equals(deviceType)){
					mSourceStack.pushDeviceSD(YeconConstants.MUSIC_PACKAGE_NAME, YeconConstants.MUSIC_START_ACTIVITY);
				}else if(MediaConstants.DEVICE_USB.equals(deviceType)){
					mSourceStack.pushDeviceUSB(YeconConstants.MUSIC_PACKAGE_NAME, YeconConstants.MUSIC_START_ACTIVITY);
				}		
			}else if(YeconConstants.VIDEO_PACKAGE_NAME.equalsIgnoreCase(pushMediaSourcePackageName)){
				if(MediaConstants.DEVICE_SD.equals(deviceType)){
					mSourceStack.pushDeviceSD(YeconConstants.VIDEO_PACKAGE_NAME, YeconConstants.VIDEO_START_ACTIVITY);
				}else if(MediaConstants.DEVICE_USB.equals(deviceType)){
					mSourceStack.pushDeviceUSB(YeconConstants.VIDEO_PACKAGE_NAME, YeconConstants.VIDEO_START_ACTIVITY);
				}		
			}
		}
	}
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			List<View> enableViews = new ArrayList<View>();
			List<View> unenableViews = new ArrayList<View>();	
			String action = intent.getAction();
			L.e("mReceiver action : " + action);
			if		(MediaConstants.TO_PLAY.equalsIgnoreCase(action)){
				play.setVisibility(View.GONE);
				pause.setVisibility(View.VISIBLE);
				
			}else if(MediaConstants.TO_PAUSE.equalsIgnoreCase(action)){
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.GONE);
				
			}else if(MediaConstants.TO_STOP.equalsIgnoreCase(action)){
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.GONE);
				
			}else if(MediaConstants.CURRENT_MEDIA_IS_NONE.equalsIgnoreCase(action)){
				unenableViews.add(play);
				unenableViews.add(pause);
				unenableViews.add(prev);
				unenableViews.add(next);
				sendSourceMCU(McuExternalConstant.MCU_SOURCE_OFF);
				SystemProperties.set("persist.sys.top_media", "empty");
			}else if(MediaConstants.CURRENT_MEDIA_IS_RADIO.equalsIgnoreCase(action)){
				unenableViews.add(play);
				unenableViews.add(pause);
				enableViews.add(prev);
				enableViews.add(next);
				sendSourceMCU(McuExternalConstant.MCU_SOURCE_RADIO);
				SystemProperties.set("persist.sys.top_media", "empty");
				
			}else if(MediaConstants.CURRENT_MEDIA_IS_MUSIC.equalsIgnoreCase(action)){
				enableViews.add(play);
				enableViews.add(pause);
				enableViews.add(prev);
				enableViews.add(next);
				sendSourceMCU(McuExternalConstant.MCU_SOURCE_SD);
				String state = intent.getStringExtra(MediaConstants.CURRENT_MEDIA);
				if		(MediaConstants.TO_PLAY.equalsIgnoreCase(state)){
					play.setVisibility(View.GONE);
					pause.setVisibility(View.VISIBLE);
				}else if(MediaConstants.TO_PAUSE.equalsIgnoreCase(state)){
					play.setVisibility(View.VISIBLE);
					pause.setVisibility(View.GONE);
				}else if(MediaConstants.TO_STOP.equalsIgnoreCase(state)){
					play.setVisibility(View.VISIBLE);
					pause.setVisibility(View.GONE);
				}
				SystemProperties.set("persist.sys.top_media", "empty");
			}else if(MediaConstants.CURRENT_MEDIA_IS_VIDEO.equalsIgnoreCase(action)){
				enableViews.add(play);
				enableViews.add(pause);
				enableViews.add(prev);
				enableViews.add(next);
				sendSourceMCU(McuExternalConstant.MCU_SOURCE_SD);
				String state = intent.getStringExtra(MediaConstants.CURRENT_MEDIA);
				if		(MediaConstants.TO_PLAY.equalsIgnoreCase(state)){
					play.setVisibility(View.GONE);
					pause.setVisibility(View.VISIBLE);
				}else if(MediaConstants.TO_PAUSE.equalsIgnoreCase(state)){
					play.setVisibility(View.VISIBLE);
					pause.setVisibility(View.GONE);
				}else if(MediaConstants.TO_STOP.equalsIgnoreCase(state)){
					play.setVisibility(View.VISIBLE);
					pause.setVisibility(View.GONE);
				}
				SystemProperties.set("persist.sys.top_media", "empty");
			}else if(MediaConstants.CURRENT_MEDIA_IS_CARLIFE.equalsIgnoreCase(action) || MediaConstants.CURRENT_MEDIA_IS_FAWLINK.equalsIgnoreCase(action)){
				unenableViews.add(play);
				unenableViews.add(pause);
				unenableViews.add(prev);
				unenableViews.add(next);
				sendSourceMCU(McuExternalConstant.MCU_SOURCE_SD);
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.GONE);
				SystemProperties.set("persist.sys.top_media", "empty");
			}
			else if(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH.equalsIgnoreCase(action)){
				enableViews.add(play);
				enableViews.add(pause);
				enableViews.add(prev);
				enableViews.add(next);
				sendSourceMCU(McuExternalConstant.MCU_SOURCE_BT);
				String state = intent.getStringExtra(MediaConstants.CURRENT_MEDIA);
				if		(MediaConstants.TO_PLAY.equalsIgnoreCase(state)){
					play.setVisibility(View.GONE);
					pause.setVisibility(View.VISIBLE);
				}else if(MediaConstants.TO_PAUSE.equalsIgnoreCase(state)){
					play.setVisibility(View.VISIBLE);
					pause.setVisibility(View.GONE);
				}else if(MediaConstants.TO_STOP.equalsIgnoreCase(state)){
					play.setVisibility(View.VISIBLE);
					pause.setVisibility(View.GONE);
				}
				setMediaType(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH);
				setControlViewEnable(enableViews,unenableViews);
			}else if(MuteTextView.ACTION_MEDIA_VOLUME_OPEN.equals(action)){//FIX ME
				MuteTextView muteTextView = (MuteTextView) findViewById(R.id.btn_mute_state);
				if(muteTextView != null){		
					//muteTextView.openVolume();
				}
			}else if(MuteTextView.ACTION_MEDIA_VOLUME_MUTE.equals(action)){
				((MuteTextView)findViewById(R.id.btn_mute_state)).toggle();
			}
		}
	};
	
	private BroadcastReceiver mSourceStackReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			L.e("mSourceStackReceiver action : " + action);
			if(Context.ACTION_SOURCE_STACK_NOMAL.equalsIgnoreCase(action)){
				String apkPackageName = intent.getStringExtra(Context.EXTRA_APK_PACKAGENAME);
				String apkActivity = intent.getStringExtra(Context.EXTRA_APK_ACTIVITY);
				mSourceStack.pushNormal(apkPackageName, apkActivity);
				if(YeconConstants.CARLIFE_PACKAGE_NAME.equals(apkPackageName) || YeconConstants.FAWLINK_PACKAGE_NAME.equals(apkPackageName)){
					SystemProperties.set("persist.sys.top_media", "empty");
					sendControlBroadcast(MediaConstants.DO_STOP);
					//sendControlBroadcast(MediaConstants.DO_INACTIVE);
					if (currentSource == McuExternalConstant.MCU_SOURCE_RADIO) {
						sendSourceMCU(McuExternalConstant.MCU_SOURCE_SD);
					}
				}
			}else if(Context.ACTION_SOURCE_STACK_SD.equalsIgnoreCase(action)){
				String apkPackageName = intent.getStringExtra(Context.EXTRA_APK_PACKAGENAME);
				String apkActivity = intent.getStringExtra(Context.EXTRA_APK_ACTIVITY);
				mSourceStack.pushDeviceSD(apkPackageName, apkActivity);
			}else if(Context.ACTION_SOURCE_STACK_USB.equalsIgnoreCase(action)){
				String apkPackageName = intent.getStringExtra(Context.EXTRA_APK_PACKAGENAME);
				String apkActivity = intent.getStringExtra(Context.EXTRA_APK_ACTIVITY);
				mSourceStack.pushDeviceUSB(apkPackageName, apkActivity);
			}else if(Context.ACTION_SOURCE_STACK_AUTOEXIT.equalsIgnoreCase(action)){
				String apkPackageName = intent.getStringExtra(Context.EXTRA_APK_PACKAGENAME);
				String apkActivity = intent.getStringExtra(Context.EXTRA_APK_ACTIVITY);
				mSourceStack.pushAutoExitSource(apkPackageName, apkActivity);
			}else if(Context.ACTION_SOURCE_STACK_DO_OUT_SD.equalsIgnoreCase(action)){
				Map<String, SourceItem> map = mSourceStack.doOutDeviceSD();
				/*FIXME:11102 弹出sd卡、USB时,不需要流转
				SourceItem oldTopSourceItem = null;
				SourceItem newTopSourceItem = null;
				if(map.containsKey("oldTopSourceItem")){
					oldTopSourceItem = map.get("oldTopSourceItem");
				}
				if(map.containsKey("newTopSourceItem")){
					newTopSourceItem = map.get("newTopSourceItem");
				}
				doDeviceOut(oldTopSourceItem,newTopSourceItem);*/
			}else if(Context.ACTION_SOURCE_STACK_DO_OUT_USB.equalsIgnoreCase(action)){
				L.e("sourceItem:ACTION_SOURCE_STACK_DO_OUT_USB");
				Map<String, SourceItem> map = mSourceStack.doOutDeviceUSB();
				/*FIXME:11102 弹出sd卡、USB时,不需要流转
				SourceItem oldTopSourceItem = null;
				SourceItem newTopSourceItem = null;
				if(map.containsKey("oldTopSourceItem")){
					oldTopSourceItem = map.get("oldTopSourceItem");
				}
				if(map.containsKey("newTopSourceItem")){
					newTopSourceItem = map.get("newTopSourceItem");
				}

				doDeviceOut(oldTopSourceItem,newTopSourceItem);
				*/
			}/*FIXME:11103
			else if(Context.ACTION_SOURCE_STACK_DO_OUT_AUTOEXIT.equalsIgnoreCase(action)){
				String apkPackageName = intent.getStringExtra(Context.EXTRA_APK_PACKAGENAME);
				String apkActivity = intent.getStringExtra(Context.EXTRA_APK_ACTIVITY);
				SourceItem sourceItem = mSourceStack.doOutAutoExitSource(apkPackageName);
				if(sourceItem == null){
				}else{
					L.e("SourceItem : "+ apkPackageName + "--->" + sourceItem.apkPackageName);
					openSource(sourceItem.apkPackageName);
				}
			}*/else if(MediaConstants.CURRENT_MEDIA_IS_MUSIC.equalsIgnoreCase(action)){
				String playingDevice = intent.getStringExtra(MediaConstants.EXTRA_PALYING_DEVICE);
				if(!TextUtils.isEmpty(playingDevice)){
					/*if(playingDevice.equalsIgnoreCase(MediaConstants.DEVICE_SD)){
						L.e("hallo sd");
					}else if(playingDevice.equalsIgnoreCase(MediaConstants.DEVICE_USB)){
						L.e("hallo usb");
					}*/
					requestPushStackForDevice(YeconConstants.CARLIFE_PACKAGE_NAME, YeconConstants.MUSIC_PACKAGE_NAME, playingDevice);
					requestPushStackForDevice(YeconConstants.FAWLINK_PACKAGE_NAME, YeconConstants.MUSIC_PACKAGE_NAME, playingDevice);
				}
			}else if(MediaConstants.CURRENT_MEDIA_IS_VIDEO.equalsIgnoreCase(action)){
				String playingDevice = intent.getStringExtra(MediaConstants.EXTRA_PALYING_DEVICE);
				if(!TextUtils.isEmpty(playingDevice)){
					/*if(playingDevice.equalsIgnoreCase(MediaConstants.DEVICE_SD)){
						L.e("hallo sd");
					}else if(playingDevice.equalsIgnoreCase(MediaConstants.DEVICE_USB)){
						L.e("hallo usb");
					}*/
					requestPushStackForDevice(YeconConstants.CARLIFE_PACKAGE_NAME, YeconConstants.VIDEO_PACKAGE_NAME, playingDevice);
					requestPushStackForDevice(YeconConstants.FAWLINK_PACKAGE_NAME, YeconConstants.VIDEO_PACKAGE_NAME, playingDevice);
				}
			}else if(Context.ACTION_LAST_MEDIA_OPEN.equals(action)){
				String name = intent.getStringExtra(Context.EXTRA_APK_PACKAGENAME);
				if(YeconConstants.BLUETOOTH_PACKAGE_NAME.equals("name")){
					SystemProperties.set("persist.sys.top_media", YeconConstants.BLUETOOTH_PACKAGE_NAME);
				}
				else {
					SystemProperties.set("persist.sys.top_media", "empty");
				}
			}
		}
	};
	
	private BroadcastReceiver mOtherReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			L.e("action : " + action + "mediaType :" + mediaType);
			if(MediaConstants.ACTION_SETTINGS_AUTO_FINISH.equals(action)){
				if(MediaConstants.CURRENT_MEDIA_IS_RADIO.equals(mediaType)){
					findViewById(R.id.app_radio).callOnClick();
				}else if(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH.equals(mediaType)){
					findViewById(R.id.app_bt_music).callOnClick();
				}else if(MediaConstants.CURRENT_MEDIA_IS_MUSIC.equals(mediaType)){
					//if(mDeviceStateMode.getStateStorageValue() > 0){
						findViewById(R.id.app_music).callOnClick();
					//}
				}else if(MediaConstants.CURRENT_MEDIA_IS_VIDEO.equals(mediaType)){
					//if(mDeviceStateMode.getStateStorageValue() > 0){
						findViewById(R.id.app_video).callOnClick();
					//}
				}
			}else if(YeconConstants.ACTION_QB_PREPOWEROFF.equalsIgnoreCase(action)){
				isPowerOn = false;
				sleepStatus = 1;
				mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				List<RunningTaskInfo> tasks =  mActivityManager.getRunningTasks(10);
				for(RunningTaskInfo t : tasks){
					String packageName = t.topActivity.getPackageName();
					//String activityName = t.topActivity.getClassName();
					L.e("packageName:" + packageName);
					//Log.d("TEST","activityName:"+activityName);
					if("com.yecon.music".equals(packageName)){
						setMediaType(MediaConstants.CURRENT_MEDIA_IS_MUSIC);
						break;
					}else if("com.yecon.video".equals(packageName)){
						setMediaType(MediaConstants.CURRENT_MEDIA_IS_VIDEO);
						break;
					}else if("com.yecon.fmradio".equals(packageName)){
						setMediaType(MediaConstants.CURRENT_MEDIA_IS_RADIO);
						break;
					}else if("com.autochips.bluetooth".equals(packageName)
					//&&!activityName.contains("TuoXianDialActivity")
					){
						setMediaType(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH);
						break;
					}else if("com.baidu.carlifevehicle".equals(packageName)){
						setMediaType(MediaConstants.CURRENT_MEDIA_IS_CARLIFE);
						break;
					}else if(YeconConstants.FAWLINK_PACKAGE_NAME.equals(packageName)){
						setMediaType(MediaConstants.CURRENT_MEDIA_IS_FAWLINK);
						break;
					}
				}
				if(!TextUtils.isEmpty(mediaType)){
					if(mediaType != MediaConstants.CURRENT_MEDIA_IS_CARLIFE){
						mEditor.putString("powerMediaType_is_none_to_open", mediaType).commit();
					}
					mEditor.putString("powerMediaType", mediaType).commit();
				}
				L.e("ACTION_QB_PREPOWEROFF-mediaType:" + mediaType);
			}else if (Context.ACTION_ACC_OFF.equalsIgnoreCase(action)) {
				L.e(Context.ACTION_ACC_OFF);
				mEditor.putString("powerMediaType", "").commit();
				mEditor.putString("powerMediaType_is_none_to_open","");
				mEditor.putString("ACCMediaType", mediaType).commit();
			}else if (Context.ACTION_ACC_ON.equalsIgnoreCase(action)) {
				L.e(Context.ACTION_ACC_ON);
			}else if (MediaConstants.ACTION_INIT_MCU_SOURCE.equalsIgnoreCase(action)) {
				try {
					mMcuManager.RPC_SetSource(frontSource, rearSource);
				} catch (RemoteException e) {
					Log.e(this.getClass().getName(), e.toString());
				}
			} else if (action.equals("action.set.backlight")) {
				mEditor.putInt("light_level", intent.getIntExtra("brightness",0x9f)).commit();
				setScreenBrightness();
			} else if (action.equals("action.system.keycode.backlightOn")) {
				mIsNight = intent.getBooleanExtra("isNight",false);
				setScreenBrightness();
			}else if("action.system.keycode.backlightOff".equalsIgnoreCase(action)){
			}else if("action.system.keycode.next".equalsIgnoreCase(action)){
				/*((MuteTextView)findViewById(R.id.btn_mute_state)).setMute(false,400);
				sendControlBroadcast(MediaConstants.DO_NEXT);*/
			}else if("action.system.keycode.prev".equalsIgnoreCase(action)){
				/*((MuteTextView)findViewById(R.id.btn_mute_state)).setMute(false,400);
				sendControlBroadcast(MediaConstants.DO_PREV);*/
			}else if("com.wesail.initvol".equalsIgnoreCase(action)){
				((MuteTextView)findViewById(R.id.btn_mute_state)).initDefualtVolume();
				mMcuManager.Init_RPC_Volume();
			}
			else{
				L.w(action);
			}
		}
	};

	private void setScreenBrightness(){
		int brightness = mPref.getInt("light_level", 0x9f);
		if(mIsNight){
			switch(brightness){
				case 0xdf:
					brightness = 0x9f;
					//break;
				case 0x9f:
					brightness = 0x5f;
					//break;
				case 0x5f:
					brightness = 0x10;
					break;
			}
		}
		Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,1);
		Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,brightness);
		Log.d("TEST","CarSettingsBootService SCREEN_BRIGHTNESS "+brightness);
		getContentResolver().notifyChange(android.provider.Settings.System.getUriFor("screen_brightness"), null);
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,0);       
 //        lp.screenBrightness = brightness/255.0f;
         lp.screenBrightness = brightness;
         this.getWindow().setAttributes(lp);
	}
	
	McuListener mcuListener = new McuListener() {
		
		@Override
		public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {
			L.e("mcuListener infoType : " + infoType);
			if(infoType == McuExternalConstant.MCU_DEVICE_STATUS_INFO_TYPE){
				McuDeviceStatusInfo mcuDeviceStatusInfo = mcuBaseInfo.getDeviceStatusInfo();
				
				if(backStauts != mcuDeviceStatusInfo.getBackcarStatus()){
					backStauts = mcuDeviceStatusInfo.getBackcarStatus();
					L.e("backStauts" + backStauts);
					if(backStauts == 0){
						//L.e(Context.ACTION_BACKCAR_OFF);
						sendBroadcast(new Intent(Context.ACTION_BACKCAR_OFF));
					}else{
						//L.e(Context.ACTION_BACKCAR_ON);
						sendBroadcast(new Intent(Context.ACTION_BACKCAR_ON));
					}
				}
				L.e("sleepStatus: " + mcuDeviceStatusInfo.getSleepStatus() + " backStauts : " + backStauts + "  mediaType:" + mediaType);
				
				if(sleepStatus != mcuDeviceStatusInfo.getSleepStatus() && mcuDeviceStatusInfo.getSleepStatus() == 0 && backStauts == 0){
					sleepStatus = 0;
					((MuteTextView)findViewById(R.id.btn_mute_state)).initDefualtVolume();
					openMediaForPowerOn();
					isPowerOn = true;
					Intent intent = new Intent("action.hzh.media.power.on");
					if(mediaType == MediaConstants.CURRENT_MEDIA_IS_CARLIFE){
						sendOrderedBroadcast(intent, null);
					}
					else{
						sendOrderedBroadcast(intent, null);
					}
					L.e("openMediaForPowerOn");
				}	
			}
		}
	};
	private final static String[] stopAppPackageNames = {
		"com.autochips.bluetooth",
		"com.yecon.video",
		"com.yecon.music",
		"com.wesail.tdr",
		"com.yecon.carsetting",
		"com.yecon.fmradio",
		"com.yecon.sound.setting",
		"com.yecon.imagebrowser",
	};
	
	private class MediaSource{
		public String prevName = "";
		public String currentName = "";
		public boolean prevIsPlay = false;
		public boolean currentIsPlay = false;
		public void clean(){
			prevName = "";
			currentName = "";
			prevIsPlay = false;
			currentIsPlay = false;
		}
		public void setData(String source,View pause){
			if(!currentName.equals(source)){
				prevName = currentName;
				currentName = source;
				prevIsPlay = currentIsPlay;
			}
			if(pause == null){
				currentIsPlay = false;
			}
			else if(pause.isEnabled() && pause.getVisibility() == View.VISIBLE){
				currentIsPlay = true;
			}else{
				currentIsPlay = false;
			}
			L.e("prevName: " + prevName + "   prevIsPlay:" + prevIsPlay);
			L.e("currentName: " + currentName + "   currentIsPlay:" + currentIsPlay);
		}
	}
	
	private void killCarlife(){
		try {
			mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasks =  mActivityManager.getRunningTasks(10);
			for(RunningTaskInfo t : tasks){
				String packageName = t.topActivity.getPackageName();
				if("com.baidu.carlifevehicle".equals(packageName)){
					ActivityManager mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE); 
					mAm.forceStopPackage(packageName);
					return;
				}
			}
		} catch (Exception e) {
			L.e(e.toString());
		}
	}
	
	private void setMediaType(String mt){
		L.d("Media type old : " + mediaType + "   new: " + mt);
		mediaType = mt;
	}
	/*private class TestTask extends AsyncTask<String, Integer, Object>{

		@Override
		protected Object doInBackground(String... arg0) {
			L.e("TestTask doInBackground");
			ActivityManager mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE); 
			for(String packName : stopAppPackageNames){
				mAm.forceStopPackage(packName);
			}
			try {
				Environment.ClearOSData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//
			{
				File directory = new File("/data/data/"
		                + "com.android.launcher" + "/databases");
				if (directory != null && directory.exists() && directory.isDirectory()) {
					for (File item : directory.listFiles()) {
		            	L.e(item.getPath() + "db delete");
		            	item.delete();
		            }
		        }else{
		        	L.e("com.android.launcher" + " delete db error");
		        }
			}
			SystemProperties.set("persist.sys.current_volume", "14");
			SystemProperties.set("persist.sys.volume_balance_lr", "0");
			SystemProperties.set("persist.sys.volume_balance_fr", "0");
			//mAm.forceStopPackage("com.android.launcher");
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
		
	}*/
}
