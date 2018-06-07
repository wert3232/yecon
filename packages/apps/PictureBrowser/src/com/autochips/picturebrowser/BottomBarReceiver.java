package com.autochips.picturebrowser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import static android.mcu.McuExternalConstant.*;

public class BottomBarReceiver extends BroadcastReceiver {

	public static String strNaviTitle = "";
	public static Handler uiHandler = null;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		/*
		 final String action = arg1.getAction();
         if (ACTION_UPDATE_NAVI_ICON_TEXT.equals(action)) {
             strNaviTitle = arg1.getStringExtra(INTENT_EXTRA_NAVI_ICON_TEXT);
             if (BottomBarReceiver.strNaviTitle.equalsIgnoreCase(String
                     .valueOf(MCU_ACTION_MEDIA_PLAY_PAUSE)))
                 return;
             if(uiHandler!=null){
            	 uiHandler.sendEmptyMessage(0);
             }
         }
		 */
	}

}
