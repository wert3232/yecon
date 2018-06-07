/**
 * @Title: MusicPlayerActivity.java
 * @Package com.yecon.musicplayer
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年5月12日 下午1:43:53
 * @version V1.0
 */
package com.yecon.music;

import static android.mcu.McuExternalConstant.MCU_SOURCE_BT;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.mcu.McuExternalConstant;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
import com.yecon.media.MarqueeTextView;
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
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Type;
import com.yecon.music.R;
import com.yecon.music.msg.EventBusMsg;
import com.yecon.music.util.L;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ClassName: MusicPlayerActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年5月12日 下午1:43:53
 *
 */
@SuppressLint("DefaultLocale")
public class MusicPlaybackMainActivity extends MediaBaseActivity implements OnClickListener, OnSeekBarChangeListener {
	
	private static final String TAG = "YG_MusicPlayer";
	
	private Activity self = this;
	private static final int MSG_EXIT_APP = 255;
	private static final int MSG_ATTACH_OTHER = 122;
	private static final int MODE_PLAY_RANDOM = 11; 
	private static final int MODE_PLAY_SQUEEN = 12; 
	private static final int MODE_PLAY_SIGLE = 13; 
	private static final int MODE_PLAY_REPEAT_ALL = 14; 
	public static final String SOUND_SETTING_PACKAGE_NAME = "com.yecon.sound.setting";
	public static final String SOUND_SETTING_STARTUP_ACTIVITY = "com.yecon.sound.setting.AudioSetting"; 
	
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	
	// layout
	private LinearLayout mLayoutPlayer;
	private LinearLayout mLayoutLoading;
	
	// widget
	private MarqueeTextView mTvTitle;
	private MarqueeTextView mTvAlbum;
	private MarqueeTextView mTvArtist;
	private TextView mTvDuration;
	private TextView mTvProgress;
	private SeekBar mSeekBar;
	private TextView mTvTrackIndex;
	
	private ImageView mIVApic;
	
	private View mBtnPrev;
	private View mBtnPlay;
	private View mBtnPause;
	private View mBtnNext;
	private View mBtnMute;
	private TextView mBtnRandomON;
	private TextView mBtnRandomOFF;
	private TextView mBtnRepeatList;
	private TextView mBtnRepeatSingle;
	private TextView mBtnRepeatOFF;
	private TextView mBtnList;
	private ImageView mBtnEQ;
	private ImageView playModeStateView;
	private ObservableEmitter playerEmitter;
	boolean mbSeeking = false;
	
	Handler mHandlerExit = new Handler(this);

	private long mlLaunchTime = 0;
	
	private int IGNORE_TIME = 1000 * 5;
	
	private Toast mToast;
	private boolean isMutePause;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		L.e(TAG, "onCreate");
		setContentView(R.layout.yecon_music_player_activity);
		mLayoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
		mLayoutPlayer = (LinearLayout) findViewById(R.id.layout_play);
		
		mTvTitle = (MarqueeTextView) findViewById(R.id.tv_track);
		mTvAlbum = (MarqueeTextView) findViewById(R.id.tv_album);
		mTvArtist = (MarqueeTextView) findViewById(R.id.tv_artist);
		mSeekBar = (SeekBar) findViewById(R.id.sb_music_playback_process);
		mTvDuration = (TextView) findViewById(R.id.tv_total_time);
		mTvProgress = (TextView) findViewById(R.id.tv_progress_time);
		mTvTrackIndex = (TextView) findViewById(R.id.tv_track_index);
		
		mIVApic = (ImageView) findViewById(R.id.iv_album);
		
		mBtnPrev = findViewById(R.id.btn_op_pre);
		mBtnPlay = findViewById(R.id.btn_op_play);
		mBtnPause = findViewById(R.id.btn_op_pause);
		mBtnNext = findViewById(R.id.btn_op_next);
		mBtnMute = findViewById(R.id.btn_mute);
		mBtnRandomON = (TextView) findViewById(R.id.btn_op_rand);
		mBtnRandomOFF = (TextView) findViewById(R.id.btn_op_rand_off);
		mBtnRepeatList = (TextView) findViewById(R.id.btn_op_loop);
		mBtnRepeatSingle = (TextView) findViewById(R.id.btn_op_loop_single);
		mBtnRepeatOFF = (TextView) findViewById(R.id.btn_op_loop_off);
		mBtnList = (TextView) findViewById(R.id.btn_op_list);
		mBtnEQ = (ImageView) findViewById(R.id.iv_control_audio);
		playModeStateView =  (ImageView) findViewById(R.id.play_mode_state);

