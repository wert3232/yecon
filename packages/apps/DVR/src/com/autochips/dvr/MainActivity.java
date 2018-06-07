package com.autochips.dvr;

import static android.constant.YeconConstants.ACTION_QB_POWEROFF;
import static android.constant.YeconConstants.ACTION_QB_POWERON;
import static android.constant.YeconConstants.ACTION_QB_PREPOWEROFF;
import static android.constant.YeconConstants.ACTION_QUIT_APK;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.autochips.dvr.DVR.DVRNativeEvent;
import com.autochips.storage.EnvironmentATC;
import com.yecon.common.SourceManager;
import com.yecon.common.YeconEnv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity
{
    private final static String TAG = "dvr_8317";
    private Object syncObj = new Object();

    private final static String dvrdir = "/dvr/";
    
    public static final String EXT_SDCARD1_PATH = "/mnt/ext_sdcard1";
    public static final String EXT_SDCARD2_PATH = "/mnt/ext_sdcard2";
    public static final String UDISK1_PATH = "/mnt/udisk1";
    public static final String UDISK2_PATH = "/mnt/udisk2";
    public static final String UDISK3_PATH = "/mnt/udisk3";
    public static final String UDISK4_PATH = "/mnt/udisk4";
    public static final String UDISK5_PATH = "/mnt/udisk5";
    
    private static String mRecoderFilePath = EXT_SDCARD1_PATH + dvrdir;
    private Button mPreviewBtn;
    private Button mRecoderBtn;
    private Button mUrgentBtn;
    private Button mAudioOnBtn;
    private Button mSnapshotBtn;
    private TextView mFileListBtn;
    private Bitmap bm;
    FrontView mFrontView = null;
    private Toast snapshotToast;
    private LocationManager locationManager;
    private String locationProvider;
    private View mSettingView = null;
    private LinearLayout mLinearLayout=null;
    private RadioGroup mRGPath = null;
    private RadioButton msd1 = null;
    private RadioButton msd2 = null;
    private RadioButton musb1 = null;
    private RadioButton musb2 = null;
    private RadioButton musb3 = null;
    private RadioButton musb4 = null;
    private RadioButton musb5 = null;
    private RadioGroup mRGResolution = null;
    private RadioButton mR1080p = null;
    private RadioButton mR720p = null;
    private RadioGroup mRGDuration = null;
    private RadioButton mD10Mins = null;
    private RadioButton mD5Mins = null;
    private RadioButton mD3Mins = null;
    private LinearLayout bottom_bar = null;
    private LinearLayout top_bar = null;
    private static int MSG_FULLSCREEN = 0;

    private static int mRecordDuration = 5 * 60;
    private int mRecordCapacity = 8 * 1024; // in M Bytes, means 8G for Normal Record Size.
    private int mUrgentCapacity = 2 * 1024; // in M Bytes, means 2G for Urgent Record Size.

    private static int mResolutionX = 1280;
    private static int mResolutionY = 720;

    private final static int mCamVendorId = 3141;
    private final static int mCamProductId = 25446;
    private TimerTask mTimerTask = null;

    private DVR mDvr = new DVR();

    public final static int STATE_IDLE              = (0x01 << 0);
    public final static int STATE_INITED            = (0x01 << 1);
    public final static int STATE_PREVIEW           = (0x01 << 2);
    public final static int STATE_RECORD            = (0x01 << 3);
    public final static int STATE_SD_UNMOUNT        = (0x01 << 4);
    public final static int STATE_CAMERA_DETATCH    = (0x01 << 5);
    public final static int STATE_PAUSE             = (0x01 << 6);
    public final static int STATE_CHANGE_RESOLUTION = (0x01 << 7);
    
    public static int mDvrState = STATE_IDLE;
    EnvironmentATC EnvATC;
    YeconEnv ygEnv;

    private boolean audioOn = false;
    private boolean urgent = false;
    private boolean settingMenuIn = true;
    private boolean needvisible = false;
    private boolean mTempDvrPreviewFlag=false;
    private ImageView mRecordIconImage;
    private static boolean mShowIconFlag=false;
    private Object sourceTocken;
    private boolean mDvrRecordFlag=false;
    private boolean mDvrInitFlag=false;
    Runnable mShowiconRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mDvrRecordFlag) {
				if (mShowIconFlag) {
					mShowIconFlag = false;
					if (mRecordIconImage != null) {
						mRecordIconImage.setVisibility(View.GONE);
					}					
				}else {
					mShowIconFlag = true;
					if (mRecordIconImage != null) {
						mRecordIconImage.setVisibility(View.VISIBLE);
					}					
				}
			}else {
				mShowIconFlag = false;
				mRecordIconImage.setVisibility(View.GONE);
			}
			mShowiconHandler.postDelayed(mShowiconRunnable, 1000);
		}
	};
	Handler mShowiconHandler = new Handler();
			
    private void onHomePressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	onHomePressed();
    }
    
    boolean dvr_stop_record() {
    	boolean ret = mDvr.StopRecord();
    	mDvrRecordFlag = false;
    	return ret;
    }
    boolean dvr_start_record() {
    	boolean ret = mDvr.StartRecord();
    	mDvrRecordFlag = true;
    	return ret;
    }
    
    boolean dvr_init() {
    	boolean ret=false;
    	
    	if (mDvrInitFlag) {
    		Log.d(TAG, "dvr--- dvr has init, now deinit");
    		mDvr.Deinit();
    	}
    	
    	Log.d(TAG, "dvr--- start dvr init");
   		ret = mDvr.Init();
   		
   		Log.d(TAG, "dvr--- dvr init result="+ret);
   		mDvrInitFlag = true;
    	return ret;
    }
    
    boolean dvr_deinit() {
    	boolean ret = false;
    	
    	if (mDvrInitFlag) {
    		Log.d(TAG, "dvr--- start dvr deinit");
    		ret = mDvr.Deinit();
    		Log.d(TAG, "dvr--- dvr deinit result="+ret);
    	}else{
    		Log.d(TAG, "dvr--- dvr not init, do not need deinit");
    		ret = true;
    	}
    	
    	return ret;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
 //   	YeconEnv.checkStorageExist(env)
    	
        mDvrState = STATE_IDLE;
        mDvrInitFlag = false;
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate entry");
        /*
        sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.dvr);
        SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify() {
			
			@Override
			public void onAudioFocusChange(int arg0) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onAudioFocusChange:" +arg0 );
				switch (arg0) {
                case AudioManager.AUDIOFOCUS_LOSS:
                	finish();
                	break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: 
                    break;
                case AudioManager.AUDIOFOCUS_GAIN: 
                    break;
				}
			}

		});   */    
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        mPreviewBtn = (Button) findViewById(R.id.PreviewBtn);
        mRecoderBtn = (Button) findViewById(R.id.RecoderBtn);
        mUrgentBtn = (Button) findViewById(R.id.UrgentBtn);
        mAudioOnBtn = (Button) findViewById(R.id.AudioOnBtn);
        mSnapshotBtn = (Button) findViewById(R.id.SnapshotBtn);
        mFileListBtn = (TextView) findViewById(R.id.FileListBtn);
        bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar);
        mRecordIconImage = (ImageView)findViewById(R.id.imageView_recordicon);
  //      top_bar = (LinearLayout) findViewById(R.id.top_bar);
        FrameLayout layout = (FrameLayout)findViewById(R.id.mainLayout);
        layout.setOnTouchListener(mViewOnTouchListener);

  //      mPreviewBtn.setText("Preview");
 //       mRecoderBtn.setText("Recoder");
   //     mAudioOnBtn.setText("Audio On");

        regiestPowerOnOffReceiver();
        regiestUsbDevAttachedReceiver();
        regiestMountUnmountReceiver();

        EnvATC = new EnvironmentATC(this);
        ygEnv = new YeconEnv();
        
        mFrontView = (FrontView) this.findViewById(R.id.Surfaceview);
        mFrontView.setDvr(mDvr);
        snapshotToast = Toast.makeText(this, "Snapshot Processing ...", Toast.LENGTH_SHORT);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        snapshotToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, screenWidth * 3 / 4, screenHeight * 11 / 16);
        
        Log.i(TAG, "onCreate screenWidth " + screenWidth);

        //Get Location Provider and Location Manager
        getLocationProvider();
        //Listen Location Change..
        if ((null != locationManager) && (null != locationProvider)) {
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        }

        checkVendorProductId();

        audioOn = false;
        
        mPreviewBtn.setOnClickListener(previewBtnClickListener);
        mRecoderBtn.setOnClickListener(recoderBtnClickListener);
        mUrgentBtn.setOnClickListener(urgentOnBtnClickListener);
        mAudioOnBtn.setOnClickListener(audioOnBtnClickListener);
        mSnapshotBtn.setOnClickListener(snapshotBtnClickListener);
        mFileListBtn.setOnClickListener(filelistBtnClickListener);
        
        mSettingView = View.inflate(this, R.layout.view_setting, null);
        mSettingView.setVisibility(View.VISIBLE);
        mLinearLayout = (LinearLayout)findViewById(R.id.box);
        mLinearLayout.addView(mSettingView);
        mFrontView.setOnTouchListener(mViewOnTouchListener);
        
        mRGResolution = (RadioGroup) findViewById(R.id.Resolution);
        mR1080p = (RadioButton) findViewById(R.id.R1080p);
        mR720p = (RadioButton) findViewById(R.id.R720p);
        mRGResolution.setOnCheckedChangeListener(resolutionChangedListener);
  //      mR720p.setChecked(true);
       
        mRGDuration = (RadioGroup) findViewById(R.id.Duration);
        mD10Mins = (RadioButton) findViewById(R.id.D10Mins);
        mD5Mins = (RadioButton) findViewById(R.id.D5Mins);
        mD3Mins = (RadioButton) findViewById(R.id.D3Mins);
        mRGDuration.setOnCheckedChangeListener(durationChangedListener);
  //      mD5Mins.setChecked(true);
		starttimer();

		if (mResolutionY == 720) {
			mR720p.setChecked(true);
		}else{
			mR1080p.setChecked(true);
		}
		
		ReadRecordTime(this);
		if (mRecordDuration == 10 * 60) {
			mD10Mins.setChecked(true);
		}else if (mRecordDuration == 5 * 60) {
			mD5Mins.setChecked(true);
		}else if (mRecordDuration == 3 * 60) {
			mD3Mins.setChecked(true);
		}

        mRGPath = (RadioGroup) findViewById(R.id.Path);
    	msd1 = (RadioButton)mSettingView.findViewById(R.id.SD1);
    	msd2 = (RadioButton)mSettingView.findViewById(R.id.SD2);
    	musb1 = (RadioButton)mSettingView.findViewById(R.id.USB1);
    	musb2 = (RadioButton)mSettingView.findViewById(R.id.USB2);
    	musb3 = (RadioButton)mSettingView.findViewById(R.id.USB3);
    	musb4 = (RadioButton)mSettingView.findViewById(R.id.USB4);
    	musb5 = (RadioButton)mSettingView.findViewById(R.id.USB5);
        mRGPath.setOnCheckedChangeListener(pathChangedListener);
        ReadSavePath(this);
		checkpath();
        
        mDvrState |= STATE_INITED;
        if (0 == (mDvrState & STATE_CAMERA_DETATCH)) {
            startDVR();
            mPreviewBtn.performClick();
            
            if (ReadRecordState()) {
            	mRecoderBtn.performClick();
			}
        }

        mShowiconHandler.postDelayed(mShowiconRunnable, 1000);
        
        Log.i(TAG, "onCreate end");
    }
    private void showtip(int nindex)
    {
    	String tip = "";
    	switch (nindex) {
		case 1:
			tip = getString(R.string.tip1) + EXT_SDCARD1_PATH + getString(R.string.tip2);
			break;
		case 2:
			tip = getString(R.string.tip1) + EXT_SDCARD2_PATH + getString(R.string.tip2);
			break;
		case 3:
			tip = getString(R.string.tip1) + UDISK1_PATH + getString(R.string.tip2);
			break;
		case 4:
			tip = getString(R.string.tip1) + UDISK2_PATH + getString(R.string.tip2);
			break;
		case 5:
			tip = getString(R.string.tip1) + UDISK3_PATH + getString(R.string.tip2);
			break;
		case 6:
			tip = getString(R.string.tip1) + UDISK4_PATH + getString(R.string.tip2);
			break;
		case 7:
			tip = getString(R.string.tip1) + UDISK5_PATH + getString(R.string.tip2);
			break;

		default:
			break;
		}
        Toast.makeText(MainActivity.this, tip, Toast.LENGTH_SHORT).show();
    }
    private boolean iscanrecord()
    {
    	return mRecoderFilePath.equals(EXT_SDCARD1_PATH + dvrdir) && YeconEnv.checkStorageExist(EnvATC, EXT_SDCARD1_PATH)
    			|| mRecoderFilePath.equals(EXT_SDCARD2_PATH + dvrdir) && YeconEnv.checkStorageExist(EnvATC, EXT_SDCARD2_PATH)
    			|| mRecoderFilePath.equals(UDISK1_PATH + dvrdir) && YeconEnv.checkStorageExist(EnvATC, UDISK1_PATH)
    			|| mRecoderFilePath.equals(UDISK2_PATH + dvrdir) && YeconEnv.checkStorageExist(EnvATC, UDISK2_PATH)
    			|| mRecoderFilePath.equals(UDISK3_PATH + dvrdir) && YeconEnv.checkStorageExist(EnvATC, UDISK3_PATH)
    			|| mRecoderFilePath.equals(UDISK4_PATH + dvrdir) && YeconEnv.checkStorageExist(EnvATC, UDISK4_PATH)
    			|| mRecoderFilePath.equals(UDISK5_PATH + dvrdir) && YeconEnv.checkStorageExist(EnvATC, UDISK5_PATH);
    }
    private void checkpath()
    {
	    	if (YeconEnv.checkStorageExist(EnvATC, EXT_SDCARD1_PATH)) {
	        	msd1.setVisibility(View.VISIBLE);
	        	if (mRecoderFilePath.equals(EXT_SDCARD1_PATH + dvrdir)) {
	        		msd1.setChecked(true);
	        	}
			}else{
	        	msd1.setVisibility(View.GONE);
	        	if (mRecoderFilePath.equals(EXT_SDCARD1_PATH + dvrdir)) {
	        		showtip(1);
	        	}
	        }
        	if (YeconEnv.checkStorageExist(EnvATC, EXT_SDCARD2_PATH)) {
            	msd2.setVisibility(View.VISIBLE);
            	if (mRecoderFilePath.equals(EXT_SDCARD2_PATH + dvrdir)) {
            		msd2.setChecked(true);
            	}
    		}else{
            	msd2.setVisibility(View.GONE);
            	if (mRecoderFilePath.equals(EXT_SDCARD2_PATH + dvrdir)) {
            		showtip(2);
        		}
            }
	    	if (YeconEnv.checkStorageExist(EnvATC, UDISK1_PATH)) {
	    		musb1.setVisibility(View.VISIBLE);
	        	if (mRecoderFilePath.equals(UDISK1_PATH + dvrdir)) {
	        		musb1.setChecked(true);
	        	}
			}else{
				musb1.setVisibility(View.GONE);
	        	if (mRecoderFilePath.equals(UDISK1_PATH + dvrdir)) {
	        		showtip(3);
	    		}
	        }
	    	if (YeconEnv.checkStorageExist(EnvATC, UDISK2_PATH)) {
	    		musb2.setVisibility(View.VISIBLE);
		    	if (mRecoderFilePath.equals(UDISK2_PATH + dvrdir)) {
		    		musb2.setChecked(true);
		    	}
			}else{
				musb2.setVisibility(View.GONE);
		    	if (mRecoderFilePath.equals(UDISK2_PATH + dvrdir)) {
		    		showtip(4);
				}
	        }
	    	if (YeconEnv.checkStorageExist(EnvATC, UDISK3_PATH)) {
	    		musb3.setVisibility(View.VISIBLE);
		    	if (mRecoderFilePath.equals(UDISK3_PATH + dvrdir)) {
		    		musb3.setChecked(true);
		    	}
			}else{
				musb3.setVisibility(View.GONE);
		    	if (mRecoderFilePath.equals(UDISK3_PATH + dvrdir)) {
		    		showtip(5);
				}
	        }
	    	if (YeconEnv.checkStorageExist(EnvATC, UDISK4_PATH)) {
	    		musb4.setVisibility(View.VISIBLE);
		    	if (mRecoderFilePath.equals(UDISK4_PATH + dvrdir)) {
		    		musb4.setChecked(true);
		    	}
			}else{
				musb4.setVisibility(View.GONE);
		    	if (mRecoderFilePath.equals(UDISK4_PATH + dvrdir)) {
		    		showtip(6);
				}
	        }
	    	if (YeconEnv.checkStorageExist(EnvATC, UDISK5_PATH)) {
	    		musb5.setVisibility(View.VISIBLE);
		    	if (mRecoderFilePath.equals(UDISK5_PATH + dvrdir)) {
		    		musb5.setChecked(true);
		    	}
			}else{
				musb5.setVisibility(View.GONE);
		    	if (mRecoderFilePath.equals(UDISK5_PATH + dvrdir)) {
		    		showtip(7);
				}
	        }
    	
    }
    private void starttimer()
    {
    	if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
    			@Override
    			public void run() {
    				if (settingMenuIn) {
    					eventHandler.sendMessage(eventHandler.obtainMessage(MSG_FULLSCREEN, null));
    				}
    			}
    		};
		}
        new Timer().schedule(mTimerTask, 8000, 8000);
    }
    private void stoptimer()
    {
    	if (mTimerTask == null) {
			return;
		}
    	mTimerTask.cancel();
    	mTimerTask = null;
    }
    private void restarttimer()
    {
    	stoptimer();
    	starttimer();
    }
    private void WriteRecordState(boolean bRecord){
        SharedPreferences.Editor sharedata = getApplicationContext().getSharedPreferences("DVR", 0).edit();
        sharedata.putBoolean("isrecordlast", bRecord);
        sharedata.commit();
    }
    private boolean ReadRecordState(){
        SharedPreferences sharedata = getApplicationContext().getSharedPreferences("DVR", 0);
        return sharedata.getBoolean("isrecordlast", false);
    }
    private RadioGroup.OnCheckedChangeListener pathChangedListener = new RadioGroup.OnCheckedChangeListener() {	
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
        	restarttimer();
	        if (0 == (mDvrState & STATE_CAMERA_DETATCH)) {
				if (checkedId == msd1.getId()) {
					mRecoderFilePath = EXT_SDCARD1_PATH + dvrdir;
				}else if (checkedId == msd2.getId()) {
					mRecoderFilePath = EXT_SDCARD2_PATH + dvrdir;
				}else if (checkedId == musb1.getId()) {
					mRecoderFilePath = UDISK1_PATH + dvrdir;
				}else if (checkedId == musb2.getId()) {
					mRecoderFilePath = UDISK2_PATH + dvrdir;
				}else if (checkedId == musb3.getId()) {
					mRecoderFilePath = UDISK3_PATH + dvrdir;
				}else if (checkedId == musb4.getId()) {
					mRecoderFilePath = UDISK4_PATH + dvrdir;
				}else if (checkedId == musb5.getId()) {
					mRecoderFilePath = UDISK5_PATH + dvrdir;
				}
				if ((mDvrState & STATE_INITED) != 0) {
					boolean brestart = false;
					if ((mDvrState & STATE_RECORD) != 0) {
						dvr_stop_record();
						brestart = true;
						sendBroadcast(new Intent(MsgDefine.ACTION_DVR_RECORD_STOP));
					}
			        mDvr.SetRecordPath(mRecoderFilePath);
					if (brestart) {
						dvr_start_record();
					}
				}
		        WriteSavePath(getApplicationContext());
	        }
		}
	};
    private RadioGroup.OnCheckedChangeListener resolutionChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId){
        	restarttimer();
            int iX = 0, iY = 0;
            if (checkedId == mR1080p.getId()) {
                Log.i(TAG, "Resolution 1080p is selected");
                mResolutionX = 1920;
                mResolutionY = 1080;
            }
            if (checkedId == mR720p.getId()) {
                Log.i(TAG, "Resolution 720p is selected");
                mResolutionX = 1280;
                mResolutionY = 720;
            }
            if (0 != (mDvrState & STATE_CAMERA_DETATCH)) {
                Toast.makeText(MainActivity.this, R.string.Camera_Tip, Toast.LENGTH_SHORT).show();
                return;
            }
            if ((0 != (mDvrState & STATE_INITED)) && (mResolutionX != iX))
            {
       //         mResolutionX = iX;
     //           mResolutionY = iY;
                mDvrState |= STATE_CHANGE_RESOLUTION;
                stopDVR(true);
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener durationChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId){
        	restarttimer();
            if (checkedId == mD10Mins.getId()) {
                Log.i(TAG, "Record duration 10mins is selected");
                mRecordDuration = 10 * 60;
            }
            if (checkedId == mD5Mins.getId()) {
                Log.i(TAG, "Record duration 5mins is selected");
                mRecordDuration = 5 * 60;
            }
            if (checkedId == mD3Mins.getId()) {
                Log.i(TAG, "Record duration 3mins is selected");
                mRecordDuration = 3 * 60;
            }
            
            WriteRecordTime(getApplicationContext());
            
            if (0 != (mDvrState & STATE_CAMERA_DETATCH)) {
                Toast.makeText(MainActivity.this, R.string.Camera_Tip, Toast.LENGTH_SHORT).show();
                return;
            }
            if (0 != (mDvrState & STATE_INITED)) {
                mDvr.SetRecordDuration(mRecordDuration); // unit: s
            }
        }
    };

    /**
    * Location Listerner
    * Parameter: Location Provider, Listen interval , Location change as distance(m), Location Listener.
    */
    LocationListener locationListener =  new LocationListener() {
        @Override  
        public void onStatusChanged(String provider, int status, Bundle arg2) {  
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location location) {
            //If Location Changed, Reset Location and Speed for Camera.
            setCameraEx(location);
        }
    };
    
    @Override
    public void onStart()
    {
        Log.i(TAG, "onStart entry");
        super.onStart();
        Log.i(TAG, "onStart end");
    }
    
    @Override
    public void onRestart()
    {
        Log.i(TAG, "onRestart entry");
        super.onRestart();
        Log.i(TAG, "onRestart end");
    }
    
    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume entry");
        SourceManager.acquireSource(sourceTocken);

        if (((0 != (mDvrState & STATE_PREVIEW)) &&
            (0 != (mDvrState & STATE_PAUSE)) &&
            (0 == (mDvrState & STATE_CAMERA_DETATCH))) ||
            needvisible) {
            Log.i(TAG, "onResume, set visible!");
            mFrontView.setVisibility(View.VISIBLE);
            starttimer();
        }
        
        
        super.onResume();
        
        mDvrState &= ~STATE_PAUSE;
        Log.i(TAG, "onResume end");
    }

    
    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause entry");
        stoptimer();
        if ((0 != (mDvrState & STATE_PREVIEW)) &&
            (0 == (mDvrState & STATE_CAMERA_DETATCH))){
            mDvr.StopPreview();
        }

        super.onPause();
        
        mDvrState |= STATE_PAUSE;
        
        addImageToSystemDatabase();
        
        Log.i(TAG, "onPause end");
    }

    @Override
    public void onStop()
    {
        Log.i(TAG, "onStop entry");
        super.onStop();
        Log.i(TAG, "onStop end");
    }


    @Override
    public void onDestroy() 
    {
        Log.i(TAG, "onDestroy entry");
        
        super.onDestroy();
        
        stopDVR(false);
        mDvrState = STATE_IDLE;
        
        unregiestPowerOnOffReceiver();
        unregiestUsbDevAttachedReceiver();
        unregiestMountUnmountReceiver();
        
        SourceManager.unregisterSourceListener(sourceTocken);
        
        mShowiconHandler.removeCallbacks(mShowiconRunnable);
        
        Log.i(TAG, "onDestroy end");
        
        //LogcatHelper.getInstance(this).stop();
    }

    private void getLocationProvider()
    {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //Get all available location Provider
        if (null != locationManager) {
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //Is GPS
                locationProvider = LocationManager.GPS_PROVIDER;
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)){
                //Is Network
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(this, "No Available Location Provider", Toast.LENGTH_SHORT).show();
            }
        }
    }

	private void switchFullScreen(boolean bFull) {
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		if (!bFull) {
			mSettingView.clearAnimation();
			TranslateAnimation tAnim = new TranslateAnimation(
					-mSettingView.getWidth() - mSettingView.getLeft(),
					mSettingView.getLeft(), 0, 0);
			tAnim.setDuration(500);
			tAnim.setInterpolator(new OvershootInterpolator());
			tAnim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					mSettingView.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub
				}

			});			
			mSettingView.startAnimation(tAnim);
			settingMenuIn = true;
			params.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
			window.setAttributes(params);
			bottom_bar.setVisibility(View.VISIBLE);
			// top_bar.setVisibility(View.GONE);
			restarttimer();
		} else {
			mSettingView.clearAnimation();
			//ViewGroup vp = (ViewGroup) mSettingView.getParent();			
			TranslateAnimation tAnim = new TranslateAnimation(
					mSettingView.getLeft(), -mSettingView.getWidth()
							- mSettingView.getLeft(), 0, 0);
			tAnim.setDuration(500);
			tAnim.setInterpolator(new OvershootInterpolator());
			tAnim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					mSettingView.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub
				}

			});			
			mSettingView.startAnimation(tAnim);
			settingMenuIn = false;
			params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			window.setAttributes(params);
			bottom_bar.setVisibility(View.INVISIBLE);
			// top_bar.setVisibility(View.VISIBLE);
			stoptimer();
		}
	}

    private View.OnTouchListener mViewOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(TAG, "Get touch evnet MotionEvent.ACTION_UP");
                if(!settingMenuIn)
                {
                	switchFullScreen(false);
                }
                else
                {
                	switchFullScreen(true);
                }
            }
            return true;
        }
    };
    

    private void setCameraEx(Location location) 
    {
        // Set System Time
        Date curDate = new Date(System.currentTimeMillis());
        DVR.SysTime systime = new DVR.SysTime();
        systime.setTime(curDate.getYear() + 1900, 
                        curDate.getMonth() + 1,
                        curDate.getDate(),
                        curDate.getHours(),
                        curDate.getMinutes(),
                        curDate.getSeconds());
        
        mDvr.SetTime(systime);

        // Set Coordinate
        if (null != location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            int lat_deg, lat_min, lat_sec, lon_deg, lon_min, lon_sec;
            lat_deg = (int)lat;
            lat = lat - (double)lat_deg;
            lat *= 60;
            lat_min = (int)lat;
            lat = lat - (double)lat_min;
            lat_sec = (int)(lat * 60);

            lon_deg = (int)lon;
            lon = lon - (double)lon_deg;
            lon *= 60;
            lon_min = (int)lon;
            lon = lon - (double)lon_min;
            lon_sec = (int)(lon * 60);

            DVR.Coordinate coord = new DVR.Coordinate();
            coord.setCoordinate(lat_deg, lat_min, lat_sec, lon_deg, lon_min, lon_sec);
            mDvr.SetGpsCoordinate(coord);

            // Set Speed.
            mDvr.SetGpsSpeed((int)location.getSpeed());
        }
    }

    /**
     * when start dvr app, we will check it onCreate function
     */
    private void checkVendorProductId() 
    {
        //check camera vendor id and product id
        UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        
        mDvrState |= STATE_CAMERA_DETATCH;

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            int vendorId = device.getVendorId();
            int productId = device.getProductId();
            
            Log.i(TAG, "checkVendorProductId device vendorId " + vendorId + ", productId " + productId);
            if ((mCamVendorId == vendorId) && (mCamProductId == productId)) {
                if (device.getDeviceName().substring(9, 16).equals("usb/002")) {
                    mDvrState &= ~STATE_CAMERA_DETATCH;
                }
            }
        }

        if (0 != (mDvrState & STATE_CAMERA_DETATCH)) {
            Toast.makeText(MainActivity.this, R.string.Camera_Tip, Toast.LENGTH_SHORT).show();
        }
    }
        
    private void regiestPowerOnOffReceiver() {
        Log.i(TAG, "regiestPowerOnOffReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_QB_POWEROFF);
        filter.addAction(ACTION_QB_PREPOWEROFF);
        filter.addAction(ACTION_QB_POWERON);
        filter.addAction(ACTION_QUIT_APK);
        registerReceiver(mPowerOnOffReceiver, filter);
    }

    
    private void unregiestPowerOnOffReceiver() {
        Log.i(TAG, "unregiestPowerOnOffReceiver");
        unregisterReceiver(mPowerOnOffReceiver);
    }

    BroadcastReceiver mPowerOnOffReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); 
            Log.i(TAG, "QB Power on off BroadcastReceiver, action:" + action);
            if (ACTION_QB_POWEROFF.equals(action)) {
                Log.i(TAG, "QB Power on off BroadcastReceiver, power off");
                
                mDvr.setAudioOn(false);
                stopDVR(false);
            }
            else if (ACTION_QB_POWERON.equals(action)) {
                Log.i(TAG, "QB Power on off BroadcastReceiver, power on");
                checkpath();
                checkVendorProductId();
                mDvrState &= ~STATE_RECORD;
                mRecoderBtn.setText(R.string.recorder);
                mRecoderBtn.setBackgroundResource(R.xml.bg_recoderbtn);
                mPreviewBtn.setBackgroundResource(R.xml.bg_previewbtn);
                mDvrState &= ~STATE_PREVIEW;
        //        mPreviewBtn.setText("Preview");
         //       mPreviewBtn.setBackgroundResource(R.xml.bg_previewbtn);
                audioOn = false;
          //      mAudioOnBtn.setText("Audio On");
                mAudioOnBtn.setBackgroundResource(R.xml.bg_mute_btn);
                if (0 == (mDvrState & STATE_CAMERA_DETATCH)) {
                    startDVR();
                    mPreviewBtn.performClick();
                    
                    if (ReadRecordState()) {
                    	mRecoderBtn.performClick();
        			}
                }
                
            } else if (ACTION_QUIT_APK.equals(action)) {
				finish();
			}
        }
    };
     
    private void regiestUsbDevAttachedReceiver() {
        Log.i(TAG, "regiestUsbDevAttachedReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
    }

    
    private void unregiestUsbDevAttachedReceiver() {
        Log.i(TAG, "unregiestUsbDevAttachedReceiver");
        unregisterReceiver(mUsbReceiver);
    }
    
    private void stopDVR(boolean changeResolutionFlag) {
        if (0 != (mDvrState & STATE_RECORD)) {
            Log.i(TAG, "Stop DVR: stop record");
            dvr_stop_record();
            sendBroadcast(new Intent(MsgDefine.ACTION_DVR_RECORD_STOP));
            mRecoderBtn.setText(R.string.recorder);
            mRecoderBtn.setBackgroundResource(R.xml.bg_recoderbtn);
        }
        
        if ((0 == (mDvrState & STATE_PAUSE)) &&
            (0 != (mDvrState & STATE_PREVIEW))) {

			
            Log.i(TAG, "Stop DVR: stop preview");
            mDvr.StopPreview();
            if (changeResolutionFlag) {
            	mDvrState &= ~STATE_PREVIEW;//因为不支持的分辨率，不能调用 startPreview否则会死,所以切换分辨率，先把这个标志去掉
            	mTempDvrPreviewFlag = true;
            }
        }
        else {
            mDvr.setHandler(null);
            Log.i(TAG, "Stop DVR: dvr deinit");
            dvr_deinit();

            if (0 != (mDvrState & STATE_CHANGE_RESOLUTION))
            {
                Log.i(TAG, "Stop DVR: Restart DVR for resolution changed!");
                mDvrState &= ~STATE_CHANGE_RESOLUTION;
                startDVR();
            }
        }
    }

    private boolean SDCardMounted()
    {
    	return iscanrecord();
    	/*
        String strSDMountedPath[] = EnvATC.getSdMountedPaths();
        for (int i = 0; i < strSDMountedPath.length; i++)
        {
            Log.i(TAG, "SD Card Path is: " + strSDMountedPath[i]);
            if (mountpath.equals(strSDMountedPath[i])) {
                return true;
            }
        }
        return false;*/
    }
    
    private void startDVR() {
        // We Must Set VideoInfo Before Init DVR driver.
        DVR.SetVideoInfo(new DVR.VideoInfo(mResolutionX, mResolutionY, 30));

        dvr_init();
        
        // Set System Time
        Date curDate = new Date(System.currentTimeMillis());
        DVR.SysTime systime = new DVR.SysTime();
        systime.setTime(curDate.getYear() + 1900, 
                        curDate.getMonth() + 1,
                        curDate.getDate(),
                        curDate.getHours(),
                        curDate.getMinutes(),
                        curDate.getSeconds());
        mDvr.SetTime(systime);

        mDvr.setHandler(eventHandler);
        mDvr.SetRecordPath(mRecoderFilePath);
        mDvr.SetRecordDuration(mRecordDuration); // unit: s
        mDvr.SetRecordCapacity(mRecordCapacity, mUrgentCapacity);
        mDvr.setAudioOn(audioOn);

        if ((null != locationManager) && (null != locationProvider)) {
            setCameraEx(locationManager.getLastKnownLocation(locationProvider));
        }

        if (0 != (mDvrState & STATE_RECORD)){
            Log.i(TAG, "USB DEV BroadcastReceiver, device attached start record");
            if (SDCardMounted()) {
                if (dvr_start_record()) {
                    //mRecoderBtn.setText("Stop Recoder");
                }
                else {
                    Log.i(TAG, "Failed to Start Record.");
                }
            }
            else {
            	checkpath();
         //       Toast.makeText(MainActivity.this, "Please Insert ", Toast.LENGTH_SHORT).show();
            }
        }

        if ( (0 != (mDvrState & STATE_PREVIEW)) && (0 == (mDvrState & STATE_PAUSE)) ) {
            mFrontView.setVisibility(View.INVISIBLE);
            Log.i(TAG, "USB DEV BroadcastReceiver, device attached set front view visible");			
            mFrontView.setVisibility(View.VISIBLE);
        }
    }
    
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            checkpath();
            
            String action = intent.getAction();
            
            Log.i(TAG, "USB DEV BroadcastReceiver, action:" + action);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.i(TAG, "USB DEV BroadcastReceiver, device attached");

                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                int vendorId = 0;
                int productId = 0;
                String deviceName=null;
                if (device != null) {
                	 vendorId = device.getVendorId();
                     productId = device.getProductId();
                     deviceName = device.getDeviceName();
                }
               
                Log.i(TAG, "USB DEV BroadcastReceiver vendorId " + vendorId + ", productId " + productId);
                
                if ((mCamVendorId != vendorId) || 
                    (mCamProductId != productId)){
                    Log.i(TAG, "USB DEV BroadcastReceiver, device is not our camera");
                    return;
                }
                
                if (deviceName != null && deviceName.length() > 15) {
                	if (!deviceName.substring(9, 16).equals("usb/002"))
                    {
                        Log.i(TAG, "USB DEV BroadcastReceiver, camera not in USB0");
                        return ;
                    }
                }

                mDvr.setHandler(null);
                dvr_deinit();
                mDvrState &= ~STATE_CAMERA_DETATCH;
                Log.i(TAG, "before startDvr()");
                startDVR();                
            }
            else {
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                	stoptimer();
                	
                    Log.i(TAG, "USB DEV BroadcastReceiver, device detached");
                    
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    int vendorId = 0;
                    int productId = 0;
                    String deviceName=null;
                    if (device != null) {
                    	vendorId = device.getVendorId();
                        productId = device.getProductId();
                        deviceName = device.getDeviceName();
                    }
                    Log.i(TAG, "USB DEV BroadcastReceiver vendorId " + vendorId + ", productId " + productId);
                    
                    if ((mCamVendorId != vendorId) || 
                        (mCamProductId != productId)){
                        Log.i(TAG, "USB DEV BroadcastReceiver, device is not our camera");
                        return;
                    }

                    if (deviceName != null && deviceName.length() > 15) {
                    	if (!deviceName.substring(9, 16).equals("usb/002"))
                        {
                            Log.i(TAG, "USB DEV BroadcastReceiver, camera not in USB0");
                            return ;
                        }
                    }

                    mDvrState |= STATE_CAMERA_DETATCH;
                    
					Log.i(TAG, "before stopDvr() ----->" + mDvrState);
                    
					stopDVR(false);
                }
            }
        }
    };

    private void WriteRecordTime(Context context){
        SharedPreferences.Editor sharedata = context.getSharedPreferences("DVR", 0).edit();
        sharedata.putInt("DVR_RecordTime", mRecordDuration);
        sharedata.commit();
    }
    private void ReadRecordTime(Context context){
        SharedPreferences sharedata = context.getSharedPreferences("DVR", 0);
        mRecordDuration = sharedata.getInt("DVR_RecordTime", 5*60);
    }
    
    private void WriteSavePath(Context context){
        SharedPreferences.Editor sharedata = context.getSharedPreferences("DVR", 0).edit();
        sharedata.putString("DVR_SavePath", mRecoderFilePath);
        sharedata.commit();
    }

    private void ReadSavePath(Context context){
        SharedPreferences sharedata = context.getSharedPreferences("DVR", 0);
        mRecoderFilePath = sharedata.getString("DVR_SavePath", EXT_SDCARD1_PATH + dvrdir);
    }
    
    private void regiestMountUnmountReceiver() {
        IntentFilter iFilter = new IntentFilter();
        /*
        iFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        iFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        iFilter.addAction(Intent.ACTION_MEDIA_NOFS);
        iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        iFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        */
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addDataScheme("file");
        registerReceiver(mMountUnmountBroadcastReceiver, iFilter);
    }
    
    private void unregiestMountUnmountReceiver() {
        Log.i(TAG, "unregiestMountUnmountReceiver");
        unregisterReceiver(mMountUnmountBroadcastReceiver);
    }

    private BroadcastReceiver mMountUnmountBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	checkpath();
            String action = intent.getAction();
            final Uri uri = intent.getData();
            if (uri.getScheme().equals("file")) {
                String path = uri.getPath();
                if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    Log.i(TAG, "mount msg:" + action + "  path:" + path);
                    if (0 != (mDvrState & STATE_RECORD)){
                        if (dvr_start_record()) {
                            mRecoderBtn.setText(R.string.stop);
                            mRecoderBtn.setBackgroundResource(R.xml.bg_recoder_no_btn);
                        }
                        else {
                            Log.i(TAG, "Failed to Start Record.");
                        }
                    }
                    mDvr.StorageMount(true);
                    mDvrState &= ~STATE_SD_UNMOUNT;
                } 
                else {
                    if (action.equals(Intent.ACTION_MEDIA_CHECKING) ||
                        action.equals(Intent.ACTION_MEDIA_NOFS) ||
                        action.equals(Intent.ACTION_MEDIA_REMOVED) ||
                        action.equals(Intent.ACTION_MEDIA_SHARED) ||
                        action.equals(Intent.ACTION_MEDIA_UNMOUNTED) ||
                        action.equals(Intent.ACTION_MEDIA_EJECT)) {
                        if (mRecoderFilePath.indexOf(path) >= 0) {
                            Log.i(TAG, "sd card unmount");
                            if (0 != (mDvrState & STATE_RECORD)) {
                                dvr_stop_record();
                                sendBroadcast(new Intent(MsgDefine.ACTION_DVR_RECORD_STOP));
                                mRecoderBtn.setText(R.string.recorder);
                                mRecoderBtn.setBackgroundResource(R.xml.bg_recoderbtn);
                            }
                            mDvr.StorageMount(false);
                            mDvrState |= STATE_SD_UNMOUNT;
                        }
                    }
                }
            }
        }
    };

    /**
     * 停止预览，注意 摄像头没插上或者不支持的分辨率，目前调用stopPreview会死
     */
    void stopPreview() {
    	if ( 0 == (mDvrState & STATE_CAMERA_DETATCH)) {
    		stoptimer();
    		mDvr.StopPreview();
            mPreviewBtn.setBackgroundResource(R.xml.bg_previewbtn);
            mDvrState &= ~STATE_PREVIEW;
    	}        
    }
    
    private void startPreView() {
    	if ( 0 == (mDvrState & STATE_CAMERA_DETATCH)) {
    		mDvrState |= STATE_PREVIEW;
            mFrontView.setVisibility(View.INVISIBLE);
            mFrontView.setVisibility(View.VISIBLE);
            mPreviewBtn.setBackgroundResource(R.xml.bg_preview_no_btn);
            restarttimer();
    	}    	
    }
    
    private Button.OnClickListener previewBtnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            synchronized(syncObj) {
                if (0 != (mDvrState & STATE_CAMERA_DETATCH)) {
                    Toast.makeText(MainActivity.this, R.string.Camera_Tip, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Log.i(TAG, "Current Camera Name is: " + mDvr.GetCamName());
                
                if (0 == (mDvrState & STATE_PREVIEW)) {
                	startPreView();
                }
                else {
                	stopPreview();
                }
            }
        }
    };

    Button.OnClickListener recoderBtnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v)
        {
        	restarttimer();
            if (0 != (mDvrState & STATE_CAMERA_DETATCH)) {
                Toast.makeText(MainActivity.this, R.string.Camera_Tip, Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (0 == (mDvrState & STATE_RECORD)) {
                if (SDCardMounted()) {
                    if (dvr_start_record()) {
                    	WriteRecordState(true);
                        mDvrState |= STATE_RECORD;
                        mRecoderBtn.setText(R.string.stop);
                        mRecoderBtn.setBackgroundResource(R.xml.bg_recoder_no_btn);
                        Log.i(TAG, "Started Recorder");
                    }
                    else {
                        Log.i(TAG, "start record fail");
                    }
                }
                else {
                	checkpath();
              //      Toast.makeText(MainActivity.this, "Please Insert ", Toast.LENGTH_SHORT).show();
                }
            }
            else {
            	WriteRecordState(false);
                dvr_stop_record();
                mDvrState &= ~STATE_RECORD;
                sendBroadcast(new Intent(MsgDefine.ACTION_DVR_RECORD_STOP));
                mRecoderBtn.setText(R.string.recorder);
                mRecoderBtn.setBackgroundResource(R.xml.bg_recoderbtn);
                Log.i(TAG, "Stopped Recorder");
            }
        }
    };

    private Button.OnClickListener urgentOnBtnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
        	restarttimer();
            Log.i(TAG, "start urgent");
            
            if (0 != (mDvrState & STATE_CAMERA_DETATCH)) {
                Toast.makeText(MainActivity.this, R.string.Camera_Tip, Toast.LENGTH_SHORT).show();
                return;
            }

            if (SDCardMounted()) {
	            if (!mDvr.Urgent()) {
					return;
				}
            }else {
            	checkpath();
            }
            urgent = !urgent;
            if (urgent) {
            	WriteRecordState(true);
                //mUrgentBtn.setText("Urgent");
                //mUrgentBtn.setTextColor(android.graphics.Color.RED);
            }
            else {
            	WriteRecordState(false);
                //mUrgentBtn.setText("Urgent");
                //mUrgentBtn.setTextColor(android.graphics.Color.BLACK);
            }
        }
    };

    private Button.OnClickListener audioOnBtnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
        	restarttimer();
            if (!audioOn) {
                audioOn = true;
                mDvr.setAudioOn(audioOn);
          //      mAudioOnBtn.setText("Audio Off");
                mAudioOnBtn.setBackgroundResource(R.xml.bg_mute_no_btn);
            }
            else {
                audioOn = false;
                mDvr.setAudioOn(audioOn);
         //       mAudioOnBtn.setText("Audio On");
                mAudioOnBtn.setBackgroundResource(R.xml.bg_mute_btn);
            }
        }
    };

    private Button.OnClickListener snapshotBtnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
        	restarttimer();
            if (0 != (mDvrState & STATE_CAMERA_DETATCH)) {
                Toast.makeText(MainActivity.this, R.string.Camera_Tip, Toast.LENGTH_SHORT).show();
                return;
            }
            if (SDCardMounted()) {
                snapshotToast.show();
                mDvr.Snapshot(new DVR.VideoInfo(mResolutionX, mResolutionY, 30), 1);
            }
            else {
            	checkpath();
          //      Toast.makeText(MainActivity.this, "Please Insert ", Toast.LENGTH_SHORT).show();
            }
        }
    };
    
    private Button.OnClickListener filelistBtnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
        		Intent intent = new Intent();
        		intent.setClass(MainActivity.this, FileListActivity.class);
        		startActivity(intent);
            }
    };

	void addImageToSystemDatabase() {
		//Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR");
		//intent.setData(Uri.fromFile(new File(mRecoderFilePath + "image/")));
		//sendBroadcast(intent);
		final FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				// TODO Auto-generated method stub
				return name.endsWith(".jpg")||name.endsWith(".png");
			}
		};

		File file = new File(mRecoderFilePath + "image/");
		File[] imageFiles = file.listFiles(filter);
		if (imageFiles != null) {
			int count = imageFiles.length;			
			if (imageFiles != null && count > 0) {
				for (int i = 0; i < count; i++) {
					String[] paths = new String[] { imageFiles[i].getAbsolutePath() };
					MediaScannerConnection.scanFile(getApplicationContext(), paths, null,null);
				}
			}
		}		
	}
    
    ////////////////////
    private ShutterCallback myShutterCallback = new ShutterCallback() {
        @Override
        public void onShutter(){
        }
    };
    
    private PictureCallback myRawCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        }    
    };
    
    private PictureCallback myjpegCallback = new PictureCallback() {
        String BITMAP = new String();
        @Override
        public void onPictureTaken(byte[] data, Camera camera) 
        {
            bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
    };

    private Handler eventHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            int arg1 = msg.arg1;
            int arg2 = msg.arg2;
            Log.i(TAG, "eventNotify, event:" + what + " (" + arg1 + ", " + arg2 + ")");

            if (what == MSG_FULLSCREEN) {
            	if (settingMenuIn && 0 != (mDvrState & STATE_PREVIEW)) {
            		switchFullScreen(true);
				}
                return;
			}
            switch (what)
            {
            case DVRNativeEvent.DVR_UI_MSG_PREV_STOPED:
            	if (mFrontView != null)
                {
//            		Toast.makeText(getApplicationContext(), "receive prev stop msg",
//            			     Toast.LENGTH_SHORT).show();
            		
                    Log.i(TAG, "eventNotify, preview stoped!");
                    mFrontView.setVisibility(View.INVISIBLE);
                    
                    if (0 == (mDvrState & STATE_PAUSE)) {
                        Log.i(TAG, "eventNotify, set visible!");
                        mFrontView.setVisibility(View.VISIBLE);
                    }
                    else {
                        Log.i(TAG, "eventNotify, needvisible true!");
                        needvisible = true;
                    }

                    if (0 != (mDvrState & STATE_CAMERA_DETATCH))
                    {
                    	 Log.i(TAG, "eventNotify, dvr deinit!");
                        mDvr.setHandler(null);
                        dvr_deinit();
//                        Toast.makeText(getApplicationContext(), "receive prev stop msg, dvr_deinit",
//               			     Toast.LENGTH_SHORT).show();
                    }
                    else if (0 != (mDvrState & STATE_CHANGE_RESOLUTION))
                    {
                        Log.i(TAG, "eventNotify, Restart DVR for resolution changed!");
                        dvr_deinit();
                        startDVR();
                        mDvrState &= ~STATE_CHANGE_RESOLUTION;
                    }
                }
                break;

            case DVRNativeEvent.DVR_UI_MSG_PREV_STARTED:
                if (mFrontView != null)
                {
                    Log.i(TAG, "eventNotify, preview started");
                }
                break;

            case DVRNativeEvent.DVR_UI_MSG_SNAPSHOT_END:
                snapshotToast.cancel();               
                Log.i(TAG, "eventNotify, snapshot end");
                //addImageToSystemDatabase();
                
                break;

            case DVRNativeEvent.DVR_UI_MSG_SD_FULL:
                if (0 != (mDvrState & STATE_RECORD)) {
                    dvr_stop_record();
                    mDvrState &= ~STATE_RECORD;
                    sendBroadcast(new Intent(MsgDefine.ACTION_DVR_RECORD_STOP));
                    mRecoderBtn.setText(R.string.recorder);
                    mRecoderBtn.setBackgroundResource(R.xml.bg_recoderbtn);
                }
                Toast.makeText(MainActivity.this, "SD Card Full!!!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Stopped Recorder");
                break;
            case DVRNativeEvent.DVR_UI_MSG_DEINIT_FAILED:
            	Toast.makeText(MainActivity.this, "DVR_UI_MSG_DEINIT_FAILED", Toast.LENGTH_LONG).show();
            	break;
            case DVRNativeEvent.DVR_UI_MSG_INIT_FAILED:
            	Toast.makeText(MainActivity.this, "DVR_UI_MSG_INIT_FAILED", Toast.LENGTH_LONG).show();
                break;

            case DVRNativeEvent.DVR_UI_MSG_START_REC_FAILED:
                break;

            case DVRNativeEvent.DVR_UI_MSG_START_PREV_FAILED:
            	Toast.makeText(MainActivity.this, "DVR_UI_MSG_START_PREV_FAILED", Toast.LENGTH_LONG).show();
                break;

            case DVRNativeEvent.DVR_UI_MSG_REC_STARTED:
                if (0 == (mDvrState & STATE_RECORD)) {
                    if (SDCardMounted()) {
                        mDvrState |= STATE_RECORD;
                        mRecoderBtn.setText(R.string.stop);
                        mRecoderBtn.setBackgroundResource(R.xml.bg_recoder_no_btn);
                        Log.i(TAG, "Started Recorder");
                    }
                }
                else {
                    mRecoderBtn.setText(R.string.stop);
                    mRecoderBtn.setBackgroundResource(R.xml.bg_recoder_no_btn);
                }
                
                sendBroadcast(new Intent(MsgDefine.ACTION_DVR_RECORD_START));
                break;

            case DVRNativeEvent.DVR_UI_MSG_FORMAT_INVALID:
                if (720 == arg2)
                {
                    Toast.makeText(MainActivity.this, "Camera Not Support 720P", Toast.LENGTH_LONG).show();
                }
                else if (1080 == arg2)
                {
                    Toast.makeText(MainActivity.this, "Camera Not Support 1080P", Toast.LENGTH_LONG).show();
                    mR720p.setChecked(true);
                    mResolutionX = 1280;
                    mResolutionY = 720;
                    
                    dvr_deinit();
                    startDVR();
                    if (mTempDvrPreviewFlag) {
                    	mTempDvrPreviewFlag = false;
                    	startPreView();
                    }
                }

            default:
                break;
            }
        }
    };
}
