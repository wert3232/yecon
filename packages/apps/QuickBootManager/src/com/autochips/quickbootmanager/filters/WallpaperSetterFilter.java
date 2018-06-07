package com.autochips.quickbootmanager.filters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.service.wallpaper.WallpaperService;

public class WallpaperSetterFilter implements Filter {
    private List<ResolveInfo> mWallpaperSetter = null;
    private List<ResolveInfo> mWallpaperSrv = null;

    @Override
    public boolean allowAlive(Context ctx, String pack) {
        for (ResolveInfo info : mWallpaperSetter) {
            if (info.activityInfo.packageName.equals(pack)) {
                return true;
            }
        }
        for (ResolveInfo info : mWallpaperSrv) {
            if (info.serviceInfo.packageName.equals(pack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void prepare(Context ctx) {
        if (mWallpaperSetter == null || mWallpaperSrv == null) {
            final PackageManager pm = ctx.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
            mWallpaperSetter = pm.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            intent = new Intent(WallpaperService.SERVICE_INTERFACE);
            mWallpaperSrv = pm.queryIntentServices(intent,
                    PackageManager.GET_META_DATA);
        }
    }

    @Override
    public void done(Context ctx) {
        mWallpaperSetter = null;
        mWallpaperSrv = null;
    }

}
