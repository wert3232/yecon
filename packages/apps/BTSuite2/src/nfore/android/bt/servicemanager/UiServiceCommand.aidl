package nfore.android.bt.servicemanager;

/** 
 * nForeTek service interface for Android
 *
 * First release on 20120907
 * 
 * @author Bohan	<bohanlu@nforetek.com>
 * @version 20130207
 */


import nfore.android.bt.servicemanager.UiHfpCallback;
import nfore.android.bt.servicemanager.UiHspCallback;
import nfore.android.bt.servicemanager.UiSppCallback;
import nfore.android.bt.servicemanager.UiA2dpCallback;
import nfore.android.bt.servicemanager.UiServiceCallback;
import nfore.android.bt.servicemanager.UiPbapCallback;
import nfore.android.bt.servicemanager.UiMapCallback;
import nfore.android.bt.servicemanager.UiPanCallback;
import nfore.android.bt.servicemanager.UiHidCallback;

/**
 * The API interface for HSP, HFP, SPP, PBAP, MAP, PAN, A2DP and AVRCP profiles.
 * UI program may use this for requesting the specific API to nFore service.
 * <br>The naming principle of API in this doc is as follows,
 *		<p><blockquote>setXXX() : request to set attributes with or without returned result.
 *		<br>isXXX() : the requested result with boolean type would be returned immediately, Syn mode.
 *		<br>getXXX() : the requested result would be returned immediately, Syn mode.
 *		<br>reqXXX() : request nFore service to do something or the requested result might NOT be returned immediately, App needs to wait for another callback, Asyn mode.</blockquote>
 *
 * <p> The constant variables in this doc could be found and referred by importing
 * 		<br><blockquote>nfore.android.bt.res.NfDef</blockquote>
 * <p> with prefix NfDef class name. Ex : NfDef.DEFAULT_ADDRESS
 */
 
 interface UiServiceCommand {
 
 	/** 
	 * Register callback functions for basic service functions.
	 * Call this function to register callback functions for nFore service.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
	boolean registerNforeServiceCallback(UiServiceCallback cb);
	
	/** Remove callback functions for nFore service.
     * Call this function to remove previously registered callback interface for nFore service.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */ 
	boolean unregisterNforeServiceCallback(UiServiceCallback cb);

	/** 
	 * Request to enable or disable local Bluetooth adapter.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param isEnable : true to enable Bluetooth adapter or false to disable.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean setBluetoothEnable(boolean isEnable);

	/** 
	 * Request to make local Bluetooth adapter discoverable for specific duration in seconds.
	 * If the duration is 0, the system default discoverable duration would be adopted.
	 * The system default duration for discoverable might differentiate from each other in different platforms.
	 * However, it is 120 seconds in general.
	 * If the duration is negative value, we would disable discoverable.	 
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param duration : the duration time in seconds to apply the discoverable mode. This value must be a positive value.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean setBluetoothDiscoverableDuration(int duration);

	/** 
	 * Request to make local Bluetooth adapter discoverable permanently or not.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param isEnable : true to enable or false to disable.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean setBluetoothDiscoverablePermanent(boolean isEnable);

	/** 
	 * Request to start or stop scanning process for remote devices.
	 * Client should not request to start scanning twice or more in 12 seconds.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged} and {@link IServiceCallback#onDeviceFound onDeviceFound}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param isEnable : true to start or false to stop.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqBluetoothScanning(boolean isEnable);

	/** 
	 * Request to pair with given Bluetooth hardware address.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IServiceCallback#onDevicePaired onDevicePaired} and {@link IServiceCallback#onDeviceUuidsGot onDeviceUuidsGot}
	 * to be notified if pairing is successful.	 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @attention : We only support 7 paired devices maximal. System would tear down the first paired device automatically when
	 * the limit is reached. 
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 		
	boolean reqBluetoothPair(String address);

	/** 
	 * Request to unpair with given Bluetooth hardware address.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IServiceCallback#onDeviceUnpaired onDeviceUnpaired}
	 * to be notified if unpairing is successful.	 	 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * However, this operation only removes the pairing record locally. Remote device would not be notified or
	 * to become aware of this util it tries to connect to us or we initiate the pairing again next time.
	 * All connected or connecting profiles would be terminated before unpairing.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 		
	boolean reqBluetoothUnpair(String address);
		
	/** 
	 * Request to remove all paired devices.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IServiceCallback#onDeviceUnpaired onDeviceUnpaired}
	 * to be notified if each unpairing is successful.	 	 
	 * However, this operation only removes the pairing record locally. Remote device would not be notified or
	 * to become aware of this util it tries to connect to us or we initiate the pairing again next time.
	 * All connected or connecting profiles should be terminated before unpairing.
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 		
	boolean reqBluetoothUnpairAll();

	/** 
	 * Request to remove all paired devices on next time Bluetooth turning on.	 	 
	 * However, this operation only removes the pairing record locally. Remote device would not be notified or
	 * to become aware of this util it tries to connect to us or we initiate the pairing again next time.
	 * The operation of unpairing devices would be executed on next time Bluetooth is turned on and if this operation is done successfully,
	 * Bluetooth would be automatically turned off.
	 * The possible usage of this method might be in the scenario when system needs to be reset to default. 
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 		
	boolean reqBluetoothUnpairAllNextTime();

	/** 
	 * Request to get the paired device list.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#retBluetoothPairedDevices retBluetoothPairedDevices}
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 		
	boolean reqBluetoothPairedDevices();
	
	/** 
	 * Request to get the paired device list.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#retBluetoothPairedDevices retBluetoothPairedDevices}
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 		
	boolean reqBluetoothPairedDevicesExtend();

	/** 
	 * Request to get the name of local Bluetooth adapter.
	 * If there is an error, the string "UNKNOWN" would be returned.
	 *
	 * @return the String type name of local Bluetooth adapter.
	 */ 		
	String getBluetoothLocalAdapterName();
	
	/** 
	 * Request to get the name of remote Bluetooth device with given address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This method would return the name announced by remote device in String type only if this remote device
	 * has been scanned before or otherwise the empty string would be returned.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.	 
	 * @return the real String type name of remote device or the empty string.
	 */ 		
	String getBluetoothRemoteDeviceName(in String address);	

	/** 
	 * Request to get the address of local Bluetooth adapter.
	 *
	 * @return the String type address of local Bluetooth adapter.
	 */ 		
	String getBluetoothLocalAdapterAddress();

	/** 
	 * Request to change the name of local Bluetooth adapter.
	 *
	 * @param name : the name to set.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 		
	boolean setBluetoothLocalAdapterName(String name);

	/** 
	 * Return true if Bluetooth is currently enabled and ready for use.
	 *
	 * @return true if the local adapter is turned on.
	 */ 		
	boolean isBluetoothEnable();
	
	/** 
	 * Request to get the current state of local Bluetooth adapter.
	 *
	 * @return int value to denote the current state. Possible values are
	 *		<p><blockquote>ERROR		(int) -1
	 *		<br>BT_STATE_OFF			(int) 300
	 *		<br>BT_STATE_TURNING_ON		(int) 301
	 *		<br>BT_STATE_ON				(int) 302
	 *		<br>BT_STATE_TURNING_OFF	(int) 303</blockquote>	
	 */ 		
	int getBluetoothCurrentState();	

	/** 
	 * Return true if Bluetooth is currently scanning remote devices.
	 *
	 * @return true if scanning.
	 */ 		
	boolean isBluetoothScanning();

	/** 
	 * Return true if Bluetooth is currently discoverable from remote devices.
	 *
	 * @return true if discoverable.
	 */ 		
	boolean isBluetoothDiscoverable();
	
	/** 
	 * Request to enable or disable auto-reconnect.
	 * The setting would persist until client requests again.
	 * Auto-reconnect only applies to HSP, HFP and A2DP profiles when out of range event takes place.
	 * Besides, only one of HSP or HFP would be re-connected, and HFP is preferred to HSP.
	 * Enabling auto-reconnect only applies to the following conditions, 
	 *		<p><blockquote>next out of range event,
	 *		<br>there is out of range event before system shutdown AND system is restarted, or 
	 *		<br>there is out of range event before Bluetooth turning-off AND Bluetooth is restarted</blockquote>
	 *
	 * @param isEnable : true to enable or false to disable.
	 */ 		
	void setBluetoothAutoReconnect(boolean isEnable);
	
	/** 
	 * Request to force out of range flags to be set or un-set.
	 * This request dose not enable or disable auto-reconnect automatically. It just sets OOR flags.
	 * {@link INforeService#setBluetoothAutoReconnect setBluetoothAutoReconnect} must be invocated before calling this API.
	 * Besides, if there is no connected profile when this API is invocated, this API would change nothing.
	 * This API is generally invocated before system shutdown or Bluetooth turning-off, and it would force our
	 * service to connect to the device, which is connected when this API is invocated, 
	 * when system is restarted or Bluetooth is turned on next time. 
	 *
	 * @param isEnable : true to enable or false to disable.
	 */ 		
	void setBluetoothAutoReconnectForceOor(boolean isEnable);
	
	/** 
	 * Return true if auto-reconnect is currently enabled.
	 *
	 * @return true if auto-reconnect is enabled.
	 */ 		
	boolean isBluetoothAutoReconnectEnable();
	
	/** 
	 * Request to connect HSP, HFP, A2DP, and AVRCP to remote device with given Bluetooth hardware address.
	 * If remote device supports both HSP and HFP, our service prefers HFP to HSP.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately with int returned value, 
	 * which denotes the possible profiles our service would try to connect to, and 
	 * clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged}, 
	 * {@link IHfpCallback#hfpConnectState(int mState,String address,String mName)}, 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged}, and
	 * {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that all profiles will be connected and the sequence of state changed callback of profiles! 
	 * This depends on the behavior of connected device.
	 * Besides, client should make sure there is no connection currently or this request would return ERROR.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return int value to denote the profiles we would connect to. Possible values are
	 *		<p><blockquote>ERROR	(int) -1
	 *		<br>PROFILE_HSP		(int) 1
	 *		<br>PROFILE_HFP		(int) (1 << 1)
	 *		<br>PROFILE_A2DP	(int) (1 << 2)</blockquote>	 
	 * For example, value 6 (0000 0110) means that HFP and A2DP would be connected.	 	 
	 */ 	
	int reqBluetoothConnectAll(in String address);

	/** 
	 * Request to disconnect all connected profiles.
	 * This is an asynchronous call: it will return immediately with int returned value, 
	 * which denotes the possible profiles our service would try to disconnect to, and 
	 * clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged}, 
	 * {@link IHfpCallback#hfpConnectState(int mState,String address,String mName)}, 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged}, and
	 * {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee to the sequence of state changed callback of profiles! 
	 * This depends on the behavior of connected device.
	 * Besides, if there is no connection currently, this request would return ERROR.
	 *
	 * @return int value to denote the profiles we would disconnect. Possible values are
	 *		<p><blockquote>ERROR	(int) -1
	 *		<br>PROFILE_HSP		(int) 1
	 *		<br>PROFILE_HFP		(int) (1 << 1)
	 *		<br>PROFILE_A2DP	(int) (1 << 2)
	 * 		<br>PROFILE_SPP		(int) (1 << 3)
	 * 		<br>PROFILE_PBAP	(int) (1 << 4)
	 *		<br>PROFILE_FTP		(int) (1 << 6)
	 *		<br>PROFILE_MAP		(int) (1 << 7)</blockquote>	  
	 * For example, value 6 (0000 0110) means that HFP and A2DP would be disconnected.	 	 
	 */ 	
	int reqBluetoothDisconnectAll();
	
	/** 
	 * Request to disconnect all connected profiles.
	 * This is an asynchronous call: it will return immediately with int returned value, 
	 * which denotes the possible profiles our service would try to disconnect to, and 
	 * clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged}, 
	 * {@link IHfpCallback#hfpConnectState(int mState,String address,String mName)}, 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged}, and
	 * {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee to the sequence of state changed callback of profiles! 
	 * This depends on the behavior of connected device.
	 * Besides, if there is no connection currently, this request would return ERROR.
	 *
	 * @return int value to denote the profiles we would disconnect. Possible values are
	 *		<p><blockquote>ERROR	(int) -1
	 *		<br>PROFILE_HSP		(int) 1
	 *		<br>PROFILE_HFP		(int) (1 << 1)
	 *		<br>PROFILE_A2DP	(int) (1 << 2)
	 * 		<br>PROFILE_SPP		(int) (1 << 3)
	 * 		<br>PROFILE_PBAP	(int) (1 << 4)
	 *		<br>PROFILE_FTP		(int) (1 << 6)
	 *		<br>PROFILE_MAP		(int) (1 << 7)</blockquote>	  
	 * For example, value 6 (0000 0110) means that HFP and A2DP would be disconnected.	 	 
	 */ 	
	int reqDisconnectAll(in String address);//add by Jackie
	
	/** 
	 * Request the supported profiles of remote device with given Bluetooth hardware address.
	 * The requested address must be the paired device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This request will return immediately with int returned value, which denotes the supported profiles.
	 * 0x00 would be returned if we could not get UUIDs from system framework, and our service will request
	 * real query to this device again automatically. Clients should register and implement callback functions
	 * {@link IServiceCallback#onDeviceUuidsGot onDeviceUuidsGot} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of paired device.
	 * @return the supported profiles of paired device in int type. Possible values are
	 *		<p><blockquote>ERROR	(int) -1
	 *		<br>PROFILE_HSP			(int) 1
	 *		<br>PROFILE_HFP			(int) (1 << 1)
	 *		<br>PROFILE_A2DP		(int) (1 << 2)
	 * 		<br>PROFILE_SPP			(int) (1 << 3)
	 * 		<br>PROFILE_PBAP		(int) (1 << 4)
	 *		<br>PROFILE_AVRCP		(int) (1 << 5)
	 *		<br>PROFILE_FTP			(int) (1 << 6)
	 *		<br>PROFILE_MAP			(int) (1 << 7)
	 *		<br>PROFILE_AVRCP_13	(int) (1 << 8)
	 *		<br>PROFILE_AVRCP_14	(int) (1 << 9)</blockquote>	 
	 * For example, value 7 (0000 0111) means it supports HSP, HFP and A2DP.
	 */
	int reqBluetoothRemoteUuids(String address);


	/* ======================================================================================================================================================== 
	 * HFP
	 */

	/** 
	 * Register callback functions for HFP profile.
	 * Call this function to register callback functions for HFP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean registerHfpCallback(UiHfpCallback cb);   
    
	/** 
	 * Remove callback functions for HFP profile.
     * Call this function to remove previously registered callback interface for HFP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */ 
    boolean unregisterHfpCallback(UiHfpCallback cb);

	/** 
	 * Request to connect HFP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @attention : We only support one connection at this released version.
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean reqHfpConnect(String address);
    
	/** 
	 * Request to disconnect the connected HFP connection to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean reqHfpDisconnect(String address);

	/** 
	 * Request for the hardware Bluetooth address of connected remote HFP device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected HFP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */ 
    String getHfpConnectedDeviceAddress(); 
    
	/** 
	 * Request for the current state of remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".	 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return current state of HFP profile service.
	 */ 
    int getHfpCurrentState(String address);     
    
	/** 
	 * Request for the current SCO state of remote connected HFP device with given Bluetooth hardware address.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return current SCO state of HFP profile service.
	 */ 
    int getHfpCurrentScoState(String address);

    /** 
	 * Request for the current voice control state of remote connected HFP device with given Bluetooth hardware address.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return voice control status (On/Off).
	 */ 
    boolean isHfpVoiceControlOn(String address);
    
	/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to dial the outgoing call with given phone number.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param callNumber : the outgoing call phone number.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean reqHfpDialCall(String address, String callNumber);    
    
	/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to re-dial the last outgoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean reqHfpReDial(String address);    
    
	/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the memory dialing.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @param memoryLocation : the memory location on mobile phone. Mobile phone would retrive phone number stored in this place and dial out.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean reqHfpMemoryDial(String address, String memoryLocation);         
    
	/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to answer the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true if send command success
	 */ 
    boolean reqHfpAnswerCall(String address);
    
	/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to reject the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true if send command success
	 */ 
    boolean reqHfpRejectIncomingCall(String address);
    
	/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to terminate the ongoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true if send command success
	 */ 
    boolean reqHfpTerminateOngoingCall(String address);    
        
	/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the multipaty calls control.
	 * This API should only been called when there are multiparty calls.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param opCode :
	 *		<p><blockquote>HFP_CHLD_RELEASE_ALL_HELD_CALLS				(byte) 0x00
	 *		<br>HFP_CHLD_SET_USER_BUSY_FOR_WAITING_CALL					(byte) 0x00
	 *		<br>HFP_CHLD_RELEASE_ALL_ACTIVE_CALLS_ACCEPT_THEOTHER		(byte) 0x01
	 *		<br>HFP_CHLD_HOLD_ALL_ACTIVE_CALLS_ACCEPT_THEOTHER			(byte) 0x02
	 *		<br>HFP_CHLD_ADD_HELD_CALL_TO_CONVERSATION					(byte) 0x03</blockquote>	
	 * @return true if send command success
	 */
    boolean reqHfpMultiCallControl(String address, byte opCode);
    
	/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the DTMF.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Due to the compatibility, please request this API with single DTMF number each time.
	 * Avoid requesting with serial DTMF numbers. 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @param dtmfNumber : the DTMF number.
 	 * @return true if send command success
	 */ 
    boolean reqHfpSendDtmf(String address, String dtmfNumber);
    
	/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the SCO audio transfer.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged} to be notified of subsequent state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param opCode :
	 *		<p><blockquote>AUDIO_TRANSFER_TO_PHONE	(byte) 0x00
	 *		<br>AUDIO_TRANSFER_TO_CARKIT			(byte) 0x01</blockquote>
	 * @return true if send command success	 
	 */
    boolean reqHfpAudioTransfer(String address, byte opCode);
    
	/** 
	 * Request to enable or disable the local ringtone when there is an incoming call.
	 *
	 * @param isEnable
	 *<p><value>=true enable ring
	 *<p><value>=false disable ring
	 */
    void setHfpLocalRingEnable(boolean isEnable);    
    
	/** 
	 * Request information of remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#retHfpRemoteDeviceInfo retHfpRemoteDeviceInfo} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 */
    void reqHfpRemoteDeviceInfo(String address);
    
	/** 
	 * Request for the name of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This method would return the name announced in HFP by remote device in String type.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.	 
	 * @return the real String type name of remote device or the empty string.
	 */ 
    String getHfpRemoteDeviceName(String address);    
        
	/** 
	 * Request for the active or held phone number of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpRemoteDevicePhoneNumberChanged onHfpRemoteDevicePhoneNumberChanged} to be notified of subsequent result.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true if send command success
	 */ 
    boolean reqHfpRemoteDevicePhoneNumber(String address);
    
	/** 
	 * Request for the network operator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#retHfpRemoteDeviceNetworkOperator retHfpRemoteDeviceNetworkOperator} to be notified of subsequent result.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true if send command success
	 */ 
    boolean reqHfpRemoteDeviceNetworkOperator(String address);
    
	/** 
	 * Request for the subscriber number information of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#retHfpRemoteDeviceSubscriberNumber retHfpRemoteDeviceSubscriberNumber} to be notified of subsequent result.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true if send command success
	 */ 
    boolean reqHfpRemoteDeviceSubscriberNumber(String address);    
    
	/** 
	 * Request for the status of telecom service indicator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate remote device has telecom service.	 
	 */ 
    boolean isHfpRemoteDeviceTelecomServiceOn(String address);
    
	/** 
	 * Request for the status of roaming indicator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate roaming is active on remote device.	 
	 */ 
    boolean isHfpRemoteDeviceOnRoaming(String address);     
    
	/** 
	 * Request for the status of battery indicator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return the current battery status on remote device.	 
	 */ 
    int getHfpRemoteDeviceBatteryStatus(String address);     
    
	/** 
	 * Request for the status of signal strength indicator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return the current signal strength status on remote device.	 
	 */
    int getHfpRemoteDeviceSignalStatus(String address);     
    
	/** 
	 * Request to enable or disable new SMS notification by AT command interface.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.	 
	 * @param isEnable : true to enable or false.
	 */ 
    void setHfpAtNewSmsNotify(String address, boolean isEnable);
    
    /**
	 * Request to download phone book by AT command interface from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpAtDownloadPhonebookStateChanged onHfpAtDownloadPhonebookStateChanged} to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.	
	 * @param storage :
	 *		<p><blockquote>PBAP_STORAGE_SIM		(int) 1
	 *		<br>PBAP_STORAGE_PHONE_MEMORY		(int) 2
	 *		<br>PBAP_STORAGE_DIALED_CALLS		(int) 3
	 *		<br>PBAP_STORAGE_MISSED_CALLS		(int) 4
	 *		<br>PBAP_STORAGE_RECEIVED_CALLS		(int) 5</blockquote>	
	 * @return true if send command success	 
	 */
    boolean reqHfpAtDownloadPhonebook(String address, int storage);    
    
    /**
	 * Request to download SMS by AT command interface from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpAtDownloadSMSStateChanged onHfpAtDownloadSMSStateChanged} to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.	
	 * @return true if send command success	 
	 */
    boolean reqHfpAtDownloadSMS(String address);    
    
    /**
	 * Request to inform remote connected HFP device with given Bluetooth hardware address the current gain settings
	 * of local speaker or microphone.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#setHFPSpeakVolState setHFPSpeakVolState} and {@link IHfpCallback#setHFPVolState setHFPVolState}
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.
	 * @param attributeId :
	 *		<p><blockquote>VOLUME_ATTRIBUTE_SPEAKER	(byte) 0x01
	 *		<br>VOLUME_ATTRIBUTE_MIC				(byte) 0x02</blockquote>	 
	 * @param volume : sync volume
	 * @return true if send command success
	 */
    boolean reqHfpRemoteDeviceVolumeSync(String address, byte attributeId, int volume);

    /**
	 * Request remote connected HFP device with given Bluetooth hardware address to do the voice dialing.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!		 
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @param isEnable : true to start the voice dialing.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
    boolean reqHfpVoiceDial(String address, boolean isEnable);
    
    
	/* ======================================================================================================================================================== 
	 * HSP
	 */    
    
	/** 
	 * Register callback functions for HSP profile.
	 * Call this function to register callback functions for HSP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean registerHspCallback(UiHspCallback cb);   
  
	/** 
	 * Remove callback functions for HSP profile.
     * Call this function to remove previously registered callback interface for HSP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */   
    boolean unregisterHspCallback(UiHspCallback cb);    
    
	/** 
	 * Request to connect HSP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @attention : We only support one connection at this released version.
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */    
    boolean reqHspConnect(String address);
	
	/** 
	 * Request to disconnect the connected HSP connection to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */ 
	boolean reqHspDisconnect(String address);
	
	/** 
	 * Request for the hardware Bluetooth address of connected remote HSP device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected HSP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */ 
	String getHspConnectedDeviceAddress();
	
	/** 
	 * Request for the current state of remote connected HSP device with given Bluetooth hardware address.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return current state of HSP profile service.
	 */ 
    int getHspCurrentState(String address);     
    
	/** 
	 * Request for the current SCO state of remote connected HSP device with given Bluetooth hardware address.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return current SCO state of HSP profile service.
	 */ 
    int getHspCurrentScoState(String address);         

	/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to re-dial the last outgoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */    
    boolean reqHspReDial(String address);    
    
	/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to answer the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean reqHspAnswerCall(String address);     
    
	/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to reject the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!! By the definition of HSP spec, 
	 * HS could not request this operation. Due to this reason, please avoid to use this API.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */ 
    boolean reqHspRejectIncomingCall(String address);    
    
	/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to terminate the ongoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */    
    boolean reqHspTerminateOngoingCall(String address);
    
	/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to do the SCO audio transfer.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}	 
	 * to be notified of subsequent state changes.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!! By the definition of HSP spec, 
	 * HS could only request to transfer audio connection from AG to HS. If we request to transfer audio connection from HS to AG,
	 * the overall connection might be released by AG, and the ongoing call might be terminated. 
	 * Due to this reason, please avoid to use this API.
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param opCode :
	 *		<p><blockquote>AUDIO_TRANSFER_TO_PHONE	(byte) 0x00
	 *		<br>AUDIO_TRANSFER_TO_CARKIT			(byte) 0x01</blockquote>	 
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
    boolean reqHspAudioTransfer(String address, byte opCode);    
    
    /**
	 * Request to inform remote connected HSP device with given Bluetooth hardware address the current gain settings
	 * of local speaker or microphone.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspSpeakerVolumeChanged onHspSpeakerVolumeChanged} and {@link HSPCallback#onHspMicVolumeChanged onHspMicVolumeChanged}
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.
	 * @param attributeId :
	 *		<p><blockquote>VOLUME_ATTRIBUTE_SPEAKER	(byte) 0x01
	 *		<br>VOLUME_ATTRIBUTE_MIC				(byte) 0x02</blockquote>	 
	 * @param volume : sync volume
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */  
    boolean reqHspRemoteDeviceVolumeSync(String address, byte attributeId, int volume); 
    
    
	/* ======================================================================================================================================================== 
	 * SPP
	 */    
    
	/** 
	 * Register callback functions for SPP profile.
	 * Call this function to register callback functions for SPP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean registerSppCallback(UiSppCallback cb);   
    
	/** 
	 * Remove callback functions for SPP profile.
     * Call this function to remove previously registered callback interface for SPP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */ 
    boolean unregisterSppCallback(UiSppCallback cb);    
    
	/** 
	 * Request to connect SPP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link ISppCallback#onSppStateChanged onSppStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
    boolean reqSppConnect(String address);
    
	/** 
	 * Request to disconnect the connected SPP connection to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link ISppCallback#onSppStateChanged onSppStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean reqSppDisconnect(String address);
    
	/** 
	 * Request for the hardware Bluetooth address of connected remote SPP devices.
	 * For example, "00:11:22:AA:BB:CC".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link ISppCallback#retSppConnectedDeviceAddressList retSppConnectedDeviceAddressList} to be notified of subsequent result.
	 */
    void reqSppConnectedDeviceAddressList();    
    
	/** 
	 * Return true if device with given address is currently connected.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 * @return true if connected.
	 */ 
    boolean isSppConnected(String address);    
    
	/** 
	 * Request to send given data to remote connected SPP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Data size should not be greater than 512 bytes each time. 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param sppData : the data to be sent.
	 */
    void reqSppSendData(String address, in byte[] sppData);
    
    
	/* ======================================================================================================================================================== 
	 * PBAP
	 */    
    
	/** 
	 * Register callback functions for PBAP profile.
	 * Call this function to register callback functions for PBAP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean registerPbapCallback(UiPbapCallback cb);   
    
	/** 
	 * Remove callback functions for PBAP profile.
     * Call this function to remove previously registered callback interface for PBAP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */ 
    boolean unregisterPbapCallback(UiPbapCallback cb);    
    
    /**
	 * Request to download phone book by Vcard from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IPbapCallback#onPbapStateChanged onPbapStateChanged} and {@link IPbapCallback#onPbapDownloadNotify onPbapDownloadNotify} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.	
	 * @param storage :
	 *		<p><blockquote>PBAP_STORAGE_SIM		(int) 1
	 *		<br>PBAP_STORAGE_PHONE_MEMORY		(int) 2
	 *		<br>PBAP_STORAGE_DIALED_CALLS		(int) 3
	 *		<br>PBAP_STORAGE_MISSED_CALLS		(int) 4
	 *		<br>PBAP_STORAGE_RECEIVED_CALLS		(int) 5</blockquote>	
	 * @param notifyFreq : define the callback frequency.
	 * @param isUseContactsProvider : true is use Contacts Provider , or false is use nFore Database
	 *<p><value>=0 all vcards would be downloaded first, and inserted to database. Only one callback would be invocated.
	 *<p><value>>0 Callbacks would be invocated every "notifyFreq" vcards have been downloaded and inserted to database. 
	 * @return true to indicate the operation has been done without error, or false on immediate error or system is busy.	 
	 */
    boolean reqPbapDownloadAllVcards(String address, int storage, int notifyFreq, boolean isUseContactsProvider);
    
    /**
	 * Request to download phone book by Vcard from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IPbapCallback#onPbapStateChanged onPbapStateChanged} and {@link IPbapCallback#onPbapDownloadNotify onPbapDownloadNotify} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.	
	 *<p><value>=0 all vcards would be downloaded first, and inserted to database. Only one callback would be invocated.
	 *<p><value>>0 Callbacks would be invocated every "notifyFreq" vcards have been downloaded and inserted to database. 
	 * @return true to indicate the operation has been done without error, or false on immediate error or system is busy.	 
	 */
    boolean reqCancelPbapDownload(String address);
    
    /*
	 * Request to check if database is available for query.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapDatabaseAvailable retPbapDatabaseAvailable} to be notified when database is available.
	 *
	 * @param address : valid Bluetooth MAC address.	    
	 */ 
    void reqPbapDatabaseAvailable(String address);    
    
    /**
	 * Request to query database the corresponding name of given phone number.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapQueryName retPbapQueryName} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.
     * @param phoneNumber : the phone number given to database query.
     */
	void reqPbapQueryName(String address, String phoneNumber);
	
	/**
	 * Request to delete data by specific address from database of PBAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapDeleteDatabaseByAddressCompleted retPbapDeleteDatabaseByAddressCompleted} to be notified when database has been deleted.	 
	 *
	 * @param address : valid Bluetooth MAC address.
	 */      
    void reqPbapDeleteDatabaseByAddress(String address);

    /**
	 * Request to clean database of PBAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapCleanDatabaseCompleted retPbapCleanDatabaseCompleted} to be notified when database has been cleaned.	 
	 */     
    void reqPbapCleanDatabase();
    
    /**
	 * Request to interrupt the ongoing download on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Clients should register and implement callback function {@link IPbapCallback#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.    
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true if really try to interrupt.
	 */
    boolean reqPbapDownloadInterrupt(String address);

    /** 
	 * Request for the current download state of remote connected PBAP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".	 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return current state of PBAP profile service.
	 */ 
    int getPbapCurrentState(String address);

	/* ======================================================================================================================================================== 
	 * MAP
	 */
    
	/** 
	 * Register callback functions for MAP profile.
	 * Call this function to register callback functions for MAP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
    boolean registerMapCallback(UiMapCallback cb);   
    
	/** 
	 * Remove callback functions for MAP profile.
     * Call this function to remove previously registered callback interface for MAP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */ 
    boolean unregisterMapCallback(UiMapCallback cb);    
    
    /**
	 * Request to download all messages from remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IMapCallback#onMapStateChanged onMapStateChanged} to be notified of subsequent result.
	 * Besides, clients should also register and implement callback function
	 * {@link IMapCallback#onMapDownloadOutlineCompleted onMapDownloadOutlineCompleted} to be notified of subsequent result if
	 * parameter isContentDownload is set to false.	 
	 *
	 * @param address : valid Bluetooth MAC address.	
	 * @param folder :
	 *		<p><blockquote>MAP_INBOX_OUTBOX		(int) 0
	 *		<br>MAP_INBOX_ONLY					(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>	
	 * @param notifyFreq : define the callback frequency.
	 *<p><value>=0 all messages would be downloaded first, and inserted to database. Only one callback would be invocated.
	 *<p><value>>0 Callbacks would be invocated every "notifyFreq" messages have been downloaded and inserted to database. 
	 * @param isContentDownload :
	 * <p><value>=false, download outline only
	 * <p><value>=true, download all messages including the contents, but this will let all message set to read status
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
    boolean reqMapDownloadAllMessages(String address, int folder, int notifyFreq, boolean isContentDownload);    
    
    /**
	 * Request to download single message from remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IMapCallback#onMapStateChanged onMapStateChanged} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.	
	 * @param folder :
	 *		<p><blockquote>MAP_INBOX_ONLY		(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>
	 * @param handle : MAP handle
	 * @return true to indicate the operation has been done without error, or false on immediate error.		 
	 */
	boolean reqMapDownloadSingleMessage(String address, int folder, String handle);
     
    /**
	 * Request to register notification when there is new message on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Clients should register and implement callback function {@link IMapCallback#onMapNotificationStateChanged onMapNotificationStateChanged} and
	 * {@link IMapCallback#onMapNewMessageReceived onMapNewMessageReceived}
	 * to be notified of subsequent result.    
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @param downloadNewMessage : if true, download the new message, too
	 */
    boolean reqMapRegisterNotification(String address, boolean downloadNewMessage);
    
    /**
	 * Request to unregister new message notification on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB". 
	 *
	 * @param address : valid Bluetooth MAC address.
	 */
    void reqMapUnregisterNotification(String address); 
    
	/** 
	 * Return true if the new message notification is registered on device with given address.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 * @return true if registered.
	 */ 
    boolean isMapNotificationRegistered(String address);

    /**
	 * Request to interrupt the ongoing download on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Clients should register and implement callback function {@link IMapCallback#onMapStateChanged onMapStateChanged} to be notified of subsequent result.    
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true if really try to interrupt.
	 */
    boolean reqMapDownloadInterrupt(String address);

	/*
	 * Request to check if database is available for query
	 * Client should register and implement {@link IMapCallback#retMapDatabaseAvailable retMapDatabaseAvailable} 
	 * to be notified when database is available.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 */ 
    void reqMapDatabaseAvailable(String address);

    /**
	 * Request to delete data by specific address
	 * Client should register and implement {@link IMapCallback#retMapDeleteDatabaseByAddressCompleted retMapDeleteDatabaseByAddressCompleted} 
	 * to be notified when database is available.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 */
	void reqMapDeleteDatabaseByAddress(String address);
	
    /**
	 * Request to clean database of MAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IMapCallback#retMapCleanDatabaseCompleted retMapCleanDatabaseCompleted} to be notified when database has been cleaned.	 
	 */ 
	void reqMapCleanDatabase();

	/** 
	 * Request for the current download state of remote connected MAP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".	 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return current state of MAP profile service.
	 */ 
    int getMapCurrentState(String address);

    /** 
	 * Request for the current register state of remote connected MAP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".	 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return current state of MAP profile service.
	 */ 
    int getMapRegisterState(String address);  

	/* ======================================================================================================================================================== 
	 * A2DP AVRCP
	 */
	
	/** 
	 * Register callback functions for A2DP and AVRCP profile.
	 * Call this function to register callback functions for A2DP and AVRCP profile.
	 * Allow nFore service to call back to its registered clients, which is often UI.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
	boolean registerA2dpAvrcpCallback(UiA2dpCallback cb);
	
	/** 
	 * Remove callback functions for A2DP and AVRCP profile.
     * Call this function to remove previously registered callback interface for A2DP and AVRCP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */ 
	boolean unregisterA2dpAvrcpCallback(UiA2dpCallback cb);
	
	
	/* ======================================================================================================================================================== 
	 * A2DP v1.0 v1.2
	 */
	
	/** 
	 * Request to connect A2DP and AVRCP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} and {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both A2DP and AVRCP will be connected and the sequence of state changed callback of profiles! 
	 * This depends on the behavior of connected device.
	 *
	 * @attention : We only support one connection of all and restrict A2DP and AVRCP should be the same remote device at this released version.
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqA2dpConnect(in String address);
	
	/** 
	 * Request to disconnect the connected A2DP and AVRCP connections to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} and {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both A2DP and AVRCP will be disconnected and the sequence of state changed callback of profiles! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqA2dpDisconnect(in String address);
	
	/** 
	 * Request for the hardware Bluetooth address of connected remote A2DP/AVRCP device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected A2DP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */ 
	String getA2dpConnectedDeviceAddress();
	
	/** 
	 * Request for the current state of A2DP profile.
	 * This request is for profile service but not specific remote device.
	 *
	 * @return current state of A2DP profile service.
	 */ 
	int getA2dpCurrentState();
	
	/** 
	 * Request for the current state of AVRCP profile.
	 * This request is for profile service but not specific remote device.
	 *
	 * @return current state of AVRCP profile service.
	 */ 
	int getAvrcpCurrentState();
	
	/** 
	 * Request to set the volume of A2DP streaming music locally.
	 * Volume level is indicated by the unit of millibels. 
	 * Besides, due to the limit of system, this API could only lower down the volume, 
	 * which is done by giving negative value to parameter vol, and set volume back to the original level,
	 * which is done by giving zero to parameter vol.
	 * If the player is muted, which is done by calling {@link INforeService#setA2dpLocalMute setA2dpLocalMute}, 
	 * calls to this API will still change the internal volume level, but this will have no audible effect until
	 * the player is unmuted.
	 * Never provide the positive value to parameter vol.
	 * This is an asynchronous call: it will return immediately. 
	 *
	 * @param vol : volume level to set. The possible values are from -32768 (-0x7FFF-1) to 0 (0x00).
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean setA2dpLocalVolume(int vol);
	
	/** 
	 * Request to mute the A2DP streaming music locally.
	 * If the player is muted by calling this API, calls to {@link INforeService#setA2dpLocalVolume setA2dpLocalVolume} will still change the internal volume level, 
	 * but this will have no audible effect until the player is unmuted by calling this API again.
	 * This is an asynchronous call: it will return immediately. 
	 *
	 * @param isMute : boolean indicating whether to mute or unmute.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean setA2dpLocalMute(boolean isMute);
	
	
	/* ======================================================================================================================================================== 
	 * AVRCP v1.0
	 */	

	/** 
	 * Request for the hardware Bluetooth address of connected remote AVRCP device.
	 * There might be no need for users to concern for this in general case. Users could call 
	 * {@link INforeService#getA2dpConnectedDeviceAddress getA2dpConnectedDeviceAddress} to get the connected A2DP/AVRCP device address.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected AVRCP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */ 
	String getAvrcpConnectedDeviceAddress();
	
	/** 
	 * Request if AVRCP 1.3 is supported by remote device with given Bluetooth hardware address.
	 * The requested address must be the paired device and is connected currently.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : valid Bluetooth MAC address of paired and connected AVRCP device.
	 * @return false if the device dose not support version 1.3 or is not connected currently.  
	 */
	boolean isAvrcp13Supported(in String address);	
	
	/** 
	 * Request if AVRCP 1.4 is supported by remote device with given Bluetooth hardware address.
	 * The requested address must be the paired device and is connected currently.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : valid Bluetooth MAC address of paired and connected AVRCP device.
	 * @return false if the device dose not support version 1.4 or is not connected currently.  
	 */
	boolean isAvrcp14Supported(in String address);	
	
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Play" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqAvrcpPlay(in String address);
	
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Stop" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqAvrcpStop(in String address);
	
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Pause" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqAvrcpPause(in String address);
	
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Forward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqAvrcpForward(in String address);
	
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Backward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqAvrcpBackward(in String address);
	
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Volume Up" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqAvrcpVolumeUp(in String address);
	
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Volume Down" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */	
	boolean reqAvrcpVolumeDown(in String address);

	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to start the "Fast Forward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpStartFastForward(in String address);

	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to stop the "Fast Forward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpStopFastForward(in String address);

	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to start the "Rewind" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpStartRewind(in String address);

	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to stop the "Rewind" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpStopRewind(in String address);
	
	
	/* ======================================================================================================================================================== 
	 * AVRCP v1.3
	 */		
	 
	/** 
	 * Request to get the supported events of capabilities supported by remote connected A2DP/AVRCP device 
	 * with given Bluetooth hardware address. This is sent by CT to inquire capabilities of the peer device.
	 * This requests the list of events supported by the remote device. Remote device is expected to respond with all the events supported 
	 * including the mandatory events defined in the AVRCP v1.3 specification. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpCapabilitiesSupportEvent retAvrcpCapabilitiesSupportEvent} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */	 
	boolean reqAvrcpGetCapabilitiesSupportEvent(in String address);	 
	 
	/** 
	 * Request to get the supported player application setting attributes provided by remote connected A2DP/AVRCP device 
	 * with given Bluetooth hardware address. 
	 * The list of reserved player application setting attributes is provided in Appendix F of AVRCP v1.3 specification. 
	 * It is expected that a target device may have additional attributes not defined as part of the specification.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingAttributesList retAvrcpPlayerSettingAttributesList} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		  
	boolean reqAvrcpGetPlayerSettingAttributesList(in String address);
	 
	/** 
	 * Request to get the set of possible values for the requested player application setting attribute 
	 * provided by remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * The list of reserved player application setting attributes and their values are provided in Appendix F of AVRCP v1.3 specification. 
	 * It is expected that a target device may have additional attribute values not defined as part of the specification.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingValuesList retAvrcpPlayerSettingValuesList} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpGetPlayerSettingValuesList(in String address, byte attributeId);
	 
	/** 
	 * Request to get the current set values on the remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * for the provided player application setting attribute. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingCurrentValues retAvrcpPlayerSettingCurrentValues} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpGetPlayerSettingCurrentValues(in String address, byte attributeId);
	 
	/** 
	 * Request to set the player application setting of player application setting value on the remote connected A2DP/AVRCP device 
	 * with given Bluetooth hardware address for the corresponding defined PlayerApplicationSettingAttribute. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingValueSet retAvrcpPlayerSettingValueSet} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @param valueId : the setting value. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpSetPlayerSettingValue(in String address, byte attributeId, byte valueId);
	 
	/** 
	 * Request to get the attributes of the element specified in the parameter 
	 * provided by remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpElementAttributesPlaying retAvrcpElementAttributesPlaying} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpGetElementAttributesPlaying(in String address);
	 
	/** 
	 * Request to get the status of the currently playing media at 
	 * remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayStatus retAvrcpPlayStatus} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpGetPlayStatus(in String address);
	 
	/** 
	 * Request to register with remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * to receive notifications asynchronously based on specific events occurring. 
	 * The events registered would be kept on remote device until another
	 * {@link INforeService#reqAvrcpUnregisterEventWatcher reqAvrcpUnregisterEventWatcher} is called.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * <p><blockquote>{@link IA2dpCallback#onAvrcpEventPlaybackStatusChanged onAvrcpEventPlaybackStatusChanged},
	 * <br>{@link IA2dpCallback#onAvrcpEventTrackChanged onAvrcpEventTrackChanged},
	 * <br>{@link IA2dpCallback#onAvrcpEventTrackReachedEnd onAvrcpEventTrackReachedEnd},
	 * <br>{@link IA2dpCallback#onAvrcpEventTrackReachedStart onAvrcpEventTrackReachedStart},
	 * <br>{@link IA2dpCallback#onAvrcpEventPlaybackPosChanged onAvrcpEventPlaybackPosChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventBatteryStatusChanged onAvrcpEventBatteryStatusChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventSystemStatusChanged onAvrcpEventSystemStatusChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventPlayerSettingChanged onAvrcpEventPlayerSettingChanged},
 	 * <br>v1.4
 	 * <br>{@link IA2dpCallback#onAvrcpEventNowPlayingContentChanged onAvrcpEventNowPlayingContentChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventAvailablePlayerChanged onAvrcpEventAvailablePlayerChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventAddressedPlayerChanged onAvrcpEventAddressedPlayerChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventUidsChanged onAvrcpEventUidsChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventVolumeChanged onAvrcpEventVolumeChanged}, and
	 * <br>{@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} </blockquote>
	 * to be notified of subsequent result.
	 * Each corresponding callback would be invoked once immediately after the event has been registered successfully. 
	 *
	 * @attention : Although we implement all these events required by specification, 
	 * we STRONGLY suggest that please AVOID using the following three events :
	 *		<p><blockquote>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07	 
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d	</blockquote>	 
	 *  
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param eventId : the registering event ID. Possible values are
	 *		<p><blockquote>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED				(byte) 0x01
	 *		<br>AVRCP_EVENT_ID_TRACK_CHANGED						(byte) 0x02
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_END					(byte) 0x03
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_START					(byte) 0x04
	 *		<br>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED					(byte) 0x05
	 *		<br>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07
	 *		<br>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED	(byte) 0x08
	 *		<br>v1.4
	 *		<br>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED			(byte) 0x09
	 *		<br>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED			(byte) 0x0a
	 *		<br>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED				(byte) 0x0b
	 *		<br>AVRCP_EVENT_ID_UIDS_CHANGED							(byte) 0x0c
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d</blockquote>		
	 * @param interval : the update interval in second. This parameter applicable only for
	 * AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED. For other events, this parameter is ignored !
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpRegisterEventWatcher(in String address, byte eventId, long interval);
	 
	/** 
	 * Request to unregister the specific events with remote connected A2DP/AVRCP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param eventId : the unregistering event ID. Possible values are
	 *		<p><blockquote>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED				(byte) 0x01
	 *		<br>AVRCP_EVENT_ID_TRACK_CHANGED						(byte) 0x02
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_END					(byte) 0x03
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_START					(byte) 0x04
	 *		<br>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED					(byte) 0x05
	 *		<br>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07
	 *		<br>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED	(byte) 0x08
	 *		<br>v1.4	 
	 *		<br>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED			(byte) 0x09
	 *		<br>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED			(byte) 0x0a
	 *		<br>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED				(byte) 0x0b
	 *		<br>AVRCP_EVENT_ID_UIDS_CHANGED							(byte) 0x0c
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d</blockquote>		
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpUnregisterEventWatcher(in String address, byte eventId);
	 
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to abort the continuing response.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpMetadataReceivingAborted retAvrcpMetadataReceivingAborted} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpAbortMetadataReceiving(in String address);	 
	 
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to move to the first song in the next group.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	 
 	boolean reqAvrcpNextGroup(in String address);
	 
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to move to the first song in the previous group.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	 
	boolean reqAvrcpPreviousGroup(in String address);
	 	 
	/** 
	 * Request to set the company ID on the remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.	
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param companyId : the setting company ID. Company-ID is 24bit unsigned value, initialized to Vendor_Unique (0x001958) when
	 * called on Connect().
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 	 
	boolean reqAvrcpSetMetadataCompanyId(in String address, int companyId);
	 
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to get 
	 * the supported player application setting attributes displayable text for the provided PlayerApplicationSettingAttributeID 
	 * This command is expected to be used only for extended attributes for menu navigation. 
	 * It is assumed that all <attribute, value> pairs used for menu extensions are statically defined by remote device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingAttributeText retAvrcpPlayerSettingAttributeText} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.		 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpGetPlayerSettingAttributeText(in String address, byte attributeId);
	 
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to get 
	 * the supported player application setting value displayable text for the provided player application setting attribute value. 
	 * This command is expected to be used only for extended attributes for menu navigation. 
	 * It is assumed that all <attribute, value> pairs used for menu extensions are statically defined by remote device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingValueText retAvrcpPlayerSettingValueText} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.		 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @param valueId : the requesting value. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpGetPlayerSettingValueText(in String address, byte attributeId, byte valueId);
	 	 
	/** 
	 * Request to informs the character set we supported to remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * This shall allow remote device to send responses with strings in the character set we supported.
	 * By default remote device shall send strings in UTF-8 if this command has not been sent.
	 * It is mandatory to send UTF-8 as one of the supported character set.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpDisplayableCharsetInformed retAvrcpDisplayableCharsetInformed} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. AVRCP's default charaset is UTF-8, so don't change the character set used by this command.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param charsetId : the supported charset IDs.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpInformDisplayableCharset(in String address, in int[] charsetId);
	
	/** 
	 * Request to informs the loacl battery status changed to remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpBatteryStatusOfCtInformed retAvrcpBatteryStatusOfCtInformed} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.		 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param batteryStatusId : the battery status. Possible values are
	 *		<p><blockquote>AVRCP_BATTERY_STATUS_ID_NORMAL		(byte) 0x00
	 *		<br>AVRCP_BATTERY_STATUS_ID_WARNING		(byte) 0x01
	 *		<br>AVRCP_BATTERY_STATUS_ID_CRITICAL	(byte) 0x02
	 * 		<br>AVRCP_BATTERY_STATUS_ID_EXTERNAL	(byte) 0x03
	 * 		<br>AVRCP_BATTERY_STATUS_ID_FULL		(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpInformBatteryStatusOfCt(in String address, byte batteryStatusId);	 
	 
	/* ======================================================================================================================================================== 
	 * AVRCP v1.4
	 */	
	 
	/** 
	 * Request to inform the remote connected A2DP/AVRCP device with given Bluetooth hardware address 
	 * of which media player we wishes to control.
	 * The player is selected by its "Player Id".
	 * When the addressed player is changed, whether locally on the remote device or explicitly by us, 
	 * the remote device shall complete notifications following the mechanism described in section 6.9.2 of AVRCP v1.4 specification. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpAddressedPlayerSet retAvrcpAddressedPlayerSet} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param playerId : the selected player ID of 2 octets. 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		 
	boolean reqAvrcpSetAddressedPlayer(in String address, int playerId);
	
	/** 
	 * Request the remote connected A2DP/AVRCP device with given Bluetooth hardware address to control to which player browsing commands should be routed.
	 * The player to which AVRCP shall route browsing commands is the browsed player. 
	 * This command shall be sent successfully before any other commands are sent on the browsing channel except 
	 * {@link INforeService#reqAvrcpGetFolderItems reqAvrcpGetFolderItems}
	 * in the Media Player List scope. 
	 * If the browsed player has become unavailable this command shall be sent successfully again before further commands are sent on the browsing channel. 
	 * Some players may support browsing only when set as the Addressed Player.
	 * The player is selected by its "Player Id".
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpBrowsedPlayerSet retAvrcpBrowsedPlayerSet} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param playerId : the selected player ID of 2 octets. 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */				
	boolean reqAvrcpSetBrowsedPlayer(in String address, int playerId);
	
	/** 
	 * Request to retrieve a listing of the contents of a folder on remote connected A2DP/AVRCP device with given Bluetooth hardware address.
	 * The folder is the representation of available media players, virtual file system, the last searching result, or the playing list.
	 * Should not issue this command to an empty folder.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpFolderItems retAvrcpFolderItems} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : the requesting folder ID. Possible values are
	 * 		<p><blockquote>AVRCP_SCOPE_ID_MEDIA_PLAYER	(byte) 0x00
	 *		<br>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */	
	boolean reqAvrcpGetFolderItems(in String address, byte scopeId);
	
	/** 
	 * Request to navigate the virtual filesystem on remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * This command allows us to navigate one level up or down in the virtual filesystem.
	 * Uid counters parameter is used to make sure that our uid cache is consistent with what remote device has currently. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPathChanged retAvrcpPathChanged} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the folder to navigate to. This may be retrieved via a 
	 * {@link INforeService#reqAvrcpGetFolderItems reqAvrcpGetFolderItems} command. 
	 * If the navigation command is "Folder Up" this field is reserved..	 
	 * @param direction : the requesting operation on selested UID. Possible values are
	 * 		<p><blockquote>AVRCP_FOLDER_DIRECTION_ID_UP	(byte) 0x00
	 *		<br>AVRCP_FOLDER_DIRECTION_ID_DOWN	(byte) 0x01</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpChangePath(in String address, int uidCounter, long uid, byte direction);
	
	/** 
	 * Request to retrieve the metadata attributes for a particular media element item or folder item 
	 * on remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * When the remote device supports this command, we shall use this command and not {@link INforeService#reqAvrcpGetElementAttributesPlaying reqAvrcpGetElementAttributesPlaying}. 
	 * To retrieve the Metadata for the currently playing track we should register to receive Track Changed Notifications. 
	 * This shall then provide the UID of the currently playing track, which can be used in the scope of the Now Playing folder.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpItemAttributes retAvrcpItemAttributes} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : the requesting folder ID. Possible values are
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the media element item or folder item to return the attributes for. UID 0 shall not be used.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */			
	boolean reqAvrcpGetItemAttributes(in String address, byte scopeId, int uidCounter, long uid);
				
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address 
	 * to starts playing an item indicated by the UID. It is routed to the Addressed Player. 
	 * If a UID changed event has happened on the remote device, but not yet received by us, we may send this command with the previous UID counter. 
	 * In this case a failure status shall be returned.
	 * Request this command with the scope parameter of 
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01 or 
	 * 		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02</blockquote>
	 * shall result in the NowPlaying folder being invalidated. The old content may not be valid any more or may contain additional items. 
	 * What is put in the NowPlaying folder depends on both the media player and its state, however the item selected by us shall be included.
	 * Request this command with the scope parameter of 
	 * 		<p><blockquote>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * should not change the contents of the NowPlaying Folder, the only effect is that the new item is played.
	 * Never request this command with the scope parameter 
	 * 		<p><blockquote>AVRCP_SCOPE_ID_MEDIA_PLAYER	(byte) 0x00.</blockquote>
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpSelectedItemPlayed retAvrcpSelectedItemPlayed} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : The scope in which the UID of the media element item or folder item, if supported, is valid. Possible values are
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the media element item or folder item, if supported, to be played.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpPlaySelectedItem(in String address, byte scopeId, int uidCounter,long uid);
	
	/** 
	 * Request to perform the basic search from the current folder and all folders below that in the Browsed Players virtual filesystem. 
	 * Regular expressions shall not be supported. 
	 * Search results are valid until
	 * 		<p><blockquote>Another search request is performed or
	 *		<br>A UIDs changed notification response is received
	 * 		<br>The Browsed player is changed</blockquote>
	 * The search result would contain only media element items, not folder items.
	 * Searching may not be supported by all players. Furthermore, searching may only be possible on some players 
	 * when they are set as the Addressed Player.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpSearchResult retAvrcpSearchResult} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param text : The string to search on in the specified character set.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpSearch(in String address, in String text);
	
	/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address 
	 * to adds an item indicated by the UID to the NowPlaying queue. 
	 * If a UID changed event has happened on the remote device, but not yet received by us, we may send this command with the previous UID counter. 
	 * In this case a failure status shall be returned.
	 * Request this command with the scope parameter of 
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 * 		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02 or
	 * 		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * shall result in the item being added to the NowPlaying folder on media players that support this command.
	 * Never request this command with the scope parameter 
	 * 		<p><blockquote>AVRCP_SCOPE_ID_MEDIA_PLAYER	(byte) 0x00.</blockquote>
	 * We could request this command with the UID of a Folder Item if the folder is playable. 
	 * The result of this is that the contents of that folder are added to the NowPlaying folder, not the folder itself.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpNowPlayingAdded retAvrcpNowPlayingAdded} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : The scope in which the UID of the media element item or folder item, if supported, is valid. Possible values are
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the media element item or folder item, if supported, to be played.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpAddToNowPlaying(in String address, byte scopeId, int uidCounter, long uid);	

	/** 
	 * Request if remote connected A2DP/AVRCP device with given Bluetooth hardware address has browsing channel established. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the remote device has browsing channel.
	 */			 	 	 
	boolean isAvrcpBrowsingChannelEstablished(in String address);
	
	/** 
	 * By the AVRCP v1.4 specification, this command is used to set an absolute volume to be used by the rendering device. 
	 * This is in addition to the relative volume commands. 
	 * It is expected that the audio sink will perform as the TG role for this command.
	 * As this command specifies a percentage rather than an absolute dB level, the CT should exercise caution when sending this command.
	 * It should be noted that this command is intended to alter the rendering volume on the audio sink. 
	 * It is not intended to alter the volume within the audio stream. The volume level which has actually been set on the TG is returned in the response. 
	 * This is to enable the CT to deal with whatever granularity of volume control the TG provides.
	 * The setting volume is represented in one octet. The top bit (bit 7) is reserved for future definition. 
	 * The volume is specified as a percentage of the maximum. The value 0x0 corresponds to 0%. The value 0x7F corresponds to 100%.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpAbsoluteVolumeSet retAvrcpAbsoluteVolumeSet} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param volume : the setting volume value of octet from 0x00 to 0x7F.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */		
	boolean reqAvrcpSetAbsoluteVolume(in String address, byte volume);	
	
	/**
	 * Request to re-start the A2DP native socket thread for playing out stream data.
	 * <br>This request MUST be called if application has called {@link INforeService#setA2dpNativeStop setA2dpNativeStop}
	 * <br>This request is only useful for some specific situation, and application might not need to use this in general case.  
	 */ 	
	void setA2dpNativeStart();
	
	/**
	 * Request to stop the A2DP native socket thread immediately.
	 * <br>{@link INforeService#setA2dpNativeStart setA2dpNativeStart} MUST be called after anything you want to do if this API was called.
	 * <br>Otherwise, there would be no audio playing out anymore !!! 
	 * <br>This request is only useful for some specific situation, and application might not need to use this in general case.  
	 */ 	
	void setA2dpNativeStop();
	
	/**
	 * Return false if A2DP native socket thread was stopped by {@link INforeService#setA2dpNativeStop setA2dpNativeStop}.
	 *
	 * @return false if A2DP native socket thread was stopped.
	 */ 	
	boolean isA2dpNativeRunning();
	
	/**
	 * Request song status 
	 * include title , artist , album
	 * by retAvrcpUpdateSongStatus() callback
	 */

	 void reqAvrcpUpdateSongStatus();

	 	/* ======================================================================================================================================================== 
	 * PAN Profile
	 */


	/** 
	 * Register callback functions for PAN profile.
	 * Call this function to register callback functions for PAN profile.
	 * Allow nFore service to call back to its registered clients, which is often UI.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
	boolean registerPanCallback(UiPanCallback cb);
	
	/** 
	 * Remove callback functions for PAN profile.
     * Call this function to remove previously registered callback interface for PAN profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */ 
	boolean unregisterPanCallback(UiPanCallback cb);
	
	/** 
	 * Request to connect PAN to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IPanCallback#onPanStateChanged onPanStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that PAN will be connected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqPanConnect(in String address);
	
	/** 
	 * Request to disconnect the connected PAN connections to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IPanCallback#onPanStateChanged onPanStateChanged}
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both PAN will be disconnected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqPanDisconnect(in String address);
	
	/** 
	 * Request for the hardware Bluetooth address of connected remote PAN device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected PAN device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */ 
	String getPanConnectedDeviceAddress();
	
	/** 
	 * Request for the current state of PAN profile.
	 * This request is for profile service but not specific remote device.
	 *
	 * @return current state of PAN profile service.
	 */ 
	int getPanCurrentState();
	
	
	/* ======================================================================================================================================================== 
	 * HID Profile
	 */

	/** 
	 * Register callback functions for HID profile.
	 * Call this function to register callback functions for HID profile.
	 * Allow nFore service to call back to its registered clients, which is often UI.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 
	boolean registerHidCallback(UiHidCallback cb);
	
	/** 
	 * Remove callback functions for HID profile.
     * Call this function to remove previously registered callback interface for HID profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */ 
	boolean unregisterHidCallback(UiHidCallback cb);
	
	/** 
	 * Request to connect HID to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is a blocking call and clients should register and implement callback functions 
	 * {@link IHidCallback#onHidStateChanged onHidStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that HID will be connected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqHidConnect(in String address);
	
	/** 
	 * Request to disconnect the connected HID connections to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an blocking call and clients should register and implement callback functions 
	 * {@link IHidCallback#onHidStateChanged onHidStateChanged}
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both HID will be disconnected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 	
	boolean reqHidDisconnect(in String address);
	
	
	/** 
	 * Request to send HID mouse command to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".. 
	 * It should be noticed that the function will become usefully after HID connect success.
	 * The return value is an integer means this function has sent data to the given Bluetooth hardware address. 0 means fail.
	 * @param button : You should put the correct value to this parameter. For exmple : 0x01 means left button. 0x02 means right button.	
	 *				   Please refers to "USB HID Usage Tables, v1.12, page 67". 		   
	 * @param offset_x : You should put x-direction of mouse offset on this parameter. The range should be in (32768 ~ -32768).
	 *				   The parameter is the relative value of last position.
	 * @param offset_y : You should put y-direction of mouse offset on this parameter. The range should be in (32768 ~ -32768).
	 *				   The parameter is the relative value of last position.
	 * @param wheel : You should put wheel information of mouse offset on this parameter. The range should be in (127 ~ -127).
	 *				   The parameter is the relative value of last position.
	 * @return integer to indicate the operation has been done, or 0 on immediate error.
	 */ 
	int sendHidMouseCommand(int button, int offset_x, int offset_y, int wheel);
	
	
	/** 
	 * Request to send virtual key event to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".. 
	 * It should be noticed that the function will become usefully after HID connect success.
	 * The return value is an integer means this function has sent data to the given Bluetooth hardware address. 0 means fail.
	 * About the key_1 and key_2 value, please refers to "USB HID Usage Tables, v1.12, page 75-102".
	 * @param key_1 : You should put virtual key command on this parameter. For example, 0x223 means home key. 0x224 means back button.
	 * 					The range should be in (1 ~ 652).
	 * @param key_2 : You should put virtual key command on this parameter. For example, 0x223 means home key. 0x224 means back button.
	 * 					The range should be in (1 ~ 652).
	 * @return integer to indicate the operation has been done, or 0 on immediate error.
	 */ 
	int sendHidVirtualKeyCommand(int key_1, int key_2);
	
	/** 
	 * Request for the current state of HID profile.
	 * This request is for profile service but not specific remote device.
	 *
	 * @return current state of HID profile service.
	 */ 
	int getHidCurrentState();

	/*
	 * Add for HSP page to use.
	 */
	void setTargetAddress(String address);

	/*
	 * Add for HSP page to use.
	 */
	String getTargetAddress();
	
	/** 
	 * Request to get the paired device list.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#retBluetoothPairedDevices retBluetoothPairedDevices}
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 		
	boolean reqBluetoothFoundDevices();
	
	/** 
	 * Request to get the paired device list.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#retBluetoothPairedDevices retBluetoothPairedDevices}
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */ 		
	int reqBluetoothPairedDevicesCount();
	
	int getConnectedHfpDeviceCount();
	
	boolean reqAvrcpPlayPause();
	
	boolean reqSoftwareUpdate();
	
	boolean isFirstBootUp();
	
	int getSppCurrentState(String address);
	
	void setHfpAutoAnswer(boolean auto);
	
	void setActivePhone(String address);
	
	void setTouchesOptions(boolean enable);
	
	boolean isHfpConnectedAppleDevice();
	
	boolean isAppleDeviceFinishAuth();
	
	int getAppleDeviceAuthState();
	
	String getHfpConnectedDeviceVendorID();
	
	boolean isHfpAutoAnswer();
	
	boolean isAlreadyReadPairedDevices();
	
	boolean isForiPodDisconnectA2DP();
	
	boolean isPhonebookAutoSync();
	
	boolean isTwinConnectEnable();
	
	void setTwinConnectEnable(boolean enable);
	
	void setPinCode(String pincode);
	
	String getSoftwareVersion();
	
	void setPhonebookAutoSyncEnable(boolean enable);
	
	boolean reqHfpMicSelection();
	
	int getHfpMicSelection();
	
	boolean reqHfpMicMute();
	
	boolean reqHfpPttDialCall(String number);
	
	boolean getHfpMicMuteState();
	
	int getHfpCallState();
	
	int getHfpCallCounts();
	
	int getAutoConnectionStatus();
	
	String getCurrentCallNumber();
	
	String getThreeWayCallNumber();
	
	int getCallTime(String number);
	
	String getCallerId(String number);
	
	int getThreeWayCallTime();
	
	int getCurrentSyncType();
	
	int getCurrentSyncState();
	
	boolean getImportVCardState();
	
	boolean avrcpPlayOrPause();
	
	String getCurrentSource();
	
	void requestAudioFocus();
	
	void requestBTAudioChannel();
	
	boolean isA2dpPlaying();
	
	boolean sendSppMessage(String address, String message);
	
	int getPhoneScreenState();
	int getPhoneScreenType();
	
	String getPhoneResolution();
	
	int getDeviceX();
	int getDeviceY();
	
	int getHXPhoneLen();
	int getHYPhoneLen();
	int getPXPhoneLen();
	int getPYPhoneLen();
	
	int getHCaliX1();
	int getHCaliX2();
	int getHCaliY1();
	int getHCaliY2();
	
	int getPCaliX1();
	int getPCaliX2();
	int getPCaliY1();
	int getPCaliY2();
	
	boolean isCalibration();
	int getCalibrationState();
	int getCalibrationClick();
	
	String getCurrentImei();
	
	String getLastDialNumber();
	
	void setCalibration(boolean state);
	
	boolean enterBluetoothTestMode();
	
	void reqAvrcpCtPlayerAudio(boolean audioFlag);
	void reqHfpCallAudio(boolean audioFlag);
	
	boolean setOutputFrequencyChannel(int mode, int channelRange);
	
	boolean setOutputFrequencyChannelExtended(int mode);
	
}
