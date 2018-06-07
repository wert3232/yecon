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

public class BalancePreference extends SeekBarDialogPreference implements
        SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener {
    private SeekBar mSeekBar;
    private TextView mTextView;
    private static mtksetting mmtksetting;
    private Intent serviceIntent;
    private Context mContext;
    private int mBalance;
    int arg0;
    private static final String Balance = "balance";
    private boolean isBind = false;

    public BalancePreference(Context context, AttributeSet attrs) {
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
            //Begin : Added by vend00102 2012.07.27
            //mmtksetting.GOpenDVPSet();
            //End : Added by vend00102
        }
    };

    @Override
    protected void onBindDialogView(View view) {
        // TODO Auto-generated method stub
        super.onBindDialogView(view);
        mSeekBar = getSeekBar(view);
        mTextView=(TextView)view.findViewById(R.id.textview);
        mSeekBar.setMax(40);
        SharedPreferences setting = getSharedPreferences();

        //add by hct for klocwork
        if(null == setting)
            {
                return;
            }
        
        arg0 = setting.getInt(Balance, 0)+20;
        String balancevalue=String.valueOf(arg0-20);
        mTextView.setText(balancevalue);
        mSeekBar.setProgress(arg0);
        mSeekBar.setOnSeekBarChangeListener(this);
        serviceIntent = new Intent(mContext, mtksetting.class);
        isBind = mContext.bindService(serviceIntent, serviceConnection,
                Context.BIND_AUTO_CREATE);
        Log.d("BalancePreference", "onBindDialogView: bindService isBind = " + isBind);
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
        mBalance = progress - 20;
        mTextView.setText(String.valueOf(mBalance));
        int i4RightValue = mBalance + 20;
        int i4LeftValue = 40 - i4RightValue;

        if (mmtksetting == null) {

        } else {
            seekBar.setProgress(progress);
            
            mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4LeftValue], 0);
            mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4LeftValue], 2);
            mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4RightValue], 1);
            mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4RightValue], 3);
            SharedPreferences uiState = mContext.getSharedPreferences(Array.MTKSettings,mContext.MODE_PRIVATE);
            SharedPreferences.Editor editor = uiState.edit();
            editor.putInt(Balance, mBalance);
            editor.commit();
            //Begin : Added by vend00102 2012.07.27
            i4RightValue = mBalance;
            i4LeftValue = -mBalance;
            int rValues[] = new int[6];
            rValues[0] = 0;
            rValues[1] = i4LeftValue;
            rValues[2] = i4RightValue;
            rValues[3] = i4LeftValue;
            rValues[4] = i4RightValue;
            rValues[5] = 0;
          //  mmtksetting.DVP_GSetBalances(rValues);
            //End : Added by vend00102 
        }

    }
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Log.e("BalancePreference", "onClick: " + which);
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            int i4RightValue = (arg0-20) + 20;
            int i4LeftValue = 40 - i4RightValue;
            mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4LeftValue], 0);
            mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4LeftValue], 2);
            mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4RightValue], 1);
            mmtksetting.SetBalance(Array.Balance_au4TrimValue[i4RightValue], 3);
            SharedPreferences uiState = mContext.getSharedPreferences(Array.MTKSettings,mContext.MODE_PRIVATE);
            SharedPreferences.Editor editor = uiState.edit();
            editor.putInt(Balance, (arg0-20));
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
            Log.d("BalancePreference", "onDialogClosed: unbindService");
        }
        //Begin : Added by vend00102 2012.07.27
        //mmtksetting.GCloseDVPSet();
        //End : Added by vend00102 2012.07.27
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        // TODO Auto-generated method stub
        super.onDismiss(dialog);
    }

}
