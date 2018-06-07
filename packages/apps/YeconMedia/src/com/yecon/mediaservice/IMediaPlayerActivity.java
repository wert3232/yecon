package com.yecon.mediaservice;

import android.os.RemoteException;

public abstract interface IMediaPlayerActivity
{
  public abstract void updateListData(int paramInt)
    throws RemoteException;

  public abstract void updateServiceStatus(int paramInt, String paramString)
    throws RemoteException;

  public abstract void updatePlayStatus(int paramInt)
    throws RemoteException;

  public abstract void updateTimeProcess(int paramInt1, int paramInt2)
    throws RemoteException;

  public abstract void updateMediaInfo(int paramInt1, int paramInt2)
    throws RemoteException;

  public abstract void updateRepeatStatus(int paramInt)
    throws RemoteException;

  public abstract void updateRandomStatus(int paramInt)
    throws RemoteException;
}

