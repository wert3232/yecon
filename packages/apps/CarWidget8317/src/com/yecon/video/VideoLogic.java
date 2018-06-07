package com.yecon.video;

import com.yecon.carwidget8317.CarWidgetService;
import com.yecon.carwidget8317.ConstDefine;
import com.yecon.carwidget8317.R;
import com.yecon.carwidget8317.SendKey;
import com.yecon.carwidget8317.SendMsg;
import com.yecon.carwidget8317.WidgetsBase;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.mcu.McuExternalConstant;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RemoteViews;

public class VideoLogic extends WidgetsBase {
	private static final String TAG = "videoLogic";

	static {
	}
	
	public VideoLogic() {
	}
	
	/**
	 * @param remoteViews
	 * @param context
	 */
	public void updateVideo(final RemoteViews remoteViews, final Context context) {
		String name = "";
		
		name = CarWidgetService.mSaveData.getMediaSongName();
		if (name == null) {
			name = context.getResources().getString(R.string.unknow_name);
		}else if (name.length() <= 0) {
			name = context.getResources().getString(R.string.unknow_name);
		}
		Log.i(TAG, "widget--->updateVideo, name="+name);
		
	    remoteViews.setTextViewText(R.id.video_name, name);
	}
	
	@Override
	public void init(final Context context) {
		// TODO Auto-generated method stub
		Log.i(TAG, "widget--->videologic init");
		
		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.video_widget_layout);
		
		final Intent preIntent = new Intent().setAction(WidgetsBase.ACTION_PREVIOUS);		
		PendingIntent prePendingIntent = PendingIntent.getBroadcast(context, 0, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.video_prev_btn, prePendingIntent);
		
		final Intent nextIntent = new Intent().setAction(WidgetsBase.ACTION_NEXT);		
		PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.video_next_btn, nextPendingIntent);
		
		final Intent playpauseIntent = new Intent().setAction(WidgetsBase.ACTION_PLAYPAUSE);		
		PendingIntent playpausePendingIntent = PendingIntent.getBroadcast(context, 0, playpauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.video_playpause_btn, playpausePendingIntent);
		
		if (ConstDefine.TOUCH_INTO_APP) {
			final Intent enterIntent = new Intent(Intent.ACTION_VIEW);
			enterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			enterIntent.setComponent(new ComponentName("com.yecon.videoplayer2", "com.yecon.videoplayer2.VideoPlaybackListActivity"));
			PendingIntent enterPendingIntent = PendingIntent.getActivity(context, 0, enterIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.music_layout, enterPendingIntent);
		}
		
		updateVideo(remoteViews, context);
		
		AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, com.yecon.carwidget8317.CarWidgetProvider.class), remoteViews);                           
	}
	
	@Override
	public void destroy(final Context context) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void update(final Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG, "##widget_video update");
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.video_widget_layout);
        updateVideo(remoteViews, context);
        AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, com.yecon.carwidget8317.CarWidgetProvider.class), remoteViews);
	}
	
	@Override
	public void notify(final Context context, final String s) {
		// TODO Auto-generated method stub
		Log.d(TAG, "##widget_video receive msg="+s);
		
		if (s.equals(WidgetsBase.ACTION_PREVIOUS)) {
			SendMsg.send(context, McuExternalConstant.MCU_ACTION_MEDIA_PREVIOUS);
		}else if (s.equals(WidgetsBase.ACTION_NEXT)) {
			SendMsg.send(context, McuExternalConstant.MCU_ACTION_MEDIA_NEXT);
		}else if (s.equals(WidgetsBase.ACTION_PLAYPAUSE)) {
			SendMsg.send(context, McuExternalConstant.MCU_ACTION_MEDIA_PLAY_PAUSE);
		}
		
		super.notify(context, s);
	}
}
