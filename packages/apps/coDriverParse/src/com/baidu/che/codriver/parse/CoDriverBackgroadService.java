package com.baidu.che.codriver.parse;


import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;

import com.baidu.che.codriver.navi.NaviManager;
import com.baidu.che.codriver.platform.CoDriverPlatformConstants;
import com.baidu.che.codriver.platform.CoDriverPlatformManager;
import com.baidu.che.codriver.platform.ICoDriverCallback;

public class CoDriverBackgroadService extends Service implements ICoDriverCallback {
	private final String TAG = "coDriverParse.CoDriverBackgroadService";
	private final static String PERSYS_VOICE_BTN_ENABLE = "persist.sys.voice_btn_enable";
	private final static String ACTION_VOICE_BTN_ENABLE = "com.yecon.action.ACTION_VOICE_BTN_ENABLE";
	private Context mContext;
	
	WindowManager mWindowManager = null;
	WindowManager.LayoutParams ballWmParams = null;
	private View mBallView = null;
	private Button mBtnCoDriver = null;
	private float mXpos = 0;
	private float mYpos = 0;
	private float mTouchStartX = 0;
	private float mTouchStartY = 0;
	private boolean mIsmoving = false;
	protected static final long TIME = 1000; 
	
	@Override
	public void onCreate() {
		Log.d(TAG, "CoDriverBackgroadService.onCreate()");
		mContext = this;
		Function.setContext(mContext);
		mBallView = LayoutInflater.from(this).inflate(R.layout.float_menu, null);
		mBtnCoDriver = (Button) mBallView.findViewById(R.id.btn_start_codriver);
		createView();
		IntentFilter filter = new IntentFilter(ACTION_VOICE_BTN_ENABLE);
		registerReceiver(mReceiver, filter);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "CoDriverBackgroadService.onDestroy()");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		CoDriverPlatformManager.getInstance().init(
				getApplicationContext(),
				this,
				CoDriverPlatformConstants.CMD_FORMAT_OPEN_SDK
						| CoDriverPlatformConstants.CMD_TYPE_PHONE
						| CoDriverPlatformConstants.CMD_TYPE_MUSIC
						| CoDriverPlatformConstants.CMD_TYPE_NAVI
						| CoDriverPlatformConstants.CMD_TYPE_SYSTEM
						| CoDriverPlatformConstants.CMD_TYPE_OTHER);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "CoDriverBackgroadService.onBind()");
		return null;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			if (intent.getAction().equals(ACTION_VOICE_BTN_ENABLE)) {
				showFloatUi();
			}
		}
	};
	
	
	Handler handler = new Handler();  
    Runnable runnable = new Runnable() {  
        @Override  
        public void run() {  
            // handler 
            try {  
               
                if(mBtnCoDriver != null ){    				
                	mBtnCoDriver.setPressed(false);
                	mBtnCoDriver.setActivated(false);
                	mBtnCoDriver.invalidate();
    			}
                handler.removeCallbacks(runnable);
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
                System.out.println("exception...");  
            }  
        }  
    };  
    
	long m_tiemcheck=System.currentTimeMillis();
	private void createView() {
		mWindowManager = (WindowManager) getApplicationContext().getSystemService("window");
		ballWmParams =  ((MyApplication) getApplication()).getMywmParams();
		ballWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		ballWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		ballWmParams.gravity = Gravity.LEFT | Gravity.TOP;
		
		WindowManager wm = (WindowManager) getBaseContext()
                .getSystemService(Context.WINDOW_SERVICE);
		ballWmParams.x = wm.getDefaultDisplay().getWidth();
		ballWmParams.y = wm.getDefaultDisplay().getHeight()/2;
		ballWmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		ballWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		ballWmParams.format = PixelFormat.TRANSLUCENT;
		//
		
		mBtnCoDriver.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				mXpos = event.getRawX();
				mYpos = event.getRawY(); 
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mIsmoving = false;
					mTouchStartX = (int)event.getX();
					mTouchStartY = (int)event.getY();
					m_tiemcheck=System.currentTimeMillis();
					handler.removeCallbacks(runnable);
					break;
				case MotionEvent.ACTION_MOVE:
					mIsmoving = true;
					updateViewPosition();
					handler.removeCallbacks(runnable);
					handler.postDelayed(runnable, TIME); 
					break;
				case MotionEvent.ACTION_UP:
					long after=System.currentTimeMillis()-m_tiemcheck;
					if (after<300) {
						boolean isTop = false;
						ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE) ;
//			            List<ActivityManager.RunningAppProcessInfo> appList = mActivityManager.getRunningAppProcesses() ;
//			            for (RunningAppProcessInfo running : appList) {
//		                    if (running.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//		                    	if (running.processName.equals("com.baidu.che.codriver")) {
//		                    		isTop = true;
//								}
//		                    	break;
//		                    }
//			            }
			            ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
			            String packageName = cn.getPackageName();
			            if (packageName != null && packageName.equals("com.baidu.che.codriver")) {
			            	isTop = true;
						}
			            Log.i(TAG ,"packageName:"+packageName);
			            if (isTop) {
			            	Function.sendKeyCode(KeyEvent.KEYCODE_BACK);
						}else {
							if (!Function.sIsReverse) {
								Function.onStartActivity(CoDriverBackgroadService.this, 
										"com.baidu.che.codriver", "com.baidu.che.codriver.ui.MainActivity");
							}
						}
					} else {
						mTouchStartX = mTouchStartY = 0;
						handler.postDelayed(runnable, TIME); 
					}
					break;
				}
				//
				if(mIsmoving == false){
					return false;
				}else{
					return true;
				}
			}
		});
		showFloatUi();
	}
	
	private void showFloatUi(){
		boolean enable = SystemProperties.getBoolean(PERSYS_VOICE_BTN_ENABLE, true);
		if (enable) {
			mWindowManager.addView(mBallView, ballWmParams);
		} else {
			mWindowManager.removeView(mBallView);
		}
	}
	
	private void updateViewPosition() {
		ballWmParams.x = (int) (mXpos - mTouchStartX);
		ballWmParams.y = (int) (mYpos - mTouchStartY - mBtnCoDriver.getWidth()/2);
		mWindowManager.updateViewLayout(mBallView, ballWmParams);
	}
	
	@Override
	public String getValue(String key) {
		if (CoDriverPlatformConstants.KEY_CONTACT.equals(key)) {
			boolean isContactReady = true; // 如果联系人已经ready
			if (isContactReady) {
				return CoDriverPlatformConstants.VALUE_CONTACT_CONTENTPROVIDER;
			} else {
				return CoDriverPlatformConstants.VALUE_CONTACT_NOT_READY;
			}
		} else if (CoDriverPlatformConstants.KEY_BT_STATE.equals(key)) {
			if (Function.sConnectState) {
				return CoDriverPlatformConstants.VALUE_BT_STATE_CONNECTED;
			} else {
				return CoDriverPlatformConstants.VALUE_BT_STATE_DISCONNECTED;
			}
		} else {
			// 对于无法识别的key，默认返回此值，实现向前兼容
			return CoDriverPlatformConstants.VALUE_UNSUPPORT_KEY;
		}
	}

	@Override
	public int setValue(String key, String value) {
		Log.i("coDriver", "setValue.....key:" + key + ".......value:" + value);
		return CoDriverPlatformConstants.RETURN_SUCCESS;
	}

	@Override
	public int onCommandReceived(int cmdType, String command) {
		Log.i("coDriver", "onCommandReceived......cmdType:" + cmdType + "....command:" + command);
		switch (cmdType) {
		case CoDriverPlatformConstants.CMD_TYPE_MUSIC:
			// 音乐类指令，目前无需处理，在SDK内部已经处理
			if (Function.handleMusic(command)) {
				return 0;
			} else {
				break;
			}
		case CoDriverPlatformConstants.CMD_TYPE_NAVI:
			// 导航类指令，目前无需处理，在SDK内部已经处理
			if (NaviManager.getInstance(this).excuteCmd(command)) {
				return 0;
			} else {
				break;
			}
		case CoDriverPlatformConstants.CMD_TYPE_PHONE:
			// 电话类指令，domain取值为"telephone"，需要对指令进行解析后执行
			if (Function.handleTelePhone(mContext, command)) {
				return 0;
			} else {
				break;
			}
		case CoDriverPlatformConstants.CMD_TYPE_SYSTEM:
			// 系统类指令，domain取值为"app","setting","instruction"，需要对指令进行解析后执行
			if (Function.handleSystem(mContext, command)) {
				return 0;
			} else {
				break;
			}
		case CoDriverPlatformConstants.CMD_TYPE_OTHER:
			// 其他指令，需要对指令进行解析后执行
			Function.handleOther(command);
			break;
		default:
			break;
		}
		return 0;
	}

	@Override
	public void onEvent(int event, String args) {
		Log.i("coDriver", "onEvent......................." + event);
		switch (event) {
		case CoDriverPlatformConstants.EVENT_ERROR_NOT_INITED:
			// 未调用init方法，调用后SDK方可正常运行
			break;
		case CoDriverPlatformConstants.EVENT_INIT_FAILED:
			// 初始化失败
			break;
		case CoDriverPlatformConstants.EVENT_INIT_SUCCEED:
			// 初始化成功
			break;
		case CoDriverPlatformConstants.EVENT_SERVICE_DISCONNECTED:
			// 连接断开，需要重新调用init方法
			break;
		default:
			break;
		}
	}
}
