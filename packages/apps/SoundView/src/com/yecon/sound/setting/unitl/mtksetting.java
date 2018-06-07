/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yecon.sound.setting.unitl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.mcu.McuManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.autochips.settings.AtcSettings;
import com.autochips.settings.AtcSettings.Audio.SpeakerLayout;
import com.autochips.settings.AtcSettings.Audio.SpeakerSize;

public class mtksetting extends Service {
	private static String TAG = "mtksetting";
	private MyBinder myBinder = new MyBinder();
	//save the values
	public	static SharedPreferences.Editor editor;
	public	static SharedPreferences uiState ;
	private McuManager mMcuManager;
	private Context mContent;
	public mtksetting() {
		super();
	}
	public mtksetting(Context context) {
		super();
		mContent = context;
		if(mContent != null){
			mMcuManager = (McuManager) mContent.getSystemService(Context.MCU_SERVICE);
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		mContent = this;
		return myBinder;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		mContent = this;
	}
	
	public void setSharedPreferences(SharedPreferences mS){
		uiState = mS;
		editor = uiState.edit();
	}
	
	//sound effect
	public  Integer[] getSelfEQGain(String tag){
		String mValue=uiState.getString(tag, null);
		if(mValue == null) return null;
		String []sValues = mValue.split("#");
		Integer[] iValues = new Integer[sValues.length];
		for(int i=0;i<sValues.length;i++){
			iValues[i]=Integer.parseInt(sValues[i]);
		}
		return iValues;
	}
	
	public void saveSelfEQGain(Integer[] value,String tag){
		editor.putBoolean(Tag.eq_type_self, true);
		StringBuffer mValues = new StringBuffer();
		for(int i=0;i<value.length;i++){
			mValues.append(value[i]+"#");
		}
		editor.putString(tag, mValues.toString());
		editor.commit();	
	}
	
	public void SetEQValuesSelf(Integer[] selfValuse){
		int[] EQGain = new int[11];
		int uBass = uiState.getInt(Tag.bass, 0);
		int uTreble = uiState.getInt(Tag.treble, 0);
		int uAlto = uiState.getInt(Tag.alto, 0);
		for (int idx = 0; idx < 11; idx++) {
			int temp = 0;

			if (idx >= 1 && idx <= 3) {
				temp = selfValuse[idx] + uBass;
			} else if (idx >= 8 && idx <= 10) {
				temp = selfValuse[idx]  + uTreble;
			} else if (idx >= 4 && idx <= 7){
				temp = selfValuse[idx] + uAlto;
			} else{
				temp = selfValuse[idx];
			}

			if (temp > 14) {
				temp =  14;
			} else if (temp < -14) {
				temp = -14;
			}

			if (idx == 0) {
				EQGain[idx] = SoundArray.g_dryValues[temp + 14];
			} else {
				EQGain[idx] = SoundArray.g_ganValues[temp + 14];
			}
		}
		AtcSettings.Audio.SetEQValues(EQGain);
	}
	
	
	public void OnSetEQValues(int uEQType, int uTreble, int uBass, int uAlto) {
		int[] EQGain = new int[11];
		for (int idx = 0; idx < 11; idx++) {
			int temp = 0;

			if (idx >= 1 && idx <= 3) {
				temp = SoundArray.gEQTypePos[uEQType][idx] + uBass;
			} else if (idx >= 8 && idx <= 10) {
				temp = SoundArray.gEQTypePos[uEQType][idx] + uTreble;
			} else if (idx >= 4 && idx <= 7){
				temp = SoundArray.gEQTypePos[uEQType][idx] + uAlto;
			} else{
				temp = SoundArray.gEQTypePos[uEQType][idx];
			}
				

			if (temp > 14) {
				temp =  14;
			} else if (temp < -14) {
				temp = -14;
			}

			if (idx == 0) {
				EQGain[idx] = SoundArray.g_dryValues[temp + 14];
			} else {
				EQGain[idx] = SoundArray.g_ganValues[temp + 14];
			}
		}
		//FIXME:音效全交给MCU处理，所以注释掉了
//		AtcSettings.Audio.SetEQValues(EQGain);
	}
	
	public void SetEQValues(int index){
		sendEQ_MCU(index);
		
		int uBass = uiState.getInt(Tag.bass, 0);
		int uTreble = uiState.getInt(Tag.treble, 0);
		int uAlto = uiState.getInt(Tag.alto, 0); 
		OnSetEQValues(index, uTreble, uBass, uAlto);
	}
	
	public void SetTrebleValue(int index){
		int uEQType = uiState.getInt(Tag.eq_type, 0);
		int uBass = uiState.getInt(Tag.bass, 0);
		int uAlto = uiState.getInt(Tag.alto, 0); 
		int uTreble = index;
		OnSetEQValues(uEQType, uTreble, uBass, uAlto);
	}
	
	public void SetBassValue(int index){
		int uEQType = uiState.getInt(Tag.eq_type, 0);
		int uTreble = uiState.getInt(Tag.treble, 0);
		int uAlto = uiState.getInt(Tag.alto, 0); 
		int uBass = index;
		OnSetEQValues(uEQType, uTreble, uBass, uAlto);
	}
	
	public void SetAltoValue(int index){
		int uEQType = uiState.getInt(Tag.eq_type, 0);
		int uTreble = uiState.getInt(Tag.bass, 0);
		int uBass = uiState.getInt(Tag.alto, 0); 
		int uAlto = index;
		OnSetEQValues(uEQType, uTreble, uBass, uAlto);
	}

	
	public void EnableSubwoofer(boolean bEnable)
	{
		int  u4SpeakerLayoutType = SpeakerLayout.SPEAKER_LAYOUT_LRLSRS;		
		int  u4SpeakerSize = SpeakerSize.SPEAKER_LAYOUT_L_LARGE |
        		SpeakerSize.SPEAKER_LAYOUT_R_LARGE | SpeakerSize.SPEAKER_LAYOUT_LS_LARGE |
        		SpeakerSize.SPEAKER_LAYOUT_RS_LARGE;
		
		if ( bEnable )
		{
			u4SpeakerLayoutType |= SpeakerLayout.SPEAKER_LAYOUT_SUBWOOFER;
		}

		//�������ȹرգ���Ϊ�����������
//	    AudFuncOption afo = new AudFuncOption();
//	    afo.u4FuncOption0 = 1 << 4;
//	    AtcSettings.Audio.SetAudFuncOption(afo);
        
		AtcSettings.DVP.SetSpeakerLayout(u4SpeakerLayoutType, u4SpeakerSize);
		AtcSettings.Audio.SetSpeakerLayout(u4SpeakerLayoutType, u4SpeakerSize);
		
//		if(bEnable)
//		{
//			// subwoofer��ֵ, 0 ~ 40 (TRIM_LEVEL_MAX), ���ﲻ�������ֵȡ�м�ֵ20.
//			SetSubwoofer(20);
//		}	
	}
	
	
	public void SetSubwoofer(int Value)
	{
		AtcSettings.Audio.SetBalance(AtcSettings.Audio.BalanceType.nativeToType(5),SoundArray.Balance_au4TrimValue[Value]*5);
		Log.i("lzy", "...........................uSubwoofer = "+Value);
	}
	
	
	public void setReverbCoef(int value) {
		int ReverbCoef[] = null;
		if (value == 0) {
			ReverbCoef = SoundArray.ReverbCoef_off;
		} else if (value == 1) {
			ReverbCoef = SoundArray.ReverbCoef_live;
		} else if (value == 2) {
			ReverbCoef = SoundArray.ReverbCoef_hall;
		} else if (value == 3) {
			ReverbCoef = SoundArray.ReverbCoef_concert;
		} else if (value == 4) {
			ReverbCoef = SoundArray.ReverbCoef_cave;
		} else if (value == 5) {
			ReverbCoef = SoundArray.ReverbCoef_bathroom;
		} else {
			ReverbCoef = SoundArray.ReverbCoef_arena;
		}
		SetReverbType(value, ReverbCoef);
	}


	//balance
	/**
	 * 0 front_left
	 * 1 fron_right
	 * 2 rear_left
	 * 3 rear_right
	 * */
	public void setBalance(int valueX,int valueY){
		// ��Ƴ����м�ʱ,�ĸ������������,������ʱ,������˥��,ǰ���Ȳ���,��֮��Ȼ
		// ������ʱ��������˥��,�����Ȳ���,��֮��Ȼ
		// pt��Χ��-20~20, Balance����40��,����һ����˥������	
		
		final int MAX_EQ_BALANCE_LEVEL = 40;
		// ȷ������Ϸ�
		valueX = Math.min(valueX,MAX_EQ_BALANCE_LEVEL/2);
		valueX = Math.max(valueX,-(MAX_EQ_BALANCE_LEVEL/2));
		valueY = Math.min(valueY,MAX_EQ_BALANCE_LEVEL/2);
		valueY = Math.max(valueY,-(MAX_EQ_BALANCE_LEVEL/2));

		// �ĸ����ȵ�˥��ֵ,������£����Ҷ��Ը�������Ӱ��,ʹ��˥������Ǹ�ֵ
		int attenuate_fl = 0;
		int attenuate_fr = 0;
		int attenuate_rl = 0;
		int attenuate_rr = 0;
		if (valueX >=0)	// ǰ�����Ҫ˥��
		{
			attenuate_fl = valueX;
			attenuate_rl = valueX;
		}
		else	// ǰ�Һ��
		{
			attenuate_fr = Math.abs(valueX);
			attenuate_rr = Math.abs(valueX);
		}

		if (valueY >=0)	// ���󣬺���
		{
			attenuate_rl = Math.max(attenuate_rl, valueY);
			attenuate_rr = Math.max(attenuate_rr, valueY);
		}
		else	// ǰ��ǰ��
		{
			attenuate_fl = Math.max(attenuate_fl, Math.abs(valueY));
			attenuate_fr = Math.max(attenuate_fr, Math.abs(valueY));
		}
		
		// ȱʡ���м�ʱ,ÿ�������������
		int i4FLeftValue = MAX_EQ_BALANCE_LEVEL - attenuate_fl*2;
		int i4FRightValue = MAX_EQ_BALANCE_LEVEL - attenuate_fr*2;
		int i4RLeftValue = MAX_EQ_BALANCE_LEVEL - attenuate_rl*2;
		int i4RRightValue = MAX_EQ_BALANCE_LEVEL - attenuate_rr*2;
		/*L.e("i4FLeftValue:"+ i4FLeftValue + " i4FRightValue:" + i4FRightValue
				+ " i4RLeftValue:" + i4RLeftValue + " i4RRightValue:" + i4RRightValue);*/
		if(mMcuManager == null){
			if(mContent != null){
				mMcuManager = (McuManager) mContent.getSystemService(Context.MCU_SERVICE);
			}
		}
		
		if(mMcuManager != null){	
			mMcuManager.RPC_SetVolumeBalance(valueX, valueY);
			return;
		}
		
		/*AtcSettings.Audio.SetBalance( 
		AtcSettings.Audio.BalanceType.nativeToType(0),
		SoundArray.Balance_au4TrimValue[i4FLeftValue]);

        AtcSettings.Audio.SetBalance(
		AtcSettings.Audio.BalanceType.nativeToType(1),
		SoundArray.Balance_au4TrimValue[i4FRightValue]);

        AtcSettings.Audio.SetBalance(
		AtcSettings.Audio.BalanceType.nativeToType(2),
		SoundArray.Balance_au4TrimValue[i4RLeftValue]);
        
        AtcSettings.Audio.SetBalance(
		AtcSettings.Audio.BalanceType.nativeToType(3),
		SoundArray.Balance_au4TrimValue[i4RRightValue]);*/
	}

	private int getIntShare(String tag){
		return uiState.getInt(tag, 0);
	}
	
	private float getFloatShare(String tag){
		return uiState.getFloat(tag, 0f);
	}

	void syncBrightnessToHW() {
//		SharedPreferences setting =
//				getApplicationContext().getSharedPreferences(SoundArray.MTKSettings,Context.MODE_PRIVATE); 
		if( uiState == null )
		{
			Log.e(TAG, "mtk kkk syncBrightnessToHW setting is NULL");
			return;
		}
		int backlightValue=uiState.getInt("backlight", 55); //backlight value saved: 0 ~ 100
		Log.d(TAG, "mtk kkk3 syncBrightnessToHW backlightValue="+backlightValue);
		Settings.System.putInt(getApplicationContext().getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, backlightValue*235/100 + 20);
		if(backlightValue < 5){
			AtcSettings.Display.SetBackLightLevel(5);
		}else{
			AtcSettings.Display.SetBackLightLevel(backlightValue); //backlight value set to HW: 5~100
		}
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG, "onRebind");
		super.onRebind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart");
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");

		return super.onUnbind(intent);
	}

