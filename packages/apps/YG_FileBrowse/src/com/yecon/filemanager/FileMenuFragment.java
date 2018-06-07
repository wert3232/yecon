package com.yecon.filemanager;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by chenchu on 15-1-13.
 */
public class FileMenuFragment extends Fragment {
    private static final String Tag = "FileMenuFragment";
    public ImageButton mButtonGridView;
    public ImageButton mButtonListView;
    public ImageButton mButtonFileInfo;
    public ImageButton mButtonFilter;
    public ImageButton mButtonNewFolder;
    public boolean isInFilterMode = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu,container,false);
        mButtonGridView = (ImageButton) v.findViewById(R.id.action_gridview);
        mButtonListView = (ImageButton) v.findViewById(R.id.action_listview);
        mButtonFileInfo = (ImageButton) v.findViewById(R.id.action_info);
        mButtonFilter = (ImageButton) v.findViewById(R.id.action_filter);
        mButtonNewFolder = (ImageButton) v.findViewById(R.id.action_newfolder);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        int mode  = ((MainActivity)getActivity()).getCurrFragment().getFragmentCurrViewMode();
        Log.d(Tag, "curr mode is" + mode);
        if (mode == FileListFragment.LIST_MODE) {
            toggleFileView(R.id.action_listview);
        } else if (mode == FileListFragment.GRID_MODE) {
            toggleFileView(R.id.action_gridview);
        }
        */
        setInfoEnabled(isInFilterMode);
    }

    public void setInfoEnabled(boolean b) {
        mButtonFileInfo.setEnabled(b);
        mButtonFilter.setEnabled(!b);
        isInFilterMode = b;
        mButtonNewFolder.setEnabled(!isInFilterMode);
        Log.d(Tag,(b?"is":"is not")+" in filter mode");
    }

    public void toggleFileView(int selectedViewId) {
        if (selectedViewId == R.id.action_gridview) {
            mButtonGridView.setImageResource(R.drawable.gridview_f);
            mButtonListView.setImageResource(R.drawable.listview_n);
            Log.d(Tag,"on grid view");
            return;
        }

        if (selectedViewId == R.id.action_listview) {
            Log.d(Tag,"on list view");
            mButtonGridView.setImageResource(R.drawable.gridview_n);
            mButtonListView.setImageResource(R.drawable.listview_f);
            return;
        }
    }
}
