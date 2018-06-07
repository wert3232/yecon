package com.android.gallery3d.app;

import java.lang.Thread.UncaughtExceptionHandler;

import android.os.Debug;
import android.os.Environment;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    private UncaughtExceptionHandler mDefaultHandler;
    private static final String OOM = "java.lang.OutOfMemoryError";
    private static final String HPROF_FILE_PATH = "/data/misc/gallery.hprof";

    private static CrashHandler sCrashHandler;

    private CrashHandler() {}

    public synchronized static CrashHandler getInstance() {
        if (sCrashHandler == null) {
            sCrashHandler = new CrashHandler();
        }
        return sCrashHandler;
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static boolean isOOM(Throwable throwable){
        Log.d(TAG, "getName:" + throwable.getClass().getName());
        if(OOM.equals(throwable.getClass().getName())){
            return true;
        }else{
            Throwable cause = throwable.getCause();
            if(cause != null){
                return isOOM(cause);
            }
            return false;
        }
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        if(isOOM(throwable)){
            try {
                Debug.dumpHprofData(HPROF_FILE_PATH);
            } catch (Exception e) {
                Log.e(TAG, "couldn’t dump hprof", e);
            }
        }

        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
}
