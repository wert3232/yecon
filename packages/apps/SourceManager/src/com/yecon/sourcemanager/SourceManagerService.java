
package com.yecon.sourcemanager;

import static android.constant.YeconConstants.*;
import static android.mcu.McuExternalConstant.*;

import java.util.ArrayList;
import java.util.Iterator;

import com.autochips.storage.EnvironmentATC;
import com.yecon.common.RebootStatus;
import com.yecon.common.SourceManager;
import com.yecon.common.YeconEnv;
import com.yecon.common.RebootStatus.SOURCE;
import com.yecon.mediaprovider.YeconMediaStore;
import com.yecon.metazone.YeconMetazone;
import com.yecon.sourcemanager.SourceScheduler.POWER_STATE;

import android.app.ActivityManager;
import android.app.Service;
import android.backlight.BacklightControl;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.mcu.McuExternalConstant;
import android.mcu.McuManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;

public class SourceManagerService extends Service {
    public final static boolean DEBUG = true;
    private final String TAG = "SourceManagerService";

    private static final String ACTION_BLUETOOTH_CALL_STATUS_CHANGE =
            "com.autochips.bluetooth.PhoneCallActivity.action.BLUETOOTH_CALL_STATUS_CHANGE";

    private static final String ACTION_BLUETOOTH_CALL_STATUS =
            "com.autochips.bluetooth.PhoneCallActivity.action.BLUETOOTH_CALL_STATUS";

    private static final int CALL_STATE_START = 1;
    private static final int CALL_STATE_FINISH = 0;

    private static final int MSG_ID_BKL_ENABLE = 1;
    private static final int MSG_BKL_POWER_DISENABLE = 2;
    private static final int MSG_BKL_BLACKOUT_DISENABLE = 3;
    private static final int MSG_ID_CHECK_BACKCAR_STATUS = 4;
    private static final int MSG_ID_START_APP_SOURCE = 5;
    private static final int MSG_ID_START_FACTORY_APP = 6;

    private static final int BKL_ENABLE_TIMEOUT = 1000;
    private static final int CHECK_BACKCAR_STATUS_TIMEOUT = 200;
    private static final int START_APP_SOURCE_TIMEOUT = 1500;
    private static final int START_FACTORY_APP_TIMEOUT = 1500;

    public static final String[] INTERNAL_PACKAGE = new String[] {
            FMRADIO_PACKAGE_NAME,
            MUSIC_PACKAGE_NAME,
            VIDEO_PACKAGE_NAME,
            BLUETOOTH_PACKAGE_NAME,
            DVD_PACKAGE_NAME,
            AVIN_PACKAGE_NAME,
            AVIN_EXT_PACKAGE_NAME,
            IPOD_PACKAGE_NAME,
            MIRACAST_PACKAGE_NAME,
            CANBUS_PACKAGE_NAME,
            DVR_PACKAGE_NAME
    };
    private String[] storages = new String[] {
			YeconMediaStore.EXTERNAL_PATH,
			YeconMediaStore.EXT_SDCARD1_PATH,
			YeconMediaStore.EXT_SDCARD2_PATH,
			YeconMediaStore.UDISK1_PATH,
			YeconMediaStore.UDISK2_PATH,
			YeconMediaStore.UDISK3_PATH,
			YeconMediaStore.UDISK4_PATH,
			YeconMediaStore.UDISK5_PATH
	};
    private boolean AUTO_BOOT_START = true;

    private SourceScheduler sourceScheduler;

    private boolean bootCompleted = false;

    private boolean mIsAccOff = false;

    private ActivityManager activityMgr;

    private McuManager mMcuManager;

    private AudioManager mAudioManager;
    
    private boolean isPowerOff = false;
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ID_BKL_ENABLE: {
                    boolean bklEnable = BacklightControl.GetBklEnable();
                    if (DEBUG) {
                        Log.i(TAG, "MSG_ID_BKL_ENABLE - bklEnable: " + bklEnable);
                    }
                    if (!bklEnable) {
                        int brightness = BacklightControl.getBrightness();
                        int backlightLevel = BacklightControl.getBacklightLevel(brightness);

                        setBacklight(1, 1, 0, backlightLevel);
                    }
                    break;
                }

