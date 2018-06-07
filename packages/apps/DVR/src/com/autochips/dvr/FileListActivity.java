package com.autochips.dvr;

import java.util.ArrayList;

import com.autochips.storage.EnvironmentATC;
import com.yecon.common.YeconEnv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * image normal urgent
 * @author CB070
 *
 */
public class FileListActivity extends Activity implements OnClickListener{
	private final static String TAG="dvr_8317";
	private final int MEDIA_DEVICE_COUNT = 8;
	private final int FULLSCREEN_WAIT_TIME=16;
	static YeconVideoView mVideoView;
	ImageButton mBtnPrev;
	static ImageButton mBtnPlay;
	static ImageButton mBtnPause;
	ImageButton mBtnNext;
	TextView mCurTimeText;
	TextView mTotalTimeText;
	static TextView mNoFileHint;
	SeekBar mVideoProgress;
	static ListView mListView;
	private static int mPausePos=0;
	private static boolean mPauseFlag=false;
	static FileSearch mFileSearch = new FileSearch();
	boolean mTrackSeekBarFlag=false;
	public static int mCurListViewPos=0;
	public static String mSearchPath;
	private static String mCurPlayVideoName="";
	private final static String dvrdir = "/dvr/";
	private static AudioManager mAudioManager;
	private static boolean mPausedByTransientLossOfFocus;
	private boolean mFullscreenFlag=false;
	private LinearLayout mFileListLineLayout;
	private LinearLayout mPlaycontrolLineLayout;
	private LinearLayout mBottomLineLayout;
	private boolean mFullscreenFlag2=false;
	private static int mFullscreen2Count=0;
	//MediaController  mMediaController;

	
	private static FileListAdapter mFileListAdapter;
	//private static ProgressBar mLoadingVideo;
	private int mCurSelectBtnIndex=0;
	ArrayList<String> mSearchExt = new ArrayList<String>();
	private View []btnTabs = new View[MEDIA_DEVICE_COUNT];
	String[] mAllDiskName = 
		{ 
		  YeconEnv.INT_SDCARD_PATH,
		  YeconEnv.EXT_SDCARD1_PATH,
		  YeconEnv.EXT_SDCARD2_PATH,
		  YeconEnv.UDISK1_PATH,
		  YeconEnv.UDISK2_PATH,
		  YeconEnv.UDISK3_PATH,
		  YeconEnv.UDISK4_PATH,
		  YeconEnv.UDISK5_PATH};
	
	private Handler mVideoTimeHandler = new Handler();
	private Runnable mVideoTimeRunnable = new Runnable() {
		int buffer, currentPosition, duration;

		public void run() {
			currentPosition = mVideoView.getCurrentPosition();
			duration = mVideoView.getDuration();
				
			mCurTimeText.setText(tools.millions2hour(currentPosition));
			if (!mTrackSeekBarFlag) {
				//拖动滑动条的时候就不去更新
				mVideoProgress.setProgress(currentPosition);
			}
			
			mVideoTimeHandler.postDelayed(mVideoTimeRunnable, 500);
			if (mFullscreenFlag) {
				mFullscreen2Count ++;
				if (mFullscreen2Count >= FULLSCREEN_WAIT_TIME) {
					mFullscreen2Count = 0;
					autoEnterFullscreen2();
				}
			}			
		}
	};
	
	void switch2smallVideo() {
		if (!mVideoView.isPlaying()) {
			playPauseVideo();	
		}
		
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        
		mFileListLineLayout.setVisibility(View.VISIBLE);
    	mBottomLineLayout.setVisibility(View.VISIBLE);
    	mPlaycontrolLineLayout.setVisibility(View.VISIBLE);
		mFullscreenFlag = false;// 改变全屏/窗口的标记
		mFullscreen2Count = 0;
		
	}
	
	void switchFullscreen() {
		if (!mVideoView.isPlaying()) {
			playPauseVideo();	
		}
		
		if (!mFullscreenFlag) {
			mFileListLineLayout.setVisibility(View.GONE);
	    	mBottomLineLayout.setVisibility(View.GONE);
	    	mPlaycontrolLineLayout.setVisibility(View.GONE);
	    	
	    	mFullscreenFlag = true;// 改变全屏/窗口的标记
	    	mFullscreenFlag2 = true;
	    	mFullscreen2Count = 0;
	    	
	    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	        
	        Window window = getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			window.setAttributes(params);
	        
	    	//mVideoView.setMediaController(mMediaController);
		} else {
			switch2smallVideo();
		}
	}
	
