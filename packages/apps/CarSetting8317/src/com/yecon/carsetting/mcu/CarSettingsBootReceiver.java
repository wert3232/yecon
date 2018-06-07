package com.yecon.carsetting.mcu;

import static android.mcu.McuExternalConstant.*;
import static android.constant.YeconConstants.*;

import java.io.File;

import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.util.Log;

import com.autochips.settings.AtcSettings;
import com.yecon.carsetting.ApplicationManage;
import com.yecon.carsetting.TouchCalibrationMainActivity;
import com.yecon.carsetting.unitl.ArraySetting;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.unitl.XmlRW;
import com.yecon.carsetting.unitl.mtksetting;
import com.yecon.common.RebootStatus;
import com.yecon.common.RebootStatus.SOURCE;
import com.yecon.metazone.YeconMetazone;
import com.yecon.settings.YeconSettings;

/**
 * if the apk is been removed,the database will remove ,and the prop will set to
 * nothing if update to the apk, do nothing;
 */

public class CarSettingsBootReceiver extends BroadcastReceiver {
	private final String TAG="CarSettingsBootReceiver";
	private static final int[] STREAM_TYPE = new int[] { AudioManager.STREAM_MUSIC,
			AudioManager.STREAM_MUSIC, AudioManager.STREAM_GIS, AudioManager.STREAM_BACKCAR,
			AudioManager.STREAM_BLUETOOTH_SCO, };
	
