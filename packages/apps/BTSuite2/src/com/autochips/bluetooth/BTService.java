package com.autochips.bluetooth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static android.mcu.McuExternalConstant.*;

import android.media.AudioManager;
import com.autochips.bluetooth.PbSyncManager.PBRecord;
import com.autochips.bluetooth.PbSyncManager.PBSyncManagerService;
import com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtPlayerManage;
import com.autochips.bluetooth.hf.BluetoothHfAtHandler;
import com.autochips.bluetooth.hf.BluetoothHfService;
import com.autochips.bluetooth.hf.BluetoothHfUtility;
import com.autochips.bluetooth.util.L;
import com.autochips.bluetooth.util.SystemContactsManager;
import com.media.constants.MediaConstants;
import com.yecon.common.SourceManager;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.bluetooth.BluetoothDevice;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.view.View;
import static android.constant.YeconConstants.*;

public class BTService extends Service {
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	private static List<Handler> notifyHandlerList=new ArrayList<Handler>();
	public static int MSG_BT_STATUS_NOTIFY  = 1008;
	private int currentPlayingState = BluetoothAvrcpCtPlayerManage.PAUSED;
	private int mScoState = 0;
	private boolean mIsPhoneSco = false;
	private boolean isInBackCar = false;
	private int mPhoneCallState = 0;
	public static void registerNotifyHandler(Handler handler){
		notifyHandlerList.add(handler);
	}
	public static void unregisterNotifyHandler(Handler handler){
		notifyHandlerList.remove(handler);
	}
	private void notifyUiBtStatus(Intent intent){
		Iterator<Handler> nf = notifyHandlerList.iterator();
		Handler h;
		while(nf.hasNext()){
			h = nf.next();
			h.sendMessage(h.obtainMessage(MSG_BT_STATUS_NOTIFY, intent));
		}
	}
	
