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

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.autochips.media.*;

import android.graphics.Rect;
import android.graphics.Canvas;

/**
 * Displays a video file. The RearVideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 */
public class RearVideoView extends SurfaceView {
    private String TAG = "RearVideoView";
    // All the stuff we need for playing and showing a video
    private SurfaceHolder mSurfaceHolder = null;

    public FrontVideoView mMasterVideoView = null;
    private final static String CBM_FORBIDEN_EXCEP = "cbm forbid";

    @SuppressWarnings("deprecation")
    public RearVideoView(Context context) {
        super(context);
        Log.i(TAG, "RearVideoView 1 param");
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public RearVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        Log.i(TAG, "RearVideoView 2 param");
    }

    @SuppressWarnings("deprecation")
    public RearVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i(TAG, "RearVideoView 3 param holder:" + getHolder() + "mSHCallback:" + mSHCallback);
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                int h) {

            Log.i(TAG, "----mSHCallback----RearSurfaceChanged, w=" + w + ", h=" + h);

            if (null != mMasterVideoView) {
                Log.i(TAG, "----mSHCallback----RearSurfaceChanged, mVideoWidth=" +
                        mMasterVideoView.mVideoWidth + ", mVideoHeight="
                        + mMasterVideoView.mVideoHeight);
            }
            mSurfaceHolder = holder;
            // getHolder().setFixedSize(FrontVideoView.mVideoWidth,
            // FrontVideoView.mVideoHeight);

            if ((null != mMasterVideoView) && (null != mMasterVideoView.mMediaPlayer)) {
                Log.i(TAG, "----mSHCallback----RearSurfaceChanged, set rear display");
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMasterVideoView.mMediaPlayer);
                try
                {
                    atcMediaPlayer.setRearDisplay(mSurfaceHolder);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exception in RearsurfaceChanged setdisplay: " + e);
                    String err = e.getMessage();
                    Log.e(TAG, "Exception in RearsurfaceChanged setdisplay, exception message: "
                            + err);
                    if (err.startsWith(CBM_FORBIDEN_EXCEP))
                    {
                    }
                }
            }

        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.w(TAG, "----mSHCallback----RearSurfaceCreated,holder=" + holder);

            mSurfaceHolder = holder;
            // to do: Setting Front SurfaceHolder low
            // getHolder().setFixedSize(FrontVideoView.mVideoWidth,
            // FrontVideoView.mVideoHeight);
            if ((null != mMasterVideoView) && (null != mMasterVideoView.mMediaPlayer)) {
                Log.i(TAG, "----mSHCallback----RearSurfaceCreated, set rear display");
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMasterVideoView.mMediaPlayer);
                try
                {
                    atcMediaPlayer.setRearDisplay(mSurfaceHolder);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exception in RearsurfaceCreated setdisplay: " + e);
                    String err = e.getMessage();
                    Log.e(TAG, "Exception in RearsurfaceCreated setdisplay, exception message: "
                            + err);
                    if (err.startsWith(CBM_FORBIDEN_EXCEP))
                    {
                    }
                }
            }

        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.w(TAG, "----mSHCallback----RearSurfaceDestroyed");

            // after we return from this we can't use the surface any more
            // to do: Close Front SurfaceHolder null low
            mSurfaceHolder = null;
            if ((null != mMasterVideoView) && (null != mMasterVideoView.mMediaPlayer)) {
                AtcMediaPlayer atcMediaPlayer = new AtcMediaPlayer(mMasterVideoView.mMediaPlayer);
                try
                {
                    atcMediaPlayer.setRearDisplay(mSurfaceHolder);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exception in RearsurfaceDestroyed setdisplay: " + e);
                    String err = e.getMessage();
                    Log.e(TAG, "Exception in RearsurfaceDestroyed setdisplay, exception message: "
                            + err);
                    if (err.startsWith(CBM_FORBIDEN_EXCEP))
                    {
                    }
                }
            }
        }
    };

    protected void onDraw(Canvas canvas) {
        if (null != mMasterVideoView) {
            Rect dstRect = new Rect(0, 0, getWidth(), getHeight());

            mMasterVideoView.drawSubtitle(canvas, dstRect);
        }
    }

}
