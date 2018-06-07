package com.autochips.bluetooth;

import static android.constant.YeconConstants.ACTION_QB_POWEROFF;
import static android.mcu.McuExternalConstant.PROPERTY_KEY_BTPHONE_STARTUP;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.PhoneCallActivity.MyTimerTask;
import com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtPlayerManage;
import com.autochips.bluetooth.hf.BluetoothHfAtHandler;
import com.autochips.bluetooth.hf.BluetoothHfService;
import com.autochips.bluetooth.hf.BluetoothHfUtility;
import com.autochips.bluetooth.util.L;
import com.autochips.settings.AtcSettings;
import com.media.constants.MediaConstants;
import com.tuoxianui.tools.AtTimerHelpr;
import com.tuoxianui.view.DeviceStateMode;
import com.tuoxianui.view.TopBar;
import com.yecon.common.SourceManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.mcu.McuExternalConstant;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public class TuoXianDialActivity extends Activity implements OnClickListener,OnLongClickListener{
	//commom
	ActivityManager mActivityManager;
	private View handUpBtn;
	// dial
	private final int MAXPHONENUM = 20; 
    private TextView m_callnumber_et;
    private View callBtn;
    public static String minputstr = "";
    
    // call
    Intent call_state_change_intent = new Intent(BluetoothConstants.ACTION_BLUETOOTH_CALL_STATUS_CHANGE);
    
    private Object sourceTocken = null;
    private MyTimerTask m_MyTimerTask = null;
    public static int mStartTimer = 0;
    private autoAnswerTimeoutThread m_thrdAutoAnswerTimeout = null;
    private String m_strPhoneNum = "";
    private String m_strPhoneName = "";
//    private TextView m_phonetimer;
    private TextView callingNameTextView;
    private TextView callingNumTextView;
    private TextView mIncomeNameTextView;
    private TextView mIncomeNumTextView;
    private View dialogCallingView;
    private View dialogIncomeView;
    private TextView m_phonetimer;
    private boolean isBTTop = true;
    private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	private AtTimerHelpr mAtTimerHelpr;
	private boolean isReturnMusicWindow = false;
	private boolean isPowerOff = false;
	private static int SWITCH_TO_PAIR = 100;
	private static boolean mCallVoiceOnPhone = false;
	private boolean isHandStart = false;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		L.v("lifecycle " + this.getClass()  + " : onCreate");
		Intent serviceIntent = new Intent(this, BTService.class);
		startService(serviceIntent);
		setContentView(R.layout.bt_call_tuoxian_version);
		MainApp.addActivity(this); 
		initView();
		mPref = this.getSharedPreferences("system_settings", Context.MODE_PRIVATE);
		mEditor = mPref.edit();
		
		mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//call
		BluetoothReceiver.registerNotifyHandler(uiHandler);
		sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.bt_phone);
        SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify(){
			@Override
			public void onAudioFocusChange(int arg0) {
				//do nothing
			}     	
        });
        SourceManager.acquireSource(sourceTocken, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        
    	//控制蓝牙音量
        setVolumeControlStream(AudioManager.STREAM_BLUETOOTH_SCO);
        
    	//ui
        updateuiforcallstate();
        checkAutoAnswerOption();
        /*if (Bluetooth.getInstance().iscallidle()) {
        }*/
        if (getIntent().getBooleanExtra("isspeaking", false)) {
			startspeaktimer();
		}
        
        TopBar topBar = (TopBar) findViewById(R.id.topbar);
        topBar.setTopBarBackCall(new TopBar.TopBarBackCall(){
        	public void onHomeReturn(){
       		onBackPressed();
	//			MainApp.exitAllActivity();
        	}
        });
        
        
        if(mPref.getBoolean("startTag", false) && Bluetooth.getInstance().iscallidle()){
			findViewById(R.id.call_bt_music).callOnClick();
			mPref.edit().putBoolean("startTag", false).commit();
		}else{
			mPref.edit().putBoolean("startTag", false).commit();
		}
		isHandStart = getIntent().getBooleanExtra("handStart",false);
	}

	@Override
	protected void onStart() {
		super.onStart();
		L.v("lifecycle " + this.getClass()  + " : onStart Thread: " + Thread.currentThread() + "  hashCode:" + this.hashCode());
		L.i("lifecycle " + isHandStart);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		L.v("lifecycle onNewIntent" + this.getClass()  + " : onStart Thread: " + Thread.currentThread() + "  hashCode:" + this.hashCode());
		isHandStart = intent.getBooleanExtra("handStart",false);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		L.v("lifecycle " + this.getClass()  + " : onRestart Thread: " + Thread.currentThread() + "  hashCode:" + this.hashCode());
	}

	@Override
	protected void onResume() {
		super.onResume();
		L.v("lifecycle " + this.getClass()  + " : onResume Thread: " + Thread.currentThread() + "  hashCode:" + this.hashCode());
		isPowerOff = false;
		sendBroadcast(new Intent(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH), null);
		checkPrevPackage();
		IntentFilter otherIntentFilter = new IntentFilter();
		otherIntentFilter.addAction("action.bt.return.prev.window");
		/*
		FIXME:#11101 并不需要插入USB、SD跳出,执行操作
		otherIntentFilter.addAction("com.action.storage.sd_mount");
		otherIntentFilter.addAction("com.action.storage.usb_mount");
		*/
		otherIntentFilter.addAction(DeviceStateMode.SHARED_PREFERENCES_NAME);
		otherIntentFilter.addAction(YeconConstants.ACTION_QB_PREPOWEROFF);
		otherIntentFilter.addAction("action.hzh.media.power.on");
		registerReceiver(otherReceiver,otherIntentFilter);
		registerReceiver(homeReceiver, new IntentFilter("android.intent.action.homekey_down"));
		//FIXME:一汽要求,不自动流转 mAtTimerHelpr.start(10);
		sendOrderedBroadcast(new Intent("action.bt.dialphoneview.on"), null);
		L.e(SystemProperties.get("persist.sys.top_media","empty"));
		if(!Bluetooth.getInstance().isConnected()){
	        (findViewById(R.id.btn_call_zero)).setEnabled(false);
	        (findViewById(R.id.btn_call_one)).setEnabled(false);
	        (findViewById(R.id.btn_call_two)).setEnabled(false);
	        (findViewById(R.id.btn_call_three)).setEnabled(false);
	        (findViewById(R.id.btn_call_four)).setEnabled(false);
	        (findViewById(R.id.btn_call_five)).setEnabled(false);
	        (findViewById(R.id.btn_call_six)).setEnabled(false);
	        (findViewById(R.id.btn_call_seven)).setEnabled(false);
	        (findViewById(R.id.btn_call_eight)).setEnabled(false);
	        (findViewById(R.id.btn_call_nine)).setEnabled(false);
	        (findViewById(R.id.btn_call_asterisk)).setEnabled(false);
	        (findViewById(R.id.btn_call_pound)).setEnabled(false);
	        (findViewById(R.id.btn_del_num)).setEnabled(false);
			(findViewById(R.id.call_phone_book)).setEnabled(false);
        	(findViewById(R.id.call_history)).setEnabled(false);
			(findViewById(R.id.call_machine_name)).setEnabled(true);
			try{
				((TextView)findViewById(R.id.bt_calling_phone_name)).setText(getString(R.string.btisnotconnected));
			}catch(Exception e){
				e.printStackTrace();
			}
			//uiHandler.sendEmptyMessage(SWITCH_TO_PAIR);
		}else{
	        (findViewById(R.id.btn_call_zero)).setEnabled(true);
	        (findViewById(R.id.btn_call_one)).setEnabled(true);
	        (findViewById(R.id.btn_call_two)).setEnabled(true);
	        (findViewById(R.id.btn_call_three)).setEnabled(true);
	        (findViewById(R.id.btn_call_four)).setEnabled(true);
	        (findViewById(R.id.btn_call_five)).setEnabled(true);
	        (findViewById(R.id.btn_call_six)).setEnabled(true);
	        (findViewById(R.id.btn_call_seven)).setEnabled(true);
	        (findViewById(R.id.btn_call_eight)).setEnabled(true);
	        (findViewById(R.id.btn_call_nine)).setEnabled(true);
	        (findViewById(R.id.btn_call_asterisk)).setEnabled(true);
	        (findViewById(R.id.btn_call_pound)).setEnabled(true);
	        (findViewById(R.id.btn_del_num)).setEnabled(true);
			(findViewById(R.id.call_phone_book)).setEnabled(true);
        	(findViewById(R.id.call_history)).setEnabled(true);
			(findViewById(R.id.call_machine_name)).setEnabled(true);
			try{
				((TextView)findViewById(R.id.bt_calling_phone_name)).setText(getString(R.string.str_device_name,Bluetooth.getInstance().getConnectedHFPDevice().getName()));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onPause() {
		L.v("lifecycle " + this.getClass()  + " : onPause");
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
		isPowerOff = false;
		sendOrderedBroadcast(new Intent("action.bt.dialphoneview.off"), null);
		unregisterReceiver(otherReceiver);
		unregisterReceiver(homeReceiver);
		super.onPause();
	}

	@Override
	protected void onStop() {
		L.v("lifecycle " + this.getClass()  + " : onStop");
		sendBroadcast(new Intent(McuExternalConstant.ACTION_BT_IS_IDLE));
		super.onStop();
      
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      L.e("keycode= " + String.valueOf(keyCode));
        if (keyCode == KeyEvent.KEYCODE_YECON_PHONE_OFF) { // K_PHONE_OFF
        	if(!Bluetooth.getInstance().iscallidle()){
        		Bluetooth.getInstance().terminatecall();
        	}
        } else if (keyCode == KeyEvent.KEYCODE_YECON_PHONE_ON) { // K_PHONE_ON K_BLUETOOTH
        	if(Bluetooth.getInstance().isincoming()){
        		Bluetooth.getInstance().answercall();
        	}else if(Bluetooth.getInstance().iscallidle()){
        		if (!minputstr.isEmpty()) {
                	Bluetooth.getInstance().call(minputstr);
        		}else{
        			minputstr = Bluetooth.getInstance().getlastcallnum();
        			formatshownum();
        			if (!minputstr.isEmpty()) {
                    	Bluetooth.getInstance().call(minputstr);
            		}
        		}
        	}else if(Bluetooth.getInstance().isspeaking()){
				mCallVoiceOnPhone = !mCallVoiceOnPhone;
				Bluetooth.getInstance().switchcallaudio(mCallVoiceOnPhone);
			}
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override
	protected void onDestroy() {
		L.v("lifecycle " + this.getClass()  + " : onDestroy");
		sendBroadcast(new Intent(McuExternalConstant.ACTION_BT_IS_IDLE));
		SourceManager.unregisterSourceListener(sourceTocken);
		BluetoothReceiver.unregisterNotifyHandler(uiHandler);
		MainApp.removeActivity(this);
		super.onDestroy();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.onTouchEvent(event);
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.dispatchTouchEvent(ev);
	}
	
	
	@Override
	public void onBackPressed() {
		//MainApp.exitAllActivity();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		        intent.addCategory(Intent.CATEGORY_HOME);
		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		        startActivity(intent);
	}
	private void initView(){
        m_callnumber_et = (TextView)findViewById(R.id.text_call_info);
        m_callnumber_et.setText(minputstr);
        m_callnumber_et.addTextChangedListener(textViewWatcher);
        callBtn = (findViewById(R.id.btn_call));
        handUpBtn = (findViewById(R.id.btn_hangup));
        callBtn.setOnClickListener(this);
        handUpBtn.setOnClickListener(this);
        dialogCallingView = findViewById(R.id.dialog_calling);
        dialogIncomeView = findViewById(R.id.dialog_income_call);
        callingNameTextView = (TextView)findViewById(R.id.calling_name);
        callingNumTextView = (TextView)findViewById(R.id.calling_num);
        mIncomeNameTextView = (TextView)findViewById(R.id.call_income_name);
        mIncomeNumTextView = (TextView)findViewById(R.id.call_income_num);
        m_phonetimer = (TextView)findViewById(R.id.bt_calling_phone_time);
        (findViewById(R.id.btn_call_zero)).setOnClickListener(this);
        (findViewById(R.id.btn_call_one)).setOnClickListener(this);
        (findViewById(R.id.btn_call_two)).setOnClickListener(this);
        (findViewById(R.id.btn_call_three)).setOnClickListener(this);
        (findViewById(R.id.btn_call_four)).setOnClickListener(this);
        (findViewById(R.id.btn_call_five)).setOnClickListener(this);
        (findViewById(R.id.btn_call_six)).setOnClickListener(this);
        (findViewById(R.id.btn_call_seven)).setOnClickListener(this);
        (findViewById(R.id.btn_call_eight)).setOnClickListener(this);
        (findViewById(R.id.btn_call_nine)).setOnClickListener(this);
        (findViewById(R.id.btn_call_asterisk)).setOnClickListener(this);
        (findViewById(R.id.btn_call_pound)).setOnClickListener(this);
        (findViewById(R.id.btn_del_num)).setOnClickListener(this);
        (findViewById(R.id.btn_del_num)).setOnLongClickListener(this);
        (findViewById(R.id.btn_add)).setOnClickListener(this);
        (findViewById(R.id.bluetooth_pairing)).setOnClickListener(this);
        (findViewById(R.id.btn_call_back)).setOnClickListener(this);
        (findViewById(R.id.call_phone_book)).setOnClickListener(this);
        (findViewById(R.id.call_history)).setOnClickListener(this);
        (findViewById(R.id.call_bt_music)).setOnClickListener(this);
		(findViewById(R.id.call_machine_name)).setOnClickListener(this);
        (findViewById(R.id.calling_hangup)).setOnClickListener(this);
        (findViewById(R.id.call_income_answer)).setOnClickListener(this);
        (findViewById(R.id.call_income_hangup)).setOnClickListener(this);
	}
	
	@Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_del_num) {
            deleteAllDialPadString();
            return true;
        }
        return false;
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.btn_call_back:
        	isBTTop = false;
        	onBackPressed();
        	break;
        case R.id.btn_call:
        	onCall();
            break;
        case R.id.btn_call_zero:
            addDialPadInputString("0");
            break;
        case R.id.btn_add:
        	addDialPadInputString("+");
        	break;
        case R.id.btn_call_one:
            addDialPadInputString("1");
            break;
        case R.id.btn_call_two:
            addDialPadInputString("2");
            break;
        case R.id.btn_call_three:
            addDialPadInputString("3");
            break;
        case R.id.btn_call_four:
            addDialPadInputString("4");
            break;
        case R.id.btn_call_five:
            addDialPadInputString("5");
            break;
        case R.id.btn_call_six:    
            addDialPadInputString("6");
            break;
        case R.id.btn_call_seven:
            addDialPadInputString("7");
            break;
        case R.id.btn_call_eight:
            addDialPadInputString("8");
            break;
        case R.id.btn_call_nine:    
            addDialPadInputString("9");
            break;
        case R.id.btn_call_asterisk:
            addDialPadInputString("*");
            break;
        case R.id.btn_call_pound:    
            addDialPadInputString("#");
            break;
        case R.id.btn_del_num:
            deleteOneDialPadString();
            break;
			
		case R.id.call_machine_name:
			{
	        	Intent intent = new Intent(this,MainBluetoothActivity.class);
	        	intent.putExtra("openFragmentOrder", BluetoothConstants.START_FRAGMENT_PAIR);
				intent.putExtra("rename",true);
				intent.putExtra("startTag",0);
	        	startActivity(intent);
			}
			break;
        case R.id.bluetooth_pairing:{
				//FIXME:11149 +
			    if(Bluetooth.getInstance().iscallidle()){
					Intent intent = new Intent(this,MainBluetoothActivity.class);
					intent.putExtra("openFragmentOrder", BluetoothConstants.START_FRAGMENT_PAIR);
					intent.putExtra("godial", false);
					intent.putExtra("startTag",0);
	        		startActivity(intent);
				}
	        }
        	break;
        case R.id.call_bt_music:{
	        	Intent intent = new Intent(this,MainBluetoothActivity.class);
	        	intent.putExtra("openFragmentOrder", BluetoothConstants.START_FRAGMENT_BT_MUSIC);
				intent.putExtra("godial", false);
	        	startActivity(intent);
	        	finish();
	        }
        	break;
        case R.id.call_phone_book:{
				Intent intent = new Intent(this,MainBluetoothActivity.class);
				intent.putExtra("openFragmentOrder", BluetoothConstants.START_FRAGMENT_PHONEBOOK);
				intent.putExtra("startTag",0);
				intent.putExtra("godial", false);
				startActivity(intent);
	        }
        	
        	break;
        case R.id.call_history:{
	        	Intent intent = new Intent(this,MainBluetoothActivity.class);
	        	intent.putExtra("openFragmentOrder", BluetoothConstants.START_FRAGMENT_CALL_LOG);
				intent.putExtra("startTag",0);
				intent.putExtra("godial", false);
	        	startActivity(intent);
        	}
        	break;
        case R.id.call_income_hangup:
        case R.id.calling_hangup:
        case R.id.btn_hangup:{
        	Bluetooth.getInstance().terminatecall();
        	}
        	break;
        case R.id.call_income_answer: 
        	Bluetooth.getInstance().answercall();
            stopAutoAnswerTimeoutThread();
            break;
        default:
            break;
        }
	}
	public void onCall(){
        if (!minputstr.isEmpty()) {
        	Bluetooth.getInstance().call(minputstr);
		}else{
			minputstr = Bluetooth.getInstance().getlastcallnum();
			formatshownum();
		}
    }
	
	private boolean addDialPadInputString(CharSequence str) {
        if(str == null)
            return false;
        
        if(Bluetooth.getInstance().isspeaking()){
        	L.e("SendDTMFCode:" + str);
        	Bluetooth.getInstance().SendDTMFCode(str + "");
        }
        
        if(minputstr.length() > MAXPHONENUM){
        	CmnUtil.showToast(this,  R.string.str_call_number_is_too_long);
            return false;
    	}
        minputstr += str;
        formatshownum();
        return true;
    }

    private boolean deleteAllDialPadString() {
    	minputstr = "";
        m_callnumber_et.setText(minputstr);
        return true ;
    }
    
    private boolean deleteOneDialPadString() {
    	if (minputstr.isEmpty()) {
			return true;
		}
    	minputstr = minputstr.substring(0, minputstr.length() - 1);
    	formatshownum();
        return true ;
    }
    private void formatshownum(){
        String callnum_afterformat 	= FormatTelNumber.ui_format_tel_number(minputstr);
        m_callnumber_et.setText(callnum_afterformat);
    }
    
    private void checkAutoAnswerOption(){
        if(Bluetooth.getInstance().isincoming()){
            if(Bluetooth.getInstance().isautoanswer()){
                m_thrdAutoAnswerTimeout = new autoAnswerTimeoutThread();
                if(m_thrdAutoAnswerTimeout!= null){
                    m_thrdAutoAnswerTimeout.start();
                }
            }
        }
        return ;
    }
    
    //判断上一个进入拨号的应用界面是哪一个应用
    private void checkPrevPackage(){
    	List<RunningTaskInfo> tasks =  mActivityManager.getRunningTasks(10);
    	String appPackageName = getApplicationContext().getApplicationInfo().packageName;
    	if(tasks.size() > 2){
    		if(appPackageName.equals(tasks.get(1).topActivity.getPackageName())){
    			isBTTop = true;
    		}else{
    			isBTTop = false;
    		}
    	}
    }
    //判断是否退出
    private void decideActivityToFinish(){
    	if(isBTTop && isHandStart){
    		L.e("decideActivityToFinish nothing");
    	}
    	else{
    		L.e("decideActivityToFinish finish!");
    		finish();
    	}
    }
    
    
    private void stopAutoAnswerTimeoutThread(){
        if (m_thrdAutoAnswerTimeout != null) {
            try {
            	L.e(this.getClass().getName(),"mConnectTimeout close.");
                m_thrdAutoAnswerTimeout.shutdown();
                m_thrdAutoAnswerTimeout.join();
                m_thrdAutoAnswerTimeout = null;
            } catch (InterruptedException e) {
            }
        }
    }
    
    private void startspeaktimer(){
        mStartTimer = 0;
        if(m_MyTimerTask!=null){
        	m_MyTimerTask.cancel();
        	m_MyTimerTask=null;
        }
        m_MyTimerTask = new MyTimerTask(); 
        Timer timer = new Timer(); 
        timer.scheduleAtFixedRate(m_MyTimerTask, 0, 1000);
    }
    
    public String millSeconds2readableTime(int millseconds){

        StringBuffer dateBf = new StringBuffer();
        int totalSeconds = millseconds ; 

        // HOUR_OF_DAY:24Hour 
        int hour = (totalSeconds / 60) / 60;
        if(hour <= 9){
            dateBf.append("0").append(hour+":");
        }else{
            dateBf.append(hour+":");
        }
        // Minute   
        int minute = (totalSeconds / 60) % 60;
        if (minute<=9) {
            dateBf.append("0").append(minute+":");
        }else {
            dateBf.append(minute+":");
        }
        // Seconds   
        int second = totalSeconds % 60;
        if (second<=9) {
            dateBf.append("0").append(second);
        }else {
            dateBf.append(second);
        }
        return dateBf.toString();    

    }
    
    private void updateuiforcallstate(){
        int isMute = AtcSettings.Audio.GetMicMute(0);
//		m_micmute_cb.setChecked(isMute == 1);
		if (Bluetooth.getInstance().isspeaking()) {
			callBtn.setVisibility(View.GONE);
			handUpBtn.setVisibility(View.VISIBLE);
			dialogCallingView.setVisibility(View.GONE);
			dialogIncomeView.setVisibility(View.GONE);
			m_phonetimer.setVisibility(View.VISIBLE);
			setBackAble(false);
			if(TextUtils.isEmpty(m_callnumber_et.getText())){
				m_callnumber_et.setText(Bluetooth.getInstance().GetCallingNum());
			}
		}else if (Bluetooth.getInstance().isincoming()) {
			callBtn.setVisibility(View.GONE);
			handUpBtn.setVisibility(View.VISIBLE);
			dialogIncomeView.setVisibility(View.VISIBLE);
			dialogIncomeView.setOnClickListener(this);
			m_callnumber_et.setText(Bluetooth.getInstance().GetCallingNum());
			m_phonetimer.setVisibility(View.INVISIBLE);
			setBackAble(false);
		}else if (Bluetooth.getInstance().isoutgoing()) {
			callBtn.setVisibility(View.GONE);
			handUpBtn.setVisibility(View.VISIBLE);
			dialogCallingView.setVisibility(View.VISIBLE);
			dialogCallingView.setOnClickListener(this);
			m_callnumber_et.setText(Bluetooth.getInstance().GetCallingNum());
			m_phonetimer.setVisibility(View.INVISIBLE);
			setBackAble(false);
		}else if(Bluetooth.getInstance().iscallidle()){
			callBtn.setVisibility(View.VISIBLE);
			handUpBtn.setVisibility(View.GONE);
			m_phonetimer.setVisibility(View.INVISIBLE);
			dialogCallingView.setVisibility(View.GONE);
			dialogIncomeView.setVisibility(View.GONE);
			m_callnumber_et.setText("");
			mIncomeNameTextView.setText("");
			minputstr = "";
	        if(m_MyTimerTask != null){
	        	m_MyTimerTask.cancel();
	        	m_MyTimerTask = null;
	        }
			//FIXME:11138 -  decideActivityToFinish();
			setBackAble(true);
		}
		updatePhoneNumberDisplay();
    }
    
    private void updatePhoneNumberDisplay(){
    	String strPhoneNumber = Bluetooth.getInstance().GetCallingNum();
        if(strPhoneNumber == null)
            return ;
        if(strPhoneNumber.isEmpty()){
            return ;
        }
        String callnum_afterformat 	= FormatTelNumber.ui_format_tel_number(strPhoneNumber);
        String name = Bluetooth.getInstance().GetCallingName();
        if (name == null || TextUtils.isEmpty(name) || name.equals(strPhoneNumber)) {
        	callingNameTextView.setText(getResources().getString(R.string.call_name_is_null));
        	callingNumTextView.setText(callnum_afterformat);
        	
        	mIncomeNameTextView.setText(getResources().getString(R.string.call_name_is_null));
        	mIncomeNumTextView.setText(callnum_afterformat);
		}else{
			callingNameTextView.setText(name);
			callingNumTextView.setText(callnum_afterformat);
			
			mIncomeNameTextView.setText(name);
			mIncomeNumTextView.setText(callnum_afterformat);
		}   
        String telzone = MainBluetoothActivity.mdbmanager.GetTelZone(strPhoneNumber);
        if (!telzone.isEmpty()) {
//            m_phoneNumberArea_Tv.setText(telzone);
		}else{
//            m_phoneNumberArea_Tv.setText(getResources().getString(R.string.unknownzone));
		}
    }
    
    private void updatecallingnumandname(){
    	String num = Bluetooth.getInstance().GetCallingNum();
    	if (num == null || num.isEmpty()) {
			return;
		}
        m_strPhoneNum = num;
        m_strPhoneName = Bluetooth.getInstance().GetCallingName();
    }
    
    private void resetComeIn() {
        SystemProperties.set(PROPERTY_KEY_BTPHONE_STARTUP, "true");
    }
    
    private void resetBlackout() {
        SystemProperties.set(PROPERTY_KEY_BTPHONE_STARTUP, "false");
    }
    
    private void setBackAble(boolean isBackAble){
    	findViewById(R.id.btn_call_back).setEnabled(isBackAble);
    	findViewById(R.id.call_bt_music).setEnabled(isBackAble);
    	findViewById(R.id.top_ic_return_home).setEnabled(isBackAble);
    	if(isBackAble){
    		sendBroadcast(new Intent(McuExternalConstant.ACTION_BT_IS_IDLE));
    	}else{
    		sendBroadcast(new Intent(McuExternalConstant.ACTION_BT_IS_CALLING));
    	}
    }
	public static int i = 0;
    private TextWatcher textViewWatcher = new TextWatcher() {
        private String temp;
        private int editStart ;
        private int editEnd ;
        private int length;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s.toString().trim();
            length = temp.length();
            L.w("m_callnumber_et Text:" + s + " start:" + start + " before:" + before + " count:" + count + " length:" + length + "   I : " + i++);
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = m_callnumber_et.getSelectionStart();
            editEnd = m_callnumber_et.getSelectionEnd();
            if(length > 0){
            	callBtn.setEnabled(true);
            }else{
            	callBtn.setEnabled(false);
            }
        }
    };
    
    
    
    private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == SWITCH_TO_PAIR){
				findViewById(R.id.bluetooth_pairing).performClick();
			}
			else if (msg.what == BluetoothReceiver.MSG_BT_STATUS_NOTIFY) {
				Intent intent = (Intent) msg.obj;
				String action = intent.getAction();
				if (action.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)) {
					BTProfile profilename = (BTProfile) intent.getSerializableExtra(LocalBluetoothProfileManager.EXTRA_PROFILE);
					if (profilename == null)
						return;
					if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)) {
				        int profilestate = intent.getIntExtra(LocalBluetoothProfileManager.EXTRA_NEW_STATE,LocalBluetoothProfileManager.STATE_DISCONNECTED);
				        if(profilestate == LocalBluetoothProfileManager.STATE_DISCONNECTED){ //蓝牙断开
				        	deleteAllDialPadString();
				        	setBackAble(true);
							Log.d("TEST","TuoXianDialActivity disconnected");
					        if(m_MyTimerTask!=null){
					        	m_MyTimerTask.cancel();
					        	m_MyTimerTask=null;
					        }
							findViewById(R.id.bluetooth_pairing).performClick();
				        	//finish();
				        }else if(profilestate == LocalBluetoothProfileManager.STATE_CONNECTED){
                            L.e(this.getClass().getName(), "hf CONNECTED");
                        }
					}
				}else if(action.equals(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE)){
		            int phoneCallState = intent.getIntExtra(BluetoothHfUtility.EXTRA_CALL_STATE, 0);
	                if(phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING ||
	                    phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING){
	                	updateuiforcallstate();
	                	resetComeIn();
						Log.d("TEST","BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING");
        call_state_change_intent.putExtra(BluetoothConstants.ACTION_BLUETOOTH_CALL_STATUS, 
                PhoneCallActivity.CALL_STATE_START);
		            }else if(phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING){
						mCallVoiceOnPhone = false;
		            	startspeaktimer();
		            	updatecallingnumandname();  	
		            	updateuiforcallstate();
		            	resetComeIn();
						Log.d("TEST","BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING");
        call_state_change_intent.putExtra(BluetoothConstants.ACTION_BLUETOOTH_CALL_STATUS, 
                PhoneCallActivity.CALL_STATE_START);
		            }else if(phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_IDLE){
						Log.d("TEST","BluetoothHfUtility.HFP_UTILITY_CALLSTATE_IDLE");
		                call_state_change_intent.putExtra(BluetoothConstants.ACTION_BLUETOOTH_CALL_STATUS, 
		                        PhoneCallActivity.CALL_STATE_FINISH);
		            	resetBlackout();
		            	updateuiforcallstate();
		            	//FIXME:11138 +
						decideActivityToFinish();
		            }
		            else {
		            	/*resetBlackout();*/
						Log.d("TEST","BluetoothHfUtility.HFP_UTILITY_CALLSTATE else");
		                call_state_change_intent.putExtra(BluetoothConstants.ACTION_BLUETOOTH_CALL_STATUS, 
		                        PhoneCallActivity.CALL_STATE_FINISH);
		            	resetBlackout();
		            	deleteAllDialPadString();
	                }
        sendBroadcast(call_state_change_intent);
	            }
				//呼叫设置
				else if (action.equals(BluetoothHfAtHandler.ACTION_CALLSETUP_CHANGED)) {
					int mBtCallsetupState = intent.getIntExtra(BluetoothHfAtHandler.EXTRA_NEW_CALLSETUP_STATE, 0);
                    if( mBtCallsetupState == 0){
                    }else if(mBtCallsetupState == 1){
                        
                    }else if(mBtCallsetupState == 2){
                        
                    }else if(mBtCallsetupState == 3){
                        
                    }
	            }
				
				else if (action.equals(BluetoothHfService.ACTION_SCO_STATE_CHANGED)) {
					int ScoState = intent.getIntExtra(BluetoothHfService.EXTRA_NEW_SCO_STATE, 0);
					//呼叫转移 0为转移到手机  其它则为车机
	            }
				
				else if (action.equals(BluetoothHfAtHandler.ACTION_AG_VOLUME_CHANGED)) {
	            	
	            }
				
	            else if (action.equals(BluetoothHfAtHandler.ACTION_AG_MIC_GAIN_CHANGED)) {
	            	
	            }
	            //电源off
	            else if (action.equals(ACTION_QB_POWEROFF)) {
	            	finish();
	            }
	            
	            else if (action.equals(BluetoothHfAtHandler.ACTION_PHONE_NUMBER_CHANGED)) {
	            	String num = intent.getStringExtra(BluetoothHfAtHandler.EXTRA_NEW_PHONE_NUMBER);
	            	if (num != null && !num.isEmpty()) {
						m_strPhoneNum = num;
						m_strPhoneName = Bluetooth.getInstance().GetCallingName(num);
						updatePhoneNumberDisplay();
					}
	            }
	            else if(action.equals(BluetoothAvrcpCtPlayerManage.ACTION_PLAYBACK_DATA_UPDATE)){
	            	try {
	            		byte play_status = intent.getByteExtra(BluetoothAvrcpCtPlayerManage.PLAYBACK_STATUS, (byte) 0);
	            		switch (play_status) {
							case BluetoothAvrcpCtPlayerManage.PLAYING:{
								isReturnMusicWindow = true;
								L.e("BluetoothAvrcpCtPlayerManage.PLAYING");
							}
							break;
							case BluetoothAvrcpCtPlayerManage.PAUSED:{
								isReturnMusicWindow = false;
								L.e("BluetoothAvrcpCtPlayerManage.PAUSED");							
							}
							break;
							case BluetoothAvrcpCtPlayerManage.STOPPED:{
								L.e("BluetoothAvrcpCtPlayerManage.STOPPED");
								isReturnMusicWindow = false;
							}
							break;
						}
	            	} catch (Exception e) {
						L.e(e.toString());
					}
	            }
			}
		}
	};
	
	Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 300) {
           		String position = millSeconds2readableTime(mStartTimer);
           		m_phonetimer.setText(getResources().getString(R.string.speak_time,position));
        		m_phonetimer.invalidate();
            }
        }
    };
	
    BroadcastReceiver otherReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e("recv " + intent.getAction());
			//sd卡相关
			if(DeviceStateMode.SHARED_PREFERENCES_NAME.equals(intent.getAction()) ){
				int state = intent.getIntExtra(DeviceStateMode.USB_STATE, -1);
				if(state == 0){
				}else if(state == 2){
					if(Bluetooth.getInstance().iscallidle()){
//						onBackPressed();
					}
				}
			}else if("action.bt.return.prev.window".equals(intent.getAction())){
				findViewById(R.id.call_bt_music).callOnClick();
			}/*FIXME:11101 else if("com.action.storage.sd_mount".equals(intent.getAction()) || "com.action.storage.usb_mount".equals(intent.getAction())){
				if(Bluetooth.getInstance().iscallidle()){
					onBackPressed();
				}
			}*/else if(YeconConstants.ACTION_QB_PREPOWEROFF.equals(intent.getAction())){
        	    isPowerOff = true;
	        }else if("action.hzh.media.power.on".equals(intent.getAction())){
	        	isPowerOff = false;
	        }
		}
	};
    
	 private BroadcastReceiver homeReceiver = new BroadcastReceiver() {
	      @Override
	      public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	       if (action.equals("android.intent.action.homekey_down")){
	    	   if(Bluetooth.getInstance().iscallidle()){
					onBackPressed();
				}
	         }
	      }
	 };
	
	private class MyTimerTask extends TimerTask 
    { 
    	@Override public void run() 
    	{ 
    		Log.d("MyTimerTask", "Timer Task run!");
    		++mStartTimer ;
 
            Message message = Message.obtain();
            message.what = 300;
            message.obj = 0;
            myHandler.sendMessage(message);
    	} 
    }
	
	private class autoAnswerTimeoutThread extends Thread {
        private    boolean    stoped = false;
        @Override
        public void run() {
            autoAnswertimeout( );
        }
        
        public void shutdown(){
            stoped=true;
        }
        private void autoAnswertimeout(){
            boolean timeout = false;
            int cnt = 0;
            L.e(this.getClass().getName(), "autoAnswer timeout Thread is running");    
            while (!stoped) {
                // wait for 5 seconds
                if (cnt >= 5000) {
                    timeout = true;
                    break;
                }

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                	L.e(this.getClass().getName(), "Waiting for action was interrupted.");
                }
                cnt += 100;            
            }

            if (timeout) {        
            	L.e(this.getClass().getName(), "auto answer time out,answer the call");
                Bluetooth.getInstance().answercall();
            }
        }        
    }
}
