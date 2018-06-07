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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.mcu.McuExternalConstant;
import android.mcu.McuManager;
import android.media.AudioManager;
import static android.mcu.McuExternalConstant.*;
import static android.constant.YeconConstants.*;

import android.text.TextUtils;
import com.autochips.bluetooth.R;
import com.autochips.bluetooth.BtEmulatorDebug;
import com.autochips.bluetooth.Fragment.CallLogFragment;
import com.autochips.bluetooth.Fragment.DialFragment;
import com.autochips.bluetooth.Fragment.MusicFragment;
import com.autochips.bluetooth.Fragment.PhonebookFragment2;
import com.autochips.bluetooth.Fragment.SettingFragment;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtPlayerManage;
import com.autochips.bluetooth.util.L;
import com.media.constants.MediaConstants;
import com.yecon.common.SourceManager;
import com.yecon.savedata.SaveData;

import android.app.ActivityGroup;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ActivityManager.RunningTaskInfo;
import android.bluetooth.BluetoothDevice;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tuoxianui.view.TopBar;

public class MainBluetoothActivity extends ActivityGroup implements OnCheckedChangeListener{
	private static final String TAG = "MainBluetoothActivity";
	private static boolean DEBUG = BtEmulatorDebug.mDebugAll & BtEmulatorDebug.mDebugSettings;
	private static final String ACTION_BT_SON_ACTIVITY = "action.bt.son.activity";
	private static final String ACTION_TESTBT = "factory_test";
	private static final String BT_BOOK     = "bt.book";
	
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
    private SaveData mObjSaveData = new SaveData();
	public static final int TAB_INDEX_BT_DIALER = 0;
	public static final int TAB_INDEX_BT_HISTORY = 1;
	public static final int TAB_INDEX_BT_PHONEBOOK = 2;
	public static final int TAB_INDEX_BT_SETTINGS = 3;
	public static final int TAB_INDEX_BT_MUSIC = 4;
	public static final int TAB_INDEX_BT_RETURN = 5;
	public static final int TAB_INDEX_BT_PAIREDHISTORY = 6;
	private static int TAB_WITCH = 0;
	private static boolean bact = false;
    public static DBManager mdbmanager = null; 
	private static Object sourceTocken=null;
	public static boolean playNextTime = false;
	public static boolean goMusicFragment = false;
	private DialFragment mDialFragment = null;
	private CallLogFragment mCallLogFragment = null;
	private PhonebookFragment2 mPhonebookFragment = null;
	private MusicFragment mMusicFragment = null;
	private SettingFragment mSettingFragment = null;
	public static MainBluetoothActivity mInstance = null;
	public static boolean pausedByAudioFocus = false;
	public static boolean isPowerOff = false;
	public McuManager mMcuManager = null;
	public boolean goDial = false;
	TopBar topBar;
	
	public static Object getSourceTocken(){
		return sourceTocken;
	}

