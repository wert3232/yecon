package com.yecon.sourcemanager;

import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_NEXT;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_PLAY_PAUSE;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_PREVIOUS;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_PLAY;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_PAUSE;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_STOP;
import static android.mcu.McuExternalConstant.PROPERTY_KEY_CBMSOURCE;

import java.util.List;


import com.yecon.common.SourceManager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonIntentReceiver extends BroadcastReceiver {
    private static final int KEYCODE_DELAY_300 = 300;

    private static long mLastClickTime = 0;

	private String TAG = "MediaButtonIntentReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        // DebugUtil.printLog("action: " + intentAction);

        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event == null) {
                return;
            }

            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime();

            if (action == KeyEvent.ACTION_DOWN) {
                if (eventtime - mLastClickTime > KEYCODE_DELAY_300) {
                    mLastClickTime = eventtime;
                    Intent i = null;
                    switch (keycode) {
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            i = new Intent(MCU_ACTION_MEDIA_NEXT);
                            break;

                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            i = new Intent(MCU_ACTION_MEDIA_PREVIOUS);
                            break;

                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            i = new Intent(MCU_ACTION_MEDIA_PLAY_PAUSE);
                            break;
                           
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            i = new Intent(MCU_ACTION_MEDIA_PLAY);
                            break;
                            
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            i = new Intent(MCU_ACTION_MEDIA_PAUSE);
                            break;
							
						case KeyEvent.KEYCODE_MEDIA_STOP:
                            i = new Intent(MCU_ACTION_MEDIA_STOP);
                            break;
                    }
                    if (i != null) {
                        //int source = Integer.parseInt(SystemProperties
                        //        .get(PROPERTY_KEY_CBMSOURCE, "0"));
                        //i.putExtra("cbm_source", source);
                    	int srcNo = SystemProperties.getInt(SourceManager.PERSIST_LAST_SOURCE,  -1);
                		if(srcNo >=0 && srcNo< SourceScheduler.sourcePackets.length && SourceScheduler.sourcePackets[srcNo].length()>0){
                			i.setPackage(SourceScheduler.sourcePackets[srcNo]); 
                		}
                		Log.i(TAG , "MediaButtonIntentReceiver: "+keycode);
                		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        				List<RunningTaskInfo> tasks =  mActivityManager.getRunningTasks(10);
        				String pn = "";
        				for(RunningTaskInfo t : tasks){
        					String packageName = t.topActivity.getPackageName();
        					if("com.yecon.music".equals(packageName)){
        						pn = packageName;
        						break;
        					}else if("com.yecon.video".equals(packageName)){
        						pn = packageName;
        						break;
        					}else if("com.yecon.fmradio".equals(packageName)){
        						pn = packageName;
        						break;
        					}else if("com.autochips.bluetooth".equals(packageName)){
        						pn = packageName;
        						break;
        					}else if("com.baidu.carlifevehicle".equals(packageName)){
        						pn = packageName;
        						break;
        					}else if("com.yecon.imagebrowser".equals(packageName)){
        						pn = packageName;
        						break;
        					}
        				}
        				Log.i(TAG , "packageName: "+ pn);
                		if("com.baidu.carlifevehicle".equals(pn)){
                			
                		}else{
                			context.sendBroadcastAsUser(i, UserHandle.ALL);
                		}
                		
                    }
                }
            }
        }
    }

}