package nfore.android.bt.servicemanager;


/**
 * The callback interface for Serial Port profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 */

 
interface ISppCallback{

    /**
	 * Callback to inform change in SPP state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED		(int) 100
	 * 		<br>STATE_READY								(int) 110
	 * 		<br>STATE_CONNECTING						(int) 120
	 * 		<br>STATE_DISCONNECTING						(int) 125
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
    void onSppStateChanged(String address, String deviceName, int prevState, int newState);

    /**
	 * Callback to inform the error response with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device.
	 * @param errorCode : the possible reason of error. 
     */
    void onSppErrorResponse(String address, int errorCode);
    
    /**
	 * Callback to inform response to {@link INforeService#reqSppConnectedDeviceAddressList reqSppConnectedDeviceAddressList}
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
     * @param totalNum : total number of device addresses in connected list 
     * @param addressList : connected Bluetooth device address
     * @param nameList : connected Bluetooth device name
     */
    void retSppConnectedDeviceAddressList(int totalNum, in String[] addressList, in String[] nameList);    

    /**
	 * Callback to inform the data have been received from device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device.
     * @param receivedData : the data received
     */
    void onSppDataReceived(String address, in byte[] receivedData);
        
}
