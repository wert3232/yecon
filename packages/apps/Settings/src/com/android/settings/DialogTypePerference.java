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

import com.android.settings.mtksetting.MyBinder;
import com.mtksetting.Array;

public class DialogTypePerference  extends SeekBarDialogPreference implements
		SeekBar.OnSeekBarChangeListener {
	private SeekBar mSeekBar;
	//Begin : Added by vend00102 2012.07.20
	private TextView mTextView;
	private static mtksetting mmtksetting;
	private Intent serviceIntent;
	private Context mContext;
	private int mCurrent;
	private boolean IsBind = false;
	private static final String DIALOGTYPE = "dialogtype";
	private static final String TAG = "DialogTypePerference";

	public DialogTypePerference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setDialogLayoutResource(R.layout.preference_dialog_contrast);
		mContext = context;
	}

	private static ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e(TAG, "[Lingling]DialogTypePerference: onServiceDisconnected");//Lingling
			mmtksetting = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder binder = (MyBinder) service;
			mmtksetting = binder.getService();
		}
	};

	@Override
	protected void onBindDialogView(View view) {
		// TODO Auto-generated method stub
		super.onBindDialogView(view);
		mSeekBar = getSeekBar(view);
		mSeekBar.setMax(20);
		//SharedPreferences setting = getSharedPreferences();
		mTextView = (TextView) view.findViewById(R.id.textview);
		SharedPreferences setting = mContext.getSharedPreferences(Array.MTKSettings,mContext.MODE_PRIVATE);
		//add by hct for klocwork
		if(null == setting)
			{
				return;
			}
		
		int arg0 = setting.getInt(DIALOGTYPE, 0);
		mTextView.setText(String.valueOf(arg0));
		mSeekBar.setProgress(arg0);
		mSeekBar.setOnSeekBarChangeListener(this);
		serviceIntent = new Intent(mContext, mtksetting.class);
		IsBind=mContext.bindService(serviceIntent, serviceConnection,
				Context.BIND_AUTO_CREATE);
		if(IsBind)
			Log.e(TAG, "[Lingling]DialogTypePerference:onBindDialogView:bindService OK:||IsBind = "+IsBind);//Lingling
		else
			Log.e(TAG, "[Lingling]DialogTypePerference:onBindDialogView:bindService FAIL:||IsBind = "+IsBind);//Lingling

	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {
		mCurrent = progress;
		mTextView.setText(String.valueOf(progress));
//		if (mmtksetting == null) {
//
//		} else {
//			seekBar.setProgress(progress);
//			mmtksetting.DVP_GSetDialogType(progress);
//			SharedPreferences uiState = mContext.getSharedPreferences(Array.MTKSettings,mContext.MODE_PRIVATE);
//			SharedPreferences.Editor editor = uiState.edit();
//			editor.putInt(DIALOGTYPE, mCurrent);
//			editor.commit();
//		}

	}

	@Override
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

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult){
		    if (mmtksetting == null){
		        
		    }else{
		       // mmtksetting.GOpenDVPSet();
    		    mSeekBar.setProgress(mCurrent);
                mmtksetting.SetDialogType(mCurrent);
                SharedPreferences uiState = mContext.getSharedPreferences(Array.MTKSettings, mContext.MODE_PRIVATE);
                SharedPreferences.Editor editor = uiState.edit();
                editor.putInt(DIALOGTYPE, mCurrent);
                editor.commit();
                
                setSummary(Integer.toString(mCurrent));
               // mmtksetting.GCloseDVPSet();
		    }
		}
		if(IsBind)
			{
				mContext.unbindService(serviceConnection);
				IsBind = false;
				Log.e(TAG, "[Lingling]onDialogClosed:unbindService");			

		}//Lingling
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		super.onDismiss(dialog);
	}

	public void SetContrast(int arg0) {
		mmtksetting.SetDialogType(arg0);
	}

}