	public class MyBinder extends Binder {
		public mtksetting getService() {
			return mtksetting.this;
		}
	}

	/** BT_Volume setting */
	public int SetBTHFPVolume(int u4Vol) {
		Log.d(TAG, "SetBTHFPVolume");
		return AtcSettings.Audio.SetBTHFPVolume(u4Vol);
	}

	/** Display setting */
	public int GSetBrightnessLevel(int level) {
		Log.d(TAG, "GSetBrightnessLevel");
		return AtcSettings.Display.SetBrightnessLevel(level);
	}

	public int GSetContrastLevel(int level) {
		Log.d(TAG, "GSetContrastLevel");
		return AtcSettings.Display.SetContrastLevel(level);
	}

	public int GSetBackLightLevel(int level) {
		Log.d(TAG, "GSetBackLightLevel");
		return AtcSettings.Display.SetBackLightLevel(level);
	}

	public int GSetHueLevel(int level) {
		Log.d(TAG, "GSetHueLevel");
		return AtcSettings.Display.SetHueLevel(level);
	}

	public int GSetSaturationLevel(int level) {
		Log.d(TAG, "GSetSaturationLevel");
		return AtcSettings.Display.SetSaturationLevel(level);
	}

	/** Audio setting */
	public int SetMute(int eMute) {
		Log.d(TAG, "SetMute");
		boolean isMute = true;
		if (eMute == 0) {
			isMute = false;
		}
		else
		{
			isMute = true;
		}
		return AtcSettings.Audio.SetMute(isMute);
	}

