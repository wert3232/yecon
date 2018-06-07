package com.yecon.videoplayer2;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class ForeBackGroundHandler {
	private static final String ACTIVITY_CHANGE = "android.activity.action.STATE_CHANGED";
	private static final String ACTIVITY_FG     = "foreground";
    private static final String ACTIVITY_BG     = "background";
	public interface ForeBackGroundLisenter{
		void onForeGround();
		void onBackGround();
	};
	private ForeBackGroundLisenter foreBackGroundLisenter;
	private String pkgName="" , activityName="";
	public void setForeBackGroundLisenter(String pkg, ForeBackGroundLisenter foreBackGroundLisenter){
		this.foreBackGroundLisenter = foreBackGroundLisenter;
		pkgName = pkg;
		//activityName = activity;
	}
		
	public ForeBackGroundHandler(Context context) {
		super();
		this.context = context;
	}
	
	private boolean isApplicationForeground() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(2);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(pkgName)) {
                return true;
            }
            else{
            	if(tasks.size()>1){
            		ComponentName secondActivity = tasks.get(1).topActivity;
            		if (secondActivity.getPackageName().equals(pkgName)) {
	            		if(topActivity.getClassName().equals("com.yecon.backcar.BackCarActivity")
	                			|| topActivity.getClassName().equals("com.autochips.bluetooth.PhoneCallActivity")){
	                		return true;
	                	}
            		}
            	}            	
            }
        }
        return false;
    }
	
	private Context context;
	private BroadcastReceiver receiver;
	public void init(){
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(ACTIVITY_CHANGE);
        receiver = new Receiver();
        context.registerReceiver(receiver, filter);
	}
	
	public void release(){
		context.unregisterReceiver(receiver);
	}

	 class Receiver extends BroadcastReceiver {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            final String action = intent.getAction();

	            if (ACTIVITY_CHANGE.equals(action)) {
	                Bundle bundle = intent.getExtras();
	                String szState = bundle.getString("state");
	                //String szClass = bundle.getString("class");
	                String szPkg = bundle.getString("package");
	                //int pid = bundle.getInt("pid");
	                	
	                if (szState.equals(ACTIVITY_FG)) {
	                    // foreground
	                    if(pkgName.equals(szPkg)){
                    		if(foreBackGroundLisenter!=null){
	                    		foreBackGroundLisenter.onForeGround();
	                    	}             		
	                    }
	                    else if(szPkg.equals("com.android.launcher")){
	                    	 if(foreBackGroundLisenter!=null){
		                    	foreBackGroundLisenter.onBackGround();
		                    }
	                    }
	                } 
//	                else  if(szState.equals(ACTIVITY_BG)){
//	                    // background
//	                	 if(pkgName.equals(szPkg)){
//	                		 if(!isApplicationForeground()){
//	                			 if(foreBackGroundLisenter!=null){
//			                    	foreBackGroundLisenter.onBackGround();
//			                    }
//	                		 }
//	                	}
//	                }
	            } 
	        }
	    }
}
