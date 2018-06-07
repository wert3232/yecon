package com.android.settings.divxdrm;

import android.drm.DrmManagerClient;
import android.drm.DrmInfo;
import android.drm.DrmInfoRequest;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.android.settings.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import android.util.Log;

public class DivXDRMSettingFragment extends Fragment{
    Context mcontext;
    private String TAG = "DivXDRMSettingFragment";
    private String DRMSettingMainFragment_TAG = "DivXDRMMainFragment";
    private String DRMRegisterFragment_TAG = "DivXDRMRegisterFragment";
    private String DRMReregisterFragment_TAG = "DivXDRMReregisterFragment";
    private String DRMDeregisterFragment_TAG = "DivXDRMDeRegisterFragment";
    private View mView = null;
    public  DrmManagerClient mDrmManagerClient = null;
    private DivXDRMMainFragment mDivXDrmMainFragment = null;
    private DivXDRMRegisterFragment mRegisterFragment = null;
    private DivXDRMReregisterFragment mReregisterFragment = null;
    private DivXDRMDeregisterFragment mDeregisterFragment = null;

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, " onCreate\r\n");
        super.onCreate(savedInstanceState);
        mcontext = getActivity();
        Log.i(TAG, " onCreate -- mDrmManagerClient\r\n");
        if (mDrmManagerClient == null)
        {
            mDrmManagerClient = new DrmManagerClient(mcontext);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i(TAG, " onCreateView\r\n");
        mView = inflater.inflate(R.layout.drm_divx_settings, null);

        FragmentManager fm = getChildFragmentManager();
        if (savedInstanceState == null) {
            FragmentTransaction ft = fm.beginTransaction();
            if (mDivXDrmMainFragment == null)
            {
                mDivXDrmMainFragment = new DivXDRMMainFragment(mDrmManagerClient);
            }
	    if(mDivXDrmMainFragment.isAdded()){
		Log.d(TAG, "----[kuan]DivXDRMSettingFragment:onCreateView, mDivXDrmMainFragment has added to back stack.");
		return mView;
	    }
            ft.add(R.id.divxdrm_framelayout, mDivXDrmMainFragment,
                DRMSettingMainFragment_TAG);
            ft.commit();
        }

        return mView;
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView!!!");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mDrmManagerClient != null)
        {
            Log.i(TAG, "onDestroy -- release mDrmManagerClient!!!");
            mDrmManagerClient.release();
        }
        Log.i(TAG, "onDestroy exit!!!");
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onDetach()  {
        Log.i(TAG, "onDetach!!!");
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        }
        catch(NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void RegisterDivXDRM() {
        Log.i(TAG, " RegisterDivXDRM!!!");
        FragmentManager fm = getChildFragmentManager();
        mRegisterFragment = (DivXDRMRegisterFragment)fm.findFragmentByTag(DRMRegisterFragment_TAG);
        if (mRegisterFragment == null)
        {
            Log.i(TAG, " -- mRegisterFragment == null, new DivXDRMRegisterFragment!!!");
            mRegisterFragment = new DivXDRMRegisterFragment(mDrmManagerClient);
        }

        if (mRegisterFragment != null)
        {
            Log.i(TAG, " -- replace R.id.divxdrm_framelayout with mRegisterFragment!!!");
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.divxdrm_framelayout, mRegisterFragment, DRMRegisterFragment_TAG);
            //ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void ReRegisterDivXDRM() {
        Log.i(TAG, " ReRegisterDivXDRM!!!");
        FragmentManager fm = getChildFragmentManager();
        mReregisterFragment = (DivXDRMReregisterFragment)fm.findFragmentByTag(DRMReregisterFragment_TAG);
        if (mReregisterFragment == null)
        {
            Log.i(TAG, " -- mRegisterFragment == null, new DivXDRMReregisterFragment!!!");
            mReregisterFragment = new DivXDRMReregisterFragment();
        }

        if (mReregisterFragment != null)
        {
            Log.i(TAG, " -- replace R.id.divxdrm_framelayout with mReregisterFragment!!!");
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.divxdrm_framelayout, mReregisterFragment, DRMReregisterFragment_TAG);
            //ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void DeRegisterDivXDRM() {
        Log.i(TAG, " DeRegisterDivXDRM!!!");
        FragmentManager fm = getChildFragmentManager();
        mDeregisterFragment = (DivXDRMDeregisterFragment)fm.findFragmentByTag(DRMDeregisterFragment_TAG);
        if (mDeregisterFragment == null)
        {
            Log.i(TAG, " -- mRegisterFragment == null, new DivXDRMDeregisterFragment!!!");
            mDeregisterFragment = new DivXDRMDeregisterFragment(mDrmManagerClient);
        }

        if (mDeregisterFragment != null)
        {
            Log.i(TAG, " -- replace R.id.divxdrm_framelayout with mDeregisterFragment!!!");
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.divxdrm_framelayout, mDeregisterFragment, DRMDeregisterFragment_TAG);
            //ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void BackToDrmSettingMainFragment() {
        Log.i(TAG, " RegisterDivXDRM!!!");
        FragmentManager fm = getChildFragmentManager();
        mDivXDrmMainFragment = (DivXDRMMainFragment)fm.findFragmentByTag(DRMSettingMainFragment_TAG);
        if (mDivXDrmMainFragment == null)
        {
            Log.i(TAG, " -- mDivXDrmMainFragment == null, new DivXDRMMainFragment!!!");
            mDivXDrmMainFragment = new DivXDRMMainFragment(mDrmManagerClient);
        }

        if (mDivXDrmMainFragment != null)
        {
            Log.i(TAG, " -- replace R.id.divxdrm_framelayout with mDivXDrmMainFragment!!!");
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.divxdrm_framelayout, mDivXDrmMainFragment, DRMSettingMainFragment_TAG);
            //ft.addToBackStack(null);
            ft.commit();
        }
    }

}

