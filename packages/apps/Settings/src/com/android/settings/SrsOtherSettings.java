package com.android.settings;

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

public class SrsOtherSettings extends PreferenceActivity implements
        OnPreferenceChangeListener {
    CheckBoxPreference mfocusfront;// 前置语音加强
    CheckBoxPreference mfocusrear;// 后置语音加强
    CheckBoxPreference mfocuscenter;// 低音语音加强
    ListPreference mtbssfront;// 前置喇叭频宽
    ListPreference mtbssrear;// 环绕喇叭频宽
    ListPreference mtbsssub;// 低音喇叭频宽
    private final String FOCUSFRONT_SETTING_KEY = "srsothfocusfront";
    private final String FOCUSREAR_SETTING_KEY = "srsothfocusrear";
    private final String FOCUSCENTER_SETTING_KEY = "srsothfocussub";
    private final String TASSFRONT_SETTING_KEY = "speakersizefront";
    private final String TASSREAR_SETTING_KEY = "speakersizerear";
    private final String TASSSUB_SETTING_KEY = "speakersizesub";

    private final String FOCUSFRONT_KEY = "focusfront_key";
    private final String FOCUSREAR_KEY = "focusrear_key";
    private final String FOCUSCENTER_KEY = "focuscenter_key";
    private final String TASSFRONT_KEY="tassfront_key";
    private final String TASSREAR_KEY="tassrear_key";
    private final String TASSSUB_KEY="tasssub_key";

    private mtksetting mmtksetting;
    private Intent serviceIntent;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.srsothers_setting);
        serviceIntent = new Intent(this, mtksetting.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        setting = getSharedPreferences(Array.MTKSettings, this.MODE_PRIVATE);
        SharedPreferences uiState = getSharedPreferences(Array.MTKSettings,
                this.MODE_PRIVATE);
        editor = uiState.edit();

        int focusfront=setting.getInt(FOCUSFRONT_KEY, 0);
        int focusrear=setting.getInt(FOCUSREAR_KEY, 0);
        int focuscenter=setting.getInt(FOCUSCENTER_KEY, 0);
        int tassfront=setting.getInt(TASSFRONT_KEY, 0);
        int tassrear=setting.getInt(TASSREAR_KEY, 0);
        int tasssub=setting.getInt(TASSSUB_KEY, 0);
        
        mfocusfront = (CheckBoxPreference) findPreference(FOCUSFRONT_SETTING_KEY);
        mfocusfront.setOnPreferenceChangeListener(this);
        mfocusrear = (CheckBoxPreference) findPreference(FOCUSREAR_SETTING_KEY);
        mfocusrear.setOnPreferenceChangeListener(this);
        mfocuscenter = (CheckBoxPreference) findPreference(FOCUSCENTER_SETTING_KEY);
        mfocuscenter.setOnPreferenceChangeListener(this);
        mtbssfront = (ListPreference) findPreference(TASSFRONT_SETTING_KEY);
        mtbssfront.setOnPreferenceChangeListener(this);
        mtbssrear = (ListPreference) findPreference(TASSREAR_SETTING_KEY);
        mtbssrear.setOnPreferenceChangeListener(this);
        mtbsssub = (ListPreference) findPreference(TASSSUB_SETTING_KEY);
        mtbsssub.setOnPreferenceChangeListener(this);
        Srsfocus_front(focusfront);
        Srsfocus_rear(focusrear);
        Srsfocus_center(focuscenter);
        Srstass_front(tassfront);
        Srstass_rear(tassrear);
        Srstass_sub(tasssub);
        
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        // TODO Auto-generated method stub
        if (arg0.getKey().equals(FOCUSFRONT_SETTING_KEY)) {
            boolean flag = (Boolean) arg1;
            int value = 0;
            if (flag == true) {
                value = 1;
                mmtksetting.SetSRSFocus(1, value);// 打开
            } else {
                value = 0;
                mmtksetting.SetSRSFocus(1, value);// 关闭
            }
            mfocusfront.setChecked(flag);
            Srsfocus_front(value);
            editor.putInt(FOCUSFRONT_KEY, value);
            editor.commit();
        } else if (arg0.getKey().equals(FOCUSREAR_SETTING_KEY)) {
            boolean flag = (Boolean) arg1;
            int value = 0;
            if (flag == true) {
                value = 1;
                mmtksetting.SetSRSFocus(2, value);// 打开
            } else {
                value = 0;
                mmtksetting.SetSRSFocus(2, value);// 关闭
            }
            mfocusrear.setChecked(flag);
            Srsfocus_rear(value);
            editor.putInt(FOCUSREAR_KEY, value);
            editor.commit();
        } else if (arg0.getKey().equals(FOCUSCENTER_SETTING_KEY)) {
            boolean flag = (Boolean) arg1;
            int value = 0;
            if (flag == true) {
                value = 1;
                mmtksetting.SetSRSFocus(0, value);// 打开
            } else {
                value = 0;
                mmtksetting.SetSRSFocus(0, value);// 关闭
            }
            mfocuscenter.setChecked(flag);
            Srsfocus_center(value);
            editor.putInt(FOCUSCENTER_KEY, value);
            editor.commit();
        }else if(arg0.getKey().equals(TASSFRONT_SETTING_KEY)){
            int value=Integer.parseInt(arg1.toString());
            int tasshz = 0;
            switch(value){
            case 0:
                tasshz=1 << 0;
                break;
            case 1:
                tasshz=1 << 1;
                break;
            case 2:
                tasshz=1 << 2;
                break;
            case 3:
                tasshz=1 << 3;
                break;
            case 4:
                tasshz=1 << 4;
                break;
            case 5:
                tasshz=1 << 5;
                break;
            case 6:
                tasshz=1 << 6;
                break;
            case 7:
                tasshz=1 << 7;
                break;
            }
            mmtksetting.SetSRSTrueBassSize(0,tasshz);
            Srstass_front(value);
            editor.putInt(TASSFRONT_KEY, value);
            editor.commit();
        }else if(arg0.getKey().equals(TASSREAR_SETTING_KEY)){
            int value=Integer.parseInt(arg1.toString());
            int tasshz = 0;
            switch(value){
            case 0:
                tasshz=1 << 0;
                break;
            case 1:
                tasshz=1 << 1;
                break;
            case 2:
                tasshz=1 << 2;
                break;
            case 3:
                tasshz=1 << 3;
                break;
            case 4:
                tasshz=1 << 4;
                break;
            case 5:
                tasshz=1 << 5;
                break;
            case 6:
                tasshz=1 << 6;
                break;
            case 7:
                tasshz=1 << 7;
                break;
            }
            mmtksetting.SetSRSTrueBassSize(2,tasshz);
            Srstass_rear(value);
            editor.putInt(TASSREAR_KEY, value);
            editor.commit();
        }else{
            int value=Integer.parseInt(arg1.toString());
            int tasshz = 0;
            switch(value){
            case 0:
                tasshz=1 << 0;
                break;
            case 1:
                tasshz=1 << 1;
                break;
            case 2:
                tasshz=1 << 2;
                break;
            case 3:
                tasshz=1 << 3;
                break;
            case 4:
                tasshz=1 << 4;
                break;
            case 5:
                tasshz=1 << 5;
                break;
            case 6:
                tasshz=1 << 6;
                break;
            case 7:
                tasshz=1 << 7;
                break;
            }
            mmtksetting.SetSRSTrueBassSize(1,tasshz);
            Srstass_sub(value);
            editor.putInt(TASSSUB_KEY, value);
            editor.commit();
        }
        return false;
    }

    public void Srsfocus_front(int value) {
        if (value == 1) {
            mfocusfront.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
        } else {
            mfocusfront.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
        }

    }

    public void Srsfocus_rear(int value) {
        if (value == 1) {
            mfocusrear.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
        } else {
            mfocusrear.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
        }

    }

    public void Srsfocus_center(int value) {
        if (value == 1) {
            mfocuscenter.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
        } else {
            mfocuscenter.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
        }

    }
    public void Srstass_front(int value){
        switch(value){
        case 0:
            mtbssfront.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_40);
            break;
        case 1:
            mtbssfront.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_60);
            break;
        case 2:
            mtbssfront.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_100);
            break;
        case 3:
            mtbssfront.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_150);
            break;
        case 4:
            mtbssfront.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_200);
            break;
        case 5:
            mtbssfront.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_250);
            break;
        case 6:
            mtbssfront.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_300);
            break;
        case 7:
            mtbssfront.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_400);
            break;
        }
        mtbssfront.setValueIndex(value);
    }
    
    public void Srstass_rear(int value){
        switch(value){
        case 0:
            mtbssrear.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_40);
            break;
        case 1:
            mtbssrear.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_60);
            break;
        case 2:
            mtbssrear.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_100);
            break;
        case 3:
            mtbssrear.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_150);
            break;
        case 4:
            mtbssrear.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_200);
            break;
        case 5:
            mtbssrear.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_250);
            break;
        case 6:
            mtbssrear.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_300);
            break;
        case 7:
            mtbssrear.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_400);
            break;
        }
        mtbssrear.setValueIndex(value);
    }
    public void Srstass_sub(int value){
        switch(value){
        case 0:
            mtbsssub.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_40);
            break;
        case 1:
            mtbsssub.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_60);
            break;
        case 2:
            mtbsssub.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_100);
            break;
        case 3:
            mtbsssub.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_150);
            break;
        case 4:
            mtbsssub.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_200);
            break;
        case 5:
            mtbsssub.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_250);
            break;
        case 6:
            mtbsssub.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_300);
            break;
        case 7:
            mtbsssub.setSummary(R.string.STR_SETTINGS_SRS_SPKSIZE_400);
            break;
        }
        mtbsssub.setValueIndex(value);
    }
}
