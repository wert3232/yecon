package nfore.android.bt.servicemanager;

/** 
 * The callback interface for Headset profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 *
 * @author Fred
 * @version 1.0
 */
interface UiHspCallback {

    /** 
	 * Callback to inform change in HSP state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED		(int) 100
	 * 		<br>STATE_READY								(int) 110
	 * 		<br>STATE_CONNECTING						(int) 120
	 *		<br>STATE_CONNECTED							(int) 140
	 *		<br>STATE_OUTGOING_CALL						(int) 160 
	 * 		<br>STATE_INCOMING_CALL						(int) 165	 
	 *		<br>STATE_CALL_IS_ACTIVE					(int) 180</blockquote>	 
	 * Parameter address is only valid in state greater than STATE_READY.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 * However, STATE_OUTGOING_CALL, STATE_INCOMING_CALL and STATE_CALL_IS_ACTIVE do not support at this released version.
	 * Bluetooth HSP specification dose not require these states so we could not distinguish these states by limited information at this moment. 
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param deviceName : the name of remote device.
	 * @param prevState : the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.
	 * @param newState : the new state.
     */     
    void onHspStateChanged(String address, String deviceName, int prevState, int newState);
    
    /** 
	 * Callback to inform change in HSP SCO state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED		(int) 100
	 * 		<br>STATE_READY								(int) 110
	 * 		<br>STATE_CONNECTING						(int) 120
	 *		<br>STATE_CONNECTED							(int) 140</blockquote>
	 * Parameter address is only valid in state greater than STATE_READY.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param deviceName : the name of remote device.
	 * @param prevState : the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.
	 * @param newState : the new state.
     */     
    void onHspScoStateChanged(String address, String deviceName, int prevState, int newState);    
       
    /**
	 * Callback to inform the error response with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device.
	 * @param errorCode : the possible reason of error.
     */     
    void onHspErrorResponse(String address, int errorCode);        
    
    /**
	 * Callback to inform a new incoming call of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB". 
	 * This callback is used mainly to help state changed management.
     * 
	 * @param address : Bluetooth MAC address of remote device.
     */     
    void onHspNewIncomingCall(String address);
    
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
	void onHspSpeakerVolumeChanged(String address, String prevVolume, String newVolume);    

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
	void onHspMicVolumeChanged(String address, String prevVolume, String newVolume);
	

}