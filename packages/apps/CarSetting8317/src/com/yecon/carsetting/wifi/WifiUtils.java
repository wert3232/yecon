package com.yecon.carsetting.wifi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import android.widget.Toast;

public class WifiUtils {
	private WifiManager localWifiManager;// 提供Wifi管理的各种主要API，主要包含wifi的扫描、建立连接、配置信息等
	// private List<ScanResult>
	// wifiScanList;//ScanResult用来描述已经检测出的接入点，包括接入的地址、名称、身份认证、频率、信号强度等
	private List<WifiConfiguration> wifiConfigList;// WIFIConfiguration描述WIFI的链接信息，包括SSID、SSID隐藏、password等的设置
	private WifiInfo wifiConnectedInfo;// 已经建立好网络链接的信息
	private WifiLock wifiLock;// 手机锁屏后，阻止WIFI也进入睡眠状态及WIFI的关闭

	public WifiUtils(Context context) {
		localWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	// 检查WIFI状态
	public int WifiCheckState() {
		return localWifiManager.getWifiState();
	}

	public boolean isWifiEnabled() {
		return localWifiManager.isWifiEnabled();
	}

	public void WifiOpen() {
		// Log.i("lzytest_WifiState",
		// "localWifiManager.getWifiState()............."+localWifiManager.getWifiState());
		if (localWifiManager.getWifiState() == localWifiManager.WIFI_STATE_ENABLING
				|| localWifiManager.getWifiState() == localWifiManager.WIFI_STATE_DISABLING) {
			return;
		}

		if (localWifiManager.getWifiState() == localWifiManager.WIFI_STATE_DISABLED
				|| localWifiManager.getWifiState() == localWifiManager.WIFI_STATE_UNKNOWN) {
			if (localWifiManager.isWifiApEnabled())
				localWifiManager.setWifiApEnabled(null, false);
			localWifiManager.setWifiEnabled(true);
		}
	}

	// 关闭WIFI
	public void WifiClose() {
		if (localWifiManager.getWifiState() == localWifiManager.WIFI_STATE_ENABLING
				|| localWifiManager.getWifiState() == localWifiManager.WIFI_STATE_DISABLING) {
			return;
		}

		if (localWifiManager.getWifiState() == localWifiManager.WIFI_STATE_ENABLED) {
			localWifiManager.setWifiEnabled(false);
		}

	}

	// 扫描wifi
	public void WifiStartScan() {
		localWifiManager.startScan();
	}

	// 得到Scan结果
	public List<ScanResult> getScanResults() {
		return localWifiManager.getScanResults();// 得到扫描结果
	}

	// Scan结果转为String
	public List<String> scanResultToString(List<ScanResult> list) {
		List<String> strReturnList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			ScanResult strScan = list.get(i);
			String str = strScan.toString();
			boolean bool = strReturnList.add(str);
			if (!bool) {
				Log.i("scanResultToSting", "Addfail");
			}
		}
		return strReturnList;
	}

	// 得到Wifi配置好的信息
	public List<WifiConfiguration> getConfiguration() {
		wifiConfigList = localWifiManager.getConfiguredNetworks();// 得到配置好的网络信息
		// for(int i =0;i<wifiConfigList.size();i++){
		// Log.i("getConfiguration",wifiConfigList.get(i).SSID);
		// Log.i("getConfiguration",String.valueOf(wifiConfigList.get(i).networkId));
		// }
		return wifiConfigList;
	}

	// 判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId
	public int IsConfiguration(String SSID) {
		if (wifiConfigList == null) {
			getConfiguration();
		}
		Log.i("IsConfiguration", String.valueOf(wifiConfigList.size()));
		for (int i = 0; i < wifiConfigList.size(); i++) {
			Log.i(wifiConfigList.get(i).SSID, String.valueOf(wifiConfigList.get(i).networkId));
			if (wifiConfigList.get(i).SSID.equals(SSID)) {// 地址相同
				return wifiConfigList.get(i).networkId;
			}
		}
		return -1;
	}

