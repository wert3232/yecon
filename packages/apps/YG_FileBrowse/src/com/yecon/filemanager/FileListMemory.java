package com.yecon.filemanager;

import java.util.ArrayList;

import android.os.Bundle;

public class FileListMemory {
	static ArrayList <FileListSaveData> mList = new ArrayList<FileListSaveData>();
	
	public static void add(int top, int pos, String path) {
		FileListSaveData listData = new FileListSaveData(top, pos, path);
		mList.add(listData);
	}
	
	public static Bundle find(String path) {
		for (int i=0; i<mList.size(); i++) {
			if (path.equalsIgnoreCase(mList.get(i).mPath)) {
				Bundle bundle = new Bundle();
				bundle.putInt("top", mList.get(i).mTop);
				bundle.putInt("pos", mList.get(i).mPos);
				mList.remove(i);
				return bundle;
			}
		}
		
		return null;
	}
	
	public static void clear() {
		mList.clear();
	}
}