                case MSG_BKL_POWER_DISENABLE: {
                    boolean isPowerKey = SystemProperties.getBoolean(PROPERTY_KEY_POWERKEY,
                            false);
                    if (DEBUG) {
                        Log.i(TAG, "MSG_BKL_POWER_DISENABLE - isPowerKey: " + isPowerKey);
                    }
                    if (isPowerKey) {
                        setBacklight(0, 0, 1, 0);
                    }
                    break;
                }

                case MSG_BKL_BLACKOUT_DISENABLE: {
                    boolean isPowerKey = SystemProperties.getBoolean(PROPERTY_KEY_POWERKEY,
                            false);
                    boolean isBlackoutKey = SystemProperties.getBoolean(PROPERTY_KEY_BACKOUTKEY,
                            false);
                    if (DEBUG) {
                        Log.i(TAG, "MSG_BKL_BLACKOUT_DISENABLE - isPowerKey: " + isPowerKey
                                + " - isBlackoutKey: " + isBlackoutKey);
                    }
                    if (isBlackoutKey && !isPowerKey) {
                        setBacklight(0, 1, 0, 0);
                    }
                    break;
                }

                case MSG_ID_CHECK_BACKCAR_STATUS: {
                    int backcarStatus = YeconMetazone.GetBackCarStatus();
                    if (DEBUG) {
                        Log.i(TAG, "mHandler - backcarStatus: " + backcarStatus);
                    }
                    if (backcarStatus == 1) {
                        removeMessages(MSG_ID_CHECK_BACKCAR_STATUS);
                        sendEmptyMessageDelayed(MSG_ID_CHECK_BACKCAR_STATUS,
                                CHECK_BACKCAR_STATUS_TIMEOUT);
                    } else if (backcarStatus == 2) {
                        removeMessages(START_APP_SOURCE_TIMEOUT);
                        sendEmptyMessageDelayed(MSG_ID_START_APP_SOURCE,
                                START_APP_SOURCE_TIMEOUT);
                    }
                    break;
                }

                case MSG_ID_START_APP_SOURCE: {
                    startSourceApp();
                    break;
                }

