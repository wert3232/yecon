package com.yecon.filemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by chenchu on 14-12-16.
 */

public class ListItemHolder extends ListViewItemHolder {

    private static final String Tag = "list item holder";

    public  TextView mFileName;
    public TextView mFileSize;
    public TextView mFileModifiedTime;
    public TextView mFileKind;
    public ImageView mFileIcon;

    public ListItemHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        onSetup();
    }

    private void onSetup() {
        mFileName =(TextView) findViewById(R.id.fragment_list_item_file_name);
        mFileSize = (TextView)findViewById(R.id.fragment_list_item_file_size);
        mFileModifiedTime = (TextView)findViewById(R.id.fragment_list_item_file_modifiedtime);
        mFileKind = (TextView)findViewById(R.id.fragment_list_item_file_kind);
        mFileIcon = (ImageView)findViewById(R.id.fragment_list_item_file_icon);
    }
}