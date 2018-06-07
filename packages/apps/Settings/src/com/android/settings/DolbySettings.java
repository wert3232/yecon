package com.android.settings;

import com.android.settings.R;
import com.android.settings.mtksetting.MyBinder;
import com.mtksetting.Array;
import android.util.Log;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class DolbySettings extends PreferenceActivity implements
        OnPreferenceChangeListener {
    private final static String TAG = "DolbySettings";        
    private mtksetting mmtksetting;
    private Intent serviceIntent;
    private ListPreference mDualMonoType;// 双声道
    private ListPreference mDynamicType;// 动态范围压缩
    private final String KEY_DUALMONO_SETTING = "dualMono";
    private final String KEY_DYNAMIC_SETTING = "dynamic";
    private final String DUALMONO_TYPE = "dualmonotype";
    private final String DYNAMIC_TYPE = "dynamictype";
    SharedPreferences.Editor editor ;
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
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.dubi_setting);
        serviceIntent = new Intent(this, mtksetting.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        
        SharedPreferences uiState = getSharedPreferences(Array.MTKSettings,this.MODE_PRIVATE);
        editor= uiState.edit();
        setting=getSharedPreferences(Array.MTKSettings, this.MODE_PRIVATE);
        mDualMonoType = (ListPreference) findPreference(KEY_DUALMONO_SETTING);
        int DualMonoType = setting.getInt(DUALMONO_TYPE, 0);
        Switch_DualMonotype(DualMonoType);
        mDualMonoType.setOnPreferenceChangeListener(this);
        
        
        mDynamicType = (ListPreference) findPreference(KEY_DYNAMIC_SETTING);
        int DynamicType = setting.getInt(DYNAMIC_TYPE, 8);
        Switch_Dynamictype(DynamicType);
        mDynamicType.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onPreferenceChange");
       // mmtksetting.GOpenDVPSet();
        if ((arg0.getKey()).equals(KEY_DUALMONO_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetDualMonoType(value);
                Switch_DualMonotype(value);
                editor.putInt(DUALMONO_TYPE, value);
                editor.commit();
            }
        }else if((arg0.getKey()).equals(KEY_DYNAMIC_SETTING)){
            int value = Integer.parseInt(arg1.toString());
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetDynamicType(value);
                Switch_Dynamictype(value);
                editor.putInt(DYNAMIC_TYPE, value);
                editor.commit();
            }
        }
       // mmtksetting.GCloseDVPSet();
        return true;

    }

    public void Switch_DualMonotype(int arg0) {
        Log.e(TAG, "Switch_DualMonotype arg0 :" + arg0);
        switch (arg0) {
        case 0:
            mDualMonoType.setSummary(R.string.STR_SETTINGS_DOLBY_STEREO);
            break;
        case 1:
            mDualMonoType.setSummary(R.string.STR_SETTINGS_DOLBY_L);
            break;
        case 2:
            mDualMonoType.setSummary(R.string.STR_SETTINGS_DOLBY_R);
            break;
        case 3:
            mDualMonoType.setSummary(R.string.STR_SETTINGS_DOLBY_MONO);
            break;
        }
        Log.e(TAG, "Switch_DualMonotype success");
    }

    public void Switch_Dynamictype(int arg0) {
        Log.e(TAG, "Switch_Dynamictype arg0 :" + arg0);
        switch(arg0){
        case 0:
            mDynamicType.setSummary(R.string.STR_SETTINGS_DOLBY_OFF);
            break;
        case 1:
            mDynamicType.setSummary(R.string.STR_SETTINGS_DOLBY_18);
            break;
        case 2:
            mDynamicType.setSummary(R.string.STR_SETTINGS_DOLBY_14);
            break;
        case 3:
            mDynamicType.setSummary(R.string.STR_SETTINGS_DOLBY_38);
            break;
        case 4:
            mDynamicType.setSummary(R.string.STR_SETTINGS_DOLBY_12);
            break;
        case 5:
            mDynamicType.setSummary(R.string.STR_SETTINGS_DOLBY_58);
            break;
        case 6:
            mDynamicType.setSummary(R.string.STR_SETTINGS_DOLBY_34);
            break;
        case 7:
            mDynamicType.setSummary(R.string.STR_SETTINGS_DOLBY_78);
            break;
        case 8:
            mDynamicType.setSummary(R.string.STR_SETTINGS_DOLBY_FULL);
            break;
        }
        Log.e(TAG, "Switch_Dynamictype success");
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
