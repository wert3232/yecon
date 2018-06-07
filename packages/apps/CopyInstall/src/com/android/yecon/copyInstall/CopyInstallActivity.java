package com.android.yecon.copyInstall;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yecon.copyInstall.FileManager.onListItemClickListener;

public class CopyInstallActivity extends Activity implements OnClickListener,
		onListItemClickListener {

	final String TAG = "CopyInstallActivity";
	Integer[] IDs_btn = { R.id.btn_source_path, R.id.btn_target_path, R.id.btn_snsource_path,
			R.id.btn_sntarget_path, R.id.copy, R.id.btn_apk_path, R.id.install,
			R.id.activation_key, R.id.close, };

	Button mButton[] = new Button[IDs_btn.length];

	private TextView mText_source_path, mText_target_path, mText_snsource_path,
			mText_sntarget_path, mText_apk_path;
	private TextView mCopyFinishView;
	private TextView mInstallFinishView;
	private TextView mMessageView;
	// add
	private final String INTERNAL_SD = "/mnt/sdcard";
	private final static String EXTERNAL_SD = "/mnt/ext_sdcard1";
	private final String COPY_FINISH = "copy_finish";
	public static String SOURCE_PATH_APK = EXTERNAL_SD + "/apk";
	private String SOURCE_PATH_DATA = EXTERNAL_SD + "/NaviOne";
	private String TARGET_PATH_DATA = INTERNAL_SD;

	private String TARGET_PATH_APK = INTERNAL_SD + "/apk";
	private String MAP_File = EXTERNAL_SD + "/cldasn.cld";
	private String TARGET_PATH_SN = INTERNAL_SD + "/NaviOne";

	private String mSdcardState;
	private String mNandState;
	private String absolutePath;

	private File mSourceDataDirs;
	private File mNandDirs;
	private List<File> mPreList;
	private List<File> mApkList;
	private int mPreLen;
	private int mApkLen;
	private long mCopyFileSize = 0;
	private long mNandAvailableSize = 0;
	private Object[] prelist;
	private Object[] apklist;

	private int lowMemoryPercent = 12;
	private float lowmemorySize;
	private long availSize;
	private long TotalSize;
	private long LowMemory;

	private final int COPY_DIALOG = 100;
	private final int INSTALL_DIALOG = 101;
	private final int COPY_HANDLER = 201;
	private final int INSTALL_HANDLER = 202;
	private final int FILE_FAIL = 203;
	private final int HANDLE_INSTALL_MESSAGE = 300;
	private final int HANDLE_FINISH_INSTALL = 301;
	private final int HANDLE_FIRST_INSTALL_MESSAGE = 302;
	private final int INSTALL_INTENT_CALLBACK = 400;

	private static ProgressDialog mDialog = null;
	private int copyFileNum = 0;
	private int installApkNum = 0;

	private boolean mDidRegister = false;
	private boolean mStorageShared = false;
	private boolean mIsCopying = false;
	private boolean mIsInstalling = false;
	private boolean mCopyFinish = false;
	private boolean mInstallFinish = false;
	private boolean mSdcardUnmounted;
	private boolean mNandUnmount;

	Runnable mLowMemoryHandler = new Runnable() {
		public void run() {
			mInstallFinishView.setText(R.string.low_memory_message);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// String id = SystemProperties.get("ro.build.display.id");
		// if (id.indexOf(getResources().getString(R.string.version_id)) == -1)
		// {
		// Toast.makeText(this, R.string.no_permission,
		// Toast.LENGTH_SHORT).show();
		// finish();
		// return;
		// }
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);

		// Initialize.
		mSdcardState = Environment.getStorageState(EXTERNAL_SD);
		mNandState = Environment.getStorageState(INTERNAL_SD);
		// mSdcardDirs = new File(SDCARD_PATH);
		mSourceDataDirs = new File(SOURCE_PATH_DATA);
		// mNandDirs = new File(TARGET_PATH_APK);
		mPreList = new ArrayList<File>();
		mApkList = new ArrayList<File>();

		FileManager.setOnItemClickListener(this);
		int i = 0;
		for (Button button : mButton) {
			button = (Button) findViewById(IDs_btn[i]);
			button.setOnClickListener(this);
			i++;
		}

		mText_source_path = (TextView) findViewById(R.id.text_source_path);
		mText_target_path = (TextView) findViewById(R.id.text_target_path);
		mText_apk_path = (TextView) findViewById(R.id.text_apk_path);
		mText_snsource_path = (TextView) findViewById(R.id.text_snsource_path);
		mText_sntarget_path = (TextView) findViewById(R.id.text_sntarget_path);
		mText_source_path.setText(SOURCE_PATH_DATA);
		mText_target_path.setText(INTERNAL_SD);
		mText_apk_path.setText(SOURCE_PATH_APK);
		mText_snsource_path.setText(MAP_File);
		mText_sntarget_path.setText(TARGET_PATH_SN);

		mCopyFinishView = (TextView) findViewById(R.id.copy_text);
		mInstallFinishView = (TextView) findViewById(R.id.install_text);
		mMessageView = (TextView) findViewById(R.id.message_text);

		// If NandFlash or SD card were in shared state,set the buttons can not
		// be clicked.
		if (mSdcardState.equals(Environment.MEDIA_SHARED)
				|| mNandState.equals(Environment.MEDIA_SHARED)) {
			mMessageView.setText(R.string.nand_sd_in_shared);
			((Button) findViewById(R.id.copy)).setEnabled(false);
			((Button) findViewById(R.id.install)).setEnabled(false);
			return;
		}

		// (new File(TARGET_PATH_DATA)).mkdirs();
		// (new File(TARGET_PATH_APK)).mkdirs();
		// getApkFiles(new File(TARGET_PATH_APK));

		if (!mSourceDataDirs.exists()) {
			mMessageView.setText(R.string.sdcard_dirs_not_exit);
			((Button) findViewById(R.id.copy)).setEnabled(false);
			return;
		}

		// previewFiles(new File(SDCARD_PATH));
		// preNandSize(new File(TARGET_PATH_DATA));

		if (isExist(SOURCE_PATH_DATA))
			mCopyFileSize = previewFiles(new File(SOURCE_PATH_DATA)); // Get the
		// bytes of
		// the all copy
		// files.
		if (isExist(TARGET_PATH_DATA))
			mNandAvailableSize = preNandSize(new File(TARGET_PATH_DATA)); // Get
		// the
		// bytes
		// of the
		// NandFlash
		// that
		// available.
		// ----pz----- start ------
		if (mSdcardState.equals(Environment.MEDIA_MOUNTED)) {
			mSdcardUnmounted = false;
			mStorageShared = false;
			mNandUnmount = false;
			updateUI();
			Log.d(TAG, "--onCreate()----mounted-----");
		} else {
			Log.d(TAG, "--onCreate()----unmounted----");
		}
		// ----pz----- end ------
	}

	// public void goToCopyAnother() {
	// mIsCopying = true;
	// File[] files = (new File(SDCARD_PATH)).listFiles();
	// Log.v(TAG, "**goToCopyAnother**files.length-----***" + files.length);
	// for (int i = 0; i < files.length; i++) {
	// if (files[i].isFile()) {
	// copyFile(files[i], new File(TARGET_PATH_DATA + "/" +
	// files[i].getName()));
	// copyFileNum++;
	// MainHandler.sendEmptyMessage(COPY_HANDLER);
	// }
	// /*
	// * else { String sourceDir = SDCARD_PATH + File.separator +
	// * files[i].getName(); String targetDir = NAND_PATH + File.separator
	// * + files[i].getName(); try { copyDirectiory(sourceDir, targetDir);
	// * } catch (IOException e) { e.printStackTrace(); } }
	// */
	//
	// }
	// }

	/**
	 * copy apk method.
	 */
	@SuppressWarnings("static-access")
	public void goToCopyApk() {
		mIsCopying = true;
		File[] files = (new File(SOURCE_PATH_APK)).listRoots();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				copyFile(files[i], new File(TARGET_PATH_APK + files[i].getName()));
			}
			if (files[i].isDirectory()) {
				String sourceDir = SOURCE_PATH_APK + File.separator + files[i].getName();
				String targetDir = TARGET_PATH_APK + File.separator + files[i].getName();
				try {
					copyDirectiory(sourceDir, targetDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * copy apk_data method.
	 */
	@SuppressWarnings("static-access")
	public void goToCopyData() {
		mIsCopying = true;

		String targetPath = TARGET_PATH_DATA
				+ SOURCE_PATH_DATA.substring(SOURCE_PATH_DATA.lastIndexOf('/'));
		if (isExist(targetPath)) {
			File file = new File(targetPath);
			file.mkdirs();
		}

		copyFolder(SOURCE_PATH_DATA, targetPath);

		// File[] files = (new File(SDCARD_PATH_DATA)).listRoots();
		// for (int i = 0; i < files.length; i++) {
		// if (files[i].isFile()) {
		// copyFile(files[i], new File(NAND_PATH + files[i].getName()));
		// }
		// if (files[i].isDirectory()) {
		// String sourceDir = SDCARD_PATH_DATA + File.separator +
		// files[i].getName();
		// String targetDir = NAND_PATH + File.separator + files[i].getName();
		// try {
		// copyDirectiory(sourceDir, targetDir);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }
	}

	public void copyFolder(String oldPath, String newPath) {
		try {
			File createFile = new File(newPath);
			createFile.mkdirs(); // 如果文件夹不存在则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();// 列出给定目录中的内容
			File temp = null;
			for (int i = 0; i < file.length; i++) {

				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {// 如果是文件
					copyFileNum++;
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/"
							+ (temp.getName()).toString());
					byte[] b = new byte[1024];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是目录，即子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);// 重新遍历
				}
				MainHandler.sendEmptyMessage(COPY_HANDLER);
			}
		} catch (Exception e) {
			System.out.println("复制文件夹内容操作出错");
			e.printStackTrace();
		}
	}

	// lzy add
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static boolean isExist(String path) {

		boolean bisExist = false;
		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (file.exists()) {
			// file.mkdir();
			bisExist = true;
		}
		return bisExist;
	}

	/**
	 * Get the all filse' size.
	 * 
	 * @param file
	 * @return
	 */
	private long previewFiles(File file) {
		long fileSize = 0;
		final File[] files = file.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					fileSize += previewFiles(files[i]);
				} else if (files[i].isFile()) {
					fileSize += files[i].length();
					mPreList.add(files[i]);
				}
			}
		}

		prelist = mPreList.toArray();
		mPreLen = prelist.length;
		Log.v(TAG, "--------mPreLen: " + mPreLen);
		Log.d(TAG, "fileSize = " + fileSize);

		return fileSize;
	}

	/**
	 * Get the apk files,and put them into fileList.
	 * 
	 * @param file
	 */
	private void getApkFiles(final File file) {
		final File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					getApkFiles(f);
				} else {
					if (f.getName().endsWith(".apk") || f.getName().endsWith(".APK"))
						mApkList.add(f);
				}
			}
		}
		apklist = mApkList.toArray();
		mApkLen = apklist.length;
		Log.v(TAG, "--------mApkLen: " + mApkLen);
	}

	/**
	 * Get the available size of the NandFlash.
	 * 
	 * @param file
	 * @return
	 */
	private long preNandSize(File file) {
		long nandSize = 0;
		StatFs stat = new StatFs(TARGET_PATH_DATA);
		long blockSize = stat.getBlockSize();
		int avail = stat.getAvailableBlocks();
		nandSize = blockSize * avail;

		return nandSize;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.copy) {

			String targetPath = TARGET_PATH_DATA
					+ SOURCE_PATH_DATA.substring(SOURCE_PATH_DATA.lastIndexOf('/'));

			if (isExist(targetPath)) {
				deleteDir(new File(targetPath));
			}
			mNandAvailableSize = preNandSize(new File(TARGET_PATH_DATA));

			mPreList.clear();
			copyFileNum = 0;

			Log.i(TAG, "state = " + mSdcardState);
			Log.i(TAG, "mSdcardUnounted " + mSdcardUnmounted);
			Log.i(TAG, "mNandAvailableSize = " + mNandAvailableSize);
			Log.i(TAG, "mCopyFileSize = " + mCopyFileSize);
			if (mNandAvailableSize < mCopyFileSize) {
				Toast.makeText(this, R.string.not_enough_space, Toast.LENGTH_SHORT).show();
				mMessageView.setText(R.string.not_enough_space);
				Log.w(TAG, "Not enough space to copy or storage not ready. ");
				return;
			}

			if (!mSourceDataDirs.exists()) {
				mMessageView.setText(R.string.sdcard_dirs_not_exit);
			}

			previewFiles(mSourceDataDirs);
			// previewFiles(new File(SDCARD_PATH));
			if (mPreLen == 0) {
				Toast.makeText(this, R.string.no_files, Toast.LENGTH_SHORT).show();
				mCopyFinishView.setText(R.string.no_files);
				return;
			}
			mCopyFinishView.setText(R.string.copy_text_defalut);

			showDialog(COPY_DIALOG);
			mDialog.setProgress(0);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.w(TAG, "goToCopy");
					// goToCopyAnother();
					if (mSourceDataDirs.exists())
						goToCopyData();

				}
			}).start();
		} else if (v.getId() == R.id.install) {
			mInstallFinishView.setText("");
			installApkNum = 0;
			mApkList.clear();
			// if (mSPf.getInt(INSTALL_YES, -1) == 1) {
			// Toast.makeText(this, R.string.is_already_install_message,
			// Toast.LENGTH_LONG).show();
			// return;
			// }

			// by lzy modify
			getApkFiles(new File(SOURCE_PATH_APK));
			Log.i(TAG, "mCopyFinish = " + mCopyFinish);
			Log.i(TAG, "mApkLen = " + mApkLen);
			if (mApkLen == 0 && !mCopyFinish) {
				Toast.makeText(this, R.string.no_apk_message, Toast.LENGTH_SHORT).show();
				return;
			}
			Log.i(TAG, "----installing");
			mInstallFinishView.setText(R.string.install_text_defalut);
			Intent mIntent = new Intent();
			mIntent.setClass(CopyInstallActivity.this, ApkAutoInstall_test.class);
			startActivity(mIntent);
			finish();
		} else if (v.getId() == R.id.close) {
			finish();
		} else if (v.getId() == R.id.activation_key) {

			if (0 == copyKeyFile(MAP_File, TARGET_PATH_SN)) {
				Toast.makeText(this, R.string.activation_key_success, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, R.string.activation_key_failure, Toast.LENGTH_SHORT).show();
			}
		} else if (v.getId() == R.id.btn_source_path) {
			Intent mIntent = new Intent();
			mIntent.setClass(this, FileManager.class);
			mIntent.putExtra("id", 0);
			startActivity(mIntent);
		} else if (v.getId() == R.id.btn_target_path) {
			Intent mIntent = new Intent();
			mIntent.setClass(this, FileManager.class);
			mIntent.putExtra("id", 1);
			startActivity(mIntent);
		} else if (v.getId() == R.id.btn_apk_path) {
			Intent mIntent = new Intent();
			mIntent.setClass(this, FileManager.class);
			mIntent.putExtra("id", 2);
			startActivity(mIntent);
		} else if (v.getId() == R.id.btn_snsource_path) {
			Intent mIntent = new Intent();
			mIntent.setClass(this, FileManager.class);
			mIntent.putExtra("id", 3);
			startActivity(mIntent);
		} else if (v.getId() == R.id.btn_sntarget_path) {
			Intent mIntent = new Intent();
			mIntent.setClass(this, FileManager.class);
			mIntent.putExtra("id", 4);
			startActivity(mIntent);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case COPY_DIALOG:
			Log.v(TAG, "-------copyDialog!");
			mDialog = new ProgressDialog(this);
			mDialog.setTitle(getResources().getString(R.string.copying_title));
			mDialog.setMax(mPreLen);
			mDialog.setCancelable(false);
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			break;
		}
		return mDialog;
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				mSdcardUnmounted = false;
				mStorageShared = false;
				mNandUnmount = false;
				updateUI();
				Log.v(TAG, "mounted");
			} else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
					|| action.equals(Intent.ACTION_MEDIA_EJECT)) {
				mSdcardUnmounted = true;
				mNandUnmount = true;
				updateUI();
				isCopying(mIsCopying);
				Log.v(TAG, "unmounted");
			} else if (action.equals(Intent.ACTION_MEDIA_SHARED)) {
				mStorageShared = true;
				updateUI();
				Log.v(TAG, "shared");
			} else if (action.equals(Intent.ACTION_MEDIA_CHECKING)) {
				mMessageView.setText(R.string.prepare_sd);
				Log.v(TAG, "checking");
			} else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
				Log.v(TAG, "scanner.finish");
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		if (mDidRegister) {
			unregisterReceiver(mReceiver);
			mDidRegister = false;
		}
		// if (mHandler.hasMessages(INSTALL_HANDLER))
		// mHandler.removeMessages(INSTALL_HANDLER);
		if (MainHandler.hasMessages(COPY_HANDLER))
			MainHandler.removeMessages(COPY_HANDLER);
		// finish();

	}

	protected void isCopying(boolean Copying) {
		if (Copying) {
			mDialog.dismiss();
			mCopyFinishView.setText(R.string.sdcard_eject);
			((Button) findViewById(R.id.copy)).setEnabled(!mIsCopying);
			return;
		}
	}

	/*
	 * 2.如果数据误差在0.1m左右，那么表示，数据拷贝ok，如果不是，那么数据有丢失
	 */
	public boolean compareSpace() {
		File file = new File(SOURCE_PATH_DATA);
		double sourceSize = getDirSize(file);

		String targetPath = TARGET_PATH_DATA
				+ SOURCE_PATH_DATA.substring(SOURCE_PATH_DATA.lastIndexOf('/'));
		file = new File(targetPath);
		double targetSize = getDirSize(file);

		if (Math.abs(targetSize - sourceSize) <= 0.1) {
			return true;
		} else {
			return false;
		}
	}

	public static double getDirSize(File file) {
		// 判断文件是否存在
		if (file.exists()) {
			// 如果是目录则递归计算其内容的总大小
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				double size = 0;
				for (File f : children)
					size += getDirSize(f);
				return size;
			} else {// 如果是文件则直接返回其大小,以“兆”为单位
				double size = (double) file.length() / 1024 / 1024;
				return size;
			}
		} else {
			System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
			return 0.0;
		}
	}

	protected void updateUI() {
		// ((Button) findViewById(R.id.copy)).setEnabled(!mSdcardUnmounted &&
		// !mCopyFinish);
		if (!mStorageShared) {
			if (!mInstallFinish) {
				if (mApkLen != 0) {
					((Button) findViewById(R.id.install)).setEnabled(true);
				}
			} else {
				mInstallFinishView.setText(R.string.install_finish);
				((Button) findViewById(R.id.install)).setEnabled(false);
			}
		}

		if (mSdcardUnmounted) {
			mMessageView.setText(R.string.sdcard_dirs_not_exit);
			mCopyFinishView.setText("");
			((Button) findViewById(R.id.copy)).setEnabled(false);
		} else {
			if (mCopyFinish) {
				// 添加拷贝完整性校验
				if (compareSpace()) {
					// 完整性校验ok 误差在0.1k之间
					mCopyFinishView.setText(R.string.copy_finish);
					mCopyFinishView.setTextColor(Color.WHITE);
					mMessageView.setText("");
					((Button) findViewById(R.id.copy)).setEnabled(false);

				} else {
					// 提示重新拷贝
					mCopyFinishView.setText(R.string.copy_fail);
					mMessageView.setText("");
					mCopyFinishView.setTextColor(Color.RED);
					((Button) findViewById(R.id.copy)).setEnabled(true);
				}

			} else {
				if (!mSourceDataDirs.exists()) {
					mMessageView.setText(R.string.sdcard_dirs_not_exit);
					((Button) findViewById(R.id.copy)).setEnabled(false);
				} else {
					mCopyFinishView.setText("");
					mMessageView.setText("");
					((Button) findViewById(R.id.copy)).setEnabled(true);
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "---- onResume() -----");
		if (mSdcardState.equals(Environment.MEDIA_MOUNTED)) {
			Log.d(TAG, "mSdcardState = " + mSdcardState);
			mSdcardUnmounted = false;
			mStorageShared = false;
			mNandUnmount = false;
			updateUI();
			Log.d(TAG, "---onResume()---mounted");
		} else {
			Log.d(TAG, "---onResume()---mSdcardState is null");
		}
		installIntentFilter();
	}

	private void installIntentFilter() {
		// install an intent filter to receive SD card related events.
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
		intentFilter.addDataScheme("file");
		registerReceiver(mReceiver, intentFilter);
		mDidRegister = true;
	}

	/**
	 * Copy the source files to target directory.
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public void copyFile(File sourceFile, File targetFile) {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			byte[] b = new byte[1024 * 10];
			int len;
			try {
				while ((len = inBuff.read(b)) > 0) {
					outBuff.write(b, 0, len);
				}

			} catch (IOException e) {
				Log.v(TAG, "**copyFile**" + e);
				MainHandler.sendEmptyMessage(FILE_FAIL);
				// Toast.makeText(this, "", 1000).show();

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} finally {
			try {
				if (outBuff != null)
					outBuff.flush();
				Log.v(TAG, "this is will read ");
				if (inBuff != null)
					inBuff.close();
				if (outBuff != null)
					outBuff.close();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * Copy source directory to the target area.
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	public void copyDirectiory(String sourceDir, String targetDir) throws IOException {
		(new File(targetDir)).mkdirs();
		File[] file = (new File(sourceDir)).listFiles();
		for (File files : file) {
			if (files.isFile()) {
				File sourceFile = files;
				File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator
						+ files.getName());
				copyFile(sourceFile, targetFile);
				copyFileNum++;

				Log.d(TAG, files.getName());
				Log.d(TAG, "copyFileNum = " + copyFileNum);
			}
			if (files.isDirectory()) {
				String dir1 = sourceDir + "/" + files.getName();
				String dir2 = targetDir + "/" + files.getName();
				copyDirectiory(dir1, dir2);
			}
			MainHandler.sendEmptyMessage(COPY_HANDLER);
		}
	}

	private Handler MainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case COPY_HANDLER:
				mDialog.setProgress(copyFileNum);

				if (copyFileNum >= mPreLen) {
					if (mDialog != null && mDialog.isShowing())
						mDialog.dismiss();
					mPreList.clear();
					copyFileNum = 0;
					mCopyFinishView.setText(R.string.copy_finish);
					((Button) findViewById(R.id.copy)).setEnabled(false);
					mIsCopying = false;
					mCopyFinish = true;
				}

				break;

			case FILE_FAIL:
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
				mCopyFinishView.setText(R.string.copy_file_fail);
				break;
			}
		}
	};

	public static int copyKeyFile(String sFile, String dstPath) {
		if ((null == sFile) || (null == dstPath)) {
			return -1;
		}

		try {
			File srcFile = new File(sFile);
			if (!srcFile.exists())// 源文件不存在
			{
				return -1;
			}

			File dstFileDir = new File(dstPath);
			if (!dstFileDir.exists())// 目标文件夹不存在
			{
				return -1;
			}
			String filName = srcFile.getName();// 获取源文件名称
			if (filName == null || "".equals(filName))// 名称为空
			{
				return -1;
			}
			File dstFile = new File(dstPath + File.separator + filName);
			if (dstFile.exists())// 目标文件存在就删除
			{
				if (!dstFile.delete()) {
					return -1;
				}
			}

			if (!dstFile.createNewFile())// 创建新的目标文件用于拷贝
			{
				return -1;
			}

			// 以下内容为拷贝
			FileInputStream in = new FileInputStream(srcFile);
			FileOutputStream out = new FileOutputStream(dstFile);
			byte[] dataBuffer = new byte[512];
			int readlength = 0;
			while ((readlength = in.read(dataBuffer)) != -1) {
				out.write(dataBuffer, 0, readlength);
			}

			out.flush();
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	@Override
	public void onSetListItem(int id, String value) {
		// TODO Auto-generated method stub
		if (id == 0) {
			mText_source_path.setText(value);
			SOURCE_PATH_DATA = value;
			mSourceDataDirs = new File(SOURCE_PATH_DATA);
			((Button) findViewById(R.id.copy)).setEnabled(true);
		} else if (id == 1) {
			mText_target_path.setText(value);
			TARGET_PATH_DATA = value;
			((Button) findViewById(R.id.copy)).setEnabled(true);
		} else if (id == 2) {
			mText_apk_path.setText(value);
			SOURCE_PATH_APK = value;
			((Button) findViewById(R.id.install)).setEnabled(true);
		} else if (id == 3) {
			MAP_File = value;
			mText_snsource_path.setText(value);
		} else if (id == 4) {
			TARGET_PATH_SN = value;
			mText_sntarget_path.setText(value);
		}
	}

}
