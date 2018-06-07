package com.android.launcher2.util;

import com.media.constants.MediaConstants;

import android.constant.YeconConstants;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

//import static android.constant.YeconConstants.*;

public class Function {
	private final static String TAG="Function";
	public static void onStartActivity(Context context, String packageName, String className) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(new ComponentName(packageName, className));
		context.startActivity(intent);
	}

	public static void onNavigation(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String packageName = null;
		String className = null;
		String mapPackage = SystemProperties.get("persist.sys.maps", "nothing");
		if (!mapPackage.equals("nothing")) {
			packageName = mapPackage.split("#")[0];
			className = mapPackage.split("#")[1];
			intent.setComponent(new ComponentName(packageName, className));
			context.startActivity(intent);
		} else {
			// packageName = CAR_SETTING_PACKAGE_NAME;
			// className = CAR_SETTING_NAVIGATION_ACTIVITY;
			packageName = "com.yecon.carsetting";
			className = "com.yecon.carsetting.NavigationAcitivity";
			onStartActivity(context, packageName, className);
		}
	}

	public static void onRadio(Context context) {
		// String packageName = FMRADIO_PACKAGE_NAME;
		// String className = FMRADIO_START_ACTIVITY;
		String packageName = "com.yecon.fmradio";
		String className = "com.yecon.fmradio.FMRadioMainActivity";
		onStartActivity(context, packageName, className);
	}

	public static void onBT(Context context) {
		// String packageName = BLUETOOTH_PACKAGE_NAME;
		// String className = BLUETOOTH_START_ACTIVITY;
		String packageName = "com.autochips.bluetooth";
		String className = "com.autochips.bluetooth.MainBluetoothActivity";
		onStartActivity(context, packageName, className);
	}

	public static void onMusic(Context context) {
		// String packageName = MUSIC_PACKAGE_NAME;
		// String className = MUSIC_START_ACTIVITY;
		String packageName = "com.yecon.music";
		String className = "com.yecon.music.MusicPlaybackMainActivity";
		onStartActivity(context, packageName, className);
	}
	
	public static void onCarSetting(Context context) {
		Log.d(TAG, "run car setting icon");
		String packageName = "com.yecon.carsetting";
		String className = "com.yecon.carsetting.FragmentTabAcitivity";
		onStartActivity(context, packageName, className);
	}

	public static void onCarlife(Context context) {
		L.e("onCarlife");
		String packageName = YeconConstants.CARLIFE_PACKAGE_NAME;
		String className = YeconConstants.CARLIFE_START_ACTIVITY;
		onStartActivity(context, packageName, className);
		
		{
        	Intent intent = new Intent(Context.ACTION_SOURCE_STACK_NOMAL);
        	intent.putExtra(MediaConstants.EXTRA_APK_PACKAGENAME, YeconConstants.CARLIFE_PACKAGE_NAME);
        	intent.putExtra(MediaConstants.EXTRA_APK_ACTIVITY, YeconConstants.CARLIFE_START_ACTIVITY);
        	context.sendOrderedBroadcast(intent,null);
        }
		
		Intent intent = new Intent("com.yecon.action.closevoice");
		context.sendBroadcast(intent);
	}

	public static void onPhoneConnect(Context context) {
		L.e("onPhoneConnect");
		String packageName = YeconConstants.FAWLINK_PACKAGE_NAME;
		String className = YeconConstants.FAWLINK_START_ACTIVITY;
		onStartActivity(context, packageName, className);
		
		{
        	Intent intent = new Intent(Context.ACTION_SOURCE_STACK_NOMAL);
        	intent.putExtra(MediaConstants.EXTRA_APK_PACKAGENAME, YeconConstants.FAWLINK_PACKAGE_NAME);
        	intent.putExtra(MediaConstants.EXTRA_APK_ACTIVITY, YeconConstants.FAWLINK_START_ACTIVITY);
        	context.sendOrderedBroadcast(intent,null);
        }
		
		Intent intent = new Intent("com.yecon.action.closevoice");
		context.sendBroadcast(intent);
	}

	public static void onSetDatetime(Context context) {
		String packageName = "com.yecon.carsetting";
		String className = "com.yecon.carsetting.DatetimeSetActivity";
		onStartActivity(context, packageName, className);
	}
	
	public static void onSetWeatherCity(Context context) {
		String packageName = "com.autochips.weather";
		String className = "com.autochips.weather.SettingsActivity";
		onStartActivity(context, packageName, className);
	}


}
