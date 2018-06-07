package com.autochips.picturebrowser.status;

import java.util.Vector;

public class StatusHub {
    private static Vector<PreludeStatus> mStatusHub = new Vector<PreludeStatus>();

    public static void addStatusOwner(PreludeStatus status) {
        mStatusHub.add(status);
    }

    public static void clearStatusOwners() {
        mStatusHub.clear();
    }

    public static void onLoad() {
        for (PreludeStatus status : mStatusHub) {
            status.onLoad();
        }
    }

    public static void loadDataSource() {
        if (mStatusHub.size() <= 0)
            return;
        (mStatusHub.elementAt(0)).onLoad();
    }

    public static void loadPlayStatus() {
        if (mStatusHub.size() <= 1)
            return;
        ((PlayStatus) mStatusHub.elementAt(1)).onLoad();
    }

    public static void onStore() {
        for (PreludeStatus status : mStatusHub) {
            status.onStore();
        }
    }

    public static void onClear() {
        for (PreludeStatus status : mStatusHub) {
            status.onClear();
        }
    }

    public static void removeLastStatusOwner() {
        if (mStatusHub.size() <= 1)
            return;
        mStatusHub.removeElementAt(mStatusHub.size() - 1);
    }
}
