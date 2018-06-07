
package com.yecon.yeconfactorytesting;

import android.location.GpsStatus.Listener;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

import com.autochips.bluetooth.LocalBluetoothManager;
import com.autochips.dvp.DvdLogicManager;
import com.autochips.dvp.MultiMediaConstant;
import com.autochips.inputsource.AVIN;
import com.autochips.inputsource.InputSource;
import com.autochips.storage.EnvironmentATC;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.constant.YeconConstants.*;
import static com.yecon.yeconfactorytesting.Constants.*;
import static com.yecon.yeconfactorytesting.FactoryTestUtil.*;

public class FactoryTestingMainActivity extends Activity implements OnClickListener, Listener {
    private static final int AUTO_TEST_SD_DONE = 0x00000001;
    private static final int AUTO_TEST_USB_DONE = 0x00000002;
    private static final int AUTO_TEST_COM_DONE = 0x00000004;
    private static final int AUTO_TEST_WIFI_DONE = 0x00000008;
    private static final int AUTO_TEST_AUX_DONE = 0x00000010;
    private static final int AUTO_TEST_DVD_DONE = 0x00000020;
    private static final int AUTO_TEST_ALL_DONE = AUTO_TEST_SD_DONE | AUTO_TEST_USB_DONE
            | AUTO_TEST_COM_DONE | AUTO_TEST_WIFI_DONE | AUTO_TEST_AUX_DONE | AUTO_TEST_DVD_DONE;

    private static String mRecordFile;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(THREAD_POOL_MAX_NUM);

    private FactoryConfigInfo mConfigInfo;

    private EnvironmentATC mEnv;

    private AudioManager mAudioManager;

    private LocationManager mLocationManager;

    private Iterator<GpsSatellite> mIterator;
    private ArrayList<GPSInfoItem> mGPSInfoItemList = new ArrayList<GPSInfoItem>();

    private int mCurrComType = COM3_TEST;

    private int mAuxTestCount = 0;

    private AVIN mAvinV;
    private AVIN mAvinA;

    private TestCOM mTestCOM;

    private TestWifi mTestWifi;

    private int mCurrVideoPort = AVIN.PORT_NONE;
    private int mCurrAudioPort = AVIN.PORT_NONE;

    private int mBTStatus = -1;
    private LocalBluetoothManager mLocalBluetoothManager;

    private DvdLogicManager mLogiccd;

    private AudioRecord mAudioRecord;

    private boolean mIsRecording = false;

    private int mRecordBufferSize;

    private int mAutoTestDoneFlag = 0;

    private int mTestType = -1;

    private ImageView mIVSD1;
    private ImageView mIVSD2;
    private ImageView mIVUSB0;
    private ImageView mIVUSB1;
    private ImageView mIVCOM3;
    private ImageView mIVCOM4;
    private ImageView mIVCOM6;

    private ImageView mIVAUX1Left;
    private ImageView mIVAUX1Right;
    private ImageView mIVAUX1Video;
    private ImageView mIVAUX2Left;
    private ImageView mIVAUX2Right;
    private ImageView mIVAUX2Video;
    private ImageView mIVAUX3Left;
    private ImageView mIVAUX3Right;
    private ImageView mIVAUX3Video;
    private ImageView mIVAUX4Left;
    private ImageView mIVAUX4Right;
    private ImageView mIVAUX4Video;
    private ImageView mIVAUX5Left;
    private ImageView mIVAUX5Right;
    private ImageView mIVAUX5Video;

    private ImageView mIVBT;
    private ImageView mIVDVD;
    private ImageView mIVWifi;
    private ImageView mIVNavi;
    private ImageView mIVStorage;

    private TextView mTVBT;
    private TextView mTVKernelVersion;
    private TextView mTVStorage;
    private TextView mTVPrompt;
    private TextView mTVNavi;
    private TextView mTVRec;
    private TextView mTVLight;
    private TextView mTVVideo;
    private TextView mTVBack;
    private TextView mTVCarlife;

    private TextView mTVBTCover;
    private TextView mTVNaviCover;
    private TextView mTVLightCover;
    private TextView mTVVideoCover;
    private TextView mTVBackCover;
    private TextView mTVCarlifeCover;

    private boolean mBTTestStarted = false;

    private boolean mHasTestDvd = false;

    private boolean mTestStorageResult = true;

    private boolean testSDResult(int what) {
        boolean ret = true;
        switch (what) {
            case MSG_SDCARD1_ERROR:
                mIVSD1.setImageResource(R.drawable.icon_error);
                mHandler.sendEmptyMessage(MSG_SDCARD1_DONE);
                break;

            case MSG_SDCARD1_SUCCESS:
                mIVSD1.setImageResource(R.drawable.icon_ok);
                mHandler.sendEmptyMessage(MSG_SDCARD1_DONE);
                break;

            case MSG_SDCARD1_DONE:
                if (NEED_TEST_WIFI) {
                    mAutoTestDoneFlag |= AUTO_TEST_SD_DONE;
                    if (mAutoTestDoneFlag == AUTO_TEST_ALL_DONE) {
                        mHandler.sendEmptyMessage(MSG_ALLAUTOTEST_DONE);
                    }
                }
                break;

            case MSG_SDCARD2_ERROR:
                mIVSD2.setImageResource(R.drawable.icon_error);
                mHandler.sendEmptyMessage(MSG_SDCARD2_DONE);
                break;

            case MSG_SDCARD2_SUCCESS:
                mIVSD2.setImageResource(R.drawable.icon_ok);
                mHandler.sendEmptyMessage(MSG_SDCARD2_DONE);
                break;

            case MSG_SDCARD2_DONE:
                mAutoTestDoneFlag |= AUTO_TEST_SD_DONE;
                if (mAutoTestDoneFlag == AUTO_TEST_ALL_DONE) {
                    mHandler.sendEmptyMessage(MSG_ALLAUTOTEST_DONE);
                }
                break;

            default:
                ret = false;
                break;
        }

        return ret;
    }

    private boolean testUDISKResult(int what) {
        boolean ret = true;
        switch (what) {
            case MSG_UDISK0_ERROR:
                mIVUSB0.setImageResource(R.drawable.icon_error);
                if (!NEED_TEST_CARLIFE) {
                    mHandler.sendEmptyMessage(MSG_UDISK_DONE);
                }
                break;

            case MSG_UDISK0_SUCCESS:
                mIVUSB0.setImageResource(R.drawable.icon_ok);
                if (NEED_TEST_CARLIFE) {
                    mHandler.sendEmptyMessage(MSG_UDISK_DONE);
                }
                break;

            case MSG_UDISK1_ERROR:
                mIVUSB1.setImageResource(R.drawable.icon_error);
                mHandler.sendEmptyMessage(MSG_UDISK_DONE);
                break;

            case MSG_UDISK1_SUCCESS:
                mIVUSB1.setImageResource(R.drawable.icon_ok);
                mHandler.sendEmptyMessage(MSG_UDISK_DONE);
                break;

            case MSG_UDISK_DONE:
                mAutoTestDoneFlag |= AUTO_TEST_USB_DONE;
                if (mAutoTestDoneFlag == AUTO_TEST_ALL_DONE) {
                    mHandler.sendEmptyMessage(MSG_ALLAUTOTEST_DONE);
                }
                break;

            default:
                ret = false;
                break;
        }

        return ret;
    }

