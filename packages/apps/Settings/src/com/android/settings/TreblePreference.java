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

public class TreblePreference extends SeekBarDialogPreference implements
        SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener {
    private SeekBar mSeekBar;
    private TextView mTextView;
    private static mtksetting mmtksetting;
    private Intent serviceIntent;
    private Context mContext;
    private int uTreble;
    private static final String Treble = "treble";
    private static final String TAG = "TreblePreference";
    private boolean isBind = false;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    int arg0;
    public TreblePreference(Context context, AttributeSet attrs) {
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
        mSeekBar.setMax(28);
        setting = getSharedPreferences();
        if( setting == null )
            return;
        arg0 = setting.getInt(Treble, 0) + 14;
        String treblevalue=String.valueOf(arg0-14);
        mTextView.setText(treblevalue);
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
        uTreble = progress - 14;
        mTextView.setText(String.valueOf(uTreble));
        int uEQType = setting.getInt("eq_key", 0);
        int uBass = setting.getInt("bass", 0);

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

            editor.putInt(Treble, uTreble);
            editor.commit();
        }

    }
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Log.e(TAG, "onClick: " + which);

        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            uTreble = arg0 - 14;
            mTextView.setText(String.valueOf(uTreble));
            int uEQType = setting.getInt("eq_key", 0);
            int uBass = setting.getInt("bass", 0);

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
            //    mmtksetting.DVP_GSetEQValues(rvalue);
                
                editor.putInt(Treble, uTreble);
                editor.commit();
            }
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
        //Begin : Added by vend00102 2012.07.27
      //  mmtksetting.GCloseDVPSet();
        //End : Added by vend00102
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        // TODO Auto-generated method stub
        super.onDismiss(dialog);
    }

}
