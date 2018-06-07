package com.yecon.carsetting;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.mcu.McuBaseInfo;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.mcu.McuUpgradeInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.view.View;
import android.widget.Toast;

import com.autochips.storage.EnvironmentATC;
import com.yecon.carsetting.fragment.Fragment_Keyboard_num;
import com.yecon.carsetting.fragment.Fragment_Keyboard_num.OnKeyboardListener;
import com.yecon.carsetting.fragment.Fragment_List;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.fragment.Fragment_Progress;
import com.yecon.carsetting.fragment.Fragment_Progress.OnProgressDlgListener;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog.OnConfirmListener;
import com.yecon.carsetting.fragment.Fragment_TimeZone;
import com.yecon.carsetting.fragment.Fragment_TimeZone.OnItemClickListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.unitl.XmlRW;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;
import com.yecon.carsetting.view.HeaderLayout.onTwoRadioButtonListener;

public class FactorySettingActivity extends Activity implements onOneButtonListener,
		onOneCheckBoxListener, onTwoRadioButtonListener, OnProgressDlgListener, OnListDlgListener, OnKeyboardListener {

	int ID_OneButton[] = { R.id.factory_language_select, R.id.factory_timezone_set,
			R.id.factory_radio_set, R.id.factory_navigation_set, R.id.factory_bt_set,
			R.id.factory_backcar_set, R.id.factory_gain_adjust, R.id.factory_sound_effect,
			R.id.factory_volume_set, R.id.factory_video_set, R.id.factory_yuv_set,
			R.id.factory_other_set, R.id.system_volume_adjust, R.id.system_factory_testing,
			R.id.factory_reset_def, R.id.factory_xml_export, R.id.factory_save_reboot,
			R.id.factory_system_updata_usb, R.id.system_mcu_upgrade,
			R.id.system_factory_fun_config, R.id.system_factory_lcd_config, R.id.factory_mapdata_upgrade,
			R.id.factory_tocuh_key_learn, R.id.factory_tocuh_calibration, R.id.factory_reboot, };

	int ID_OneCheck[] = {};
	
	int ID_TextView[] = { R.id.factory_password };

	int ID_TwoRadio_UsbMode[] = { R.id.system_usb_model, };
	int ID_TwoRadio_UsbType[] = { R.id.system_usb_type, };

	private HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	private HeaderLayout mLayout_OneCheck[] = new HeaderLayout[ID_OneCheck.length];
	private HeaderLayout mLayout_TwoRadio_UsbMode[] = new HeaderLayout[ID_TwoRadio_UsbMode.length];
	private HeaderLayout mLayout_TwoRadio_UsbType[] = new HeaderLayout[ID_TwoRadio_UsbType.length];
	
	HeaderLayout mLayout_TextView[] = new HeaderLayout[ID_TextView.length];
	
	String mStr_password = XmlParse.factory_password;
	
	private static Context mContext;
	// IPowerManager power;
	// PowerManager pm;
	FragmentManager mFragmentManager;
	Fragment_List mDialog;

	private static final String TAG = "FactorySetting";
	private static final String EXT_SDCARD1_PATH = "/mnt/ext_sdcard1";
	private static final String EXT_SDCARD2_PATH = "/mnt/ext_sdcard2";
	private static final String UDISK1_PATH = "/mnt/udisk1";
	private static final String UDISK2_PATH = "/mnt/udisk2";
	private static final String MCU_UPGRADE_DIR = "McuUpgrade";
	private static final String MCU_UPGRADE_S19_SUFFIX = ".s19";
	private static final String MCU_UPGRADE_BIN_SUFFIX = ".bin";
	private static final String USB_UPGRADE_FILE = "yc8317.img";
	private static final String XML_EXPORT_FILE = "factoryconfig.xml";
	private static final int MCU_UPGRADE_TRANS_DATA_SIZE = 128;

	private ProgressDialog mProgressDialog;
	private EnvironmentATC mEnv;
	private List<String> mMcuUpgradeFiles = new ArrayList<String>();
	private List<String> mASCIIFileDatas = new ArrayList<String>();
	private byte[] mBinFileDatas = null;
	private String mFileSuffix;
	private int mFileLines = 0;
	private int mRetransCount = 0;

	private McuManager mMcuManager;
	private McuUpgradeInfo mMcuUpgradeInfo;

	private String[] specialLocaleNames;

	private int index_language;

	ArrayList<String> list_lang;

	private List<HashMap<String, Object>> sortedList;
	static tzutil.TimeZoneSet mTimeZoneSet;
	private int mTimezoneIndex;

	private McuListener mMcuListener = new McuListener() {

		@Override
		public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {
			if (mcuBaseInfo == null) {
				L.e(TAG, "onMcuInfoChanged - mcuBaseInfo is null");
				return;
			}

			if (infoType == MCU_UPGRADE_INFO_TYPE) {
				mMcuUpgradeInfo = mcuBaseInfo.getUpgradeInfo();
				int upgradeState = mMcuUpgradeInfo.getUpgradeState();
				L.e(TAG, "onMcuInfoChanged - upgradeState: " + upgradeState);
				if (mFileSuffix.equals(MCU_UPGRADE_S19_SUFFIX)) {
					onS19FileUpgrade(upgradeState);
				} else if (mFileSuffix.equals(MCU_UPGRADE_BIN_SUFFIX)) {
					onBinFileUpgrade(upgradeState);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.factory_setting_main);
		initData();
		setView();
		setListener();
		View view = findViewById(R.id.factory_return);
		if(view != null){
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onBackPressed();
				}
			});
		}
	}

	private void initData() {
		mContext = this;
		mFragmentManager = getFragmentManager();
		tzutil.initSharePF(this);

		mEnv = new EnvironmentATC(this);
		mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
		try {
			mMcuManager.RPC_RequestMcuInfoChangedListener(mMcuListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		//power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
		// if (power != null) {
		// pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		// }

		mProgressDialog = new ProgressDialog(FactorySettingActivity.this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(100);
		mProgressDialog.setTitle(R.string.system_mcu_upgrade);
		mProgressDialog.setMessage(getString(R.string.system_mcu_upgrade_prompt));

		mTimeZoneSet = tzutil.TimeZoneSet.getInstance(mContext);

		tzutil.MyComparator comparator = new tzutil.MyComparator(mTimeZoneSet.KEY_OFFSET);
		sortedList = mTimeZoneSet.getZones(mContext);
		Collections.sort(sortedList, comparator);

		mTimezoneIndex = mTimeZoneSet.getTimeZoneIndex(sortedList, XmlParse.default_timezone);

		specialLocaleNames = getResources().getStringArray(R.array.special_locale_names);
		list_lang = XmlParse.getStringArray(XmlParse.support_language);
		for (int i = 0; i < list_lang.size(); i++) {
			if (XmlParse.default_language.equalsIgnoreCase(list_lang.get(i))) {
				index_language = i;
				break;
			}
		}

	}

	private void setView() {
		// //////////////////////////////////////////////////////////////////////////////////////////
		((HeaderLayout) findViewById(R.id.factory_language_select))
				.setPromptTitle(specialLocaleNames[index_language]);
		setTimezonePromptTitle(mTimezoneIndex);

		int i = 0;
		String str[] = { mStr_password };
		for (HeaderLayout layout : mLayout_TextView) {
			mLayout_TextView[i] = (HeaderLayout) findViewById(ID_TextView[i]);
			mLayout_TextView[i].setRightTitle(str[i]);
			mLayout_TextView[i].setOneButtonListener(this);
			i++;
		}
		
		// //////////////////////////////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////////////////////////////
		//usb 主机与从机
		((HeaderLayout) findViewById(R.id.system_usb_model)).setRadioButtonTitle(
				R.string.system_usb_host, R.string.system_usb_device);
		((HeaderLayout) findViewById(R.id.system_usb_model)).setRadioCheck(SystemProperties.get(
				Tag.PERSYS_USB_MODE).equals("device") ? 2 : 1);
		
		//usb 1.0与usb2.0
		((HeaderLayout) findViewById(R.id.system_usb_type)).setRadioButtonTitle(
				R.string.system_usb_10, R.string.system_usb_20);
		((HeaderLayout) findViewById(R.id.system_usb_type)).setRadioCheck(SystemProperties.get(
				Tag.PERSYS_USB_TYPE).equals("usb2_0") ? 2 : 1);
	}

	private void setListener() {

		int i = 0;
		for (HeaderLayout headLayout : mLayout_OneButton) {
			headLayout = (HeaderLayout) findViewById(ID_OneButton[i]);
			headLayout.setOneButtonListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout headLayout : mLayout_OneCheck) {
			headLayout = (HeaderLayout) findViewById(ID_OneCheck[i]);
			headLayout.setOneCheckBoxListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout headLayout : mLayout_TwoRadio_UsbMode) {
			headLayout = (HeaderLayout) findViewById(ID_TwoRadio_UsbMode[i]);
			headLayout.setTwoRadioButtonListener(this);
			i++;
		}
		
		i = 0;
		for (HeaderLayout headLayout : mLayout_TwoRadio_UsbType) {
			headLayout = (HeaderLayout) findViewById(ID_TwoRadio_UsbType[i]);
			headLayout.setTwoRadioButtonListener(this);
			i++;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		mProgressDialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mMcuManager.RPC_RemoveMcuInfoChangedListener(mMcuListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_language_select:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager, specialLocaleNames,
					index_language);
			mDialog.setOnItemClickListener(this);

			break;

		case R.id.factory_timezone_set:
			Fragment_TimeZone dlg = Function.onSet_timezone(mFragmentManager, mTimezoneIndex);
			dlg.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onClickItem(String str) {
					// TODO Auto-generated method stub
					// Update the system timezone value
					XmlParse.default_timezone = str;
					mTimezoneIndex = mTimeZoneSet.getTimeZoneIndex(sortedList, str);
					setTimezonePromptTitle(mTimezoneIndex);
				}
			});
			break;

		case R.id.factory_radio_set:
			Function.onSet_factory_radio(mFragmentManager);
			break;

		case R.id.factory_navigation_set:
			Function.onSet_factory_navi(mFragmentManager);
			break;
		case R.id.factory_bt_set:
			Function.onSet_bt(mFragmentManager, true);
			break;

		case R.id.factory_backcar_set:
			Function.onSet_backcar(mFragmentManager);
			break;

		case R.id.factory_gain_adjust:
			Function.onSet_factory_gain(mFragmentManager);
			break;

		case R.id.factory_sound_effect:
			Function.onSet_SoundEffect(mFragmentManager);
			break;

		case R.id.factory_volume_set:
			Function.onSet_VolumeSet(mFragmentManager);
			break;

		case R.id.factory_video_set:
			Function.onSet_VideoSet(mFragmentManager);
			break;

		case R.id.factory_yuv_set:
			Function.onSet_YUVSet(mContext, mFragmentManager);
			break;

		case R.id.factory_other_set:
			Function.onSet_factory_other(mFragmentManager);
			break;

		case R.id.factory_reset_def:
			Fragment_Prompt_dialog dialog = Function.onSet_Prompt_dialog(mContext,
					mFragmentManager, getResources().getString(R.string.factory_reset_def));
			dialog.setOnConfirmListener(new OnConfirmListener() {
				@Override
				public void onConfirm() {
					// TODO Auto-generated method stub
					XmlRW.readXMLFile(Tag.XML_PATH_DEF);
					setView();
					mHandler.sendEmptyMessage(Tag.HANDLER_WRITE_XMLFILE);
					Function.RebootSystem(mContext, mFragmentManager);
				}
			});
			break;

		case R.id.factory_xml_export:
			onExportFile();
			break;

		case R.id.factory_save_reboot:
			mHandler.sendEmptyMessage(Tag.HANDLER_WRITE_XMLFILE);
			Function.RebootSystem(mContext, mFragmentManager);
			break;

		case R.id.factory_system_updata_usb:
			system_updata_usb();
			break;

		case R.id.system_volume_adjust:
			Function.onSet_VolumeAdjust(mContext);
			break;

		case R.id.system_factory_testing:
			Function.onSet_onFactoryTest(mContext);
			break;

		case R.id.system_mcu_upgrade:
			onMcuUpgrade();
			break;

		case R.id.system_factory_fun_config:
			Function.onSet_FunConfig(mFragmentManager);
			break;
		case R.id.system_factory_lcd_config:
			Function.onSet_LcdConfig(mFragmentManager);
			break;
		case R.id.factory_mapdata_upgrade:
			Function.onSet_MapDataUpgrade(mContext);
			break;

		case R.id.factory_tocuh_key_learn:
			Function.onSet_TouchKeyLearn(mContext);
			break;
		case R.id.factory_tocuh_calibration:
			Function.onSet_TouchCalibration(mContext);
			break;
		case R.id.factory_reboot:
			Function.RebootSystem(mContext, mFragmentManager);
			break;
		case R.id.factory_password:
			Fragment_Keyboard_num pass_dialog = Function.onSet_keyboard_num(mFragmentManager);
			pass_dialog.setOnKeyboardListener(this);
			break;
		default:
			break;
		}
	}

	private void onExportFile() {
		boolean hasDevice = false;
		StringBuilder xmlfileDir = new StringBuilder();

		if (!mEnv.getStorageState(EXT_SDCARD1_PATH).equals(Environment.MEDIA_MOUNTED)) {
			hasDevice = false;
		} else {
			hasDevice = true;
			xmlfileDir.append(EXT_SDCARD1_PATH);
		}

		if (!hasDevice) {
			if (!mEnv.getStorageState(EXT_SDCARD2_PATH).equals(Environment.MEDIA_MOUNTED)) {
				hasDevice = false;
			} else {
				hasDevice = true;
				xmlfileDir.append(EXT_SDCARD2_PATH);
			}
		}

		if (!hasDevice) {
			if (!mEnv.getStorageState(UDISK1_PATH).equals(Environment.MEDIA_MOUNTED)) {
				hasDevice = false;
			} else {
				hasDevice = true;
				xmlfileDir.append(UDISK1_PATH);
			}
		}

		if (!hasDevice) {
			if (!mEnv.getStorageState(UDISK2_PATH).equals(Environment.MEDIA_MOUNTED)) {
				hasDevice = false;
			} else {
				hasDevice = true;
				xmlfileDir.append(UDISK2_PATH);
			}
		}

		L.e(TAG, "onWriteExportFile - export file dir: " + xmlfileDir.toString());

		if (!hasDevice) {
			Toast.makeText(this, R.string.factory_storage_device_no_exist, Toast.LENGTH_SHORT)
					.show();
		} else {
			tzutil.copyFolder("/mnt/usr2", xmlfileDir.toString());
			xmlfileDir.append("/");
			xmlfileDir.append(XML_EXPORT_FILE);
			XmlRW.writeXMLFile(xmlfileDir.toString());
			Toast.makeText(this,
					getString(R.string.factory_xml_export_path) + xmlfileDir.toString(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void system_updata_usb() {
		boolean hasFile = false;
		StringBuilder usbUpgradeDir = new StringBuilder();
		File file = null;

		if (!hasFile) {
			if (!mEnv.getStorageState(UDISK1_PATH).equals(Environment.MEDIA_MOUNTED)) {
				hasFile = false;
			} else {
				hasFile = true;

				usbUpgradeDir.append(UDISK1_PATH);
				usbUpgradeDir.append("/");
				usbUpgradeDir.append(USB_UPGRADE_FILE);
			}
		}

		if (!hasFile) {
			if (!mEnv.getStorageState(UDISK2_PATH).equals(Environment.MEDIA_MOUNTED)) {
				hasFile = false;
			} else {
				hasFile = true;

				usbUpgradeDir.append(UDISK2_PATH);
				usbUpgradeDir.append("/");
				usbUpgradeDir.append(USB_UPGRADE_FILE);
			}
		}

		L.e(TAG, "onUsbUpgrade - upgrade dir: " + usbUpgradeDir.toString());

		file = new File(usbUpgradeDir.toString());
		if (!file.exists()) {
			hasFile = false;
		}

		if (!hasFile) {
			Toast.makeText(this, R.string.system_usb_upgrade_error, Toast.LENGTH_SHORT).show();
		} else {
			try {
				mMcuManager.RPC_UsbUpgrade(1);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		default:
			break;
		}
	}

	@Override
	public void onLeftRadioButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.system_usb_model:
			tzutil.setSynchronization(false);
			break;
		case R.id.system_usb_type:
			tzutil.setUsbType(tzutil.USB_10);
			break;
		default:
			break;
		}
	}

	@Override
	public void onRightRadioButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.system_usb_model:
			tzutil.setSynchronization(true);
			break;
		case R.id.system_usb_type:
			tzutil.setUsbType(tzutil.USB_20);
			break;
		default:
			break;
		}
	}

	@Override
	public void onProgessDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {

		default:
			break;
		}
	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.factory_language_select:
			((HeaderLayout) findViewById(R.id.factory_language_select))
					.setPromptTitle(specialLocaleNames[value]);
			XmlParse.default_language = list_lang.get(value);
			index_language = value;
			break;

		}
	}

	public static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (Tag.HANDLER_RESET_FACTORY == msg.what) {
				tzutil.ResetFactory(mContext);
			} else if (Tag.HANDLER_WRITE_XMLFILE == msg.what) {
				XmlRW.writeXMLFile(Tag.XML_PATH_USER);
				XmlRW.setSystemProperties();
			}
		}
	};

	private void buildProgressDialog(int id, String title, int progress, int pos) {
		Fragment_Progress Dialog = new Fragment_Progress();
		Dialog.initData(id, title, progress, pos);
		Dialog.setOnProgressDlgListener(this);
		Dialog.show(mFragmentManager, title);
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

		L.e(TAG, "onMcuUpgrade - upgrade dir: " + mcuUpgradeDir.toString());

		file = new File(mcuUpgradeDir.toString());
		if (!file.exists()) {
			hasFile = false;
		}

		if (hasFile) {
			mMcuUpgradeFiles = loadingUpgradeFiles(mcuUpgradeDir.toString());
			L.e(TAG, "onMcuUpgrade - upgrade file size: " + mMcuUpgradeFiles.size());
		}

		if (mMcuUpgradeFiles == null || mMcuUpgradeFiles.size() <= 0) {
			hasFile = false;
		} else {
			hasFile = true;
		}

		L.e(TAG, "onMcuUpgrade - hasFile: " + hasFile);

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
					upgradFilesArray, false);
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
							L.e(TAG, "onMcuUpgrade - read filename: " + filename);
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

			// new AlertDialog.Builder(this)
			// .setTitle(R.string.system_mcu_upgrade_file_list)
			// .setItems(upgradFilesArray,
			// new android.content.DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, final int which) {
			// // TODO Auto-generated method stub
			// }
			// }).show();
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
				// if (lines == 1) {
				// Log.e(TAG, lines + ": " + line);
				// byte[] valueLine = line.getBytes();
				// StringBuffer sb = new StringBuffer();
				// for (int i = 0; i < valueLine.length; i++) {
				// String str = String.format("0x%02X ", valueLine[i]);
				// sb.append(str);
				// }
				// Log.e(TAG, "len: " + valueLine.length + " - " +
				// sb.toString());
				// }

				mASCIIFileDatas.add(line);
			}
			L.e(TAG, "file lines: " + lines);
			// for (int i = 0; i < mFileDatas.size(); i++) {
			// Log.e(TAG, (i + 1) + ": " + mFileDatas.get(i));
			// byte[] valueLine = mFileDatas.get(i).getBytes();
			// StringBuffer sb = new StringBuffer();
			// for (int j = 0; j < valueLine.length; j++) {
			// String str = String.format("0x%02X ", valueLine[j]);
			// sb.append(str);
			// }
			// Log.e(TAG, "len: " + valueLine.length + " - " +
			// sb.toString());
			// }
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
			L.e(TAG, "readBinUpgradeFileData - len: " + len);
			int remainder = len % MCU_UPGRADE_TRANS_DATA_SIZE;
			if (remainder != 0) {
				len += MCU_UPGRADE_TRANS_DATA_SIZE - remainder;
			}
			mBinFileDatas = new byte[len];
			fis.read(mBinFileDatas);
			L.e(TAG, "readBinUpgradeFileData - need len: " + mBinFileDatas.length);

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
		L.e(TAG, "onMcuInfoChanged - mFileLines: " + mFileLines);

		int size = mASCIIFileDatas.size();
		if (mFileLines == (size - 1) || (mRetransCount == 3 && upgradeState == MCU_UPGRADE_RETRANS)) {
			if (mFileLines == (size - 1)) {
				mProgressDialog.setProgress(100);
				L.e(TAG, "onMcuInfoChanged - mcu upgrade end");
			} else {
				L.e(TAG, "onMcuInfoChanged - mRetransCount: " + mRetransCount);
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
		L.e(TAG, "onMcuInfoChanged - mFileLines: " + mFileLines);

		int num = mBinFileDatas.length / MCU_UPGRADE_TRANS_DATA_SIZE;

		if (mFileLines == num || (mRetransCount == 3 && upgradeState == MCU_UPGRADE_RETRANS)) {
			if (mFileLines == num) {
				mProgressDialog.setProgress(100);
				L.e(TAG, "onMcuInfoChanged - mcu upgrade end");
			} else {
				L.e(TAG, "onMcuInfoChanged - mRetransCount: " + mRetransCount);
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

	private void setTimezonePromptTitle(int index) {
		final Map<?, ?> map = (Map<?, ?>) sortedList.get(index);
		final String displayname = (String) map.get(mTimeZoneSet.KEY_DISPLAYNAME);
		final String gmt = (String) map.get(mTimeZoneSet.KEY_GMT);

		((HeaderLayout) findViewById(R.id.factory_timezone_set)).setPromptTitle(displayname);
		((HeaderLayout) findViewById(R.id.factory_timezone_set)).setPromptTitle2(gmt);
	}

	@Override
	public void back(int id, String str) {
		// TODO Auto-generated method stub
		mLayout_TextView[0].setRightTitle(str);
		XmlParse.factory_password = str;
	}

}
