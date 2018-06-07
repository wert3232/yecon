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

import static android.mcu.McuExternalConstant.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.yecon.mediaprovider.YeconMediaStore;
import com.yecon.mediaservice.MediaPlayerContants.MediaPlayerMessage;
import com.yecon.mediaservice.MediaPlayerContants.PlayStatus;
import com.yecon.mediaservice.LogUtil;
import com.yecon.mediaservice.MultiMediaPlayer;
import com.yecon.mediaservice.IMultiMediaPlayer;
import com.yecon.settings.YeconSettings;
import com.yecon.video.util.L;

/**
 * @ClassName: VideoPreview
 * @Description: TODO
 * @author hzGuo
 * @date 2016年5月12日 下午1:43:53
 *
 */
@SuppressLint("DefaultLocale")
public class VideoPreview extends Activity implements OnClickListener, OnSeekBarChangeListener, Callback, IMultiMediaPlayer , SurfaceHolder.Callback{
	
	private static final String TAG = "VideoPreview";
	
	private static final int MSG_EXIT_APP = 255;
	private static final int MSG_HIDE_BAR = 254;
	
	private static final int TIME_AUTO_FULLSREEN = 8 * 1000;

	public static final String SOUND_SETTING_PACKAGE_NAME = "com.yecon.sound.setting";
	public static final String SOUND_SETTING_STARTUP_ACTIVITY = "com.yecon.sound.setting.AudioSetting";

	public static final String ACTION_QB_POWEROFF = "autochips.intent.action.QB_POWEROFF";
	public static final String ACTION_QB_PREPOWEROFF = "autochips.intent.action.QB_PREPOWEROFF";
	
	// layout
	private LinearLayout mLayoutPlayer;
	private LinearLayout mLayoutLoading;
	private LinearLayout mLayoutBottomBar;
	private LinearLayout mLayoutPlayInfo;
	
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
	
	private SurfaceView mSurfaceFront;
	private boolean mbSurfaceFrontCreate = true;
	private SurfaceView mSurfaceRear;
	private boolean mbSurfaceRearCreate = true;
	private VideoRearView mRearView;
	private MultiMediaPlayer mMediaPlayer;
	private int miProgress = 0;
	private String mstrSource = null;
	
	boolean mbSeeking = false;
	boolean mbShow = false;
	
	Handler mHandler = new Handler(this);

	private Toast mToast;
	
