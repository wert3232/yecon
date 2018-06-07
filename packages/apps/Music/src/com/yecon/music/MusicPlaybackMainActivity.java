
package com.yecon.music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.autochips.media.AtcMediaPlayer;
import com.yecon.common.SourceManager;
import com.yecon.common.YeconEnv;
import com.yecon.music.MediaPlaybackService;
import com.yecon.music.MusicUtils;
import com.yecon.music.MediaPlaybackService.UserActionState;
import com.yecon.music.MusicUtils.ServiceToken;
import com.yecon.music.StepAsyncScanner.STATUS;

import java.lang.reflect.Method;

import static android.mcu.McuExternalConstant.*;
import static com.yecon.music.MusicConstant.*;
import static com.yecon.music.util.DebugUtil.*;
import static android.constant.YeconConstants.*;

@SuppressLint("HandlerLeak")
public class MusicPlaybackMainActivity extends Activity implements OnClickListener,
        ServiceConnection {
	
	private final String TAG = "MusicPlaybackMainActivity";
    private static final int REFRESH = 1;
    private static final int QUIT = 2;
    private static final int GET_ALBUM_ART = 3;
    private static final int ALBUM_ART_DECODED = 4;
    private static final int NO_SD_USB = 5;
    private static final int SD_USB_EXIST = 6;
    private static final int MEDIA_SCAN_STARTED = 7;
    private static final int MEDIA_SCAN_FINISHED = 8;

    private static final int MEDIA_KEY_PROCCESS_DELAY_TIME = 600;

    private static final String STORAGE_STATE_MOUNTED = "mounted";

    public static PlayStatus LAST_STATUS;

    public static boolean IS_REAR_OPENED = false;
    //public static long SAVE_PROGRESS;

    private static boolean autoplay_nexttime = true;

    private static long mLastMediaKeyEventTime;

    public enum PlayStatus {
        STATUS_NONE, STATUS_STOP, STATUS_PAUSE, STATUS_PLAY
    };

    //private int mSavePlayPos;
    //private int mSavePlayListLen;
    //private long[] mSavePlayList = null;

    /**
     * UI variables
     */
    private LinearLayout mLayoutPlay;
    private LinearLayout mLayoutScan;

    private TextView mBtnOpPre;
    private TextView mBtnOpPlay;
    private TextView mBtnOpNext;
    private TextView mBtnOpRand;
    private TextView mBtnOpLoop;
    private TextView mBtnOpList;

    private ImageView mIVAlbum;
    private TextView mTVTrack;
    private TextView mTVArtist;
    private TextView mTVAlbum;
    private SeekBar mSBPlaybackProcess;
    private TextView mTVTrackIndex;
    private TextView mTVProgressTime;
    private TextView mTVTotalTime;

    private ImageView mIVControlAudio;

    /**
     * Service variables
     */
    private ServiceToken mToken;
    private IMediaPlaybackService mService = null;

    private long mPosOverride = -1;
    private long mDuration;

    /**
     * AlbumArt variables
     */
    private Worker mAlbumArtWorker;
    private AlbumArtHandler mAlbumArtHandler;

    Class<?> mStatusBarManager;
    Method mMethod;
    Object mStatusbarService;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // printLog("MusicPlaybackMainActivity - mHandler - msg.what: " +
            // msg.what + " - mPaused: " + mPaused, true);
            switch (msg.what) {
                case ALBUM_ART_DECODED:
                    if (msg.obj != null) {
                        printLog("MusicPlaybackMainActivity - ALBUM_ART_DECODED", true);

                        mIVAlbum.setImageBitmap((Bitmap) msg.obj);
                        mIVAlbum.getDrawable().setDither(true);
                    }
                    break;

                case REFRESH:
                    queueNextRefresh(refreshNow());
                    break;

                case QUIT:
                    // This can be moved back to onCreate once the bug that
                    // prevents Dialogs from being started from
                    // onCreate/onResume is fixed.
                    new AlertDialog.Builder(MusicPlaybackMainActivity.this)
                            .setTitle(R.string.service_start_error_title)
                            .setMessage(R.string.service_start_error_msg)
                            .setPositiveButton(R.string.service_start_error_button,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                            finish();
                                        }
                                    }).setCancelable(false).show();
                    break;

                case NO_SD_USB:
                    mTVTrack.setText(getString(R.string.music_no_file));
                    mBtnOpPre.setEnabled(false);
                    mBtnOpPlay.setEnabled(false);
                    mBtnOpNext.setEnabled(false);
                    mBtnOpRand.setEnabled(false);
                    mBtnOpLoop.setEnabled(false);
                    mBtnOpList.setEnabled(false);
                    break;

                case SD_USB_EXIST:
                    mBtnOpPre.setEnabled(true);
                    mBtnOpPlay.setEnabled(true);
                    mBtnOpNext.setEnabled(true);
                    mBtnOpRand.setEnabled(true);
                    mBtnOpLoop.setEnabled(true);
                    mBtnOpList.setEnabled(true);
                    break;

                case MEDIA_SCAN_STARTED:
                    mLayoutPlay.setVisibility(View.GONE);
                    mLayoutScan.setVisibility(View.VISIBLE);
                    break;

                case MEDIA_SCAN_FINISHED:
                    mLayoutPlay.setVisibility(View.VISIBLE);
                    mLayoutScan.setVisibility(View.GONE);
                    //getTrackList();
                    //startPlayback();
                    break;

                default:
                    break;
            }
        }
    };

    private BroadcastReceiver mQuickBootListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            printLog("MusicPlaybackMainActivity - mQuickBootListener--------action=" + action);
            if (action.equals(MediaPlaybackService.ACTION_QB_POWERON)
                    || action.equals(MCU_ACTION_ACC_ON)) {
                sendBroadcast(new Intent("com.yecon.setting.audio.exit"));
                // if (mCurPlaybackRate != PLAYBACK_RATE_NORMAL) {
                // mCurPlaybackRate = PLAYBACK_RATE_NORMAL;
                // updateTitleIconPlayState(PlayStatus.STATUS_PLAY);
                // }
            } else if (action.equals(MediaPlaybackService.ACTION_QB_POWERON_COMPLETE)) {
                if (mService != null) {
                    updateTrackInfo();
                    queueNextRefresh(refreshNow());
                }
            } else if (action.equals(ACTION_QB_POWEROFF) || action.equals(ACTION_QB_PREPOWEROFF)) {
                //MainApp.exitAllActivity();
            }
        }
    };

    private BroadcastReceiver mCbmListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int commond = intent.getIntExtra("commond", -1);
            printLog("MusicPlaybackMainActivity - mCbmListener - commond: " + commond, true);
            try {
                switch (commond) {
                    case MediaPlaybackService.MEDIA_INFO_CBM_STOP:
                    case MediaPlaybackService.MEDIA_INFO_CBM_PAUSE:
                        // if (mService.isPlaying()) {
                        // // printLog("mCbmListener - pause music");
                        // mService.pause();
                        // }
                        break;

                    case MediaPlaybackService.MEDIA_INFO_CBM_START:
                    case MediaPlaybackService.MEDIA_INFO_CBM_RESUME:
                        if (mService != null
                                && MediaPlaybackService.LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_STARTED) {
                            if (mService.duration() == -1) {
                                printLog("MusicPlaybackMainActivity - mCbmListener - rePlay", true);
                                // mService.rePlay();
                                mHandler.sendEmptyMessage(REFRESH);
                            } else {
                                printLog("MusicPlaybackMainActivity - mCbmListener - play", true);
                                // mService.play();
                                refreshNow();
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {

            }

        }
    };
    
    private boolean isScanning = false;
    private void onLoadingListChanged(Intent intent){
    	if(mService!=null){
    		int state = intent.getIntExtra("scanning", 0);
        	try {
    	    	if(state>0){
    	    		//scanning.
    				if (mService.getPlayListLen()==0) {
    					mHandler.sendEmptyMessage(MEDIA_SCAN_STARTED);
    					isScanning = true;
    				}				
    	    	}
    	    	else{
    	    		//scan or load list end.
    	    		mHandler.sendEmptyMessage(MEDIA_SCAN_FINISHED);
    	    		if (mService.getPlayListLen()==0) {
		                mHandler.sendEmptyMessage(NO_SD_USB);
		            } else {
		                mHandler.sendEmptyMessage(SD_USB_EXIST);
		                if(MediaPlaybackService.LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_STARTED
		                		|| isScanning){
			    			startPlayback(false);
				            MediaPlaybackService.LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_STARTED;
			    		}			                
		            }	   
    	    		isScanning = false;
    	    	}
    		} catch (RemoteException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}    	
    }
    
    private void checkListState(){
    	if(mService != null ){
    		try{
    			if (mService.getPlayListLen()==0) {
    	            mHandler.sendEmptyMessage(NO_SD_USB);
    	        } else {
    	            mHandler.sendEmptyMessage(SD_USB_EXIST);
    	        }
    		}
    		catch (RemoteException e) {
	            e.printStackTrace();
	        }
    	}    	
    }
    private void onMetaChanged(){
    	if(mService != null ){
	    	try {
	    		
	    		if (mService.getPlayListLen()==0) {
	                mHandler.sendEmptyMessage(NO_SD_USB);
	            } else {
	                mHandler.sendEmptyMessage(SD_USB_EXIST);
		            if (mService.duration() > -1) {            	
		                // printLog(
		                // "MusicPlaybackMainActivity - mStatusListener - mService.duration(): "
		                // + mService.duration(), true);
		                mSBPlaybackProcess.setEnabled(true);
		                updateTrackInfo();
		                setPauseButtonImage();
		                queueNextRefresh(1);
		            }
	            }
	        } catch (RemoteException e) {
	            e.printStackTrace();
	        }
    	}
    }
    private void onPlayStateChanged(){
    	setPauseButtonImage();
        updateTrackInfo();
        mHandler.sendEmptyMessage(REFRESH);
    }
    private BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            printLog("MusicPlaybackMainActivity - mStatusListener - action: " + action, false);
            if(action.equals(MediaPlaybackService.LOADING_LIST)){
            	onLoadingListChanged(intent);
            }
            if (action.equals(MediaPlaybackService.META_CHANGED)) {
            	onMetaChanged();
            } else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
            	onPlayStateChanged();
            } else if (action.equals(MediaPlaybackService.FORCESTOPACTIVITY)) {
                Toast.makeText(MusicPlaybackMainActivity.this, R.string.service_start_error_msg,
                        Toast.LENGTH_SHORT).show();
               MainApp.exitAllActivity();
            } else if (action.equals(MediaPlaybackService.FINISHACTIVITY)) {
            	MainApp.exitAllActivity();
            } else if (MediaPlaybackService.OPEN_REAR.equals(action)) {
                openRearAudio();
            }
        }
    };

    private BroadcastReceiver mScanListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            printLog("MusicPlaybackMainActivity - mScanListener - action: " + action, true);
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                
            } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
            
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
               // if (mSdMounted && (mSavePlayListLen == 0)) {
               //     mHandler.sendEmptyMessage(MEDIA_SCAN_STARTED);
               // }
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
			/*
                Cursor cursor = getContentResolver().query(MediaStore.getMediaScannerUri(),
                        null, null, null, null);
                if (cursor != null) {
                    printLog("MusicPlaybackMainActivity - mScanListener - Media Scanner is working");
                    cursor.close();
                } else {
                    printLog("MusicPlaybackMainActivity - mScanListener - media scan finished");
                    mHandler.sendEmptyMessageDelayed(MEDIA_SCAN_FINISHED, 100);
                }
			*/
            }
        }
    };

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar bar) {

        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser || mService == null) {
                return;
            }
            printLog("MusicPlaybackMainActivity - onProgressChanged - progress: " + progress, true);
            mPosOverride = mDuration * progress / 1000;
            printLog("MusicPlaybackMainActivity - onProgressChanged - mPosOverride: "
                    + mPosOverride, true);
            refreshNow();
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {

            try {
                if (mPosOverride >= 0) {
                    mPosOverride = mDuration * mSBPlaybackProcess.getProgress() / 1000;
                    mService.seek(mPosOverride);
                }
            } catch (RemoteException ex) {
            }

            mPosOverride = -1;
        }
    };

    private Object sourceTocken = null;
    private boolean pausedByAudioFocus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainApp.addActivity(this);

        sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.music);
        SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify() {
			
			@Override
			public void onAudioFocusChange(int arg0) {
				// TODO Auto-generated method stub
				//Log.i(TAG, "onAudioFocusChange:" +arg0 );
				switch (arg0) {
                case AudioManager.AUDIOFOCUS_LOSS:
                	Log.i(TAG, "AudioManager.AUDIOFOCUS_LOSS:" );
                	MainApp.exitAllActivity();
                	break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: 
                	try{
                		if(mService!=null){
                			if (mService.isPlaying()) {
                                mService.pause();
                                refreshNow();
                                pausedByAudioFocus = true;
                            } 
                		}                		
                	}
                	catch(Exception e){
                		e.printStackTrace();
                	}
                    break;
                case AudioManager.AUDIOFOCUS_GAIN: 
                	try{
                		if( pausedByAudioFocus){
                			pausedByAudioFocus = false;
                			if(mService!=null){
                				if (mService.duration() == -1) {
                                    mService.rePlay();
                                    mHandler.sendEmptyMessage(REFRESH);
                                } else {
                                    mService.play();
                                    refreshNow();
                                }
                                mSBPlaybackProcess.setEnabled(true);
                			}                			
                		}                		
                	}
                	catch(Exception e){
                		e.printStackTrace();
                	}
                    break;
				}
			}
		});
        printLog("MusicPlaybackMainActivity - onCreate - start", true);

        initData();

        initUI();
        
        mToken = MusicUtils.bindToService(this, this, null);
        if (mToken == null) {
            mHandler.sendEmptyMessage(QUIT);
        }
        // printLog("MusicPlaybackMainActivity - onCreate - end", true);
    }
    
    private void handlerIntent(Intent intent){
    	boolean got = false;
         if(intent!=null){
         	String device = intent.getStringExtra("plugindevice");
             if(device!=null && device.length()>0){
            	 got = true;
             }
         }
         else{
        	 intent = new Intent();
         }
         if(!got){
        	 intent.putExtra("checkall",  true);
         }
         intent.setClass(this, MediaPlaybackService.class);
  		startService(intent);
    }

    @Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
    	printLog("MusicPlaybackMainActivity - onNewIntent - ", true);
    	//handlerIntent(intent);
		super.onNewIntent(intent);
	}

	private void initData() {
        // printLog("MusicPlaybackMainActivity - initData - start", true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mAlbumArtWorker = new Worker("album art worker");
        mAlbumArtHandler = new AlbumArtHandler(mAlbumArtWorker.getLooper());

        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.ACTION_CBM_CHANGE);
        registerReceiver(mCbmListener, f);

        f = new IntentFilter();
        f.addAction(MediaPlaybackService.ACTION_QB_POWERON);
        f.addAction(MediaPlaybackService.ACTION_QB_POWEROFF);
        f.addAction(ACTION_QB_PREPOWEROFF);
        f.addAction(MCU_ACTION_ACC_ON);
        f.addAction(MCU_ACTION_ACC_OFF);
        f.addAction(MediaPlaybackService.ACTION_QB_POWERON_COMPLETE);
        registerReceiver(mQuickBootListener, new IntentFilter(f));

        f = new IntentFilter();
        f.addAction(MediaPlaybackService.LOADING_LIST);
        f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.FORCESTOPACTIVITY);
        f.addAction(MediaPlaybackService.FINISHACTIVITY);
        f.addAction(MediaPlaybackService.OPEN_REAR);
        registerReceiver(mStatusListener, f);
        try {
            Cursor cursor = getContentResolver().query(MediaStore.getMediaScannerUri(),
                    null, null, null, null);
            if (cursor != null) {
                printLog("MusicPlaybackMainActivity - initData - Media Scanner is working");
                mHandler.sendEmptyMessage(MEDIA_SCAN_STARTED);
                cursor.close();
            }
        } catch (Exception e) {
        }

        handlerIntent(getIntent());
        
        mStatusbarService = getSystemService("statusbar");
        
        // printLog("MusicPlaybackMainActivity - initData - end", true);
    }
	/*
    private void getTrackList() {
        printLog("MusicPlaybackMainActivity - getTrackList - start", false);

        SharedPreferences preferences = getSharedPreferences("Music", Context.MODE_PRIVATE);

        if (preferences.contains("curpos")) {
            mSavePlayPos = preferences.getInt("curpos", 0);
        } else {
            mSavePlayPos = 0;
        }

        if (mSavePlayPos < 0) {
            mSavePlayPos = 0;
        }

        printLog("MusicPlaybackMainActivity - getTrackList - mSavePlayPos: " + mSavePlayPos, false);

        String[] cols = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
        };

        String sortOrder = MediaStore.Audio.Media.TITLE_KEY;

        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Audio.Media.TITLE + " != ''");
        where.append(" AND " + MediaStore.Audio.Media.IS_MUSIC + "=1");

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        int count = 0;

        Cursor cursor = getContentResolver().query(uri, cols, where.toString(), null, sortOrder);
        if (cursor != null && cursor.getCount() > 0) {
            mSavePlayList = new long[cursor.getCount()];
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                mSavePlayList[count++] = cursor.getLong(0);
                cursor.moveToNext();
            }

            if (mSavePlayList != null) {
                mSavePlayListLen = mSavePlayList.length;
            }

            if (mSavePlayPos >= mSavePlayListLen) {
                mSavePlayPos = 0;
            }

            cursor.close();
        }

        if (mSavePlayListLen == 0) {
            mHandler.sendEmptyMessage(NO_SD_USB);
        } else {
            mHandler.sendEmptyMessage(SD_USB_EXIST);
        }

        printLog("MediaPlaybackMainActivity - getTrackList - mSavePlayListLen: " + mSavePlayListLen);
    }
	*/
    private void initUI() {
        // printLog("MusicPlaybackMainActivity - initUI - start", true);

        setContentView(R.layout.yecon_music_playback_main_activity);

        mBtnOpPre = (TextView) findViewById(R.id.btn_op_pre);
        mBtnOpPlay = (TextView) findViewById(R.id.btn_op_play);
        mBtnOpNext = (TextView) findViewById(R.id.btn_op_next);
        mBtnOpRand = (TextView) findViewById(R.id.btn_op_rand);
        mBtnOpLoop = (TextView) findViewById(R.id.btn_op_loop);
        mBtnOpList = (TextView) findViewById(R.id.btn_op_list);

        mBtnOpPre.setOnClickListener(this);
        mBtnOpPlay.setOnClickListener(this);
        mBtnOpNext.setOnClickListener(this);
        mBtnOpRand.setOnClickListener(this);
        mBtnOpLoop.setOnClickListener(this);
        mBtnOpList.setOnClickListener(this);

        mIVAlbum = (ImageView) findViewById(R.id.iv_album);
        mTVTrack = (TextView) findViewById(R.id.tv_track);
        mTVArtist = (TextView) findViewById(R.id.tv_artist);
        mTVAlbum = (TextView) findViewById(R.id.tv_album);

        mTVTrackIndex = (TextView) findViewById(R.id.tv_track_index);
        mTVTrackIndex.setVisibility(View.INVISIBLE);

        mSBPlaybackProcess = (SeekBar) findViewById(R.id.sb_music_playback_process);
        mSBPlaybackProcess.setMax(1000);
        mSBPlaybackProcess.setOnSeekBarChangeListener(mSeekListener);

        mTVProgressTime = (TextView) findViewById(R.id.tv_progress_time);
        mTVTotalTime = (TextView) findViewById(R.id.tv_total_time);

        mIVControlAudio = (ImageView) findViewById(R.id.iv_control_audio);
        mIVControlAudio.setOnClickListener(this);

        mLayoutPlay = (LinearLayout) findViewById(R.id.layout_play);
        mLayoutScan = (LinearLayout) findViewById(R.id.layout_scan);
        // printLog("MusicPlaybackMainActivity - initUI - end", true);
    }
    
    @Override
    protected void onStart() {
        super.onStart();

        printLog("MusicPlaybackMainActivity - onStart - start", true);
        
//        Intent input  = getIntent();
//        Intent intent = new Intent();
//        if(input!=null){
//        	String action = input.getAction();
//            if(action.equals("com.yecon.sourcemanager.plugdevice")){
//            	String devPath = input.getStringExtra("device");
//            	if(devPath!=null && devPath.length()>0){
//            		intent = input;
//            	}
//            }
//        }
        
//        mToken = MusicUtils.bindToService(this, this, intent);
//        if (mToken == null) {
//            mHandler.sendEmptyMessage(QUIT);
//        }

//        IntentFilter scanFilter = new IntentFilter();
//        scanFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
//        scanFilter.addAction(Intent.ACTION_MEDIA_EJECT);
//        scanFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
//        scanFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
//        scanFilter.addDataScheme("file");
//        registerReceiver(mScanListener, scanFilter);

        // printLog("MusicPlaybackMainActivity - onStart - end", true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SourceManager.acquireSource(sourceTocken);
        printLog("MusicPlaybackMainActivity - onResume - start", true);

        resetStatusBarBackground(0);

       onMetaChanged();

        // printLog("MusicPlaybackMainActivity - onResume - end", true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        printLog("MusicPlaybackMainActivity - onStop", true);

        mHandler.removeMessages(REFRESH);

        //MusicUtils.unbindFromService(mToken);
        //mService = null;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        MainApp.removeActivity(this);
        mHandler.removeCallbacksAndMessages(null);
        printLog("MusicPlaybackMainActivity - onDestroy", true);
        sendBroadcast(new Intent("com.yecon.setting.audio.exit"));

        if (MediaPlaybackService.LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_STARTED
                || MediaPlaybackService.LAST_PLAYBACK_STATUS == -1) {
            autoplay_nexttime = true;
        }
        else {
            autoplay_nexttime = false;
        }

        stopPlayback();

        unregisterReceiver(mQuickBootListener);
        unregisterReceiver(mCbmListener);
        //unregisterReceiver(mScanListener);
        unregisterReceiver(mStatusListener);

        // unregisterReceiver(mScanMediaFileReceiver);

        mAlbumArtWorker.quit();

        if (MediaPlaybackService.LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_STOPPED
                || MediaPlaybackService.LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_PAUSED) {
            MediaPlaybackService.LAST_PLAYBACK_STATUS = -1;
        }

        MusicUtils.unbindFromService(mToken);
        mService = null;
        
        SourceManager.unregisterSourceListener(sourceTocken);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_op_list: {
                Intent intent = new Intent(this, MusicTrackActivity.class);
                intent.putExtra(MusicConstant.ENTER_FOLDER, "false");
                startActivity(intent);
                break;
            }

            case R.id.btn_op_pre: {
                long now = SystemClock.elapsedRealtime();
                if (now - mLastMediaKeyEventTime > MEDIA_KEY_PROCCESS_DELAY_TIME) {
                    mLastMediaKeyEventTime = now;

                    doPrevious();
                }
                break;
            }

            case R.id.btn_op_play:
                doPauseResume();
                break;

            case R.id.btn_op_next: {
                long now = SystemClock.elapsedRealtime();
                if (now - mLastMediaKeyEventTime > MEDIA_KEY_PROCCESS_DELAY_TIME) {
                    mLastMediaKeyEventTime = now;

                    doNext();
                }
                break;
            }

            case R.id.btn_op_rand:
                toggleShuffle();
                break;

            case R.id.btn_op_loop:
                cycleRepeat();
                break;

            case R.id.iv_control_audio:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setComponent(new ComponentName(SOUND_SETTING_PACKAGE_NAME,
                        SOUND_SETTING_STARTUP_ACTIVITY));
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        printLog("MusicPlaybackMainActivity - onServiceConnected - LAST_PLAYBACK_STATUS: "
                + MediaPlaybackService.LAST_PLAYBACK_STATUS, true);

        mService = IMediaPlaybackService.Stub.asInterface(service);

        try {
        	checkListState();
            // if (MediaPlaybackService.LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_STARTED
            // || MediaPlaybackService.LAST_PLAYBACK_STATUS == -1) {
            //if (autoplay_nexttime) {
                startPlayback(true);
                MediaPlaybackService.LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_STARTED;
            //}
            
            updateTrackInfo();
            setRepeatButtonImage();
            setShuffleButtonImage();
            setPauseButtonImage();
            mHandler.sendEmptyMessage(REFRESH);
            if (mService != null && mService.duration() > -1) {
                mSBPlaybackProcess.setEnabled(true);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

        // printLog("MusicPlaybackMainActivity - onServiceConnected - end",
        // true);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    private void updateTrackInfo() {
        if (mService != null) {
            try {
                String path = mService.getPath();
                if (path == null) {
                    printLog("MusicPlaybackMainActivity - updateTrackInfo - path is null", false);

                    // mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
                    // mAlbumArtHandler.obtainMessage(GET_ALBUM_ART,
                    // new AlbumSongIdWrapper(-1, -1)).sendToTarget();
                    //
                    // mTVTrack.setText(R.string.music_no_track);
                    //
                    // mTVProgressTime.setVisibility(View.INVISIBLE);
                    // mTVTotalTime.setVisibility(View.INVISIBLE);
                    return;
                }

                printLog("MusicPlaybackMainActivity - updateTrackInfo - path: " + path, true);

                mTVProgressTime.setVisibility(View.VISIBLE);
                mTVTotalTime.setVisibility(View.VISIBLE);

                long songid = mService.getAudioId();

                if (songid < 0 && path.toLowerCase().startsWith("http://")) {
                    mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
                    mAlbumArtHandler.obtainMessage(GET_ALBUM_ART,
                            new AlbumSongIdWrapper(-1, -1)).sendToTarget();
                } else {
                    String trackName = mService.getTrackName();
                    mTVTrack.setText(trackName);

                    String artistName = mService.getArtistName();
                    if (MediaStore.UNKNOWN_STRING.equals(artistName)) {
                        artistName = getString(R.string.unknown_artist_name);
                    }
                    mTVArtist.setText(artistName);

                    String albumName = mService.getAlbumName();
                    long albumid = mService.getAlbumId();
                    if (MediaStore.UNKNOWN_STRING.equals(albumName)) {
                        albumName = getString(R.string.unknown_album_name);
                        albumid = -1;
                    }
                    mTVAlbum.setText(albumName);

                    mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
                    mAlbumArtHandler.obtainMessage(GET_ALBUM_ART,
                            new AlbumSongIdWrapper(albumid, songid)).sendToTarget();
                }
                mDuration = mService.duration();
                if (mDuration > 0) {
                    mTVTotalTime.setText(MusicUtils.makeTimeString(this, mDuration / 1000));
                } else {
                    mTVTotalTime.setText("00:00:00");
                }

                String trackIndex = String.format("%02d/%02d", mService.getQueuePosition() + 1,
                        mService.getPlayListLen());
                mTVTrackIndex.setText(trackIndex);
                mTVTrackIndex.setVisibility(View.VISIBLE);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void openRearAudio() {
        try {
            if (mService == null) {
                return;
            }
            mService.openRearAudio();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void closeRearAudio() {
        try {
            mService.closeRearAudio();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void resetStatusBarBackground(int color) {
        if (null != mStatusbarService) {
            try {
                if (null == mStatusBarManager) {
                    mStatusBarManager = Class.forName("android.app.StatusBarManager");
                    mMethod = mStatusBarManager.getMethod("resetBackground", int.class);
                }

                if (null != mMethod) {
                    mMethod.invoke(mStatusbarService, color);
                }
            } catch (Exception e) {
                mStatusBarManager = null;
                mMethod = null;
                e.printStackTrace();
            }
        }
    }

    private void setPauseButtonImage() {
        try {
            if (mService != null) {
                if (mService.isPlaying()) {
                    Drawable drawable = getResources().getDrawable(R.drawable.btn_pause_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    mBtnOpPlay.setCompoundDrawables(null, drawable, null, null);
                    mBtnOpPlay.setText(R.string.str_btn_pause);
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.btn_play_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    mBtnOpPlay.setCompoundDrawables(null, drawable, null, null);
                    mBtnOpPlay.setText(R.string.str_btn_play);
                }
            }
            // refreshRearView(mPauseButton);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private long refreshNow() {
        if (mService == null) {
            printLog("MusicPlaybackMainActivity - refreshNow - mService is null", true);
            return 500;
        }

        try {
            long pos = mPosOverride < 0 ? mService.position() : mPosOverride;
            if ((pos >= 0) && (mDuration > 0)) {
                int progress = (int) (1000 * pos / mDuration);

                if (!mSBPlaybackProcess.isPressed())
                    mSBPlaybackProcess.setProgress(progress);

                mTVProgressTime.setText(MusicUtils.makeTimeString(this, pos / 1000));
                if (mDuration > 0) {
                    mTVTotalTime.setText(MusicUtils.makeTimeString(this, mDuration / 1000));
                } else {
                    mTVTotalTime.setText("00:00:00");
                }

                if (!mService.isPlaying()) {
                    return 500;
                }
            } else {
                mSBPlaybackProcess.setProgress(0);
            }
            // calculate the number of milliseconds until the next full second,
            // so the counter can be updated at just the right time
            long remaining = 1000 - (pos % 1000);

            // approximate how often we would need to refresh the slider to
            // move it smoothly
            int width = mSBPlaybackProcess.getWidth();
            if (width == 0) {
                width = 320;
            }
            long smoothrefreshtime = mDuration / width;

            if (smoothrefreshtime > remaining) {
                return remaining;
            }
            if (smoothrefreshtime < 20) {
                return 20;
            }
            return smoothrefreshtime;
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return 500;
    }

    private void queueNextRefresh(long delay) {
    	 Message msg = mHandler.obtainMessage(REFRESH);
         mHandler.removeMessages(REFRESH);
         mHandler.sendMessageDelayed(msg, delay);
    }

    @SuppressWarnings("unused")
    private void initRepeatMode() {
        try {
            if (mService == null) {
                return;
            }

            int shuffle = mService.getShuffleMode();
            int mode = mService.getRepeatMode();

            if (shuffle == MediaPlaybackService.SHUFFLE_NONE
                    && mode != MediaPlaybackService.REPEAT_ALL
                    && mode != MediaPlaybackService.REPEAT_CURRENT) {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
                mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    private void stopPlayback(){
    	if (mService != null) {
    		try {
    			//notify service , do not auto play.
				mService.userAction(1);
				mService.stop();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
//        Intent pauseIntent = new Intent();
//        pauseIntent.setAction(MediaPlaybackService.SERVICECMD);
//        pauseIntent.putExtra(MediaPlaybackService.CMDNAME,
//                MediaPlaybackService.CMDPAUSE);
//        sendBroadcast(pauseIntent);
    }
    private void startPlayback(boolean try2reload) {
        if (mService == null) {
            return;
        }       
        if (MediaPlaybackService.IS_QB_POWEROFF) {
            // MediaPlaybackService.IS_QB_POWEROFF = false;
            return;
        }        
        try {        	
        	//notify service can play now.
        	mService.userAction(0);
            int playListLen = mService.getPlayListLen();
            boolean isPlaying = mService.isPlaying();
            long duration = mService.duration();
            long id = mService.getAudioId();
            
            StringBuffer log = new StringBuffer();
            log.append("MusicPlaybackMainActivity - startPlayback - PlayListLen: ");
            log.append(playListLen);
            //log.append(" - mSavePlayListLen: ");
            //log.append(mSavePlayListLen);
            log.append(" - isPlaying: ");
            log.append(isPlaying);
            log.append(" - duration: ");
            log.append(duration);
            log.append(" - id: ");
            log.append(id);
            printLog(log.toString(), true);
            
            if (playListLen > 0 && id != -1) {
                if (!isPlaying) {
                    if (mService.duration() == -1) {
                        mService.rePlay();
                    } else {
                        mService.play();
                    }
                }
                // initRepeatMode();
            }

//            if ((playListLen <= 0 || (playListLen > 0 && id == -1)) && mSavePlayListLen > 0
//                    && mSavePlayList != null) {
//                mHandler.sendEmptyMessage(SD_USB_EXIST);
//                printLog("MusicPlaybackMainActivity - startPlayback - play save list", true);
//                mService.open(mSavePlayList, mSavePlayPos, 0, 0);
//                if (mQBCompleted) {
//                    if (SAVE_PROGRESS > 0) {
//                        mService.seek(SAVE_PROGRESS);
//                    }
//                    mQBCompleted = false;
//                }
//                mService.play();
//
//                // initRepeatMode();
//            }

            if (mService.duration() > -1) {
                printLog("MusicPlaybackMainActivity - startPlayback - updateTrackInfo", true);
                updateTrackInfo();
                mSBPlaybackProcess.setEnabled(true);
                long next = refreshNow();
                queueNextRefresh(next);
            }
            else{
            	if(try2reload){
            		printLog("MusicPlaybackMainActivity - reloadPlayList", true);
                	mService.reloadPlayList();
            	}            	
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    private void doPauseResume() {
        try {
            if (mService != null) {
                if (mService.isPlaying()) {
                    printLog("1 MusicPlaybackMainActivity - doPauseResume", true);
                    mService.pause();
                    MediaPlaybackService.LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_PAUSED;
                    refreshNow();
                } else {
                    if (mService.duration() == -1) {
                        printLog("2 MusicPlaybackMainActivity - doPauseResume", true);
                        mService.rePlay();
                        MediaPlaybackService.LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_STARTED;
                        mHandler.sendEmptyMessage(REFRESH);
                    } else {
                        printLog("3 MusicPlaybackMainActivity - doPauseResume", true);
                        mService.play();
                        MediaPlaybackService.LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_STARTED;
                        refreshNow();
                    }
                    mSBPlaybackProcess.setEnabled(true);
                }
                setPauseButtonImage();
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private void doPrevious() {
        if (mService == null) {
            return;
        }

        printLog("1 MusicPlaybackMainActivity - doPrevious", true);

        try {
            mService.prev();
        } catch (RemoteException ex) {
            printLog("2 MusicPlaybackMainActivity - doPrevious", true);
            ex.printStackTrace();
        }
    }

    private void doNext() {
        if (mService == null) {
            return;
        }

        printLog("1 MusicPlaybackMainActivity - doNext", true);

        try {
            mService.next();
        } catch (RemoteException ex) {
            printLog("2 MusicPlaybackMainActivity - doNext", true);
        }
    }

    private void toggleShuffle() {
        if (mService == null) {
            return;
        }

        try {
            if (mService.getPlayListLen() <= 0) {
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            int shuffle = mService.getShuffleMode();
            int mode = mService.getRepeatMode();

            StringBuffer logBuffer = new StringBuffer();
            logBuffer.append("MusicPlaybackMainActivity - toggleShuffle - shuffleMode: ");
            logBuffer.append(shuffle);
            logBuffer.append(" - repeastMode: ");
            logBuffer.append(mode);
            printLog(logBuffer.toString(), true);

            if (shuffle == MediaPlaybackService.SHUFFLE_NONE) {
                mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NORMAL);
                if (mode == MediaPlaybackService.REPEAT_CURRENT) {
                    mService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
                    setRepeatButtonImage();
                }
            } else if (shuffle == MediaPlaybackService.SHUFFLE_NORMAL
                    || shuffle == MediaPlaybackService.SHUFFLE_AUTO) {
                mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
            } else {
                Log.e("mymusic", "Invalid shuffle mode: " + shuffle);
            }
            setShuffleButtonImage();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private void cycleRepeat() {
        if (mService == null) {
            return;
        }

        try {
            if (mService.getPlayListLen() <= 0) {
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            int shuffle = mService.getShuffleMode();
            int mode = mService.getRepeatMode();

            StringBuffer logBuffer = new StringBuffer();
            logBuffer.append("MusicPlaybackMainActivity - cycleRepeat - shuffleMode: ");
            logBuffer.append(shuffle);
            logBuffer.append(" - repeastMode: ");
            logBuffer.append(mode);
            printLog(logBuffer.toString(), true);

            if (mode == MediaPlaybackService.REPEAT_NONE) {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
            } else if (mode == MediaPlaybackService.REPEAT_ALL) {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_CURRENT);
                if (shuffle != MediaPlaybackService.SHUFFLE_NONE) {
                    mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
                    setShuffleButtonImage();
                }
            } else if (mode == MediaPlaybackService.REPEAT_CURRENT) {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_NONE);
            }

            setRepeatButtonImage();
        } catch (RemoteException ex) {
        }

    }

    private void setShuffleButtonImage() {
        if (mService == null) {
            return;
        }

        try {
            int shuffle = mService.getShuffleMode();
            StringBuffer logBuffer = new StringBuffer();
            logBuffer.append("MusicPlaybackMainActivity - setShuffleButtonImage - shuffleMode: ");
            logBuffer.append(shuffle);
            printLog(logBuffer.toString(), false);

            switch (shuffle) {
                case MediaPlaybackService.SHUFFLE_NONE: {
                    Drawable drawable = getResources().getDrawable(R.drawable.btn_rand_off_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    mBtnOpRand.setCompoundDrawables(null, drawable, null, null);
                    mBtnOpRand.setText(R.string.str_btn_rand_off);
                    break;
                }

                //case MediaPlaybackService.SHUFFLE_AUTO:
                    // mShuffleButton.setImageResource(R.drawable.ic_mp_partyshuffle_on_btn);
                //    break;

                default: {
                    Drawable drawable = getResources().getDrawable(R.drawable.btn_rand_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    mBtnOpRand.setCompoundDrawables(null, drawable, null, null);
                    mBtnOpRand.setText(R.string.str_btn_rand);
                    break;
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private void setRepeatButtonImage() {
        if (mService == null) {
            return;
        }

        try {
            int shuffle = mService.getShuffleMode();
            int mode = mService.getRepeatMode();

            StringBuffer logBuffer = new StringBuffer();
            logBuffer.append("MusicPlaybackMainActivity - setRepeatButtonImage - shuffleMode: ");
            logBuffer.append(shuffle);
            logBuffer.append(" - repeastMode: ");
            logBuffer.append(mode);
            printLog(logBuffer.toString(), false);

            switch (mode) {
                case MediaPlaybackService.REPEAT_ALL: {
                    Drawable drawable = getResources().getDrawable(R.drawable.btn_loop_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    mBtnOpLoop.setCompoundDrawables(null, drawable, null, null);
                    mBtnOpLoop.setText(R.string.str_btn_loop);
                    break;
                }

                case MediaPlaybackService.REPEAT_CURRENT: {
                    Drawable drawable = getResources().getDrawable(R.drawable.btn_loop_current_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    mBtnOpLoop.setCompoundDrawables(null, drawable, null, null);
                    mBtnOpLoop.setText(R.string.str_btn_loop_current);
                    break;
                }

                default: {
                    Drawable drawable = getResources().getDrawable(R.drawable.btn_loop_off_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    mBtnOpLoop.setCompoundDrawables(null, drawable, null, null);
                    mBtnOpLoop.setText(R.string.str_btn_loop_off);
                    break;
                }
            }
        } catch (RemoteException ex) {
        }
    }

    private static class AlbumSongIdWrapper {
        public long albumid;
        public long songid;

        AlbumSongIdWrapper(long aid, long sid) {
            albumid = aid;
            songid = sid;
        }
    }

    public class AlbumArtHandler extends Handler {
        private long mAlbumId = -1;

        public AlbumArtHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            long albumid = ((AlbumSongIdWrapper) msg.obj).albumid;
            long songid = ((AlbumSongIdWrapper) msg.obj).songid;
            if (msg.what == GET_ALBUM_ART && (mAlbumId != albumid || albumid < 0)) {
                // while decoding the new image, show the default album art
                Message numsg = mHandler.obtainMessage(ALBUM_ART_DECODED, null);
                mHandler.removeMessages(ALBUM_ART_DECODED);
                mHandler.sendMessageDelayed(numsg, 300);
                // Don't allow default artwork here, because we want to fall
                // back to song-specific
                // album art if we can't find anything for the album.
                Bitmap bm = MusicUtils.getArtwork(MusicPlaybackMainActivity.this,
                        songid, albumid, false);
                if (bm == null) {
                    bm = MusicUtils.getArtwork(MusicPlaybackMainActivity.this,
                            songid, -1);
                    albumid = -1;
                }
                if (bm != null) {
                    numsg = mHandler.obtainMessage(ALBUM_ART_DECODED, bm);
                    mHandler.removeMessages(ALBUM_ART_DECODED);
                    mHandler.sendMessage(numsg);
                }
                mAlbumId = albumid;
            }
        }
    }

    private static class Worker implements Runnable {
        private final Object mLock = new Object();
        private Looper mLooper;

        /**
         * Creates a worker thread with the given name. The thread then runs a
         * {@link android.os.Looper}.
         * 
         * @param name A name for the new thread
         */
        Worker(String name) {
            Thread t = new Thread(null, this, name);
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
            synchronized (mLock) {
                while (mLooper == null) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }

        public Looper getLooper() {
            return mLooper;
        }

        public void run() {
            synchronized (mLock) {
                Looper.prepare();
                mLooper = Looper.myLooper();
                mLock.notifyAll();
            }
            Looper.loop();
        }

        public void quit() {
            mLooper.quit();
        }
    }

}
