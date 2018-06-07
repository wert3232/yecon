package com.yecon.filemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by chenchu on 14-12-17.
 */
public class GridItemHolder extends ListViewItemHolder{
    private static final String Tag = "grid item holder";

    public TextView mFileName;
    public ImageView mFileLogo;

    public GridItemHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        onSetup();
    }

    private void onSetup() {
        mFileName =(TextView) findViewById(R.id.fragment_grid_item_file_name);
        mFileLogo =(ImageView) findViewById(R.id.fragment_grid_item_file_logo);
    }
}
