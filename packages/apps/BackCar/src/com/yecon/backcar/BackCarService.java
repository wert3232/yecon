
package com.yecon.backcar;

import static android.mcu.McuExternalConstant.*;
import static android.constant.YeconConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.backlight.BacklightControl;
import android.cbm.CBManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.mcu.McuBaseInfo;
import android.mcu.McuDeviceStatusInfo;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.autochips.backcar.BackCar;
import com.autochips.bluetooth.hf.BluetoothHfUtility;
import com.autochips.settings.AtcSettings;
import com.yecon.common.SourceManager;
import com.yecon.metazone.YeconMetazone;

@SuppressLint("HandlerLeak")
public class BackCarService extends Service {
    private static final String TAG = "BackCarService";

    public static final String ACTION_BACKCAR_TRACK = "com.yecon.action.DyncTrackData";

    private static final int MSG_BACKCAR_START = 0;
    private static final int MSG_BACKCAR_STOP = 1;
    private static final int MSG_BACKCAR_RUNNING = 2;
    private static final int MSG_BACKCAR_STARTACTIVITY = 3;
    private static final int MSG_BACKCAR_STOPACTIVITY = 4;
    private static final int MSG_SIGNAL_READY = 5;
    private static final int MSG_SIGNAL_LOST = 6;
    private static final int MSG_SET_AUX_VIDEO = 7;
    private static final int MSG_SET_SYS_VIDEO = 8;

    private static final int STARTACTIVITY_TIMEOUT = 300;
    private static final int DISPLAYNOSIGNAL_TIMEOUT = 2000;

    private static final int PAL_WIDTH = 800; // 720;
    private static final int PAL_HEIGHT = 480; // 576;

    public final static String PERSYS_BACKCAR_ENABLE = "persist.sys.backcar_enable";
    public final static String PERSYS_BACKCAR_TRACE = "persist.sys.trace_enable";
    public final static String PERSYS_BACKCAR_RADAR = "persist.sys.radar_enable";
    public final static String PERSYS_BACKCAR_MUTE = "persist.sys.backcar_mute_enable";
    public final static String PERSYS_BACKCAR_MIRROR = "persist.sys.backcar_mirror";

    public final static String PERSYS_BACKCAR_BRIGHT = "persist.sys.backcar_bright";
    public final static String PERSYS_BACKCAR_CONTRAST = "persist.sys.backcar_contrast";
    public final static String PERSYS_BACKCAR_HUE = "persist.sys.backcar_hue";
    public final static String PERSYS_BACKCAR_SATRATION = "persist.sys.backcar_saturation";

    private WindowManager mWM = null;
    private View mBackCarMainView = null;
    private BackCarView mBackCarView = null;
    private TextView mTVNoSignal = null;
    private FrameLayout trace_view = null;
    private boolean mIsBCViewAdded = false;
    // private Thread mBCThread = null;
    private int mScreenW = 800;
    private int mScreenH = 480;

