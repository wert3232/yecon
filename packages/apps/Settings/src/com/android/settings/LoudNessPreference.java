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

public class LoudNessPreference extends SeekBarDialogPreference implements
        SeekBar.OnSeekBarChangeListener , DialogInterface.OnClickListener{
    private SeekBar mSeekBar;
    private TextView mTextView;
    private static mtksetting mmtksetting;
    private Intent serviceIntent;
    private Context mContext;
    private int mCurrent;
    private static final String LoudNess = "loudness";
    private static final String TAG = "LoudNessPreference";
    private boolean isBind = false;
    SharedPreferences.Editor editor;
    int arg0;
    public LoudNessPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setDialogLayoutResource(R.layout.preference_dialog_contrast);
        mContext = context;
    }

    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
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
        SharedPreferences uiState = mContext.getSharedPreferences(Array.MTKSettings,mContext.MODE_PRIVATE);
        editor = uiState.edit();
        mSeekBar = getSeekBar(view);
        mTextView=(TextView)view.findViewById(R.id.textview);
        mSeekBar.setMax(19);
        SharedPreferences setting = getSharedPreferences();
        
        //add by hct for klocwork
        if(null == setting){
                return;
        }

        
        arg0 = setting.getInt(LoudNess, 0);
        String loudbessvalue=String.valueOf(arg0);
        mTextView.setText(loudbessvalue);
        mSeekBar.setProgress(arg0);
        mSeekBar.setOnSeekBarChangeListener(this);
        serviceIntent = new Intent(mContext, mtksetting.class);
        isBind = mContext.bindService(serviceIntent, serviceConnection,
                Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onBindDialogView: bindService isBind = " + isBind);
    }
      
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
        mCurrent = progress;
        mTextView.setText(String.valueOf(mCurrent));
        if (mmtksetting == null) {

        } else {
            seekBar.setProgress(progress);
            int[] rLoudNessGain = Array.LoudNess_gLoudNessGain[progress];
            mmtksetting.SetLoudNess(progress, rLoudNessGain);
            editor.putInt(LoudNess, mCurrent);
            editor.commit();
        }

    }
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Log.e("TAG", "onClick: " + which);
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            int[] rLoudNessGain = Array.LoudNess_gLoudNessGain[arg0];
            mmtksetting.SetLoudNess(arg0, rLoudNessGain);
            editor.putInt(LoudNess, arg0);
            editor.commit();
        }
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
        if (isBind) {
            mContext.unbindService(serviceConnection);
            isBind = false;
            Log.d(TAG, "onDialogClosed: unbindService");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        // TODO Auto-generated method stub
        super.onDismiss(dialog);
    }

}
