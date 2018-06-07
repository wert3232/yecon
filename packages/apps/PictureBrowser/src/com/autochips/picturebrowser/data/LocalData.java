package com.autochips.picturebrowser.data;

import com.autochips.picturebrowser.R;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class LocalData extends MediaData {
	private final boolean FILT_INTERTAL = false;
	public static final String INT_SDCARD_PATH = "/mnt/sdcard";
    public static final String EXT_SDCARD1_PATH = "/mnt/ext_sdcard1";
    public static final String EXT_SDCARD2_PATH = "/mnt/ext_sdcard2";
    public static final String UDISK1_PATH = "/mnt/udisk1";
    public static final String UDISK2_PATH = "/mnt/udisk2";
    public static final String UDISK3_PATH = "/mnt/udisk3";
    public static final String UDISK4_PATH = "/mnt/udisk4";
    public static final String UDISK5_PATH = "/mnt/udisk5";
    
    private static final String[] mFilePathColumn = {
            MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media._ID };
    /*
    private static final String mSelect = 
    	MediaStore.Images.Media.DATA + " like ? OR "  + 
    	MediaStore.Images.Media.DATA + " like ? OR "  + 
    	MediaStore.Images.Media.DATA + " like ? OR "  + 
    	MediaStore.Images.Media.DATA + " like ? OR "  + 
    	MediaStore.Images.Media.DATA + " like ? OR "  + 
    	MediaStore.Images.Media.DATA + " like ? OR "  + 
    	MediaStore.Images.Media.DATA + " like ?"
    ;	
    private static final String[] mSelectArgs = {
    	EXT_SDCARD1_PATH+"%",
    	EXT_SDCARD2_PATH+"%",
    	UDISK1_PATH+"%",
    	UDISK2_PATH+"%",
    	UDISK3_PATH+"%",
    	UDISK4_PATH+"%",
    	UDISK5_PATH+"%",
    };	
    */
    private static final String mSelectFilt = 
    	MediaStore.Images.Media.DATA + " NOT LIKE ?"
    ;	
    private static final String[] mSelectFiltArgs = {
    	INT_SDCARD_PATH +"%",
    };
    private static final Uri mUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private Context mContext = null;

    public LocalData(Context context, ContentResolver cr) {
        super(cr);
        mContext = context;
    }

    @Override
    public void load() {
        super.load();
        Cursor tmpCursor;
        if(FILT_INTERTAL){
        	tmpCursor = mCr.query(mUri, mFilePathColumn, mSelectFilt, mSelectFiltArgs, null);
        }
        else{
        	tmpCursor = mCr.query(mUri, mFilePathColumn, null, null, null);
        }
        int tmpCount = 0;
        if (null != tmpCursor) {
            tmpCount = tmpCursor.getCount();
        }
        int curCount = (null == mSrcCursor) ? 0 : mSrcCursor.getCount();
        if (tmpCount == curCount && 0 != curCount) {
            int cusorPosition = mSrcCursor.getPosition();
            if (-1 != cusorPosition) {
                tmpCursor.moveToPosition(cusorPosition);
            }
        }
        mSrcCursor = tmpCursor;
    }

    @Override
    public void unload() {
        super.unload();
    }

    public void reload() {
        if (mSrcCursor != null) {
            mSrcCursor.close();
            mSrcCursor = null;
        }
        mSrcCursor = mCr.query(mUri, mFilePathColumn, null, null, null);
    }

    private BitmapContext getCurrentBitmapContext() {
        if (!isDataValid())
            return null;

        int picturePathIndex = mSrcCursor.getColumnIndex(mFilePathColumn[0]);
        String picturePath = mSrcCursor.getString(picturePathIndex);
        int fileNameIndex = mSrcCursor.getColumnIndex(mFilePathColumn[1]);
        String fileName = mSrcCursor.getString(fileNameIndex);
        BitmapContext bmpCtx = new BitmapContext();
        bmpCtx.setFileName(fileName);
        bmpCtx.setFilePath(picturePath);
        return bmpCtx;
    }
    
    @Override
    public int getCount(){
    	if(mSrcCursor!=null){
    		return mSrcCursor.getCount();
    	}
    	return 0;
    }
    
    @Override
    public int getPosision(){
    	if(mSrcCursor!=null){
    		return mSrcCursor.getPosition();
    	}
    	return -1;
    }
    
    public int getPosisionByPath(String path){
    	 if (!isDataValid())
             return -1;
         int picturePathIndex = mSrcCursor.getColumnIndex(mFilePathColumn[0]);
         String picturePath;
         if(mSrcCursor.moveToFirst()){
	    		do{
	    			picturePath = mSrcCursor.getString(picturePathIndex);
	    			if(picturePath.equals(path)){
	    				return mSrcCursor.getPosition();
	    			}
	    		}while(mSrcCursor.moveToNext());
 		}
    	return -1;
    }

    @Override
    public BitmapContext next() {
        return next(true);
    }
    
    private Toast toast = null;
    private void noMorePicShow(int resID) {
    	if(toast!=null){
    		toast.cancel();
    	}
    	toast = Toast.makeText(mContext, resID, Toast.LENGTH_SHORT);
    	toast.show();
    }

    @Override
    public BitmapContext next(boolean cycle) {
        if (!isDataValid()) {
            return null;
        }
        BitmapContext bmpCtx = null;
        if (mSrcCursor.isLast()) {
            if (cycle) {
                mSrcCursor.moveToFirst();
                bmpCtx = getCurrentBitmapContext();
            } else {
                noMorePicShow(R.string.last_pic_msg);
            }
        } else {
            mSrcCursor.moveToNext();
            bmpCtx = getCurrentBitmapContext();
        }

        return bmpCtx;
    }

    @Override
    public BitmapContext prev() {
       return prev(true);
    }
    
    public BitmapContext prev(boolean cycle) {
        if (!isDataValid()) {
            return null;
        }
        if (mSrcCursor.isFirst()) {
        	if(cycle){
        		mSrcCursor.moveToLast();
				return getCurrentBitmapContext();
        	}
        	else{
        		noMorePicShow(R.string.first_pic_msg);
        		return null;
        	}
        }
        mSrcCursor.moveToPrevious();
        return getCurrentBitmapContext();
    }
    
    @Override
    public BitmapContext current() {
        if (!isDataValid()) {
            return null;
        }
        return getCurrentBitmapContext();
    }

    @Override
    public BitmapContext position(int position, boolean isMove) {
        if (!isDataValid() || (mSrcCursor.getCount() - 1) < position) {
            return null;
        }
        int last = -1;
        if (!isMove) {
            last = mSrcCursor.getPosition();
        }
        mSrcCursor.moveToPosition(position);
        BitmapContext bmpCtx = getCurrentBitmapContext();
        if (!isMove && last != -1) {
            mSrcCursor.moveToPosition(last);
        }

        return bmpCtx;
    }

    @Override
    public Uri getUri() {
        return mUri;
    }

    @Override
    public void onLoad() {
        load();
    }

    @Override
    public void onStore() {
        unload();
    }

    @Override
    public void onClear() {
        reload();
    }
}
