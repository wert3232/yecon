package com.android.settings;

import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.app.WallpaperInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.os.SystemProperties;
import com.autochips.quickboot.QuickBootManager;
import android.net.wifi.WifiManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.util.Log;
import com.android.settings.wifi.WifiEnabler;
import android.widget.Switch;
import com.autochips.quickboot.QuickBootManager;
import android.net.wifi.WifiNative;
import android.os.PowerManager;

import static android.constant.YeconConstants.*;


/**
 * Manage the white list and act based on the white list.
 * <p>
 * The system app which would be listed in white list would
 * NOT be killed when quickboot down, but the 3rd party one
 * would be done.
 * <p>
 * Of cause, could use broadcast to config that:
 * <ul>
 *   <li>Whether launch the killed app when quickboot on</li>
 *   <li>Launch which activity or activities when quickboot on</li>
 *   <li>Loop it or them infinite or limited n times in gived interval</li>
 * </ul>
 * <b>First Modify:</b>
 * <p>
 * <ul>
 *   <li>Add addToWhiteList and removeFromWhiteList APIs, could be invoked
 *       with QuickBootManager in autochips.jar</li>
 * </ul>
 */

public class WifiQuickBootReceiver extends BroadcastReceiver {
	private String LOG_TAG = "WifiQuickBootReceiver";
    private static Context mCtx = null;
    private WifiManager mWifiManager;
    private static boolean mWifiOnOff = false;
	private static boolean mWifiDriverload = true;
	private String mInterfaceName;
	private WifiNative mWifiNative;
	private PowerManager.WakeLock mWakeLock;
	private int mCount = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        mCtx = context;
		
		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int wifiState = mWifiManager.getWifiState();
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
		mWakeLock.setReferenceCounted(false);
        if(QuickBootManager.ACTION_QB_POWERON.equals(action)){
            Log.e(LOG_TAG,"Wifi Receive ACTION_QB_POWERON");
			if(mWifiOnOff){					
				Log.e(LOG_TAG,"Wifi Receive ACTION_QB_POWERON: Wlan Enabled");
				mWifiManager.setWifiEnabled(mWifiOnOff);
			}
        }else if(ACTION_QB_POWEROFF.equals(action)){
            Log.e(LOG_TAG,"Wifi Receive ACTION_QB_POWEROFF");
			mCount = 10;
			wifiState = mWifiManager.getWifiState();
			mWifiOnOff = wifiState == WifiManager.WIFI_STATE_ENABLED;
            SystemProperties.set(PROPERTY_WIFI_ONOFF, mWifiOnOff ? "true" : "false");
			if(mWifiOnOff){
				mWakeLock.acquire();
				Log.e(LOG_TAG,"Wifi Receive ACTION_QB_POWEROFF: acquire");
				mWifiManager.setWifiEnabled(false);
				while(mCount > 0)
				{
					if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED)
					{
						mWakeLock.release();
						break;
					} 
					else 
					{
						mCount--;
						try {
                    		Thread.sleep(1000);
                		} catch (InterruptedException x) {
                			Log.e(LOG_TAG,"Wifi Receive ACTION_QB_POWEROFF: Exception");
						}
					}
				}
				Log.e(LOG_TAG,"Wifi Receive ACTION_QB_POWEROFF: release");
				mWakeLock.release();
				
			}
        }
    }
}
