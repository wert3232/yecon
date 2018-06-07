package com.baidu.che.codriver.parse;

import static android.mcu.McuExternalConstant.MCU_SYSTEM_PARAM_VIDEOSTATE_INDEX;
import static android.mcu.McuExternalConstant.T_BLACKOUT;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;


import com.autochips.bluetooth.LocalBluetoothProfileManager;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.hf.BluetoothHfUtility;
import com.baidu.che.codriver.platform.CoDriverPlatformConstants;
import com.baidu.che.codriver.platform.CoDriverPlatformManager;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.backlight.BacklightControl;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.media.AudioService;
import android.media.AudioSystem;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.renderscript.Element;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;


public class Function implements CoDriverDef{
	static Context sContext = null;
	static McuManager sMcuManager = null;
	static WifiManager sWifiManager = null;
	static boolean sSaveWifiEnable = false;
	static ConnectivityManager mConnService = null;
	static int sCallState = 0;
	static boolean sConnectState = false;
	static boolean sIsReverse = false;
	static final String TAG = "coDriverParse.Function";
	
	static BroadcastReceiver sReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.i(TAG, "onReceive action:"+action);
			if (action.equals(ACTION_BACKCAR_START)) {
				sIsReverse = true;
				CoDriverPlatformManager.getInstance().setValue(
						CoDriverPlatformConstants.KEY_WAKE_UP_STATE,
						CoDriverPlatformConstants.VALUE_WAKE_UP_DISABLE);
			} 
			else if (action.equals(ACTION_BACKCAR_STOP)) {
				sIsReverse = false;
				CoDriverPlatformManager.getInstance().setValue(
						CoDriverPlatformConstants.KEY_WAKE_UP_STATE,
						CoDriverPlatformConstants.VALUE_WAKE_UP_ENABLE);
			} 
			else if (action.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)) {
                com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile profilename = (BTProfile)intent.getSerializableExtra(LocalBluetoothProfileManager.EXTRA_PROFILE);
               if(profilename != null){
            	   if(profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)
	               			|| profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_A2DP_SINK)) {
		                int profilestate = intent.getIntExtra(LocalBluetoothProfileManager.EXTRA_NEW_STATE,LocalBluetoothProfileManager.STATE_DISCONNECTED);
		                if(profilestate == LocalBluetoothProfileManager.STATE_CONNECTED && !sConnectState){
		                	sConnectState = true;
		    				CoDriverPlatformManager.getInstance().setValue(
		    						CoDriverPlatformConstants.KEY_BT_STATE,
		    						CoDriverPlatformConstants.VALUE_BT_STATE_CONNECTED);
		                }else if(profilestate == LocalBluetoothProfileManager.STATE_DISCONNECTED && sConnectState){
		                	sConnectState = false;
		    				CoDriverPlatformManager.getInstance().setValue(
		    						CoDriverPlatformConstants.KEY_BT_STATE,
		    						CoDriverPlatformConstants.VALUE_BT_STATE_DISCONNECTED);
		                }
	               }
            	   Log.i(TAG, "onReceive sConnectState:"+sConnectState);
               }
			} 
			else if (action.equals(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE)) {
				sCallState = intent.getIntExtra(BluetoothHfUtility.EXTRA_CALL_STATE, 0);
		        if (sCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING ||
	        		sCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING ||
    				sCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING) {
					CoDriverPlatformManager.getInstance().setValue(
							CoDriverPlatformConstants.KEY_WAKE_UP_STATE,
							CoDriverPlatformConstants.VALUE_WAKE_UP_DISABLE);
		        	
		        } else if (sCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_IDLE) {
					CoDriverPlatformManager.getInstance().setValue(
							CoDriverPlatformConstants.KEY_WAKE_UP_STATE,
							CoDriverPlatformConstants.VALUE_WAKE_UP_ENABLE);
				}
		        Log.i(TAG, "onReceive sCallState:"+sCallState);
			}
		}
	};
	public static void onHomePressed(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(intent);
	}

	public static void setContext(Context context) {
		if (context != null) {
			sContext = context;
		}
		sContext.unregisterReceiver(sReceiver);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BACKCAR_START);
		intentFilter.addAction(ACTION_BACKCAR_STOP);
		intentFilter.addAction(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE);
		intentFilter.addAction(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE);
		sContext.registerReceiver(sReceiver, intentFilter);
	}
	
	public static void sendKeyCode(final int keyCode) {

		new Thread() {
			@Override
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(keyCode);
				} catch (Exception e) {

				}
			}
		}.start();
	}

	public static void Delay_sendKeyCode(final int keyCode) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				new Thread() {
					@Override
					public void run() {
						try {
							Instrumentation inst = new Instrumentation();
							inst.sendKeyDownUpSync(keyCode);
						} catch (Exception e) {

						}
					}
				}.start();
			}
		};
		timer.schedule(task, 500);
	}

	private static void openNavigation(boolean isOpen) {
		String mapPackage = SystemProperties.get("persist.sys.maps", "nothing");
		if (!mapPackage.equals("nothing")) {
			String packageName = mapPackage.split("#")[0];
			String className = mapPackage.split("#")[1];
			if (isOpen) {
				onStartActivity(sContext, packageName, className);
			} else {
				killBackgroudProcesses(packageName);
			}
		} else {
			if (isOpen) {
				onStartActivity(sContext, APP_PACKAGE_LIST[2][0], APP_PACKAGE_LIST[2][1]);
			}
		}
	}

	public static void onStartActivity(Context context, String packageName, String className) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(new ComponentName(packageName, className));
		startActivity(intent);
	}
	

	private static void startActivity(Intent intent) {
		if (sCallState != BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING &&
			sCallState != BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING &&
			sCallState != BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING ) {
			if (sContext != null && intent != null) {
				sContext.startActivity(intent);
			}
		}
	}

	public static boolean handleNavigation(Context context, String command) {

		if (!TextUtils.isEmpty(command) && sContext != null ) {
			try {
				JSONObject obj = new JSONObject(command);
				if (obj != null) {
					String domain = obj.optString("domain");
					String intents = obj.optString("intent");
					JSONObject objects = obj.optJSONObject("object");
					String item = objects.optString("item");
					//if ("navigate_instruction".equals(domain) && "navigate".equals(intents)) {
					if (HEAD_NAVIGATE_INSTRUCTION.equals(domain)) {
						if (NAVI_QUIT.equals(intents)) {
							openNavigation(false);
						} else {
							openNavigation(true);
						}
					} else if (HEAD_MAP.equals(domain) && MAP_ROUTE.equals(intents)) {
						openNavigation(true);
					}
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public static boolean handleMusic(String command) {

		if (!TextUtils.isEmpty(command) && sContext != null) {
			try {
				JSONObject obj = new JSONObject(command);
				if (obj != null) {
					String domain = obj.optString(JSON_TYPE[0]);
					String intents = obj.optString(JSON_TYPE[1]);
					JSONObject objects = obj.optJSONObject(JSON_TYPE[2]);
					String action_type = objects.optString(JSON_TYPE[5]);
					String mode = objects.optString(JSON_TYPE[7]);
					if (HEAD_MUSIC.equals(domain) && MUSIC_PLAY.equals(intents)) {
						sendKeyCode(KeyEvent.KEYCODE_YECON_MUSIC);
						String byartist = strChange(objects.optString(JSON_TYPE[8]));
						String song = objects.optString(JSON_TYPE[9]);
						String name = objects.optString(JSON_TYPE[10]);
						if ( !(byartist.equals("") && song.equals("") && name.equals(""))) {
							Intent intent = new Intent(ACTION_MEDIA_INFO);
							intent.putExtra("artist", byartist);
							if (song.equals("") && !name.equals("")) {
								intent.putExtra("song", name);
							} else {
								intent.putExtra("song", song);
							}
							sContext.sendBroadcast(intent);
						}
					} else if (HEAD_PLAYER.equals(domain) && PLAYER_SET.equals(intents)) {
						if (PLAYER_EXITPLAYER.equals(action_type)) {
							closeInternalApk(YeconConstants.MUSIC_PACKAGE_NAME);
						} else {
							sendKeyCode(KeyEvent.KEYCODE_YECON_MUSIC);
							if (PLAYER_PLAY.equals(action_type)) {
								Delay_sendKeyCode(KeyEvent.KEYCODE_MEDIA_PLAY);
								
							} else if (PLAYER_EXITPLAYER.equals(action_type)) {
								Delay_sendKeyCode(KeyEvent.KEYCODE_MEDIA_STOP);
								
							} else if (PLAYER_PAUSE.equals(action_type)) {
								Delay_sendKeyCode(KeyEvent.KEYCODE_MEDIA_PAUSE);
								
							} else if (PLAYER_PREVIOUS.equals(action_type)) {
								Delay_sendKeyCode(KeyEvent.KEYCODE_YECON_PREV);
								
							} else if (PLAYER_NEXT.equals(action_type)) {
								Delay_sendKeyCode(KeyEvent.KEYCODE_YECON_NEXT);
								
							} else if (PLAYER_RANDOM.equals(mode)) {
								Intent intent = new Intent(ACTION_MEDIA_RANDOM);
								intent.putExtra(ACTION_MEDIA_RANDOM, 0x01);
								sContext.sendBroadcast(intent);
								
							} else if (PLAYER_FULL_LOOP.equals(mode)) {
								Intent intent = new Intent(ACTION_MEDIA_REPEAT);
								intent.putExtra(ACTION_MEDIA_REPEAT, 0x02);
								sContext.sendBroadcast(intent);
								
							} else if (PLAYER_ORDER.equals(mode)) {
								Intent intent = new Intent(ACTION_MEDIA_REPEAT);
								intent.putExtra(ACTION_MEDIA_REPEAT, 0x00);
								sContext.sendBroadcast(intent);
								
							} else if (PLAYER_SINGLE.equals(mode)) {
								Intent intent = new Intent(ACTION_MEDIA_REPEAT);
								intent.putExtra(ACTION_MEDIA_REPEAT, 0x01);
								sContext.sendBroadcast(intent);
								
							} else if (PLAYER_SINGLE_LOOP.equals(mode)) {
								Intent intent = new Intent(ACTION_MEDIA_REPEAT);
								intent.putExtra(ACTION_MEDIA_REPEAT, 0x01);
								sContext.sendBroadcast(intent);
							}
						}
					}
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public static boolean handleSystem(Context context, String command) {
		if (!TextUtils.isEmpty(command) && sContext != null) {
			try {
				JSONObject obj = new JSONObject(command);
				if (obj != null) {
					String domain = obj.optString(JSON_TYPE[0]);
					String intents = obj.optString(JSON_TYPE[1]);
					JSONObject objects = obj.optJSONObject(JSON_TYPE[2]);
					String appname = objects.optString(JSON_TYPE[3]);
					String item = objects.optString(JSON_TYPE[4]);
					if (HEAD_APP.equals(domain)) {
						if (HEAD_OPEN.equals(intents) || HEAD_CLOSE.equals(intents)) {
							openSystem(intents, appname);
						}
					} else if (HEAD_CODRIVER.equals(domain)) {
						if (HEAD_OPEN.equals(intents) || HEAD_CLOSE.equals(intents)) {
							openSystem(intents, item);
						} 
						else if (CODRIVER_BACK_HOME.equals(intents)) {
							onHomePressed(context);
						} 
						else if (CODRIVER_VOLUME_UP.equals(intents)) {
							sendKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
						} 
						else if (CODRIVER_VOLUME_DOWN.equals(intents)) {
							sendKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN);
						} 
						else if (CODRIVER_VOLUME_UP_MAX.equals(intents)) {
							setVolume(AudioService.volumeMax);
						}
						else if (CODRIVER_VOLUME_DN_MIX.equals(intents)) {
							setVolume(0);
						}
						else if (CODRIVER_LOCK_SCREEN.equals(intents)) {
							sendKeyCode(KeyEvent.KEYCODE_YECON_BLACKOUT);
						} 
						else if (CODRIVER_BACK.equals(intents)) {
							sendKeyCode(KeyEvent.KEYCODE_BACK);
						} 
						else if (CODRIVER_LIGHT_UP.equals(intents)) {
							setBacklightMode(ADD_BKL);
						} 
						else if (CODRIVER_LIGHT_UP_MAX.equals(intents)) {
							setBacklightMode(MAX_BKL);
						} 
						else if (CODRIVER_LIGHT_DOWN.equals(intents)) {
							setBacklightMode(DEL_BKL);
						} 
						else if (CODRIVER_LIGHT_DOWN_MIN.equals(intents)) {
							setBacklightMode(MIN_BKL);
						} 
					}
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private static void setVolume(int volume) {
		AudioManager mAudioManager = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
        boolean mUnifiedVolumeAdjust = SystemProperties.getBoolean(
        		YeconConstants.PROPERTY_KEY_FUNC_UNIFIED_VOLUME_ADJUST, false);
		int streamType = AudioManager.STREAM_MUSIC;
        if (!mUnifiedVolumeAdjust) {
            streamType = getCurrentStreamType();
        }
		mAudioManager.setStreamVolume(streamType, 
				volume, AudioManager.FLAG_SHOW_UI);
	}
	
	private static int getCurrentStreamType() {
        int streamType = AudioManager.STREAM_MUSIC;

        if (AudioSystem.isStreamActive(AudioManager.STREAM_MUSIC, 0)) {
            streamType = AudioManager.STREAM_MUSIC;
        }
        boolean mIsNaviApp = SystemProperties.getBoolean(YeconConstants.PROPERTY_KEY_IS_NAVI_APP, false);
        boolean mNaviActivityIsForeground = SystemProperties.getBoolean(YeconConstants.PROPERTY_KEY_NAVI_IS_FORE,
                false);
        if (mIsNaviApp && mNaviActivityIsForeground) {
            streamType = AudioManager.STREAM_GIS;
        }

        if (AudioSystem.isStreamActive(AudioManager.STREAM_RING, 0)) {
            streamType = AudioManager.STREAM_RING;
        }

        if (AudioSystem.isStreamActive(AudioManager.STREAM_BLUETOOTH_SCO, 0)) {
            streamType = AudioManager.STREAM_BLUETOOTH_SCO;
        }

        if (AudioSystem.isStreamActive(AudioManager.STREAM_BACKCAR, 0)) {
            streamType = AudioManager.STREAM_BACKCAR;
        }

        if (AudioSystem.isStreamActive(AudioManager.STREAM_RDS, 0)) {
            streamType = AudioManager.STREAM_RDS;
        }

        StringBuffer log = new StringBuffer();
        log.append("getCurrentStreamType - streamType: ");
        log.append(streamType);
        Log.e(TAG, log.toString());

        return AudioManager.STREAM_GIS;
    }
	
	private static String strChange(String string) {
		String str = "[\"";
		int iStart = string.indexOf(str);
		int iEnd = string.indexOf("\"]");
		if (iStart != -1 && iEnd != -1) {
			return string.substring(iStart+str.length(), iEnd);
		} else {
			return string;
		}
	}
	
	public static void openSystem(String intents, String item) {
		if (HEAD_OPEN.equals(intents)) {
			if (APP_NAME_DOG.equals(item)) {
//				Uri uri = Uri.parse("dugou://");
//				final Bundle b = new Bundle();
//				b.putInt("voiceRegMode", 2);
//				if ("open".equals(intents)) {
//					b.putInt("actionType", 1);
//				} else {
//					b.putInt("actionType", 2);
//				}
//				Intent intent1 = new Intent("com.baidu.navi.action.START", uri);
//				intent1.putExtra("dugou", b);
//				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				sContext.startActivity(intent1);
			} 
			else if (APP_NAME_DVR.equals(item)) {
				onStartActivity(sContext, YeconConstants.DVR_PACKAGE_NAME, YeconConstants.DVR_START_ACTIVITY);
			} 
			else if (APP_NAME_KUWO.equals(item)) {
				//
			} 
			else if (CODRIVER_VIDEO.equals(item)) {
				sendKeyCode(KeyEvent.KEYCODE_YECON_VIDEO);
			} 
			else if (CODRIVER_MEDIA_PLAYER.equals(item)) {
				sendKeyCode(KeyEvent.KEYCODE_YECON_MUSIC);
			} 
			else if (CODRIVER_PICTURE.equals(item)) {
				onStartActivity(sContext, YeconConstants.PICBROWSER_PACKAGE_NAME, YeconConstants.PICBROWSER_START_ACTIVITY);
			} 
			else if (CODRIVER_BLUETOOTH.equals(item)) {
				sendKeyCode(KeyEvent.KEYCODE_YECON_BLUETOOTH);
			} 
			else if (CODRIVER_SYSTEM_SETTING.equals(item)) {
				sendKeyCode(KeyEvent.KEYCODE_YECON_SETTING);
			} 
			else if (CODRIVER_RADIO.equals(item)) {
				onStartActivity(sContext, YeconConstants.FMRADIO_PACKAGE_NAME, YeconConstants.FMRADIO_START_ACTIVITY);
			} 
			else if (CODRIVER_COLLISION_DETECT.equals(item)) {
				//
			} 
			else if (CODRIVER_WIFI.equals(item)) {
				setWifiEnable(true);
			} 
			else if (CODRIVER_NETWORK_SHARING.equals(item)) {
				setHotSopt(true);
			} 
			else if (CODRIVER_NETWORK.equals(item)) {
				setMobileDataEnabled(true);
			} 
			else if (CODRIVER_IND_CENTER.equals(item)) {
				onStartActivity(sContext, YeconConstants.SETTINGS_PACKAGE_NAME, YeconConstants.SETTINGS_STARTUP_ACTIVITY);
			}
			else if (CODRIVER_MUTE.equals(item)) {
				AudioManager mAudioManager = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
				mAudioManager.setYeconVolumeMute(AudioManager.STREAM_MUSIC, true, 
						AudioManager.FLAG_SHOW_UI, YeconConstants.SRC_VOLUME_MUTE);
              
			} 
			else if (CODRIVER_DVD.equals(item)) {
				sendKeyCode(KeyEvent.KEYCODE_YECON_DVD);
			}
			else if (CODRIVER_TV.equals(item)) {
				sendKeyCode(KeyEvent.KEYCODE_YECON_TV);
			}
			else if (CODRIVER_AVIN.equals(item)) {
				onStartActivity(sContext, YeconConstants.AVIN_PACKAGE_NAME, YeconConstants.AVIN_START_ACTIVITY);
			}
		} else if (HEAD_CLOSE.equals(intents)) {
			if (APP_NAME_DOG.equals(item)) {
				//
			} 
			else if (APP_NAME_DVR.equals(item)) {
				closeInternalApk(YeconConstants.DVR_PACKAGE_NAME);
			} 
			else if (APP_NAME_KUWO.equals(item)) {
				//
			} 
			else if (CODRIVER_PICTURE.equals(item)) {
				closeInternalApk(YeconConstants.PICBROWSER_PACKAGE_NAME);
			} 
			else if (CODRIVER_BLUETOOTH.equals(item)) {
				closeInternalApk(YeconConstants.BLUETOOTH_PACKAGE_NAME);
			} 
			else if (CODRIVER_RADIO.equals(item)) {
				closeInternalApk(YeconConstants.FMRADIO_PACKAGE_NAME);
			} 
			else if (CODRIVER_COLLISION_DETECT.equals(item)) {
				//
			} 
			else if (CODRIVER_WIFI.equals(item)) {
				setWifiEnable(false);
			} 
			else if (CODRIVER_NETWORK_SHARING.equals(item)) {
				setHotSopt(false);
			} 
			else if (CODRIVER_NETWORK.equals(item)) {
				setMobileDataEnabled(false);
			}			
			else if (CODRIVER_MUTE.equals(item)) {
				AudioManager mAudioManager = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
				mAudioManager.setYeconVolumeMute(AudioManager.STREAM_MUSIC, false, 
						AudioManager.FLAG_SHOW_UI, YeconConstants.SRC_VOLUME_UNMUTE);
			} 
			else if (CODRIVER_DVD.equals(item)) {
				closeInternalApk(YeconConstants.DVD_PACKAGE_NAME);
			}
			else if (CODRIVER_TV.equals(item)) {
				closeInternalApk(YeconConstants.TV_PACKAGE_NAME);
			}
			else if (CODRIVER_AVIN.equals(item)) {
				closeInternalApk(YeconConstants.AVIN_PACKAGE_NAME);
			}
		}
	}
	
	static void closeInternalApk(String name) {
		Intent intent = new Intent(YeconConstants.ACTION_QUIT_APK);
		intent.putExtra("apk_name", name);
		intent.setPackage(name);
		sContext.sendBroadcast(intent);
	}
	
	static void killBackgroudProcesses(String packageName) {
		ActivityManager am = (ActivityManager)sContext.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(packageName);
	}
	
	public static boolean handleTelePhone(Context context, String command) {
		if (!TextUtils.isEmpty(command) && sContext != null) {
			try {
				JSONObject obj = new JSONObject(command);
				if (obj != null) {
					String domain = obj.optString(JSON_TYPE[0]);
					String intents = obj.optString(JSON_TYPE[1]);
					JSONObject objects = obj.optJSONObject(JSON_TYPE[2]);
					String operation = objects.optString(JSON_TYPE[6]);
					if (HEAD_TELEPHONE.equals(domain)) {
						if (TELEPHONE_SETTING.equals(intents)) {
							if (TELEPHONE_ANSWERCALL.equals(operation)) {

							} else if (TELEPHONE_HANGUP.equals(operation)) {
								
							}
						} else if (TELEPHONE_CALL.equals(intents)) {
							String number = objects.optString(JSON_TYPE[11]);
							Intent intent = new Intent(ACTION_BT_CALLOUT);
							intent.putExtra(EXTRA_BT_CALLOUT_NUM, number);
							sContext.sendBroadcast(intent);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	static boolean handleOther(String command) {
		if (!TextUtils.isEmpty(command) && sContext != null) {
			try {
				JSONObject obj = new JSONObject(command);
				if (obj != null) {
					String domain = obj.optString(JSON_TYPE[0]);
					String intents = obj.optString(JSON_TYPE[1]);
					if (HEAD_CONTACTS.equals(domain) && HEAD_VIEW.equals(intents)) {
						if (sConnectState) {
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_LAUNCHER);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setComponent(new ComponentName(YeconConstants.BLUETOOTH_PACKAGE_NAME, 
									YeconConstants.BLUETOOTH_START_ACTIVITY));
							intent.putExtra(ACTION_BT_SON_ACTIVITY, BT_BOOK);
							startActivity(intent);
							
							Intent intent1 = new Intent(ACTION_BT_SON_ACTIVITY);
							intent1.putExtra(ACTION_BT_SON_ACTIVITY, BT_BOOK);
							sContext.sendBroadcast(intent1);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	static void setWifiEnable(boolean isOpen) {
		if (sWifiManager == null) {
			sWifiManager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
		}
		if (sWifiManager != null) {
			if (sWifiManager.getWifiState() == sWifiManager.WIFI_STATE_ENABLING
					|| sWifiManager.getWifiState() == sWifiManager.WIFI_STATE_DISABLING) {
				return;
			} else if (sWifiManager.getWifiState() == sWifiManager.WIFI_STATE_ENABLED && !isOpen) {
				sWifiManager.setWifiEnabled(false);

			} else if ((sWifiManager.getWifiState() == sWifiManager.WIFI_STATE_DISABLED
					|| sWifiManager.getWifiState() == sWifiManager.WIFI_STATE_UNKNOWN) && isOpen) {
				sWifiManager.setWifiApEnabled(null, false);
				sWifiManager.setWifiEnabled(true);
			}
		}
	}
	
	static void setHotSopt(boolean isOpen) {
		if (sWifiManager == null) {
			sWifiManager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
		}
		if (sWifiManager.isWifiApEnabled() && !isOpen) {
			sWifiManager.setWifiApEnabled(null, false);
			if (sSaveWifiEnable) {
				sWifiManager.setWifiEnabled(true);
			}
		} else {
			if (sWifiManager.isWifiEnabled() && isOpen) {
				sSaveWifiEnable = true;
				sWifiManager.setWifiEnabled(false);
			} else {
				sSaveWifiEnable = false;
			}
			sWifiManager.setWifiApEnabled(null, true);
		}
	}
	
	static void setBacklightMode(int type) {
        int iBacklightLevel = BacklightControl.getBacklightLevel(BacklightControl.getBrightness());
        int brightness = BacklightControl.getBrightness();
        boolean bIsBlkEnable = BacklightControl.GetBklEnable();

        if (iBacklightLevel == BKL_HIGH) {
			if (type == ADD_BKL || type == MAX_BKL ) {
				return;
			} else if (type == DEL_BKL) {
				iBacklightLevel = BKL_MID;
			} else if (type == MIN_BKL) {
				iBacklightLevel = BKL_LOW;
			}
		} else if (iBacklightLevel == BKL_MID) {
			if (type == ADD_BKL || type == MAX_BKL ) {
				iBacklightLevel = BKL_HIGH;
			} else if (type == DEL_BKL || type == MIN_BKL) {
				iBacklightLevel = BKL_LOW;
			}
		} else if (iBacklightLevel == BKL_LOW) {
			if (type == ADD_BKL) {
				iBacklightLevel = BKL_MID;
			} else if (type == MAX_BKL) {
				iBacklightLevel = BKL_HIGH;
			}else if (type == DEL_BKL || type == MIN_BKL) {
				return;
			} 
		}
        
        brightness = BRIGHT_TAB[iBacklightLevel];
        
        SystemProperties.set(Style_light_backlight_tag, brightness + "");
        
        setMcuSystemParam();
        sendBackoutCMD(iBacklightLevel);
        if (brightness < 0 && bIsBlkEnable) {
            Settings.System.putInt(sContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, BRIGHTNESS_HIGH);
            BacklightControl.SetBklEnable(0, 1, 0);
        } else {
            Settings.System.putInt(sContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, brightness);
        }
        
        Intent intent = new Intent(YECON_ACTION_BRIGHTNESS_CHANGED);
        sContext.sendBroadcast(intent);
    }
	
	static void setMcuSystemParam() {
        int brightness = SystemProperties.getInt(Style_light_bright_tag, DEFAULT_BRIGHTNESS);
        int contrast = SystemProperties.getInt(Style_light_contrast_tag, DEFAULT_CONTRAST);
        int hue = SystemProperties.getInt(Style_light_hue_tag, DEFAULT_HUE);
        int saturation = SystemProperties.getInt(Style_light_saturation_tag, DEFAULT_SATURATION);
        int backlight =  SystemProperties.getInt(Style_light_backlight_tag, DEFAULT_BACKLIGHT);
        int backcarMirror = SystemProperties.getInt(Style_backcar_mirror_tag, 0);
        
        byte[] videoParma = new byte[7];
        videoParma[0] = (byte) MCU_SYSTEM_PARAM_VIDEOSTATE_INDEX;
        videoParma[1] = (byte) brightness;
        videoParma[2] = (byte) contrast;
        videoParma[3] = (byte) hue;
        videoParma[4] = (byte) saturation;
        videoParma[5] = (byte) backlight;
        videoParma[6] = (byte) backcarMirror;
        
        try {
            if (sMcuManager == null) {
            	sMcuManager = (McuManager)sContext.getSystemService(Context.MCU_SERVICE);
            }
            if (sMcuManager != null) {
            	sMcuManager.RPC_SetSysParams(videoParma, videoParma.length);
			}
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
	
    static void sendBackoutCMD(int backlightLevel) {
        byte[] param = new byte[4];
        param[0] = (byte) backlightLevel;
        param[1] = (byte) 0x00;
        param[2] = (byte) 0x00;
        param[3] = (byte) 0x00;

        try {
        	if (sMcuManager == null) {
        		sMcuManager = (McuManager)sContext.getSystemService(Context.MCU_SERVICE);
			}
            if (sMcuManager != null) {
            	sMcuManager.RPC_KeyCommand(T_BLACKOUT, param);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    static boolean isMobileDataEnabled() {
    	if (mConnService == null) {
    		 mConnService = ConnectivityManager.from(sContext);
		}
        return mConnService.getMobileDataEnabled();
    }

    static void setMobileDataEnabled(boolean enabled) {
    	if (mConnService == null) {
   		 	mConnService = ConnectivityManager.from(sContext);
		}
    	if (enabled != isMobileDataEnabled()) {
    		 mConnService.setMobileDataEnabled(enabled);
		}
    }
}
