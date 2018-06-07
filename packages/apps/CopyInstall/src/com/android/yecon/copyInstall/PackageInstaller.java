package com.android.yecon.copyInstall;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.os.Environment;

import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageDeleteObserver;
import android.os.Bundle;
import android.os.FileUtils;


public class PackageInstaller extends Activity {
	
	//add
	private final String INTERNAL_SD = "/storage/sdcard0";
    private final String EXTERNAL_SD = "/storage/sdcard1";
    
	private File mTmpFile;
	private final String TMP_FILE_NAME = "SoftTest.apk";
	private final String TMP_PACKAGE_NAME="com.android.mid.instructions.soft";
	private final String pathString=INTERNAL_SD;
	private final static String TAG = "PackInstaller";
	private Context mContext;

	  @Override
	    protected void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	        mContext=this;
	        instatllBatch(pathString,TMP_PACKAGE_NAME); 
	  }
	
	public void install(String path,String packageName){
		 Intent intent = new Intent(Intent.ACTION_VIEW);
		 intent.setDataAndType(Uri.fromFile(new File(path)),
		 "application/vnd.android.package-archive");
		 mContext.startActivity(intent);
	}
	
	public void instatllBatch(String path, String packageName) {

		Log.i(TAG, "path=" + path);
		int installFlags = 0;
		PackageManager pm = mContext.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			if (pi != null) {
				installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
			}
		} catch (NameNotFoundException e) {
		}
		if ((installFlags & PackageManager.INSTALL_REPLACE_EXISTING) != 0) {
			Log.w(TAG, "Replacing package:" + packageName);
		}

		// Create temp file before invoking install api
		mTmpFile = createTempPackageFile(path);
		if (mTmpFile == null) {
			// Message msg = mHandler.obtainMessage(INSTALL_COMPLETE);
			// msg.arg1 = PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE;
			// mHandler.sendMessage(msg);
			return;
		}
		Uri mPackageURI = Uri.parse("file://" + mTmpFile.getPath());
		String installerPackageName = getIntent().getStringExtra(
				Intent.EXTRA_INSTALLER_PACKAGE_NAME);

		PackageInstallObserver observer = new PackageInstallObserver();
		pm.installPackage(mPackageURI, observer, installFlags,
				installerPackageName);
	}

	private File createTempPackageFile(String filePath) {
		File tmpPackageFile = mContext.getFileStreamPath(TMP_FILE_NAME);
		if (tmpPackageFile == null) {
			Log.w(TAG, "Failed to create temp file");
			return null;
		}
		if (tmpPackageFile.exists()) {
			tmpPackageFile.delete();
		}
		// Open file to make it world readable
		FileOutputStream fos;
		try {
			fos = openFileOutput(TMP_FILE_NAME, MODE_WORLD_READABLE);
		} catch (FileNotFoundException e1) {
			Log.e(TAG, "Error opening file " + TMP_FILE_NAME);
			return null;
		}
		try {
			fos.close();
		} catch (IOException e) {
			Log.e(TAG, "Error opening file " + TMP_FILE_NAME);
			return null;
		}

		File srcPackageFile = new File(filePath);
		if (!FileUtils.copyFile(srcPackageFile, tmpPackageFile)) {
			Log.w(TAG, "Failed to make copy of file: " + srcPackageFile);
			return null;
		}
		return tmpPackageFile;
	}

	private class PackageInstallObserver extends IPackageInstallObserver.Stub {
		public void packageInstalled(String packageName, int returnCode) {
			// Message msg = mHandler.obtainMessage(INSTALL_COMPLETE);
			// msg.arg1 = returnCode;
			// mHandler.sendMessage(msg);
			Log.i(TAG, "====INSTALL_COMPLETE");
		}
	}
	

}