	public int SelectDAC(int Output, int Type, int Pin) {
		Log.d(TAG, "SelectDAC");
		return AtcSettings.Audio.SelectDAC(Output, Type, Pin);
	}

	public int SetVolume(int u4Vol) {
		Log.d(TAG, "SetVolume");
		return AtcSettings.Audio.SetVolume(u4Vol);
	}

	public int SetRearVolume(int u4Vol) {
		Log.d(TAG, "SetRearVolume");
		return AtcSettings.Audio.SetRearVolume(u4Vol);
	}

	public int SetBalance(int u4Values, int eBalanceType) {
		Log.d(TAG, "SetBalance");
		return AtcSettings.Audio.SetBalance(AtcSettings.Audio.BalanceType.nativeToType(eBalanceType), u4Values);
	}

	public int SetReverbType(int eReverbType, int[] ReverbCoef) {
		Log.d(TAG, "SetReverbType");
		return AtcSettings.Audio.SetReverb(AtcSettings.Audio.ReverbType.nativeToType(eReverbType), ReverbCoef);
	}

	public int SetTestTone(int eTestTone, int eTestToneType) {
		Log.d(TAG, "SetTestTone");
		return AtcSettings.Audio.SetTestTone(AtcSettings.Audio.TestToneMainType.nativeToType(eTestTone), 
				AtcSettings.Audio.TestToneSubType.nativeToType(eTestToneType));
	}

