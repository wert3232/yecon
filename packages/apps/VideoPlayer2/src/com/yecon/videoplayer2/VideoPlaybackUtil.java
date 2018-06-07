
package com.yecon.videoplayer2;

import static com.yecon.videoplayer2.DebugUtil.*;
import static com.yecon.videoplayer2.VideoPlaybackConstant.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.autochips.storage.EnvironmentATC;

public class VideoPlaybackUtil {
    private static final String STORAGE_STATE_MOUNTED = "mounted";

    private static EnvironmentATC mEnv;

    public static boolean hasSDCard(Context context) {
        if (mEnv == null) {
            mEnv = new EnvironmentATC(context);
        }

        if (STORAGE_STATE_MOUNTED.equals(mEnv.getStorageState(INT_SDCARD_PATH))
                || STORAGE_STATE_MOUNTED.equals(mEnv.getStorageState(EXT_SDCARD1_PATH))
                || STORAGE_STATE_MOUNTED.equals(mEnv.getStorageState(EXT_SDCARD2_PATH))) {
            return true;
        }

        return false;
    }

    public static boolean hasUDisk(Context context) {
        if (mEnv == null) {
            mEnv = new EnvironmentATC(context);
        }

        if (STORAGE_STATE_MOUNTED.equals(mEnv.getStorageState(UDISK1_PATH))
                || STORAGE_STATE_MOUNTED.equals(mEnv.getStorageState(UDISK2_PATH))
                || STORAGE_STATE_MOUNTED.equals(mEnv.getStorageState(UDISK3_PATH))
                || STORAGE_STATE_MOUNTED.equals(mEnv.getStorageState(UDISK4_PATH))
                || STORAGE_STATE_MOUNTED.equals(mEnv.getStorageState(UDISK5_PATH))) {
            return true;
        }

        return false;
    }

    public static String convertDurationToTime(String durationMs) {
        // convert ms to (05:03:21)
        String rtnString = null;

        int ms = 0;
        try {
            ms = Integer.parseInt(durationMs);
        } catch (Exception e) {
            printLog("Integer.parseInt durationMs exception");
            ms = 0;
        }

        int sec = ms / 1000;
        int minute = sec / 60;
        int hour = minute / 60;
        int second = sec % 60;
        minute %= 60;

        rtnString = String.format("%02d:%02d:%02d", hour, minute, second);
        return rtnString;
    }
    
}
