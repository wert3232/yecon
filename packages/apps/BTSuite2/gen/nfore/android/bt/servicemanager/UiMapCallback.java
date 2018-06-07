/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\yecon\\yecon_ac8317\\yecon\\packages\\apps\\BTSuite2\\src\\nfore\\android\\bt\\servicemanager\\UiMapCallback.aidl
 */
package nfore.android.bt.servicemanager;
/**
 * The callback interface for Message Access profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 */
public interface UiMapCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements nfore.android.bt.servicemanager.UiMapCallback
{
private static final java.lang.String DESCRIPTOR = "nfore.android.bt.servicemanager.UiMapCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an nfore.android.bt.servicemanager.UiMapCallback interface,
 * generating a proxy if needed.
 */
public static nfore.android.bt.servicemanager.UiMapCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof nfore.android.bt.servicemanager.UiMapCallback))) {
return ((nfore.android.bt.servicemanager.UiMapCallback)iin);
}
return new nfore.android.bt.servicemanager.UiMapCallback.Stub.Proxy(obj);
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
case TRANSACTION_onMapStateChanged:
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
int _arg4;
_arg4 = data.readInt();
int _arg5;
_arg5 = data.readInt();
this.onMapStateChanged(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
return true;
}
case TRANSACTION_onMapNotificationStateChanged:
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
int _arg4;
_arg4 = data.readInt();
this.onMapNotificationStateChanged(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_onMapNewMessageReceived:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
this.onMapNewMessageReceived(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_onMapDownloadNotify:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.onMapDownloadNotify(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_retMapDatabaseAvailable:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.retMapDatabaseAvailable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_retMapDeleteDatabaseByAddressCompleted:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.retMapDeleteDatabaseByAddressCompleted(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_retMapCleanDatabaseCompleted:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.retMapCleanDatabaseCompleted(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements nfore.android.bt.servicemanager.UiMapCallback
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
	 * 		<br>REASON_DOWNLOAD_FAILED									(int) 3
	 *		<br>REASON_DOWNLOAD_TIMEOUT									(int) 4
 	 *		<br>REASON_DOWNLOAD_INTERRUPTED								(int) 6</blockquote>		
	 * @param itemCounts : the number of messages downloaded and updated. 
	 * This parameter is only available and meaningful when reason parameter is REASON_DOWNLOAD_FULL_CONTENT_COMPLETED or REASON_DOWNLOAD_OUTLINE_COMPLETED. 
	 * In other cases, it would be -1.
     */
@Override public void onMapStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState, int reason, int itemCounts) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(deviceName);
_data.writeInt(prevState);
_data.writeInt(newState);
_data.writeInt(reason);
_data.writeInt(itemCounts);
mRemote.transact(Stub.TRANSACTION_onMapStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onMapNotificationStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState, int reason) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(deviceName);
_data.writeInt(prevState);
_data.writeInt(newState);
_data.writeInt(reason);
mRemote.transact(Stub.TRANSACTION_onMapNotificationStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onMapNewMessageReceived(java.lang.String address, java.lang.String handle, java.lang.String sender, java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(handle);
_data.writeString(sender);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_onMapNewMessageReceived, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onMapDownloadNotify(java.lang.String address, int totalItems, int currentItems) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(totalItems);
_data.writeInt(currentItems);
mRemote.transact(Stub.TRANSACTION_onMapDownloadNotify, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform response to {@link INforeService#reqMapDatabaseAvailable reqMapDatabaseAvailable}
	 * for remote connected device with given Bluetooth hardware address.
	 * When this callback is invocated, it means database is available.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which just completed downloading.
     */
@Override public void retMapDatabaseAvailable(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_retMapDatabaseAvailable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     *  Callback to inform response to {@link INforeService#retMapDeleteDatabaseByAddress retMapDeleteDatabaseByAddress}
     *
     *  @param address which address delete from table
     *  @param isSuccessed said success or not
     */
@Override public void retMapDeleteDatabaseByAddressCompleted(java.lang.String address, boolean isSuccessed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(((isSuccessed)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_retMapDeleteDatabaseByAddressCompleted, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*
	 * Callback to inform response to {@link INforeService#reqMapCleanDatabase reqMapCleanDatabase}
	 * when database has been cleaned.
	 *
	 *  @param isSuccessed said success or not
	 */
@Override public void retMapCleanDatabaseCompleted(boolean isSuccessed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isSuccessed)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_retMapCleanDatabaseCompleted, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onMapStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onMapNotificationStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onMapNewMessageReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onMapDownloadNotify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_retMapDatabaseAvailable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_retMapDeleteDatabaseByAddressCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_retMapCleanDatabaseCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
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
	 * 		<br>REASON_DOWNLOAD_FAILED									(int) 3
	 *		<br>REASON_DOWNLOAD_TIMEOUT									(int) 4
 	 *		<br>REASON_DOWNLOAD_INTERRUPTED								(int) 6</blockquote>		
	 * @param itemCounts : the number of messages downloaded and updated. 
	 * This parameter is only available and meaningful when reason parameter is REASON_DOWNLOAD_FULL_CONTENT_COMPLETED or REASON_DOWNLOAD_OUTLINE_COMPLETED. 
	 * In other cases, it would be -1.
     */
public void onMapStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState, int reason, int itemCounts) throws android.os.RemoteException;
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
public void onMapNotificationStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState, int reason) throws android.os.RemoteException;
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
public void onMapNewMessageReceived(java.lang.String address, java.lang.String handle, java.lang.String sender, java.lang.String message) throws android.os.RemoteException;
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
public void onMapDownloadNotify(java.lang.String address, int totalItems, int currentItems) throws android.os.RemoteException;
/**
	 * Callback to inform response to {@link INforeService#reqMapDatabaseAvailable reqMapDatabaseAvailable}
	 * for remote connected device with given Bluetooth hardware address.
	 * When this callback is invocated, it means database is available.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which just completed downloading.
     */
public void retMapDatabaseAvailable(java.lang.String address) throws android.os.RemoteException;
/**
     *  Callback to inform response to {@link INforeService#retMapDeleteDatabaseByAddress retMapDeleteDatabaseByAddress}
     *
     *  @param address which address delete from table
     *  @param isSuccessed said success or not
     */
public void retMapDeleteDatabaseByAddressCompleted(java.lang.String address, boolean isSuccessed) throws android.os.RemoteException;
/*
	 * Callback to inform response to {@link INforeService#reqMapCleanDatabase reqMapCleanDatabase}
	 * when database has been cleaned.
	 *
	 *  @param isSuccessed said success or not
	 */
public void retMapCleanDatabaseCompleted(boolean isSuccessed) throws android.os.RemoteException;
}
