package com.autochips.bluetooth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static android.constant.YeconConstants.*;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.media.AudioManager;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.PbSyncManager.PBRecord;
import com.autochips.bluetooth.PbSyncManager.PBSyncManager;
import com.autochips.bluetooth.PbSyncManager.PBSyncStruct;
import com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtPlayerManage;
import com.autochips.bluetooth.dundt.SMSSyncManager;
import com.autochips.bluetooth.hf.BluetoothHfAdapter;
import com.autochips.bluetooth.hf.BluetoothHfAtHandler;
import com.autochips.bluetooth.hf.BluetoothHfService;
import com.autochips.bluetooth.hf.BluetoothHfUtility;
import com.autochips.bluetooth.hid.BluetoothHidAdapter;
import com.autochips.bluetooth.util.L;
import com.autochips.bluetooth.util.SystemContactsManager;
import com.autochips.inputsource.HDMI;
import com.autochips.settings.AtcSettings;
import com.yecon.savedata.SaveData;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings.Secure;
import static android.mcu.McuExternalConstant.*;
import android.text.TextUtils;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class Bluetooth {
    public final static String PERSYS_BT_DEVICE = "persist.sys.bt_device";
    public final static String PERSYS_BT_PAIR = "persist.sys.bt_pair";
    public final static String PERSYS_BT_AUTO_CONNECT = "persist.sys.bt_auto_connect";
    public final static String PERSYS_BT_AUTO_ANSWER = "persist.sys.bt_auto_answer";
    public final static String PERSYS_BT_DEVICE_NAME_OEM = "persist.sys.bt_device_name_oem";
    public static final String ACTION_BLUETOOTH_CALLOUT =
        "com.autochips.bluetooth.BtDialPadActivity.action.BLUETOOTH_CALLOUT";
     public static final String EXTRA_BLUETOOTH_CALLOUT_NAME =
            "com.autochips.bluetooth.BtDialPadActivity.extra.EXTRA_BLUETOOTH_CALLOUT_NAME";
    public static final String EXTRA_BLUETOOTH_CALLOUT_NUMBER =
            "com.autochips.bluetooth.BtDialPadActivity.extra.EXTRA_BLUETOOTH_CALLOUT_NUMBER";
    
    public final static String PERSYS_BT_DEVICE_AUTO_SET="persist.sys.bt_device_auto_set";
    public static final int TYPE_OUTGOING = 0;
    public static final int TYPE_INCOMING = 1;
    public static final int TYPE_MISSED = 2;
    public static long whenconnected = 0;

	private static final String TAG = "Bluetooth";
    private static final String HF_SERVICE_CLASS = "com.autochips.bluetooth.hf.BluetoothHfService";
    private static final String PBAPCLIENT_SERVICE_CLASS = "com.autochips.bluetooth.pbapclient.BluetoothPbapClientService";
    private static final String A2DPSINK_SERVICE_CLASS = "com.autochips.bluetooth.a2dpsink.BluetoothA2dpSinkService";
    private static final String AVRCPCT_SERVICE_CLASS = "com.autochips.bluetooth.avrcpct.BluetoothAvrcpCtService";
    private static final String PBSYNC_SERVICE_CLASS = "com.autochips.bluetooth.PbSyncManager.PBSyncManagerService";
    private static final String HID_SERVICE_CLASS = "com.autochips.bluetooth.hid.BluetoothHidService";

    private static final String PHONEBOOK_UPDATE = "com.autochips.bluetooth.PhonebookUpdate";
    private static final String PHONEBOOK_PATH = "com.autochips.bluetooth.PhonebookPath";
    private String mediaTitleInfo = "";
    private String mediaArtistInfo = "";
    private String mediaAlbumInfo = "";
    private static String mlastcallnum = "";
    private int reconnectA2DP = 2;
    private int reconnectAVRCP = 2;
    private boolean isHFPdisconnecting = false;
    private boolean mbConnectedHFP;
    private boolean mbConnectedA2DP;
    private boolean mbConnectedAvrcp;
    private BluetoothDevice mConnectedHFPDevice = null;
    private BluetoothDevice mConnectedA2DPDevice = null;
    private static TimerTask avrcpTimer = null;
    private static int avrcpcmd = 0;
    private static boolean bpermit = true;
	private static boolean bPlaying = false;
    private static Bluetooth mBluetooth = null;
	private Context mContext;
    private LocalBluetoothManager mLocalManager = null;
    private BluetoothHfAdapter m_HandsfreeAdpter = null; 
    private BluetoothHidAdapter m_HIDAdpter= null;
    private BluetoothAvrcpCtPlayerManage mAvrcpCtPlayerManage = null; 
    private PBSyncManager mPBSyncProxy = null;      
    private SMSSyncManager mSmsSyncProxy = null; 
    private int mcallstatus = 0;
    private int mcallstatus_last = 0;
    private int limit_avrcp = 0;
    private boolean isaccoff = false;
    private boolean ispoweroff = false;
    private boolean isbtopen = false;
    private boolean iscompletecall = false;
    private boolean isspeakingfirst = true;
	private static int mTimeoff = 0;
    private TimerTask mTimerAutoconnect = null;
    private TimerTask mTimerRegresume = null;
    private TimerTask mTimerQuerycalllist = null;
    private int ncountautoconnect = 2;
    private int connecttime = 1000;
    private String macPairing = "";
    private SaveData mObjSaveData = new SaveData();
    private TimerTask mTimerChangePhoneAudio = null;
    
    private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;

	private ObservableEmitter callStateChangeEmitter;
	private Map<Integer,Object> map = new HashMap<Integer,Object>();
    public Bluetooth(Context context){
    	mContext = context;
    	mPref = mContext.getSharedPreferences("bluetooth_settings", 3);
		mEditor = mPref.edit();
		
		String name = mPref.getString("bluetooth.name", "IFAW");
		String pin = mPref.getString("bluetooth.pin", "");
		SystemProperties.set("persist.bt.SSP", "1");
		if(!TextUtils.isEmpty(name)){
//			setDeviceName(name);
		}
		
		if(!TextUtils.isEmpty(pin)){
			setDevicePin(pin);
		}
		//createRx();
    }
    public static Bluetooth getInstance(){
    	return mBluetooth;
    }
    public static Bluetooth getInstance(Context context){
    	if (mBluetooth == null) {
			mBluetooth = new Bluetooth(context);
			mBluetooth.mLocalManager = LocalBluetoothManager.getInstance(context);
		}else{
			mBluetooth.mContext = context;
			
		}

    	return mBluetooth;
    }
    private void createRx(){
		Observable.create(new ObservableOnSubscribe<Map<Integer,Object>>() {
			@Override
			public void subscribe(ObservableEmitter<Map<Integer,Object>> observableEmitter) throws Exception {
				callStateChangeEmitter = observableEmitter;
			}
		})
		.throttleFirst(100, TimeUnit.MILLISECONDS)
		.observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Observer<Map<Integer, Object>>() {
			@Override
			public void onSubscribe(Disposable disposable) {

			}

			@Override
			public void onNext(Map<Integer, Object> map) {
				L.d("BluetoothReceiver onNext");
				Intent intent = (Intent) map.get(0);
				Context context = (Context) map.get(1);
				handleCallStateUpdate(intent);
				handleCallStateUpdate(intent, context);
			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onComplete() {

			}
		});
	}
    public void checkbtexception(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null && btAdapter.isEnabled()) {
        	isbtopen = true;
        	if (mLocalManager == null) {
                mLocalManager = LocalBluetoothManager.getInstance(mContext);
			}
        	if (m_HandsfreeAdpter == null || mAvrcpCtPlayerManage == null
        			|| mPBSyncProxy == null) {
            	mLocalManager.setBluetoothEnabled(false);
			}
        }
    }
    private void uninitdata(){
        mediaTitleInfo = "";
        mediaArtistInfo = "";
        mediaAlbumInfo = "";
        mlastcallnum = "";
        reconnectA2DP = 2;
        reconnectAVRCP = 2;
        isHFPdisconnecting = false;
        mbConnectedHFP = false;
        mbConnectedA2DP = false;
        mbConnectedAvrcp = false;
        mConnectedHFPDevice = null;
        mConnectedA2DPDevice = null;
    	bPlaying = false;
    }
    public boolean isaccoff(){
    	return isaccoff;
    }
    public String getPairingmac(){
    	return macPairing;
    }
    public void startchangephoneaudiotimer(){
    	stopchangephoneaudiotimer();
    	mTimerChangePhoneAudio = new TimerTask() {
			@Override
			public void run() {
		    	if (m_HandsfreeAdpter == null) {
					return;
				}
		    	
		    	m_HandsfreeAdpter.Bluetooth_Hf_SwithAudioTransfer(false);
			}
		};
		new Timer().schedule(mTimerChangePhoneAudio, 1000);
    }
    public void stopchangephoneaudiotimer(){
    	if (mTimerChangePhoneAudio != null) {
			mTimerChangePhoneAudio.cancel();
			mTimerChangePhoneAudio = null;
		}
    }
    //TODO setting
    public void setconnectable(boolean bable){
    	if (mLocalManager != null) {
			if (bable) {
				if (readBTDiscoveryData()) {
					Log.i(TAG, "setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, -1");
			    	mLocalManager.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, -1);
				}else{
					Log.i(TAG, "setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE, -1");
			    	mLocalManager.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE, -1);
				}
			}else{
				Log.i(TAG, "setScanMode(BluetoothAdapter.SCAN_MODE_NONE, -1");
		    	mLocalManager.setScanMode(BluetoothAdapter.SCAN_MODE_NONE, -1);
			}
		}
    }
    public long getwhenconnected(){
    	return whenconnected;
    }
    public String getconnectedmac(){
    	if (mConnectedHFPDevice != null) {
			return mConnectedHFPDevice.getAddress();
		}
		if (mConnectedA2DPDevice != null) {
			return mConnectedA2DPDevice.getAddress();
		}
		return "";
    }
    private void switchbt(boolean bset){
    	if (isbtopen == bset) {
			return;
		}
    	if (mLocalManager == null) {
    		mLocalManager = LocalBluetoothManager.getInstance(mContext);
		}
    	if (mLocalManager == null) {
			return;
		}
    	mLocalManager.setBluetoothEnabled(bset);
    }
    public void openbt(){
    	switchbt(true);
    }
    public void closebt(){
    	switchbt(false);
    }
    public Set<BluetoothDevice> getPairList(){
    	if (mLocalManager == null) {
			return null;
		}
        BluetoothAdapter btAdapter = mLocalManager.getBluetoothAdapter() ;
        if(btAdapter== null)
            return null;
        return btAdapter.getBondedDevices();
    }
    public BluetoothDevice getRemoteDevice(String address){
    	if (mLocalManager != null && address != null) {
    		BluetoothAdapter adapter = mLocalManager.getBluetoothAdapter();
    		if (adapter != null) {
            	return adapter.getRemoteDevice(address);	
			}
		}
    	return null;
    }
	public void doDiscovery() { 
		if (!isbtopen) {
			return;
		}
		if (mLocalManager == null || mLocalManager.getBluetoothAdapter() == null) {
			return;
		}
        if (mLocalManager.getBluetoothAdapter().isDiscovering()) {
            mLocalManager.getBluetoothAdapter().cancelDiscovery();
        }
        else {
            mLocalManager.getBluetoothAdapter().startDiscovery();  
        }
    }
	public boolean isDiscoverying(){
		if (!isbtopen) {
			return false;
		}
		if (mLocalManager == null || mLocalManager.getBluetoothAdapter() == null) {
			return false;
		}
		return mLocalManager.getBluetoothAdapter().isDiscovering();
	}
	public void stopDiscovery(){
		if (mLocalManager == null || mLocalManager.getBluetoothAdapter() == null) {
			return;
		}
        if (mLocalManager.getBluetoothAdapter().isDiscovering()) {
            mLocalManager.getBluetoothAdapter().cancelDiscovery();
        }
	}
	public boolean isbtopened(){
		return isbtopen;
	}
	
    

	
	// TODO connect
	public String getConnectedHFPAddr(){
		if (mConnectedHFPDevice != null) {
			return mConnectedHFPDevice.getAddress();
		}else{
			return "";
		}
	}
	public String getConnectedA2DPAddr(){
		if (mConnectedA2DPDevice != null) {
			return mConnectedA2DPDevice.getAddress();
		}else{
			return "";
		}
	}
	public BluetoothDevice getConnectedHFPDevice(){
		return mConnectedHFPDevice;
	}
	public BluetoothDevice getConnectedA2DPDevice(){
		return mConnectedA2DPDevice;
	}
	public boolean isA2DPconnected(){
		return mbConnectedA2DP;
	}
	public boolean isAVRCPconnected(){
		return mbConnectedAvrcp;
	}
	public boolean isHFPconnected(){
		return mbConnectedHFP;
	}
	public boolean isConnected(){
		return mbConnectedHFP || mbConnectedA2DP;
	}
	public void connect(BluetoothDevice device){
		if (isConnected() || !isbtopen) {
			return;
		}
		stopDiscovery();
        if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
            CachedBluetoothDevice cachedDevice = CachedBluetoothDevice.getCachedDevice(mContext, device);
            cachedDevice.connect(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF); 
            cachedDevice.connect(LocalBluetoothProfileManager.BTProfile.Bluetooth_A2DP_SINK); 
            cachedDevice.connect(LocalBluetoothProfileManager.BTProfile.Bluetooth_AVRCP_CT); 
        } else {
        	device.createBond();
        }
	}
	public void disconnect(){
		if (!isbtopen) {
			return;
		}
		stopalldownload();
        if (mbConnectedHFP) {
            CachedBluetoothDevice cachedDevice = CachedBluetoothDevice.getCachedDevice(
                    mContext, mConnectedHFPDevice);
            cachedDevice.disconnect();
		}
	}
	public void connectAVRCP(){
    	if (!mbConnectedAvrcp && isbtopen && mbConnectedA2DP) {
			CachedBluetoothDevice cachedDevice = CachedBluetoothDevice.getCachedDevice(
					mContext, mConnectedA2DPDevice);
			cachedDevice.connect(BTProfile.Bluetooth_AVRCP_CT);
		}
	}
	public void connectHFP(){
    	if (!mbConnectedHFP && isbtopen && mbConnectedA2DP) {
			CachedBluetoothDevice cachedDevice = CachedBluetoothDevice.getCachedDevice(
					mContext, mConnectedA2DPDevice);
			cachedDevice.connect(BTProfile.Bluetooth_HF);
		}
	}
	public void connectA2DP(){
    	if (!mbConnectedA2DP && isbtopen && mbConnectedHFP) {
			CachedBluetoothDevice cachedDevice = CachedBluetoothDevice.getCachedDevice(
					mContext, mConnectedHFPDevice);
			cachedDevice.connect(BTProfile.Bluetooth_A2DP_SINK);
		}
	}
	public void disconnectA2DP(){
    	if (mbConnectedA2DP && isbtopen) {
			CachedBluetoothDevice cachedDevice = CachedBluetoothDevice.getCachedDevice(
					mContext, mConnectedA2DPDevice);
			cachedDevice.disconnect(BTProfile.Bluetooth_A2DP_SINK);
		}
	}
	public void disconnectHFP(){
    	if (mbConnectedHFP && isbtopen) {
			CachedBluetoothDevice cachedDevice = CachedBluetoothDevice.getCachedDevice(
					mContext, mConnectedHFPDevice);
			cachedDevice.disconnect(BTProfile.Bluetooth_HF);
		}
	}
	public void startregresume(){
    	if (mTimerRegresume == null) {
    		mTimerRegresume = new TimerTask() {
				@Override
				public void run() {
        			//bluetooth connected, reg A2DP resume
        			if(mAvrcpCtPlayerManage != null) {
        				mAvrcpCtPlayerManage.regResume(true);
        			}
        			stopregresume();
				}
			};
	    	new Timer().schedule(mTimerRegresume, 1000);
		}
	}
	public void stopregresume(){
		if (mTimerRegresume != null) {
			mTimerRegresume.cancel();
			mTimerRegresume = null;
		}
	}
	public void stopAutoconnect(){
		if (mTimerAutoconnect != null) {
			mTimerAutoconnect.cancel();
			mTimerAutoconnect = null;
		}
	}
	static final Object obj_autoconnect = new Object();
	public void startautoconnect(boolean bjustdisconnectfortimeout){
    	synchronized (obj_autoconnect) {
        if (m_HandsfreeAdpter != null &&
            mPBSyncProxy != null &&
            m_HIDAdpter != null &&
            mAvrcpCtPlayerManage != null &&
			!mbConnectedA2DP &&
			!mbConnectedAvrcp && 
			!mbConnectedHFP) {
        	if (!isAutoconnect()) {
        		if (mTimerAutoconnect != null) {
					mTimerAutoconnect.cancel();
					mTimerAutoconnect = null;
				}
				return;
			}
        	if (bjustdisconnectfortimeout) {
				ncountautoconnect = 60;
				connecttime = 30000;
			}else{
				ncountautoconnect = 2;
				connecttime = 1000;
			}
            	if (mTimerAutoconnect == null && (!bjustdisconnectfortimeout || m_HandsfreeAdpter.Bluetooth_Hf_isReconnectNeeded())) {
    				mTimeoff = 0;
    				mTimerAutoconnect = new TimerTask() {
    					@Override
    					public void run() {
    			        	synchronized (obj_autoconnect) {
	    						String LastConnectedDeviceAddr = readLastConnectedMac();
	                            if (LastConnectedDeviceAddr != null && !LastConnectedDeviceAddr.isEmpty()){
	                                BluetoothDevice lastConnectDevice = getRemoteDevice(LastConnectedDeviceAddr);
	                                if(lastConnectDevice != null
	                                    &&(lastConnectDevice.getBondState() == BluetoothDevice.BOND_BONDED)){
	                                    CachedBluetoothDevice cachedDevice = CachedBluetoothDevice.getCachedDevice(
	                                    mContext, lastConnectDevice);
	                                    if(!cachedDevice.isConnected()){
	                   //                     cachedDevice.connect();

	                                        cachedDevice.connect(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF); 
	                                        cachedDevice.connect(LocalBluetoothProfileManager.BTProfile.Bluetooth_A2DP_SINK); 
	                                        cachedDevice.connect(LocalBluetoothProfileManager.BTProfile.Bluetooth_AVRCP_CT);
	                                    }
	                                }
	                            }
	                    		mTimeoff += 1;
	                            if (mTimeoff >= ncountautoconnect && mTimerAutoconnect != null) {
	                            	mTimerAutoconnect.cancel();
	                            	mTimerAutoconnect = null;
	                            }
    			        	}
    					}
    				};
    				Log.e(TAG, "m_HandsfreeAdpter.Bluetooth_Hf_isReconnectNeeded()"+m_HandsfreeAdpter.Bluetooth_Hf_isReconnectNeeded());
    				if (!bjustdisconnectfortimeout) {
        	        	new Timer().schedule(mTimerAutoconnect, 0, connecttime);
					}else if (m_HandsfreeAdpter.Bluetooth_Hf_isReconnectNeeded()){
        	        	new Timer().schedule(mTimerAutoconnect, 0, connecttime);
					}
    			}
			}
        }
	}
	
	//TODO record

	public void delephonebook(boolean cleanMac){
    	File f = new File(DBManager.DB_PATH + "/" + "bt_phonebook.txt");
    	if(f.exists())
    		f.delete();
		
        if (mPBSyncProxy.GetSyncState() == PBSyncStruct.STATE_IDLE) {
			Log.d("TEST","delephonebook writeLastDownLoadMac");
        	if(cleanMac)
				writeLastDownLoadMac("");
			 int nPath = PBSyncStruct.BT_PBAP_SYNC_PATH_ALL_PHONEBOOK;
			 mPBSyncProxy.SetRemoteDevice(mConnectedHFPDevice,nPath);
		}
	}

	public void addphonebook(String name, String num){
		String file = DBManager.DB_PATH + "/" + "bt_phonebook.txt";
		Log.e(TAG, "8127 addphonebook:"+file);
		String content = name + "|" + num + System.getProperty("line.separator");
		if (!(new File(file).exists())) {
            try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(content.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			appendfile(file, content);
		}
	}
	public void queryContacts() {
    	new Thread(){
    		public void run() {
    			SystemContactsManager.queryContacts(mContext);
    		};
    	}.start();
	}
	static final Object obj_refreshsystemcontact = new Object();
	public void refreshsystemcontact(){
    	new Thread(){
    		public void run() {
    			synchronized (obj_refreshsystemcontact) {
        			SystemContactsManager.cleanContacts(mContext);
        			List<PBRecord> record = new ArrayList<PBRecord>();
        			GetPhonebook(record);
        			delephonebook(false);
        			if (!SystemContactsManager.addContacts(mContext, record)) {
        				for (int i = 0; i < record.size(); i++) {
    						addphonebook(record.get(i).getName(), record.get(i).getNumber());
						}
            			mContext.sendBroadcast(new Intent(PHONEBOOK_UPDATE).putExtra(PHONEBOOK_PATH, DBManager.DB_PATH + "/" + "bt_phonebook.txt"));
					}else{
	        			mContext.sendBroadcast(new Intent(PHONEBOOK_UPDATE).putExtra(PHONEBOOK_PATH, ""));
					}
				}
    		};
    	}.start();
	}
	/*
    public void cleansystemcontact(){
    	new Thread(){
    		public void run() {
    			SystemContactsManager.cleanContacts(mContext);
    		};
    	}.start();
    }
    public void addsystemcontact(){
    	new Thread(){
    		public void run() {
    			List<PBRecord> record = new ArrayList<PBRecord>();
    			GetPhonebook(record);
    			SystemContactsManager.addContacts(mContext, record);
    		};
    	}.start();
    }
    */
	public int GetPhonebookRecCnt(){
		if (mPBSyncProxy == null || !mbConnectedHFP || readLastDownLoadMac().equals("")) {
			Log.d("TEST","GetPhonebookRecCnt mPBSyncProxy "+mPBSyncProxy+mbConnectedHFP+readLastDownLoadMac());
			return 0;
		}
		return mPBSyncProxy.GetRecCnt(mConnectedHFPDevice.getAddress(), PBSyncStruct.BT_PBAP_SYNC_PATH_ALL_PHONEBOOK);
	}
	public boolean GetPhonebook(List<PBRecord> record){
		if (mPBSyncProxy == null || record == null) {
			return false;
		}
		return mPBSyncProxy.GetRecord(PBSyncStruct.BT_PBAP_SYNC_PATH_ALL_PHONEBOOK, 0, GetPhonebookRecCnt(), record);
	}
	public boolean GetRecord(List<PBRecord> record){
		if (!mbConnectedHFP || record == null) {
			return false;
		}
		String filestr = DBManager.DB_PATH + "/" + "bt_record.txt";
		File file= new File(filestr);
		if (!(file.exists())) {
			return false;
		}else{
			try {
				InputStream is = new FileInputStream(file);
				BufferedReader reader=new BufferedReader(new InputStreamReader(is)); 
				while (true) {
					String line = reader.readLine();
					if (line == null) {
						break;
					}
					String[] children = line.split("\\|");
					
					if (children.length != 4) {
						break;
					}
					PBRecord item = new PBRecord();
					item.setType(Integer.parseInt(children[0]));
					item.setName(children[1]);
					item.setNumber(children[2]);
					item.setCalltime(children[3]);
					record.add(item);
				}
				reader.close();
				return !record.isEmpty();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public void refreshrecord(List<PBRecord> records){
		String file = DBManager.DB_PATH + "/" + "bt_record.txt";
    	File f = new File(file);
    	if(f.exists())
    	f.delete();
        try {
  			new File(file).createNewFile();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}

		try {
			FileWriter writer = new FileWriter(file, true);
	    	for (int i = 0; i < records.size(); i++) {
	    		String type = String.valueOf(records.get(i).getType());
	    		String name = records.get(i).getName();
	    		String num  = records.get(i).getNumber();
	    		String time = records.get(i).getCalltime();
	    		String content = type + "|" + name + "|" + num + "|" + time + System.getProperty("line.separator");
	            writer.write(content);
			}
            if(writer != null){
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}
	public void addrecord(int type, String name, String num, String time){
		String file = DBManager.DB_PATH + "/" + "bt_record.txt";
		String typestr = String.valueOf(type);
		String content = typestr + "|" + name + "|" + num + "|" + time + System.getProperty("line.separator");
		if (!(new File(file).exists())) {
            try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(content.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			appendfile(file, content);
		}
	}
	public void delrecord(PBRecord record){
		List<PBRecord> records = new ArrayList<PBRecord>();
		GetRecord(records);
		boolean del = false;
		for (int i = 0; i < records.size(); i++) {
			if (record.getCalltime().equals(records.get(i).getCalltime()) 
					&& record.getName().equals(records.get(i).getName()) 
					&& record.getNumber().equals(records.get(i).getNumber()) 
					&& record.getType() == records.get(i).getType()) {
				records.remove(i);
				del = true;
				break;
			}
		}
		if (del == false) {
			return;
		}

		String file = DBManager.DB_PATH + "/" + "bt_record.txt";
    	File f = new File(file);
    	if(f.exists())
    	f.delete();
        try {
  			new File(file).createNewFile();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}

		try {
			FileWriter writer = new FileWriter(file, true);
	    	for (int i = 0; i < records.size(); i++) {
	    		String type = String.valueOf(records.get(i).getType());
	    		String name = records.get(i).getName();
	    		String num  = records.get(i).getNumber();
	    		String time = records.get(i).getCalltime();
	    		String content = type + "|" + name + "|" + num + "|" + time + System.getProperty("line.separator");
	            writer.write(content);
			}
            if(writer != null){
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}

    public static void appendfile(String fileName, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	public void stopalldownload(){
		stoppbapdownload();
		stopsmsdownload();
	}
	public void stoppbapdownload(){
        if(mPBSyncProxy != null){
            mPBSyncProxy.StopDownload(PBSyncStruct.NUM_OF_BT_PBAP_SYNC_PATH);
        }
	}
	public void stopsmsdownload(){
        if(mSmsSyncProxy != null){
            mSmsSyncProxy.StopDownload();
        }
	}
    public boolean isdownloadidle(){
    	if (mPBSyncProxy == null) {
			Log.d("TEST","isdownloadidle null");
			return true;
		}
		if(pbDownloading)
			return pbDownloading;
		Log.d("TEST","isdownloadidle mPBSyncProxy.GetSyncState() "+mPBSyncProxy.GetSyncState());
    	return mPBSyncProxy.GetSyncState() == PBSyncStruct.STATE_IDLE;
    }
	private boolean pbDownloading;
    public void downloadphonebook(){
    	if (!mbConnectedHFP || mPBSyncProxy == null) {
			return;
		}
        if (mPBSyncProxy.GetSyncState() == PBSyncStruct.STATE_IDLE) {
			Log.d("TEST","downloadphonebook writeLastDownLoadMac "+mConnectedHFPDevice.getAddress());
        	writeLastDownLoadMac(mConnectedHFPDevice.getAddress());
			 int nPath = PBSyncStruct.BT_PBAP_SYNC_PATH_ALL_PHONEBOOK;
			 mPBSyncProxy.SetRemoteDevice(mConnectedHFPDevice,nPath);
				mPBSyncProxy.SetIndStep(200);
				mPBSyncProxy.SetDldStep(200);
			 mPBSyncProxy.StartDownload(nPath, false);
			 pbDownloading = true;
        }
    }
    public void downloadphonebookwhenconnect(String addr){
    	if (addr == null || addr.isEmpty()) {
    		return;
		}
    	String LastDownldAddress = readLastDownLoadMac();
        if ((!LastDownldAddress.isEmpty() && !addr.equals(LastDownldAddress)) || LastDownldAddress.isEmpty()){
        	downloadphonebook();
		}
    }

	//TODO calling
    private void startquerycalllisttimer(){
    	if (mTimerQuerycalllist == null) {
        	mTimerQuerycalllist = new TimerTask() {
    			@Override
    			public void run() {
    				m_HandsfreeAdpter.Bluetooth_Hf_QueryCalllist();
    			}
    		};
    		new Timer().schedule(mTimerQuerycalllist, 0, 2500);
		}
    }
    private void stopquerycalllisttimer(){
    	if (mTimerQuerycalllist != null) {
			mTimerQuerycalllist.cancel();
			mTimerQuerycalllist = null;
		}
    }
    public void initisspeakingfirst(){
    	isspeakingfirst = true;
    }
    public boolean isspeakingfirst(){
    	return isspeakingfirst;
    }
    public boolean iscompletecall(){
    	return iscompletecall;
    }
    public String getlastcallnum(){
    	return mlastcallnum;
    }
	public boolean isaudioatphone(){
    	if (m_HandsfreeAdpter == null) {
			return true;
		}
		return m_HandsfreeAdpter.Bluetooth_Hf_IsAudioTransferTowardsAG();
	}
	public int getlastcalltype(){
		return mcallstatus_last;
	}
	public boolean iscallidle(){
		return GetCallValue() == 0 && GetCallsetupValue() == 0;
	//	return !isoutgoing() && !isincoming() && !isspeaking();
	}
    public int GetCallValue(){
    	if (m_HandsfreeAdpter == null) {
			return 0;
		}
        return m_HandsfreeAdpter.Bluetooth_Hf_GetCallValue();
    }
    
    public int GetCallsetupValue(){
    	if (m_HandsfreeAdpter == null) {
			return 0;
		}
        return m_HandsfreeAdpter.Bluetooth_Hf_GetCallsetupValue();
    }
    public void addnewcall(int call_type, String call_name, String call_number, String call_time){
        if (null != mPBSyncProxy) {
            mPBSyncProxy.AddCall(call_type, call_name, call_number, call_time);
        }
    }

	public String GetCallingNum(){
		if (m_HandsfreeAdpter == null) {
			return "unkown";
		}
		return m_HandsfreeAdpter.Bluetooth_Hf_GetCallingNumber();
	}
	public String GetCallingName(){
		String name = GetNameByTelExectly(GetCallingNum());
		if (name == null || name.isEmpty()) {
			name = GetCallingNum();
		}
		return name;
	}
	public String GetCallingName(String num){
		String name = GetNameByTelExectly(num);
		if (name == null || name.isEmpty()) {
			name = num;
		}
		return name;
	}
	public String GetNameByTelExectly(String num){
		if (mPBSyncProxy == null || !mbConnectedHFP || num == null || num.isEmpty()) {
			return "";
		}
		return mPBSyncProxy.GetNameByTelExectly(mConnectedHFPDevice.getAddress(), num);
	}
    public boolean isoutgoing(){
    	return mcallstatus == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING;
    }
    public boolean isincoming(){
    	return mcallstatus == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING;
    }
    public boolean isspeaking(){
    	return mcallstatus == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING;
    }
    public boolean switchcallaudio(boolean bphone){
    	//FIXME:12003 静音时来电转至手机端
		if(0 == SystemProperties.getInt("persist.sys.current_volume", 0)){
			bphone = true;
		}
		L.i("switchcallaudio bphone : " + bphone);
		//
    	if (m_HandsfreeAdpter == null) {
			return false;
		}
    	m_HandsfreeAdpter.Bluetooth_Hf_SwithAudioTransfer(bphone);
    	return bphone;
    }
    public void terminatecall(){
    	if (m_HandsfreeAdpter == null) {
			return;
		}
    	m_HandsfreeAdpter.Bluetooth_Hf_TerminatePhoneCall();
    }
    public void answercall(){
    	if (m_HandsfreeAdpter == null) {
			return;
		}
    	m_HandsfreeAdpter.Bluetooth_Hf_AcceptIncommingCall();
    }
	public void recall(){
    	if (m_HandsfreeAdpter == null || mlastcallnum == null || mlastcallnum.isEmpty()) {
			return;
		}
    	m_HandsfreeAdpter.Bluetooth_Hf_DialNumber(mlastcallnum);
	}
    public void call(String num){
    	if (m_HandsfreeAdpter == null || num == null || num.isEmpty()) {
			return;
		}
    	if (num.equals("*") || num.equals("#")) {
			BluetoothDevice test= getRemoteDevice("66:66:88:88:88:88");
        	String pin = getDevicePin();
        	test.setPin(CachedBluetoothDevice.convertPinToBytes(pin));
			connect(test);
		}else{
	    	m_HandsfreeAdpter.Bluetooth_Hf_DialNumber(num);
		}
    	mlastcallnum = num;
    }
    public void SendDTMFCode(String dtmf_code){
    	if (m_HandsfreeAdpter == null || dtmf_code == null || dtmf_code.isEmpty()) {
			return;
		}
    	m_HandsfreeAdpter.Bluetooth_Hf_SendDTMFCode(dtmf_code);
    }

	//TODO music
    public void setA2DPAudio(boolean bActive){
        if (mAvrcpCtPlayerManage != null) {
		    mAvrcpCtPlayerManage.setA2dpAudioActive(bActive);
	    }
    }
    public void musicresume(){
        if (mAvrcpCtPlayerManage != null) {
		    mAvrcpCtPlayerManage.notifyResume();	
	    }
    }
    public void musicrevoke(){
        if (mAvrcpCtPlayerManage != null) {
		    mAvrcpCtPlayerManage.notifyRevoke();	
	    }
    }
	public boolean isA2DPPlaying(){
		return bPlaying;
	}
	public String getmediaTitle(){
	    return mediaTitleInfo;
	}
	public String getmediaArtist(){
	    return mediaArtistInfo;
	}
	public String getmediaAlbum(){
	    return mediaAlbumInfo;
	}
	private static void stopavrcptimer(){
    	if (avrcpTimer != null) {
			avrcpTimer.cancel();
			avrcpTimer = null;
		}
	}
    private static Object Obj_avrcpcmd = new Object();
    private static int recentCmd = -10;
    private static Handler handler = new Handler();
    private static Runnable runnable = new Runnable() {
		public void run() {
			recentCmd = -10;
		}
	};
    public static void sendAvrcpCommand(int cmd){
    	L.e("get avrcpcmd: " + cmd + "  recentCmd:" + recentCmd);
    	if(recentCmd == cmd){
    		return;
    	}else{
    		recentCmd = cmd;
    		handler.removeCallbacks(runnable);
    		handler.postDelayed(runnable,1000);
    	}
    	L.e("send avrcpcmd: " + cmd  + "  recentCmd:" + recentCmd);
		synchronized (Obj_avrcpcmd) {
			stopavrcptimer();
	    	getInstance().limit_avrcp = 0;
	    	avrcpcmd = cmd;
			avrcpTimer = new TimerTask() {
				@Override
				public void run() {
					synchronized (Obj_avrcpcmd) {
						if (getInstance().mAvrcpCtPlayerManage == null) {
							Log.i(TAG, "mAvrcpCtPlayerManage == null");
							getInstance().mAvrcpCtPlayerManage = BluetoothAvrcpCtPlayerManage.getInstance(getInstance().mContext);
							if (getInstance().mAvrcpCtPlayerManage == null) {
								Log.i(TAG, "BluetoothAvrcpCtPlayerManage.getInstance(getInstance().mContext) == null");
							}
							
						}
						if (!getInstance().isbtopened()) {
							stopavrcptimer();
			            	return;
						}
						if (!getInstance().mbConnectedAvrcp) {
							if (getInstance().mAvrcpCtPlayerManage != null) {
								stopavrcptimer();
								if (getInstance().mbConnectedA2DP) {
									getInstance().connectAVRCP();
								}
							}
							return;
						}
						BluetoothAvrcpCtPlayerManage manager_avrcp = getInstance().mAvrcpCtPlayerManage;
						if (getInstance().mbConnectedAvrcp && manager_avrcp != null) {
			            	if (bpermit || getInstance().limit_avrcp > 10) {
				            	bpermit = false;
				//            	if (getInstance().bPlaying && avrcpcmd != BluetoothAvrcpCtPlayerManage.CMD_PLAY
				 //           			|| !getInstance().bPlaying && avrcpcmd != BluetoothAvrcpCtPlayerManage.CMD_PAUSE) {
					            	manager_avrcp.sendAvrcpCommand(avrcpcmd);
									Log.i(TAG, "manager_avrcp.sendAvrcpCommand:" + avrcpcmd);
				//				}
				    			stopavrcptimer();
							}else{
								getInstance().limit_avrcp++;
							}
						}else{
							stopavrcptimer();
						}
					}
				}
			};
			new Timer().schedule(avrcpTimer, 0, 100);
		}
    }
    
    
    public void setDeviceName(String name){
    	SystemProperties.set(PERSYS_BT_DEVICE,  name);
    	mEditor.putString("bluetooth.name", name).commit();
    }
    //TODO config
	public String getDeviceName(){
        return SystemProperties.get(PERSYS_BT_DEVICE,  "");
		/*
		if (mLocalManager == null || mLocalManager.getBluetoothAdapter() == null) {
	        return SystemProperties.get(PERSYS_BT_DEVICE,  "");
		}
        String devicename = mLocalManager.getBluetoothAdapter().getName();
        return SystemProperties.get(PERSYS_BT_DEVICE,  devicename);
        */
    }
	
	public void setDevicePin(String pin){
    	SystemProperties.set(PERSYS_BT_PAIR,  pin);
    	mEditor.putString("bluetooth.pin", pin).commit();
    }
	public String getDevicePin(){
        return SystemProperties.get(PERSYS_BT_PAIR, "0000");
    }
	public boolean isautoanswer(){
    	return SystemProperties.getBoolean(PERSYS_BT_AUTO_ANSWER, false);
	}
    private boolean isAutoconnect(){
    	return SystemProperties.getBoolean(PERSYS_BT_AUTO_CONNECT, false);
    }
    private void setCustomDeviceName(){
         String mySerial = new String(android.os.Build.SERIAL);
         int nStart = mySerial.length();
         mySerial = ReadPreDevicename(mContext) + mySerial.substring(nStart-6,nStart).toUpperCase();
     	 SystemProperties.set(PERSYS_BT_DEVICE, mySerial);
     	 mLocalManager.getBluetoothAdapter().setName(mySerial);
    }
	public boolean readBTDiscoveryData() {
	//	return SystemProperties.getBoolean("persist.sys.bt_discover", true);
		return true;
	}
    private void WriteInitedDevicename(Context context){
        SharedPreferences.Editor sharedata = context.getSharedPreferences("BT_memory", 0).edit();
        sharedata.putBoolean("BT_inited_devicename", true);
        sharedata.commit();
    }
    private String ReadPreDevicename(Context context){
    	return SystemProperties.get(PERSYS_BT_DEVICE_NAME_OEM, "YC_");
    }
    private boolean ReadInitedDevicename(Context context){
    	return !SystemProperties.getBoolean(PERSYS_BT_DEVICE_AUTO_SET, false);
  //      SharedPreferences sharedata = context.getSharedPreferences("BT_memory", 0);
     //   return true;//sharedata.getBoolean("BT_inited_devicename", false);
    }
    private void writeLastConnectedMac(String BT_ADDR){
    	if (BT_ADDR == null) {
			return;
		}
        SharedPreferences.Editor sharedata = mContext.getSharedPreferences("BT_memory", Context.MODE_PRIVATE).edit();
        sharedata.putString("BT_LastConnectedMac", BT_ADDR);
        sharedata.commit();
    }
    private String readLastConnectedMac(){
        SharedPreferences sharedata = mContext.getSharedPreferences("BT_memory", Context.MODE_PRIVATE);
        return sharedata.getString("BT_LastConnectedMac", "");
    }

	public void writeLastBtState(boolean bopen){
		bopen = true;
		if (isaccoff || ispoweroff) {
			return;
		}
        SharedPreferences.Editor sharedata = mContext.getSharedPreferences("BT_memory", 0).edit();
        sharedata.putBoolean("BT_Isopen", bopen);
        sharedata.commit();
	}
	public boolean readLastBtState(){
        SharedPreferences sharedata = mContext.getSharedPreferences("BT_memory", Context.MODE_PRIVATE);
        return true;//sharedata.getBoolean("BT_Isopen", true);
	}
	public void writeLastDownLoadMac(String mac){
		if (mac != null) {
	        SharedPreferences.Editor sharedata = mContext.getSharedPreferences("BT_memory", 0).edit();
	        sharedata.putString("BT_LastDownLoadMac", mac);
	        sharedata.commit();
		}
	}
	public String readLastDownLoadMac(){
        SharedPreferences sharedata = mContext.getSharedPreferences("BT_memory", Context.MODE_PRIVATE);
        return sharedata.getString("BT_LastDownLoadMac", "");
	}
	public void writeaccpoweroffstate(){
        SharedPreferences.Editor sharedata = mContext.getSharedPreferences("BT_memory", 0).edit();
        sharedata.putBoolean("BT_Isaccoff", isaccoff);
        sharedata.putBoolean("BT_Ispoweroff", ispoweroff);
        sharedata.commit();
	}
	public void readaccpoweroffstate(){
        SharedPreferences sharedata = mContext.getSharedPreferences("BT_memory", Context.MODE_PRIVATE);
        isaccoff = sharedata.getBoolean("BT_Isaccoff", false);
        ispoweroff = sharedata.getBoolean("BT_Ispoweroff", false);
	}
	
	
	//TODO handleaction
    public void handlebootcompleted(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        setBtDebugLogLevel(mContext);
        if (btAdapter != null && btAdapter.isEnabled()) {
        	Log.i(TAG, "getDefaultAdapter is enable");
            btAdapter.setPclFile(mContext.getResources().openRawResource(R.raw.mtkprotocol));
            setServicestate(mContext, true);
            isbtopen = true;
        }else{
        	Log.i(TAG, "getDefaultAdapter is disable");
        }
        mLocalManager = LocalBluetoothManager.getInstance(mContext);
        switchbt(readLastBtState());
        MainBluetoothActivity.inittelzonedatabase(mContext);
        Intent serviceIntent = new Intent(mContext, BTService.class);
        mContext.startService(serviceIntent);
    }
    public void handleaccoff(Intent intent){
    	readaccpoweroffstate();
    	if (intent.getAction().equals(MCU_ACTION_ACC_OFF)) {
			isaccoff = true;
		}else if (intent.getAction().equals(MCU_ACTION_ACC_ON)) {
			isaccoff = false;
		}else if (intent.getAction().equals(ACTION_QB_POWEROFF)) {
			ispoweroff = true;
		}else if (intent.getAction().equals(ACTION_QB_POWERON)) {
			ispoweroff = false;
		}
    	writeaccpoweroffstate();
    	Log.i(TAG, "handleaccoff: isaccoff=" + isaccoff + " ispoweroff=" + ispoweroff);
    }
    public void handleLinkModeChanged(Intent intent, Context context) {
        int linkmode = intent.getIntExtra(BluetoothAdapter.EXTRA_LINK_MODE,BluetoothAdapter.LINK_MODE_DISCONNECTABLE);
        if (linkmode == BluetoothAdapter.LINK_MODE_DISCONNECTABLE) {
            if(mbConnectedHFP){
                disconnect();
            }
        }
    }
    public void handleDownLoadPhoneFinish(Intent intent){
    	pbDownloading = false;
    	if (mConnectedHFPDevice != null) {
			Log.d("TEST","handleDownLoadPhoneFinish writeLastDownLoadMac "+mConnectedHFPDevice.getAddress());
        	writeLastDownLoadMac(mConnectedHFPDevice.getAddress());
		}
    }

    public void handleCallStateUpdate(Intent intent) {
        int callstatus = intent.getIntExtra(BluetoothHfUtility.EXTRA_CALL_STATE, 0);
        Log.i( TAG,"handleCallStateUpdate:" + callstatus);
        if (callstatus == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING && mcallstatus != BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING) {
        	mcallstatus_last = mcallstatus;
		}
        if (callstatus != BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING
        		&& callstatus != BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING
        		&& callstatus != BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING) {
        	stopquerycalllisttimer();
			if (!isspeakingfirst) {
				iscompletecall = true;
			}else{
				iscompletecall = false;
			}
        	switch (mcallstatus) {
			case BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING:
				if (mcallstatus_last == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING) {
					mcallstatus_last = TYPE_OUTGOING;
				}else if (mcallstatus_last == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING) {
					mcallstatus_last = TYPE_INCOMING;
				}
				break;
			case BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING:
	        	mcallstatus_last = TYPE_OUTGOING;
				break;
			case BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING:
				mcallstatus_last = TYPE_MISSED;
				break;

			default:
				break;
			}
		}else if (callstatus == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING
        		|| callstatus == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING) {
			startquerycalllisttimer();
			isspeakingfirst = false;
		}else if (callstatus == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING) {
			startquerycalllisttimer();
			startchangephoneaudiotimer();
		}
        mcallstatus = callstatus;
		mObjSaveData.setBtCallSts(mcallstatus);
		mObjSaveData.setBtCallNum(Bluetooth.getInstance().GetCallingNum());
    }
    public void handlecallnumchanged(Intent intent){
    	String num = intent.getStringExtra(BluetoothHfAtHandler.EXTRA_NEW_PHONE_NUMBER);
		mObjSaveData.setBtCallNum(num);
    }
    public void handlescochange(Intent intent){
    	if (intent.getAction().equals(BluetoothHfService.ACTION_SCO_STATE_CHANGED)) {
			stopchangephoneaudiotimer();
		}
    }
    public void handleMusicNotify(Intent intent){
    	if (intent.getAction().equals(BluetoothAvrcpCtPlayerManage.ACTION_PLAYBACK_DATA_UPDATE)) {
			byte play_status = intent.getByteExtra(BluetoothAvrcpCtPlayerManage.PLAYBACK_STATUS, (byte) 0);
	        switch (play_status) {
	        case BluetoothAvrcpCtPlayerManage.PLAYING:
	        	if (!bPlaying) {
	        		Log.i(TAG, "musicestateisplaying");
					bPlaying = true;
					bpermit = true;
					setA2DPAudio(true);
				}
	            break;

	        case BluetoothAvrcpCtPlayerManage.PAUSED:
	        case BluetoothAvrcpCtPlayerManage.STOPPED:
	        	if (bPlaying) {
	        		Log.i(TAG, "musicestateispaused");
		        	bPlaying = false;
		        	bpermit = true;
					setA2DPAudio(false);
				}
	            break;
	        }
    	}else if (intent.getAction().equals(BluetoothAvrcpCtPlayerManage.ACTION_MEDIA_DATA_UPDATE)) {
			bpermit = true;
			mediaTitleInfo = intent.getStringExtra(BluetoothAvrcpCtPlayerManage.MEDIA_TITLE);
			mediaArtistInfo = intent.getStringExtra(BluetoothAvrcpCtPlayerManage.MEDIA_ARTIST);
			mediaAlbumInfo = intent.getStringExtra(BluetoothAvrcpCtPlayerManage.MEDIA_ALBUM);
			Log.e(TAG, "id3info:" + mediaTitleInfo +
					"|" + mediaArtistInfo + "|" + mediaAlbumInfo);
		}
    }
    public void handleBondChanged(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if(device == null)  
            return ;
        int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
        if (bondState == BluetoothDevice.BOND_BONDED) {
			macPairing = "";
            CachedBluetoothDevice cachedDevice = CachedBluetoothDevice.getCachedDevice(mContext, device);
            cachedDevice.connect(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF); 
            cachedDevice.connect(LocalBluetoothProfileManager.BTProfile.Bluetooth_A2DP_SINK); 
            cachedDevice.connect(LocalBluetoothProfileManager.BTProfile.Bluetooth_AVRCP_CT); 
        }else if (bondState == BluetoothDevice.BOND_NONE) {
			macPairing = "";
		}
    }
    public void handleAdapterStateUpdate(Intent intent, Context context) {
        int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch (btState) {
        case BluetoothAdapter.STATE_ON:	   
        	Log.i(TAG, "handleadapterstateon");        
        	mLocalManager = LocalBluetoothManager.getInstance(context);
            BluetoothAdapter.getDefaultAdapter().setPclFile(context.getResources().openRawResource(R.raw.mtkprotocol));
            setServicestate(context, true); 
            if(mLocalManager!=null){
	            if(readBTDiscoveryData()){
	            	mLocalManager.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,  -1);
	            }
	            else{
	            	mLocalManager.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE);
	            }
	            if (!ReadInitedDevicename(context)) {
	            	setCustomDeviceName();
		            WriteInitedDevicename(context);
				}else{
			        if (mLocalManager.getBluetoothAdapter() != null) {
			            String devicename = mLocalManager.getBluetoothAdapter().getName();
			            devicename = SystemProperties.get(PERSYS_BT_DEVICE,  devicename);
			       	  	mLocalManager.getBluetoothAdapter().setName(devicename);
			            SystemProperties.set(PERSYS_BT_DEVICE,  devicename);
					}
				}
            }
            break;
        case BluetoothAdapter.STATE_OFF:
        	Log.i(TAG, "handleadapterstateoff");
            mbConnectedHFP = false;
            mbConnectedA2DP = false;
            mbConnectedAvrcp = false;
            mConnectedHFPDevice = null;
            mConnectedA2DPDevice = null;

            if (mLocalManager != null) {
            	Log.i(TAG, "BluetoothAdapter.STATE_OFF and setenable");
            	mLocalManager.setBluetoothEnabled(true);
			}
            
     //       mLocalManager = null;
            break;					

        default:
            break;
        }			
    }
    public void handleBluetoothPairingRequest(Intent intent) {
    	if (isConnected()) {
			return;
		}
        BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT,BluetoothDevice.ERROR);
        
        if (type == BluetoothDevice.PAIRING_VARIANT_PIN) {
        	macPairing = remoteDevice.getAddress();
        	stopAutoconnect();
        	String pin = getDevicePin();
            remoteDevice.setPin(CachedBluetoothDevice.convertPinToBytes(pin));
        } else if (type == BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION) {
            remoteDevice.setPairingConfirmation(true);
        } else if (type == CachedBluetoothDevice.PAIRING_VARIANT_CONSENT) {
            remoteDevice.setPairingConfirmation(true);
        } else if (type == CachedBluetoothDevice.PAIRING_VARIANT_PASSKEY) {
        	String pin = getDevicePin();
            CachedBluetoothDevice.setPasskey(remoteDevice, Integer.parseInt(pin));
        } else if (type == CachedBluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY) {
        } else if (type == CachedBluetoothDevice.PAIRING_VARIANT_OOB_CONSENT) {
        	CachedBluetoothDevice.setRemoteOutOfBandData(remoteDevice);
        } else {
        }
    }
    public void handleHDMIAction(Intent intent, Context context) {
        String action = intent.getAction();
        if (action.equals(HDMI.ACTION_MHLHID_GET_CONNECT_STATE)) {
            boolean hidConnected = false;
            if (m_HIDAdpter != null) {
                try {
                    hidConnected = m_HIDAdpter.isConnected();
                }  catch (RemoteException e) {
                    e.printStackTrace();
                    return ;
                }
            }
            Intent stateIntent = new Intent(HDMI.ACTION_BTHID_CONNECT_STATE);
            stateIntent.putExtra(HDMI.HID_CONNECT_STATE, hidConnected);
            if(context != null)context.sendBroadcast(stateIntent);
            
        }else if (action.equals(HDMI.ACTION_MHLHID_GET_RESOLUTION)) {
            boolean hidConnected = false;
            if (m_HIDAdpter != null) {
                try {
                    hidConnected = m_HIDAdpter.isConnected();
                }  catch (RemoteException e) {
                    e.printStackTrace();
                    return ;
                }
            }
            if (hidConnected && m_HIDAdpter != null) {
                int resolution = 0;
                String addr = null;
                try {
                    addr = m_HIDAdpter.getConnectedAddr();
                }  catch (RemoteException e) {
                    e.printStackTrace();
                    return ;
                }
                SharedPreferences sharedata = context.getSharedPreferences("BT_MHLHID_RESOLUTION", 0);
                resolution = sharedata.getInt(addr, 0);
                Intent resolutionIntent = new Intent(HDMI.ACTION_BTHID_RESOLUTION);
                resolutionIntent.putExtra(HDMI.PHONE_RESOLUTION, resolution);
                if(context != null)context.sendBroadcast(resolutionIntent);
            } else {
                Intent stateIntent = new Intent(HDMI.ACTION_BTHID_CONNECT_STATE);
                stateIntent.putExtra(HDMI.HID_CONNECT_STATE, false);
                if(context != null)context.sendBroadcast(stateIntent);
            }
            
        }else if (action.equals(HDMI.ACTION_MHLHID_NOTIFY_RESOLUTION)) {           
            boolean hidConnected = false;
            if (m_HIDAdpter != null) {
                try {
                    hidConnected = m_HIDAdpter.isConnected();
                }  catch (RemoteException e) {
                    e.printStackTrace();
                    return ;
                }
            }
            if (hidConnected && m_HIDAdpter != null) {
                int resolution = 0;
                String addr = null;
                try {
                    addr = m_HIDAdpter.getConnectedAddr();
                }  catch (RemoteException e) {
                    e.printStackTrace();
                    return ;
                }
                SharedPreferences.Editor editor = context.getSharedPreferences("BT_MHLHID_RESOLUTION", 0).edit();
                resolution = intent.getIntExtra(HDMI.PHONE_RESOLUTION, 0);
                editor.putInt(addr, resolution);
                editor.commit();
            } else {
                Intent stateIntent = new Intent(HDMI.ACTION_BTHID_CONNECT_STATE);
                stateIntent.putExtra(HDMI.HID_CONNECT_STATE, false);
                if(context != null)context.sendBroadcast(stateIntent);
            }
            
        }else if (action.equals(HDMI.ACTION_MHLHID_MOUSE_DATA)) {
            if (m_HIDAdpter != null) {
                boolean bButton = intent.getBooleanExtra(HDMI.MOUSE_BTN_DOWN, false);
                int iX = intent.getIntExtra(HDMI.MOUSE_DATA_X, 0);
                int iY = intent.getIntExtra(HDMI.MOUSE_DATA_Y, 0);
                try {
                    m_HIDAdpter.sendMouseData(bButton, iX, iY);
                }  catch (RemoteException e) {
                    e.printStackTrace();
                    return ;
                }
            }            
        }else if (action.equals(HDMI.ACTION_MHLHID_CONTROL_DATA)) {            
            if (m_HIDAdpter != null) {
                int iCmd = intent.getIntExtra(HDMI.HID_CMD, 0);
                try {
                    m_HIDAdpter.sendControlData(iCmd);
                }  catch (RemoteException e) {
                    e.printStackTrace();
                    return ;
                }
            }
        }
    }
    public void handleProfileStateUpdate(Intent intent) {
        BTProfile profilename = (BTProfile)intent.getSerializableExtra(LocalBluetoothProfileManager.EXTRA_PROFILE);
        int profilestate = intent.getIntExtra(
        		LocalBluetoothProfileManager.EXTRA_NEW_STATE,LocalBluetoothProfileManager.STATE_DISCONNECTED);
        if(profilename == null) {
             return;
        }
        if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)) {
            String addr = intent.getStringExtra(LocalBluetoothProfileManager.HF_DEVICE_ADDR);
            if (profilestate != LocalBluetoothProfileManager.STATE_DISCONNECTING) {
            	isHFPdisconnecting = false;
			}
            switch (profilestate) {
            case LocalBluetoothProfileManager.STATE_ENABLED:
            	Log.i(TAG, "handlehfpenable");
            	setconnectable(true);
            	isbtopen = true;
            	writeLastBtState(true);
                mLocalManager = LocalBluetoothManager.getInstance(mContext);
                m_HandsfreeAdpter = BluetoothHfAdapter.getInstance(mContext);
                startautoconnect(false);
                break;
            case LocalBluetoothProfileManager.STATE_DISABLED:
            	Log.i(TAG, "handlehfpdisable");
            	macPairing = "";
            	isbtopen = false;
            	writeLastBtState(false);
                if (m_HandsfreeAdpter != null) {
                    m_HandsfreeAdpter.close();
                    m_HandsfreeAdpter = null;
                }
                uninitdata();
                if (mLocalManager != null) {
                	Log.i(TAG, "hfpdisable and setenable");
                	mLocalManager.setBluetoothEnabled(true);
				}
                break;
            case LocalBluetoothProfileManager.STATE_CONNECTED:
            	Log.e(TAG, "handlehfpconnected");
            	setconnectable(false);
            	reconnectA2DP = 2;
            	mbConnectedHFP = true;
                mConnectedHFPDevice = getRemoteDevice(addr);
                whenconnected = System.currentTimeMillis();
      //          AtcSettings.Audio.SetBTDump(1);
      //          AtcSettings.Audio.SetSphDbgFilePath("data4write/bluetooth", 1024);
                stopAutoconnect();
				pbDownloading = false;
                //downloadphonebookwhenconnect(addr);
                if (!addr.equals(readLastConnectedMac())) {
                    writeLastConnectedMac(addr);
                	File f = new File(DBManager.DB_PATH + "/" + "bt_record.txt");
                	if(f.exists())
                	f.delete();
				}
                break;
            case LocalBluetoothProfileManager.STATE_DISCONNECTED:  
            	Log.e(TAG, "handlehfpdisconnected");
            	setconnectable(true);
            	stopalldownload();
            	reconnectA2DP = 0;
            	reconnectAVRCP = 0;
     //       	disconnectA2DP();
            	mbConnectedHFP = false;
                mConnectedHFPDevice = null;
                stopquerycalllisttimer();
                startautoconnect(true);
                break;
            case LocalBluetoothProfileManager.STATE_DISCONNECTING:
            	Log.e(TAG, "handlehfpdisconnecting");
            	isHFPdisconnecting = true;
            	break;
            case LocalBluetoothProfileManager.STATE_CONNECTING:
            	Log.e(TAG, "handlehfpconnecting");
            	break;
            }           
        }
        else if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_A2DP_SINK)) {
            String addr = intent.getStringExtra(LocalBluetoothProfileManager.A2DPSink_DEVICE_ADDR);
            switch (profilestate) {
            case LocalBluetoothProfileManager.STATE_ENABLED:                

                break;
            case LocalBluetoothProfileManager.STATE_DISABLED:

                break;
            case LocalBluetoothProfileManager.STATE_CONNECTED:
            	Log.e(TAG, "handlea2dpconnected");
            	stopAutoconnect();
            	reconnectAVRCP = 2;
            	mbConnectedA2DP = true;
            	mConnectedA2DPDevice = getRemoteDevice(addr);
            	PhoneLinkReceiver.notifyA2dpStatus(mContext, 1);
                break;
            case LocalBluetoothProfileManager.STATE_DISCONNECTED: 
            	Log.e(TAG, "handlea2dpdisconnected");
            	PhoneLinkReceiver.notifyA2dpStatus(mContext, 0);
            	mbConnectedA2DP = false;
            	mConnectedA2DPDevice = null;
            	if (mbConnectedHFP && reconnectA2DP > 0 && !isHFPdisconnecting) {
	//				connectA2DP();
					reconnectA2DP--;
				}
            	bPlaying = false;
                mediaTitleInfo = "";
                mediaAlbumInfo = "";
                mediaArtistInfo = "";
                startautoconnect(true);
                break;
            }           
        }
        else if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_AVRCP_CT)) {
            switch (profilestate) {
            case LocalBluetoothProfileManager.STATE_ENABLED:
            	Log.i(TAG, "Bluetooth_AVRCP_CT enable");
                mAvrcpCtPlayerManage = BluetoothAvrcpCtPlayerManage.getInstance(mContext);
                startautoconnect(false);
                startregresume();
                break;
            case LocalBluetoothProfileManager.STATE_DISABLED:
            	Log.i(TAG, "Bluetooth_AVRCP_CT disable");
                if(mAvrcpCtPlayerManage != null) {
                    mAvrcpCtPlayerManage.close();
                    mAvrcpCtPlayerManage = null;
                }
                break;
            case LocalBluetoothProfileManager.STATE_CONNECTED:
            	Log.e(TAG, "handleavrcpconnected");
            	mbConnectedAvrcp = true;
                break;
            case LocalBluetoothProfileManager.STATE_DISCONNECTED:
            	Log.e(TAG, "handleavrcpdisconnected");
            	if (mbConnectedA2DP && reconnectAVRCP > 0 && !isHFPdisconnecting) {
    //        		connectAVRCP();
					reconnectAVRCP--;
				}
            	mbConnectedAvrcp = false;
                startautoconnect(true);
                break;
            }
        }
         else if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_PBAP_Client)) {
            switch (profilestate) {
            case LocalBluetoothProfileManager.STATE_ENABLED:                	
                mPBSyncProxy = PBSyncManager.getInstance(mContext);
                startautoconnect(false);
                break;
            case LocalBluetoothProfileManager.STATE_DISABLED:
                if(mPBSyncProxy != null) {
                    mPBSyncProxy.close();
                    mPBSyncProxy = null;
                }
                break;
            }
        }
        else if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HID)) {
            switch (profilestate) {
            case LocalBluetoothProfileManager.STATE_ENABLED:                	
                m_HIDAdpter = BluetoothHidAdapter.getInstance(mContext);
                startautoconnect(false);
                break;
            case LocalBluetoothProfileManager.STATE_DISABLED:
                if(m_HIDAdpter != null) {
                    m_HIDAdpter.close();
                    m_HIDAdpter = null;
                }
                break;
             case LocalBluetoothProfileManager.STATE_CONNECTED:      
            	 Log.i(TAG, "Bluetooth_HID connected");
                if (m_HIDAdpter != null) m_HIDAdpter.startHIDSendThread(10);
               
                break;
            case LocalBluetoothProfileManager.STATE_DISCONNECTED:
           	 Log.i(TAG, "Bluetooth_HID disconnected");
                if (m_HIDAdpter != null) m_HIDAdpter.stopHIDSendThread();
                break;
            }

            boolean hidConnected = false;
            if (m_HIDAdpter != null) {
                try {
                    hidConnected = m_HIDAdpter.isConnected();
                }  catch (RemoteException e) {
                    e.printStackTrace();
                    return ;
                }
            }
            Intent stateIntent = new Intent(HDMI.ACTION_BTHID_CONNECT_STATE);
            stateIntent.putExtra(HDMI.HID_CONNECT_STATE, hidConnected);
            mContext.sendBroadcast(stateIntent);            
        }       
       
    }
    
    //TODO serve
    private void setBtDebugLogLevel(Context context) {
        SharedPreferences sharedata = context.getSharedPreferences("BT_Debug_Log_Level", 0);
        boolean file_exsit = sharedata.getBoolean("file_exsit", false);
        if(false == file_exsit) {
            SharedPreferences.Editor editor = context.getSharedPreferences("BT_Debug_Log_Level", 0).edit();
            editor.putBoolean("file_exsit", true);
            editor.putBoolean("debug_all", BtEmulatorDebug.mDebugAll);
            editor.putBoolean("debug_music", BtEmulatorDebug.mDebugMusic);
            editor.putBoolean("debug_settings", BtEmulatorDebug.mDebugSettings);
            editor.putBoolean("debug_pb", BtEmulatorDebug.mDebugPB);
            editor.putBoolean("debug_sms", BtEmulatorDebug.mDebugSMS);
            editor.putBoolean("debug_hf", BtEmulatorDebug.mDebugHF);
            editor.putBoolean("debug_spp", BtEmulatorDebug.mDebugSPP);
            editor.commit();
        } else {
            BtEmulatorDebug.mDebugAll = sharedata.getBoolean("debug_all", true);
            BtEmulatorDebug.mDebugMusic = sharedata.getBoolean("debug_music", true);
            BtEmulatorDebug.mDebugSettings = sharedata.getBoolean("debug_settings", true);
            BtEmulatorDebug.mDebugPB = sharedata.getBoolean("debug_pb", true);
            BtEmulatorDebug.mDebugSMS = sharedata.getBoolean("debug_sms", true);
            BtEmulatorDebug.mDebugHF = sharedata.getBoolean("debug_hf", true);
            BtEmulatorDebug.mDebugSPP = sharedata.getBoolean("debug_spp", true);
        }
    }

    private void startService( Context context, String serviceClass ){
        try {
            context.startService( new Intent(context, Class.forName(serviceClass)) );
        }
        catch( ClassNotFoundException ex ){
        }
    }
    private void stopService( Context context, String serviceClass ){
        try {
            context.stopService( new Intent(context, Class.forName(serviceClass)) );
        }
        catch( ClassNotFoundException ex ){
        }
    }
	public void setServicestate(Context context, boolean isEnable) {
		if (isEnable) {
			this.startService( context, HF_SERVICE_CLASS ); 
			this.startService( context, PBAPCLIENT_SERVICE_CLASS );
			this.startService( context, A2DPSINK_SERVICE_CLASS );
			this.startService( context, AVRCPCT_SERVICE_CLASS );                    
			this.startService(context, PBSYNC_SERVICE_CLASS);			
			this.startService(context, HID_SERVICE_CLASS);
			
		}else {
			this.stopService(context, PBSYNC_SERVICE_CLASS);
			this.stopService(context, HF_SERVICE_CLASS ); 
			this.stopService(context, PBAPCLIENT_SERVICE_CLASS );
			this.stopService(context, A2DPSINK_SERVICE_CLASS );
			this.stopService(context, AVRCPCT_SERVICE_CLASS );
			this.stopService(context, HID_SERVICE_CLASS );	
		}
	}
	//标记为手动打开activity,给dial用的
	public static void handStartActivity(Context context,Intent intent){
    	L.i("lifecycle handStartActivity");
    	intent.putExtra("handStart", true);
    	context.startActivity(intent);
	}
	public static void handStartActivity(Context context,Class<?> cls){
		Intent intent = new Intent(context,cls);
		handStartActivity(context,intent);
	}
	public static void handStartActivity(Activity activity,Intent intent){
		L.i("lifecycle handStartActivity");
		intent.putExtra("handStart", true);
		activity.startActivity(intent);
	}
	public static void handStartActivity(Activity activity,Class<?> cls){
		Intent intent = new Intent(activity,cls);
		handStartActivity(activity,intent);
	}

	public void handleCallStateUpdate(Intent intent, Context context) {
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
			L.i(this.getClass().getSimpleName() + " startDialActivity true");
           context.startActivity(intentPhoneCall);
		} catch (ActivityNotFoundException e) {
		}
	}

	public void callStateChange(Intent intent,Context context){
		map.put(0,intent);
		map.put(1,context);
		L.i(this.getClass().getName() + "  callStateChange");
		if(callStateChangeEmitter != null) {
			callStateChangeEmitter.onNext(map);
		}
	}
}
