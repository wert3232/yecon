/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

import com.android.settings.mtksetting.MyBinder;
import com.mtksetting.Array;

public class SoundSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "SoundSettings";

    /** If there is no setting in the provider, use this. */
    private static final int FALLBACK_EMERGENCY_TONE_VALUE = 0;

    private static final String KEY_SILENT_MODE = "silent_mode";
    private static final String KEY_VIBRATE = "vibrate_when_ringing";
    private static final String KEY_RING_VOLUME = "ring_volume";
    private static final String KEY_MUSICFX = "musicfx";
    //mtk68031 delete private static final String KEY_DTMF_TONE = "dtmf_tone";
    private static final String KEY_SOUND_EFFECTS = "sound_effects";
    private static final String KEY_HAPTIC_FEEDBACK = "haptic_feedback";
    private static final String KEY_EMERGENCY_TONE = "emergency_tone";
    private static final String KEY_SOUND_SETTINGS = "sound_settings";
    //mtk68031 delete private static final String KEY_LOCK_SOUNDS = "lock_sounds";
    private static final String KEY_RINGTONE = "ringtone";
    //mtk68031 delete private static final String KEY_NOTIFICATION_SOUND = "notification_sound";
    private static final String KEY_CATEGORY_CALLS = "category_calls";

    private static final String SILENT_MODE_OFF = "off";
    private static final String SILENT_MODE_VIBRATE = "vibrate";
    private static final String SILENT_MODE_MUTE = "mute";

    private static final String[] NEED_VOICE_CAPABILITY = {
            KEY_RINGTONE, 
            //mtk68031 delete KEY_DTMF_TONE, 
            KEY_CATEGORY_CALLS,
            KEY_EMERGENCY_TONE
    };

    private static final int MSG_UPDATE_RINGTONE_SUMMARY = 1;
    private static final int MSG_UPDATE_NOTIFICATION_SUMMARY = 2;

    private CheckBoxPreference mVibrateOnRing;
    private ListPreference mSilentMode;
    //mtk68031 delete private CheckBoxPreference mDtmfTone;
    private CheckBoxPreference mSoundEffects;
    private CheckBoxPreference mHapticFeedback;
    private Preference mMusicFx;
    //mtk68031 delete private CheckBoxPreference mLockSounds;
    private Preference mRingtonePreference;
    //mtk68031 delete private Preference mNotificationPreference;
    private PreferenceScreen  mHighAudio;//高级

    private Runnable mRingtoneLookupRunnable;
    
    private mtksetting mmtksetting;
    private Context mContext;
    private Intent serviceIntent;
    private CheckBoxPreference mVolumeMute;
    private static final String KEY_VOLUME_MUTE = "volume_mute";
    SharedPreferences setting;
    private static final String VOLUME_VALUE = "volume_value";
    SharedPreferences.Editor editor;

    private AudioManager mAudioManager;
    private static final String KEY_AUDIO_HIGH = "high";
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                updateState(false);
            }
        }
    };
    
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mmtksetting = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            mmtksetting = binder.getService();
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_RINGTONE_SUMMARY:
                mRingtonePreference.setSummary((CharSequence) msg.obj);
                break;
            /* mtk68031 delete   
            case MSG_UPDATE_NOTIFICATION_SUMMARY:
                mNotificationPreference.setSummary((CharSequence) msg.obj);
                break;
            */
            }
        }
    };

    private PreferenceGroup mSoundSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getContentResolver();
        int activePhoneType = TelephonyManager.getDefault().getCurrentPhoneType();
        addPreferencesFromResource(R.xml.sound_settings);
        
        /** **/
        mContext = getActivity();
        serviceIntent = new Intent(mContext, mtksetting.class);
        mContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        
        setting = mContext.getSharedPreferences(Array.MTKSettings, mContext.MODE_MULTI_PROCESS);
        
        mVolumeMute = (CheckBoxPreference)findPreference(KEY_VOLUME_MUTE);
        int mVolumeValue = setting.getInt(VOLUME_VALUE, 0); 
        Switch_VolumeMute(mVolumeValue);

        //add judge by hct for klocwork
        if(mVolumeMute != null)
            {
                mVolumeMute.setOnPreferenceChangeListener(this);    
            }
        SharedPreferences uiState = mContext.getSharedPreferences(Array.MTKSettings, mContext.MODE_MULTI_PROCESS);
        editor = uiState.edit();
        /** **/

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (TelephonyManager.PHONE_TYPE_CDMA != activePhoneType) {
            // device is not CDMA, do not display CDMA emergency_tone
            getPreferenceScreen().removePreference(findPreference(KEY_EMERGENCY_TONE));
        }

        mSilentMode = (ListPreference) findPreference(KEY_SILENT_MODE);
        if (!getResources().getBoolean(R.bool.has_silent_mode)) {
            getPreferenceScreen().removePreference(mSilentMode);
            findPreference(KEY_RING_VOLUME).setDependency(null);
        } else {
            mSilentMode.setOnPreferenceChangeListener(this);
        }

        mVibrateOnRing = (CheckBoxPreference) findPreference(KEY_VIBRATE);
        Log.d("SoundSettings", "----------before--find KEY_VIBRATE--11111111111----");
        mVibrateOnRing.setOnPreferenceChangeListener(this);
        Log.d("SoundSettings", "----------after--find KEY_VIBRATE--22222222222----");
        
     // Begin: Add to select platform to use.
        mHighAudio=(PreferenceScreen)findPreference(KEY_AUDIO_HIGH);
        
        // End by vend_am00103
        
        /*mtk68031 delete
        mDtmfTone = (CheckBoxPreference) findPreference(KEY_DTMF_TONE);
        mDtmfTone.setPersistent(false);
        mDtmfTone.setChecked(Settings.System.getInt(resolver,
                Settings.System.DTMF_TONE_WHEN_DIALING, 1) != 0);
        */
        mSoundEffects = (CheckBoxPreference) findPreference(KEY_SOUND_EFFECTS);
        mSoundEffects.setPersistent(false);
        mSoundEffects.setChecked(Settings.System.getInt(resolver,
                Settings.System.SOUND_EFFECTS_ENABLED, 1) != 0);
        mHapticFeedback = (CheckBoxPreference) findPreference(KEY_HAPTIC_FEEDBACK);
        mHapticFeedback.setPersistent(false);
        mHapticFeedback.setChecked(Settings.System.getInt(resolver,
                Settings.System.HAPTIC_FEEDBACK_ENABLED, 1) != 0);

        /*mtk68031 delete
        mLockSounds = (CheckBoxPreference) findPreference(KEY_LOCK_SOUNDS);
        mLockSounds.setPersistent(false);
        mLockSounds.setChecked(Settings.System.getInt(resolver,
                Settings.System.LOCKSCREEN_SOUNDS_ENABLED, 1) != 0);
        */
        
        mRingtonePreference = findPreference(KEY_RINGTONE);
        //mtk68031 delete mNotificationPreference = findPreference(KEY_NOTIFICATION_SOUND);

        if (!((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator()) {
            getPreferenceScreen().removePreference(mVibrateOnRing);
            getPreferenceScreen().removePreference(mHapticFeedback);
        }

        if (TelephonyManager.PHONE_TYPE_CDMA == activePhoneType) {
            ListPreference emergencyTonePreference =
                (ListPreference) findPreference(KEY_EMERGENCY_TONE);
            emergencyTonePreference.setValue(String.valueOf(Settings.Global.getInt(
                resolver, Settings.Global.EMERGENCY_TONE, FALLBACK_EMERGENCY_TONE_VALUE)));
            emergencyTonePreference.setOnPreferenceChangeListener(this);
        }

        mSoundSettings = (PreferenceGroup) findPreference(KEY_SOUND_SETTINGS);

        mMusicFx = mSoundSettings.findPreference(KEY_MUSICFX);
        Intent i = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        PackageManager p = getPackageManager();
        List<ResolveInfo> ris = p.queryIntentActivities(i, PackageManager.GET_DISABLED_COMPONENTS);
        if (ris.size() <= 2) {
            // no need to show the item if there is no choice for the user to make
            // note: the built in musicfx panel has two activities (one being a
            // compatibility shim that launches either the other activity, or a
            // third party one), hence the check for <=2. If the implementation
            // of the compatbility layer changes, this check may need to be updated.
            mSoundSettings.removePreference(mMusicFx);
        }

        if (!Utils.isVoiceCapable(getActivity())) {
            for (String prefKey : NEED_VOICE_CAPABILITY) {
                Preference pref = findPreference(prefKey);
                if (pref != null) {
                    getPreferenceScreen().removePreference(pref);
                }
            }
        }

        mRingtoneLookupRunnable = new Runnable() {
            public void run() {
                if (mRingtonePreference != null) {
                    updateRingtoneName(RingtoneManager.TYPE_RINGTONE, mRingtonePreference,
                            MSG_UPDATE_RINGTONE_SUMMARY);
                }
                /* mtk68031 delete
                if (mNotificationPreference != null) {
                    updateRingtoneName(RingtoneManager.TYPE_NOTIFICATION, mNotificationPreference,
                            MSG_UPDATE_NOTIFICATION_SUMMARY);
                }*/
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        updateState(true);
        lookupRingtoneNames();

        IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(mReceiver);
    }

    /**
     * Put the audio system into the correct vibrate setting
     */
    private void setPhoneVibrateSettingValue(boolean vibeOnRing) {
        // If vibrate-on-ring is checked, use VIBRATE_SETTING_ON
        // Otherwise vibrate is off when ringer is silent
        int vibrateMode = vibeOnRing ? AudioManager.VIBRATE_SETTING_ON
                : AudioManager.VIBRATE_SETTING_ONLY_SILENT;
        mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, vibrateMode);
        mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, vibrateMode);
    }

    private void setPhoneSilentSettingValue(String value) {
        int ringerMode = AudioManager.RINGER_MODE_NORMAL;
        if (value.equals(SILENT_MODE_MUTE)) {
            ringerMode = AudioManager.RINGER_MODE_SILENT;
        } else if (value.equals(SILENT_MODE_VIBRATE)) {
            ringerMode = AudioManager.RINGER_MODE_VIBRATE;
        }
        mAudioManager.setRingerMode(ringerMode);
    }

    private String getPhoneSilentModeSettingValue() {
        switch (mAudioManager.getRingerMode()) {
        case AudioManager.RINGER_MODE_NORMAL:
            return SILENT_MODE_OFF;
        case AudioManager.RINGER_MODE_VIBRATE:
            return SILENT_MODE_VIBRATE;
        case AudioManager.RINGER_MODE_SILENT:
            return SILENT_MODE_MUTE;
        }
        // Shouldn't happen
        return SILENT_MODE_OFF;
    }

    // updateState in fact updates the UI to reflect the system state
    private void updateState(boolean force) {
        if (getActivity() == null) return;

        final int vibrateMode = mAudioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);

        mVibrateOnRing.setChecked(vibrateMode == AudioManager.VIBRATE_SETTING_ON);
        mSilentMode.setValue(getPhoneSilentModeSettingValue());

        mSilentMode.setSummary(mSilentMode.getEntry());

        //for ringer mode sync to  VolumeMute UI.
        //Add by mtk94088 for CNB00140472.
        int ringerMode =  mAudioManager.getRingerMode();
        if( ringerMode == AudioManager.RINGER_MODE_SILENT )
        {   Log.d("SoundSettings", "----ringerMode mVolumeMute: 1");
            mVolumeMute.setSummary(R.string.STR_SETTINGS_VOLUMEMUTE_ON);
            mVolumeMute.setChecked(true);
        }else{
        	Log.d("SoundSettings", "----ringerMode mVolumeMute: 0");
            mVolumeMute.setSummary(R.string.STR_SETTINGS_VOLUMEMUTE_OFF);
            mVolumeMute.setChecked(false);
        }
    }

    private void updateRingtoneName(int type, Preference preference, int msg) {
        if (preference == null) return;
        Context context = getActivity();
        if (context == null) return;
        Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, type);
        CharSequence summary = context.getString(com.android.internal.R.string.ringtone_unknown);
        // Is it a silent ringtone?
        if (ringtoneUri == null) {
            summary = context.getString(com.android.internal.R.string.ringtone_silent);
        } else {
            // Fetch the ringtone title from the media provider
            try {
                Cursor cursor = context.getContentResolver().query(ringtoneUri,
                        new String[] { MediaStore.Audio.Media.TITLE }, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        summary = cursor.getString(0);
                    }
                    cursor.close();
                }
            } catch (SQLiteException sqle) {
                // Unknown title for the ringtone
            }
        }
        mHandler.sendMessage(mHandler.obtainMessage(msg, summary));
    }

    private void lookupRingtoneNames() {
        new Thread(mRingtoneLookupRunnable).start();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if(preference==mHighAudio){
                Intent intent =new Intent("com.android.high");
                startActivity(intent);
            }
        
        /*mtk68031 delete
        if (preference == mDtmfTone) {
            Settings.System.putInt(getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING,
                    mDtmfTone.isChecked() ? 1 : 0);

        } else */
        if (preference == mSoundEffects) {
            int volumeValue = setting.getInt(VOLUME_VALUE, 0);
            // If the volume is not be muted, SoundEffects will be OK.
            if( volumeValue == 0 ){
                if (mSoundEffects.isChecked()) {
                    mAudioManager.loadSoundEffects();
                } else {
                    mAudioManager.unloadSoundEffects();
                }
            }
            Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED,
                    mSoundEffects.isChecked() ? 1 : 0);

        } else if (preference == mHapticFeedback) {
            Settings.System.putInt(getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED,
                    mHapticFeedback.isChecked() ? 1 : 0);

        /*mtk68031 delete } 
        else if (preference == mLockSounds) {
            Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_SOUNDS_ENABLED,
                    mLockSounds.isChecked() ? 1 : 0);
        */
        } else if (preference == mMusicFx) {
            // let the framework fire off the intent
            return false;
        }

        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (KEY_EMERGENCY_TONE.equals(key)) {
            try {
                int value = Integer.parseInt((String) objValue);
                Settings.System.putInt(getContentResolver(),
                        Settings.Global.EMERGENCY_TONE, value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist emergency tone setting", e);
            }
        } else if (preference == mVibrateOnRing) {
            setPhoneVibrateSettingValue((Boolean) objValue);
        } else if (preference == mSilentMode) {
            setPhoneSilentSettingValue(objValue.toString());
        } else if (preference == mVolumeMute) {
            //for ringer mode sync to  VolumeMute UI.
            //mRingerModeAffectedStreams can be configed in auidoservice.java;
            //Current mute effect stream:AudioManager.STREAM_MUSIC,AudioManager.STREAM_RING
            //AudioManager.STREAM_NOTIFICATION,AudioManager.STREAM_SYSTEM;
            //modfiy by mtk94088 for CNB00140472 begin:
            boolean flag = (Boolean)objValue;
            int muteTrue = 0;
            if( flag ){
                // set volume mute
                mVolumeMute.setSummary(R.string.STR_SETTINGS_VOLUMEMUTE_ON);
                /* following codes about soundEffiect is wrong !!!
                int soundEffict = Settings.System.getInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
                if( soundEffict == 1 ){
                    mAudioManager.unloadSoundEffects();
                    Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
                    mSoundEffects.setChecked(false);
                }
                */
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                muteTrue = 1;
            }else{

                mVolumeMute.setSummary(R.string.STR_SETTINGS_VOLUMEMUTE_OFF);
                /* following codes about soundEffiect is wrong !!!
                int soundEffict = Settings.System.getInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
                if( soundEffict == 0 ){
                    mAudioManager.loadSoundEffects();
                    Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
                    mSoundEffects.setChecked(true);
                }
                */
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                muteTrue = 0;
            }
            //config mute the mtk audio hardware;
            Log.d("SoundSettings", "----muteTrue: "+muteTrue);
            mmtksetting.SetMute(muteTrue);
            //modfiy by mtk94088 for CNB00140472 end.
           
            editor.putInt(VOLUME_VALUE, muteTrue);
            editor.commit();
        }

        return true;
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mContext.unbindService(serviceConnection);
    }
    
    private void Switch_VolumeMute(int value){
        switch(value){
        case 1:
            Log.d("SoundSettings", "----wujiangbo value1 == 1------");
            mVolumeMute.setSummary(R.string.STR_SETTINGS_VOLUMEMUTE_ON);
            mVolumeMute.setChecked(true);
            break;
        case 0:
            Log.d("SoundSettings", "----wujiangbo value1 == 0------");
            mVolumeMute.setSummary(R.string.STR_SETTINGS_VOLUMEMUTE_OFF);
            mVolumeMute.setChecked(false);
            break;
        }
    }
}
