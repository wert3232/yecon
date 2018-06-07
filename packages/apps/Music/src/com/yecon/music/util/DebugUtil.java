
package com.yecon.music.util;

import android.os.SystemProperties;
import android.util.Log;

public class DebugUtil {
    private static final String TAG = "mymusic";

    private static boolean DEBUG = true;

    static {
        String debugEnable = SystemProperties.get("ro.debug.enable", "false");
        if (debugEnable.equals("false")) {
            DEBUG = false;
        } else if (debugEnable.equals("true")) {
            DEBUG = true;
        }
    }

    public static void printLog(byte[] log, int len) {
        if (log == null) {
            return;
        }

        if (DEBUG) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String str = String.format("0x%02X ", log[i]);
                sb.append(str);
            }
            Log.e(TAG, "len: " + len + " - " + sb.toString());
        }
    }

    public static void printLog(byte[] log, int len, boolean debug) {
        if (log == null) {
            return;
        }

        if (debug) {
            printLog(log, len);
        }
    }

    public static void printLog(int[] log, int len) {
        if (log == null) {
            return;
        }

        if (DEBUG) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String str = String.format("0x%02X ", log[i]);
                sb.append(str);
            }
            Log.e(TAG, "len: " + len + " - " + sb.toString());
        }
    }

    public static void printLog(int[] log, int len, boolean debug) {
        if (log == null) {
            return;
        }

        if (debug) {
            printLog(log, len);
        }
    }

    public static void printLog(long[] log, int len) {
        if (log == null) {
            return;
        }

        if (DEBUG) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String str = String.format("0x%02X ", log[i]);
                sb.append(str);
            }
            Log.e(TAG, "len: " + len + " - " + sb.toString());
        }
    }

    public static void printLog(long[] log, int len, boolean debug) {
        if (log == null) {
            return;
        }

        if (debug) {
            printLog(log, len);
        }
    }

    public static void printLog(String log) {
        if (log == null) {
            return;
        }

        if (DEBUG) {
            Log.e(TAG, log);
        }
    }

    public static void printLog(String log, boolean debug) {
        if (log == null) {
            return;
        }

        if (debug) {
            printLog(log);
        }
    }
}
