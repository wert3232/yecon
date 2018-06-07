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

public class BassPreference extends SeekBarDialogPreference implements
        SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener {
    private SeekBar mSeekBar;
    private TextView mTextView;
    private static mtksetting mmtksetting;
    private Intent serviceIntent;
    private Context mContext;
    private static final String Bass = "bass";
    int uBass;
    int arg0;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    private static final String TAG = "BassPreference";
    private boolean isBind = false;

    public BassPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setDialogLayoutResource(R.layout.preference_dialog_contrast);
        mContext = context;
        Log.i(TAG, "BassPreference");
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
        Log.i(TAG, "onBindDialogView");
        // TODO Auto-generated method stub
        super.onBindDialogView(view);
        mSeekBar = getSeekBar(view);
        mTextView = (TextView) view.findViewById(R.id.textview);
        mSeekBar.setMax(28);
        setting = getSharedPreferences();
        if( setting == null )
            return;
        arg0 = setting.getInt(Bass, 0) + 14;
        String bassvalue = String.valueOf(arg0 - 14);
        mTextView.setText(bassvalue);
        mSeekBar.setProgress(arg0);
        mSeekBar.setOnSeekBarChangeListener(this);
        serviceIntent = new Intent(mContext, mtksetting.class);
        isBind = mContext.bindService(serviceIntent, serviceConnection,
                Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onBindDialogView: bindService isBind = " + isBind);
        SharedPreferences uiState = mContext.getSharedPreferences(Array.MTKSettings,mContext.MODE_PRIVATE);
        editor = uiState.edit();
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
        Log.i(TAG, "onProgressChanged, progress:" + progress);
        uBass = progress - 14;
        mTextView.setText(String.valueOf(uBass));
        int uEQType = setting.getInt("eq_key", 0);
        int uTreble = setting.getInt("treble", 0);
        int[] EQGain = new int[11];
        for (int idx = 0; idx < 11; idx++) {
            int temp = 0;
            
            if (idx >= 1 && idx <= 3) {
                temp = Array.gEQTypePos[uEQType][idx] + uBass;
            } else if (idx >= 8 && idx <= 10) {
                temp = Array.gEQTypePos[uEQType][idx] + uTreble;
            } else {
                temp = Array.gEQTypePos[uEQType][idx];
            }
            
            if (temp > 14) {
                temp =  14;
            } else if (temp < -14) {
                temp = -14;
            }

            if (idx == 0) {
                EQGain[idx] = Array.g_dryValues[temp + 14];
            } else {
                EQGain[idx] = Array.g_ganValues[temp + 14];
            }
        }
        if (mmtksetting == null) {
        } else {
            seekBar.setProgress(progress);
            mmtksetting.SetEQValues(EQGain);

            int rvalue[] = new int[]{0, uBass, uBass, uBass,
                    0, 0, 0, 0,
                    uTreble, uTreble, uTreble};
           // mmtksetting.DVP_GSetEQValues(rvalue);

            editor.putInt(Bass, uBass);
            editor.commit();
        }
    }
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Log.e(TAG, "onClick: " + which);
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            uBass = arg0 - 14;
            mTextView.setText(String.valueOf(uBass));
            int uEQType = setting.getInt("eq_key", 0);
            int uTreble = setting.getInt("treble", 0);
            int[] EQGain = new int[11];
            for (int idx = 0; idx < 11; idx++) {
                int temp = 0;
                
                if (idx >= 1 && idx <= 3) {
                    temp = Array.gEQTypePos[uEQType][idx] + uBass;
                } else if (idx >= 8 && idx <= 10) {
                    temp = Array.gEQTypePos[uEQType][idx] + uTreble;
                } else {
                    temp = Array.gEQTypePos[uEQType][idx];
                }
                
                if (temp > 14) {
                    temp =  14;
                } else if (temp < -14) {
                    temp = -14;
                }

                if (idx == 0) {
                    EQGain[idx] = Array.g_dryValues[temp + 14];
                } else {
                    EQGain[idx] = Array.g_ganValues[temp + 14];
                }
            }
            if (mmtksetting == null) {
            } else {
                mmtksetting.SetEQValues(EQGain);

                int rvalue[] = new int[]{0, uBass, uBass, uBass,
                        0, 0, 0, 0,
                        uTreble, uTreble, uTreble};
             //   mmtksetting.DVP_GSetEQValues(rvalue);

                editor.putInt(Bass, uBass);
                editor.commit();
            }
        }
    }

    @Override
    protected View onCreateDialogView() {
        Log.i(TAG, "onCreateDialogView");
        // TODO Auto-generated method stub
        return super.onCreateDialogView();

    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "onStartTrackingTouch");
        // NA
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "onStopTrackingTouch");
        // NA
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        Log.i(TAG, "onDialogClosed");
        super.onDialogClosed(positiveResult);
        if (isBind) {
            mContext.unbindService(serviceConnection);
            isBind = false;
            Log.d(TAG, "onDialogClosed: unbindService");
        }
        //Begin : Added by vend00102 2012.07.27
       // mmtksetting.GCloseDVPSet();
        //End : Added by vend00102
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.i(TAG, "onDismiss");
        // TODO Auto-generated method stub
        super.onDismiss(dialog);
    }

}
