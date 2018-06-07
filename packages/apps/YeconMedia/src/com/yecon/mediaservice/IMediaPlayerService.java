package com.yecon.mediaservice;

import android.os.RemoteException;
import android.view.SurfaceHolder;
import java.util.List;

public abstract interface IMediaPlayerService
{
  public abstract boolean requestReScaning(String paramString)
    throws RemoteException;

  public abstract void RegisterSource(int paramInt);

  public abstract void UnregisterSource();

  public abstract void registerUserInterface(IMediaPlayerActivity paramIMediaPlayerActivity)
    throws RemoteException;

  public abstract void unregisterUserInterface(IMediaPlayerActivity paramIMediaPlayerActivity)
    throws RemoteException;

  public abstract boolean requestAttachStorage(String paramString)
    throws RemoteException;

  public abstract void requestRandom()
    throws RemoteException;

  public abstract void requestRepeat()
    throws RemoteException;

  public abstract void requestPlayList()
    throws RemoteException;

  public abstract void requestAllFileList()
    throws RemoteException;

  public abstract void requestFavoriteList()
    throws RemoteException;

  public abstract void requestFileListByDir(int paramInt)
    throws RemoteException;

  public abstract void requestFileListByAlbum(int paramInt)
    throws RemoteException;

  public abstract void requestFileListByArtist(int paramInt)
    throws RemoteException;

  public abstract void requestSaveLastMemory()
    throws RemoteException;

  public abstract void requestPlay(int paramInt1, int paramInt2)
    throws RemoteException;

  public abstract void requestPause()
    throws RemoteException;

  public abstract void requestStart()
    throws RemoteException;

  public abstract void requestStop()
    throws RemoteException;

  public abstract void requestSetFrontDisplay(SurfaceHolder paramSurfaceHolder)
    throws RemoteException;

  public abstract void requestSetRearDisplay(SurfaceHolder paramSurfaceHolder)
    throws RemoteException;

  public abstract void requestSeek(int paramInt)
    throws RemoteException;

  public abstract void requestNext()
    throws RemoteException;

  public abstract void requestPrev()
    throws RemoteException;

  public abstract void requestActiveSource()
    throws RemoteException;

  public abstract void requestDeactiveSource()
    throws RemoteException;

  public abstract boolean requestFavoriteAdd(int paramInt1, int paramInt2)
    throws RemoteException;

  public abstract boolean requestFavoriteErase(int paramInt1, int paramInt2)
    throws RemoteException;

  public abstract int getPlayListType()
    throws RemoteException;

  public abstract int getPlayStatus()
    throws RemoteException;

  public abstract int getRandomStatus()
    throws RemoteException;

  public abstract int getRepeatStatus()
    throws RemoteException;

  public abstract int getMediaType()
    throws RemoteException;

  public abstract List<MediaStorage> getStorageList()
    throws RemoteException;

  public abstract MediaStorage getPlayingStorage()
    throws RemoteException;

  public abstract List<MediaObject> getPlayList()
    throws RemoteException;

  public abstract List<MediaObject> getAllFileList()
    throws RemoteException;

  public abstract List<MediaObject> getDirList()
    throws RemoteException;

  public abstract List<MediaObject> getAlbumList()
    throws RemoteException;

  public abstract List<MediaObject> getArtistList()
    throws RemoteException;

  public abstract List<MediaObject> getCurDirFileList()
    throws RemoteException;

  public abstract List<MediaObject> getCurArtistFileList()
    throws RemoteException;

  public abstract List<MediaObject> getCurAlbumFileList()
    throws RemoteException;

  public abstract List<MediaObject> getFavoriteFileList()
    throws RemoteException;

  public abstract MediaTrackInfo getPlayingFileInfo()
    throws RemoteException;

  public abstract long getFilePosInList()
    throws RemoteException;

  public abstract long getDirPosInList()
    throws RemoteException;

  public abstract long getArtistPosInList()
    throws RemoteException;

  public abstract long getAlbumPosInList()
    throws RemoteException;

  public abstract byte[] getApicData(int paramInt)
    throws RemoteException;
  public abstract List<MediaObject> getPlayingDirList();
  
  public abstract int getPlayingDirPos();
}