	/**
	 * 这个是指进入全屏的再次切换
	 */
	void switchFullscreen2() {
		if (!mFullscreenFlag2) {
			mPlaycontrolLineLayout.setVisibility(View.GONE);
			Window window = getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			window.setAttributes(params);
			
										
			mFullscreenFlag2 = true;
		} else {
			Window window = getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			window.setAttributes(params);

			mPlaycontrolLineLayout.setVisibility(View.VISIBLE);
			mFullscreen2Count = 0;

			mFullscreenFlag2 = false;
		}
	}
	
	/**
	 * 过5秒自动切入全屏模式
	 */
	void autoEnterFullscreen2() {
		if (!mFullscreenFlag2 && mFullscreenFlag && mVideoView.isPlaying()) {
			mPlaycontrolLineLayout.setVisibility(View.GONE);
			Window window = getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			window.setAttributes(params);
										
			mFullscreenFlag2 = true;
		}
	}
	/**
	 * 申请音频焦点
	 */
	private static boolean requestAudioFocus() {
		int ret = mAudioManager.requestAudioFocus(mAudioFocusListener, 
				AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		
		return (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
	}

	/**
	 * 释放音频焦点
	 */
	private static void anandonAudioFocus() {
		int ret = mAudioManager.abandonAudioFocus(mAudioFocusListener);
	}

	// 音频焦点处理
	private static OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {

		@Override
		public void onAudioFocusChange(int focusChange) {
			// TODO Auto-generated method stub
			Log.e(TAG, "mAudioFocusListener:" + focusChange);
			switch (focusChange) {
			case AudioManager.AUDIOFOCUS_LOSS:
				mPausedByTransientLossOfFocus = false;
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            	if (mVideoView.isPlaying()) {
                    mPausedByTransientLossOfFocus = true;
                    pauseVideo();
                }
            	break;
            case AudioManager.AUDIOFOCUS_GAIN:
            	if (mPausedByTransientLossOfFocus) {
                    mPausedByTransientLossOfFocus = false;
                    //继续播放
                    resumeVideo();
                }
            	break;
            default:
            	break;
			}
		}
     	
     };
	
	/**
	 * 当盘符被卸载
	 * @param removedisk
	 */
	void onDiskRemove(String removedisk) {
		if (mSearchPath.contains(removedisk)) {
			if (mVideoView.isPlaying()) {
				mVideoView.stopPlayback();
			}
			updateBottomBtn();
			// 先从当前往后面找一个有效盘符，如果找到则选中这个盘符
			// 如果没找到，则从当前往前面找一个有效盘符
			boolean flag = false;
			if (mCurSelectBtnIndex < MEDIA_DEVICE_COUNT - 1) {
				for (int i = mCurSelectBtnIndex + 1; i < MEDIA_DEVICE_COUNT; i++) {
					if (btnTabs[i].getVisibility() == View.VISIBLE) {
						mCurSelectBtnIndex = i;
						flag = true;
						break;
					}
				}
			}

			if (!flag && mCurSelectBtnIndex > 0) {
				for (int i = mCurSelectBtnIndex - 1; i >= 0; i--) {
					if (btnTabs[i].getVisibility() == View.VISIBLE) {
						mCurSelectBtnIndex = i;
						break;
					}
				}
			}

			btnTabs[mCurSelectBtnIndex].setSelected(true);
			mSearchPath = mAllDiskName[mCurSelectBtnIndex] + dvrdir;
			mFileSearch.startFileSearchThread(mSearchPath, mSearchExt);
		}
	}
	private BroadcastReceiver mMediaMountedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String path = intent.getData().getPath();
            if (Intent.ACTION_MEDIA_REMOVED.equals(action)
                    || Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {
            	onDiskRemove(path);
            } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {   
            	updateBottomBtn();
            }
        }

    };
    
	public static Handler getHandler() {
		return mUpdateUIHandle;
	}
	
	public static Handler mUpdateUIHandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MsgDefine.MSG_SEARH_START:
				break;
			case MsgDefine.MSG_SEARH_OVER:
				mFileListAdapter.notifyDataSetChanged();
				if (msg.arg1 == 0) {
					mNoFileHint.setVisibility(View.VISIBLE);
				}else {
					mNoFileHint.setVisibility(View.GONE);
					if (!mVideoView.isPlaying()) {
						playVideo(mFileSearch.mFileArrayList.get((int) mListView.getSelectedItemId()));
					}					
				}
				break;
			case MsgDefine.MSG_DELETE_FILE:				
				int position = msg.arg1;
				
				//如果删除的文件正在播放，需要先停止播放
				if (mCurPlayVideoName.equalsIgnoreCase(mFileSearch.mFileArrayList.get(position))) {
					if (mVideoView.isPlaying()) {
						mVideoView.stopPlayback();
					}
				}
				
				if (tools.deleteFile(mFileSearch.mFileArrayList.get(position))) {
					mFileSearch.mFileArrayList.remove(position);
					mFileListAdapter.notifyDataSetChanged();
				}
				break;
			default:
				break;
			}
		}
		
	};
	
	/**
	 * 根据当前系统挂载的盘符，隐藏或者显示底部的盘符按钮
	 */
	void updateBottomBtn() {
		EnvironmentATC envATC = new EnvironmentATC(this);
		
		for (int i=0; i<mAllDiskName.length; i++) {
			if(YeconEnv.checkStorageExist(envATC, mAllDiskName[i])){
				btnTabs[i].setVisibility(View.VISIBLE);
			}else {
				btnTabs[i].setVisibility(View.GONE);
			}
		}	
	}
	
	void initMediaMountedReceiver() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);

        filter.addDataScheme("file");
        registerReceiver(mMediaMountedReceiver, filter);
        filter = null;
    }
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_filelist);
		
		mSearchExt.clear();
        mSearchExt.add(".avi");
        mSearchExt.add(".AVI");
        
		btnTabs[0]= findViewById(R.id.btn_tab_internal);
		btnTabs[1]= findViewById(R.id.btn_tab_sd1);
        btnTabs[2]= findViewById(R.id.btn_tab_sd2);
        btnTabs[3]= findViewById(R.id.btn_tab_usb1);
        btnTabs[4]= findViewById(R.id.btn_tab_usb2);
        btnTabs[5]= findViewById(R.id.btn_tab_usb3);
        btnTabs[6]= findViewById(R.id.btn_tab_usb4);
        btnTabs[7]= findViewById(R.id.btn_tab_usb5);
        for(View v:btnTabs){
        	v.setOnClickListener(this);        	
	   	 }
        
        mVideoProgress = (SeekBar)findViewById(R.id.dvr_video_progress);
        mVideoProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				if (mVideoView.canSeekForward() && mVideoView.canSeekBackward()) {
					mVideoView.seekTo(arg0.getProgress());
					mTrackSeekBarFlag = false;
					mFullscreen2Count = 0;
				}				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				mTrackSeekBarFlag = true;
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}
		});
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
       
        //mLoadingVideo = (ProgressBar)findViewById(R.id.loading_video);
        
        mFileListLineLayout = (LinearLayout)findViewById(R.id.filelist_layout);
    	mPlaycontrolLineLayout = (LinearLayout)findViewById(R.id.playcontrol_layout);
    	mBottomLineLayout = (LinearLayout)findViewById(R.id.bottom_layout);
    	
        mNoFileHint = (TextView)findViewById(R.id.nofile_hint);
		mVideoView = (YeconVideoView)findViewById(R.id.dvr_video_view);
		mVideoView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					if (!mFullscreenFlag) {
						switchFullscreen();
					}else {
						//已经是全屏状态则切换底部按钮显示和标题栏隐藏显示
						switchFullscreen2();
					}
					return true;
				}
				
				return false;
			}
		});
		mBtnPrev = (ImageButton)findViewById(R.id.dvr_btn_pre);
		mBtnNext = (ImageButton)findViewById(R.id.dvr_btn_next);
		mBtnPlay = (ImageButton)findViewById(R.id.dvr_btn_play);
		mBtnPause = (ImageButton)findViewById(R.id.dvr_btn_pause);
		mCurTimeText = (TextView)findViewById(R.id.dvr_cur_time);
		mTotalTimeText = (TextView)findViewById(R.id.dvr_total_time);
		mListView = (ListView)findViewById(R.id.lv_all);
		mFileListAdapter = new FileListAdapter(this, mFileSearch);
		mListView.setAdapter(mFileListAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				// TODO Auto-generated method stub
				mCurListViewPos = position;// 更新当前行 
				mFileListAdapter.notifyDataSetChanged();
				playVideo(mFileSearch.mFileArrayList.get(position));
			}
		});
		
		mBtnPrev.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnPause.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		//mMediaController = new MediaController(this);
		
		//视频播放准备好
		mVideoView.setOnPreparedListener(new OnPreparedListener() {			
			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				//mLoadingVideo.setVisibility(View.GONE);
				int curProgress = mVideoView.getCurrentPosition();
				int total = mVideoView.getDuration();
				mVideoProgress.setProgress(curProgress);
				mVideoProgress.setMax(total);
				
				mCurTimeText.setText(tools.millions2hour(curProgress));
				mTotalTimeText.setText(tools.millions2hour(total));
				
				mVideoTimeHandler.postDelayed(mVideoTimeRunnable, 1000);
			}
		});
		//视频播放完毕
		mVideoView.setOnCompletionListener(new OnCompletionListener() {			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				mVideoTimeHandler.removeCallbacks(mVideoTimeRunnable);
				if (canNextVideo()) {
					nextVideo();
				}else {
					anandonAudioFocus();
				}				
			}
		});
		//视频播放出错
		mVideoView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		//显示或者隐藏底部盘符按钮
		updateBottomBtn();
		
		//根据DVR保存的盘符，选中这个盘符，并且搜索这个盘符的文件，列出来
		findValidDisk();
        
        mFileSearch.startFileSearchThread(mSearchPath, mSearchExt);
        initMediaMountedReceiver();
	}

	/**
	 * 找到一个有效的盘符，选中他
	 */
	void findValidDisk() {
		SharedPreferences sharedata = getSharedPreferences("DVR", 0);
        String curRecordFilePath = sharedata.getString("DVR_SavePath", YeconEnv.EXT_SDCARD1_PATH + dvrdir);
        boolean flag = false;
        for (int i=0; i<mAllDiskName.length; i++) {
        	if (curRecordFilePath.startsWith(mAllDiskName[i]) && btnTabs[i].getVisibility() == View.VISIBLE) {
        		//找到了
        		flag = true;
        		btnTabs[i].setSelected(true);
        		mSearchPath = curRecordFilePath;
        		mCurSelectBtnIndex = i;
        		break;
        	}
        }
        if (!flag) {
        	for (int i=MEDIA_DEVICE_COUNT-1; i>=0; i--) {
				if (btnTabs[i].getVisibility() == View.VISIBLE) {
					btnTabs[i].setSelected(true);
					mSearchPath = mAllDiskName[i] + dvrdir ;
    				mCurSelectBtnIndex = i;
    				break;
    			}
			}
        }
	}
	
	private void updateTabsSelected(View selectedView) {
		for (View v : btnTabs) {
			if (v.getId() != selectedView.getId()) {
				v.setSelected(false);
			} else {
				v.setSelected(true);
			}
		}
	}

	/**
	 * 开始播放某个视频
	 * 
	 * @param name
	 */
	static void playVideo(String name) {
		//mLoadingVideo.setVisibility(View.VISIBLE);
		if  (requestAudioFocus()) {
			mCurPlayVideoName = name;
			mBtnPlay.setVisibility(View.GONE);
			mBtnPause.setVisibility(View.VISIBLE);
			mVideoView.setVideoPath(name);
			mVideoView.start();
		}			
	}
	
	/**
	 * 暂停
	 */
	static void pauseVideo() {
		if (mVideoView.isPlaying() && mVideoView.canPause()) {
			mPauseFlag=true;
			mPausePos = mVideoView.getCurrentPosition();
			mVideoView.pause();
			mBtnPlay.setVisibility(View.VISIBLE);
			mBtnPause.setVisibility(View.GONE);
			anandonAudioFocus();
		}
		mFullscreen2Count = 0;
	}
	
	/**
	 * 恢复播放
	 */
	static void resumeVideo() {
		if (mPauseFlag) {
			if (requestAudioFocus()) {
				mBtnPlay.setVisibility(View.GONE);
				mBtnPause.setVisibility(View.VISIBLE);
				mVideoView.start();
				mVideoView.seekTo(mPausePos);
				mPauseFlag = false;
			}			
		}
		mFullscreen2Count = 0;
	}
	
	/**
	 * 暂停或者恢复播放
	 */
	void playPauseVideo() {
		if (mVideoView.isPlaying() && mVideoView.canPause()) {
			mVideoView.pause();
			mBtnPlay.setVisibility(View.VISIBLE);
			mBtnPause.setVisibility(View.GONE);
			//anandonAudioFocus();
			mPauseFlag = true;
		}else if (mPauseFlag) {
			if (requestAudioFocus()) {
				mVideoView.start();
				mBtnPlay.setVisibility(View.GONE);
				mBtnPause.setVisibility(View.VISIBLE);
				mPauseFlag = false;
			}			
		}
		mFullscreen2Count = 0;
	}
	
	/**
	 * 下一首
	 */
	boolean canNextVideo() {
//		if ( (mCurListViewPos+1) < mFileSearch.mFileArrayList.size()) {
//			return true;
//		}else {
//			return false;
//		}
		return true;
	}
	
	void nextVideo() {
		if ( (mCurListViewPos+1) < mFileSearch.mFileArrayList.size()) {
			mCurListViewPos += 1;
		}else {
			mCurListViewPos = 0;
		}
		
		mListView.setSelection(mCurListViewPos);
		mFileListAdapter.notifyDataSetChanged();
		playVideo(mFileSearch.mFileArrayList.get(mCurListViewPos));
		mFullscreen2Count = 0;
	}
	
	/**
	 * 上一首
	 */
	void preVideo() {
		if ( mCurListViewPos > 0) {
			mCurListViewPos -= 1;
			mListView.setSelection(mCurListViewPos);
			mFileListAdapter.notifyDataSetChanged();
			playVideo(mFileSearch.mFileArrayList.get(mCurListViewPos));
			mFullscreen2Count = 0;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mVideoTimeHandler.removeCallbacks(mVideoTimeRunnable);
		if (mVideoView.isPlaying()) {
			mVideoView.stopPlayback();
			anandonAudioFocus();
		}
		
		if (mMediaMountedReceiver != null) {
			unregisterReceiver(mMediaMountedReceiver);
		}		
		
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		pauseVideo();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		resumeVideo();
	}

	void changeDisk(int index) {
		updateTabsSelected(btnTabs[index]);
		mSearchPath = mAllDiskName[index] + dvrdir;
		mFileSearch.startFileSearchThread(mSearchPath, mSearchExt);
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int id = view.getId();		
		switch (id) {
		case R.id.dvr_btn_pre:
			preVideo();
			break;
		case R.id.dvr_btn_next:
			nextVideo();
			break;
		case R.id.dvr_btn_play:
		case R.id.dvr_btn_pause:
			playPauseVideo();
			break;
		case R.id.btn_tab_internal:
			changeDisk(0);
			break;
		case R.id.btn_tab_sd1:
			changeDisk(1);
			break;
		case R.id.btn_tab_sd2:
			changeDisk(2);
			break;
		case R.id.btn_tab_usb1:
			changeDisk(3);
			break;
		case R.id.btn_tab_usb2:
			changeDisk(4);
			break;
		case R.id.btn_tab_usb3:
			changeDisk(5);
			break;
		case R.id.btn_tab_usb4:
			changeDisk(6);
			break;
		case R.id.btn_tab_usb5:
			changeDisk(7);
			break;
		}
	}
	
	@Override
	public boolean onKeyUp(int arg0, KeyEvent arg1) {
		// TODO Auto-generated method stub
		if (mFullscreenFlag) {
			switch2smallVideo();
			return true;
		}
		
		return super.onKeyUp(arg0, arg1);
	}

}
