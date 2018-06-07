/**
 * @Title: YeconCANBusBootReceiver.java
 * @Package com.yecon.canbus
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author Comsys-CB050
 * @date 2016年4月14日 上午11:09:05
 * @version V1.0
 */
package com.yecon.canbus.parse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @ClassName: YeconCANBusBootReceiver
 * @Description: receive boot intent to create CANBUS service
 * @author hzGuo
 * @date 2016年4月14日 上午11:09:05
 *
 */
public class YeconCANBusBootReceiver extends BroadcastReceiver {

	private static final String ACTION_CANBUS_SERVICE = "yecon.intent.action.CANBUS_SERVICE";
	
	/**
	 * Title: onReceive Description:
	 * 
	 * @param context
	 * @param intent
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 *      android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (context == null || intent == null) {
			return;
		} else {
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				Intent intentService = new Intent(ACTION_CANBUS_SERVICE);
				context.startService(intentService);
				Log.e("YeconCANBusBootReceiver", "++startService++");
			}
		}
	}

}
