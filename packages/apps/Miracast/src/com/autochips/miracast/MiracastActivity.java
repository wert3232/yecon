package com.autochips.miracast;

import static android.mcu.McuExternalConstant.MCU_ACTION_ACC_OFF;
import static android.mcu.McuExternalConstant.MCU_ACTION_ACC_ON;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplayStatus;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.app.AlertDialog;
import android.view.WindowManager;
import android.view.Window;
import android.view.KeyEvent;
import android.database.ContentObserver;
import android.provider.Settings;
import android.net.Uri;
import android.widget.FrameLayout;
import android.content.pm.ActivityInfo;
import android.view.MotionEvent;
import android.text.TextUtils;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import com.autochips.metazone.AtcMetazone;
import com.autochips.miracast.SppManager.CMDNO;
import com.autochips.cbmctx.CBMCtx;
import com.yecon.common.SourceManager;
import android.constant.YeconConstants;
import com.yecon.settings.YeconSettings;

public class MiracastActivity extends Activity implements SurfaceHolder.Callback, OnClickListener, OnTouchListener{
    /** Called when the activity is first created. */

    public static final boolean DEBUG = true;
    static final String TAG = "MiracastActivity";
    private static String mDeviceName = null;
    private int mPreWfdState = WifiDisplayStatus.DISPLAY_STATE_NOT_CONNECTED;
    private SinkState mSinkState;
    private final static int WFD_WAIT_CONNECT_DIALOG = 1;
    private final static int WFD_BUILD_CONNECT_DIALOG = 2;
    private final static int P2P_DISCONNECT_DIALOG = 3;
    private final static int DISPLAY_DIRECT_NOT_READY_DIALOG = 4;
    private final static int PACKET_LOST_DIALOG = 5;
    private final static int WFD_CONNECTING_DIALOG = 6;
    private final static int NO_WIFI_DIALOG = 7;
    private final static int WIFI_ENABLING_DIALOG = 8;
    private final static int WAIT_WFD_FEATURE_ON_DIALOG = 9;
    private final static int TEST_DIALOG = 10;
    private final static int WIFI_DISCONNECT_DIALOG = 11;
    private final static int WIFI_ENABLED = 12;
    private final static int WAIT_FINISHING = 13;
    private String mSinkDeviceName;
    private final int HIDE_CTRLBAR_TIMEOUT = 2000;

    private int mOrientationBak;
    private boolean isCalibrateShowed = false;
    private boolean isTestDialog = false;

    private SurfaceView mSurfaceView = null;
    //private View mWifiDispalyDisableView = null;
    private DisplayManager mDisplayManager = null;
    //private WifiDisplayStatus mWifiDisplayStatus = null;
    private WifiP2pDevice mP2pDevice;
    private static boolean DisplayConnectedFlag = false;
    private static boolean isAppBackgroundFlag = false;
    private FrameLayout mSinkViewLayout = null;
    //private Button mBtShowHide = null;
    private View layoutCtrl, layoutTools;
    //private Button mBtCalibrate = null;
    private boolean mIsCalibrate = false;
    private int mPointIndex = 0; // mhlhid calibrate
    private final static int CALIBRATION_POINT_NUM = 4;

    static private CBMCtx gCbmCtx = null;

    private WifiManager mWifiManger = null;
    private String dialogContent = null;
    private String toastContent = null;
    private String wifiTypeStr = null;

    private DialogHub mDialogHub = null;
    // TODO : After enabled wifi, the p2p disconnect broadcast would be send,
    // which should be ignore. Check later?
    private boolean bIgnoreNextP2pDisconnect = false;
    private boolean bIgnoreApConnect = true;
    private boolean bFinishFromCancel = false;
    private boolean bIgnoreDisplayBroadcast = true;
    private boolean bIsWaitDialogShowed = false;
    private OnceNotify mOnceNotify = null;
    private WifiNotify mWifiNotify = null;
    private DisplayNotify mDisplayNotify = null;
    private MiracastApplication mApp = null;
    
    private View vLinkLayout;
    private TextView tvStep1, tvStep2;
    private ImageView ivStep1, ivStep2;
    //private Button btnBack;
    private AnimationDrawable anim;

    public long WifiMetaIndex = 0x10028;
    public long[] WifiType = { 0 };
    public WifiChipType wifiTypeResult = WifiChipType.WIFI5931;

	private static boolean isCanClickCaliButton = true;
    public enum WifiChipType {
        WIFI5931, WIFI7601, WIFI6630, ERROR
    }

    enum SinkState {
        SINK_STATE_IDLE,
        SINK_STATE_WAITING_P2P_CONNECTION,
        SINK_STATE_WIFI_P2P_CONNECTED,
        SINK_STATE_WAITING_RTSP,
        SINK_STATE_RTSP_CONNECTED,
    }

