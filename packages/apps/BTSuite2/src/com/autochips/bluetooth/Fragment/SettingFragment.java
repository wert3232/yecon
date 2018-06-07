/**
 * @author Administrator
 *
 */
package com.autochips.bluetooth.Fragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nfore.android.bt.servicemanager.UiServiceCommand;

import com.autochips.bluetooth.Bluetooth;
import com.autochips.bluetooth.BluetoothReceiver;
import com.autochips.bluetooth.BtEmulatorDebug;
import com.autochips.bluetooth.CachedBluetoothDevice;
import com.autochips.bluetooth.CmnUtil;
import com.autochips.bluetooth.LocalBluetoothProfileManager;
import com.autochips.bluetooth.MainBluetoothActivity;
import com.autochips.bluetooth.R;
import com.autochips.bluetooth.LocalBluetoothManager;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.MainBluetoothActivity.MyTouchListener;
import com.autochips.bluetooth.util.L;
import com.autochips.bluetooth.view.ConfirmDialog;
import com.autochips.bluetooth.TuoXianDialActivity;
import com.tuoxianui.tools.AtTimerHelpr;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.constant.YeconConstants;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
public class SettingFragment extends BaseFragment implements OnClickListener,OnEditorActionListener{
	public static final String CREATEVIEW_GO_DIALVIEW = "createview_go_dialview";
    private ArrayList<HashMap<String, Object>> mDeviceList;
    private SimpleAdapter mPairedDevicesArrayAdapter;
    private BluetoothDevice mSelectedDevice;
	private LocalBluetoothManager mLocalManager;
    
