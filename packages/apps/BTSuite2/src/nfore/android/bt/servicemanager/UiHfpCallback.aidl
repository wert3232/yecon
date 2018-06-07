package nfore.android.bt.servicemanager;

/**import com.adayo.midwareproxy.bluetooth.HfpStatusInfo;
* import com.adayo.midwareproxy.bluetooth.HfpPhoneNumberInfo;
* import com.adayo.midwareproxy.bluetooth.HfpErrorInfo;
**/

/**
 * The callback interface for Hands-free profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 */
    
interface UiHfpCallback {  
    
	/** 
	 * Callback to inform change in HFP state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED		(int) 100
	 * 		<br>STATE_READY								(int) 110
	 * 		<br>STATE_CONNECTING						(int) 120
	 *		<br>STATE_CONNECTED							(int) 140
	 *		<br>STATE_OUTGOING_CALL						(int) 160
	 * 		<br>STATE_INCOMING_CALL						(int) 165
	 * 		<br>STATE_RESP_AND_HOLD						(int) 170
	 *		<br>STATE_CALL_IS_ACTIVE					(int) 180
	 * 		<br>STATE_3WAY_OUTGOING_CALL_S_HOLD			(int) 190
	 *		<br>STATE_3WAY_INCOMING_CALL_S_ACT			(int) 195	 
	 * 		<br>STATE_3WAY_CALL_S_ACT_S_HOLD			(int) 200		 	 
	 *		<br>STATE_3WAY_S_HOLD						(int) 210
	 *		<br>STATE_3WAY_INCOMING_CALL_S_ACT_S_HOLD	(int) 212
	 *		<br>STATE_3WAY_CALL_MULTIPARTY				(int) 214		 	  
	 * 		<br>STATE_3WAY_INCOMING_CALL_S_HOLD			(int) 220
	 *		<br>STATE_3WAY_OUTGOING_CALL_M_HOLD			(int) 222
	 *		<br>STATE_3WAY_INCOMING_CALL_M_ACT			(int) 224
	 * 		<br>STATE_3WAY_CALL_S_ACT_M_HOLD			(int) 230
	 * 		<br>STATE_3WAY_M_HOLD						(int) 240
	 *		<br>STATE_3WAY_INCOMING_CALL_S_ACT_M_HOLD	(int) 242
	 *		<br>STATE_3WAY_CALL_M_ACT_S_HOLD			(int) 244
	 * 		<br>STATE_3WAY_INCOMING_CALL_M_ACT_S_HOLD	(int) 250
	 * 		<br>STATE_3WAY_INCOMING_CALL_M_HOLD			(int) 252</blockquote>
	 * Parameter address is only valid in state greater than STATE_READY.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 * States greater than STATE_CALL_IS_ACTIVE are only useful in 3-Way situation. 
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState : the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.
	 * @param newState : the new state.
     */
    void onHfpStateChanged(String address, int prevState, int newState);
    
	/** 
	 * Callback to inform change in HFP SCO state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED		(int) 100
	 * 		<br>STATE_READY								(int) 110
	 *		<br>STATE_CONNECTED							(int) 140</blockquote>
	 * Parameter address is only valid in state greater than STATE_READY.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState : the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.
	 * @param newState : the new state.
     */
    void onHfpScoStateChanged(String address, int prevState, int newState);    

    /**
	 * Callback to inform change in HFP voice control state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param isVoiceControlOn : return voice control status (On/Off).
     */ 

    void onHfpVoiceControlStateChanged(String address,boolean isVoiceControlOn);

    /**
	 * Callback to inform the error response from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param errorInfo : HFPErrorVO
    	void onHfpErrorResponse(String address, in HfpErrorInfo errorInfo);  
     */
    
    /**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDeviceInfo reqHfpRemoteDeviceInfo}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param deviceInfo : StateVO
    	void retHfpRemoteDeviceInfo(String address, in HfpStatusInfo deviceInfo);
     */
    
    /**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDevicePhoneNumber reqHfpRemoteDevicePhoneNumber}
	 * from remote connected device with given Bluetooth hardware address has received or	 
	 * when our service recognizes there is a change on active phone numbers in multiparty calls situation.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param phoneNumberInfo : PhoneNumberVO
    void onHfpRemoteDevicePhoneNumberChanged(String address, in List<HfpPhoneNumberInfo> phoneNumberInfo); 
     */
    
    /**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDeviceNetworkOperator reqHfpRemoteDeviceNetworkOperator}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param mode : contain the current mode and provides no information with regard to the name of the operator.
     * @param format : specify the format of the <operator> parameter string, and shall always be 0 for this specification.
     * @param operator : specify a quoted string in alphanumeric format representing the name of the network operator. This string shall not exceed 16 characters. 
     */
    void retHfpRemoteDeviceNetworkOperator(String address, String mode, int format, String operator);    
    