    private final ContentObserver mMiracastObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            updateDeviceName();
        }
    };
	private ImageView ivGuide;
	private Animation mAnimAlpha;
	private Object sourceTocken;
	private SppManager sppManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "[lingling]onCreate");
        sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.phonelink);
        SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify() {
			
			@Override
			public void onAudioFocusChange(int arg0) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onAudioFocusChange:" +arg0 );
				switch (arg0) {
                case AudioManager.AUDIOFOCUS_LOSS:
                	
                	break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: 
                    break;
                case AudioManager.AUDIOFOCUS_GAIN: 
                    break;
				}
				
			}
		});

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.connect_miracast);

        if (isRDSActive()) {
            Log.d(TAG, "[lingling]onCreate RDS playing, finish");
            String RDSToastContent = "RDS playing, Miracast exit!";
            Toast RDSToast = Toast.makeText(getApplicationContext(),
                    RDSToastContent, Toast.LENGTH_LONG);
            RDSToast.setGravity(Gravity.CENTER, 0, 0);
            RDSToast.show();
            finish();
            return;
        }
        
        this.findViewById(R.id.btn_back).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finishLater();
			}
        	
        });
        ivGuide = (ImageView) this.findViewById(R.id.iv_guide);
        vLinkLayout = this.findViewById(R.id.layout_guide_frame);
        tvStep1 = (TextView) this.findViewById(R.id.tv_step1);
        tvStep2 = (TextView) this.findViewById(R.id.tv_step2);
        ivStep1 = (ImageView) this.findViewById(R.id.iv_step1);
        ivStep2 = (ImageView) this.findViewById(R.id.iv_step2);       
        
        mApp = (MiracastApplication)getApplication();
        //mApp.setLastWifiState();
        mDialogHub = new DialogHub(this);
        mOnceNotify = new OnceNotify();
        mWifiNotify = new WifiNotify();
        mDisplayNotify = new DisplayNotify();
        
        //mWifiDispalyDisableView = findViewById(R.id.layout_no_wifi_display_disabled);
        mSinkViewLayout = (FrameLayout) findViewById(R.id.layout_frame);
        mSinkViewLayout.setOnTouchListener(this);
        //mWifiDispalyDisableView.setVisibility(View.INVISIBLE);

        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        //mWifiDisplayStatus = mDisplayManager.getWifiDisplayStatus();

        mWifiManger = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        layoutCtrl = findViewById(R.id.layout_ctrl);
        layoutCtrl.setVisibility(View.INVISIBLE);
        layoutTools = findViewById(R.id.layout_tools);
        layoutTools.setVisibility(View.INVISIBLE);
        findViewById(R.id.bt_tools).setOnClickListener(this);
        findViewById(R.id.bt_phone_home).setOnClickListener(this);
        findViewById(R.id.bt_phone_back).setOnClickListener(this);
        
        //mBtShowHide = (Button) findViewById(R.id.SHOW_HIDE_UI);
        //mBtCalibrate = (Button) findViewById(R.id.bt_calibrate);
        //mBtShowHide.setOnClickListener(this);
        findViewById(R.id.bt_calibrate).setOnClickListener(this);
        findViewById(R.id.bt_home).setOnClickListener(this);
        findViewById(R.id.bt_back).setOnClickListener(this);
        mAnimAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);

        wifiTypeResult = GetWifiType();
        Log.d(TAG, "[lingling] wifi chip type:" + wifiTypeResult);

        if (wifiTypeResult == WifiChipType.WIFI5931) {
            wifiTypeStr = "5931";
        } else if (wifiTypeResult == WifiChipType.WIFI7601) {
            wifiTypeStr = "7601";
        } else if (wifiTypeResult == WifiChipType.WIFI6630) {
            wifiTypeStr = "6630";
        }
        dialogContent = getResources().getString(R.string.notice_content);
        toastContent = getResources().getString(R.string.ap_warning_content);
        dialogContent = String.format(dialogContent, wifiTypeStr);
        toastContent = String.format(toastContent, wifiTypeStr);

        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(
                Settings.Global.WIFI_DISPLAY_ON), false, mMiracastObserver);

        // Init
        mDisplayManager.init();
        mPreWfdState = WifiDisplayStatus.DISPLAY_STATE_NOT_CONNECTED;
        bIgnoreNextP2pDisconnect = true;
        bIgnoreApConnect = false;
        bFinishFromCancel = false;
        bIgnoreDisplayBroadcast = true;
        bIsWaitDialogShowed = false;

        mApp.setOnceStatusCallback(mOnceNotify);
        mApp.setDisplayStatusCallback(mDisplayNotify);
        mApp.setWifiStatusCallback(mWifiNotify);
        
        //startSurface();
        IntentFilter filter = new IntentFilter();
        filter.addAction(YeconConstants.ACTION_QB_POWERON);
        filter.addAction(YeconConstants.ACTION_QB_POWEROFF);
        filter.addAction(YeconConstants.ACTION_QB_PREPOWEROFF);
        filter.addAction(MCU_ACTION_ACC_ON);
        filter.addAction(MCU_ACTION_ACC_OFF);
        registerReceiver(quickBootListener, new IntentFilter(filter));
        
        sppManager = new SppManager(this, sppHandler);
        sppManager.init();
    }
    
    private Handler sppHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
    	
    };
    
    private void showCtrlbar(long delayMillis){
    	//if(WifiDisplayStatus.DISPLAY_STATE_CONNECTED == mPreWfdState){
    	if(vLinkLayout.getVisibility() != View.VISIBLE){
    		layoutCtrl.setVisibility(View.VISIBLE);
    	}
    	mAH.removeMessages(MSG_HIDE_CTRLBAR);
    	mAH.sendEmptyMessageDelayed(MSG_HIDE_CTRLBAR, delayMillis);
    }
    private void hideCtrlbar(){
    	mAH.removeMessages(MSG_HIDE_CTRLBAR);
    	mAH.sendEmptyMessageDelayed(MSG_HIDE_CTRLBAR, 0);
    }
    
    private void notify2closeA2dp(){
    	Intent it = new Intent("com.yecon.bt.customcmd");
        it.putExtra("cmd", "a2dp_close");
        this.sendBroadcast(it);
    }
    private void notify2resumeA2dp(){
    	 Intent it = new Intent("com.yecon.bt.customcmd");
         it.putExtra("cmd", "a2dp_open");
         this.sendBroadcast(it);
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "[lingling]onResume");
        SourceManager.acquireSource(sourceTocken);
        Intent intent = new Intent(SourceManager.ACTION_PHONE_LINK_RESUME);
        intent.putExtra("flag", "miracast");
        sendBroadcast(intent);
        
        if(layoutTools.getVisibility()!=View.INVISIBLE){
    		layoutTools.setVisibility(View.INVISIBLE);
    	}
                
        isAppBackgroundFlag = false;
        mSinkViewLayout.setFocusableInTouchMode(true);
        mSinkViewLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        mApp.setOnceStatusCallback(mOnceNotify);
        mApp.setDisplayStatusCallback(mDisplayNotify);
        mApp.setWifiStatusCallback(mWifiNotify);
        
        mPreWfdState = mDisplayManager.getWifiDisplayStatus().getActiveDisplayState();
        if (WifiDisplayStatus.DISPLAY_STATE_CONNECTED == mPreWfdState) {
            //doInit();
        	YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);
            mSinkViewLayout.setVisibility(View.VISIBLE);
        }         
        else if (mApp.isWifiEnabled()) {
            Log.d(TAG, "[lingling] Wifi on");           
            //mWifiDispalyDisableView.setVisibility(View.GONE);
            resetWifi(0);
        }         
        else {
        	if(mApp.isWifiEnabling()){
				Log.d(TAG,"[lingling] Wifi Enabling");
				updateLinkStatus(WIFI_ENABLING_DIALOG);
			}else{
            	Log.d(TAG, "[lingling] Wifi off");
            	updateLinkStatus(NO_WIFI_DIALOG);
            	mApp.setWifiEnabled(true,true);
			}
        }
        if(isSuspended){
        	isSuspended = false;
	        if(WifiDisplayStatus.DISPLAY_STATE_CONNECTED == mDisplayManager.getWifiDisplayStatus().getActiveDisplayState()){
	        	prepareDialog(DISPLAY_DIRECT_NOT_READY_DIALOG);
	        }
        }
    }

    private void doInit() {
        startSurface();

        mOrientationBak = getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideStatusbar();

        updateDeviceName();
    }

    private void prepareInit() {
        if (!mAH.hasMessages(MSG_WAIT_WFD_FEATURE_ON))
		bIsWaitDialogShowed = true;
        updateLinkStatus(WAIT_WFD_FEATURE_ON_DIALOG);
		mAH.sendEmptyMessage(MSG_WAIT_WFD_FEATURE_ON);
    }

    private void resetWifi(long timeDelay) {
        Log.d(TAG, "[lingling]resetWifi");
        mAH.removeMessages(MSG_DISABLE_WIFI);
        mAH.sendEmptyMessageDelayed(MSG_DISABLE_WIFI, timeDelay);
        mAH.sendEmptyMessageDelayed(MSG_CHECK_WIFI_RESET_TIMEOUT, CHECK_WIFI_DISABLE_TIMEOUT);
    }

    private void showNoticeDialog() {
        Log.d(TAG, "[lingling]showNoticeDialog");

        new AlertDialog.Builder(this)
                .setTitle(R.string.notice_title)
                .setCancelable(false)
                .setMessage(dialogContent)
                .setPositiveButton(R.string.cancel, new cancelOnClickListener())
                .setNegativeButton(R.string.ok, new okOnClickListener())
                .create().show();
    }

    class okOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "[lingling]click ok for notice dialog");

            bIgnoreApConnect = false;

            boolean result = mApp.disconnectWifiAp();
            Log.d(TAG, "[lingling]disconnect result:" + result);

            prepareInit();
        }
    }

    class cancelOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "[lingling]click cancel for notice dialog");

            bFinishFromCancel = true;
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	showCtrlbar(HIDE_CTRLBAR_TIMEOUT);  	
        if (!isRDSActive()) {
            if (!mIsCalibrate) {
                if (mDisplayManager != null) {
                    if (!mDisplayManager.getBTHIDConnectState()) {
                        if (MotionEvent.ACTION_DOWN == event.getAction()) {
                            Log.d(TAG, "Connect BT Hid First  ");
                            //ShowToastText(getResources().getString( R.string.connect_HID));
                        }
                    } else if (!mDisplayManager.getPhoneResolutionValid()) {
                        Log.d(TAG, "Phone Resoulution Unkown");
                        //ShowToastText(getResources().getString( R.string.calibrate_Tip));
                    } else {
                        Log.d(TAG, "mHDMIV.HandleMouseEvent");
                        if(DisplayConnectedFlag){
                        	mDisplayManager.HandleMouseEvent(event.getAction(), (int) event.getX(), (int) event.getY());
						}else{
							Log.d(TAG, "mHDMIV.HandleMouseEvent, Miracast not_connected.");
						}
                    }
                }
            }

            if (MotionEvent.ACTION_UP == event.getAction()) {
                if (mIsCalibrate) {
                    if (CALIBRATION_POINT_NUM > mPointIndex) {
                        mPointIndex = mPointIndex + 1;
                        Log.d(TAG, "HDMIV.setCarkitPoint");
                        if (mDisplayManager != null) {
                            mDisplayManager.setCarkitPoint((int) event.getX(), (int) event.getY(), mPointIndex);
                        }
                        if (CALIBRATION_POINT_NUM == mPointIndex) {
                            Log.d(TAG, "Get Four Calibrate Carkit");
                            if (mDisplayManager != null) {
                                if (mDisplayManager.getCalibrateResolutionValid()) {
                                    Log.d(TAG, "calibrate success");
                                    showToastText(getResources().getString( R.string.success_calibrate));
                                    mPointIndex = 0;
                                    mIsCalibrate = false;
                                } else {
                                    Log.d(TAG, "calibrate failed");
                                    showToastText(getResources().getString( R.string.fail_calibrate));
                                    if (mDisplayManager.getBTHIDConnectState()) {
                                        mIsCalibrate = true;
                                        mPointIndex = 0;
                                        Log.d(TAG, "Calibrate Setting OK");
                                        mDisplayManager.setCalibrate();
                                    } else {
                                        mIsCalibrate = false;
                                        Log.d(TAG, "Connect BT Hid First");
                                        showToastText(getResources().getString( R.string.connect_HID));
                                        mPointIndex = 0;
                                        mIsCalibrate = false;
                                    }

                                }
                            }

                        }
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "[lingling]onBackPressed");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "[lingling]onPause");
        isAppBackgroundFlag = true;
        mDialogHub.dismissDialogDetail();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //stopSurface();
        //setRequestedOrientation(mOrientationBak);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "[lingling]onStop");
    }

    private void clearAllMessages() {
        Log.i(TAG, "[lingling]Clear All Messages");
        mAH.removeMessages(MSG_CONNECT_TIME_OUT);
        mAH.removeMessages(MSG_DISABLE_WIFI);
        mAH.removeMessages(MSG_DISMISS_PACKET_LOST);
        mAH.removeMessages(MSG_RECONNECT);
        mAH.removeMessages(MSG_WAIT_WFD_FEATURE_ON);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "[lingling]onDestroy");
        mAH.removeCallbacksAndMessages(null);
        sppManager.release();
        
        SourceManager.unregisterSourceListener(sourceTocken);
        unregisterReceiver(quickBootListener);
        notify2resumeA2dp();
        
        stopSurface();

        Log.i(TAG, "DisplayConnectedFlag = " + DisplayConnectedFlag + ", bFinishFromCancel = " + bFinishFromCancel);
        if (DisplayConnectedFlag) {
            mDisplayManager.requestSinkDestroy();
            disconnectWfdSinkConnection();
        }

        DisplayConnectedFlag = false;

        clearAllMessages();
        Log.i(TAG, "App ever operate wifi? " + mApp.everOperateWifi());
        if (mApp.everOperateWifi()) {
            Log.d(TAG, "[lingling]need reenable wifi");
            mApp.setWifiEnabled(false);
            mApp.setWifiEnabled(true);
        }
        mApp.feedWifiStatusDog();
        bFinishFromCancel = false;
        mDisplayManager.release();
        getContentResolver().unregisterContentObserver(mMiracastObserver);
        mApp.setOnceStatusCallback(null);
        mApp.setWifiStatusCallback(null);
        mApp.setDisplayStatusCallback(null);
        Log.d(TAG, "[lingling]onDestroy end!");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        Log.d(TAG, "[lingling]surface changed: " + width + "x" + height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "[lingling]surfaceCreated DisplayConnectedFlag: " + DisplayConnectedFlag);
        if (!DisplayConnectedFlag) {
            Log.d(TAG, "[lingling]surfaceCreated not showing");
            setupWfdSinkConnection();
        } else {
            Log.d(TAG, "[lingling]surfaceCreated showing");
            resumeWfdConnection();
        }
    }

    private boolean isSuspended = false;
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "[lingling]surfaceDestroyed");

        WifiDisplayStatus wfdStatus = mDisplayManager.getWifiDisplayStatus();

        if (wfdStatus.getActiveDisplayState() == WifiDisplayStatus.DISPLAY_STATE_CONNECTED) {
            Log.d(TAG, "[lingling]surfaceDestroyed connect display, suspend: " + getSinkSurface());
            mDisplayManager.requestSuspendDisplay(true, null);
            // TODO : this would in main thread
            int MediaReleaseTimeoutCount = 0;
            while (!mDisplayManager.getIsMediaRelease()) {
                if (5 == MediaReleaseTimeoutCount) {
                    Log.d(TAG, "[lingling]surfaceDestroyed MediaReleaseTimeout, surfaceDestroyed");
                    break;
                } else {
                    MediaReleaseTimeoutCount++;
                    Log.d(TAG, "[lingling]surfaceDestroyed MediaReleaseTimeoutCount:" + MediaReleaseTimeoutCount);
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    Log.e(TAG, "[lingling]surfaceDestroyed thread sleep exception = " + e);
                }

            }
            
            isSuspended = true;
            Log.d(TAG, "[lingling]surfaceDestroyed connect display, suspend end");

        }else{
            Log.d(TAG, "[lingling]surfaceDestroyed connect not display");

            mDisplayManager.sendP2pStopListen();
            // disconnectWfdSinkConnection();
            setWfdMode(false);
        }
    }

    private void finishLater(){
    	prepareDialog(WAIT_FINISHING);
    	mAH.removeMessages(MSG_FINISH);
    	mAH.sendEmptyMessageDelayed(MSG_FINISH,  2000);
    }
	
    @Override
    public void onClick(View v) {
    	showCtrlbar(HIDE_CTRLBAR_TIMEOUT);
        switch (v.getId()) {
        /*
        case R.id.SHOW_HIDE_UI:
            Log.d(TAG, "[linling]onClick show_hide_button");

            triggerShownStatusbarAndCurtainView();
            mAH.sendEmptyMessageDelayed(MSG_HIDE_STATEBAR,
                    HIDE_STATEBAR_DELAYED);
            if (!isCalibrateShowed) {
                isCalibrateShowed = true;
                mBtShowHide.setText(getResources().getString(R.string.hide_ui));
                mBtCalibrate.setVisibility(View.VISIBLE);

            } else {
                isCalibrateShowed = false;
                mBtShowHide.setText(getResources().getString(R.string.show_ui));
                mBtCalibrate.setVisibility(View.INVISIBLE);
            }
            break;
        */
        case R.id.bt_tools:
        	if(layoutTools.getVisibility()!=View.VISIBLE){
        		layoutTools.setVisibility(View.VISIBLE);
        	}
        	else{
        		layoutTools.setVisibility(View.INVISIBLE);
        	}
        	break;
        case R.id.bt_phone_home:
        	sppManager.sendCmd(CMDNO.CMD_HOME);
        	break;
        case R.id.bt_phone_back:
        	sppManager.sendCmd(CMDNO.CMD_BACK);
        	break;
        case R.id.bt_home:
        	Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_HOME);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);        	
        	break;
        case R.id.bt_back:
        	finishLater();
        	break;
        case R.id.bt_calibrate:
            Log.d(TAG, "[linling]onClick calibrate button");
            if (!isRDSActive()) {
                Log.d(TAG, "[linling]onClick calibrate button no RDS");
                if (mDisplayManager != null) {
                    if (mDisplayManager.getBTHIDConnectState()) {
                        mIsCalibrate = true;
                        Log.d(TAG, "Calibrate Setting OK");
                        mDisplayManager.setCalibrate();
                    } else {
                        mIsCalibrate = false;
						if(isCanClickCaliButton){
							isCanClickCaliButton = false;
	                		Log.d(TAG, "----Connect BT Hid First  ");
	                		showToastText(getResources().getString(R.string.connect_HID));
							mAH.sendEmptyMessageDelayed(MSG_CALIBRATE_BUTTON, 2000);
						}else{
							Log.d(TAG, "----wait isCanClickCaliButton is true ");
	                    }
                	}
               }
            } else {
                Log.d(TAG, "[linling]onClick calibrate button RDS playing");
                String contentStr = "RDS playing!";
                showToastText(contentStr);
            }
            break;
        }

    }

    private void startSurface() {
        Log.d(TAG, "[lingling]startSurface");
        ViewGroup.LayoutParams viewParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mSurfaceView = new SurfaceView(MiracastActivity.this);
        //mSurfaceView.setBackgroundColor(this.getResources().getColor(R.color.back_default));
        mSurfaceView.getHolder().addCallback(MiracastActivity.this);
        mSurfaceView.setFocusableInTouchMode(false);
        mSurfaceView.setFocusable(false);
        mSinkViewLayout.addView(mSurfaceView, viewParams);
    }

    private void stopSurface() {
        Log.d(TAG, "[lingling]stopSurface");

        mSinkViewLayout.removeAllViews();
        mSurfaceView = null;
        hideCtrlbar();
    }

	private void triggerShownStatusbarAndCurtainView() {
       showStatusbar();
    }

	private void showStatusbar() {
        mSinkViewLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

	private void hideStatusbar() {
        mSinkViewLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private boolean isSinkState(SinkState state) {
        int result = mDisplayManager.getSinkState();

        if (0 == result) {
            mSinkState = SinkState.SINK_STATE_IDLE;
        } else if (1 == result) {
            mSinkState = SinkState.SINK_STATE_WAITING_P2P_CONNECTION;
        } else if (2 == result) {
            mSinkState = SinkState.SINK_STATE_WIFI_P2P_CONNECTED;
        } else if (3 == result) {
            mSinkState = SinkState.SINK_STATE_WAITING_RTSP;
        } else if (4 == result) {
            mSinkState = SinkState.SINK_STATE_RTSP_CONNECTED;
        }

        return  (mSinkState == state) ? true : false;
    }

    private void updateDeviceName() {
        Log.d(TAG, "[lingling]updateDeviceName");
        if (mP2pDevice != null) {
            if (TextUtils.isEmpty(mP2pDevice.deviceName)) {
                mDeviceName = mP2pDevice.deviceAddress;
                Log.d(TAG, "[lingling]updateDeviceName mP2pDevice.deviceAddress:" + mDeviceName);
            } else {
                mDeviceName = mP2pDevice.deviceName;
                Log.d(TAG, "[lingling]updateDeviceName mP2pDevice.deviceName:" + mDeviceName);
            }
        }
    }

    public void setupWfdSinkConnection() {
        Log.d(TAG, "[lingling]setupWfdSinkConnection");
        setWfdMode(true);
        waitWfdSinkConnection();
    }

    public void resumeWfdConnection() {
        Log.d(TAG, "[lingling]resumeWfdConnection");
        if (getSinkSurface() != null) {
            Log.d(TAG, "[lingling]resumeWfdConnection surface != null");
        }
        mDisplayManager.requestSuspendDisplay(false, getSinkSurface());
    }

    public Surface getSinkSurface() {
       Log.d(TAG, "mSurfaceView = " + mSurfaceView.getHolder().getSurface());
        return mSurfaceView != null ? mSurfaceView.getHolder().getSurface() : null;
    }

    public void disconnectWfdSinkConnection() {
        Log.d(TAG, "[lingling]disconnectWfdSinkConnection");
        mDisplayManager.disconnectWifiDisplay();
    }

    private void setWfdMode(boolean sink) {
        Log.d(TAG, "[lingling]setWfdMode " + sink);
        mDisplayManager.requestEnableSink(sink);
    }

    private void waitWfdSinkConnection() {
        Log.d(TAG, "[lingling]waitWfdSinkConnection");
        mDisplayManager.requestWaitConnection(getSinkSurface());

        updateLinkStatus(WFD_WAIT_CONNECT_DIALOG);
    }

    public void sendUibcEvent(String eventDesc) {
        mDisplayManager.sendUibcInputEvent(eventDesc);
    }

    private class OnceNotify implements OnceStatusCallback {

        @Override
        public void notifyOnceStatus(Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "[lingling]BroadcastReceiver on Once " + action);
            if(action.equals(YeconConstants.ACTION_QB_POWEROFF)){
                finish();
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                mP2pDevice = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                updateDeviceName();
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable extra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (extra == null) return;
                NetworkInfo info = (NetworkInfo)extra;
                State state = info.getState();
                if (state == State.CONNECTED && !bIgnoreApConnect) {
                    Log.i(TAG, "The Wifi AP connected, need disconnect it");
                    if(!bIsWaitDialogShowed){
                    	updateLinkStatus(WIFI_DISCONNECT_DIALOG);
					}
                    tryDisconnectWifiAp(true);
                }
            }
        }
    }

    private class DisplayNotify implements DisplayStatusCallback {

        @Override
        public void notifyDisplayStatus(Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "notifyDisplayStatus: " + action);
            if (action.equals(DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED)) {
                Log.d(TAG,"[lingling]BroadcastReceiver ACTION_WIFI_DISPLAY_STATUS_CHANGED");
                WifiDisplayStatus status = (WifiDisplayStatus)intent.getParcelableExtra(DisplayManager.EXTRA_WIFI_DISPLAY_STATUS);
                //mWifiDisplayStatus = status;
                handleWfdStatusChanged(status);

            } else if (action.equals(BroadcastFilter.WFD_SINK_GC_REQUEST_CONNECT)){
                mSinkDeviceName = intent.getStringExtra("deviceName");
                Log.i(TAG, "[lingling]BroadcastReceiver WFD_SINK_GC_REQUEST_CONNECT, mSinkDeviceName:" + mSinkDeviceName);
                Log.d(TAG, "[lingling]BroadcastReceiver WfdSinkExt sinkState: " + mDisplayManager.getSinkState());

                if (!isSinkState(SinkState.SINK_STATE_WAITING_P2P_CONNECTION)) {
                    Log.d(TAG, "[lingling]BroadcastReceiver WfdSinkExt return sinkState: " + mDisplayManager.getSinkState());
                    Log.d(TAG, "[lingling]BroadcastReceiver State is wrong. Decline directly !!");
                    return;
                }
                if (mSinkDeviceName != null) {
                    updateLinkStatus(WFD_BUILD_CONNECT_DIALOG);
                } else {
                    Log.i(TAG, "[lingling]Sink device name is null");
                }

            } else if(BroadcastFilter.P2P_DISCONNECT.equals(action)){
                // TODO : when wifi enabled this broadcast would send?
                if (bIgnoreNextP2pDisconnect || bIgnoreDisplayBroadcast) {
                    Log.d(TAG, "[lingling]Ignore this P2p disconnect, " +
                            "bIgnoreNextP2pDisconnect = " + bIgnoreNextP2pDisconnect + ", " + 
                            "bIgnoreDisplayBroadcast = " + bIgnoreDisplayBroadcast);
                    bIgnoreNextP2pDisconnect = false;
                    return;
                }

                Log.i(TAG, "Progress in P2P disconnect");
                disconnectWfdSinkConnection();
                stopSurface();
                updateLinkStatus(P2P_DISCONNECT_DIALOG);
                mPreWfdState = WifiDisplayStatus.DISPLAY_STATE_NOT_CONNECTED;
                DisplayConnectedFlag = false;
                mAH.removeMessages(MSG_CONNECT_TIME_OUT);                
                resetWifi(DISABLE_WIFI_DELAYED);
                mApp.setDisplayStatusCallback(null);
                vLinkLayout.setVisibility(View.VISIBLE);
                mDialogHub.dismissDialogDetail();
                mSinkViewLayout.setVisibility(View.INVISIBLE);
                notify2resumeA2dp();
            } else if (BroadcastFilter.GROUPNEGOT_P2P_DISCONNECT.equals(action)) {
                ConnectTimeoutCount = 0;
                mAH.removeMessages(MSG_CONNECT_TIME_OUT);
            } else if (BroadcastFilter.ACTION_DISPLAY_DIRECT_READY.equals(action)) {
            	 //tvStep2.setText(R.string.wfd_connecting);
                 ivStep2.setImageResource(R.drawable.android_connect_step_complete);
                 ivStep2.setVisibility(View.VISIBLE);
                 //anim = (AnimationDrawable) ivStep2.getDrawable();
                //anim.start();
                 mDialogHub.dismissDialogDetail();
				 isSuspended = false;
                 vLinkLayout.setVisibility(View.INVISIBLE);
                 notify2closeA2dp();
				 YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);
                 mSinkViewLayout.setVisibility(View.VISIBLE);
            } else if (BroadcastFilter.ACTION_PACKET_LOST.equals(action)){
                detectPacketLost();
            } else if (BroadcastFilter.MIRACAST_CONNECT_START.equals(action)){
                if (bIgnoreDisplayBroadcast) {
                    Log.i(TAG, "[lingling]Ignore this " + action);
                    return;
                }
                if(DisplayConnectedFlag){
					Log.i(TAG,"[lingling]connected Ignore this " + action);
					return;
				}
                ConnectTimeoutCount = 0;
                if (!mAH.hasMessages(MSG_CONNECT_TIME_OUT))
                    mAH.sendEmptyMessageDelayed(MSG_CONNECT_TIME_OUT, CONNECT_TIME_OUT_DELAYED);
                //if (mWifiDisplayStatus.getActiveDisplayState() == WifiDisplayStatus.DISPLAY_STATE_CONNECTING) {
                // TODO : [wjxing]
                if (mPreWfdState == WifiDisplayStatus.DISPLAY_STATE_CONNECTING) {
                	updateLinkStatus(WFD_CONNECTING_DIALOG);
                } else {
                    Log.i(TAG, "[lingling]No need show wfd_connecting_dialog at state " + mPreWfdState);
                }
                bIgnoreNextP2pDisconnect = false;
            } else if (BroadcastFilter.MIRACAST_CONNECT_END.equals(action)){
                if (bIgnoreDisplayBroadcast) {
                    Log.i(TAG, "[lingling]Ignore this " + action);
                    return;
                }
                ConnectTimeoutCount = 0;
                mAH.removeMessages(MSG_CONNECT_TIME_OUT);
                bIgnoreNextP2pDisconnect = false;
            }
        }
    };

    private void detectPacketLost() {
        mAH.removeMessages(MSG_DISMISS_PACKET_LOST);
        prepareDialog(PACKET_LOST_DIALOG);
        mAH.sendEmptyMessageDelayed(MSG_DISMISS_PACKET_LOST, DISMISS_PACKET_LOST_DELAYED);
    }

    private void handleWfdStatusChanged(WifiDisplayStatus status) {
        boolean bStateOn = (status != null && status.getFeatureState() == WifiDisplayStatus.FEATURE_STATE_ON);
        Log.d(TAG, "[lingling]handleWfdStatusChanged bStateOn: " + bStateOn);
        if (bStateOn) {
            int wfdState = status.getActiveDisplayState();
            Log.d(TAG, "[lingling]handleWfdStatusChanged wfdState: " + wfdState);
            if (mPreWfdState != wfdState) {
                Log.d(TAG, "[lingling]handleWfdStatusChanged mPreWfdState != wfdState mPreWfdState: " + mPreWfdState);
                boolean res = handleWfdStateChanged(wfdState);
                if (res) {
                    mPreWfdState = wfdState;
                } else {
                    Log.i(TAG, "[lingling]handleWfdStateChanged fail NOT set mPreWfdState");
                }
                bIgnoreDisplayBroadcast = false;
            }
        } else {
            // TODO :
            /*
             * if (mPreWfdState != -1) {
             * handleWfdStateChanged(WifiDisplayStatus.
             * DISPLAY_STATE_NOT_CONNECTED); } mPreWfdState = -1;
             */
        }
    }

    private static final int MSG_RECONNECT = 0;
    private static final int MSG_DISMISS_PACKET_LOST = 1;
    private static final int MSG_HIDE_STATEBAR = 2;
    private static final int MSG_TEST_DIALOG = 3;
    private static final int MSG_CONNECT_TIME_OUT = 4;
    private static final int MSG_DISABLE_WIFI = 5;
    private static final int MSG_WAIT_WFD_FEATURE_ON = 6;
	private static final int MSG_CALIBRATE_BUTTON = 7;
	private static final int MSG_HIDE_CTRLBAR = 8;
	private static final int MSG_FINISH = 9;
	private static final int MSG_CHECK_WIFI_RESET_TIMEOUT = 10;
	
    private static final int RECONNECT_DELAYED = 5000;
    private static final int DISMISS_PACKET_LOST_DELAYED = 1000;
    private static final int HIDE_STATEBAR_DELAYED = 4000;
    private static final int CONNECT_TIME_OUT_DELAYED = 1000;
    private static final int DISABLE_WIFI_DELAYED = 3000;
    private static final int WAIT_WFD_FEATURE_ON_DELAYED = 100;

    private int ConnectTimeoutCount = 0;
    private static final int ConnectTimeoutMaxCount = 20;
    
    private final int CHECK_WIFI_DISABLE_TIMEOUT = 6000;

    private Handler mAH = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_CHECK_WIFI_RESET_TIMEOUT:
            	this.removeMessages(MSG_CHECK_WIFI_RESET_TIMEOUT);
            	if(mApp.isWifiEnabled() || mApp.isWifiEnabling()){
            		resetWifi(0);
            	}            	
            	break;
            case MSG_RECONNECT:
                Log.d(TAG,"[lingling]MSG_RECONNECT isAppBackgroundFlag =" + isAppBackgroundFlag);
                if (!isAppBackgroundFlag) {
                    mDialogHub.dismissDialogDetail();
                    prepareInit();
                }
                break;
            case MSG_DISMISS_PACKET_LOST:
                mDialogHub.dismissDialogDetail();
                break;
            case MSG_HIDE_STATEBAR:
                hideStatusbar();
                break;
            case MSG_TEST_DIALOG:
                prepareDialog(TEST_DIALOG);
                break;
            case MSG_CONNECT_TIME_OUT:
                Log.d(TAG,"[lingling]MSG_CONNECT_TIME_OUT ConnectTimeoutCount: " + ConnectTimeoutCount);
                if(ConnectTimeoutMaxCount == ConnectTimeoutCount){
                    ConnectTimeoutCount = 0;
                    disconnectWfdSinkConnection();
                }else{
                    ConnectTimeoutCount++;
                    if (!mAH.hasMessages(MSG_CONNECT_TIME_OUT))
                        mAH.sendEmptyMessageDelayed(MSG_CONNECT_TIME_OUT, CONNECT_TIME_OUT_DELAYED);
                }
                break;
            case MSG_DISABLE_WIFI:
                Log.i(TAG, "Do disable wifi message");
                mApp.setWifiEnabled(false, true);
                if (hasMessages(MSG_RECONNECT)) {
                    removeMessages(MSG_RECONNECT);
                }
                break;
            case MSG_WAIT_WFD_FEATURE_ON:
                WifiDisplayStatus status = mDisplayManager.getWifiDisplayStatus();
                if (status.getFeatureState() == WifiDisplayStatus.FEATURE_STATE_ON) {
                    Log.i(TAG, "[lingling]WaitFeatureOn true");
                    doInit();
                } else {
                    Log.i(TAG, "[lingling]WaitFeatureOn false");
                    sendEmptyMessageDelayed(MSG_WAIT_WFD_FEATURE_ON, WAIT_WFD_FEATURE_ON_DELAYED);
                }
                break;
			case MSG_CALIBRATE_BUTTON:
				isCanClickCaliButton = true;
				break;
			case MSG_HIDE_CTRLBAR:
				layoutCtrl.setVisibility(View.INVISIBLE);
				layoutCtrl.startAnimation(mAnimAlpha);
				break;
			case MSG_FINISH:
				onBackPressed();
				break;
            }
        }
    };

    private boolean handleWfdStateChanged(int wfdState) {
        Log.d(TAG, "[lingling]handleWfdStateChanged wfdState : " + wfdState);
        switch (wfdState) {
        case WifiDisplayStatus.DISPLAY_STATE_NOT_CONNECTED:
            if (mPreWfdState == WifiDisplayStatus.DISPLAY_STATE_NOT_CONNECTED) {
                Log.d(TAG, "[lingling]Ignore this DISPLAY_STATE_NOT_CONNECTED, last state is wrong");
                return false;
            }
            Log.d(TAG, "[lingling]handleWfdStateChanged DISPLAY_STATE_NOT_CONNECTED");

            if (mSurfaceView != null) {
                stopSurface();
            }
            // TODO: If invoke startSurface immediately, maybe probolem. So
            // the framework maybe check this issue.
            if (DisplayConnectedFlag) {
                Log.d(TAG, "[lingling]handleWfdStateChanged DisplayConnedFlag : true");
                DisplayConnectedFlag = false;
            }
            updateLinkStatus(P2P_DISCONNECT_DIALOG);
            mApp.setDisplayStatusCallback(null);
            resetWifi(DISABLE_WIFI_DELAYED);
            vLinkLayout.setVisibility(View.VISIBLE);
            mDialogHub.dismissDialogDetail();
            mSinkViewLayout.setVisibility(View.INVISIBLE);
            notify2resumeA2dp();
            Log.d(TAG, "[lingling]handleWfdStateChanged mPreWfdState: " + mPreWfdState);
            break;
        case WifiDisplayStatus.DISPLAY_STATE_CONNECTING:
            Log.d(TAG, "[lingling]handleWfdStateChanged DISPLAY_STATE_CONNECTING");
            break;
        case WifiDisplayStatus.DISPLAY_STATE_CONNECTED:
            Log.d(TAG, "[lingling]handleWfdStateChanged DISPLAY_STATE_CONNECTED");
            if (mPreWfdState != WifiDisplayStatus.DISPLAY_STATE_CONNECTING) {
                Log.d(TAG, "[lingling]Ignore this DISPLAY_STATE_CONNECTED, last state is wrong");
                return false;
            }

            DisplayConnectedFlag = true;
            bIgnoreNextP2pDisconnect = false;

            mDialogHub.dismissDialogDetail();
            
            ivStep2.setImageResource(R.drawable.android_connect_step_complete);
            ivStep2.setVisibility(View.VISIBLE);
            vLinkLayout.setVisibility(View.INVISIBLE);
            notify2closeA2dp();
			YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.USB);
            mSinkViewLayout.setVisibility(View.VISIBLE);
            
            if (isTestDialog)
                mAH.sendEmptyMessageDelayed(MSG_TEST_DIALOG, 3000);

            hideStatusbar();
            break;

        default:
            break;
        }
        return true;
    }

    private Toast toast = null;
    private void showToastText(String content) {
        //Log.d(TAG, "[lingling]showToastText " + content);
        if(toast!=null){
        	toast.cancel();
        	toast  = null;
        }
        toast = Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private OnKeyListener onBackListener = new OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode,
                KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                MiracastActivity.this.finish();
                return true;
            } else {
                return false;
            }
        }
    };

    private void prepareDialog(int dialogID) {
        Log.d(TAG, "[lingling]prepareDialog dialogID:" + dialogID);
        if (WFD_WAIT_CONNECT_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show wait_dialog");
            String text = mDeviceName + " " + getResources().getString(R.string.wait_invitations);
            mDialogHub.prepareDialog(text, onBackListener);            
        } else if (WFD_BUILD_CONNECT_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show build_dialog");
            String text = getResources().getString(R.string.build_connection);
            text = String.format(text, mSinkDeviceName);
            mDialogHub.prepareDialog(text, onBackListener);
        } else if (P2P_DISCONNECT_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show disconnecting_dialog");
            String text = getResources().getString(R.string.miracast_disconnect);
            mDialogHub.prepareDialog(text, onBackListener);
        } else if (DISPLAY_DIRECT_NOT_READY_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show display_direct_not_ready_dialog");
            String text = getResources().getString(R.string.display_direct_not_ready);
            mDialogHub.prepareDialog(text);
        } else if (PACKET_LOST_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show packet_lost_dialog");
            String text = getResources().getString(R.string.packet_lost);
            mDialogHub.prepareDialog(text);
        } else if (TEST_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show packet_lost_dialog");
            String text = getResources().getString(R.string.packet_lost);
            mDialogHub.prepareDialog(text);
        } else if (WFD_CONNECTING_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show wfd_connecting_dialog");
            String text = getResources().getString(R.string.wfd_connecting);
            mDialogHub.prepareDialog(text, onBackListener);
        } else if (NO_WIFI_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show no_wifi_dialog");
            String text = getResources().getString(R.string.wifi_disabled);
            mDialogHub.prepareDialog(text, onBackListener);
        } else if (WIFI_ENABLING_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show wifi_enabling_dialog");
            String text = getResources().getString(R.string.wifi_enabling);
            mDialogHub.prepareDialog(text, onBackListener);
        } else if (WAIT_WFD_FEATURE_ON_DIALOG == dialogID) {
            Log.d(TAG, "[lingling] show wait_wfd_feature_on_dialog");
            String text = getResources().getString(R.string.wait_wfd_feature_on);
            mDialogHub.prepareDialog(text, onBackListener);
        } else if (WIFI_DISCONNECT_DIALOG == dialogID){
			Log.d(TAG, "[lingling] show wif_disconnect_dialog");
            String text = getResources().getString(R.string.disconnect_wifi);
            mDialogHub.prepareDialog(text, onBackListener);
		} else if(WAIT_FINISHING == dialogID){
			String text = getResources().getString(R.string.exiting);
            mDialogHub.prepareDialog(text, onBackListener);
		}
    }

    public WifiChipType GetWifiType() {
        AtcMetazone.readval(WifiMetaIndex, WifiType);
        Log.d("wifitype", "###get WifiType: " + WifiType[0]);
        if (0 == WifiType[0]) {
            return WifiChipType.WIFI5931;
        } else if (1 == WifiType[0]) {
            return WifiChipType.WIFI7601;
        } else if (2 == WifiType[0]) {
            return WifiChipType.WIFI6630;
        } else {
            return WifiChipType.ERROR;
        }
    }

    static public boolean isRDSActive() {
        Parcel reply = Parcel.obtain();
        if (gCbmCtx == null) {
            gCbmCtx = new CBMCtx();
        }
        gCbmCtx.query(reply);
        int souce_count = reply.readInt();
        Log.d(TAG, "[lingling]isRDSActive souce_count=" + souce_count);
        for (int i = 0; i < souce_count; i++) {
            int t = reply.readInt();
            Log.d(TAG, "[lingling]isRDSActive souce=" + t);
            if (CBMCtx.SRC_AVIN_A_RDS == t) {
                reply.recycle();
                return true;
            }
            t = reply.readInt();
        }
        reply.recycle();

        return false;
    }

    // If force == false, then check AP status first.
    private void tryDisconnectWifiAp(boolean force) {
        Log.i(TAG, "Try disconnect Wifi Ap");
        if (!force) {
            WifiInfo info = mWifiManger.getConnectionInfo();
            if (info == null || info.getIpAddress() <= 0) {
                Log.i(TAG, "The AP NOT connect, so NO need disconnect AP");
                return;
            }
        }
        mApp.disconnectWifiAp();
    }

    private class WifiNotify implements WifiStatusCallback {

        @Override
        public void notifyWifiStatus(int state) {
            if (state == WifiManager.WIFI_STATE_ENABLED) {
                if (mAH.hasMessages(MSG_RECONNECT) || !mWifiManger.isWifiEnabled()){
                	Log.d(TAG, "[lingling]?  what? ");
                	return;
                }
                Log.d(TAG, "[lingling]Do the Wifi enabled Background? (" + isAppBackgroundFlag + ")");
                if (isAppBackgroundFlag) {
                    mApp.setOnceStatusCallback(null);
                    mApp.setWifiStatusCallback(null);
                    mApp.setDisplayStatusCallback(null);
                } else {
                    mApp.setDisplayStatusCallback(mDisplayNotify);
                    mAH.sendEmptyMessageDelayed(MSG_RECONNECT, RECONNECT_DELAYED);
                }
                bIgnoreNextP2pDisconnect = true;
                updateLinkStatus(WIFI_ENABLED);
            } else if (state == WifiManager.WIFI_STATE_DISABLED) {    
            	mAH.removeMessages(MSG_CHECK_WIFI_RESET_TIMEOUT);
                Log.d(TAG, "[lingling]Do the Wifi disabled");
                if (isAppBackgroundFlag) {
					Log.d(TAG,"----kuan wifi disabled, in background");
                    mApp.setOnceStatusCallback(null);
                    mApp.setWifiStatusCallback(null);
                    mApp.setDisplayStatusCallback(null);
                } else {
                	Log.d(TAG,"----kuan wifi disabled, not in background");
                    mApp.setWifiEnabled(true, true);
                }
            } else if (state == WifiManager.WIFI_STATE_ENABLING) {
                Log.d(TAG, "[lingling]Do the Wifi enabling");
                updateLinkStatus(WIFI_ENABLING_DIALOG);
            }
        }
    }
    
    private void updateLinkStatus(int status){
    	if(status == WIFI_ENABLING_DIALOG){
    		 tvStep1.setText("1. " + this.getString(R.string.wifi_enabling));
             ivStep1.setImageResource(R.anim.status_wait);
             ivStep1.setVisibility(View.VISIBLE);
             anim = (AnimationDrawable) ivStep1.getDrawable();
             anim.start();
    	}
    	else if(NO_WIFI_DIALOG == status){
    		 tvStep1.setText("1. " + this.getString(R.string.wifi_disabled));
             ivStep1.setImageResource(R.anim.status_wait);
             ivStep1.setVisibility(View.VISIBLE);
             anim = (AnimationDrawable) ivStep1.getDrawable();
             anim.start();
    	}
    	else  if(WIFI_ENABLED == status){
    		tvStep1.setText("1. " + this.getString(R.string.wifi_enabled));
            ivStep1.setImageResource(R.drawable.android_connect_step_complete);
            ivStep1.setVisibility(View.VISIBLE);
    	}
    	else  if(WAIT_WFD_FEATURE_ON_DIALOG == status){
			tvStep2.setText("2. "+ getResources().getString(R.string.wait_wfd_feature_on));
			ivStep2.setImageResource(R.anim.status_wait);
			ivStep2.setVisibility(View.VISIBLE);
			anim = (AnimationDrawable) ivStep2.getDrawable();
			anim.start();
		} else if (WFD_WAIT_CONNECT_DIALOG == status) {
			tvStep1.setText("1. " + this.getString(R.string.wifi_enabled));
            ivStep1.setImageResource(R.drawable.android_connect_step_complete);
            ivStep1.setVisibility(View.VISIBLE);            
			tvStep2.setText("2. "+ getResources().getString(R.string.wait_invitations) +": "+mDeviceName);
			ivStep2.setImageResource(R.anim.status_wait);
			ivStep2.setVisibility(View.VISIBLE);
			anim = (AnimationDrawable) ivStep2.getDrawable();
			anim.start();
    	}
		else if(WFD_BUILD_CONNECT_DIALOG == status){
			String text = getResources().getString(R.string.build_connection);
            text = String.format(text, mSinkDeviceName);
			tvStep2.setText("2. "+ text);
			ivStep2.setImageResource(R.anim.status_wait);
			ivStep2.setVisibility(View.VISIBLE);
			anim = (AnimationDrawable) ivStep2.getDrawable();
			anim.start();
		}
		else if(P2P_DISCONNECT_DIALOG == status){
			tvStep2.setText("2. "+ getResources().getString(R.string.miracast_disconnect));
			ivStep2.setImageResource(R.anim.status_wait);
			ivStep2.setVisibility(View.VISIBLE);
			anim = (AnimationDrawable) ivStep2.getDrawable();
			anim.start();
		}
		else if(WIFI_DISCONNECT_DIALOG == status){
			
		}
		else if(WFD_CONNECTING_DIALOG == status){
			tvStep2.setText("2. "+ getResources().getString(R.string.wfd_connecting));
			ivStep2.setImageResource(R.anim.status_wait);
			ivStep2.setVisibility(View.VISIBLE);
			anim = (AnimationDrawable) ivStep2.getDrawable();
			anim.start();
		}
		else if(DISPLAY_DIRECT_NOT_READY_DIALOG == status){
			
		}
    }
    
    private BroadcastReceiver quickBootListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MCU_ACTION_ACC_ON)){
            	
            }
            else if(action.equals(MCU_ACTION_ACC_OFF)){
            	onBackPressed();
            }
            else if (action.equals(YeconConstants.ACTION_QB_POWERON)) {
            		
            } else if (action.equals(YeconConstants.ACTION_QB_POWEROFF)) {
            	onBackPressed();
            }
        }
    };

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.layout_frame:
			if(layoutTools.getVisibility()!=View.INVISIBLE){
        		layoutTools.setVisibility(View.INVISIBLE);
        	}
			break;
		}
		return false;
	}
}