		//FIXME:111001
		TopBar topBar = (TopBar) findViewById(R.id.topbar);
		topBar.setTopBarBackCall(new TopBar.TopBarBackCall() {
			@Override
			public void onHomeReturn() {
				if(mHandlerExit != null){
					mHandlerExit.sendEmptyMessage(MSG_EXIT_APP);
				}
			}
		});//

		findViewById(R.id.btn_play_mode).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		mBtnPrev.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnPause.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnMute.setOnClickListener(this);
		mBtnRandomON.setOnClickListener(this);
		mBtnRandomOFF.setOnClickListener(this);
		mBtnRepeatList.setOnClickListener(this);
		mBtnRepeatSingle.setOnClickListener(this);
		mBtnRepeatOFF.setOnClickListener(this);
		mBtnList.setOnClickListener(this);
		mBtnEQ.setOnClickListener(this);
		
		mSeekBar.setOnSeekBarChangeListener(this);
		
		mPref = this.getSharedPreferences("Music_settings", 3);
		mEditor = mPref.edit();
		
		mLayoutPlayer.setVisibility(View.VISIBLE);
		mHandlerExit.removeMessages(MSG_EXIT_APP);	
		if (SystemProperties.getBoolean(YeconConstants.PROPERTY_QB_POWERON, false)) {
			// Quick Power ON
			mlLaunchTime = SystemClock.uptimeMillis();
		}
		
		recoverDevice();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(MediaConstants.DO_PLAY);
		filter.addAction(MediaConstants.DO_PAUSE);
		filter.addAction(MediaConstants.DO_PREV);
		filter.addAction(MediaConstants.DO_NEXT);
		filter.addAction(MediaConstants.DO_STOP);
		filter.addAction(MediaConstants.CURRENT_MEDIA);
		
		registerReceiver(controlReceiver, filter);
		
		IntentFilter otherfilter = new IntentFilter();
		otherfilter.addAction(MediaConstants.ACTION_SOURCE_ATTACH_OTHER_STORAGE);
		otherfilter.addAction(MediaConstants.DO_EXIT_APP);
		otherfilter.addAction("action.hzh.media.power.on");
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
        
