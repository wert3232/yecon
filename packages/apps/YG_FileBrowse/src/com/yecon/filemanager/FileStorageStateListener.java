package com.yecon.filemanager;

import java.io.File;

import com.autochips.storage.EnvironmentATC;
import com.yecon.common.YeconEnv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by chenchu on 15-2-5.
 */
public class FileStorageStateListener {

    private static final String Tag = "FileStorageStateListener";
    
   public static final String USB_ROOT_PATH="/storage/usbotg/";
    
    public File[] getUSBMountedPoints() {
    	File file = new File(USB_ROOT_PATH);
    	File[] result = null;
    	if (file.exists() && file.canRead() && file.isDirectory()) {
    		result = file.listFiles();
    	} else {
    		Log.e(Tag, "--------------------fail to access /storage/usb-otg/------------------");
    	}
    	return result;
    }

    public static interface FileStorageStateChangeCallBack{

        public void onMediaMounted(String point);
        public void onMediaEjected(String point);
    }

    public final static String pathSD = YeconEnv.INT_SDCARD_PATH;
    public final static String pathSD1 = YeconEnv.EXT_SDCARD1_PATH;
    public final static String pathSD2 = YeconEnv.EXT_SDCARD2_PATH;
    public final static String pathUSB1 = YeconEnv.UDISK1_PATH;
    public final static String pathUSB2 = YeconEnv.UDISK2_PATH;
    public final static String pathUSB3 = YeconEnv.UDISK3_PATH;
    public final static String pathUSB4 = YeconEnv.UDISK4_PATH;
    public final static String pathUSB5 = YeconEnv.UDISK5_PATH;

    private BroadcastReceiver mMediaStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
        	String path = intent.getData().getPath();
        	Log.d(Tag, "path is"+path);
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)){
                if (mCallBack != null) {
                    mCallBack.onMediaMounted(path);
                }
            }  else if (action.equals(Intent.ACTION_MEDIA_EJECT)){
                if (mCallBack != null) {
                	mCallBack.onMediaEjected(path);
                }
        }
        }
    };

    private Context mContext;
    private FileStorageStateChangeCallBack mCallBack;

    public FileStorageStateListener(Context context,FileStorageStateChangeCallBack callBack) {
        mContext = context;
        mCallBack = callBack;
    }

    public void register() {
        if (mContext != null) {
            IntentFilter mediaStateFilter = new IntentFilter();
            mediaStateFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            mediaStateFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            mediaStateFilter.addDataScheme("file");
            mContext.registerReceiver(mMediaStateReceiver, mediaStateFilter);
        }
    }

    public void unregister() {
        if (mContext != null) {
            mContext.unregisterReceiver(mMediaStateReceiver);
        }
    }
    
    public boolean isSD1Mounted() {
    	return isMounted(pathSD1);
    }
    public boolean isSD2Mounted() {
    	return isMounted(pathSD2);
    }
    
    public boolean isUsb1Mounted() {
    	return isMounted(pathUSB1);
    }
    public boolean isUsb2Mounted() {
    	return isMounted(pathUSB2);
    }
    public boolean isUsb3Mounted() {
    	return isMounted(pathUSB3);
    }
    public boolean isUsb4Mounted() {
    	return isMounted(pathUSB4);
    }
    public boolean isUsb5Mounted() {
    	return isMounted(pathUSB5);
    }

  public boolean isMounted(String path) {
	  if (path != null && path.length() > 0) {
		  EnvironmentATC envATC = new EnvironmentATC(mContext);
		  return YeconEnv.checkStorageExist(envATC, path);  
	  }else {
		  return false;
	  }	  
  }
  
}
