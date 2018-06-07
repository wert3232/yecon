package com.yecon.carsetting.wifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yecon.carsetting.R;
import com.yecon.carsetting.wifi.Fragment_Wifi_connect.OnConnectActionListener;
import com.yecon.carsetting.wifi.Fragment_Wifi_password.OnCustomDialogListener;

public class Fragment_Wifi_search extends DialogFragment {

	private final static String TAG = "wifi_search";
	public Context mContext;
	FragmentManager mFragmentManager;

	private ProgressBar wifiRefreshing;
	private CheckBox checkBox;

	private WifiUtils mWifiUtils;
	private List<ScanResult> mScanResultList;
	private List<WifiItem> mListWifi = new ArrayList<WifiItem>();
	private ListView listView;

	private WifiItem mCurrentWifi = null;
	private WifiListAdapter wifiAdapter;
	private List<WifiConfiguration> mConfigList;
	private String wifiPassword = null;

	private DetailedState mLastState;

	public Fragment_Wifi_search() {

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	private void initData() {
		mContext = getActivity();
		mFragmentManager = getFragmentManager();

		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		filter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		filter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_wifi, container);
		initView(rootView);
		return rootView;
	}

	public void initView(View rootView) {

		wifiRefreshing = (ProgressBar) rootView.findViewById(R.id.wifi_refreshing_prog);
		listView = (ListView) rootView.findViewById(R.id.wifi_listview);
		wifiRefreshing.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		wifiAdapter = new WifiListAdapter(mContext, R.layout.list_wifi_item, mListWifi);
		listView.setAdapter(wifiAdapter);
		ListOnItemClickListener wifiListListener = new ListOnItemClickListener();
		listView.setOnItemClickListener(wifiListListener);

		mWifiUtils = new WifiUtils(mContext);
		mWifiUtils.WifiOpen();
		setTimer_WifiStartScan();
	}
	private Timer timer = null;
	public void setTimer_WifiStartScan() {
		if(timer != null){
			timer.cancel();
		}
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (mWifiUtils.WifiCheckState() == WifiManager.WIFI_STATE_ENABLED)
					WifiConfigInfo();
				if (mWifiUtils.getScanResults().size() > 0)
					timer.cancel();
			}
		};
		timer.schedule(task, 0, 1000);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(mBroadcastReceiver);
		if (mDlgDissmisListener != null)
			mDlgDissmisListener.onDlgDismiss();
		super.onDestroy();
	}

	public interface onDlgDismissListener {
		public void onDlgDismiss();
	}

	public static onDlgDismissListener mDlgDissmisListener;

	public static void setOnDlgDismissListener(onDlgDismissListener mL) {
		mDlgDissmisListener = mL;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
					|| WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
					|| WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
				refreshWifiList(mLastState);
			} else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				if (mWifiUtils.WifiCheckState() == WifiManager.WIFI_STATE_DISABLED) {
					wifiAdapter.clear();
					wifiAdapter.notifyDataSetChanged();

				} else if (mWifiUtils.WifiCheckState() == WifiManager.WIFI_STATE_ENABLED) {

				} else if (mWifiUtils.WifiCheckState() == WifiManager.WIFI_STATE_ENABLING) {
				} else if (mWifiUtils.WifiCheckState() == WifiManager.WIFI_STATE_DISABLING) {
				} else {
					String str = getResources().getString(R.string.wifi_WLAN_UNKNOWN);
					Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
				}
			} else if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)) {
			} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
				DetailedState state = ((NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
				mLastState = state;
				refreshWifiList(state);
			} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
				//这里接收到的状态有时候是错误的 delete by xuhh
//				SupplicantState supState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
//				DetailedState state = WifiInfo.getDetailedStateOf(supState);
//				mLastState = state;
//				refreshWifiList(state);
			} else if (WifiManager.NETWORK_IDS_CHANGED_ACTION.equals(action)) {
			} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
			}
		}
	};

	/**
	 * 获取wifi扫描结果，并按信号强度排列
	 */
	private void getScanResultOrderedList() {
		mListWifi.clear();
		mScanResultList = mWifiUtils.getScanResults();

		onShowListView();

		if (!isWifiConnected(mContext)
				&& (mWifiUtils.WifiCheckState() == WifiManager.WIFI_STATE_ENABLED)) {
			Log.w("getScanResultOrderedList", "scan result , change left display");
		}

		for (int i = 0; i < mScanResultList.size(); i++) {
			// 如果没有查到名字，不写入
			if (null == mScanResultList.get(i).SSID || mScanResultList.get(i).SSID == "") {
				continue;
			}

			WifiItem wifiItem = new WifiItem();
			wifiItem.setWifiName(mScanResultList.get(i).SSID);
			wifiItem.setWifiLock(true);
			wifiItem.setWifiBssid(mScanResultList.get(i).BSSID);

			String desc = "";
			String mDesc = mScanResultList.get(i).capabilities;
			if (mDesc.toUpperCase().contains("WPA-PSK")) {
				desc = "WPA";
			}
			if (mDesc.toUpperCase().contains("WPA2-PSK")) {
				desc = "WPA2";
			}
			if (mDesc.toUpperCase().contains("WPA-PSK") && mDesc.toUpperCase().contains("WPA2-PSK")) {
				desc = "WPA/WPA2";
			}
			if (mDesc.toUpperCase().contains("WPS")) {
				desc = desc + getResources().getString(R.string.wifi_WPA_Usable);
			}

			// 根据信号强度分级，写入强度icon的ID
			int level = mScanResultList.get(i).level;

			// 是否要密码
			if (desc.isEmpty()) {
				wifiItem.setWifiLock(false);
				desc = getResources().getString(R.string.wifi_WLAN_Unprotected);
				if (level <= 0 && level > -60) {
					wifiItem.setSignalCount(4);
					wifiItem.setImageLevelID(R.drawable.wifi_signal_4);
				} else if (level <= -60 && level > -72) {
					wifiItem.setSignalCount(3);
					wifiItem.setImageLevelID(R.drawable.wifi_signal_3);
				} else if (level <= -72 && level >= -83) {
					wifiItem.setSignalCount(2);
					wifiItem.setImageLevelID(R.drawable.wifi_signal_2);
				} else if (level < -83 && level > -95) {
					wifiItem.setSignalCount(1);
					wifiItem.setImageLevelID(R.drawable.wifi_signal_1);
				}
			} else {
				wifiItem.setWifiLock(true);
				if (level <= 0 && level > -60) {
					wifiItem.setSignalCount(4);
					wifiItem.setImageLevelID(R.drawable.wifi_signal_lock4);
				} else if (level <= -60 && level > -72) {
					wifiItem.setSignalCount(3);
					wifiItem.setImageLevelID(R.drawable.wifi_signal_lock3);
				} else if (level <= -72 && level >= -83) {
					wifiItem.setSignalCount(2);
					wifiItem.setImageLevelID(R.drawable.wifi_signal_lock2);
				} else if (level < -83 && level > -95) {
					wifiItem.setSignalCount(1);
					wifiItem.setImageLevelID(R.drawable.wifi_signal_lock1);
				}
			}
			wifiItem.setDescribes(desc);
			wifiItem.setConnected(desc); // 用于保存连接状态
			wifiItem.setSignalLevel(level);
			mListWifi.add(wifiItem);
		}

		// 比较信号强度
		Comparator<WifiItem> comp = new Comparator<WifiItem>() {
			@Override
			public int compare(WifiItem lhs, WifiItem rhs) {
				int level1 = lhs.getSignalLevel();
				int level2 = rhs.getSignalLevel();
				if (level1 < level2)
					return 1;
				if (level1 > level2)
					return -1;
				return 0;
			}
		};
		// 根据信号强弱排序
		Collections.sort(mListWifi, comp);
	}

	private boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	private void connectedFace() {
		String strState = getResources().getString(R.string.wifi_state_CONNECTED);
		mListWifi.get(0).setConnected(strState);
		Log.i("connectedFace", "connected Face");
	}

	/**
	 * 刷新wifi列表
	 */
	private void refreshWifiList(DetailedState dState) {
		// if wifi device don't enable
		if (mWifiUtils.WifiCheckState() != WifiManager.WIFI_STATE_ENABLED) {
			return;
		}

		String connectingWifissid;
		WifiInfo mWifiInfo = mWifiUtils.getConnectedInfo();
		connectingWifissid = mWifiInfo.getSSID();

		getScanResultOrderedList(); // 刷新列表
		// 把正在连接的热点，置于列表顶部，，有时会出现两个同名？？
		if (null != connectingWifissid && connectingWifissid != "" && dState != null) {
			mCurrentWifi = null;
			for (int i = 0; i < mListWifi.size(); i++) {
				int length = connectingWifissid.length() - 1;
				String ssid = connectingWifissid.substring(1, length);
				if (mListWifi.get(i).getWifiName().equals(ssid)) {
					mCurrentWifi = mListWifi.get(i);
					mListWifi.remove(i);
					break; // out of the loop (for)
				}
			}

			if (mCurrentWifi != null) {
				String strState;
				Log.d("DetailedState", "SUPPLICANT-----DetailedState_  " + dState);
				switch (dState) {
				case SCANNING:
					strState = getResources().getString(R.string.wifi_state_SCANNING);
					mCurrentWifi.setConnected(strState);
					mListWifi.add(0, mCurrentWifi);
					break;
				case CONNECTING:
					strState = getResources().getString(R.string.wifi_state_CONNECTING);
					mCurrentWifi.setConnected(strState);
					mListWifi.add(0, mCurrentWifi);
					break;
				case AUTHENTICATING:
					strState = getResources().getString(R.string.wifi_state_AUTHENTICATING);
					mCurrentWifi.setConnected(strState);
					mListWifi.add(0, mCurrentWifi);
					break;
				case OBTAINING_IPADDR:
					strState = getResources().getString(R.string.wifi_state_OBTAINING_IPADDR);
					mCurrentWifi.setConnected(strState);
					mListWifi.add(0, mCurrentWifi);
					break;
				case CONNECTED:
					strState = getResources().getString(R.string.wifi_state_CONNECTED);
					mCurrentWifi.setConnected(strState);
					mListWifi.add(0, mCurrentWifi);
					break;
				case DISCONNECTED:
					strState = getResources().getString(R.string.wifi_state_DISCONNECTED);
					mCurrentWifi.setConnected(strState); // 已断开
					break;
				case DISCONNECTING:
					strState = getResources().getString(R.string.wifi_state_DISCONNECTING);
					mCurrentWifi.setConnected(strState);
					mListWifi.add(0, mCurrentWifi);
					break;
				default:
					break;
				}

			}
			// 连接过程中，改变wifi强度图片
			if (mCurrentWifi != null && mCurrentWifi.getSignalCount() > 0) {
				//不能强制设为已连接。
				//connectedFace();
				switch (mCurrentWifi.getSignalCount()) {
				case 4:
					mCurrentWifi.setImageLevelID(R.drawable.wifi_signal_connect_4);
					break;
				case 3:
					mCurrentWifi.setImageLevelID(R.drawable.wifi_signal_connect_3);
					break;
				case 2:
					mCurrentWifi.setImageLevelID(R.drawable.wifi_signal_connect_2);
					break;
				case 1:
					mCurrentWifi.setImageLevelID(R.drawable.wifi_signal_connect_1);
					break;
				default:
					break;
				}
			}
			wifiAdapter.notifyDataSetChanged();
		}
	}

	private void onShowListView() {
		if (!mScanResultList.isEmpty()) {
			if (wifiRefreshing.getVisibility() == View.VISIBLE)
				wifiRefreshing.setVisibility(View.GONE);
			if (wifiRefreshing.getVisibility() != View.VISIBLE)
				listView.setVisibility(View.VISIBLE);
		}
	}

	class ListOnItemClickListener implements OnItemClickListener {
		String wifiItemSSID = null;
		private String wifiItemENC;
		private int wifiItemId;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			wifiItemENC = wifiAdapter.getItem(position).getDescribes();
			wifiItemSSID = wifiAdapter.getItem(position).getWifiName();
			wifiItemId = mWifiUtils.IsConfiguration("\"" + wifiItemSSID + "\"");
			if (wifiItemId != -1) {
				if (wifiItemId == mWifiUtils.getConnectedID()) {
					// 点击正在连接的
					Fragment_Wifi_connect Dialog = new Fragment_Wifi_connect(wifiItemSSID,
							wifiItemENC, new OnConnectActionListener() {

								@Override
								public void connectAction(String mAction) {
									// TODO Auto-generated method stub
									if (mAction.equalsIgnoreCase("forget")) {
										mWifiUtils.removeNetwork(wifiItemId);
										mWifiUtils.getConfiguration();
									} else if (mAction.equalsIgnoreCase("connect")) {
										// mWifiUtils.ConnectWifi(mContext,
										// wifiItemId);
									} else if(mAction.equalsIgnoreCase("disconnect")){
										WifiManager mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
										WifiInfo info = mWifiManager.getConnectionInfo();
								        mWifiManager.disableNetwork(info.getNetworkId());
								        mWifiManager.disconnect();
									}
								}
							});
					Dialog.show(mFragmentManager, "dlg_wifi_connecting");
					return;
				}
				// 点击已经保存过密码的
				Fragment_Wifi_connect Dialog = new Fragment_Wifi_connect(wifiItemSSID, wifiItemENC,
						new OnConnectActionListener() {

							@Override
							public void connectAction(String mAction) {
								// TODO Auto-generated method stub
								if (mAction.equalsIgnoreCase("forget")) {
									mWifiUtils.removeNetwork(wifiItemId);
									mWifiUtils.getConfiguration();
								} else if (mAction.equalsIgnoreCase("connect")) {
									mWifiUtils.ConnectWifi(mContext, wifiItemId);
								}
							}
						});
				Dialog.show(mFragmentManager, "dlg_wifi_connected");

			} else if (!(wifiAdapter.getItem(position).isWifiLock())) {
				// 如果不需要密码，直接连接
				connectNewWifi(wifiItemSSID, false);

			} else {
				// 如果没有，提示输入密码
				Fragment_Wifi_password fragment = new Fragment_Wifi_password(wifiItemSSID,
						new OnCustomDialogListener() {
							@Override
							public void back(String str) {
								// TODO Auto-generated method stub
								wifiPassword = str;
								if (wifiPassword != null) {
									connectNewWifi(wifiItemSSID, true);
								}
							}
						});
				fragment.show(mFragmentManager, "set_wifi_password");
			}
		}
	}

	protected void connectNewWifi(String newWifiItemSSID, boolean ispwd) {
		int netId = mWifiUtils.AddWifiConfig(mScanResultList, newWifiItemSSID, wifiPassword, ispwd);
		Log.d("WifiPswDialog", String.valueOf(netId)); // add wifi
														// success,display NetId
		if (netId != -1) {
			mWifiUtils.getConfiguration();// 添加了配置信息，要重新得到配置信息
			mWifiUtils.ConnectWifi(mContext, netId);// 与真正连接结果无关，成功与不成功，都会返回true
		} else {
			String strState = getResources().getString(R.string.wifi_UNABLECONNECT);
			Toast.makeText(mContext, strState, Toast.LENGTH_SHORT).show();
		}
	}

	private void WifiConfigInfo() {
		mWifiUtils.WifiStartScan();
		mConfigList = mWifiUtils.getConfiguration();
	}

	private String getWifiConnectedInfo() {
		WifiInfo mWifiInfo = mWifiUtils.getConnectedInfo();
		SupplicantState connectSupplicantState = mWifiInfo.getSupplicantState();
		int mNetworkId = mWifiInfo.getNetworkId();
		String cntSSID;
		if (null != mWifiInfo.getSSID() && mWifiInfo.getSSID() != "") {
			cntSSID = mWifiInfo.getSSID().substring(1, (mWifiInfo.getSSID().length() - 1));
		} else
			cntSSID = mWifiInfo.getSSID();
		if (mNetworkId != -1) {

		}
		return cntSSID;
	}

}