	private FragmentManager mFragmentManager;
	private static RadioGroup mTabList = null;
	private void initMember(){
		mFragmentManager = getFragmentManager();
		mDialFragment = new DialFragment();
		mCallLogFragment = new CallLogFragment();
		mPhonebookFragment = new PhonebookFragment2();
		mMusicFragment = new MusicFragment();
		mSettingFragment = new SettingFragment();
		mSettingFragment.setArguments(new Bundle());
		mTabList = (RadioGroup)findViewById(R.id.tab_List);
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.v("lifecycle " + this.getClass()  + " : onCreate");
		//L.e("playNextTime:" + playNextTime + " goMusic:" + goMusicFragment); 
		Intent serviceIntent = new Intent(this, BTService.class);
        startService(serviceIntent);
		setContentView(R.layout.main);
		topBar = (TopBar)findViewById(R.id.topbar);
		mPref = this.getSharedPreferences("system_settings", Context.MODE_PRIVATE);
		mEditor = mPref.edit();
		mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
		Bundle bundle = getIntent().getExtras();
		mInstance = this;
		initMember();
		mTabList.setOnCheckedChangeListener(this);
		if(bundle != null && bundle.getInt("startTag", 0) == 1){	
			try {
				L.e("startTag");
				mMcuManager.RPC_SetSource(McuExternalConstant.MCU_SOURCE_BT, 0x00);
				mEditor.putBoolean("startTag", true).commit();
				goMusicFragment = true;
				((RadioButton)findViewById(R.id.tab_music)).setChecked(true);
			} catch (Exception e) {
				L.e(e.toString());
			}
		}else{
			mEditor.putBoolean("startTag", false).commit();
			((RadioButton)findViewById(R.id.tab_setting)).setChecked(true);
		}
		Bluetooth.getInstance(this);
		MainApp.addActivity(this);
		if(SystemProperties.getBoolean("persist.sys.bluetooth.clear", false)){
			SystemProperties.set("persist.sys.bluetooth.clear", "false");
			Set<BluetoothDevice> pairedDevices = Bluetooth.getInstance().getPairList();
			for(BluetoothDevice bluetoothDevice : pairedDevices){
				try {
					if(!Bluetooth.getInstance().getconnectedmac().equals(bluetoothDevice.getAddress()))
						CachedBluetoothDevice.removeBond(bluetoothDevice);
				} catch (Exception e) {
					L.e(e.toString());
				}
			}
		}
		
		sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.bluetooth);
		SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify() {
			
			@Override
			public void onAudioFocusChange(int arg0) {
				Log.i(TAG, "onAudioFocusChange:" +arg0 );
				switch (arg0) {
                case AudioManager.AUDIOFOCUS_LOSS:
                		goMusicFragment = false;
    					if (Bluetooth.getInstance().isA2DPPlaying()/* && !playNextTime
    							&& Bluetooth.getInstance().iscallidle()*/) {
    						L.e("main 1 BluetoothAvrcpCtPlayerManage.CMD_PAUSE");
    						Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
    						playNextTime = true;
    					}
    					ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    					List<RunningTaskInfo> tasks =  mActivityManager.getRunningTasks(10);
    					for(RunningTaskInfo t : tasks){
    						String packageName = t.topActivity.getPackageName();
    						//L.e("packageName:" + packageName + " " + t.topActivity.getClassName() + getPackageName().equals(packageName));
    						if(getPackageName().equals(packageName)){
    							if(TAB_WITCH == TAB_INDEX_BT_MUSIC){
    								goMusicFragment = true;
    							}
    							break;
    						}
    					}
    					//L.e("goMusicFragment:" + goMusicFragment);
                	MainApp.exitAllActivity();
                	break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: 
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                	if (Bluetooth.getInstance().isA2DPPlaying() && Bluetooth.getInstance().iscallidle()) {
                		/*L.e("main 2 BluetoothAvrcpCtPlayerManage.CMD_PAUSE");
						Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
						Log.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT then pause music");
						pausedByAudioFocus = true;*/
					}
                    break;
                case AudioManager.AUDIOFOCUS_GAIN: 
                	if(pausedByAudioFocus){
                		pausedByAudioFocus = false;
                		if (!Bluetooth.getInstance().isA2DPPlaying()) {
                			Log.e(TAG, "audiofocusgain playmusic");
//                			L.e("main 1 BluetoothAvrcpCtPlayerManage.CMD_PLAY");
    						Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PLAY);
        				}
                	}
                    break;
				}
				
			}
		});
		boolean btest = getIntent().getBooleanExtra(ACTION_TESTBT, false);
		if (btest) {
			switchtab(TAB_INDEX_BT_DIALER);
			Bluetooth.getInstance().call("*");
		}else{
			String StrSon = getIntent().getStringExtra(ACTION_BT_SON_ACTIVITY);
			if (StrSon != null && StrSon.equals(BT_BOOK)) {
				switchtab(TAB_INDEX_BT_PHONEBOOK);
			}else{
				setTabWhenGoBT();
			}
		}
		
		IntentFilter intentFilter = new IntentFilter(ACTION_BT_SON_ACTIVITY);
		intentFilter.addAction("action.bt.switch.fragment.music");
		registerReceiver(mReceiver, intentFilter);
		BluetoothReceiver.registerNotifyHandler(uiHandler);
		inittelzonedatabase(this);
		Bluetooth.getInstance().openbt();
		
		
    }
    public void switchtab(int index){
		Log.d("TEST","switchtab "+index);
    	switch (index) {
		case TAB_INDEX_BT_DIALER:
			((RadioButton)findViewById(R.id.tab_dial)).setChecked(true);
			break;
		case TAB_INDEX_BT_HISTORY:
			((RadioButton)findViewById(R.id.tab_callhistory)).setChecked(true);
			break;
		case TAB_INDEX_BT_PHONEBOOK:
			((RadioButton)findViewById(R.id.tab_phonebook)).setChecked(true);
			break;
		case TAB_INDEX_BT_SETTINGS:
			((RadioButton)findViewById(R.id.tab_setting)).setChecked(true);
			break;
		case TAB_INDEX_BT_MUSIC:
			((RadioButton)findViewById(R.id.tab_music)).setChecked(true);
			break;

		default:
			break;
		}
    }
    private void setTabWhenGoBT()
    {
		Log.d("TEST","setTabWhenGoBT");
		//if(Bluetooth.getInstance().isConnected()){
			switchtab(TAB_INDEX_BT_DIALER);
		//}else{
		//	switchtab(TAB_INDEX_BT_SETTINGS);
		//}
    }

	private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BluetoothReceiver.MSG_BT_STATUS_NOTIFY) {
				Intent intent = (Intent) msg.obj;
				String recievedAction = intent.getAction();
				if (recievedAction.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)) {

			        BTProfile profilename = (BTProfile)intent.getSerializableExtra(
			        		LocalBluetoothProfileManager.EXTRA_PROFILE);
			        int profilestate = intent.getIntExtra(
			        		LocalBluetoothProfileManager.EXTRA_NEW_STATE,LocalBluetoothProfileManager.STATE_DISCONNECTED);
			        if(profilename == null) {
			             return;
			        }
			        if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)) {
			        	switch (profilestate) {
						case LocalBluetoothProfileManager.STATE_ENABLED:
							CmnUtil.showTuoXianToast(MainBluetoothActivity.mInstance, R.string.bt_turned_on);
							break;
							
						case LocalBluetoothProfileManager.STATE_DISABLED:
							CmnUtil.showTuoXianToast(MainBluetoothActivity.mInstance, R.string.bt_turned_off);
							break;
							
						case LocalBluetoothProfileManager.STATE_CONNECTED:
							CmnUtil.showTuoXianToast(MainBluetoothActivity.mInstance, R.string.str_handsfree_device_connected);
							break;
							
						//FIXME:蓝牙断开
						case LocalBluetoothProfileManager.STATE_DISCONNECTED:
							CmnUtil.showTuoXianToast(MainBluetoothActivity.mInstance, R.string.str_handsfree_device_disconnected);
							break;

						default:
							break;
						}
			        }
				}else if (recievedAction.equals(BluetoothAvrcpCtPlayerManage.ACTION_MEDIA_DATA_UPDATE)) {
					if (mObjSaveData != null ) {
						String song = intent.getStringExtra(BluetoothAvrcpCtPlayerManage.MEDIA_TITLE);
						String curName = mObjSaveData.getMediaSongName();
						if (curName != null && !curName.equals(song)) {
							mObjSaveData.setMediaSongName(song);
						}
					}
				}
			}
		}
	};
	private BroadcastReceiver onForceReceiver =  new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
    	   String action = intent.getAction();
           /*FIXME:11101
           if ("com.action.storage.sd_mount".equals(action) || "com.action.storage.usb_mount".equals(action)) {

        	   if(Bluetooth.getInstance().isDiscoverying()){
        		   //如果处于扫描中，不做任何操作
        	   }else if(Bluetooth.getInstance().isConnected() && Bluetooth.getInstance().iscallidle()){
        		   if(TAB_WITCH == TAB_INDEX_BT_MUSIC){
						goMusicFragment = true;
				   }
        		   if (Bluetooth.getInstance().isA2DPPlaying()) {
						L.e("main 4 BluetoothAvrcpCtPlayerManage.CMD_PAUSE");
						Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
						//playNextTime = true;
					}
        		   int time = 300;
        		   if(TAB_WITCH == TAB_INDEX_BT_MUSIC){
        			   time = 2000;
        		   }
        		   exitHandler.removeCallbacks(exitRunRunnable);
        		   exitHandler.postDelayed(exitRunRunnable,time);
        	   }
           }else */if(YeconConstants.ACTION_QB_PREPOWEROFF.equals(action)){
        	   isPowerOff = true;
           }else if("action.hzh.media.power.on".equals(action)){
        	   isPowerOff = false;
           }
       }
    };
    private Handler exitHandler = new Handler();
    private Runnable exitRunRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			MainApp.exitAllActivity();
		}
	};
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           if (intent.getAction().equals(ACTION_BT_SON_ACTIVITY)) {
               String StrSon = intent.getStringExtra(ACTION_BT_SON_ACTIVITY);
               if (StrSon != null && StrSon.equals(BT_BOOK)) {
            	   switchtab(TAB_INDEX_BT_PHONEBOOK);
               }
           }else if(intent.getAction().equals("action.bt.switch.fragment.music")){
        	   abortBroadcast();
        	   L.e("action.bt.switch.fragment.music");
        	   TAB_WITCH = TAB_INDEX_BT_MUSIC;
   			   setFragment(mMusicFragment);
           }
       }
    };
    public static void inittelzonedatabase(Context context){
    	if (mdbmanager == null) {
        	mdbmanager = new DBManager(context);
        	mdbmanager.openDatabase();
    	}
    }
    private void uninittelzonedatabase(){
    	if (mdbmanager != null) {
			mdbmanager.closeDatabase();
			mdbmanager = null;
		}
    }
    
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		goDial = intent.getBooleanExtra("godial", false);
		startDialActivity(goDial);
		//避免蓝牙音乐暂停后,打开蓝牙电话时,又自动播放蓝牙音乐
		if(goDial && TAB_WITCH == TAB_INDEX_BT_MUSIC) {
			TAB_WITCH = TAB_INDEX_BT_SETTINGS;
			setFragment(mSettingFragment);
			L.e("==========goDial========");
		}
		/////////////
        goDial = false;
		Bundle newExtras = intent.getExtras();
		if(newExtras.containsKey("startTag")){
			getIntent().putExtra("startTag",newExtras.getInt("startTag",0));
		}
		if(newExtras.containsKey("godial")){
			getIntent().putExtra("godial",newExtras.getBoolean("godial",false));
		}

		//L.d("startTag: " + getIntent().getIntExtra("startTag", 0) + "    " + newExtras.getInt("startTag", 0));
		openFragment(intent);
	}


	@Override
    public void onStart(){
		L.v("lifecycle " + this.getClass()  + " : onStart");
	    SystemProperties.set(PROPERTY_KEY_STARTBT, "true");
    	super.onStart();
    }

    @Override
    public void onRestart(){
    	super.onRestart();
    }

    @Override
    public void onResume(){
		L.v("lifecycle " + this.getClass()  + " : onResume");
    	isPowerOff = false;
    	sendBroadcast(new Intent(MediaConstants.CURRENT_MEDIA_IS_BLUETOOTH), null);
        SystemProperties.set(PROPERTY_KEY_STARTBT, "true");
        Bluetooth.getInstance().checkbtexception();
    	bact = true;
    	SourceManager.acquireSource(sourceTocken);

    	/*try {
			((McuManager) getSystemService(Context.MCU_SERVICE)).RPC_SetSource(MCU_SOURCE_BT, 0x00);
		} catch (RemoteException e) {
			e.printStackTrace();
		}*/
    	/*Bluetooth.getInstance().musicresume();
		if ((playNextTime || pausedByAudioFocus) && !Bluetooth.getInstance().isA2DPPlaying()) {
			playNextTime = false;
			pausedByAudioFocus = false;
//			L.e("main 2 BluetoothAvrcpCtPlayerManage.CMD_PLAY");
			Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PLAY);
		}*/

		IntentFilter onForceIntentFilter = new IntentFilter();
		/*FIXME:11101 -
		onForceIntentFilter.addAction("com.action.storage.sd_mount");
		onForceIntentFilter.addAction("com.action.storage.usb_mount");
		//*/
		onForceIntentFilter.addAction(YeconConstants.ACTION_QB_PREPOWEROFF);
		onForceIntentFilter.addAction("action.hzh.media.power.on");
		registerReceiver(onForceReceiver,onForceIntentFilter);
		goDial = getIntent().getBooleanExtra("godial", false);
		getIntent().putExtra("godial", false);
        startDialActivity(goDial);
		//

		//FIXME:11149 +
		openFragment(getIntent());
		//

		//避免蓝牙音乐暂停后,打开蓝牙电话时,又自动播放蓝牙音乐
        if(goDial && TAB_WITCH == TAB_INDEX_BT_MUSIC) {
			TAB_WITCH = TAB_INDEX_BT_SETTINGS;
			setFragment(mSettingFragment);
		}
		////////////////
        goDial = false;
    	super.onResume();
    }
    @Override
    public void onPause(){
		L.v("lifecycle " + this.getClass()  + " : onPause");
    	isPowerOff = false;
        SystemProperties.set(PROPERTY_KEY_STARTBT, "false");
    	CmnUtil.cancelToast();
    	bact = false;
    	unregisterReceiver(onForceReceiver);
    	super.onPause();
    }


    @Override
    public void onStop(){
		L.v("lifecycle " + this.getClass()  + " : onStop");
        SystemProperties.set(PROPERTY_KEY_STARTBT, "false");
    	super.onStop();
    }


    @Override
    public void onDestroy(){
		L.v("lifecycle " + this.getClass()  + " : onDestroy");
    	Bluetooth.getInstance().musicrevoke();
		if (Bluetooth.getInstance().isA2DPPlaying() && !playNextTime) {
			/*L.e("main 3 BluetoothAvrcpCtPlayerManage.CMD_PAUSE");
			Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
			playNextTime = true;*/
		}

        MainApp.removeActivity(this);
		BluetoothReceiver.unregisterNotifyHandler(uiHandler);
    	if(sourceTocken!=null){
    		SourceManager.unregisterSourceListener(sourceTocken);
    	}
		unregisterReceiver(mReceiver);
    	uninituidata();
    	mInstance = null;
    	super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	L.e("onKeyDown : keyCode :" + keyCode);
    	/*if(keyCode == KeyEvent.KEYCODE_YECON_PHONE_ON){
    		if (bact && TAB_WITCH == TAB_INDEX_BT_DIALER) {
    			mDialFragment.onCall();
			}else{
	    		Bluetooth.getInstance().recall();
			}
    		return true;
    	}*/
    	if(bact && Bluetooth.getInstance().isConnected() && keyCode == KeyEvent.KEYCODE_YECON_PHONE_ON){
    		startDialActivity(true);
    	}
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onCheckedChanged(RadioGroup rg, int id) {
		Log.d("TEST","onCheckedChanged " + id);

		mTabList.playSoundEffect(android.view.SoundEffectConstants.CLICK);
		switch (id) {
		case R.id.tab_dial:
			TAB_WITCH = TAB_INDEX_BT_DIALER;
//			setFragment(mDialFragment);
			break;
		case R.id.tab_callhistory:
			TAB_WITCH = TAB_INDEX_BT_HISTORY;
			setFragment(mCallLogFragment);
			break;
		case R.id.tab_phonebook:
			TAB_WITCH = TAB_INDEX_BT_PHONEBOOK;
			setFragment(mPhonebookFragment);
			break;
		case R.id.tab_music:
			TAB_WITCH = TAB_INDEX_BT_MUSIC;
			setFragment(mMusicFragment);
			break;
		case R.id.tab_setting:
			TAB_WITCH = TAB_INDEX_BT_SETTINGS;
			setFragment(mSettingFragment);
			break;

		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	static void uninituidata(){
		DialFragment.minputstr = "";
	}

	private void openFragment(Intent intent){
		Bundle extras = intent.getExtras();

		int openFragmentOrder = intent.getIntExtra("openFragmentOrder", 0);
//		L.e("openFragmentOrder:" + openFragmentOrder);
		if(openFragmentOrder == BluetoothConstants.START_FRAGMENT_PAIR){
			L.w("openFragmentOrder: + START_FRAGMENT_PAIR:" + intent.getBooleanExtra("rename",false));
			TAB_WITCH = TAB_INDEX_BT_SETTINGS;
			Bundle bundle = mSettingFragment.getArguments();
			bundle.putBoolean(SettingFragment.CREATEVIEW_GO_DIALVIEW, false);
			bundle.putBoolean("rename",intent.getBooleanExtra("rename",false));
			setFragment(mSettingFragment);
		}else if(openFragmentOrder == BluetoothConstants.START_FRAGMENT_BT_MUSIC || (
				//Bluetooth.getInstance().isHFPconnected() &&
				extras != null && extras.getInt("startTag", 0) == 1)){
			L.d("startTag:" + extras.getInt("startTag", 0) + "  openFragmentOrder" + openFragmentOrder);
			L.e("openFragmentOrder: + START_FRAGMENT_BT_MUSIC");
			TAB_WITCH = TAB_INDEX_BT_MUSIC;
			setFragment(mMusicFragment);
		}else if(openFragmentOrder == BluetoothConstants.START_FRAGMENT_PHONEBOOK){
			L.e("openFragmentOrder: + START_FRAGMENT_PHONEBOOK");
			TAB_WITCH = TAB_INDEX_BT_PHONEBOOK;
			setFragment(mPhonebookFragment);
		}else if(openFragmentOrder == BluetoothConstants.START_FRAGMENT_CALL_LOG){
			L.e("openFragmentOrder: + START_FRAGMENT_CALL_LOG");
			TAB_WITCH = TAB_INDEX_BT_HISTORY;
			setFragment(mCallLogFragment);
		}
		intent.removeExtra("openFragmentOrder");
	}

	public void setFragment(Fragment frag) {
		if (frag == null) {
			return;
		}
		if(frag == mSettingFragment){
			topBar.setTitle(getString(R.string.BtSettingsLabel));
		}else if(frag == mMusicFragment){
			topBar.setTitle(getString(R.string.BtMusicLabel));
		}else if(frag == mPhonebookFragment){
			topBar.setTitle(getString(R.string.BtPhonebookLabel));
		}else if(frag == mCallLogFragment){
			topBar.setTitle(getString(R.string.BtCallHistoryLabel));
		}
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.layout_content, frag);
		ft.commitAllowingStateLoss();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		for (MyTouchListener listener : myTouchListeners) {  
		    listener.onTouchEvent(ev);  
	    }
		return super.dispatchTouchEvent(ev);
	}
	
	public interface MyTouchListener {
	    public void onTouchEvent(MotionEvent event);
	}
	
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<MyTouchListener>();
    
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }
    
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove( listener );
    }
    
    public void startDialActivity(boolean goDial){
        L.i(this.getClass().getSimpleName() + " startDialActivity " + goDial);
    	if(goDial// && Bluetooth.getInstance().isConnected()
		){
			//FIXME:12004
	    	//startActivity(new Intent(this,TuoXianDialActivity.class));
	    	Bluetooth.handStartActivity(this,TuoXianDialActivity.class);
	    	//
			{
	        	Intent intent = new Intent(Context.ACTION_SOURCE_STACK_AUTOEXIT);
	        	intent.putExtra(Context.EXTRA_APK_PACKAGENAME, YeconConstants.BLUETOOTH_PACKAGE_NAME);
	        	intent.putExtra(Context.EXTRA_APK_ACTIVITY, YeconConstants.BLUETOOTH_START_ACTIVITY);
	        	sendOrderedBroadcast(intent,null); 
	        }
    	}
    }
}