    /**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDeviceSubscriberNumber reqHfpRemoteDeviceSubscriberNumber}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param number : quoted string containing the phone number in the format specified
     * @param type : specify the format of the phone number provided, and can be one of the following values:
     * <p><value>=128-143: The phone number format may be a national or international format, and may contain prefix and/or escape digits. No changes on the number presentation are required.
     * <p><value>=144-159: The phone number format is an international number,including the country code prefix. If the plus sign ("+") is not included as part of the number and shall be added by the AG as needed.
     * <p><value>=160-175: National number. No prefix nor escape digits included.
     * @param service : indicate which service this phone number relates to. Shall be either 4 (voice) or 5 (fax).
     */
    void retHfpRemoteDeviceSubscriberNumber(String address, String number, int type, int service);    
    
    /**
	 * Callback to inform the change on "Service" indicator status from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param isTelecomServiceOn : 
	 * <p><value>=false implies no service. No Home/Roam network available.
     * <p><value>=true implies presence of service. Home/Roam network available.  
     */
    void onHfpRemoteDeviceServiceStatusChanged(String address, boolean isTelecomServiceOn);    
    
    /**
	 * Callback to inform the change on "Roaming" indicator status from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param isOnRoaming : 
     *<p><value>=false means roaming is not active.
     *<p><value>=true means a roaming is active.  
     */
    void onHfpRemoteDeviceRoamStatusChanged(String address, boolean isOnRoaming); 
    
    /**
	 * Callback to inform the change on "Battery" indicator status from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param prevStatus : previous battery status
     * @param newStatus : new battery state
     * @param maxStatus : maximum battery status number 
     * @param minStatus : minimum battery status number
     */
    void onHfpRemoteDeviceBatteryStatusChanged(String address, int prevStatus, int newStatus, int maxStatus, int minStatus);    
        
    /**
	 * Callback to inform the change on "Signal" indicator status from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param prevStatus : previous signal strength
     * @param newStatus : new signal strength
     * @param maxStatus : maximum signal strength number 
     * @param minStatus : minimum signal strength number
     */
    void onHfpRemoteDeviceSignalStatusChanged(String address, int prevStatus, int newStatus, int maxStatus, int minStatus);
    
    /**
	 * Callback to inform the state change on phone book downloading from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param state : 
	 *		<p><blockquote>STATE_DOWNLOAD_COMPLETED		(int) 110
	 *		<br>STATE_DOWNLOAD_START					(int) 120
	 *		<br>STATE_DOWNLOAD_PAUSE					(int) 140
	 *		<br>STATE_DOWNLOAD_STOP						(int) 141
	 *		<br>STATE_DOWNLOADING						(int) 150</blockquote>	     
     */
    void onHfpAtDownloadPhonebookStateChanged(String address, int prevState, int newState);
    
    /**
	 * Callback to inform the state change on SMS downloading from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param state : 
	 *		<p><blockquote>STATE_DOWNLOAD_COMPLETED		(int) 110
	 *		<br>STATE_DOWNLOAD_START					(int) 120
	 *		<br>STATE_DOWNLOADING						(int) 150</blockquote>	     
     */
    void onHfpAtDownloadSMSStateChanged(String address, int newState);    
    
    /**
	 * Callback to inform the new incoming call number from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param callNumber : incoming call number
     */
    void onHfpIncomingCallNumber(String address, String callNumber);
    
    /**
	 * Callback to inform the new outgoing call number from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param callNumber : outgoing call number
     */
    void onHfpOutgoingCallNumber(String address, String callNumber);       
    
    /**
	 * Callback to inform the Speaker volume changed on remote connected device with given Bluetooth hardware address.
	 * This callback is mainly used for doing the synchronization but not control.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @warning : RESERVED !!! Due to the compatibility, Please DO NOT USE at this release version !!!		 
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param prevVolume : the previous speaker volume level
     * @param newVolume : the new speaker volume level
     */
    void onHfpSpeakerVolumeChanged(String address, String prevVolume, String newVolume);         
    
    /**
	 * Callback to inform the Mic volume changed on remote connected device with given Bluetooth hardware address.
	 * This callback is mainly used for doing the synchronization but not control.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @warning : RESERVED !!! Due to the compatibility, Please DO NOT USE at this release version !!!		 
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param prevVolume : the previous mic volume level
     * @param newVolume : the new mic volume level
     */
    void onHfpMicVolumeChanged(String address, String prevVolume, String newVolume);

	/**
	 * Callback to inform response to {@link INforeService#reqPbapQueryName reqPbapQueryName}
	 * for remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
     * @param address : Bluetooth MAC address of remote device.
     * @param phoneNumber : the queried phone number.     
     * @param fullName : the corresponding name of specified phone number. This name is meaningful only if isSuccessed is true.
     * @param isSuccessed : indicate that if the corresponding name is retrieved. 
     */
    void retPbapQueryName(String address, String phoneNumber, String fullName, boolean isSuccessed); 

    /**
     * Callback to inform active and hold call number while active call number is changed.
     *
     * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param activeNumber : the active call number
     * @param holdNumber : the hold call number
     */
    void onHfpActiveNumberChanged(String address , String activeNumber , String holdNumber);
    
    void onHfpCallStateChanged(String address, int prevState, int newState);
    
    void onHfpMuteStateChanged(String address, int prevState, int newState);
    
    void onHfpMicSelectionChanged(String address, int prevState, int newState);
    
}