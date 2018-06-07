/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * MediaTek Inc. (C) 2010. All rights reserved.
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


import android.bluetooth.BluetoothDevice;

//add by steve
import static android.mcu.McuExternalConstant.*;
import static android.constant.YeconConstants.*;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.autochips.bluetooth.LocalBluetoothManager;
import com.autochips.bluetooth.LocalBluetoothProfileManager;
import com.autochips.bluetooth.Fragment.DialFragment;
import com.autochips.bluetooth.Fragment.PhonebookFragment2;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.PbSyncManager.PBSyncManager;
import com.autochips.bluetooth.PbSyncManager.PBSyncManagerService;
import com.autochips.bluetooth.PbSyncManager.PBSyncStruct;
import com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtService;
import com.autochips.bluetooth.hf.BluetoothHfAdapter;
import com.autochips.bluetooth.hf.BluetoothHfAtHandler;
import com.autochips.bluetooth.hf.BluetoothHfService;
import com.autochips.bluetooth.hf.BluetoothHfUtility;
import com.autochips.bluetooth.hid.BluetoothHidAdapter;
import com.autochips.bluetooth.util.L;
import com.autochips.bluetooth.dundt.SMSSyncManager;
import com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtPlayerManage;
import com.autochips.bluetooth.BtEmulatorDebug;
import com.autochips.bluetooth.CachedBluetoothDevice;
import com.autochips.inputsource.HDMI;
import com.yecon.common.SourceManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BluetoothReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothReceiver";
    private static boolean DEBUG = BtEmulatorDebug.mDebugAll & BtEmulatorDebug.mDebugSettings;
    
    //by steve
    public final static String ACTION_QB_POWERON_COMPLETE = "autochips.intent.action.QB_POWERON_COMPLETE";
    
    public static final String ACTION_DOWNLOAD_FINISH  = "com.autochips.bluetooth.PbSyncManager.PbSyncManagerService.action.download_finish";
    public static BluetoothHfAdapter m_HandsfreeAdpter= null;
    public static BluetoothHidAdapter m_HIDAdpter= null;
    public static PBSyncManager mPBSyncProxy = null;
    public static SMSSyncManager mSmsSyncProxy = null; 

    public static String mHfConnectedDeviceAddr = "";
    public static boolean mbHfConnected = false;
    public static boolean mbA2DPConnected = false;
    public static BluetoothDevice mConnectedDevice = null;
    public static BluetoothDevice mConnectedA2DPDevice = null;
    private static LocalBluetoothManager mLocalManager = null;


    private static TimerTask mTaskShutBT = null;
	private String mPin=null;

    
    private static Object syncObj = new Object();
    private static Object syncObj_closebt = new Object();
	public BluetoothReceiver(){
    	super();
	}

    @Override
    public void onReceive(Context context, Intent intent) {
    	Bluetooth.getInstance(context);
        String action = intent.getAction();
        if (action.equals(MCU_ACTION_ACC_OFF) || action.equals(ACTION_QB_POWEROFF)) {
        	Bluetooth.getInstance().handleaccoff(intent);
        	if (action.equals(ACTION_QB_POWEROFF)) {
        		Bluetooth.getInstance().closebt();
             	MainApp.exitAllActivity();
			}else{
        		boolean powerOff = intent.getBooleanExtra("power_off", false);
        		if (!powerOff) {
    				synchronized (syncObj_closebt) {
    		        	if (mTaskShutBT == null) {
    						mTaskShutBT = new TimerTask() {
    							@Override
    							public void run() {
    									if (mTaskShutBT != null) {
    										Log.e("btrec", "shutdownbt");
    										Bluetooth.getInstance().closebt();	
    									}
    								}
    						};
        		        	new Timer().schedule(mTaskShutBT, 4000);
    					}
    				}
				}
			}
		} 
		else if (action.equals(MCU_ACTION_ACC_ON) || action.equals(ACTION_QB_POWERON)) {
        	Bluetooth.getInstance().handleaccoff(intent);
			synchronized (syncObj_closebt) {
				if (mTaskShutBT != null) {
					mTaskShutBT.cancel();
					mTaskShutBT = null;
				}
				Log.e("btrec", "openbt");
				if (Bluetooth.getInstance().readLastBtState()) {
					Bluetooth.getInstance().openbt();
				}
			}
		}

        //************************  ACTION_BOOT_COMPLETED  ***********************//
        else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
     //      handleBootCompleted(intent, context);
           Bluetooth.getInstance().handlebootcompleted();
        }
        //************************  ACTION_STATE_CHANGED  ***********************//
		else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
	//	    handleAdapterStateUpdate(intent, context);   
		    Bluetooth.getInstance().handleAdapterStateUpdate(intent, context);           
		    notifyUiBtStatus(intent);
        }
        //************************  ACTION_DISABLE_PROFILES  ***********************//
        else if(action.equals(LocalBluetoothProfileManager.ACTION_DISABLE_PROFILES)){
			Bluetooth.getInstance().setServicestate(context, false);
			notifyUiBtStatus(intent);
        }        
        //************************  ACTION_PROFILE_STATE_UPDATE  ***********************//
	    else if(action.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)){
			handleProfileStateUpdate(intent, context);
			Bluetooth.getInstance().handleProfileStateUpdate(intent);
			notifyUiBtStatus(intent);
        }
        //************************  ACTION_LINK_MODE_CHANGED  ***********************//
        else if (action.equals(BluetoothAdapter.ACTION_LINK_MODE_CHANGED)) {
    		Bluetooth.getInstance().handleLinkModeChanged(intent, context);
    		notifyUiBtStatus(intent);
        }

        else if (action.equals(BluetoothHfAtHandler.ACTION_PHONE_NUMBER_CHANGED)) {
        	Bluetooth.getInstance().handlecallnumchanged(intent);
        	notifyUiBtStatus(intent);
        }
		else if (action.equals(PBSyncManagerService.ACTION_DOWNLOAD_FINISH)) {
			int iDownloadPath = intent.getIntExtra(PBSyncManagerService.EXTRA_PBSYNC_FOLDER,
                    PBSyncStruct.NUM_OF_BT_PBAP_SYNC_PATH);
			 if(iDownloadPath == PBSyncStruct.BT_PBAP_SYNC_PATH_ALL_PHONEBOOK) {
				 Bluetooth.getInstance().handleDownLoadPhoneFinish(intent);
             }
	    	notifyUiBtStatus(intent);
		}
		else if (action.equals(PBSyncManagerService.ACTION_STARTDOWNLOAD_POSITION)
				|| action.equals(PBSyncManagerService.ACTION_DOWNLOAD_ONESTEP)) {
	    	notifyUiBtStatus(intent);
		}
		else if (action.equals(BluetoothAvrcpCtPlayerManage.ACTION_MEDIA_DATA_UPDATE)
				|| action.equals(BluetoothAvrcpCtPlayerManage.ACTION_PLAYBACK_DATA_UPDATE)
				|| action.equals(BluetoothAvrcpCtService.ACTION_BTMUSIC_INTERACTIVE)) {
			Bluetooth.getInstance().handleMusicNotify(intent);
    		notifyUiBtStatus(intent);
		}

        //************************  ACTION_CALL_STATE_CHANGE  ***********************//
        else if(action.equals(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE)){
	    	//#FIXME 12005
           /*Bluetooth.getInstance().callStateChange(intent, context);*/
			//

           Bluetooth.getInstance().handleCallStateUpdate(intent);
           handleCallStateUpdate(intent, context);
           notifyUiBtStatus(intent);
	    }else if (action.equals(BluetoothHfService.ACTION_SCO_STATE_CHANGED)) {
	    	Bluetooth.getInstance().handlescochange(intent);
	    	notifyUiBtStatus(intent);
        }
		else if (action.equals(BluetoothDevice.ACTION_FOUND) 
				|| action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
				|| action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
				|| action.equals(BluetoothDevice.ACTION_NAME_CHANGED)) {    
		    notifyUiBtStatus(intent);
		}
		else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
			Bluetooth.getInstance().handleBondChanged(intent);       
		    notifyUiBtStatus(intent);
		}
        //add by chenwl for call out
        
        else if(action.equals(Bluetooth.ACTION_BLUETOOTH_CALLOUT)  ){
            onBluetoothCalloutRequest(intent);
        }
        else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
        	Bluetooth.getInstance().handleBluetoothPairingRequest(intent); 
		    notifyUiBtStatus(intent);
		}
        //************************  HDMI ACTION  ***********************//
        else if (action.equals(HDMI.ACTION_MHLHID_GET_CONNECT_STATE)) {
            Bluetooth.getInstance().handleHDMIAction(intent, context);
        }else if (action.equals(HDMI.ACTION_MHLHID_GET_RESOLUTION)) {
        	Bluetooth.getInstance().handleHDMIAction(intent, context);
        } else if (action.equals(HDMI.ACTION_MHLHID_NOTIFY_RESOLUTION)) {
        	Bluetooth.getInstance().handleHDMIAction(intent, context);
        } else if (action.equals(HDMI.ACTION_MHLHID_MOUSE_DATA)) {
        	Bluetooth.getInstance().handleHDMIAction(intent, context);
        } else if (action.equals(HDMI.ACTION_MHLHID_CONTROL_DATA)) {           
        	Bluetooth.getInstance().handleHDMIAction(intent, context);
        }
        else{
			synchronized (syncObj)
			{
				if(SourceManager.lastSource()!=SourceManager.SRC_NO.bluetooth){
					return;
				}
        		if (intent.getAction().equals(MCU_ACTION_MEDIA_NEXT)){
        			Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_NEXT);
                } 
                else if (intent.getAction().equals(MCU_ACTION_MEDIA_PREVIOUS)){
                	Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PREV);
                } 
                else if (intent.getAction().equals(MCU_ACTION_MEDIA_PLAY_PAUSE)){
					if (Bluetooth.getInstance().isA2DPPlaying()) {
						L.e("BT receiver BluetoothAvrcpCtPlayerManage.CMD_PAUSE");
						Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PAUSE);
					} else{
						L.e("BT receiver BluetoothAvrcpCtPlayerManage.CMD_PLAY");
						Bluetooth.getInstance().sendAvrcpCommand(BluetoothAvrcpCtPlayerManage.CMD_PLAY);
					}
                }
			}
		}

    }
    private void onBluetoothCalloutRequest(Intent intent){
        String calloutNumber 
            = intent.getStringExtra(Bluetooth.EXTRA_BLUETOOTH_CALLOUT_NUMBER);
        Bluetooth.getInstance().call(calloutNumber);
    }


    private void handleCallStateUpdate(Intent intent, Context context) {
        int phoneCallState = intent.getIntExtra(BluetoothHfUtility.EXTRA_CALL_STATE, 0);

        if (Bluetooth.getInstance().GetCallingNum() == null
        		|| Bluetooth.getInstance().GetCallingNum().isEmpty()) {
			Log.i(TAG, "callinnum is not exist, phonecallactivity not act");
		}
        if (phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_IDLE || Bluetooth.getInstance().iscallidle()
        	/*	|| Bluetooth.getInstance().GetCallingNum() == null
        		|| Bluetooth.getInstance().GetCallingNum().isEmpty()*/
        		|| phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING && Bluetooth.getInstance().isspeakingfirst()
        		&& (System.currentTimeMillis() - Bluetooth.getInstance().getwhenconnected() > 60000 && Bluetooth.getInstance().isHFPconnected())) {
			return;
		}

        Intent intentPhoneCall= new Intent();
//      intentPhoneCall.setClass(context,PhoneCallActivity.class); //FIXME:tuoxian
        intentPhoneCall.setClass(context,TuoXianDialActivity.class);
        intentPhoneCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentPhoneCall.putExtra("PhoneNumber", Bluetooth.getInstance().GetCallingNum());
        intentPhoneCall.putExtra("PhoneName","");
        intentPhoneCall.putExtra("isHFInitiated","false");	
        intentPhoneCall.putExtra("isspeaking", phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING);

        try {
			L.i(this.getClass().getSimpleName() + " startDialActivity");
			context.startActivity(intentPhoneCall);
        } catch (ActivityNotFoundException e) {
        }
    }

    private void handleProfileStateUpdate(Intent intent, Context context) {
        BTProfile profilename = (BTProfile)intent.getSerializableExtra(LocalBluetoothProfileManager.EXTRA_PROFILE);
        int profilestate = intent.getIntExtra(LocalBluetoothProfileManager.EXTRA_NEW_STATE,LocalBluetoothProfileManager.STATE_DISCONNECTED);
        if(profilename == null) {
             Log.e(TAG, "profilename == null. return.");
             return;
        }
        if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)) {
            switch (profilestate) {
            case LocalBluetoothProfileManager.STATE_ENABLED:        
                break;
            case LocalBluetoothProfileManager.STATE_DISABLED:
            	DialFragment.minputstr = "";
            	PhonebookFragment2.allPhonebooks.clear();
                break;
            case LocalBluetoothProfileManager.STATE_CONNECTED:          
                break;
            case LocalBluetoothProfileManager.STATE_DISCONNECTED:
            	DialFragment.minputstr = "";
            	PhonebookFragment2.allPhonebooks.clear();
                break;
            }           
        }
    }  

	private static List<Handler> notifyHandlerList=new ArrayList<Handler>();
	public static int MSG_BT_STATUS_NOTIFY  = 1008;
	public static void registerNotifyHandler(Handler handler){
		notifyHandlerList.add(handler);
	}
	public static void unregisterNotifyHandler(Handler handler){
		notifyHandlerList.remove(handler);
	}
	private void notifyUiBtStatus(Intent intent){
		Iterator<Handler> nf = notifyHandlerList.iterator();
		Handler h;
		while(nf.hasNext()){
			h = nf.next();
			h.sendMessage(h.obtainMessage(MSG_BT_STATUS_NOTIFY, intent));
		}
	}
}
