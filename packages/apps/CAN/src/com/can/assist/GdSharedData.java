package com.can.assist;

import java.util.Calendar;

import com.autochips.bluetooth.LocalBluetoothProfileManager;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.hf.BluetoothHfUtility;
import com.can.parser.DDef.PhoneState;
import com.can.parser.DDef.TimeInfo;
import com.can.tool.CanInfo;
import com.yecon.common.SourceManager;
import com.yecon.savedata.SaveData;
import android.annotation.SuppressLint;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

/**
 * ClassName:GdSharedData
 * 
 * @function:共享数据（获取媒体信息及接收倒车信号广播）
 * @author Kim
 * @Date: 2016-6-15 上午11:24:08
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class GdSharedData {

	private int miSource = -1;
	private int miVolume = -1;
	private int miloopTick = 0;
	private long mDelayMillis = 1000;
	private String mStrDyncTrack = "";
	private Context mObjContext = null;
	private MediaInfo mObjMediaInfo = null;
	private GdReceiver mObjGdReceiver = null;
	private OnGdDataListener mGdDataListener;
	private AudioManager mObjAudioManager = null;
	protected Handler mObjhHandler = new Handler();

	protected final String TAG = this.getClass().getName();
	private static final String ACTION_BACKCAR_START = "com.yecon.action.BACKCAR_START";
	private static final String ACTION_BACKCAR_STOP = "com.yecon.action.BACKCAR_STOP";
	private static final String ACTION_SOURCE_CHANGE = "com.yecon.sourcemanager.source_changed_notify";
	private static final String ACTION_BACKCAR_TRACK = "com.yecon.action.DyncTrackData";
	private static final String ACTION_ACC_OFF = "com.yecon.action.ACC_OFF";
	private static final String ACTION_ACC_ON = "com.yecon.action.ACC_ON";

	public GdSharedData(Context context) {
		// TODO Auto-generated constructor stub
		mObjContext = context;
		mObjGdReceiver = new GdReceiver();

		IntentFilter inflater = new IntentFilter();
		inflater.addAction(ACTION_BACKCAR_START);
		inflater.addAction(ACTION_BACKCAR_STOP);
		inflater.addAction(ACTION_SOURCE_CHANGE);
		inflater.addAction(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE);
		inflater.addAction(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE);
		inflater.addAction(AudioManager.VOLUME_CHANGED_ACTION);
		inflater.addAction(ACTION_ACC_OFF);
		inflater.addAction(ACTION_ACC_ON);
		context.registerReceiver(mObjGdReceiver, inflater);

		mObjAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
	}

	public void start(long delayMillis) {

		try {
			mDelayMillis = delayMillis;
			mObjhHandler.post(runnable);
			InitSend();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void InitSend(){
		if (mGdDataListener != null) {
			mGdDataListener.onVolInfo(getMediaInfo().getVol());
		}
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mGdDataListener != null) {

				int iMax = mGdDataListener.onCycle2send(miloopTick);

				++miloopTick;
				miloopTick = (miloopTick >= iMax) ? 0 : miloopTick;
				mObjhHandler.postDelayed(runnable, mDelayMillis);
			}
		}
	};

	/**
	 * 
	 * @Title: setOnGdDataListener   
	 * @Description: TODO 
	 * @param onGdDataListener      
	 * @return: void
	 */
	public void setOnGdDataListener(OnGdDataListener onGdDataListener) {
		mGdDataListener = onGdDataListener;
	}

	/**
	 * 
	 * interface:OnGdDataListener
	 * 
	 * @function:回调给注册用户
	 * @author Kim
	 * @Date: 2016-6-8 下午4:15:30
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public interface OnGdDataListener {
		// 循环执行发送
		int onCycle2send(int iloopTick);

		// 倒车状态
		void onReverse(boolean bReverse);

		// 蓝牙状态 iType:0 连接状态, iType:1 通话状态
		void onBtInfo(int iType, int iValue);

		// 音量广播
		void onVolInfo(int iVol);
		
		// Acc on/off
		void onAcc(boolean bState);
	}

	/**
	 * 
	 * @Title: getMediaInfo   
	 * @Description: TODO 
	 * @return      
	 * @return: MediaInfo
	 */
	public MediaInfo getMediaInfo() {

		if (mObjMediaInfo == null) {
			mObjMediaInfo = new MediaInfo();
		}

		return mObjMediaInfo;
	}

	/**
	 * 
	 * @Title: setDyncTrackData   
	 * @Description: 设置动态倒车轨迹角度 
	 * @param iAngle      
	 * @return: void
	 */
	@SuppressLint("NewApi")
	public void setDyncTrackData(int iAngle) {

		Intent intent = new Intent();
		intent.setAction(ACTION_BACKCAR_TRACK);

		Bundle bundle = new Bundle();
		bundle.putInt("TrackData", iAngle);
		bundle.putString("TrackShowSts", mStrDyncTrack);
		intent.putExtras(bundle);

		mObjContext.sendBroadcastAsUser(intent, UserHandle.ALL);
	}

	/**
	 * 
	 * @Title: showDyncTrack   
	 * @Description: 显示隐藏动态轨迹 
	 * @param bshow      
	 * @return: void
	 */
	public void showDyncTrack(boolean bshow) {
		mStrDyncTrack = bshow ? "show" : "hide";
	}

	/**
	 * 
	 * ClassName:MediaInfo
	 * 
	 * @function:媒体信息类
	 * @author Kim
	 * @Date: 2016-6-8 下午3:59:36
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public class MediaInfo {

		public SaveData mObjSaveData = new SaveData();

		/**
		 * 
		 * @Title: getBand  getMainFreq getFreqIndex
		 * @Description: 收音机信息 
		 * @return      
		 * @return: byte
		 */
		public byte getBand() {
			return (byte) mObjSaveData.getCurBand();
		}

		public int getMainFreq() {
			return mObjSaveData.getCurFreq();
		}

		public byte getFreqIndex() {
			return (byte) mObjSaveData.getRadioListId();
		}

		/**
		 * 
		 * @Title: getSource   
		 * @Description: 获取源信息 
		 * @return      
		 * @return: int
		 */
		@SuppressWarnings("static-access")
		public int getSource() {

			int iSource = 0;

			if (miSource == -1) {
				SourceManager objManager = new SourceManager();
				iSource = objManager.lastSource();

			} else {
				iSource = miSource;
			}

			return iSource;
		}

		/**
		 * 
		 * @Title: getCurTrack getTotalTrack getPlayTime   
		 * @Description: TODO 
		 * @return      
		 * @return: int
		 */
		public int getCurTrack() {
			return mObjSaveData.getMediaTrack()+1;
		}

		public int getTotalTrack() {
			return mObjSaveData.getMediaTotalNum();
		}

		public int getPlayTime() {
			return mObjSaveData.getMediaPlayTime();
		}

		public String getID3Title() {
			return mObjSaveData.getMediaSongName();
		}

		public String getID3Author() {
			return null;
		}

		public String getID3Album() {
			return null;
		}

		/**
		 * 
		 * @Title: getPhonestate getPhoneConnects getPhoneNumber
		 * @Description: 电话信息 
		 * @return      
		 * @return: int
		 */
		public int getPhonestate() {
			return mObjSaveData.getBtCallSts();
		}

		public int getPhoneConnects() {
			return mObjSaveData.getBtConnectSts();
		}

		public String getPhoneNumber() {
			return mObjSaveData.getBtCallNum();
		}

		/**
		 * 
		 * @Title: getTimeInfo   
		 * @Description: 获取时间信息 
		 * @return      
		 * @return: TimeInfo
		 */
		public TimeInfo getTimeInfo() {

			TimeInfo timeInfo = new TimeInfo();
			ContentResolver cResolver = mObjContext.getContentResolver();

			timeInfo.by24Mode = 0x00;

			if (Settings.System
					.getString(cResolver, Settings.System.TIME_12_24).equals(
							"24")) {
				timeInfo.by24Mode = 0x01;
			}

			Calendar calendar = Calendar.getInstance();
			timeInfo.iYear = calendar.get(Calendar.YEAR);
			timeInfo.byMonth = (byte) (calendar.get(Calendar.MONTH)+1);
			timeInfo.byDay = (byte) calendar.get(Calendar.DAY_OF_MONTH);

			int iHour = calendar.get(Calendar.HOUR);
			timeInfo.byAmPm = (byte) calendar.get(Calendar.AM_PM);

			if (timeInfo.by24Mode == 0x01) {
				timeInfo.byHour = (byte) (iHour + 12);
			} else {
				timeInfo.byHour = (byte) iHour;
			}

			timeInfo.byMinute = (byte) calendar.get(Calendar.MINUTE);
			timeInfo.bySecond = (byte) calendar.get(Calendar.SECOND);

			return timeInfo;
		}

		/**
		 * 
		 * @Title: getVol   
		 * @Description: 获取音量值 
		 * @return      
		 * @return: int
		 */
		public int getVol() {
			return mObjAudioManager.getStreamVolume(getCurrentStreamType());
		}
		
		public boolean getMute(int streamType){
			return mObjAudioManager.isStreamMute(streamType);
		}
	}

	/**
	 * 
	 * ClassName:GdReceiver
	 * 
	 * @function:TODO
	 * @author Kim
	 * @Date: 2016-6-13 下午2:05:03
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public class GdReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (context == null || intent == null) {

			} else {

				String strAction = intent.getAction();

				if (strAction.equals(ACTION_BACKCAR_START)) {

					if (mGdDataListener != null) {
						mGdDataListener.onReverse(true);
						Log.i(TAG, "onReverse ing!");
					}

				} else if (strAction.equals(ACTION_BACKCAR_STOP)) {

					if (mGdDataListener != null) {
						mGdDataListener.onReverse(false);
						Log.i(TAG, "onReverse stop!");
					}
				} else if (strAction.equals(ACTION_SOURCE_CHANGE)) {

					int iSource = intent.getIntExtra(
							SourceManager.EXTRA_SOURCE_NO, 0);

					if (miSource != iSource) {
						miSource = iSource;
					}
				} else if (strAction
						.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)) {
					com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile profilename = (BTProfile) intent
							.getSerializableExtra(LocalBluetoothProfileManager.EXTRA_PROFILE);
					if (profilename != null) {
						if (profilename
								.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)
								|| profilename
										.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_A2DP_SINK)) {
							int profilestate = intent
									.getIntExtra(
											LocalBluetoothProfileManager.EXTRA_NEW_STATE,
											LocalBluetoothProfileManager.STATE_DISCONNECTED);
							if (profilestate == LocalBluetoothProfileManager.STATE_CONNECTED) {
								mGdDataListener.onBtInfo(0x00, 0x01);
							} else if (profilestate == LocalBluetoothProfileManager.STATE_DISCONNECTED) {
								mGdDataListener.onBtInfo(0x00, 0x00);
							}
						}
					}
				} else if (strAction
						.equals(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE)) {
					int iPhoneCallState = intent.getIntExtra(
							BluetoothHfUtility.EXTRA_CALL_STATE, 0);

					if (iPhoneCallState == PhoneState.INCOMING
							|| iPhoneCallState == PhoneState.OUTGOING
							|| iPhoneCallState == PhoneState.SPEAKING
							|| iPhoneCallState == PhoneState.IDLE) {
						mGdDataListener.onBtInfo(0x01, iPhoneCallState);
					}
				}else if (strAction.equals(AudioManager.VOLUME_CHANGED_ACTION)) {
						
					if (mGdDataListener != null) {
						int iVolume = 0;
						
						if (getMediaInfo().getMute(intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, 0))) {
							iVolume = 0;
						}else {
							iVolume = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, 0);
						}
						
						if (miVolume != iVolume) {
							miVolume = iVolume;
							mGdDataListener.onVolInfo(iVolume);					
						}
					}
				}else if (strAction.equals(ACTION_ACC_OFF)) {
					if (mGdDataListener != null) {
						mGdDataListener.onAcc(false);
					}
				}else if (strAction.equals(ACTION_ACC_ON)) {
					if (mGdDataListener != null) {
						mGdDataListener.onAcc(true);
					}
				}
			}
		}
	}

	private int getCurrentStreamType() {
		int streamType = AudioManager.STREAM_MUSIC;

		if (AudioSystem.isStreamActive(AudioManager.STREAM_MUSIC, 0)) {
			streamType = AudioManager.STREAM_MUSIC;
		}
		boolean mIsNaviApp = SystemProperties.getBoolean(
				YeconConstants.PROPERTY_KEY_IS_NAVI_APP, false);
		boolean mNaviActivityIsForeground = SystemProperties.getBoolean(
				YeconConstants.PROPERTY_KEY_NAVI_IS_FORE, false);
		if (mIsNaviApp && mNaviActivityIsForeground) {
			streamType = AudioManager.STREAM_GIS;
		}

		if (AudioSystem.isStreamActive(AudioManager.STREAM_RING, 0)) {
			streamType = AudioManager.STREAM_RING;
		}

		if (AudioSystem.isStreamActive(AudioManager.STREAM_BLUETOOTH_SCO, 0)) {
			streamType = AudioManager.STREAM_BLUETOOTH_SCO;
		}

		if (AudioSystem.isStreamActive(AudioManager.STREAM_BACKCAR, 0)) {
			streamType = AudioManager.STREAM_BACKCAR;
		}

		if (AudioSystem.isStreamActive(AudioManager.STREAM_RDS, 0)) {
			streamType = AudioManager.STREAM_RDS;
		}

		StringBuffer log = new StringBuffer();
		log.append("getCurrentStreamType - streamType: ");
		log.append(streamType);
		CanInfo.i(TAG, log.toString());

		return AudioManager.STREAM_GIS;
	}
}
