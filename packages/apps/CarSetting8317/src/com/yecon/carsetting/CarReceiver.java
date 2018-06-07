package com.yecon.carsetting;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

import com.yecon.carsetting.unitl.L;

public class CarReceiver extends BroadcastReceiver {
	private static final String NaviName = "com.yecon.action.NAVI_KEY";
	private int mapIndex = 0;
	private String[] mapPackage;
	private String sMapPackage;
	

	@Override
	public void onReceive(Context mContext, Intent mIntent) {
		String mActivity = mIntent.getAction();
		L.v("-----" + mActivity);
		getCurrentAction(mContext);
	}

	private void getCurrentAction(Context mContext) {
		mapPackage = mContext.getResources().getStringArray(R.array.map_info);
		mapIndex = Integer.parseInt(SystemProperties.get("persist.sys.maps", "0"));
		sMapPackage = mapPackage[mapIndex];
		Intent mIntent;

		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		if (cn.getPackageName().equals(sMapPackage.split("/")[0])) {
			mIntent = new Intent(Intent.ACTION_MAIN);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.addCategory(Intent.CATEGORY_HOME);
			mContext.startActivity(mIntent);

		} else {
			mIntent = new Intent(Intent.ACTION_MAIN);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.setComponent(new ComponentName(sMapPackage.split("/")[0], sMapPackage
					.split("/")[1]));
			mContext.startActivity(mIntent);
		}

		L.v("pkg:" + cn.getPackageName());
		L.v("cls:" + cn.getClassName());
	}

}
