package com.yecon.carsetting.tuoxian;


import static android.mcu.McuExternalConstant.MCU_DATA_UPGRADE_END;
import static android.mcu.McuExternalConstant.MCU_DATA_UPGRADE_START;
import static android.mcu.McuExternalConstant.MCU_UPGRADE_CONTINUE;
import static android.mcu.McuExternalConstant.MCU_UPGRADE_INFO_TYPE;
import static android.mcu.McuExternalConstant.MCU_UPGRADE_RETRANS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.autochips.storage.EnvironmentATC;
import com.tuoxianui.tools.AtTimerHelpr;
import com.yecon.carsetting.FactorySettingActivity;
import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog.OnConfirmListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.L;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.mcu.McuBaseInfo;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.mcu.McuUpgradeInfo;

public class SystemUpdateActivity extends Activity implements OnClickListener{
	private FragmentManager mFragmentManager;
	private Fragment_List mDialog;
	private static Context mContext;
	private static final String EXT_SDCARD1_PATH = "/mnt/ext_sdcard1";
	private static final String EXT_SDCARD2_PATH = "/mnt/ext_sdcard2";
	private static final String MCU_UPGRADE_DIR = "McuUpgrade";
	private static final String UDISK1_PATH = "/mnt/udisk1";
	private static final String UDISK2_PATH = "/mnt/udisk2";
	
	private static final String MCU_UPGRADE_S19_SUFFIX = ".s19";
	private static final String MCU_UPGRADE_BIN_SUFFIX = ".bin";
	
	private static final int MCU_UPGRADE_TRANS_DATA_SIZE = 128;
	
	private List<String> mASCIIFileDatas = new ArrayList<String>();
	private List<String> mMcuUpgradeFiles = new ArrayList<String>();
	
	byte[] mBinFileDatas = null;
	private String mFileSuffix;
	
