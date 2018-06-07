/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yecon.music;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.autochips.media.AtcMediaPlayer;
import com.autochips.storage.EnvironmentATC;
import com.yecon.common.RebootStatus;
import com.yecon.common.SourceManager;
import com.yecon.common.YeconEnv;
import com.yecon.music.StepAsyncScanner.STATUS;

import static android.constant.YeconConstants.*;
import static android.mcu.McuExternalConstant.*;
import static com.yecon.music.util.DebugUtil.*;

/**
 * Provides "background" audio playback capabilities, allowing the user to
 * switch between activities without stopping playback.
 */
public class MediaPlaybackService extends Service {
    /**
     * used to specify whether enqueue() should start playing the new list of
     * files right away, next or once all the currently queued files have been
     * played
     */
    // private static final String LOGTAG = "[APP]MediaPlaybackService";
    public static final String ACTION_CBM_CHANGE = "autochips.music.action.CBM_CHANGE";

    public static final int MEDIA_INFO_CBM_STOP = 2001;
    public static final int MEDIA_INFO_CBM_START = 2002;
    public static final int MEDIA_INFO_CBM_PAUSE = 2003;
    public static final int MEDIA_INFO_CBM_RESUME = 2004;
    public static final int MEDIA_INFO_CBM_EXIT = 2005;
    public static final int MEDIA_INFO_CBM_FORBID = 2006;

    public static int LAST_PLAYBACK_STATUS = -1;

    public static final int NOW = 1;
    public static final int NEXT = 2;
    public static final int LAST = 3;
    public static final int PLAYBACKSERVICE_STATUS = 1;

    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;

    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;

    public final static String ACTION_QB_POWERON_COMPLETE = "autochips.intent.action.QB_POWERON_COMPLETE";

    public static boolean IS_NEED_PLAYBACK_FOR_QB_POWERON = true;
    
    public static final String LOADING_LIST = "com.android.music.loadinglist";
    public static final String PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
    public static final String META_CHANGED = "com.android.music.metachanged";
    public static final String QUEUE_CHANGED = "com.android.music.queuechanged";
    public static final String FORCESTOPACTIVITY = "com.android.music.forcestopactivity";
    public static final String FINISHACTIVITY = "com.android.music.finishactivity";
    public static final String OPEN_REAR = "com.android.music.openrear";

    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";

    public static final String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
    public static final String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
    public static final String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
    public static final String NEXT_ACTION = "com.android.music.musicservicecommand.next";

    private static final int TRACK_ENDED = 1;
    private static final int RELEASE_WAKELOCK = 2;
    private static final int SERVER_DIED = 3;
    private static final int FOCUSCHANGE = 4;
    private static final int FADEDOWN = 5;
    private static final int FADEUP = 6;
    private static final int TRACK_WENT_TO_NEXT = 7;
    private static final int TRY_NEXT_TRACK = 8;
    private static final int MAX_HISTORY_SIZE = 100;

    public static final String ACTION_AVIN_REQUEST_NOTIFY = "yecon.intent.action.AVIN.REQUEST";
    private EnvironmentATC envAtc;

    private MultiPlayer mPlayer;
    private String mFileToPlay;
    private int mShuffleMode = SHUFFLE_NONE;
    private int mRepeatMode = REPEAT_ALL;
    private long[] mAutoShuffleList = null;
    private long[] mPlayList = null;
    private int mPlayListLen = 0;
    private Vector<Integer> mHistory = new Vector<Integer>(MAX_HISTORY_SIZE);
    private Vector<Integer> mHistoryTmp = new Vector<Integer>();
    private Cursor mCursor;
    private int mPlayPos = -1;
    private int mNextPlayPos = -1;
    private final Shuffler mRand = new Shuffler();
    private int mOpenFailedCounter = 0;
    private static int mOnErrorCounter = 0;
    String[] mCursorCols = new String[] {
            "audio._id AS _id",
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.IS_PODCAST,
            MediaStore.Audio.Media.BOOKMARK,
            MediaStore.Audio.Media.YEAR
    };

    private final static int IDCOLIDX = 0;
    private final static int DATACOLIDX = 4;
    private final static int PODCASTCOLIDX = 8;
    private final static int BOOKMARKCOLIDX = 9;

    private BroadcastReceiver mUnmountReceiver = null;
    private WakeLock mWakeLock;
    private int mServiceStartId = -1;
    private boolean mServiceInUse = false;
    private boolean mQuietMode = false;
    //private AudioManager mAudioManager;
    private boolean mQueueIsSaveable = true;
    // used to track what type of audio focus loss caused the playback to pause
    private boolean mPausedByTransientLossOfFocus = false;

    private SharedPreferences mPreferences;
    // We use this to distinguish between different cards when saving/restoring
    // playlists.
    // This will have to change if we want to support multiple simultaneous
    // cards.
    private int mCardId;

    private MediaAppWidgetProvider mAppWidgetProvider = MediaAppWidgetProvider
            .getInstance();

    // interval after which we stop the service when idle
    private static final int IDLE_DELAY = 60000;

    private static final String TAG = "service_list";

    // private RemoteControlClient mRemoteControlClient;

    private ActivityManager mActivityManager;
    private String mPackageName;

    private Handler mMediaplayerHandler = new Handler() {
        float mCurrentVolume = 1.0f;
		

        @Override
        public void handleMessage(Message msg) {
            printLog("MediaPlaybackService - mMediaplayerHandler - msg.what: " + msg.what);
            if (mPlayer == null) {
                return;
            }

            switch (msg.what) {
                case FADEDOWN:
                    mCurrentVolume -= .05f;
                    if (mCurrentVolume > .2f) {
                        mMediaplayerHandler.sendEmptyMessageDelayed(FADEDOWN, 10);
                    } else {
                        mCurrentVolume = .2f;
                    }
                    mPlayer.setVolume(mCurrentVolume);
                    break;
                case FADEUP:
                    mCurrentVolume += .01f;
                    if (mCurrentVolume < 1.0f) {
                        mMediaplayerHandler.sendEmptyMessageDelayed(FADEUP, 10);
                    } else {
                        mCurrentVolume = 1.0f;
                    }
                    mPlayer.setVolume(mCurrentVolume);
                    break;
                case SERVER_DIED:
                    if (isPlaying()) {
                        gotoNext(true, false);
                    } else {
                        // the server died when we were idle, so just
                        // reopen the same song (it will start again
                        // from the beginning though when the user
                        // restarts)
                    	openCurrentPlayPos(true);
                    }
                    break;
                case TRACK_WENT_TO_NEXT:
                    mPlayPos = mNextPlayPos;
                    if (mCursor != null) {
                        mCursor.close();
                        mCursor = null;
                    }
                    mCursor = getCursorForId(mPlayList[mPlayPos]);
                    notifyChange(META_CHANGED);
                    updateNotification();
                    setNextTrack();
                    break;
                case TRACK_ENDED:
                    if (mRepeatMode == REPEAT_CURRENT) {
                        seek(0);
                        play();
                    } else {
                        gotoNext(false, false);
                    }
                    break;
                case TRY_NEXT_TRACK:
                	if(msg.arg1==0){
            			prev(true, true);
            		}
            		else{
            			gotoNext(true, true);
            		}
                	break;
                case RELEASE_WAKELOCK:
                    mWakeLock.release();
                    break;

                case FOCUSCHANGE:
                    // This code is here so we can better synchronize it with
                    // the code that handles fade-in
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                            printLog("MediaPlaybackService - mMediaplayerHandler - AUDIOFOCUS_LOSS");
                            if (isPlaying()) {
                                mPausedByTransientLossOfFocus = false;
                            }
                            pause();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            mMediaplayerHandler.removeMessages(FADEUP);
                            mMediaplayerHandler.sendEmptyMessage(FADEDOWN);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            printLog("MediaPlaybackService - mMediaplayerHandler - AUDIOFOCUS_LOSS_TRANSIENT");
                            if (isPlaying()) {
                                mPausedByTransientLossOfFocus = true;
                            }
                            pause();
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            printLog("MediaPlaybackService - mMediaplayerHandler - AUDIOFOCUS_GAIN");
                            if (!isPlaying() && mPausedByTransientLossOfFocus) {
                                mPausedByTransientLossOfFocus = false;
                                mCurrentVolume = 0f;
                                mPlayer.setVolume(mCurrentVolume);
                                play(); // also queues a fade-in
                            } else {
                                mMediaplayerHandler.removeMessages(FADEDOWN);
                                mMediaplayerHandler.sendEmptyMessage(FADEUP);
                            }
                            break;
                        default:
                            printLog("Unknown audio focus change code");
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");

            StringBuffer log = new StringBuffer();
            log.append("MediaPlaybackService - mIntentReceiver - action: ");
            log.append(action);
            log.append(" - cmd: ");
            log.append(cmd);
            printLog(log.toString());

            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                gotoNext(true, false);
            } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
                prev(true, false);
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
                if (isPlaying()) {
                    pause();
                    mPausedByTransientLossOfFocus = false;
                    LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_PAUSED;
                } else {
                    play();
                    LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_STARTED;
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
                pause();
                mPausedByTransientLossOfFocus = false;
                LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_PAUSED;
            } else if (CMDPLAY.equals(cmd)) {
                play();
                LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_STARTED;
            } else if (CMDSTOP.equals(cmd)) {
                pause();
                mPausedByTransientLossOfFocus = false;
                seek(0);
                LAST_PLAYBACK_STATUS = AtcMediaPlayer.MEDIA_PLAYER_STOPPED;
            } else if (MediaAppWidgetProvider.CMDAPPWIDGETUPDATE.equals(cmd)) {
                // Someone asked us to refresh a set of specific widgets,
                // probably because they were just added.
                int[] appWidgetIds = intent
                        .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                mAppWidgetProvider.performUpdate(MediaPlaybackService.this,
                        appWidgetIds);
            }
        }
    };

//    private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
//        public void onAudioFocusChange(int focusChange) {
//            printLog("MediaPlaybackService - mAudioFocusListener");
//            mMediaplayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0)
//                    .sendToTarget();
//        }
//    };

    public boolean isActivityOnForeground() {
        List<RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        if (taskInfo.size() > 0) {
            StringBuffer log = new StringBuffer();
            log.append("MediaPlaybackService - isActivityOnForeground - mPackageName: ");
            log.append(mPackageName);
            log.append(" - currentRunPackageName: ");
            log.append(taskInfo.get(0).topActivity.getPackageName());
            printLog(log.toString());

            if (mPackageName.equalsIgnoreCase(taskInfo.get(0).topActivity
                    .getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean IS_QB_POWEROFF = false;
    private boolean pauseByAccOff = false;
    private BroadcastReceiver mQuickBootListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            printLog("MediaPlaybackService - mQuickBootListener - action: " + action);
            
            if(action.equals(MCU_ACTION_ACC_ON)){
            	if(pauseByAccOff){
            		pauseByAccOff = false;
            		if(mPlayer!=null && AtcMediaPlayer.MEDIA_PLAYER_PAUSED == mPlayer.getCurrentRealState()){
            			mPlayer.start();
            		}
            	}
            }
            else if(action.equals(MCU_ACTION_ACC_OFF)){
            	if(mPlayer!=null && AtcMediaPlayer.MEDIA_PLAYER_STARTED == mPlayer.getCurrentRealState()){
            		mPlayer.pause();
            		pauseByAccOff = true;
            	}
            }
            else if (action.equals(ACTION_QB_POWERON)) {
            	IS_QB_POWEROFF = false;
                quickBootResume();
                if(pauseByAccOff){
                	pauseByAccOff = false;
	                if (userActionState == UserActionState.STARTED && LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_STARTED) {
	                    printLog("1 MediaPlaybackService - mQuickBootListener - play");
	                    play();
	                }
                }            	
                sendBroadcast(new Intent(ACTION_QB_POWERON_COMPLETE));
            } else if (action.equals(ACTION_QB_POWEROFF) || action.equals(ACTION_QB_PREPOWEROFF)) {
            	IS_QB_POWEROFF = true;
            	MediaPlaybackService.this.sendBroadcast(new Intent("com.yecon.setting.audio.exit"));
                if (mPlayer != null) {
                	 if(AtcMediaPlayer.MEDIA_PLAYER_STARTED == mPlayer.getCurrentRealState()){
                     	pauseByAccOff = true;
                     }
                    saveQueue(false);
                    savePlayListMemory(true);
                    quickBootRelease();   
                    Intent finishactivity = new Intent(FINISHACTIVITY);
                    sendBroadcast(finishactivity);
                }               
            }
        }
    };
    
    /**
     * jy 20150114 add for music
     */
    private BroadcastReceiver mMediaKeyListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (this) {

                if(!MusicUtils.isMusicSource()){
					return;
				}

                String action = intent.getAction();
                printLog("MediaPlaybackService - mMediaKeyListener - action: " + action);

                if (action.equals(MCU_ACTION_MEDIA_NEXT)) {
                    gotoNext(true, false);
                } else if (action.equals(MCU_ACTION_MEDIA_PREVIOUS)) {
                    prev(true, false);
                } else if (action.equals(MCU_ACTION_MEDIA_PLAY_PAUSE)) {
                    if (isPlaying()) {
                        printLog("MediaPlaybackService - mMediaKeyListener - pause");
                        pause();
                        LAST_PLAYBACK_STATUS =
                                AtcMediaPlayer.MEDIA_PLAYER_PAUSED;
                    } else {
                        LAST_PLAYBACK_STATUS =
                                AtcMediaPlayer.MEDIA_PLAYER_STARTED;
                        if (duration() == -1) {
                            printLog("MediaPlaybackService - mMediaKeyListener - rePlay");
                            rePlay();
                        } else {
                            printLog("MediaPlaybackService - mMediaKeyListener - play");
                            play();
                        }
                    }
                }
            }
        }
    };

