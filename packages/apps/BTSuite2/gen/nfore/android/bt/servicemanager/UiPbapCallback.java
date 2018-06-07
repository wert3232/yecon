/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\yecon\\yecon_ac8317\\yecon\\packages\\apps\\BTSuite2\\src\\nfore\\android\\bt\\servicemanager\\UiPbapCallback.aidl
 */
package nfore.android.bt.servicemanager;
/**
 * The callback interface for Phonebook Access profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 *
 * @author Space
 * @version 1.1
 */
public interface UiPbapCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements nfore.android.bt.servicemanager.UiPbapCallback
{
private static final java.lang.String DESCRIPTOR = "nfore.android.bt.servicemanager.UiPbapCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an nfore.android.bt.servicemanager.UiPbapCallback interface,
 * generating a proxy if needed.
 */
public static nfore.android.bt.servicemanager.UiPbapCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof nfore.android.bt.servicemanager.UiPbapCallback))) {
return ((nfore.android.bt.servicemanager.UiPbapCallback)iin);
}
return new nfore.android.bt.servicemanager.UiPbapCallback.Stub.Proxy(obj);
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
case TRANSACTION_onPbapStateChanged:
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
this.onPbapStateChanged(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
return true;
}
case TRANSACTION_onPbapDownloadNotify:
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
this.onPbapDownloadNotify(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_retPbapDatabaseAvailable:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.retPbapDatabaseAvailable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_retPbapQueryName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _arg3;
_arg3 = (0!=data.readInt());
this.retPbapQueryName(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_retPbapDeleteDatabaseByAddressCompleted:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.retPbapDeleteDatabaseByAddressCompleted(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_retPbapCleanDatabaseCompleted:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.retPbapCleanDatabaseCompleted(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements nfore.android.bt.servicemanager.UiPbapCallback
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
	 * 		<br>REASON_DOWNLOAD_FAILED									(int) 3
	 *		<br>REASON_DOWNLOAD_TIMEOUT									(int) 4</blockquote>	
	 * In other cases, it would be -1.	 
	 * @param itemCounts : the number of vcards downloaded and updated. 
	 * This parameter is only available and meaningful when state changed from STATE_CONNECTED to STATE_DB_UPDATE and STATE_DB_UPDATE to STATE_READY. 
	 * In other cases, it would be -1.
     */
@Override public void onPbapStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState, int reason, int itemCounts) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onPbapStateChanged, _data, _reply, 0);
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
@Override public void onPbapDownloadNotify(java.lang.String address, int storage, int totalItems, int currentItems) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(storage);
_data.writeInt(totalItems);
_data.writeInt(currentItems);
mRemote.transact(Stub.TRANSACTION_onPbapDownloadNotify, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform response to {@link INforeService#reqPbapDatabaseAvailable reqPbapDatabaseAvailable}
	 * for remote connected device with given Bluetooth hardware address.
	 * When this callback is invocated, it means database is available.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which just completed downloading.
     */
@Override public void retPbapDatabaseAvailable(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_retPbapDatabaseAvailable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void retPbapQueryName(java.lang.String address, java.lang.String phoneNumber, java.lang.String fullName, boolean isSuccessed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(phoneNumber);
_data.writeString(fullName);
_data.writeInt(((isSuccessed)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_retPbapQueryName, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*
	 * Callback to inform response to {@link INforeService#retPbapDeleteDatabaseByAddress retPbapDeleteDatabaseByAddress}
	 * when database has been deleted.
	 */
@Override public void retPbapDeleteDatabaseByAddressCompleted(java.lang.String deletedAddress, boolean isSuccessed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(deletedAddress);
_data.writeInt(((isSuccessed)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_retPbapDeleteDatabaseByAddressCompleted, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*
	 * Callback to inform response to {@link INforeService#reqPbapCleanDatabase reqPbapCleanDatabase}
	 * when database has been cleaned.
	 * @param isSuccessed
	 */
@Override public void retPbapCleanDatabaseCompleted(boolean isSuccessed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isSuccessed)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_retPbapCleanDatabaseCompleted, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onPbapStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onPbapDownloadNotify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_retPbapDatabaseAvailable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_retPbapQueryName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_retPbapDeleteDatabaseByAddressCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_retPbapCleanDatabaseCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
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
	 * 		<br>REASON_DOWNLOAD_FAILED									(int) 3
	 *		<br>REASON_DOWNLOAD_TIMEOUT									(int) 4</blockquote>	
	 * In other cases, it would be -1.	 
	 * @param itemCounts : the number of vcards downloaded and updated. 
	 * This parameter is only available and meaningful when state changed from STATE_CONNECTED to STATE_DB_UPDATE and STATE_DB_UPDATE to STATE_READY. 
	 * In other cases, it would be -1.
     */
public void onPbapStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState, int reason, int itemCounts) throws android.os.RemoteException;
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
public void onPbapDownloadNotify(java.lang.String address, int storage, int totalItems, int currentItems) throws android.os.RemoteException;
/**
	 * Callback to inform response to {@link INforeService#reqPbapDatabaseAvailable reqPbapDatabaseAvailable}
	 * for remote connected device with given Bluetooth hardware address.
	 * When this callback is invocated, it means database is available.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which just completed downloading.
     */
public void retPbapDatabaseAvailable(java.lang.String address) throws android.os.RemoteException;
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
public void retPbapQueryName(java.lang.String address, java.lang.String phoneNumber, java.lang.String fullName, boolean isSuccessed) throws android.os.RemoteException;
/*
	 * Callback to inform response to {@link INforeService#retPbapDeleteDatabaseByAddress retPbapDeleteDatabaseByAddress}
	 * when database has been deleted.
	 */
public void retPbapDeleteDatabaseByAddressCompleted(java.lang.String deletedAddress, boolean isSuccessed) throws android.os.RemoteException;
/*
	 * Callback to inform response to {@link INforeService#reqPbapCleanDatabase reqPbapCleanDatabase}
	 * when database has been cleaned.
	 * @param isSuccessed
	 */
public void retPbapCleanDatabaseCompleted(boolean isSuccessed) throws android.os.RemoteException;
}
