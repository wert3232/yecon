//Begin : Added by yongwu.zhou 2012/06/04
package com.android.calibrate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.os.SystemProperties;		//Jade add;

public class BootCompletedReceiver extends BroadcastReceiver{
		final String TAG = "AndroidCalibrate";
		
    @Override
    public void onReceive(Context context, Intent intent){
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            int m_tag = 0;
            Log.d(TAG, "onReceive : ACTION_BOOT_COMPLETED");

			int ctp_flag = SystemProperties.getInt("persist.sys.ctp.flag",0);
			Log.d(TAG,"ctp_flag="+ctp_flag);
			if(ctp_flag > 0)
				{
					Log.d(TAG,"Capacitive Touch Screen DON'T need calibrate...");
					return;
				}
            SharedPreferences sp = context.getSharedPreferences("BOOT_CALIBRATE", 0);
            m_tag = sp.getInt("TAG", 1);
            
            if (m_tag != 2) {
                Intent newIntent = new Intent(context, AndroidCalibrate.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            }
        }
    }
}
    