    void resumePlaybackStatus(long seek) {
        try {
            if (LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_STARTED
                    || LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_PAUSED) {
                rePlayFromSpecifyPosition(seek);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void quickBootRelease() {
        printLog("quickBootRelease--------enter");
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    void quickBootResume() {
        printLog("quickBootResume--------enter");
        if (mPlayer == null) {
            printLog("quickBootResume--------=2");
            mPlayer = new MultiPlayer();
            mPlayer.setHandler(mMediaplayerHandler);
        }
        printLog("quickBootResume--------leave");
    }

    public int getCurrentRealState() {
        if (mPlayer != null) {
            return mPlayer.getCurrentRealState();
        }
        return -1;
    }

    public MediaPlaybackService() {
    }

    @SuppressLint({
            "WorldWriteableFiles", "WorldReadableFiles"
    })
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();

        printLog("MediaPlaybackService - onCreate - start");

        envAtc = new EnvironmentATC(this);
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mPackageName = getPackageName();

//        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        ComponentName rec = new ComponentName(getPackageName(),
//                MediaButtonIntentReceiver.class.getName());
//        mAudioManager.registerMediaButtonEventReceiver(rec);

        // mRemoteControlClient = new RemoteControlClient(rec);
        // mAudioManager.registerRemoteControlClient(mRemoteControlClient);
        //
        // int flags = RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
        // | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
        // | RemoteControlClient.FLAG_KEY_MEDIA_PLAY
        // | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
        // | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
        // | RemoteControlClient.FLAG_KEY_MEDIA_STOP;
        // mRemoteControlClient.setTransportControlFlags(flags);

        mPreferences = getSharedPreferences("Music", MODE_WORLD_READABLE
                | MODE_WORLD_WRITEABLE);
        mCardId = MusicUtils.getCardId(this);

        registerExternalStorageListener();

        // Needs to be done in this thread, since otherwise
        // ApplicationContext.getPowerManager() crashes.
        mPlayer = new MultiPlayer();
        mPlayer.setHandler(mMediaplayerHandler);

        if (RebootStatus.isReboot(RebootStatus.SOURCE.SOURCE_MUSIC)) {
            clearUserData();
        }
        else {
            //reloadQueue();
        	loadPlayListMemory();
        }
        //notifyChange(QUEUE_CHANGED);
        //notifyChange(META_CHANGED);

        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(SERVICECMD);
        commandFilter.addAction(TOGGLEPAUSE_ACTION);
        commandFilter.addAction(PAUSE_ACTION);
        commandFilter.addAction(NEXT_ACTION);
        commandFilter.addAction(PREVIOUS_ACTION);
        registerReceiver(mIntentReceiver, commandFilter);

        IntentFilter f = new IntentFilter();
        f.addAction(ACTION_QB_POWERON);
        f.addAction(ACTION_QB_POWEROFF);
        f.addAction(ACTION_QB_PREPOWEROFF);
        f.addAction(MCU_ACTION_ACC_ON);
        f.addAction(MCU_ACTION_ACC_OFF);
        registerReceiver(mQuickBootListener, new IntentFilter(f));

        /**
         * jy 20150114 add for music
         */
        IntentFilter mediaKeyFilter = new IntentFilter();
        mediaKeyFilter.addAction(MCU_ACTION_MEDIA_NEXT);
        mediaKeyFilter.addAction(MCU_ACTION_MEDIA_PREVIOUS);
        mediaKeyFilter.addAction(MCU_ACTION_MEDIA_PLAY_PAUSE);
        registerReceiver(mMediaKeyListener, mediaKeyFilter);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
                .getClass().getName());
        mWakeLock.setReferenceCounted(false);

        // If the service was idle, but got killed before it stopped itself, the
        // system will relaunch it. Make sure it gets stopped again in that
        // case.
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);

        printLog("MediaPlaybackService - onCreate - end");
    }

    private void clearUserData() {
        Log.i("clear", "music data");
        Editor ed = mPreferences.edit();
        ed.remove("cardid");
        ed.remove("repeatmode");
        ed.remove("shufflemode");
        ed.remove("queue");
        ed.remove("curpos");
        ed.remove("seekpos");
        SharedPreferencesCompat.apply(ed);
        mShuffleMode = SHUFFLE_NONE;
        mRepeatMode = REPEAT_ALL;
        mPlayPos = 0;
        mNextPlayPos = 0;
        mPlayListLen = 0;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("InlinedApi")
    @Override
    public void onDestroy() {
        printLog("MediaPlaybackService - onDestroy - start");

        // Check that we're not being destroyed while something is still
        // playing.
        if (isPlaying()) {
            printLog("Service being destroyed while still playing.");
        }
        // release all MediaPlayer resources, including the native player and
        // wakelocks
        Intent i = new Intent(
                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(i);

        if (mPlayer != null) {
            mPlayer.release();
        }
        mPlayer = null;

        //mAudioManager.abandonAudioFocus(mAudioFocusListener);
        // mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);

        // make sure there aren't any other messages coming
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mMediaplayerHandler.removeCallbacksAndMessages(null);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        unregisterReceiver(mIntentReceiver);
        unregisterReceiver(mQuickBootListener);
        /**
         * jy 20150114 add for music
         */
        unregisterReceiver(mMediaKeyListener);
        if (mUnmountReceiver != null) {
            unregisterReceiver(mUnmountReceiver);
            mUnmountReceiver = null;
        }
        mWakeLock.release();
        super.onDestroy();

        printLog("MediaPlaybackService - onDestroy - end");
    }

    private final char hexdigits[] = new char[] {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private void saveQueue(boolean full) {
        if (mPlayer == null || !mQueueIsSaveable) {
            return;
        }

        Editor ed = mPreferences.edit();

        if (full) {
            printLog("MediaPlaybackService - saveQueue - mPlayListLen: " + mPlayListLen);
            savePlayListMemory(false);
        }

        printLog("MediaPlaybackService - saveQueue - mPlayPos: " + mPlayPos);
        
        ed.putInt("repeatmode", mRepeatMode);
        ed.putInt("shufflemode", mShuffleMode);
        SharedPreferencesCompat.apply(ed);
    }

//    private void reloadQueue() {
//        if (mPlayer == null) {
//            return;
//        }
//
//        String q = null;
//
//        printLog("MediaPlaybackService - reloadQueue - start");
//
//        // boolean newstyle = false;
//        int id = mCardId;
//        if (mPreferences.contains("cardid")) {
//            // newstyle = true;
//            id = mPreferences.getInt("cardid", ~mCardId);
//        }
//        if (id == mCardId) {
//            // Only restore the saved playlist if the card is still
//            // the same one as when the playlist was saved
//            q = mPreferences.getString("queue", "");
//        }
//
//        int qlen = q != null ? q.length() : 0;
//
//        StringBuffer log = new StringBuffer();
//        log.append("MediaPlaybackService - reloadQueue - mCardId: ");
//        log.append(mCardId);
//        log.append(" - id: ");
//        log.append(id);
//        log.append(" - qlen: ");
//        log.append(qlen);
//        // log.append(" - queue: ");
//        // log.append(q);
//        printLog(log.toString());
//
//        if (qlen > 1) {
//            int plen = 0;
//            int n = 0;
//            int shift = 0;
//            for (int i = 0; i < qlen; i++) {
//                char c = q.charAt(i);
//                if (c == ';') {
//                    ensurePlayListCapacity(plen + 1);
//                    mPlayList[plen] = n;
//                    plen++;
//                    n = 0;
//                    shift = 0;
//                } else {
//                    if (c >= '0' && c <= '9') {
//                        n += ((c - '0') << shift);
//                    } else if (c >= 'a' && c <= 'f') {
//                        n += ((10 + c - 'a') << shift);
//                    } else {
//                        // bogus playlist data
//                        plen = 0;
//                        break;
//                    }
//                    shift += 4;
//                }
//            }
//            mPlayListLen = plen;
//
//            int pos = mPreferences.getInt("curpos", 0);
//            if (pos < 0 || pos >= mPlayListLen) {
//                // The saved playlist is bogus, discard it
//                mPlayListLen = 0;
//                return;
//            }
//            mPlayPos = pos;
//
//            StringBuffer log2 = new StringBuffer();
//            log2.append("MediaPlaybackService - reloadQueue - mPlayListLen: ");
//            log2.append(mPlayListLen);
//            log2.append(" - mPlayPos: ");
//            log2.append(mPlayPos);
//            // log2.append(" - mPlayList: ");
//            // if (mPlayList != null) {
//            // for (int i = 0; i < mPlayListLen; i++) {
//            // log2.append(mPlayList[i]);
//            // log2.append(" ");
//            // }
//            // }
//            printLog(log2.toString());
//
//            // When reloadQueue is called in response to a card-insertion,
//            // we might not be able to query the media provider right away.
//            // To deal with this, try querying for the current file, and if
//            // that fails, wait a while and try again. If that too fails,
//            // assume there is a problem and don't restore the state.
//            Cursor crsr = MusicUtils.query(this,
//                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    new String[] {
//                    "_id"
//                    }, "_id=" + mPlayList[mPlayPos], null,
//                    null);
//            if (crsr == null || crsr.getCount() == 0) {
//                printLog("MediaPlaybackService - reloadQueue - get data again");
//                // wait a bit and try again
//                SystemClock.sleep(1500);
//                if (crsr != null) {
//                    crsr.close();
//                }
//                crsr = getContentResolver().query(
//                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                        mCursorCols, "_id=" + mPlayList[mPlayPos], null, null);
//            }
//            if (crsr != null) {
//                crsr.close();
//            }
//
//            // Make sure we don't auto-skip to the next song, since that
//            // also starts playback. What could happen in that case is:
//            // - music is paused
//            // - go to UMS and delete some files, including the currently
//            // playing one
//            // - come back from UMS
//            // (time passes)
//            // - music app is killed for some reason (out of memory)
//            // - music service is restarted, service restores state, doesn't
//            // find
//            // the "current" file, goes to the next and: playback starts on its
//            // own, potentially at some random inconvenient time.
//            mOpenFailedCounter = 20;
//            mQuietMode = false;
//            printLog("MediaPlaybackService - reloadQueue - start call openCurrentAndNext");
//            openCurrentPlayPos(true);
//            mQuietMode = false;
//            if (!mPlayer.isInitialized()) {
//                // couldn't restore the saved state
//                mPlayListLen = 0;
//                return;
//            }
//
//            long seekpos = mPreferences.getLong("seekpos", 0);
//            seek(seekpos >= 0 && seekpos < duration() ? seekpos : 0);
//
//            int repmode = mPreferences.getInt("repeatmode", REPEAT_NONE);
//            if (repmode != REPEAT_ALL && repmode != REPEAT_CURRENT) {
//                repmode = REPEAT_NONE;
//            }
//            mRepeatMode = repmode;
//
//            int shufmode = mPreferences.getInt("shufflemode", SHUFFLE_NONE);
//            if (shufmode != SHUFFLE_AUTO && shufmode != SHUFFLE_NORMAL) {
//                shufmode = SHUFFLE_NONE;
//            }
//            if (shufmode != SHUFFLE_NONE) {
//                // in shuffle mode we need to restore the history too
//                q = mPreferences.getString("history", "");
//                qlen = q != null ? q.length() : 0;
//                if (qlen > 1) {
//                    plen = 0;
//                    n = 0;
//                    shift = 0;
//                    mHistory.clear();
//                    for (int i = 0; i < qlen; i++) {
//                        char c = q.charAt(i);
//                        if (c == ';') {
//                            if (n >= mPlayListLen) {
//                                // bogus history data
//                                mHistory.clear();
//                                break;
//                            }
//                            mHistory.add(n);
//                            n = 0;
//                            shift = 0;
//                        } else {
//                            if (c >= '0' && c <= '9') {
//                                n += ((c - '0') << shift);
//                            } else if (c >= 'a' && c <= 'f') {
//                                n += ((10 + c - 'a') << shift);
//                            } else {
//                                // bogus history data
//                                mHistory.clear();
//                                break;
//                            }
//                            shift += 4;
//                        }
//                    }
//                }
//            }
//            if (shufmode == SHUFFLE_AUTO) {
//                if (!makeAutoShuffleList()) {
//                    shufmode = SHUFFLE_NONE;
//                }
//            }
//            mShuffleMode = shufmode;
//
//            StringBuffer log3 = new StringBuffer();
//            log3.append("MediaPlaybackService - reloadQueue - position: ");
//            log3.append(position());
//            log3.append(" - duration: ");
//            log3.append(duration());
//            log3.append(" - seekpos: ");
//            log3.append(seekpos);
//            log3.append(" - mRepeatMode: ");
//            log3.append(mRepeatMode);
//            log3.append(" - mShuffleMode: ");
//            log3.append(mShuffleMode);
//            printLog(log3.toString());
//        }
//
//        printLog("MediaPlaybackService - reloadQueue - end");
//    }

    private void handlerInsertDevScan(Intent intent){
    	 if(intent!=null){
    		 String devPath = intent.getStringExtra("plugindevice");
          	if(devPath!=null && devPath.length()>0){
          		if(YeconEnv.checkStorageExist(envAtc, devPath)){
          			Log.i(TAG, "handlerInsertDevScan " + devPath);
          			savePlugedDevices(devPath, true);
      				reloadPlayList();    				
      			}
          	}
          	else{
          		boolean checkall = intent.getBooleanExtra("checkall", false);
          		if(checkall){
          			clearPlugedDevices();
          			if(YeconEnv.checkStorageExist(envAtc, YeconEnv.EXT_SDCARD1_PATH)){
          				savePlugedDevices(YeconEnv.EXT_SDCARD1_PATH, true);
          			}
          			if(YeconEnv.checkStorageExist(envAtc, YeconEnv.EXT_SDCARD2_PATH)){
          				savePlugedDevices(YeconEnv.EXT_SDCARD2_PATH, true);
          			}
          			if(YeconEnv.checkStorageExist(envAtc, YeconEnv.UDISK1_PATH)){
          				savePlugedDevices(YeconEnv.UDISK1_PATH, true);
          			}
          			if(YeconEnv.checkStorageExist(envAtc, YeconEnv.UDISK2_PATH)){
          				savePlugedDevices(YeconEnv.UDISK2_PATH, true);
          			}
          			if(YeconEnv.checkStorageExist(envAtc, YeconEnv.UDISK3_PATH)){
          				savePlugedDevices(YeconEnv.UDISK3_PATH, true);
          			}
          			if(YeconEnv.checkStorageExist(envAtc, YeconEnv.UDISK4_PATH)){
          				savePlugedDevices(YeconEnv.UDISK4_PATH, true);
          			}
          			if(YeconEnv.checkStorageExist(envAtc, YeconEnv.UDISK5_PATH)){
          				savePlugedDevices(YeconEnv.UDISK5_PATH, true);
          			}
          		}
          	}
         }
    }
    @Override
    public IBinder onBind(Intent intent) {
        printLog("MediaPlaybackService - onBind");
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mServiceInUse = true;
        //handlerInsertDevScan(intent);
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        printLog("MediaPlaybackService - onRebind");
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mServiceInUse = true;
    }
    
    private void startMainActivity(){
    	Intent it = new Intent(Intent.ACTION_MAIN);
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.addCategory("android.intent.category.LAUNCHER");
        ComponentName cn = new ComponentName(this.getPackageName(), MusicPlaybackMainActivity.class.getName());
        it.setComponent(cn);
        startActivity(it);
    }
    
    enum UserActionState{
    	STARTED,
    	STOPED
    };
	private UserActionState userActionState = UserActionState.STARTED;
	private boolean isScanning = false;
	private int curListMode;//0: all list, 1: artist, 2: album, 3: folder.
	private long curFieldId;// album id or artist id.
	private long curAudioId;
	private long resumeAudioId;
	private long resumeSeekPos;
	private String curPlayPath="";
    private StepAsyncScanner stepAsyncScanner;
    private void loadPlayListMemory(){
    	curListMode = mPreferences.getInt("curListMode", 0);
    	curFieldId = mPreferences.getLong("curFieldId", 0);
    	resumeAudioId = curAudioId = mPreferences.getLong("curid", 0);
    	resumeSeekPos =  mPreferences.getLong("seekpos", 0);
    	curPlayPath = mPreferences.getString("curPlayPath", "");    	
    	
    	Log.i(TAG, "loadPlayListMemory - " + curListMode + "," +  curFieldId + "," + curAudioId + ","+curPlayPath);       
    }
    private void savePlayListMemory(boolean savePos){
    	if(mPlayer==null || mPreferences==null){
    		return;
    	}
    	Editor ed = mPreferences.edit();
    	ed.putInt("curListMode", curListMode);
    	ed.putLong("curFieldId", curFieldId);
    	ed.putString("curPlayPath", curPlayPath);
    	if(savePos){
    		long curId = mPreferences.getLong("curid", 0);
            ed.putInt("curpos", mPlayPos);
            if(mPlayPos>=0 && mPlayPos<mPlayList.length){
            	ed.putLong("curid", mPlayList[mPlayPos]);
            }
            else{
            	ed.putLong("curid", -1);
            }
            if (mPlayer.isInitialized()) {
            	ed.putLong("seekpos", mPlayer.position());
            }    
    	}
    	ed.commit();
    }
    private void clearPlayListMemory(){
    	Editor ed = mPreferences.edit();
    	ed.remove("curListMode");
    	ed.remove("curFieldId");
    	ed.remove("curPlayPath");
    	ed.commit();
    }
    
    private void cancelReloadPlayList(){
    	if(stepAsyncScanner!=null){
    		stepAsyncScanner.cancel();
    	}
    	isScanning = false;
    }
    
    private void notifyLoadListState(){
    	 Intent it = new Intent(LOADING_LIST);
    	 it.putExtra("scanning", isScanning?1:0);
    	this.sendBroadcast(it);
    }
    private void reloadPlayList() {    	
    	
    	loadPlayListMemory();
    	
    	String[]  cols = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
        };
    	Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	String sortOrder = MediaStore.Audio.Media.TITLE_KEY;
    	StringBuilder where = new StringBuilder();;
        //uri = uri.buildUpon().appendQueryParameter("limit", ""+SCAN_STEP_COUNT).build();
        Cursor crsr=null;
        try{
        	if(0==curListMode){
            	Log.i(TAG, "it's all songs type");         
            }
            else if(curListMode == 1){
            	Log.i(TAG, "it's artist type");
                 crsr = MusicUtils.query(this,
                         MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                         new String[] {
                		 MediaStore.Files.FileColumns._ID
                         }, MediaStore.Audio.Media.ARTIST_ID + "=" + curFieldId, null,
                         null);
        	}
        	else if(curListMode == 2){
        		Log.i(TAG, "it's album type");
                crsr = MusicUtils.query(this,
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[] {
                		MediaStore.Files.FileColumns._ID
                        }, MediaStore.Audio.Media.ALBUM_ID + "=" + curFieldId, null,
                        null);
        	}
        	else if(curListMode == 3){
        		Log.i(TAG, "it's folder type");
                crsr = MusicUtils.query(this,
                		MediaStore.Files.getContentUri("external"),
                        new String[] {
                		MediaStore.Files.FileColumns._ID
                        }, MediaStore.Files.FileColumns.PARENT + "=" + curFieldId, null,
                        null);
        	}
        	else{
        		Log.w(TAG, "unknonw list mode : " + curListMode);
        		return;
        	}
        }
        catch(Exception e){
        	e.printStackTrace();
        	crsr = null;
        }        
        if(crsr == null || crsr.getCount()==0){
        	if(0!=curListMode){
        		Log.i(TAG, "media database is changed , we will scann all list to replay.");
        	}
        	curListMode = 0;
        }
        if(crsr!=null){
        	crsr.close();
        }
        
        if(0==curListMode){
            where.append(MediaStore.Audio.Media.TITLE + " != ''");
            where.append(" AND " + MediaStore.Audio.Media.IS_MUSIC + "=1");            
        }
        else if(curListMode == 1){
             where.append(MediaStore.Audio.Media.TITLE);
             where.append(" != '' AND ");
             where.append(MediaStore.Audio.Media.ARTIST_ID);
             where.append("=");
             where.append(curFieldId);
             where.append(" AND ");
             where.append(MediaStore.Audio.Media.IS_MUSIC);
             where.append("=1");
    	}
    	else if(curListMode == 2){
            where.append(MediaStore.Audio.Media.TITLE);
            where.append(" != '' AND ");
            where.append(MediaStore.Audio.Media.ALBUM_ID);
            where.append("=");
            where.append(curFieldId);
            where.append(" AND ");
            where.append(MediaStore.Audio.Media.IS_MUSIC);
            where.append("=1");
    	}
    	else if(curListMode == 3){
    		uri = MediaStore.Files.getContentUri("external");
    		where.append(MediaStore.Files.FileColumns.TITLE);
            where.append(" != '' AND ");
            where.append(MediaStore.Files.FileColumns.PARENT);
            where.append("=");
            where.append(curFieldId);
            where.append(" AND ");
            where.append(MediaStore.Audio.Media.IS_MUSIC);
            where.append("=1");
    	}
    	else{
    		Log.w(TAG, "unknonw list mode : " + curListMode);
    		return;
    	}
                
        if(stepAsyncScanner==null){
        	stepAsyncScanner = new StepAsyncScanner(this, 50, new StepAsyncScanner.OnResult() {
        		       		
				@Override
        		public void onResult(Cursor cursor, STATUS status) {
        			// TODO Auto-generated method stub
					if(STATUS.SCANEND == status || STATUS.SCANFIRST == status || STATUS.SCANNING == status){
						if(isScanning){
							isScanning = false;
							notifyLoadListState();
						}
						if(STATUS.SCANEND == status){
							notifyLoadListState();
						}
					}
        			if(cursor!=null && cursor.getCount()>0){        
        				isScanning = false;        				
    					//int index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
    					int count = 0;
    					int pos = 0;
    					long []list = new long[cursor.getCount()];
	    	            cursor.moveToFirst();
	    	            while (!cursor.isAfterLast()) {
	    	            	if(STATUS.SCANFIRST == status){
	    	            		//if(curPlayPath!=null && curPlayPath.length()>0 && curPlayPath.equals(cursor.getString(index))){
	    	            		if(curAudioId == cursor.getLong(0)){
		    	            		pos = count;
		    	            	}
	    	            	}
	    	            	list[count++] = cursor.getLong(0);
	    	                cursor.moveToNext();
	    	            }
	    	            if(STATUS.SCANFIRST == status && list.length>0){
	    	            	Log.d(TAG, "First update, begin play: " + list.length);
	    	            	if(userActionState == UserActionState.STARTED){
	    	            		if(isPlaying()){
		    	            		updateList(list, true);
		    	            	}
		    	            	else{
		    	            		open(list, pos, curListMode, curFieldId);
		    	            	}
	    	            	}
	    	            	else{
	    	            		updateList(list, false);
	    	            	}
	    	            }
	    	            else if(list.length>0){
	    	            	updateList(list, userActionState == UserActionState.STARTED);
	    	            }
    				}        			
        		}
        	});
        }        
        
        Cursor scanCursor = getContentResolver().query(MediaStore.getMediaScannerUri(),
                null, null, null, null);
        if(scanCursor!=null){
        	 isScanning = true;
             notifyLoadListState();
             scanCursor.close();
        }       
        stepAsyncScanner.startQuery(uri, cols, where.toString(), null, sortOrder);
        
    }
           
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
        mDelayedStopHandler.removeCallbacksAndMessages(null);

        if (intent != null) {
        	//
        	handlerInsertDevScan(intent);
        	//
            String action = intent.getAction();            
        	String cmd = intent.getStringExtra("command");
            if(cmd!=null){
            	StringBuffer log = new StringBuffer();
                log.append("MediaPlaybackService - onStartCommand - action: ");
                log.append(action);
                log.append(" - cmd: ");
                log.append(cmd);
                printLog(log.toString());

                if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                    gotoNext(true, false);
                } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
                    if (position() < 2000) {
                        prev(true, false);
                    } else {
                        seek(0);
                        play();
                    }
                } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
                    if (isPlaying()) {
                        pause();
                        mPausedByTransientLossOfFocus = false;
                    } else {
                        play();
                    }
                } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
                    pause();
                    mPausedByTransientLossOfFocus = false;
                } else if (CMDPLAY.equals(cmd)) {
                    play();
                } else if (CMDSTOP.equals(cmd)) {
                    pause();
                    mPausedByTransientLossOfFocus = false;
                    seek(0);
                }
            }            
        }

        // make sure the service will shut down on its own if it was
        // just started but not bound to and nothing is playing
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        printLog("MediaPlaybackService - onUnbind");

