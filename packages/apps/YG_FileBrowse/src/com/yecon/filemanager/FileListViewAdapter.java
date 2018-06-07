package com.yecon.filemanager;

import java.util.List;

import android.content.Context;

/**
 * Created by chenchu on 14-12-16.
 */

class FileListViewAdapter extends AbsFileItemAdapter<ListItemHolder> {

    public static final String Tag = "file list item adapter";

    private FileTypeLoader mTypeLoader;

    public FileListViewAdapter(Context context, FileItemGestureDetector.OnListItemTouchListener listener,List<FileInfo> data) {
        super(context,R.layout.fragment_list_list_item_neat,listener,data);
        mTypeLoader = new FileTypeLoader(context);
    }


    //TODO: add file kind
    public void setView(ListItemHolder holder,FileInfo info){
        holder.mFileName.setText(info.fileName);
        holder.mFileModifiedTime.setText(info.formattedTime);
        holder.mFileSize.setText(info.isDir?"----":info.formattedSize);
        holder.mFileKind.setText(mTypeLoader.getType(info.filePath));
        holder.mFileIcon.setImageResource(info.fileRes);
    }

}