	private BroadcastReceiver mReceiverQBPowerOFF = new BroadcastReceiver() {
		@Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
            	if (ACTION_QB_POWEROFF.equals(action) ||
            		ACTION_QB_PREPOWEROFF.equals(action)) {
            		if (mMediaPlayer != null) {
						mMediaPlayer.acquireWakeLock();
					}
            		mHandler.sendEmptyMessage(MSG_EXIT_APP);
                }
			}
        }
    };
	
	private BroadcastReceiver mReceiverUnMount = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				final String action = intent.getAction();
				final Uri uri = intent.getData();
				String path = YeconMediaStore.getStoragePath(uri.getPath());
				Log.i(TAG, action + ":" + uri.toString());
				if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_EJECT)
					|| action.equals(Intent.ACTION_MEDIA_REMOVED)
					|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
					try {
						if (mMediaPlayer != null) {
							if (path == null || mMediaPlayer.getSourcePath().contains(path)) {
								// stop play
								Log.i(TAG, "unmount, release");
								mMediaPlayer.release();
								Log.i(TAG, "unmount, exit");
								mHandler.sendEmptyMessage(MSG_EXIT_APP);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

	};
	
	private BroadcastReceiver mReceiverMCUKey = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action != null) {
				LogUtil.printError(TAG, LogUtil._FUNC_(), action);
				if (MCU_ACTION_ACC_OFF.equals(action)) {
					mMediaPlayer.pause();
					mRearView.hide();
    			} else if (MCU_ACTION_ACC_ON.equals(action)) {
    				mMediaPlayer.play();
    				mRearView.show();
				} else if (action.equals(MCU_ACTION_MEDIA_PLAY_PAUSE)) {
					if (mMediaPlayer.getCurrentRealState() == PlayStatus.STARTED) {
						mMediaPlayer.pause();
					} else {
						mMediaPlayer.play();
					}
				} else if (action.equals(MCU_ACTION_MEDIA_PLAY)) {
					if (mMediaPlayer.getCurrentRealState() == PlayStatus.PAUSED) {
						mMediaPlayer.play();
					}
				} else if (action.equals(MCU_ACTION_MEDIA_PAUSE) ||
						action.equals(MCU_ACTION_MEDIA_STOP)) {
					if (mMediaPlayer.getCurrentRealState() == PlayStatus.STARTED) {
						mMediaPlayer.pause();
					}
				}
			}
		}
	};

	private AudioManager mAudioManager;

	protected boolean mPausedByTransientLossOfFocus = false;

	private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
        	Log.e(TAG, "mAudioFocusListener:" + focusChange);
            if (mMediaPlayer == null) {
                mAudioManager.abandonAudioFocus(this);
                Log.e(TAG, "mAudioFocusListener:abandonAudioFocus");
                return;
            }
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    mPausedByTransientLossOfFocus = false;
                    mMediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (mMediaPlayer.getCurrentRealState() == PlayStatus.STARTED) {
                        mPausedByTransientLossOfFocus = true;
                        mMediaPlayer.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mPausedByTransientLossOfFocus) {
                        mPausedByTransientLossOfFocus = false;
                        mMediaPlayer.play();
                    }
                    break;
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
		
		mLayoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
		mLayoutPlayer = (LinearLayout) findViewById(R.id.layout_play);
		mLayoutBottomBar = (LinearLayout) findViewById(R.id.layout_music_op);
		mLayoutPlayInfo = (LinearLayout) findViewById(R.id.layoutPlayInfo);
		
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
		mLayoutBottomBar.setVisibility(View.GONE);
		mTvTrackIndex.setVisibility(View.GONE);
		
		mHandler.removeMessages(MSG_EXIT_APP);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addDataScheme("file");
		registerReceiver(mReceiverUnMount, filter);
		
		filter = new IntentFilter();
		filter.addAction(ACTION_QB_POWEROFF);
		filter.addAction(ACTION_QB_PREPOWEROFF);
		registerReceiver(mReceiverQBPowerOFF, filter);
		
		filter = new IntentFilter();
        filter.addAction(MCU_ACTION_MEDIA_PLAY_PAUSE);
        filter.addAction(MCU_ACTION_MEDIA_PLAY);
        filter.addAction(MCU_ACTION_MEDIA_PAUSE);
        filter.addAction(MCU_ACTION_MEDIA_STOP);
        filter.addAction(MCU_ACTION_ACC_ON);
        filter.addAction(MCU_ACTION_ACC_OFF);
        registerReceiver(mReceiverMCUKey, filter);
		
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
		initSurface();
		mMediaPlayer = new MultiMediaPlayer(this, this);
		recoverDevice();
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
			mLayoutLoading.setVisibility(View.GONE);
			Intent intent = getIntent();
			if (intent != null) {
				Uri uri = intent.getData();
		        if (uri == null) {
		            mHandler.sendEmptyMessage(MSG_EXIT_APP);
		            return;
		        }
		        String file = uri.getPath();
		        Log.e(TAG, file);
		        if (mMediaPlayer != null) {
					mMediaPlayer.decode(file);
				}
		        mAudioManager.requestAudioFocus(mAudioFocusListener , AudioManager.STREAM_MUSIC,
		                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e(TAG, "onStart");
		if (mMediaPlayer != null) {
			if (mstrSource != null && !mstrSource.isEmpty()) {
				mMediaPlayer.decode(mstrSource);
			}
		}
		mAudioManager.requestAudioFocus(mAudioFocusListener,
				AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e(TAG, "onPause");
		mbShow = false;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e(TAG, "onResume");
		mbShow = true;
		YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		mHandler.sendEmptyMessage(MSG_EXIT_APP);
		if (mMediaPlayer != null) {
			if (mMediaPlayer.getPlayState() == PlayStatus.STARTED ||
				mMediaPlayer.getPlayState() == PlayStatus.PAUSED) {
				miProgress = mMediaPlayer.getProgress();
			}
			mstrSource = mMediaPlayer.getSourcePath();
			mMediaPlayer.release();
			mAudioManager.abandonAudioFocus(mAudioFocusListener);
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
		boolean bPreFullHideBar = true;
		try {
			switch (v.getId()) {
			case R.id.surfaceView:
				bPreFullHideBar = false;
				if (mBtnVideoCtrl.getVisibility() == View.VISIBLE) {
					showBar(false);
				} else {
					showBar(true);
				}
				break;
				
			case R.id.rgbControl:
				YeconSettings.callVideoRgbSettingActivity(this, YeconSettings.RGBTYPE.USB);
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
		case MediaPlayerMessage.UPDATE_PLAY_STATE:
			// update track info 
			if (msg.arg1 == PlayStatus.DECODED ||
				msg.arg1 == PlayStatus.STARTED) {
				if (msg.arg1 == PlayStatus.DECODED) {
					if (miProgress != 0) {
						mMediaPlayer.seek(miProgress);
						Log.e(TAG, "current pos:" + miProgress);
					}
					showVideo();
				}
				ForceUpdateTrack();
			} else if (msg.arg1 == PlayStatus.ERROR ||
					msg.arg1 == PlayStatus.FINISH) {
				// play error & finish
				mHandler.sendEmptyMessage(MSG_EXIT_APP);
			} else if (msg.arg1 == PlayStatus.IDLE) {
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
				showToast(strMediaInfo);
			}
			if (mTvMediaInfo != null) {
				if (strMediaInfo != null) {
					mTvMediaInfo.setText(strMediaInfo);
				} else {
					mTvMediaInfo.setText(null);
				}
			}
			break;
			
		case MSG_EXIT_APP:
			Log.e(TAG, "MSG_EXIT_APP");
			if (mMediaPlayer != null) {
				mMediaPlayer.release();
				mMediaPlayer.releaseWakeLock();
			}
			mAudioManager.abandonAudioFocus(mAudioFocusListener);
			finish();
			break;

		case MSG_HIDE_BAR:
			showBar(false);
			break;
			
		default:
			break;
		}
		return false;
	}

	private void showVideo() {
		// TODO Auto-generated method stub
		synchronized (TAG) {
			if (mMediaPlayer != null) {
				if (mbSurfaceFrontCreate) {
					mMediaPlayer.setFrontDisplay(mSurfaceFront.getHolder());
				}
				if (mbSurfaceRearCreate) {
					if (mRearView != null) {
						mMediaPlayer.setRearDisplay(mSurfaceRear.getHolder());
					}
				}
			}	
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
//		Log.e("onProgressChanged", "" + progress);
		if (mbSeeking) {
			mTvProgress.setText(formatData(progress));
			mSeekBar.setProgress(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.e(TAG, "[onStartTrackingTouch] start get seek to progress");
		mbSeeking = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		int iProgress = seekBar.getProgress();
		Log.e(TAG, "[onStopTrackingTouch] progress:" + iProgress);
		if (mMediaPlayer != null) {
			mMediaPlayer.seek(iProgress);
		}
		mbSeeking = false;
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
		}
	}
	
	// 强制刷新曲目信息
	private void ForceUpdateTrack () {
		try {
			if (mMediaPlayer != null) {
				mSeekBar.setMax(mMediaPlayer.getDuration());
				mTvDuration.setText(formatData(mMediaPlayer.getDuration()));
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		unregisterReceiver(mReceiverQBPowerOFF);
		unregisterReceiver(mReceiverUnMount);
		unregisterReceiver(mReceiverMCUKey);
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
		try {
			Window window = getWindow();
	        WindowManager.LayoutParams params = window.getAttributes();
			if (bShow) {
				mHandler.removeMessages(MSG_HIDE_BAR);
				mHandler.sendEmptyMessageDelayed(MSG_HIDE_BAR, TIME_AUTO_FULLSREEN);
//FIXME:				params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
				mLayoutPlayInfo.setVisibility(View.VISIBLE);
				mBtnVideoCtrl.setVisibility(View.VISIBLE);
			} else {
//FIXME:				params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;  
				mLayoutPlayInfo.setVisibility(View.INVISIBLE);
				mBtnVideoCtrl.setVisibility(View.INVISIBLE);
			}
			window.setAttributes(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void initSurface() {
		mSurfaceFront = (SurfaceView) findViewById(R.id.surfaceView);
		mSurfaceFront.setBackgroundResource(android.R.color.transparent);
		mSurfaceFront.getHolder().addCallback(this);
		mSurfaceFront.setOnClickListener(this);
		
		DisplayManager mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        String displayCategory = DisplayManager.DISPLAY_CATEGORY_PRESENTATION;
        Display[] displays = mDisplayManager.getDisplays(displayCategory);
        if (displays.length >= 1) {
        	mRearView = new VideoRearView(this, displays[0]);
        } else {
        	Log.e(TAG, "initRearSurface: no have rear displays");
        }
        if (mRearView != null) {
			mRearView.show();
			mSurfaceRear = mRearView.getSurfaceView();
			mSurfaceRear.getHolder().addCallback(this);
			mSurfaceRear.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}
	
	public String formatData(int iDuration) {
		return String.format("%02d:%02d:%02d",
				iDuration / 60 / 60, iDuration / 60 % 60,
				iDuration % 60);
	}

	@Override
	public void updateMediaInfo(int arg0, int arg1) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = MediaPlayerMessage.UPDATE_MEDIA_INTO;
		msg.arg1 = arg0;
		msg.arg2 = arg1;
		mHandler.sendMessage(msg);
	}

	@Override
	public void updatePlayProgress(int arg0, int arg1) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = MediaPlayerMessage.UPDATE_PLAY_PROGRESS;
		msg.arg1 = arg0;
		msg.arg2 = arg1;
		mHandler.sendMessage(msg);
	}

	@Override
	public void updatePlayState() {
		// TODO Auto-generated method stub
		if (mMediaPlayer != null) {
			int iState = mMediaPlayer.getPlayState();
			Message msg = Message.obtain();
			msg.what = MediaPlayerMessage.UPDATE_PLAY_STATE;
			msg.arg1 = iState;
			mHandler.sendMessage(msg);
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			synchronized (TAG) {
				Log.e(TAG, "surfaceCreated");
				if (mMediaPlayer != null) {
					if (holder.equals(mSurfaceFront.getHolder())) {
						mbSurfaceFrontCreate = true;
						mMediaPlayer.setFrontDisplay(holder);
					} else if (holder.equals(mSurfaceRear.getHolder())) {
						mbSurfaceRearCreate = true;
						mMediaPlayer.setRearDisplay(holder);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			synchronized (TAG) {
				Log.e(TAG, "surfaceDestroyed");
				if (mMediaPlayer != null) {
					if (holder.equals(mSurfaceFront.getHolder())) {
						mbSurfaceFrontCreate = true;
						mMediaPlayer.setFrontDisplay(null);
					} else if (holder.equals(mSurfaceRear.getHolder())) {
						mbSurfaceRearCreate = true;
						mMediaPlayer.setRearDisplay(null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
