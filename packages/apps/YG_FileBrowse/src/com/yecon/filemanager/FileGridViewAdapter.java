package com.yecon.filemanager;

import android.content.Context;

import java.util.List;

import com.yecon.filemanager.R;

/**
 * Created by chenchu on 14-12-17.
 */
public class FileGridViewAdapter extends AbsFileItemAdapter<GridItemHolder> {
    public FileGridViewAdapter(Context context, FileItemGestureDetector.OnListItemTouchListener listener,List<FileInfo> data) {
        super(context,R.layout.fragment_list_grid_item,listener,data);
    }

    public void setView(GridItemHolder holder,FileInfo info){
        holder.mFileName.setText(info.fileName);
        holder.mFileLogo.setImageResource(info.fileRes);
    }
}
