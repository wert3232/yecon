package com.android.settings;

import com.android.settings.SpeakerLayoutSettings;
import com.android.settings.R;
import com.android.settings.SrsSettings;
import com.android.settings.mtksetting.MyBinder;
import com.mtksetting.Array;

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
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

public class AdvavcedAudioSettings extends PreferenceActivity implements
        OnPreferenceChangeListener {
    private final String Key_SRS_SETTING = "srs";
    private final String Key_LOUDSPEAKER_SETTING = "speaker";
    private PreferenceScreen mSrs;
    private PreferenceScreen mspeaker;
    private ListPreference mequilibria;
    private ListPreference mreverb;
    private ListPreference mpl2;
    private CheckBoxPreference mMix;

    private ListPreference mspdif;
    private final String KEY_REVERB_SETTING = "reverb";
    private final String REVERB = "reverb_key";
    private final String KEY_EQUILBRIA_SETTING = "equilibria";
    private final String EQUILBRIA = "eq_key";
    private final String KEY_PL2_SETTING = "pl2";
    private final String PL2 = "pl2_key";
    private final String KEY_MIX_SETTING = "mix";
    private final String MIX = "mix_key";
    private final String KEY_SPDIF_SETTING = "spdif";
    private final String SPDIF = "spdif_key";
    private mtksetting mmtksetting;
    private Intent serviceIntent;
    SharedPreferences.Editor editor;
    SharedPreferences setting;

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
           // mmtksetting.GOpenDVPSet();
            //End : Added by vend00102 2012.07.27
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.advancedaudio_setting);
        setting = getSharedPreferences(Array.MTKSettings, this.MODE_PRIVATE);

        SharedPreferences uiState = getSharedPreferences(Array.MTKSettings,
                getApplication().MODE_PRIVATE);
        editor = uiState.edit();
        serviceIntent = new Intent(this, mtksetting.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        mequilibria = (ListPreference) findPreference(KEY_EQUILBRIA_SETTING);
        int eq = setting.getInt(EQUILBRIA, 0);
        Switch_EQtype(eq);
        mequilibria.setOnPreferenceChangeListener(this);

        mreverb = (ListPreference) findPreference(KEY_REVERB_SETTING);
        int reverb = setting.getInt(REVERB, 0);
        Switch_Reverb(reverb);
        mreverb.setOnPreferenceChangeListener(this);

        mpl2 = (ListPreference) findPreference(KEY_PL2_SETTING);
        int pl2 = setting.getInt(PL2, 0);

        mpl2.setOnPreferenceChangeListener(this);

        mMix = (CheckBoxPreference) findPreference(KEY_MIX_SETTING);
        int mix = setting.getInt(MIX, 0);
        Switch_Mix(mix);
        mMix.setOnPreferenceChangeListener(this);

        mspdif = (ListPreference) findPreference(KEY_SPDIF_SETTING);
        int spdif = setting.getInt(SPDIF, 0);
        Switch_Spdif(spdif);
        mspdif.setOnPreferenceChangeListener(this);
        mSrs = (PreferenceScreen) findPreference(Key_SRS_SETTING);
        mspeaker = (PreferenceScreen) findPreference(Key_LOUDSPEAKER_SETTING);
        Switch_PL2(pl2);
/*      String advanvced = setting.getString("advanced", "");
        if (advanvced.equals("")) {
            mpl2.setEnabled(false);
            mMix.setEnabled(false);
            mSrs.setEnabled(false);
        }else if(setting.getString("srsenable", "").equals("")&&setting.getString("speakerenable","").equals("")){
            mpl2.setEnabled(false);
            mMix.setEnabled(false);
            mSrs.setEnabled(false);
        } else {
            int initadavce = setting.getInt("srsswitch_key", 0);
            if (initadavce == 0) {

                double mspeakerSize = 2;
                int centerspeak = setting.getInt("centerspeaker_state", 0);
                int surroundspeak = setting.getInt("surround_state", 0);
                if (centerspeak == 2) {
                } else {
                    mspeakerSize += 2;
                }

                if (surroundspeak == 2) {
                    //mMix.setEnabled(false);
                } else {
                    mspeakerSize += 2;
                    //mMix.setEnabled(true);

                }
                if (mspeakerSize >= 4) {
                    mpl2.setEnabled(true);
                    if( pl2 != 0 )
                        mSrs.setEnabled(false);
                    else
                        mSrs.setEnabled(true);
                    //mSrs.setEnabled(true);
                    mMix.setEnabled(true);
                } else {
                    mSrs.setEnabled(false);
                    mpl2.setEnabled(false);
                    mMix.setEnabled(false);
                }

            } else if (initadavce == 1) {
                mpl2.setEnabled(false);
                int Srsstate = setting.getInt("srsswitch_key", 0);
                String initsrs = setting.getString("srsinit", "");
                if (initsrs.equals("")) {
                } else {
                    if (Srsstate == 0) {
                        mpl2.setEnabled(true);
                    } else if (Srsstate == 1) {
                        mpl2.setEnabled(false);
                    }
                }

            }

        }*/

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

        Log.d("AdvanceAudioSettings", "mspeakerSize: " + mspeakerSize);
        
        // Set the UpMix.
        if( mspeakerSize >= 4 )
            mMix.setEnabled(true);
        else
            mMix.setEnabled(false);

        // Set the PL2 and SRS.
        int enableSrs = setting.getInt("Share_SRS", 0);
        int enablePl2 = setting.getInt("Share_PL2", 0);
        if( enablePl2 == 0 && enableSrs == 0 )
        {
            Log.d("AdvanceAudioSettings","Set mSrs and mPl2");
            if( mspeakerSize > 2 )
                mpl2.setEnabled(true);
            else
                mpl2.setEnabled(false);
            if( mspeakerSize > 5 )
                mSrs.setEnabled(true);
            else
                mSrs.setEnabled(false);
        }
        else if( enablePl2 == 0 && enableSrs == 1 )
        {
            Log.d("AdvanceAudioSettings","Set mSrs");
            if( mspeakerSize > 5 )
                mSrs.setEnabled(true);
            else
                mSrs.setEnabled(false);
            mpl2.setEnabled(false);
        }
        else if( enablePl2 == 1 && enableSrs == 0 )
        {
            Log.d("AdvanceAudioSettings","Set Pl2");
            mSrs.setEnabled(false);
            if( mspeakerSize > 2 )
                mpl2.setEnabled(true);
            else
                mpl2.setEnabled(false);
        }

    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        // getspeaker();
/*      int initadavce = setting.getInt("initadvavce", 0);
        if (initadavce == 0) {
            double mspeakerSize = 2;
            int centerspeak = setting.getInt("centerspeaker_state", 0);
            int surroundspeak = setting.getInt("surround_state", 0);
            if (centerspeak == 2) {
            } else {
                mspeakerSize += 2;
            }

            if (surroundspeak == 2) {
                mMix.setEnabled(false);
            } else {
                mspeakerSize += 2;
                mMix.setEnabled(true);

            }
            if (mspeakerSize >= 4) {
                mpl2.setEnabled(true);
                mSrs.setEnabled(true);
            } else {
                mSrs.setEnabled(false);
                mpl2.setEnabled(false);
            }
        } else if (initadavce == 1) {
            int Srsstate = setting.getInt("srsswitch_key", 0);
            String initsrs = setting.getString("srsinit", "");

            if (initsrs.equals("")) {
            } else {
                if (Srsstate == 0) {
                    mpl2.setEnabled(true);
                } else if (Srsstate == 1) {
                    mpl2.setEnabled(false);
                }
            }
        }*/
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

        Log.d("AdvanceAudioSettings", "mspeakerSize: " + mspeakerSize);
        // Set the UpMix.
        if( mspeakerSize >= 4 )
            mMix.setEnabled(true);
        else
            mMix.setEnabled(false);

        // Set the PL2 and SRS.
        int enableSrs = setting.getInt("Share_SRS", 0);
        int enablePl2 = setting.getInt("Share_PL2", 0);
        if( enablePl2 == 0 && enableSrs == 0 )
        {
            Log.d("AdvanceAudioSettings","Set mSrs and mPl2");
            if( mspeakerSize > 2 )
                mpl2.setEnabled(true);
            else
                mpl2.setEnabled(false);
            if( mspeakerSize > 5 )
                mSrs.setEnabled(true);
            else
                mSrs.setEnabled(false);
        }
        else if( enablePl2 == 0 && enableSrs == 1 )
        {
            Log.d("AdvanceAudioSettings","Set mSrs");
            if( mspeakerSize > 5 )
                mSrs.setEnabled(true);
            else
                mSrs.setEnabled(false);
            mpl2.setEnabled(false);
        }
        else if( enablePl2 == 1 && enableSrs == 0 )
        {
            Log.d("AdvanceAudioSettings","Set Pl2");
            mSrs.setEnabled(false);
            if( mspeakerSize > 2 )
                mpl2.setEnabled(true);
            else
                mpl2.setEnabled(false);
        }
    }

    public void getspeaker() {
        double mspeakerSize = 2;
        int mchannel = 2;
        int centerspeak = setting.getInt("centerspeaker_state", 0);
        int surroundspeak = setting.getInt("surround_state", 0);
        int subwooferspeak = setting.getInt("subwooferspeaker_key", 0);
        String init = setting.getString("init", "");
        if (init.equals("")) {
            centerspeak = 2;
            surroundspeak = 2;
            subwooferspeak = 0;
        }

        if (centerspeak == 2) {
        } else {
            mspeakerSize += 2;
            mchannel += 2;
        }

        if (surroundspeak == 2) {
            mMix.setEnabled(false);
        } else {
            mspeakerSize += 2;
            mchannel += 2;
            mMix.setEnabled(true);

        }

        if (subwooferspeak == 1) {
            mchannel += 0.1;
        }
        if (mspeakerSize >= 4) {
            mpl2.setEnabled(true);
            mSrs.setEnabled(true);
        } else {
            mSrs.setEnabled(false);
            mpl2.setEnabled(false);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mSrs) {
            Intent intent = new Intent("com.android.srs");
            //intent.setClass(AdvavcedAudioSettings.this, SrsSettings.class);
            startActivity(intent);
        }
        if (preference == mspeaker) {
            Intent intent = new Intent("com.android.speaker");
//          intent.setClass(AdvavcedAudioSettings.this,
//                  SpeakerLayoutSettings.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        // TODO Auto-generated method stub
        if ((arg0.getKey()).equals(KEY_EQUILBRIA_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            if (value > 6) {
                value = 0;
            } else if (value < 0) {
                value = 6;
            }
            
            int uBass = setting.getInt("bass", 0);
            int uTreble = setting.getInt("treble", 0);
            int[] EQGain = new int[11];
            for (int idx = 0; idx < 11; idx++) {
                int temp = 0;
                
                if (idx >= 1 && idx <= 3) {
                    temp = Array.gEQTypePos[value][idx] + uBass;
                } else if (idx >= 8 && idx <= 10) {
                    temp = Array.gEQTypePos[value][idx] + uTreble;
                } else {
                    temp = Array.gEQTypePos[value][idx];
                }
                
                if (temp > 14) {
                    temp =  14;
                } else if (temp < -14) {
                    temp = -14;
                }

                if (idx == 0) {
                    EQGain[idx] = Array.g_dryValues[temp + 14];
                } else {
                    EQGain[idx] = Array.g_ganValues[temp + 14];
                }
            }
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetEQValues(EQGain);
            }
            Switch_EQtype(value);
            editor.putInt(EQUILBRIA, value);
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_REVERB_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            if (mmtksetting == null) {
            } else {

                int ReverbCoef[] = null;
                if (value == 0) {
                    ReverbCoef = Array.ReverbCoef_off;
                } else if (value == 1) {
                    ReverbCoef = Array.ReverbCoef_live;
                } else if (value == 2) {
                    ReverbCoef = Array.ReverbCoef_hall;
                } else if (value == 3) {
                    ReverbCoef = Array.ReverbCoef_concert;
                } else if (value == 4) {
                    ReverbCoef = Array.ReverbCoef_cave;
                } else if (value == 5) {
                    ReverbCoef = Array.ReverbCoef_bathroom;
                } else {

                    ReverbCoef = Array.ReverbCoef_arena;
                }
                mmtksetting.SetReverbType(value, ReverbCoef);
                //Begin : Added by vend00102 2012.07.27
              //  mmtksetting.DVP_GSetReverbType(value);
                //End : Added by vend00102 2012.07.27
                Switch_Reverb(value);
                editor.putInt(REVERB, value);
                editor.commit();
            }
        } else if ((arg0.getKey()).equals(KEY_PL2_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetPL2(value);
                //Begin : Added by vend00102 2012.07.27
              //  mmtksetting.DVP_GSetPL2(value);
                //End : Added by vend00102
                Switch_PL2(value);
                editor.putInt(PL2, value);
                editor.putString("initpl2", "initpl2");
                //editor.commit();

                if( value == 0 )
                    editor.putInt("Share_PL2", 0);
                else
                {
                    if( setting.getInt("Share_SRS", 0) == 1 )
                        editor.putInt("Share_SRS", 0);
                    editor.putInt("Share_PL2", 1);
                }

                editor.commit();
            }
        } else if ((arg0.getKey()).equals(KEY_MIX_SETTING)) {
            boolean flag = (Boolean) arg1;
            int value = 0;
            int[] rUpmixGain = null;
            if (flag == true) {
                value = 1;
                rUpmixGain = Array.rUpmixGain_1;
                mMix.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
            } else {
                value = 0;
                rUpmixGain = Array.rUpmixGain_0;
                mMix.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
            }
            mmtksetting.SetUpMix(value, rUpmixGain);
            mMix.setChecked(flag);
            editor.putInt(MIX, value);
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_SPDIF_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetSpdifOutputType(value);
                Switch_Spdif(value);
                editor.putInt(SPDIF, value);
                editor.commit();
            }
        }
        return true;
    }

    public void Switch_Reverb(int value) {
        switch (value) {
        case 0:
            mreverb.setSummary(R.string.STR_SETTINGS_RVB_NONE);
            break;
        case 1:
            mreverb.setSummary(R.string.STR_SETTINGS_RVB_LIVING);
            break;
        case 2:
            mreverb.setSummary(R.string.STR_SETTINGS_RVB_HALL);
            break;
        case 3:
            mreverb.setSummary(R.string.STR_SETTINGS_RVB_CONCERT);
            break;
        case 4:
            mreverb.setSummary(R.string.STR_SETTINGS_RVB_CAVE);
            break;
        case 5:
            mreverb.setSummary(R.string.STR_SETTINGS_RVB_BATHROOM);
            break;
        case 6:
            mreverb.setSummary(R.string.STR_SETTINGS_RVB_ARENA);
            break;
        }
        mreverb.setValueIndex(value);
    }

    public void Switch_PL2(int value) {
        switch (value) {
        case 0:
            mpl2.setSummary(R.string.STR_SETTINGS_PL2_NONE);
            mSrs.setEnabled(true);
            break;
        case 1:
            mpl2.setSummary(R.string.STR_SETTINGS_PL2_MUSIC);
            mSrs.setEnabled(false);
            break;
        case 2:
            mpl2.setSummary(R.string.STR_SETTINGS_PL2_MOVIE);
            mSrs.setEnabled(false);
            break;
        }
        mpl2.setValueIndex(value);
    }

    public void Switch_Mix(int value) {
        switch (value) {
        case 0:
            mMix.setChecked(false);
            mMix.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
            break;
        case 1:
            mMix.setChecked(true);
            mMix.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
            break;
        }
    }

    public void Switch_Spdif(int value) {
        switch (value) {
        case 0:
            mspdif.setSummary(R.string.STR_SETTINGS_SPDIF_OFF);
            break;
        case 1:
            mspdif.setSummary(R.string.STR_SETTINGS_SPDIF_RAW);
            break;
        case 2:
            mspdif.setSummary(R.string.STR_SETTINGS_SPDIF_PCM);
            break;
        }
        mspdif.setValueIndex(value);
    }

    public void Switch_EQtype(int value) {
        switch (value) {
        case 0:
            mequilibria.setSummary(R.string.STR_SETTINGS_EQ_OFF);
            break;
        case 1:
            mequilibria.setSummary(R.string.STR_SETTINGS_EQ_ROCK);
            break;
        case 2:
            mequilibria.setSummary(R.string.STR_SETTINGS_EQ_POP);
            break;
        case 3:
            mequilibria.setSummary(R.string.STR_SETTINGS_EQ_LIVE);
            break;
        case 4:
            mequilibria.setSummary(R.string.STR_SETTINGS_EQ_DANCE);
            break;
        case 5:
            mequilibria.setSummary(R.string.STR_SETTINGS_EQ_CLASSIC);
            break;
        case 6:
            mequilibria.setSummary(R.string.STR_SETTINGS_EQ_SOFT);
            break;
        }
        mequilibria.setValueIndex(value);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unbindService(serviceConnection);
        //Begin : Added by vend00102 2012.07.27
       // mmtksetting.GCloseDVPSet();
        //End : Added by vend00102 2012.07.27
        editor.putString("advanced", "advanced");
        editor.commit();
    }

}
