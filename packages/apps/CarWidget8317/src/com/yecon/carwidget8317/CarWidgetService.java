package com.yecon.carwidget8317;

import com.yecon.common.SourceManager;
import com.yecon.savedata.SaveData;
import com.yecon.savedata.SaveData.SaveDataListener;
import com.yecon.savedata.SaveDataConstant;

import android.app.Service;
import android.app.Fragment.SavedState;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class CarWidgetService extends Service{
	public static final String ACTION_NAME = "command";
    public static final String CMD_SERVICE = "com.yecon.carwidget8317.CarWidgetService";
    private static final String TAG = "CarWidgetService";
    private int mCurSource;
    private static CarWidgetService sInstance;
    public WidgetsBase mCurLogic=null;
    private BroadcastReceiver mIntentReceiver;
    public static SaveData mSaveData;
    
    static {
    	sInstance = null;
    }
    
    Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case ConstDefine.WIDGET_BUTTON_MSG:
				String str = (String)msg.obj;
				if (mCurLogic != null && str != null) {
					Log.d(TAG, "widget_service--->on handleMessage, receive msg="+str);
					mCurLogic.notify(getApplicationContext(), str);
				}else {
					Log.d(TAG, "widget_service--->on handleMessage, receive msg="+str+",but mCurLogic=null");
				}
				break;
			case ConstDefine.WIDGET_UPDATE_MSG:
				if (mCurLogic != null) {
					mCurLogic.update(getApplicationContext());
				}
				break;
			default:
				break;
			}
		}
    	
    };
    
    private void getSourceLogic(final int n) {
        Log.i(TAG, "widget--->source changed=" + n);
        final String logicNameFromSourceID = manager.getLogicNameFromSourceID(n);
        Log.i(TAG, "widget--->logicNameFromSourceID=" + logicNameFromSourceID);
        
        if (logicNameFromSourceID != "") {
            if (mCurLogic != null) {
                mCurLogic.destroy(getApplicationContext());
            }
            mCurLogic = manager.getLogicFromString(logicNameFromSourceID);
            if (mCurLogic != null) {
            	Log.i(TAG, "widget--->start run logic init");
                mCurLogic.init(getApplicationContext());
            }
        }
    }
   
    public void UpdateWidget() {
        Log.i(TAG, "UpdateWidget");
        getSourceLogic(mCurSource);
    }
    
    public static CarWidgetService getInstance() {
        return CarWidgetService.sInstance;
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->on service onBind");
		
		return null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "widget--->on service create");
		
		// TODO Auto-generated method stub
		mIntentReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				 final String action = arg1.getAction();
				 Log.d(TAG, "widget_service--->on service, receive msg="+action);
				 
				 if (action.equals(ConstDefine.ACTION_SOURCE_CHANGE)) {
					 mCurSource = arg1.getIntExtra(SourceManager.EXTRA_SOURCE_NO, 0);
					Log.d(TAG, "widget--->on service source changed,source="+mCurSource);
					getSourceLogic(mCurSource);
				 }else if (action.equals(SourceManager.ACTION_AUDIO_FUCOS_CHANGED)) {
					 //音频焦点发生改变
					 String getFucosPkg = arg1.getStringExtra(SourceManager.EXTRA_AUDIO_FUCOS.GOT_FUCOS_PKG);
					 String lossFucosPkg = arg1.getStringExtra(SourceManager.EXTRA_AUDIO_FUCOS.LOSS_FUCOS_PKG);
					 Log.d(TAG, "widget--->on audio fucos changed, get="+getFucosPkg+",loss="+lossFucosPkg);
					 
				 }else if (action.equals(WidgetsBase.ACTION_NEXT)
						 || action.equals(WidgetsBase.ACTION_PREVIOUS)
						 || action.equals(WidgetsBase.ACTION_PLAYPAUSE)
						 || action.equals(WidgetsBase.ACTION_PLAY)
						 || action.equals(WidgetsBase.ACTION_PAUSE)) {
					 
					 Message msg = new Message();
					 msg.what = ConstDefine.WIDGET_BUTTON_MSG;
					 msg.obj = (String)action;
					 mHandler.sendMessage(msg);
				 }
			}			
		};
		
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConstDefine.ACTION_SOURCE_CHANGE);
		intentFilter.addAction(WidgetsBase.ACTION_NEXT);
		intentFilter.addAction(WidgetsBase.ACTION_PREVIOUS);
		intentFilter.addAction(WidgetsBase.ACTION_PAUSE);
		intentFilter.addAction(WidgetsBase.ACTION_PLAY);
		intentFilter.addAction(WidgetsBase.ACTION_PLAYPAUSE);
		intentFilter.addAction(SourceManager.ACTION_AUDIO_FUCOS_CHANGED);
		registerReceiver(mIntentReceiver, intentFilter);
		
		mSaveData = new SaveData();
		mSaveData.registerListener(new SaveDataListener() {
			
			@Override
			public void onSaveDataChanged(int arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				if (mCurLogic != null) {
					mCurLogic.update(getApplicationContext());
				}
			}
		}, SaveDataConstant.SAVEDATA_MEDIA_INFO);
		
		mSaveData.registerListener(new SaveDataListener() {
			
			@Override
			public void onSaveDataChanged(int arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				if (mCurLogic != null) {
					mCurLogic.update(getApplicationContext());
				}
			}
		}, SaveDataConstant.SAVEDATA_RADIO_INFO);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->on service destroy");
		if (mIntentReceiver != null) {
			unregisterReceiver(mIntentReceiver);
		}
		
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->on service lowMemory");
		super.onLowMemory();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->on service rebind");
		super.onRebind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->on service onStart");
		
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->on service onStartCommand");
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		// TODO Auto-generated method stub
		super.onTaskRemoved(rootIntent);
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "widget--->on service onUnbind");
		
		return super.onUnbind(intent);
	}

}
