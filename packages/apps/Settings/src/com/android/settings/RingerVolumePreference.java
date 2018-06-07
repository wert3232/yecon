/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.settings;

import static android.os.BatteryManager.BATTERY_STATUS_UNKNOWN;

import com.android.internal.telephony.TelephonyIntents;
import com.android.settings.mtksetting.MyBinder;
import com.mtksetting.Array;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.VolumePreference;
import android.provider.Settings;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.media.AudioService;
/**
 * Special preference type that allows configuration of both the ring volume and
 * notification volume.
 */
public class RingerVolumePreference extends VolumePreference implements OnSeekBarChangeListener {
    private static final String TAG = "RingerVolumePreference";
    private boolean isBind = false;
    private static final int MSG_RINGER_MODE_CHANGED = 101;
    protected Context mContext;

    private SeekBarVolumizer [] mSeekBarVolumizer;

    private static final int[] SEEKBAR_ID = new int[] {
        R.id.media_volume_seekbar,
        R.id.rear_volume_seekbar,
        R.id.gis_volume_seekbar,
        R.id.backcar_volume_seekbar,
        R.id.bt_volume_seekbar,
        ///mtk68031 delete R.id.ringer_volume_seekbar,
        ///mtk68031 delete R.id.notification_volume_seekbar,
        ///mtk68031 delete R.id.alarm_volume_seekbar
    };

    private static final int[] SEEKBAR_TYPE = new int[] {
        AudioManager.STREAM_MUSIC,
        AudioManager.STREAM_MUSIC,
        AudioManager.STREAM_GIS,
        AudioManager.STREAM_BACKCAR,
        AudioManager.STREAM_BLUETOOTH_SCO,
        ///mtk68031 delete AudioManager.STREAM_RING,
        ///mtk68031 delete AudioManager.STREAM_NOTIFICATION,
        ///mtk68031 delete AudioManager.STREAM_ALARM
    };

    private static final int[] CHECKBOX_VIEW_ID = new int[] {
        R.id.media_mute_button,
        R.id.rear_mute_button,   
        R.id.gis_mute_button,
        R.id.backcar_mute_button,
        R.id.bt_mute_button,
        ///mtk68031 delete R.id.ringer_mute_button,
        ///mtk68031 delete R.id.notification_mute_button,
        ///mtk68031 delete R.id.alarm_mute_button
    };

    private static final int[] SEEKBAR_MUTED_RES_ID = new int[] {
        com.android.internal.R.drawable.ic_audio_vol_mute,
        com.android.internal.R.drawable.ic_audio_vol_mute,
        com.android.internal.R.drawable.ic_audio_vol_mute,
        com.android.internal.R.drawable.ic_audio_vol_mute,
        com.android.internal.R.drawable.ic_audio_vol_mute,
        ///mtk68031 delete com.android.internal.R.drawable.ic_audio_ring_notif_mute,
        ///mtk68031 delete com.android.internal.R.drawable.ic_audio_notification_mute,
        ///mtk68031 delete com.android.internal.R.drawable.ic_audio_alarm_mute
    };

    private static final int[] SEEKBAR_UNMUTED_RES_ID = new int[] {
        com.android.internal.R.drawable.ic_audio_vol,
        com.android.internal.R.drawable.ic_audio_vol,
        com.android.internal.R.drawable.ic_audio_vol,
        com.android.internal.R.drawable.ic_audio_vol,
        com.android.internal.R.drawable.ic_audio_vol,
        ///mtk68031 delete com.android.internal.R.drawable.ic_audio_ring_notif,
        ///mtk68031 delete com.android.internal.R.drawable.ic_audio_notification,
        ///mtk68031 delete com.android.internal.R.drawable.ic_audio_alarm
    };

    private SharedPreferences mPreferences;
    private static mtksetting mSettingService;
    
    private SeekBar frontSeekBar;
    private SeekBar rearSeekBar;
    private SeekBar gisSeekBar;
    private SeekBar backcarSeekBar;
    private SeekBar btSeekBar;
    
    private final static String FRONTPROGRESS_KEY = "frontprogress_key";
    private final static String REARPROGRESS_KEY = "rearprogress_key";
    private final static String GISPROGRESS_KEY = "gisprogress_key";
    private final static String BACKCARPROGRESS_KEY = "backcarprogress_key";
    private final static String BTPROGRESS_KEY = "btprogress_key";

