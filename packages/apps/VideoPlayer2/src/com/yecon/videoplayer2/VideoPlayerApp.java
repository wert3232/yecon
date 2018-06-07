
package com.yecon.videoplayer2;

import com.yecon.common.ActivityApp;

import android.app.Application;

import android.util.Log;

public class VideoPlayerApp extends ActivityApp {

    public final static String TAG = "VideoPlayerApp";

    @SuppressWarnings("unused")
    private static VideoPlayerApp gInst = null;

    public static VideoPlayerApp GetInstance() {
        return (gInst);
    }

    private VideoPlayerContext videoPlayerContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "VideoPlayerApp onCreate");
        gInst = this;
        videoPlayerContext = new VideoPlayerContext(this);
    }
    
    public VideoPlayerContext getVideoPlayerContext(){
    	return videoPlayerContext;
    }
}
