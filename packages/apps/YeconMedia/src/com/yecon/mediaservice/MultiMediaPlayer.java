 package com.yecon.mediaservice;
 
 import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnTimedTextListener;
import android.media.TimedText;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.SurfaceHolder;

import com.autochips.media.AtcMediaPlayer;
import com.autochips.media.AtcMediaPlayer.OnSetRateCompleteListener;

import java.io.IOException;
import java.lang.ref.WeakReference;

import org.json.JSONObject;
 
 @SuppressLint({"NewApi"})
 public class MultiMediaPlayer
   implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, AtcMediaPlayer.OnSetRateCompleteListener, MediaPlayer.OnTimedTextListener, Handler.Callback
 {
   public static final String[] PLAY_STATE = { 
     "IDLE", "DECODING", "DECODED", "SEEKING", "STATED", "PAUSED", "STOPED", "ERROR", "FINISH" };
   public static final String PROPERTY_KEY_CBMSOURCE = "persist.sys.cbmsource";
   public static final int MSG_MEDIA_UPDATE_STATE = 0;
   public static final int MSG_MEDIA_DECODE = 1;
   public static final int MSG_MEDIA_PLAY = 2;
   public static final int MSG_MEDIA_PAUSE = 3;
   public static final int MSG_MEDIA_SEEK = 4;
   public static final int MSG_MEDIA_STOP = 5;
   public static final int MSG_MEDIA_FRONT = 6;
   public static final int MSG_MEDIA_REAR = 7;
   public static final int MSG_MEDIA_PROGRESS = 8;
   public static final int MSG_MEDIA_INFO = 9;
   public static final int MEDIA_INFO_VIDEO_RENDERING_START = 1;
   public static final int MEDIA_INFO_NOT_SEEKABLE = 2;
   public static final int MEDIA_INFO_UNSUPPORTED_VIDEO = 3;
   public static final int MEDIA_INFO_UNSUPPORTED_AUDIO = 4;
   public static final int MEDIA_INFO_DIVXDRM_ERROR = 5;
   public static final int CAP_FILE_SEEK_UNSUPPORT = 1;
   public static final int CAP_FILE_FF_UNSUPPORT = 2;
   public static final int CAP_FILE_RW_UNSUPPORT = 4;
   public static final int CAP_VIDEO_RESOLUTION_UNSUPPORT = 8;
   public static final int CAP_VIDEO_BITRATE_UNSUPPORT = 16;
   public static final int CAP_VIDEO_FRAMERATE_UNSUPPORT = 32;
   public static final int CAP_VIDEO_CODEC_UNSUPPORT = 64;
   public static final int CAP_VIDEO_PROFILE_LEVEL_UNSUPPORT = 128;
   public static final int CAP_AUDIO_BITRATE_UNSUPPORT = 256;
   public static final int CAP_AUDIO_SAMPLERATE_UNSUPPORT = 512;
   public static final int CAP_AUDIO_CODEC_UNSUPPORT = 1024;
   public static final int CAP_AUDIO_PROFILE_LEVEL_UNSUPPORT = 2048;
   public static final int[] HANDLE_ACTION = { 
     1, 
     2, 
     3, 
     4, 
     5, 
     6, 
     7, 
     8 };
 
   public static int DELAY_EXEC_TIME = 200;
 
   public boolean DEBUG = true;
   private static final String TAG = "YeconMultiMediaPlayer";
   private MediaPlayer mSysMediaPlayer;
   private AtcMediaPlayer mAtcMediaPlayer;
   private boolean mbInitialize = false;
   private int mDuration = 0;
   private IMultiMediaPlayer mPlayerClient;
   private int miPlayState = 0;
   private Context mContext = null;
   private Handler mHandler;
   private PowerManager.WakeLock mWakeLock;
   private String mSourceFile = "";
   private WeakReference<SurfaceHolder> mSurfaceFront;
   private WeakReference<SurfaceHolder> mSurfaceRear;
   private SharedPreferences mPrf;
   public MultiMediaPlayer(IMultiMediaPlayer playerClient, Context context)
   {
     this.mPlayerClient = playerClient;
     this.mContext = context;
     this.mHandler = new Handler(this);
     mPrf = context.getSharedPreferences(MediaPlayerContants.LAST_MEMORY_MEDIA_INFO, Context.MODE_PRIVATE);
     init();
   }
 
   private void init() {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "++init++:" + this);
       if (this.mSysMediaPlayer == null) {
         this.mSysMediaPlayer = new MediaPlayer();
       }
       this.mSysMediaPlayer.setOnCompletionListener(this);
       this.mSysMediaPlayer.setOnErrorListener(this);
       this.mSysMediaPlayer.setOnInfoListener(this);
       this.mSysMediaPlayer.setOnPreparedListener(this);
       this.mSysMediaPlayer.setOnSeekCompleteListener(this);
 
       if (this.mAtcMediaPlayer == null) {
         this.mAtcMediaPlayer = new AtcMediaPlayer(this.mSysMediaPlayer);
         this.mAtcMediaPlayer.openAudioOutput(3);
       }
       this.mAtcMediaPlayer.setOnSetRateCompleteListener(this);
       Log.e("YeconMultiMediaPlayer", "--init--");
     }
   }
 
   private void deinit() {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "++deinit++");
       this.mbInitialize = false;
       clearMessage();
       if (this.mAtcMediaPlayer != null) {
         if (this.mAtcMediaPlayer != null) {
           this.mAtcMediaPlayer.closeAudioOutput(3);
           this.mAtcMediaPlayer.clearDivxService();
         }
         this.mAtcMediaPlayer.setOnSetRateCompleteListener(null);
         this.mAtcMediaPlayer = null;
       }
       if (this.mSysMediaPlayer != null) {
         this.mSysMediaPlayer.setOnCompletionListener(null);
         this.mSysMediaPlayer.setOnErrorListener(null);
         this.mSysMediaPlayer.setOnInfoListener(null);
         this.mSysMediaPlayer.setOnPreparedListener(null);
         this.mSysMediaPlayer.setOnSeekCompleteListener(null);
         this.mSysMediaPlayer.reset();
         this.mSysMediaPlayer.release();
         this.mSysMediaPlayer = null;
       }
       Log.e("YeconMultiMediaPlayer", "--deinit--");
     }
   }
 
   public void setVolume(float fVolume) {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "setVolume");
       if (this.mSysMediaPlayer != null) {
         this.mSysMediaPlayer.setVolume(fVolume, fVolume);
       }
     }
   }
 
   private void clearMessage(){
     if (this.mHandler != null)
       for (int iMsgWhat : HANDLE_ACTION)
         this.mHandler.removeMessages(iMsgWhat);
   }
 
   public String getSourcePath(){
     return this.mSourceFile;
   }
 
   public boolean decode(String strFile)
   {
     clearMessage();
     Message msg = Message.obtain();
     msg.what = 1;
     Bundle data = new Bundle();
     data.putString("file", strFile);
     msg.setData(data);
     return this.mHandler.sendMessage(msg);
   }
 
   private boolean execDecode(String strFile) {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "++execDecode++ ->" + strFile);
       try {
         init();
         this.miPlayState = MediaPlayerContants.PlayStatus.IDLE;
         this.mHandler.removeMessages(MediaPlayerContants.ServiceStatus.QB_POWER );
         this.mHandler.sendEmptyMessage(MediaPlayerContants.ServiceStatus.QB_POWER );
         if (this.mSysMediaPlayer != null) {
           this.mSysMediaPlayer.reset();
         }
         setDuration(0);
         this.mSysMediaPlayer.setDataSource(strFile);
         this.mSourceFile = strFile;
         this.miPlayState = MediaPlayerContants.PlayStatus.DECODING;
         this.mbInitialize = prepare();
         if (this.mbInitialize) {
           Log.e("YeconMultiMediaPlayer", "decode : " + strFile + " success!");
         } else {
           this.miPlayState = MediaPlayerContants.PlayStatus.ERROR;
           Log.e("YeconMultiMediaPlayer", "decode : " + strFile + " failed!");
           updatePlayStatus();
         }
       } catch (IllegalArgumentException e) {
         this.miPlayState = MediaPlayerContants.PlayStatus.ERROR;
         e.printStackTrace();
       } catch (SecurityException e) {
         this.miPlayState = MediaPlayerContants.PlayStatus.ERROR;
         e.printStackTrace();
       } catch (IllegalStateException e) {
         this.miPlayState = MediaPlayerContants.PlayStatus.ERROR;
         e.printStackTrace();
       } catch (IOException e) {
         this.miPlayState = MediaPlayerContants.PlayStatus.ERROR;
         e.printStackTrace();
         Log.e("YeconMultiMediaPlayer", "file is not exist!!!");
       }
       Log.e("YeconMultiMediaPlayer", "--execDecode--" + this.mbInitialize);
       return this.mbInitialize;
     }
   }
 
   public void setFrontDisplay(SurfaceHolder holder)
   {
     synchronized ("YeconMultiMediaPlayer") {
       if (holder == null){
         this.mSurfaceFront = null;
       }else {
         this.mSurfaceFront = new WeakReference(holder);
       }
       execFrontDisplay();
     }
   }
 
   private void execFrontDisplay() {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "++execFrontDisplay:" + this.miPlayState);
       SurfaceHolder holder = null;
       if (this.mbInitialize) {
         if (this.mSurfaceFront != null) {
           holder = (SurfaceHolder)this.mSurfaceFront.get();
         }
         if (holder != null) {
           this.mSysMediaPlayer.setDisplay(holder);
           if (this.miPlayState == 5) {
             try {
               setVolume(0.0F);
               this.mSysMediaPlayer.seekTo(this.mSysMediaPlayer.getCurrentPosition());
               setVolume(1.0F);
             } catch (Exception e) {
               e.printStackTrace();
             }
           }
         } else {
           this.mSysMediaPlayer.setDisplay(null);
         }
       }
       Log.e("YeconMultiMediaPlayer", "--execFrontDisplay:" + holder);
     }
   }
 
   public void setRearDisplay(SurfaceHolder holder)
   {
     synchronized ("YeconMultiMediaPlayer") {
       if (holder == null) {
         this.mSurfaceRear = null;
       } else {
         this.mSurfaceRear = new WeakReference(holder);
       }
       execRearDisplay();
     }
   }
 
   private void execRearDisplay() {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "++execRearDisplay");
       SurfaceHolder holder = null;
       if (this.mbInitialize) {
         if (this.mSurfaceRear != null) {
           holder = (SurfaceHolder)this.mSurfaceRear.get();
         }
         if (holder != null) {
           this.mAtcMediaPlayer.setRearDisplay(holder);
         }else {
           this.mAtcMediaPlayer.setRearDisplay(null);
         }
       }
       Log.e("YeconMultiMediaPlayer", "--exeRearDisplay:" + holder);
     }
   }
 
   public void play()
   {
     this.mHandler.sendEmptyMessageDelayed(2, DELAY_EXEC_TIME);
   }
 
   private void execPlay() {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "execPlay:" + this.mbInitialize);
       try {
         if (this.mbInitialize) {
           this.mSysMediaPlayer.start();
           setVolume(1.0F);
           this.miPlayState = 4;
           updatePlayStatus();
         }
       } catch (Exception e) {
         e.printStackTrace();
       }
     }
   }
 
   public void pause()
   {
     this.mHandler.sendEmptyMessageDelayed(3, DELAY_EXEC_TIME);
   }
 
   private void execPause() {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "execPause");
       try {
         if (this.mbInitialize) {
           this.mSysMediaPlayer.pause();
           this.miPlayState = 5;
           updatePlayStatus();
         }
       } catch (Exception e) {
         e.printStackTrace();
       }
     }
   }
 
   public void stop()
   {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "stop");
       try {
         this.mHandler.removeMessages(8);
         if (this.mbInitialize) {
           this.mSysMediaPlayer.stop();
           updatePlayStatus();
         }
       } catch (Exception e) {
         e.printStackTrace();
       }
     }
   }
 
   public void seek(int msec)
   {
     Log.e("YeconMultiMediaPlayer", "seek");
     Message msg = Message.obtain();
     msg.what = 4;
     msg.arg1 = msec;
     this.mHandler.sendMessageDelayed(msg, DELAY_EXEC_TIME / 2);
   }
 
   private void execSeek(int msec) {
     synchronized ("YeconMultiMediaPlayer") {
       msec *= 1000;
       Log.e("YeconMultiMediaPlayer", "execSeek to " + msec + " mbInitialize:" + this.mbInitialize);
       if (this.mbInitialize) {
         try {
           this.mSysMediaPlayer.seekTo(msec);
           this.miPlayState = 3;
           updatePlayStatus();
         } catch (Exception e) {
           e.printStackTrace();
         }
       }
     }
   }
 
   public void reset(){
     Log.e("YeconMultiMediaPlayer", "reset");
     deinit();
     try {
       Thread.sleep(100L);
     } catch (Exception e) {
       e.printStackTrace();
     }
     init();
     this.miPlayState = 0;
   }
 
   public void release() {
     Log.e("YeconMultiMediaPlayer", "release");
     deinit();
     this.miPlayState = 0;
     this.mbInitialize = false;
   }
 
   private boolean prepare()
   {
     synchronized ("YeconMultiMediaPlayer") {
       Log.e("YeconMultiMediaPlayer", "prepare");
       boolean bRet = false;
       try {
         this.mSysMediaPlayer.prepare();
         bRet = true;
       } catch (IllegalStateException e) {
         e.printStackTrace();
         Log.e("YeconMultiMediaPlayer", "illegal state!!!");
       } catch (IOException e) {
         e.printStackTrace();
         Log.e("YeconMultiMediaPlayer", "file is not exist!!!");
       }
       return bRet;
     }
   }
 
   public void onCompletion(MediaPlayer mp)
   {
     Log.e("YeconMultiMediaPlayer", "onCompletion!!!");
     if (this.mbInitialize) {
       this.miPlayState = 8;
       updatePlayStatus();
     }
   }
 
   public boolean onError(MediaPlayer mp, int what, int extra)
   {
     Log.e("YeconMultiMediaPlayer", "onError what:" + what + " extra:" + extra + "mbInitialize:" + this.mbInitialize);
 
     if (this.mbInitialize) {
       this.mbInitialize = false;
       this.miPlayState = 7;
       updatePlayStatus();
     }
     return false;
   }
 
   public boolean onInfo(MediaPlayer mp, int whatInfo, int extra)
   {
     Log.e("YeconMultiMediaPlayer", "onInfo what:" + whatInfo + " extra:" + extra);
     Message msg = Message.obtain();
     msg.what = 9;
     switch (whatInfo) {
     case 800:
       break;
     case 802:
       break;
     case 700:
       break;
     case 3:
       Log.i("YeconMultiMediaPlayer", "onInfo - MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START");
       this.mHandler.removeMessages(9);
       msg.arg1 = 1;
       break;
     case 801:
       msg.arg1 = 2;
       break;
     case 1003:
       Log.i("YeconMultiMediaPlayer", " MEDIA_INFO_DIVX Divx menu file");
       break;
     case 1000:
       msg.arg1 = 3;
       if ((extra & 0x8) != 0) {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Resolution not supported!");
         msg.arg2 = 8;
       } else if ((extra & 0x10) != 0) {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Bitrate not supported!");
         msg.arg2 = 16;
       } else if ((extra & 0x20) != 0) {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Frame Rate not supported!");
         msg.arg2 = 32;
       } else if ((extra & 0x40) != 0) {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Format not supported!");
         msg.arg2 = 64;
       } else if ((extra & 0x80) != 0) {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO -- Video Profile Level not supported!");
         msg.arg2 = 128;
       } else {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_VIDEO, extra = " + extra);
       }
 
       break;
     case 1001:
       msg.arg1 = 4;
       if ((extra & 0x400) != 0) {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO -- Audio Format not supported!");
         msg.arg2 = 1024;
       } else if ((extra & 0x100) != 0) {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO -- Audio Bitrate not supported!");
         msg.arg2 = 256;
       } else if ((extra & 0x200) != 0) {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO -- Audio Sampling Rate not supported!");
         msg.arg2 = 512;
       } else if ((extra & 0x800) != 0) {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO -- Audio Profile Level not supported!");
         msg.arg2 = 2048;
       } else {
         Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_AUDIO, extra = " + extra);
       }
 
       break;
     case 1002:
       Log.i("YeconMultiMediaPlayer", "onInfo:MEDIA_INFO_UNSUPPORTED_MENU");
       break;
     case 1004:
       Log.i("YeconMultiMediaPlayer", "onInfo  :MEDIA_INFO_DIVXDRM");
 
       break;
     case 1005:
       if ((extra == 63035) || 
         (extra == 63034) || 
         (extra == 63033)) {
         Log.i("YeconMultiMediaPlayer", "onInfo :MEDIA_ERROR_DRM_NO_LICENSE");
       } else if (extra == 63032) {
         Log.i("YeconMultiMediaPlayer", "onInfo :MEDIA_ERROR_DRM_RENTAL_EXPIRED");
       } else {
         Log.i("YeconMultiMediaPlayer", "onInfo, whatInfo=" + whatInfo + ", extra=" + extra);
       }
       msg.arg1 = 5;
       break;
     default:
       Log.i("YeconMultiMediaPlayer", "other onInfo:" + whatInfo);
     }
 
     if (msg.arg1 != 0) {
       this.mHandler.sendMessageDelayed(msg, DELAY_EXEC_TIME);
     }
 
     return false;
   }
 
   public void onSeekComplete(MediaPlayer mp)
   {
     Log.e("YeconMultiMediaPlayer", "onSeekComplete!!!");
     if (this.mbInitialize) {
       this.miPlayState = getCurrentRealState();
       updatePlayStatus();
     }
   }
   public void onPrepared(MediaPlayer mp)
   {
     Log.e("YeconMultiMediaPlayer", "onPrepared!!!");
     if (this.mbInitialize) {
       this.miPlayState = 2;
       this.mHandler.sendEmptyMessageDelayed(8, 1000L);
       //FIXME:yfzhang 在这里可以设置进度
       //Log.e("YeconMultiMediaPlayer", mSysMediaPlayer.getDuration() + "");
      
       try {
	       if(mPrf != null && mPrf.getBoolean(MediaPlayerService.getMediaStorageType(mSourceFile) + "_hasProgres", false)){
	    	   mPrf.edit().remove(MediaPlayerService.getMediaStorageType(mSourceFile) + "_hasProgres");
	    	   JSONObject jsonObject = new JSONObject(mPrf.getString(MediaPlayerService.getMediaStorageType(mSourceFile) + "_progress", ""));
	    	   int iProgress = jsonObject.getInt("iProgress") * 1000;
	    	   String iPath = jsonObject.getString("strFile");
	    	   if(iProgress >= 0 && iProgress < mSysMediaPlayer.getDuration() && mSourceFile.equals(iPath)){
	    		   
	    		   /* yfzhang 加上这里可以有插拔断点进度记忆
	    		    * mSysMediaPlayer.seekTo(iProgress);*/
	    	   }
//	    	   Log.e("YeconMultiMediaPlayer", "mSourceFile:" + mSourceFile);
//	    	   Log.e("YeconMultiMediaPlayer", "iPath:" + iPath);
//	    	   Log.e("YeconMultiMediaPlayer", "iProgress:" + iProgress);
//	    	   Log.e("YeconMultiMediaPlayer", "Duration:" + mSysMediaPlayer.getDuration());
	       }
       } catch (Exception e) {
    	   Log.e("YeconMultiMediaPlayer", e.toString());
       }
       this.mSysMediaPlayer.start();
       setDuration(this.mSysMediaPlayer.getDuration());
       updatePlayStatus();
     }
   }
   public void onSetRateComplete(MediaPlayer arg0) {
   }
   public void onTimedText(MediaPlayer mp, TimedText text)
   {
     Log.e("YeconMultiMediaPlayer", "onTimedText");
     updatePlayStatus();
   }
 
   public int getPlayState() {
     synchronized ("YeconMultiMediaPlayer") {
       if (this.mbInitialize) {
         Log.e("YeconMultiMediaPlayer", "state:" + PLAY_STATE[this.miPlayState]);
       }
       return this.miPlayState;
     }
   }
 
   public int getCurrentRealState() {
     synchronized ("YeconMultiMediaPlayer") {
       if ((this.mAtcMediaPlayer != null) && (this.mbInitialize)) {
         int realState = this.mAtcMediaPlayer.getRealCurrentState();
         switch (realState) {
         case 2:
         case 4:
         case 8:
         case 16:
         case 128:
           return 4;
         case 1:
         case 32:
           return 5;
         }
 
       }
 
       return this.miPlayState;
     }
   }
 
   @SuppressLint({"DefaultLocale"})
   public int getDuration() {
     return this.mDuration / 1000;
   }
 
   public int getProgress() {
     synchronized ("YeconMultiMediaPlayer") {
       if (this.mbInitialize) {
         return this.mSysMediaPlayer.getCurrentPosition() / 1000;
       }
     }
     return 0;
   }
 
   private void setDuration(int mDuration) {
     this.mDuration = mDuration;
   }
 
   @SuppressLint({"Wakelock"})
   public void acquireWakeLock() {
     try {
       releaseWakeLock();
       String lockName = "MediaPlay";
       PowerManager pm = (PowerManager)this.mContext.getSystemService("power");
       this.mWakeLock = pm.newWakeLock(536870913, lockName);
       this.mWakeLock.setReferenceCounted(false);
       Log.e("YeconMultiMediaPlayer", "acquire WakeLock mWakeLock.acquire:" + lockName);
       this.mWakeLock.acquire();
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 
   @SuppressLint({"Wakelock"})
   public void releaseWakeLock() {
     try {
       if (this.mWakeLock != null) {
         Log.e("YeconMultiMediaPlayer", "release WakeLock mWakeLock.release");
         this.mWakeLock.release();
         this.mWakeLock = null;
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 
   public boolean handleMessage(Message msg)
   {
     try
     {
       switch (msg.what) {
       case MediaPlayerContants.ServiceStatus.SCANING:
         execDecode(msg.getData().getString("file"));
         updatePlayStatus();
         break;
       case MediaPlayerContants.ServiceStatus.SCANED:
         if (msg.arg1 <= 3) {
           execPlay();
           if (getCurrentRealState() != 4) {
             Log.e("YeconMultiMediaPlayer", "exec play again!");
             Message remsg = Message.obtain();
             remsg.what = 2;
             msg.arg1 += 1;
             this.mHandler.sendMessageDelayed(remsg, DELAY_EXEC_TIME);
           }
         } else {
           Log.e("YeconMultiMediaPlayer", "exec play failed!");
         }
         break;
       case MediaPlayerContants.ServiceStatus.SCAN_TIMEOUT:
         if (msg.arg1 <= 3) {
           execPause();
           if (getCurrentRealState() != 5) {
             Log.e("YeconMultiMediaPlayer", "exec pause again!");
             Message remsg = Message.obtain();
             remsg.what = 3;
             msg.arg1 += 1;
             this.mHandler.sendMessageDelayed(remsg, DELAY_EXEC_TIME);
           }
         } else {
           Log.e("YeconMultiMediaPlayer", "exec pause failed!");
         }
         break;
       case MediaPlayerContants.ServiceStatus.PLAYED:
         execSeek(msg.arg1);
         break;
       case MediaPlayerContants.ServiceStatus.EMPTY_STORAGE:
         break;
       case MediaPlayerContants.ServiceStatus.LOST_AUDIO_FOCUS:
         if ((this.mbInitialize) && 
           (this.mSysMediaPlayer.isPlaying())) {
           if (this.miPlayState != 4) {
             this.miPlayState = 4;
             updatePlayStatus();
           }
           int iProgress = this.mSysMediaPlayer.getCurrentPosition() / 1000;
           setDuration(this.mSysMediaPlayer.getDuration());
           //FIXME:yfzhang 更新进度条
           this.mPlayerClient.updatePlayProgress(iProgress, getDuration());
         }
 
         if (this.mHandler != null) {
           this.mHandler.removeMessages(8);
           this.mHandler.sendEmptyMessageDelayed(8, 1000L);
         }
         break;
       case 0:
         if (this.mPlayerClient != null) {
           this.mPlayerClient.updatePlayState();
         }
         break;
       case MediaPlayerContants.ServiceStatus.QB_POWER:
         if (this.mPlayerClient != null) {
           this.mPlayerClient.updateMediaInfo(msg.arg1, msg.arg2);
         }
         break;
       }
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
     return false;
   }
 
   protected void finalize()
     throws Throwable
   {
     super.finalize();
     Log.e("YeconMultiMediaPlayer", "finalize");
     releaseWakeLock();
   }
 
   private void updatePlayStatus() {
     if (this.mHandler != null) {
       this.mHandler.sendEmptyMessage(0);
     }
   }
 
   public boolean isPlaying(){
     boolean bPlaying = false;
     synchronized ("YeconMultiMediaPlayer") {
       if ((this.mbInitialize) && (this.mSysMediaPlayer != null) && (
         (this.miPlayState == 4) || 
         (this.miPlayState == 5))) {
         bPlaying = true;
       }
     }
 
     return bPlaying;
   }
 
   public boolean isStop() {
     boolean bStop = true;
     synchronized ("YeconMultiMediaPlayer") {
       if ((this.mbInitialize) && (this.mSysMediaPlayer != null) && 
         (this.miPlayState != 0)) {
         bStop = false;
       }
     }
 
     return bStop;
   }
 }

