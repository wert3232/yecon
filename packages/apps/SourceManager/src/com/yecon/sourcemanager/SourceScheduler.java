package com.yecon.sourcemanager;

import java.io.File;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.autochips.storage.EnvironmentATC;
import com.autochips.whitelist.WhiteList;
import com.yecon.common.SourceManager;
import com.yecon.common.YeconEnv;

import static android.constant.YeconConstants.*;

public class SourceScheduler {
		private static final String TAG =  "SourceManager";
		private static final int MSG_START_TOP_PENDING_SOURCE = 0;
		private static final int MSG_CHECK_MEDIA_SOURCE = 1;
		private static final int MSG_BOOTANIM_EXIT = 2;
		private static final int MSG_ID_SET_VOLUME = 3;
		
		private String musicPlayingDev = "";
		private String videoPlayingDev = "";
		private final String  PREFS = "source_manager_prefs";
		private final String MUSIC_PLAYING_PREFS = "music_playing_path";
		private final String VIDEO_PLAYING_PREFS = "video_playing_path";
		private final String RESUME_NAVI_FLAG_PREFS = "resume_navi";
		private SharedPreferences mPrefs;
		
		private static final int MSG_CHECK_AQUIRE_MEDIA_BUTTON = 1000;
		
		private static final int DELAY_START_SRC = 2000;
		
		private static final int MEDIA_CHECK_MAX_COUNT = 5;
		private int mMediaCheckCount;
		
		//private final int MSG_START_SOURCE = 1;
		private Context context;
		private Handler handler, handlerMediaKey;
		private EnvironmentATC env;
		private int powerState = POWER_STATE.accon;
		private AudioManager mAudioManager;
		
		private McuManager mMcuManager;
		
		/* from SourceManager.java
		 * public  enum SRC_NO{
		;
		public static int radio = 0,
		music = 1,
		video = 2,
		bluetooth = 3,
		dvd = 4,
		dvr = 5,
		dtv = 6,
		atv = 7,
		avin = 8,
		ipod = 9,
		phonelink = 10,
		backcar = 11,
		bt_phone = 12,
		max = 13;
	};
		 */	
		public  enum POWER_STATE{
			;
			public static int accon = 0,
			accoff = 1,
			poweroff = 2,
			poweron=3;
		};
	
	public final static String sourcePackets[] = new String[] {
	        FMRADIO_PACKAGE_NAME,
	        MUSIC_PACKAGE_NAME, 
	        VIDEO_PACKAGE_NAME,
	        BLUETOOTH_PACKAGE_NAME, 
	        DVD_PACKAGE_NAME, 
	        DVR_PACKAGE_NAME, 
	        DTV_PACKAGE_NAME, 
	        ATV_PACKAGE_NAME, 
	        AVIN_PACKAGE_NAME,
	        AVIN_EXT_PACKAGE_NAME,
	        IPOD_PACKAGE_NAME,
			"",
			"",
			PICBROWSER_PACKAGE_NAME,
			"",
			"",
			"",
			"",
			"",
			"",
		};
	public final static String sourceDefaultActivity[] = new String[] {
	        FMRADIO_START_ACTIVITY,
	        MUSIC_START_ACTIVITY,
			VIDEO_START_ACTIVITY,
			BLUETOOTH_START_ACTIVITY,
			DVD_START_ACTIVITY,
			DVR_START_ACTIVITY, 
			DTV_START_ACTIVITY, 
			ATV_START_ACTIVITY, 
			AVIN_START_ACTIVITY, 
			IPOD_START_ACTIVITY,
			"",
			"",
			"",
			PICBROWSER_START_ACTIVITY,
			"",
			"",
			"",
			"",
			"",
		};
	
	public static final int SRC_HOTPLUG[]={
		SourceManager.SRC_NO.music, SourceManager.SRC_NO.video, SourceManager.SRC_NO.dvd, SourceManager.SRC_NO.ipod
	};
	
	public enum SRC_KEY{
		usb(0),
		max(1);
		private int value;
		SRC_KEY(int val){
			this.value = val;
		}
		int getValue(){
			return value;
		}
	};
	
	private static boolean isHotPlug(int srcNo){
		for(int n:SRC_HOTPLUG){
			if(n == srcNo)
				return true;
		}
		return false;
	}
	
	private static boolean shouldAquire(int srcNo){
		//some source should not aquire sound, for example: photo
		/* yfzhang
		 * if(srcNo == SourceManager.SRC_NO.photo)
			return false;*/
		return true;
	}
	
	private void removeSourceFromStack(int srcNo){
		
		synchronized(sources){
			Iterator<SourceInfo> it = sources.iterator();	
			SourceInfo si;
			while(it.hasNext()){
				si = it.next();
				if(si.getSrcNo() == srcNo){
					it.remove();
				}
			}		
		}
					
	}
	
