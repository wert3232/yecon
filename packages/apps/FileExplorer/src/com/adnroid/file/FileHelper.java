/*****************Copyright (C), 2010-2015, FORYOU Tech. Co., Ltd.********************/
package com.adnroid.file;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;

import com.adnroid.file.MyActivity.searchEntity;


/**
 * @Filename: FileHelper.java
 * @Author: wanghb
 * @Email: wanghb@foryouge.com.cn
 * @CreateDate: 2011-6-13
 * @Description: description of the new class
 * @Others: comments
 * @ModifyHistory:
 */
public class FileHelper {
	private ArrayList<File> filesList;

	private searchEntity se;


	/** scan all APK files in SD card and ROM. */
	public ArrayList<File> scanAllAPKFile(searchEntity se) {
		this.se = se;
		filesList = new ArrayList<File>();
		getFiles(se.path);
		return filesList;
	}


	// Get all apk files.
	private void getFiles(String path) {
		File f = new File(path);
		File[] files = f.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File ff = files[i];
				if (ff.isDirectory()) {
					getFiles(ff.getPath());
				} else {
					if (se.isKeyworld) {
						if (ff.getName().toLowerCase().indexOf(se.keyworld.toLowerCase()) > -1) {
							filesList.add(ff);
						}
					} else {
						String f_type = MyUtil.getMIMEType(ff, false);
						if (se.type.equals(f_type)) {
							filesList.add(ff);
						}
					}
				}
			}
		}
	}
	/** Check if file exists. */
	public boolean isFileExist(String path) {
		File file = new File(path);

		if (file.exists())
			return true;

		return false;
	}
}
