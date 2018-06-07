package com.yecon.video;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ActivityMonitor {

	public interface ActivityMonitorLisenter{
		void onForeGround();
		void onBackGround();
	};
	
	private static final String ACTIVITY_CHANGE = "android.activity.action.STATE_CHANGED";
	
	private static final String HOVER_PACKAGE[] = new String[] {
		YeconConstants.CANBUS_PACKAGE_NAME,
		YeconConstants.CAR_SETTING_PACKAGE_NAME
//		"com.yecon.backcar.BackCarActivity",
//		"com.autochips.bluetooth.PhoneCallActivity"
	};
	
	private static final String TAG = "ActivityMonitor";
	
	private String mMonitorPacketName="";
	
	private boolean mbForeground = true;
	
	private ActivityMonitorLisenter mActivityMonitorLisenter;
	public void setActivityMonitorLisenter(String PacketName, ActivityMonitorLisenter ActivityMonitorLisenter){
		this.mActivityMonitorLisenter = ActivityMonitorLisenter;
		mMonitorPacketName = PacketName;
	}
		
	public ActivityMonitor(Context context) {
		super();
		this.context = context;
	}
	
	private static final int MAX_TASK = 5;
	
	public boolean isForeground() {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RecentTaskInfo> tasksAll = am.getRecentTasks(MAX_TASK, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
		int iMonitorPacketLevel = -1;
		if (tasksAll != null) {
			for (RecentTaskInfo recentTaskInfo : tasksAll) {
				if (recentTaskInfo.id > 0 && recentTaskInfo.baseIntent != null) {
					String baseintent = recentTaskInfo.baseIntent.toString();
					boolean bHoverPacket = false;
					for (String hoverPacket : HOVER_PACKAGE) {
						if (baseintent.contains(hoverPacket)) {
							bHoverPacket = true;
							break;
						}
					}
					if (!bHoverPacket) {
						iMonitorPacketLevel++;
						if (baseintent.contains(mMonitorPacketName)) {
							break;
						}
					}
					if (baseintent.contains("com.android.launcher")) {
						return false;
					}
				}
			}
			Log.e("isForeground", "iMonitorPacketLevel:" + iMonitorPacketLevel);
		}
		return (iMonitorPacketLevel == 0);
	}
	
	private Context context;
	private BroadcastReceiver receiver;
	public void init(){
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(ACTIVITY_CHANGE);
        receiver = new Receiver();
        context.registerReceiver(receiver, filter);
	}
	
	public void deinit(){
		context.unregisterReceiver(receiver);
	}

	 class Receiver extends BroadcastReceiver {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            final String action = intent.getAction();
	            synchronized (TAG) {
	            	if (ACTIVITY_CHANGE.equals(action) && mActivityMonitorLisenter != null) {
		                if (isForeground()) {
		                	if (!mbForeground) {
		                		mbForeground = true;
								mActivityMonitorLisenter.onForeGround();
							}
		                } else {
		                	if (mbForeground) {
		                		mbForeground = false;
								mActivityMonitorLisenter.onBackGround();
							}
		                }
		            }
				}
	        }
	    }
}
