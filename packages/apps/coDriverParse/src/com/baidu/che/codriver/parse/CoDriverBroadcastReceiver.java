package com.baidu.che.codriver.parse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class CoDriverBroadcastReceiver extends BroadcastReceiver {
	private final String POWER_ON = "autochips.intent.action.QB_POWERON";
	private final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private final String CODRIVER_PLATFORM = "com.baidu.che.codriver.platform.go";
    @Override
    public void onReceive(Context context, Intent intent) {
    	String action = intent.getAction();
    	Log.i("coDriver", "CoDriverBroadcastReceiver :"+action);
    	if (action.equals(POWER_ON) || action.equals(BOOT_COMPLETED)) {
        	Intent intent1 = new Intent("com.baidu.codriver.action.START");
        	Uri uri = Uri.parse("codriver://lanuch?src=navi&state=true");
        	intent1.setData(uri);
        	context.startActivity(intent);
		} else if (action.equals(CODRIVER_PLATFORM)) {

	        Intent service = new Intent();
	        service.setClass(context, CoDriverBackgroadService.class);
	        context.startService(service);
		}

    }
}