                case MSG_ID_START_FACTORY_APP: {
                    startFactoryTestApp();
                    break;
                }
            }
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) {
            Log.i(TAG, "onCreate");
        }

        activityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        sourceScheduler = new SourceScheduler(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(SourceManager.ACTION_SOURCE_ACQUIRE);
        filter.addAction(SourceManager.ACTION_SOURCE_RELEASE);
        filter.addAction(SourceManager.ACTION_SOURCE_HOTPLUG);
        this.registerReceiver(sourceReceiver, filter);

        filter = new IntentFilter();
        // filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");
        registerReceiver(mediaDeviceReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_QB_POWERON);
        filter.addAction(ACTION_QB_POWEROFF);
        filter.addAction(MCU_ACTION_ACC_ON);
        filter.addAction(MCU_ACTION_ACC_OFF);
        //make sure handle ACTION_SCREEN_OFF before quickbootmanager kill apps
        filter.setPriority(1000);
        registerReceiver(quickBootListener, new IntentFilter(filter));

        filter = new IntentFilter();
        filter.addAction(SourceManager.ACTION_PHONE_LINK_START);
        filter.addAction(SourceManager.ACTION_PHONE_LINK_STOP);
        filter.addAction(SourceManager.ACTION_PHONE_LINK_RESUME);
        filter.addAction(SourceManager.ACTION_PHONE_LINK_PAUSE);
        filter.addAction(SourceManager.ACTION_AUDIO_FUCOS_CHANGED);
        filter.addAction(ACTION_BLUETOOTH_CALL_STATUS_CHANGE);
        filter.addAction(ACTION_BACKCAR_START);
        filter.addAction(ACTION_BACKCAR_STOP);
        filter.addAction(YeconConstants.ACTION_QB_PREPOWEROFF);
        filter.addAction("action.hzh.media.power.on");
        registerReceiver(otherReceiver, new IntentFilter(filter));

        try {
            ContentResolver cr = getContentResolver();
            ContentValues cv = new ContentValues();
            cv.put("password", "y9y8e1e3c5c6o8o6n4n");
            cv.put("bind", true);
            cr.update(SourceManager.SOURCES_URI, cv, null, null);
        } catch (Exception e) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int backcarStatus = YeconMetazone.GetBackCarStatus();
        if (DEBUG) {
            Log.i(TAG, "onStartCommand - backcarStatus: " + backcarStatus);
        }
        if (backcarStatus == 1) {
            mHandler.sendEmptyMessageDelayed(MSG_ID_CHECK_BACKCAR_STATUS,
                    CHECK_BACKCAR_STATUS_TIMEOUT);
        }

        boolean isFactoryTest = SystemProperties.getBoolean("ro.factory.test.enable", false);

        if (DEBUG) {
            Log.i(TAG, "onStartCommand - isFactoryTest: " + isFactoryTest);
        }

        if (isFactoryTest && backcarStatus == 2) {
            mHandler.removeMessages(MSG_ID_START_FACTORY_APP);
            mHandler.sendEmptyMessageDelayed(MSG_ID_START_FACTORY_APP, START_FACTORY_APP_TIMEOUT);
        }

        // AUTO_BOOT_START = !SystemProperties.getBoolean("persist.sys.firstboot_flag", true);
        if (intent != null && !bootCompleted && intent.getStringExtra("start") != null
                && !isFactoryTest && backcarStatus == 2) {
            startSourceApp();
        }

        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(sourceReceiver);
        this.unregisterReceiver(mediaDeviceReceiver);
        this.unregisterReceiver(quickBootListener);
        this.unregisterReceiver(otherReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class ServiceStub extends ISourceManagerService.Stub {

        // WeakReference<SourceManagerService> mService;
        ServiceStub(SourceManagerService service) {
            // mService = new WeakReference<SourceManagerService>(service);
        }

        @Override
        public int request(int srcNO) throws RemoteException {
            return sourceScheduler.request(srcNO);
        }

        @Override
        public void release(int srcNO) throws RemoteException {
            sourceScheduler.release(srcNO);
        }

        @Override
        public void hotplug(int srcNo, String devPath, boolean insert,
                boolean appExit) throws RemoteException {
            sourceScheduler.hotPlug(srcNo, devPath, insert, appExit);
        }

        @Override
        public void releaseMediaKey(int srcNo) throws RemoteException {
            sourceScheduler.cancelCheckMediaButton(srcNo);
        }

        @Override
        public void updatePlayingPath(int srcNo, String path)
                throws RemoteException {
            sourceScheduler.updatePlayingPath(srcNo, path);
        }

    }

    private final IBinder mBinder = new ServiceStub(this);

    private BroadcastReceiver sourceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "sourceReceiver" + action);
            if (action.equals(SourceManager.ACTION_SOURCE_ACQUIRE)) {
                int srcNo = intent.getIntExtra(SourceManager.EXTRA_SOURCE_NO, -1);
                if (DEBUG) {
                    Log.i(TAG, action + ":" + srcNo);
                }
                if (srcNo >= 0 && srcNo < SourceManager.SRC_NO.max) {
                	isPowerOff = false;
                    sourceScheduler.request(srcNo);
                }
            } else if (action.equals(SourceManager.ACTION_SOURCE_RELEASE)) {
                int srcNo = intent.getIntExtra(SourceManager.EXTRA_SOURCE_NO, -1);
                if (DEBUG) {
                    Log.i(TAG, action + ":" + srcNo);
                }
                if (srcNo >= 0 && srcNo < SourceManager.SRC_NO.max) {
                    sourceScheduler.release(srcNo);
                }
            } else if (action.equals(SourceManager.ACTION_SOURCE_HOTPLUG)) {
                String devPath = intent.getStringExtra(SourceManager.EXTRA_HOTPLUG_PATH);
                int srcNo = intent.getIntExtra(SourceManager.EXTRA_SOURCE_NO, -1);
                boolean insert = intent.getBooleanExtra(SourceManager.EXTRA_HOTPLUG_STATE, false);
                if (DEBUG) {
                    Log.i(TAG, action + ":" + devPath + ":" + insert);
                }
                if (srcNo >= 0 && srcNo < SourceManager.SRC_NO.max) {
                	isPowerOff = false;
                    sourceScheduler.hotPlug(srcNo, devPath, insert, true);
                }
            }
        }

    };

    private BroadcastReceiver mediaDeviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!bootCompleted) {
                return;
            }
            String action = intent.getAction();
            String devPath = intent.getData().getPath();
            if (DEBUG) {
                Log.i(TAG, "mediaDeviceReceiver :" + devPath + " action: " + action);
            }
            //FIXME:插拔媒体卡
            if (action.equals(Intent.ACTION_MEDIA_REMOVED)
                    || action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
                boolean isQBPowerOn = SystemProperties.getBoolean(PROPERTY_QB_POWERON, false);
                if (!isQBPowerOn
                        && (sourceScheduler.getPowerState() == POWER_STATE.accon || sourceScheduler
                                .getPowerState() == POWER_STATE.poweron)) {
                    SourceManager.saveMediaEverRemoved(devPath);
                }
            } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                // } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
                // savePlugedDevices(devPath, true);
            	if(isPowerOff){
            		return;
            	}
                /*FIXME:一汽插入卡不自动跳转
                sourceScheduler.hotPlug(-1, devPath, true, false);*/
                Log.e("SourceManagerService", Intent.ACTION_MEDIA_MOUNTED);
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
               Log.e("SourceManagerService", Intent.ACTION_MEDIA_SCANNER_FINISHED);
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
               Log.e("SourceManagerService", Intent.ACTION_MEDIA_SCANNER_STARTED);
            }

        }

    };

    private BroadcastReceiver quickBootListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DEBUG) {
                Log.i(TAG, "quickBootListener:" + action);
            }
            if (action.equals(ACTION_QB_POWERON)) {
                mIsAccOff = false;
                sourceScheduler.setPower(SourceScheduler.POWER_STATE.poweron);
            } 
            else if(action.equals(Intent.ACTION_SCREEN_OFF)){    
            	//make sure handle ACTION_SCREEN_OFF before quickbootmanager kill apps
				mIsAccOff = false;
                sourceScheduler.setPower(SourceScheduler.POWER_STATE.poweroff);
            }
            //else if (action.equals(ACTION_QB_POWEROFF)) {
            //    mIsAccOff = false;
            //    sourceScheduler.setPower(SourceScheduler.POWER_STATE.poweroff);
            //} 
			else if (action.equals(MCU_ACTION_ACC_ON)) {
                mIsAccOff = false;
                sourceScheduler.setPower(SourceScheduler.POWER_STATE.accon);
            } else if (action.equals(MCU_ACTION_ACC_OFF)) {
                mIsAccOff = true;
                boolean powerOff = intent.getBooleanExtra("power_off", false);
                if (powerOff) {
                    mIsAccOff = false;
                }
                sourceScheduler.setPower(SourceScheduler.POWER_STATE.accoff);
            }
        }
    };

    private BroadcastReceiver otherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DEBUG) {
                Log.i(TAG, "otherReceiver:" + action);
            }
            if (action.equals(SourceManager.ACTION_PHONE_LINK_START)
                    || action.equals(SourceManager.ACTION_PHONE_LINK_RESUME)) {
                sourceScheduler.request(SourceManager.SRC_NO.phonelink);
            } else if (action.equals(SourceManager.ACTION_PHONE_LINK_STOP)) {
                sourceScheduler.release(SourceManager.SRC_NO.phonelink);
            } else if (action.equals(SourceManager.ACTION_AUDIO_FUCOS_CHANGED)) {
                String pkgLossFocus = intent
                        .getStringExtra(SourceManager.EXTRA_AUDIO_FUCOS.LOSS_FUCOS_PKG);
                if (pkgLossFocus != null && pkgLossFocus.length() > 0) {
                    boolean bInternalPkg = false;
                    for (String pkg : INTERNAL_PACKAGE) {
                        if (pkg.equals(pkgLossFocus)) {
                            bInternalPkg = true;
                            break;
                        }
                    }
                    if (!bInternalPkg) {
                        activityMgr.forceStopPackage(pkgLossFocus);
                    }
                }
            } else if (action.equals(ACTION_BLUETOOTH_CALL_STATUS_CHANGE)) {
                if (DEBUG) {
                    Log.i(TAG, "otherReceiver - mIsAccOff: " + mIsAccOff);
                }
                if (mIsAccOff) {
                    return;
                }

                boolean bklEnable = BacklightControl.GetBklEnable();
                int callState = intent.getIntExtra(ACTION_BLUETOOTH_CALL_STATUS, CALL_STATE_FINISH);
                if (DEBUG) {
                    Log.i(TAG, "otherReceiver - bklEnable: " + bklEnable + " - callState: "
                            + callState);
                }
                if (callState == CALL_STATE_START) {
                    setMute(AudioManager.STREAM_BLUETOOTH_SCO, false, 0, SRC_VOLUME_UNMUTE);

                    mHandler.sendEmptyMessageDelayed(MSG_ID_BKL_ENABLE, BKL_ENABLE_TIMEOUT);
                } else if (callState == CALL_STATE_FINISH) {
                    boolean backcarStartup = SystemProperties.getBoolean(
                            PROPERTY_KEY_STARTBACKCAR, false);
                    if (DEBUG) {
                        Log.i(TAG, "otherReceiver - backcarStartup: " + backcarStartup);
                    }
                    if (backcarStartup) {
                        //setMute(AudioManager.STREAM_MUSIC, true, 0, SRC_VOLUME_MUTE);
                        return;
                    }

                    if (bklEnable) {
                        boolean isPowerKey = SystemProperties.getBoolean(PROPERTY_KEY_POWERKEY,
                                false);
                        if (DEBUG) {
                            Log.i(TAG, "otherReceiver - isPowerKey: " + isPowerKey);
                        }
                        if (isPowerKey) {
                            setMute(AudioManager.STREAM_BLUETOOTH_SCO, true, 0, SRC_VOLUME_BT_PHONE);
                        }

                        mHandler.sendEmptyMessage(MSG_BKL_POWER_DISENABLE);

                        mHandler.sendEmptyMessage(MSG_BKL_BLACKOUT_DISENABLE);
                    }
                }
            } else if (action.equals(ACTION_BACKCAR_START)) {
                boolean btPhoneStartup = SystemProperties.getBoolean(PROPERTY_KEY_BTPHONE_STARTUP,
                        false);
                if (DEBUG) {
                    Log.i(TAG, "otherReceiver - btPhoneStartup: " + btPhoneStartup);
                }
                if (!btPhoneStartup) {
                    setMute(AudioManager.STREAM_MUSIC, true, 0, SRC_VOLUME_BACKCAR);
                }

                mHandler.removeMessages(MSG_ID_BKL_ENABLE);
                mHandler.sendEmptyMessageDelayed(MSG_ID_BKL_ENABLE, BKL_ENABLE_TIMEOUT);
            } else if (action.equals(ACTION_BACKCAR_STOP)) {
                boolean isPowerKey = SystemProperties.getBoolean(PROPERTY_KEY_POWERKEY, false);
                boolean muted = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC);
                if (DEBUG) {
                    Log.i(TAG, "otherReceiver - isPowerKey: " + isPowerKey + " - muted: " + muted);
                }
                if (!isPowerKey && !muted) {
                    setMute(AudioManager.STREAM_MUSIC, false, 0, SRC_VOLUME_BACKCAR);
                }

                boolean btPhoneStartup = SystemProperties.getBoolean(PROPERTY_KEY_BTPHONE_STARTUP,
                        false);
                if (DEBUG) {
                    Log.i(TAG, "otherReceiver - btPhoneStartup: " + btPhoneStartup);
                }

                if (!btPhoneStartup) {
                    mHandler.sendEmptyMessage(MSG_BKL_POWER_DISENABLE);

                    mHandler.sendEmptyMessage(MSG_BKL_BLACKOUT_DISENABLE);
                }
            }else if(action.equals("action.hzh.media.power.on")){
            	isPowerOff = false;
            }else if(action.equals(YeconConstants.ACTION_QB_PREPOWEROFF)){
            	isPowerOff = true;
            }
        }
    };

    private void startFactoryTestApp() {
        if (DEBUG) {
            Log.i(TAG, "startFactoryTestApp - start");
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(new ComponentName("com.yecon.yeconfactorytesting",
                "com.yecon.yeconfactorytesting.FactoryTestingMainActivity"));
        startActivity(intent);
    }
    
    private void startSourceApp() {
        if (DEBUG) {
            Log.i(TAG, "startSourceApp - start : " + SystemProperties.getInt(SourceManager.PERSIST_LAST_SOURCE, -1));
        }
        //yfzhang
        bootCompleted = true;
        if (SystemProperties.getInt("persist.sys.power.state", -1) == 0  || !RebootStatus.isReboot(SOURCE.SOURCE_MEMORY)) {
        	int srcNo = SystemProperties.getInt(SourceManager.PERSIST_LAST_SOURCE, -1);
            Log.i(TAG, "start first source:" + srcNo);
            if (AUTO_BOOT_START) {
                if (srcNo >= 0 && srcNo < SourceManager.SRC_NO.max) {
                    if (srcNo == SourceManager.SRC_NO.dvd) {
                        // start dvd service.
                        Intent startIntent = new Intent("android.intent.action.DvdBootService");
                        startService(startIntent);

                        sourceScheduler.startSource(srcNo, "", 1000);
                    } else if (srcNo == SourceManager.SRC_NO.bluetooth){
                        sourceScheduler.startSource(srcNo, "", 4000);
                    } else {
                    	if(srcNo == SourceManager.SRC_NO.music || 
                			srcNo == SourceManager.SRC_NO.video ||
                			srcNo == SourceManager.SRC_NO.photo){
                    		 Log.i(TAG, "getOneExistStorage() :" + getOneExistStorage() );
                    		if(getOneExistStorage() == null){
                    			return;
                    		}
                    	}
                        sourceScheduler.startSource(srcNo, "", 200);
                    }
                }
            }
        }else if(SystemProperties.getInt("persist.sys.power.state", -1) == 1){
        	SystemProperties.set(SourceManager.PERSIST_LAST_SOURCE, "" + -1);
        }
    }
   
    public String getOneExistStorage(){
		EnvironmentATC env = new EnvironmentATC(this);
		for (String storage : storages) {
			boolean checkResult = YeconEnv.checkStorageExist(env, storage);
			if(checkResult){
				if(storage.matches("(.*)ext_sdcard(.*)")){
					return storage;
				}
				if(storage.matches("(.*)udisk(.*)")){
					return storage;
				}
			}
		}
		return YeconMediaStore.EXTERNAL_PATH;
	}
    
    
    private void setBacklight(int enable, int touch_wakeup_en, int power_key_pressed,
            int backlightLevel) {
        BacklightControl.SetBklEnable(enable, touch_wakeup_en, power_key_pressed);

        sendBlackoutKeyCMD(backlightLevel);
    }

    private void setMute(int streamType, boolean state, int flags, int mark) {
        mAudioManager.setYeconVolumeMute(streamType, state, flags, mark);
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

    private ArrayList<String> plugedDevices = new ArrayList<String>();

    private void savePlugedDevices(String devPath, boolean add) {

        boolean got = false;
        if (devPath == null || devPath.length() == 0)
            return;
        synchronized (plugedDevices) {
            Iterator<String> it = plugedDevices.iterator();
            String path;
            while (it.hasNext()) {
                path = it.next();
                if (path.equals(devPath)) {
                    got = true;
                    if (!add) {
                        it.remove();
                    }
                    break;
                }
            }
            if (!got && add) {
                plugedDevices.add(devPath);
            }
        }

    }
}
