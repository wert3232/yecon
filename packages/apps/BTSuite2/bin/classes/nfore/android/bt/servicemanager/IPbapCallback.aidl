package nfore.android.bt.servicemanager;

/**
 * The callback interface for Phonebook Access profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 *
 * @author Space
 * @version 1.1
 */   

interface IPbapCallback{

    
    /**
	 * Callback to inform change in PBAP state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED		(int) 100
	 * 		<br>STATE_READY								(int) 110
	 *		<br>STATE_CONNECTED							(int) 140
	 *		<br>STATE_DB_UPDATE							(int) 142</blockquote>	 
	 * Parameter address is only valid in state greater than STATE_READY.
	 * 
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 * STATE_CONNECTED implies the vcard downloading.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param deviceName : the name of remote device.
	 * @param prevState : the previous state. 
	 * @param newState : the new state.
	 * @param reason : the reason of state changed back to STATE_READY which means download finished. Possible values are
	 * 		<p><blockquote>REASON_DOWNLOAD_FULL_CONTENT_COMPLETED		(int) 1
	 * 		<br>REASON_DOWNLOAD_FAILED									(int) 5
	 *		<br>REASON_DOWNLOAD_TIMEOUT									(int) 6</blockquote>	
	 * In other cases, it would be -1.	 
	 * @param itemCounts : the number of vcards downloaded and updated. 
	 * This parameter is only available and meaningful when state changed from STATE_CONNECTED to STATE_DB_UPDATE and STATE_DB_UPDATE to STATE_READY. 
	 * In other cases, it would be -1.
     */
    void onPbapStateChanged(String address, String deviceName, int prevState, int newState, int reason, int itemCounts);
       
    /**
	 * Callback to inform the specified number of vcards have been downloaded from 
	 * remote connected device with given Bluetooth hardware address.
	 * This callback might be invocated due to {@link INforeService#reqPbapDownloadAllVcards reqPbapDownloadAllVcards} with 
	 * non-zero parameter notifyFreq set.
     * 
	 * @param address : Bluetooth MAC address of remote device.
	 * @param storage :
	 *		<p><blockquote>PBAP_STORAGE_SIM		(int) 1
	 *		<br>PBAP_STORAGE_PHONE_MEMORY		(int) 2
	 *		<br>PBAP_STORAGE_DIALED_CALLS		(int) 3
	 *		<br>PBAP_STORAGE_MISSED_CALLS		(int) 4
	 *		<br>PBAP_STORAGE_RECEIVED_CALLS		(int) 5</blockquote>	
	 * @param totalItems : total number of vcards would be downloaded
	 * @param currentItems : the number of vcards have been downloaded
     */
    void onPbapDownloadNotify(String address, int storage, int totalItems, int currentItems);    
    
	/**
	 * Callback to inform response to {@link INforeService#reqPbapDatabaseAvailable reqPbapDatabaseAvailable}
	 * for remote connected device with given Bluetooth hardware address.
	 * When this callback is invocated, it means database is available.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which just completed downloading.
     */
    void retPbapDatabaseAvailable(String address);     
        
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
    
	/*
	 * Callback to inform response to {@link INforeService#retPbapDeleteDatabaseByAddress retPbapDeleteDatabaseByAddress}
	 * when database has been deleted.
	 *
	 * @param adrress : Bluetooth MAC address of remote device which just completed deleted 
	 * @param isSuccessed : true if deletion is completed successfully
	 */	
	void retPbapDeleteDatabaseByAddressCompleted(String adrress, boolean isSuccessed);

    /*
	 * Callback to inform response to {@link INforeService#reqPbapCleanDatabase reqPbapCleanDatabase}
	 * when database has been cleaned.
    *
	 * @param isSuccessed : true if database is cleaned successfully
	 */	
	void retPbapCleanDatabaseCompleted(boolean isSuccessed);
}
