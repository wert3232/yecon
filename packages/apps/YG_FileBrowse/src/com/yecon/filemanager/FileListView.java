package com.yecon.filemanager;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by chenchu on 14-12-15.
 */
public class FileListView extends FrameLayout {
    public static final String Tag = "file list view";
    public ListView mListView;

    public FileListView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mListView = (ListView)findViewById(R.id.fragment_list_list_view);
    }

    public void setEmptyViewHelper(int resId, OnLongClickListener listener) {
        if (mListView.getEmptyView() == null) {
            View emptyView = null;
            ViewGroup vg = (ViewGroup) mListView.getParent().getParent();
            if (vg != null) {
                emptyView = vg.findViewById(resId);
                Log.d(Tag,"vg is not null"+vg.toString());
            }
            if (emptyView != null) {
                Log.d(Tag,"empty view");
                mListView.setEmptyView(emptyView);
                if (listener != null) {
                    emptyView.setOnLongClickListener(listener);
                }
            }
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        FileListViewAdapter adapter = (FileListViewAdapter) mListView.getAdapter();
        if(changedView == this && visibility == View.VISIBLE && adapter != null) {
            Log.d(Tag,"on visible");
            adapter.notifyDataSetChanged();
        }
    }

    public void setAdapter(ListAdapter adapter){
        if(mListView !=null){
            mListView.setAdapter(adapter);
        }
    }

    public ListAdapter getAdapter(){
        return mListView.getAdapter();
    }

}
