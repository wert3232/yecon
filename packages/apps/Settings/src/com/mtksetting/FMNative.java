package com.mtksetting;

import android.util.Log;

public class FMNative {
private final static String TAG = "FMsetting native";
	
	static {
		Log.i(TAG, "System.load(\"libmtk_fmtx.so\");");
		System.loadLibrary("mtk_fmtx");
		Log.i(TAG,"load ok");
	}

	public static native void setFMTransEnabled();
	public static native void setFMTransDisabled();
	public static native void setFMTransFreq(int freq);
	public static native int getFMTransFreq();
	public static native int getFMTransState(); 
}
