package com.yecon.carsetting.mcu;

import java.util.ArrayList;
import java.util.Iterator;

import android.cbm.CBManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcel;
import android.util.Log;

public class ExtApkCbm {
	private Context mContext = null;
	private ArrayList<Apk>listApks = new ArrayList<Apk>();
	ExtApkCbm(Context context){
		mContext = context;
	}
	protected static final String TAG = "ExtApkCbm";
	private class Apk{
		private String name;
		private String intentAction;
		private volatile CBManager mCBM = null;
		private int srcId = 0;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getIntentAction() {
			return intentAction;
		}
		public void setIntentAction(String intentAction) {
			this.intentAction = intentAction;
		}
		public int getSrcId() {
			return srcId;
		}
		public void setSrcId(int srcId) {
			this.srcId = srcId;
		}
		Apk(String name, int srcid, String intentAction){
			this.name = name;
			srcId = srcid;
			this.intentAction = intentAction;
		}
		boolean request(){
			if(mCBM == null){
				mCBM = new CBManager();
		        if (null != mCBM) {
		            mCBM.setOnActionListener(mCBMActionListener);
		        }
			}
			Parcel reply = Parcel.obtain();
			int ret = mCBM.request(srcId, CBManager.DIR_FRONT, reply);
            int res = reply.readInt();
            reply.recycle();
            if (ret == CBManager.CBM_FAIL) {
                //throw new IllegalStateException("CBM run exception!!!");
            	Log.e(TAG, "CBM run exception!");
            	return false;
            }else if (ret == CBManager.CBM_STATE_ERR){
            	Log.w(TAG, "WARN:CBManager no need request again!");
            }else if (res == CBManager.REQ_FORBID) {
            	Log.w(TAG, "WARN:CBManager reply REQ_FORBID, may be can not play!");			
            	return false;
            }
            
            Log.i(TAG, "request OK srcId:" + srcId);
            return true;
		}
		void release(){
			Log.i(TAG, "release srcId:" + srcId);
			if(mCBM!=null){
				mCBM.release();
				mCBM=null;
			}
		}
		void handleIntent(Intent intent){
			int  state = intent.getIntExtra("state",  -1);
			if(state == 0){
				 //app exit.
				release();
			 }
			 else if(state == 1){
				 //app started.
				boolean ret = request();
				Intent it = new Intent();
				it.setAction(intentAction);
				it.putExtra("cbm_quest", ret);
				mContext.sendBroadcast(it);
			 }
		}
		private CBManager.OnActionListener mCBMActionListener = new CBManager.OnActionListener() {

	        public int onAction(int command, int param1, int param2) {
	            Log.i(TAG, "onAction:cmd = " + command);
	            switch (command) {
	            case CBManager.CMD_START:
	            case CBManager.CMD_RESUME:
	            case CBManager.CMD_PAUSE:
	            case CBManager.CMD_STOP:
	            	if(mContext!=null){
	            		Intent it = new Intent();
	            		it.setAction(intentAction);
	            		it.putExtra("cbm_nofity", command);
	            		mContext.sendBroadcast(it);
	            	}
	            	break;
	            }
	            return CBManager.CBM_OK;
	        }
	 };
	}
	
	public static final String YECON_PHONE_LINK_NAME = "com.yecon.phonelink";
	public static final String YECON_PHONE_LINK_ACTION = "yecon.phonelink.action.STATE_CHANGED";
	   
	 void handleExtApkIntent(Intent intent){
		 final String action = intent.getAction();
		 Iterator<Apk> apks = listApks.iterator();
			Apk apk;
			while (apks.hasNext()) {
				apk = apks.next();
				if(apk.getIntentAction().equals(action)){
					apk.handleIntent(intent);
				}
			}
	 }
	 
	void registerApk(String name, int srcId, String intent_action) {
		Apk apk = new Apk(name, srcId, intent_action);
		listApks.add(apk);
	}
	 
	void unregisterApk(String name) {
		Iterator<Apk> apks = listApks.iterator();
		Apk apk;
		while (apks.hasNext()) {
			apk = apks.next();
			if (apk.getName().equals(name)) {
				apks.remove();
			}
		}
	}
	
	void start(){
		if (mContext != null) {
			IntentFilter filter = new IntentFilter();
			Iterator<Apk> apks = listApks.iterator();
			Apk apk;
			while (apks.hasNext()) {
				apk = apks.next();
				filter.addAction(apk.getIntentAction());
			}
			mContext.registerReceiver(mReceiver, filter);
		}
	}
	void stop(){
		mContext.unregisterReceiver(mReceiver);
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleExtApkIntent(intent);
        }
	};
}
