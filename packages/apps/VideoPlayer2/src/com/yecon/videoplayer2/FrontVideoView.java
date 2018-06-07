/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.yecon.videoplayer2;

import android.annotation.SuppressLint;
import android.app.Presentation;
import android.constant.YeconConstants;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.TimedText;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Display;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Color;

import java.io.IOException;

import com.autochips.media.*;

import com.yecon.common.SourceManager;
import com.yecon.videoplayer2.VideoPlaybackMainActivity;
import com.yecon.videoplayer2.VideoPlaybackMainActivity.RearDispPresentation;

import static com.yecon.videoplayer2.VideoPlaybackConstant.*;
import static android.constant.YeconConstants.*;

/**
 * Displays a video file. The FrontVideoView class can load images from various sources (such as
 * resources or content providers), takes care of computing its measurement from the video so that
 * it can be used in any layout manager, and provides various display options such as scaling and
 * tinting.
 */
public class FrontVideoView extends SurfaceView implements MediaPlayerControl {
    private String TAG = "FrontVideoView";
    public static final int INVOKE_ID_GET_TRACK_INFO = 1;
    public static final int INVOKE_ID_ADD_EXTERNAL_SOURCE = 2;
    public static final int INVOKE_ID_ADD_EXTERNAL_SOURCE_FD = 3;
    public static final int INVOKE_ID_SELECT_TRACK = 4;
    public static final int INVOKE_ID_DESELECT_TRACK = 5;
    public static final int INVOKE_ID_SET_VIDEO_SCALE_MODE = 6;
    public static final int INVOKE_ID_DIVX_MENU_DO_ACTION = 7;
    public static final int INVOKE_ID_DIVX_DRM_GET_INFO = 8;

    public static final int MEDIA_TRACK_TYPE_UNKNOWN = 0;
    public static final int MEDIA_TRACK_TYPE_VIDEO = 1;
    public static final int MEDIA_TRACK_TYPE_AUDIO = 2;
    public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = 3;

    public static final int KEY_PARAMETER_PLAYBACK_RATE_PERMILLE = 1300;
    public static final int KEY_PARAMETER_DIVX_SERVICE_RESET = 1400;
    public static final int KEY_PARAMETER_MEDIA_CAPABILITY = 1500;
    public static final int KEY_PARAMETER_MAXRATE_SUPPORTED = 1600;
    public static final int KEY_PARAMETER_DEFAULT_AUDSUB_INDEX = 1700; // reading
                                                                       // only
    public static final int KEY_PARAMETER_SET_VIDEO_VISIABLE = 1900;

    public static final int KEY_PARAMETER_GET_LOADING_STATUS = 2100;
    
    private RearDispPresentation presentation=null;
    public void setRearDispPresentation(RearDispPresentation presentation){
    	this.presentation = presentation;
    }
    // private final static int VIDEOSRC_START = 10;
    // private final static int AUDIOSRC_START = 20;
    // private final static int DEST_START = 0;
    // private final static int SETAVREADY = 100;

    public enum SubTitleType {
        SUBTITLE_TYPE_UNKNOWN, SUBTITLE_TYPE_TEXT, SUBTITLE_TYPE_PIC,
    };

    // all possible internal states
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    public int mCurrentState = STATE_IDLE;
    public int mTargetState = STATE_IDLE;

    private SubTitleType mSubTitleType = SubTitleType.SUBTITLE_TYPE_UNKNOWN;

    private final static String CBM_FORBIDEN_EXCEP = "cbm forbid";
    public Context mContext;
    public boolean mShowErrorToast = false;
    public Handler mHandler;

    // settable by the client
    public Uri mUri;
    private int mDuration;

    // storage the audio/subtitle idx map info
    private int mAudSubInfo[][];
    private int mAllTrackCount;

    private int mPbRate = 0;

    // All the stuff we need for playing and showing a video
    private SurfaceHolder mSurfaceHolder = null;
    public MediaPlayer mMediaPlayer = null;
    public int mVideoWidth;
    public int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private boolean mIsPrepared;
    private MediaController mMediaController;
    private OnCompletionListener mOnCompletionListener;
    private OnSeekCompleteListener mOnSeekCompletionListener;
    private OnInfoListener mOnInfoListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private int mCurrentBufferPercentage;
    private OnErrorListener mOnErrorListener;
    private int mSeekWhenPrepared;

    private TimedText mSubtitleContent = null;
    private Bitmap mSubtitleBmp = null;
    private Paint mSubtitlePaint = null;
    public SurfaceView mSubVideoView = null;
    private boolean mIsSupportDivxHT311 = false;
    private boolean mIsBackgroundPlayback = false;

    private ISizeChangedLinstener mSizeChangedLinstener;

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void setVideoScale(int width, int height) {
        LayoutParams lp = getLayoutParams();
        lp.height = height;
        lp.width = width;
        setLayoutParams(lp);
        // setAnimatiable(false); // only when release DivXHT31 Image, open this
        // code
        requestLayout();
    }

    public interface ISizeChangedLinstener {
        public void sizeChangedEvent(int width, int height);
    }

    public void setSizeChangedListener(ISizeChangedLinstener l) {
        mSizeChangedLinstener = l;
    }

