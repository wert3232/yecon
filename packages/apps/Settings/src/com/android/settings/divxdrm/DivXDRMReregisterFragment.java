
package com.android.settings.divxdrm;

import android.drm.DrmManagerClient;
import android.drm.DrmInfo;
import android.drm.DrmInfoRequest;

import android.app.Activity;
import android.app.Fragment;
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

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.util.Log;


public class DivXDRMReregisterFragment extends Fragment implements Button.OnClickListener{
    private static final String TAG ="DivXDRMReregisterFragment";

    private Button   mOK = null;
    private Button   mCancel = null;
    private View     mView = null;

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, " onCreate\r\n");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i(TAG, " onCreateView\r\n");
        mView = inflater.inflate(R.layout.drm_divx_reregister, null);
        mOK = (Button) mView.findViewById(R.id.bOK);

        mOK.setOnClickListener(this);

        mCancel = (Button) mView.findViewById(R.id.bCancel);

        mCancel.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy!!!");
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bOK:
            {
                Log.i(TAG, "Click OK Button!!!\r\n");
                DivXDRMSettingFragment ParentFragment = (DivXDRMSettingFragment)getParentFragment();
                if (ParentFragment == null)
                {
                    Log.e(TAG, " divxdrm device -- no parent fragment\r\n");
                    getActivity().finish();
                }
                else
                {
                    ParentFragment.DeRegisterDivXDRM();
                }
            }
            break;
        case R.id.bCancel:
            {
                Log.i(TAG, "Click CANCEL Button!!!\r\n");
                DivXDRMSettingFragment ParentFragment = (DivXDRMSettingFragment)getParentFragment();
                if (ParentFragment == null)
                {
                    Log.e(TAG, " divxdrm device -- no parent fragment");
                    getActivity().finish();
                }
                else
                {
                    Log.i(TAG, " ParentFragment.BackToDrmSettingMainFragment()!!!");
                    ParentFragment.BackToDrmSettingMainFragment();
                }
            }
            break;
        default:
            break;
        }
    }
}



