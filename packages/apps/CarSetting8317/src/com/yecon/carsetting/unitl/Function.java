package com.yecon.carsetting.unitl;

import static android.constant.YeconConstants.CANBUS_PACKAGE_NAME;
import static android.constant.YeconConstants.CANBUS_START_ACTIVITY;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;

import com.yecon.carsetting.DatetimeSetActivity;
import com.yecon.carsetting.FactorySettingActivity;
import com.yecon.carsetting.KeyLearnMain;
import com.yecon.carsetting.NavigationsetActivity;
import com.yecon.carsetting.R;
import com.yecon.carsetting.TouchCalibrationMainActivity;
import com.yecon.carsetting.VideosetActivity;
import com.yecon.carsetting.fragment.Fragment_About;
import com.yecon.carsetting.fragment.Fragment_BT;
import com.yecon.carsetting.fragment.Fragment_General;
import com.yecon.carsetting.fragment.Fragment_Keyboard_all;
import com.yecon.carsetting.fragment.Fragment_Keyboard_num;
import com.yecon.carsetting.fragment.Fragment_Keyboard_num.OnKeyboardListener;
import com.yecon.carsetting.fragment.Fragment_List;
import com.yecon.carsetting.fragment.Fragment_MapAppSelect;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog.OnConfirmListener;
import com.yecon.carsetting.fragment.Fragment_TimeZone;
import com.yecon.carsetting.fragment.factory_Fragment_Assistive_Touch;
import com.yecon.carsetting.fragment.factory_Fragment_BackCar;
import com.yecon.carsetting.fragment.factory_Fragment_BootLogo;
import com.yecon.carsetting.fragment.factory_Fragment_DvdLogo;
import com.yecon.carsetting.fragment.factory_Fragment_FunsConfig;
import com.yecon.carsetting.fragment.factory_Fragment_FunsConfig_Other;
import com.yecon.carsetting.fragment.factory_Fragment_Gain;
import com.yecon.carsetting.fragment.factory_Fragment_LcdConfig;
import com.yecon.carsetting.fragment.factory_Fragment_Navi;
import com.yecon.carsetting.fragment.factory_Fragment_Other;
import com.yecon.carsetting.fragment.factory_Fragment_QiCaiDeng;
import com.yecon.carsetting.fragment.factory_Fragment_Radio;
import com.yecon.carsetting.fragment.factory_Fragment_RadioArea_Config;
import com.yecon.carsetting.fragment.factory_Fragment_Sleep;
import com.yecon.carsetting.fragment.factory_Fragment_Video;
import com.yecon.carsetting.fragment.factory_fragment_RGBSet;
import com.yecon.carsetting.fragment.factory_fragment_SoundEffect;
import com.yecon.carsetting.fragment.factory_fragment_VolumeSet;
import com.yecon.carsetting.fragment.factory_fragment_YUVSet;
import com.yecon.carsetting.wifi.Fragment_Wifi_search;
import com.yecon.metazone.YeconMetazone;
import android.os.SystemProperties;
public class Function {
	static String[] mCpuTypeArray = {"AC8217KBFI", "8227HBFI", "AC8317KNFI", "AC8327HNFI", "AC8327MXFI"};
	
