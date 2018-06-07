/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.autochips.bluetooth.Fragment;

import com.autochips.bluetooth.Bluetooth;
import com.autochips.bluetooth.BluetoothReceiver;
import com.autochips.bluetooth.CmnUtil;
import com.autochips.bluetooth.LocalBluetoothProfileManager;
import com.autochips.bluetooth.R;
import com.autochips.bluetooth.TuoXianDialActivity;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.MainBluetoothActivity;
import com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtPlayerManage;
import com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtService;
import com.autochips.bluetooth.util.L;
import com.autochips.bluetooth.util.SystemContactsManager;
import com.autochips.bluetooth.view.MarqueeText;
import com.media.constants.MediaConstants;
import com.tuoxianui.view.MuteTextView;
import com.yecon.common.SourceManager;

import android.app.Instrumentation;
import android.constant.YeconConstants;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
public class MusicFragment extends BaseFragment implements OnClickListener{
	
	private MusicFragment self = this;
    private TextView mediaTitleInfo;
    private TextView mediaArtistInfo;
    private TextView mediaAlbumInfo;
    private TextView mediaPlayingPositionInfo;
    private TextView mediaLengthInfo;
    private SeekBar mMusicPlayingProgressBar;
    private View pausebutton;
    private View playbutton;
	private LinearLayout mMusicBg;
    public static final byte cmdGetPlayerStatus 		= (byte)0x01;
    public static final byte cmdPlayingPosChangedNoti 	= (byte)0x02;
    private int mDefaultMusicLong     = 0; //00:00:00
    private int mDefaultPlayingTime = 0; //00:00:00
    private static int mMusicLong     = 0;
    private static int mPlayingTime = 0;
    private volatile boolean isRds = false;
    private View rootView;
    private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.bt_music, container, false);
		mPref = getActivity().getSharedPreferences("system_settings", Context.MODE_PRIVATE);
		mEditor = mPref.edit();
		mPref.edit().putBoolean("startTag", false).commit();
		initView(mRootView);
		BluetoothReceiver.registerNotifyHandler(uiHandler);
		rootView = mRootView;
		Handler openVolumeHandler = new Handler();
        openVolumeHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				openVolume();
				*/
			}
		}, 800);
		Log.d("TEST","MusicFragment onCreateView");
        
		return mRootView;
	}
	@Override
	public void onResume() {
		if (!Bluetooth.getInstance().isA2DPconnected()) {
			Bluetooth.getInstance().connectA2DP();
		}else if (!Bluetooth.getInstance().isAVRCPconnected()) {
			Bluetooth.getInstance().connectAVRCP();
		}
		super.onResume();
		Bluetooth.getInstance().musicresume();
		mPref.edit().putBoolean("startTag", false).commit();
		if(
		//mPref.getBoolean("autoPlayBTMusic", false) && 
		!Bluetooth.getInstance().isA2DPPlaying()){	
			rootView.postDelayed(new Runnable() {		
				@Override
				public void run() {
					if(!Bluetooth.getInstance().isA2DPPlaying())
						Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PLAY);
					L.e("BluetoothAvrcpCtPlayerManage.CMD_PLAY");
				}
			}, 1000);
		}
		
		{
        	Intent intent = new Intent(Context.ACTION_SOURCE_STACK_AUTOEXIT);
        	intent.putExtra(Context.EXTRA_APK_PACKAGENAME, YeconConstants.BLUETOOTH_PACKAGE_NAME);
        	intent.putExtra(Context.EXTRA_APK_ACTIVITY, YeconConstants.BLUETOOTH_START_ACTIVITY);
        	getActivity().sendOrderedBroadcast(intent,null);
        	SystemProperties.set("persist.sys.top_media", YeconConstants.BLUETOOTH_PACKAGE_NAME);
        }
		mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_null_bg);
		if(!Bluetooth.getInstance().isConnected()){
			CmnUtil.showTuoXianToast(MainBluetoothActivity.mInstance, R.string.btisnotconnected);
            mediaTitleInfo.setText("");	
        	mediaArtistInfo.setText("");	
			mediaAlbumInfo.setText("");
		}else{
	        if (Bluetooth.getInstance().getmediaTitle().isEmpty()) {
	            mediaTitleInfo.setText("");	
			}else{
		        mediaTitleInfo.setText(Bluetooth.getInstance().getmediaTitle());
				mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_bg);
			}               
	        if (Bluetooth.getInstance().getmediaArtist().isEmpty()) {
	        	mediaArtistInfo.setText("");	
			}else{
				mediaArtistInfo.setText(Bluetooth.getInstance().getmediaArtist());
				mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_bg);
			}
	        if (Bluetooth.getInstance().getmediaAlbum().isEmpty()) {
				mediaAlbumInfo.setText("");
			}else{
	        	mediaAlbumInfo.setText(Bluetooth.getInstance().getmediaAlbum());	
				mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_bg);
			}
			if(Bluetooth.getInstance().isaccoff()){
				uiHandler.sendEmptyMessageDelayed(0,6000);
			}
		}

		//FIXME:11107
		if(Bluetooth.getInstance().isspeaking()){
			L.i(this.getClass().getSimpleName() + " startDialActivity true");
			//FIXME:12004
//			startActivity(new Intent(this.getActivity(),TuoXianDialActivity.class));
			Bluetooth.handStartActivity(this.getActivity(),TuoXianDialActivity.class);
		}
		//
	}
	@Override
	public void onDestroy() {
		BluetoothReceiver.unregisterNotifyHandler(uiHandler);
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		if(!Bluetooth.getInstance().isConnected()){
			CmnUtil.showTuoXianToast(MainBluetoothActivity.mInstance, R.string.btisnotconnected);
		}
        if (!Bluetooth.getInstance().isA2DPconnected()) {
            return;
        }
        if (isRds) {
        	CmnUtil.showToast(getActivity(), R.string.str_have_rds_can_not_play);
            return;
        }
        switch (v.getId()) {
        case R.id.btn_music_play:
        case R.id.btn_music_pause:
        	/*FIXME:一汽要求只有VOL和静音钮可解除静音
				openVolume();
				*/
        	if (Bluetooth.getInstance().isA2DPPlaying()) {
        		L.e("music BluetoothAvrcpCtPlayerManage.CMD_PAUSE");
				Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
				mEditor.putBoolean("autoPlayBTMusic", false).commit();
			}else{
				L.e("music BluetoothAvrcpCtPlayerManage.CMD_PLAY");
				Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PLAY);
				mEditor.putBoolean("autoPlayBTMusic", true).commit();
			}
        	break;
        case R.id.btn_music_prev:
        	/*FIXME:一汽要求只有VOL和静音钮可解除静音
				openVolume();
				*/
			Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PREV);
        	break;
        case R.id.btn_music_next:
        	/*FIXME:一汽要求只有VOL和静音钮可解除静音
				openVolume();
				*/
			Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_NEXT);
        	break;
        }
	}
	private void initView(View rootView){
        mediaTitleInfo = (MarqueeText)rootView.findViewById(R.id.tv_music_title);
        mediaArtistInfo = (MarqueeText)rootView.findViewById(R.id.tv_music_icon_artist);
        mediaAlbumInfo = (MarqueeText)rootView.findViewById(R.id.tv_music_icon_album);
        pausebutton = rootView.findViewById(R.id.btn_music_pause);
        playbutton = rootView.findViewById(R.id.btn_music_play);
        mediaPlayingPositionInfo = (TextView)rootView.findViewById(R.id.tv_music_play_time);
        mediaLengthInfo = (TextView)rootView.findViewById(R.id.tv_music_total_time);
        mMusicPlayingProgressBar = (SeekBar)rootView.findViewById(R.id.tv_music_playing_progress);
        mMusicPlayingProgressBar.setEnabled(false);
		mMusicBg = (LinearLayout)rootView.findViewById(R.id.music_bg);
        if (!Bluetooth.getInstance().isA2DPconnected()) {
        	mMusicLong = mPlayingTime = 0;
		}
		updateProgress(cmdGetPlayerStatus, mMusicLong, mPlayingTime);
		updateTimeText(mMusicLong, mPlayingTime, (byte)0);
        pausebutton.setOnClickListener(this);
        playbutton.setOnClickListener(this);
        rootView.findViewById(R.id.btn_music_prev).setOnClickListener(this);
        rootView.findViewById(R.id.btn_music_next).setOnClickListener(this);
        rootView.findViewById(R.id.btn_music_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
	           new Thread() {     //不可在主线程中调用  
	                public void run() {  
	                    try {  
	                        Instrumentation inst = new Instrumentation();  
	                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);  
	                   } catch (Exception e) {  
	                        e.printStackTrace();  
	                    }  
	                 }  
         
	            }.start();
			}
		});
		mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_null_bg);
        
        if (Bluetooth.getInstance().getmediaTitle().isEmpty()) {
            mediaTitleInfo.setText("");	
		}else{
	        mediaTitleInfo.setText(Bluetooth.getInstance().getmediaTitle());
			mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_bg);
		}               
        if (Bluetooth.getInstance().getmediaArtist().isEmpty()) {
        	mediaArtistInfo.setText("");	
		}else{
			mediaArtistInfo.setText(Bluetooth.getInstance().getmediaArtist());
			mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_bg);
		}
        if (Bluetooth.getInstance().getmediaAlbum().isEmpty()) {
			mediaAlbumInfo.setText("");
		}else{
        	mediaAlbumInfo.setText(Bluetooth.getInstance().getmediaAlbum());	
			mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_bg);
		}
	}
	private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BluetoothReceiver.MSG_BT_STATUS_NOTIFY) {
				Intent intent = (Intent) msg.obj;
				String recievedAction = intent.getAction();
				if (recievedAction.equals(BluetoothAvrcpCtPlayerManage.ACTION_MEDIA_DATA_UPDATE)) {
					mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_null_bg);
					String mediaTitle = intent.getStringExtra(BluetoothAvrcpCtPlayerManage.MEDIA_TITLE);
					String mediaAritist = intent.getStringExtra(BluetoothAvrcpCtPlayerManage.MEDIA_ARTIST);
					String mediaAlbum = intent.getStringExtra(BluetoothAvrcpCtPlayerManage.MEDIA_ALBUM);
					if (mediaTitle == null || mediaTitle.isEmpty()) {
			            mediaTitleInfo.setText("");
					} else {
			            mediaTitleInfo.setText(mediaTitle);
						mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_bg);
					}
					
					if (mediaAritist == null || mediaAritist.isEmpty()) {
						mediaArtistInfo.setText("");
					} else {
						mediaArtistInfo.setText(mediaAritist);
						mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_bg);
					}
					if (mediaAlbum == null || mediaAlbum.isEmpty()) {
						mediaAlbumInfo.setText("");
					} else {
						mediaAlbumInfo.setText(mediaAlbum);
						mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_bg);
					}
				}else if (recievedAction.equals(BluetoothAvrcpCtPlayerManage.ACTION_PLAYBACK_DATA_UPDATE)) {
					int mediaLength = intent.getIntExtra(BluetoothAvrcpCtPlayerManage.MEDIA_LENGTH, 0);
					int mediaPosition = intent.getIntExtra(BluetoothAvrcpCtPlayerManage.MEDIA_POSITION, 0);
					mMusicLong = mediaLength;
					mPlayingTime = mediaPosition;
					byte play_status = intent.getByteExtra(BluetoothAvrcpCtPlayerManage.PLAYBACK_STATUS, (byte) 0);
			        switch (play_status) {
			        case BluetoothAvrcpCtPlayerManage.PLAYING:            
			            pausebutton.setVisibility(View.VISIBLE);
			            playbutton.setVisibility(View.GONE);         
			            break;

			        case BluetoothAvrcpCtPlayerManage.PAUSED:
			        case BluetoothAvrcpCtPlayerManage.STOPPED:            
			            pausebutton.setVisibility(View.GONE);
			            playbutton.setVisibility(View.VISIBLE); 
			            break;
			        }
					if (mediaLength == 0) {
						updateProgress(cmdGetPlayerStatus,mDefaultMusicLong, mDefaultPlayingTime);
						updateTimeText(mDefaultMusicLong, mDefaultPlayingTime,(byte) 0);
					} else {
						updateProgress(cmdGetPlayerStatus,mediaLength, mediaPosition);
						updateTimeText(mediaLength, mediaPosition,play_status);
					}
				}else if (recievedAction.equals(BluetoothAvrcpCtService.ACTION_BTMUSIC_INTERACTIVE)) {
					int param = intent.getIntExtra(BluetoothAvrcpCtService.EXTRA_BTMUSIC_INTERACTIVE, 0);
					if (param == 15) {
						isRds = true;
					} else if (param == 0) {
						isRds = false;
					}
				}else if(recievedAction.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)){
			        BTProfile profilename = (BTProfile)intent.getSerializableExtra(
			        		LocalBluetoothProfileManager.EXTRA_PROFILE);
			        int profilestate = intent.getIntExtra(
			        		LocalBluetoothProfileManager.EXTRA_NEW_STATE,LocalBluetoothProfileManager.STATE_DISCONNECTED);
			        if(profilename == null) {
			             return;
			        }
			        if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_A2DP_SINK)) {
			            switch (profilestate) {
			            case LocalBluetoothProfileManager.STATE_DISCONNECTED:
							Log.d("TEST","DISCONNECTED MsuicFragment");
							//startActivity(new Intent(self.getActivity(),BtSettingsActivity.class));
        					//getActivity().findViewById(R.id.tab_setting).performClick();
							//rootView.findViewById(R.id.btn_music_back).performClick();
							break;
			            case LocalBluetoothProfileManager.STATE_DISABLED:
							mMusicBg.setBackgroundResource(R.drawable.tuoxian_bt_music_null_bg);
			                mediaTitleInfo.setText("");
			                mediaArtistInfo.setText("");
			                mediaAlbumInfo.setText("");
			            	break;
			            }
			        }
				}
		        
			}else if(msg.what == 0){
				if(Bluetooth.getInstance().isaccoff()){
					Intent intent = new Intent(SourceManager.ACTION_SOURCE_RELEASE);
					intent.putExtra(SourceManager.EXTRA_SOURCE_NO,SourceManager.SRC_NO.bluetooth);
					getActivity().sendBroadcast(intent);
				}
			}
		}
	};

    private void updateTimeText(int song_length, int song_position, byte play_status)
    {
        String length = null;
        String pos = null;

        if (song_length != 0xFFFFFFFF){
            length = millSeconds2readableTime(song_length);
        }else{
            length = "00:00:00";
        }
        if (song_position != 0xFFFFFFFF){
            pos = millSeconds2readableTime(song_position);
        }else{
            pos = "00:00:00";
        }
        mediaLengthInfo.setText(length);
        mediaPlayingPositionInfo.setText(pos);
    }


    private void updateProgress(byte cmdType, int total_long, int playing_time){
                
        switch(cmdType){
        case cmdGetPlayerStatus:
        {    
            if ((total_long == 0xFFFFFFFF)||(playing_time == 0xFFFFFFFF)){
                total_long = 0;
                playing_time = 0;
            }
            mMusicPlayingProgressBar.setMax(total_long);
            mMusicPlayingProgressBar.setProgress(playing_time);  
        }
            break;
        case cmdPlayingPosChangedNoti:
        {    
            if (playing_time == 0xFFFFFFFF)
                playing_time = 0;
            mMusicPlayingProgressBar.setProgress(playing_time);
        }
            break;
         default:
            break;
        }
    }

    public String millSeconds2readableTime(int millseconds){

        StringBuffer dateBf = new StringBuffer();
        int totalSeconds = millseconds / 1000; 

        // HOUR_OF_DAY:24Hour 
        int hour = (totalSeconds / 60) / 60;
        if(hour <= 9){
            dateBf.append("0").append(hour+":");
        }else{
            dateBf.append(hour+":");
        }
        // Minute   
        int minute = (totalSeconds / 60) % 60;
        if (minute<=9) {
            dateBf.append("0").append(minute+":");
        }else {
            dateBf.append(minute+":");
        }
        // Seconds   
        int second = totalSeconds % 60;
        if (second<=9) {
            dateBf.append("0").append(second);
        }else {
            dateBf.append(second);
        }
        return dateBf.toString();    

    }
    public void openVolume(){
		if(rootView != null && rootView.findViewById(R.id.btn_mute_state) != null){
			((MuteTextView)rootView.findViewById(R.id.btn_mute_state)).setMute(false,400);
		}
	}
}