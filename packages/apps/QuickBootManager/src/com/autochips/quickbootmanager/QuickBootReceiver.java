package com.autochips.quickbootmanager;

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
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemProperties;
import com.autochips.quickboot.QuickBootManager;
import com.autochips.quickbootmanager.filters.FilterHub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

public class QuickBootReceiver extends BroadcastReceiver {
    private static final String QB_TEST_KILL = "autochips.intent.action.KILL_APPS";
    private static final String QB_RESUME_APP = "autochips.intent.action.RESUME_APPS";
    private static final String QB_TEST_START = "autochips.intent.action.TEST_APPS";
    private static StartAppInfo mStartApp = new StartAppInfo();
    private static Context mCtx = null;

    private static final String PARAMS_SPLIT = "%";
    private static final String PARAM_SPLIT = "#";
    private static final String KV_SPLIT = ":";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Utils.IF_RLOG_OUT("receive " + action);
        mCtx = context;
        if (QuickBootManager.ACTION_QB_POWEROFF.equals(action) ||
                QB_TEST_KILL.equals(action)) { // Only for TEST
            doQuickBootPowerOff(context);
        } else if(QuickBootManager.ACTION_QB_POWERON.equals(action)) {
            doQuickBootPowerOn(context);
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Config.initConfig(context);
            Intent serv = new Intent(context, QuickBootService.class);
            context.startService(serv);
        } else if (QB_RESUME_APP.equals(action)) {
            String param = intent.getStringExtra("startapp");
            if (param != null)
                parseStartAppInfo(param, StartAppInfo.SET_FROM_BROAD);
        } else if (QB_TEST_START.equals(action)) { // Only for TEST
            startApp();
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            doQuickBootPowerOff(context);
        }
    }

    /**
     * Property string maybe: L%P:packagename#A:activityname%I:time
     * L - resume Last kill app
     * P - Package name
     * A - Activity name
     * I - Interval time
     * T - loop Time
     * B - extra int
     * C - extra int array
     * D - extra string
     * E - extra boolean
     *
     * The P/A param is higher prioprity than L param.
     */
    public static void parseStartAppInfo(String prop, int from) {
        Utils.IF_LOG_OUT("params origin string :" + prop);
        String[] params = prop.split(PARAMS_SPLIT);
        mStartApp.beginTransact();
        for (int i = 0; i < params.length; i++) {
            String pack = null;
            String activity = null;
            String temp = params[i];
            if (temp == null || temp.length() == 0) continue;
            Utils.IF_LOG_OUT("param origin string :" + temp);
            String[] param = temp.split(PARAM_SPLIT);
            if (param == null || param.length == 0) continue;
            Intent extra = new Intent();
            for (int j = 0; j < param.length; j++) {
                temp = param[j];
                if (temp == null || temp.length() == 0) continue;
                Utils.IF_LOG_OUT("single param origin string :" + temp);
                switch(temp.charAt(0)) {
                  case 'L':
                    mStartApp.setUpdateBeforeShutdown(true);
                    break;
                  case 'P':
                    if (2 < temp.length()) {
                        pack = temp.substring(2);
                    } else {
                        Utils.IF_RLOG_OUT("the P param wrong: miss value");
                    }
                    break;
                  case 'A':
                    if (2 < temp.length()) {
                        activity = temp.substring(2);
                    } else {
                        Utils.IF_RLOG_OUT("the A param wrong: miss value");
                    }
                    break;
                  case 'C':
                    if (2 < temp.length()) {
                        String[] iaStr = null;
                        iaStr = temp.substring(2).split(",");
                        int[] ia = new int[iaStr.length - 1];
                        for (int k = 0; k < iaStr.length - 1; k++) {
                            ia[k] = Integer.valueOf(iaStr[k + 1]);
                        }
                        extra.putExtra(iaStr[0], ia);
                    } else {
                        Utils.IF_RLOG_OUT("the C param wrong: miss value");
                    }
                    break;
                  case 'D':
                    if (2 < temp.length()) {
                        String[] saStr = temp.substring(2).split(",");
                        extra.putExtra(saStr[0], saStr[1]);
                    } else {
                        Utils.IF_RLOG_OUT("the D param wrong: miss value");
                    }
                    break;
                  case 'E':
                    if (2 < temp.length()) {
                        String[] saStr = temp.substring(2).split(",");
                        extra.putExtra(saStr[0],
                                Boolean.valueOf(saStr[1]));
                    } else {
                        Utils.IF_RLOG_OUT("the D param wrong: miss value");
                    }
                    break;
                  case 'I':
                    if (2 < temp.length()) {
                        int loopInterval = Integer.parseInt(temp.substring(2));
                        if (0 < loopInterval) mStartApp.setLoopInterval(loopInterval);
                    } else {
                        Utils.IF_RLOG_OUT("the I param wrong: miss value");
                    }
                    break;
                  case 'T':
                    if (2 < temp.length()) {
                        int loopTime = Integer.parseInt(temp.substring(2));
                        if (0 < loopTime) mStartApp.setLoopTime(loopTime);
                    } else {
                        Utils.IF_RLOG_OUT("the T param wrong: miss value");
                    }
                    break;
                }
            }

            if (pack != null) {
                Utils.IF_LOG_OUT("try to start package " + pack);
                if (activity == null) {
                    PackageManager pm = mCtx.getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(pack);
                    if (intent != null) {
                        Utils.IF_LOG_OUT("get launch intent " + intent);
                        Bundle bundle = extra.getExtras();
                        if (bundle != null) intent.putExtras(bundle);
                        mStartApp.addIntent(intent);
                    } else {
                        Utils.IF_RLOG_OUT("can't get launch intent for " + pack);
                    }
                } else {
                    Utils.IF_LOG_OUT("try to start activity " + activity);
                    ComponentName cn = new ComponentName(pack, activity);
                    extra.setComponent(cn);
                    mStartApp.addIntent(extra);
                }
            }
        }
        mStartApp.endTransact();
        mStartApp.setFrom(from);
    }

    private String updateStartAppWhenResume(
            ActivityManager mgr,
            Context ctx,
            Map<String, String> wl,
            Map<String, String> yl,
            String wallpaper) {
        if (!mStartApp.needUpdateBeforeShutdown()) {
            Utils.IF_RLOG_OUT("no need update start app before shutdown");
            return null;
        }
        List<ActivityManager.RunningTaskInfo> task = mgr.getRunningTasks(1);
        ComponentName top = task.get(0).topActivity;
        String packageName = top.getPackageName();
        if (!wl.containsKey(packageName)
                && !packageName.equals(wallpaper)
                && yl.containsKey(packageName)) {
            PackageManager pm = ctx.getPackageManager();
            Intent main = pm.getLaunchIntentForPackage(packageName);
            mStartApp.beginTransact();
            if (main != null) {
                Utils.IF_LOG_OUT("the 3rd party app with main " + main.toString() + " would be started when resume");
                mStartApp.addIntent(main);
            } else {
                Utils.IF_LOG_OUT("the 3rd party app with top " + top.toString() + " would be started when resume");
                mStartApp.addComponent(top);
            }
            mStartApp.setUpdateBeforeShutdown(false);
            mStartApp.endTransact();
            return packageName;
        } else {
            Utils.IF_RLOG_OUT("the system app is top activity now, but would not be kiled");
        }
        return null;
    }

    private WakeLock acquireWakeLock() {
        PowerManager pm =
            (PowerManager)mCtx.getSystemService(Context.POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP, "qbm");
        wl.setReferenceCounted(false);
        wl.acquire();
        return wl;
    }

    private void releaseWakeLock(WakeLock wl) {
        wl.release();
    }

    /**
     * The running apps not in whitelist will be killed.
     */
    public void doQuickBootPowerOff(Context context) {
        Utils.IF_LOG_OUT("doQuickBootPowerOff");
        mCtx = context;

        String me = context.getPackageName();
        Config.initConfig(context);
        Map<String, String> wl = Config.WhiteList;
        Map<String, String> yl = Config.YellowList;
        ArrayList<String> gisPacks = Config.GisWhiteList;

        mHandler.removeMessages(0);
        mHandler.removeMessages(1);

        // If not broadcast set, default with "L"
        String defProp = null;
        if (mStartApp.getLastSetFrom() != StartAppInfo.SET_FROM_BROAD)
            defProp = "L";
        String prop = SystemProperties.get("sys.qb.startapp_onresume", defProp);
        if (prop != null && 0 < prop.length())
            parseStartAppInfo(prop, StartAppInfo.SET_FROM_PROP);
        ActivityManager mgr =
            (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        WallpaperManager wm = WallpaperManager.getInstance(context);
        String wallpaper = null;
        WallpaperInfo wInfo = null;
        if ((wInfo = wm.getWallpaperInfo()) != null) {
            wallpaper = wInfo.getPackageName();
            Utils.IF_LOG_OUT("current wallpaper is " + wallpaper);
        }
        
        //modify by chenwl 2016/8/4 
        //String foregroundPack = updateStartAppWhenResume(
        //        mgr, context, wl, yl, wallpaper);
        String foregroundPack = "";
        //modify by chenwl end 
        
        List<ActivityManager.RunningAppProcessInfo> running =
            mgr.getRunningAppProcesses();
        int n = running.size();
        HashSet<String> wantKilled = new HashSet<String>();
        FilterHub.prepare(context);
        for (int i = 0; i < n; i++) {
            String[] pkgs = running.get(i).pkgList;
            int m = pkgs.length;
            //boolean killProc = true;
            Utils.IF_LOG_OUT("process name :" + running.get(i).processName);
            for (int j = 0; j < m; j++) {
                Utils.IF_LOG_OUT("\tpackage name :" + pkgs[j]);
                if (pkgs[j] == null) continue;
                if (!wl.containsKey(pkgs[j])
                        && !me.equals(pkgs[j])
                        && !pkgs[j].equals(wallpaper)
                        && !wantKilled.contains(pkgs[j])
                        && !pkgs[j].equals(foregroundPack)) {
                    //Utils.IF_RLOG_OUT("process " + running.get(i).processName +
                    //        " with " + pkgs[j] + " would not be killed");
                    //killProc = false;
                    boolean isGisApp = false;
                    for (int k = 0; k < gisPacks.size(); k++) {
                        if (pkgs[j].contains(gisPacks.get(k))) {
                            isGisApp = true;
                            break;
                        }
                    }
                    isGisApp = false;
                    if (isGisApp) {
                        continue;
                    }
                    if (!FilterHub.allowAlive(context, pkgs[j]))
                        wantKilled.add(pkgs[j]);
                    //break;
                }
            }
            //if (killProc) {
            //    Utils.IF_RLOG_OUT("kill process " + running.get(i).processName);
            //    mgr.forceStopPackage(pkgs[0]);
            //}
        }
        FilterHub.done(context);
        for(Iterator<String> it = wantKilled.iterator();
                it.hasNext();) {
            String killedPack = it.next();
            Utils.IF_RLOG_OUT("kill process " + killedPack);
            mgr.forceStopPackage(killedPack);
        }
        if (foregroundPack != null) {
            mgr.forceStopPackage(foregroundPack);
        }
        Utils.IF_LOG_OUT("QuickBootPowerOff done");
    }

    public static void startApp() {
        Utils.IF_LOG_OUT("try to start app");
        StartAppInfo info = mStartApp;
        Intent start = info.getNextIntent();
        int delay = StartAppInfo.DELAY_START_TIME;
        while (start != null) {
            Utils.IF_LOG_OUT("try to start app " + start);
            Message msg = Message.obtain(mHandler, 1, start);
            mHandler.sendMessageDelayed(msg, delay);
            start = info.getNextIntent();
            delay += StartAppInfo.SLICE_TIME;
        }
    }

    public static void doQuickBootPowerOn(Context context) {
        Utils.IF_LOG_OUT("doQuickBootPowerOn");
        mCtx = context;
        startApp();
        int loopInterval = mStartApp.getLoopInterval();
        if (loopInterval <= StartAppInfo.NON_INTERVAL) return;
        mHandler.sendEmptyMessageDelayed(0, loopInterval * 1000);
    }

    private static IntervalStartApp mHandler = new IntervalStartApp();
    private static class IntervalStartApp extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (mCtx != null) {
                switch(msg.what) {
                  case 0:
                    //StartAppInfo info = (StartAppInfo)msg.obj;
                    StartAppInfo info = mStartApp;
                    int loopInterval = info.getLoopInterval();
                    int loopTime = info.getLoopTime();
                    Utils.IF_LOG_OUT("cmd 0 loopInterval = " + loopInterval +
                            ", loopTime = " + loopTime);
                    if (loopInterval <= 0 || loopTime == 0) return;
                    ActivityManager mgr =
                        (ActivityManager)mCtx.getSystemService(
                        Context.ACTIVITY_SERVICE);
                    mStartApp.forceStopPackage(mgr);
                    startApp();
                    if (0 < loopTime) info.setLoopTime(loopTime - 1);
                    Message tramp = Message.obtain(this, 0, info);
                    sendMessageDelayed(tramp, loopInterval * 1000);
                    break;
                  case 1:
                    Intent start = (Intent)msg.obj;
                    Utils.IF_LOG_OUT("cmd 1 start " + start + " directly");
                    mCtx.startActivity(start);
                    break;
                }
            }
        }
    }
}
