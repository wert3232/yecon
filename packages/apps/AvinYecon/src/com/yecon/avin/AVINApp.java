package com.yecon.avin;

import android.app.Application;

import android.util.Log;


public class AVINApp extends Application {

    public final static String TAG = "AVINApp";

    private static AVINApp gInst = null;

    public static AVINApp GetInstance() {
        //return (gInst);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "AVINApp onCreate");
        gInst = this;
    }
}
