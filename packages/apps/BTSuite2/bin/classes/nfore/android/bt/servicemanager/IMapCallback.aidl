package nfore.android.bt.servicemanager;

/**
 * The callback interface for Message Access profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 */


interface IMapCallback{

	/**
	 * Callback to inform change in MAP state of remote device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED		(int) 100
	 * 		<br>STATE_READY								(int) 110
	 * 		<br>STATE_CONNECTING						(int) 120	 
	 *		<br>STATE_CONNECTED							(int) 140</blockquote>
	 * Parameter address is only valid in state greater than STATE_READY.
	 * 
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 * STATE_CONNECTED implies the message downloading or database updating.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param deviceName : the name of remote device.
	 * @param prevState : the previous state. 
	 * @param newState : the new state.
	 * @param reason : the reason of state changed back to STATE_READY which means download finished. Possible values are
	 * 		<p><blockquote>REASON_DOWNLOAD_FULL_CONTENT_COMPLETED		(int) 1
	 * 		<br>REASON_DOWNLOAD_OUTLINE_COMPLETED						(int) 2
	 * 		<br>REASON_DOWNLOAD_SINGLE_MESSAGE_COMPLETED				(int) 3
	 * 		<br>REASON_DOWNLOAD_NEW_MESSAGE_COMPLETED					(int) 4
	 * 		<br>REASON_DOWNLOAD_FAILED									(int) 5
	 *		<br>REASON_DOWNLOAD_TIMEOUT									(int) 6
 	 *		<br>REASON_DOWNLOAD_INTERRUPTED								(int) 8</blockquote>		
	 * @param itemCounts : the number of messages downloaded and updated. 
	 * This parameter is only available and meaningful when reason parameter is REASON_DOWNLOAD_FULL_CONTENT_COMPLETED or REASON_DOWNLOAD_OUTLINE_COMPLETED. 
	 * In other cases, it would be -1.
     */
    void onMapStateChanged(String address, String deviceName, int prevState, int newState, int reason, int itemCounts);    
    
    /**
	 * Callback to inform change in state of notification with remote device.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<br>STATE_READY								(int) 110
	 *		<br>STATE_CONNECTED							(int) 140</blockquote>
	 * Parameter address is only valid in STATE_REGISTERED.
	 * 
	 * In state STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param deviceName : the name of remote device.
	 * @param prevState : the previous state. 
	 * @param newState : the new state.
	 * @param reason : the reason of state changed back to STATE_READY. Possible values are
	 * 		<p><blockquote>REASON_REGISTER_TIMEOUT		(int) 5
	 * 		<br>REASON_UNREGISTER_COMPLETED				(int) 7</blockquote>	 
     */
    void onMapNotificationStateChanged(String address, String deviceName, int prevState, int newState, int reason);    
    
    /**
	 * Callback to inform the new message received on device with given Bluetooth hardware address.
	 * This callback might be invocated due to {@link INforeService#reqMapRegisterNotification reqMapRegisterNotification}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * If parameter downloadNewMessage of {@link INforeService#reqMapRegisterNotification reqMapRegisterNotification} is 
	 * set to false, the parameter sender and outline would be empty string.
	 *
	 * @param address : Bluetooth MAC address of remote device.
     * @param handle : MAP handle.
     * @param sender : the sender of message. 
     * @param message : the message content. 
     * This parameter is only available if parameter downloadNewMessage of {@link INforeService#reqMapRegisterNotification reqMapRegisterNotification} is set to true. 
     */
    void onMapNewMessageReceived(String address, String handle, String sender, String message);
      
    /**
	 * Callback to inform the specified number of vcards have been downloaded from 
	 * remote connected device with given Bluetooth hardware address.
	 * This callback might be invocated due to {@link INforeService#reqMapDownloadAllMessages reqMapDownloadAllMessages} with 
	 * non-zero parameter notifyFreq set.
     * 
	 * @param address : Bluetooth MAC address of remote device.
	 * @param totalItems : total number of messages would be downloaded
	 * @param currentItems : the number of messages have been downloaded
     */
    void onMapDownloadNotify(String address, int totalItems, int currentItems);
    
     /**
	 * Callback to inform response to {@link INforeService#reqMapDatabaseAvailable reqMapDatabaseAvailable}
	 * for remote connected device with given Bluetooth hardware address.
	 * When this callback is invocated, it means database is available.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which just completed downloading.
     */
    void retMapDatabaseAvailable(String address);

    /**
     *  Callback to inform response to {@link INforeService#retMapDeleteDatabaseByAddress retMapDeleteDatabaseByAddress}
     *
     *  @param address which address delete from table
     *  @param isSuccessed said success or not
     */
    void retMapDeleteDatabaseByAddressCompleted(String address, boolean isSuccessed);        
    
    /*
	 * Callback to inform response to {@link INforeService#reqMapCleanDatabase reqMapCleanDatabase}
	 * when database has been cleaned.
	 *
	 *  @param isSuccessed said success or not
	 */	
	void retMapCleanDatabaseCompleted(boolean isSuccessed);
    
}