/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\yecon\\yecon_ac8317\\yecon\\packages\\apps\\BTSuite2\\src\\nfore\\android\\bt\\servicemanager\\IHfpCallback.aidl
 */
package nfore.android.bt.servicemanager;
/**import com.adayo.midwareproxy.bluetooth.HfpStatusInfo;
import com.adayo.midwareproxy.bluetooth.HfpPhoneNumberInfo;
import com.adayo.midwareproxy.bluetooth.HfpErrorInfo;
 *//**
 * The callback interface for Hands-free profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 */
public interface IHfpCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements nfore.android.bt.servicemanager.IHfpCallback
{
private static final java.lang.String DESCRIPTOR = "nfore.android.bt.servicemanager.IHfpCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an nfore.android.bt.servicemanager.IHfpCallback interface,
 * generating a proxy if needed.
 */
public static nfore.android.bt.servicemanager.IHfpCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof nfore.android.bt.servicemanager.IHfpCallback))) {
return ((nfore.android.bt.servicemanager.IHfpCallback)iin);
}
return new nfore.android.bt.servicemanager.IHfpCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onHfpStateChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.onHfpStateChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpScoStateChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.onHfpScoStateChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpVoiceControlStateChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.onHfpVoiceControlStateChanged(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_retHfpRemoteDeviceNetworkOperator:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
java.lang.String _arg3;
_arg3 = data.readString();
this.retHfpRemoteDeviceNetworkOperator(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_retHfpRemoteDeviceSubscriberNumber:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
this.retHfpRemoteDeviceSubscriberNumber(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpRemoteDeviceServiceStatusChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.onHfpRemoteDeviceServiceStatusChanged(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpRemoteDeviceRoamStatusChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.onHfpRemoteDeviceRoamStatusChanged(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpRemoteDeviceBatteryStatusChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
this.onHfpRemoteDeviceBatteryStatusChanged(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpRemoteDeviceSignalStatusChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
this.onHfpRemoteDeviceSignalStatusChanged(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpAtDownloadPhonebookStateChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.onHfpAtDownloadPhonebookStateChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpAtDownloadSMSStateChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.onHfpAtDownloadSMSStateChanged(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpIncomingCallNumber:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onHfpIncomingCallNumber(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpOutgoingCallNumber:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onHfpOutgoingCallNumber(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpSpeakerVolumeChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.onHfpSpeakerVolumeChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpMicVolumeChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.onHfpMicVolumeChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_onHfpActiveNumberChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.onHfpActiveNumberChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements nfore.android.bt.servicemanager.IHfpCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
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
@Override public void onHfpStateChanged(java.lang.String address, int prevState, int newState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(prevState);
_data.writeInt(newState);
mRemote.transact(Stub.TRANSACTION_onHfpStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onHfpScoStateChanged(java.lang.String address, int prevState, int newState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(prevState);
_data.writeInt(newState);
mRemote.transact(Stub.TRANSACTION_onHfpScoStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform change in HFP voice control state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param isVoiceControlOn : return voice control status (On/Off).
     */
@Override public void onHfpVoiceControlStateChanged(java.lang.String address, boolean isVoiceControlOn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(((isVoiceControlOn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_onHfpVoiceControlStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform the error response from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param errorInfo : HFPErrorVO
    void onHfpErrorResponse(String address, in HfpErrorInfo errorInfo);   
     *//**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDeviceInfo reqHfpRemoteDeviceInfo}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param deviceInfo : StateVO
    void retHfpRemoteDeviceInfo(String address, in HfpStatusInfo deviceInfo);
     *//**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDevicePhoneNumber reqHfpRemoteDevicePhoneNumber}
	 * from remote connected device with given Bluetooth hardware address has received or	 
	 * when our service recognizes there is a change on active phone numbers in multiparty calls situation.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param phoneNumberInfo : PhoneNumberVO
    void onHfpRemoteDevicePhoneNumberChanged(String address, in List<HfpPhoneNumberInfo> phoneNumberInfo); 
     *//**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDeviceNetworkOperator reqHfpRemoteDeviceNetworkOperator}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param mode : contain the current mode and provides no information with regard to the name of the operator.
     * @param format : specify the format of the <operator> parameter string, and shall always be 0 for this specification.
     * @param operator : specify a quoted string in alphanumeric format representing the name of the network operator. This string shall not exceed 16 characters. 
     */
@Override public void retHfpRemoteDeviceNetworkOperator(java.lang.String address, java.lang.String mode, int format, java.lang.String operator) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(mode);
_data.writeInt(format);
_data.writeString(operator);
mRemote.transact(Stub.TRANSACTION_retHfpRemoteDeviceNetworkOperator, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void retHfpRemoteDeviceSubscriberNumber(java.lang.String address, java.lang.String number, int type, int service) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(number);
_data.writeInt(type);
_data.writeInt(service);
mRemote.transact(Stub.TRANSACTION_retHfpRemoteDeviceSubscriberNumber, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform the change on "Service" indicator status from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param isTelecomServiceOn : 
	 * <p><value>=false implies no service. No Home/Roam network available.
     * <p><value>=true implies presence of service. Home/Roam network available.  
     */
@Override public void onHfpRemoteDeviceServiceStatusChanged(java.lang.String address, boolean isTelecomServiceOn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(((isTelecomServiceOn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_onHfpRemoteDeviceServiceStatusChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform the change on "Roaming" indicator status from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param isOnRoaming : 
     *<p><value>=false means roaming is not active.
     *<p><value>=true means a roaming is active.  
     */
@Override public void onHfpRemoteDeviceRoamStatusChanged(java.lang.String address, boolean isOnRoaming) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(((isOnRoaming)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_onHfpRemoteDeviceRoamStatusChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onHfpRemoteDeviceBatteryStatusChanged(java.lang.String address, int prevStatus, int newStatus, int maxStatus, int minStatus) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(prevStatus);
_data.writeInt(newStatus);
_data.writeInt(maxStatus);
_data.writeInt(minStatus);
mRemote.transact(Stub.TRANSACTION_onHfpRemoteDeviceBatteryStatusChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onHfpRemoteDeviceSignalStatusChanged(java.lang.String address, int prevStatus, int newStatus, int maxStatus, int minStatus) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(prevStatus);
_data.writeInt(newStatus);
_data.writeInt(maxStatus);
_data.writeInt(minStatus);
mRemote.transact(Stub.TRANSACTION_onHfpRemoteDeviceSignalStatusChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onHfpAtDownloadPhonebookStateChanged(java.lang.String address, int prevState, int newState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(prevState);
_data.writeInt(newState);
mRemote.transact(Stub.TRANSACTION_onHfpAtDownloadPhonebookStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onHfpAtDownloadSMSStateChanged(java.lang.String address, int newState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(newState);
mRemote.transact(Stub.TRANSACTION_onHfpAtDownloadSMSStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform the new incoming call number from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param callNumber : incoming call number
     */
@Override public void onHfpIncomingCallNumber(java.lang.String address, java.lang.String callNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(callNumber);
mRemote.transact(Stub.TRANSACTION_onHfpIncomingCallNumber, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform the new outgoing call number from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param callNumber : outgoing call number
     */
@Override public void onHfpOutgoingCallNumber(java.lang.String address, java.lang.String callNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(callNumber);
mRemote.transact(Stub.TRANSACTION_onHfpOutgoingCallNumber, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onHfpSpeakerVolumeChanged(java.lang.String address, java.lang.String prevVolume, java.lang.String newVolume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(prevVolume);
_data.writeString(newVolume);
mRemote.transact(Stub.TRANSACTION_onHfpSpeakerVolumeChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onHfpMicVolumeChanged(java.lang.String address, java.lang.String prevVolume, java.lang.String newVolume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(prevVolume);
_data.writeString(newVolume);
mRemote.transact(Stub.TRANSACTION_onHfpMicVolumeChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Callback to inform active and hold call number while active call number is changed.
     *
     * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param activeNumber : the active call number
     * @param holdNumber : the hold call number
     */
@Override public void onHfpActiveNumberChanged(java.lang.String address, java.lang.String activeNumber, java.lang.String holdNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(activeNumber);
_data.writeString(holdNumber);
mRemote.transact(Stub.TRANSACTION_onHfpActiveNumberChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onHfpStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onHfpScoStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onHfpVoiceControlStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_retHfpRemoteDeviceNetworkOperator = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_retHfpRemoteDeviceSubscriberNumber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onHfpRemoteDeviceServiceStatusChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onHfpRemoteDeviceRoamStatusChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onHfpRemoteDeviceBatteryStatusChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onHfpRemoteDeviceSignalStatusChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_onHfpAtDownloadPhonebookStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_onHfpAtDownloadSMSStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_onHfpIncomingCallNumber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_onHfpOutgoingCallNumber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_onHfpSpeakerVolumeChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_onHfpMicVolumeChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_onHfpActiveNumberChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
}
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
public void onHfpStateChanged(java.lang.String address, int prevState, int newState) throws android.os.RemoteException;
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
public void onHfpScoStateChanged(java.lang.String address, int prevState, int newState) throws android.os.RemoteException;
/**
	 * Callback to inform change in HFP voice control state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param isVoiceControlOn : return voice control status (On/Off).
     */
public void onHfpVoiceControlStateChanged(java.lang.String address, boolean isVoiceControlOn) throws android.os.RemoteException;
/**
	 * Callback to inform the error response from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param errorInfo : HFPErrorVO
    void onHfpErrorResponse(String address, in HfpErrorInfo errorInfo);   
     *//**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDeviceInfo reqHfpRemoteDeviceInfo}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param deviceInfo : StateVO
    void retHfpRemoteDeviceInfo(String address, in HfpStatusInfo deviceInfo);
     *//**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDevicePhoneNumber reqHfpRemoteDevicePhoneNumber}
	 * from remote connected device with given Bluetooth hardware address has received or	 
	 * when our service recognizes there is a change on active phone numbers in multiparty calls situation.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param phoneNumberInfo : PhoneNumberVO
    void onHfpRemoteDevicePhoneNumberChanged(String address, in List<HfpPhoneNumberInfo> phoneNumberInfo); 
     *//**
	 * Callback to inform response to {@link INforeService#reqHfpRemoteDeviceNetworkOperator reqHfpRemoteDeviceNetworkOperator}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
     * @param mode : contain the current mode and provides no information with regard to the name of the operator.
     * @param format : specify the format of the <operator> parameter string, and shall always be 0 for this specification.
     * @param operator : specify a quoted string in alphanumeric format representing the name of the network operator. This string shall not exceed 16 characters. 
     */
public void retHfpRemoteDeviceNetworkOperator(java.lang.String address, java.lang.String mode, int format, java.lang.String operator) throws android.os.RemoteException;
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
public void retHfpRemoteDeviceSubscriberNumber(java.lang.String address, java.lang.String number, int type, int service) throws android.os.RemoteException;
/**
	 * Callback to inform the change on "Service" indicator status from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param isTelecomServiceOn : 
	 * <p><value>=false implies no service. No Home/Roam network available.
     * <p><value>=true implies presence of service. Home/Roam network available.  
     */
public void onHfpRemoteDeviceServiceStatusChanged(java.lang.String address, boolean isTelecomServiceOn) throws android.os.RemoteException;
/**
	 * Callback to inform the change on "Roaming" indicator status from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param isOnRoaming : 
     *<p><value>=false means roaming is not active.
     *<p><value>=true means a roaming is active.  
     */
public void onHfpRemoteDeviceRoamStatusChanged(java.lang.String address, boolean isOnRoaming) throws android.os.RemoteException;
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
public void onHfpRemoteDeviceBatteryStatusChanged(java.lang.String address, int prevStatus, int newStatus, int maxStatus, int minStatus) throws android.os.RemoteException;
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
public void onHfpRemoteDeviceSignalStatusChanged(java.lang.String address, int prevStatus, int newStatus, int maxStatus, int minStatus) throws android.os.RemoteException;
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
public void onHfpAtDownloadPhonebookStateChanged(java.lang.String address, int prevState, int newState) throws android.os.RemoteException;
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
public void onHfpAtDownloadSMSStateChanged(java.lang.String address, int newState) throws android.os.RemoteException;
/**
	 * Callback to inform the new incoming call number from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param callNumber : incoming call number
     */
public void onHfpIncomingCallNumber(java.lang.String address, java.lang.String callNumber) throws android.os.RemoteException;
/**
	 * Callback to inform the new outgoing call number from remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param callNumber : outgoing call number
     */
public void onHfpOutgoingCallNumber(java.lang.String address, java.lang.String callNumber) throws android.os.RemoteException;
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
public void onHfpSpeakerVolumeChanged(java.lang.String address, java.lang.String prevVolume, java.lang.String newVolume) throws android.os.RemoteException;
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
public void onHfpMicVolumeChanged(java.lang.String address, java.lang.String prevVolume, java.lang.String newVolume) throws android.os.RemoteException;
/**
     * Callback to inform active and hold call number while active call number is changed.
     *
     * @param address : Bluetooth MAC address of remote device which involves state changed.
     * @param activeNumber : the active call number
     * @param holdNumber : the hold call number
     */
public void onHfpActiveNumberChanged(java.lang.String address, java.lang.String activeNumber, java.lang.String holdNumber) throws android.os.RemoteException;
}
