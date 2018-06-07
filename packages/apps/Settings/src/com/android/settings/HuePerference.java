package com.android.settings;

import com.android.settings.R;
import com.android.settings.mtksetting.MyBinder;
import com.mtksetting.Array;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.SeekBarDialogPreference;
import android.preference.SeekBarPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class HuePerference extends SeekBarDialogPreference implements
		SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener {
	private SeekBar mSeekBar;
	private TextView mTextView;
	private static mtksetting mmtksetting;
	private Intent serviceIntent;
	private Context mContext;
	private int mCurrent;
	private static final String HUE = "hue";
	SharedPreferences.Editor editor;
	int arg0;
	private boolean IsBind = false;
	private static final String TAG = "HuePerference";

	public HuePerference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setDialogLayoutResource(R.layout.preference_dialog_contrast);
		mContext = context;
	}

	private static ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			mmtksetting = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder binder = (MyBinder) service;
			mmtksetting = binder.getService();
		}
	};

	protected void onBindDialogView(View view) {
		// TODO Auto-generated method stub
		super.onBindDialogView(view);
		SharedPreferences uiState = mContext.getSharedPreferences(
				Array.MTKSettings, mContext.MODE_PRIVATE);
		editor = uiState.edit();
		mSeekBar = getSeekBar(view);
		mTextView = (TextView) view.findViewById(R.id.textview);
		mSeekBar.setMax(100);
		SharedPreferences setting = getSharedPreferences();
		if( setting == null )
			return;
		String hueinit = setting.getString("hueinit", "");
		arg0 = setting.getInt(HUE, 0);
		String huevalue = String.valueOf(arg0);
		if (hueinit.equals("")) {
			// give the arg0 a value that refer to process.
			arg0 = 50;
			mSeekBar.setProgress(50);
			mTextView.setText(String.valueOf(50));
			editor.putInt(HUE, 50);
			editor.commit();

			final ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putInt(resolver,
            Settings.System.SCREEN_HUE, arg0);
		} else {
			mSeekBar.setProgress(arg0);
			mTextView.setText(huevalue);
			final ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putInt(resolver,
            Settings.System.SCREEN_HUE, arg0);
		}
		mSeekBar.setOnSeekBarChangeListener(this);
		serviceIntent = new Intent(mContext, mtksetting.class);
		IsBind = mContext.bindService(serviceIntent, serviceConnection,
				Context.BIND_AUTO_CREATE);
		if(IsBind)
			Log.e(TAG, "[Lingling]HuePerference:onBindDialogView:bindService OK:||IsBind = "+IsBind);//Lingling
		else
			Log.e(TAG, "[Lingling]HuePerference:onBindDialogView:bindService FAIL:||IsBind = "+IsBind);//Lingling
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {
		mCurrent = progress;
		mTextView.setText(String.valueOf(progress));
		if (mmtksetting == null) {

		} else {
			seekBar.setProgress(progress);
			mmtksetting.GSetHueLevel(progress);
			editor.putInt(HUE, mCurrent);
			editor.commit();
			final ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putInt(resolver,
            Settings.System.SCREEN_HUE, progress);
		}

	}
/*	
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			Log.e("tag", "msg-----------------------wuxin------backlight11===="
					+ which);

		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			mmtksetting.GSetHueLevel(arg0);
			editor.putInt(HUE, arg0);
			editor.commit();
			Log.e("tag", "msg-----------------------wuxin------backlight22===="
					+ which);

		}
	}*/
	protected View onCreateDialogView() {
		// TODO Auto-generated method stub
		return super.onCreateDialogView();

	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// NA
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// NA
	}

	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		// Begin: Modify by jiangbo.wu@archermind.com
		// Modity to response the Button event.
		SharedPreferences uiState = mContext.getSharedPreferences(
					Array.MTKSettings, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = uiState.edit();
		if( !positiveResult ){
			mmtksetting.GSetHueLevel(arg0);

			// restore the old value.
			editor.putInt(HUE, arg0);
		}

		editor.commit();
		//End
		
		if(IsBind)
			{
				mContext.unbindService(serviceConnection);
				IsBind = false;
				Log.e(TAG, "[Lingling]onDialogClosed:unbindService");			

		}//Lingling
	}

	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		super.onDismiss(dialog);
		editor.putString("hueinit", "hueinit");
		editor.commit();
	}
}
