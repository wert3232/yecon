package com.tuoxianui.view;

import com.media.constants.MediaConstants;
import com.tuoxianui.db.ContentData;
import com.tuoxianui.view.util.TopLog;
import com.tuoxianui.view.util.VolumeUtil;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class MuteTextView extends TextView {
	private MuteTextView self = this;
	private Context mContext;
	private AudioManager mAudioManager;
	public final static String ACTION_MEDIA_VOLUME_OPEN= "com.hzh.media.volume.open";
	public final static String ACTION_MEDIA_VOLUME_MUTE= "com.hzh.media.volume.mute_key";
	public MuteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MuteTextView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public MuteTextView(Context context) {
		super(context);
		init(context);
	}
	
	@Override
	protected void onAttachedToWindow() {
		checkVolume();
		super.onAttachedToWindow();
		
	}
	@Override
	protected void onDetachedFromWindow() {
		mContext.unregisterReceiver(receiver);
		super.onDetachedFromWindow();
	}
	
	
	private void init(Context context) {
		mContext = context;
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE) ;
		mContext.registerReceiver(receiver, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
	}
	public void toggle(){
		int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if(mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)){
			mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC,false);
		}
		if(volume == 0){
			openVolume(AudioManager.FLAG_SHOW_UI);
		}else{;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,AudioManager.FLAG_SHOW_UI);
		}
		/*McuManager manager = (McuManager)mContext.getSystemService(Context.MCU_SERVICE);
		//manager.RPC_SetVolume(3,10);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10,1);*/
	}
	public boolean isMute(){
		return this.isActivated();
	}
	public void setMute(boolean mute){
		int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if(mute){
			if(isMute() || volume == 0){
				return;
			}
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,AudioManager.FLAG_SHOW_UI);
		}else{
			if(volume > 0){
				return;
			}
			Cursor cursor = mContext.getContentResolver().query(ContentData.DeviceStateTableData.CONTENT_URI_PRE_VOLUME, null, null, null, null);
			int preVolume = 0;
			if(cursor != null){
				while(cursor.moveToNext()){
					int index = cursor.getColumnIndex(ContentData.DeviceStateTableData.PRE_VOLUME);
					preVolume = cursor.getInt(index);
				}
			}
			if(preVolume == 0){
				return;
			}
			
			openVolume(1);
		}
	}
	public void setMute(boolean mute,long delay){
		
		if(!mute && mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0){
			self.removeCallbacks(openVolumeRn);
			self.postDelayed(openVolumeRn, delay);
		}else{
			setMute(mute);
		}
	}
	private Runnable openVolumeRn = new Runnable() {
		@Override
		public void run() {
			self.setMute(false);
		}
	};
	
	public void initDefualtVolume(){
		
		Cursor cursor = mContext.getContentResolver().query(ContentData.DeviceStateTableData.CONTENT_URI_PRE_VOLUME, null, null, null, null);
		int currentVolume = Context.DEFUALT_VOLUME;
		if(cursor != null){
			while(cursor.moveToNext()){
				int index2 = cursor.getColumnIndex(ContentData.DeviceStateTableData.CURRENT_VOLUME);
				currentVolume = cursor.getInt(index2);
			}
		}
		SystemProperties.set("persist.sys.current_volume", currentVolume +"");
		if(currentVolume > Context.DEFUALT_VOLUME){
			mAudioManager.setStreamVolume(AudioManager.STREAM_GIS, Context.DEFUALT_VOLUME,0); 
			mAudioManager.setStreamVolume(AudioManager.STREAM_RING, Context.DEFUALT_VOLUME,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, Context.DEFUALT_VOLUME,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_RDS, Context.DEFUALT_VOLUME,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Context.DEFUALT_VOLUME,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, Context.DEFUALT_VOLUME,0);
			SystemProperties.set("persist.sys.current_volume", Context.DEFUALT_VOLUME + "");
		}else{
			mAudioManager.setStreamVolume(AudioManager.STREAM_GIS, currentVolume,0); 
			mAudioManager.setStreamVolume(AudioManager.STREAM_RING, currentVolume,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, currentVolume,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_RDS, currentVolume,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, currentVolume,0);
		}
	}
	
	public void openVolume(){
		/*int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		TopLog.e("Launcher",MuteTextView.ACTION_MEDIA_VOLUME_OPEN + "   volume:"  + volume);
		if(volume == 0){
			VolumeUtil.muteToggle(mContext);
		}else{
		}*/
		openVolume(0);
	}
	public void openVolume(int flag){
		Cursor cursor = mContext.getContentResolver().query(ContentData.DeviceStateTableData.CONTENT_URI_PRE_VOLUME, null, null, null, null);
		int volume = -1;
		int currentVolume = -1;
		int hasVolume = -1;
		if(cursor != null){
			while(cursor.moveToNext()){
				int index = cursor.getColumnIndex(ContentData.DeviceStateTableData.PRE_VOLUME);
				volume = cursor.getInt(index);
				int index2 = cursor.getColumnIndex(ContentData.DeviceStateTableData.CURRENT_VOLUME);
				currentVolume = cursor.getInt(index2);
				int index3 = cursor.getColumnIndex(ContentData.DeviceStateTableData.HAVE_VOLUME);
				hasVolume = cursor.getInt(index3);
			}
		}
		Log.e("Launcher","toggle mAudioManager.getPreVolume():" + mAudioManager.getPreVolume() + 
						 "   volume:" + volume + "  " + currentVolume + "  " + hasVolume);
		int preVolume = volume;
		if(preVolume >= 0 ){	
			mAudioManager.setStreamVolume(AudioManager.STREAM_GIS, preVolume,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_RING, preVolume,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, preVolume,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_RDS, preVolume,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, preVolume,flag);
			mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, preVolume,0);
		}else{
			mAudioManager.setStreamVolume(AudioManager.STREAM_GIS, Context.DEFUALT_VOLUME,0); 
			mAudioManager.setStreamVolume(AudioManager.STREAM_RING, Context.DEFUALT_VOLUME,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, Context.DEFUALT_VOLUME,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_RDS, Context.DEFUALT_VOLUME,0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Context.DEFUALT_VOLUME,flag);
			mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, Context.DEFUALT_VOLUME,0);
		}	
	}
	private void checkVolume(){
		int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;
		if(volume == 0){
			this.setActivated(true);
		}else if(volume > 0){
			this.setActivated(false);
		}
	}
	BroadcastReceiver receiver = new BroadcastReceiver() {	
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
				checkVolume();
			}
		}
	};
}