    private boolean testCOMResult(int what) {
        boolean ret = true;
        switch (what) {
            case MSG_COM_ALL_ERROR:
                mIVCOM3.setImageResource(R.drawable.icon_error);
                mIVCOM4.setImageResource(R.drawable.icon_error);
                mIVCOM6.setImageResource(R.drawable.icon_error);
                mHandler.sendEmptyMessage(MSG_COM_DONE);
                break;

            case MSG_COM_TEST:
                onComAutoTest();
                break;

            case MSG_COM3_TEST:
                mCurrComType = COM3_TEST;
                mTestCOM.singleComTest(mHandler, mCurrComType);
                break;

            case MSG_COM4_TEST:
                mCurrComType = COM4_TEST;
                mTestCOM.singleComTest(mHandler, mCurrComType);
                break;

            case MSG_COM6_TEST:
                mCurrComType = COM6_TEST;
                mTestCOM.singleComTest(mHandler, mCurrComType);
                break;

            case MSG_COM3_ERROR:
                mIVCOM3.setImageResource(R.drawable.icon_error);
                mHandler.sendEmptyMessageDelayed(MSG_COM4_TEST, COM_TEST_DELAY_TIME);
                break;

            case MSG_COM3_SUCCESS:
                mIVCOM3.setImageResource(R.drawable.icon_ok);
                mHandler.sendEmptyMessageDelayed(MSG_COM4_TEST, COM_TEST_DELAY_TIME);
                break;

            case MSG_COM4_ERROR:
                mIVCOM4.setImageResource(R.drawable.icon_error);
                mHandler.sendEmptyMessageDelayed(MSG_COM6_TEST, COM_TEST_DELAY_TIME);
                break;

            case MSG_COM4_SUCCESS:
                mIVCOM4.setImageResource(R.drawable.icon_ok);
                mHandler.sendEmptyMessageDelayed(MSG_COM6_TEST, COM_TEST_DELAY_TIME);
                break;

            case MSG_COM6_ERROR:
                mIVCOM6.setImageResource(R.drawable.icon_error);
                mHandler.sendEmptyMessageDelayed(MSG_COM_DONE, COM_TEST_DELAY_TIME);
                break;

            case MSG_COM6_SUCCESS:
                mIVCOM6.setImageResource(R.drawable.icon_ok);
                mHandler.sendEmptyMessageDelayed(MSG_COM_DONE, COM_TEST_DELAY_TIME);
                break;

            case MSG_COM_DONE:
                mTestCOM.startUartService(false);

                mAutoTestDoneFlag |= AUTO_TEST_COM_DONE;
                if (mAutoTestDoneFlag == AUTO_TEST_ALL_DONE) {
                    mHandler.sendEmptyMessage(MSG_ALLAUTOTEST_DONE);
                }
                break;

            default:
                ret = false;
                break;
        }

        return ret;
    }

    private boolean testAUXResult(int what) {
        boolean ret = true;
        switch (what) {
            case MSG_AUX_NO_INPUT_TEST:
                mAuxTestCount++;
                printLog("testAUXResult - aux test count: " + mAuxTestCount);
                if (mAuxTestCount < REPEAT_TEST_COUNT) {
                    onAUXAutoTest();
                    break;
                } else {
                    mAuxTestCount = 0;
                }

                int tempWhat = 0;
                switch (mCurrVideoPort) {
                    case AVIN.PORT1:
                        mIVAUX1Left.setImageResource(R.drawable.icon_error);
                        mIVAUX1Right.setImageResource(R.drawable.icon_error);
                        mIVAUX1Video.setImageResource(R.drawable.icon_error);
                        tempWhat = MSG_AUX1_DONE;
                        break;

                    case AVIN.PORT2:
                        mIVAUX2Left.setImageResource(R.drawable.icon_error);
                        mIVAUX2Right.setImageResource(R.drawable.icon_error);
                        mIVAUX2Video.setImageResource(R.drawable.icon_error);
                        tempWhat = MSG_AUX2_DONE;
                        break;

                    case AVIN.PORT3:
                        mIVAUX3Left.setImageResource(R.drawable.icon_error);
                        mIVAUX3Right.setImageResource(R.drawable.icon_error);
                        mIVAUX3Video.setImageResource(R.drawable.icon_error);
                        tempWhat = MSG_AUX3_DONE;
                        break;

                    case AVIN.PORT4:
                        mIVAUX4Left.setImageResource(R.drawable.icon_error);
                        mIVAUX4Right.setImageResource(R.drawable.icon_error);
                        mIVAUX4Video.setImageResource(R.drawable.icon_error);
                        tempWhat = MSG_AUX4_DONE;
                        break;

                    case AVIN.PORT5:
                        mIVAUX5Left.setImageResource(R.drawable.icon_error);
                        mIVAUX5Right.setImageResource(R.drawable.icon_error);
                        mIVAUX5Video.setImageResource(R.drawable.icon_error);
                        tempWhat = MSG_AUX5_DONE;
                        break;
                }
                mHandler.sendEmptyMessage(tempWhat);
                break;

            case MSG_AUX_AUDIO_REPEAT_TEST:
                mAuxTestCount++;
                if (mAuxTestCount <= REPEAT_TEST_COUNT) {
                    TestAux.onAUXAudioTest(mAvinA, mHandler, mCurrAudioPort, mAuxTestCount);
                }
                break;

            case MSG_AUX1_LEFT_ERROR:
                mIVAUX1Left.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX1_RIGHT_ERROR:
                mIVAUX1Right.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX1_VIDEO_ERROR:
                mIVAUX1Video.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX1_LEFT_SUCCESS:
                mIVAUX1Left.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX1_RIGHT_SUCCESS:
                mIVAUX1Right.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX1_VIDEO_SUCCESS:
                mIVAUX1Video.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX1_DONE:
                mAuxTestCount = 0;
                mCurrVideoPort = AVIN.PORT2;
                mCurrAudioPort = AVIN.PORT2;
                onAUXAutoTest();
                break;

            case MSG_AUX2_LEFT_ERROR:
                mIVAUX2Left.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX2_RIGHT_ERROR:
                mIVAUX2Right.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX2_VIDEO_ERROR:
                mIVAUX2Video.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX2_LEFT_SUCCESS:
                mIVAUX2Left.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX2_RIGHT_SUCCESS:
                mIVAUX2Right.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX2_VIDEO_SUCCESS:
                mIVAUX2Video.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX2_DONE:
                mAuxTestCount = 0;
                mCurrVideoPort = AVIN.PORT3;
                mCurrAudioPort = AVIN.PORT3;
                onAUXAutoTest();
                break;

            case MSG_AUX3_LEFT_ERROR:
                mIVAUX3Left.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX3_RIGHT_ERROR:
                mIVAUX3Right.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX3_VIDEO_ERROR:
                mIVAUX3Video.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX3_LEFT_SUCCESS:
                mIVAUX3Left.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX3_RIGHT_SUCCESS:
                mIVAUX3Right.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX3_VIDEO_SUCCESS:
                mIVAUX3Video.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX3_DONE:
                mAuxTestCount = 0;
                mCurrVideoPort = AVIN.PORT4;
                mCurrAudioPort = AVIN.PORT4;
                onAUXAutoTest();
                break;

            case MSG_AUX4_LEFT_ERROR:
                mIVAUX4Left.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX4_RIGHT_ERROR:
                mIVAUX4Right.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX4_VIDEO_ERROR:
                mIVAUX4Video.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX4_LEFT_SUCCESS:
                mIVAUX4Left.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX4_RIGHT_SUCCESS:
                mIVAUX4Right.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX4_VIDEO_SUCCESS:
                mIVAUX4Video.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX4_DONE:
                mAuxTestCount = 0;
                mCurrVideoPort = AVIN.PORT5;
                mCurrAudioPort = AVIN.PORT5;
                onAUXAutoTest();
                break;

            case MSG_AUX5_LEFT_ERROR:
                mIVAUX5Left.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX5_RIGHT_ERROR:
                mIVAUX5Right.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX5_VIDEO_ERROR:
                mIVAUX5Video.setImageResource(R.drawable.icon_error);
                break;

            case MSG_AUX5_LEFT_SUCCESS:
                mIVAUX5Left.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX5_RIGHT_SUCCESS:
                mIVAUX5Right.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX5_VIDEO_SUCCESS:
                mIVAUX5Video.setImageResource(R.drawable.icon_ok);
                break;

            case MSG_AUX5_DONE:
                resetAVIN();

                if (!BT_TEST_CALL) {
                    mHandler.sendEmptyMessage(MSG_BT_TEST);
                }

                mAutoTestDoneFlag |= AUTO_TEST_AUX_DONE;
                if (mAutoTestDoneFlag == AUTO_TEST_ALL_DONE) {
                    mHandler.sendEmptyMessage(MSG_ALLAUTOTEST_DONE);
                }
                break;

            default:
                ret = false;
                break;
        }

        return ret;
    }