	@Override
	public void onReceive(Context mContext, Intent intent) {
		String action = intent.getAction();
		L.e("CarSettingsBootReceiver - action: " + action);
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			initData(mContext);
			startMcuService(mContext);
		} else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
			final String packageName = intent.getData().getSchemeSpecificPart();
			ApplicationManage.getInstance().getMapListDB().delMap_Package(packageName);
			if (SystemProperties.get("persist.sys.maps").indexOf(packageName) != -1) {
				L.v("this is remove the " + packageName);
				SystemProperties.set("persist.sys.maps", "nothing");
			}
		} else if (Tag.ACTION_SETTING_EXIT.equals(action)) {
			ApplicationManage.getInstance().exit();
		} else if (Tag.ACTION_QB_POWERON.equals(action) || MCU_ACTION_ACC_ON.equals(action)) {
			CloseMute(mContext);			
			adjustPoweronVolume(mContext);
		} else if (MCU_ACTION_ACC_OFF.equals(action)) {
		    int mcuBacklight = SystemProperties.getInt(Tag.PERSYS_BKLIGHT[0], ArraySetting.BRIGHTNESS_HIGH);
		    YeconMetazone.SetBacklightness(mcuBacklight * 100 / 255);
		    
		    writeMzoneUIPara();
			writeMzoneBackcarPara();	
			writeMzoneBackcarMirror();
		}
		// else
		// if(intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)){
		// final String packageName = intent.getData().getSchemeSpecificPart();
		//
		// }
	}

	//当前音量大于等于默认时回到默认，小于默认时记忆当前值
	private void adjustPoweronVolume(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager != null) {
			int curVolume= audioManager.getStreamVolume(STREAM_TYPE[0]);
			Log.d(TAG, "adjust power on volume, cur volume="+curVolume+",default volume="+XmlParse.volume[0]);
			if (curVolume >= XmlParse.volume[0]) {
			    YeconSettings.setDefaultVolume(context);
				//curVolume = XmlParse.volume[0];
				//audioManager.setStreamVolume(STREAM_TYPE[0], curVolume, 0);
			}
		}
	}
	
	private void initData(Context context) {
		readXMLFile();
		initMetezone();
		// SystemProperties.set(Tag.PERSYS_BKLIGHT, XmlParse.backlight + "");
		
		if (RebootStatus.isReboot(SOURCE.SOURCE_OTHER)) {
			int volume = SystemProperties.getInt("persist.sys.current_volume", Context.DEFUALT_VOLUME);
			/*if(SystemProperties.getInt("persist.sys.hzh_factroy_reset",0) == 1){
				volume = Context.DEFUALT_VOLUME;
				SystemProperties.set("persist.sys.hzh_factroy_reset","" + 0);
				SystemProperties.set("persist.sys.current_volume","" + Context.DEFUALT_VOLUME);
			}*/
			if(volume <= Context.DEFUALT_VOLUME){
				mtksetting mmtksetting = mtksetting.getInstance();
				mmtksetting.SetRearVolume(volume);
			}else{	
				YeconSettings.setDefaultVolume(context);
				mtksetting mmtksetting = mtksetting.getInstance();
				mmtksetting.SetRearVolume(mtksetting.getVolume(XmlParse.volume[1]));
			}
		} else {
		    boolean wifiOnOff = SystemProperties.getBoolean(PROPERTY_WIFI_ONOFF, false);
		    if (wifiOnOff) {
		        //L.e("wifi enable");
		        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		        wifiManager.setWifiEnabled(wifiOnOff);
		    }
		}

		/*
		 * If you want to set u4SphDelay=1100, u4SphMicGain=40, you can set
		 * configs[0]=1100, configs[1]=40
		 */
		int configs[] = new int[2];
		AtcSettings.Audio.GetPCMConfig(configs);
		configs[1] = XmlParse.mic_volume;
		AtcSettings.Audio.SetPCMConfig(configs);
		// record denoising
		AtcSettings.Audio.SetCaptureNdcEn(false);
		
		adjustPoweronVolume(context);
	}

	/**
	 * 写ui参数到mzone, 先对比，有不同才写
	 */
	void writeMzoneUIPara() {
		Integer value[] = new Integer[4];
		
		for (int i = 0; i < 4; i++) {
			value[i] = SystemProperties
					.getInt(Tag.PERSYS_RGB_VIDEO[0][i], XmlParse.rgb_video[0][i]);
		}
		int mzone_ui_brightness = YeconMetazone.GetUIBrightness();
		int mzone_ui_constrast = YeconMetazone.GetUIConstrast();
		int mzone_ui_hue = YeconMetazone.GetUIHue();
		int mzone_ui_saturation = YeconMetazone.GetUISaturation();
		if (value[0] != mzone_ui_brightness
			|| value[1] != mzone_ui_constrast
			|| value[2] != mzone_ui_hue
			|| value[3] != mzone_ui_saturation) {
			YeconMetazone.MetaSetUIVideoPara(value[0], value[1], value[2], value[3]);
		}
	}
	
	/**
	 * 写倒车参数到mzone, 先对比，有不同才写
	 */
	void writeMzoneBackcarPara() {
		Integer value[] = new Integer[4];
		
		for (int i = 0; i < 4; i++) {
			value[i] = SystemProperties
					.getInt(Tag.PERSYS_RGB_VIDEO[6][i], XmlParse.rgb_video[6][i]);
		}
		int mzone_backcar_brightness = YeconMetazone.GetBackCarBrightness();
		int mzone_backcar_constrast = YeconMetazone.GetBackCarConstrast();
		int mzone_backcar_hue = YeconMetazone.GetBackCarHue();
		int mzone_backcar_saturation = YeconMetazone.GetBackCarSaturation();
		if (value[0] != mzone_backcar_brightness
			|| value[1] != mzone_backcar_constrast
			|| value[2] != mzone_backcar_hue
			|| value[3] != mzone_backcar_saturation) {
			YeconMetazone.MetaSetBackCarVideoPara(value[0], value[1], value[2], value[3]);
			}
	}
	
	/**
	 * 写倒车镜像到mzone,不同才写
	 */
	void writeMzoneBackcarMirror() {
		//读出来的是1 2 3， 但写入要|0xe0
		int mirrorValue = SystemProperties.getInt(Tag.PERSYS_BACKCAR_MIRROR,
				XmlParse.backcar_mirror)|0xe0;
		
		if (YeconMetazone.GetBackCarMirror() != mirrorValue) {
			//不同再重新写
			YeconMetazone.SetBackCarMirror(mirrorValue);
		}
	}
	
	private void initMetezone() {
		//先从xml读数据，写入
		if (YeconMetazone.GetPoweroffFlag() != 0x00) {
			YeconMetazone.SetPoweroffFlag(0x00);
		}
	}

	private void readXMLFile() {
		File file = new File(Tag.XML_PATH_USER);
		if (file.exists()) {
			XmlRW.readXMLFile(Tag.XML_PATH_USER);
		} else {
			file = new File(Tag.XML_PATH_DEF);
			if (file.exists()) {
				XmlRW.readXMLFile(Tag.XML_PATH_DEF);
			}
		}
	}


	private void startMcuService(Context context) {
		Intent intent = new Intent(context, CarSettingsBootService.class);
		intent.setAction("com.yecon.carsetting.mcuService");
		context.startService(intent);
	}

	private void CloseMute(Context context) {
		final AudioManager mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		boolean muted = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC);
		if (muted) {
			mAudioManager.setYeconVolumeMute(AudioManager.STREAM_MUSIC, false, 0,
					YeconConstants.SRC_VOLUME_UNMUTE);
		}

	}

}
