/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

package com.yecon.videoplayer2;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.Presentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.media.MediaFile;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.Parcel;
import com.autochips.media.AtcMediaPlayer;
import com.autochips.storage.EnvironmentATC;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.widget.RelativeLayout;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;

import com.autochips.cbmctx.CBMCtx;
import com.yecon.common.SourceManager;
import com.yecon.settings.YeconSettings;
import com.yecon.videoplayer2.FileListManager.FileInfo;
import com.yecon.videoplayer2.FileScanner.FileType;
import com.yecon.videoplayer2.ForeBackGroundHandler.ForeBackGroundLisenter;
import com.yecon.videoplayer2.FrontVideoView.ISizeChangedLinstener;
import com.yecon.videoplayer2.PlayListData.ListItem;

import static android.mcu.McuExternalConstant.*;
import static com.yecon.videoplayer2.DebugUtil.*;
import static com.yecon.videoplayer2.VideoPlaybackConstant.*;
import static android.constant.YeconConstants.*;

public class VideoPlaybackMainActivity extends Activity implements OnClickListener {

    public static final String INTERNAL_PREFIX = "/mnt/sdcard";
    public static final String EXT_SDCARD_PREFIX = "/mnt/ext_sdcard";
    public static final String UDISK_PREFIX = "/mnt/udisk";
    public static final String ACTION_AVIN_REQUEST_NOTIFY = "yecon.intent.action.AVIN.REQUEST";

    private VideoPlayerApp videoPlayerApp;
    // private static int mPosition;

    //public static boolean mVideoPlaybackOnCreate = false;

    private RearDispPresentation reardisppresentation = null;

    static private CBMCtx gCbmCtx = null;

    private AudioManager mAudioManager;

    private static int mLoopModel = REPEAT_ALL;
    private static int mShuffModel = 0;

    private static String mFFString = "";
    private static String mRWString = "";

    //private String mOldPlaybackPath = null;
    private String mVideoPlaying = null;

    private boolean mCvbsEnable;

    private int unSupportType = -1;

    private Toast rearPlayToast;
    private String backHomeString = "";
    private int timetohome = 3;
    private EnvironmentATC envATC;
    // public LinkedList<MovieInfo> mPlayList = new LinkedList<MovieInfo>();
	private ForeBackGroundHandler foreBackGroundHandler;
	private boolean isBackGround = false;
	
    public class SubtitleInfo {
        int index;
        int count;
    }

    public class AudioChannelInfo {
        int index;
        int count;
    };

    public class SongFfInfo {
        int speedType;
        int speedTypeBkp;
        int mMaxFFRate;
        int mMaxRWRate;
    };

    public enum MediaType {
        MEDIA_TYPE_NONE, MEDIA_TYPE_SDCARD, MEDIA_TYPE_USB, MEDIA_TYPE_FLASH,
    };

    public enum PlayStatus {
        STATUS_NONE, STATUS_STOP, STATUS_PAUSE, STATUS_PLAY, STATUS_FF, STATUS_RW
    };

    public enum FrontRearDisplayStatus {
        NONE_DISP, FRONT_DISP, REAR_DISP, FRONT_REAR_DISP,
    };

    private boolean mIsOnline = false;
    private boolean mIsDivx = false;
    private boolean mIsSupportDivxHT311 = false;
    private int mPrevBtnClickCnt = 0;
    private boolean mIsDivxDrm = false;
    private int mDivxStopClickTimes = 0;
    public Random randomIndex = new Random();

    private AtcMediaPlayer.DivxDrmInfo mDivxDrmInfo = null;
    private SongFfInfo mSongFfInfo;
    private boolean mCanSeek = false;
    private boolean mCanFF = false;
    private boolean mCanRW = false;
    private boolean mCanFFEx = false;
    private boolean mCanRWEx = false;

    private int mMediaCapability = 0;
    private int mTempPosition = 0;
    private int tempPlayedTime = 0;
    private int mPlayedTime = -1;
    //private int mPlayedTimeBak = 0;
    private int mPlayProgressDisp = 0;
    private int mPlayProgressSecondDisp = 0;
    private int clickcount = 0;

    // private View mCurView = null;
    private FrontVideoView mVideoView = null;
    private LinearLayout mParkingLayout = null;
    private SeekBar mSeekBar = null;
    private TextView mTVProcessTime = null;
    private TextView mTVTotalTime = null;
    private TextView mTVTrackIndex = null;
    private TextView mTVSongPlayState;

    private TextView mBtnOpPre;
    private TextView mBtnOpPlay;
    private TextView mBtnOpNext;
    private TextView mBtnOpRand;
    private TextView mBtnOpLoop;
    private TextView mBtnOpList;

    private TextView mAuthorizationErrorText;
    private TextView mRentalExpiredText1;
    private TextView mRentalExpiredText2;
    private TextView mRentalConfirmationChoiceText1;
    private TextView mRentalConfirmationChoiceText2;

    private Button mAuthorizationErrorOKButton;
    private Button mRentalExpiredOKButton;
    private Button mRentalConfirmationChoiceOKButton;
    private Button mRentalConfirmationChoiceCancelButton;

    private AlertDialog mDialogAuthorizationError;
    private AlertDialog mDialogRentalExpired;
    private AlertDialog mDialogRentalConfirmationChoice;

    private LinearLayout mBottomPanelView = null;
    private View vAuthorizationErrorView = null;
    private View vRentalExpiredView = null;
    private View vRentalConfirmationChoiceView = null;
    private View ivControlVideo;

    private static final String VOLUME_PANEL_DIALOG_SHOW = "com.yecon.action.VOLUME_PANEL_DIALOG_SHOW";
    private static final String VOLUME_PANEL_DIALOG_DISMISS = "com.yecon.action.VOLUME_PANEL_DIALOG_DISMISS";
    private static final String TAG = "VideoPlaybackMainActivity";

    private int sScreenWidth = 0;
    private int sScreenHeight = 0;
    private int sVideoWidth = 0;
    private int sVideoHeight = 0;

    private boolean mIsControllerShow = true;
    // private PlayStatus mPlayStatus = PlayStatus.STATUS_NONE;
    private PlayStatus mPlayStatusTmp = PlayStatus.STATUS_NONE;
    private Uri playUri = null;
    private FrontRearDisplayStatus mFrontRearDisplayStatus = FrontRearDisplayStatus.FRONT_DISP;
    AlertDialog mErrorDialog = null;
    Toast mToast = null;

    private boolean mCanTouchView = true;

    private boolean mIsHWDecoded = false;
    private int mConfDisplayDecoding;
    private SharedPreferences mSharedPreferences;
    private boolean mSeekNotComplete = false;
    private int mProgress = -1;
    private int mDuration = 0;

    private SubtitleInfo mSubtitleInfo = new SubtitleInfo();
    private AudioChannelInfo mAudioChannelInfo = new AudioChannelInfo();

    private boolean mNeedRestoreStreamIndex = false;
    private boolean mNeedRestoreFFState = false;
    private boolean mQBSuspend = false;
    private boolean mQBPowerOn = false;
    private boolean mAccOff = false;
    private String mPlayingUsbVolume = null;
    private boolean mQBMountResume = false;

    String totalTimeStr = "";
    String currentTimeStr = "";
    String tmpText = "";

    private boolean mParkingEnable = false;
    private boolean mMcuIsParking = false;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case PROGRESS_CHANGED:
                    refreshNow();

                    mHandler.removeMessages(PROGRESS_CHANGED);
                    mHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 500);
                    break;

                case HIDE_CONTROLER:
                    if (!VideoPlaybackMainActivity.this.isResumed()) {
                        mHandler.removeMessages(HIDE_CONTROLER);
                        mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, 500);
                        break;
                    }
                    hideController();
                    break;

                case HANDLEERROR_PLAYNEXT:
                    playNext();
                    break;
                case PREPARE_FINISH:
                    // printLog("PREPARE_FINISH handle");
                    if (mQBPowerOn) {
                        mQBPowerOn = false;
                        if (mVideoView.isPlaying()) {

                            changePlaySts(PlayStatus.STATUS_PLAY);
                            cancelDelayHide();
                            hideControllerDelay();
                            mHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
                        } else {
                            changePlaySts(PlayStatus.STATUS_PAUSE);
                        }
                    }

                    updateTopDisplayInfo();
                    break;

                case JUDGE_TOUCH_DELAY:
                    mCanTouchView = true;
                    break;

                case SHOW_ERROR_TOAST:
                    showErrToastAndPlayNext();
                    break;

                case GO_HOME:
                    rearPlayToast.setText(backHomeString + timetohome);
                    rearPlayToast.show();
                    timetohome--;

                    if (timetohome < 0) {
                        rearPlayToast.cancel();
                        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
                        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
                        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(mHomeIntent);
                        timetohome = 3;
                    } else {
                        mHandler.sendEmptyMessageDelayed(GO_HOME, 1000);
                    }
                    break;

                case SEEK_TO_FILESTART:

                    if (mPrevBtnClickCnt < 2) {
                        mPrevBtnClickCnt = 0;
                        if (!mIsOnline) {
                            // mProgress = 0;
                            if (mSeekNotComplete) {
                                return;
                            }
                            resetFFState();
                            cancelDelayHide();
                            if (!mIsControllerShow) {
                                showController();
                                hideControllerDelay();
                            }
                            if (mSeekBar != null) {
                                mSeekBar.setProgress(0);
                            }
                            mVideoView.seekTo(0);
                            mSeekNotComplete = true;
                            mBtnOpPlay.setEnabled(false);
                            updateTopSongPlayState();
                            hideControllerDelay();
                        }
                    }
                    break;

                case HIDE_REARTOAST:
                    hideRearToast();
                    break;

                case UNMOUNT_EXITAPP:
                    exitApp();
                    break;

                case SHOW_VIDEOVIEW:
                    printLog("handleMessage - SHOW_VIDEOVIEW");
                    if (null != mVideoView) {

                        mVideoView.setBackgroundColor(0x00000000);
                        //mVideoView.setVisibility(View.VISIBLE);

                        if ((reardisppresentation != null)
                                && (reardisppresentation.mRearVideoView != null)) {
                            reardisppresentation.mRearVideoView.setBackgroundColor(0x00000000);
                            reardisppresentation.mRearVideoView.setVisibility(View.VISIBLE);
                        }

                        displayParking();

                    }
                    break;
                case PLAY_PAUSE:
                    if (msg.arg1 == 1) {
                        // play
                        doPlayPause(false, true);
                    }
                    else if (msg.arg1 == 0) {
                        // pause
                        doPlayPause(false, false);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void doPlayPause(boolean play) {
        mHandler.sendMessage(mHandler.obtainMessage(PLAY_PAUSE, play ? 1 : 0, 0));
    }

    private View.OnTouchListener mViewOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                printLog("View MotionEvent.ACTION_UP");
                cancelDelayHide();
                hideControllerDelay();
            }

            return false;
        }
    };

    private View.OnTouchListener mCustomViewOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_UP && mCanTouchView) {
                mHandler.sendEmptyMessageDelayed(JUDGE_TOUCH_DELAY, DELAY_TIME);
                mCanTouchView = false;

                if (!mIsControllerShow) {
                    cancelDelayHide();
                    showController();
                    hideControllerDelay();

                    // sbm.showSystemUI(1);
                } else {
                    float y = event.getY(); // get the hight of the touch point
                    float HightOfWin = mVideoView.getMeasuredHeight();
                    if ((y > HightOfWin * 0.77) || (y < HightOfWin * 0.15)) {
                        return true;
                    }
                    cancelDelayHide();
                    hideController();

                }

                if (mIsDivx) {
                    try {

                        // printLog("This File's is Divx, Then postTouchPos!!!");
                        mVideoView.postTouchPos((int) event.getX(), (int) event.getY());
                    } catch (Exception e) {
                        printLog("postTouchPos  error!");
                    }
                } else {
                    // printLog("This File's is not Divx!!!");
                }
            }
            return true;
        }
    };

    private void doPlayPause(boolean toogle, boolean play) {
        if (isRDSActive()) {
            return;
        }

        if (videoPlayerApp.getVideoPlayerContext().getPlayStatus() == PlayStatus.STATUS_PLAY
                && mSongFfInfo.speedType != SONG_FF_SPEED_NORMAL) {
            if (mSongFfInfo.speedType >= SONG_FF_SPEED_2X
                    && mSongFfInfo.speedType <= SONG_FF_SPEED_32X) {
                // mPlayStatus = PlayStatus.STATUS_FF;
                videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_FF);
            } else if (mSongFfInfo.speedType >= SONG_RW_SPEED_32X
                    && mSongFfInfo.speedType <= SONG_RW_SPEED_2X) {
                // mPlayStatus = PlayStatus.STATUS_RW;
                videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_RW);
            }
        }

        if (!toogle) {
            if (!play
                    && videoPlayerApp.getVideoPlayerContext().getPlayStatus() != PlayStatus.STATUS_STOP
                    && videoPlayerApp.getVideoPlayerContext().getPlayStatus() != PlayStatus.STATUS_PAUSE) {

            }
            else if (play
                    && videoPlayerApp.getVideoPlayerContext().getPlayStatus() != PlayStatus.STATUS_PLAY
                    && videoPlayerApp.getVideoPlayerContext().getPlayStatus() != PlayStatus.STATUS_FF
                    && videoPlayerApp.getVideoPlayerContext().getPlayStatus() != PlayStatus.STATUS_RW) {

            }
            else {
                return;
            }
        }

        // toogle status
        if (videoPlayerApp.getVideoPlayerContext().getPlayStatus() == PlayStatus.STATUS_STOP) {
            try {
                mIsDivxDrm = false;
                mVideoView.setVideoPath(videoPlayerApp.getVideoPlayerContext()
                        .getCurrentTrackPath());
                mPlayingUsbVolume = getUsbVolume(videoPlayerApp.getVideoPlayerContext()
                        .getCurrentTrackPath());
                mediaPlayStart();
                notifyPlayFileChanged();
            } catch (Exception e) {
                // printLog("mVideoView.setVideoPath(mPlayList.get(mPosition).path) Error");
            }
            // mPlayStatus = PlayStatus.STATUS_PLAY;
            videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_PLAY);
            mNeedRestoreStreamIndex = true;
            mNeedRestoreFFState = false;
            mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
            mSeekBar.setEnabled(mCanSeek);
        } else if (videoPlayerApp.getVideoPlayerContext().getPlayStatus() == PlayStatus.STATUS_PAUSE) {
            resetFFState();
            mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
            if (mIsSupportDivxHT311 && mIsDivxDrm) {
                mVideoView.SetVideoVisibility(true);
            }
            mDivxStopClickTimes = 0;
            // mPlayStatus = PlayStatus.STATUS_PLAY;
            videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_PLAY);
            mediaPlayStart();
            mSeekBar.setEnabled(mCanSeek);
        } else if (videoPlayerApp.getVideoPlayerContext().getPlayStatus() == PlayStatus.STATUS_FF
                || videoPlayerApp.getVideoPlayerContext().getPlayStatus() == PlayStatus.STATUS_RW) {
            mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
            // mPlayStatus = PlayStatus.STATUS_PLAY;
            videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_PLAY);
            setFFState(mSongFfInfo.speedType);
            mSeekBar.setEnabled(mCanSeek);
        } else {
            mVideoView.pause();
            // mPlayStatus = PlayStatus.STATUS_PAUSE;
            videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_PAUSE);
            if (mIsSupportDivxHT311 && mIsDivxDrm) {
                mVideoView.SetVideoVisibility(false);
            }
            mSongFfInfo.speedType = 0;
            mSeekBar.setEnabled(mCanSeek);
        }
        // printLog("mPlayStatus: " + mPlayStatus);
        mHandler.sendEmptyMessage(PROGRESS_CHANGED);
        updateTopSongPlayState();
        updatePauseButtonDisplay();
    }

    private OnClickListener mPauseButtonClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            doPlayPause(true, true);
        }

    };

    private BroadcastReceiver mMediaMountedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            String path = intent.getData().getPath();
            if (Intent.ACTION_MEDIA_REMOVED.equals(action)
                    || Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {
                try {
                	String curPath = videoPlayerApp.getVideoPlayerContext().getCurrentTrackPath();
                    if (curPath!=null && curPath.startsWith(path)) {
                    	 exitApp();
                        // reset rand and loop mode if current device is removed.
                        if (!mQBSuspend) {
                            mLoopModel = REPEAT_ALL;
                            mShuffModel = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {

            }
        }

    };

    private BroadcastReceiver mQuickBootListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            if (null == action) {
                return;
            }
            
            if (MCU_ACTION_ACC_ON.equals(action)) {
                mQBSuspend = false;
                mQBPowerOn = true;
                VideoPlayerContext playerContext = VideoPlayerApp.GetInstance().getVideoPlayerContext();
                if (isResumed() || playerContext.isPausedByAccOff()) {
                	playerContext.setPausedByAccOff(false);
                    doPlayPause(true);
                    onShowRearDisplay();
                }
                mNeedRestoreStreamIndex = true;
                mNeedRestoreFFState = true;

                if (!mIsControllerShow) {
                    cancelDelayHide();
                    showController();
                    hideControllerDelay();
                }

            } else if (MCU_ACTION_ACC_OFF.equals(action)) {
                mQBPowerOn = false;
                VideoPlayerContext playerContext = VideoPlayerApp.GetInstance().getVideoPlayerContext();
                if (mVideoView.isRealPlaying()) {
                	doPlayPause(false);
                	playerContext.setPausedByAccOff(true);
                }

                if (reardisppresentation != null) {
                    if (FrontRearDisplayStatus.REAR_DISP == mFrontRearDisplayStatus) {
                        mFrontRearDisplayStatus = FrontRearDisplayStatus.NONE_DISP;
                    } else if (FrontRearDisplayStatus.FRONT_REAR_DISP == mFrontRearDisplayStatus) {
                        mFrontRearDisplayStatus = FrontRearDisplayStatus.FRONT_DISP;
                    }
                    hideRearDisplay();
                    updateDestAudio();
                }

                //mVideoView.suspend();
                mQBSuspend = true;
                mHandler.removeMessages(PROGRESS_CHANGED);
            } else if (ACTION_QB_POWEROFF.equals(action)) {
                finish();
            }
        }

    };

    private BroadcastReceiver mMediaKeyListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            if(!VideoConstant.PLAY_BACKGROUND){
            	if(isBackGround)
            		return;
            }
            
            if (action.equals(MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS)) {
                int brakeStatus = intent.getIntExtra(INTENT_EXTRA_BRAKE_STATUS, 0);
                mMcuIsParking = (brakeStatus == 0) ? true : false;
                displayParking();
                return;
            }

            int source = intent.getIntExtra("cbm_source", 0);
            printLog("mMediaKeyListener - action: " + action + " - source: " + source);
            // if (CBManager.SRC_MM_AV != source) {
            // return;
            // }

            if (!mIsControllerShow) {
                showController();
            }

            cancelDelayHide();
            hideControllerDelay();

            if (action.equals(MCU_ACTION_MEDIA_NEXT)) {
                mBtnOpNext.performClick();
            } else if (action.equals(MCU_ACTION_MEDIA_PREVIOUS)) {
                mBtnOpPre.performClick();
            } else if (action.equals(MCU_ACTION_MEDIA_PLAY_PAUSE)) {
                mBtnOpPlay.performClick();
            }
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.authorization_error_ok:
                    if (mVideoView.isPlaying()) {
                        mVideoView.stopPlayback();
                    }
                    VideoPlaybackMainActivity.this.finish(); // exit the
                                                             // application
                    mDialogAuthorizationError.cancel();
                    if (null != reardisppresentation)
                    {
                        if (null != reardisppresentation.mRearDialogAuthorizationError) {
                            reardisppresentation.mRearDialogAuthorizationError.hide();
                            reardisppresentation.mRearDialogAuthorizationError.dismiss();
                            reardisppresentation.mRearDialogAuthorizationError = null;
                        }
                    }
                    break;

                case R.id.rental_expired_ok:
                    if (mVideoView.isPlaying()) {
                        mVideoView.stopPlayback();
                    }
                    VideoPlaybackMainActivity.this.finish(); // exit the
                                                             // application
                    mDialogRentalExpired.cancel();
                    if (null != reardisppresentation) {
                        if (null != reardisppresentation.mRearDialogRentalExpired) {
                            reardisppresentation.mRearDialogRentalExpired.hide();
                            reardisppresentation.mRearDialogRentalExpired.dismiss();
                            reardisppresentation.mRearDialogRentalExpired = null;
                        }
                    }
                    break;

                case R.id.rental_confirmation_choice_ok:
                    mDialogRentalConfirmationChoice.cancel();
                    if (null != reardisppresentation) {
                        if (null != reardisppresentation.mRearDialogRentalConfirmationChoice) {
                            reardisppresentation.mRearDialogRentalConfirmationChoice.hide();
                            reardisppresentation.mRearDialogRentalConfirmationChoice.dismiss();
                            reardisppresentation.mRearDialogRentalConfirmationChoice = null;
                        }
                    }
                    onPreparedtoPlay();
                    mediaPlayStart();
                    break;

                case R.id.rental_confirmation_choice_cancel:
                    if (mVideoView.isPlaying()) {
                        mVideoView.stopPlayback();
                    }
                    VideoPlaybackMainActivity.this.finish(); // exit the
                                                             // application
                    mDialogRentalConfirmationChoice.cancel();
                    if (null != reardisppresentation)
                    {
                        if (null != reardisppresentation.mRearDialogRentalConfirmationChoice) {
                            reardisppresentation.mRearDialogRentalConfirmationChoice.hide();
                            reardisppresentation.mRearDialogRentalConfirmationChoice.dismiss();
                            reardisppresentation.mRearDialogRentalConfirmationChoice = null;
                        }
                    }
                    break;
                default:
                    break;
            }
            return;
        }
    };

    public static boolean isRDSActive() {
        Parcel reply = Parcel.obtain();
        if (gCbmCtx == null) {
            gCbmCtx = new CBMCtx();
        }
        gCbmCtx.query(reply);
        int souce_count = reply.readInt();
        // printLog("rds --------souce_count=" + souce_count);
        for (int i = 0; i < souce_count; i++) {
            int t = reply.readInt();
            // printLog("rds --------souce=" + t);
            if (CBMCtx.SRC_AVIN_A_RDS == t) {
                reply.recycle();
                return true;
            }
        }
        reply.recycle();

        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "playback onCreate");
        super.onCreate(savedInstanceState);
