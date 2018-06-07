package com.yecon.mediaservice;

import android.database.Cursor;

abstract interface IMediaListQueryHandler
{
  public abstract void onQueryComplete(int paramInt, Object paramObject, Cursor paramCursor);
}

