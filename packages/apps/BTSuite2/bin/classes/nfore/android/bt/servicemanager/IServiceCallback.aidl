/** 
 * nForeTek service callback interface for Android
 *
 * Copyright (C) 2011-2019  nForeTek Corporation
 *
 * First release on 20120907
 * 
 * @author Bohan	<bohanlu@nforetek.com>
 * @modifier Piggy 	<piggylee@nforetek.com>
 * @version 20130515
 */

package nfore.android.bt.servicemanager;

/**
 * The callback interface for basic primitive functions or service related events.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 * <br>The naming principle of callback in this doc is as follows,
 *		<p><blockquote>retXXX() : must be the callback of previous request API.
 *		<br>onXXX() : might be the unsolicated callback or callback due to previous requested API.</blockquote>
 *
 * <p> The constant variables in this doc could be found and referred by importing
 * 		<br><blockquote>nfore.android.bt.res.NfDef</blockquote>
 * <p> with prefix NfDef class name. Ex : NfDef.DEFAULT_ADDRESS 
 */
 

interface IServiceCallback {

	/** 
	 * Callback to inform nFore service is ready.
	 * This callback is invocated only on the time system booting up is completed or Bluetooth is turned on.  
	 *
	 * @param profilesEnable : the profiles enabled. The possible values are
	 *		<p><blockquote>PROFILE_HSP		(int) 1
	 *		<br>PROFILE_HFP		(int) (1 << 1)
	 *		<br>PROFILE_A2DP	(int) (1 << 2)
	 * 		<br>PROFILE_SPP		(int) (1 << 3)
	 * 		<br>PROFILE_PBAP	(int) (1 << 4)
	 *		<br>PROFILE_AVRCP	(int) (1 << 5)
	 *		<br>PROFILE_FTP		(int) (1 << 6)
	 *		<br>PROFILE_MAP		(int) (1 << 7)</blockquote>	 
	 * For example, value 7 (0000 0111) means it HSP, HFP and A2DP are enabled and running.	 
	 */
	void onServiceReady(int profilesEnable);

	/** 
	 * Callback to inform change in state of local Bluetooth adapter.
	 * The possible values are as follows.
	 * For Bluetooth enable and disable
	 * 		<p><blockquote>BT_STATE_OFF			(int) 300
	 * 		<br>BT_STATE_TURNING_ON		(int) 301
	 * 		<br>BT_STATE_ON				(int) 302
	 *		<br>BT_STATE_TURNING_OFF	(int) 303</blockquote>
	 * For discoverable
	 *		<p><blockquote>BT_MODE_NONE						(int) 310
	 *		<br>BT_MODE_CONNECTABLE					(int) 311
	 *		<br>BT_MODE_CONNECTABLE_DISCOVERABLE	(int) 312</blockquote>
	 * For scanning
	 *		<p><blockquote>BT_SCAN_START				(int) 320
	 *		<br>BT_SCAN_FINISH				(int) 321</blockquote>
	 *
	 * @param newState : the new state.
	 */
	void onAdapterStateChanged(int newState);

	/** 
	 * This callback is the response to {@link INforeService#reqBluetoothPairedDevices reqBluetoothPairedDevices}.
	 *
	 * @param elements : the number of paired devices returned. In this release, due to we only support 7 paired devices maximal,
	 * this value would be equal to or less than 7.
	 * @param address : the address of device in String type. The name, supported profiles, and possible category of n-th device of address 
	 * are placed at n-th element of name, supportProfile, and category correspondingly.
	 * @param name : the name of device in String type.
	 * @param supportProfile : the supported profiles of device in int type. Possible values are
	 *		<p><blockquote>PROFILE_HSP	(int) 1
	 *		<br>PROFILE_HFP				(int) (1 << 1)
	 *		<br>PROFILE_A2DP			(int) (1 << 2)
	 * 		<br>PROFILE_SPP				(int) (1 << 3)
	 * 		<br>PROFILE_PBAP			(int) (1 << 4)
	 *		<br>PROFILE_AVRCP			(int) (1 << 5)
	 *		<br>PROFILE_FTP				(int) (1 << 6)
	 *		<br>PROFILE_MAP				(int) (1 << 7)
	 *		<br>PROFILE_AVRCP_13		(int) (1 << 8)
	 *		<br>PROFILE_AVRCP_14		(int) (1 << 9)
	 *		<br>PROFILE_PANU     		(int) (1 << 10)</blockquote>
	 	 	 
	 * For example, value 7 (0000 0111) means it supports HSP, HFP and A2DP. However, this parameter might be null on the first time 
	 * we get this device because we retrieve the records from local cache by default. In this situation, system would start SDP query
	 * automatically and notify users when SDP records are gotten successfully by calling {@link IServiceCallback#onDeviceUuidsGot onDeviceUuidsGot}.
	 * User application might need to register and implement this callback function, too.     
	 * @param category : the possible category or class of device. This is not precise. It might be only useful on icon selecting.
	 * Possible values are
	 *		<p><blockquote>CAT_COMPUTER		(byte) 1
	 *		<br>CAT_PHONE			(byte) (1 << 1)
	 *		<br>CAT_STEREO_AUDIO	(byte) (1 << 2)
	 *		<br>CAT_MONO_AUDIO		(byte) (1 << 3)</blockquote>
	 */
	void retBluetoothPairedDevices(int elements, in String[] address, in String[] name, in int[] supportProfile, in byte[] category);