	// 添加指定WIFI的配置信息,原列表不存在此SSID
	public int AddWifiConfig(List<ScanResult> wifiList, String ssid, String pwd, boolean ispwd) {
		int wifiId = -1;
		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult wifi = wifiList.get(i);
			if (wifi.SSID.equals(ssid)) {
				Log.i("AddWifiConfig", "equals");
				WifiConfiguration wifiCong = new WifiConfiguration();
				wifiCong.SSID = "\"" + wifi.SSID + "\"";// \"转义字符，代表"
				// 是否需要密码
				if (ispwd) {
					wifiCong.preSharedKey = "\"" + pwd + "\"";// WPA-PSK密码
				} else {
					wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				}
				wifiCong.hiddenSSID = false;
				wifiCong.status = WifiConfiguration.Status.ENABLED;
				wifiId = localWifiManager.addNetwork(wifiCong);// 将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
				if (wifiId != -1) {
					return wifiId;
				}
			}
		}
		return wifiId;
	}

	// 连接指定Id的WIFI
	public boolean ConnectWifi(Context context, int wifiId) {
		// if(wifiId > wifiConfigList.size()) {
		// Toast.makeText(context, "无法连接", Toast.LENGTH_SHORT).show();
		// return false;
		// }
		if (wifiId == localWifiManager.getConnectionInfo().getNetworkId()) {
			return false;
		}

		for (int i = 0; i < wifiConfigList.size(); i++) {
			WifiConfiguration wifi = wifiConfigList.get(i);
			if (wifi.networkId == wifiId) {
				while (!(localWifiManager.enableNetwork(wifiId, true))) {// 激活该Id，建立连接
					Log.i("ConnectWifi", String.valueOf(wifiConfigList.get(wifiId).status));// status:0--已经连接，1--不可连接，2--可以连接
				}
				localWifiManager.saveConfiguration();
				return true;
			}
		}
		return false;
	}

	// 创建一个WIFILock
	public void createWifiLock(String lockName) {
		wifiLock = localWifiManager.createWifiLock(lockName);
	}

	// 锁定wifilock
	public void acquireWifiLock() {
		wifiLock.acquire();
	}

	// 解锁WIFI
	public void releaseWifiLock() {
		if (wifiLock.isHeld()) {// 判定是否锁定
			wifiLock.release();
		}
	}

	// 得到建立连接的信息
	public WifiInfo getConnectedInfo() {
		wifiConnectedInfo = localWifiManager.getConnectionInfo();
		return wifiConnectedInfo;
	}

	// 得到连接的MAC地址
	public String getConnectedMacAddr() {
		return (wifiConnectedInfo == null) ? "NULL" : wifiConnectedInfo.getMacAddress();
	}

	// 得到连接的名称SSID
	public String getConnectedSSID() {
		return (wifiConnectedInfo == null) ? "NULL" : wifiConnectedInfo.getSSID();
	}

	// 得到连接的IP地址
	public int getConnectedIPAddr() {
		return (wifiConnectedInfo == null) ? 0 : wifiConnectedInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getConnectedID() {
		return (wifiConnectedInfo == null) ? -1 : wifiConnectedInfo.getNetworkId();
	}

	// NETWORK_STATE_CHANGED_ACTION
	// 显示‘正在连接’、‘获取ip地址’
	public DetailedState getDetailedState(Intent intent) {
		DetailedState mdState = ((NetworkInfo) intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
		return mdState;
	}

	// SUPPLICANT_STATE_CHANGED_ACTION
	public SupplicantState getSupplicanState(Intent intent) {
		SupplicantState msState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
		return msState;
	}

	// 断开指定ID的网络
	public void disConnectionWifi(int netId) {
		localWifiManager.disableNetwork(netId);
		localWifiManager.disconnect();
	}

	// 删除Configuration中的Network
	public void removeNetwork(int netId) {
		localWifiManager.disableNetwork(netId);
		localWifiManager.removeNetwork(netId);
		localWifiManager.saveConfiguration();
	}

}
