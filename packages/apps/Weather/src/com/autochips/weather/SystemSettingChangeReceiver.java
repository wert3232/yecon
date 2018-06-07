package com.autochips.weather;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class SystemSettingChangeReceiver extends BroadcastReceiver {
	private final String TAG="SystemSettingChangeReceiver";
	
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            String language = Locale.getDefault().getLanguage();
            if ("zh".equalsIgnoreCase(language)) {
                Constants.IS_EN = false;
            } else {
                Constants.IS_EN = true;
            }
        } else if (intent.getAction().equals(
                ConnectivityManager.CONNECTIVITY_ACTION)) {       
            WeatherApp.IS_WIFI_CONNECTED = Utils.isWiFiConnected(context);
        }else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        	//Æô¶¯Íê³É
        	Log.d(TAG, "weather---receive boot completed msg");
        	
        	if (!Utils.isServiceRunning(context, "com.autochips.weather.WeatherService")) {
        		Log.d(TAG, "weather---ACTION_BOOT_COMPLETED start weather service");
        		context.startService(new Intent(context, WeatherService.class));
        	}else {
        		Log.d(TAG, "weather---ACTION_BOOT_COMPLETED weather service already started");
        	}
        }
    }
}
