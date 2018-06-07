package com.yecon.filemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * Created by chenchu on 14-12-24.
 */
public class FileGridView extends GridView {

    private static final String Tag ="filegridview";
    public FileGridView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setEmptyViewHelper(int resId,OnLongClickListener listener) {
        if (getEmptyView() == null) {
            View emptyView = null;
            ViewGroup vg =  (ViewGroup) getParent();
            if (vg != null) {
                emptyView = vg.findViewById(resId);
                Log.d(Tag,"vg is not null");
            }
            if (emptyView != null) {
                setEmptyView(emptyView);
                if (listener != null) {
                    emptyView.setOnLongClickListener(listener);
                }
                Log.d(Tag, "empty view");
            }
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        ListAdapter adapter = getAdapter();
        if(changedView == this && visibility == View.VISIBLE && adapter != null) {
            ((FileGridViewAdapter)adapter).notifyDataSetChanged();
            Log.d(Tag,"on visible");
        }
    }
}
