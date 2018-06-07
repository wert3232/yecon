/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\yecon\\yecon_ac8317\\yecon\\packages\\apps\\BTSuite2\\src\\nfore\\android\\bt\\servicemanager\\IServiceCallback.aidl
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
public interface IServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements nfore.android.bt.servicemanager.IServiceCallback
{
private static final java.lang.String DESCRIPTOR = "nfore.android.bt.servicemanager.IServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an nfore.android.bt.servicemanager.IServiceCallback interface,
 * generating a proxy if needed.
 */
public static nfore.android.bt.servicemanager.IServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof nfore.android.bt.servicemanager.IServiceCallback))) {
return ((nfore.android.bt.servicemanager.IServiceCallback)iin);
}
return new nfore.android.bt.servicemanager.IServiceCallback.Stub.Proxy(obj);
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
case TRANSACTION_onServiceReady:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onServiceReady(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onAdapterStateChanged:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onAdapterStateChanged(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_retBluetoothPairedDevices:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String[] _arg1;
_arg1 = data.createStringArray();
java.lang.String[] _arg2;
_arg2 = data.createStringArray();
int[] _arg3;
_arg3 = data.createIntArray();
byte[] _arg4;
_arg4 = data.createByteArray();
this.retBluetoothPairedDevices(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_onDeviceFound:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
byte _arg2;
_arg2 = data.readByte();
boolean _arg3;
_arg3 = (0!=data.readInt());
this.onDeviceFound(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_onDevicePaired:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
byte _arg3;
_arg3 = data.readByte();
this.onDevicePaired(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_onDeviceUnpaired:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onDeviceUnpaired(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onDevicePairFailed:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onDevicePairFailed(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onDeviceUuidsGot:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
this.onDeviceUuidsGot(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements nfore.android.bt.servicemanager.IServiceCallback
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
@Override public void onServiceReady(int profilesEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(profilesEnable);
mRemote.transact(Stub.TRANSACTION_onServiceReady, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onAdapterStateChanged(int newState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(newState);
mRemote.transact(Stub.TRANSACTION_onAdapterStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void retBluetoothPairedDevices(int elements, java.lang.String[] address, java.lang.String[] name, int[] supportProfile, byte[] category) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(elements);
_data.writeStringArray(address);
_data.writeStringArray(name);
_data.writeIntArray(supportProfile);
_data.writeByteArray(category);
mRemote.transact(Stub.TRANSACTION_retBluetoothPairedDevices, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onDeviceFound(java.lang.String address, java.lang.String name, byte category, boolean isRealName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(name);
_data.writeByte(category);
_data.writeInt(((isRealName)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_onDeviceFound, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onDevicePaired(java.lang.String address, java.lang.String name, int supportProfile, byte category) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(name);
_data.writeInt(supportProfile);
_data.writeByte(category);
mRemote.transact(Stub.TRANSACTION_onDevicePaired, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 
	 * Callback to inform a device is unpaired. This callback might be the response to 
	 * {@link INforeService#reqBluetoothUnpair reqBluetoothUnpair}.
	 *
	 * @param address : the address of paired device in String type.
	 * @param name : the name of paired device in String type.
	 */
@Override public void onDeviceUnpaired(java.lang.String address, java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_onDeviceUnpaired, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 
	 * Callback to inform a device is pair failed. This callback might be the response to 
	 * {@link INforeService#reqBluetoothPair reqBluetoothPair}.
	 *
	 * @param address : the address of paired device in String type.
	 * @param name : the name of paired device in String type.
	 */
@Override public void onDevicePairFailed(java.lang.String address, java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_onDevicePairFailed, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onDeviceUuidsGot(java.lang.String address, java.lang.String name, int supportProfile) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(name);
_data.writeInt(supportProfile);
mRemote.transact(Stub.TRANSACTION_onDeviceUuidsGot, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onServiceReady = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onAdapterStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_retBluetoothPairedDevices = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onDeviceFound = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onDevicePaired = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onDeviceUnpaired = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onDevicePairFailed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onDeviceUuidsGot = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
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
public void onServiceReady(int profilesEnable) throws android.os.RemoteException;
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
public void onAdapterStateChanged(int newState) throws android.os.RemoteException;
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
public void retBluetoothPairedDevices(int elements, java.lang.String[] address, java.lang.String[] name, int[] supportProfile, byte[] category) throws android.os.RemoteException;
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
public void onDeviceFound(java.lang.String address, java.lang.String name, byte category, boolean isRealName) throws android.os.RemoteException;
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
public void onDevicePaired(java.lang.String address, java.lang.String name, int supportProfile, byte category) throws android.os.RemoteException;
/** 
	 * Callback to inform a device is unpaired. This callback might be the response to 
	 * {@link INforeService#reqBluetoothUnpair reqBluetoothUnpair}.
	 *
	 * @param address : the address of paired device in String type.
	 * @param name : the name of paired device in String type.
	 */
public void onDeviceUnpaired(java.lang.String address, java.lang.String name) throws android.os.RemoteException;
/** 
	 * Callback to inform a device is pair failed. This callback might be the response to 
	 * {@link INforeService#reqBluetoothPair reqBluetoothPair}.
	 *
	 * @param address : the address of paired device in String type.
	 * @param name : the name of paired device in String type.
	 */
public void onDevicePairFailed(java.lang.String address, java.lang.String name) throws android.os.RemoteException;
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
public void onDeviceUuidsGot(java.lang.String address, java.lang.String name, int supportProfile) throws android.os.RemoteException;
}
