package com.can.tool;

import android.os.SystemProperties;
import android.util.Log;

/**
 * ClassName:CanInfo
 * 
 * @function:can数据Debug输出
 * @author Kim
 * @Date:  2016-5-28 上午10:44:09
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanInfo {
	
	public static String CAN_RX = "CAN_RX";
	public static String CAN_TX = "CAN_TX";
	public static boolean isDebug = SystemProperties.get("persist.sys.isDebug", "0").equals("1") ? true : false;

    public static void Rx(byte[] byPacket) {
        if (isDebug) {
            Log.i(CAN_RX, ":"+DataConvert.Bytes2Str(byPacket));
        }
    }
    
    public static void Tx(byte[] byPacket) {
        if (isDebug) {
            Log.i(CAN_TX, ":"+DataConvert.Bytes2Str(byPacket));
        }
    }
	
    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg + "");
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg + "");
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg + "");
        }
    }
    
    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg + "");
        }
    }
}
