package com.yecon.videoplayer2;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import android.content.Context;
import android.media.MediaFile;
import android.os.AsyncTask;
import android.util.Log;

public class FileScanner {
	private FileScanTask fileScanTask;
	private final String TAG="FileList";
	private int fileType = 0;
	public class FileType{
		public static final int AUDIO = 0x01;
		public static final int VIDEO = 0x02;
		public static final int IMAGE = 0x04;
	};
	
	FileScanner(Context context, int fileType){
		this.fileType = fileType;
	}
	
	public interface LoadCallback{
		void onStart();
		void onLoad(String path, int fileType);
		void onFinished();
	}

	public void loadAllFiles(String dirPath, FileScanTask myTask, boolean recursion ,LoadCallback loadCallback) {
        File file = new File(dirPath);
        if (myTask.isCancelled() || file == null || !file.exists() || !file.isDirectory()) {
            return;
        }

        File[] files = file.listFiles();

        for (File f : files) {
        	if(myTask.isCancelled())
        		return;
            if (f.isHidden()) {
                continue;
            }

            if (f.isDirectory()) {
            	if(recursion){
            		loadAllFiles(f.getAbsolutePath(), myTask, recursion, loadCallback);
            	}
            } else {
                String path = f.getAbsolutePath();
                MediaFile.MediaFileType mediaFileType = MediaFile.getFileType(path);
                if (mediaFileType != null) {
                    int fileType = mediaFileType.fileType;
                    if (0!=(FileType.VIDEO&this.fileType) && MediaFile.isVideoFileType(fileType)
                    		&& !path.substring(path.length()-4).equalsIgnoreCase(".dat")) {
                        if(loadCallback!=null){
                        	loadCallback.onLoad(path, FileType.VIDEO);
                        }
                    }
                    if (0!=(FileType.AUDIO&this.fileType) && MediaFile.isAudioFileType(fileType)) {
                        if(loadCallback!=null){
                        	loadCallback.onLoad(path, FileType.AUDIO);
                        }
                    }
                    if (0!=(FileType.IMAGE&this.fileType) && MediaFile.isImageFileType(fileType)) {
                        if(loadCallback!=null){
                        	loadCallback.onLoad(path, FileType.IMAGE);
                        }
                    }
                }
            }
        }
    }
	
	public void loadAllFolders(String dirPath, FileScanTask myTask, LoadCallback loadCallback) {
		boolean added = false;
        File file = new File(dirPath);
        if (myTask.isCancelled() || file == null || !file.exists() || !file.isDirectory()) {
            return;
        }

        File[] files = file.listFiles();
        if(files==null){
        	return;
        }
        for (File f : files) {
        	if(myTask.isCancelled())
        		return;
            if (f.isHidden()) {
                continue;
            }

            if (f.isDirectory()) {
            	loadAllFolders(f.getAbsolutePath(), myTask, loadCallback);
            } else if(!added){
                String path = f.getAbsolutePath();
                MediaFile.MediaFileType mediaFileType = MediaFile.getFileType(path);
                if (mediaFileType != null) {
                    int fileType = mediaFileType.fileType;
                    if (0!=(FileType.VIDEO&this.fileType) && MediaFile.isVideoFileType(fileType)
                    		&& !path.substring(path.length()-4).equalsIgnoreCase(".dat")) {
                        if(loadCallback!=null){
                        	loadCallback.onLoad(f.getParent(), this.fileType);                        	
                        }
                        added = true;
                        continue;
                    }
                    if (0!=(FileType.AUDIO&this.fileType) && MediaFile.isAudioFileType(fileType)) {
                    	if(loadCallback!=null){
                    		loadCallback.onLoad(f.getParent(), this.fileType);
                        }
                    	added = true;
                    	continue;
                    }
                    if (0!=(FileType.IMAGE&this.fileType) && MediaFile.isImageFileType(fileType)) {
                    	if(loadCallback!=null){
                    		loadCallback.onLoad(f.getParent(), this.fileType);
                        }
                    	added = true;
                    	continue;
                    }
                }
            }
        }
    }

    class FileScanTask extends AsyncTask<Object, Integer, Object>{
    	
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			Object input = arg0[0];
			if(null!=input){
				if(input instanceof ScanItem){
					//scan device all folders
					ScanItem item = (ScanItem)input;
					
					if(item.getLoadCallback()!=null){
						item.getLoadCallback().onStart();
					}
					
					if(item.getMode() == 0){
						loadAllFolders(item.getPath(), item.getScanTask(), item.getLoadCallback());
					}
					else if(item.getMode()==1){
						loadAllFiles(item.getPath(), item.getScanTask(), item.isRecursion(), item.getLoadCallback());
					}
					
					if(item.getLoadCallback()!=null){
						item.getLoadCallback().onFinished();
					}
					
				}
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}
    	
    };

