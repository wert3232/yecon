package com.yecon.carsetting.mcu;

import static android.mcu.McuExternalConstant.*;
import static android.constant.YeconConstants.*;
import static com.yecon.carsetting.unitl.ArraySetting.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.AlarmManager;
import android.app.Service;
import android.backlight.BacklightControl;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.mcu.McuBaseInfo;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.mcu.McuSystemParamInfo;
import android.mcu.McuVersionInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;

import com.autochips.whitelist.WhiteList;
import com.yecon.carsetting.DatetimeSetActivity;
import com.yecon.carsetting.R;
import com.yecon.carsetting.DatetimeSetActivity.onGpsListener;
import com.yecon.carsetting.unitl.ArraySetting;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.unitl.McuMethodManager;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.metazone.YeconMetazone;

public class CarSettingsBootService extends Service implements onGpsListener {
	private static final int MSG_GET_SYSTEM_VIDEO_PARAMS = 0;
	private static final int MSG_SET_MCU_PARAMS = 1;
	private static final int MSG_ID_CHECK_BACKCAR_STATUS = 3;
	
	private static final int CHECK_BACKCAR_STATUS_TIMEOUT = 1000;

	private static final String START_CARSETTINGS_SERVICE_ACTION = "com.yecon.carsetting.mcu";
	private static final String ACTIVITY_CHANGE = "android.activity.action.STATE_CHANGED";
	private static final String ACTIVITY_FG = "foreground";
	private static int mSaveBacklightLevel = BKL_HIGH;

	private Context mContext;
	private McuManager mMcuManager;
	private AudioManager mAudioManager;

	private int mSystemParamIndex;
	private int mBrightness = XmlParse.rgb_video[0][0];
	private int mContrast = XmlParse.rgb_video[0][1];
	private int mHue = XmlParse.rgb_video[0][2];
	private int mSaturation = XmlParse.rgb_video[0][3];
	private int mBacklight = XmlParse.backlight[0];
	private int mSaveLightState = 0;

	private boolean mNaviRemixEnable = false;
	private boolean mNaviListenEnable = false;
	private boolean mNaviMuteMusic = false;
	private boolean mUnifiedVolumeAdjust = false;
	
	private boolean mIsNight = false;
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;

	private static final String SDCARD1_UPGRADE_CHECK_FILE = "mnt/ext_sdcard1/yc8317.img";
	private static final String SDCARD2_UPGRADE_CHECK_FILE = "mnt/ext_sdcard2/yc8317.img";
	
	private static final String USB1_UPGRADE_CHECK_FILE = "mnt/udisk1/yc8317.img";
	private static final String USB2_UPGRADE_CHECK_FILE = "mnt/udisk2/yc8317.img";
	private static final String USB3_UPGRADE_CHECK_FILE = "mnt/udisk3/yc8317.img";
	private static final String USB4_UPGRADE_CHECK_FILE = "mnt/udisk4/yc8317.img";
	private static final String USB5_UPGRADE_CHECK_FILE = "mnt/udisk5/yc8317.img";

	// 事件监听
	public static ArrayList<onDeviceStatusListener> ehList = new ArrayList<onDeviceStatusListener>();

	// gps
	Location mLocation = null;
	LocationManager mLocationManager = null;
	LocationListener mLocationListener = null;

	class DeviceStatusListener implements onDeviceStatusListener {

		@Override
		public void onDeviceStatus(Map<String, Integer> values) {
			int headLightStatus = values.get("headLightStatus");
			if (headLightStatus != mSaveLightState) {
				mSaveLightState = headLightStatus;
				setHeadLightModel(headLightStatus);
			}
			setMediaBanOnVideo(values.get("brakeStatus"));
		}

	}

	public interface onDeviceStatusListener {
		public void onDeviceStatus(Map<String, Integer> values);
	}

