
package com.yecon.backcar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.mcu.McuExternalConstant.*;

public class BootCompletedReceiver extends BroadcastReceiver {
    public static final String TAG = "BackCar BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "BackCar BootCompletedReceiver - action: " + action);
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            if (null != context) {
                Intent startIntent = new Intent("android.intent.action.BACKCAR_SERVICE");
                context.startService(startIntent);

                Intent bootLogoIntent = new Intent(context, BootLogoService.class);
                context.startService(bootLogoIntent);
            }
        }
    }
}
