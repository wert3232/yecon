package com.autochips.picturebrowser;

import android.content.Context;

import com.autochips.picturebrowser.data.LocalData;
import com.autochips.picturebrowser.data.LocalFiles;
import com.autochips.picturebrowser.data.MediaData;
import com.autochips.picturebrowser.status.StatusHub;

public class DataManager {
	private MediaData mData = null;
	private Context context;
	
	public DataManager(Context context, String path){
		this.context = context;
		if (path != null && path.length() > 0) {
			// create list from a local path.
			mData = new LocalFiles(context, path);
		} else {
			// create list from system database.
			mData = new LocalData(context, context.getContentResolver());
		}
		StatusHub.addStatusOwner(mData);
	}
	
	public void load(){		
		if(mData!=null)
			mData.onLoad();		
	}
	
	public void load(String curFile){		
		if(mData!=null && mData instanceof LocalFiles){
			((LocalFiles)mData).setCurFile(curFile);
			((LocalFiles)mData).onLoad();
		}
	}
	public MediaData getData() {
		return mData;
    }
	public void unLoad() {
		if(mData!=null){
			mData.onStore();
		}
    }
     
}
