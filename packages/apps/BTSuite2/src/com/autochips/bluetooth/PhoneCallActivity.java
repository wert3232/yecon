
/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 */
/* MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */


package com.autochips.bluetooth;


import static android.mcu.McuExternalConstant.*;
import static android.constant.YeconConstants.*;

import com.autochips.bluetooth.R;
import com.autochips.bluetooth.hf.BluetoothHfUtility;
import com.autochips.bluetooth.hf.BluetoothHfAtHandler;
import com.autochips.bluetooth.hf.BluetoothHfService;
import com.autochips.bluetooth.BtEmulatorDebug;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.Editable;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

import com.autochips.bluetooth.LocalBluetoothProfileManager;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.util.L;
import com.autochips.settings.AtcSettings;
import com.yecon.common.SourceManager;

import android.media.AudioManager;
import android.app.StatusBarManager;
import android.os.Handler;
import android.os.Message;

public class PhoneCallActivity extends Activity implements OnClickListener, OnCheckedChangeListener{
    private static final String TAG = "PhoneCallActivity";
    private static boolean DEBUG = BtEmulatorDebug.mDebugAll & BtEmulatorDebug.mDebugHF;
    
    private EditText m_subcallNumber_Et;
    private Editable m_subcallNumstr_Edt;
    private Button m_hungup_Btn;
    private Button m_answer_Btn;
    private Button m_showpad_Btn;
    private CheckBox m_switch_cb;
    private CheckBox m_micmute_cb;
    private TextView m_phoneNumberArea_Tv;
    private TextView m_phoneNumber_Tv;
    private TextView m_phoneName_Tv;
    private TextView m_phonetimer;
    private ProgressBar m_Calloutimg;
    private ProgressBar m_Callinimg;
    private TextView m_callstatus_TV;

    private String m_strPhoneNum = "";
    private String m_strPhoneName = "";
    private Time m_callStartTime;

    private boolean m_bIsIncomingCall = false;
    private boolean m_bIsCallActive = false;
    private  boolean m_bIsSoftkeyPadVisible   = false;
    private StatusBarManager mStatusBarManager;
    private autoAnswerTimeoutThread m_thrdAutoAnswerTimeout = null;
    
    public static final String EXTRA_CALL_TYPE = 
        "com.autochips.bluetooth.PhoneCallActivity.extra.CALL_TYPE";
    
    public static final String EXTRA_CALL_NAME = 
        "com.autochips.bluetooth.PhoneCallActivity.extra.CALL_NAME";
    
    public static final String EXTRA_CALL_NUMBER = 
        "com.autochips.bluetooth.PhoneCallActivity.extra.CALL_NUMBER";
    
    public static final String EXTRA_CALL_TIME = 
        "com.autochips.bluetooth.PhoneCallActivity.extra.CALL_TIME";
    
    public static final int TYPE_OUTGOING = 0;
    public static final int TYPE_INCOMING = 1;
    public static final int TYPE_MISSED = 2;
    public static final int DIALOG_WIDTH = 450;
    public static final int DIALOG_HEIGHT = 520;
    
    public static final int CALL_STATE_START    = 1;
    public static final int CALL_STATE_FINISH   = 0;
    
    public static int mStartTimer = 0;

    Intent call_state_change_intent = new Intent(BluetoothConstants.ACTION_BLUETOOTH_CALL_STATUS_CHANGE);
    Intent call_type_intent = new Intent(BluetoothConstants.ACTION_BLUETOOTH_NEW_CALL);

    MyTimerTask m_MyTimerTask = null;