	private void startSourceAppNow(SourceInfo src){
		int srcNo = src.getSrcNo();
		if(canStartSourceNow(src)){
			if(SourceManagerService.DEBUG){
				Log.i(TAG, "startSourceAppNow source:"+ srcNo);
			}
			src.setPending(0);
			Log.e(TAG, sourcePackets[srcNo] + "|" + sourceDefaultActivity[srcNo]);
			/*
			//send resume broadcast
			Intent intent = new Intent();
			intent.setAction(SourceManager.ACTION_SOURCE_CHANGED_NOTIFY);
			intent.putExtra(SourceManager.EXTRA_SOURCE_NO,  srcNo);
			intent.putExtra(SourceManager.EXTRA_SOURCE_ACTION,  SourceManager.SRC_ACTION.START);
			context.sendBroadcast(intent);
			*/
			//bring app to front 
			if(sourcePackets[srcNo].length()>0 && sourceDefaultActivity[srcNo].length()>0){
				if(src.getDevPath()!=null && src.getDevPath().length()>0){
					if(YeconEnv.checkStorageExist(env, src.getDevPath())){
						//check if the device is mounted.
						try{
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.putExtra("plugindevice", src.getDevPath());
				            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				            intent.addCategory("android.intent.category.LAUNCHER");
				            intent.setComponent(new ComponentName(sourcePackets[srcNo], sourceDefaultActivity[srcNo]));
				            /*Log.e("startActivity", sourcePackets[srcNo]); 
				            if(sourcePackets[srcNo].equals("com.yecon.music")){
				            	return;
				            }*/
				            context.startActivity(intent);
				            Log.e(TAG, sourcePackets[srcNo] + "|" + sourceDefaultActivity[srcNo] + "src != null");
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
					else{
						//do not start app, maybe the device is pulled out..
						removeSourceFromStack(srcNo);
					}
				}
				else{
					try{
						Intent intent = new Intent(Intent.ACTION_MAIN);
						//intent.putExtra("plugindevice", src.getDevPath());
						intent.putExtra("startTag", 1);
			            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			            intent.addCategory("android.intent.category.LAUNCHER");
			            intent.setComponent(new ComponentName(sourcePackets[srcNo], sourceDefaultActivity[srcNo]));
			            context.startActivity(intent);
			            
			            Log.e(TAG, sourcePackets[srcNo] + "|" + sourceDefaultActivity[srcNo] + "src == null");
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		else{
			if(SourceManagerService.DEBUG){
				Log.i(TAG, "start later:"+srcNo);
			}
			src.setPending(SourceInfo.PEINDING_RESUME);
		}
	}	
			
		SourceScheduler(Context cntx){
			this.context = cntx;
			env = new EnvironmentATC(cntx);
			handlerMediaKey = new Handler(context.getMainLooper()){
				@Override
				public void handleMessage(Message msg) {
					if(msg.what>=MSG_CHECK_AQUIRE_MEDIA_BUTTON 
							&& msg.what < (MSG_CHECK_AQUIRE_MEDIA_BUTTON + SourceManager.SRC_NO.max)){
						checkMediaButton(false);
					}
				}
			};
			handler = new Handler(context.getMainLooper()){

				@Override
				public void handleMessage(Message msg) {
				    Log.i(TAG, "handler - msg.what: " + msg.what);
					if(msg.what==MSG_START_TOP_PENDING_SOURCE){
					    removeMessages(MSG_CHECK_MEDIA_SOURCE);
					    
						SourceInfo si = getTopSource();
						if (si == null) {
						    return;
						}
						if(SourceManagerService.DEBUG){
							Log.i(TAG, "check pending srcNo:"+si.getSrcNo() + ":"+si.getPending());
						}
						
//						if (POWER_STATE.poweron== powerState) {
//						    if (si.getSrcNo() == SourceManager.SRC_NO.bluetooth) {
//						        sendEmptyMessageDelayed(MSG_BOOTANIM_EXIT, 3000);
//						    } else {
//						        sendEmptyMessageDelayed(MSG_BOOTANIM_EXIT, 1500);
//						    }
//                        }
						//yfzhang 插拔启动音乐的地方
						if(si.isSrcExists() && si.getPending()!=0){
							startSourceAppNow(si);
						}
					} else if (msg.what == MSG_CHECK_MEDIA_SOURCE) {
					    mMediaCheckCount++;
					    int srcNo = msg.arg1;
					    
					    if (mMediaCheckCount <= MEDIA_CHECK_MAX_COUNT) {		
					    	String playingDev = "";
					    	if(SourceManager.SRC_NO.music == srcNo){
					    		playingDev = musicPlayingDev;
					    	}
					    	else if(SourceManager.SRC_NO.video == srcNo){
					    		playingDev = videoPlayingDev;
					    	}
					    	else{
					    		Log.w(TAG, "PARAMTERS ERROR.");
					    		return;
					    	}
					    	boolean needStartMediaSource = false;
					    	if(playingDev!=null || playingDev.length()>0){
					    		 needStartMediaSource = YeconEnv.checkStorageExist(env, playingDev);
							        Log.i(TAG, "mMediaCheckCount: " + mMediaCheckCount + " - srcNo: " 
							                + srcNo + " - needStartMediaSource: " + needStartMediaSource);
					    	}					
					    	if (!needStartMediaSource) {
					            checkMediaSource(srcNo);
					            return;
					        }
					    } else {
		                    try{
		                        synchronized(sources){
		                            Iterator<SourceInfo> it = sources.iterator();
		                            SourceInfo si = null;
		                            while(it.hasNext()){
		                                si = it.next();
		                                if(si.getSrcNo() == srcNo){
		                                    it.remove();
		                                }
		                            }
		                        }
		                    } catch(EmptyStackException e){
		                        e.printStackTrace();
		                    } catch(Exception e){
		                        e.printStackTrace();
		                    }
		                    
		                    srcNo = SourceManager.SRC_NO.radio;
					    }
					    
					    mMediaCheckCount = 0;
					    
					    startSource(srcNo, "", DELAY_START_SRC);
					} else if (msg.what == MSG_BOOTANIM_EXIT) {
					    SystemProperties.set("service.bootanim.exit", "1");
					    
					    if (SystemProperties.getBoolean(PROPERTY_QB_POWEROFF, false)) {
					        SystemProperties.set(PROPERTY_QB_POWEROFF, "false");
					    }
					    
					    if (SystemProperties.getBoolean(PROPERTY_QB_POWERON, false)) {
    					    SystemProperties.set(PROPERTY_QB_POWERON, "false");

    					    sendEmptyMessage(MSG_ID_SET_VOLUME);
					    }
					} else if (msg.what == MSG_ID_SET_VOLUME) {
					    if (mMcuManager != null) {
	                        try {
	                            mMcuManager.RPC_SetVolumeMute(false);
	                        } catch (RemoteException e) {
	                            e.printStackTrace();
	                        }
	                    }
					}
				}
				
			};
			
			mAudioManager = (AudioManager) cntx.getSystemService(Context.AUDIO_SERVICE);
			mMcuManager = (McuManager) cntx.getSystemService(Context.MCU_SERVICE);
			
			mPrefs = context.getSharedPreferences(PREFS,Context.MODE_PRIVATE );
			updatePrefs();
		}	

	public class SourceInfo{
		public final static int PEINDING_AUTOPLAY = 1;
		public final static int PEINDING_RESUME = 2;		
		private int srcNo;				//SourceManager.SRC_NO
		private boolean srcExists; //If the source exists,: sd/usb/dvd etc.
		private int pending;//0: nothing,1: autoplay , 2: resume;
		private boolean withMediaKey = false;
		private String devPath="";
		
		public SourceInfo(int srcNo, boolean srcExists) {
			super();
			this.srcNo = srcNo;
			this.srcExists = srcExists;
			this.pending = 0;
		}
		public SourceInfo(int srcNo) {
			super();
			this.srcNo = srcNo;
			this.srcExists = true;
			this.pending = 0;
		}
		public int getSrcNo() {
			return srcNo;
		}
		public void setSrcNo(int srcNo) {
			this.srcNo = srcNo;
		}
		public boolean isSrcExists() {
			return srcExists;
		}
		public void setSrcExists(boolean srcExists) {
			this.srcExists = srcExists;
		}
		public int getPending() {
			return pending;
		}
		public void setPending(int pending) {
			this.pending = pending;
		}		
		public boolean isWithMediaKey() {
			return withMediaKey;
		}
		public void setWithMediaKey(boolean withMediaKey) {
			this.withMediaKey = withMediaKey;
		}
		public String getDevPath() {
			return devPath;
		}
		public void setDevPath(String devPath) {
			this.devPath = devPath;
		}
		
	}
	
	private void updatePrefs(){
		String playingPath;
		try {
			playingPath = mPrefs.getString(MUSIC_PLAYING_PREFS,  "");
			if(SourceManagerService.DEBUG){
				Log.i(TAG, "MUSIC_PLAYING_PREFS: "+playingPath);
			}
			musicPlayingDev = YeconEnv.getMeidaPathByFilePath(playingPath);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			playingPath = mPrefs.getString(VIDEO_PLAYING_PREFS,  "");
			if(SourceManagerService.DEBUG){
				Log.i(TAG, "VIDEO_PLAYING_PREFS: "+playingPath);
			}
			videoPlayingDev = YeconEnv.getMeidaPathByFilePath(playingPath);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(SourceManagerService.DEBUG){
			Log.i(TAG, "musicPlayingPath: "+musicPlayingDev + "  videoPlayingPath: " + videoPlayingDev);
		}		
	}	
	
	private LinkedList<SourceInfo> sources = new LinkedList<SourceInfo>();	
	public int request(int srcNo){		
//		if(!isEmergencySource(srcNo)){
//			//cancel boot source.
//			handler.removeMessages(MSG_START_SOURCE);
//		}
		
		try{
			if(SourceManagerService.DEBUG){
				Log.i(TAG, "SourceScheduler - request:"+srcNo);
			}
			synchronized(sources){				
				Iterator<SourceInfo> it = sources.iterator();
				SourceInfo siNow=null, si = null;
				while(it.hasNext()){
					si = it.next();
					if(SourceManagerService.DEBUG){
						Log.i(TAG, "SourceScheduler - scan:"+si.getSrcNo());
					}
					if(si.getSrcNo() == srcNo){
						siNow = si;
						it.remove();
					}
				}
				if(siNow==null){
					siNow = new SourceInfo(srcNo);
				}
				siNow.setPending(0);
				siNow.setSrcExists(true);				
				sources.add(0, siNow);
				if(SourceManagerService.DEBUG){
					Log.i(TAG, "SourceScheduler - notify:"+siNow.getSrcNo());
				}
				if(!isEmergencySource(siNow.getSrcNo())){
					siNow.setWithMediaKey(true);
					if(isHotPlug(siNow.getSrcNo())){
						it = sources.iterator();
						it.next();
						//check the stack source if it should be resume next time.
						while( it.hasNext()){
							//find the last source which is exist. 
							si = it.next();
							if(!isEmergencySource(si.getSrcNo()) && si.isSrcExists()){
								//flag to resume next time.
								si.setPending(SourceInfo.PEINDING_RESUME);
								break;
							}
						}	
					}

					if(shouldAquire(siNow.getSrcNo())){
						SystemProperties.set(SourceManager.PERSIST_LAST_SOURCE, ""+siNow.getSrcNo());
						Intent intent = new Intent();
						intent.setAction(SourceManager.ACTION_SOURCE_CHANGED_NOTIFY);
						intent.putExtra(SourceManager.EXTRA_SOURCE_NO, siNow.getSrcNo());
						context.sendBroadcast(intent);
					}
					setupMediaKeyReceiver(true);
					if (!startNaviApp()) {
					    exitBootAnim(1000);
					}
				}
				else{				    			    
					Intent intent = new Intent();
					intent.setAction(SourceManager.ACTION_SOURCE_NOTIFY);
					intent.putExtra(SourceManager.EXTRA_SOURCE_NO, siNow.getSrcNo());
					intent.putExtra(SourceManager.EXTRA_SOURCE_ACTION, SourceManager.SRC_ACTION.START.ordinal());
					context.sendBroadcast(intent);
					if (!startNaviApp()) {
                        exitBootAnim(1000);
                    }
				}
			}
		}
		catch(EmptyStackException e){
			//e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	private void exitBootAnim(long delayMillis){
		handler.removeMessages(MSG_BOOTANIM_EXIT);
		if (isPowerOn()) {
			handler.sendEmptyMessageDelayed(MSG_BOOTANIM_EXIT, delayMillis);
		}		
	}
	
	private void setupMediaKeyReceiver(boolean checkLater) {
		int srcNo = SystemProperties.getInt(SourceManager.PERSIST_LAST_SOURCE,  -1);		
		//if(srcNo >=0 && srcNo< sourcePackets.length && sourcePackets[srcNo].length()>0){
		if(srcNo>=0){
			handlerMediaKey.removeMessages(MSG_CHECK_AQUIRE_MEDIA_BUTTON + srcNo);
//			String receiverName = Settings.System.getStringForUser(context.getContentResolver(),
//	                 Settings.System.MEDIA_BUTTON_RECEIVER, UserHandle.USER_CURRENT);
//			 Log.i(TAG, "setupMediaKeyReceiver : "+receiverName);
//	         if (!receiverName.contains(context.getPackageName())) {
				mAudioManager.registerMediaButtonEventReceiver(new ComponentName(
						context.getPackageName(), MediaButtonIntentReceiver.class.getName()));
				if(checkLater){
					checkMediaButtonLater(srcNo);
				}
//	        }
		}		
	}
	private void checkMediaButtonLater(int srcNo){
		handlerMediaKey.removeMessages(MSG_CHECK_AQUIRE_MEDIA_BUTTON + srcNo);
		handlerMediaKey.sendEmptyMessageDelayed(MSG_CHECK_AQUIRE_MEDIA_BUTTON + srcNo, 2000);
	}
	private void checkMediaButton(boolean checkLater){
		int srcNo = SystemProperties.getInt(SourceManager.PERSIST_LAST_SOURCE,  -1);
		//if(srcNo >=0 && srcNo< sourcePackets.length && sourcePackets[srcNo].length()>0){
		if(srcNo>=0){
			Log.i(TAG, "checkMediaButton : "+srcNo);
			 String receiverName = Settings.System.getStringForUser(context.getContentResolver(),
	                 Settings.System.MEDIA_BUTTON_RECEIVER, UserHandle.USER_CURRENT);
			 Log.i(TAG, "checkMediaButton : "+receiverName);
	         if (!receiverName.contains(context.getPackageName())) {
	        	 Log.i(TAG, "checkMediaButton registerMediaButtonEventReceiver again ");
	        	 mAudioManager.registerMediaButtonEventReceiver(new ComponentName(
	        			 context.getPackageName(), MediaButtonIntentReceiver.class.getName()));
	        	 setupMediaKeyReceiver(checkLater);
	         }
	         else{
	        	 Log.i(TAG, "checkMediaButton registerMediaButtonEventReceiver OK ");
	         }
		}		
	}
	
	public void cancelCheckMediaButton(int srcNo){
		if(srcNo>=0){
			handlerMediaKey.removeMessages(MSG_CHECK_AQUIRE_MEDIA_BUTTON + srcNo);
			synchronized(sources){
				Iterator<SourceInfo> it = sources.iterator();
				SourceInfo si;
				boolean removeMediaKey = true;
				while(it.hasNext()){
					si = it.next();
					if(si.getSrcNo() == srcNo){
						si.setWithMediaKey(false);
					}
					else if(si.isWithMediaKey()){
						removeMediaKey = false;
						break;
					}
				}
				if(removeMediaKey){
					removeMediaKey();
				}
			}			
		}
		else{
			handlerMediaKey.removeCallbacksAndMessages(null);
		}
	}
	
	private void removeMediaKey(){
		Log.i(TAG, "removeMediaKey");
		mAudioManager.unregisterMediaButtonEventReceiver(
				 new ComponentName(context.getPackageName(),
						 MediaButtonIntentReceiver.class.getName()));
	}

	private void notifyTempSourceFree(int srcNo){
		Iterator<SourceInfo> it = sources.iterator();
		SourceInfo si;
		while(it.hasNext()){
			si = it.next();
			if(si!=null && isEmergencySource(si.getSrcNo())){
				return;
			}
		}		
		Intent intent = new Intent();
		intent.setAction(SourceManager.ACTION_SOURCE_NOTIFY);
		intent.putExtra(SourceManager.EXTRA_SOURCE_NO, srcNo);
		intent.putExtra(SourceManager.EXTRA_SOURCE_ACTION, SourceManager.SRC_ACTION.STOP.ordinal());
		context.sendBroadcast(intent);
	}
	public void release(int srcNo){
		boolean sourceEmpty = true;
		int top = 0;
		try{
			if(SourceManagerService.DEBUG){
				Log.i(TAG, "release:"+srcNo);
			}
			synchronized(sources){
				Iterator<SourceInfo> it;
				SourceInfo si;
				if(isEmergencySource(srcNo)){	
					it = sources.iterator();		
					boolean gotit = false;
					while(it.hasNext()){
						si = it.next();
						if(si.getSrcNo() == srcNo){
							it.remove();			
							gotit = true;
						}
					}					
					
					notifyTempSourceFree(srcNo);
					
					if(gotit){
						startTopSourceWithPending(100);
					}					
				}
				else{
					//SourceInfo siNow = null;					
					it = sources.iterator();
					si = null;
					top = 0;
					while(it.hasNext()){
						top++;
						si = it.next();						
						if(isEmergencySource(si.getSrcNo())){
							continue;
						}
						else{
							if(si.getSrcNo() == srcNo){
								//cancel media key checking.
								si.setWithMediaKey(false);
								cancelCheckMediaButton(si.getSrcNo());
								//
							}
							if(si.getSrcNo() == srcNo && si.getPending()==0){
								//Found a top normal source, remove it.
								if(SourceManagerService.DEBUG){
									Log.i(TAG, "remove it:"+srcNo);
								}
								it.remove();
								
								if(isHotPlug(si.getSrcNo())){
									if(!si.isSrcExists() && top==1){
										//it's pulled out,, so resume the last source			
										//startTopSourceWithPending(0); //start last source in hotplug
									}
									else{
										/*
										//the app is exited by user, 
										//Do not resume the last source next time.
										*/
										while(it.hasNext()){
											top++;
											si = it.next();
											if(isEmergencySource(si.getSrcNo())){
												continue;
											}
											else{
												si.setPending(0);
												break;
											}
										}
									}
								}
								break;
							}							
						}
					}					
				}
				
				//check if stack empty without pending srcNo.
				it = sources.iterator();
				while(it.hasNext()){
					si = it.next();
					if(!isEmergencySource(si.getSrcNo()) && si.getPending()==0){
						sourceEmpty = false;
						break;
					}
				}
				
				boolean isQBPowerOff = SystemProperties.getBoolean(PROPERTY_QB_POWEROFF, false);
				if(SourceManagerService.DEBUG){
	                Log.i(TAG, "isQBPowerOff: " + isQBPowerOff);
	            }
				if(sourceEmpty && !isQBPowerOff){
					SystemProperties.set(SourceManager.PERSIST_LAST_SOURCE, ""+ -1);
				}
			}
		}
		catch(EmptyStackException e){
			//e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	private SourceInfo getTopSource(){
		try{
			synchronized(sources){
				Iterator<SourceInfo> it = sources.iterator();
				SourceInfo si;
				while(it.hasNext()){
					si = it.next();
					if(isEmergencySource(si.getSrcNo()) || !shouldAquire(si.getSrcNo())){
						continue;
					}
					if(si!=null &&  si.isSrcExists()){
						return si;
					}
					else{
						it.remove();
					}
				}		
			}
			SystemProperties.set(SourceManager.PERSIST_LAST_SOURCE, ""+ -1);
		}
		catch(Exception e){
			//e.printStackTrace();
		}		
		return null;
	}

	public void hotPlug(int srcNo, String devPath, boolean insert, boolean appExit) {
	    boolean isQBPowerOn = SystemProperties.getBoolean(PROPERTY_QB_POWERON, false);
	    if(SourceManagerService.DEBUG){
            Log.i(TAG, "isQBPowerOn: " + isQBPowerOn);
        }
	    if (isQBPowerOn) {
	        return;
	    }
	    Log.w(TAG, "insert: " + insert + "   appExit:" + appExit + "  srcNo:" + srcNo);
		if(insert){
			//device is inserted.
			if(srcNo == SourceManager.SRC_NO.dvd){
				return;
			}
			else if(srcNo == SourceManager.SRC_NO.ipod){
				return;
			}
			else{			
				if(SourceManagerService.DEBUG){
						Log.i(TAG, "hotPlug: media inserted");
				}
				//check if video on top of stack, start video ,else start music.
				synchronized(sources){
					Iterator<SourceInfo> it = sources.iterator();
					SourceInfo si;
					while(it.hasNext()){
						si = it.next();
						Log.w(TAG, "hotPlug: media inserted si:" + si.getSrcNo());
						if(isEmergencySource(si.getSrcNo())){
							continue;
						}
						else{
							if(si.getSrcNo() == SourceManager.SRC_NO.music){
								startSource(SourceManager.SRC_NO.music, devPath, 0);
								return;
							}
							if(si.getSrcNo() == SourceManager.SRC_NO.video){
								startSource(SourceManager.SRC_NO.video, devPath, 0);
								return;
							}
							if(si.getSrcNo() == SourceManager.SRC_NO.photo){
								startSource(SourceManager.SRC_NO.photo, devPath, 0);
								return;
							}
							break;
						}
					}
				}			
				startSource(SourceManager.SRC_NO.music, devPath, 1000);
			}
		}
		else{
			//device is pulled out.
			try{
				synchronized(sources){
					Iterator<SourceInfo> it = sources.iterator();
					SourceInfo si =null;
					while(it.hasNext()){
						si = it.next();			
						if(srcNo == SourceManager.SRC_NO.dvd){
							if(si.getSrcNo() == SourceManager.SRC_NO.dvd){
								if(SourceManagerService.DEBUG){
									Log.i(TAG, "hotPlug:dvd pull out");
								}
								si.setSrcExists(false);
								startTopSourceWithPending(0);
							}
						}
						else if(srcNo == SourceManager.SRC_NO.ipod){
							if(si.getSrcNo() == SourceManager.SRC_NO.ipod){
								if(SourceManagerService.DEBUG){
									Log.i(TAG, "hotPlug:ipod pull out");
								}
								si.setSrcExists(false);
								startTopSourceWithPending(0);
							}
						}
						else{
							if(si.getSrcNo() == SourceManager.SRC_NO.music
									||si.getSrcNo() == SourceManager.SRC_NO.video
									||si.getSrcNo() == SourceManager.SRC_NO.photo){		
								if(appExit){
									si.setSrcExists(false);
									startTopSourceWithPending(0);
								}
								else if(!YeconEnv.checkStorageExist(env)){
									if(SourceManagerService.DEBUG){
										if(si.getSrcNo() == SourceManager.SRC_NO.music)
											Log.i(TAG, "hotPlug: music pull out");
										else
											Log.i(TAG, "hotPlug: video pull out");
									}
									si.setSrcExists(false);
									startTopSourceWithPending(0);
								}
							}
						}				
					}
				}
			}
			catch(Exception e){
				//e.printStackTrace();
			}
		}
	}
	
	public void setPower(int powerState) {
		this.powerState = powerState;
		if(SourceManagerService.DEBUG){
			Log.i(TAG, "this.powerState:" + this.powerState);	
		}
		if(POWER_STATE.poweron== powerState){
			//startTopSourceWithPending(0);
			//check boot anim in 15 seconds in case of no app will running when wake up from suspend.
			exitBootAnim(15000);
			//
			updatePrefs();
			int srcNo = SystemProperties.getInt(SourceManager.PERSIST_LAST_SOURCE,  -1);
			Log.i(TAG, "start first source:"+srcNo);
			if(srcNo>=0 && srcNo<SourceManager.SRC_NO.max){
		        if(srcNo == SourceManager.SRC_NO.music
		                ||srcNo == SourceManager.SRC_NO.video){
		            mMediaCheckCount = 0;
		            checkMediaSource(srcNo);
		        } else {
		            startSource(srcNo, "", DELAY_START_SRC);
		        }
			}
			else{
			    startSource(SourceManager.SRC_NO.radio, "", DELAY_START_SRC);
			}
		}
		else if(POWER_STATE.poweroff == powerState){
			if(SourceManagerService.DEBUG){
				Log.i(TAG, "last source:" + SystemProperties.getInt(SourceManager.PERSIST_LAST_SOURCE, -1));	
			}
			
			//check navi 
			mPrefs.edit().putBoolean(RESUME_NAVI_FLAG_PREFS, false).commit();
        	ActivityManager mgr = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> task = mgr.getRunningTasks(1);        
            if(task.size()>0){
            	String packageName = task.get(0).topActivity.getPackageName();
            	Log.i(TAG, "top pkg :" + packageName);
            	if (WhiteList.getPackageType(packageName) == WhiteList.APP_NAVIGATOR) {
            		 Log.i(TAG, "set PROPERTY_KEY_RESTORE_NAVI flag");
            		 mPrefs.edit().putBoolean(RESUME_NAVI_FLAG_PREFS, true).commit();
            	}
            }    
            //
			//clean temp source. because system is suspend.
			synchronized(sources){
				Iterator<SourceInfo> it = sources.iterator();
				SourceInfo si;
				while(it.hasNext()){
					si = it.next();
					if(isEmergencySource(si.getSrcNo())){
						it.remove();
					}
				}		
			}
			//
		}
		else if(POWER_STATE.accon == powerState){
			startTopSourceWithPending(0);
		}
		else if(POWER_STATE.accoff == powerState){
			if(SourceManagerService.DEBUG){
				Log.i(TAG, "last source:" + SystemProperties.getInt(SourceManager.PERSIST_LAST_SOURCE, -1));	
			}
		}
	}
	
	public boolean isPowerOn(){
		return (powerState==POWER_STATE.accon) || (powerState==POWER_STATE.poweron);
	}
	
	/*
	 * 判断srcNo顶是否为紧急临时源：电话和倒车
	 * 紧急临时源，不需要做源切换通知和恢复，有MTK那套源管理实现。
	 * 所以这里只是把紧急源记在stack里，做为其他源是否可以马上恢复启动的依据。
	 */
	public static boolean isEmergencySource(int srcNo){
		if(SourceManager.SRC_NO.bt_phone == srcNo
				||SourceManager.SRC_NO.backcar == srcNo){
			return true;
		}
		else{
			return false;
		}
	}
	/*
	 * 现在是否可以启动源srcNo
	 * 只要stack 存在紧急源，不能启动其他源
	 */
	private boolean canStartSourceNow(SourceInfo src){
		int srcNo = src.getSrcNo();
		
//		boolean navi = SystemProperties.getBoolean(PROPERTY_KEY_IS_NAVI_APP, false);
//        boolean naviFore = SystemProperties.getBoolean(PROPERTY_KEY_NAVI_IS_FORE,  false);
//        if(src.getPending()==SourceInfo.PEINDING_RESUME && navi && naviFore){
//        	//导航软件在前面时，不做恢复
//        	return false;
//        }
        
		if(isEmergencySource(srcNo)){
			//紧急源永远不由我们自己启动。
			return false;
		}
		if(!isPowerOn()){
			return false;
		}
		try{
			synchronized(sources){
				Iterator<SourceInfo> it = sources.iterator();
				SourceInfo si;
				while(it.hasNext()){
					si = it.next();
					if(si!=null && isEmergencySource(si.getSrcNo())){
						//只要stack 存在紧急源，不能启动其他源
						return false;
					}
				}		
			}
		}
		catch(Exception e){
			//e.printStackTrace();
		}
		return true;
	}
	
	private boolean startMediaSource(int srcNo) {
	    File musicPlayFile = null;
	    File videoPlayFile = null;
	    
	    String musicFile = SystemProperties.get(PROPERTY_MUSIC_PLAY_FILE, "");
	    String videoFile = SystemProperties.get(PROPERTY_VIDEO_PLAY_FILE, "");
	    
	    Log.i(TAG, "startMediaSource - musicFile: " + musicFile);
	    
	    Log.i(TAG, "startMediaSource - videoFile: " + videoFile);
	    
	    musicPlayFile = new File(musicFile);
	    videoPlayFile = new File(videoFile);
	    
	    if (srcNo == SourceManager.SRC_NO.music && musicPlayFile != null 
	            && musicPlayFile.exists()) {
	        return true;
	    }
	    
	    if (srcNo == SourceManager.SRC_NO.video && videoPlayFile != null 
	            && videoPlayFile.exists()) {
            return true;
        }
	    
	    return false;
	}
	
	private void checkMediaSource(int srcNo) {
	    handler.removeMessages(MSG_CHECK_MEDIA_SOURCE);
	    handler.sendMessageDelayed(handler.obtainMessage(MSG_CHECK_MEDIA_SOURCE, srcNo, 0), 1000);
	}
	
	private boolean startNaviApp() {
		String test_flag = SystemProperties.get("persis.sys.test_flag", "null");
		Log.i(TAG, "test_flag : "+ test_flag);
	    boolean restoreNavi = mPrefs.getBoolean(RESUME_NAVI_FLAG_PREFS, false);//SystemProperties.getBoolean(PROPERTY_KEY_RESTORE_NAVI, false);
        if (SourceManagerService.DEBUG) {
            Log.i(TAG, "setPower - startNaviApp - restoreNavi: "+ restoreNavi);
        }
        if (restoreNavi) {
            //SystemProperties.set(PROPERTY_KEY_RESTORE_NAVI, "false");
        	mPrefs.edit().putBoolean(RESUME_NAVI_FLAG_PREFS, false).commit();
        	
            exitBootAnim(1000);
            
            String mapPackage = SystemProperties.get("persist.sys.maps", "nothing");
            String mapPackageName = mapPackage.split("#")[0];
            String mapClassName = mapPackage.split("#")[1];
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addCategory("android.intent.category.LAUNCHER");
            i.setComponent(new ComponentName(mapPackageName, mapClassName));
            context.startActivity(i);
            return true;
        }
	    return false;
	}
		
	public void startTopSourceWithPending(long delayMillis){
		handler.removeMessages(MSG_START_TOP_PENDING_SOURCE);
		handler.sendMessageDelayed(handler.obtainMessage(MSG_START_TOP_PENDING_SOURCE, 1, 0), delayMillis);
	}
	
	public void startSource(int srcNo, String devPath, long delayMillis){
		Log.i(TAG, "startSource - srcNo: " + srcNo);
		
		try{
			synchronized(sources){
				Iterator<SourceInfo> it = sources.iterator();
				SourceInfo siNow=null, si = null;
				while(it.hasNext()){
					si = it.next();
					if(si.getSrcNo() == srcNo){
						siNow = si;
						it.remove();
					}
				}
				if(siNow==null){
					siNow = new SourceInfo(srcNo);
				}
				siNow.setSrcExists(true);
				siNow.setDevPath(devPath);
				siNow.setPending(SourceInfo.PEINDING_AUTOPLAY);
				sources.add(0, siNow);
			}
			
			startTopSourceWithPending(delayMillis);
		}
		catch(EmptyStackException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public int getPowerState(){
		return powerState;
	}

	public void updatePlayingPath(int srcNo, String path) {
		// TODO Auto-generated method stub
		if(path==null){
			path = "";
		}
		if(	SourceManager.SRC_NO.music == srcNo){
			try {
				mPrefs.edit().putString(MUSIC_PLAYING_PREFS, path).commit();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else	 if(SourceManager.SRC_NO.video == srcNo){
			try {
				mPrefs.edit().putString(VIDEO_PLAYING_PREFS, path).commit();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
