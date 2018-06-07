package com.autochips.picturebrowser.data;

import java.io.File;
import java.util.ArrayList;

import com.autochips.picturebrowser.R;

import android.content.Context;
import android.media.MediaFile;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LocalFiles extends MediaData {
	
	private static final String TAG = "LocalFiles";
	private Context mContext = null;
	private String curPath = null;
	private String curFile = null;
	private ArrayList<String>fileList = new ArrayList<String>();
	private LoadFilesTask loadFilesTask=null;
	private int curPos = 0;
			
	class LoadFilesTask extends AsyncTask<String, Integer, ArrayList<String>>{

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			String path = (String) params[0];
			String filePath = (String) params[1];
			File dirFile = new File(path);
			ArrayList<String>files = new ArrayList<String>();
	    	if(dirFile!=null){
	    		if(dirFile.isDirectory() && dirFile.exists()){	    			
	    			for(File f:dirFile.listFiles()){
	    				if(isCancelled()){
	    					return null;
	    				}
	    	            if (f.isHidden()) {
	    	                continue;
	    	            }
	    	            if (f.isFile()) {
	    	                MediaFile.MediaFileType mediaFileType = MediaFile.getFileType(f.getAbsolutePath());
	    	                if (mediaFileType != null) {	                    
			    				if (MediaFile.isImageFileType(mediaFileType.fileType)) {
			    					files.add(f.getAbsolutePath());
			    					if(filePath!=null && filePath.equals(f.getAbsolutePath())){
			    						curPos = files.size()-1;
			    					}
			                    }
	    	                }
	    	            }
	    			}	    			
	    		}
	    	}
	    	return files;
		}
				
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			fileList.clear();
			curPos = 0;
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			curPos = 0;
			if(getNotifyShowImg()!=null){
				getNotifyShowImg().onLoadStart();
			}
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			fileList = (ArrayList<String>) result;
			if(getNotifyShowImg()!=null){
				getNotifyShowImg().onLoadFinish();
			}
			super.onPostExecute(result);
		}	
				
	};
	
	private void udpateFileList(String path){
		if(path==null){
			return;
		}
		if(loadFilesTask!=null){
			loadFilesTask.cancel(true);
			loadFilesTask = null;
		}
		loadFilesTask = new LoadFilesTask();
		loadFilesTask.execute(new String[]{path, curFile});
	}
	
    public LocalFiles(Context context, String path) {
        super(null);
        this.curPath = path;
        mContext = context;
    }
    
    public void setCurFile(String curFile){
    	this.curFile = curFile;
    }
    
    @Override
    public void onLoad() {
        load();
    }
    
    @Override
    public void load() {
    	if(curPath == null){
    		Log.i(TAG, "path null ");
    		return;
    	}
    	udpateFileList(curPath);
    }
    
    @Override
    public void onStore() {
    	if(loadFilesTask!=null){
			loadFilesTask.cancel(true);
			loadFilesTask = null;
		}
        unload();
        fileList.clear();
    }
    
    @Override
    public void onClear() {
    	load();
    }
    
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return fileList.size();
	}

	public boolean isDataValid() {
        return fileList.size()>0;
    }
	
	@Override
	public Uri getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BitmapContext next() {
		// TODO Auto-generated method stub
		return next(true);
	}
	
	 @Override
    public BitmapContext next(boolean cycle) {
        if (!isDataValid()) {
            return null;
        }
        BitmapContext bmpCtx = null;
        if ((curPos+1)>=fileList.size()) {
            if (cycle) {
            	curPos = 0;
                bmpCtx = getCurrentBitmapContext();
            } else {
                noMorePicShow(R.string.last_pic_msg);
            }
        } else {        
        	curPos++;
            bmpCtx = getCurrentBitmapContext();
        }

        return bmpCtx;
    }
	 
	 private Toast toast = null;
	    private void noMorePicShow(int resID) {
	    	if(toast!=null){
	    		toast.cancel();
	    	}
	    	toast = Toast.makeText(mContext, resID, Toast.LENGTH_SHORT);
	    	toast.show();
	    }
	    
	 private BitmapContext getCurrentBitmapContext() {
	        if (!isDataValid())
	            return null;
	        if(curPos >=0 && curPos< fileList.size()){
	        	String picturePath = fileList.get(curPos);
	        	String fileName = picturePath.substring(picturePath.lastIndexOf("/")+1);
    	        BitmapContext bmpCtx = new BitmapContext();
    	        bmpCtx.setFileName(fileName);
    	        bmpCtx.setFilePath(picturePath);
    	        return bmpCtx;
	        }
	        return null;
	    }

	@Override
	public BitmapContext prev() {
		// TODO Auto-generated method stub
		return prev(true);
	}
	
	public BitmapContext prev(boolean cycle) {
        if (!isDataValid()) {
            return null;
        }
        if (curPos <= 0) {
        	if(cycle){
        		curPos = fileList.size()-1;
				return getCurrentBitmapContext();
        	}
        	else{
        		noMorePicShow(R.string.first_pic_msg);
        		return null;
        	}
        }
        curPos--;
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
        if (!isDataValid() || (fileList.size() - 1) < position) {
            return null;
        }
        int last = -1;
        if (!isMove) {
            last = curPos;
        }
        curPos = position;
        BitmapContext bmpCtx = getCurrentBitmapContext();
        if (!isMove && last != -1) {
        	curPos = last;
        }
        return bmpCtx;
    }
    
    public int getCount() {
		// TODO Auto-generated method stub
		return size();
	}

	public int getPosision() {
		// TODO Auto-generated method stub
		return curPos>=0?curPos:0;
	}
	
	@Override
	public int getPosisionByPath(String lastPlayPath) {
		// TODO Auto-generated method stub
		return 0;
	}

}
