package com.yecon.music.util;

import android.util.Log;

public class L {
	public static String TAG="YG_MusicPlayer"; 
	public static boolean isDebug = true;
	public static boolean DEBUG_KEY=false;
	public static boolean DEBUG=false;
	public static boolean DEBUG_LAYOUT=false;
	public static boolean DEBUG_MOTION=false;
	public static boolean DEBUG_DRAG=false;
	public static boolean DEBUG_UNREAD=false;
	public static boolean DEBUG_LOADER=false;
	public static boolean DEBUG_DRAW=false;
	public static boolean DEBUG_SURFACEWIDGET=false;
	public static boolean DEBUG_PERFORMANCE=false;
	
	
	

	public static void i(String msg) {
		if (isDebug)
			Log.i(TAG, msg);
	}

	public static void d(String msg) {
		if (isDebug)
			Log.d(TAG, msg);
	}

	public static void e(String msg) {
		if (isDebug)
			Log.e(TAG, msg);
	}

	public static void v(String msg) {
		if (isDebug)
			Log.v(TAG, msg);
	}

	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.e(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}
	public static void w(String tag, String msg) {
		if (isDebug)
			Log.w(tag, msg);
	}
	
	
	 /**
     * The method prints the log, level warning.
     *
     * @param tag the tag of the class.
     * @param msg the message to print.
     * @param t an exception to L.
     */
    public static void w(String tag, String msg, Throwable t) {
        //XL.w(MODULE_NAME, tag + ", " + msg, t);
    }
    
    /**
     * The method prints the log, level debug.
     *
     * @param tag the tag of the class.
     * @param msg the message to print.
     * @param t an exception to L.
     */
    public static void i(String tag, String msg, Throwable t) {
       // XL.i(MODULE_NAME, tag + ", " + msg, t);
    }
    
    /**
     * The method prints the log, level debug.
     *
     * @param tag the tag of the class.
     * @param msg the message to print.
     * @param t An exception to L.
     */
    public static void d(String tag, String msg, Throwable t) {
        //XL.d(MODULE_NAME, tag + ", " + msg, t);
    }
    
    /**
     * The method prints the log, level debug.
     *
     * @param tag the tag of the class.
     * @param msg the message to print.
     * @param t An exception to L.
     */
    public static void v(String tag, String msg, Throwable t) {
       // XL.v(MODULE_NAME, tag + ", " + msg, t);
    }
    /**
     * The method prints the log, level error.
     *
     * @param tag the tag of the class.
     * @param msg the message to print.
     * @param t an exception to L.
     */
    public static void e(String tag, String msg, Throwable t) {
       // XL.e(MODULE_NAME, tag + ", " + msg, t);
    }
}