    private void updateuiforcallstate(){
        int isMute = AtcSettings.Audio.GetMicMute(0);
		m_micmute_cb.setChecked(isMute == 1);
		if (Bluetooth.getInstance().isspeaking()) {
			m_showpad_Btn.setEnabled(true);
			m_answer_Btn.setEnabled(false);
			m_callstatus_TV.setText(R.string.str_bt_call_active);
	        m_phonetimer.setVisibility(View.VISIBLE);
	        m_Calloutimg.setVisibility(View.GONE);
	        m_Callinimg.setVisibility(View.GONE);
		}else if (Bluetooth.getInstance().isincoming()) {
			m_answer_Btn.setEnabled(true);
			m_showpad_Btn.setEnabled(false);
			m_callstatus_TV.setText(R.string.str_income_call_status);
	        m_phonetimer.setVisibility(View.GONE);
	        m_Calloutimg.setVisibility(View.GONE);
	        m_Callinimg.setVisibility(View.VISIBLE);
		}else if (Bluetooth.getInstance().isoutgoing()) {
			m_answer_Btn.setEnabled(false);
			m_showpad_Btn.setEnabled(false);
			m_callstatus_TV.setText(R.string.str_out_call_status);
	        m_phonetimer.setVisibility(View.GONE);
	        m_Calloutimg.setVisibility(View.VISIBLE);
	        m_Callinimg.setVisibility(View.GONE);
		}
		updatePhoneNumberDisplay();
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
    
    private Object sourceTocken = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.v("lifecycle " + this.getClass()  + " : onCreate");
    	if (DEBUG) Log.d(TAG, "+++ onCreate +++");
        super.onCreate(savedInstanceState);
        sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.bt_phone);
        SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify(){

			@Override
			public void onAudioFocusChange(int arg0) {
				// TODO Auto-generated method stub
				//do nothing
			}
        	
        });
        SourceManager.acquireSource(sourceTocken, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        
        setContentView(R.layout.bt_calling_status);

        m_showpad_Btn = (Button)findViewById(R.id.btn_softkeypad);
        m_phonetimer = (TextView)findViewById(R.id.bt_calling_phone_time);
        m_Calloutimg = (ProgressBar)findViewById(R.id.bt_callout_image);
        m_Calloutimg.setIndeterminate(false);
        m_Callinimg = (ProgressBar)findViewById(R.id.bt_callin_image);
        m_Callinimg.setIndeterminate(false);
        m_callStartTime = new Time(); 
        m_callStartTime.setToNow();
        updatecallingnumandname();
        
        m_answer_Btn = (Button)findViewById(R.id.btn_answer);
        m_hungup_Btn = (Button)findViewById(R.id.btn_hangup);
        m_switch_cb = ((CheckBox) findViewById(R.id.btn_switch));
    	m_phoneNumberArea_Tv = (TextView)findViewById(R.id.bt_calling_phone_area);
        m_phoneNumber_Tv = (TextView)findViewById(R.id.bt_calling_phone_number);
        m_phoneName_Tv = (TextView)findViewById(R.id.bt_calling_phone_name);
        m_subcallNumber_Et = (EditText)findViewById(R.id.calling_input_et);
        m_callstatus_TV = (TextView)findViewById(R.id.bt_calling_status_tv);
        
        m_subcallNumber_Et.setInputType(InputType.TYPE_NULL);
        m_subcallNumber_Et.setText("");
        m_micmute_cb = (CheckBox) findViewById(R.id.cb_voiceswitch_forever);
        
		MainApp.addActivity(this);        
        setVolumeControlStream(AudioManager.STREAM_BLUETOOTH_SCO);
 //       Window window = getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
 //       params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
 //       window.setAttributes(params);

        m_micmute_cb.setOnCheckedChangeListener(this);
        m_switch_cb.setOnCheckedChangeListener(switchlistener);
        m_answer_Btn.setOnClickListener(this);
        ((Button) findViewById(R.id.btn_hangup)).setOnClickListener(this);    
        ((Button) findViewById(R.id.btn_softkeypad)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_zero)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_one)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_two)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_three)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_four)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_five)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_six)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_seven)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_eight)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_nine)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_asterisk)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_calling_pound)).setOnClickListener(this);
        ((Button) findViewById(R.id.delete_calling_pad)).setOnClickListener(this);
        
        mStatusBarManager = (StatusBarManager)getSystemService(Context.STATUS_BAR_SERVICE);
        if (mStatusBarManager != null) {
            Log.d(TAG, " Disable StatusBarManager.DISABLE_EXPAND");
            mStatusBarManager.disable(StatusBarManager.DISABLE_EXPAND);
        } else {
            Log.d(TAG, " get STATUS_BAR_SERVICE fail!");
        }
        updateuiforcallstate();
        checkAutoAnswerOption();
        BluetoothReceiver.registerNotifyHandler(uiHandler);
        if (Bluetooth.getInstance().iscallidle()) {
        	finish();
            return;
        }
        if (getIntent().getBooleanExtra("isspeaking", false)) {
			startspeaktimer();
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
    OnCheckedChangeListener switchlistener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			Bluetooth.getInstance().switchcallaudio(arg1);
		}
	};
    @Override
    protected void onResume() {
        L.v("lifecycle " + this.getClass()  + " : onResume");
        super.onResume();
        SourceManager.acquireSource(sourceTocken, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        
        CmnUtil.cancelToast();
        if (true)
            Log.d(TAG, "+++ onResume +++");
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        L.v("lifecycle " + this.getClass()  + " : onStart");
        if (DEBUG)Log.d(TAG, "===== onStart =====");
        call_state_change_intent.putExtra(BluetoothConstants.ACTION_BLUETOOTH_CALL_STATUS, 
                PhoneCallActivity.CALL_STATE_START);
        sendBroadcast(call_state_change_intent);

        SystemProperties.set(PROPERTY_KEY_BTPHONE_STARTUP, "true");
    }
    @Override
    protected void onPause() {
        L.v("lifecycle " + this.getClass()  + " : onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        L.v("lifecycle " + this.getClass()  + " : onDestroy");
    	BluetoothReceiver.unregisterNotifyHandler(uiHandler);
        if(m_MyTimerTask != null){
        	m_MyTimerTask.cancel();
        	m_MyTimerTask=null;
        }
        sendBroadcast(call_state_change_intent);
        notifyUpdateCallHistory();
        
        if (mStatusBarManager != null) {
            Log.d(TAG, " Disable StatusBarManager.DISABLE_NONE");
            mStatusBarManager.disable(StatusBarManager.DISABLE_NONE);
        } else {
            Log.d(TAG, " get STATUS_BAR_SERVICE fail!");
        }
        MainApp.removeActivity(this);
        SourceManager.unregisterSourceListener(sourceTocken);
        super.onDestroy();        
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       // return super.onTouchEvent( event);
        return true;

    }
    @Override
    public void onBackPressed(){
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
            if(DEBUG) Log.e(TAG, "autoAnswer timeout Thread is running");    
            while (!stoped) {
                // wait for 5 seconds
                if (cnt >= 5000) {
                    timeout = true;
                    break;
                }

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    if(DEBUG) Log.e(TAG, "Waiting for action was interrupted.");
                }
                cnt += 100;            
            }

            if (timeout) {        
                if(DEBUG)Log.w(TAG, "auto answer time out,answer the call");
                Bluetooth.getInstance().answercall();
            }
        }        
    }
    
    private void stopAutoAnswerTimeoutThread(){
        if (m_thrdAutoAnswerTimeout != null) {
            try {
                if(DEBUG)Log.i(TAG,"mConnectTimeout close.");
                m_thrdAutoAnswerTimeout.shutdown();
                m_thrdAutoAnswerTimeout.join();
                m_thrdAutoAnswerTimeout = null;
            } catch (InterruptedException e) {
            }
        }
    }

   private void notifyUpdateCallHistory(){
	   if (!Bluetooth.getInstance().iscompletecall()) {
		   Bluetooth.getInstance().initisspeakingfirst();
			return;
	   }
	   Bluetooth.getInstance().initisspeakingfirst();
        int call_type = 0 ;
        String call_name = m_strPhoneName;
        String call_number = m_strPhoneNum;
        String call_time = null ;
        if (m_callStartTime == null || m_strPhoneName == null|| m_strPhoneNum == null) {
            return;
        }

        /*call time format is  2012/03/05 18:32:05   */
        call_time = String.valueOf(m_callStartTime.year) 
            +"/"
            + String.valueOf(m_callStartTime.month + 1) 
            +"/"
            + String.valueOf(m_callStartTime.monthDay)
            +" ";   
        
            call_time +=  String.valueOf(m_callStartTime.hour)
            + ":" 
            + String.valueOf(m_callStartTime.minute)
            + ":" 
            + String.valueOf(m_callStartTime.second);
            
        call_type = Bluetooth.getInstance().getlastcalltype();
//        Bluetooth.getInstance().addnewcall(call_type, call_name, call_number, call_time);
        Bluetooth.getInstance().addrecord(call_type, call_name, call_number, call_time);
        sendBroadcast(call_type_intent);
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
        if (name == null || name.isEmpty() || name.equals(strPhoneNumber)) {
            m_phoneName_Tv.setText(getResources().getString(R.string.unknownname));
		}else{
            m_phoneName_Tv.setText(name);
		}
        m_phoneNumber_Tv.setText(callnum_afterformat);
            
        String telzone = MainBluetoothActivity.mdbmanager.GetTelZone(strPhoneNumber);
        if (!telzone.isEmpty()) {
            m_phoneNumberArea_Tv.setText(telzone);
		}else{
            m_phoneNumberArea_Tv.setText(getResources().getString(R.string.unknownzone));
		}
    }
    
    private void resetBlackout() {
        SystemProperties.set(PROPERTY_KEY_BTPHONE_STARTUP, "false");
    }
    
    /*
     * Function name : BroadcastReceiver
     * Parameters:  
     *                 
     *                 
     * Description : 
     *                 handle received messages
     */    
    
    class MyTimerTask extends TimerTask 
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
    
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 300) {
           		String position = millSeconds2readableTime(mStartTimer);
        		m_phonetimer.setText(position);
        		m_phonetimer.invalidate();
            }
        }
    };
    void startspeaktimer(){
        mStartTimer = 0;
        if(m_MyTimerTask!=null){
        	m_MyTimerTask.cancel();
        	m_MyTimerTask=null;
        }
        m_MyTimerTask = new MyTimerTask(); 
        Timer timer = new Timer(); 
        timer.scheduleAtFixedRate(m_MyTimerTask, 0, 1000);
    }
	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BluetoothReceiver.MSG_BT_STATUS_NOTIFY) {
				Intent intent = (Intent) msg.obj;
	            String action = intent.getAction();
	            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
	            }else if(action.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)){
	            	BTProfile   profile =(BTProfile)intent.getSerializableExtra(LocalBluetoothProfileManager.EXTRA_PROFILE);
	                    if(profile== null){
	                        if(DEBUG) Log.w(TAG, "ACTION_PROFILE_STATE_UPDATE,profilename is null");
	                        return ;
	                    }
	                    if(profile.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)){
	                        int profilestate = intent.getIntExtra(LocalBluetoothProfileManager.EXTRA_NEW_STATE,0);
	                        if(profilestate == LocalBluetoothProfileManager.STATE_DISCONNECTED ){
	                            Toast.makeText(getApplicationContext(),
	                                    getString(R.string.str_handsfree_device_disconnected),
	                                    Toast.LENGTH_SHORT).show();
	                            finish();
	                        }else if(profilestate == LocalBluetoothProfileManager.STATE_CONNECTED){
	                            if(DEBUG) Log.w(TAG, "hf disconnected,exit phonecall activity ");
	                        }
	                    }
	                
	            }else if(action.equals(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE)){
		            int phoneCallState = intent.getIntExtra(BluetoothHfUtility.EXTRA_CALL_STATE, 0);
	                if(phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING ||
	                    phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING){
		                
		            }else if(phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING){
		            	startspeaktimer();
		            	updatecallingnumandname();
		            } else {
		                resetBlackout();
		                call_state_change_intent.putExtra(BluetoothConstants.ACTION_BLUETOOTH_CALL_STATUS, 
		                        PhoneCallActivity.CALL_STATE_FINISH);
		                finish();
	                    return;
	                }
	                updateuiforcallstate();
	            }
	            else if (action.equals(BluetoothHfAtHandler.ACTION_CALLSETUP_CHANGED)) {
	                    int mBtCallsetupState = intent.getIntExtra(BluetoothHfAtHandler.EXTRA_NEW_CALLSETUP_STATE, 0);
	                    if( mBtCallsetupState == 0){
	                    }else if(mBtCallsetupState == 1){
	                        
	                    }else if(mBtCallsetupState == 2){
	                        
	                    }else if(mBtCallsetupState == 3){
	                        
	                    }
	            }else if (action.equals(BluetoothHfService.ACTION_SCO_STATE_CHANGED)) {
	                int ScoState = intent.getIntExtra(BluetoothHfService.EXTRA_NEW_SCO_STATE, 0);
	            	m_switch_cb.setChecked(ScoState == 0);
	            }else if (action.equals(BluetoothHfAtHandler.ACTION_AG_VOLUME_CHANGED)) {
	            }else if (action.equals(BluetoothHfAtHandler.ACTION_AG_MIC_GAIN_CHANGED)) {
	            }else if (action.equals(ACTION_QB_POWEROFF)) {
	            	finish();
	            }else if (action.equals(BluetoothHfAtHandler.ACTION_PHONE_NUMBER_CHANGED)) {
	            	 String num = intent.getStringExtra(BluetoothHfAtHandler.EXTRA_NEW_PHONE_NUMBER);
	            	 if (num != null && !num.isEmpty()) {
						m_strPhoneNum = num;
						m_strPhoneName = Bluetooth.getInstance().GetCallingName(num);
						updatePhoneNumberDisplay();
					}
	            }
	        }
		}
	};
    
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

    public boolean addSubPhoneCallInputString(CharSequence str) {
        
        if(m_subcallNumber_Et ==  null)
            return false ;
        
        int index = m_subcallNumber_Et.getSelectionStart();
        m_subcallNumstr_Edt = m_subcallNumber_Et.getEditableText();

        if(m_subcallNumstr_Edt == null)
            return false ;
        
        if(index < 0 || index >m_subcallNumstr_Edt.length()){
            m_subcallNumstr_Edt.append(str);
        }else {
            m_subcallNumstr_Edt.insert(index ,str);
        }
        m_subcallNumber_Et.setText(m_subcallNumstr_Edt);
        m_subcallNumber_Et.setSelection(index+1);

        return true;
    }
    private void phoneCallShowSoftkeyPad(boolean fgShow){
        final LinearLayout softkeyPadLayout = (LinearLayout)findViewById(R.id.calling_softkeypad);
        final LinearLayout callinginfolayout = (LinearLayout)findViewById(R.id.calling_infopad);
        if(fgShow){
            softkeyPadLayout.setVisibility(View.VISIBLE);
            callinginfolayout.setVisibility(View.VISIBLE);
            m_bIsSoftkeyPadVisible = true;
        }else{
            softkeyPadLayout.setVisibility(View.GONE);
            callinginfolayout.setVisibility(View.VISIBLE);
            m_bIsSoftkeyPadVisible = false ;     
        }
    }
    
    
    private boolean delelteOneCallingPadString() {
        
        int index = m_subcallNumber_Et.getSelectionStart();
        Editable callnumstr_edt = m_subcallNumber_Et.getText();

        if(index >=1){
            callnumstr_edt.delete(index-1,index);
        }else{
            return false ;
        }
        m_subcallNumber_Et.setText(callnumstr_edt);
        m_subcallNumber_Et.setSelection(index-1);
        return true ;
    }
    
    public void onClick(View v) {
        String dtmf_code = null;
        boolean bsenddtmf = false;
        
        // Button Click Listener
        switch (v.getId()) {
        case R.id.btn_hangup:
        	Bluetooth.getInstance().terminatecall();
            break;
            
        case R.id.btn_answer:
        	Bluetooth.getInstance().answercall();
            stopAutoAnswerTimeoutThread();
            break;
            
        case R.id.btn_softkeypad:
            phoneCallShowSoftkeyPad(!m_bIsSoftkeyPadVisible);
            break;    
        /*
        case R.id.btn_switch:
            Bluetooth.getInstance().switchcallaudio();
            break;
                */
        case R.id.btn_calling_zero:
            addSubPhoneCallInputString("0");
            dtmf_code = "0" ;
            bsenddtmf = true ;
            break;
            
        case R.id.btn_calling_one:
            addSubPhoneCallInputString("1");
            dtmf_code = "1" ;
            bsenddtmf = true ;
            break;
            
        case R.id.btn_calling_two:
            addSubPhoneCallInputString("2");
            dtmf_code = "2" ;
            bsenddtmf = true ;
            break;
        
        case R.id.btn_calling_three:
            addSubPhoneCallInputString("3");
            dtmf_code = "3" ;
            bsenddtmf = true ;
            break;    
            
        case R.id.btn_calling_four:
            addSubPhoneCallInputString("4");
            dtmf_code = "4" ;
            bsenddtmf = true ;
            break;
        
        case R.id.btn_calling_five:
            addSubPhoneCallInputString("5");
            dtmf_code = "5" ;
            bsenddtmf = true ;
            break;    
            
        case R.id.btn_calling_six:    
            addSubPhoneCallInputString("6");
            dtmf_code = "6" ;
            bsenddtmf = true ;
            break;
            
        case R.id.btn_calling_seven:
            addSubPhoneCallInputString("7");
            dtmf_code = "7" ;
            bsenddtmf = true ;
            break;
        
        case R.id.btn_calling_eight:
            addSubPhoneCallInputString("8");
            dtmf_code = "8" ;
            bsenddtmf = true ;
            break;    
            
        case R.id.btn_calling_nine:    
            addSubPhoneCallInputString("9");
            dtmf_code = "9" ;
            bsenddtmf = true ;
            break;    
                    
        case R.id.btn_calling_asterisk:
            addSubPhoneCallInputString("*");
            dtmf_code = "*" ;
            bsenddtmf = true ;
            break;    
            
        case R.id.btn_calling_pound:    
            addSubPhoneCallInputString("#");
            dtmf_code = "#" ;
            bsenddtmf = true ;
            break;        
        case R.id.delete_calling_pad:
        	delelteOneCallingPadString();
        default:
            break;
        }
        if(bsenddtmf  && (dtmf_code != null) ){
        	Bluetooth.getInstance().SendDTMFCode(dtmf_code);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "m_bIsIncomingCall: " + m_bIsIncomingCall + " - m_bIsCallActive: " + m_bIsCallActive + "keycode= " + String.valueOf(keyCode));
        if (keyCode == KeyEvent.KEYCODE_YECON_PHONE_OFF) { // K_PHONE_OFF
                m_hungup_Btn.performClick();
        } else if (keyCode == KeyEvent.KEYCODE_YECON_PHONE_ON) { // K_PHONE_ON K_BLUETOOTH
            if (!m_bIsCallActive || m_bIsIncomingCall) {
                m_answer_Btn.performClick();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		AtcSettings.Audio.SetMicMute(arg1);
		if (arg1) {
			m_micmute_cb.setChecked(true);
		}else{
			m_micmute_cb.setChecked(false);
		}
	}
}