    private boolean testBTResult(int what) {
        boolean ret = true;
        switch (what) {
            case MSG_BT_TEST:
                mBTTestStarted = true;

                onBTAutoTest();
                break;

            case MSG_BT_ERROR:
                if (mIVBT != null) {
                    mIVBT.setImageResource(R.drawable.icon_error);
                }
                mHandler.sendEmptyMessage(MSG_BT_DONE);
                break;

            case MSG_BT_SUCCESS:
                if (mIVBT != null) {
                    mIVBT.setImageResource(R.drawable.icon_ok);
                }
                mHandler.sendEmptyMessage(MSG_BT_DONE);
                break;

            case MSG_BT_DONE:
                // mHandler.sendEmptyMessageDelayed(MSG_DVD_TEST, DVD_TEST_DELAY_TIME);
                break;

            default:
                ret = false;
                break;
        }

        return ret;
    }

    private boolean testDVDResult(int what) {
        boolean ret = true;
        switch (what) {
            case MSG_DVD_TEST:
                onDVDAutoTest();
                break;

            case MSG_DVD_ERROR:
                mHandler.removeMessages(MSG_DVD_ERROR);

                mIVDVD.setImageResource(R.drawable.icon_error);
                mHandler.sendEmptyMessage(MSG_DVD_DONE);
                break;

            case MSG_DVD_SUCCESS:
                mHandler.removeMessages(MSG_DVD_ERROR);

                mIVDVD.setImageResource(R.drawable.icon_ok);
                mHandler.sendEmptyMessage(MSG_DVD_DONE);
                break;

            case MSG_DVD_DONE:
                stopDVD();
                mAutoTestDoneFlag |= AUTO_TEST_DVD_DONE;
                if (mAutoTestDoneFlag == AUTO_TEST_ALL_DONE) {
                    mHandler.sendEmptyMessage(MSG_ALLAUTOTEST_DONE);
                }
                break;

            default:
                ret = false;
                break;
        }

        return ret;
    }

    private boolean testWIFIResult(int what) {
        boolean ret = true;
        switch (what) {
            case MSG_WIFI_TEST:
                if (WIFI_TEST_HOT_LIST) {
                    if (mTestWifi.getWifiList().size() > 0) {
                        mHandler.sendEmptyMessage(MSG_WIFI_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(MSG_WIFI_ERROR);
                    }
                } else {
                    new Thread() {

                        @Override
                        public void run() {
                            super.run();

                            if (mTestWifi == null) {
                                return;
                            }

                            if (mTestWifi.getWebpage()) {
                                mHandler.sendEmptyMessage(MSG_WIFI_SUCCESS);
                            } else {
                                mHandler.sendEmptyMessage(MSG_WIFI_ERROR);
                            }
                        }

                    }.start();
                }
                break;

            case MSG_WIFI_ERROR:
                mIVWifi.setImageResource(R.drawable.icon_error);
                mHandler.sendEmptyMessage(MSG_WIFI_DONE);
                break;

            case MSG_WIFI_SUCCESS:
                mIVWifi.setImageResource(R.drawable.icon_ok);
                mHandler.sendEmptyMessage(MSG_WIFI_DONE);
                break;

            case MSG_WIFI_DONE:
                mHandler.removeMessages(MSG_WIFI_TEST);
                mHandler.removeMessages(MSG_WIFI_ERROR);

                if (mTestWifi != null) {
                    mTestWifi.closeWifi();
                    mTestWifi = null;
                }

                mAutoTestDoneFlag |= AUTO_TEST_WIFI_DONE;
                if (mAutoTestDoneFlag == AUTO_TEST_ALL_DONE) {
                    mHandler.sendEmptyMessage(MSG_ALLAUTOTEST_DONE);
                }
                break;

            default:
                ret = false;
                break;
        }

        return ret;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String log = String.format("mHandler - msg.what: 0x%04X", msg.what);
            printLog(log);

            if (testSDResult(msg.what)) {
                return;
            }

            if (testUDISKResult(msg.what)) {
                return;
            }

            if (testCOMResult(msg.what)) {
                return;
            }

            if (testAUXResult(msg.what)) {
                return;
            }

            if (testBTResult(msg.what)) {
                return;
            }

            if (testDVDResult(msg.what)) {
                return;
            }

            if (testWIFIResult(msg.what)) {
                return;
            }

            switch (msg.what) {
                case MSG_ALLAUTOTEST_DONE:
                    manualTestViewEnable(TEST_TYPE_DEFAULT, true);

                    mTVPrompt.setTextColor(Color.GREEN);
                    mTVPrompt.setText(R.string.auto_test_done);
                    break;

                case MSG_REC_START_REC:
                    mTVRec.setTextColor(getResources().getColor(R.color.orange));
                    mTVRec.setEnabled(false);
                    // mTVRec.setBackgroundResource(R.drawable.icon_rec_down);

                    onRecordingManualTest();

                    mHandler.sendEmptyMessage(MSG_REC_RECORDING);
                    break;

                case MSG_REC_RECORDING:
                    mTVRec.setText(R.string.rec_prompt);
                    mTVRec.setTextColor(getResources().getColor(R.color.red));

                    mHandler.sendEmptyMessageDelayed(MSG_REC_STOP_REC, REC_DURATION_TIME);
                    break;

                case MSG_REC_STOP_REC:
                    mTVRec.setText(R.string.rec_stop);
                    mTVRec.setTextColor(getResources().getColor(R.color.cyan));

                    mHandler.sendEmptyMessageDelayed(MSG_REC_START_PLAY, REC_TEST_DELAY_TIME);
                    break;

                case MSG_REC_START_PLAY:
                    mTVRec.setText(R.string.rec_play_start);
                    mTVRec.setTextColor(getResources().getColor(R.color.green));

                    startPlayback();
                    break;

                case MSG_REC_STOP_PLAY:
                    mTVRec.setText(R.string.rec_play_stop);
                    mTVRec.setTextColor(getResources().getColor(R.color.red));

                    mTVRec.setEnabled(true);
                    break;

                case MSG_GPIO_START:
                    mTVLight.setEnabled(false);
                    if (TestGPIO.GPIO_SINGLE_TEST) {
                        if (TestGPIO.mGPIOEnable) {
                            mTVLight.setText(TestGPIO.GPIO_PORT_NUM[TestGPIO.mGPIOIndex] + " - 0");
                        } else {
                            mTVLight.setText(TestGPIO.GPIO_PORT_NUM[TestGPIO.mGPIOIndex] + " - 1");
                        }
                    }
                    break;

                case MSG_GPIO_END:
                    mTVLight.setEnabled(true);
                    break;

                case MSG_GPS_TEST:
                    mHandler.sendEmptyMessage(MSG_GPS_ERROR);
                    break;

                case MSG_GPS_ERROR:
                    mIVNavi.setImageResource(R.drawable.icon_error);
                    break;

                case MSG_GPS_SUCCESS:
                    mIVNavi.setImageResource(R.drawable.icon_ok);
                    break;
            }
        }

    };

