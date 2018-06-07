package com.autochips.quickbootmanager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import com.autochips.quickboot.IQuickBootService;
import com.autochips.quickboot.QuickBootManager;

public class QuickBootService extends Service {
    private static BroadcastReceiver mQuickBootRecv = null;
    private IBinder mIBinder = new ServiceImpl();

    private class ServiceImpl extends IQuickBootService.Stub {
        @Override
        public boolean addToWhiteList(String packageName) {
            Utils.IF_LOG_OUT("prepare to add " + packageName + " to whitelist");
            if (packageName == null) return false;
            return Utils.addPackToList(
                    QuickBootService.this,
                    packageName,
                    Config.WhiteList,
                    Utils.WL_PREF_FILE);
        }

        @Override
        public boolean removeFromWhiteList(String packageName) {
            Utils.IF_LOG_OUT("prepare to remove " + packageName + " to whitelist");
            if (packageName == null) return false;
            return Utils.removePackFromList(
                    QuickBootService.this,
                    packageName,
                    Config.WhiteList,
                    Utils.WL_PREF_FILE);
        }

        @Override
        public boolean addToYellowList(String packageName) {
            Utils.IF_LOG_OUT("prepare to add " + packageName + " to yellowlist");
            if (packageName == null) return false;
            return Utils.addPackToList(
                    QuickBootService.this,
                    packageName,
                    Config.YellowList,
                    Utils.YL_PREF_FILE);
        }

        @Override
        public boolean removeFromYellowList(String packageName) {
            Utils.IF_LOG_OUT("prepare to remove " + packageName + " to yellowlist");
            if (packageName == null) return false;
            return Utils.removePackFromList(
                    QuickBootService.this,
                    packageName,
                    Config.YellowList,
                    Utils.YL_PREF_FILE);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        if (intent == null) {
            Utils.IF_RLOG_OUT("start service with null intent, maybe restart by AMS.");
        }
        if (!Config.IS_STATIC_BOOT && mQuickBootRecv == null) {
            Utils.IF_RLOG_OUT("register SCREEN_OFF and POWERON");
            mQuickBootRecv = new QuickBootReceiver();
            IntentFilter filter = new IntentFilter();
            //filter.addAction(QuickBootManager.ACTION_QB_POWEROFF);
            filter.addAction(QuickBootManager.ACTION_QB_POWERON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.setPriority(999);
            registerReceiver(mQuickBootRecv, filter);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Config.initConfig(this);
    }

    @Override
    public void onDestroy() {
        if (mQuickBootRecv != null) {
            unregisterReceiver(mQuickBootRecv);
            mQuickBootRecv = null;
        }
    }
}
