package com.autochips.bluetooth.Fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.Fragment;

public class BaseFragment extends Fragment {
    // sava the valuse
    public SharedPreferences.Editor editor;
    public SharedPreferences uiState;

    public void initSharePF() {
        uiState = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = uiState.edit();
    }

    public void saveCheck(String str, boolean mValue) {
        editor.putBoolean(str, mValue);
        editor.commit();
    }

    public boolean getCheckValues(String str) {

        return uiState.getBoolean(str, false);

    }

    public void saveIntValue(String str, int mValue) {
        editor.putInt(str, mValue);
        editor.commit();
    }

    public int getIntValue(String str) {
        return uiState.getInt(str, 0);
    }

    public String getStringValue(String str, String defaulValue) {
        return uiState.getString(str, defaulValue);
    }

    public void saveStringValue(String str, String mValue) {
        editor.putString(str, mValue);
        editor.commit();
    }

}
