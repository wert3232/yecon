
package com.yecon.sourcemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i("boot", action);
		if ("android.intent.action.PRE_BOOT_COMPLETED".equals(action)
				|| "com.yecon.action.YECON_BOOT_COMPLETED".equals(action)) {
			if (null != context) {
				Intent startIntent = new Intent("android.intent.action.SourceManagerService");
				context.startService(startIntent);
			}
		} else if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
			if (null != context) {
				Intent startIntent = new Intent("android.intent.action.SourceManagerService");
				startIntent.putExtra("start", "source");
				context.startService(startIntent);
			}
		}
	}

}
