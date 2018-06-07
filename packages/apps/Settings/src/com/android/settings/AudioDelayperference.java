package com.android.settings;

import com.android.settings.R;

import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.media.AudioManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.VolumePreference;
import android.preference.Preference.BaseSavedState;
import android.preference.VolumePreference.SeekBarVolumizer;
import android.preference.VolumePreference.VolumeStore;
import android.provider.Settings;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class AudioDelayperference extends VolumePreference {


    private TextView mNotificationVolumeTitle;

    public AudioDelayperference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setStreamType(AudioManager.STREAM_RING);
        setDialogLayoutResource(R.layout.preference_dialog_audiodelay);
        setDialogIcon(R.drawable.ic_settings_sound);
    }
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mNotificationVolumeTitle = (TextView) view.findViewById(R.id.notification_volume_title);
    }
    
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setNotificationVolumeVisibility(!isChecked);
        Settings.System.putInt(getContext().getContentResolver(),
                Settings.System.NOTIFICATIONS_USE_RING_VOLUME, isChecked ? 1 : 0);
        if (isChecked) {
            // The user wants the notification to be same as ring, so do a
            // one-time sync right now
            AudioManager audioManager = (AudioManager) getContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                    audioManager.getStreamVolume(AudioManager.STREAM_RING), 0);
        }
    }


    private void setNotificationVolumeVisibility(boolean visible) {
        mNotificationVolumeTitle.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}