    @SuppressLint("HandlerLeak")
    private Handler mDVDHomeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            printLog("mDVDHomeHandler - msg.what: " + msg.what);

            switch (msg.what) {
                case MultiMediaConstant.DVD_LOAD_OK:
                case MultiMediaConstant.VCD_LOAD_OK:
                case MultiMediaConstant.DDISC_LOAD_OK:
                case MultiMediaConstant.CD_LOAD_OK:
                case MultiMediaConstant.CDG_LOAD_OK:
                case MultiMediaConstant.PICDISC_LOAD_OK:
                    mHandler.removeMessages(MSG_DVD_ERROR);
                    mHandler.sendEmptyMessage(MSG_DVD_SUCCESS);
                    break;

                case MultiMediaConstant.NODISC:
                case MultiMediaConstant.UNKOWN_DISC:
                    mHandler.removeMessages(MSG_DVD_ERROR);
                    mHandler.sendEmptyMessage(MSG_DVD_ERROR);
                    break;
            }
        }

    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            printLog("mBroadcastReceiver - action: " + action);
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                printLog("mBroadcastReceiver - btState: " + btState + " - mBTStatus: " + mBTStatus);
                if (mBTTestStarted) {
                    if (btState == BluetoothAdapter.STATE_TURNING_ON
                            || btState == BluetoothAdapter.STATE_TURNING_OFF) {
                        return;
                    }
                    if (((mBTStatus != BluetoothAdapter.STATE_ON) && btState == BluetoothAdapter.STATE_ON)
                            || ((mBTStatus == BluetoothAdapter.STATE_ON) && btState == BluetoothAdapter.STATE_OFF)) {
                        mHandler.sendEmptyMessage(MSG_BT_SUCCESS);

                        mBTTestStarted = false;
                    } else {
                        mHandler.sendEmptyMessage(MSG_BT_ERROR);

                        mBTTestStarted = false;
                    }
                }
            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                printLog("mReceiver - wifiState: " + wifiState);
                if (mTestWifi != null && wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    if (WIFI_TEST_HOT_LIST) {
                        /**
                         * 如果wifi已经打开，则扫描可用的wifi热点
                         */
                        mTestWifi.startScan();
                        mHandler.removeMessages(MSG_WIFI_TEST);
                        mHandler.sendEmptyMessageDelayed(MSG_WIFI_TEST, WIFI_TEST_HOT_DELAY_TIME);
                    } else {
                        mHandler.removeMessages(MSG_WIFI_TEST);
                        mHandler.removeMessages(MSG_WIFI_ERROR);

                        String password = mConfigInfo.getWifiConnectPassword();
                        int type = TestWifi.WIFICIPHER_WPA;
                        if (password == null || password.length() == 0) {
                            type = TestWifi.WIFICIPHER_NOPASS;
                        }
                        WifiConfiguration wifiConfig = mTestWifi.createWifiInfo(
                                mConfigInfo.getWifiConnectSSID(),
                                mConfigInfo.getWifiConnectPassword(),
                                type);
                        if (wifiConfig != null) {
                            mTestWifi.addNetwork(wifiConfig);
                        }
                    }
                } else if (wifiState == WifiManager.WIFI_STATE_UNKNOWN) {
                    mHandler.sendEmptyMessage(MSG_WIFI_ERROR);
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                if ((mAutoTestDoneFlag & AUTO_TEST_WIFI_DONE) == AUTO_TEST_WIFI_DONE) {
                    printLog("mReceiver - AUTO_TEST_WIFI_DONE");
                    return;
                }

                if (intent != null) {
                    if (!WIFI_TEST_HOT_LIST) {
                        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(
                                WifiManager.EXTRA_NETWORK_INFO);
                        printLog("mReceiver - networkInfo: " + networkInfo.toString());

                        NetworkInfo.State state = networkInfo.getState();

                        mHandler.removeMessages(MSG_WIFI_TEST);
                        mHandler.removeMessages(MSG_WIFI_ERROR);
                        if (state == NetworkInfo.State.CONNECTED) {
                            if (WIFI_TEST_CONNECT) {
                                mHandler.sendEmptyMessageDelayed(MSG_WIFI_SUCCESS,
                                        WIFI_TEST_WEB_DELAY_TIME);
                            } else {
                                mHandler.sendEmptyMessageDelayed(MSG_WIFI_TEST,
                                        WIFI_TEST_WEB_DELAY_TIME);
                            }
                        } else if (state == NetworkInfo.State.CONNECTING ||
                                state == NetworkInfo.State.DISCONNECTED ||
                                state == NetworkInfo.State.DISCONNECTING ||
                                state == NetworkInfo.State.SUSPENDED ||
                                state == NetworkInfo.State.UNKNOWN) {
                            mHandler.sendEmptyMessageDelayed(MSG_WIFI_ERROR,
                                    WIFI_TEST_ERROR_DELAY_TIME);
                        }
                    }
                }
            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {

            }
        }
    };

    /**
     * AUX信号检测，只有检测到AUX有信号了，才测试AUX的音频信号和视频信号
     */
    private AVIN.OnSignalListener mListenerSignal = new AVIN.OnSignalListener() {

        @Override
        public void onSignal(int msg, int param1, int param2) {
            printLog("mListenerSignal - msg: " + msg + " - mCurrVideoPort: " + mCurrVideoPort);

            mHandler.removeMessages(MSG_AUX_NO_INPUT_TEST);

            TestAux.onAUXVideoTest(msg, mCurrVideoPort, mHandler);

            mAuxTestCount = 1;
            TestAux.onAUXAudioTest(mAvinA, mHandler, mCurrAudioPort, mAuxTestCount);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        printLog("FactoryTestingMainActivity - onCreate");

        initData();

        initUI();

        if (mTestType == TEST_TYPE_BT) {
            manualTestViewEnable(TEST_TYPE_BT, true);
        } else if (mTestType == TEST_TYPE_CARLIFE) {
            manualTestViewEnable(TEST_TYPE_CARLIFE, true);
        } else {
            manualTestViewEnable(TEST_TYPE_DEFAULT, false);

            startupAutoTest();
        }
    }

    private void setFactoryTestVolume() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mConfigInfo
                .getDefaultVolume(), 0);
    }

    private void initData() {
        parseFactoryConfig();
        mConfigInfo = getConfigInfo();

        mTestType = mConfigInfo.getTestType();

        setFactoryTestVolume();

        mEnv = new EnvironmentATC(this);

        TestGPIO.mGPIOEnable = false;
        TestGPIO.mGPIOIndex = 0;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mBroadcastReceiver, filter);

        mCurrVideoPort = AVIN.PORT1;
        mCurrAudioPort = AVIN.PORT1;
    }

    private void initUI() {
        setContentView(R.layout.factory_testing_main_activity);

        mIVSD1 = (ImageView) findViewById(R.id.iv_sd1);
        mIVSD2 = (ImageView) findViewById(R.id.iv_sd2);

        mIVUSB0 = (ImageView) findViewById(R.id.iv_usb0);
        mIVUSB1 = (ImageView) findViewById(R.id.iv_usb1);

        mIVCOM3 = (ImageView) findViewById(R.id.iv_com3);
        mIVCOM4 = (ImageView) findViewById(R.id.iv_com4);
        mIVCOM6 = (ImageView) findViewById(R.id.iv_com6);

        mIVAUX1Left = (ImageView) findViewById(R.id.iv_aux1_left);
        mIVAUX1Right = (ImageView) findViewById(R.id.iv_aux1_right);
        mIVAUX1Video = (ImageView) findViewById(R.id.iv_aux1_video);
        mIVAUX2Left = (ImageView) findViewById(R.id.iv_aux2_left);
        mIVAUX2Right = (ImageView) findViewById(R.id.iv_aux2_right);
        mIVAUX2Video = (ImageView) findViewById(R.id.iv_aux2_video);
        mIVAUX3Left = (ImageView) findViewById(R.id.iv_aux3_left);
        mIVAUX3Right = (ImageView) findViewById(R.id.iv_aux3_right);
        mIVAUX3Video = (ImageView) findViewById(R.id.iv_aux3_video);
        mIVAUX4Left = (ImageView) findViewById(R.id.iv_aux4_left);
        mIVAUX4Right = (ImageView) findViewById(R.id.iv_aux4_right);
        mIVAUX4Video = (ImageView) findViewById(R.id.iv_aux4_video);
        mIVAUX5Left = (ImageView) findViewById(R.id.iv_aux5_left);
        mIVAUX5Right = (ImageView) findViewById(R.id.iv_aux5_right);
        mIVAUX5Video = (ImageView) findViewById(R.id.iv_aux5_video);

        if (!BT_TEST_CALL) {
            mIVBT = (ImageView) findViewById(R.id.iv_bt);
        } else {
            mTVBT = (TextView) findViewById(R.id.tv_bt);
            mTVBT.setOnClickListener(this);
        }

        mIVDVD = (ImageView) findViewById(R.id.iv_dvd);
        mIVWifi = (ImageView) findViewById(R.id.iv_wifi);
        mIVNavi = (ImageView) findViewById(R.id.iv_navi);

        mTVKernelVersion = (TextView) findViewById(R.id.tv_version);
        mTVKernelVersion.setText(getFormattedKernelVersion());

        mTVStorage = (TextView) findViewById(R.id.tv_storage);
        mTVStorage.setText(getStorageInfo());

        mIVStorage = (ImageView) findViewById(R.id.iv_storage);
        if (mTestStorageResult) {
            mIVStorage.setImageResource(R.drawable.icon_ok);
        } else {
            mIVStorage.setImageResource(R.drawable.icon_error);
        }

        mTVPrompt = (TextView) findViewById(R.id.tv_prompt);

        mTVNavi = (TextView) findViewById(R.id.tv_navi);
        mTVNavi.setOnClickListener(this);
        mTVRec = (TextView) findViewById(R.id.tv_rec);
        if (BT_TEST_CALL) {
            mTVRec.setVisibility(View.GONE);
        } else {
            mTVRec.setOnClickListener(this);
        }
        mTVLight = (TextView) findViewById(R.id.tv_light);
        mTVLight.setOnClickListener(this);
        mTVVideo = (TextView) findViewById(R.id.tv_video);
        mTVVideo.setOnClickListener(this);

        mTVBack = (TextView) findViewById(R.id.tv_back);
        mTVBack.setOnClickListener(this);

        mTVCarlife = (TextView) findViewById(R.id.tv_carlife);
        mTVCarlife.setOnClickListener(this);

        mTVBTCover = (TextView) findViewById(R.id.tv_bt_cover);
        mTVNaviCover = (TextView) findViewById(R.id.tv_navi_cover);
        mTVLightCover = (TextView) findViewById(R.id.tv_light_cover);
        mTVVideoCover = (TextView) findViewById(R.id.tv_video_cover);
        mTVBackCover = (TextView) findViewById(R.id.tv_back_cover);
        mTVCarlifeCover = (TextView) findViewById(R.id.tv_carlife_cover);

        FrameLayout layoutSD2 = (FrameLayout) findViewById(R.id.layout_sd2);
        FrameLayout layoutWifi = (FrameLayout) findViewById(R.id.layout_wifi);
        if (!NEED_TEST_WIFI) {
            if (layoutSD2 != null) {
                layoutSD2.setVisibility(View.VISIBLE);
            }
            if (layoutWifi != null) {
                layoutWifi.setVisibility(View.GONE);
            }
        } else {
            if (layoutSD2 != null) {
                layoutSD2.setVisibility(View.GONE);
            }
            if (layoutWifi != null) {
                layoutWifi.setVisibility(View.VISIBLE);
            }
        }

        FrameLayout layoutUSB1 = (FrameLayout) findViewById(R.id.layout_usb1);
        FrameLayout layoutCarlife = (FrameLayout) findViewById(R.id.layout_carlife);
        if (!NEED_TEST_CARLIFE) {
            if (layoutUSB1 != null) {
                layoutUSB1.setVisibility(View.VISIBLE);
            }
            if (layoutCarlife != null) {
                layoutCarlife.setVisibility(View.GONE);
            }
        } else {
            if (layoutUSB1 != null) {
                layoutUSB1.setVisibility(View.GONE);
            }
            if (layoutCarlife != null) {
                layoutCarlife.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        printLog("FactoryTestingMainActivity - onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();

        printLog("FactoryTestingMainActivity - onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        printLog("FactoryTestingMainActivity - onDestroy");

        if (mTestCOM != null) {
            mTestCOM.setStopComThread(true);
        }

        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }

        resetAVIN();

        stopDVD();

        removeGpsListener();

        if (mTestWifi != null) {
            mHandler.removeMessages(MSG_WIFI_TEST);
            mTestWifi.closeWifi();
            mTestWifi = null;
        }

        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }

        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_carlife:
                onCarlifeManualTest();
                break;

            case R.id.tv_bt:
                onBTManualTest();
                break;

            case R.id.tv_navi:
                onNaviManualTest();
                break;

            case R.id.tv_rec:
                mTVRec.setEnabled(false);
                mHandler.sendEmptyMessage(MSG_REC_START_PLAY);
                break;

            case R.id.tv_light:
                mExecutorService.submit(new Runnable() {

                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(MSG_GPIO_START);
                        onLightManualTest();
                    }
                });
                break;

            case R.id.tv_video:
                onVideoManualTest();
                break;

            case R.id.tv_back:
                this.finish();
                break;
        }
    }

    private void manualTestViewEnableInter(boolean enable) {
        if (enable) {
            mTVBTCover.setVisibility(View.GONE);
            mTVNaviCover.setVisibility(View.GONE);
            mTVLightCover.setVisibility(View.GONE);
            mTVVideoCover.setVisibility(View.GONE);
            mTVBackCover.setVisibility(View.GONE);
            mTVCarlifeCover.setVisibility(View.GONE);

            mTVBT.setEnabled(true);
            mTVNavi.setEnabled(true);
            mTVRec.setEnabled(true);
            mTVLight.setEnabled(true);
            mTVVideo.setEnabled(true);
            mTVBack.setEnabled(true);
            mTVCarlife.setEnabled(true);
        } else {
            mTVBTCover.setVisibility(View.VISIBLE);
            mTVNaviCover.setVisibility(View.VISIBLE);
            mTVLightCover.setVisibility(View.VISIBLE);
            mTVVideoCover.setVisibility(View.VISIBLE);
            mTVBackCover.setVisibility(View.VISIBLE);
            mTVCarlifeCover.setVisibility(View.VISIBLE);

            mTVBT.setEnabled(false);
            mTVNavi.setEnabled(false);
            mTVRec.setEnabled(false);
            mTVLight.setEnabled(false);
            mTVVideo.setEnabled(false);
            mTVBack.setEnabled(false);
            mTVCarlife.setEnabled(false);
        }
    }

    private void manualTestViewEnable(int testType, boolean enable) {
        if (testType == TEST_TYPE_BT) {
            if (enable) {
                manualTestViewEnableInter(!enable);

                mTVBTCover.setVisibility(View.GONE);
                mTVBackCover.setVisibility(View.GONE);

                mTVBT.setEnabled(true);
                mTVBack.setEnabled(true);
            }
        } else if (testType == TEST_TYPE_CARLIFE) {
            manualTestViewEnableInter(!enable);

            mTVCarlifeCover.setVisibility(View.GONE);
            mTVBackCover.setVisibility(View.GONE);

            mTVCarlife.setEnabled(true);
            mTVBack.setEnabled(true);
        } else {
            manualTestViewEnableInter(enable);
        }
    }

    /**
     * 启动多个线程，开始自动测试SD卡、USB、COM、AUX、WIFI、REC
     */
    private void startupAutoTest() {
        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                TestStorage.onSDCardAutoTest(mEnv, mHandler);
            }
        });

        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                TestStorage.onUDISKAutoTest(mEnv, mHandler);
            }
        });

        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                /**
                 * 要测试COM，首先需要硬件把COM口短路成回路，然后需要软件把arm2的快速倒车和MCU服务屏蔽
                 * 
                 * @有两种测试方式：
                 * @一、启动uart服务，依次测试三个COM口（COM3、COM4、COM6），然后关闭uart服务；
                 * @二、启动uart服务，依次一个COM口，然后关闭uart服务，接着按前面的顺序测试下一个COM口。
                 */
                mHandler.sendEmptyMessageDelayed(MSG_COM_TEST, COM_TEST_DELAY_TIME);
            }
        });

        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                if (NEED_TEST_WIFI) {
                    onWIFIAutoTest();
                }
            }
        });

        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                onAUXAutoTest();
            }
        });

        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                onDVDAutoTest();
            }
        });

        /**
         * 自动测试录音功能
         */
        if (!BT_TEST_CALL) {
            mHandler.sendEmptyMessageDelayed(MSG_REC_START_REC, REC_TEST_DELAY_TIME);
        }

        /**
         * 自动测试导航功能
         */
        // onNaviAutoTest();

        // mHandler.sendEmptyMessage(MSG_ALLAUTOTEST_DONE);
    }

    private void onComAutoTest() {
        printLog("onComAutoTest - start");

        mTestCOM = new TestCOM();

        mTestCOM.onComAutoTest(mHandler);

        printLog("onComAutoTest - end");
    }

    /**
     * 测试AUX功能
     */
    private void onAUXAutoTest() {
        printLog("onAUXAutoTest - start");

        mHandler.removeMessages(MSG_AUX_NO_INPUT_TEST);

        resetAVIN();

        mAvinV = new AVIN();
        mAvinA = new AVIN();

        printLog("onAUXAutoTest - setSource - mCurrVideoPort: " + mCurrVideoPort
                + " - mCurrAudioPort: " + mCurrAudioPort);

        int retValueVideo = mAvinV.setSource(InputSource.SOURCE_TYPE_AVIN, mCurrVideoPort,
                AVIN.PORT_NONE, 0);
        int retValueAudio = mAvinA.setSource(InputSource.SOURCE_TYPE_AVIN,
                AVIN.PORT_NONE, mCurrAudioPort, AVIN.PRIORITY_IN_CBM_LEVEL_DEFAULT);

        // printLog("onAUXAutoTest - setSource - retValueVideo: " + retValueVideo
        // + " - retValueAudio: " + retValueAudio);

        if (InputSource.ERR_FAILED == retValueVideo && InputSource.ERR_FAILED == retValueAudio) {
            int what = MSG_AUX1_VIDEO_ERROR;
            switch (mCurrVideoPort) {
                case AVIN.PORT1:
                    what = MSG_AUX1_VIDEO_ERROR;
                    break;
                case AVIN.PORT2:
                    what = MSG_AUX2_VIDEO_ERROR;
                    break;
                case AVIN.PORT3:
                    what = MSG_AUX3_VIDEO_ERROR;
                    break;
                case AVIN.PORT4:
                    what = MSG_AUX4_VIDEO_ERROR;
                    break;
                case AVIN.PORT5:
                    what = MSG_AUX5_VIDEO_ERROR;
                    break;
            }
            mHandler.sendEmptyMessage(what);
            return;
        }

        mAvinV.setDestination(InputSource.DEST_TYPE_FRONT);
        mAvinA.setDestination(InputSource.DEST_TYPE_FRONT);
        mAvinV.setOnSignalListener(mListenerSignal);
        retValueVideo = mAvinV.play();
        retValueAudio = mAvinA.play();
        // printLog("onAUXAutoTest - play - retValueVideo: " + retValueVideo
        // + " - retValueAudio: " + retValueAudio);

        /**
         * 如果当前AUX无输入超时，则当前AUX检测失败
         */
        mHandler.sendEmptyMessageDelayed(MSG_AUX_NO_INPUT_TEST, AUX_TEST_DELAY_TIME);

        printLog("onAUXAutoTest - end");
    }

    private void onBTAutoTest() {
        setBluetoothState();
    }

    private void onDVDAutoTest() {
        if (!mHasTestDvd) {
            mHasTestDvd = true;

            startupDVD();

            mHandler.sendEmptyMessageDelayed(MSG_DVD_ERROR, DVD_TEST_ERROR_DELAY_TIME);
        }
    }

    /**
     * 测试wifi功能－－打开wifi，获取可用的wifi热点数目不为0，则OK / 数目为0，则NO
     */
    private void onWIFIAutoTest() {
        printLog("onWIFIAutoTest - start");

        mTestWifi = new TestWifi(this);

        mTestWifi.openWifi();

        printLog("onWIFIAutoTest - end");
    }

    @SuppressWarnings("unused")
    private void onNaviAutoTest() {
        printLog("onNaviAutoTest - start");

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        removeGpsListener();
        mLocationManager.addGpsStatusListener(this);

        mHandler.removeMessages(MSG_GPS_TEST);
        mHandler.sendEmptyMessageDelayed(MSG_GPS_TEST, GPS_TEST_DELAY_TIME);
    }

    private void onNaviManualTest() {
        final String NAVI_PACKAGE_NAME = "com.yecon.gpstest";
        final String NAVI_CLASS_NAME = "com.yecon.gpstest.GPSTestActivity";

        Intent mIntent = new Intent(Intent.ACTION_MAIN);
        mIntent.setComponent(new ComponentName(NAVI_PACKAGE_NAME, NAVI_CLASS_NAME));
        startActivity(mIntent);
    }

    private void onRecordingManualTest() {
        stopAudioPlayback();

        stopRecording();

        startRecording();
    }

    private void onLightManualTest() {
        boolean ret = TestGPIO.setGPIOStatus();
        if (ret) {
            mHandler.sendEmptyMessage(MSG_GPIO_END);
        }
    }

    private void onCarlifeManualTest() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("factory_test", true);
        ComponentName cn = new ComponentName(CARLIFE_PACKAGE_NAME, CARLIFE_START_ACTIVITY);
        intent.setComponent(cn);
        startActivity(intent);
    }

    private void onBTManualTest() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("factory_test", true);
        ComponentName cn = new ComponentName(BLUETOOTH_PACKAGE_NAME, BLUETOOTH_START_ACTIVITY);
        intent.setComponent(cn);
        startActivity(intent);
    }

    private void onVideoManualTest() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("factory_test", true);
        ComponentName cn = new ComponentName(VIDEO_PACKAGE_NAME, VIDEO_START_ACTIVITY);
        intent.setComponent(cn);
        startActivity(intent);
    }

    private String getStorageInfo() {
        String strTotalMemory = formatSize(getTotalMemorySize(this));
        String strTotalInternalMemory = formatSize(getTotalInternalMemorySize());

        int mem = Integer.parseInt(strTotalMemory.substring(0, strTotalMemory.length() - 2));
        int flash = Integer.parseInt(strTotalInternalMemory.substring(0, strTotalInternalMemory
                .length() - 2));
        switch (mConfigInfo.getFlashType()) {
            case MEM_1G_FLASH_8G:
                if (mem == 1 && flash == 8) {
                    mTestStorageResult = true;
                } else {
                    mTestStorageResult = false;
                }
                break;

            case MEM_1G_FLASH_16G:
                if (mem == 1 && flash == 16) {
                    mTestStorageResult = true;
                } else {
                    mTestStorageResult = false;
                }
                break;

            case MEM_1G_FLASH_32G:
                if (mem == 1 && flash == 32) {
                    mTestStorageResult = true;
                } else {
                    mTestStorageResult = false;
                }
                break;

            case MEM_2G_FLASH_8G:
                if (mem == 2 && flash == 8) {
                    mTestStorageResult = true;
                } else {
                    mTestStorageResult = false;
                }
                break;

            case MEM_2G_FLASH_16G:
                if (mem == 2 && flash == 16) {
                    mTestStorageResult = true;
                } else {
                    mTestStorageResult = false;
                }
                break;

            case MEM_2G_FLASH_32G:
                if (mem == 2 && flash == 32) {
                    mTestStorageResult = true;
                } else {
                    mTestStorageResult = false;
                }
                break;
        }

        StringBuffer storageInfo = new StringBuffer();
        storageInfo.append(getResources().getText(R.string.storage_type));
        storageInfo.append("\n\t");
        storageInfo.append(mem);
        storageInfo.append("GB - ");
        storageInfo.append(flash);
        storageInfo.append("GB");
        storageInfo.append("\n");
        storageInfo.append(getResources().getText(R.string.total_memory));
        storageInfo.append("\n\t");
        storageInfo.append(strTotalMemory);
        storageInfo.append("\n");
        storageInfo.append(getResources().getText(R.string.flash_memory));
        storageInfo.append("\n\t");
        storageInfo.append(strTotalInternalMemory);

        printLog("storageInfo:" + storageInfo.toString());

        return storageInfo.toString();
    }

    private void stopAudioPlayback() {
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "pause");

        sendBroadcast(intent);
    }

    private void startRecording() {
        RecordTask mRecordTask = new RecordTask();
        mRecordTask.execute();
    }

    private void stopRecording() {
        if (mAudioRecord == null) {
            return;
        }

        mIsRecording = false;

        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioRecord = null;
    }

    private void startPlayback() {
        printLog("startPlayback - mRecordFile: " + mRecordFile);

        stopRecording();

        PlayTask mPlayTask = new PlayTask();
        mPlayTask.execute();
    }

    private void resetAVIN() {
        mHandler.removeMessages(MSG_AUX_NO_INPUT_TEST);

        if (mAvinV != null) {
            mAvinV.setDisplay(null);
            mAvinV.setRearDisplay(null);
            mAvinV.stop();
            mAvinV.release();
            mAvinV = null;
        }

        if (mAvinA != null) {
            mAvinA.stop();
            mAvinA.release();
            mAvinA = null;
        }
    }

    private void setBluetoothState() {
        if (mLocalBluetoothManager == null) {
            mLocalBluetoothManager = LocalBluetoothManager.getInstance(getApplicationContext());
        }

        if (mLocalBluetoothManager != null) {
            mBTStatus = mLocalBluetoothManager.getBluetoothState();
            printLog("enableBluetooth - mBTStatus: " + mBTStatus);
            if (mBTStatus != BluetoothAdapter.STATE_ON) {
                mLocalBluetoothManager.setBluetoothEnabled(true);
            } else {
                mLocalBluetoothManager.setBluetoothEnabled(false);
            }
        }
    }

    private void startupDVD() {
        printLog("startupDVD - start");
        if (mLogiccd == null) {
            mLogiccd = DvdLogicManager.getInstance();
        }

        if (!mLogiccd.init(mHandler)) {
            printLog("startupDVD - init error");
            // mIVDVD.setImageResource(R.drawable.icon_error);
            mHandler.sendEmptyMessage(MSG_DVD_ERROR);
            return;
        }
        printLog("startupDVD - init success");

        mLogiccd.initHomeHandle(mDVDHomeHandler);
        if (!mLogiccd.enableDevice()) {
            printLog("startupDVD - enableDevice error");
            mLogiccd.initHomeHandle(null);
            // mIVDVD.setImageResource(R.drawable.icon_error);
            mHandler.sendEmptyMessage(MSG_DVD_ERROR);
            return;
        }

        mLogiccd.openAudOutput(true);
        mLogiccd.lastMemoryLoad();
        printLog("startupDVD - end");
    }

    private void stopDVD() {
        if (mLogiccd != null) {
            mLogiccd.lastMerofyWrite();
            mLogiccd.openFrontDisplay(false);
            mLogiccd.stop();
            mLogiccd.disableDevice();
            mLogiccd.closeAudOutput(true);
            mLogiccd.initHomeHandle(null);
            mLogiccd.deinit();
        }
    }

    private void removeGpsListener() {
        if (mLocationManager != null) {
            mLocationManager.removeGpsStatusListener(this);
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        printLog("onGpsStatusChanged - event: " + event);

        GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
        Iterable<GpsSatellite> iStatellite = gpsStatus.getSatellites();
        mIterator = iStatellite.iterator();
        if (mIterator == null || mGPSInfoItemList == null) {
            return;
        }
        if (!mGPSInfoItemList.isEmpty()) {
            mGPSInfoItemList.clear();
        }
        while (mIterator.hasNext()) {
            GpsSatellite satellite = mIterator.next();
            float snr = satellite.getSnr();
            int prn = satellite.getPrn();
            float azi = satellite.getAzimuth();
            float ele = satellite.getElevation();
            mGPSInfoItemList.add(new GPSInfoItem(snr, prn, azi, ele));
        }

        Collections.sort(mGPSInfoItemList, new Comparator<GPSInfoItem>() {
            public int compare(GPSInfoItem i1, GPSInfoItem i2) {
                float snr1 = i1.mSnr;
                float snr2 = i2.mSnr;
                if (snr1 < snr2) {
                    return 1;
                } else if (snr1 == snr2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        if (event == GpsStatus.GPS_EVENT_FIRST_FIX || event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            if (mGPSInfoItemList.size() >= 1) {
                GPSInfoItem gpsInfoItem = mGPSInfoItemList.get(0);
                printLog("onGpsStatusChanged - mSnr: " + gpsInfoItem.mSnr);
                if ((int) gpsInfoItem.mSnr >= GPS_MIN_SNR_VALUE) {
                    mHandler.removeMessages(MSG_GPS_TEST);
                    mHandler.sendEmptyMessage(MSG_GPS_SUCCESS);

                    removeGpsListener();
                }
            }
        }
        printLog("onGpsStatusChanged - size: " + mGPSInfoItemList.size());
    }

    private class RecordTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            stopRecording();

            File file = new File(RECORD_FILENAME);
            if (file.exists()) {
                file.delete();
            }

            if (file != null) {
                mRecordFile = file.getAbsolutePath();
                printLog("startRecording - mRecordFile: " + mRecordFile);
            }

            int frequency = 16000;
            int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;
            int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

            mRecordBufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration,
                    audioEncoding);

            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    frequency, channelConfiguration, audioEncoding, mRecordBufferSize);

            mAudioRecord.startRecording();

            short[] buffer = new short[mRecordBufferSize];
            OutputStream os = null;
            DataOutputStream dos = null;
            try {
                os = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(os);
                dos = new DataOutputStream(bos);

                mIsRecording = true;
                while (mIsRecording) {
                    int bufferReadResult = mAudioRecord.read(buffer, 0,
                            mRecordBufferSize);
                    for (int i = 0; i < bufferReadResult; i++) {
                        dos.writeShort(buffer[i]);
                    }
                }
                printLog("RecordTask - record end");
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dos != null) {
                        dos.close();
                    }

                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

    }

    private class PlayTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // Get the file we want to playback.
            File file = new File(RECORD_FILENAME);
            // Get the length of the audio stored in the file (16 bit so 2 bytes
            // per short) and create a short array to store the recorded audio.
            int musicLength = (int) file.length() / 2;
            printLog("PlayTask - musicLength: " + musicLength);
            short[] music = new short[musicLength];

            try {
                // Create a DataInputStream to read the audio data back from the
                // saved file.
                InputStream is = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                DataInputStream dis = new DataInputStream(bis);

                // Read the file into the music array.
                int i = 0;
                while (dis.available() > 0) {
                    music[i] = dis.readShort();
                    i++;
                }

                // Close the input streams.
                dis.close();

                // Create a new AudioTrack object using the same parameters as
                // the AudioRecord object used to create the file.
                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_VOICE_CALL,
                        16000,
                        AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        musicLength,
                        AudioTrack.MODE_STREAM);
                // Start playback
                audioTrack.play();

                // Write the music buffer to the AudioTrack object
                audioTrack.write(music, 0, music.length);

                audioTrack.stop();
                audioTrack.release();

                mHandler.sendEmptyMessageDelayed(MSG_REC_STOP_PLAY, REC_TEST_DELAY_TIME);
            } catch (Exception e) {

            }

            return null;
        }

    }

}
