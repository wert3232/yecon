/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\yecon\\yecon_ac8317\\yecon\\packages\\apps\\SourceManager\\src\\com\\yecon\\sourcemanager\\ISourceManagerService.aidl
 */
package com.yecon.sourcemanager;
public interface ISourceManagerService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yecon.sourcemanager.ISourceManagerService
{
private static final java.lang.String DESCRIPTOR = "com.yecon.sourcemanager.ISourceManagerService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yecon.sourcemanager.ISourceManagerService interface,
 * generating a proxy if needed.
 */
public static com.yecon.sourcemanager.ISourceManagerService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yecon.sourcemanager.ISourceManagerService))) {
return ((com.yecon.sourcemanager.ISourceManagerService)iin);
}
return new com.yecon.sourcemanager.ISourceManagerService.Stub.Proxy(obj);
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
case TRANSACTION_request:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.request(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_release:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.release(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_hotplug:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _arg2;
_arg2 = (0!=data.readInt());
boolean _arg3;
_arg3 = (0!=data.readInt());
this.hotplug(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_releaseMediaKey:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.releaseMediaKey(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updatePlayingPath:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.updatePlayingPath(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yecon.sourcemanager.ISourceManagerService
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
@Override public int request(int srcNo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(srcNo);
mRemote.transact(Stub.TRANSACTION_request, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void release(int srcNo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(srcNo);
mRemote.transact(Stub.TRANSACTION_release, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void hotplug(int srcNo, java.lang.String devPath, boolean insert, boolean appExit) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(srcNo);
_data.writeString(devPath);
_data.writeInt(((insert)?(1):(0)));
_data.writeInt(((appExit)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_hotplug, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void releaseMediaKey(int srcNo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(srcNo);
mRemote.transact(Stub.TRANSACTION_releaseMediaKey, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updatePlayingPath(int srcNo, java.lang.String path) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(srcNo);
_data.writeString(path);
mRemote.transact(Stub.TRANSACTION_updatePlayingPath, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_request = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_release = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_hotplug = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_releaseMediaKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_updatePlayingPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public int request(int srcNo) throws android.os.RemoteException;
public void release(int srcNo) throws android.os.RemoteException;
public void hotplug(int srcNo, java.lang.String devPath, boolean insert, boolean appExit) throws android.os.RemoteException;
public void releaseMediaKey(int srcNo) throws android.os.RemoteException;
public void updatePlayingPath(int srcNo, java.lang.String path) throws android.os.RemoteException;
}
