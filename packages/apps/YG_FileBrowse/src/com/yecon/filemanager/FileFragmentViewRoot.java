package com.yecon.filemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

/**
 * Created by chenchu on 14-12-24.
 */
public class FileFragmentViewRoot extends FrameLayout {
    public static final String Tag ="file fragment view root";

    private SparseArray<View> mViews = new SparseArray<View>();

    private View mVisibleView;

    public FileFragmentViewRoot(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public void registerViewStubId(int[] Id){
        for (int id:Id){
            if (mViews.get(id) != null){
                throw new IllegalArgumentException();
            }
            mViews.put(id,null);
        }
    }

    public View inflateViewStub(int id){
        Log.d(Tag, "inflate view stub");
        View v = mViews.get(id);
        if (v !=null){
            setViewVisible(v);
            return v;
        }
        ViewStub stub = (ViewStub) findViewById(id);
        Log.d(Tag,stub.toString());
        if(stub == null){
            throw new IllegalArgumentException();
        }
        v = stub.inflate();
        mViews.put(id,v);
        setViewVisible(v);
        return v;
    }

    public void setViewVisible(int id) {
        View v = mViews.get(id);
        if (v == null){
            throw new IllegalArgumentException();
        }
        setViewVisible(v);
    }

    private void setViewVisible(View v){
        if (v != null){
            Log.d(Tag,v.toString());
            if (mVisibleView != v){
                if(mVisibleView != null){
                    mVisibleView.setVisibility(View.GONE);
                }
                    v.setVisibility(View.VISIBLE);
                }
            else {
                Log.d(Tag,"refresh view");
                v.setVisibility(View.INVISIBLE);
                v.setVisibility(View.VISIBLE);
            }
                mVisibleView = v;
        }
    }
}