	public int SetUpMix(int eUpMixType, int []aiUpMixGains) {
		Log.d(TAG, "SetUpMix");
		boolean isOn = true;

		if (eUpMixType == 0) {
			isOn = false;
		} else {
			isOn = true;
		}

		return AtcSettings.Audio.SetUpMix(isOn, aiUpMixGains);
	}

	public int SetLoudNess(int uLoudNessType, int [] aiLoudNessGains) {
		Log.d(TAG, "SetLoudNess");
		return AtcSettings.Audio.SetLoudness(AtcSettings.Audio.LoudnessMode.nativeToType(uLoudNessType), aiLoudNessGains);
	}

	public int SetEQValues(int[] rValues) {
		Log.d(TAG, "SetEQValues");
		return AtcSettings.Audio.SetEQValues(rValues);
	}

	public int SetAudFeature(int eAudFeatur) {
		Log.d(TAG, "SetAudFeature");
		return AtcSettings.Audio.SetDecFeatureType(AtcSettings.Audio.DecFeatureType.nativeToType(eAudFeatur));
	}

	public int SetSRSSwitch(int eCSIISwitch) {
		Log.d(TAG, "SetSRSSwitch");
		return AtcSettings.Audio.SetSRSSwitchType(AtcSettings.Audio.SRSSwitchType.nativeToType(eCSIISwitch));
	}

