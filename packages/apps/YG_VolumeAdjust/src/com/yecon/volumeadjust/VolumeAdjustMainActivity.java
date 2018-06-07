
package com.yecon.volumeadjust;

import static com.yecon.volumeadjust.Constants.*;
import static com.yecon.volumeadjust.VolumeAdjustUtil.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class VolumeAdjustMainActivity extends Activity implements OnClickListener {
    private static final String TAG = "VolumeAdjust";

    public static String VOLUME_DATA_DIR = Environment.getExternalStorageDirectory()
            + "/VolumeData";
    public static String VOLUME_DATA_FILE_PATH = VOLUME_DATA_DIR + "/VolumeData.txt";

    private static long mLastKeyEventTime;

    private AudioManager mAudioManager;

    private Spinner mSPVolumeStreamType;
    private Spinner mSPVolumeIndex;

    private TextView mTVOldVolumeData;
    private TextView mTVNewVolumeData;

    private VolumeStreamTypeItemSelectedListener mVolumeStreamTypeListener = new VolumeStreamTypeItemSelectedListener();
    private VolumeIndexItemSelectedListener mVolumeIndexListener = new VolumeIndexItemSelectedListener();

    private int mCurrStreamType;
    private int mCurrVolumeLevel;

    private int mOldVolumeData;
    private int mNewVolumeData;

    private int[] mMusicNewVolumeDataArray = new int[MAX_VOLUME_LEVEL + 1];
    private int[] mAvinNewVolumeDataArray = new int[MAX_VOLUME_LEVEL + 1];
    private int[] mBtNewVolumeDataArray = new int[MAX_VOLUME_LEVEL + 1];

    private StringBuffer mExportVolumeData = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        initUI();
    }

    private void initData() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        initNewVolumeData();

        VOLUME_DATA_DIR = Environment.getExternalStorageDirectory() + "/VolumeData";
        if (Environment.getStorageState(SDCARD1_PATH).equals(Environment.MEDIA_MOUNTED)) {
            VOLUME_DATA_DIR = SDCARD1_PATH + "/VolumeData";
        }
        VOLUME_DATA_FILE_PATH = VOLUME_DATA_DIR + "/VolumeData.txt";
        File file = new File(VOLUME_DATA_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private void initNewVolumeData() {
        for (int i = 0; i <= MAX_VOLUME_LEVEL; i++) {
            mMusicNewVolumeDataArray[i] = VOLUME_MUSIC_DAC[i];
            mAvinNewVolumeDataArray[i] = VOLUME_AVIN_DAC[i];
            mBtNewVolumeDataArray[i] = VOLUME_BT_DAC[i];
        }

        // String musicNewVolumeData = readFileFromData(this,
        // VOLUME_STREAM_MUSIC_FILE);
        // if (musicNewVolumeData != null && musicNewVolumeData.length() > 0) {
        // Log.e(TAG, musicNewVolumeData);
        // String[] data = musicNewVolumeData.split(FILE_SPACE_FLAG);
        // if (data.length == MAX_VOLUME_LEVEL + 1) {
        // for (int i = 0; i <= MAX_VOLUME_LEVEL; i++) {
        // mMusicNewVolumeDataArray[i] = Integer.parseInt(data[i]);
        // }
        // }
        // }
        //
        // String avinNewVolumeData = readFileFromData(this,
        // VOLUME_STREAM_AVIN_FILE);
        // if (avinNewVolumeData != null && avinNewVolumeData.length() > 0) {
        // Log.e(TAG, avinNewVolumeData);
        // String[] data = avinNewVolumeData.split(FILE_SPACE_FLAG);
        // if (data.length == MAX_VOLUME_LEVEL + 1) {
        // for (int i = 0; i <= MAX_VOLUME_LEVEL; i++) {
        // mAvinNewVolumeDataArray[i] = Integer.parseInt(data[i]);
        // }
        // }
        // }
    }

    private void initUI() {
        setContentView(R.layout.volume_adjust_main_activity);

        initVolumeStreamUI();

        initVolumeDataUI();

        initOperationUI();
    }

    private void initVolumeStreamUI() {
        ArrayAdapter<String> streamTypeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_layout, getResources().getStringArray(
                        R.array.VolumeStreamType));
        streamTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSPVolumeStreamType = (Spinner) findViewById(R.id.sp_volume_stream_type);
        mSPVolumeStreamType.setAdapter(streamTypeAdapter);
        mSPVolumeStreamType.setOnItemSelectedListener(mVolumeStreamTypeListener);

        String[] volumeLevel = new String[MAX_VOLUME_LEVEL + 1];
        for (int i = 0; i <= MAX_VOLUME_LEVEL; i++) {
            volumeLevel[i] = Integer.toString(i);
        }
        ArrayAdapter<String> indexAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_layout, volumeLevel);
        indexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSPVolumeIndex = (Spinner) findViewById(R.id.sp_volume_index);
        mSPVolumeIndex.setAdapter(indexAdapter);
        mSPVolumeIndex.setOnItemSelectedListener(mVolumeIndexListener);

        ImageButton btnLeft = (ImageButton) findViewById(R.id.ib_left_volume);
        btnLeft.setOnClickListener(this);
        ImageButton btnRight = (ImageButton) findViewById(R.id.ib_right_volume);
        btnRight.setOnClickListener(this);
    }

    private void initVolumeDataUI() {
        mTVOldVolumeData = (TextView) findViewById(R.id.tv_old_volume_data);
        mTVNewVolumeData = (TextView) findViewById(R.id.tv_new_volume_data);

        setVolumeData();

        Button btnSub1 = (Button) findViewById(R.id.btn_sub1);
        btnSub1.setOnClickListener(this);
        Button btnSub2 = (Button) findViewById(R.id.btn_sub2);
        btnSub2.setOnClickListener(this);
        Button btnSub3 = (Button) findViewById(R.id.btn_sub3);
        btnSub3.setOnClickListener(this);
        Button btnSub4 = (Button) findViewById(R.id.btn_sub4);
        btnSub4.setOnClickListener(this);
        Button btnSub5 = (Button) findViewById(R.id.btn_sub5);
        btnSub5.setOnClickListener(this);
        Button btnSub6 = (Button) findViewById(R.id.btn_sub6);
        btnSub6.setOnClickListener(this);

        Button btnAdd1 = (Button) findViewById(R.id.btn_add1);
        btnAdd1.setOnClickListener(this);
        Button btnAdd2 = (Button) findViewById(R.id.btn_add2);
        btnAdd2.setOnClickListener(this);
        Button btnAdd3 = (Button) findViewById(R.id.btn_add3);
        btnAdd3.setOnClickListener(this);
        Button btnAdd4 = (Button) findViewById(R.id.btn_add4);
        btnAdd4.setOnClickListener(this);
        Button btnAdd5 = (Button) findViewById(R.id.btn_add5);
        btnAdd5.setOnClickListener(this);
        Button btnAdd6 = (Button) findViewById(R.id.btn_add6);
        btnAdd6.setOnClickListener(this);
    }

    private void initOperationUI() {
        Button btnResetSingle = (Button) findViewById(R.id.btn_reset_single);
        btnResetSingle.setOnClickListener(this);
        Button btnResetAll = (Button) findViewById(R.id.btn_reset_all);
        btnResetAll.setOnClickListener(this);
        Button btnExport = (Button) findViewById(R.id.btn_export);
        btnExport.setOnClickListener(this);
        Button btnLoading = (Button) findViewById(R.id.btn_loading);
        btnLoading.setOnClickListener(this);
        Button btnExit = (Button) findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        long now = SystemClock.elapsedRealtime();
        if (now - mLastKeyEventTime < VOLUME_KEY_DELAY_TIME) {
            return;
        }
        mLastKeyEventTime = now;

        int id = view.getId();
        switch (id) {
            case R.id.ib_left_volume:
                if (mCurrVolumeLevel > MIN_VOLUME_LEVEL) {
                    mCurrVolumeLevel--;
                }
                mSPVolumeIndex.setSelection(mCurrVolumeLevel);

                setVolumeData();
                break;

            case R.id.ib_right_volume:
                if (mCurrVolumeLevel < MAX_VOLUME_LEVEL) {
                    mCurrVolumeLevel++;
                }
                mSPVolumeIndex.setSelection(mCurrVolumeLevel);

                setVolumeData();
                break;

            case R.id.btn_sub1:
                updateVolumeData(VOLUME_CHANGE_LEVEL1, VOLUME_CHANGE_FLAG_SUB);
                break;

            case R.id.btn_sub2:
                updateVolumeData(VOLUME_CHANGE_LEVEL2, VOLUME_CHANGE_FLAG_SUB);
                break;

            case R.id.btn_sub3:
                updateVolumeData(VOLUME_CHANGE_LEVEL3, VOLUME_CHANGE_FLAG_SUB);
                break;

            case R.id.btn_sub4:
                updateVolumeData(VOLUME_CHANGE_LEVEL4, VOLUME_CHANGE_FLAG_SUB);
                break;

            case R.id.btn_sub5:
                updateVolumeData(VOLUME_CHANGE_LEVEL5, VOLUME_CHANGE_FLAG_SUB);
                break;

            case R.id.btn_sub6:
                updateVolumeData(VOLUME_CHANGE_LEVEL6, VOLUME_CHANGE_FLAG_SUB);
                break;

            case R.id.btn_add1:
                updateVolumeData(VOLUME_CHANGE_LEVEL1, VOLUME_CHANGE_FLAG_ADD);
                break;

            case R.id.btn_add2:
                updateVolumeData(VOLUME_CHANGE_LEVEL2, VOLUME_CHANGE_FLAG_ADD);
                break;

            case R.id.btn_add3:
                updateVolumeData(VOLUME_CHANGE_LEVEL3, VOLUME_CHANGE_FLAG_ADD);
                break;

            case R.id.btn_add4:
                updateVolumeData(VOLUME_CHANGE_LEVEL4, VOLUME_CHANGE_FLAG_ADD);
                break;

            case R.id.btn_add5:
                updateVolumeData(VOLUME_CHANGE_LEVEL5, VOLUME_CHANGE_FLAG_ADD);
                break;

            case R.id.btn_add6:
                updateVolumeData(VOLUME_CHANGE_LEVEL6, VOLUME_CHANGE_FLAG_ADD);
                break;

            case R.id.btn_reset_single:
                if (mNewVolumeData != mOldVolumeData) {
                    doResetSingle();
                }
                break;

            case R.id.btn_reset_all:
                doResetAll();
                break;

            case R.id.btn_export:
                doExport();
                break;

            case R.id.btn_loading:
                doLoading();
                break;

            case R.id.btn_exit:
                doExit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        doExit();
    }

    private void setVolumeData() {
        if (mCurrStreamType == VOLUME_STREAM_MUSIC) {
            mOldVolumeData = VOLUME_MUSIC_DAC[mCurrVolumeLevel];
            mNewVolumeData = mMusicNewVolumeDataArray[mCurrVolumeLevel];
        } else if (mCurrStreamType == VOLUME_STREAM_AVIN) {
            mOldVolumeData = VOLUME_AVIN_DAC[mCurrVolumeLevel];
            mNewVolumeData = mAvinNewVolumeDataArray[mCurrVolumeLevel];
        } else if (mCurrStreamType == VOLUME_STREAM_BT) {
            mOldVolumeData = VOLUME_BT_DAC[mCurrVolumeLevel];
            mNewVolumeData = mBtNewVolumeDataArray[mCurrVolumeLevel];
        }

        mTVOldVolumeData.setText(mOldVolumeData + "");
        mTVNewVolumeData.setText(mNewVolumeData + "");

        mAudioManager.setCustomStreamVolume(AudioManager.STREAM_MUSIC, mCurrVolumeLevel,
                mNewVolumeData, 0);
    }

    private void updateVolumeData(int changeLevel, int flag) {
        int tempData = 0;
        if (mCurrStreamType == VOLUME_STREAM_MUSIC) {
            tempData = mMusicNewVolumeDataArray[mCurrVolumeLevel];

            if (flag == VOLUME_CHANGE_FLAG_SUB) {
                tempData -= changeLevel;
                if (tempData < 0) {
                    tempData = 0;
                }
            } else if (flag == VOLUME_CHANGE_FLAG_ADD) {
                tempData += changeLevel;
            }

            mMusicNewVolumeDataArray[mCurrVolumeLevel] = tempData;
        } else if (mCurrStreamType == VOLUME_STREAM_AVIN) {
            tempData = mAvinNewVolumeDataArray[mCurrVolumeLevel];

            if (flag == VOLUME_CHANGE_FLAG_SUB) {
                tempData -= changeLevel;
                if (tempData < 0) {
                    tempData = 0;
                }
            } else if (flag == VOLUME_CHANGE_FLAG_ADD) {
                tempData += changeLevel;
            }

            mAvinNewVolumeDataArray[mCurrVolumeLevel] = tempData;
        } else if (mCurrStreamType == VOLUME_STREAM_BT) {
            tempData = mBtNewVolumeDataArray[mCurrVolumeLevel];

            if (flag == VOLUME_CHANGE_FLAG_SUB) {
                tempData -= changeLevel;
                if (tempData < 0) {
                    tempData = 0;
                }
            } else if (flag == VOLUME_CHANGE_FLAG_ADD) {
                tempData += changeLevel;
            }

            mBtNewVolumeDataArray[mCurrVolumeLevel] = tempData;
        }

        setVolumeData();
    }

    private void doResetSingle() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(getString(R.string.reset_query_msg1));
        builder.setPositiveButton(R.string.btn_ok,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (mCurrStreamType == VOLUME_STREAM_MUSIC) {
                            mNewVolumeData = VOLUME_MUSIC_DAC[mCurrVolumeLevel];
                        } else if (mCurrStreamType == VOLUME_STREAM_AVIN) {
                            mNewVolumeData = VOLUME_AVIN_DAC[mCurrVolumeLevel];
                        } else if (mCurrStreamType == VOLUME_STREAM_BT) {
                            mNewVolumeData = VOLUME_BT_DAC[mCurrVolumeLevel];
                        }
                        mTVNewVolumeData.setText(mNewVolumeData + "");

                        if (mCurrStreamType == VOLUME_STREAM_MUSIC) {
                            mMusicNewVolumeDataArray[mCurrVolumeLevel] = mNewVolumeData;
                        } else if (mCurrStreamType == VOLUME_STREAM_AVIN) {
                            mAvinNewVolumeDataArray[mCurrVolumeLevel] = mNewVolumeData;
                        } else if (mCurrStreamType == VOLUME_STREAM_BT) {
                            mBtNewVolumeDataArray[mCurrVolumeLevel] = mNewVolumeData;
                        }
                    }

                });
        builder.setNegativeButton(R.string.btn_cancel,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        builder.create().show();
    }

    private void doResetAll() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(getString(R.string.reset_query_msg1));
        builder.setPositiveButton(R.string.btn_ok,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        mCurrVolumeLevel = 0;
                        mSPVolumeIndex.setSelection(mCurrVolumeLevel);

                        if (mCurrStreamType == VOLUME_STREAM_MUSIC) {
                            mNewVolumeData = VOLUME_MUSIC_DAC[mCurrVolumeLevel];
                        } else if (mCurrStreamType == VOLUME_STREAM_AVIN) {
                            mNewVolumeData = VOLUME_AVIN_DAC[mCurrVolumeLevel];
                        } else if (mCurrStreamType == VOLUME_STREAM_BT) {
                            mNewVolumeData = VOLUME_BT_DAC[mCurrVolumeLevel];
                        }
                        mTVNewVolumeData.setText(mNewVolumeData + "");

                        for (int i = 0; i <= MAX_VOLUME_LEVEL; i++) {
                            mMusicNewVolumeDataArray[i] = VOLUME_MUSIC_DAC[i];
                            mAvinNewVolumeDataArray[i] = VOLUME_AVIN_DAC[i];
                            mBtNewVolumeDataArray[i] = VOLUME_BT_DAC[i];
                        }
                    }

                });
        builder.setNegativeButton(R.string.btn_cancel,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        builder.create().show();
    }

    private void doExport() {
        mExportVolumeData.delete(0, mExportVolumeData.length());

        mExportVolumeData.append("UINT32 DVP_VOLUME_SD_DAC[40+1] =\n{\n");
        for (int i = 0; i < MAX_VOLUME_LEVEL; i++) {
            if (i == 0) {
                mExportVolumeData.append("\t");
                mExportVolumeData.append(mMusicNewVolumeDataArray[i]);
                mExportVolumeData.append(",\n");
            } else {
                if (i % 5 == 1) {
                    mExportVolumeData.append("\t");
                    mExportVolumeData.append(mMusicNewVolumeDataArray[i]);
                    mExportVolumeData.append(",\t\t");
                } else if (i % 5 == 0) {
                    mExportVolumeData.append(mMusicNewVolumeDataArray[i]);
                    mExportVolumeData.append(",\n");
                } else {
                    mExportVolumeData.append(mMusicNewVolumeDataArray[i]);
                    mExportVolumeData.append(",\t\t");
                }
            }
        }
        mExportVolumeData.append(mMusicNewVolumeDataArray[MAX_VOLUME_LEVEL]);
        mExportVolumeData.append("\n};\n\n");

        mExportVolumeData.append("UINT32 DVP_VOLUME_FM_DAC[40+1] =\n{\n");
        for (int i = 0; i < MAX_VOLUME_LEVEL; i++) {
            if (i == 0) {
                mExportVolumeData.append("\t");
                mExportVolumeData.append(mAvinNewVolumeDataArray[i]);
                mExportVolumeData.append(",\n");
            } else {
                if (i % 5 == 1) {
                    mExportVolumeData.append("\t");
                    mExportVolumeData.append(mAvinNewVolumeDataArray[i]);
                    mExportVolumeData.append(",\t\t");
                } else if (i % 5 == 0) {
                    mExportVolumeData.append(mAvinNewVolumeDataArray[i]);
                    mExportVolumeData.append(",\n");
                } else {
                    mExportVolumeData.append(mAvinNewVolumeDataArray[i]);
                    mExportVolumeData.append(",\t\t");
                }
            }
        }
        mExportVolumeData.append(mAvinNewVolumeDataArray[MAX_VOLUME_LEVEL]);
        mExportVolumeData.append("\n};\n\n");

        mExportVolumeData.append("UINT32 DVP_VOLUME_BT_DAC[40+1] =\n{\n");
        for (int i = 0; i < MAX_VOLUME_LEVEL; i++) {
            if (i == 0) {
                mExportVolumeData.append("\t");
                mExportVolumeData.append(mBtNewVolumeDataArray[i]);
                mExportVolumeData.append(",\n");
            } else {
                if (i % 5 == 1) {
                    mExportVolumeData.append("\t");
                    mExportVolumeData.append(mBtNewVolumeDataArray[i]);
                    mExportVolumeData.append(",\t\t");
                } else if (i % 5 == 0) {
                    mExportVolumeData.append(mBtNewVolumeDataArray[i]);
                    mExportVolumeData.append(",\n");
                } else {
                    mExportVolumeData.append(mBtNewVolumeDataArray[i]);
                    mExportVolumeData.append(",\t\t");
                }
            }
        }
        mExportVolumeData.append(mBtNewVolumeDataArray[MAX_VOLUME_LEVEL]);
        mExportVolumeData.append("\n};");

        Log.e(TAG, mExportVolumeData.toString());

        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.export_title) + "\n\t\t" + VOLUME_DATA_FILE_PATH);
        builder.setPositiveButton(R.string.btn_ok,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        writeFileToSDCard(getApplicationContext(), VOLUME_DATA_FILE_PATH,
                                mExportVolumeData.toString());
                    }
                });
        builder.setNegativeButton(R.string.btn_cancel,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void doLoading() {
        File file = new File(VOLUME_DATA_FILE_PATH);
        if (!file.exists()) {
            Toast.makeText(this, R.string.loading_error, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.loading_title) + "\n\t\t" + VOLUME_DATA_FILE_PATH);
        builder.setPositiveButton(R.string.btn_ok,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String contents = readFileFromSDCard(getApplicationContext(),
                                VOLUME_DATA_FILE_PATH);
                        // Log.e(TAG, contents);
                        String[] array1 = contents.split(";");
                        for (int i = 0; i < array1.length; i++) {
                            int index1 = array1[i].indexOf("{");
                            int index2 = array1[i].indexOf("}");
                            String text1 = array1[i].substring(index1 + 1, index2);
                            String[] array2 = text1.split(",");
                            if (i == 0) {
                                for (int j = 0; j < array2.length; j++) {
                                    String text2 = array2[j].trim();
                                    mMusicNewVolumeDataArray[j] = Integer.parseInt(text2);
                                }
                            } else if (i == 1) {
                                for (int j = 0; j < array2.length; j++) {
                                    String text2 = array2[j].trim();
                                    mAvinNewVolumeDataArray[j] = Integer.parseInt(text2);
                                }
                            } else if (i == 2) {
                                for (int j = 0; j < array2.length; j++) {
                                    String text2 = array2[j].trim();
                                    mBtNewVolumeDataArray[j] = Integer.parseInt(text2);
                                }
                            }
                        }

                        setVolumeData();
                    }
                });
        builder.setNegativeButton(R.string.btn_cancel,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }

    private void doExit() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(getString(R.string.exit_query_msg));
        builder.setPositiveButton(R.string.btn_ok,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        VolumeAdjustMainActivity.this.finish();
                    }

                });
        builder.setNegativeButton(R.string.btn_cancel,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        builder.create().show();
    }

    private class VolumeStreamTypeItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCurrStreamType = position;

            mCurrVolumeLevel = 0;
            mSPVolumeIndex.setSelection(mCurrVolumeLevel);
            setVolumeData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private class VolumeIndexItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCurrVolumeLevel = position;

            setVolumeData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

}
