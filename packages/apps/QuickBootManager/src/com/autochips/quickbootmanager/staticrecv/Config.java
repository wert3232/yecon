package com.autochips.quickbootmanager;

import android.content.Context;
import android.content.res.Resources;

import java.util.Map;

public class Config {
    public static Map<String, String> WhiteList = null;
    public static Map<String, String> YellowList = null;
    public static final boolean IS_STATIC_BOOT = true;

    public static void initConfig(Context context) {
        if (WhiteList != null && YellowList != null) return;
        WhiteList = Utils.getWhiteList(context);
        YellowList = Utils.getYellowList(context);
    }
}
