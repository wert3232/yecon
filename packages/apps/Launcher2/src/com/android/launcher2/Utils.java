package com.android.launcher2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
public class Utils {

	final static int DELAYMILLIS_CALIBRATION = 1000;
	final static int HANDLER_ID_CALIBRATION = 1000;

	public static void onStartActivity(Context context, String packageName, String className) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(new ComponentName(packageName, className));
		context.startActivity(intent);
	}

	public static void onCalibartion(Context context) {
		String packageName = "com.yecon.carsetting";
		String className = "com.yecon.carsetting.TouchCalibrationMainActivity";
		onStartActivity(context, packageName, className);
	}
}
