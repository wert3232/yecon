/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.settings.deviceinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentQueryMap;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.mtksetting.Array;
/**
 * USB storage settings.
 */
public class UsbSettings extends SettingsPreferenceFragment {

    private static final String TAG = "UsbSettings";

    private static final String KEY_MTP = "usb_mtp";
    private static final String KEY_PTP = "usb_ptp";
    
    private static final String KEY_USBDEVICE = "disk drive";

    private UsbManager mUsbManager;
    private CheckBoxPreference mMtp;
    private CheckBoxPreference mPtp;
    
    private CheckBoxPreference mDtd;
    SharedPreferences.Editor editor;
    private static final String KEY_USBDEVICE_VALUE = "usbdevice_value";
    private boolean mUsbDeviceValue;

    private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context content, Intent intent) {
            updateToggles(mUsbManager.getDefaultFunction());
        }
    };

    private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen root = getPreferenceScreen();
        if (root != null) {
            root.removeAll();
        }
        addPreferencesFromResource(R.xml.usb_settings);
        root = getPreferenceScreen();

        mMtp = (CheckBoxPreference)root.findPreference(KEY_MTP);
        mPtp = (CheckBoxPreference)root.findPreference(KEY_PTP);
        //Begin : Added by fei.liu
        //Remove MTP,PTP Settings
			if(mMtp!=null){
            root.removePreference(mMtp);
            }
            if(mPtp!=null){
            root.removePreference(mPtp);
        }
        //End : Added by fei.liu        
        mDtd = (CheckBoxPreference)root.findPreference(KEY_USBDEVICE);
        
        Context context = getActivity();
        SharedPreferences uiState = context.getSharedPreferences(Array.MTKSettings, context.MODE_PRIVATE);

		if(null != uiState){
        	editor = uiState.edit();
		}
		
        mUsbDeviceValue = uiState.getBoolean(KEY_USBDEVICE_VALUE, true);
        if( mUsbDeviceValue ){
        	//SystemProperties.set("sys.usb.config", "adb,mass_storage");
        	if(null != mDtd){
    			mDtd.setChecked(true);
        	}
        }
        else{
        	//SystemProperties.set("sys.usb.config", "adb");
        	if(null != mDtd){
    			mDtd.setChecked(false);
        	}
        }

        return root;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mStateReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Make sure we reload the preference hierarchy since some of these settings
        // depend on others...
        createPreferenceHierarchy();

        // ACTION_USB_STATE is sticky so this will call updateToggles
        getActivity().registerReceiver(mStateReceiver,
                new IntentFilter(UsbManager.ACTION_USB_STATE));
    }

    private void updateToggles(String function) {
        if (UsbManager.USB_FUNCTION_MTP.equals(function)) {
            mMtp.setChecked(true);
            mPtp.setChecked(false);
        } else if (UsbManager.USB_FUNCTION_PTP.equals(function)) {
            mMtp.setChecked(false);
            mPtp.setChecked(true);
        } else {
            mMtp.setChecked(false);
            mPtp.setChecked(false);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        // Don't allow any changes to take effect as the USB host will be disconnected, killing
        // the monkeys
        if (Utils.isMonkeyRunning()) {
            return true;
        }
        // temporary hack - using check boxes as radio buttons
        // don't allow unchecking them
        if (preference instanceof CheckBoxPreference) {
            CheckBoxPreference checkBox = (CheckBoxPreference)preference;
            if (!checkBox.isChecked() && preference != mDtd) {
                checkBox.setChecked(true);
                return true;
            }
        }
        if (preference == mMtp) {
            mUsbManager.setCurrentFunction(UsbManager.USB_FUNCTION_MTP, true);
            updateToggles(UsbManager.USB_FUNCTION_MTP);
        } else if (preference == mPtp) {
            mUsbManager.setCurrentFunction(UsbManager.USB_FUNCTION_PTP, true);
            updateToggles(UsbManager.USB_FUNCTION_PTP);
        } else if (preference == mDtd) {
            if (mDtd.isChecked() ) {
                SystemProperties.set("sys.usb.config", "adb,mass_storage");
                changeMassNotification(true);
                editor.putBoolean(KEY_USBDEVICE_VALUE, true);
                mDtd.setChecked(true);
            } else {
                SystemProperties.set("sys.usb.config", "adb");
                changeMassNotification(false);
                editor.putBoolean(KEY_USBDEVICE_VALUE, false);
                mDtd.setChecked(false);
            }
            editor.commit();
        }
        return true;
    }

    public static boolean mAdbAndMassStorageNotificationShown;
    public void changeMassNotification(boolean showFlag) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final int id = com.android.internal.R.string.adb_active_notification_title;
        if (showFlag && !mAdbAndMassStorageNotificationShown) {
            Resources r = getResources();
            CharSequence title = r.getText(id);
            CharSequence message = r.getText(com.android.internal.R.string.adb_active_notification_message);
            Notification notification = new Notification();
            notification.icon = com.android.internal.R.drawable.stat_sys_adb;
            notification.when = 0;
            notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
            notification.tickerText = title;
            notification.defaults = 0; // please be quiet
            notification.sound = null;
            notification.vibrate = null;
            Intent intent = Intent.makeRestartActivityTask(new ComponentName("com.android.settings", "com.android.settings.UsbSettings"));
            PendingIntent pi = PendingIntent.getActivity(getActivity(), 0, intent, 0); 
            notification.setLatestEventInfo(getActivity(), title, message, pi);
            mAdbAndMassStorageNotificationShown = true;
            notificationManager.notify(id, notification);
        } else if (mAdbAndMassStorageNotificationShown) {
            mAdbAndMassStorageNotificationShown = false;
            notificationManager.cancel(id);
        }
    }
}
