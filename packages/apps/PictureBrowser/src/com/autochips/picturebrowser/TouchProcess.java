package com.autochips.picturebrowser;

import java.lang.ref.WeakReference;

import com.autochips.picturebrowser.ShowImgActivity.AHandler;
import com.autochips.picturebrowser.ShowImgActivity.PicturePresentation;
import com.autochips.picturebrowser.data.BitmapContext;
import com.autochips.picturebrowser.data.MediaData;
import com.autochips.picturebrowser.status.PlayStatus;
import com.autochips.picturebrowser.ui.ZoomRotateImageView;
import com.autochips.picturebrowser.utils.Logger;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class TouchProcess implements OnTouchListener, OnClickListener {
    private static final long PROCESS_SLICE = 200; // 200ms
    private long mLastProcessUptime = -1;

    //private WeakReference<?> mApplication = null;
    private WeakReference<DataManager> dataManager = null;
    private WeakReference<ShowImgActivity> mShowImgActivity = null;

    public TouchProcess(DataManager dataManager,
            ShowImgActivity showImgActivity) {
        this.dataManager = new WeakReference<DataManager>(dataManager);
        this.mShowImgActivity = new WeakReference<ShowImgActivity>(showImgActivity);
    }

    private boolean dataValid() {
    	if(dataManager.get()!=null){
    		MediaData data = ((DataManager) dataManager.get()).getData();
            return data.isDataValid();
    	}
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent me) {
        if (mShowImgActivity.get() == null) {
            return false;
        }
        if (!dataValid()) {
            Toast.makeText(mShowImgActivity.get(), R.string.no_pic_msg, Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        int action = me.getAction();
        boolean isTouchDown = action == MotionEvent.ACTION_DOWN;
        boolean isTouchUp = action == MotionEvent.ACTION_UP;
        if (!isTouchDown && !isTouchUp)
            return false;
        if (me.getEventTime() < mLastProcessUptime + PROCESS_SLICE) {
            Logger.print("Drop the fast press");
            return true;
        }
        int fgId = -1;
        switch (v.getId()) {
        /*
        case R.id.zoom_out:
            fgId = R.id.zoom_out_fg;
            if (isTouchUp) {
                delayHideStatusBar(false);
                doZoomOut();
            }
            break;
        case R.id.zoom_in:
            fgId = R.id.zoom_in_fg;
            if (isTouchUp) {
                delayHideStatusBar(false);
                doZoomIn();
            }
            break;
         
        case R.id.rotate_left:
            fgId = R.id.rotate_left_fg;
            if (isTouchUp) {
                delayHideStatusBar(false);
                doRotateLeft();
            }
            break;
            */
        case R.id.rotate_right:
           // fgId = R.id.rotate_right_fg;
            if (isTouchUp) {
                delayHideStatusBar(false);
                doRotateRight();
            }
            break;
        case R.id.prev:
           // fgId = R.id.prev_fg;
            if (isTouchUp) {
                delayHideStatusBar(false);
                doPrev(true);
            }
            break;
        case R.id.next:
            //fgId = R.id.next_fg;
            if (isTouchUp) {
                delayHideStatusBar(false);
                doNext(true);
            }
            break;
            /*
        case R.id.cicle:
           // fgId = R.id.cicle_fg;
            if (isTouchUp) {
                delayHideStatusBar(false);
                doCicle();
            }
            break;
        case R.id.shuffle:
           // fgId = R.id.shuffle_fg;
            if (isTouchUp) {
                delayHideStatusBar(false);
                doShuffle();
            }
            break;
            */
        case R.id.play:
           // fgId = R.id.play_fg;
            if (isTouchUp) {
            	ShowImgActivity ctx = (ShowImgActivity) mShowImgActivity.get();
                if (ctx != null) {
                	if(!ctx.getPlayStatus().isPlaying()){
                		delayHideStatusBar(true);
                	}
                }                
                doPlay();
            }
            break;
        case R.id.back:
            //fgId = R.id.grid_fg;
            if (isTouchUp) {
                doShowThumbnails();
            }
            break;
        default:
            return false;
        }
        if (isTouchUp) {
            mLastProcessUptime = SystemClock.uptimeMillis();
        }

        //updateButtonForeground(fgId, isTouchDown);
        return true;
    }
    
    public void keyActionPrevious(){
         //delayHideStatusBar(false);
         doPrev(false);
    }
    
    public void keyActionNext(){
         //delayHideStatusBar(false);
         doNext(false);  
    }
    
    public void keyActionPlay(){
         //delayHideStatusBar(true);
         doPlay();
    }

    private void doShowThumbnails() {
        ShowImgActivity owner = (ShowImgActivity) mShowImgActivity.get();
        if (owner == null) {
            return;
        }
        owner.jumpToGrid();
    }

    private void updateButtonForeground(int id, boolean isShow) {
        ShowImgActivity owner = (ShowImgActivity) mShowImgActivity.get();
        if (owner == null) {
            return;
        }
        ImageView imgFg = (ImageView) owner.findViewById(id);
        if (isShow) {
            imgFg.setVisibility(View.VISIBLE);
        } else {
            imgFg.setVisibility(View.INVISIBLE);
        }
    }

    private void doZoomOut() {
        ShowImgActivity owner = (ShowImgActivity) mShowImgActivity.get();
        if (owner == null) {
            return;
        }
        ZoomRotateImageView curImage = (ZoomRotateImageView) owner
                .getCurrentImageView();
        if (curImage == null) {
            return;
        }
        PlayStatus ps = owner.getPlayStatus();
        ps.pause();
        curImage.zoomOut();

        // change contents of the rear view
        PicturePresentation presentation = owner.getPicturePresentation();
        if (null != presentation) {
            ZoomRotateImageView currentImage = (ZoomRotateImageView) presentation
                    .getCurImage();
            if (null != currentImage) {
                currentImage.zoomOut();
            }
        }
    }

    private void doZoomIn() {
        ShowImgActivity owner = (ShowImgActivity) mShowImgActivity.get();
        if (owner == null) {
            return;
        }
        ZoomRotateImageView curImage = (ZoomRotateImageView) owner
                .getCurrentImageView();
        if (curImage == null) {
            return;
        }
        PlayStatus ps = owner.getPlayStatus();
        ps.pause();
        curImage.zoomIn();

        // change contents of the rear view
        PicturePresentation presentation = owner.getPicturePresentation();
        if (null != presentation) {
            ZoomRotateImageView currentImage = (ZoomRotateImageView) presentation
                    .getCurImage();
            if (null != currentImage) {
                currentImage.zoomIn();
            }
        }
    }

    private void doRotateLeft() {
        ShowImgActivity owner = (ShowImgActivity) mShowImgActivity.get();
        if (owner == null) {
            return;
        }
        ZoomRotateImageView curImage = (ZoomRotateImageView) owner
                .getCurrentImageView();
        if (curImage == null) {
            return;
        }
        PlayStatus ps = owner.getPlayStatus();
        ps.pause();
        curImage.rotateLeft();

        // change contents of the rear view
        PicturePresentation presentation = owner.getPicturePresentation();
        if (null != presentation) {
            ZoomRotateImageView currentImage = (ZoomRotateImageView) presentation
                    .getCurImage();
            if (null != currentImage) {
                currentImage.rotateLeft();
            }
        }
    }

    private void doRotateRight() {
        ShowImgActivity owner = (ShowImgActivity) mShowImgActivity.get();
        if (owner == null) {
            return;
        }
        ZoomRotateImageView curImage = (ZoomRotateImageView) owner
                .getCurrentImageView();
        if (curImage == null) {
            return;
        }
        PlayStatus ps = owner.getPlayStatus();
        ps.pause();
        curImage.rotateRight();

        // change contents of the rear view
        PicturePresentation presentation = owner.getPicturePresentation();
        if (null != presentation) {
            ZoomRotateImageView currentImage = (ZoomRotateImageView) presentation
                    .getCurImage();
            if (null != currentImage) {
                currentImage.rotateRight();
            }
        }
    }

    private void doPrev(boolean pause) {
        ShowImgActivity owner = (ShowImgActivity) mShowImgActivity.get();
        if (owner == null) {
            return;
        }
        if(dataManager.get()==null){
        	return;
        }
        MediaData data = dataManager.get().getData();
        if (data == null) {
            return;
        }
        if(pause){
            PlayStatus ps = owner.getPlayStatus();
            ps.pause();
        }
        BitmapContext bmpCtx = data.prev();
        // If img = null, means the first one, then do nothing.
        if (bmpCtx != null && bmpCtx.getFilePath() != null) {
            owner.setCurrentImageASync(bmpCtx);
        }
    }

    private void doNext(boolean pause) {
        ShowImgActivity owner = (ShowImgActivity) mShowImgActivity.get();
        if (owner == null) {
            return;
        }
        if(dataManager.get()==null){
        	return;
        }
        MediaData data = dataManager.get().getData();
        if (data == null) {
            return;
        }
        if(pause){
            PlayStatus ps = owner.getPlayStatus();
            ps.pause();
        }
        BitmapContext bmpCtx = data.next();
        // If img = null, means the last one, then don nothing.
        if (bmpCtx != null && bmpCtx.getFilePath() != null) {
            owner.setCurrentImageASync(bmpCtx);
        }
    }

    private void doCicle() {
        ShowImgActivity ctx = (ShowImgActivity) mShowImgActivity.get();
        if (ctx == null) {
            return;
        }
        PlayStatus ps = ctx.getPlayStatus();
        ps.triggerLoopAll();

        // change contents of the rear view
        PicturePresentation presentation = ctx.getPicturePresentation();
        if (null != presentation) {
            ImageView circle = presentation.getCircle();
            if (null != circle) {
                circle.setImageResource(ps.isLoopAll() ? R.drawable.play_all
                        : R.drawable.play_single);
            }
        }
    }
    
    private void showCurtain(){
    	ShowImgActivity ctx = (ShowImgActivity) mShowImgActivity.get();
        if (ctx == null) {
            return;
        }
        AHandler handler = ctx.getHandler();
    	if (null != handler) {
        	handler.sendEmptyMessage(AHandler.SHOW_CURTAIN_MSG);
    	}    	
    }
    private void delayHideStatusBar(boolean now) {
        ShowImgActivity ctx = (ShowImgActivity) mShowImgActivity.get();
        if (ctx == null) {
            return;
        }

        AHandler handler = ctx.getHandler();
        if (null != handler) {
            if (handler.hasMessages(AHandler.HIDE_CURTAIN_MSG)) {
                handler.removeMessages(AHandler.HIDE_CURTAIN_MSG);
            }

            if (handler.hasMessages(AHandler.HIDE_STATUSBAR_MSG)) {
                handler.removeMessages(AHandler.HIDE_STATUSBAR_MSG);
            }

            if(now){
            	handler.sendEmptyMessageDelayed(AHandler.HIDE_STATUSBAR_MSG, 500);
            	handler.sendEmptyMessageDelayed(AHandler.HIDE_CURTAIN_MSG, 500);
            }
            else{
                // hide after 5s
                if (!handler.hasMessages(AHandler.HIDE_STATUSBAR_MSG)) {
                    handler.sendEmptyMessageDelayed(AHandler.HIDE_STATUSBAR_MSG,
                            ShowImgActivity.HIDE_LONG_TIMEOUT
                                    + ShowImgActivity.HIDE_SHORT_TIMEOUT);
                }

                if (!handler.hasMessages(AHandler.HIDE_CURTAIN_MSG)) {
                    handler.sendEmptyMessageDelayed(AHandler.HIDE_CURTAIN_MSG,
                            ShowImgActivity.HIDE_LONG_TIMEOUT);
                }
            }
        }
    }

    private void doShuffle() {
        ShowImgActivity ctx = (ShowImgActivity) mShowImgActivity.get();
        if (ctx == null) {
            return;
        }
        PlayStatus ps = ctx.getPlayStatus();
        ps.triggerShuffleMode();

        // change contents of the rear view
        PicturePresentation presentation = ctx.getPicturePresentation();
        if (null != presentation) {
            ImageView shuffle = presentation.getShuffle();
            if (null != shuffle) {
                shuffle.setImageResource(ps.isShuffleMode() ? R.drawable.shuffle
                        : R.drawable.shuffle_no);
            }
        }
    }

    private void doPlay() {
        ShowImgActivity ctx = (ShowImgActivity) mShowImgActivity.get();
        if (ctx == null) {
            return;
        }
        PlayStatus ps = ctx.getPlayStatus();
        ps.triggerPlaying();
        if (ctx != null) {
        	if(ctx.getPlayStatus().isPlaying()){
        		delayHideStatusBar(true);
        	}
        	else{
        		showCurtain();
        	}
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.rotate_right:
			delayHideStatusBar(false);
	        doRotateRight();
	         break;
	     case R.id.prev:
	    	 delayHideStatusBar(false);
	         doPrev(true);
	         break;
	     case R.id.next:
	    	 delayHideStatusBar(false);
	         doNext(true);
	         break;
	         /*
	     case R.id.cicle:
	        // fgId = R.id.cicle_fg;
	         if (isTouchUp) {
	             delayHideStatusBar(false);
	             doCicle();
	         }
	         break;
	     case R.id.shuffle:
	         delayHideStatusBar(false);
	         doShuffle();
	         break;
	         */
	     case R.id.play:	    	 
	         doPlay();
	         break;
	     case R.id.back:
	    	 doShowThumbnails();
	         break;
		}
	}

}
