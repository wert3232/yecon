package com.yecon.videoplayer2;

import static com.yecon.videoplayer2.VideoPlaybackConstant.REPEAT_ALL;
import static com.yecon.videoplayer2.VideoPlaybackConstant.UNMOUNT_EXITAPP;

import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Iterator;

import com.autochips.storage.EnvironmentATC;
import com.yecon.common.YeconEnv;
import com.yecon.videoplayer2.FileScanner.LoadCallback;
import com.yecon.videoplayer2.FileScanner.ScanItem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

public class FileListManager {
	private final boolean DEBUG = true;
	private final String TAG = "FileListManager";
	private int fileType;
	private Context context;
	private FileScanner fileScanner;
	private EnvironmentATC envATC;
	private ListLoadingNotify listChangedNotify;
	private Object syncFolders = new Object();
	private ArrayList<FileInfo>fileInfoSd1=new ArrayList<FileInfo>();
	private ArrayList<FileInfo>fileInfoSd2=new ArrayList<FileInfo>();
	private ArrayList<FileInfo>fileInfoUsb1=new ArrayList<FileInfo>();
	private ArrayList<FileInfo>fileInfoUsb2=new ArrayList<FileInfo>();
	private ArrayList<FileInfo>fileInfoUsb3=new ArrayList<FileInfo>();
	private ArrayList<FileInfo>fileInfoUsb4=new ArrayList<FileInfo>();
	private ArrayList<FileInfo>fileInfoUsb5=new ArrayList<FileInfo>();
	private ArrayList<FileInfo>fileInfoInternal=new ArrayList<FileInfo>();
	private final boolean SCAN_INTERNAL = true;
	
	private final String allDeviceRoot[] = {
			YeconEnv.INT_SDCARD_PATH,
			YeconEnv.EXT_SDCARD1_PATH,
			YeconEnv.EXT_SDCARD2_PATH,
			YeconEnv.UDISK1_PATH,
			YeconEnv.UDISK2_PATH,
			YeconEnv.UDISK3_PATH,
			YeconEnv.UDISK4_PATH,
			YeconEnv.UDISK5_PATH
			};
	
	FileListManager(Context context, int fileType,ListLoadingNotify listChangedNotify){
		this.context = context;
		fileScanner = new FileScanner(context, fileType);
		envATC = new EnvironmentATC(context);
		this.fileType = fileType;
		this.listChangedNotify = listChangedNotify;
	}
	
	public void init(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addDataScheme("file");
		context.registerReceiver(mMediaMountedReceiver, filter);
	}
	
	public void release(){
		context.unregisterReceiver(mMediaMountedReceiver);
		fileScanner.removeAllDevice();
	}

	enum DEVICE{
		internal(0x01),
		sd1(0x02),
		sd2(0x04),
		usb1(0x08),
		usb2(0x10),
		usb3(0x20),
		usb4(0x40),
		usb5(0x80);
		int value;
		DEVICE(int value){
			this.value = value;
		}
		int getValue(){
			return value;
		}
	};
	
	public interface ListLoadingNotify{
		void onStart();
		void onListChanged();
		void onDeviceIsEmpty();
	}
	
	//private ArrayList<String> plugedDevices = new ArrayList<String>();
	private boolean isPlugedDevicesEmpty(){
		String path[] = envATC.getStorageMountedPaths();
		int ret = 0;
		for(String dev:path){
			if(!dev.startsWith(YeconEnv.INT_SDCARD_PATH)){
				ret++;
			}
		}
		return (ret==0);
	}
	public ArrayList<String> getAvailibleDevices(){
		String path[] = envATC.getStorageMountedPaths();
		ArrayList<String> ret = new ArrayList<String>();
		for(int i=0;i<allDeviceRoot.length;i++){
			for(String dev:path){
				if(dev.startsWith(allDeviceRoot[i])){
					if(0==i && SCAN_INTERNAL){
						ret.add(allDeviceRoot[i]);
					}
					else{
						ret.add(allDeviceRoot[i]);
					}
				}
			}
		}
		return ret;
	}
	