    public FrontVideoView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
        // new MsgThread().start();
    }

    public void SetHandler(Handler handler) {
        mHandler = handler;
    }

    public void SetShowErrorToast(boolean showErrorToast) {
        mShowErrorToast = showErrorToast;
    }

    public FrontVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initVideoView();
    }

    public FrontVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Log.i(TAG, "onMeasure");
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        Log.i(TAG, "onMeasure -- setting size: " + width + 'x' + height);
        setMeasuredDimension(width, height);
    }

    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /*
                 * Parent says we can be as big as we want. Just don't be larger than max size
                 * imposed on ourselves.
                 */
                result = desiredSize;
                break;

            case MeasureSpec.AT_MOST:
                /*
                 * Parent says we can be as big as we want, up to specSize. Don't be larger than
                 * specSize, and don't be larger than the max size imposed on ourselves.
                 */
                result = Math.min(desiredSize, specSize);
                break;

            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    public void clearDivxService() {
        if (mMediaPlayer != null) {
            Log.i(TAG, "clearDivxService: enter");
            try {
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                // mMediaPlayer.setParameter(KEY_PARAMETER_DIVX_SERVICE_RESET,0);
                atcMediaPlayer.clearDivxService();
            } catch (Exception e) {
                Log.e(TAG, "clearDivxService setParameter error");
                e.printStackTrace();
            }
        }
    }

    public int getMediaCapability() {
        Log.e(TAG, "getMediaCapability: enter");
        if (mMediaPlayer != null) {
            try {
                // reply =
                // mMediaPlayer.getParcelParameter(KEY_PARAMETER_MEDIA_CAPABILITY);
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                return atcMediaPlayer.getMediaCapability();
            } catch (Exception e) {
                Log.e(TAG, "getMediaCapability getParcelParameter error");
                e.printStackTrace();
                return 0;
            }
        }
        Log.e(TAG, "getMediaCapability fail for mMediaPlayer is NULL ");
        return 0;

    }

    public int[] getMaxRate() {
        Log.i(TAG, "getMaxRate: enter");
        int[] rt = new int[2];// ffrate/rwrate
        rt[0] = rt[1] = 0;
        if (mMediaPlayer != null) {
            try {
                // reply =
                // mMediaPlayer.getParcelParameter(KEY_PARAMETER_MAXRATE_SUPPORTED);
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                rt[0] = atcMediaPlayer.getFFMaxRate();
                rt[1] = atcMediaPlayer.getRWMaxRate();
            } catch (Exception e) {
                Log.e(TAG, "getMaxRate getParcelParameter error");
                e.printStackTrace();
                return rt;
            }

            Log.i(TAG, " getMaxRate(): max ff rate is =" + rt[0]
                    + ",max rw rate is =" + rt[1]);
            return rt;
        }
        Log.e(TAG, "getMaxRate fail for mMediaPlayer is NULL ");
        return rt;
    }

    public int[] getDfltTrack() {
        Log.i(TAG, "getDfltTrack: enter");
        int[] rt = new int[2];// currentAud/currentSub
        rt[0] = rt[1] = -1;
        if (mMediaPlayer != null) {
            try {
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                rt[0] = atcMediaPlayer.getCurrentAudioTrack();
                rt[1] = atcMediaPlayer.getCurrentSubtitleTrack();
            } catch (Exception e) {
                Log.e(TAG, "getDfltTrack getParcelParameter error");
                e.printStackTrace();
                return rt;
            }

            int currentAud = rt[0];
            int currentSub = rt[1];
            // getting audio index
            if ((currentAud >= mAllTrackCount) || (currentAud < 0)) {
                currentAud = -1;
            } else {
                if (mAudSubInfo[currentAud][0] != MEDIA_TRACK_TYPE_AUDIO) {
                    currentAud = -1;
                } else {
                    currentAud = mAudSubInfo[currentAud][1];
                }
            }
            // getting subtitle index
            if ((currentSub >= mAllTrackCount) || (currentSub < 0)) {
                currentSub = -1;
            } else {
                if (mAudSubInfo[currentSub][0] != MEDIA_TRACK_TYPE_TIMEDTEXT) {
                    currentSub = -1;
                } else {
                    currentSub = mAudSubInfo[currentSub][1];
                }
            }

            rt[0] = currentAud;
            rt[1] = currentSub;
            Log.i(TAG, " getDfltTrack(): current aud track is =" + currentAud
                    + ", current sub track is ="
                    + currentSub);
            return rt;
        }
        Log.e(TAG, "getDfltTrack fail for mMediaPlayer is NULL ");
        return rt;

    }
	
    public void setVideoPath(String path) {
        Log.e(TAG, "setVideoPath"); 
        SourceManager.updatePlayingPath(VideoPlaybackListActivity.getSourceTocken(), path);
        setVideoUri(Uri.parse(path));
    }
    
    public void setVideoPath(Uri path) {
        Log.i(TAG, "setVideoPath Uri"); 
        SourceManager.updatePlayingPath(VideoPlaybackListActivity.getSourceTocken(), path.toString());
        setVideoUri(path);
    }
    
    public  String getVideoPath(){
    	if(mUri!=null){
    		return mUri.toString();
    	}
    	return null;
    }

    private void setVideoUri(Uri uri) {
        mUri = uri;
        // mSeekWhenPrepared = 0;
        mTargetState = STATE_IDLE;
        Log.e(TAG, "openVideo");
        openVideo();
        requestLayout();
        invalidate();
    }

    public void stopPlayback() {
        Log.e(TAG, "stopPlayback - stopPlayback");
        try {
            if (mMediaPlayer != null) {
                mSubtitlePaint = null;
                mSubtitleContent = null;
                if (null != mSubtitleBmp) {
                    if (SubTitleType.SUBTITLE_TYPE_TEXT == mSubTitleType) {
                        mSubtitleBmp.recycle();
                    }
                    mSubtitleBmp = null;
                }
                mSubTitleType = SubTitleType.SUBTITLE_TYPE_UNKNOWN;
                if (isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = null;

                if (null != mSubVideoView) {
                    mSubVideoView.invalidate(0, 0, mSubVideoView.getWidth(),
                            mSubVideoView.getHeight());
                }

                mCurrentState = STATE_IDLE;
                mTargetState = STATE_IDLE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlayback(boolean cleartargetstate) {
        Log.e(TAG, "stopPlayback - cleartargetstate: " + cleartargetstate);
        try {
            if (mMediaPlayer != null) {
                mSubtitlePaint = null;
                mSubtitleContent = null;
                if (null != mSubtitleBmp) {
                    if (SubTitleType.SUBTITLE_TYPE_TEXT == mSubTitleType) {
                        mSubtitleBmp.recycle();
                    }
                    mSubtitleBmp = null;
                }
                mSubTitleType = SubTitleType.SUBTITLE_TYPE_UNKNOWN;
                mSeekWhenPrepared = getCurrentPosition();
                if (isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
                if (null != mSubVideoView) {
                    mSubVideoView.invalidate(0, 0, mSubVideoView.getWidth(),
                            mSubVideoView.getHeight());
                }
                mCurrentState = STATE_IDLE;
                if (cleartargetstate)
                {
                    mTargetState = STATE_IDLE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openVideo() {
        if (mUri == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        Log.i(TAG, "[openVideo] - start");
        // Tell the music playback service to pause
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "pause");
        // mContext.sendBroadcast(intent);
        intent = null;

        Log.i(TAG, "[openVideo] - stop and release media player");
        try {
            release(false);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "FrontVideoView.STOPPLAYER message handle!");
        }

        mMediaPlayer = new MediaPlayer();
        if (mMediaPlayer != null) {
            try {
                // cancel setDisplay() by mtk94107: for AVSwitch
                mMediaPlayer.setDisplay(mSurfaceHolder);
                if (presentation != null) {
                    AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                    atcMediaPlayer.setRearDisplay(presentation.getVideoViewHolder());
                }
                mMediaPlayer.setOnPreparedListener(mPreparedListener);
                mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
                mIsPrepared = false;
                Log.v(TAG, "in fun openVideo reset duration time to -1 ");
                mDuration = -1;
                mMediaPlayer.setOnCompletionListener(mCompletionListener);
                mMediaPlayer.setOnSeekCompleteListener(mSeekCompletionListener);
                mMediaPlayer.setOnInfoListener(mInfoListener);

                mIsSupportDivxHT311 = AtcMediaPlayer.isSupportDivxHT31();

                mMediaPlayer.setOnErrorListener(mErrorListener);
                mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
                mMediaPlayer.setOnTimedTextListener(mTimedTextListener);
                mCurrentBufferPercentage = 0;

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setScreenOnWhilePlaying(true);
                attachMediaController();

                String strUri = mUri.toString();
                Log.e(TAG, "strUri:" + strUri);
                if (strUri.contains("%") || strUri.contains("#")) {
                    Log.i(TAG, "setDataSource 1");
                    mMediaPlayer.setDataSource(mUri.toString());
                }
                else {
                    Log.i(TAG, "setDataSource");
                    mMediaPlayer.setDataSource(mContext, mUri);
                }

                Log.i(TAG,
                        "OpenVideo --- send Message(LOADINGFILE_PROGRESS_CHANGED after 2 seconds)!!!");
                mHandler.sendEmptyMessageDelayed(
                        LOADINGFILE_PROGRESS_CHANGED, 2000);
                Log.e(TAG, "mMediaPlayer.prepareAsync !");
                mMediaPlayer.prepareAsync();

                mCurrentState = STATE_PREPARING;
            } catch (IOException e) {
                mCurrentState = STATE_ERROR;
                mTargetState = STATE_ERROR;
                mHandler.removeMessages(LOADINGFILE_PROGRESS_CHANGED);
                Log.w(TAG, "IOException in Thread of OpenVideo() : " + mUri + e);
                if (mShowErrorToast) {
                    mHandler.sendEmptyMessage(SHOW_ERROR_TOAST);
                    mHandler.removeMessages(PREPARE_FINISH);
                    mHandler.sendEmptyMessage(PREPARE_FINISH);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in openVideo() ERR: " + e);
                mCurrentState = STATE_ERROR;
                mTargetState = STATE_ERROR;
                String err = e.getMessage();
                mHandler.removeMessages(LOADINGFILE_PROGRESS_CHANGED);
                if (err.startsWith(CBM_FORBIDEN_EXCEP)) {
                    Log.e(TAG, "CBM FORBIDDEN VIDEO");
                    mErrorListener.onError(mMediaPlayer, 1000, 0);
                    return;
                }
                if (mShowErrorToast) {
                    mHandler.sendEmptyMessage(SHOW_ERROR_TOAST);
                }
            }
        }
        Log.i(TAG, "[openVideo] - end");
    }

    public void setMediaController(MediaController controller) {
        if (mMediaController != null) {
            mMediaController.hide();
        }
        mMediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ? (View) this
                    .getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(mIsPrepared);
        }
    }

    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            Log.i(TAG, "onVideoSizeChanged -- mVideoWidth=" + mVideoWidth + ", mVideoHeight="
                    + mVideoHeight);
            Log.i(TAG, "onVideoSizeChanged -- width=" + width + ", height=" + height);

            if (mSizeChangedLinstener != null) {
                mSizeChangedLinstener.sizeChangedEvent(width, height);
            }

            boolean IsSupportDivxHT311 = AtcMediaPlayer.isSupportDivxHT31();

            Log.e(TAG, "onVideoSizeChanged -- IsSupportDivxHT311=" + IsSupportDivxHT311);

            if (IsSupportDivxHT311) {
                if ((width > 0) && (height > 0)) {
                    AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mp);
                    atcMediaPlayer.showDivxHT31Video();
                }
            } else {
                if (mVideoWidth != 0 && mVideoHeight != 0) {
                    getHolder().setFixedSize(mVideoWidth, mVideoHeight);

                    if (null != mSubVideoView) {
                        mSubVideoView.getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                    }
                }
            }
        }
    };

    public void CancelAutoStartWhenPrepared() {
        Log.e(TAG, " CancelAutoStartWhenPrepared");
        if (STATE_PLAYING == mTargetState)
        {
            mTargetState = STATE_PREPARED;
        }
    }

    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            Log.e(TAG, "onPrepared: monkey");
            mCurrentState = STATE_PREPARED;
            // briefly show the mediacontroller
            mIsPrepared = true;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            int seekToPosition = mSeekWhenPrepared; // mSeekWhenPrepared may be
                                                    // changed after seekTo()
                                                    // call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }

            Log.i(TAG, "onPrepared - video size: " + mVideoWidth + "/" + mVideoHeight);
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                Log.i(TAG, "onPrepared -- mVideoWidth=" + mVideoWidth +
                        ", mVideoHeight=" + mVideoHeight +
                        ", mSurfaceWidth=" + mSurfaceWidth +
                        ", mSurfaceHeight=" + mSurfaceHeight);
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (null != mSubVideoView) {
                    mSubVideoView.getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                }
                if ((mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) ||
                        mIsBackgroundPlayback) {
                    // We didn't actually change the size (it was already at the
                    // size
                    // we need), so we won't get a "surface changed" callback,
                    // so
                    // start the video here instead of in the callback.
                    if (mTargetState == STATE_PLAYING) {
                        start();
                        if (mMediaController != null) {
                            mMediaController.show();
                        }
                    } else if (!isPlaying()
                            && (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mMediaController != null) {
                            // Show the media controls when we're paused into a
                            // video and make 'em stick.
                            mMediaController.show(0);
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                try {
                    if (mTargetState == STATE_PLAYING) {
                        start();
                        if (mMediaController != null) {
                            mMediaController.show();
                        }
                    } else if (mTargetState == STATE_PAUSED) {
                        // start();
                        pause();
                        if (!isPlaying() && ((seekToPosition != 0) || (getCurrentPosition() > 0))) {
                            if (mMediaController != null) {
                                mMediaController.show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mMediaController != null) {
                mMediaController.hide();
            }
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    private MediaPlayer.OnSeekCompleteListener mSeekCompletionListener = new MediaPlayer.OnSeekCompleteListener() {
        public void onSeekComplete(MediaPlayer mp) {
            if (mMediaController != null) {
                mMediaController.hide();
            }
            if (mOnSeekCompletionListener != null) {
                mOnSeekCompletionListener.onSeekComplete(mMediaPlayer);
            }
        }
    };

    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        public boolean onInfo(MediaPlayer player, int whatInfo, int extra) {
            Log.e(TAG, "OnInfo: " + whatInfo + "," + extra);
            if (mOnInfoListener != null) {
                return mOnInfoListener.onInfo(player, whatInfo, extra);
            }

            return false;
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            Log.e(TAG, "Error: " + framework_err + "," + impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaController != null) {
                mMediaController.hide();
            }

            /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err,
                        impl_err)) {
                    return true;
                }
            }

            /*
             * Otherwise, pop up an error dialog so the user knows that something bad has happened.
             * Only try and pop up the dialog if we're attached to a window. When we're going away
             * and no longer have a window, don't bother showing the user an error.
             */
            // if (getWindowToken() != null) {
            // Resources r = mContext.getResources();
            // int messageId;
            // }
            return true;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };

    private MediaPlayer.OnTimedTextListener mTimedTextListener = new MediaPlayer.OnTimedTextListener() {
        public void onTimedText(MediaPlayer mp, TimedText text) {
            mSubtitleContent = text;
            AtcTimedText atcTimedText = new AtcTimedText(text);
            if (null == mSubtitleContent) {
                invalidate(0, 0, getWidth(), getHeight());
                if (null != mSubVideoView) {
                    mSubVideoView.invalidate(0, 0, mSubVideoView.getWidth(),
                            mSubVideoView.getHeight());
                }
            }
            else {
                if (null != mSubtitleContent.getText()) {
                    if (SubTitleType.SUBTITLE_TYPE_PIC == mSubTitleType) {
                        mSubtitleBmp = null;
                    }
                    mSubTitleType = SubTitleType.SUBTITLE_TYPE_TEXT;
                    if (null == mSubtitleBmp) {
                        WindowManager manager = (WindowManager) mContext
                                .getSystemService(Context.WINDOW_SERVICE);
                        Display display = manager.getDefaultDisplay();
                        int screen_w = display.getWidth();
                        int screen_h = display.getHeight();
                        mSubtitleBmp = Bitmap.createBitmap(screen_w, screen_h / 4,
                                Bitmap.Config.ARGB_4444);
                        // Log.i(TAG,
                        // " onTimedText -- screen_w use screen's width("+
                        // screen_w + ") and height("+ screen_h + ")/4");
                    }
                    if (null == mSubtitlePaint) {
                        mSubtitlePaint = new Paint();
                        mSubtitlePaint.setAntiAlias(true);
                        mSubtitlePaint.setTextSize(30);
                        mSubtitlePaint.setTypeface(Typeface.SERIF);
                        mSubtitlePaint.setColor(Color.WHITE);
                        mSubtitlePaint.setFakeBoldText(true);
                    }

                    mSubtitleBmp.eraseColor(0);
                    Canvas subtitleCanvas = new Canvas(mSubtitleBmp);
                    Paint.FontMetrics fm = mSubtitlePaint.getFontMetrics();
                    float baseline = fm.descent - fm.ascent;
                    // Log.i(TAG, "Font Metrics, ascent: " + fm.ascent +
                    // ", descent: " + fm.descent);
                    float x = 0;
                    float y = 0;
                    // Log.i(TAG, "subtext mSubtitleBmp's width: " +
                    // mSubtitleBmp.getWidth() + ", height: " +
                    // mSubtitleBmp.getHeight());
                    String[] subtexts = autoSplit(mSubtitleContent.getText(), mSubtitlePaint,
                            mSubtitleBmp.getWidth());
                    y = (mSubtitleBmp.getHeight() - subtexts.length * baseline -
                            (subtexts.length - 1) * fm.leading) / 2 - fm.ascent;
                    for (String subtext : subtexts) {
                        // Log.i(TAG, "subtext is " + subtext);
                        // Log.i(TAG, "mSubtitlePaint.measureText(subtext): " +
                        // mSubtitlePaint.measureText(subtext));
                        x = (mSubtitleBmp.getWidth() - mSubtitlePaint.measureText(subtext)) / 2;
                        subtitleCanvas.drawText(subtext, x, y, mSubtitlePaint);
                        y += (baseline + fm.leading);
                    }
                    // Log.i(TAG, "subtext width: " + getWidth() + ", height: "
                    // + getHeight());
                    invalidate(0, 0, getWidth(), getHeight());
                    if (null != mSubVideoView) {
                        mSubVideoView.invalidate(0, 0, mSubVideoView.getWidth(),
                                mSubVideoView.getHeight());
                    }
                }
                else if (null != atcTimedText.getPicture()) {
                    if (SubTitleType.SUBTITLE_TYPE_TEXT == mSubTitleType) {
                        if (null != mSubtitleBmp) {
                            mSubtitleBmp.recycle();
                            mSubtitleBmp = null;
                        }
                    }
                    mSubTitleType = SubTitleType.SUBTITLE_TYPE_PIC;
                    mSubtitleBmp = atcTimedText.getPicture();
                    // Log.i(TAG, "subpicture width: " + mSubtitleBmp.getWidth()
                    // + ", height: " + mSubtitleBmp.getHeight());
                    invalidate(0, 0, getWidth(), getHeight());
                    if (null != mSubVideoView) {
                        mSubVideoView.invalidate(0, 0, mSubVideoView.getWidth(),
                                mSubVideoView.getHeight());
                    }
                }
            }
        }
    };

    /**
     * Register a callback to be invoked when the media file is loaded and ready to go.
     * 
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file has been reached during
     * playback.
     * 
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    /**
     * Register a callback to be invoked when a seek operation has been completed.
     * 
     * @param listener the callback that will be run
     */
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mOnSeekCompletionListener = listener;
    }

    public void setOnInfoListener(OnInfoListener listener) {
        mOnInfoListener = listener;
    }

    /**
     * Register a callback to be invoked when an error occurs during playback or setup. If no
     * listener is specified, or if the listener returned false, FrontVideoView will inform the user
     * of any errors.
     * 
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                int h) {

            Log.w(TAG, "----mSHCallback----FrontSurfaceChanged");
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            // Log.e(TAG, "FrontSurface: mSurfaceWidth:" + mSurfaceWidth +
            // " mSurfaceHeight:"
            // + mSurfaceHeight);
            // Log.e(TAG, "FrontVideo: mVideoWidth:" + mVideoWidth +
            // " mVideoHeight:" + mVideoHeight);
            mIsBackgroundPlayback = false;

            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = ((mVideoWidth == w) && (mVideoHeight == h));

            Log.e(TAG, "isValidState: " + isValidState + " - hasValidSize: " + hasValidSize);

            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
                if (mMediaController != null) {
                    mMediaController.show();
                }
            }

            if (mMediaPlayer != null && mTargetState == STATE_PAUSED) {
                AudioManager audioManager = (AudioManager) mContext
                        .getSystemService(Context.AUDIO_SERVICE);
                audioManager.setYeconVolumeMute(AudioManager.STREAM_MUSIC, true, 0,
                        YeconConstants.SRC_VOLUME_VIDEO);

                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
                if (mMediaController != null) {
                    mMediaController.show();
                }

                SystemClock.sleep(100);
                pause();

                audioManager.setYeconVolumeMute(AudioManager.STREAM_MUSIC, false, 0,
                        YeconConstants.SRC_VOLUME_VIDEO);
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.w(TAG, "----mSHCallback----FrontSurfaceCreated,holder=" + holder);
            mIsBackgroundPlayback = false;

            mSurfaceHolder = holder;
            // to do: Setting Front SurfaceHolder low
            if (null != mMediaPlayer) {
                try {
                    mMediaPlayer.setDisplay(mSurfaceHolder);
                } catch (Exception e) {
                    Log.e(TAG, "Exception in surfaceCreated setdisplay: " + e);
                    String err = e.getMessage();
                    Log.e(TAG, "Exception in surfaceCreated setdisplay, exception message: " + err);
                    if (err.startsWith(CBM_FORBIDEN_EXCEP))
                    {
                    }
                }
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.w(TAG, "----mSHCallback----FrontSurfaceDestroyed");

            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            if (null != mMediaPlayer) {
                try {
                    mMediaPlayer.setDisplay(null);
                } catch (Exception e) {
                    Log.e(TAG, "Exception in surfaceDestroyed setdisplay: " + e);
                    String err = e.getMessage();
                    Log.e(TAG, "Exception in surfaceDestroyed setdisplay, exception message: "
                            + err);
                    if (err.startsWith(CBM_FORBIDEN_EXCEP)) {
                    }
                }
            }
            // to do: Close Front SurfaceHolder null low
            if (mMediaController != null)
                mMediaController.hide();

            // if (null != mMediaPlayer) {
            mIsBackgroundPlayback = true;
            // }
        }
    };

    /*
     * release the media player in any state
     */
    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            Log.i(TAG, "[release] - mMediaPlayer");

            mSubtitlePaint = null;
            mSubtitleContent = null;
            if (null != mSubtitleBmp) {
                if (SubTitleType.SUBTITLE_TYPE_TEXT == mSubTitleType) {
                    mSubtitleBmp.recycle();
                }
                mSubtitleBmp = null;
            }
            mSubTitleType = SubTitleType.SUBTITLE_TYPE_UNKNOWN;
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            if (null != mSubVideoView) {
                mSubVideoView.invalidate(0, 0, mSubVideoView.getWidth(),
                        mSubVideoView.getHeight());
            }
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mIsPrepared && keyCode != KeyEvent.KEYCODE_BACK
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP
                && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_MENU
                && keyCode != KeyEvent.KEYCODE_CALL
                && keyCode != KeyEvent.KEYCODE_ENDCALL && mMediaPlayer != null
                && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    && isPlaying()) {
                pause();
                mMediaController.show();
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (isPlaying()) {
                try {
                    mMediaPlayer.pause();
                    mCurrentState = STATE_PAUSED;
                } catch (Exception e) {
                    Log.e("CustomView", "pause fail when playing");
                    e.printStackTrace();
                }
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void openDestAudio(int destAudio) {
        if (mMediaPlayer == null) {
            return;
        }
        try {
            AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
            atcMediaPlayer.openAudioOutput(destAudio);

        } catch (Exception e) {
            Log.e(TAG, "openAudioOutput audio fail destAudio: " + destAudio);
            e.printStackTrace();
        }
    }

    public void closeDestAudio(int destAudio) {
        if (mMediaPlayer == null) {
            return;
        }
        try {
            AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
            atcMediaPlayer.closeAudioOutput(destAudio);

        } catch (Exception e) {
            Log.e(TAG, "closeAudioOutput audio fail destAudio: " + destAudio);
            e.printStackTrace();
        }
    }

    public void suspend() {
        int capability = getMediaCapability();
        if (0 != (capability & AtcMediaPlayer.FILE_SEEK_UNSUPPORT)) {
            Log.i(TAG, "suspend - media file not support seek!");
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = getCurrentPosition();
        }
        Log.i(TAG, "suspend: mSeekWhenPrepared = " + mSeekWhenPrepared);
        release(false);
    }

    public void resume() {
        Log.i(TAG, "resume: mSeekWhenPrepared = " + mSeekWhenPrepared);
        Log.i(TAG, "resume - mTargetState = " + mTargetState);
        if (STATE_IDLE != mTargetState) {
            openVideo();
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null && mIsPrepared) {
            try {
                mDuration = mMediaPlayer.getDuration();
            } catch (Exception e) {
                Log.e("CustomView", "get getDuration fail");
                e.printStackTrace();
                mDuration = -1;
            }
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null && mIsPrepared) {
            try {
                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                if (0 == mDuration) {
                    return mCurrentPosition; // only when mDuration is invalid
                } else {
                    return mCurrentPosition < mDuration ? mCurrentPosition : mDuration;
                }
            } catch (Exception e) {
                Log.e("CustomView", "get getCurrentPosition fail");
                e.printStackTrace();
                return -1;
            }
        } else {
            return -1;
        }
    }

    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            Log.i(TAG, "seek position is: " + msec);
            try {
                mMediaPlayer.seekTo(msec);
                mSeekWhenPrepared = 0;
            } catch (Exception e) {
                Log.e("CustomView", "seekTo msec" + msec);
                e.printStackTrace();
            }
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsPrepared) {
            try {
                return mMediaPlayer.isPlaying();
            } catch (Exception e) {
                Log.e("CustomView", "get status of isPlaying fail");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean isRealPlaying() {
        if (isInPlaybackState()) {
            AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
            if ((null != atcMediaPlayer) &&
                    (AtcMediaPlayer.MEDIA_PLAYER_STARTED == atcMediaPlayer.getRealCurrentState())) {
                return (true);
            }
        }

        return (false);
    }

    public boolean isPrepared() {
        return mIsPrepared;
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    public int findAudIdxInAll(int audioIndex) {
        for (int i = 0; i < mAllTrackCount; i++) {
            if ((mAudSubInfo[i][0] == MEDIA_TRACK_TYPE_AUDIO)
                    && ((mAudSubInfo[i][1] == audioIndex)))
            {
                Log.e(TAG, "getting aud mAudSubInfo[" + i + "][0]=" + mAudSubInfo[i][0]
                        + "mAudSubInfo[" + i + "][0]=" + mAudSubInfo[i][1]);
                return i;
            }
        }
        return -1;
    }

    public int findSubIdxInAll(int subIndex) {
        for (int i = 0; i < mAllTrackCount; i++) {
            if ((mAudSubInfo[i][0] == MEDIA_TRACK_TYPE_TIMEDTEXT)
                    && ((mAudSubInfo[i][1] == subIndex)))
            {
                Log.e(TAG, "getting sub mAudSubInfo[" + i + "][0]=" + mAudSubInfo[i][0]
                        + "mAudSubInfo[" + i + "][0]=" + mAudSubInfo[i][1]);
                return i;
            }
        }
        return -1;
    }

    public int setSubtitleIndex(int index) throws Exception {
        Log.e(TAG, "setSubtitleIndex index is: " + index);
        if (mMediaPlayer != null) {

            int indexInAllTrack = findSubIdxInAll(index);
            Log.e(TAG, "Subtitle index displayed=" + index
                    + ",Subtitle index In all tracks ="
                    + indexInAllTrack);
            if (indexInAllTrack == -1)
            {
                Log.e(TAG, "setSubtitleIndex find audio index in all track failed");
                return -1;
            }
            try {
                Log.e(TAG, "setSubtitleIndex before invoke");
                mMediaPlayer.selectTrack(indexInAllTrack);
            } catch (IllegalStateException ex) {
                Log.e(TAG, "setSubtitleIndex invoke error");
                ex.printStackTrace();
                return -1;
            }

            return 0;
        }

        Log.e(TAG, "setSubtitleIndex fail for mMediaPlayer is NULL");
        return -1;
    }

    public int hideSubtitle(int index) throws Exception {
        Log.i(TAG, "hideSubtitle index is: " + index);
        if (mMediaPlayer != null) {

            try {
                int indexInAllTrack = findSubIdxInAll(index);
                Log.i(TAG, " hideSubtitle(): Subtitle index displayed=" + index
                        + ",Subtitle index In all tracks ="
                        + indexInAllTrack);
                mMediaPlayer.deselectTrack(indexInAllTrack);
                Log.i(TAG, "hideSubtitle success");
                return 0;
            } catch (IllegalStateException e) {
                Log.i(TAG, "hideSubtitle error");
                e.printStackTrace();
                return -1;
            }
        }
        Log.i(TAG, "hideSubtitle fail for mMediaPlayer is NULL");
        return -1;
    }

    public int[] getAllTrackCount() {
        int[] rt = new int[3];// audio/subtitle/video
        if (null == rt) {
            return (null);
        }
        rt[0] = rt[1] = rt[2] = 0;
        try {
            MediaPlayer.TrackInfo[] trackInfos = mMediaPlayer.getTrackInfo();
            mAllTrackCount = trackInfos.length;
            if ((0 == mAllTrackCount) ||
                    (trackInfos == null))
            {
                Log.e(TAG, "invoke error monkey ");
                rt[0] = rt[1] = rt[2] = 0;
                return rt;
            }

            Log.i(TAG, "getAllTrackCount -- mAllTrackCount =" + mAllTrackCount);

            mAudSubInfo = new int[mAllTrackCount][2];
            int vcount = 0, acount = 0, scount = 0;
            for (int i = 0; i < mAllTrackCount; i++) {
                mAudSubInfo[i][0] = (int) (trackInfos[i].getTrackType());
                switch (trackInfos[i].getTrackType()) {
                    case MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                        mAudSubInfo[i][1] = acount;
                        acount++;
                        break;
                    case MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                        mAudSubInfo[i][1] = scount;
                        scount++;
                        break;
                    case MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                        mAudSubInfo[i][1] = vcount;
                        vcount++;
                        break;
                    default:
                        mAudSubInfo[i][1] = 0;
                        break;
                }
            }

            Log.i(TAG, "getAllTrackCount -- AudCnt=" + acount + ", SPCnt=" + scount + ", VidCnt="
                    + vcount);
            rt[0] = acount;
            rt[1] = scount;
            rt[2] = vcount;

            return rt;
        } catch (Exception e) {
            Log.e(TAG, "getAllTrackCount fail in mMediaPlayer.getTrackInfo");
            e.printStackTrace();
            rt[0] = rt[1] = rt[2] = 0;
            return rt;
        }
    }

    public int setAudioChannelIndex(int index) {
        Log.e(TAG, "setAudioChannelIndex index: " + index);
        if (mMediaPlayer != null) {
            int indexInAllTrack = findAudIdxInAll(index);
            if (indexInAllTrack == -1) {
                Log.i(TAG, "setAudioChannelIndex find audio index in all track failed");
                return -1;
            }

            try {
                Log.i(TAG, "setAudioChannelIndex before invoke");
                mMediaPlayer.selectTrack(indexInAllTrack);

                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                int ret = -1;

                if ((null != atcMediaPlayer) &&
                        (indexInAllTrack == atcMediaPlayer.getCurrentAudioTrack()))
                {
                    ret = 0;
                }
                atcMediaPlayer = null;

                return (ret);
            } catch (IllegalStateException ex) {
                Log.i(TAG, "setAudioChannelIndex invoke error");

                ex.printStackTrace();
                return -1;
            } catch (Exception e) {
                Log.i(TAG, "setAudioChannelIndex invoke error 2");
                return -1;
            }
        }
        Log.e(TAG, "setAudioChannelIndex fail for mMediaPlayer is NULL");
        return -1;
    }

    public int postTouchPos(int x, int y) {
        Log.e(TAG, "postTouchPos x:" + x + "y:" + y);
        if (mMediaPlayer != null) {
            try {
                Log.i(TAG, "postTouchPos(): before invoke");
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                atcMediaPlayer.selectMenu(x, y);
            } catch (IllegalStateException ex) {
                Log.i(TAG, "postTouchPos(): invoke error");
                ex.printStackTrace();
                return -1;
            } catch (Exception e) {
                Log.i(TAG, "postTouchPos(): invoke error 2");
                e.printStackTrace();
                return -1;
            }

            return 0;
        }
        Log.e(TAG, "postTouchPos fail for mMediaPlayer is NULL");
        return -1;
    }

    public AtcMediaPlayer.DivxDrmInfo getDivxDrmInfo() {
        Log.e(TAG, "getDivxDrmInfo");

        if (mMediaPlayer != null) {
            try {
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                return atcMediaPlayer.getDivxDrmInfo();
            } catch (RuntimeException ex) {
                Log.e(TAG, "getDivxDrmInfo(): invoke error");

                ex.printStackTrace();
                return null;
            }
        }
        Log.e(TAG, "getDivxDrmInfo fail for mMediaPlayer is NULL");
        return null;
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
            if ((null != atcMediaPlayer)
                    && (AtcMediaPlayer.MEDIA_PLAYER_STARTED == atcMediaPlayer.getRealCurrentState())) {
                Log.i(TAG, "start - play success");
                mCurrentState = STATE_PLAYING;
            } else {
                Log.i(TAG, "start - play failed");
            }
        }
        mTargetState = STATE_PLAYING;
    }

    private boolean isInPlaybackState() {
        Log.i(TAG, "isInPlaybackState - mMediaPlayer: " + mMediaPlayer + " - mCurrentState:"
                + mCurrentState);
        return (mMediaPlayer != null && mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    public void pause(boolean cleartargetstate) {
        Log.i(TAG, "pause - cleartargetstate is " + cleartargetstate);
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        if (cleartargetstate)
        {
            mTargetState = STATE_PAUSED;
        }
    }

    public void resumeToTargetState() {
        Log.i(TAG, "resumeToTargetState - mCurrentState is " + mCurrentState + ", mTargetState is "
                + mTargetState);
        if (mCurrentState == mTargetState) {
            return;
        }
        if (STATE_PREPARING == mCurrentState)
        {
            return;
        }
        if ((STATE_PAUSED == mCurrentState) && (STATE_PLAYING == mTargetState))
        {
            if (isInPlaybackState()) {
                mMediaPlayer.start();
                if (mMediaPlayer.isPlaying()) {
                    mCurrentState = STATE_PLAYING;
                }
            }
        }
        else if ((STATE_PREPARED == mCurrentState) && (STATE_PLAYING == mTargetState)) {
            if (isInPlaybackState()) {
                mMediaPlayer.start();
                if (mMediaPlayer.isPlaying()) {
                    mCurrentState = STATE_PLAYING;
                }
            }
        }
        else if (STATE_IDLE == mCurrentState)
        {
            openVideo();
        }
    }

    // *********Get current playback rate****************
    public int getMediaFFState() {
        Log.i("CustomView", "getMediaFFState mPbRate: " + mPbRate);
        return mPbRate;
    }

    public int setMediaFFState(int rate) {
        Log.e("CustomView", "setMediaFFState rate: " + rate);
        if (mMediaPlayer != null) {
            try {
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                if (null == atcMediaPlayer) {
                    Log.i(TAG, "setMediaFFState - failed, null == atcMediaPlayer");
                    return -1;
                }
                boolean ret = atcMediaPlayer.setPlaybackRateAsync(rate);
                if (true == ret) {
                    mPbRate = rate;
                    return 0;
                } else {
                    Log.e("CustomView", "setMediaFFState fail rate: " + rate);
                    return -1;
                }
            } catch (Exception e) {
                Log.e(TAG, "setParameter error ", e);
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public int IsHWDecoded() {
        return 1;
    }

    public int SetVideoVisibility(boolean fgVisible) {
        Log.e("CustomView", "SetVideoVisibility -- fgVisible = " + fgVisible);
        if (mMediaPlayer != null) {
            boolean ret = false;
            try {
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                atcMediaPlayer.SetVideoVisibile(fgVisible);
            } catch (Exception e) {
                Log.e(TAG, "setParameter error ", e);
                e.printStackTrace();
                return -1;
            }
            if (ret != true) {
                Log.e("CustomView", "SetVideoVisibility fail fgVisible = " + fgVisible);
                return -1; // fail
            }
            return 0; // success
        }
        return -1;
    }

    public int[] GetLoadingStatus() {
        int[] rt = new int[2];
        rt[0] = rt[1] = 0;
        if (mMediaPlayer != null) {
            try {
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMediaPlayer);
                rt[0] = atcMediaPlayer.getLoadingPercentage();
                rt[1] = atcMediaPlayer.getLoadingRemainTime();
            } catch (Exception e) {
                Log.e(TAG, "GetLoadingStatus getParcelParameter error");
                e.printStackTrace();
                return rt;
            }
            Log.i(TAG, "GetLoadingStatus() -- current loadingstatus = " + rt[0]
                    + "%%, Remaining Time = " + rt[1] + "ms");
            return rt;
        }

        Log.e(TAG, "GetLoadingStatus fail for mMediaPlayer is NULL");

        return rt;
    }

    private String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float text_w = p.measureText(content);
        if (text_w <= width) {
            return new String[] {
                    content
            };
        }

        int start = 0, end = 1, i = 0;
        int lines = (int) Math.ceil(text_w / width);
        String[] lineTexts = new String[lines];
        while (start < length) {
            if (p.measureText(content, start, end) > width) {
                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }
            if (end == length) {
                lineTexts[i] = (String) content.subSequence(start, end);
                break;
            }
            end += 1;
        }

        return (lineTexts);
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        if ((null == mSubtitleContent) || (null == mSubtitleBmp)) {
            return;
        }
        AtcTimedText atcTimedText = new AtcTimedText(mSubtitleContent);
        Rect src = new Rect(0, 0, mSubtitleBmp.getWidth(), mSubtitleBmp.getHeight());
        Rect dst = null;
        if (null != atcTimedText.getPicture()) {
            if (null != mSubtitleContent.getBounds()) {
                dst = new Rect(mSubtitleContent.getBounds());
                dst.left = dst.left * getWidth() / atcTimedText.getVideoWidth();
                dst.right = dst.right * getWidth() / atcTimedText.getVideoWidth();
                dst.top = dst.top * getHeight() / atcTimedText.getVideoHeight();
                dst.bottom = dst.bottom * getHeight() / atcTimedText.getVideoHeight();
            } else {
                dst = new Rect(0, getHeight() * 3 / 4, getWidth(), getHeight());
            }
        } else {
            dst = new Rect(0, getHeight() * 3 / 4, getWidth(), getHeight());
        }
        canvas.drawBitmap(mSubtitleBmp, src, dst, mSubtitlePaint);
    }

    public void drawSubtitle(Canvas canvas, Rect dstVideoRect) {
        if ((null == canvas) || (null == dstVideoRect) ||
                (null == mSubtitleContent) || (null == mSubtitleBmp)) {
            return;
        }
        AtcTimedText atcTimedText = new AtcTimedText(mSubtitleContent);
        Rect src = new Rect(0, 0, mSubtitleBmp.getWidth(), mSubtitleBmp.getHeight());
        Rect dst = null;

        if (null != atcTimedText.getPicture()) {
            if (null != mSubtitleContent.getBounds()) {
                dst = new Rect(mSubtitleContent.getBounds());
                dst.left = dst.left * dstVideoRect.width() / atcTimedText.getVideoWidth();
                dst.right = dst.right * dstVideoRect.width() / atcTimedText.getVideoWidth();
                dst.top = dst.top * dstVideoRect.height() / atcTimedText.getVideoHeight();
                dst.bottom = dst.bottom * dstVideoRect.height() / atcTimedText.getVideoHeight();
            } else {
                dst = new Rect(0, dstVideoRect.height() * 3 / 4, dstVideoRect.width(), dstVideoRect
                        .height());
            }
        } else {
            dst = new Rect(0, dstVideoRect.height() * 3 / 4, dstVideoRect.width(), dstVideoRect
                    .height());
        }
        canvas.drawBitmap(mSubtitleBmp, src, dst, mSubtitlePaint);
    }
}