    private int mFrontProgress = -1;
    private int mRearProgress = -1;
    private int mGISProgress = -1;
    private int mBackcarProgress = -1;
    private int mBTProgress = -1;
    
    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSettingService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            mSettingService = binder.getService();
        }
    };

    private ImageView[] mCheckBoxes = new ImageView[SEEKBAR_MUTED_RES_ID.length];
    private SeekBar[] mSeekBars = new SeekBar[SEEKBAR_ID.length];

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            updateSlidersAndMutedStates();
        }
    };

    @Override
    public void createActionButtons() {
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(null);
    }

    private void updateSlidersAndMutedStates() {
        for (int i = 0; i < SEEKBAR_TYPE.length; i++) {
            int streamType = SEEKBAR_TYPE[i];
            boolean muted = mAudioManager.isStreamMute(streamType);

            if (mCheckBoxes[i] != null) {
                if (streamType == AudioManager.STREAM_RING && muted
                        && mAudioManager.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER)) {
                    mCheckBoxes[i].setImageResource(
                            com.android.internal.R.drawable.ic_audio_ring_notif_vibrate);
                } else {
                    mCheckBoxes[i].setImageResource(
                            muted ? SEEKBAR_MUTED_RES_ID[i] : SEEKBAR_UNMUTED_RES_ID[i]);
                }
            }
            
            if (1 == i || 4 == i) {
                continue;
            }
            
            if (mSeekBars[i] != null) {
                final int volume = muted ? mAudioManager.getLastAudibleStreamVolume(streamType)
                        : mAudioManager.getStreamVolume(streamType);
                mSeekBars[i].setProgress(volume);
            }
        }
    }

    private BroadcastReceiver mRingModeChangedReceiver;
    private AudioManager mAudioManager;

    //private SeekBarVolumizer mNotificationSeekBarVolumizer;
    //private TextView mNotificationVolumeTitle;

    public RingerVolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
         mContext = context;

        // The always visible seekbar is for ring volume
        setStreamType(AudioManager.STREAM_RING);

        setDialogLayoutResource(R.layout.preference_dialog_ringervolume);
        //setDialogIcon(R.drawable.ic_settings_sound);

        mSeekBarVolumizer = new SeekBarVolumizer[SEEKBAR_ID.length];

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        mPreferences = context.getSharedPreferences(Array.MTKSettings,Context.MODE_MULTI_PROCESS);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        for (int i = 0; i < SEEKBAR_ID.length; i++) {
            SeekBar seekBar = (SeekBar) view.findViewById(SEEKBAR_ID[i]);
            mSeekBars[i] = seekBar;
            if (SEEKBAR_TYPE[i] == AudioManager.STREAM_MUSIC) {
                mSeekBarVolumizer[i] = new SeekBarVolumizer(getContext(), seekBar,
                        SEEKBAR_TYPE[i], getMediaVolumeUri(getContext()));
            } else {
                mSeekBarVolumizer[i] = new SeekBarVolumizer(getContext(), seekBar,
                        SEEKBAR_TYPE[i]);
            }
        }

        final int silentableStreams = System.getInt(getContext().getContentResolver(),
                System.MODE_RINGER_STREAMS_AFFECTED,
                ((1 << AudioSystem.STREAM_NOTIFICATION) | (1 << AudioSystem.STREAM_RING)));
        // Register callbacks for mute/unmute buttons
        for (int i = 0; i < mCheckBoxes.length; i++) {
            ImageView checkbox = (ImageView) view.findViewById(CHECKBOX_VIEW_ID[i]);
            mCheckBoxes[i] = checkbox;
        }

        // Load initial states from AudioManager
        updateSlidersAndMutedStates();
        // Listen for updates from AudioManager
        if (mRingModeChangedReceiver == null) {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
            mRingModeChangedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(action)) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_RINGER_MODE_CHANGED, intent
                                .getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1), 0));
                    }
                }
            };
            getContext().registerReceiver(mRingModeChangedReceiver, filter);
        }

        /*mtk68031 delete 
        // Disable either ringer+notifications or notifications
        int id;
        if (!Utils.isVoiceCapable(getContext())) {
            id = R.id.ringer_section;
        } else {
            id = R.id.notification_section;
        }
        View hideSection = view.findViewById(id);
        hideSection.setVisibility(View.GONE);
        */
        frontSeekBar = (SeekBar) view.findViewById(R.id.media_volume_seekbar);
        rearSeekBar = (SeekBar) view.findViewById(R.id.rear_volume_seekbar);
        gisSeekBar = (SeekBar) view.findViewById(R.id.gis_volume_seekbar);
        backcarSeekBar = (SeekBar) view.findViewById(R.id.backcar_volume_seekbar);
        btSeekBar = (SeekBar) view.findViewById(R.id.bt_volume_seekbar);
        
        frontSeekBar.setOnSeekBarChangeListener(this);
        rearSeekBar.setOnSeekBarChangeListener(this);
        gisSeekBar.setOnSeekBarChangeListener(this);
        backcarSeekBar.setOnSeekBarChangeListener(this);
        btSeekBar.setOnSeekBarChangeListener(this);

        frontSeekBar.setMax(AudioService.volumeMax);
        rearSeekBar.setMax(AudioService.volumeMax);
        gisSeekBar.setMax(AudioService.volumeMax);
        backcarSeekBar.setMax(AudioService.volumeMax);
        btSeekBar.setMax(AudioService.volumeMax);

        /**
         * jy 20150519 modify for volume
         */
        mFrontProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mFrontProgress == -1) {
            mFrontProgress = AudioService.volumeMax * 11 / AudioService.volumeMax;
        }
        frontSeekBar.setProgress(mFrontProgress);

        mRearProgress = mPreferences.getInt(REARPROGRESS_KEY, -1);
        if (mRearProgress == -1) {
            mRearProgress = AudioService.volumeMax * 11 / AudioService.volumeMax;
        }
        rearSeekBar.setProgress(mRearProgress);
        
        mGISProgress =  mAudioManager.getStreamVolume(AudioManager.STREAM_GIS);
        if (mGISProgress == -1) {
            mGISProgress = AudioService.volumeMax * 11 / AudioService.volumeMax;
        }
        gisSeekBar.setProgress(mGISProgress);

        mBackcarProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_BACKCAR);
        if (mBackcarProgress == -1) {
            mBackcarProgress = AudioService.volumeMax * 11 / AudioService.volumeMax;
        }
        backcarSeekBar.setProgress(mBackcarProgress);

        mBTProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO);
        if (mBTProgress == -1) {
            mBTProgress = AudioService.volumeMax * 11 / AudioService.volumeMax;
        }
        btSeekBar.setProgress(mBTProgress); 
        
        Intent serviceIntent = new Intent(mContext, mtksetting.class);
        isBind = mContext.bindService(serviceIntent, serviceConnection,
                Context.BIND_AUTO_CREATE);
        Log.i(TAG, "onBindDialogView: bindService isBind = " + isBind);
    }

    private Uri getMediaVolumeUri(Context context) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + context.getPackageName()
                + "/" + R.raw.media_volume);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (!positiveResult) {
            Log.w(TAG, "onDialogClosed, !positiveResult == true, mFrontProgress:" + mFrontProgress);
            for (SeekBarVolumizer vol : mSeekBarVolumizer) {
                if (vol != null) vol.revertVolume();
            }
            /**
             * jy 20150519 modify for volume
             */
            SharedPreferences.Editor editor = mPreferences.edit();
            mSettingService.SetRearVolume(mtksetting.getVolume(mRearProgress));
            editor.putInt(REARPROGRESS_KEY, mRearProgress);
            editor.commit();
            
            mFrontProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mFrontProgress, 0);
            
            mGISProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_GIS);
            mAudioManager.setStreamVolume(AudioManager.STREAM_GIS, mGISProgress, 0);

            mBackcarProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_BACKCAR);
            mAudioManager.setStreamVolume(AudioManager.STREAM_BACKCAR, mBackcarProgress, 0);

            mBTProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO);
            mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, mBTProgress, 0);
        }
        cleanup();
        if (isBind) {
            mContext.unbindService(serviceConnection);
            isBind = false;
            Log.d(TAG, "onDialogClosed: unbindService");
        }
    }

    @Override
    public void onActivityStop() {
        super.onActivityStop();
        for (SeekBarVolumizer vol : mSeekBarVolumizer) {
            if (vol != null) vol.stopSample();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        boolean isdown = (event.getAction() == KeyEvent.ACTION_DOWN);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onSampleStarting(SeekBarVolumizer volumizer) {
        super.onSampleStarting(volumizer);
        for (SeekBarVolumizer vol : mSeekBarVolumizer) {
            if (vol != null && vol != volumizer) vol.stopSample();
        }
    }

    private void startSample(SeekBar seekBar){
        for (SeekBarVolumizer vol : mSeekBarVolumizer) {
            if (vol != null && vol.getSeekBar() != seekBar) {
                vol.stopSample();
            } else if(vol != null && vol.getSeekBar() == seekBar) { 
                vol.stopSample();
                vol.startSample();
            }
        }
    }

    private void cleanup() {
        for (int i = 0; i < SEEKBAR_ID.length; i++) {
            if (mSeekBarVolumizer[i] != null) {
                Dialog dialog = getDialog();
                if (dialog != null && dialog.isShowing()) {
                    // Stopped while dialog was showing, revert changes
                    mSeekBarVolumizer[i].revertVolume();
                }
                mSeekBarVolumizer[i].stop();
                mSeekBarVolumizer[i] = null;
            }
        }
        if (mRingModeChangedReceiver != null) {
            getContext().unregisterReceiver(mRingModeChangedReceiver);
            mRingModeChangedReceiver = null;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        VolumeStore[] volumeStore = myState.getVolumeStore(SEEKBAR_ID.length);
        for (int i = 0; i < SEEKBAR_ID.length; i++) {
            SeekBarVolumizer vol = mSeekBarVolumizer[i];
            if (vol != null) {
                vol.onSaveInstanceState(volumeStore[i]);
            }
        }
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        VolumeStore[] volumeStore = myState.getVolumeStore(SEEKBAR_ID.length);
        for (int i = 0; i < SEEKBAR_ID.length; i++) {
            SeekBarVolumizer vol = mSeekBarVolumizer[i];
            if (vol != null) {
                vol.onRestoreInstanceState(volumeStore[i]);
            }
        }
    }

    private static class SavedState extends BaseSavedState {
        VolumeStore [] mVolumeStore;

        public SavedState(Parcel source) {
            super(source);
            mVolumeStore = new VolumeStore[SEEKBAR_ID.length];
            for (int i = 0; i < SEEKBAR_ID.length; i++) {
                mVolumeStore[i] = new VolumeStore();
                mVolumeStore[i].volume = source.readInt();
                mVolumeStore[i].originalVolume = source.readInt();
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            for (int i = 0; i < SEEKBAR_ID.length; i++) {
                dest.writeInt(mVolumeStore[i].volume);
                dest.writeInt(mVolumeStore[i].originalVolume);
            }
        }

        VolumeStore[] getVolumeStore(int count) {
            if (mVolumeStore == null || mVolumeStore.length != count) {
                mVolumeStore = new VolumeStore[count];
                for (int i = 0; i < count; i++) {
                    mVolumeStore[i] = new VolumeStore();
                }
            }
            return mVolumeStore;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * jy 20150519 modify for volume
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        // TODO Auto-generated method stub
        
        switch (seekBar.getId()) {

            case R.id.media_volume_seekbar:
                    mFrontProgress = progress;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mFrontProgress, 0);
//                    SharedPreferences.Editor editor_front = mPreferences.edit();
//                    editor_front.putInt(FRONTPROGRESS_KEY, mFrontProgress);
//                    editor_front.commit();
                break;

            case R.id.rear_volume_seekbar:
                    mRearProgress = progress;
                    
                    if (mSettingService == null) {
                        return;
                    }
                    
                    mSettingService.SetRearVolume(mtksetting.getVolume(mRearProgress));
					SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putInt(REARPROGRESS_KEY, mRearProgress);
                    editor.commit();
                break;

            case R.id.gis_volume_seekbar:
                mGISProgress = progress;
                mAudioManager.setStreamVolume(AudioManager.STREAM_GIS, mGISProgress, 0);
//				SharedPreferences.Editor editor_gis = mPreferences.edit();
//                editor_gis.putInt(GISPROGRESS_KEY, mGISProgress);
//                editor_gis.commit();
                break;

            case R.id.backcar_volume_seekbar:
                mBackcarProgress = progress;
                mAudioManager.setStreamVolume(AudioManager.STREAM_BACKCAR, mBackcarProgress, 0);
//				SharedPreferences.Editor editor_bc = mPreferences.edit();
//                editor_bc.putInt(BACKCARPROGRESS_KEY, mBackcarProgress);
//                editor_bc.commit();
                break;

            case R.id.bt_volume_seekbar:
                mBTProgress = progress;
                mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, mBTProgress, 0);
//				SharedPreferences.Editor editor_bt = mPreferences.edit();
//                editor_bt.putInt(BTPROGRESS_KEY, mBTProgress);
//                editor_bt.commit();
                break;
        }
        updateSlidersAndMutedStates();
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }
}
