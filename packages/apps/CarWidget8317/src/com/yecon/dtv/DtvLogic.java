package com.yecon.dtv;

import com.yecon.carwidget8317.R;
import com.yecon.carwidget8317.WidgetsBase;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

public class DtvLogic extends WidgetsBase {
	private static final String TAG = "dtvLogic";
	
	static {
	}
	
	public DtvLogic() {
	}
	
	@Override
	public void init(final Context context) {
		// TODO Auto-generated method stub
		Log.i(TAG, "widget--->dtvlogic init");
		
		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.dtv_widget_layout);
		
		AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, com.yecon.carwidget8317.CarWidgetProvider.class), remoteViews);                           
	}
	
	@Override
	public void destroy(final Context context) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void update(final Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG, "##widget_dtv update");
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.dtv_widget_layout);
        AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, com.yecon.carwidget8317.CarWidgetProvider.class), remoteViews);
	}
	
	@Override
	public void notify(final Context context, final String s) {
		super.notify(context, s);
	}
}
