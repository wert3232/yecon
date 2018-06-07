 package com.yecon.mediaservice;
 
 import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

 import com.autochips.storage.EnvironmentATC;
import com.yecon.mediaprovider.YeconMediaStore;
import com.yecon.mediaservice.MediaPlayerContants.MediaPlayerMessage;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Type;

 import java.util.ArrayList;
import java.util.List;
 
 public class MediaPlayerActivityProxy
   implements IMediaPlayerActivity, ServiceConnection
 {
   private static final String TAG = "MediaPlayerActivityProxy";
   private List<Handler> mListUIHandler;
   private static IMediaPlayerService mService;
   private static Context mContext;
   private static String mStorage;
   private static int mMediaType = 1;
   private static EnvironmentATC mEnv;
 
   public MediaPlayerActivityProxy(Context context)
   {
     this.mListUIHandler = new ArrayList();
     mContext = context;
     mEnv = new EnvironmentATC(mContext);
   }
 
   public void bindMediaPlayService()
   {
     if (mService == null) {
       Intent intentService = new Intent();
       intentService.setClass(mContext, MediaPlayerServiceProxy.class);
       if (mContext.bindService(intentService, this, 1)){
         Log.e("MediaPlayerActivityProxy", "Bind Service Success!");
       }else{
         Log.e("MediaPlayerActivityProxy", "Bind Service Failed!");
	   }
     }else {
       Log.e("MediaPlayerActivityProxy", "Application Has been Binded Media Service!!!");
     }
   }
 
   public void unbindMediaPlayService()
   {
     if (mContext != null) {
       if (isBindService()) {
         try {
           mService.unregisterUserInterface(this);
           mService.UnregisterSource();
         } catch (RemoteException e) {
           e.printStackTrace();
         }
       }
       mContext.unbindService(this);
       mService = null;
     }
     this.mListUIHandler.clear();
   }
 
   public void RegisterHandler(Handler handler)
   {
     for (Handler temphandler : this.mListUIHandler) {
       if (temphandler.equals(handler)) {
         return;
       }
     }
     this.mListUIHandler.add(handler);
   }
 
   public void UnRegisterHandler(Handler handler)
   {
     for (Handler temphandler : this.mListUIHandler){
	       if (temphandler.equals(handler)) {
	         this.mListUIHandler.remove(handler);
	         return;
	       }
   		}
 	}
   public void updatePlayStatus(int iStatus)
     throws RemoteException
   {
     for (Handler handler : this.mListUIHandler) {
       Message msg = Message.obtain();
       msg.what = 2;
       msg.arg1 = iStatus;
       handler.sendMessage(msg);
     }
   }
 
   public void updateRepeatStatus(int iStatus)
     throws RemoteException
   {
     for (Handler handler : this.mListUIHandler) {
       Message msg = Message.obtain();
       msg.what = 4;
       msg.arg1 = iStatus;
       handler.sendMessage(msg);
     }
   }
 
   public void updateRandomStatus(int iStatus)
     throws RemoteException
   {
     for (Handler handler : this.mListUIHandler) {
       Message msg = Message.obtain();
       msg.what = 3;
       msg.arg1 = iStatus;
       handler.sendMessage(msg);
     }
   }
 
   public void updateServiceStatus(int iStatus, String strData)
     throws RemoteException
   {
     for (Handler handler : this.mListUIHandler) {
       Message msg = Message.obtain();
       msg.what = MediaPlayerMessage.UPDATE_SERVICE_STATE;
       msg.arg1 = iStatus;
       if ((strData != null) && (!strData.isEmpty())) {
         Bundle data = new Bundle();
         data.putString("path", strData);
         msg.setData(data);
       }
       handler.sendMessage(msg);
     }
   }
 
   public void updateListData(int iList)
     throws RemoteException
   {
     for (Handler handler : this.mListUIHandler) {
       Message msg = Message.obtain();
       msg.what = 1;
       msg.arg1 = iList;
       handler.sendMessage(msg);
     }
   }
 
   public void updateTimeProcess(int iProgress, int iDuration)
     throws RemoteException
   {
     for (Handler handler : this.mListUIHandler) {
       Message msg = Message.obtain();
       msg.what = MediaPlayerContants.MediaPlayerMessage.UPDATE_PLAY_PROGRESS;
       msg.arg1 = iProgress;
       msg.arg2 = iDuration;
       handler.sendMessage(msg);
     }
   }
 
   public void updateMediaInfo(int iInfo, int iExtra)
     throws RemoteException
   {
     for (Handler handler : this.mListUIHandler) {
       Message msg = Message.obtain();
       msg.what = 6;
       msg.arg1 = iInfo;
       msg.arg2 = iExtra;
       handler.sendMessage(msg);
     }
   }
 
   public void onServiceConnected(ComponentName name, IBinder service)
   {
     if (service != null) {
       mService = (IMediaPlayerService)service;
       try {
         mService.RegisterSource(mMediaType);
         mService.registerUserInterface(this);
         AttachStorage(mStorage, mMediaType);
       }
       catch (RemoteException e) {
         e.printStackTrace();
       }
     }
   }
 
   public void onServiceDisconnected(ComponentName name)
   {
     mService = null;
     Log.e("MediaPlayerActivityProxy", "onServiceDisconnected");
   }
 
   public boolean AttachStorage(String storage, int mediaType)
   {
     mStorage = storage;
     mMediaType = mediaType;
     if (isBindService()) {
       try {
         return mService.requestAttachStorage(mStorage);
       } catch (RemoteException e) {
         e.printStackTrace();
       }
     }
     return false;
   }
 
   public IMediaPlayerService getMediaSevice()
   {
     return mService;
   }
 
   public boolean isBindService() {
     return mService != null;
   }
 
   public int getMediaType() {
     return mMediaType;
   }
 
   public List<MediaObject> getMediaList(int iList)
   {
     List<MediaObject>  ls = null;
     if (isBindService()) {
       try {
         switch (iList) {
         case Type.PLAY_FILE:
           ls = mService.getPlayList();
           break;
         case Type.ALL_FILE:
           ls = mService.getAllFileList();
           break;
         case Type.ALL_DIR:
           ls = mService.getDirList();
           break;
         case Type.ALL_ALBUM:
           ls = mService.getAlbumList();
           break;
         case Type.ALL_ARTIST:
           ls = mService.getArtistList();
           break;
         case Type.DIR_FILE:
           ls = mService.getCurDirFileList();
           break;
         case Type.ALBUM_FILE:
           ls = mService.getCurAlbumFileList();
           break;
         case Type.ARTIST_FILE:
           ls = mService.getCurArtistFileList();
           break;
         case Type.FAVORITE_FILE:
           ls = mService.getFavoriteFileList();
           break;
         default:
           ls = mService.getPlayList();
         }
       }
       catch (RemoteException e) {
         e.printStackTrace();
       }
     }
     return ls;
   }
 
   public boolean isDeviceExist(String device)
   {
     return YeconMediaStore.checkStorageExist(mEnv, device);
   }
 }

