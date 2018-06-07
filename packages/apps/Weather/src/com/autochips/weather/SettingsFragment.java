package com.autochips.weather;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener {

    public final static String KEY_SELECT_CITY = "select_city";

    private Preference mSelectCityPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_headers);

        mSelectCityPref = (Preference) findPreference(KEY_SELECT_CITY);
        mSelectCityPref.setSummary(getText(R.string.current_city)
                + getCurrentCity());
        getActivity().getApplicationContext().startService(
                new Intent(getActivity(), WeatherService.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            List<CountryRegion> list = intent
                    .getParcelableArrayListExtra("listParce");
            if (list != null && list.size() > 0) {
                mSelectCityPref.setSummary(getText(R.string.current_city)
                        + StringUtil.replaceSymbol(list.get(list.size() - 1)
                                .getTitle()));
                saveCurrentCity(list);
            }
        }

    }

    private String getCurrentCity() {
        Dao dao = DBFactory.getInstance(getActivity());
        CountryRegion bean = dao.getCurrentCity();
        if (Constants.IS_EN) {
            return StringUtil.replaceSymbol(bean.getCityNameEn());
        } else {
            return StringUtil.replaceSymbol(bean.getCityName());
        }
    }

    private void saveCurrentCity(List<CountryRegion> list) {
        Dao dao = DBFactory.getInstance(getActivity());
        CountryRegion bean = new CountryRegion();
        for (CountryRegion cr : list) {
            switch (cr.getRegionLevel()) {
            case CountryRegion.REGION_LEVEL_COUNTRY:
                bean.setCountryName(cr.getCountryName());
                bean.setCountryNameEn(cr.getCountryNameEn());
                bean.setCountryCode(cr.getCountryCode());
                break;
            case CountryRegion.REGION_LEVEL_STATE:
                bean.setStateName(cr.getStateName());
                bean.setStateNameEn(cr.getStateNameEn());
                bean.setStateCode(cr.getStateCode());
                break;
            case CountryRegion.REGION_LEVEL_CITY:
                bean.setCityName(cr.getCityName());
                bean.setCityNameEn(cr.getCityNameEn());
                bean.setCityCode(cr.getCityCode());
                bean.setWoeid(cr.getWoeid());
                break;
            }
        }
        dao.saveOrUpdateCurrentCity(bean);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

}
