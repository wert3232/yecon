package com.android.settings;

import com.android.settings.mtksetting.MyBinder;
import com.android.settings.deviceinfo.UsbSettings;
import com.autochips.settings.AudFuncOption;
import com.mtksetting.Array;

import android.R.integer;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.content.res.Resources;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.nfc.Tag;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemProperties;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.media.AudioSystem;
import android.provider.Settings;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioService;
import android.view.IWindowManager;
import com.yecon.metazone.YeconMetazone;			//Jade add;
import com.autochips.settings.AtcSettings;		// by WYD 20150714
import android.os.SystemClock;


public class BootCompletedReceiver extends BroadcastReceiver {

    //public static final String REGISTER_AND_SYNC_BRIGHTNESS = "com.mediatek.registerAndSyncBrightness";
    public static final String TAG = "Settings BootCompletedReceiver";
    // Audio
    int bass;
    int treble;
    int balance;
    int loudness;
    int EQ;
    int reverb;
    int pl2;
    int upmix;
    int spdifout;
    int srsstate;
    int srsmode;
    int phantom;
    int fullband;
    int truebass;
    
    int frontProgress;
    int rearProgress;
    int btProgress;
    int gisProgress;
    int backcarProgress;

    int speakertype;
    int speakersize;
    int testspeaker;

    int brightness;
    int contrast;
    int hue;
    int saturation;
    int backlight;

    int tvtype;
    int caption;
    int screensaver;
    int lasemem;
    int dialog;
    int dolbymode;
    int dolbydyn;
    int tvdisplay;
    int pbc;
    int audiolan;
    int sublan;
    int menulan;
    int parental;
	int savelogenabled;
	boolean screen_rotated;

    private AudioManager mAudioManager;
    mtksetting mmtksetting = new mtksetting();
    Intent intentservices;
    SharedPreferences setting;
    private Context mContext;
    boolean mDvpSettingfag = false;

   // public static final int DEFAULT_BRIGHTNESS_VALUE = 140;
   // public static final int MINIMUM_BACKLIGHT = android.os.PowerManager.BRIGHTNESS_OFF + 20;
   // public static final int MAXIMUM_BACKLIGHT = android.os.PowerManager.BRIGHTNESS_ON;

    private static final String ACTION_BLUETOOTH_CALL_STATUS_CHANGE = "com.mediatek.bluetooth.PhoneCallActivity.action.BLUETOOTH_CALL_STATUS_CHANGE";
    private static final String ACTION_BLUETOOTH_CALL_STATUS = "com.mediatek.bluetooth.PhoneCallActivity.action.BLUETOOTH_CALL_STATUS";
    private static final int CALL_STATE_START    = 1;
    private static final int CALL_STATE_FINISH   = 0;  
    private static boolean  mIsBTCalling = false;
    private IWindowManager mWindowManager;

	 private void writePointerLocationOptions() {			//Jade add ;
	 
	 	int show_touch = YeconMetazone.GetTouchShowLocation();
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.POINTER_LOCATION, show_touch);
		Log.d("Jade_settings","show_touch"+show_touch);
		
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        setting = context.getSharedPreferences(Array.MTKSettings, Context.MODE_PRIVATE);
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.d("Jade_settings","onReceive...");
		
