package com.yecon.mediaservice;

public abstract interface IMultiMediaPlayer
{
  public abstract void updatePlayState();

  public abstract void updatePlayProgress(int paramInt1, int paramInt2);

  public abstract void updateMediaInfo(int paramInt1, int paramInt2);
}

