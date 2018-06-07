/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\yecon\\yecon_ac8317\\yecon\\packages\\apps\\BTSuite2\\src\\nfore\\android\\bt\\servicemanager\\INfServiceCommand.aidl
 */
package nfore.android.bt.servicemanager;
/**
 * The API interface for HSP, HFP, SPP, PBAP, MAP, A2DP, AVRCP and PAN profiles.
 * UI program may use this for requesting the specific API to nFore service.
 * <br>The naming principle of API in this doc is as follows,
 *		<p><blockquote>setXXX() : request to set attributes with or without returned result.
 *		<br>isXXX() : the requested result with boolean type would be returned immediately, Syn mode.
 *		<br>getXXX() : the requested result would be returned immediately, Syn mode.
 *		<br>reqXXX() : request nFore service to do something or the requested result might NOT be returned immediately, App needs to wait for another callback, Asyn mode.</blockquote>
 *
 * <p> The constant variables in this doc could be found and referred by importing
 * 		<br><blockquote>nfore.android.bt.res.NfDef</blockquote>
 * <p> with prefix NfDef class name. Ex : NfDef.DEFAULT_ADDRESS
 */
public interface INfServiceCommand extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements nfore.android.bt.servicemanager.INfServiceCommand
{
private static final java.lang.String DESCRIPTOR = "nfore.android.bt.servicemanager.INfServiceCommand";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an nfore.android.bt.servicemanager.INfServiceCommand interface,
 * generating a proxy if needed.
 */
public static nfore.android.bt.servicemanager.INfServiceCommand asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof nfore.android.bt.servicemanager.INfServiceCommand))) {
return ((nfore.android.bt.servicemanager.INfServiceCommand)iin);
}
return new nfore.android.bt.servicemanager.INfServiceCommand.Stub.Proxy(obj);
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
case TRANSACTION_registerNforeServiceCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IServiceCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IServiceCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerNforeServiceCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterNforeServiceCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IServiceCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IServiceCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterNforeServiceCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setBluetoothEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setBluetoothEnable(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setBluetoothDiscoverableDuration:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setBluetoothDiscoverableDuration(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setBluetoothDiscoverablePermanent:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setBluetoothDiscoverablePermanent(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqBluetoothScanning:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.reqBluetoothScanning(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqBluetoothPair:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqBluetoothPair(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqBluetoothUnpair:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqBluetoothUnpair(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqBluetoothUnpairAll:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.reqBluetoothUnpairAll();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqBluetoothUnpairAllNextTime:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.reqBluetoothUnpairAllNextTime();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqBluetoothPairedDevices:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.reqBluetoothPairedDevices();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getBluetoothLocalAdapterName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getBluetoothLocalAdapterName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getBluetoothRemoteDeviceName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.getBluetoothRemoteDeviceName(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getBluetoothLocalAdapterAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getBluetoothLocalAdapterAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setBluetoothLocalAdapterName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.setBluetoothLocalAdapterName(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isBluetoothEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isBluetoothEnable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getBluetoothCurrentState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBluetoothCurrentState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isBluetoothScanning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isBluetoothScanning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isBluetoothDiscoverable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isBluetoothDiscoverable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setBluetoothAutoReconnect:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setBluetoothAutoReconnect(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setBluetoothAutoReconnectForceOor:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setBluetoothAutoReconnectForceOor(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isBluetoothAutoReconnectEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isBluetoothAutoReconnectEnable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqBluetoothConnectAll:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.reqBluetoothConnectAll(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_reqBluetoothDisconnectAll:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.reqBluetoothDisconnectAll();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_reqBluetoothRemoteUuids:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.reqBluetoothRemoteUuids(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_registerHfpCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IHfpCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IHfpCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerHfpCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterHfpCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IHfpCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IHfpCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterHfpCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpConnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpConnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpDisconnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpDisconnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getHfpConnectedDeviceAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getHfpConnectedDeviceAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getHfpCurrentState:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getHfpCurrentState(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getHfpCurrentScoState:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getHfpCurrentScoState(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isHfpVoiceControlOn:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isHfpVoiceControlOn(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpDialCall:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.reqHfpDialCall(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpReDial:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpReDial(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpMemoryDial:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.reqHfpMemoryDial(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpAnswerCall:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpAnswerCall(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpRejectIncomingCall:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpRejectIncomingCall(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpTerminateOngoingCall:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpTerminateOngoingCall(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpMultiCallControl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqHfpMultiCallControl(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpSendDtmf:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.reqHfpSendDtmf(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpAudioTransfer:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqHfpAudioTransfer(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setHfpLocalRingEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setHfpLocalRingEnable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_reqHfpRemoteDeviceInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.reqHfpRemoteDeviceInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getHfpRemoteDeviceName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.getHfpRemoteDeviceName(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_reqHfpRemoteDevicePhoneNumber:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpRemoteDevicePhoneNumber(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpRemoteDeviceNetworkOperator:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpRemoteDeviceNetworkOperator(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpRemoteDeviceSubscriberNumber:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpRemoteDeviceSubscriberNumber(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isHfpRemoteDeviceTelecomServiceOn:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isHfpRemoteDeviceTelecomServiceOn(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isHfpRemoteDeviceOnRoaming:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isHfpRemoteDeviceOnRoaming(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getHfpRemoteDeviceBatteryStatus:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getHfpRemoteDeviceBatteryStatus(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getHfpRemoteDeviceSignalStatus:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getHfpRemoteDeviceSignalStatus(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setHfpAtNewSmsNotify:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setHfpAtNewSmsNotify(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_reqHfpAtDownloadPhonebook:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.reqHfpAtDownloadPhonebook(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpAtDownloadSMS:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHfpAtDownloadSMS(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpRemoteDeviceVolumeSync:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
int _arg2;
_arg2 = data.readInt();
boolean _result = this.reqHfpRemoteDeviceVolumeSync(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHfpVoiceDial:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _result = this.reqHfpVoiceDial(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_registerHspCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IHspCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IHspCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerHspCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterHspCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IHspCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IHspCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterHspCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHspConnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHspConnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHspDisconnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHspDisconnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getHspConnectedDeviceAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getHspConnectedDeviceAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getHspCurrentState:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getHspCurrentState(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getHspCurrentScoState:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getHspCurrentScoState(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_reqHspReDial:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHspReDial(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHspAnswerCall:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHspAnswerCall(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHspRejectIncomingCall:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHspRejectIncomingCall(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHspTerminateOngoingCall:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHspTerminateOngoingCall(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHspAudioTransfer:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqHspAudioTransfer(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHspRemoteDeviceVolumeSync:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
int _arg2;
_arg2 = data.readInt();
boolean _result = this.reqHspRemoteDeviceVolumeSync(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_registerSppCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.ISppCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.ISppCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerSppCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterSppCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.ISppCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.ISppCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterSppCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqSppConnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqSppConnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqSppDisconnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqSppDisconnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqSppConnectedDeviceAddressList:
{
data.enforceInterface(DESCRIPTOR);
this.reqSppConnectedDeviceAddressList();
reply.writeNoException();
return true;
}
case TRANSACTION_isSppConnected:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isSppConnected(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqSppSendData:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte[] _arg1;
_arg1 = data.createByteArray();
this.reqSppSendData(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_registerPbapCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IPbapCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IPbapCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerPbapCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterPbapCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IPbapCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IPbapCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterPbapCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqPbapDownloadAllVcards:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
boolean _arg3;
_arg3 = (0!=data.readInt());
boolean _result = this.reqPbapDownloadAllVcards(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqPbapDatabaseAvailable:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.reqPbapDatabaseAvailable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_reqPbapQueryName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.reqPbapQueryName(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_reqPbapDeleteDatabaseByAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.reqPbapDeleteDatabaseByAddress(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_reqPbapCleanDatabase:
{
data.enforceInterface(DESCRIPTOR);
this.reqPbapCleanDatabase();
reply.writeNoException();
return true;
}
case TRANSACTION_reqPbapDownloadInterrupt:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqPbapDownloadInterrupt(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getPbapCurrentState:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getPbapCurrentState(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_registerMapCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IMapCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IMapCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerMapCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterMapCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IMapCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IMapCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterMapCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqMapDownloadAllMessages:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
boolean _arg3;
_arg3 = (0!=data.readInt());
boolean _result = this.reqMapDownloadAllMessages(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqMapDownloadSingleMessage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.reqMapDownloadSingleMessage(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqMapRegisterNotification:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _result = this.reqMapRegisterNotification(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqMapUnregisterNotification:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.reqMapUnregisterNotification(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isMapNotificationRegistered:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isMapNotificationRegistered(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqMapDownloadInterrupt:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqMapDownloadInterrupt(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqMapDatabaseAvailable:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.reqMapDatabaseAvailable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_reqMapDeleteDatabaseByAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.reqMapDeleteDatabaseByAddress(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_reqMapCleanDatabase:
{
data.enforceInterface(DESCRIPTOR);
this.reqMapCleanDatabase();
reply.writeNoException();
return true;
}
case TRANSACTION_getMapCurrentState:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getMapCurrentState(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getMapRegisterState:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getMapRegisterState(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_registerA2dpAvrcpCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IA2dpCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IA2dpCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerA2dpAvrcpCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterA2dpAvrcpCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IA2dpCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IA2dpCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterA2dpAvrcpCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqA2dpConnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqA2dpConnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqA2dpDisconnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqA2dpDisconnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getA2dpConnectedDeviceAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getA2dpConnectedDeviceAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getA2dpCurrentState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getA2dpCurrentState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getAvrcpCurrentState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAvrcpCurrentState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setA2dpLocalVolume:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setA2dpLocalVolume(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setA2dpLocalMute:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setA2dpLocalMute(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getAvrcpConnectedDeviceAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getAvrcpConnectedDeviceAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_isAvrcp13Supported:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isAvrcp13Supported(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isAvrcp14Supported:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isAvrcp14Supported(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpPlay:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpPlay(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpStop:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpStop(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpPause:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpPause(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpForward:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpForward(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpBackward:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpBackward(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpVolumeUp:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpVolumeUp(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpVolumeDown:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpVolumeDown(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpStartFastForward:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpStartFastForward(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpStopFastForward:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpStopFastForward(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpStartRewind:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpStartRewind(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpStopRewind:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpStopRewind(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetCapabilitiesSupportEvent:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpGetCapabilitiesSupportEvent(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetPlayerSettingAttributesList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpGetPlayerSettingAttributesList(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetPlayerSettingValuesList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqAvrcpGetPlayerSettingValuesList(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetPlayerSettingCurrentValues:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqAvrcpGetPlayerSettingCurrentValues(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpSetPlayerSettingValue:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
byte _arg2;
_arg2 = data.readByte();
boolean _result = this.reqAvrcpSetPlayerSettingValue(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetElementAttributesPlaying:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpGetElementAttributesPlaying(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetPlayStatus:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpGetPlayStatus(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpRegisterEventWatcher:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
long _arg2;
_arg2 = data.readLong();
boolean _result = this.reqAvrcpRegisterEventWatcher(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpUnregisterEventWatcher:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqAvrcpUnregisterEventWatcher(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpAbortMetadataReceiving:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpAbortMetadataReceiving(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpNextGroup:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpNextGroup(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpPreviousGroup:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqAvrcpPreviousGroup(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpSetMetadataCompanyId:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.reqAvrcpSetMetadataCompanyId(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetPlayerSettingAttributeText:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqAvrcpGetPlayerSettingAttributeText(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetPlayerSettingValueText:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
byte _arg2;
_arg2 = data.readByte();
boolean _result = this.reqAvrcpGetPlayerSettingValueText(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpInformDisplayableCharset:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int[] _arg1;
_arg1 = data.createIntArray();
boolean _result = this.reqAvrcpInformDisplayableCharset(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpInformBatteryStatusOfCt:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqAvrcpInformBatteryStatusOfCt(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpSetAddressedPlayer:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.reqAvrcpSetAddressedPlayer(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpSetBrowsedPlayer:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.reqAvrcpSetBrowsedPlayer(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetFolderItems:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqAvrcpGetFolderItems(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpChangePath:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
long _arg2;
_arg2 = data.readLong();
byte _arg3;
_arg3 = data.readByte();
boolean _result = this.reqAvrcpChangePath(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpGetItemAttributes:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
int _arg2;
_arg2 = data.readInt();
long _arg3;
_arg3 = data.readLong();
boolean _result = this.reqAvrcpGetItemAttributes(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpPlaySelectedItem:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
int _arg2;
_arg2 = data.readInt();
long _arg3;
_arg3 = data.readLong();
boolean _result = this.reqAvrcpPlaySelectedItem(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpSearch:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.reqAvrcpSearch(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpAddToNowPlaying:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
int _arg2;
_arg2 = data.readInt();
long _arg3;
_arg3 = data.readLong();
boolean _result = this.reqAvrcpAddToNowPlaying(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isAvrcpBrowsingChannelEstablished:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isAvrcpBrowsingChannelEstablished(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqAvrcpSetAbsoluteVolume:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte _arg1;
_arg1 = data.readByte();
boolean _result = this.reqAvrcpSetAbsoluteVolume(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setA2dpNativeStart:
{
data.enforceInterface(DESCRIPTOR);
this.setA2dpNativeStart();
reply.writeNoException();
return true;
}
case TRANSACTION_setA2dpNativeStop:
{
data.enforceInterface(DESCRIPTOR);
this.setA2dpNativeStop();
reply.writeNoException();
return true;
}
case TRANSACTION_isA2dpNativeRunning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isA2dpNativeRunning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_registerPanCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IPanCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IPanCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerPanCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterPanCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IPanCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IPanCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterPanCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqPanConnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqPanConnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqPanDisconnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqPanDisconnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getPanConnectedDeviceAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getPanConnectedDeviceAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getPanCurrentState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPanCurrentState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_registerHidCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IHidCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IHidCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerHidCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterHidCallback:
{
data.enforceInterface(DESCRIPTOR);
nfore.android.bt.servicemanager.IHidCallback _arg0;
_arg0 = nfore.android.bt.servicemanager.IHidCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterHidCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHidConnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHidConnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_reqHidDisconnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.reqHidDisconnect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_sendHidMouseCommand:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _result = this.sendHidMouseCommand(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_sendHidVirtualKeyCommand:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.sendHidVirtualKeyCommand(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getHidCurrentState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getHidCurrentState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements nfore.android.bt.servicemanager.INfServiceCommand
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
	 * Register callback functions for basic service functions.
	 * Call this function to register callback functions for nFore service.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean registerNforeServiceCallback(nfore.android.bt.servicemanager.IServiceCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerNforeServiceCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** Remove callback functions for nFore service.
     * Call this function to remove previously registered callback interface for nFore service.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean unregisterNforeServiceCallback(nfore.android.bt.servicemanager.IServiceCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterNforeServiceCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to enable or disable local Bluetooth adapter.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param isEnable : true to enable Bluetooth adapter or false to disable.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean setBluetoothEnable(boolean isEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setBluetoothEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to make local Bluetooth adapter discoverable for specific duration in seconds.
	 * If the duration is 0, the system default discoverable duration would be adopted.
	 * The system default duration for discoverable might differentiate from each other in different platforms.
	 * However, it is 120 seconds in general.
	 * If the duration is negative value, we would disable discoverable.	 
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param duration : the duration time in seconds to apply the discoverable mode. This value must be a positive value.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean setBluetoothDiscoverableDuration(int duration) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(duration);
mRemote.transact(Stub.TRANSACTION_setBluetoothDiscoverableDuration, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to make local Bluetooth adapter discoverable permanently or not.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param isEnable : true to enable or false to disable.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean setBluetoothDiscoverablePermanent(boolean isEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setBluetoothDiscoverablePermanent, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to start or stop scanning process for remote devices.
	 * Client should not request to start scanning twice or more in 12 seconds.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged} and {@link IServiceCallback#onDeviceFound onDeviceFound}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param isEnable : true to start or false to stop.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqBluetoothScanning(boolean isEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_reqBluetoothScanning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to pair with given Bluetooth hardware address.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IServiceCallback#onDevicePaired onDevicePaired} and {@link IServiceCallback#onDeviceUuidsGot onDeviceUuidsGot}
	 * to be notified if pairing is successful.	 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @attention : We only support 7 paired devices maximal. System would tear down the first paired device automatically when
	 * the limit is reached. 
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqBluetoothPair(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqBluetoothPair, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to unpair with given Bluetooth hardware address.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IServiceCallback#onDeviceUnpaired onDeviceUnpaired}
	 * to be notified if unpairing is successful.	 	 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * However, this operation only removes the pairing record locally. Remote device would not be notified or
	 * to become aware of this util it tries to connect to us or we initiate the pairing again next time.
	 * All connected or connecting profiles should be terminated before unpairing.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqBluetoothUnpair(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqBluetoothUnpair, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to remove all paired devices.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IServiceCallback#onDeviceUnpaired onDeviceUnpaired}
	 * to be notified if each unpairing is successful.	 	 
	 * However, this operation only removes the pairing record locally. Remote device would not be notified or
	 * to become aware of this util it tries to connect to us or we initiate the pairing again next time.
	 * All connected or connecting profiles should be terminated before unpairing.
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqBluetoothUnpairAll() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reqBluetoothUnpairAll, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to remove all paired devices on next time Bluetooth turning on.	 	 
	 * However, this operation only removes the pairing record locally. Remote device would not be notified or
	 * to become aware of this util it tries to connect to us or we initiate the pairing again next time.
	 * The operation of unpairing devices would be executed on next time Bluetooth is turned on and if this operation is done successfully,
	 * Bluetooth would be automatically turned off.
	 * The possible usage of this method might be in the scenario when system needs to be reset to default. 
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqBluetoothUnpairAllNextTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reqBluetoothUnpairAllNextTime, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to get the paired device list.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#retBluetoothPairedDevices retBluetoothPairedDevices}
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqBluetoothPairedDevices() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reqBluetoothPairedDevices, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to get the name of local Bluetooth adapter.
	 * If there is an error, the string "UNKNOWN" would be returned.
	 *
	 * @return the String type name of local Bluetooth adapter.
	 */
@Override public java.lang.String getBluetoothLocalAdapterName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBluetoothLocalAdapterName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to get the name of remote Bluetooth device with given address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This method would return the name announced by remote device in String type only if this remote device
	 * has been scanned before or otherwise the empty string would be returned.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.	 
	 * @return the real String type name of remote device or the empty string.
	 */
@Override public java.lang.String getBluetoothRemoteDeviceName(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getBluetoothRemoteDeviceName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to get the address of local Bluetooth adapter.
	 *
	 * @return the String type address of local Bluetooth adapter.
	 */
@Override public java.lang.String getBluetoothLocalAdapterAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBluetoothLocalAdapterAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to change the name of local Bluetooth adapter.
	 *
	 * @param name : the name to set.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean setBluetoothLocalAdapterName(java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_setBluetoothLocalAdapterName, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Return true if Bluetooth is currently enabled and ready for use.
	 *
	 * @return true if the local adapter is turned on.
	 */
@Override public boolean isBluetoothEnable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isBluetoothEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getBluetoothCurrentState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBluetoothCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Return true if Bluetooth is currently scanning remote devices.
	 *
	 * @return true if scanning.
	 */
@Override public boolean isBluetoothScanning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isBluetoothScanning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Return true if Bluetooth is currently discoverable from remote devices.
	 *
	 * @return true if discoverable.
	 */
@Override public boolean isBluetoothDiscoverable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isBluetoothDiscoverable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to enable or disable auto-reconnect.
	 * The setting would persist until client requests again.
	 * Auto-reconnect only applies to HSP, HFP and A2DP profiles when out of range event takes place.
	 * Besides, only one of HSP or HFP would be re-connected, and HFP is preferred to HSP.
	 * Enabling auto-reconnect only applies to the following conditions, 
	 *		<p><blockquote>next out of range event,
	 *		<br>there is out of range event before system shutdown AND system is restarted, or 
	 *		<br>there is out of range event before Bluetooth turning-off AND Bluetooth is restarted</blockquote>
	 *
	 * @param isEnable : true to enable or false to disable.
	 */
@Override public void setBluetoothAutoReconnect(boolean isEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setBluetoothAutoReconnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 
	 * Request to force out of range flags to be set or un-set.
	 * This request dose not enable or disable auto-reconnect automatically. It just sets OOR flags.
	 * {@link INforeService#setBluetoothAutoReconnect setBluetoothAutoReconnect} must be invocated before calling this API.
	 * Besides, if there is no connected profile when this API is invocated, this API would change nothing.
	 * This API is generally invocated before system shutdown or Bluetooth turning-off, and it would force our
	 * service to connect to the device, which is connected when this API is invocated, 
	 * when system is restarted or Bluetooth is turned on next time. 
	 *
	 * @param isEnable : true to enable or false to disable.
	 */
@Override public void setBluetoothAutoReconnectForceOor(boolean isEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setBluetoothAutoReconnectForceOor, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 
	 * Return true if auto-reconnect is currently enabled.
	 *
	 * @return true if auto-reconnect is enabled.
	 */
@Override public boolean isBluetoothAutoReconnectEnable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isBluetoothAutoReconnectEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int reqBluetoothConnectAll(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqBluetoothConnectAll, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int reqBluetoothDisconnectAll() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reqBluetoothDisconnectAll, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int reqBluetoothRemoteUuids(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqBluetoothRemoteUuids, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * HFP
	 *//** 
	 * Register callback functions for HFP profile.
	 * Call this function to register callback functions for HFP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean registerHfpCallback(nfore.android.bt.servicemanager.IHfpCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerHfpCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Remove callback functions for HFP profile.
     * Call this function to remove previously registered callback interface for HFP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean unregisterHfpCallback(nfore.android.bt.servicemanager.IHfpCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterHfpCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to connect HFP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @attention : We only support one connection at this released version.
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqHfpConnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpConnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to disconnect the connected HFP connection to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqHfpDisconnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpDisconnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the hardware Bluetooth address of connected remote HFP device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected HFP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
@Override public java.lang.String getHfpConnectedDeviceAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getHfpConnectedDeviceAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getHfpCurrentState(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getHfpCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getHfpCurrentScoState(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getHfpCurrentScoState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the current voice control state of remote connected HFP device with given Bluetooth hardware address.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return voice control status (On/Off).
	 */
@Override public boolean isHfpVoiceControlOn(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_isHfpVoiceControlOn, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to dial the outgoing call with given phone number.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param callNumber : the outgoing call phone number.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqHfpDialCall(java.lang.String address, java.lang.String callNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(callNumber);
mRemote.transact(Stub.TRANSACTION_reqHfpDialCall, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to re-dial the last outgoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqHfpReDial(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpReDial, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the memory dialing.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @param memoryLocation : the memory location on mobile phone. Mobile phone would retrive phone number stored in this place and dial out.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqHfpMemoryDial(java.lang.String address, java.lang.String memoryLocation) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(memoryLocation);
mRemote.transact(Stub.TRANSACTION_reqHfpMemoryDial, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to answer the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true if send command success
	 */
@Override public boolean reqHfpAnswerCall(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpAnswerCall, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to reject the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true if send command success
	 */
@Override public boolean reqHfpRejectIncomingCall(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpRejectIncomingCall, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to terminate the ongoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true if send command success
	 */
@Override public boolean reqHfpTerminateOngoingCall(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpTerminateOngoingCall, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the multipaty calls control.
	 * This API should only been called when there are multiparty calls.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param opCode :
	 *		<p><blockquote>HFP_CHLD_RELEASE_ALL_HELD_CALLS				(byte) 0x00
	 *		<br>HFP_CHLD_SET_USER_BUSY_FOR_WAITING_CALL					(byte) 0x00
	 *		<br>HFP_CHLD_RELEASE_ALL_ACTIVE_CALLS_ACCEPT_THEOTHER		(byte) 0x01
	 *		<br>HFP_CHLD_HOLD_ALL_ACTIVE_CALLS_ACCEPT_THEOTHER			(byte) 0x02
	 *		<br>HFP_CHLD_ADD_HELD_CALL_TO_CONVERSATION					(byte) 0x03</blockquote>	
	 * @return true if send command success
	 */
@Override public boolean reqHfpMultiCallControl(java.lang.String address, byte opCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(opCode);
mRemote.transact(Stub.TRANSACTION_reqHfpMultiCallControl, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the DTMF.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Due to the compatibility, please request this API with single DTMF number each time.
	 * Avoid requesting with serial DTMF numbers. 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @param dtmfNumber : the DTMF number.
 	 * @return true if send command success
	 */
@Override public boolean reqHfpSendDtmf(java.lang.String address, java.lang.String dtmfNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(dtmfNumber);
mRemote.transact(Stub.TRANSACTION_reqHfpSendDtmf, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the SCO audio transfer.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged} to be notified of subsequent state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param opCode :
	 *		<p><blockquote>AUDIO_TRANSFER_TO_PHONE	(byte) 0x00
	 *		<br>AUDIO_TRANSFER_TO_CARKIT			(byte) 0x01</blockquote>
	 * @return true if send command success	 
	 */
@Override public boolean reqHfpAudioTransfer(java.lang.String address, byte opCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(opCode);
mRemote.transact(Stub.TRANSACTION_reqHfpAudioTransfer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to enable or disable the local ringtone when there is an incoming call.
	 *
	 * @param isEnable
	 *<p><value>=true enable ring
	 *<p><value>=false disable ring
	 */
@Override public void setHfpLocalRingEnable(boolean isEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setHfpLocalRingEnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 
	 * Request information of remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#retHfpRemoteDeviceInfo retHfpRemoteDeviceInfo} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 */
@Override public void reqHfpRemoteDeviceInfo(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpRemoteDeviceInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 
	 * Request for the name of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This method would return the name announced in HFP by remote device in String type.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.	 
	 * @return the real String type name of remote device or the empty string.
	 */
@Override public java.lang.String getHfpRemoteDeviceName(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getHfpRemoteDeviceName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the active or held phone number of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpRemoteDevicePhoneNumberChanged onHfpRemoteDevicePhoneNumberChanged} to be notified of subsequent result.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true if send command success
	 */
@Override public boolean reqHfpRemoteDevicePhoneNumber(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpRemoteDevicePhoneNumber, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the network operator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#retHfpRemoteDeviceNetworkOperator retHfpRemoteDeviceNetworkOperator} to be notified of subsequent result.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true if send command success
	 */
@Override public boolean reqHfpRemoteDeviceNetworkOperator(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpRemoteDeviceNetworkOperator, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the subscriber number information of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#retHfpRemoteDeviceSubscriberNumber retHfpRemoteDeviceSubscriberNumber} to be notified of subsequent result.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true if send command success
	 */
@Override public boolean reqHfpRemoteDeviceSubscriberNumber(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpRemoteDeviceSubscriberNumber, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the status of telecom service indicator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate remote device has telecom service.	 
	 */
@Override public boolean isHfpRemoteDeviceTelecomServiceOn(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_isHfpRemoteDeviceTelecomServiceOn, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the status of roaming indicator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate roaming is active on remote device.	 
	 */
@Override public boolean isHfpRemoteDeviceOnRoaming(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_isHfpRemoteDeviceOnRoaming, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getHfpRemoteDeviceBatteryStatus(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getHfpRemoteDeviceBatteryStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getHfpRemoteDeviceSignalStatus(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getHfpRemoteDeviceSignalStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to enable or disable new SMS notification by AT command interface.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.	 
	 * @param isEnable : true to enable or false.
	 */
@Override public void setHfpAtNewSmsNotify(java.lang.String address, boolean isEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(((isEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setHfpAtNewSmsNotify, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Request to download phone book by AT command interface from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpAtDownloadPhonebookStateChanged onHfpAtDownloadPhonebookStateChanged} to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.	
	 * @param storage :
	 *		<p><blockquote>PBAP_STORAGE_SIM		(int) 1
	 *		<br>PBAP_STORAGE_PHONE_MEMORY		(int) 2
	 *		<br>PBAP_STORAGE_DIALED_CALLS		(int) 3
	 *		<br>PBAP_STORAGE_MISSED_CALLS		(int) 4
	 *		<br>PBAP_STORAGE_RECEIVED_CALLS		(int) 5</blockquote>	
	 * @return true if send command success	 
	 */
@Override public boolean reqHfpAtDownloadPhonebook(java.lang.String address, int storage) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(storage);
mRemote.transact(Stub.TRANSACTION_reqHfpAtDownloadPhonebook, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to download SMS by AT command interface from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpAtDownloadSMSStateChanged onHfpAtDownloadSMSStateChanged} to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.	
	 * @return true if send command success	 
	 */
@Override public boolean reqHfpAtDownloadSMS(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHfpAtDownloadSMS, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to inform remote connected HFP device with given Bluetooth hardware address the current gain settings
	 * of local speaker or microphone.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#setHFPSpeakVolState setHFPSpeakVolState} and {@link IHfpCallback#setHFPVolState setHFPVolState}
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.
	 * @param attributeId :
	 *		<p><blockquote>VOLUME_ATTRIBUTE_SPEAKER	(byte) 0x01
	 *		<br>VOLUME_ATTRIBUTE_MIC				(byte) 0x02</blockquote>	 
	 * @param volume : sync volume
	 * @return true if send command success
	 */
@Override public boolean reqHfpRemoteDeviceVolumeSync(java.lang.String address, byte attributeId, int volume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(attributeId);
_data.writeInt(volume);
mRemote.transact(Stub.TRANSACTION_reqHfpRemoteDeviceVolumeSync, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request remote connected HFP device with given Bluetooth hardware address to do the voice dialing.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!		 
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @param isEnable : true to start the voice dialing.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqHfpVoiceDial(java.lang.String address, boolean isEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(((isEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_reqHfpVoiceDial, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * HSP
	 *//** 
	 * Register callback functions for HSP profile.
	 * Call this function to register callback functions for HSP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean registerHspCallback(nfore.android.bt.servicemanager.IHspCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerHspCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Remove callback functions for HSP profile.
     * Call this function to remove previously registered callback interface for HSP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean unregisterHspCallback(nfore.android.bt.servicemanager.IHspCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterHspCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to connect HSP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @attention : We only support one connection at this released version.
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean reqHspConnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHspConnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to disconnect the connected HSP connection to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean reqHspDisconnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHspDisconnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the hardware Bluetooth address of connected remote HSP device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected HSP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
@Override public java.lang.String getHspConnectedDeviceAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getHspConnectedDeviceAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getHspCurrentState(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getHspCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getHspCurrentScoState(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getHspCurrentScoState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to re-dial the last outgoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean reqHspReDial(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHspReDial, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to answer the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqHspAnswerCall(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHspAnswerCall, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to reject the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!! By the definition of HSP spec, 
	 * HS could not request this operation. Due to this reason, please avoid to use this API.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
@Override public boolean reqHspRejectIncomingCall(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHspRejectIncomingCall, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to terminate the ongoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
@Override public boolean reqHspTerminateOngoingCall(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHspTerminateOngoingCall, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to do the SCO audio transfer.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}	 
	 * to be notified of subsequent state changes.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!! By the definition of HSP spec, 
	 * HS could only request to transfer audio connection from AG to HS. If we request to transfer audio connection from HS to AG,
	 * the overall connection might be released by AG, and the ongoing call might be terminated. 
	 * Due to this reason, please avoid to use this API.
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param opCode :
	 *		<p><blockquote>AUDIO_TRANSFER_TO_PHONE	(byte) 0x00
	 *		<br>AUDIO_TRANSFER_TO_CARKIT			(byte) 0x01</blockquote>	 
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
@Override public boolean reqHspAudioTransfer(java.lang.String address, byte opCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(opCode);
mRemote.transact(Stub.TRANSACTION_reqHspAudioTransfer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to inform remote connected HSP device with given Bluetooth hardware address the current gain settings
	 * of local speaker or microphone.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspSpeakerVolumeChanged onHspSpeakerVolumeChanged} and {@link HSPCallback#onHspMicVolumeChanged onHspMicVolumeChanged}
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.
	 * @param attributeId :
	 *		<p><blockquote>VOLUME_ATTRIBUTE_SPEAKER	(byte) 0x01
	 *		<br>VOLUME_ATTRIBUTE_MIC				(byte) 0x02</blockquote>	 
	 * @param volume : sync volume
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
@Override public boolean reqHspRemoteDeviceVolumeSync(java.lang.String address, byte attributeId, int volume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(attributeId);
_data.writeInt(volume);
mRemote.transact(Stub.TRANSACTION_reqHspRemoteDeviceVolumeSync, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * SPP
	 *//** 
	 * Register callback functions for SPP profile.
	 * Call this function to register callback functions for SPP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean registerSppCallback(nfore.android.bt.servicemanager.ISppCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerSppCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Remove callback functions for SPP profile.
     * Call this function to remove previously registered callback interface for SPP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean unregisterSppCallback(nfore.android.bt.servicemanager.ISppCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterSppCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to connect SPP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link ISppCallback#onSppStateChanged onSppStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqSppConnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqSppConnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to disconnect the connected SPP connection to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link ISppCallback#onSppStateChanged onSppStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqSppDisconnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqSppDisconnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the hardware Bluetooth address of connected remote SPP devices.
	 * For example, "00:11:22:AA:BB:CC".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link ISppCallback#retSppConnectedDeviceAddressList retSppConnectedDeviceAddressList} to be notified of subsequent result.
	 */
@Override public void reqSppConnectedDeviceAddressList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reqSppConnectedDeviceAddressList, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 
	 * Return true if device with given address is currently connected.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 * @return true if connected.
	 */
@Override public boolean isSppConnected(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_isSppConnected, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to send given data to remote connected SPP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Data size should not be greater than 512 bytes each time. 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param sppData : the data to be sent.
	 */
@Override public void reqSppSendData(java.lang.String address, byte[] sppData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByteArray(sppData);
mRemote.transact(Stub.TRANSACTION_reqSppSendData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/* ======================================================================================================================================================== 
	 * PBAP
	 *//** 
	 * Register callback functions for PBAP profile.
	 * Call this function to register callback functions for PBAP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean registerPbapCallback(nfore.android.bt.servicemanager.IPbapCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerPbapCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Remove callback functions for PBAP profile.
     * Call this function to remove previously registered callback interface for PBAP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean unregisterPbapCallback(nfore.android.bt.servicemanager.IPbapCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterPbapCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to download phone book by Vcard from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IPbapCallback#onPbapStateChanged onPbapStateChanged} and {@link IPbapCallback#onPbapDownloadNotify onPbapDownloadNotify} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.	
	 * @param storage :
	 *		<p><blockquote>PBAP_STORAGE_SIM		(int) 1
	 *		<br>PBAP_STORAGE_PHONE_MEMORY		(int) 2
	 *		<br>PBAP_STORAGE_DIALED_CALLS		(int) 3
	 *		<br>PBAP_STORAGE_MISSED_CALLS		(int) 4
	 *		<br>PBAP_STORAGE_RECEIVED_CALLS		(int) 5</blockquote>	
	 * @param notifyFreq : define the callback frequency.
	 * @param isUseContactsProvider : true is use Contacts Provider , or false is use nFore Database
	 *<p><value>=0 all vcards would be downloaded first, and inserted to database. Only one callback would be invocated.
	 *<p><value>>0 Callbacks would be invocated every "notifyFreq" vcards have been downloaded and inserted to database. 
	 * @return true to indicate the operation has been done without error, or false on immediate error or system is busy.	 
	 */
@Override public boolean reqPbapDownloadAllVcards(java.lang.String address, int storage, int notifyFreq, boolean isUseContactsProvider) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(storage);
_data.writeInt(notifyFreq);
_data.writeInt(((isUseContactsProvider)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_reqPbapDownloadAllVcards, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	 * Request to check if database is available for query.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapDatabaseAvailable retPbapDatabaseAvailable} to be notified when database is available.
	 *
	 * @param address : valid Bluetooth MAC address.	    
	 */
@Override public void reqPbapDatabaseAvailable(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqPbapDatabaseAvailable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Request to query database the corresponding name of given phone number.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapQueryName retPbapQueryName} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.
     * @param phoneNumber : the phone number given to database query.
     */
@Override public void reqPbapQueryName(java.lang.String address, java.lang.String phoneNumber) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(phoneNumber);
mRemote.transact(Stub.TRANSACTION_reqPbapQueryName, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Request to delete data by specific address from database of PBAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapDeleteDatabaseByAddressCompleted retPbapDeleteDatabaseByAddressCompleted} to be notified when database has been deleted.	 
	 *
	 * @param address : valid Bluetooth MAC address.
	 */
@Override public void reqPbapDeleteDatabaseByAddress(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqPbapDeleteDatabaseByAddress, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Request to clean database of PBAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapCleanDatabaseCompleted retPbapCleanDatabaseCompleted} to be notified when database has been cleaned.	 
	 */
@Override public void reqPbapCleanDatabase() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reqPbapCleanDatabase, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Request to interrupt the ongoing download on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Clients should register and implement callback function {@link IPbapCallback#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.    
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true if really try to interrupt.
	 */
@Override public boolean reqPbapDownloadInterrupt(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqPbapDownloadInterrupt, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getPbapCurrentState(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getPbapCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * MAP
	 *//** 
	 * Register callback functions for MAP profile.
	 * Call this function to register callback functions for MAP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean registerMapCallback(nfore.android.bt.servicemanager.IMapCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerMapCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Remove callback functions for MAP profile.
     * Call this function to remove previously registered callback interface for MAP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean unregisterMapCallback(nfore.android.bt.servicemanager.IMapCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterMapCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to download all messages from remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IMapCallback#onMapStateChanged onMapStateChanged} to be notified of subsequent result.
	 * Besides, clients should also register and implement callback function
	 * {@link IMapCallback#onMapDownloadOutlineCompleted onMapDownloadOutlineCompleted} to be notified of subsequent result if
	 * parameter isContentDownload is set to false.	 
	 *
	 * @param address : valid Bluetooth MAC address.	
	 * @param folder :
	 *		<p><blockquote>MAP_INBOX_OUTBOX		(int) 0
	 *		<br>MAP_INBOX_ONLY					(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>	
	 * @param notifyFreq : define the callback frequency.
	 *<p><value>=0 all messages would be downloaded first, and inserted to database. Only one callback would be invocated.
	 *<p><value>>0 Callbacks would be invocated every "notifyFreq" messages have been downloaded and inserted to database. 
	 * @param isContentDownload :
	 * <p><value>=false, download outline only
	 * <p><value>=true, download all messages including the contents, but this will let all message set to read status
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
@Override public boolean reqMapDownloadAllMessages(java.lang.String address, int folder, int notifyFreq, boolean isContentDownload) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(folder);
_data.writeInt(notifyFreq);
_data.writeInt(((isContentDownload)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_reqMapDownloadAllMessages, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to download single message from remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IMapCallback#onMapStateChanged onMapStateChanged} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.	
	 * @param folder :
	 *		<p><blockquote>MAP_INBOX_ONLY		(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>
	 * @param handle : MAP handle
	 * @return true to indicate the operation has been done without error, or false on immediate error.		 
	 */
@Override public boolean reqMapDownloadSingleMessage(java.lang.String address, int folder, java.lang.String handle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(folder);
_data.writeString(handle);
mRemote.transact(Stub.TRANSACTION_reqMapDownloadSingleMessage, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to register notification when there is new message on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Clients should register and implement callback function {@link IMapCallback#onMapNotificationStateChanged onMapNotificationStateChanged} and
	 * {@link IMapCallback#onMapNewMessageReceived onMapNewMessageReceived}
	 * to be notified of subsequent result.    
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @param downloadNewMessage : if true, download the new message, too
	 */
@Override public boolean reqMapRegisterNotification(java.lang.String address, boolean downloadNewMessage) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(((downloadNewMessage)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_reqMapRegisterNotification, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to unregister new message notification on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB". 
	 *
	 * @param address : valid Bluetooth MAC address.
	 */
@Override public void reqMapUnregisterNotification(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqMapUnregisterNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 
	 * Return true if the new message notification is registered on device with given address.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 * @return true if registered.
	 */
@Override public boolean isMapNotificationRegistered(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_isMapNotificationRegistered, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to interrupt the ongoing download on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Clients should register and implement callback function {@link IMapCallback#onMapStateChanged onMapStateChanged} to be notified of subsequent result.    
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true if really try to interrupt.
	 */
@Override public boolean reqMapDownloadInterrupt(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqMapDownloadInterrupt, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	 * Request to check if database is available for query
	 * Client should register and implement {@link IMapCallback#retMapDatabaseAvailable retMapDatabaseAvailable} 
	 * to be notified when database is available.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 */
@Override public void reqMapDatabaseAvailable(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqMapDatabaseAvailable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Request to delete data by specific address
	 * Client should register and implement {@link IMapCallback#retMapDeleteDatabaseByAddressCompleted retMapDeleteDatabaseByAddressCompleted} 
	 * to be notified when database is available.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 */
@Override public void reqMapDeleteDatabaseByAddress(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqMapDeleteDatabaseByAddress, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Request to clean database of MAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IMapCallback#retMapCleanDatabaseCompleted retMapCleanDatabaseCompleted} to be notified when database has been cleaned.	 
	 */
@Override public void reqMapCleanDatabase() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reqMapCleanDatabase, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getMapCurrentState(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getMapCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getMapRegisterState(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getMapRegisterState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * A2DP AVRCP
	 *//** 
	 * Register callback functions for A2DP and AVRCP profile.
	 * Call this function to register callback functions for A2DP and AVRCP profile.
	 * Allow nFore service to call back to its registered clients, which is often UI.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean registerA2dpAvrcpCallback(nfore.android.bt.servicemanager.IA2dpCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerA2dpAvrcpCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Remove callback functions for A2DP and AVRCP profile.
     * Call this function to remove previously registered callback interface for A2DP and AVRCP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean unregisterA2dpAvrcpCallback(nfore.android.bt.servicemanager.IA2dpCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterA2dpAvrcpCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * A2DP v1.0 v1.2
	 *//** 
	 * Request to connect A2DP and AVRCP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} and {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both A2DP and AVRCP will be connected and the sequence of state changed callback of profiles! 
	 * This depends on the behavior of connected device.
	 *
	 * @attention : We only support one connection of all and restrict A2DP and AVRCP should be the same remote device at this released version.
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqA2dpConnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqA2dpConnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to disconnect the connected A2DP and AVRCP connections to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} and {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both A2DP and AVRCP will be disconnected and the sequence of state changed callback of profiles! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqA2dpDisconnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqA2dpDisconnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the hardware Bluetooth address of connected remote A2DP/AVRCP device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected A2DP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
@Override public java.lang.String getA2dpConnectedDeviceAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getA2dpConnectedDeviceAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getA2dpCurrentState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getA2dpCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getAvrcpCurrentState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAvrcpCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to set the volume of A2DP streaming music locally.
	 * Volume level is indicated by the unit of millibels. 
	 * Besides, due to the limit of system, this API could only lower down the volume, 
	 * which is done by giving negative value to parameter vol, and set volume back to the original level,
	 * which is done by giving zero to parameter vol.
	 * If the player is muted, which is done by calling {@link INforeService#setA2dpLocalMute setA2dpLocalMute}, 
	 * calls to this API will still change the internal volume level, but this will have no audible effect until
	 * the player is unmuted.
	 * Never provide the positive value to parameter vol.
	 * This is an asynchronous call: it will return immediately. 
	 *
	 * @param vol : volume level to set. The possible values are from -32768 (-0x7FFF-1) to 0 (0x00).
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean setA2dpLocalVolume(int vol) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(vol);
mRemote.transact(Stub.TRANSACTION_setA2dpLocalVolume, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to mute the A2DP streaming music locally.
	 * If the player is muted by calling this API, calls to {@link INforeService#setA2dpLocalVolume setA2dpLocalVolume} will still change the internal volume level, 
	 * but this will have no audible effect until the player is unmuted by calling this API again.
	 * This is an asynchronous call: it will return immediately. 
	 *
	 * @param isMute : boolean indicating whether to mute or unmute.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean setA2dpLocalMute(boolean isMute) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isMute)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setA2dpLocalMute, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * AVRCP v1.0
	 *//** 
	 * Request for the hardware Bluetooth address of connected remote AVRCP device.
	 * There might be no need for users to concern for this in general case. Users could call 
	 * {@link INforeService#getA2dpConnectedDeviceAddress getA2dpConnectedDeviceAddress} to get the connected A2DP/AVRCP device address.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected AVRCP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
@Override public java.lang.String getAvrcpConnectedDeviceAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAvrcpConnectedDeviceAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request if AVRCP 1.3 is supported by remote device with given Bluetooth hardware address.
	 * The requested address must be the paired device and is connected currently.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : valid Bluetooth MAC address of paired and connected AVRCP device.
	 * @return false if the device dose not support version 1.3 or is not connected currently.  
	 */
@Override public boolean isAvrcp13Supported(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_isAvrcp13Supported, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request if AVRCP 1.4 is supported by remote device with given Bluetooth hardware address.
	 * The requested address must be the paired device and is connected currently.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : valid Bluetooth MAC address of paired and connected AVRCP device.
	 * @return false if the device dose not support version 1.4 or is not connected currently.  
	 */
@Override public boolean isAvrcp14Supported(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_isAvrcp14Supported, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Play" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpPlay(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpPlay, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Stop" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpStop(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpStop, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Pause" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpPause(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpPause, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Forward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpForward(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpForward, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Backward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpBackward(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpBackward, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Volume Up" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpVolumeUp(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpVolumeUp, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Volume Down" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpVolumeDown(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpVolumeDown, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to start the "Fast Forward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpStartFastForward(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpStartFastForward, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to stop the "Fast Forward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpStopFastForward(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpStopFastForward, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to start the "Rewind" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpStartRewind(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpStartRewind, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to stop the "Rewind" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpStopRewind(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpStopRewind, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * AVRCP v1.3
	 *//** 
	 * Request to get the supported events of capabilities supported by remote connected A2DP/AVRCP device 
	 * with given Bluetooth hardware address. This is sent by CT to inquire capabilities of the peer device.
	 * This requests the list of events supported by the remote device. Remote device is expected to respond with all the events supported 
	 * including the mandatory events defined in the AVRCP v1.3 specification. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpCapabilitiesSupportEvent retAvrcpCapabilitiesSupportEvent} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetCapabilitiesSupportEvent(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetCapabilitiesSupportEvent, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to get the supported player application setting attributes provided by remote connected A2DP/AVRCP device 
	 * with given Bluetooth hardware address. 
	 * The list of reserved player application setting attributes is provided in Appendix F of AVRCP v1.3 specification. 
	 * It is expected that a target device may have additional attributes not defined as part of the specification.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingAttributesList retAvrcpPlayerSettingAttributesList} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetPlayerSettingAttributesList(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetPlayerSettingAttributesList, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to get the set of possible values for the requested player application setting attribute 
	 * provided by remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * The list of reserved player application setting attributes and their values are provided in Appendix F of AVRCP v1.3 specification. 
	 * It is expected that a target device may have additional attribute values not defined as part of the specification.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingValuesList retAvrcpPlayerSettingValuesList} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetPlayerSettingValuesList(java.lang.String address, byte attributeId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(attributeId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetPlayerSettingValuesList, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to get the current set values on the remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * for the provided player application setting attribute. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingCurrentValues retAvrcpPlayerSettingCurrentValues} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetPlayerSettingCurrentValues(java.lang.String address, byte attributeId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(attributeId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetPlayerSettingCurrentValues, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to set the player application setting of player application setting value on the remote connected A2DP/AVRCP device 
	 * with given Bluetooth hardware address for the corresponding defined PlayerApplicationSettingAttribute. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingValueSet retAvrcpPlayerSettingValueSet} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @param valueId : the setting value. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpSetPlayerSettingValue(java.lang.String address, byte attributeId, byte valueId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(attributeId);
_data.writeByte(valueId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpSetPlayerSettingValue, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to get the attributes of the element specified in the parameter 
	 * provided by remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpElementAttributesPlaying retAvrcpElementAttributesPlaying} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetElementAttributesPlaying(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetElementAttributesPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to get the status of the currently playing media at 
	 * remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayStatus retAvrcpPlayStatus} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetPlayStatus(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetPlayStatus, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to register with remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * to receive notifications asynchronously based on specific events occurring. 
	 * The events registered would be kept on remote device until another
	 * {@link INforeService#reqAvrcpUnregisterEventWatcher reqAvrcpUnregisterEventWatcher} is called.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * <p><blockquote>{@link IA2dpCallback#onAvrcpEventPlaybackStatusChanged onAvrcpEventPlaybackStatusChanged},
	 * <br>{@link IA2dpCallback#onAvrcpEventTrackChanged onAvrcpEventTrackChanged},
	 * <br>{@link IA2dpCallback#onAvrcpEventTrackReachedEnd onAvrcpEventTrackReachedEnd},
	 * <br>{@link IA2dpCallback#onAvrcpEventTrackReachedStart onAvrcpEventTrackReachedStart},
	 * <br>{@link IA2dpCallback#onAvrcpEventPlaybackPosChanged onAvrcpEventPlaybackPosChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventBatteryStatusChanged onAvrcpEventBatteryStatusChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventSystemStatusChanged onAvrcpEventSystemStatusChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventPlayerSettingChanged onAvrcpEventPlayerSettingChanged},
 	 * <br>v1.4
 	 * <br>{@link IA2dpCallback#onAvrcpEventNowPlayingContentChanged onAvrcpEventNowPlayingContentChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventAvailablePlayerChanged onAvrcpEventAvailablePlayerChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventAddressedPlayerChanged onAvrcpEventAddressedPlayerChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventUidsChanged onAvrcpEventUidsChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventVolumeChanged onAvrcpEventVolumeChanged}, and
	 * <br>{@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} </blockquote>
	 * to be notified of subsequent result.
	 * Each corresponding callback would be invoked once immediately after the event has been registered successfully. 
	 *
	 * @attention : Although we implement all these events required by specification, 
	 * we STRONGLY suggest that please AVOID using the following three events :
	 *		<p><blockquote>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07	 
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d	</blockquote>	 
	 *  
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param eventId : the registering event ID. Possible values are
	 *		<p><blockquote>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED				(byte) 0x01
	 *		<br>AVRCP_EVENT_ID_TRACK_CHANGED						(byte) 0x02
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_END					(byte) 0x03
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_START					(byte) 0x04
	 *		<br>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED					(byte) 0x05
	 *		<br>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07
	 *		<br>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED	(byte) 0x08
	 *		<br>v1.4
	 *		<br>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED			(byte) 0x09
	 *		<br>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED			(byte) 0x0a
	 *		<br>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED				(byte) 0x0b
	 *		<br>AVRCP_EVENT_ID_UIDS_CHANGED							(byte) 0x0c
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d</blockquote>		
	 * @param interval : the update interval in second. This parameter applicable only for
	 * AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED. For other events, this parameter is ignored !
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpRegisterEventWatcher(java.lang.String address, byte eventId, long interval) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(eventId);
_data.writeLong(interval);
mRemote.transact(Stub.TRANSACTION_reqAvrcpRegisterEventWatcher, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to unregister the specific events with remote connected A2DP/AVRCP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param eventId : the unregistering event ID. Possible values are
	 *		<p><blockquote>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED				(byte) 0x01
	 *		<br>AVRCP_EVENT_ID_TRACK_CHANGED						(byte) 0x02
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_END					(byte) 0x03
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_START					(byte) 0x04
	 *		<br>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED					(byte) 0x05
	 *		<br>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07
	 *		<br>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED	(byte) 0x08
	 *		<br>v1.4	 
	 *		<br>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED			(byte) 0x09
	 *		<br>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED			(byte) 0x0a
	 *		<br>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED				(byte) 0x0b
	 *		<br>AVRCP_EVENT_ID_UIDS_CHANGED							(byte) 0x0c
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d</blockquote>		
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpUnregisterEventWatcher(java.lang.String address, byte eventId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(eventId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpUnregisterEventWatcher, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to abort the continuing response.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpMetadataReceivingAborted retAvrcpMetadataReceivingAborted} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpAbortMetadataReceiving(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpAbortMetadataReceiving, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to move to the first song in the next group.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpNextGroup(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpNextGroup, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to move to the first song in the previous group.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpPreviousGroup(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqAvrcpPreviousGroup, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to set the company ID on the remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.	
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param companyId : the setting company ID. Company-ID is 24bit unsigned value, initialized to Vendor_Unique (0x001958) when
	 * called on Connect().
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpSetMetadataCompanyId(java.lang.String address, int companyId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(companyId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpSetMetadataCompanyId, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to get 
	 * the supported player application setting attributes displayable text for the provided PlayerApplicationSettingAttributeID 
	 * This command is expected to be used only for extended attributes for menu navigation. 
	 * It is assumed that all <attribute, value> pairs used for menu extensions are statically defined by remote device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingAttributeText retAvrcpPlayerSettingAttributeText} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.		 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetPlayerSettingAttributeText(java.lang.String address, byte attributeId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(attributeId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetPlayerSettingAttributeText, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to get 
	 * the supported player application setting value displayable text for the provided player application setting attribute value. 
	 * This command is expected to be used only for extended attributes for menu navigation. 
	 * It is assumed that all <attribute, value> pairs used for menu extensions are statically defined by remote device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingValueText retAvrcpPlayerSettingValueText} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.		 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @param valueId : the requesting value. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetPlayerSettingValueText(java.lang.String address, byte attributeId, byte valueId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(attributeId);
_data.writeByte(valueId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetPlayerSettingValueText, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to informs the character set we supported to remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * This shall allow remote device to send responses with strings in the character set we supported.
	 * By default remote device shall send strings in UTF-8 if this command has not been sent.
	 * It is mandatory to send UTF-8 as one of the supported character set.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpDisplayableCharsetInformed retAvrcpDisplayableCharsetInformed} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. AVRCP's default charaset is UTF-8, so don't change the character set used by this command.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param charsetId : the supported charset IDs.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpInformDisplayableCharset(java.lang.String address, int[] charsetId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeIntArray(charsetId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpInformDisplayableCharset, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to informs the loacl battery status changed to remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpBatteryStatusOfCtInformed retAvrcpBatteryStatusOfCtInformed} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.		 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param batteryStatusId : the battery status. Possible values are
	 *		<p><blockquote>AVRCP_BATTERY_STATUS_ID_NORMAL		(byte) 0x00
	 *		<br>AVRCP_BATTERY_STATUS_ID_WARNING		(byte) 0x01
	 *		<br>AVRCP_BATTERY_STATUS_ID_CRITICAL	(byte) 0x02
	 * 		<br>AVRCP_BATTERY_STATUS_ID_EXTERNAL	(byte) 0x03
	 * 		<br>AVRCP_BATTERY_STATUS_ID_FULL		(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpInformBatteryStatusOfCt(java.lang.String address, byte batteryStatusId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(batteryStatusId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpInformBatteryStatusOfCt, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * AVRCP v1.4
	 *//** 
	 * Request to inform the remote connected A2DP/AVRCP device with given Bluetooth hardware address 
	 * of which media player we wishes to control.
	 * The player is selected by its "Player Id".
	 * When the addressed player is changed, whether locally on the remote device or explicitly by us, 
	 * the remote device shall complete notifications following the mechanism described in section 6.9.2 of AVRCP v1.4 specification. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpAddressedPlayerSet retAvrcpAddressedPlayerSet} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param playerId : the selected player ID of 2 octets. 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpSetAddressedPlayer(java.lang.String address, int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpSetAddressedPlayer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request the remote connected A2DP/AVRCP device with given Bluetooth hardware address to control to which player browsing commands should be routed.
	 * The player to which AVRCP shall route browsing commands is the browsed player. 
	 * This command shall be sent successfully before any other commands are sent on the browsing channel except 
	 * {@link INforeService#reqAvrcpGetFolderItems reqAvrcpGetFolderItems}
	 * in the Media Player List scope. 
	 * If the browsed player has become unavailable this command shall be sent successfully again before further commands are sent on the browsing channel. 
	 * Some players may support browsing only when set as the Addressed Player.
	 * The player is selected by its "Player Id".
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpBrowsedPlayerSet retAvrcpBrowsedPlayerSet} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param playerId : the selected player ID of 2 octets. 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpSetBrowsedPlayer(java.lang.String address, int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpSetBrowsedPlayer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to retrieve a listing of the contents of a folder on remote connected A2DP/AVRCP device with given Bluetooth hardware address.
	 * The folder is the representation of available media players, virtual file system, the last searching result, or the playing list.
	 * Should not issue this command to an empty folder.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpFolderItems retAvrcpFolderItems} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : the requesting folder ID. Possible values are
	 * 		<p><blockquote>AVRCP_SCOPE_ID_MEDIA_PLAYER	(byte) 0x00
	 *		<br>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetFolderItems(java.lang.String address, byte scopeId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(scopeId);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetFolderItems, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to navigate the virtual filesystem on remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * This command allows us to navigate one level up or down in the virtual filesystem.
	 * Uid counters parameter is used to make sure that our uid cache is consistent with what remote device has currently. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPathChanged retAvrcpPathChanged} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the folder to navigate to. This may be retrieved via a 
	 * {@link INforeService#reqAvrcpGetFolderItems reqAvrcpGetFolderItems} command. 
	 * If the navigation command is "Folder Up" this field is reserved..	 
	 * @param direction : the requesting operation on selested UID. Possible values are
	 * 		<p><blockquote>AVRCP_FOLDER_DIRECTION_ID_UP	(byte) 0x00
	 *		<br>AVRCP_FOLDER_DIRECTION_ID_DOWN	(byte) 0x01</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpChangePath(java.lang.String address, int uidCounter, long uid, byte direction) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeInt(uidCounter);
_data.writeLong(uid);
_data.writeByte(direction);
mRemote.transact(Stub.TRANSACTION_reqAvrcpChangePath, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to retrieve the metadata attributes for a particular media element item or folder item 
	 * on remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * When the remote device supports this command, we shall use this command and not {@link INforeService#reqAvrcpGetElementAttributesPlaying reqAvrcpGetElementAttributesPlaying}. 
	 * To retrieve the Metadata for the currently playing track we should register to receive Track Changed Notifications. 
	 * This shall then provide the UID of the currently playing track, which can be used in the scope of the Now Playing folder.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpItemAttributes retAvrcpItemAttributes} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : the requesting folder ID. Possible values are
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the media element item or folder item to return the attributes for. UID 0 shall not be used.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpGetItemAttributes(java.lang.String address, byte scopeId, int uidCounter, long uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(scopeId);
_data.writeInt(uidCounter);
_data.writeLong(uid);
mRemote.transact(Stub.TRANSACTION_reqAvrcpGetItemAttributes, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address 
	 * to starts playing an item indicated by the UID. It is routed to the Addressed Player. 
	 * If a UID changed event has happened on the remote device, but not yet received by us, we may send this command with the previous UID counter. 
	 * In this case a failure status shall be returned.
	 * Request this command with the scope parameter of 
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01 or 
	 * 		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02</blockquote>
	 * shall result in the NowPlaying folder being invalidated. The old content may not be valid any more or may contain additional items. 
	 * What is put in the NowPlaying folder depends on both the media player and its state, however the item selected by us shall be included.
	 * Request this command with the scope parameter of 
	 * 		<p><blockquote>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * should not change the contents of the NowPlaying Folder, the only effect is that the new item is played.
	 * Never request this command with the scope parameter 
	 * 		<p><blockquote>AVRCP_SCOPE_ID_MEDIA_PLAYER	(byte) 0x00.</blockquote>
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpSelectedItemPlayed retAvrcpSelectedItemPlayed} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : The scope in which the UID of the media element item or folder item, if supported, is valid. Possible values are
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the media element item or folder item, if supported, to be played.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpPlaySelectedItem(java.lang.String address, byte scopeId, int uidCounter, long uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(scopeId);
_data.writeInt(uidCounter);
_data.writeLong(uid);
mRemote.transact(Stub.TRANSACTION_reqAvrcpPlaySelectedItem, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to perform the basic search from the current folder and all folders below that in the Browsed Players virtual filesystem. 
	 * Regular expressions shall not be supported. 
	 * Search results are valid until
	 * 		<p><blockquote>Another search request is performed or
	 *		<br>A UIDs changed notification response is received
	 * 		<br>The Browsed player is changed</blockquote>
	 * The search result would contain only media element items, not folder items.
	 * Searching may not be supported by all players. Furthermore, searching may only be possible on some players 
	 * when they are set as the Addressed Player.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpSearchResult retAvrcpSearchResult} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param text : The string to search on in the specified character set.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpSearch(java.lang.String address, java.lang.String text) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeString(text);
mRemote.transact(Stub.TRANSACTION_reqAvrcpSearch, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address 
	 * to adds an item indicated by the UID to the NowPlaying queue. 
	 * If a UID changed event has happened on the remote device, but not yet received by us, we may send this command with the previous UID counter. 
	 * In this case a failure status shall be returned.
	 * Request this command with the scope parameter of 
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 * 		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02 or
	 * 		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * shall result in the item being added to the NowPlaying folder on media players that support this command.
	 * Never request this command with the scope parameter 
	 * 		<p><blockquote>AVRCP_SCOPE_ID_MEDIA_PLAYER	(byte) 0x00.</blockquote>
	 * We could request this command with the UID of a Folder Item if the folder is playable. 
	 * The result of this is that the contents of that folder are added to the NowPlaying folder, not the folder itself.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpNowPlayingAdded retAvrcpNowPlayingAdded} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : The scope in which the UID of the media element item or folder item, if supported, is valid. Possible values are
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the media element item or folder item, if supported, to be played.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpAddToNowPlaying(java.lang.String address, byte scopeId, int uidCounter, long uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(scopeId);
_data.writeInt(uidCounter);
_data.writeLong(uid);
mRemote.transact(Stub.TRANSACTION_reqAvrcpAddToNowPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request if remote connected A2DP/AVRCP device with given Bluetooth hardware address has browsing channel established. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the remote device has browsing channel.
	 */
@Override public boolean isAvrcpBrowsingChannelEstablished(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_isAvrcpBrowsingChannelEstablished, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * By the AVRCP v1.4 specification, this command is used to set an absolute volume to be used by the rendering device. 
	 * This is in addition to the relative volume commands. 
	 * It is expected that the audio sink will perform as the TG role for this command.
	 * As this command specifies a percentage rather than an absolute dB level, the CT should exercise caution when sending this command.
	 * It should be noted that this command is intended to alter the rendering volume on the audio sink. 
	 * It is not intended to alter the volume within the audio stream. The volume level which has actually been set on the TG is returned in the response. 
	 * This is to enable the CT to deal with whatever granularity of volume control the TG provides.
	 * The setting volume is represented in one octet. The top bit (bit 7) is reserved for future definition. 
	 * The volume is specified as a percentage of the maximum. The value 0x0 corresponds to 0%. The value 0x7F corresponds to 100%.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpAbsoluteVolumeSet retAvrcpAbsoluteVolumeSet} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param volume : the setting volume value of octet from 0x00 to 0x7F.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqAvrcpSetAbsoluteVolume(java.lang.String address, byte volume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
_data.writeByte(volume);
mRemote.transact(Stub.TRANSACTION_reqAvrcpSetAbsoluteVolume, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Request to re-start the A2DP native socket thread for playing out stream data.
	 * <br>This request MUST be called if application has called {@link INforeService#setA2dpNativeStop setA2dpNativeStop}
	 * <br>This request is only useful for some specific situation, and application might not need to use this in general case.  
	 */
@Override public void setA2dpNativeStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setA2dpNativeStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Request to stop the A2DP native socket thread immediately.
	 * <br>{@link INforeService#setA2dpNativeStart setA2dpNativeStart} MUST be called after anything you want to do if this API was called.
	 * <br>Otherwise, there would be no audio playing out anymore !!! 
	 * <br>This request is only useful for some specific situation, and application might not need to use this in general case.  
	 */
@Override public void setA2dpNativeStop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setA2dpNativeStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Return false if A2DP native socket thread was stopped by {@link INforeService#setA2dpNativeStop setA2dpNativeStop}.
	 *
	 * @return false if A2DP native socket thread was stopped.
	 */
@Override public boolean isA2dpNativeRunning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isA2dpNativeRunning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * PAN Profile
	 *//** 
	 * Register callback functions for PAN profile.
	 * Call this function to register callback functions for PAN profile.
	 * Allow nFore service to call back to its registered clients, which is often UI.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean registerPanCallback(nfore.android.bt.servicemanager.IPanCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerPanCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Remove callback functions for PAN profile.
     * Call this function to remove previously registered callback interface for PAN profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean unregisterPanCallback(nfore.android.bt.servicemanager.IPanCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterPanCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to connect PAN to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IPanCallback#onPanStateChanged onPanStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that PAN will be connected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqPanConnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqPanConnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to disconnect the connected PAN connections to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IPanCallback#onPanStateChanged onPanStateChanged}
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both PAN will be disconnected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqPanDisconnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqPanDisconnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request for the hardware Bluetooth address of connected remote PAN device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected PAN device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
@Override public java.lang.String getPanConnectedDeviceAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPanConnectedDeviceAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getPanCurrentState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPanCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* ======================================================================================================================================================== 
	 * HID Profile
	 *//** 
	 * Register callback functions for HID profile.
	 * Call this function to register callback functions for HID profile.
	 * Allow nFore service to call back to its registered clients, which is often UI.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean registerHidCallback(nfore.android.bt.servicemanager.IHidCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerHidCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Remove callback functions for HID profile.
     * Call this function to remove previously registered callback interface for HID profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
@Override public boolean unregisterHidCallback(nfore.android.bt.servicemanager.IHidCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterHidCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to connect HID to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is a blocking call and clients should register and implement callback functions 
	 * {@link IHidCallback#onHidStateChanged onHidStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that HID will be connected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqHidConnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHidConnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** 
	 * Request to disconnect the connected HID connections to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an blocking call and clients should register and implement callback functions 
	 * {@link IHidCallback#onHidStateChanged onHidStateChanged}
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both HID will be disconnected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
@Override public boolean reqHidDisconnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_reqHidDisconnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int sendHidMouseCommand(int button, int offset_x, int offset_y, int wheel) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(button);
_data.writeInt(offset_x);
_data.writeInt(offset_y);
_data.writeInt(wheel);
mRemote.transact(Stub.TRANSACTION_sendHidMouseCommand, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int sendHidVirtualKeyCommand(int key_1, int key_2) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(key_1);
_data.writeInt(key_2);
mRemote.transact(Stub.TRANSACTION_sendHidVirtualKeyCommand, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getHidCurrentState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getHidCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_registerNforeServiceCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterNforeServiceCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setBluetoothEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setBluetoothDiscoverableDuration = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setBluetoothDiscoverablePermanent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_reqBluetoothScanning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_reqBluetoothPair = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_reqBluetoothUnpair = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_reqBluetoothUnpairAll = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_reqBluetoothUnpairAllNextTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_reqBluetoothPairedDevices = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getBluetoothLocalAdapterName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getBluetoothRemoteDeviceName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getBluetoothLocalAdapterAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_setBluetoothLocalAdapterName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_isBluetoothEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_getBluetoothCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_isBluetoothScanning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_isBluetoothDiscoverable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_setBluetoothAutoReconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_setBluetoothAutoReconnectForceOor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_isBluetoothAutoReconnectEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_reqBluetoothConnectAll = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_reqBluetoothDisconnectAll = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_reqBluetoothRemoteUuids = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_registerHfpCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_unregisterHfpCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_reqHfpConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_reqHfpDisconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_getHfpConnectedDeviceAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_getHfpCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_getHfpCurrentScoState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_isHfpVoiceControlOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_reqHfpDialCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_reqHfpReDial = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_reqHfpMemoryDial = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_reqHfpAnswerCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_reqHfpRejectIncomingCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
static final int TRANSACTION_reqHfpTerminateOngoingCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
static final int TRANSACTION_reqHfpMultiCallControl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
static final int TRANSACTION_reqHfpSendDtmf = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
static final int TRANSACTION_reqHfpAudioTransfer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
static final int TRANSACTION_setHfpLocalRingEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
static final int TRANSACTION_reqHfpRemoteDeviceInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
static final int TRANSACTION_getHfpRemoteDeviceName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 44);
static final int TRANSACTION_reqHfpRemoteDevicePhoneNumber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 45);
static final int TRANSACTION_reqHfpRemoteDeviceNetworkOperator = (android.os.IBinder.FIRST_CALL_TRANSACTION + 46);
static final int TRANSACTION_reqHfpRemoteDeviceSubscriberNumber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 47);
static final int TRANSACTION_isHfpRemoteDeviceTelecomServiceOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 48);
static final int TRANSACTION_isHfpRemoteDeviceOnRoaming = (android.os.IBinder.FIRST_CALL_TRANSACTION + 49);
static final int TRANSACTION_getHfpRemoteDeviceBatteryStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 50);
static final int TRANSACTION_getHfpRemoteDeviceSignalStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 51);
static final int TRANSACTION_setHfpAtNewSmsNotify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 52);
static final int TRANSACTION_reqHfpAtDownloadPhonebook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 53);
static final int TRANSACTION_reqHfpAtDownloadSMS = (android.os.IBinder.FIRST_CALL_TRANSACTION + 54);
static final int TRANSACTION_reqHfpRemoteDeviceVolumeSync = (android.os.IBinder.FIRST_CALL_TRANSACTION + 55);
static final int TRANSACTION_reqHfpVoiceDial = (android.os.IBinder.FIRST_CALL_TRANSACTION + 56);
static final int TRANSACTION_registerHspCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 57);
static final int TRANSACTION_unregisterHspCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 58);
static final int TRANSACTION_reqHspConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 59);
static final int TRANSACTION_reqHspDisconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 60);
static final int TRANSACTION_getHspConnectedDeviceAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 61);
static final int TRANSACTION_getHspCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 62);
static final int TRANSACTION_getHspCurrentScoState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 63);
static final int TRANSACTION_reqHspReDial = (android.os.IBinder.FIRST_CALL_TRANSACTION + 64);
static final int TRANSACTION_reqHspAnswerCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 65);
static final int TRANSACTION_reqHspRejectIncomingCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 66);
static final int TRANSACTION_reqHspTerminateOngoingCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 67);
static final int TRANSACTION_reqHspAudioTransfer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 68);
static final int TRANSACTION_reqHspRemoteDeviceVolumeSync = (android.os.IBinder.FIRST_CALL_TRANSACTION + 69);
static final int TRANSACTION_registerSppCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 70);
static final int TRANSACTION_unregisterSppCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 71);
static final int TRANSACTION_reqSppConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 72);
static final int TRANSACTION_reqSppDisconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 73);
static final int TRANSACTION_reqSppConnectedDeviceAddressList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 74);
static final int TRANSACTION_isSppConnected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 75);
static final int TRANSACTION_reqSppSendData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 76);
static final int TRANSACTION_registerPbapCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 77);
static final int TRANSACTION_unregisterPbapCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 78);
static final int TRANSACTION_reqPbapDownloadAllVcards = (android.os.IBinder.FIRST_CALL_TRANSACTION + 79);
static final int TRANSACTION_reqPbapDatabaseAvailable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 80);
static final int TRANSACTION_reqPbapQueryName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 81);
static final int TRANSACTION_reqPbapDeleteDatabaseByAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 82);
static final int TRANSACTION_reqPbapCleanDatabase = (android.os.IBinder.FIRST_CALL_TRANSACTION + 83);
static final int TRANSACTION_reqPbapDownloadInterrupt = (android.os.IBinder.FIRST_CALL_TRANSACTION + 84);
static final int TRANSACTION_getPbapCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 85);
static final int TRANSACTION_registerMapCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 86);
static final int TRANSACTION_unregisterMapCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 87);
static final int TRANSACTION_reqMapDownloadAllMessages = (android.os.IBinder.FIRST_CALL_TRANSACTION + 88);
static final int TRANSACTION_reqMapDownloadSingleMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 89);
static final int TRANSACTION_reqMapRegisterNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 90);
static final int TRANSACTION_reqMapUnregisterNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 91);
static final int TRANSACTION_isMapNotificationRegistered = (android.os.IBinder.FIRST_CALL_TRANSACTION + 92);
static final int TRANSACTION_reqMapDownloadInterrupt = (android.os.IBinder.FIRST_CALL_TRANSACTION + 93);
static final int TRANSACTION_reqMapDatabaseAvailable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 94);
static final int TRANSACTION_reqMapDeleteDatabaseByAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 95);
static final int TRANSACTION_reqMapCleanDatabase = (android.os.IBinder.FIRST_CALL_TRANSACTION + 96);
static final int TRANSACTION_getMapCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 97);
static final int TRANSACTION_getMapRegisterState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 98);
static final int TRANSACTION_registerA2dpAvrcpCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 99);
static final int TRANSACTION_unregisterA2dpAvrcpCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 100);
static final int TRANSACTION_reqA2dpConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 101);
static final int TRANSACTION_reqA2dpDisconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 102);
static final int TRANSACTION_getA2dpConnectedDeviceAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 103);
static final int TRANSACTION_getA2dpCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 104);
static final int TRANSACTION_getAvrcpCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 105);
static final int TRANSACTION_setA2dpLocalVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 106);
static final int TRANSACTION_setA2dpLocalMute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 107);
static final int TRANSACTION_getAvrcpConnectedDeviceAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 108);
static final int TRANSACTION_isAvrcp13Supported = (android.os.IBinder.FIRST_CALL_TRANSACTION + 109);
static final int TRANSACTION_isAvrcp14Supported = (android.os.IBinder.FIRST_CALL_TRANSACTION + 110);
static final int TRANSACTION_reqAvrcpPlay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 111);
static final int TRANSACTION_reqAvrcpStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 112);
static final int TRANSACTION_reqAvrcpPause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 113);
static final int TRANSACTION_reqAvrcpForward = (android.os.IBinder.FIRST_CALL_TRANSACTION + 114);
static final int TRANSACTION_reqAvrcpBackward = (android.os.IBinder.FIRST_CALL_TRANSACTION + 115);
static final int TRANSACTION_reqAvrcpVolumeUp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 116);
static final int TRANSACTION_reqAvrcpVolumeDown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 117);
static final int TRANSACTION_reqAvrcpStartFastForward = (android.os.IBinder.FIRST_CALL_TRANSACTION + 118);
static final int TRANSACTION_reqAvrcpStopFastForward = (android.os.IBinder.FIRST_CALL_TRANSACTION + 119);
static final int TRANSACTION_reqAvrcpStartRewind = (android.os.IBinder.FIRST_CALL_TRANSACTION + 120);
static final int TRANSACTION_reqAvrcpStopRewind = (android.os.IBinder.FIRST_CALL_TRANSACTION + 121);
static final int TRANSACTION_reqAvrcpGetCapabilitiesSupportEvent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 122);
static final int TRANSACTION_reqAvrcpGetPlayerSettingAttributesList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 123);
static final int TRANSACTION_reqAvrcpGetPlayerSettingValuesList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 124);
static final int TRANSACTION_reqAvrcpGetPlayerSettingCurrentValues = (android.os.IBinder.FIRST_CALL_TRANSACTION + 125);
static final int TRANSACTION_reqAvrcpSetPlayerSettingValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 126);
static final int TRANSACTION_reqAvrcpGetElementAttributesPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 127);
static final int TRANSACTION_reqAvrcpGetPlayStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 128);
static final int TRANSACTION_reqAvrcpRegisterEventWatcher = (android.os.IBinder.FIRST_CALL_TRANSACTION + 129);
static final int TRANSACTION_reqAvrcpUnregisterEventWatcher = (android.os.IBinder.FIRST_CALL_TRANSACTION + 130);
static final int TRANSACTION_reqAvrcpAbortMetadataReceiving = (android.os.IBinder.FIRST_CALL_TRANSACTION + 131);
static final int TRANSACTION_reqAvrcpNextGroup = (android.os.IBinder.FIRST_CALL_TRANSACTION + 132);
static final int TRANSACTION_reqAvrcpPreviousGroup = (android.os.IBinder.FIRST_CALL_TRANSACTION + 133);
static final int TRANSACTION_reqAvrcpSetMetadataCompanyId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 134);
static final int TRANSACTION_reqAvrcpGetPlayerSettingAttributeText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 135);
static final int TRANSACTION_reqAvrcpGetPlayerSettingValueText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 136);
static final int TRANSACTION_reqAvrcpInformDisplayableCharset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 137);
static final int TRANSACTION_reqAvrcpInformBatteryStatusOfCt = (android.os.IBinder.FIRST_CALL_TRANSACTION + 138);
static final int TRANSACTION_reqAvrcpSetAddressedPlayer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 139);
static final int TRANSACTION_reqAvrcpSetBrowsedPlayer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 140);
static final int TRANSACTION_reqAvrcpGetFolderItems = (android.os.IBinder.FIRST_CALL_TRANSACTION + 141);
static final int TRANSACTION_reqAvrcpChangePath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 142);
static final int TRANSACTION_reqAvrcpGetItemAttributes = (android.os.IBinder.FIRST_CALL_TRANSACTION + 143);
static final int TRANSACTION_reqAvrcpPlaySelectedItem = (android.os.IBinder.FIRST_CALL_TRANSACTION + 144);
static final int TRANSACTION_reqAvrcpSearch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 145);
static final int TRANSACTION_reqAvrcpAddToNowPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 146);
static final int TRANSACTION_isAvrcpBrowsingChannelEstablished = (android.os.IBinder.FIRST_CALL_TRANSACTION + 147);
static final int TRANSACTION_reqAvrcpSetAbsoluteVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 148);
static final int TRANSACTION_setA2dpNativeStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 149);
static final int TRANSACTION_setA2dpNativeStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 150);
static final int TRANSACTION_isA2dpNativeRunning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 151);
static final int TRANSACTION_registerPanCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 152);
static final int TRANSACTION_unregisterPanCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 153);
static final int TRANSACTION_reqPanConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 154);
static final int TRANSACTION_reqPanDisconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 155);
static final int TRANSACTION_getPanConnectedDeviceAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 156);
static final int TRANSACTION_getPanCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 157);
static final int TRANSACTION_registerHidCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 158);
static final int TRANSACTION_unregisterHidCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 159);
static final int TRANSACTION_reqHidConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 160);
static final int TRANSACTION_reqHidDisconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 161);
static final int TRANSACTION_sendHidMouseCommand = (android.os.IBinder.FIRST_CALL_TRANSACTION + 162);
static final int TRANSACTION_sendHidVirtualKeyCommand = (android.os.IBinder.FIRST_CALL_TRANSACTION + 163);
static final int TRANSACTION_getHidCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 164);
}
/** 
	 * Register callback functions for basic service functions.
	 * Call this function to register callback functions for nFore service.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean registerNforeServiceCallback(nfore.android.bt.servicemanager.IServiceCallback cb) throws android.os.RemoteException;
/** Remove callback functions for nFore service.
     * Call this function to remove previously registered callback interface for nFore service.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean unregisterNforeServiceCallback(nfore.android.bt.servicemanager.IServiceCallback cb) throws android.os.RemoteException;
/** 
	 * Request to enable or disable local Bluetooth adapter.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param isEnable : true to enable Bluetooth adapter or false to disable.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean setBluetoothEnable(boolean isEnable) throws android.os.RemoteException;
/** 
	 * Request to make local Bluetooth adapter discoverable for specific duration in seconds.
	 * If the duration is 0, the system default discoverable duration would be adopted.
	 * The system default duration for discoverable might differentiate from each other in different platforms.
	 * However, it is 120 seconds in general.
	 * If the duration is negative value, we would disable discoverable.	 
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param duration : the duration time in seconds to apply the discoverable mode. This value must be a positive value.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean setBluetoothDiscoverableDuration(int duration) throws android.os.RemoteException;
/** 
	 * Request to make local Bluetooth adapter discoverable permanently or not.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param isEnable : true to enable or false to disable.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean setBluetoothDiscoverablePermanent(boolean isEnable) throws android.os.RemoteException;
/** 
	 * Request to start or stop scanning process for remote devices.
	 * Client should not request to start scanning twice or more in 12 seconds.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IServiceCallback#onAdapterStateChanged onAdapterStateChanged} and {@link IServiceCallback#onDeviceFound onDeviceFound}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param isEnable : true to start or false to stop.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqBluetoothScanning(boolean isEnable) throws android.os.RemoteException;
/** 
	 * Request to pair with given Bluetooth hardware address.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IServiceCallback#onDevicePaired onDevicePaired} and {@link IServiceCallback#onDeviceUuidsGot onDeviceUuidsGot}
	 * to be notified if pairing is successful.	 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @attention : We only support 7 paired devices maximal. System would tear down the first paired device automatically when
	 * the limit is reached. 
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqBluetoothPair(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to unpair with given Bluetooth hardware address.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IServiceCallback#onDeviceUnpaired onDeviceUnpaired}
	 * to be notified if unpairing is successful.	 	 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * However, this operation only removes the pairing record locally. Remote device would not be notified or
	 * to become aware of this util it tries to connect to us or we initiate the pairing again next time.
	 * All connected or connecting profiles should be terminated before unpairing.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqBluetoothUnpair(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to remove all paired devices.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IServiceCallback#onDeviceUnpaired onDeviceUnpaired}
	 * to be notified if each unpairing is successful.	 	 
	 * However, this operation only removes the pairing record locally. Remote device would not be notified or
	 * to become aware of this util it tries to connect to us or we initiate the pairing again next time.
	 * All connected or connecting profiles should be terminated before unpairing.
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqBluetoothUnpairAll() throws android.os.RemoteException;
/** 
	 * Request to remove all paired devices on next time Bluetooth turning on.	 	 
	 * However, this operation only removes the pairing record locally. Remote device would not be notified or
	 * to become aware of this util it tries to connect to us or we initiate the pairing again next time.
	 * The operation of unpairing devices would be executed on next time Bluetooth is turned on and if this operation is done successfully,
	 * Bluetooth would be automatically turned off.
	 * The possible usage of this method might be in the scenario when system needs to be reset to default. 
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqBluetoothUnpairAllNextTime() throws android.os.RemoteException;
/** 
	 * Request to get the paired device list.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IServiceCallback#retBluetoothPairedDevices retBluetoothPairedDevices}
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqBluetoothPairedDevices() throws android.os.RemoteException;
/** 
	 * Request to get the name of local Bluetooth adapter.
	 * If there is an error, the string "UNKNOWN" would be returned.
	 *
	 * @return the String type name of local Bluetooth adapter.
	 */
public java.lang.String getBluetoothLocalAdapterName() throws android.os.RemoteException;
/** 
	 * Request to get the name of remote Bluetooth device with given address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This method would return the name announced by remote device in String type only if this remote device
	 * has been scanned before or otherwise the empty string would be returned.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.	 
	 * @return the real String type name of remote device or the empty string.
	 */
public java.lang.String getBluetoothRemoteDeviceName(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to get the address of local Bluetooth adapter.
	 *
	 * @return the String type address of local Bluetooth adapter.
	 */
public java.lang.String getBluetoothLocalAdapterAddress() throws android.os.RemoteException;
/** 
	 * Request to change the name of local Bluetooth adapter.
	 *
	 * @param name : the name to set.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean setBluetoothLocalAdapterName(java.lang.String name) throws android.os.RemoteException;
/** 
	 * Return true if Bluetooth is currently enabled and ready for use.
	 *
	 * @return true if the local adapter is turned on.
	 */
public boolean isBluetoothEnable() throws android.os.RemoteException;
public int getBluetoothCurrentState() throws android.os.RemoteException;
/** 
	 * Return true if Bluetooth is currently scanning remote devices.
	 *
	 * @return true if scanning.
	 */
public boolean isBluetoothScanning() throws android.os.RemoteException;
/** 
	 * Return true if Bluetooth is currently discoverable from remote devices.
	 *
	 * @return true if discoverable.
	 */
public boolean isBluetoothDiscoverable() throws android.os.RemoteException;
/** 
	 * Request to enable or disable auto-reconnect.
	 * The setting would persist until client requests again.
	 * Auto-reconnect only applies to HSP, HFP and A2DP profiles when out of range event takes place.
	 * Besides, only one of HSP or HFP would be re-connected, and HFP is preferred to HSP.
	 * Enabling auto-reconnect only applies to the following conditions, 
	 *		<p><blockquote>next out of range event,
	 *		<br>there is out of range event before system shutdown AND system is restarted, or 
	 *		<br>there is out of range event before Bluetooth turning-off AND Bluetooth is restarted</blockquote>
	 *
	 * @param isEnable : true to enable or false to disable.
	 */
public void setBluetoothAutoReconnect(boolean isEnable) throws android.os.RemoteException;
/** 
	 * Request to force out of range flags to be set or un-set.
	 * This request dose not enable or disable auto-reconnect automatically. It just sets OOR flags.
	 * {@link INforeService#setBluetoothAutoReconnect setBluetoothAutoReconnect} must be invocated before calling this API.
	 * Besides, if there is no connected profile when this API is invocated, this API would change nothing.
	 * This API is generally invocated before system shutdown or Bluetooth turning-off, and it would force our
	 * service to connect to the device, which is connected when this API is invocated, 
	 * when system is restarted or Bluetooth is turned on next time. 
	 *
	 * @param isEnable : true to enable or false to disable.
	 */
public void setBluetoothAutoReconnectForceOor(boolean isEnable) throws android.os.RemoteException;
/** 
	 * Return true if auto-reconnect is currently enabled.
	 *
	 * @return true if auto-reconnect is enabled.
	 */
public boolean isBluetoothAutoReconnectEnable() throws android.os.RemoteException;
public int reqBluetoothConnectAll(java.lang.String address) throws android.os.RemoteException;
public int reqBluetoothDisconnectAll() throws android.os.RemoteException;
public int reqBluetoothRemoteUuids(java.lang.String address) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * HFP
	 *//** 
	 * Register callback functions for HFP profile.
	 * Call this function to register callback functions for HFP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean registerHfpCallback(nfore.android.bt.servicemanager.IHfpCallback cb) throws android.os.RemoteException;
/** 
	 * Remove callback functions for HFP profile.
     * Call this function to remove previously registered callback interface for HFP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean unregisterHfpCallback(nfore.android.bt.servicemanager.IHfpCallback cb) throws android.os.RemoteException;
/** 
	 * Request to connect HFP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @attention : We only support one connection at this released version.
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqHfpConnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to disconnect the connected HFP connection to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqHfpDisconnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the hardware Bluetooth address of connected remote HFP device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected HFP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
public java.lang.String getHfpConnectedDeviceAddress() throws android.os.RemoteException;
public int getHfpCurrentState(java.lang.String address) throws android.os.RemoteException;
public int getHfpCurrentScoState(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the current voice control state of remote connected HFP device with given Bluetooth hardware address.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return voice control status (On/Off).
	 */
public boolean isHfpVoiceControlOn(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to dial the outgoing call with given phone number.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param callNumber : the outgoing call phone number.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqHfpDialCall(java.lang.String address, java.lang.String callNumber) throws android.os.RemoteException;
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to re-dial the last outgoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqHfpReDial(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the memory dialing.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @param memoryLocation : the memory location on mobile phone. Mobile phone would retrive phone number stored in this place and dial out.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqHfpMemoryDial(java.lang.String address, java.lang.String memoryLocation) throws android.os.RemoteException;
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to answer the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true if send command success
	 */
public boolean reqHfpAnswerCall(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to reject the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true if send command success
	 */
public boolean reqHfpRejectIncomingCall(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to terminate the ongoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true if send command success
	 */
public boolean reqHfpTerminateOngoingCall(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the multipaty calls control.
	 * This API should only been called when there are multiparty calls.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param opCode :
	 *		<p><blockquote>HFP_CHLD_RELEASE_ALL_HELD_CALLS				(byte) 0x00
	 *		<br>HFP_CHLD_SET_USER_BUSY_FOR_WAITING_CALL					(byte) 0x00
	 *		<br>HFP_CHLD_RELEASE_ALL_ACTIVE_CALLS_ACCEPT_THEOTHER		(byte) 0x01
	 *		<br>HFP_CHLD_HOLD_ALL_ACTIVE_CALLS_ACCEPT_THEOTHER			(byte) 0x02
	 *		<br>HFP_CHLD_ADD_HELD_CALL_TO_CONVERSATION					(byte) 0x03</blockquote>	
	 * @return true if send command success
	 */
public boolean reqHfpMultiCallControl(java.lang.String address, byte opCode) throws android.os.RemoteException;
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the DTMF.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Due to the compatibility, please request this API with single DTMF number each time.
	 * Avoid requesting with serial DTMF numbers. 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @param dtmfNumber : the DTMF number.
 	 * @return true if send command success
	 */
public boolean reqHfpSendDtmf(java.lang.String address, java.lang.String dtmfNumber) throws android.os.RemoteException;
/** 
	 * Request remote connected HFP device with given Bluetooth hardware address to do the SCO audio transfer.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged} to be notified of subsequent state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param opCode :
	 *		<p><blockquote>AUDIO_TRANSFER_TO_PHONE	(byte) 0x00
	 *		<br>AUDIO_TRANSFER_TO_CARKIT			(byte) 0x01</blockquote>
	 * @return true if send command success	 
	 */
public boolean reqHfpAudioTransfer(java.lang.String address, byte opCode) throws android.os.RemoteException;
/** 
	 * Request to enable or disable the local ringtone when there is an incoming call.
	 *
	 * @param isEnable
	 *<p><value>=true enable ring
	 *<p><value>=false disable ring
	 */
public void setHfpLocalRingEnable(boolean isEnable) throws android.os.RemoteException;
/** 
	 * Request information of remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#retHfpRemoteDeviceInfo retHfpRemoteDeviceInfo} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 */
public void reqHfpRemoteDeviceInfo(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the name of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This method would return the name announced in HFP by remote device in String type.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.	 
	 * @return the real String type name of remote device or the empty string.
	 */
public java.lang.String getHfpRemoteDeviceName(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the active or held phone number of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpRemoteDevicePhoneNumberChanged onHfpRemoteDevicePhoneNumberChanged} to be notified of subsequent result.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true if send command success
	 */
public boolean reqHfpRemoteDevicePhoneNumber(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the network operator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#retHfpRemoteDeviceNetworkOperator retHfpRemoteDeviceNetworkOperator} to be notified of subsequent result.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true if send command success
	 */
public boolean reqHfpRemoteDeviceNetworkOperator(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the subscriber number information of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#retHfpRemoteDeviceSubscriberNumber retHfpRemoteDeviceSubscriberNumber} to be notified of subsequent result.
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true if send command success
	 */
public boolean reqHfpRemoteDeviceSubscriberNumber(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the status of telecom service indicator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate remote device has telecom service.	 
	 */
public boolean isHfpRemoteDeviceTelecomServiceOn(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the status of roaming indicator of connected remote HFP device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *	 	 
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate roaming is active on remote device.	 
	 */
public boolean isHfpRemoteDeviceOnRoaming(java.lang.String address) throws android.os.RemoteException;
public int getHfpRemoteDeviceBatteryStatus(java.lang.String address) throws android.os.RemoteException;
public int getHfpRemoteDeviceSignalStatus(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to enable or disable new SMS notification by AT command interface.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.	 
	 * @param isEnable : true to enable or false.
	 */
public void setHfpAtNewSmsNotify(java.lang.String address, boolean isEnable) throws android.os.RemoteException;
/**
	 * Request to download phone book by AT command interface from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpAtDownloadPhonebookStateChanged onHfpAtDownloadPhonebookStateChanged} to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.	
	 * @param storage :
	 *		<p><blockquote>PBAP_STORAGE_SIM		(int) 1
	 *		<br>PBAP_STORAGE_PHONE_MEMORY		(int) 2
	 *		<br>PBAP_STORAGE_DIALED_CALLS		(int) 3
	 *		<br>PBAP_STORAGE_MISSED_CALLS		(int) 4
	 *		<br>PBAP_STORAGE_RECEIVED_CALLS		(int) 5</blockquote>	
	 * @return true if send command success	 
	 */
public boolean reqHfpAtDownloadPhonebook(java.lang.String address, int storage) throws android.os.RemoteException;
/**
	 * Request to download SMS by AT command interface from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHfpCallback#onHfpAtDownloadSMSStateChanged onHfpAtDownloadSMSStateChanged} to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.	
	 * @return true if send command success	 
	 */
public boolean reqHfpAtDownloadSMS(java.lang.String address) throws android.os.RemoteException;
/**
	 * Request to inform remote connected HFP device with given Bluetooth hardware address the current gain settings
	 * of local speaker or microphone.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#setHFPSpeakVolState setHFPSpeakVolState} and {@link IHfpCallback#setHFPVolState setHFPVolState}
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.
	 * @param attributeId :
	 *		<p><blockquote>VOLUME_ATTRIBUTE_SPEAKER	(byte) 0x01
	 *		<br>VOLUME_ATTRIBUTE_MIC				(byte) 0x02</blockquote>	 
	 * @param volume : sync volume
	 * @return true if send command success
	 */
public boolean reqHfpRemoteDeviceVolumeSync(java.lang.String address, byte attributeId, int volume) throws android.os.RemoteException;
/**
	 * Request remote connected HFP device with given Bluetooth hardware address to do the voice dialing.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHfpCallback#onHfpStateChanged onHfpStateChanged} and {@link IHfpCallback#onHfpScoStateChanged onHfpScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!		 
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @param isEnable : true to start the voice dialing.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqHfpVoiceDial(java.lang.String address, boolean isEnable) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * HSP
	 *//** 
	 * Register callback functions for HSP profile.
	 * Call this function to register callback functions for HSP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean registerHspCallback(nfore.android.bt.servicemanager.IHspCallback cb) throws android.os.RemoteException;
/** 
	 * Remove callback functions for HSP profile.
     * Call this function to remove previously registered callback interface for HSP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean unregisterHspCallback(nfore.android.bt.servicemanager.IHspCallback cb) throws android.os.RemoteException;
/** 
	 * Request to connect HSP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @attention : We only support one connection at this released version.
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean reqHspConnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to disconnect the connected HSP connection to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean reqHspDisconnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the hardware Bluetooth address of connected remote HSP device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected HSP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
public java.lang.String getHspConnectedDeviceAddress() throws android.os.RemoteException;
public int getHspCurrentState(java.lang.String address) throws android.os.RemoteException;
public int getHspCurrentScoState(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to re-dial the last outgoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean reqHspReDial(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to answer the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
 	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqHspAnswerCall(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to reject the incoming call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!! By the definition of HSP spec, 
	 * HS could not request this operation. Due to this reason, please avoid to use this API.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
public boolean reqHspRejectIncomingCall(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to terminate the ongoing call.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
public boolean reqHspTerminateOngoingCall(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected HSP device with given Bluetooth hardware address to do the SCO audio transfer.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspStateChanged onHspStateChanged} and {@link IHspCallback#onHspScoStateChanged onHspScoStateChanged}	 
	 * to be notified of subsequent state changes.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!! By the definition of HSP spec, 
	 * HS could only request to transfer audio connection from AG to HS. If we request to transfer audio connection from HS to AG,
	 * the overall connection might be released by AG, and the ongoing call might be terminated. 
	 * Due to this reason, please avoid to use this API.
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param opCode :
	 *		<p><blockquote>AUDIO_TRANSFER_TO_PHONE	(byte) 0x00
	 *		<br>AUDIO_TRANSFER_TO_CARKIT			(byte) 0x01</blockquote>	 
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
public boolean reqHspAudioTransfer(java.lang.String address, byte opCode) throws android.os.RemoteException;
/**
	 * Request to inform remote connected HSP device with given Bluetooth hardware address the current gain settings
	 * of local speaker or microphone.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IHspCallback#onHspSpeakerVolumeChanged onHspSpeakerVolumeChanged} and {@link HSPCallback#onHspMicVolumeChanged onHspMicVolumeChanged}
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!	 
	 * @param address : valid Bluetooth MAC address.
	 * @param attributeId :
	 *		<p><blockquote>VOLUME_ATTRIBUTE_SPEAKER	(byte) 0x01
	 *		<br>VOLUME_ATTRIBUTE_MIC				(byte) 0x02</blockquote>	 
	 * @param volume : sync volume
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
public boolean reqHspRemoteDeviceVolumeSync(java.lang.String address, byte attributeId, int volume) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * SPP
	 *//** 
	 * Register callback functions for SPP profile.
	 * Call this function to register callback functions for SPP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean registerSppCallback(nfore.android.bt.servicemanager.ISppCallback cb) throws android.os.RemoteException;
/** 
	 * Remove callback functions for SPP profile.
     * Call this function to remove previously registered callback interface for SPP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean unregisterSppCallback(nfore.android.bt.servicemanager.ISppCallback cb) throws android.os.RemoteException;
/** 
	 * Request to connect SPP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link ISppCallback#onSppStateChanged onSppStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqSppConnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to disconnect the connected SPP connection to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link ISppCallback#onSppStateChanged onSppStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqSppDisconnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the hardware Bluetooth address of connected remote SPP devices.
	 * For example, "00:11:22:AA:BB:CC".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link ISppCallback#retSppConnectedDeviceAddressList retSppConnectedDeviceAddressList} to be notified of subsequent result.
	 */
public void reqSppConnectedDeviceAddressList() throws android.os.RemoteException;
/** 
	 * Return true if device with given address is currently connected.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 * @return true if connected.
	 */
public boolean isSppConnected(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to send given data to remote connected SPP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Data size should not be greater than 512 bytes each time. 
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param sppData : the data to be sent.
	 */
public void reqSppSendData(java.lang.String address, byte[] sppData) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * PBAP
	 *//** 
	 * Register callback functions for PBAP profile.
	 * Call this function to register callback functions for PBAP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean registerPbapCallback(nfore.android.bt.servicemanager.IPbapCallback cb) throws android.os.RemoteException;
/** 
	 * Remove callback functions for PBAP profile.
     * Call this function to remove previously registered callback interface for PBAP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean unregisterPbapCallback(nfore.android.bt.servicemanager.IPbapCallback cb) throws android.os.RemoteException;
/**
	 * Request to download phone book by Vcard from remote connected HFP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link IPbapCallback#onPbapStateChanged onPbapStateChanged} and {@link IPbapCallback#onPbapDownloadNotify onPbapDownloadNotify} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.	
	 * @param storage :
	 *		<p><blockquote>PBAP_STORAGE_SIM		(int) 1
	 *		<br>PBAP_STORAGE_PHONE_MEMORY		(int) 2
	 *		<br>PBAP_STORAGE_DIALED_CALLS		(int) 3
	 *		<br>PBAP_STORAGE_MISSED_CALLS		(int) 4
	 *		<br>PBAP_STORAGE_RECEIVED_CALLS		(int) 5</blockquote>	
	 * @param notifyFreq : define the callback frequency.
	 * @param isUseContactsProvider : true is use Contacts Provider , or false is use nFore Database
	 *<p><value>=0 all vcards would be downloaded first, and inserted to database. Only one callback would be invocated.
	 *<p><value>>0 Callbacks would be invocated every "notifyFreq" vcards have been downloaded and inserted to database. 
	 * @return true to indicate the operation has been done without error, or false on immediate error or system is busy.	 
	 */
public boolean reqPbapDownloadAllVcards(java.lang.String address, int storage, int notifyFreq, boolean isUseContactsProvider) throws android.os.RemoteException;
/*
	 * Request to check if database is available for query.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapDatabaseAvailable retPbapDatabaseAvailable} to be notified when database is available.
	 *
	 * @param address : valid Bluetooth MAC address.	    
	 */
public void reqPbapDatabaseAvailable(java.lang.String address) throws android.os.RemoteException;
/**
	 * Request to query database the corresponding name of given phone number.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapQueryName retPbapQueryName} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.
     * @param phoneNumber : the phone number given to database query.
     */
public void reqPbapQueryName(java.lang.String address, java.lang.String phoneNumber) throws android.os.RemoteException;
/**
	 * Request to delete data by specific address from database of PBAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapDeleteDatabaseByAddressCompleted retPbapDeleteDatabaseByAddressCompleted} to be notified when database has been deleted.	 
	 *
	 * @param address : valid Bluetooth MAC address.
	 */
public void reqPbapDeleteDatabaseByAddress(java.lang.String address) throws android.os.RemoteException;
/**
	 * Request to clean database of PBAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IPbapCallback#retPbapCleanDatabaseCompleted retPbapCleanDatabaseCompleted} to be notified when database has been cleaned.	 
	 */
public void reqPbapCleanDatabase() throws android.os.RemoteException;
/**
	 * Request to interrupt the ongoing download on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Clients should register and implement callback function {@link IPbapCallback#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.    
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true if really try to interrupt.
	 */
public boolean reqPbapDownloadInterrupt(java.lang.String address) throws android.os.RemoteException;
public int getPbapCurrentState(java.lang.String address) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * MAP
	 *//** 
	 * Register callback functions for MAP profile.
	 * Call this function to register callback functions for MAP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean registerMapCallback(nfore.android.bt.servicemanager.IMapCallback cb) throws android.os.RemoteException;
/** 
	 * Remove callback functions for MAP profile.
     * Call this function to remove previously registered callback interface for MAP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean unregisterMapCallback(nfore.android.bt.servicemanager.IMapCallback cb) throws android.os.RemoteException;
/**
	 * Request to download all messages from remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IMapCallback#onMapStateChanged onMapStateChanged} to be notified of subsequent result.
	 * Besides, clients should also register and implement callback function
	 * {@link IMapCallback#onMapDownloadOutlineCompleted onMapDownloadOutlineCompleted} to be notified of subsequent result if
	 * parameter isContentDownload is set to false.	 
	 *
	 * @param address : valid Bluetooth MAC address.	
	 * @param folder :
	 *		<p><blockquote>MAP_INBOX_OUTBOX		(int) 0
	 *		<br>MAP_INBOX_ONLY					(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>	
	 * @param notifyFreq : define the callback frequency.
	 *<p><value>=0 all messages would be downloaded first, and inserted to database. Only one callback would be invocated.
	 *<p><value>>0 Callbacks would be invocated every "notifyFreq" messages have been downloaded and inserted to database. 
	 * @param isContentDownload :
	 * <p><value>=false, download outline only
	 * <p><value>=true, download all messages including the contents, but this will let all message set to read status
	 * @return true to indicate the operation has been done without error, or false on immediate error.	 
	 */
public boolean reqMapDownloadAllMessages(java.lang.String address, int folder, int notifyFreq, boolean isContentDownload) throws android.os.RemoteException;
/**
	 * Request to download single message from remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IMapCallback#onMapStateChanged onMapStateChanged} to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address.	
	 * @param folder :
	 *		<p><blockquote>MAP_INBOX_ONLY		(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>
	 * @param handle : MAP handle
	 * @return true to indicate the operation has been done without error, or false on immediate error.		 
	 */
public boolean reqMapDownloadSingleMessage(java.lang.String address, int folder, java.lang.String handle) throws android.os.RemoteException;
/**
	 * Request to register notification when there is new message on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Clients should register and implement callback function {@link IMapCallback#onMapNotificationStateChanged onMapNotificationStateChanged} and
	 * {@link IMapCallback#onMapNewMessageReceived onMapNewMessageReceived}
	 * to be notified of subsequent result.    
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @param downloadNewMessage : if true, download the new message, too
	 */
public boolean reqMapRegisterNotification(java.lang.String address, boolean downloadNewMessage) throws android.os.RemoteException;
/**
	 * Request to unregister new message notification on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB". 
	 *
	 * @param address : valid Bluetooth MAC address.
	 */
public void reqMapUnregisterNotification(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Return true if the new message notification is registered on device with given address.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 * @return true if registered.
	 */
public boolean isMapNotificationRegistered(java.lang.String address) throws android.os.RemoteException;
/**
	 * Request to interrupt the ongoing download on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * Clients should register and implement callback function {@link IMapCallback#onMapStateChanged onMapStateChanged} to be notified of subsequent result.    
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true if really try to interrupt.
	 */
public boolean reqMapDownloadInterrupt(java.lang.String address) throws android.os.RemoteException;
/*
	 * Request to check if database is available for query
	 * Client should register and implement {@link IMapCallback#retMapDatabaseAvailable retMapDatabaseAvailable} 
	 * to be notified when database is available.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 */
public void reqMapDatabaseAvailable(java.lang.String address) throws android.os.RemoteException;
/**
	 * Request to delete data by specific address
	 * Client should register and implement {@link IMapCallback#retMapDeleteDatabaseByAddressCompleted retMapDeleteDatabaseByAddressCompleted} 
	 * to be notified when database is available.
	 *
	 * @param address : valid Bluetooth MAC address.	 
	 */
public void reqMapDeleteDatabaseByAddress(java.lang.String address) throws android.os.RemoteException;
/**
	 * Request to clean database of MAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link IMapCallback#retMapCleanDatabaseCompleted retMapCleanDatabaseCompleted} to be notified when database has been cleaned.	 
	 */
public void reqMapCleanDatabase() throws android.os.RemoteException;
public int getMapCurrentState(java.lang.String address) throws android.os.RemoteException;
public int getMapRegisterState(java.lang.String address) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * A2DP AVRCP
	 *//** 
	 * Register callback functions for A2DP and AVRCP profile.
	 * Call this function to register callback functions for A2DP and AVRCP profile.
	 * Allow nFore service to call back to its registered clients, which is often UI.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean registerA2dpAvrcpCallback(nfore.android.bt.servicemanager.IA2dpCallback cb) throws android.os.RemoteException;
/** 
	 * Remove callback functions for A2DP and AVRCP profile.
     * Call this function to remove previously registered callback interface for A2DP and AVRCP profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean unregisterA2dpAvrcpCallback(nfore.android.bt.servicemanager.IA2dpCallback cb) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * A2DP v1.0 v1.2
	 *//** 
	 * Request to connect A2DP and AVRCP to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} and {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both A2DP and AVRCP will be connected and the sequence of state changed callback of profiles! 
	 * This depends on the behavior of connected device.
	 *
	 * @attention : We only support one connection of all and restrict A2DP and AVRCP should be the same remote device at this released version.
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqA2dpConnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to disconnect the connected A2DP and AVRCP connections to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} and {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both A2DP and AVRCP will be disconnected and the sequence of state changed callback of profiles! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqA2dpDisconnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the hardware Bluetooth address of connected remote A2DP/AVRCP device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected A2DP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
public java.lang.String getA2dpConnectedDeviceAddress() throws android.os.RemoteException;
public int getA2dpCurrentState() throws android.os.RemoteException;
public int getAvrcpCurrentState() throws android.os.RemoteException;
/** 
	 * Request to set the volume of A2DP streaming music locally.
	 * Volume level is indicated by the unit of millibels. 
	 * Besides, due to the limit of system, this API could only lower down the volume, 
	 * which is done by giving negative value to parameter vol, and set volume back to the original level,
	 * which is done by giving zero to parameter vol.
	 * If the player is muted, which is done by calling {@link INforeService#setA2dpLocalMute setA2dpLocalMute}, 
	 * calls to this API will still change the internal volume level, but this will have no audible effect until
	 * the player is unmuted.
	 * Never provide the positive value to parameter vol.
	 * This is an asynchronous call: it will return immediately. 
	 *
	 * @param vol : volume level to set. The possible values are from -32768 (-0x7FFF-1) to 0 (0x00).
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean setA2dpLocalVolume(int vol) throws android.os.RemoteException;
/** 
	 * Request to mute the A2DP streaming music locally.
	 * If the player is muted by calling this API, calls to {@link INforeService#setA2dpLocalVolume setA2dpLocalVolume} will still change the internal volume level, 
	 * but this will have no audible effect until the player is unmuted by calling this API again.
	 * This is an asynchronous call: it will return immediately. 
	 *
	 * @param isMute : boolean indicating whether to mute or unmute.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean setA2dpLocalMute(boolean isMute) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * AVRCP v1.0
	 *//** 
	 * Request for the hardware Bluetooth address of connected remote AVRCP device.
	 * There might be no need for users to concern for this in general case. Users could call 
	 * {@link INforeService#getA2dpConnectedDeviceAddress getA2dpConnectedDeviceAddress} to get the connected A2DP/AVRCP device address.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected AVRCP device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
public java.lang.String getAvrcpConnectedDeviceAddress() throws android.os.RemoteException;
/** 
	 * Request if AVRCP 1.3 is supported by remote device with given Bluetooth hardware address.
	 * The requested address must be the paired device and is connected currently.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : valid Bluetooth MAC address of paired and connected AVRCP device.
	 * @return false if the device dose not support version 1.3 or is not connected currently.  
	 */
public boolean isAvrcp13Supported(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request if AVRCP 1.4 is supported by remote device with given Bluetooth hardware address.
	 * The requested address must be the paired device and is connected currently.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : valid Bluetooth MAC address of paired and connected AVRCP device.
	 * @return false if the device dose not support version 1.4 or is not connected currently.  
	 */
public boolean isAvrcp14Supported(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Play" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpPlay(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Stop" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpStop(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Pause" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpPause(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Forward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpForward(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Backward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpBackward(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Volume Up" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpVolumeUp(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to do the "Volume Down" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpVolumeDown(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to start the "Fast Forward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpStartFastForward(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to stop the "Fast Forward" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpStopFastForward(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to start the "Rewind" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpStartRewind(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to stop the "Rewind" operation.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpStopRewind(java.lang.String address) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * AVRCP v1.3
	 *//** 
	 * Request to get the supported events of capabilities supported by remote connected A2DP/AVRCP device 
	 * with given Bluetooth hardware address. This is sent by CT to inquire capabilities of the peer device.
	 * This requests the list of events supported by the remote device. Remote device is expected to respond with all the events supported 
	 * including the mandatory events defined in the AVRCP v1.3 specification. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpCapabilitiesSupportEvent retAvrcpCapabilitiesSupportEvent} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetCapabilitiesSupportEvent(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to get the supported player application setting attributes provided by remote connected A2DP/AVRCP device 
	 * with given Bluetooth hardware address. 
	 * The list of reserved player application setting attributes is provided in Appendix F of AVRCP v1.3 specification. 
	 * It is expected that a target device may have additional attributes not defined as part of the specification.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingAttributesList retAvrcpPlayerSettingAttributesList} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetPlayerSettingAttributesList(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to get the set of possible values for the requested player application setting attribute 
	 * provided by remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * The list of reserved player application setting attributes and their values are provided in Appendix F of AVRCP v1.3 specification. 
	 * It is expected that a target device may have additional attribute values not defined as part of the specification.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingValuesList retAvrcpPlayerSettingValuesList} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetPlayerSettingValuesList(java.lang.String address, byte attributeId) throws android.os.RemoteException;
/** 
	 * Request to get the current set values on the remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * for the provided player application setting attribute. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingCurrentValues retAvrcpPlayerSettingCurrentValues} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetPlayerSettingCurrentValues(java.lang.String address, byte attributeId) throws android.os.RemoteException;
/** 
	 * Request to set the player application setting of player application setting value on the remote connected A2DP/AVRCP device 
	 * with given Bluetooth hardware address for the corresponding defined PlayerApplicationSettingAttribute. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingValueSet retAvrcpPlayerSettingValueSet} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @param valueId : the setting value. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpSetPlayerSettingValue(java.lang.String address, byte attributeId, byte valueId) throws android.os.RemoteException;
/** 
	 * Request to get the attributes of the element specified in the parameter 
	 * provided by remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpElementAttributesPlaying retAvrcpElementAttributesPlaying} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetElementAttributesPlaying(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to get the status of the currently playing media at 
	 * remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayStatus retAvrcpPlayStatus} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.	 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetPlayStatus(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to register with remote connected A2DP/AVRCP device with given Bluetooth hardware address
	 * to receive notifications asynchronously based on specific events occurring. 
	 * The events registered would be kept on remote device until another
	 * {@link INforeService#reqAvrcpUnregisterEventWatcher reqAvrcpUnregisterEventWatcher} is called.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * <p><blockquote>{@link IA2dpCallback#onAvrcpEventPlaybackStatusChanged onAvrcpEventPlaybackStatusChanged},
	 * <br>{@link IA2dpCallback#onAvrcpEventTrackChanged onAvrcpEventTrackChanged},
	 * <br>{@link IA2dpCallback#onAvrcpEventTrackReachedEnd onAvrcpEventTrackReachedEnd},
	 * <br>{@link IA2dpCallback#onAvrcpEventTrackReachedStart onAvrcpEventTrackReachedStart},
	 * <br>{@link IA2dpCallback#onAvrcpEventPlaybackPosChanged onAvrcpEventPlaybackPosChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventBatteryStatusChanged onAvrcpEventBatteryStatusChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventSystemStatusChanged onAvrcpEventSystemStatusChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventPlayerSettingChanged onAvrcpEventPlayerSettingChanged},
 	 * <br>v1.4
 	 * <br>{@link IA2dpCallback#onAvrcpEventNowPlayingContentChanged onAvrcpEventNowPlayingContentChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventAvailablePlayerChanged onAvrcpEventAvailablePlayerChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventAddressedPlayerChanged onAvrcpEventAddressedPlayerChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventUidsChanged onAvrcpEventUidsChanged},
 	 * <br>{@link IA2dpCallback#onAvrcpEventVolumeChanged onAvrcpEventVolumeChanged}, and
	 * <br>{@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} </blockquote>
	 * to be notified of subsequent result.
	 * Each corresponding callback would be invoked once immediately after the event has been registered successfully. 
	 *
	 * @attention : Although we implement all these events required by specification, 
	 * we STRONGLY suggest that please AVOID using the following three events :
	 *		<p><blockquote>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07	 
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d	</blockquote>	 
	 *  
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param eventId : the registering event ID. Possible values are
	 *		<p><blockquote>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED				(byte) 0x01
	 *		<br>AVRCP_EVENT_ID_TRACK_CHANGED						(byte) 0x02
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_END					(byte) 0x03
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_START					(byte) 0x04
	 *		<br>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED					(byte) 0x05
	 *		<br>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07
	 *		<br>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED	(byte) 0x08
	 *		<br>v1.4
	 *		<br>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED			(byte) 0x09
	 *		<br>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED			(byte) 0x0a
	 *		<br>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED				(byte) 0x0b
	 *		<br>AVRCP_EVENT_ID_UIDS_CHANGED							(byte) 0x0c
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d</blockquote>		
	 * @param interval : the update interval in second. This parameter applicable only for
	 * AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED. For other events, this parameter is ignored !
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpRegisterEventWatcher(java.lang.String address, byte eventId, long interval) throws android.os.RemoteException;
/** 
	 * Request to unregister the specific events with remote connected A2DP/AVRCP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param eventId : the unregistering event ID. Possible values are
	 *		<p><blockquote>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED				(byte) 0x01
	 *		<br>AVRCP_EVENT_ID_TRACK_CHANGED						(byte) 0x02
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_END					(byte) 0x03
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_START					(byte) 0x04
	 *		<br>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED					(byte) 0x05
	 *		<br>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07
	 *		<br>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED	(byte) 0x08
	 *		<br>v1.4	 
	 *		<br>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED			(byte) 0x09
	 *		<br>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED			(byte) 0x0a
	 *		<br>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED				(byte) 0x0b
	 *		<br>AVRCP_EVENT_ID_UIDS_CHANGED							(byte) 0x0c
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d</blockquote>		
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpUnregisterEventWatcher(java.lang.String address, byte eventId) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to abort the continuing response.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpMetadataReceivingAborted retAvrcpMetadataReceivingAborted} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpAbortMetadataReceiving(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to move to the first song in the next group.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpNextGroup(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to move to the first song in the previous group.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately.
	 *
	 * @attention : There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpPreviousGroup(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to set the company ID on the remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.	
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param companyId : the setting company ID. Company-ID is 24bit unsigned value, initialized to Vendor_Unique (0x001958) when
	 * called on Connect().
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpSetMetadataCompanyId(java.lang.String address, int companyId) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to get 
	 * the supported player application setting attributes displayable text for the provided PlayerApplicationSettingAttributeID 
	 * This command is expected to be used only for extended attributes for menu navigation. 
	 * It is assumed that all <attribute, value> pairs used for menu extensions are statically defined by remote device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingAttributeText retAvrcpPlayerSettingAttributeText} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.		 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetPlayerSettingAttributeText(java.lang.String address, byte attributeId) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address to get 
	 * the supported player application setting value displayable text for the provided player application setting attribute value. 
	 * This command is expected to be used only for extended attributes for menu navigation. 
	 * It is assumed that all <attribute, value> pairs used for menu extensions are statically defined by remote device.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPlayerSettingValueText retAvrcpPlayerSettingValueText} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.		 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param attributeId : the requesting attribute ID. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @param valueId : the requesting value. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetPlayerSettingValueText(java.lang.String address, byte attributeId, byte valueId) throws android.os.RemoteException;
/** 
	 * Request to informs the character set we supported to remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * This shall allow remote device to send responses with strings in the character set we supported.
	 * By default remote device shall send strings in UTF-8 if this command has not been sent.
	 * It is mandatory to send UTF-8 as one of the supported character set.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpDisplayableCharsetInformed retAvrcpDisplayableCharsetInformed} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices. AVRCP's default charaset is UTF-8, so don't change the character set used by this command.	 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param charsetId : the supported charset IDs.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpInformDisplayableCharset(java.lang.String address, int[] charsetId) throws android.os.RemoteException;
/** 
	 * Request to informs the loacl battery status changed to remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpBatteryStatusOfCtInformed retAvrcpBatteryStatusOfCtInformed} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @warning : RESERVED !!! There is NO guarantee that this API would be supported by remote device and the corresponding result !!! 
	 * It all depends on devices.		 
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param batteryStatusId : the battery status. Possible values are
	 *		<p><blockquote>AVRCP_BATTERY_STATUS_ID_NORMAL		(byte) 0x00
	 *		<br>AVRCP_BATTERY_STATUS_ID_WARNING		(byte) 0x01
	 *		<br>AVRCP_BATTERY_STATUS_ID_CRITICAL	(byte) 0x02
	 * 		<br>AVRCP_BATTERY_STATUS_ID_EXTERNAL	(byte) 0x03
	 * 		<br>AVRCP_BATTERY_STATUS_ID_FULL		(byte) 0x04</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpInformBatteryStatusOfCt(java.lang.String address, byte batteryStatusId) throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * AVRCP v1.4
	 *//** 
	 * Request to inform the remote connected A2DP/AVRCP device with given Bluetooth hardware address 
	 * of which media player we wishes to control.
	 * The player is selected by its "Player Id".
	 * When the addressed player is changed, whether locally on the remote device or explicitly by us, 
	 * the remote device shall complete notifications following the mechanism described in section 6.9.2 of AVRCP v1.4 specification. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpAddressedPlayerSet retAvrcpAddressedPlayerSet} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param playerId : the selected player ID of 2 octets. 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpSetAddressedPlayer(java.lang.String address, int playerId) throws android.os.RemoteException;
/** 
	 * Request the remote connected A2DP/AVRCP device with given Bluetooth hardware address to control to which player browsing commands should be routed.
	 * The player to which AVRCP shall route browsing commands is the browsed player. 
	 * This command shall be sent successfully before any other commands are sent on the browsing channel except 
	 * {@link INforeService#reqAvrcpGetFolderItems reqAvrcpGetFolderItems}
	 * in the Media Player List scope. 
	 * If the browsed player has become unavailable this command shall be sent successfully again before further commands are sent on the browsing channel. 
	 * Some players may support browsing only when set as the Addressed Player.
	 * The player is selected by its "Player Id".
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpBrowsedPlayerSet retAvrcpBrowsedPlayerSet} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param playerId : the selected player ID of 2 octets. 
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpSetBrowsedPlayer(java.lang.String address, int playerId) throws android.os.RemoteException;
/** 
	 * Request to retrieve a listing of the contents of a folder on remote connected A2DP/AVRCP device with given Bluetooth hardware address.
	 * The folder is the representation of available media players, virtual file system, the last searching result, or the playing list.
	 * Should not issue this command to an empty folder.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpFolderItems retAvrcpFolderItems} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : the requesting folder ID. Possible values are
	 * 		<p><blockquote>AVRCP_SCOPE_ID_MEDIA_PLAYER	(byte) 0x00
	 *		<br>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetFolderItems(java.lang.String address, byte scopeId) throws android.os.RemoteException;
/** 
	 * Request to navigate the virtual filesystem on remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * This command allows us to navigate one level up or down in the virtual filesystem.
	 * Uid counters parameter is used to make sure that our uid cache is consistent with what remote device has currently. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpPathChanged retAvrcpPathChanged} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the folder to navigate to. This may be retrieved via a 
	 * {@link INforeService#reqAvrcpGetFolderItems reqAvrcpGetFolderItems} command. 
	 * If the navigation command is "Folder Up" this field is reserved..	 
	 * @param direction : the requesting operation on selested UID. Possible values are
	 * 		<p><blockquote>AVRCP_FOLDER_DIRECTION_ID_UP	(byte) 0x00
	 *		<br>AVRCP_FOLDER_DIRECTION_ID_DOWN	(byte) 0x01</blockquote>
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpChangePath(java.lang.String address, int uidCounter, long uid, byte direction) throws android.os.RemoteException;
/** 
	 * Request to retrieve the metadata attributes for a particular media element item or folder item 
	 * on remote connected A2DP/AVRCP device with given Bluetooth hardware address. 
	 * When the remote device supports this command, we shall use this command and not {@link INforeService#reqAvrcpGetElementAttributesPlaying reqAvrcpGetElementAttributesPlaying}. 
	 * To retrieve the Metadata for the currently playing track we should register to receive Track Changed Notifications. 
	 * This shall then provide the UID of the currently playing track, which can be used in the scope of the Now Playing folder.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpItemAttributes retAvrcpItemAttributes} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : the requesting folder ID. Possible values are
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the media element item or folder item to return the attributes for. UID 0 shall not be used.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpGetItemAttributes(java.lang.String address, byte scopeId, int uidCounter, long uid) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address 
	 * to starts playing an item indicated by the UID. It is routed to the Addressed Player. 
	 * If a UID changed event has happened on the remote device, but not yet received by us, we may send this command with the previous UID counter. 
	 * In this case a failure status shall be returned.
	 * Request this command with the scope parameter of 
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01 or 
	 * 		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02</blockquote>
	 * shall result in the NowPlaying folder being invalidated. The old content may not be valid any more or may contain additional items. 
	 * What is put in the NowPlaying folder depends on both the media player and its state, however the item selected by us shall be included.
	 * Request this command with the scope parameter of 
	 * 		<p><blockquote>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * should not change the contents of the NowPlaying Folder, the only effect is that the new item is played.
	 * Never request this command with the scope parameter 
	 * 		<p><blockquote>AVRCP_SCOPE_ID_MEDIA_PLAYER	(byte) 0x00.</blockquote>
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpSelectedItemPlayed retAvrcpSelectedItemPlayed} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : The scope in which the UID of the media element item or folder item, if supported, is valid. Possible values are
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the media element item or folder item, if supported, to be played.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpPlaySelectedItem(java.lang.String address, byte scopeId, int uidCounter, long uid) throws android.os.RemoteException;
/** 
	 * Request to perform the basic search from the current folder and all folders below that in the Browsed Players virtual filesystem. 
	 * Regular expressions shall not be supported. 
	 * Search results are valid until
	 * 		<p><blockquote>Another search request is performed or
	 *		<br>A UIDs changed notification response is received
	 * 		<br>The Browsed player is changed</blockquote>
	 * The search result would contain only media element items, not folder items.
	 * Searching may not be supported by all players. Furthermore, searching may only be possible on some players 
	 * when they are set as the Addressed Player.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpSearchResult retAvrcpSearchResult} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param text : The string to search on in the specified character set.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpSearch(java.lang.String address, java.lang.String text) throws android.os.RemoteException;
/** 
	 * Request remote connected A2DP/AVRCP device with given Bluetooth hardware address 
	 * to adds an item indicated by the UID to the NowPlaying queue. 
	 * If a UID changed event has happened on the remote device, but not yet received by us, we may send this command with the previous UID counter. 
	 * In this case a failure status shall be returned.
	 * Request this command with the scope parameter of 
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 * 		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02 or
	 * 		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * shall result in the item being added to the NowPlaying folder on media players that support this command.
	 * Never request this command with the scope parameter 
	 * 		<p><blockquote>AVRCP_SCOPE_ID_MEDIA_PLAYER	(byte) 0x00.</blockquote>
	 * We could request this command with the UID of a Folder Item if the folder is playable. 
	 * The result of this is that the contents of that folder are added to the NowPlaying folder, not the folder itself.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpNowPlayingAdded retAvrcpNowPlayingAdded} and
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param scopeId : The scope in which the UID of the media element item or folder item, if supported, is valid. Possible values are
	 *		<p><blockquote>AVRCP_SCOPE_ID_VFS			(byte) 0x01
	 *		<br>AVRCP_SCOPE_ID_SEARCH		(byte) 0x02
	 *		<br>AVRCP_SCOPE_ID_NOW_PLAYING	(byte) 0x03</blockquote>
	 * @param uidCounter : the value of uid counter we have.
	 * @param uid : The UID of the media element item or folder item, if supported, to be played.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpAddToNowPlaying(java.lang.String address, byte scopeId, int uidCounter, long uid) throws android.os.RemoteException;
/** 
	 * Request if remote connected A2DP/AVRCP device with given Bluetooth hardware address has browsing channel established. 
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @return true to indicate the remote device has browsing channel.
	 */
public boolean isAvrcpBrowsingChannelEstablished(java.lang.String address) throws android.os.RemoteException;
/** 
	 * By the AVRCP v1.4 specification, this command is used to set an absolute volume to be used by the rendering device. 
	 * This is in addition to the relative volume commands. 
	 * It is expected that the audio sink will perform as the TG role for this command.
	 * As this command specifies a percentage rather than an absolute dB level, the CT should exercise caution when sending this command.
	 * It should be noted that this command is intended to alter the rendering volume on the audio sink. 
	 * It is not intended to alter the volume within the audio stream. The volume level which has actually been set on the TG is returned in the response. 
	 * This is to enable the CT to deal with whatever granularity of volume control the TG provides.
	 * The setting volume is represented in one octet. The top bit (bit 7) is reserved for future definition. 
	 * The volume is specified as a percentage of the maximum. The value 0x0 corresponds to 0%. The value 0x7F corresponds to 100%.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IA2dpCallback#retAvrcpAbsoluteVolumeSet retAvrcpAbsoluteVolumeSet} and 
	 * {@link IA2dpCallback#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @warning : RESERVED !!! Please DO NOT USE at this release version !!!
	 * @param address : valid Bluetooth MAC address of connected device.
	 * @param volume : the setting volume value of octet from 0x00 to 0x7F.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqAvrcpSetAbsoluteVolume(java.lang.String address, byte volume) throws android.os.RemoteException;
/**
	 * Request to re-start the A2DP native socket thread for playing out stream data.
	 * <br>This request MUST be called if application has called {@link INforeService#setA2dpNativeStop setA2dpNativeStop}
	 * <br>This request is only useful for some specific situation, and application might not need to use this in general case.  
	 */
public void setA2dpNativeStart() throws android.os.RemoteException;
/**
	 * Request to stop the A2DP native socket thread immediately.
	 * <br>{@link INforeService#setA2dpNativeStart setA2dpNativeStart} MUST be called after anything you want to do if this API was called.
	 * <br>Otherwise, there would be no audio playing out anymore !!! 
	 * <br>This request is only useful for some specific situation, and application might not need to use this in general case.  
	 */
public void setA2dpNativeStop() throws android.os.RemoteException;
/**
	 * Return false if A2DP native socket thread was stopped by {@link INforeService#setA2dpNativeStop setA2dpNativeStop}.
	 *
	 * @return false if A2DP native socket thread was stopped.
	 */
public boolean isA2dpNativeRunning() throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * PAN Profile
	 *//** 
	 * Register callback functions for PAN profile.
	 * Call this function to register callback functions for PAN profile.
	 * Allow nFore service to call back to its registered clients, which is often UI.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean registerPanCallback(nfore.android.bt.servicemanager.IPanCallback cb) throws android.os.RemoteException;
/** 
	 * Remove callback functions for PAN profile.
     * Call this function to remove previously registered callback interface for PAN profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean unregisterPanCallback(nfore.android.bt.servicemanager.IPanCallback cb) throws android.os.RemoteException;
/** 
	 * Request to connect PAN to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IPanCallback#onPanStateChanged onPanStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that PAN will be connected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqPanConnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to disconnect the connected PAN connections to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link IPanCallback#onPanStateChanged onPanStateChanged}
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both PAN will be disconnected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqPanDisconnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request for the hardware Bluetooth address of connected remote PAN device.
	 * For example, "00:11:22:AA:BB:CC".
	 *
	 * @return Bluetooth hardware address as string if there is a connected PAN device, or 
	 * 00:00:00:00:00:00 would be returned.
	 */
public java.lang.String getPanConnectedDeviceAddress() throws android.os.RemoteException;
public int getPanCurrentState() throws android.os.RemoteException;
/* ======================================================================================================================================================== 
	 * HID Profile
	 *//** 
	 * Register callback functions for HID profile.
	 * Call this function to register callback functions for HID profile.
	 * Allow nFore service to call back to its registered clients, which is often UI.
	 *
	 * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean registerHidCallback(nfore.android.bt.servicemanager.IHidCallback cb) throws android.os.RemoteException;
/** 
	 * Remove callback functions for HID profile.
     * Call this function to remove previously registered callback interface for HID profile.
     * 
     * @param cb : callback interface instance.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
     */
public boolean unregisterHidCallback(nfore.android.bt.servicemanager.IHidCallback cb) throws android.os.RemoteException;
/** 
	 * Request to connect HID to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is a blocking call and clients should register and implement callback functions 
	 * {@link IHidCallback#onHidStateChanged onHidStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that HID will be connected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqHidConnect(java.lang.String address) throws android.os.RemoteException;
/** 
	 * Request to disconnect the connected HID connections to the given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * This is an blocking call and clients should register and implement callback functions 
	 * {@link IHidCallback#onHidStateChanged onHidStateChanged}
	 * to be notified of subsequent profile state changes.
	 * There is no guarantee that both HID will be disconnected and the sequence of state changed callback of profile! 
	 * This depends on the behavior of connected device.
	 *
	 * @param address : valid Bluetooth MAC address.
	 * @return true to indicate the operation has been done without error, or false on immediate error.
	 */
public boolean reqHidDisconnect(java.lang.String address) throws android.os.RemoteException;
public int sendHidMouseCommand(int button, int offset_x, int offset_y, int wheel) throws android.os.RemoteException;
public int sendHidVirtualKeyCommand(int key_1, int key_2) throws android.os.RemoteException;
public int getHidCurrentState() throws android.os.RemoteException;
}