//	        if(videoInstance!=null){
//	        	videoInstance.finish();
//	        	videoInstance = null;
//	        }

        videoPlayerApp = VideoPlayerApp.GetInstance();

        initData();

        initUI();
        
        if(!VideoConstant.PLAY_BACKGROUND){
			foreBackGroundHandler = new ForeBackGroundHandler(this);
	    	foreBackGroundHandler.init();
	    	foreBackGroundHandler.setForeBackGroundLisenter(this.getPackageName(), new ForeBackGroundLisenter() {
				
				@Override
				public void onForeGround() {
					// TODO Auto-generated method stub
					mHandler.post(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.i(TAG, "onForeGround");
							isBackGround = false;
							VideoPlayerContext playerContext = VideoPlayerApp.GetInstance().getVideoPlayerContext();
							if(playerContext.isStopedByBackground()){
								playerContext.setStopedByBackground(false);
								PlayStatus playStatus = VideoPlayerApp.GetInstance().getVideoPlayerContext().getPlayStatus();
	                    		if(playStatus == PlayStatus.STATUS_STOP){
									doPlayPause(true);
		                    	}
							}						
						}
						
					});
				}
				
				@Override
				public void onBackGround() {
					// TODO Auto-generated method stub
					//VideoPlayerApp.exitAllActivity();
					mHandler.post(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.i(TAG, "onBackGround");
							isBackGround = true;
							 //VideoPlayerApp.exitAllActivity();
							VideoPlayerContext playerContext = VideoPlayerApp.GetInstance().getVideoPlayerContext();
							PlayStatus playStatus = VideoPlayerApp.GetInstance().getVideoPlayerContext().getPlayStatus();
                    		if(playStatus != PlayStatus.STATUS_STOP 
	                    				&& playStatus != PlayStatus.STATUS_NONE){
								doStop();
								playerContext.setStopedByBackground(true);
	                    	}
						}
						
					});
				}
			});
	    	
		}
        	
        mHandler.postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handleIntentData();
			}
        	
        }, 100);        
        
        if (Intent.ACTION_VIEW.equalsIgnoreCase(getIntent().getAction())){
        	// disable list icon
            mBtnOpList.setEnabled(false);
            mBtnOpList.setVisibility(View.GONE);
        }
        else{
        	VideoPlaybackListActivity.setVideoPlaybackMainActivityInstance(this);
        }
    }

    @Override
    protected void onStart() {
        printLog("VideoPlaybackMainActivity - onStart");

        mVideoView.SetShowErrorToast(true);

        super.onStart();
    }

    @Override
    protected void onResume() {
        printLog("VideoPlaybackMainActivity - onResume");
        isBackGround = false;
        if(mAudioFocusListener!=null){
        	mAudioManager.requestAudioFocus(mAudioFocusListener,
            		AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        	audioFocusFileBrowserVideo(true);
        }
        else{
        	SourceManager.acquireSource(VideoPlaybackListActivity.getSourceTocken());
        	 VideoPlaybackListActivity.resume2PlayByAudioFocus();
        }      
        
        YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);

        mParkingEnable = SystemProperties.getBoolean("persist.sys.parking_enable",
                false);
        mMcuIsParking = SystemProperties.getBoolean("persist.sys.mcu_parking", false);

        printLog("VideoPlaybackMainActivity - onResume - mParkingEnable: " + mParkingEnable
                + " - mMcuIsParking: " + mMcuIsParking);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        if (mVideoView.isPlaying()) {
            mHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
        } 
        mHandler.sendEmptyMessageDelayed(SHOW_VIDEOVIEW, BLACK_TIMEOUT);
        
        // delay display UI and Control bar
        hideControllerDelay(); 

        setPauseButtonImage();

        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        onShowRearDisplay();

        super.onResume();
    }

    private void setPauseButtonImageInt(boolean isPause) {
        if (isPause) {
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

    private void setPauseButtonImage() {
        if (mVideoView.isPlaying()) {
            setPauseButtonImageInt(true);
            cancelDelayHide();
            hideControllerDelay();
        } else {
            setPauseButtonImageInt(false);
        }
    }

    @Override
    protected void onPause() {
        printLog("onPause - mPlayedTime: " + mPlayedTime);
        cancelToast();

        if (!mIsSupportDivxHT311) {
            printLog("VideoPlaybackMainActivity - onPause - setVideoScale(SCREEN_FULL)");
            setVideoScale(SCREEN_FULL);
        }

        // if (null != mVideoView) {
        // mVideoView.setBackgroundColor(0xFF000000);
        // mVideoView.setVisibility(View.INVISIBLE);
        // }
        // if ((reardisppresentation != null) &&
        // (reardisppresentation.mRearVideoView != null))
        // {
        // reardisppresentation.mRearVideoView.setBackgroundColor(0xFF000000);
        // reardisppresentation.mRearVideoView.setVisibility(View.INVISIBLE);
        // }
        // mBtnOpPlay.setImageResource(R.drawable.op_ic_play_bg);

        if (isFinishing()) {
            printLog("onPause because of finish this activity");
            mVideoView.post(new Runnable() {
                @Override
                public void run() {
                    if (mIsDivx) {
                        printLog("onPause mVideoView.clearDivxService()");
                        mVideoView.clearDivxService();
                    }
                    printLog("onPause - call mVideoView.stopPlayback");
                    mVideoView.stopPlayback();
                }
            });
        }        
        mVideoView.SetShowErrorToast(false);
        mHandler.removeMessages(SHOW_VIDEOVIEW);
        
        super.onPause();
    }

    void changePlaySts(PlayStatus sts) {
        // mPlayStatus = sts;
        videoPlayerApp.getVideoPlayerContext().setPlayStatus(sts);
        if (sts == PlayStatus.STATUS_PLAY) {
            setPauseButtonImageInt(true);
        } else if (sts == PlayStatus.STATUS_PAUSE) {
            setPauseButtonImageInt(false);
        }

    }

    @Override
    protected void onStop() {
        printLog("VideoPlaybackMainActivity - onStop");

        // modified by mtk94107: put it into onPause();
        // SharedPreferences uiState =
        // getPreferences(VideoPlayerActivity.MODE_PRIVATE);
        // SharedPreferences.Editor editor = uiState.edit();
        // editor.putString(KEY_PLAY_PATH, mVideoPlaying);
        // editor.commit();

        mHandler.removeMessages(HIDE_CONTROLER);
        hideRearToast();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "playback onDestroy");
        if(foreBackGroundHandler!=null){
    		foreBackGroundHandler.release();
    		foreBackGroundHandler=null;
    	}
        if(mAudioFocusListener!=null){
        	mAudioManager.abandonAudioFocus(mAudioFocusListener);
        	mAudioFocusListener=null;
        }
        if(mPlayedTime>=0 && mVideoPlaying!=null && mVideoPlaying.length()>0){
        	videoPlayerApp.getVideoPlayerContext().savePlaying(mVideoPlaying, mPlayedTime, mVideoView.getDuration());
        }
        mHandler.removeCallbacksAndMessages(null);
        hideRearToast();
        mVideoView.stopPlayback();
    	//videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_NONE);
        //videoPlayerApp.getVideoPlayerContext().clearPlayList();    
        deinitReceiver();
        removePlayNextMessage();

        if (reardisppresentation != null) {
            mFrontRearDisplayStatus = FrontRearDisplayStatus.FRONT_DISP;
            hideRearDisplay();
            updateDestAudio();
        }
        if (!Intent.ACTION_VIEW.equalsIgnoreCase(getIntent().getAction())){
        	VideoPlaybackListActivity.setVideoPlaybackMainActivityInstance(null);
        }   
        super.onDestroy();            
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        printLog("VideoPlaybackMainActivity - onNewIntent");

        setIntent(intent);

        String listType;
        listType = intent.getStringExtra(VideoPlayerContext.OPEN_LIST);
        if (listType != null) {
            int pos = intent.getIntExtra(VideoPlayerContext.OPEN_POS, -1);
            if (pos >= 0) {
                // list changed, need to open track now.
                mVideoPlaying = videoPlayerApp.getVideoPlayerContext().getTrackPathByPos(pos);
                if (mVideoPlaying != null) {
                    videoPlayerApp.getVideoPlayerContext().setCurrentPos(pos);
                    playUri = Uri.parse(mVideoPlaying);
                    mTempPosition = pos;
                    mSeekBar.setProgress(0);
                    playMedia();
                }
            }
            else {
                PlayStatus playStatus = videoPlayerApp.getVideoPlayerContext().getPlayStatus();
                if (playStatus == PlayStatus.STATUS_PAUSE || playStatus == PlayStatus.STATUS_STOP) {
                    mBtnOpPlay.performClick();
                }
            }
        }

        /*
         * mSeekBar.setProgress(0); Bundle bundle = getIntent().getExtras(); int position = -1;
         * ArrayList<String> revlist = null; if (bundle != null) { revlist =
         * bundle.getStringArrayList("PLAYLIST"); position = bundle.getInt("POSITION", -1);
         * bundle.clear(); bundle = null; if (null != revlist) { if (-1 != position) { mVideoPlaying
         * = revlist.get(position); playUri = Uri.parse(mVideoPlaying);
         * getPlayListByPathList(mPlayList, revlist, 0); mPosition = position; } else { // get the
         * index of playUri(the first play video) in revlist getPlayListByPathList(mPlayList,
         * revlist, 1); mPosition = getIndexByPathStringFromPlayList(mPlayList, revlist.get(0));
         * mVideoPlaying = revlist.get(0); playUri = Uri.parse(mVideoPlaying); } mTempPosition =
         * mPosition; mNeedSetDataSource = true; }
         */
        /*
         * if (gPlaylist.size() > 0 && gPlaylist.size() > position) { if (-1 != position) {
         * mVideoPlaying = gPlaylist.get(position); playUri = Uri.parse(mVideoPlaying);
         * getPlayListByPathList(mPlayList, gPlaylist, 0); if (mPosition != position) { mPosition =
         * position; playMedia(); } } else { // get the index of playUri(the first play video) in
         * revlist getPlayListByPathList(mPlayList, gPlaylist, 1); mPosition =
         * getIndexByPathStringFromPlayList(mPlayList, gPlaylist.get(0)); mVideoPlaying =
         * gPlaylist.get(0); playUri = Uri.parse(mVideoPlaying); } mTempPosition = mPosition;
         * mNeedSetDataSource = true; } if (null != mVideoView) { mVideoView.mUri = playUri; } }
         */

        // mHandler.removeMessages(PROGRESS_CHANGED);
        updateTopDisplayInfo();
    }

    private void initData() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mIsSupportDivxHT311 = getDivxSupportProperty();

        // printLog("VideoPlaybackMainActivity - initData - mIsSupportDivxHT311: "
        // + mIsSupportDivxHT311);

        envATC = new EnvironmentATC(this);

        backHomeString = getText(R.string.timegohome) + ": ";
        rearPlayToast = Toast.makeText(this, backHomeString, Toast.LENGTH_SHORT);

        mFFString = getResources().getString(R.string.prompt_fastforward);
        mRWString = getResources().getString(R.string.prompt_rewind);

        getScreenSize();

        mConfDisplayDecoding = getTheHWDisplayParam();

        initReceiver();

//        mOldPlaybackPath = sp.getString(KEY_PLAY_PATH, null);
//        mPlayedTimeBak = sp.getInt(KEY_PLAY_TIME, 0);

//        mVideoPlaybackOnCreate = true;

//        printLog("VideoPlaybackMainActivity - initData - mOldPlaybackPath: "
//                + mOldPlaybackPath + " - mVideoPlaying: " + mVideoPlaying);
    }

    private void initUI() {
        printLog("VideoPlaybackMainActivity - initUI - start");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.main);

        initTopControls();

        initBottomButtons();

        initVideoView();

        setInitResetDisplayUI();

        mParkingLayout = (LinearLayout) findViewById(R.id.layout_parking);
        mParkingLayout.setOnTouchListener(mCustomViewOnTouchListener);

        // mCurView = getWindow().getDecorView();
        ivControlVideo = findViewById(R.id.iv_control_video);
        ivControlVideo.setOnClickListener(this);

        printLog("VideoPlaybackMainActivity - initUI - end");
    }

    private void showToast() {
    	if(mToast!=null){
    		mToast.cancel();
    		mToast=null;
    	}
    	mToast = Toast.makeText(VideoPlaybackMainActivity.this,
                R.string.error_message, Toast.LENGTH_LONG);
    	mToast.show();
    }
    private void cancelToast(){
    	if(mToast!=null){
    		mToast.cancel();
    		mToast=null;
    	}
    }

    private void initTopControls() {
        setTopButtons();
        SetTopStateControl();
    }

    private void setTopButtons() {
        // mPosition = -1;
        // mTVSongPlayState = (TextView) findViewById(R.id.tv_song_play_state);
    }

    private void SetTopStateControl() {
        mSongFfInfo = new SongFfInfo();
        mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
        mSongFfInfo.mMaxFFRate = SONG_FF_SPEED_32X;
        mSongFfInfo.mMaxRWRate = SONG_RW_SPEED_32X;
    }

    private void initBottomButtons() {
        mBottomPanelView = (LinearLayout) findViewById(R.id.layout_bottom_control);
        mBottomPanelView.setVisibility(View.VISIBLE);

        mBtnOpLoop = (TextView) findViewById(R.id.btn_op_loop);
        mBtnOpLoop.setOnTouchListener(mViewOnTouchListener);
        mBtnOpLoop.setOnClickListener(this);

        mBtnOpRand = (TextView) findViewById(R.id.btn_op_rand);
        mBtnOpRand.setOnTouchListener(mViewOnTouchListener);
        mBtnOpRand.setOnClickListener(this);

        setLoopButtonImage();
        setRandButtonImage();

        mBtnOpPre = (TextView) findViewById(R.id.btn_op_pre);
        mBtnOpPre.setOnTouchListener(mViewOnTouchListener);
        mBtnOpPre.setOnClickListener(this);

        mBtnOpPlay = (TextView) findViewById(R.id.btn_op_play);
        mBtnOpPlay.setOnTouchListener(mViewOnTouchListener);
        mBtnOpPlay.setOnClickListener(mPauseButtonClickListener);

        mBtnOpNext = (TextView) findViewById(R.id.btn_op_next);
        mBtnOpNext.setOnTouchListener(mViewOnTouchListener);
        mBtnOpNext.setOnClickListener(this);

        mBtnOpList = (TextView) findViewById(R.id.btn_op_list);
        mBtnOpList.setOnTouchListener(mViewOnTouchListener);
        mBtnOpList.setOnClickListener(this);

        mTVTrackIndex = (TextView) mBottomPanelView.findViewById(R.id.tv_track_index);
        mTVTrackIndex.setVisibility(View.INVISIBLE);

        mTVProcessTime = (TextView) mBottomPanelView.findViewById(R.id.tv_progress_time);
        mTVTotalTime = (TextView) mBottomPanelView.findViewById(R.id.tv_total_time);

        mSeekBar = (SeekBar) mBottomPanelView.findViewById(R.id.sb_playback_process);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromuser) {
                if (unSupportType == 0) {
                    // mCanSeek = false;
                    // mSeekBar.setEnabled(mCanSeek);
                    // showToast(R.string.not_support_seek);
                    return;
                }
                if (!fromuser) {
                    return;
                }
                if (isRDSActive()) {
                    return;
                }

                if (!mIsOnline) {
                    printLog("user drag seek bar seekbar pso change:  " + progress);
                    mProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                mHandler.removeMessages(HIDE_CONTROLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                printLog("user drag seek bar click up when mProgress:  " + mProgress);
                // mSeekNotComplete = false;

                if (mProgress != -1) {
                    mVideoView.seekTo(mProgress);
                    printLog("user seek to " + mProgress);
                    mSeekNotComplete = true;
                    // mSeekBar.setEnabled(false);
                    mBtnOpPlay.setEnabled(false);
                }
                hideControllerDelay();
                // onProgressChanged(seekBar, mProgress, true);
                if (IS_AUTO_HIDE_PANEL) {
                    mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, HIDEDELAYTIME);
                }
            }
        });
    }

    private void initVideoView() {
        printLog("VideoPlaybackMainActivity - initVideoView()");

        sVideoWidth = 0;
        sVideoHeight = 0;

        mVideoView = (FrontVideoView) findViewById(R.id.video_view);

        setVideoScale(SCREEN_FULL);
        mPrevBtnClickCnt = 0;
        mCanTouchView = true;

        mVideoView.SetHandler(mHandler);
        // mVideoView.setBackgroundColor(0xFF000000);
        // mVideoView.setVisibility(View.INVISIBLE);        
        mVideoView.setOnTouchListener(mCustomViewOnTouchListener);

        mVideoView.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (1000 == what) {
                    printLog("CBM FORBIDDEN VIDEO");
                    if (reardisppresentation != null) {
                        mFrontRearDisplayStatus = FrontRearDisplayStatus.FRONT_DISP;
                        hideRearDisplay();
                        updateDestAudio();
                    }
                    finish();
                    return (true);
                } else if (AtcMediaPlayer.MEDIA_ERROR_CARD_EJECT == what) {
                    printLog("onError - AtcMediaPlayer.MEDIA_ERROR_CARD_EJECT");
                    exitApp();
                    return (true);
                }

                mVideoView.stopPlayback();
                mIsOnline = false;

                try {
                    String storageVolume = getStorageVolume(videoPlayerApp.getVideoPlayerContext()
                            .getCurrentTrackPath());
                    if (null == storageVolume) {
                        if (reardisppresentation != null) {
                            mFrontRearDisplayStatus = FrontRearDisplayStatus.FRONT_DISP;
                            hideRearDisplay();
                            updateDestAudio();
                        }
                        printLog("Storage device isn't present, so finish this activity!");
                        finish();
                        return (true);
                    }
                } catch (Exception e) {
                    printLog("onError - Exception occured: " + e);
                }

                showErrToastAndPlayNext();

                mBtnOpPlay.setEnabled(false);
                mSeekBar.setEnabled(false);

                return false;
            }

        });

        mVideoView.setSizeChangedListener(new ISizeChangedLinstener() {
            @Override
            public void sizeChangedEvent(int width, int height) {
                if (mIsSupportDivxHT311) {
                    setVideoDisplayRect(width, height);
                } else {
                    setVideoScale(SCREEN_FULL);
                }
            }

        });

        mVideoView.setOnPreparedListener(new OnPreparedListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onPrepared(MediaPlayer arg0) {
                // printLog("onPrepared Begin - mPlayStatus: " + mPlayStatus);

                mCanSeek = true;
                mCanFF = true;
                mCanRW = true;

                mBtnOpNext.setEnabled(true);
                mBtnOpPlay.setEnabled(true);
                mSeekBar.setEnabled(mCanSeek);

                mSeekNotComplete = false;
                mProgress = -1;
                mIsDivx = false;

                mHandler.removeMessages(PROGRESS_CHANGED);
                mHandler.removeMessages(HANDLEERROR_PLAYNEXT);
                mHandler.removeMessages(PREPARE_FINISH);
                mHandler.sendEmptyMessage(PREPARE_FINISH);

                if (mIsSupportDivxHT311 && mIsDivxDrm && (null != mDivxDrmInfo)) {
                    if (mDivxDrmInfo.maxplayCount > 0) {
                        if (mDivxDrmInfo.remainPlayCount > 0) {
                            mVideoView.CancelAutoStartWhenPrepared();
                            cancelDelayHide();
                            showController();
                            showDialog(3);
                            String tf31 = getString(R.string.rental_has_used)
                                    + (mDivxDrmInfo.maxplayCount - mDivxDrmInfo.remainPlayCount) +
                                    getString(R.string.rental_tip_mid) + mDivxDrmInfo.maxplayCount
                                    + getString(R.string.rental_tip_suffix);
                            mRentalConfirmationChoiceText1.setText(tf31);
                            String tf32 = getString(R.string.rental_continue);
                            mRentalConfirmationChoiceText2.setText(tf32);
                            if (null != reardisppresentation) {
                                printLog("onPrepared ---  reardisppresentation.showRentalConfirmationDialog()");
                                reardisppresentation.showRentalConfirmationDialog();
                                if (null != reardisppresentation.mRearRentalConfirmationChoiceText1) {
                                    reardisppresentation.mRearRentalConfirmationChoiceText1
                                            .setText(tf31);
                                }

                                if (null != reardisppresentation.mRearRentalConfirmationChoiceText2) {
                                    reardisppresentation.mRearRentalConfirmationChoiceText2
                                            .setText(tf32);
                                }
                            }
                        } else {
                            mVideoView.CancelAutoStartWhenPrepared();
                            cancelDelayHide();
                            showController();
                            showDialog(2);
                            String tf21 = getString(R.string.rental_has_used)
                                    + (mDivxDrmInfo.maxplayCount - mDivxDrmInfo.remainPlayCount) +
                                    getString(R.string.rental_tip_mid) + mDivxDrmInfo.maxplayCount
                                    + getString(R.string.rental_tip_suffix);
                            mRentalExpiredText1.setText(tf21);
                            String tf22 = getString(R.string.rental_has_expired);
                            mRentalExpiredText2.setText(tf22);
                            if (null != reardisppresentation) {
                                reardisppresentation.showRentalExpiredDialog();
                                if (null != reardisppresentation.mRearRentalExpiredText2) {
                                    reardisppresentation.mRearRentalExpiredText2.setText(tf21);
                                }
                                if (null != reardisppresentation.mRearRentalExpiredText2) {
                                    reardisppresentation.mRearRentalExpiredText2.setText(tf22);
                                }
                            }
                        }
                    } else {
                        printLog("onPrepared  :mDivxDrmInfo.maxplayCount.:"
                                + mDivxDrmInfo.maxplayCount);
                        printLog("onPrepared  :mDivxDrmInfo.remainplayCount:"
                                + mDivxDrmInfo.remainPlayCount);
                        printLog("onPrepared  :mDivxDrmInfo.cgmsa]:" + mDivxDrmInfo.cgmsa);
                        printLog("onPrepared  :mDivxDrmInfo.acptb" + mDivxDrmInfo.acptb);
                        printLog("onPrepared  :mDivxDrmInfo.digitalpretection"
                                + mDivxDrmInfo.digitalProtection);
                        printLog("onPrepared  :mDivxDrmInfo.lict" + mDivxDrmInfo.lict);
                        onPreparedtoPlay();
                    }
                } else {
                    onPreparedtoPlay();
                }
                
              //saveVideoPath();
                String videoPath = mVideoView.getVideoPath();
                if(videoPath!=null){
	                int seekTime = videoPlayerApp.getVideoPlayerContext().getSeekPos(videoPath);	            
	                if(mCanSeek){
	                	 mVideoView.seekTo(seekTime);
	                }
	                videoPlayerApp.getVideoPlayerContext().savePlaying(videoPath, seekTime, mVideoView.getDuration());
                }
            }
        });

        mVideoView.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                printLog("onCompletion!!!");
                videoPlayerApp.getVideoPlayerContext().removePlayed(mVideoView.getVideoPath());
                if (mSeekBar != null) {
                    mSeekBar.setProgress(0);
                }
                if (mIsSupportDivxHT311
                        && (videoPlayerApp.getVideoPlayerContext().getPlayStatus() == PlayStatus.STATUS_RW)) {
                    if (!mIsOnline) {
                        resetFFState();
                        cancelDelayHide();
                        if (!mIsControllerShow) {
                            showController();
                            hideControllerDelay();
                        }
                        mediaPlayStart();
                        printLog("Send Message -- PROGRESS_CHANGED!!!");
                        // mPlayStatus = PlayStatus.STATUS_PLAY;
                        videoPlayerApp.getVideoPlayerContext()
                                .setPlayStatus(PlayStatus.STATUS_PLAY);
                        updateTopSongPlayState();
                        mHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
                    }
                    return;
                }

                mSeekBar.setEnabled(false);

                // //this function is auto called when the current video is over
                int size = videoPlayerApp.getVideoPlayerContext().getListSize();
                int pos = videoPlayerApp.getVideoPlayerContext().getCurrentPos();
                printLog("onCompletion current mPlayList size: " + size);

                mIsOnline = false;

                if (!mIsDivx
                        && (videoPlayerApp.getVideoPlayerContext().getPlayStatus() != PlayStatus.STATUS_RW)) {
                    if (mShuffModel == SHUFFLE_NORMAL) {
                        pos = (int) randomIndex.nextInt(size);
                    } else if (mLoopModel == REPEAT_CURRENT) {
                        pos = pos + 0;
                    } else if (mLoopModel == REPEAT_ALL) {
                        pos++;
                    } else if (mLoopModel == REPEAT_NONE) {
                        pos++;
                        if (pos >= size) {
                            pos = size - 1;
                            doStop();
                            return;
                        }
                    }
                } else {
                    printLog("initVideoView Divx onCompletion current mPosition=" + pos);
                }

                mTempPosition = pos;

                if (pos < size) {
                    try {
                        mIsDivxDrm = false;

                        videoPlayerApp.getVideoPlayerContext().setCurrentPos(pos);
                        mVideoPlaying = videoPlayerApp.getVideoPlayerContext()
                                .getCurrentTrackPath();

                        mVideoView.setVideoPath(mVideoPlaying);
                        mediaPlayStart();

                        mPlayingUsbVolume = getUsbVolume(mVideoPlaying);

                        // mPlayStatus = PlayStatus.STATUS_PLAY;
                        videoPlayerApp.getVideoPlayerContext()
                                .setPlayStatus(PlayStatus.STATUS_PLAY);
                        
                        mNeedRestoreStreamIndex = false;
                        mNeedRestoreFFState = false;
                        mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
                        notifyPlayFileChanged();
                    } catch (Exception e) {
                        printLog("mVideoView.setVideoPath(mPlayList.get(mPosition).path) Error");
                    }
                } else {
                    if (!IS_CYCLE_PLAY_LIST) {
                        // // cycle play is false ,exit the app
                        if (mVideoView.isPlaying()) {
                            printLog("one cycle over stopPlayback ");
                            mVideoView.stopPlayback();
                        }
                        VideoPlaybackMainActivity.this.finish();
                        return;
                    } else {
                        // /cycle play
                        videoPlayerApp.getVideoPlayerContext().setCurrentPos(0);
                        mTempPosition = 0;

                        try {
                            mIsDivxDrm = false;

                            mVideoPlaying = videoPlayerApp.getVideoPlayerContext()
                                    .getCurrentTrackPath();
                            mVideoView.setVideoPath(mVideoPlaying);

                            mPlayingUsbVolume = getUsbVolume(mVideoPlaying);
                            mediaPlayStart();
                            // mPlayStatus = PlayStatus.STATUS_PLAY;
                            videoPlayerApp.getVideoPlayerContext().setPlayStatus(
                                    PlayStatus.STATUS_PLAY);
                            mNeedRestoreStreamIndex = false;
                            mNeedRestoreFFState = false;
                            mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
                            //saveVideoPath();
                            notifyPlayFileChanged();
                        } catch (Exception e) {
                            printLog("mPlayList.size() == 0");
                        }
                        // End : Modified by fei.liu
                    }
                }
                printLog("onCompletion current position="
                        + videoPlayerApp.getVideoPlayerContext().getCurrentPos());
                updateTopDisplayInfo();
                //mPlayedTime = 0;
            }
        });

        mVideoView.setOnSeekCompleteListener(new OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(MediaPlayer arg0) {
                printLog("onSeekComplete!!!");
                mSeekNotComplete = false;
                mProgress = -1;
                mBtnOpPlay.setEnabled(true);

                PlayStatus playStatus = videoPlayerApp.getVideoPlayerContext().getPlayStatus();
                if ((playStatus == PlayStatus.STATUS_PLAY) ||
                        (playStatus == PlayStatus.STATUS_FF) ||
                        (playStatus == PlayStatus.STATUS_RW)) {
                }                
                printLog("onSeekComplete end!!!");
            }
        });

        mVideoView.setOnInfoListener(new OnInfoListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onInfo(MediaPlayer player, int whatInfo, int extra) {
                printLog("onInfo,whatInfo=" + whatInfo);

                switch (whatInfo) {
                    case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        break;

                    case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        updateVideoDuration();
                        // initAllTrackInfo();
                        initDecodedInfo();
                        break;

                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        break;

                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        printLog("onInfo - MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START");
                        //mVideoView.setVisibility(View.VISIBLE);
                        break;

                    case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        // not seekable
                        mCanSeek = false;
                        mSeekBar.setEnabled(mCanSeek);
                        break;

                    case AtcMediaPlayer.MEDIA_INFO_DIVX: // add by mtk94107
                        printLog(" MEDIA_INFO_DIVX Divx menu file");
                        mIsDivx = true;
                        break;

                    case AtcMediaPlayer.MEDIA_INFO_UNSUPPORTED_VIDEO: {
                        if ((extra & CAP_VIDEO_RESOLUTION_UNSUPPORT) != 0) {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Resolution not supported!");
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_video_resolution, Toast.LENGTH_SHORT)
                                    .show();
                            showRearToast(R.string.not_support_video_resolution, Toast.LENGTH_SHORT);
                        } else if ((extra & CAP_VIDEO_BITRATE_UNSUPPORT) != 0) {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Bitrate not supported!");
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_video_bitrate, Toast.LENGTH_SHORT).show();
                            showRearToast(R.string.not_support_video_bitrate, Toast.LENGTH_SHORT);
                        } else if ((extra & CAP_VIDEO_FRAMERATE_UNSUPPORT) != 0) {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Frame Rate not supported!");
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_video_framerate, Toast.LENGTH_SHORT)
                                    .show();
                            showRearToast(R.string.not_support_video_framerate, Toast.LENGTH_SHORT);
                        } else if ((extra & CAP_VIDEO_CODEC_UNSUPPORT) != 0) {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Format not supported!");
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_video_codec, Toast.LENGTH_SHORT).show();
                            showRearToast(R.string.not_support_video_codec, Toast.LENGTH_SHORT);
                        } else if ((extra & CAP_VIDEO_PROFILE_LEVEL_UNSUPPORT) != 0) {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Profile Level not supported!");
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_video_profilelevel, Toast.LENGTH_SHORT)
                                    .show();
                            showRearToast(R.string.not_support_video_profilelevel,
                                    Toast.LENGTH_SHORT);
                        } else {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO, extra = " + extra);
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_video, Toast.LENGTH_SHORT).show();
                            showRearToast(R.string.not_support_video, Toast.LENGTH_SHORT);
                        }
                    }
                        break;

                    case AtcMediaPlayer.MEDIA_INFO_UNSUPPORTED_AUDIO: {
                        if ((extra & CAP_AUDIO_CODEC_UNSUPPORT) != 0) {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO -- Audio Format not supported!");
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_audio_codec, Toast.LENGTH_SHORT).show();
                            showRearToast(R.string.not_support_audio_codec, Toast.LENGTH_SHORT);
                        } else if ((extra & CAP_AUDIO_BITRATE_UNSUPPORT) != 0) {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO -- Audio Bitrate not supported!");
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_audio_bitrate, Toast.LENGTH_SHORT).show();
                            showRearToast(R.string.not_support_audio_bitrate, Toast.LENGTH_SHORT);
                        } else if ((extra & CAP_AUDIO_SAMPLERATE_UNSUPPORT) != 0) {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO -- Audio Sampling Rate not supported!");
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_audio_samplingrate, Toast.LENGTH_SHORT)
                                    .show();
                            showRearToast(R.string.not_support_audio_samplingrate,
                                    Toast.LENGTH_SHORT);
                        } else if ((extra & CAP_AUDIO_PROFILE_LEVEL_UNSUPPORT) != 0) {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO -- Audio Profile Level not supported!");
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_audio_profilelevel, Toast.LENGTH_SHORT)
                                    .show();
                            showRearToast(R.string.not_support_audio_profilelevel,
                                    Toast.LENGTH_SHORT);
                        } else {
                            printLog("onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO, extra = " + extra);
                            Toast.makeText(VideoPlaybackMainActivity.this,
                                    R.string.not_support_audio, Toast.LENGTH_SHORT).show();
                            showRearToast(R.string.not_support_audio, Toast.LENGTH_SHORT);
                        }
                    }
                        break;

                    case AtcMediaPlayer.MEDIA_INFO_UNSUPPORTED_MENU:
                        printLog("onInfo:MEDIA_INFO_UNSUPPORTED_MENU");
                        Toast.makeText(VideoPlaybackMainActivity.this, R.string.not_support_menu,
                                Toast.LENGTH_SHORT).show();
                        showRearToast(R.string.not_support_menu, Toast.LENGTH_SHORT);
                        break;

                    case AtcMediaPlayer.MEDIA_INFO_DIVXDRM:
                        printLog("onInfo  :MEDIA_INFO_DIVXDRM");
                        mDivxDrmInfo = mVideoView.getDivxDrmInfo();
                        printLog("onInfo  :mDivxDrmInfo.maxplayCount.:" + mDivxDrmInfo.maxplayCount);
                        printLog("onInfo  :mDivxDrmInfo.remainplayCount:"
                                + mDivxDrmInfo.remainPlayCount);
                        printLog("onInfo  :mDivxDrmInfo.cgmsa]:" + mDivxDrmInfo.cgmsa);
                        printLog("onInfo  :mDivxDrmInfo.acptb" + mDivxDrmInfo.acptb);
                        printLog("onInfo  :mDivxDrmInfo.digitalpretection"
                                + mDivxDrmInfo.digitalProtection);
                        printLog("onInfo  :mDivxDrmInfo.lict" + mDivxDrmInfo.lict);

                        mIsDivxDrm = true;
                        break;

                    case AtcMediaPlayer.MEDIA_INFO_DIVXDRM_ERROR:
                        if ((extra == AtcMediaPlayer.MEDIA_ERROR_DIVXDRM_NOT_AUTHORIZED) ||
                                (extra == AtcMediaPlayer.MEDIA_ERROR_DIVXDRM_NOT_REGISTERED) ||
                                (extra == AtcMediaPlayer.MEDIA_ERROR_DIVXDRM_NEVER_REGISTERED)) {
                            printLog("onInfo :MEDIA_ERROR_DRM_NO_LICENSE");
                            if (mIsSupportDivxHT311) {
                                mVideoView.CancelAutoStartWhenPrepared();
                                cancelDelayHide();
                                showController();
                                showDialog(1);
                                String tf1 = getString(R.string.authorization_error);
                                mAuthorizationErrorText.setText(tf1);
                                if (null != reardisppresentation) {
                                    reardisppresentation.showAuthorizationErrorDialog();
                                    if (null != reardisppresentation.mRearAuthorizationErrorText) {
                                        reardisppresentation.mRearAuthorizationErrorText
                                                .setText(tf1);
                                    }
                                }
                            }
                        } else if (extra == AtcMediaPlayer.MEDIA_ERROR_DIVXDRM_RENTAL_EXPIRED) {
                            printLog("onInfo :MEDIA_ERROR_DRM_RENTAL_EXPIRED");
                            if (mIsSupportDivxHT311) {
                                mVideoView.CancelAutoStartWhenPrepared();
                                cancelDelayHide();
                                showController();
                                showDialog(2);
                                String tf21 = getString(R.string.rental_has_used)
                                        + (mDivxDrmInfo.maxplayCount - mDivxDrmInfo.remainPlayCount)
                                        +
                                        getString(R.string.rental_tip_mid)
                                        + mDivxDrmInfo.maxplayCount
                                        + getString(R.string.rental_tip_suffix);
                                mRentalExpiredText1.setText(tf21);
                                String tf22 = getString(R.string.rental_has_expired);
                                mRentalExpiredText2.setText(tf22);
                                if (null != reardisppresentation) {
                                    reardisppresentation.showRentalExpiredDialog();
                                    if (null != reardisppresentation.mRearRentalExpiredText1) {
                                        reardisppresentation.mRearRentalExpiredText1.setText(tf21);
                                    }
                                    if (null != reardisppresentation.mRearRentalExpiredText2) {
                                        reardisppresentation.mRearRentalExpiredText2.setText(tf22);
                                    }
                                }
                            }
                        } else {
                            printLog("onInfo, whatInfo=" + whatInfo + ", extra=" + extra);
                        }
                        break;

                    default:
                        printLog("other onInfo:" + whatInfo);
                }
                return false;
            }
        });

    }

    private void setInitResetDisplayUI() {
        updateTopDisplayInfo();
        initBottomButtons();
    }

    void initReceiver() {
        initMediaMountedReceiver();

        IntentFilter filter = new IntentFilter();

//        filter.addAction(ACTION_MUSIC_STATE_CHANGE_FORECAST);
//        filter.addAction(ACTION_AVIN_STATE_CHANGE_FORECAST);
//        filter.addAction(ACTION_DVD_STATE_CHANGE_FORECAST);
//        registerReceiver(mConcurrenceAppListener, filter);

        filter = new IntentFilter();
        filter.addAction(ACTION_QB_POWERON);
        filter.addAction(ACTION_QB_POWEROFF);
        filter.addAction(ACTION_QB_PREPOWEROFF);
        filter.addAction(MCU_ACTION_ACC_ON);
        filter.addAction(MCU_ACTION_ACC_OFF);
        registerReceiver(mQuickBootListener, filter);

        IntentFilter mediaKeyFilter = new IntentFilter();
        mediaKeyFilter.addAction(MCU_ACTION_MEDIA_NEXT);
        mediaKeyFilter.addAction(MCU_ACTION_MEDIA_PREVIOUS);
        mediaKeyFilter.addAction(MCU_ACTION_MEDIA_PLAY_PAUSE);
        mediaKeyFilter.addAction(MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS);
        registerReceiver(mMediaKeyListener, mediaKeyFilter);
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

    void deinitReceiver() {
        unregisterReceiver(mQuickBootListener);
        unregisterReceiver(mMediaKeyListener);
        // unregisterReceiver(mConcurrenceAppListener);
        unregisterReceiver(mMediaMountedReceiver);
    }

    private void mediaPlayStart() {
        if (mVideoView != null) {
            mVideoView.start();
            hideControllerDelay();
        }
    }

    private String getUsbVolume(String file) {
        String[] usbVolumes = envATC.getUsbMountedPaths();

        if ((null == usbVolumes) || (null == file)) {
            return (null);
        }

        for (int i = 0; i < usbVolumes.length; i++) {
            if (file.startsWith(usbVolumes[i])) {
                printLog("getUsbVolume - this is a usb file, file = " + file + ", volume = "
                        + usbVolumes[i]);
                return (usbVolumes[i]);
            }
        }

        return (null);
    }

    private String getStorageVolume(String file) {
        String[] storageVolumes = envATC.getStorageMountedPaths();

        if ((null == storageVolumes) || (null == file)) {
            return (null);
        }
        for (int i = 0; i < storageVolumes.length; i++) {
            if (file.startsWith(storageVolumes[i])) {
                printLog("getStorageVolume - this is a file, file = " + file + ", volume = "
                        + storageVolumes[i]);
                return (storageVolumes[i]);
            }
        }

        return (null);
    }

    private static boolean getDivxSupportProperty() {
        return AtcMediaPlayer.isSupportDivxHT31();
    }

    public void onShowRearDisplay() {
        if (isRDSActive()) {
            return;
        }
        printLog("onShowRearDisplay enter");
        mCvbsEnable = SystemProperties.getBoolean("persist.sys.cvbs_enable", true);
        if (mCvbsEnable && FrontRearDisplayStatus.FRONT_REAR_DISP != mFrontRearDisplayStatus) {
            mFrontRearDisplayStatus = FrontRearDisplayStatus.FRONT_REAR_DISP;
            showRearDisplay(); // open rear Display
        } else if (!mCvbsEnable
                && FrontRearDisplayStatus.FRONT_REAR_DISP == mFrontRearDisplayStatus) {
            mFrontRearDisplayStatus = FrontRearDisplayStatus.FRONT_DISP;
            hideRearDisplay(); // close rear Display
        }
        updateDestAudio();
    }

    public void showRearToast(int resId, int toastduration) {
        printLog("showRearToast");
        if (reardisppresentation != null) {
            if (toastduration == Toast.LENGTH_SHORT) {
                mHandler.removeMessages(HIDE_REARTOAST);
                mHandler.sendEmptyMessageDelayed(HIDE_REARTOAST, 3000);
            } else if (toastduration == Toast.LENGTH_LONG) {
                mHandler.removeMessages(HIDE_REARTOAST);
                mHandler.sendEmptyMessageDelayed(HIDE_REARTOAST, 4000);
            }
        }
    }

    public void hideRearToast() {
        // printLog("VideoPlaybackMainActivity - hideRearToast");
        if (reardisppresentation != null) {
            mHandler.removeMessages(HIDE_REARTOAST);
        }
    }

    @SuppressLint("InlinedApi")
    void initDisplayPresentation() {
        DisplayManager mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        String displayCategory = DisplayManager.DISPLAY_CATEGORY_PRESENTATION;
        Display[] displays = mDisplayManager.getDisplays(displayCategory);

        // printLog("There are currently " + displays.length +
        // " displays connected.");
        // for (Display display : displays) {
        // printLog("  " + display);
        // }
        if (displays.length < 1) {
            printLog("only get (" + displays.length + ")display instance");
            reardisppresentation = null;
            return;
        }
        if (null != videoPlayerApp) {
            reardisppresentation = new RearDispPresentation(videoPlayerApp, displays[0]);
        } else {
            reardisppresentation = new RearDispPresentation(this, displays[0]);
        }
        mVideoView.setRearDispPresentation(reardisppresentation);
    }

    private void showRearDisplay() {
        if (reardisppresentation == null) {
            initDisplayPresentation();
            if (null == reardisppresentation) {
                printLog("showRearDisplay fail for reardisppresentation not init");
                return;
            }
        }

        // if (false) {
        // if ((null != reardisppresentation.mRearVideoView) &&
        // (sVideoWidth != 0) && (sVideoHeight != 0)) {
        // reardisppresentation.setVideoDisplayRect(sVideoWidth, sVideoHeight);
        // }
        // }
        if(!reardisppresentation.isShowing()){
        	reardisppresentation.show();
        }      
        if (null != reardisppresentation.mRearVideoView) {
        	  mVideoView.mSubVideoView = reardisppresentation.mRearVideoView;
            reardisppresentation.mRearVideoView.mMasterVideoView = mVideoView;
        }
    }

    private void hideRearDisplay() {
        if (reardisppresentation == null) {
            printLog("hideRearDisplay fail for reardisppresentation not init");
            return;
        }

        if (null != reardisppresentation.mRearVideoView) {
            reardisppresentation.mRearVideoView.mMasterVideoView = null;
        }

        if (null != reardisppresentation.mRearDialogRentalConfirmationChoice) {
            reardisppresentation.mRearDialogRentalConfirmationChoice.hide();
            reardisppresentation.mRearDialogRentalConfirmationChoice.dismiss();
            reardisppresentation.mRearDialogRentalConfirmationChoice = null;
            reardisppresentation.mRearRentalConfirmationChoiceText1 = null;
            reardisppresentation.mRearRentalConfirmationChoiceText2 = null;
            reardisppresentation.vRearRentalConfirmationChoiceView = null;
        }
        if (null != reardisppresentation.mRearDialogRentalExpired) {
            reardisppresentation.mRearDialogRentalExpired.hide();
            reardisppresentation.mRearDialogRentalExpired.dismiss();
            reardisppresentation.mRearDialogRentalExpired = null;
            reardisppresentation.mRearRentalExpiredText1 = null;
            reardisppresentation.mRearRentalExpiredText2 = null;
            reardisppresentation.vRearRentalExpiredView = null;
        }
        if (null != reardisppresentation.mRearDialogAuthorizationError) {
            reardisppresentation.mRearDialogAuthorizationError.hide();
            reardisppresentation.mRearDialogAuthorizationError.dismiss();
            reardisppresentation.mRearDialogAuthorizationError = null;
            reardisppresentation.mRearAuthorizationErrorText = null;
            reardisppresentation.vRearAuthorizationErrorView = null;
        }

        mVideoView.mSubVideoView = null;
        if (null != mVideoView.mMediaPlayer) {
            try {
                mVideoView.mMediaPlayer.setRearDisplay(null);
            } catch (Exception e) {
                printLog("Exception in hideRearDisplay setdisplay: " + e);
                String err = e.getMessage();
                printLog("Exception in hideRearDisplay setdisplay, exception message: " + err);
            }
        }
        reardisppresentation.hide();
        reardisppresentation.dismiss();
        reardisppresentation = null;
    }

    private void updateDestAudio() {
        if (mVideoView.mMediaPlayer == null) {
            printLog("setDestAudio fail for mVideoView.mMediaPlayer is null");
            return;
        }
        if (mFrontRearDisplayStatus == FrontRearDisplayStatus.FRONT_DISP) {
            mVideoView.openDestAudio(AtcMediaPlayer.MEDIA_DEST_FRONT);
            mVideoView.closeDestAudio(AtcMediaPlayer.MEDIA_DEST_REAR);
        } else if (mFrontRearDisplayStatus == FrontRearDisplayStatus.FRONT_REAR_DISP) {
            mVideoView.openDestAudio(AtcMediaPlayer.MEDIA_DEST_FRONT_REAR);
        } else if (mFrontRearDisplayStatus == FrontRearDisplayStatus.REAR_DISP) {
            mVideoView.closeDestAudio(AtcMediaPlayer.MEDIA_DEST_FRONT);
            mVideoView.openDestAudio(AtcMediaPlayer.MEDIA_DEST_REAR);
        } else if (mFrontRearDisplayStatus == FrontRearDisplayStatus.NONE_DISP) {
            mVideoView.closeDestAudio(AtcMediaPlayer.MEDIA_DEST_FRONT_REAR);
        }
    }

    private void updateTopDisplayInfo() {
        updateTopIVImageSource();
        updateTopSongPlayState();
    }

    private void updateTopIVImageSource() {
        String lower = null;

        if (null != playUri) {
            lower = playUri.toString().toLowerCase();
        }

        if ((null != lower) && lower.startsWith("/mnt/")) {
            mIsOnline = false;
        } else {
            // sd card is the default
            mIsOnline = false;
        }
    }

    private void updateTopSongPlayState() {
        // printLog("updateTopSongPlayState mPlayStatus is :" + mPlayStatus);

        String songPlayState = "";
        PlayStatus playStatus = videoPlayerApp.getVideoPlayerContext().getPlayStatus();
        if (playStatus == PlayStatus.STATUS_STOP) {
            mSongFfInfo.speedType = 0;
        } else if (playStatus == PlayStatus.STATUS_PAUSE) {
            mSongFfInfo.speedType = 0;
        } else if ((playStatus == PlayStatus.STATUS_PLAY) ||
                (playStatus == PlayStatus.STATUS_FF) ||
                (playStatus == PlayStatus.STATUS_RW)) {
            printLog("updateTopSongPlayState mSongFfInfo.speedType is :" + mSongFfInfo.speedType);
            switch (mSongFfInfo.speedType) {
                case SONG_FF_SPEED_NORMAL:
                    songPlayState = "";
                    break;

                case SONG_FF_SPEED_2X:
                    songPlayState = mFFString + "2x";
                    break;

                case SONG_FF_SPEED_4X:
                    songPlayState = mFFString + "4x";
                    break;

                case SONG_FF_SPEED_8X:
                    songPlayState = mFFString + "8x";
                    break;

                case SONG_FF_SPEED_16X:
                    songPlayState = mFFString + "16x";
                    break;

                case SONG_FF_SPEED_32X:
                    songPlayState = mFFString + "32x";
                    break;

                case SONG_RW_SPEED_2X:
                    songPlayState = mRWString + "2x";
                    break;

                case SONG_RW_SPEED_4X:
                    songPlayState = mRWString + "4x";
                    break;

                case SONG_RW_SPEED_8X:
                    songPlayState = mRWString + "8x";
                    break;

                case SONG_RW_SPEED_16X:
                    songPlayState = mRWString + "16x";
                    break;

                case SONG_RW_SPEED_32X:
                    songPlayState = mRWString + "32x";
                    break;

                default:
                    songPlayState = "";
                    break;
            }

        }

        if (mTVSongPlayState != null) {
            mTVSongPlayState.setText(songPlayState);
        }
    }

    private void resetFFState() {
        int state = mVideoView.getMediaFFState();
        if (state != SONG_FF_SPEED_NORMAL) {
            mSongFfInfo.speedType = setFFState(SONG_FF_SPEED_NORMAL);
        } else {
            mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
        }
    }

    private int setFFState(int iSpeed) {
        mSongFfInfo.speedType = iSpeed;
        if (mVideoView.setMediaFFState(iSpeed) < 0) {
            // if setting ffrate fail
            mSongFfInfo.speedType = mVideoView.getMediaFFState();
            printLog("setFFState fail reback mSongFfInfo.speedType is " + mSongFfInfo.speedType);
        }
        return mSongFfInfo.speedType;
    }

    private void showErrToastAndPlayNext() {
        if (isResumed()) {
        	showToast();
        }
        showRearToast(R.string.error_message, Toast.LENGTH_LONG);

        // set the time to auto play next
        removePlayNextMessage();
        mHandler.sendEmptyMessageDelayed(HANDLEERROR_PLAYNEXT, AUTO_DISMISS_ERROR_TOAST_TIME);
    }

    private void removePlayNextMessage() {
        if (mSeekBar != null) {
            mSeekBar.setProgress(0);
        }
        mHandler.removeMessages(PROGRESS_CHANGED);
        mHandler.removeMessages(HANDLEERROR_PLAYNEXT);
    }

    private void onPreparedtoPlay() {
        // printLog("onPreparedtoPlay - mPlayStatus: " + mPlayStatus);
        updateDestAudio();

        mHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);

        initAllTrackInfo();

        int[] retval = new int[2];
        retval = mVideoView.getMaxRate();
        mSongFfInfo.mMaxFFRate = retval[0];
        mSongFfInfo.mMaxRWRate = retval[1];

        printLog("onPreparedtoPlay - mMaxFFRate: " + mSongFfInfo.mMaxFFRate);
        printLog("onPreparedtoPlay - mMaxFBRate: " + mSongFfInfo.mMaxRWRate);

        mMediaCapability = mVideoView.getMediaCapability();
        unSupportType = -1;
        if ((mMediaCapability & CAP_FILE_SEEK_UNSUPPORT) != 0) {
            printLog("onPreparedtoPlay this file do not support seek");
            unSupportType = 0;
            mCanSeek = false;
            mSeekBar.setEnabled(mCanSeek);
        }

        if ((mMediaCapability & CAP_FILE_FF_UNSUPPORT) != 0) {
            printLog("onPreparedtoPlay this file do not support FF");
            unSupportType = 1;
            mCanFFEx = false;
            // mCanFF = false;
            // mButtonFF.setEnabled(mCanFF);
        } else {
            mCanFFEx = true;
        }

        if ((mMediaCapability & CAP_FILE_RW_UNSUPPORT) != 0) {
            printLog("onPreparedtoPlay this file do not support RW");
            unSupportType = 2;
            mCanRWEx = false;
            // mCanRW = false;
            // mButtonRW.setEnabled(mCanRW);
        } else {
            mCanRWEx = true;
        }

        initDecodedInfo();

        try {
            if (!mNeedRestoreStreamIndex) {
                retval[0] = retval[1] = -1;
                retval = mVideoView.getDfltTrack();

                if (0 < mAudioChannelInfo.count) {
                    mAudioChannelInfo.index = retval[0];
                } else {
                    mAudioChannelInfo.index = -1;
                }
                if (0 < mSubtitleInfo.count) {
                    mSubtitleInfo.index = retval[1];
                } else {
                    mSubtitleInfo.index = -1;
                }
            } else {
                retval[0] = retval[1] = -1;
                retval = mVideoView.getDfltTrack();

                if (mSubtitleInfo.index == -1) {
                    if (retval[1] >= 0) {
                        mVideoView.hideSubtitle(retval[1]);
                    }
                } else {
                    mVideoView.setSubtitleIndex(mSubtitleInfo.index);
                }

                if (mAudioChannelInfo.index >= 0) {
                    mVideoView.setAudioChannelIndex(mAudioChannelInfo.index);
                }
                mNeedRestoreStreamIndex = false;
            }
            printLog("onPreparedtoPlay,  mAudioChannelInfo.index is " + mAudioChannelInfo.index);
            printLog("onPreparedtoPlay,  mSubtitleInfo.index is " + mSubtitleInfo.index);
        } catch (Exception e) {
            printLog("getting mVideoView.getSubtitleCurIdx Fail! ");
            mAudioChannelInfo.index = -1;
            mSubtitleInfo.index = -1;
        }

        // In DivXHT31 Spec, it is recommended that by default,
        // when playing back a clip with subtitle tracks, the subtitle be off.
        if (mIsSupportDivxHT311) {
            printLog("default set the subtitle off, mSubtitleInfo(index = " +
                    mSubtitleInfo.index + ", count = " + mSubtitleInfo.count + ")");
            if ((0 <= mSubtitleInfo.index) && (0 < mSubtitleInfo.count)) {
                try {
                    // mVideoView.hideSubtitle(mSubtitleInfo.index);
                    mSubtitleInfo.index = -1;
                } catch (Exception e) {
                    printLog("mVideoView.hideSubtitle error!");
                }
            }
        }

        if (mNeedRestoreFFState) {
            int state = mVideoView.getMediaFFState();
            setFFState(state);
            mNeedRestoreFFState = false;
        } else {
            resetFFState();
        }

        updateTopSongPlayState();

        updateVideoDuration();

        updatePauseButtonDisplay();

        if (mIsControllerShow) {
            showController();
        }

        int size = videoPlayerApp.getVideoPlayerContext().getListSize();
        // if (mPlayList != null && mPlayList.size() > 0 && mPosition != -1) {
        if (size > 0) {
            // String trackIndex = String.format("%02d/%02d", mPosition + 1, mPlayList.size());
            String trackIndex = String.format("%02d/%02d", videoPlayerApp.getVideoPlayerContext()
                    .getCurrentPos() + 1, size);
            mTVTrackIndex.setText(trackIndex);
            mTVTrackIndex.setVisibility(View.VISIBLE);
        }

    }

    protected Dialog onCreateDialog(int id) {
        printLog("onCreateDialog id is" + id);
        switch (id) {
            case 1:
                LayoutInflater factory1 = LayoutInflater.from(this);
                vAuthorizationErrorView = factory1.inflate(R.layout.authorization_error_control,
                        null);
                mAuthorizationErrorText = (TextView) vAuthorizationErrorView
                        .findViewById(R.id.authorization_error_info);
                mAuthorizationErrorOKButton = (Button) vAuthorizationErrorView
                        .findViewById(R.id.authorization_error_ok);

                if (mAuthorizationErrorOKButton == null) {
                    printLog("mAuthorizationErrorOKButton is null....");
                } else {
                    mAuthorizationErrorOKButton.setOnClickListener(mClickListener);
                    mAuthorizationErrorOKButton.setClickable(true);
                }

                mDialogAuthorizationError = new AlertDialog.Builder(this).setView(
                        vAuthorizationErrorView).create();
                mDialogAuthorizationError.setCancelable(false);
                mDialogAuthorizationError.show();
                break;

            case 2:
                LayoutInflater factory2 = LayoutInflater.from(this);
                vRentalExpiredView = factory2.inflate(R.layout.rental_expired_control, null);
                mRentalExpiredText1 = (TextView) vRentalExpiredView
                        .findViewById(R.id.rental_expired_info1);
                mRentalExpiredText2 = (TextView) vRentalExpiredView
                        .findViewById(R.id.rental_expired_info2);

                mRentalExpiredOKButton = (Button) vRentalExpiredView
                        .findViewById(R.id.rental_expired_ok);

                if (mRentalExpiredOKButton == null) {
                    printLog("mRentalExpiredOKButton is null....");
                } else {
                    mRentalExpiredOKButton.setOnClickListener(mClickListener);
                    mRentalExpiredOKButton.setClickable(true);
                }

                mDialogRentalExpired = new AlertDialog.Builder(this).setView(vRentalExpiredView)
                        .create();
                mDialogRentalExpired.setCancelable(false);
                mDialogRentalExpired.show();
                break;

            case 3:
                LayoutInflater factory3 = LayoutInflater.from(this);
                vRentalConfirmationChoiceView = factory3.inflate(
                        R.layout.rental_confirmation_choice_control, null);
                mRentalConfirmationChoiceText1 = (TextView) vRentalConfirmationChoiceView
                        .findViewById(R.id.rental_confirmation_choice_info1);
                mRentalConfirmationChoiceText2 = (TextView) vRentalConfirmationChoiceView
                        .findViewById(R.id.rental_confirmation_choice_info2);

                mRentalConfirmationChoiceOKButton = (Button) vRentalConfirmationChoiceView
                        .findViewById(R.id.rental_confirmation_choice_ok);
                mRentalConfirmationChoiceCancelButton = (Button) vRentalConfirmationChoiceView
                        .findViewById(R.id.rental_confirmation_choice_cancel);
                if (mRentalConfirmationChoiceOKButton == null) {
                    printLog("mRentalConfirmationChoiceOKButton is null....");
                } else {
                    mRentalConfirmationChoiceOKButton.setOnClickListener(mClickListener);
                    mRentalConfirmationChoiceOKButton.setClickable(true);
                }

                if (mRentalConfirmationChoiceCancelButton == null) {
                    printLog("mRentalConfirmationChoiceCancelButton is null....");
                } else {
                    mRentalConfirmationChoiceCancelButton.setOnClickListener(mClickListener);
                    mRentalConfirmationChoiceCancelButton.setClickable(true);
                }

                mDialogRentalConfirmationChoice = new AlertDialog.Builder(this).setView(
                        vRentalConfirmationChoiceView).create();
                mDialogRentalConfirmationChoice.setCancelable(false);
                mDialogRentalConfirmationChoice.show();
                break;

            default:
                break;
        }
        return null;
    }

    private String time2Str(int Time) {
        StringBuffer timeStr = new StringBuffer();

        if (Time <= 0) {
            timeStr.append("00:00:00");
        } else {
            timeStr.append(String.format("%02d", (((Time / 1000) / 60) / 60))); // hour
            timeStr.append(":");
            timeStr.append(String.format("%02d", (((Time / 1000) / 60) % 60))); // min
            timeStr.append(":");
            timeStr.append(String.format("%02d", ((Time / 1000) % 60))); // second
        }
        return timeStr.toString();
    }

    private void updateVideoDuration() {
        int DurationTmp = mVideoView.getDuration();
        if (DurationTmp <= 0) {
            mDuration = 0; // if getting duration fail ,set duration as zero
        } else {
            mDuration = DurationTmp;
        }
        printLog("getVideoDuration,getDuration=" + mDuration);
        if (mSeekBar != null) {
            mSeekBar.setMax(mDuration);
        }
        return;
    }

    private void getPlayListByPathList(List<FileInfo> list, ArrayList<String> pathList,
            int startIndex) {
        String s;
        if (list != null) {
            list.clear();
        }
        for (int i = startIndex; i < pathList.size(); i++) {
            s = pathList.get(i);

            File file = new File(s);
            FileInfo mi = new FileInfo();
            mi.setName(file.getName());
            mi.setPath(file.getAbsolutePath());
            list.add(mi);

            file = null;
            mi = null;
        }
    }
    
    private int getPlayListByPath(List<FileInfo> list,  String path, String playingPath) {
        String subPath;
        int ret = -1;
        if (list != null) {
            list.clear();
        }
        else{
        	return 0;
        }
        File folder = new File(path);
        if(folder!=null && folder.isDirectory()){
        	File files[] = folder.listFiles();
        	if(files !=null && files.length>0){
        		for(int i=0;i<files.length;i++){
        			if(files[i].isFile()){
	        			subPath = files[i].getAbsolutePath();        		
	        			MediaFile.MediaFileType mediaFileType = MediaFile.getFileType(subPath);
	                    if (mediaFileType != null) {
	                        int fileType = mediaFileType.fileType;
	                        if (MediaFile.isVideoFileType(fileType)
	                        		&& !subPath.substring(subPath.length()-4).equalsIgnoreCase(".dat")) {
	                            FileInfo mi = new FileInfo();
	                            mi.setName(files[i].getName());
	                            mi.setPath(subPath);
	                            list.add(mi);
	                			if(playingPath!=null && ret<0){
	                				if(playingPath.equals(subPath)){
	                					ret = i;
	                				}
	                			}
	                        }
	                    }
        			}
        		}
        	}
        }
        if(ret<0)ret = 0;
        return ret;
    }

    private int getIndexByPathStringFromPlayList(
            List<FileInfo> pathList, String path) {
        int index = 0;
        String s;
        for (int i = 0; i < pathList.size(); i++) {
            s = pathList.get(i).getPath();
            if (s.equals(path)) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    boolean fromFileBrowser = false;
	private OnAudioFocusChangeListener mAudioFocusListener;
	private void audioFocusFileBrowserVideo(boolean play){
		if(fromFileBrowser){
			if(play){
				VideoPlayerContext playerContext = VideoPlayerApp.GetInstance().getVideoPlayerContext();
		    	if(playerContext.isPausedByAudioFocus()){
		    		playerContext.setPausedByAudioFocus(false);
		    		doPlayPause(true);
		    	}
			}
			else{
				VideoPlayerContext playerContext = VideoPlayerApp.GetInstance().getVideoPlayerContext();
        		PlayStatus playStatus =playerContext.getPlayStatus();
        		if(playStatus == PlayStatus.STATUS_PLAY 
        				|| playStatus == PlayStatus.STATUS_FF
        				|| playStatus == PlayStatus.STATUS_RW){
        			doPlayPause(false);
        			playerContext.setPausedByAudioFocus(true);
        		}
			}
		}
	}

    private void handleIntentData() {
    	fromFileBrowser = false;
        mVideoPlaying = null;
        playUri = null;
        
        /*
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // from files browser
            ArrayList<String> revlist = bundle.getStringArrayList("PLAYLIST");
            if (null != revlist) {
                ArrayList<FileInfo> list = new ArrayList<FileInfo>();
                fromFileBrowser = true;
                int position = bundle.getInt("POSITION", -1);
                printLog("VideoPlaybackMainActivity - getIntentData - position: " + position);
                if (position != -1) {
                    // mVideoPlaying = revlist.get(position);
                    // playUri = Uri.parse(mVideoPlaying);
                    getPlayListByPathList(list, revlist, 0);
                    videoPlayerApp.getVideoPlayerContext().openFileList(list, position, false);
                    videoPlayerApp.getVideoPlayerContext().setCurrentPos(position);
                    mVideoPlaying = videoPlayerApp.getVideoPlayerContext().getCurrentTrackPath();
                    if (mVideoPlaying != null) {
                        playUri = Uri.parse(mVideoPlaying);
                    }
                } else {
                    getPlayListByPathList(list, revlist, 1);
                    int pos = getIndexByPathStringFromPlayList(list, revlist.get(0));
                    printLog("VideoPlaybackMainActivity - getIntentData - pos: " + pos);
                    // mVideoPlaying = revlist.get(0);
                    // playUri = Uri.parse(mVideoPlaying);
                    videoPlayerApp.getVideoPlayerContext().openFileList(list, pos, false);
                    videoPlayerApp.getVideoPlayerContext().setCurrentPos(pos);
                    mVideoPlaying = videoPlayerApp.getVideoPlayerContext().getCurrentTrackPath();
                    if (mVideoPlaying != null) {
                        playUri = Uri.parse(mVideoPlaying);
                    }
                }
                revlist = null;
            }
        }*/
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equalsIgnoreCase(getIntent().getAction())){
        	//play video from other app such as file browser.
        	fromFileBrowser = true;
        	mAudioFocusListener = new OnAudioFocusChangeListener() {

                @Override
                public void onAudioFocusChange(int focusChange) {
				
					switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                            //cancelAquireMediaKey(callback);
                        	finish();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        	audioFocusFileBrowserVideo(false);
                            break;

                        case AudioManager.AUDIOFOCUS_GAIN:
                        	audioFocusFileBrowserVideo(true);
                            break;
                    }
                }
            };
            mAudioManager.requestAudioFocus(mAudioFocusListener,
            		AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            
	   		 try{
		    		 String data = intent.getData().getPath();
		    		 String path = data.substring(0, data.lastIndexOf("/"));
		    		 String name = data.substring(data.lastIndexOf("/")+1);
		    		 Log.i(TAG, "loadData: path:"+path+ " name:" + name);
		    		 ArrayList<FileInfo> list = new ArrayList<FileInfo>();
		    		 int pos = getPlayListByPath(list, path, data);
		    		 if(list.size()>0){
		    			 videoPlayerApp.getVideoPlayerContext().openFileList(list, pos, false);
	                    videoPlayerApp.getVideoPlayerContext().setCurrentPos(pos);
	                    mVideoPlaying = videoPlayerApp.getVideoPlayerContext().getCurrentTrackPath();
	                    if (mVideoPlaying != null) {
	                        playUri = Uri.parse(mVideoPlaying);
	                    }
		    		 }
		    		 else{
		    			 
		    		 }		    		
	   		 }
	   		 catch(Exception e){
	   			 e.printStackTrace();
	   		 }
        }

        if (!fromFileBrowser) {
            // from play list.
            //Intent intent = getIntent();
            String listType;
            listType = intent.getStringExtra(VideoPlayerContext.OPEN_LIST);
            if (listType != null) {
                int pos = intent.getIntExtra(VideoPlayerContext.OPEN_POS, -1);
                if (pos >= 0) {
                    // list changed, need to open track now.
                    videoPlayerApp.getVideoPlayerContext().setCurrentPos(pos);
                    mVideoPlaying = videoPlayerApp.getVideoPlayerContext().getCurrentTrackPath();
                    if (mVideoPlaying != null) {
                        playUri = Uri.parse(mVideoPlaying);
                    }
                }
            }
            else {
                playUri = getIntent().getData();

                if (playUri != null) {
                    printLog("VideoPlaybackMainActivity - getIntentData - uri: "
                            + playUri.toString());
                } else {
                    printLog("VideoPlaybackMainActivity - getIntentData - uri is null");
                    ListItem listItem = videoPlayerApp.getVideoPlayerContext().getPlaying();
                    if(listItem!=null){
                    	playUri = Uri.parse(listItem.getPath());
                        if (mBtnOpPlay != null) {
                            setPauseButtonImageInt(false);
                        }
                    }                    
                }
            }
        }
        printLog("VideoPlaybackMainActivity - getIntentData - playback begin");
        mVideoView.stopPlayback();
        mTempPosition = videoPlayerApp.getVideoPlayerContext().getCurrentPos();
        mSeekBar.setEnabled(false);
        hideRearToast();
        if (playUri != null) {
            mVideoView.setVideoPath(playUri);
            mPlayingUsbVolume = getUsbVolume(playUri.getPath());
            mediaPlayStart();                    
            // mPlayStatus = PlayStatus.STATUS_PLAY;
            videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_PLAY);                   
            notifyPlayFileChanged();
        }
        else{
        	Log.w(TAG, "no video to play!!!");
        	finish();
        	return;
        }
        mNeedRestoreStreamIndex = false;
        mNeedRestoreFFState = false;
        mIsDivxDrm = false;
        updateTopDisplayInfo();
        // printLog("VideoPlaybackMainActivity - getIntentData - end");
    }

    void playPrev() {

        removePlayNextMessage();
        int size = videoPlayerApp.getVideoPlayerContext().getListSize();
        int pos = videoPlayerApp.getVideoPlayerContext().getCurrentPos();
        if (mShuffModel == SHUFFLE_NORMAL) {
            pos = (int) randomIndex.nextInt(size);
        } else if (mLoopModel == REPEAT_CURRENT) {
            pos = pos + 0;
        } else if (mLoopModel == REPEAT_ALL) {
            pos--;
        } else if (mLoopModel == REPEAT_NONE) {
            pos--;
            if (pos < 0) {
                pos = size - 1;
            }
        }

        if (pos < 0) {
            pos = size - 1;
        }
        videoPlayerApp.getVideoPlayerContext().setCurrentPos(pos);
        playMedia();

    }

    void playNext() {

        removePlayNextMessage();
        int size = videoPlayerApp.getVideoPlayerContext().getListSize();
        int pos = videoPlayerApp.getVideoPlayerContext().getCurrentPos();
        if (mShuffModel == SHUFFLE_NORMAL) {
            pos = (int) randomIndex.nextInt(size);
        } else if (mLoopModel == REPEAT_CURRENT) {
            pos = pos + 0;
        } else if (mLoopModel == REPEAT_ALL) {
            pos++;
        } else if (mLoopModel == REPEAT_NONE) {
            pos++;
            if (pos >= size) {
                pos = 0;
            }
        }

        if (pos >= size) {
            pos = 0;
        }

        videoPlayerApp.getVideoPlayerContext().setCurrentPos(pos);
        playMedia();

    }

    void playIndex(int index) {

        if (index < 0 ||
                index >= videoPlayerApp.getVideoPlayerContext().getListSize() ||
                videoPlayerApp.getVideoPlayerContext().getCurrentPos() == index) {
            return;
        }

        videoPlayerApp.getVideoPlayerContext().setCurrentPos(index);
        playMedia();

    }

    private void playMedia() {
    	String curPath = mVideoView.getVideoPath();
    	if(curPath!=null && curPath.length()>0){
    		if(mPlayedTime>=0){
    			videoPlayerApp.getVideoPlayerContext().savePlayed(curPath, mPlayedTime, mVideoView.getDuration());
    		}    		
    	}
        mIsOnline = false;
        mPrevBtnClickCnt = 0;
        mPlayedTime = -1;

        mSeekBar.setEnabled(false);

        printLog("mPosition: " + videoPlayerApp.getVideoPlayerContext().getCurrentPos());
        printLog("mTempPosition: " + mTempPosition);

        if (mIsSupportDivxHT311) {
            if (!mIsDivx) {
                printLog("playNext stopPlayback ");
                mVideoView.stopPlayback();
            }
        }

        try {
        	
            mVideoView.stopPlayback();
            if (mIsDivx) {
                printLog("playNext mVideoView.clearDivxService()");
                mVideoView.clearDivxService();
            }
            mIsDivxDrm = false;
            mVideoPlaying = videoPlayerApp.getVideoPlayerContext().getCurrentTrackPath();
            hideRearToast();
            // mVideoView.setVisibility(View.INVISIBLE);
            mVideoView.setVideoPath(mVideoPlaying);
            mPlayingUsbVolume = getUsbVolume(mVideoPlaying);
            mediaPlayStart();
            // mPlayStatus = PlayStatus.STATUS_PLAY;
            videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_PLAY);
            
            mNeedRestoreStreamIndex = false;
            mNeedRestoreFFState = false;
            mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
            //saveVideoPath();
            notifyPlayFileChanged();
        } catch (Exception e) {
            printLog("mVideoView.setVideoPath(mPlayList.get(mPosition).path) Error");
        }
        mTempPosition = videoPlayerApp.getVideoPlayerContext().getCurrentPos();

        updateTopDisplayInfo();

    }

    private void updatePauseButtonDisplay() {
        PlayStatus playStatus = videoPlayerApp.getVideoPlayerContext().getPlayStatus();
        printLog("updatePauseButtonDisplay: mPlayStatus = " + playStatus);
        if (playStatus == PlayStatus.STATUS_STOP) {
            setPauseButtonImageInt(false);
        } else {
            if (playStatus == PlayStatus.STATUS_PAUSE) {
                setPauseButtonImageInt(false);
            } else {
                setPauseButtonImageInt(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

            mVideoView.stopPlayback();

            int result = data.getIntExtra("CHOOSE", -1);
            printLog("" + result);
            if (result != -1) {
                mIsOnline = false;
                mIsDivxDrm = false;
                mVideoView.setVideoPath(videoPlayerApp.getVideoPlayerContext().getTrackPathByPos(
                        result));
                mNeedRestoreStreamIndex = false;
                mNeedRestoreFFState = false;
                mPlayingUsbVolume = getUsbVolume(videoPlayerApp.getVideoPlayerContext()
                        .getTrackPathByPos(result));
                videoPlayerApp.getVideoPlayerContext().setCurrentPos(result);
            } else {
                String url = data.getStringExtra("CHOOSE_URL");
                if (url != null) {
                    mIsDivxDrm = false;
                    mVideoView.setVideoPath(url);
                    mNeedRestoreStreamIndex = false;
                    mNeedRestoreFFState = false;
                    mPlayingUsbVolume = null;
                    mIsOnline = true;
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void resetLanguage(){
    	setLoopButtonImage();
    	setRandButtonImage();
    	setPauseButtonImage();
    	if(mBtnOpPre!=null){
    		mBtnOpPre.setText(R.string.str_btn_pre);
    	}
    	if(mBtnOpList!=null){
    		mBtnOpList.setText(R.string.str_btn_list);
    	}
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getScreenSize();
        if(newConfig.userSetLocale){
        	resetLanguage();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void getScreenSize() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        sScreenWidth = metric.widthPixels;
        sScreenHeight = metric.heightPixels;

        // printLog("VideoPlaybackMainActivity - getScreenSize - sScreenWidth: "
        // + sScreenWidth + ", sScreenHeight: " + sScreenHeight);
    }

    private void showController() {
        // mCurView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(params);
        // window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mBottomPanelView.setVisibility(View.VISIBLE);
        ivControlVideo.setVisibility(View.VISIBLE);
        mIsControllerShow = true;
    }

    private void hideController() {
        // mCurView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
        // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        PlayStatus playStatus = videoPlayerApp.getVideoPlayerContext().getPlayStatus();
        if (playStatus == PlayStatus.STATUS_STOP
                || playStatus == PlayStatus.STATUS_NONE
                || playStatus == PlayStatus.STATUS_PAUSE) {
            return;
        }
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(params);
        // window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mBottomPanelView.setVisibility(View.INVISIBLE);
        ivControlVideo.setVisibility(View.INVISIBLE);
        mIsControllerShow = false;
    }

    public void cancelDelayHide() {
        mHandler.removeMessages(HIDE_CONTROLER);
        mHandler.removeMessages(GO_HOME);
    }

    public void hideControllerDelay() {
        if (IS_AUTO_HIDE_PANEL) {
        	mHandler.removeMessages(HIDE_CONTROLER);
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, HIDEDELAYTIME);
        }
    }

    public void setVideoDisplayRect(int videoWidth, int videoHeight) {
        int mWidth = sScreenWidth;
        int mHeight = sScreenHeight;
        printLog("setVideoDisplayRect -- mWidth = " + mWidth + ", mHeight = " + mHeight);
        printLog("setVideoDisplayRect -- width = " + videoWidth + ", height = " + videoHeight);

        if ((0 == videoWidth) || (0 == videoHeight) || (0 == sScreenWidth) || (0 == sScreenHeight)) {
            return;
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (((videoWidth != sScreenWidth) || (videoHeight != sScreenHeight)) && (0 < videoWidth)
                && (0 < videoHeight) && (0 < sScreenWidth) &&
                (0 < sScreenHeight)) {
            Rect rDstRect = new Rect(0, 0, sScreenWidth, sScreenHeight);
            Rect rDisplayRect = new Rect(0, 0, sScreenWidth, sScreenHeight);

            double dRatio = 0;
            Rect rSrcRect = new Rect(0, 0, videoWidth, videoHeight);
            printLog("setVideoDisplayRect -- rDisplayRect (left: " + rDisplayRect.left +
                    " right: " + rDisplayRect.right + " top: " + rDisplayRect.top +
                    " bottom: " + rDisplayRect.bottom);
            printLog("setVideoDisplayRect -- rSrcRect (left: " + rSrcRect.left +
                    " right: " + rSrcRect.right + " top: " + rSrcRect.top +
                    " bottom: " + rSrcRect.bottom);
            printLog("setVideoDisplayRect -- rDstRect (left: " + rDstRect.left +
                    " right: " + rDstRect.right + " top: " + rDstRect.top +
                    " bottom: " + rDstRect.bottom);

            dRatio = (double) ((rDisplayRect.right - rDisplayRect.left) * (rSrcRect.bottom - rSrcRect.top))
                    /
                    ((rDisplayRect.bottom - rDisplayRect.top) * (rSrcRect.right - rSrcRect.left));
            printLog("setVideoDisplayRect -- dRatio = " + dRatio);

            if (dRatio > 1.0) {
                rDstRect.left = (int) ((rDisplayRect.right - rDisplayRect.left) * (dRatio - 1) / (2 * dRatio));
                rDstRect.right = (int) ((rDisplayRect.right - rDisplayRect.left) * (dRatio + 1) / (2 * dRatio));
                rDstRect.top = rDisplayRect.top;
                rDstRect.bottom = rDisplayRect.bottom;
            } else {
                rDstRect.left = rDisplayRect.left;
                rDstRect.right = rDisplayRect.right;
                rDstRect.top = (int) ((rDisplayRect.bottom - rDisplayRect.top) * (1 - dRatio) / 2);
                rDstRect.bottom = (int) ((rDisplayRect.bottom - rDisplayRect.top) * (1 + dRatio) / 2);
            }

            mWidth = rDstRect.right - rDstRect.left;
            mHeight = rDstRect.bottom - rDstRect.top;

            printLog("setVideoDisplayRect -- after compute, (mWidth=" + mWidth +
                    " mHeight=" + mHeight + ")" + ", rDstRect(left: " + rDstRect.left +
                    " right: " + rDstRect.right + " top: " + rDstRect.top +
                    " bottom: " + rDstRect.bottom + ")");

            double phywidth = 16.0;
            double phyheight = 9.0;
            if (phywidth * 600 < phyheight * 1024) {
                mHeight = (int) ((mHeight * phywidth * 600) / (phyheight * 1024));
            } else {
                mWidth = (int) ((mWidth * phyheight * 1024) / (phywidth * 600));
            }

            rDstRect.left = (sScreenWidth - mWidth) / 2;
            rDstRect.top = (sScreenHeight - mHeight) / 2;
            rDstRect.right = rDstRect.left + mWidth;
            rDstRect.bottom = rDstRect.top + mHeight;

            printLog("setVideoDisplayRect -- after compute 2, (mWidth=" + mWidth +
                    " mHeight=" + mHeight + ")" + ", rDstRect(left: " + rDstRect.left +
                    " right: " + rDstRect.right + " top: " + rDstRect.top +
                    " bottom: " + rDstRect.bottom + ")");

            // mVideoView.setAnimatiable(false); // only when release DivXHT31
            // Image, open this code

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    mWidth, mHeight);
            lp.width = mWidth;
            lp.height = mHeight;

            // lp.addRule(RelativeLayout.CENTER_IN_PARENT);

            printLog("setVideoDisplayRect -- setMargins (left: " + rDstRect.left +
                    " top: " + rDstRect.top + ")");
            lp.setMargins(rDstRect.left, rDstRect.top, 0, 0);

            mVideoView.setLayoutParams(lp);
        }

        sVideoWidth = videoWidth;
        sVideoHeight = videoHeight;

        // if (false) {
        // if ((null != reardisppresentation) && (null !=
        // reardisppresentation.mRearVideoView) &&
        // (sVideoWidth != 0) && (sVideoHeight != 0))
        // {
        // reardisppresentation.setVideoDisplayRect(videoWidth, videoHeight);
        // }
        // }
    }

    private void setVideoScale(int flag) {
        mVideoView.getLayoutParams();

        switch (flag) {
            case SCREEN_FULL:
                // printLog("VideoPlaybackMainActivity - setVideoScale(SCREEN_FULL) -- screenWidth: "
                // + sScreenWidth + " screenHeight: " + sScreenHeight);

                mVideoView.setVideoScale(sScreenWidth, sScreenHeight);
                // mVideoView.setVideoScale(sScreenWidth, FULL_SCREEN_HEIGHT);
                break;

            case SCREEN_DEFAULT:
                int videoWidth = mVideoView.getVideoWidth();
                int videoHeight = mVideoView.getVideoHeight();
                int mWidth = sScreenWidth;
                int mHeight = sScreenHeight - 25;

                if (videoWidth > 0 && videoHeight > 0) {
                    if (videoWidth * mHeight > mWidth * videoHeight) {
                        // printLog("image too tall, correcting");
                        mHeight = mWidth * videoHeight / videoWidth;
                    } else if (videoWidth * mHeight < mWidth * videoHeight) {
                        // printLog("image too wide, correcting");
                        mWidth = mHeight * videoWidth / videoHeight;
                    } else {

                    }
                }

                mVideoView.setVideoScale(mWidth, mHeight);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
        }
    }

    private void initAllTrackInfo() {
        try {
            int[] ret = mVideoView.getAllTrackCount();
            mAudioChannelInfo.count = ret[0];
            mSubtitleInfo.count = ret[1];
        } catch (Exception e) {
            printLog("getAllTrackCountAndIdx error");
        }

        printLog("initAllTrackInfo mAudioChannelInfo.count=" + mAudioChannelInfo.count);
        printLog("initAllTrackInfo mSubtitleInfo.count=" + mSubtitleInfo.count);
    }

    void initDecodedInfo() {
        if (1 == mConfDisplayDecoding) {
            int ret = mVideoView.IsHWDecoded();
            if (ret == 0) {
                mIsHWDecoded = false;
            } else {
                mIsHWDecoded = true;
            }
        }
    }

    void exitApp() {
        hideRearToast();

        if (mIsDivx) {
            printLog("exitApp mVideoView.clearDivxService()");
            mVideoView.clearDivxService();
        }
        printLog("exitApp stopPlayback ");
        mVideoView.stopPlayback();

        if (reardisppresentation != null) {
            mFrontRearDisplayStatus = FrontRearDisplayStatus.FRONT_DISP;
            hideRearDisplay();
            updateDestAudio();
        }
        videoPlayerApp.getVideoPlayerContext().removePlaying();
        SourceManager.hotPlugSource(this, SourceManager.SRC_NO.video, "", false);
        // VideoPlaybackMainActivity.this.finish(); // exit the application
        VideoPlayerApp.exitAllActivity();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            hideRearToast();
            // mVideoView.stopPlayback();
            // finish();
            onBackPressed();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_YECON_FASTF) {
            if (!mIsControllerShow) {
                showController();
            }

            cancelDelayHide();
            hideControllerDelay();
        } else if (keyCode == KeyEvent.KEYCODE_YECON_FASTB) {
            if (!mIsControllerShow) {
                showController();
            }

            cancelDelayHide();
            hideControllerDelay();
        }
        return super.onKeyUp(keyCode, event);
    }

    private int getTheHWDisplayParam() {
        SharedPreferences mSharedPreferences = getSharedPreferences("SHOWDECODING", MODE_PRIVATE);
        int whethershowdecoding = mSharedPreferences.getInt("showdecoding", 0);
        return whethershowdecoding;
    }

    private boolean refreshNow() {
        try {
            if (videoPlayerApp.getVideoPlayerContext().getPlayStatus() != PlayStatus.STATUS_STOP) {
                // if (mVideoView.isPlaying()) {
                tempPlayedTime = mVideoView.getCurrentPosition();
                if (tempPlayedTime < 0) {
                    printLog("refreshNow - get getCurrentPosition fail");
                }
                mPlayedTime = tempPlayedTime;
                // }
                mPlayedTime = mProgress < 0 ? mPlayedTime : mProgress;
            } else {
                //mPlayedTime = 0;
            }
            
            if(mDuration > 0){
            	totalTimeStr = time2Str(mDuration);
            }
            else{
            	 totalTimeStr = "00:00:00";
            }
            if (mPlayedTime >= 0) {
                mPlayProgressDisp = mPlayedTime;
                currentTimeStr = time2Str(mPlayedTime);
            } else {
                mPlayProgressDisp = 0;               
                currentTimeStr = "00:00:00";
            }

            if (mIsOnline) {
                int j = mVideoView.getBufferPercentage();
                mPlayProgressSecondDisp = j * mSeekBar.getMax() / 100;
            } else {
                mPlayProgressSecondDisp = 0;
            }

            if (mSeekBar != null) {
                if (mIsOnline) {
                    mSeekBar.setSecondaryProgress(mPlayProgressSecondDisp);
                }
                mSeekBar.setProgress(mPlayProgressDisp);
            }
            if (mTVProcessTime != null && mTVTotalTime != null) {
                mTVProcessTime.setText(currentTimeStr);
                mTVTotalTime.setText(totalTimeStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            printLog("refreshNow get exception!!!!");
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_op_loop:
                doLoop();
                break;

            case R.id.btn_op_rand:
                doRand();
                break;

            case R.id.btn_op_pre:
                doPre();
                break;

            case R.id.btn_op_next:
                doNext();
                break;

            case R.id.btn_op_list:
                gotoListView();
                break;

            case R.id.iv_control_video:
                YeconSettings.callVideoRgbSettingActivity(this, YeconSettings.RGBTYPE.USB);
                break;
        }
    }
    
    
    @Override
    public void onBackPressed() {
        printLog("VideoPlaybackMainActivity - onBackPressed");
        gotoListView();
        //super.onBackPressed();
    }

    private void gotoListView() {
    	 hideRearToast();
    	 if(!fromFileBrowser){
    		 Intent intent = new Intent(this,VideoPlaybackListActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
             intent.putExtra(VideoConstant.ENTER_FOLDER, "false");
             startActivity(intent);
    	 }
    	 else{
    		 finish();
    	 }
    }

    private void doLoop() {
        if (mLoopModel == REPEAT_NONE) {
            mLoopModel = REPEAT_ALL;
        } else if (mLoopModel == REPEAT_ALL) {
            mLoopModel = REPEAT_CURRENT;
            if (mShuffModel == SHUFFLE_NORMAL) {
                mShuffModel = SHUFFLE_NONE;
                setRandButtonImage();
            }
        } else if (mLoopModel == REPEAT_CURRENT) {
            mLoopModel = REPEAT_NONE;
        }

        setLoopButtonImage();
    }

    private void doRand() {
        if (mShuffModel == SHUFFLE_NONE) {
            mShuffModel = SHUFFLE_NORMAL;
            if (mLoopModel == REPEAT_CURRENT) {
                mLoopModel = REPEAT_ALL;
                setLoopButtonImage();
            }
        } else if (mShuffModel == SHUFFLE_NORMAL) {
            mShuffModel = SHUFFLE_NONE;
        }

        setRandButtonImage();
    }

    private void setRandButtonImage() {
        if (mShuffModel == SHUFFLE_NONE) {
            Drawable drawable = getResources().getDrawable(R.drawable.btn_rand_off_bg);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                    .getMinimumHeight());
            mBtnOpRand.setCompoundDrawables(null, drawable, null, null);
            mBtnOpRand.setText(R.string.str_btn_rand_off);
        } else if (mShuffModel == SHUFFLE_NORMAL) {
            Drawable drawable = getResources().getDrawable(R.drawable.btn_rand_bg);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                    .getMinimumHeight());
            mBtnOpRand.setCompoundDrawables(null, drawable, null, null);
            mBtnOpRand.setText(R.string.str_btn_rand);
        }
    }

    private void setLoopButtonImage() {
        if (mLoopModel == REPEAT_NONE) {
            Drawable drawable = getResources().getDrawable(R.drawable.btn_loop_off_bg);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                    .getMinimumHeight());
            mBtnOpLoop.setCompoundDrawables(null, drawable, null, null);
            mBtnOpLoop.setText(R.string.str_btn_loop_off);
        } else if (mLoopModel == REPEAT_ALL) {
            Drawable drawable = getResources().getDrawable(R.drawable.btn_loop_bg);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                    .getMinimumHeight());
            mBtnOpLoop.setCompoundDrawables(null, drawable, null, null);
            mBtnOpLoop.setText(R.string.str_btn_loop);
        } else if (mLoopModel == REPEAT_CURRENT) {
            Drawable drawable = getResources().getDrawable(R.drawable.btn_loop_current_bg);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                    .getMinimumHeight());
            mBtnOpLoop.setCompoundDrawables(null, drawable, null, null);
            mBtnOpLoop.setText(R.string.str_btn_loop_current);
        }
    }

    public void doStop() {
        if (isRDSActive()) {
            return;
        }
        printLog("VideoPlaybackMainActivity - doStop");
        if (mIsDivx) {
            printLog("mButtonStop mVideoView.clearDivxService()");
            mVideoView.clearDivxService();
        }

        if (mIsSupportDivxHT311) {
            if ((0 == mDivxStopClickTimes)
                    && (PlayStatus.STATUS_PLAY == videoPlayerApp.getVideoPlayerContext()
                            .getPlayStatus())) {
                mDivxStopClickTimes++;
                printLog("initVideoView : mDivxStopClickTimes" + mDivxStopClickTimes);
                return; // first stop as pause method
            } else {
                mDivxStopClickTimes = 0;
                // to do realy stop
            }
        }
        if(mVideoPlaying!=null && mVideoPlaying.length()>0){
        	if(mPlayedTime>=0)
        		videoPlayerApp.getVideoPlayerContext().savePlaying(mVideoPlaying, mPlayedTime, mVideoView.getDuration());
        }
        // mVideoView.seekTo(0);
        hideRearToast();
        mVideoView.stopPlayback();
        // mVideoView.setVisibility(View.INVISIBLE);
        // mPlayStatus = PlayStatus.STATUS_STOP;
        videoPlayerApp.getVideoPlayerContext().setPlayStatus(PlayStatus.STATUS_STOP);
        updateTopSongPlayState();
        updatePauseButtonDisplay();

        mSeekBar.setEnabled(false);
        mBtnOpPlay.setEnabled(true);

        mSeekBar.setProgress(0);
        //mPlayedTime = 0;
        printLog("onClick mButtonStop send PROGRESS_CHANGED!!!");
        mHandler.sendEmptyMessage(PROGRESS_CHANGED);

        //saveVideoPath();

    }

    private void doPre() {
        if (isRDSActive()) {
            return;
        }
        printLog("onClick mButtonPrev -- mPrevBtnClickCnt = " + mPrevBtnClickCnt +
                "mIsSupportDivxHT311 = " + mIsSupportDivxHT311);
        if (mIsSupportDivxHT311 && (mPrevBtnClickCnt < 1)) {
            printLog("onClick mButtonPrev -- mHandler.sendEmptyMessageDelayed(SEEK_TO_FILESTART, 500)!!!");
            mPrevBtnClickCnt++;
            mHandler.sendEmptyMessageDelayed(SEEK_TO_FILESTART, 500);
        } else {
            printLog("onClick mButtonPrev -- playPrev()!!!");
            mPrevBtnClickCnt = 0;
            mHandler.removeMessages(SEEK_TO_FILESTART);
            playPrev();
        }
    }

    private void doNext() {
        if (isRDSActive()) {
            return;
        }
        clickcount++;
        printLog("OnClick mButtonNext" + clickcount);
        playNext();
    }

    private void doRewind() {
        if (isRDSActive()) {
            return;
        }
        printLog("onClick mButtonRW!!!");
        if (unSupportType == 2) {
            Toast.makeText(VideoPlaybackMainActivity.this, R.string.not_support_rw,
                    Toast.LENGTH_SHORT).show();
            showRearToast(R.string.not_support_rw, Toast.LENGTH_SHORT);
            return;
        }
        PlayStatus playStatus = videoPlayerApp.getVideoPlayerContext().getPlayStatus();
        if ((playStatus != PlayStatus.STATUS_PLAY) && (playStatus != PlayStatus.STATUS_FF)
                && (playStatus != PlayStatus.STATUS_RW)) {
            return;
        }

        mSongFfInfo.speedTypeBkp = mSongFfInfo.speedType;
        if (mSongFfInfo.speedType <= SONG_RW_SPEED_2X) {
            mPlayStatusTmp = PlayStatus.STATUS_RW;
            mSongFfInfo.speedType = (mSongFfInfo.speedType << 1);

            if ((mSongFfInfo.speedType > mSongFfInfo.mMaxFFRate)
                    || (mSongFfInfo.speedType < mSongFfInfo.mMaxRWRate)) {
                mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
                mPlayStatusTmp = PlayStatus.STATUS_PLAY;
            }
        } else {
            mSongFfInfo.speedType = SONG_RW_SPEED_2X;
            mPlayStatusTmp = PlayStatus.STATUS_RW;
        }

        mSongFfInfo.speedType = setFFState(mSongFfInfo.speedType);

        if (mSongFfInfo.speedType != mSongFfInfo.speedTypeBkp) {
            // if switch play status success ,chang actual play status.
            // mPlayStatus = mPlayStatusTmp;
            videoPlayerApp.getVideoPlayerContext().setPlayStatus(mPlayStatusTmp);
        }
        printLog("mButtonRW mSongFfInfo.speedType is :" + mSongFfInfo.speedType);
        updateTopSongPlayState();
    }

    private void doFastForward() {
        if (isRDSActive()) {
            return;
        }
        printLog("onClick mButtonFF!!!");
        if (!mCanFFEx) {
            Toast.makeText(VideoPlaybackMainActivity.this, R.string.not_support_ff,
                    Toast.LENGTH_SHORT).show();
            showRearToast(R.string.not_support_ff, Toast.LENGTH_SHORT);
            return;
        }
        PlayStatus playStatus = videoPlayerApp.getVideoPlayerContext().getPlayStatus();
        if ((playStatus != PlayStatus.STATUS_PLAY) && (playStatus != PlayStatus.STATUS_FF)
                && (playStatus != PlayStatus.STATUS_RW)) {
            return;
        }

        mSongFfInfo.speedTypeBkp = mSongFfInfo.speedType;
        if (mSongFfInfo.speedType >= SONG_FF_SPEED_2X) {
            mPlayStatusTmp = PlayStatus.STATUS_FF;
            mSongFfInfo.speedType = (mSongFfInfo.speedType << 1);

            if (mSongFfInfo.speedType > mSongFfInfo.mMaxFFRate) {
                mSongFfInfo.speedType = SONG_FF_SPEED_NORMAL;
                mPlayStatusTmp = PlayStatus.STATUS_PLAY;
            }
        } else {
            mSongFfInfo.speedType = SONG_FF_SPEED_2X;
            mPlayStatusTmp = PlayStatus.STATUS_FF;
        }

        mSongFfInfo.speedType = setFFState(mSongFfInfo.speedType);

        if (mSongFfInfo.speedType != mSongFfInfo.speedTypeBkp) {
            // if switch play status success ,chang actual play status.
            // mPlayStatus = mPlayStatusTmp;
            videoPlayerApp.getVideoPlayerContext().setPlayStatus(mPlayStatusTmp);
        }
        printLog("mButtonFF mSongFfInfo.speedType is :" + mSongFfInfo.speedType);
        updateTopSongPlayState();
    }

    void displayParking() {

        if (mParkingEnable && !mMcuIsParking) {
            mParkingLayout.setVisibility(View.VISIBLE);
        } else {
            mParkingLayout.setVisibility(View.INVISIBLE);
        }

    }
    
    private void notifyPlayFileChanged() {
        this.sendBroadcast(new Intent(VideoPlayerContext.ACTION_PLAY_FILE_CHANGED));
    }

    public final class RearDispPresentation extends Presentation {
        public RearVideoView mRearVideoView = null;

        public TextView mRearAuthorizationErrorText = null;
        public TextView mRearRentalExpiredText1 = null;
        public TextView mRearRentalExpiredText2 = null;
        public TextView mRearRentalConfirmationChoiceText1 = null;
        public TextView mRearRentalConfirmationChoiceText2 = null;
        public AlertDialog mRearDialogAuthorizationError = null;
        public AlertDialog mRearDialogRentalExpired = null;
        public AlertDialog mRearDialogRentalConfirmationChoice = null;
        public View vRearAuthorizationErrorView = null;
        public View vRearRentalExpiredView = null;
        public View vRearRentalConfirmationChoiceView = null;

        public RearDispPresentation(Context context, Display display) {
            super(context, display);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Window win = getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            params.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 26;

            setContentView(R.layout.main_rearview);

            mRearVideoView = (RearVideoView) findViewById(R.id.vv_rear);
            // printLog("onCreate rear: " + mRearVideoView);

            if ((mVideoView.getVideoWidth() != 0) && (mVideoView.getVideoHeight() != 0)) {
                mRearVideoView.getHolder().setFixedSize(mVideoView.getVideoWidth(),
                        mVideoView.getVideoHeight());
            }

            if (mRearVideoView != null) {
                mRearVideoView.setBackgroundColor(0x00000000);
                mRearVideoView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onStart() {
            // printLog("RearDispPresentation onStart!!!");
            updateTopDisplayInfo();
            super.onStart();
        }

        @Override
        protected void onStop() {
            printLog("RearDispPresentation onStop!!!");
            super.onStop();
        }

        @SuppressWarnings({
                "unused", "deprecation"
        })
        public void setVideoDisplayRect(int videoWidth, int videoHeight) {
            int sScreenHeight = getDisplay().getHeight();
            int sScreenWidth = getDisplay().getWidth();

            int mWidth = sScreenWidth;
            int mHeight = sScreenHeight;

            printLog("setVideoDisplayRect(rear) -- sScreenWidth = " + sScreenWidth
                    + ", sScreenHeight = " + sScreenHeight);
            printLog("setVideoDisplayRect(rear) -- width = " + videoWidth + ", height = "
                    + videoHeight);

            if ((0 == videoWidth) ||
                    (0 == videoHeight) ||
                    (0 == sScreenWidth) ||
                    (0 == sScreenHeight)) {
                return;
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (((videoWidth != sScreenWidth) || (videoHeight != sScreenHeight)) &&
                    (0 < videoWidth) &&
                    (0 < videoHeight) &&
                    (0 < sScreenWidth) &&
                    (0 < sScreenHeight)) {

                // Rect rDisplayRect = new Rect(0, 0, sScreenWidth,
                // sScreenHeight);
                // Rect rSrcRect = new Rect(0, 0, videoWidth, videoHeight);
                Rect rDstRect = new Rect(0, 0, sScreenWidth, sScreenHeight);

                if (videoWidth * 3 > 4 * videoHeight) {
                    rDstRect.right = sScreenWidth;
                    rDstRect.bottom = (videoHeight * sScreenHeight * 4) / (3 * videoWidth);
                } else {
                    rDstRect.right = (videoWidth * 3 * sScreenWidth) / (videoHeight * 4);
                    rDstRect.bottom = sScreenHeight;
                }

                int DstW = rDstRect.right;
                int DstH = rDstRect.bottom;

                printLog("setVideoDisplayRect(rear) -- DstW : " + DstW +
                        " DstH: " + DstH + ")");

                rDstRect.left = (sScreenWidth - DstW) / 2;
                rDstRect.top = (sScreenHeight - DstH) / 2;

                rDstRect.right = rDstRect.left + DstW;
                rDstRect.bottom = rDstRect.top + DstH;

                mWidth = rDstRect.right - rDstRect.left;
                mHeight = rDstRect.bottom - rDstRect.top;

                // mRearVideoView.setAnimatiable(false); // only when release
                // DivXHT31 Image, open this code

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        mWidth, mHeight);
                lp.width = mWidth;
                lp.height = mHeight;

                // lp.addRule(RelativeLayout.CENTER_IN_PARENT);

                printLog("setVideoDisplayRect(rear) -- setMargins (left: " + rDstRect.left +
                        " top: " + rDstRect.top + ")");
                lp.setMargins(rDstRect.left, rDstRect.top, 0, 0);

                mRearVideoView.setLayoutParams(lp);
            }
        }
        
        public SurfaceHolder getVideoViewHolder(){
        	if(mRearVideoView!=null){
        		return mRearVideoView.getHolder();
        	}
        	return null;
        }
        
        public void showAuthorizationErrorDialog() {
            if (null != mRearDialogAuthorizationError) {
                mRearDialogAuthorizationError.setCancelable(false);
                mRearDialogAuthorizationError.show();
                return;
            }
            vRearAuthorizationErrorView = getLayoutInflater().inflate(
                    R.layout.rear_authorization_error_control, null);
            if (null == vRearAuthorizationErrorView) {
                return;
            }
            mRearAuthorizationErrorText = (TextView) vRearAuthorizationErrorView
                    .findViewById(R.id.rear_authorization_error_info);
            if (null == mRearAuthorizationErrorText) {
                return;
            }

            mRearDialogAuthorizationError = new AlertDialog.Builder(getContext()).setView(
                    vRearAuthorizationErrorView).create();
            if (null == mRearDialogAuthorizationError) {
                return;
            }

            Window win = mRearDialogAuthorizationError.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            params.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 26;

            mRearDialogAuthorizationError.setCancelable(false);
            mRearDialogAuthorizationError.show();
        }

        public void showRentalExpiredDialog() {
            if (null != mRearDialogRentalExpired) {
                mRearDialogRentalExpired.setCancelable(false);
                mRearDialogRentalExpired.show();
                return;
            }
            vRearRentalExpiredView = getLayoutInflater().inflate(
                    R.layout.rear_rental_expired_control, null);
            if (null == vRearRentalExpiredView) {
                return;
            }

            mRearRentalExpiredText1 = (TextView) vRearRentalExpiredView
                    .findViewById(R.id.rear_rental_expired_info1);
            mRearRentalExpiredText2 = (TextView) vRearRentalExpiredView
                    .findViewById(R.id.rear_rental_expired_info2);
            if ((null == mRearRentalExpiredText1) || (null == mRearRentalExpiredText2)) {
                return;
            }
            mRearDialogRentalExpired = new AlertDialog.Builder(getContext()).setView(
                    vRearRentalExpiredView).create();
            if (null == mRearDialogRentalExpired) {
                return;
            }

            Window win = mRearDialogRentalExpired.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            params.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 26;

            mRearDialogRentalExpired.setCancelable(false);
            mRearDialogRentalExpired.show();
        }

        public void showRentalConfirmationDialog() {
            if (null != mRearDialogRentalConfirmationChoice) {
                mRearDialogRentalConfirmationChoice.setCancelable(false);
                mRearDialogRentalConfirmationChoice.show();
                return;
            }
            vRearRentalConfirmationChoiceView = getLayoutInflater().inflate(
                    R.layout.rear_rental_confirmation_choice_control, null);
            if (null == vRearRentalConfirmationChoiceView) {
                printLog("showRentalConfirmationDialog(rear) vRearRentalConfirmationChoiceView == null");
                return;
            }
            mRearRentalConfirmationChoiceText1 = (TextView) vRearRentalConfirmationChoiceView
                    .findViewById(R.id.rear_rental_confirmation_choice_info1);
            mRearRentalConfirmationChoiceText2 = (TextView) vRearRentalConfirmationChoiceView
                    .findViewById(R.id.rear_rental_confirmation_choice_info2);
            if ((null == mRearRentalConfirmationChoiceText1)
                    || (null == mRearRentalConfirmationChoiceText2)) {
                printLog("showRentalConfirmationDialog(rear) mRearRentalConfirmationChoiceText1 == null");
                return;
            }

            mRearDialogRentalConfirmationChoice = new AlertDialog.Builder(getContext()).setView(
                    vRearRentalConfirmationChoiceView).create();
            if (null == mRearDialogRentalConfirmationChoice) {
                printLog("showRentalConfirmationDialog(rear) mRearDialogRentalConfirmationChoice == null");
                return;
            }

            Window win = mRearDialogRentalConfirmationChoice.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            params.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 26;

            mRearDialogRentalConfirmationChoice.setCancelable(false);
            mRearDialogRentalConfirmationChoice.show();
        }

    }

}
