package com.android.settings;

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

import com.android.settings.mtksetting.MyBinder;
import com.mtksetting.Array;

public class SaturationPerference extends SeekBarDialogPreference implements
		SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener {
	private SeekBar mSeekBar;
	private TextView mTextView;
	private static mtksetting mmtksetting;
	private Intent serviceIntent;
	private Context mContext;
	private int mCurrent;
	private static final String SATURATION = "saturation";
	SharedPreferences.Editor editor;
	int arg0;
	private boolean IsBind = false;
	private static final String TAG = "SaturationPerference";

	public SaturationPerference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setDialogLayoutResource(R.layout.preference_dialog_contrast);
		mContext = context;
	}

	private static ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			Log.e(TAG, "[Lingling]SaturationPerference: onServiceDisconnected");//Lingling
			mmtksetting = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e(TAG, "[Lingling]SaturationPerference: onServiceConnected");//Lingling
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
		arg0 = setting.getInt(SATURATION, 0);
		String satruationvalue = String.valueOf(arg0);
		String sat = setting.getString("sat", "");
		if (sat.equals("")) {
			// give the arg0 a value that refer to process.
			arg0 = 50;
			mTextView.setText(String.valueOf(50));
			mSeekBar.setProgress(50);
			editor.putInt(SATURATION, 50);
			editor.commit();
			final ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putInt(resolver,
            Settings.System.SCREEN_SATURATION, arg0);
			
			//Begin : Added by vend00102 2012.07.16
            SharedPreferences sp = mContext.getSharedPreferences(
                    "com.android.color_preferences", mContext.MODE_WORLD_READABLE);
            SharedPreferences.Editor edcon = sp.edit();
            edcon.putInt(SATURATION, 50);
            edcon.commit();
            //Begin : Added by vend00102 2012.07.16
		} else {
			mTextView.setText(satruationvalue);
			mSeekBar.setProgress(arg0);
			final ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putInt(resolver,
            Settings.System.SCREEN_SATURATION, arg0);
		}

		mSeekBar.setOnSeekBarChangeListener(this);
		serviceIntent = new Intent(mContext, mtksetting.class);
		IsBind = mContext.bindService(serviceIntent, serviceConnection,
				Context.BIND_AUTO_CREATE);
		if(IsBind)
			Log.e(TAG, "[Lingling]SaturationPerference:onBindDialogView:bindService OK:||IsBind = "+IsBind);//Lingling
		else
			Log.e(TAG, "[Lingling]SaturationPerference:onBindDialogView:bindService FAIL:||IsBind = "+IsBind);//Lingling
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {
		mCurrent = progress;
		mTextView.setText(String.valueOf(progress));
		if (mmtksetting == null) {

		} else {
			seekBar.setProgress(progress);
			mmtksetting.GSetSaturationLevel(progress);
			editor.putInt(SATURATION, mCurrent);
			editor.commit();
			final ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putInt(resolver,
            Settings.System.SCREEN_SATURATION, mCurrent);
			
			//Begin : Added by vend00102 2012.07.16
            SharedPreferences sp = mContext.getSharedPreferences(
                    "com.android.color_preferences", mContext.MODE_WORLD_READABLE);
            SharedPreferences.Editor edcon = sp.edit();
            edcon.putInt(SATURATION, mCurrent);
            edcon.commit();
            //Begin : Added by vend00102 2012.07.16
		}

	}
/*
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			Log.e("tag", "msg-----------------------wuxin------backlight11===="
					+ which);

		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			mmtksetting.GSetSaturationLevel(arg0);
			editor.putInt(SATURATION, arg0);
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
		Log.e(TAG, "[Lingling]SaturationPerference: onDialogClosed");//Lingling

		// Begin: Modify by jiangbo.wu@archermind.com
		// Modity to response the Button event.
		SharedPreferences uiState = mContext.getSharedPreferences(
					Array.MTKSettings, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = uiState.edit();
		if( !positiveResult ){
			mmtksetting.GSetSaturationLevel(arg0);

			// restore the old value.
			editor.putInt(SATURATION, arg0);
			
			//Begin : Added by vend00102 2012.07.16
            SharedPreferences sp = mContext.getSharedPreferences(
                    "com.android.color_preferences", mContext.MODE_WORLD_READABLE);
            SharedPreferences.Editor edcon = sp.edit();
            edcon.putInt(SATURATION, arg0);
            edcon.commit();
            //Begin : Added by vend00102 2012.07.16
		}

		editor.commit();

		if(IsBind)
			{
				mContext.unbindService(serviceConnection);
				IsBind = false;
				Log.e(TAG, "[Lingling]onDialogClosed:unbindService");			

		}//Lingling
		//End
	}

	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		super.onDismiss(dialog);
		Log.e(TAG, "[Lingling]SaturationPerference: onDismiss");//Lingling
		//mContext.unbindService(serviceConnection);
		editor.putString("sat", "sat");
		editor.commit();
	}

}