    private PlaySoundWorker mPlaySoundWorker = null;
    private final ThreadPoolExecutor mPlaySoundExecutor = new ThreadPoolExecutor(1, 1, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    private boolean mNeedInit = true;
    public BackCar backcar;
    private static CBManager gCBM = null;
    private byte[] data = new byte[256 * 1024];
    // private boolean mARM2BCstatus = false;//true :running
    private static BackCarService gInst = null;
    private Activity mActivity = null;
    private boolean mIsReversing = false;

    private int mWidth;
    private int mHeight;

    private McuManager mMcuManager;
    private boolean mBackCarStarted = false;
    private boolean mSignalReady = false;
    private TraceView mObjTraceView = null;
    private boolean mbNoSignalShow = false;

    Handler msgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "msg.what: " + msg.what + " mIsBCViewAdded: " + mIsBCViewAdded);
            switch (msg.what) {
                case MSG_BACKCAR_START:
                    mIsReversing = true;

                    removeMessages(MSG_BACKCAR_STARTACTIVITY);
                    sendEmptyMessageDelayed(MSG_BACKCAR_STARTACTIVITY, STARTACTIVITY_TIMEOUT);

                    removeMessages(MSG_SIGNAL_LOST);
                    sendEmptyMessageDelayed(MSG_SIGNAL_LOST, DISPLAYNOSIGNAL_TIMEOUT);

                    // WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                    // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    // // lp.width = mScreenW;
                    // // lp.height = mScreenH;
                    // lp.x = 0;
                    // lp.y = 0;
                    // lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                    // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                    // lp.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    // | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    // View.STATUS_BAR_HIDDEN | View.SYSTEM_UI_FLAG_FULLSCREEN |
                    // View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    //
                    // if (!mIsBCViewAdded) {
                    // mWM.addView(mBackCarMainView, lp);
                    // mIsBCViewAdded = true;
                    // Log.i(TAG, "MSG_BACKCAR_START - mWM.addView");
                    // } else {
                    // Log.i(TAG, "MSG_BACKCAR_START - BCView is already added");
                    // }
                    break;

                case MSG_BACKCAR_STOP:
                    removeMessages(MSG_SET_SYS_VIDEO);
                    msgHandler.sendEmptyMessage(MSG_SET_SYS_VIDEO);

                    mIsReversing = false;
                    mBackCarView.setVisibility(View.INVISIBLE);
                    mTVNoSignal.setVisibility(View.INVISIBLE);
                    trace_view.setVisibility(View.INVISIBLE);
                    if (mIsBCViewAdded) {
                        mWM.removeView(mBackCarMainView);
                        mIsBCViewAdded = false;
                        Log.e(TAG, "MSG_BACKCAR_STOP - mWM.removeView");
                    } else {
                        Log.e(TAG, "MSG_BACKCAR_STOP - BCView is already removed");
                    }
                    removeMessages(MSG_SIGNAL_READY);
                    removeMessages(MSG_SIGNAL_LOST);
                    removeMessages(MSG_BACKCAR_STARTACTIVITY);
                    removeMessages(MSG_BACKCAR_STOPACTIVITY);

                    if (null != mActivity) {
                        mSignalReady = false;
                        mActivity.finish();
                        mActivity = null;
                        Log.e(TAG, "MSG_BACKCAR_STOP - BackCarActivity finish");
                    } else {
                        Log.e(TAG, "MSG_BACKCAR_STOP - gInst is null!");
                    }
                    break;

                case MSG_BACKCAR_RUNNING:
                    mBackCarView.update(msg.arg1);
                    if (msg.arg1 == 1) {
                        if (null != mPlaySoundWorker) {
                            mPlaySoundExecutor.remove(mPlaySoundWorker);
                        }
                        mPlaySoundWorker = new PlaySoundWorker();
                        mPlaySoundWorker.setContext(BackCarService.this);
                        mPlaySoundExecutor.execute(mPlaySoundWorker);
                    }
                    break;

                case MSG_BACKCAR_STARTACTIVITY: {
                    Log.i(TAG, "handleMessage - MSG_BACKCAR_STARTACTIVITY");

                    Intent intentBCActvity = new Intent(getApplicationContext(),
                            BackCarActivity.class);
                    intentBCActvity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentBCActvity.putExtra("IsSignalReady", mSignalReady);
                    startActivity(intentBCActvity);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                    // lp.width = mScreenW;
                    // lp.height = mScreenH;
                    lp.x = 0;
                    lp.y = 0;
                    lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                    lp.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.STATUS_BAR_HIDDEN | View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

                    if (!mIsBCViewAdded) {
                        mWM.addView(mBackCarMainView, lp);
                        mIsBCViewAdded = true;
                        Log.i(TAG, "MSG_BACKCAR_START - mWM.addView");
                    } else {
                        Log.i(TAG, "MSG_BACKCAR_START - BCView is already added");
                    }
                }
                    break;

                case MSG_BACKCAR_STOPACTIVITY:
                    removeMessages(MSG_SIGNAL_READY);
                    if (null != mActivity) {
                        mActivity.finish();
                        mActivity = null;
                        Log.i(TAG, "MSG_BACKCAR_STOPACTIVITY - BackCarActivity finish 2222");
                    } else {
                        Log.i(TAG, "MSG_BACKCAR_STOPACTIVITY - mActivity is null 222222!");
                    }
                    break;

                case MSG_SIGNAL_READY:
                    Log.i(TAG, "MSG_SIGNAL_READY - mWidth: " + mWidth + " - mHeight: " + mHeight);
                    removeMessages(MSG_SIGNAL_LOST);
                    mBackCarView.getHolder().setFixedSize(mWidth, mHeight);
                    mBackCarView.requestLayout();
                    if (mBackCarView.getVisibility() == View.INVISIBLE) {
                        mBackCarView.setVisibility(View.VISIBLE);
                    }
                    mTVNoSignal.setVisibility(View.INVISIBLE);

                    boolean bBackcartrace = SystemProperties
                            .getBoolean(PERSYS_BACKCAR_TRACE, false);
                    trace_view.setVisibility(bBackcartrace ? View.VISIBLE : View.INVISIBLE);
                    break;

                case MSG_SIGNAL_LOST:
                    Log.i(TAG, "MSG_SIGNAL_LOST - MSG_SIGNAL_LOST");
                    removeMessages(MSG_SIGNAL_READY);
                    mBackCarView.setVisibility(View.INVISIBLE);
                    mTVNoSignal.setVisibility(View.VISIBLE);
                    trace_view.setVisibility(View.INVISIBLE);
                    break;

                case MSG_SET_AUX_VIDEO:
                    BackCarUtil.SetAuxVideoPara();
                    break;

                case MSG_SET_SYS_VIDEO:
                    BackCarUtil.SetSysVideoPara();
                    break;

                default:
                    break;
            }
        }
    };

    private Object sourceTocken;
    private McuListener mMcuListener = new McuListener() {

        @Override
        public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {
            if (infoType == MCU_DEVICE_STATUS_INFO_TYPE) {
                if (!SystemProperties.getBoolean(PERSYS_BACKCAR_ENABLE, true))
                    return;

                McuDeviceStatusInfo info = mcuBaseInfo.getDeviceStatusInfo();
                if (info == null) {
                    return;
                }
                int backcarStatus = info.getBackcarStatus();

                if (backcarStatus == MCU_DATA_SWITCH_OFF) {
                    if (mBackCarStarted) {
                        msgHandler.removeMessages(MSG_BACKCAR_STARTACTIVITY);
                        msgHandler.removeMessages(MSG_SIGNAL_LOST);
                        msgHandler.removeMessages(MSG_SET_AUX_VIDEO);

                        mBackCarStarted = false;

                        BackCar.stop();
                        msgHandler.sendEmptyMessage(MSG_BACKCAR_STOP);

                        SystemProperties.set(PROPERTY_KEY_STARTBACKCAR, "false");

                        sendBroadcast(new Intent(ACTION_BACKCAR_STOP));

                        if (sourceTocken != null) {
                            SourceManager.unregisterSourceListener(sourceTocken);
                            sourceTocken = null;
                        }
                    }
                } else if (backcarStatus == MCU_DATA_SWITCH_ON) {
                    if (!mBackCarStarted) {
                        msgHandler.removeMessages(MSG_BACKCAR_STARTACTIVITY);
                        msgHandler.removeMessages(MSG_SIGNAL_LOST);
                        msgHandler.removeMessages(MSG_SET_AUX_VIDEO);

                        mBackCarStarted = true;

                        SystemProperties.set(PROPERTY_KEY_STARTBACKCAR, "true");

                        sourceTocken = SourceManager.registerSourceListener(BackCarService.this,
                                SourceManager.SRC_NO.backcar);
                        SourceManager.setAudioFocusNotify(sourceTocken,
                                new SourceManager.AudioFocusNotify() {

                                    @Override
                                    public void onAudioFocusChange(int arg0) {
                                        // do nothing
                                    }

                                });
                        SourceManager.acquireSource(sourceTocken, AudioManager.STREAM_VOICE_CALL,
                                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

                        // mBackCarView.setVisibility(View.GONE);
                        // if (mTVNoSignal != null) {
                        // mTVNoSignal.setVisibility(View.GONE);
                        // }
                        // trace_view.setVisibility(View.GONE);

                        BackCar.start();
                        msgHandler.sendEmptyMessage(MSG_BACKCAR_START);

                        sendBroadcast(new Intent(ACTION_BACKCAR_START));
                    }
                }
            }
        }

    };

    @Override
    public void onCreate() {
        Log.e(TAG, "Enter backcar_service onCreate");

        backcarInit();

        // for QuickBoot
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PREQB_POWERON);
        filter.addAction(ACTION_QB_POWEROFF);
        filter.addAction(ACTION_QB_PREPOWEROFF);
        filter.addAction(ACTION_BACKCAR_TRACK);
        filter.addAction(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE);
        registerReceiver(mQuickBootListener, filter);

        Log.e(TAG, "Set On signal listener!");
        backcar = new BackCar();
        backcar.setOnSignalListener(mListenerSignal);

        super.onCreate();
    }

    private void backcarInit() {
        // jy 20141127 add for backcar
        mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
        try {
            mMcuManager.RPC_RequestMcuInfoChangedListener(mMcuListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (null == gCBM) {
            Log.i(TAG, "Create cbm object! \r\n");
            gCBM = new CBManager();
            if (null != gCBM) {
                Log.i(TAG, "Create cbm object Success! \r\n");
            }
        }

        Log.i(TAG, "mNeedInit:" + mNeedInit);

        if (mNeedInit) {
            Log.i(TAG, "Start of BackCar.Init()!");

            BackCar.init();
            mNeedInit = false;

            Log.i(TAG, "End of BackCar.Init()!");
        }

        if (null != gCBM) {
            Log.i(TAG, "Inform cbm arm1 is ready!");
            gCBM.systemReady();
            gCBM = null;
        } else {
            Log.i(TAG, "invalid cbm object,can't Inform cbm!");
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Enter backcar_service onDestroy");
        BackCar.Deinit();

        unregisterReceiver(mQuickBootListener);

        try {
            mMcuManager.RPC_RemoveMcuInfoChangedListener(mMcuListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand, intent: " + intent + " flags: " + flags + " startId: "
                + startId);
        gInst = this;
        if (intent == null) {
            Log.i(TAG, "onStartCommand, reset after Terminate Reset Native Resource");
            BackCar.reset();
        }
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = mWM.getDefaultDisplay();
        mScreenW = display.getWidth();
        mScreenH = display.getHeight();
        Log.i(TAG, "getScreenSize - mScreenW: " + mScreenW + ", mScreenH: " + mScreenH);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBackCarMainView = inflater.inflate(R.layout.main, null);
        mBackCarView = (BackCarView) mBackCarMainView.findViewById(R.id.bcview);
        mTVNoSignal = (TextView) mBackCarMainView.findViewById(R.id.tv_no_signal);
        mTVNoSignal.setVisibility(View.INVISIBLE);
        trace_view = (FrameLayout) mBackCarMainView.findViewById(R.id.trace_view_parent);
        trace_view.setVisibility(View.INVISIBLE);
        mObjTraceView = (TraceView) trace_view.findViewById(R.id.trace_view);
        mBackCarView.getHolder().setFormat(ImageFormat.YV12);
        mBackCarView.getHolder().setFixedSize(PAL_WIDTH, PAL_HEIGHT);

        /*
         * mBCThread = new Thread(new Runnable() {
         * @Override public void run() { Log.i(TAG, "BackCar Service Thread entry"); if (mNeedInit)
         * { BackCar.init(); mNeedInit = false; } while (true) { Message msg = BackCar.getEvent();
         * if (null == msg) { break; } if (BackCar.EVENT_BACKCAR_START == msg.what) { Log.i(TAG,
         * "Receive EVENT_BACKCAR_START event"); BackCar.start();
         * msgHandler.sendEmptyMessage(MSG_BACKCAR_START); } else if (BackCar.EVENT_BACKCAR_STOP ==
         * msg.what) { Log.i(TAG, "Receive EVENT_BACKCAR_STOP event"); BackCar.stop();
         * msgHandler.sendEmptyMessage(MSG_BACKCAR_STOP); } else if (BackCar.EVENT_BACKCAR_RUNNING
         * == msg.what) { Log.i(TAG, "Receive EVENT_BACKCAR_RUNNING event"); Message tmpMsg =
         * Message.obtain(); tmpMsg.what = MSG_BACKCAR_RUNNING; tmpMsg.arg1 = msg.arg1;
         * msgHandler.sendMessage(tmpMsg); } else if (BackCar.EVENT_BACKCAR_ERROR == msg.what) {
         * Log.i(TAG, "Receive EVENT_BACKCAR_ERROR event"); msg.recycle(); break; } msg.recycle(); }
         * Log.i(TAG, "BackCar Service Thread exit"); } });//.start(); mBCThread.start();;
         */

        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private BackCar.OnSignalListener mListenerSignal = new BackCar.OnSignalListener() {
        public void onSignal(int msg, int param1, int param2) {
            Log.i(TAG, "onSignal msg:" + msg);
            switch (msg) {
                case BackCar.SIGNAL_READY:
                    Log.i(TAG, "Signal is on!");
                    mWidth = param1;
                    mHeight = param2;
                    msgHandler.removeMessages(MSG_SIGNAL_LOST);
                    msgHandler.sendEmptyMessage(MSG_SIGNAL_READY);

                    msgHandler.removeMessages(MSG_SET_AUX_VIDEO);
                    msgHandler.sendEmptyMessageDelayed(MSG_SET_AUX_VIDEO, STARTACTIVITY_TIMEOUT);
                    break;

                case BackCar.SIGNAL_LOST:
                    Log.i(TAG, "Signal is off!");
                    msgHandler.removeMessages(MSG_SIGNAL_READY);
                    msgHandler.sendEmptyMessageDelayed(MSG_SIGNAL_LOST, DISPLAYNOSIGNAL_TIMEOUT);

                    msgHandler.sendEmptyMessage(MSG_SET_SYS_VIDEO);
                    break;

                default:
                    return;
            }
        }
    };

    static BackCarService getInstance() {
        return (gInst);
    }

    public void setActivity(Activity activity) {
        Log.i(TAG, "setActivity - mIsReversing = " + mIsReversing);
        if ((null != mActivity) && (mActivity != activity)) {
            mActivity.finish();
        }
        mActivity = activity;
        if (!mIsReversing) {
            msgHandler.removeMessages(MSG_BACKCAR_STOPACTIVITY);
            msgHandler.sendEmptyMessage(MSG_BACKCAR_STOPACTIVITY);
        }
    }

    private void sendBlackoutKeyCMD(int backlightLevel) {
        byte[] param = new byte[4];
        param[0] = (byte) backlightLevel;
        param[1] = (byte) 0x00;
        param[2] = (byte) 0x00;
        param[3] = (byte) 0x00;

        try {
            if (mMcuManager != null) {
                mMcuManager.RPC_KeyCommand(T_BLACKOUT, param);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class PlaySoundWorker implements Runnable {
        private Context mContext = null;

        // byte[] data = new byte[256 * 1024];

        public void setContext(Context context) {
            mContext = context;
        }

        @Override
        public void run() {
            Resources resources = BackCarService.this.getResources();
            InputStream is = resources.openRawResource(R.raw.sound_48000);

            try {
                // byte[] data = new byte[256 * 1024];
                is.read(data);
                is.close();
                BackCar.playSound(data);
            } catch (IOException e) {
            }
        }
    }

    BroadcastReceiver mQuickBootListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (null == action) {
                Log.i(TAG, "mQuickBootListener - action is null");
                return;
            }
            Log.i(TAG, "mQuickBootListener - action: " + action);

            if (ACTION_PREQB_POWERON.equals(action)) {
                Log.i(TAG, "mQuickBootListener - ACTION_PREQB_POWERON received");
                Log.i(TAG, "mQuickBootListener resume Backcar Playback begin");

                int DACType = YeconMetazone.GetAudioDACType();
                AtcSettings.Audio.SelectDAC(0, DACType, 2);

                mBackCarStarted = false;

                BackCar.resume();

                backcarInit();

                Log.i(TAG, "mQuickBootListener resume Backcar end");
            } else if (ACTION_QB_POWEROFF.equals(action)) {
                Log.i(TAG, "mQuickBootListener - ACTION_QB_POWEROFF received");
                Log.i(TAG, "mQuickBootListener stop Backcar Playback begin");

                mBackCarStarted = false;

                BackCar.suspend();

                if (mIsBCViewAdded) {
                    mWM.removeView(mBackCarMainView);
                    mIsBCViewAdded = false;
                    Log.i(TAG, "MCU_ACTION_ACC_OFF - mWM.removeView");
                }

                try {
                    mMcuManager.RPC_RemoveMcuInfoChangedListener(mMcuListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                if (null != mActivity) {
                    mActivity.finish();
                    mActivity = null;
                    Log.i(TAG, "mQuickBootListener - BackCarActivity finish");
                } else {
                    Log.i(TAG, "mQuickBootListener - gInst is null!");
                }

                Log.i(TAG, "mQuickBootListener stop Backcar end");
            } else if (ACTION_BACKCAR_TRACK.equals(action)) {

                Bundle bundle = intent.getExtras();
                int iAngle = bundle.getInt("TrackData");

                if (mObjTraceView != null && trace_view != null) {

                    trace_view.setVisibility(View.VISIBLE);
                    mObjTraceView.DrawTrack(iAngle);
                    Log.i(TAG, "mQuickBootListener DrawTrack");
                }
            } else if (BluetoothHfUtility.ACTION_CALL_STATE_CHANGE.equals(action)) {
            	int iPhoneCallState = intent.getIntExtra(
						BluetoothHfUtility.EXTRA_CALL_STATE, 0);
    
            	if (iPhoneCallState == 1) {
					if (mTVNoSignal != null) {
						if (mbNoSignalShow) {
							mTVNoSignal.setVisibility(View.VISIBLE);
						}
					}
				}else {
					if (mTVNoSignal != null) {
						mbNoSignalShow = mTVNoSignal.isShown();
						if (mbNoSignalShow) {
							mTVNoSignal.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
        }

    };
}