	private ProgressDialog mProgressDialog;	
	private int mFileLines = 0;
	private int mRetransCount = 0;
	private android.mcu.McuManager mMcuManager;
	private EnvironmentATC mEnv;
	private McuUpgradeInfo mMcuUpgradeInfo;
	private AtTimerHelpr mAtTimerHelpr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tuoxian_update);
		initData();
		initView();
		//一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
		/** 一汽要求,不自动流转  AtTimerHelpr.AtTimerHelprDoItCallBack atTimerHelprDoItCallBack = new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				backHandler.sendEmptyMessage(0);
			}
		};**/
		//一汽要求,不自动流转  mAtTimerHelpr.setCallBack(atTimerHelprDoItCallBack);
	}
	@Override
	protected void onDestroy() {
		try {
			mMcuManager.RPC_RemoveMcuInfoChangedListener(mMcuListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	private void initView() {
		findViewById(R.id.mcu_update).setOnClickListener(this);
		findViewById(R.id.system_update).setOnClickListener(this);
		findViewById(R.id.faw_update).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}


	private void initData() {
		mFragmentManager = getFragmentManager();
		mContext = this;
		mEnv = new EnvironmentATC(this);
		mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
		try {
			mMcuManager.RPC_RequestMcuInfoChangedListener(mMcuListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(100);
		mProgressDialog.setTitle(R.string.system_mcu_upgrade);
		mProgressDialog.setMessage(getString(R.string.system_mcu_upgrade_prompt));
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.system_update:
			//一汽要求,不自动流转  mAtTimerHelpr.stop();
			onMcuUpgrade();
			break;
		case R.id.faw_update:
			Toast.makeText(this, R.string.system_faw_upgrade_error, Toast.LENGTH_SHORT).show();
			break;
		case R.id.mcu_update:
			//一汽要求,不自动流转  mAtTimerHelpr.stop();
			Fragment_Prompt_dialog dlg = Function
					.onSet_Prompt_dialog(mContext, mFragmentManager, getResources()
							.getString(R.string.make_sure_insert_update));
			dlg.setOnConfirmListener(new OnConfirmListener() {
				@Override
				public void onConfirm() {
					// TODO Auto-generated method stub
					Function.RebootSystem(mContext);
				}
			});
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
	
	private void onMcuUpgrade() {
		boolean hasFile = false;
		StringBuilder mcuUpgradeDir = new StringBuilder();
		File file = null;

		if (!mEnv.getStorageState(EXT_SDCARD1_PATH).equals(Environment.MEDIA_MOUNTED)) {
			hasFile = false;
		} else {
			hasFile = true;

			mcuUpgradeDir.append(EXT_SDCARD1_PATH);
			mcuUpgradeDir.append("/");
			mcuUpgradeDir.append(MCU_UPGRADE_DIR);
		}

		file = new File(mcuUpgradeDir.toString());
		if (!file.exists()) {
			hasFile = false;
		}

		if (!hasFile) {
			if (!mEnv.getStorageState(EXT_SDCARD2_PATH).equals(Environment.MEDIA_MOUNTED)) {
				hasFile = false;
			} else {
				hasFile = true;

				mcuUpgradeDir.setLength(0);
				mcuUpgradeDir.append(EXT_SDCARD2_PATH);
				mcuUpgradeDir.append("/");
				mcuUpgradeDir.append(MCU_UPGRADE_DIR);
			}
		}

		file = new File(mcuUpgradeDir.toString());
		if (!file.exists()) {
			hasFile = false;
		}

		if (!hasFile) {
			if (!mEnv.getStorageState(UDISK1_PATH).equals(Environment.MEDIA_MOUNTED)) {
				hasFile = false;
			} else {
				hasFile = true;

				mcuUpgradeDir.setLength(0);
				mcuUpgradeDir.append(UDISK1_PATH);
				mcuUpgradeDir.append("/");
				mcuUpgradeDir.append(MCU_UPGRADE_DIR);
			}
		}

		file = new File(mcuUpgradeDir.toString());
		if (!file.exists()) {
			hasFile = false;
		}

		if (!hasFile) {
			if (!mEnv.getStorageState(UDISK2_PATH).equals(Environment.MEDIA_MOUNTED)) {
				hasFile = false;
			} else {
				hasFile = true;

				mcuUpgradeDir.setLength(0);
				mcuUpgradeDir.append(UDISK2_PATH);
				mcuUpgradeDir.append("/");
				mcuUpgradeDir.append(MCU_UPGRADE_DIR);
			}
		}

		L.e("onMcuUpgrade - upgrade dir: " + mcuUpgradeDir.toString());

		file = new File(mcuUpgradeDir.toString());
		if (!file.exists()) {
			hasFile = false;
		}

		if (hasFile) {
			mMcuUpgradeFiles = loadingUpgradeFiles(mcuUpgradeDir.toString());
			L.e("onMcuUpgrade - upgrade file size: " + mMcuUpgradeFiles.size());
		}

		if (mMcuUpgradeFiles == null || mMcuUpgradeFiles.size() <= 0) {
			hasFile = false;
		} else {
			hasFile = true;
		}

		L.e("onMcuUpgrade - hasFile: " + hasFile);

		if (!hasFile) {
			Toast.makeText(this, R.string.system_mcu_upgrade_error, Toast.LENGTH_SHORT).show();
		} else {
			int size = mMcuUpgradeFiles.size();
			String[] upgradFilesArray = new String[size];
			for (int i = 0; i < size; i++) {
				String path = mMcuUpgradeFiles.get(i);
				int lastIndex = path.lastIndexOf(File.separator);
				upgradFilesArray[i] = path.substring(lastIndex + 1);
			}

			mDialog = Function.buildListDialog(R.id.system_mcu_upgrade, mFragmentManager,
					upgradFilesArray, false,true);
			mDialog.setOnItemClickListener(new OnListDlgListener() {

				@Override
				public void onListDlgSetValue(int id, final int value) {
					// TODO Auto-generated method stub
					Fragment_Prompt_dialog dlg = Function
							.onSet_Prompt_dialog(mContext, mFragmentManager, getResources()
									.getString(R.string.system_mcu_upgrade));
					dlg.setOnConfirmListener(new OnConfirmListener() {
						@Override
						public void onConfirm() {
							// TODO Auto-generated method stub
							String filename = mMcuUpgradeFiles.get(value);
							L.e("onMcuUpgrade - read filename: " + filename);
							if (filename.endsWith(MCU_UPGRADE_S19_SUFFIX)) {
								readS19UpgradeFileData(filename);
								mFileSuffix = MCU_UPGRADE_S19_SUFFIX;
							} else if (filename.endsWith(MCU_UPGRADE_BIN_SUFFIX)) {
								readBinUpgradeFileData(filename);
								mFileSuffix = MCU_UPGRADE_BIN_SUFFIX;
							}
							mProgressDialog.show();
							try {
								mMcuManager.RPC_UpdateMCU(MCU_DATA_UPGRADE_START);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					});
				}
			});
		}
	}
	
	private List<String> loadingUpgradeFiles(String path) {
		List<String> fileList = new ArrayList<String>();

		FilenameFilter filter = new FilenameFilter() {

			@SuppressLint("DefaultLocale")
			@Override
			public boolean accept(File file, String filename) {
				String lowercasename = filename.toLowerCase();
				if (lowercasename.endsWith(MCU_UPGRADE_S19_SUFFIX)
						|| lowercasename.endsWith(MCU_UPGRADE_BIN_SUFFIX)) {
					return true;
				}
				return false;
			}
		};

		File file = new File(path);
		File[] filesArray = file.listFiles(filter);
		for (File f : filesArray) {
			fileList.add(f.getPath());
		}
		Collections.sort(fileList);

		return fileList;
	}
	
	private void readS19UpgradeFileData(String filename) {
		mFileLines = 0;
		if (mASCIIFileDatas != null && mASCIIFileDatas.size() > 0) {
			mASCIIFileDatas.clear();
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));

			String line = "";
			int lines = 0;
			while ((line = reader.readLine()) != null) {
				lines++;
				mASCIIFileDatas.add(line);
			}
			L.e("file lines: " + lines);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void readBinUpgradeFileData(String filename) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
			int len = fis.available();
			L.e("readBinUpgradeFileData - len: " + len);
			int remainder = len % MCU_UPGRADE_TRANS_DATA_SIZE;
			if (remainder != 0) {
				len += MCU_UPGRADE_TRANS_DATA_SIZE - remainder;
			}
			mBinFileDatas = new byte[len];
			fis.read(mBinFileDatas);
			L.e("readBinUpgradeFileData - need len: " + mBinFileDatas.length);

			// int num = len / MCU_UPGRADE_TRANS_DATA_SIZE;
			// for (int i = 0; i < num; i++) {
			// byte[] valueLine = Arrays.copyOfRange(mBinFileDatas, i
			// * MCU_UPGRADE_TRANS_DATA_SIZE,
			// (i + 1) * MCU_UPGRADE_TRANS_DATA_SIZE);
			// StringBuffer sb = new StringBuffer();
			// for (int j = 0; j < valueLine.length; j++) {
			// String str = String.format("%02X ", valueLine[j]);
			// sb.append(str);
			// }
			// Log.e(TAG, (i + 1) + " - len: " + valueLine.length + " - " +
			// sb.toString());
			// }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void onS19FileUpgrade(int upgradeState) {
		L.e("onMcuInfoChanged - mFileLines: " + mFileLines);

		int size = mASCIIFileDatas.size();
		if (mFileLines == (size - 1) || (mRetransCount == 3 && upgradeState == MCU_UPGRADE_RETRANS)) {
			if (mFileLines == (size - 1)) {
				mProgressDialog.setProgress(100);
				L.e("onMcuInfoChanged - mcu upgrade end");
			} else {
				L.e("onMcuInfoChanged - mRetransCount: " + mRetransCount);
			}

			try {
				mMcuManager.RPC_UpdateMCU(MCU_DATA_UPGRADE_END);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return;
		}

		mProgressDialog.setProgress((int) ((float) mFileLines / size * 100));

		byte[] valueLine = mASCIIFileDatas.get(mFileLines).getBytes();

		// StringBuffer sb = new StringBuffer();
		// for (int j = 0; j < valueLine.length; j++) {
		// String str = String.format("0x%02X ", valueLine[j]);
		// sb.append(str);
		// }
		// Log.e(TAG, "len: " + valueLine.length + " - " + sb.toString());

		try {
			mMcuManager.RPC_SendMCUData(valueLine, valueLine.length);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (upgradeState == MCU_UPGRADE_CONTINUE) {
			mFileLines++;
			mRetransCount = 0;
		} else if (upgradeState == MCU_UPGRADE_RETRANS) {
			mRetransCount++;
		}
	}

	private void onBinFileUpgrade(int upgradeState) {
		L.e("onMcuInfoChanged - mFileLines: " + mFileLines);

		int num = mBinFileDatas.length / MCU_UPGRADE_TRANS_DATA_SIZE;

		if (mFileLines == num || (mRetransCount == 3 && upgradeState == MCU_UPGRADE_RETRANS)) {
			if (mFileLines == num) {
				mProgressDialog.setProgress(100);
				L.e( "onMcuInfoChanged - mcu upgrade end");
			} else {
				L.e("onMcuInfoChanged - mRetransCount: " + mRetransCount);
			}

			try {
				mMcuManager.RPC_UpdateMCU(MCU_DATA_UPGRADE_END);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return;
		}

		mProgressDialog.setProgress((int) ((float) mFileLines / num * 100));

		byte[] valueLine = Arrays.copyOfRange(mBinFileDatas, mFileLines
				* MCU_UPGRADE_TRANS_DATA_SIZE, (mFileLines + 1) * MCU_UPGRADE_TRANS_DATA_SIZE);
		try {
			mMcuManager.RPC_SendMCUData(valueLine, valueLine.length);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (upgradeState == MCU_UPGRADE_CONTINUE) {
			mFileLines++;
			mRetransCount = 0;
		} else if (upgradeState == MCU_UPGRADE_RETRANS) {
			mRetransCount++;
		}
	}
	
	private McuListener mMcuListener = new McuListener() {

		@Override
		public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {
			if (mcuBaseInfo == null) {
				L.e("onMcuInfoChanged - mcuBaseInfo is null");
				return;
			}

			if (infoType == MCU_UPGRADE_INFO_TYPE) {
				mMcuUpgradeInfo = mcuBaseInfo.getUpgradeInfo();
				int upgradeState = mMcuUpgradeInfo.getUpgradeState();
				L.e("onMcuInfoChanged - upgradeState: " + upgradeState);
				if (mFileSuffix.equals(MCU_UPGRADE_S19_SUFFIX)) {
					onS19FileUpgrade(upgradeState);
				} else if (mFileSuffix.equals(MCU_UPGRADE_BIN_SUFFIX)) {
					onBinFileUpgrade(upgradeState);
				}
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		//一汽要求,不自动流转  mAtTimerHelpr.start(10);
	}
	@Override
	protected void onPause() {
		super.onPause();
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.onTouchEvent(event);
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//一汽要求,不自动流转  mAtTimerHelpr.reset();
		return super.dispatchTouchEvent(ev);
	}
	Handler backHandler = new Handler() {
		public void handleMessage(Message msg) {
			onBackPressed();
		}
	};
}