	/** 
	 * Callback to inform a new device is found. This callback might be the response to 
	 * {@link INforeService#reqBluetoothScanning reqBluetoothScanning}.
	 *
	 * @param address : the address of found device in String type.
	 * @param name : the name of found device in String type if it is available, otherwise return the mac address as name and set parameter 
	 * isRealName to false. 
	 * @param category : the possible category or class of found device. This is not precise. It might be only useful on icon selecting.
	 * Possible values are
	 *		<p><blockquote>CAT_COMPUTER		(byte) 1
	 *		<br>CAT_PHONE			(byte) (1 << 1)
	 *		<br>CAT_STEREO_AUDIO	(byte) (1 << 2)
	 *		<br>CAT_MONO_AUDIO		(byte) (1 << 3)</blockquote>
	 * @param isRealName : true if the name of found device is got.
	 */
	void onDeviceFound(String address, String name, byte category, boolean isRealName);
	
	/** 
	 * Callback to inform a new device is paired. This callback might be the response to 
	 * {@link INforeService#reqBluetoothPair reqBluetoothPair}.
	 *
	 * @param address : the address of paired device in String type.
	 * @param name : the name of paired device in String type.
	 * @param supportProfile : the supported profiles of paired device in int type. Possible values are
	 *		<p><blockquote>PROFILE_HSP	(int) 1
	 *		<br>PROFILE_HFP				(int) (1 << 1)
	 *		<br>PROFILE_A2DP			(int) (1 << 2)
	 * 		<br>PROFILE_SPP				(int) (1 << 3)
	 * 		<br>PROFILE_PBAP			(int) (1 << 4)
	 *		<br>PROFILE_AVRCP			(int) (1 << 5)
	 *		<br>PROFILE_FTP				(int) (1 << 6)
	 *		<br>PROFILE_MAP				(int) (1 << 7)
	 *		<br>PROFILE_AVRCP_13		(int) (1 << 8)
	 *		<br>PROFILE_AVRCP_14		(int) (1 << 9)
	 *		<br>PROFILE_PANU     		(int) (1 << 10)</blockquote>
	 * For example, value 7 (0000 0111) means it supports HSP, HFP and A2DP. However, this parameter might be null on the first time 
	 * we get this device because we retrieve the records from local cache by default. In this situation, system would start SDP query
	 * automatically and notify users when SDP records are gotten successfully by calling {@link IServiceCallback#onDeviceUuidsGot onDeviceUuidsGot}.
	 * User application might need to register and implement this callback function, too.   
	 * @param category : the possible category or class of paired device. This is not precise. It might be only useful on icon selecting.
	 * Possible values are
	 *		<p><blockquote>CAT_COMPUTER		(byte) 1
	 *		<br>CAT_PHONE			(byte) (1 << 1)
	 *		<br>CAT_STEREO_AUDIO	(byte) (1 << 2)
	 *		<br>CAT_MONO_AUDIO		(byte) (1 << 3)</blockquote>
	 */
	void onDevicePaired(String address, String name, int supportProfile, byte category);
	
	/** 
	 * Callback to inform a device is unpaired. This callback might be the response to 
	 * {@link INforeService#reqBluetoothUnpair reqBluetoothUnpair}.
	 *
	 * @param address : the address of paired device in String type.
	 * @param name : the name of paired device in String type.
	 */
	void onDeviceUnpaired(String address, String name);

	/** 
	 * Callback to inform a device is pair failed. This callback might be the response to 
	 * {@link INforeService#reqBluetoothPair reqBluetoothPair}.
	 *
	 * @param address : the address of paired device in String type.
	 * @param name : the name of paired device in String type.
	 */
	void onDevicePairFailed(String address, String name);
	
	/** 
	 * Callback to inform the supported profiles of remote device are gotten.
	 *
	 * @param address : the address of paired device in String type.
	 * @param name : the name of paired device in String type.
	 * @param supportProfile : the supported profiles of paired device in int type. Possible values are
	 *		<p><blockquote>PROFILE_HSP	(int) 1
	 *		<br>PROFILE_HFP				(int) (1 << 1)
	 *		<br>PROFILE_A2DP			(int) (1 << 2)
	 * 		<br>PROFILE_SPP				(int) (1 << 3)
	 * 		<br>PROFILE_PBAP			(int) (1 << 4)
	 *		<br>PROFILE_AVRCP			(int) (1 << 5)
	 *		<br>PROFILE_FTP				(int) (1 << 6)
	 *		<br>PROFILE_MAP				(int) (1 << 7)
	 *		<br>PROFILE_AVRCP_13		(int) (1 << 8)
	 *		<br>PROFILE_AVRCP_14		(int) (1 << 9)
	 *		<br>PROFILE_PANU     		(int) (1 << 10)</blockquote>	 	 
	 * For example, value 7 (0000 0111) means it supports HSP, HFP and A2DP.
	 */
	void onDeviceUuidsGot(String address, String name, int supportProfile);	

}
