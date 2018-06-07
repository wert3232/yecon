package com.can.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ClassName:CanBroadCast
 * 
 * @function:收到ACTION_BOOT_COMPLETED后启动Can服务
 * @author Kim
 * @Date:  2016-5-26 上午11:55:37
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanBroadCast extends BroadcastReceiver{

	private boolean mbStart = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if (context == null || intent == null) {
			
			return;
		} else {
			
			if (mbStart == false) {
				mbStart = true;
				
				//开机启动Can服务端
				Intent objIntent = new Intent(CanTxRxStub.ACTION_CAN_SERVICE);
				context.startService(objIntent);
				
				Log.i("CanBroadCast", "++startCanService++");
				
				//开机启动Can用户端	
				Intent objUIIntent = new Intent(CanTxRxStub.ACTION_CAN_UI_SERVICE);
				context.startService(objUIIntent);
				
				Log.i("CanBroadCast", "++startCanUIService++");
			}
		}
	}

}
