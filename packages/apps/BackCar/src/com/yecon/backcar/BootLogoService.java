
package com.yecon.backcar;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;

import static android.mcu.McuExternalConstant.*;

public class BootLogoService extends Service {
    private static final String TAG = "BootLogoService";

    private static final int MSG_BOOTLOGO_START = 0;
    private static final int MSG_BOOTLOGO_STOP = 1;
    private static final int MSG_BOOTLOGO_STARTACTIVITY = 2;
    private static final int MSG_BOOTLOGO_STOPACTIVITY = 3;
    private static final int MSG_BOOTLOGO_FORCE_STOP = 4;

    private static final int BOOTLOGO_STARTACTIVITY_DELAY_TIME = 1000;
    private static final int BOOTLOGO_STOPACTIVITY_DELAY_TIME = 1000;
    private static final int BOOTLOGO_STOP_DELAY_TIME = 2000;
    private static final int BOOTLOGO_FORCE_STOP_DELAY_TIME = 5000;

    private static final String ACTION_PREQB_POWERON = "autochips.intent.action.PREQB_POWERON";

    private static final String BOOT_LOGO_PATH = "/mnt/sdcard/logo.bmp";

    private static BootLogoService gInst;

    private WindowManager mWM = null;
    private View mBootLogoView = null;
    private ImageView mIVBootLogo;

    private Activity mActivity = null;

    private boolean mBootLogoStop = false;

    private BroadcastReceiver mBootLogoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean bootLogoEnable = SystemProperties.getBoolean(PROPERTY_KEY_BOOTLOGO_ENABLE,
                    false);
            Log.e(TAG, "BootLogoService - action: " + action + " - bootLogoEnable: "
                    + bootLogoEnable);
            if (!bootLogoEnable) {
                return;
            }

            if (action.equals(ACTION_PREQB_POWERON)) {
                mHandler.sendEmptyMessage(MSG_BOOTLOGO_START);

                mHandler.removeMessages(MSG_BOOTLOGO_FORCE_STOP);
                mHandler.sendEmptyMessageDelayed(MSG_BOOTLOGO_FORCE_STOP,
                        BOOTLOGO_FORCE_STOP_DELAY_TIME);
            } else if (action.equals(YECON_ACTION_BOOTANIM_EXIT)) {
                mHandler.removeMessages(MSG_BOOTLOGO_FORCE_STOP);
                mHandler.sendEmptyMessageDelayed(MSG_BOOTLOGO_STOP, BOOTLOGO_STOP_DELAY_TIME);
            }
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "BootLogoService - what: " + msg.what);
            switch (msg.what) {
                case MSG_BOOTLOGO_START: {
                    mBootLogoStop = false;

                    SystemProperties.set(PROPERTY_KEY_BOOTLOGO_STARTUP, "true");

                    removeMessages(MSG_BOOTLOGO_STARTACTIVITY);
                    sendEmptyMessageDelayed(MSG_BOOTLOGO_STARTACTIVITY,
                            BOOTLOGO_STARTACTIVITY_DELAY_TIME);

                    File file = new File(BOOT_LOGO_PATH);
                    if (file.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(BOOT_LOGO_PATH, null);
                        mIVBootLogo.setImageBitmap(bm);
                    } else {
                        mIVBootLogo.setImageResource(R.drawable.boot_logo);
                    }

                    WindowManager.LayoutParams lp = new
                            WindowManager.LayoutParams(
                                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    // lp.width = mScreenW;
                    // lp.height = mScreenH;
                    lp.x = 0;
                    lp.y = 0;
                    lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                    lp.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    mWM.addView(mBootLogoView, lp);
                    break;
                }

                case MSG_BOOTLOGO_STOP:
                    mBootLogoStop = true;

                    SystemProperties.set(PROPERTY_KEY_BOOTLOGO_STARTUP, "false");

                    mWM.removeView(mBootLogoView);

                    removeMessages(MSG_BOOTLOGO_STARTACTIVITY);
                    sendEmptyMessage(MSG_BOOTLOGO_STOPACTIVITY);
                    break;

                case MSG_BOOTLOGO_STARTACTIVITY: {
                    if (mBootLogoStop) {
                        return;
                    }

                    Intent intent = new Intent(getApplicationContext(),
                            BootLogoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                }

                case MSG_BOOTLOGO_STOPACTIVITY:
                    if (mActivity != null) {
                        mActivity.finish();
                        mActivity = null;
                    } else {
                        sendEmptyMessageDelayed(MSG_BOOTLOGO_STOPACTIVITY,
                                BOOTLOGO_STOPACTIVITY_DELAY_TIME);
                    }
                    break;

                case MSG_BOOTLOGO_FORCE_STOP:
                    mHandler.sendEmptyMessage(MSG_BOOTLOGO_STOP);
                    break;
            }
        }

    };

    public static BootLogoService getInstance() {
        return gInst;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PREQB_POWERON);
        filter.addAction(YECON_ACTION_BOOTANIM_EXIT);
        registerReceiver(mBootLogoReceiver, filter);

        Log.e(TAG, "BootLogoService - onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "BootLogoService - onStartCommand");

        SystemProperties.set(PROPERTY_KEY_BOOTLOGO_STARTUP, "false");

        gInst = this;

        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBootLogoView = inflater.inflate(R.layout.boot_logo, null);
        mIVBootLogo = (ImageView) mBootLogoView.findViewById(R.id.iv_boot_logo);

        flags = START_STICKY;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "BootLogoService - onDestroy");

        unregisterReceiver(mBootLogoReceiver);
    }

    public void setActivity(Activity activity) {
        if ((null != mActivity) && (mActivity != activity)) {
            mActivity.finish();
        }
        mActivity = activity;
    }

}
