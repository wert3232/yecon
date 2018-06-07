package com.android.settings;

import com.android.settings.R;
import com.android.settings.mtksetting.MyBinder;
import com.mtksetting.Array;

import android.app.DownloadManager.Request;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SpeakerLayoutSettings extends PreferenceActivity implements
        OnPreferenceChangeListener {
    private ListPreference mChannelDownMix;
    private ListPreference mfrontspeaker;
    private ListPreference mcenterspeaker;
    private ListPreference msurroundspeaker;
    private ListPreference mtestspeaker;
    private CheckBoxPreference msubwooferspeaker;
    private PreferenceScreen mChannelDelay;
    private final String KEY_CHANNELDOWNMIX_SETTING = "downmix";
    private final String KEY_FRONTSPEAKER_SETTING = "frontspeaker";
    private final String KEY_CENTERSPEAKER_SETTING = "centerspeaker";
    private final String KEY_SURROUNDSPEAKER_SETTING = "surroundspeaker";
    private final String KEY_TESTSPEAKER_SETTING = "testspeaker";
    private final String KEY_SUBWOOFERSPEAKER_SETTING = "subwooferspeaker";
    private final String KEY_CHANNELDELAY = "channeldelay";
    private final String SPEAKERSLAYOUTTYPE = "speakerslayouttype_key";
    private final String SPEAKERSLAYOUTSIZE = "speakerslayoutsize_key";
    private final String CHANNELDOWNMIX_ON = "channeldowmmixon_key";
    private final String FRONTSPEAKER = "frontspeaker_key";
    private final String CENTERSPEAKER_ON = "centerspeakeron_key";
    private final String SURROUNDSPEAKER_ON = "surroundon_key";
    private final String TESTSPEAKER = "testspeaker_key";
    private final String SUBWOOFERSPEAKER = "subwooferspeaker_key";
    private final String CENTERAPEAKER_STATE = "centerspeaker_state";
    private final String SURROUND_STATE = "surround_state";
    private final String DOWNMIX_STATE = "domnmix_state";

    private int mSpeakLayoutType = 0;
    private int mSpeakLayoutSize = 0;
    private mtksetting mmtksetting;
    private Intent serviceIntent;
    private int DownMix;
    private int subwoofer;
    private int frontspeaker;
    private int centerspeaker;
    private int surroundspeaker;
    private int mtestspeaker_state;
    SharedPreferences.Editor editor;
    SharedPreferences setting;
    int SPEAKER_LAYOUT_LR = 0x0001 << 0;
    int SPEAKER_LAYOUT_LRC = 0x0001 << 3;
    int SPEAKER_LAYOUT_LRLSRS = 0x0001 << 6;
    int SPEAKER_LAYOUT_LRCLSRS = 0x0001 << 7;
    int SPEAKER_LAYOUT_SUBWOOFER = 0x0001 << 8;
    int SPEAKER_LAYOUT_C_LARGE = 0x0001 << 0;
    int SPEAKER_LAYOUT_L_LARGE = 0x0001 << 1;
    int SPEAKER_LAYOUT_R_LARGE = 0x0001 << 2;
    int SPEAKER_LAYOUT_LS_LARGE = 0x0001 << 3;
    int SPEAKER_LAYOUT_RS_LARGE = 0x0001 << 4;
    int SPEAKER_LAYOUT_STEREO = 0x0001 << 2;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mmtksetting = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            mmtksetting = binder.getService();
            //Begin : Added by vend00102 2012.07.27
            //mmtksetting.GOpenDVPSet();
            //End : Added by vend00102 2012.07.27
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.loudspeaker_setting);
        SharedPreferences uiState = getSharedPreferences(Array.MTKSettings,
                this.MODE_PRIVATE);
        editor = uiState.edit();

        setting = getSharedPreferences(Array.MTKSettings, this.MODE_PRIVATE);

        serviceIntent = new Intent(this, mtksetting.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        mChannelDownMix = (ListPreference) findPreference(KEY_CHANNELDOWNMIX_SETTING);
        mChannelDownMix.setOnPreferenceChangeListener(this);

        mfrontspeaker = (ListPreference) findPreference(KEY_FRONTSPEAKER_SETTING);
        mfrontspeaker.setOnPreferenceChangeListener(this);

        mcenterspeaker = (ListPreference) findPreference(KEY_CENTERSPEAKER_SETTING);
        mcenterspeaker.setOnPreferenceChangeListener(this);

        msurroundspeaker = (ListPreference) findPreference(KEY_SURROUNDSPEAKER_SETTING);
        msurroundspeaker.setOnPreferenceChangeListener(this);

        mtestspeaker = (ListPreference) findPreference(KEY_TESTSPEAKER_SETTING);
        mtestspeaker.setOnPreferenceChangeListener(this);

        msubwooferspeaker = (CheckBoxPreference) findPreference(KEY_SUBWOOFERSPEAKER_SETTING);
        msubwooferspeaker.setOnPreferenceChangeListener(this);

        mChannelDelay = (PreferenceScreen)findPreference(KEY_CHANNELDELAY);
        mChannelDelay.setEnabled(false);

        init();
    }

    @Override
    public boolean onPreferenceChange(Preference arg0, Object arg1) {

        if ((arg0.getKey()).equals(KEY_CHANNELDOWNMIX_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            DownMix = value;
            switch (value) {
            case 0:
                mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_LTRT);
                mcenterspeaker.setSummary(R.string.STR_SETTINGS_CENTER_OFF);
                msurroundspeaker.setSummary(R.string.STR_SETTINGS_REAR_OFF);
                editor.putInt(CHANNELDOWNMIX_ON, 0);
                editor.putInt(CENTERAPEAKER_STATE, 2);
                editor.putInt(SURROUND_STATE, 2);
                editor.putInt(DOWNMIX_STATE, 0);
                editor.commit();
                break;
            case 1:
                mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_STER);
                mcenterspeaker.setSummary(R.string.STR_SETTINGS_CENTER_OFF);
                msurroundspeaker.setSummary(R.string.STR_SETTINGS_REAR_OFF);
                editor.putInt(CHANNELDOWNMIX_ON, 1);
                editor.putInt(CENTERAPEAKER_STATE, 2);
                editor.putInt(SURROUND_STATE, 2);
                editor.putInt(DOWNMIX_STATE, 1);
                editor.commit();
                break;
            case 2:
                mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_NONE);
                int centerstate = setting.getInt(CENTERSPEAKER_ON, 0);
                if (centerstate == 0) {
                    mcenterspeaker
                            .setSummary(R.string.STR_SETTINGS_CENTER_LARGE);
                } else {
                    mcenterspeaker
                            .setSummary(R.string.STR_SETTINGS_CENTER_SMALL);
                }
                editor.putInt(CENTERAPEAKER_STATE, centerstate);
                int surroundstate = setting.getInt(SURROUNDSPEAKER_ON, 0);
                if (surroundstate == 0) {
                    msurroundspeaker
                            .setSummary(R.string.STR_SETTINGS_CENTER_LARGE);
                } else {
                    msurroundspeaker
                            .setSummary(R.string.STR_SETTINGS_CENTER_SMALL);
                }
                editor.putInt(SURROUND_STATE, surroundstate);
                editor.putInt(DOWNMIX_STATE, 2);
                editor.commit();
                break;
            }
            getDownMixData();
            mmtksetting.SetSpeakerLayout(mSpeakLayoutType, mSpeakLayoutSize);
            //Begin : Added by vend00102 2012.07.27
            //mmtksetting. DVP_GSetSpeakerLayout(mSpeakLayoutType, mSpeakLayoutSize);
            //End : Added by vend00102 2012.07.27
            editor.putInt(SPEAKERSLAYOUTTYPE, mSpeakLayoutType);
            editor.putInt(SPEAKERSLAYOUTSIZE, mSpeakLayoutSize);
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_FRONTSPEAKER_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            switch (value) {
            case 0:
                mfrontspeaker.setSummary(R.string.STR_SETTINGS_FRONT_LARGE);
                editor.putInt(FRONTSPEAKER, 0);
                break;
            case 1:
                mfrontspeaker.setSummary(R.string.STR_SETTINGS_FRONT_SMALL);
                editor.putInt(FRONTSPEAKER, 1);
                break;
            }
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_CENTERSPEAKER_SETTING)) {
            int value = Integer.parseInt(arg1.toString().trim());
            centerspeaker = value;
            switch (value) {
            case 0:
                mcenterspeaker.setSummary(R.string.STR_SETTINGS_CENTER_LARGE);
                mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_NONE);
                editor.putInt(CENTERSPEAKER_ON, 0);
                editor.putInt(DOWNMIX_STATE, 2);
                editor.commit();
                break;
            case 1:
                mcenterspeaker.setSummary(R.string.STR_SETTINGS_CENTER_SMALL);
                mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_NONE);
                editor.putInt(CENTERSPEAKER_ON, 1);
                editor.putInt(DOWNMIX_STATE, 2);
                editor.commit();
                break;
            case 2:
                mcenterspeaker.setSummary(R.string.STR_SETTINGS_CENTER_OFF);
                int surroundstate = setting.getInt(SURROUND_STATE, 0);
                if (surroundstate == 0 || surroundstate == 1) {
                    mChannelDownMix
                            .setSummary(R.string.STR_SETTINGS_DOWNMIX_NONE);
                    editor.putInt(DOWNMIX_STATE, 2);
                    editor.commit();
                } else {
                    int Downmixstate = setting.getInt(CHANNELDOWNMIX_ON, 0);
                    if (Downmixstate == 0) {
                        mChannelDownMix
                                .setSummary(R.string.STR_SETTINGS_DOWNMIX_LTRT);
                        editor.putInt(DOWNMIX_STATE, 0);
                        editor.commit();
                    } else {
                        mChannelDownMix
                                .setSummary(R.string.STR_SETTINGS_DOWNMIX_STER);
                        editor.putInt(DOWNMIX_STATE, 1);
                        editor.commit();
                    }
                }
                
                break;

            }
            editor.putInt(CENTERAPEAKER_STATE, centerspeaker);
            editor.putInt("initadvavce", 0);
            editor.putString("speakerenable", "speakerenable");
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_SURROUNDSPEAKER_SETTING)) {
            int value = Integer.parseInt(arg1.toString().trim());
            surroundspeaker = value;
            switch (value) {
            case 0:
                msurroundspeaker.setSummary(R.string.STR_SETTINGS_REAR_LARGE);
                mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_NONE);
                editor.putInt(SURROUNDSPEAKER_ON, 0);
                editor.putInt(DOWNMIX_STATE, 2);
                editor.commit();
                break;
            case 1:
                msurroundspeaker.setSummary(R.string.STR_SETTINGS_REAR_SMALL);
                mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_NONE);
                editor.putInt(SURROUNDSPEAKER_ON, 1);
                editor.putInt(DOWNMIX_STATE, 2);
                editor.commit();
                break;
            case 2:
                msurroundspeaker.setSummary(R.string.STR_SETTINGS_REAR_OFF);
                int centerstate = setting.getInt(CENTERAPEAKER_STATE, 0);
                if (centerstate == 0 || centerstate == 1) {
                    mChannelDownMix
                            .setSummary(R.string.STR_SETTINGS_DOWNMIX_NONE);
                    editor.putInt(DOWNMIX_STATE, 2);
                    editor.commit();
                } else {
                    int Downmixstate = setting.getInt(CHANNELDOWNMIX_ON, 0);
                    if (Downmixstate == 0) {
                        mChannelDownMix
                                .setSummary(R.string.STR_SETTINGS_DOWNMIX_LTRT);
                        editor.putInt(DOWNMIX_STATE, 0);
                        editor.commit();
                    } else {
                        mChannelDownMix
                                .setSummary(R.string.STR_SETTINGS_DOWNMIX_STER);
                        editor.putInt(DOWNMIX_STATE, 1);
                        editor.commit();
                    }
                }
                editor.commit();
                break;
            }
            editor.putInt(SURROUND_STATE, surroundspeaker);
            editor.putInt("initadvavce", 0);
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_SUBWOOFERSPEAKER_SETTING)) {
            boolean flag = (Boolean) arg1;
            int Switch;
            if (flag == false) {
                Switch = 0;
                msubwooferspeaker.setChecked(false);
                msubwooferspeaker.setSummary(R.string.STR_SETTINGS_SUBWOOFER_OFF);
            } else {
                Switch = 1;
                msubwooferspeaker.setChecked(true);
                msubwooferspeaker.setSummary(R.string.STR_SETTINGS_SUBWOOFER_ON);

            }
            subwoofer = Switch;
            editor.putInt(SUBWOOFERSPEAKER, Switch);
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_TESTSPEAKER_SETTING)) {
            int value = Integer.parseInt(arg1.toString().trim());
            int TestToneType = 0;
            switch (value) {
            case 0:
                mtestspeaker_state = 0;
                mtestspeaker.setSummary(R.string.STR_SETTINGS_TESTTONE_FRONT);
                TestToneType = 0;
                break;
            case 1:
                mtestspeaker_state = 1;
                mtestspeaker.setSummary(R.string.STR_SETTINGS_TESTTONE_REAR);
                TestToneType = 2;
                break;
            case 2:
                mtestspeaker_state = 2;
                mtestspeaker.setSummary(R.string.STR_SETTINGS_TESTTONE_OFF);
                break;
            }
            if (2 != value)
            {
                mmtksetting.SetTestTone(2, 0);
            }
            mmtksetting.SetTestTone(value, TestToneType);
            editor.putInt(TESTSPEAKER, value);
            editor.commit();
        }
        if ((arg0.getKey()).equals(KEY_FRONTSPEAKER_SETTING)
                || (arg0.getKey()).equals(KEY_CENTERSPEAKER_SETTING)
                || (arg0.getKey()).equals(KEY_SURROUNDSPEAKER_SETTING)||
                (arg0.getKey()).equals(KEY_SUBWOOFERSPEAKER_SETTING)) {
            Log.d("tag","msg--------------------wuxin00000000000000");
            
            getSpeakerData();
            mmtksetting.SetSpeakerLayout(mSpeakLayoutType, mSpeakLayoutSize);
            editor.putInt(SPEAKERSLAYOUTTYPE, mSpeakLayoutType);
            editor.putInt(SPEAKERSLAYOUTSIZE, mSpeakLayoutSize);
            editor.commit();
            Log.d("MTKSetting", "SetDownMix=="+DownMix+" ----front " + frontspeaker
                    + ", center " + centerspeaker + ", surround "
                    + surroundspeaker);
        }

        return true;
    }

    public void getDownMixData() {
        
        int DownMix=setting.getInt(DOWNMIX_STATE, 0);
        int frontspeaker=setting.getInt(FRONTSPEAKER, 0);
        int centerspeaker=setting.getInt(CENTERAPEAKER_STATE, 0);
        int surroundspeaker=setting.getInt(SURROUND_STATE, 0);
        int subwoofer=setting.getInt(SUBWOOFERSPEAKER, 0);
        Log.d("MTKSetting", "DownMixState " + DownMix
                + "SetDownMix front " + frontspeaker + ", center "
                + centerspeaker + ", surround " + surroundspeaker);
        if (DownMix == 0 || DownMix == 1) {
            if (DownMix == 0) {
                mSpeakLayoutType = SPEAKER_LAYOUT_LR;
                if (frontspeaker == 0)
                    mSpeakLayoutSize = SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE;
                else
                    mSpeakLayoutSize = 0;
            } else if (DownMix == 1) {
                mSpeakLayoutType = SPEAKER_LAYOUT_STEREO;
                if (frontspeaker == 0)
                    mSpeakLayoutSize = SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE;
                else
                    mSpeakLayoutSize = 0;
            }

            if (subwoofer == 1)
                mSpeakLayoutType |= SPEAKER_LAYOUT_SUBWOOFER;

        } else  {
            Log.d("tag","msg------------------wuxin------mixoff");
            if (frontspeaker == 0)
                mSpeakLayoutSize = SPEAKER_LAYOUT_L_LARGE
                        | SPEAKER_LAYOUT_R_LARGE;
            else
                mSpeakLayoutSize = 0;
            if (centerspeaker == 0 || centerspeaker == 1) {
                if (surroundspeaker == 0 || surroundspeaker == 1) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRCLSRS;
                    if (surroundspeaker == 0)
                        mSpeakLayoutSize |= SPEAKER_LAYOUT_LS_LARGE
                                | SPEAKER_LAYOUT_RS_LARGE;
                    if (centerspeaker == 0) {
                        mSpeakLayoutSize |= SPEAKER_LAYOUT_C_LARGE;
                    }
                } else {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRC;
                    if (centerspeaker == 0)
                        mSpeakLayoutSize |= SPEAKER_LAYOUT_C_LARGE;
                }
            } else if (surroundspeaker == 0
                    || surroundspeaker == 1) {
                mSpeakLayoutType = SPEAKER_LAYOUT_LRLSRS;
                if (surroundspeaker == 0)
                    mSpeakLayoutSize |= SPEAKER_LAYOUT_LS_LARGE
                            | SPEAKER_LAYOUT_RS_LARGE;
            } else
                mSpeakLayoutType = SPEAKER_LAYOUT_LR;

            if (subwoofer == 1)
                mSpeakLayoutType |= SPEAKER_LAYOUT_SUBWOOFER;
        
        }
    }

    public void getSpeakerData() {
    
        int frontspeaker=setting.getInt(FRONTSPEAKER, 0);
        int centerspeaker=setting.getInt(CENTERAPEAKER_STATE, 0);
        int surroundspeaker=setting.getInt(SURROUND_STATE, 0);
        int subwoofer=setting.getInt(SUBWOOFERSPEAKER, 0);
        Log.d("tag-getSpeakerData", "mfrontspeaker_state " + frontspeaker +
                "mcenterspeaker_state " + centerspeaker + 
                "msurroundspeaker_state" + surroundspeaker);
        if (frontspeaker == 0) {
            if (centerspeaker == 0) {
                if (surroundspeaker == 0) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRCLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_LS_LARGE
                            | SPEAKER_LAYOUT_RS_LARGE | SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE | SPEAKER_LAYOUT_C_LARGE;
                } else if (surroundspeaker == 1) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRCLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE | SPEAKER_LAYOUT_C_LARGE;

                } else {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRC;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE | SPEAKER_LAYOUT_C_LARGE;

                }
            } else if (centerspeaker == 1) {
                if (surroundspeaker == 0) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRCLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_LS_LARGE
                            | SPEAKER_LAYOUT_RS_LARGE | SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE;
                } else if (surroundspeaker == 1) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRCLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE;
                } else {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRC;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE;
                }
            } else {
                if (surroundspeaker == 0) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_LS_LARGE
                            | SPEAKER_LAYOUT_RS_LARGE | SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE;
                } else if (surroundspeaker == 1) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE;
                } else {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LR;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_L_LARGE
                            | SPEAKER_LAYOUT_R_LARGE;
                }

            }
        } else {
            if (centerspeaker == 0) {
                if (surroundspeaker == 0) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRCLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_LS_LARGE
                            | SPEAKER_LAYOUT_RS_LARGE | SPEAKER_LAYOUT_C_LARGE;
                } else if (surroundspeaker == 1) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRCLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_C_LARGE;

                } else {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRC;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_C_LARGE;
                }
            } else if (centerspeaker == 1) {
                if (surroundspeaker == 0) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRCLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_LS_LARGE
                            | SPEAKER_LAYOUT_RS_LARGE;
                } else if (surroundspeaker == 1) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRCLSRS;
                    mSpeakLayoutSize = 0;

                } else {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRC;
                    mSpeakLayoutSize = 0;

                }
            } else {
                if (surroundspeaker == 0) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRLSRS;
                    mSpeakLayoutSize = SPEAKER_LAYOUT_LS_LARGE
                            | SPEAKER_LAYOUT_RS_LARGE;
                } else if (surroundspeaker == 1) {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LRLSRS;
                    mSpeakLayoutSize = 0;

                } else {
                    mSpeakLayoutType = SPEAKER_LAYOUT_LR;
                    mSpeakLayoutSize = 0;

                }

            }

        }
        if (subwoofer == 1)
            mSpeakLayoutType |= SPEAKER_LAYOUT_SUBWOOFER;
    }

    public void init() {
        
        String init=setting.getString("init","");
        if(init.equals("")){
            editor.putInt(DOWNMIX_STATE, 2);
            editor.putInt(FRONTSPEAKER, 0);
            editor.putInt(CENTERAPEAKER_STATE, 2);
            editor.putInt(SURROUND_STATE, 2);
            editor.putInt(SUBWOOFERSPEAKER, 0);
            editor.commit();
        }
        editor.putInt(TESTSPEAKER, 2);
        editor.commit();
        int DownMIX = setting.getInt(DOWNMIX_STATE, 0);
        int FrontSpeaker = setting.getInt(FRONTSPEAKER, 0);
        int centerspeaker = setting.getInt(CENTERAPEAKER_STATE, 0);
        int surroundspeaker = setting.getInt(SURROUND_STATE, 0);
        int subwoofer = setting.getInt(SUBWOOFERSPEAKER, 0);
        int testspeaker = setting.getInt(TESTSPEAKER, 0);
        switch_downmix(DownMIX);
        if (FrontSpeaker == 0) {
            mfrontspeaker.setSummary(R.string.STR_SETTINGS_FRONT_LARGE);
            mfrontspeaker.setValueIndex(0);
        } else {
            mfrontspeaker.setSummary(R.string.STR_SETTINGS_FRONT_SMALL);
            mfrontspeaker.setValueIndex(1);
        }
        switch_center(centerspeaker);
        switch_testtone(testspeaker);
        switch_surround(surroundspeaker);
        if (subwoofer == 0) {
            msubwooferspeaker.setChecked(false);
            msubwooferspeaker.setSummary(R.string.STR_SETTINGS_SUBWOOFER_OFF);
        } else {
            msubwooferspeaker.setChecked(true);
            msubwooferspeaker.setSummary(R.string.STR_SETTINGS_SUBWOOFER_ON);
        }
    }

    public void switch_surround(int value) {
        switch (value) {
        case 0:
            msurroundspeaker.setSummary(R.string.STR_SETTINGS_REAR_LARGE);
            break;
        case 1:
            msurroundspeaker.setSummary(R.string.STR_SETTINGS_REAR_SMALL);
            break;
        case 2:
            msurroundspeaker.setSummary(R.string.STR_SETTINGS_REAR_OFF);
            break;
        }
        msurroundspeaker.setValueIndex(value);
    }

    public void switch_center(int value) {
        switch (value) {
        case 0:
            mcenterspeaker.setSummary(R.string.STR_SETTINGS_CENTER_LARGE);
            break;
        case 1:
            mcenterspeaker.setSummary(R.string.STR_SETTINGS_CENTER_SMALL);
            break;
        case 2:
            mcenterspeaker.setSummary(R.string.STR_SETTINGS_CENTER_OFF);
            break;
        }
        mcenterspeaker.setValueIndex(value);
    }

    public void switch_testtone(int value) {
        switch (value) {
        case 0:
            mtestspeaker.setSummary(R.string.STR_SETTINGS_TESTTONE_FRONT);
            break;

        case 1:
            mtestspeaker.setSummary(R.string.STR_SETTINGS_TESTTONE_REAR);
            break;

        case 2:
            mtestspeaker.setSummary(R.string.STR_SETTINGS_TESTTONE_OFF);
            break;

        default:
            return;
        }
        mtestspeaker.setValueIndex(value);
    }
    

    public void switch_downmix(int value) {
        switch (value) {
        case 0:
            mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_LTRT);
            break;
        case 1:
            mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_STER);
            break;
        case 2:
            mChannelDownMix.setSummary(R.string.STR_SETTINGS_DOWNMIX_NONE);
            break;
        }
        mChannelDownMix.setValueIndex(value);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        int TestToneType = 0;
        int testspeaker = setting.getInt(TESTSPEAKER, 0);
        switch_testtone(testspeaker);
        
        switch (testspeaker) {
        case 0:
            TestToneType = 0;
            break;
        
        case 1:
            TestToneType = 2;
            break;

        case 2:
            break;
        }
        if (null != mmtksetting) {
            if (2 != testspeaker) {
                mmtksetting.SetTestTone(2, 0);
            }
            mmtksetting.SetTestTone(testspeaker, TestToneType);
        }       
    }

    @Override
    protected void onPause() {
        super.onPause();
        mmtksetting.SetTestTone(2, 0);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        editor.putInt(TESTSPEAKER, 2);
        editor.commit();
        mmtksetting.SetTestTone(2, 0);
        super.onDestroy();
        unbindService(serviceConnection);
        //Begin : Added by vend00102 2012.07.27
        //mmtksetting.GCloseDVPSet();
        //End : Added by vend00102
    }
}
