package com.yecon.radio;

import com.yecon.carwidget8317.CarWidgetService;
import com.yecon.carwidget8317.ConstDefine;
import com.yecon.carwidget8317.R;
import com.yecon.carwidget8317.SendKey;
import com.yecon.carwidget8317.SendMsg;
import com.yecon.carwidget8317.WidgetsBase;
import com.yecon.savedata.SaveData;
import com.yecon.savedata.SaveData.SaveDataListener;
import com.yecon.savedata.SaveDataConstant;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.mcu.McuExternalConstant;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RemoteViews;

public class RadioLogic extends WidgetsBase {
	private static final String TAG = "radioLogic";

	static {
	}
	
	public RadioLogic() {
		
	}
	
	/**
	 * @param remoteViews
	 * @param context
	 */
	public void updateFreq(final RemoteViews remoteViews, final Context context) {
		final String packageName = context.getPackageName();
		
		int band = CarWidgetService.mSaveData.getCurBand();
		int freq = CarWidgetService.mSaveData.getCurFreq();
		Log.i(TAG, "widget--->updateFreq, band="+band+", freq="+freq);
		
		    if (band > 2) {
	            remoteViews.setTextViewText(R.id.radio_band, "AM" + (band-2));
	            if (freq < 1000) {
	                remoteViews.setViewVisibility(R.id.digit_1,  View.GONE);
	            }
	            else {
	                remoteViews.setViewVisibility(R.id.digit_1, View.VISIBLE);
	                final int n = freq / 1000 % 10;
	                remoteViews.setImageViewResource(R.id.digit_1, context.getResources().getIdentifier("digit" + freq / 1000 % 10, "drawable", packageName));
	            }
	            remoteViews.setImageViewResource(R.id.digit_2, context.getResources().getIdentifier("digit" + freq / 100 % 10, "drawable", packageName));
	            remoteViews.setImageViewResource(R.id.digit_3, context.getResources().getIdentifier("digit" + freq / 10 % 10, "drawable", packageName));
	            remoteViews.setViewVisibility(R.id.digit_dot,  View.GONE);
	            remoteViews.setImageViewResource(R.id.digit_4, context.getResources().getIdentifier("digit" + freq % 10, "drawable", packageName));
	            remoteViews.setViewVisibility(R.id.digit_5, View.GONE);
	            remoteViews.setTextViewText(R.id.radio_unit, "KHz");
	            return;
	        }
		    
	        remoteViews.setTextViewText(R.id.radio_band, "FM" + (1 + band));
	        if (freq < 10000) {
	            remoteViews.setViewVisibility(R.id.digit_1, View.GONE);
	        }
	        else {
	            remoteViews.setViewVisibility(R.id.digit_1, View.VISIBLE);
	            remoteViews.setImageViewResource(R.id.digit_1, context.getResources().getIdentifier("digit" + freq / 10000 % 10, "drawable", packageName));
	        }
	        remoteViews.setImageViewResource(R.id.digit_2, context.getResources().getIdentifier("digit" + freq / 1000 % 10, "drawable", packageName));
	        remoteViews.setImageViewResource(R.id.digit_3, context.getResources().getIdentifier("digit" + freq / 100 % 10, "drawable", packageName));
	        remoteViews.setViewVisibility(R.id.digit_dot, View.VISIBLE);
	        remoteViews.setImageViewResource(R.id.digit_dot, context.getResources().getIdentifier("dot", "drawable", packageName));
	        remoteViews.setImageViewResource(R.id.digit_4, context.getResources().getIdentifier("digit" + freq / 10 % 10, "drawable", packageName));
	        remoteViews.setViewVisibility(R.id.digit_5, View.VISIBLE);
	        remoteViews.setImageViewResource(R.id.digit_5, context.getResources().getIdentifier("digit" + freq % 10, "drawable", packageName));
	        remoteViews.setTextViewText(R.id.radio_unit, "MHz");
	}
	
	@Override
	public void init(final Context context) {
		// TODO Auto-generated method stub
		Log.i(TAG, "widget--->radiologic init");
		
		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.radio_widget_layout);
		
		final Intent preIntent = new Intent().setAction(WidgetsBase.ACTION_PREVIOUS);		
		PendingIntent prePendingIntent = PendingIntent.getBroadcast(context, 0, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.radio_prev_btn, prePendingIntent);
		final Intent nextIntent = new Intent().setAction(WidgetsBase.ACTION_NEXT);		
		PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.radio_next_btn, nextPendingIntent);
		
		if (ConstDefine.TOUCH_INTO_APP) {
			final Intent enterIntent = new Intent(Intent.ACTION_VIEW);
			enterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			enterIntent.setComponent(new ComponentName("com.yecon.fmradio", "com.yecon.fmradio.FMRadioMainActivity"));
			PendingIntent enterPendingIntent = PendingIntent.getActivity(context, 0, enterIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.radio_layout, enterPendingIntent);
		}		
		
		updateFreq(remoteViews, context);
		
		AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, com.yecon.carwidget8317.CarWidgetProvider.class), remoteViews);                           
	}
	
	@Override
	public void destroy(final Context context) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void update(final Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG, "##widget_radio update");
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.radio_widget_layout);
        updateFreq(remoteViews, context);
        AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, com.yecon.carwidget8317.CarWidgetProvider.class), remoteViews);
	}
	
	@Override
	public void notify(final Context context, final String s) {
		// TODO Auto-generated method stub
		Log.d(TAG, "##widget_radio receive msg="+s);
		if (s.equals(WidgetsBase.ACTION_PREVIOUS)) {
			SendMsg.send(context, McuExternalConstant.MCU_ACTION_RADIO_PRESET_PRE);
		}else if (s.equals(WidgetsBase.ACTION_NEXT)) {
			SendMsg.send(context, McuExternalConstant.MCU_ACTION_RADIO_PRESET_NEXT);
		}
		
		super.notify(context, s);
	}
}
