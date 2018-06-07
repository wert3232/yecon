package com.yecon.carsetting.wifi;

public class WifiItem {

	private String wifiName;	//SSID名字
	
	private int signalLevel;	//信号强度的实际值
	
	private boolean wifiLock;
	
	private int signalCount;
	
	private int imageLevelID;	//信号强度图片id
	
	private String wifiBssid;
	
	private String describes;	//认证，密钥管理，由接入点所支持的加密方案
	
	private String connected;

	public String getWifiName() {
		return wifiName;
	}

	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}

	public int getSignalLevel() {
		return signalLevel;
	}

	public void setSignalLevel(int signalLevel) {
		this.signalLevel = signalLevel;
	}

	public boolean isWifiLock() {
		return wifiLock;
	}

	public void setWifiLock(boolean wifiLock) {
		this.wifiLock = wifiLock;
	}

	public int getSignalCount() {
		return signalCount;
	}

	public void setSignalCount(int signalCount) {
		this.signalCount = signalCount;
	}

	public int getImageLevelID() {
		return imageLevelID;
	}

	public void setImageLevelID(int imageLevelID) {
		this.imageLevelID = imageLevelID;
	}

	public String getWifiBssid() {
		return wifiBssid;
	}

	public void setWifiBssid(String wifiBssid) {
		this.wifiBssid = wifiBssid;
	}

	public String getDescribes() {
		return describes;
	}

	public void setDescribes(String describes) {
		this.describes = describes;
	}

	public String getConnected() {
		return connected;
	}

	public void setConnected(String connected) {
		this.connected = connected;
	}

}