	public int getDevIndexByPath(String devPath){
    	if(devPath.startsWith(YeconEnv.EXT_SDCARD1_PATH)){
	 		return 0;
		}		
		else if(devPath.startsWith(YeconEnv.EXT_SDCARD2_PATH)){
			return 1;
		}
		else if(devPath.startsWith(YeconEnv.UDISK1_PATH)){
			return 2;
		}
		else if(devPath.startsWith(YeconEnv.UDISK2_PATH)){
			return 3;
		}
		else if(devPath.startsWith(YeconEnv.UDISK3_PATH)){
			return 4;
		}
		else if(devPath.startsWith(YeconEnv.UDISK4_PATH)){
			return 5;
		}
		else if(devPath.startsWith(YeconEnv.UDISK5_PATH)){
			return 6;
		}
		else if(devPath.startsWith(YeconEnv.INT_SDCARD_PATH)){
			return 7;
		}
    	return -1;
    }
	
	public void reloadAllDevicesFiles(){
		int device = 0;
		String path[] = envATC.getStorageMountedPaths();
		for(int i=0;i<allDeviceRoot.length;i++){
			for(String dev:path){
				if(dev.startsWith(allDeviceRoot[i])){
					device |= (1<<i);
				}
			}
		}
		if(!SCAN_INTERNAL){
			device &= ~DEVICE.internal.getValue();
		}
		if(device!=0){
			reloadFiles(device, "",  0, null);
		}
	}

