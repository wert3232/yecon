/**
 * @Title: VideoPlayerActivity.java
 * @Package com.yecon.musicplayer
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年5月12日 下午1:43:53
 * @version V1.0
 */
package com.yecon.video;

import static android.mcu.McuExternalConstant.MCU_ACTION_ACC_OFF;
import static android.mcu.McuExternalConstant.MCU_ACTION_ACC_ON;

import java.util.List;

import android.annotation.SuppressLint;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.mcu.McuBaseInfo;
import android.mcu.McuDeviceStatusInfo;
import android.mcu.McuExternalConstant;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window; 
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.autochips.storage.EnvironmentATC;
import com.media.constants.MediaConstants;
import com.tuoxianui.view.MuteTextView;
import com.tuoxianui.view.TopBar;
import com.yecon.common.YeconEnv;
import com.yecon.mediaprovider.YeconMediaStore;
import com.yecon.mediaservice.MediaBaseActivity;
import com.yecon.mediaservice.MediaPlayerContants;
//import com.yecon.mediaservice.MediaPlayerService;
import com.yecon.mediaservice.MediaStorage;
import com.yecon.mediaservice.MediaTrackInfo;
import com.yecon.mediaservice.MediaPlayerContants.MediaPlayerMessage;
import com.yecon.mediaservice.MediaPlayerContants.MediaType;
import com.yecon.mediaservice.MediaPlayerContants.PlayStatus; 
import com.yecon.mediaservice.MediaPlayerContants.RandomMode;
import com.yecon.mediaservice.MediaPlayerContants.RepeatMode;
import com.yecon.mediaservice.MediaPlayerContants.ServiceStatus;
import com.yecon.mediaservice.MultiMediaPlayer;
import com.yecon.video.util.L;
import com.yecon.settings.YeconSettings;
import com.yecon.video.ActivityMonitor.ActivityMonitorLisenter;
/**
 * @ClassName: VideoPlaybackMainActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年5月12日 下午1:43:53
 *
 */
@SuppressLint("DefaultLocale")
public class VideoPlaybackMainActivity extends MediaBaseActivity implements OnClickListener, OnSeekBarChangeListener, Callback, ActivityMonitorLisenter {
	
	private static final String TAG = "VideoPlayerActivity";
	
	private static final int MSG_EXIT_APP = 255;
	private static final int MSG_ATTACH_OTHER = 122;
	private static final int MSG_HIDE_BAR = 254;
	private static final int MSG_START_MEDIA = 253;
	private static final int MSG_STOP_MEDIA = 252;
	
	private static final int TIME_AUTO_FULLSREEN = 8 * 1000;

	public static final String SOUND_SETTING_PACKAGE_NAME = "com.yecon.sound.setting";
	public static final String SOUND_SETTING_STARTUP_ACTIVITY = "com.yecon.sound.setting.AudioSetting";
	public static final String PACKET_BAIDU_CARLIFE = "com.baidu.carlifevehicle";

	// layout
	private LinearLayout mLayoutPlayer;
	private LinearLayout mLayoutLoading;
	private LinearLayout mLayoutBottomBar;
	private LinearLayout mLayoutPlayInfo;
	private TopBar topBar;
	// widget
	private TextView mTvDuration;
	private TextView mTvProgress;
	private SeekBar mSeekBar;
	private TextView mTvTrackIndex;
	private TextView mTvMediaInfo;
	
	private TextView mBtnVideoCtrl;
	private TextView mBtnPrev;
	private TextView mBtnPlay;
	private TextView mBtnPause;
	private TextView mBtnNext;
	private TextView mBtnRandomON;
	private TextView mBtnRandomOFF;
	private TextView mBtnRepeatList;
	private TextView mBtnRepeatSingle;
	private TextView mBtnRepeatOFF;
	private TextView mBtnList;
	private ViewGroup mBtnMute;
	
	private SurfaceView mSurfaceFront;
	private boolean mbSurfaceFrontCreated = true;
	private SurfaceView mSurfaceRear;
	private boolean mbSurfaceRearCreated = true;
	private boolean mbStopbyBackGround = false;
	
	private VideoRearView mRearView;
	
	boolean mbSeeking = false;
	boolean mbShow = false;
	boolean isFirstDecoded = true;
	
	Handler mHandler = new Handler(this);

	private long mlLaunchTime = 0;
	
	private int IGNORE_TIME = 1000 * 5;
	
	private Toast mToast;
	private boolean isMutePause;
	
