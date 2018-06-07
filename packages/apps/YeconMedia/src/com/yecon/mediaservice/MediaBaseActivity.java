 package com.yecon.mediaservice;
 
 import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.RemoteException;
import android.util.Log;

import com.autochips.storage.EnvironmentATC;
import com.yecon.common.ActivityApp;
import com.yecon.common.YeconEnv;
import com.yecon.mediaprovider.YeconMediaStore;

import java.util.List; 
 public abstract class MediaBaseActivity 
 extends Activity
   implements Handler.Callback
 {
   private static MediaPlayerActivityProxy mActivityProxy = null;
 
   private Handler mHandler = new Handler(this);
   
   
   private String[] storages = new String[] {
			YeconMediaStore.EXTERNAL_PATH,
			YeconMediaStore.EXT_SDCARD1_PATH,
			YeconMediaStore.EXT_SDCARD2_PATH,
			YeconMediaStore.UDISK1_PATH,
			YeconMediaStore.UDISK2_PATH,
			YeconMediaStore.UDISK3_PATH,
			YeconMediaStore.UDISK4_PATH,
			YeconMediaStore.UDISK5_PATH
	};
   
   protected void onCreate(Bundle savedInstanceState)
   {
     super.onCreate(savedInstanceState);
     if (mActivityProxy == null) {
       mActivityProxy = new MediaPlayerActivityProxy(getApplicationContext());
     }
 
     mActivityProxy.bindMediaPlayService();
 
     ActivityApp.addActivity(this);
 
     mActivityProxy.RegisterHandler(this.mHandler);
   }
 
   protected void onResume()
   {
     super.onResume();
     try {
       if (isBindService()){
         getMediaSevice().requestActiveSource();
     	}
     }catch (Exception e) {
       e.printStackTrace();
     }
   }
   
   
   @Override
	protected void onPause() {
		super.onPause();
	}
   
   protected void onDestroy()
   {
     super.onDestroy();
 
     if (mActivityProxy != null) {
       mActivityProxy.UnRegisterHandler(this.mHandler);
     }
 
     ActivityApp.removeActivity(this);
   }
 
   protected void exitApp()
   {
     if (mActivityProxy != null) {
       try {
         getMediaSevice().requestSaveLastMemory();
         getMediaSevice().requestDeactiveSource();
         if (mActivityProxy != null){
           mActivityProxy.unbindMediaPlayService();
       	 }
       }catch (RemoteException e) {
         e.printStackTrace();
       }
       ActivityApp.exitAllActivity();
       mActivityProxy = null;
     }
   }
 
   protected boolean isScaningAttachedDevice()
   {
     boolean bRet = false;
     try
     {
       if (isBindService()) {
         MediaStorage storage = getMediaSevice().getPlayingStorage();
         if (storage != null) {
           if (storage.getState().equals("scan_start")){
             bRet = true;
         	}
         }else{
           bRet = true;
       }
       }else {
         bRet = true;
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
     return bRet;
   }
 
   protected boolean AttachStorage(String strDisk, int mediaAudio)
   {
     if (mActivityProxy == null) {
       return false;
     }
     return mActivityProxy.AttachStorage(strDisk, mediaAudio);
   }
 
   public boolean isBindService()
   {
     if (mActivityProxy == null) {
       return false;
     }
     return mActivityProxy.isBindService();
   }
 
   public int getMediaType()
   {
     if (mActivityProxy == null) {
       return 1;
     }
     return mActivityProxy.getMediaType();
   }
 
   public IMediaPlayerService getMediaSevice()
   {
     if (mActivityProxy == null) {
       return null;
     }
     return mActivityProxy.getMediaSevice();
   }
 
   public List<MediaObject> getList(int iList)
   {
     if (mActivityProxy == null) {
       return null;
     }
     return mActivityProxy.getMediaList(iList);
   }
 
   public static boolean isDeviceExist(String device)
   {
     if (mActivityProxy == null) {
       return false;
     }
     return mActivityProxy.isDeviceExist(device);
   }
 
   @SuppressLint({"DefaultLocale"})
   public String formatData(int iDuration) {
     return String.format("%02d:%02d:%02d", new Object[] { 
       Integer.valueOf(iDuration / 60 / 60), Integer.valueOf(iDuration / 60 % 60), 
       Integer.valueOf(iDuration % 60) });
   }
   
   public String getOneExistStorage(){
		EnvironmentATC env = new EnvironmentATC(this);
		for (String storage : storages) {
			boolean checkResult = YeconEnv.checkStorageExist(env, storage);
			if(checkResult){
				if(storage.matches("(.*)ext_sdcard(.*)")){
					return storage;
				}
				if(storage.matches("(.*)udisk(.*)")){
					return storage;
				}
			}
		}
		return YeconMediaStore.EXTERNAL_PATH;
	}
 }

