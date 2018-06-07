 package com.yecon.mediaservice;
 
 import android.app.Service;
 import android.content.Intent;
 import android.os.IBinder;
 import android.util.Log;
 
 public class MediaPlayerServiceProxy extends Service
 {
   private static final String TAG = "MediaPlayerServiceProxy";
   private MediaPlayerService mMediaPlayerService;
 
   public void onCreate()
   {
     super.onCreate();
     try {
       if (this.mMediaPlayerService == null) {
         this.mMediaPlayerService = new MediaPlayerService(getApplicationContext());
         this.mMediaPlayerService.Initialize();
       }
     }
     catch (Exception e) {
       e.printStackTrace();
     }
     Log.e("MediaPlayerServiceProxy", "onCreate");
   }
 
   public IBinder onBind(Intent intent)
   {
     return this.mMediaPlayerService;
   }
 
   public int onStartCommand(Intent intent, int flags, int startId)
   {
     return 1;
   }
 
   public void onDestroy()
   {
     super.onDestroy();
     if (this.mMediaPlayerService != null) {
       this.mMediaPlayerService.UnInitialize();
       this.mMediaPlayerService = null;
     }
     Log.e("MediaPlayerServiceProxy", "onDestroy");
     System.exit(0);
   }
 }

