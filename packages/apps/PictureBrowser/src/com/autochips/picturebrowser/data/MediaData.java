package com.autochips.picturebrowser.data;

import com.autochips.picturebrowser.DataNotify;
import com.autochips.picturebrowser.status.PreludeStatus;
import com.autochips.picturebrowser.status.StatusHub;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public abstract class MediaData extends PreludeStatus {

    protected ContentResolver mCr = null;
    protected Cursor mSrcCursor = null;

    // update three activity when ContentObserver changed
    private DataNotify mNotifyShow = null;
    private DataNotify mNotifyGrid = null;

    private DataContentObserver mDataObserver = null;
    private DirtyHandler mDH = null;

    public MediaData(ContentResolver cr) {
        mCr = cr;
    }

    public void load() {
        if (mDataObserver == null) {
            mDH = new DirtyHandler();
            mDataObserver = new DataContentObserver(mDH);
        }
        if(mCr!=null){
        	 mCr.registerContentObserver(getUri(), false, mDataObserver);
        }       
    }

    public void unload() {
        if (mSrcCursor != null) {
            mSrcCursor.close();
            mSrcCursor = null;
        }
        if (mDataObserver != null && mCr!=null) {
            mCr.unregisterContentObserver(mDataObserver);
        }
    }

    public abstract Uri getUri();

    public abstract BitmapContext next();

    public abstract BitmapContext next(boolean cycle);

    public abstract BitmapContext prev();

    public abstract BitmapContext current();

    public abstract BitmapContext position(int position, boolean isMove);

    public int size() {
        if (!isDataValid()) {
            return 0;
        }
        return mSrcCursor.getCount();
    }

    public void reset() {
        if (!isDataValid()) {
            return;
        }
        mSrcCursor.moveToFirst();
    }

    public boolean isDataValid() {
        if (mSrcCursor == null || mSrcCursor.getCount() <= 0) {
            return false;
        }
        return true;
    }

    public void setNotifyShowImg(DataNotify notify) {
        mNotifyShow = notify;
    }
    
    public DataNotify getNotifyShowImg() {
        return mNotifyShow;
    }
    
    public void setNotifyGrid(DataNotify notify) {
        mNotifyGrid = notify;
    }
    
    public DataNotify getNotifyGrid() {
    	return mNotifyGrid;
    }

    private static final int HANDLE_DIRTY_MSG = 0;
    private boolean mNeedLastHandle = false;
    private static final int HANDLE_DIRTY_TIMEOUT = 3000; // 3s

    private class DirtyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLE_DIRTY_MSG:
                if (mNeedLastHandle) {
                    handleDirtyContent();
                    mNeedLastHandle = false;
                }
                break;
            }
        }
    }

    private void handleDirtyContent() {
        mDH.sendEmptyMessageDelayed(HANDLE_DIRTY_MSG, HANDLE_DIRTY_TIMEOUT);
        int oldSize = this.size();
        StatusHub.loadDataSource();
        int newSize = this.size();

        if (oldSize == newSize) {
            return;
        }

        if (mNotifyShow != null) {
            mNotifyShow.onDataChanged();
        }

        if (mNotifyGrid != null) {
            mNotifyGrid.onDataChanged();
        }
    }

    // If there are many pictures in SDCard/UDisk, the onChange would be revoked
    // frequently which leads ANR.
    // So decrease it : every HANDLE_DIRTY_TIMEOUT time response once.
    private class DataContentObserver extends ContentObserver {
        public DataContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mDH.hasMessages(HANDLE_DIRTY_MSG)) {
                mNeedLastHandle = true;
            } else {
                handleDirtyContent();
            }
        }

    }

	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPosision() {
		// TODO Auto-generated method stub
		return 0;
	}

	public abstract int getPosisionByPath(String lastPlayPath);

}
