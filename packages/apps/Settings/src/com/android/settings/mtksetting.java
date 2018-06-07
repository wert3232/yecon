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

package com.android.settings;

import android.util.Log;
import android.os.Bundle;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;

import com.mtksetting.Array;
import com.autochips.settings.AtcSettings;
import com.autochips.settings.AudFuncOption;

import android.media.AudioService;


;

public class mtksetting extends Service {
    private static String TAG = "mtksetting";
    private MyBinder myBinder = new MyBinder();

    private static boolean mGISAudioStatus = false;
    private static float curve[][] = {{1, -56.0f}, {20, -34.0f}, {60, -11.0f}, {100, 0.0f}};

    public static int getVolume(int Index) {
        int volIdx = 100 * (Index)/AudioService.volumeMax;

        int segment = 0;
        if (volIdx < curve[0][0]) {         // out of bounds
            return 0;
        } else if (volIdx < curve[1][0]) {
            segment = 0;
        } else if (volIdx < curve[2][0]) {
            segment = 1;
        } else if (volIdx <= curve[3][0]) {
            segment = 2;
        } else {                                                               // out of bounds
            return 0x20000;
        }

        float decibels = curve[segment][1] + 
            ((float)(volIdx - curve[segment][0]))*
            ((curve[segment+1][1] - curve[segment][1]) /
            (curve[segment+1][0] - curve[segment][0]));

        int ret = (int)(Math.exp( decibels * 0.115129f) * 0x20000);
        //Log.d(TAG, "mtksetting getVolume multi "+(Math.exp( decibels * 0.115129f)));
        //Log.d(TAG, "mtksetting getVolume Index "+Index);
        return ret;
        
    }

    public static void setGISAudioStatus(boolean status) {
        mGISAudioStatus = status;
    }
    
    public static boolean getGISAudioStatus() {
        return mGISAudioStatus;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return myBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        super.onCreate();
        
        
        //add by vend_am00104 for CR134997
        //syncBrightnessToHW();//00
        
        Intent intent2 = new Intent("com.mediatek.registerAndSyncBrightness").
        setComponent(new ComponentName("com.android.settings",
        "com.android.settings.BootCompletedReceiver"));
        getApplicationContext().sendBroadcast(intent2);
    }

    /*void syncBrightnessToHW() {
        SharedPreferences setting = getApplicationContext().getSharedPreferences(Array.MTKSettings,
                Context.MODE_PRIVATE); 
        if( setting == null )
        {
            Log.e(TAG, "mtk kkk syncBrightnessToHW setting is NULL");
            return;
        }
        int backlightValue=setting.getInt("backlight", 55); //backlight value saved: 0 ~ 100
        Log.d(TAG, "mtk kkk3 syncBrightnessToHW backlightValue="+backlightValue);
        Settings.System.putInt(getApplicationContext().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, backlightValue*235/100 + 20);
        if(backlightValue < 5){
            AtcSettings.Display.SetBackLightLevel(20);
        }else{
            AtcSettings.Display.SetBackLightLevel(backlightValue); //backlight value set to HW: 5~100
        }
    }*///00
    
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
   /* public int GSetBrightnessLevel(int level) {
        Log.d(TAG, "GSetBrightnessLevel");
        return AtcSettings.Display.SetBrightnessLevel(level);
    }*/

    public int GSetContrastLevel(int level) {
        Log.d(TAG, "GSetContrastLevel");
        return AtcSettings.Display.SetContrastLevel(level);
    }

    /*public int GSetBackLightLevel(int level) {
        Log.d(TAG, "GSetBackLightLevel");
        return AtcSettings.Display.SetBackLightLevel(level);
    }*/

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
    
    public int SetAudFuncOption(AudFuncOption Afo) {
        Log.d(TAG, "SetAudFuncOption");
        return AtcSettings.Audio.SetAudFuncOption(Afo);
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
        AtcSettings.Audio.SetSpeakerLayout(u4SpeakerLayoutType, u4SpeakerSize);
        return AtcSettings.DVP.SetSpeakerLayout(u4SpeakerLayoutType, u4SpeakerSize);
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
