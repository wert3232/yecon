package com.autochips.quickbootmanager;

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
import java.util.HashMap;

public class Utils {
    private static final String TAG = "QuickBootManager";
    private static final boolean DEBUG = true;
    private static final boolean RDEBUG = true;

    public static final String WL_PREF_FILE = "added_white_list";
    public static final String YL_PREF_FILE = "added_yellow_list";
    public static final String PREF_VALUE_SYS = "sys";
    public static final String PREF_VALUE_3RD = "3RD";

    public static void IF_LOG_OUT(String msg) {
        if (DEBUG) Log.i(TAG, msg);
    }

    public static void IF_RLOG_OUT(String msg) {
        if (RDEBUG) Log.i(TAG, msg);
    }

    public static Map<String, String> getWhiteList(Context ctx) {
        Map<String, String> wl = new HashMap<String, String>();
        updateListFromRes(ctx, wl, R.array.app_whitelist);
        updateListFromSP(ctx, wl, WL_PREF_FILE);
        return wl;
    }

    public static Map<String, String> getYellowList(Context ctx) {
        Map<String, String> yl = new HashMap<String, String>();
        updateListFromRes(ctx, yl, R.array.app_yellowlist);
        updateListFromSP(ctx, yl, YL_PREF_FILE);
        return yl;
    }

    private static void updateListFromRes(
            Context ctx,
            Map<String, String> list,
            int resId) {
        IF_LOG_OUT("update list from res");
        Resources res = ctx.getResources();
        String[] packs = res.getStringArray(resId);
        int size = packs.length;
        for (int i = 0; i < size; i++) {
            IF_LOG_OUT("int list add " + packs[i]);
            list.put(packs[i], PREF_VALUE_SYS);
        }
    }

    private static void updateListFromSP(
            Context ctx,
            Map<String, String> list,
            String pref_file) {
        IF_LOG_OUT("update list from shared preferences");
        SharedPreferences sp =
            ctx.getSharedPreferences(
                pref_file, Context.MODE_WORLD_READABLE);
        if (sp == null) return;
        IF_LOG_OUT("int list shared preferences not null");
        Map<String, String> wlsp = (Map<String, String>)sp.getAll();
        if (wlsp == null) return;
        IF_LOG_OUT("int list value " + wlsp);
        list.putAll(wlsp);
    }

    public static boolean addPackToList(
            Context ctx,
            String pack,
            Map<String, String> list,
            String pref_file) {
        if (list.containsKey(pack)) {
            IF_LOG_OUT("already exist package " + pack + ", not add");
            return false;
        }
        SharedPreferences sp =
            ctx.getSharedPreferences(
                pref_file, Context.MODE_WORLD_WRITEABLE);
        if (sp == null) return false;
        IF_LOG_OUT("add whitelist " + pack + ", shared preferences not null");
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(pack, PREF_VALUE_3RD);
        list.put(pack, PREF_VALUE_3RD);
        edit.commit();
        return true;
    }

    public static boolean removePackFromList(
            Context ctx,
            String pack,
            Map<String, String> list,
            String pref_file) {
        String value = list.get(pack);
        if (value == null || PREF_VALUE_SYS.equals(value)) {
            IF_LOG_OUT("the wanted pack " + pack + " not in whitelist or is a sys pack, so not remove");
            return false;
        }

        SharedPreferences sp =
            ctx.getSharedPreferences(
                pref_file, Context.MODE_WORLD_WRITEABLE);
        if (sp == null) return false;
        IF_LOG_OUT("remove whitelist " + pack + ", shared preferences not null");
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(pack);
        list.remove(pack);
        edit.commit();
        return true;
    }

}
