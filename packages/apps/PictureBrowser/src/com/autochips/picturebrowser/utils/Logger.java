package com.autochips.picturebrowser.utils;

import android.util.Log;

public class Logger {
    private static final String TAG = "PictureBrowser";
    private static final boolean DEBUG = true;
    private static final boolean RDEBUG = true;

    private Logger() {
    }

    public static void print(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void printr(String msg) {
        if (RDEBUG) {
            Log.i(TAG, msg);
        }
    }
}
