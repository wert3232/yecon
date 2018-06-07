package com.autochips.quickbootmanager.filters;

import java.util.HashSet;

import android.content.Context;

public class FilterHub {
    private static HashSet<Filter> mFilters = new HashSet<Filter>();
    static {
        addFilter(new InputMethodFilter());
        addFilter(new WallpaperSetterFilter());
    }
    public static void addFilter(Filter filter) {
        mFilters.add(filter);
    }
    public static void prepare(Context ctx) {
        for (Filter filter : mFilters) {
            filter.prepare(ctx);
        }
    }
    public static boolean allowAlive(Context ctx, String pack) {
        for (Filter filter : mFilters) {
            boolean res = filter.allowAlive(ctx, pack);
            if (res) return true;
        }
        return false;
    }
    public static void done(Context ctx) {
        for (Filter filter : mFilters) {
            filter.done(ctx);
        }
    }
}
