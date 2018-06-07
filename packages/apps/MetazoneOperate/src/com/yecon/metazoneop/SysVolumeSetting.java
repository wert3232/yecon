
package com.yecon.metazoneop;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.TimerTask;

import com.android.org.bouncycastle.util.Arrays;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.autochips.settings.AtcSettings;

public class SysVolumeSetting extends Activity implements View.OnClickListener,
        OnItemSelectedListener {
    private static final String TAG = "SysVolumeSetting";

    private static final String VOL_TYPE_SYSTEM = "SystemVolume";
    private static final String VOL_TYPE_BT_PHONE = "BTVolume";
    private static final String FILE_BTVOLUME = "/mnt/ext_sdcard1/btvolume.csv";
    private static final String FILE_SYSVOLUME = "/mnt/ext_sdcard1/sysvolume.csv";
    private static final int MAX_VOLUME_DATA = 0x20000;
    private static final int MAX_BTVOL_DATA = 0xff;
    private static final int BT_PHONE_MIN_VOL = 60;

    private static final int ERROR_NONE = 0;
    private static final int ERROR_NO_FILE = 1;
    private static final int ERROR_BAD_FORMAT = 2;
    private static final int ERROR_BAD_ID = 3;
    private static final int ERROR_ILLEGAL_DATA = 4;

    private static final int[] ERROR_STRING_ID = {
            R.string.str_read_failed,
            R.string.str_read_failed,
            R.string.str_format_error,
            R.string.str_id_error,
            R.string.str_data_error
    };

    private static final int MSG_SET_VOLUME = 1;

    private VolumeLevelAdapter mAdapter;
    private ListView mLVVolumeLevel;
    private static String mVolType;
    private static int[] mVolumeData;
    private static int[] mBTVolData;
    
    private static int mBtVolMaxLevel;
    private static int mSysVolMaxLevel;

    private static int[] mVolumeDataOrg;
    private static int[] mBTVolDataOrg;

    private Button btnAdd1;
    private Button btnAdd10;
    private Button btnAdd100;
    private Button btnAdd1000;
    private Button btnAdd10000;

    private Button btnDec1;
    private Button btnDec10;
    private Button btnDec100;
    private Button btnDec1000;
    private Button btnDec10000;

    private Button btnPlay;
    private Button btnSave;
    private Button btnBack;
    private Button btnAutoRun;

    private EditText mEdtInterval;

    private java.util.Timer mTimer;
    private MyTimerTask mTimerTask;

    // private RadioGroup group;
    private RadioButton rb_sysvol;
    private RadioButton rb_btvol;
    private Spinner sp_volmax;

    private boolean mPlayFlag = true;
    private static final Object mLock = new Object();
    private static int mAutoCnt = 0;
    private boolean mAutoMode = false;
    private boolean mNextCntZero = false;

    private static int mMaxVolumeLevel = 40;

    private int mItemPosition = 1;
    private int mCurrVolIndex = 1;
    private boolean mPauseFlag = false;


    private static MediaPlayer mplayer = null;

    private static final int[] IncKeyTab = {
            R.id.btn_add1, R.id.btn_add10,
            R.id.btn_add100, R.id.btn_add1000, R.id.btn_add10000
    };
    private static final int[] DecKeyTab = {
            R.id.btn_dec1, R.id.btn_dec10,
            R.id.btn_dec100, R.id.btn_dec1000, R.id.btn_dec10000
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sysvolumsetting_activity);

        Intent intent = getIntent();
        mVolType = intent.getStringExtra("Type");

        if (!InitVolByCSV())
            InitVolumeData();

        mLVVolumeLevel = (ListView) findViewById(R.id.lv_volume_level);
        mAdapter = new VolumeLevelAdapter(this);
        mLVVolumeLevel.setAdapter(mAdapter);
        InitControls();
        if (mVolType.equals(VOL_TYPE_SYSTEM)) {
            btnPlay.setText(getResources().getText(R.string.btn_pause));
            // mplayer = MediaPlayer.create(getApplicationContext(),
            // R.raw.l_r_1khz_0db);
            mplayer = MediaPlayer.create(getApplicationContext(),
                    R.raw.test);
            mplayer.setLooping(true);
            mplayer.start();
        }

    }

    private void InitControls() {
        btnAdd1 = (Button) findViewById(R.id.btn_add1);
        btnAdd10 = (Button) findViewById(R.id.btn_add10);
        btnAdd100 = (Button) findViewById(R.id.btn_add100);
        btnAdd1000 = (Button) findViewById(R.id.btn_add1000);
        btnAdd10000 = (Button) findViewById(R.id.btn_add10000);

        btnDec1 = (Button) findViewById(R.id.btn_dec1);
        btnDec10 = (Button) findViewById(R.id.btn_dec10);
        btnDec100 = (Button) findViewById(R.id.btn_dec100);
        btnDec1000 = (Button) findViewById(R.id.btn_dec1000);
        btnDec10000 = (Button) findViewById(R.id.btn_dec10000);

        btnAdd1.setOnClickListener(this);
        btnAdd10.setOnClickListener(this);
        btnAdd100.setOnClickListener(this);
        btnAdd1000.setOnClickListener(this);
        btnAdd10000.setOnClickListener(this);

        btnDec1.setOnClickListener(this);
        btnDec10.setOnClickListener(this);
        btnDec100.setOnClickListener(this);
        btnDec1000.setOnClickListener(this);
        btnDec10000.setOnClickListener(this);

        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);

        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnAutoRun = (Button) findViewById(R.id.btn_auto_op);
        btnAutoRun.setOnClickListener(this);
        

        // group = (RadioGroup)findViewById(R.id.rg_volume_type);
        rb_sysvol = (RadioButton) findViewById(R.id.rb_system_volume);
        rb_btvol = (RadioButton) findViewById(R.id.rb_bt_volume);
        rb_sysvol.setOnClickListener(this);
        rb_btvol.setOnClickListener(this);
        sp_volmax = (Spinner) findViewById(R.id.sp_max_volume);
        sp_volmax.setSelection(mMaxVolumeLevel-30); // Jade,default is 40;
        sp_volmax.setOnItemSelectedListener(this);
        mEdtInterval = (EditText) findViewById(R.id.et_interval_time);

        if (mVolType.equals(VOL_TYPE_SYSTEM)) {
            rb_sysvol.setChecked(true);
            rb_btvol.setChecked(false);
            btnAdd1000.setEnabled(true);
            btnAdd10000.setEnabled(true);
            btnDec1000.setEnabled(true);
            btnDec10000.setEnabled(true);
            btnPlay.setEnabled(true);
        } else if (mVolType.equals(VOL_TYPE_BT_PHONE)) {
            if(mPlayFlag)
            {
                mPlayFlag = false;
                mplayer.pause();
                btnPlay.setText(getResources().getText(R.string.btn_play));
                Log.d(TAG, "Test sound pause...");
            }
            rb_sysvol.setChecked(false);
            rb_btvol.setChecked(true);
            btnAdd1000.setEnabled(false);
            btnAdd10000.setEnabled(false);
            btnDec1000.setEnabled(false);
            btnDec10000.setEnabled(false);
            btnPlay.setEnabled(false);
        }
    }

    private boolean InitVolByCSV()
    {
        boolean bSuc = false;
        if (mVolType.equals(VOL_TYPE_SYSTEM)) {
            mVolumeDataOrg = ReadFromCSV(FILE_SYSVOLUME);
            if (mVolumeDataOrg != null) {
                mMaxVolumeLevel = mVolumeDataOrg.length - 1;
                mSysVolMaxLevel = mMaxVolumeLevel;
                mVolumeData = Arrays.copyOf(mVolumeDataOrg, mMaxVolumeLevel + 1);
                if(sp_volmax!=null)
                    sp_volmax.setSelection(mMaxVolumeLevel-30);
                bSuc = true;
            }
        } else if (mVolType.equals(VOL_TYPE_BT_PHONE)) {
             mBTVolDataOrg = ReadFromCSV(FILE_BTVOLUME);
            if (mBTVolDataOrg != null){
                mMaxVolumeLevel = mBTVolDataOrg.length - 1;
                mBtVolMaxLevel = mMaxVolumeLevel;
                mBTVolData = Arrays.copyOf(mBTVolDataOrg, mMaxVolumeLevel + 1);
                if(sp_volmax!=null)
                    sp_volmax.setSelection(mMaxVolumeLevel-30);
                bSuc = true;
        }
       }
        return bSuc;
    }

    private void InitVolumeData() {
        if (mVolType.equals(VOL_TYPE_SYSTEM)) {
            mVolumeData = new int[mMaxVolumeLevel + 1];
            mSysVolMaxLevel = mMaxVolumeLevel;
            mVolumeData[0] = 0;
            for (int i = 1; i < mMaxVolumeLevel + 1; i++) {
                mVolumeData[i] = i * MAX_VOLUME_DATA / mMaxVolumeLevel;
            }
            mVolumeDataOrg = Arrays.copyOf(mVolumeData, mMaxVolumeLevel + 1);
        } else if (mVolType.equals(VOL_TYPE_BT_PHONE)) {
            mBTVolData = new int[mMaxVolumeLevel + 1];
            mBtVolMaxLevel = mMaxVolumeLevel;
            mBTVolData[0] = BT_PHONE_MIN_VOL;
            double step = (MAX_BTVOL_DATA - BT_PHONE_MIN_VOL)
                    / (double) mMaxVolumeLevel;
            for (int i = 1; i < mMaxVolumeLevel + 1; i++) {
                mBTVolData[i] = (int) (BT_PHONE_MIN_VOL + i * step);
            }
            mBTVolDataOrg = Arrays.copyOf(mBTVolData, mMaxVolumeLevel + 1);
        }
        
    }

    private void SetVolume(int vol) {

      /*  if (mVolType.equals(VOL_TYPE_SYSTEM))
            AtcSettings.Audio.SetVolumeData(vol);
        else if (mVolType.equals(VOL_TYPE_BT_PHONE))
            AtcSettings.Audio.SetBTHFPVolumeData(vol);		*/
        Log.d(TAG, "SetVolume[" + mVolType + "]---->" + "vol=" + vol);
    }

    private void ShowVolumeLevel(int level) {
        TextView tv = (TextView) findViewById(R.id.tv_curr_level);
        tv.setText(getResources().getString(R.string.curr_volume_level) + "  "
                + level);
    }

    private class VolumeLevelAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public VolumeLevelAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mMaxVolumeLevel + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.volume_item_layout, null);
                holder.mVolumeLevel = (Button) convertView.findViewById(R.id.btn_level);

                holder.mVolumeLevel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Auto-generated method stub
                        EditText edt = (EditText) view.getTag();
                        edt.requestFocus();
                        mItemPosition = (Integer) edt.getTag();

                        String strCurrVol = edt.getText().toString().trim();
                        int CurrVol = Integer.parseInt(strCurrVol);
                        SetVolume(CurrVol);
                        mCurrVolIndex = mItemPosition;
                        ShowVolumeLevel(mCurrVolIndex);
                        Log.d(TAG, "POS=" + mItemPosition);

                    }
                });
                holder.mVolumeData = (EditText) convertView
                        .findViewById(R.id.et_volume_data);

                holder.mVolumeData.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            mItemPosition = (Integer) view.getTag();
                            Log.e(TAG, "onClick - position: " + mItemPosition);
                            mCurrVolIndex = mItemPosition;
                        }
                        return false;
                    }
                });

                holder.mVolumeData.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence cs, int start,
                            int before, int count) {

                        return;
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                            int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub
                        // Log.d(TAG,"+++afterTextChanged+++");
                        int MaxVolume = MAX_VOLUME_DATA;
                        String strMsg = (String) getResources().getText(R.string.str_sysvol_out_of_range);
                        if (mVolType.equals(VOL_TYPE_BT_PHONE)) {
                            MaxVolume = MAX_BTVOL_DATA;
                            strMsg = (String) getResources().getText(R.string.str_btvol_out_of_range);
                        }
                        if (s != null && !s.equals("")) {
                            int markVal = 0;
                            try {
                                markVal = Integer.parseInt(s.toString());
                            } catch (NumberFormatException e) {
                                markVal = 0;
                                s.clear();
                                s.append('0');

                            }
                            if (s.toString().length() > 1 && s.toString().startsWith("0"))
                            {
                                s.delete(0, 1);
                            }
                            if (markVal > MaxVolume) {
                                Toast.makeText(getBaseContext(), strMsg, Toast.LENGTH_SHORT).show();
                                s.delete(s.toString().length() - 1, s.toString().length());

                            }

                            return;
                        }

                    }
                });
                holder.mVolumeData.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView tv, int action, KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (action == EditorInfo.IME_ACTION_DONE) {
                            String strTv = tv.getText().toString();
                            Log.e(TAG, "DONE,tv=" + tv.getText());
                            int VolData = Integer.parseInt(strTv);
                            if (mVolType.equals(VOL_TYPE_SYSTEM))
                                mVolumeData[mCurrVolIndex] = VolData;
                            else if (mVolType.equals(VOL_TYPE_BT_PHONE))
                                mBTVolData[mCurrVolIndex] = VolData;
                            SetVolume(VolData);
                        }
                        return false;
                    }
                });
                convertView.setTag(holder);

            } else
                holder = (ViewHolder) convertView.getTag();
            if (position >= 0 && position < mMaxVolumeLevel + 1) {
                holder.mVolumeLevel.setText(position + "");
                if (mVolType.equals(VOL_TYPE_SYSTEM))
                    holder.mVolumeData.setText(mVolumeData[position] + "");
                else if (mVolType.equals(VOL_TYPE_BT_PHONE))
                    holder.mVolumeData.setText(mBTVolData[position] + "");
                holder.mVolumeLevel.setTag(holder.mVolumeData);
                holder.mVolumeData.setTag(position);
                // Log.d(TAG,"update edit,pos="+position);
            }
            return convertView;
        }

        private final class ViewHolder {
            public Button mVolumeLevel;
            public EditText mVolumeData;
        }
    }

    private void btnPlayProc() {
        if (mPlayFlag) {
            mPlayFlag = false;
            mplayer.pause();
            btnPlay.setText(getResources().getText(R.string.btn_play));
            Log.d(TAG, "Test sound pause...");
        } else {
            mPlayFlag = true;
            mplayer.start();
            btnPlay.setText(getResources().getText(R.string.btn_pause));
            Log.d(TAG, "Test sound playing...");
        }
    }

    private void InitControlsInAutoMode() {
        if (mAutoMode) {
            mEdtInterval.setEnabled(false);
            rb_btvol.setEnabled(false);
            rb_sysvol.setEnabled(false);
            sp_volmax.setEnabled(false);
            rb_sysvol.setEnabled(false);
            rb_btvol.setEnabled(false);
            btnSave.setEnabled(false);
            btnAutoRun
                    .setText(getResources().getString(R.string.str_stop_auto));

        } else {
            rb_btvol.setEnabled(true);
            rb_sysvol.setEnabled(true);
            sp_volmax.setEnabled(true);
            mEdtInterval.setEnabled(true);
            rb_sysvol.setEnabled(true);
            rb_btvol.setEnabled(true);
            btnSave.setEnabled(true);
            btnAutoRun.setText(getResources().getString(R.string.str_auto));
        }
    }

    private void StartAutoTimer() {

        String strInterval = mEdtInterval.getText().toString().trim();
        mTimer = new java.util.Timer(true);
        Log.e(TAG, "Auto execute every " + strInterval + " Second...");
        int interval = Integer.parseInt(strInterval);
        interval = (interval < 1) ? 1 : interval;
        mEdtInterval.setText(String.valueOf(interval));
        interval *= 1000;

        mTimerTask = new MyTimerTask(); // 新建一个任务
        mTimer.schedule(mTimerTask, 0, interval);
        mAutoMode = true;
        InitControlsInAutoMode();
    }

    private void StopAutoTimer() {

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
            Log.e(TAG, "StopAutoTimer...");
        }
        mCurrVolIndex = mAutoCnt - 1;
        mAutoMode = false;
        mAutoCnt = 0;
        InitControlsInAutoMode();

    }

    private int IncDecKeyJudge(boolean bIncKey, int btnID) {
        int KeyIdx = -1;
        int[] KeyMap = bIncKey ? IncKeyTab : DecKeyTab;
        for (int i = 0; i < 5; i++) {
            if (btnID == KeyMap[i]) {
                KeyIdx = i;
                break;
            }
        }
        return KeyIdx;
    }

    private void IncDecKeyProc(int btnId) {

        int KeyIncIdx = IncDecKeyJudge(true, btnId);
        int KeyDecIdx = IncDecKeyJudge(false, btnId);
        if ((KeyIncIdx < 0) && (KeyDecIdx < 0))
            return; // neither INC nor DEC,do nothing;

        double delta = 0;
        if (KeyIncIdx >= 0)
            delta = Math.pow(10, KeyIncIdx);
        else if (KeyDecIdx >= 0)
            delta = Math.pow(10, KeyDecIdx);

        Log.d(TAG, "KeyIncIdx=" + KeyIncIdx + ",KeyDecIdx=" + KeyDecIdx
                + ",delta=" + delta);

        // mAdapter.notifyDataSetChanged();
        if (mCurrVolIndex < 0)
            mCurrVolIndex = 1;

        mLVVolumeLevel.performItemClick(
                mLVVolumeLevel.getAdapter().getView(mCurrVolIndex, null, null),
                mCurrVolIndex,
                mLVVolumeLevel.getAdapter().getItemId(mCurrVolIndex));
        mLVVolumeLevel.requestFocusFromTouch();
        mLVVolumeLevel.setSelection(mCurrVolIndex);
        mLVVolumeLevel.setSelected(true);

        int sum = 0;
        if (mVolType.equals(VOL_TYPE_SYSTEM))
            sum = mVolumeData[mCurrVolIndex];
        else if (mVolType.equals(VOL_TYPE_BT_PHONE))
            sum = mBTVolData[mCurrVolIndex];

        if (KeyIncIdx >= 0)
            sum += delta;
        else if (KeyDecIdx >= 0)
            sum -= delta;

        int MaxNum = MAX_VOLUME_DATA;
        String strMsg = (String) getResources().getText(R.string.str_sysvol_out_of_range);

        if (mVolType.equals(VOL_TYPE_BT_PHONE)) {
            MaxNum = MAX_BTVOL_DATA;
            strMsg = (String) getResources().getText(R.string.str_btvol_out_of_range);
        }

        if (sum > MaxNum || sum < 0) {
            Toast.makeText(getApplicationContext(), strMsg, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mVolType.equals(VOL_TYPE_SYSTEM))
            mVolumeData[mCurrVolIndex] = sum;
        else if (mVolType.equals(VOL_TYPE_BT_PHONE))
            mBTVolData[mCurrVolIndex] = sum;

        mAdapter.notifyDataSetChanged();
        // mAdapter.notifyDataSetInvalidated();
        synchronized (mLock) {
            mPauseFlag = true;
        }

        SetVolume(sum);
        ShowVolumeLevel(mCurrVolIndex);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onClick, id=" + v.getId());
        switch (v.getId()) {
            case R.id.btn_play:
                btnPlayProc();
                break;

            case R.id.btn_save:
                if (mVolType.equals(VOL_TYPE_SYSTEM))
                    SaveToCSV(mVolumeData, FILE_SYSVOLUME);
                else if (mVolType.equals(VOL_TYPE_BT_PHONE)){
                    if(mSysVolMaxLevel != mBtVolMaxLevel){
                        String strMsg = (String) getResources().getText(R.string.str_save_error);                              
                        Toast.makeText(getBaseContext(), strMsg, Toast.LENGTH_LONG).show(); 
                    } else SaveToCSV(mBTVolData, FILE_BTVOLUME);
                    
                }
                break;

            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.rb_system_volume:
                if(CheckSave())
                    ShowConfirmSaveDlg(false);
                mVolType = VOL_TYPE_SYSTEM;
                InitControls();
                if (!InitVolByCSV())
                    InitVolumeData();
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.rb_bt_volume:
                if(CheckSave())
                    ShowConfirmSaveDlg(false);
                mVolType = VOL_TYPE_BT_PHONE;
                InitControls();
                if (!InitVolByCSV())
                    InitVolumeData();
                mAdapter.notifyDataSetChanged();  
                break;

            case R.id.btn_auto_op:
                if (!mAutoMode)
                    StartAutoTimer();
                else
                    StopAutoTimer();

                break;

            default:
                IncDecKeyProc(v.getId());
                break;
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        if (mPlayFlag && mVolType.equals(VOL_TYPE_SYSTEM)) {
            mplayer.pause();
            mPlayFlag = false;
            btnPlay.setText(getResources().getText(R.string.btn_play));
        }
        if (mAutoMode)
            StopAutoTimer();
        if(CheckSave())
            ShowConfirmSaveDlg(true);
        else 
            super.onBackPressed();
        
       //  
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub
        if(mMaxVolumeLevel != position+30){
            mMaxVolumeLevel = 30 + position;
            InitVolumeData();
            mAdapter.notifyDataSetChanged();
        }
        Log.e(TAG,"onItemSelected,pos="+position+",id="+id);
       
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    private boolean IsAutoClickFinish() {
        boolean result = false;
        if (mAutoCnt < mMaxVolumeLevel + 1) {

            mLVVolumeLevel.performItemClick(mLVVolumeLevel.getAdapter()
                    .getView(mAutoCnt, null, null), mAutoCnt, mLVVolumeLevel
                    .getAdapter().getItemId(mAutoCnt));
            mLVVolumeLevel.requestFocusFromTouch();
            mLVVolumeLevel.setSelection(mAutoCnt);
            mLVVolumeLevel.setSelected(true);
            int first = mLVVolumeLevel.getFirstVisiblePosition();
            Log.d(TAG, "First=" + first);
            if (mAutoCnt == 0)
                if (first > 0) {
                    mLVVolumeLevel.scrollTo(0, 0);
                    return result;
                }

            if (first > mAutoCnt) {
                mNextCntZero = true;
                return result;
            }

            int cnt = (mAutoCnt == 0 || mNextCntZero) ? 0 : 1;
            mNextCntZero = false;
            View view = mLVVolumeLevel.getChildAt(cnt);
            EditText edt = (EditText) view.findViewById(R.id.et_volume_data);
            String strVol = edt.getText().toString().trim();
            Log.d(TAG, "VolData=" + strVol + " ,AutoCnt=" + mAutoCnt);
            SetVolume(Integer.parseInt(strVol));
            ShowVolumeLevel(mAutoCnt);
            mCurrVolIndex = mAutoCnt;
            mAutoCnt++;
        } else {
            mAutoCnt = 0;
            result = true;
        }
        return result;
    }

    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_SET_VOLUME:
                    Log.d(TAG, "do MSG_SET_VOLUME...");
                    if (IsAutoClickFinish()) {
                        StopAutoTimer();
                    }

                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub

            Log.i(TAG, "TimerTask....");
            synchronized (mLock) {
                if (mPauseFlag) {
                    mPauseFlag = false;
                    Log.e(TAG, "TimerTask pause...");
                } else {
                    Message msg = mHandler.obtainMessage(MSG_SET_VOLUME);
                    msg.sendToTarget();
                    Log.d(TAG, "TimerTask running...");
                }
            }
        }

    }

    public boolean SaveToCSV(int[] VolData, String strFullPath) {
        boolean bResult = false;
        int len = VolData.length;
        String strVolume;

        try {
            FileWriter CSV_Writer = new FileWriter(strFullPath, false);
            for (int i = 0; i < len; i++) {
                strVolume = i + "," + VolData[i] + "\n";
                CSV_Writer.write(strVolume);
            }
            CSV_Writer.close();
            bResult = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bResult = false;

        }

        String strMsg = (String) getResources().getText(R.string.str_save_failed);
        if (bResult) {
            strMsg = (String) getResources().getText(R.string.str_save_ok);
            strMsg += strFullPath;
        }
        Log.d(TAG, "Save to CSV, result=" + bResult + ",path=" + strFullPath);
        Toast.makeText(getBaseContext(), strMsg, Toast.LENGTH_SHORT).show();
        return bResult;

    }

    public int[] ReadFromCSV(String strFile) {
        int ErrCode = ERROR_NONE;
        int[] CsvData = new int[100];
        DataInputStream in = null;
        try {
            in = new DataInputStream(new FileInputStream(new File(strFile)));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CsvData = null;
            ErrCode = ERROR_NO_FILE;
        }
        BufferedReader bufferedreader = null;
        
        if(ErrCode==ERROR_NONE)
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CsvData = null;
            ErrCode = ERROR_NO_FILE;
        }

        int MaxVolLevel = 0;
        int MaxVol = MAX_VOLUME_DATA;
        if (mVolType.equals(VOL_TYPE_BT_PHONE))
            MaxVol = MAX_BTVOL_DATA;

        String stemp;
        if(ErrCode == ERROR_NONE)
        try {
            while ((stemp = bufferedreader.readLine()) != null) {
                Log.d(TAG, "ReadFromCSV: " + stemp);
                String[] StrVolData = stemp.split(",");
                if (StrVolData.length != 2) {
                    ErrCode = ERROR_BAD_FORMAT;
                    break;
                }

                int Idx = Integer.parseInt(StrVolData[0]);
                int Value = Integer.parseInt(StrVolData[1]);
                if (Idx != MaxVolLevel) {
                    ErrCode = ERROR_BAD_ID;
                    break;
                }

                if (Value < 0 || Value > MaxVol) {
                    ErrCode = ERROR_ILLEGAL_DATA;
                    break;
                }

                CsvData[MaxVolLevel] = Value;
                //Log.e(TAG, "DATA[" + MaxVolLevel + "]=" + CsvData[MaxVolLevel]);
                MaxVolLevel++;

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CsvData = null;
        }

        Log.e(TAG, "MaxVolLevel=" + MaxVolLevel);
        if(ErrCode == ERROR_NONE)
        try {
            bufferedreader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (CsvData != null && MaxVolLevel > 0)
        {
            int[] VolData = Arrays.copyOfRange(CsvData, 0, MaxVolLevel);
            return VolData;

        }

        if (ErrCode > 0) {
            Log.d(TAG,"ErrCode="+ErrCode);
            int strId = ERROR_STRING_ID[ErrCode];
            String strErr = "File: "+strFile+", "+(String) getResources().getText(strId);
            String strDefault = (String)getResources().getText(R.string.str_load_default);
            strErr += strDefault;
           // Toast.makeText(getBaseContext(), strErr, Toast.LENGTH_LONG).show();
            AlertDialog.Builder dialog=new AlertDialog.Builder(SysVolumeSetting.this);
            dialog.setTitle(getResources().getText(R.string.dlg_title_csv)).setIcon(android.R.drawable.ic_dialog_alert).setMessage(strErr).setPositiveButton(getResources().getText(R.string.str_ok), 
                    new DialogInterface.OnClickListener() {
              
             @Override
             public void onClick(DialogInterface dialog, int which) {
             
                 // TODO Auto-generated method stub
                
             }
         }).setNegativeButton(getResources().getText(R.string.str_cancel), new DialogInterface.OnClickListener() {
              
              
             public void onClick(DialogInterface dialog, int which) {
                 // TODO Auto-generated method stub
                 dialog.cancel();//取消弹出框
                 onBackPressed();
             }
         }).create().show();
            
        
        }
        return null;

    }
    
    public void ShowConfirmSaveDlg(final boolean bQuit)
    {
       
        AlertDialog.Builder dialog=new AlertDialog.Builder(SysVolumeSetting.this);
        String strMsg = (String)getResources().getText(R.string.dlg_msg_save);
        dialog.setTitle(getResources().getText(R.string.dlg_title_vol)).setIcon(android.R.drawable.ic_dialog_alert).setMessage(strMsg).setPositiveButton(getResources().getText(R.string.str_ok), 
                  new DialogInterface.OnClickListener() {
              
             @Override
             public void onClick(DialogInterface dialog, int which) {
             
                 // TODO Auto-generated method stub
                btnSave.performClick();
                if(bQuit)
                    finish();
               // mSave = true;
                //finish();
             }
         }).setNegativeButton(getResources().getText(R.string.str_cancel), new DialogInterface.OnClickListener() {
                      
             public void onClick(DialogInterface dialog, int which) {
                 // TODO Auto-generated method stub
                 dialog.cancel();//取消弹出框
               //  mSave = false;
                 if(bQuit)
                     finish();
             }
         }).create().show();
        
        
    }
    public boolean CheckSave()
    {
        @SuppressWarnings("unused")
        boolean bNeedSave = false;
        if (mVolType.equals(VOL_TYPE_SYSTEM)) {
            if (mSysVolMaxLevel != mMaxVolumeLevel)
                bNeedSave = true;
            else {
                for (int i = 0; i < mMaxVolumeLevel + 1; i++) {
                    if (mVolumeData[i] != mVolumeDataOrg[i]) {
                        bNeedSave = true;
                        break;
                    }
                }

            }
        } else if (mVolType.equals(VOL_TYPE_BT_PHONE)) {
            if (mBtVolMaxLevel != mMaxVolumeLevel)
                bNeedSave = true;
            else {
                for (int i = 0; i < mMaxVolumeLevel + 1; i++) {
                    if (mBTVolData[i] != mBTVolDataOrg[i]) {
                        bNeedSave = true;
                        break;
                    }
                }

            }
        }
         
      
        return bNeedSave;
        
    }
}
    
