package com.autochips.miracast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class BroadcastFilter {
    public static final String WFD_SINK_GC_REQUEST_CONNECT = "com.mediatek.wifi.p2p.GO.GCrequest.connect";
    public static final String ACTION_QB_POWEROFF = "autochips.intent.action.QB_POWEROFF";
    public static final String P2P_DISCONNECT = "com.autochips.WifiP2pService.P2pDisconnect";
    public static final String ACTION_DISPLAY_DIRECT_READY = "autochips.intent.action.DISPLAY_DIRECT_READY";
    public static final String ACTION_PACKET_LOST = "autochips.intent.action.PACKET_LOST";
    public static final String GROUPNEGOT_P2P_DISCONNECT = "GroupNegotiationState.WifiMonitor.P2P_GROUP_REMOVED_EVENT";
    public static final String MIRACAST_CONNECT_START = "com.autochips.WifiP2pService.Miracast.ConnectStart";
    public static final String MIRACAST_CONNECT_END = "com.autochips.WifiP2pService.Miracast.ConnectEnd";

    private IntentFilter mFilterOnce = null;
    private IntentFilter mFilterWifi = null;
    private IntentFilter mFilterDisplay = null;

    private final String TAG = "BroadcastFilter";

    private static final String[] mRegisterOnce = {
        ACTION_QB_POWEROFF,
        WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION,
        WifiManager.NETWORK_STATE_CHANGED_ACTION,
    };

    private static final String[] mRegisterWifi = {
        WifiManager.WIFI_STATE_CHANGED_ACTION,
    };

    private static final String[] mRegisterDisplay = {
        DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED,
        WFD_SINK_GC_REQUEST_CONNECT,
        P2P_DISCONNECT,
        // NO miracast suspend reqire, so mark here.
        ACTION_DISPLAY_DIRECT_READY,
        ACTION_PACKET_LOST,
        MIRACAST_CONNECT_START,
        MIRACAST_CONNECT_END,
        GROUPNEGOT_P2P_DISCONNECT,
    };

    public BroadcastFilter(Context ctx) {
        mFilterOnce = new IntentFilter();
        mFilterWifi = new IntentFilter();
        mFilterDisplay = new IntentFilter();

        for (String item : mRegisterOnce) {
            mFilterOnce.addAction(item);
        }

        for (String item : mRegisterWifi) {
            mFilterWifi.addAction(item);
        }

        for (String item : mRegisterDisplay) {
            mFilterDisplay.addAction(item);
        }
    }

    public void registerOnce(Context ctx, BroadcastReceiver recver) {
        Log.i(TAG, "registerOnce");
        ctx.registerReceiver(recver, mFilterOnce);
    }

    public void registerWifi(Context ctx, BroadcastReceiver recver) {
        Log.i(TAG, "registerWifi");
        ctx.registerReceiver(recver, mFilterWifi);
    }

    public void registerDisplay(Context ctx, BroadcastReceiver recver) {
        Log.i(TAG, "registerDisplay");
        ctx.registerReceiver(recver, mFilterDisplay);
    }

    public void unregister(Context ctx, BroadcastReceiver recver) {
        ctx.unregisterReceiver(recver);
    }

}
