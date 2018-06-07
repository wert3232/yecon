package com.autochips.quickbootmanager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;

public class StartAppInfo {
    public static final int NON_INTERVAL = 0;
    public static final int NON_TIME = -1;
    public static final int SLICE_TIME = 2000; //2s
    public static final int DELAY_START_TIME = 500; //0.5s

    public static final int NOT_SET_YET = 0;
    public static final int SET_FROM_BROAD = 1;
    public static final int SET_FROM_PROP = 2;

    private boolean mBeginTransact = false;
    private boolean mBarrier = false;
    private int mLoopInterval = NON_INTERVAL;
    private int mLoopTime = NON_TIME;
    private int mStartedIndex = -1;
    private List<Intent> mIntents = new ArrayList<Intent>();
    private HashSet<String> mPackage = new HashSet<String>();
    private int setFrom = NOT_SET_YET;

    public void setFrom(int from) {
        setFrom = from;
    }

    public int getLastSetFrom() {
        return setFrom;
    }

    public int getLoopInterval() {
        return mLoopInterval;
    }

    public void setLoopInterval(int interval) {
        if (interval <= NON_INTERVAL) return;
        mLoopInterval = interval;
    }

    public int getLoopTime() {
        return mLoopTime;
    }

    public void setLoopTime(int time) {
        if (time <= NON_TIME) return;
        mLoopTime = time;
    }

    public void beginTransact() {
        mBeginTransact = true;
        mBarrier = false;
        mStartedIndex = -1;
        mLoopInterval = NON_INTERVAL;
        mLoopTime = NON_TIME;
        mIntents.clear();
        mPackage.clear();
    }

    public void endTransact() {
        mBeginTransact = false;
    }

    public void addComponent(ComponentName name) {
        if (!mBeginTransact) return;
        if (name != null) {
            Log.e("QuickBootReceiver",
                    "addComponent " + name);
            Intent intent = new Intent();
            intent.setComponent(name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntents.add(intent);
            String pack = name.getPackageName();
            if (pack != null && !mPackage.contains(pack))
                mPackage.add(pack);
        }
    }

    public void addIntent(Intent intent) {
        if (!mBeginTransact) return;
        if (intent != null) {
            Log.e("QuickBootReceiver",
                    "addIntent " + intent);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntents.add(intent);
            String pack = intent.getComponent().getPackageName();
            if (pack != null && !mPackage.contains(pack))
                mPackage.add(pack);
        }
    }

    public Intent getNextIntent() {
        if (mBeginTransact) return null;
        int size = mIntents.size();
        Log.e("QuickBootReceiver",
                "size = " + size + ", index" + mStartedIndex);
        if (++mStartedIndex >= size) {
            mStartedIndex = -1;
            return null;
        }
        return mIntents.get(mStartedIndex);
    }

    public void setUpdateBeforeShutdown(boolean barrier) {
        mBarrier = barrier;
    }

    public boolean needUpdateBeforeShutdown() {
        return mBarrier;
    }

    public void forceStopPackage(ActivityManager mgr) {
        if (mBeginTransact) return;
        for (Iterator it = mPackage.iterator(); it.hasNext();) {
            mgr.forceStopPackage((String)it.next());
        }
    }

    public boolean valid() {
        return !mIntents.isEmpty();
    }

    public void clear() {
        beginTransact();
    }
}
