package com.tuoxianui.view;

import com.autochips.storage.EnvironmentATC;
import com.tuoxianui.db.ContentData;
import com.tuoxianui.view.util.TopLog;
import com.yecon.common.YeconEnv;
import com.yecon.mediaprovider.YeconMediaStore;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemProperties;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;

public class DeviceStateMode {
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	private Context mContext;
	private boolean hasSDCard = false;
	private boolean hasUSB = false;
	public static final String SHARED_PREFERENCES_NAME = "com.tuoxianui.device.state";
	public static final String ACITON_STATE_STORAGE = "action.state.storage";
	public static final String TAG_STATE_STORAGE = "storageTag";
	public static final int VALUE_STATE_STORAGE_NULL = -1;
	public static final int VALUE_STATE_STORAGE_EMPTY = 0;
	public static final int VALUE_STATE_STORAGE_SD = 1;
	public static final int VALUE_STATE_STORAGE_USB = 2;
	public static final int VALUE_STATE_STORAGE_ALL = VALUE_STATE_STORAGE_SD + VALUE_STATE_STORAGE_USB;
	public static final String USB_STATE = "USB_STATE";
	public static final String BLUETOOTH_STATE = "BLUETOOTH_STATE";
	public Handler handler;
	private StorageManager mStorageManager;
	private AudioManager mAudioManager;
	private static final int MIN_VOLUME = 0;
	private int prevVolume = MIN_VOLUME;
	private int stateStorageValue = 0;
	private StorageCallBack storageCallBack;
	private int mStorageTag = -1;
	private  static boolean isFirstCreate = true;
	private String[] storages = new String[] {
			YeconMediaStore.EXTERNAL_PATH,
			YeconMediaStore.EXT_SDCARD1_PATH,
			YeconMediaStore.EXT_SDCARD2_PATH,
			YeconMediaStore.UDISK1_PATH,
			YeconMediaStore.UDISK2_PATH,
			YeconMediaStore.UDISK3_PATH,
			YeconMediaStore.UDISK4_PATH,
			YeconMediaStore.UDISK5_PATH
	};
	public DeviceStateMode(Context context) {
		mContext = context;
		mPref = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_WORLD_WRITEABLE + Context.MODE_MULTI_PROCESS);
		mEditor = mPref.edit();
		mStorageManager = (StorageManager) mContext.getSystemService(mContext.STORAGE_SERVICE);
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE) ;
		setAsync();
	}
	private void check(){
		if(handler != null){
			handler.post(new Runnable() {			
				@Override
				public void run() {
					checkStorge();
					checkBluetooth();
				}
			});
		}
	}
	
	private void setAsync(){
		HandlerThread thr = new HandlerThread("device state thread");
		thr.start();
		handler = new Handler(thr.getLooper());
	}
	
	public void register(){
		mStorageManager.registerListener(eventListener);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
		intentFilter.addAction("action.system.supply.b_plus");
		intentFilter.addAction("action.system.volume.is_zero");
		mContext.registerReceiver(volumeReceiver, intentFilter);
		mStorageTag = SystemProperties.getInt("persist.sys.storageDeviceTag", 0);
		check();
		//Log.d("TEST","isFirstCreate "+isFirstCreate);
	}
	public void unRegister(){
		mStorageManager.unregisterListener(eventListener);
		mContext.unregisterReceiver(volumeReceiver);
	}
	
	private void checkStorge(){
		int stateStorage = 0;
		hasSDCard = false;
		hasUSB = false;
		EnvironmentATC env = new EnvironmentATC(mContext);
		for (String storage : storages) {
			boolean checkResult = YeconEnv.checkStorageExist(env, storage);
			LogE(storage + " : " + checkResult);
			if(checkResult){
				if(storage.matches("(.*)ext_sdcard(.*)")){
					hasSDCard = checkResult;
					//stateStorage = stateStorage + VALUE_STATE_STORAGE_SD;
				}
				if(storage.matches("(.*)udisk(.*)")){
					hasUSB = checkResult;
					//stateStorage = stateStorage + VALUE_STATE_STORAGE_USB;
				}
			}
		}
		if(hasSDCard)
			stateStorage = stateStorage + VALUE_STATE_STORAGE_SD;
		if(hasUSB)
			stateStorage = stateStorage + VALUE_STATE_STORAGE_USB;
		stateStorageValue = stateStorage;
		LogE("" + hasUSB +" " + hasSDCard + "  ACITON_STATE_STORAGE:" + stateStorage);
		
		SystemProperties.set("persist.sys.storageDeviceTag", "" + stateStorageValue);
		if(mStorageTag != -1 && mStorageTag != stateStorageValue){
			
			switch (mStorageTag) {
				case VALUE_STATE_STORAGE_EMPTY:{
					if(stateStorageValue == VALUE_STATE_STORAGE_SD || stateStorageValue == VALUE_STATE_STORAGE_ALL){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 3);
					}else if(stateStorageValue == VALUE_STATE_STORAGE_USB){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 2);
					}
				}
				break;
				case VALUE_STATE_STORAGE_SD:{
					if(stateStorageValue == VALUE_STATE_STORAGE_EMPTY){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 0);
					}else if(stateStorageValue == VALUE_STATE_STORAGE_USB){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 2);
					}else if(stateStorageValue == VALUE_STATE_STORAGE_ALL){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 2);
					}
				}
				break;
				case VALUE_STATE_STORAGE_USB:{
					if(stateStorageValue == VALUE_STATE_STORAGE_EMPTY){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 0);
					}else if(stateStorageValue == VALUE_STATE_STORAGE_SD){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 3);
					}else if(stateStorageValue == VALUE_STATE_STORAGE_ALL){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 3);
					}
				}
				break;
				case VALUE_STATE_STORAGE_ALL:{
					if(stateStorageValue == VALUE_STATE_STORAGE_EMPTY){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 0);
					}else if(stateStorageValue == VALUE_STATE_STORAGE_SD){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 3);
					}else if(stateStorageValue == VALUE_STATE_STORAGE_USB){
						SystemProperties.set(Context.PERSIST_LAST_STORAGE_DEVICE,"" + 2);
					}
				}
				break;
			}		
		}
		mStorageTag = -1;
		
		
		Intent intent = new Intent(ACITON_STATE_STORAGE);
		intent.putExtra(TAG_STATE_STORAGE, stateStorage);
		mContext.sendBroadcast(intent);
		setUsbState(hasSDCard,hasUSB);
	}
	public int getStateStorageValue(){
		return stateStorageValue;
	}
	public void setStorageCallBack(StorageCallBack storageCallBack){
		this.storageCallBack = storageCallBack;
	}
	private void setUsbState(boolean hasSDCard,boolean hasUSB){
		Intent intent = new Intent();
		intent.setAction(SHARED_PREFERENCES_NAME);
		
		if(hasUSB  || hasSDCard){
			mEditor.putInt(USB_STATE, 2).commit();
			intent.putExtra(USB_STATE, mPref.getInt(USB_STATE, 2));

		}else{
			mEditor.putInt(USB_STATE, 0).commit();
			intent.putExtra(USB_STATE, mPref.getInt(USB_STATE, 0));
		}
		
		mContext.sendBroadcast(intent);
	}
	
	private void checkBluetooth(){
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		int connectState = adapter.getConnectionState();
		switch (connectState) {
			case BluetoothProfile.STATE_DISCONNECTING:
			case BluetoothProfile.STATE_DISCONNECTED:
				//do it
				mEditor.putInt(BLUETOOTH_STATE, 0).commit();
				break;
			case BluetoothProfile.STATE_CONNECTED:
				mEditor.putInt(BLUETOOTH_STATE, 2).commit();
				break;	
			default:
				break;
			}
	}
	
	StorageEventListener eventListener = new StorageEventListener() {
		@Override
		public void onStorageStateChanged(final String path,final String oldState,final String newState) {
			LogE("TopBar"," path:" + path + "  oldState : " + oldState + "  newState: " + newState);
			if(storageCallBack != null){
				storageCallBack.onStorageState(path, oldState, newState);
			}
			handler.post(new Runnable() {			
				@Override
				public void run() {
					checkStorge();
				}
			});
		}
		@Override
		public void onUsbMassStorageConnectionChanged(boolean connected) {
			LogE("TopBar"," onUsbMassStorageConnectionChanged:" + connected);
		}
	};
	
	private void LogE(String tag,String msg){
		boolean isOpen= false;
		if(isOpen){
			TopLog.e(tag, msg);
		}
	}
	private void LogE(String msg){
			LogE("TopBar", msg);
	}
	int hasVolume = 0;
	private boolean isTurnDownVolumeZero = false;
	BroadcastReceiver volumeReceiver = new BroadcastReceiver() {	
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
				int type = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE,-1);
				int newValue = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, -1);
				int oldValue = intent.getIntExtra(AudioManager.EXTRA_PREV_VOLUME_STREAM_VALUE, -1);
				if(type == AudioManager.STREAM_MUSIC){
					Log.e("TestVolume",type + "  new value:" + newValue + "   old value:" + oldValue + " current: " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + " " + isTurnDownVolumeZero);
					if(newValue == 0){
						/*if(oldValue > MIN_VOLUME){
							prevVolume = oldValue;
						}
						else if(oldValue <= MIN_VOLUME){
							prevVolume = MIN_VOLUME;
						}*/
						prevVolume = oldValue;
						if(isTurnDownVolumeZero){
							isTurnDownVolumeZero = false;
							prevVolume = 0;
							hasVolume = 0;
						}
					}else if(newValue == oldValue && oldValue == 1){
						newValue = 0;
						oldValue = 0;
						prevVolume = newValue;
						hasVolume = newValue;
					}
					else if(newValue < oldValue){
						prevVolume = newValue;
					}else if(newValue > oldValue){
						prevVolume = newValue;
					}else if(newValue == oldValue && newValue > 0){
						prevVolume = newValue;
						hasVolume = newValue;
					}
					Log.e("TestVolume","pre_volume :" + prevVolume + 
							"have_volume :" + hasVolume +
							"current_volume :" + newValue);
					mAudioManager.setPreVolume(prevVolume);
					//
					ContentValues contentValues = new ContentValues();
					contentValues.put(ContentData.DeviceStateTableData.PRE_VOLUME, prevVolume);
					contentValues.put(ContentData.DeviceStateTableData.CURRENT_VOLUME, newValue);
					if(prevVolume > 0){
						hasVolume = prevVolume;
						contentValues.put(ContentData.DeviceStateTableData.HAVE_VOLUME, prevVolume);
					}else{
						contentValues.put(ContentData.DeviceStateTableData.PRE_VOLUME, hasVolume);
						contentValues.put(ContentData.DeviceStateTableData.HAVE_VOLUME, hasVolume);
					}
					SystemProperties.set("persist.sys.hasVolume", hasVolume + "");
					/*Log.e("TestVolume","2PRE_VOLUME :" + contentValues.getAsInteger(ContentData.DeviceStateTableData.PRE_VOLUME) + 
							"HAVE_VOLUME :" + contentValues.getAsInteger(ContentData.DeviceStateTableData.HAVE_VOLUME) +
							"CURRENT_VOLUME :" + contentValues.getAsInteger(ContentData.DeviceStateTableData.CURRENT_VOLUME));*/
					mContext.getContentResolver().insert(ContentData.DeviceStateTableData.CONTENT_URI_PRE_VOLUME, contentValues);
				}
			}else if("action.system.supply.b_plus".equals(intent.getAction())){
				Log.e("TestVolume","ACTION : action.system.supply.b_plus");
				Log.d("TEST","isFirstCreate "+isFirstCreate);
				if(SystemProperties.get("persist.sys.first.b_plus","1").equals("1")){
					try {
					if(isFirstCreate){
					
					ContentValues contentValues = new ContentValues();
					mAudioManager.setPreVolume(Context.DEFUALT_VOLUME);
					contentValues.put(ContentData.DeviceStateTableData.CURRENT_VOLUME,  Context.DEFUALT_VOLUME);
					contentValues.put(ContentData.DeviceStateTableData.PRE_VOLUME, Context.DEFUALT_VOLUME);
					contentValues.put(ContentData.DeviceStateTableData.HAVE_VOLUME, Context.DEFUALT_VOLUME);
				
					mContext.getContentResolver().insert(ContentData.DeviceStateTableData.CONTENT_URI_PRE_VOLUME, contentValues);
						mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Context.DEFUALT_VOLUME, 0);
						isFirstCreate = false;
						SystemProperties.set("persist.sys.first.b_plus","2");
					Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,0x9f);
					}
					} catch (Exception e) {
						Log.e("TestVolume","error : action.system.supply.b_plus");
					}
				}
			}else if("action.system.volume.is_zero".equals(intent.getAction())){
				Log.e("TestVolume","ACTION : action.system.volume.is_zero");
				isTurnDownVolumeZero = true;
			}
		}
	};
	public interface StorageCallBack{
		public void onStorageState(String path,String oldState,String newState);
	}
	
}
