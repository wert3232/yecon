package com.android.settings;

import com.android.settings.R;
import com.android.settings.mtksetting;
import com.android.settings.mtksetting.MyBinder;
import com.android.settings.mtksetting;
import com.mtksetting.Array;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemProperties;
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

public class ContrastPerference extends SeekBarDialogPreference implements
		SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener {
	private SeekBar mSeekBar;
	private TextView mTextView;
	private static mtksetting mmtksetting;
	private Intent serviceIntent;
	private Context mContext;
	private static final String CONTRAST = "contrast";
	int arg0;
	private boolean IsBind = false;
	private static final String TAG = "ContrastPerference";

	public ContrastPerference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setDialogLayoutResource(R.layout.preference_dialog_contrast);
		mContext = context;
	}

	private static ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
		Log.d(TAG, "Lingling ContrastPerference Log2");
			mmtksetting = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "Lingling ContrastPerference Log3");
			MyBinder binder = (MyBinder) service;
			mmtksetting = binder.getService();
		}
	};

	protected void onBindDialogView(View view) {
		// TODO Auto-generated method stub
		super.onBindDialogView(view);
		mSeekBar = getSeekBar(view);
		mSeekBar.setMax(100);
		mTextView = (TextView) view.findViewById(R.id.textview);
		SharedPreferences setting = getSharedPreferences();
		if( setting == null )
			return;

		String con = setting.getString("con", "");
		arg0 = setting.getInt(CONTRAST, 0);
		String contrastvalue = String.valueOf(arg0);
		if (con.equals("")) {
			//Begin: added by Meng.li ;Modify the default value from 20 to 25 in 3353
	           arg0 = 20;

	        //End
			mTextView.setText(String.valueOf(arg0));
			// give the arg0 a value that refer to process.
			//arg0 = 20;
			mSeekBar.setProgress(arg0);
			SharedPreferences uiState = mContext.getSharedPreferences(
					Array.MTKSettings, mContext.MODE_PRIVATE);
			SharedPreferences.Editor editor = uiState.edit();
			editor.putInt(CONTRAST, arg0);
			editor.commit();
			
			final ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putInt(resolver,
            Settings.System.SCREEN_CONTRAST, arg0);
			//Begin : Added by vend00102 2012.07.16
			SharedPreferences sp = mContext.getSharedPreferences(
			        "com.android.color_preferences", mContext.MODE_WORLD_READABLE);
            SharedPreferences.Editor edcon = sp.edit();
            edcon.putInt(CONTRAST, arg0);
            edcon.commit();
            //Begin : Added by vend00102 2012.07.16
		} else {
			mTextView.setText(contrastvalue);
			mSeekBar.setProgress(arg0);
			final ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putInt(resolver,
            Settings.System.SCREEN_CONTRAST, arg0);
		}
		mSeekBar.setOnSeekBarChangeListener(this);
		serviceIntent = new Intent(mContext, mtksetting.class);
		IsBind = mContext.bindService(serviceIntent, serviceConnection,
				Context.BIND_AUTO_CREATE);
		if(IsBind)
			Log.e(TAG, "[Lingling]ContrastPerference:onBindDialogView:bindService OK:||IsBind = "+IsBind);//Lingling
		else
			Log.e(TAG, "[Lingling]ContrastPerference:onBindDialogView:bindService FAIL:||IsBind = "+IsBind);//Lingling
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {
		mTextView.setText(String.valueOf(progress));
		if (mmtksetting == null) {

		} else {
			seekBar.setProgress(progress);
			mmtksetting.GSetContrastLevel(progress);
			Log.e("tag", "msg-----------------------wuxin------backlight11===="
					+ progress);
			SharedPreferences uiState = mContext.getSharedPreferences(
					Array.MTKSettings, mContext.MODE_PRIVATE);
			SharedPreferences.Editor editor = uiState.edit();
			editor.putInt(CONTRAST, progress);
			editor.commit();
			final ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putInt(resolver,
            Settings.System.SCREEN_CONTRAST, progress);
			
			//Begin : Added by vend00102 2012.07.16
            SharedPreferences sp = mContext.getSharedPreferences(
                    "com.android.color_preferences", mContext.MODE_WORLD_READABLE);
            SharedPreferences.Editor edcon = sp.edit();
            edcon.putInt(CONTRAST, progress);
            edcon.commit();
            //Begin : Added by vend00102 2012.07.16
		}

	}

/*	
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			

		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			mmtksetting.GSetContrastLevel(arg0);
			SharedPreferences uiState = mContext.getSharedPreferences(
					Array.MTKSettings, mContext.MODE_PRIVATE);
			SharedPreferences.Editor editor = uiState.edit();
			editor.putInt(CONTRAST, arg0);
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
			if (mmtksetting == null) {

			} else {
			mmtksetting.GSetContrastLevel(arg0);
			}
			// restore the old value.
			editor.putInt(CONTRAST, arg0);
			
			//Begin : Added by vend00102 2012.07.16
            SharedPreferences sp = mContext.getSharedPreferences(
                    "com.android.color_preferences", mContext.MODE_WORLD_READABLE);
            SharedPreferences.Editor edcon = sp.edit();
            edcon.putInt(CONTRAST, arg0);
            edcon.commit();
            //Begin : Added by vend00102 2012.07.16
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
		SharedPreferences uiState = mContext.getSharedPreferences(
				Array.MTKSettings, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = uiState.edit();
		editor.putString("con", "con");
		editor.commit();
	}
}