	//mode:0:folder, 1:files
	private void reloadFiles(int device, String devPath,  final int mode, final LoadCallback loadCallback){

		if(0!=(device&DEVICE.sd1.getValue())
				|| devPath.startsWith(YeconEnv.EXT_SDCARD1_PATH)){
			//if(YeconEnv.checkStorageExist(envATC, YeconEnv.EXT_SDCARD1_PATH))
			{
				fileScanner.scanDevice(YeconEnv.EXT_SDCARD1_PATH, new LoadCallback(){
	
					@Override
					public void onLoad(String path, int type) {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							if(DEBUG){
								Log.i(TAG, "sd1: " + path);
							}		
							fileInfoSd1.add(new FileInfo(path, mode));
							if(loadCallback!=null){
								loadCallback.onLoad(path, fileType);
							}
						}					
					}
	
					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						if(loadCallback!=null){
							loadCallback.onFinished();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onListChanged();
						}
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							fileInfoSd1.clear();
						}
						if(loadCallback!=null){
							loadCallback.onStart();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onStart();
						}
					}
					
				}, mode);
			}
		}
		if(0!=(device&DEVICE.sd2.getValue())
				|| devPath.startsWith(YeconEnv.EXT_SDCARD2_PATH)){
			//if(YeconEnv.checkStorageExist(envATC, YeconEnv.EXT_SDCARD2_PATH))
			{
				fileScanner.scanDevice(YeconEnv.EXT_SDCARD2_PATH, new LoadCallback(){
	
					@Override
					public void onLoad(String path, int type) {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							if(DEBUG){
								Log.i(TAG, "sd2: " + path);
							}		
							fileInfoSd2.add(new FileInfo(path, mode));
							if(loadCallback!=null){
								loadCallback.onLoad(path, fileType);
							}
						}					
					}
	
					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						if(loadCallback!=null){
							loadCallback.onFinished();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onListChanged();
						}
					}
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							fileInfoSd2.clear();
						}
						if(loadCallback!=null){
							loadCallback.onStart();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onStart();
						}
					}
					
				}, mode);
			}
		}
		if(0!=(device&DEVICE.usb1.getValue())
				|| devPath.startsWith(YeconEnv.UDISK1_PATH)){
			//if(YeconEnv.checkStorageExist(envATC, devPath))
			{
				fileScanner.scanDevice(YeconEnv.UDISK1_PATH, new LoadCallback(){
	
					@Override
					public void onLoad(String path, int type) {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							if(DEBUG){
								Log.i(TAG, "usb1: " + path);
							}		
							fileInfoUsb1.add(new FileInfo(path, mode));
							if(loadCallback!=null){
								loadCallback.onLoad(path, fileType);
							}
						}					
					}
	
					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						if(loadCallback!=null){
							loadCallback.onFinished();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onListChanged();
						}
					}
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							fileInfoUsb1.clear();
						}
						if(loadCallback!=null){
							loadCallback.onStart();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onStart();
						}
					}
					
				}, mode);
			}
		}
		if(0!=(device&DEVICE.usb2.getValue())
				|| devPath.startsWith(YeconEnv.UDISK2_PATH)){
			//if(YeconEnv.checkStorageExist(envATC, YeconEnv.UDISK2_PATH))
			{
				fileScanner.scanDevice(YeconEnv.UDISK2_PATH, new LoadCallback(){
	
					@Override
					public void onLoad(String path, int type) {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							if(DEBUG){
								Log.i(TAG, "usb2: " + path);
							}		
							fileInfoUsb2.add(new FileInfo(path, mode));
							if(loadCallback!=null){
								loadCallback.onLoad(path, fileType);
							}
						}					
					}
	
					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						if(loadCallback!=null){
							loadCallback.onFinished();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onListChanged();
						}
					}
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							fileInfoUsb2.clear();
						}
						if(loadCallback!=null){
							loadCallback.onStart();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onStart();
						}
					}
					
				}, mode);
			}
		}
		if(0!=(device&DEVICE.usb3.getValue())
				|| devPath.startsWith(YeconEnv.UDISK3_PATH)){
			//if(YeconEnv.checkStorageExist(envATC, YeconEnv.UDISK3_PATH))
			{
				fileScanner.scanDevice(YeconEnv.UDISK3_PATH, new LoadCallback(){
	
					@Override
					public void onLoad(String path, int type) {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							if(DEBUG){
								Log.i(TAG, "usb3: " + path);
							}		
							fileInfoUsb3.add(new FileInfo(path, mode));
							if(loadCallback!=null){
								loadCallback.onLoad(path, fileType);
							}
						}					
					}
	
					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						if(loadCallback!=null){
							loadCallback.onFinished();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onListChanged();
						}
					}
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							fileInfoUsb3.clear();
						}
						if(loadCallback!=null){
							loadCallback.onStart();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onStart();
						}
					}
					
				}, mode);
			}
		}
		if(0!=(device&DEVICE.usb4.getValue())
				|| devPath.startsWith(YeconEnv.UDISK4_PATH)){
			//if(YeconEnv.checkStorageExist(envATC, YeconEnv.UDISK4_PATH))
			{
				fileScanner.scanDevice(YeconEnv.UDISK4_PATH, new LoadCallback(){
	
					@Override
					public void onLoad(String path, int type) {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							if(DEBUG){
								Log.i(TAG, "usb4: " + path);
							}		
							fileInfoUsb4.add(new FileInfo(path, mode));
							if(loadCallback!=null){
								loadCallback.onLoad(path, fileType);
							}
						}					
					}
	
					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						if(loadCallback!=null){
							loadCallback.onFinished();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onListChanged();
						}
					}
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							fileInfoUsb4.clear();
						}
						if(loadCallback!=null){
							loadCallback.onStart();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onStart();
						}
					}
					
				}, mode);
			}
		}
		if(0!=(device&DEVICE.usb5.getValue())
				|| devPath.startsWith(YeconEnv.UDISK5_PATH)){
			//if(YeconEnv.checkStorageExist(envATC, YeconEnv.UDISK5_PATH))
			{
				fileScanner.scanDevice(YeconEnv.UDISK5_PATH, new LoadCallback(){
	
					@Override
					public void onLoad(String path, int type) {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							if(DEBUG){
								Log.i(TAG, "usb5: " + path);
							}		
							fileInfoUsb5.add(new FileInfo(path, mode));
							if(loadCallback!=null){
								loadCallback.onLoad(path, fileType);
							}
						}					
					}
	
					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						if(loadCallback!=null){
							loadCallback.onFinished();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onListChanged();
						}
					}
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							fileInfoUsb5.clear();
						}
						if(loadCallback!=null){
							loadCallback.onStart();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onStart();
						}
					}
					
				}, mode);
			}
		}
		
		if(0!=(device&DEVICE.internal.getValue())
				|| devPath.equals(YeconEnv.INT_SDCARD_PATH)){
			if(YeconEnv.checkStorageExist(envATC, YeconEnv.INT_SDCARD_PATH)){
				fileScanner.scanDevice(YeconEnv.INT_SDCARD_PATH, new LoadCallback(){
	
					@Override
					public void onLoad(String path, int type) {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							if(DEBUG){
								Log.i(TAG, "internal: " + path);
							}		
							fileInfoInternal.add(new FileInfo(path, mode));
							if(loadCallback!=null){
								loadCallback.onLoad(path, fileType);
							}
						}					
					}
	
					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						if(loadCallback!=null){
							loadCallback.onFinished();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onListChanged();
						}
					}
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						synchronized(syncFolders){
							fileInfoInternal.clear();
						}
						if(loadCallback!=null){
							loadCallback.onStart();
						}
						if(listChangedNotify!=null){
							listChangedNotify.onStart();
						}
					}
					
				}, mode);
			}
		}
	}
	
	private void removeDevice(String devPath){
    	fileScanner.removeDevice(devPath);
    	synchronized(syncFolders){
    		if(devPath.startsWith(YeconEnv.EXT_SDCARD1_PATH)){
        		fileInfoSd1.clear();
        	}
        	else if(devPath.startsWith(YeconEnv.EXT_SDCARD2_PATH)){
        		fileInfoSd2.clear();
        	}
        	else if(devPath.startsWith(YeconEnv.UDISK1_PATH)){
        		fileInfoUsb1.clear();
        	}
        	else if(devPath.startsWith(YeconEnv.UDISK2_PATH)){
        		fileInfoUsb2.clear();
        	}
        	else if(devPath.startsWith(YeconEnv.UDISK3_PATH)){
        		fileInfoUsb3.clear();
        	}
        	else if(devPath.startsWith(YeconEnv.UDISK4_PATH)){
        		fileInfoUsb4.clear();
        	}
        	else if(devPath.startsWith(YeconEnv.UDISK5_PATH)){
        		fileInfoUsb5.clear();
        	}
    	}    	
    	if(listChangedNotify!=null){
    		listChangedNotify.onListChanged();
    	}
	}
	
	public ArrayList<FileInfo>getAllFiles(int device){
		ArrayList<FileInfo>fileInfo = new ArrayList<FileInfo>();
		synchronized(syncFolders){
			if((DEVICE.sd1.getValue() & device)!=0)
				fileInfo.addAll(fileInfoSd1);
			if((DEVICE.sd2.getValue() & device)!=0)
				fileInfo.addAll(fileInfoSd2);
			if((DEVICE.usb1.getValue() & device)!=0)
				fileInfo.addAll(fileInfoUsb1);
			if((DEVICE.usb2.getValue() & device)!=0)
				fileInfo.addAll(fileInfoUsb2);
			if((DEVICE.usb3.getValue() & device)!=0)
				fileInfo.addAll(fileInfoUsb3);
			if((DEVICE.usb4.getValue() & device)!=0)
				fileInfo.addAll(fileInfoUsb4);
			if((DEVICE.usb5.getValue() & device)!=0)
				fileInfo.addAll(fileInfoUsb5);
			if((DEVICE.internal.getValue() & device)!=0)
				fileInfo.addAll(fileInfoInternal);
		}
		return fileInfo;
	}
	
	public boolean isScanning(int device){
		boolean ret = false;
		ScanItem scanItem;
		if((DEVICE.sd1.getValue() & device)!=0){
			scanItem = fileScanner.getDevice(YeconEnv.EXT_SDCARD1_PATH);
			if(scanItem!=null && scanItem.getScanTask().getStatus()==AsyncTask.Status.RUNNING){
				return true;
			}
		}
		if((DEVICE.sd2.getValue() & device)!=0){
			scanItem = fileScanner.getDevice(YeconEnv.EXT_SDCARD2_PATH);
			if(scanItem!=null && scanItem.getScanTask().getStatus()==AsyncTask.Status.RUNNING){
				return true;
			}
		}
		if((DEVICE.usb1.getValue() & device)!=0){
			scanItem = fileScanner.getDevice(YeconEnv.UDISK1_PATH);
			if(scanItem!=null && scanItem.getScanTask().getStatus()==AsyncTask.Status.RUNNING){
				return true;
			}
		}
		if((DEVICE.usb2.getValue() & device)!=0){
			scanItem = fileScanner.getDevice(YeconEnv.UDISK2_PATH);
			if(scanItem!=null && scanItem.getScanTask().getStatus()==AsyncTask.Status.RUNNING){
				ret = true;
			}
		}
		if((DEVICE.usb3.getValue() & device)!=0){
			scanItem = fileScanner.getDevice(YeconEnv.UDISK3_PATH);
			if(scanItem!=null && scanItem.getScanTask().getStatus()==AsyncTask.Status.RUNNING){
				return true;
			}
		}
		if((DEVICE.usb4.getValue() & device)!=0){
			scanItem = fileScanner.getDevice(YeconEnv.UDISK4_PATH);
			if(scanItem!=null && scanItem.getScanTask().getStatus()==AsyncTask.Status.RUNNING){
				return true;
			}
		}
		if((DEVICE.usb5.getValue() & device)!=0){
			scanItem = fileScanner.getDevice(YeconEnv.UDISK5_PATH);
			if(scanItem!=null && scanItem.getScanTask().getStatus()==AsyncTask.Status.RUNNING){
				return true;
			}
		}
		if((DEVICE.internal.getValue() & device)!=0){
			scanItem = fileScanner.getDevice(YeconEnv.INT_SDCARD_PATH);
			if(scanItem!=null && scanItem.getScanTask().getStatus()==AsyncTask.Status.RUNNING){
				return true;
			}
		}
		return ret;		
	}
	
	public static class FileInfo{
		private long id;
		private long size; //file size of files count if mode==0
		private int mode;//0:folder, 1:file
		private String path="";
		private String name="";
		private boolean playing=false;
		
		public FileInfo(){
			super();
		}
		public FileInfo(String path, int mode) {
			super();
			this.path = path;
			this.mode = mode;
		}
		
		public FileInfo(String path, String name, long size) {
			super();
			this.path = path;
			this.mode = 1;
			this.name = name;
			this.size = size;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isPlaying() {
			return playing;
		}

		public void setPlaying(boolean playing) {
			this.playing = playing;
		}
		public int getMode() {
			return mode;
		}
		public void setMode(int mode) {
			this.mode = mode;
		}
				
	}
	
	public interface MediaPlugCallback{
		void onMediaPlug(String devPath, boolean insert);
	};
	private MediaPlugCallback mediaPlugCallback;
	public void setMediaPlugCallback(MediaPlugCallback mediaPlugCallback){
		this.mediaPlugCallback = mediaPlugCallback;
	}
	private BroadcastReceiver mMediaMountedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            String path = intent.getData().getPath();
            if (Intent.ACTION_MEDIA_REMOVED.equals(action)  
            	|| Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {            	
            	if(mediaPlugCallback!=null){
            		mediaPlugCallback.onMediaPlug(path, false);
            	}
            	if(isPlugedDevicesEmpty()){
            		if(listChangedNotify!=null){
            			if(SCAN_INTERNAL){
            				if(fileInfoInternal.size()==0){
            					listChangedNotify.onDeviceIsEmpty();
            				}
            			}
            			else{
            				listChangedNotify.onDeviceIsEmpty();
            			}                		
                	}
            	}
            	else{
            		removeDevice(path);
            	}
            } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            	reloadFiles(0, path, 0, null);
            	if(mediaPlugCallback!=null){
            		mediaPlugCallback.onMediaPlug(path, true);
            	}
            }
        }

    };
	
}
