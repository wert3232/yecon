/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.launcher.R;

import static android.constant.YeconConstants.*;

/**
 * Cache of application icons. Icons can be made from any thread.
 */
public class IconCache {
    @SuppressWarnings("unused")
    private static final String TAG = "Launcher.IconCache";
    
    private static HashMap<String, String> APP_ICON_WHITE_LIST ; 
    
    static {
        APP_ICON_WHITE_LIST = new HashMap<String, String>();
        APP_ICON_WHITE_LIST.put(CAR_SETTING_START_ACTIVITY, CAR_SETTING_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(CAR_SETTING_NAVIGATION_ACTIVITY, CAR_SETTING_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(DVD_START_ACTIVITY, DVD_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(VIDEO_START_ACTIVITY, VIDEO_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(MUSIC_START_ACTIVITY, MUSIC_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(BLUETOOTH_START_ACTIVITY, BLUETOOTH_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(FMRADIO_START_ACTIVITY, FMRADIO_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(AVIN_START_ACTIVITY, AVIN_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(AVIN_EXT_START_ACTIVITY, AVIN_EXT_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(TV_START_ACTIVITY, TV_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(ATV_START_ACTIVITY, ATV_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(DTV_START_ACTIVITY, DTV_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(IPOD_START_ACTIVITY, IPOD_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(BROWSER_STARTUP_ACTIVITY, BROWSER_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(CALENDAR_STARTUP_ACTIVITY, CALENDAR_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(DESKCLOCK_STARTUP_ACTIVITY, DESKCLOCK_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(MIRACAST_STARTUP_ACTIVITY, MIRACAST_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(DVR_START_ACTIVITY, DVR_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(SETTINGS_STARTUP_ACTIVITY, SETTINGS_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(CALCULATOR_STARTUP_ACTIVITY, CALCULATOR_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(FILEBROWSER_START_ACTIVITY, FILEBROWSER_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(QUICKSEARCH_STARTUP_ACTIVITY, QUICKSEARCH_PACKAGE_NAME); 
        APP_ICON_WHITE_LIST.put(DOWNLOADS_STARTUP_ACTIVITY, DOWNLOADS_PACKAGE_NAME); 
        APP_ICON_WHITE_LIST.put(PICBROWSER_START_ACTIVITY, PICBROWSER_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(TPMS_START_ACTIVITY, TPMS_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(CANBUS_START_ACTIVITY, CANBUS_PACKAGE_NAME);
        APP_ICON_WHITE_LIST.put(OPERATEINTRO_START_ACTIVITY,OPERATEINTRO_PACKAGE_NAME);
        /*APP_ICON_WHITE_LIST.put("com.baidu.che.codriver.ui.MainActivity", "com.baidu.che.codriver");*/
    }
    
    static String str_class[] = {
       CAR_SETTING_START_ACTIVITY, 
       CAR_SETTING_NAVIGATION_ACTIVITY, 
       DVD_START_ACTIVITY,
       VIDEO_START_ACTIVITY,
       MUSIC_START_ACTIVITY,
       BLUETOOTH_START_ACTIVITY,
       FMRADIO_START_ACTIVITY,
       AVIN_START_ACTIVITY,
       AVIN_EXT_START_ACTIVITY,
       TV_START_ACTIVITY,
       ATV_START_ACTIVITY,
       DTV_START_ACTIVITY,
       IPOD_START_ACTIVITY,
       BROWSER_STARTUP_ACTIVITY,
       CALENDAR_STARTUP_ACTIVITY,
       DESKCLOCK_STARTUP_ACTIVITY,
       MIRACAST_STARTUP_ACTIVITY,
       DVR_START_ACTIVITY,
       SETTINGS_STARTUP_ACTIVITY,
       CALCULATOR_STARTUP_ACTIVITY,
       FILEBROWSER_START_ACTIVITY,
       QUICKSEARCH_STARTUP_ACTIVITY,
       DOWNLOADS_STARTUP_ACTIVITY,
       PICBROWSER_START_ACTIVITY,
       TPMS_START_ACTIVITY,
       CANBUS_START_ACTIVITY,
       OPERATEINTRO_START_ACTIVITY,
    };
    
    static int ID_icon[] = {
    	R.drawable.ic_launcher_carsetting,
    	R.drawable.ic_launcher_navigation,
    	R.drawable.ic_launcher_dvd,
    	R.drawable.ic_launcher_video,
    	R.drawable.ic_launcher_music,
    	R.drawable.ic_launcher_bt,
    	R.drawable.ic_launcher_radio,
    	R.drawable.ic_launcher_aux,
    	R.drawable.ic_launcher_aux,
    	R.drawable.ic_launcher_tv,
    	R.drawable.ic_launcher_tv,
    	R.drawable.ic_launcher_tv,
    	R.drawable.ic_launcher_ipod,
    	R.drawable.ic_launcher_ie,
    	R.drawable.ic_launcher_calendar,
    	R.drawable.ic_launcher_clock,
    	R.drawable.ic_launcher_gsm,
    	R.drawable.ic_launcher_dvr,
    	R.drawable.ic_launcher_setting,
    	R.drawable.ic_launcher_calculator,
    	R.drawable.ic_launcher_filebrowser,
    	R.drawable.ic_launcher_search,
    	R.drawable.ic_launcher_down,
    	R.drawable.ic_launcher_photo,
    	R.drawable.ic_launcher_tpms,
    	R.drawable.ic_launcher_car,
    	R.drawable.ic_launcher_operateintro
    };

    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

    private static class CacheEntry {
        public Bitmap icon;
        public String title;
    }

    private final Bitmap mDefaultIcon;
    private final LauncherApplication mContext;
    private final PackageManager mPackageManager;
    private final HashMap<ComponentName, CacheEntry> mCache = new HashMap<ComponentName, CacheEntry>(
            INITIAL_ICON_CACHE_CAPACITY);
    private int mIconDpi;

    public IconCache(LauncherApplication context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        mContext = context;
        mPackageManager = context.getPackageManager();
        mIconDpi = activityManager.getLauncherLargeIconDensity();

        // need to set mIconDpi before getting default icon
        mDefaultIcon = makeDefaultIcon();
    }

    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(),
                android.R.mipmap.sym_def_app_icon);
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }

    public Drawable getFullResIcon(ActivityInfo info) {

        Resources resources;
        try {
            resources = mPackageManager
                    .getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    private Bitmap makeDefaultIcon() {
        Drawable d = getFullResDefaultActivityIcon();
        Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
                Math.max(d.getIntrinsicHeight(), 1), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap(null);
        return b;
    }

    /**
     * Remove any records for the supplied ComponentName.
     */
    public void remove(ComponentName componentName) {
        synchronized (mCache) {
            mCache.remove(componentName);
        }
    }

    /**
     * Empty out the cache.
     */
    public void flush() {
        synchronized (mCache) {
            mCache.clear();
        }
    }

    /**
     * Fill in "application" with the icon and label for "info."
     */
    public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            CacheEntry entry = cacheLocked(application.componentName, info,
                    labelCache);

            application.title = entry.title;
            application.iconBitmap = entry.icon;
        }
    }

    public Bitmap getIcon(Intent intent) {
        synchronized (mCache) {
            final ResolveInfo resolveInfo = mPackageManager.resolveActivity(
                    intent, 0);
            ComponentName component = intent.getComponent();

            if (resolveInfo == null || component == null) {
                return mDefaultIcon;
            }

            CacheEntry entry = cacheLocked(component, resolveInfo, null);
            return entry.icon;
        }
    }

    public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            if (resolveInfo == null || component == null) {
                return null;
            }

            CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
            return entry.icon;
        }
    }

    public boolean isDefaultIcon(Bitmap icon) {
        return mDefaultIcon == icon;
    }

    private CacheEntry cacheLocked(ComponentName componentName,
            ResolveInfo info, HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = mCache.get(componentName);
        if (entry == null) {
            entry = new CacheEntry();

            mCache.put(componentName, entry);

            ComponentName key = LauncherModel
                    .getComponentNameFromResolveInfo(info);
            if (labelCache != null && labelCache.containsKey(key)) {
                entry.title = labelCache.get(key).toString();
            } else {
                entry.title = info.loadLabel(mPackageManager).toString();
                if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            }
            if (entry.title == null) {
                entry.title = info.activityInfo.name;
            }
            /*try {
                int appFlags = mPackageManager.getApplicationInfo(
                        componentName.getPackageName(), 0).flags;
                if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
                    entry.icon = Utilities.create3rdIconBitmap(
                            getFullResIcon(info), mContext);
                    return entry;
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }*/
            
            //by lzy modify
            if (!APP_ICON_WHITE_LIST.containsKey(componentName.getClassName())) {
                entry.icon = Utilities.create3rdIconBitmap(
                        getFullResIcon(info), mContext);
                return entry;
            }
            else {      
            	Drawable drawable = getFullResIcon(info);
            	for(int i=0;i<str_class.length;i++)
            	{
            		if(componentName.getClassName().equalsIgnoreCase(str_class[i]))
            		{
            			drawable = mContext.getResources().getDrawable(ID_icon[i]);
            			break;
            		}
            	}
            	entry.icon = Utilities.createIconBitmap(drawable,mContext);
            }
        }
        return entry;
    }

    public HashMap<ComponentName, Bitmap> getAllIcons() {
        synchronized (mCache) {
            HashMap<ComponentName, Bitmap> set = new HashMap<ComponentName, Bitmap>();
            for (ComponentName cn : mCache.keySet()) {
                final CacheEntry e = mCache.get(cn);
                set.put(cn, e.icon);
            }
            return set;
        }
    }
}
