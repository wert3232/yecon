/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\yecon\\yecon_ac8317\\yecon\\packages\\apps\\BTSuite2\\src\\nfore\\android\\bt\\servicemanager\\IHidCallback.aidl
 */
package nfore.android.bt.servicemanager;
/**
 * The callback interface for Hid profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 */
public interface IHidCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements nfore.android.bt.servicemanager.IHidCallback
{
private static final java.lang.String DESCRIPTOR = "nfore.android.bt.servicemanager.IHidCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an nfore.android.bt.servicemanager.IHidCallback interface,
 * generating a proxy if needed.
 */
public static nfore.android.bt.servicemanager.IHidCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof nfore.android.bt.servicemanager.IHidCallback))) {
return ((nfore.android.bt.servicemanager.IHidCallback)iin);
}
return new nfore.android.bt.servicemanager.IHidCallback.Stub.Proxy(obj);
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
case TRANSACTION_onHidStateChanged:
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
this.onHidStateChanged(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements nfore.android.bt.servicemanager.IHidCallback
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
/* ======================================================================================================================================================== 
	 * Callback function of state changed event to Hid
	 *//** 
	 * Callback to inform change in Hid state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED	(int) 100
	 * 		<br>STATE_READY				(int) 110
	 * 		<br>STATE_CONNECTING		(int) 120
	 *		<br>STATE_CONNECTED			(int) 140
	 * Parameter address is only valid in state STATE_CONNECTING, STATE_CONNECTED.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState : the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.
	 * @param newState : the new state.
	 * @param reason : The reason of state changed. -1 means that is in the right steps. Possible values are
	 * 		<p><blockquote>ERROR_LOCAL_ADDRESS_NULL		        (int) 706
	 * 		<br>ERROR_REMOTE_ADDRESS_NULL						(int) 707	 
	 * 		<br>ERROR_HID_CONNECT_FAIL							(int) 708
	 *		<br>ERROR_HID_ACCEPT_FAIL							(int) 709
 	 *		<br>ERROR_HID_DISCONNECT_FAIL						(int) 710
 	 *		<br>HID_DISCONNECT_BY_REMOTE						(int) 904</blockquote>	
 	 	
	 */
@Override public void onHidStateChanged(java.lang.String address, int prevState, int newState, int reason) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(prevState);
_data.writeInt(newState);
_data.writeInt(reason);
mRemote.transact(Stub.TRANSACTION_onHidStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onHidStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/* ======================================================================================================================================================== 
	 * Callback function of state changed event to Hid
	 *//** 
	 * Callback to inform change in Hid state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED	(int) 100
	 * 		<br>STATE_READY				(int) 110
	 * 		<br>STATE_CONNECTING		(int) 120
	 *		<br>STATE_CONNECTED			(int) 140
	 * Parameter address is only valid in state STATE_CONNECTING, STATE_CONNECTED.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState : the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.
	 * @param newState : the new state.
	 * @param reason : The reason of state changed. -1 means that is in the right steps. Possible values are
	 * 		<p><blockquote>ERROR_LOCAL_ADDRESS_NULL		        (int) 706
	 * 		<br>ERROR_REMOTE_ADDRESS_NULL						(int) 707	 
	 * 		<br>ERROR_HID_CONNECT_FAIL							(int) 708
	 *		<br>ERROR_HID_ACCEPT_FAIL							(int) 709
 	 *		<br>ERROR_HID_DISCONNECT_FAIL						(int) 710
 	 *		<br>HID_DISCONNECT_BY_REMOTE						(int) 904</blockquote>	
 	 	
	 */
public void onHidStateChanged(java.lang.String address, int prevState, int newState, int reason) throws android.os.RemoteException;
}