	public int SetSRSMode(int eCSIIMode) {
		Log.d(TAG, "SetSRSMode");
		return AtcSettings.Audio.SetSRSMode(AtcSettings.Audio.SRSMode.nativeToType(eCSIIMode));
	}

	public int SetSRSPhantom(int eCSIISwitch) {
		Log.d(TAG, "SetSRSPhantom");
		boolean isOn = false;

		if (eCSIISwitch == 0)
		{
			isOn = false;
		}
		else
		{
			isOn = true;
		}

		return AtcSettings.Audio.SetSRSPhantomOn(isOn);
	}

	public int SetSRSFullBand(int eCSIISwitch) {
		Log.d(TAG, "SetSRSFullBand");
		boolean  isOn = false;

		if (eCSIISwitch == 0)
		{
			isOn = false;
		}
		else
		{
			isOn = true;
		}

		return AtcSettings.Audio.SetSRSFullBandOn(isOn);
	}

	public int SetSRSFocus(int eFocus, int eCSIISwitch) {
		Log.d(TAG, "SetSRSFocus");
		boolean   isOn = false;

		if (eCSIISwitch == 0) {
			isOn = false;
		} else {
			isOn = true;
		}

		return AtcSettings.Audio.SetSRSFocusOn(AtcSettings.Audio.SRSFocusType.nativeToType(eFocus), isOn);
	}

	public int SetSRSTrueBass(int eCSIISwitch) {
		Log.d(TAG, "SetSRSTrueBass");

		boolean   isOn = false;

		if (eCSIISwitch == 0) {
			isOn = false;
		} else {
			isOn = true;
		}

		return AtcSettings.Audio.SetSRSTrueBassOn(isOn);
	}

	public int SetSRSTrueBassSize(int eTBSS, int eCSIITBSS) {
		Log.d(TAG, "SetSRSTrueBassSize");
		return AtcSettings.Audio.SetSRSTrueBassSize(AtcSettings.Audio.SRSTrueBassSizeType.nativeToType(eTBSS), eCSIITBSS);
	}

	public int SetSpeakerLayout(int u4SpeakerLayoutType, int u4SpeakerSize) {
		Log.d(TAG, "SetSpeakerLayout");
		return AtcSettings.Audio.SetSpeakerLayout(u4SpeakerLayoutType, u4SpeakerSize);
	}

	public int SetPL2(int ePL2Type) {
		Log.d(TAG, "SetPL2");
		return AtcSettings.Audio.SetPL2(AtcSettings.Audio.PL2Type.nativeToType(ePL2Type));
	}

	public int ChooseSpdifOutput(int eOutType) {
		Log.d(TAG, "ChooseSpdifOutput");
		return AtcSettings.Audio.ChooseSpdifOutput(AtcSettings.Audio.SpdifOutputType.nativeToType(eOutType));
	}

	/** DVP setting */
	public int SetDisplayOutputType(int eTVDisplayType) {
		Log.d(TAG, "DVP_GSetDisplayType");
		return AtcSettings.DVP.SetDisplayType(eTVDisplayType);
	}

	public int SetTVType(int eTVType) {
		Log.d(TAG, "DVP_GSetTVType");
		return AtcSettings.DVP.SetTVType(eTVType);
	}

	public int SetPBCOn(int ePBCType) {
		Log.d(TAG, "DVP_GSetPBCType");
		return AtcSettings.DVP.SetPBCType(ePBCType);
	}

	public int SetAudioLanType(int eAudioLanType) {
		Log.d(TAG, "DVP_GSetAudioLanType");
		return AtcSettings.DVP.SetAudioLanType(eAudioLanType);
	}