	private final BroadcastReceiver mMountReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			L.e("mMountReceiver - action: " + action);
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				L.e("ACTION_MEDIA_MOUNTED - Detection USB upgrade");
				File file1 = new File(USB1_UPGRADE_CHECK_FILE);
				File file2 = new File(USB2_UPGRADE_CHECK_FILE);
				File file3 = new File(USB3_UPGRADE_CHECK_FILE);
				File file4 = new File(USB4_UPGRADE_CHECK_FILE);
				File file5 = new File(USB5_UPGRADE_CHECK_FILE);
				File file6 = new File(SDCARD1_UPGRADE_CHECK_FILE);
				File file7 = new File(SDCARD2_UPGRADE_CHECK_FILE);
				if (file1.exists() || file2.exists() || file3.exists() || file4.exists()
				        || file5.exists() || file6.exists() || file7.exists()) {
					try {
						mMcuManager.RPC_UsbUpgrade(1);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
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
		Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,brightness);
		Log.d("TEST","CarSettingsBootService SCREEN_BRIGHTNESS "+brightness);
	}

	private final BroadcastReceiver mCarSettingsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			L.e("McuReceiver - action: " + action);

			if (action.equals(START_CARSETTINGS_SERVICE_ACTION)) {
				startMcuServer();
			} else if (action.equals("action.set.backlight")) {
				mEditor.putInt("light_level", intent.getIntExtra("brightness",0x9f)).commit();
				setScreenBrightness();
			} else if (action.equals("action.system.keycode.backlightOn")) {
				mIsNight = intent.getBooleanExtra("isNight",false);
				setScreenBrightness();
			} else if (action.equals(MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS)) {
				int headlightsStatus = intent.getIntExtra(INTENT_EXTRA_HEADLIGHTS_STATUS, 0);
				int brakeStatus = intent.getIntExtra(INTENT_EXTRA_BRAKE_STATUS, 0);
				int backcarStatus = intent.getIntExtra(INTENT_EXTRA_BACKCAR_STATUS, 0);

				Map<String, Integer> item = new HashMap<String, Integer>();
				item.put("headLightStatus", headlightsStatus);
				item.put("brakeStatus", brakeStatus);
				L.e("headlightsStatus: " + headlightsStatus + " - brakeStatus: " + brakeStatus);

				SystemProperties.set("persist.sys.mcu_parking", (brakeStatus == 0 ? "true"
						: "false"));

				new DeviceStatusListener().onDeviceStatus(item);
			} else if (action.equals(Tag.ACTION_RADIO_UPDATE)) {
				int radio_index = intent.getIntExtra("radio_index", 0);
				McuMethodManager.getInstance(mContext).setMcuParam_RadioCountry((byte) radio_index);
			} else if (action.equals(ACTIVITY_CHANGE)) {
			    boolean startQBPowerOff = SystemProperties.getBoolean(PROPERTY_QB_POWEROFF, false);
			    boolean startQBPowerOn = SystemProperties.getBoolean(PROPERTY_QB_POWERON, false);
			    if (startQBPowerOff || startQBPowerOn) {
			        return;
			    }
			    
				Bundle bundle = intent.getExtras();
				String szState = bundle.getString("state");
				// String szClass = bundle.getString("class");
				String szPkg = bundle.getString("package");
				// int pid = bundle.getInt("pid");

				boolean isNaviApp = false;
				boolean naviActivityIsForeground = false;
				if (szState.equals(ACTIVITY_FG)) {
					if (WhiteList.getPackageType(szPkg) == WhiteList.APP_NAVIGATOR) {
						isNaviApp = true;
						naviActivityIsForeground = true;
					} else {
						isNaviApp = false;
						naviActivityIsForeground = false;
					}
				} else {
					if (WhiteList.getPackageType(szPkg) == WhiteList.APP_NAVIGATOR) {
						isNaviApp = true;
						naviActivityIsForeground = false;
					} else {
						isNaviApp = false;
						naviActivityIsForeground = false;
					}
				}

//				if (isNaviApp && naviActivityIsForeground) {
//                    SystemProperties.set(PROPERTY_KEY_RESTORE_NAVI, "true");
//                } else {
//                    SystemProperties.set(PROPERTY_KEY_RESTORE_NAVI, "false");
//                }
//
//				boolean restoreNavi = SystemProperties.getBoolean(PROPERTY_KEY_RESTORE_NAVI, false);

				SystemProperties.set(PROPERTY_KEY_IS_NAVI_APP, isNaviApp ? "true" : "false");
				SystemProperties.set(PROPERTY_KEY_NAVI_IS_FORE, naviActivityIsForeground ? "true"
						: "false");

				mNaviRemixEnable = SystemProperties.getBoolean(Tag.PERSYS_NAVI_REMIX, false);
				mNaviListenEnable = SystemProperties.getBoolean(Tag.PERSYS_NAVI_LISTEN, false);

				mUnifiedVolumeAdjust = SystemProperties.getBoolean(
						PROPERTY_KEY_FUNC_UNIFIED_VOLUME_ADJUST, false);

				int streamType = AudioManager.STREAM_MUSIC;
				boolean streamMusicMuted = mAudioManager.isStreamMute(streamType);
				int currMusicVolume = mAudioManager.getStreamVolume(streamType);
				int savedMusicVolume = SystemProperties
						.getInt(PROPERTY_KEY_STREAM_MUSIC_VOLUME, 11);

				StringBuffer log = new StringBuffer();
				log.append("state: ");
				log.append(szState);
				log.append(" - szPkg: ");
				log.append(szPkg);
//				log.append(" - restoreNavi: ");
//				log.append(restoreNavi);
				log.append(" - startQBPowerOff: ");
				log.append(startQBPowerOff);
				log.append(" - isNaviApp: ");
				log.append(isNaviApp);
				log.append(" - naviActivityIsForeground: ");
				log.append(naviActivityIsForeground);
				log.append(" - mNaviRemixEnable: ");
				log.append(mNaviRemixEnable);
				log.append(" - mNaviListenEnable: ");
				log.append(mNaviListenEnable);
				log.append(" - streamMusicMuted: ");
				log.append(streamMusicMuted);
				log.append(" - mNaviMuteMusic: ");
				log.append(mNaviMuteMusic);
				log.append(" - mUnifiedVolumeAdjust: ");
				log.append(mUnifiedVolumeAdjust);
				log.append(" - currMusicVolume: ");
				log.append(currMusicVolume);
				log.append(" - savedMusicVolume: ");
				log.append(savedMusicVolume);
				L.e(log.toString());

				if (!mNaviRemixEnable && mNaviListenEnable) {
					if (isNaviApp && naviActivityIsForeground) {
						// if (!mNaviMuteMusic && !streamMusicMuted) {
						mNaviMuteMusic = true;

						// SystemProperties.set(PROPERTY_KEY_STREAM_MUSIC_VOLUME,
						// currMusicVolume + "");

						mAudioManager.setStreamVolume(streamType, 0, 0);

						mAudioManager.setStreamMute(streamType, true);
						// }
					} else {
						// if (mNaviMuteMusic && streamMusicMuted &&
						// currMusicVolume == 0) {
						mNaviMuteMusic = false;

						mAudioManager.setStreamVolume(streamType, savedMusicVolume, 0);

						mAudioManager.setStreamMute(streamType, false);
						// }
					}
				}
			}
		}
	};

	private McuListener mMcuListener = new McuListener() {
		public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {
			if (infoType == MCU_SYSTEM_PARAM_INFO_TYPE) {
				McuSystemParamInfo info = mcuBaseInfo.getSystemParamInfo();
				if (info == null) {
					return;
				}

				if (mSystemParamIndex == MCU_SYSTEM_PARAM_VIDEOSTATE_INDEX) {
					int[] videoStateSetData = info.getVideoStateSetData();
					int len = videoStateSetData.length;

					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < len; i++) {
						String str = String.format("%d ", videoStateSetData[i]);
						sb.append(str);
					}
					L.e("mMcuListener - len: " + len + " - " + sb.toString());

					mBrightness = videoStateSetData[1];
					mContrast = videoStateSetData[2];
					mHue = videoStateSetData[3];
					mSaturation = videoStateSetData[4];
					mBacklight = videoStateSetData[5];

					SystemProperties.set(Tag.PERSYS_RGB_VIDEO[0][0], mBrightness + "");
					SystemProperties.set(Tag.PERSYS_RGB_VIDEO[0][1], mContrast + "");
					SystemProperties.set(Tag.PERSYS_RGB_VIDEO[0][2], mHue + "");
					SystemProperties.set(Tag.PERSYS_RGB_VIDEO[0][3], mSaturation + "");

					SystemProperties.set(Tag.PERSYS_BKLIGHT[0], mBacklight + "");
					SystemProperties.set(Tag.PERSYS_BACKCAR_MIRROR, videoStateSetData[6] + "");

					Settings.System.putInt(mContext.getContentResolver(),
							Settings.System.SCREEN_BRIGHTNESS, mBacklight);
				}
			} else if (infoType == MCU_VERSION_INFO_TYPE) {
				McuVersionInfo info = mcuBaseInfo.getVersionInfo();
				if (info == null) {
					return;
				}
				int[] verData = info.getVersionData();
				for (int i = 0; i < verData.length; i++) {
					tzutil.mcuVersion.append((char) verData[i]);
				}
			} else if (infoType == MCU_ID_INFO_TYPE) {
				McuVersionInfo info = mcuBaseInfo.getVersionInfo();
				if (info == null) {
					return;
				}
				int[] verData = info.getMcuID();
				int Ver_sn = (verData[0] << 24) | (verData[1] << 16) | (verData[2] << 8)
						| verData[3];
				tzutil.mcuID.append(String.format("%016d", Ver_sn));
			}
		}
	};

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_GET_SYSTEM_VIDEO_PARAMS) {
				mSystemParamIndex = MCU_SYSTEM_PARAM_VIDEOSTATE_INDEX;
				try {
					mMcuManager.RPC_GetSysParams(mSystemParamIndex);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (msg.what == MSG_SET_MCU_PARAMS) {
				McuMethodManager mMcuMethodManager = McuMethodManager.getInstance(mContext);
				setMcuSystemPara();
				mMcuMethodManager.setRadioCountry();
				mMcuMethodManager.setRadioType();
				boolean enable = XmlParse.headlight_enable == 1 ? true : false;
				mMcuMethodManager.setMcuParam_Headlight_detection(enable);
				mMcuMethodManager.setMcuParam_backcar();
				// mMcuMethodManager.setCantype();
				// mMcuMethodManager.setMcuParam_sleepTime();
				mMcuMethodManager.setMcuParam_qicaideng();

				//int flag = SystemProperties.getBoolean(Tag.PERSYS_SLEEP_POWER_ENABLE, true) ? 0 : 1;
				int sleepTimeIndex = SystemProperties.getInt(Tag.PERSYS_SLEEP_READY_TIME,
						XmlParse.sleep_ready_time);
				int powerOffTimeIndex = SystemProperties.getInt(Tag.PERSYS_SLEEP_TIME,
                        XmlParse.sleep_time);
				int sleepTime = XmlParse.intarray_sleep_time[sleepTimeIndex];
				int powerOffTime = XmlParse.intarray_power_off_time[powerOffTimeIndex];
				McuMethodManager.getInstance(mContext).sendKeyPowerOffCMD(1, sleepTime);
				McuMethodManager.getInstance(mContext).setMcuSystemParam_sleep(sleepTime, powerOffTime);
				
				mHandler.sendEmptyMessageDelayed(MSG_GET_SYSTEM_VIDEO_PARAMS, 500);
			} else if (msg.what == MSG_ID_CHECK_BACKCAR_STATUS) {
			    int backcarStatus = YeconMetazone.GetBackCarStatus();
			    L.e("MSG_ID_CHECK_BACKCAR_STATUS - backcarStatus: " + backcarStatus);
			    if (backcarStatus == 1) {
                    removeMessages(MSG_ID_CHECK_BACKCAR_STATUS);
                    sendEmptyMessageDelayed(MSG_ID_CHECK_BACKCAR_STATUS,
                            CHECK_BACKCAR_STATUS_TIMEOUT);
                } else if (backcarStatus == 2) {
                    try {
                        mMcuManager.RPC_GetMcuVer();
                        
                        mHandler.sendEmptyMessageDelayed(MSG_SET_MCU_PARAMS, 1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
			}
		}
	};

	@Override
	public void onCreate() {
		L.v("CarSettingsBootService - onCreate");
		mContext = this;
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		
		XmlParse.intarray_sleep_time = getResources().getIntArray(R.array.sleep_ready_time);
		XmlParse.intarray_power_off_time = getResources().getIntArray(R.array.power_off_time);
		
		initCarSettingsSP();
		initReceiver();
		initMCUManeger();
		openGPS();	
		mPref = this.getSharedPreferences("light_settings", MODE_PRIVATE);
		mEditor = mPref.edit();
		//setScreenBrightness();
		
		Intent intent = new Intent("action.set.backlight");
		intent.putExtra("brightness",mPref.getInt("light_level", 0x9f));
		sendBroadcast(intent);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(mCarSettingsReceiver);
		this.unregisterReceiver(mMountReceiver);

		try {
			mMcuManager.RPC_RemoveMcuInfoChangedListener(mMcuListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		Intent intent = new Intent();
		intent.setClass(this, CarSettingsBootService.class);
		startService(intent);

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.v("CarSettingsBootService - onStartCommand");
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void initCarSettingsSP() {
		tzutil.initSharePF(this);
	}

	public void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(START_CARSETTINGS_SERVICE_ACTION);
		filter.addAction(Tag.ACTION_RADIO_UPDATE);
		filter.addAction(MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS);
		filter.addAction(ACTIVITY_CHANGE);
		filter.addAction("action.set.backlight");
		filter.addAction("action.system.keycode.backlightOn");
		this.registerReceiver(mCarSettingsReceiver, filter);

		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addDataScheme("file");
		this.registerReceiver(mMountReceiver, filter);
	}

	public void initMCUManeger() {
		mMcuManager = (McuManager) mContext.getSystemService(Context.MCU_SERVICE);
		try {
			mMcuManager.RPC_RequestMcuInfoChangedListener(mMcuListener);

			int backcarStatus = YeconMetazone.GetBackCarStatus();
			if (backcarStatus == 1) {
	            mHandler.sendEmptyMessageDelayed(MSG_ID_CHECK_BACKCAR_STATUS,
	                    CHECK_BACKCAR_STATUS_TIMEOUT);
	        } else if (backcarStatus == 2) {
    			mMcuManager.RPC_GetMcuVer();
    
    			// mMcuManager.RPC_GetMcuID();
    
    			mHandler.sendEmptyMessageDelayed(MSG_SET_MCU_PARAMS, 1500);
	        }
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void startMcuServer() {
		try {
			mMcuManager.RPC_GetStatus();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * this is set the bright when the headlight off or no when off is normal
	 * when on check true,will set low bright; when check false,set normal
	 * bright
	 */
	private void setHeadLightModel(int isOpen) {
		boolean isCheck = SystemProperties.getBoolean(Tag.PERSYS_HEADLIGHT_ENABLE, false);
		boolean isPowerKey = SystemProperties.getBoolean(PROPERTY_KEY_POWERKEY, false);
		boolean isBlackoutKey = SystemProperties.getBoolean(PROPERTY_KEY_BACKOUTKEY, false);
		L.e("setHeadLightModel - isPowerKey: " + isPowerKey + " - isBackoutKey: " + isBlackoutKey);
		if (isPowerKey || isBlackoutKey || !isCheck) {
			return;
		}
		int backlightLevel = BacklightControl.getBacklightLevel(BacklightControl.getBrightness());
		int backlight = ArraySetting.BRIGHT_TAB[backlightLevel];
		if (isOpen == 1) {
			if (!BacklightControl.GetBklEnable()) {
				BacklightControl.SetBklEnable(1, 1, 0);
			}
			int smallLightsBacklight = tzutil.getIntValue(small_lights_backlight_tag, BRIGHTNESS_HIGH);
			if (smallLightsBacklight == SMALL_LIGHTS_BACKLIGHT) {
			    return;
			}
			mSaveBacklightLevel = BacklightControl.getBacklightLevel(BacklightControl
					.getBrightness());
			tzutil.saveIntValue(backlight_level_tag, mSaveBacklightLevel);
			backlightLevel = BKL_LOW;
			backlight = BRIGHTNESS_LOW;
			tzutil.saveIntValue(small_lights_backlight_tag, SMALL_LIGHTS_BACKLIGHT);
		} else {
		    mSaveBacklightLevel = tzutil.getIntValue(backlight_level_tag, BKL_HIGH);
			backlightLevel = mSaveBacklightLevel;
			// if (isPowerKey) {
			// if (BacklightControl.GetBklEnable()) {
			// BacklightControl.SetBklEnable(0, 0, 1);
			// backlightLevel = 0;
			// }
			// }
			// if (isBlackoutKey) {
			// if (BacklightControl.GetBklEnable()) {
			// BacklightControl.SetBklEnable(0, 1, 0);
			// backlightLevel = 0;
			// }
			// }
			backlight = ArraySetting.BRIGHT_TAB[mSaveBacklightLevel];
			tzutil.saveIntValue(small_lights_backlight_tag, backlight);
		}

		SystemProperties.set(Tag.PERSYS_BKLIGHT[0], backlight + "");
		setMcuSystemPara();
		McuMethodManager.getInstance(mContext).sendBlackoutKeyCMD(backlightLevel);
		Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
				backlight);
		Intent intent = new Intent(Tag.ACTION_BRIGHTNESS_CHANGED);
		sendBroadcast(intent);

		L.e("setHeadLightModel - backlightLevel: " + backlightLevel + " - backlight: " + backlight
		        + " - mSaveBacklightLevel: " + mSaveBacklightLevel);
	}

	private void setMcuSystemPara() {
		McuMethodManager.SystemParam para = new McuMethodManager.SystemParam();
		para.brightness = SystemProperties.getInt(Tag.PERSYS_RGB_VIDEO[0][0],
				XmlParse.rgb_video[0][0]);
		para.contrast = SystemProperties.getInt(Tag.PERSYS_RGB_VIDEO[0][1],
				XmlParse.rgb_video[0][1]);
		para.hue = SystemProperties.getInt(Tag.PERSYS_RGB_VIDEO[0][2], XmlParse.rgb_video[0][2]);
		para.saturation = SystemProperties.getInt(Tag.PERSYS_RGB_VIDEO[0][3],
				XmlParse.rgb_video[0][3]);
		para.backlight = SystemProperties.getInt(Tag.PERSYS_BKLIGHT[0],
		        ArraySetting.BRIGHTNESS_HIGH);
		para.backcarMirror = SystemProperties.getInt(Tag.PERSYS_BACKCAR_MIRROR,
				XmlParse.backcar_mirror);
		McuMethodManager.getInstance(mContext).setMcuSystemParam(para);
	}

	public void setMediaBanOnVideo(int isOpen) {
		boolean isCheck = XmlParse.brake_enable == 1 ? true : false;
		if (!isCheck)
			return;

		if (isOpen == 1) {// open and check
			// if open ,then close video
		} else {
			// if close open the video
		}

	}

	public void openGPS() {
		DatetimeSetActivity.setLocationListener(this);
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		mLocationListener = new LocationListener() {

			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			public void onProviderEnabled(String provider) {

			}

			public void onProviderDisabled(String provider) {

			}

			public void onLocationChanged(Location location) {
				setDate(location);
				if (mLListener != null) {
					mLListener.onLocation(location);
				}
			}
		};

		if (tzutil.getAutoState(mContext, Settings.Global.AUTO_TIME)) {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					ArraySetting.update_gps_time, 0, mLocationListener);

		}

	}

	public interface onLocationListener {
		public void onLocation(Location location);
	}

	public static onLocationListener mLListener;

	public static void setLocationListener(onLocationListener mL) {
		mLListener = mL;
	}

	@Override
	public void removeGpsListener(boolean isRemove) {
		if (isRemove) {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					ArraySetting.update_gps_time, 0, mLocationListener);
		} else {
			mLocationManager.removeUpdates(mLocationListener);
		}
	}

	static void setTime(Context context, int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();

		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
		}
	}

	public static int longToString(long currentTime, String formatType) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatType);
		String data = dateFormat.format(new Date(currentTime));
		return Integer.parseInt(data);
	}

	void setDate(Location mLication) {
		int year = longToString(mLication.getTime(), "yyyy");
		int month = longToString(mLication.getTime(), "MM") - 1;
		int day = longToString(mLication.getTime(), "dd");
		int hour = longToString(mLication.getTime(), "HH");
		int minute = longToString(mLication.getTime(), "mm");
		Calendar mCalendar = Calendar.getInstance();
		// L.v(year + "%%setDate%%" + month + "&&&&" + day);
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, month);
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
		mCalendar.set(Calendar.HOUR_OF_DAY, hour);
		mCalendar.set(Calendar.MINUTE, minute);
		long when = mCalendar.getTimeInMillis();
		if (when / 1000 < Integer.MAX_VALUE) {
			((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setTime(when);
		}

		// updateTitle();
	}

}