    private void cancelFileScan(){
    	if(fileScanTask!=null){
    		fileScanTask.cancel(true);
    		fileScanTask = null;
    	}
    }
    
    public class ScanItem{
    	private String path="";
    	private int mode; //0:folders, 1:files.
    	private boolean recursion = false;
    	private int fileType;
    	private int status=0; //0:idle, 1:scanning
    	private FileScanTask scanTask = null;
    	private LoadCallback loadCallback;
    	
    	ScanItem(int mode, String devPath, int fileType){
    		this.mode = mode;
    		this.scanTask = null;
    		this.path = devPath;
    		this.status = 0;
    	}
    	
    	ScanItem(int mode, String devPath, int fileType, LoadCallback loadCallback){
    		this.mode = mode;
    		this.scanTask = null;
    		this.path = devPath;
    		this.status = 0;
    		this.loadCallback = loadCallback;
    	}
    	
    	public boolean isRecursion() {
			return recursion;
		}
		public void setRecursion(boolean recursion) {
			this.recursion = recursion;
		}
		public LoadCallback getLoadCallback() {
			return loadCallback;
		}
		public void setLoadCallback(
				LoadCallback loadCallback) {
			this.loadCallback = loadCallback;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public FileScanTask getScanTask() {
			return scanTask;
		}

		public void setScanTask(FileScanTask scanTask) {
			this.scanTask = scanTask;
		}

		public int getMode() {
			return mode;
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

		public int getFileType() {
			return fileType;
		}

		public void setFileType(int fileType) {
			this.fileType = fileType;
		}
				
    }
   
    private ArrayList<ScanItem> devices = new ArrayList<ScanItem>();
    public ArrayList<ScanItem> getDevices(){
    	return devices;
    }
    public ScanItem getDevice(String devPath){
    	synchronized(devices){
    		Iterator<ScanItem> it = devices.iterator();
    		ScanItem si;
			while(it.hasNext()){
				si = it.next();
				if(si.getPath().equals(devPath)){
					return si;
				}
			}
    	}
    	return null;
    }    
    public void removeDevice(String devPath){
    	synchronized(devices){
    		Iterator<ScanItem> it = devices.iterator();
    		ScanItem si;
			while(it.hasNext()){
				si = it.next();
				if(si.getPath().startsWith(devPath)){
					FileScanTask scanTask =  si.getScanTask();
					if(scanTask!=null){
						scanTask.cancel(true);
					}
					it.remove();
				}
			}
    	}
    }    
    public void removeAllDevice(){
    	synchronized(devices){
    		Iterator<ScanItem> it = devices.iterator();
    		ScanItem si;
			while(it.hasNext()){
				si = it.next();
				FileScanTask scanTask =  si.getScanTask();
				if(scanTask!=null){
					scanTask.cancel(true);
				}
				it.remove();
			}
    	}
    }    
    private ScanItem scanFolder;
    public void scanFolder(String devPath, LoadCallback loadCallback){
    	if(scanFolder!=null && scanFolder.getScanTask()!=null){
    		scanFolder.getScanTask().cancel(true);
    		scanFolder.setScanTask(null);
    	}
    	scanFolder = new ScanItem(1, devPath, fileType, loadCallback);
    	scanFolder.setRecursion(false);
    	scanFolder.setScanTask(new FileScanTask());
    	scanFolder.getScanTask().execute(scanFolder);
    }
    

    //mode: 0:folders, 1:all files
    public void scanDevice(String devPath, LoadCallback loadCallback, int mode) {     
    	if(devPath==null || devPath.length()==0){
    		return;
    	}
    	synchronized(devices){
    		Iterator<ScanItem> it = devices.iterator();
    		ScanItem si;
			while(it.hasNext()){
				si = it.next();
				if(si.getPath().equals(devPath)){
					FileScanTask scanTask =  si.getScanTask();
					if(scanTask!=null){
						scanTask.cancel(true);
					}
					it.remove();
				}
			}
			//
			si = new ScanItem(mode, devPath, fileType, loadCallback);
			si.setRecursion(true);
			FileScanTask scanTask = new FileScanTask();
			si.setScanTask(scanTask);
			devices.add(si);
			scanTask.execute(si);			
    	}
    }
}