	public int SetSubLanType(int eSubLanType) {
		Log.d(TAG, "DVP_GSetSubLanType");
		return AtcSettings.DVP.SetSubLanType(eSubLanType);
	}

	public int SetMenuLanType(int eMenuLanType) {
		Log.d(TAG, "DVP_GSetMenuLanType");
		return AtcSettings.DVP.SetMenuLanType(eMenuLanType);
	}

	public int SetParentalType(int eParentalType) {
		Log.d(TAG, "DVP_GSetParentalType");
		return AtcSettings.DVP.SetParentalType(eParentalType);
	}

	public int SetPwdModeType(int ePwdModeType) {
		Log.d(TAG, "DVP_GSetPwdModeType");
		return AtcSettings.DVP.SetPwdModeType(ePwdModeType);
	}

	public int SetDialogType(int eDialogType) {
		Log.d(TAG, "DVP_GSetDialogType");
		return AtcSettings.DVP.SetDialogType(eDialogType);
	}

	public int SetSpdifOutputType(int eSpdifOutputType) {
		Log.d(TAG, "DVP_GSetSpdifOutputType");
		return AtcSettings.DVP.SetSpdifOutputType(eSpdifOutputType);
	}

	public int SetDualMonoType(int eDualMonoType) {
		Log.d(TAG, "DVP_GSetDualMonoType");
		return AtcSettings.DVP.SetDualMonoType(eDualMonoType);
	}

	public int SetDynamicType(int eDynamicType) {
		Log.d(TAG, "DVP_GSetDynamicType");
		return AtcSettings.DVP.SetDynamicType(eDynamicType);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//uiState = getSharedPreferences(SoundArray.MTKSettings, MODE_PRIVATE);
		//editor = uiState.edit();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void sendEQ_MCU(int index){
		if(mMcuManager == null){
			return;
		}
		byte[] data = {0};
		try {
			switch (index) {
				case 0:{
					data[0] = SoundArray.EFFECT_EQ_CODE_ROCK;
					mMcuManager.RPC_SetEQ(data);
				}
				break;
				case 1:{
					data[0] = SoundArray.EFFECT_EQ_CODE_POP;
					mMcuManager.RPC_SetEQ(data);
				}
				break;
				case 2:{
					data[0] = SoundArray.EFFECT_EQ_CODE_LIVE;
					mMcuManager.RPC_SetEQ(data);
				}
				break;
				case 3:{
					data[0] = SoundArray.EFFECT_EQ_CODE_DANCE;
					mMcuManager.RPC_SetEQ(data);
				}
				break;
				case 4:{
					data[0] = SoundArray.EFFECT_EQ_CODE_CLASSIC;
					mMcuManager.RPC_SetEQ(data);
				}
				break;
				case 5:{
					data[0] = SoundArray.EFFECT_EQ_CODE_SOFT;
					mMcuManager.RPC_SetEQ(data);
				}
				break;
				case 6:{
					data[0] = SoundArray.EFFECT_EQ_CODE_NORMAL;
					mMcuManager.RPC_SetEQ(data);
				}
				break;
				case 7:{
					data[0] = SoundArray.EFFECT_EQ_CODE_JAZZ;
					mMcuManager.RPC_SetEQ(data);
				}
				case 8:{
					data[0] = SoundArray.EFFECT_EQ_CODE_HIPHOP;
					mMcuManager.RPC_SetEQ(data);
				}
				break;
			}
		} catch (RemoteException e) {
		}	
	}
}