//        mMediaplayerHandler.removeMessages(MSG_GET_AUDIOFOCUS);
//        mAudioManager.unregisterMediaButtonEventReceiver(new ComponentName(this
//                .getPackageName(), MediaButtonIntentReceiver.class.getName()));

        mServiceInUse = false;

        // Take a snapshot of the current playlist        
        saveQueue(true);

        if (isPlaying() || mPausedByTransientLossOfFocus) {
            // something is currently playing, or will be playing once
            // an in-progress action requesting audio focus ends, so don't stop
            // the service now.
            return true;
        }

        // If there is a playlist but playback is paused, then wait a while
        // before stopping the service, so that pause/resume isn't slow.
        // Also delay stopping the service if we're transitioning between
        // tracks.
        if (mPlayListLen > 0 || mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
            Message msg = mDelayedStopHandler.obtainMessage();
            mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
            return true;
        }

        // No active playlist, OK to stop the service right now
        stopSelf(mServiceStartId);
        return true;
    }

    private Handler mDelayedStopHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Check again to make sure nothing is playing right now
            if (isPlaying() || mPausedByTransientLossOfFocus || mServiceInUse
                    || mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
                return;
            }
            // save the queue again, because it might have changed
            // since the user exited the music app (because of
            // party-shuffle or because the play-position changed)
            printLog("MediaPlaybackService - mDelayedStopHandler");
            saveQueue(true);
            stopSelf(mServiceStartId);
        }
    };

	private String TAG_LIST = "service_list";
	private ArrayList<String> plugedDevices = new ArrayList<String>();
	private boolean isPlugedDevicesEmpty(){
		return (plugedDevices.size() == 0);
	}
	private void clearPlugedDevices(){
		plugedDevices.clear();
	}
	private void savePlugedDevices(String devPath, boolean add){
		
		boolean got = false;
		if(devPath==null || devPath.length()==0)
			return;
		synchronized (plugedDevices) {
			Iterator<String> it = plugedDevices.iterator();
			String path;
			while(it.hasNext()){
				path = it.next();
				if(path.equals(devPath)){
					got = true;
					if(!add){
						it.remove();
					}
					break;
				}
			}
			if(!got && add){
				plugedDevices.add(devPath);
			}
		}
		
		Log.i(TAG, "savePlugedDevices :  "  + add + "  size: " + plugedDevices.size());
	}
	
	private boolean isDevPathExist(String devPath){
		boolean got = false;
		if(devPath==null || devPath.length()==0)
			return false;
		synchronized (plugedDevices) {
			Iterator<String> it = plugedDevices.iterator();
			String path;
			while(it.hasNext()){
				path = it.next();
				if(path.equals(devPath)){
					return true;
				}
			}
		}
		return false;
	}
	
	private void removeInvalidList(){
		Log.i(TAG, "removeInvalidList");
		if(mPlayListLen>0 && mPlayList!=null){
			try{
				Cursor crsr;
				if(mPlayList.length>0){
					long []tmpList = new long[mPlayList.length];
					int n=0, i, pos=0;
					long curid = mPlayList[mPlayPos];
					for(i=0;i<mPlayList.length && i<mPlayListLen;i++){
						crsr = MusicUtils.query(this,
			                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			                    new String[] {
			                    "_id"
			                    }, "_id=" + mPlayList[i], null,
			                    null);
						if(crsr!=null){
							if(curid == mPlayList[i]){
								pos = n;
							}
							tmpList[n++] = mPlayList[i];							
							crsr.close();
						}
					}
					
					if(n != mPlayListLen){
						Log.i(TAG, "some songs are not valid, update list");
						if(n>0){
							long []list = new long[n];
							for(i=0;i<n;i++){
								list[i] = tmpList[i];
							}						
							updateList(list, true);
						}
						else{
							mPlayListLen=0;
							mPlayPos = 0;
							stop();
						}
						notifyChange(META_CHANGED);
					}					
				}			
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	

    /**
     * Called when we receive a ACTION_MEDIA_EJECT notification.
     * 
     * @param storagePath path to mount point for the removed media
     */
    public void closeExternalStorageFiles(String storagePath) {
        printLog("MediaPlaybackService - closeExternalStorageFiles - storagePath: " + storagePath);
        // stop playback and clean up if the SD card is going to be unmounted.
        stop(true);
        notifyChange(QUEUE_CHANGED);
        notifyChange(META_CHANGED);
    }

    /**
     * Registers an intent to listen for ACTION_MEDIA_EJECT notifications. The intent will call
     * closeExternalStorageFiles() if the external media is going to be ejected, so applications can
     * clean up any files they have open.
     */
    public void registerExternalStorageListener() {
        if (mUnmountReceiver == null) {
            mUnmountReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    String storagePath = intent.getData().getPath();
                    String playPath = getRealPlayPath();
                    boolean isPlaying = isPlaying();

                    StringBuffer log = new StringBuffer();
                    log.append("MediaPlaybackService - ExternalStorageListener - action: ");
                    log.append(action);
                    log.append(" - storagePath: ");
                    log.append(storagePath);
                    log.append(" - playPath: ");
                    log.append(playPath);
                    log.append(" - isPlaying: ");
                    log.append(isPlaying);
                    printLog(log.toString());

                    // if (TextUtils.isEmpty(playPath)) {
                    // closeExternalStorageFiles(intent.getData().getPath());
                    // Intent finishactivity = new Intent(FINISHACTIVITY);
                    // sendBroadcast(finishactivity);
                    // return;
                    // }

                    /**
                     * 如果音乐正在播放，则插入新的SD卡或U盘时，不重新去装载数据，因为新数据不会影响正在播放的列表。
                     */
                    //if (action.equals(Intent.ACTION_MEDIA_EJECT)) {      
                    if (action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
                    		|| action.equals(Intent.ACTION_MEDIA_REMOVED)) {      
                    	savePlugedDevices(storagePath, false);                             	
                    	boolean nothing2play = false;
                    	if(getPlayListLen()==0 && isPlugedDevicesEmpty()){
                    		Log.w(TAG, "nothing to play , close app");
                    		cancelReloadPlayList();
                    		nothing2play = true; 
                    	}
                        if (nothing2play || (playPath!=null && playPath.startsWith(storagePath))) {                        	
                        	cancelTryNextTrack();
                            saveQueue(true);
                            mQueueIsSaveable = false;
                            // 通知源管理拔出设备导致关闭APP，源管理会自动切换回之前的被占源
                            SourceManager.hotPlugSource(context, SourceManager.SRC_NO.music,
                                    storagePath, false);
                            //
                            closeExternalStorageFiles(intent.getData().getPath());
                            Intent finishactivity = new Intent(FINISHACTIVITY);
                            sendBroadcast(finishactivity);
                            clearUserData();
                        }
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    	savePlugedDevices(storagePath, true);                    	
//                    	if(!isPlaying()){
//                            //mCardId = MusicUtils.getCardId(MediaPlaybackService.this);
//                            reloadQueue();
//                            mQueueIsSaveable = true;
//                            notifyChange(QUEUE_CHANGED);
//                            notifyChange(META_CHANGED);
//                    	}
                    }
                    else if(action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)){
                    	if(!isPlugedDevicesEmpty()){
                    		reloadPlayList();
                    	}
                    }
                    else if(action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)){
                    	removeInvalidList();
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            //iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
            iFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
            //iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            iFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
            iFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
            iFilter.addDataScheme("file");
            registerReceiver(mUnmountReceiver, iFilter);
        }
    }

    private String getRealPlayPath() {
        synchronized (this) {
            if (mCursor == null || mCursor.getCount() == 0) {
                return null;
            }
            return mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        }
    }

    /**
     * Notify the change-receivers that something has changed. The intent that
     * is sent contains the following data for the currently playing track: "id"
     * - Integer: the database row ID "artist" - String: the name of the artist
     * "album" - String: the name of the album "track" - String: the name of the
     * track The intent has an action that is one of
     * "com.android.music.metachanged" "com.android.music.queuechanged",
     * "com.android.music.playbackcomplete" "com.android.music.playstatechanged"
     * respectively indicating that a new track has started playing, that the
     * playback queue has changed, that playback has stopped because the last
     * file in the list has been played, or that the play-state changed
     * (paused/resumed).
     */
    private void notifyChange(String what) {
        printLog("MediaPlaybackService - notifyChange - what: " + what);

        Intent i = new Intent(what);
        i.putExtra("id", Long.valueOf(getAudioId()));
        i.putExtra("artist", getArtistName());
        i.putExtra("album", getAlbumName());
        i.putExtra("track", getTrackName());
        i.putExtra("playing", isPlaying());
        sendStickyBroadcast(i);

        if (what.equals(PLAYSTATE_CHANGED)) {
            // mRemoteControlClient.setPlaybackState(isPlaying() ?
            // RemoteControlClient.PLAYSTATE_PLAYING :
            // RemoteControlClient.PLAYSTATE_PAUSED);        	
        } else if (what.equals(META_CHANGED)) {
            // RemoteControlClient.MetadataEditor ed =
            // mRemoteControlClient.editMetadata(true);
            // ed.putString(MediaMetadataRetriever.METADATA_KEY_TITLE,
            // getTrackName());
            // ed.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM,
            // getAlbumName());
            // ed.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
            // getArtistName());
            // ed.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION,
            // duration());
            // Bitmap b = MusicUtils.getArtwork(this, getAudioId(),
            // getAlbumId(), false);
            // if (b != null) {
            // ed.putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, b);
            // }
            // ed.apply();
        }

        if (what.equals(QUEUE_CHANGED) || what.equals(META_CHANGED)) {
            saveQueue(true);
        } else {
            saveQueue(false);
        }

        // Share this notification directly with our widgets
        mAppWidgetProvider.notifyChange(this, what);
    }
    
    private void notifyChange(String what, int errorNo) {
    	Intent i = new Intent(what);
        i.putExtra("id", Long.valueOf(getAudioId()));
        i.putExtra("artist", getArtistName());
        i.putExtra("album", getAlbumName());
        i.putExtra("track", getTrackName());
        i.putExtra("playing", isPlaying());
        i.putExtra("errorNo", errorNo);
        sendStickyBroadcast(i);
        
        if (what.equals(QUEUE_CHANGED) || what.equals(META_CHANGED)) {
            saveQueue(true);
        } else {
            saveQueue(false);
        }

        // Share this notification directly with our widgets
        mAppWidgetProvider.notifyChange(this, what);
    }

    private void ensurePlayListCapacity(int size) {
        if (mPlayList == null || size > mPlayList.length) {
            // reallocate at 2x requested size so we don't
            // need to grow and copy the array for every insert

            // StringBuffer logBuffer = new StringBuffer();
            // logBuffer.append("MediaPlaybackService - ensurePlayListCapacity - size: ");
            // logBuffer.append(size);
            // if (mPlayList != null) {
            // logBuffer.append(" - mPlayList.length: ");
            // logBuffer.append(mPlayList.length);
            // }
            // logBuffer.append(" - mPlayListLen: ");
            // logBuffer.append(mPlayListLen);
            // printLog(logBuffer.toString());

            long[] newlist = new long[size * 2];
            int len = mPlayList != null ? mPlayList.length : mPlayListLen;
            for (int i = 0; i < len; i++) {
                newlist[i] = mPlayList[i];
            }
            mPlayList = newlist;
        }
        // shrink the array when the needed size is much smaller
        // than the allocated size
    }

    // insert the list of songs at the specified position in the playlist
    private void addToPlayList(long[] list, int position) {
        int addlen = list.length;

        StringBuffer log = new StringBuffer();
        log.append("1 MediaPlaybackService - addToPlayList - addlen: ");
        log.append(addlen);
        log.append(" - mPlayListLen: ");
        log.append(mPlayListLen);
        log.append(" - position: ");
        log.append(position);
        // if (mPlayList != null) {
        // log.append(" - mPlayList: ");
        // for (int i = 0; i < mPlayList.length; i++) {
        // String str = String.format("%d ", mPlayList[i]);
        // log.append(str);
        // }
        // }
        printLog(log.toString(), true);

        if (position < 0) { // overwrite
            mPlayListLen = 0;
            position = 0;
        }
        ensurePlayListCapacity(mPlayListLen + addlen);

        if (position > mPlayListLen) {
            position = mPlayListLen;
        }

        // move part of list after insertion point
        int tailsize = mPlayListLen - position;
        for (int i = tailsize; i > 0; i--) {
            mPlayList[position + i] = mPlayList[position + i - addlen];
        }

        // copy list into playlist
        for (int i = 0; i < addlen; i++) {
            mPlayList[position + i] = list[i];
        }
        mPlayListLen += addlen;

        StringBuffer log2 = new StringBuffer();
        log2.append("2 MediaPlaybackService - addToPlayList - mPlayListLen: ");
        log2.append(mPlayListLen);
        // if (mPlayList != null) {
        // log2.append(" - mPlayList: ");
        // for (int i = 0; i < mPlayList.length; i++) {
        // String str = String.format("%d ", mPlayList[i]);
        // log2.append(str);
        // }
        // }
        printLog(log2.toString(), true);

        if (mPlayListLen == 0) {
            mCursor.close();
            mCursor = null;
            notifyChange(META_CHANGED);
        }
    }

    /**
     * Appends a list of tracks to the current playlist. If nothing is playing
     * currently, playback will be started at the first track. If the action is
     * NOW, playback will switch to the first of the new tracks immediately.
     * 
     * @param list The list of tracks to append.
     * @param action NOW, NEXT or LAST
     */
    public void enqueue(long[] list, int action) {
        synchronized (this) {
            printLog("MediaPlaybackService - enqueue - action: " + action);

            if (action == NEXT && mPlayPos + 1 < mPlayListLen) {
                addToPlayList(list, mPlayPos + 1);
                notifyChange(QUEUE_CHANGED);
            } else {
                // action == LAST || action == NOW || mPlayPos + 1 ==
                // mPlayListLen
                addToPlayList(list, Integer.MAX_VALUE);
                notifyChange(QUEUE_CHANGED);
                if (action == NOW) {
                    mPlayPos = mPlayListLen - list.length;
                    if(openCurrentPlayPos(true)){
                    	play();
                    	notifyChange(META_CHANGED);
                    }
                    return;
                }
            }
            if (mPlayPos < 0) {
                mPlayPos = 0;
                if(openCurrentPlayPos(true)){
                	play();
                	notifyChange(META_CHANGED);
                }
            }
        }
    }

    /**
     * Replaces the current playlist with a new list, and prepares for starting
     * playback at the specified position in the list, or a random position if
     * the specified position is 0.
     * 
     * @param list The new list of tracks.
     * @param listMode  0: all list, 1: artist, 2: album,  3: folder.
     * @param fieldId id of artist or album, not valid with listMode==0
     */
    public void open(long[] list, int position, int listMode, long fieldId) {
        synchronized (this) {
            if (mShuffleMode == SHUFFLE_AUTO) {
                mShuffleMode = SHUFFLE_NORMAL;
            }
            long oldId = getAudioId();
            int listlength = list.length;
            boolean newlist = true;
            if (mPlayListLen == listlength) {
                // possible fast path: list might be the same
                newlist = false;
                for (int i = 0; i < listlength; i++) {
                    if (list[i] != mPlayList[i]) {
                        newlist = true;
                        break;
                    }
                }
            }

            StringBuffer logBuffer = new StringBuffer();
            logBuffer.append("MediaPlaybackService - open list - oldId: ");
            logBuffer.append(oldId);
            logBuffer.append(" - listlength: ");
            logBuffer.append(listlength);
            logBuffer.append(" - newlist: ");
            logBuffer.append(newlist);
            printLog(logBuffer.toString(), true);
            
            if (newlist) {
            	curListMode = listMode;
            	curFieldId = fieldId;
                addToPlayList(list, -1);
            }
            
         // int oldpos = mPlayPos;
            if (position >= 0) {
                mPlayPos = position;
            } else {
                mPlayPos = 0;
            }

            if(newlist){
            	notifyChange(QUEUE_CHANGED);
            }
            
            mHistory.clear();
            mHistoryTmp.clear();
            saveBookmarkIfNeeded();
            openCurrentPlayPos(true);
            curAudioId = getAudioId();
            if (oldId != curAudioId) {
                notifyChange(META_CHANGED);
            }
        }
    }

    /*
     * udpate play list, but do not interrupt current player state.
     */
    public void updateList(long[] list, boolean playIfneed) {
        synchronized (this) {
        	Log.i(TAG_LIST   , "updateList :" + list.length);
            long oldId = getAudioId();
            int listlength = list.length;
            boolean newlist = true;
            if (mPlayListLen == listlength) {
                // possible fast path: list might be the same
                newlist = false;
                for (int i = 0; i < listlength; i++) {
                    if (list[i] != mPlayList[i]) {
                        newlist = true;
                        break;
                    }
                }
            }
            if (newlist) {
            	int nowIndex = -1;
                addToPlayList(list, -1);
                for (int i = 0; i < mPlayList.length; i++) {
                	if(mPlayList[i] == oldId){
                		nowIndex = i;
                		break;
                	}
                }
                if(nowIndex>=0){
                	Log.i(TAG_LIST  , "update quiet");
                	mPlayPos = nowIndex;
                }
                else{
                	Log.w(TAG_LIST  , "some thing error, open this list.");
                	mPlayPos  = 0;
                	 mHistory.clear();
                	 mHistoryTmp.clear();
                     saveBookmarkIfNeeded();
                     if(playIfneed){
                    	 openCurrentPlayPos(true);
//                         if (oldId != getAudioId()) {
//                             notifyChange(META_CHANGED);
//                         }
                     }                     
                }                
                notifyChange(META_CHANGED);
                Log.i(TAG_LIST, "updateList: mPlayList count:"+mPlayList.length + " mPlayPos:"+mPlayPos);
            }
        }        
    }
    /**
     * Moves the item at index1 to index2.
     * 
     * @param index1
     * @param index2
     */
    public void moveQueueItem(int index1, int index2) {
        synchronized (this) {
            if (index1 >= mPlayListLen) {
                index1 = mPlayListLen - 1;
            }
            if (index2 >= mPlayListLen) {
                index2 = mPlayListLen - 1;
            }
            if (index1 < index2) {
                long tmp = mPlayList[index1];
                for (int i = index1; i < index2; i++) {
                    mPlayList[i] = mPlayList[i + 1];
                }
                mPlayList[index2] = tmp;
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index1 && mPlayPos <= index2) {
                    mPlayPos--;
                }
            } else if (index2 < index1) {
                long tmp = mPlayList[index1];
                for (int i = index1; i > index2; i--) {
                    mPlayList[i] = mPlayList[i - 1];
                }
                mPlayList[index2] = tmp;
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index2 && mPlayPos <= index1) {
                    mPlayPos++;
                }
            }
            notifyChange(QUEUE_CHANGED);
        }
    }

    /**
     * Returns the current play list
     * 
     * @return An array of integers containing the IDs of the tracks in the play
     *         list
     */
    public long[] getQueue() {
        synchronized (this) {
            int len = mPlayListLen;
            long[] list = new long[len];
            for (int i = 0; i < len; i++) {
                list[i] = mPlayList[i];
            }
            return list;
        }
    }

    private Cursor getCursorForId(long lid) {
        String id = String.valueOf(lid);

        Cursor c = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mCursorCols,
                "_id=" + id, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    /*
    private void openCurrentAndPre() {
        synchronized (this) {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }

            if (mPlayListLen == 0) {
                return;
            }
            stop(false);

            mCursor = getCursorForId(mPlayList[mPlayPos]);
            while (true) {
                printLog("1 MediaPlaybackService - openCurrentAndPre - mPlayPos: " + mPlayPos);
                if (mCursor != null && mCursor.getCount() != 0
                        && open(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                                + "/" + mCursor.getLong(IDCOLIDX))) {
                    printLog("MediaPlaybackService - openCurrentAndPre - data: "
                            + mCursor.getString(DATACOLIDX));
                    break;
                }
                Log.e("mymusic", "MediaPlaybackService - openCurrentAndPre - open error");
                // if we get here then opening the file failed. We can close the
                // cursor now, because
                // we're either going to create a new one next, or stop trying
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
                if (mOpenFailedCounter++ < 10 && mPlayListLen > 1) {
                    int pos = getPrePosition(false, true);
                    if (pos < 0) {
                        gotoIdleState();
                        if (mIsSupposedToBePlaying) {
                            printLog("MediaPlaybackService - openCurrentAndNext - OpenFailed - gotoIdleState");
                            mIsSupposedToBePlaying = false;
                            notifyChange(PLAYSTATE_CHANGED);
                        }
                        return;
                    }
                    printLog("2 MediaPlaybackService - openCurrentAndPre - pos: " + pos);
                    mPlayPos = pos;
                    stop(false);
                    mPlayPos = pos;
                    mCursor = getCursorForId(mPlayList[mPlayPos]);
                } else {
                    mOpenFailedCounter = 0;
                    if (!mQuietMode) {
                        Toast.makeText(this, R.string.playback_failed,
                               Toast.LENGTH_SHORT).show();
                    }
                    printLog("Failed to open file for playback");
                    gotoIdleState();
                    if (mIsSupposedToBePlaying) {
                        printLog("MediaPlaybackService - openCurrentAndPre - OpenFailed - PLAYSTATE_CHANGED");
                        //mIsSupposedToBePlaying = false;
                        notifyChange(PLAYSTATE_CHANGED);
                    }
                    return;
                }
            }

            // go to bookmark if needed
            if (isPodcast()) {
                long bookmark = getBookmark();
                // Start playing a little bit before the bookmark,
                // so it's easier to get back in to the narrative.
                seek(bookmark - 5000);
            }
            setNextTrack();
        }
    }
    */
    
    //打开一个文件失败时，将尝试下一个文件，如果连续打开错误回到开始的错误文件，即不再尝试。
    private int mTryingBeginPlayPos = -1;
    private void tryNextTrack(boolean next){
    	mMediaplayerHandler.removeMessages(TRY_NEXT_TRACK);
    	mMediaplayerHandler.sendMessageDelayed(mMediaplayerHandler.obtainMessage(TRY_NEXT_TRACK, next?1:0, 0), 500);
    }
    private void cancelTryNextTrack(){
    	mMediaplayerHandler.removeMessages(TRY_NEXT_TRACK);
    }

    //private boolean openCurrentAndNext() {
    private boolean openCurrentPlayPos(boolean tryNext){
        synchronized (this) {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }

            if (mPlayListLen == 0) {
                return false;
            }
            stop(false);

            mCursor = getCursorForId(mPlayList[mPlayPos]);
            while (true) {
                printLog("MediaPlaybackService - openCurrentAndNext - mPlayPos: " + mPlayPos);
                if (mCursor != null && mCursor.getCount() != 0
                        && open(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                                + "/" + mCursor.getLong(IDCOLIDX))) {
                    printLog("MediaPlaybackService - openCurrentAndNext - data: "
                            + mCursor.getString(DATACOLIDX));
                    break;
                }
                printLog("MediaPlaybackService - openCurrentAndNext - open error");
                // if we get here then opening the file failed. We can close the
                // cursor now, because
                // we're either going to create a new one next, or stop trying
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
                /*
                if (mOpenFailedCounter++ < 10 && mPlayListLen > 1) {
                    int pos = getNextPosition(false, true);
                    if (pos < 0) {
                        gotoIdleState();
                        if (mIsSupposedToBePlaying) {
                            printLog("MediaPlaybackService - openCurrentAndNext - OpenFailed - gotoIdleState");
                            mIsSupposedToBePlaying = false;
                            notifyChange(PLAYSTATE_CHANGED);
                        }
                        return;
                    }
                    mPlayPos = pos;
                    stop(false);
                    mPlayPos = pos;
                    mCursor = getCursorForId(mPlayList[mPlayPos]);
                } else {
                    mOpenFailedCounter = 0;
                    if (!mQuietMode) {
                        Toast.makeText(this, R.string.playback_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                        */
                    printLog("Failed to open file for playback pos: "+ mPlayPos) ;
                    gotoIdleState();                 
                    //if (mIsSupposedToBePlaying) {
                        printLog("MediaPlaybackService - openCurrentAndNext - OpenFailed - PLAYSTATE_CHANGED");
                        //mIsSupposedToBePlaying = false;
                        notifyChange(PLAYSTATE_CHANGED, -1);
                        if(mTryingBeginPlayPos!=mPlayPos){
                        	if(mTryingBeginPlayPos == -1){
                            	mTryingBeginPlayPos = mPlayPos;
                            }
                        	tryNextTrack(tryNext);
                        }                        
                   // }
                    return false;
               // }
            }

            // go to bookmark if needed
            if (isPodcast()) {
                long bookmark = getBookmark();
                // Start playing a little bit before the bookmark,
                // so it's easier to get back in to the narrative.
                seek(bookmark - 5000);
            }
            setNextTrack();
            
            mTryingBeginPlayPos = -1;
            cancelTryNextTrack();
            return true;
        }
    }

    private void setNextTrack() {
        if (mPlayList != null) {
            mNextPlayPos = getNextPosition(false, false);
            if (mNextPlayPos >= 0) {
                // long id = mPlayList[mNextPlayPos];
                // MTK
                // mPlayer.setNextDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                // + "/" + id);
            }
        }
    }

    /**
     * Opens the specified file and readies it for playback.
     * 
     * @param path The full path of the file to be opened.
     */
    public boolean open(String path) {
        synchronized (this) {
            if (mPlayer == null || path == null) {
                return false;
            }

            printLog("MediaPlaybackService - open - path: " + path + " - mCursor: " + mCursor);
            
            // if mCursor is null, try to associate path with a database cursor
            if (mCursor == null) {
                ContentResolver resolver = getContentResolver();
                Uri uri;
                String where;
                String selectionArgs[];
                if (path.startsWith("content://media/")) {
                    uri = Uri.parse(path);
                    where = null;
                    selectionArgs = null;
                } else {
                    uri = MediaStore.Audio.Media.getContentUriForPath(path);
                    where = MediaStore.Audio.Media.DATA + "=?";
                    selectionArgs = new String[] {
                            path
                    };
                }

                try {
                    mCursor = resolver.query(uri, mCursorCols, where, selectionArgs, null);
                    if (mCursor != null) {
                        if (mCursor.getCount() == 0) {
                            mCursor.close();
                            mCursor = null;
                        } else {
                            mCursor.moveToNext();
                            ensurePlayListCapacity(1);
                            mPlayListLen = 1;
                            mPlayList[0] = mCursor.getLong(IDCOLIDX);
                            mPlayPos = 0;
                        }
                    }
                } catch (UnsupportedOperationException ex) {
                }
            }
            mFileToPlay = path;
            mPlayer.setDataSource(mFileToPlay);
            if (mPlayer.isInitialized()) {
                mOpenFailedCounter = 0;
                return true;
            }
            stop(true);
            return false;
        }
    }

    /**
     * Starts playback of a previously opened file.
     */

    public void play() {
        printLog("MediaPlaybackService - play");

        if (mPlayer == null) {
            return;
        }
//        mAudioManager.requestAudioFocus(mAudioFocusListener,
//                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//        mAudioManager.registerMediaButtonEventReceiver(new ComponentName(this
//                .getPackageName(), MediaButtonIntentReceiver.class.getName()));
//        mMediaplayerHandler.sendEmptyMessageDelayed(MSG_GET_AUDIOFOCUS, 2000);

        if (mPlayer.isInitialized()) {
            // if we are at the end of the song, go to the next song first
            long duration = mPlayer.duration();
            if (mRepeatMode != REPEAT_CURRENT && duration > 2000
                    && mPlayer.position() >= duration - 2000) {
                gotoNext(true, false);
            }
            mPlayer.start();
            
            if(resumeAudioId == getAudioId()){
            	resumeAudioId = -1;
                seek((resumeSeekPos >= 0 && resumeSeekPos < duration()) ? resumeSeekPos : 0);
            }     
            //int realState = mPlayer.getCurrentRealState();
            // make sure we fade in, in case a previous fadein was stopped
            // because
            // of another focus loss
            mMediaplayerHandler.removeMessages(FADEDOWN);
            mMediaplayerHandler.sendEmptyMessage(FADEUP);

            updateNotification();
            notifyChange(PLAYSTATE_CHANGED);
        } else if (mPlayListLen <= 0) {
            // This is mostly so that if you press 'play' on a bluetooth headset
            // without every having played anything before, it will still play
            // something.
            setShuffleMode(SHUFFLE_AUTO);
        }
    }

    private void updateNotification() {
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.statusbar);
        views.setImageViewResource(R.id.icon,
                R.drawable.stat_notify_musicplayer);
        if (getAudioId() < 0) {
            // streaming
            views.setTextViewText(R.id.trackname, getPath());
            views.setTextViewText(R.id.artistalbum, null);
        } else {
            String artist = getArtistName();
            views.setTextViewText(R.id.trackname, getTrackName());
            if (artist == null || artist.equals(MediaStore.UNKNOWN_STRING)) {
                artist = getString(R.string.unknown_artist_name);
            }
            String album = getAlbumName();
            if (album == null || album.equals(MediaStore.UNKNOWN_STRING)) {
                album = getString(R.string.unknown_album_name);
            }

            views.setTextViewText(
                    R.id.artistalbum,
                    getString(R.string.notification_artist_album, artist, album));
        }
        Notification status = new Notification();
        status.contentView = views;
        status.flags |= Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.stat_notify_musicplayer;
        status.contentIntent = PendingIntent.getActivity(this, 0, new Intent(
                "com.android.music.PLAYBACK_VIEWER")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
        // startForeground(PLAYBACKSERVICE_STATUS, status);
    }

    private void stop(boolean remove_status_icon) {
        // printLog("1 MediaPlaybackService - stop - remove_status_icon: " +
        // remove_status_icon);
        if (mPlayer == null) {
            return;
        }
       
        if (mPlayer.isInitialized()) {
            mPlayer.stop();
        }
        mFileToPlay = null;
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        if (remove_status_icon) {
            gotoIdleState();
        } else {
            stopForeground(false);
        }

        printLog("1 MediaPlaybackService - stop - state: " + mPlayer.getCurrentRealState());
    }

    /**
     * Stops playback.
     */
    public void stop() {
        //printLog("2 MediaPlaybackService - stop - state: " + mPlayer.getCurrentRealState());
        stop(true);
    }

    /**
     * Pauses playback (call play() to resume)
     */
    public void pause() {
        printLog("MediaPlaybackService - pause");
        synchronized (this) {
            mMediaplayerHandler.removeMessages(FADEUP);
            if (mPlayer != null && isPlaying()) {
                mPlayer.pause();
                gotoIdleState();
                notifyChange(PLAYSTATE_CHANGED);
                saveBookmarkIfNeeded();
            }
        }
    }

    /**
     * Returns whether something is currently playing
     * 
     * @return true if something is playing (or will be playing shortly, in case
     *         we're currently transitioning between tracks), false if not.
     */
    public boolean isPlaying() {
        // printLog("MediaPlaybackService - isPlaying - mIsSupposedToBePlaying: "
        // + mIsSupposedToBePlaying);
    	if(mPlayer!=null)
    		return mPlayer.isPlaying();
    	else
    		return false;
    }
    
    private int getRandPrevPos(){
    	int numTracks = mPlayListLen;
        int[] tracks = new int[numTracks];
        for (int i = 0; i < numTracks; i++) {
            tracks[i] = i;
        }

        int numHistory = mHistoryTmp.size();
        int numUnplayed = numTracks;
        for (int i = 0; i < numHistory; i++) {
            int idx = mHistoryTmp.get(i).intValue();
            if (idx < numTracks && tracks[idx] >= 0) {
                numUnplayed--;
                tracks[idx] = -1;
            }
        }

        // 'numUnplayed' now indicates how many tracks have not yet
        // been played, and 'tracks' contains the indices of those
        // tracks.
        if (numUnplayed <= 0) {
        	// pick from full set
            numUnplayed = numTracks;
            for (int i = 0; i < numTracks; i++) {
                tracks[i] = i;
            }
            mHistoryTmp.clear();
        }
        int skip = mRand.nextInt(numUnplayed);
        int cnt = -1;
        while (true) {
            while (tracks[++cnt] < 0)
                ;
            skip--;
            if (skip < 0) {
                break;
            }
        }
        mHistoryTmp.add(cnt);
        if(mHistoryTmp.size()>mPlayListLen || mHistoryTmp.size()>20){
        	mHistoryTmp.remove(0);
        }
        
        return cnt;
    }

    /*
     * Desired behavior for prev/next/shuffle: - NEXT will move to the next
     * track in the list when not shuffling, and to a track randomly picked from
     * the not-yet-played tracks when shuffling. If all tracks have already been
     * played, pick from the full set, but avoid picking the previously played
     * track if possible. - when shuffling, PREV will go to the previously
     * played track. Hitting PREV again will go to the track played before that,
     * etc. When the start of the history has been reached, PREV is a no-op.
     * When not shuffling, PREV will go to the sequentially previous track (the
     * difference with the shuffle-case is mainly that when not shuffling, the
     * user can back up to tracks that are not in the history). Example: When
     * playing an album with 10 tracks from the start, and enabling shuffle
     * while playing track 5, the remaining tracks (6-10) will be shuffled, e.g.
     * the final play order might be 1-2-3-4-5-8-10-6-9-7. When hitting 'prev' 8
     * times while playing track 7 in this example, the user will go to tracks
     * 9-6-10-8-5-4-3-2. If the user then hits 'next', a random track will be
     * picked again. If at any time user disables shuffling the next/previous
     * track will be picked in sequential order again.
     */

    public void prev(boolean forse, boolean error) {
        synchronized (this) {
            Log.e("mymusic", "MediaPlaybackService - prev - mShuffleMode: " + mShuffleMode
                    + " - mPlayPos: " + mPlayPos);
            if (mShuffleMode == SHUFFLE_NORMAL) {
                // go to previously-played track and remove it from the history
                int histsize = mHistory.size();

                /**
                 * jy 20150211 modify for music
                 */
                // Log.e("mymusic", "prev: mPlayPos = " + mPlayPos +
                // " -  mHistory = " + mHistory);
                if (histsize > 1) {
                    Integer pos = mHistory.get(histsize - 2);
                    mHistory.remove(histsize - 1);
                    mHistory.remove(histsize - 2);
                    mPlayPos = pos.intValue();
                } else {
                	mHistory.clear();                	                	
                    mPlayPos = getRandPrevPos();
                }                
            } else {
                mPlayPos = getPrePosition(forse, error);
                // if (mPlayPos > 0) {
                // mPlayPos--;
                // } else {
                // mPlayPos = mPlayListLen - 1;
                // }
            }

            saveBookmarkIfNeeded();
            stop(false);
            Log.e("mymusic", "MediaPlaybackService - prev - mPlayPos: " + mPlayPos);
            if(openCurrentPlayPos(false)){
	            play();
	            notifyChange(META_CHANGED);
            }
        }
    }

    private int getPrePosition(boolean force, boolean error) {
        printLog("MediaPlaybackService - getPrePosition - mShuffleMode: " +
                mShuffleMode + " - mRepeatMode: " + mRepeatMode);

        if (!force && mRepeatMode == REPEAT_CURRENT) {
            if (mPlayPos < 0)
                return 0;
            return mPlayPos;
        } else if (mShuffleMode == SHUFFLE_NORMAL) {

            // 如果发生错误，则顺序跳过，而不是随机取
            if (error) {
                if (mPlayPos <= 0) {
                    return mPlayListLen - 1;
                } else {
                    mPlayPos--;
                    return mPlayPos;
                }
            }

            // Pick random next track from the not-yet-played ones
            // make it work right after adding/removing items in the
            // queue.

            // Store the current file in the history, but keep the history at a
            // reasonable size
            int histsize = mHistory.size();
            Integer pos = -1;
            if (histsize > 1) {
                pos = mHistory.get(histsize - 1);
            }
            // Log.e("mymusic", "1 MediaPlaybackService - mPlayPos: " + mPlayPos
            // + " -  pos = " + pos + " -  mHistory = " + mHistory);
            if (mPlayPos >= 0 && pos != mPlayPos) {
                mHistory.add(mPlayPos);
            }
            if (mHistory.size() > MAX_HISTORY_SIZE) {
                mHistory.removeElementAt(0);
            }

            int numTracks = mPlayListLen;
            int[] tracks = new int[numTracks];
            for (int i = 0; i < numTracks; i++) {
                tracks[i] = i;
            }

            int numHistory = mHistory.size();
            int numUnplayed = numTracks;
            for (int i = 0; i < numHistory; i++) {
                int idx = mHistory.get(i).intValue();
                if (idx < numTracks && tracks[idx] >= 0) {
                    numUnplayed--;
                    tracks[idx] = -1;
                }
            }

            // 'numUnplayed' now indicates how many tracks have not yet
            // been played, and 'tracks' contains the indices of those
            // tracks.
            if (numUnplayed <= 0) {
                // everything's already been played
            	// pick from full set
                numUnplayed = numTracks;
                for (int i = 0; i < numTracks; i++) {
                    tracks[i] = i;
                }
            }
            int skip = mRand.nextInt(numUnplayed);
            int cnt = -1;
            while (true) {
                while (tracks[++cnt] < 0)
                    ;
                skip--;
                if (skip < 0) {
                    break;
                }
            }
            return cnt;
        } else if (mShuffleMode == SHUFFLE_AUTO) {
            // 如果发生错误，则顺序跳过，而不是随机取
            if (error) {
                if (mPlayPos <= 0) {
                    return mPlayListLen - 1;
                } else {
                    mPlayPos--;
                    return mPlayPos;
                }
            }

            doAutoShuffleUpdate();
            return mPlayPos--;
        } else {
            StringBuffer log = new StringBuffer();
            log.append("MediaPlaybackService - getPrePosition - mPlayPos: ");
            log.append(mPlayPos);
            log.append(" - mPlayListLen: ");
            log.append(mPlayListLen);
            printLog(log.toString());

            if (mPlayPos <= 0) {
                // we're at the end of the list
                // if (mRepeatMode == REPEAT_NONE && !force) {
                // // all done
                // return -1;
                // } else if (mRepeatMode == REPEAT_ALL || force) {
                // return mPlayListLen - 1;
                // }
                return mPlayListLen - 1;
            } else {
                mPlayPos--;
                return mPlayPos;
            }
        }
    }

    /**
     * Get the next position to play. Note that this may actually modify
     * mPlayPos if playback is in SHUFFLE_AUTO mode and the shuffle list window
     * needed to be adjusted. Either way, the return value is the next value
     * that should be assigned to mPlayPos;
     */
    private int getNextPosition(boolean force, boolean error) {
        if (!force && mRepeatMode == REPEAT_CURRENT) {
            if (mPlayPos < 0)
                return 0;
            return mPlayPos;
        } else if (mShuffleMode == SHUFFLE_NORMAL) {

            // 如果发生错误，则顺序跳过，而不是随机取
            if (error) {
                if (mPlayPos >= mPlayListLen - 1) {
                    return 0;
                } else {
                    return mPlayPos + 1;
                }
            }

            // Pick random next track from the not-yet-played ones
            // make it work right after adding/removing items in the
            // queue.

            // Store the current file in the history, but keep the history at a
            // reasonable size
            /**
             * jy 20150211 modify for music
             */
            int histsize = mHistory.size();
            Integer pos = -1;
            if (histsize > 1) {
                pos = mHistory.get(histsize - 1);
            }
            // Log.e("mymusic", "1 MediaPlaybackService - mPlayPos: " + mPlayPos
            // + " -  pos = " + pos + " -  mHistory = " + mHistory);
            if (mPlayPos >= 0 && pos != mPlayPos) {
                mHistory.add(mPlayPos);
            }
            if (mHistory.size() > MAX_HISTORY_SIZE) {
                mHistory.removeElementAt(0);
            }

            int numTracks = mPlayListLen;
            int[] tracks = new int[numTracks];
            for (int i = 0; i < numTracks; i++) {
                tracks[i] = i;
            }

            int numHistory = mHistory.size();
            int numUnplayed = numTracks;
            for (int i = 0; i < numHistory; i++) {
                int idx = mHistory.get(i).intValue();
                if (idx < numTracks && tracks[idx] >= 0) {
                    numUnplayed--;
                    tracks[idx] = -1;
                }
            }

            // 'numUnplayed' now indicates how many tracks have not yet
            // been played, and 'tracks' contains the indices of those
            // tracks.
            if (numUnplayed <= 0) {
                // everything's already been played
                if (mRepeatMode == REPEAT_ALL || force) {
                    // pick from full set
                    numUnplayed = numTracks;
                    for (int i = 0; i < numTracks; i++) {
                        tracks[i] = i;
                    }
                } else {
                    // all done
                    return -1;
                }
            }
            int skip = mRand.nextInt(numUnplayed);
            int cnt = -1;
            while (true) {
                while (tracks[++cnt] < 0)
                    ;
                skip--;
                if (skip < 0) {
                    break;
                }
            }
            return cnt;
        } else if (mShuffleMode == SHUFFLE_AUTO) {
            // 如果发生错误，则顺序跳过，而不是随机取
            if (error) {
                if (mPlayPos >= mPlayListLen - 1) {
                    return 0;
                } else {
                    return mPlayPos + 1;
                }
            }

            doAutoShuffleUpdate();
            return mPlayPos + 1;
        } else {
            if (mPlayPos >= mPlayListLen - 1) {
                // we're at the end of the list
                if (mRepeatMode == REPEAT_NONE && !force) {
                    // all done
                    return -1;
                } else if (mRepeatMode == REPEAT_ALL || force) {
                    return 0;
                }
                return -1;
            } else {
                return mPlayPos + 1;
            }
        }
    }

    public void gotoNext(boolean force, boolean error) {
    	
    	Log.i(TAG, "thread id : "+Thread.currentThread().getId());
    	
        synchronized (this) {
            if (mPlayListLen <= 0) {
                printLog("No play queue");
                return;
            }

            int pos = getNextPosition(force, error);
            if (pos < 0) {
                gotoIdleState();
                notifyChange(PLAYSTATE_CHANGED);
                return;
            }
            mPlayPos = pos;
            saveBookmarkIfNeeded();
            stop(false);
            mPlayPos = pos;
            if(openCurrentPlayPos(true)){
            	play();
            	notifyChange(META_CHANGED);
            }
        }
    }

    private void gotoIdleState() {
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
        stopForeground(true);
    }

    private void saveBookmarkIfNeeded() {
        try {
            if (isPodcast()) {
                long pos = position();
                long bookmark = getBookmark();
                long duration = duration();
                if ((pos < bookmark && (pos + 10000) > bookmark)
                        || (pos > bookmark && (pos - 10000) < bookmark)) {
                    // The existing bookmark is close to the current
                    // position, so don't update it.
                    return;
                }
                if (pos < 15000 || (pos + 10000) > duration) {
                    // if we're near the start or end, clear the bookmark
                    pos = 0;
                }

                // write 'pos' to the bookmark field
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.BOOKMARK, pos);
                Uri uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        mCursor.getLong(IDCOLIDX));
                getContentResolver().update(uri, values, null, null);
            }
        } catch (SQLiteException ex) {
        }
    }

    // Make sure there are at least 5 items after the currently playing item
    // and no more than 10 items before.
    private void doAutoShuffleUpdate() {
        boolean notify = false;

        // remove old entries
        if (mPlayPos > 10) {
            removeTracks(0, mPlayPos - 9);
            notify = true;
        }
        // add new entries if needed
        int to_add = 7 - (mPlayListLen - (mPlayPos < 0 ? -1 : mPlayPos));
        for (int i = 0; i < to_add; i++) {
            // pick something at random from the list

            int lookback = mHistory.size();
            int idx = -1;
            while (true) {
                idx = mRand.nextInt(mAutoShuffleList.length);
                if (!wasRecentlyUsed(idx, lookback)) {
                    break;
                }
                lookback /= 2;
            }
            mHistory.add(idx);
            if (mHistory.size() > MAX_HISTORY_SIZE) {
                mHistory.remove(0);
            }
            ensurePlayListCapacity(mPlayListLen + 1);
            mPlayList[mPlayListLen++] = mAutoShuffleList[idx];
            notify = true;
        }
        if (notify) {
            notifyChange(QUEUE_CHANGED);
        }
    }

    // check that the specified idx is not in the history (but only look at at
    // most lookbacksize entries in the history)
    private boolean wasRecentlyUsed(int idx, int lookbacksize) {

        // early exit to prevent infinite loops in case idx == mPlayPos
        if (lookbacksize == 0) {
            return false;
        }

        int histsize = mHistory.size();
        if (histsize < lookbacksize) {
            printLog("lookback too big");
            lookbacksize = histsize;
        }
        int maxidx = histsize - 1;
        for (int i = 0; i < lookbacksize; i++) {
            long entry = mHistory.get(maxidx - i);
            if (entry == idx) {
                return true;
            }
        }
        return false;
    }

    // A simple variation of Random that makes sure that the
    // value it returns is not equal to the value it returned
    // previously, unless the interval is 1.
    private static class Shuffler {
        private int mPrevious;
        private Random mRandom = new Random();

        public int nextInt(int interval) {
            int ret;
            do {
                ret = mRandom.nextInt(interval);
            } while (ret == mPrevious && interval > 1);
            mPrevious = ret;
            return ret;
        }
    };

    private boolean makeAutoShuffleList() {
        ContentResolver res = getContentResolver();
        Cursor c = null;
        try {
            c = res.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[] {
                        MediaStore.Audio.Media._ID
                    },
                    MediaStore.Audio.Media.IS_MUSIC + "=1", null, null);
            if (c == null || c.getCount() == 0) {
                return false;
            }
            int len = c.getCount();
            long[] list = new long[len];
            for (int i = 0; i < len; i++) {
                c.moveToNext();
                list[i] = c.getLong(0);
            }
            mAutoShuffleList = list;
            return true;
        } catch (RuntimeException ex) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }

    /**
     * Removes the range of tracks specified from the play list. If a file
     * within the range is the file currently being played, playback will move
     * to the next file after the range.
     * 
     * @param first The first file to be removed
     * @param last The last file to be removed
     * @return the number of tracks deleted
     */
    public int removeTracks(int first, int last) {
        int numremoved = removeTracksInternal(first, last);
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }

    private int removeTracksInternal(int first, int last) {
        synchronized (this) {
            if (last < first)
                return 0;
            if (first < 0)
                first = 0;
            if (last >= mPlayListLen)
                last = mPlayListLen - 1;

            boolean gotonext = false;
            if (first <= mPlayPos && mPlayPos <= last) {
                mPlayPos = first;
                gotonext = true;
            } else if (mPlayPos > last) {
                mPlayPos -= (last - first + 1);
            }
            int num = mPlayListLen - last - 1;
            for (int i = 0; i < num; i++) {
                mPlayList[first + i] = mPlayList[last + 1 + i];
            }
            mPlayListLen -= last - first + 1;

            if (gotonext) {
                if (mPlayListLen == 0) {
                    stop(true);
                    mPlayPos = -1;
                    if (mCursor != null) {
                        mCursor.close();
                        mCursor = null;
                    }
                } else {
                    if (mPlayPos >= mPlayListLen) {
                        mPlayPos = 0;
                    }
                    boolean wasPlaying = isPlaying();
                    stop(false);
                    if (wasPlaying && openCurrentPlayPos(true)) {
                        play();
                    }
                }
                notifyChange(META_CHANGED);
            }
            return last - first + 1;
        }
    }

    /**
     * Removes all instances of the track with the given id from the playlist.
     * 
     * @param id The id to be removed
     * @return how many instances of the track were removed
     */
    public int removeTrack(long id) {
        int numremoved = 0;
        synchronized (this) {
            for (int i = 0; i < mPlayListLen; i++) {
                if (mPlayList[i] == id) {
                    numremoved += removeTracksInternal(i, i);
                    i--;
                }
            }
        }
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }

    public void setShuffleMode(int shufflemode) {
        synchronized (this) {
            if (mShuffleMode == shufflemode && mPlayListLen > 0) {
                return;
            }
            mShuffleMode = shufflemode;
            if (mShuffleMode == SHUFFLE_AUTO) {
                if (makeAutoShuffleList()) {
                    mPlayListLen = 0;
                    doAutoShuffleUpdate();
                    mPlayPos = 0;
                    if(openCurrentPlayPos(true)){
                    	play();
                    	notifyChange(META_CHANGED);
                    }
                    return;
                } else {
                    // failed to build a list of files to shuffle
                    mShuffleMode = SHUFFLE_NONE;
                }
            }
            saveQueue(false);
        }
    }

    public int getShuffleMode() {
        return mShuffleMode;
    }

    public void setRepeatMode(int repeatmode) {
        synchronized (this) {
            mRepeatMode = repeatmode;
            setNextTrack();
            saveQueue(false);
        }
    }

    public int getRepeatMode() {
        return mRepeatMode;
    }

    public int getMediaMountedCount() {
        return plugedDevices.size();
    }

    /**
     * Returns the path of the currently playing file, or null if no file is
     * currently playing.
     */
    public String getPath() {
        return mFileToPlay;
    }

    /**
     * Returns the rowid of the currently playing file, or -1 if no file is
     * currently playing.
     */
    public long getAudioId() {
        synchronized (this) {
            if (mPlayer != null && mPlayPos >= 0 && mPlayer.isInitialized()) {
                return mPlayList[mPlayPos];
            }
        }
        return -1;
    }

    /**
     * Returns the position in the queue
     * 
     * @return the position in the queue
     */
    public int getQueuePosition() {
        synchronized (this) {
            return mPlayPos;
        }
    }

    /**
     * Starts playing the track at the given position in the queue.
     * 
     * @param pos The position in the queue of the track that will be played.
     */
    public void setQueuePosition(int pos) {
        synchronized (this) {
            stop(false);
            mPlayPos = pos;
            if(openCurrentPlayPos(true)){
            	play();
            	notifyChange(META_CHANGED);
            }
            if (mShuffleMode == SHUFFLE_AUTO) {
                doAutoShuffleUpdate();
            }
        }
    }

    public String getArtistName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        }
    }

    public long getArtistId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
        }
    }

    public String getAlbumName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
        }
    }

    public long getAlbumId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        }
    }

    public String getTrackName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
        }
    }
    private String  getTrackPath() {
    	synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        }
    }
    public String getYear() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            } else if (mCursor.getCount() == 0) {
                return null;
            } else {
                return mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
            }
        }
    }

    private boolean isPodcast() {
        synchronized (this) {
            if (mCursor == null) {
                return false;
            }
            int index = mCursor.getColumnIndex(MediaStore.Audio.Media.IS_PODCAST);
            if (index == -1) {
                return false;
            }

            return (mCursor.getInt(PODCASTCOLIDX) > 0);
        }
    }

    private long getBookmark() {
        synchronized (this) {
            if (mCursor == null) {
                return 0;
            }
            return mCursor.getLong(BOOKMARKCOLIDX);
        }
    }

    /**
     * Returns the duration of the file in milliseconds. Currently this method
     * returns -1 for the duration of MIDI files.
     */
    public long duration() {
        if (mPlayer != null && mPlayer.isInitialized()) {
            return mPlayer.duration();
        }
        return -1;
    }

    /**
     * Returns the current playback position in milliseconds
     */
    public long position() {
        if (mPlayer != null && mPlayer.isInitialized()) {
            return mPlayer.position();
        }
        return -1;
    }

    /**
     * Seeks to the position specified.
     * 
     * @param pos The position to seek to, in milliseconds
     */
    public long seek(long pos) {
        printLog("seek(long pos) -------------enter");
        if (mPlayer != null && mPlayer.isInitialized()) {
            if (pos < 0)
                pos = 0;
            if (pos > mPlayer.duration())
                pos = mPlayer.duration();
            printLog("seek(long pos) -------------leave");
            return mPlayer.seek(pos);
        }
        printLog("seek(long pos) -------------leave");
        return -1;
    }

    /**
     * Sets the audio session ID.
     * 
     * @param sessionId : the audio session ID.
     */
    public void setAudioSessionId(int sessionId) {
        synchronized (this) {
            if (mPlayer != null) {
                mPlayer.setAudioSessionId(sessionId);
            }
        }
    }

    /**
     * Returns the audio session ID.
     */
    public int getAudioSessionId() {
        synchronized (this) {
            if (mPlayer != null) {
                return mPlayer.getAudioSessionId();
            }
            return -1;
        }
    }

    public void rePlay() {
        synchronized (this) {
            printLog("MediaPlaybackService - rePlay");
            openCurrent();
            play();
        }
    }

    public void rePlayFromSpecifyPosition(long position) {
        synchronized (this) {
            printLog("MediaPlaybackService - rePlayFromSpecifyPosition");
            openCurrent();
            seek(position);
        }
    }

    private void openCurrent() {
        synchronized (this) {
            printLog("MediaPlaybackService - openCurrent - start");
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }

            if (mPlayListLen == 0) {
                return;
            }
            stop(false);

            String id = String.valueOf(mPlayList[mPlayPos]);
            printLog("MediaPlaybackService - openCurrent - id: " + id);

            mCursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mCursorCols,
                    "_id=" + id, null, null);
            /**
             * jy 20150328 modify for music
             */
            if (mCursor != null && mCursor.getCount() > 0) {
                // printLog("MediaPlaybackService - openCurrent - data: "
                // + mCursor.getString(DATACOLIDX));
                mCursor.moveToFirst();
                /* Begin : parse for not support FD play */
                int index = mCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                String path = null;
                if (index != -1 && mCursor.getCount() != 0) {
                    path = mCursor.getString(index);
                }
                printLog("openCurrent - path: " + path);
                // open(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id);
                if (null != path) {
                    open(path);
                }
                /* End : parse for not support FD play */
                // go to bookmark if needed
                if (isPodcast()) {
                    long bookmark = getBookmark();
                    // Start playing a little bit before the bookmark,
                    // so it's easier to get back in to the narrative.
                    seek(bookmark - 5000);
                }
            }
            printLog("MediaPlaybackService - openCurrent - end");
        }
    }

    /**
     * Provides a unified interface for dealing with midi files and other media
     * files.
     */
    private class MultiPlayer {
        private CompatMediaPlayer mCurrentMediaPlayer = new CompatMediaPlayer();
        private CompatMediaPlayer mNextMediaPlayer;
        private AtcMediaPlayer mAtcMediaPlayer;
        private Handler mHandler;
        private boolean mIsInitialized = false;

        // public static final int KEY_PARAMETER_PLAYBACK_RATE_PERMILLE = 1300;
        // public static final int KEY_PARAMETER_MEDIA_CAPABILITY = 1500;
        // public static final int KEY_PARAMETER_MAXRATE_SUPPORTED = 1600;

        private int mPlaybackRate = 1;

        public MultiPlayer() {
            printLog("MediaPlaybackService - MultiPlayer - start");
            mAtcMediaPlayer = new AtcMediaPlayer(mCurrentMediaPlayer);
            mCurrentMediaPlayer.setWakeMode(MediaPlaybackService.this,
                    PowerManager.PARTIAL_WAKE_LOCK);
        }

        private boolean pausedByCmb = false;
        MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                printLog("MediaPlaybackService - MultiPlayer - mInfoListener - what: " + what);
                switch (what) {
                    case MEDIA_INFO_CBM_STOP:
                    case MEDIA_INFO_CBM_PAUSE:
                    	 if (isPlaying()) {
                             printLog("MediaPlaybackService - onInfo - pause");
                             pause();
                             pausedByCmb = true;
                             notifyChange(PLAYSTATE_CHANGED);
                         } 
                        break;
                    case MEDIA_INFO_CBM_START:
                    case MEDIA_INFO_CBM_RESUME:
                    	if(pausedByCmb){
                    		pausedByCmb = false;
	                        if (!isPlaying()
	                                && LAST_PLAYBACK_STATUS == AtcMediaPlayer.MEDIA_PLAYER_STARTED) {
	                            if (duration() == -1) {
	                                printLog("MediaPlaybackService - onInfo - rePlay");
	                                rePlay();
	                            } else {
	                                printLog("MediaPlaybackService - onInfo - play");
	                                play();
	                            }
	                            notifyChange(PLAYSTATE_CHANGED);
	                        }
                        }
                        break;
                    default:
                        break;
                }
                //cbmNotifyChange(what, extra);
                return false;
            }
        };

        public int getCurrentRealState() {
            if (mAtcMediaPlayer != null) {
                int realState = mAtcMediaPlayer.getRealCurrentState();
                printLog("MediaPlaybackService - MultiPlayer - getCurrentRealState - realState: "
                        + realState);

                switch (realState) {
                    case AtcMediaPlayer.MEDIA_PLAYER_INITIALIZED:
                    case AtcMediaPlayer.MEDIA_PLAYER_PREPARED:
                    case AtcMediaPlayer.MEDIA_PLAYER_PREPARING:
                    case AtcMediaPlayer.MEDIA_PLAYER_STARTED:
                    case AtcMediaPlayer.MEDIA_PLAYER_PLAYBACK_COMPLETE:
                        return AtcMediaPlayer.MEDIA_PLAYER_STARTED;
                    case AtcMediaPlayer.MEDIA_PLAYER_PAUSED:
                        return AtcMediaPlayer.MEDIA_PLAYER_PAUSED;
                    default:
                        return mAtcMediaPlayer.getRealCurrentState();
                }

            }
            return -1;
        }
        
        private Toast toast = null;
        private void showToast(String content, int len){
        	if(toast!=null){
        		toast.cancel();
        		toast = null;
        	}
        	if(mServiceInUse){
	        	toast = Toast.makeText(MediaPlaybackService.this,
	        			content,
	        			len);
	        	toast.show();
        	}
        }
        private void showToast(int strId, int len){
        	showToast(MediaPlaybackService.this.getResources().getString(strId), len);
        }
        private void cancelToast(){
        	if(toast!=null){
        		toast.cancel();
        		toast = null;
        	}
        }
        private void cbmNotifyChange(int commond, int pos) {
            Intent i = new Intent(ACTION_CBM_CHANGE);
            i.putExtra("commond", commond);
            i.putExtra("position", pos);
            sendStickyBroadcast(i);
        }

        public void setDataSource(String path) {
            printLog("MediaPlaybackService - MultiPlayer - setDataSource - path: "
                    + path);
            mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
            // printLog("MediaPlaybackService - MultiPlayer - setDataSource - mIsInitialized: "
            // + mIsInitialized);
            if (mIsInitialized) {
                setNextDataSource(null);
            }
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @SuppressLint("InlinedApi")
        private boolean setDataSourceImpl(MediaPlayer player, String path) {
            try {
                player.reset();
                player.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                    player.setDataSource(MediaPlaybackService.this,
                            Uri.parse(path));
                } else {
                    player.setDataSource(path);
                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepare();
            } catch (IOException ex) {
            	//ex.printStackTrace();
            	String str = MediaPlaybackService.this.getString(R.string.open);
            	str += "\"" + mCursor.getString(DATACOLIDX) + "\"";
            	str += MediaPlaybackService.this.getString(R.string.error);
            	showToast(str,Toast.LENGTH_SHORT);
                return false;
            } catch (IllegalArgumentException ex) {
                return false;
            }
            mPlaybackRate = 1;
            player.setOnCompletionListener(listener);
            player.setOnErrorListener(errorListener);
            player.setOnInfoListener(mInfoListener);
            Intent i = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
            i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
            i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
            sendBroadcast(i);
            return true;
        }

        public void setNextDataSource(String path) {
            mCurrentMediaPlayer.setNextMediaPlayer(null);
            if (mNextMediaPlayer != null) {
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
            if (path == null) {
                return;
            }
            printLog("MediaPlaybackService - MultiPlayer - setNextDataSource - path: "
                    + path);
            mNextMediaPlayer = new CompatMediaPlayer();
            mNextMediaPlayer.setWakeMode(MediaPlaybackService.this,
                    PowerManager.PARTIAL_WAKE_LOCK);
            mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
            if (setDataSourceImpl(mNextMediaPlayer, path)) {
                mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
            } else {
                // failed to open next, we'll transition the old fashioned way,
                // which will skip over the faulty file
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
        }

        public boolean isInitialized() {
            // printLog("MediaPlaybackService - MultiPlayer - isInitialized - mIsInitialized: "
            // + mIsInitialized);
            return mIsInitialized;
        }

        public void start() {
            // printLog("MediaPlaybackService - MultiPlayer - start called");
            mCurrentMediaPlayer.start();
        }

        public void stop() {
            // printLog("MediaPlaybackService - MultiPlayer - stop called");
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
        }
        
        public boolean isPlaying(){
        	return mCurrentMediaPlayer.isPlaying();
        }

        /**
         * You CANNOT use this player anymore after calling release()
         */
        public void release() {
            printLog("MediaPlaybackService - MultiPlayer - release called");
            stop();
            mCurrentMediaPlayer.release();
        }

        public void pause() {
            printLog("MediaPlaybackService - MultiPlayer - pause called");
            mCurrentMediaPlayer.pause();
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mOnErrorCounter = 0;
                if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = mNextMediaPlayer;
                    mNextMediaPlayer = null;
                    mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
                    printLog("MediaPlaybackService - MultiPlayer - listener - TRACK_WENT_TO_NEXT");
                } else {
                    // Acquire a temporary wakelock, since when we return from
                    // this callback the MediaPlayer will release its wakelock
                    // and allow the device to go to sleep.
                    // This temporary wakelock is released when the
                    // RELEASE_WAKELOCK
                    // message is processed, but just in case, put a timeout on
                    // it.
                    mWakeLock.acquire(30000);
                    mHandler.sendEmptyMessage(TRACK_ENDED);
                    mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
                    printLog("MediaPlaybackService - MultiPlayer - listener - TRACK_ENDED");
                }
            }
        };

        MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                printLog("MediaPlaybackService - MultiPlayer - errorListener - what: " + what);

                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        mIsInitialized = false;
                        mCurrentMediaPlayer.release();
                        // Creating a new MediaPlayer and settings its wakemode
                        // does not require the media service, so it's OK to do
                        // this now, while the service is still being restarted
                        mCurrentMediaPlayer = new CompatMediaPlayer();
                        mCurrentMediaPlayer.setWakeMode(MediaPlaybackService.this,
                                PowerManager.PARTIAL_WAKE_LOCK);
                        mHandler.sendMessageDelayed(
                        mHandler.obtainMessage(SERVER_DIED), 2000);
                        return true;

                    default:
                        mIsInitialized = false;
                        if (mRepeatMode == REPEAT_CURRENT) {
                            stop();
                            showToast(R.string.service_start_error_msg,Toast.LENGTH_SHORT);
                            return true;
                        } else if (mOnErrorCounter++ < 10 && mPlayListLen > 1) {
                            stop();
                        } else {
                            Intent forcestopactivity = new Intent(FORCESTOPACTIVITY);
                            sendBroadcast(forcestopactivity);
                            return true;
                        }
                        break;
                }
                return false;
            }
        };

        public long duration() {
            return mCurrentMediaPlayer.getDuration();
        }

        public long position() {
            return mCurrentMediaPlayer.getCurrentPosition();
        }

        public long seek(long whereto) {
            mCurrentMediaPlayer.seekTo((int) whereto);
            return whereto;
        }

        public void setVolume(float vol) {
            mCurrentMediaPlayer.setVolume(vol, vol);
        }

        public void setAudioSessionId(int sessionId) {
            mCurrentMediaPlayer.setAudioSessionId(sessionId);
        }

        public int getAudioSessionId() {
            return mCurrentMediaPlayer.getAudioSessionId();
        }

        public int getPlaySpeed() {
            return (mPlaybackRate);
        }

        public int setPlaySpeed(int speed) {
            mAtcMediaPlayer.setMediaPlayer(mCurrentMediaPlayer);
            boolean ret = mAtcMediaPlayer.setPlaybackRateAsync(speed);
            if (ret != true) {
                return -1; // fail
            } else {
                mPlaybackRate = speed;
                return 0; // success
            }
        }

        public void openRearAudio() {
            printLog("MediaPlaybackService - MultiPlayer - openRearAudio");
            mAtcMediaPlayer.openAudioOutput(AtcMediaPlayer.MEDIA_DEST_REAR);
        }

        public void closeRearAudio() {
            printLog("MediaPlaybackService - MultiPlayer - closeRearAudio");
            mAtcMediaPlayer.closeAudioOutput(AtcMediaPlayer.MEDIA_DEST_REAR);
        }

        public int[] getSupportedMaxRate() {
            int[] rt = new int[2];// ffrate/fbrate
            rt[0] = rt[1] = 1;
            if (mCurrentMediaPlayer != null) {
                try {
                    mAtcMediaPlayer.setMediaPlayer(mCurrentMediaPlayer);
                    int ffrate = mAtcMediaPlayer.getFFMaxRate();
                    int fbrate = mAtcMediaPlayer.getRWMaxRate();
                    rt[0] = ffrate;
                    rt[1] = Math.abs(fbrate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (rt[0] < 1) {
                    rt[0] = 1;
                }
                if (rt[1] < 1) {
                    rt[1] = 1;
                }
                return rt;
            }
            return rt;
        }

        public int getCapability() {
            if (mCurrentMediaPlayer != null) {
                mAtcMediaPlayer.setMediaPlayer(mCurrentMediaPlayer);
                return mAtcMediaPlayer.getMediaCapability();
            }
            return 0;
        }
    }

    static class CompatMediaPlayer extends MediaPlayer implements
            OnCompletionListener {

        private boolean mCompatMode = true;
        private MediaPlayer mNextPlayer;
        private OnCompletionListener mCompletion;

        public CompatMediaPlayer() {
            try {
                MediaPlayer.class.getMethod("setNextMediaPlayer",
                        MediaPlayer.class);
                mCompatMode = false;
                printLog("MediaPlaybackService - CompatMediaPlayer - mCompatMode: " + mCompatMode);
            } catch (NoSuchMethodException e) {
                mCompatMode = true;
                printLog("MediaPlaybackService - CompatMediaPlayer - mCompatMode: " + mCompatMode);
                super.setOnCompletionListener(this);
            }
        }

        public void setNextMediaPlayer(MediaPlayer next) {
            printLog("MediaPlaybackService - setNextMediaPlayer - mCompatMode: " + mCompatMode);
            if (mCompatMode) {
                mNextPlayer = next;
            } else {
                super.setNextMediaPlayer(next);
            }
        }

        @Override
        public void setOnCompletionListener(OnCompletionListener listener) {
            if (mCompatMode) {
                mCompletion = listener;
            } else {
                super.setOnCompletionListener(listener);
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mNextPlayer != null) {
                // as it turns out, starting a new MediaPlayer on the completion
                // of a previous player ends up slightly overlapping the two
                // playbacks, so slightly delaying the start of the next player
                // gives a better user experience
                printLog("MediaPlaybackService - onCompletion");
                SystemClock.sleep(50);
                mNextPlayer.start();
            }
            mCompletion.onCompletion(this);
        }
    }

    /*
     * By making this a static class with a WeakReference to the Service, we
     * ensure that the Service can be GCd even when the system process still has
     * a remote reference to the stub.
     */
    static class ServiceStub extends IMediaPlaybackService.Stub {
        private static final boolean DEBUG = true;
        WeakReference<MediaPlaybackService> mService;

        ServiceStub(MediaPlaybackService service) {
            mService = new WeakReference<MediaPlaybackService>(service);
        }

        public void openFile(String path) {
            mService.get().open(path);
        }

        public void open(long[] list, int position, int listMode, long fieldId) {
            mService.get().open(list, position, listMode, fieldId);
        }

        public int getQueuePosition() {
            return mService.get().getQueuePosition();
        }

        public void setQueuePosition(int index) {
            mService.get().setQueuePosition(index);
        }

        public boolean isPlaying() {
            return mService.get().isPlaying();
        }

        public void stop() {
        	mService.get().cancelTryNextTrack();
            mService.get().stop();
        }

        public void pause() {
        	mService.get().cancelTryNextTrack();
            mService.get().pause();
        }

        public void play() {
            mService.get().play();
        }

        public void prev() {
        	mService.get().cancelTryNextTrack();
            mService.get().prev(true, false);
        }

        public void next() {
        	mService.get().cancelTryNextTrack();
            mService.get().gotoNext(true, false);
        }

        public String getTrackName() {
            return mService.get().getTrackName();
        }
        public String getTrackPath(){
        	return mService.get().getTrackPath();
        }
        public String getAlbumName() {
            return mService.get().getAlbumName();
        }

        public long getAlbumId() {
            return mService.get().getAlbumId();
        }

        public String getArtistName() {
            return mService.get().getArtistName();
        }

        public String getYear() {
            return mService.get().getYear();
        }

        public long getArtistId() {
            return mService.get().getArtistId();
        }

        public void enqueue(long[] list, int action) {
            mService.get().enqueue(list, action);
        }

        public long[] getQueue() {
            return mService.get().getQueue();
        }

        public void moveQueueItem(int from, int to) {
            mService.get().moveQueueItem(from, to);
        }

        public String getPath() {
            return mService.get().getPath();
        }

        public long getAudioId() {
            return mService.get().getAudioId();
        }

        public long position() {
            return mService.get().position();
        }

        public long duration() {
            return mService.get().duration();
        }

        public long seek(long pos) {
            return mService.get().seek(pos);
        }

        public void setShuffleMode(int shufflemode) {
            if (DEBUG) {
                printLog("setShuffleMode(int shufflemode) - shufflemode: " + shufflemode);
            }
            mService.get().setShuffleMode(shufflemode);
        }

        public int getShuffleMode() {
            return mService.get().getShuffleMode();
        }

        public int removeTracks(int first, int last) {
            return mService.get().removeTracks(first, last);
        }

        public int removeTrack(long id) {
            return mService.get().removeTrack(id);
        }

        public void setRepeatMode(int repeatmode) {
            if (DEBUG) {
                printLog("setRepeatMode(int repeatmode) - repeatmode: " + repeatmode);
            }
            mService.get().setRepeatMode(repeatmode);
        }

        public int getRepeatMode() {
            return mService.get().getRepeatMode();
        }

        public int getMediaMountedCount() {
            return mService.get().getMediaMountedCount();
        }

        public int getAudioSessionId() {
            return mService.get().getAudioSessionId();
        }

        public MusicFileInfo[] getPlayList() {
            return mService.get().getPlayList();
        }

        public int getPlayListLen() {
            return mService.get().getPlayListLen();
        }

        public void rePlay() {
            mService.get().rePlay();
        }

        public void rePlayFromSpecifyPosition(long position) {
            mService.get().rePlayFromSpecifyPosition(position);
        }

        public int getPlaySpeed() {
            return mService.get().getPlaySpeed();
        }

        public int setPlaySpeed(int speed) {
            return mService.get().setPlaySpeed(speed);
        }

        public void openRearAudio() {
            mService.get().openRearAudio();
        }

        public void closeRearAudio() {
            mService.get().closeRearAudio();
        }

        public int[] getSupportedMaxRate() {
            return mService.get().getSupportedMaxRate();
        }

        public int getCapability() {
            return mService.get().getCapability();
        }

        public int getCurrentRealState() {
            return mService.get().getCurrentRealState();
        }

		@Override
		public void reloadPlayList() throws RemoteException {
			// TODO Auto-generated method stub
			mService.get().reloadPlayList();
		}

		@Override
		public boolean isScanning() throws RemoteException {
			// TODO Auto-generated method stub
			return mService.get().isScanning();
		}

		@Override
		public void userAction(int action) throws RemoteException {
			// TODO Auto-generated method stub
			mService.get().userAction(action);
		}

    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        if (mPlayer == null) {
            return;
        }
        writer.println("" + mPlayListLen
                + " items in queue, currently at index " + mPlayPos);
        writer.println("Currently loaded:");
        writer.println(getArtistName());
        writer.println(getAlbumName());
        writer.println(getTrackName());
        writer.println(getPath());
        writer.println("playing: " + isPlaying());
        writer.println("actual: " + mPlayer.mCurrentMediaPlayer.isPlaying());
        writer.println("shuffle mode: " + mShuffleMode);
        MusicUtils.debugDump(writer);
    }

	public void userAction(int action) {
		// TODO Auto-generated method stub
		if(action == UserActionState.STARTED.ordinal()){			
			userActionState = UserActionState.STARTED;
			loadPlayListMemory();
		}
		else if(action == UserActionState.STOPED.ordinal()){
			savePlayListMemory(true);
			userActionState = UserActionState.STOPED;
			cancelTryNextTrack();
		}
	}

	public boolean isScanning() {
		// TODO Auto-generated method stub
		return isScanning;
	}

	private MusicFileInfo getFileInfoForId(long id) {
        String[] cols = new String[] {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.FOLDER_NAME,
                MediaStore.Files.FileColumns.PARENT,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Audio.Media.ARTIST,
        };

        String sortOrder = MediaStore.Audio.Media.TITLE_KEY;

        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Files.FileColumns.TITLE);
        where.append(" != '' AND ");
        where.append(MediaStore.Files.FileColumns._ID);
        where.append("=");
        where.append(id);
        where.append(" AND ");
        where.append(MediaStore.Audio.Media.IS_MUSIC);
        where.append("=1");

        Uri uri = MediaStore.Files.getContentUri("external");

        Cursor cursor = getContentResolver().query(uri, cols, where.toString(), null, sortOrder);
        if (cursor == null) {
            return null;
        }

        int folderIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.FOLDER_NAME);
        int parentIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.PARENT);
        int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
        int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

        String folder = cursor.getString(folderIdx);

        MusicFileInfo fileInfo = new MusicFileInfo();
        fileInfo.setId(id);
        fileInfo.setParent(cursor.getInt(parentIdx));
        fileInfo.setDirName(folder);
        fileInfo.setDirPath(folder);
        fileInfo.setFilename(cursor.getString(titleIdx));
        fileInfo.setArtistName(cursor.getString(artistIdx));
        if (id == getAudioId()) {
            fileInfo.setIsPlaying(true);
        } else {
            fileInfo.setIsPlaying(false);
        }

        cursor.close();

        return fileInfo;
    }

    public MusicFileInfo[] getPlayList() {
        printLog("getPlayList - start", false);
        return null;
    }

    public int getPlayListLen() {
        return mPlayListLen;
    }

    public int setPlaySpeed(int speed) {
        return mPlayer.setPlaySpeed(speed);
    }

    public int getPlaySpeed() {
        return mPlayer.getPlaySpeed();
    }

    public void openRearAudio() {
        mPlayer.openRearAudio();
    }

    public void closeRearAudio() {
        mPlayer.closeRearAudio();
    }

    public int[] getSupportedMaxRate() {
        return mPlayer.getSupportedMaxRate();
    }

    public int getCapability() {
        return mPlayer.getCapability();
    }

    private final IBinder mBinder = new ServiceStub(this);
}