        McuManager mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.getInt("startTag", 0) == 1){	
			try {
				mMcuManager.RPC_SetSource(McuExternalConstant.MCU_SOURCE_SD, 0x00);
			} catch (RemoteException e) {
				Log.e(this.getClass().getName(), e.toString());
			}
        }

		//FIXME:12001 RxJava 频率快速变化只取0.65秒内最后一个
		Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
				playerEmitter = observableEmitter;
			}
		})
				.throttleLast(650, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Integer>() {
					@Override
					public void onSubscribe(Disposable disposable) {

					}

					@Override
					public void onNext(Integer volume) {
						try{
							int iState = getMediaSevice().getPlayStatus();
							if (iState == PlayStatus.PAUSED && isMutePause && volume > 0) {
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

					@Override
					public void onError(Throwable throwable) {

					}

					@Override
					public void onComplete() {

					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//L.e("onKeyDown keyCode : " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT || keyCode == KeyEvent.KEYCODE_YECON_NEXT){
			next();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS || keyCode == KeyEvent.KEYCODE_YECON_PREV){
			prev();
			return true;
		}
		else return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//L.e("onKeyUp keyCode : " + keyCode);
		return super.onKeyUp(keyCode, event);
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
		L.e(TAG, "recoverDevice");
		try {
			Intent intent = getIntent();
			String device = null;
			if (intent != null) {
				EnvironmentATC atc = new EnvironmentATC(this);
				device = intent.getStringExtra("plugindevice");
				//Toast.makeText(self, "device1:" + device + "  " + SystemProperties.get("persist.sys.music_init_state", "empty"), Toast.LENGTH_LONG).show();
				if (device == null) {
					if (isBindService()) {
						if (getMediaSevice().getPlayingStorage() == null) {
							// play last memory device
							device = MediaPlayerContants.LAST_MEMORY_DEVICE;
						}
					} else {
						device = MediaPlayerContants.LAST_MEMORY_DEVICE;
					}
					String devPath = SystemProperties.get("persist.sys.music_init_state", "empty");
					if(!"empty".equals(devPath)){
						boolean isExist = YeconEnv.checkStorageExist(atc, devPath);
						if(isExist){
							device = SystemProperties.get("persist.sys.music_init_state", "empty");
						}
					}
				}else{
					SystemProperties.set("persist.sys.video_init_state", device);
					//Toast.makeText(this, "device2:" + SystemProperties.get("persist.sys.video_init_state", "empty"), Toast.LENGTH_LONG).show();
				}
				SystemProperties.set("persist.sys.music_init_state", "empty");
			}  
			//Toast.makeText(this, "device3:" + device, Toast.LENGTH_LONG).show();
			if (device != null && device.length() > 0) {
				L.e(TAG, "recoverDevice -> loading " + device); 
				//mLayoutLoading.setVisibility(View.VISIBLE);
				AttachStorage(device, MediaType.MEDIA_AUDIO);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		L.e(TAG, "onPause");
		try {
			if (isBindService()) {
				if (getMediaSevice().getPlayStatus() == PlayStatus.STARTED) {
					getMediaSevice().requestSaveLastMemory();
				}
				//else if(getMediaSevice().getPlayStatus() == PlayStatus.IDLE)
				//	finish();
			}
		} catch (Exception e) {
			L.e(e.toString());
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		L.e(TAG, "onResume");
		{
			Intent intent = new Intent(MediaConstants.CURRENT_MEDIA_IS_MUSIC);
			try {
				List<MediaStorage> lsStorages = getMediaSevice().getStorageList();
				MediaStorage playStorage = null;
				for(MediaStorage storage : lsStorages){
					if(storage.getPlaying()){
						playStorage = storage;
						break;
					}
				}
				if(playStorage != null){//FIX ME YeconMedia
					/*String path = playStorage.getPath();
					if(MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_USB.equals(MediaPlayerService.getMediaStorageType(path))){
						intent.putExtra(MediaConstants.EXTRA_PALYING_DEVICE, MediaConstants.DEVICE_USB);
					}else if(MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_SD.equals(MediaPlayerService.getMediaStorageType(path))){
						intent.putExtra(MediaConstants.EXTRA_PALYING_DEVICE, MediaConstants.DEVICE_SD);
					}*/
				}else{
					//if(YeconEnv.hasSD(this) || YeconEnv.hasUsb(this)){
						Intent intent2 = new Intent(MediaConstants.ACTION_SOURCE_ATTACH_OTHER_STORAGE);
						intent2.putExtra(Context.EXTRA_APK_PACKAGENAME, YeconConstants.MUSIC_PACKAGE_NAME);
						sendOrderedBroadcast(intent2, null);
					//}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendBroadcast(intent, null);
		}
		if (mLayoutLoading != null) {
			boolean bScaning = isScaningAttachedDevice();
			mLayoutLoading.setVisibility(bScaning ? View.VISIBLE : View.GONE);
			if (!bScaning) {
				ForceUpdateTrack();
			} else {
				L.e(TAG, "onResume:scaning -> loading");
			}
		}
	}
	

	
	/**
	 * Title: onClick
	 * Description:
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try {
			switch (v.getId()) {
			case R.id.btn_op_list:
				startActivity(new Intent(this, MusicListActivity.class));
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
				next();
				
				/*try {
					
					L.e(getMediaSevice().getFilePosInList() + "   ,   " + getMediaSevice().getDirPosInList() + " ,  " + getMediaSevice().getPlayListType());
					MediaPlayerService mMediaPlayerService = (MediaPlayerService) getMediaSevice();
					mMediaPlayerService.SetPlayListType(Type.ALL_DIR);
				} catch (RemoteException e) {
					e.printStackTrace();
				}*/
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
			case R.id.iv_control_audio:{
				Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setComponent(new ComponentName(SOUND_SETTING_PACKAGE_NAME,
                        SOUND_SETTING_STARTUP_ACTIVITY));
                startActivity(intent);
			}
				break;
			case R.id.back:{
				onBackPressed();
			}
				break;
			case R.id.btn_play_mode:	
				L.e("ExternalStorageState", Environment.getExternalStorageState());
				nextPlayMode();
				break;
			case R.id.btn_mute:
				((MuteTextView)findViewById(R.id.btn_mute_state)).toggle();
				/*Intent intent = new Intent(MediaConstants.DO_MUTE_PAUSE);
				intent.putExtra("media_type", MediaConstants.CURRENT_MEDIA_IS_MUSIC);
				sendBroadcast(intent);*/
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		/*switch (msg.arg1) {
			case ServiceStatus.LOST_STORAGE:
				Log.e(TAG, "LOST_STORAGE");
				break;
			case ServiceStatus.NO_STORAGE:
				Log.e(TAG, "NO_STORAGE");
				break;
			case ServiceStatus.LOST_AUDIO_FOCUS:
				Log.e(TAG, "LOST_AUDIO_FOCUS");
				break;
			case ServiceStatus.QB_POWER:
				Log.e(TAG, "QB_POWER");
				break;
		}*/
		switch (msg.what) {
		case MediaPlayerMessage.UPDATE_SERVICE_STATE:
			if (msg.arg1 == ServiceStatus.SCANED) {
				ForceUpdateTrack();
				mLayoutLoading.setVisibility(View.GONE);
				//L.e("ALL_FILE size: " + getList(Type.ALL_FILE).size() + "");
			}else if(msg.arg1 == ServiceStatus.LOST_STORAGE){
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
							mHandlerExit.sendEmptyMessage(MSG_ATTACH_OTHER);
						}
					} catch (Exception e) {
						L.e(e.toString());
					}
				}
			} else if (msg.arg1 == ServiceStatus.NO_STORAGE ||
					   msg.arg1 == ServiceStatus.LOST_AUDIO_FOCUS ||
					   msg.arg1 == ServiceStatus.QB_POWER) {
				boolean bExistApp = true;
				try {
					String tips = null;
					if (!isBindService() || getMediaSevice().getPlayingStorage() == null) {
						if (msg.arg1 == ServiceStatus.NO_STORAGE) {
							tips = getString(R.string.no_storage);
						} else if (msg.arg1 == ServiceStatus.LOST_AUDIO_FOCUS) {
							L.e(TAG, "lost audio focus!!! exit");
						} else {
//							tips = getString(R.string.unmount_disk);
						} 
					} else if (msg.arg1 == ServiceStatus.LOST_AUDIO_FOCUS) {
						L.e(TAG, "lost audio focus!!! exit");
					} else if (msg.arg1 == ServiceStatus.QB_POWER) {
						L.e(TAG, "QB_POWER_OFF!!!");
						//FIXME:取消假关机关闭APP
						/*bExistApp = false;
						
						if(getMediaSevice().getPlayStatus() == MediaPlayerContants.PlayStatus.STARTED){
							getMediaSevice().requestPause();
						}*/
						//
					} 
					if (tips != null) {
						Toast.makeText(this,
								tips + "," + getString(R.string.exit_app),
								Toast.LENGTH_SHORT).show();
					}
					if (bExistApp) {
						mHandlerExit.sendEmptyMessage(MSG_EXIT_APP);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.arg1 == ServiceStatus.EMPTY_STORAGE) {
				mLayoutLoading.setVisibility(View.GONE);
				//L.e("ALL_FILE size: " + getList(Type.ALL_FILE).size() + "");
				mTvTitle.setText("");
				mTvTitle.removeCallbacks(mMediaInfoEmptyTread);
				mTvTitle.postDelayed(mMediaInfoEmptyTread, 800);
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
								L.e(TAG, "mlLaunchTime:" + mlLaunchTime);
								L.e(TAG, "SystemClock.uptimeMillis():" + SystemClock.uptimeMillis());
								if (SystemClock.uptimeMillis() - mlLaunchTime > IGNORE_TIME) {
									AttachStorage(pathNew, MediaType.MEDIA_AUDIO);
									L.e(TAG, "handlemessage:scaning -> loading");
									if (isScaningAttachedDevice()) {
										//mLayoutLoading.setVisibility(View.VISIBLE);
									}
								} else {
									L.e(TAG, "handlemessage:system is QB_POWERON, ignore mount");
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
			L.e(TAG, "UPDATE_SERVICE_STATE:" + msg.arg1);
			break;
		case MediaPlayerMessage.UPDATE_LIST_DATA:
			ForceUpdateTrack();
			break;
		case MediaPlayerMessage.UPDATE_PLAY_STATE:
			// update track info 
			if (msg.arg1 == PlayStatus.DECODED ||
				msg.arg1 == PlayStatus.STARTED) {
				if (msg.arg1 == PlayStatus.DECODED) {
					mSeekBar.setProgress(0);
				}else if(msg.arg1 == PlayStatus.STARTED){

				}
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
			
		case MediaPlayerMessage.UPDATE_RANDOM_STATE:
			UpdateRandom(msg.arg1);
			break;
			
		case MediaPlayerMessage.UPDATE_REPEAT_STATE:
			UpdateRepeat(msg.arg1);
			break;
			
		case MSG_EXIT_APP:
			exitApp();
			break;
		case MSG_ATTACH_OTHER:{
			String existStorage = getOneExistStorage();
			if(!TextUtils.isEmpty(existStorage)){
				L.e("MSG_ATTACH_OTHER : " + existStorage);
				AttachStorage(existStorage, MediaType.MEDIA_AUDIO);
				/*	try {
						if (getMediaSevice().getPlayStatus() == PlayStatus.IDLE) {
							mTvTitle.setText("");
							mTvTitle.removeCallbacks(mMediaInfoEmptyTread);
							mTvTitle.postDelayed(mMediaInfoEmptyTread, 800);
							mTvAlbum.setText("");
							mTvArtist.setText("");
							mTvProgress.setText(formatData(0));
							mTvDuration.setText(formatData(0));
							mSeekBar.setMax(0);
							mSeekBar.setEnabled(false);
							mTvTrackIndex.setText(getString(R.string.musicbrowserlabel));
							mIVApic.setImageResource(R.drawable.albumart_mp_unknown);
						}
					} catch (Exception e) {
						L.e(e.toString());
					}*/
			}else{
				L.e("MSG_ATTACH_OTHER : MSG_EXIT_APP");
				mHandlerExit.sendEmptyMessage(MSG_EXIT_APP);
			}
		}
		break;
		default:
			break;
		}
		return false;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
//		L.e("onProgressChanged", "" + progress);
		if (mbSeeking) {
			mTvProgress.setText(formatData(progress));
			mSeekBar.setProgress(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		L.e(TAG, "[onStartTrackingTouch] start get seek to progress");
		mbSeeking = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		int iProgress = seekBar.getProgress();
		L.e(TAG, "[onStopTrackingTouch] progress:" + iProgress);
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
					mTvTitle.removeCallbacks(mMediaInfoEmptyTread);
					mTvTitle.setText(cv.getTitle());
					mTvArtist.setText(cv.getArtist());
					mTvAlbum.setText(cv.getAlbum());
					int iDuration = cv.getDuration();
					mTvDuration.setText(formatData(iDuration));
					String strTrackIndex = String.format("%d/%d", cv.getTrackID() + 1, cv.getTrackTotal());
					mTvTrackIndex.setText(strTrackIndex);
					mSeekBar.setMax(iDuration);
					mSeekBar.setEnabled(iDuration != 0);
					if (iDuration == 0) {
						mTvProgress.setText(formatData(0));
					}
					try {
						Bitmap bmpApic = cv.getApicBmp();
						if (bmpApic != null) {
							mIVApic.setImageBitmap(bmpApic);
						} else {
							mIVApic.setImageResource(R.drawable.albumart_mp_unknown);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Log.d("TEST","cv.getTrackTotal()==0");
					mTvTitle.setText("");
					mTvTitle.removeCallbacks(mMediaInfoEmptyTread);
					mTvTitle.postDelayed(mMediaInfoEmptyTread, 800);
					mTvAlbum.setText("");
					mTvArtist.setText("");
					mTvProgress.setText(formatData(0));
					mTvDuration.setText(formatData(0));
					mSeekBar.setMax(0);
					mSeekBar.setEnabled(false);
					mTvTrackIndex.setText(getString(R.string.musicbrowserlabel));
					mIVApic.setImageResource(R.drawable.albumart_mp_unknown);
					UpdatePlayPause(PlayStatus.PAUSED);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 更新随机状态
	public void UpdateRandom(int iState){
		//L.e(TAG, "UpdateRandom " + iState);
		/*if (iState == RandomMode.OFF) {
			mBtnRandomOFF.setVisibility(View.VISIBLE);
			mBtnRandomON.setVisibility(View.GONE);
		} else {
			mBtnRandomOFF.setVisibility(View.GONE);
			mBtnRandomON.setVisibility(View.VISIBLE);
		}*/
		
		try {	
			if(getMediaSevice().getRepeatStatus() == RepeatMode.ALL && getMediaSevice().getRandomStatus() == RandomMode.OFF){
				updatePlayMode(MODE_PLAY_REPEAT_ALL);
			}else if(getMediaSevice().getRepeatStatus() == RepeatMode.OFF){
				updatePlayMode(MODE_PLAY_SQUEEN);
			}else if(getMediaSevice().getRepeatStatus() == RepeatMode.ALL && getMediaSevice().getRandomStatus() == RandomMode.ON){
				updatePlayMode(MODE_PLAY_RANDOM);
			}else if(getMediaSevice().getRepeatStatus() == RepeatMode.SINGLE){
				updatePlayMode(MODE_PLAY_SIGLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 更新循环状态
	public void UpdateRepeat(int iState){
		//L.e(TAG, "UpdateRepeat " + iState);
		/*if (iState == RepeatMode.ALL) {
			mBtnRepeatList.setVisibility(View.VISIBLE);
			mBtnRepeatSingle.setVisibility(View.GONE);
			mBtnRepeatOFF.setVisibility(View.GONE);
		} else if (iState == RepeatMode.SINGLE) {
			mBtnRepeatList.setVisibility(View.GONE);
			mBtnRepeatSingle.setVisibility(View.VISIBLE);
			mBtnRepeatOFF.setVisibility(View.GONE);
		} else if (iState == RepeatMode.OFF) {
			mBtnRepeatList.setVisibility(View.GONE);
			mBtnRepeatSingle.setVisibility(View.GONE);
			mBtnRepeatOFF.setVisibility(View.VISIBLE);
		}
		*/
		try {	
			if(getMediaSevice().getRepeatStatus() == RepeatMode.ALL && getMediaSevice().getRandomStatus() == RandomMode.OFF){
//				playModeStateView.getDrawable().setLevel(1);
//				mEditor.putInt("next_play_mode", 1).commit();
				updatePlayMode(MODE_PLAY_REPEAT_ALL);
			}else if(getMediaSevice().getRepeatStatus() == RepeatMode.OFF){
//				playModeStateView.getDrawable().setLevel(0);
//				mEditor.putInt("next_play_mode", 1).commit();
				updatePlayMode(MODE_PLAY_SQUEEN);
			}else if(getMediaSevice().getRepeatStatus() == RepeatMode.ALL && getMediaSevice().getRandomStatus() == RandomMode.ON){
//				playModeStateView.getDrawable().setLevel(2);
//				mEditor.putInt("next_play_mode", 2).commit();
				updatePlayMode(MODE_PLAY_RANDOM);
			}else if(getMediaSevice().getRepeatStatus() == RepeatMode.SINGLE){
//				playModeStateView.getDrawable().setLevel(3);
//				mEditor.putInt("next_play_mode", 0).commit();
				updatePlayMode(MODE_PLAY_SIGLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				if(SystemProperties.getInt("persist.sys.hasVolume", 0) == 0)
					((AudioManager)getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, Context.DEFUALT_VOLUME, 0);
			}
			if(0 != ((AudioManager)getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC))
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
		L.e(TAG, "onBackPressed");
//		super.onBackPressed();
//		mHandlerExit.sendEmptyMessage(MSG_EXIT_APP);
//		moveTaskToBack(true);
		onBackPressedToHome();
	}
	private void onBackPressedToHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		playerEmitter.onComplete();
		unregisterReceiver(controlReceiver);
		unregisterReceiver(otherReceiver);
		super.onDestroy();
		L.e(TAG, "onDestroy");
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
	
	private void updatePlayMode(int mode){
		try {	
			switch (mode) {
			case MODE_PLAY_RANDOM:{
				setPlayMode(RepeatMode.ALL, RandomMode.ON);	
				mEditor.putInt("next_play_mode", MODE_PLAY_SQUEEN).commit();
				playModeStateView.getDrawable().setLevel(2); //随机循环
				}
				break;
			case MODE_PLAY_SQUEEN:{
				setPlayMode(RepeatMode.OFF, RandomMode.OFF);	
				mEditor.putInt("next_play_mode", MODE_PLAY_SIGLE).commit();
				playModeStateView.getDrawable().setLevel(0); //顺序播放
				}
				break;
			case MODE_PLAY_SIGLE:{
				setPlayMode(RepeatMode.SINGLE, RandomMode.OFF);	
				mEditor.putInt("next_play_mode", MODE_PLAY_REPEAT_ALL).commit();
				playModeStateView.getDrawable().setLevel(3); //单曲循环
				}
				break;
			case MODE_PLAY_REPEAT_ALL:{
				setPlayMode(RepeatMode.ALL, RandomMode.OFF);	
				mEditor.putInt("next_play_mode", MODE_PLAY_RANDOM).commit();
				playModeStateView.getDrawable().setLevel(1); 		
				}
				break;
	
			}
		} catch (Exception e) {
			L.e(e.toString());
		}
	}
	public void nextPlayMode() throws Exception{
		int playMode = mPref.getInt("next_play_mode",MODE_PLAY_RANDOM);
		updatePlayMode(playMode);
		/*int randomState = 	getMediaSevice().getRandomStatus();
		int repeateState = 	getMediaSevice().getRepeatStatus();
		switch (playMode) {
			case 0:			
				setPlayMode(RepeatMode.ALL, RandomMode.OFF);	
				mEditor.putInt("next_play_mode", 1).commit();
				playModeStateView.getDrawable().setLevel(1); //列表顺序循环
//				prompt(R.string.str_repeat_order);
				break;
			case 1:
				setPlayMode(RepeatMode.ALL, RandomMode.ON);	
				mEditor.putInt("next_play_mode", 2).commit();
				playModeStateView.getDrawable().setLevel(2); //随机循环
//				prompt(R.string.str_repeat_shuffle);
				break;
			case 2: 
				setPlayMode(RepeatMode.OFF, RandomMode.OFF);	
				mEditor.putInt("next_play_mode", 3).commit();
				playModeStateView.getDrawable().setLevel(0); //顺序播放
//				prompt(R.string.str_unrepeat_order);
				break;
			case 3:
				setPlayMode(RepeatMode.SINGLE, RandomMode.OFF);	
				mEditor.putInt("next_play_mode", 0).commit();
				playModeStateView.getDrawable().setLevel(3); //单曲循环
//				prompt(R.string.str_repeat_one);
				break;
			default:
				setPlayMode(RepeatMode.SINGLE, RandomMode.OFF);	
				mEditor.putInt("next_play_mode", 0).commit();
				playModeStateView.getDrawable().setLevel(3); //单曲循环
//				prompt(R.string.str_repeat_one);
				break;
		}*/
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
	Toast toast;
	public void prompt(int rid){
		if(toast == null){
			toast = Toast.makeText(this, rid, Toast.LENGTH_SHORT);
		}else{	
			toast.cancel();
			toast = Toast.makeText(this, rid, Toast.LENGTH_SHORT);
		}
		toast.show();
	}
	public void prompt(String msg){
		if(toast == null){
			toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		}else{	
			toast.cancel();
			toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		}
		toast.show();
	}
	
	public void prev(){
		if (isBindService()) {
			try {
				boolean rePlay = (mSeekBar != null) && mSeekBar.getProgress() > 3;
				if(rePlay){
					getMediaSevice().requestSeek(0);
				}else{
					if(getMediaSevice().getRepeatStatus()==RepeatMode.SINGLE){
						setPlayMode(RepeatMode.ALL, RandomMode.OFF);
						getMediaSevice().requestPrev();
						setPlayMode(RepeatMode.SINGLE, RandomMode.OFF);
					}else
						getMediaSevice().requestPrev();
				}
			} catch (Exception e) {
				L.e(e.toString());
			}
		}
	}
	
	public void next(){
		if (isBindService()) {
			try {
				if(getMediaSevice().getRepeatStatus()==RepeatMode.SINGLE){
					setPlayMode(RepeatMode.ALL, RandomMode.OFF);
					getMediaSevice().requestNext();
					setPlayMode(RepeatMode.SINGLE, RandomMode.OFF);
				}else
					getMediaSevice().requestNext();
			} catch (Exception e) {
				L.e(e.toString());
			}
		}
	}
	
	public void openVolume(){
		if(findViewById(R.id.btn_mute_state) != null){
			((MuteTextView)findViewById(R.id.btn_mute_state)).setMute(false,400);
			/*AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			if(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0){
				findViewById(R.id.btn_mute_state).removeCallbacks(openVolumeRn);
				findViewById(R.id.btn_mute_state).postDelayed(openVolumeRn, 400);
			}*/
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
							AttachStorage(existStorage, MediaType.MEDIA_AUDIO);
						}
					} catch (Exception e) {
						L.e(e.toString());
					}
					/*try {
						if (getMediaSevice().getPlayStatus() == PlayStatus.IDLE) {
							mTvTitle.setText("");
							mTvTitle.removeCallbacks(mMediaInfoEmptyTread);
							mTvTitle.postDelayed(mMediaInfoEmptyTread, 800);
							mTvAlbum.setText("");
							mTvArtist.setText("");
							mTvProgress.setText(formatData(0));
							mTvDuration.setText(formatData(0));
							mSeekBar.setMax(0);
							mSeekBar.setEnabled(false);
							mTvTrackIndex.setText(getString(R.string.musicbrowserlabel));
							mIVApic.setImageResource(R.drawable.albumart_mp_unknown);
						}
					} catch (Exception e) {
						L.e(e.toString());
					}*/
				}else{
					mHandlerExit.sendEmptyMessage(MSG_EXIT_APP);
				}
			}else if(MediaConstants.DO_EXIT_APP.equalsIgnoreCase(action)){
				mHandlerExit.sendEmptyMessage(MSG_EXIT_APP);
			}else if("action.hzh.media.power.on".equals(action)){
				//FIXME:2017/03/22 add
				
				/*try {
					int iState = getMediaSevice().getPlayStatus();
					if (iState == PlayStatus.PAUSED) {
						//FIXME:一汽要求只有VOL和静音钮可解除静音 openVolume();
						
						getMediaSevice().requestPause();
					} else if (iState == PlayStatus.STARTED) {		
					} else if (iState == PlayStatus.IDLE){
						try {
							getMediaSevice().requestStart();
						} catch (Exception e) {
							L.e(e.toString());
						}
					}
					L.e("iState : " + iState);
				} catch (RemoteException e) {
					L.e(e.toString());
				}*/
				//
			}else if(action.equals("android.media.VOLUME_CHANGED_ACTION")){
				int volume = ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC) ;
				playerEmitter.onNext(volume);
				/*FIXME:12001
					try{
					int volume = ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC) ;
					int iState = getMediaSevice().getPlayStatus();
					if (iState == PlayStatus.PAUSED && isMutePause && volume > 0) {
						*//*FIXME:一汽要求只有VOL和静音钮可解除静音
			        	openVolume();
						*//*
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
				}*/
			}
		}};
	
	BroadcastReceiver controlReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String mediaType = intent.getStringExtra("media_type");
			L.e(this.getClass().getName(), "action:" + action + "  mediaType:" + mediaType);
			if(MediaConstants.CURRENT_MEDIA_IS_MUSIC.equals(mediaType)){
				try {	
					if		(MediaConstants.DO_PLAY.equalsIgnoreCase(action)){
						int iState = getMediaSevice().getPlayStatus();
						if (iState == PlayStatus.PAUSED) {
							//openVolume();
							getMediaSevice().requestPause();
						} else if (iState == PlayStatus.STARTED) {		
						} else if (iState == PlayStatus.IDLE){
							try {
								getMediaSevice().requestStart();
							} catch (Exception e) {
								L.e(e.toString());
							}
						}
						L.e("iState : " + iState);
					}else if(MediaConstants.DO_PAUSE.equalsIgnoreCase(action)){
						int iState = getMediaSevice().getPlayStatus();
						if (iState == PlayStatus.PAUSED) {
							getMediaSevice().requestPause();
						} else if (iState == PlayStatus.STARTED) {
							//openVolume();
							getMediaSevice().requestPause();	
						}
					}else if (MediaConstants.DO_STOP.equalsIgnoreCase(action)) {
						int iState = getMediaSevice().getPlayStatus();
						if (iState == PlayStatus.STARTED) {
							getMediaSevice().requestPause();
						}
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
						next();
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
	
	Runnable mMediaInfoEmptyTread = new Runnable() {
		@Override
		public void run() {
			mTvTitle.setText(R.string.emptyplaylist);
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