/*
 * class Native { private final static String TAG = "mtksetting native";
 * 
 * static { // The runtime will add "lib" on the front and ".o" on the end of //
 * the name supplied to loadLibrary. Log.i(TAG,
 * "System.load(\"libmtkset.so\");"); System.loadLibrary("mtkset");
 * Log.i(TAG,"load ok"); }
 * 
 * static native int add(int a, int b); static native int
 * GSetBrightnessLevel(int level); static native int GSetContrastLevel(int
 * level); static native int GSetBackLightLevel(int level); static native int
 * GSetHueLevel(int level); static native int GSetSaturationLevel(int level);
 * 
 * 
 * static native int GSetMute(int eMute); static native int GSetVolume(int
 * u4Vol); static native int GSetRearVolume(int u4Vol); static native int
 * GSetEQValue(int i4Band, int i4Value); static native int
 * GClientSetEQValues(int eEQType, int[] rEQValues, int[] rEQGain, boolean
 * fgSetBassOrTreble); static native int GClientSetBalance(int u4Values, int
 * eBalanceType); static native int GClientSetLoudNess(int uLoudNessType, int[]
 * rLoudNessGain); static native int GClientSetVolume(int u4Vol); static native
 * int GClientSetRearVolume(int u4Vol); static native int GClientSetEQType(int
 * eEQType, int[] rEQTypeValues, int[] rEQValues, boolean fgSetBassOrTreble);
 * static native int GClientSetReverbType(int eReverbType, int[] ReverbCoef);
 * static native int GClientSetTestTone(int eTestTone, int eTestToneType);
 * static native int GClientSetUpMix(int eUpMixType, int[] rUpmixGain); static
 * native int GSetEQValues(int eEQType, int[] rValues); static native int
 * GSetTestTone(int eTestTone); static native int GSetAudFeature(int
 * eAudFeatur); static native int GSetBalance(int u4Values,int eBalanceType);
 * static native int GSetSRSSwitch(int eCSIISwitch); static native int
 * GSetSRSMode(int eCSIIMode); static native int GSetSRSPhantom(int
 * eCSIISwitch); static native int GSetSRSFullBand(int eCSIISwitch); static
 * native int GSetSRSFocus(int eFocus,int eCSIISwitch); static native int
 * GSetSRSTrueBass(int eCSIISwitch); static native int GSetSRSTrueBassSize(int
 * eTBSS,int eCSIITBSS); static native int GSetSpeakerLayout(int
 * u4SpeakerLayoutType, int u4SpeakerSize); static native int GSetEQType(int[]
 * rValues, int eEQType); static native int GSetPL2(int ePL2Type); static native
 * int GSetReverbType(int eReverbType); static native int GSetUpMix(int
 * eUpMixType); static native int GSetLoudNess(int uLoudNessType); static native
 * int GChooseSpdifOutput(int eOutType);
 * 
 * 
 * static native int DVP_GSetDisplayType(int eTVDisplayType); static native int
 * DVP_GSetCaptionsType(int eCaptionsType); static native int
 * DVP_GSetScreenSaverType(int eScreenSaverType); static native int
 * DVP_GSetLastMemType(int eLastMemType); static native int DVP_GSetTVType(int
 * eTVType); static native int DVP_GSetPBCType(int ePBCType); static native int
 * DVP_GSetAudioLanType(int eAudioLanType); static native int
 * DVP_GSetSubLanType(int eSubLanType); static native int
 * DVP_GSetMenuLanType(int eMenuLanType); static native int
 * DVP_GSetParentalType(int eParentalType); static native int
 * DVP_GSetPwdModeType(int ePwdModeType); static native int
 * DVP_GSetSpeakerLayout(int u4SpeakerLayoutType, int u4SpeakerSize); static
 * native int DVP_GSetDialogType(int eDialogType); static native int
 * DVP_GSetSpdifOutputType(int eSpdifOutputType); static native int
 * DVP_GSetLpcmOutType(int eLpcmOutType); static native int
 * DVP_GSetCdelayCType(int eCoelayCType); static native int
 * DVP_GSetCdelaySubType(int eCoelaySubType); static native int
 * DVP_GSetCdelayLSType(int eCoelayLSType); static native int
 * DVP_GSetCdelayRSType(int eCoelayRSType); static native int DVP_GSetEQType(int
 * eEQType); static native int DVP_GSetPL2(int ePL2Type); static native int
 * DVP_GSetReverbType(int eReverbType); static native int
 * DVP_GSetDualMonoType(int eDualMonoType); static native int
 * DVP_GSetDynamicType(int eDynamicType); static native int DVP_GSetVolume(int
 * u4Vol); static native int DVP_GSetRearVolume(int u4Vol); static native int
 * DVP_GSetEQValue(int u4Band, int i4Value); static native int
 * DVP_GSetBalance(int u4Values, int eBalanceType); }
 */
