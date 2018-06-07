/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\yecon\\yecon_ac8317\\yecon\\packages\\apps\\BTSuite2\\src\\nfore\\android\\bt\\servicemanager\\UiHspCallback.aidl
 */
package nfore.android.bt.servicemanager;
/** 
 * The callback interface for Headset profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 *
 * @author Fred
 * @version 1.0
 */
public interface UiHspCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements nfore.android.bt.servicemanager.UiHspCallback
{
private static final java.lang.String DESCRIPTOR = "nfore.android.bt.servicemanager.UiHspCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an nfore.android.bt.servicemanager.UiHspCallback interface,
 * generating a proxy if needed.
 */
public static nfore.android.bt.servicemanager.UiHspCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof nfore.android.bt.servicemanager.UiHspCallback))) {
return ((nfore.android.bt.servicemanager.UiHspCallback)iin);
}
return new nfore.android.bt.servicemanager.UiHspCallback.Stub.Proxy(obj);
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
case TRANSACTION_onHspStateChanged:
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
this.onHspStateChanged(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_onHspScoStateChanged:
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
this.onHspScoStateChanged(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_onHspErrorResponse:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.onHspErrorResponse(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onHspNewIncomingCall:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onHspNewIncomingCall(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onHspSpeakerVolumeChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.onHspSpeakerVolumeChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_onHspMicVolumeChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.onHspMicVolumeChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements nfore.android.bt.servicemanager.UiHspCallback
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
@Override public void onHspStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(deviceName);
_data.writeInt(prevState);
_data.writeInt(newState);
mRemote.transact(Stub.TRANSACTION_onHspStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
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
@Override public void onHspScoStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(deviceName);
_data.writeInt(prevState);
_data.writeInt(newState);
mRemote.transact(Stub.TRANSACTION_onHspScoStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform the error response with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device.
	 * @param errorCode : the possible reason of error.
     */
@Override public void onHspErrorResponse(java.lang.String address, int errorCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(errorCode);
mRemote.transact(Stub.TRANSACTION_onHspErrorResponse, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Callback to inform a new incoming call of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB". 
	 * This callback is used mainly to help state changed management.
     * 
	 * @param address : Bluetooth MAC address of remote device.
     */
@Override public void onHspNewIncomingCall(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_onHspNewIncomingCall, _data, _reply, 0);
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
@Override public void onHspSpeakerVolumeChanged(java.lang.String address, java.lang.String prevVolume, java.lang.String newVolume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(prevVolume);
_data.writeString(newVolume);
mRemote.transact(Stub.TRANSACTION_onHspSpeakerVolumeChanged, _data, _reply, 0);
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
@Override public void onHspMicVolumeChanged(java.lang.String address, java.lang.String prevVolume, java.lang.String newVolume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(prevVolume);
_data.writeString(newVolume);
mRemote.transact(Stub.TRANSACTION_onHspMicVolumeChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onHspStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onHspScoStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onHspErrorResponse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onHspNewIncomingCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onHspSpeakerVolumeChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onHspMicVolumeChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
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
public void onHspStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState) throws android.os.RemoteException;
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
public void onHspScoStateChanged(java.lang.String address, java.lang.String deviceName, int prevState, int newState) throws android.os.RemoteException;
/**
	 * Callback to inform the error response with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device.
	 * @param errorCode : the possible reason of error.
     */
public void onHspErrorResponse(java.lang.String address, int errorCode) throws android.os.RemoteException;
/**
	 * Callback to inform a new incoming call of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB". 
	 * This callback is used mainly to help state changed management.
     * 
	 * @param address : Bluetooth MAC address of remote device.
     */
public void onHspNewIncomingCall(java.lang.String address) throws android.os.RemoteException;
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
public void onHspSpeakerVolumeChanged(java.lang.String address, java.lang.String prevVolume, java.lang.String newVolume) throws android.os.RemoteException;
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
public void onHspMicVolumeChanged(java.lang.String address, java.lang.String prevVolume, java.lang.String newVolume) throws android.os.RemoteException;
}
