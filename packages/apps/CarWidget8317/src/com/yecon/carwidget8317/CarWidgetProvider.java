package com.yecon.carwidget8317;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class CarWidgetProvider extends AppWidgetProvider{
	private final String TAG="CarWidgetProvider";
	private static CarWidgetProvider sInstance;
	private int mWidgetsEnable;

	public int getWidgetsEnable() {
		return this.mWidgetsEnable;
	}

	public CarWidgetProvider() {
		this.mWidgetsEnable = 0;
	}
	
	 static CarWidgetProvider getInstance() {
	        synchronized (CarWidgetProvider.class) {
	            if (CarWidgetProvider.sInstance == null) {
	            	CarWidgetProvider.sInstance = new CarWidgetProvider();
	                Log.d("WidgetProvider", "widget--->getInstance");
	            }
	            return CarWidgetProvider.sInstance;
	        }
	    }
	 
	 void startCarwidgetService(Context context) {
		 if (!isServiceRunning(context, "com.stt.carwidgets.CarWidgetsService")) {
	            Log.d(TAG, "widget--->onReceive, start service");
	            context.startService(new Intent(context, CarWidgetService.class));
	        }
	 }
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final String action = intent.getAction();
        Log.d(TAG, "widget--->onReceive, action=" + action + ", id=" + intent.getIntExtra("id", -1));
        
        startCarwidgetService(context);
        
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->onUpdate");
		
		 if (CarWidgetService.getInstance() != null) {
             CarWidgetService.getInstance().UpdateWidget();
		 }
             
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId, Bundle newOptions) {
		// TODO Auto-generated method stub
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->onDelete");
		
		super.onDeleted(context, appWidgetIds);
	}

	public boolean isServiceRunning(Context mContext, String serviceName) {  
        boolean isWork = false;  
        ActivityManager myAM = (ActivityManager) mContext  
                .getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
        if (myList.size() <= 0) {  
            return false;  
        }  
        for (int i = 0; i < myList.size(); i++) {  
            String mName = myList.get(i).service.getClassName().toString();  
            if (mName.equals(serviceName)) {  
                isWork = true;  
                break;  
            }  
        }  
        return isWork;  
    } 
	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->onEnabled");
		
		mWidgetsEnable = 1;
		startCarwidgetService(context);
        if (CarWidgetService.getInstance() != null) {
            CarWidgetService.getInstance().UpdateWidget();
        }
        
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->onDisabled");
		
		mWidgetsEnable = 0;
		context.stopService(new Intent(context, CarWidgetService.class));
		super.onDisabled(context);
	}

}
