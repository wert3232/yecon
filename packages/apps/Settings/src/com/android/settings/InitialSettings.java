package com.android.settings;

import com.android.settings.R;
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
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InitialSettings extends PreferenceActivity implements
        OnPreferenceChangeListener {
    private final static String TAG = "InitialSettings";  
    private PreferenceScreen mpasswordPreferenceScreen;// 密码
    private final String KEY_PASSWORD_SETTING = "password";
    private ListPreference mTvType;// 电视机形式
    private CheckBoxPreference mPbcType;// /*pbc*/
    private ListPreference mAudioLanType;// 声音
    private ListPreference mSubLanType;// 字幕
    private ListPreference mMenuLanType;// 碟片选单语言
    private ListPreference mParentalType;// 年龄控制
    private final String KEY_TVTYPE_SETING = "tvtype";
    private final String KEY_PBCTYPE_SETTING = "pbctype";
    private final String KEY_AUDIOLANTYPE_SETTING = "audiolantype";
    private final String KEY_SUBLANTYPE_SETTING = "sublantype";
    private final String KEY_MENULANTYPE_SETTING = "menulantype";
    private final String KEY_PARENTALTYPE_SETTING = "parentaltype";
    private mtksetting mmtksetting;
    private Intent serviceIntent;
    private final String TVTYPE = "tvtype_key";
    private final String PBCTYPE = "pbctype_key";
    private final String AUDIOLANTYPE = "audiolantype_key";
    private final String SUBLANTYPE = "sublantype_key";
    private final String MENULANTYPE = "menulantype_key";
    private final String PARENTALTYPE = "parentaltype_key";
    private String key = null;
    private final String ISEANABLE = "key";
    SharedPreferences setting;
    SharedPreferences.Editor editor;
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
    private enum LANGUAGEID{
        CFG_LANGUAGE_ENGLISH,
        CFG_LANGUAGE_FRENCH,
        CFG_LANGUAGE_SPANISH,
        CFG_LANGUAGE_CHINESE,
        CFG_LANGUAGE_JAPANESE,
        CFG_LANGUAGE_OTHER,
        CFG_LANGUAGE_GERMAN,
        CFG_LANGUAGE_SWEDISH,
        CFG_LANGUAGE_DANISH,
        CFG_LANGUAGE_NORWEGIAN,
        CFG_LANGUAGE_FINNISH,
        CFG_LANGUAGE_DUTCH,
        CFG_LANGUAGE_ICELANDIC,
        CFG_LANGUAGE_PORTUGUES,
        CFG_LANGUAGE_HEBREW,
        CFG_LANGUAGE_GREEK,
        CFG_LANGUAGE_CROATIAN,
        CFG_LANGUAGE_TURKISH,
        CFG_LANGUAGE_ITALIAN,
        CFG_LANGUAGE_POLISH,
        CFG_LANGUAGE_HUNGARIAN,
        CFG_LANGUAGE_CZECH,
        CFG_LANGUAGE_KOREAN,
        CFG_LANGUAGE_RUSSIAN,
        CFG_LANGUAGE_THAI,
        CFG_LANGUAGE_INDONESIAN,
        CFG_LANGUAGE_MALAY,
        CFG_LANGUAGE_IRISH,
        CFG_LANGUAGE_ROMANIAN,
        CFG_LANGUAGE_LANG_OFF
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(this, mtksetting.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        addPreferencesFromResource(R.xml.initial_setting);
        SharedPreferences uiState = getSharedPreferences(Array.MTKSettings,
                this.MODE_PRIVATE);
        editor = uiState.edit();
        mTvType = (ListPreference) findPreference(KEY_TVTYPE_SETING);
        setting = getSharedPreferences(Array.MTKSettings, this.MODE_PRIVATE);
        int tvtype = setting.getInt(TVTYPE, 2);
        Switch_TvType(tvtype);
        mTvType.setOnPreferenceChangeListener(this);

        mPbcType = (CheckBoxPreference) findPreference(KEY_PBCTYPE_SETTING);
        int pbctype = setting.getInt(PBCTYPE, 0);
        Switch_PbcType(pbctype);
        mPbcType.setOnPreferenceChangeListener(this);

        mAudioLanType = (ListPreference) findPreference(KEY_AUDIOLANTYPE_SETTING);
        LANGUAGEID eAudlantype = LANGUAGEID.values()[setting.getInt(AUDIOLANTYPE, 0)];
        Switch_AudioLanType(eAudlantype, mAudioLanType);
        mAudioLanType.setOnPreferenceChangeListener(this);

        mSubLanType = (ListPreference) findPreference(KEY_SUBLANTYPE_SETTING);
        LANGUAGEID eSublantype = LANGUAGEID.values()[setting.getInt(SUBLANTYPE, 0)];
        Switch_AudioLanType(eSublantype, mSubLanType);
        mSubLanType.setOnPreferenceChangeListener(this);

        mMenuLanType = (ListPreference) findPreference(KEY_MENULANTYPE_SETTING);
        LANGUAGEID eMenulantype = LANGUAGEID.values()[setting.getInt(MENULANTYPE, 0)];
        Switch_AudioLanType(eMenulantype, mMenuLanType);
        mMenuLanType.setOnPreferenceChangeListener(this);

        mParentalType = (ListPreference) findPreference(KEY_PARENTALTYPE_SETTING);
        int parentaltype = setting.getInt(PARENTALTYPE, 8);
        Intent intent = getIntent();
        key = intent.getStringExtra(ISEANABLE);
        if (key == null) {
            mParentalType.setEnabled(false);
        } else {
            mParentalType.setEnabled(true);
        }
        Switch_ParentType(parentaltype);
        mParentalType.setOnPreferenceChangeListener(this);
        mpasswordPreferenceScreen = (PreferenceScreen) findPreference(KEY_PASSWORD_SETTING);
        repassword = false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mpasswordPreferenceScreen) {
            factory = LayoutInflater.from(InitialSettings.this);
            final View DialogView = factory.inflate(R.layout.password, null);
            dialog = new AlertDialog.Builder(InitialSettings.this)
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
        SharedPreferences sp = getSharedPreferences(
                "password_preferences", Context.MODE_WORLD_READABLE);
        password = sp.getString(PASSWORD, "");
        if (password.equals("")) {
            ispassword = false;
            Toast.makeText(InitialSettings.this, R.string.dvd_settingpassword,
                    Toast.LENGTH_LONG).show();
        } else {
            ispassword = true;
            Toast.makeText(InitialSettings.this, R.string.dvd_password,
                    Toast.LENGTH_LONG).show();
        }
    }

    private LANGUAGEID translateLanguageID(int value) {
        LANGUAGEID eValue = LANGUAGEID.CFG_LANGUAGE_ENGLISH;
        switch(value) {
            case 0:
                eValue = LANGUAGEID.CFG_LANGUAGE_ENGLISH;
                break;                
            case 1:
                eValue = LANGUAGEID.CFG_LANGUAGE_FRENCH;
                break;
            case 2:
                eValue = LANGUAGEID.CFG_LANGUAGE_SPANISH;
                break;
            case 3:   
                eValue = LANGUAGEID.CFG_LANGUAGE_CHINESE;
                break;
            case 4:
                eValue = LANGUAGEID.CFG_LANGUAGE_JAPANESE;
                break;
            case 5:
                eValue = LANGUAGEID.CFG_LANGUAGE_KOREAN;
                break;
            case 6:
                eValue = LANGUAGEID.CFG_LANGUAGE_RUSSIAN;
                break;
            case 7:
                eValue = LANGUAGEID.CFG_LANGUAGE_THAI;
                break;
            case 8:
                eValue = LANGUAGEID.CFG_LANGUAGE_OTHER;
                break;
            case 9:
                eValue = LANGUAGEID.CFG_LANGUAGE_LANG_OFF;
                break;                
            default:
                eValue = LANGUAGEID.CFG_LANGUAGE_ENGLISH;
                break;                
        }
        return eValue;
    }
    @Override
    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        // TODO Auto-generated method stub
       // mmtksetting.GOpenDVPSet();
        if ((arg0.getKey()).equals(KEY_TVTYPE_SETING)) {
            int value = Integer.parseInt(arg1.toString());
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetTVType(value);
                Switch_TvType(value);
                editor.putInt(TVTYPE, value);
                editor.commit();
            }
        } else if ((arg0.getKey()).equals(KEY_PBCTYPE_SETTING)) {
            boolean flag = (Boolean) arg1;
            int value = 0;
            if (flag == true) {
                value = 1;
                mPbcType.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
                mmtksetting.SetPBCOn(value);
            } else {
                value = 0;
                mPbcType.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
                mmtksetting.SetPBCOn(value);
            }
            mPbcType.setChecked(flag);
            editor.putInt(PBCTYPE, value);
            editor.commit();
        } else if ((arg0.getKey()).equals(KEY_AUDIOLANTYPE_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            LANGUAGEID eValue = translateLanguageID(value);
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetAudioLanType(eValue.ordinal());
                Switch_AudioLanType(eValue, mAudioLanType);
                editor.putInt(AUDIOLANTYPE, eValue.ordinal());
                editor.commit();
            }
        } else if ((arg0.getKey()).equals(KEY_SUBLANTYPE_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            LANGUAGEID eValue = translateLanguageID(value);      
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetSubLanType(eValue.ordinal());
                Switch_AudioLanType(eValue, mSubLanType);
                editor.putInt(SUBLANTYPE, eValue.ordinal());
                editor.commit();
            }
        } else if ((arg0.getKey()).equals(KEY_MENULANTYPE_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            LANGUAGEID eValue = translateLanguageID(value); 
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetMenuLanType(eValue.ordinal());
                Switch_AudioLanType(eValue, mMenuLanType);
                editor.putInt(MENULANTYPE, eValue.ordinal());
                editor.commit();
            }
        } else if ((arg0.getKey()).equals(KEY_PARENTALTYPE_SETTING)) {
            int value = Integer.parseInt(arg1.toString());
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetParentalType(value);
                Switch_ParentType(value);
                editor.putInt(PARENTALTYPE, value);
                editor.commit();
                
                SharedPreferences sp = getSharedPreferences(
                        "password_preferences", Context.MODE_WORLD_READABLE);
                SharedPreferences.Editor edcon = sp.edit();
                edcon.putInt(PARENTALTYPE, value);
                edcon.commit();
            }
        }
      //  mmtksetting.GCloseDVPSet();
        return true;
    }

    public void Switch_TvType(int value) {
        switch (value) {
        case 0:
            mTvType.setSummary(R.string.STR_SETTINGS_TVTYPE_PAL);
            break;
        case 1:
            mTvType.setSummary(R.string.STR_SETTINGS_TVTYPE_MULTI);
            break;
        case 2:
            mTvType.setSummary(R.string.STR_SETTINGS_TVTYPE_NTSC);
            break;

        }
    }

    public void Switch_PbcType(int value) {
        switch (value) {
        case 0:
            mPbcType.setChecked(false);
            mPbcType.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_OFF);
            break;
        case 1:
            mPbcType.setChecked(true);
            mPbcType.setSummary(R.string.STR_SETTINGS_SRS_FOCUS_ON);
            break;
        }
    }

    public void Switch_AudioLanType(LANGUAGEID eValue, ListPreference mListPreference) {
        switch (eValue) {
        case CFG_LANGUAGE_ENGLISH:
            mListPreference.setSummary(R.string.STR_SETTINGS_SUBTI_ENG);
            break;
        case CFG_LANGUAGE_FRENCH:
            mListPreference.setSummary(R.string.STR_SETTINGS_SUBTI_FRN);
            break;
        case CFG_LANGUAGE_SPANISH:
            mListPreference.setSummary(R.string.STR_SETTINGS_SUBTI_SPN);
            break;
        case CFG_LANGUAGE_CHINESE:
            mListPreference.setSummary(R.string.STR_SETTINGS_AUDIO_CHN);
            break;
        case CFG_LANGUAGE_JAPANESE:
            mListPreference.setSummary(R.string.STR_SETTINGS_AUDIO_JPN);
            break;
        case CFG_LANGUAGE_OTHER:
            mListPreference.setSummary(R.string.STR_SETTINGS_AUDIO_OTHER);
            break;
        case CFG_LANGUAGE_KOREAN:
            mListPreference.setSummary(R.string.STR_SETTINGS_AUDIO_KRN);
            break;
        case CFG_LANGUAGE_RUSSIAN:
            mListPreference.setSummary(R.string.STR_SETTINGS_AUDIO_RUS);
            break;
        case CFG_LANGUAGE_THAI:
            mListPreference.setSummary(R.string.STR_SETTINGS_AUDIO_THI);
            break;
        case CFG_LANGUAGE_LANG_OFF:
            mListPreference.setSummary(R.string.STR_SETTINGS_DOLBY_OFF);
            break;
        }
    }

    public void Switch_ParentType(int value) {
        switch (value) {
        case 1:
            mParentalType.setSummary(R.string.STR_SETTINGS_PARENTAL_1);
            break;
        case 2:
            mParentalType.setSummary(R.string.STR_SETTINGS_PARENTAL_2);
            break;
        case 3:
            mParentalType.setSummary(R.string.STR_SETTINGS_PARENTAL_3);
            break;
        case 4:
            mParentalType.setSummary(R.string.STR_SETTINGS_PARENTAL_4);
            break;
        case 5:
            mParentalType.setSummary(R.string.STR_SETTINGS_PARENTAL_5);
            break;
        case 6:
            mParentalType.setSummary(R.string.STR_SETTINGS_PARENTAL_6);
            break;
        case 7:
            mParentalType.setSummary(R.string.STR_SETTINGS_PARENTAL_7);
            break;
        case 8:
            mParentalType.setSummary(R.string.STR_SETTINGS_PARENTAL_8);
            break;

        }
    }

    public void setParVisible() {
        mParentalType.setEnabled(true);
        int parentaltype = setting.getInt(PARENTALTYPE, 8);
        Switch_ParentType(parentaltype);
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
                        setParVisible();
                        repassword = true; 
                        dialog.dismiss();
                    } else {
                        Toast.makeText(InitialSettings.this,
                                R.string.dvd_errorpassword, Toast.LENGTH_LONG)
                                .show();
                        temp = "";
                    }
                } else {
                    if (temp.length() == 6){
                        SharedPreferences sp = getSharedPreferences(
                                "password_preferences", Context.MODE_WORLD_READABLE);
                        SharedPreferences.Editor edcon = sp.edit();
                        edcon.putString(PASSWORD, temp);
                        edcon.commit();
                        Toast.makeText(InitialSettings.this,
                                R.string.dvd_rightpassword, Toast.LENGTH_LONG)
                                .show();
                        dialog.dismiss();
                    }else{
                         Toast.makeText(InitialSettings.this,
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
