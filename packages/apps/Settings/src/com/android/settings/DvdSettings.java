package com.android.settings;

import com.android.settings.DolbySettings;
import com.android.settings.InitialSettings;
import com.android.settings.R;
import com.android.settings.InitialSettings.mPasswordClick;
import com.android.settings.mtksetting.MyBinder;
import com.mtksetting.Array;

import android.app.AlertDialog;
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
import android.preference.SeekBarPreference;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DvdSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
    private PreferenceScreen mdubiPreferenceScreen;// 杜比
    private PreferenceScreen mearlyPreferenceScreen;// 初期设定
    private ListPreference mDisplayType;// 电视机形式
    private CheckBoxPreference mCaptionsType;// 隐藏字幕
    private CheckBoxPreference mscreensaver;// 屏幕保护
    private CheckBoxPreference mLastMemType;// 上次记忆设定
    private DialogTypePerference mdialogtype;// 对话音量
    private PreferenceScreen mpasswordPreferenceScreen;// 密码
    private final String KEY_DUBI_SETTING = "dubi";
    private final String KEY_EARLY_SETTING = "early";
    private final String KEY_DISPALYTYPE_SETTING = "displaytype";
    private final String KEY_CAPTIONSTYPE_SETTING = "captionstype";
    private final String KEY_SCREENSAVER_SETTING = "screensaver";
    private final String KEY_LASTMEN_SETTING = "LastMem";
    private final String KEY_DIALOGTYPE_SETTING = "dialogtype";
    private final String KEY_PASSWORD_SETTING = "password";
    private mtksetting mmtksetting;
    private Intent serviceIntent;
    private static final String DISPLAY_TYPE = "display";
    private static final String CAPTION_TYPE = "caption";
    private static final String SCREENSAVER_KEY = "screensaver_key";
    private static final String LASTMEN = "lastmemorytype";
    private static final String DIALOGTYPE = "dialogtype";
    
    //password
    // password
    private EditText mEditText;
    private Button mButtonSure;
    private Button mButtonDelete;
    private Button mButtonZero;
    private Button mButtonOne;
    private Button mButtonTwo;
    private Button mButtonThree;
    private Button mButtonFour;
    private Button mButtonFive;
    private Button mButtonSix;
    private Button mButtonSeven;
    private Button mButtonEight;
    private Button mButtonNine;
    private LayoutInflater factory;
    private String temp = null;
    private boolean ispassword;
    private boolean repassword;
    private final String PASSWORD = "password";
    private final String PASSWORD_KEY = "key";
    String password;
    AlertDialog dialog;
    //
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    Context mcontext;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.dvd_setting);
        mcontext=getActivity();
        serviceIntent = new Intent(mcontext, mtksetting.class);
        mcontext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        mdialogtype = (DialogTypePerference) findPreference(KEY_DIALOGTYPE_SETTING);
        mdubiPreferenceScreen = (PreferenceScreen) findPreference(KEY_DUBI_SETTING);
        mearlyPreferenceScreen = (PreferenceScreen) findPreference(KEY_EARLY_SETTING);
        mpasswordPreferenceScreen = (PreferenceScreen) findPreference(KEY_PASSWORD_SETTING);
        setting = mcontext.getSharedPreferences(Array.MTKSettings,mcontext.MODE_PRIVATE);
        SharedPreferences uiState = mcontext.getSharedPreferences(Array.MTKSettings, mcontext.MODE_PRIVATE);
        editor = uiState.edit();
        mDisplayType = (ListPreference) findPreference(KEY_DISPALYTYPE_SETTING);
        int diaplaytype = setting.getInt(DISPLAY_TYPE, 0);
        Switch_DisplayType(diaplaytype);
        if(mDisplayType != null){
            mDisplayType.setOnPreferenceChangeListener(this);
        }

        mCaptionsType = (CheckBoxPreference) findPreference(KEY_CAPTIONSTYPE_SETTING);
        int captionstype = setting.getInt(CAPTION_TYPE, 1);
        Switch_CaptionsType(captionstype);
        if(mCaptionsType != null){
            mCaptionsType.setOnPreferenceChangeListener(this);
        }

        mscreensaver = (CheckBoxPreference) findPreference(KEY_SCREENSAVER_SETTING);
        int screensaver = setting.getInt(SCREENSAVER_KEY, 0);
        Switch_Screensaver(screensaver);
        if(mscreensaver != null){
            mscreensaver.setOnPreferenceChangeListener(this);
        }

        mLastMemType = (CheckBoxPreference) findPreference(KEY_LASTMEN_SETTING);
        int lastmen = setting.getInt(LASTMEN, 0);
        Switch_LastMem(lastmen);
        if(mLastMemType != null){
            mLastMemType.setOnPreferenceChangeListener(this);
        }

        int volume = setting.getInt(DIALOGTYPE, 0);
        mdialogtype.setSummary(Integer.toString(volume));
        mdialogtype = (DialogTypePerference) findPreference("dialogtype");
        
        //ATC
        removePreference(KEY_DISPALYTYPE_SETTING);
        removePreference(KEY_CAPTIONSTYPE_SETTING);
        removePreference(KEY_SCREENSAVER_SETTING);
        removePreference(KEY_LASTMEN_SETTING);
        removePreference(KEY_DIALOGTYPE_SETTING);
        removePreference(KEY_EARLY_SETTING);
        
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mdubiPreferenceScreen) {
            Intent intent = new Intent("com.android.dolby");
            
            //intent.setClass(DvdSettings.this, DolbySettings.class);
            startActivity(intent);
        }
        if (preference == mearlyPreferenceScreen) {
            Intent intent = new Intent("com.android.initial");
            //intent.setClass(DvdSettings.this, InitialSettings.class);
            startActivity(intent);
        }
        if (preference == mpasswordPreferenceScreen) {
            factory = LayoutInflater.from(mcontext);
            final View DialogView = factory.inflate(R.layout.password, null);
            dialog = new AlertDialog.Builder(mcontext)
                    .setTitle(R.string.dvd_password_title)
                    .setView(DialogView)
                    .create();
            dialog.show();
            mEditText = (EditText) DialogView.findViewById(R.id.password);
            mEditText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mButtonSure = (Button) DialogView.findViewById(R.id.button_sure);
            mButtonSure.setOnClickListener(new mPasswordClick());
            mButtonDelete = (Button) DialogView
                    .findViewById(R.id.button_delete);
            mButtonDelete.setOnClickListener(new mPasswordClick());
            mButtonZero = (Button) DialogView.findViewById(R.id.button00);
            mButtonZero.setOnClickListener(new mPasswordClick());
            mButtonOne = (Button) DialogView.findViewById(R.id.button01);
            mButtonOne.setOnClickListener(new mPasswordClick());
            mButtonTwo = (Button) DialogView.findViewById(R.id.button02);
            mButtonTwo.setOnClickListener(new mPasswordClick());
            mButtonThree = (Button) DialogView.findViewById(R.id.button03);
            mButtonThree.setOnClickListener(new mPasswordClick());
            mButtonFour = (Button) DialogView.findViewById(R.id.button04);
            mButtonFour.setOnClickListener(new mPasswordClick());
            mButtonFive = (Button) DialogView.findViewById(R.id.button05);
            mButtonFive.setOnClickListener(new mPasswordClick());
            mButtonSix = (Button) DialogView.findViewById(R.id.button06);
            mButtonSix.setOnClickListener(new mPasswordClick());
            mButtonSeven = (Button) DialogView.findViewById(R.id.button07);
            mButtonSeven.setOnClickListener(new mPasswordClick());
            mButtonEight = (Button) DialogView.findViewById(R.id.button08);
            mButtonEight.setOnClickListener(new mPasswordClick());
            mButtonNine = (Button) DialogView.findViewById(R.id.button09);
            mButtonNine.setOnClickListener(new mPasswordClick());
            init();
        }
        return true;
    }
    
    public void init() {
        SharedPreferences sp = mcontext.getSharedPreferences(
                "password_preferences", Context.MODE_WORLD_READABLE);
        password = sp.getString(PASSWORD, "");
        if (password.equals("")) {
            ispassword = false;
            Toast.makeText(mcontext, R.string.dvd_settingpassword,
                    Toast.LENGTH_LONG).show();
        } else {
            ispassword = true;
            Toast.makeText(mcontext, R.string.dvd_password,
                    Toast.LENGTH_LONG).show();
        }
    }
    
    class mPasswordClick implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            temp = mEditText.getText().toString().trim();
            switch (arg0.getId()) {
            case R.id.button_sure:
                if (ispassword == true && repassword == false){
                    if (password.equals(temp)) {
                        repassword = true; 
                        dialog.dismiss();
                    } else {
                        Toast.makeText(mcontext,
                                R.string.dvd_errorpassword, Toast.LENGTH_LONG)
                                .show();
                        temp = "";
                    }
                } else {
                    if (temp.length() == 6){
                        SharedPreferences sp = mcontext.getSharedPreferences(
                                "password_preferences", Context.MODE_WORLD_READABLE);
                        SharedPreferences.Editor edcon = sp.edit();
                        edcon.putString(PASSWORD, temp);
                        edcon.commit();
                        Toast.makeText(mcontext,
                                R.string.dvd_rightpassword, Toast.LENGTH_LONG)
                                .show();
                        dialog.dismiss();
                    }else{
                         Toast.makeText(mcontext,
                                    R.string.dvd_password, Toast.LENGTH_LONG)
                                    .show();
                    }
                }
                break;
            case R.id.button_delete:
                if ((temp.length()) >= 1) {
                    temp = temp.substring(0, (temp.length()) - 1);
                }
                break;
            case R.id.button00:
                temp += "0";
                break;
            case R.id.button01:
                temp += "1";
                break;
            case R.id.button02:
                temp += "2";
                break;
            case R.id.button03:
                temp += "3";
                break;
            case R.id.button04:
                temp += "4";
                break;
            case R.id.button05:
                temp += "5";
                break;
            case R.id.button06:
                temp += "6";
                break;
            case R.id.button07:
                temp += "7";
                break;
            case R.id.button08:
                temp += "8";
                break;
            case R.id.button09:
                temp += "9";
                break;

            }
            Log.d("tag", "msg------------wuxin########--------temp===" + temp);
            mEditText.setText(temp);
        }
    }


    public void Switch_DisplayType(int arg0) {

        switch (arg0) {
        case 0:
            mDisplayType.setSummary(R.string.STR_SETTINGS_TVDISPLAY_NORMALPS);
            break;
        case 1:
            mDisplayType.setSummary(R.string.STR_SETTINGS_TVDISPLAY_NORMALLB);
            break;
        case 2:
            mDisplayType.setSummary(R.string.STR_SETTINGS_TVDISPLAY_WIDE);
            break;
        case 3:
            mDisplayType.setSummary(R.string.STR_SETTINGS_TVDISPLAY_WIDES);
            break;

        }
    }

    public void Switch_CaptionsType(int value) {
        switch (value) {
        case 0:
            mCaptionsType.setSummary(R.string.STR_SETTINGS_EQ_ON);
            mCaptionsType.setChecked(true);
            break;
        case 1:
            mCaptionsType.setSummary(R.string.STR_SETTINGS_EQ_OFF);
            mCaptionsType.setChecked(false);
            break;

        }
    }

    public void Switch_Screensaver(int value) {
        switch (value) {
        case 0:
            mscreensaver.setSummary(R.string.STR_SETTINGS_EQ_ON);
            mscreensaver.setChecked(true);
            break;
        case 1:
            mscreensaver.setSummary(R.string.STR_SETTINGS_EQ_OFF);
            mscreensaver.setChecked(false);
            break;
        }
    }

    public void Switch_LastMem(int value) {
        switch (value) {
        case 0:
            mLastMemType.setSummary(R.string.STR_SETTINGS_EQ_ON);
            mLastMemType.setChecked(true);
            break;
        case 1:
            mLastMemType.setSummary(R.string.STR_SETTINGS_EQ_OFF);
            mLastMemType.setChecked(false);
            break;
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mcontext.unbindService(serviceConnection);
    }

    @Override
    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        // TODO Auto-generated method stub
     //   mmtksetting.GOpenDVPSet();
        if ((arg0.getKey()).equals(KEY_DISPALYTYPE_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetDisplayOutputType(value);
                Switch_DisplayType(value);
                editor.putInt(DISPLAY_TYPE, value);
                editor.commit();
            }
        } else if ((arg0.getKey()).equals(KEY_CAPTIONSTYPE_SETTING)) {
            /*boolean flag = (Boolean) arg1;
            int value = 0;
            if (flag) {
                value = 0;
            } else {
                value = 1;
            }
            mmtksetting.DVP_GSetCaptionsType(value);
            Switch_CaptionsType(value);
            editor.putInt(CAPTION_TYPE, value);
            editor.commit();*/
        } else if ((arg0.getKey()).equals(KEY_SCREENSAVER_SETTING)) {
            /*boolean flag = (Boolean) arg1;
            int value = 0;
            if (flag) {
                value = 0;
            } else {
                value = 1;
            }
            mmtksetting.DVP_GSetScreenSaverType(value);            
            Switch_Screensaver(value);
            editor.putInt(SCREENSAVER_KEY, value);
            editor.commit();*/
        } else if ((arg0.getKey()).equals(KEY_LASTMEN_SETTING)) {
          /*  boolean flag = (Boolean) arg1;
            int value = 0;
            if (flag) {
                value = 0;
            } else {
                value = 1;
            }
            mmtksetting.DVP_GSetLastMemType(value);            
            Switch_LastMem(value);
            editor.putInt(LASTMEN, value);
            editor.commit();*/
        }
       // mmtksetting.GCloseDVPSet();
        return true;
    }

}
