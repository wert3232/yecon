package com.autochips.dvr;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.os.Message;

public class FileSearch {
	private final static String dvr_normal_dir = "normal/";
	private final static String dvr_urgent_dir = "urgent/";
	public ArrayList<String> mFileArrayList = new ArrayList<String>();
	mySearchThread mThread = null;
	String mFilepath;
	ArrayList<String> mFileext;
	
	public FileSearch() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 开始搜索文件
	 * @param filepath
	 * @param fileext
	 */
	public void startFileSearchThread(String filepath, final ArrayList<String> fileext) {
		//如果线程并未结束，要终止线程，并且等待线程结束
		mFilepath = filepath;
		mFileext = fileext;
		if (mThread != null && mThread.isAlive()) {
			try {
				mThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mThread = new mySearchThread();
		mThread.start();
	}
	
	class mySearchThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			startFileSearch();
		}
		
	}
	/**
	 * 从指定的文件路径下搜索后缀名为fileext的所有文件
	 * @param filepath
	 * @param fileext
	 */
	public int startFileSearch() {
			FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				// TODO Auto-generated method stub
				boolean ret = false;
				
				for (int i=0; i<mFileext.size(); i++) {
					if (name.endsWith(mFileext.get(i))) {
						ret = true;
						break;
					}
				}
				
				return ret;
			}
		};
		
		//通知搜索开始，UI那边要显示一个进度条
		FileListActivity.getHandler().sendEmptyMessage(MsgDefine.MSG_SEARH_START);		
		mFileArrayList.clear();
		
		File file_normal = new File(mFilepath+dvr_normal_dir);
		File[] allfiles_normal = file_normal.listFiles(filter);
		if (allfiles_normal != null ) {
			for (int i=0; i<allfiles_normal.length; i++) {
				if (!allfiles_normal[i].getName().equals("Recording.avi")) {
					mFileArrayList.add(allfiles_normal[i].getAbsolutePath());
				}
			}
		}
		
		File file_urgent = new File(mFilepath+dvr_urgent_dir);
		File[] allfiles_urgent = file_urgent.listFiles(filter);
		if (allfiles_urgent != null ) {
			for (int i=0; i<allfiles_urgent.length; i++) {
				if (!allfiles_urgent[i].getName().equals("Recording.avi")) {
					mFileArrayList.add(allfiles_urgent[i].getAbsolutePath());
				}
			}
		}
		
		//排序一下，按时间先后顺序
		if (mFileArrayList.size() > 1) {
			Comparator<Object> comp = new SortComparator();
	        Collections.sort(mFileArrayList,comp);
		}		
        
		//通知搜索结束，UI那边要刷新LISTVIEW
		Message msg = new Message();
		msg.what = MsgDefine.MSG_SEARH_OVER;
		msg.arg1 = mFileArrayList.size();
		FileListActivity.getHandler().sendMessage(msg);
		
		return mFileArrayList.size();
	}
}
