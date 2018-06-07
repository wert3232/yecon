package com.yecon.videoplayer2;

import android.os.SystemProperties;
import android.util.Log;

public class RebootStatus {
	private final static String TAG = "RebootStatus";
	public enum SOURCE {
		 SOURCE_MUSIC, 
		 SOURCE_VIDEO, 
		 SOURCE_PICTURE, 
		 SOURCE_DVD, 
		 SOURCE_BT, 
		 SOURCE_RADIO, 
		 SOURCE_AUX, 
		 SOURCE_TV}
		 public static boolean isReboot(SOURCE source){
			 boolean ret = false;
			 int rebootFlag = SystemProperties.getInt("persist.sys.app_reboot_flag", 0);
			 Log.i(TAG, "rebootFlag: 0x" + Integer.toHexString(rebootFlag));
			 if(source.ordinal() >=SOURCE.SOURCE_MUSIC.ordinal() && source.ordinal() <= SOURCE.SOURCE_TV.ordinal()){
				 if((rebootFlag & (1<<source.ordinal()))!=0){
					 ret = true;
					 rebootFlag &= ~(1<<source.ordinal());
					 Log.i(TAG, "clear:" + source.ordinal()  + " rebootFlag: 0x" + Integer.toHexString(rebootFlag));
					 SystemProperties.set("persist.sys.app_reboot_flag",String.valueOf(rebootFlag));
				 }
			 }
			return ret;
		 }
		 
}
