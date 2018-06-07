package com.autochips.picturebrowser.status;

import java.lang.ref.WeakReference;

import com.autochips.picturebrowser.DataManager;
import com.autochips.picturebrowser.PictureApplication;
import com.autochips.picturebrowser.ShowImgActivity;
import com.autochips.picturebrowser.R;
import com.autochips.picturebrowser.ShowImgActivity.PicturePresentation;
import com.autochips.picturebrowser.data.BitmapContext;
import com.autochips.picturebrowser.data.MediaData;
import com.autochips.picturebrowser.utils.RandomIndex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayStatus extends PreludeStatus {

    private static final int HANDLE_PLAY = 0;
    private static final int HANDLE_PLAY_TIMEOUT = 2000; // 2s

    private static final String PREF_NAME = "PREF_PLAY_STATUS";
    private static final String PREF_SHUFFLE = "PREF_SHUFFLE";
    private static final String PREF_PLAYING = "PREF_PLAYING";
    private static final String PREF_LOOP_ALL = "PREF_LOOP_ALL";

    private boolean bShuffleMode = false;
    private boolean bLoopAll = true;
    private boolean bPlaying = false;

    private StatusMachine mStatusMachine = null;
    private WeakReference<?> mOwner = null;
    private WeakReference<DataManager> dataManager = null;
    private RandomIndex mIndexs = new RandomIndex();

    public PlayStatus(DataManager dataManager,
            ShowImgActivity showImgActivity) {
    	this.dataManager = new WeakReference<DataManager>(dataManager);
        mOwner = new WeakReference<ShowImgActivity>(showImgActivity);
        mStatusMachine = new StatusMachine(showImgActivity);
    }

    @SuppressLint("HandlerLeak")
    private class StatusMachine extends Handler {
        private WeakReference<?> mContext = null;

        public StatusMachine(ShowImgActivity showImgActivity) {
            mContext = new WeakReference<ShowImgActivity>(showImgActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLE_PLAY:
                ShowImgActivity owner = (ShowImgActivity) mContext.get();
                if (owner == null) {
                    return;
                }
                if (!bLoopAll || !bPlaying) {
                    return;
                }
                if (bShuffleMode) {
                    int idx = mIndexs.next();
                    MediaData data = dataManager.get().getData();
                    BitmapContext bmpCtx = data.position(idx, false);
                    owner.setCurrentImageASync(bmpCtx);
                } else {
                    MediaData data =dataManager.get().getData();
                    BitmapContext bmpCtx = null;
                    bmpCtx = data.next(true);
                    owner.setCurrentImageASync(bmpCtx);
                }
                sendEmptyMessageDelayed(HANDLE_PLAY, HANDLE_PLAY_TIMEOUT);
                break;
            }
        }
    }

    public boolean isShuffleMode() {
        return bShuffleMode;
    }

    public boolean isLoopAll() {
        return bLoopAll;
    }

    public boolean isPlaying() {
        return bPlaying;
    }
    
    public void triggerShuffleMode() {
        ShowImgActivity owner = (ShowImgActivity) mOwner.get();
        if (owner == null)
            return;
        /*
        bShuffleMode = !bShuffleMode;
        ImageView cicle = (ImageView) owner.findViewById(R.id.shuffle);
        if (bShuffleMode) {
            cicle.setImageResource(R.drawable.shuffle);
            MediaData data = ((PictureApplication) mApplication.get())
                    .getData();
            mIndexs.setRange(data.size());
        } else {
            cicle.setImageResource(R.drawable.shuffle_no);
        }
        if (!mStatusMachine.hasMessages(HANDLE_PLAY)) {
            mStatusMachine.sendEmptyMessageDelayed(HANDLE_PLAY,
                    HANDLE_PLAY_TIMEOUT);
        }
        */
    }

    public void triggerLoopAll() {
        ShowImgActivity owner = (ShowImgActivity) mOwner.get();
        if (owner == null)
            return;
        /*
        bLoopAll = !bLoopAll;
        ImageView cicle = (ImageView) owner.findViewById(R.id.cicle);
        if (mStatusMachine.hasMessages(HANDLE_PLAY)) {
            mStatusMachine.removeMessages(HANDLE_PLAY);
        }
        if (bLoopAll) {
            cicle.setImageResource(R.drawable.play_all);
            mStatusMachine.sendEmptyMessageDelayed(HANDLE_PLAY,
                    HANDLE_PLAY_TIMEOUT);
        } else {
            cicle.setImageResource(R.drawable.play_single);
        }
        */
    }

    public void triggerPlaying() {
        ShowImgActivity owner = (ShowImgActivity) mOwner.get();
        if (owner == null)
            return;
        bPlaying = !bPlaying;

        PicturePresentation presentation = owner.getPicturePresentation();
        if (null != presentation) {
            View play = presentation.getPlay();
            if (null != play) {
                if(play instanceof ImageView){
            		((ImageView)play).setImageResource(bPlaying ? R.drawable.main_pause : R.drawable.main_play);
            	}
            	else if(play instanceof TextView){
            		((TextView)play).setCompoundDrawablesWithIntrinsicBounds(0, bPlaying? R.drawable.main_pause : R.drawable.main_play,0,0);
            		((TextView)play).setText(bPlaying?R.string.str_btn_pause:R.string.str_btn_play);
            	}
            }
        }

        View play = (View) owner.findViewById(R.id.play);
        if (mStatusMachine.hasMessages(HANDLE_PLAY)) {
            mStatusMachine.removeMessages(HANDLE_PLAY);
        }
        if (bPlaying) {
        	if(play instanceof ImageView){
        		((ImageView)play).setImageResource(R.drawable.main_pause);
        	}
        	else if(play instanceof TextView){
        		((TextView)play).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.main_pause,0,0);
        		((TextView)play).setText(R.string.str_btn_pause);
        	}
            mStatusMachine.sendEmptyMessageDelayed(HANDLE_PLAY,
                    HANDLE_PLAY_TIMEOUT);
        } else {
        	if(play instanceof ImageView){
        		((ImageView)play).setImageResource(R.drawable.main_play);
        	}
        	else if(play instanceof TextView){
        		((TextView)play).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.main_play,0,0);
        		((TextView)play).setText(R.string.str_btn_play);
        	}
            mStatusMachine.removeMessages(HANDLE_PLAY);
        }
    }

    public void onResume() {
        if (!mStatusMachine.hasMessages(HANDLE_PLAY)) {
            mStatusMachine.sendEmptyMessageDelayed(HANDLE_PLAY,
                    HANDLE_PLAY_TIMEOUT);
        }
    }

    public void onPause() {
        if (mStatusMachine.hasMessages(HANDLE_PLAY)) {
            mStatusMachine.removeMessages(HANDLE_PLAY);
        }
    }

    public void pause() {
        if (!bPlaying)
            return;
        triggerPlaying();
    }

    @Override
    public void onLoad() {
        ShowImgActivity showImgActivity = (ShowImgActivity) mOwner.get();
        SharedPreferences sp = showImgActivity.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        bShuffleMode = sp.getBoolean(PREF_SHUFFLE, bShuffleMode);
        if (bShuffleMode) {
            mIndexs.setRange(dataManager.get().getData().size());
        }
        bPlaying = sp.getBoolean(PREF_PLAYING, bPlaying);
        bLoopAll = sp.getBoolean(PREF_LOOP_ALL, bLoopAll);
        if (!mStatusMachine.hasMessages(HANDLE_PLAY)) {
            mStatusMachine.sendEmptyMessageDelayed(HANDLE_PLAY,
                    HANDLE_PLAY_TIMEOUT);
        }
    }

    @Override
    public void onClear() {
        if (mStatusMachine.hasMessages(HANDLE_PLAY)) {
            mStatusMachine.removeMessages(HANDLE_PLAY);
        }
        bShuffleMode = false;
        bPlaying = false;
        bLoopAll = true;
        mIndexs = null;
    }

    @Override
    public void onStore() {
        if (mStatusMachine.hasMessages(HANDLE_PLAY)) {
            mStatusMachine.removeMessages(HANDLE_PLAY);
        }
        ShowImgActivity showImgActivity = (ShowImgActivity) mOwner.get();
        if (showImgActivity == null)
            return;
        SharedPreferences sp = showImgActivity.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(PREF_SHUFFLE, bShuffleMode);
        edit.putBoolean(PREF_PLAYING, false);
        edit.putBoolean(PREF_LOOP_ALL, bLoopAll);
        edit.commit();
    }
}
