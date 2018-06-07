/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.settings.wfd;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import com.android.internal.app.ActionBarImpl;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.ViewGroup.LayoutParams;






//import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;//00
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplayStatus;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;//0+0
import android.net.wifi.p2p.WifiP2pManager.ActionListener;//0+0
import android.net.wifi.p2p.WifiP2pManager.Channel;//0+0
import android.net.wifi.WpsInfo;//0+0
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;//0+0
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;//00
import android.preference.ListPreference;//00
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Html;
import android.util.Slog;//00
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;//00
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;//00
import android.view.ViewGroup;//00
import android.widget.Button;//00
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.settings.ProgressCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
//import com.autochips.featureoption.FeatureOption;
import com.android.settings.wfd.WfdSettingsExt;
import android.util.Log;


/**
 * The Settings screen for WifiDisplay configuration and connection management.
 */
public final class WifiDisplaySettings extends SettingsPreferenceFragment {
    private static final String TAG = "WifiDisplaySettings";

    //private static final int MENU_ID_SCAN = Menu.FIRST;
	private static final int MENU_ID_ENABLE_WIFI_DISPLAY = Menu.FIRST;

    private DisplayManager mDisplayManager;

    private boolean mWifiDisplayOnSetting;
    private WifiDisplayStatus mWifiDisplayStatus;

    private PreferenceGroup mPairedDevicesCategory;
    private ProgressCategory mAvailableDevicesCategory;

    private TextView mEmptyView;

    private Switch mActionBarSwitch;


	//A: add WfdSettingsExt instance
    private WfdSettingsExt mExt;


    public WifiDisplaySettings() {
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

		//lingling add//****************************
		/*Activity activity = getActivity();

		activity.setContentView(R.layout.main);
			
		Log.d(TAG, "lingling_onCreate()");

		ActionBar actionBar = activity.getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setDisplayShowTitleEnabled(false);

			//Tab edit = actionBar.newTab().setText("EDIT");
		Tab edit = actionBar.newTab().setIcon(R.drawable.ic_sync_green_holo);


		MyTabListener editListener = new MyTabListener(new EditFragment());
		edit.setTabListener(editListener);


		actionBar.addTab(edit);*/

		//lingling add//****************************

		Log.e("[lingling]onCreate","wifidisplaysettings");

		//new WfdSettingsExt for ATC WFD feature
        //if(FeatureOption.ATC_WFD_SUPPORT) {        
        if (isWifiDisplayEnabled()){
            mExt = new WfdSettingsExt(getActivity());			
        }	
        //}

        mDisplayManager = (DisplayManager)getActivity().getSystemService(Context.DISPLAY_SERVICE);

        addPreferencesFromResource(R.xml.wifi_display_settings);
        setHasOptionsMenu(true);
    }

	//lingling add //*******************************************
	/*class MyTabListener implements TabListener
	{
		private Fragment fragment;

		public MyTabListener(Fragment fragment)
		{
			this.fragment = fragment;

		}

		public void onTabReselected(Tab tab, FragmentTransaction ft)
		{}

		public void onTabSelected(Tab tab,FragmentTransaction ft)
		{


			ft.add(R.id.context, fragment, null);

		}


		public void onTabUnselected(Tab tab,FragmentTransaction ft)
		{
			ft.remove(fragment);

		}

	}*/

	//lingling add //**********************************************

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		Log.e("[lingling]onActivityCreated 1","wifidisplaysettings");