    TextView mTVisempty;
    private ListView mDeviceListView;
    private EditText mTxtdevicename;
    private EditText mTxtdevicepin;
    private ImageButton mIBtnConnect;
    private ImageButton mIBtnDisConnect;
    private View connectBtn;
    private View disconnectBtn;
    private View deleteBtn;
    private ImageButton mIBtnSearch;
    private ImageButton mIBtnDelete; 
	private TextView mIBtnScan;
    private LinearLayout mScanLayout = null;
    private int mSelectInx = -1;
    private boolean isOpenDialView = true;
    private AtTimerHelpr mAtTimerHelpr;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		initData();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.bt_settings, container, false);
		
		mLocalManager = LocalBluetoothManager.getInstance(getActivity());
		initView(mRootView);
		setViewData();
		Bundle bundle = getArguments();
		BluetoothReceiver.registerNotifyHandler(uiHandler);
		
		if(bundle != null && bundle.containsKey(CREATEVIEW_GO_DIALVIEW)){
			isOpenDialView = bundle.getBoolean(CREATEVIEW_GO_DIALVIEW);
		}else{
			isOpenDialView = false;
		}
		L.e("isGoDialView: " + isOpenDialView + " " + (bundle == null ));
		if(bundle != null){
			L.e("bundle.getBoolean CREATEVIEW_GO_DIALVIEW: " + bundle.getBoolean(CREATEVIEW_GO_DIALVIEW));
			if(bundle.containsKey("rename")&&bundle.getBoolean("rename")){
				L.e("bundle.getBoolean rename: " + bundle.getBoolean("rename"));
				mTxtdevicename.setFocusableInTouchMode(true);
			}
		}
		
		if(//Bluetooth.getInstance().isConnected() && 
		MainBluetoothActivity.goMusicFragment){
			//getActivity().sendOrderedBroadcast(new Intent("action.bt.switch.fragment.music"),null);
			//MainBluetoothActivity.goMusicFragment = false;
		}else if(Bluetooth.getInstance().isConnected() && isOpenDialView && !Bluetooth.getInstance().isspeaking()){
			L.i(this.getClass().getSimpleName() + " startDialActivity true onCreateView");
			startDialActivity();
		}
		MainBluetoothActivity.goMusicFragment = false;
		
		judgeConnectBtnEnableState();
		
		/**FIXME:11103 一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
        mAtTimerHelpr.setCallBack( new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				Log.d("TEST","SettingFragment doIt");
				Rect rect = new Rect();
				getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
				if(Bluetooth.getInstance().iscallidle() && !Bluetooth.getInstance().isDiscoverying() && !MainBluetoothActivity.isPowerOff
					&&getActivity().getWindow().getDecorView().getHeight()==rect.bottom){
					if(Bluetooth.getInstance().isA2DPPlaying()){ 
						getActivity().sendOrderedBroadcast(new Intent("action.bt.switch.fragment.music"),null);
					}
					else{
						Intent intent = new Intent(Intent.ACTION_MAIN);
				        intent.addCategory(Intent.CATEGORY_HOME);
				        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				        startActivity(intent);
						//
			        	Intent intent2 = new Intent(Context.ACTION_SOURCE_STACK_DO_OUT_AUTOEXIT);
			        	intent2.putExtra(Context.EXTRA_APK_PACKAGENAME, YeconConstants.BLUETOOTH_PACKAGE_NAME);
			        	intent2.putExtra(Context.EXTRA_APK_ACTIVITY, YeconConstants.BLUETOOTH_START_ACTIVITY);
			        	getActivity().sendOrderedBroadcast(intent2,null);
			        	
			        }
				}else{
					mAtTimerHelpr.start(10);
				}
			}
		} );**/
		
		return mRootView;
	}
	
	
	
	@Override
	public void onDestroy() {
		((InputMethodManager) ((MainBluetoothActivity) getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mTxtdevicename.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		BluetoothReceiver.unregisterNotifyHandler(uiHandler);
		super.onDestroy();
	}
	@Override
	public void onResume() {
	    showDevInfo();
	    judgeConnectBtnEnableState();
		super.onResume();
		
		try {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.start(10);
			((MainBluetoothActivity) getActivity()).registerMyTouchListener(touchListener);
		} catch (Exception e) {
			L.e(e.toString());
		}
		
		if(Bluetooth.getInstance().isConnected()){
			/*FIXME:11149 -
			if(!Bluetooth.getInstance().iscallidle()){
				startDialActivity();
			}*/
		}
		
	}
	
	@Override
	public void onPause() {
		try {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.stop();
			((MainBluetoothActivity) getActivity()).unRegisterMyTouchListener(touchListener);
		} catch (Exception e) {
			L.e(e.toString());
		}
		super.onPause();
	}
	
	private MyTouchListener touchListener = new MyTouchListener() {
		
		@Override
		public void onTouchEvent(MotionEvent event) {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.reset();
		}
	};
	
	private ConfirmDialog connectDialog;
	private ConfirmDialog disconnectDialog;
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.txt_devicename:{
		}
		break;
		case R.id.txt_devicepin:{
		}
		break;
		case R.id.btn_setting_back:
			//if(Bluetooth.getInstance().isConnected()){	
//				getActivity().findViewById(R.id.tab_dial).performClick();
//				startActivity(new Intent(this.getActivity(),TuoXianDialActivity.class));
			//Log.d("TEST","startDialActivity onClick");
				startDialActivity();
			//}else{
				//getActivity().onBackPressed();
				
				/*Intent intent = new Intent(Intent.ACTION_MAIN);
		        intent.addCategory(Intent.CATEGORY_HOME);
		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		        startActivity(intent);*/
				//
	        	/*Intent intent2 = new Intent(Context.ACTION_SOURCE_STACK_DO_OUT_AUTOEXIT);
	        	intent2.putExtra(Context.EXTRA_APK_PACKAGENAME, YeconConstants.BLUETOOTH_PACKAGE_NAME);
	        	intent2.putExtra(Context.EXTRA_APK_ACTIVITY, YeconConstants.BLUETOOTH_START_ACTIVITY);
	        	getActivity().sendOrderedBroadcast(intent2,null);*/
			//}
			break;
		case R.id.conect:
        case R.id.btn_connect_setting:
        	{	
        		Bluetooth.getInstance().stopDiscovery();
	        	if(connectDialog == null){
		        	connectDialog = new ConfirmDialog(getActivity());
		        	connectDialog.setContent(getResources().getString(R.string.connect_bluetooth));
		        	ConfirmDialog.Callback mConnectCallback = new ConfirmDialog.Callback() {
						@Override
						public void onCallOK(Dialog dialog, TextView ok, TextView no) {
							if (Bluetooth.getInstance().isA2DPconnected() && Bluetooth.getInstance().isHFPconnected()
				        			|| mSelectedDevice == null) {
								return;
							}else if (Bluetooth.getInstance().isA2DPconnected()) {
								Bluetooth.getInstance().connectHFP();
							}else if (Bluetooth.getInstance().isHFPconnected()) {
								Bluetooth.getInstance().connectA2DP();
							}else{
					        	Bluetooth.getInstance().connect(mSelectedDevice);
							}
						}
						
						@Override
						public void onCallCanncel(Dialog dialog, TextView ok, TextView no) {
							dialog.dismiss();
						}
					};
					connectDialog.setCallBack(mConnectCallback);
					connectDialog.show();
	        	}else{
	        		connectDialog.show();
	        	}
	        	/*if (Bluetooth.getInstance().isA2DPconnected() && Bluetooth.getInstance().isHFPconnected()
	        			|| mSelectedDevice == null) {
					return;
				}else if (Bluetooth.getInstance().isA2DPconnected()) {
					Bluetooth.getInstance().connectHFP();
				}else if (Bluetooth.getInstance().isHFPconnected()) {
					Bluetooth.getInstance().connectA2DP();
				}else{
		        	Bluetooth.getInstance().connect(mSelectedDevice);
				}*/
	        }
	        break;
        case R.id.disconect:
        case R.id.btn_disconnect_setting:
        {	
        	//if(disconnectDialog == null){
	        	disconnectDialog = new ConfirmDialog(getActivity());
	        	disconnectDialog.setContent(getResources().getString(R.string.disconnect_bluetooth));
	        	ConfirmDialog.Callback mDisconnectCallback = new ConfirmDialog.Callback() {
					@Override
					public void onCallOK(Dialog dialog, TextView ok, TextView no) {
						if (!Bluetooth.getInstance().isConnected()) {
							return;
						}
						Bluetooth.getInstance().delephonebook(true);
			        	Bluetooth.getInstance().disconnect();
					}
					
					@Override
					public void onCallCanncel(Dialog dialog, TextView ok, TextView no) {
						dialog.dismiss();
					}
				};
				disconnectDialog.setCallBack(mDisconnectCallback);
				disconnectDialog.show();
        	//}else{
        	//	disconnectDialog.show();
        	//}
        	/*if (!Bluetooth.getInstance().isConnected()) {
				return;
			}
        	Bluetooth.getInstance().disconnect();*/
        }
        break;
        case R.id.scan_bluetooth:
        case R.id.btn_search_setting:
        	if (Bluetooth.getInstance().isConnected() || !Bluetooth.getInstance().getPairingmac().isEmpty()) {
				return;
			}
        	if (!Bluetooth.getInstance().isDiscoverying()) {
                showPairedList();
			}
        	Bluetooth.getInstance().doDiscovery();
        	break;
        case R.id.delete:
        case R.id.btn_delete_setting:
	        	disconnectDialog = new ConfirmDialog(getActivity());
	        	disconnectDialog.setContent(getResources().getString(R.string.confirm_del_one_device));
	        	ConfirmDialog.Callback mDisconnectCallback = new ConfirmDialog.Callback() {
					@Override
					public void onCallOK(Dialog dialog, TextView ok, TextView no) {
			            if(mSelectedDevice != null) {
			            	Bluetooth.getInstance().stopDiscovery();
			            	Bluetooth.getInstance().stopalldownload();
			                if(mSelectedDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
			                	if (Bluetooth.getInstance().isHFPconnected() && mSelectedDevice.equals(Bluetooth.getInstance().getConnectedHFPDevice())) {
			                		CmnUtil.showToast(getActivity(), R.string.disconnectbeforedelete);
									return;
								}
			                    if(!CachedBluetoothDevice.removeBond(mSelectedDevice)){
			                    }
			                }
			            	if (!mDeviceList.isEmpty() && mSelectInx != -1) {
			            		View v = mDeviceListView.getChildAt(mSelectInx);
			                	if (v != null) {
			                		v.setBackgroundResource(R.drawable.bt_device_normal);
								}
			                	mDeviceList.remove(mSelectInx);
			    			}
			            	if (mDeviceList.isEmpty()){
				            	mSelectInx = -1;
				            	mSelectedDevice = null;
							}else if (mDeviceList.size() > mSelectInx) {
								HashMap<String, Object> bt_map = mDeviceList.get(mSelectInx);
								String bt_addr = bt_map.get("remote_device_macaddr").toString();
								mSelectedDevice = Bluetooth.getInstance().getRemoteDevice(bt_addr);
							}else{
								onlistnocheck();
							}
			            	notifylistviewdataupdate();
			            }
            			judgeConnectBtnEnableState();
					}
					
					@Override
					public void onCallCanncel(Dialog dialog, TextView ok, TextView no) {
						dialog.dismiss();
					}
				};
				disconnectDialog.setCallBack(mDisconnectCallback);
				disconnectDialog.show();
        	break;
		default:
			break;
		}
	}
	private void onlistnocheck(){
		if (Bluetooth.getInstance().isConnected()) {
			for (int i = 0; i < mDeviceList.size(); i++) {
				HashMap<String, Object> bt_map = mDeviceList.get(i);
				String bt_addr = bt_map.get("remote_device_macaddr").toString();
				if (Bluetooth.getInstance().getConnectedHFPAddr().equals(bt_addr)) {
					mSelectInx = i;
					mSelectedDevice = Bluetooth.getInstance().getRemoteDevice(bt_addr);
					return;
				}
			}
		}else if (mDeviceList.size() > 0){
			mSelectInx = 0;
			mSelectedDevice = Bluetooth.getInstance().getRemoteDevice(mDeviceList.get(0).get("remote_device_macaddr").toString());
		}
	}
	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BluetoothReceiver.MSG_BT_STATUS_NOTIFY) {
				Intent intent = (Intent) msg.obj;
				String recievedAction = intent.getAction();
				if (recievedAction.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			        int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
					handleStateChanged(btState);
				}else if (recievedAction.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
					handleBondChanged(intent);
				}else if (recievedAction.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
					notifylistviewdataupdate();
				}else if (recievedAction.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)) {
					BTProfile profilename = (BTProfile) intent.getSerializableExtra(LocalBluetoothProfileManager.EXTRA_PROFILE);
					if (profilename == null)
						return;
					if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)) {
						handleConnectStateChanged(intent, 0);
					}else if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_A2DP_SINK)){
						handleConnectStateChanged(intent, 1);
					}
					judgeConnectBtnEnableState();
				}else if (recievedAction.equals(BluetoothDevice.ACTION_FOUND)) {
					handleDeviceFound(intent);
				}else if (recievedAction.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
					mScanLayout.setVisibility(View.VISIBLE);
		        	mTVisempty.setVisibility(View.INVISIBLE);
				} else if (recievedAction.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
					mScanLayout.setVisibility(View.GONE);
					notifylistviewdataupdate();
				} else if (recievedAction.equals(BluetoothDevice.ACTION_NAME_CHANGED)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String current_bt_addr = device.getAddress();
					for (int i = 0; i < mDeviceList.size(); i++) {
						HashMap<String, Object> bt_map = mDeviceList.get(i);
						String bt_addr = bt_map.get("remote_device_macaddr").toString();
						if (bt_addr.equals(current_bt_addr)) {
							bt_map.put("remote_device_name",device.getName());
							notifylistviewdataupdate();
						}
					}
				}
			}
		}
	};

    public void handleDeviceFound(Intent intent){
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		for (int i = 0; i < mDeviceList.size(); i++) {
			String addr = (String) mDeviceList.get(i).get("remote_device_macaddr");
			String name = (String) mDeviceList.get(i).get("remote_device_name");
			if (device.getAddress().equals(addr)) {
				if (!name.equals(addr)) {
					return;
				}
				mDeviceList.get(i).put("remote_device_name",device.getName());
				notifylistviewdataupdate();
				return;
			}
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("remote_device_name", device.getName());
		map.put("remote_connect_status",getResources().getString(R.string.bt_status_unpair));
		map.put("remote_device_macaddr", device.getAddress());
		mDeviceList.add(map);
		notifylistviewdataupdate();
    }
    //flag: 0:hf  1:a2dp
    private void handleConnectStateChanged(Intent intent, int flag){
        int bondstate;
        BluetoothDevice remoteDevice;
        String addr;
        if(0==flag){
        	 addr = intent.getStringExtra(LocalBluetoothProfileManager.HF_DEVICE_ADDR);
        }
        else{
        	 addr = intent.getStringExtra(LocalBluetoothProfileManager.A2DPSink_DEVICE_ADDR);
        }
        int profilestate = intent.getIntExtra(LocalBluetoothProfileManager.EXTRA_NEW_STATE,LocalBluetoothProfileManager.STATE_DISCONNECTED);
        if(profilestate == LocalBluetoothProfileManager.STATE_CONNECTED){
        	showPairedList();
        	judgeConnectBtnEnableState();
        	if (Bluetooth.getInstance().isConnected()) {	
//        		startActivity(new Intent(this.getActivity(),TuoXianDialActivity.class));
			Log.d("TEST","startDialActivity handleConnectStateChanged");
        		startDialActivity();
			}
        	/*
            remoteDevice = Bluetooth.getInstance().getRemoteDevice(addr);
		    boolean bFound = false;
            for(int i = 0; i < mDeviceList.size(); i++) {
                if(mDeviceList.get(i).get("remote_device_macaddr").equals(addr)) {
                	if (addr.equals(mDeviceList.get(i).get("remote_device_name"))) {
                        mDeviceList.get(i).put("remote_device_name", remoteDevice.getName());
					}
                	bFound = true;
                    mDeviceList.get(i).put("remote_connect_status", btProfileContectedStateHint(remoteDevice));
                    notifylistviewdataupdate();
                    return;
                }
            }
            if (!bFound) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("remote_device_name", remoteDevice.getName());
                map.put("remote_connect_status", btProfileContectedStateHint(remoteDevice));
                map.put("remote_device_macaddr", remoteDevice.getAddress());
                mDeviceList.add(map);
                notifylistviewdataupdate();
			}
            */
        }else if(profilestate == LocalBluetoothProfileManager.STATE_DISCONNECTED){
            remoteDevice = Bluetooth.getInstance().getRemoteDevice(addr);
            bondstate = remoteDevice.getBondState();
            for(int i = 0; i < mDeviceList.size(); i++) {
                if(mDeviceList.get(i).get("remote_device_macaddr").equals(addr)) {   
                    if(bondstate != BluetoothDevice.BOND_NONE){    
                    	mDeviceList.get(i).put("remote_connect_status", btProfileContectedStateHint(remoteDevice));
                    	notifylistviewdataupdate();
                    return;
                }
            }
            judgeConnectBtnEnableState();
        }
    }
}
    private void handleBondChanged(Intent intent) {
		int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String strBTRemoteAddr = device.getAddress();
		boolean bFound = false;
        if (bondState == BluetoothDevice.BOND_BONDED) {
            for(int i = 0; i < mDeviceList.size(); i++) {
                if(mDeviceList.get(i).get("remote_device_macaddr").equals(strBTRemoteAddr)) {
                	mDeviceList.get(i).put("remote_connect_status", btProfileContectedStateHint(device));
                	notifylistviewdataupdate();
                    bFound = true;
                    break;
                }
            }
            if (!bFound) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("remote_device_name", device.getName());
                map.put("remote_connect_status", getResources().getString(R.string.bt_status_paired));
                map.put("remote_device_macaddr", device.getAddress());
                mDeviceList.add(map);
            }
        }
        notifylistviewdataupdate();
    }
    private void handleStateChanged(int state) {
    	switch (state) {
        case BluetoothAdapter.STATE_ON:
        	showPairedList();
        	break;
        case BluetoothAdapter.STATE_OFF:	
            mDeviceList.clear();
            notifylistviewdataupdate();
        	break;
		default:
			break;
		}
    }
    final class ViewHolder_DeviceListItem {
        public TextView name;
        public TextView connecttip;
        public ImageView imgcon;
    }
    class Adapter_DeviceListHighlight extends SimpleAdapter
    {
        private Context mContext;
    	 public Adapter_DeviceListHighlight(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
            this.mContext = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder_DeviceListItem holder = null;
			 if (convertView != null) {
				 holder = (ViewHolder_DeviceListItem)convertView.getTag();
			 }else{
				holder = new ViewHolder_DeviceListItem();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.device_listitem, null);
				holder.imgcon = (ImageView)convertView.findViewById(R.id.device_icon);
				holder.name = (TextView)convertView.findViewById(R.id.item_remote_device_name);
				holder.connecttip = (TextView)convertView.findViewById(R.id.item_remote_connect_status);
				convertView.setTag(holder);
			 }
    		 String name = "";
    		 String constate = "";
    		 String mac = "";
        	 name = (String)mDeviceList.get(position).get("remote_device_name");
        	 constate = (String)mDeviceList.get(position).get("remote_connect_status");
        	 mac = (String)mDeviceList.get(position).get("remote_device_macaddr");
        	 String strTmp = getResources().getString(R.string.bt_status_connected);

			if (Bluetooth.getInstance().getconnectedmac().equals(mac) && !Bluetooth.getInstance().getconnectedmac().isEmpty()) {
				holder.name.setTextColor(Color.parseColor("#ffb400"));
				holder.imgcon.setVisibility(View.VISIBLE);
			}else{
				holder.name.setTextColor(Color.WHITE);
				holder.imgcon.setVisibility(View.GONE);
			}
			if (position == mSelectInx) {
				convertView.setBackgroundResource(R.drawable.bt_device_highlight);
			}else{
				convertView.setBackgroundResource(R.drawable.bt_device_normal);
			}
             holder.name.setText(name);
             holder.connecttip.setText(constate);
             String macPairing = Bluetooth.getInstance().getPairingmac();
             if (!macPairing.isEmpty() && mac.equals(macPairing)) {
                 holder.connecttip.setText(getResources().getString(R.string.bluetooth_pairing));
             }
    		 return convertView;
    	 }
    };
	private void setViewData(){
        mPairedDevicesArrayAdapter = new Adapter_DeviceListHighlight(getActivity(), mDeviceList, R.layout.device_listitem, 
                new String[] { "remote_device_name", "remote_connect_status", "remote_device_macaddr" }, 
                new int[] { R.id.item_remote_device_name, R.id.item_remote_connect_status, R.id.item_remote_device_macaddr});

        mDeviceListView.setAdapter(mPairedDevicesArrayAdapter);
        mDeviceListView.setOnItemClickListener(mDeviceClickListener);
        showPairedList();
        mIBtnConnect.setOnClickListener(this);
        mIBtnDisConnect.setOnClickListener(this);
        mIBtnSearch.setOnClickListener(this);
        mIBtnDelete.setOnClickListener(this);
	}
	private void initData(){
		mDeviceList = new ArrayList<HashMap<String, Object>>();
	}
	private void initView(View rootView) {
		mTVisempty = (TextView)rootView.findViewById(R.id.pairedisempty);
		mDeviceListView = (ListView)rootView.findViewById(R.id.bluetooth_paired_devices);
		mTxtdevicename = (EditText)rootView.findViewById(R.id.txt_devicename);
		mTxtdevicepin = (EditText)rootView.findViewById(R.id.txt_devicepin);
		mIBtnConnect = (ImageButton)rootView.findViewById(R.id.btn_connect_setting);
		mIBtnDisConnect = (ImageButton)rootView.findViewById(R.id.btn_disconnect_setting);
		connectBtn = rootView.findViewById(R.id.conect);
		disconnectBtn = rootView.findViewById(R.id.disconect);
		mIBtnScan = (TextView)rootView.findViewById(R.id.scan_bluetooth);
		mIBtnSearch = (ImageButton)rootView.findViewById(R.id.btn_search_setting);
		mIBtnDelete = (ImageButton)rootView.findViewById(R.id.btn_delete_setting);
        mScanLayout = (LinearLayout)rootView.findViewById(R.id.scan_device_layout);
        mTxtdevicename.setOnEditorActionListener(this);
        mTxtdevicepin.setOnEditorActionListener(this);
        mTxtdevicename.setOnClickListener(this);
        /*mTxtdevicepin.setOnClickListener(this);*/
        mTxtdevicename.setOnTouchListener(new View.OnTouchListener() {	
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				//点击后可以编辑
				mTxtdevicename.setFocusableInTouchMode(true);
				return false;
			}
		});
        mTxtdevicepin.setOnTouchListener(new View.OnTouchListener() {	
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				//点击后可以编辑
//				mTxtdevicepin.setFocusableInTouchMode(true);
				return false;
			}
		});
        mTxtdevicename.clearFocus();
        mTxtdevicepin.clearFocus();
        
        rootView.findViewById(R.id.btn_setting_back).setOnClickListener(this);
        rootView.findViewById(R.id.scan_bluetooth).setOnClickListener(this);
        rootView.findViewById(R.id.conect).setOnClickListener(this);
        rootView.findViewById(R.id.disconect).setOnClickListener(this);
        deleteBtn = rootView.findViewById(R.id.delete);
		deleteBtn.setOnClickListener(this);
	}
	

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            HashMap<String, Object> deviceInfo = mDeviceList.get(arg2);
            String address = (String)deviceInfo.get("remote_device_macaddr");
            BluetoothDevice device_select = Bluetooth.getInstance().getRemoteDevice(address);
            mSelectedDevice = device_select;
            mSelectInx = arg2;
            for(int i = 0;i < mDeviceListView.getCount();i++){
	           	 View child = mDeviceListView.getChildAt(i);
	           	 if (child != null) {
			            child.setBackgroundResource(R.drawable.bt_device_normal);
				}
            }
		    v.setBackgroundResource(R.drawable.bt_device_highlight);
		    judgeConnectBtnEnableState();
        }
    };
    private String btProfileContectedStateHint(Object odevice){
    	BluetoothDevice device = null;
    	if (!(odevice instanceof BluetoothDevice)) {
			return null;
		}else{
    		device = (BluetoothDevice)odevice;
    	}
 //   	String strTmp = getActivity().getResources().getString(R.string.bt_status_connected);
    	String strTmp = "";
    	if (!Bluetooth.getInstance().isConnected()
    			|| (Bluetooth.getInstance().isHFPconnected() && !Bluetooth.getInstance().getConnectedHFPDevice().equals(device))
    			|| (Bluetooth.getInstance().isA2DPconnected() && !Bluetooth.getInstance().getConnectedA2DPDevice().equals(device))){
    		strTmp = getActivity().getResources().getString(R.string.bt_status_paired);
    	}
    	else if(!Bluetooth.getInstance().isHFPconnected()){
    		strTmp += "(" + getActivity().getResources().getString(R.string.bt_status_nophone) + ")";
    	}
    	else if(!Bluetooth.getInstance().isA2DPconnected()){
    		strTmp += "(" + getActivity().getResources().getString(R.string.bt_status_nomedia) + ")";
    	}
    	return strTmp;
    }

    private void notifylistviewdataupdate()
    {
    	if (mDeviceList.isEmpty()) {
        	mTVisempty.setVisibility(View.VISIBLE);
		}else{
        	mTVisempty.setVisibility(View.INVISIBLE);
		}
        mPairedDevicesArrayAdapter.notifyDataSetChanged();
    }
    
	private void showPairedList(){
		if (Bluetooth.getInstance() == null) {
			return;
		}
		Set<BluetoothDevice> pairedDevices = null;
		pairedDevices = Bluetooth.getInstance().getPairList();
		mDeviceList.clear();
        if (pairedDevices != null && pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("remote_device_name", device.getName());
                map.put("remote_connect_status", btProfileContectedStateHint(device));
                map.put("remote_device_macaddr", device.getAddress());
                if (Bluetooth.getInstance().isConnected() 
                		&& Bluetooth.getInstance().getconnectedmac().equals(device.getAddress())) {
                	mDeviceList.add(0, map);
                	mSelectInx = 0;
				}else{
					mDeviceList.add(map);
				}
            }
        }
		onlistnocheck();
		
        notifylistviewdataupdate();
	}
	private void showDevInfo(){
    	mTxtdevicename.setText(Bluetooth.getInstance().getDeviceName());
    	mTxtdevicepin.setText(Bluetooth.getInstance().getDevicePin());
	}
	
	private void judgeConnectBtnEnableState(){
		boolean isConnected = Bluetooth.getInstance().isConnected();
		connectBtn.setEnabled(false);
		mIBtnScan.setEnabled(false);
		disconnectBtn.setEnabled(false);
		if (isConnected){
			disconnectBtn.setEnabled(true);
		}else{
			mIBtnScan.setEnabled(true);
			if(mSelectedDevice != null){
				connectBtn.setEnabled(true);
			}
		}
		deleteBtn.setEnabled(mSelectedDevice!=null);
	}
	
	//added all
	@Override
	public boolean onEditorAction(TextView tx, int actionId, KeyEvent event) {
		 switch(actionId){  
	        case EditorInfo.IME_ACTION_DONE:{
	        	switch (tx.getId()) {
					case R.id.txt_devicename:{
						String name = mTxtdevicename.getText().toString();
						Log.e("callLog", "set bluetooth name : " + name);
						mTxtdevicename.clearFocus();
					}
					break;
					case R.id.txt_devicepin:{
						String name = mTxtdevicepin.getText().toString();
						mTxtdevicepin.clearFocus();
					}
					break;
	        	}
	        	try {
		        	if(tx.getId() == R.id.txt_devicename){
		        		String name = mTxtdevicename.getText().toString();	
	        			
	        			/* this way set device name fail
	        			 * IBinder getServiceObj = connectService();
	        			UiServiceCommand mBlueToothService = UiServiceCommand.Stub.asInterface((IBinder)getServiceObj);
	        			mBlueToothService.setBluetoothLocalAdapterName(name);*/
	        			if(Bluetooth.getInstance().isConnected()){
	        				//Toast.makeText(this.getActivity(), getResources().getString(R.string.please_break_bluetooth_connect_before_set_device_name), Toast.LENGTH_SHORT).show();
	        				//mTxtdevicename.setText(Bluetooth.getInstance().getDeviceName());
	        				//setFocusableInTouchMode(false,tx);
	        				//return false;
							if(!Bluetooth.getInstance().getDeviceName().equals(mTxtdevicename.getText().toString()))
								Bluetooth.getInstance().disconnect();
	        			}
	        			
	        			if(!TextUtils.isEmpty(name)){		
		        			SystemProperties.set("persist.sys.bt_device",  name);
//		        			BluetoothAdapter.getDefaultAdapter().setName(name);
	        				Bluetooth.getInstance(getActivity()).setDeviceName(name);
		        			Log.e("callLog", "bluetooth setName : " + name);
							if (!mLocalManager.getBluetoothAdapter().setName(name))
								Log.e("bt", "set local bluetooth adapter name fail");
		        		}else{
		        			mTxtdevicename.setText(Bluetooth.getInstance().getDeviceName());
		        			Toast.makeText(this.getActivity(), getResources().getString(R.string.device_is_no_empty), Toast.LENGTH_SHORT).show();
		        		}
	        			setFocusableInTouchMode(false,tx);
						
		        	}else if(tx.getId() == R.id.txt_devicepin){
		        		String pin = mTxtdevicepin.getText().toString();
		        		if(Bluetooth.getInstance().isConnected()){
	        				Toast.makeText(this.getActivity(), getResources().getString(R.string.please_break_bluetooth_connect_before_set_device_pin), Toast.LENGTH_SHORT).show();
	        				mTxtdevicepin.setText(Bluetooth.getInstance().getDevicePin());
	        				setFocusableInTouchMode(false,tx);
	        				return false;
		        		}
		        		
		        		if(!TextUtils.isEmpty(pin)){		
//		        			SystemProperties.set("persist.sys.bt_pair",  name);
		        			Bluetooth.getInstance(getActivity()).setDevicePin(pin);
		        			Log.e("callLog", "set bluetooth pin : " + pin);
		        		}else{
		        			mTxtdevicepin.setText(Bluetooth.getInstance().getDevicePin());
		        			Toast.makeText(this.getActivity(), getResources().getString(R.string.pin_is_no_empty), Toast.LENGTH_SHORT).show();
		        		}
		        		setFocusableInTouchMode(false,tx);
		        	}
	        	} catch (Exception e) {
					e.printStackTrace();
				}
		    break;  
	        }
		 }
        return false;
	}
	public void setFocusableInTouchMode(boolean touchMode,TextView editText){
		mTxtdevicename.clearFocus();
		mTxtdevicepin.clearFocus();
		mTxtdevicename.setFocusableInTouchMode(touchMode);
		mTxtdevicepin.setFocusableInTouchMode(touchMode);
		hideKeyboard(editText.getWindowToken());
	}
	private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
	//no use
	public IBinder connectService(){
		Object object = new Object();
        Method getService; 
        IBinder serviceObj = null;
		try {
			getService = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
			serviceObj = (IBinder)getService.invoke(object, getServiceName());
		} catch (Exception e) {
			e.printStackTrace();
		}
        return serviceObj;
	}
	
	public String getServiceName(){
		return "adayo.bluetooth";
	}
	
	public void startDialActivity(){
		L.i(this.getClass().getSimpleName() + " startDialActivity true");
		//FIXME:12004
//			startActivity(new Intent(this.getActivity(),TuoXianDialActivity.class));
		Bluetooth.handStartActivity(this.getActivity(),TuoXianDialActivity.class);
		
		{
        	Intent intent = new Intent(Context.ACTION_SOURCE_STACK_AUTOEXIT);
        	intent.putExtra(Context.EXTRA_APK_PACKAGENAME, YeconConstants.BLUETOOTH_PACKAGE_NAME);
        	intent.putExtra(Context.EXTRA_APK_ACTIVITY, YeconConstants.BLUETOOTH_START_ACTIVITY);
        	getActivity().sendOrderedBroadcast(intent,null); 
        }
	}
}