	public static boolean RebootSystem(final Context context, FragmentManager fm) {

		final McuManager mMcuManager = (McuManager) context.getSystemService(Context.MCU_SERVICE);
		//Fragment_Prompt_dialog dialog = Function.onSet_Prompt_dialog(context, fm, context
		//		.getResources().getString(R.string.system_shutdown_prompt));
		//dialog.setOnConfirmListener(new OnConfirmListener() {
		//	@Override
		//	public void onConfirm() {
				
				IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager
						.getService("power"));
				try {
					setDefaultVolume(context); //FIXME:由上电状态为1初始化设置
					//mMcuManager.RPC_KeyCommand(T_SETTING_RESET,new byte[0]);
					McuMethodManager.getInstance(context).sendSysRestartKeyCMD();
					power.reboot(false, "", false);
					McuMethodManager.getInstance(context).sendSysRestartKeyCMD();
				} catch (RemoteException e) {
					L.e(L.TAG, "RemoteException when RebootSystem: ");
					//return;
				}
		//	}
		//});
		return true;
	}

	public static void RebootSystem(Context context) {
		Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
		intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
		// if set true,the dialog will show on
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public static void setDefaultVolume(Context context){
		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE) ;
		mAudioManager.setStreamVolume(AudioManager.STREAM_GIS, Context.DEFUALT_VOLUME,0); 
		mAudioManager.setStreamVolume(AudioManager.STREAM_RING, Context.DEFUALT_VOLUME,0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, Context.DEFUALT_VOLUME,0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_RDS, Context.DEFUALT_VOLUME,0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Context.DEFUALT_VOLUME,0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, Context.DEFUALT_VOLUME,0);
	}
	
	public static boolean getBuzzerEnable(Context context) {
		return Settings.System.getInt(context.getContentResolver(),
				Settings.System.SOUND_EFFECTS_ENABLED, 1) != 0;
	}

	public static void setBuzzerEnable(Context context, boolean enable) {
		Settings.System.putInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED,
				enable ? 1 : 0);
		if (mBuzzerListener != null)
			mBuzzerListener.onListener(enable);
	}

	public interface onBuzzerListener {
		public void onListener(boolean isOpen);
	}

	public static void setBuzzerListener(onBuzzerListener mListener) {
		mBuzzerListener = mListener;
	}

	public static onBuzzerListener mBuzzerListener;

	// ///////////////////////////////////////////////

	public static void onSet_resetfactory(final Context context, final FragmentManager fm,
			String str) {
		Fragment_Prompt_dialog fragment = new Fragment_Prompt_dialog(str);
		fragment.setOnConfirmListener(new OnConfirmListener() {

			@Override
			public void onConfirm() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Tag.ACTION_RESET_FACTORY);
				context.sendBroadcast(intent);
				// resetRadio();
				XmlRW.readXMLFile(Tag.XML_PATH_DEF);
				XmlRW.writeXMLFile(Tag.XML_PATH_USER);
				XmlRW.setSystemProperties();

				try {
					Function.onSet_DefWallpaper(context);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				RebootSystem(context, fm);
			}
		});
		fragment.show(fm, "set_reset");
	}

	public static void onSet_general(FragmentManager fm) {
		Fragment_General fragment = new Fragment_General();
		fragment.show(fm, "set_general");
	}

	public static void onSet_datetime(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, DatetimeSetActivity.class);
		context.startActivity(intent);
	}

	public static void onSet_navigation(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, NavigationsetActivity.class);
		context.startActivity(intent);
	}

	public static void onSet_video(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, VideosetActivity.class);
		context.startActivity(intent);
	}

	public static void onSet_bt(FragmentManager fm, boolean bFactory) {
		Fragment_BT fragment = new Fragment_BT(bFactory);
		fragment.show(fm, "set_bt");
	}

	public static void onSet_wifi(FragmentManager fm) {
		Fragment_Wifi_search fragment = new Fragment_Wifi_search();
		fragment.show(fm, "set_wifi");
	}

	public static void onSet_backcar(FragmentManager fm) {
		factory_Fragment_BackCar fragment = new factory_Fragment_BackCar();
		fragment.show(fm, "set_backcar");
	}

	public static Fragment_Keyboard_num onSet_keyboard_num(FragmentManager fm) {
		Fragment_Keyboard_num fragment = new Fragment_Keyboard_num();
		fragment.show(fm, "set_keyboard");
		return fragment;
	}

	public static Fragment_Keyboard_all onSet_keyboard_all(FragmentManager fm) {
		Fragment_Keyboard_all fragment = new Fragment_Keyboard_all();
		fragment.show(fm, "set_keyboard_all");
		return fragment;
	}

	public static String getCpuType() {
		int mCpuType = YeconMetazone.GetCpuType();
		Log.d("xuhh", " YeconMetazone.GetCpuType()="+mCpuType);
		
		if ( (mCpuType >= 0xE1) 
				&& (mCpuType <= (0xE1+mCpuTypeArray.length)) ) {
			return mCpuTypeArray[mCpuType-0xE1];
		}else {
			Log.d("xuhh", "YeconMetazone.GetCpuType invalid");
			return Build.MODEL;
		}
	}
	
	public static void onSet_factory(final Context context, FragmentManager fm) {
		Fragment_Keyboard_num dialog = onSet_keyboard_num(fm);
		dialog.setOnKeyboardListener(new OnKeyboardListener() {

			@Override
			public void back(int id, String str) {
				// TODO Auto-generated method stub
				String password = XmlParse.factory_password;
				if (password == null) {
					password = "8317";
				}
				
				if (str.equalsIgnoreCase(password)) {
					Intent mIntent = new Intent(context, FactorySettingActivity.class);
					context.startActivity(mIntent);
				}
			}
		});
	}

	public static void onSet_about(FragmentManager fm) {
		Fragment_About fragment = new Fragment_About();
		fragment.show(fm, "set_about");
	}

	public static Fragment_TimeZone onSet_timezone(FragmentManager fm, int mTimezoneIndex) {
		Fragment_TimeZone fragment = new Fragment_TimeZone(mTimezoneIndex);
		fragment.show(fm, "set_timezone");
		return fragment;
	}

	public static void onSet_MapList(Context context, FragmentManager fm) {
		Fragment_MapAppSelect dialog = new Fragment_MapAppSelect(context);
		dialog.show(fm, "nar_mapList");
	}

	public static Fragment_List buildListDialog(int id, FragmentManager fm, String[] stringArray) {

		Fragment_List fragment = new Fragment_List();
		fragment.setData(id, stringArray);
		// Dialog.setOnItemClickListener(this);
		fragment.show(fm, id + "");
		return fragment;
	}

	public static Fragment_List buildListDialog(int id, FragmentManager fm, String[] stringArray,
			int select) {
		Fragment_List fragment = new Fragment_List();
		fragment.setData(id, stringArray, select);
		fragment.show(fm, id + "");
		return fragment;
	}
	
	public static Fragment_List buildListDialog(int id, FragmentManager fm, String[] stringArray,
			int select,int main_layout,int item_layout) {
		Fragment_List fragment = new Fragment_List(main_layout,item_layout);
		fragment.setData(id, stringArray, select);
		fragment.show(fm, id + "");
		return fragment;
	}

	public static Fragment_List buildListDialog(int id, FragmentManager fm, String[] stringArray,
			boolean bExitFlag) {
		Fragment_List fragment = new Fragment_List();
		fragment.setData(id, stringArray, bExitFlag);
		fragment.show(fm, id + "");
		return fragment;
	}
	public static Fragment_List buildListDialog(int id, FragmentManager fm, String[] stringArray,
			boolean bExitFlag,boolean isfullScreen) {
		Fragment_List fragment = new Fragment_List(isfullScreen);
		fragment.setData(id, stringArray, bExitFlag);
		fragment.show(fm, id + "");
		return fragment;
	}

	public static void setRadioCountry(Context context, int radio_index) {
		Intent mIntent = new Intent(Tag.ACTION_RADIO_UPDATE);
		mIntent.putExtra("radio_index", radio_index);
		context.sendBroadcast(mIntent);
	}

	public static void onStartActivity(Context context, String packageName, String className) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(new ComponentName(packageName, className));
		context.startActivity(intent);
	}

	public static void onSet_Wallpaper(Context context) {
		String packageName = "com.android.launcher";
		String className = "com.android.launcher2.WallpaperChooser";
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setComponent(new ComponentName(packageName, className));
		context.startActivity(intent);
	}

	public static void onSet_LearnSteerWheel(Context context) {
		String packageName = "com.yecon.swc";
		String className = "com.yecon.swc.SWCMainActivity";
		onStartActivity(context, packageName, className);
	}

	public static void onSet_SoundEffect(Context context) {
		String packageName = "com.yecon.sound.setting";
		String className = "com.yecon.sound.setting.AudioSetting";
		onStartActivity(context, packageName, className);
	}
	public static void onSet_SoundEffect(Context context,String function) {
		if(function != null){
			function = function.toLowerCase();
		}
		if("balance".equals(function)){
			String packageName = "com.yecon.sound.setting";
			String className = "com.yecon.sound.setting.tuoxian.BalanceSetting";
			onStartActivity(context, packageName, className);
		}
		else if("effect".equals(function))
		{
			String packageName = "com.yecon.sound.setting";
			String className = "com.yecon.sound.setting.tuoxian.EffectSetting";
			onStartActivity(context, packageName, className);
		}
		else{
			onSet_SoundEffect(context);
		}
	}
	

	public static void onSet_GpsTest(Context context) {
		String packageName = "com.yecon.gpstest";
		String className = "com.yecon.gpstest.GPSTestActivity";
		onStartActivity(context, packageName, className);
	}

	public static void onSet_TouchKeyLearn(Context context) {
		String packageName = context.getPackageName();
		String className = KeyLearnMain.class.getName();
		onStartActivity(context, packageName, className);
	}

	public static void onSet_TouchCalibration(Context context) {
		String packageName = context.getPackageName();
		String className = TouchCalibrationMainActivity.class.getName();
		onStartActivity(context, packageName, className);
	}

	public static void onSet_MapDataUpgrade(Context context) {
		String packageName = "com.android.yecon.copyInstall";
		String className = "com.android.yecon.copyInstall.CopyInstallActivity";
		onStartActivity(context, packageName, className);
	}

	public static void onSet_VolumeAdjust(final Context context) {
		String packageName = "com.yecon.volumeadjust";
		String className = "com.yecon.volumeadjust.VolumeAdjustMainActivity";
		onStartActivity(context, packageName, className);
	}

	public static void onSet_Bootlogo(FragmentManager fm) {
		factory_Fragment_BootLogo fragment = new factory_Fragment_BootLogo();
		fragment.show(fm, "set_bootlogo");
	}

	public static void onSet_Dvdlogo(FragmentManager fm) {
		factory_Fragment_DvdLogo fragment = new factory_Fragment_DvdLogo();
		fragment.show(fm, "set_dvdlogo");
	}

	public static void onSet_onFactoryTest(final Context context) {
		String packageName = "com.yecon.yeconfactorytesting";
		String className = "com.yecon.yeconfactorytesting.FactoryTestingMainActivity";
		onStartActivity(context, packageName, className);
	}

	public static void onSet_FunConfig(FragmentManager fm) {

		factory_Fragment_FunsConfig dialog = new factory_Fragment_FunsConfig();
		dialog.show(fm, "system_factory_fun_config");
	}
	
	/**
	 * lcd配置
	 * @param fm
	 */
	public static void onSet_LcdConfig(FragmentManager fm) {
		factory_Fragment_LcdConfig dialog = new factory_Fragment_LcdConfig();
		dialog.show(fm, "system_factory_lcd_config");
	}

	public static void onSet_AssistiveTouch(FragmentManager fm) {

		factory_Fragment_Assistive_Touch dialog = new factory_Fragment_Assistive_Touch();
		dialog.show(fm, "system_factory_assistive_touch");
	}

	public static void onSet_Qucaideng(FragmentManager fm) {
		factory_Fragment_QiCaiDeng fragment = new factory_Fragment_QiCaiDeng();
		fragment.show(fm, "factory_Fragment_QiCaiDeng");
	}

	public static void onSet_SoundEffect(FragmentManager fm) {
		factory_fragment_SoundEffect fragment = new factory_fragment_SoundEffect();
		fragment.show(fm, "factory_sound_set");
	}
	
	public static void onSet_VideoSet(FragmentManager fm) {
		factory_Fragment_Video fragment = new factory_Fragment_Video();
		fragment.show(fm, "factory_video_set");
	}

	public static void onSet_RGBSet(FragmentManager fm) {
		factory_fragment_RGBSet fragment = new factory_fragment_RGBSet();
		fragment.show(fm, "factory_rgb_set");
	}

	public static void onSet_YUVSet(final Context context, FragmentManager fm) {
		factory_fragment_YUVSet fragment = new factory_fragment_YUVSet(context);
		fragment.show(fm, "factory_yuv_set");
	}

	public static void onSet_VolumeSet(FragmentManager fm) {
		factory_fragment_VolumeSet fragment = new factory_fragment_VolumeSet();
		fragment.show(fm, "factory_volume_set");
	}

	public static void onSet_DefWallpaper(final Context context) throws RemoteException {
		// try {
		// final WallpaperManager wpm = (WallpaperManager) context
		// .getSystemService(Context.WALLPAPER_SERVICE);
		// wpm.setResource(R.drawable.default_wallpaper);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		WallpaperManager mWallpaperManager = WallpaperManager.getInstance(context);
		Intent intent = new Intent();
		String packageName = "com.android.noisefield";
		String className = "com.android.noisefield.NoiseFieldWallpaper";
		intent.setComponent(new ComponentName(packageName, className));
		mWallpaperManager.getIWallpaperManager().setWallpaperComponent(intent.getComponent());
		mWallpaperManager.setWallpaperOffsetSteps(0.5f, 0.0f);
		// mWallpaperManager.setWallpaperOffsets(v.getRootView().getWindowToken(),
		// 0.5f, 0.0f);
	}

	public static Fragment_Prompt_dialog onSet_Prompt_dialog(final Context context,
			FragmentManager fm, String str) {
		Fragment_Prompt_dialog fragment = new Fragment_Prompt_dialog(str);
		fragment.show(fm, "set_reset");
		return fragment;
	}
	public static Fragment_Prompt_dialog onSet_Prompt_dialog(final Context context,
			FragmentManager fm, String str,Drawable drawable) {
		Fragment_Prompt_dialog fragment = new Fragment_Prompt_dialog(str,drawable);
		fragment.show(fm, "set_reset");
		return fragment;
	}

	public static void onSet_factory_radio(FragmentManager fm) {
		factory_Fragment_Radio fragment = new factory_Fragment_Radio();
		fragment.show(fm, "set_radio");
	}

	public static void onSet_factory_gain(FragmentManager fm) {
		factory_Fragment_Gain fragment = new factory_Fragment_Gain();
		fragment.show(fm, "set_gain");
	}

	public static void onSet_factory_navi(FragmentManager fm) {
		factory_Fragment_Navi fragment = new factory_Fragment_Navi();
		fragment.show(fm, "set_navi");
	}

	public static void onSet_factory_other(FragmentManager fm) {
		factory_Fragment_Other fragment = new factory_Fragment_Other();
		fragment.show(fm, "set_other");
	}

	public static void onSet_factory_Fragment_FunsConfig_Other(FragmentManager fm) {
		factory_Fragment_FunsConfig_Other fragment = new factory_Fragment_FunsConfig_Other();
		fragment.show(fm, "set_FunsConfig_Other");
	}

	public static void onSet_factory_Fragment_RadioArea_Config(FragmentManager fm) {
		factory_Fragment_RadioArea_Config fragment = new factory_Fragment_RadioArea_Config();
		fragment.show(fm, "set_RadioArea_Config");
	}
	
	public static void onSet_factory_Fragment_Sleep(FragmentManager fm) {
		factory_Fragment_Sleep fragment = new factory_Fragment_Sleep();
		fragment.show(fm, "factory_Fragment_Sleep");
	}

	public static void onSet_car_type(final Context context) {
		String packageName = CANBUS_PACKAGE_NAME;
		String className = "com.can.ui.CanChoose";
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("showType", "Models to choose");
		intent.setComponent(new ComponentName(packageName, className));
		context.startActivity(intent);
	}
	public static void onSet_RearMute(Context context){
		McuMethodManager.getInstance(context).send_RearMute();
	}
}