        Activity activity = getActivity();
        mActionBarSwitch = new Switch(activity);
        if (activity instanceof PreferenceActivity) {
			Log.e("[lingling]onActivityCreated 2","wifidisplaysettings");
            PreferenceActivity preferenceActivity = (PreferenceActivity) activity;
            if (preferenceActivity.onIsHidingHeaders() || !preferenceActivity.onIsMultiPane()) {
                final int padding = activity.getResources().getDimensionPixelSize(
                        R.dimen.action_bar_switch_padding);
                mActionBarSwitch.setPadding(0, 0, padding, 0);
                activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                        ActionBar.DISPLAY_SHOW_CUSTOM);
                activity.getActionBar().setCustomView(mActionBarSwitch,
                        new ActionBar.LayoutParams(
                                ActionBar.LayoutParams.WRAP_CONTENT,
                                ActionBar.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER_VERTICAL | Gravity.END));
            }
        }

        mActionBarSwitch.setOnCheckedChangeListener(mSwitchOnCheckedChangedListener);

        mEmptyView = (TextView) getView().findViewById(android.R.id.empty);
        getListView().setEmptyView(mEmptyView);

        update();

        if (mWifiDisplayStatus.getFeatureState() == WifiDisplayStatus.FEATURE_STATE_UNAVAILABLE) {
            activity.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = getActivity();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED);
        context.registerReceiver(mReceiver, filter);

        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(
                Settings.Global.WIFI_DISPLAY_ON), false, mSettingsObserver);

        //mDisplayManager.scanWifiDisplays();

        update();
		///A:WFD sink support {@
		if(mExt!=null){
			mExt.onResume();
		}
		///@}
    }

    @Override
    public void onPause() {
        super.onPause();

		///A:WFD sink support {@
		if(mExt!=null){
			mExt.onPause();
		}
		///@}

        Context context = getActivity();
        context.unregisterReceiver(mReceiver);

        getContentResolver().unregisterContentObserver(mSettingsObserver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e("[lingling]onCreateOptionsMenu","wifidisplaysettings");
		if (mExt != null){
			MenuItem item = menu.add(Menu.NONE, MENU_ID_ENABLE_WIFI_DISPLAY, 0, R.string.wifi_display_enable_menu_item);
			item.setCheckable(true);
        	item.setChecked(mWifiDisplayOnSetting);
		}	
		//if(mExt != null) {
            //mExt.onCreateOptionMenu(menu, mWifiDisplayStatus);
        //}
        super.onCreateOptionsMenu(menu, inflater);
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_ENABLE_WIFI_DISPLAY:
				Log.e("[lingling]onOptionsItemSelected","wifidisplaysettings");
                mWifiDisplayOnSetting = !item.isChecked();
                item.setChecked(mWifiDisplayOnSetting);
                Settings.Global.putInt(getContentResolver(),
                        Settings.Global.WIFI_DISPLAY_ON, mWifiDisplayOnSetting ? 1 : 0);
                return true;
        }

        //if(mExt != null && mExt.onOptionMenuSelected(item, getFragmentManager())) {
            //return true;
        //} else {
            return super.onOptionsItemSelected(item);
        //}
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference instanceof WifiDisplayPreference) {
            WifiDisplayPreference p = (WifiDisplayPreference)preference;
            WifiDisplay display = p.getDisplay();

            if (display.equals(mWifiDisplayStatus.getActiveDisplay())) {
                showDisconnectDialog(display);
            } else {
                mDisplayManager.connectWifiDisplay(display.getDeviceAddress());
            }
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void update() {
        mWifiDisplayOnSetting = Settings.Global.getInt(getContentResolver(),
                Settings.Global.WIFI_DISPLAY_ON, 0) != 0;
        mWifiDisplayStatus = mDisplayManager.getWifiDisplayStatus();

        applyState();
    }

    private void applyState() {
        final int featureState = mWifiDisplayStatus.getFeatureState();
        mActionBarSwitch.setEnabled(featureState != WifiDisplayStatus.FEATURE_STATE_DISABLED);
        mActionBarSwitch.setChecked(mWifiDisplayOnSetting);

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();

		///A:WFD sink support {@
        PreferenceCategory category = null;
        if (mExt != null) {
            boolean added = mExt.addAdditionalPreference(
                preferenceScreen, mWifiDisplayStatus != null
                    && mWifiDisplayStatus.getFeatureState() == WifiDisplayStatus.FEATURE_STATE_ON);
            if (added) {
                Preference pre = preferenceScreen
                        .getPreference(preferenceScreen.getPreferenceCount() - 1);
                if (pre != null && pre instanceof PreferenceCategory) {
                    category = (PreferenceCategory) pre;
                }
            }
        }

		///@}



        if (featureState == WifiDisplayStatus.FEATURE_STATE_ON) {
            final WifiDisplay[] pairedDisplays = mWifiDisplayStatus.getRememberedDisplays();
            final WifiDisplay[] availableDisplays = mWifiDisplayStatus.getAvailableDisplays();

            if (mPairedDevicesCategory == null) {
                mPairedDevicesCategory = new PreferenceCategory(getActivity());
                mPairedDevicesCategory.setTitle(R.string.wifi_display_paired_devices);
            } else {
                mPairedDevicesCategory.removeAll();
            }
            preferenceScreen.addPreference(mPairedDevicesCategory);

            for (WifiDisplay d : pairedDisplays) {
                mPairedDevicesCategory.addPreference(createWifiDisplayPreference(d, true));
            }
            if (mPairedDevicesCategory.getPreferenceCount() == 0) {
                preferenceScreen.removePreference(mPairedDevicesCategory);
            }

            if (mAvailableDevicesCategory == null) {
                mAvailableDevicesCategory = new ProgressCategory(getActivity(), null,
                        R.string.wifi_display_no_devices_found);
                mAvailableDevicesCategory.setTitle(R.string.wifi_display_available_devices);
            } else {
                mAvailableDevicesCategory.removeAll();
            }
            preferenceScreen.addPreference(mAvailableDevicesCategory);

            for (WifiDisplay d : availableDisplays) {
                if (!contains(pairedDisplays, d.getDeviceAddress())) {
                    mAvailableDevicesCategory.addPreference(createWifiDisplayPreference(d, false));
                }
            }
            if (mWifiDisplayStatus.getScanState() == WifiDisplayStatus.SCAN_STATE_SCANNING) {
                mAvailableDevicesCategory.setProgress(true);
            } else {
                mAvailableDevicesCategory.setProgress(false);
            }
        } else {
            mEmptyView.setText(featureState == WifiDisplayStatus.FEATURE_STATE_OFF ?
                    R.string.wifi_display_settings_empty_list_wifi_display_off :
                            R.string.wifi_display_settings_empty_list_wifi_display_disabled);
        }

        getActivity().invalidateOptionsMenu();
    }

    private Preference createWifiDisplayPreference(final WifiDisplay d, boolean paired) {
        WifiDisplayPreference p = new WifiDisplayPreference(getActivity(), d);
        if (d.equals(mWifiDisplayStatus.getActiveDisplay())) {
            switch (mWifiDisplayStatus.getActiveDisplayState()) {
                case WifiDisplayStatus.DISPLAY_STATE_CONNECTED:
                    p.setSummary(R.string.wifi_display_status_connected);
                    break;
                case WifiDisplayStatus.DISPLAY_STATE_CONNECTING:
                    p.setSummary(R.string.wifi_display_status_connecting);
                    break;
            }
        } else if (paired && contains(mWifiDisplayStatus.getAvailableDisplays(),
                d.getDeviceAddress())) {
            p.setSummary(R.string.wifi_display_status_available);
        }
        if (paired) {
            p.setWidgetLayoutResource(R.layout.wifi_display_preference);
        }
        return p;
    }

    private void showDisconnectDialog(final WifiDisplay display) {
        DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (display.equals(mWifiDisplayStatus.getActiveDisplay())) {
                    mDisplayManager.disconnectWifiDisplay();
                }
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle(R.string.wifi_display_disconnect_title)
                .setMessage(Html.fromHtml(getResources().getString(
                        R.string.wifi_display_disconnect_text, display.getFriendlyDisplayName())))
                .setPositiveButton(android.R.string.ok, ok)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void showOptionsDialog(final WifiDisplay display) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.wifi_display_options, null);
        final EditText nameEditText = (EditText)view.findViewById(R.id.name);
        nameEditText.setText(display.getFriendlyDisplayName());

        DialogInterface.OnClickListener done = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString().trim();
                if (name.isEmpty() || name.equals(display.getDeviceName())) {
                    name = null;
                }
                mDisplayManager.renameWifiDisplay(display.getDeviceAddress(), name);
            }
        };
        DialogInterface.OnClickListener forget = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDisplayManager.forgetWifiDisplay(display.getDeviceAddress());
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle(R.string.wifi_display_options_title)
                .setView(view)
                .setPositiveButton(R.string.wifi_display_options_done, done)
                .setNegativeButton(R.string.wifi_display_options_forget, forget)
                .create();
        dialog.show();
    }

    private static boolean contains(WifiDisplay[] displays, String address) {
        for (WifiDisplay d : displays) {
            if (d.getDeviceAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

	
    private boolean isWifiDisplayEnabled() {
        String prop = SystemProperties.get("persist.wfd.enabled");
        return prop != null && prop.equals("1");
    }

    private final CompoundButton.OnCheckedChangeListener mSwitchOnCheckedChangedListener =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mWifiDisplayOnSetting = isChecked;
            Settings.Global.putInt(getContentResolver(),
                    Settings.Global.WIFI_DISPLAY_ON, isChecked ? 1 : 0);
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED)) {
                WifiDisplayStatus status = (WifiDisplayStatus)intent.getParcelableExtra(
                        DisplayManager.EXTRA_WIFI_DISPLAY_STATUS);
                mWifiDisplayStatus = status;
                applyState();
            }
        }
    };

    private final ContentObserver mSettingsObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            update();
        }
    };

    private final class WifiDisplayPreference extends Preference
            implements View.OnClickListener {
        private final WifiDisplay mDisplay;

        public WifiDisplayPreference(Context context, WifiDisplay display) {
            super(context);

            mDisplay = display;
            setTitle(display.getFriendlyDisplayName());
        }

        public WifiDisplay getDisplay() {
            return mDisplay;
        }

        @Override
        protected void onBindView(View view) {
            super.onBindView(view);

            ImageView deviceDetails = (ImageView) view.findViewById(R.id.deviceDetails);
            if (deviceDetails != null) {
                deviceDetails.setOnClickListener(this);

                if (!isEnabled()) {
                    TypedValue value = new TypedValue();
                    getContext().getTheme().resolveAttribute(android.R.attr.disabledAlpha,
                            value, true);
                    deviceDetails.setImageAlpha((int)(value.getFloat() * 255));
                }
            }
        }

        @Override
        public void onClick(View v) {
            showOptionsDialog(mDisplay);
        }
    }
}
