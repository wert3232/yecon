
package com.autochips.quickbootmanager;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

public class Config {
    public static Map<String, String> WhiteList = null;
    public static Map<String, String> YellowList = null;
    public static ArrayList<String> GisWhiteList = new ArrayList<String>();
    public static final boolean IS_STATIC_BOOT = false;

    public static void initConfig(Context context) {
        if (WhiteList != null && YellowList != null && GisWhiteList != null)
            return;
        WhiteList = Utils.getWhiteList(context);
        YellowList = Utils.getYellowList(context);

        GisWhiteListParse.gisWhiteListParse();
        GisWhiteList = GisWhiteListParse.getGisWhiteList();
    }
}
