package com.yecon.filemanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by chenchu on 15-3-18.
 */
public class FileNeatMenuFragment extends Fragment implements TextView.OnEditorActionListener {
    private static final String Tag = "file neatmenufragement";
    public ImageButton mMoreButton;
    //public SearchView mSearchView;
    public EditText mSearchEditText;
    public boolean isInFilterMode = false;

    public static interface IMenuFragmentOp {
        public void onSearch(String query);
    }

    /*
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mSearchEditText != null) {
                InputMethodManager manager = (InputMethodManager) mSearchEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                Log.d(Tag,"timer....");
                //TODO
                if(!manager.isAcceptingText()) {
                    Log.d(Tag,"is not active");
                    mSearchEditText.getText().clear();
                    mMoreButton.requestFocusFromTouch();
                }
            }
        }
    };

    private Timer mTimer = new Timer();
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_menu_neat,container,false);
        if (v != null) {
            mMoreButton = (ImageButton) v.findViewById(R.id.action_more);
            mSearchEditText = (EditText) v.findViewById(R.id.action_search);
            mSearchEditText.setOnEditorActionListener(this);
            mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                      Log.d(Tag,"on focus gain");
                      //mTimer.schedule(mTimerTask,1000,2000);
                    } else {
                      Log.d(Tag,"on focus cancel");
                      //mTimer.cancel();
                    }
                }
            });

            //mSearchEditText.setOnClickListener(this);
            /*
            mSearchView = (SearchView) v.findViewById(R.id.action_search);
            SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchableInfo info = manager.getSearchableInfo(new ComponentName(getActivity(),FileSearchActivity.class));

            Log.d(Tag,info.toString());
            mSearchView.setSearchableInfo(info);
            mSearchView.setSubmitButtonEnabled(true);
            */
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setInfoEnabled(boolean b) {
        //mButtonFileInfo.setEnabled(b);
        //mButtonFilter.setEnabled(!b);
        isInFilterMode = b;
        //mButtonNewFolder.setEnabled(!isInFilterMode);
        Log.d(Tag, (b ? "is" : "is not") + " in filter mode");
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

       if (v.getId() == R.id.action_search) {
           Activity activity = getActivity();
           InputMethodManager manager =(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
           manager.toggleSoftInput(0,0);
           if (activity instanceof IMenuFragmentOp && actionId == EditorInfo.IME_ACTION_SEARCH) {
               IMenuFragmentOp op =  (IMenuFragmentOp) activity;
               op.onSearch(v.getText().toString());
               Log.d(Tag,"on editor action");

               return true;
           }
       }
       return false;
    }

}
