package com.autochips.weather;

import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    public static int getValue(Context context, String node, String key,
            int defaultValue) {
        return context.getSharedPreferences(node, Context.MODE_PRIVATE).getInt(
                key, defaultValue);
    }

    public static String getValue(Context context, String node, String key,
            String defaultValue) {
        return context.getSharedPreferences(node, Context.MODE_PRIVATE)
                .getString(key, defaultValue);
    }

    public static boolean getValue(Context context, String node, String key,
            boolean defaultValue) {
        return context.getSharedPreferences(node, Context.MODE_PRIVATE)
                .getBoolean(key, defaultValue);
    }

    public static void saveValue(Context context, String node, String key,
            String value) {
        SharedPreferences.Editor sp = context.getSharedPreferences(node,
                Context.MODE_PRIVATE).edit();
        sp.putString(key, value);
        sp.commit();
    }

    public static void saveValue(Context context, String node, String key,
            boolean value) {
        SharedPreferences.Editor sp = context.getSharedPreferences(node,
                Context.MODE_PRIVATE).edit();
        sp.putBoolean(key, value);
        sp.commit();
    }

    public static void saveValue(Context context, String node, String key,
            int value) {
        SharedPreferences.Editor sp = context.getSharedPreferences(node,
                Context.MODE_PRIVATE).edit();
        sp.putInt(key, value);
        sp.commit();
    }

    public static void saveValueForMultithread(Context context, String node,
            String key, boolean value) {
        if (context != null) {
            SharedPreferences.Editor sp = context.getSharedPreferences(node,
                    Context.MODE_MULTI_PROCESS).edit();
            sp.putBoolean(key, value);
            sp.commit();
        }
    }

    public static boolean getValueForMultithread(Context context, String node,
            String key, boolean defaultValue) {
        if (context != null) {
            SharedPreferences prference = context.getSharedPreferences(node,
                    Context.MODE_MULTI_PROCESS);
            return prference.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    public synchronized static boolean isItemViewType(int param,
            TreeSet<Integer> treeSet) {
        if (treeSet.contains(param)) {
            return true;
        } else {
            return false;
        }
    }
}