    private BroadcastReceiver mBTReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	String action = intent.getAction();
    		if (action.equals(PBSyncManagerService.ACTION_STARTDOWNLOAD_POSITION)) {
    			notifyUiBtStatus(intent);
    		}else if (action.equals(PBSyncManagerService.ACTION_DOWNLOAD_ONESTEP)) {
    			notifyUiBtStatus(intent);
			}else if (action.equals(PBSyncManagerService.ACTION_DOWNLOAD_FINISH)) {
				Bluetooth.getInstance().writeLastDownLoadMac(
						Bluetooth.getInstance().getConnectedHFPAddr());
    			notifyUiBtStatus(intent);
    			Bluetooth.getInstance().refreshsystemcontact();
			}else if (action.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)
    				|| action.equals(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE)
    				|| action.equals(ACTION_QB_POWEROFF)) {
    			Bluetooth.getInstance().handleCallStateUpdate(intent);
    			notifyUiBtStatus(intent);
			}
        }
        
    };
    
 	BroadcastReceiver controlReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String mediaType = intent.getStringExtra("media_type");
			L.e(this.getClass().getName(), "action:" + action + "  mediaType:" + mediaType);
			if(MediaConstants.DO_INACTIVE.equalsIgnoreCase(action)){
				//Bluetooth.getInstance().setA2DPAudio(false);
						if (Bluetooth.getInstance().isA2DPPlaying()) {
							Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
							mEditor.putBoolean("autoPlayBTMusic", true).commit();
						}
				//SourceManager.releaseSource(context,SourceManager.SRC_NO.bluetooth);
				//Object sourceTocken = SourceManager.registerSourceListener(context,SourceManager.SRC_NO.music);
				//SourceManager.acquireSource(sourceTocken);
			}
			else if(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH.equals(mediaType)
			||(MediaConstants.DO_STOP.equalsIgnoreCase(action))
			){
				try {	
					if(MediaConstants.DO_PLAY.equalsIgnoreCase(action)){
						if (!Bluetooth.getInstance().isA2DPPlaying()) {
							Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PLAY);
							mEditor.putBoolean("autoPlayBTMusic", true).commit();
						}
					}else if(MediaConstants.DO_PAUSE.equalsIgnoreCase(action)){
						if (Bluetooth.getInstance().isA2DPPlaying()) {
							Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
							mEditor.putBoolean("autoPlayBTMusic", false).commit();
						}
					}else if(MediaConstants.DO_STOP.equalsIgnoreCase(action)){
						if (Bluetooth.getInstance().isA2DPPlaying()) {
							Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
							mEditor.putBoolean("autoPlayBTMusic", true).commit();
						}
					}
					
					else if(MediaConstants.DO_PREV.equalsIgnoreCase(action)){
						Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PREV);
					}else if(MediaConstants.DO_NEXT.equalsIgnoreCase(action)){
						Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_NEXT);
					}else if(MediaConstants.CURRENT_MEDIA.equalsIgnoreCase(action)){
						boolean isPlaying = Bluetooth.getInstance().isA2DPPlaying();
						if (!isPlaying) {
							sendBroadcast(new Intent(MediaConstants.TO_PAUSE), null);
						} else if (isPlaying) {
							sendBroadcast(new Intent(MediaConstants.TO_PLAY), null);
						}else {
							sendBroadcast(new Intent(MediaConstants.TO_STOP), null);
						}
					}else if(MediaConstants.ACTION_GET_BT_CURRENT_STATE.equalsIgnoreCase(action)){
//						L.e("get MediaConstants.ACTION_GET_BT_CURRENT_STATE");
						if(Bluetooth.getInstance().isConnected()){
							Intent outIntent = new Intent();
							outIntent.setAction(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH);
							boolean isPlaying = Bluetooth.getInstance().isA2DPPlaying();
							if (!isPlaying) {
								outIntent.putExtra(MediaConstants.CURRENT_MEDIA,MediaConstants.TO_PAUSE);	
							} else if (isPlaying) {
								outIntent.putExtra(MediaConstants.CURRENT_MEDIA,MediaConstants.TO_PLAY);	
							}else {
								outIntent.putExtra(MediaConstants.CURRENT_MEDIA,MediaConstants.TO_STOP);
							}
							sendBroadcast(outIntent, null);
//							L.e("out MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH isPlaying: " + isPlaying);
						}
					}
				} catch (Exception e) {
					L.e(this.getClass().getName(), e.toString()); 
				}
			}
		}
	};
	
	BroadcastReceiver otherReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			L.e("action  " + this.getClass() + "  : " + action);
			if("com.action.source.open.no_audio".equals(action)){  //在图片源时,不要播放蓝牙音乐
				if(Bluetooth.getInstance().isA2DPPlaying()){
					//mEditor.putBoolean("autoPlayBTMusic", true).commit();
					Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
					MainBluetoothActivity.playNextTime = true;
				}
			}else if(BluetoothHfService.ACTION_SCO_STATE_CHANGED.equals(action)){
				mScoState = intent.getIntExtra(BluetoothHfService.EXTRA_NEW_SCO_STATE, 0);
				L.e("BluetoothHfService.ACTION_SCO_STATE_CHANGED:" + mScoState);
			}else if(action.equals(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE)){
				mPhoneCallState = intent.getIntExtra(BluetoothHfUtility.EXTRA_CALL_STATE, 0);
				if(mPhoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING){
					if(isInBackCar){
						L.e("BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING):switchcallaudio");
						try {
							mIsPhoneSco = Bluetooth.getInstance().switchcallaudio(true);
						} catch (Exception e) {
							L.e(e.toString());
						}
					}
					int volume = SystemProperties.getInt("persist.sys.current_volume",0);
					L.i("BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING:" + volume);
					if(volume == 0){
						mIsPhoneSco = Bluetooth.getInstance().switchcallaudio(true);
					}
	            }else if(mPhoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING || mPhoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING){
					int volume = SystemProperties.getInt("persist.sys.current_volume",0);
					L.i("BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING/outComing:" + volume);
					if(volume == 0){
						mIsPhoneSco = Bluetooth.getInstance().switchcallaudio(true);
					}
	            }else if(mPhoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_IDLE){
					SystemProperties.set(PROPERTY_KEY_BTPHONE_STARTUP, "false");
	            	if(mIsPhoneSco){
		            	try {
							mIsPhoneSco = Bluetooth.getInstance().switchcallaudio(false);
		            	} catch (Exception e) {
							L.e(e.toString());
						}
	            	}
	            }
			}else if(Context.ACTION_BACKCAR_OFF.equals(action)){	
				isInBackCar = false;
				if(mIsPhoneSco){
	            	try {
	            		mIsPhoneSco = Bluetooth.getInstance().switchcallaudio(false);
	            	} catch (Exception e) {
						L.e(e.toString());
					}
            	}
				if("com.baidu.carlifevehicle".equalsIgnoreCase(getRecentApp())
				||FAWLINK_PACKAGE_NAME.equalsIgnoreCase(getRecentApp())){
					
				}else{
					if(mPref.getBoolean("autoPlayBTMusic", false)){
						if(!Bluetooth.getInstance().isA2DPPlaying()){
							Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PLAY);
							mEditor.putBoolean("autoPlayBTMusic", false).commit();
						}
					}
				}
			}else if(Context.ACTION_BACKCAR_ON.equals(action)){
				isInBackCar = true;
				
				if(mPhoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING){
					try {
						mIsPhoneSco = Bluetooth.getInstance().switchcallaudio(true);
					} catch (Exception e) {
						L.e(e.toString());
					}
				}
				if(Bluetooth.getInstance().isA2DPPlaying()){
					Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
					mEditor.putBoolean("autoPlayBTMusic", true).commit();
				}
			}else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
				
			}else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
				mIsPhoneSco = false;
			}else if(YeconConstants.ACTION_QB_PREPOWEROFF.equals(action)){
				if(mPhoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING){
					try {
						mIsPhoneSco = Bluetooth.getInstance().switchcallaudio(true);
					} catch (Exception e) {
						L.e(e.toString());
					}
				}
			}else if("action.hzh.media.power.on".equals(action)){
				if(mPhoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING){
					try {
						Bluetooth.getInstance().switchcallaudio(false);
	            		mIsPhoneSco = false;
					} catch (Exception e) {
						L.e(e.toString()); 
					}
				}
			} else if(MediaConstants.DO_EXIT_APP.equals(action)){
				if(Bluetooth.getInstance().isConnected()){}
				else {
					MainApp.exitAllActivity();
				}
			}
		}
	};
	
	private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BluetoothReceiver.MSG_BT_STATUS_NOTIFY) {
				Intent intent = (Intent) msg.obj;
				String recievedAction = intent.getAction();
				 if (recievedAction.equals(BluetoothAvrcpCtPlayerManage.ACTION_PLAYBACK_DATA_UPDATE)) {
					 byte play_status = intent.getByteExtra(BluetoothAvrcpCtPlayerManage.PLAYBACK_STATUS, (byte) 0);
				        switch (play_status) {
				        case BluetoothAvrcpCtPlayerManage.PLAYING:            
//				        	sendBroadcast(new Intent(MediaConstants.TO_PLAY), null);
				        	L.e(this.getClass().getName(), "BluetoothAvrcpCtPlayerManage.PLAYING");
				        	if(currentPlayingState != BluetoothAvrcpCtPlayerManage.PLAYING){	
				        		currentPlayingState = BluetoothAvrcpCtPlayerManage.PLAYING;
				        		sendBroadcast(new Intent(MediaConstants.TO_PLAY), null);
				        	}
				            break;

				        case BluetoothAvrcpCtPlayerManage.PAUSED:
//				        	sendBroadcast(new Intent(MediaConstants.TO_PAUSE), null);\
				        	L.e(this.getClass().getName(), "BluetoothAvrcpCtPlayerManage.PAUSED");
				        	if(currentPlayingState != BluetoothAvrcpCtPlayerManage.PAUSED){	
				        		currentPlayingState = BluetoothAvrcpCtPlayerManage.PAUSED;
				        		sendBroadcast(new Intent(MediaConstants.TO_PAUSE), null);
				        	}
				        	break;
				        case BluetoothAvrcpCtPlayerManage.STOPPED:            
//				        	sendBroadcast(new Intent(MediaConstants.TO_STOP), null);
				        	L.e(this.getClass().getName(), "BluetoothAvrcpCtPlayerManage.STOPPED");
				        	if(currentPlayingState != BluetoothAvrcpCtPlayerManage.STOPPED){
				        		currentPlayingState = BluetoothAvrcpCtPlayerManage.STOPPED;
				        		sendBroadcast(new Intent(MediaConstants.TO_STOP), null);
				        	}
				            break;
				        }
				 }
			}
		}
	};
	
    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("system_settings", Context.MODE_PRIVATE);
		mEditor = mPref.edit();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PBSyncManagerService.ACTION_STARTDOWNLOAD_POSITION);
        filter.addAction(PBSyncManagerService.ACTION_DOWNLOAD_ONESTEP);
        filter.addAction(PBSyncManagerService.ACTION_DOWNLOAD_FINISH);
        registerReceiver(mBTReceiver, filter);
        
        IntentFilter mediaFilter = new IntentFilter();
        mediaFilter.addAction(MediaConstants.DO_PLAY);
        mediaFilter.addAction(MediaConstants.DO_PAUSE);
        mediaFilter.addAction(MediaConstants.DO_PREV);
        mediaFilter.addAction(MediaConstants.DO_NEXT);
        mediaFilter.addAction(MediaConstants.DO_STOP);
        mediaFilter.addAction(MediaConstants.CURRENT_MEDIA);
        mediaFilter.addAction(MediaConstants.ACTION_GET_BT_CURRENT_STATE);
	
		registerReceiver(controlReceiver, mediaFilter);
		BluetoothReceiver.registerNotifyHandler(uiHandler);
		
		IntentFilter otherFilter = new IntentFilter();
		otherFilter.addAction("com.action.source.open.no_audio");
		otherFilter.addAction(BluetoothHfService.ACTION_SCO_STATE_CHANGED);
		otherFilter.addAction(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE);
		otherFilter.addAction(Context.ACTION_BACKCAR_OFF);
		otherFilter.addAction(Context.ACTION_BACKCAR_ON);
		otherFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		otherFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		otherFilter.addAction(YeconConstants.ACTION_QB_PREPOWEROFF);
		otherFilter.addAction(MediaConstants.DO_EXIT_APP);
		otherFilter.addAction("action.hzh.media.power.on");
		otherFilter.setPriority(100);
		registerReceiver(otherReceiver, otherFilter);
    }

    @Override
    public void onDestroy() {
    	unregisterReceiver(controlReceiver);
    	unregisterReceiver(mBTReceiver);
    	unregisterReceiver(otherReceiver);
    	BluetoothReceiver.unregisterNotifyHandler(uiHandler);
        super.onDestroy();
    }
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}
	
	private String getRecentApp(){
		ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
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
			}else if(FAWLINK_PACKAGE_NAME.equals(packageName)){
				pn = packageName;
				break;
			}
		}
		return pn;
	}
}
