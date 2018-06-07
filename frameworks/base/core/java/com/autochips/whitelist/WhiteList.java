package com.autochips.whitelist;

import android.util.Log;
public class WhiteList {
    
    public static final String TAG = "WhiteList";

    public static final int APP_NAVIGATOR = 1;
    public static final int APP_UNDEFINE  = 2;
    
    static {
        System.loadLibrary("whitelist_jni");
    }

  public native static int getPackageType(String pkgName);			//Jade add;
  public native static int getApplicationType(int pid);
  public native static boolean addToWhiteList4GIS(String name);
  public native static boolean removeFromWhiteList4GIS(String name);
}