	private ActivityMonitor mVideoPlayerMonitor;
	private McuManager mMcuManager;
	private int backStauts = 0;
	private String[] storages = new String[] {
			YeconMediaStore.EXTERNAL_PATH,
			YeconMediaStore.EXT_SDCARD1_PATH,
			YeconMediaStore.EXT_SDCARD2_PATH,
			YeconMediaStore.UDISK1_PATH,
			YeconMediaStore.UDISK2_PATH,
			YeconMediaStore.UDISK3_PATH,
			YeconMediaStore.UDISK4_PATH,
			YeconMediaStore.UDISK5_PATH
	};
	private BroadcastReceiver mReceiverACC = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action != null && mRearView != null) {
				Log.e(TAG, action);
				if (MCU_ACTION_ACC_OFF.equals(action)) {
					mRearView.hide();
    			} else if (MCU_ACTION_ACC_ON.equals(action)) {
    				mRearView.show();
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
		setContentView(R.layout.yecon_video_player_activity);
		mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
		/*try {
			//mMcuManager.RPC_SetSource(McuExternalConstant.MCU_SOURCE_VIDEO, 0x00);
			mMcuManager.RPC_RequestMcuInfoChangedListener(mcuListener);
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(), e.toString());
		}*/
		
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.getInt("startTag", 0) == 1){	
			try {
				mMcuManager.RPC_SetSource(McuExternalConstant.MCU_SOURCE_SD, 0x00);
			} catch (RemoteException e) {
				Log.e(this.getClass().getName(), e.toString());
			}
        }
		
		mLayoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
		mLayoutPlayer = (LinearLayout) findViewById(R.id.layout_play);
		mLayoutBottomBar = (LinearLayout) findViewById(R.id.layout_music_op);
		mLayoutPlayInfo = (LinearLayout) findViewById(R.id.layoutPlayInfo);
		topBar = (TopBar)findViewById(R.id.topbar);
		
		mSeekBar = (SeekBar) findViewById(R.id.sb_music_playback_process);
		mTvDuration = (TextView) findViewById(R.id.tv_total_time);
		mTvProgress = (TextView) findViewById(R.id.tv_progress_time);
		mTvTrackIndex = (TextView) findViewById(R.id.tv_track_index);
		mTvMediaInfo = (TextView) findViewById(R.id.tv_notice);
		
		mBtnVideoCtrl = (TextView) findViewById(R.id.rgbControl);
		mBtnPrev = (TextView) findViewById(R.id.btn_op_pre);
		mBtnPlay = (TextView) findViewById(R.id.btn_op_play);
		mBtnPause = (TextView) findViewById(R.id.btn_op_pause);
		mBtnNext = (TextView) findViewById(R.id.btn_op_next);
		mBtnRandomON = (TextView) findViewById(R.id.btn_op_rand);
		mBtnRandomOFF = (TextView) findViewById(R.id.btn_op_rand_off);
		mBtnRepeatList = (TextView) findViewById(R.id.btn_op_loop);
		mBtnRepeatSingle = (TextView) findViewById(R.id.btn_op_loop_single);
		mBtnRepeatOFF = (TextView) findViewById(R.id.btn_op_loop_off);
		mBtnList = (TextView) findViewById(R.id.btn_op_list);
		mBtnMute = (ViewGroup) findViewById(R.id.btn_mute);

		//FIXME:111001
		TopBar topBar = (TopBar) findViewById(R.id.topbar);
		topBar.setTopBarBackCall(new TopBar.TopBarBackCall() {
			@Override
			public void onHomeReturn() {
				if(mHandler != null){
					mHandler.sendEmptyMessage(MSG_EXIT_APP);
				}
			}
		});//

		mBtnMute.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		mBtnPrev.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnPause.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnRandomON.setOnClickListener(this);
		mBtnRandomOFF.setOnClickListener(this);
		mBtnRepeatList.setOnClickListener(this);
		mBtnRepeatSingle.setOnClickListener(this);
		mBtnRepeatOFF.setOnClickListener(this);
		mBtnList.setOnClickListener(this);
		mBtnVideoCtrl.setOnClickListener(this);

		mSeekBar.setOnSeekBarChangeListener(this);
		mLayoutPlayer.setVisibility(View.VISIBLE);
		
		mHandler.removeMessages(MSG_EXIT_APP);
		
		if (SystemProperties.getBoolean(YeconConstants.PROPERTY_QB_POWERON, false)) {
			// Quick Power ON
			mlLaunchTime = SystemClock.uptimeMillis();
		}
		
		CreateFrontDisplay();
		CreateRearDisplay();
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(MCU_ACTION_ACC_ON);
        filter.addAction(MCU_ACTION_ACC_OFF);
        registerReceiver(mReceiverACC, filter);
        
        mVideoPlayerMonitor = new ActivityMonitor(this);
		mVideoPlayerMonitor.init();
		mVideoPlayerMonitor.setActivityMonitorLisenter(getPackageName(), this);
		recoverDevice();
		
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(MediaConstants.DO_PLAY);
		controlFilter.addAction(MediaConstants.DO_PAUSE);
		controlFilter.addAction(MediaConstants.DO_PREV);
		controlFilter.addAction(MediaConstants.DO_NEXT);
		controlFilter.addAction(MediaConstants.DO_STOP); 
		controlFilter.addAction(Context.ACTION_VIDEO_DO_STOP); 
		controlFilter.addAction(MediaConstants.CURRENT_MEDIA);	
		registerReceiver(controlReceiver, controlFilter);
		
		IntentFilter otherfilter = new IntentFilter();
		otherfilter.addAction(MediaConstants.ACTION_SOURCE_ATTACH_OTHER_STORAGE);
		otherfilter.addAction(MediaConstants.DO_EXIT_APP);
		otherfilter.addAction("action.hzh.media.power.on");
		otherfilter.addAction("action.hzh.mediaPlayer.exitApp");
		otherfilter.addAction("android.media.VOLUME_CHANGED_ACTION");
		registerReceiver(otherReceiver, otherfilter);
		
		Handler openVolumeHandler = new Handler();
        openVolumeHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				openVolume();
				*/
			}
		}, 800); 
	}
	
	/**
	 * @Title: recoverDevice
	 * @Description: 加载设备
	 * 1, intent制定设备
	 * 2, intent未制定,播放服务未绑定
	 * 3, 服务已经绑定但是不存在播放的设备
	 * 1,2,3都需要显示loading
	 */
	protected void recoverDevice() {
		Log.e(TAG, "recoverDevice");
		try {
			Intent intent = getIntent();
			String device = null;
			if (intent != null) {
				EnvironmentATC atc = new EnvironmentATC(this);
				device = intent.getStringExtra("plugindevice");
				//Toast.makeText(this, "device1:" + device + "  " + SystemProperties.get("persist.sys.video_init_state", "empty"), Toast.LENGTH_LONG).show();
				if (device == null) {
					if (isBindService()) {
						if (getMediaSevice().getPlayingStorage() == null) {
							// play last memory device
							device = MediaPlayerContants.LAST_MEMORY_DEVICE;
						}
					} else {
						device = MediaPlayerContants.LAST_MEMORY_DEVICE;
					}
					
					String devPath = SystemProperties.get("persist.sys.video_init_state", "empty");
					if(!"empty".equals(devPath)){
						boolean isExist = YeconEnv.checkStorageExist(atc, devPath);
						if(isExist){
							device = SystemProperties.get("persist.sys.video_init_state", "empty");
						}
					}
				}else{
					String devPath = SystemProperties.get("persist.sys.video_init_state", "empty");
					boolean isExist = YeconEnv.checkStorageExist(atc, devPath);
					if(isExist){
						SystemProperties.set("persist.sys.music_init_state", device);
					}
					//Toast.makeText(this, "device2:" + SystemProperties.get("persist.sys.music_init_state", "empty") + "  " + isExist, Toast.LENGTH_LONG).show();
				}
				SystemProperties.set("persist.sys.video_init_state", "empty");
			}
			//Toast.makeText(this, "device3:" + device, Toast.LENGTH_LONG).show();
			
			if (device != null && device.length() > 0) {
				Log.e(TAG, "recoverDevice -> loading " + device);
				//mLayoutLoading.setVisibility(View.VISIBLE);
				AttachStorage(device, MediaType.MEDIA_VIDEO);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e(TAG, "onPause");
		mbShow = false;
		try {
			if (isBindService()) {
				if (getMediaSevice().getPlayStatus() == PlayStatus.STARTED) {
					getMediaSevice().requestSaveLastMemory();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onResume");
		super.onResume();
		Intent intent = new Intent(MediaConstants.CURRENT_MEDIA_IS_VIDEO);
		try {
			List<MediaStorage> lsStorages = getMediaSevice().getStorageList();
			MediaStorage playStorage = null;
			for(MediaStorage storage : lsStorages){
				if(storage.getPlaying()){
					playStorage = storage;
					break;
				}
			}
			if(playStorage != null){
				//FIX ME YeconMedia
				/*String path = playStorage.getPath();
				if(MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_USB.equals(MediaPlayerService.getMediaStorageType(path))){
					intent.putExtra(MediaConstants.EXTRA_PALYING_DEVICE, MediaConstants.DEVICE_USB);
				}else if(MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_SD.equals(MediaPlayerService.getMediaStorageType(path))){
					intent.putExtra(MediaConstants.EXTRA_PALYING_DEVICE, MediaConstants.DEVICE_SD);
				}*/
			}else{
				if(YeconEnv.hasSD(this) || YeconEnv.hasUsb(this)){
					Intent intent2 = new Intent(MediaConstants.ACTION_SOURCE_ATTACH_OTHER_STORAGE);
					intent2.putExtra(Context.EXTRA_APK_PACKAGENAME, YeconConstants.MUSIC_PACKAGE_NAME);
					sendOrderedBroadcast(intent2, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sendBroadcast(intent, null);

		mbShow = true;
		YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);
		if (mLayoutLoading != null) {
			boolean bScaning = isScaningAttachedDevice();
			mLayoutLoading.setVisibility(bScaning ? View.VISIBLE : View.GONE);
			if (!bScaning) {
				showBar(true);
				/*showVideo();
				ForceUpdateTrack();	*/
			} else {
				Log.e(TAG, "onResume:scaning -> loading");
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * Title: onClick
	 * Description:
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		boolean bPreFullHideBar = true;
		try {
			switch (v.getId()) {
			case R.id.btn_op_list:
				startActivity(new Intent(this, VideoListActivity.class));
				break;
			case R.id.btn_op_loop:
			case R.id.btn_op_loop_off:
			case R.id.btn_op_loop_single:
				getMediaSevice().requestRepeat();
				break;
			case R.id.btn_op_next:
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				openVolume();
				*/
				getMediaSevice().requestNext();
				break;
			case R.id.btn_op_play:
			case R.id.btn_op_pause:
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				openVolume();
				*/
				getMediaSevice().requestPause();
				break;
			case R.id.btn_op_pre:
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				openVolume();
				*/
				prev();
				break;
			case R.id.btn_op_rand:
			case R.id.btn_op_rand_off:
				getMediaSevice().requestRandom();
				break;
				
			case R.id.surfaceView:
				bPreFullHideBar = false;
				if (mLayoutBottomBar.getVisibility() == View.VISIBLE) {
					showBar(false);
				} else {
					showBar(true);
				}
				break;
				
			case R.id.rgbControl:
				YeconSettings.callVideoRgbSettingActivity(this, YeconSettings.RGBTYPE.USB);
				break;
			case R.id.back:
//				onBackPressed();
				startActivity(new Intent(this, VideoListActivity.class));
				break;
			case R.id.btn_mute:
				((MuteTextView)findViewById(R.id.btn_mute_state)).toggle();
				break;
			default:
				bPreFullHideBar = false;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bPreFullHideBar) {
			showBar(true);
		}
	}

	/**
	 * Title: handleMessage
	 * Description:
	 * @param msg
	 * @return
	 * @see android.os.Handler.Callback#handleMessage(android.os.Message)
	 */
	@SuppressLint("DefaultLocale")
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case MediaPlayerMessage.UPDATE_SERVICE_STATE:
			if (msg.arg1 == ServiceStatus.SCANED) {
				mTvMediaInfo.setText("");
				ForceUpdateTrack();
				mLayoutLoading.setVisibility(View.GONE);
				
				try {
					if(getMediaSevice().getRepeatStatus() != RepeatMode.ALL || getMediaSevice().getRandomStatus() != RandomMode.OFF){	
						setPlayMode(RepeatMode.ALL, RandomMode.OFF);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(msg.arg1 == ServiceStatus.LOST_STORAGE){
				Bundle data = msg.getData();
				if (data != null) {
					String pathNew = data
							.getString(MediaPlayerContants.PATH);
					/*if (pathNew != null) {
						L.e(TAG, "storage pulg out : " + pathNew);
						//bExistApp = false;
					}*/
					try {
						if (getMediaSevice().getPlayStatus() == PlayStatus.IDLE) {
							mHandler.sendEmptyMessage(MSG_ATTACH_OTHER);
						}
					} catch (Exception e) {
						L.e(e.toString());
					}
				}
			}
			else if (
					   msg.arg1 == ServiceStatus.NO_STORAGE ||
					   msg.arg1 == ServiceStatus.LOST_AUDIO_FOCUS ||
					   msg.arg1 == ServiceStatus.QB_POWER) {
				boolean bExistApp = true;
				try {
					String tips = null;
					if (!isBindService() || getMediaSevice().getPlayingStorage() == null) {
						if (msg.arg1 == ServiceStatus.NO_STORAGE) {
							tips = getString(R.string.no_storage);
						} else if (msg.arg1 == ServiceStatus.LOST_AUDIO_FOCUS) {
//							tips = getString(R.string.lost_audio_focus);
							Log.e(TAG, "lost audio focus!!! exit");
						} else {
//							tips = getString(R.string.unmount_disk);
						} 
					} else if (msg.arg1 == ServiceStatus.LOST_AUDIO_FOCUS) {
//						tips = getString(R.string.lost_audio_focus);
						Log.e(TAG, "lost audio focus!!! exit");
					} else if (msg.arg1 == ServiceStatus.QB_POWER) {
						Log.e(TAG, "QB_POWER_OFF!!!");
						//FIXME:2017/03/22 add 取消假关机关闭APP
						/*bExistApp = false;
						stop();*/
						//
					} 
					if (tips != null) {
						Toast.makeText(this,
								tips + "," + getString(R.string.exit_app),
								Toast.LENGTH_SHORT).show();
					}
					if (bExistApp) {
						mHandler.sendEmptyMessage(MSG_EXIT_APP);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.arg1 == ServiceStatus.EMPTY_STORAGE) {
				mLayoutLoading.setVisibility(View.GONE);
				mTvMediaInfo.setText("");
				mTvMediaInfo.removeCallbacks(mMediaInfoEmptyTread);
				mTvMediaInfo.postDelayed(mMediaInfoEmptyTread, 800);
				ForceUpdateTrack();
			} else if (msg.arg1 == ServiceStatus.PLAYED) {
				showToast(getString(R.string.finish_play_list));
				onClick(mBtnList);
			} else if (msg.arg1 == ServiceStatus.SCANING) {
				try {
					MediaStorage storage = getMediaSevice().getPlayingStorage();
					String pathOld = null;
					Bundle data = msg.getData();
					if (data != null) {
						String pathNew = data.getString(MediaPlayerContants.PATH);
						if (storage != null && pathNew != null) {
							pathOld = storage.getPath();
							if (!pathNew.equals(pathOld)) {
								Log.e(TAG, "mlLaunchTime:" + mlLaunchTime);
								Log.e(TAG, "SystemClock.uptimeMillis():" + SystemClock.uptimeMillis());
								if (SystemClock.uptimeMillis() - mlLaunchTime > IGNORE_TIME) {
									AttachStorage(pathNew, MediaType.MEDIA_VIDEO);
									Log.e(TAG, "handlemessage:scaning -> loading");
									if (isScaningAttachedDevice()) {
										showBar(true);
										//mLayoutLoading.setVisibility(View.VISIBLE);
									}
								} else {
									Log.e(TAG, "handlemessage:system is QB_POWERON, ignore mount");
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.arg1 == ServiceStatus.SCAN_TIMEOUT) {
				showToast(getString(R.string.scan_timeout));
			}
			Log.e(TAG, "UPDATE_SERVICE_STATE:" + msg.arg1);
			break;
		case MediaPlayerMessage.UPDATE_LIST_DATA:
			ForceUpdateTrack();
			break;
		case MediaPlayerMessage.UPDATE_PLAY_STATE:
			// update track info 
			if (msg.arg1 == PlayStatus.DECODED ||
				msg.arg1 == PlayStatus.STARTED) {
				if (msg.arg1 == PlayStatus.DECODED) {
//					mSeekBar.setProgress(0);
					if(isFirstDecoded){
						showBar(true);
						isFirstDecoded = false;
					}
					showVideo();
				}
				//FIXME:11136 +
				else{
					mHandler.removeMessages(MSG_START_MEDIA);
					mHandler.sendEmptyMessageDelayed(MSG_START_MEDIA,100);
				}
				L.e("surface msg.arg1 : " + msg.arg1);
				//
				ForceUpdateTrack();
			} else if (msg.arg1 == PlayStatus.ERROR) {
				// play error
				try {
					ForceUpdateTrack();
					int iRepeat = RepeatMode.ALL;
					if (isBindService()) {
						iRepeat = getMediaSevice().getRepeatStatus();
					}
					if (iRepeat == RepeatMode.SINGLE) {
						showToast(getString(R.string.play_repeat_one_failed));
						onClick(mBtnList);
					} else {
						showToast(getString(R.string.play_failed));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.arg1 == PlayStatus.IDLE) {
				mTvMediaInfo.removeCallbacks(mMediaInfoEmptyTread);
				mTvMediaInfo.setText("");
			}
			
			UpdatePlayPause(msg.arg1);
			break;
		case MediaPlayerMessage.UPDATE_PLAY_PROGRESS:
			if (!mbSeeking) {
				mTvProgress.setText(formatData(msg.arg1));
				mTvDuration.setText(formatData(msg.arg2));
				mSeekBar.setProgress(msg.arg1);
				mSeekBar.setMax(msg.arg2);
			}
			break;
			
		case MediaPlayerMessage.UPDATE_MEDIA_INTO:
			String strMediaInfo = null;
			if (msg.arg1 == MultiMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
				showVideo();
			} else if (msg.arg1 == MultiMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
				strMediaInfo = getString(R.string.not_support_seek);
			} else if (msg.arg1 == MultiMediaPlayer.MEDIA_INFO_UNSUPPORTED_AUDIO) {
				strMediaInfo = getString(R.string.not_support_audio);
				if (msg.arg2 == MultiMediaPlayer.CAP_AUDIO_BITRATE_UNSUPPORT) {
					strMediaInfo = getString(R.string.not_support_audio_bitrate);
				} else if (msg.arg2 == MultiMediaPlayer.CAP_AUDIO_CODEC_UNSUPPORT) {
					strMediaInfo = getString(R.string.not_support_audio_codec);
				} else if (msg.arg2 == MultiMediaPlayer.CAP_AUDIO_PROFILE_LEVEL_UNSUPPORT) {
					strMediaInfo = getString(R.string.not_support_audio_profilelevel);
				} else if (msg.arg2 == MultiMediaPlayer.CAP_AUDIO_SAMPLERATE_UNSUPPORT) {
					strMediaInfo = getString(R.string.not_support_audio_samplingrate);
				}
			} else if (msg.arg1 == MultiMediaPlayer.MEDIA_INFO_UNSUPPORTED_VIDEO) {
				strMediaInfo = getString(R.string.not_support_video);
				if (msg.arg2 == MultiMediaPlayer.CAP_VIDEO_BITRATE_UNSUPPORT) {
					strMediaInfo = getString(R.string.not_support_video_bitrate);
				} else if (msg.arg2 == MultiMediaPlayer.CAP_VIDEO_CODEC_UNSUPPORT) {
					strMediaInfo = getString(R.string.not_support_video_codec);
				} else if (msg.arg2 == MultiMediaPlayer.CAP_VIDEO_FRAMERATE_UNSUPPORT) {
					strMediaInfo = getString(R.string.not_support_video_bitrate);
				} else if (msg.arg2 == MultiMediaPlayer.CAP_VIDEO_PROFILE_LEVEL_UNSUPPORT) {
					strMediaInfo = getString(R.string.not_support_video_profilelevel);
				} else if (msg.arg2 == MultiMediaPlayer.CAP_VIDEO_RESOLUTION_UNSUPPORT) {
					strMediaInfo = getString(R.string.not_support_video_resolution);
				}
			} else if (msg.arg1 == MultiMediaPlayer.MEDIA_INFO_DIVXDRM_ERROR) {
				strMediaInfo = getString(R.string.playback_failed);
			}
			if (strMediaInfo != null) {
				if (!mVideoPlayerMonitor.isForeground()) {
					showToast(strMediaInfo);
				}
			}
			if (mTvMediaInfo != null) {
				mTvMediaInfo.removeCallbacks(mMediaInfoEmptyTread);
				if (strMediaInfo != null) {
					mTvMediaInfo.setText(strMediaInfo);
				} else {
					mTvMediaInfo.setText(null);
				}
			}
			break;
			
		case MediaPlayerMessage.UPDATE_RANDOM_STATE:
			UpdateRandom(msg.arg1);
			break;
			
		case MediaPlayerMessage.UPDATE_REPEAT_STATE:
			UpdateRepeat(msg.arg1);
			break;
			
		case MSG_EXIT_APP:
			Log.e(TAG, "MSG_EXIT_APP");
			exitApp();
			break;
		case MSG_ATTACH_OTHER:{
			String existStorage = getOneExistStorage();
			if(!TextUtils.isEmpty(existStorage)){
				L.e("MSG_ATTACH_OTHER : " + existStorage);
				AttachStorage(existStorage, MediaType.MEDIA_VIDEO);
				/*	try {
						if (getMediaSevice().getPlayStatus() == PlayStatus.IDLE) {
							mTvProgress.setText(formatData(0));
							mTvDuration.setText(formatData(0));
							mSeekBar.setMax(0);
							mSeekBar.setEnabled(false);
							mTvTrackIndex.setText(getString(R.string.videobrowserlabel));
						}
					} catch (Exception e) {
						L.e(e.toString());
					}*/
			}else{
				L.e("MSG_ATTACH_OTHER : MSG_EXIT_APP");
				mHandler.sendEmptyMessage(MSG_EXIT_APP);
			}
		}
		break;
		case MSG_HIDE_BAR:
			showBar(false);
			break;
			
		case MSG_START_MEDIA:
			L.e("MSG_START_MEDIA: start()");
			start();
			break;
			
		case MSG_STOP_MEDIA:
			/*FIXME:11136  if(backStauts == 1){
				stop();
			}*/
			stop();
			break;
			
		default:
			break;
		}
		return false;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (mbSeeking) {
			mTvProgress.setText(formatData(progress));
			mSeekBar.setProgress(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.e(TAG, "[onStartTrackingTouch] start get seek to progress");
		mbSeeking = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int iProgress = seekBar.getProgress();
		Log.e(TAG, "[onStopTrackingTouch] progress:" + iProgress);
		try {
			if (isBindService()) {
				getMediaSevice().requestSeek(iProgress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mbSeeking = false;
	}
	
	// 更新曲目信息
	public void UpdateTrackInfo() {
		try {
			if (isBindService()) {
				MediaTrackInfo cv = getMediaSevice().getPlayingFileInfo();
				if (cv.getTrackTotal() != 0) {
					int iDuration = cv.getDuration();
					mTvDuration.setText(formatData(iDuration));
					String strTrackIndex = String.format("%d/%d", cv.getTrackID() + 1, cv.getTrackTotal());
					mTvTrackIndex.setText(strTrackIndex);
					mSeekBar.setMax(iDuration);
					mSeekBar.setEnabled(iDuration != 0);
					if (iDuration == 0) {
						mTvProgress.setText(formatData(0));
					}
				} else {
					mTvMediaInfo.setText("");
					mTvMediaInfo.removeCallbacks(mMediaInfoEmptyTread);
					mTvMediaInfo.postDelayed(mMediaInfoEmptyTread, 800);
					mTvProgress.setText(formatData(0));
					mTvDuration.setText(formatData(0));
					mSeekBar.setMax(0);
					mSeekBar.setEnabled(false);
					mTvTrackIndex.setText(getString(R.string.videobrowserlabel));
					UpdatePlayPause(PlayStatus.PAUSED);
				}
				if (mbSeeking) {
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 更新随机状态
	public void UpdateRandom(int iState) {
		if (iState == RandomMode.OFF) {
//			mBtnRandomOFF.setVisibility(View.VISIBLE);
			mBtnRandomON.setVisibility(View.GONE);
		} else {
			mBtnRandomOFF.setVisibility(View.GONE);
//			mBtnRandomON.setVisibility(View.VISIBLE);
		}
	}
	
	// 更新循环状态
	public void UpdateRepeat(int iState) {
		Log.e(TAG, "UpdateRepeat " + iState);
		if (iState == RepeatMode.ALL) {
//			mBtnRepeatList.setVisibility(View.VISIBLE);
			mBtnRepeatSingle.setVisibility(View.GONE);
			mBtnRepeatOFF.setVisibility(View.GONE);
		} else if (iState == RepeatMode.SINGLE) {
			mBtnRepeatList.setVisibility(View.GONE);
//			mBtnRepeatSingle.setVisibility(View.VISIBLE);
			mBtnRepeatOFF.setVisibility(View.GONE);
		} else if (iState == RepeatMode.OFF) {
			mBtnRepeatList.setVisibility(View.GONE);
			mBtnRepeatSingle.setVisibility(View.GONE);
//			mBtnRepeatOFF.setVisibility(View.VISIBLE);
		}
	}

	// 更新播放状态
	private void UpdatePlayPause(int iState) {
		// TODO Auto-generated method stub
		if (iState == PlayStatus.PAUSED) {
			mBtnPlay.setVisibility(View.VISIBLE);
			mBtnPause.setVisibility(View.GONE);
		} else if (iState == PlayStatus.STARTED) {
			mBtnPlay.setVisibility(View.GONE);
			mBtnPause.setVisibility(View.VISIBLE);
			if(isMutePause){
				if(SystemProperties.getInt("persist.sys.hasVolume", 0)==0)
					((AudioManager)getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, Context.DEFUALT_VOLUME, 0);
			}
			if(0!=((AudioManager)getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC))
				isMutePause = false;
		}
		
		if (iState == PlayStatus.PAUSED) {
			sendBroadcast(new Intent(MediaConstants.TO_PAUSE), null);
		} else if (iState == PlayStatus.STARTED) {
			sendBroadcast(new Intent(MediaConstants.TO_PLAY), null);
		}else {
			sendBroadcast(new Intent(MediaConstants.TO_STOP), null);
		}
	}
	
	// 强制刷新曲目信息
	private void ForceUpdateTrack () {
		try {
			if (isBindService()) {
				UpdateTrackInfo();
				UpdateRandom(getMediaSevice().getRandomStatus());
				UpdateRepeat(getMediaSevice().getRepeatStatus());
				UpdatePlayPause(getMediaSevice().getPlayStatus());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onBackPressed");
		super.onBackPressed();
		mHandler.sendEmptyMessage(MSG_EXIT_APP);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		ReleaseRearDisplay();
		unregisterReceiver(mReceiverACC);
		unregisterReceiver(controlReceiver);
		unregisterReceiver(otherReceiver);
		/*try {
			mMcuManager.RPC_RemoveMcuInfoChangedListener(mcuListener);
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(), e.toString());
		}*/
		mVideoPlayerMonitor.deinit();
	}
	
	public void showToast(String tips) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
		}
		if (tips != null) {
			mToast.setText(tips);
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.show();
		} else {
			mToast.cancel();
		}
	}
	
	public void showBar(boolean bShow) {
		if (isBindService()) {
			try {
				if (getMediaSevice().getMediaType() == MediaType.MEDIA_VIDEO) {
					Window window = getWindow();
			        WindowManager.LayoutParams params = window.getAttributes();
					if (bShow) {
						mHandler.removeMessages(MSG_HIDE_BAR);
						mHandler.sendEmptyMessageDelayed(MSG_HIDE_BAR, TIME_AUTO_FULLSREEN);
//FIXME:						params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);						
						mLayoutBottomBar.setVisibility(View.VISIBLE);
						mLayoutPlayInfo.setVisibility(View.VISIBLE);
						mTvTrackIndex.setVisibility(View.VISIBLE);
						mBtnVideoCtrl.setVisibility(View.INVISIBLE);
						topBar.setVisibility(View.VISIBLE);
						
					} else {
						MediaTrackInfo cv;
						cv = getMediaSevice().getPlayingFileInfo();
						if (cv.getTrackTotal() != 0) {
//FIXME:							params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
							mLayoutBottomBar.setVisibility(View.INVISIBLE);
							mLayoutPlayInfo.setVisibility(View.INVISIBLE);
							mTvTrackIndex.setVisibility(View.INVISIBLE);
							mBtnVideoCtrl.setVisibility(View.INVISIBLE);
							topBar.setVisibility(View.INVISIBLE);
						}
					}
					window.setAttributes(params);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressLint("NewApi")
	public void CreateFrontDisplay() {
		mSurfaceFront = (SurfaceView) findViewById(R.id.surfaceView);
		mSurfaceFront.setBackgroundResource(android.R.color.transparent);
		mSurfaceFront.getHolder().addCallback(this);
		mSurfaceFront.setOnClickListener(this);
	}
	
	public void CreateRearDisplay() {
		DisplayManager mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        String displayCategory = DisplayManager.DISPLAY_CATEGORY_PRESENTATION;
        Display[] displays = mDisplayManager.getDisplays(displayCategory);
        if (displays.length >= 1) {
    		mRearView = new VideoRearView(getApplicationContext(), displays[0]);
        } else {
        	Log.e(TAG, "initRearSurface: no have rear displays");
        }
        if (mRearView != null) {
			mRearView.show();
			mSurfaceRear = mRearView.getSurfaceView();
			mSurfaceRear.getHolder().addCallback(this);
		}
	}
	
	public void ReleaseRearDisplay() {
		if (mRearView != null) {
			mRearView.hide();
			mSurfaceRear = null;
			if (isBindService()) {
				try {
					getMediaSevice().requestSetRearDisplay(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mRearView.cancel();
			mRearView = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceCreated");
		if (isBindService()) {
			synchronized (TAG) {
				try {
					if (holder.equals(mSurfaceFront.getHolder())) {
						mbSurfaceFrontCreated = true;
						getMediaSevice().requestSetFrontDisplay(holder);
					} else if (holder.equals(mSurfaceRear.getHolder())) {
						mbSurfaceRearCreated = true;
						getMediaSevice().requestSetRearDisplay(holder);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceChanged:format=" + format + ",width=" + width + ",height=" + height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceDestroyed");
		if (isBindService()) {
			synchronized (TAG) {
				try {
					if (holder.equals(mSurfaceFront.getHolder())) {
						mbSurfaceFrontCreated = false;
						getMediaSevice().requestSetFrontDisplay(null);
					} else if (holder.equals(mSurfaceRear.getHolder())) {
						mbSurfaceRearCreated = false;
						getMediaSevice().requestSetRearDisplay(null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void showVideo() {
		try {
			synchronized (TAG) {
				if (mbSurfaceFrontCreated) {
					getMediaSevice().requestSetFrontDisplay(mSurfaceFront.getHolder());
				}
				if (mbSurfaceRearCreated && mSurfaceRear != null) {
					getMediaSevice().requestSetRearDisplay(mSurfaceRear.getHolder());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 // ALL → OFF → SINGLE → ALL
	public void setPlayMode (int repeatType,int randomType) throws Exception{
		int randomState = 	getMediaSevice().getRandomStatus();
		int repeateState = 	getMediaSevice().getRepeatStatus();
		if(repeateState == repeatType){
			if(repeateState != RepeatMode.SINGLE){
				if(randomState != randomType){
					getMediaSevice().requestRandom();
				}
			}
		}
		else if(repeateState == RepeatMode.ALL){
			switch (repeatType) {
				case RepeatMode.OFF:
					getMediaSevice().requestRepeat();
					
					randomState = 	getMediaSevice().getRandomStatus();
					if(randomState != randomType){
						getMediaSevice().requestRandom();
					}
					break;
				case RepeatMode.SINGLE:	
					getMediaSevice().requestRepeat();
					getMediaSevice().requestRepeat();
					break;
			}
		}else if(repeateState == RepeatMode.OFF){
			switch (repeatType) {
				case RepeatMode.ALL:
					getMediaSevice().requestRepeat();
					getMediaSevice().requestRepeat();
					
					randomState = 	getMediaSevice().getRandomStatus();
					if(randomState != randomType){
						getMediaSevice().requestRandom();
					}
					break;
				case RepeatMode.SINGLE:	
					getMediaSevice().requestRepeat();
					break;
			}
		}else if(repeateState == RepeatMode.SINGLE){
			switch (repeatType) {
				case RepeatMode.OFF:
					getMediaSevice().requestRepeat();
					getMediaSevice().requestRepeat();
					
					randomState = 	getMediaSevice().getRandomStatus();
					if(randomState != randomType){
						getMediaSevice().requestRandom();
					}
					break;
				case RepeatMode.ALL:
					getMediaSevice().requestRepeat();
					break;
			}
		}
	}
	@Override
	public void onForeGround() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onForeGround");
		mHandler.sendEmptyMessageDelayed(MSG_START_MEDIA, 1000);
	}

	@Override
	public void onBackGround() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onBackGround");
		/*mHandler.removeMessages(MSG_START_MEDIA);
		mHandler.sendEmptyMessage(MSG_STOP_MEDIA);*/
	}
	
	public void start() {
		synchronized (TAG) {
			YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);
			if (mLayoutLoading != null) {
				boolean bScaning = isScaningAttachedDevice();
				if (!bScaning) {
					showBar(true);
					showVideo();
					ForceUpdateTrack();	
				} else {
					Log.e(TAG, "onResume:scaning -> loading");
				}
			}
			
			if (isBindService() && mbStopbyBackGround) {
				mbStopbyBackGround = false;
				try {
					getMediaSevice().requestStart();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void stop() {
		synchronized (TAG) {
			if (isBindService() && !mbStopbyBackGround) {
				mbStopbyBackGround = true;
				try {
					getMediaSevice().requestStop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void prev(){
		if (isBindService()) {
			try {
				boolean rePlay = (mSeekBar != null) && mSeekBar.getProgress() > 3;
				if(rePlay){
					getMediaSevice().requestSeek(0);
				}else{
					getMediaSevice().requestPrev();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void openVolume(){
		if(findViewById(R.id.btn_mute_state) != null){
			((MuteTextView)findViewById(R.id.btn_mute_state)).setMute(false,400);
		}
	}
	
	BroadcastReceiver otherReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			L.e(this.getClass().getName(), "action:" + action);
			if(MediaConstants.ACTION_SOURCE_ATTACH_OTHER_STORAGE.equalsIgnoreCase(action)){
				String existStorage = getOneExistStorage();
				if(!TextUtils.isEmpty(existStorage)){
					try {
						if (getMediaSevice().getPlayStatus() == PlayStatus.IDLE) {
							AttachStorage(existStorage, MediaType.MEDIA_VIDEO);
						}
					} catch (Exception e) {
						L.e(e.toString());
					}
					/*try {
						if (getMediaSevice().getPlayStatus() == PlayStatus.IDLE) {
							mTvProgress.setText(formatData(0));
							mTvDuration.setText(formatData(0));
							mSeekBar.setMax(0);
							mSeekBar.setEnabled(false);
							mTvTrackIndex.setText(getString(R.string.videobrowserlabel));
						}
					} catch (Exception e) {
						L.e(e.toString());
					}*/
				}else{
					L.e("MSG_ATTACH_OTHER : MSG_EXIT_APP");
					mHandler.sendEmptyMessage(MSG_EXIT_APP);
				}
			}else if(MediaConstants.DO_EXIT_APP.equalsIgnoreCase(action)){
				mHandler.sendEmptyMessage(MSG_EXIT_APP);
			}else if("action.hzh.media.power.on".equals(action)){
				//FIXME:2017/03/22 add
				//start();
				//
			}else if("action.hzh.mediaPlayer.exitApp".equals(action)){
				mHandler.sendEmptyMessage(MSG_EXIT_APP);
			}else if(action.equals("android.media.VOLUME_CHANGED_ACTION")){
				try{
					int volume = ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC) ;
					int iState = getMediaSevice().getPlayStatus();
					if (iState == PlayStatus.PAUSED && isMutePause && volume > 0) {
						/*FIXME:一汽要求只有VOL和静音钮可解除静音
						openVolume();
						*/
						getMediaSevice().requestPause();
						isMutePause = false;
					} else if (iState == PlayStatus.STARTED && !isMutePause && volume == 0) {
						//openVolume();
						isMutePause = true;
						getMediaSevice().requestPause();	
					}else if (iState == PlayStatus.STARTED && volume > 0) {
						isMutePause = false;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}};
	
	BroadcastReceiver controlReceiver = new BroadcastReceiver() {
			
			@Override
		public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				
				if(Context.ACTION_VIDEO_DO_STOP.equals(action)){
					L.e(Context.ACTION_VIDEO_DO_STOP);
					stop();
					return;
				}
				String mediaType = intent.getStringExtra("media_type");
				L.e(this.getClass().getName(), "action:" + action + "  mediaType:" + mediaType);
				if(MediaConstants.CURRENT_MEDIA_IS_VIDEO.equals(mediaType)){
					try {	
						if		(MediaConstants.DO_PLAY.equalsIgnoreCase(action)){
							int iState = getMediaSevice().getPlayStatus();
						if (iState == PlayStatus.PAUSED) {
							//openVolume();
								getMediaSevice().requestPause();
							} else if (iState == PlayStatus.STARTED) {		
							}else if(iState == PlayStatus.IDLE){
								start();
							}
						}else if(MediaConstants.DO_PAUSE.equalsIgnoreCase(action)){
							int iState = getMediaSevice().getPlayStatus();
							if (iState == PlayStatus.PAUSED) {
							getMediaSevice().requestPause();
							} else if (iState == PlayStatus.STARTED) {
							//openVolume();
								getMediaSevice().requestPause();	
							}
						}else if(MediaConstants.DO_STOP.equalsIgnoreCase(action)){
							/*int iState = getMediaSevice().getPlayStatus();
							if (iState == PlayStatus.STARTED || iState == PlayStatus.PAUSED) {*/
							stop();
							/*}*/
						}
						else if(MediaConstants.DO_PREV.equalsIgnoreCase(action)){
							/*FIXME:一汽要求只有VOL和静音钮可解除静音
							openVolume();
							*/
							prev();
						}else if(MediaConstants.DO_NEXT.equalsIgnoreCase(action)){
							/*FIXME:一汽要求只有VOL和静音钮可解除静音
							openVolume();
							*/
							getMediaSevice().requestNext();
						}else if(MediaConstants.CURRENT_MEDIA.equalsIgnoreCase(action)){
							int iState = getMediaSevice().getPlayStatus();
							if (iState == PlayStatus.PAUSED) {
								sendBroadcast(new Intent(MediaConstants.TO_PAUSE), null);
							} else if (iState == PlayStatus.STARTED) {
								sendBroadcast(new Intent(MediaConstants.TO_PLAY), null);
							}else {
								sendBroadcast(new Intent(MediaConstants.TO_STOP), null);
							}
						}
					} catch (Exception e) {
						L.e(this.getClass().getName(), e.toString()); 
					}
				}
			}
	};
	McuListener mcuListener = new McuListener() {
		
		@Override
		public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {
			if(infoType == McuExternalConstant.MCU_DEVICE_STATUS_INFO_TYPE){
				McuDeviceStatusInfo mcuDeviceStatusInfo = mcuBaseInfo.getDeviceStatusInfo();
				
				if(backStauts != mcuDeviceStatusInfo.getBackcarStatus()){
					backStauts = mcuDeviceStatusInfo.getBackcarStatus();
				}
				
			}
		}
	};
	
	Runnable mMediaInfoEmptyTread = new Runnable() {
		@Override
		public void run() {
			mTvMediaInfo.setText(R.string.emptyplaylist);
		}
	};
   
    public String getOneExistStorage(){
		EnvironmentATC env = new EnvironmentATC(this);
		for (String storage : storages) {
			boolean checkResult = YeconEnv.checkStorageExist(env, storage);
			if(checkResult){
				if(storage.matches("(.*)ext_sdcard(.*)")){
					return storage;
				}
				if(storage.matches("(.*)udisk(.*)")){
					return storage;
				}
			}
		}
		return YeconMediaStore.EXTERNAL_PATH;
	}
	
	
}
