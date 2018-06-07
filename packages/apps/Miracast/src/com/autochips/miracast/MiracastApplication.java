package com.autochips.miracast;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class MiracastApplication extends Application {

    private static final String TAG = "MiracastActivity";
    private OnceStatusCallback mOnceCb = null;
    private WifiStatusCallback mWifiCb = null;
    private DisplayStatusCallback mDisplayCb = null;
    private WifiManager mWifiManager = null;
    private boolean mMiracastEverSetEnableWifi = false;
    private boolean mMiracastEverDisconnectWifi = false;
    // The Wifi state broadcast is sticky, use this to protect
    private boolean mLastWifiEnabled = false;
	private boolean mLastwifiEnabling = false;
    private BroadcastFilter mBroadcastFilter = null;

    public void setOnceStatusCallback(OnceStatusCallback cb) {
        Log.i(TAG, "Set Once Status Callback " + cb);
        mOnceCb = cb;
    }

    public void setWifiStatusCallback(WifiStatusCallback cb) {
        Log.i(TAG, "Set Wifi Status Callback " + cb);
        mWifiCb = cb;
    }

    public void setDisplayStatusCallback(DisplayStatusCallback cb) {
        Log.i(TAG, "Set Display Status Callback " + cb);
        mDisplayCb = cb;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mBroadcastFilter = new BroadcastFilter(this);
        mBroadcastFilter.registerOnce(this, mReceiverOnce);
        mBroadcastFilter.registerWifi(this, mReceiverWifi);
        mBroadcastFilter.registerDisplay(this, mReceiverDisplay);

        mMiracastEverSetEnableWifi = false;
        setLastWifiState();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void setLastWifiState() {
        mLastWifiEnabled = mWifiManager.isWifiEnabled();
    }

    public boolean isWifiEnabled() {
        return mLastWifiEnabled;
    }

	public boolean isWifiEnabling(){
		return mLastwifiEnabling;
	}

    public void feedWifiStatusDog() {
        mMiracastEverSetEnableWifi = false;
        mMiracastEverDisconnectWifi = false;
    }

    public boolean everOperateWifi() {
        return mMiracastEverSetEnableWifi || mMiracastEverDisconnectWifi;
    }

    public void setWifiEnabled(boolean enable) {
        setWifiEnabled(enable, false);
    }

    public void setWifiEnabled(boolean enable, boolean record) {
        Log.i(TAG, "[lingling]setWifiEnabled Wifi enable " + enable + ", record " + record);
        if (record) {
            mMiracastEverSetEnableWifi = true;
        }
        mWifiManager.setWifiEnabled(enable);
    }

    public boolean disconnectWifiAp() {
        mMiracastEverDisconnectWifi = true;
        return mWifiManager.disconnect();
    }

    private final BroadcastReceiver mReceiverWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);
                if ((state == WifiManager.WIFI_STATE_ENABLED)) {
                    Log.i(TAG, "[lingling]BroadcastReceiver on Wifi enabled");
                    if (!mLastWifiEnabled)
                        mLastWifiEnabled = true;
                    else
                        return;

					if(mLastwifiEnabling){
						mLastwifiEnabling = false;
					}
                } else if (state == WifiManager.WIFI_STATE_DISABLED) {
                    Log.i(TAG, "[lingling]BroadcastReceiver on Wifi disabled");
                    if (mLastWifiEnabled) {
                        mLastWifiEnabled = false;
                    }
                    else
                        return;

					if(mLastwifiEnabling){
						mLastwifiEnabling = false;
					}
                } else if (state == WifiManager.WIFI_STATE_ENABLING) {
                    Log.i(TAG, "[lingling]BroadcastReceiver on Wifi enabling");
					mLastwifiEnabling = true;
                } else {
                    return;
                }
                if (mWifiCb != null) {
                    mWifiCb.notifyWifiStatus(state);
                } else {
                    Log.i(TAG, "[lingling]BroadcastReceiver no one care");
                }
            }
        }
    };

    private final BroadcastReceiver mReceiverDisplay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "[lingling]BroadcastReceiver on Display " + intent.getAction());
            if (mDisplayCb != null) {
                mDisplayCb.notifyDisplayStatus(intent);
            } else {
                Log.i(TAG, "[lingling]BroadcastReceiver on Display no one care");
            }
        }
    };

    private final BroadcastReceiver mReceiverOnce = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "[lingling]BroadcastReceiver on Once " + intent.getAction());
            if (mOnceCb != null) {
                mOnceCb.notifyOnceStatus(intent);
            } else {
                Log.i(TAG, "[lingling]BroadcastReceiver on Display no one care");
            }
        }
    };
}
