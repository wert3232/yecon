package com.android.launcher2.tuoxian;

import java.util.zip.Inflater;

import com.android.launcher.R;
import com.android.launcher2.util.CmnUtil;
import com.android.launcher2.util.L;

import android.app.Service;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.tuoxianui.view.DeviceStateMode;

public class NoTouchViewService extends Service {
	private WindowManager mWindowManager = null;
	private View mNoTouchWindow = null;
	private WindowManager.LayoutParams mNoTouchParams = null;
	private View mLogoWindow = null;
	private WindowManager.LayoutParams mLogoParams = null;
	private McuManager mMcuManager;
	private DeviceStateMode mDeviceStateMode; 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		L.e("NoTouchViewService onCreate");
		mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
		mWindowManager = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		mNoTouchParams = new WindowManager.LayoutParams();
		mLogoParams = new WindowManager.LayoutParams();
		IntentFilter filter = new IntentFilter();
		filter.addAction("action.system.keycode.backlightOn");
		filter.addAction("action.system.keycode.backlightOff");
		filter.addAction("action.hzh.media.power.on");
		filter.addAction(YeconConstants.ACTION_QB_PREPOWEROFF);
		registerReceiver(receiver, filter);
		
		mDeviceStateMode = new DeviceStateMode(this);
		mDeviceStateMode.register();
		mDeviceStateMode.setStorageCallBack(mStorageCallBack);
		mMcuManager.Init_RPC_Volume();
		sendBroadcast(new Intent("com.wesail.initvol"));
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		mDeviceStateMode.unRegister();
		super.onDestroy();
	}
	private DeviceStateMode.StorageCallBack mStorageCallBack = new DeviceStateMode.StorageCallBack(){
		public void onStorageState(String path,String oldState,String newState){
			L.e("path :" + path + " newState" + newState);
			if(path.matches("(.*)ext_sdcard(.*)")){
				if("unmounted".equalsIgnoreCase(newState)){
					SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE, "" + 2);
					SystemProperties.set("persist.sys.music_init_state", "empty");
					SystemProperties.set("persist.sys.video_init_state", "empty");
					CmnUtil.showTuoXianToast(getApplicationContext(),R.string.sd_unmount);
					sendBroadcast(new Intent(Context.ACTION_SOURCE_STACK_DO_OUT_SD));
					try {
						Environment.ClearSDMediaData();
					} catch (Exception e) {
						L.e(e.toString());
					}
				}else if("mounted".equalsIgnoreCase(newState)){
					SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE, "" + 3);
					CmnUtil.showTuoXianToast(getApplicationContext(),R.string.sd_mount);
					/*FIXME:11101
					sendBroadcast(new Intent("com.action.storage.sd_mount"));*/
				}
			}
			else if(path.matches("(.*)udisk(.*)")){
				if("bad_removal".equalsIgnoreCase(newState)){
					SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE, "" + 3);
					SystemProperties.set("persist.sys.music_init_state", "empty");
					SystemProperties.set("persist.sys.video_init_state", "empty");
					CmnUtil.showTuoXianToast(getApplicationContext(),R.string.usb_unmount);
					sendBroadcast(new Intent(Context.ACTION_SOURCE_STACK_DO_OUT_USB));
					try {
						Environment.ClearUSBMediaData();
					} catch (Exception e) {
						L.e(e.toString());
					}
				}else if("mounted".equalsIgnoreCase(newState)){
					SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE, "" + 2);
					CmnUtil.showTuoXianToast(getApplicationContext(),R.string.usb_mount);
					/*FIXME:11101
					sendBroadcast(new Intent("com.action.storage.usb_mount"));*/
				}
			}
		}
	};
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if("action.system.keycode.backlightOn".equalsIgnoreCase(intent.getAction())){
				removeView();
			}else if("action.system.keycode.backlightOff".equalsIgnoreCase(intent.getAction())){
				showView();
			}else if("action.hzh.media.power.on".equalsIgnoreCase(intent.getAction())){
				logoHandler.removeCallbacks(removeLogoRunnable);
				logoHandler.postDelayed(removeLogoRunnable, 1000);
				new Handler().postDelayed(new Runnable() {	
					@Override
					public void run() {
						AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE) ;
						int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
						try {
							if(volume != 0){
								mMcuManager.RPC_SetVolumeMute(true);
							}
						} catch (Exception e) {
							L.e(e.toString());
						}
					}
				}, 50);
			}else if(YeconConstants.ACTION_QB_PREPOWEROFF.equalsIgnoreCase(intent.getAction())){
				showLogo();
			}
		}
	};
	private Handler logoHandler = new Handler();
	private Runnable removeLogoRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE) ;
				int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				if(volume != 0){
					mMcuManager.RPC_SetVolumeMute(false);
				}
			} catch (Exception e) {
				L.e(e.toString());
			}
			removeLogoView();
			removeView();
		}
	};
	public void removeLogoView(){
		if (mLogoWindow != null) {
			mWindowManager.removeView(mLogoWindow);
			mLogoWindow = null;
		} 
	}
	public void showLogo(){
		removeLogoView();
		LayoutInflater inflater = LayoutInflater.from(this);
		mLogoWindow = inflater.inflate(R.layout.layout_logo, null);
		setupLogoLayoutParams();
		mWindowManager.addView(mLogoWindow, mLogoParams);
	}
	public void removeView(){
		if (mNoTouchWindow != null) {
			mWindowManager.removeView(mNoTouchWindow);
			mNoTouchWindow = null;
		} 
	}
	public void showView(){
		removeView();
		mNoTouchWindow = getLayout();
		setupLayoutParams();
		mWindowManager.addView(mNoTouchWindow, mNoTouchParams);
	}
	private View getLayout(){
		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.WHITE);  
        layout.setOrientation(LinearLayout.VERTICAL);  
        layout.setGravity(Gravity.CENTER); 
		return layout;
	}
	private void setupLayoutParams() {
		/*----*/
		mNoTouchParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		mNoTouchParams.format = PixelFormat.RGBA_8888;
		mNoTouchParams.alpha = 0.0f;
		mNoTouchParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
								| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
								| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		mNoTouchParams.gravity = Gravity.CENTER;
		/*mNoTouchParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mNoTouchParams.height = WindowManager.LayoutParams.WRAP_CONTENT;*/
		mNoTouchParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		mNoTouchParams.height = WindowManager.LayoutParams.MATCH_PARENT;
	}
	
	private void setupLogoLayoutParams() {
		/*----*/
		mLogoParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		mLogoParams.format = PixelFormat.RGBA_8888;
		mLogoParams.alpha = 1f;
		mLogoParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
								| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
								| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		mLogoParams.gravity = Gravity.CENTER;
		mLogoParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		mLogoParams.height = WindowManager.LayoutParams.MATCH_PARENT;
	}
}
