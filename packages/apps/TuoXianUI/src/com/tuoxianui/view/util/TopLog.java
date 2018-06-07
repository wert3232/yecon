package com.tuoxianui.view.util;

import android.util.Log;

public class TopLog {
	public static final boolean isDebug = false;
	public static void e(String tag,String msg){
		if(isDebug){
			Log.e(tag, msg);
		}
	}
}
