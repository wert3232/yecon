package com.yecon.sound.setting.service;

import static android.constant.YeconConstants.ACTION_QB_POWERON;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.preference.PreferenceManager;

import com.yecon.common.RebootStatus;
import com.yecon.common.RebootStatus.SOURCE;
import com.yecon.sound.setting.MyApplication;
import com.yecon.sound.setting.unitl.SoundArray;
import com.yecon.sound.setting.unitl.Tag;
import com.yecon.sound.setting.unitl.mtksetting;

public class BootCompleteRecevice extends BroadcastReceiver {

	mtksetting mmtksetting = new mtksetting();
	Intent intentservices;

	@Override
	public void onReceive(Context mContext, Intent intent) {
		String action = intent.getAction();
		if (Tag.ACTION_BOOT_COMPLETED.equals(action)) {
			initPara(mContext);
		} else if (Tag.MCU_ACTION_LOUDNESS_KEY.equals(action)) {
			mmtksetting.setSharedPreferences(PreferenceManager
					.getDefaultSharedPreferences(mContext));
			boolean bLoundness = !mmtksetting.uiState.getBoolean(Tag.loudNess_enable, false);
			int uLoudNessType = bLoundness ? SystemProperties.getInt(Tag.PERSYS_AUDIO[4],
					Tag.audio[4]) : 0;
			mmtksetting
					.SetLoudNess(uLoudNessType, SoundArray.LoudNess_gLoudNessGain[uLoudNessType]);
			mmtksetting.editor.putBoolean(Tag.loudNess_enable, bLoundness);
			mmtksetting.editor.commit();
		} else if (Tag.ACTION_SETTING_AUDIO_EXIT.equals(action)) {
			MyApplication.getInstance().exit();
		} else if (Tag.ACTION_RESET_FACTORY.equals(action)) {
			resetFactory(mContext);
		} else if(ACTION_QB_POWERON.equals(action)){
			//reinit sound device setting.
			restorePara(mContext);
		}
	}

	private void initPara(Context context) {

		for (int i = 0; i < Tag.PERSYS_AUDIO.length; i++) {
			Tag.audio[i] = SystemProperties.getInt(Tag.PERSYS_AUDIO[i], Tag.audio[i]);
		}

		if (RebootStatus.isReboot(SOURCE.SOURCE_SOUNDVIEW)) {
			resetFactory(context);
		} else {
			restorePara(context);
		}
	}

	private void resetFactory(Context context) {
		mmtksetting.setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context));
		mmtksetting.editor.putInt(Tag.treble, Tag.audio[0]);
		mmtksetting.editor.putInt(Tag.alto, Tag.audio[1]);
		mmtksetting.editor.putInt(Tag.bass, Tag.audio[2]);

		mmtksetting.editor.putFloat(Tag.treble_angle, Tag.audio[0] * Tag.RANGLE / 14);
		mmtksetting.editor.putFloat(Tag.alto_angle, Tag.audio[1] * Tag.RANGLE / 14);
		mmtksetting.editor.putFloat(Tag.bass_angle, Tag.audio[2] * Tag.RANGLE / 14);
		mmtksetting.editor.putFloat(Tag.subwoofer_angle, Tag.audio[3] * Tag.RANGLE / 20
				- Tag.RANGLE);
		mmtksetting.editor.commit();
		
		// subwoofer
		boolean enable = SystemProperties.getBoolean(Tag.PERSYS_SUBWOOFER_ENABLE, true);
		if (enable) {
			mmtksetting.EnableSubwoofer(true);
			mmtksetting.SetSubwoofer(Tag.audio[3]);
		} else {
			mmtksetting.EnableSubwoofer(false);
		}
		// eq
		int nEQ_type = 6;
		mmtksetting.SetEQValues(nEQ_type);
		mmtksetting.editor.putInt(Tag.eq_type, nEQ_type);
		mmtksetting.editor.putBoolean(Tag.eq_type_self, false);
		// scene
		int index_ReverbCoef = 0;
		mmtksetting.editor.putInt(Tag.ReverbCoef, index_ReverbCoef);
		mmtksetting.setReverbCoef(index_ReverbCoef);
		// loundness
		boolean bLoundness = false;
		int uLoudNessType = 0;
		mmtksetting.SetLoudNess(uLoudNessType, SoundArray.LoudNess_gLoudNessGain[uLoudNessType]);
		mmtksetting.editor.putBoolean(Tag.loudNess_enable, bLoundness);
		// balance
		mmtksetting.setBalance(Tag.audio[6], Tag.audio[5]);
		mmtksetting.editor.putInt(Tag.valueX_tag, Tag.audio[6]);
		mmtksetting.editor.putInt(Tag.valueY_tag, Tag.audio[5]);
		mmtksetting.editor.commit();
	}

	void restorePara(Context context) {
		mmtksetting.setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context));
		// subwoofer
		boolean enable = SystemProperties.getBoolean(Tag.PERSYS_SUBWOOFER_ENABLE, true);
		if (enable) {
			mmtksetting.EnableSubwoofer(true);
			int value = mmtksetting.uiState.getInt(Tag.subwoofer, Tag.audio[3]);
			mmtksetting.SetSubwoofer(value);
		} else {
			mmtksetting.EnableSubwoofer(false);
		}
		// eq
		if (mmtksetting.uiState.getBoolean(Tag.eq_type_self, false)) {
			Integer[] selfEQGain = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			selfEQGain = mmtksetting.getSelfEQGain(Tag.eq_self_value);
			if (selfEQGain != null)
				mmtksetting.SetEQValuesSelf(selfEQGain);
		} else {
			int type_id = mmtksetting.uiState.getInt(Tag.eq_type, 6);
			mmtksetting.SetEQValues(type_id);
		}
		// scene
		int index_ReverbCoef = mmtksetting.uiState.getInt(Tag.ReverbCoef, 0);
		mmtksetting.setReverbCoef(index_ReverbCoef);
		// loundness
		boolean bLoundness = mmtksetting.uiState.getBoolean(Tag.loudNess_enable, false);
		int uLoudNessType = bLoundness ? SystemProperties.getInt(Tag.PERSYS_AUDIO[4], Tag.audio[4])
				: 0;
		mmtksetting.SetLoudNess(uLoudNessType, SoundArray.LoudNess_gLoudNessGain[uLoudNessType]);
		// balance
		int valueX = mmtksetting.uiState.getInt(Tag.valueX_tag, 0);
		int valueY = mmtksetting.uiState.getInt(Tag.valueY_tag, 0);
		mmtksetting.setBalance(valueX, valueY);
	}
}
