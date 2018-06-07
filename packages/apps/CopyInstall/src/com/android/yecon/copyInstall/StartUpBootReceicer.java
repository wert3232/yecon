package com.android.yecon.copyInstall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class StartUpBootReceicer extends BroadcastReceiver
{
    
    private static final String TAG = "StartUpBootReceicer";

	//add
	private final String INTERNAL_SD = "/storage/sdcard0";
    private final String EXTERNAL_SD = "/storage/sdcard1";
    
    public static final String SEND_TYPE_BOOT_KEY = "SEND_TYPE_BOOT_KEY";
    public static final String SEND_TYPE_MOUNT_KEY = "SEND_TYPE_MOUNT_KEY";
    public static final int SEND_TYPE_BOOT = 0;
    public static final int SEND_TYPE_MOUNT = 1;

    private SharedPreferences prefs;
    private String APK_INTALL = "apk_install";
    private String INSTALL_YES = "install_yes";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG , "intent action " + intent.getAction());
        // TODO Auto-generated method stub
        prefs = context.getSharedPreferences(APK_INTALL, Context.MODE_PRIVATE);
        if (prefs.getInt(INSTALL_YES, -1) != 100)
        {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            {
                Intent sendIntent = new Intent(context, StorageMounted.class);
                sendIntent.putExtra(SEND_TYPE_BOOT_KEY, SEND_TYPE_BOOT);
                context.startService(sendIntent);
            }
            else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED))
            {
                Log.d(TAG , "mount path = " + intent.getDataString());
                if (intent.getDataString().endsWith(INTERNAL_SD))
                {
                    Intent sendIntent = new Intent(context, StorageMounted.class);
                    sendIntent.putExtra(SEND_TYPE_MOUNT_KEY, SEND_TYPE_MOUNT);
                    context.startService(sendIntent);
                }
            }
        }
    }
}
