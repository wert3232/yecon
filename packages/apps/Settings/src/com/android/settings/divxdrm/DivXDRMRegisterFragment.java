
package com.android.settings.divxdrm;

import android.drm.DrmManagerClient;
import android.drm.DrmInfo;
import android.drm.DrmInfoRequest;

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


public class DivXDRMRegisterFragment extends Fragment implements Button.OnClickListener{
    private static final String TAG ="DivXDRMRegisterFragment";

    Context mcontext;
    private TextView mRegisterTip = null;
    private Button   mOK = null;
    private View     mView = null;
    public  DrmManagerClient mDrmManagerClient = null;
    private boolean mNeedReleaseDrmManagerClient = false;

    public DivXDRMRegisterFragment(DrmManagerClient Client) {
        if (mDrmManagerClient == null)
        {
            Log.i(TAG, " Constructor -- set mDrmManagerClient to be Client\r\n");
            mDrmManagerClient = Client;
            mNeedReleaseDrmManagerClient = false;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, " onCreate\r\n");
        super.onCreate(savedInstanceState);
        mcontext = getActivity();
        if (mDrmManagerClient == null) {
            mDrmManagerClient = new DrmManagerClient(mcontext);
            Log.i(TAG, " onCreate -- mDrmManagerClient\r\n");
            mNeedReleaseDrmManagerClient = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Log.i(TAG, " onCreateView\r\n");

        mView = inflater.inflate(R.layout.drm_divx_register, null);

        if (savedInstanceState == null) {

            Log.i(TAG, " onCreateView -- savedInstanceState == null\r\n");
            mRegisterTip = (TextView) mView.findViewById(R.id.tDrmDivxRegTip);

            String mRegTipPrefixStr = getResources().getString(R.string.drm_divx_register_tip);
            String mRegCodeTipStr = getResources().getString(R.string.drm_divx_register_code);
            String mRegWebTipStr = getResources().getString(R.string.drm_divx_register_web);

            if (mDrmManagerClient != null) {
                DrmInfoRequest drmInfoReq = new DrmInfoRequest(DrmInfoRequest.TYPE_REGISTRATION_INFO, "video/avi");
                DrmInfo mdrmInfo = mDrmManagerClient.acquireDrmInfo(drmInfoReq);
                if ((mdrmInfo != null) && (mdrmInfo.getData() != null)) {
                    byte[] drmRegcode = mdrmInfo.getData();
                    String mRegCodeStr = new String(drmRegcode);
                    mRegisterTip.setText(mRegTipPrefixStr + mRegCodeTipStr + mRegCodeStr + "\r\n" + mRegWebTipStr);
                }
                else {
                    mRegisterTip.setText(mRegTipPrefixStr + mRegCodeTipStr + " \r\n" + mRegWebTipStr);
                }
            }
            else {
                mRegisterTip.setText(mRegTipPrefixStr + mRegCodeTipStr + " \r\n" + mRegWebTipStr);
            }

            mOK = (Button) mView.findViewById(R.id.bOK);

            mOK.setOnClickListener(this);
        }
        else
        {
            Log.i(TAG, " onCreateView -- savedInstanceState != null\r\n");
        }

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy -- release mDrmManagerClient!!!");
        if ((mDrmManagerClient != null) && mNeedReleaseDrmManagerClient)
        {
            mDrmManagerClient.release();
        }
        Log.i(TAG, "onDestroy exit!!!");
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bOK:
            Log.i(TAG, " Click OK Button!!!");
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
            break;
        default:
            break;
        }
    }
}