        if("android.intent.action.BOOT_COMPLETED".equals(action)){
        	//by lzy close animation
        	mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        	try {
				mWindowManager.setAnimationScale(1, 0);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            intentservices = new Intent(context, mtksetting.class);
            IBinder mibinder = mmtksetting.onBind(intentservices);
            MyBinder binder = (MyBinder) mibinder;
            mmtksetting = binder.getService();
            context.startService(intentservices);
			Log.d("Jade_settings","BOOT_COMPLETED...");
			//Jade add;
			int DACType = YeconMetazone.GetAudioDACType();	// WYD add 20150714
			Log.d("WYD","DAC TYPE = "+DACType);
			AtcSettings.Audio.SelectDAC(0,DACType,2);
			
			 writePointerLocationOptions();

            /**
             * jy 20150519 modify for volume
             */
            frontProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            rearProgress = setting.getInt("rearprogress_key", 11);
            btProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO);
            gisProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_GIS);
            backcarProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_BACKCAR);

            bass = setting.getInt("bass", 0);
            treble = setting.getInt("treble", 0);
            balance = setting.getInt("balance", 0);
            loudness = setting.getInt("loudness", 0);
            EQ = setting.getInt("eq_key", 0);
            reverb = setting.getInt("reverb_key", 0);
            pl2 = setting.getInt("pl2_key", 0);
            upmix = setting.getInt("mix_key", 0);
            spdifout = setting.getInt("spdif_key", 0);
            srsstate = setting.getInt("srsswitch_key", 0);
            srsmode = setting.getInt("srsmode_key", 0);
            phantom = setting.getInt("srsphantom_key", 0);
            fullband = setting.getInt("srsfullband_key", 0);
            truebass = setting.getInt("srstruebass_key", 0);
            testspeaker = setting.getInt("testspeaker_key", 0);
            speakertype = setting.getInt("speakerslayouttype_key", 384);
            speakersize = setting.getInt("speakerslayoutsize_key", 31);
            
            screen_rotated = setting.getBoolean("screen_rotated", false);
            Intent intentRotated = new Intent("com.ATCSetting.mainui.rotationservice");
            intentRotated.putExtra("screen_rotated", screen_rotated);
            mContext.startService(intentRotated);
            
            //brightness = setting.getInt("bridhtness", 55);
            //contrast = setting.getInt("contrast", 20);
            //hue = setting.getInt("hue", 50);
            //saturation = setting.getInt("saturation", 50);
            //backlight = setting.getInt("backlight", 55);
            //Log.w(TAG, "bri=" + brightness+ ", con=" + contrast + ", hue=" + hue + ", sat=" + saturation + ", bac=" + backlight);
            if(!SystemProperties.getBoolean("persist.sys.cvbs_enable", true)){
            	AtcSettings.Display.SetFrontRearDisplayFollow(true);
            }
            else{
            	AtcSettings.Display.SetFrontRearDisplayFollow(false);
            }
            
            AtcSettings.Audio.SetDspMixCh(0x7F);
            
            // DVD
            tvdisplay = setting.getInt("display", 0);
            caption = setting.getInt("caption", 1);
            screensaver = setting.getInt("screensaver_key", 0);
            lasemem = setting.getInt("lastmemorytype", 0);
            dialog = setting.getInt("dialogtype", 0);
            dolbymode = setting.getInt("dualmonotype", 0);
            dolbydyn = setting.getInt("dynamictype", 8);
            tvtype = setting.getInt("tvtype_key", 2);
            pbc = setting.getInt("pbctype_key", 0);
            audiolan = setting.getInt("audiolantype_key", 0);
            sublan = setting.getInt("sublantype_key", 0);
            menulan = setting.getInt("menulantype_key", 0);
            parental = setting.getInt("parentaltype_key", 8);

            int saveLogFlag = Settings.Secure.getInt(context.getContentResolver(),
                Settings.Secure.SAVELOG_ENABLED, 0);
            Settings.Secure.putInt(context.getContentResolver(),
                Settings.Secure.SAVELOG_ENABLED, saveLogFlag);
            writeEnableSaveLog(saveLogFlag);
            setAudio_Jni(bass, treble, balance, loudness, EQ, reverb, pl2, upmix, spdifout, srsstate, 
                srsmode, phantom, fullband, truebass, testspeaker, speakertype, speakersize);

            //mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, btProgress, 0);
            //mmtksetting.SetRearVolume(mtksetting.getVolume(rearProgress));
            //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, frontProgress, 0);
            //mAudioManager.setStreamVolume(AudioManager.STREAM_GIS, gisProgress, 0);
            //mAudioManager.setStreamVolume(AudioManager.STREAM_BACKCAR, backcarProgress, 0);

            //SetDisplay_Jni(context, brightness, contrast, hue, saturation, backlight);

            SetDvd_Jni(tvdisplay, caption, screensaver, lasemem, dialog, dolbymode,
                dolbydyn, tvtype, pbc, audiolan, sublan, menulan, parental);

            // Begin: to set the USB Device.
            /* 
            boolean mUsbDeviceValue = setting.getBoolean("usbdevice_value", true);
            String usbConfig;
            if( mUsbDeviceValue ){
                usbConfig = SystemProperties.get("sys.usb.config");
                if(!usbConfig.equals("adb,mass_storage")){
                    SystemProperties.set("sys.usb.config", "adb,mass_storage");
                    //changeMassNotification(true);
                }
                changeMassNotification(true);
            } else{
                usbConfig = SystemProperties.get("sys.usb.config");
                if(!usbConfig.equals("adb")){
                    SystemProperties.set("sys.usb.config", "adb");
                    //changeMassNotification(false);
                }
                changeMassNotification(false);
            }*/
        }
		else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
            if (UsbSettings.mAdbAndMassStorageNotificationShown) {
                changeMassNotification(false);
                changeMassNotification(true);
            }
        } 
		/*else if (REGISTER_AND_SYNC_BRIGHTNESS.equals(action)) {
            // this broadcast is sent from onCreate in Setting;
            Log.d(TAG, REGISTER_AND_SYNC_BRIGHTNESS);
            syncBrightnessToHW();
            registerBrightnessChangedObserver();
        }*/
        //for ringer mode sync to  VolumeMute.
        else if("android.media.RINGER_MODE_CHANGED".equals(action))
        {
            Log.i(TAG,"BootCompletedReceiver RINGER_MODE_CHANGED");
            int value = 0;
            int mode = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL);     
            value = (mode == AudioManager.RINGER_MODE_NORMAL)?0:1;

            mmtksetting.SetMute(value);
            SharedPreferences.Editor mMuteEditor = setting.edit();
            mMuteEditor.putInt("volume_value", value);
            mMuteEditor.commit();
        }
        
        else if("android.media.GIS_AUDIO_STATUS_ACTION".equals(action))
        {
            boolean GISAudiostatus = intent.getBooleanExtra(AudioTrack.EXTRA_GIS_AUDIO_STATUS_TYPE, false);
            mtksetting.setGISAudioStatus(GISAudiostatus);
            boolean getGISAudiostatus = mtksetting.getGISAudioStatus();
            Log.i(TAG,"get GIS_AUDIO_STATUS_ACTION "+getGISAudiostatus);
        }
        else if("autochips.intent.action.QB_POWERON".equals(action)
            || "com.yecon.action.ACC_ON".equals(action))
        {
        	if(!SystemProperties.getBoolean("persist.sys.cvbs_enable", true)){
        		AtcSettings.Display.SetFrontRearDisplayFollow(true);
        	}
        }
        else if("autochips.intent.action.QB_POWEROFF".equals(action)
            || "com.yecon.action.ACC_OFF".equals(action))
        {
        	if(!SystemProperties.getBoolean("persist.sys.cvbs_enable", true)){
        		AtcSettings.Display.SetFrontRearDisplayFollow(false);
        	}
        }  
    }

    // Begin : Added by fei.liu
    /*void registerBrightnessChangedObserver() {
        mContext.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
                true, mBrightnessOberserver);
    }

    private ContentObserver mBrightnessOberserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            Log.w(TAG, "mBrightnessOberserver, onChange");
            syncBrightnessToHW();
        }
    };*/

   /*void syncBrightnessToHW() {
        int brightnessValue = Settings.System.getInt(mContext.getContentResolver(),
                                                     Settings.System.SCREEN_BRIGHTNESS, 
                                                     DEFAULT_BRIGHTNESS_VALUE);

        int brightnessValue2 = setting.getInt("brightnessValue2", -1);

        Log.d(TAG, "syncBrightnessToHW(), brightnessValue:" + brightnessValue
                + ", brightnessValue2:" + brightnessValue2);
        if(brightnessValue2 == -1){
            brightnessValue2 = brightnessValue;
            SharedPreferences.Editor Editor = setting.edit();
            Editor.putInt("brightnessValue2", brightnessValue);
            Editor.commit();
        }
        if(brightnessValue != brightnessValue2){

            if (brightnessValue <= MINIMUM_BACKLIGHT) {
                brightnessValue = MINIMUM_BACKLIGHT;
            }
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue);
            int backlight = (brightnessValue - 20)*100/235; //0~100
            SharedPreferences.Editor mBrightnessEditor = setting.edit();
            mBrightnessEditor.putInt("backlight", backlight);
            mBrightnessEditor.putInt("brightnessValue2", brightnessValue);
            mBrightnessEditor.putString("brigth", "brigth");
            mBrightnessEditor.putString("back", "back");
            mBrightnessEditor.commit();
            if(backlight < 10){
                mmtksetting.GSetBackLightLevel(10);
            }else{
                mmtksetting.GSetBackLightLevel(backlight);
            }
        }
    }*/


    public void setAudio_Jni(int bass, int treble, int balance, int loudness,
            int EQ, int reverb, int pl2, int upmix, int spdifout,
            int srsstate, int srsmode, int phantom, int fullband, int truebass,
            int testspeaker, int speakertype, int speakersize) {

        String init = setting.getString("init", "");
        if (init.equals("")) {
            int SPEAKER_LAYOUT_LRLSRS = 0x0001 << 6;
            int SPEAKER_LAYOUT_LRCLSRS   = 0x0001 << 7;  //L/R/C/LS/RS
            int SPEAKER_LAYOUT_SUBWOOFER = 0x0001 << 8;  //Sub
            
            int SPEAKER_LAYOUT_C_LARGE  = 0x0001 << 0;   //Center channel large
            int SPEAKER_LAYOUT_L_LARGE  = 0x0001 << 1;   //Left channel large
            int SPEAKER_LAYOUT_R_LARGE  = 0x0001 << 2;   //Right channel large
            int SPEAKER_LAYOUT_LS_LARGE = 0x0001 << 3;   //Left surround channel large
            int SPEAKER_LAYOUT_RS_LARGE = 0x0001 << 4;   //Right surround channel large
            
            speakertype = SPEAKER_LAYOUT_LRLSRS;
            speakersize = SPEAKER_LAYOUT_L_LARGE |
                          SPEAKER_LAYOUT_R_LARGE | SPEAKER_LAYOUT_LS_LARGE |
                          SPEAKER_LAYOUT_RS_LARGE;

            SharedPreferences.Editor mADECEditor = setting.edit();

			mADECEditor.putInt("mix_key", 1);
            mADECEditor.putInt("frontspeaker_key", 0);
            mADECEditor.putInt("centerspeakeron_key", 0);
            mADECEditor.putInt("surroundon_key", 0);
            mADECEditor.putInt("subwooferspeaker_key", 1);
            mADECEditor.putInt("speakerslayouttype_key", speakertype);
            mADECEditor.putInt("speakerslayoutsize_key", speakersize);
            
            mADECEditor.putInt("domnmix_state", 2);
            mADECEditor.putInt("centerspeaker_state", 0);
            mADECEditor.putInt("surround_state", 0);

            mADECEditor.putString("init", "init");
            mADECEditor.commit();
            Log.w("BootCompletedReceiver","FirstInit, speakertype:" + 
                speakertype + ", speakersize:" + speakersize);
    
        } else {
            Log.w("BootCompletedReceiver","Not FirstInit, speakertype:" + 
                speakertype + ", speakersize:" + speakersize);
        }
        mmtksetting.SetSpeakerLayout(speakertype, speakersize);
        
        AudFuncOption afo = new AudFuncOption();
        afo.u4FuncOption0 = 1 << 4;
        mmtksetting.SetAudFuncOption(afo);
    
        // modify by lzy 2015.9.21
//        int[] EQGain = new int[11];
//        for (int idx = 0; idx < 11; idx++) {
//            int temp = 0;
//            
//            if (idx >= 1 && idx <= 3) {
//                temp = Array.gEQTypePos[EQ][idx] + bass;
//            } else if (idx >= 8 && idx <= 10) {
//                temp = Array.gEQTypePos[EQ][idx] + treble;
//            } else {
//                temp = Array.gEQTypePos[EQ][idx];
//            }
//            
//            if (temp > 14) {
//                temp =  14;
//            } else if (temp < -14) {
//                temp = -14;
//            }
//
//            if (idx == 0) {
//                EQGain[idx] = Array.g_dryValues[temp + 14];
//            } else {
//                EQGain[idx] = Array.g_ganValues[temp + 14];
//            }
//        }
//        if (mmtksetting == null) {
//        } else {
//            mmtksetting.SetEQValues(EQGain);
//        }    

//        int i4RightValue = balance + 20;
//        int i4LeftValue = 40 - i4RightValue;
//        mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4LeftValue],
//                0);
//        mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4LeftValue],
//                2);
//        mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4RightValue],
//                1);
//        mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4RightValue],
//                3);
//        int[] rLoudNessGain = Array.LoudNess_gLoudNessGain[loudness];
//        mmtksetting.SetLoudNess(loudness, rLoudNessGain);

//        int ReverbCoef[] = null;
//        if (reverb == 0) {
//            ReverbCoef = Array.ReverbCoef_off;
//        } else if (reverb == 1) {
//            ReverbCoef = Array.ReverbCoef_live;
//        } else if (reverb == 2) {
//            ReverbCoef = Array.ReverbCoef_hall;
//        } else if (reverb == 3) {
//            ReverbCoef = Array.ReverbCoef_concert;
//        } else if (reverb == 4) {
//            ReverbCoef = Array.ReverbCoef_cave;
//        } else if (reverb == 5) {
//            ReverbCoef = Array.ReverbCoef_bathroom;
//        } else {
//
//            ReverbCoef = Array.ReverbCoef_arena;
//        }
//        mmtksetting.SetReverbType(reverb, ReverbCoef);

        upmix = setting.getInt("mix_key", 0);
        int[] rUpmixGain = null;
        if (upmix == 0) {
            rUpmixGain = Array.rUpmixGain_0;
        } else {
            rUpmixGain = Array.rUpmixGain_1;
        }
        mmtksetting.SetUpMix(upmix, rUpmixGain);

        double mspeakerSize = 2;
        int centerspeak = setting.getInt("centerspeaker_state", 0);
        int surroundspeak = setting.getInt("surround_state", 0);
        int subwoofer = setting.getInt("subwooferspeaker_key", 0);
        if( centerspeak != 2 )
            mspeakerSize += 2;
        if( surroundspeak != 2 )
            mspeakerSize += 2;
        if( subwoofer == 1 )
            mspeakerSize += 1;
        
        int enableSrs = setting.getInt("Share_SRS", 0);
        int enablePl2 = setting.getInt("Share_PL2", 0);
        if( enablePl2 == 0 && enableSrs == 0 )
        {
            Log.d("AdvanceAudioSettings","Set mSrs and mPl2");
        }
        else if( enablePl2 == 0 && enableSrs == 1 )
        {
            Log.d("AdvanceAudioSettings","Set mSrs");
            if( mspeakerSize > 5 )
            {
                mmtksetting.SetSRSSwitch(srsstate);
                mmtksetting.SetSRSMode(srsmode);
                mmtksetting.SetSRSPhantom(phantom);
                mmtksetting.SetSRSFullBand(fullband);
                mmtksetting.SetSRSTrueBass(truebass);
            }
        }
        else if( enablePl2 == 1 && enableSrs == 0 )
        {
            Log.d("AdvanceAudioSettings","Set Pl2");
            if( mspeakerSize > 2 ){
                mmtksetting.SetPL2(pl2);
                mDvpSettingfag = true;
            }
        }
        
//        i4RightValue = balance;
//        i4LeftValue = -balance;
//        int rValues[] = new int[6];
//        rValues[0] = 0;
//        rValues[1] = i4LeftValue;
//        rValues[2] = i4RightValue;
//        rValues[3] = i4LeftValue;
//        rValues[4] = i4RightValue;
//        rValues[5] = 0;
//        int rvalue[] = new int[]{0, bass, bass, bass, 0, 0, 0, 0, treble, treble, treble};
//        int mVolumeValue = setting.getInt("volume_value", 0);
//        int value = 0;
//        if( mVolumeValue == 1 ) {
//                value = 1;
//                int soundEffict = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
//                if( soundEffict == 1 ) {
//                    Settings.System.putInt(mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
//                }
//        }else {
//            value = 0;
//            int soundEffict = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
//            if( soundEffict == 1 ) {
//            }
//        }
//        mmtksetting.SetMute(value);
//        SharedPreferences.Editor mMuteEditor = setting.edit();
//        mMuteEditor.putInt("volume_value", value);
//        mMuteEditor.commit();
    }

    /*public void SetDisplay_Jni(Context context, int brightness, int contrast, int hue,
            int saturation, int backlight) {
        Log.w(TAG,"SetDisplay_Jni, brightness:" + brightness + ", contrast:" + contrast 
            + ", hue:" + hue + ", saturation:" + saturation + ", backlight:" + backlight);
        Log.w(TAG,"SetDisplay_Jni, MAXIMUM_BACKLIGHT:" + MAXIMUM_BACKLIGHT + ", MINIMUM_BACKLIGHT" + MINIMUM_BACKLIGHT);
        if (mmtksetting == null) {

        } else {
            String bright=setting.getString("brigth", "");
            if(bright.equals("")){
                Log.w(TAG, "bright equals blank");
                mmtksetting.GSetBrightnessLevel(55); //brightness: always be 55
                mmtksetting.GSetContrastLevel(20);
                mmtksetting.GSetHueLevel(50);
                mmtksetting.GSetSaturationLevel(50);
                mmtksetting.GSetBackLightLevel(55);
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 55*235/100 + 20);
                SharedPreferences.Editor Editor = setting.edit();
                Editor.putInt("brightnessValue2", 55*235/100 + 20);
                Editor.commit();
            }else{
                if (brightness < 10)
                {
                    mmtksetting.GSetBrightnessLevel(10);
                }
                else
                {
            mmtksetting.GSetBrightnessLevel(brightness); //brightness: always be 55
                }
                mmtksetting.GSetContrastLevel(contrast);
                mmtksetting.GSetHueLevel(hue);
                mmtksetting.GSetSaturationLevel(saturation);
                if(backlight < 5){
                    mmtksetting.GSetBackLightLevel(5);
                }else{
                    mmtksetting.GSetBackLightLevel(backlight);
                }
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, backlight*235/100 + 20);
                SharedPreferences.Editor Editor = setting.edit();
                Editor.putInt("brightnessValue2", backlight*235/100 + 20);
                Editor.commit();
            }
            
        }

    }*/

    public void SetDvd_Jni(int tvdisplay, int caption, int screencsver,
            int lastmen, int dialog, int dolbymode, int dolbydyn,
            int tvtype, int pbc, int audiolan, int sublan, int menulan,
            int parental) {
       // mmtksetting.GOpenDVPSet();
        mmtksetting.SetDisplayOutputType(tvdisplay);
        mmtksetting.SetTVType(tvtype);
        mmtksetting.SetPBCOn(pbc);
        mmtksetting.SetAudioLanType(audiolan);
        mmtksetting.SetSubLanType(sublan);
        mmtksetting.SetMenuLanType(menulan);
        mmtksetting.SetParentalType(parental);
        mmtksetting.SetPwdModeType(0);
        //mmtksetting.DVP_GSetSpeakerLayout(0, 6);
        mmtksetting.SetDialogType(dialog);
        mmtksetting.SetSpdifOutputType(1);
        //mmtksetting.DVP_GSetEQType(0);
        if (mDvpSettingfag == false){
         //   mmtksetting.DVP_GSetPL2(0);
        }
        //mmtksetting.DVP_GSetReverbType(0);
        mmtksetting.SetDualMonoType(dolbymode);
        mmtksetting.SetDynamicType(dolbydyn);
       // mmtksetting.DVP_GSetVolume(50);
        //mmtksetting.DVP_GSetRearVolume(50);
        //mmtksetting.DVP_GSetEQValue(0, 0);
        //mmtksetting.DVP_GSetBalance(1, 0);
      //  mmtksetting.GCloseDVPSet();
    }

    public void changeMassNotification(boolean showFlag) {
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        final int id = com.android.internal.R.string.adb_active_notification_title;
        if (showFlag && !UsbSettings.mAdbAndMassStorageNotificationShown) {
            Resources r = mContext.getResources();
            CharSequence title = r.getText(id);
            CharSequence message = r.getText(com.android.internal.R.string.adb_active_notification_message);
            Notification notification = new Notification();
            notification.icon = com.android.internal.R.drawable.stat_sys_adb;
            notification.when = 0;
            notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
            notification.tickerText = title;
            notification.defaults = 0; // please be quiet
            notification.sound = null;
            notification.vibrate = null;
            Intent intent = Intent.makeRestartActivityTask(new ComponentName("com.android.settings", "com.android.settings.UsbSettings"));
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0); 
            notification.setLatestEventInfo(mContext, title, message, pi);
            UsbSettings.mAdbAndMassStorageNotificationShown = true;
            notificationManager.notify(id, notification);
        } else if (UsbSettings.mAdbAndMassStorageNotificationShown) {
            UsbSettings.mAdbAndMassStorageNotificationShown = false;
            notificationManager.cancel(id);
        }   
    }
	public void writeEnableSaveLog(int SavelogEnabled) {
		if (SavelogEnabled == 1) {
			SystemProperties.set("ctl.start", "savelog");
		}else {
			SystemProperties.set("ctl.stop", "savelog");
		}
	}
}
