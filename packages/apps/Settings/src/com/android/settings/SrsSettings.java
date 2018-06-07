package com.android.settings;

import com.android.settings.R;
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
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

public class SrsSettings extends PreferenceActivity implements
        OnPreferenceChangeListener {

    private CheckBoxPreference msrcswitch;// src状态
    private ListPreference msrsmode;// srs模式
    private CheckBoxPreference msrsphantom;// 虚拟中置
    private CheckBoxPreference msrsfullband;// 屏宽限制
    private CheckBoxPreference msrstruebass;// 低音增强
    private final String KEY_SRSSWITCH_SETTING = "srsswitch";
    private final String SRSSWITCH_KEY = "srsswitch_key";
    private final String KEY_SRSMODE_SETTING = "srsmode";
    private final String SRSMODE_KEY = "srsmode_key";
    private final String KEY_SRSPHANTOM_SETTING = "srsphantom";
    private final String SRSPHANTOM_KEY = "srsphantom_key";
    private final String KEY_SRSFULLBAND_SETTING = "srsfullband";
    private final String SRSFULLBAND_KEY = "srsfullband_key";
    private final String KEY_SRSTRUEBASS_SETTING = "srstruebass";
    private final String SRSTRUEBASS_KEY = "srstruebass_key";
    private final String KEY_SRSOTHER_SETTING="srsother";
    private PreferenceScreen mSrsOther;
    private mtksetting mmtksetting;
    private Intent serviceIntent;
    SharedPreferences setting ;
    SharedPreferences.Editor editor;
    private int mfrontspeaker_state;
    private int mcenterspeaker_state;
    private int msurroundspeaker_state;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.srs_settings);
        serviceIntent = new Intent(this, mtksetting.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        setting=getSharedPreferences(Array.MTKSettings,this.MODE_PRIVATE);
        SharedPreferences uiState = getSharedPreferences(Array.MTKSettings, this.MODE_PRIVATE);
        editor= uiState.edit();
        msrcswitch = (CheckBoxPreference) findPreference(KEY_SRSSWITCH_SETTING);
        int srsswitch = setting.getInt(SRSSWITCH_KEY, 0);
        msrcswitch.setOnPreferenceChangeListener(this);

        msrsmode = (ListPreference) findPreference(KEY_SRSMODE_SETTING);
        int srsmode = setting.getInt(SRSMODE_KEY, 0);
        Srsmode(srsmode);
        msrsmode.setOnPreferenceChangeListener(this);

        msrsphantom = (CheckBoxPreference) findPreference(KEY_SRSPHANTOM_SETTING);
        int srsphantom = setting.getInt(SRSPHANTOM_KEY, 0);
        Switch_SrsPhantom(srsphantom);
        msrsphantom.setOnPreferenceChangeListener(this);

        msrsfullband = (CheckBoxPreference) findPreference(KEY_SRSFULLBAND_SETTING);
        int srsfullband = setting.getInt(SRSFULLBAND_KEY, 0);
        Switch_SrsFullBand(srsfullband);
        msrsfullband.setOnPreferenceChangeListener(this);

        msrstruebass = (CheckBoxPreference) findPreference(KEY_SRSTRUEBASS_SETTING);
        int srstruebass = setting.getInt(SRSTRUEBASS_KEY, 0);
        Switch_SrsTrueBass(srstruebass);
        msrstruebass.setOnPreferenceChangeListener(this);
        mSrsOther=(PreferenceScreen)findPreference(KEY_SRSOTHER_SETTING);
        getSpeaker();
        SrsSwitch(srsswitch);
        mSrsOther.setOnPreferenceClickListener(new OnPreferenceClickListener(){

            @Override
            public boolean onPreferenceClick(Preference arg0) {
                // TODO Auto-generated method stub
                Intent intent =new  Intent();
                intent.setClass(getApplicationContext(), SrsOtherSettings.class);
                startActivity(intent);
                return false;
            }});
    }
     public void getSpeaker(){
        
        mcenterspeaker_state=setting.getInt("centerspeaker_state", 0);
        msurroundspeaker_state=setting.getInt("surround_state", 0);
        if(mcenterspeaker_state==2&&msurroundspeaker_state==2){
            mSrsOther.setEnabled(false);
        }else{
            mSrsOther.setEnabled(true);
        }
     }
    @Override
    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        // TODO Auto-generated method stub
        if ((arg0.getKey()).equals(KEY_SRSSWITCH_SETTING)) {
            boolean flag = (Boolean) arg1;
            int value;
            if (flag == true) {
                value = 1;
                mmtksetting.SetSRSSwitch(value);
                editor.putInt("initadvavce", 1);
                if( setting.getInt("Share_PL2", 0) == 1 )
                    editor.putInt("Share_PL2", 0);
                editor.putInt("Share_SRS", 1);
                editor.commit();
            } else {
                value = 0;
                mmtksetting.SetSRSSwitch(value);
                editor.putInt("Share_SRS", 0);
            }
            SrsSwitch(value);
            editor.putInt(SRSSWITCH_KEY, value);
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_SRSMODE_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetSRSMode(value);
                Srsmode(value);
                editor.putInt(SRSMODE_KEY, value);
                editor.commit();
            }

        } else if ((arg0.getKey()).equals(KEY_SRSPHANTOM_SETTING)) {
            boolean flag = (Boolean) arg1;
            int value = 0;
            if (flag == true) {
                value = 1;
                mmtksetting.SetSRSPhantom(value);
                msrsphantom.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
            } else {
                value = 0;
                mmtksetting.SetSRSPhantom(value);
                msrsphantom.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
            }
            msrsphantom.setChecked(flag);
            editor.putInt(SRSPHANTOM_KEY, value);
            editor.commit();

        } else if ((arg0.getKey()).equals(KEY_SRSFULLBAND_SETTING)) {
            boolean flag = (Boolean) arg1;
            int value = 0;
            if (flag == true) {
                value = 1;
                msrsfullband.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
                mmtksetting.SetSRSFullBand(value);
            } else {
                value = 0;
                msrsfullband.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
                mmtksetting.SetSRSFullBand(value);
            }
            msrsfullband.setChecked(flag);
            editor.putInt(SRSFULLBAND_KEY, value);
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_SRSTRUEBASS_SETTING)) {
            boolean flag = (Boolean) arg1;
            int value;
            if (flag == true) {
                value = 1;
                msrstruebass.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
                mmtksetting.SetSRSTrueBass(value);
            } else {
                value = 0;
                msrstruebass.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
                mmtksetting.SetSRSTrueBass(value);
            }
            msrstruebass.setChecked(flag);
            editor.putInt(SRSTRUEBASS_KEY, value);
            editor.commit();
        }
        
        editor.putString("srsenable", "srsenable");
        editor.commit();
        return true;
    }

    public void Switch_SrsPhantom(int value) {
        switch (value) {
        case 0:
            msrsphantom.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
            msrsphantom.setChecked(false);
            break;
        case 1:
            msrsphantom.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
            msrsphantom.setChecked(true);
            break;
        

        }
    }

    public void Switch_SrsFullBand(int value) {
        switch (value) {
        case 0:
            msrsfullband.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
            msrsfullband.setChecked(false);
            break;
        case 1:
            msrsfullband.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
            msrsfullband.setChecked(true);
            break;
    

        }
    }

    public void Switch_SrsTrueBass(int value) {
        switch (value) {
        case 0:
            msrstruebass.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
            msrstruebass.setChecked(false);
            break;
        case 1:
            msrstruebass.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
            msrstruebass.setChecked(true);
            break;
        

        }
    }

    public void SrsSwitch(int value) {

        switch (value) {
        case 0:
            msrcswitch.setChecked(false);
            msrcswitch.setSummary(R.string.STR_SETTINGS_SRS_PHANTOM_OFF);
            msrsmode.setEnabled(false);
            msrstruebass.setEnabled(false);
            msrsfullband.setEnabled(false);
            msrsphantom.setEnabled(false);
            mSrsOther.setEnabled(false);
            break;
        case 1:
            msrcswitch.setChecked(true);
            msrcswitch.setSummary(R.string.STR_SETTINGS_SRS_PHANTOM_ON);
            msrsmode.setEnabled(true);
            msrstruebass.setEnabled(true);
            msrsfullband.setEnabled(true);
            msrsphantom.setEnabled(true);
            mSrsOther.setEnabled(true);
            break;
        
        }

    }

    public void Srsmode(int value) {

        switch (value) {
        case 0:
            msrsmode.setSummary(R.string.STR_SETTINGS_SRS_MODE_CINEMA);
            break;
        case 1:
            msrsmode.setSummary(R.string.STR_SETTINGS_SRS_MODE_PRO);
            break;
        case 2:
            msrsmode.setSummary(R.string.STR_SETTINGS_SRS_MODE_MUSIC);
            break;
        case 3:
            msrsmode.setSummary(R.string.STR_SETTINGS_SRS_MODE_MONO);
            break;
        case 4:
            msrsmode.setSummary(R.string.STR_SETTINGS_SRS_MODE_LCRS);
            break;
        }
        msrsmode.setValueIndex(value);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unbindService(serviceConnection);
    }

}
